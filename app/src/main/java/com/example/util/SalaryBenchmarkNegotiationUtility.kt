package com.example.util

import com.example.data.model.NegotiationScriptScenario
import com.example.data.model.PhoneCallTalkingPoints
import com.example.data.model.SalaryBenchmarkAnalysis
import com.example.data.model.SalaryNegotiationBundle
import com.example.data.model.UserProfile
import com.example.data.model.VerifiedFact
import com.example.ui.components.CareerSeniority
import com.example.ui.components.MarketCompensationRepository
import com.example.ui.components.MarketTier
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.roundToInt

data class SampleBenchmarkJob(
  val company: String,
  val role: String,
  val tier: MarketTier,
  val seniority: CareerSeniority,
  val description: String
)

object SalaryBenchmarkNegotiationUtility {

  val PRESET_BENCHMARK_JOBS = listOf(
    SampleBenchmarkJob(
      company = "Zepto",
      role = "Lead - Strategy & Dark Store Operations",
      tier = MarketTier.UNICORN_HIGH_GROWTH,
      seniority = CareerSeniority.SENIOR,
      description = """
        Zepto is hiring a Lead for Strategy & Dark Store Operations.
        Responsibilities:
        - Own regional dark store throughput, delivery micro-dispatch logistics, and 10-minute turnaround metrics.
        - Build predictive capacity allocation models across 80+ micro-fulfillment nodes in Tier 1 metros.
        - Coordinate cross-functionally with Product, Fleet Operations, and Supply Chain to drive unit economic margin expansion.
        - Scale quick-commerce dispatch automation pipelines targeting 99.4% SLA adherence.
        Requirements:
        - 4+ years in Operations Strategy, Management Consulting, or High-Growth Tech.
        - Strong analytical problem solving (SQL, cohort modeling, unit economics analysis).
        - Proven track record of cross-functional executive stakeholder influence.
      """.trimIndent()
    ),
    SampleBenchmarkJob(
      company = "Google",
      role = "Senior Systems Engineer (L5 / Distributed Platforms)",
      tier = MarketTier.BIG_TECH_FAANG,
      seniority = CareerSeniority.SENIOR,
      description = """
        Google Core Platforms is hiring a Senior Systems Engineer (L5).
        Responsibilities:
        - Architect resilient, horizontally scalable microservices handling 50k+ QPS with sub-15ms p99 latency SLAs.
        - Partner with Global Infrastructure teams on multi-region failover, gRPC service mesh orchestration, and automated capacity scheduling.
        - Lead technical direction, write architecture decision records (ADRs), and mentor mid-level engineering ICs.
        Requirements:
        - 5+ years building and running distributed cloud-native services in production.
        - Mastery of Kotlin, Java, Go, Kubernetes, Kafka, and distributed consensus mechanisms.
        - Demonstrated ownership of high-impact technical initiatives spanning multiple quarters.
      """.trimIndent()
    ),
    SampleBenchmarkJob(
      company = "Razorpay",
      role = "Staff Backend Engineer / Platform Lead",
      tier = MarketTier.UNICORN_HIGH_GROWTH,
      seniority = CareerSeniority.STAFF_PRINCIPAL,
      description = """
        Razorpay Payment Gateway Infrastructure is seeking a Staff Backend Engineer.
        Responsibilities:
        - Direct architectural evolution of core merchant settlement and transaction routing microservices processing billions in annualized GMV.
        - Champion 99.999% availability, zero-downtime database schema migrations, and event-driven ledger pipelines.
        - Influence company-wide technical strategy, security compliance (PCI-DSS), and engineering standards.
        Requirements:
        - 7+ years backend engineering in high-throughput transactional systems.
        - Deep expertise in relational/distributed storage, messaging topologies, and fault isolation.
      """.trimIndent()
    ),
    SampleBenchmarkJob(
      company = "Salesforce",
      role = "Lead Solution Strategy & Enterprise Value Consultant",
      tier = MarketTier.ENTERPRISE_SAAS,
      seniority = CareerSeniority.SENIOR,
      description = """
        Salesforce Enterprise Strategy is seeking a Lead Value Consultant.
        Responsibilities:
        - Formulate ROI business cases, digital transformation roadmaps, and value realization models for Fortune 500 CxOs.
        - Collaborate with Strategic Enterprise Account Directors to navigate multi-million dollar annual contract commitments.
        - Build proprietary benchmarking models quantifying labor productivity and automated workflow margin uplift.
        Requirements:
        - 5+ years experience in management consulting (MBB/Big 4) or enterprise SaaS strategy.
        - Outstanding presentation capabilities, executive presence, and financial modeling mastery.
      """.trimIndent()
    )
  )

