package com.example.truth

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * ADI-OS TRUTH ENGINE v4: Automated Regression Test Suite
 *
 * Mandatory CI/CD & Build Gate:
 * Every code change must pass this suite before the build can be deemed production-safe.
 *
 * Enforces:
 * 1. Canonical Identity
 * 2. Canonical Education
 * 3. Forbidden Values & Semantic Variants
 * 4. Evidence-to-Claim Requirements
 * 5. Contradiction Engine Gating
 * 6. Metric Validation & Unverified Metric Defense
 * 7. Title Validation & Inflation Defense
 * 8. Skill Promotion & Inflation Defense
 * 9. External Output Firewall
 * 10. Agent Least-Privilege Permission Model
 */
class TruthEngineRegressionTest {

  @Before
  fun setUp() {
    ContradictionEngine.clearConflictsForTesting()
    ImmutableChangeLog.clearForTesting()
  }

  // --- 1. CANONICAL IDENTITY ---
  @Test
  fun testCanonicalIdentityIntegrity() {
    assertEquals("Aditya Mehra", EvidenceVault.CANONICAL_FULL_NAME)
    assertEquals("Adi", EvidenceVault.CANONICAL_CALL_NAME)
    assertFalse("Canonical name must not contain Shenoy", EvidenceVault.CANONICAL_FULL_NAME.contains("Shenoy", true))
  }

  // --- 2. CANONICAL EDUCATION ---
  @Test
  fun testCanonicalEducationIntegrity() {
    assertEquals("Dayananda Sagar University", EvidenceVault.CANONICAL_UNIVERSITY)
    assertEquals("BBA (Bachelor of Business Administration)", EvidenceVault.CANONICAL_DEGREE)
    assertEquals("International Business", EvidenceVault.CANONICAL_SPECIALIZATION)
    assertEquals("2023–2026", EvidenceVault.CANONICAL_YEARS)
    assertEquals("~6.33/10 CGPA", EvidenceVault.CANONICAL_CGPA)
    assertEquals("41/41 subjects passed", EvidenceVault.CANONICAL_SUBJECTS_PASSED)
    assertEquals("Zero backlogs", EvidenceVault.CANONICAL_BACKLOG_STATUS)
  }

  // --- 3. FORBIDDEN VALUES & SEMANTIC VARIANTS ---
  @Test
  fun testForbiddenValuesDetection() {
    val testForbiddenStrings = listOf(
      "Aditya Shenoy",
      "shenoy",
      "Bengaluru University",
      "Bangalore University",
      "GPA 3.82",
      "3.82/4.0",
      "3.820",
      "First Class Honors",
      "42% cycle reduction",
      "42 %",
      "38% latency",
      "₹18.5L offer",
      "18.5 Lakh",
      "₹24L working capital"
    )

    testForbiddenStrings.forEach { forbidden ->
      val detected = EvidenceVault.detectForbiddenTokens(forbidden)
      assertTrue("Must detect forbidden token in: '$forbidden'", detected.isNotEmpty())
    }
  }

  // --- 4. EVIDENCE-TO-CLAIM REQUIREMENTS ---
  @Test
  fun testEvidenceToClaimMatching() {
    // Transcript supports BBA at DSU
    val dsuEvidence = EvidenceVault.CANONICAL_EVIDENCE_RECORDS.first { it.evidenceId == "EVID_DSU_TRANSCRIPT_2026" }
    assertEquals("ACADEMIC_RECORD", dsuEvidence.sourceType)

    // Code repo supports Applied AI workflows, but does NOT prove 'Principal AI Scientist'
    val codeEvidence = EvidenceVault.CANONICAL_EVIDENCE_RECORDS.first { it.evidenceId == "EVID_AI_WORKFLOWS_REPO" }
    assertTrue(codeEvidence.verifierNote.contains("Gemini API integrations"))
    assertFalse(codeEvidence.verifierNote.contains("Principal AI Scientist"))

    // Event roster supports supervised promoter deployments, does NOT prove VP/Executive
    val rosterEvidence = EvidenceVault.CANONICAL_EVIDENCE_RECORDS.first { it.evidenceId == "EVID_EVENT_ACTIVATIONS_ROSTER" }
    assertTrue(rosterEvidence.verifierNote.contains("Supervised crowd coordination"))
    assertFalse(rosterEvidence.verifierNote.contains("Vice President"))
  }

