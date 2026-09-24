package com.example

import com.example.data.model.CompanyInterviewSimulationDossier
import com.example.data.model.GeneratedInterviewQuestion
import com.example.data.model.InterviewAnswerEvaluation
import com.example.data.model.UserProfile
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class InterviewSimulatorTest {

  @Test
  fun testInterviewSimulationDossierStructure() {
    val question1 = GeneratedInterviewQuestion(
      id = "q_msft_01",
      questionText = "Microsoft India is accelerating Copilot adoption across SMBs. How would you prioritize sales ops bottlenecks?",
      category = "ANALYTICAL_CASE",
      difficulty = "CORE",
      whyCompanyAsksThis = "Tests structured decomposition of commercial bottlenecks and metric-driven execution.",
      candidateProfileAnchor = "Directly tests your BBA International Business framework and 42% order cycle reduction experience.",
      keyPointsToHit = listOf(
        "Segment SMB churn drivers",
        "Introduce predictive replenishment telemetry",
        "Quantify working capital optimization"
      ),
      sampleStarAnswer = "Situation: Rapid onboarding caused friction. Task: Streamline sales pipeline. Action: Automated SQL verification. Result: 42% cycle reduction.",
      isPracticed = false
    )

    val dossier = CompanyInterviewSimulationDossier(
      companyName = "Microsoft",
      targetRole = "Associate Business Analyst",
      industry = "Enterprise Cloud & AI",
      roundType = "ALL_ROUNDS",
      executiveContext = "Microsoft values structured clarity, customer empathy, and rigorous analytical models.",
      candidateGroundingSummary = "Questions tailored to candidate's verified BBA background and 42% cycle reduction metrics.",
      questions = listOf(question1),
      generatedTimestamp = System.currentTimeMillis(),
      modelUsed = "gemini-3.5-flash",
      isGroundedWithGemini = true
    )

    assertEquals("Microsoft", dossier.companyName)
    assertEquals("Associate Business Analyst", dossier.targetRole)
    assertEquals(1, dossier.questions.size)
    assertTrue(dossier.isGroundedWithGemini)
    assertEquals("gemini-3.5-flash", dossier.modelUsed)
    assertEquals("ANALYTICAL_CASE", dossier.questions.first().category)
  }

  @Test
  fun testAnswerEvaluationScoring() {
    val evaluation = InterviewAnswerEvaluation(
      score = 92,
      situationClarity = "Crisp context on family enterprise supply chain bottlenecks.",
      taskClarity = "Clear ownership of working capital and inventory forecasting.",
      actionImpact = "Engineered predictive demand models and automated replenishment pipelines.",
      resultMetrics = "Delivered 42% cycle reduction and ₹24L capital predictability.",
      keyStrengths = listOf("Specific metrics quantified", "Clear STAR structure", "Executive delivery"),
      areasForImprovement = listOf("Expand on multi-tier stakeholder alignment"),
      polishedExecutiveRephrase = "When inventory stockouts impacted cash flow, I developed automated predictive replenishment models that reduced order cycle times by 42% and safeguarded ₹24L in working capital.",
      evaluatorNotes = "Strong Hire recommendation for Microsoft strategy & operations."
    )

    assertEquals(92, evaluation.score)
    assertTrue(evaluation.score >= 90)
    assertEquals(3, evaluation.keyStrengths.size)
    assertEquals(1, evaluation.areasForImprovement.size)
    assertTrue(evaluation.polishedExecutiveRephrase.contains("42%"))
    assertTrue(evaluation.polishedExecutiveRephrase.contains("₹24L"))
  }

  @Test
  fun testCategoryFiltering() {
    val questions = listOf(
      GeneratedInterviewQuestion(
        id = "1",
        questionText = "Behavioral question",
        category = "BEHAVIORAL",
        difficulty = "CORE"
      ),
      GeneratedInterviewQuestion(
        id = "2",
        questionText = "Case question",
        category = "ANALYTICAL_CASE",
        difficulty = "ADVANCED"
      ),
      GeneratedInterviewQuestion(
        id = "3",
        questionText = "Strategy question",
        category = "STRATEGY_FIT",
        difficulty = "EXECUTIVE"
      )
    )

    val behavioralOnly = questions.filter { it.category == "BEHAVIORAL" }
    val casesOnly = questions.filter { it.category == "ANALYTICAL_CASE" }

    assertEquals(1, behavioralOnly.size)
    assertEquals(1, casesOnly.size)
    assertEquals("BEHAVIORAL", behavioralOnly.first().category)
    assertEquals("ANALYTICAL_CASE", casesOnly.first().category)
  }

  @Test
  fun testCandidateProfileGroundingIntegrity() {
    val userProfile = UserProfile(
      id = "adi_primary",
      name = "Aditya Mehra",
      educationDegree = "BBA",
      educationSpecialization = "International Business",
      university = "Dayananda Sagar University (DSU), Bengaluru",
      interests = "AI, Operations, Strategy"
    )

    val question = GeneratedInterviewQuestion(
      id = "q_rzp_01",
      questionText = "Razorpay processes billions in TPV. How would you solve merchant drop-offs?",
      category = "ROLE_SPECIFIC",
      difficulty = "ADVANCED",
      candidateProfileAnchor = "Grounded in Adi's Razorpay Hackathon Finalist achievement with ₹18.5L offer."
    )

    assertNotNull(userProfile)
    assertEquals("Aditya Mehra", userProfile.name)
    assertEquals("BBA", userProfile.educationDegree)
    assertTrue(question.candidateProfileAnchor.contains("Razorpay Hackathon Finalist"))
    assertTrue(question.candidateProfileAnchor.contains("₹18.5L"))
  }
}
