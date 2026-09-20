package com.example.ui.components

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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.data.model.CareerMilestoneStatus
import com.example.data.model.CareerStrategyRoadmap
import com.example.data.model.MilestoneWithTasks
import com.example.data.model.UserProfile
import com.example.ui.theme.ObsidianDark
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateCard
import com.example.ui.theme.SlateElevated
import com.example.ui.theme.TextMutedDark
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import com.example.ui.theme.TitanCyan
import com.example.ui.theme.TitanEmerald
import com.example.ui.theme.TitanGold
import com.example.ui.theme.TitanIndigo
import com.example.ui.theme.TitanViolet
import kotlin.math.roundToInt

enum class RechartsSkillViewMode(val title: String, val icon: ImageVector) {
  SKILL_GROWTH_CURVE("Skill Growth Area", Icons.AutoMirrored.Filled.ShowChart),
  VELOCITY_BARS("Quarterly Velocity", Icons.Default.BarChart),
  COMPETENCY_TIERS("Domain Mastery", Icons.Default.Layers)
}

data class SkillTimelineMilestone(
  val id: String,
  val horizonLabel: String,
  val quarterLabel: String,
  val milestoneTitle: String,
  val targetMetric: String,
  val skillsAcquired: List<String>,
  val skillsInProgress: List<String>,
  val cumulativeSkills: Int,
  val growthPercent: Int,
  val benchmarkVelocity: Int,
  val status: String,
  val category: String,
  val rationale: String
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CareerSkillTimelineRechartsVisualizer(
  milestonesWithTasks: List<MilestoneWithTasks>,
  roadmap: CareerStrategyRoadmap?,
  userProfile: UserProfile?,
  modifier: Modifier = Modifier,
  onAddMilestoneClick: () -> Unit = {},
  onToggleSkillMilestone: (String) -> Unit = {}
) {
  var selectedViewMode by remember { mutableStateOf(RechartsSkillViewMode.SKILL_GROWTH_CURVE) }
  val textMeasurer = rememberTextMeasurer()

  // Synthesize timeline data from documented professional milestones & career roadmap
  val timelineNodes = remember(milestonesWithTasks, roadmap, userProfile) {
    buildSkillTimelineNodes(milestonesWithTasks, roadmap, userProfile)
  }

  var activeScrubIndex by remember { mutableIntStateOf(timelineNodes.size - 1) }

  // Ensure scrub index stays within valid bounds when nodes change
  LaunchedEffect(timelineNodes.size) {
    if (activeScrubIndex >= timelineNodes.size) {
      activeScrubIndex = (timelineNodes.size - 1).coerceAtLeast(0)
    }
  }

  // Animation controller
  var animProgress by remember { mutableStateOf(0f) }
  val animatedProgress by animateFloatAsState(
    targetValue = animProgress,
    animationSpec = tween(durationMillis = 850, easing = FastOutSlowInEasing),
    label = "recharts_skill_growth_anim"
  )

  LaunchedEffect(milestonesWithTasks, roadmap, selectedViewMode) {
    animProgress = 0f
    animProgress = 1f
  }

  val activeNode = timelineNodes.getOrNull(activeScrubIndex) ?: timelineNodes.lastOrNull()

  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("career_skill_timeline_recharts_visualizer"),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = SlateCard),
    border = BorderStroke(1.dp, SlateBorder)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
    ) {
      // 1. Header Bar with Recharts Branding & Mode Switchers
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(24.dp)
                .background(TitanCyan.copy(alpha = 0.18f), RoundedCornerShape(6.dp)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Timeline,
                contentDescription = null,
                tint = TitanCyan,
                modifier = Modifier.size(15.dp)
              )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "RECHARTS DATA ENGINE",
              style = MaterialTheme.typography.labelSmall,
              color = TitanCyan,
              fontWeight = FontWeight.Bold,
              letterSpacing = 1.2.sp
            )
          }
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = "Skill Acquisition & Growth Timeline",
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimaryDark,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = "Chronological competency progression based on documented milestones",
            style = MaterialTheme.typography.bodySmall,
            color = TextMutedDark,
            fontSize = 11.sp
          )
        }

        // Mode Switcher Tabs
        Row(
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(SlateElevated)
            .border(1.dp, SlateBorder, RoundedCornerShape(8.dp))
            .padding(2.dp)
            .testTag("recharts_skill_mode_selector")
        ) {
          RechartsSkillViewMode.values().forEach { mode ->
            val isSelected = selectedViewMode == mode
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(if (isSelected) TitanCyan.copy(alpha = 0.22f) else Color.Transparent)
                .clickable { selectedViewMode = mode }
                .padding(horizontal = 8.dp, vertical = 6.dp),
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

      Spacer(modifier = Modifier.height(14.dp))

      // 2. High-Level Summary Telemetry Strip
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(ObsidianDark, RoundedCornerShape(10.dp))
          .border(1.dp, SlateBorder, RoundedCornerShape(10.dp))
          .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        val totalMilestones = timelineNodes.size
        val totalSkillsDocumented = timelineNodes.lastOrNull()?.cumulativeSkills ?: 0
        val currentProgress = timelineNodes.lastOrNull()?.growthPercent ?: 0

        Column(horizontalAlignment = Alignment.Start) {
          Text(text = "DOCUMENTED MILESTONES", fontSize = 9.sp, color = TextMutedDark, fontWeight = FontWeight.Bold)
          Text(text = "$totalMilestones Horizons", fontSize = 13.sp, color = TextPrimaryDark, fontWeight = FontWeight.Bold)
        }
        Box(modifier = Modifier.width(1.dp).height(24.dp).background(SlateBorder))
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text(text = "ACQUIRED SKILLS", fontSize = 9.sp, color = TextMutedDark, fontWeight = FontWeight.Bold)
          Text(text = "$totalSkillsDocumented Competencies", fontSize = 13.sp, color = TitanCyan, fontWeight = FontWeight.Bold)
        }
        Box(modifier = Modifier.width(1.dp).height(24.dp).background(SlateBorder))
        Column(horizontalAlignment = Alignment.End) {
          Text(text = "OVERALL GROWTH", fontSize = 9.sp, color = TextMutedDark, fontWeight = FontWeight.Bold)
          Text(text = "$currentProgress%", fontSize = 13.sp, color = TitanEmerald, fontWeight = FontWeight.Bold)
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // 3. Recharts Legend
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(TitanCyan))
          Spacer(modifier = Modifier.width(5.dp))
          Text(text = "Cumulative Skills Growth", fontSize = 11.sp, color = TextMutedDark)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(TitanEmerald))
          Spacer(modifier = Modifier.width(5.dp))
          Text(text = "Acquired Competency", fontSize = 11.sp, color = TextMutedDark)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .width(12.dp)
              .height(2.dp)
              .background(TitanGold)
          )
          Spacer(modifier = Modifier.width(5.dp))
          Text(text = "Target Velocity", fontSize = 11.sp, color = TextMutedDark)
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // 4. Interactive Recharts Hardware-Accelerated Canvas
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(230.dp)
          .background(ObsidianDark, RoundedCornerShape(12.dp))
          .border(1.dp, SlateBorder, RoundedCornerShape(12.dp))
          .padding(top = 16.dp, bottom = 12.dp, start = 8.dp, end = 12.dp)
          .testTag("recharts_skill_timeline_canvas_container")
          .pointerInput(timelineNodes) {
            detectTapGestures { offset ->
              val stepWidth = size.width / timelineNodes.size.coerceAtLeast(1)
              val idx = (offset.x / stepWidth).toInt().coerceIn(0, timelineNodes.size - 1)
              activeScrubIndex = idx
            }
          }
          .pointerInput(timelineNodes) {
            detectDragGestures { change, _ ->
              val stepWidth = size.width / timelineNodes.size.coerceAtLeast(1)
              val idx = (change.position.x / stepWidth).toInt().coerceIn(0, timelineNodes.size - 1)
              activeScrubIndex = idx
            }
          }
      ) {
        Canvas(
          modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
        ) {
          if (timelineNodes.isEmpty()) return@Canvas

          val paddingLeft = 36.dp.toPx()
          val paddingRight = 16.dp.toPx()
          val paddingTop = 16.dp.toPx()
          val paddingBottom = 28.dp.toPx()

          val chartWidth = size.width - paddingLeft - paddingRight
          val chartHeight = size.height - paddingTop - paddingBottom

          // 4A. Cartesian Grid (Horizontal & Vertical Recharts Style)
          val gridSteps = 4
          for (i in 0..gridSteps) {
            val y = paddingTop + (chartHeight * (i.toFloat() / gridSteps))
            drawLine(
              color = SlateBorder.copy(alpha = 0.5f),
              start = Offset(paddingLeft, y),
              end = Offset(size.width - paddingRight, y),
              strokeWidth = 1f,
              pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
            )

            // Y-Axis Metric Labels (Cumulative skills count scale)
            val maxSkills = timelineNodes.maxOfOrNull { it.cumulativeSkills }?.coerceAtLeast(10) ?: 10
            val labelVal = (maxSkills * (1f - (i.toFloat() / gridSteps))).roundToInt()
            drawText(
              textMeasurer = textMeasurer,
              text = "$labelVal",
              topLeft = Offset(4.dp.toPx(), y - 7.dp.toPx()),
              style = TextStyle(
                color = TextMutedDark,
                fontSize = 9.sp,
                fontFamily = FontFamily.Monospace
              )
            )
          }

          when (selectedViewMode) {
            RechartsSkillViewMode.SKILL_GROWTH_CURVE -> {
              drawSkillGrowthAreaCurve(
                nodes = timelineNodes,
                paddingLeft = paddingLeft,
                paddingRight = paddingRight,
                paddingTop = paddingTop,
                chartWidth = chartWidth,
                chartHeight = chartHeight,
                animatedProgress = animatedProgress,
                activeScrubIndex = activeScrubIndex,
                textMeasurer = textMeasurer
              )
            }
            RechartsSkillViewMode.VELOCITY_BARS -> {
              drawVelocityBars(
                nodes = timelineNodes,
                paddingLeft = paddingLeft,
                paddingRight = paddingRight,
                paddingTop = paddingTop,
                chartWidth = chartWidth,
                chartHeight = chartHeight,
                animatedProgress = animatedProgress,
                activeScrubIndex = activeScrubIndex,
                textMeasurer = textMeasurer
              )
            }
            RechartsSkillViewMode.COMPETENCY_TIERS -> {
              drawCompetencyTiers(
                nodes = timelineNodes,
                paddingLeft = paddingLeft,
                paddingRight = paddingRight,
                paddingTop = paddingTop,
                chartWidth = chartWidth,
                chartHeight = chartHeight,
                animatedProgress = animatedProgress,
                activeScrubIndex = activeScrubIndex,
                textMeasurer = textMeasurer
              )
            }
          }
        }

        // Floating Recharts Interactive Tooltip Overlay Card
        activeNode?.let { node ->
          val stepRatio = (activeScrubIndex.toFloat() / (timelineNodes.size - 1).coerceAtLeast(1))
          val isAlignedRight = stepRatio > 0.55f

          Box(
            modifier = Modifier
              .align(if (isAlignedRight) Alignment.TopStart else Alignment.TopEnd)
              .padding(start = 12.dp, end = 12.dp, top = 4.dp)
          ) {
            Card(
              colors = CardDefaults.cardColors(containerColor = SlateElevated),
              border = BorderStroke(1.dp, TitanCyan.copy(alpha = 0.7f)),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.width(190.dp).testTag("recharts_skill_tooltip")
            ) {
              Column(modifier = Modifier.padding(8.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(
                    text = node.quarterLabel,
                    color = TitanCyan,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                  )
                  Text(
                    text = "${node.growthPercent}% Mastery",
                    color = TitanEmerald,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                  )
                }
                Text(
                  text = node.milestoneTitle,
                  color = TextPrimaryDark,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.SemiBold,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(TitanCyan))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(
                    text = "Total Skills: ${node.cumulativeSkills}",
                    color = TextSecondaryDark,
                    fontSize = 10.sp
                  )
                }
                if (node.skillsAcquired.isNotEmpty()) {
                  Text(
                    text = "Key: ${node.skillsAcquired.take(2).joinToString(", ")}",
                    color = TextMutedDark,
                    fontSize = 9.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                  )
                }
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // 5. Selected Milestone Competency & Skill Acquisition Breakdown Card
      activeNode?.let { node ->
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .testTag("recharts_active_milestone_breakdown"),
          colors = CardDefaults.cardColors(containerColor = ObsidianDark),
          shape = RoundedCornerShape(12.dp),
          border = BorderStroke(1.dp, SlateBorder)
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(TitanCyan.copy(alpha = 0.15f))
                    .border(1.dp, TitanCyan.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                  Text(
                    text = node.quarterLabel,
                    color = TitanCyan,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                  )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                  Text(
                    text = node.milestoneTitle,
                    color = TextPrimaryDark,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                  )
                  Text(
                    text = "Target Metric: ${node.targetMetric}",
                    color = TextMutedDark,
                    fontSize = 11.sp
                  )
                }
              }

              // Status Badge
              Surface(
                color = when (node.status) {
                  "ACHIEVED" -> TitanEmerald.copy(alpha = 0.2f)
                  "IN_PROGRESS" -> TitanCyan.copy(alpha = 0.2f)
                  else -> TitanViolet.copy(alpha = 0.2f)
                },
                shape = RoundedCornerShape(6.dp)
              ) {
                Text(
                  text = node.status,
                  color = when (node.status) {
                    "ACHIEVED" -> TitanEmerald
                    "IN_PROGRESS" -> TitanCyan
                    else -> TitanViolet
                  },
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
              }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Skills Acquired in this Milestone
            if (node.skillsAcquired.isNotEmpty()) {
              Text(
                text = "Documented Skills Mastered in Milestone:",
                color = TextSecondaryDark,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
              )
              Spacer(modifier = Modifier.height(6.dp))
              FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                node.skillsAcquired.forEach { skill ->
                  Box(
                    modifier = Modifier
                      .clip(RoundedCornerShape(12.dp))
                      .background(TitanEmerald.copy(alpha = 0.15f))
                      .border(1.dp, TitanEmerald.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                      .padding(horizontal = 8.dp, vertical = 4.dp)
                  ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = TitanEmerald,
                        modifier = Modifier.size(12.dp)
                      )
                      Spacer(modifier = Modifier.width(4.dp))
                      Text(text = skill, color = TextPrimaryDark, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                    }
                  }
                }
              }
            }

            // Skills In-Progress
            if (node.skillsInProgress.isNotEmpty()) {
              Spacer(modifier = Modifier.height(8.dp))
              Text(
                text = "In-Flight Competencies Under Cultivation:",
                color = TextSecondaryDark,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
              )
              Spacer(modifier = Modifier.height(6.dp))
              FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                node.skillsInProgress.forEach { skill ->
                  Box(
                    modifier = Modifier
                      .clip(RoundedCornerShape(12.dp))
                      .background(TitanGold.copy(alpha = 0.15f))
                      .border(1.dp, TitanGold.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                      .padding(horizontal = 8.dp, vertical = 4.dp)
                  ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Box(
                        modifier = Modifier
                          .size(6.dp)
                          .clip(CircleShape)
                          .background(TitanGold)
                      )
                      Spacer(modifier = Modifier.width(4.dp))
                      Text(text = skill, color = TextPrimaryDark, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                    }
                  }
                }
              }
            }

            // Strategic Rationale
            if (node.rationale.isNotBlank()) {
              Spacer(modifier = Modifier.height(8.dp))
              Text(
                text = node.rationale,
                color = TextMutedDark,
                fontSize = 11.sp,
                lineHeight = 15.sp
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // 6. Footer Note
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Interactive: Tap or drag chart to inspect milestone competencies",
          style = MaterialTheme.typography.bodySmall,
          color = TextMutedDark,
          fontSize = 10.sp
        )
        Text(
          text = "Recharts Monotone Spline Engine",
          style = MaterialTheme.typography.labelSmall,
          color = TitanCyan,
          fontSize = 10.sp,
          fontWeight = FontWeight.Bold
        )
      }
    }
  }
}

// -------------------------------------------------------------------------------------
// Canvas Drawing Helper: Recharts Smooth Monotone Spline Curve
// -------------------------------------------------------------------------------------
private fun DrawScope.drawSkillGrowthAreaCurve(
  nodes: List<SkillTimelineMilestone>,
  paddingLeft: Float,
  paddingRight: Float,
  paddingTop: Float,
  chartWidth: Float,
  chartHeight: Float,
  animatedProgress: Float,
  activeScrubIndex: Int,
  textMeasurer: TextMeasurer
) {
  val stepX = chartWidth / (nodes.size - 1).coerceAtLeast(1)
  val maxVal = nodes.maxOfOrNull { it.cumulativeSkills }?.coerceAtLeast(10)?.toFloat() ?: 10f

  val points = nodes.mapIndexed { idx, node ->
    val x = paddingLeft + (idx * stepX)
    val ratio = (node.cumulativeSkills / maxVal).coerceIn(0f, 1f)
    val y = (paddingTop + chartHeight) - (chartHeight * ratio * animatedProgress)
    Offset(x, y)
  }

  if (points.isEmpty()) return

  // Build Monotone Spline Path
  val strokePath = Path().apply {
    moveTo(points.first().x, points.first().y)
    for (i in 0 until points.size - 1) {
      val p0 = points[i]
      val p1 = points[i + 1]
      val controlPointX1 = p0.x + (p1.x - p0.x) / 2f
      val controlPointX2 = controlPointX1
      cubicTo(controlPointX1, p0.y, controlPointX2, p1.y, p1.x, p1.y)
    }
  }

  // Build Gradient Area Fill Path
  val fillPath = Path().apply {
    addPath(strokePath)
    lineTo(points.last().x, paddingTop + chartHeight)
    lineTo(points.first().x, paddingTop + chartHeight)
    close()
  }

  // Draw Gradient Fill (Cyan -> Emerald)
  drawPath(
    path = fillPath,
    brush = Brush.verticalGradient(
      colors = listOf(
        TitanCyan.copy(alpha = 0.28f * animatedProgress),
        TitanEmerald.copy(alpha = 0.12f * animatedProgress),
        Color.Transparent
      ),
      startY = paddingTop,
      endY = paddingTop + chartHeight
    )
  )

  // Draw Stroke Line
  drawPath(
    path = strokePath,
    brush = Brush.horizontalGradient(
      colors = listOf(TitanCyan, TitanEmerald, TitanViolet)
    ),
    style = Stroke(
      width = 3.5.dp.toPx(),
      cap = StrokeCap.Round,
      join = StrokeJoin.Round
    )
  )

  // Draw Milestone Dots and X-Axis Labels
  points.forEachIndexed { idx, point ->
    val isScrubbed = idx == activeScrubIndex
    val node = nodes[idx]

    // Halo
    drawCircle(
      color = if (isScrubbed) TitanCyan.copy(alpha = 0.35f) else Color.Transparent,
      radius = if (isScrubbed) 11.dp.toPx() else 0f,
      center = point
    )

    // Outer Circle
    drawCircle(
      color = when (node.status) {
        "ACHIEVED" -> TitanEmerald
        "IN_PROGRESS" -> TitanCyan
        else -> TitanViolet
      },
      radius = if (isScrubbed) 6.dp.toPx() else 4.5.dp.toPx(),
      center = point
    )

    // Inner Dot
    drawCircle(
      color = ObsidianDark,
      radius = if (isScrubbed) 3.dp.toPx() else 2.dp.toPx(),
      center = point
    )

    // X-Axis Quarter Label
    val quarterText = node.quarterLabel
    drawText(
      textMeasurer = textMeasurer,
      text = quarterText,
      topLeft = Offset(point.x - 16.dp.toPx(), paddingTop + chartHeight + 6.dp.toPx()),
      style = TextStyle(
        color = if (isScrubbed) TitanCyan else TextMutedDark,
        fontSize = 9.sp,
        fontWeight = if (isScrubbed) FontWeight.Bold else FontWeight.Normal,
        fontFamily = FontFamily.Monospace
      )
    )
  }

  // Draw Vertical Tooltip Cursor Line at Scrub Index
  if (activeScrubIndex in points.indices) {
    val scrubPoint = points[activeScrubIndex]
    drawLine(
      color = TitanCyan.copy(alpha = 0.75f),
      start = Offset(scrubPoint.x, paddingTop),
      end = Offset(scrubPoint.x, paddingTop + chartHeight),
      strokeWidth = 1.5.dp.toPx(),
      pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 4f), 0f)
    )
  }
}

