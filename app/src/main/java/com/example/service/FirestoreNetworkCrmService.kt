package com.example.service

import android.content.Context
import android.util.Log
import com.example.data.model.ContactInteraction
import com.example.data.model.FollowUpReminder
import com.example.data.model.RecruiterContact
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

data class CrmContactBundle(
  val contact: RecruiterContact,
  val interactions: List<ContactInteraction> = emptyList(),
  val reminders: List<FollowUpReminder> = emptyList()
)

/**
 * Service to manage and persist Professional Network CRM contacts,
 * past interactions, and follow-up reminders in Firebase Firestore
 * under users/{userId}/network_contacts/{contactId} with offline caching.
 */
class FirestoreNetworkCrmService(private val context: Context) {
  private val tag = "FirestoreNetworkCrm"
  private val prefs = context.getSharedPreferences("titan_network_crm_vault", Context.MODE_PRIVATE)
  private val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
  private val bundleListType = Types.newParameterizedType(List::class.java, CrmContactBundle::class.java)
  private val bundleAdapter = moshi.adapter<List<CrmContactBundle>>(bundleListType)
  private val remindersListType = Types.newParameterizedType(List::class.java, FollowUpReminder::class.java)
  private val remindersAdapter = moshi.adapter<List<FollowUpReminder>>(remindersListType)

  private val _syncState = MutableStateFlow(FirestoreSyncState.IDLE)
  val syncState: StateFlow<FirestoreSyncState> = _syncState.asStateFlow()

  private val _statusMessage = MutableStateFlow("CRM Database Ready")
  val statusMessage: StateFlow<String> = _statusMessage.asStateFlow()

  suspend fun saveContactToFirestore(
    userId: String,
    contact: RecruiterContact,
    interactions: List<ContactInteraction> = emptyList(),
    reminders: List<FollowUpReminder> = emptyList()
  ): Result<Boolean> = withContext(Dispatchers.IO) {
    _syncState.value = FirestoreSyncState.SYNCING
    _statusMessage.value = "Syncing '${contact.name}' & interactions to Firestore..."

    var firestoreSuccess = false

    try {
      val firestore = FirebaseFirestore.getInstance()
      val docRef = firestore.collection("users")
        .document(userId)
        .collection("network_contacts")
        .document(contact.id)

      val data = hashMapOf(
        "id" to contact.id,
        "name" to contact.name,
        "title" to contact.title,
        "companyId" to contact.companyId,
        "companyName" to contact.companyName,
        "department" to contact.department,
        "email" to contact.email,
        "linkedinUrl" to contact.linkedinUrl,
        "relationshipState" to contact.relationshipState,
        "lastInteractionDate" to contact.lastInteractionDate,
        "notes" to contact.notes,
        "nextFollowUpDate" to contact.nextFollowUpDate,
        "lastSyncedAt" to System.currentTimeMillis(),
        "interactionsCount" to interactions.size,
        "remindersCount" to reminders.size,
        "interactions" to interactions.map {
          hashMapOf(
            "id" to it.id,
            "contactId" to it.contactId,
            "contactName" to it.contactName,
            "interactionType" to it.interactionType,
            "dateFormatted" to it.dateFormatted,
            "summaryNotes" to it.summaryNotes,
            "nextSteps" to it.nextSteps
          )
        },
        "reminders" to reminders.map {
          hashMapOf(
            "id" to it.id,
            "contactId" to it.contactId,
            "contactName" to it.contactName,
            "companyName" to it.companyName,
            "dueDateFormatted" to it.dueDateFormatted,
            "reminderSubject" to it.reminderSubject,
            "isCompleted" to it.isCompleted,
            "priorityLevel" to it.priorityLevel
          )
        }
      )

      docRef.set(data, SetOptions.merge()).await()
      firestoreSuccess = true
      Log.d(tag, "Contact '${contact.name}' saved to Firestore users/$userId/network_contacts/${contact.id}")
    } catch (e: Exception) {
      Log.w(tag, "Firestore contact write failed: ${e.message}. Using local cache.")
    }

    // Save locally
    saveToLocalCache(contact, interactions, reminders)

    _syncState.value = if (firestoreSuccess) FirestoreSyncState.SYNCED else FirestoreSyncState.IDLE
    _statusMessage.value = if (firestoreSuccess) {
      "Saved to Firestore: users/$userId/network_contacts/${contact.id}"
    } else {
      "Saved to local vault (will sync to Firestore when online)"
    }

    Result.success(true)
  }

