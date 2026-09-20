package com.example.ui.components

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.LinkOff
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LinkedInConnectionStatus
import com.example.data.model.LinkedInExperience
import com.example.data.model.LinkedInNetworkActivityItem
import com.example.data.model.LinkedInNetworkContact
import com.example.data.model.LinkedInProfile
import com.example.data.model.LinkedInSkill
import com.example.data.model.LinkedInSyncTelemetry
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
import com.example.ui.viewmodel.TitanViewModel

private val LinkedInBrandBlue = Color(0xFF0A66C2)

/**
 * UI Component for LinkedIn API & Firebase Auth Integration.
 * Allows users to link LinkedIn, review live status, and automatically
 * ingest professional history, skills, and network activity into Adi OS.
 */
@Composable
fun LinkedInIntegrationView(
  viewModel: TitanViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val activity = context as? Activity

  val connectionStatus by viewModel.linkedInConnectionStatus.collectAsState()
  val profile by viewModel.linkedInProfile.collectAsState()
  val experiences by viewModel.linkedInImportedExperiences.collectAsState()
  val skills by viewModel.linkedInImportedSkills.collectAsState()
  val activities by viewModel.linkedInNetworkActivities.collectAsState()
  val contacts by viewModel.linkedInNetworkContacts.collectAsState()
  val telemetry by viewModel.linkedInSyncTelemetry.collectAsState()
  val isSyncing by viewModel.isLinkedInSyncing.collectAsState()
  val errorMessage by viewModel.linkedInErrorMessage.collectAsState()

  // Background Periodic Service States
  val isPeriodicSyncActive by viewModel.isLinkedInPeriodicSyncActive.collectAsState()
  val lastBgSyncTime by viewModel.lastLinkedInBackgroundSyncTime.collectAsState()
  val totalSyncCycles by viewModel.linkedInTotalSyncCycles.collectAsState()
  val isBgSyncRunning by viewModel.isLinkedInBackgroundSyncRunning.collectAsState()

  var selectedTab by remember { mutableStateOf(0) } // 0: History, 1: Network, 2: Telemetry, 3: Daemon

  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("linkedin_integration_card"),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = SlateCard),
    border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      // 1. Header with LinkedIn Badge & Status
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Box(
            modifier = Modifier
              .size(38.dp)
              .clip(RoundedCornerShape(8.dp))
              .background(LinkedInBrandBlue),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "in",
              color = Color.White,
              fontWeight = FontWeight.Bold,
              fontSize = 22.sp
            )
          }

          Column {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              Text(
                text = "LINKEDIN SYNC SERVICE",
                style = MaterialTheme.typography.labelSmall,
                color = LinkedInBrandBlue,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.1.sp
              )
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(4.dp))
                  .background(TitanIndigo.copy(alpha = 0.2f))
                  .border(0.5.dp, TitanIndigo, RoundedCornerShape(4.dp))
                  .padding(horizontal = 4.dp, vertical = 2.dp)
              ) {
                Text(
                  text = "Firebase Auth",
                  style = MaterialTheme.typography.labelSmall,
                  fontSize = 8.sp,
                  color = TitanCyan
                )
              }
            }
            Text(
              text = "Professional History & Network",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = TextPrimaryDark
            )
          }
        }

        // Connection State Pill
        val (statusText, statusColor) = when (connectionStatus) {
          LinkedInConnectionStatus.CONNECTED,
          LinkedInConnectionStatus.SYNC_SUCCESS -> "Connected" to TitanEmerald
          LinkedInConnectionStatus.SYNCING -> "Syncing..." to TitanCyan
          LinkedInConnectionStatus.CONNECTING -> "Authorizing..." to TitanGold
          LinkedInConnectionStatus.ERROR -> "Sync Error" to TitanCrimson
          LinkedInConnectionStatus.DISCONNECTED -> "Disconnected" to TextMutedDark
        }

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(statusColor.copy(alpha = 0.15f))
            .border(1.dp, statusColor.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
          .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            Box(
              modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(statusColor)
            )
            Text(
              text = statusText,
              style = MaterialTheme.typography.labelSmall,
              fontSize = 10.sp,
              color = statusColor,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }

      // Error banner if any
      errorMessage?.let { msg ->
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(TitanCrimson.copy(alpha = 0.12f))
            .border(1.dp, TitanCrimson.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
            .padding(10.dp)
        ) {
          Text(
            text = "Notice: $msg",
            style = MaterialTheme.typography.bodySmall,
            color = TitanCrimson,
            fontSize = 11.sp
          )
        }
      }

      // 2. Profile Card or Connect Call-to-Action
      if (connectionStatus == LinkedInConnectionStatus.DISCONNECTED || profile == null) {
        DisconnectedPrompt(
          isConnecting = connectionStatus == LinkedInConnectionStatus.CONNECTING,
          onConnect = {
            if (activity != null) {
              viewModel.connectWithLinkedIn(activity)
            }
          }
        )
      } else {
        ConnectedProfileBanner(
          profile = profile!!,
          isSyncing = isSyncing,
          onSyncNow = { viewModel.syncLinkedInData(forceRefresh = true) },
          onDisconnect = { viewModel.disconnectLinkedIn() }
        )
      }

      // 3. Tab Navigation (History, Network, Telemetry)
      if (profile != null || connectionStatus != LinkedInConnectionStatus.DISCONNECTED) {
        TabRow(
          selectedTabIndex = selectedTab,
          containerColor = SlateElevated,
          contentColor = TitanCyan,
          indicator = { tabPositions ->
            TabRowDefaults.SecondaryIndicator(
              Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
              color = TitanCyan
            )
          },
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, SlateBorder, RoundedCornerShape(8.dp))
        ) {
          Tab(
            selected = selectedTab == 0,
            onClick = { selectedTab = 0 },
            modifier = Modifier.testTag("linkedin_tab_history"),
            text = {
              Text(
                "History (${experiences.size})",
                fontSize = 11.sp,
                fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                color = if (selectedTab == 0) TitanCyan else TextSecondaryDark
              )
            }
          )
          Tab(
            selected = selectedTab == 1,
            onClick = { selectedTab = 1 },
            modifier = Modifier.testTag("linkedin_tab_network"),
            text = {
              Text(
                "Network (${activities.size})",
                fontSize = 11.sp,
                fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                color = if (selectedTab == 1) TitanCyan else TextSecondaryDark
              )
            }
          )
          Tab(
            selected = selectedTab == 2,
            onClick = { selectedTab = 2 },
            modifier = Modifier.testTag("linkedin_tab_telemetry"),
            text = {
              Text(
                "Telemetry",
                fontSize = 11.sp,
                fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal,
                color = if (selectedTab == 2) TitanCyan else TextSecondaryDark
              )
            }
          )
          Tab(
            selected = selectedTab == 3,
            onClick = { selectedTab = 3 },
            modifier = Modifier.testTag("linkedin_tab_daemon"),
            text = {
              Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                if (isPeriodicSyncActive) {
                  Box(
                    modifier = Modifier
                      .size(6.dp)
                      .clip(CircleShape)
                      .background(TitanEmerald)
                  )
                }
                Text(
                  "Daemon",
                  fontSize = 11.sp,
                  fontWeight = if (selectedTab == 3) FontWeight.Bold else FontWeight.Normal,
                  color = if (selectedTab == 3) TitanCyan else TextSecondaryDark
                )
              }
            }
          )
        }

        // Tab Content
        when (selectedTab) {
          0 -> ProfessionalHistoryTab(experiences, skills)
          1 -> NetworkActivityTab(
            activities = activities,
            contacts = contacts,
            onImportContact = { contact -> viewModel.importLinkedInContactToCrm(contact) }
          )
          2 -> SyncTelemetryTab(telemetry)
          3 -> BackgroundDaemonTab(
            viewModel = viewModel,
            isPeriodicActive = isPeriodicSyncActive,
            lastBgSyncTime = lastBgSyncTime,
            totalSyncCycles = totalSyncCycles,
            isBgSyncRunning = isBgSyncRunning
          )
        }
      }
    }
  }
}

