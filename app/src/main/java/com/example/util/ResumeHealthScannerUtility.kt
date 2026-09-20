package com.example.util

import java.util.regex.Pattern

enum class SuggestionPriority(val label: String, val weight: Int) {
  CRITICAL("CRITICAL FIX", 4),
  HIGH("HIGH IMPACT", 3),
  MEDIUM("RECOMMENDED", 2),
  QUICK_WIN("QUICK WIN", 1)
}

enum class HealthCategory(val title: String) {
  METRICS_IMPACT("Quantifiable Metrics & KPIs"),
  ACTION_VERBS("Power Verbs & Active Voice"),
  ATS_COMPLIANCE("ATS Parseability & Layout"),
  INDUSTRY_KEYWORDS("Industry Standards & Keywords"),
  CLICHE_ELIMINATION("Cliché & Buzzword Removal"),
  STRUCTURE_BREVITY("Section Structure & Brevity")
}

enum class IndustryBenchmark(val displayName: String, val targetKeywords: List<String>) {
  TECH_DISTRIBUTED_SYSTEMS(
    "Cloud & Distributed Systems",
    listOf(
      "Microservices", "Kubernetes", "Docker", "Kafka", "Redis", "gRPC", "Distributed Systems",
      "System Design", "CI/CD", "AWS", "GCP", "PostgreSQL", "Observability", "Latency",
      "High Availability", "Scalability", "Clean Architecture", "REST API", "OpenTelemetry"
    )
  ),
  AI_ML_ENGINEERING(
    "AI & Machine Learning",
    listOf(
      "Gemini", "LLM", "RAG", "PyTorch", "Transformers", "Vector Database", "Fine-tuning",
      "Model Evaluation", "Inference Latency", "Prompt Engineering", "Embeddings", "Hugging Face",
      "LangChain", "Autonomous Agents", "MLOps", "Feature Store", "Data Pipeline"
    )
  ),
  MOBILE_ANDROID(
    "Modern Mobile / Android",
    listOf(
      "Kotlin", "Jetpack Compose", "Coroutines", "Flow", "Room DB", "MVVM", "Clean Architecture",
      "Dependency Injection", "Hilt", "Retrofit", "Unit Testing", "Material Design 3", "Gradle",
      "CI/CD", "Performance Optimization", "WorkManager", "StateFlow", "Android SDK"
    )
  ),
  PRODUCT_OPERATIONS(
    "Product Strategy & Operations",
    listOf(
      "North Star Metric", "Product Roadmap", "A/B Testing", "Cohort Analysis", "CAC", "LTV",
      "User Retention", "Conversion Funnel", "Cross-Functional Leadership", "Unit Economics",
      "Stakeholder Management", "Data-Driven Strategy", "Agile", "OKRs", "Market Discovery"
    )
  )
}

data class ResumeHealthSubScore(
  val category: HealthCategory,
  val score: Int, // 0-100
  val status: String, // "EXCELLENT", "HEALTHY", "NEEDS_ATTENTION", "CRITICAL"
  val summary: String
)

data class BulletHealthTransformation(
  val originalBullet: String,
  val improvedBullet: String,
  val metricInjected: String,
  val reasoning: String
)

data class ResumeHealthSuggestion(
  val id: String,
  val title: String,
  val category: HealthCategory,
  val priority: SuggestionPriority,
  val scorePotentialBonus: Int, // e.g. +6 points
  val diagnosis: String,
  val actionableFix: String,
  val exampleBefore: String? = null,
  val exampleAfter: String? = null
)

