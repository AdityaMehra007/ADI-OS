package com.example.truth

import java.util.UUID

/**
 * ADI-OS TRUTH ENGINE v3: Immutable Change Log
 *
 * Implements the immutable audit trail for fact mutations and system corrections.
 * Never silently overwrites important identity or career data.
 */
object ImmutableChangeLog {

  private val changeEntries = mutableListOf<AuditChangeLogEntry>()

  fun getChangeLog(): List<AuditChangeLogEntry> = changeEntries.toList()

  fun recordChange(
    factId: String,
    fieldName: String,
    oldValue: String,
    newValue: String,
    reason: String,
    source: String = "TRUTH_ENGINE_V3_AUDIT",
    confirmedBy: String = "ADI_SECURITY_ENGINE",
    affectedSystems: List<String> = listOf("UserProfile", "VerifiedFacts", "ResumeGenerator")
  ): AuditChangeLogEntry {
    val entry = AuditChangeLogEntry(
      changeId = "CHG_${UUID.randomUUID().toString().take(8)}",
      factId = factId,
      fieldName = fieldName,
      oldValue = oldValue,
      newValue = newValue,
      reason = reason,
      source = source,
      confirmedBy = confirmedBy,
      timestamp = System.currentTimeMillis(),
      affectedSystems = affectedSystems
    )
    changeEntries.add(entry)
    return entry
  }

  fun clearForTesting() {
    changeEntries.clear()
  }
}
