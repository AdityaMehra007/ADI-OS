package com.example.data.repository

import com.example.data.model.CareerVelocityReport
import com.example.data.model.RequirementFulfillmentStatus
import com.example.data.model.RoleRequirementItem
import com.example.data.model.TargetCompanyRole
import com.example.data.model.VelocityTimelinePoint

object CareerVelocityDataProvider {

  fun getDefaultCareerVelocityReport(): CareerVelocityReport {
    val roles = listOf(
      buildGoogleStrategyRole(),
      buildMicrosoftCloudRole(),
      buildNvidiaAiOpsRole(),
      buildRazorpayFintechRole(),
      buildSwiggyQuickCommerceRole()
    )

    return CareerVelocityReport(
      overallVelocityIndex = 86,
      averageMonthlyGain = 13.8f,
      pacingVerdict = "ACCELERATING • 1.4x Market Velocity",
      roles = roles,
      activeRoleId = roles.first().id,
      lastUpdated = "Live Grounded Radar • Sep 2026",
      nextLeverageAction = "Deploy Google Cloud Ops Case Study and verify C-Suite presentation artifact to close final 8% readiness gap."
    )
  }

  private fun buildGoogleStrategyRole(): TargetCompanyRole {
    val reqs = listOf(
      RoleRequirementItem(
        id = "goog_req_1",
        name = "SQL & BigQuery Data Pipelines",
        category = "Data & Analytics",
        weightPercent = 25,
        requiredProficiency = 90,
        currentProgress = 92,
        status = RequirementFulfillmentStatus.FULFILLED,
        gapDetails = "Exceeds role threshold. Production-grade query optimization verified.",
        verificationEvidence = "Titan automated SQL discovery & pipeline analysis project"
      ),
      RoleRequirementItem(
        id = "goog_req_2",
        name = "Cross-Functional Ops & OKRs",
        category = "Strategic Ops",
        weightPercent = 25,
        requiredProficiency = 85,
        currentProgress = 82,
        status = RequirementFulfillmentStatus.IN_PROGRESS,
        gapDetails = "3% remaining. Needs multi-quarter OKR alignment artifact.",
        verificationEvidence = "Executive Command Center milestone framework"
      ),
      RoleRequirementItem(
        id = "goog_req_3",
        name = "Business Case & Financial Modeling",
        category = "Product & GTM",
        weightPercent = 20,
        requiredProficiency = 80,
        currentProgress = 76,
        status = RequirementFulfillmentStatus.IN_PROGRESS,
        gapDetails = "4% remaining. Unit economics and sensitivity modeling required.",
        verificationEvidence = "BBA International Business degree & financial modeling coursework"
      ),
      RoleRequirementItem(
        id = "goog_req_4",
        name = "Global Program Scalability",
        category = "Domain Systems",
        weightPercent = 15,
        requiredProficiency = 75,
        currentProgress = 72,
        status = RequirementFulfillmentStatus.IN_PROGRESS,
        gapDetails = "3% gap in regional APAC deployment protocols.",
        verificationEvidence = "Titan multi-agent workflow orchestration system"
      ),
      RoleRequirementItem(
        id = "goog_req_5",
        name = "Executive Presentation & Pitching",
        category = "Executive Leadership",
        weightPercent = 15,
        requiredProficiency = 85,
        currentProgress = 70,
        status = RequirementFulfillmentStatus.DEVELOPING,
        gapDetails = "15% gap. Finalizing executive deck synthesis & live case study defense.",
        verificationEvidence = "STAR Story bank (12 verified executive cases)"
      )
    )

    val timeline = listOf(
      VelocityTimelinePoint("Apr 2026", 0, 32f, 30f, 0.0f, milestoneAchieved = "SQL Core Baseline"),
      VelocityTimelinePoint("May 2026", 1, 44f, 42f, 12.0f, milestoneAchieved = "Data Pipeline V1"),
      VelocityTimelinePoint("Jun 2026", 2, 57f, 55f, 13.0f, milestoneAchieved = "Ops Governance Lab"),
      VelocityTimelinePoint("Jul 2026", 3, 69f, 66f, 12.0f, milestoneAchieved = "GTM Case Approved"),
      VelocityTimelinePoint("Aug 2026", 4, 78f, 75f, 9.0f, milestoneAchieved = "BBA Capstone Passed"),
      VelocityTimelinePoint("Sep 2026 (Now)", 5, 88f, 85f, 10.0f, milestoneAchieved = "Executive Defense Ready"),
      VelocityTimelinePoint("Oct 2026 (Proj)", 6, 96f, 92f, 8.0f, isProjection = true, milestoneAchieved = "Target Offer Pacing"),
      VelocityTimelinePoint("Nov 2026 (Target)", 7, 100f, 100f, 4.0f, isProjection = true, milestoneAchieved = "100% Role Mastery")
    )

    return TargetCompanyRole(
      id = "role_google_strat_ops",
      companyId = "comp_google",
      companyName = "Google India",
      logoEmoji = "🌐",
      roleTitle = "Strategy & Operations Program Specialist",
      department = "Google Cloud & Enterprise Ops",
      location = "Bengaluru (Outer Ring Road)",
      experienceTier = "Associate / 0-2 yrs",
      targetCompensation = "₹18L - ₹24L + Alphabet Equity",
      overallReadinessScore = 88,
      monthlyVelocityRate = 12.4f,
      velocityPacingStatus = "Accelerating",
      daysToFullReadiness = 38,
      primaryFocusArea = "Global Cloud Operations & Scaled Business Planning",
      requirements = reqs,
      timeline = timeline,
      executivePitch = "Leveraging deep BBA International Business grounding combined with automated SQL pipelines and executive operational rigor."
    )
  }

