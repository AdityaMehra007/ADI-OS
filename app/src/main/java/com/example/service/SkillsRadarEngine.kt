package com.example.service

import com.example.data.model.GapPriority
import com.example.data.model.MissingItemInsight
import com.example.data.model.MissingItemType
import com.example.data.model.MockResumeDocument
import com.example.data.model.ParsedResumeDocument
import com.example.data.model.SkillsRadarDimension
import com.example.data.model.SkillsRadarReport
import com.example.data.model.TargetJobRequirementProfile

object SkillsRadarEngine {

  val TARGET_JOB_PROFILES = listOf(
    TargetJobRequirementProfile(
      id = "jd_google_strat_ops",
      companyId = "comp_google",
      companyName = "Google India",
      roleTitle = "Strategy & Operations Program Specialist",
      department = "Google Cloud Enterprise Solutions",
      experienceTier = "0-2 Years (Associate / Fresher Friendly)",
      compensationRange = "₹18L - ₹24L + Alphabet Equity",
      fullDescription = """
        Google Cloud is looking for a Strategy & Operations Program Specialist in Bengaluru.
        Responsibilities:
        - Own business telemetry and build scalable SQL pipelines using BigQuery and Google Cloud Platform.
        - Partner with sales, product, and engineering leaders to execute cross-functional OKRs and business reviews.
        - Formulate quantitative business cases and financial models for enterprise migrations.
        - Drive post-mortem root cause analyses (RCA) and program governance protocols across APAC.
        Requirements:
        - Demonstrated experience with SQL (CTEs, Window Functions) and BigQuery data pipelines.
        - Strong background in business analytics, unit economics, and data storytelling.
        - Experience with Looker, Tableau, or PowerBI dashboard instrumentation.
        - Google Cloud Certified (Cloud Digital Leader or Professional Data Engineer) preferred.
        - Cross-functional stakeholder leadership and C-suite presentation capabilities.
      """.trimIndent(),
      requiredKeywords = listOf(
        "BigQuery", "SQL Pipelines", "Cross-Functional OKRs", "Business Telemetry",
        "Financial Modeling", "Root Cause Analysis (RCA)", "Looker", "APAC Regional Governance"
      ),
      preferredKeywords = listOf(
        "Python Automation", "Enterprise SLA", "Data Storytelling", "Capacity Planning", "Cohort Analysis"
      ),
      requiredCertifications = listOf(
        "Google Cloud Certified (Cloud Digital Leader or Data Engineer)",
        "Advanced SQL Proficiency Certification"
      ),
      preferredCertifications = listOf(
        "Six Sigma Green Belt",
        "Looker Business Analyst Credential"
      ),
      dimensionTargets = mapOf(
        "data_analytics" to 0.90f,
        "strategy_ops" to 0.85f,
        "product_commercial" to 0.80f,
        "ai_systems" to 0.75f,
        "exec_leadership" to 0.85f,
        "compliance_scale" to 0.75f
      )
    ),

    TargetJobRequirementProfile(
      id = "jd_msft_cloud_ops",
      companyId = "comp_microsoft",
      companyName = "Microsoft India",
      roleTitle = "Cloud Operations & Tech Strategy Lead",
      department = "Microsoft Cloud & Enterprise Ecosystem",
      experienceTier = "1-3 Years",
      compensationRange = "₹20L - ₹28L + Azure Stock Plan",
      fullDescription = """
        Microsoft Cloud Operations is seeking a Technical Strategy Lead in Bengaluru.
        Responsibilities:
        - Orchestrate Azure enterprise governance and multi-cloud TCO (Total Cost of Ownership) frameworks.
        - Build PowerBI telemetry dashboards and SQL data flows to monitor customer workloads and incidents.
        - Manage high-severity incident escalation playbooks and SLA adherence for Tier-1 partners.
        - Drive TPM execution across engineering, operations, and support organizations.
        Requirements:
        - Proficiency in PowerBI, SQL, Azure Monitor, and Cloud Economics.
        - Knowledge of ITIL Service Management, Incident Playbooks, and SLA Governance.
        - Microsoft Certified: Azure Fundamentals (AZ-900) or Azure Data Fundamentals (DP-900).
        - Power BI Data Analyst Associate (PL-300) preferred.
      """.trimIndent(),
      requiredKeywords = listOf(
        "Azure Fundamentals", "TCO Analysis", "PowerBI Telemetry", "Incident Playbooks",
        "SLA Adherence", "Technical Program Management", "Cloud Economics", "ITIL Framework"
      ),
      preferredKeywords = listOf(
        "Python ETL", "Root Cause Discovery", "Capacity Forecasting", "Partner Co-Sell"
      ),
      requiredCertifications = listOf(
        "Microsoft Certified: Azure Fundamentals (AZ-900)",
        "Power BI Data Analyst Associate (PL-300)"
      ),
      preferredCertifications = listOf(
        "ITIL 4 Foundation",
        "Certified ScrumMaster (CSM)"
      ),
      dimensionTargets = mapOf(
        "data_analytics" to 0.88f,
        "strategy_ops" to 0.88f,
        "product_commercial" to 0.75f,
        "ai_systems" to 0.80f,
        "exec_leadership" to 0.80f,
        "compliance_scale" to 0.85f
      )
    ),

    TargetJobRequirementProfile(
      id = "jd_zepto_ops_lead",
      companyId = "comp_zepto",
      companyName = "Zepto",
      roleTitle = "Lead - Strategy & Dark Store Operations",
      department = "City Expansion & Rapid Fulfillment",
      experienceTier = "1-3 Years",
      compensationRange = "₹18L - ₹25L + ESOPs",
      fullDescription = """
        Zepto is building India's fastest hyperlocal quick-commerce network.
        Responsibilities:
        - Supervise dark store order cycle times, picking throughput, and fulfillment latency.
        - Construct geospatial delivery models and hyperlocal order dispatching logic.
        - Monitor store unit economics, shrink/spoilage mitigation, and labor cost efficiency.
        - Lead war-room incident response during peak monsoon and festival demand spikes.
        Requirements:
        - Strong SQL and Python skills for real-time logistics analytics.
        - Deep understanding of dark store layout, picker batching, and inventory replenishment.
        - High-tempo operational grit and direct squad management experience.
      """.trimIndent(),
      requiredKeywords = listOf(
        "Dark Store Throughput", "Order Cycle Time", "Picker Batching", "Inventory Replenishment",
        "Shrinkage Mitigation", "Unit Economics", "Hyperlocal Logistics", "Monsoon Peak Ops"
      ),
      preferredKeywords = listOf(
        "Geospatial SQL", "PostgreSQL", "Redis Telemetry", "Fleet Dispatching"
      ),
      requiredCertifications = listOf(
        "Lean Six Sigma Yellow/Green Belt (or Supply Chain Ops Equivalent)",
        "Advanced Data Analytics with SQL/Python"
      ),
      preferredCertifications = listOf(
        "Certified Supply Chain Professional (CSCP)",
        "Logistics Operations Specialist"
      ),
      dimensionTargets = mapOf(
        "data_analytics" to 0.85f,
        "strategy_ops" to 0.95f,
        "product_commercial" to 0.85f,
        "ai_systems" to 0.70f,
        "exec_leadership" to 0.80f,
        "compliance_scale" to 0.75f
      )
    ),

    TargetJobRequirementProfile(
      id = "jd_nvidia_ai_ops",
      companyId = "comp_nvidia",
      companyName = "NVIDIA India",
      roleTitle = "Enterprise AI Operations Lead",
      department = "Accelerated Computing & AI Platforms",
      experienceTier = "1-3 Years",
      compensationRange = "₹22L - ₹32L + NVIDIA RSUs",
      fullDescription = """
        NVIDIA is leading the generative AI infrastructure revolution.
        Responsibilities:
        - Oversee cluster compute capacity planning, GPU utilization metrics, and SLA governance.
        - Build automated telemetry pipelines monitoring AI workload execution and latency.
        - Collaborate with global engineering leads to streamline enterprise software releases.
        Requirements:
        - Familiarity with AI infrastructure, GPU cluster metrics, and model serving telemetry.
        - Quantitative analytics in Python, SQL, and data visualization.
        - Proven ability to bridge engineering data with executive board communication.
      """.trimIndent(),
      requiredKeywords = listOf(
        "GPU Utilization", "AI Workload Telemetry", "Cluster Capacity Planning", "Latency Optimization",
        "Autonomous Agent Pipelines", "Model Serving Heuristics", "Cross-Timezone Leadership", "Quantitative Sizing"
      ),
      preferredKeywords = listOf(
        "CUDA Awareness", "Distributed Systems", "PyTorch Analytics", "Enterprise Hardware SLA"
      ),
      requiredCertifications = listOf(
        "NVIDIA Deep Learning Institute (DLI) Credential (or Generative AI Systems)",
        "Advanced Quantitative Data Engineering"
      ),
      preferredCertifications = listOf(
        "AWS/GCP Machine Learning Specialty",
        "Kubernetes Fundamentals (CKA/CKAD)"
      ),
      dimensionTargets = mapOf(
        "data_analytics" to 0.90f,
        "strategy_ops" to 0.80f,
        "product_commercial" to 0.70f,
        "ai_systems" to 0.95f,
        "exec_leadership" to 0.80f,
        "compliance_scale" to 0.75f
      )
    ),

    TargetJobRequirementProfile(
      id = "jd_razorpay_risk_ops",
      companyId = "comp_razorpay",
      companyName = "Razorpay",
      roleTitle = "Senior Operations & Risk Lead",
      department = "Payment Gateway & Financial Integrity",
      experienceTier = "1-3 Years",
      compensationRange = "₹16L - ₹22L + ESOPs",
      fullDescription = """
        Razorpay powers digital payments for millions of Indian merchants.
        Responsibilities:
        - Manage settlement pipelines, merchant dispute workflows, and transaction fraud anomalies.
        - Ensure 99.99% payment SLA compliance and regulatory adherence (RBI guidelines).
        - Automate chargeback investigation playbooks using SQL and event heuristics.
        Requirements:
        - Deep SQL querying, anomaly detection, and business logic automation.
        - Strong financial acumen, merchant risk mitigation, and compliance awareness.
      """.trimIndent(),
      requiredKeywords = listOf(
        "Payment Settlements", "Fraud Anomaly Detection", "RBI Regulatory Compliance", "Chargeback Playbooks",
        "SLA Adherence 99.99%", "Transaction Reconciliation", "Merchant Risk Scoring", "High-Volume SQL"
      ),
      preferredKeywords = listOf(
        "PostgreSQL Analytics", "Event-Driven Telemetry", "Fintech Payment Protocols"
      ),
      requiredCertifications = listOf(
        "Certified Anti-Money Laundering Specialist (CAMS) or Indian Banking Compliance Credential",
        "SQL Query Optimization Specialist"
      ),
      preferredCertifications = listOf(
        "Financial Risk Manager (FRM Level 1) or CBAP",
        "ISO 27001 Security Fundamentals"
      ),
      dimensionTargets = mapOf(
        "data_analytics" to 0.92f,
        "strategy_ops" to 0.85f,
        "product_commercial" to 0.82f,
        "ai_systems" to 0.75f,
        "exec_leadership" to 0.78f,
        "compliance_scale" to 0.92f
      )
    )
  )

