package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateCard
import com.example.ui.theme.SlateElevated
import com.example.ui.theme.TextMutedDark
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import com.example.ui.theme.TitanCyan
import com.example.ui.theme.TitanEmerald
import com.example.ui.theme.TitanGold
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

data class RadarDimension(
  val label: String,
  val score: Float, // 0.0f to 1.0f
  val displayValue: String,
  val benchmark: Float = 0.70f,
  val insight: String
)

@Composable
fun ExecutiveRadarChart(
  modifier: Modifier = Modifier,
  dimensions: List<RadarDimension> = listOf(
    RadarDimension("AI Automation", 0.94f, "94%", 0.65f, "Top 1% in autonomous prompt chains & data ops"),
    RadarDimension("Biz Analytics", 0.90f, "90%", 0.72f, "SQL, Python pipelines, KPI instrumentation"),
    RadarDimension("Ops & Strategy", 0.88f, "88%", 0.70f, "Bottleneck teardowns & unit economics modeling"),
    RadarDimension("Exec Comms", 0.92f, "92%", 0.68f, "Board memos, structured STAR storytelling"),
    RadarDimension("Commercial Sense", 0.86f, "86%", 0.60f, "Family business turnaround & gross margin scaling"),
    RadarDimension("Global Context", 0.89f, "89%", 0.62f, "BBA International Business cross-border perspective")
  )
) {
  var selectedDimensionIndex by remember { mutableStateOf<Int?>(null) }
  val activeDimension = selectedDimensionIndex?.let { dimensions.getOrNull(it) } ?: dimensions.first()

  val animatedProgress by animateFloatAsState(
    targetValue = 1f,
    animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
    label = "radar_anim"
  )

  Column(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(12.dp))
      .background(SlateCard)
      .border(1.dp, SlateBorder, RoundedCornerShape(12.dp))
      .padding(14.dp)
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
            .background(TitanCyan)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "EXECUTIVE COMPETENCY RADAR",
          style = MaterialTheme.typography.labelSmall,
          color = TitanCyan,
          fontWeight = FontWeight.Bold,
          letterSpacing = 1.sp
        )
      }
      Text(
        text = "Composite: 90/100",
        style = MaterialTheme.typography.labelSmall,
        color = TitanGold,
        fontWeight = FontWeight.Bold
      )
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Radar Canvas
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(180.dp),
      contentAlignment = Alignment.Center
    ) {
      Canvas(modifier = Modifier.size(170.dp)) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val radius = (size.width / 2f) * 0.85f
        val count = dimensions.size
        val angleStep = (2 * PI / count).toFloat()

        // 1. Draw web grid rings (25%, 50%, 75%, 100%)
        val rings = listOf(0.25f, 0.5f, 0.75f, 1.0f)
        rings.forEach { ringFraction ->
          val ringPath = Path()
          for (i in 0 until count) {
            val angle = i * angleStep - (PI / 2).toFloat()
            val r = radius * ringFraction
            val x = center.x + r * cos(angle)
            val y = center.y + r * sin(angle)
            if (i == 0) ringPath.moveTo(x, y) else ringPath.lineTo(x, y)
          }
          ringPath.close()
          drawPath(
            path = ringPath,
            color = SlateBorder.copy(alpha = if (ringFraction == 1.0f) 0.6f else 0.25f),
            style = Stroke(width = if (ringFraction == 1.0f) 1.5f else 1f)
          )
        }

        // 2. Draw spoke lines from center to outer ring
        for (i in 0 until count) {
          val angle = i * angleStep - (PI / 2).toFloat()
          val x = center.x + radius * cos(angle)
          val y = center.y + radius * sin(angle)
          drawLine(
            color = SlateBorder.copy(alpha = 0.4f),
            start = center,
            end = Offset(x, y),
            strokeWidth = 1f
          )
        }

        // 3. Draw Benchmark polygon (dashed/faint gold)
        val benchmarkPath = Path()
        for (i in 0 until count) {
          val angle = i * angleStep - (PI / 2).toFloat()
          val r = radius * dimensions[i].benchmark * animatedProgress
          val x = center.x + r * cos(angle)
          val y = center.y + r * sin(angle)
          if (i == 0) benchmarkPath.moveTo(x, y) else benchmarkPath.lineTo(x, y)
        }
        benchmarkPath.close()
        drawPath(
          path = benchmarkPath,
          color = TitanGold.copy(alpha = 0.15f),
          style = Fill
        )
        drawPath(
          path = benchmarkPath,
          color = TitanGold.copy(alpha = 0.4f),
          style = Stroke(width = 1f)
        )

        // 4. Draw Candidate Score Polygon (TitanCyan & Emerald)
        val candidatePath = Path()
        val points = mutableListOf<Offset>()
        for (i in 0 until count) {
          val angle = i * angleStep - (PI / 2).toFloat()
          val r = radius * dimensions[i].score * animatedProgress
          val x = center.x + r * cos(angle)
          val y = center.y + r * sin(angle)
          val pt = Offset(x, y)
          points.add(pt)
          if (i == 0) candidatePath.moveTo(x, y) else candidatePath.lineTo(x, y)
        }
        candidatePath.close()

        drawPath(
          path = candidatePath,
          color = TitanCyan.copy(alpha = 0.25f),
          style = Fill
        )
        drawPath(
          path = candidatePath,
          color = TitanCyan,
          style = Stroke(width = 2.5f)
        )

        // 5. Draw Vertices Nodes
        points.forEachIndexed { index, pt ->
          val isSelected = selectedDimensionIndex == index
          drawCircle(
            color = if (isSelected) TitanEmerald else TitanCyan,
            radius = if (isSelected) 6f else 4f,
            center = pt
          )
          drawCircle(
            color = Color.Black,
            radius = if (isSelected) 3f else 2f,
            center = pt
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(8.dp))

    // Dimension Selectors Pill Row
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
      dimensions.forEachIndexed { index, dim ->
        val isSelected = (selectedDimensionIndex ?: 0) == index
        Box(
          modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(6.dp))
            .background(if (isSelected) TitanCyan.copy(alpha = 0.2f) else SlateElevated)
            .border(1.dp, if (isSelected) TitanCyan else SlateBorder, RoundedCornerShape(6.dp))
            .clickable { selectedDimensionIndex = index }
            .padding(vertical = 4.dp),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = dim.label.split(" ").firstOrNull() ?: dim.label,
            style = MaterialTheme.typography.labelSmall,
            color = if (isSelected) TitanCyan else TextSecondaryDark,
            fontWeight = FontWeight.Bold,
            fontSize = 8.sp,
            maxLines = 1
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Selected Dimension Detail Card
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(8.dp))
        .background(SlateElevated)
        .border(1.dp, SlateBorder, RoundedCornerShape(8.dp))
        .padding(10.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = activeDimension.label.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = TitanCyan,
            fontWeight = FontWeight.Bold
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = activeDimension.insight,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondaryDark,
            fontSize = 11.sp
          )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column(horizontalAlignment = Alignment.End) {
          Text(
            text = activeDimension.displayValue,
            style = MaterialTheme.typography.titleMedium,
            color = TitanEmerald,
            fontWeight = FontWeight.Black
          )
          Text(
            text = "vs ${ (activeDimension.benchmark * 100).toInt() }% Peer Avg",
            style = MaterialTheme.typography.labelSmall,
            color = TextMutedDark,
            fontSize = 8.sp
          )
        }
      }
    }
  }
}
