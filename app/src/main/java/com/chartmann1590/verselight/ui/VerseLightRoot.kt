@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.chartmann1590.verselight.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.chartmann1590.verselight.MainViewModel
import com.chartmann1590.verselight.model.ActivityType
import com.chartmann1590.verselight.model.DailyVerse
import com.chartmann1590.verselight.model.PrivateActivity
import com.chartmann1590.verselight.model.VerseComment
import com.chartmann1590.verselight.translation.AppLanguage
import com.chartmann1590.verselight.translation.OnDeviceTranslationRepository
import com.chartmann1590.verselight.translation.TranslationModelState
import com.chartmann1590.verselight.ui.theme.Gold
import com.chartmann1590.verselight.ui.theme.Ink
import kotlinx.coroutines.flow.collectLatest
import java.text.DateFormat

private data class TranslationContext(val repository: OnDeviceTranslationRepository, val languageTag: String)
private val LocalTranslation = staticCompositionLocalOf<TranslationContext?> { null }

private enum class Tab(val label: String, val icon: ImageVector) {
    TODAY("Today", Icons.Default.Home), COMMUNITY("Community", Icons.Default.ChatBubbleOutline), JOURNEY("My Journey", Icons.Default.AutoStories), PROFILE("Profile", Icons.Default.Person)
}

@Composable
fun VerseLightRoot(vm: MainViewModel = viewModel()) {
    val context = LocalContext.current
    val user by vm.user.collectAsStateWithLifecycle()
    val comments by vm.comments.collectAsStateWithLifecycle()
    val liked by vm.liked.collectAsStateWithLifecycle()
    val activity by vm.activity.collectAsStateWithLifecycle()
    val reminder by vm.reminder.collectAsStateWithLifecycle()
    val busy by vm.busy.collectAsStateWithLifecycle()
    val onboardingComplete by vm.onboardingComplete.collectAsStateWithLifecycle()
    val languageTag by vm.languageTag.collectAsStateWithLifecycle()
    val translationModelState by vm.translationModelState.collectAsStateWithLifecycle()
    var tab by rememberSaveable { mutableStateOf(Tab.TODAY) }
    var authOpen by rememberSaveable { mutableStateOf(false) }
    var composeOpen by rememberSaveable { mutableStateOf(false) }
    var reportTarget by remember { mutableStateOf<VerseComment?>(null) }
    val snackbars = remember { SnackbarHostState() }

    LaunchedEffect(Unit) { vm.messages.collectLatest { snackbars.showSnackbar(vm.translated(it.text)) } }
    LaunchedEffect(user?.uid) {
        if (user != null) authOpen = false
    }

    fun requireAuth(action: () -> Unit) {
        if (user == null) authOpen = true else action()
    }

    val translationContext = remember(languageTag) {
        TranslationContext((context.applicationContext as com.chartmann1590.verselight.VerseLightApplication).container.translation, languageTag)
    }

    if (onboardingComplete == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        return
    }
    if (onboardingComplete == false) {
        CompositionLocalProvider(LocalTranslation provides translationContext) {
            OnboardingScreen(
                userEmail = user?.email,
                selectedLanguageTag = languageTag,
                modelState = translationModelState,
                busy = busy,
                onSelectLanguage = vm::selectLanguage,
                onEmail = { email, password, register, name -> if (register) vm.register(email, password, name) else vm.signInEmail(email, password) },
                onGoogle = { vm.signInGoogle(context as Activity) },
                onFinish = vm::finishOnboarding,
            )
        }
        return
    }

    CompositionLocalProvider(LocalTranslation provides translationContext) {
    val translatedVerse = rememberTranslatedValue(vm.verse.text)
    val translatedReference = rememberTranslatedValue(vm.verse.reference)
    val translatedTranslation = rememberTranslatedValue(vm.verse.translation)
    val translatedSharedFrom = rememberTranslatedValue("Shared from VerseLight")
    val translatedShareTitle = rememberTranslatedValue("Share today's light")
    Scaffold(
        contentWindowInsets = WindowInsets(0),
        snackbarHost = { SnackbarHost(snackbars) },
        bottomBar = {
            NavigationBar(modifier = Modifier.navigationBarsPadding()) {
                Tab.entries.forEach { item ->
                    NavigationBarItem(selected = tab == item, onClick = { tab = item }, icon = { Icon(item.icon, null) }, label = { LText(item.label) })
                }
            }
        },
    ) { padding ->
        AnimatedContent(tab, modifier = Modifier.statusBarsPadding(), label = "section") { selected ->
            when (selected) {
                Tab.TODAY -> TodayScreen(vm.verse, liked, comments.size, padding, onLike = { requireAuth(vm::toggleLike) }, onShare = {
                    val text = "“$translatedVerse”\n— $translatedReference ($translatedTranslation)\n\n$translatedSharedFrom"
                    context.startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).apply { type = "text/plain"; putExtra(Intent.EXTRA_TEXT, text) }, translatedShareTitle))
                    vm.recordShare()
                }, onComment = { requireAuth { composeOpen = true } })
                Tab.COMMUNITY -> CommunityScreen(comments, user?.uid, padding, onSignIn = { authOpen = true }, onComment = { requireAuth { composeOpen = true } }, onDelete = vm::deleteComment, onReport = { comment -> requireAuth { reportTarget = comment } })
                Tab.JOURNEY -> JourneyScreen(activity, user != null, padding, onSignIn = { authOpen = true })
                Tab.PROFILE -> ProfileScreen(user?.displayName, user?.email, user?.photoUrl?.toString(), reminder.first, reminder.second, languageTag, translationModelState, padding, onSignIn = { authOpen = true }, onSignOut = vm::signOut, onUpdateName = vm::updateName, onReminder = vm::setReminder, onLanguage = vm::selectLanguage, onDelete = vm::deleteAccount)
            }
        }
        if (busy) LinearProgressIndicator(Modifier.fillMaxWidth().statusBarsPadding())
    }

    if (authOpen) AuthDialog(busy = busy, onDismiss = { authOpen = false }, onEmail = { email, password, register, name -> if (register) vm.register(email, password, name) else vm.signInEmail(email, password) }, onGoogle = { vm.signInGoogle(context as Activity) }, onReset = vm::resetPassword)
    if (composeOpen) CommentDialog(busy, onDismiss = { composeOpen = false }, onPost = { text, complete -> vm.postComment(text) { result -> complete(result.explanation, result.allowed); if (result.allowed) composeOpen = false } })
    reportTarget?.let { comment ->
        ReportDialog(busy, onDismiss = { reportTarget = null }, onSubmit = { reason, details ->
            vm.report(comment, reason, details)
            reportTarget = null
        })
    }
    }
}