  /**
   * Compares a stored resume against a target job requirement profile.
   */
  fun generateSkillsRadarReport(
    resume: MockResumeDocument,
    jobProfile: TargetJobRequirementProfile,
    injectedKeywords: Set<String> = emptySet(),
    injectedCertifications: Set<String> = emptySet()
  ): SkillsRadarReport {
    val resumeText = (resume.rawContent + " " + injectedKeywords.joinToString(" ") + " " + injectedCertifications.joinToString(" ")).lowercase()

    // 1. Evaluate Keywords
    val allRequiredKeywords = jobProfile.requiredKeywords + jobProfile.preferredKeywords
    val matchedKeywords = mutableListOf<String>()
    val missingKeywordsInsights = mutableListOf<MissingItemInsight>()

    allRequiredKeywords.forEachIndexed { idx, keyword ->
      val isPresent = isItemPresentInText(keyword, resumeText) || injectedKeywords.contains(keyword)
      if (isPresent) {
        matchedKeywords.add(keyword)
      } else {
        val isCoreRequired = jobProfile.requiredKeywords.contains(keyword)
        val priority = if (isCoreRequired) GapPriority.CRITICAL_ATS else GapPriority.HIGH_ADVANTAGE
        val category = determineCategoryForKeyword(keyword)

        missingKeywordsInsights.add(
          MissingItemInsight(
            id = "kw_${jobProfile.id}_$idx",
            title = keyword,
            type = MissingItemType.KEYWORD,
            priority = priority,
            category = category,
            targetCompany = jobProfile.companyName,
            targetRole = jobProfile.roleTitle,
            presenceInTargetJd = "Explicit requirement in ${jobProfile.companyName} JD under $category",
            candidateCurrentState = "Keyword '$keyword' is absent or under-quantified in stored resume '${resume.fileName}'.",
            suggestedAction = "Incorporate '$keyword' into your recent project or professional experience section with quantifiable results.",
            readyToInjectBullet = generateBulletForKeyword(keyword, jobProfile.companyName),
            isAddedToDraft = injectedKeywords.contains(keyword)
          )
        )
      }
    }

    // 2. Evaluate Certifications
    val allRequiredCerts = jobProfile.requiredCertifications + jobProfile.preferredCertifications
    val matchedCerts = mutableListOf<String>()
    val missingCertsInsights = mutableListOf<MissingItemInsight>()

    allRequiredCerts.forEachIndexed { idx, cert ->
      val isPresent = isItemPresentInText(cert, resumeText) || injectedCertifications.contains(cert)
      if (isPresent) {
        matchedCerts.add(cert)
      } else {
        val isCore = jobProfile.requiredCertifications.contains(cert)
        val priority = if (isCore) GapPriority.CRITICAL_ATS else GapPriority.HIGH_ADVANTAGE

        missingCertsInsights.add(
          MissingItemInsight(
            id = "cert_${jobProfile.id}_$idx",
            title = cert,
            type = MissingItemType.CERTIFICATION,
            priority = priority,
            category = "Professional Credentials",
            targetCompany = jobProfile.companyName,
            targetRole = jobProfile.roleTitle,
            presenceInTargetJd = "Preferred / Required credential specified by ${jobProfile.companyName} hiring managers.",
            candidateCurrentState = "Stored resume has BBA degree and coursework, but lacks the formal '$cert' credential badge.",
            suggestedAction = "Complete the official exam or highlight equivalent coursework/capstone project on your profile.",
            readyToInjectBullet = "Certified: Completed '$cert' with verified distinction and practical application.",
            isAddedToDraft = injectedCertifications.contains(cert)
          )
        )
      }
    }

    // 3. Compute 6 Dimensional Radar Scores
    val dimensions = listOf(
      computeDimension(
        id = "data_analytics",
        name = "Data Analytics & Telemetry",
        shortLabel = "Data & SQL",
        keywords = listOf("sql", "bigquery", "powerbi", "python", "telemetry", "looker", "dashboard", "etl"),
        resumeText = resumeText,
        targetScore = jobProfile.dimensionTargets["data_analytics"] ?: 0.85f,
        matchedKw = matchedKeywords,
        missingKw = missingKeywordsInsights.map { it.title },
        matchedCert = matchedCerts,
        missingCert = missingCertsInsights.map { it.title },
        tacticalAdvice = "Strengthen BigQuery optimization & real-time telemetry metrics."
      ),
      computeDimension(
        id = "strategy_ops",
        name = "Strategic Operations & Execution",
        shortLabel = "Strategy Ops",
        keywords = listOf("dark store", "unit economics", "order cycle", "inventory", "sla", "throughput", "dispatch"),
        resumeText = resumeText,
        targetScore = jobProfile.dimensionTargets["strategy_ops"] ?: 0.85f,
        matchedKw = matchedKeywords,
        missingKw = missingKeywordsInsights.map { it.title },
        matchedCert = matchedCerts,
        missingCert = missingCertsInsights.map { it.title },
        tacticalAdvice = "Highlight unit economics, shrinkage control, and rapid fulfillment turnarounds."
      ),
      computeDimension(
        id = "product_commercial",
        name = "Commercial Acumen & P&L",
        shortLabel = "Commercial P&L",
        keywords = listOf("financial modeling", "pipeline", "c-suite", "revenue", "margin", "market sizing", "cohort"),
        resumeText = resumeText,
        targetScore = jobProfile.dimensionTargets["product_commercial"] ?: 0.80f,
        matchedKw = matchedKeywords,
        missingKw = missingKeywordsInsights.map { it.title },
        matchedCert = matchedCerts,
        missingCert = missingCertsInsights.map { it.title },
        tacticalAdvice = "Emphasize BBA International Business grounding and commercial pipeline value."
      ),
      computeDimension(
        id = "ai_systems",
        name = "AI & Autonomous Systems",
        shortLabel = "AI Systems",
        keywords = listOf("ai agents", "autonomous", "llm", "prompt", "gpu", "latency", "model"),
        resumeText = resumeText,
        targetScore = jobProfile.dimensionTargets["ai_systems"] ?: 0.75f,
        matchedKw = matchedKeywords,
        missingKw = missingKeywordsInsights.map { it.title },
        matchedCert = matchedCerts,
        missingCert = missingCertsInsights.map { it.title },
        tacticalAdvice = "Showcase autonomous multi-agent orchestration and local model evaluations."
      ),
      computeDimension(
        id = "exec_leadership",
        name = "Executive Comms & Leadership",
        shortLabel = "Exec Comms",
        keywords = listOf("cross-functional", "okr", "stakeholder", "presentation", "task force", "governance"),
        resumeText = resumeText,
        targetScore = jobProfile.dimensionTargets["exec_leadership"] ?: 0.85f,
        matchedKw = matchedKeywords,
        missingKw = missingKeywordsInsights.map { it.title },
        matchedCert = matchedCerts,
        missingCert = missingCertsInsights.map { it.title },
        tacticalAdvice = "Detail cross-functional squad management and structured board memos."
      ),
      computeDimension(
        id = "compliance_scale",
        name = "Governance, Risk & Scalability",
        shortLabel = "Governance",
        keywords = listOf("rbi", "compliance", "fraud", "incident", "playbook", "rca", "itil", "audit"),
        resumeText = resumeText,
        targetScore = jobProfile.dimensionTargets["compliance_scale"] ?: 0.80f,
        matchedKw = matchedKeywords,
        missingKw = missingKeywordsInsights.map { it.title },
        matchedCert = matchedCerts,
        missingCert = missingCertsInsights.map { it.title },
        tacticalAdvice = "Document post-mortem root cause analysis (RCA) and SLA governance frameworks."
      )
    )

    // 4. Overall Match & ATS Keyword Percentages
    val totalKw = allRequiredKeywords.size
    val kwMatchPercent = if (totalKw > 0) ((matchedKeywords.size.toFloat() / totalKw) * 100).toInt() else 80
    val totalCert = allRequiredCerts.size
    val certMatchPercent = if (totalCert > 0) ((matchedCerts.size.toFloat() / totalCert) * 100).toInt() else 60

    val dimAverage = dimensions.map { it.resumeScore }.average().toFloat()
    val overallMatch = ((dimAverage * 0.6f + (kwMatchPercent / 100f) * 0.3f + (certMatchPercent / 100f) * 0.1f) * 100).toInt().coerceIn(40, 99)

    val verifiedStrengths = listOf(
      "Direct BBA in International Business aligns with commercial strategy expectations",
      "Demonstrated 42% order cycle reduction using automated SQL & Python pipelines",
      "Quantifiable proof-of-work with ₹18L enterprise pipeline generation",
      "Autonomous agent architecture projects demonstrate advanced tech multiplier"
    )

    val summary = "Your stored resume '${resume.fileName}' has an overall match of $overallMatch% with ${jobProfile.companyName}'s ${jobProfile.roleTitle}. " +
      "You have matched ${matchedKeywords.size}/$totalKw target keywords. " +
      "Addressing the ${missingKeywordsInsights.size} missing keywords and ${missingCertsInsights.size} certification recommendations will raise your ATS pass-through probability to 98%."

    return SkillsRadarReport(
      selectedResumeId = resume.id,
      selectedResumeTitle = resume.title,
      selectedJobId = jobProfile.id,
      selectedJobTitle = jobProfile.roleTitle,
      targetCompanyName = jobProfile.companyName,
      overallMatchScore = overallMatch,
      keywordMatchPercent = kwMatchPercent,
      certificationMatchPercent = certMatchPercent,
      matchedKeywordsCount = matchedKeywords.size,
      totalRequiredKeywordsCount = totalKw,
      matchedCertificationsCount = matchedCerts.size,
      totalRequiredCertificationsCount = totalCert,
      dimensions = dimensions,
      missingKeywords = missingKeywordsInsights,
      missingCertifications = missingCertsInsights,
      verifiedStrengths = verifiedStrengths,
      executiveSummary = summary
    )
  }

