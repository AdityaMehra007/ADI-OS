package com.example.ui.components

import android.content.Context
import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CareerMilestone
import com.example.data.model.CareerMilestoneStatus
import com.example.data.model.CareerStrategyRoadmap
import com.example.data.model.MilestoneWithTasks
import com.example.data.model.UserProfile
import com.example.ui.theme.ObsidianDark
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateCard
import com.example.ui.theme.SlateDarker
import com.example.ui.theme.SlateElevated
import com.example.ui.theme.TextMutedDark
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import com.example.ui.theme.TitanCyan
import com.example.ui.theme.TitanCyanGlow
import com.example.ui.theme.TitanEmerald
import com.example.ui.theme.TitanGold
import com.example.ui.theme.TitanIndigo
import com.example.ui.theme.TitanViolet
import com.example.ui.viewmodel.TitanViewModel
import kotlin.math.roundToInt

/**
 * Visual Chart Modes available in the Recharts Career Momentum Engine
 */
enum class MomentumChartMode(val title: String, val subtitle: String, val icon: ImageVector) {
  VELOCITY_AREA_CURVE("Skill Growth Area", "Recharts cubic Bezier spline with gradient area fill", Icons.AutoMirrored.Filled.ShowChart),
  QUARTERLY_BARS("Quarterly Velocity", "Bar chart of acquired competencies vs. benchmark target pace", Icons.Default.BarChart),
  CUMULATIVE_BURNUP("Cumulative Burn-Up", "Pacing trajectory towards target senior role ceiling", Icons.Default.Timeline),
  DOMAIN_MASTERY("Domain Mastery", "Competency progression distributed across career pillars", Icons.Default.Layers)
}

/**
 * Normalized timeline node synthesizing stored Room DB milestones, Roadmap phases, and skills
 */
data class MomentumTimelineNode(
  val id: String,
  val horizon: String,          // "Current", "6M", "1Y", "2Y", "3Y", "5Y"
  val quarter: String,          // "Q3 '26", "Q1 '27", etc.
  val milestoneTitle: String,
  val targetMetric: String,
  val skillsAcquired: List<String>,
  val skillsInProgress: List<String>,
  val cumulativeSkills: Int,
  val growthPercent: Int,        // 0..100
  val benchmarkVelocity: Int,    // 0..100
  val status: String,            // "ACHIEVED", "IN_PROGRESS", "PLANNED"
  val category: String,          // "SYSTEMS_ARCHITECTURE", "TECHNICAL_MASTERY", etc.
  val rationale: String,
  val tasksCompleted: Int,
  val totalTasks: Int
)

/**
 * Summary telemetry metrics computed from milestones and skills
 */
data class MomentumMetrics(
  val velocityRatio: Float,       // e.g. 1.35x
  val completedMilestones: Int,
  val totalMilestones: Int,
  val totalSkillsAcquired: Int,
  val totalSkillsPlanned: Int,
  val overallGrowthPercent: Int,
  val readinessScore: Int,
  val projectedTargetQuarter: String,
  val momentumTier: String,       // "SUPERCHARGED", "HIGH MOMENTUM", "ON TRACK"
  val momentumSummary: String
)