@Composable
private fun OnboardingScreen(
    userEmail: String?,
    selectedLanguageTag: String,
    modelState: TranslationModelState,
    busy: Boolean,
    onSelectLanguage: (String, (Result<Unit>) -> Unit) -> Unit,
    onEmail: (String, String, Boolean, String) -> Unit,
    onGoogle: () -> Unit,
    onFinish: (String) -> Unit,
) {
    var step by rememberSaveable { mutableIntStateOf(0) }
    var pendingLanguage by rememberSaveable(selectedLanguageTag) { mutableStateOf(selectedLanguageTag) }
    var register by rememberSaveable { mutableStateOf(false) }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var name by rememberSaveable { mutableStateOf("") }
    var languageError by remember { mutableStateOf("") }
    val selected = OnDeviceTranslationRepository.supportedLanguages.first { it.tag == pendingLanguage }
    val gradient = Brush.verticalGradient(listOf(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.background))

    LazyColumn(
        Modifier.fillMaxSize().background(gradient).statusBarsPadding(),
        contentPadding = PaddingValues(24.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primary) {
                    Icon(Icons.Default.WbSunny, null, tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.padding(12.dp))
                }
                Spacer(Modifier.width(12.dp))
                Column { LText("VERSELight", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary); LText("A daily light for every language") }
            }
        }
        item { LText("Step ${step + 1} of 3", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.secondary) }
        when (step) {
            0 -> {
                item { LText("Welcome to VerseLight", style = MaterialTheme.typography.displaySmall); LText("A beautiful, quiet place for Scripture, reflection, and a private record of the verses you carry.", style = MaterialTheme.typography.bodyLarge) }
                item { OnboardingFeature(Icons.Default.AutoStories, "A new verse every day", "Read and share the public-domain World English Bible without an API key.") }
                item { OnboardingFeature(Icons.Default.Translate, "Your language, on your device", "ML Kit can download a language model and translate the whole experience privately on this device.") }
                item { OnboardingFeature(Icons.Default.Security, "A gracious community", "Comments are checked on your device before posting, checked again by the server, and can be reported for human review.") }
                item {
                    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer), shape = RoundedCornerShape(20.dp)) {
                        LText("Guests can read, browse reflections, and share verses. Sign-in is required to like, comment, report, or keep a synchronized private journey.", Modifier.padding(18.dp))
                    }
                }
                item { Button(onClick = { step = 1 }, modifier = Modifier.fillMaxWidth()) { LText("Choose my language") } }
            }
            1 -> {
                item { LText("Choose your language", style = MaterialTheme.typography.displaySmall); LText("Language models are about 30 MB and download once. Translation then runs on-device. Machine translation may not always be accurate, so Scripture references and the original source remain identifiable.") }
                item {
                    LazyColumn(Modifier.fillMaxWidth().height(420.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(OnDeviceTranslationRepository.supportedLanguages, key = { it.tag }) { language ->
                            Card(
                                Modifier.fillMaxWidth().clickable { pendingLanguage = language.tag },
                                colors = CardDefaults.cardColors(containerColor = if (pendingLanguage == language.tag) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface),
                                shape = RoundedCornerShape(16.dp),
                            ) {
                                Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Column(Modifier.weight(1f)) { Text(language.nativeName, fontWeight = FontWeight.SemiBold); Text(language.englishName, style = MaterialTheme.typography.bodySmall) }
                                    if (pendingLanguage == language.tag) Icon(Icons.Default.Bookmark, "Selected")
                                }
                            }
                        }
                    }
                }
                if (modelState is TranslationModelState.Downloading) item { LinearProgressIndicator(Modifier.fillMaxWidth()); LText("Downloading ${selected.englishName} for private on-device translation…") }
                if (languageError.isNotBlank()) item { LText(languageError, color = MaterialTheme.colorScheme.error) }
                item {
                    Button(
                        onClick = {
                            languageError = ""
                            onSelectLanguage(pendingLanguage) { result ->
                                result.onSuccess { step = 2 }.onFailure { languageError = it.message ?: "Language download failed. Please try again." }
                            }
                        },
                        enabled = !busy,
                        modifier = Modifier.fillMaxWidth(),
                    ) { if (busy) CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp) else LText(if (pendingLanguage == "en") "Continue in English" else "Download language and continue") }
                }
            }
            else -> {
                item { LText("How would you like to continue?", style = MaterialTheme.typography.displaySmall); LText("An account unlocks likes, comments, reports, and your private synchronized journey. You can still read and share as a guest.") }
                if (userEmail != null) {
                    item { Card(shape = RoundedCornerShape(20.dp)) { Column(Modifier.padding(20.dp)) { LText("Signed in", fontWeight = FontWeight.Bold); Text(userEmail); Button(onClick = { onFinish(pendingLanguage) }, modifier = Modifier.fillMaxWidth().padding(top = 12.dp)) { LText("Continue with this account") } } } }
                } else {
                    item {
                        Card(shape = RoundedCornerShape(22.dp)) { Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            LText(if (register) "Create your account" else "Sign in with email", style = MaterialTheme.typography.titleLarge)
                            if (register) OutlinedTextField(name, { name = it.take(40) }, label = { LText("Display name") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                            OutlinedTextField(email, { email = it }, label = { LText("Email") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                            OutlinedTextField(password, { password = it }, label = { LText("Password") }, singleLine = true, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth())
                            Button(onClick = { onEmail(email, password, register, name) }, enabled = !busy && email.contains('@') && password.length >= 6 && (!register || name.isNotBlank()), modifier = Modifier.fillMaxWidth()) { LText(if (register) "Create account" else "Sign in") }
                            OutlinedButton(onClick = onGoogle, enabled = !busy, modifier = Modifier.fillMaxWidth()) { LText("Continue with Google") }
                            TextButton(onClick = { register = !register }, modifier = Modifier.fillMaxWidth()) { LText(if (register) "I already have an account" else "Create an email account") }
                        } }
                    }
                }
                item { OutlinedButton(onClick = { onFinish(pendingLanguage) }, modifier = Modifier.fillMaxWidth()) { LText("Continue as guest") } }
                item { LText("You can sign in later from Community, My Journey, or Profile. Translation can always be changed in Profile settings.", style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()) }
            }
        }
    }
}

