package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.TravelExplore
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.service.FirestoreSyncState
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
import com.example.ui.theme.TitanViolet
import kotlin.math.cos
import kotlin.math.sin

/**
 * High-fidelity animated visual loading and sync indicator for when the app fetches
 * or synchronizes Company Intelligence data from Firebase Firestore.
 *
 * Features:
 * - Cybernetic radar scan canvas with rotating sweep beam & orbiting data packet nodes.
 * - Pulsing signal beacons and smooth multi-step progress telemetry bar.
 * - Metallic shimmer sweep across high-contrast dark card container.
 * - Granular Firestore status messaging (collection, latency, document key).
 */
@Composable
fun CompanyIntelFirestoreSyncIndicator(
  isSyncing: Boolean,
  syncState: FirestoreSyncState,
  targetCompany: String,
  syncProgress: Float,
  statusMessage: String,
  onRetry: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  // Infinite transition for continuous cybernetic motion
  val infiniteTransition = rememberInfiniteTransition(label = "firestore_company_intel_sync")

  // 1. Radar sweep rotation (0 to 360 deg)
  val sweepRotation by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 360f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 2400, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "sweep_rotation"
  )

  // 2. Concentric beacon ring pulse scale (0.4f to 1.15f)
  val pulseRingScale by infiniteTransition.animateFloat(
    initialValue = 0.4f,
    targetValue = 1.15f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 1800, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "beacon_pulse_scale"
  )

  // 3. Beacon ring alpha (0.9f down to 0.0f)
  val pulseRingAlpha by infiniteTransition.animateFloat(
    initialValue = 0.85f,
    targetValue = 0f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 1800, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "beacon_pulse_alpha"
  )

  // 4. Shimmer translation across card
  val shimmerOffset by infiniteTransition.animateFloat(
    initialValue = -300f,
    targetValue = 1200f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 2000, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "shimmer_offset"
  )

  // 5. Breathing core glow alpha
  val coreGlowAlpha by infiniteTransition.animateFloat(
    initialValue = 0.45f,
    targetValue = 0.95f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "core_glow_alpha"
  )

  // Smooth progress animation
  val animatedProgress by animateFloatAsState(
    targetValue = syncProgress.coerceIn(0.05f, 1.0f),
    animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing),
    label = "animated_sync_progress"
  )

  val shimmerBrush = Brush.linearGradient(
    colors = listOf(
      Color.Transparent,
      TitanCyan.copy(alpha = 0.12f),
      TitanEmerald.copy(alpha = 0.18f),
      TitanCyan.copy(alpha = 0.12f),
      Color.Transparent
    ),
    start = Offset(shimmerOffset - 250f, 0f),
    end = Offset(shimmerOffset + 250f, 150f)
  )

  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("firestore_company_intel_sync_indicator")
      .clip(RoundedCornerShape(14.dp))
      .border(
        width = 1.dp,
        brush = Brush.horizontalGradient(
          listOf(
            TitanCyan.copy(alpha = 0.6f),
            TitanEmerald.copy(alpha = 0.6f),
            TitanCyan.copy(alpha = 0.3f)
          )
        ),
        shape = RoundedCornerShape(14.dp)
      ),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = SlateCard)
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .background(shimmerBrush)
        .padding(14.dp)
    ) {
      Column(modifier = Modifier.fillMaxWidth()) {
        // Top Header Row with Cybernetic Radar & Title
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
          ) {
            // Animated Radar Scan & Sync Core
            Box(
              modifier = Modifier.size(54.dp),
              contentAlignment = Alignment.Center
            ) {
              // Background Radar Canvas
              Canvas(modifier = Modifier.size(54.dp)) {
                val center = Offset(size.width / 2f, size.height / 2f)
                val maxRadius = size.minDimension / 2f

                // Outer border grid
                drawCircle(
                  color = TitanCyan.copy(alpha = 0.25f),
                  radius = maxRadius - 1f,
                  style = Stroke(width = 1.5.dp.toPx())
                )

                // Middle concentric ring
                drawCircle(
                  color = TitanCyan.copy(alpha = 0.18f),
                  radius = maxRadius * 0.62f,
                  style = Stroke(width = 1.dp.toPx())
                )

                // Concentric pulsing beacon wave
                if (isSyncing) {
                  drawCircle(
                    color = TitanEmerald.copy(alpha = pulseRingAlpha),
                    radius = maxRadius * pulseRingScale.coerceAtMost(1.0f),
                    style = Stroke(width = 1.8.dp.toPx())
                  )
                }

                // Radar Crosshair Lines
                drawLine(
                  color = TitanCyan.copy(alpha = 0.2f),
                  start = Offset(center.x, 4f),
                  end = Offset(center.x, size.height - 4f),
                  strokeWidth = 1.dp.toPx()
                )
                drawLine(
                  color = TitanCyan.copy(alpha = 0.2f),
                  start = Offset(4f, center.y),
                  end = Offset(size.width - 4f, center.y),
                  strokeWidth = 1.dp.toPx()
                )

                // Rotating Radar Sweep Arc
                if (isSyncing) {
                  drawArc(
                    brush = Brush.sweepGradient(
                      listOf(
                        Color.Transparent,
                        TitanCyan.copy(alpha = 0.05f),
                        TitanEmerald.copy(alpha = 0.45f)
                      )
                    ),
                    startAngle = sweepRotation - 55f,
                    sweepAngle = 55f,
                    useCenter = true,
                    topLeft = Offset(2f, 2f),
                    size = androidx.compose.ui.geometry.Size(size.width - 4f, size.height - 4f)
                  )

                  // Orbiting Data Packet Nodes
                  val rad = Math.toRadians(sweepRotation.toDouble())
                  val orbitRadius = maxRadius * 0.75f
                  val orbitX = center.x + (orbitRadius * cos(rad)).toFloat()
                  val orbitY = center.y + (orbitRadius * sin(rad)).toFloat()
                  drawCircle(
                    color = TitanGold,
                    radius = 2.5.dp.toPx(),
                    center = Offset(orbitX, orbitY)
                  )
                }
              }

              // Center Core Icon (CloudSync or CloudDone)
              Box(
                modifier = Modifier
                  .size(24.dp)
                  .clip(CircleShape)
                  .background(
                    if (isSyncing) TitanCyan.copy(alpha = coreGlowAlpha * 0.35f)
                    else TitanEmerald.copy(alpha = 0.25f)
                  ),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = if (isSyncing) Icons.Default.CloudSync else Icons.Default.CloudDone,
                  contentDescription = "Firestore Intelligence Sync",
                  tint = if (isSyncing) TitanCyan else TitanEmerald,
                  modifier = Modifier
                    .size(16.dp)
                    .then(if (isSyncing) Modifier.rotate(sweepRotation * 0.5f) else Modifier)
                )
              }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Title and Live State
            Column {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = "FIRESTORE COMPANY INTEL",
                  style = MaterialTheme.typography.labelSmall,
                  color = TitanCyan,
                  fontWeight = FontWeight.Black,
                  letterSpacing = 0.8.sp,
                  fontSize = 10.sp
                )
                Spacer(modifier = Modifier.width(6.dp))

                // Pulsing Live Beacon Pill
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(
                      if (isSyncing) TitanEmerald.copy(alpha = 0.2f)
                      else TitanCyan.copy(alpha = 0.15f)
                    )
                    .padding(horizontal = 5.dp, vertical = 2.dp)
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                      modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(if (isSyncing) TitanEmerald else TitanCyan)
                        .then(if (isSyncing) Modifier.scale(pulseRingScale.coerceIn(0.8f, 1.2f)) else Modifier)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                      text = if (isSyncing) "FETCHING" else "SYNCED",
                      style = MaterialTheme.typography.labelSmall,
                      color = if (isSyncing) TitanEmerald else TitanCyan,
                      fontWeight = FontWeight.Bold,
                      fontSize = 9.sp
                    )
                  }
                }
              }

              Spacer(modifier = Modifier.height(2.dp))

              Text(
                text = if (targetCompany.isNotBlank()) "Syncing Dossier: $targetCompany" else "Cloud Intelligence Engine",
                style = MaterialTheme.typography.titleSmall,
                color = TextPrimaryDark,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
            }
          }

          // Percentage Counter Badge
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .background(SlateElevated)
              .border(1.dp, SlateBorder, RoundedCornerShape(8.dp))
              .padding(horizontal = 8.dp, vertical = 4.dp)
          ) {
            Text(
              text = "${(animatedProgress * 100).toInt()}%",
              style = MaterialTheme.typography.labelMedium,
              color = if (animatedProgress >= 1f) TitanEmerald else TitanCyan,
              fontWeight = FontWeight.Bold,
              fontFamily = FontFamily.Monospace,
              fontSize = 11.sp
            )
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Animated Smooth Progress Bar
        LinearProgressIndicator(
          progress = { animatedProgress },
          modifier = Modifier
            .fillMaxWidth()
            .height(5.dp)
            .clip(RoundedCornerShape(3.dp)),
          color = TitanCyan,
          trackColor = SlateBorder.copy(alpha = 0.5f)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Multi-Step Phase Indicators
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          PhaseStepIndicator(
            label = "Handshake",
            isReached = animatedProgress >= 0.15f,
            isActive = animatedProgress in 0.15f..0.39f,
            modifier = Modifier.weight(1f)
          )
          PhaseStepIndicator(
            label = "Firestore Query",
            isReached = animatedProgress >= 0.40f,
            isActive = animatedProgress in 0.40f..0.69f,
            modifier = Modifier.weight(1f)
          )
          PhaseStepIndicator(
            label = "Dossier Signals",
            isReached = animatedProgress >= 0.70f,
            isActive = animatedProgress in 0.70f..0.89f,
            modifier = Modifier.weight(1f)
          )
          PhaseStepIndicator(
            label = "Verified",
            isReached = animatedProgress >= 0.90f,
            isActive = animatedProgress >= 0.90f,
            modifier = Modifier.weight(1f)
          )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Telemetry Status Message with AnimatedContent Transition
        AnimatedContent(
          targetState = statusMessage,
          transitionSpec = {
            (slideInVertically { height -> height } + fadeIn())
              .togetherWith(slideOutVertically { height -> -height } + fadeOut())
          },
          label = "sync_status_text"
        ) { message ->
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = if (isSyncing) Icons.Default.Sensors else Icons.Default.CheckCircle,
              contentDescription = null,
              tint = if (isSyncing) TitanCyan else TitanEmerald,
              modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = message,
              style = MaterialTheme.typography.bodySmall,
              color = TextSecondaryDark,
              fontSize = 11.sp,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
          }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Technical Telemetry Footer (Collection, Security, Mode)
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Hub, contentDescription = null, tint = TextMutedDark, modifier = Modifier.size(11.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "collection: users/{id}/company_intelligence",
              style = MaterialTheme.typography.labelSmall,
              color = TextMutedDark,
              fontSize = 9.sp,
              fontFamily = FontFamily.Monospace
            )
          }

          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Shield, contentDescription = null, tint = TitanEmerald, modifier = Modifier.size(10.dp))
            Spacer(modifier = Modifier.width(3.dp))
            Text(
              text = "AES-256 Cloud Vault",
              style = MaterialTheme.typography.labelSmall,
              color = TitanEmerald,
              fontSize = 9.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }
    }
  }
}