// -------------------------------------------------------------------------------------
// Canvas Drawing Helper: Quarterly Velocity Bars
// -------------------------------------------------------------------------------------
private fun DrawScope.drawVelocityBars(
  nodes: List<SkillTimelineMilestone>,
  paddingLeft: Float,
  paddingRight: Float,
  paddingTop: Float,
  chartWidth: Float,
  chartHeight: Float,
  animatedProgress: Float,
  activeScrubIndex: Int,
  textMeasurer: TextMeasurer
) {
  val stepX = chartWidth / nodes.size.coerceAtLeast(1)
  val barWidth = (stepX * 0.45f).coerceAtMost(28.dp.toPx())
  val maxSkills = nodes.maxOfOrNull { it.skillsAcquired.size + it.skillsInProgress.size }?.coerceAtLeast(6)?.toFloat() ?: 6f

  nodes.forEachIndexed { idx, node ->
    val centerX = paddingLeft + (idx * stepX) + (stepX / 2f)
    val acquiredCount = node.skillsAcquired.size.toFloat()
    val inProgressCount = node.skillsInProgress.size.toFloat()

    val acquiredHeight = (chartHeight * (acquiredCount / maxSkills) * animatedProgress)
    val inProgressHeight = (chartHeight * (inProgressCount / maxSkills) * animatedProgress)

    val baseY = paddingTop + chartHeight

    // Acquired Bar (Emerald)
    drawRoundRect(
      color = TitanEmerald,
      topLeft = Offset(centerX - (barWidth / 2f), baseY - acquiredHeight),
      size = Size(barWidth, acquiredHeight),
      cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
    )

    // In-Progress Stacked Bar (Cyan/Gold)
    if (inProgressCount > 0) {
      drawRoundRect(
        color = TitanGold.copy(alpha = 0.85f),
        topLeft = Offset(centerX - (barWidth / 2f), baseY - acquiredHeight - inProgressHeight),
        size = Size(barWidth, inProgressHeight),
        cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
      )
    }

    // X-Axis Label
    val isScrubbed = idx == activeScrubIndex
    drawText(
      textMeasurer = textMeasurer,
      text = node.quarterLabel,
      topLeft = Offset(centerX - 16.dp.toPx(), baseY + 6.dp.toPx()),
      style = TextStyle(
        color = if (isScrubbed) TitanCyan else TextMutedDark,
        fontSize = 9.sp,
        fontWeight = if (isScrubbed) FontWeight.Bold else FontWeight.Normal,
        fontFamily = FontFamily.Monospace
      )
    )
  }
}