  // --- 5. CONTRADICTION HANDLING ---
  @Test
  fun testContradictionHandling() {
    val conflict = ContradictionEngine.evaluateIncomingClaim(
      incomingClaim = "Aditya graduated from Bengaluru University with 3.82 GPA",
      category = "EDUCATION"
    )
    assertNotNull("Contradiction must be generated", conflict)
    assertTrue("Contradiction must block overwrite", conflict?.blockedOverwrite == true)
  }

  // --- 6. METRIC VALIDATION ---
  @Test
  fun testMetricVerificationStructure() {
    val completeMetric = MetricVerification(
      metricName = "Brand Activations Supervised",
      definition = "Total volume of corporate promotional events supervised on-ground with zero SLA breaches",
      value = 100.0,
      unit = "events",
      timePeriod = "2023–2026",
      source = "Bengaluru Event Operations Records",
      evidenceId = "EVID_EVENT_ACTIVATIONS_ROSTER",
      calculationMethod = "Direct tally from client shift rosters and sign-off sheets"
    )
    assertTrue("Complete verified metric must be valid", completeMetric.isCompleteAndVerified)

    val incompleteMetric = MetricVerification(
      metricName = "Cycle Reduction",
      definition = "",
      value = 42.0,
      unit = "%",
      timePeriod = "",
      source = "",
      evidenceId = "",
      calculationMethod = ""
    )
    assertFalse("Incomplete metric must fail verification", incompleteMetric.isCompleteAndVerified)
  }

  // --- 7. TITLE VALIDATION ---
  @Test
  fun testTitleValidation() {
    val violations = mutableListOf<FirewallViolation>()
    ExternalOutputFirewall.ExperienceTitleFirewall.inspect("Served as Director of Operations", violations)
    assertTrue("Director title must be flagged", violations.any { it.ruleName == "EXPERIENCE_TITLE_FIREWALL" })

    val cleanViolations = mutableListOf<FirewallViolation>()
    ExternalOutputFirewall.ExperienceTitleFirewall.inspect("Event Management & Brand Activation Operations Experience", cleanViolations)
    assertTrue("Descriptive experience heading must NOT be flagged", cleanViolations.isEmpty())
  }

  // --- 8. SKILL VALIDATION ---
  @Test
  fun testSkillLevelHierarchyAndPromotion() {
    val currentSkill = SkillLevel.PRACTICAL
    // Promoting without evidence is forbidden
    assertFalse("Promotion to EXPERT requires evidence", currentSkill.canPromoteTo(SkillLevel.EXPERT, hasEvidence = false))
    // Promoting with evidence is permitted
    assertTrue("Promotion with evidence is allowed", currentSkill.canPromoteTo(SkillLevel.ADVANCED, hasEvidence = true))
    // Maintaining or stepping down requires no extra proof
    assertTrue("Maintaining level is always permitted", currentSkill.canPromoteTo(SkillLevel.PRACTICAL, hasEvidence = false))
  }

  // --- 9. EXTERNAL OUTPUT FIREWALL ---
  @Test
  fun testExternalOutputFirewallGating() {
    val dirtyPayload = "Candidate from Bengaluru University with 42% order efficiency"
    val dirtyAudit = ExternalOutputFirewall.inspectAndFilterOutput(dirtyPayload)
    assertFalse("Dirty payload must be rejected", dirtyAudit.isApproved)
    assertEquals("CRITICAL_BLOCKED", dirtyAudit.riskLevel)

    val cleanPayload = "Dayananda Sagar University BBA student with 100+ brand activations in Bengaluru"
    val cleanAudit = ExternalOutputFirewall.inspectAndFilterOutput(cleanPayload)
    assertTrue("Clean canonical payload must be approved", cleanAudit.isApproved)
    assertEquals("LOW", cleanAudit.riskLevel)
  }

  // --- 10. AGENT LEAST PRIVILEGE ---
  @Test
  fun testAgentPermissionModel() {
    // Read is allowed
    assertTrue(AgentPermissionManager.authorizeAction("MarketIntelligenceAgent", AgentPermission.READ))
    // Internal write is allowed
    assertTrue(AgentPermissionManager.authorizeAction("ResumeOptimizerAgent", AgentPermission.WRITE, isExternalFacing = false))
    // External action with forbidden content is rejected
    assertFalse(
      AgentPermissionManager.authorizeAction(
        agentName = "OutreachAgent",
        requestedPermission = AgentPermission.EXTERNAL_ACTION,
        isExternalFacing = true,
        contentPayload = "I graduated from Bengaluru University with a 3.82 GPA."
      )
    )
  }
}
