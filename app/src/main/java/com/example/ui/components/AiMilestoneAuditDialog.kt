package com.example.ui.components

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
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
import com.example.ui.theme.TitanCrimson
import com.example.ui.viewmodel.AiMilestoneAdviceState

@Composable
fun AiMilestoneAuditDialog(
  state: AiMilestoneAdviceState,
  onDismiss: () -> Unit
) {
  Dialog(onDismissRequest = onDismiss) {
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .testTag("ai_milestone_audit_dialog"),
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = SlateCard),
      border = androidx.compose.foundation.BorderStroke(1.dp, TitanCyan.copy(alpha = 0.5f))
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp)
          .verticalScroll(rememberScrollState())
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
                .size(36.dp)
                .background(TitanGold.copy(alpha = 0.2f), RoundedCornerShape(10.dp)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = TitanGold,
                modifier = Modifier.size(20.dp)
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "AI Milestone Pacing Audit",
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimaryDark,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "Gemini 3.5 Strategic Trajectory Calibration",
                style = MaterialTheme.typography.labelSmall,
                color = TitanCyan,
                fontSize = 11.sp
              )
            }
          }

          IconButton(
            onClick = onDismiss,
            modifier = Modifier.testTag("close_audit_dialog")
          ) {
            Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextMutedDark)
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (state.isLoading) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(160.dp),
            contentAlignment = Alignment.Center
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              CircularProgressIndicator(color = TitanCyan, modifier = Modifier.size(36.dp))
              Spacer(modifier = Modifier.height(12.dp))
              Text(
                text = "Calibrating 5-year execution velocity & task pacing...",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondaryDark,
                fontSize = 12.sp
              )
            }
          }
        } else {
          val audit = state.auditResult
          if (audit != null) {
            // Overall Verdict Box
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(ObsidianDark)
                .border(1.dp, SlateBorder, RoundedCornerShape(12.dp))
                .padding(14.dp)
            ) {
              Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(imageVector = Icons.Default.Speed, contentDescription = null, tint = TitanCyan, modifier = Modifier.size(16.dp))
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = "PACING VERDICT & VELOCITY",
                    style = MaterialTheme.typography.labelSmall,
                    color = TitanCyan,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                  )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                  text = audit.overallVerdict,
                  style = MaterialTheme.typography.bodyMedium,
                  color = TextPrimaryDark,
                  fontSize = 13.sp,
                  lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(8.dp))
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(TitanEmerald.copy(alpha = 0.15f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                  Text(
                    text = audit.velocityRate,
                    style = MaterialTheme.typography.labelSmall,
                    color = TitanEmerald,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                  )
                }
              }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Horizon Status Badges
            Text(
              text = "PROJECTED HORIZON READINESS",
              style = MaterialTheme.typography.labelSmall,
              color = TextMutedDark,
              fontWeight = FontWeight.Bold,
              fontSize = 10.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              audit.projectedHorizonStatus.entries.forEach { entry ->
                val h = entry.key
                val status = entry.value
                val bgColor = when (status) {
                  "ON_TRACK" -> TitanEmerald.copy(alpha = 0.15f)
                  "IN_PROGRESS" -> TitanCyan.copy(alpha = 0.15f)
                  "ACTION_REQUIRED" -> TitanCrimson.copy(alpha = 0.15f)
                  else -> SlateElevated
                }
                val textColor = when (status) {
                  "ON_TRACK" -> TitanEmerald
                  "IN_PROGRESS" -> TitanCyan
                  "ACTION_REQUIRED" -> TitanCrimson
                  else -> TextSecondaryDark
                }
                Column(
                  modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(bgColor)
                    .padding(vertical = 6.dp),
                  horizontalAlignment = Alignment.CenterHorizontally
                ) {
                  Text(text = h, style = MaterialTheme.typography.labelMedium, color = textColor, fontWeight = FontWeight.Bold)
                  Text(text = status.replace("_", " ").take(8), style = MaterialTheme.typography.labelSmall, color = textColor, fontSize = 8.5.sp)
                }
              }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Critical Gaps
            if (audit.criticalGaps.isNotEmpty()) {
              Text(
                text = "STRATEGIC GAPS IDENTIFIED",
                style = MaterialTheme.typography.labelSmall,
                color = TitanCrimson,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp
              )
              Spacer(modifier = Modifier.height(6.dp))
              audit.criticalGaps.forEach { gap ->
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 3.dp),
                  verticalAlignment = Alignment.Top
                ) {
                  Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = TitanCrimson, modifier = Modifier.size(14.dp))
                  Spacer(modifier = Modifier.width(8.dp))
                  Text(
                    text = gap,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondaryDark,
                    fontSize = 11.5.sp
                  )
                }
              }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Actionable Recommendations
            if (audit.recommendedPacingActions.isNotEmpty()) {
              Text(
                text = "RECOMMENDED PACING ACCELERATORS",
                style = MaterialTheme.typography.labelSmall,
                color = TitanGold,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp
              )
              Spacer(modifier = Modifier.height(6.dp))
              audit.recommendedPacingActions.forEach { action ->
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 3.dp),
                  verticalAlignment = Alignment.Top
                ) {
                  Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = TitanEmerald, modifier = Modifier.size(14.dp))
                  Spacer(modifier = Modifier.width(8.dp))
                  Text(
                    text = action,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextPrimaryDark,
                    fontSize = 11.5.sp
                  )
                }
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
          onClick = onDismiss,
          colors = ButtonDefaults.buttonColors(containerColor = SlateElevated),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("dismiss_audit_button")
        ) {
          Text(text = "Close Audit", color = TextPrimaryDark)
        }
      }
    }
  }
}
