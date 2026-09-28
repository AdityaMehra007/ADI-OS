package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ObsidianDark
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateCard
import com.example.ui.theme.SlateElevated
import com.example.ui.theme.TextMutedDark
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import com.example.ui.theme.TitanCrimson
import com.example.ui.theme.TitanCyan
import com.example.ui.theme.TitanEmerald
import com.example.ui.theme.TitanGold
import com.example.ui.theme.TitanIndigo
import com.example.ui.theme.TitanViolet
import kotlin.math.cos
import kotlin.math.sin

/**
 * Data Model for Career Growth Milestones on the Strategy Horizon.
 */
data class StrategyGrowthMilestone(
  val id: String,
  val phaseNumber: Int,
  val phaseTitle: String,
  val milestoneTitle: String,
  val domain: String,
  val completionPct: Int,
  val isAcquired: Boolean,
  val targetQuarter: String,
  val evidenceCitation: String,
  val impactMetric: String
)

/**
 * Target Role Alignment Evaluation Vector.
 */
data class CompetencyAlignmentVector(
  val name: String,
  val candidateScore: Int, // 0 - 100
  val targetBenchmark: Int, // 0 - 100
  val category: String,
  val strategicRationale: String
) {
  val surplusPct: Int get() = candidateScore - targetBenchmark
}

/**
 * Target Executive Role Profile.
 */
data class StrategyTargetRole(
  val id: String,
  val title: String,
  val targetOrganizationTier: String,
  val overallAlignmentPct: Int,
  val compensationRange: String,
  val primaryLeveragePoint: String,
  val vectors: List<CompetencyAlignmentVector>
)

enum class StrategyHudTab(val label: String, val badge: String) {
  ALIGNMENT_RADAR("Role Alignment", "94% Match"),
  MILESTONE_TRAJECTORY("Growth Milestones", "7/9 Cleared"),
  COMPETENCY_VECTORS("Skill Surpluses", "+8% Avg"),
  EXECUTIVE_DIRECTIVES("Action Vectors", "3 High-Leverage")
}

/**
 * Career Strategy Dashboard HUD:
 * High-precision executive dashboard component visualizing career growth milestones,
 * role target alignment metrics, and competency vector surpluses.
 */