  fun detectMarketTier(company: String, jobDescription: String): MarketTier {
    val compUpper = company.uppercase()
    val jdUpper = jobDescription.uppercase()

    return when {
      listOf("GOOGLE", "META", "MICROSOFT", "APPLE", "AMAZON", "NETFLIX", "UBER", "STRIPE", "AIRBNB").any { compUpper.contains(it) } -> {
        MarketTier.BIG_TECH_FAANG
      }
      listOf("ZEPTO", "RAZORPAY", "SWIGGY", "ZOMATO", "MEESHO", "CRED", "FLIPKART", "OLA", "BLINKIT").any { compUpper.contains(it) } -> {
        MarketTier.UNICORN_HIGH_GROWTH
      }
      listOf("SALESFORCE", "ADOBE", "SNOWFLAKE", "ATLASSIAN", "ORACLE", "SERVICENOW", "SAP", "WORKDAY").any { compUpper.contains(it) } -> {
        MarketTier.ENTERPRISE_SAAS
      }
      jdUpper.contains("SERIES A") || jdUpper.contains("SERIES B") || jdUpper.contains("SEED") || jdUpper.contains("STEALTH") || compUpper.contains("STARTUP") -> {
        MarketTier.EARLY_STAGE
      }
      jdUpper.contains("ENTERPRISE") || jdUpper.contains("FORTUNE 500") -> {
        MarketTier.ENTERPRISE_SAAS
      }
      else -> MarketTier.UNICORN_HIGH_GROWTH
    }
  }

  fun detectSeniority(roleTitle: String, jobDescription: String, experienceYears: Int = 4): CareerSeniority {
    val rUpper = roleTitle.uppercase()
    val jdUpper = jobDescription.uppercase()

    return when {
      rUpper.contains("STAFF") || rUpper.contains("PRINCIPAL") || rUpper.contains("DIRECTOR") || rUpper.contains("HEAD") || rUpper.contains("L6") || rUpper.contains("IC6") -> {
        CareerSeniority.STAFF_PRINCIPAL
      }
      rUpper.contains("SENIOR") || rUpper.contains("LEAD") || rUpper.contains("ARCHITECT") || rUpper.contains("L5") || rUpper.contains("IC5") || experienceYears >= 5 -> {
        CareerSeniority.SENIOR
      }
      rUpper.contains("MID") || rUpper.contains("SPECIALIST") || rUpper.contains("II") || rUpper.contains("L4") || experienceYears in 2..4 -> {
        CareerSeniority.MID_LEVEL
      }
      else -> CareerSeniority.EARLY_CAREER
    }
  }

