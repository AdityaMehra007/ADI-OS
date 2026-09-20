package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.QuickActionExecutionTelemetry
import com.example.ui.theme.ObsidianDark
import com.example.ui.theme.TextMutedDark
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TitanCyan
import com.example.ui.theme.TitanEmerald
import com.example.ui.theme.TitanGold

/**
 * Animated feedback notification banner that displays the live status
 * and telemetry of an executed Quick Action automation.
 */
@Composable
fun QuickActionFeedbackBanner(
  telemetry: QuickActionExecutionTelemetry?,
  onDismiss: () -> Unit,
  modifier: Modifier = Modifier
) {
  AnimatedVisibility(
    visible = telemetry != null,
    enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
    exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
    modifier = modifier
  ) {
    if (telemetry != null) {
      val isSuccess = telemetry.status == "SUCCESS"
      val isRunning = telemetry.status == "RUNNING"
      val accentColor = when {
        isRunning -> TitanCyan
        isSuccess -> TitanEmerald
        else -> TitanGold
      }

      Surface(
        shape = RoundedCornerShape(12.dp),
        color = ObsidianDark,
        border = androidx.compose.foundation.BorderStroke(1.dp, accentColor.copy(alpha = 0.6f)),
        shadowElevation = 8.dp,
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 8.dp)
          .testTag("quick_action_feedback_banner")
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 10.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
          ) {
            Box(
              modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(accentColor.copy(alpha = 0.15f)),
              contentAlignment = Alignment.Center
            ) {
              if (isRunning) {
                CircularProgressIndicator(
                  color = accentColor,
                  strokeWidth = 2.dp,
                  modifier = Modifier.size(16.dp)
                )
              } else if (isSuccess) {
                Icon(
                  imageVector = Icons.Default.CheckCircle,
                  contentDescription = null,
                  tint = TitanEmerald,
                  modifier = Modifier.size(16.dp)
                )
              } else {
                Icon(
                  imageVector = Icons.Default.Warning,
                  contentDescription = null,
                  tint = TitanGold,
                  modifier = Modifier.size(16.dp)
                )
              }
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = telemetry.taskName,
                  style = MaterialTheme.typography.bodyMedium,
                  color = TextPrimaryDark,
                  fontWeight = FontWeight.Bold,
                  fontSize = 12.sp
                )
                if (telemetry.durationMs > 0) {
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = "(${telemetry.durationMs}ms)",
                    color = accentColor,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold
                  )
                }
              }
              Text(
                text = telemetry.message,
                style = MaterialTheme.typography.bodySmall,
                color = TextMutedDark,
                fontSize = 11.sp,
                maxLines = 1
              )
            }
          }

          IconButton(
            onClick = onDismiss,
            modifier = Modifier.size(24.dp)
          ) {
            Icon(Icons.Default.Close, contentDescription = "Dismiss", tint = TextMutedDark, modifier = Modifier.size(14.dp))
          }
        }
      }
    }
  }
}