@Composable
private fun DisconnectedPrompt(
  isConnecting: Boolean,
  onConnect: () -> Unit
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(10.dp))
      .background(SlateElevated)
      .border(1.dp, SlateBorder, RoundedCornerShape(10.dp))
      .padding(14.dp)
  ) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
      Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Icon(Icons.Default.Security, contentDescription = null, tint = TitanCyan, modifier = Modifier.size(16.dp))
        Text(
          text = "Zero-Trust Enterprise OAuth2 Linking",
          style = MaterialTheme.typography.labelSmall,
          color = TitanCyan,
          fontWeight = FontWeight.Bold
        )
      }

      Text(
        text = "Securely authenticate your LinkedIn account via Firebase Auth to automatically import verified employment history, endorsed skills, and executive network activity into Adi OS.",
        style = MaterialTheme.typography.bodySmall,
        color = TextSecondaryDark,
        fontSize = 12.sp,
        lineHeight = 17.sp
      )

      Button(
        onClick = onConnect,
        enabled = !isConnecting,
        colors = ButtonDefaults.buttonColors(containerColor = LinkedInBrandBlue),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("linkedin_connect_button")
      ) {
        if (isConnecting) {
          CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
          Spacer(modifier = Modifier.width(8.dp))
          Text("Connecting with Firebase Auth...", color = Color.White, fontWeight = FontWeight.Bold)
        } else {
          Icon(Icons.Default.Link, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text("Connect with LinkedIn", color = Color.White, fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}

@Composable
private fun ConnectedProfileBanner(
  profile: LinkedInProfile,
  isSyncing: Boolean,
  onSyncNow: () -> Unit,
  onDisconnect: () -> Unit
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(10.dp))
      .background(SlateElevated)
      .border(1.dp, SlateBorder, RoundedCornerShape(10.dp))
      .padding(12.dp)
  ) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(10.dp),
          modifier = Modifier.weight(1f)
        ) {
          Box(
            modifier = Modifier
              .size(44.dp)
              .clip(CircleShape)
              .background(LinkedInBrandBlue),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = profile.firstName.take(1) + profile.lastName.take(1),
              color = Color.White,
              fontWeight = FontWeight.Bold,
              fontSize = 16.sp
            )
          }

          Column {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
              Text(
                text = profile.fullName,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = TextPrimaryDark
              )
              Icon(Icons.Default.Verified, contentDescription = "Verified", tint = LinkedInBrandBlue, modifier = Modifier.size(14.dp))
            }
            Text(
              text = profile.headline,
              style = MaterialTheme.typography.bodySmall,
              color = TextSecondaryDark,
              fontSize = 11.sp,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
            Text(
              text = "${profile.location} • ${profile.connectionsCount}+ connections",
              style = MaterialTheme.typography.bodySmall,
              color = TextMutedDark,
              fontSize = 10.sp
            )
          }
        }
      }

      // Actions row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Button(
          onClick = onSyncNow,
          enabled = !isSyncing,
          colors = ButtonDefaults.buttonColors(containerColor = SlateCard),
          border = androidx.compose.foundation.BorderStroke(1.dp, TitanCyan.copy(alpha = 0.5f)),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier
            .weight(1f)
            .testTag("linkedin_sync_button")
        ) {
          if (isSyncing) {
            CircularProgressIndicator(modifier = Modifier.size(14.dp), color = TitanCyan, strokeWidth = 2.dp)
            Spacer(modifier = Modifier.width(6.dp))
            Text("Importing...", color = TitanCyan, fontSize = 12.sp)
          } else {
            Icon(Icons.Default.Refresh, contentDescription = null, tint = TitanCyan, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Sync History & Network", color = TitanCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
          }
        }

        OutlinedButton(
          onClick = onDisconnect,
          shape = RoundedCornerShape(8.dp),
          colors = ButtonDefaults.outlinedButtonColors(contentColor = TextMutedDark),
          border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder),
          modifier = Modifier.testTag("linkedin_disconnect_button")
        ) {
          Icon(Icons.Default.LinkOff, contentDescription = null, modifier = Modifier.size(14.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Unlink", fontSize = 11.sp)
        }
      }
    }
  }
}