// -------------------------------------------------------------------------------------
// Canvas Drawing Helper: Domain Competency Tiers
// -------------------------------------------------------------------------------------
private fun DrawScope.drawCompetencyTiers(
  nodes: List<SkillTimelineMilestone>,
  paddingLeft: Float,
  paddingRight: Float,
  paddingTop: Float,
  chartWidth: Float,
  chartHeight: Float,
  animatedProgress: Float,
  activeScrubIndex: Int,
  textMeasurer: TextMeasurer
) {
  val stepX = chartWidth / (nodes.size - 1).coerceAtLeast(1)

  // Draw Horizon Tier bands
  val bands = listOf("Foundation", "Senior Eng", "Staff Architect", "Executive VP")
  bands.forEachIndexed { i, band ->
    val y = paddingTop + (chartHeight * (i.toFloat() / bands.size))
    drawText(
      textMeasurer = textMeasurer,
      text = band,
      topLeft = Offset(paddingLeft + 4.dp.toPx(), y + 4.dp.toPx()),
      style = TextStyle(
        color = TextMutedDark.copy(alpha = 0.45f),
        fontSize = 8.sp,
        fontFamily = FontFamily.Monospace
      )
    )
  }

  // Draw Progression line connecting growth percent
  val points = nodes.mapIndexed { idx, node ->
    val x = paddingLeft + (idx * stepX)
    val ratio = (node.growthPercent / 100f).coerceIn(0f, 1f)
    val y = (paddingTop + chartHeight) - (chartHeight * ratio * animatedProgress)
    Offset(x, y)
  }

  for (i in 0 until points.size - 1) {
    drawLine(
      color = TitanCyan,
      start = points[i],
      end = points[i + 1],
      strokeWidth = 3.dp.toPx(),
      cap = StrokeCap.Round
    )
  }

  points.forEachIndexed { idx, pt ->
    val isScrubbed = idx == activeScrubIndex
    drawCircle(
      color = if (isScrubbed) TitanCyan else TitanEmerald,
      radius = if (isScrubbed) 6.dp.toPx() else 4.dp.toPx(),
      center = pt
    )
    drawText(
      textMeasurer = textMeasurer,
      text = nodes[idx].quarterLabel,
      topLeft = Offset(pt.x - 14.dp.toPx(), paddingTop + chartHeight + 6.dp.toPx()),
      style = TextStyle(
        color = if (isScrubbed) TitanCyan else TextMutedDark,
        fontSize = 9.sp,
        fontWeight = if (isScrubbed) FontWeight.Bold else FontWeight.Normal
      )
    )
  }
}

