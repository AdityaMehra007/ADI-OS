package com.example.auth

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import com.example.R
import com.google.android.gms.tasks.Task
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class TitanAuthService(
  private val context: Context,
  private val scope: CoroutineScope
) {
  private val tag = "TitanAuthService"
  private val prefsName = "titan_auth_session"
  private val prefs by lazy { context.getSharedPreferences(prefsName, Context.MODE_PRIVATE) }

  private var firebaseAuth: FirebaseAuth? = null
  private val credentialManager = CredentialManager.create(context)

  private val _currentUser = MutableStateFlow<TitanAuthUser?>(null)
  val currentUser: StateFlow<TitanAuthUser?> = _currentUser.asStateFlow()

  private val _authState = MutableStateFlow(AuthState.UNAUTHENTICATED)
  val authState: StateFlow<AuthState> = _authState.asStateFlow()

  private val _sessionState = MutableStateFlow<ExecutiveSessionState>(ExecutiveSessionState.Unauthenticated)
  val sessionState: StateFlow<ExecutiveSessionState> = _sessionState.asStateFlow()

  private val _isExecutiveAccessGranted = MutableStateFlow(false)
  val isExecutiveAccessGranted: StateFlow<Boolean> = _isExecutiveAccessGranted.asStateFlow()

  private val _authError = MutableStateFlow<String?>(null)
  val authError: StateFlow<String?> = _authError.asStateFlow()

  // Stored reference to FirebaseAuth.AuthStateListener to serve as the single source
  // of truth for user session persistence and prevent memory leaks on disposal.
  private val authStateListener = FirebaseAuth.AuthStateListener { firebaseUserAuth ->
    val user = firebaseUserAuth.currentUser
    handleAuthStateChanged(user)
  }

  init {
    restorePersistedSessionMetadataIfAvailable()
    initializeFirebase()
  }

  private fun initializeFirebase() {
    try {
      if (FirebaseApp.getApps(context).isNotEmpty()) {
        val auth = FirebaseAuth.getInstance()
        firebaseAuth = auth

        // Register the stored AuthStateListener to guarantee session persistence across lifecycle
        auth.addAuthStateListener(authStateListener)

        val initialUser = auth.currentUser
        if (initialUser != null) {
          handleAuthStateChanged(initialUser)
        }
      }
    } catch (e: Exception) {
      Log.w(tag, "Firebase Auth initialization warning: ${e.message}", e)
    }
  }

  private fun handleAuthStateChanged(user: FirebaseUser?) {
    if (user != null) {
      val titanUser = mapFirebaseUser(user)
      persistSession(titanUser)
      _currentUser.value = titanUser
      _authState.value = AuthState.AUTHENTICATED
      _isExecutiveAccessGranted.value = true
      _sessionState.value = ExecutiveSessionState.Authenticated(
        user = titanUser,
        sessionStartedTimestamp = System.currentTimeMillis(),
        isExecutiveClearanceGranted = true,
        securityLevel = "LEVEL-5 EXECUTIVE CLEARANCE"
      )
      Log.d(tag, "Firebase Auth state listener: Active session persisted for UID: ${user.uid} (${user.email})")
    } else {
      clearPersistedSession()
      _currentUser.value = null
      _authState.value = AuthState.UNAUTHENTICATED
      _isExecutiveAccessGranted.value = false
      _sessionState.value = ExecutiveSessionState.Unauthenticated
      Log.d(tag, "Firebase Auth state listener: User unauthenticated / session terminated")
    }
  }

  private fun persistSession(user: TitanAuthUser) {
    try {
      prefs.edit()
        .putString("session_persisted_uid", user.uid)
        .putString("session_persisted_email", user.email)
        .putString("session_persisted_display_name", user.displayName)
        .putString("session_persisted_photo_url", user.photoUrl)
        .putLong("session_persisted_timestamp", System.currentTimeMillis())
        .putBoolean("session_executive_clearance_active", true)
        .commit()
    } catch (e: Exception) {
      Log.w(tag, "Failed to persist session to preferences: ${e.message}")
    }
  }

  private fun restorePersistedSessionMetadataIfAvailable() {
    try {
      val persistedUid = prefs.getString("session_persisted_uid", null)
      val hasExecutiveClearance = prefs.getBoolean("session_executive_clearance_active", false)
      if (!persistedUid.isNullOrBlank() && hasExecutiveClearance) {
        val email = prefs.getString("session_persisted_email", null)
        val displayName = prefs.getString("session_persisted_display_name", "Career Titan") ?: "Career Titan"
        val photoUrl = prefs.getString("session_persisted_photo_url", null)
        val timestamp = prefs.getLong("session_persisted_timestamp", System.currentTimeMillis())

        val cachedUser = TitanAuthUser(
          uid = persistedUid,
          email = email,
          displayName = displayName,
          photoUrl = photoUrl,
          isAnonymous = false,
          isEmailVerified = true,
          providerId = "google.com",
          lastSignInTimestamp = timestamp
        )
        // Set cached user for fast UI rendering while Firebase validates token in background
        _currentUser.value = cachedUser
        _authState.value = AuthState.AUTHENTICATED
        _isExecutiveAccessGranted.value = true
        _sessionState.value = ExecutiveSessionState.Authenticated(
          user = cachedUser,
          sessionStartedTimestamp = timestamp,
          isExecutiveClearanceGranted = true,
          securityLevel = "LEVEL-5 EXECUTIVE CLEARANCE (RESTORED)"
        )
        Log.d(tag, "Restored persisted session metadata for UID: $persistedUid")
      }
    } catch (e: Exception) {
      Log.w(tag, "Failed to read persisted session: ${e.message}")
    }
  }

  private fun clearPersistedSession() {
    try {
      prefs.edit().clear().commit()
    } catch (e: Exception) {
      Log.w(tag, "Failed to clear persisted session: ${e.message}")
    }
  }

  /**
   * Cleans up listener registration to prevent memory leaks during ViewModel or Activity teardown.
   */
  fun dispose() {
    try {
      firebaseAuth?.removeAuthStateListener(authStateListener)
      Log.d(tag, "AuthStateListener unregistered successfully")
    } catch (e: Exception) {
      Log.w(tag, "Error removing auth state listener: ${e.message}")
    }
  }

  /**
   * Requires active Level-5 Executive clearance for secure command center access.
   */
  fun requireExecutiveAccess(): Result<TitanAuthUser> {
    val user = _currentUser.value
    return if (user != null && _authState.value == AuthState.AUTHENTICATED && _isExecutiveAccessGranted.value) {
      Result.success(user)
    } else {
      Result.failure(SecurityException("Restricted Executive Access: Active Firebase authenticated session required."))
    }
  }

  private fun mapFirebaseUser(user: FirebaseUser): TitanAuthUser {
    return TitanAuthUser(
      uid = user.uid,
      email = user.email,
      displayName = user.displayName ?: user.email?.substringBefore("@") ?: "Career Titan",
      photoUrl = user.photoUrl?.toString(),
      isAnonymous = false,
      isEmailVerified = user.isEmailVerified,
      providerId = "google.com",
      lastSignInTimestamp = System.currentTimeMillis()
    )
  }

  private fun resolveClientId(): String? {
    return try {
      context.getString(R.string.default_web_client_id)
    } catch (e: Exception) {
      Log.w(tag, "default_web_client_id not available in resources: ${e.message}")
      null
    }
  }

  /**
   * Silent Auto-Sign-In on App Startup
   */
  fun attemptAutoSignIn() {
    val auth = firebaseAuth ?: return
    if (auth.currentUser != null) {
      _currentUser.value = mapFirebaseUser(auth.currentUser!!)
      _authState.value = AuthState.AUTHENTICATED
      return
    }

    val clientId = resolveClientId() ?: return

    val googleIdOption = GetGoogleIdOption.Builder()
      .setFilterByAuthorizedAccounts(true)
      .setServerClientId(clientId)
      .setAutoSelectEnabled(true)
      .build()

    val request = GetCredentialRequest.Builder()
      .addCredentialOption(googleIdOption)
      .build()

    scope.launch(Dispatchers.IO) {
      try {
        val result = credentialManager.getCredential(context, request)
        val credential = result.credential
        if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
          val googleIdToken = GoogleIdTokenCredential.createFrom(credential.data).idToken
          val authCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
          val authResult = auth.signInWithCredential(authCredential).awaitTask()
          val user = authResult.user
          if (user != null) {
            _currentUser.value = mapFirebaseUser(user)
            _authState.value = AuthState.AUTHENTICATED
          }
        }
      } catch (e: Exception) {
        Log.d(tag, "Silent auto-sign-in skipped or unavailable: ${e.message}")
      }
    }
  }

  /**
   * Interactive Google Sign-In using Credential Manager (GetSignInWithGoogleOption)
   */
  suspend fun signInWithGoogle(activity: Activity): Result<TitanAuthUser> =
    withContext(Dispatchers.IO) {
      _authState.value = AuthState.AUTHENTICATING
      _authError.value = null

      val clientId = resolveClientId()
      if (clientId.isNullOrBlank()) {
        val err = "Google Sign-In configuration missing: default_web_client_id not found"
        _authError.value = err
        _authState.value = AuthState.ERROR
        return@withContext Result.failure(IllegalStateException(err))
      }

      try {
        val signInOption = GetSignInWithGoogleOption.Builder(serverClientId = clientId).build()
        val request = GetCredentialRequest.Builder()
          .addCredentialOption(signInOption)
          .build()

        val result = credentialManager.getCredential(activity, request)
        val credential = result.credential

        if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
          val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
          val idToken = googleIdTokenCredential.idToken

          val auth = firebaseAuth
          if (auth != null) {
            val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
            val authResult = auth.signInWithCredential(firebaseCredential).awaitTask()
            val firebaseUser = authResult.user
            if (firebaseUser != null) {
              val titanUser = mapFirebaseUser(firebaseUser)
              _currentUser.value = titanUser
              _authState.value = AuthState.AUTHENTICATED
              return@withContext Result.success(titanUser)
            }
          }

          val fallbackUser = TitanAuthUser(
            uid = googleIdTokenCredential.id,
            email = googleIdTokenCredential.id,
            displayName = googleIdTokenCredential.displayName ?: "Google Titan",
            photoUrl = googleIdTokenCredential.profilePictureUri?.toString(),
            isAnonymous = false,
            isEmailVerified = true,
            providerId = "google.com"
          )
          _currentUser.value = fallbackUser
          _authState.value = AuthState.AUTHENTICATED
          Result.success(fallbackUser)
        } else {
          val errorMsg = "Unexpected credential type returned from Credential Manager"
          _authError.value = errorMsg
          _authState.value = AuthState.ERROR
          Result.failure(IllegalStateException(errorMsg))
        }
      } catch (e: GetCredentialCancellationException) {
        Log.w(tag, "Google Sign-In flow cancelled or dismissed: ${e.message}", e)
        _authState.value = if (_currentUser.value != null) AuthState.AUTHENTICATED else AuthState.UNAUTHENTICATED
        Result.failure(e)
      } catch (e: Exception) {
        Log.e(tag, "Google Sign-In failed: ${e.message}", e)
        _authError.value = e.localizedMessage ?: "Google Sign-In error"
        _authState.value = AuthState.ERROR
        Result.failure(e)
      }
    }

  /**
   * Sign Out
   */
  fun signOut() {
    try {
      firebaseAuth?.signOut()
      scope.launch(Dispatchers.IO) {
        try {
          credentialManager.clearCredentialState(ClearCredentialStateRequest())
        } catch (e: Exception) {
          Log.w(tag, "Failed to clear credential state: ${e.message}")
        }
      }
    } catch (e: Exception) {
      Log.w(tag, "Sign out exception: ${e.message}")
    }
    clearPersistedSession()
    _currentUser.value = null
    _authState.value = AuthState.UNAUTHENTICATED
    _sessionState.value = ExecutiveSessionState.Unauthenticated
    _isExecutiveAccessGranted.value = false
    _authError.value = null
  }

  fun clearError() {
    _authError.value = null
  }
}

/**
 * Coroutine task await helper for Google Play Services & Firebase tasks
 */
internal suspend fun <T> Task<T>.awaitTask(): T = suspendCancellableCoroutine { continuation: CancellableContinuation<T> ->
  addOnSuccessListener { result ->
    continuation.resume(result)
  }
  addOnFailureListener { exception ->
    continuation.resumeWithException(exception)
  }
  addOnCanceledListener {
    continuation.cancel()
  }
}
