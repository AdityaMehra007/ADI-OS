package com.example

import com.example.data.model.CandidateMatchStatus
import com.example.data.model.JobActionPlan
import com.example.data.model.SkillImportance
import com.example.service.LocalJobActionPlanGenerator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class JobDescriptionAiAnalysisTest {

  @Test
  fun testSampleJobDescriptionsCompleteness() {
    val sampleJds = LocalJobActionPlanGenerator.SAMPLE_JOB_DESCRIPTIONS
    assertTrue("Sample job descriptions must not be empty", sampleJds.isNotEmpty())
    assertTrue("Should provide at least 4 real-world benchmarks", sampleJds.size >= 4)

    val companies = sampleJds.map { it.company }
    assertTrue(companies.contains("Zepto"))
    assertTrue(companies.contains("Razorpay"))
    assertTrue(companies.contains("Blinkit"))
    assertTrue(companies.contains("Swiggy"))

    sampleJds.forEach { jd ->
      assertTrue("Role title must not be blank for ${jd.company}", jd.role.isNotBlank())
      assertTrue("Salary benchmark must not be blank for ${jd.company}", jd.salary.isNotBlank())
      assertTrue("Job description must contain substantial text for ${jd.company}", jd.description.length > 200)
      assertTrue("Highlights must not be empty for ${jd.company}", jd.keyHighlights.isNotEmpty())
    }
  }

  @Test
  fun testDeterministicActionPlanGenerationForZepto() {
    val zeptoJd = LocalJobActionPlanGenerator.SAMPLE_JOB_DESCRIPTIONS.first { it.company == "Zepto" }
    val plan = LocalJobActionPlanGenerator.generateActionPlan(
      targetRole = zeptoJd.role,
      targetCompany = zeptoJd.company,
      jobDescription = zeptoJd.description
    )

    assertNotNull("Plan must not be null", plan)
    assertEquals(zeptoJd.company, plan.targetCompany)
    assertEquals(zeptoJd.role, plan.targetRole)
    assertTrue("Match score must be in 0..100 range", plan.matchFitScore in 50..100)
    assertTrue("Role mission summary must be populated", plan.roleMissionSummary.isNotBlank())
    assertTrue("Seniority level must be defined", plan.seniorityLevel.isNotBlank())
    assertTrue("Compensation benchmark must be defined", plan.compensationBenchmark.isNotBlank())

    // Required Skills verification
    assertTrue("Must extract required skills", plan.requiredSkills.isNotEmpty())
    val criticalSkills = plan.requiredSkills.filter { it.importance == SkillImportance.CRITICAL }
    assertTrue("Must flag critical operational skills", criticalSkills.isNotEmpty())

    // Interview talking points verification
    assertTrue("Must generate STAR interview talking points", plan.interviewTalkingPoints.isNotEmpty())
    val firstPoint = plan.interviewTalkingPoints.first()
    assertTrue("Talking point must have executive narrative", firstPoint.executiveNarrative.isNotBlank())
    assertTrue("STAR breakdown must have action", firstPoint.starBreakdown.action.isNotBlank())
    assertTrue("STAR breakdown must have measurable result", firstPoint.starBreakdown.result.isNotBlank())

    // Trap & probing questions verification
    assertTrue("Must generate trap questions", plan.trapQuestions.isNotEmpty())
    val trap = plan.trapQuestions.first()
    assertTrue("Trap question must have recommended pivot", trap.recommendedPivot.isNotBlank())

    // Roadmap Phases
    assertTrue("Must contain multi-phase roadmap", plan.roadmapPhases.isNotEmpty())
    val allTasks = plan.roadmapPhases.flatMap { it.tasks }
    assertTrue("Must have executable tactical tasks", allTasks.isNotEmpty())
  }

  @Test
  fun testCustomPastedJobDescriptionAnalysis() {
    val customJd = """
      Role: Senior Analytics Lead - E-Commerce Growth
      Company: Flipkart
      Location: Bengaluru
      
      About the Role:
      Lead payment funnel analysis, cart abandonment analytics, and vendor retention models.
      Drive SQL and Python dashboards for daily GMV tracking.
      
      Requirements:
      - 2+ years experience in analytical problem solving.
      - Strong SQL, Python, and Tableau skills.
      - Strong stakeholder management and communication.
    """.trimIndent()

    val plan = LocalJobActionPlanGenerator.generateActionPlan(
      targetRole = "Senior Analytics Lead",
      targetCompany = "Flipkart",
      jobDescription = customJd
    )

    assertNotNull(plan)
    assertTrue("Fit score should reflect analytical match", plan.matchFitScore > 60)
    assertTrue("Skills must be extracted", plan.requiredSkills.isNotEmpty())
    assertTrue("Talking points must be tailored", plan.interviewTalkingPoints.isNotEmpty())
  }

  @Test
  fun testCandidateMatchStatusIntegrity() {
    val zeptoJd = LocalJobActionPlanGenerator.SAMPLE_JOB_DESCRIPTIONS.first { it.company == "Zepto" }
    val plan = LocalJobActionPlanGenerator.generateActionPlan(
      targetRole = zeptoJd.role,
      targetCompany = zeptoJd.company,
      jobDescription = zeptoJd.description
    )

    val matched = plan.requiredSkills.filter { it.candidateMatchStatus == CandidateMatchStatus.STRONG_MATCH }
    val gaps = plan.requiredSkills.filter {
      it.candidateMatchStatus == CandidateMatchStatus.PARTIAL_GAP ||
      it.candidateMatchStatus == CandidateMatchStatus.CRITICAL_GAP
    }

    assertTrue("Candidate with Operations background should have matched skills", matched.isNotEmpty())
    // Each skill must provide actionable details
    plan.requiredSkills.forEach { skill ->
      assertTrue("Why demanded explanation cannot be blank", skill.whyDemanded.isNotBlank())
      assertTrue("Upskilling action cannot be blank", skill.upskillingAction.isNotBlank())
    }
  }

  @Test
  fun testMatchTierClassification() {
    assertEquals(com.example.ui.components.MatchTier.EXCEPTIONAL, com.example.ui.components.MatchTier.fromScore(95))
    assertEquals(com.example.ui.components.MatchTier.EXCEPTIONAL, com.example.ui.components.MatchTier.fromScore(85))
    assertEquals(com.example.ui.components.MatchTier.STRONG, com.example.ui.components.MatchTier.fromScore(84))
    assertEquals(com.example.ui.components.MatchTier.STRONG, com.example.ui.components.MatchTier.fromScore(70))
    assertEquals(com.example.ui.components.MatchTier.MODERATE, com.example.ui.components.MatchTier.fromScore(69))
    assertEquals(com.example.ui.components.MatchTier.MODERATE, com.example.ui.components.MatchTier.fromScore(50))
    assertEquals(com.example.ui.components.MatchTier.LOW, com.example.ui.components.MatchTier.fromScore(49))
    assertEquals(com.example.ui.components.MatchTier.LOW, com.example.ui.components.MatchTier.fromScore(0))
  }
}