@Composable
private fun OnboardingFeature(icon: ImageVector, title: String, body: String) {
    Card(shape = RoundedCornerShape(20.dp)) {
        Row(Modifier.padding(18.dp), verticalAlignment = Alignment.Top) {
            Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer) { Icon(icon, null, Modifier.padding(10.dp), tint = MaterialTheme.colorScheme.primary) }
            Spacer(Modifier.width(14.dp))
            Column { LText(title, fontWeight = FontWeight.Bold); LText(body, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) }
        }
    }
}

@Composable
private fun ReportDialog(busy: Boolean, onDismiss: () -> Unit, onSubmit: (String, String) -> Unit) {
    val reasons = listOf("abuse" to "Abuse", "hate" to "Hate", "threat" to "Threat", "sexual" to "Sexual content", "spam" to "Spam", "other" to "Other")
    var selected by rememberSaveable { mutableStateOf("abuse") }
    var details by rememberSaveable { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { LText("Report this reflection") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                LText("Choose the clearest reason. Three independent reports temporarily hide a comment for moderator review.")
                reasons.forEach { (value, label) ->
                    FilterChip(selected = selected == value, onClick = { selected = value }, label = { LText(label) })
                }
                OutlinedTextField(value = details, onValueChange = { details = it.take(500) }, label = { LText("Optional details") }, modifier = Modifier.fillMaxWidth(), minLines = 2)
            }
        },
        confirmButton = { Button(onClick = { onSubmit(selected, details.trim()) }, enabled = !busy) { LText("Submit report") } },
        dismissButton = { TextButton(onClick = onDismiss, enabled = !busy) { LText("Cancel") } },
    )
}