  private fun buildMicrosoftCloudRole(): TargetCompanyRole {
    val reqs = listOf(
      RoleRequirementItem(
        id = "msft_req_1",
        name = "Cloud Economics & TCO Analysis",
        category = "Strategic Ops",
        weightPercent = 25,
        requiredProficiency = 85,
        currentProgress = 85,
        status = RequirementFulfillmentStatus.FULFILLED,
        gapDetails = "Fully met. Enterprise TCO and multi-cloud ROI models verified.",
        verificationEvidence = "Multi-Offer compensation and ROI simulation models"
      ),
      RoleRequirementItem(
        id = "msft_req_2",
        name = "SQL & PowerBI Telemetry Pipelines",
        category = "Data & Analytics",
        weightPercent = 25,
        requiredProficiency = 90,
        currentProgress = 94,
        status = RequirementFulfillmentStatus.FULFILLED,
        gapDetails = "Exceeds standard benchmark.",
        verificationEvidence = "Recharts and automated telemetry data connectors"
      ),
      RoleRequirementItem(
        id = "msft_req_3",
        name = "Technical Program Management",
        category = "Domain Systems",
        weightPercent = 20,
        requiredProficiency = 80,
        currentProgress = 76,
        status = RequirementFulfillmentStatus.IN_PROGRESS,
        gapDetails = "4% gap in cross-discipline release tracking.",
        verificationEvidence = "Job discovery background automation service"
      ),
      RoleRequirementItem(
        id = "msft_req_4",
        name = "Enterprise Escalations Governance",
        category = "Operations",
        weightPercent = 15,
        requiredProficiency = 80,
        currentProgress = 78,
        status = RequirementFulfillmentStatus.IN_PROGRESS,
        gapDetails = "2% gap. Incident response protocols under final review.",
        verificationEvidence = "Human-in-the-loop audit log engine"
      ),
      RoleRequirementItem(
        id = "msft_req_5",
        name = "Partner Ecosystem Ops",
        category = "Executive Leadership",
        weightPercent = 15,
        requiredProficiency = 75,
        currentProgress = 66,
        status = RequirementFulfillmentStatus.DEVELOPING,
        gapDetails = "9% gap. Needs Tier-1 vendor co-sell case study.",
        verificationEvidence = "Network CRM relationship tracking"
      )
    )

    val timeline = listOf(
      VelocityTimelinePoint("Apr 2026", 0, 30f, 28f, 0.0f, milestoneAchieved = "Azure Fundamentals"),
      VelocityTimelinePoint("May 2026", 1, 41f, 38f, 11.0f, milestoneAchieved = "Data Schema Audit"),
      VelocityTimelinePoint("Jun 2026", 2, 53f, 50f, 12.0f, milestoneAchieved = "PowerBI Dashboards"),
      VelocityTimelinePoint("Jul 2026", 3, 67f, 62f, 14.0f, milestoneAchieved = "Enterprise ROI Model"),
      VelocityTimelinePoint("Aug 2026", 4, 76f, 73f, 9.0f, milestoneAchieved = "Ops SLA Framework"),
      VelocityTimelinePoint("Sep 2026 (Now)", 5, 85f, 82f, 9.0f, milestoneAchieved = "Partner Track Verified"),
      VelocityTimelinePoint("Oct 2026 (Proj)", 6, 94f, 90f, 9.0f, isProjection = true, milestoneAchieved = "Cloud Lead Pacing"),
      VelocityTimelinePoint("Nov 2026 (Target)", 7, 100f, 100f, 6.0f, isProjection = true, milestoneAchieved = "Offer Readiness")
    )

    return TargetCompanyRole(
      id = "role_microsoft_cloud_ops",
      companyId = "comp_microsoft",
      companyName = "Microsoft India",
      logoEmoji = "🪟",
      roleTitle = "Cloud Operations & Tech Strategy Lead",
      department = "Microsoft Cloud & Enterprise Ecosystem",
      location = "Bengaluru (Bellandur)",
      experienceTier = "Associate / 1-3 yrs",
      targetCompensation = "₹20L - ₹28L + Stock Options",
      overallReadinessScore = 85,
      monthlyVelocityRate = 11.8f,
      velocityPacingStatus = "On Track",
      daysToFullReadiness = 46,
      primaryFocusArea = "Azure Enterprise Governance & Multi-Cloud Economics",
      requirements = reqs,
      timeline = timeline,
      executivePitch = "Bridging cloud infrastructure metrics with board-ready enterprise storytelling and predictive cost governance."
    )
  }

