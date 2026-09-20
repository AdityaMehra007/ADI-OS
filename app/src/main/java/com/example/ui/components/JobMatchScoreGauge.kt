package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
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
import com.example.ui.theme.TitanCyanGlow
import com.example.ui.theme.TitanEmerald
import com.example.ui.theme.TitanGold
import com.example.ui.theme.TitanIndigo
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

/**
 * Visual styling options for the Job Match Score gauge.
 */
enum class GaugeStyle {
  /** Open 240-degree speedometer arc layout with bottom calibration break */
  RADIAL_SPEEDOMETER,
  /** Complete 360-degree closed circular progress ring */
  CIRCULAR_RING
}

/**
 * Match tier categorization based on AI-calculated alignment score.
 */
enum class MatchTier(
  val label: String,
  val executiveVerdict: String,
  val primaryColor: Color,
  val secondaryColor: Color,
  val icon: ImageVector
) {
  EXCEPTIONAL(
    label = "EXCEPTIONAL FIT",
    executiveVerdict = "Direct role alignment with verified domain competencies. Interview priority recommended.",
    primaryColor = TitanEmerald,
    secondaryColor = TitanCyan,
    icon = Icons.Default.Stars
  ),
  STRONG(
    label = "STRONG ALIGNMENT",
    executiveVerdict = "Solid operational coverage. Focus preparation on target company specifics and nuances.",
    primaryColor = TitanCyan,
    secondaryColor = TitanIndigo,
    icon = Icons.Default.CheckCircle
  ),
  MODERATE(
    label = "MODERATE FIT • UPSKILL",
    executiveVerdict = "Core aptitude matches, but key technical or domain gaps exist. Action plan bridging advised.",
    primaryColor = TitanGold,
    secondaryColor = Color(0xFFFFB74D),
    icon = Icons.Default.Warning
  ),
  LOW(
    label = "CRITICAL SKILL GAPS",
    executiveVerdict = "Substantial competency prerequisites missing. Significant targeted preparation required.",
    primaryColor = TitanCrimson,
    secondaryColor = Color(0xFFF97316),
    icon = Icons.Default.ErrorOutline
  );

  companion object {
    fun fromScore(score: Int): MatchTier = when {
      score >= 85 -> EXCEPTIONAL
      score >= 70 -> STRONG
      score >= 50 -> MODERATE
      else -> LOW
    }
  }
}

/**
 * High-craft circular progress indicator & gauge component to visually display
 * the AI-calculated job match score with animated radial sweeps, gradient strokes,
 * tick graduation markers, and dynamic tier badges.
 */
