package com.chartmann1590.verselight

import android.app.Activity
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.chartmann1590.verselight.model.ModerationResult
import com.chartmann1590.verselight.model.PrivateActivity
import com.chartmann1590.verselight.model.VerseComment
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.chartmann1590.verselight.translation.TranslationModelState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class AppMessage(val text: String, val isError: Boolean = false)

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val container = (application as VerseLightApplication).container
    val verse = container.verses.today()
    val user: StateFlow<FirebaseUser?> = container.auth.user.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), container.auth.currentUser())
    val comments: StateFlow<List<VerseComment>> = container.community.comments(verse.dayKey)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    val liked: StateFlow<Boolean> = user.flatMapLatest { if (it == null) flowOf(false) else container.community.isLiked(verse.dayKey) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)
    val activity: StateFlow<List<PrivateActivity>> = user.flatMapLatest { if (it == null) flowOf(emptyList()) else container.community.activity() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    val reminder = container.preferences.reminder.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false to 8)
    val onboardingComplete = container.preferences.onboardingComplete.stateIn(viewModelScope, SharingStarted.Eagerly, null)
    val languageTag = container.preferences.languageTag.stateIn(viewModelScope, SharingStarted.Eagerly, "en")
    val translationModelState: StateFlow<TranslationModelState> = container.translation.modelState
    val busy = MutableStateFlow(false)
    private val _messages = MutableSharedFlow<AppMessage>(extraBufferCapacity = 4)
    val messages = _messages.asSharedFlow()

    init {
        viewModelScope.launch {
            languageTag.collect { tag ->
                FirebaseCrashlytics.getInstance().setCustomKey("translation_language", tag)
                container.translation.prepareLanguage(tag)
            }
        }
        viewModelScope.launch {
            user.collect { account -> FirebaseCrashlytics.getInstance().setUserId(account?.uid.orEmpty()) }
        }
    }

    fun signInEmail(email: String, password: String) = launchTask("Welcome back") { container.auth.signInWithEmail(email, password) }
    fun register(email: String, password: String, name: String) = launchTask("Account created — check your email to verify it") { container.auth.register(email, password, name) }
    fun resetPassword(email: String) = launchTask("Password reset email sent") { container.auth.sendPasswordReset(email) }
    fun signInGoogle(activity: Activity) = launchTask("Welcome to VerseLight") { container.auth.signInWithGoogle(activity) }
    fun signOut() = launchTask("Signed out") { container.auth.signOut() }
    fun updateName(name: String) = launchTask("Profile updated") { container.auth.updatePublicProfile(name) }
    fun deleteAccount() = launchTask("Your account was deleted") {
        container.community.deleteAccountComments()
        container.auth.deleteAccount()
    }
    fun toggleLike() = launchTask(if (liked.value) "Removed from your journey" else "Saved to your journey") { container.community.toggleLike(verse, liked.value) }
    fun recordShare() { viewModelScope.launch { runCatching { container.community.recordShare(verse) } } }

    fun postComment(text: String, onResult: (ModerationResult) -> Unit) {
        viewModelScope.launch {
            busy.value = true
            val localModeration = container.safety.classify(text)
            val englishText = if (languageTag.value == "en") text else
                runCatching { container.translation.translateToEnglish(text, languageTag.value) }.getOrDefault(text)
            val translatedModeration = if (englishText == text) localModeration else container.safety.classify(englishText)
            val moderation = if (!localModeration.allowed) localModeration else translatedModeration
            if (!moderation.allowed) {
                busy.value = false
                onResult(moderation)
                return@launch
            }
            runCatching { container.community.addComment(verse, text) }
                .onSuccess { _messages.emit(AppMessage("Your reflection is now part of today's conversation")); onResult(moderation) }
                .onFailure { _messages.emit(AppMessage(it.readableMessage(), true)) }
            busy.value = false
        }
    }

    fun deleteComment(comment: VerseComment) = launchTask("Comment deleted") { container.community.deleteComment(comment.dayKey, comment.id) }
    fun report(comment: VerseComment, reason: String, details: String = "") = launchTask("Thank you. The report is queued for review") {
        container.community.report(comment.dayKey, comment.id, reason, details)
    }

    fun setReminder(enabled: Boolean, hour: Int) {
        viewModelScope.launch {
            container.preferences.setReminder(enabled, hour)
            ReminderScheduler.schedule(getApplication(), enabled, hour)
            _messages.emit(AppMessage(if (enabled) "Daily reminder set" else "Daily reminder turned off"))
        }
    }

    suspend fun translated(text: String, dynamicSource: Boolean = false): String =
        if (dynamicSource) container.translation.translateDynamic(text, languageTag.value)
        else container.translation.translateUi(text, languageTag.value)

    fun selectLanguage(tag: String, onComplete: (Result<Unit>) -> Unit = {}) {
        viewModelScope.launch {
            busy.value = true
            val result = container.translation.prepareLanguage(tag)
            if (result.isSuccess) container.preferences.setLanguage(tag)
            else FirebaseCrashlytics.getInstance().recordException(result.exceptionOrNull()!!)
            busy.value = false
            onComplete(result)
        }
    }

    fun finishOnboarding(tag: String) {
        viewModelScope.launch { container.preferences.completeOnboarding(tag) }
    }

    private fun launchTask(success: String, task: suspend () -> Unit) {
        viewModelScope.launch {
            busy.value = true
            runCatching { task() }
                .onSuccess { _messages.emit(AppMessage(success)) }
                .onFailure { _messages.emit(AppMessage(it.readableMessage(), true)) }
            busy.value = false
        }
    }

    private fun Throwable.readableMessage(): String = message?.substringBefore(" [")?.take(180) ?: "Something went wrong. Please try again."
}