  private fun buildNvidiaAiOpsRole(): TargetCompanyRole {
    val reqs = listOf(
      RoleRequirementItem(
        id = "nvda_req_1",
        name = "Quantitative Model Telemetry",
        category = "Data & Analytics",
        weightPercent = 25,
        requiredProficiency = 80,
        currentProgress = 86,
        status = RequirementFulfillmentStatus.FULFILLED,
        gapDetails = "Exceeds role requirement.",
        verificationEvidence = "Autonomous agent evaluation & hallucination monitoring"
      ),
      RoleRequirementItem(
        id = "nvda_req_2",
        name = "Strategic Compute Capacity Planning",
        category = "Strategic Ops",
        weightPercent = 25,
        requiredProficiency = 75,
        currentProgress = 80,
        status = RequirementFulfillmentStatus.FULFILLED,
        gapDetails = "Fully verified.",
        verificationEvidence = "Global supply chain & GPU cluster allocation research"
      ),
      RoleRequirementItem(
        id = "nvda_req_3",
        name = "AI Compute Infrastructure Ops",
        category = "Domain Systems",
        weightPercent = 20,
        requiredProficiency = 85,
        currentProgress = 74,
        status = RequirementFulfillmentStatus.IN_PROGRESS,
        gapDetails = "11% gap in CUDA cluster deployment metrics.",
        verificationEvidence = "Gemini multi-tier model latency telemetry"
      ),
      RoleRequirementItem(
        id = "nvda_req_4",
        name = "High-Performance Workflows",
        category = "Operations",
        weightPercent = 15,
        requiredProficiency = 80,
        currentProgress = 68,
        status = RequirementFulfillmentStatus.CRITICAL_GAP,
        gapDetails = "12% priority gap. Needs distributed execution case study.",
        verificationEvidence = "Async Coroutines & Room Database concurrency engine"
      ),
      RoleRequirementItem(
        id = "nvda_req_5",
        name = "Cross-Regional Engineering Coordination",
        category = "Executive Leadership",
        weightPercent = 15,
        requiredProficiency = 85,
        currentProgress = 78,
        status = RequirementFulfillmentStatus.IN_PROGRESS,
        gapDetails = "7% gap. Global matrix communication in progress.",
        verificationEvidence = "Executive Communication briefing & Star Stories"
      )
    )

    val timeline = listOf(
      VelocityTimelinePoint("Apr 2026", 0, 24f, 25f, 0.0f, milestoneAchieved = "AI Systems Overview"),
      VelocityTimelinePoint("May 2026", 1, 38f, 36f, 14.0f, milestoneAchieved = "GPU Architecture Model"),
      VelocityTimelinePoint("Jun 2026", 2, 51f, 48f, 13.0f, milestoneAchieved = "Agent Telemetry Built"),
      VelocityTimelinePoint("Jul 2026", 3, 62f, 60f, 11.0f, milestoneAchieved = "Capacity Planning Proof"),
      VelocityTimelinePoint("Aug 2026", 4, 71f, 70f, 9.0f, milestoneAchieved = "Benchmark Pipeline"),
      VelocityTimelinePoint("Sep 2026 (Now)", 5, 81f, 80f, 10.0f, milestoneAchieved = "AI Ops Framework Complete"),
      VelocityTimelinePoint("Oct 2026 (Proj)", 6, 91f, 88f, 10.0f, isProjection = true, milestoneAchieved = "Distributed Ops Defense"),
      VelocityTimelinePoint("Nov 2026 (Target)", 7, 100f, 100f, 9.0f, isProjection = true, milestoneAchieved = "Silicon Ops Mastery")
    )

    return TargetCompanyRole(
      id = "role_nvidia_ai_ops",
      companyId = "comp_nvidia",
      companyName = "NVIDIA India",
      logoEmoji = "⚡",
      roleTitle = "Enterprise AI Operations Lead",
      department = "Accelerated Computing & Enterprise AI",
      location = "Bengaluru (Bagmane Tech Park)",
      experienceTier = "Mid-Level / 1-3 yrs",
      targetCompensation = "₹22L - ₹32L + RSU Grants",
      overallReadinessScore = 81,
      monthlyVelocityRate = 12.0f,
      velocityPacingStatus = "Surging",
      daysToFullReadiness = 52,
      primaryFocusArea = "Accelerated Computing Operations & Real-Time Agent Telemetry",
      requirements = reqs,
      timeline = timeline,
      executivePitch = "Synthesizing AI infrastructure telemetry with quantitative capacity models to streamline multi-node deployment pipelines."
    )
  }