@Composable
fun JobMatchScoreGauge(
  score: Int,
  modifier: Modifier = Modifier,
  gaugeSize: Dp = 136.dp,
  strokeWidth: Dp = 12.dp,
  style: GaugeStyle = GaugeStyle.RADIAL_SPEEDOMETER,
  showLabel: Boolean = true,
  showTierBadge: Boolean = true,
  animateOnAppear: Boolean = true
) {
  val clampedScore = score.coerceIn(0, 100)
  val tier = remember(clampedScore) { MatchTier.fromScore(clampedScore) }

  var animationTriggered by remember { mutableStateOf(!animateOnAppear) }
  LaunchedEffect(clampedScore) {
    animationTriggered = true
  }

  val animatedProgress by animateFloatAsState(
    targetValue = if (animationTriggered) clampedScore / 100f else 0f,
    animationSpec = tween(durationMillis = 1100, easing = FastOutSlowInEasing),
    label = "job_match_gauge_progress"
  )

  val displayScore = (animatedProgress * 100).roundToInt()

  val startAngle = if (style == GaugeStyle.RADIAL_SPEEDOMETER) 150f else -90f
  val totalSweepAngle = if (style == GaugeStyle.RADIAL_SPEEDOMETER) 240f else 360f

  val primaryColor = tier.primaryColor
  val secondaryColor = tier.secondaryColor

  Box(
    modifier = modifier
      .size(gaugeSize)
      .semantics {
        contentDescription = "AI Job Match Score: $clampedScore percent, ${tier.label}"
      }
      .testTag("job_match_score_gauge"),
    contentAlignment = Alignment.Center
  ) {
    // 1. Custom Arc Drawing Canvas
    Canvas(
      modifier = Modifier
        .size(gaugeSize)
        .testTag("job_match_score_canvas")
    ) {
      val strokePx = strokeWidth.toPx()
      val diameter = size.minDimension - strokePx
      val radius = diameter / 2f
      val arcTopLeft = Offset(
        x = (size.width - diameter) / 2f,
        y = (size.height - diameter) / 2f
      )
      val arcSize = Size(diameter, diameter)
      val center = Offset(size.width / 2f, size.height / 2f)

      // Background Track Arc
      drawArc(
        color = SlateBorder.copy(alpha = 0.55f),
        startAngle = startAngle,
        sweepAngle = totalSweepAngle,
        useCenter = false,
        topLeft = arcTopLeft,
        size = arcSize,
        style = Stroke(
          width = strokePx,
          cap = if (style == GaugeStyle.RADIAL_SPEEDOMETER) StrokeCap.Round else StrokeCap.Butt
        )
      )

      // Subtle Tick Calibration Marks for Radial Speedometer
      if (style == GaugeStyle.RADIAL_SPEEDOMETER) {
        val tickCount = 9
        for (i in 0 until tickCount) {
          val fraction = i / (tickCount - 1).toFloat()
          val angleDeg = startAngle + fraction * totalSweepAngle
          val angleRad = Math.toRadians(angleDeg.toDouble())

          val innerR = radius - (strokePx * 0.75f)
          val outerR = radius - (strokePx * 0.4f)

          val startX = center.x + (cos(angleRad) * innerR).toFloat()
          val startY = center.y + (sin(angleRad) * innerR).toFloat()
          val endX = center.x + (cos(angleRad) * outerR).toFloat()
          val endY = center.y + (sin(angleRad) * outerR).toFloat()

          val isPassed = fraction <= animatedProgress
          val tickColor = if (isPassed) primaryColor.copy(alpha = 0.7f) else TextMutedDark.copy(alpha = 0.4f)

          drawLine(
            color = tickColor,
            start = Offset(startX, startY),
            end = Offset(endX, endY),
            strokeWidth = 1.5.dp.toPx(),
            cap = StrokeCap.Round
          )
        }
      }

      // Active Gradient Foreground Arc
      val activeSweep = (totalSweepAngle * animatedProgress).coerceAtLeast(0.1f)
      val gradientBrush = Brush.sweepGradient(
        colors = listOf(
          secondaryColor,
          primaryColor,
          primaryColor
        ),
        center = center
      )

      drawArc(
        brush = gradientBrush,
        startAngle = startAngle,
        sweepAngle = activeSweep,
        useCenter = false,
        topLeft = arcTopLeft,
        size = arcSize,
        style = Stroke(
          width = strokePx,
          cap = StrokeCap.Round
        )
      )
    }

    // 2. Central Score Display & Metrics
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center,
      modifier = Modifier.padding(horizontal = 4.dp)
    ) {
      Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.Center
      ) {
        Text(
          text = "$displayScore",
          fontSize = if (gaugeSize >= 120.dp) 32.sp else 20.sp,
          fontWeight = FontWeight.Black,
          color = TextPrimaryDark,
          letterSpacing = (-0.5).sp,
          modifier = Modifier.testTag("job_match_score_text")
        )
        Text(
          text = "%",
          fontSize = if (gaugeSize >= 120.dp) 16.sp else 11.sp,
          fontWeight = FontWeight.Bold,
          color = primaryColor,
          modifier = Modifier.padding(top = 2.dp)
        )
      }

      if (showLabel) {
        Text(
          text = if (gaugeSize >= 120.dp) "MATCH FIT" else "FIT",
          fontSize = if (gaugeSize >= 120.dp) 9.sp else 8.sp,
          fontWeight = FontWeight.Bold,
          color = TextMutedDark,
          letterSpacing = 0.8.sp
        )
      }

      if (showTierBadge && gaugeSize >= 130.dp) {
        Spacer(modifier = Modifier.height(3.dp))
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(primaryColor.copy(alpha = 0.15f))
            .border(0.5.dp, primaryColor.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
            .padding(horizontal = 5.dp, vertical = 1.5.dp)
        ) {
          Text(
            text = when (tier) {
              MatchTier.EXCEPTIONAL -> "TOP FIT"
              MatchTier.STRONG -> "STRONG"
              MatchTier.MODERATE -> "MODERATE"
              MatchTier.LOW -> "GAP"
            },
            color = primaryColor,
            fontSize = 8.sp,
            fontWeight = FontWeight.Black
          )
        }
      }
    }
  }
}

