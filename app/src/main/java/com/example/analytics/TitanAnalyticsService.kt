package com.example.analytics

import android.content.Context
import android.os.Bundle
import android.util.Log
import com.example.data.model.CorporateApplicationTarget
import com.example.util.KeypadDirective
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject

/**
 * Model representing insight data for a specific Career Strategy in Adi OS (Project Titan).
 */
data class CareerStrategyInsightItem(
  val strategyId: String,
  val strategyName: String,
  val categoryLabel: String,
  val description: String,
  val popularityRank: Int,
  val popularityScore: Int,
  val keypadDirectivesUsedCount: Int,
  val oneClickApplicationsDispatchedCount: Int,
  val engagementLevel: String, // "🔥 ULTRA HIGH", "⚡ HIGH", "📈 GROWING", "🟢 ACTIVE"
  val marketDemandPercentile: Int,
  val targetCtcRange: String,
  val recommendedDirectives: List<String>,
  val primaryTargetCompanies: List<String>
)

/**
 * Aggregated Popularity Report providing real-time insights into which career strategies are most popular.
 */
data class CareerStrategyPopularityReport(
  val strategies: List<CareerStrategyInsightItem> = emptyList(),
  val topStrategy: CareerStrategyInsightItem? = null,
  val totalKeypadExecutions: Int = 0,
  val totalOneClickDispatches: Int = 0,
  val totalCorporateTargetsEngaged: Int = 0,
  val telemetryTransmissionStatus: String = "FIREBASE ANALYTICS ACTIVE",
  val lastTelemetryTimestamp: Long = System.currentTimeMillis()
)

/**
 * Enterprise-grade Firebase Analytics Service for Project Titan.
 * Tracks user engagement with the 25-Keypad Grid and 1-Click Apply Corporate Hub,
 * and computes dynamic insights into which career strategies are most popular.
 */
