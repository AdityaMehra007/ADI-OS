package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ObsidianDark
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateCard
import com.example.ui.theme.SlateDarker
import com.example.ui.theme.SlateElevated
import com.example.ui.theme.TextMutedDark
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import com.example.ui.theme.TitanCyan
import com.example.ui.theme.TitanEmerald
import com.example.ui.theme.TitanGold
import com.example.ui.theme.TitanIndigo
import com.example.ui.theme.TitanViolet

enum class ProgressionTimeRange(val label: String, val title: String) {
  ONE_MONTH("1M", "Past 30 Days"),
  THREE_MONTHS("3M", "Past Quarter"),
  SIX_MONTHS("6M", "Past 6 Months"),
  ONE_YEAR("1Y", "Past 12 Months"),
  ALL_TIME("ALL", "All-Time Velocity")
}

enum class ProgressionMetricMode(val label: String) {
  COMBINED("Dual Progression"),
  SKILLS("Skill Acquisition"),
  NETWORKING("Network Growth")
}

data class ProgressionDataPoint(
  val periodLabel: String,
  val fullDate: String,
  val cumulativeSkills: Int,
  val newSkillsAdded: Int,
  val skillMasteryScore: Int,
  val totalNetwork: Int,
  val executiveLeads: Int,
  val inboundOpportunities: Int,
  val keySkillAcquisitions: List<String>,
  val milestoneEvent: String? = null,
  val correlationInsight: String? = null
)

/**
 * Recharts-inspired Career Progression Dashboard Component
 * Visualizes Skill Acquisition Rates and Strategic Networking Growth over time
 * with smooth cubic Bezier area charts, Cartesian grids, interactive scrub tooltips,
 * metric breakdown tiles, and velocity correlations.
 */