@Composable
private fun TodayScreen(verse: DailyVerse, liked: Boolean, comments: Int, padding: PaddingValues, onLike: () -> Unit, onShare: () -> Unit, onComment: () -> Unit) {
    val gradient = Brush.verticalGradient(listOf(MaterialTheme.colorScheme.primaryContainer.copy(alpha = .7f), MaterialTheme.colorScheme.background, MaterialTheme.colorScheme.background))
    LazyColumn(Modifier.fillMaxSize().background(gradient).padding(padding), contentPadding = PaddingValues(horizontal = 20.dp, vertical = 24.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primary) { Icon(Icons.Default.WbSunny, null, tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.padding(10.dp)) }
                Spacer(Modifier.width(12.dp))
                Column { LText("VERSELight", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary); LText("A quiet moment for your soul", style = MaterialTheme.typography.labelMedium) }
            }
        }
        item { LText("Today’s light", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.secondary) }
        item {
            ElevatedCard(shape = RoundedCornerShape(32.dp), colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface), elevation = CardDefaults.elevatedCardElevation(8.dp)) {
                Column(Modifier.padding(horizontal = 28.dp, vertical = 34.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    LText("✦", color = Gold, fontSize = 26.sp)
                    Spacer(Modifier.height(18.dp))
                    LText("“${verse.text}”", style = MaterialTheme.typography.displaySmall, textAlign = TextAlign.Center)
                    Spacer(Modifier.height(24.dp))
                    LText(verse.reference.uppercase(), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                    LText(verse.translation, style = MaterialTheme.typography.bodySmall, fontStyle = FontStyle.Italic)
                    Spacer(Modifier.height(28.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = .25f))
                    Row(Modifier.fillMaxWidth().padding(top = 12.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                        VerseAction(if (liked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder, if (liked) "Saved" else "Like", onLike)
                        VerseAction(Icons.Default.Share, "Share", onShare)
                        VerseAction(Icons.Default.ChatBubbleOutline, if (comments == 0) "Reflect" else "$comments reflections", onComment)
                    }
                }
            }
        }
        item {
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = .55f)), shape = RoundedCornerShape(22.dp)) {
                Row(Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AutoStories, null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(14.dp))
                    LText("Carry this word with you. A new verse arrives for everyone at midnight UTC.", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Composable
private fun VerseAction(icon: ImageVector, label: String, action: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(onClick = action, modifier = Modifier.semantics { contentDescription = label }) { Icon(icon, null, tint = MaterialTheme.colorScheme.primary) }
        LText(label, style = MaterialTheme.typography.labelSmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
private fun CommunityScreen(comments: List<VerseComment>, uid: String?, padding: PaddingValues, onSignIn: () -> Unit, onComment: () -> Unit, onDelete: (VerseComment) -> Unit, onReport: (VerseComment) -> Unit) {
    LazyColumn(Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            LText("Community reflections", style = MaterialTheme.typography.headlineMedium)
            LText("A gracious place to share what today’s verse means to you.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(14.dp))
            Button(onClick = if (uid == null) onSignIn else onComment, modifier = Modifier.fillMaxWidth()) { Icon(Icons.Default.ChatBubbleOutline, null); Spacer(Modifier.width(8.dp)); LText(if (uid == null) "Sign in to reflect" else "Add your reflection") }
        }
        if (comments.isEmpty()) item { EmptyCard("Be the first to leave a thoughtful reflection today.") }
        items(comments, key = { it.id }) { comment ->
            CommentCard(comment, own = uid == comment.authorUid, onDelete = { onDelete(comment) }, onReport = { onReport(comment) })
        }
    }
}

@Composable
private fun CommentCard(comment: VerseComment, own: Boolean, onDelete: () -> Unit, onReport: () -> Unit) {
    Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)) {
        Column(Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (comment.authorAvatarUrl != null) AsyncImage(comment.authorAvatarUrl, null, Modifier.size(38.dp).clip(CircleShape)) else Icon(Icons.Default.AccountCircle, null, Modifier.size(38.dp), tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) { LText(comment.authorName, fontWeight = FontWeight.SemiBold, translate = false); LText(comment.createdAt?.toDate()?.let(DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT)::format) ?: "Just now", style = MaterialTheme.typography.labelSmall) }
                IconButton(onClick = if (own) onDelete else onReport) { Icon(if (own) Icons.Default.DeleteOutline else Icons.Default.Flag, if (own) "Delete comment" else "Report comment") }
            }
            Spacer(Modifier.height(12.dp)); LText(comment.body, style = MaterialTheme.typography.bodyLarge, dynamicSource = true)
        }
    }
}

@Composable
private fun JourneyScreen(activity: List<PrivateActivity>, signedIn: Boolean, padding: PaddingValues, onSignIn: () -> Unit) {
    LazyColumn(Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { LText("My Journey", style = MaterialTheme.typography.headlineMedium); LText("Your private trail of verses, reflections, and shares.", color = MaterialTheme.colorScheme.onSurfaceVariant); Spacer(Modifier.height(12.dp)) }
        if (!signedIn) item { EmptyCard("Sign in to keep a private, synchronized record of the light you carry.", "Sign in", onSignIn) }
        else if (activity.isEmpty()) item { EmptyCard("Your journey begins when you save, share, or reflect on a verse.") }
        items(activity, key = { it.id }) { item -> ActivityCard(item) }
    }
}

@Composable
private fun ActivityCard(item: PrivateActivity) {
    val icon = when (item.type) { ActivityType.LIKE.name -> Icons.Default.Bookmark; ActivityType.COMMENT.name -> Icons.Default.ChatBubbleOutline; else -> Icons.Default.Share }
    Card(shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)) {
        Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer) { Icon(icon, null, Modifier.padding(10.dp), tint = MaterialTheme.colorScheme.primary) }
            Spacer(Modifier.width(14.dp)); Column(Modifier.weight(1f)) { LText(item.reference, fontWeight = FontWeight.SemiBold); LText(item.preview, maxLines = 2, overflow = TextOverflow.Ellipsis, style = MaterialTheme.typography.bodyMedium, dynamicSource = true); LText(item.dayKey, style = MaterialTheme.typography.labelSmall, translate = false) }
        }
    }
}