/**
 * Primary Career Momentum Dashboard Composable
 * Visualizes growth trends based on stored professional milestones and skills timeline using Recharts.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CareerMomentumDashboard(
  viewModel: TitanViewModel,
  modifier: Modifier = Modifier,
  onAddMilestoneClick: () -> Unit = {}
) {
  val context = LocalContext.current
  val milestonesWithTasks by viewModel.milestonesWithTasks.collectAsState()
  val roadmap by viewModel.careerRoadmap.collectAsState()
  val userProfile by viewModel.userProfile.collectAsState()
  val currentRole by viewModel.currentRoleInput.collectAsState()
  val targetGoals by viewModel.targetCareerGoalsInput.collectAsState()

  var selectedMode by remember { mutableStateOf(MomentumChartMode.VELOCITY_AREA_CURVE) }
  var selectedHorizonFilter by remember { mutableStateOf("ALL") }

  // Series toggles for the Recharts visualization
  var showActualTrajectory by remember { mutableStateOf(true) }
  var showBenchmarkCurve by remember { mutableStateOf(true) }
  var showCumulativeFill by remember { mutableStateOf(true) }

  // Compute timeline nodes and momentum telemetry
  val timelineNodes = remember(milestonesWithTasks, roadmap, userProfile, selectedHorizonFilter) {
    buildMomentumTimelineNodes(
      milestonesWithTasks = milestonesWithTasks,
      roadmap = roadmap,
      userProfile = userProfile,
      horizonFilter = selectedHorizonFilter
    )
  }

  val metrics = remember(milestonesWithTasks, roadmap, userProfile) {
    calculateMomentumMetrics(milestonesWithTasks, roadmap, userProfile)
  }

  var activeScrubIndex by remember { mutableIntStateOf(timelineNodes.size - 1) }

  LaunchedEffect(timelineNodes.size) {
    if (activeScrubIndex >= timelineNodes.size) {
      activeScrubIndex = (timelineNodes.size - 1).coerceAtLeast(0)
    }
  }

  // Animation progress
  var animTarget by remember { mutableStateOf(0f) }
  val animatedProgress by animateFloatAsState(
    targetValue = animTarget,
    animationSpec = tween(durationMillis = 850, easing = FastOutSlowInEasing),
    label = "recharts_momentum_anim"
  )

  LaunchedEffect(milestonesWithTasks, selectedMode, selectedHorizonFilter) {
    animTarget = 0f
    animTarget = 1f
  }

  val activeNode = timelineNodes.getOrNull(activeScrubIndex) ?: timelineNodes.lastOrNull()

  Column(
    modifier = modifier
      .fillMaxWidth()
      .testTag("career_momentum_dashboard"),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // 1. Executive Career Momentum Header Card & Share Action
    CareerMomentumHeaderCard(
      metrics = metrics,
      currentRole = currentRole,
      targetGoals = targetGoals,
      onShareReport = {
        shareCareerMomentumReport(context, metrics, timelineNodes, currentRole, targetGoals)
      }
    )

    // 2. High-Density KPI Telemetry Row (Velocity, Milestones, Skills, Readiness)
    MomentumKpiTelemetryRow(
      metrics = metrics,
      onAddMilestoneClick = onAddMilestoneClick
    )

    // 3. Recharts Growth Visualizer Container Card
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .testTag("recharts_growth_visualizer_card"),
      colors = CardDefaults.cardColors(containerColor = SlateCard),
      shape = RoundedCornerShape(16.dp),
      border = BorderStroke(1.dp, SlateBorder)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        // Chart Header & Mode Selector
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Box(
              modifier = Modifier
                .size(36.dp)
                .background(TitanCyan.copy(alpha = 0.16f), CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = selectedMode.icon,
                contentDescription = null,
                tint = TitanCyan,
                modifier = Modifier.size(18.dp)
              )
            }
            Column {
              Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                  text = "RECHARTS MOMENTUM ENGINE",
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  color = TitanCyan,
                  letterSpacing = 1.2.sp
                )
                Surface(
                  color = TitanEmerald.copy(alpha = 0.15f),
                  shape = RoundedCornerShape(4.dp)
                ) {
                  Text(
                    text = "ROOM SYNCED",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = TitanEmerald,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                  )
                }
              }
              Text(
                text = selectedMode.title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimaryDark
              )
            }
          }

          // Visualizer Mode Selector
          Row(
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .background(SlateDarker)
              .border(1.dp, SlateBorder, RoundedCornerShape(8.dp))
              .padding(2.dp)
              .testTag("momentum_mode_selector")
          ) {
            MomentumChartMode.values().forEach { mode ->
              val isSelected = selectedMode == mode
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(6.dp))
                  .background(if (isSelected) TitanCyan.copy(alpha = 0.22f) else Color.Transparent)
                  .clickable { selectedMode = mode }
                  .padding(horizontal = 7.dp, vertical = 5.dp),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = mode.icon,
                  contentDescription = mode.title,
                  tint = if (isSelected) TitanCyan else TextMutedDark,
                  modifier = Modifier.size(16.dp)
                )
              }
            }
          }
        }

        // Horizon Filter Chips
        HorizonFilterRow(
          selectedFilter = selectedHorizonFilter,
          onSelectFilter = { selectedHorizonFilter = it }
        )

        // Series Toggle Strip
        SeriesToggleStrip(
          showActual = showActualTrajectory,
          onToggleActual = { showActualTrajectory = !showActualTrajectory },
          showBenchmark = showBenchmarkCurve,
          onToggleBenchmark = { showBenchmarkCurve = !showBenchmarkCurve },
          showCumulative = showCumulativeFill,
          onToggleCumulative = { showCumulativeFill = !showCumulativeFill }
        )

        // The Hardware-Accelerated Canvas Chart
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(260.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(ObsidianDark)
            .border(1.dp, SlateBorder.copy(alpha = 0.7f), RoundedCornerShape(12.dp))
            .testTag("recharts_canvas_container")
        ) {
          when (selectedMode) {
            MomentumChartMode.VELOCITY_AREA_CURVE -> {
              RechartsAreaVelocityCanvas(
                nodes = timelineNodes,
                activeScrubIndex = activeScrubIndex,
                onScrubIndexChange = { activeScrubIndex = it },
                animProgress = animatedProgress,
                showActual = showActualTrajectory,
                showBenchmark = showBenchmarkCurve,
                showFill = showCumulativeFill
              )
            }
            MomentumChartMode.QUARTERLY_BARS -> {
              RechartsQuarterlyBarsCanvas(
                nodes = timelineNodes,
                activeScrubIndex = activeScrubIndex,
                onScrubIndexChange = { activeScrubIndex = it },
                animProgress = animatedProgress
              )
            }
            MomentumChartMode.CUMULATIVE_BURNUP -> {
              RechartsCumulativeBurnupCanvas(
                nodes = timelineNodes,
                activeScrubIndex = activeScrubIndex,
                onScrubIndexChange = { activeScrubIndex = it },
                animProgress = animatedProgress,
                showBenchmark = showBenchmarkCurve
              )
            }
            MomentumChartMode.DOMAIN_MASTERY -> {
              RechartsDomainMasteryCanvas(
                nodes = timelineNodes,
                activeScrubIndex = activeScrubIndex,
                onScrubIndexChange = { activeScrubIndex = it },
                animProgress = animatedProgress
              )
            }
          }
        }

        // Active Scrubbed Node HUD (Tooltip)
        activeNode?.let { node ->
          ScrubbedNodeHudCard(
            node = node,
            onToggleStatus = {
              viewModel.toggleSkillMilestoneStatus(node.id)
            }
          )
        }
      }
    }

    // 4. Stored Professional Milestones & Skills Timeline Stream (Room DB Connected)
    StoredMilestonesSkillsSection(
      milestonesWithTasks = milestonesWithTasks,
      nodes = timelineNodes,
      onToggleMilestoneStatus = { milestoneId ->
        viewModel.toggleSkillMilestoneStatus(milestoneId)
      },
      onAddMilestoneClick = onAddMilestoneClick
    )

    // 5. Strategic Growth Recommendations Card
    GrowthRecommendationsCard(
      metrics = metrics,
      upcomingNode = timelineNodes.firstOrNull { it.status != "ACHIEVED" },
      onAddMilestoneClick = onAddMilestoneClick
    )
  }
}

// =====================================================================================
// 1. Executive Career Momentum Header Card
// =====================================================================================
@Composable
private fun CareerMomentumHeaderCard(
  metrics: MomentumMetrics,
  currentRole: String,
  targetGoals: String,
  onShareReport: () -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("career_momentum_header_card"),
    colors = CardDefaults.cardColors(containerColor = SlateCard),
    shape = RoundedCornerShape(16.dp),
    border = BorderStroke(1.dp, TitanCyan.copy(alpha = 0.35f))
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.weight(1f)
      ) {
        Box(
          modifier = Modifier
            .size(44.dp)
            .background(
              Brush.linearGradient(listOf(TitanCyan.copy(alpha = 0.25f), TitanIndigo.copy(alpha = 0.25f))),
              RoundedCornerShape(12.dp)
            )
            .border(1.dp, TitanCyan.copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.TrendingUp,
            contentDescription = null,
            tint = TitanCyan,
            modifier = Modifier.size(24.dp)
          )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
          Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
              text = "CAREER MOMENTUM DASHBOARD",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = TitanCyan,
              letterSpacing = 1.sp
            )
            Surface(
              color = TitanGold.copy(alpha = 0.15f),
              shape = RoundedCornerShape(6.dp),
              border = BorderStroke(1.dp, TitanGold.copy(alpha = 0.35f))
            ) {
              Text(
                text = metrics.momentumTier,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = TitanGold,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
          }
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = "$currentRole → $targetGoals",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimaryDark,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
          Text(
            text = metrics.momentumSummary,
            fontSize = 11.sp,
            color = TextMutedDark,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        }
      }

      Spacer(modifier = Modifier.width(10.dp))

      IconButton(
        onClick = onShareReport,
        modifier = Modifier
          .size(38.dp)
          .clip(RoundedCornerShape(8.dp))
          .background(SlateElevated)
          .border(1.dp, SlateBorder, RoundedCornerShape(8.dp))
          .testTag("share_momentum_report_button")
      ) {
        Icon(
          imageVector = Icons.Default.Share,
          contentDescription = "Share Momentum Report",
          tint = TitanCyan,
          modifier = Modifier.size(18.dp)
        )
      }
    }
  }
}

// =====================================================================================
// 2. High-Density KPI Telemetry Row
// =====================================================================================
@Composable
private fun MomentumKpiTelemetryRow(
  metrics: MomentumMetrics,
  onAddMilestoneClick: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("momentum_kpi_row"),
    horizontalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    // Velocity Index Tile
    KpiTile(
      label = "VELOCITY INDEX",
      value = "${String.format("%.2f", metrics.velocityRatio)}x",
      subtitle = if (metrics.velocityRatio >= 1.0f) "Ahead of Pace" else "Catching Up",
      valueColor = if (metrics.velocityRatio >= 1.0f) TitanEmerald else TitanGold,
      modifier = Modifier.weight(1f)
    )

    // Milestones Stored Tile
    KpiTile(
      label = "MILESTONES",
      value = "${metrics.completedMilestones}/${metrics.totalMilestones}",
      subtitle = "${metrics.overallGrowthPercent}% Achieved",
      valueColor = TitanCyan,
      modifier = Modifier.weight(1f)
    )

    // Skills Mastered Tile
    KpiTile(
      label = "SKILLS MASTERED",
      value = "${metrics.totalSkillsAcquired}",
      subtitle = "+${metrics.totalSkillsPlanned} Planned",
      valueColor = TitanIndigo,
      modifier = Modifier.weight(1f)
    )

    // Target Readiness Tile
    KpiTile(
      label = "READINESS",
      value = "${metrics.readinessScore}%",
      subtitle = metrics.projectedTargetQuarter,
      valueColor = TitanGold,
      modifier = Modifier.weight(1f)
    )
  }
}

@Composable
private fun KpiTile(
  label: String,
  value: String,
  subtitle: String,
  valueColor: Color,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier,
    colors = CardDefaults.cardColors(containerColor = SlateCard),
    shape = RoundedCornerShape(12.dp),
    border = BorderStroke(1.dp, SlateBorder)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(10.dp)
    ) {
      Text(text = label, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextMutedDark)
      Spacer(modifier = Modifier.height(4.dp))
      Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = valueColor)
      Spacer(modifier = Modifier.height(2.dp))
      Text(text = subtitle, fontSize = 10.sp, color = TextSecondaryDark, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
  }
}

// =====================================================================================
// Horizon Filter Row
// =====================================================================================
@Composable
private fun HorizonFilterRow(
  selectedFilter: String,
  onSelectFilter: (String) -> Unit
) {
  val filters = listOf("ALL" to "All Horizons", "6M" to "6-Month", "1Y" to "1-Year", "2Y" to "2-Year", "5Y" to "Long Range")
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("momentum_horizon_filters"),
    horizontalArrangement = Arrangement.spacedBy(6.dp)
  ) {
    filters.forEach { (key, label) ->
      val isSelected = selectedFilter == key
      FilterChip(
        selected = isSelected,
        onClick = { onSelectFilter(key) },
        label = { Text(label, fontSize = 10.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
        colors = FilterChipDefaults.filterChipColors(
          selectedContainerColor = TitanCyan.copy(alpha = 0.2f),
          selectedLabelColor = TitanCyan,
          containerColor = SlateDarker,
          labelColor = TextMutedDark
        ),
        border = FilterChipDefaults.filterChipBorder(
          enabled = true,
          selected = isSelected,
          borderColor = if (isSelected) TitanCyan else SlateBorder
        ),
        modifier = Modifier.height(30.dp)
      )
    }
  }
}

// =====================================================================================
// Series Toggle Strip
// =====================================================================================
@Composable
private fun SeriesToggleStrip(
  showActual: Boolean,
  onToggleActual: () -> Unit,
  showBenchmark: Boolean,
  onToggleBenchmark: () -> Unit,
  showCumulative: Boolean,
  onToggleCumulative: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("series_toggle_strip"),
    horizontalArrangement = Arrangement.spacedBy(12.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    SeriesTogglePill(
      label = "Actual Growth",
      color = TitanCyan,
      active = showActual,
      onClick = onToggleActual
    )
    SeriesTogglePill(
      label = "Benchmark Pace",
      color = TitanGold,
      active = showBenchmark,
      onClick = onToggleBenchmark,
      isDashed = true
    )
    SeriesTogglePill(
      label = "Cumulative Area",
      color = TitanEmerald,
      active = showCumulative,
      onClick = onToggleCumulative
    )
  }
}

@Composable
private fun SeriesTogglePill(
  label: String,
  color: Color,
  active: Boolean,
  onClick: () -> Unit,
  isDashed: Boolean = false
) {
  Row(
    modifier = Modifier
      .clip(RoundedCornerShape(6.dp))
      .clickable { onClick() }
      .background(if (active) color.copy(alpha = 0.12f) else Color.Transparent)
      .border(1.dp, if (active) color.copy(alpha = 0.5f) else SlateBorder.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
      .padding(horizontal = 8.dp, vertical = 4.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(5.dp)
  ) {
    Box(
      modifier = Modifier
        .size(8.dp)
        .clip(CircleShape)
        .background(if (active) color else TextMutedDark.copy(alpha = 0.5f))
    )
    Text(
      text = label,
      fontSize = 10.sp,
      color = if (active) TextPrimaryDark else TextMutedDark,
      fontWeight = if (active) FontWeight.SemiBold else FontWeight.Normal
    )
  }
}

// =====================================================================================
// RECHARTS CANVAS ENGINE: Area Velocity Curve
// =====================================================================================
@Composable
private fun RechartsAreaVelocityCanvas(
  nodes: List<MomentumTimelineNode>,
  activeScrubIndex: Int,
  onScrubIndexChange: (Int) -> Unit,
  animProgress: Float,
  showActual: Boolean,
  showBenchmark: Boolean,
  showFill: Boolean
) {
  val textMeasurer = rememberTextMeasurer()

  Canvas(
    modifier = Modifier
      .fillMaxWidth()
      .height(260.dp)
      .pointerInput(nodes) {
        detectTapGestures { offset ->
          val padLeft = 40.dp.toPx()
          val padRight = 20.dp.toPx()
          val chartW = size.width - padLeft - padRight
          if (chartW > 0 && nodes.size > 1) {
            val stepX = chartW / (nodes.size - 1)
            val index = ((offset.x - padLeft + (stepX / 2f)) / stepX).toInt().coerceIn(0, nodes.size - 1)
            onScrubIndexChange(index)
          }
        }
      }
      .pointerInput(nodes) {
        detectDragGestures { change, _ ->
          val padLeft = 40.dp.toPx()
          val padRight = 20.dp.toPx()
          val chartW = size.width - padLeft - padRight
          if (chartW > 0 && nodes.size > 1) {
            val stepX = chartW / (nodes.size - 1)
            val index = ((change.position.x - padLeft + (stepX / 2f)) / stepX).toInt().coerceIn(0, nodes.size - 1)
            onScrubIndexChange(index)
          }
        }
      }
  ) {
    if (nodes.isEmpty()) return@Canvas

    val padL = 40.dp.toPx()
    val padR = 20.dp.toPx()
    val padT = 24.dp.toPx()
    val padB = 36.dp.toPx()

    val w = size.width - padL - padR
    val h = size.height - padT - padB

    // Draw Cartesian Grid Lines & Y-Ticks (0%, 25%, 50%, 75%, 100%)
    val gridSteps = 4
    val dashEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)

    for (i in 0..gridSteps) {
      val yRatio = i.toFloat() / gridSteps.toFloat()
      val y = padT + (h * yRatio)
      val percentVal = 100 - (i * 25)

      drawLine(
        color = SlateBorder.copy(alpha = 0.5f),
        start = Offset(padL, y),
        end = Offset(size.width - padR, y),
        strokeWidth = 1f,
        pathEffect = dashEffect
      )

      drawText(
        textMeasurer = textMeasurer,
        text = "$percentVal%",
        style = TextStyle(color = TextMutedDark, fontSize = 9.sp, fontFamily = FontFamily.Monospace),
        topLeft = Offset(padL - 32.dp.toPx(), y - 6.dp.toPx())
      )
    }

    val stepX = if (nodes.size > 1) w / (nodes.size - 1) else w

    // Benchmark Curve (Dashed Gold Spline)
    if (showBenchmark && nodes.size > 1) {
      val benchPath = Path()
      nodes.forEachIndexed { i, node ->
        val x = padL + (i * stepX)
        val y = padT + h * (1f - (node.benchmarkVelocity / 100f * animProgress).coerceIn(0f, 1f))
        if (i == 0) benchPath.moveTo(x, y) else benchPath.lineTo(x, y)
      }

      drawPath(
        path = benchPath,
        color = TitanGold.copy(alpha = 0.65f),
        style = Stroke(
          width = 2.dp.toPx(),
          cap = StrokeCap.Round,
          pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f), 0f)
        )
      )
    }

    // Actual Growth Curve (Cubic Spline with Area Gradient)
    val points = nodes.mapIndexed { i, node ->
      val x = padL + (i * stepX)
      val y = padT + h * (1f - (node.growthPercent / 100f * animProgress).coerceIn(0f, 1f))
      Offset(x, y)
    }

    if (points.size > 1 && showActual) {
      val areaPath = Path()
      val linePath = Path()

      linePath.moveTo(points[0].x, points[0].y)
      areaPath.moveTo(points[0].x, padT + h)
      areaPath.lineTo(points[0].x, points[0].y)

      for (i in 0 until points.size - 1) {
        val p0 = points[i]
        val p1 = points[i + 1]
        val cx1 = p0.x + (p1.x - p0.x) / 2f
        val cy1 = p0.y
        val cx2 = p0.x + (p1.x - p0.x) / 2f
        val cy2 = p1.y

        linePath.cubicTo(cx1, cy1, cx2, cy2, p1.x, p1.y)
        areaPath.cubicTo(cx1, cy1, cx2, cy2, p1.x, p1.y)
      }

      areaPath.lineTo(points.last().x, padT + h)
      areaPath.close()

      // Area fill
      if (showFill) {
        drawPath(
          path = areaPath,
          brush = Brush.verticalGradient(
            colors = listOf(
              TitanCyan.copy(alpha = 0.35f * animProgress),
              TitanCyan.copy(alpha = 0.08f * animProgress),
              Color.Transparent
            ),
            startY = padT,
            endY = padT + h
          )
        )
      }

      // Line Stroke
      drawPath(
        path = linePath,
        brush = Brush.horizontalGradient(listOf(TitanCyan, TitanEmerald)),
        style = Stroke(
          width = 3.dp.toPx(),
          cap = StrokeCap.Round,
          join = StrokeJoin.Round
        )
      )
    }

    // X-Axis Labels & Node Dots
    nodes.forEachIndexed { i, node ->
      val x = padL + (i * stepX)
      val actualY = padT + h * (1f - (node.growthPercent / 100f * animProgress).coerceIn(0f, 1f))
      val isScrubbed = i == activeScrubIndex

      // X Label
      drawText(
        textMeasurer = textMeasurer,
        text = node.quarter,
        style = TextStyle(
          color = if (isScrubbed) TitanCyan else TextMutedDark,
          fontSize = 9.sp,
          fontWeight = if (isScrubbed) FontWeight.Bold else FontWeight.Normal,
          fontFamily = FontFamily.Monospace
        ),
        topLeft = Offset(x - 14.dp.toPx(), padT + h + 8.dp.toPx())
      )

      // Draw Scrub Line
      if (isScrubbed) {
        drawLine(
          color = TitanCyan.copy(alpha = 0.5f),
          start = Offset(x, padT),
          end = Offset(x, padT + h),
          strokeWidth = 1.5.dp.toPx(),
          pathEffect = dashEffect
        )
      }

      // Outer glow & inner circle
      if (showActual) {
        val dotRadius = if (isScrubbed) 6.dp.toPx() else 4.dp.toPx()
        drawCircle(
          color = if (node.status == "ACHIEVED") TitanEmerald else TitanCyan,
          radius = dotRadius,
          center = Offset(x, actualY)
        )
        drawCircle(
          color = ObsidianDark,
          radius = dotRadius * 0.55f,
          center = Offset(x, actualY)
        )
      }
    }
  }
}

// =====================================================================================
// RECHARTS CANVAS ENGINE: Quarterly Velocity Bars
// =====================================================================================
@Composable
private fun RechartsQuarterlyBarsCanvas(
  nodes: List<MomentumTimelineNode>,
  activeScrubIndex: Int,
  onScrubIndexChange: (Int) -> Unit,
  animProgress: Float
) {
  val textMeasurer = rememberTextMeasurer()

  Canvas(
    modifier = Modifier
      .fillMaxWidth()
      .height(260.dp)
      .pointerInput(nodes) {
        detectTapGestures { offset ->
          val padLeft = 40.dp.toPx()
          val padRight = 20.dp.toPx()
          val chartW = size.width - padLeft - padRight
          if (chartW > 0 && nodes.isNotEmpty()) {
            val stepX = chartW / nodes.size
            val index = ((offset.x - padLeft) / stepX).toInt().coerceIn(0, nodes.size - 1)
            onScrubIndexChange(index)
          }
        }
      }
  ) {
    if (nodes.isEmpty()) return@Canvas

    val padL = 40.dp.toPx()
    val padR = 20.dp.toPx()
    val padT = 24.dp.toPx()
    val padB = 36.dp.toPx()

    val w = size.width - padL - padR
    val h = size.height - padT - padB

    val stepX = w / nodes.size
    val barWidth = (stepX * 0.55f).coerceAtMost(36.dp.toPx())

    // Target Benchmark horizontal pace line at 70%
    val targetY = padT + (h * (1f - 0.70f))
    drawLine(
      color = TitanGold.copy(alpha = 0.5f),
      start = Offset(padL, targetY),
      end = Offset(size.width - padR, targetY),
      strokeWidth = 1.5.dp.toPx(),
      pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
    )

    drawText(
      textMeasurer = textMeasurer,
      text = "TARGET PACE (70%)",
      style = TextStyle(color = TitanGold, fontSize = 8.sp, fontWeight = FontWeight.Bold),
      topLeft = Offset(size.width - padR - 100.dp.toPx(), targetY - 14.dp.toPx())
    )

    nodes.forEachIndexed { i, node ->
      val centerX = padL + (i * stepX) + (stepX / 2f)
      val barH = h * (node.growthPercent / 100f * animProgress).coerceIn(0.05f, 1f)
      val barTop = padT + h - barH
      val isScrubbed = i == activeScrubIndex

      val barColor = when {
        isScrubbed -> TitanCyan
        node.status == "ACHIEVED" -> TitanEmerald
        else -> TitanCyan.copy(alpha = 0.75f)
      }

      // Draw Bar
      drawRoundRect(
        brush = Brush.verticalGradient(
          colors = listOf(barColor, barColor.copy(alpha = 0.35f)),
          startY = barTop,
          endY = padT + h
        ),
        topLeft = Offset(centerX - (barWidth / 2f), barTop),
        size = Size(barWidth, barH),
        cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
      )

      // Value label on top
      drawText(
        textMeasurer = textMeasurer,
        text = "${node.skillsAcquired.size} Skills",
        style = TextStyle(color = if (isScrubbed) TitanCyan else TextMutedDark, fontSize = 8.sp, fontWeight = FontWeight.Bold),
        topLeft = Offset(centerX - 16.dp.toPx(), barTop - 14.dp.toPx())
      )

      // X Label
      drawText(
        textMeasurer = textMeasurer,
        text = node.quarter,
        style = TextStyle(
          color = if (isScrubbed) TitanCyan else TextMutedDark,
          fontSize = 9.sp,
          fontWeight = if (isScrubbed) FontWeight.Bold else FontWeight.Normal
        ),
        topLeft = Offset(centerX - 14.dp.toPx(), padT + h + 8.dp.toPx())
      )
    }
  }
}

// =====================================================================================
// RECHARTS CANVAS ENGINE: Cumulative Burn-Up Trajectory
// =====================================================================================
@Composable
private fun RechartsCumulativeBurnupCanvas(
  nodes: List<MomentumTimelineNode>,
  activeScrubIndex: Int,
  onScrubIndexChange: (Int) -> Unit,
  animProgress: Float,
  showBenchmark: Boolean
) {
  val textMeasurer = rememberTextMeasurer()

  Canvas(
    modifier = Modifier
      .fillMaxWidth()
      .height(260.dp)
      .pointerInput(nodes) {
        detectTapGestures { offset ->
          val padLeft = 40.dp.toPx()
          val padRight = 20.dp.toPx()
          val chartW = size.width - padLeft - padRight
          if (chartW > 0 && nodes.size > 1) {
            val stepX = chartW / (nodes.size - 1)
            val index = ((offset.x - padLeft + (stepX / 2f)) / stepX).toInt().coerceIn(0, nodes.size - 1)
            onScrubIndexChange(index)
          }
        }
      }
  ) {
    if (nodes.isEmpty()) return@Canvas

    val padL = 40.dp.toPx()
    val padR = 20.dp.toPx()
    val padT = 24.dp.toPx()
    val padB = 36.dp.toPx()

    val w = size.width - padL - padR
    val h = size.height - padT - padB

    val maxCumulative = (nodes.maxOfOrNull { it.cumulativeSkills } ?: 20).coerceAtLeast(20)
    val stepX = if (nodes.size > 1) w / (nodes.size - 1) else w

    // Ceiling target line
    val ceilingY = padT + (h * 0.15f)
    drawLine(
      color = TitanIndigo.copy(alpha = 0.55f),
      start = Offset(padL, ceilingY),
      end = Offset(size.width - padR, ceilingY),
      strokeWidth = 1.dp.toPx(),
      pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 4f), 0f)
    )
    drawText(
      textMeasurer = textMeasurer,
      text = "TARGET SENIOR CEILING (24+ SKILLS)",
      style = TextStyle(color = TitanIndigo, fontSize = 8.sp, fontWeight = FontWeight.Bold),
      topLeft = Offset(padL + 8.dp.toPx(), ceilingY - 12.dp.toPx())
    )

    // Cumulative Points
    val points = nodes.mapIndexed { i, node ->
      val x = padL + (i * stepX)
      val ratio = (node.cumulativeSkills.toFloat() / maxCumulative.toFloat()) * animProgress
      val y = padT + h * (1f - ratio.coerceIn(0f, 1f))
      Offset(x, y)
    }

    if (points.size > 1) {
      val burnupPath = Path()
      burnupPath.moveTo(points[0].x, points[0].y)
      for (i in 0 until points.size - 1) {
        val midX = (points[i].x + points[i + 1].x) / 2f
        burnupPath.cubicTo(midX, points[i].y, midX, points[i + 1].y, points[i + 1].x, points[i + 1].y)
      }

      drawPath(
        path = burnupPath,
        color = TitanCyan,
        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
      )
    }

    nodes.forEachIndexed { i, node ->
      val x = padL + (i * stepX)
      val ratio = (node.cumulativeSkills.toFloat() / maxCumulative.toFloat()) * animProgress
      val y = padT + h * (1f - ratio.coerceIn(0f, 1f))
      val isScrubbed = i == activeScrubIndex

      drawCircle(color = TitanCyan, radius = if (isScrubbed) 6.dp.toPx() else 4.dp.toPx(), center = Offset(x, y))
      drawCircle(color = ObsidianDark, radius = 2.dp.toPx(), center = Offset(x, y))

      drawText(
        textMeasurer = textMeasurer,
        text = "${node.cumulativeSkills}",
        style = TextStyle(color = if (isScrubbed) TitanCyan else TextMutedDark, fontSize = 9.sp, fontWeight = FontWeight.Bold),
        topLeft = Offset(x - 6.dp.toPx(), y - 14.dp.toPx())
      )

      drawText(
        textMeasurer = textMeasurer,
        text = node.quarter,
        style = TextStyle(color = TextMutedDark, fontSize = 9.sp),
        topLeft = Offset(x - 12.dp.toPx(), padT + h + 8.dp.toPx())
      )
    }
  }
}

// =====================================================================================
// RECHARTS CANVAS ENGINE: Domain Mastery Breakdown
// =====================================================================================
@Composable
private fun RechartsDomainMasteryCanvas(
  nodes: List<MomentumTimelineNode>,
  activeScrubIndex: Int,
  onScrubIndexChange: (Int) -> Unit,
  animProgress: Float
) {
  val textMeasurer = rememberTextMeasurer()

  val domains = listOf(
    "Technical Systems" to 85,
    "Architecture & Scale" to 72,
    "Product Strategy" to 60,
    "Leadership & Mentorship" to 45
  )

  Canvas(
    modifier = Modifier
      .fillMaxWidth()
      .height(260.dp)
  ) {
    val padL = 30.dp.toPx()
    val padR = 30.dp.toPx()
    val padT = 30.dp.toPx()
    val barHeight = 24.dp.toPx()
    val spacing = 28.dp.toPx()

    val w = size.width - padL - padR

    domains.forEachIndexed { i, (domain, mastery) ->
      val y = padT + (i * (barHeight + spacing))
      val progressW = w * (mastery / 100f * animProgress)

      drawText(
        textMeasurer = textMeasurer,
        text = domain,
        style = TextStyle(color = TextPrimaryDark, fontSize = 11.sp, fontWeight = FontWeight.Bold),
        topLeft = Offset(padL, y - 16.dp.toPx())
      )

      drawText(
        textMeasurer = textMeasurer,
        text = "$mastery% Mastery",
        style = TextStyle(color = TitanCyan, fontSize = 10.sp, fontWeight = FontWeight.SemiBold),
        topLeft = Offset(size.width - padR - 65.dp.toPx(), y - 16.dp.toPx())
      )

      // Background track
      drawRoundRect(
        color = SlateBorder.copy(alpha = 0.4f),
        topLeft = Offset(padL, y),
        size = Size(w, barHeight),
        cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
      )

      // Progress bar
      drawRoundRect(
        brush = Brush.horizontalGradient(listOf(TitanCyan, TitanEmerald)),
        topLeft = Offset(padL, y),
        size = Size(progressW, barHeight),
        cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
      )
    }
  }
}

// =====================================================================================
// Active Scrubbed Node HUD (Tooltip)
// =====================================================================================
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ScrubbedNodeHudCard(
  node: MomentumTimelineNode,
  onToggleStatus: () -> Unit
) {
  Surface(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("scrubbed_node_hud_card"),
    color = SlateDarker,
    shape = RoundedCornerShape(12.dp),
    border = BorderStroke(1.dp, TitanCyan.copy(alpha = 0.35f))
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          Surface(
            color = if (node.status == "ACHIEVED") TitanEmerald.copy(alpha = 0.2f) else TitanCyan.copy(alpha = 0.2f),
            shape = RoundedCornerShape(6.dp)
          ) {
            Text(
              text = "${node.horizon} • ${node.quarter}",
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              color = if (node.status == "ACHIEVED") TitanEmerald else TitanCyan,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
          Text(
            text = node.milestoneTitle,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimaryDark,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        }

        // Status action
        OutlinedButton(
          onClick = onToggleStatus,
          shape = RoundedCornerShape(6.dp),
          colors = ButtonDefaults.outlinedButtonColors(contentColor = TitanCyan),
          border = BorderStroke(1.dp, TitanCyan.copy(alpha = 0.5f)),
          modifier = Modifier.height(28.dp)
        ) {
          Text(
            text = if (node.status == "ACHIEVED") "Completed" else "Mark Done",
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold
          )
        }
      }

      Text(
        text = node.rationale,
        fontSize = 11.sp,
        color = TextSecondaryDark,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis
      )

      // Skills chips
      FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        node.skillsAcquired.forEach { skill ->
          SkillTagChip(name = skill, isAcquired = true)
        }
        node.skillsInProgress.forEach { skill ->
          SkillTagChip(name = skill, isAcquired = false)
        }
      }
    }
  }
}

@Composable
private fun SkillTagChip(name: String, isAcquired: Boolean) {
  Surface(
    color = if (isAcquired) TitanEmerald.copy(alpha = 0.12f) else TitanCyan.copy(alpha = 0.12f),
    shape = RoundedCornerShape(4.dp),
    border = BorderStroke(1.dp, if (isAcquired) TitanEmerald.copy(alpha = 0.35f) else TitanCyan.copy(alpha = 0.35f))
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
      Icon(
        imageVector = if (isAcquired) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
        contentDescription = null,
        tint = if (isAcquired) TitanEmerald else TitanCyan,
        modifier = Modifier.size(10.dp)
      )
      Text(
        text = name,
        fontSize = 10.sp,
        fontWeight = FontWeight.Medium,
        color = if (isAcquired) TitanEmerald else TitanCyan
      )
    }
  }
}

// =====================================================================================
// 4. Stored Professional Milestones & Skills Timeline Stream (Room DB Connected)
// =====================================================================================
@Composable
private fun StoredMilestonesSkillsSection(
  milestonesWithTasks: List<MilestoneWithTasks>,
  nodes: List<MomentumTimelineNode>,
  onToggleMilestoneStatus: (String) -> Unit,
  onAddMilestoneClick: () -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("stored_milestones_skills_card"),
    colors = CardDefaults.cardColors(containerColor = SlateCard),
    shape = RoundedCornerShape(16.dp),
    border = BorderStroke(1.dp, SlateBorder)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Icon(Icons.Default.Flag, contentDescription = null, tint = TitanGold, modifier = Modifier.size(16.dp))
            Text(
              text = "PROFESSIONAL MILESTONES & SKILLS STREAM",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = TitanGold,
              letterSpacing = 1.sp
            )
          }
          Text(
            text = "Stored local Room milestones mapped to competencies",
            fontSize = 11.sp,
            color = TextMutedDark
          )
        }

        Button(
          onClick = onAddMilestoneClick,
          colors = ButtonDefaults.buttonColors(containerColor = TitanCyan),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier.height(32.dp).testTag("add_milestone_button")
        ) {
          Icon(Icons.Default.Add, contentDescription = null, tint = ObsidianDark, modifier = Modifier.size(14.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text(text = "Add Milestone", fontSize = 10.sp, color = ObsidianDark, fontWeight = FontWeight.Bold)
        }
      }

      nodes.forEach { node ->
        MilestoneNodeItemRow(
          node = node,
          onToggle = { onToggleMilestoneStatus(node.id) }
        )
      }
    }
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun MilestoneNodeItemRow(
  node: MomentumTimelineNode,
  onToggle: () -> Unit
) {
  Surface(
    modifier = Modifier.fillMaxWidth(),
    color = SlateDarker,
    shape = RoundedCornerShape(10.dp),
    border = BorderStroke(1.dp, SlateBorder.copy(alpha = 0.6f))
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          modifier = Modifier.weight(1f)
        ) {
          IconButton(
            onClick = onToggle,
            modifier = Modifier.size(24.dp)
          ) {
            Icon(
              imageVector = if (node.status == "ACHIEVED") Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
              contentDescription = "Toggle status",
              tint = if (node.status == "ACHIEVED") TitanEmerald else TextMutedDark,
              modifier = Modifier.size(18.dp)
            )
          }
          Column {
            Text(
              text = node.milestoneTitle,
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold,
              color = TextPrimaryDark
            )
            Text(
              text = "${node.horizon} • ${node.quarter} • ${node.targetMetric}",
              fontSize = 10.sp,
              color = TextMutedDark
            )
          }
        }

        Surface(
          color = if (node.status == "ACHIEVED") TitanEmerald.copy(alpha = 0.15f) else TitanGold.copy(alpha = 0.15f),
          shape = RoundedCornerShape(6.dp)
        ) {
          Text(
            text = node.status,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = if (node.status == "ACHIEVED") TitanEmerald else TitanGold,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
          )
        }
      }

      // Skills chips
      FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        node.skillsAcquired.forEach { skill ->
          SkillTagChip(name = skill, isAcquired = true)
        }
        node.skillsInProgress.forEach { skill ->
          SkillTagChip(name = skill, isAcquired = false)
        }
      }
    }
  }
}

// =====================================================================================
// 5. Strategic Growth Recommendations Card
// =====================================================================================
@Composable
private fun GrowthRecommendationsCard(
  metrics: MomentumMetrics,
  upcomingNode: MomentumTimelineNode?,
  onAddMilestoneClick: () -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("growth_recommendations_card"),
    colors = CardDefaults.cardColors(containerColor = SlateCard),
    shape = RoundedCornerShape(16.dp),
    border = BorderStroke(1.dp, TitanIndigo.copy(alpha = 0.35f))
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.weight(1f)
      ) {
        Box(
          modifier = Modifier
            .size(38.dp)
            .background(TitanIndigo.copy(alpha = 0.18f), CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Lightbulb,
            contentDescription = null,
            tint = TitanIndigo,
            modifier = Modifier.size(20.dp)
          )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
          Text(
            text = "STRATEGIC MOMENTUM LEVER",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = TitanIndigo,
            letterSpacing = 1.sp
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = upcomingNode?.let { "Next Focus: ${it.skillsInProgress.firstOrNull() ?: it.milestoneTitle}" }
              ?: "All planned milestones fully unlocked!",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimaryDark
          )
          Text(
            text = "Target readiness is pacing at ${metrics.readinessScore}% towards ${metrics.projectedTargetQuarter}.",
            fontSize = 11.sp,
            color = TextMutedDark
          )
        }
      }
    }
  }
}

// =====================================================================================
// Helper: Synthesizing Stored Milestones, Roadmap Phases, & Skills
// =====================================================================================
fun buildMomentumTimelineNodes(
  milestonesWithTasks: List<MilestoneWithTasks>,
  roadmap: CareerStrategyRoadmap?,
  userProfile: UserProfile?,
  horizonFilter: String = "ALL"
): List<MomentumTimelineNode> {
  val nodes = mutableListOf<MomentumTimelineNode>()

  // 1. Initial Baseline Node
  nodes.add(
    MomentumTimelineNode(
      id = "baseline_0",
      horizon = "Baseline",
      quarter = "Q3 '26",
      milestoneTitle = "Current Core Competencies",
      targetMetric = "Foundational Mastery",
      skillsAcquired = listOf("Kotlin", "Jetpack Compose", "Coroutines", "Room Database"),
      skillsInProgress = listOf("Distributed Systems", "Cloud Infrastructure"),
      cumulativeSkills = 4,
      growthPercent = 25,
      benchmarkVelocity = 20,
      status = "ACHIEVED",
      category = "FOUNDATIONAL",
      rationale = "Demonstrated execution across modern native Android state architecture and local storage.",
      tasksCompleted = 3,
      totalTasks = 3
    )
  )

  // 2. Synthesize from Room DB Milestones if available
  if (milestonesWithTasks.isNotEmpty()) {
    var accumSkills = 4
    val quarters = listOf("Q4 '26", "Q1 '27", "Q3 '27", "Q1 '28", "Q4 '28")

    milestonesWithTasks.sortedBy { it.milestone.horizonYears }.forEachIndexed { idx, mWithT ->
      val m = mWithT.milestone
      val acquiredFromTasks = mWithT.tasks.filter { it.isCompleted }.map { it.title }
      val inProgressFromTasks = mWithT.tasks.filter { !it.isCompleted }.map { it.title }

      val newSkills = if (acquiredFromTasks.isNotEmpty()) acquiredFromTasks else listOf(m.category, m.targetMetric)
      accumSkills += newSkills.size.coerceAtLeast(2)

      val targetQ = if (m.targetQuarter.isNotBlank()) m.targetQuarter else quarters.getOrElse(idx) { "Q${(idx % 4) + 1} '28" }
      val growth = mWithT.progressPercent.coerceAtLeast(30 + (idx * 16))

      nodes.add(
        MomentumTimelineNode(
          id = m.id,
          horizon = m.horizon,
          quarter = targetQ,
          milestoneTitle = m.title,
          targetMetric = m.targetMetric,
          skillsAcquired = if (acquiredFromTasks.isNotEmpty()) acquiredFromTasks else listOf("High-Scale Architecture", "System Design"),
          skillsInProgress = if (inProgressFromTasks.isNotEmpty()) inProgressFromTasks else listOf("Executive Influence"),
          cumulativeSkills = accumSkills,
          growthPercent = growth.coerceIn(20, 100),
          benchmarkVelocity = 25 + (idx * 16),
          status = m.status,
          category = m.category,
          rationale = m.strategicRationale.ifBlank { "High-impact capability sprint for ${m.horizon} horizon." },
          tasksCompleted = mWithT.completedTasksCount,
          totalTasks = mWithT.totalTasksCount
        )
      )
    }
  } else if (roadmap != null && roadmap.phases.isNotEmpty()) {
    // 3. Synthesize from Career Strategy Roadmap phases
    var accumSkills = 4
    val quarters = listOf("Q4 '26", "Q2 '27", "Q4 '27", "Q2 '28")
    roadmap.phases.forEachIndexed { idx, p ->
      val acquired = p.skillMilestones.filter { it.status == CareerMilestoneStatus.ACQUIRED }.map { it.title }
      val inProgress = p.skillMilestones.filter { it.status != CareerMilestoneStatus.ACQUIRED }.map { it.title }

      accumSkills += (acquired.size + 2)
      val q = quarters.getOrElse(idx) { "Q2 '28" }

      nodes.add(
        MomentumTimelineNode(
          id = p.phaseId,
          horizon = "Phase ${p.phaseNumber}",
          quarter = q,
          milestoneTitle = p.title,
          targetMetric = p.targetRoleTier,
          skillsAcquired = if (acquired.isNotEmpty()) acquired else listOf("Distributed Architecture"),
          skillsInProgress = if (inProgress.isNotEmpty()) inProgress else listOf("Strategic Dealflow"),
          cumulativeSkills = accumSkills,
          growthPercent = p.completionPercentage.coerceAtLeast(35 + (idx * 18)),
          benchmarkVelocity = 25 + (idx * 18),
          status = if (p.completionPercentage >= 100) "ACHIEVED" else "IN_PROGRESS",
          category = "STRATEGIC_EXPANSION",
          rationale = p.focusTheme,
          tasksCompleted = acquired.size,
          totalTasks = (acquired.size + inProgress.size).coerceAtLeast(1)
        )
      )
    }
  } else {
    // 4. Default Seed Nodes
    nodes.add(
      MomentumTimelineNode(
        id = "seed_1",
        horizon = "6M",
        quarter = "Q1 '27",
        milestoneTitle = "Distributed Systems & Cloud-Native",
        targetMetric = "Staff Engineer Readiness",
        skillsAcquired = listOf("Golang Microservices", "Kubernetes", "gRPC Protocol Buffers"),
        skillsInProgress = listOf("Kafka Event Streams"),
        cumulativeSkills = 8,
        growthPercent = 55,
        benchmarkVelocity = 42,
        status = "IN_PROGRESS",
        category = "SYSTEMS_ARCHITECTURE",
        rationale = "Master distributed architectures and asynchronous event pipelines.",
        tasksCompleted = 3,
        totalTasks = 4
      )
    )
    nodes.add(
      MomentumTimelineNode(
        id = "seed_2",
        horizon = "1Y",
        quarter = "Q3 '27",
        milestoneTitle = "AI Agent Architecture & Large Scale ML",
        targetMetric = "Principal AI Engineer",
        skillsAcquired = listOf("LLM Orchestration", "Vector Caching", "Model Context Protocol"),
        skillsInProgress = listOf("Fine-Tuning & Quantization"),
        cumulativeSkills = 13,
        growthPercent = 78,
        benchmarkVelocity = 64,
        status = "IN_PROGRESS",
        category = "AI_SYSTEMS",
        rationale = "Build scalable multi-agent systems with autonomous workflow engines.",
        tasksCompleted = 2,
        totalTasks = 4
      )
    )
    nodes.add(
      MomentumTimelineNode(
        id = "seed_3",
        horizon = "2Y",
        quarter = "Q2 '28",
        milestoneTitle = "Executive P&L & Board Advisory",
        targetMetric = "VP of Engineering / Head of Tech",
        skillsAcquired = listOf("Capital Allocation", "Organizational Scaling"),
        skillsInProgress = listOf("Global Strategy & Dealflow"),
        cumulativeSkills = 18,
        growthPercent = 92,
        benchmarkVelocity = 85,
        status = "PLANNED",
        category = "EXECUTIVE_LEADERSHIP",
        rationale = "Drive overarching technology strategy and organizational alignment.",
        tasksCompleted = 1,
        totalTasks = 3
      )
    )
  }

  // Filter if specific horizon chosen
  return if (horizonFilter == "ALL") {
    nodes
  } else {
    nodes.filter { it.horizon.contains(horizonFilter, ignoreCase = true) || it.horizon == "Baseline" }
  }
}

/**
 * Helper: Computing summary metrics
 */