@Composable
private fun ProfessionalHistoryTab(
  experiences: List<LinkedInExperience>,
  skills: List<LinkedInSkill>
) {
  Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "IMPORTED POSITIONS (${experiences.size})",
        style = MaterialTheme.typography.labelSmall,
        color = TitanGold,
        fontWeight = FontWeight.Bold
      )
      Text(
        text = "Auto-Added to Verified Facts",
        style = MaterialTheme.typography.labelSmall,
        fontSize = 9.sp,
        color = TitanEmerald
      )
    }

    experiences.forEach { exp ->
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(8.dp))
          .background(SlateElevated)
          .border(1.dp, SlateBorder, RoundedCornerShape(8.dp))
          .padding(12.dp)
      ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = exp.title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimaryDark
              )
              Text(
                text = "${exp.companyName} • ${exp.employmentType}",
                style = MaterialTheme.typography.bodySmall,
                color = TitanCyan,
                fontSize = 11.sp
              )
            }

            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(if (exp.isCurrent) TitanEmerald.copy(alpha = 0.2f) else SlateCard)
                .border(0.5.dp, if (exp.isCurrent) TitanEmerald else SlateBorder, RoundedCornerShape(4.dp))
                .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
              Text(
                text = if (exp.isCurrent) "Current" else "${exp.startDate} - ${exp.endDate}",
                style = MaterialTheme.typography.labelSmall,
                fontSize = 9.sp,
                color = if (exp.isCurrent) TitanEmerald else TextMutedDark
              )
            }
          }

          Text(
            text = exp.description,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondaryDark,
            fontSize = 11.sp,
            lineHeight = 16.sp
          )

          // Key metrics chips
          if (exp.keyImpactMetrics.isNotEmpty()) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              exp.keyImpactMetrics.take(2).forEach { metric ->
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(SlateCard)
                    .border(0.5.dp, SlateBorder, RoundedCornerShape(4.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                  Text(
                    text = "• $metric",
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 9.sp,
                    color = TitanGold
                  )
                }
              }
            }
          }
        }
      }
    }

    // Skills Section
    Spacer(modifier = Modifier.height(4.dp))
    Text(
      text = "LINKEDIN ENDORSED SKILLS (${skills.size})",
      style = MaterialTheme.typography.labelSmall,
      color = TitanCyan,
      fontWeight = FontWeight.Bold
    )

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      skills.take(3).forEach { skill ->
        Box(
          modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(6.dp))
            .background(SlateElevated)
            .border(1.dp, SlateBorder, RoundedCornerShape(6.dp))
            .padding(8.dp)
        ) {
          Column {
            Text(
              text = skill.name,
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.Bold,
              color = TextPrimaryDark,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = "${skill.endorsementCount} endorsements",
              style = MaterialTheme.typography.labelSmall,
              fontSize = 8.sp,
              color = TitanCyan
            )
          }
        }
      }
    }
  }
}

