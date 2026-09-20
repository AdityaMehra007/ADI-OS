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
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

/**
 * Data point representing a day or timeframe of automation agent metrics
 */
data class AutomationKpiDataPoint(
  val label: String,
  val fullDate: String,
  val applicationsSent: Int,
  val responsesReceived: Int,
  val responseRatePercent: Float,
  val interviewsGenerated: Int,
  val agentMatchQuality: Int
)

enum class KpiTimeRange(val label: String) {
  LAST_7_DAYS("Last 7D"),
  LAST_30_DAYS("Last 30D"),
  ALL_TIME("All-Time")
}

/**
 * Recharts-style Interactive KPI Visualizer for Executive Automation Agents
 * Tracks Applications Sent (Bar/Histogram) vs Response Rate (Area/Line Curve)
 */
@Composable
fun AutomationKpiSummaryChart(
  modifier: Modifier = Modifier,
  initialTimeRange: KpiTimeRange = KpiTimeRange.LAST_7_DAYS
) {
  var selectedTimeRange by remember { mutableStateOf(initialTimeRange) }
  var selectedIndex by remember { mutableIntStateOf(6) } // Default to latest point
  var showApplicationsSeries by remember { mutableStateOf(true) }
  var showResponseRateSeries by remember { mutableStateOf(true) }
  var animateChart by remember { mutableStateOf(false) }

  LaunchedEffect(selectedTimeRange) {
    animateChart = false
    kotlinx.coroutines.delay(50)
    animateChart = true
  }

  val chartProgress by animateFloatAsState(
    targetValue = if (animateChart) 1f else 0f,
    animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
    label = "kpiChartProgress"
  )

  // Dataset based on time range
  val dataPoints = remember(selectedTimeRange) {
    when (selectedTimeRange) {
      KpiTimeRange.LAST_7_DAYS -> listOf(
        AutomationKpiDataPoint("Mon", "Sep 4", 2, 0, 0.0f, 0, 88),
        AutomationKpiDataPoint("Tue", "Sep 5", 4, 1, 25.0f, 0, 91),
        AutomationKpiDataPoint("Wed", "Sep 6", 3, 1, 33.3f, 1, 94),
        AutomationKpiDataPoint("Thu", "Sep 7", 5, 2, 40.0f, 1, 95),
        AutomationKpiDataPoint("Fri", "Sep 8", 6, 2, 33.3f, 1, 96),
        AutomationKpiDataPoint("Sat", "Sep 9", 3, 1, 33.3f, 0, 92),
        AutomationKpiDataPoint("Sun", "Sep 10 (Today)", 5, 2, 40.0f, 1, 97)
      )
      KpiTimeRange.LAST_30_DAYS -> listOf(
        AutomationKpiDataPoint("W1", "Aug 14-20", 12, 2, 16.6f, 1, 84),
        AutomationKpiDataPoint("W2", "Aug 21-27", 18, 4, 22.2f, 2, 89),
        AutomationKpiDataPoint("W3", "Aug 28-Sep 3", 22, 6, 27.2f, 3, 93),
        AutomationKpiDataPoint("W4", "Sep 4-10", 28, 9, 32.1f, 4, 96)
      )
      KpiTimeRange.ALL_TIME -> listOf(
        AutomationKpiDataPoint("Jul", "July 2026", 15, 2, 13.3f, 1, 80),
        AutomationKpiDataPoint("Aug", "August 2026", 48, 12, 25.0f, 5, 90),
        AutomationKpiDataPoint("Sep", "September 2026", 28, 9, 32.1f, 4, 96)
      )
    }
  }

  // Ensure index is within range
  val activeIndex = selectedIndex.coerceIn(0, dataPoints.lastIndex)
  val activePoint = dataPoints[activeIndex]

  val totalApplicationsSent = remember(dataPoints) { dataPoints.sumOf { it.applicationsSent } }
  val totalResponses = remember(dataPoints) { dataPoints.sumOf { it.responsesReceived } }
  val averageResponseRate = remember(dataPoints) {
    if (totalApplicationsSent > 0) (totalResponses.toFloat() / totalApplicationsSent * 100f) else 0f
  }
  val totalInterviews = remember(dataPoints) { dataPoints.sumOf { it.interviewsGenerated } }

  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("automation_kpi_summary_chart"),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = SlateCard),
    border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      // Header & Mode Controls
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(32.dp)
              .clip(CircleShape)
              .background(TitanCyan.copy(alpha = 0.15f))
              .border(1.dp, TitanCyan.copy(alpha = 0.4f), CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ShowChart,
              contentDescription = null,
              tint = TitanCyan,
              modifier = Modifier.size(18.dp)
            )
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "AUTOMATION EFFECTIVENESS",
                style = MaterialTheme.typography.labelSmall,
                color = TitanCyan,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.1.sp,
                fontSize = 10.sp
              )
              Spacer(modifier = Modifier.width(6.dp))
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(4.dp))
                  .background(TitanEmerald.copy(alpha = 0.15f))
                  .padding(horizontal = 4.dp, vertical = 1.dp)
              ) {
                Text(
                  text = "RECHARTS KPI",
                  color = TitanEmerald,
                  fontSize = 7.5.sp,
                  fontWeight = FontWeight.Black
                )
              }
            }
            Text(
              text = "Applications Sent vs. Response Rate",
              style = MaterialTheme.typography.titleMedium,
              color = TextPrimaryDark,
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp
            )
          }
        }

        // Time Range Pills
        Row(
          horizontalArrangement = Arrangement.spacedBy(4.dp),
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(SlateElevated)
            .padding(2.dp)
        ) {
          KpiTimeRange.entries.forEach { range ->
            val isSelected = selectedTimeRange == range
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(if (isSelected) TitanCyan else Color.Transparent)
                .clickable {
                  selectedTimeRange = range
                  selectedIndex = when (range) {
                    KpiTimeRange.LAST_7_DAYS -> 6
                    KpiTimeRange.LAST_30_DAYS -> 3
                    KpiTimeRange.ALL_TIME -> 2
                  }
                }
                .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
              Text(
                text = range.label,
                style = MaterialTheme.typography.labelSmall,
                color = if (isSelected) ObsidianDark else TextMutedDark,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                fontSize = 9.sp
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // KPI Summary Metrics Bar
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        KpiStatPill(
          label = "Apps Sent",
          value = "$totalApplicationsSent",
          change = "+14 this wk",
          tintColor = TitanCyan,
          modifier = Modifier.weight(1f)
        )
        KpiStatPill(
          label = "Avg Response",
          value = String.format("%.1f%%", averageResponseRate),
          change = "+8.4% vs baseline",
          tintColor = TitanGold,
          modifier = Modifier.weight(1f)
        )
        KpiStatPill(
          label = "Interviews",
          value = "$totalInterviews",
          change = "2 S+ Tier",
          tintColor = TitanEmerald,
          modifier = Modifier.weight(1f)
        )
        KpiStatPill(
          label = "Agent Quality",
          value = "94%",
          change = "High Match",
          tintColor = TitanIndigo,
          modifier = Modifier.weight(1f)
        )
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Series Toggle & Legend
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          horizontalArrangement = Arrangement.spacedBy(14.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Bar series toggle
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
              .clickable { showApplicationsSeries = !showApplicationsSeries }
              .padding(vertical = 2.dp)
          ) {
            Box(
              modifier = Modifier
                .size(10.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(if (showApplicationsSeries) TitanCyan else TitanCyan.copy(alpha = 0.2f))
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Applications Sent (Bars)",
              style = MaterialTheme.typography.labelSmall,
              color = if (showApplicationsSeries) TextPrimaryDark else TextMutedDark,
              fontSize = 10.sp
            )
          }

          // Line series toggle
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
              .clickable { showResponseRateSeries = !showResponseRateSeries }
              .padding(vertical = 2.dp)
          ) {
            Box(
              modifier = Modifier
                .width(12.dp)
                .height(3.dp)
                .background(if (showResponseRateSeries) TitanGold else TitanGold.copy(alpha = 0.2f))
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Response Rate % (Line)",
              style = MaterialTheme.typography.labelSmall,
              color = if (showResponseRateSeries) TextPrimaryDark else TextMutedDark,
              fontSize = 10.sp
            )
          }
        }

        Text(
          text = "Tap bar to inspect",
          style = MaterialTheme.typography.labelSmall,
          color = TextMutedDark,
          fontSize = 9.sp
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Interactive Recharts Canvas Chart
      val maxApps = remember(dataPoints) { (dataPoints.maxOfOrNull { it.applicationsSent } ?: 6).coerceAtLeast(6) }
      val maxRate = 50f // 50% scale ceiling

      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(170.dp)
          .clip(RoundedCornerShape(8.dp))
          .background(SlateElevated)
          .pointerInput(dataPoints) {
            detectTapGestures { offset ->
              val pointWidth = size.width / dataPoints.size
              val tappedIndex = (offset.x / pointWidth).toInt().coerceIn(0, dataPoints.lastIndex)
              selectedIndex = tappedIndex
            }
          }
      ) {
        Canvas(modifier = Modifier.matchParentSize()) {
          val canvasWidth = size.width
          val canvasHeight = size.height
          val paddingBottom = 26.dp.toPx()
          val paddingTop = 16.dp.toPx()
          val chartHeight = canvasHeight - paddingBottom - paddingTop

          val count = dataPoints.size
          val segmentWidth = canvasWidth / count

          // 1. Horizontal Reference Grid Lines
          val gridSteps = 4
          for (i in 0..gridSteps) {
            val y = paddingTop + (chartHeight * (i.toFloat() / gridSteps))
            drawLine(
              color = SlateBorder.copy(alpha = 0.5f),
              start = Offset(0f, y),
              end = Offset(canvasWidth, y),
              strokeWidth = 1f
            )
          }

          // 2. Render Applications Sent (Bars)
          if (showApplicationsSeries) {
            val barWidth = (segmentWidth * 0.42f).coerceAtMost(28.dp.toPx())
            dataPoints.forEachIndexed { index, point ->
              val centerX = (index * segmentWidth) + (segmentWidth / 2f)
              val normalizedApps = (point.applicationsSent.toFloat() / maxApps) * chartProgress
              val barHeight = (chartHeight * normalizedApps).coerceAtLeast(2f)
              val barTop = paddingTop + chartHeight - barHeight

              val isSelected = index == activeIndex
              val barColor = if (isSelected) TitanCyan else TitanCyan.copy(alpha = 0.65f)

              drawRoundRect(
                color = barColor,
                topLeft = Offset(centerX - (barWidth / 2f), barTop),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
              )

              // Selected Bar indicator ring
              if (isSelected) {
                drawRoundRect(
                  color = TitanCyan,
                  topLeft = Offset(centerX - (barWidth / 2f) - 1.5.dp.toPx(), barTop - 1.5.dp.toPx()),
                  size = Size(barWidth + 3.dp.toPx(), barHeight + 3.dp.toPx()),
                  cornerRadius = CornerRadius(5.dp.toPx(), 5.dp.toPx()),
                  style = Stroke(width = 1.5.dp.toPx())
                )
              }
            }
          }

          // 3. Render Response Rate (Line + Smooth Gradient Fill)
          if (showResponseRateSeries && count > 1) {
            val path = Path()
            val fillPath = Path()

            val points = dataPoints.mapIndexed { index, point ->
              val x = (index * segmentWidth) + (segmentWidth / 2f)
              val normalizedRate = (point.responseRatePercent / maxRate) * chartProgress
              val y = paddingTop + chartHeight - (chartHeight * normalizedRate)
              Offset(x, y)
            }

            path.moveTo(points[0].x, points[0].y)
            fillPath.moveTo(points[0].x, points[0].y)

            // Cubic Bezier curve calculation
            for (i in 0 until points.size - 1) {
              val p0 = points[i]
              val p1 = points[i + 1]
              val controlX1 = (p0.x + p1.x) / 2f
              val controlY1 = p0.y
              val controlX2 = (p0.x + p1.x) / 2f
              val controlY2 = p1.y
              path.cubicTo(controlX1, controlY1, controlX2, controlY2, p1.x, p1.y)
              fillPath.cubicTo(controlX1, controlY1, controlX2, controlY2, p1.x, p1.y)
            }

            // Close fill path to bottom
            fillPath.lineTo(points.last().x, paddingTop + chartHeight)
            fillPath.lineTo(points.first().x, paddingTop + chartHeight)
            fillPath.close()

            // Draw translucent gradient fill
            drawPath(
              path = fillPath,
              brush = Brush.verticalGradient(
                colors = listOf(
                  TitanGold.copy(alpha = 0.22f),
                  TitanGold.copy(alpha = 0.02f)
                ),
                startY = paddingTop,
                endY = paddingTop + chartHeight
              )
            )

            // Draw line stroke
            drawPath(
              path = path,
              color = TitanGold,
              style = Stroke(
                width = 2.5.dp.toPx(),
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
              )
            )

            // Draw data points nodes
            points.forEachIndexed { index, pt ->
              val isSelected = index == activeIndex
              drawCircle(
                color = if (isSelected) TitanGold else ObsidianDark,
                radius = if (isSelected) 5.dp.toPx() else 3.5.dp.toPx(),
                center = pt
              )
              drawCircle(
                color = TitanGold,
                radius = if (isSelected) 3.dp.toPx() else 2.dp.toPx(),
                center = pt
              )
            }
          }
        }

        // X-Axis Text Labels
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.BottomCenter)
            .padding(bottom = 6.dp),
          horizontalArrangement = Arrangement.SpaceAround
        ) {
          dataPoints.forEachIndexed { index, point ->
            val isSelected = index == activeIndex
            Text(
              text = point.label,
              style = MaterialTheme.typography.labelSmall,
              color = if (isSelected) TitanCyan else TextMutedDark,
              fontWeight = if (isSelected) FontWeight.Black else FontWeight.Normal,
              fontSize = 9.sp
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Dynamic Interactive Scrubber Tooltip Card
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(8.dp))
          .background(SlateElevated)
          .border(1.dp, TitanCyan.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
          .padding(10.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.BarChart,
                contentDescription = null,
                tint = TitanCyan,
                modifier = Modifier.size(13.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = activePoint.fullDate.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = TitanCyan,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp
              )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              Text(
                text = "${activePoint.applicationsSent} Apps Sent",
                style = MaterialTheme.typography.bodySmall,
                color = TextPrimaryDark,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
              )
              Text("•", color = TextMutedDark, fontSize = 11.sp)
              Text(
                text = "${activePoint.responsesReceived} Responses (${String.format("%.1f%%", activePoint.responseRatePercent)})",
                style = MaterialTheme.typography.bodySmall,
                color = TitanGold,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
              )
            }
          }

          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(TitanEmerald.copy(alpha = 0.15f))
              .padding(horizontal = 8.dp, vertical = 4.dp)
          ) {
            Text(
              text = "${activePoint.interviewsGenerated} Interview Scheduled",
              style = MaterialTheme.typography.labelSmall,
              color = TitanEmerald,
              fontWeight = FontWeight.Bold,
              fontSize = 9.5.sp
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Strategic AI Performance Insight
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(6.dp))
          .background(TitanCyan.copy(alpha = 0.08f))
          .border(1.dp, TitanCyan.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
          .padding(8.dp),
        verticalAlignment = Alignment.Top
      ) {
        Icon(
          imageVector = Icons.Default.AutoAwesome,
          contentDescription = null,
          tint = TitanCyan,
          modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "Titan autonomous outbound matching yield: Tailored application pipelines currently generate a 32.1% response rate (+18.4% above market baseline of 13.7%), saving ~18.2 manual hours this week.",
          style = MaterialTheme.typography.bodySmall,
          color = TextSecondaryDark,
          fontSize = 10.5.sp,
          lineHeight = 14.sp
        )
      }
    }
  }
}

@Composable
private fun KpiStatPill(
  label: String,
  value: String,
  change: String,
  tintColor: Color,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(8.dp))
      .background(SlateElevated)
      .border(1.dp, SlateBorder, RoundedCornerShape(8.dp))
      .padding(8.dp)
  ) {
    Column {
      Text(
        text = label.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        color = TextMutedDark,
        fontSize = 8.5.sp,
        fontWeight = FontWeight.SemiBold
      )
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = value,
        style = MaterialTheme.typography.titleMedium,
        color = tintColor,
        fontWeight = FontWeight.Black,
        fontSize = 14.sp
      )
      Spacer(modifier = Modifier.height(1.dp))
      Text(
        text = change,
        style = MaterialTheme.typography.labelSmall,
        color = TextSecondaryDark,
        fontSize = 7.5.sp
      )
    }
  }
}
