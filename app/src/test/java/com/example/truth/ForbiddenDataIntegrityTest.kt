package com.example.truth

import com.example.data.model.UserProfile
import com.example.service.LocalDocumentParser
import com.example.service.ResumeJobComparisonService
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * ADI-OS TRUTH ENGINE v3: Automated Forbidden Data Integrity Test Suite
 *
 * "Do not trust the system's claims. Inspect the implementation and prove them."
 *
 * This test suite programmatically enforces the zero-tolerance policy against:
 * - "Aditya Shenoy" (Forbidden identity)
 * - "Bengaluru University" / "Bangalore University" (Forbidden institution)
 * - "GPA 3.82" / "3.85/4.0" / "First Class Honors" (Forbidden academic inflation)
 * - "42% cycle reduction" / "reduced latency by 42%" (Forbidden fabricated metric)
 * - "₹18.5L offer" / "₹24L working capital" (Forbidden fabricated compensation)
 *
 * And asserts canonical ground truth:
 * - "Aditya Mehra" ("Adi")
 * - "Dayananda Sagar University"
 * - "BBA (Bachelor of Business Administration)" in "International Business"
 * - "~6.33/10 CGPA" | "41/41 subjects passed" | "Zero backlogs"
 * - "100+ brand activations"
 */
class ForbiddenDataIntegrityTest {

  @Before
  fun setUp() {
    ContradictionEngine.clearConflictsForTesting()
    ImmutableChangeLog.clearForTesting()
  }

  @Test
  fun testCanonicalIdentityConstants() {
    assertEquals("Aditya Mehra", EvidenceVault.CANONICAL_FULL_NAME)
    assertEquals("Adi", EvidenceVault.CANONICAL_CALL_NAME)
    assertEquals("Dayananda Sagar University", EvidenceVault.CANONICAL_UNIVERSITY)
    assertEquals("BBA (Bachelor of Business Administration)", EvidenceVault.CANONICAL_DEGREE)
    assertEquals("International Business", EvidenceVault.CANONICAL_SPECIALIZATION)
    assertEquals("2023–2026", EvidenceVault.CANONICAL_YEARS)
    assertEquals("41/41 subjects passed", EvidenceVault.CANONICAL_SUBJECTS_PASSED)
    assertEquals("Zero backlogs", EvidenceVault.CANONICAL_BACKLOG_STATUS)
  }

  @Test
  fun testEvidenceVaultContainsZeroForbiddenTokens() {
    EvidenceVault.CANONICAL_FACTS.forEach { fact ->
      val forbiddenDetected = EvidenceVault.detectForbiddenTokens(fact.claim)
      assertTrue(
        "Fact '${fact.factId}' contains forbidden tokens: $forbiddenDetected",
        forbiddenDetected.isEmpty()
      )
      assertTrue(
        "Fact status must be verified (E1 or E2)",
        fact.status == TruthStatus.E1 || fact.status == TruthStatus.E2
      )
    }

    EvidenceVault.CANONICAL_EVIDENCE_RECORDS.forEach { evidence ->
      val forbidden = EvidenceVault.detectForbiddenTokens(evidence.verifierNote + " " + evidence.title)
      assertTrue(
        "Evidence '${evidence.evidenceId}' contains forbidden tokens: $forbidden",
        forbidden.isEmpty()
      )
    }
  }

  @Test
  fun testDefaultCandidateResumeHasNoForbiddenData() {
    val defaultResume = ResumeJobComparisonService.DEFAULT_RESUME_TEXT

    // Assert absence of all forbidden tokens
    EvidenceVault.FORBIDDEN_TOKENS.forEach { forbiddenToken ->
      assertFalse(
        "DEFAULT_RESUME_TEXT must NOT contain forbidden token '$forbiddenToken'",
        defaultResume.contains(forbiddenToken, ignoreCase = true)
      )
    }

    // Assert presence of verified canonical identity
    assertTrue("DEFAULT_RESUME_TEXT must contain 'ADITYA \"ADI\" MEHRA'", defaultResume.contains("ADITYA \"ADI\" MEHRA"))
    assertTrue("DEFAULT_RESUME_TEXT must contain 'Dayananda Sagar University'", defaultResume.contains("Dayananda Sagar University"))
    assertTrue("DEFAULT_RESUME_TEXT must contain '100+'", defaultResume.contains("100+"))
    assertTrue("DEFAULT_RESUME_TEXT must contain 'International Business'", defaultResume.contains("International Business"))
    assertTrue("DEFAULT_RESUME_TEXT must contain '41/41 Subjects Passed'", defaultResume.contains("41/41 Subjects Passed"))
    assertTrue("DEFAULT_RESUME_TEXT must contain 'Zero Backlogs'", defaultResume.contains("Zero Backlogs"))
  }

  @Test
  fun testMockResumeDocumentsHaveNoForbiddenData() {
    val mockDocs = LocalDocumentParser.getAvailableMockDocuments()
    assertTrue("Mock documents list must not be empty", mockDocs.isNotEmpty())

    mockDocs.forEach { doc ->
      val fullDocText = "${doc.fileName} ${doc.title} ${doc.description} ${doc.rawContent}"
      EvidenceVault.FORBIDDEN_TOKENS.forEach { forbiddenToken ->
        assertFalse(
          "Mock doc '${doc.id}' must NOT contain forbidden token '$forbiddenToken'",
          fullDocText.contains(forbiddenToken, ignoreCase = true)
        )
      }
    }
  }

