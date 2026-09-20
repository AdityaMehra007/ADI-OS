package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.TravelExplore
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.example.data.model.QuickActionType
import com.example.ui.theme.ObsidianDark
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateCard
import com.example.ui.theme.SlateElevated
import com.example.ui.theme.TextMutedDark
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TitanCyan
import com.example.ui.theme.TitanEmerald
import com.example.ui.theme.TitanGold
import com.example.ui.theme.TitanViolet

/**
 * High-craft Speed-Dial Floating Action Button that provides 1-tap triggers
 * for predefined automation tasks ('Sync Company Intel', 'Summarize Career Note', etc.).
 */
@Composable
fun QuickActionFloatingButton(
  isExecuting: Boolean,
  executingAction: QuickActionType?,
  onTriggerAction: (QuickActionType) -> Unit,
  modifier: Modifier = Modifier
) {
  var isExpanded by remember { mutableStateOf(false) }

  val infiniteTransition = rememberInfiniteTransition(label = "quickActionTransition")
  val pulseScale by infiniteTransition.animateFloat(
    initialValue = 1.0f,
    targetValue = 1.08f,
    animationSpec = infiniteRepeatable(
      animation = tween(1400, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "pulseScale"
  )
  val haloRotation by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 360f,
    animationSpec = infiniteRepeatable(
      animation = tween(6000, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "haloRotation"
  )

  Box(
    modifier = modifier,
    contentAlignment = Alignment.BottomEnd
  ) {
    // 1. Speed Dial Popup / Panel
    if (isExpanded) {
      Popup(
        alignment = Alignment.BottomEnd,
        offset = androidx.compose.ui.unit.IntOffset(0, -180),
        onDismissRequest = { isExpanded = false },
        properties = PopupProperties(focusable = true, dismissOnClickOutside = true)
      ) {
        Surface(
          shape = RoundedCornerShape(16.dp),
          color = ObsidianDark,
          border = androidx.compose.foundation.BorderStroke(1.dp, TitanCyan.copy(alpha = 0.5f)),
          shadowElevation = 12.dp,
          modifier = Modifier
            .widthIn(max = 380.dp)
            .padding(16.dp)
            .testTag("quick_action_menu_popup")
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(14.dp)
          ) {
            // Header
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(TitanCyan.copy(alpha = 0.2f)),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    Icons.Default.Bolt,
                    contentDescription = null,
                    tint = TitanCyan,
                    modifier = Modifier.size(16.dp)
                  )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                  Text(
                    text = "Quick Automations",
                    style = MaterialTheme.typography.titleSmall,
                    color = TextPrimaryDark,
                    fontWeight = FontWeight.Bold
                  )
                  Text(
                    text = "Instant 1-tap agent triggers",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMutedDark,
                    fontSize = 10.sp
                  )
                }
              }

              IconButton(
                onClick = { isExpanded = false },
                modifier = Modifier
                  .size(28.dp)
                  .testTag("close_quick_action_menu_btn")
              ) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMutedDark, modifier = Modifier.size(16.dp))
              }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Items
            QuickActionItemRow(
              title = "Sync Company Intel",
              description = "Fetch live Firestore dossier & market news for Zepto",
              badge = "FIRESTORE",
              accentColor = TitanCyan,
              icon = Icons.Default.CloudSync,
              testTag = "quick_action_sync_intel",
              isRunning = isExecuting && executingAction == QuickActionType.SYNC_COMPANY_INTEL,
              onClick = {
                isExpanded = false
                onTriggerAction(QuickActionType.SYNC_COMPANY_INTEL)
              }
            )

            Spacer(modifier = Modifier.height(8.dp))

            QuickActionItemRow(
              title = "Summarize Career Note",
              description = "Synthesize strategic memo into executive battle-plan using AI",
              badge = "GEMINI AI",
              accentColor = TitanGold,
              icon = Icons.Default.AutoAwesome,
              testTag = "quick_action_summarize_note",
              isRunning = isExecuting && executingAction == QuickActionType.SUMMARIZE_CAREER_NOTE,
              onClick = {
                isExpanded = false
                onTriggerAction(QuickActionType.SUMMARIZE_CAREER_NOTE)
              }
            )

            Spacer(modifier = Modifier.height(8.dp))

            QuickActionItemRow(
              title = "Run Job Discovery Radar",
              description = "Crawl active tech roles in Bengaluru & compute ATS match scores",
              badge = "CRAWLER",
              accentColor = TitanEmerald,
              icon = Icons.Default.TravelExplore,
              testTag = "quick_action_run_radar",
              isRunning = isExecuting && executingAction == QuickActionType.RUN_JOB_RADAR,
              onClick = {
                isExpanded = false
                onTriggerAction(QuickActionType.RUN_JOB_RADAR)
              }
            )

            Spacer(modifier = Modifier.height(8.dp))

            QuickActionItemRow(
              title = "Audit Career Health",
              description = "Run weekly application momentum & readiness audit",
              badge = "TELEMETRY",
              accentColor = TitanViolet,
              icon = Icons.AutoMirrored.Filled.TrendingUp,
              testTag = "quick_action_audit_health",
              isRunning = isExecuting && executingAction == QuickActionType.AUDIT_CAREER_HEALTH,
              onClick = {
                isExpanded = false
                onTriggerAction(QuickActionType.AUDIT_CAREER_HEALTH)
              }
            )
          }
        }
      }
    }

    // 2. The Primary Floating Action Button
    FloatingActionButton(
      onClick = { isExpanded = !isExpanded },
      containerColor = ObsidianDark,
      contentColor = TitanCyan,
      shape = RoundedCornerShape(28.dp),
      modifier = Modifier
        .border(
          width = 1.5.dp,
          brush = Brush.sweepGradient(
            colors = listOf(
              TitanCyan,
              TitanGold,
              TitanCyan.copy(alpha = 0.3f),
              TitanCyan
            )
          ),
          shape = RoundedCornerShape(28.dp)
        )
        .testTag("quick_action_fab")
    ) {
      Row(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        if (isExecuting) {
          CircularProgressIndicator(
            color = TitanCyan,
            modifier = Modifier.size(18.dp),
            strokeWidth = 2.dp
          )
        } else {
          Box(
            modifier = Modifier
              .scale(pulseScale)
              .rotate(if (isExpanded) 45f else 0f),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = if (isExpanded) Icons.Default.Close else Icons.Default.Bolt,
              contentDescription = "Quick Action",
              tint = if (isExpanded) TitanGold else TitanCyan,
              modifier = Modifier.size(20.dp)
            )
          }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Text(
          text = if (isExecuting) "Running..." else "Quick Action",
          color = TextPrimaryDark,
          fontWeight = FontWeight.Bold,
          fontSize = 13.sp
        )

        Spacer(modifier = Modifier.width(6.dp))

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(TitanCyan.copy(alpha = 0.2f))
            .padding(horizontal = 5.dp, vertical = 2.dp)
        ) {
          Text(
            text = "AUTO",
            color = TitanCyan,
            fontSize = 9.sp,
            fontWeight = FontWeight.ExtraBold
          )
        }
      }
    }
  }
}

