package com.example

import com.example.data.model.AgentItem
import com.example.data.model.Application
import com.example.data.model.Company
import com.example.data.model.Job
import com.example.data.model.RecruiterContact
import com.example.data.model.SkillItem
import com.example.data.model.StarStory
import com.example.data.model.UserProfile
import com.example.data.model.VerifiedFact
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class TitanDataModelTest {

  @Test
  fun testUserProfileDefaultState() {
    val profile = UserProfile(
      id = "adi_primary",
      name = "Adi",
      educationDegree = "BBA (Bachelor of Business Administration)",
      educationSpecialization = "International Business",
      university = "Bengaluru University",
      location = "Bengaluru, India",
      interests = "AI, Technology, International Business, Strategy, Operations",
      automationMode = "APPROVAL",
      brutalStrategyMode = false,
      profileCompleteness = 96
    )

    assertEquals("Adi", profile.name)
    assertEquals("APPROVAL", profile.automationMode)
    assertFalse(profile.brutalStrategyMode)
    assertEquals(96, profile.profileCompleteness)
  }

  @Test
  fun testJobScoringLogic() {
    val job = Job(
      id = "job_01",
      companyId = "comp_msft",
      companyName = "Microsoft",
      title = "Associate Strategy & Operations Analyst",
      roleFamily = "STRATEGY_OPS",
      department = "Strategic Operations",
      location = "Outer Ring Road / Bellandur, Bengaluru",
      city = "Bengaluru",
      country = "India",
      remoteStatus = "HYBRID",
      employmentType = "FULL_TIME",
      salaryRange = "₹16L - ₹22L",
      currency = "INR",
      experienceRequirement = "0-1 Years / Fresh Graduate",
      adiFitScore = 96,
      opportunityScore = 95,
      whyItFits = "Direct match for BBA International Business background.",
      whyItDoesntFit = "Requires fast adaptation to internal financial modeling tools.",
      missingRequirements = "Deep PowerBI / DAX modeling proficiency",
      recommendedAction = "Apply immediately via Employee Referral path.",
      applicationUrl = "https://careers.microsoft.com",
      officialSource = "Microsoft Careers India",
      sourceQuality = "OFFICIAL_CAREERS_PORTAL",
      postedDate = "2026-08-30",
      isFresherFriendly = true,
      strategicPriority = "APPLY_NOW",
      status = "ACTIVE"
    )

    assertTrue(job.adiFitScore >= 90)
    assertTrue(job.isFresherFriendly)
    assertEquals("APPLY_NOW", job.strategicPriority)
    assertEquals("Microsoft", job.companyName)
  }

  @Test
  fun testApplicationPipelineFlow() {
    val validStatuses = listOf("APPLIED", "ASSESSMENT", "INTERVIEW", "OFFER", "REJECTED")
    val app = Application(
      id = "app_1",
      jobId = "job_01",
      companyName = "Microsoft",
      roleTitle = "Associate Strategy Analyst",
      status = "INTERVIEW",
      dateDiscovered = "2026-08-27",
      dateApplied = "2026-08-28",
      resumeVersion = "v3.2-Strategy-Focused",
      coverLetterSnippet = "Tailored cover letter grounded in verified facts.",
      customAnswersJson = "{}",
      followUpDate = "2026-09-03",
      followUpNotes = "Prepare STAR scenario for leadership round",
      outcomeNotes = "Interview scheduled for next Tuesday"
    )

    assertTrue(validStatuses.contains(app.status))
    assertNotNull(app.resumeVersion)
    assertEquals("Microsoft", app.companyName)
  }

  @Test
  fun testVerifiedFactImmutabilityAndGrounding() {
    val fact = VerifiedFact(
      id = 1L,
      category = "EXPERIENCE",
      claim = "Led supply chain analytics pipeline project reducing latency by 42%",
      evidenceDetails = "Project repository, verified benchmarking telemetry, and final report",
      isVerified = true,
      tags = "SQL, Analytics, Performance"
    )

    assertTrue(fact.isVerified)
    assertTrue(fact.claim.contains("42%"))
    assertTrue(fact.tags.contains("SQL"))
  }

  @Test
  fun testStarStoryStructure() {
    val story = StarStory(
      id = 1L,
      title = "Supply Chain Bottleneck Resolution",
      storyType = "BUSINESS",
      situation = "Wholesale distributor was losing 18% margin to stockouts during quarterly peaks.",
      task = "Design real-time predictive inventory replenishment model.",
      action = "Built Python simulation model and automated alerts for low stock levels.",
      result = "Reduced stockout incidents by 42% and delivered INR 1.2M in quarterly savings.",
      verifiedFactsUsed = "Supply chain internship telemetry 2025",
      relevantSkills = "Analytics, Automation, Operations",
      matchedQuestions = "Tell me about a time you solved a complex operational problem"
    )

    assertNotNull(story.situation)
    assertNotNull(story.task)
    assertNotNull(story.action)
    assertNotNull(story.result)
    assertEquals("BUSINESS", story.storyType)
  }

  @Test
  fun testAgentSwarmCoverage() {
    val agentCategories = listOf(
      "STRATEGY", "INTELLIGENCE", "OUTREACH", "INTERVIEW_PREP", "PORTFOLIO", "SECURITY"
    )
    val agent = AgentItem(
      id = "agent_01",
      agentNumber = 1,
      name = "Market Opportunity Scanner",
      roleCategory = "INTELLIGENCE",
      permissionScope = "READ_ONLY",
      status = "ACTIVE",
      lastRunTimestamp = "2026-09-01 08:30",
      currentTask = "Scanning Bengaluru S-Tier tech openings",
      successCount = 42,
      failureCount = 0,
      confidenceScore = 99
    )

    assertEquals(1, agent.agentNumber)
    assertEquals("ACTIVE", agent.status)
    assertTrue(agentCategories.contains(agent.roleCategory))
  }

  @Test
  fun testCompanyTiering() {
    val company = Company(
      id = "comp_msft",
      name = "Microsoft",
      logoEmoji = "🏢",
      website = "https://microsoft.com",
      careersUrl = "https://careers.microsoft.com",
      industry = "Cloud & Enterprise AI",
      subIndustry = "Platform & Productivity",
      hqLocation = "Redmond, WA",
      bengaluruPresence = "Bellandur & Outer Ring Road (14,000+ staff)",
      indiaPresence = "Hyderabad, Bengaluru, Noida",
      employeeScale = "220,000+",
      tier = "S",
      strategicPriority = "HIGH_PRIORITY",
      hiringVelocity = "ACCELERATING",
      aiAdoptionLevel = "VERY HIGH",
      compensationTier = "₹16L - ₹24L+",
      isOpenForEarlyCareer = true,
      isWatched = true,
      watchNotes = "Primary target for strategy analyst tracks",
      scoreExplanation = "Tier S leader with high early-career leverage",
      activeOpeningsCount = 14
    )

    assertEquals("S", company.tier)
    assertTrue(company.isOpenForEarlyCareer)
    assertTrue(company.isWatched)
    assertTrue(company.activeOpeningsCount > 0)
  }

  @Test
  fun testBengaluruSalaryModelerCalculations() {
    val annualCtcLakhs = 18.0 // ₹18,00,000
    val totalAnnual = annualCtcLakhs * 100_000.0
    val basePay = totalAnnual * 0.70 // 70% Base: ₹12,60,000
    val variablePay = totalAnnual * 0.15 // 15% Variable: ₹2,70,000
    val joiningBonus = totalAnnual * 0.10 // 10% Bonus: ₹1,80,000
    val pfAnnual = (basePay * 0.12).coerceAtMost(21_600.0 * 12) // Provident Fund
    val professionalTaxAnnual = 2_400.0 // Karnataka PT

    val taxableIncome = (basePay - 75_000.0 - 50_000.0).coerceAtLeast(0.0)
    assertTrue(taxableIncome > 0)

    val monthlyTakeHome = (basePay - pfAnnual - professionalTaxAnnual) / 12.0
    assertTrue("Monthly in-hand should be greater than ₹80,000", monthlyTakeHome > 80_000.0)
    assertTrue("Base pay should be ₹12.6L", basePay == 1_260_000.0)
    assertTrue("Variable should be ₹2.7L", variablePay == 270_000.0)
    assertTrue("Bonus should be ₹1.8L", joiningBonus == 180_000.0)
  }

  @Test
  fun testStarStoryCompetencyAlignment() {
    val story = StarStory(
      id = 1L,
      title = "Wholesale Trading Modernization & Demand Forecasting",
      storyType = "BUSINESS",
      situation = "Traditional family enterprise with delayed turnaround.",
      task = "Modernize catalog, build tracking pipeline and AI demand model.",
      action = "Implemented SQL tracking, automated invoice pipeline.",
      result = "42% faster fulfillment, zero stockouts, 18% margin improvement.",
      verifiedFactsUsed = "Turnaround from 14 days to 8 days; zero stockouts",
      relevantSkills = "OPERATIONS, DATA_ANALYTICS, AUTOMATION, ENTREPRENEURSHIP",
      matchedQuestions = "Tell me about resolving a business bottleneck"
    )

    assertTrue(story.relevantSkills.contains("OPERATIONS"))
    assertTrue(story.relevantSkills.contains("AUTOMATION"))
    assertTrue(story.result.contains("42%"))
    assertEquals(1L, story.id)
  }

  @Test
  fun testJobDiscoveryAutomationDataStructures() {
    val logItem = com.example.service.JobDiscoveryLogItem(
      id = "log_test_01",
      timestamp = "Today, 10:30 AM",
      triggerSource = "BACKGROUND_RADAR",
      targetCompaniesScannedCount = 14,
      jobsDiscoveredCount = 8,
      newJobsStoredCount = 5,
      highMatchCount = 3,
      topJobTitles = listOf("Associate Product Operations Analyst", "Global Strategy Associate"),
      summaryMessage = "Scanned 14 target companies matching active BBA Strategy goals. Stored 5 new opportunities in Room.",
      activeGoalAlignment = "BBA Strategy Track"
    )

    assertEquals("BACKGROUND_RADAR", logItem.triggerSource)
    assertEquals(14, logItem.targetCompaniesScannedCount)
    assertEquals(5, logItem.newJobsStoredCount)
    assertEquals(3, logItem.highMatchCount)
    assertEquals(2, logItem.topJobTitles.size)
    assertTrue(logItem.summaryMessage.contains("Room"))

    val pullResult = com.example.service.JobPullResult(
      success = true,
      companiesScannedCount = 14,
      jobsPulledCount = 8,
      newJobsStoredCount = 5,
      highMatchJobsCount = 3,
      storedJobTitles = listOf("Product Operations Lead"),
      message = "Discovered 8 roles, stored 5 new openings locally."
    )

    assertTrue(pullResult.success)
    assertEquals(5, pullResult.newJobsStoredCount)
    assertEquals(3, pullResult.highMatchJobsCount)
  }

  @Test
  fun testExecutiveCareerInsightsDataStructures() {
    val insights = com.example.data.model.ExecutiveCareerInsights(
      headline = "High Leverage in Quick Commerce",
      executiveVerdict = "Triple threat candidate with 42% operational reduction.",
      marketPositioningStatement = "Algorithmic Operations Leader",
      targetCompensationRange = "₹28L - ₹42L CTC",
      strategicPillars = listOf(
        com.example.data.model.ExecutiveInsightPillar(
          id = "p1",
          title = "Technical Moat",
          score = 94,
          benchmarkTier = "Top 3%",
          keyStrength = "SQL & Python ETL",
          strategicImperative = "Frame ETL as executive telemetry"
        )
      ),
      topMarketOpportunities = listOf(
        com.example.data.model.ExecutiveMarketOpportunity(
          companyName = "Zepto",
          targetRole = "Lead Strategy & Operations",
          compensationRange = "₹32L - ₹42L CTC",
          fitScore = 96,
          leverageThesis = "Immediate fit for dark store throughput optimization",
          hiringVelocity = "SURGING"
        )
      ),
      competitiveMoats = listOf("42% cycle reduction", "Full-stack agent builder"),
      criticalBlindspots = listOf("Risk of being categorized as solo IC"),
      immediate30DayPriorities = listOf("Stage 3 concurrent interviews"),
      interviewPreparationVectors = listOf("Operational trade-offs drill"),
      overallReadinessScore = 94,
      confidenceScore = 97,
      modelUsed = "gemini-3.5-flash",
      timestamp = "Just now",
      isLiveApi = true
    )

    assertEquals("High Leverage in Quick Commerce", insights.headline)
    assertEquals(94, insights.overallReadinessScore)
    assertEquals(1, insights.strategicPillars.size)
    assertEquals(94, insights.strategicPillars.first().score)
    assertEquals("Zepto", insights.topMarketOpportunities.first().companyName)
    assertTrue(insights.isLiveApi)
  }

  @Test
  fun testCareerProgressionDataStructures() {
    val point = com.example.ui.components.ProgressionDataPoint(
      periodLabel = "Aug",
      fullDate = "August 2026",
      cumulativeSkills = 29,
      newSkillsAdded = 4,
      skillMasteryScore = 94,
      totalNetwork = 432,
      executiveLeads = 92,
      inboundOpportunities = 34,
      keySkillAcquisitions = listOf("Autonomous Multi-Agents", "SLA Telemetry"),
      milestoneEvent = "Command Center Live",
      correlationInsight = "Skill velocity boosted founder inbounds."
    )

    assertEquals("Aug", point.periodLabel)
    assertEquals(29, point.cumulativeSkills)
    assertEquals(4, point.newSkillsAdded)
    assertEquals(94, point.skillMasteryScore)
    assertEquals(432, point.totalNetwork)
    assertEquals(92, point.executiveLeads)
    assertEquals(2, point.keySkillAcquisitions.size)

    assertEquals(5, com.example.ui.components.ProgressionTimeRange.values().size)
    assertEquals(3, com.example.ui.components.ProgressionMetricMode.values().size)
  }

  @Test
  fun testLocalDocumentParserWithMockDocuments() {
    val mockDocs = com.example.service.LocalDocumentParser.getAvailableMockDocuments()
    assertTrue(mockDocs.isNotEmpty())
    assertEquals(4, mockDocs.size)

    val opsLeadDoc = mockDocs.first { it.id == "doc_ops_lead_v3" }
    assertEquals("Adi_Shenoy_Operations_Lead_v3.pdf", opsLeadDoc.fileName)
    assertEquals("PDF", opsLeadDoc.fileType)

    val parsed = com.example.service.LocalDocumentParser.parseDocumentText(
      fileName = opsLeadDoc.fileName,
      rawText = opsLeadDoc.rawContent,
      fileType = opsLeadDoc.fileType,
      fileSizeBytes = opsLeadDoc.simulatedSizeBytes
    )

    assertNotNull(parsed)
    assertTrue(parsed.contactInfo.candidateName.contains("ADI", ignoreCase = true))
    assertTrue(parsed.contactInfo.email.contains("shenoy", ignoreCase = true))
    assertTrue(parsed.wordCount > 100)
    assertTrue(parsed.quantifiableMetricRatio > 0.4f)
    assertTrue(parsed.actionVerbStrengthScore > 60)
    assertTrue(parsed.atsFormattingScore >= 70)
    assertTrue(parsed.experiences.isNotEmpty())
  }

  @Test
  fun testLocalDocumentParserWithPassiveWeakResume() {
    val mockDocs = com.example.service.LocalDocumentParser.getAvailableMockDocuments()
    val weakDoc = mockDocs.first { it.id == "doc_early_career_generalist_v1" }

    val parsed = com.example.service.LocalDocumentParser.parseDocumentText(
      fileName = weakDoc.fileName,
      rawText = weakDoc.rawContent,
      fileType = weakDoc.fileType,
      fileSizeBytes = weakDoc.simulatedSizeBytes
    )

    assertNotNull(parsed)
    // Weak resume has 0% metrics detected and weak verbs
    assertEquals(0f, parsed.quantifiableMetricRatio, 0.01f)
    assertTrue(parsed.weakVerbOccurrences.isNotEmpty())
    assertTrue(parsed.weakVerbOccurrences.contains("helped") || parsed.weakVerbOccurrences.contains("assisted"))

    // Generate deterministic feedback
    val feedback = com.example.service.LocalDocumentParser.generateDeterministicFeedback(
      parsedDoc = parsed,
      targetRole = "Lead Strategy & Operations",
      targetCompany = "Zepto"
    )

    assertNotNull(feedback)
    assertTrue(feedback.bulletTransformations.isNotEmpty())
    assertTrue(feedback.sectionCritiques.isNotEmpty())
    assertTrue(feedback.keywordAudit.missingHighPriorityKeywords.isNotEmpty())
    assertTrue(feedback.topImmediatePriorities.isNotEmpty())
  }

  @Test
  fun testDailyCareerBriefingModelsAndService() = kotlinx.coroutines.runBlocking {
    val service = com.example.ai.GeminiCareerService()
    val briefing = service.fetchDailyCareerBriefingWithSearchGrounding()

    assertNotNull(briefing)
    assertTrue(briefing.topFiveCompanies.size >= 5)
    assertTrue(briefing.searchQueriesUsed.isNotEmpty())
    assertNotNull(briefing.macroMarketSummary)
    assertNotNull(briefing.overallMarketSentiment)

    // Verify Google India data
    val googleBriefing = briefing.topFiveCompanies.find { it.companyName.contains("Google", ignoreCase = true) }
    assertNotNull(googleBriefing)
    assertEquals("NASDAQ: GOOGL", googleBriefing?.tickerSymbol)
    assertTrue(googleBriefing?.stockPrice?.isNotEmpty() == true)
    assertTrue(googleBriefing?.newsHeadline?.isNotEmpty() == true)
    assertTrue(googleBriefing?.hiringShiftsSummary?.isNotEmpty() == true)
    assertTrue(googleBriefing?.keyInDemandRoles?.isNotEmpty() == true)
    assertTrue(googleBriefing?.strategicInterviewAngle?.isNotEmpty() == true)
    assertTrue(googleBriefing?.groundedSources?.isNotEmpty() == true)

    // Verify all 5 top target companies have news, stock, and hiring shift metrics
    val companyNames = briefing.topFiveCompanies.map { it.companyName }
    assertTrue(companyNames.any { it.contains("Google", ignoreCase = true) })
    assertTrue(companyNames.any { it.contains("Microsoft", ignoreCase = true) })
    assertTrue(companyNames.any { it.contains("NVIDIA", ignoreCase = true) })
    assertTrue(companyNames.any { it.contains("Razorpay", ignoreCase = true) })
    assertTrue(companyNames.any { it.contains("Swiggy", ignoreCase = true) })
  }

  @Test
  fun testCareerVelocityReportData() {
    val report = com.example.data.repository.CareerVelocityDataProvider.getDefaultCareerVelocityReport()
    assertNotNull(report)
    assertEquals(86, report.overallVelocityIndex)
    assertTrue(report.roles.size >= 5)

    val googleRole = report.roles.find { it.companyName.contains("Google", ignoreCase = true) }
    assertNotNull(googleRole)
    assertEquals("Strategy & Operations Program Specialist", googleRole?.roleTitle)
    assertTrue((googleRole?.overallReadinessScore ?: 0) >= 80)
    assertTrue((googleRole?.monthlyVelocityRate ?: 0f) > 10f)
    assertTrue(googleRole?.timeline?.isNotEmpty() == true)
    assertTrue(googleRole?.requirements?.isNotEmpty() == true)

    // Verify requirements structure
    val req = googleRole?.requirements?.first()
    assertNotNull(req)
    assertTrue((req?.requiredProficiency ?: 0) > 0)
    assertTrue((req?.weightPercent ?: 0) > 0)
    assertNotNull(req?.verificationEvidence)

    // Verify all 5 target companies have roles
    val companies = report.roles.map { it.companyName }
    assertTrue(companies.any { it.contains("Google", ignoreCase = true) })
    assertTrue(companies.any { it.contains("Microsoft", ignoreCase = true) })
    assertTrue(companies.any { it.contains("NVIDIA", ignoreCase = true) })
    assertTrue(companies.any { it.contains("Razorpay", ignoreCase = true) })
    assertTrue(companies.any { it.contains("Swiggy", ignoreCase = true) })
  }

  @Test
  fun testD3CareerVelocityHtmlGeneration() {
    val report = com.example.data.repository.CareerVelocityDataProvider.getDefaultCareerVelocityReport()
    val role = report.roles.first()

    val html = com.example.ui.components.D3CareerVelocityHtmlBuilder.buildHtml(role)
    assertNotNull(html)
    assertTrue(html.contains("<!DOCTYPE html>"))
    assertTrue(html.contains("d3.v7.min.js"))
    assertTrue(html.contains("CAREER VELOCITY RADAR (D3.js ENGINE)"))
    assertTrue(html.contains("100% ROLE REQUIREMENT THRESHOLD"))
    assertTrue(html.contains("d3.scaleLinear()"))
    assertTrue(html.contains("d3.curveMonotoneX"))
    assertTrue(html.contains(role.companyName))
    assertTrue(html.contains(role.roleTitle))
  }

  @Test
  fun testSkillsRadarTargetJobProfiles() {
    val profiles = com.example.service.SkillsRadarEngine.TARGET_JOB_PROFILES
    assertTrue("Target job profiles should not be empty", profiles.isNotEmpty())
    assertEquals(5, profiles.size)

    val googleProfile = profiles.first { it.companyName == "Google India" }
    assertEquals("Strategy & Operations Program Specialist", googleProfile.roleTitle)
    assertTrue(googleProfile.requiredKeywords.contains("BigQuery"))
    assertTrue(googleProfile.requiredCertifications.isNotEmpty())
    assertEquals(6, googleProfile.dimensionTargets.size)
  }

  @Test
  fun testSkillsRadarReportGeneration() {
    val resume = com.example.service.LocalDocumentParser.AVAILABLE_MOCK_DOCUMENTS.first()
    val jobProfile = com.example.service.SkillsRadarEngine.TARGET_JOB_PROFILES.first { it.companyName == "Google India" }

    val report = com.example.service.SkillsRadarEngine.generateSkillsRadarReport(
      resume = resume,
      jobProfile = jobProfile
    )

    assertNotNull(report)
    assertEquals(resume.id, report.selectedResumeId)
    assertEquals("Google India", report.targetCompanyName)
    assertTrue("Overall match score should be positive", report.overallMatchScore > 50)
    assertTrue("Should have 6 dimensions", report.dimensions.size == 6)
    assertTrue("Should identify missing keywords", report.missingKeywords.isNotEmpty())
    assertTrue("Should identify missing certifications", report.missingCertifications.isNotEmpty())
    assertTrue("Should identify verified strengths", report.verifiedStrengths.isNotEmpty())

    // Verify dimension attributes
    val dataDim = report.dimensions.first { it.id == "data_analytics" }
    assertTrue(dataDim.resumeScore > 0f)
    assertTrue(dataDim.targetJdScore > 0f)
  }

  @Test
  fun testSkillsRadarDynamicKeywordInjection() {
    val resume = com.example.service.LocalDocumentParser.AVAILABLE_MOCK_DOCUMENTS.first()
    val jobProfile = com.example.service.SkillsRadarEngine.TARGET_JOB_PROFILES.first { it.companyName == "Google India" }

    val baselineReport = com.example.service.SkillsRadarEngine.generateSkillsRadarReport(
      resume = resume,
      jobProfile = jobProfile
    )

    val injectedReport = com.example.service.SkillsRadarEngine.generateSkillsRadarReport(
      resume = resume,
      jobProfile = jobProfile,
      injectedKeywords = setOf("Looker", "APAC Regional Governance"),
      injectedCertifications = setOf("Google Cloud Certified (Cloud Digital Leader or Data Engineer)")
    )

    assertTrue(
      "Injected keywords should increase or maintain matched keyword count",
      injectedReport.matchedKeywordsCount >= baselineReport.matchedKeywordsCount
    )
    assertTrue(
      "Injected certifications should increase matched certification count",
      injectedReport.matchedCertificationsCount >= baselineReport.matchedCertificationsCount
    )
  }

  @Test
  fun testCompanyIntelligenceFields() {
    val company = Company(
      id = "comp_custom_test",
      name = "Zepto",
      industry = "Quick Commerce / Logistics",
      website = "https://zepto.com",
      careersUrl = "https://zepto.com/careers",
      tier = "S+",
      strategicPriority = "Urgent",
      watchNotes = "Focus on micro-fulfillment center ops",
      latestNewsSummary = "Zepto raises $665M at a $3.6B valuation to expand quick commerce dark stores across top metro corridors.",
      newsSummaryTimestamp = "Just now",
      isCustomAdded = true
    )

    assertEquals("Zepto", company.name)
    assertEquals("S+", company.tier)
    assertTrue(company.isCustomAdded)
    assertEquals("Just now", company.newsSummaryTimestamp)
    assertTrue(company.latestNewsSummary.contains("$665M"))
  }
}

