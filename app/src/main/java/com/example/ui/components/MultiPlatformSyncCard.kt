package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Application
import com.example.data.model.JobPlatform
import com.example.data.model.PlatformApplicationStatus
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
import com.example.ui.theme.TitanViolet
import com.example.ui.viewmodel.TitanViewModel

@Composable
fun MultiPlatformSyncCard(
  viewModel: TitanViewModel,
  modifier: Modifier = Modifier,
  initialExpanded: Boolean = false
) {
  val haptic = LocalHapticFeedback.current
  val isSyncing by viewModel.isPlatformSyncRunning.collectAsState()
  val lastSyncTimestamp by viewModel.lastPlatformSyncTimestamp.collectAsState()
  val lastResult by viewModel.lastPlatformSyncResult.collectAsState()
  val totalCycles by viewModel.totalPlatformSyncCycles.collectAsState()
  val daemonConfig by viewModel.platformSyncDaemonConfig.collectAsState()
  val platformHealthMap by viewModel.platformHealthMap.collectAsState()
  val recentTransitions by viewModel.recentPlatformStatusTransitions.collectAsState()
  val applications by viewModel.applications.collectAsState()

  var isExpanded by remember { mutableStateOf(initialExpanded) }
  var showSimulatorDialog by remember { mutableStateOf(false) }

  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("multi_platform_sync_card"),
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = SlateCard),
    border = BorderStroke(1.dp, if (isSyncing) TitanCyan.copy(alpha = 0.6f) else SlateBorder)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp)
    ) {
      // -----------------------------------------------------------------------
      // Top Status Bar: Title, Live Daemon Pill, and Actions
      // -----------------------------------------------------------------------
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(if (daemonConfig.isPeriodicEnabled) TitanEmerald else TitanGold)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = if (daemonConfig.isPeriodicEnabled) "BACKGROUND DAEMON ACTIVE • ${daemonConfig.intervalMinutes}M ALARM" else "PERIODIC DAEMON PAUSED",
              style = MaterialTheme.typography.labelSmall,
              color = if (daemonConfig.isPeriodicEnabled) TitanEmerald else TitanGold,
              fontWeight = FontWeight.Bold,
              letterSpacing = 0.8.sp,
              fontSize = 10.sp
            )
          }
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = "Multi-Platform Sync & ATS Tracker",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = TextPrimaryDark
          )
          Text(
            text = "Background daemon syncing saved listings & candidate stages",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondaryDark,
            fontSize = 11.sp
          )
        }

        Row(
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Manual Trigger Button
          Button(
            onClick = {
              haptic.performHapticFeedback(HapticFeedbackType.LongPress)
              viewModel.triggerMultiPlatformSync("USER_DASHBOARD_TAP")
            },
            enabled = !isSyncing,
            colors = ButtonDefaults.buttonColors(
              containerColor = TitanCyan,
              contentColor = Color.Black
            ),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
              .height(36.dp)
              .testTag("btn_sync_platforms_now")
          ) {
            if (isSyncing) {
              CircularProgressIndicator(
                modifier = Modifier.size(14.dp),
                color = Color.Black,
                strokeWidth = 2.dp
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text("Syncing...", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            } else {
              Icon(Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(14.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Sync Now", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
          }

          // Expand / Collapse Toggle
          IconButton(
            onClick = { isExpanded = !isExpanded },
            modifier = Modifier.size(36.dp)
          ) {
            Icon(
              imageVector = if (isExpanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
              contentDescription = if (isExpanded) "Collapse" else "Expand",
              tint = TitanCyan
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // -----------------------------------------------------------------------
      // Connected Platforms Horizontal Hub Row
      // -----------------------------------------------------------------------
      val scrollState = rememberScrollState()
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(scrollState),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        JobPlatform.entries.forEach { platform ->
          val health = platformHealthMap[platform]
          val savedCount = health?.savedJobsCount ?: 3
          val appsCount = health?.monitoredApplicationsCount ?: 1

          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .background(SlateElevated)
              .border(1.dp, platform.getBrandColor().copy(alpha = 0.3f), RoundedCornerShape(8.dp))
              .padding(horizontal = 10.dp, vertical = 6.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(text = platform.logoEmoji, fontSize = 14.sp)
              Spacer(modifier = Modifier.width(6.dp))
              Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(
                    text = platform.displayName,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextPrimaryDark,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                  )
                  Spacer(modifier = Modifier.width(4.dp))
                  Box(
                    modifier = Modifier
                      .size(6.dp)
                      .clip(CircleShape)
                      .background(TitanEmerald)
                  )
                }
                Text(
                  text = "$savedCount saved • $appsCount apps",
                  style = MaterialTheme.typography.bodySmall,
                  color = TextMutedDark,
                  fontSize = 10.sp
                )
              }
            }
          }
        }
      }

      // -----------------------------------------------------------------------
      // Live Status Transitions & Daemon Controls (Expandable Body)
      // -----------------------------------------------------------------------
      AnimatedVisibility(
        visible = isExpanded,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
      ) {
        Column(modifier = Modifier.padding(top = 14.dp)) {
          // Daemon Polling Settings Card
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(8.dp))
              .background(SlateElevated.copy(alpha = 0.6f))
              .border(1.dp, SlateBorder, RoundedCornerShape(8.dp))
              .padding(12.dp)
          ) {
            Column {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    text = "Background Periodic Alarm Daemon",
                    style = MaterialTheme.typography.titleSmall,
                    color = TextPrimaryDark,
                    fontWeight = FontWeight.Bold
                  )
                  Text(
                    text = "Uses Android AlarmManager & Foreground Service to sync while app is closed",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMutedDark,
                    fontSize = 11.sp
                  )
                }

                Switch(
                  checked = daemonConfig.isPeriodicEnabled,
                  onCheckedChange = { isEnabled ->
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    if (isEnabled) {
                      viewModel.startPlatformPeriodicSync(daemonConfig.intervalMinutes)
                    } else {
                      viewModel.stopPlatformPeriodicSync()
                    }
                  },
                  colors = SwitchDefaults.colors(
                    checkedThumbColor = TitanCyan,
                    checkedTrackColor = TitanCyan.copy(alpha = 0.3f),
                    uncheckedThumbColor = TextMutedDark,
                    uncheckedTrackColor = SlateCard
                  )
                )
              }

              if (daemonConfig.isPeriodicEnabled) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                  text = "Sync Frequency:",
                  style = MaterialTheme.typography.labelSmall,
                  color = TextSecondaryDark
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                  horizontalArrangement = Arrangement.spacedBy(6.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  listOf(15, 30, 60, 180, 360).forEach { mins ->
                    val isSelected = daemonConfig.intervalMinutes == mins
                    FilterChip(
                      selected = isSelected,
                      onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        viewModel.startPlatformPeriodicSync(mins)
                      },
                      label = {
                        Text(
                          text = if (mins >= 60) "${mins / 60}h" else "${mins}m",
                          fontSize = 10.sp,
                          fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                      },
                      colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = TitanCyan.copy(alpha = 0.2f),
                        selectedLabelColor = TitanCyan,
                        containerColor = SlateCard,
                        labelColor = TextSecondaryDark
                      ),
                      border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isSelected,
                        borderColor = if (isSelected) TitanCyan else SlateBorder
                      ),
                      modifier = Modifier.height(28.dp)
                    )
                  }
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // -------------------------------------------------------------------
          // Detected ATS Stage Movements Section
          // -------------------------------------------------------------------
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.NotificationsActive,
                contentDescription = null,
                tint = TitanEmerald,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "Detected ATS Stage Transitions (${recentTransitions.size})",
                style = MaterialTheme.typography.titleSmall,
                color = TextPrimaryDark,
                fontWeight = FontWeight.Bold
              )
            }

            // Quick Simulate Button for Testing
            TextButton(
              onClick = { showSimulatorDialog = !showSimulatorDialog }
            ) {
              Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = TitanGold, modifier = Modifier.size(12.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Simulate ATS Update", color = TitanGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
          }

          if (showSimulatorDialog && applications.isNotEmpty()) {
            SimulatorTestBox(
              applications = applications,
              onSimulate = { appId, status, note ->
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                viewModel.simulatePlatformAtsTransition(appId, status, note)
                showSimulatorDialog = false
              },
              onDismiss = { showSimulatorDialog = false }
            )
            Spacer(modifier = Modifier.height(10.dp))
          }

          if (recentTransitions.isEmpty()) {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(SlateElevated)
                .padding(14.dp),
              contentAlignment = Alignment.Center
            ) {
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = TitanCyan, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = "All applications synced with ATS portals",
                  style = MaterialTheme.typography.bodySmall,
                  color = TextPrimaryDark,
                  fontWeight = FontWeight.Medium
                )
                Text(
                  text = "Tap 'Sync Now' or 'Simulate ATS Update' to trigger live candidate progression detection.",
                  style = MaterialTheme.typography.labelSmall,
                  color = TextMutedDark,
                  fontSize = 10.sp
                )
              }
            }
          } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
              recentTransitions.take(4).forEach { transition ->
                TransitionEventItem(transition = transition)
              }
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // -------------------------------------------------------------------
          // Telemetry Bar (Last Sync, Cycle Count, Discovered Metrics)
          // -------------------------------------------------------------------
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(6.dp))
              .background(SlateElevated)
              .padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Schedule, contentDescription = null, tint = TextMutedDark, modifier = Modifier.size(12.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "Last Synced: ${lastSyncTimestamp ?: "Awaiting first run"}",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondaryDark,
                fontSize = 10.sp
              )
            }

            Text(
              text = "Total Sync Cycles: $totalCycles",
              style = MaterialTheme.typography.labelSmall,
              color = TitanCyan,
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }
    }
  }
}

