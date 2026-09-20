package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CareerMilestoneStatus
import com.example.data.model.CareerStrategyRoadmap
import com.example.data.model.RoadmapPhase
import com.example.data.model.SkillMilestone
import com.example.service.FirestoreSyncState
import com.example.ui.theme.ObsidianDark
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateCard
import com.example.ui.theme.SlateElevated
import com.example.ui.theme.TextMutedDark
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import com.example.ui.theme.TitanAmber
import com.example.ui.theme.TitanCyan
import com.example.ui.theme.TitanEmerald
import com.example.ui.theme.TitanGold
import com.example.ui.theme.TitanIndigo
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

/**
 * Visual chart display mode for career goal completion.
 */
enum class CareerGoalChartMode(val title: String, val icon: ImageVector) {
  VELOCITY_CURVE("Pacing Curve", Icons.Default.Timeline),
  PHASE_BARS("Phase Breakdown", Icons.Default.BarChart),
  COMPETENCY_RADAR("Skill Domains", Icons.Default.PieChart)
}

/**
 * Comprehensive Progress Bar & Interactive Chart Component
 * Visualizes career goal completion using the existing roadmap milestones data stored in Firestore.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CareerGoalCompletionChartCard(
  roadmap: CareerStrategyRoadmap,
  currentRole: String = "Current Role",
  targetGoals: String = roadmap.targetRole,
  syncState: FirestoreSyncState = FirestoreSyncState.SYNCED,
  lastSyncedTimestamp: Long? = null,
  isSavingToFirestore: Boolean = false,
  onQuickSync: () -> Unit = {},
  onToggleMilestoneStatus: ((String) -> Unit)? = null,
  onUpdateMilestoneProgress: ((String, Int) -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  var selectedMode by remember { mutableStateOf(CareerGoalChartMode.VELOCITY_CURVE) }
  var selectedMilestone by remember { mutableStateOf<SkillMilestone?>(null) }
  var selectedMilestonePhase by remember { mutableStateOf<RoadmapPhase?>(null) }

  // Aggregate telemetry from Firestore roadmap data
  val allMilestones = remember(roadmap) {
    roadmap.phases.flatMap { it.skillMilestones }
  }
  val totalMilestones = allMilestones.size
  val acquiredMilestones = allMilestones.filter { it.status == CareerMilestoneStatus.ACQUIRED }
  val inProgressMilestones = allMilestones.filter { it.status == CareerMilestoneStatus.IN_PROGRESS }
  val notStartedMilestones = allMilestones.filter { it.status == CareerMilestoneStatus.NOT_STARTED }

  val acquiredCount = acquiredMilestones.size
  val inProgressCount = inProgressMilestones.size
  val notStartedCount = notStartedMilestones.size

  // Overall completion percentage calculation (weighted average progress)
  val overallPercentage = roadmap.overallProgressPercentage

  // Animated progress representation
  val animatedProgress by animateFloatAsState(
    targetValue = overallPercentage / 100f,
    animationSpec = tween(durationMillis = 900, easing = FastOutSlowInEasing),
    label = "overallGoalProgress"
  )

  Card(
    modifier = modifier
      .fillMaxWidth()
      .animateContentSize()
      .testTag("career_goal_completion_chart_card"),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = SlateCard),
    border = BorderStroke(1.dp, TitanCyan.copy(alpha = 0.45f))
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      // -----------------------------------------------------------------------
      // 1. Header: Goal Path + Firestore Live Vault Status
      // -----------------------------------------------------------------------
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(7.dp)
                .clip(CircleShape)
                .background(TitanEmerald)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "CAREER GOAL COMPLETION ENGINE",
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              letterSpacing = 1.sp,
              color = TitanCyan
            )
            Spacer(modifier = Modifier.width(6.dp))
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(TitanGold.copy(alpha = 0.15f))
                .padding(horizontal = 5.dp, vertical = 1.5.dp)
            ) {
              Text(
                text = "FIRESTORE VAULT",
                fontSize = 7.5.sp,
                fontWeight = FontWeight.Bold,
                color = TitanGold
              )
            }
          }

          Spacer(modifier = Modifier.height(4.dp))

          // Trajectory breadcrumbs
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
          ) {
            Text(
              text = currentRole.ifBlank { "Current Role" },
              fontSize = 12.sp,
              fontWeight = FontWeight.Medium,
              color = TextSecondaryDark,
              maxLines = 1
            )
            Spacer(modifier = Modifier.width(6.dp))
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowForward,
              contentDescription = null,
              tint = TitanCyan,
              modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = targetGoals.ifBlank { roadmap.targetRole },
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = TitanEmerald,
              maxLines = 1
            )
          }

          Text(
            text = "Target: ${roadmap.targetCompensation} • ${roadmap.targetHorizon}",
            fontSize = 10.sp,
            color = TextMutedDark
          )
        }

        // Firestore sync indicator & manual sync button
        Column(
          horizontalAlignment = Alignment.End,
          verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(
                when (syncState) {
                  FirestoreSyncState.SYNCED -> TitanEmerald.copy(alpha = 0.12f)
                  FirestoreSyncState.SYNCING -> TitanAmber.copy(alpha = 0.12f)
                  FirestoreSyncState.FAILED -> TitanAmber.copy(alpha = 0.12f)
                  FirestoreSyncState.IDLE -> SlateElevated
                }
              )
              .border(
                1.dp,
                when (syncState) {
                  FirestoreSyncState.SYNCED -> TitanEmerald.copy(alpha = 0.4f)
                  FirestoreSyncState.SYNCING -> TitanAmber.copy(alpha = 0.4f)
                  FirestoreSyncState.FAILED -> TitanAmber.copy(alpha = 0.4f)
                  FirestoreSyncState.IDLE -> SlateBorder
                },
                RoundedCornerShape(6.dp)
              )
              .clickable { onQuickSync() }
              .padding(horizontal = 8.dp, vertical = 4.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              if (isSavingToFirestore || syncState == FirestoreSyncState.SYNCING) {
                CircularProgressIndicator(
                  modifier = Modifier.size(10.dp),
                  color = TitanAmber,
                  strokeWidth = 1.5.dp
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Syncing...", fontSize = 9.sp, color = TitanAmber, fontWeight = FontWeight.Bold)
              } else {
                Icon(
                  imageVector = if (syncState == FirestoreSyncState.SYNCED) Icons.Default.CloudDone else Icons.Default.CloudUpload,
                  contentDescription = "Cloud Status",
                  tint = if (syncState == FirestoreSyncState.SYNCED) TitanEmerald else TitanCyan,
                  modifier = Modifier.size(11.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = if (syncState == FirestoreSyncState.SYNCED) "Synced" else "Save Cloud",
                  fontSize = 9.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (syncState == FirestoreSyncState.SYNCED) TitanEmerald else TitanCyan
                )
              }
            }
          }

          lastSyncedTimestamp?.let { ts ->
            val formatted = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(ts))
            Text(
              text = "Saved $formatted",
              fontSize = 8.5.sp,
              color = TextMutedDark
            )
          }
        }
      }

      // -----------------------------------------------------------------------
      // 2. Master Progress Bar & Telemetry Metric Strip
      // -----------------------------------------------------------------------
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(10.dp))
          .background(SlateElevated)
          .border(1.dp, SlateBorder, RoundedCornerShape(10.dp))
          .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.Bottom
        ) {
          Column {
            Text(
              text = "GOAL MATURITY PROGRESS",
              fontSize = 9.5.sp,
              fontWeight = FontWeight.Bold,
              color = TextMutedDark,
              letterSpacing = 0.5.sp
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "${(animatedProgress * 100).roundToInt()}%",
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = TitanEmerald
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "Completed",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = TextSecondaryDark
              )
            }
          }

          // Velocity delta badge
          val isAhead = overallPercentage >= 50
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(if (isAhead) TitanEmerald.copy(alpha = 0.15f) else TitanAmber.copy(alpha = 0.15f))
              .padding(horizontal = 8.dp, vertical = 4.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                contentDescription = null,
                tint = if (isAhead) TitanEmerald else TitanAmber,
                modifier = Modifier.size(12.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = if (isAhead) "On Track • Velocity +14%" else "Acquisition in Progress",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = if (isAhead) TitanEmerald else TitanAmber
              )
            }
          }
        }

        // Multi-Segment Visual Progress Bar
        // Segment 1: Acquired (Emerald)
        // Segment 2: In Progress (Cyan)
        // Segment 3: Not Started (Track)
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(10.dp)
            .clip(RoundedCornerShape(5.dp))
            .background(ObsidianDark)
            .border(1.dp, SlateBorder.copy(alpha = 0.4f), RoundedCornerShape(5.dp))
        ) {
          if (totalMilestones > 0) {
            val acquiredRatio = (acquiredCount.toFloat() / totalMilestones).coerceIn(0f, 1f)
            val inProgressRatio = (inProgressCount.toFloat() / totalMilestones).coerceIn(0f, 1f)

            Row(modifier = Modifier.fillMaxWidth().height(10.dp)) {
              if (acquiredRatio > 0f) {
                Box(
                  modifier = Modifier
                    .weight(acquiredRatio)
                    .height(10.dp)
                    .background(
                      Brush.horizontalGradient(listOf(TitanCyan, TitanEmerald))
                    )
                )
              }
              if (inProgressRatio > 0f) {
                Box(
                  modifier = Modifier
                    .weight(inProgressRatio)
                    .height(10.dp)
                    .background(
                      Brush.horizontalGradient(listOf(TitanIndigo, TitanCyan))
                    )
                )
              }
              val remainingRatio = (1f - acquiredRatio - inProgressRatio).coerceAtLeast(0f)
              if (remainingRatio > 0f) {
                Box(
                  modifier = Modifier
                    .weight(remainingRatio)
                    .height(10.dp)
                    .background(SlateElevated)
                )
              }
            }
          }
        }

        // Breakdown Legend Badges
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          MilestonePill(
            count = acquiredCount,
            label = "Acquired",
            color = TitanEmerald,
            total = totalMilestones
          )
          MilestonePill(
            count = inProgressCount,
            label = "In Progress",
            color = TitanCyan,
            total = totalMilestones
          )
          MilestonePill(
            count = notStartedCount,
            label = "Pending",
            color = TextMutedDark,
            total = totalMilestones
          )
        }
      }

      // -----------------------------------------------------------------------
      // 3. Visualization Mode Switcher Tabs
      // -----------------------------------------------------------------------
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        CareerGoalChartMode.values().forEach { mode ->
          val isSelected = selectedMode == mode
          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(8.dp))
              .background(if (isSelected) TitanCyan.copy(alpha = 0.18f) else SlateElevated)
              .border(
                1.dp,
                if (isSelected) TitanCyan else SlateBorder.copy(alpha = 0.6f),
                RoundedCornerShape(8.dp)
              )
              .clickable { selectedMode = mode }
              .padding(vertical = 7.dp, horizontal = 4.dp),
            contentAlignment = Alignment.Center
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.Center
            ) {
              Icon(
                imageVector = mode.icon,
                contentDescription = null,
                tint = if (isSelected) TitanCyan else TextMutedDark,
                modifier = Modifier.size(13.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = mode.title,
                fontSize = 10.5.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) TitanCyan else TextSecondaryDark
              )
            }
          }
        }
      }

      // -----------------------------------------------------------------------
      // 4. Dynamic Chart Rendering Area
      // -----------------------------------------------------------------------
      when (selectedMode) {
        CareerGoalChartMode.VELOCITY_CURVE -> {
          VelocityCurveCanvasChart(
            roadmap = roadmap,
            onSelectMilestone = { milestone, phase ->
              selectedMilestone = milestone
              selectedMilestonePhase = phase
            }
          )
        }

        CareerGoalChartMode.PHASE_BARS -> {
          PhaseBreakdownBarsChart(
            roadmap = roadmap,
            onSelectMilestone = { milestone, phase ->
              selectedMilestone = milestone
              selectedMilestonePhase = phase
            }
          )
        }

        CareerGoalChartMode.COMPETENCY_RADAR -> {
          CompetencyDomainsBreakdown(
            milestones = allMilestones,
            onSelectMilestone = { milestone ->
              selectedMilestone = milestone
              selectedMilestonePhase = roadmap.phases.find { p -> p.skillMilestones.any { it.id == milestone.id } }
            }
          )
        }
      }

      // -----------------------------------------------------------------------
      // 5. Interactive Milestone Inspector Drawer (Shown on point tap)
      // -----------------------------------------------------------------------
      AnimatedVisibility(
        visible = selectedMilestone != null,
        enter = fadeIn(),
        exit = fadeOut()
      ) {
        selectedMilestone?.let { m ->
          MilestoneInspectorCard(
            milestone = m,
            phase = selectedMilestonePhase,
            onClose = { selectedMilestone = null },
            onToggleStatus = {
              onToggleMilestoneStatus?.invoke(m.id)
              // Update local state copy
              val updated = m.copy(
                status = when (m.status) {
                  CareerMilestoneStatus.NOT_STARTED -> CareerMilestoneStatus.IN_PROGRESS
                  CareerMilestoneStatus.IN_PROGRESS -> CareerMilestoneStatus.ACQUIRED
                  CareerMilestoneStatus.ACQUIRED -> CareerMilestoneStatus.NOT_STARTED
                },
                progress = when (m.status) {
                  CareerMilestoneStatus.NOT_STARTED -> 50
                  CareerMilestoneStatus.IN_PROGRESS -> 100
                  CareerMilestoneStatus.ACQUIRED -> 0
                }
              )
              selectedMilestone = updated
            }
          )
        }
      }
    }
  }
}

// -----------------------------------------------------------------------------
// Component: Milestone Pill
// -----------------------------------------------------------------------------
@Composable
private fun MilestonePill(
  count: Int,
  label: String,
  color: Color,
  total: Int
) {
  Row(verticalAlignment = Alignment.CenterVertically) {
    Box(
      modifier = Modifier
        .size(6.dp)
        .clip(CircleShape)
        .background(color)
    )
    Spacer(modifier = Modifier.width(4.dp))
    Text(
      text = "$count $label",
      fontSize = 10.sp,
      fontWeight = FontWeight.SemiBold,
      color = TextSecondaryDark
    )
    Spacer(modifier = Modifier.width(2.dp))
    Text(
      text = "(${if (total > 0) ((count * 100) / total) else 0}%)",
      fontSize = 9.sp,
      color = TextMutedDark
    )
  }
}

// -----------------------------------------------------------------------------
// View 1: Velocity Curve Canvas Chart
// Plots the timeline pacing benchmark vs actual acquired milestone trajectory
// -----------------------------------------------------------------------------
@Composable
private fun VelocityCurveCanvasChart(
  roadmap: CareerStrategyRoadmap,
  onSelectMilestone: (SkillMilestone, RoadmapPhase) -> Unit
) {
  val textMeasurer = rememberTextMeasurer()
  var scrubbedX by remember { mutableFloatStateOf(-1f) }
  var hoveredPointIndex by remember { mutableIntStateOf(-1) }

  // Extract sequential milestones mapped to timeline points
  val phases = roadmap.phases
  val dataPoints = remember(phases) {
    val points = mutableListOf<MilestoneChartDataPoint>()
    var accumulatedTimeWeeks = 0f

    // Start origin (Month 0 / Current Baseline)
    points.add(
      MilestoneChartDataPoint(
        label = "Month 0",
        subLabel = "Current",
        timeWeeks = 0f,
        targetProgress = 0f,
        actualProgress = 0f,
        milestone = null,
        phase = null
      )
    )

    phases.forEachIndexed { pIdx, phase ->
      val milestones = phase.skillMilestones
      val phaseStartWeek = accumulatedTimeWeeks
      val phaseEstimatedDurationWeeks = (phase.skillMilestones.sumOf { it.estimatedWeeks }).coerceAtLeast(12)

      milestones.forEachIndexed { mIdx, milestone ->
        val milestoneOffset = (mIdx + 1).toFloat() / (milestones.size.coerceAtLeast(1)) * phaseEstimatedDurationWeeks
        val timePoint = phaseStartWeek + milestoneOffset
        val targetExpectedProgress = ((pIdx * 33f) + ((mIdx + 1).toFloat() / milestones.size) * 33f).coerceIn(0f, 100f)

        points.add(
          MilestoneChartDataPoint(
            label = "P${phase.phaseNumber} • M${mIdx + 1}",
            subLabel = phase.timeframe,
            timeWeeks = timePoint,
            targetProgress = targetExpectedProgress,
            actualProgress = milestone.progress.toFloat(),
            milestone = milestone,
            phase = phase
          )
        )
      }
      accumulatedTimeWeeks += phaseEstimatedDurationWeeks
    }
    points
  }

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(10.dp))
      .background(ObsidianDark)
      .border(1.dp, SlateBorder, RoundedCornerShape(10.dp))
      .padding(12.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.Timeline, contentDescription = null, tint = TitanCyan, modifier = Modifier.size(13.dp))
        Spacer(modifier = Modifier.width(5.dp))
        Text(
          text = "ACQUISITION PACING CURVE (ACTUAL VS TARGET)",
          fontSize = 9.5.sp,
          fontWeight = FontWeight.Bold,
          color = TextPrimaryDark
        )
      }

      Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(modifier = Modifier.size(8.dp, 2.dp).background(TitanEmerald))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Actual", fontSize = 8.5.sp, color = TextMutedDark)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(modifier = Modifier.size(8.dp, 2.dp).background(TitanGold))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Target Pacing", fontSize = 8.5.sp, color = TextMutedDark)
        }
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(180.dp)
    ) {
      Canvas(
        modifier = Modifier
          .fillMaxWidth()
          .height(180.dp)
          .pointerInput(dataPoints) {
            detectTapGestures { offset ->
              scrubbedX = offset.x
              // Find closest point
              val width = size.width
              val paddingLeft = 32.dp.toPx()
              val paddingRight = 16.dp.toPx()
              val graphWidth = width - paddingLeft - paddingRight

              if (dataPoints.isNotEmpty() && graphWidth > 0) {
                val step = graphWidth / (dataPoints.size - 1).coerceAtLeast(1)
                val index = (((offset.x - paddingLeft) / step).roundToInt()).coerceIn(0, dataPoints.size - 1)
                hoveredPointIndex = index
                val pt = dataPoints[index]
                if (pt.milestone != null && pt.phase != null) {
                  onSelectMilestone(pt.milestone, pt.phase)
                }
              }
            }
          }
          .pointerInput(dataPoints) {
            detectDragGestures(
              onDragStart = { offset ->
                scrubbedX = offset.x
              },
              onDrag = { change, _ ->
                scrubbedX = change.position.x
                val width = size.width
                val paddingLeft = 32.dp.toPx()
                val paddingRight = 16.dp.toPx()
                val graphWidth = width - paddingLeft - paddingRight
                if (dataPoints.isNotEmpty() && graphWidth > 0) {
                  val step = graphWidth / (dataPoints.size - 1).coerceAtLeast(1)
                  val index = (((change.position.x - paddingLeft) / step).roundToInt()).coerceIn(0, dataPoints.size - 1)
                  hoveredPointIndex = index
                  val pt = dataPoints[index]
                  if (pt.milestone != null && pt.phase != null) {
                    onSelectMilestone(pt.milestone, pt.phase)
                  }
                }
              }
            )
          }
      ) {
        val width = size.width
        val height = size.height
        val paddingLeft = 32.dp.toPx()
        val paddingRight = 16.dp.toPx()
        val paddingTop = 16.dp.toPx()
        val paddingBottom = 26.dp.toPx()

        val graphWidth = width - paddingLeft - paddingRight
        val graphHeight = height - paddingTop - paddingBottom

        if (dataPoints.isEmpty() || graphWidth <= 0 || graphHeight <= 0) return@Canvas

        // 1. Draw horizontal gridlines & Y-axis labels
        val gridYValues = listOf(0, 25, 50, 75, 100)
        gridYValues.forEach { yVal ->
          val yPos = paddingTop + graphHeight - (yVal / 100f) * graphHeight
          drawLine(
            color = SlateBorder.copy(alpha = 0.4f),
            start = Offset(paddingLeft, yPos),
            end = Offset(width - paddingRight, yPos),
            strokeWidth = 1.dp.toPx(),
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
          )

          // Y-axis label text
          val labelResult = textMeasurer.measure(
            text = "$yVal%",
            style = TextStyle(color = TextMutedDark, fontSize = 8.sp, fontFamily = FontFamily.Monospace)
          )
          drawText(
            textLayoutResult = labelResult,
            topLeft = Offset(paddingLeft - labelResult.size.width - 4.dp.toPx(), yPos - labelResult.size.height / 2)
          )
        }

        // 2. Map coordinates
        val count = dataPoints.size
        val stepX = graphWidth / (count - 1).coerceAtLeast(1)

        val targetPoints = mutableListOf<Offset>()
        val actualPoints = mutableListOf<Offset>()

        dataPoints.forEachIndexed { i, dp ->
          val x = paddingLeft + i * stepX
          val targetY = paddingTop + graphHeight - (dp.targetProgress / 100f) * graphHeight
          val actualY = paddingTop + graphHeight - (dp.actualProgress / 100f) * graphHeight

          targetPoints.add(Offset(x, targetY))
          actualPoints.add(Offset(x, actualY))
        }

        // 3. Draw Target Pacing Line (Dashed Gold)
        val targetPath = Path().apply {
          targetPoints.forEachIndexed { i, pt ->
            if (i == 0) moveTo(pt.x, pt.y) else lineTo(pt.x, pt.y)
          }
        }
        drawPath(
          path = targetPath,
          color = TitanGold.copy(alpha = 0.7f),
          style = Stroke(
            width = 2.dp.toPx(),
            cap = StrokeCap.Round,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f), 0f)
          )
        )

        // 4. Draw Actual Curve Gradient Fill & Line
        val actualPath = Path().apply {
          actualPoints.forEachIndexed { i, pt ->
            if (i == 0) {
              moveTo(pt.x, pt.y)
            } else {
              val prev = actualPoints[i - 1]
              val cx1 = prev.x + (pt.x - prev.x) / 2
              val cy1 = prev.y
              val cx2 = prev.x + (pt.x - prev.x) / 2
              val cy2 = pt.y
              cubicTo(cx1, cy1, cx2, cy2, pt.x, pt.y)
            }
          }
        }

        // Area gradient fill underneath actual curve
        val fillPath = Path().apply {
          addPath(actualPath)
          lineTo(actualPoints.last().x, paddingTop + graphHeight)
          lineTo(actualPoints.first().x, paddingTop + graphHeight)
          close()
        }

        drawPath(
          path = fillPath,
          brush = Brush.verticalGradient(
            colors = listOf(
              TitanCyan.copy(alpha = 0.28f),
              TitanEmerald.copy(alpha = 0.12f),
              Color.Transparent
            ),
            startY = paddingTop,
            endY = paddingTop + graphHeight
          )
        )

        // Actual Line stroke
        drawPath(
          path = actualPath,
          brush = Brush.horizontalGradient(listOf(TitanCyan, TitanEmerald)),
          style = Stroke(
            width = 3.dp.toPx(),
            cap = StrokeCap.Round,
            join = StrokeJoin.Round
          )
        )

        // 5. Draw milestone data nodes
        dataPoints.forEachIndexed { i, dp ->
          val pt = actualPoints[i]
          val isHovered = hoveredPointIndex == i

          val milestone = dp.milestone
          val nodeColor = when {
            milestone == null -> TitanCyan
            milestone.status == CareerMilestoneStatus.ACQUIRED -> TitanEmerald
            milestone.status == CareerMilestoneStatus.IN_PROGRESS -> TitanCyan
            else -> SlateBorder
          }

          // Outer glow ring if hovered or acquired
          if (isHovered || milestone?.status == CareerMilestoneStatus.ACQUIRED) {
            drawCircle(
              color = nodeColor.copy(alpha = 0.25f),
              radius = if (isHovered) 10.dp.toPx() else 7.dp.toPx(),
              center = pt
            )
          }

          // Inner solid circle
          drawCircle(
            color = ObsidianDark,
            radius = 5.dp.toPx(),
            center = pt
          )
          drawCircle(
            color = nodeColor,
            radius = 3.5.dp.toPx(),
            center = pt
          )

          // X-axis label
          if (i == 0 || i == dataPoints.size - 1 || i % 2 == 0) {
            val xLabelResult = textMeasurer.measure(
              text = dp.label,
              style = TextStyle(color = TextMutedDark, fontSize = 7.5.sp)
            )
            drawText(
              textLayoutResult = xLabelResult,
              topLeft = Offset(pt.x - xLabelResult.size.width / 2, paddingTop + graphHeight + 4.dp.toPx())
            )
          }
        }

        // 6. Draw vertical scrubber line if interactive
        if (hoveredPointIndex in dataPoints.indices) {
          val activePt = actualPoints[hoveredPointIndex]
          drawLine(
            color = TitanCyan.copy(alpha = 0.6f),
            start = Offset(activePt.x, paddingTop),
            end = Offset(activePt.x, paddingTop + graphHeight),
            strokeWidth = 1.dp.toPx(),
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 4f), 0f)
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(4.dp))
    Text(
      text = "💡 Tap or scrub any point to inspect milestone details, deliverables & verification proof",
      fontSize = 9.sp,
      color = TextMutedDark,
      style = MaterialTheme.typography.bodySmall
    )
  }
}

// -----------------------------------------------------------------------------
// View 2: Phase Breakdown Bars Chart
// Shows completion meters for Phase 1, Phase 2, and Phase 3
// -----------------------------------------------------------------------------
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PhaseBreakdownBarsChart(
  roadmap: CareerStrategyRoadmap,
  onSelectMilestone: (SkillMilestone, RoadmapPhase) -> Unit
) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(10.dp))
      .background(ObsidianDark)
      .border(1.dp, SlateBorder, RoundedCornerShape(10.dp))
      .padding(12.dp),
    verticalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    Text(
      text = "PHASED GOAL PROGRESSION & MILESTONES",
      fontSize = 9.5.sp,
      fontWeight = FontWeight.Bold,
      color = TextPrimaryDark
    )

    roadmap.phases.forEach { phase ->
      val phaseCompletion = phase.completionPercentage
      val phaseColor = when (phase.phaseNumber) {
        1 -> TitanCyan
        2 -> TitanGold
        else -> TitanIndigo
      }

      Column(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(8.dp))
          .background(SlateElevated)
          .border(1.dp, SlateBorder.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
          .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(6.dp)
                  .clip(CircleShape)
                  .background(phaseColor)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "PHASE ${phase.phaseNumber} • ${phase.title.uppercase()}",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimaryDark
              )
            }
            Text(
              text = "${phase.timeframe} • Target: ${phase.targetCompLakhs} (${phase.targetRoleTier})",
              fontSize = 9.sp,
              color = TextMutedDark
            )
          }

          Text(
            text = "$phaseCompletion%",
            fontSize = 13.sp,
            fontWeight = FontWeight.Black,
            color = phaseColor
          )
        }

        // Horizontal Phase Progress Bar
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(6.dp)
            .clip(RoundedCornerShape(3.dp))
            .background(ObsidianDark)
        ) {
          Box(
            modifier = Modifier
              .fillMaxWidth(fraction = (phaseCompletion / 100f).coerceIn(0f, 1f))
              .height(6.dp)
              .background(phaseColor)
          )
        }

        // Quick Milestone Pills in this Phase
        FlowRow(
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          verticalArrangement = Arrangement.spacedBy(4.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          phase.skillMilestones.forEach { m ->
            val isAcquired = m.status == CareerMilestoneStatus.ACQUIRED
            val isInProgress = m.status == CareerMilestoneStatus.IN_PROGRESS

            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(
                  when {
                    isAcquired -> TitanEmerald.copy(alpha = 0.15f)
                    isInProgress -> TitanCyan.copy(alpha = 0.15f)
                    else -> ObsidianDark
                  }
                )
                .border(
                  0.8.dp,
                  when {
                    isAcquired -> TitanEmerald.copy(alpha = 0.5f)
                    isInProgress -> TitanCyan.copy(alpha = 0.5f)
                    else -> SlateBorder
                  },
                  RoundedCornerShape(4.dp)
                )
                .clickable { onSelectMilestone(m, phase) }
                .padding(horizontal = 6.dp, vertical = 3.dp)
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = when {
                    isAcquired -> Icons.Default.CheckCircle
                    isInProgress -> Icons.AutoMirrored.Filled.TrendingUp
                    else -> Icons.Default.Flag
                  },
                  contentDescription = null,
                  tint = when {
                    isAcquired -> TitanEmerald
                    isInProgress -> TitanCyan
                    else -> TextMutedDark
                  },
                  modifier = Modifier.size(10.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "${m.title.take(18)}... (${m.progress}%)",
                  fontSize = 8.5.sp,
                  fontWeight = FontWeight.Medium,
                  color = if (isAcquired || isInProgress) TextPrimaryDark else TextMutedDark
                )
              }
            }
          }
        }
      }
    }
  }
}

// -----------------------------------------------------------------------------
// View 3: Competency Domains Breakdown
// Categorizes milestones into skill clusters and computes domain acquisition
// -----------------------------------------------------------------------------
@Composable
private fun CompetencyDomainsBreakdown(
  milestones: List<SkillMilestone>,
  onSelectMilestone: (SkillMilestone) -> Unit
) {
  // Group by category
  val categories = remember(milestones) {
    milestones.groupBy { it.category }.map { (category, mList) ->
      val avgProgress = if (mList.isEmpty()) 0 else (mList.sumOf { it.progress } / mList.size)
      val acquired = mList.count { it.status == CareerMilestoneStatus.ACQUIRED }
      DomainCategoryProgress(
        category = category,
        milestones = mList,
        averageProgress = avgProgress,
        acquiredCount = acquired,
        totalCount = mList.size
      )
    }.sortedByDescending { it.averageProgress }
  }

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(10.dp))
      .background(ObsidianDark)
      .border(1.dp, SlateBorder, RoundedCornerShape(10.dp))
      .padding(12.dp),
    verticalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    Text(
      text = "SKILL DOMAIN ACQUISITION METERS",
      fontSize = 9.5.sp,
      fontWeight = FontWeight.Bold,
      color = TextPrimaryDark
    )

    categories.forEach { domain ->
      val domainColor = when {
        domain.averageProgress >= 80 -> TitanEmerald
        domain.averageProgress >= 40 -> TitanCyan
        else -> TitanGold
      }

      Column(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(8.dp))
          .background(SlateElevated)
          .border(1.dp, SlateBorder.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
          .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = domain.category,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimaryDark
          )
          Text(
            text = "${domain.averageProgress}%",
            fontSize = 12.sp,
            fontWeight = FontWeight.Black,
            color = domainColor
          )
        }

        // Linear Progress bar
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(5.dp)
            .clip(RoundedCornerShape(2.5.dp))
            .background(ObsidianDark)
        ) {
          Box(
            modifier = Modifier
              .fillMaxWidth(fraction = (domain.averageProgress / 100f).coerceIn(0f, 1f))
              .height(5.dp)
              .background(domainColor)
          )
        }

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text(
            text = "${domain.acquiredCount} of ${domain.totalCount} Milestones Acquired",
            fontSize = 8.5.sp,
            color = TextMutedDark
          )
          Text(
            text = if (domain.averageProgress >= 75) "Mastery Tier" else "Active Practice",
            fontSize = 8.5.sp,
            fontWeight = FontWeight.SemiBold,
            color = domainColor
          )
        }
      }
    }
  }
}

// -----------------------------------------------------------------------------
// Component: Milestone Inspector Card (Popup Drawer on node tap)
// -----------------------------------------------------------------------------
@Composable
private fun MilestoneInspectorCard(
  milestone: SkillMilestone,
  phase: RoadmapPhase?,
  onClose: () -> Unit,
  onToggleStatus: () -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("milestone_inspector_card"),
    shape = RoundedCornerShape(10.dp),
    colors = CardDefaults.cardColors(containerColor = SlateElevated),
    border = BorderStroke(1.dp, TitanCyan.copy(alpha = 0.7f))
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
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(4.dp))
              .background(TitanCyan.copy(alpha = 0.2f))
              .padding(horizontal = 6.dp, vertical = 2.dp)
          ) {
            Text(
              text = milestone.category.uppercase(),
              fontSize = 8.sp,
              fontWeight = FontWeight.Bold,
              color = TitanCyan
            )
          }
          phase?.let { p ->
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Phase ${p.phaseNumber} (${p.timeframe})",
              fontSize = 9.sp,
              color = TextMutedDark
            )
          }
        }

        IconButton(onClick = onClose, modifier = Modifier.size(24.dp)) {
          Text("✕", fontSize = 12.sp, color = TextMutedDark)
        }
      }

      Text(
        text = milestone.title,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        color = TextPrimaryDark
      )

      if (milestone.proofOfWorkArtifact.isNotBlank()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Layers, contentDescription = null, tint = TitanGold, modifier = Modifier.size(12.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "Artifact: ${milestone.proofOfWorkArtifact}",
            fontSize = 9.5.sp,
            color = TitanGold
          )
        }
      }

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Target Proficiency: ${milestone.targetProficiency}",
            fontSize = 9.5.sp,
            color = TextSecondaryDark
          )
          Text(
            text = "Current Status: ${milestone.status.label} (${milestone.progress}%)",
            fontSize = 9.5.sp,
            fontWeight = FontWeight.Bold,
            color = when (milestone.status) {
              CareerMilestoneStatus.ACQUIRED -> TitanEmerald
              CareerMilestoneStatus.IN_PROGRESS -> TitanCyan
              CareerMilestoneStatus.NOT_STARTED -> TextMutedDark
            }
          )
        }

        Button(
          onClick = onToggleStatus,
          colors = ButtonDefaults.buttonColors(
            containerColor = when (milestone.status) {
              CareerMilestoneStatus.ACQUIRED -> SlateBorder
              CareerMilestoneStatus.IN_PROGRESS -> TitanEmerald
              CareerMilestoneStatus.NOT_STARTED -> TitanCyan
            }
          ),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier.height(30.dp)
        ) {
          Text(
            text = when (milestone.status) {
              CareerMilestoneStatus.ACQUIRED -> "Reset Status"
              CareerMilestoneStatus.IN_PROGRESS -> "Mark Acquired"
              CareerMilestoneStatus.NOT_STARTED -> "Start Working"
            },
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = if (milestone.status == CareerMilestoneStatus.ACQUIRED) TextPrimaryDark else ObsidianDark
          )
        }
      }
    }
  }
}

// -----------------------------------------------------------------------------
// Helper Data Classes
// -----------------------------------------------------------------------------
private data class MilestoneChartDataPoint(
  val label: String,
  val subLabel: String,
  val timeWeeks: Float,
  val targetProgress: Float,
  val actualProgress: Float,
  val milestone: SkillMilestone?,
  val phase: RoadmapPhase?
)

private data class DomainCategoryProgress(
  val category: String,
  val milestones: List<SkillMilestone>,
  val averageProgress: Int,
  val acquiredCount: Int,
  val totalCount: Int
)
