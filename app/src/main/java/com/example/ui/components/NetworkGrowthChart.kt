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
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.automirrored.filled.TrendingUp
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Fill
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

enum class NetworkTimeRange(val label: String, val title: String) {
  ONE_MONTH("1M", "Past 30 Days"),
  THREE_MONTHS("3M", "Past Quarter"),
  SIX_MONTHS("6M", "Past 6 Months"),
  ONE_YEAR("1Y", "Past 12 Months"),
  ALL_TIME("ALL", "All-Time Velocity")
}

data class NetworkDataPoint(
  val periodLabel: String,
  val totalConnections: Int,
  val executiveLeads: Int,
  val activeOutreachReplies: Int,
  val growthRatePercentage: Float,
  val milestoneEvent: String? = null
)

@Composable
fun NetworkGrowthChart(
  modifier: Modifier = Modifier,
  initialTimeRange: NetworkTimeRange = NetworkTimeRange.SIX_MONTHS
) {
  var selectedRange by remember { mutableStateOf(initialTimeRange) }
  var selectedIndex by remember { mutableStateOf<Int?>(null) }
  var isInteracting by remember { mutableStateOf(false) }

  val rawData = remember(selectedRange) {
    when (selectedRange) {
      NetworkTimeRange.ONE_MONTH -> listOf(
        NetworkDataPoint("W1", 310, 48, 14, 4.2f, "Launched Zepto Inbound Outreach"),
        NetworkDataPoint("W2", 335, 56, 19, 8.0f),
        NetworkDataPoint("W3", 370, 68, 25, 10.4f, "SQL & AI Agent Portfolio Live"),
        NetworkDataPoint("W4", 412, 84, 34, 11.3f, "Bain & McKinsey Referrals Confirmed")
      )
      NetworkTimeRange.THREE_MONTHS -> listOf(
        NetworkDataPoint("Jun", 240, 32, 8, 5.0f, "BBA Degree Completed"),
        NetworkDataPoint("Mid-Jun", 275, 41, 12, 14.5f),
        NetworkDataPoint("Jul", 320, 52, 18, 16.3f, "Automated Talent Pipeline Setup"),
        NetworkDataPoint("Mid-Jul", 365, 65, 26, 14.0f),
        NetworkDataPoint("Aug", 412, 84, 34, 12.8f, "Executive Mentorship Program")
      )
      NetworkTimeRange.SIX_MONTHS -> listOf(
        NetworkDataPoint("Mar", 160, 18, 4, 3.2f, "Target List Created (Bengaluru Tiers)"),
        NetworkDataPoint("Apr", 195, 24, 6, 21.8f),
        NetworkDataPoint("May", 230, 30, 9, 17.9f, "Consulting Casebook Finalized"),
        NetworkDataPoint("Jun", 280, 44, 15, 21.7f),
        NetworkDataPoint("Jul", 345, 62, 24, 23.2f, "Autonomous Job Agent Deployed"),
        NetworkDataPoint("Aug", 412, 84, 34, 19.4f, "Top 1% Career Capital Reached")
      )
      NetworkTimeRange.ONE_YEAR -> listOf(
        NetworkDataPoint("Sep '25", 90, 8, 2, 2.0f, "Career Strategy Initialized"),
        NetworkDataPoint("Nov '25", 125, 14, 3, 38.8f),
        NetworkDataPoint("Jan '26", 160, 20, 5, 28.0f, "Kaggle & SQL Certifications"),
        NetworkDataPoint("Mar '26", 210, 32, 9, 31.2f),
        NetworkDataPoint("May '26", 290, 52, 18, 38.0f, "Bengaluru Ops Network Onboarding"),
        NetworkDataPoint("Aug '26", 412, 84, 34, 42.0f, "Full Multi-Vector Autonomous Matching")
      )
      NetworkTimeRange.ALL_TIME -> listOf(
        NetworkDataPoint("Q1 '25", 45, 3, 1, 0.0f, "Foundation Era"),
        NetworkDataPoint("Q3 '25", 110, 12, 3, 144.4f),
        NetworkDataPoint("Q1 '26", 185, 26, 7, 68.1f, "Applied AI & Ops Teardowns"),
        NetworkDataPoint("Q3 '26", 412, 84, 34, 122.7f, "Executive Command Scale")
      )
    }
  }

  // Animate transition on range changes
  var animTrigger by remember { mutableStateOf(0f) }
  LaunchedEffect(selectedRange) {
    animTrigger = 0f
    selectedIndex = rawData.lastIndex
    animTrigger = 1f
  }

  val animatedProgress by animateFloatAsState(
    targetValue = animTrigger,
    animationSpec = tween(durationMillis = 650, easing = FastOutSlowInEasing),
    label = "network_chart_anim"
  )

  val activePoint = selectedIndex?.let { rawData.getOrNull(it) } ?: rawData.last()

  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("network_growth_trends_chart"),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = SlateCard),
    border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp)
    ) {
      // 1. Header with Title, Live Badge, and Metrics
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
              imageVector = Icons.Default.Timeline,
              contentDescription = null,
              tint = TitanCyan,
              modifier = Modifier.size(18.dp)
            )
          }

          Spacer(modifier = Modifier.width(10.dp))

          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "NETWORK GROWTH TRENDS",
                style = MaterialTheme.typography.labelSmall,
                color = TitanCyan,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                fontSize = 10.sp
              )
              Spacer(modifier = Modifier.width(6.dp))
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(3.dp))
                  .background(TitanEmerald.copy(alpha = 0.15f))
                  .padding(horizontal = 4.dp, vertical = 1.dp)
              ) {
                Text(
                  text = "+${activePoint.growthRatePercentage}% MoM",
                  color = TitanEmerald,
                  fontSize = 8.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }
            Text(
              text = "Verified Connections & Executive Inbounds Over Time",
              style = MaterialTheme.typography.bodySmall,
              color = TextSecondaryDark,
              fontSize = 10.sp
            )
          }
        }

        // Recharts-inspired Live Pill
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(SlateElevated)
            .border(1.dp, SlateBorder, RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(TitanEmerald)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "RECHARTS ENGINE",
              style = MaterialTheme.typography.labelSmall,
              color = TextPrimaryDark,
              fontSize = 8.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // 2. High-Level KPI Summary Tiles
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        NetworkKpiTile(
          label = "TOTAL NETWORK",
          value = "${activePoint.totalConnections}",
          subtext = "Verified contacts",
          color = TitanCyan,
          modifier = Modifier.weight(1f)
        )
        NetworkKpiTile(
          label = "S-TIER DECISION MAKERS",
          value = "${activePoint.executiveLeads}",
          subtext = "Founders & Partners",
          color = TitanGold,
          modifier = Modifier.weight(1f)
        )
        NetworkKpiTile(
          label = "OUTREACH ENGAGED",
          value = "${activePoint.activeOutreachReplies}",
          subtext = "42% response rate",
          color = TitanEmerald,
          modifier = Modifier.weight(1f)
        )
      }

      Spacer(modifier = Modifier.height(12.dp))

      // 3. Time Range Granularity Selector Chips
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          NetworkTimeRange.values().forEach { range ->
            val isSelected = selectedRange == range
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(if (isSelected) TitanCyan else SlateElevated)
                .border(
                  width = 1.dp,
                  color = if (isSelected) TitanCyan else SlateBorder,
                  shape = RoundedCornerShape(6.dp)
                )
                .clickable {
                  selectedRange = range
                  selectedIndex = null
                }
                .padding(horizontal = 9.dp, vertical = 4.dp)
                .testTag("network_filter_${range.label.lowercase()}")
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

        Text(
          text = selectedRange.title,
          style = MaterialTheme.typography.labelSmall,
          color = TextMutedDark,
          fontSize = 9.sp
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      // 4. Recharts-style Interactive Gradient Area & Line Canvas
      val maxConnections = (rawData.maxOfOrNull { it.totalConnections } ?: 500) * 1.15f
      val minConnections = (rawData.minOfOrNull { it.totalConnections } ?: 0) * 0.75f

      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(180.dp)
          .clip(RoundedCornerShape(10.dp))
          .background(SlateElevated)
          .border(1.dp, SlateBorder.copy(alpha = 0.6f), RoundedCornerShape(10.dp))
          .pointerInput(rawData) {
            detectTapGestures { offset ->
              val count = rawData.size
              if (count > 1) {
                val stepX = size.width / (count - 1)
                val tappedIdx = ((offset.x + stepX / 2f) / stepX).toInt().coerceIn(0, count - 1)
                selectedIndex = tappedIdx
                isInteracting = true
              }
            }
          }
          .pointerInput(rawData) {
            detectDragGestures(
              onDragStart = { isInteracting = true },
              onDragEnd = { isInteracting = false },
              onDragCancel = { isInteracting = false }
            ) { change, _ ->
              val count = rawData.size
              if (count > 1) {
                val stepX = size.width / (count - 1)
                val draggedIdx = (change.position.x / stepX).toInt().coerceIn(0, count - 1)
                selectedIndex = draggedIdx
              }
            }
          }
          .testTag("network_recharts_canvas")
      ) {
        Canvas(modifier = Modifier.matchParentSize().padding(horizontal = 16.dp, vertical = 20.dp)) {
          val canvasWidth = size.width
          val canvasHeight = size.height
          val pointCount = rawData.size

          if (pointCount < 2) return@Canvas

          val stepX = canvasWidth / (pointCount - 1)

          // Draw Horizontal Grid Lines (Recharts style CartesianGrid)
          val gridLines = 4
          for (i in 0..gridLines) {
            val y = canvasHeight * (i.toFloat() / gridLines)
            drawLine(
              color = SlateBorder.copy(alpha = 0.35f),
              start = Offset(0f, y),
              end = Offset(canvasWidth, y),
              strokeWidth = 1.dp.toPx()
            )
          }

          // Calculate Coordinates for Main Curve (Total Network)
          val points = rawData.mapIndexed { idx, item ->
            val x = idx * stepX
            val normalizedY = ((item.totalConnections - minConnections) / (maxConnections - minConnections)).coerceIn(0f, 1f)
            val y = canvasHeight - (normalizedY * canvasHeight * animatedProgress)
            Offset(x, y)
          }

          // Calculate Coordinates for Secondary Series (Executive Leads)
          val execPoints = rawData.mapIndexed { idx, item ->
            val x = idx * stepX
            val normalizedY = ((item.executiveLeads.toFloat() * 3.5f - minConnections) / (maxConnections - minConnections)).coerceIn(0f, 1f)
            val y = canvasHeight - (normalizedY * canvasHeight * animatedProgress)
            Offset(x, y)
          }

          // 1. Primary Area Gradient Path (Total Network Area)
          val areaPath = Path().apply {
            moveTo(points.first().x, canvasHeight)
            lineTo(points.first().x, points.first().y)
            for (i in 0 until points.size - 1) {
              val p0 = points[i]
              val p1 = points[i + 1]
              val midX = (p0.x + p1.x) / 2f
              cubicTo(midX, p0.y, midX, p1.y, p1.x, p1.y)
            }
            lineTo(points.last().x, canvasHeight)
            close()
          }

          drawPath(
            path = areaPath,
            brush = Brush.verticalGradient(
              colors = listOf(
                TitanCyan.copy(alpha = 0.35f),
                TitanIndigo.copy(alpha = 0.15f),
                Color.Transparent
              ),
              startY = 0f,
              endY = canvasHeight
            )
          )

          // 2. Secondary Area Gradient Path (Exec Leads)
          val execAreaPath = Path().apply {
            moveTo(execPoints.first().x, canvasHeight)
            lineTo(execPoints.first().x, execPoints.first().y)
            for (i in 0 until execPoints.size - 1) {
              val p0 = execPoints[i]
              val p1 = execPoints[i + 1]
              val midX = (p0.x + p1.x) / 2f
              cubicTo(midX, p0.y, midX, p1.y, p1.x, p1.y)
            }
            lineTo(execPoints.last().x, canvasHeight)
            close()
          }

          drawPath(
            path = execAreaPath,
            brush = Brush.verticalGradient(
              colors = listOf(
                TitanGold.copy(alpha = 0.25f),
                Color.Transparent
              ),
              startY = 0f,
              endY = canvasHeight
            )
          )

          // 3. Primary Smooth Bezier Line (Cyan)
          val linePath = Path().apply {
            moveTo(points.first().x, points.first().y)
            for (i in 0 until points.size - 1) {
              val p0 = points[i]
              val p1 = points[i + 1]
              val midX = (p0.x + p1.x) / 2f
              cubicTo(midX, p0.y, midX, p1.y, p1.x, p1.y)
            }
          }

          drawPath(
            path = linePath,
            color = TitanCyan,
            style = Stroke(
              width = 3.dp.toPx(),
              cap = StrokeCap.Round,
              join = StrokeJoin.Round
            )
          )

          // 4. Secondary Smooth Bezier Line (Gold - Executive series)
          val execLinePath = Path().apply {
            moveTo(execPoints.first().x, execPoints.first().y)
            for (i in 0 until execPoints.size - 1) {
              val p0 = execPoints[i]
              val p1 = execPoints[i + 1]
              val midX = (p0.x + p1.x) / 2f
              cubicTo(midX, p0.y, midX, p1.y, p1.x, p1.y)
            }
          }

          drawPath(
            path = execLinePath,
            color = TitanGold,
            style = Stroke(
              width = 2.dp.toPx(),
              cap = StrokeCap.Round,
              join = StrokeJoin.Round
            )
          )

          // 5. Draw Interactive Scrubber Line & Highlight Dots
          selectedIndex?.let { idx ->
            if (idx in points.indices) {
              val selPoint = points[idx]
              val execSelPoint = execPoints[idx]

              // Vertical Cursor Reference Line (Recharts style Tooltip Cursor)
              drawLine(
                color = TitanCyan.copy(alpha = 0.6f),
                start = Offset(selPoint.x, 0f),
                end = Offset(selPoint.x, canvasHeight),
                strokeWidth = 1.5.dp.toPx()
              )

              // Glow ring for primary dot
              drawCircle(
                color = TitanCyan.copy(alpha = 0.3f),
                radius = 9.dp.toPx(),
                center = selPoint
              )
              drawCircle(
                color = TitanCyan,
                radius = 5.dp.toPx(),
                center = selPoint
              )
              drawCircle(
                color = ObsidianDark,
                radius = 2.5.dp.toPx(),
                center = selPoint
              )

              // Exec dot
              drawCircle(
                color = TitanGold,
                radius = 4.dp.toPx(),
                center = execSelPoint
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // 5. X-Axis Time Labels
      Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        rawData.forEachIndexed { idx, point ->
          val isSelected = selectedIndex == idx
          Text(
            text = point.periodLabel,
            style = MaterialTheme.typography.labelSmall,
            color = if (isSelected) TitanCyan else TextMutedDark,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            fontSize = 9.sp
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // 6. Interactive Scrubber Tooltip & Contextual Dossier Card
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(10.dp))
          .background(SlateElevated)
          .border(1.dp, TitanCyan.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
          .padding(12.dp)
          .testTag("network_chart_tooltip_card")
      ) {
        Column {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.AutoMirrored.Filled.ShowChart, contentDescription = null, tint = TitanCyan, modifier = Modifier.size(14.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "PERIOD: ${activePoint.periodLabel.uppercase()}",
                style = MaterialTheme.typography.labelSmall,
                color = TitanCyan,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp
              )
            }

            Text(
              text = "${activePoint.totalConnections} Verified Contacts",
              style = MaterialTheme.typography.labelMedium,
              color = TextPrimaryDark,
              fontWeight = FontWeight.Bold,
              fontSize = 11.sp
            )
          }

          Spacer(modifier = Modifier.height(6.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(TitanCyan))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Total Connections: ${activePoint.totalConnections}", color = TextSecondaryDark, fontSize = 10.sp)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(TitanGold))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Exec Leads: ${activePoint.executiveLeads}", color = TextSecondaryDark, fontSize = 10.sp)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(TitanEmerald))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Replies: ${activePoint.activeOutreachReplies}", color = TextSecondaryDark, fontSize = 10.sp)
            }
          }

          // Milestone event if available
          activePoint.milestoneEvent?.let { event ->
            Spacer(modifier = Modifier.height(8.dp))
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(6.dp))
                .background(SlateCard)
                .padding(horizontal = 8.dp, vertical = 6.dp)
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = TitanGold, modifier = Modifier.size(12.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "Milestone: $event",
                  style = MaterialTheme.typography.bodySmall,
                  color = TitanGold,
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Medium
                )
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // 7. Ecosystem Geographic & Tier Distribution Breakdown
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        EcosystemCohortTag(label = "Bengaluru Ecosystem", percentage = "68%", color = TitanCyan, modifier = Modifier.weight(1f))
        EcosystemCohortTag(label = "Strategy & Ops Leads", percentage = "44%", color = TitanGold, modifier = Modifier.weight(1f))
        EcosystemCohortTag(label = "Top-Tier Tier S/S+", percentage = "52%", color = TitanEmerald, modifier = Modifier.weight(1f))
      }
    }
  }
}

@Composable
fun NetworkKpiTile(
  label: String,
  value: String,
  subtext: String,
  color: Color,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(8.dp))
      .background(SlateElevated)
      .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
      .padding(8.dp)
  ) {
    Column {
      Text(
        text = label,
        style = MaterialTheme.typography.labelSmall,
        fontSize = 7.5.sp,
        color = color,
        fontWeight = FontWeight.Bold
      )
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = value,
        style = MaterialTheme.typography.titleMedium,
        color = TextPrimaryDark,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp
      )
      Text(
        text = subtext,
        style = MaterialTheme.typography.bodySmall,
        color = TextMutedDark,
        fontSize = 8.5.sp
      )
    }
  }
}

@Composable
fun EcosystemCohortTag(
  label: String,
  percentage: String,
  color: Color,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(6.dp))
      .background(SlateElevated)
      .border(1.dp, SlateBorder, RoundedCornerShape(6.dp))
      .padding(horizontal = 6.dp, vertical = 4.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(label, style = MaterialTheme.typography.labelSmall, color = TextSecondaryDark, fontSize = 8.sp)
      Text(percentage, style = MaterialTheme.typography.labelSmall, color = color, fontWeight = FontWeight.Bold, fontSize = 8.sp)
    }
  }
}