@Composable
fun CareerProgressionRechartsDashboard(
  modifier: Modifier = Modifier,
  initialTimeRange: ProgressionTimeRange = ProgressionTimeRange.SIX_MONTHS,
  onNavigateToSkills: (() -> Unit)? = null,
  onNavigateToNetwork: (() -> Unit)? = null
) {
  var selectedRange by remember { mutableStateOf(initialTimeRange) }
  var selectedMetricMode by remember { mutableStateOf(ProgressionMetricMode.COMBINED) }
  var selectedIndex by remember { mutableStateOf<Int?>(null) }
  var isInteracting by remember { mutableStateOf(false) }

  // Animated entrance transition
  val animatedProgress = remember { Animatable(0f) }
  LaunchedEffect(selectedRange, selectedMetricMode) {
    animatedProgress.snapTo(0f)
    animatedProgress.animateTo(
      targetValue = 1f,
      animationSpec = tween(durationMillis = 650, easing = FastOutSlowInEasing)
    )
  }

  val dataPoints = remember(selectedRange) {
    when (selectedRange) {
      ProgressionTimeRange.ONE_MONTH -> listOf(
        ProgressionDataPoint(
          periodLabel = "W1",
          fullDate = "Week 1, Aug 2026",
          cumulativeSkills = 24,
          newSkillsAdded = 2,
          skillMasteryScore = 86,
          totalNetwork = 340,
          executiveLeads = 58,
          inboundOpportunities = 14,
          keySkillAcquisitions = listOf("Prompt Routing", "SLA Monitoring"),
          milestoneEvent = "Deployed Gemini 3.5 Fallback Service",
          correlationInsight = "Added prompt engineering benchmarks; +18% founder replies."
        ),
        ProgressionDataPoint(
          periodLabel = "W2",
          fullDate = "Week 2, Aug 2026",
          cumulativeSkills = 25,
          newSkillsAdded = 1,
          skillMasteryScore = 88,
          totalNetwork = 365,
          executiveLeads = 68,
          inboundOpportunities = 19,
          keySkillAcquisitions = listOf("Python Async Coroutines"),
          milestoneEvent = "Automated Real-time Job Scraping Radar",
          correlationInsight = "Automated radar active; captured 8 new Tier-1 leads."
        ),
        ProgressionDataPoint(
          periodLabel = "W3",
          fullDate = "Week 3, Aug 2026",
          cumulativeSkills = 27,
          newSkillsAdded = 2,
          skillMasteryScore = 91,
          totalNetwork = 398,
          executiveLeads = 79,
          inboundOpportunities = 26,
          keySkillAcquisitions = listOf("Multi-Agent Orchestration", "SQL CTEs"),
          milestoneEvent = "Verified 42% Cycle-Time Reduction Proof",
          correlationInsight = "Cycle reduction case study live; Zepto VP referral secured."
        ),
        ProgressionDataPoint(
          periodLabel = "W4",
          fullDate = "Week 4, Aug 2026",
          cumulativeSkills = 29,
          newSkillsAdded = 2,
          skillMasteryScore = 94,
          totalNetwork = 432,
          executiveLeads = 92,
          inboundOpportunities = 34,
          keySkillAcquisitions = listOf("Executive Telemetry", "Cross-functional SLA P&L"),
          milestoneEvent = "Command Center Intelligence Live",
          correlationInsight = "Peak outreach momentum: 34 active inbound conversations."
        )
      )

      ProgressionTimeRange.THREE_MONTHS -> listOf(
        ProgressionDataPoint(
          periodLabel = "Jun",
          fullDate = "June 2026",
          cumulativeSkills = 19,
          newSkillsAdded = 3,
          skillMasteryScore = 78,
          totalNetwork = 240,
          executiveLeads = 32,
          inboundOpportunities = 8,
          keySkillAcquisitions = listOf("BBA Strategic Analysis", "PowerBI Executive Dashboards"),
          milestoneEvent = "BBA International Business Concluded",
          correlationInsight = "Foundational business degree established high executive trust."
        ),
        ProgressionDataPoint(
          periodLabel = "Jul",
          fullDate = "July 2026",
          cumulativeSkills = 23,
          newSkillsAdded = 4,
          skillMasteryScore = 85,
          totalNetwork = 320,
          executiveLeads = 52,
          inboundOpportunities = 17,
          keySkillAcquisitions = listOf("Python ETL Pipelines", "PostgreSQL Optimization"),
          milestoneEvent = "Productionized Operations Pipeline",
          correlationInsight = "Data engineering skills doubled inbound recruiter interest."
        ),
        ProgressionDataPoint(
          periodLabel = "Aug",
          fullDate = "August 2026",
          cumulativeSkills = 29,
          newSkillsAdded = 6,
          skillMasteryScore = 94,
          totalNetwork = 432,
          executiveLeads = 92,
          inboundOpportunities = 34,
          keySkillAcquisitions = listOf("Autonomous AI Agents", "Operational SLA Reduction"),
          milestoneEvent = "42% Cycle Reduction Verified & Grounded",
          correlationInsight = "Autonomous agents + business rigor established 'Triple Threat' moat."
        )
      )

      ProgressionTimeRange.SIX_MONTHS -> listOf(
        ProgressionDataPoint(
          periodLabel = "Mar",
          fullDate = "March 2026",
          cumulativeSkills = 14,
          newSkillsAdded = 2,
          skillMasteryScore = 65,
          totalNetwork = 150,
          executiveLeads = 16,
          inboundOpportunities = 4,
          keySkillAcquisitions = listOf("Advanced Excel Modeling", "Market Sizing"),
          milestoneEvent = "Initiated Career Fast-Track Strategy"
        ),
        ProgressionDataPoint(
          periodLabel = "Apr",
          fullDate = "April 2026",
          cumulativeSkills = 16,
          newSkillsAdded = 2,
          skillMasteryScore = 71,
          totalNetwork = 190,
          executiveLeads = 24,
          inboundOpportunities = 7,
          keySkillAcquisitions = listOf("SQL Joins & Aggregations", "Workflow Automation"),
          milestoneEvent = "First End-to-End ETL Pipeline Built"
        ),
        ProgressionDataPoint(
          periodLabel = "May",
          fullDate = "May 2026",
          cumulativeSkills = 18,
          newSkillsAdded = 2,
          skillMasteryScore = 76,
          totalNetwork = 225,
          executiveLeads = 30,
          inboundOpportunities = 11,
          keySkillAcquisitions = listOf("Quick Commerce Dark Store Metrics", "Unit Economics"),
          milestoneEvent = "Zepto & Swiggy Operational Research"
        ),
        ProgressionDataPoint(
          periodLabel = "Jun",
          fullDate = "June 2026",
          cumulativeSkills = 21,
          newSkillsAdded = 3,
          skillMasteryScore = 82,
          totalNetwork = 270,
          executiveLeads = 42,
          inboundOpportunities = 16,
          keySkillAcquisitions = listOf("Python Automation", "REST APIs"),
          milestoneEvent = "BBA Graduated with Honors"
        ),
        ProgressionDataPoint(
          periodLabel = "Jul",
          fullDate = "July 2026",
          cumulativeSkills = 25,
          newSkillsAdded = 4,
          skillMasteryScore = 88,
          totalNetwork = 345,
          executiveLeads = 65,
          inboundOpportunities = 24,
          keySkillAcquisitions = listOf("Gemini API Orchestration", "SLA Telemetry"),
          milestoneEvent = "Multi-Agent System Deployed"
        ),
        ProgressionDataPoint(
          periodLabel = "Aug",
          fullDate = "August 2026",
          cumulativeSkills = 29,
          newSkillsAdded = 4,
          skillMasteryScore = 94,
          totalNetwork = 432,
          executiveLeads = 92,
          inboundOpportunities = 34,
          keySkillAcquisitions = listOf("Executive Telemetry", "Board-level P&L Alignment"),
          milestoneEvent = "Executive Command Center Live",
          correlationInsight = "Combined skill velocity (4/mo) boosted executive outreach by 280%."
        )
      )

      ProgressionTimeRange.ONE_YEAR -> listOf(
        ProgressionDataPoint(
          periodLabel = "Q3'25",
          fullDate = "Q3 2025",
          cumulativeSkills = 8,
          newSkillsAdded = 3,
          skillMasteryScore = 52,
          totalNetwork = 95,
          executiveLeads = 8,
          inboundOpportunities = 2,
          keySkillAcquisitions = listOf("Business Statistics", "Financial Analysis")
        ),
        ProgressionDataPoint(
          periodLabel = "Q4'25",
          fullDate = "Q4 2025",
          cumulativeSkills = 12,
          newSkillsAdded = 4,
          skillMasteryScore = 62,
          totalNetwork = 135,
          executiveLeads = 15,
          inboundOpportunities = 4,
          keySkillAcquisitions = listOf("SQL Fundamentals", "Product Thinking")
        ),
        ProgressionDataPoint(
          periodLabel = "Q1'26",
          fullDate = "Q1 2026",
          cumulativeSkills = 17,
          newSkillsAdded = 5,
          skillMasteryScore = 74,
          totalNetwork = 210,
          executiveLeads = 28,
          inboundOpportunities = 9,
          keySkillAcquisitions = listOf("Python Scripting", "Process Engineering"),
          milestoneEvent = "First Operations Automation Shipped"
        ),
        ProgressionDataPoint(
          periodLabel = "Q2'26",
          fullDate = "Q2 2026",
          cumulativeSkills = 23,
          newSkillsAdded = 6,
          skillMasteryScore = 86,
          totalNetwork = 320,
          executiveLeads = 56,
          inboundOpportunities = 20,
          keySkillAcquisitions = listOf("LLM Integration", "Quick Commerce Ops"),
          milestoneEvent = "BBA Completion & Zepto Pilot"
        ),
        ProgressionDataPoint(
          periodLabel = "Q3'26",
          fullDate = "Q3 2026",
          cumulativeSkills = 29,
          newSkillsAdded = 6,
          skillMasteryScore = 94,
          totalNetwork = 432,
          executiveLeads = 92,
          inboundOpportunities = 34,
          keySkillAcquisitions = listOf("Autonomous Operations", "Executive Telemetry"),
          milestoneEvent = "Command Center & 42% SLA Reduction",
          correlationInsight = "Annual skill acquisition compounded at +260% year-over-year."
        )
      )

      ProgressionTimeRange.ALL_TIME -> listOf(
        ProgressionDataPoint(
          periodLabel = "2024",
          fullDate = "Academic Year 2024",
          cumulativeSkills = 6,
          newSkillsAdded = 6,
          skillMasteryScore = 45,
          totalNetwork = 55,
          executiveLeads = 4,
          inboundOpportunities = 1,
          keySkillAcquisitions = listOf("BBA Core Business", "Corporate Strategy")
        ),
        ProgressionDataPoint(
          periodLabel = "2025",
          fullDate = "Academic Year 2025",
          cumulativeSkills = 14,
          newSkillsAdded = 8,
          skillMasteryScore = 66,
          totalNetwork = 160,
          executiveLeads = 18,
          inboundOpportunities = 5,
          keySkillAcquisitions = listOf("SQL Data Analytics", "Operations Research")
        ),
        ProgressionDataPoint(
          periodLabel = "H1'26",
          fullDate = "First Half 2026",
          cumulativeSkills = 22,
          newSkillsAdded = 8,
          skillMasteryScore = 84,
          totalNetwork = 290,
          executiveLeads = 48,
          inboundOpportunities = 18,
          keySkillAcquisitions = listOf("Python Engineering", "Quick Commerce Workflows")
        ),
        ProgressionDataPoint(
          periodLabel = "NOW",
          fullDate = "August 2026 (Present)",
          cumulativeSkills = 29,
          newSkillsAdded = 7,
          skillMasteryScore = 94,
          totalNetwork = 432,
          executiveLeads = 92,
          inboundOpportunities = 34,
          keySkillAcquisitions = listOf("Autonomous Multi-Agents", "42% Cycle Reduction Moat"),
          milestoneEvent = "Titan Executive Candidate Status",
          correlationInsight = "Full transition from academic student to high-leverage operator."
        )
      )
    }
  }

  // Active highlighted point: either hovered or latest
  val activePoint = selectedIndex?.let { dataPoints.getOrNull(it) } ?: dataPoints.last()
  val previousPoint = if (dataPoints.size > 1) dataPoints[dataPoints.size - 2] else activePoint
  val skillsDelta = activePoint.cumulativeSkills - previousPoint.cumulativeSkills
  val networkDelta = activePoint.totalNetwork - previousPoint.totalNetwork
  val leadsDelta = activePoint.executiveLeads - previousPoint.executiveLeads

  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("career_progression_recharts_dashboard"),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = SlateCard),
    border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
    ) {
      // 1. Recharts Engine Header & Visual Badges
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.weight(1f)
        ) {
          Box(
            modifier = Modifier
              .size(34.dp)
              .clip(RoundedCornerShape(8.dp))
              .background(TitanEmerald.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ShowChart,
              contentDescription = null,
              tint = TitanEmerald,
              modifier = Modifier.size(18.dp)
            )
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "CAREER PROGRESSION ANALYTICS",
                style = MaterialTheme.typography.labelSmall,
                color = TitanEmerald,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp
              )
              Spacer(modifier = Modifier.width(6.dp))
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(4.dp))
                  .background(TitanEmerald.copy(alpha = 0.18f))
                  .padding(horizontal = 5.dp, vertical = 1.dp)
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Box(
                    modifier = Modifier
                      .size(5.dp)
                      .clip(CircleShape)
                      .background(TitanEmerald)
                  )
                  Spacer(modifier = Modifier.width(3.dp))
                  Text(
                    text = "RECHARTS ENGINE",
                    color = TitanEmerald,
                    fontSize = 8.5.sp,
                    fontWeight = FontWeight.Bold
                  )
                }
              }
            }
            Text(
              text = "Skill Acquisition Rates & Networking Trajectory",
              style = MaterialTheme.typography.bodySmall,
              color = TextSecondaryDark,
              fontSize = 11.sp
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // 2. High-Density KPI Summary Tiles
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        ProgressionKpiTile(
          label = "SKILLS MASTERED",
          value = "${activePoint.cumulativeSkills}",
          badgeText = "+$skillsDelta this cycle",
          badgeColor = TitanEmerald,
          subtext = "Mastery: ${activePoint.skillMasteryScore}%",
          accentColor = TitanEmerald,
          icon = Icons.Default.Code,
          modifier = Modifier.weight(1f)
        )
        ProgressionKpiTile(
          label = "STRATEGIC NETWORK",
          value = "${activePoint.totalNetwork}",
          badgeText = "+$networkDelta added",
          badgeColor = TitanCyan,
          subtext = "43% growth velocity",
          accentColor = TitanCyan,
          icon = Icons.Default.Hub,
          modifier = Modifier.weight(1f)
        )
        ProgressionKpiTile(
          label = "EXEC DECISION LEADS",
          value = "${activePoint.executiveLeads}",
          badgeText = "+$leadsDelta senior",
          badgeColor = TitanGold,
          subtext = "${activePoint.inboundOpportunities} inbounds",
          accentColor = TitanGold,
          icon = Icons.Default.People,
          modifier = Modifier.weight(1f)
        )
      }

      Spacer(modifier = Modifier.height(12.dp))

      // 3. Time Range & Metric Mode Controls
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Time Range Chips
        Row(
          horizontalArrangement = Arrangement.spacedBy(4.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          ProgressionTimeRange.values().forEach { range ->
            val isSelected = selectedRange == range
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(if (isSelected) TitanEmerald else SlateElevated)
                .clickable {
                  selectedRange = range
                  selectedIndex = null
                }
                .padding(horizontal = 8.dp, vertical = 4.dp)
                .testTag("progression_filter_${range.label.lowercase()}")
            ) {
              Text(
                text = range.label,
                style = MaterialTheme.typography.labelSmall,
                color = if (isSelected) ObsidianDark else TextSecondaryDark,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                fontSize = 10.sp
              )
            }
          }
        }

        // View Mode Switcher
        Row(
          horizontalArrangement = Arrangement.spacedBy(4.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          ProgressionMetricMode.values().forEach { mode ->
            val isSelected = selectedMetricMode == mode
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(if (isSelected) SlateElevated else Color.Transparent)
                .border(
                  width = 0.8.dp,
                  color = if (isSelected) TitanCyan else SlateBorder.copy(alpha = 0.5f),
                  shape = RoundedCornerShape(6.dp)
                )
                .clickable {
                  selectedMetricMode = mode
                }
                .padding(horizontal = 6.dp, vertical = 4.dp)
                .testTag("metric_mode_${mode.name.lowercase()}")
            ) {
              Text(
                text = mode.label,
                style = MaterialTheme.typography.labelSmall,
                color = if (isSelected) TitanCyan else TextMutedDark,
                fontSize = 9.5.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // 4. Interactive Recharts-Style Chart Canvas
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(210.dp)
          .clip(RoundedCornerShape(10.dp))
          .background(SlateDarker.copy(alpha = 0.85f))
          .border(0.8.dp, SlateBorder.copy(alpha = 0.6f), RoundedCornerShape(10.dp))
          .padding(top = 16.dp, bottom = 8.dp, start = 12.dp, end = 12.dp)
          .pointerInput(dataPoints) {
            detectTapGestures { offset ->
              val count = dataPoints.size
              if (count > 1) {
                val stepX = size.width / (count - 1)
                val clampedIdx = ((offset.x + stepX / 2f) / stepX)
                  .toInt()
                  .coerceIn(0, count - 1)
                selectedIndex = clampedIdx
                isInteracting = true
              }
            }
          }
          .pointerInput(dataPoints) {
            detectDragGestures(
              onDragStart = { offset ->
                val count = dataPoints.size
                if (count > 1) {
                  val stepX = size.width / (count - 1)
                  val clampedIdx = ((offset.x + stepX / 2f) / stepX)
                    .toInt()
                    .coerceIn(0, count - 1)
                  selectedIndex = clampedIdx
                  isInteracting = true
                }
              },
              onDragEnd = {
                isInteracting = false
              },
              onDragCancel = {
                isInteracting = false
              },
              onDrag = { change, _ ->
                val count = dataPoints.size
                if (count > 1) {
                  val stepX = size.width / (count - 1)
                  val clampedIdx = ((change.position.x + stepX / 2f) / stepX)
                    .toInt()
                    .coerceIn(0, count - 1)
                  selectedIndex = clampedIdx
                  isInteracting = true
                }
              }
            )
          }
      ) {
        // Find scaling bounds
        val minSkills = (dataPoints.minOfOrNull { it.cumulativeSkills } ?: 0).toFloat() * 0.8f
        val maxSkills = (dataPoints.maxOfOrNull { it.cumulativeSkills } ?: 30).toFloat() * 1.05f

        val minNetwork = (dataPoints.minOfOrNull { it.totalNetwork } ?: 0).toFloat() * 0.8f
        val maxNetwork = (dataPoints.maxOfOrNull { it.totalNetwork } ?: 500).toFloat() * 1.05f

        Canvas(modifier = Modifier.fillMaxWidth().height(160.dp)) {
          val canvasWidth = size.width
          val canvasHeight = size.height
          val pointCount = dataPoints.size
          if (pointCount < 2) return@Canvas

          val stepX = canvasWidth / (pointCount - 1)

          // 4a. Cartesian Grid Lines (Recharts CartesianGrid style)
          val gridLines = 4
          for (i in 0..gridLines) {
            val y = canvasHeight * (i.toFloat() / gridLines)
            drawLine(
              color = SlateBorder.copy(alpha = 0.35f),
              start = Offset(0f, y),
              end = Offset(canvasWidth, y),
              strokeWidth = 1.dp.toPx(),
              pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f)
            )
          }

          // 4b. Monthly Skill Acquisition Bars (Visible in SKILLS mode or COMBINED)
          if (selectedMetricMode == ProgressionMetricMode.SKILLS || selectedMetricMode == ProgressionMetricMode.COMBINED) {
            val maxMonthly = (dataPoints.maxOfOrNull { it.newSkillsAdded } ?: 6).toFloat()
            val barWidth = 14.dp.toPx()
            dataPoints.forEachIndexed { idx, pt ->
              val centerX = idx * stepX
              val barHeight = ((pt.newSkillsAdded.toFloat() / maxMonthly) * (canvasHeight * 0.45f) * animatedProgress.value)
              val barTop = canvasHeight - barHeight
              drawRoundRect(
                brush = Brush.verticalGradient(
                  colors = listOf(
                    TitanEmerald.copy(alpha = 0.45f),
                    TitanEmerald.copy(alpha = 0.10f)
                  ),
                  startY = barTop,
                  endY = canvasHeight
                ),
                topLeft = Offset(centerX - barWidth / 2f, barTop),
                size = Size(barWidth, barHeight),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx(), 4.dp.toPx())
              )
            }
          }

          // Compute Network Growth Coordinates
          val networkPoints = dataPoints.mapIndexed { idx, pt ->
            val x = idx * stepX
            val normalizedY = ((pt.totalNetwork - minNetwork) / (maxNetwork - minNetwork)).coerceIn(0f, 1f)
            val y = canvasHeight - (normalizedY * canvasHeight * animatedProgress.value)
            Offset(x, y)
          }

          // Compute Skill Cumulative Coordinates
          val skillPoints = dataPoints.mapIndexed { idx, pt ->
            val x = idx * stepX
            val normalizedY = ((pt.cumulativeSkills - minSkills) / (maxSkills - minSkills)).coerceIn(0f, 1f)
            val y = canvasHeight - (normalizedY * canvasHeight * animatedProgress.value)
            Offset(x, y)
          }

          // 4c. Networking Growth Area & Bezier Curve
          if (selectedMetricMode == ProgressionMetricMode.NETWORKING || selectedMetricMode == ProgressionMetricMode.COMBINED) {
            val networkAreaPath = Path().apply {
              moveTo(networkPoints.first().x, canvasHeight)
              lineTo(networkPoints.first().x, networkPoints.first().y)
              for (i in 0 until networkPoints.size - 1) {
                val p0 = networkPoints[i]
                val p1 = networkPoints[i + 1]
                val midX = (p0.x + p1.x) / 2f
                cubicTo(midX, p0.y, midX, p1.y, p1.x, p1.y)
              }
              lineTo(networkPoints.last().x, canvasHeight)
              close()
            }

            drawPath(
              path = networkAreaPath,
              brush = Brush.verticalGradient(
                colors = listOf(
                  TitanCyan.copy(alpha = 0.30f),
                  TitanIndigo.copy(alpha = 0.12f),
                  Color.Transparent
                ),
                startY = 0f,
                endY = canvasHeight
              )
            )

            val networkLinePath = Path().apply {
              moveTo(networkPoints.first().x, networkPoints.first().y)
              for (i in 0 until networkPoints.size - 1) {
                val p0 = networkPoints[i]
                val p1 = networkPoints[i + 1]
                val midX = (p0.x + p1.x) / 2f
                cubicTo(midX, p0.y, midX, p1.y, p1.x, p1.y)
              }
            }

            drawPath(
              path = networkLinePath,
              color = TitanCyan,
              style = Stroke(
                width = 2.5.dp.toPx(),
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
              )
            )
          }

          // 4d. Skill Acquisition Area & Bezier Curve
          if (selectedMetricMode == ProgressionMetricMode.SKILLS || selectedMetricMode == ProgressionMetricMode.COMBINED) {
            val skillAreaPath = Path().apply {
              moveTo(skillPoints.first().x, canvasHeight)
              lineTo(skillPoints.first().x, skillPoints.first().y)
              for (i in 0 until skillPoints.size - 1) {
                val p0 = skillPoints[i]
                val p1 = skillPoints[i + 1]
                val midX = (p0.x + p1.x) / 2f
                cubicTo(midX, p0.y, midX, p1.y, p1.x, p1.y)
              }
              lineTo(skillPoints.last().x, canvasHeight)
              close()
            }

            drawPath(
              path = skillAreaPath,
              brush = Brush.verticalGradient(
                colors = listOf(
                  TitanEmerald.copy(alpha = 0.28f),
                  TitanEmerald.copy(alpha = 0.08f),
                  Color.Transparent
                ),
                startY = 0f,
                endY = canvasHeight
              )
            )

            val skillLinePath = Path().apply {
              moveTo(skillPoints.first().x, skillPoints.first().y)
              for (i in 0 until skillPoints.size - 1) {
                val p0 = skillPoints[i]
                val p1 = skillPoints[i + 1]
                val midX = (p0.x + p1.x) / 2f
                cubicTo(midX, p0.y, midX, p1.y, p1.x, p1.y)
              }
            }

            drawPath(
              path = skillLinePath,
              color = TitanEmerald,
              style = Stroke(
                width = 2.8.dp.toPx(),
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
              )
            )
          }

          // 4e. Recharts Interactive Scrubber & Cursor Indicator
          val activeIdx = selectedIndex ?: (pointCount - 1)
          if (activeIdx in 0 until pointCount) {
            val cursorX = activeIdx * stepX

            // Vertical cursor line
            drawLine(
              color = TitanGold.copy(alpha = 0.65f),
              start = Offset(cursorX, 0f),
              end = Offset(cursorX, canvasHeight),
              strokeWidth = 1.2.dp.toPx(),
              pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
            )

            // Skill data dot
            if (selectedMetricMode == ProgressionMetricMode.SKILLS || selectedMetricMode == ProgressionMetricMode.COMBINED) {
              val sPoint = skillPoints[activeIdx]
              drawCircle(
                color = TitanEmerald.copy(alpha = 0.25f),
                radius = 8.dp.toPx(),
                center = sPoint
              )
              drawCircle(
                color = TitanEmerald,
                radius = 4.5.dp.toPx(),
                center = sPoint
              )
              drawCircle(
                color = ObsidianDark,
                radius = 2.dp.toPx(),
                center = sPoint
              )
            }

            // Network data dot
            if (selectedMetricMode == ProgressionMetricMode.NETWORKING || selectedMetricMode == ProgressionMetricMode.COMBINED) {
              val nPoint = networkPoints[activeIdx]
              drawCircle(
                color = TitanCyan.copy(alpha = 0.25f),
                radius = 8.dp.toPx(),
                center = nPoint
              )
              drawCircle(
                color = TitanCyan,
                radius = 4.5.dp.toPx(),
                center = nPoint
              )
              drawCircle(
                color = ObsidianDark,
                radius = 2.dp.toPx(),
                center = nPoint
              )
            }
          }
        }

        // X-Axis Labels Row below canvas
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.BottomCenter)
            .padding(horizontal = 4.dp),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          dataPoints.forEachIndexed { idx, point ->
            val isCurrent = (selectedIndex ?: (dataPoints.size - 1)) == idx
            Text(
              text = point.periodLabel,
              color = if (isCurrent) TitanGold else TextMutedDark,
              fontSize = 10.sp,
              fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
              textAlign = TextAlign.Center
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // 5. Recharts Interactive Tooltip Overlay Card (Dynamic Hover/Touch scrub detail)
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("recharts_scrub_tooltip_card"),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = SlateElevated),
        border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(8.dp)
                  .clip(CircleShape)
                  .background(TitanGold)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = activePoint.fullDate,
                style = MaterialTheme.typography.labelMedium,
                color = TextPrimaryDark,
                fontWeight = FontWeight.Bold
              )
            }

            Text(
              text = "Tap or drag chart to scrub",
              style = MaterialTheme.typography.labelSmall,
              color = TextMutedDark,
              fontSize = 9.5.sp
            )
          }

          Spacer(modifier = Modifier.height(8.dp))

          // Metric comparison columns in tooltip
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            // Skills Metric Tooltip
            Box(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(8.dp))
                .background(TitanEmerald.copy(alpha = 0.08f))
                .border(0.6.dp, TitanEmerald.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                .padding(8.dp)
            ) {
              Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Box(
                    modifier = Modifier
                      .size(6.dp)
                      .clip(CircleShape)
                      .background(TitanEmerald)
                  )
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(
                    text = "SKILLS VELOCITY",
                    style = MaterialTheme.typography.labelSmall,
                    color = TitanEmerald,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                  )
                }
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                  text = "${activePoint.cumulativeSkills} Total Mastered",
                  style = MaterialTheme.typography.bodySmall,
                  color = TextPrimaryDark,
                  fontWeight = FontWeight.Bold,
                  fontSize = 12.sp
                )
                Text(
                  text = "+${activePoint.newSkillsAdded} new skills in period",
                  style = MaterialTheme.typography.labelSmall,
                  color = TextSecondaryDark,
                  fontSize = 10.sp
                )
              }
            }

            // Network Metric Tooltip
            Box(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(8.dp))
                .background(TitanCyan.copy(alpha = 0.08f))
                .border(0.6.dp, TitanCyan.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                .padding(8.dp)
            ) {
              Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Box(
                    modifier = Modifier
                      .size(6.dp)
                      .clip(CircleShape)
                      .background(TitanCyan)
                  )
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(
                    text = "NETWORK SCALE",
                    style = MaterialTheme.typography.labelSmall,
                    color = TitanCyan,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                  )
                }
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                  text = "${activePoint.totalNetwork} Connections",
                  style = MaterialTheme.typography.bodySmall,
                  color = TextPrimaryDark,
                  fontWeight = FontWeight.Bold,
                  fontSize = 12.sp
                )
                Text(
                  text = "${activePoint.executiveLeads} Executive / C-Suite",
                  style = MaterialTheme.typography.labelSmall,
                  color = TextSecondaryDark,
                  fontSize = 10.sp
                )
              }
            }
          }

          // Catalyst Milestone Event
          if (!activePoint.milestoneEvent.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(6.dp))
                .background(SlateDarker)
                .padding(horizontal = 8.dp, vertical = 5.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Bolt,
                contentDescription = null,
                tint = TitanGold,
                modifier = Modifier.size(13.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "Milestone: ${activePoint.milestoneEvent}",
                style = MaterialTheme.typography.labelSmall,
                color = TextPrimaryDark,
                fontWeight = FontWeight.SemiBold,
                fontSize = 10.5.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
            }
          }

          // Key skills added pill row
          if (activePoint.keySkillAcquisitions.isNotEmpty()) {
            Spacer(modifier = Modifier.height(6.dp))
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              Text(
                text = "Acquired:",
                style = MaterialTheme.typography.labelSmall,
                color = TextMutedDark,
                fontSize = 9.5.sp
              )
              activePoint.keySkillAcquisitions.forEach { skill ->
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(TitanEmerald.copy(alpha = 0.15f))
                    .padding(horizontal = 5.dp, vertical = 2.dp)
                ) {
                  Text(
                    text = skill,
                    color = TitanEmerald,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Medium
                  )
                }
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // 6. Series Legend & Interactive Toggles
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          horizontalArrangement = Arrangement.spacedBy(14.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          LegendIndicator(
            label = "Skill Acquisition",
            color = TitanEmerald,
            isActive = selectedMetricMode == ProgressionMetricMode.SKILLS || selectedMetricMode == ProgressionMetricMode.COMBINED,
            onClick = {
              selectedMetricMode = if (selectedMetricMode == ProgressionMetricMode.SKILLS) {
                ProgressionMetricMode.COMBINED
              } else {
                ProgressionMetricMode.SKILLS
              }
            }
          )
          LegendIndicator(
            label = "Network Growth",
            color = TitanCyan,
            isActive = selectedMetricMode == ProgressionMetricMode.NETWORKING || selectedMetricMode == ProgressionMetricMode.COMBINED,
            onClick = {
              selectedMetricMode = if (selectedMetricMode == ProgressionMetricMode.NETWORKING) {
                ProgressionMetricMode.COMBINED
              } else {
                ProgressionMetricMode.NETWORKING
              }
            }
          )
        }

        // Action links
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          if (onNavigateToSkills != null) {
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(SlateElevated)
                .clickable { onNavigateToSkills() }
                .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = "Skills",
                  color = TitanEmerald,
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(3.dp))
                Icon(
                  imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                  contentDescription = null,
                  tint = TitanEmerald,
                  modifier = Modifier.size(11.dp)
                )
              }
            }
          }

          if (onNavigateToNetwork != null) {
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(SlateElevated)
                .clickable { onNavigateToNetwork() }
                .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = "Network",
                  color = TitanCyan,
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(3.dp))
                Icon(
                  imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                  contentDescription = null,
                  tint = TitanCyan,
                  modifier = Modifier.size(11.dp)
                )
              }
            }
          }
        }
      }

      // 7. Correlation Analysis Banner
      Spacer(modifier = Modifier.height(10.dp))
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(8.dp))
          .background(TitanViolet.copy(alpha = 0.08f))
          .border(0.8.dp, TitanViolet.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
          .padding(10.dp)
      ) {
        Row(
          verticalAlignment = Alignment.Top,
          modifier = Modifier.fillMaxWidth()
        ) {
          Icon(
            imageVector = Icons.Default.AutoAwesome,
            contentDescription = null,
            tint = TitanViolet,
            modifier = Modifier
              .size(16.dp)
              .padding(top = 1.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Column {
            Text(
              text = "PROGRESSION CORRELATION INSIGHT",
              style = MaterialTheme.typography.labelSmall,
              color = TitanViolet,
              fontWeight = FontWeight.Bold,
              fontSize = 9.5.sp,
              letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = activePoint.correlationInsight
                ?: "Accelerating skill acquisition in Autonomous Multi-Agents and SLA telemetry drove a 3.4x spike in Tier-1 C-suite inbound conversions over the past quarter.",
              style = MaterialTheme.typography.bodySmall,
              color = TextSecondaryDark,
              fontSize = 11.sp,
              lineHeight = 15.sp
            )
          }
        }
      }
    }
  }
}

