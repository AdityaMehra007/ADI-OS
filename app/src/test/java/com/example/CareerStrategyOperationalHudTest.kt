package com.example

import com.example.ui.components.CompetencyAlignmentVector
import com.example.ui.components.StrategyGrowthMilestone
import com.example.ui.components.StrategyHudTab
import com.example.ui.components.StrategyTargetRole
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class CareerStrategyOperationalHudTest {

  @Test
  fun testCompetencyAlignmentVectorCalculations() {
    val vector = CompetencyAlignmentVector(
      name = "Supply Chain Velocity",
      candidateScore = 98,
      targetBenchmark = 90,
      category = "OPERATIONS",
      strategicRationale = "380s to 223s dispatch cycle in 14 urban hubs"
    )

    // Surplus = 98 - 90 = +8%
    assertEquals(8, vector.surplusPct)
    assertTrue("Candidate score should exceed benchmark", vector.candidateScore > vector.targetBenchmark)
    assertEquals("OPERATIONS", vector.category)
  }

  @Test
  fun testStrategyGrowthMilestoneModel() {
    val milestone = StrategyGrowthMilestone(
      id = "M1",
      phaseNumber = 1,
      phaseTitle = "Phase 1: Academic & Operational Foundation",
      milestoneTitle = "DSU International Business 100% Clearance",
      domain = "ACADEMIC",
      completionPct = 100,
      isAcquired = true,
      targetQuarter = "Q1 2025",
      evidenceCitation = "41/41 subjects cleared on first attempt with 0 backlogs",
      impactMetric = "Academic Purity Anchor"
    )

    assertEquals("M1", milestone.id)
    assertEquals(1, milestone.phaseNumber)
    assertTrue("Milestone must be acquired", milestone.isAcquired)
    assertEquals(100, milestone.completionPct)
    assertEquals("41/41 subjects cleared on first attempt with 0 backlogs", milestone.evidenceCitation)
  }

  @Test
  fun testStrategyTargetRoleIntegrity() {
    val role = StrategyTargetRole(
      id = "STRAT_OPS_LEAD",
      title = "Lead - Strategy & Operations",
      targetOrganizationTier = "Tier-1 Quick Commerce (Zepto, Swiggy Instamart, Blinkit)",
      overallAlignmentPct = 94,
      compensationRange = "₹28L - ₹36L Fixed + ESOPs",
      primaryLeveragePoint = "14 Bengaluru Dark Store Network Turnaround & -42% Cycle Reduction",
      vectors = listOf(
        CompetencyAlignmentVector("Supply Chain Velocity", 98, 90, "OPERATIONS", "Dispatch cycle"),
        CompetencyAlignmentVector("CM2 Margin Optimization", 94, 85, "FINANCIAL", "+₹28.40 CM2")
      )
    )

    assertEquals(94, role.overallAlignmentPct)
    assertEquals(2, role.vectors.size)
    assertTrue("Role alignment must be >= 90%", role.overallAlignmentPct >= 90)
    assertTrue("Vectors must have positive surplus", role.vectors.all { it.surplusPct > 0 })
  }

  @Test
  fun testStrategyHudTabsEnum() {
    assertEquals(4, StrategyHudTab.values().size)
    assertNotNull(StrategyHudTab.ALIGNMENT_RADAR)
    assertNotNull(StrategyHudTab.MILESTONE_TRAJECTORY)
    assertNotNull(StrategyHudTab.COMPETENCY_VECTORS)
    assertNotNull(StrategyHudTab.EXECUTIVE_DIRECTIVES)
  }
}
