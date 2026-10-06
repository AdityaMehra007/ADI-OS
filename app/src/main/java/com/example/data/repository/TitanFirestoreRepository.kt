package com.example.data.repository

import android.content.Context
import com.example.R
import com.example.data.model.FirestoreDirectiveExecution
import com.example.data.model.FirestorePinnedDirective
import com.example.data.model.FirestoreUserProfile
import com.example.util.OperationType
import com.example.util.handleFirestoreError
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class TitanFirestoreRepository(private val db: FirebaseFirestore) {

  // Primary convenience constructor resolving named database ID from string resources
  constructor(context: Context) : this(
    FirebaseFirestore.getInstance(
      context.applicationContext.getString(R.string.firestore_database_id)
    )
  )

  private val auth = Firebase.auth

  fun requireUserId(): String {
    return auth.currentUser?.uid
      ?: throw IllegalStateException("User must be signed in with Google before accessing Firestore.")
  }

  fun observeUserProfile(): Flow<FirestoreUserProfile?> = flow {
    val uid = requireUserId()
    val path = "users/$uid"
    emitAll(
      db.collection("users").document(uid)
        .snapshots()
        .map { snapshot ->
          if (snapshot.exists()) snapshot.toObject(FirestoreUserProfile::class.java) else null
        }
        .catch { error ->
          if (error is Exception) handleFirestoreError(error, OperationType.GET, path)
          throw error
        }
    )
  }

  suspend fun saveUserProfile(
    displayName: String,
    email: String,
    tier: String = "APEX_FOUNDER",
    targetCTC: Double = 120.0,
    currentTitle: String = "Apex Executive"
  ) {
    val uid = requireUserId()
    val path = "users/$uid"
    val docRef = db.collection("users").document(uid)

    val payload = mapOf(
      "userId" to uid,
      "displayName" to displayName,
      "email" to email,
      "tier" to tier,
      "targetCTC" to targetCTC,
      "currentTitle" to currentTitle,
      "createdAt" to FieldValue.serverTimestamp(),
      "updatedAt" to FieldValue.serverTimestamp()
    )

    try {
      docRef.set(payload).await()
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.WRITE, path)
      throw e
    }
  }

  fun observeRecentExecutions(): Flow<List<FirestoreDirectiveExecution>> = flow {
    val uid = requireUserId()
    val path = "users/$uid/executions"
    emitAll(
      db.collection("users").document(uid).collection("executions")
        .snapshots()
        .map { snapshot ->
          snapshot.toObjects(FirestoreDirectiveExecution::class.java)
        }
        .catch { error ->
          if (error is Exception) handleFirestoreError(error, OperationType.LIST, path)
          throw error
        }
    )
  }

  suspend fun recordExecution(
    id: String,
    directiveCode: String,
    directiveTitle: String,
    category: String,
    latencyMs: Long,
    status: String,
    summary: String = ""
  ) {
    val uid = requireUserId()
    val path = "users/$uid/executions/$id"
    val docRef = db.collection("users").document(uid).collection("executions").document(id)

    val payload = mapOf(
      "id" to id,
      "userId" to uid,
      "directiveCode" to directiveCode,
      "directiveTitle" to directiveTitle,
      "category" to category,
      "latencyMs" to latencyMs,
      "status" to status,
      "summary" to summary,
      "executedAt" to FieldValue.serverTimestamp()
    )

    try {
      docRef.set(payload).await()
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.CREATE, path)
      throw e
    }
  }

  fun observePinnedDirectives(): Flow<List<FirestorePinnedDirective>> = flow {
    val uid = requireUserId()
    val path = "users/$uid/pinnedDirectives"
    emitAll(
      db.collection("users").document(uid).collection("pinnedDirectives")
        .snapshots()
        .map { snapshot ->
          snapshot.toObjects(FirestorePinnedDirective::class.java)
        }
        .catch { error ->
          if (error is Exception) handleFirestoreError(error, OperationType.LIST, path)
          throw error
        }
    )
  }

  suspend fun pinDirective(directiveCode: String, title: String) {
    val uid = requireUserId()
    val path = "users/$uid/pinnedDirectives/$directiveCode"
    val docRef = db.collection("users").document(uid).collection("pinnedDirectives").document(directiveCode)

    val payload = mapOf(
      "userId" to uid,
      "directiveCode" to directiveCode,
      "title" to title,
      "pinnedAt" to FieldValue.serverTimestamp()
    )

    try {
      docRef.set(payload).await()
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.WRITE, path)
      throw e
    }
  }

  suspend fun unpinDirective(directiveCode: String) {
    val uid = requireUserId()
    val path = "users/$uid/pinnedDirectives/$directiveCode"
    val docRef = db.collection("users").document(uid).collection("pinnedDirectives").document(directiveCode)

    try {
      docRef.delete().await()
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.DELETE, path)
      throw e
    }
  }
}