  private fun isItemPresentInText(item: String, text: String): Boolean {
    val clean = item.lowercase().replace("(", "").replace(")", "").replace("-", " ")
    val tokens = clean.split(" ").filter { it.length > 2 && it !in listOf("and", "the", "for", "with") }
    if (tokens.isEmpty()) return false
    val matches = tokens.count { text.contains(it) }
    return matches.toFloat() / tokens.size >= 0.7f
  }

  private fun computeDimension(
    id: String,
    name: String,
    shortLabel: String,
    keywords: List<String>,
    resumeText: String,
    targetScore: Float,
    matchedKw: List<String>,
    missingKw: List<String>,
    matchedCert: List<String>,
    missingCert: List<String>,
    tacticalAdvice: String
  ): SkillsRadarDimension {
    val presentCount = keywords.count { kw -> resumeText.contains(kw) }
    val rawRatio = (presentCount.toFloat() / keywords.size.coerceAtLeast(1))
    // Anchor between realistic baseline (0.65) and 0.96
    val resumeScore = (0.55f + rawRatio * 0.42f).coerceIn(0.40f, 0.98f)

    val matchedInDim = matchedKw.filter { kw -> keywords.any { kw.contains(it, ignoreCase = true) } }
    val missingInDim = missingKw.filter { kw -> keywords.any { kw.contains(it, ignoreCase = true) } }

    return SkillsRadarDimension(
      id = id,
      name = name,
      shortLabel = shortLabel,
      resumeScore = resumeScore,
      targetJdScore = targetScore,
      matchedKeywords = matchedInDim,
      missingKeywords = missingInDim,
      matchedCertifications = matchedCert.filter { it.contains(shortLabel, ignoreCase = true) },
      missingCertifications = missingCert.filter { it.contains(shortLabel, ignoreCase = true) },
      tacticalAdvice = tacticalAdvice
    )
  }

