package com.example.truth

/**
 * ADI-OS TRUTH ENGINE v3: Truth Classification System
 *
 * Status taxonomy:
 * 🟢 E1 = Documentary Verified (academic transcripts, signed letters, government records)
 * 🟢 E2 = Confirmed (third-party platform confirmation, event rosters, verifiable public links)
 * 🟡 U1 = User Claimed (reported by user, plausible, but lacks formal documentary proof)
 * 🟠 T  = Temporary (provisional, active sprint, unconfirmed draft)
 * 🔵 G  = Goal (target outcome, future ambition, NEVER to be presented as current reality)
 * 🔴 F  = Forbidden (explicitly banned fabricated claims, non-existent achievements, fake metrics)
 */
enum class TruthStatus(
  val code: String,
  val displayName: String,
  val emoji: String,
  val isPermittedInExternalOutput: Boolean
) {
  E1("E1", "Documentary Verified", "🟢", true),
  E2("E2", "Confirmed", "🟢", true),
  U1("U1", "User Claimed", "🟡", false),
  T("T", "Temporary Context", "🟠", false),
  G("G", "Future Goal", "🔵", false),
  F("F", "Forbidden / Fabricated", "🔴", false);

  companion object {
    fun fromCode(code: String): TruthStatus {
      return values().firstOrNull { it.code.equals(code, ignoreCase = true) } ?: U1
    }
  }
}

/**
 * Verifiable evidence record stored in the Evidence Vault.
 */
data class EvidenceRecord(
  val evidenceId: String,
  val title: String,
  val sourceType: String, // ACADEMIC_RECORD, EVENT_ROSTER, CLIENT_LETTER, FAMILY_COMMERCIAL_LEDGER, CODE_REPOSITORY
  val sourceReference: String,
  val documentUri: String = "",
  val verificationMethod: String, // DOCUMENT_INSPECTION, ATTESTATION, PUBLIC_REGISTRY
  val verifiedTimestamp: Long,
  val verifierNote: String,
  val confidenceScore: Float = 1.0f
)

/**
 * Standard unit of claim tracked by ADI-OS Truth Engine v4.
 * Fulfills Section 5 of the specification.
 */
data class ClaimObject(
  val claimId: String,
  val claimText: String,
  val claimType: String, // IDENTITY, EDUCATION, EXPERIENCE, SKILL, METRIC, TITLE, COMPENSATION, CERTIFICATION
  val subject: String,
  val value: String,
  val status: TruthStatus,
  val sourceType: String,
  val sourceReference: String,
  val evidenceReference: String,
  val confidence: Float = 1.0f,
  val createdAt: Long = System.currentTimeMillis(),
  val updatedAt: Long = System.currentTimeMillis(),
  val lastVerifiedAt: Long = System.currentTimeMillis(),
  val expiryAt: Long? = null,
  val userConfirmed: Boolean = true,
  val externalUseAllowed: Boolean = (status == TruthStatus.E1 || status == TruthStatus.E2),
  val notes: String = ""
)

/**
 * Experience verification levels (Section 8).
 * Upgrades between levels strictly require documentary proof.
 */
enum class ExperienceLevel(val rank: Int) {
  EXPOSURE(1),
  PARTICIPATION(2),
  EXECUTION(3),
  RESPONSIBILITY(4),
  SUPERVISION(5),
  LEADERSHIP(6),
  MANAGEMENT(7),
  OWNERSHIP(8),
  EXPERTISE(9);

  fun canUpgradeTo(target: ExperienceLevel, hasDocumentaryProof: Boolean): Boolean {
    if (target.rank <= this.rank) return true
    return hasDocumentaryProof
  }
}

/**
 * Metric verification structure (Section 9).
 * Every number is high-risk. Missing components trigger UNVERIFIED status.
 */