@Composable
private fun QuickActionItemRow(
  title: String,
  description: String,
  badge: String,
  accentColor: Color,
  icon: ImageVector,
  testTag: String,
  isRunning: Boolean,
  onClick: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(10.dp),
    colors = CardDefaults.cardColors(containerColor = SlateCard),
    border = androidx.compose.foundation.BorderStroke(1.dp, accentColor.copy(alpha = 0.4f)),
    modifier = Modifier
      .fillMaxWidth()
      .clickable(onClick = onClick)
      .testTag(testTag)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(10.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(36.dp)
          .clip(CircleShape)
          .background(accentColor.copy(alpha = 0.15f))
          .border(1.dp, accentColor.copy(alpha = 0.35f), CircleShape),
        contentAlignment = Alignment.Center
      ) {
        if (isRunning) {
          CircularProgressIndicator(
            color = accentColor,
            modifier = Modifier.size(18.dp),
            strokeWidth = 2.dp
          )
        } else {
          Icon(
            imageVector = icon,
            contentDescription = null,
            tint = accentColor,
            modifier = Modifier.size(18.dp)
          )
        }
      }

      Spacer(modifier = Modifier.width(10.dp))

      Column(modifier = Modifier.weight(1f)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            color = TextPrimaryDark,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
          )

          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(4.dp))
              .background(accentColor.copy(alpha = 0.2f))
              .padding(horizontal = 6.dp, vertical = 2.dp)
          ) {
            Text(
              text = badge,
              color = accentColor,
              fontSize = 9.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }

        Spacer(modifier = Modifier.height(2.dp))

        Text(
          text = description,
          style = MaterialTheme.typography.bodySmall,
          color = TextMutedDark,
          fontSize = 10.sp,
          maxLines = 2
        )
      }

      Spacer(modifier = Modifier.width(8.dp))

      Icon(
        imageVector = Icons.Default.PlayArrow,
        contentDescription = "Trigger Now",
        tint = accentColor,
        modifier = Modifier.size(16.dp)
      )
    }
  }
}