  fun analyzeSalaryBenchmark(
    targetRole: String,
    targetCompany: String,
    jobDescription: String,
    userProfile: UserProfile?,
    verifiedFacts: List<VerifiedFact> = emptyList(),
    currentCtcLakhs: Double = 18.0
  ): SalaryBenchmarkAnalysis {
    val tier = detectMarketTier(targetCompany, jobDescription)
    val seniority = detectSeniority(targetRole, jobDescription, if ((userProfile?.careerCapitalScore ?: 80) > 85) 5 else 3)
    val benchmark = MarketCompensationRepository.getBenchmark(tier, seniority)

    val p25Base = benchmark.baseSalaryLakhs * 0.88
    val p50Base = benchmark.baseSalaryLakhs.toDouble()
    val p75Base = benchmark.baseSalaryLakhs * 1.15
    val p90Base = benchmark.baseSalaryLakhs * 1.30

    val p25Total = benchmark.p25TotalCompLakhs.toDouble()
    val p50Total = benchmark.p50TotalCompLakhs.toDouble()
    val p75Total = benchmark.p75TotalCompLakhs.toDouble()
    val p90Total = benchmark.p90TotalCompLakhs.toDouble()

    val signOn = when (tier) {
      MarketTier.BIG_TECH_FAANG -> "₹5L – ₹12L (Lump-sum signing bonus)"
      MarketTier.UNICORN_HIGH_GROWTH -> "₹3L – ₹8L (Bridge / retention bonus)"
      MarketTier.ENTERPRISE_SAAS -> "₹4L – ₹7L (Sign-on cash component)"
      MarketTier.EARLY_STAGE -> "₹2L – ₹4L (Relocation & early joining kicker)"
    }

    // Calculate candidate leverage score (0-100)
    var leverage = 70
    if (userProfile != null) {
      if (userProfile.careerScore > 85) leverage += 6
      if (userProfile.careerCapitalScore > 80) leverage += 5
      if (userProfile.aiLeverageScore > 85) leverage += 5
      if (userProfile.totalActiveOffers > 0) leverage += 8
    }
    if (verifiedFacts.isNotEmpty()) leverage += 4
    leverage = leverage.coerceIn(55, 96)

    val verdict = when {
      leverage >= 85 -> "VERY HIGH LEVERAGE (Strong Market Position)"
      leverage >= 75 -> "STRONG LEVERAGE (Competitive Candidate)"
      leverage >= 65 -> "BALANCED LEVERAGE (Solid Fit)"
      else -> "MODERATE LEVERAGE"
    }

    val marketPoints = listOf(
      "${tier.label} ${seniority.levelCode} Median Base Salary is ₹${p50Base.roundToInt()} Lakhs, with 75th percentile at ₹${p75Base.roundToInt()} Lakhs.",
      "Total Annual Rewards (Base + Performance Bonus + 4-Yr Equity) 75th percentile target is ₹${p75Total.roundToInt()} Lakhs.",
      "Expected 4-year equity valuation: ₹${benchmark.fourYearEquityLakhs.roundToInt()} Lakhs (${benchmark.vestingSchedule}).",
      "Sign-on cash incentive flexibility: $signOn."
    )

    val alignmentHighlights = listOf(
      "JD requires strategic problem solving and cross-functional ownership matching candidate's documented track record.",
      "Candidate's operational velocity directly aligns with ${targetCompany}'s tier performance expectations.",
      "Demonstrated ability to drive quantifiable business outcomes reduces hiring onboarding risk."
    )

    val strengths = verifiedFacts.map { it.claim }.take(3).ifEmpty {
      listOf(
        "Strong analytical and systems execution mindset.",
        "Demonstrated leadership in 0-to-1 operational delivery.",
        "Proven cross-functional stakeholder communication."
      )
    }

    return SalaryBenchmarkAnalysis(
      targetRole = targetRole,
      targetCompany = targetCompany,
      detectedTier = tier,
      detectedSeniority = seniority,
      yearsOfExperience = 4,
      p25BaseLakhs = p25Base,
      p50BaseLakhs = p50Base,
      p75BaseLakhs = p75Base,
      p90BaseLakhs = p90Base,
      p25TotalLakhs = p25Total,
      p50TotalLakhs = p50Total,
      p75TotalLakhs = p75Total,
      p90TotalLakhs = p90Total,
      medianBonusPercent = benchmark.annualBonusPercent,
      fourYearEquityLakhs = benchmark.fourYearEquityLakhs.toDouble(),
      signOnBonusRange = signOn,
      candidateLeverageScore = leverage,
      leverageVerdict = verdict,
      keyMarketDataPoints = marketPoints,
      jdAlignmentHighlights = alignmentHighlights,
      candidateStrengthsCited = strengths
    )
  }

  fun generateDeterministicNegotiationBundle(
    targetRole: String,
    targetCompany: String,
    jobDescription: String,
    userProfile: UserProfile?,
    verifiedFacts: List<VerifiedFact> = emptyList(),
    currentCtcLakhs: Double = 18.0,
    requestedTargetCtcLakhs: Double? = null
  ): SalaryNegotiationBundle {
    val analysis = analyzeSalaryBenchmark(
      targetRole = targetRole,
      targetCompany = targetCompany,
      jobDescription = jobDescription,
      userProfile = userProfile,
      verifiedFacts = verifiedFacts,
      currentCtcLakhs = currentCtcLakhs
    )

    val candidateName = userProfile?.name ?: "Adi"
    val targetAnchorTotal = requestedTargetCtcLakhs ?: analysis.p75TotalLakhs
    val targetBaseAnchor = analysis.p75BaseLakhs
    val walkawayFloor = (analysis.p50TotalLakhs).coerceAtLeast(currentCtcLakhs * 1.30)

    val fmt = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("en-IN"))
    fmt.maximumFractionDigits = 0