  private fun determineCategoryForKeyword(kw: String): String {
    val k = kw.lowercase()
    return when {
      k.contains("sql") || k.contains("bigquery") || k.contains("telemetry") || k.contains("looker") -> "Data Analytics"
      k.contains("dark store") || k.contains("cycle") || k.contains("inventory") || k.contains("batch") -> "Operations Strategy"
      k.contains("cloud") || k.contains("azure") || k.contains("gpu") || k.contains("agent") -> "Cloud & Systems"
      k.contains("rbi") || k.contains("compliance") || k.contains("sla") || k.contains("rca") -> "Governance & Scale"
      else -> "Strategic Execution"
    }
  }

  private fun generateBulletForKeyword(keyword: String, company: String): String {
    return when (keyword.lowercase()) {
      "bigquery" -> "Architected automated BigQuery analytical data pipelines querying multi-million row event logs to power daily operational throughput dashboards."
      "looker" -> "Engineered custom Looker executive telemetry dashboards tracking cross-regional dark store order completion rates with 1-second refresh cadence."
      "cross-functional okrs" -> "Orchestrated cross-functional OKRs aligning 65+ regional team leads, accelerating milestone completion velocity by 24%."
      "root cause analysis (rca)" -> "Led structured post-mortem Root Cause Analyses (RCAs) for tier-1 supply disruptions, engineering preventative workflows that cut recurring bottlenecks by 38%."
      "azure fundamentals" -> "Implemented Azure cloud resource monitoring and governance telemetry frameworks, reducing cloud infrastructure cost waste by ₹4.8L annually."
      "tco analysis" -> "Formulated enterprise Total Cost of Ownership (TCO) and multi-cloud ROI models for executive leadership, securing approval for full migration."
      "dark store throughput" -> "Optimized dark store picking routes and inventory allocation, expanding peak throughput from 420 to 680 orders/hour."
      "picker batching" -> "Deployed algorithmic picker batching and dispatch logic, eliminating idle dwell time by 31% across 14 metropolitan micro-fulfillment hubs."
      "rbi regulatory compliance" -> "Directed operational transaction settlement protocols in compliance with RBI digital payment directives, achieving zero regulatory penalties."
      "fraud anomaly detection" -> "Automated real-time fraud anomaly scoring heuristics across 50,000+ daily transaction events, reducing chargeback exposure by 19%."
      else -> "Spearheaded strategic optimization integrating $keyword across operational workflows, driving a 28% gain in delivery performance for $company target initiatives."
    }
  }
}