@Composable
private fun ProfileScreen(name: String?, email: String?, avatar: String?, reminderEnabled: Boolean, reminderHour: Int, languageTag: String, modelState: TranslationModelState, padding: PaddingValues, onSignIn: () -> Unit, onSignOut: () -> Unit, onUpdateName: (String) -> Unit, onReminder: (Boolean, Int) -> Unit, onLanguage: (String, (Result<Unit>) -> Unit) -> Unit, onDelete: () -> Unit) {
    val context = LocalContext.current
    var editName by remember(name) { mutableStateOf(name.orEmpty()) }
    var hour by remember(reminderHour) { mutableIntStateOf(reminderHour) }
    var confirmDelete by remember { mutableStateOf(false) }
    var languageOpen by remember { mutableStateOf(false) }
    val notificationPermission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted -> if (granted) onReminder(true, hour) }
    LazyColumn(Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item { LText("Profile & peace", style = MaterialTheme.typography.headlineMedium); LText("Make VerseLight feel like home.", color = MaterialTheme.colorScheme.onSurfaceVariant) }
        if (email == null) item { EmptyCard("Sign in with Google or email to save your journey across devices.", "Sign in", onSignIn) }
        else {
            item {
                Card(shape = RoundedCornerShape(24.dp)) { Column(Modifier.fillMaxWidth().padding(22.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    if (avatar != null) AsyncImage(avatar, null, Modifier.size(78.dp).clip(CircleShape)) else Icon(Icons.Default.AccountCircle, null, Modifier.size(78.dp), tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.height(10.dp)); LText(name ?: "Friend", style = MaterialTheme.typography.titleLarge, translate = false); LText(email, style = MaterialTheme.typography.bodySmall, translate = false)
                    OutlinedTextField(editName, { editName = it.take(40) }, label = { LText("Public display name") }, singleLine = true, modifier = Modifier.fillMaxWidth().padding(top = 16.dp))
                    LText("Your email stays private.", style = MaterialTheme.typography.labelSmall, modifier = Modifier.align(Alignment.Start))
                    FilledTonalButton(onClick = { onUpdateName(editName) }, enabled = editName.isNotBlank(), modifier = Modifier.padding(top = 10.dp)) { LText("Save name") }
                } }
            }
        }
        item {
            Card(shape = RoundedCornerShape(22.dp), modifier = Modifier.clickable { languageOpen = true }) { Row(Modifier.fillMaxWidth().padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Language, null); Spacer(Modifier.width(10.dp)); Column(Modifier.weight(1f)) { LText("App language", fontWeight = FontWeight.SemiBold); Text(OnDeviceTranslationRepository.supportedLanguages.first { it.tag == languageTag }.nativeName, style = MaterialTheme.typography.bodySmall); LText("On-device translations may not always be accurate.", style = MaterialTheme.typography.labelSmall) }; LText("Change", color = MaterialTheme.colorScheme.primary)
            } }
        }
        item {
            Card(shape = RoundedCornerShape(22.dp)) { Column(Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.Notifications, null); Spacer(Modifier.width(10.dp)); Column(Modifier.weight(1f)) { LText("Daily reminder", fontWeight = FontWeight.SemiBold); LText(if (reminderEnabled) "Around ${formatHour(hour)}" else "Off", style = MaterialTheme.typography.bodySmall) }; Switch(reminderEnabled, onCheckedChange = { enabled -> if (enabled && Build.VERSION.SDK_INT >= 33) notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS) else onReminder(enabled, hour) }) }
                if (reminderEnabled) { Spacer(Modifier.height(12.dp)); Slider(value = hour.toFloat(), onValueChange = { hour = it.toInt() }, onValueChangeFinished = { onReminder(true, hour) }, valueRange = 0f..23f, steps = 22); LText(formatHour(hour), modifier = Modifier.align(Alignment.CenterHorizontally)) }
            } }
        }
        if (email != null) item { OutlinedButton(onClick = onSignOut, Modifier.fillMaxWidth()) { Icon(Icons.AutoMirrored.Filled.Logout, null); Spacer(Modifier.width(8.dp)); LText("Sign out") }; TextButton(onClick = { confirmDelete = true }, Modifier.fillMaxWidth()) { LText("Delete my account and private data", color = MaterialTheme.colorScheme.error) } }
        item { LText("World English Bible · Public domain\nVerseLight 1.0.0", style = MaterialTheme.typography.labelSmall, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()) }
    }
    if (languageOpen) LanguagePickerDialog(languageTag, modelState, onDismiss = { languageOpen = false }, onSelect = onLanguage)
    if (confirmDelete) AlertDialog(onDismissRequest = { confirmDelete = false }, title = { LText("Delete your account?") }, text = { LText("This permanently removes your VerseLight profile. Public comments are removed by the backend cleanup process.") }, confirmButton = { TextButton(onClick = { confirmDelete = false; onDelete() }) { LText("Delete permanently") } }, dismissButton = { TextButton(onClick = { confirmDelete = false }) { LText("Cancel") } })
}

