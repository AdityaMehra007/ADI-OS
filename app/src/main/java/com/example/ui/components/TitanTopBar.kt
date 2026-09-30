package com.example.ui.components

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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import com.example.auth.TitanAuthUser
import com.example.data.model.UserProfile
import com.example.ui.theme.ObsidianDark
import com.example.ui.theme.TitanTheme
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

@Composable
fun TitanTopBar(
  userProfile: UserProfile?,
  authUser: TitanAuthUser? = null,
  notificationUnreadCount: Int = 0,
  onToggleBrutalMode: () -> Unit,
  onAutomationModeClick: () -> Unit,
  onSearchClick: () -> Unit,
  onNotificationClick: () -> Unit = {},
  onAuthClick: () -> Unit = {}
) {
  val brutalMode = userProfile?.brutalStrategyMode ?: false
  val careerScore = userProfile?.careerScore ?: 88
  val aiLeverage = userProfile?.aiLeverageScore ?: 92
  val autoMode = userProfile?.automationMode ?: "APPROVAL"

  Surface(
    color = ObsidianDark,
    modifier = Modifier
      .fillMaxWidth()
      .statusBarsPadding()
      .testTag("titan_top_bar")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
      // Tier 1: Brand Identifier & Primary Action Controls
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Logo and System Monogram
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(32.dp)
              .clip(RoundedCornerShape(8.dp))
              .background(if (brutalMode) TitanCrimson else TitanCyan)
              .border(1.dp, SlateBorder, RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = if (brutalMode) "⚡" else "A",
              color = ObsidianDark,
              fontWeight = FontWeight.Black,
              fontSize = 16.sp
            )
          }

          Spacer(modifier = Modifier.width(8.dp))

          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "ADI OS",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black,
                color = TextPrimaryDark,
                letterSpacing = 0.5.sp,
                fontSize = 15.sp
              )
              Spacer(modifier = Modifier.width(5.dp))
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(4.dp))
                  .background(if (brutalMode) TitanCrimson.copy(alpha = 0.2f) else TitanCyan.copy(alpha = 0.15f))
                  .padding(horizontal = 5.dp, vertical = 1.5.dp)
              ) {
                Text(
                  text = if (brutalMode) "NO-BS MODE" else "PROJECT TITAN",
                  style = MaterialTheme.typography.labelSmall,
                  color = if (brutalMode) TitanCrimson else TitanCyan,
                  fontWeight = FontWeight.Bold,
                  fontSize = 8.5.sp
                )
              }
            }
            Text(
              text = "Personal Executive Command Center",
              style = MaterialTheme.typography.bodySmall,
              color = TextMutedDark,
              fontSize = 9.sp
            )
          }
        }

        // Action Cluster: Account Sync, Notifications, HUD Search, Brutal Switch
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
          // Google Sign-In / Firebase Vault Account Button
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .background(SlateElevated)
              .border(
                1.dp,
                if (authUser != null) TitanEmerald.copy(alpha = 0.6f) else TitanCyan.copy(alpha = 0.5f),
                RoundedCornerShape(8.dp)
              )
              .clickable { onAuthClick() }
              .padding(horizontal = 6.dp, vertical = 4.dp)
              .testTag("top_bar_account_button")
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              if (authUser != null) {
                Box(
                  modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(TitanEmerald),
                  contentAlignment = Alignment.Center
                ) {
                  Text(
                    text = authUser.displayName?.take(1)?.uppercase() ?: "A",
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Black,
                    color = ObsidianDark
                  )
                }
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                  text = "SYNC",
                  style = MaterialTheme.typography.labelSmall,
                  fontWeight = FontWeight.Bold,
                  color = TitanEmerald,
                  fontSize = 8.5.sp
                )
              } else {
                Text(
                  text = "G",
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Black,
                  color = Color(0xFF4285F4)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                  text = "Cloud",
                  style = MaterialTheme.typography.labelSmall,
                  fontWeight = FontWeight.Bold,
                  color = TitanCyan,
                  fontSize = 8.5.sp
                )
              }
            }
          }

          // Notification Bell Center Button
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .background(SlateElevated)
              .border(
                1.dp,
                if (notificationUnreadCount > 0) TitanGold.copy(alpha = 0.8f) else SlateBorder,
                RoundedCornerShape(8.dp)
              )
              .clickable { onNotificationClick() }
              .padding(horizontal = 6.dp, vertical = 4.dp)
              .testTag("top_bar_notifications_button")
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = if (notificationUnreadCount > 0) Icons.Default.NotificationsActive else Icons.Default.Notifications,
                contentDescription = "Alert Command Center",
                tint = if (notificationUnreadCount > 0) TitanGold else TextSecondaryDark,
                modifier = Modifier.size(13.dp)
              )
              if (notificationUnreadCount > 0) {
                Spacer(modifier = Modifier.width(3.dp))
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(TitanCrimson)
                    .padding(horizontal = 3.dp, vertical = 0.5.dp)
                ) {
                  Text(
                    text = "$notificationUnreadCount",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    fontSize = 8.sp
                  )
                }
              }
            }
          }

          // Command Palette Search Button
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .background(SlateElevated)
              .border(1.dp, TitanCyan.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
              .clickable { onSearchClick() }
              .padding(horizontal = 6.dp, vertical = 4.dp)
              .testTag("top_bar_search_button")
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search / Command Palette",
                tint = TitanCyan,
                modifier = Modifier.size(12.dp)
              )
              Spacer(modifier = Modifier.width(3.dp))
              Text(
                text = "HUD",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Black,
                color = TitanCyan,
                fontSize = 8.5.sp
              )
            }
          }

          // Brutal Strategy Mode Switch
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .background(if (brutalMode) TitanCrimson.copy(alpha = 0.15f) else SlateCard)
              .border(1.dp, if (brutalMode) TitanCrimson else SlateBorder, RoundedCornerShape(8.dp))
              .clickable { onToggleBrutalMode() }
              .padding(horizontal = 6.dp, vertical = 4.dp)
              .testTag("brutal_mode_toggle")
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = if (brutalMode) Icons.Default.Warning else Icons.Default.Shield,
                contentDescription = "Brutal Mode",
                tint = if (brutalMode) TitanCrimson else TextSecondaryDark,
                modifier = Modifier.size(12.dp)
              )
              Spacer(modifier = Modifier.width(3.dp))
              Text(
                text = if (brutalMode) "NO-BS" else "NORMAL",
                style = MaterialTheme.typography.labelSmall,
                color = if (brutalMode) TitanCrimson else TextSecondaryDark,
                fontWeight = FontWeight.Bold,
                fontSize = 8.5.sp
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(6.dp))

      // Tier 2: Real-time Telemetry & Mode Governance Strip
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(8.dp))
          .background(SlateCard)
          .border(1.dp, SlateBorder, RoundedCornerShape(8.dp))
          .padding(horizontal = 10.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Career Score Pill
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.Bolt,
            contentDescription = "Career Score",
            tint = TitanGold,
            modifier = Modifier.size(13.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "CAREER $careerScore/100",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = TitanGold,
            fontSize = 9.sp
          )
        }

        // AI Leverage Pill
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.Psychology,
            contentDescription = "AI Leverage",
            tint = TitanCyan,
            modifier = Modifier.size(13.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "AI $aiLeverage%",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = TitanCyan,
            fontSize = 9.sp
          )
        }

        // Automation Mode Pill
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(SlateElevated)
            .border(1.dp, SlateBorder, RoundedCornerShape(6.dp))
            .clickable { onAutomationModeClick() }
            .padding(horizontal = 6.dp, vertical = 2.dp)
            .testTag("automation_mode_button")
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(if (autoMode == "AUTO") TitanEmerald else TitanCyan)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "MODE: $autoMode",
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.Bold,
              color = if (autoMode == "AUTO") TitanEmerald else TitanCyan,
              fontSize = 9.sp
            )
          }
        }
      }
    }
  }
}

@androidx.compose.ui.tooling.preview.Preview(name = "Titan Top Bar - Obsidian Dark")
@Composable
fun TitanTopBarPreview() {
  TitanTheme {
    TitanTopBar(
      userProfile = null,
      authUser = null,
      notificationUnreadCount = 3,
      onToggleBrutalMode = {},
      onAutomationModeClick = {},
      onSearchClick = {},
      onNotificationClick = {},
      onAuthClick = {}
    )
  }
}
