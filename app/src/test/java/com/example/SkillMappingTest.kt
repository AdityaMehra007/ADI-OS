package com.example

import com.example.data.model.CoreCompetency
import com.example.data.model.IndustrySkillGap
import com.example.data.model.SkillGapAnalysisReport
import com.example.data.model.TargetIndustryCatalog
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SkillMappingTest {

  @Test
  fun testCoreCompetencyCreationAndIndustryTagging() {
    val comp = CoreCompetency(
      id = "comp_sql_01",
      name = "SQL & Relational Schema Modeling",
      category = "ANALYTICS_DATA",
      proficiency = "ADVANCED",
      targetIndustries = listOf("Enterprise Cloud & SaaS", "FinTech & Digital Payments", "Quick Commerce & Logistics"),
      evidenceOrProofOfWork = "Automated reconciliation SQL pipeline cutting cycle times by 42%",
      yearsOfExperience = 2.0,
      isVerified = true
    )

    assertEquals("comp_sql_01", comp.id)
    assertEquals("SQL & Relational Schema Modeling", comp.name)
    assertEquals("ADVANCED", comp.proficiency)
    assertEquals(3, comp.targetIndustries.size)
    assertTrue(comp.targetIndustries.contains("Enterprise Cloud & SaaS"))
    assertTrue(comp.targetIndustries.contains("FinTech & Digital Payments"))
    assertTrue(comp.targetIndustries.contains("Quick Commerce & Logistics"))
    assertTrue(comp.isVerified)
  }

  @Test
  fun testIndustryCatalogConsistency() {
    assertTrue(TargetIndustryCatalog.ALL_INDUSTRIES.isNotEmpty())
    assertTrue(TargetIndustryCatalog.ALL_INDUSTRIES.contains("Enterprise Cloud & SaaS"))
    assertTrue(TargetIndustryCatalog.ALL_INDUSTRIES.contains("FinTech & Digital Payments"))
    assertTrue(TargetIndustryCatalog.ALL_INDUSTRIES.contains("Quick Commerce & Logistics"))
    assertTrue(TargetIndustryCatalog.ALL_INDUSTRIES.contains("Management Consulting & Strategy"))

    // Ensure color definitions exist for all primary industries
    TargetIndustryCatalog.ALL_INDUSTRIES.forEach { industry ->
      val color = TargetIndustryCatalog.INDUSTRY_COLORS[industry]
      assertNotNull("Color must exist for industry: $industry", color)
    }
  }

  @Test
  fun testIndustrySkillGapStructure() {
    val gap = IndustrySkillGap(
      id = "gap_agentic_01",
      skillName = "Agentic LLM Workflow Orchestration & MCP",
      targetIndustry = "Enterprise Cloud & SaaS",
      gapSeverity = "CRITICAL_GAP",
      currentMarketTrend = "2026 Enterprise SaaS leaders require operations analysts to orchestrate multi-agent LLM systems with Model Context Protocol (MCP).",
      whyNeeded = "Directly bridges BBA business framing into high-leverage AI productivity architectures.",
      estimatedSalaryDelta = "+₹4.5L - ₹6.0L / yr",
      recommendedAction = "Build a local Python/LangGraph pipeline that connects SQL inventory databases to Gemini.",
      suggestedRoles = listOf("Associate AI Operations Analyst", "Product Ops Associate"),
      isAddedToMyCompetencies = false
    )

    assertEquals("CRITICAL_GAP", gap.gapSeverity)
    assertEquals("Enterprise Cloud & SaaS", gap.targetIndustry)
    assertEquals("+₹4.5L - ₹6.0L / yr", gap.estimatedSalaryDelta)
    assertEquals(2, gap.suggestedRoles.size)
    assertFalse(gap.isAddedToMyCompetencies)
  }

  @Test
  fun testSkillGapAnalysisReportAggregation() {
    val gaps = listOf(
      IndustrySkillGap(
        id = "g1",
        skillName = "Automated Multi-Rail Settlement & Ledger Reconciliation",
        targetIndustry = "FinTech & Digital Payments",
        gapSeverity = "CRITICAL_GAP",
        currentMarketTrend = "High-growth payment players require sub-second dispute telemetry.",
        whyNeeded = "Leverages ₹24L working capital optimization into digital payments.",
        estimatedSalaryDelta = "+₹4.0L - ₹5.5L / yr",
        recommendedAction = "Simulate mock merchant payout ledger in SQL.",
        suggestedRoles = listOf("FinTech Operations Analyst"),
        isAddedToMyCompetencies = false
      ),
      IndustrySkillGap(
        id = "g2",
        skillName = "Dark Store Micro-Fulfillment & SKU Velocity Telemetry",
        targetIndustry = "Quick Commerce & Logistics",
        gapSeverity = "HIGH_PRIORITY",
        currentMarketTrend = "10-minute delivery unit economics require picker throughput optimization.",
        whyNeeded = "Translates 42% cycle reduction into quick commerce fulfillment networks.",
        estimatedSalaryDelta = "+₹3.8L - ₹5.2L / yr",
        recommendedAction = "Model a 500-SKU heat map ranking bin placement.",
        suggestedRoles = listOf("Supply Chain Strategist"),
        isAddedToMyCompetencies = false
      )
    )

    val report = SkillGapAnalysisReport(
      targetIndustries = listOf("FinTech & Digital Payments", "Quick Commerce & Logistics"),
      totalCompetenciesMapped = 8,
      marketAlignmentScore = 86,
      marketMacroSummary = "2026 technology hiring in Bengaluru values dual-threat operators combining SQL pipelines with unit economics.",
      gaps = gaps,
      modelUsed = "gemini-3.5-flash",
      isLiveAiGenerated = true
    )

    assertEquals(2, report.gaps.size)
    assertEquals(86, report.marketAlignmentScore)
    assertEquals(8, report.totalCompetenciesMapped)
    assertEquals("gemini-3.5-flash", report.modelUsed)
    assertTrue(report.isLiveAiGenerated)
  }

  @Test
  fun testAdoptingSuggestedGapToCompetencies() {
    val initialCompetencies = mutableListOf(
      CoreCompetency(
        id = "comp_sql",
        name = "SQL Modeling",
        category = "ANALYTICS_DATA",
        proficiency = "ADVANCED",
        targetIndustries = listOf("Enterprise Cloud & SaaS")
      )
    )

    val gapToAdopt = IndustrySkillGap(
      id = "gap_recon",
      skillName = "Automated Ledger Reconciliation",
      targetIndustry = "FinTech & Digital Payments",
      gapSeverity = "CRITICAL_GAP",
      currentMarketTrend = "High demand for automated dispute telemetry",
      whyNeeded = "Reconciliation pipelines",
      recommendedAction = "Build synthetic ledger"
    )

    // Simulate adopt logic
    val newCompetency = CoreCompetency(
      id = "comp_from_gap_${gapToAdopt.id}",
      name = gapToAdopt.skillName,
      category = "COMMERCIAL_FINANCE",
      proficiency = "BEGINNER",
      targetIndustries = listOf(gapToAdopt.targetIndustry),
      evidenceOrProofOfWork = "Target market skill gap: ${gapToAdopt.recommendedAction}",
      yearsOfExperience = 0.5,
      isVerified = false
    )
    initialCompetencies.add(newCompetency)

    assertEquals(2, initialCompetencies.size)
    assertTrue(initialCompetencies.any { it.name == "Automated Ledger Reconciliation" })
    val adopted = initialCompetencies.first { it.name == "Automated Ledger Reconciliation" }
    assertTrue(adopted.targetIndustries.contains("FinTech & Digital Payments"))
    assertEquals("BEGINNER", adopted.proficiency)
  }
}