@Composable
private fun TransitionEventItem(transition: PlatformApplicationStatus) {
  val statusColor = when (transition.currentStatus) {
    "OFFER" -> TitanGold
    "INTERVIEW", "FINAL_ROUND" -> TitanEmerald
    "ASSESSMENT" -> TitanCyan
    "VIEWED" -> TitanViolet
    "REJECTED" -> TitanCrimson
    else -> TitanIndigo
  }

  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(8.dp))
      .background(SlateElevated)
      .border(1.dp, statusColor.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
      .padding(10.dp)
  ) {
    Column {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(text = transition.platform.logoEmoji, fontSize = 12.sp)
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = transition.platform.displayName,
            style = MaterialTheme.typography.labelSmall,
            color = transition.platform.getBrandColor(),
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp
          )
        }

        Text(
          text = transition.formattedCheckTime,
          style = MaterialTheme.typography.labelSmall,
          color = TextMutedDark,
          fontSize = 9.sp
        )
      }

      Spacer(modifier = Modifier.height(4.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = "${transition.companyName} • ${transition.roleTitle}",
            style = MaterialTheme.typography.titleSmall,
            color = TextPrimaryDark,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
          )
          Text(
            text = transition.platformStageName,
            style = MaterialTheme.typography.bodySmall,
            color = statusColor,
            fontWeight = FontWeight.SemiBold,
            fontSize = 11.sp
          )
        }

        // Status Transition Pill
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(statusColor.copy(alpha = 0.15f))
            .padding(horizontal = 6.dp, vertical = 3.dp)
        ) {
          if (transition.previousStatus != null) {
            Text(
              text = transition.previousStatus,
              fontSize = 9.sp,
              color = TextMutedDark,
              fontWeight = FontWeight.Medium
            )
            Icon(
              imageVector = Icons.Default.SwapHoriz,
              contentDescription = null,
              tint = statusColor,
              modifier = Modifier
                .padding(horizontal = 2.dp)
                .size(10.dp)
            )
          }
          Text(
            text = transition.currentStatus,
            fontSize = 10.sp,
            color = statusColor,
            fontWeight = FontWeight.Bold
          )
        }
      }

      if (transition.transitionNotes.isNotBlank()) {
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = transition.transitionNotes,
          style = MaterialTheme.typography.bodySmall,
          color = TextSecondaryDark,
          fontSize = 10.sp
        )
      }

      transition.actionRequired?.let { action ->
        Spacer(modifier = Modifier.height(4.dp))
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(TitanGold.copy(alpha = 0.1f))
            .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
          Text(
            text = "⚡ Action: $action",
            style = MaterialTheme.typography.labelSmall,
            color = TitanGold,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }
    }
  }
}