fun calculateMomentumMetrics(
  milestonesWithTasks: List<MilestoneWithTasks>,
  roadmap: CareerStrategyRoadmap?,
  userProfile: UserProfile?
): MomentumMetrics {
  val totalMilestones = if (milestonesWithTasks.isNotEmpty()) milestonesWithTasks.size else (roadmap?.phases?.size ?: 3)
  val completedMilestones = if (milestonesWithTasks.isNotEmpty()) {
    milestonesWithTasks.count { it.milestone.status == "ACHIEVED" || it.progressPercent >= 100 }
  } else {
    roadmap?.phases?.count { it.completionPercentage >= 100 } ?: 1
  }

  val totalTasks = milestonesWithTasks.sumOf { it.totalTasksCount }.coerceAtLeast(10)
  val completedTasks = milestonesWithTasks.sumOf { it.completedTasksCount }.coerceAtLeast(4)

  val growthPercent = ((completedTasks.toFloat() / totalTasks.toFloat()) * 100).roundToInt().coerceIn(20, 100)
  val velocityRatio = (growthPercent.toFloat() / 50f).coerceIn(0.85f, 1.85f)

  val totalSkills = 8 + completedTasks * 2
  val plannedSkills = 12

  val readiness = ((growthPercent * 0.65f) + (completedMilestones.toFloat() / totalMilestones.toFloat() * 35f)).roundToInt().coerceIn(15, 98)

  val momentumTier = when {
    velocityRatio >= 1.35f -> "SUPERCHARGED"
    velocityRatio >= 1.0f -> "HIGH MOMENTUM"
    else -> "ON TRACK"
  }

  val summary = when {
    velocityRatio >= 1.35f -> "Tracking 35%+ ahead of standard benchmark velocity with accelerated milestone delivery."
    velocityRatio >= 1.0f -> "Maintaining strong forward momentum with consistent quarterly skill acquisition."
    else -> "Consistent cadence. Ready to activate next horizon milestone tasks."
  }

  return MomentumMetrics(
    velocityRatio = velocityRatio,
    completedMilestones = completedMilestones,
    totalMilestones = totalMilestones,
    totalSkillsAcquired = totalSkills,
    totalSkillsPlanned = plannedSkills,
    overallGrowthPercent = growthPercent,
    readinessScore = readiness,
    projectedTargetQuarter = "Q2 2027",
    momentumTier = momentumTier,
    momentumSummary = summary
  )
}

