package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.notification.NotificationAlertItem
import com.example.notification.NotificationPriority
import com.example.notification.NotificationType
import com.example.ui.theme.ObsidianDark
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

@Composable
fun NotificationCenterDialog(
  viewModel: TitanViewModel,
  onDismissRequest: () -> Unit
) {
  val alerts by viewModel.notificationAlerts.collectAsState()
  val unreadCount by viewModel.notificationUnreadCount.collectAsState()
  val preferences by viewModel.notificationPreferences.collectAsState()

  var activeTab by remember { mutableStateOf(0) } // 0 = All, 1 = High-Match Jobs, 2 = Company Intel, 3 = Settings & Test
  var expandedAlertId by remember { mutableStateOf<String?>(null) }

  val filteredAlerts = remember(alerts, activeTab) {
    when (activeTab) {
      1 -> alerts.filter { it.type == NotificationType.HIGH_MATCH_JOB }
      2 -> alerts.filter { it.type == NotificationType.COMPANY_INTEL }
      else -> alerts
    }
  }

  Dialog(
    onDismissRequest = onDismissRequest,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = Modifier
        .fillMaxWidth(0.95f)
        .fillMaxHeight(0.90f)
        .testTag("notification_center_dialog"),
      shape = RoundedCornerShape(20.dp),
      color = ObsidianDark,
      border = androidx.compose.foundation.BorderStroke(1.dp, TitanCyan.copy(alpha = 0.5f))
    ) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(16.dp)
      ) {
        // Top Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(TitanCyan.copy(alpha = 0.15f))
                .border(1.dp, TitanCyan, CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.NotificationsActive,
                contentDescription = null,
                tint = TitanCyan,
                modifier = Modifier.size(20.dp)
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = "ALERT COMMAND CENTER",
                  color = TitanCyan,
                  fontWeight = FontWeight.Black,
                  fontSize = 10.sp,
                  letterSpacing = 1.sp
                )
                if (unreadCount > 0) {
                  Spacer(modifier = Modifier.width(6.dp))
                  Box(
                    modifier = Modifier
                      .clip(RoundedCornerShape(4.dp))
                      .background(TitanCrimson)
                      .padding(horizontal = 5.dp, vertical = 1.dp)
                  ) {
                    Text(
                      text = "$unreadCount NEW",
                      color = Color.White,
                      fontSize = 8.sp,
                      fontWeight = FontWeight.Bold
                    )
                  }
                }
              }
              Text(
                text = "Real-Time High-Match Jobs & Breaking Intel",
                style = MaterialTheme.typography.titleSmall,
                color = TextPrimaryDark,
                fontWeight = FontWeight.Bold
              )
            }
          }

          IconButton(
            onClick = onDismissRequest,
            modifier = Modifier.testTag("close_notification_center_button")
          ) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondaryDark)
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Tabs
        TabRow(
          selectedTabIndex = activeTab,
          containerColor = SlateCard,
          indicator = { tabPositions ->
            if (activeTab in tabPositions.indices) {
              TabRowDefaults.SecondaryIndicator(
                Modifier.tabIndicatorOffset(tabPositions[activeTab]),
                color = TitanCyan
              )
            }
          }
        ) {
          Tab(
            selected = activeTab == 0,
            onClick = { activeTab = 0 },
            text = { Text("All Alerts (${alerts.size})", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (activeTab == 0) TitanCyan else TextSecondaryDark) }
          )
          Tab(
            selected = activeTab == 1,
            onClick = { activeTab = 1 },
            text = { Text("Jobs (${alerts.count { it.type == NotificationType.HIGH_MATCH_JOB }})", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (activeTab == 1) TitanCyan else TextSecondaryDark) }
          )
          Tab(
            selected = activeTab == 2,
            onClick = { activeTab = 2 },
            text = { Text("Intel (${alerts.count { it.type == NotificationType.COMPANY_INTEL }})", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (activeTab == 2) TitanCyan else TextSecondaryDark) }
          )
          Tab(
            selected = activeTab == 3,
            onClick = { activeTab = 3 },
            text = {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Tune, contentDescription = null, tint = if (activeTab == 3) TitanGold else TextSecondaryDark, modifier = Modifier.size(12.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Settings", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (activeTab == 3) TitanGold else TextSecondaryDark)
              }
            }
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (activeTab != 3) {
          // Action row for clear all / mark read
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(6.dp))
                  .background(SlateElevated)
                  .clickable { viewModel.testHighMatchJobNotification() }
                  .padding(horizontal = 8.dp, vertical = 4.dp)
                  .testTag("test_job_alert_button")
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(Icons.Default.Bolt, contentDescription = null, tint = TitanCyan, modifier = Modifier.size(12.dp))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text("Test Job Alert", color = TitanCyan, fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                }
              }

              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(6.dp))
                  .background(SlateElevated)
                  .clickable { viewModel.testCriticalCompanyIntelNotification() }
                  .padding(horizontal = 8.dp, vertical = 4.dp)
                  .testTag("test_intel_alert_button")
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(Icons.Default.Warning, contentDescription = null, tint = TitanGold, modifier = Modifier.size(12.dp))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text("Test Intel Alert", color = TitanGold, fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                }
              }
            }

            Row {
              if (unreadCount > 0) {
                TextButton(
                  onClick = { viewModel.markAllNotificationsAsRead() },
                  modifier = Modifier.padding(0.dp)
                ) {
                  Text("Mark all read", color = TextSecondaryDark, fontSize = 10.sp)
                }
              }
              if (alerts.isNotEmpty()) {
                TextButton(
                  onClick = { viewModel.clearAllNotifications() },
                  modifier = Modifier.padding(0.dp)
                ) {
                  Text("Clear all", color = TitanCrimson, fontSize = 10.sp)
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          // List of Alerts
          if (filteredAlerts.isEmpty()) {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
              contentAlignment = Alignment.Center
            ) {
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                  imageVector = Icons.Default.NotificationsOff,
                  contentDescription = null,
                  tint = TextMutedDark,
                  modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                  text = "No alerts in this category",
                  style = MaterialTheme.typography.bodyMedium,
                  color = TextMutedDark
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                  onClick = { viewModel.testHighMatchJobNotification() },
                  colors = ButtonDefaults.buttonColors(containerColor = SlateElevated),
                  shape = RoundedCornerShape(8.dp)
                ) {
                  Text("Trigger Demo High-Match Alert", color = TitanCyan, fontSize = 11.sp)
                }
              }
            }
          } else {
            LazyColumn(
              modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
              verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              items(filteredAlerts, key = { it.id }) { alert ->
                AlertCard(
                  alert = alert,
                  isExpanded = expandedAlertId == alert.id,
                  onToggleExpand = {
                    expandedAlertId = if (expandedAlertId == alert.id) null else alert.id
                    if (!alert.isRead) {
                      viewModel.markNotificationAsRead(alert.id)
                    }
                  },
                  onActionClick = {
                    viewModel.markNotificationAsRead(alert.id)
                    onDismissRequest()
                    when (alert.targetScreen) {
                      "JOBS" -> viewModel.navigateTo(TitanScreen.JOBS)
                      "COMPANIES" -> {
                        viewModel.navigateTo(TitanScreen.COMPANIES)
                        if (alert.targetItemName.isNotBlank()) {
                          viewModel.selectCompanyForIntel(alert.targetItemName)
                        }
                      }
                      "APPLICATIONS" -> viewModel.navigateTo(TitanScreen.APPLICATIONS)
                    }
                  },
                  onDismiss = { viewModel.clearNotification(alert.id) }
                )
              }
            }
          }
        } else {
          // Settings & Configuration Tab
          NotificationSettingsView(
            viewModel = viewModel,
            modifier = Modifier
              .fillMaxWidth()
              .weight(1f)
          )
        }
      }
    }
  }
}