@Composable
private fun LanguagePickerDialog(currentTag: String, modelState: TranslationModelState, onDismiss: () -> Unit, onSelect: (String, (Result<Unit>) -> Unit) -> Unit) {
    var pending by rememberSaveable(currentTag) { mutableStateOf(currentTag) }
    var error by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { LText("Choose app language") },
        text = {
            Column {
                LText("The selected ML Kit model downloads to this device. Automatic translations may not always be accurate.", style = MaterialTheme.typography.bodySmall)
                LazyColumn(Modifier.height(360.dp).padding(top = 10.dp)) {
                    items(OnDeviceTranslationRepository.supportedLanguages, key = { it.tag }) { language ->
                        Row(Modifier.fillMaxWidth().clickable { pending = language.tag }.padding(vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) { Text(language.nativeName, fontWeight = FontWeight.SemiBold); Text(language.englishName, style = MaterialTheme.typography.bodySmall) }
                            if (pending == language.tag) Icon(Icons.Default.Bookmark, "Selected")
                        }
                    }
                }
                if (modelState is TranslationModelState.Downloading) LinearProgressIndicator(Modifier.fillMaxWidth())
                if (error.isNotBlank()) LText(error, color = MaterialTheme.colorScheme.error)
            }
        },
        confirmButton = { Button(onClick = { onSelect(pending) { result -> result.onSuccess { onDismiss() }.onFailure { error = it.message ?: "Language download failed." } } }, enabled = modelState !is TranslationModelState.Downloading) { LText("Download and use") } },
        dismissButton = { TextButton(onClick = onDismiss) { LText("Cancel") } },
    )
}