data class ResumeHealthReport(
  val id: String,
  val timestamp: Long = System.currentTimeMillis(),
  val overallHealthScore: Int, // 0-100
  val letterGrade: String, // "A+", "A", "B+", "B", "C", "D"
  val verdict: String,
  val targetBenchmark: IndustryBenchmark,
  val wordCount: Int,
  val estimatedPages: Float,
  val totalBulletsCount: Int,
  val quantifiedBulletsCount: Int,
  val quantifiedRatioPercent: Int,
  val powerVerbsFound: List<String>,
  val weakVerbsFound: List<String>,
  val clichesFound: List<String>,
  val detectedIndustryKeywords: List<String>,
  val missingIndustryKeywords: List<String>,
  val detectedSections: List<String>,
  val missingSections: List<String>,
  val subScores: List<ResumeHealthSubScore>,
  val suggestions: List<ResumeHealthSuggestion>,
  val bulletTransformations: List<BulletHealthTransformation>
)

/**
 * High-performance resume health scanning engine evaluating uploaded or pasted resume
 * content against modern 2026 industry recruitment standards.
 */
object ResumeHealthScannerUtility {

  // Strong Action / Leadership Verbs
  private val POWER_VERBS = setOf(
    "spearheaded", "architected", "engineered", "orchestrated", "deployed", "scaled",
    "streamlined", "pioneered", "modernized", "accelerated", "optimized", "formulated",
    "authored", "elevated", "championed", "maximized", "negotiated", "automated",
    "designed", "reduced", "eliminated", "delivered", "executed", "surpassed", "restructured",
    "consolidated", "directed", "mentored", "revamped", "instituted", "standardized", "benchmarked"
  )

  // Weak / Passive Phrases that diminish impact
  private val WEAK_VERB_PHRASES = listOf(
    "responsible for",
    "assisted with",
    "helped to",
    "worked on",
    "participated in",
    "involved with",
    "duties included",
    "tasked with",
    "was part of",
    "contributed to",
    "handled",
    "supported",
    "aided",
    "attempted to"
  )

  // Low-signal Cliches & Buzzwords to eradicate
  private val CLICHES = listOf(
    "hardworking",
    "team player",
    "go-getter",
    "self-starter",
    "dynamic professional",
    "think outside the box",
    "detail-oriented",
    "synergy",
    "fast learner",
    "results-driven",
    "passionate worker",
    "motivated individual",
    "people person",
    "strategic thinker",
    "out of the box",
    "ninja",
    "rockstar",
    "guru"
  )

  // Standard Expected Sections
  private val STANDARD_SECTIONS = listOf(
    "Summary" to listOf("summary", "objective", "profile", "about me", "executive summary"),
    "Experience" to listOf("experience", "work history", "employment", "professional experience"),
    "Education" to listOf("education", "academic background", "degrees", "university"),
    "Skills" to listOf("skills", "technical skills", "competencies", "technologies", "tech stack"),
    "Projects" to listOf("projects", "proof of work", "portfolio", "key initiatives"),
    "Certifications" to listOf("certifications", "licenses", "credentials")
  )

  // Regex Patterns for Quantifiable Outcomes
  private val METRIC_PATTERNS = listOf(
    Pattern.compile("\\b\\d+(\\.\\d+)?%\\b"), // Percentages (e.g. 45%, 12.5%)
    Pattern.compile("(\\\$|₹|€|USD|INR|EUR)\\s?\\d+([\\.,]\\d+)?\\s?([KkMmBb]|million|billion|cr|lakh)?"), // Currency ($1.2M, ₹45L)
    Pattern.compile("\\b\\d+(\\.\\d+)?x\\b", Pattern.CASE_INSENSITIVE), // Multipliers (3x, 10x)
    Pattern.compile("\\b\\d+([,\\.]\\d+)?\\s*(users|MAU|DAU|QPS|RPS|ms|engineers|microservices|TB|GB|million|k|teams|clients)\\b", Pattern.CASE_INSENSITIVE),
    Pattern.compile("\\b(reduced|increased|accelerated|saved|boosted|grew)\\s+.*\\b\\d+\\b", Pattern.CASE_INSENSITIVE)
  )

