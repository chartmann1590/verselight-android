package com.chartmann1590.verselight.model

import com.google.firebase.Timestamp

data class DailyVerse(
    val dayKey: String,
    val reference: String,
    val text: String,
    val translation: String = "World English Bible",
)

data class PublicProfile(
    val uid: String = "",
    val displayName: String = "Friend",
    val avatarUrl: String? = null,
)

enum class ModerationStatus { VISIBLE, HIDDEN_PENDING_REVIEW, REMOVED, RESTORED }

data class VerseComment(
    val id: String = "",
    val dayKey: String = "",
    val authorUid: String = "",
    val authorName: String = "Friend",
    val authorAvatarUrl: String? = null,
    val body: String = "",
    val createdAt: Timestamp? = null,
    val editedAt: Timestamp? = null,
    val moderationStatus: String = "visible",
)

enum class ActivityType { LIKE, COMMENT, SHARE }

data class PrivateActivity(
    val id: String = "",
    val dayKey: String = "",
    val reference: String = "",
    val preview: String = "",
    val type: String = "LIKE",
    val commentId: String? = null,
    val createdAt: Timestamp? = null,
)

data class ModerationResult(
    val allowed: Boolean,
    val categories: Set<String> = emptySet(),
    val confidence: Float = 0f,
    val explanation: String = "",
    val usedGeminiNano: Boolean = false,
)