@Composable
private fun EmptyCard(text: String, button: String? = null, action: () -> Unit = {}) {
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = .5f)), shape = RoundedCornerShape(20.dp)) { Column(Modifier.fillMaxWidth().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) { Icon(Icons.Default.AutoStories, null, tint = MaterialTheme.colorScheme.primary); Spacer(Modifier.height(10.dp)); LText(text, textAlign = TextAlign.Center); if (button != null) Button(action, Modifier.padding(top = 14.dp)) { LText(button) } } }
}

@Composable
private fun AuthDialog(busy: Boolean, onDismiss: () -> Unit, onEmail: (String, String, Boolean, String) -> Unit, onGoogle: () -> Unit, onReset: (String) -> Unit) {
    var register by rememberSaveable { mutableStateOf(false) }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var name by rememberSaveable { mutableStateOf("") }
    AlertDialog(onDismissRequest = onDismiss, title = { LText(if (register) "Begin your journey" else "Welcome back") }, text = { Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        LText("Your saved verses and activity remain private.")
        if (register) OutlinedTextField(name, { name = it.take(40) }, label = { LText("Display name") }, singleLine = true)
        OutlinedTextField(email, { email = it }, label = { LText("Email") }, singleLine = true)
        OutlinedTextField(password, { password = it }, label = { LText("Password") }, singleLine = true, visualTransformation = PasswordVisualTransformation())
        Button(onClick = { onEmail(email, password, register, name) }, enabled = !busy && email.contains('@') && password.length >= 6 && (!register || name.isNotBlank()), modifier = Modifier.fillMaxWidth()) { LText(if (register) "Create account" else "Sign in") }
        OutlinedButton(onClick = onGoogle, enabled = !busy, modifier = Modifier.fillMaxWidth()) { LText("Continue with Google") }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { TextButton(onClick = { register = !register }) { LText(if (register) "I have an account" else "Create account") }; if (!register) TextButton(onClick = { if (email.contains('@')) onReset(email) }) { LText("Reset password") } }
    } }, confirmButton = {}, dismissButton = { TextButton(onClick = onDismiss) { LText("Not now") } })
}