@Composable
fun CareerStrategyOperationalHud(
  modifier: Modifier = Modifier,
  initialExpanded: Boolean = true,
  onNavigateToRoadmap: (() -> Unit)? = null
) {
  val haptic = LocalHapticFeedback.current
  var isExpanded by remember { mutableStateOf(initialExpanded) }
  var selectedTab by remember { mutableStateOf(StrategyHudTab.ALIGNMENT_RADAR) }
  var selectedRoleId by remember { mutableStateOf("STRAT_OPS_LEAD") }
  var simulationTick by remember { mutableIntStateOf(0) }

  // Pulsing animation for real-time strategic alignment telemetry
  val infiniteTransition = rememberInfiniteTransition(label = "strategy_hud_pulse")
  val pulseAlpha by infiniteTransition.animateFloat(
    initialValue = 0.4f,
    targetValue = 1.0f,
    animationSpec = infiniteRepeatable(
      animation = tween(1000, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "pulse_alpha"
  )

  // Target Roles Dataset (Adi's verified target positions)
  val targetRoles = remember {
    listOf(
      StrategyTargetRole(
        id = "STRAT_OPS_LEAD",
        title = "Lead - Strategy & Operations",
        targetOrganizationTier = "Tier-1 Quick Commerce (Zepto, Swiggy Instamart, Blinkit)",
        overallAlignmentPct = 94,
        compensationRange = "₹28L - ₹36L Fixed + ESOPs",
        primaryLeveragePoint = "14 Bengaluru Dark Store Network Turnaround & -42% Cycle Reduction",
        vectors = listOf(
          CompetencyAlignmentVector("Supply Chain Velocity", 98, 90, "OPERATIONS", "380s → 223s dispatch cycle in 14 urban hubs"),
          CompetencyAlignmentVector("CM2 Margin Optimization", 94, 85, "FINANCIAL", "+₹28.40 cash surplus per drop unit economics"),
          CompetencyAlignmentVector("Cross-Functional Operations", 99, 88, "LEADERSHIP", "AERO India 2025: 300+ chalets with 0.00% downtime"),
          CompetencyAlignmentVector("SQL & Telemetry Architecture", 88, 80, "TECH", "Real-time picking latency and stage queues"),
          CompetencyAlignmentVector("Vendor SLAs & Procurement", 92, 85, "SUPPLY", "Puma India e-commerce reconciliation & SLAs")
        )
      ),
      StrategyTargetRole(
        id = "CHIEF_OF_STAFF",
        title = "Chief of Staff (Operations & Scaled Growth)",
        targetOrganizationTier = "High-Growth Unicorns / C-Suite Founder Office",
        overallAlignmentPct = 89,
        compensationRange = "₹32L - ₹42L + Performance Bonus",
        primaryLeveragePoint = "Rigorous 100% First-Attempt Academic Record (41/41) & High-Stress Execution",
        vectors = listOf(
          CompetencyAlignmentVector("Executive Problem Deconstruction", 96, 90, "STRATEGY", "BBA International Business DSU First-Class Honors"),
          CompetencyAlignmentVector("Operational SLA Rigor", 97, 88, "OPERATIONS", "Zero operational downtime across high-security VIP chalets"),
          CompetencyAlignmentVector("Cross-Department Governance", 90, 85, "LEADERSHIP", "Tata Communications SLAs & Instawork QA >99%"),
          CompetencyAlignmentVector("Financial P&L Modeling", 86, 88, "FINANCIAL", "Aladdin macro risk factor beta and cost stress-testing"),
          CompetencyAlignmentVector("Strategic Communications", 91, 90, "EXECUTIVE", "2-min CXO elevator hooks & boardroom narratives")
        )
      ),
      StrategyTargetRole(
        id = "GLOBAL_OPS_ARCH",
        title = "Global Operations Architect",
        targetOrganizationTier = "Multinational Enterprise / Supply Chain Platforms",
        overallAlignmentPct = 86,
        compensationRange = "₹35L - ₹48L Total Comp",
        primaryLeveragePoint = "International Business Specialization & End-to-End Distribution Architecture",
        vectors = listOf(
          CompetencyAlignmentVector("International Trade & Logistics", 95, 88, "GLOBAL", "Dayananda Sagar University BBA IB (Class of 2026)"),
          CompetencyAlignmentVector("Micro-Fulfillment Hub Design", 98, 85, "OPERATIONS", "Dark store velocity zoning and cross-dock layout"),
          CompetencyAlignmentVector("Algorithm Governance", 87, 82, "AI_OPS", "AI annotation workflow automation with >99% accuracy"),
          CompetencyAlignmentVector("Enterprise Scalability", 85, 90, "ENTERPRISE", "Managed 14-hub network serving 10,000+ daily drops"),
          CompetencyAlignmentVector("Risk & Contingency Engineering", 92, 86, "RISK", "Monsoon surge routing & fuel price volatility buffer")
        )
      )
    )
  }

  val activeRole = targetRoles.find { it.id == selectedRoleId } ?: targetRoles.first()

  // Chronological Growth Milestones (Adi's verified roadmap)
  val milestones = remember(simulationTick) {
    listOf(
      StrategyGrowthMilestone("M1", 1, "Phase 1: Academic & Operational Foundation", "DSU International Business 100% Clearance", "ACADEMIC", 100, true, "Q1 2025", "41/41 subjects cleared on first attempt with 0 backlogs", "Academic Purity Anchor"),
      StrategyGrowthMilestone("M2", 1, "Phase 1: Academic & Operational Foundation", "AERO India 2025 Defense Pavilion Operations", "DEFENSE_OPS", 100, true, "Q1 2025", "Coordinated 300+ chalets with 0.00% downtime", "100% Operational Reliability"),
      StrategyGrowthMilestone("M3", 1, "Phase 1: Academic & Operational Foundation", "Instawork AI QA & Workflow Annotation", "AI_OPERATIONS", 100, true, "Q2 2025", ">99% annotation verification accuracy across workflows", "AI Telemetry Quality"),
      StrategyGrowthMilestone("M4", 2, "Phase 2: Quick-Commerce Dark Store Turnaround", "14 Bengaluru Distribution Hubs Re-Architecture", "SUPPLY_CHAIN", 100, true, "Q3 2025", "Engineered cross-dock & velocity zoning across 14 hubs", "42% Cycle Reduction (380s→223s)"),
      StrategyGrowthMilestone("M5", 2, "Phase 2: Quick-Commerce Dark Store Turnaround", "Positive Unit Economics & CM2 Optimization", "FINANCIAL_OPS", 100, true, "Q4 2025", "+₹28.40 CM2 cash surplus per drop", "Sustained Positive Unit Economics"),
      StrategyGrowthMilestone("M6", 2, "Phase 2: Quick-Commerce Dark Store Turnaround", "Puma India & Tata Communications SLA Audits", "ENTERPRISE_SLA", 100, true, "Q1 2026", "Enterprise infrastructure SLAs & e-commerce reconciliations", "0 Defect Reconciliation"),
      StrategyGrowthMilestone("M7", 3, "Phase 3: Executive C-Suite Alignment", "WorkManager Background Telemetry Daemon", "SYSTEM_ARCH", 100, true, "Q2 2026", "Automated 4H periodic status polling across target firms", "Zero-Failure ATS Synchronization"),
      StrategyGrowthMilestone("M8", 3, "Phase 3: Executive C-Suite Alignment", "BlackRock Aladdin Macro Risk Factor Stress-Testing", "MACRO_STRAT", 82, false, "Q3 2026", "R_p - R_f = β(R_m - R_f) + α stress models on fuel and monsoon", "In-Flight (82% Calibrated)"),
      StrategyGrowthMilestone("M9", 3, "Phase 3: Executive C-Suite Alignment", "Full C-Suite Council Consensus & Boardroom Pitch", "EXECUTIVE", 75, false, "Q4 2026", "12-agent C-suite council automated decision alignment", "In-Flight (75% Prepared)")
    )
  }

  val completedMilestonesCount = milestones.count { it.isAcquired }
  val totalMilestonesCount = milestones.size
  val overallRoadmapProgressPct = (completedMilestonesCount * 100) / totalMilestonesCount

  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("career_strategy_operational_hud_card"),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = SlateCard),
    border = androidx.compose.foundation.BorderStroke(1.5.dp, TitanCyan.copy(alpha = 0.65f))
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
    ) {
      // 1. TOP HEADER STRIP WITH LIVE ALIGNMENT PULSE
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(9.dp)
                .clip(CircleShape)
                .background(TitanCyan.copy(alpha = pulseAlpha))
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "CAREER STRATEGY HUD",
              fontSize = 11.sp,
              fontWeight = FontWeight.Black,
              fontFamily = FontFamily.Monospace,
              color = TitanCyan,
              letterSpacing = 1.2.sp
            )
            Spacer(modifier = Modifier.width(8.dp))
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(TitanEmerald.copy(alpha = 0.15f))
                .border(1.dp, TitanEmerald.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
                .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
              Text(
                text = "${activeRole.overallAlignmentPct}% TARGET FIT",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = TitanEmerald
              )
            }
          }

          Spacer(modifier = Modifier.height(3.dp))
          Text(
            text = activeRole.title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = TextPrimaryDark,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
          Text(
            text = "Target: ${activeRole.targetOrganizationTier}",
            fontSize = 11.sp,
            color = TextSecondaryDark,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          IconButton(
            onClick = {
              haptic.performHapticFeedback(HapticFeedbackType.LongPress)
              simulationTick++
            },
            modifier = Modifier
              .size(36.dp)
              .clip(CircleShape)
              .background(SlateElevated)
              .testTag("strategy_hud_refresh_button")
          ) {
            Icon(
              imageVector = Icons.Default.Refresh,
              contentDescription = "Recalculate Alignment",
              tint = TitanCyan,
              modifier = Modifier.size(18.dp)
            )
          }

          Spacer(modifier = Modifier.width(6.dp))

          IconButton(
            onClick = {
              haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
              isExpanded = !isExpanded
            },
            modifier = Modifier
              .size(36.dp)
              .clip(CircleShape)
              .background(SlateElevated)
              .testTag("strategy_hud_toggle_expand")
          ) {
            Icon(
              imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
              contentDescription = if (isExpanded) "Collapse Strategy HUD" else "Expand Strategy HUD",
              tint = TitanCyan,
              modifier = Modifier.size(20.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // 2. MACRO KPIS METRICS STRIP
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(10.dp))
          .background(ObsidianDark)
          .border(1.dp, SlateBorder, RoundedCornerShape(10.dp))
          .padding(vertical = 10.dp, horizontal = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Metric 1: Overall Alignment
        Column(modifier = Modifier.weight(1f)) {
          Text("ROLE ALIGNMENT", fontSize = 8.5.sp, fontFamily = FontFamily.Monospace, color = TextMutedDark, fontWeight = FontWeight.Bold)
          Row(verticalAlignment = Alignment.Bottom) {
            Text(
              text = "${activeRole.overallAlignmentPct}%",
              fontSize = 17.sp,
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Black,
              color = TitanCyan
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "STRONG",
              fontSize = 9.sp,
              fontFamily = FontFamily.Monospace,
              color = TitanEmerald,
              fontWeight = FontWeight.Bold
            )
          }
          Text("vs Tier-1 standard", fontSize = 8.sp, color = TextMutedDark)
        }

        Box(modifier = Modifier.width(1.dp).height(32.dp).background(SlateBorder))

        // Metric 2: Milestones Acquired
        Column(modifier = Modifier.weight(1f).padding(horizontal = 6.dp)) {
          Text("MILESTONES", fontSize = 8.5.sp, fontFamily = FontFamily.Monospace, color = TextMutedDark, fontWeight = FontWeight.Bold)
          Row(verticalAlignment = Alignment.Bottom) {
            Text(
              text = "$completedMilestonesCount/$totalMilestonesCount",
              fontSize = 17.sp,
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Black,
              color = TitanEmerald
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text("done", fontSize = 9.sp, color = TextMutedDark)
          }
          Text("$overallRoadmapProgressPct% pacing", fontSize = 8.sp, color = TitanEmerald)
        }

        Box(modifier = Modifier.width(1.dp).height(32.dp).background(SlateBorder))

        // Metric 3: Academic & Operations Rigor
        Column(modifier = Modifier.weight(1.1f).padding(horizontal = 6.dp)) {
          Text("PURITY RECORD", fontSize = 8.5.sp, fontFamily = FontFamily.Monospace, color = TextMutedDark, fontWeight = FontWeight.Bold)
          Row(verticalAlignment = Alignment.Bottom) {
            Text(
              text = "41/41",
              fontSize = 17.sp,
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Black,
              color = TitanGold
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text("0 backlogs", fontSize = 9.sp, color = TitanGold)
          }
          Text("100% 1st-attempt (DSU)", fontSize = 8.sp, color = TextMutedDark)
        }

        Box(modifier = Modifier.width(1.dp).height(32.dp).background(SlateBorder))

        // Metric 4: Compensation Target
        Column(modifier = Modifier.weight(0.9f), horizontalAlignment = Alignment.End) {
          Text("TARGET COMP", fontSize = 8.5.sp, fontFamily = FontFamily.Monospace, color = TextMutedDark, fontWeight = FontWeight.Bold)
          Text(
            text = "₹28L-₹36L",
            fontSize = 15.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Black,
            color = TitanEmerald
          )
          Text("Fixed + ESOPs", fontSize = 8.sp, fontFamily = FontFamily.Monospace, color = TextMutedDark)
        }
      }

      // EXPANDED SECTION: VISUALIZERS & INTERACTIVE HUDS
      AnimatedVisibility(
        visible = isExpanded,
        enter = fadeIn(),
        exit = fadeOut()
      ) {
        Column(modifier = Modifier.fillMaxWidth().padding(top = 14.dp)) {
          // 3. TARGET ROLE SELECTOR PILLS
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            targetRoles.forEach { role ->
              val isSelected = selectedRoleId == role.id
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(8.dp))
                  .background(if (isSelected) TitanCyan.copy(alpha = 0.2f) else SlateElevated)
                  .border(
                    width = 1.dp,
                    color = if (isSelected) TitanCyan else SlateBorder,
                    shape = RoundedCornerShape(8.dp)
                  )
                  .clickable {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    selectedRoleId = role.id
                  }
                  .padding(horizontal = 10.dp, vertical = 6.dp)
                  .testTag("target_role_pill_${role.id.lowercase()}")
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(
                    text = role.title,
                    fontSize = 11.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) TitanCyan else TextSecondaryDark
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Box(
                    modifier = Modifier
                      .clip(RoundedCornerShape(4.dp))
                      .background(if (isSelected) TitanEmerald.copy(alpha = 0.25f) else ObsidianDark)
                      .padding(horizontal = 4.dp, vertical = 1.dp)
                  ) {
                    Text(
                      text = "${role.overallAlignmentPct}%",
                      fontSize = 9.sp,
                      fontFamily = FontFamily.Monospace,
                      fontWeight = FontWeight.Bold,
                      color = if (isSelected) TitanEmerald else TextMutedDark
                    )
                  }
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // 4. TAB SELECTOR PILLS
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            StrategyHudTab.values().forEach { tab ->
              val isSelected = selectedTab == tab
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(8.dp))
                  .background(if (isSelected) TitanCyan.copy(alpha = 0.2f) else SlateElevated)
                  .border(
                    width = 1.dp,
                    color = if (isSelected) TitanCyan else SlateBorder,
                    shape = RoundedCornerShape(8.dp)
                  )
                  .clickable {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    selectedTab = tab
                  }
                  .padding(horizontal = 12.dp, vertical = 7.dp)
                  .testTag("strategy_hud_tab_${tab.name.lowercase()}")
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(
                    text = tab.label,
                    fontSize = 11.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) TitanCyan else TextSecondaryDark
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Box(
                    modifier = Modifier
                      .clip(RoundedCornerShape(4.dp))
                      .background(if (isSelected) TitanCyan.copy(alpha = 0.25f) else ObsidianDark)
                      .padding(horizontal = 5.dp, vertical = 1.dp)
                  ) {
                    Text(
                      text = tab.badge,
                      fontSize = 9.sp,
                      fontFamily = FontFamily.Monospace,
                      fontWeight = FontWeight.Bold,
                      color = if (isSelected) TextPrimaryDark else TextMutedDark
                    )
                  }
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // 5. MAIN VISUALIZATION ACCORDING TO SELECTED TAB
          when (selectedTab) {
            StrategyHudTab.ALIGNMENT_RADAR -> {
              AlignmentRadarSection(activeRole = activeRole)
            }
            StrategyHudTab.MILESTONE_TRAJECTORY -> {
              MilestoneTrajectorySection(milestones = milestones)
            }
            StrategyHudTab.COMPETENCY_VECTORS -> {
              CompetencyVectorsSection(vectors = activeRole.vectors)
            }
            StrategyHudTab.EXECUTIVE_DIRECTIVES -> {
              ExecutiveDirectivesSection(activeRole = activeRole)
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // 6. EXECUTIVE FOOTER CITATION
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(8.dp))
              .background(SlateElevated.copy(alpha = 0.6f))
              .border(1.dp, SlateBorder.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
              .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
              Icon(
                imageVector = Icons.Default.Bolt,
                contentDescription = null,
                tint = TitanGold,
                modifier = Modifier.size(14.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "Aditya Mehra Career Strategy • DSU Bengaluru Class of 2026 • 100% Academic Purity",
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                color = TextSecondaryDark,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
            }

            if (onNavigateToRoadmap != null) {
              Text(
                text = "ROADMAP →",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = TitanCyan,
                modifier = Modifier.clickable { onNavigateToRoadmap() }
              )
            }
          }
        }
      }
    }
  }
}

// ============================================================================
// COMPONENT 1: RADAR / PENTAGON TARGET ALIGNMENT CANVAS
// ============================================================================
@Composable
private fun AlignmentRadarSection(activeRole: StrategyTargetRole) {
  val haptic = LocalHapticFeedback.current
  var selectedVectorIndex by remember { mutableIntStateOf(0) }
  val activeVector = activeRole.vectors.getOrNull(selectedVectorIndex) ?: activeRole.vectors.first()

  Column(modifier = Modifier.fillMaxWidth()) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Text(
          text = "5-POINT COMPETENCY ALIGNMENT RADAR",
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          fontFamily = FontFamily.Monospace,
          color = TitanCyan
        )
        Text(
          text = "Candidate Strengths vs. Enterprise Role Benchmark",
          fontSize = 11.sp,
          color = TextSecondaryDark
        )
      }

      Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        LegendPill("Candidate", TitanEmerald)
        LegendPill("Target", TitanGold)
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Interactive Radar Canvas
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(190.dp)
        .clip(RoundedCornerShape(12.dp))
        .background(ObsidianDark)
        .border(1.dp, SlateBorder, RoundedCornerShape(12.dp))
        .pointerInput(activeRole.vectors) {
          detectTapGestures { offset ->
            val centerX = size.width / 2f
            val centerY = size.height / 2f
            val dx = offset.x - centerX
            val dy = offset.y - centerY
            var angle = Math.toDegrees(Math.atan2(dy.toDouble(), dx.toDouble())).toFloat()
            if (angle < 0) angle += 360f
            // 5 slices
            val slice = (angle + 90f - 36f) % 360f
            val index = ((slice / 72f).toInt()).coerceIn(0, activeRole.vectors.size - 1)
            selectedVectorIndex = index
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
          }
        }
        .testTag("strategy_alignment_radar_canvas")
    ) {
      Canvas(modifier = Modifier.fillMaxWidth().height(190.dp)) {
        val centerX = size.width / 2f
        val centerY = size.height / 2f
        val radius = (size.height / 2f) - 22.dp.toPx()
        val vectorCount = activeRole.vectors.size
        val angleStep = (2 * Math.PI / vectorCount).toFloat()

        // 1. Web Concentric Polygons (25%, 50%, 75%, 100%)
        val concentricLevels = listOf(0.25f, 0.50f, 0.75f, 1.0f)
        concentricLevels.forEach { level ->
          val path = Path()
          for (i in 0 until vectorCount) {
            val angle = -Math.PI / 2 + i * angleStep
            val x = centerX + (radius * level * cos(angle)).toFloat()
            val y = centerY + (radius * level * sin(angle)).toFloat()
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
          }
          path.close()
          drawPath(
            path = path,
            color = if (level == 1.0f) SlateBorder else SlateBorder.copy(alpha = 0.35f),
            style = Stroke(width = if (level == 1.0f) 1.2.dp.toPx() else 0.8.dp.toPx())
          )
        }

        // 2. Radiating Axis Lines
        for (i in 0 until vectorCount) {
          val angle = -Math.PI / 2 + i * angleStep
          val endX = centerX + (radius * cos(angle)).toFloat()
          val endY = centerY + (radius * sin(angle)).toFloat()
          drawLine(
            color = SlateBorder.copy(alpha = 0.5f),
            start = Offset(centerX, centerY),
            end = Offset(endX, endY),
            strokeWidth = 1.dp.toPx()
          )
        }

        // 3. Target Role Polygon (Gold)
        val targetPath = Path()
        for (i in 0 until vectorCount) {
          val v = activeRole.vectors[i]
          val frac = (v.targetBenchmark / 100f).coerceIn(0f, 1f)
          val angle = -Math.PI / 2 + i * angleStep
          val x = centerX + (radius * frac * cos(angle)).toFloat()
          val y = centerY + (radius * frac * sin(angle)).toFloat()
          if (i == 0) targetPath.moveTo(x, y) else targetPath.lineTo(x, y)
        }
        targetPath.close()
        drawPath(
          path = targetPath,
          color = TitanGold.copy(alpha = 0.15f)
        )
        drawPath(
          path = targetPath,
          color = TitanGold.copy(alpha = 0.8f),
          style = Stroke(
            width = 1.8.dp.toPx(),
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
          )
        )

        // 4. Candidate Actual Polygon (Emerald)
        val candidatePath = Path()
        for (i in 0 until vectorCount) {
          val v = activeRole.vectors[i]
          val frac = (v.candidateScore / 100f).coerceIn(0f, 1f)
          val angle = -Math.PI / 2 + i * angleStep
          val x = centerX + (radius * frac * cos(angle)).toFloat()
          val y = centerY + (radius * frac * sin(angle)).toFloat()
          if (i == 0) candidatePath.moveTo(x, y) else candidatePath.lineTo(x, y)
        }
        candidatePath.close()
        drawPath(
          path = candidatePath,
          color = TitanEmerald.copy(alpha = 0.28f)
        )
        drawPath(
          path = candidatePath,
          color = TitanEmerald,
          style = Stroke(width = 2.2.dp.toPx())
        )

        // 5. Vertices and Selected Node Highlight
        for (i in 0 until vectorCount) {
          val v = activeRole.vectors[i]
          val frac = (v.candidateScore / 100f).coerceIn(0f, 1f)
          val angle = -Math.PI / 2 + i * angleStep
          val x = centerX + (radius * frac * cos(angle)).toFloat()
          val y = centerY + (radius * frac * sin(angle)).toFloat()

          val isSelected = i == selectedVectorIndex
          drawCircle(
            color = if (isSelected) TitanCyan else TitanEmerald,
            radius = if (isSelected) 5.dp.toPx() else 3.5.dp.toPx(),
            center = Offset(x, y)
          )
          if (isSelected) {
            drawCircle(
              color = ObsidianDark,
              radius = 2.dp.toPx(),
              center = Offset(x, y)
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Interactive Inspector Card for Selected Vector
    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(10.dp),
      colors = CardDefaults.cardColors(containerColor = SlateElevated),
      border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
    ) {
      Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(TitanCyan.copy(alpha = 0.2f))
                .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
              Text(
                text = activeVector.category,
                fontSize = 9.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = TitanCyan
              )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = activeVector.name,
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = TextPrimaryDark
            )
          }

          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = "${activeVector.candidateScore}%",
              fontSize = 14.sp,
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Black,
              color = TitanEmerald
            )
            Text(
              text = " (vs ${activeVector.targetBenchmark}%)",
              fontSize = 11.sp,
              fontFamily = FontFamily.Monospace,
              color = TextMutedDark
            )
            Spacer(modifier = Modifier.width(6.dp))
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(if (activeVector.surplusPct >= 0) TitanEmerald.copy(alpha = 0.2f) else TitanCrimson.copy(alpha = 0.2f))
                .padding(horizontal = 5.dp, vertical = 2.dp)
            ) {
              Text(
                text = "${if (activeVector.surplusPct >= 0) "+" else ""}${activeVector.surplusPct}%",
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Black,
                color = if (activeVector.surplusPct >= 0) TitanEmerald else TitanCrimson
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = activeVector.strategicRationale,
          fontSize = 11.sp,
          color = TextSecondaryDark,
          lineHeight = 15.sp
        )
      }
    }
  }
}

// ============================================================================
// COMPONENT 2: MILESTONE TRAJECTORY & BURN-DOWN SECTION
// ============================================================================
@Composable
private fun MilestoneTrajectorySection(milestones: List<StrategyGrowthMilestone>) {
  Column(modifier = Modifier.fillMaxWidth()) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "CAREER GROWTH TRAJECTORY (CHRONOLOGICAL)",
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = FontFamily.Monospace,
        color = TitanCyan
      )
      Text(
        text = "CLASS OF 2026",
        fontSize = 9.sp,
        fontFamily = FontFamily.Monospace,
        color = TitanGold,
        fontWeight = FontWeight.Bold
      )
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Interactive Milestone Step Cards
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
      milestones.forEach { milestone ->
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(10.dp),
          colors = CardDefaults.cardColors(
            containerColor = if (milestone.isAcquired) SlateElevated else ObsidianDark
          ),
          border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = if (milestone.isAcquired) TitanEmerald.copy(alpha = 0.5f) else SlateBorder
          )
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              modifier = Modifier.weight(1f),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Box(
                modifier = Modifier
                  .size(32.dp)
                  .clip(CircleShape)
                  .background(if (milestone.isAcquired) TitanEmerald.copy(alpha = 0.2f) else TitanGold.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = if (milestone.isAcquired) Icons.Default.CheckCircle else Icons.Default.Flag,
                  contentDescription = null,
                  tint = if (milestone.isAcquired) TitanEmerald else TitanGold,
                  modifier = Modifier.size(16.dp)
                )
              }

              Spacer(modifier = Modifier.width(10.dp))

              Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(
                    text = milestone.phaseTitle.substringBefore(":"),
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = TitanCyan
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = "• ${milestone.targetQuarter}",
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace,
                    color = TextMutedDark
                  )
                }

                Text(
                  text = milestone.milestoneTitle,
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = TextPrimaryDark
                )

                Text(
                  text = milestone.evidenceCitation,
                  fontSize = 10.sp,
                  color = TextSecondaryDark,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
                )
              }
            }

            Column(horizontalAlignment = Alignment.End) {
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(4.dp))
                  .background(if (milestone.isAcquired) TitanEmerald.copy(alpha = 0.2f) else SlateBorder.copy(alpha = 0.3f))
                  .padding(horizontal = 6.dp, vertical = 2.dp)
              ) {
                Text(
                  text = if (milestone.isAcquired) "ACQUIRED" else "${milestone.completionPct}%",
                  fontSize = 9.sp,
                  fontFamily = FontFamily.Monospace,
                  fontWeight = FontWeight.Black,
                  color = if (milestone.isAcquired) TitanEmerald else TitanGold
                )
              }
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = milestone.impactMetric,
                fontSize = 8.5.sp,
                fontFamily = FontFamily.Monospace,
                color = if (milestone.isAcquired) TitanEmerald else TextMutedDark
              )
            }
          }
        }
      }
    }
  }
}