@Composable
private fun NetworkActivityTab(
  activities: List<LinkedInNetworkActivityItem>,
  contacts: List<LinkedInNetworkContact>,
  onImportContact: (LinkedInNetworkContact) -> Unit
) {
  Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
    Text(
      text = "LIVE NETWORK & RECRUITER STREAM",
      style = MaterialTheme.typography.labelSmall,
      color = TitanCyan,
      fontWeight = FontWeight.Bold
    )

    activities.forEach { act ->
      val (badgeText, badgeColor) = when (act.activityType) {
        "RECRUITER_INMAIL" -> "Recruiter InMail" to TitanGold
        "CONNECTION_REQUEST" -> "Connection" to TitanCyan
        "POST_ENGAGEMENT" -> "Engagement" to TitanIndigo
        else -> "Network Activity" to TextMutedDark
      }

      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(8.dp))
          .background(SlateElevated)
          .border(1.dp, SlateBorder, RoundedCornerShape(8.dp))
          .padding(12.dp)
      ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(4.dp))
                  .background(badgeColor.copy(alpha = 0.2f))
                  .border(0.5.dp, badgeColor, RoundedCornerShape(4.dp))
                  .padding(horizontal = 6.dp, vertical = 2.dp)
              ) {
                Text(
                  text = badgeText,
                  style = MaterialTheme.typography.labelSmall,
                  fontSize = 8.sp,
                  color = badgeColor,
                  fontWeight = FontWeight.Bold
                )
              }

              Text(
                text = "${act.actorName} (${act.actorCompany})",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = TextPrimaryDark
              )
            }

            Text(
              text = act.timeFormatted,
              style = MaterialTheme.typography.labelSmall,
              fontSize = 9.sp,
              color = TextMutedDark
            )
          }

          Text(
            text = act.content,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondaryDark,
            fontSize = 11.sp,
            lineHeight = 16.sp
          )

          act.recommendedAction?.let { action ->
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(4.dp))
                .background(SlateCard)
                .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
              Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = TitanGold, modifier = Modifier.size(10.dp))
                Text(
                  text = "Recommended Action: $action",
                  style = MaterialTheme.typography.labelSmall,
                  fontSize = 9.sp,
                  color = TitanGold
                )
              }
            }
          }
        }
      }
    }

    // Contacts section with 1-click CRM Ingestion
    Spacer(modifier = Modifier.height(4.dp))
    Text(
      text = "RECRUITER CONTACTS HARVESTED (${contacts.size})",
      style = MaterialTheme.typography.labelSmall,
      color = TitanGold,
      fontWeight = FontWeight.Bold
    )

    contacts.forEach { contact ->
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
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              Text(
                text = contact.name,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = TextPrimaryDark
              )
              Text(
                text = "• ${contact.relationshipDegree}",
                style = MaterialTheme.typography.labelSmall,
                fontSize = 9.sp,
                color = TitanCyan
              )
            }
            Text(
              text = contact.headline,
              style = MaterialTheme.typography.labelSmall,
              fontSize = 9.sp,
              color = TextSecondaryDark,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
          }

          if (contact.isImportedToCrm) {
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(TitanEmerald.copy(alpha = 0.2f))
                .padding(horizontal = 6.dp, vertical = 3.dp)
            ) {
              Text(
                text = "In CRM",
                style = MaterialTheme.typography.labelSmall,
                fontSize = 9.sp,
                color = TitanEmerald,
                fontWeight = FontWeight.Bold
              )
            }
          } else {
            Button(
              onClick = { onImportContact(contact) },
              shape = RoundedCornerShape(6.dp),
              colors = ButtonDefaults.buttonColors(containerColor = SlateCard),
              border = androidx.compose.foundation.BorderStroke(1.dp, TitanCyan.copy(alpha = 0.5f)),
              modifier = Modifier.height(28.dp)
            ) {
              Icon(Icons.Default.PersonAdd, contentDescription = null, tint = TitanCyan, modifier = Modifier.size(10.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Add CRM", fontSize = 9.sp, color = TitanCyan)
            }
          }
        }
      }
    }
  }
}

