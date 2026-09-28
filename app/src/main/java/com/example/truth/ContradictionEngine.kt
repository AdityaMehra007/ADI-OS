package com.example.truth

/**
 * ADI-OS TRUTH ENGINE v3: Contradiction Engine
 *
 * Implements the automated detection and blocking of conflicting data updates:
 * INCOMING CLAIM → NORMALIZE → COMPARE WITH CANONICAL TRUTH → CONFLICT?
 *   - NO  → APPROVE STORE
 *   - YES → BLOCK OVERWRITE → LOG CONTRADICTION → REQUIRE USER RESOLUTION
 */
object ContradictionEngine {

  private val detectedConflicts = mutableListOf<ContradictionConflict>()

  fun getAllConflicts(): List<ContradictionConflict> = detectedConflicts.toList()

  fun clearConflictsForTesting() {
    detectedConflicts.clear()
  }

  /**
   * Evaluates an incoming claim against canonical facts and evidence.
   * Returns null if no contradiction, or ContradictionConflict if a violation is detected.
   */
  fun evaluateIncomingClaim(
    incomingClaim: String,
    category: String = "GENERAL"
  ): ContradictionConflict? {
    val normalized = normalizeClaim(incomingClaim)

    // 0. Check for Prompt Injection / Adversarial Override Attempts (Section 14)
    val adversarialMatches = EvidenceVault.detectAdversarialInjection(normalized)
    if (adversarialMatches.isNotEmpty()) {
      val conflict = ContradictionConflict(
        conflictId = "CONFLICT_ADVERSARIAL_${System.currentTimeMillis()}",
        conflictType = "PROMPT_INJECTION_ATTEMPT",
        existingFactId = "SYSTEM_INTEGRITY_POLICY",
        existingClaim = "Prompt injection and Truth Engine overrides are unconditionally blocked.",
        incomingClaim = incomingClaim,
        conflictReason = "Adversarial prompt injection detected: ${adversarialMatches.joinToString()}",
        blockedOverwrite = true,
        userResolutionRequired = true
      )
      detectedConflicts.add(conflict)
      return conflict
    }

    // 1. Check for University / Institution Mismatch
    if (normalized.contains("bengaluru university") ||
        normalized.contains("bangalore university") ||
        normalized.contains("institution = bengaluru") ||
        normalized.contains("university = bengaluru") ||
        normalized.contains("institution: bengaluru") ||
        normalized.contains("university: bengaluru") ||
        normalized.contains("bengaluru univ") ||
        normalized.contains("bangalore univ")
    ) {
      val conflict = ContradictionConflict(
        conflictId = "CONFLICT_UNIV_${System.currentTimeMillis()}",
        conflictType = "INSTITUTION_MISMATCH",
        existingFactId = "FACT_EDUCATION_01",
        existingClaim = "Dayananda Sagar University (BBA International Business, 2023–2026)",
        incomingClaim = incomingClaim,
        conflictReason = "Institution contradicts verified academic record (Dayananda Sagar University). Overwrite blocked.",
        blockedOverwrite = true,
        userResolutionRequired = true
      )
      detectedConflicts.add(conflict)
      return conflict
    }

    // 2. Check for Surname / Identity Mismatch
    if (normalized.contains("shenoy") || normalized.contains("aditya shenoy")) {
      val conflict = ContradictionConflict(
        conflictId = "CONFLICT_NAME_${System.currentTimeMillis()}",
        conflictType = "NAME_MISMATCH",
        existingFactId = "FACT_IDENTITY_01",
        existingClaim = "Candidate's legal and professional name is Aditya Mehra.",
        incomingClaim = incomingClaim,
        conflictReason = "Surname contradicts canonical identity (Aditya Mehra). Overwrite blocked.",
        blockedOverwrite = true,
        userResolutionRequired = true
      )
      detectedConflicts.add(conflict)
      return conflict
    }

    // 3. Check for GPA / Academic Honors Inflation
    if (normalized.contains("3.82") ||
        normalized.contains("3.85") ||
        normalized.contains("first class honors") ||
        normalized.contains("three point eight two")
    ) {
      val conflict = ContradictionConflict(
        conflictId = "CONFLICT_GPA_${System.currentTimeMillis()}",
        conflictType = "GPA_MISMATCH",
        existingFactId = "FACT_EDUCATION_01",
        existingClaim = "41/41 subjects passed with zero backlogs, ~6.33 CGPA on 10-point scale.",
        incomingClaim = incomingClaim,
        conflictReason = "Academic score contradicts official grade transcripts (~6.33 CGPA, 10-point scale). Overwrite blocked.",
        blockedOverwrite = true,
        userResolutionRequired = true
      )
      detectedConflicts.add(conflict)
      return conflict
    }

    // 4. Check for Fabricated Metrics
    val isVerifiedTelemetryFact = (normalized.contains("14 fulfillment") || normalized.contains("16.2")) &&
        (normalized.contains("42%") || normalized.contains("forty-two percent"))

    if (!isVerifiedTelemetryFact && (
        normalized.contains("42%") ||
        normalized.contains("42 %") ||
        normalized.contains("forty-two percent") ||
        normalized.contains("forty two percent") ||
        normalized.contains("18.5l") ||
        normalized.contains("18.5 lakh") ||
        normalized.contains("24l working capital") ||
        normalized.contains("safeguarded ₹24l") ||
        normalized.contains("38% latency")
    )) {
      val conflict = ContradictionConflict(
        conflictId = "CONFLICT_METRIC_${System.currentTimeMillis()}",
        conflictType = "METRIC_INFLATION",
        existingFactId = "FACT_EXPERIENCE_01",
        existingClaim = "Verified supply chain telemetry and 300+ on-ground operational deployments.",
        incomingClaim = incomingClaim,
        conflictReason = "Fabricated percentage/monetary metric lacks documentary evidence. Overwrite blocked.",
        blockedOverwrite = true,
        userResolutionRequired = true
      )
      detectedConflicts.add(conflict)
      return conflict
    }

    // 5. Check for Extreme Title Inflation
    val inflatedTitles = listOf("vice president", "chief of staff", "director of operations", "principal strategist", "executive director")
    for (inflated in inflatedTitles) {
      if (normalized.contains(inflated)) {
        val conflict = ContradictionConflict(
          conflictId = "CONFLICT_TITLE_${System.currentTimeMillis()}",
          conflictType = "TITLE_INFLATION",
          existingFactId = "FACT_IDENTITY_01",
          existingClaim = "Early-Career / Graduate level (Operations, Business Analysis, Strategy).",
          incomingClaim = incomingClaim,
          conflictReason = "Title '$inflated' represents executive inflation without supporting documentary evidence. Overwrite blocked.",
          blockedOverwrite = true,
          userResolutionRequired = true
        )
        detectedConflicts.add(conflict)
        return conflict
      }
    }

    // 6. Check for Fabricated Employer Claims
    val fabricatedEmployers = listOf("worked at microsoft", "employed by google", "software engineer at amazon")
    for (fe in fabricatedEmployers) {
      if (normalized.contains(fe)) {
        val conflict = ContradictionConflict(
          conflictId = "CONFLICT_EMPLOYER_${System.currentTimeMillis()}",
          conflictType = "FABRICATED_EMPLOYER",
          existingFactId = "FACT_EXPERIENCE_01",
          existingClaim = "100+ brand activations and family enterprise commercial operations.",
          incomingClaim = incomingClaim,
          conflictReason = "Claim of past full-time employment at '$fe' has zero documentary evidence. Overwrite blocked.",
          blockedOverwrite = true,
          userResolutionRequired = true
        )
        detectedConflicts.add(conflict)
        return conflict
      }
    }

    // 7. Check for Other Forbidden Tokens
    val forbiddenMatches = EvidenceVault.detectForbiddenTokens(normalized)
    if (forbiddenMatches.isNotEmpty()) {
      val conflict = ContradictionConflict(
        conflictId = "CONFLICT_${System.currentTimeMillis()}",
        conflictType = "FORBIDDEN_DATA_VIOLATION",
        existingFactId = "CANONICAL_RULES",
        existingClaim = "Zero tolerance for fabricated claims or unverified credentials.",
        incomingClaim = incomingClaim,
        conflictReason = "Incoming claim contains forbidden fabricated tokens: ${forbiddenMatches.joinToString()}",
        blockedOverwrite = true,
        userResolutionRequired = true
      )
      detectedConflicts.add(conflict)
      return conflict
    }

    return null
  }

  /**
   * Evaluates a structured ClaimObject against canonical evidence.
   */
  fun evaluateClaimObject(claim: ClaimObject): ContradictionConflict? {
    return evaluateIncomingClaim(claim.claimText, claim.claimType)
  }

  fun normalizeClaim(claim: String): String {
    return EvidenceVault.normalizeSemanticVariant(claim)
  }
}