    // Scenario 1: Initial Counter (75th Percentile Data-Driven Anchor)
    val scenario1 = NegotiationScriptScenario(
      scenarioKey = "INITIAL_COUNTER",
      title = "🎯 Initial Counter-Offer (75th Percentile Market Anchor)",
      subtitle = "Grounded in verified tier compensation benchmarks and past quantifiable impact",
      subjectLine = "Offer Discussion & Compensation Alignment - $targetRole - $candidateName",
      scriptBody = """
Dear Hiring Team at $targetCompany,

Thank you very much for extending the offer for the $targetRole position. I am energized by the team's mission, the technical scale, and the opportunity to make an immediate impact on $targetCompany's roadmap.

Having thoroughly evaluated the scope of responsibilities—specifically leading strategic cross-functional initiatives and driving unit economic delivery—I am confident in my ability to generate substantial value from day one.

To ensure our agreement reflects both verified market standards for $targetRole in the ${analysis.detectedTier.label} tier and the depth of execution required, I would like to discuss adjusting the compensation structure. Based on current market intelligence:
- The 75th percentile benchmark for professionals in this role stands at ₹${targetBaseAnchor.roundToInt()} Lakhs base salary, with total annualized compensation around ₹${targetAnchorTotal.roundToInt()} Lakhs.
- Given my verified background delivering measurable throughput improvements and cross-functional leadership, I am requesting:
  1. Base Salary: ₹${targetBaseAnchor.roundToInt()} Lakhs per annum
  2. Annual Target Performance Bonus: ${analysis.medianBonusPercent}%
  3. Equity / ESOP Grant: ₹${analysis.fourYearEquityLakhs.roundToInt()} Lakhs over 4 years
  4. One-Time Sign-on Incentive: ₹${if (analysis.detectedTier == MarketTier.BIG_TECH_FAANG) "6" else "4"} Lakhs (to offset transition timing)

If we can reach agreement around these figures, I would be thrilled to sign immediately and decline other ongoing interview processes.

Thank you again for your partnership, and I look forward to your thoughts.

Warm regards,
$candidateName
      """.trimIndent(),
      tacticalPsychologyNotes = "Anchoring at the 75th percentile positions you in the top quartile without exceeding band ceilings. Offering immediate signature creates closing urgency for the recruiter.",
      dataPointsCited = listOf(
        "Tier: ${analysis.detectedTier.label}",
        "75th Percentile Base: ₹${targetBaseAnchor.roundToInt()}L",
        "75th Percentile Total Comp: ₹${targetAnchorTotal.roundToInt()}L",
        "Equity Target: ₹${analysis.fourYearEquityLakhs.roundToInt()}L"
      )
    )

    // Scenario 2: Countering a Lowball / Band Pushback ("We're at the top of our band")
    val scenario2 = NegotiationScriptScenario(
      scenarioKey = "BUDGET_PUSHBACK",
      title = "🛡️ Countering Budget Pushback ('This is our maximum band')",
      subtitle = "Pivots from rigid base salary constraints to flexible sign-on, equity, or performance triggers",
      subjectLine = "Exploring Creative Structuring - $targetRole - $candidateName",
      scriptBody = """
Dear Hiring Team,

I completely understand and respect internal equity constraints and salary band structures at $targetCompany. Maintaining team parity is essential.

Because I am genuinely excited about the work we will do together, I would love to explore flexible ways to bridge the gap between your current offer and my target of ₹${targetAnchorTotal.roundToInt()} Lakhs total first-year package without disrupting your fixed base band:

1. Sign-on / Early Joining Bonus: A one-time sign-on bonus of ₹${if (analysis.detectedTier == MarketTier.BIG_TECH_FAANG) "6" else "4"} Lakhs to offset unvested incentives.
2. Performance Equity Kicker: An additional ₹${(analysis.fourYearEquityLakhs * 0.25).roundToInt()} Lakhs in stock options/RSUs tied to milestone deliverables at the 6-month mark.
3. Accelerated 6-Month Compensation Review: Written agreement for an off-cycle performance evaluation at 6 months with clear KPI criteria for base advancement.

Which of these avenues allows us the most flexibility on your end? I am committed to making this partnership a success.

Best regards,
$candidateName
      """.trimIndent(),
      tacticalPsychologyNotes = "Harvard Negotiation Project 'Multi-Attribute Pivoting': When one variable (base salary) is locked, introduce non-base tradeable chips (sign-on, milestone equity, accelerated review) that draw from different corporate budgets.",
      dataPointsCited = listOf(
        "Sign-on bonus pool flexibility",
        "Performance equity refreshers",
        "6-month accelerated milestone review"
      )
    )

