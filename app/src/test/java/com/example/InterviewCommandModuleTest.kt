package com.example

import com.example.data.model.InterviewCommandDossier
import com.example.data.model.InterviewCommandQuestion
import com.example.data.model.StructuredTalkingPoint
import com.example.service.InterviewCommandGeneratorService
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class InterviewCommandModuleTest {

  private val generatorService = InterviewCommandGeneratorService()

  @Test
  fun testZeptoInterviewCommandDossierGeneration() = runBlocking {
    val dossier = generatorService.createCalibratedInterviewCommandDossier(
      company = "Zepto",
      role = "Lead - Strategy & Dark Store Operations"
    )

    assertEquals("Zepto", dossier.companyName)
    assertEquals("Lead - Strategy & Dark Store Operations", dossier.jobTitle)
    assertTrue("Should include quick-commerce context", dossier.executiveContext.contains("Zepto", ignoreCase = true))

    // Verify structured talking points
    assertTrue("Should have structured talking points", dossier.strategicTalkingPoints.isNotEmpty())
    val hookPoint = dossier.strategicTalkingPoints.firstOrNull { it.pillar == "EXECUTIVE_HOOK" }
    assertNotNull("Should have an Executive Hook talking point", hookPoint)
    assertTrue("Hook should mention cycle reduction", hookPoint?.impactMetric?.contains("42%") == true)

    val metricsPoint = dossier.strategicTalkingPoints.firstOrNull { it.pillar == "METRICS_PROOF" }
    assertNotNull("Should have Metrics Proof talking point", metricsPoint)
    assertTrue("Metrics should mention working capital", metricsPoint?.impactMetric?.contains("₹24L") == true)

    // Verify role-specific mock questions
    assertTrue("Should have 5 mock questions", dossier.mockQuestions.size >= 5)
    val analyticalQuestion = dossier.mockQuestions.firstOrNull { it.category == "ANALYTICAL_CASE" }
    assertNotNull("Should have an Analytical Case question", analyticalQuestion)
    assertTrue("Question should relate to dark store picking", analyticalQuestion?.questionText?.contains("dark store", ignoreCase = true) == true)
    assertTrue("Why asked should explain signal rubric", analyticalQuestion?.whyCompanyAsksThis?.isNotBlank() == true)
    assertTrue("Candidate profile anchor should connect background", analyticalQuestion?.candidateProfileAnchor?.isNotBlank() == true)
    assertTrue("Talking points to hit should be populated", analyticalQuestion?.structuredTalkingPoints?.isNotEmpty() == true)

    // Verify high impact questions for interviewer
    assertTrue("Should have questions to ask the interviewer", dossier.highImpactQuestionsForInterviewer.isNotEmpty())
  }

  @Test
  fun testRazorpayInterviewCommandDossierGeneration() = runBlocking {
    val dossier = generatorService.createCalibratedInterviewCommandDossier(
      company = "Razorpay",
      role = "FinTech Business Analyst"
    )

    assertEquals("Razorpay", dossier.companyName)
    assertEquals("FinTech Business Analyst", dossier.jobTitle)
    assertTrue("Should contain FinTech industry", dossier.industry.contains("FinTech", ignoreCase = true))

    val hook = dossier.strategicTalkingPoints.firstOrNull { it.pillar == "EXECUTIVE_HOOK" }
    assertNotNull(hook)
    assertTrue("Should mention active ₹18.5L offer or hackathon", hook?.impactMetric?.contains("₹18.5L") == true)

    val questions = dossier.mockQuestions
    assertTrue("Should generate mock questions", questions.isNotEmpty())
    val checkoutQuestion = questions.firstOrNull { it.questionText.contains("checkout", ignoreCase = true) || it.questionText.contains("Razorpay", ignoreCase = true) }
    assertNotNull("Should have Razorpay checkout question", checkoutQuestion)
  }

  @Test
  fun testGenericCompanyInterviewCommandDossierGeneration() = runBlocking {
    val dossier = generatorService.createCalibratedInterviewCommandDossier(
      company = "Stripe",
      role = "Global Operations Lead",
      candidateName = "Adi",
      degree = "BBA in International Business"
    )

    assertEquals("Stripe", dossier.companyName)
    assertEquals("Global Operations Lead", dossier.jobTitle)
    assertTrue("Should have talking points", dossier.strategicTalkingPoints.size >= 4)
    assertTrue("Should have 5 mock questions", dossier.mockQuestions.size == 5)

    // Check behavioral STAR question
    val behavioral = dossier.mockQuestions.firstOrNull { it.category == "BEHAVIORAL_STAR" }
    assertNotNull(behavioral)
    assertTrue(behavioral?.questionText?.isNotBlank() == true)
    assertTrue(behavioral?.sampleStarAnswer?.isNotBlank() == true)

    // Check questions for interviewer
    assertEquals(3, dossier.highImpactQuestionsForInterviewer.size)
  }

  @Test
  fun testStructuredTalkingPointPillars() {
    val point = StructuredTalkingPoint(
      id = "tp_01",
      pillar = "METRICS_PROOF",
      title = "Working Capital Turnaround",
      bulletPoints = listOf("Freed ₹24,00,000 in dead inventory", "Slashed order cycle by 42%"),
      impactMetric = "₹24L Saved",
      strategicRationale = "Demonstrates immediate cash flow optimization capability."
    )

    assertEquals("tp_01", point.id)
    assertEquals("METRICS_PROOF", point.pillar)
    assertEquals(2, point.bulletPoints.size)
    assertEquals("₹24L Saved", point.impactMetric)
  }

  @Test
  fun testInterviewCommandQuestionPracticeToggling() {
    val question = InterviewCommandQuestion(
      id = "q_01",
      questionText = "How do you handle supplier contract delays?",
      category = "BEHAVIORAL_STAR",
      difficulty = "CORE",
      whyCompanyAsksThis = "Tests negotiation and SLA governance under pressure.",
      candidateProfileAnchor = "Connects to family business vendor negotiations.",
      structuredTalkingPoints = listOf("Data over emotion", "Objective SLA metrics"),
      starSituation = "Supplier missed delivery.",
      starTask = "Enforce SLA.",
      starAction = "Presented 90 days of metrics.",
      starResult = "Improved on-time delivery from 64% to 94%."
    )

    assertEquals("q_01", question.id)
    assertEquals("BEHAVIORAL_STAR", question.category)
    assertEquals("CORE", question.difficulty)
    assertEquals("Enforce SLA.", question.starTask)
  }
}