  /**
   * Scans the provided raw resume text against industry standards and returns a detailed report.
   */
  fun scanResume(
    rawText: String,
    benchmark: IndustryBenchmark = IndustryBenchmark.TECH_DISTRIBUTED_SYSTEMS
  ): ResumeHealthReport {
    val cleanText = rawText.trim()
    val words = cleanText.split("\\s+".toRegex()).filter { it.isNotBlank() }
    val wordCount = words.size
    val estimatedPages = (wordCount / 500f).coerceAtLeast(0.5f)

    val lines = cleanText.lines().map { it.trim() }.filter { it.isNotBlank() }

    // Detect Bullet Points
    val bulletLines = lines.filter { line ->
      line.startsWith("•") || line.startsWith("-") || line.startsWith("*") ||
          line.matches("^\\d+[\\.\\)]\\s+.*".toRegex()) ||
          (line.length in 35..350 && line.endsWith("."))
    }
    val totalBullets = bulletLines.size.coerceAtLeast(1)

    // Detect Quantified Bullets
    val quantifiedBullets = bulletLines.filter { bullet ->
      METRIC_PATTERNS.any { pattern -> pattern.matcher(bullet).find() }
    }
    val quantifiedCount = quantifiedBullets.size
    val quantifiedRatio = ((quantifiedCount.toFloat() / totalBullets) * 100).toInt()

    // Detect Power Verbs & Weak Verbs
    val lowerText = cleanText.lowercase()
    val powerVerbsFound = POWER_VERBS.filter { verb ->
      Pattern.compile("\\b$verb\\b", Pattern.CASE_INSENSITIVE).matcher(cleanText).find()
    }
    val weakVerbsFound = WEAK_VERB_PHRASES.filter { phrase ->
      lowerText.contains(phrase)
    }

    // Detect Clichés
    val clichesFound = CLICHES.filter { cliche ->
      lowerText.contains(cliche)
    }

    // Detect Sections
    val detectedSections = mutableListOf<String>()
    val missingSections = mutableListOf<String>()
    STANDARD_SECTIONS.forEach { (sectionName, aliases) ->
      val hasSection = lines.any { line ->
        val l = line.lowercase()
        aliases.any { alias ->
          l == alias || l == "$alias:" || l.startsWith("$alias -") || l.startsWith("$alias |")
        }
      }
      if (hasSection) {
        detectedSections.add(sectionName)
      } else {
        missingSections.add(sectionName)
      }
    }

    // Detect Industry Keywords
    val detectedKeywords = benchmark.targetKeywords.filter { kw ->
      Pattern.compile("\\b${Pattern.quote(kw)}\\b", Pattern.CASE_INSENSITIVE).matcher(cleanText).find()
    }
    val missingKeywords = benchmark.targetKeywords.filterNot { detectedKeywords.contains(it) }

    // -------------------------------------------------------------
    // Calculate Sub-Scores (0 - 100)
    // -------------------------------------------------------------

    // 1. Metrics & Quantified Impact (0 - 100)
    // Target: >= 45% of bullets quantified
    val metricScore = when {
      quantifiedRatio >= 55 -> 96
      quantifiedRatio >= 40 -> 85
      quantifiedRatio >= 25 -> 70
      quantifiedRatio >= 15 -> 50
      quantifiedRatio > 0 -> 35
      else -> 18
    }

    // 2. Power Verbs & Active Voice (0 - 100)
    val verbRatio = (powerVerbsFound.size.toFloat() / (powerVerbsFound.size + weakVerbsFound.size * 2).coerceAtLeast(1))
    val actionVerbScore = when {
      weakVerbsFound.isEmpty() && powerVerbsFound.size >= 6 -> 95
      weakVerbsFound.isEmpty() && powerVerbsFound.size >= 3 -> 85
      weakVerbsFound.size <= 2 && powerVerbsFound.size >= 4 -> 75
      weakVerbsFound.size <= 4 -> 60
      else -> 40
    }

    // 3. ATS Compliance & Structure (0 - 100)
    val requiredMajorSections = listOf("Experience", "Education", "Skills")
    val hasAllMajor = requiredMajorSections.all { detectedSections.contains(it) }
    val atsScore = when {
      hasAllMajor && detectedSections.size >= 4 -> 94
      hasAllMajor -> 82
      detectedSections.contains("Experience") -> 65
      else -> 42
    }

    // 4. Industry Keywords Alignment (0 - 100)
    val kwMatchRatio = detectedKeywords.size.toFloat() / benchmark.targetKeywords.size.coerceAtLeast(1)
    val keywordScore = when {
      kwMatchRatio >= 0.45f -> 95
      kwMatchRatio >= 0.30f -> 85
      kwMatchRatio >= 0.20f -> 75
      kwMatchRatio >= 0.10f -> 55
      else -> (kwMatchRatio * 100).toInt().coerceIn(20, 98)
    }

    // 5. Brevity & Cliché Avoidance (0 - 100)
    var brevityScore = 90
    if (clichesFound.isNotEmpty()) {
      brevityScore -= (clichesFound.size * 12).coerceAtMost(40)
    }
    if (wordCount < 120) brevityScore -= 30
    if (wordCount > 1000) brevityScore -= 20
    brevityScore = brevityScore.coerceIn(25, 98)

    // -------------------------------------------------------------
    // Overall Weighted Health Score
    // -------------------------------------------------------------
    val overallScore = (
        (metricScore * 0.28f) +
            (actionVerbScore * 0.22f) +
            (keywordScore * 0.22f) +
            (atsScore * 0.15f) +
            (brevityScore * 0.13f)
        ).toInt().coerceIn(15, 99)

    val letterGrade = when {
      overallScore >= 92 -> "A+"
      overallScore >= 84 -> "A"
      overallScore >= 75 -> "B+"
      overallScore >= 65 -> "B"
      overallScore >= 50 -> "C"
      else -> "D"
    }

    val verdict = when {
      overallScore >= 90 -> "EXCELLENT (Top 3% Tier-1 Ready)"
      overallScore >= 80 -> "STRONG (Highly Competitive)"
      overallScore >= 70 -> "SOLID (Standard Industry Caliber)"
      overallScore >= 55 -> "NEEDS REFINEMENT (Gaps in Impact)"
      else -> "CRITICAL GAPS (Requires Immediate Overhaul)"
    }

    val subScoresList = listOf(
      ResumeHealthSubScore(
        category = HealthCategory.METRICS_IMPACT,
        score = metricScore,
        status = getScoreStatus(metricScore),
        summary = "$quantifiedCount of $totalBullets bullets ($quantifiedRatio%) contain measurable numerical outcomes."
      ),
      ResumeHealthSubScore(
        category = HealthCategory.ACTION_VERBS,
        score = actionVerbScore,
        status = getScoreStatus(actionVerbScore),
        summary = "Detected ${powerVerbsFound.size} leadership power verbs and ${weakVerbsFound.size} passive phrases."
      ),
      ResumeHealthSubScore(
        category = HealthCategory.INDUSTRY_KEYWORDS,
        score = keywordScore,
        status = getScoreStatus(keywordScore),
        summary = "Matched ${detectedKeywords.size} of ${benchmark.targetKeywords.size} key standards for ${benchmark.displayName}."
      ),
      ResumeHealthSubScore(
        category = HealthCategory.ATS_COMPLIANCE,
        score = atsScore,
        status = getScoreStatus(atsScore),
        summary = "Found ${detectedSections.size} standard ATS section headers (missing: ${missingSections.joinToString().ifEmpty { "None" }})."
      ),
      ResumeHealthSubScore(
        category = HealthCategory.CLICHE_ELIMINATION,
        score = brevityScore,
        status = getScoreStatus(brevityScore),
        summary = if (clichesFound.isEmpty()) "Zero buzzwords or empty clichés found." else "Detected ${clichesFound.size} low-signal buzzwords."
      )
    )

    // -------------------------------------------------------------
    // Build Actionable Improvement Suggestions
    // -------------------------------------------------------------
    val suggestions = mutableListOf<ResumeHealthSuggestion>()

    // Metric Quantification Suggestions
    if (quantifiedRatio < 40) {
      suggestions.add(
        ResumeHealthSuggestion(
          id = "metric_quant_fix",
          title = "Adopt the Google XYZ Formula for Bullets",
          category = HealthCategory.METRICS_IMPACT,
          priority = SuggestionPriority.CRITICAL,
          scorePotentialBonus = 8,
          diagnosis = "Only $quantifiedRatio% of your bullets show measurable proof. Top 1% recruiters look for quantifiable metrics ($/%, time, scale, traffic) in at least 50% of bullets.",
          actionableFix = "Format bullets as: 'Accomplished [X] as measured by [Y], by doing [Z]'. Example: Instead of 'Optimized API responses', write 'Decreased p99 query latency by 42% across 12 microservices by implementing Redis write-through caching.'",
          exampleBefore = "Responsible for improving database queries and server speed.",
          exampleAfter = "Engineered automated SQL indexing and Redis caching, cutting p95 query latency by 58% for 4.2M daily active users."
        )
      )
    }

    // Passive Verbs Suggestions
    if (weakVerbsFound.isNotEmpty()) {
      suggestions.add(
        ResumeHealthSuggestion(
          id = "passive_verb_fix",
          title = "Replace Passive Phrases with Executive Power Verbs",
          category = HealthCategory.ACTION_VERBS,
          priority = SuggestionPriority.HIGH,
          scorePotentialBonus = 6,
          diagnosis = "Found ${weakVerbsFound.size} passive verbs/phrases (${weakVerbsFound.take(3).joinToString(", ")}). These convey task-taker behavior rather than high-ownership impact.",
          actionableFix = "Swap passive verbs for active leadership terms: 'Spearheaded', 'Architected', 'Orchestrated', 'Scaled', 'Automated'.",
          exampleBefore = "Responsible for helping the team deploy features on AWS.",
          exampleAfter = "Spearheaded zero-downtime CI/CD container deployments to AWS EKS, accelerating release frequency from weekly to daily."
        )
      )
    }

    // Cliché / Buzzword Suggestions
    if (clichesFound.isNotEmpty()) {
      suggestions.add(
        ResumeHealthSuggestion(
          id = "cliche_fix",
          title = "Eradicate Low-Signal Buzzwords",
          category = HealthCategory.CLICHE_ELIMINATION,
          priority = SuggestionPriority.MEDIUM,
          scorePotentialBonus = 4,
          diagnosis = "Detected buzzwords: ${clichesFound.joinToString(", ")}. Recruiters discard subjective adjectives that lack concrete deliverables.",
          actionableFix = "Remove buzzwords like '${clichesFound.firstOrNull() ?: "team player"}' and let specific technical or organizational deliverables speak for themselves.",
          exampleBefore = "A hardworking and dynamic self-starter who thinks outside the box.",
          exampleAfter = "Staff Engineer with 6+ years shipping high-throughput fault-tolerant distributed systems handling 50k+ QPS."
        )
      )
    }

    // Missing Industry Keywords
    if (missingKeywords.isNotEmpty()) {
      val topMissing = missingKeywords.take(4)
      suggestions.add(
        ResumeHealthSuggestion(
          id = "keywords_missing_fix",
          title = "Integrate Modern ${benchmark.displayName} Keywords",
          category = HealthCategory.INDUSTRY_KEYWORDS,
          priority = SuggestionPriority.HIGH,
          scorePotentialBonus = 7,
          diagnosis = "Your resume lacks core high-demand keywords expected by ATS parsers for ${benchmark.displayName}: ${topMissing.joinToString(", ")}.",
          actionableFix = "Weave these technologies into your project bullets and Technical Skills section where you have hands-on experience.",
          exampleBefore = "Worked on backend server infrastructure and deployment pipelines.",
          exampleAfter = "Constructed distributed ${topMissing.firstOrNull() ?: "Microservices"} with automated ${topMissing.getOrNull(1) ?: "CI/CD"} pipelines maintaining 99.99% SLA."
        )
      )
    }

    // Missing Sections
    if (missingSections.isNotEmpty()) {
      suggestions.add(
        ResumeHealthSuggestion(
          id = "section_missing_fix",
          title = "Add Explicit Section Header for '${missingSections.first()}'",
          category = HealthCategory.ATS_COMPLIANCE,
          priority = SuggestionPriority.MEDIUM,
          scorePotentialBonus = 5,
          diagnosis = "ATS parsers look for standard headers. Missing: ${missingSections.joinToString(", ")}.",
          actionableFix = "Include clear, uppercase section headers like 'TECHNICAL SKILLS', 'PROFESSIONAL EXPERIENCE', 'PROJECTS & CONTRIBUTIONS' so applicant tracking systems categorize your background accurately.",
          exampleBefore = "(Skills mixed into work paragraphs without dedicated section)",
          exampleAfter = "TECHNICAL SKILLS\nLanguages: Kotlin, Go, TypeScript\nCloud & Ops: Kubernetes, Docker, AWS, Terraform"
        )
      )
    }

    // Brevity / Word Count Guidance
    if (wordCount < 300) {
      suggestions.add(
        ResumeHealthSuggestion(
          id = "length_short_fix",
          title = "Expand Technical Depth and Project Scope",
          category = HealthCategory.STRUCTURE_BREVITY,
          priority = SuggestionPriority.HIGH,
          scorePotentialBonus = 6,
          diagnosis = "Current length ($wordCount words) is overly brief for an impactful senior engineering resume.",
          actionableFix = "Add 2-3 deep-dive bullet points per role detailing system architecture, business trade-offs, and measurable efficiency gains.",
          exampleBefore = "Built user login and authentication system.",
          exampleAfter = "Engineered multi-tenant OAuth2 / JWT authentication service with rate-limiting, reducing unauthorized login attempts by 92%."
        )
      )
    } else if (wordCount > 950) {
      suggestions.add(
        ResumeHealthSuggestion(
          id = "length_long_fix",
          title = "Tighten Word Density to 1-2 Focused Pages",
          category = HealthCategory.STRUCTURE_BREVITY,
          priority = SuggestionPriority.QUICK_WIN,
          scorePotentialBonus = 3,
          diagnosis = "Resume length ($wordCount words, ~${String.format("%.1f", estimatedPages)} pages) risks hiring manager skimming fatigue.",
          actionableFix = "Condense bullets to 1-2 lines each. Prune roles older than 7-10 years to brief 1-line summaries.",
          exampleBefore = "(Long multi-sentence paragraph outlining daily tasks)",
          exampleAfter = "Single concise bullet focusing purely on measurable impact and modern technologies."
        )
      )
    }

    // -------------------------------------------------------------
    // Synthesize Bullet Transformations from weak bullets
    // -------------------------------------------------------------
    val bulletTransformations = mutableListOf<BulletHealthTransformation>()

    // Find up to 3 candidate weak bullets in user's text to transform
    val candidates = bulletLines.filter { b ->
      WEAK_VERB_PHRASES.any { b.lowercase().contains(it) } || !METRIC_PATTERNS.any { it.matcher(b).find() }
    }.take(3)

    candidates.forEachIndexed { i, candidate ->
      val (improved, metric, reason) = transformCandidateBullet(candidate, i)
      bulletTransformations.add(
        BulletHealthTransformation(
          originalBullet = candidate,
          improvedBullet = improved,
          metricInjected = metric,
          reasoning = reason
        )
      )
    }

    // If no candidate bullets found, provide high-value exemplars
    if (bulletTransformations.isEmpty()) {
      bulletTransformations.add(
        BulletHealthTransformation(
          originalBullet = "Worked on optimizing backend APIs and caching layers.",
          improvedBullet = "Architected Redis caching layer across 14 high-throughput microservices, reducing p99 latency by 48% and infrastructure cloud spend by $35k/year.",
          metricInjected = "48% p99 latency reduction • $35k/year savings",
          reasoning = "Injects ownership verb (Architected), architectural scope (14 microservices), and dual quantifiable outcomes (latency + cost savings)."
        )
      )
      bulletTransformations.add(
        BulletHealthTransformation(
          originalBullet = "Helped team build the real-time notification engine for users.",
          improvedBullet = "Spearheaded distributed Kafka event-driven notification pipeline delivering 2.5M daily alerts with 99.98% delivery reliability.",
          metricInjected = "2.5M daily alerts • 99.98% delivery reliability",
          reasoning = "Replaces 'helped team' with 'Spearheaded', establishes scale (2.5M alerts) and quality SLA (99.98%)."
        )
      )
    }

    return ResumeHealthReport(
      id = "health_scan_${System.currentTimeMillis()}",
      overallHealthScore = overallScore,
      letterGrade = letterGrade,
      verdict = verdict,
      targetBenchmark = benchmark,
      wordCount = wordCount,
      estimatedPages = estimatedPages,
      totalBulletsCount = totalBullets,
      quantifiedBulletsCount = quantifiedCount,
      quantifiedRatioPercent = quantifiedRatio,
      powerVerbsFound = powerVerbsFound,
      weakVerbsFound = weakVerbsFound,
      clichesFound = clichesFound,
      detectedIndustryKeywords = detectedKeywords,
      missingIndustryKeywords = missingKeywords,
      detectedSections = detectedSections,
      missingSections = missingSections,
      subScores = subScoresList,
      suggestions = suggestions.sortedByDescending { it.priority.weight },
      bulletTransformations = bulletTransformations
    )
  }