    // Scenario 3: Competing Offer Leverage
    val scenario3 = NegotiationScriptScenario(
      scenarioKey = "COMPETING_OFFER",
      title = "⚡ Competing Offer Leverage (Urgent, High-Respect)",
      subtitle = "Respectfully uses active market alternatives to accelerate compensation approval",
      subjectLine = "Update Regarding Offer & Timeline - $targetRole - $candidateName",
      scriptBody = """
Dear Hiring Team at $targetCompany,

I wanted to provide an update regarding my timeline. As shared earlier, $targetCompany remains my top choice due to the team, the vision, and the specific strategic challenges of the $targetRole mandate.

In the spirit of complete transparency, I have received another competitive offer with a total first-year compensation package of ₹${(targetAnchorTotal * 1.05).roundToInt()} Lakhs. They have requested a decision by the end of this week.

However, if $targetCompany is able to adjust the total compensation package to ₹${targetAnchorTotal.roundToInt()} Lakhs (Base ₹${targetBaseAnchor.roundToInt()}L + standard bonus/equity), I will immediately decline the other offer and commit to $targetCompany today.

Please let me know if we can discuss this briefly over a 10-minute call today.

Warm regards,
$candidateName
      """.trimIndent(),
      tacticalPsychologyNotes = "Creates authentic loss aversion and scarcity. Stating that the target company is still #1 gives the recruiter strong motivation to advocate for an emergency exception with the Compensation Committee.",
      dataPointsCited = listOf(
        "Target Company remains #1 preference",
        "Competing offer at ₹${(targetAnchorTotal * 1.05).roundToInt()}L",
        "Immediate commitment clause if matched"
      )
    )

    // Scenario 4: Phone Recruiter Call Guide
    val phoneGuide = PhoneCallTalkingPoints(
      openingHook = "Thank you so much for the offer! I really enjoyed meeting the team, and I am genuinely excited about the scale and mission at $targetCompany.",
      theTargetAnchorAsk = "I've reviewed the numbers carefully against verified ${analysis.detectedTier.label} benchmarks for $targetRole at $targetCompany. Based on market data showing a median base of ₹${analysis.p50BaseLakhs.roundToInt()}L and 75th percentile at ₹${analysis.p75BaseLakhs.roundToInt()}L, plus the immediate impact I will deliver, I am targeting ₹${targetBaseAnchor.roundToInt()}L base and a total package of ₹${targetAnchorTotal.roundToInt()}L. If we can reach that, I'm ready to sign today.",
      objectionHandlingBudgetCeiling = "I hear you, and I appreciate that base salaries operate within strict bands. If base is capped at your current number, how flexible are we on a one-time sign-on bonus of ₹${if (analysis.detectedTier == MarketTier.BIG_TECH_FAANG) "6" else "4"}L or an enhanced equity grant to bridge the difference?",
      objectionHandlingInternalEquity = "Internal team fairness is crucial, and I want everyone on the team to feel valued. Could we structure this with an accelerated 6-month performance review where hitting our Q1/Q2 throughput targets triggers the adjustment?",
      closingCommitment = "If you can get approval for this structure from the Compensation Committee, you have my word that I will sign the offer letter within 24 hours."
    )

    val rules = listOf(
      "Rule 1: Always express high enthusiasm for the team and role before mentioning any numbers.",
      "Rule 2: Never give a single rigid number; anchor with a high-conviction 75th percentile target backed by market percentiles.",
      "Rule 3: If base salary hits a ceiling, pivot immediately to sign-on cash bonus, equity vesting acceleration, or accelerated reviews.",
      "Rule 4: Offer unconditional closure ('If we can meet at X, I will sign within 24 hours and close all other processes')."
    )

    return SalaryNegotiationBundle(
      targetRole = targetRole,
      targetCompany = targetCompany,
      candidateCurrentCtcLakhs = currentCtcLakhs,
      recommendedTargetAnchorLakhs = targetAnchorTotal,
      walkawayFloorLakhs = walkawayFloor,
      benchmarkAnalysis = analysis,
      scenarios = listOf(scenario1, scenario2, scenario3),
      phoneCallGuide = phoneGuide,
      strategicNegotiationRules = rules,
      isAiGenerated = false
    )
  }
}
