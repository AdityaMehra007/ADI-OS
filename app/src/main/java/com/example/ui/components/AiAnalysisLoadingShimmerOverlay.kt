package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import kotlinx.coroutines.delay

/**
 * Animated Loading Overlay with shimmer effects that provides high-fidelity visual feedback
 * while the Gemini AI analysis and Match Score calculation is being computed.
 */
@Composable
fun AiAnalysisLoadingShimmerOverlay(
  targetRole: String = "Strategic Operations Lead",
  targetCompany: String = "Target Enterprise",
  modifier: Modifier = Modifier
) {
  // 1. Shimmer infinite transition sweep
  val infiniteTransition = rememberInfiniteTransition(label = "ai_analysis_shimmer")
  val shimmerTranslateAnim by infiniteTransition.animateFloat(
    initialValue = -300f,
    targetValue = 1300f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 1700, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "shimmer_translate"
  )

  // 2. Pulse scale & alpha for radar / computing circular score gauge
  val pulseScale by infiniteTransition.animateFloat(
    initialValue = 0.93f,
    targetValue = 1.07f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "pulse_scale"
  )
  val pulseAlpha by infiniteTransition.animateFloat(
    initialValue = 0.35f,
    targetValue = 0.85f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "pulse_alpha"
  )

  // 3. Shimmer gradient brushes
  val skeletonShimmerBrush = Brush.linearGradient(
    colors = listOf(
      Color(0xFF162238),
      Color(0xFF223554),
      Color(0xFF2E466F),
      Color(0xFF223554),
      Color(0xFF162238)
    ),
    start = Offset(shimmerTranslateAnim - 350f, shimmerTranslateAnim - 350f),
    end = Offset(shimmerTranslateAnim + 350f, shimmerTranslateAnim + 350f)
  )

  val neonGlowSweepBrush = Brush.linearGradient(
    colors = listOf(
      Color.Transparent,
      TitanCyan.copy(alpha = 0.08f),
      TitanEmerald.copy(alpha = 0.18f),
      TitanCyan.copy(alpha = 0.08f),
      Color.Transparent
    ),
    start = Offset(shimmerTranslateAnim - 250f, 0f),
    end = Offset(shimmerTranslateAnim + 250f, 300f)
  )

  // 4. Multi-phase analysis ticker states
  val analysisPhases = remember {
    listOf(
      AnalysisPhase(
        step = 1,
        title = "Scanning Target Requirements & Scope",
        subtitle = "Parsing responsibilities for $targetRole...",
        progress = 0.22f
      ),
      AnalysisPhase(
        step = 2,
        title = "Extracting Competency Taxonomy",
        subtitle = "Cross-referencing core technical skills & keywords...",
        progress = 0.48f
      ),
      AnalysisPhase(
        step = 3,
        title = "Evaluating Experience & Seniority Alignment",
        subtitle = "Benchmarking domain track record vs $targetCompany expectations...",
        progress = 0.72f
      ),
      AnalysisPhase(
        step = 4,
        title = "Auditing Quantified Impact & Metric Proofs",
        subtitle = "Calculating metric density ratio and leadership power verbs...",
        progress = 0.88f
      ),
      AnalysisPhase(
        step = 5,
        title = "Synthesizing Match Score & Action Plan",
        subtitle = "Calibrating circular gauge and strategic execution roadmap...",
        progress = 0.96f
      )
    )
  }

  var currentPhaseIndex by remember { mutableIntStateOf(0) }

  LaunchedEffect(Unit) {
    while (true) {
      delay(1100)
      currentPhaseIndex = (currentPhaseIndex + 1) % analysisPhases.size
    }
  }

  val activePhase = analysisPhases[currentPhaseIndex]
  val animatedProgress by animateFloatAsState(
    targetValue = activePhase.progress,
    animationSpec = tween(durationMillis = 650, easing = FastOutSlowInEasing),
    label = "analysis_progress_anim"
  )

  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("ai_analysis_loading_shimmer_overlay"),
    colors = CardDefaults.cardColors(containerColor = SlateDarker),
    shape = RoundedCornerShape(16.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, TitanCyan.copy(alpha = 0.45f))
  ) {
    Box(modifier = Modifier.fillMaxWidth()) {
      // Shimmer sweep background accent
      Box(
        modifier = Modifier
          .matchParentSize()
          .background(neonGlowSweepBrush)
      )

      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        // --- HEADER: LIVE AI COMPUTATION STATUS ---
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
                .clip(CircleShape)
                .background(TitanCyan.copy(alpha = 0.15f))
                .scale(pulseScale),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = TitanCyan,
                modifier = Modifier.size(18.dp)
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  modifier = Modifier
                    .size(7.dp)
                    .clip(CircleShape)
                    .background(TitanEmerald)
                    .alpha(pulseAlpha)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "GEMINI 3.5 FLASH • COMPUTING MATCH FIT",
                  fontSize = 9.sp,
                  fontWeight = FontWeight.Black,
                  color = TitanCyan,
                  letterSpacing = 0.8.sp
                )
              }
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = "$targetRole • $targetCompany",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimaryDark,
                maxLines = 1
              )
            }
          }

          // Step count badge
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(TitanCyan.copy(alpha = 0.15f))
              .border(1.dp, TitanCyan.copy(alpha = 0.35f), RoundedCornerShape(6.dp))
              .padding(horizontal = 8.dp, vertical = 3.dp)
          ) {
            Text(
              text = "${activePhase.step} / ${analysisPhases.size}",
              fontSize = 10.sp,
              fontWeight = FontWeight.Black,
              color = TitanCyan
            )
          }
        }

        // --- PHASE TITLE TICKER & PROGRESS BAR ---
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(SlateElevated)
            .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
          AnimatedContent(
            targetState = activePhase,
            transitionSpec = { fadeIn(tween(250)) togetherWith fadeOut(tween(200)) },
            label = "phase_text_transition"
          ) { phase ->
            Column {
              Text(
                text = phase.title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TitanEmerald
              )
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = phase.subtitle,
                fontSize = 10.sp,
                color = TextSecondaryDark,
                maxLines = 1
              )
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          // Animated Smooth Progress Bar
          LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier
              .fillMaxWidth()
              .height(5.dp)
              .clip(RoundedCornerShape(3.dp)),
            color = TitanCyan,
            trackColor = SlateCard,
            strokeCap = StrokeCap.Round
          )
        }

        // --- SHIMMER SKELETON 1: CIRCULAR MATCH SCORE HERO ---
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .testTag("shimmer_circular_score_placeholder"),
          colors = CardDefaults.cardColors(containerColor = SlateCard),
          shape = RoundedCornerShape(14.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            // Circular Pulse Score Placeholder
            Box(
              modifier = Modifier
                .size(76.dp)
                .scale(pulseScale),
              contentAlignment = Alignment.Center
            ) {
              // Outer radar ring
              Box(
                modifier = Modifier
                  .size(76.dp)
                  .clip(CircleShape)
                  .border(2.dp, TitanCyan.copy(alpha = pulseAlpha * 0.5f), CircleShape)
              )
              // Inner glowing core
              Box(
                modifier = Modifier
                  .size(58.dp)
                  .clip(CircleShape)
                  .background(skeletonShimmerBrush),
                contentAlignment = Alignment.Center
              ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                  Icon(
                    imageVector = Icons.Default.Bolt,
                    contentDescription = null,
                    tint = TitanCyan.copy(alpha = pulseAlpha),
                    modifier = Modifier.size(16.dp)
                  )
                  Text(
                    text = "--%",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    color = TitanCyan.copy(alpha = pulseAlpha)
                  )
                }
              }
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Text placeholder lines
            Column(
              modifier = Modifier.weight(1f),
              verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Box(
                modifier = Modifier
                  .fillMaxWidth(0.85f)
                  .height(14.dp)
                  .clip(RoundedCornerShape(4.dp))
                  .background(skeletonShimmerBrush)
              )
              Box(
                modifier = Modifier
                  .fillMaxWidth(0.55f)
                  .height(11.dp)
                  .clip(RoundedCornerShape(4.dp))
                  .background(skeletonShimmerBrush)
              )
              Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Box(
                  modifier = Modifier
                    .width(60.dp)
                    .height(18.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(skeletonShimmerBrush)
                )
                Box(
                  modifier = Modifier
                    .width(75.dp)
                    .height(18.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(skeletonShimmerBrush)
                )
              }
            }
          }
        }

        // --- SHIMMER SKELETON 2: 3-METRIC CARDS (Skills Fit, Tenure, Metric Density) ---
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          repeat(3) { index ->
            Box(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(10.dp))
                .background(SlateCard)
                .border(1.dp, SlateBorder, RoundedCornerShape(10.dp))
                .padding(10.dp)
            ) {
              Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Box(
                  modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(9.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(skeletonShimmerBrush)
                )
                Box(
                  modifier = Modifier
                    .fillMaxWidth(0.45f)
                    .height(16.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(skeletonShimmerBrush)
                )
                Box(
                  modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(skeletonShimmerBrush)
                )
              }
            }
          }
        }

        // --- SHIMMER SKELETON 3: SKILLS MATCH PILLS ---
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
          Box(
            modifier = Modifier
              .width(140.dp)
              .height(10.dp)
              .clip(RoundedCornerShape(3.dp))
              .background(skeletonShimmerBrush)
          )
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            val widths = listOf(85.dp, 105.dp, 75.dp, 115.dp, 90.dp)
            widths.forEach { w ->
              Box(
                modifier = Modifier
                  .width(w)
                  .height(24.dp)
                  .clip(RoundedCornerShape(6.dp))
                  .background(skeletonShimmerBrush)
              )
            }
          }
        }

        // --- SHIMMER SKELETON 4: EXECUTIVE SUMMARY PARAGRAPH ---
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(SlateElevated)
            .padding(12.dp),
          verticalArrangement = Arrangement.spacedBy(7.dp)
        ) {
          Box(
            modifier = Modifier
              .width(160.dp)
              .height(10.dp)
              .clip(RoundedCornerShape(3.dp))
              .background(skeletonShimmerBrush)
          )
          Box(
            modifier = Modifier
              .fillMaxWidth(0.95f)
              .height(9.dp)
              .clip(RoundedCornerShape(3.dp))
              .background(skeletonShimmerBrush)
          )
          Box(
            modifier = Modifier
              .fillMaxWidth(0.82f)
              .height(9.dp)
              .clip(RoundedCornerShape(3.dp))
              .background(skeletonShimmerBrush)
          )
          Box(
            modifier = Modifier
              .fillMaxWidth(0.60f)
              .height(9.dp)
              .clip(RoundedCornerShape(3.dp))
              .background(skeletonShimmerBrush)
          )
        }
      }
    }
  }
}

private data class AnalysisPhase(
  val step: Int,
  val title: String,
  val subtitle: String,
  val progress: Float
)