@Composable
private fun CommentDialog(busy: Boolean, onDismiss: () -> Unit, onPost: (String, (String, Boolean) -> Unit) -> Unit) {
    var text by rememberSaveable { mutableStateOf("") }
    var feedback by remember { mutableStateOf("") }
    AlertDialog(onDismissRequest = onDismiss, title = { LText("Share a reflection") }, text = { Column {
        LText("Keep it gracious. Safety checks run privately on this device.", style = MaterialTheme.typography.bodySmall)
        OutlinedTextField(text, { text = it.take(500); feedback = "" }, label = { LText("What does this verse stir in you?") }, minLines = 4, modifier = Modifier.fillMaxWidth().padding(top = 10.dp), supportingText = { LText("${text.length}/500") })
        if (feedback.isNotBlank()) LText(feedback, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 8.dp))
    } }, confirmButton = { Button(onClick = { onPost(text) { message, allowed -> if (!allowed) feedback = message } }, enabled = text.isNotBlank() && !busy) { if (busy) CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp) else LText("Post") } }, dismissButton = { TextButton(onClick = onDismiss) { LText("Cancel") } })
}

private fun formatHour(hour: Int): String = when { hour == 0 -> "12:00 AM"; hour < 12 -> "$hour:00 AM"; hour == 12 -> "12:00 PM"; else -> "${hour - 12}:00 PM" }

@Composable
private fun LText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: androidx.compose.ui.unit.TextUnit = androidx.compose.ui.unit.TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    textAlign: TextAlign? = null,
    style: TextStyle = androidx.compose.material3.LocalTextStyle.current,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip,
    translate: Boolean = true,
    dynamicSource: Boolean = false,
) {
    val context = LocalTranslation.current
    var displayed by remember(text, context?.languageTag, translate, dynamicSource) { mutableStateOf(text) }
    LaunchedEffect(text, context?.languageTag, translate, dynamicSource) {
        displayed = text
        if (translate && context != null && context.languageTag != "en") {
            displayed = runCatching {
                if (dynamicSource) context.repository.translateDynamic(text, context.languageTag)
                else context.repository.translateUi(text, context.languageTag)
            }.getOrDefault(text)
        }
    }
    Text(
        text = displayed,
        modifier = modifier,
        color = color,
        fontSize = fontSize,
        fontStyle = fontStyle,
        fontWeight = fontWeight,
        textAlign = textAlign,
        style = style,
        maxLines = maxLines,
        overflow = overflow,
    )
}

@Composable
private fun rememberTranslatedValue(text: String, dynamicSource: Boolean = false): String {
    val context = LocalTranslation.current
    var displayed by remember(text, context?.languageTag, dynamicSource) { mutableStateOf(text) }
    LaunchedEffect(text, context?.languageTag, dynamicSource) {
        displayed = text
        if (context != null && context.languageTag != "en") {
            displayed = runCatching {
                if (dynamicSource) context.repository.translateDynamic(text, context.languageTag)
                else context.repository.translateUi(text, context.languageTag)
            }.getOrDefault(text)
        }
    }
    return displayed
}