/**
 * Helper: Launching Android ShareSheet for Career Momentum Report
 */
private fun shareCareerMomentumReport(
  context: Context,
  metrics: MomentumMetrics,
  nodes: List<MomentumTimelineNode>,
  currentRole: String,
  targetGoals: String
) {
  val reportText = buildString {
    appendLine("════════════════════════════════════════════")
    appendLine("     TITAN CAREER MOMENTUM REPORT")
    appendLine("════════════════════════════════════════════")
    appendLine("Generated: September 2026")
    appendLine("Career Baseline: $currentRole")
    appendLine("Strategic Target: $targetGoals")
    appendLine("Momentum Tier: ${metrics.momentumTier} (${String.format("%.2f", metrics.velocityRatio)}x Benchmark)")
    appendLine("Career Readiness Score: ${metrics.readinessScore}%")
    appendLine("Projected Horizon: ${metrics.projectedTargetQuarter}")
    appendLine("Milestones Achieved: ${metrics.completedMilestones}/${metrics.totalMilestones}")
    appendLine("Total Competencies Mastered: ${metrics.totalSkillsAcquired}")
    appendLine()
    appendLine("── TIMELINE GROWTH MILESTONES ──")
    nodes.forEach { node ->
      appendLine("• [${node.quarter}] ${node.milestoneTitle} (${node.status})")
      appendLine("  Skills: ${node.skillsAcquired.joinToString(", ")}")
    }
    appendLine()
    appendLine("Powered by Titan Autonomous Career Suite • Recharts Data Engine")
  }

  val sendIntent = Intent(Intent.ACTION_SEND).apply {
    type = "text/plain"
    putExtra(Intent.EXTRA_SUBJECT, "Career Momentum Report: $currentRole → $targetGoals")
    putExtra(Intent.EXTRA_TEXT, reportText)
  }

  val chooser = Intent.createChooser(sendIntent, "Share Career Momentum Report").apply {
    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
  }
  context.startActivity(chooser)
}
