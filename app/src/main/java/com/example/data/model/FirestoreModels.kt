package com.example.data.model

import com.google.firebase.Timestamp

data class FirestoreUserProfile(
  val userId: String = "",
  val displayName: String = "",
  val email: String = "",
  val tier: String = "APEX_FOUNDER",
  val targetCTC: Double = 120.0,
  val currentTitle: String = "Apex Executive",
  val createdAt: Timestamp? = null,
  val updatedAt: Timestamp? = null
)

data class FirestoreDirectiveExecution(
  val id: String = "",
  val userId: String = "",
  val directiveCode: String = "",
  val directiveTitle: String = "",
  val category: String = "EXECUTIVE",
  val latencyMs: Long = 14L,
  val status: String = "DISPATCHED",
  val summary: String = "",
  val executedAt: Timestamp? = null
)

data class FirestorePinnedDirective(
  val userId: String = "",
  val directiveCode: String = "",
  val title: String = "",
  val pinnedAt: Timestamp? = null
)
