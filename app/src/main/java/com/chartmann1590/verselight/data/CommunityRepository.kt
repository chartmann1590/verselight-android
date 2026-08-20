package com.chartmann1590.verselight.data

import com.chartmann1590.verselight.BuildConfig
import com.chartmann1590.verselight.model.ActivityType
import com.chartmann1590.verselight.model.DailyVerse
import com.chartmann1590.verselight.model.PrivateActivity
import com.chartmann1590.verselight.model.VerseComment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class CommunityRepository {
    private val auth by lazy { FirebaseAuth.getInstance() }
    private val db by lazy { FirebaseFirestore.getInstance() }

    fun comments(dayKey: String): Flow<List<VerseComment>> = callbackFlow {
        val registration = db.collection("dailyVerses").document(dayKey).collection("comments")
            .whereIn("moderationStatus", listOf("visible", "restored"))
            .orderBy("createdAt", Query.Direction.DESCENDING).limit(100)
            .addSnapshotListener { value, error ->
                if (error != null) close(error)
                else trySend(value?.documents.orEmpty().mapNotNull { doc ->
                    doc.toObject(VerseComment::class.java)?.copy(id = doc.id)
                })
            }
        awaitClose { registration.remove() }
    }

    fun isLiked(dayKey: String): Flow<Boolean> = callbackFlow {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            trySend(false); close(); return@callbackFlow
        }
        val registration = db.collection("users").document(uid).collection("likes").document(dayKey)
            .addSnapshotListener { value, _ -> trySend(value?.exists() == true) }
        awaitClose { registration.remove() }
    }

    fun activity(): Flow<List<PrivateActivity>> = callbackFlow {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            trySend(emptyList()); close(); return@callbackFlow
        }
        val registration = db.collection("users").document(uid).collection("activity")
            .orderBy("createdAt", Query.Direction.DESCENDING).limit(200)
            .addSnapshotListener { value, error ->
                if (error != null) close(error)
                else trySend(value?.documents.orEmpty().mapNotNull { doc ->
                    doc.toObject(PrivateActivity::class.java)?.copy(id = doc.id)
                })
            }
        awaitClose { registration.remove() }
    }

    suspend fun toggleLike(verse: DailyVerse, liked: Boolean) {
        val uid = requireNotNull(auth.currentUser?.uid)
        val like = db.collection("users").document(uid).collection("likes").document(verse.dayKey)
        val activity = db.collection("users").document(uid).collection("activity").document("like_${verse.dayKey}")
        val batch = db.batch()
        if (liked) {
            batch.delete(like).delete(activity)
        } else {
            batch.set(like, mapOf("dayKey" to verse.dayKey, "reference" to verse.reference, "createdAt" to FieldValue.serverTimestamp()))
            batch.set(activity, activityMap(verse, ActivityType.LIKE, "Saved today's light"))
        }
        batch.commit().await()
    }

    suspend fun addComment(verse: DailyVerse, body: String): String {
        val user = requireNotNull(auth.currentUser)
        val token = user.getIdToken(false).await().token.orEmpty()
        return withContext(Dispatchers.IO) {
            val connection = (URL("${BuildConfig.REPORTS_BASE_URL}/v1/comments").openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                setRequestProperty("Authorization", "Bearer $token")
                setRequestProperty("Content-Type", "application/json")
                connectTimeout = 10_000
                readTimeout = 30_000
                doOutput = true
            }
            val payload = JSONObject()
                .put("dayKey", verse.dayKey)
                .put("reference", verse.reference)
                .put("body", body.trim())
            connection.outputStream.use { it.write(payload.toString().toByteArray()) }
            val responseText = (if (connection.responseCode in 200..299) connection.inputStream else connection.errorStream)
                ?.bufferedReader()?.use { it.readText() }.orEmpty()
            if (connection.responseCode !in 200..299) error(JSONObject(responseText).optString("error", "Comment could not be posted"))
            JSONObject(responseText).getString("commentId")
        }
    }

    suspend fun deleteComment(dayKey: String, commentId: String) {
        val uid = requireNotNull(auth.currentUser?.uid)
        db.batch()
            .delete(db.collection("dailyVerses").document(dayKey).collection("comments").document(commentId))
            .delete(db.collection("users").document(uid).collection("activity").document("comment_$commentId"))
            .commit().await()
    }

    suspend fun recordShare(verse: DailyVerse) {
        val uid = auth.currentUser?.uid ?: return
        val activity = db.collection("users").document(uid).collection("activity").document()
        activity.set(activityMap(verse, ActivityType.SHARE, "Opened Android share sheet")).await()
    }

    suspend fun report(dayKey: String, commentId: String, reason: String, details: String = ""): String {
        val token = requireNotNull(auth.currentUser).getIdToken(false).await().token.orEmpty()
        return withContext(Dispatchers.IO) {
            val connection = (URL("${BuildConfig.REPORTS_BASE_URL}/v1/reports").openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                setRequestProperty("Authorization", "Bearer $token")
                setRequestProperty("Content-Type", "application/json")
                connectTimeout = 10_000
                readTimeout = 10_000
                doOutput = true
            }
            val payload = JSONObject().put("dayKey", dayKey).put("commentId", commentId).put("reason", reason).put("optionalDetails", details)
            connection.outputStream.use { it.write(payload.toString().toByteArray()) }
            val responseText = (if (connection.responseCode in 200..299) connection.inputStream else connection.errorStream)
                ?.bufferedReader()?.use { it.readText() }.orEmpty()
            if (connection.responseCode !in 200..299) error(JSONObject(responseText).optString("error", "Report failed"))
            JSONObject(responseText).optString("status", "queued")
        }
    }

    suspend fun deleteAccountComments() {
        val token = requireNotNull(auth.currentUser).getIdToken(false).await().token.orEmpty()
        withContext(Dispatchers.IO) {
            val connection = (URL("${BuildConfig.REPORTS_BASE_URL}/v1/account/comments").openConnection() as HttpURLConnection).apply {
                requestMethod = "DELETE"
                setRequestProperty("Authorization", "Bearer $token")
                connectTimeout = 10_000
                readTimeout = 15_000
            }
            val body = (if (connection.responseCode in 200..299) connection.inputStream else connection.errorStream)?.bufferedReader()?.use { it.readText() }.orEmpty()
            if (connection.responseCode !in 200..299) error(JSONObject(body).optString("error", "Could not delete public comments"))
        }
    }

    private fun activityMap(verse: DailyVerse, type: ActivityType, preview: String, commentId: String? = null) = mapOf(
        "dayKey" to verse.dayKey,
        "reference" to verse.reference,
        "preview" to preview,
        "type" to type.name,
        "commentId" to commentId,
        "createdAt" to FieldValue.serverTimestamp(),
    )
}
