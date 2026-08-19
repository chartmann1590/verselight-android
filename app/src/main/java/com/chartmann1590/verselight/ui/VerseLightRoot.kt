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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.chartmann1590.verselight.ui.theme.Gold
import com.chartmann1590.verselight.ui.theme.Ink
import kotlinx.coroutines.flow.collectLatest
import java.text.DateFormat

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
    var tab by rememberSaveable { mutableStateOf(Tab.TODAY) }
    var authOpen by rememberSaveable { mutableStateOf(false) }
    var composeOpen by rememberSaveable { mutableStateOf(false) }
    val snackbars = remember { SnackbarHostState() }

    LaunchedEffect(Unit) { vm.messages.collectLatest { snackbars.showSnackbar(it.text) } }

    fun requireAuth(action: () -> Unit) {
        if (user == null) authOpen = true else action()
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0),
        snackbarHost = { SnackbarHost(snackbars) },
        bottomBar = {
            NavigationBar(modifier = Modifier.navigationBarsPadding()) {
                Tab.entries.forEach { item ->
                    NavigationBarItem(selected = tab == item, onClick = { tab = item }, icon = { Icon(item.icon, null) }, label = { Text(item.label) })
                }
            }
        },
    ) { padding ->
        AnimatedContent(tab, modifier = Modifier.statusBarsPadding(), label = "section") { selected ->
            when (selected) {
                Tab.TODAY -> TodayScreen(vm.verse, liked, comments.size, padding, onLike = { requireAuth(vm::toggleLike) }, onShare = {
                    val text = "“${vm.verse.text}”\n— ${vm.verse.reference} (${vm.verse.translation})\n\nShared from VerseLight"
                    context.startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).apply { type = "text/plain"; putExtra(Intent.EXTRA_TEXT, text) }, "Share today's light"))
                    vm.recordShare()
                }, onComment = { requireAuth { composeOpen = true } })
                Tab.COMMUNITY -> CommunityScreen(comments, user?.uid, padding, onSignIn = { authOpen = true }, onComment = { requireAuth { composeOpen = true } }, onDelete = vm::deleteComment, onReport = { comment -> requireAuth { vm.report(comment, "abuse") } })
                Tab.JOURNEY -> JourneyScreen(activity, user != null, padding, onSignIn = { authOpen = true })
                Tab.PROFILE -> ProfileScreen(user?.displayName, user?.email, user?.photoUrl?.toString(), reminder.first, reminder.second, padding, onSignIn = { authOpen = true }, onSignOut = vm::signOut, onUpdateName = vm::updateName, onReminder = vm::setReminder, onDelete = vm::deleteAccount)
            }
        }
        if (busy) LinearProgressIndicator(Modifier.fillMaxWidth().statusBarsPadding())
    }

    if (authOpen) AuthDialog(busy = busy, onDismiss = { authOpen = false }, onEmail = { email, password, register, name -> if (register) vm.register(email, password, name) else vm.signInEmail(email, password) }, onGoogle = { vm.signInGoogle(context as Activity) }, onReset = vm::resetPassword)
    if (composeOpen) CommentDialog(busy, onDismiss = { composeOpen = false }, onPost = { text, complete -> vm.postComment(text) { result -> complete(result.explanation, result.allowed); if (result.allowed) composeOpen = false } })
}

