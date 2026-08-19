package com.chartmann1590.verselight.data

import android.app.Activity
import android.content.Context
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.chartmann1590.verselight.R
import com.chartmann1590.verselight.model.PublicProfile
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class AuthRepository(private val context: Context) {
    private val auth by lazy { FirebaseAuth.getInstance() }
    private val db by lazy { FirebaseFirestore.getInstance() }
    private val credentials by lazy { CredentialManager.create(context) }

    val user: Flow<FirebaseUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { trySend(it.currentUser) }
        auth.addAuthStateListener(listener)
        trySend(auth.currentUser)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    fun currentUser(): FirebaseUser? = auth.currentUser

    suspend fun signInWithEmail(email: String, password: String) {
        auth.signInWithEmailAndPassword(email.trim(), password).await()
        ensureProfile()
    }

    suspend fun register(email: String, password: String, displayName: String) {
        val result = auth.createUserWithEmailAndPassword(email.trim(), password).await()
        result.user?.sendEmailVerification()?.await()
        ensureProfile(displayName)
    }

    suspend fun sendPasswordReset(email: String) {
        auth.sendPasswordResetEmail(email.trim()).await()
    }

    suspend fun signInWithGoogle(activity: Activity) {
        val option = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(context.getString(R.string.google_web_client_id))
            .setAutoSelectEnabled(true)
            .build()
        val request = GetCredentialRequest.Builder().addCredentialOption(option).build()
        val result = credentials.getCredential(activity, request)
        val googleCredential = GoogleIdTokenCredential.createFrom(result.credential.data)
        auth.signInWithCredential(GoogleAuthProvider.getCredential(googleCredential.idToken, null)).await()
        ensureProfile()
    }

    suspend fun updatePublicProfile(displayName: String) {
        val user = requireNotNull(auth.currentUser)
        val clean = displayName.trim().take(40)
        val private = mapOf("displayName" to clean, "email" to user.email, "updatedAt" to com.google.firebase.firestore.FieldValue.serverTimestamp())
        val public = PublicProfile(user.uid, clean, user.photoUrl?.toString())
        db.batch()
            .set(db.collection("users").document(user.uid), private, SetOptions.merge())
            .set(db.collection("publicProfiles").document(user.uid), public)
            .commit().await()
    }

    suspend fun signOut() {
        auth.signOut()
        credentials.clearCredentialState(ClearCredentialStateRequest())
    }

    suspend fun deleteAccount() {
        val user = requireNotNull(auth.currentUser)
        deleteCollection(db.collection("users").document(user.uid).collection("likes"))
        deleteCollection(db.collection("users").document(user.uid).collection("activity"))
        db.collection("users").document(user.uid).delete().await()
        db.collection("publicProfiles").document(user.uid).delete().await()
        user.delete().await()
        credentials.clearCredentialState(ClearCredentialStateRequest())
    }

    private suspend fun deleteCollection(collection: com.google.firebase.firestore.CollectionReference) {
        while (true) {
            val snapshot = collection.limit(400).get().await()
            if (snapshot.isEmpty) return
            val batch = db.batch()
            snapshot.documents.forEach { batch.delete(it.reference) }
            batch.commit().await()
        }
    }

    private suspend fun ensureProfile(nameOverride: String? = null) {
        val user = requireNotNull(auth.currentUser)
        val display = nameOverride?.trim()?.takeIf { it.isNotEmpty() }
            ?: user.displayName?.takeIf { it.isNotBlank() }
            ?: user.email?.substringBefore('@')
            ?: "Friend"
        val private = mapOf(
            "uid" to user.uid,
            "email" to user.email,
            "displayName" to display,
            "avatarUrl" to user.photoUrl?.toString(),
            "updatedAt" to com.google.firebase.firestore.FieldValue.serverTimestamp(),
        )
        val public = PublicProfile(user.uid, display, user.photoUrl?.toString())
        db.batch()
            .set(db.collection("users").document(user.uid), private, SetOptions.merge())
            .set(db.collection("publicProfiles").document(user.uid), public, SetOptions.merge())
            .commit().await()
    }
}