@Composable
private fun AlertCard(
  alert: NotificationAlertItem,
  isExpanded: Boolean,
  onToggleExpand: () -> Unit,
  onActionClick: () -> Unit,
  onDismiss: () -> Unit
) {
  val borderColor = when (alert.type) {
    NotificationType.HIGH_MATCH_JOB -> TitanCyan.copy(alpha = if (alert.isRead) 0.25f else 0.8f)
    NotificationType.COMPANY_INTEL -> TitanGold.copy(alpha = if (alert.isRead) 0.25f else 0.8f)
    NotificationType.APPLICATION_STATUS -> TitanEmerald.copy(alpha = if (alert.isRead) 0.25f else 0.8f)
    else -> SlateBorder
  }

  val typeColor = when (alert.type) {
    NotificationType.HIGH_MATCH_JOB -> TitanCyan
    NotificationType.COMPANY_INTEL -> TitanGold
    NotificationType.APPLICATION_STATUS -> TitanEmerald
    else -> TextSecondaryDark
  }

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onToggleExpand() }
      .testTag("alert_card_${alert.id}"),
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = if (alert.isRead) SlateCard.copy(alpha = 0.6f) else SlateCard),
    border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
          Box(
            modifier = Modifier
              .size(28.dp)
              .clip(CircleShape)
              .background(typeColor.copy(alpha = 0.15f))
              .border(1.dp, typeColor, CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = when (alert.type) {
                NotificationType.HIGH_MATCH_JOB -> Icons.Default.Work
                NotificationType.COMPANY_INTEL -> Icons.Default.Business
                NotificationType.APPLICATION_STATUS -> Icons.Default.Check
                else -> Icons.Default.Notifications
              },
              contentDescription = null,
              tint = typeColor,
              modifier = Modifier.size(14.dp)
            )
          }
          Spacer(modifier = Modifier.width(8.dp))
          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = when (alert.type) {
                  NotificationType.HIGH_MATCH_JOB -> "HIGH-MATCH OPPORTUNITY"
                  NotificationType.COMPANY_INTEL -> "FOLLOWED COMPANY INTEL"
                  NotificationType.APPLICATION_STATUS -> "APPLICATION PIPELINE"
                  else -> "SYSTEM NOTIFICATION"
                },
                color = typeColor,
                fontWeight = FontWeight.Bold,
                fontSize = 8.5.sp,
                letterSpacing = 0.5.sp
              )
              if (!alert.isRead) {
                Spacer(modifier = Modifier.width(4.dp))
                Box(
                  modifier = Modifier
                    .size(5.dp)
                    .clip(CircleShape)
                    .background(TitanCrimson)
                )
              }
            }
            Text(
              text = alert.title,
              style = MaterialTheme.typography.titleSmall,
              color = TextPrimaryDark,
              fontWeight = FontWeight.Bold,
              fontSize = 12.5.sp
            )
          }
        }

        Text(
          text = alert.timestamp,
          style = MaterialTheme.typography.labelSmall,
          color = TextMutedDark,
          fontSize = 9.sp
        )
      }

      Spacer(modifier = Modifier.height(6.dp))

      Text(
        text = alert.message,
        style = MaterialTheme.typography.bodySmall,
        color = TextSecondaryDark,
        fontSize = 11.sp,
        lineHeight = 15.sp
      )

      if (isExpanded) {
        if (alert.detailedSummary.isNotBlank()) {
          Spacer(modifier = Modifier.height(8.dp))
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(8.dp))
              .background(SlateElevated)
              .padding(8.dp)
          ) {
            Text(
              text = alert.detailedSummary,
              style = MaterialTheme.typography.bodySmall,
              color = TextPrimaryDark,
              fontSize = 10.5.sp,
              lineHeight = 14.5.sp
            )
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          TextButton(
            onClick = onDismiss,
            modifier = Modifier.padding(0.dp)
          ) {
            Text("Dismiss", color = TextMutedDark, fontSize = 10.sp)
          }

          Button(
            onClick = onActionClick,
            colors = ButtonDefaults.buttonColors(containerColor = typeColor),
            shape = RoundedCornerShape(8.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = when (alert.targetScreen) {
                  "JOBS" -> "Open in Jobs"
                  "COMPANIES" -> "Open Company Dossier"
                  "APPLICATIONS" -> "Open Applications"
                  else -> "View"
                },
                color = ObsidianDark,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
              )
              Spacer(modifier = Modifier.width(4.dp))
              Icon(
                Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = ObsidianDark,
                modifier = Modifier.size(12.dp)
              )
            }
          }
        }
      }
    }
  }
}

