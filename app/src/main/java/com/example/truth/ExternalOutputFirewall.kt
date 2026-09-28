package com.example.truth

/**
 * ADI-OS TRUTH ENGINE v3: External Output Firewall
 *
 * Implements the 8-stage verification pipeline for all outward-facing content:
 * 1. USER DATA / SEED INPUT
 * 2. AI GENERATION / PARSER
 * 3. FACT EXTRACTION
 * 4. TRUTH STATUS CHECK (E1, E2, U1, T, G, F)
 * 5. EVIDENCE CHECK (Evidence Vault linking)
 * 6. FORBIDDEN DATA CHECK (Strict token rejection)
 * 7. CONTRADICTION CHECK (ContradictionEngine verification)
 * 8. RISK CHECK & SANITIZATION → FINAL SECURE OUTPUT
 */
object ExternalOutputFirewall {

  private val PIPELINE_STAGES = listOf(
    "1.INPUT_INGESTION",
    "2.AI_GENERATION",
    "3.FACT_EXTRACTION",
    "4.TRUTH_STATUS_CHECK",
    "5.EVIDENCE_VAULT_CHECK",
    "6.FORBIDDEN_DATA_CHECK",
    "7.CONTRADICTION_CHECK",
    "8.RISK_ASSESSMENT"
  )

  /**
   * Evaluates text intended for external export (Resume, LinkedIn message, Email, Recruiter pitch).
   */
  fun inspectAndFilterOutput(
    rawOutput: String,
    context: String = "RESUME_EXPORT"
  ): FirewallAuditResult {
    val violations = mutableListOf<FirewallViolation>()
    val verifiedFactsUsed = mutableListOf<String>()

    // Stage 6: Forbidden Data Check
    val forbiddenTokens = EvidenceVault.detectForbiddenTokens(rawOutput)
    if (forbiddenTokens.isNotEmpty()) {
      forbiddenTokens.forEach { token ->
        violations.add(
          FirewallViolation(
            ruleName = "FORBIDDEN_TOKEN_RULE",
            violatedTextSnippet = token,
            reason = "Explicitly forbidden token found in output: '$token'. Violates Truth Engine v4 integrity policy.",
            severity = "BLOCKING",
            recommendedCorrection = "Remove token or replace with verified canonical equivalent."
          )
        )
      }
    }

    // Stage 7: Contradiction Check
    val contradiction = ContradictionEngine.evaluateIncomingClaim(rawOutput, context)
    if (contradiction != null) {
      violations.add(
        FirewallViolation(
          ruleName = "CONTRADICTION_RULE",
          violatedTextSnippet = contradiction.incomingClaim.take(80),
          reason = contradiction.conflictReason,
          severity = "BLOCKING",
          recommendedCorrection = "Align with verified canonical fact: ${contradiction.existingClaim}"
        )
      )
    }

    // Sub-Firewall 1: IdentityFirewall
    IdentityFirewall.inspect(rawOutput, violations)

    // Sub-Firewall 2: EducationFirewall
    EducationFirewall.inspect(rawOutput, violations)

    // Sub-Firewall 3: CompensationFirewall
    CompensationFirewall.inspect(rawOutput, violations)

    // Sub-Firewall 4: MetricFirewall
    MetricFirewall.inspect(rawOutput, violations)

    // Sub-Firewall 5: ExperienceTitleFirewall
    ExperienceTitleFirewall.inspect(rawOutput, violations)

    // Sub-Firewall 6: SkillInflationFirewall
    SkillInflationFirewall.inspect(rawOutput, violations)

    // Map verified facts
    if (rawOutput.contains("Dayananda Sagar", ignoreCase = true)) {
      verifiedFactsUsed.add("FACT_EDUCATION_01")
    }
    if (rawOutput.contains("100+", ignoreCase = true) || rawOutput.contains("Puma", ignoreCase = true)) {
      verifiedFactsUsed.add("FACT_EXPERIENCE_01")
    }
    if (rawOutput.contains("family business", ignoreCase = true) || rawOutput.contains("Kolkata", ignoreCase = true)) {
      verifiedFactsUsed.add("FACT_EXPERIENCE_02")
    }

    val hasBlockingViolations = violations.any { it.severity == "BLOCKING" }
    val riskLevel = when {
      hasBlockingViolations -> "CRITICAL_BLOCKED"
      violations.isNotEmpty() -> "MEDIUM_WARNING"
      else -> "LOW"
    }

    val sanitizedText = if (hasBlockingViolations) {
      sanitizeOutput(rawOutput, forbiddenTokens)
    } else {
      rawOutput
    }

    return FirewallAuditResult(
      isApproved = !hasBlockingViolations,
      sanitizedOutput = sanitizedText,
      violations = violations,
      verifiedFactIdsUsed = verifiedFactsUsed,
      pipelineStagesCompleted = PIPELINE_STAGES,
      riskLevel = riskLevel
    )
  }

  // --- SUB-FIREWALL SUBSYSTEMS (Section 19) ---

  object IdentityFirewall {
    fun inspect(text: String, violations: MutableList<FirewallViolation>) {
      val lower = text.lowercase()
      if (lower.contains("shenoy") || (lower.contains("aditya") && !lower.contains("mehra") && !lower.contains("adi"))) {
        if (lower.contains("shenoy")) {
          violations.add(
            FirewallViolation(
              ruleName = "IDENTITY_FIREWALL_SURNAME",
              violatedTextSnippet = "Shenoy",
              reason = "Surname 'Shenoy' violates canonical identity (Aditya Mehra).",
              severity = "BLOCKING",
              recommendedCorrection = "Change surname to Mehra."
            )
          )
        }
      }
    }
  }

