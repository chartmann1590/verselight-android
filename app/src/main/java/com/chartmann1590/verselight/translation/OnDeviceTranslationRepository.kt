package com.chartmann1590.verselight.translation

import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.languageid.LanguageIdentification
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import com.google.firebase.perf.FirebasePerformance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout
import java.util.concurrent.ConcurrentHashMap

data class AppLanguage(val tag: String, val englishName: String, val nativeName: String)

sealed interface TranslationModelState {
    data object Ready : TranslationModelState
    data class Downloading(val language: AppLanguage) : TranslationModelState
    data class Failed(val language: AppLanguage, val message: String) : TranslationModelState
}

class OnDeviceTranslationRepository {
    private val clients = ConcurrentHashMap<String, Translator>()
    private val cache = ConcurrentHashMap<String, String>()
    private val identifier by lazy { LanguageIdentification.getClient() }
    private val _modelState = MutableStateFlow<TranslationModelState>(TranslationModelState.Ready)
    val modelState: StateFlow<TranslationModelState> = _modelState

    suspend fun prepareLanguage(targetTag: String): Result<Unit> = runCatching {
        val trace = FirebasePerformance.getInstance().newTrace("translation_model_prepare")
        trace.putAttribute("target_language", targetTag)
        trace.start()
        try {
        if (targetTag == TranslateLanguage.ENGLISH) {
            _modelState.value = TranslationModelState.Ready
            return@runCatching
        }
        val language = supportedLanguages.first { it.tag == targetTag }
        _modelState.value = TranslationModelState.Downloading(language)
        withTimeout(120_000) {
            client(TranslateLanguage.ENGLISH, targetTag)
                .downloadModelIfNeeded(DownloadConditions.Builder().build()).await()
        }
        _modelState.value = TranslationModelState.Ready
        } finally {
            trace.stop()
        }
    }.onFailure {
        val language = supportedLanguages.firstOrNull { language -> language.tag == targetTag } ?: supportedLanguages.first()
        _modelState.value = TranslationModelState.Failed(language, it.message ?: "Language model download failed")
    }

    suspend fun translateUi(text: String, targetTag: String): String = translate(text, TranslateLanguage.ENGLISH, targetTag)

    suspend fun translateDynamic(text: String, targetTag: String): String {
        if (text.isBlank()) return text
        val detected = runCatching { identifier.identifyLanguage(text).await() }.getOrNull()
        val source = detected?.takeUnless { it == "und" }?.let(TranslateLanguage::fromLanguageTag)
            ?: TranslateLanguage.ENGLISH
        return translate(text, source, targetTag)
    }

    suspend fun translateToEnglish(text: String, sourceTag: String): String =
        translate(text, sourceTag, TranslateLanguage.ENGLISH)

    private suspend fun translate(text: String, sourceTag: String, targetTag: String): String {
        if (text.isBlank() || sourceTag == targetTag) return text
        if (TranslateLanguage.fromLanguageTag(sourceTag) == null || TranslateLanguage.fromLanguageTag(targetTag) == null) return text
        val key = "$sourceTag|$targetTag|$text"
        cache[key]?.let { return it }
        val translator = client(sourceTag, targetTag)
        translator.downloadModelIfNeeded(DownloadConditions.Builder().build()).await()
        return translator.translate(text).await().also { cache[key] = it }
    }

    private fun client(sourceTag: String, targetTag: String): Translator =
        clients.getOrPut("$sourceTag|$targetTag") {
            Translation.getClient(
                TranslatorOptions.Builder()
                    .setSourceLanguage(sourceTag)
                    .setTargetLanguage(targetTag)
                    .build(),
            )
        }

    companion object {
        val supportedLanguages = listOf(
            AppLanguage("en", "English", "English"), AppLanguage("es", "Spanish", "Español"),
            AppLanguage("fr", "French", "Français"), AppLanguage("de", "German", "Deutsch"),
            AppLanguage("pt", "Portuguese", "Português"), AppLanguage("it", "Italian", "Italiano"),
            AppLanguage("ar", "Arabic", "العربية"), AppLanguage("bn", "Bengali", "বাংলা"),
            AppLanguage("zh", "Chinese", "中文"), AppLanguage("hi", "Hindi", "हिन्दी"),
            AppLanguage("id", "Indonesian", "Bahasa Indonesia"), AppLanguage("ja", "Japanese", "日本語"),
            AppLanguage("ko", "Korean", "한국어"), AppLanguage("ru", "Russian", "Русский"),
            AppLanguage("tr", "Turkish", "Türkçe"), AppLanguage("uk", "Ukrainian", "Українська"),
            AppLanguage("ur", "Urdu", "اردو"), AppLanguage("vi", "Vietnamese", "Tiếng Việt"),
            AppLanguage("af", "Afrikaans", "Afrikaans"), AppLanguage("be", "Belarusian", "Беларуская"),
            AppLanguage("bg", "Bulgarian", "Български"), AppLanguage("ca", "Catalan", "Català"),
            AppLanguage("cs", "Czech", "Čeština"), AppLanguage("cy", "Welsh", "Cymraeg"),
            AppLanguage("da", "Danish", "Dansk"), AppLanguage("el", "Greek", "Ελληνικά"),
            AppLanguage("eo", "Esperanto", "Esperanto"), AppLanguage("et", "Estonian", "Eesti"),
            AppLanguage("fa", "Persian", "فارسی"), AppLanguage("fi", "Finnish", "Suomi"),
            AppLanguage("ga", "Irish", "Gaeilge"), AppLanguage("gl", "Galician", "Galego"),
            AppLanguage("gu", "Gujarati", "ગુજરાતી"), AppLanguage("he", "Hebrew", "עברית"),
            AppLanguage("hr", "Croatian", "Hrvatski"), AppLanguage("ht", "Haitian Creole", "Kreyòl ayisyen"),
            AppLanguage("hu", "Hungarian", "Magyar"), AppLanguage("is", "Icelandic", "Íslenska"),
            AppLanguage("ka", "Georgian", "ქართული"), AppLanguage("kn", "Kannada", "ಕನ್ನಡ"),
            AppLanguage("lt", "Lithuanian", "Lietuvių"), AppLanguage("lv", "Latvian", "Latviešu"),
            AppLanguage("mk", "Macedonian", "Македонски"), AppLanguage("mr", "Marathi", "मराठी"),
            AppLanguage("ms", "Malay", "Bahasa Melayu"), AppLanguage("mt", "Maltese", "Malti"),
            AppLanguage("nl", "Dutch", "Nederlands"), AppLanguage("no", "Norwegian", "Norsk"),
            AppLanguage("pl", "Polish", "Polski"), AppLanguage("ro", "Romanian", "Română"),
            AppLanguage("sk", "Slovak", "Slovenčina"), AppLanguage("sl", "Slovenian", "Slovenščina"),
            AppLanguage("sq", "Albanian", "Shqip"), AppLanguage("sv", "Swedish", "Svenska"),
            AppLanguage("sw", "Swahili", "Kiswahili"), AppLanguage("ta", "Tamil", "தமிழ்"),
            AppLanguage("te", "Telugu", "తెలుగు"), AppLanguage("th", "Thai", "ไทย"),
            AppLanguage("tl", "Filipino", "Filipino"),
        )
    }
}