@Composable
private fun NotificationSettingsView(
  viewModel: TitanViewModel,
  modifier: Modifier = Modifier
) {
  val prefs by viewModel.notificationPreferences.collectAsState()

  LazyColumn(
    modifier = modifier.padding(vertical = 4.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SlateCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text("Master Notifications", color = TextPrimaryDark, fontWeight = FontWeight.Bold, fontSize = 13.sp)
              Text("Enable high-priority status bar and lock-screen alerts", color = TextSecondaryDark, fontSize = 10.sp)
            }
            Switch(
              checked = prefs.notificationsEnabled,
              onCheckedChange = { viewModel.updateNotificationPreferences(prefs.copy(notificationsEnabled = it)) },
              colors = SwitchDefaults.colors(
                checkedThumbColor = TitanCyan,
                checkedTrackColor = TitanCyan.copy(alpha = 0.3f),
                uncheckedThumbColor = TextMutedDark,
                uncheckedTrackColor = SlateElevated
              )
            )
          }
        }
      }
    }

    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SlateCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text("Minimum Job Fit Match Threshold", color = TextPrimaryDark, fontWeight = FontWeight.Bold, fontSize = 13.sp)
              Text("Only notify when match score equals or exceeds threshold", color = TextSecondaryDark, fontSize = 10.sp)
            }
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(TitanCyan.copy(alpha = 0.15f))
                .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
              Text("${prefs.minFitScoreThreshold}%+", color = TitanCyan, fontWeight = FontWeight.Black, fontSize = 12.sp)
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          Slider(
            value = prefs.minFitScoreThreshold.toFloat(),
            onValueChange = { viewModel.updateNotificationPreferences(prefs.copy(minFitScoreThreshold = it.toInt())) },
            valueRange = 70f..95f,
            steps = 4,
            colors = SliderDefaults.colors(
              thumbColor = TitanCyan,
              activeTrackColor = TitanCyan,
              inactiveTrackColor = SlateElevated
            )
          )

          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("70% (Broader)", color = TextMutedDark, fontSize = 9.sp)
            Text("85% (Recommended)", color = TitanCyan, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            Text("95% (Laser)", color = TextMutedDark, fontSize = 9.sp)
          }
        }
      }
    }

    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SlateCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text("Watched / Followed Companies Only", color = TextPrimaryDark, fontWeight = FontWeight.Bold, fontSize = 13.sp)
              Text("Filter breaking intel alerts exclusively to starred companies in watchlist", color = TextSecondaryDark, fontSize = 10.sp)
            }
            Switch(
              checked = prefs.notifyFollowedCompaniesOnly,
              onCheckedChange = { viewModel.updateNotificationPreferences(prefs.copy(notifyFollowedCompaniesOnly = it)) },
              colors = SwitchDefaults.colors(
                checkedThumbColor = TitanGold,
                checkedTrackColor = TitanGold.copy(alpha = 0.3f),
                uncheckedThumbColor = TextMutedDark,
                uncheckedTrackColor = SlateElevated
              )
            )
          }

          HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = SlateBorder)

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text("Breaking Market Radar Intel", color = TextPrimaryDark, fontWeight = FontWeight.Bold, fontSize = 13.sp)
              Text("Notify on funding rounds, leadership changes & strategic shifts", color = TextSecondaryDark, fontSize = 10.sp)
            }
            Switch(
              checked = prefs.notifyOnCriticalNews,
              onCheckedChange = { viewModel.updateNotificationPreferences(prefs.copy(notifyOnCriticalNews = it)) },
              colors = SwitchDefaults.colors(
                checkedThumbColor = TitanGold,
                checkedTrackColor = TitanGold.copy(alpha = 0.3f),
                uncheckedThumbColor = TextMutedDark,
                uncheckedTrackColor = SlateElevated
              )
            )
          }

          HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = SlateBorder)

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text("Sound & Vibration Pulse", color = TextPrimaryDark, fontWeight = FontWeight.Bold, fontSize = 13.sp)
              Text("Tactile alert pattern for S-tier opportunities", color = TextSecondaryDark, fontSize = 10.sp)
            }
            Switch(
              checked = prefs.soundAndVibration,
              onCheckedChange = { viewModel.updateNotificationPreferences(prefs.copy(soundAndVibration = it)) },
              colors = SwitchDefaults.colors(
                checkedThumbColor = TitanEmerald,
                checkedTrackColor = TitanEmerald.copy(alpha = 0.3f),
                uncheckedThumbColor = TextMutedDark,
                uncheckedTrackColor = SlateElevated
              )
            )
          }
        }
      }
    }

    item {
      // Diagnostic & Test Trigger Section
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SlateElevated),
        border = androidx.compose.foundation.BorderStroke(1.dp, TitanCyan.copy(alpha = 0.4f))
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = TitanCyan, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Notification Diagnostic Triggers", color = TitanCyan, fontWeight = FontWeight.Bold, fontSize = 12.5.sp)
          }
          Spacer(modifier = Modifier.height(4.dp))
          Text("Instantly test local notification delivery to status bar and notification shade.", color = TextSecondaryDark, fontSize = 10.sp)

          Spacer(modifier = Modifier.height(10.dp))

          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
              onClick = { viewModel.testHighMatchJobNotification() },
              modifier = Modifier.weight(1f),
              colors = ButtonDefaults.buttonColors(containerColor = TitanCyan),
              shape = RoundedCornerShape(8.dp)
            ) {
              Text("Fire Job Alert", color = ObsidianDark, fontWeight = FontWeight.Bold, fontSize = 10.5.sp)
            }

            Button(
              onClick = { viewModel.testCriticalCompanyIntelNotification() },
              modifier = Modifier.weight(1f),
              colors = ButtonDefaults.buttonColors(containerColor = TitanGold),
              shape = RoundedCornerShape(8.dp)
            ) {
              Text("Fire Intel Alert", color = ObsidianDark, fontWeight = FontWeight.Bold, fontSize = 10.5.sp)
            }
          }
        }
      }
    }
  }
}
