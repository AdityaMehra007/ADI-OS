package com.example.auth

/**
 * Authentication user model representing signed-in user credentials
 */
data class TitanAuthUser(
  val uid: String,
  val email: String?,
  val displayName: String?,
  val photoUrl: String?,
  val isAnonymous: Boolean = false,
  val isEmailVerified: Boolean = false,
  val providerId: String = "firebase",
  val lastSignInTimestamp: Long = System.currentTimeMillis()
)

enum class AuthState {
  UNAUTHENTICATED,
  AUTHENTICATING,
  AUTHENTICATED,
  ERROR
}

enum class CloudSyncStatus(val label: String) {
  LOCAL_ONLY("Local Device Only"),
  SYNCING("Syncing to Cloud..."),
  SYNCED("Securely Synced Across Devices"),
  FAILED("Sync Error (Retry Available)")
}

data class CloudSyncSummary(
  val status: CloudSyncStatus = CloudSyncStatus.LOCAL_ONLY,
  val lastSyncedTimestamp: Long? = null,
  val profileSynced: Boolean = false,
  val factsCount: Int = 0,
  val storiesCount: Int = 0,
  val applicationsCount: Int = 0,
  val decisionsCount: Int = 0,
  val goalsCount: Int = 0,
  val lastErrorMessage: String? = null
)