data class MetricVerification(
  val metricName: String,
  val definition: String,
  val value: Double,
  val unit: String,
  val timePeriod: String,
  val source: String,
  val evidenceId: String,
  val calculationMethod: String
) {
  val isCompleteAndVerified: Boolean
    get() = metricName.isNotBlank() &&
        definition.isNotBlank() &&
        unit.isNotBlank() &&
        timePeriod.isNotBlank() &&
        source.isNotBlank() &&
        evidenceId.isNotBlank() &&
        calculationMethod.isNotBlank()
}

/**
 * Skill mastery levels (Section 11).
 * Promotion requires explicit evidence.
 */
enum class SkillLevel(val rank: Int) {
  AWARENESS(1),
  LEARNING(2),
  PRACTICAL(3),
  ADVANCED(4),
  PROFESSIONAL(5),
  EXPERT(6);

  fun canPromoteTo(target: SkillLevel, hasEvidence: Boolean): Boolean {
    if (target.rank <= this.rank) return true
    return hasEvidence
  }
}

/**
 * Standard unit of truth tracked by ADI-OS Truth Engine v3 & v4.
 */
data class TruthFact(
  val factId: String,
  val category: String, // EDUCATION, EXPERIENCE, SKILL, METRIC, IDENTITY, PROJECT
  val claim: String,
  val status: TruthStatus,
  val sourceType: String,
  val sourceReference: String,
  val evidenceReference: String,
  val createdAt: Long = System.currentTimeMillis(),
  val updatedAt: Long = System.currentTimeMillis(),
  val lastVerifiedAt: Long = System.currentTimeMillis(),
  val confidence: Float = 1.0f,
  val expiryAt: Long? = null,
  val userConfirmed: Boolean = true,
  val notes: String = ""
)

/**
 * Provenance tracking for claims generated or transformed by AI / systems.
 */
data class ClaimProvenance(
  val claimId: String,
  val claimText: String,
  val sourceFactIds: List<String>,
  val evidenceIds: List<String>,
  val generationTime: Long = System.currentTimeMillis(),
  val modelOrSource: String,
  val confidence: Float,
  val approved: Boolean = false,
  val usedExternally: Boolean = false
)

/**
 * Contradiction conflict record generated when an incoming claim contradicts verified truth.
 */
data class ContradictionConflict(
  val conflictId: String,
  val conflictType: String, // INSTITUTION_MISMATCH, NAME_MISMATCH, GPA_MISMATCH, METRIC_INFLATION, TITLE_INFLATION
  val existingFactId: String,
  val existingClaim: String,
  val incomingClaim: String,
  val conflictReason: String,
  val blockedOverwrite: Boolean = true,
  val userResolutionRequired: Boolean = true,
  val timestamp: Long = System.currentTimeMillis()
)

/**
 * Immutable change log entry recording any update to truth or identity facts.
 */
data class AuditChangeLogEntry(
  val changeId: String,
  val factId: String,
  val fieldName: String,
  val oldValue: String,
  val newValue: String,
  val reason: String,
  val source: String,
  val confirmedBy: String,
  val timestamp: Long = System.currentTimeMillis(),
  val affectedSystems: List<String> = emptyList()
)

/**
 * Audit result returned by the External Output Firewall.
 */
data class FirewallAuditResult(
  val isApproved: Boolean,
  val sanitizedOutput: String,
  val violations: List<FirewallViolation>,
  val verifiedFactIdsUsed: List<String>,
  val pipelineStagesCompleted: List<String>,
  val riskLevel: String // LOW, MEDIUM, HIGH, CRITICAL_BLOCKED
)

/**
 * Specific violation detected by the External Output Firewall.
 */
data class FirewallViolation(
  val ruleName: String,
  val violatedTextSnippet: String,
  val reason: String,
  val severity: String, // BLOCKING, WARNING
  val recommendedCorrection: String
)

/**
 * Agent permission scopes.
 */
enum class AgentPermission(val level: Int, val description: String) {
  READ(1, "Read profile, database records, and market data"),
  WRITE(2, "Mutate internal drafting state and task records"),
  EXECUTE(3, "Trigger local calculation engines, parsers, and internal queries"),
  EXTERNAL_ACTION(4, "Generate or publish external-facing artifacts (requires firewall passing)")
}