// ============================================================================
// COMPONENT 3: COMPETENCY SURPLUSES & GAP RESOLUTION SECTION
// ============================================================================
@Composable
private fun CompetencyVectorsSection(vectors: List<CompetencyAlignmentVector>) {
  Column(modifier = Modifier.fillMaxWidth()) {
    Text(
      text = "COMPETENCY SURPLUS & STRATEGIC ADVANTAGE",
      fontSize = 11.sp,
      fontWeight = FontWeight.Bold,
      fontFamily = FontFamily.Monospace,
      color = TitanCyan
    )
    Text(
      text = "Verified capability delta above standard industry benchmarks",
      fontSize = 11.sp,
      color = TextSecondaryDark
    )

    Spacer(modifier = Modifier.height(10.dp))

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
      vectors.forEach { vector ->
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(10.dp),
          colors = CardDefaults.cardColors(containerColor = SlateElevated),
          border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
        ) {
          Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = vector.name,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimaryDark
              )

              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = "${vector.candidateScore}%",
                  fontSize = 12.sp,
                  fontFamily = FontFamily.Monospace,
                  fontWeight = FontWeight.Bold,
                  color = TitanEmerald
                )
                Text(
                  text = " / ${vector.targetBenchmark}%",
                  fontSize = 11.sp,
                  fontFamily = FontFamily.Monospace,
                  color = TextMutedDark
                )
                Spacer(modifier = Modifier.width(6.dp))
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(if (vector.surplusPct >= 0) TitanEmerald.copy(alpha = 0.2f) else TitanCrimson.copy(alpha = 0.2f))
                    .padding(horizontal = 5.dp, vertical = 2.dp)
                ) {
                  Text(
                    text = "${if (vector.surplusPct >= 0) "+" else ""}${vector.surplusPct}% SURPLUS",
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = if (vector.surplusPct >= 0) TitanEmerald else TitanCrimson
                  )
                }
              }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Two-tone progress bar comparing candidate vs benchmark
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(SlateBorder)
            ) {
              // Benchmark marker
              Box(
                modifier = Modifier
                  .fillMaxWidth(vector.targetBenchmark / 100f)
                  .height(6.dp)
                  .background(TitanGold.copy(alpha = 0.45f))
              )
              // Candidate score
              Box(
                modifier = Modifier
                  .fillMaxWidth(vector.candidateScore / 100f)
                  .height(6.dp)
                  .background(TitanEmerald)
              )
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = vector.strategicRationale,
              fontSize = 10.5.sp,
              color = TextSecondaryDark,
              lineHeight = 14.sp
            )
          }
        }
      }
    }
  }
}

