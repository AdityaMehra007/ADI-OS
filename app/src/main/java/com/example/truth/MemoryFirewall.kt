package com.example.truth

import java.util.UUID

/**
 * ADI-OS TRUTH ENGINE v4: Memory Firewall
 *
 * Implements the mandatory validation pipeline:
 * AI OUTPUT → CLAIM EXTRACTION → EVIDENCE CHECK → STATUS ASSIGNMENT → USER CONFIRMATION → CANONICAL MEMORY
 *
 * AI-generated text is NEVER allowed to bypass validation into canonical memory.
 */
object MemoryFirewall {

  data class IngestionResult(
    val isPromotedToCanonical: Boolean,
    val extractedClaims: List<ClaimObject>,
    val blockedReasons: List<String>,
    val requiresUserConfirmation: Boolean
  )

  /**
   * Processes raw AI output before allowing any persistence into canonical memory/profile.
   */
  fun processAiOutputForMemory(
    aiOutput: String,
    targetSubject: String = "Aditya Mehra",
    userConfirmed: Boolean = false
  ): IngestionResult {
    val blockedReasons = mutableListOf<String>()

    // Step 1: Adversarial & Injection Check
    val injections = EvidenceVault.detectAdversarialInjection(aiOutput)
    if (injections.isNotEmpty()) {
      blockedReasons.add("Adversarial injection detected in AI output: ${injections.joinToString()}")
      return IngestionResult(
        isPromotedToCanonical = false,
        extractedClaims = emptyList(),
        blockedReasons = blockedReasons,
        requiresUserConfirmation = false
      )
    }

    // Step 2: Forbidden Data Check
    val forbidden = EvidenceVault.detectForbiddenTokens(aiOutput)
    if (forbidden.isNotEmpty()) {
      blockedReasons.add("Forbidden/fabricated tokens found in AI output: ${forbidden.joinToString()}")
      return IngestionResult(
        isPromotedToCanonical = false,
        extractedClaims = emptyList(),
        blockedReasons = blockedReasons,
        requiresUserConfirmation = false
      )
    }

    // Step 3: Contradiction Check
    val contradiction = ContradictionEngine.evaluateIncomingClaim(aiOutput)
    if (contradiction != null) {
      blockedReasons.add("Contradiction detected: ${contradiction.conflictReason}")
      return IngestionResult(
        isPromotedToCanonical = false,
        extractedClaims = emptyList(),
        blockedReasons = blockedReasons,
        requiresUserConfirmation = false
      )
    }

    // Step 4: Claim Extraction & Evidence Matching
    val extracted = extractClaimsFromText(aiOutput, targetSubject)

    // Step 5: Status Assignment
    val evaluatedClaims = extracted.map { claim ->
      val evidence = EvidenceVault.CANONICAL_FACTS.firstOrNull { fact ->
        EvidenceVault.isClaimSupported(claim.claimText)
      }

      val assignedStatus = when {
        evidence != null -> TruthStatus.E2
        userConfirmed -> TruthStatus.U1
        else -> TruthStatus.T
      }

      claim.copy(
        status = assignedStatus,
        evidenceReference = evidence?.evidenceReference ?: "UNVERIFIED",
        userConfirmed = userConfirmed,
        externalUseAllowed = (assignedStatus == TruthStatus.E1 || assignedStatus == TruthStatus.E2)
      )
    }

    // AI output can only become canonical if ALL extracted claims are supported by evidence (E1/E2) AND confirmed by user
    val allSupported = evaluatedClaims.all { it.status == TruthStatus.E1 || it.status == TruthStatus.E2 }
    val canPromote = allSupported && userConfirmed

    if (!allSupported) {
      blockedReasons.add("One or more claims lack supporting evidence in Evidence Vault.")
    }
    if (!userConfirmed) {
      blockedReasons.add("User confirmation required before promotion to canonical memory.")
    }

    if (canPromote) {
      evaluatedClaims.forEach { claim ->
        ImmutableChangeLog.recordChange(
          factId = claim.claimId,
          fieldName = claim.claimType,
          oldValue = "PROVISIONAL",
          newValue = claim.value,
          reason = "Validated through MemoryFirewall with user confirmation",
          source = "AI_OUTPUT_PIPELINE"
        )
      }
    }

    return IngestionResult(
      isPromotedToCanonical = canPromote,
      extractedClaims = evaluatedClaims,
      blockedReasons = blockedReasons,
      requiresUserConfirmation = !userConfirmed && allSupported
    )
  }

  private fun extractClaimsFromText(text: String, subject: String): List<ClaimObject> {
    val sentences = text.split(Regex("[.!?\n]+")).map { it.trim() }.filter { it.length > 10 }
    return sentences.mapIndexed { index, sentence ->
      val type = when {
        sentence.contains("degree", true) || sentence.contains("bba", true) || sentence.contains("university", true) -> "EDUCATION"
        sentence.contains("event", true) || sentence.contains("activation", true) || sentence.contains("business", true) -> "EXPERIENCE"
        sentence.contains("sql", true) || sentence.contains("python", true) || sentence.contains("excel", true) -> "SKILL"
        sentence.contains("offer", true) || sentence.contains("ctc", true) || sentence.contains("lpa", true) -> "COMPENSATION"
        else -> "GENERAL"
      }
      ClaimObject(
        claimId = "CLAIM_AI_${System.currentTimeMillis()}_$index",
        claimText = sentence,
        claimType = type,
        subject = subject,
        value = sentence,
        status = TruthStatus.T,
        sourceType = "AI_GENERATED",
        sourceReference = "MemoryFirewall Extraction",
        evidenceReference = "",
        confidence = 0.5f,
        userConfirmed = false
      )
    }
  }
}
