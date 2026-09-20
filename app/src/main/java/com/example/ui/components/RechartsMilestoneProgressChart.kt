package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.Timeline
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
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
import com.example.data.model.MilestoneWithTasks
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
import kotlin.math.roundToInt

enum class RechartsMode(val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
  VELOCITY_AREA("Velocity Area", Icons.AutoMirrored.Filled.ShowChart),
  TASK_BARS("Task Distribution", Icons.Default.BarChart),
  BURN_UP("Cumulative Pacing", Icons.Default.Timeline)
}

data class RechartsMilestonePoint(
  val label: String, // "Today", "6M", "1Y", "2Y", "3Y", "5Y"
  val horizonYears: Float,
  val targetQuarter: String,
  val milestoneTitle: String,
  val targetMetric: String,
  val completedTasks: Int,
  val totalTasks: Int,
  val progressPercent: Int, // 0 to 100
  val benchmarkPercent: Int, // Ideal expected velocity
  val status: String,
  val milestoneId: String? = null
)

@Composable
fun RechartsMilestoneProgressChart(
  milestonesWithTasks: List<MilestoneWithTasks>,
  modifier: Modifier = Modifier,
  onMilestoneSelected: ((String) -> Unit)? = null
) {
  var selectedMode by remember { mutableStateOf(RechartsMode.VELOCITY_AREA) }
  var selectedIndex by remember { mutableStateOf<Int?>(null) }
  var isInteracting by remember { mutableStateOf(false) }

  // Transform entities into unified timeline points
  val chartPoints = remember(milestonesWithTasks) {
    val points = mutableListOf<RechartsMilestonePoint>()
    // Point 0: Today baseline
    points.add(
      RechartsMilestonePoint(
        label = "Today",
        horizonYears = 0.0f,
        targetQuarter = "Q3 '26",
        milestoneTitle = "Career Baseline Established",
        targetMetric = "BBA + Autonomous Career Suite",
        completedTasks = 3,
        totalTasks = 3,
        progressPercent = 100,
        benchmarkPercent = 100,
        status = "ACHIEVED"
      )
    )

    // Add milestones mapped by horizon
    milestonesWithTasks.sortedBy { it.milestone.horizonYears }.forEach { mWithT ->
      val m = mWithT.milestone
      val benchmark = when (m.horizon) {
        "6M" -> 70
        "1Y" -> 45
        "2Y" -> 25
        "3Y" -> 15
        "5Y" -> 10
        else -> 20
      }
      points.add(
        RechartsMilestonePoint(
          label = m.horizon,
          horizonYears = m.horizonYears,
          targetQuarter = m.targetQuarter,
          milestoneTitle = m.title,
          targetMetric = m.targetMetric,
          completedTasks = mWithT.completedTasksCount,
          totalTasks = mWithT.totalTasksCount,
          progressPercent = mWithT.progressPercent,
          benchmarkPercent = benchmark,
          status = m.status,
          milestoneId = m.id
        )
      )
    }
    points
  }

  // Animation trigger
  var animProgress by remember { mutableStateOf(0f) }
  val animatedProgress by animateFloatAsState(
    targetValue = animProgress,
    animationSpec = tween(durationMillis = 850, easing = FastOutSlowInEasing),
    label = "recharts_anim"
  )

  LaunchedEffect(milestonesWithTasks, selectedMode) {
    animProgress = 0f
    animProgress = 1f
  }

  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("recharts_milestone_card"),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = SlateCard),
    border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
    ) {
      // Recharts Container Header
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
                .background(TitanCyan.copy(alpha = 0.2f), RoundedCornerShape(6.dp)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.AutoMirrored.Filled.ShowChart,
                contentDescription = null,
                tint = TitanCyan,
                modifier = Modifier.size(15.dp)
              )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "RECHARTS CAREER VELOCITY",
              style = MaterialTheme.typography.labelSmall,
              color = TitanCyan,
              fontWeight = FontWeight.Bold,
              letterSpacing = 1.sp
            )
          }
          Text(
            text = "Progress Towards Long-Term Milestones",
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimaryDark,
            fontWeight = FontWeight.Bold
          )
        }

        // Chart Mode Switcher Pills
        Row(
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(SlateElevated)
            .padding(2.dp),
          horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
          RechartsMode.values().forEach { mode ->
            val isSelected = selectedMode == mode
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(if (isSelected) TitanCyan.copy(alpha = 0.25f) else Color.Transparent)
                .clickable {
                  selectedMode = mode
                  selectedIndex = null
                  isInteracting = false
                }
                .padding(horizontal = 8.dp, vertical = 5.dp)
                .testTag("recharts_mode_${mode.name.lowercase()}"),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = mode.icon,
                contentDescription = mode.title,
                tint = if (isSelected) TitanCyan else TextMutedDark,
                modifier = Modifier.size(14.dp)
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Recharts Legend
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(8.dp)
              .background(TitanCyan, CircleShape)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "Completed Tasks",
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondaryDark,
            fontSize = 10.sp
          )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .width(12.dp)
              .height(2.dp)
              .background(TitanGold, RoundedCornerShape(1.dp))
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "Target Trajectory",
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondaryDark,
            fontSize = 10.sp
          )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(8.dp)
              .background(TitanEmerald, RoundedCornerShape(2.dp))
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "On-Track Milestone",
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondaryDark,
            fontSize = 10.sp
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Recharts Responsive Canvas Container
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(220.dp)
          .clip(RoundedCornerShape(12.dp))
          .background(ObsidianDark)
          .border(1.dp, SlateBorder.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
          .testTag("recharts_canvas_container")
      ) {
        val textMeasurer = rememberTextMeasurer()

        Canvas(
          modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .pointerInput(chartPoints, selectedMode) {
              detectTapGestures(
                onPress = { offset ->
                  val leftPadding = 42.dp.toPx()
                  val rightPadding = 20.dp.toPx()
                  val chartWidth = size.width - leftPadding - rightPadding
                  val step = chartWidth / (chartPoints.size - 1).coerceAtLeast(1)

                  val index = ((offset.x - leftPadding + step / 2) / step).toInt().coerceIn(0, chartPoints.size - 1)
                  selectedIndex = index
                  isInteracting = true
                  chartPoints[index].milestoneId?.let { onMilestoneSelected?.invoke(it) }
                }
              )
            }
            .pointerInput(chartPoints, selectedMode) {
              detectDragGestures(
                onDragStart = { offset ->
                  val leftPadding = 42.dp.toPx()
                  val rightPadding = 20.dp.toPx()
                  val chartWidth = size.width - leftPadding - rightPadding
                  val step = chartWidth / (chartPoints.size - 1).coerceAtLeast(1)
                  val index = ((offset.x - leftPadding + step / 2) / step).toInt().coerceIn(0, chartPoints.size - 1)
                  selectedIndex = index
                  isInteracting = true
                },
                onDragEnd = {
                  // Keep tooltip visible or auto dismiss after delay
                },
                onDragCancel = {},
                onDrag = { change, _ ->
                  change.consume()
                  val leftPadding = 42.dp.toPx()
                  val rightPadding = 20.dp.toPx()
                  val chartWidth = size.width - leftPadding - rightPadding
                  val step = chartWidth / (chartPoints.size - 1).coerceAtLeast(1)
                  val index = ((change.position.x - leftPadding + step / 2) / step).toInt().coerceIn(0, chartPoints.size - 1)
                  selectedIndex = index
                  isInteracting = true
                }
              )
            }
        ) {
          val width = size.width
          val height = size.height
          val leftPadding = 42.dp.toPx()
          val rightPadding = 20.dp.toPx()
          val topPadding = 24.dp.toPx()
          val bottomPadding = 32.dp.toPx()

          val chartWidth = width - leftPadding - rightPadding
          val chartHeight = height - topPadding - bottomPadding

          if (chartPoints.isEmpty()) return@Canvas

          // 1. Cartesian Grid (Horizontal lines at 0%, 25%, 50%, 75%, 100%)
          val ySteps = listOf(0, 25, 50, 75, 100)
          val dashEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)

          ySteps.forEach { percent ->
            val y = topPadding + chartHeight * (1f - percent / 100f)
            // Grid Line
            drawLine(
              color = SlateBorder.copy(alpha = 0.35f),
              start = Offset(leftPadding, y),
              end = Offset(width - rightPadding, y),
              strokeWidth = 1.dp.toPx(),
              pathEffect = dashEffect
            )

            // YAxis Labels
            drawText(
              textMeasurer = textMeasurer,
              text = "$percent%",
              style = TextStyle(
                color = TextMutedDark,
                fontSize = 9.sp,
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Medium
              ),
              topLeft = Offset(leftPadding - 32.dp.toPx(), y - 6.dp.toPx())
            )
          }

          val stepX = chartWidth / (chartPoints.size - 1).coerceAtLeast(1)

          // 2. XAxis Tick Labels & Vertical Guides
          chartPoints.forEachIndexed { i, pt ->
            val x = leftPadding + i * stepX
            // Subtle vertical guide
            drawLine(
              color = SlateBorder.copy(alpha = 0.15f),
              start = Offset(x, topPadding),
              end = Offset(x, topPadding + chartHeight),
              strokeWidth = 1.dp.toPx()
            )

            // X-Axis Text
            drawText(
              textMeasurer = textMeasurer,
              text = pt.label,
              style = TextStyle(
                color = if (selectedIndex == i) TitanCyan else TextMutedDark,
                fontSize = 10.sp,
                fontFamily = FontFamily.SansSerif,
                fontWeight = if (selectedIndex == i) FontWeight.Bold else FontWeight.Medium
              ),
              topLeft = Offset(x - 12.dp.toPx(), topPadding + chartHeight + 8.dp.toPx())
            )
          }

          // Render according to selected Recharts mode
          when (selectedMode) {
            RechartsMode.VELOCITY_AREA -> {
              // 3a. Benchmark Curve (Gold Dashed Monotone)
              val benchmarkPath = Path()
              chartPoints.forEachIndexed { i, pt ->
                val x = leftPadding + i * stepX
                val y = topPadding + chartHeight * (1f - (pt.benchmarkPercent * animatedProgress) / 100f)
                if (i == 0) {
                  benchmarkPath.moveTo(x, y)
                } else {
                  val prevX = leftPadding + (i - 1) * stepX
                  val prevY = topPadding + chartHeight * (1f - (chartPoints[i - 1].benchmarkPercent * animatedProgress) / 100f)
                  val cx1 = prevX + (x - prevX) / 2f
                  val cy1 = prevY
                  val cx2 = prevX + (x - prevX) / 2f
                  val cy2 = y
                  benchmarkPath.cubicTo(cx1, cy1, cx2, cy2, x, y)
                }
              }
              drawPath(
                path = benchmarkPath,
                color = TitanGold.copy(alpha = 0.45f),
                style = Stroke(width = 1.5.dp.toPx(), pathEffect = dashEffect)
              )

              // 3b. Actual Progress Area (Monotone Cubic Curve with Gradient Fill)
              val areaPath = Path()
              val strokePath = Path()

              val pointsCoords = mutableListOf<Offset>()
              chartPoints.forEachIndexed { i, pt ->
                val x = leftPadding + i * stepX
                val y = topPadding + chartHeight * (1f - (pt.progressPercent.coerceIn(0, 100) * animatedProgress) / 100f)
                pointsCoords.add(Offset(x, y))
              }

              if (pointsCoords.isNotEmpty()) {
                strokePath.moveTo(pointsCoords.first().x, pointsCoords.first().y)
                areaPath.moveTo(pointsCoords.first().x, topPadding + chartHeight)
                areaPath.lineTo(pointsCoords.first().x, pointsCoords.first().y)

                for (i in 1 until pointsCoords.size) {
                  val prev = pointsCoords[i - 1]
                  val curr = pointsCoords[i]
                  val cx1 = prev.x + (curr.x - prev.x) / 2f
                  val cy1 = prev.y
                  val cx2 = prev.x + (curr.x - prev.x) / 2f
                  val cy2 = curr.y

                  strokePath.cubicTo(cx1, cy1, cx2, cy2, curr.x, curr.y)
                  areaPath.cubicTo(cx1, cy1, cx2, cy2, curr.x, curr.y)
                }

                areaPath.lineTo(pointsCoords.last().x, topPadding + chartHeight)
                areaPath.close()

                // Draw Gradient Fill
                drawPath(
                  path = areaPath,
                  brush = Brush.verticalGradient(
                    colors = listOf(
                      TitanCyan.copy(alpha = 0.35f),
                      TitanIndigo.copy(alpha = 0.12f),
                      Color.Transparent
                    ),
                    startY = topPadding,
                    endY = topPadding + chartHeight
                  )
                )

                // Draw Vibrant Curve Line
                drawPath(
                  path = strokePath,
                  color = TitanCyan,
                  style = Stroke(
                    width = 3.dp.toPx(),
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                  )
                )

                // Draw Data Dots & Glowing Markers
                pointsCoords.forEachIndexed { i, offset ->
                  val isSelected = selectedIndex == i
                  val pt = chartPoints[i]

                  // Reference Gate Line for Milestone
                  if (i > 0) {
                    drawLine(
                      color = if (isSelected) TitanCyan else SlateBorder.copy(alpha = 0.4f),
                      start = Offset(offset.x, topPadding),
                      end = Offset(offset.x, topPadding + chartHeight),
                      strokeWidth = if (isSelected) 1.5.dp.toPx() else 1.dp.toPx(),
                      pathEffect = dashEffect
                    )
                  }

                  // Outer Glow Ring
                  drawCircle(
                    color = if (isSelected) TitanCyan.copy(alpha = 0.35f) else TitanCyan.copy(alpha = 0.18f),
                    radius = if (isSelected) 8.dp.toPx() else 5.dp.toPx(),
                    center = offset
                  )

                  // Solid Center Dot
                  drawCircle(
                    color = if (pt.progressPercent == 100) TitanEmerald else TitanCyan,
                    radius = if (isSelected) 5.dp.toPx() else 3.5.dp.toPx(),
                    center = offset
                  )

                  // White Core
                  drawCircle(
                    color = Color.White,
                    radius = if (isSelected) 2.5.dp.toPx() else 1.5.dp.toPx(),
                    center = offset
                  )
                }
              }
            }

            RechartsMode.TASK_BARS -> {
              // Stacked Bar Chart (Completed Tasks vs Remaining Tasks)
              val barWidth = 18.dp.toPx()
              val maxTasks = (chartPoints.maxOfOrNull { it.totalTasks } ?: 5).coerceAtLeast(1)

              chartPoints.forEachIndexed { i, pt ->
                val x = leftPadding + i * stepX - barWidth / 2
                val isSelected = selectedIndex == i
                val totalH = chartHeight * (pt.totalTasks.toFloat() / maxTasks) * animatedProgress
                val completedH = chartHeight * (pt.completedTasks.toFloat() / maxTasks) * animatedProgress
                val remainingH = (totalH - completedH).coerceAtLeast(0f)

                // Remaining (Upper) Bar
                if (remainingH > 0f) {
                  val topY = topPadding + chartHeight - totalH
                  drawRoundRect(
                    color = SlateElevated,
                    topLeft = Offset(x, topY),
                    size = Size(barWidth, remainingH),
                    cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                  )
                }

                // Completed (Lower) Bar
                if (completedH > 0f) {
                  val completedTopY = topPadding + chartHeight - completedH
                  drawRoundRect(
                    brush = Brush.verticalGradient(
                      colors = listOf(TitanCyan, TitanIndigo),
                      startY = completedTopY,
                      endY = topPadding + chartHeight
                    ),
                    topLeft = Offset(x, completedTopY),
                    size = Size(barWidth, completedH),
                    cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                  )
                }

                // Bar Active Highlight Outline
                if (isSelected) {
                  drawRoundRect(
                    color = Color.White,
                    topLeft = Offset(x - 2.dp.toPx(), topPadding + chartHeight - totalH - 2.dp.toPx()),
                    size = Size(barWidth + 4.dp.toPx(), totalH + 4.dp.toPx()),
                    cornerRadius = CornerRadius(5.dp.toPx(), 5.dp.toPx()),
                    style = Stroke(width = 1.5.dp.toPx())
                  )
                }

                // Numeric Task Counter Label on top of bar
                drawText(
                  textMeasurer = textMeasurer,
                  text = "${pt.completedTasks}/${pt.totalTasks}",
                  style = TextStyle(
                    color = if (isSelected) TitanCyan else TextSecondaryDark,
                    fontSize = 8.5.sp,
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Bold
                  ),
                  topLeft = Offset(x - 2.dp.toPx(), topPadding + chartHeight - totalH - 14.dp.toPx())
                )
              }
            }

            RechartsMode.BURN_UP -> {
              // Cumulative Points Burn-Up Curve
              var cumulativeCompleted = 0
              var cumulativeTotal = 0
              val totalTarget = chartPoints.sumOf { it.totalTasks }.coerceAtLeast(1)

              val burnUpPath = Path()
              val targetPath = Path()

              chartPoints.forEachIndexed { i, pt ->
                cumulativeCompleted += pt.completedTasks
                cumulativeTotal += pt.totalTasks

                val x = leftPadding + i * stepX
                val yActual = topPadding + chartHeight * (1f - (cumulativeCompleted.toFloat() / totalTarget) * animatedProgress)
                val yTarget = topPadding + chartHeight * (1f - (cumulativeTotal.toFloat() / totalTarget) * animatedProgress)

                if (i == 0) {
                  burnUpPath.moveTo(x, yActual)
                  targetPath.moveTo(x, yTarget)
                } else {
                  burnUpPath.lineTo(x, yActual)
                  targetPath.lineTo(x, yTarget)
                }

                // Reference dots
                drawCircle(
                  color = TitanCyan,
                  radius = 4.dp.toPx(),
                  center = Offset(x, yActual)
                )
              }

              // Draw Target Line
              drawPath(
                path = targetPath,
                color = SlateBorder,
                style = Stroke(width = 2.dp.toPx(), pathEffect = dashEffect)
              )

              // Draw Actual Cumulative Burn-Up
              drawPath(
                path = burnUpPath,
                color = TitanCyan,
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
              )
            }
          }

          // 4. Interactive Vertical Scrub Cursor
          selectedIndex?.let { idx ->
            if (idx in chartPoints.indices) {
              val cursorX = leftPadding + idx * stepX
              drawLine(
                color = TitanCyan.copy(alpha = 0.8f),
                start = Offset(cursorX, topPadding),
                end = Offset(cursorX, topPadding + chartHeight),
                strokeWidth = 1.5.dp.toPx(),
                pathEffect = dashEffect
              )
            }
          }
        }

        // Floating Recharts Tooltip Overlay Card
        if (selectedIndex != null && selectedIndex in chartPoints.indices) {
          val idx = selectedIndex!!
          val pt = chartPoints.getOrNull(idx)
          if (pt != null) {
            Box(
              modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 8.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(SlateElevated.copy(alpha = 0.95f))
                .border(1.dp, TitanCyan.copy(alpha = 0.7f), RoundedCornerShape(10.dp))
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .testTag("recharts_tooltip_card")
            ) {
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.Center
                ) {
                  Box(
                    modifier = Modifier
                      .clip(RoundedCornerShape(4.dp))
                      .background(TitanCyan.copy(alpha = 0.2f))
                      .padding(horizontal = 6.dp, vertical = 2.dp)
                  ) {
                    Text(
                      text = "${pt.label} • ${pt.targetQuarter}",
                      style = MaterialTheme.typography.labelSmall,
                      color = TitanCyan,
                      fontWeight = FontWeight.Bold,
                      fontSize = 10.sp
                    )
                  }
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = "${pt.progressPercent}% PROGRESS",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (pt.progressPercent >= 50) TitanEmerald else TitanGold,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 10.sp
                  )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                  text = pt.milestoneTitle,
                  style = MaterialTheme.typography.bodySmall,
                  color = TextPrimaryDark,
                  fontWeight = FontWeight.SemiBold,
                  fontSize = 11.sp,
                  maxLines = 1
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                  text = "Tasks: ${pt.completedTasks}/${pt.totalTasks} completed • Target: ${pt.targetMetric}",
                  style = MaterialTheme.typography.labelSmall,
                  color = TextSecondaryDark,
                  fontSize = 10.sp,
                  maxLines = 1
                )
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Recharts Footer Helper
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Tap or scrub timeline to inspect milestone readiness & task weights",
          style = MaterialTheme.typography.labelSmall,
          color = TextMutedDark,
          fontSize = 10.sp
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.AutoAwesome,
            contentDescription = null,
            tint = TitanGold,
            modifier = Modifier.size(12.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "Adaptive Recharts Engine",
            style = MaterialTheme.typography.labelSmall,
            color = TitanGold,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold
          )
        }
      }
    }
  }
}