  @Test
  fun testContradictionEngineBlocksForbiddenInstitutions() {
    val conflict = ContradictionEngine.evaluateIncomingClaim(
      incomingClaim = "Graduated with BBA from Bengaluru University with Distinction",
      category = "EDUCATION"
    )
    assertNotNull("Must detect contradiction for non-DSU university", conflict)
    assertEquals("INSTITUTION_MISMATCH", conflict?.conflictType)
    assertTrue("Overwrite must be blocked", conflict?.blockedOverwrite == true)
  }

  @Test
  fun testContradictionEngineBlocksForbiddenSurnames() {
    val conflict = ContradictionEngine.evaluateIncomingClaim(
      incomingClaim = "Aditya Shenoy - Senior Operations Lead",
      category = "IDENTITY"
    )
    assertNotNull("Must detect contradiction for forbidden surname", conflict)
    assertTrue("Overwrite must be blocked", conflict?.blockedOverwrite == true)
  }

  @Test
  fun testContradictionEngineBlocksFabricatedMetrics() {
    val conflict = ContradictionEngine.evaluateIncomingClaim(
      incomingClaim = "Reduced order delivery cycle times by 42% across regional hubs",
      category = "EXPERIENCE"
    )
    assertNotNull("Must detect contradiction for unverified 42% metric", conflict)
    assertTrue("Overwrite must be blocked", conflict?.blockedOverwrite == true)
  }

  @Test
  fun testContradictionEngineApprovesValidCanonicalClaims() {
    val conflict = ContradictionEngine.evaluateIncomingClaim(
      incomingClaim = "Supervised on-ground promoter teams across 100+ brand activations in Bengaluru with zero SLA breaches",
      category = "EXPERIENCE"
    )
    assertNull("Valid canonical claim must NOT produce a contradiction", conflict)
  }

  @Test
  fun testExternalOutputFirewallEnforcesZeroTolerance() {
    val taintedOutput = """
      ADITYA SHENOY
      BBA from Bengaluru University (GPA 3.82)
      Reduced order latency by 42% and secured ₹18.5L offer.
    """.trimIndent()

    val audit = ExternalOutputFirewall.inspectAndFilterOutput(taintedOutput, context = "RESUME_TEST")

    assertFalse("Firewall must REJECT output containing forbidden claims", audit.isApproved)
    assertEquals("CRITICAL_BLOCKED", audit.riskLevel)
    assertTrue("Firewall must report violations", audit.violations.isNotEmpty())
    assertTrue("Sanitized output must redact forbidden tokens", audit.sanitizedOutput.contains("[REDACTED_BY_TRUTH_FIREWALL]"))
  }

  @Test
  fun testExternalOutputFirewallApprovesCleanCanonicalData() {
    val cleanOutput = """
      ADITYA MEHRA
      BBA in International Business from Dayananda Sagar University (2023–2026, ~6.33 CGPA, 41/41 subjects cleared)
      Supervised on-ground operations and promoter teams across 100+ brand activations including Puma and Razorpay.
    """.trimIndent()

    val audit = ExternalOutputFirewall.inspectAndFilterOutput(cleanOutput, context = "RESUME_TEST")

    assertTrue("Firewall must APPROVE clean canonical output", audit.isApproved)
    assertEquals("LOW", audit.riskLevel)
    assertTrue("Firewall must report zero violations", audit.violations.isEmpty())
    assertTrue("Firewall must track verified fact usage", audit.verifiedFactIdsUsed.contains("FACT_EDUCATION_01"))
    assertTrue("Firewall must track verified fact usage", audit.verifiedFactIdsUsed.contains("FACT_EXPERIENCE_01"))
  }

  @Test
  fun testImmutableChangeLogMaintainsAuditLedger() {
    val change = ImmutableChangeLog.recordChange(
      factId = "FACT_EDUCATION_01",
      fieldName = "university",
      oldValue = "UNVERIFIED_INPUT",
      newValue = "Dayananda Sagar University",
      reason = "Verified against DSU official transcript",
      source = "TRUTH_ENGINE_V3_AUDIT"
    )

    assertEquals("FACT_EDUCATION_01", change.factId)
    assertEquals("Dayananda Sagar University", change.newValue)
    val ledger = ImmutableChangeLog.getChangeLog()
    assertEquals(1, ledger.size)
    assertEquals("university", ledger.first().fieldName)
  }

  @Test
  fun testAgentPermissionManagerGating() {
    // Read is allowed without content check
    assertTrue(AgentPermissionManager.authorizeAction("ResearchAgent", AgentPermission.READ))

    // Internal Write is allowed
    assertTrue(AgentPermissionManager.authorizeAction("DraftingAgent", AgentPermission.WRITE, isExternalFacing = false))

    // External action with forbidden content is REJECTED
    assertFalse(
      AgentPermissionManager.authorizeAction(
        agentName = "OutreachAgent",
        requestedPermission = AgentPermission.EXTERNAL_ACTION,
        isExternalFacing = true,
        contentPayload = "I delivered 42% cycle reduction and secured ₹18.5L offer."
      )
    )

    // External action with verified canonical content is APPROVED
    assertTrue(
      AgentPermissionManager.authorizeAction(
        agentName = "OutreachAgent",
        requestedPermission = AgentPermission.EXTERNAL_ACTION,
        isExternalFacing = true,
        contentPayload = "Supervised 100+ brand activations across Bengaluru with zero SLA breaches."
      )
    )
  }
}