/**
 * Comprehensive visual hero card pairing the Circular Gauge with executive candidate alignment
 * breakdown, verified telemetry indicators, and match highlights.
 */
@Composable
fun JobMatchScoreHeroCard(
  score: Int,
  targetCompany: String,
  targetRole: String,
  modifier: Modifier = Modifier,
  skillsMatchedCount: Int = 0,
  totalSkillsCount: Int = 0,
  compensationBenchmark: String? = null,
  seniorityLevel: String? = null,
  isGeminiGenerated: Boolean = true
) {
  val tier = MatchTier.fromScore(score)

  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("job_match_score_hero_card"),
    colors = CardDefaults.cardColors(containerColor = SlateElevated),
    shape = RoundedCornerShape(14.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, tier.primaryColor.copy(alpha = 0.35f))
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp)
    ) {
      // Header Label & Model Attribution
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = tier.icon,
            contentDescription = null,
            tint = tier.primaryColor,
            modifier = Modifier.size(15.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = tier.label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Black,
            color = tier.primaryColor,
            letterSpacing = 0.8.sp
          )
        }

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(ObsidianDark)
            .border(0.5.dp, SlateBorder, RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.AutoAwesome,
              contentDescription = null,
              tint = TitanCyan,
              modifier = Modifier.size(10.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = if (isGeminiGenerated) "GEMINI 3.5 AUDIT" else "LOCAL DETERMINISTIC",
              fontSize = 8.sp,
              fontWeight = FontWeight.Bold,
              color = TitanCyan
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Gauge and Telemetry Grid
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        // The Circular Gauge Component
        JobMatchScoreGauge(
          score = score,
          gaugeSize = 120.dp,
          strokeWidth = 11.dp,
          style = GaugeStyle.RADIAL_SPEEDOMETER,
          showLabel = true,
          showTierBadge = true
        )

        // Alignment Details & Breakdown Column
        Column(
          modifier = Modifier.weight(1f),
          verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Text(
            text = "$targetRole • $targetCompany",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimaryDark,
            maxLines = 1
          )

          Text(
            text = tier.executiveVerdict,
            fontSize = 11.sp,
            color = TextSecondaryDark,
            lineHeight = 15.sp
          )

          Spacer(modifier = Modifier.height(2.dp))

          // Mini Metric Badges
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            if (totalSkillsCount > 0) {
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(6.dp))
                  .background(ObsidianDark)
                  .border(0.5.dp, SlateBorder, RoundedCornerShape(6.dp))
                  .padding(horizontal = 7.dp, vertical = 4.dp)
              ) {
                Column {
                  Text("SKILLS MATCH", fontSize = 8.sp, color = TextMutedDark, fontWeight = FontWeight.Bold)
                  Text(
                    text = "$skillsMatchedCount / $totalSkillsCount",
                    fontSize = 11.sp,
                    color = tier.primaryColor,
                    fontWeight = FontWeight.Black
                  )
                }
              }
            }

            if (!seniorityLevel.isNullOrBlank()) {
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(6.dp))
                  .background(ObsidianDark)
                  .border(0.5.dp, SlateBorder, RoundedCornerShape(6.dp))
                  .padding(horizontal = 7.dp, vertical = 4.dp)
              ) {
                Column {
                  Text("LEVEL", fontSize = 8.sp, color = TextMutedDark, fontWeight = FontWeight.Bold)
                  Text(
                    text = seniorityLevel,
                    fontSize = 11.sp,
                    color = TitanCyan,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                  )
                }
              }
            }

            if (!compensationBenchmark.isNullOrBlank()) {
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(6.dp))
                  .background(ObsidianDark)
                  .border(0.5.dp, SlateBorder, RoundedCornerShape(6.dp))
                  .padding(horizontal = 7.dp, vertical = 4.dp)
              ) {
                Column {
                  Text("BENCHMARK", fontSize = 8.sp, color = TextMutedDark, fontWeight = FontWeight.Bold)
                  Text(
                    text = compensationBenchmark,
                    fontSize = 11.sp,
                    color = TitanGold,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                  )
                }
              }
            }
          }
        }
      }
    }
  }
}