@Composable
private fun SyncTelemetryTab(telemetry: LinkedInSyncTelemetry?) {
  Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
    Text(
      text = "OAUTH2 & CLOUD INGESTION TELEMETRY",
      style = MaterialTheme.typography.labelSmall,
      color = TitanCyan,
      fontWeight = FontWeight.Bold
    )

    if (telemetry == null) {
      Text(
        text = "No sync completed yet in this session. Tap 'Sync History & Network' to execute ingestion.",
        style = MaterialTheme.typography.bodySmall,
        color = TextMutedDark,
        fontSize = 11.sp
      )
    } else {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(8.dp))
          .background(SlateElevated)
          .border(1.dp, SlateBorder, RoundedCornerShape(8.dp))
          .padding(12.dp)
      ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "Sync ID: ${telemetry.syncId.take(16)}...",
              style = MaterialTheme.typography.labelSmall,
              color = TextMutedDark,
              fontSize = 9.sp
            )
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
              Icon(Icons.Default.CloudDone, contentDescription = null, tint = TitanEmerald, modifier = Modifier.size(12.dp))
              Text(
                text = "Firestore Synced",
                style = MaterialTheme.typography.labelSmall,
                fontSize = 9.sp,
                color = TitanEmerald
              )
            }
          }

          // Stats grid
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            MetricTile("Positions", "${telemetry.experiencesCount}", TitanCyan, Modifier.weight(1f))
            MetricTile("Skills", "${telemetry.skillsCount}", TitanGold, Modifier.weight(1f))
            MetricTile("Contacts", "${telemetry.contactsImportedCount}", TitanEmerald, Modifier.weight(1f))
            MetricTile("Latency", "${telemetry.executionDurationMs}ms", TitanIndigo, Modifier.weight(1f))
          }

          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = "Ingested Facts Summary:",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = TextPrimaryDark,
            fontSize = 10.sp
          )

          telemetry.importedFactsSummary.forEach { summaryItem ->
            Text(
              text = "✓ $summaryItem",
              style = MaterialTheme.typography.bodySmall,
              fontSize = 10.sp,
              color = TextSecondaryDark
            )
          }
        }
      }
    }
  }
}

