package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.CareerMilestoneStatus
import com.example.data.model.CareerStrategyRoadmap
import com.example.data.model.RoadmapPhase
import com.example.data.model.SkillMilestone
import com.example.data.model.StrategicPillar
import com.example.service.FirestoreCareerStrategyData
import com.example.service.FirestoreCareerStrategyService
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class FirestoreCareerStrategyServiceTest {

  private lateinit var context: Context
  private lateinit var service: FirestoreCareerStrategyService

  @Before
  fun setUp() {
    context = ApplicationProvider.getApplicationContext()
    service = FirestoreCareerStrategyService(context)
  }

  private fun createSampleRoadmap(): CareerStrategyRoadmap {
    val milestonesPhase1 = listOf(
      SkillMilestone(
        id = "m1_1",
        title = "Enterprise Data Modeling & Advanced SQL",
        category = "Financial Modeling & SQL",
        targetProficiency = "Production Ready",
        status = CareerMilestoneStatus.ACQUIRED,
        progress = 100,
        estimatedWeeks = 3,
        keyLearningOutcomes = listOf("Master PostgreSQL window functions", "Design normalized schemas"),
        proofOfWorkArtifact = "GitHub SQL repository",
        verificationMethod = "Peer review"
      ),
      SkillMilestone(
        id = "m1_2",
        title = "Multi-Agent System Orchestration",
        category = "AI & Autonomous Ops",
        targetProficiency = "Advanced Practitioner",
        status = CareerMilestoneStatus.IN_PROGRESS,
        progress = 50,
        estimatedWeeks = 4,
        keyLearningOutcomes = listOf("Build LangGraph agent pipelines", "Integrate vector DBs"),
        proofOfWorkArtifact = "Live AI automation demo",
        verificationMethod = "End-to-end evaluation benchmark"
      )
    )

    val phases = listOf(
      RoadmapPhase(
        phaseId = "p1",
        phaseNumber = 1,
        title = "Foundation & Core Operations Leverage",
        timeframe = "Months 1-6",
        targetRoleTier = "Strategy & Ops Lead",
        targetCompLakhs = "₹22L - ₹28L",
        focusTheme = "Operational excellence & AI automation",
        keyObjectives = listOf("Operational rigor", "Systems design"),
        skillMilestones = milestonesPhase1,
        deliverableArtifacts = listOf("SQL Data Mart", "Agent Control Room")
      )
    )

    val pillars = listOf(
      StrategicPillar(
        id = "pillar_1",
        name = "Operational Rigor & Systems Architecture",
        description = "Building resilient operations infrastructure",
        weightPercentage = 35,
        currentScore = 80
      )
    )

    return CareerStrategyRoadmap(
      id = "roadmap_test_1",
      targetRole = "Senior Strategy & Ops Lead / Chief of Staff",
      targetHorizon = "3 Years",
      targetCompensation = "₹35L - ₹45L",
      executiveSummary = "High-leverage strategic roadmap for executive tier transition",
      strategicPillars = pillars,
      phases = phases,
      tacticalPlaybook = listOf("Prioritize asymmetric proof of work", "Publish teardowns publicly"),
      geminiStrategicAnalysis = "Strong candidate trajectory with high velocity upside",
      confidenceScore = 96
    )
  }

  @Test
  fun testLocalCache_SaveAndLoad_RoundTripPreservesIntegrity() {
    val sampleRoadmap = createSampleRoadmap()
    val currentRole = "Associate Operations Analyst"
    val targetGoals = "Chief of Staff at Google"
    val timestamp = System.currentTimeMillis()

    // Save to local cache
    service.saveToLocalCache(
      currentRole = currentRole,
      targetGoals = targetGoals,
      roadmap = sampleRoadmap,
      timestamp = timestamp
    )

    // Load from local cache
    val loaded = service.loadFromLocalCache()

    assertNotNull("Loaded data should not be null", loaded)
    assertEquals(currentRole, loaded?.currentRole)
    assertEquals(targetGoals, loaded?.targetGoals)
    assertEquals(sampleRoadmap.targetRole, loaded?.roadmap?.targetRole)
    assertEquals(sampleRoadmap.phases.size, loaded?.roadmap?.phases?.size)
    assertEquals(2, loaded?.roadmap?.phases?.first()?.skillMilestones?.size)

    val m1 = loaded?.roadmap?.phases?.first()?.skillMilestones?.first()
    assertEquals("Enterprise Data Modeling & Advanced SQL", m1?.title)
    assertEquals(CareerMilestoneStatus.ACQUIRED, m1?.status)
    assertEquals(100, m1?.progress)
  }

  @Test
  fun testMilestoneProgressCalculations() {
    val sampleRoadmap = createSampleRoadmap()
    assertEquals(2, sampleRoadmap.totalMilestonesCount)
    assertEquals(1, sampleRoadmap.acquiredMilestonesCount)
    // 100 + 50 = 150 / 2 = 75%
    assertEquals(75, sampleRoadmap.overallProgressPercentage)
  }

  @Test
  fun testPhaseCompletionAndMilestoneProgression() {
    val sampleRoadmap = createSampleRoadmap()
    val phase1 = sampleRoadmap.phases.first()
    // Phase 1 has milestones with 100% and 50% -> average is 75%
    assertEquals(75, phase1.completionPercentage)

    // Acquired milestones count in phase
    val acquiredCount = phase1.skillMilestones.count { it.status == CareerMilestoneStatus.ACQUIRED }
    val inProgressCount = phase1.skillMilestones.count { it.status == CareerMilestoneStatus.IN_PROGRESS }
    assertEquals(1, acquiredCount)
    assertEquals(1, inProgressCount)
  }
}
