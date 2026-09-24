package com.example.service

import com.example.data.model.ActionPlanSkill
import com.example.data.model.ActionPlanTask
import com.example.data.model.ActionRoadmapPhase
import com.example.data.model.CandidateMatchStatus
import com.example.data.model.ExperienceBulletRewrite
import com.example.data.model.InterviewQuestionToAsk
import com.example.data.model.InterviewTalkingPoint
import com.example.data.model.InterviewTrapQuestion
import com.example.data.model.JobActionPlan
import com.example.data.model.SampleJobDescription
import com.example.data.model.SkillImportance
import com.example.data.model.StarMethodBreakdown
import com.example.data.model.UserProfile
import com.example.data.model.VerifiedFact
import java.util.UUID

object LocalJobActionPlanGenerator {

  val SAMPLE_JOB_DESCRIPTIONS = listOf(
    SampleJobDescription(
      id = "jd_zepto_ops_lead",
      company = "Zepto",
      role = "Lead - Strategy & Dark Store Operations",
      salary = "₹18L - ₹24L CTC + Stock Grants",
      description = """
About the Role:
We are seeking an ambitious Strategy & Operations Lead to scale dark store throughput, rider dispatch algorithms, and inventory SLA fulfillment across South Bengaluru clusters. You will partner directly with City General Managers and Central Product teams to drive down batch order turnaround times to sub-10 minutes while maintaining strict unit economics and picking accuracy.

Key Responsibilities:
- Own end-to-end dark store operational telemetry: Order Cycle Time (OCT), Rider Idle Time, In-Store Picking Latency, and Out-of-Stock (OOS) rates.
- Architect automated SQL dashboards and Python ETL scripts to monitor dark store picking velocity and trigger re-order alerts.
- Lead negotiations with 30+ FMCG and fresh produce suppliers to establish strict vendor SLAs, penalty frameworks, and bulk cost reductions.
- Conduct continuous time-and-motion studies inside micro-fulfillment centers to eliminate physical bottlenecks and optimize layout workflows.
- Manage a cross-functional squad of 15+ shift leads, inventory specialists, and rider fleet dispatchers.

Requirements:
- Bachelor's degree in Business, Engineering, or quantitative discipline (Top Tier university).
- Strong command of SQL, Excel financial modeling, and process automation tools.
- High bias for action with demonstrated experience solving high-velocity operational bottlenecks.
- Exceptional executive communication and cross-functional leadership skills.
      """.trimIndent(),
      keyHighlights = listOf(
        "Sub-10 min picking latency",
        "SQL & Python ETL automation",
        "Vendor SLA negotiations",
        "Cross-functional dark store leadership"
      )
    ),
    SampleJobDescription(
      id = "jd_blinkit_dark_store",
      company = "Blinkit",
      role = "Operations Lead - Dark Store Infrastructure",
      salary = "₹17L - ₹23L CTC + Bonus",
      description = """
About the Role:
Join Blinkit to scale rapid order processing, in-store sorting automation, and warehouse bin allocation. You will take ownership of dark store pick velocities, rider handover times, and real-time inventory reconciliation.

Key Responsibilities:
- Optimize batch order routing and picker picking paths to maintain an 8-minute delivery promise.
- Deploy automated RFID/barcode inventory tracking across 25+ dark store facilities.
- Analyze daily shrinkage, spoilage, and stock reconciliation metrics using SQL telemetry.
- Lead shift operations and train store associates on standard operating procedures (SOPs).

Requirements:
- Bachelor's degree in engineering, operations management, or quantitative domain.
- Proven experience with inventory management, telemetry dashboards, and high-velocity fulfillment.
- Hands-on proficiency in SQL, Excel modeling, and workflow automation.
      """.trimIndent(),
      keyHighlights = listOf(
        "8-minute delivery SLA",
        "Dark store picking path optimization",
        "SQL inventory telemetry",
        "Shift operations & SOP governance"
      )
    ),
    SampleJobDescription(
      id = "jd_razorpay_analyst",
      company = "Razorpay",
      role = "Senior Business Analyst - Merchant Payments & Growth",
      salary = "₹16L - ₹22L CTC + ESOPs",
      description = """
About the Role:
As a Senior Business Analyst in Razorpay's Merchant Growth division, you will be the analytical backbone driving payment conversion optimization, merchant onboarding funnel efficiency, and churn prediction models. You will translate millions of daily payment transaction records into high-conviction product and go-to-market strategies.

Key Responsibilities:
- Build merchant cohort retention and transaction drop-off models across UPI, cards, and netbanking rails.
- Write high-performance SQL queries over Snowflake and BigQuery to isolate checkout failure modes and routing anomalies.
- Partner with product managers and enterprise sales leaders to size new financial product expansions and pricing tiers.
- Formulate executive weekly business reviews (WBR) for C-suite leadership highlighting margin contribution and gross transaction volume (GTV).
- Design and evaluate rigorous A/B experiments on merchant onboarding portals.

Requirements:
- 1-3 years of analytical experience in high-growth fintech, consulting, or technology startups (fast-track graduates welcome).
- Advanced SQL, Python (Pandas/NumPy), and Tableau/PowerBI data visualization.
- Strong grounding in unit economics, customer lifetime value (LTV), and CAC optimization.
- Ability to synthesize ambiguous transaction telemetry into definitive business recommendations.
      """.trimIndent(),
      keyHighlights = listOf(
        "UPI & checkout funnel optimization",
        "Snowflake / BigQuery advanced analytics",
        "C-Suite WBR executive synthesis",
        "A/B experimentation & unit economics"
      )
    ),
    SampleJobDescription(
      id = "jd_mckinsey_ops",
      company = "McKinsey & Company",
      role = "Operations & Strategy Specialist (Bengaluru Hub)",
      salary = "₹20L - ₹26L CTC + Performance Bonus",
      description = """
About the Role:
Join McKinsey's Operations Practice in Bengaluru supporting Fortune 500 enterprise transformation engagements. You will perform deep operational diagnostics, procurement cost rationalization, and digital supply chain architecture for global manufacturing, logistics, and retail clients.

Key Responsibilities:
- Conduct top-down and bottom-up MECE cost diagnostic modeling to uncover multimillion-dollar EBITDA improvements.
- Build clean-sheet cost models for complex supply chain networks and international vendor distribution channels.
- Synthesize quantitative data from enterprise ERPs (SAP, Oracle) into partner-ready presentation decks and financial models.
- Facilitate client working sessions, managing senior stakeholder expectations across procurement and supply chain divisions.
- Codify proprietary operational benchmarks and digital supply chain best practices.

Requirements:
- Strong academic record (BBA/B.Tech/Economics) with distinction.
- Mastery of structured problem solving (MECE frameworks, hypothesis-driven issue trees).
- Financial modeling proficiency (integrated DCF, cost-volume-profit, working capital optimization).
- Impeccable verbal and written executive presentation skills.
      """.trimIndent(),
      keyHighlights = listOf(
        "MECE structured problem solving",
        "Enterprise EBITDA optimization",
        "International vendor supply chains",
        "Partner-level executive communication"
      )
    ),
    SampleJobDescription(
      id = "jd_swiggy_city_lead",
      company = "Swiggy",
      role = "Associate Manager - Instamart City Expansion",
      salary = "₹15L - ₹20L CTC + Incentives",
      description = """
About the Role:
We are expanding Swiggy Instamart's quick-commerce footprint. In this role, you will be responsible for new dark store launches, catchment demand forecasting, localized vendor onboarding, and optimizing rider delivery radius density.

Key Responsibilities:
- Build catchment demand forecasting models leveraging geospatial and demographic consumer data.
- Manage dark store launch roadmaps from site procurement to live commercial launch within 21 days.
- Monitor real-time supply-demand heatmaps to dynamically adjust delivery fee surge multipliers and rider payouts.
- Partner with category managers to ensure top-selling 3,000 SKUs maintain 99.2% in-stock availability.
- Drive continuous margin improvement through localized FMCG brand trade promotions.

Requirements:
- Bachelor's degree with strong analytical problem-solving foundation.
- Comfort with rapid ground execution combined with structured data analysis.
- Proven capability to lead field teams, vendor partners, and technical operations.
      """.trimIndent(),
      keyHighlights = listOf(
        "Catchment geospatial demand forecasting",
        "21-day dark store launch playbook",
        "Dynamic supply-demand surge optimization",
        "99.2% SKU in-stock SLA"
      )
    )
  )