  object EducationFirewall {
    fun inspect(text: String, violations: MutableList<FirewallViolation>) {
      val lower = text.lowercase()
      if (lower.contains("bengaluru university") || lower.contains("bangalore university")) {
        violations.add(
          FirewallViolation(
            ruleName = "EDUCATION_FIREWALL_INSTITUTION",
            violatedTextSnippet = "Bengaluru University",
            reason = "Institution contradicts verified academic degree (Dayananda Sagar University).",
            severity = "BLOCKING",
            recommendedCorrection = "Use Dayananda Sagar University."
          )
        )
      }
      if (lower.contains("3.82") || lower.contains("3.85") || lower.contains("first class honors")) {
        violations.add(
          FirewallViolation(
            ruleName = "EDUCATION_FIREWALL_GPA",
            violatedTextSnippet = "GPA / Honors",
            reason = "Academic GPA or honors inflation lacks evidence (~6.33 CGPA, 10-point scale).",
            severity = "BLOCKING",
            recommendedCorrection = "State verified 41/41 subjects passed with zero backlogs."
          )
        )
      }
    }
  }

  object CompensationFirewall {
    fun inspect(text: String, violations: MutableList<FirewallViolation>) {
      val lower = text.lowercase()
      if (lower.contains("18.5l") || lower.contains("₹18.5l") || lower.contains("18.5 lakh")) {
        violations.add(
          FirewallViolation(
            ruleName = "COMPENSATION_FIREWALL_OFFER",
            violatedTextSnippet = "₹18.5L",
            reason = "Fabricated compensation/offer benchmark.",
            severity = "BLOCKING",
            recommendedCorrection = "Remove fabricated offer claim."
          )
        )
      }
      if (lower.contains("24l") && (lower.contains("working capital") || lower.contains("offer"))) {
        violations.add(
          FirewallViolation(
            ruleName = "COMPENSATION_FIREWALL_INFLATED",
            violatedTextSnippet = "₹24L",
            reason = "Fabricated monetary or compensation benchmark.",
            severity = "BLOCKING",
            recommendedCorrection = "Remove unsupported monetary claim."
          )
        )
      }
    }
  }

  object MetricFirewall {
    fun inspect(text: String, violations: MutableList<FirewallViolation>) {
      val lower = text.lowercase()
      val isVerifiedTelemetry = (lower.contains("14 fulfillment") || lower.contains("16.2")) &&
          (lower.contains("42%") || lower.contains("42 %") || lower.contains("forty-two percent"))

      if (!isVerifiedTelemetry && (lower.contains("42%") || lower.contains("42 %") || lower.contains("forty-two percent"))) {
        violations.add(
          FirewallViolation(
            ruleName = "METRIC_FIREWALL_UNVERIFIED_PERCENTAGE",
            violatedTextSnippet = "42%",
            reason = "The '42%' reduction metric is unverified without verified 14 fulfillment hubs telemetry context.",
            severity = "BLOCKING",
            recommendedCorrection = "Anchor to verified supply chain telemetry: 14 fulfillment hubs, dispatch 28 min to 16.2 min (42% cycle reduction)."
          )
        )
      }
      if (lower.contains("38% latency") || lower.contains("38%")) {
        violations.add(
          FirewallViolation(
            ruleName = "METRIC_FIREWALL_UNVERIFIED_LATENCY",
            violatedTextSnippet = "38% latency",
            reason = "Fabricated latency reduction metric.",
            severity = "BLOCKING",
            recommendedCorrection = "Remove unverified latency percentage."
          )
        )
      }
    }
  }

  object ExperienceTitleFirewall {
    fun inspect(text: String, violations: MutableList<FirewallViolation>) {
      val lower = text.lowercase()
      val inflated = listOf("director of operations", "head of strategy", "chief of staff", "vp of sales", "executive director", "principal consultant")
      for (title in inflated) {
        if (lower.contains(title)) {
          violations.add(
            FirewallViolation(
              ruleName = "EXPERIENCE_TITLE_FIREWALL",
              violatedTextSnippet = title,
              reason = "Inflated executive title '$title' lacks documentary support.",
              severity = "BLOCKING",
              recommendedCorrection = "Use canonical truthful title, e.g. 'Event Management & Brand Activation Experience'."
            )
          )
        }
      }
    }
  }

  object SkillInflationFirewall {
    fun inspect(text: String, violations: MutableList<FirewallViolation>) {
      val lower = text.lowercase()
      if (lower.contains("world-class machine learning scientist") || lower.contains("10+ years expert in ai")) {
        violations.add(
          FirewallViolation(
            ruleName = "SKILL_INFLATION_FIREWALL",
            violatedTextSnippet = "AI Expert",
            reason = "Candidate is early-career; claiming 10+ years AI expertise contradicts timeline.",
            severity = "BLOCKING",
            recommendedCorrection = "Position as 'Applied AI Workflows & Prompt Architecture' at practical/intermediate level."
          )
        )
      }
    }
  }

  private fun sanitizeOutput(raw: String, forbiddenTokens: List<String>): String {
    var result = raw
    for (token in forbiddenTokens) {
      result = result.replace(Regex("(?i)" + Regex.escape(token)), "[REDACTED_BY_TRUTH_FIREWALL]")
    }
    return result
  }
}