  private fun buildRazorpayFintechRole(): TargetCompanyRole {
    val reqs = listOf(
      RoleRequirementItem(
        id = "rzp_req_1",
        name = "SQL & Fraud Telemetry Pipelines",
        category = "Data & Analytics",
        weightPercent = 30,
        requiredProficiency = 95,
        currentProgress = 94,
        status = RequirementFulfillmentStatus.FULFILLED,
        gapDetails = "Exceeds entry-level threshold.",
        verificationEvidence = "Complex multi-table SQL joins & anomaly heuristics"
      ),
      RoleRequirementItem(
        id = "rzp_req_2",
        name = "Merchant Escalations & SLA",
        category = "Operations",
        weightPercent = 25,
        requiredProficiency = 85,
        currentProgress = 86,
        status = RequirementFulfillmentStatus.FULFILLED,
        gapDetails = "Fulfilled. High-velocity escalation protocol verified.",
        verificationEvidence = "Real-time task dispatch & approval workflow"
      ),
      RoleRequirementItem(
        id = "rzp_req_3",
        name = "Payments Settlement & Funnel Ops",
        category = "Strategic Ops",
        weightPercent = 20,
        requiredProficiency = 90,
        currentProgress = 88,
        status = RequirementFulfillmentStatus.IN_PROGRESS,
        gapDetails = "2% gap in reconciliation discrepancy triage.",
        verificationEvidence = "Payment gateway simulation and audit logs"
      ),
      RoleRequirementItem(
        id = "rzp_req_4",
        name = "RBI Compliance & Risk Protocols",
        category = "Domain Systems",
        weightPercent = 15,
        requiredProficiency = 80,
        currentProgress = 74,
        status = RequirementFulfillmentStatus.IN_PROGRESS,
        gapDetails = "6% gap in recurring mandate regulations.",
        verificationEvidence = "International business regulatory coursework"
      ),
      RoleRequirementItem(
        id = "rzp_req_5",
        name = "Incident Playbook Automation",
        category = "Product & GTM",
        weightPercent = 10,
        requiredProficiency = 85,
        currentProgress = 83,
        status = RequirementFulfillmentStatus.IN_PROGRESS,
        gapDetails = "2% gap. Playbook generator testing in progress.",
        verificationEvidence = "Titan automated rule engine"
      )
    )

    val timeline = listOf(
      VelocityTimelinePoint("Apr 2026", 0, 36f, 32f, 0.0f, milestoneAchieved = "Fintech Primer"),
      VelocityTimelinePoint("May 2026", 1, 48f, 44f, 12.0f, milestoneAchieved = "Fraud Heuristics"),
      VelocityTimelinePoint("Jun 2026", 2, 61f, 56f, 13.0f, milestoneAchieved = "SQL Funnel Audit"),
      VelocityTimelinePoint("Jul 2026", 3, 73f, 68f, 12.0f, milestoneAchieved = "SLA Matrix Live"),
      VelocityTimelinePoint("Aug 2026", 4, 83f, 78f, 10.0f, milestoneAchieved = "Settlement Simulation"),
      VelocityTimelinePoint("Sep 2026 (Now)", 5, 91f, 87f, 8.0f, milestoneAchieved = "Fintech Ops Certified"),
      VelocityTimelinePoint("Oct 2026 (Proj)", 6, 97f, 93f, 6.0f, isProjection = true, milestoneAchieved = "Role Ready"),
      VelocityTimelinePoint("Nov 2026 (Target)", 7, 100f, 100f, 3.0f, isProjection = true, milestoneAchieved = "Full Mastery")
    )

    return TargetCompanyRole(
      id = "role_razorpay_ops",
      companyId = "comp_razorpay",
      companyName = "Razorpay",
      logoEmoji = "💳",
      roleTitle = "Senior Operations & Risk Lead",
      department = "Merchant Operations & Financial Integrity",
      location = "Bengaluru (Koramangala HQ)",
      experienceTier = "Associate / 1-2 yrs",
      targetCompensation = "₹16L - ₹22L + ESOPs",
      overallReadinessScore = 91,
      monthlyVelocityRate = 14.1f,
      velocityPacingStatus = "Surging",
      daysToFullReadiness = 28,
      primaryFocusArea = "Payment Gateway Funnel Optimization & Merchant Risk Mitigation",
      requirements = reqs,
      timeline = timeline,
      executivePitch = "Directing end-to-end payment settlement reliability, automated dispute detection, and zero-failure SLA governance."
    )
  }