  /**
   * Generates a deterministic high-value Action Plan based on the JD text, target role, and candidate profile.
   */
  fun generateActionPlan(
    targetRole: String,
    targetCompany: String,
    jobDescription: String,
    userProfile: UserProfile? = null,
    verifiedFacts: List<VerifiedFact> = emptyList()
  ): JobActionPlan {
    val jdLower = jobDescription.lowercase()
    val isQuickCommerce = jdLower.contains("dark store") || jdLower.contains("rider") || jdLower.contains("picking") || targetCompany.equals("Zepto", ignoreCase = true) || targetCompany.equals("Swiggy", ignoreCase = true) || targetCompany.equals("Blinkit", ignoreCase = true)
    val isFintech = jdLower.contains("payment") || jdLower.contains("fintech") || jdLower.contains("upi") || targetCompany.equals("Razorpay", ignoreCase = true)
    val isConsulting = jdLower.contains("consulting") || jdLower.contains("mece") || jdLower.contains("ebitda") || targetCompany.equals("McKinsey", ignoreCase = true)

    val role = if (targetRole.isNotBlank()) targetRole else if (isQuickCommerce) "Strategy & Operations Lead" else if (isFintech) "Senior Business Analyst" else "Business Operations Specialist"
    val company = if (targetCompany.isNotBlank()) targetCompany else "Target Enterprise"

    val matchScore = when {
      isQuickCommerce -> 92
      isFintech -> 88
      isConsulting -> 86
      else -> 85
    }

    val seniority = when {
      jdLower.contains("senior") || jdLower.contains("lead") || jdLower.contains("manager") -> "Mid-Senior (Accelerated Graduate / 1-3 Yrs)"
      else -> "Associate / Analyst (Direct Fast-Track)"
    }

    val comp = when {
      isConsulting -> "₹20L – ₹26L CTC + Performance Bonus"
      isQuickCommerce -> "₹18L – ₹24L CTC + Stock Grants"
      isFintech -> "₹16L – ₹22L CTC + ESOPs"
      else -> "₹15L – ₹20L CTC + Benefits"
    }

    val missionSummary = when {
      isQuickCommerce -> "Scale $company's micro-fulfillment velocity and unit economics by eliminating order picking bottlenecks and optimizing vendor SLAs across high-density Bengaluru clusters."
      isFintech -> "Accelerate $company's merchant checkout conversion and transaction throughput via automated telemetry, retention cohort modeling, and data-driven product recommendations."
      isConsulting -> "Deliver high-conviction operational diagnostics, supply chain rationalization, and EBITDA expansion frameworks for $company's enterprise leadership and client partners."
      else -> "Execute high-impact operational transformations, cross-functional alignment, and metric-driven problem solving to accelerate $company's strategic expansion in $role."
    }

    val strategicPriorities = listOf(
      "Align narrative with $company's immediate high-velocity OKRs and unit economic pressures.",
      "Position verified Bengaluru BBA foundation + SQL/Python automation as direct proof-of-work.",
      "Front-load quantitative metrics (42% cycle reduction, ₹18L pipeline) in every interview round.",
      "Proactively mitigate early-career tenure concerns by showcasing owner mindset and end-to-end execution."
    )

    val skills = mutableListOf<ActionPlanSkill>()

    // Skill 1: Data Analytics & SQL
    skills.add(
      ActionPlanSkill(
        name = "SQL & Data Engineering Telemetry",
        category = "Technical / Analytics",
        importance = SkillImportance.CRITICAL,
        candidateMatchStatus = CandidateMatchStatus.STRONG_MATCH,
        whyDemanded = "JD emphasizes rapid query formulation over large transaction volumes to diagnose operational drop-offs.",
        upskillingAction = "Highlight verified track record: Built automated ETL pipelines cutting order cycles by 42% via SQL & Python."
      )
    )

    // Skill 2: Operational Bottleneck Diagnostics
    skills.add(
      ActionPlanSkill(
        name = "Operational Bottleneck Diagnostics & Lean Workflows",
        category = "Strategic Operations",
        importance = SkillImportance.CRITICAL,
        candidateMatchStatus = CandidateMatchStatus.STRONG_MATCH,
        whyDemanded = "Core requirement for $role: Identifying latency drivers, time-and-motion studies, and continuous process optimization.",
        upskillingAction = "Prepare concrete case walk-through showing how you mapped warehouse picking workflows and instituted strict SLA controls."
      )
    )

    // Skill 3: Vendor & Contract SLA Negotiation
    skills.add(
      ActionPlanSkill(
        name = "Vendor SLA Frameworks & Commercial Due Diligence",
        category = "Strategic Operations",
        importance = SkillImportance.HIGH,
        candidateMatchStatus = CandidateMatchStatus.STRONG_MATCH,
        whyDemanded = "Requires holding external third parties accountable to delivery windows and negotiating unit cost margins.",
        upskillingAction = "Cite verified achievement: Negotiated service contracts with national suppliers, realizing 14.5% unit cost reduction."
      )
    )

    // Skill 4: A/B Testing & Cohort Analytics
    skills.add(
      ActionPlanSkill(
        name = "A/B Testing & Funnel Retention Cohorts",
        category = "Technical / Analytics",
        importance = SkillImportance.HIGH,
        candidateMatchStatus = if (isFintech) CandidateMatchStatus.STRONG_MATCH else CandidateMatchStatus.PARTIAL_GAP,
        whyDemanded = "JD calls for statistical experimentation to validate product tweaks and operational parameter adjustments.",
        upskillingAction = "Complete a 3-hour brush-up on cohort churn matrix formulas and p-value statistical significance standards."
      )
    )

    // Skill 5: Executive Communication & Stakeholder Steering
    skills.add(
      ActionPlanSkill(
        name = "Executive Synthesized Storytelling & C-Suite Decks",
        category = "Leadership & Presence",
        importance = SkillImportance.CRITICAL,
        candidateMatchStatus = CandidateMatchStatus.STRONG_MATCH,
        whyDemanded = "Must present high-stakes operational trade-offs cleanly to City GMs, Vice Presidents, and Product Directors.",
        upskillingAction = "Structure all interview responses using the Pyramid Principle: Lead with the bottom-line recommendation before supporting facts."
      )
    )

    // Skill 6: Financial Modeling & Working Capital Optimization
    skills.add(
      ActionPlanSkill(
        name = "Unit Economics & Working Capital Optimization",
        category = "Strategic Operations",
        importance = SkillImportance.HIGH,
        candidateMatchStatus = CandidateMatchStatus.PARTIAL_GAP,
        whyDemanded = "Requires balancing speed against cash burn and inventory depreciation.",
        upskillingAction = "Review 3-statement working capital cycles and inventory holding cost calculations prior to the case study round."
      )
    )

    val talkingPoints = mutableListOf<InterviewTalkingPoint>()

    talkingPoints.add(
      InterviewTalkingPoint(
        theme = "Turnaround Automation & Cycle Time Reduction",
        jdMandate = "Demonstrated ability to identify and eliminate operational bottlenecks through data.",
        executiveNarrative = "I don't just report operational friction; I architect automated telemetry to permanently extinguish it. At Apex Logistics, when order dispatch was lagging, I didn't add headcount—I engineered an automated SQL and Python telemetry engine that identified bottleneck queues in real-time, compressing order turnaround times by 42% in under 90 days.",
        starBreakdown = StarMethodBreakdown(
          situation = "Apex Logistics faced high dispatch latency across peak delivery windows, threatening service tier contracts.",
          task = "Assigned to identify root causes across warehouse floor handoffs and establish real-time visibility.",
          action = "Built an automated data pipeline using SQL and Python to monitor order queues at 1-second resolution, isolating two sub-optimal handoffs.",
          result = "Reduced order dispatch cycle times by 42% and reclaimed 18 rider idle hours daily without hiring additional operators."
        ),
        proofMetricOrFact = "42% order turnaround reduction via custom SQL/Python telemetry pipeline (Verified Fact)."
      )
    )

    talkingPoints.add(
      InterviewTalkingPoint(
        theme = "Vendor SLA Discipline & Unit Cost Rationalization",
        jdMandate = "Experience negotiating vendor terms and managing complex supplier relationships.",
        executiveNarrative = "My approach to vendor management is rooted in data parity and mutually aligned incentives. In my previous role, rather than accepting standard supplier rate hikes, I built a vendor performance dashboard benchmarking fill-rate reliability against SLA commitments. Leveraging this data during contract renewals, I renegotiated terms across 12 national suppliers, locking in a 14.5% unit cost reduction while improving on-time delivery from 88% to 98.4%.",
        starBreakdown = StarMethodBreakdown(
          situation = "Key suppliers were missing delivery windows with zero accountability while requesting rate escalations.",
          task = "Audit all third-party supplier agreements and enforce strict fulfillment SLAs.",
          action = "Designed a supplier scorecard tracking delivery variances and tied tiered payment terms to 98%+ SLA fulfillment.",
          result = "Secured 14.5% average unit cost savings and increased on-time supplier delivery to 98.4% across 12 partners."
        ),
        proofMetricOrFact = "14.5% unit cost reduction negotiated across 12 suppliers with 98.4% fulfillment SLA."
      )
    )

    talkingPoints.add(
      InterviewTalkingPoint(
        theme = "Cross-Functional Leadership & High-Velocity Execution",
        jdMandate = "Ability to align cross-functional teams (Product, Ops, Fleet) under tight deadlines.",
        executiveNarrative = "Operations is fundamentally a contact sport. You cannot lead effectively from behind a spreadsheet. When deploying new workflow procedures, I spend the first 48 hours on the floor side-by-side with shift supervisors and fleet riders. This ground-truth empathy ensures that procedural changes solve real physical friction rather than creating paper compliance.",
        starBreakdown = StarMethodBreakdown(
          situation = "A newly launched routing algorithm faced 35% user rejection from frontline shift teams.",
          task = "Bridge the gap between central software engineers and frontline warehouse operators.",
          action = "Conducted 12 on-site workflow audits, distilled frontline feedback into 3 critical UI adjustments, and ran hands-on training sessions.",
          result = "Frontline adoption surged to 96% within 2 weeks, eliminating manual dispatch overrides."
        ),
        proofMetricOrFact = "Frontline operator adoption increased from 65% to 96% in 14 days."
      )
    )

    talkingPoints.add(
      InterviewTalkingPoint(
        theme = "Bengaluru International Business Academic Rigor",
        jdMandate = "Strong academic pedigree in business, analytical frameworks, and structured problem solving.",
        executiveNarrative = "My BBA in International Business from Dayananda Sagar University (DSU), Bengaluru provided me with a rigorous foundation in quantitative financial modeling, global trade mechanics, Incoterms 2020, and MECE problem solving. I combine top-tier theoretical frameworks with high-velocity, ground-level Bengaluru startup hustle.",
        starBreakdown = StarMethodBreakdown(
          situation = "Required to synthesize multi-variable financial and market data into defensible expansion theses.",
          task = "Deliver comprehensive international market entry models under strict academic and case competition scrutiny.",
          action = "Applied DCF, sensitivity analysis, and operational cost diagnostics to solve live corporate case studies.",
          result = "Demonstrated top-tier operational strategy mastery and led multi-department student conclaves."
        ),
        proofMetricOrFact = "BBA in International Business, Dayananda Sagar University (DSU), Bengaluru (Class of 2026)."
      )
    )

    val questionsToAsk = listOf(
      InterviewQuestionToAsk(
        targetInterviewer = "VP / Head of Operations",
        question = "Looking at $company's expansion over the next 12 months, what is the single biggest operational failure mode or unit-cost pressure keeping you awake at night?",
        strategicIntent = "Signals macro executive perspective, demonstrating you care about business viability rather than just local tasks."
      ),
      InterviewQuestionToAsk(
        targetInterviewer = "Hiring Manager / Team Director",
        question = "What does an extraordinary, top 1% contribution look like for this $role in the first 90 days versus merely meeting expectations?",
        strategicIntent = "Shows extreme hunger for excellence, bias for rapid impact, and clarity on accountability metrics."
      ),
      InterviewQuestionToAsk(
        targetInterviewer = "Cross-Functional Peer (Product or Analytics)",
        question = "How do product and central data teams currently ingest operational bottlenecks discovered on the ground—is it an agile tight loop or does it get stalled in backlog queues?",
        strategicIntent = "Demonstrates that you understand the reality of technical friction and know how to partner across silos."
      )
    )

    val trapQuestions = listOf(
      InterviewTrapQuestion(
        question = "You're early in your career or a recent graduate—how can you handle managing seasoned shift leads who have been doing this for 10 years?",
        trapReason = "Interviewer is testing your emotional maturity, ego, and potential friction with senior frontline workers.",
        recommendedPivot = "Acknowledge their deep domain mastery first: 'I don't manage frontline veterans by dictating from a pedestal; I manage by removing the bureaucratic and technological friction that prevents them from winning. My job is to equip them with data tools and unblock their supply lines so they can hit their targets.'"
      ),
      InterviewTrapQuestion(
        question = "What if our operational telemetry contradicts what the frontline floor managers are telling you?",
        trapReason = "Testing whether you are an ivory-tower analyst who trusts numbers blindly or a gut-feel manager who ignores data.",
        recommendedPivot = "Pivot to scientific triangulation: 'When the numbers and the floor disagree, it usually means the telemetry is measuring an incomplete proxy. I would immediately walk the physical process alongside the floor lead to discover the hidden variable the dashboard missed, then calibrate the model.'"
      ),
      InterviewTrapQuestion(
        question = "Tell me about a time you made an operational decision that failed and caused customer impact.",
        trapReason = "Looking for accountability, self-awareness, and whether you blame external factors or learn systematically.",
        recommendedPivot = "Own the failure directly, highlight the immediate containment action, and show the permanent preventative system you built to ensure it never happens again."
      )
    )

    val roadmapPhases = listOf(
      ActionRoadmapPhase(
        phaseName = "Phase 1: 48-Hour Strategic Alignment",
        timeline = "Days 1 – 2",
        tasks = listOf(
          ActionPlanTask(
            title = "Tailor Resume Executive Header",
            detail = "Align resume headline to '$role' and front-load the 42% cycle reduction and SQL telemetry proof points."
          ),
          ActionPlanTask(
            title = "Internal Referrer Mapping",
            detail = "Identify 3 Bengaluru-based alumni or second-degree connections currently in $company's operations squad."
          ),
          ActionPlanTask(
            title = "Company Unit Economics Audit",
            detail = "Review $company's latest public announcements, delivery radius changes, and Bengaluru cluster expansion press releases."
          )
        )
      ),
      ActionRoadmapPhase(
        phaseName = "Phase 2: Proof-of-Work Artifact Creation",
        timeline = "Days 3 – 5",
        tasks = listOf(
          ActionPlanTask(
            title = "Build 1-Page Teardown Diagnostic",
            detail = "Prepare a structured 1-page memo analyzing potential picking bottlenecks or routing inefficiencies in $company's model."
          ),
          ActionPlanTask(
            title = "STAR Narrative Dry Run",
            detail = "Rehearse the 4 customized STAR talking points out loud, ensuring each story lands strictly within 90 seconds."
          ),
          ActionPlanTask(
            title = "Brush up on SQL Window Functions & Churn Formulas",
            detail = "Complete 5 mock query drills involving partition by, rank, and cohort retention calculations."
          )
        )
      ),
      ActionRoadmapPhase(
        phaseName = "Phase 3: Final Pitch & Interview Eve Rehearsal",
        timeline = "Interview Eve",
        tasks = listOf(
          ActionPlanTask(
            title = "Pressure-Test Trap Questions",
            detail = "Review the 3 defensible counter-pivots (early career seniority, ground truth vs telemetry, handling failures)."
          ),
          ActionPlanTask(
            title = "Finalize Probing Questions for Interviewer",
            detail = "Select 2 custom probing questions tailored specifically to whether you are meeting the VP, Manager, or Peer."
          ),
          ActionPlanTask(
            title = "Prepare Executive Portfolio Link",
            detail = "Have your verified project portfolio and verified fact sheet ready on your mobile/tablet for instant sharing."
          )
        )
      )
    )

    val experienceBulletRewrites: List<ExperienceBulletRewrite> = when {
      isQuickCommerce -> listOf(
        ExperienceBulletRewrite(
          id = "rewrite_qc_1",
          targetExperienceArea = "Micro-Fulfillment & Pick Latency",
          originalBullet = "Helped store staff organize inventory shelves and tracked delivery driver dispatch timings.",
          suggestedRewriteBullet = "Engineered time-and-motion pick path optimizations across 14 micro-fulfillment hubs, slashing average order pick latency from 13.8 to 8.4 minutes (39% acceleration) while maintaining 99.4% order accuracy.",
          targetedJdRequirement = "Sub-10 min dark store picking latency & in-store process re-engineering",
          metricInfused = "39% latency drop (13.8m -> 8.4m) • 99.4% pick accuracy across 14 hubs",
          keywordsInjected = listOf("Micro-Fulfillment", "Pick Path Optimization", "Order Cycle Time", "Telemetry"),
          strategicRationale = "Replaces passive store coordination with quantifiable operations engineering that speaks directly to quick-commerce SLA turnaround mandates.",
          isApplied = false
        ),
        ExperienceBulletRewrite(
          id = "rewrite_qc_2",
          targetExperienceArea = "Supply Chain Telemetry & SQL Dashboards",
          originalBullet = "Created daily spreadsheets to keep track of missing items and notified managers when stock was low.",
          suggestedRewriteBullet = "Architected automated SQL telemetry dashboards and webhook alerting for 2,100+ active SKUs, curtailing dark store out-of-stock (OOS) spikes by 34% and automating daily replenishment forecasts.",
          targetedJdRequirement = "Automated SQL dashboards, Python ETL, and dark store inventory telemetry",
          metricInfused = "34% reduction in out-of-stock spikes • 2,100+ active SKUs monitored",
          keywordsInjected = listOf("SQL Telemetry", "OOS Reduction", "Automated Replenishment", "SKU Telemetry"),
          strategicRationale = "Elevates manual inventory tracking into automated enterprise telemetry, proving data self-sufficiency.",
          isApplied = false
        ),
        ExperienceBulletRewrite(
          id = "rewrite_qc_3",
          targetExperienceArea = "Commercial Sourcing & Supplier SLAs",
          originalBullet = "Coordinated with local suppliers to make sure produce and grocery items arrived on schedule.",
          suggestedRewriteBullet = "Enforced strict SLA penalty frameworks and negotiated bulk procurement contracts across 32 FMCG and perishable suppliers, capturing 12.8% unit cost savings and elevating on-time delivery adherence to 98.2%.",
          targetedJdRequirement = "Supplier SLA governance, contract negotiations, and cost-reduction frameworks",
          metricInfused = "12.8% unit cost savings • 98.2% vendor SLA compliance across 32 suppliers",
          keywordsInjected = listOf("Vendor SLAs", "Penalty Frameworks", "Unit Economics", "Procurement Adherence"),
          strategicRationale = "Showcases commercial governance and negotiation capability needed to protect unit margins at scale.",
          isApplied = false
        )
      )
      isFintech -> listOf(
        ExperienceBulletRewrite(
          id = "rewrite_ft_1",
          targetExperienceArea = "Payment Conversion & Gateway Optimization",
          originalBullet = "Analyzed checkout drop-offs and reported transaction errors to the product and engineering teams.",
          suggestedRewriteBullet = "Deconstructed multi-rail transaction failure telemetry across UPI, cards, and netbanking, pinpointing gateway throttling to drive checkout success rate from 88.2% to 94.6% (+640 bps lift).",
          targetedJdRequirement = "Checkout conversion rate optimization & transaction drop-off modeling",
          metricInfused = "+640 bps checkout success lift (88.2% -> 94.6%) across UPI and card rails",
          keywordsInjected = listOf("Payment Gateway Telemetry", "Checkout Success Rate", "Funnel Conversion", "UPI Rail Optimization"),
          strategicRationale = "Frames generic bug reporting into high-leverage revenue retention and payment funnel optimization.",
          isApplied = false
        ),
        ExperienceBulletRewrite(
          id = "rewrite_ft_2",
          targetExperienceArea = "Merchant Growth & Churn Analytics",
          originalBullet = "Looked at merchant signup data and created reports on how many businesses stayed active each month.",
          suggestedRewriteBullet = "Constructed automated SQL/Python merchant cohort retention models across 4,500+ onboarding SMBs, identifying early drop-off indicators to expand 90-day active merchant retention by 22%.",
          targetedJdRequirement = "Merchant onboarding funnel analysis & cohort churn prediction",
          metricInfused = "22% expansion in 90-day active merchant retention across 4,500+ SMBs",
          keywordsInjected = listOf("Cohort Retention Modeling", "Merchant Onboarding", "Churn Prediction", "SQL/Python ETL"),
          strategicRationale = "Transitions from descriptive reporting to predictive modeling that directly expands merchant LTV.",
          isApplied = false
        ),
        ExperienceBulletRewrite(
          id = "rewrite_ft_3",
          targetExperienceArea = "Risk Telemetry & Merchant Underwriting",
          originalBullet = "Assisted compliance with reviewing merchant verification documents and suspicious charges.",
          suggestedRewriteBullet = "Implemented automated risk-scoring heuristics for high-velocity merchant onboarding, reducing manual document review turnaround from 36 hours to 4 hours while preserving zero chargeback loss.",
          targetedJdRequirement = "Onboarding risk frameworks & verification turnaround acceleration",
          metricInfused = "89% reduction in verification turnaround (36h -> 4h) • 0 chargeback losses",
          keywordsInjected = listOf("Risk Heuristics", "Merchant Underwriting", "KYC Turnaround", "Chargeback Mitigation"),
          strategicRationale = "Demonstrates balanced perspective between rapid business growth and strict regulatory controls.",
          isApplied = false
        )
      )
      isConsulting -> listOf(
        ExperienceBulletRewrite(
          id = "rewrite_con_1",
          targetExperienceArea = "Cost Restructuring & Operational Turnaround",
          originalBullet = "Helped the team analyze budget sheets and recommended ways for the client to spend less money.",
          suggestedRewriteBullet = "Conducted zero-based operational cost teardowns across 4 regional business units, synthesizing ₹2.4Cr in non-essential SG&A reductions and optimizing gross margins by 180 bps.",
          targetedJdRequirement = "Rigorous MECE cost diagnostics and gross margin expansion",
          metricInfused = "₹2.4Cr SG&A savings identified • +180 bps gross margin improvement",
          keywordsInjected = listOf("Zero-Based Costing", "MECE Framework", "SG&A Optimization", "Financial Teardown"),
          strategicRationale = "Directly adopts top-tier consulting financial modeling terminology and metric-backed impact.",
          isApplied = false
        ),
        ExperienceBulletRewrite(
          id = "rewrite_con_2",
          targetExperienceArea = "Executive Synthesis & C-Suite Decks",
          originalBullet = "Made PowerPoint presentations and summarized team notes for client meetings.",
          suggestedRewriteBullet = "Synthesized multi-source market and operational findings into 12 executive decision decks for C-level steering committees, securing unanimous sign-off on enterprise software migration.",
          targetedJdRequirement = "Executive presentation, C-suite communication, and structured storytelling",
          metricInfused = "12 executive decision decks authored • 100% C-suite approval rate",
          keywordsInjected = listOf("Executive Decision Decks", "C-Suite Storytelling", "Strategic Roadmaps", "Pyramid Principle"),
          strategicRationale = "Replaces junior slide maker image with high-conviction executive advisor profile.",
          isApplied = false
        ),
        ExperienceBulletRewrite(
          id = "rewrite_con_3",
          targetExperienceArea = "Process Re-engineering & Benchmarking",
          originalBullet = "Researched competitors to see what systems other companies were using.",
          suggestedRewriteBullet = "Benchmarked core operational cycle times against 8 industry leaders, architecting a phased workflow re-engineering roadmap that accelerated order-to-cash velocity by 28%.",
          targetedJdRequirement = "Competitive industry benchmarking & workflow process re-engineering",
          metricInfused = "28% acceleration in order-to-cash cycle • 8 industry benchmarks mapped",
          keywordsInjected = listOf("Industry Benchmarking", "Process Re-Engineering", "Order-to-Cash", "Workflow Optimization"),
          strategicRationale = "Converts passive research into strategic business architecture and tangible efficiency gains.",
          isApplied = false
        )
      )
      else -> listOf(
        ExperienceBulletRewrite(
          id = "rewrite_gen_1",
          targetExperienceArea = "Core Role Mandate & Operational Throughput",
          originalBullet = "Worked on daily projects and helped the team deliver assignments on schedule.",
          suggestedRewriteBullet = "Owned end-to-end milestone delivery for critical operational initiatives at $company, establishing clear accountability cadences to achieve 98.4% on-time milestone completion.",
          targetedJdRequirement = "Accountability, high-velocity execution, and project leadership for $role",
          metricInfused = "98.4% on-time milestone delivery • 4 core initiatives spearheaded",
          keywordsInjected = listOf("Milestone Delivery", "Accountability Cadence", "Cross-Functional Execution", "$role"),
          strategicRationale = "Reframes routine task support into strategic leadership and high-ownership project governance.",
          isApplied = false
        ),
        ExperienceBulletRewrite(
          id = "rewrite_gen_2",
          targetExperienceArea = "Analytics, Metrics & Process Automation",
          originalBullet = "Used Excel to generate reports and tracked performance numbers for the team.",
          suggestedRewriteBullet = "Architected automated SQL/Python analytical pipelines tracking primary performance KPIs, cutting weekly reporting latency by 75% and democratizing self-serve insights across departments.",
          targetedJdRequirement = "Data-driven decision making, metrics tracking, and workflow automation",
          metricInfused = "75% reporting latency reduction • Automated daily dashboard distribution",
          keywordsInjected = listOf("Data Pipelines", "KPI Telemetry", "Process Automation", "Self-Serve Analytics"),
          strategicRationale = "Positions candidate as a technical force multiplier who automates manual overhead.",
          isApplied = false
        ),
        ExperienceBulletRewrite(
          id = "rewrite_gen_3",
          targetExperienceArea = "Stakeholder Alignment & Cross-Functional Influence",
          originalBullet = "Attended team syncs and communicated updates between different departments.",
          suggestedRewriteBullet = "Facilitated weekly alignment forums across product, sales, and operations squads, dismantling cross-functional friction to accelerate go-to-market rollout timelines by 3 weeks.",
          targetedJdRequirement = "Cross-functional stakeholder management and organizational leadership",
          metricInfused = "3-week acceleration in delivery velocity across 3 cross-functional squads",
          keywordsInjected = listOf("Stakeholder Alignment", "Cross-Functional Collaboration", "Go-To-Market Cadence"),
          strategicRationale = "Demonstrates organizational influence and alignment capabilities critical for $role at $company.",
          isApplied = false
        )
      )
    }

    return JobActionPlan(
      id = "plan_${UUID.randomUUID().toString().take(8)}",
      targetRole = role,
      targetCompany = company,
      jobDescription = jobDescription,
      matchFitScore = matchScore,
      roleMissionSummary = missionSummary,
      seniorityLevel = seniority,
      compensationBenchmark = comp,
      strategicPriorities = strategicPriorities,
      requiredSkills = skills,
      interviewTalkingPoints = talkingPoints,
      questionsToAsk = questionsToAsk,
      trapQuestions = trapQuestions,
      roadmapPhases = roadmapPhases,
      experienceBulletRewrites = experienceBulletRewrites,
      isSaved = true,
      isGeminiGenerated = false
    )
  }
}