  suspend fun loadContactsFromFirestore(userId: String): Result<List<CrmContactBundle>> = withContext(Dispatchers.IO) {
    _syncState.value = FirestoreSyncState.SYNCING
    _statusMessage.value = "Fetching CRM contacts from Firestore..."

    try {
      val firestore = FirebaseFirestore.getInstance()
      val snapshot = firestore.collection("users")
        .document(userId)
        .collection("network_contacts")
        .get()
        .await()

      if (!snapshot.isEmpty) {
        val bundles = snapshot.documents.mapNotNull { doc ->
          val id = doc.getString("id") ?: doc.id
          val name = doc.getString("name") ?: "Contact"
          val title = doc.getString("title") ?: "Talent Lead"
          val companyId = doc.getString("companyId") ?: "company_unknown"
          val companyName = doc.getString("companyName") ?: "Target Tech"
          val department = doc.getString("department") ?: "Talent Acquisition"
          val email = doc.getString("email") ?: ""
          val linkedinUrl = doc.getString("linkedinUrl") ?: ""
          val relationshipState = doc.getString("relationshipState") ?: "NEW"
          val lastInteractionDate = doc.getString("lastInteractionDate") ?: "None"
          val notes = doc.getString("notes") ?: ""
          val nextFollowUpDate = doc.getString("nextFollowUpDate") ?: ""

          val contact = RecruiterContact(
            id = id,
            companyId = companyId,
            companyName = companyName,
            name = name,
            title = title,
            department = department,
            linkedinUrl = linkedinUrl,
            email = email,
            relationshipState = relationshipState,
            lastInteractionDate = lastInteractionDate,
            notes = notes,
            nextFollowUpDate = nextFollowUpDate
          )

          @Suppress("UNCHECKED_CAST")
          val rawInteractions = doc.get("interactions") as? List<Map<String, Any>> ?: emptyList()
          val interactions = rawInteractions.map { map ->
            ContactInteraction(
              id = map["id"] as? String ?: "interaction_${System.currentTimeMillis()}",
              contactId = map["contactId"] as? String ?: id,
              contactName = map["contactName"] as? String ?: name,
              interactionType = map["interactionType"] as? String ?: "Note",
              dateFormatted = map["dateFormatted"] as? String ?: "Recently",
              summaryNotes = map["summaryNotes"] as? String ?: "",
              nextSteps = map["nextSteps"] as? String ?: ""
            )
          }

          @Suppress("UNCHECKED_CAST")
          val rawReminders = doc.get("reminders") as? List<Map<String, Any>> ?: emptyList()
          val reminders = rawReminders.map { map ->
            FollowUpReminder(
              id = map["id"] as? String ?: "rem_${System.currentTimeMillis()}",
              contactId = map["contactId"] as? String ?: id,
              contactName = map["contactName"] as? String ?: name,
              companyName = map["companyName"] as? String ?: companyName,
              dueDateFormatted = map["dueDateFormatted"] as? String ?: "Upcoming",
              reminderSubject = map["reminderSubject"] as? String ?: "Follow up",
              isCompleted = map["isCompleted"] as? Boolean ?: false,
              priorityLevel = map["priorityLevel"] as? String ?: "HIGH"
            )
          }

          CrmContactBundle(contact, interactions, reminders)
        }

        // Cache loaded bundles
        saveAllToLocalCache(bundles)

        _syncState.value = FirestoreSyncState.SYNCED
        _statusMessage.value = "Synced ${bundles.size} contacts from Firestore"
        return@withContext Result.success(bundles)
      }
    } catch (e: Exception) {
      Log.w(tag, "Failed to load CRM contacts from Firestore: ${e.message}")
    }

    // Fallback to local cache
    val cached = loadAllFromLocalCache()
    _syncState.value = if (cached.isNotEmpty()) FirestoreSyncState.SYNCED else FirestoreSyncState.IDLE
    _statusMessage.value = if (cached.isNotEmpty()) "Loaded ${cached.size} contacts from local cache" else "Ready to add CRM contacts"
    Result.success(cached)
  }

  private fun saveToLocalCache(
    contact: RecruiterContact,
    interactions: List<ContactInteraction>,
    reminders: List<FollowUpReminder>
  ) {
    try {
      val existing = loadAllFromLocalCache().toMutableList()
      val index = existing.indexOfFirst { it.contact.id == contact.id }
      val newBundle = CrmContactBundle(contact, interactions, reminders)
      if (index >= 0) {
        existing[index] = newBundle
      } else {
        existing.add(0, newBundle)
      }
      saveAllToLocalCache(existing)
    } catch (e: Exception) {
      Log.e(tag, "Failed to save bundle locally: ${e.message}")
    }
  }

  private fun saveAllToLocalCache(bundles: List<CrmContactBundle>) {
    try {
      val json = bundleAdapter.toJson(bundles)
      prefs.edit().putString("cached_crm_bundles", json).apply()
    } catch (e: Exception) {
      Log.e(tag, "Failed to cache CRM bundles: ${e.message}")
    }
  }

  fun loadAllFromLocalCache(): List<CrmContactBundle> {
    val json = prefs.getString("cached_crm_bundles", null) ?: return emptyList()
    return try {
      bundleAdapter.fromJson(json) ?: emptyList()
    } catch (e: Exception) {
      Log.e(tag, "Failed to parse cached CRM bundles: ${e.message}")
      emptyList()
    }
  }
}
