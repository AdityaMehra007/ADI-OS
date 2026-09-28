package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.work.WorkInfo
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateCard
import com.example.ui.theme.SlateElevated
import com.example.ui.theme.TextMutedDark
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import com.example.ui.theme.TitanCrimson
import com.example.ui.theme.TitanCyan
import com.example.ui.theme.TitanEmerald
import com.example.ui.theme.TitanGold
import com.example.ui.theme.TitanIndigo
import com.example.ui.viewmodel.TitanScreen
import com.example.ui.viewmodel.TitanViewModel

/**
 * Executive dashboard card displaying the WorkManager Batch Job Processor status.
 * Periodically synchronizes job application statuses across Zepto, Razorpay, Blinkit,
 * Instawork, and other target companies to ensure candidate dashboards and career
 * telemetry ledgers remain consistently fresh.
 */
@Composable
fun BatchJobSyncStatusCard(
  viewModel: TitanViewModel,
  modifier: Modifier = Modifier
) {
  val applications by viewModel.applications.collectAsState()
  val workInfoList by viewModel.batchJobWorkInfo.collectAsState()
  val latestWorkInfo = workInfoList.firstOrNull()
  val isRunning = latestWorkInfo?.state == WorkInfo.State.RUNNING
  val isEnqueued = latestWorkInfo?.state == WorkInfo.State.ENQUEUED
  val isSucceeded = latestWorkInfo?.state == WorkInfo.State.SUCCEEDED
  val isBlocked = latestWorkInfo?.state == WorkInfo.State.BLOCKED
  val isFailed = latestWorkInfo?.state == WorkInfo.State.FAILED

  val targetCompanies = applications.map { it.companyName }.distinct()
  var isSyncTriggering by remember { mutableStateOf(false) }

  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("batch_job_sync_card"),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = SlateCard),
    border = BorderStroke(1.dp, if (isRunning) TitanCyan.copy(alpha = 0.6f) else SlateBorder)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp)
    ) {
      // Header: WorkManager Badge & Worker Status Pill
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(34.dp)
              .clip(RoundedCornerShape(8.dp))
              .background(TitanCyan.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.CloudSync,
              contentDescription = "Batch WorkManager",
              tint = TitanCyan,
              modifier = Modifier.size(18.dp)
            )
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "BATCH JOB PROCESSOR",
                style = MaterialTheme.typography.labelSmall,
                color = TitanCyan,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp
              )
              Spacer(modifier = Modifier.width(6.dp))
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(4.dp))
                  .background(TitanIndigo.copy(alpha = 0.3f))
                  .padding(horizontal = 5.dp, vertical = 1.dp)
              ) {
                Text(
                  text = "WORKMANAGER",
                  style = MaterialTheme.typography.labelSmall,
                  color = TitanCyan,
                  fontSize = 8.sp,
                  fontWeight = FontWeight.ExtraBold
                )
              }
            }
            Text(
              text = "Multi-Company Pipeline Sync",
              style = MaterialTheme.typography.bodySmall,
              fontWeight = FontWeight.SemiBold,
              color = TextPrimaryDark
            )
          }
        }

        // Live Worker Status Badge
        val statusText = when {
          isRunning || isSyncTriggering -> "RUNNING"
          isEnqueued -> "SCHEDULED (4H)"
          isSucceeded -> "ACTIVE"
          isBlocked -> "WAITING"
          isFailed -> "RETRYING"
          else -> "STANDBY"
        }
        val statusColor = when {
          isRunning || isSyncTriggering -> TitanCyan
          isEnqueued || isSucceeded -> TitanEmerald
          isBlocked -> TitanGold
          isFailed -> TitanCrimson
          else -> TitanCyan
        }

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(statusColor.copy(alpha = 0.15f))
            .border(1.dp, statusColor.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(statusColor)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
              text = statusText,
              style = MaterialTheme.typography.labelSmall,
              color = statusColor,
              fontWeight = FontWeight.Bold,
              fontSize = 9.sp
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      Text(
        text = "Periodic WorkManager daemon synchronizes application statuses and updates career telemetry across all targeted tech enterprises without requiring active screen time.",
        style = MaterialTheme.typography.bodySmall,
        color = TextSecondaryDark,
        fontSize = 11.sp,
        lineHeight = 16.sp
      )

      Spacer(modifier = Modifier.height(10.dp))

      // Execution constraints & telemetry chips
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        // Chip 1: Network Constraint
        Row(
          modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(6.dp))
            .background(SlateElevated)
            .padding(horizontal = 8.dp, vertical = 6.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.Wifi,
            contentDescription = null,
            tint = TitanCyan,
            modifier = Modifier.size(12.dp)
          )
          Spacer(modifier = Modifier.width(5.dp))
          Text(
            text = "Network: Connected",
            style = MaterialTheme.typography.labelSmall,
            color = TextPrimaryDark,
            fontSize = 9.5.sp,
            maxLines = 1
          )
        }

        // Chip 2: Battery Constraint
        Row(
          modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(6.dp))
            .background(SlateElevated)
            .padding(horizontal = 8.dp, vertical = 6.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.BatteryChargingFull,
            contentDescription = null,
            tint = TitanEmerald,
            modifier = Modifier.size(12.dp)
          )
          Spacer(modifier = Modifier.width(5.dp))
          Text(
            text = "Battery: >20%",
            style = MaterialTheme.typography.labelSmall,
            color = TextPrimaryDark,
            fontSize = 9.5.sp,
            maxLines = 1
          )
        }

        // Chip 3: Cadence
        Row(
          modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(6.dp))
            .background(SlateElevated)
            .padding(horizontal = 8.dp, vertical = 6.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.Schedule,
            contentDescription = null,
            tint = TitanGold,
            modifier = Modifier.size(12.dp)
          )
          Spacer(modifier = Modifier.width(5.dp))
          Text(
            text = "Every 4 Hours",
            style = MaterialTheme.typography.labelSmall,
            color = TextPrimaryDark,
            fontSize = 9.5.sp,
            maxLines = 1
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Tracked Target Companies Pill Row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "TRACKED TARGET COMPANIES (${targetCompanies.size}):",
          style = MaterialTheme.typography.labelSmall,
          color = TextMutedDark,
          fontSize = 9.sp,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = "${applications.size} Pipeline Applications",
          style = MaterialTheme.typography.labelSmall,
          color = TitanCyan,
          fontSize = 9.sp,
          fontWeight = FontWeight.SemiBold
        )
      }

      Spacer(modifier = Modifier.height(6.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        val sampleDisplayCompanies = if (targetCompanies.isNotEmpty()) targetCompanies.take(4) else listOf("Zepto", "Razorpay", "Blinkit", "Instawork")
        sampleDisplayCompanies.forEach { company ->
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(SlateElevated)
              .border(1.dp, SlateBorder, RoundedCornerShape(6.dp))
              .padding(horizontal = 8.dp, vertical = 4.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = TitanEmerald,
                modifier = Modifier.size(10.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = company,
                style = MaterialTheme.typography.labelSmall,
                color = TextPrimaryDark,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Action Buttons
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Button(
          onClick = {
            isSyncTriggering = true
            viewModel.triggerBatchJobSync()
          },
          modifier = Modifier
            .weight(1f)
            .testTag("batch_job_sync_now_button"),
          colors = ButtonDefaults.buttonColors(
            containerColor = TitanCyan,
            contentColor = Color.Black
          ),
          shape = RoundedCornerShape(8.dp)
        ) {
          if (isRunning || isSyncTriggering) {
            CircularProgressIndicator(
              modifier = Modifier.size(14.dp),
              strokeWidth = 2.dp,
              color = Color.Black
            )
          } else {
            Icon(
              imageVector = Icons.Default.Refresh,
              contentDescription = "Sync Now",
              modifier = Modifier.size(14.dp)
            )
          }
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = if (isRunning || isSyncTriggering) "Syncing Batch..." else "Sync Statuses Now",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
          )
        }

        Button(
          onClick = { viewModel.navigateTo(TitanScreen.APPLICATIONS) },
          modifier = Modifier
            .weight(1f)
            .testTag("batch_job_view_pipeline_button"),
          colors = ButtonDefaults.buttonColors(
            containerColor = SlateElevated,
            contentColor = TitanCyan
          ),
          border = BorderStroke(1.dp, TitanCyan.copy(alpha = 0.4f)),
          shape = RoundedCornerShape(8.dp)
        ) {
          Text(
            text = "Pipeline CRM",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
          )
          Spacer(modifier = Modifier.width(4.dp))
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            modifier = Modifier.size(14.dp)
          )
        }
      }
    }
  }
}
