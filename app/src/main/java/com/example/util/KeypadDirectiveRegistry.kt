package com.example.util

/**
 * Registry of all 25-Keypad Direct Deterministic Execution Directives in Omega-Titan.
 */
data class KeypadDirective(
  val id: Int,
  val code: String, // "01", "02", ..., "25"
  val title: String,
  val summary: String,
  val category: String,
  val iconEmoji: String = "⚡"
) {
  val formattedCode: String get() = code
  val copilotCommand: String get() = "$id $title: $summary"
}

object KeypadDirectiveRegistry {

  val DIRECTIVES: List<KeypadDirective> = listOf(
    KeypadDirective(
      id = 1,
      code = "01",
      title = "JD Decompiler",
      summary = "Reverse-engineer hiring manager anxieties & match score in [EXP-001..006]",
      category = "STRATEGY",
      iconEmoji = "🎯"
    ),
    KeypadDirective(
      id = 2,
      code = "02",
      title = "ATS Resume Synthesizer",
      summary = "Truth-anchored ATS-optimized resume profile with high-yield quantified bullets",
      category = "RESUME",
      iconEmoji = "📄"
    ),
    KeypadDirective(
      id = 3,
      code = "03",
      title = "Executive Pitch & Hook",
      summary = "30-second bespoke recruiter screen hook anchoring high-pressure execution",
      category = "OUTREACH",
      iconEmoji = "🎙️"
    ),
    KeypadDirective(
      id = 4,
      code = "04",
      title = "CXO Radar & Funding Intel",
      summary = "CXO leadership shifts, venture funding rounds & Bangalore tech ecosystem intel",
      category = "INTELLIGENCE",
      iconEmoji = "📡"
    ),
    KeypadDirective(
      id = 5,
      code = "05",
      title = "5-Stage Outreach Sequencer",
      summary = "Multi-touch InMail, personalized Email & executive WhatsApp outreach hooks",
      category = "OUTREACH",
      iconEmoji = "✉️"
    ),
    KeypadDirective(
      id = 6,
      code = "06",
      title = "Zero-Friction Internal Referral",
      summary = "Pre-written internal referral prompt for 1st-degree alumni & senior engineers",
      category = "OUTREACH",
      iconEmoji = "🤝"
    ),
    KeypadDirective(
      id = 7,
      code = "07",
      title = "Real-Time Interview HUD",
      summary = "Live operational speed metrics: Pick 90s, Pack 45s, Buffer 120s order-to-dispatch",
      category = "INTERVIEW",
      iconEmoji = "⏱️"
    ),
    KeypadDirective(
      id = 8,
      code = "08",
      title = "STAR-V Behavioral Matrix",
      summary = "Situation-Task-Action-Result-Verification matrix anchored on AERO India proof-of-work",
      category = "INTERVIEW",
      iconEmoji = "⭐"
    ),
    KeypadDirective(
      id = 9,
      code = "09",
      title = "Hostile Bar-Raiser Counter-Attack",
      summary = "Reframe 6.33 CGPA & fresher concerns into verified on-ground operational dominance",
      category = "INTERVIEW",
      iconEmoji = "🛡️"
    ),
    KeypadDirective(
      id = 10,
      code = "10",
      title = "Live Whiteboard Case Solver",
      summary = "Structured 5-Whys root-cause synthesis for supply chain & dark store bottlenecks",
      category = "INTERVIEW",
      iconEmoji = "📊"
    ),
    KeypadDirective(
      id = 11,
      code = "11",
      title = "Live SQL CTE & Telemetry Pipeline",
      summary = "Window functions, partition keys, real-time inventory aggregation query generation",
      category = "TECHNICAL",
      iconEmoji = "💾"
    ),
    KeypadDirective(
      id = 12,
      code = "12",
      title = "Questions for CXO Interviewer",
      summary = "5 asymmetric questions demonstrating operator depth and strategic boardroom acumen",
      category = "INTERVIEW",
      iconEmoji = "❓"
    ),
    KeypadDirective(
      id = 13,
      code = "13",
      title = "Bengaluru Tech CTC Benchmarker",
      summary = "Tier-1 compensation percentiles: Base, HRA, performance bonus & joining incentives",
      category = "OFFER",
      iconEmoji = "💰"
    ),
    KeypadDirective(
      id = 14,
      code = "14",
      title = "ESOP Valuation & Tax Model",
      summary = "4-year vesting schedule, strike price calculations and dry-tax exercise planning",
      category = "OFFER",
      iconEmoji = "📈"
    ),
    KeypadDirective(
      id = 15,
      code = "15",
      title = "Hardball Counter-Offer Ghostwriter",
      summary = "Professional counter-offer negotiation scripts anchoring competing market leverage",
      category = "OFFER",
      iconEmoji = "⚖️"
    ),
    KeypadDirective(
      id = 16,
      code = "16",
      title = "Multi-Offer Leverage & Bidding War",
      summary = "Staggered decision timeline management to maximize competing offer tension",
      category = "OFFER",
      iconEmoji = "🔥"
    ),
    KeypadDirective(
      id = 17,
      code = "17",
      title = "30-60-90 Day Boardroom Action Plan",
      summary = "Quarterly leadership milestones from ground audit to autonomous optimization",
      category = "OPERATIONS",
      iconEmoji = "📅"
    ),
    KeypadDirective(
      id = 18,
      code = "18",
      title = "Dark Store Daily Health Audit",
      summary = "06:30 AM operational checklist: Cold chain, staging buffer, picker SLA compliance",
      category = "OPERATIONS",
      iconEmoji = "🏪"
    ),
    KeypadDirective(
      id = 19,
      code = "19",
      title = "Vendor 07:00 AM Punch-List",
      summary = "Contractor milestone escrows, delivery inspection and SLA penalty enforcement engine",
      category = "OPERATIONS",
      iconEmoji = "📋"
    ),
    KeypadDirective(
      id = 20,
      code = "20",
      title = "Amazon WBR Executive Memo",
      summary = "6-pager narrative memo format: Metrics table, variance analysis and bridge solutions",
      category = "OPERATIONS",
      iconEmoji = "📝"
    ),
    KeypadDirective(
      id = 21,
      code = "21",
      title = "Sev-1 Operations Incident Triage",
      summary = "Critical SLA breach triage protocol: War room escalation, root cause containment",
      category = "OPERATIONS",
      iconEmoji = "🚨"
    ),
    KeypadDirective(
      id = 22,
      code = "22",
      title = "EXIM Customs Desk & Incoterms",
      summary = "Incoterms 2020, Bill of Entry, BCD/SWS/IGST duty computation & compliance audit",
      category = "OPERATIONS",
      iconEmoji = "🚢"
    ),
    KeypadDirective(
      id = 23,
      code = "23",
      title = "Clean Android Software Foundry",
      summary = "Jetpack Compose, Kotlin Coroutines, Room offline caching & WorkManager daemons",
      category = "TECHNICAL",
      iconEmoji = "📱"
    ),
    KeypadDirective(
      id = 24,
      code = "24",
      title = "Automated Brag Sheet & Impact Ledger",
      summary = "Quantified operational wins ledger for performance evaluations and salary revisions",
      category = "STRATEGY",
      iconEmoji = "🏆"
    ),
    KeypadDirective(
      id = 25,
      code = "25",
      title = "Fast-Track Promotion Business Case",
      summary = "6-month appraisal dossier demonstrating next-level scope expansion and team leverage",
      category = "STRATEGY",
      iconEmoji = "🚀"
    )
  )

  /**
   * Finds a directive by various format keys:
   * - "01", "02", ..., "25"
   * - "1", "2", ..., "25"
   * - "#01", "#1"
   */
  fun findByCode(rawInput: String): KeypadDirective? {
    val clean = rawInput.trim().removePrefix("#").removePrefix("k").removePrefix("K")
    val numeric = clean.toIntOrNull() ?: return null
    if (numeric in 1..25) {
      return DIRECTIVES.getOrNull(numeric - 1)
    }
    return null
  }
}