@Composable
private fun ProgressionKpiTile(
  label: String,
  value: String,
  badgeText: String,
  badgeColor: Color,
  subtext: String,
  accentColor: Color,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(10.dp))
      .background(SlateElevated)
      .border(0.8.dp, SlateBorder, RoundedCornerShape(10.dp))
      .padding(10.dp)
  ) {
    Column {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = label,
          style = MaterialTheme.typography.labelSmall,
          color = TextMutedDark,
          fontSize = 8.5.sp,
          fontWeight = FontWeight.SemiBold,
          letterSpacing = 0.4.sp
        )
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = accentColor.copy(alpha = 0.7f),
          modifier = Modifier.size(12.dp)
        )
      }

      Spacer(modifier = Modifier.height(4.dp))

      Text(
        text = value,
        style = MaterialTheme.typography.titleMedium,
        color = TextPrimaryDark,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp
      )

      Spacer(modifier = Modifier.height(3.dp))

      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(3.dp))
          .background(badgeColor.copy(alpha = 0.15f))
          .padding(horizontal = 4.dp, vertical = 1.dp)
      ) {
        Text(
          text = badgeText,
          color = badgeColor,
          fontSize = 8.5.sp,
          fontWeight = FontWeight.Bold
        )
      }

      Spacer(modifier = Modifier.height(2.dp))

      Text(
        text = subtext,
        style = MaterialTheme.typography.labelSmall,
        color = TextMutedDark,
        fontSize = 9.sp,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )
    }
  }
}

@Composable
private fun LegendIndicator(
  label: String,
  color: Color,
  isActive: Boolean,
  onClick: () -> Unit
) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier
      .clip(RoundedCornerShape(4.dp))
      .clickable { onClick() }
      .padding(horizontal = 4.dp, vertical = 2.dp)
  ) {
    Box(
      modifier = Modifier
        .size(8.dp)
        .clip(CircleShape)
        .background(if (isActive) color else SlateBorder)
    )
    Spacer(modifier = Modifier.width(5.dp))
    Text(
      text = label,
      style = MaterialTheme.typography.labelSmall,
      color = if (isActive) TextPrimaryDark else TextMutedDark,
      fontSize = 10.sp,
      fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal
    )
  }
}