  private fun transformCandidateBullet(original: String, index: Int): Triple<String, String, String> {
    val clean = original.replace("^[•\\-\\*\\d\\.\\)]+\\s*".toRegex(), "").trim()
    return when (index % 3) {
      0 -> Triple(
        "Architected and deployed $clean, reducing system latency by 44% and driving 99.99% uptime across 1.2M monthly transactions.",
        "44% latency reduction • 1.2M monthly transactions",
        "Transformed passive task description into leadership action with concrete speed and scale metrics."
      )
      1 -> Triple(
        "Spearheaded end-to-end automation for $clean, saving 28 engineering hours weekly and accelerating release cycles by 3.5x.",
        "28 engineering hours saved weekly • 3.5x velocity multiplier",
        "Added business ROI and engineering productivity impact using the Google XYZ framework."
      )
      else -> Triple(
        "Modernized $clean utilizing modern CI/CD and container orchestration, cutting infrastructure spend by 32% while supporting 50k+ concurrent users.",
        "32% cost reduction • 50k+ concurrent users",
        "Anchored work to enterprise cost optimization and high-scale user concurrency benchmarks."
      )
    }
  }

  private fun getScoreStatus(score: Int): String {
    return when {
      score >= 88 -> "EXCELLENT"
      score >= 74 -> "HEALTHY"
      score >= 58 -> "NEEDS_ATTENTION"
      else -> "CRITICAL"
    }
  }

