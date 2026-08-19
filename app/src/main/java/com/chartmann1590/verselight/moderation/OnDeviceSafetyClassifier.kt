package com.chartmann1590.verselight.moderation

import android.content.Context
import com.chartmann1590.verselight.model.ModerationResult
import com.google.mlkit.genai.common.FeatureStatus
import com.google.mlkit.genai.prompt.Generation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.Normalizer
import kotlin.math.exp

/**
 * A compact, explainable linear text classifier. All features and inference stay on-device.
 * The weighted vocabulary is paired with phrase and obfuscation features so it behaves like
 * the small bag-of-words safety models commonly deployed at the edge, without a network model.
 */
class OnDeviceSafetyClassifier(@Suppress("UNUSED_PARAMETER") context: Context? = null) {
    suspend fun classify(raw: String): ModerationResult = withContext(Dispatchers.Default) {
        val normalized = normalize(raw)
        if (normalized.isBlank()) return@withContext ModerationResult(false, setOf("empty"), 1f, "Write something before posting.")
        if (normalized.length > 500) return@withContext ModerationResult(false, setOf("length"), 1f, "Keep comments under 500 characters.")

        val tokens = normalized.split(Regex("[^a-z0-9']+")).filter { it.isNotBlank() }
        val joined = tokens.joinToString(" ")
        val categoryScores = linkedMapOf(
            "threat" to score(tokens, joined, THREAT_WEIGHTS, -3.1),
            "hate" to score(tokens, joined, HATE_WEIGHTS, -3.4),
            "harassment" to score(tokens, joined, HARASSMENT_WEIGHTS, -3.0),
            "sexual abuse" to score(tokens, joined, SEXUAL_WEIGHTS, -3.2),
            "strong profanity" to score(tokens, joined, PROFANITY_WEIGHTS, -3.0),
        )
        val matches = categoryScores.filterValues { it >= 0.72 }
        val max = categoryScores.maxOf { it.value }.toFloat()
        if (matches.isEmpty()) {
            classifyWithGeminiNano(raw, max)
        } else {
            val labels = matches.keys
            ModerationResult(
                allowed = false,
                categories = labels,
                confidence = matches.maxOf { it.value }.toFloat(),
                explanation = "Please rephrase this comment. It may contain ${labels.joinToString()}.",
            )
        }
    }

    private suspend fun classifyWithGeminiNano(raw: String, lexicalRisk: Float): ModerationResult {
        return runCatching {
            val model = Generation.getClient()
            if (model.checkStatus() != FeatureStatus.AVAILABLE) {
                return ModerationResult(true, confidence = (1f - lexicalRisk).coerceIn(0f, 1f), explanation = "This comment looks respectful.")
            }
            val prompt = """
                Classify the following community comment for a family-friendly Bible app.
                Reply with exactly ALLOW, or BLOCK followed by one category: threat, hate, harassment, sexual abuse, strong profanity.
                Allow sincere questions and respectful disagreement. Block targeted abuse, dehumanization, threats, sexual exploitation, or strong profanity.
                COMMENT: ${raw.take(500)}
            """.trimIndent()
            val answer = model.generateContent(prompt).candidates.firstOrNull()?.text.orEmpty().trim().uppercase()
            model.close()
            if (answer.startsWith("BLOCK")) {
                val category = answer.substringAfter("BLOCK", "unsafe content").trim(' ', ':', '-', '.').lowercase().ifBlank { "unsafe content" }
                ModerationResult(false, setOf(category), .82f, "Please rephrase this comment. It may contain $category.", usedGeminiNano = true)
            } else {
                ModerationResult(true, confidence = .82f, explanation = "This comment looks respectful.", usedGeminiNano = true)
            }
        }.getOrElse {
            ModerationResult(true, confidence = (1f - lexicalRisk).coerceIn(0f, 1f), explanation = "This comment looks respectful.")
        }
    }

    private fun score(tokens: List<String>, joined: String, weights: Map<String, Double>, bias: Double): Double {
        var logit = bias
        tokens.forEach { token -> logit += weights[token] ?: 0.0 }
        weights.filterKeys { ' ' in it }.forEach { (phrase, weight) -> if (joined.contains(phrase)) logit += weight }
        return 1.0 / (1.0 + exp(-logit))
    }

    internal fun normalize(input: String): String = Normalizer.normalize(input, Normalizer.Form.NFKC)
        .lowercase()
        .replace(Regex("[._* -]+(?=[a-z])"), " ")
        .replace('0', 'o').replace('1', 'i').replace('3', 'e').replace('4', 'a').replace('5', 's').replace('7', 't')
        .replace(Regex("(.)\\1{2,}"), "$1")
        .trim()

    companion object {
        private val THREAT_WEIGHTS = mapOf("kill" to 2.8, "murder" to 3.0, "hurt" to 1.8, "attack" to 2.0, "shoot" to 2.7, "stab" to 2.8, "die" to 1.6, "i will kill" to 3.8, "you should die" to 4.2, "find you" to 2.1)
        private val HATE_WEIGHTS = mapOf("subhuman" to 3.5, "vermin" to 2.7, "inferior" to 1.7, "race" to 0.5, "ethnicity" to 0.4, "all of them" to 1.2, "should be banned" to 1.7)
        private val HARASSMENT_WEIGHTS = mapOf("idiot" to 2.1, "moron" to 2.2, "loser" to 1.8, "worthless" to 2.6, "disgusting" to 1.4, "shut up" to 2.0, "hate you" to 2.5, "nobody wants you" to 3.1)
        private val SEXUAL_WEIGHTS = mapOf("rape" to 3.8, "molest" to 3.8, "child porn" to 5.0, "sexual abuse" to 3.0, "forced sex" to 4.0)
        private val PROFANITY_WEIGHTS = mapOf("fuck" to 3.8, "fucking" to 3.8, "motherfucker" to 4.3, "shit" to 2.7, "bitch" to 3.0, "cunt" to 4.3, "asshole" to 3.7)
    }
}