/**
 * Compact animated sync badge suitable for screen headers, toolbars, and tab rows.
 */
@Composable
fun CompanyIntelFirestoreSyncBadge(
  isSyncing: Boolean,
  targetCompany: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val infiniteTransition = rememberInfiniteTransition(label = "badge_sync_spin")
  val rotation by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 360f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 1500, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "badge_rotation"
  )

  val pulseAlpha by infiniteTransition.animateFloat(
    initialValue = 0.4f,
    targetValue = 1.0f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "badge_pulse"
  )

  Box(
    modifier = modifier
      .testTag("firestore_company_intel_sync_badge")
      .clip(RoundedCornerShape(8.dp))
      .background(
        if (isSyncing) TitanCyan.copy(alpha = 0.15f)
        else SlateElevated
      )
      .border(
        width = 1.dp,
        color = if (isSyncing) TitanCyan.copy(alpha = 0.5f) else SlateBorder,
        shape = RoundedCornerShape(8.dp)
      )
      .clickable(onClick = onClick)
      .padding(horizontal = 9.dp, vertical = 5.dp)
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Center
    ) {
      if (isSyncing) {
        Icon(
          imageVector = Icons.Default.Sync,
          contentDescription = "Syncing from Firestore",
          tint = TitanCyan,
          modifier = Modifier
            .size(13.dp)
            .rotate(rotation)
        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(
          text = "FETCHING FIRESTORE...",
          style = MaterialTheme.typography.labelSmall,
          color = TitanCyan,
          fontWeight = FontWeight.Black,
          fontSize = 9.sp
        )
      } else {
        Box(
          modifier = Modifier
            .size(6.dp)
            .clip(CircleShape)
            .background(TitanEmerald)
        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(
          text = "FIRESTORE SYNC",
          style = MaterialTheme.typography.labelSmall,
          color = TextSecondaryDark,
          fontWeight = FontWeight.Bold,
          fontSize = 9.sp
        )
        Spacer(modifier = Modifier.width(4.dp))
        Icon(
          imageVector = Icons.Default.Refresh,
          contentDescription = "Refresh from Firestore",
          tint = TextMutedDark,
          modifier = Modifier.size(11.dp)
        )
      }
    }
  }
}

@Composable
private fun PhaseStepIndicator(
  label: String,
  isReached: Boolean,
  isActive: Boolean,
  modifier: Modifier = Modifier
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = modifier
  ) {
    Box(
      modifier = Modifier
        .size(6.dp)
        .clip(CircleShape)
        .background(
          when {
            isActive -> TitanCyan
            isReached -> TitanEmerald
            else -> SlateBorder
          }
        )
    )
    Spacer(modifier = Modifier.height(2.dp))
    Text(
      text = label,
      style = MaterialTheme.typography.labelSmall,
      color = when {
        isActive -> TitanCyan
        isReached -> TitanEmerald
        else -> TextMutedDark
      },
      fontWeight = if (isActive || isReached) FontWeight.Bold else FontWeight.Normal,
      fontSize = 8.sp,
      maxLines = 1,
      overflow = TextOverflow.Ellipsis
    )
  }
}