// -------------------------------------------------------------------------------------
// Data Synthesis: Merging Room DB Milestones + Roadmap Phases + User Profile
// -------------------------------------------------------------------------------------
private fun buildSkillTimelineNodes(
  milestonesWithTasks: List<MilestoneWithTasks>,
  roadmap: CareerStrategyRoadmap?,
  userProfile: UserProfile?
): List<SkillTimelineMilestone> {
  val nodes = mutableListOf<SkillTimelineMilestone>()

  // 1. Initial Documented Foundation Milestone
  nodes.add(
    SkillTimelineMilestone(
      id = "baseline_0",
      horizonLabel = "Current",
      quarterLabel = "2024-Q3",
      milestoneTitle = "Core Foundation & Tech Stack",
      targetMetric = "Senior Engineering Competency",
      skillsAcquired = listOf("Kotlin", "Jetpack Compose", "Coroutines", "Clean Architecture", "Room DB"),
      skillsInProgress = listOf("CI/CD Pipeline", "Distributed Caching"),
      cumulativeSkills = 5,
      growthPercent = 25,
      benchmarkVelocity = 20,
      status = "ACHIEVED",
      category = "TECHNICAL_MASTERY",
      rationale = "Proven track record delivering scalable native Android apps with clean state management."
    )
  )

  // 2. Map Roadmap Phases if available
  if (roadmap != null && roadmap.phases.isNotEmpty()) {
    var accumulatedSkills = 5
    val quarters = listOf("2025-Q1", "2025-Q3", "2026-Q2", "2027-Q1", "2028-Q2")

    roadmap.phases.forEachIndexed { idx, phase ->
      val acquired = phase.skillMilestones.filter { it.status == CareerMilestoneStatus.ACQUIRED }.map { it.title }
      val inProgress = phase.skillMilestones.filter { it.status != CareerMilestoneStatus.ACQUIRED }.map { it.title }

      val newSkills = if (acquired.isNotEmpty()) acquired else phase.skillMilestones.map { it.title }.take(3)
      accumulatedSkills += newSkills.size.coerceAtLeast(2)

      val quarter = quarters.getOrElse(idx) { "202${6 + idx}-Q1" }
      val growth = ((accumulatedSkills / 24f) * 100).roundToInt().coerceIn(30, 100)

      nodes.add(
        SkillTimelineMilestone(
          id = phase.phaseId,
          horizonLabel = "Phase ${phase.phaseNumber}",
          quarterLabel = quarter,
          milestoneTitle = phase.title,
          targetMetric = phase.targetRoleTier,
          skillsAcquired = if (newSkills.isNotEmpty()) newSkills else listOf("System Design", "Cloud Infrastructure"),
          skillsInProgress = inProgress.ifEmpty { listOf("Cross-Functional Leadership") },
          cumulativeSkills = accumulatedSkills,
          growthPercent = growth,
          benchmarkVelocity = 25 + (idx * 15),
          status = if (phase.completionPercentage >= 100) "ACHIEVED" else "IN_PROGRESS",
          category = "SYSTEMS_ARCHITECTURE",
          rationale = phase.focusTheme
        )
      )
    }
  } else if (milestonesWithTasks.isNotEmpty()) {
    // 3. Fallback to Room DB Milestones with Tasks
    var accumulatedSkills = 5
    milestonesWithTasks.sortedBy { it.milestone.horizonYears }.forEachIndexed { idx, mWithT ->
      val m = mWithT.milestone
      val skillTasks = mWithT.tasks.filter { it.category == "SKILLS" || it.title.contains("Skill", ignoreCase = true) }
      val acquired = skillTasks.filter { it.isCompleted }.map { it.title }
      val inProgress = skillTasks.filter { !it.isCompleted }.map { it.title }

      val skillCount = skillTasks.size.coerceAtLeast(2)
      accumulatedSkills += skillCount

      nodes.add(
        SkillTimelineMilestone(
          id = m.id,
          horizonLabel = m.horizon,
          quarterLabel = m.targetQuarter.ifBlank { "2025-Q${(idx % 4) + 1}" },
          milestoneTitle = m.title,
          targetMetric = m.targetMetric,
          skillsAcquired = if (acquired.isNotEmpty()) acquired else listOf("High-Scale Architecture", "Observability"),
          skillsInProgress = if (inProgress.isNotEmpty()) inProgress else listOf("Executive Leadership"),
          cumulativeSkills = accumulatedSkills,
          growthPercent = mWithT.progressPercent.coerceAtLeast(25 + (idx * 18)),
          benchmarkVelocity = 20 + (idx * 16),
          status = m.status,
          category = m.category,
          rationale = m.strategicRationale
        )
      )
    }
  } else {
    // 4. Default Seed Professional Milestones
    nodes.add(
      SkillTimelineMilestone(
        id = "sample_1",
        quarterLabel = "2025-Q1",
        horizonLabel = "6M",
        milestoneTitle = "Distributed Systems & Cloud-Native",
        targetMetric = "Staff Engineer Readiness",
        skillsAcquired = listOf("Golang Microservices", "Kubernetes & Istio", "gRPC & Protocol Buffers", "Distributed Caching"),
        skillsInProgress = listOf("Kafka Stream Processing"),
        cumulativeSkills = 9,
        growthPercent = 48,
        benchmarkVelocity = 40,
        status = "IN_PROGRESS",
        category = "SYSTEMS_ARCHITECTURE",
        rationale = "Master high-throughput event-driven microservices to lead architectural transitions."
      )
    )
    nodes.add(
      SkillTimelineMilestone(
        id = "sample_2",
        quarterLabel = "2025-Q4",
        horizonLabel = "1Y",
        milestoneTitle = "AI Agent Architecture & Large Scale ML",
        targetMetric = "Principal AI Engineer",
        skillsAcquired = listOf("Gemini 2.0 Integration", "Vector Embeddings", "Autonomous Agents", "RAG Pipeline Ops"),
        skillsInProgress = listOf("Fine-tuning & LoRA"),
        cumulativeSkills = 14,
        growthPercent = 68,
        benchmarkVelocity = 60,
        status = "IN_PROGRESS",
        category = "AI_AUTONOMOUS_OPS",
        rationale = "Lead multi-agent career automation pipelines and autonomous decisioning workflows."
      )
    )
    nodes.add(
      SkillTimelineMilestone(
        id = "sample_3",
        quarterLabel = "2026-Q3",
        horizonLabel = "2Y",
        milestoneTitle = "Engineering Leadership & Org Scalability",
        targetMetric = "Engineering Director Tier",
        skillsAcquired = listOf("Team Topology Design", "P&L & Cloud Budgeting", "Executive Presence", "Strategic Hiring"),
        skillsInProgress = listOf("Board-Level Presentations"),
        cumulativeSkills = 18,
        growthPercent = 85,
        benchmarkVelocity = 80,
        status = "STRATEGIC",
        category = "LEADERSHIP",
        rationale = "Manage engineering organizations of 25+ engineers while steering technical vision."
      )
    )
    nodes.add(
      SkillTimelineMilestone(
        id = "sample_4",
        quarterLabel = "2028-Q1",
        horizonLabel = "5Y",
        milestoneTitle = "VP of Engineering / CTO Horizon",
        targetMetric = "Executive C-Suite",
        skillsAcquired = listOf("Venture Dealflow", "M&A Tech Due Diligence", "Enterprise Governance", "Global Strategy"),
        skillsInProgress = listOf("Board Directorship"),
        cumulativeSkills = 22,
        growthPercent = 100,
        benchmarkVelocity = 95,
        status = "STRATEGIC",
        category = "EXECUTIVE",
        rationale = "Full architectural, organizational, and strategic ownership for global enterprise growth."
      )
    )
  }

  return nodes
}