  /**
   * Pre-packaged professional resume samples for 1-click user testing and demonstration.
   */
  val PRESET_RESUMES = listOf(
    PresetResume(
      id = "preset_senior_android",
      title = "Senior Mobile / Android Architect",
      targetBenchmark = IndustryBenchmark.MOBILE_ANDROID,
      sampleText = """
        ADITYA VERMA
        Bengaluru, India • aditya.verma@example.com • +91 98765 43210
        LinkedIn: linkedin.com/in/aditya-verma • GitHub: github.com/aditya-v

        EXECUTIVE SUMMARY
        Senior Android Architect with 7+ years pioneering high-scale consumer applications. Specialized in Jetpack Compose, Kotlin Coroutines, and offline-first Room architectures supporting 8M+ active users.

        PROFESSIONAL EXPERIENCE
        Staff Android Engineer | Zepto Quick Commerce (2022 - Present)
        • Spearheaded re-architecture of consumer checkout flow to Jetpack Compose, reducing cold start launch latency by 38% and memory consumption by 25MB.
        • Engineered real-time delivery rider tracking pipeline with Coroutines and StateFlow, scaling location updates to 15k events/second.
        • Automated CI/CD build pipelines using GitHub Actions and Gradle cache optimization, cutting pull request verification time from 24 mins to 8.5 mins.
        • Mentored 8 junior and mid-level engineers across modular clean architecture patterns.

        Senior Mobile Engineer | Swiggy Food Tech (2019 - 2022)
        • Architected cart and payment micro-module supporting 18M monthly transactions with 99.99% checkout reliability.
        • Implemented dynamic theme design system and offline-first Room database sync, decreasing network bandwidth consumption by 42%.

        TECHNICAL SKILLS
        Languages: Kotlin, Java, Coroutines, Flow
        Mobile Frameworks: Jetpack Compose, Android SDK, Room DB, Retrofit, Hilt, WorkManager
        Architecture & Practices: Clean Architecture, MVI, MVVM, CI/CD, Unit Testing, Roborazzi

        EDUCATION
        B.Tech in Computer Science | National Institute of Technology (2015 - 2019)
      """.trimIndent()
    ),
    PresetResume(
      id = "preset_needs_improvement",
      title = "Early-Career Resume (Contains Gaps & Buzzwords)",
      targetBenchmark = IndustryBenchmark.TECH_DISTRIBUTED_SYSTEMS,
      sampleText = """
        JOHN DOE
        Software Engineer • john.doe@email.com

        ABOUT ME
        I am a hardworking, self-motivated team player and go-getter who thinks outside the box. Passionate worker with good synergy and fast learning skills.

        WORK HISTORY
        Developer | Tech Corp (2022 - Present)
        • Responsible for backend servers and handling APIs.
        • Assisted with bug fixing and worked on the database.
        • Duties included participating in daily standups and helping team members.
        • Worked on deploying code to the cloud.

        Junior Developer | Startup Inc (2020 - 2022)
        • Was part of the development team.
        • Handled user interface updates and fixed customer complaints.
        • Helped to write unit tests for the product.

        EDUCATION
        B.S. in Computer Science | State University
      """.trimIndent()
    ),
    PresetResume(
      id = "preset_cloud_architect",
      title = "Staff Cloud & Distributed Systems Engineer",
      targetBenchmark = IndustryBenchmark.TECH_DISTRIBUTED_SYSTEMS,
      sampleText = """
        PRIYA SHARMA
        Bengaluru, India • priya.sharma@cloudsystems.io • linkedin.com/in/priyasharma

        PROFESSIONAL SUMMARY
        Staff Systems Engineer with 8+ years designing fault-tolerant microservices, Kubernetes clusters, and distributed event pipelines processing 100k+ QPS.

        PROFESSIONAL EXPERIENCE
        Staff Infrastructure Engineer | Razorpay Payments (2021 - Present)
        • Architected multi-region Kubernetes clusters on AWS EKS, processing $12B+ in annual transaction volume with 99.995% service availability.
        • Spearheaded zero-downtime database migration from monolithic MySQL to distributed CockroachDB across 45 microservices.
        • Designed Kafka streaming pipeline with OpenTelemetry observability, reducing mean time to detection (MTTD) by 64%.

        Senior Backend Engineer | Flipkart (2018 - 2021)
        • Engineered distributed Redis caching layer handling 80k read QPS during Big Billion Days sales, preventing database outages.
        • Reduced cloud infrastructure spend by $140,000 annually by automating spot instance lifecycle management.

        TECHNICAL SKILLS
        Distributed Systems: Kubernetes, Docker, Kafka, gRPC, Redis, PostgreSQL, AWS, Microservices, OpenTelemetry, CI/CD

        EDUCATION
        M.S. in Computer Science | IIIT Bangalore
      """.trimIndent()
    )
  )

  data class PresetResume(
    val id: String,
    val title: String,
    val targetBenchmark: IndustryBenchmark,
    val sampleText: String
  )
}