@Composable
private fun MetricTile(
  label: String,
  value: String,
  tint: Color,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(6.dp))
      .background(SlateCard)
      .border(0.5.dp, SlateBorder, RoundedCornerShape(6.dp))
      .padding(8.dp)
  ) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
      Text(text = label, style = MaterialTheme.typography.labelSmall, fontSize = 8.sp, color = TextMutedDark)
      Spacer(modifier = Modifier.height(2.dp))
      Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = tint)
    }
  }
}

@Composable
private fun BackgroundDaemonTab(
  viewModel: TitanViewModel,
  isPeriodicActive: Boolean,
  lastBgSyncTime: String?,
  totalSyncCycles: Int,
  isBgSyncRunning: Boolean
) {
  var selectedIntervalMinutes by remember { mutableStateOf(viewModel.getLinkedInPeriodicIntervalMinutes()) }

  Column(
    modifier = Modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    // 1. Daemon Status Card
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(10.dp))
        .background(SlateElevated)
        .border(1.dp, if (isPeriodicActive) TitanEmerald.copy(alpha = 0.5f) else SlateBorder, RoundedCornerShape(10.dp))
        .padding(14.dp)
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Box(
              modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(if (isPeriodicActive) TitanEmerald.copy(alpha = 0.2f) else SlateCard),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Schedule,
                contentDescription = null,
                tint = if (isPeriodicActive) TitanEmerald else TextMutedDark,
                modifier = Modifier.size(18.dp)
              )
            }
            Column {
              Text(
                text = "BACKGROUND SYNC DAEMON",
                style = MaterialTheme.typography.labelSmall,
                color = if (isPeriodicActive) TitanEmerald else TextMutedDark,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
              )
              Text(
                text = if (isPeriodicActive) "Active & Scheduled" else "Daemon Paused",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimaryDark
              )
            }
          }

          // Active Toggle Switch
          Switch(
            checked = isPeriodicActive,
            onCheckedChange = { enable ->
              if (enable) {
                viewModel.startLinkedInPeriodicSync(selectedIntervalMinutes)
              } else {
                viewModel.stopLinkedInPeriodicSync()
              }
            },
            colors = SwitchDefaults.colors(
              checkedThumbColor = Color.White,
              checkedTrackColor = TitanEmerald,
              uncheckedThumbColor = TextMutedDark,
              uncheckedTrackColor = SlateCard
            ),
            modifier = Modifier.testTag("linkedin_daemon_switch")
          )
        }

        // Metrics Grid
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          MetricTile(
            label = "Last Sync",
            value = lastBgSyncTime?.takeLast(8) ?: "Pending",
            tint = TitanCyan,
            modifier = Modifier.weight(1f)
          )
          MetricTile(
            label = "Interval",
            value = "${selectedIntervalMinutes / 60} hrs",
            tint = TitanGold,
            modifier = Modifier.weight(1f)
          )
          MetricTile(
            label = "Cycles Ran",
            value = "$totalSyncCycles",
            tint = TitanEmerald,
            modifier = Modifier.weight(1f)
          )
          MetricTile(
            label = "Status",
            value = if (isBgSyncRunning) "Syncing" else if (isPeriodicActive) "Armed" else "Idle",
            tint = if (isBgSyncRunning) TitanCyan else if (isPeriodicActive) TitanEmerald else TextMutedDark,
            modifier = Modifier.weight(1f)
          )
        }
      }
    }

    // 2. Frequency Selector
    Text(
      text = "SYNC CADENCE",
      style = MaterialTheme.typography.labelSmall,
      color = TitanCyan,
      fontWeight = FontWeight.Bold,
      letterSpacing = 1.sp
    )

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      listOf(
        60 to "1h Fast",
        360 to "6h Rec.",
        720 to "12h Half",
        1440 to "24h Daily"
      ).forEach { (minutes, label) ->
        val isSelected = selectedIntervalMinutes == minutes
        Box(
          modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) TitanCyan.copy(alpha = 0.2f) else SlateElevated)
            .border(
              1.dp,
              if (isSelected) TitanCyan else SlateBorder,
              RoundedCornerShape(8.dp)
            )
            .clickable {
              selectedIntervalMinutes = minutes
              if (isPeriodicActive) {
                viewModel.startLinkedInPeriodicSync(minutes)
              }
            }
            .padding(vertical = 8.dp),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) TitanCyan else TextSecondaryDark
          )
        }
      }
    }

    // 3. Manual Immediate Execution Button
    Button(
      onClick = {
        viewModel.triggerImmediateLinkedInBackgroundSync(source = "USER_REQUEST_CARD")
      },
      enabled = !isBgSyncRunning,
      colors = ButtonDefaults.buttonColors(containerColor = TitanIndigo),
      shape = RoundedCornerShape(8.dp),
      modifier = Modifier
        .fillMaxWidth()
        .testTag("linkedin_trigger_bg_sync_button")
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        if (isBgSyncRunning) {
          CircularProgressIndicator(
            modifier = Modifier.size(16.dp),
            strokeWidth = 2.dp,
            color = Color.White
          )
          Text("Running Background Ingestion...", fontSize = 12.sp, color = Color.White)
        } else {
          Icon(Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(16.dp))
          Text("Trigger Background Sync Now", fontSize = 12.sp, color = Color.White)
        }
      }
    }

    // 4. Automated Synchronization Capabilities
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(8.dp))
        .background(SlateCard)
        .border(0.5.dp, SlateBorder, RoundedCornerShape(8.dp))
        .padding(12.dp)
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
          text = "DAEMON AUTOMATION SPECIFICATION",
          style = MaterialTheme.typography.labelSmall,
          color = TitanGold,
          fontWeight = FontWeight.Bold,
          fontSize = 9.sp
        )
        Text(
          text = "• Periodically fetches LinkedIn profile positions, verified credentials, and network activity posts in the background.",
          style = MaterialTheme.typography.bodySmall,
          fontSize = 10.sp,
          color = TextSecondaryDark
        )
        Text(
          text = "• Uses AlarmManager RTC_WAKEUP & ForegroundService to ensure execution reliability even when the device is idle.",
          style = MaterialTheme.typography.bodySmall,
          fontSize = 10.sp,
          color = TextSecondaryDark
        )
        Text(
          text = "• Ingests verified facts into Room Database & updates OS Automation Task logs automatically.",
          style = MaterialTheme.typography.bodySmall,
          fontSize = 10.sp,
          color = TextSecondaryDark
        )
        Text(
          text = "• Automatically calculates career score progression and profile completeness metrics.",
          style = MaterialTheme.typography.bodySmall,
          fontSize = 10.sp,
          color = TextSecondaryDark
        )
      }
    }
  }
}