@Composable
private fun SimulatorTestBox(
  applications: List<Application>,
  onSimulate: (String, String, String) -> Unit,
  onDismiss: () -> Unit
) {
  var selectedAppIndex by remember { mutableStateOf(0) }
  var targetStatus by remember { mutableStateOf("INTERVIEW") }

  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(8.dp))
      .background(SlateCard)
      .border(1.dp, TitanGold.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
      .padding(12.dp)
  ) {
    Column {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "TEST SIMULATE ATS STAGE ADVANCEMENT",
          style = MaterialTheme.typography.labelSmall,
          color = TitanGold,
          fontWeight = FontWeight.Bold,
          fontSize = 10.sp
        )
        Text(
          text = "Cancel",
          style = MaterialTheme.typography.labelSmall,
          color = TextMutedDark,
          modifier = Modifier.clickable { onDismiss() }
        )
      }

      Spacer(modifier = Modifier.height(6.dp))

      Text(
        text = "Select Application:",
        style = MaterialTheme.typography.bodySmall,
        color = TextSecondaryDark,
        fontSize = 10.sp
      )
      Spacer(modifier = Modifier.height(4.dp))

      val targetApp = applications.getOrNull(selectedAppIndex) ?: applications.first()

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        applications.take(4).forEachIndexed { idx, app ->
          val isSel = idx == selectedAppIndex
          FilterChip(
            selected = isSel,
            onClick = { selectedAppIndex = idx },
            label = { Text("${app.companyName} (${app.status})", fontSize = 10.sp) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = TitanCyan.copy(alpha = 0.2f),
              selectedLabelColor = TitanCyan,
              containerColor = SlateElevated,
              labelColor = TextSecondaryDark
            ),
            modifier = Modifier.height(28.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = "Advance Stage To:",
        style = MaterialTheme.typography.bodySmall,
        color = TextSecondaryDark,
        fontSize = 10.sp
      )
      Spacer(modifier = Modifier.height(4.dp))

      Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        listOf("ASSESSMENT", "INTERVIEW", "FINAL_ROUND", "OFFER").forEach { st ->
          val isSel = targetStatus == st
          FilterChip(
            selected = isSel,
            onClick = { targetStatus = st },
            label = { Text(st, fontSize = 10.sp, fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = TitanEmerald.copy(alpha = 0.2f),
              selectedLabelColor = TitanEmerald,
              containerColor = SlateElevated,
              labelColor = TextSecondaryDark
            ),
            modifier = Modifier.height(28.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      Button(
        onClick = {
          onSimulate(
            targetApp.id,
            targetStatus,
            "ATS stage progression verified on portal. Candidate invited for $targetStatus."
          )
        },
        colors = ButtonDefaults.buttonColors(containerColor = TitanGold, contentColor = Color.Black),
        shape = RoundedCornerShape(6.dp),
        modifier = Modifier
          .fillMaxWidth()
          .height(34.dp)
      ) {
        Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(14.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text("Trigger Progression Event Now", fontSize = 11.sp, fontWeight = FontWeight.Bold)
      }
    }
  }
}