  private fun buildSwiggyQuickCommerceRole(): TargetCompanyRole {
    val reqs = listOf(
      RoleRequirementItem(
        id = "swiggy_req_1",
        name = "SQL & Geofenced Funnel Conversion",
        category = "Data & Analytics",
        weightPercent = 25,
        requiredProficiency = 90,
        currentProgress = 94,
        status = RequirementFulfillmentStatus.FULFILLED,
        gapDetails = "Exceeds benchmark.",
        verificationEvidence = "Geospatial SQL queries & density heatmap modeling"
      ),
      RoleRequirementItem(
        id = "swiggy_req_2",
        name = "Peak Hour War Room Orchestration",
        category = "Operations",
        weightPercent = 25,
        requiredProficiency = 85,
        currentProgress = 88,
        status = RequirementFulfillmentStatus.FULFILLED,
        gapDetails = "Fulfilled.",
        verificationEvidence = "Executive Command Center real-time dispatch simulator"
      ),
      RoleRequirementItem(
        id = "swiggy_req_3",
        name = "Dark Store Unit Economics & P&L",
        category = "Strategic Ops",
        weightPercent = 20,
        requiredProficiency = 90,
        currentProgress = 85,
        status = RequirementFulfillmentStatus.IN_PROGRESS,
        gapDetails = "5% gap in wastage shrinkage mitigation models.",
        verificationEvidence = "BBA microeconomics & inventory forecasting"
      ),
      RoleRequirementItem(
        id = "swiggy_req_4",
        name = "Fleet Dispatch & Hyperlocal Logistics",
        category = "Domain Systems",
        weightPercent = 15,
        requiredProficiency = 85,
        currentProgress = 82,
        status = RequirementFulfillmentStatus.IN_PROGRESS,
        gapDetails = "3% gap in monsoon delivery batching logic.",
        verificationEvidence = "Logistics routing algorithms analysis"
      ),
      RoleRequirementItem(
        id = "swiggy_req_5",
        name = "Dynamic Supply-Demand Balancing",
        category = "Product & GTM",
        weightPercent = 15,
        requiredProficiency = 80,
        currentProgress = 76,
        status = RequirementFulfillmentStatus.IN_PROGRESS,
        gapDetails = "4% gap in surge pricing elasticity simulation.",
        verificationEvidence = "Scenario Simulator stress test cases"
      )
    )

    val timeline = listOf(
      VelocityTimelinePoint("Apr 2026", 0, 34f, 30f, 0.0f, milestoneAchieved = "Q-Commerce 101"),
      VelocityTimelinePoint("May 2026", 1, 46f, 42f, 12.0f, milestoneAchieved = "Geofence Metrics"),
      VelocityTimelinePoint("Jun 2026", 2, 59f, 54f, 13.0f, milestoneAchieved = "Dark Store Model"),
      VelocityTimelinePoint("Jul 2026", 3, 71f, 66f, 12.0f, milestoneAchieved = "Fleet Dispatch V1"),
      VelocityTimelinePoint("Aug 2026", 4, 80f, 76f, 9.0f, milestoneAchieved = "War Room Playbook"),
      VelocityTimelinePoint("Sep 2026 (Now)", 5, 89f, 85f, 9.0f, milestoneAchieved = "P&L Defense Ready"),
      VelocityTimelinePoint("Oct 2026 (Proj)", 6, 96f, 92f, 7.0f, isProjection = true, milestoneAchieved = "Lead Candidate"),
      VelocityTimelinePoint("Nov 2026 (Target)", 7, 100f, 100f, 4.0f, isProjection = true, milestoneAchieved = "Offer Readiness")
    )

    return TargetCompanyRole(
      id = "role_swiggy_qcommerce_ops",
      companyId = "comp_swiggy",
      companyName = "Swiggy",
      logoEmoji = "🛵",
      roleTitle = "Quick Commerce Strategy & Ops Lead",
      department = "Instamart Expansion & City Operations",
      location = "Bengaluru (Marathahalli HQ)",
      experienceTier = "Associate / 1-2 yrs",
      targetCompensation = "₹17L - ₹23L + Retention Bonus",
      overallReadinessScore = 89,
      monthlyVelocityRate = 12.6f,
      velocityPacingStatus = "Accelerating",
      daysToFullReadiness = 34,
      primaryFocusArea = "Dark Store Density Optimization & Real-Time Fleet Balancing",
      requirements = reqs,
      timeline = timeline,
      executivePitch = "Mastering hyperlocal dispatch economics, rider retention dynamics, and millisecond routing efficiency for high-density metropolitan zones."
    )
  }
}