@Composable
private fun TodayScreen(verse: DailyVerse, liked: Boolean, comments: Int, padding: PaddingValues, onLike: () -> Unit, onShare: () -> Unit, onComment: () -> Unit) {
    val gradient = Brush.verticalGradient(listOf(MaterialTheme.colorScheme.primaryContainer.copy(alpha = .7f), MaterialTheme.colorScheme.background, MaterialTheme.colorScheme.background))
    LazyColumn(Modifier.fillMaxSize().background(gradient).padding(padding), contentPadding = PaddingValues(horizontal = 20.dp, vertical = 24.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primary) { Icon(Icons.Default.WbSunny, null, tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.padding(10.dp)) }
                Spacer(Modifier.width(12.dp))
                Column { Text("VERSELight", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary); Text("A quiet moment for your soul", style = MaterialTheme.typography.labelMedium) }
            }
        }
        item { Text("Today’s light", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.secondary) }
        item {
            ElevatedCard(shape = RoundedCornerShape(32.dp), colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface), elevation = CardDefaults.elevatedCardElevation(8.dp)) {
                Column(Modifier.padding(horizontal = 28.dp, vertical = 34.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("✦", color = Gold, fontSize = 26.sp)
                    Spacer(Modifier.height(18.dp))
                    Text("“${verse.text}”", style = MaterialTheme.typography.displaySmall, textAlign = TextAlign.Center)
                    Spacer(Modifier.height(24.dp))
                    Text(verse.reference.uppercase(), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                    Text(verse.translation, style = MaterialTheme.typography.bodySmall, fontStyle = FontStyle.Italic)
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
                    Text("Carry this word with you. A new verse arrives for everyone at midnight UTC.", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Composable
private fun VerseAction(icon: ImageVector, label: String, action: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(onClick = action, modifier = Modifier.semantics { contentDescription = label }) { Icon(icon, null, tint = MaterialTheme.colorScheme.primary) }
        Text(label, style = MaterialTheme.typography.labelSmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
private fun CommunityScreen(comments: List<VerseComment>, uid: String?, padding: PaddingValues, onSignIn: () -> Unit, onComment: () -> Unit, onDelete: (VerseComment) -> Unit, onReport: (VerseComment) -> Unit) {
    LazyColumn(Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Text("Community reflections", style = MaterialTheme.typography.headlineMedium)
            Text("A gracious place to share what today’s verse means to you.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(14.dp))
            Button(onClick = if (uid == null) onSignIn else onComment, modifier = Modifier.fillMaxWidth()) { Icon(Icons.Default.ChatBubbleOutline, null); Spacer(Modifier.width(8.dp)); Text(if (uid == null) "Sign in to reflect" else "Add your reflection") }
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
                Column(Modifier.weight(1f)) { Text(comment.authorName, fontWeight = FontWeight.SemiBold); Text(comment.createdAt?.toDate()?.let(DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT)::format) ?: "Just now", style = MaterialTheme.typography.labelSmall) }
                IconButton(onClick = if (own) onDelete else onReport) { Icon(if (own) Icons.Default.DeleteOutline else Icons.Default.Flag, if (own) "Delete comment" else "Report comment") }
            }
            Spacer(Modifier.height(12.dp)); Text(comment.body, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
private fun JourneyScreen(activity: List<PrivateActivity>, signedIn: Boolean, padding: PaddingValues, onSignIn: () -> Unit) {
    LazyColumn(Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { Text("My Journey", style = MaterialTheme.typography.headlineMedium); Text("Your private trail of verses, reflections, and shares.", color = MaterialTheme.colorScheme.onSurfaceVariant); Spacer(Modifier.height(12.dp)) }
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
            Spacer(Modifier.width(14.dp)); Column(Modifier.weight(1f)) { Text(item.reference, fontWeight = FontWeight.SemiBold); Text(item.preview, maxLines = 2, overflow = TextOverflow.Ellipsis, style = MaterialTheme.typography.bodyMedium); Text(item.dayKey, style = MaterialTheme.typography.labelSmall) }
        }
    }
}

@Composable
private fun ProfileScreen(name: String?, email: String?, avatar: String?, reminderEnabled: Boolean, reminderHour: Int, padding: PaddingValues, onSignIn: () -> Unit, onSignOut: () -> Unit, onUpdateName: (String) -> Unit, onReminder: (Boolean, Int) -> Unit, onDelete: () -> Unit) {
    val context = LocalContext.current
    var editName by remember(name) { mutableStateOf(name.orEmpty()) }
    var hour by remember(reminderHour) { mutableIntStateOf(reminderHour) }
    var confirmDelete by remember { mutableStateOf(false) }
    val notificationPermission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted -> if (granted) onReminder(true, hour) }
    LazyColumn(Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item { Text("Profile & peace", style = MaterialTheme.typography.headlineMedium); Text("Make VerseLight feel like home.", color = MaterialTheme.colorScheme.onSurfaceVariant) }
        if (email == null) item { EmptyCard("Sign in with Google or email to save your journey across devices.", "Sign in", onSignIn) }
        else {
            item {
                Card(shape = RoundedCornerShape(24.dp)) { Column(Modifier.fillMaxWidth().padding(22.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    if (avatar != null) AsyncImage(avatar, null, Modifier.size(78.dp).clip(CircleShape)) else Icon(Icons.Default.AccountCircle, null, Modifier.size(78.dp), tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.height(10.dp)); Text(name ?: "Friend", style = MaterialTheme.typography.titleLarge); Text(email, style = MaterialTheme.typography.bodySmall)
                    OutlinedTextField(editName, { editName = it.take(40) }, label = { Text("Public display name") }, singleLine = true, modifier = Modifier.fillMaxWidth().padding(top = 16.dp))
                    Text("Your email stays private.", style = MaterialTheme.typography.labelSmall, modifier = Modifier.align(Alignment.Start))
                    FilledTonalButton(onClick = { onUpdateName(editName) }, enabled = editName.isNotBlank(), modifier = Modifier.padding(top = 10.dp)) { Text("Save name") }
                } }
            }
        }
        item {
            Card(shape = RoundedCornerShape(22.dp)) { Column(Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.Notifications, null); Spacer(Modifier.width(10.dp)); Column(Modifier.weight(1f)) { Text("Daily reminder", fontWeight = FontWeight.SemiBold); Text(if (reminderEnabled) "Around ${formatHour(hour)}" else "Off", style = MaterialTheme.typography.bodySmall) }; Switch(reminderEnabled, onCheckedChange = { enabled -> if (enabled && Build.VERSION.SDK_INT >= 33) notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS) else onReminder(enabled, hour) }) }
                if (reminderEnabled) { Spacer(Modifier.height(12.dp)); Slider(value = hour.toFloat(), onValueChange = { hour = it.toInt() }, onValueChangeFinished = { onReminder(true, hour) }, valueRange = 0f..23f, steps = 22); Text(formatHour(hour), modifier = Modifier.align(Alignment.CenterHorizontally)) }
            } }
        }
        if (email != null) item { OutlinedButton(onClick = onSignOut, Modifier.fillMaxWidth()) { Icon(Icons.AutoMirrored.Filled.Logout, null); Spacer(Modifier.width(8.dp)); Text("Sign out") }; TextButton(onClick = { confirmDelete = true }, Modifier.fillMaxWidth()) { Text("Delete my account and private data", color = MaterialTheme.colorScheme.error) } }
        item { Text("World English Bible · Public domain\nVerseLight 1.0.0", style = MaterialTheme.typography.labelSmall, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()) }
    }
    if (confirmDelete) AlertDialog(onDismissRequest = { confirmDelete = false }, title = { Text("Delete your account?") }, text = { Text("This permanently removes your VerseLight profile. Public comments are removed by the backend cleanup process.") }, confirmButton = { TextButton(onClick = { confirmDelete = false; onDelete() }) { Text("Delete permanently") } }, dismissButton = { TextButton(onClick = { confirmDelete = false }) { Text("Cancel") } })
}

@Composable
private fun EmptyCard(text: String, button: String? = null, action: () -> Unit = {}) {
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = .5f)), shape = RoundedCornerShape(20.dp)) { Column(Modifier.fillMaxWidth().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) { Icon(Icons.Default.AutoStories, null, tint = MaterialTheme.colorScheme.primary); Spacer(Modifier.height(10.dp)); Text(text, textAlign = TextAlign.Center); if (button != null) Button(action, Modifier.padding(top = 14.dp)) { Text(button) } } }
}

@Composable
private fun AuthDialog(busy: Boolean, onDismiss: () -> Unit, onEmail: (String, String, Boolean, String) -> Unit, onGoogle: () -> Unit, onReset: (String) -> Unit) {
    var register by rememberSaveable { mutableStateOf(false) }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var name by rememberSaveable { mutableStateOf("") }
    AlertDialog(onDismissRequest = onDismiss, title = { Text(if (register) "Begin your journey" else "Welcome back") }, text = { Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("Your saved verses and activity remain private.")
        if (register) OutlinedTextField(name, { name = it.take(40) }, label = { Text("Display name") }, singleLine = true)
        OutlinedTextField(email, { email = it }, label = { Text("Email") }, singleLine = true)
        OutlinedTextField(password, { password = it }, label = { Text("Password") }, singleLine = true)
        Button(onClick = { onEmail(email, password, register, name) }, enabled = !busy && email.contains('@') && password.length >= 6 && (!register || name.isNotBlank()), modifier = Modifier.fillMaxWidth()) { Text(if (register) "Create account" else "Sign in") }
        OutlinedButton(onClick = onGoogle, enabled = !busy, modifier = Modifier.fillMaxWidth()) { Text("Continue with Google") }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { TextButton(onClick = { register = !register }) { Text(if (register) "I have an account" else "Create account") }; if (!register) TextButton(onClick = { if (email.contains('@')) onReset(email) }) { Text("Reset password") } }
    } }, confirmButton = {}, dismissButton = { TextButton(onClick = onDismiss) { Text("Not now") } })
}

@Composable
private fun CommentDialog(busy: Boolean, onDismiss: () -> Unit, onPost: (String, (String, Boolean) -> Unit) -> Unit) {
    var text by rememberSaveable { mutableStateOf("") }
    var feedback by remember { mutableStateOf("") }
    AlertDialog(onDismissRequest = onDismiss, title = { Text("Share a reflection") }, text = { Column {
        Text("Keep it gracious. Safety checks run privately on this device.", style = MaterialTheme.typography.bodySmall)
        OutlinedTextField(text, { text = it.take(500); feedback = "" }, label = { Text("What does this verse stir in you?") }, minLines = 4, modifier = Modifier.fillMaxWidth().padding(top = 10.dp), supportingText = { Text("${text.length}/500") })
        if (feedback.isNotBlank()) Text(feedback, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 8.dp))
    } }, confirmButton = { Button(onClick = { onPost(text) { message, allowed -> if (!allowed) feedback = message } }, enabled = text.isNotBlank() && !busy) { if (busy) CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp) else Text("Post") } }, dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } })
}

private fun formatHour(hour: Int): String = when { hour == 0 -> "12:00 AM"; hour < 12 -> "$hour:00 AM"; hour == 12 -> "12:00 PM"; else -> "${hour - 12}:00 PM" }