// ============================================================================
// COMPONENT 4: EXECUTIVE ACTION DIRECTIVES SECTION
// ============================================================================
@Composable
private fun ExecutiveDirectivesSection(activeRole: StrategyTargetRole) {
  val directives = listOf(
    Pair("DIRECTIVE 1: MONSOON SURGE ALGORITHM", "Deploy automated precipitation batching surcharge to elevate net CM2 from +₹28.40 past ₹32.00 per drop."),
    Pair("DIRECTIVE 2: 2-MINUTE CXO ELEVATOR HOOK", "Deliver structured pitch: 'Engineered 14 Bangalore dark store hubs, dropping dispatch cycle by 42% with 0 historical academic backlogs.'"),
    Pair("DIRECTIVE 3: TIER-1 VP NETWORKING", "Target 3 alumni VP Operations across Zepto & Swiggy Instamart for direct operational referrals.")
  )

  Column(modifier = Modifier.fillMaxWidth()) {
    Text(
      text = "HIGH-LEVERAGE CLOSING ACTIONS",
      fontSize = 11.sp,
      fontWeight = FontWeight.Bold,
      fontFamily = FontFamily.Monospace,
      color = TitanCyan
    )
    Text(
      text = "Specific levers to convert 94% alignment into formal Tier-1 offers",
      fontSize = 11.sp,
      color = TextSecondaryDark
    )

    Spacer(modifier = Modifier.height(10.dp))

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
      directives.forEach { (title, description) ->
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(10.dp),
          colors = CardDefaults.cardColors(containerColor = SlateElevated),
          border = androidx.compose.foundation.BorderStroke(1.dp, TitanCyan.copy(alpha = 0.4f))
        ) {
          Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.Top
          ) {
            Box(
              modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(TitanCyan.copy(alpha = 0.15f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Bolt,
                contentDescription = null,
                tint = TitanCyan,
                modifier = Modifier.size(16.dp)
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = TitanCyan
              )
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = description,
                fontSize = 11.sp,
                color = TextPrimaryDark,
                lineHeight = 15.sp
              )
            }
          }
        }
      }
    }
  }
}

// ============================================================================
// HELPER COMPONENT: LEGEND PILL
// ============================================================================
@Composable
private fun LegendPill(label: String, color: Color) {
  Row(verticalAlignment = Alignment.CenterVertically) {
    Box(
      modifier = Modifier
        .size(7.dp)
        .clip(CircleShape)
        .background(color)
    )
    Spacer(modifier = Modifier.width(4.dp))
    Text(
      text = label,
      fontSize = 9.sp,
      fontFamily = FontFamily.Monospace,
      color = TextMutedDark
    )
  }
}
