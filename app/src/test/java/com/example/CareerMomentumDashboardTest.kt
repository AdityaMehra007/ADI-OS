package com.example

import com.example.data.model.CareerMilestone
import com.example.data.model.MilestoneTask
import com.example.data.model.MilestoneWithTasks
import com.example.ui.components.buildMomentumTimelineNodes
import com.example.ui.components.calculateMomentumMetrics
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CareerMomentumDashboardTest {

  @Test
  fun testDefaultTimelineNodesGeneration() {
    val nodes = buildMomentumTimelineNodes(
      milestonesWithTasks = emptyList(),
      roadmap = null,
      userProfile = null,
      horizonFilter = "ALL"
    )

    assertTrue("Should contain at least baseline and default seed nodes", nodes.size >= 4)
    val baseline = nodes.first()
    assertEquals("baseline_0", baseline.id)
    assertEquals("Baseline", baseline.horizon)
    assertEquals("ACHIEVED", baseline.status)
    assertTrue("Baseline should have acquired skills", baseline.skillsAcquired.isNotEmpty())
    assertTrue("Baseline should have cumulative skills", baseline.cumulativeSkills > 0)
  }

  @Test
  fun testTimelineNodesSynthesisFromRoomMilestonesWithTasks() {
    val milestone1 = CareerMilestone(
      id = "m_room_1",
      title = "Distributed Microservices Architecture",
      horizon = "6M",
      horizonYears = 0.5f,
      targetQuarter = "Q1 '27",
      category = "SYSTEMS_ARCHITECTURE",
      targetMetric = "Staff Engineer Readiness",
      strategicRationale = "Scalable high-throughput event sourcing",
      status = "ACHIEVED"
    )

    val tasks1 = listOf(
      MilestoneTask(id = "t_1", milestoneId = "m_room_1", title = "Golang gRPC Services", isCompleted = true, category = "SKILLS"),
      MilestoneTask(id = "t_2", milestoneId = "m_room_1", title = "Distributed Caching", isCompleted = true, category = "SKILLS"),
      MilestoneTask(id = "t_3", milestoneId = "m_room_1", title = "Kafka Stream Processing", isCompleted = false, category = "SKILLS")
    )

    val milestone2 = CareerMilestone(
      id = "m_room_2",
      title = "Autonomous AI Multi-Agent Systems",
      horizon = "1Y",
      horizonYears = 1.0f,
      targetQuarter = "Q3 '27",
      category = "AI_SYSTEMS",
      targetMetric = "Principal Engineer Scope",
      strategicRationale = "Enterprise multi-agent LLM systems",
      status = "IN_PROGRESS"
    )

    val tasks2 = listOf(
      MilestoneTask(id = "t_4", milestoneId = "m_room_2", title = "Model Context Protocol", isCompleted = true, category = "SKILLS"),
      MilestoneTask(id = "t_5", milestoneId = "m_room_2", title = "Vector Pipeline Tuning", isCompleted = false, category = "SKILLS")
    )

    val roomMilestones = listOf(
      MilestoneWithTasks(milestone = milestone1, tasks = tasks1),
      MilestoneWithTasks(milestone = milestone2, tasks = tasks2)
    )

    val nodes = buildMomentumTimelineNodes(
      milestonesWithTasks = roomMilestones,
      roadmap = null,
      userProfile = null,
      horizonFilter = "ALL"
    )

    // Baseline + 2 Room milestones = 3 nodes
    assertEquals(3, nodes.size)

    val node1 = nodes[1]
    assertEquals("m_room_1", node1.id)
    assertEquals("6M", node1.horizon)
    assertEquals("Q1 '27", node1.quarter)
    assertEquals("ACHIEVED", node1.status)
    assertEquals(2, node1.skillsAcquired.size)
    assertEquals(1, node1.skillsInProgress.size)
    assertEquals(2, node1.tasksCompleted)
    assertEquals(3, node1.totalTasks)

    val node2 = nodes[2]
    assertEquals("m_room_2", node2.id)
    assertEquals("1Y", node2.horizon)
    assertEquals("Q3 '27", node2.quarter)
    assertEquals("IN_PROGRESS", node2.status)
    assertEquals(1, node2.skillsAcquired.size)
    assertEquals(1, node2.skillsInProgress.size)
    assertTrue("Cumulative skills should increment", node2.cumulativeSkills > node1.cumulativeSkills)
  }

  @Test
  fun testHorizonFiltering() {
    val milestone6M = CareerMilestone(
      id = "m_6m",
      title = "6-Month Sprint",
      horizon = "6M",
      horizonYears = 0.5f,
      targetQuarter = "2026-Q4",
      category = "CORE",
      targetMetric = "Deliverable 1",
      strategicRationale = "Rationale 1",
      status = "ACHIEVED"
    )
    val milestone2Y = CareerMilestone(
      id = "m_2y",
      title = "2-Year Horizon",
      horizon = "2Y",
      horizonYears = 2.0f,
      targetQuarter = "2028-Q2",
      category = "STRATEGY",
      targetMetric = "Deliverable 2",
      strategicRationale = "Rationale 2",
      status = "PLANNED"
    )

    val items = listOf(
      MilestoneWithTasks(milestone = milestone6M, tasks = emptyList()),
      MilestoneWithTasks(milestone = milestone2Y, tasks = emptyList())
    )

    val filtered6M = buildMomentumTimelineNodes(items, null, null, horizonFilter = "6M")
    assertTrue("Should include baseline", filtered6M.any { it.horizon == "Baseline" })
    assertTrue("Should include 6M node", filtered6M.any { it.horizon == "6M" })
    assertFalse("Should NOT include 2Y node", filtered6M.any { it.horizon == "2Y" })

    val filtered2Y = buildMomentumTimelineNodes(items, null, null, horizonFilter = "2Y")
    assertTrue("Should include 2Y node", filtered2Y.any { it.horizon == "2Y" })
    assertFalse("Should NOT include 6M node", filtered2Y.any { it.horizon == "6M" })
  }

  @Test
  fun testCalculateMomentumMetricsHighPerformance() {
    val milestone1 = CareerMilestone(
      id = "m_1",
      title = "M1",
      horizon = "6M",
      horizonYears = 0.5f,
      targetQuarter = "2026-Q4",
      category = "TECH",
      targetMetric = "T1",
      strategicRationale = "R1",
      status = "ACHIEVED"
    )
    val milestone2 = CareerMilestone(
      id = "m_2",
      title = "M2",
      horizon = "1Y",
      horizonYears = 1.0f,
      targetQuarter = "2027-Q2",
      category = "TECH",
      targetMetric = "T2",
      strategicRationale = "R2",
      status = "ACHIEVED"
    )

    val tasks1 = (1..5).map { MilestoneTask(id = "t1_$it", milestoneId = "m_1", title = "Task $it", isCompleted = true) }
    val tasks2 = (1..5).map { MilestoneTask(id = "t2_$it", milestoneId = "m_2", title = "Task $it", isCompleted = true) }

    val items = listOf(
      MilestoneWithTasks(milestone = milestone1, tasks = tasks1),
      MilestoneWithTasks(milestone = milestone2, tasks = tasks2)
    )

    val metrics = calculateMomentumMetrics(items, null, null)

    assertEquals(2, metrics.totalMilestones)
    assertEquals(2, metrics.completedMilestones)
    assertEquals(100, metrics.overallGrowthPercent)
    assertTrue("Velocity ratio should be above 1.35x for 100% completion", metrics.velocityRatio >= 1.35f)
    assertEquals("SUPERCHARGED", metrics.momentumTier)
    assertTrue(metrics.readinessScore in 70..100)
    assertNotNull(metrics.momentumSummary)
  }

  @Test
  fun testCalculateMomentumMetricsOnTrack() {
    val milestone1 = CareerMilestone(
      id = "m_1",
      title = "M1",
      horizon = "6M",
      horizonYears = 0.5f,
      targetQuarter = "2026-Q4",
      category = "TECH",
      targetMetric = "T1",
      strategicRationale = "R1",
      status = "IN_PROGRESS"
    )

    val tasks1 = listOf(
      MilestoneTask(id = "t1", milestoneId = "m_1", title = "Task 1", isCompleted = true),
      MilestoneTask(id = "t2", milestoneId = "m_1", title = "Task 2", isCompleted = false),
      MilestoneTask(id = "t3", milestoneId = "m_1", title = "Task 3", isCompleted = false),
      MilestoneTask(id = "t4", milestoneId = "m_1", title = "Task 4", isCompleted = false)
    )

    val items = listOf(
      MilestoneWithTasks(milestone = milestone1, tasks = tasks1)
    )

    val metrics = calculateMomentumMetrics(items, null, null)
    assertTrue("Completed milestones count is accurate", metrics.completedMilestones <= metrics.totalMilestones)
    assertTrue("Readiness score is valid", metrics.readinessScore in 1..100)
    assertTrue("Velocity ratio is positive", metrics.velocityRatio > 0.5f)
  }
}
