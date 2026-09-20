package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ObsidianDark
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateCard
import com.example.ui.theme.SlateElevated
import com.example.ui.theme.TextMutedDark
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TitanCyan
import com.example.ui.theme.TitanEmerald
import com.example.ui.theme.TitanGold
import com.example.ui.viewmodel.TaskLogsViewModel
import com.example.ui.viewmodel.TitanViewModel

private enum class JobAutomationSubTab(val title: String, val icon: ImageVector, val testTag: String) {
  RADAR("Opportunity Radar", Icons.Default.Work, "job_automation_tab_radar"),
  RULES("Automation Rules", Icons.Default.Tune, "job_automation_tab_rules"),
  AGENTS("Autonomous Agents", Icons.Default.SmartToy, "job_automation_tab_agents"),
  LOGS("Task Logs", Icons.Default.History, "job_automation_tab_logs")
}

@Composable
fun JobAutomationScreen(
  viewModel: TitanViewModel,
  taskLogsViewModel: TaskLogsViewModel,
  modifier: Modifier = Modifier
) {
  val userProfile by viewModel.userProfile.collectAsState()
  var selectedTabIndex by remember { mutableIntStateOf(0) }
  val automationMode = userProfile?.automationMode ?: "AUTO"

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(ObsidianDark)
      .testTag("job_automation_screen")
  ) {
    // Top Automation Overview Bar
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 8.dp)
        .testTag("job_automation_header_card"),
      colors = CardDefaults.cardColors(containerColor = SlateCard),
      shape = RoundedCornerShape(12.dp),
      border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(36.dp)
              .clip(RoundedCornerShape(8.dp))
              .background(TitanCyan.copy(alpha = 0.15f))
              .border(1.dp, TitanCyan.copy(alpha = 0.4f), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Bolt,
              contentDescription = "Job Automation Suite",
              tint = TitanCyan,
              modifier = Modifier.size(20.dp)
            )
          }
          Spacer(modifier = Modifier.width(12.dp))
          Column {
            Text(
              text = "JOB AUTOMATION SUITE",
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.Bold,
              letterSpacing = 1.2.sp,
              color = TitanCyan
            )
            Text(
              text = "Crawler & Autonomous Pipeline",
              style = MaterialTheme.typography.bodySmall,
              color = TextMutedDark,
              fontSize = 11.sp
            )
          }
        }

        // Status Badge & Quick Mode Toggle
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(
              when (automationMode) {
                "AUTO" -> TitanEmerald.copy(alpha = 0.18f)
                "APPROVAL" -> TitanGold.copy(alpha = 0.18f)
                else -> SlateElevated
              }
            )
            .border(
              1.dp,
              when (automationMode) {
                "AUTO" -> TitanEmerald.copy(alpha = 0.6f)
                "APPROVAL" -> TitanGold.copy(alpha = 0.6f)
                else -> SlateBorder
              },
              RoundedCornerShape(20.dp)
            )
            .clickable {
              val nextMode = when (automationMode) {
                "AUTO" -> "APPROVAL"
                "APPROVAL" -> "MANUAL"
                else -> "AUTO"
              }
              viewModel.setAutomationMode(nextMode)
            }
            .padding(horizontal = 10.dp, vertical = 5.dp)
            .testTag("job_automation_mode_badge"),
          contentAlignment = Alignment.Center
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(
                  when (automationMode) {
                    "AUTO" -> TitanEmerald
                    "APPROVAL" -> TitanGold
                    else -> TextMutedDark
                  }
                )
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = automationMode,
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.Bold,
              fontSize = 10.sp,
              color = when (automationMode) {
                "AUTO" -> TitanEmerald
                "APPROVAL" -> TitanGold
                else -> TextPrimaryDark
              }
            )
          }
        }
      }
    }

    // Tab Navigation Bar
    ScrollableTabRow(
      selectedTabIndex = selectedTabIndex,
      containerColor = ObsidianDark,
      contentColor = TitanCyan,
      edgePadding = 16.dp,
      indicator = { tabPositions ->
        TabRowDefaults.SecondaryIndicator(
          modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
          color = TitanCyan,
          height = 2.5.dp
        )
      },
      divider = {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(SlateBorder)
        )
      },
      modifier = Modifier
        .fillMaxWidth()
        .testTag("job_automation_sub_tabs")
    ) {
      JobAutomationSubTab.values().forEachIndexed { index, tab ->
        val isSelected = selectedTabIndex == index
        Tab(
          selected = isSelected,
          onClick = { selectedTabIndex = index },
          modifier = Modifier.testTag(tab.testTag),
          text = {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.Center
            ) {
              Icon(
                imageVector = tab.icon,
                contentDescription = tab.title,
                modifier = Modifier.size(16.dp),
                tint = if (isSelected) TitanCyan else TextMutedDark
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = tab.title,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) TextPrimaryDark else TextMutedDark,
                fontSize = 12.sp
              )
            }
          }
        )
      }
    }

    // Tab Content View
    Box(
      modifier = Modifier
        .fillMaxSize()
        .weight(1f)
    ) {
      when (JobAutomationSubTab.values()[selectedTabIndex]) {
        JobAutomationSubTab.RADAR -> JobsScreen(viewModel)
        JobAutomationSubTab.RULES -> AutomationRulesScreen(viewModel)
        JobAutomationSubTab.AGENTS -> AgentsScreen(viewModel)
        JobAutomationSubTab.LOGS -> TaskLogsScreen(taskLogsViewModel)
      }
    }
  }
}