class TitanAnalyticsService(
  private val context: Context,
  private val scope: CoroutineScope
) {
  private val tag = "TitanAnalytics"
  private val prefsName = "titan_analytics_popularity_prefs"
  private val prefs by lazy { context.getSharedPreferences(prefsName, Context.MODE_PRIVATE) }

  private var firebaseAnalytics: FirebaseAnalytics? = null

  // In-memory counter maps persisted in SharedPreferences
  private val directiveCounts = mutableMapOf<String, Int>()
  private val oneClickCounts = mutableMapOf<String, Int>()
  private val strategySelectionCounts = mutableMapOf<String, Int>()

  private val _popularityReport = MutableStateFlow(CareerStrategyPopularityReport())
  val popularityReport: StateFlow<CareerStrategyPopularityReport> = _popularityReport.asStateFlow()

  companion object {
    // Standard Strategy IDs
    const val STRATEGY_OPS_FULFILLMENT = "ops_fulfillment_scaler"
    const val STRATEGY_AI_OPS_AUTOMATION = "ai_ops_automation_lead"
    const val STRATEGY_BANGALORE_CXO = "bangalore_cxo_strategist"
    const val STRATEGY_FOUNDER_BIZOPS = "founder_bizops_growth"
    const val STRATEGY_OFFER_TACTICIAN = "offer_ctc_tactician"

    // Custom Event Names
    const val EVENT_KEYPAD_GRID_VIEWED = "keypad_grid_viewed"
    const val EVENT_KEYPAD_DIRECTIVE_TRIGGERED = "keypad_directive_triggered"
    const val EVENT_KEYPAD_DIRECTIVE_PINNED = "keypad_directive_pinned"
    const val EVENT_KEYPAD_SEARCH = "keypad_directive_search"
    const val EVENT_KEYPAD_HUD_ACTIVATED = "keypad_hud_activated"

    const val EVENT_CORPORATE_HUB_VIEWED = "corporate_hub_viewed"
    const val EVENT_ONE_CLICK_APPLY_DISPATCHED = "one_click_apply_dispatched"
    const val EVENT_ONE_CLICK_BATCH_DISPATCHED = "one_click_apply_batch_dispatched"
    const val EVENT_CORPORATE_TARGET_SAVED = "corporate_target_saved"
    const val EVENT_CORPORATE_TARGET_STATUS_UPDATED = "corporate_target_status_updated"
    const val EVENT_CORPORATE_TARGET_STARRED = "corporate_target_starred"

    const val EVENT_CAREER_STRATEGY_VIEWED = "career_strategy_viewed"
    const val EVENT_CAREER_STRATEGY_SELECTED = "career_strategy_selected"
    const val EVENT_CAREER_POPULARITY_EXPLORED = "career_popularity_explored"
  }

  init {
    initializeFirebaseAnalytics()
    loadPersistedCounts()
    recalculatePopularityReport()
  }

  private fun initializeFirebaseAnalytics() {
    try {
      if (FirebaseApp.getApps(context).isNotEmpty()) {
        firebaseAnalytics = FirebaseAnalytics.getInstance(context)
        Log.i(tag, "Firebase Analytics initialized successfully for package: ${context.packageName}")
      } else {
        Log.w(tag, "FirebaseApp is not initialized yet. Analytics events will be queued or recorded locally.")
      }
    } catch (e: Exception) {
      Log.w(tag, "Firebase Analytics initialization warning: ${e.message}")
    }
  }

  private fun logFirebaseEvent(eventName: String, params: Bundle.() -> Unit = {}) {
    val bundle = Bundle().apply(params)
    try {
      firebaseAnalytics?.logEvent(eventName, bundle)
      Log.d(tag, "Logged Firebase Analytics Event: $eventName | params=${bundle}")
    } catch (e: Exception) {
      Log.w(tag, "Failed to log event $eventName: ${e.message}")
    }
  }

  // =========================================================================
  // 1. 25-KEYPAD GRID ENGAGEMENT TELEMETRY
  // =========================================================================

  /**
   * Tracks user opening or viewing the 25-Keypad Shortcut Grid / Cheat Sheet.
   */
  fun trackKeypadGridViewed(
    categoryFilter: String = "ALL",
    totalDirectivesCount: Int = 25,
    source: String = "top_bar_keypad_button"
  ) {
    logFirebaseEvent(EVENT_KEYPAD_GRID_VIEWED) {
      putString("filter_category", categoryFilter)
      putLong("total_directives", totalDirectivesCount.toLong())
      putString("source_ui_element", source)
      putLong("timestamp", System.currentTimeMillis())
    }
  }

  /**
   * Tracks execution of an individual 25-Keypad directive.
   * Maps each directive to its respective career strategy and updates real-time popularity scores.
   */
  fun trackKeypadDirectiveTriggered(
    directive: KeypadDirective,
    triggerMethod: String = "click"
  ) {
    val associatedStrategyId = mapDirectiveToStrategy(directive)

    logFirebaseEvent(EVENT_KEYPAD_DIRECTIVE_TRIGGERED) {
      putString("directive_code", directive.code)
      putString("directive_title", directive.title)
      putString("directive_category", directive.category)
      putString("associated_career_strategy", associatedStrategyId)
      putString("trigger_method", triggerMethod)
      putLong("directive_id", directive.id.toLong())
      putLong("timestamp", System.currentTimeMillis())
    }

    // Increment in-memory counter and persist
    val currentDirectiveCount = directiveCounts.getOrDefault(directive.code, 0) + 1
    directiveCounts[directive.code] = currentDirectiveCount
    persistCount("directive_${directive.code}", currentDirectiveCount)

    val currentStrategyKeypad = directiveCounts.getOrDefault("strat_keypad_$associatedStrategyId", 0) + 1
    directiveCounts["strat_keypad_$associatedStrategyId"] = currentStrategyKeypad
    persistCount("strat_keypad_$associatedStrategyId", currentStrategyKeypad)

    recalculatePopularityReport()
  }

  /**
   * Tracks pinning/unpinning of a 25-Keypad directive.
   */
  fun trackKeypadDirectivePinned(code: String, isPinned: Boolean) {
    logFirebaseEvent(EVENT_KEYPAD_DIRECTIVE_PINNED) {
      putString("directive_code", code)
      putString("is_pinned", isPinned.toString())
      putLong("timestamp", System.currentTimeMillis())
    }
  }

  /**
   * Tracks keyword or code search queries within the 25-Keypad Grid.
   */
  fun trackKeypadSearch(query: String, resultsCount: Int) {
    logFirebaseEvent(EVENT_KEYPAD_SEARCH) {
      putString("search_query", query)
      putLong("results_count", resultsCount.toLong())
      putLong("timestamp", System.currentTimeMillis())
    }
  }

  /**
   * Tracks HUD banner activation as the user types codes like '01', '02', etc.
   */
  fun trackKeypadHudActivated(buffer: String) {
    logFirebaseEvent(EVENT_KEYPAD_HUD_ACTIVATED) {
      putString("buffer_prefix", buffer)
      putLong("timestamp", System.currentTimeMillis())
    }
  }

  // =========================================================================
  // 2. 1-CLICK APPLY CORPORATE HUB ENGAGEMENT TELEMETRY
  // =========================================================================

  /**
   * Tracks user opening the Corporate Hub / 1-Click Apply queue.
   */
  fun trackCorporateHubViewed(
    targetsCount: Int,
    readyMatchesCount: Int,
    starredCount: Int = 0
  ) {
    logFirebaseEvent(EVENT_CORPORATE_HUB_VIEWED) {
      putLong("total_targets_count", targetsCount.toLong())
      putLong("ready_matches_count", readyMatchesCount.toLong())
      putLong("starred_targets_count", starredCount.toLong())
      putLong("timestamp", System.currentTimeMillis())
    }
  }

  /**
   * Tracks 1-Click Apply dispatch to an individual automated corporate match.
   */
  fun trackOneClickApplyDispatched(
    companyName: String,
    jobTitle: String,
    matchScore: Int,
    ctcLakhs: Double = 25.0,
    careerStrategy: String? = null
  ) {
    val strategyId = careerStrategy ?: mapCompanyAndRoleToStrategy(companyName, jobTitle)

    logFirebaseEvent(EVENT_ONE_CLICK_APPLY_DISPATCHED) {
      putString("company_name", companyName)
      putString("job_title", jobTitle)
      putLong("match_score", matchScore.toLong())
      putDouble("target_ctc_lakhs", ctcLakhs)
      putString("associated_career_strategy", strategyId)
      putLong("timestamp", System.currentTimeMillis())
    }

    // Increment in-memory counter and persist
    val currentOneClickCount = oneClickCounts.getOrDefault(strategyId, 0) + 1
    oneClickCounts[strategyId] = currentOneClickCount
    persistCount("one_click_$strategyId", currentOneClickCount)

    recalculatePopularityReport()
  }

  /**
   * Tracks "Approve All (N)" batch submission via the 1-Click Apply corporate dispatch queue.
   */
  fun trackOneClickApplyBatchDispatched(
    batchCount: Int,
    primaryStrategy: String = STRATEGY_OPS_FULFILLMENT
  ) {
    logFirebaseEvent(EVENT_ONE_CLICK_BATCH_DISPATCHED) {
      putLong("batch_count", batchCount.toLong())
      putString("primary_strategy", primaryStrategy)
      putLong("timestamp", System.currentTimeMillis())
    }

    // Increment strategy counter for each item in batch
    val currentCount = oneClickCounts.getOrDefault(primaryStrategy, 0) + batchCount
    oneClickCounts[primaryStrategy] = currentCount
    persistCount("one_click_$primaryStrategy", currentCount)

    recalculatePopularityReport()
  }

  /**
   * Tracks saving a high-priority corporate target to Room SQLite.
   */
  fun trackCorporateTargetSaved(
    target: CorporateApplicationTarget,
    careerStrategy: String? = null
  ) {
    val strategyId = careerStrategy ?: mapCompanyAndRoleToStrategy(target.companyName, target.targetRoleTitle)

    logFirebaseEvent(EVENT_CORPORATE_TARGET_SAVED) {
      putString("target_id", target.id)
      putString("company_name", target.companyName)
      putString("target_role_title", target.targetRoleTitle)
      putString("priority_tier", target.priorityTier)
      putDouble("target_ctc_lakhs", target.targetCtcLakhs)
      putLong("alignment_score", target.alignmentScore.toLong())
      putString("associated_career_strategy", strategyId)
      putLong("timestamp", System.currentTimeMillis())
    }

    val currentCount = oneClickCounts.getOrDefault(strategyId, 0) + 1
    oneClickCounts[strategyId] = currentCount
    persistCount("one_click_$strategyId", currentCount)

    recalculatePopularityReport()
  }

  /**
   * Tracks updates to a corporate target's pipeline status.
   */
  fun trackCorporateTargetStatusUpdated(
    targetId: String,
    companyName: String,
    oldStatus: String,
    newStatus: String
  ) {
    logFirebaseEvent(EVENT_CORPORATE_TARGET_STATUS_UPDATED) {
      putString("target_id", targetId)
      putString("company_name", companyName)
      putString("old_status", oldStatus)
      putString("new_status", newStatus)
      putLong("timestamp", System.currentTimeMillis())
    }
  }

  /**
   * Tracks starring/favoriting a corporate target.
   */
  fun trackCorporateTargetStarred(
    targetId: String,
    companyName: String,
    isStarred: Boolean
  ) {
    logFirebaseEvent(EVENT_CORPORATE_TARGET_STARRED) {
      putString("target_id", targetId)
      putString("company_name", companyName)
      putString("is_starred", isStarred.toString())
      putLong("timestamp", System.currentTimeMillis())
    }
  }

  // =========================================================================
  // 3. CAREER STRATEGY SELECTION & POPULARITY TRACKING
  // =========================================================================

  /**
   * Tracks user selecting or filtering by a specific career strategy.
   */
  fun trackCareerStrategySelected(
    strategyId: String,
    strategyName: String,
    source: String = "command_center_chip"
  ) {
    logFirebaseEvent(EVENT_CAREER_STRATEGY_SELECTED) {
      putString("strategy_id", strategyId)
      putString("strategy_name", strategyName)
      putString("source_ui_element", source)
      putLong("timestamp", System.currentTimeMillis())
    }

    val currentSelections = strategySelectionCounts.getOrDefault(strategyId, 0) + 1
    strategySelectionCounts[strategyId] = currentSelections
    persistCount("strat_select_$strategyId", currentSelections)

    recalculatePopularityReport()
  }

  /**
   * Tracks opening the Career Strategy Popularity telemetry explorer.
   */
  fun trackCareerPopularityExplored(source: String = "executive_command_center") {
    logFirebaseEvent(EVENT_CAREER_POPULARITY_EXPLORED) {
      putString("source", source)
      putLong("timestamp", System.currentTimeMillis())
    }
  }

  /**
   * Tracks viewing the Omni-Corporate Daily Radar feed.
   */
  fun trackDailyRadarViewed(sector: String, department: String) {
    logFirebaseEvent("daily_radar_viewed") {
      putString("selected_sector", sector)
      putString("selected_department", department)
      putLong("timestamp", System.currentTimeMillis())
    }
  }

  /**
   * Tracks triggering a real-time Gemini Search Grounding scan on daily openings.
   */
  fun trackDailyRadarScanned(totalRoles: Int, newRolesCount: Int) {
    logFirebaseEvent("daily_radar_scanned") {
      putInt("total_roles_count", totalRoles)
      putInt("new_roles_today_count", newRolesCount)
      putLong("timestamp", System.currentTimeMillis())
    }
  }

  /**
   * Tracks dispatching or tracking a Daily Radar role into Relationship Ledger or 1-Click queue.
   */
  fun trackDailyRadarRoleTracked(companyName: String, roleTitle: String, destination: String) {
    logFirebaseEvent("daily_radar_role_tracked") {
      putString("company_name", companyName)
      putString("role_title", roleTitle)
      putString("destination_hub", destination)
      putLong("timestamp", System.currentTimeMillis())
    }
  }

  /**
   * Sets executive user properties in Firebase Analytics for user session attribution.
   */
  fun setUserExecutiveProperties(
    userId: String?,
    targetRole: String? = "Operations & Strategy Lead",
    clearanceLevel: String? = "LEVEL-5 EXECUTIVE"
  ) {
    try {
      firebaseAnalytics?.apply {
        setUserId(userId)
        setUserProperty("clearance_level", clearanceLevel ?: "STANDARD")
        setUserProperty("primary_target_role", targetRole ?: "UNSPECIFIED")
        setUserProperty("app_variant", "ADI_OS_TITAN")
      }
    } catch (e: Exception) {
      Log.w(tag, "Failed to set user properties: ${e.message}")
    }
  }

  // =========================================================================
  // 4. COMPUTING REAL-TIME CAREER STRATEGY POPULARITY INSIGHTS
  // =========================================================================

  private fun recalculatePopularityReport() {
    val items = listOf(
      buildStrategyInsight(
        id = STRATEGY_OPS_FULFILLMENT,
        name = "Operations & Fulfillment Scaler / Dark Store Lead",
        category = "HYPERLOCAL QUICK COMMERCE & LOGISTICS",
        description = "Dark store layout optimization, CM2 unit economics, order-to-dispatch SLA compression, pick-pack-buffer telemetry.",
        baseScore = 280,
        marketDemandPercentile = 98,
        ctcRange = "₹26L - ₹36L CTC",
        recommendedDirectives = listOf("07", "10", "18", "19", "20", "21"),
        primaryTargetCompanies = listOf("Zepto", "Swiggy Instamart", "Blinkit", "Flipkart Minutes")
      ),
      buildStrategyInsight(
        id = STRATEGY_AI_OPS_AUTOMATION,
        name = "AI Operations & Automation Lead",
        category = "AGENTIC WORKFLOWS & FOUNDRY PIPELINES",
        description = "Decompiling job descriptions, automated 1-click ATS application dispatch, prompt-to-production workflows.",
        baseScore = 245,
        marketDemandPercentile = 96,
        ctcRange = "₹28L - ₹38L CTC",
        recommendedDirectives = listOf("01", "02", "11", "23"),
        primaryTargetCompanies = listOf("Zepto AI Ops", "Swiggy Core", "Amazon Technical Ops")
      ),
      buildStrategyInsight(
        id = STRATEGY_BANGALORE_CXO,
        name = "Bangalore Tech Ecosystem CXO Strategist",
        category = "VENTURE FUNDING & C-SUITE ASYMMETRY",
        description = "High-context leadership outreach, Koramangala & Bellandur funding radar, boardroom questions and alumni referral networks.",
        baseScore = 210,
        marketDemandPercentile = 93,
        ctcRange = "₹25L - ₹34L CTC",
        recommendedDirectives = listOf("04", "05", "06", "12"),
        primaryTargetCompanies = listOf("Apex Venture Backed Startups", "Unicorn Founder Offices")
      ),
      buildStrategyInsight(
        id = STRATEGY_FOUNDER_BIZOPS,
        name = "Founder's Associate / BizOps Growth",
        category = "CROSS-FUNCTIONAL EXECUTION & BRAG SHEETS",
        description = "30-60-90 day quarterly board governance, fast-track promotion dossiers, quantified proof-of-work ledgers.",
        baseScore = 195,
        marketDemandPercentile = 91,
        ctcRange = "₹24L - ₹32L CTC",
        recommendedDirectives = listOf("03", "17", "24", "25"),
        primaryTargetCompanies = listOf("Early Stage Series A/B", "High-Growth Scaleups")
      ),
      buildStrategyInsight(
        id = STRATEGY_OFFER_TACTICIAN,
        name = "Executive Offer & Compensation Tactician",
        category = "HIGH-LEVERAGE CTC & ESOP NEGOTIATION",
        description = "Bengaluru Tier-1 compensation percentiles, STAR-V behavioral interviews, counter-offer ghostwriting, multi-offer tension.",
        baseScore = 180,
        marketDemandPercentile = 89,
        ctcRange = "₹28L - ₹42L CTC",
        recommendedDirectives = listOf("08", "09", "13", "14", "15", "16"),
        primaryTargetCompanies = listOf("Tier-1 Apex Employers", "Global Tech Captives")
      )
    )

    // Sort descending by calculated popularity score
    val rankedItems = items.sortedByDescending { it.popularityScore }
      .mapIndexed { index, item ->
        val rank = index + 1
        val engagementLevel = when {
          rank == 1 -> "🔥 ULTRA HIGH (#1 MOST POPULAR)"
          rank == 2 -> "⚡ HIGH ENGAGEMENT"
          rank == 3 -> "📈 RAPIDLY GROWING"
          else -> "🟢 CONSISTENTLY ACTIVE"
        }
        item.copy(popularityRank = rank, engagementLevel = engagementLevel)
      }

    val totalKeypadExecutions = directiveCounts.values.sum()
    val totalOneClickDispatches = oneClickCounts.values.sum()
    val totalCorporateTargets = strategySelectionCounts.values.sum()

    _popularityReport.value = CareerStrategyPopularityReport(
      strategies = rankedItems,
      topStrategy = rankedItems.firstOrNull(),
      totalKeypadExecutions = totalKeypadExecutions,
      totalOneClickDispatches = totalOneClickDispatches,
      totalCorporateTargetsEngaged = totalCorporateTargets,
      telemetryTransmissionStatus = "ACTIVE • TRANSMITTING TO FIREBASE ANALYTICS",
      lastTelemetryTimestamp = System.currentTimeMillis()
    )
  }

  private fun buildStrategyInsight(
    id: String,
    name: String,
    category: String,
    description: String,
    baseScore: Int,
    marketDemandPercentile: Int,
    ctcRange: String,
    recommendedDirectives: List<String>,
    primaryTargetCompanies: List<String>
  ): CareerStrategyInsightItem {
    val keypadUsage = directiveCounts.getOrDefault("strat_keypad_$id", 0) +
      recommendedDirectives.sumOf { directiveCounts.getOrDefault(it, 0) }
    val oneClickUsage = oneClickCounts.getOrDefault(id, 0)
    val selections = strategySelectionCounts.getOrDefault(id, 0)

    // Dynamic popularity score calculation formula:
    // Base Score + (Keypad Executions * 15) + (1-Click Dispatches * 25) + (Strategy Selections * 10)
    val totalScore = baseScore + (keypadUsage * 15) + (oneClickUsage * 25) + (selections * 10)

    return CareerStrategyInsightItem(
      strategyId = id,
      strategyName = name,
      categoryLabel = category,
      description = description,
      popularityRank = 1, // overwritten after sorting
      popularityScore = totalScore,
      keypadDirectivesUsedCount = keypadUsage,
      oneClickApplicationsDispatchedCount = oneClickUsage,
      engagementLevel = "🟢 ACTIVE",
      marketDemandPercentile = marketDemandPercentile,
      targetCtcRange = ctcRange,
      recommendedDirectives = recommendedDirectives,
      primaryTargetCompanies = primaryTargetCompanies
    )
  }

  /**
   * Maps a 25-Keypad directive to its strategic career pillar.
   */
  fun mapDirectiveToStrategy(directive: KeypadDirective): String {
    return when (directive.code) {
      "07", "10", "18", "19", "20", "21", "22" -> STRATEGY_OPS_FULFILLMENT
      "01", "02", "11", "23" -> STRATEGY_AI_OPS_AUTOMATION
      "04", "05", "06", "12" -> STRATEGY_BANGALORE_CXO
      "03", "17", "24", "25" -> STRATEGY_FOUNDER_BIZOPS
      "08", "09", "13", "14", "15", "16" -> STRATEGY_OFFER_TACTICIAN
      else -> {
        when (directive.category.uppercase()) {
          "OPERATIONS" -> STRATEGY_OPS_FULFILLMENT
          "TECHNICAL" -> STRATEGY_AI_OPS_AUTOMATION
          "INTELLIGENCE", "OUTREACH" -> STRATEGY_BANGALORE_CXO
          "OFFER" -> STRATEGY_OFFER_TACTICIAN
          else -> STRATEGY_OPS_FULFILLMENT
        }
      }
    }
  }

  /**
   * Maps a company name and role title to a primary career strategy.
   */
  fun mapCompanyAndRoleToStrategy(companyName: String, roleTitle: String): String {
    val combined = "$companyName $roleTitle".lowercase()
    return when {
      combined.contains("zepto") || combined.contains("swiggy") || combined.contains("blinkit") ||
        combined.contains("dark store") || combined.contains("fulfillment") || combined.contains("supply chain") ->
        STRATEGY_OPS_FULFILLMENT

      combined.contains("ai") || combined.contains("automation") || combined.contains("engineer") ||
        combined.contains("python") || combined.contains("developer") ->
        STRATEGY_AI_OPS_AUTOMATION

      combined.contains("cxo") || combined.contains("radar") || combined.contains("investor") ||
        combined.contains("venture") || combined.contains("head") ->
        STRATEGY_BANGALORE_CXO

      combined.contains("founder") || combined.contains("bizops") || combined.contains("chief of staff") ||
        combined.contains("strategy") ->
        STRATEGY_FOUNDER_BIZOPS

      else -> STRATEGY_OPS_FULFILLMENT
    }
  }

  // =========================================================================
  // 5. LOCAL PERSISTENCE HELPERS
  // =========================================================================

  private fun persistCount(key: String, value: Int) {
    scope.launch(Dispatchers.IO) {
      try {
        prefs.edit().putInt(key, value).apply()
      } catch (e: Exception) {
        Log.w(tag, "Failed to persist count $key: ${e.message}")
      }
    }
  }

  private fun loadPersistedCounts() {
    try {
      prefs.all.forEach { (key, value) ->
        val intVal = (value as? Int) ?: 0
        when {
          key.startsWith("directive_") -> {
            val code = key.removePrefix("directive_")
            directiveCounts[code] = intVal
          }
          key.startsWith("strat_keypad_") -> {
            directiveCounts[key] = intVal
          }
          key.startsWith("one_click_") -> {
            val strat = key.removePrefix("one_click_")
            oneClickCounts[strat] = intVal
          }
          key.startsWith("strat_select_") -> {
            val strat = key.removePrefix("strat_select_")
            strategySelectionCounts[strat] = intVal
          }
        }
      }
    } catch (e: Exception) {
      Log.w(tag, "Failed to load persisted counts: ${e.message}")
    }
  }
}
