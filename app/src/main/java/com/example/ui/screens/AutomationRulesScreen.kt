package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AutomationRule
import com.example.ui.components.AutomationAnalyticsCharts
import com.example.ui.theme.ObsidianDark
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateCard
import com.example.ui.theme.SlateElevated
import com.example.ui.theme.TextMutedDark
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import com.example.ui.theme.TitanAmber
import com.example.ui.theme.TitanCrimson
import com.example.ui.theme.TitanCyan
import com.example.ui.theme.TitanEmerald
import com.example.ui.theme.TitanGold
import com.example.ui.theme.TitanIndigo
import com.example.ui.theme.TitanViolet
import com.example.ui.viewmodel.TitanViewModel

@Composable
fun AutomationRulesScreen(
  viewModel: TitanViewModel
) {
  val rules by viewModel.automationRules.collectAsState()
  val applications by viewModel.applications.collectAsState()
  val taskLogs by viewModel.automationTaskLogs.collectAsState()
  var activeScreenTab by remember { mutableStateOf(0) } // 0 = Rules, 1 = Charts
  var isConfigDialogOpen by remember { mutableStateOf(false) }
  var editingRule by remember { mutableStateOf<AutomationRule?>(null) }
  var simulationResult by remember { mutableStateOf<Pair<AutomationRule, String>?>(null) }
  var selectedFilterCategory by remember { mutableStateOf<String?>(null) }

  val filteredRules = if (selectedFilterCategory == null) {
    rules
  } else {
    rules.filter { it.triggerCategory == selectedFilterCategory }
  }

  val activeCount = rules.count { it.isEnabled }
  val totalExecutions = rules.sumOf { it.executionCount }

  // Dialog for Creating / Editing Rule
  if (isConfigDialogOpen) {
    AutomationRuleConfigDialog(
      initialRule = editingRule,
      onDismiss = {
        isConfigDialogOpen = false
        editingRule = null
      },
      onSave = { ruleToSave ->
        viewModel.saveAutomationRule(ruleToSave)
        isConfigDialogOpen = false
        editingRule = null
      }
    )
  }

  // Dialog for Live Simulation / Test Result
  if (simulationResult != null) {
    val (simRule, simOutput) = simulationResult!!
    AlertDialog(
      onDismissRequest = { simulationResult = null },
      containerColor = SlateCard,
      title = {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.Bolt,
            contentDescription = "Simulation",
            tint = TitanCyan,
            modifier = Modifier.size(22.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Rule Simulation Triggered",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = TextPrimaryDark
          )
        }
      },
      text = {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
        ) {
          Text(
            text = "SIMULATED TRIGGER CONDITION:",
            style = MaterialTheme.typography.labelSmall,
            color = TitanGold,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = simRule.triggerCondition,
            style = MaterialTheme.typography.bodySmall,
            color = TextPrimaryDark,
            modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
          )

          Text(
            text = "GENERATED AUTOMATED RESPONSE:",
            style = MaterialTheme.typography.labelSmall,
            color = TitanCyan,
            fontWeight = FontWeight.Bold
          )
          Spacer(modifier = Modifier.height(4.dp))
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(8.dp))
              .background(ObsidianDark)
              .border(1.dp, SlateBorder, RoundedCornerShape(8.dp))
              .padding(10.dp)
          ) {
            Text(
              text = simOutput,
              style = MaterialTheme.typography.bodySmall,
              fontFamily = FontFamily.Monospace,
              color = TextPrimaryDark,
              fontSize = 11.sp,
              lineHeight = 16.sp
            )
          }

          Spacer(modifier = Modifier.height(10.dp))
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 4.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Security,
              contentDescription = null,
              tint = if (simRule.requireHumanApproval) TitanGold else TitanEmerald,
              modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = if (simRule.requireHumanApproval) {
                "Queued in Approval Center (Human Approval Enforced)"
              } else {
                "Dispatched autonomously via ${simRule.targetAgent}"
              },
              style = MaterialTheme.typography.labelSmall,
              color = if (simRule.requireHumanApproval) TitanGold else TitanEmerald,
              fontSize = 10.sp
            )
          }
        }
      },
      confirmButton = {
        Button(
          onClick = { simulationResult = null },
          colors = ButtonDefaults.buttonColors(containerColor = TitanCyan),
          modifier = Modifier.testTag("close_simulation_button")
        ) {
          Text("Done", color = ObsidianDark, fontWeight = FontWeight.Bold)
        }
      }
    )
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(ObsidianDark)
      .padding(horizontal = 16.dp, vertical = 8.dp)
      .testTag("automation_rules_screen")
  ) {
    // Header Bar
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier = Modifier
            .size(36.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(TitanCyan.copy(alpha = 0.15f))
            .border(1.dp, TitanCyan.copy(alpha = 0.4f), RoundedCornerShape(10.dp)),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Tune,
            contentDescription = "Automation Rules",
            tint = TitanCyan,
            modifier = Modifier.size(20.dp)
          )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
          Text(
            text = "Automation Rules",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = TextPrimaryDark
          )
          Text(
            text = "Event triggers & autonomous responses",
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondaryDark
          )
        }
      }

      Button(
        onClick = {
          editingRule = null
          isConfigDialogOpen = true
        },
        colors = ButtonDefaults.buttonColors(containerColor = TitanCyan),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.testTag("create_rule_button")
      ) {
        Icon(
          imageVector = Icons.Default.Add,
          contentDescription = "New Rule",
          tint = ObsidianDark,
          modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
          text = "New Rule",
          color = ObsidianDark,
          fontWeight = FontWeight.Bold,
          fontSize = 12.sp
        )
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Top Screen Tab Switcher: Rules Configuration vs Charts & Analytics
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(10.dp))
        .background(SlateElevated)
        .border(1.dp, SlateBorder, RoundedCornerShape(10.dp))
        .padding(3.dp),
      horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
      Box(
        modifier = Modifier
          .weight(1f)
          .clip(RoundedCornerShape(8.dp))
          .background(if (activeScreenTab == 0) TitanCyan.copy(alpha = 0.2f) else Color.Transparent)
          .border(
            1.dp,
            if (activeScreenTab == 0) TitanCyan else Color.Transparent,
            RoundedCornerShape(8.dp)
          )
          .clickable { activeScreenTab = 0 }
          .padding(vertical = 8.dp)
          .testTag("tab_rules_configuration"),
        contentAlignment = Alignment.Center
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.Tune,
            contentDescription = null,
            tint = if (activeScreenTab == 0) TitanCyan else TextSecondaryDark,
            modifier = Modifier.size(15.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Active Rules (${rules.size})",
            style = MaterialTheme.typography.labelSmall,
            color = if (activeScreenTab == 0) TextPrimaryDark else TextSecondaryDark,
            fontWeight = if (activeScreenTab == 0) FontWeight.Bold else FontWeight.Medium,
            fontSize = 11.sp
          )
        }
      }

      Box(
        modifier = Modifier
          .weight(1f)
          .clip(RoundedCornerShape(8.dp))
          .background(if (activeScreenTab == 1) TitanCyan.copy(alpha = 0.2f) else Color.Transparent)
          .border(
            1.dp,
            if (activeScreenTab == 1) TitanCyan else Color.Transparent,
            RoundedCornerShape(8.dp)
          )
          .clickable { activeScreenTab = 1 }
          .padding(vertical = 8.dp)
          .testTag("tab_analytics_charts"),
        contentAlignment = Alignment.Center
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.BarChart,
            contentDescription = null,
            tint = if (activeScreenTab == 1) TitanCyan else TextSecondaryDark,
            modifier = Modifier.size(15.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Pipeline Charts & Analytics (${applications.size})",
            style = MaterialTheme.typography.labelSmall,
            color = if (activeScreenTab == 1) TextPrimaryDark else TextSecondaryDark,
            fontWeight = if (activeScreenTab == 1) FontWeight.Bold else FontWeight.Medium,
            fontSize = 11.sp
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    if (activeScreenTab == 1) {
      // --- TAB 1: Automation Analytics & Recharts Data Visualizer ---
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .weight(1f)
          .verticalScroll(rememberScrollState())
          .testTag("analytics_charts_scroll_container"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        AutomationAnalyticsCharts(
          applications = applications,
          automationRules = rules,
          taskLogs = taskLogs
        )

        // Summary Card: Autonomous Intelligence Dispatch Summary
        Card(
          colors = CardDefaults.cardColors(containerColor = SlateCard),
          shape = RoundedCornerShape(12.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = TitanCyan,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "Autonomous Pipeline Dynamics",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = TextPrimaryDark
              )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = "Real-time pipeline monitoring cross-correlating ${rules.size} event automation rules, ${taskLogs.size} recorded system task logs, and ${applications.size} active application tracks.",
              style = MaterialTheme.typography.bodySmall,
              color = TextSecondaryDark,
              fontSize = 11.sp,
              lineHeight = 16.sp
            )
          }
        }
        Spacer(modifier = Modifier.height(16.dp))
      }
    } else {
      // --- TAB 0: Automation Rules List & Engine ---
      // Quick Analytics Shortcut Banner
      Card(
        colors = CardDefaults.cardColors(containerColor = SlateElevated),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
          .fillMaxWidth()
          .clickable { activeScreenTab = 1 }
          .border(1.dp, TitanCyan.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
          .testTag("shortcut_to_analytics_charts")
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.BarChart,
              contentDescription = null,
              tint = TitanCyan,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Visual Charts: Status Distribution & History (${applications.size} apps)",
              style = MaterialTheme.typography.labelSmall,
              color = TextPrimaryDark,
              fontWeight = FontWeight.SemiBold,
              fontSize = 10.5.sp
            )
          }
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            tint = TitanCyan,
            modifier = Modifier.size(14.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Metrics Overview Card
      Card(
        colors = CardDefaults.cardColors(containerColor = SlateCard),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
          .fillMaxWidth()
          .border(1.dp, SlateBorder, RoundedCornerShape(12.dp))
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 10.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "ACTIVE RULES",
              style = MaterialTheme.typography.labelSmall,
              color = TextMutedDark,
              fontSize = 9.sp,
              fontWeight = FontWeight.SemiBold
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(7.dp)
                  .clip(CircleShape)
                  .background(TitanEmerald)
              )
              Spacer(modifier = Modifier.width(5.dp))
              Text(
                text = "$activeCount of ${rules.size} enabled",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = TextPrimaryDark
              )
            }
          }

          Box(
            modifier = Modifier
              .width(1.dp)
              .height(24.dp)
              .background(SlateBorder)
          )

          Column {
            Text(
              text = "TOTAL EXECUTIONS",
              style = MaterialTheme.typography.labelSmall,
              color = TextMutedDark,
              fontSize = 9.sp,
              fontWeight = FontWeight.SemiBold
            )
            Text(
              text = "$totalExecutions firings",
              style = MaterialTheme.typography.bodySmall,
              fontWeight = FontWeight.Bold,
              color = TitanCyan
            )
          }

          Box(
            modifier = Modifier
              .width(1.dp)
              .height(24.dp)
              .background(SlateBorder)
          )

          Column {
            Text(
              text = "TRIGGER ENGINE",
              style = MaterialTheme.typography.labelSmall,
              color = TextMutedDark,
              fontSize = 9.sp,
              fontWeight = FontWeight.SemiBold
            )
            Text(
              text = "Active Swarm",
              style = MaterialTheme.typography.bodySmall,
              fontWeight = FontWeight.Bold,
              color = TitanGold
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Quick Filter & Preset Starters Bar
      val filterCategories = listOf(
        "All" to null,
        "Email Inbound" to "EMAIL_RECEIVED",
        "Interviews" to "INTERVIEW_INVITE",
        "Pipeline" to "APPLICATION_STATUS",
        "Recruiter" to "RECRUITER_OUTREACH"
      )

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        filterCategories.forEach { (label, category) ->
          val isSelected = selectedFilterCategory == category
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .background(if (isSelected) TitanCyan.copy(alpha = 0.2f) else SlateElevated)
              .border(
                1.dp,
                if (isSelected) TitanCyan else SlateBorder,
                RoundedCornerShape(8.dp)
              )
              .clickable { selectedFilterCategory = category }
              .padding(horizontal = 10.dp, vertical = 5.dp)
          ) {
            Text(
              text = label,
              style = MaterialTheme.typography.labelSmall,
              color = if (isSelected) TitanCyan else TextSecondaryDark,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
              fontSize = 11.sp
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Rules List
      if (filteredRules.isEmpty()) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
            .clip(RoundedCornerShape(12.dp))
            .background(SlateCard)
            .border(1.dp, SlateBorder, RoundedCornerShape(12.dp))
            .padding(24.dp),
          contentAlignment = Alignment.Center
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
              imageVector = Icons.Default.Tune,
              contentDescription = null,
              tint = TextMutedDark,
              modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
              text = "No automation rules found",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = TextPrimaryDark
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "Define trigger conditions like 'If email from company X received' to automate recruiter interactions.",
              style = MaterialTheme.typography.bodySmall,
              color = TextSecondaryDark,
              fontSize = 12.sp,
              textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
              onClick = {
                editingRule = null
                isConfigDialogOpen = true
              },
              colors = ButtonDefaults.buttonColors(containerColor = TitanCyan)
            ) {
              Text("Create First Rule", color = ObsidianDark, fontWeight = FontWeight.Bold)
            }
          }
        }
      } else {
        LazyColumn(
          modifier = Modifier
            .fillMaxWidth()
            .weight(1f),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          items(filteredRules, key = { it.id }) { rule ->
            AutomationRuleCard(
              rule = rule,
              onToggleEnabled = { isEnabled ->
                viewModel.toggleAutomationRule(rule.id, isEnabled)
              },
              onTestTrigger = {
                viewModel.testTriggerAutomationRule(rule) { responseOutput ->
                  simulationResult = Pair(rule, responseOutput)
                }
              },
              onEdit = {
                editingRule = rule
                isConfigDialogOpen = true
              },
              onDelete = {
                viewModel.deleteAutomationRule(rule.id)
              }
            )
          }
        }
      }
    }
  }
}

@Composable
fun AutomationRuleCard(
  rule: AutomationRule,
  onToggleEnabled: (Boolean) -> Unit,
  onTestTrigger: () -> Unit,
  onEdit: () -> Unit,
  onDelete: () -> Unit
) {
  val categoryColor = when (rule.triggerCategory) {
    "EMAIL_RECEIVED" -> TitanCyan
    "INTERVIEW_INVITE" -> TitanViolet
    "APPLICATION_STATUS" -> TitanGold
    "RECRUITER_OUTREACH" -> TitanEmerald
    else -> TitanIndigo
  }

  Card(
    colors = CardDefaults.cardColors(containerColor = SlateCard),
    shape = RoundedCornerShape(12.dp),
    modifier = Modifier
      .fillMaxWidth()
      .border(1.dp, if (rule.isEnabled) SlateBorder else SlateBorder.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
      .testTag("rule_card_${rule.id}")
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      // Header: Badge + Title + Switch
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.weight(1f)
        ) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(categoryColor.copy(alpha = 0.15f))
              .border(1.dp, categoryColor.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
              .padding(horizontal = 6.dp, vertical = 2.dp)
          ) {
            Text(
              text = rule.triggerCategory.replace("_", " "),
              style = MaterialTheme.typography.labelSmall,
              color = categoryColor,
              fontWeight = FontWeight.Bold,
              fontSize = 9.sp
            )
          }

          if (rule.targetCompany.isNotBlank()) {
            Spacer(modifier = Modifier.width(6.dp))
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(SlateElevated)
                .border(1.dp, SlateBorder, RoundedCornerShape(6.dp))
                .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
              Text(
                text = rule.targetCompany,
                style = MaterialTheme.typography.labelSmall,
                color = TextPrimaryDark,
                fontWeight = FontWeight.SemiBold,
                fontSize = 9.sp
              )
            }
          }
        }

        Switch(
          checked = rule.isEnabled,
          onCheckedChange = onToggleEnabled,
          colors = SwitchDefaults.colors(
            checkedThumbColor = TitanCyan,
            checkedTrackColor = TitanCyan.copy(alpha = 0.3f),
            uncheckedThumbColor = TextMutedDark,
            uncheckedTrackColor = SlateElevated
          ),
          modifier = Modifier
            .size(38.dp)
            .testTag("toggle_rule_${rule.id}")
        )
      }

      Spacer(modifier = Modifier.height(4.dp))

      Text(
        text = rule.name,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = if (rule.isEnabled) TextPrimaryDark else TextSecondaryDark
      )

      Spacer(modifier = Modifier.height(8.dp))

      // Trigger Condition Block (IF)
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(8.dp))
          .background(ObsidianDark)
          .border(1.dp, SlateBorder, RoundedCornerShape(8.dp))
          .padding(10.dp)
      ) {
        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Bolt,
              contentDescription = "Trigger Condition",
              tint = TitanGold,
              modifier = Modifier.size(15.dp)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
              text = "IF (TRIGGER CONDITION):",
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.Bold,
              color = TitanGold,
              fontSize = 10.sp
            )
          }
          Spacer(modifier = Modifier.height(3.dp))
          Text(
            text = rule.triggerCondition,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = TextPrimaryDark,
            fontSize = 12.sp
          )
          if (rule.triggerFilterKeywords.isNotBlank()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "Filter keywords: ${rule.triggerFilterKeywords}",
              style = MaterialTheme.typography.labelSmall,
              color = TextMutedDark,
              fontSize = 10.sp
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(6.dp))

      // Automated Response Block (THEN)
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(8.dp))
          .background(ObsidianDark)
          .border(1.dp, SlateBorder, RoundedCornerShape(8.dp))
          .padding(10.dp)
      ) {
        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.AutoAwesome,
              contentDescription = "Automated Response",
              tint = TitanCyan,
              modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
              text = "THEN (AUTOMATED RESPONSE):",
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.Bold,
              color = TitanCyan,
              fontSize = 10.sp
            )
          }
          Spacer(modifier = Modifier.height(3.dp))
          Text(
            text = rule.automatedResponse,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = TextPrimaryDark,
            fontSize = 12.sp
          )
          Spacer(modifier = Modifier.height(4.dp))
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = "Assigned Agent: ${rule.targetAgent}",
              style = MaterialTheme.typography.labelSmall,
              color = TextMutedDark,
              fontSize = 10.sp
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = if (rule.requireHumanApproval) "• Approval Gate: ON" else "• Zero-Touch Auto",
              style = MaterialTheme.typography.labelSmall,
              color = if (rule.requireHumanApproval) TitanGold else TitanEmerald,
              fontSize = 10.sp,
              fontWeight = FontWeight.SemiBold
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Footer: Execution Stats & Action Buttons
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Fired ${rule.executionCount} times",
            style = MaterialTheme.typography.labelSmall,
            color = TitanCyan,
            fontWeight = FontWeight.SemiBold,
            fontSize = 10.sp
          )
          Text(
            text = "Last: ${rule.lastTriggeredTime}",
            style = MaterialTheme.typography.labelSmall,
            color = TextMutedDark,
            fontSize = 9.sp
          )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          // Simulate / Test Button
          OutlinedButton(
            onClick = onTestTrigger,
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = TitanCyan),
            modifier = Modifier.testTag("test_rule_${rule.id}")
          ) {
            Icon(
              imageVector = Icons.Default.PlayArrow,
              contentDescription = "Test Trigger",
              modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text("Simulate", fontSize = 11.sp, fontWeight = FontWeight.Bold)
          }

          // Edit Button
          IconButton(
            onClick = onEdit,
            modifier = Modifier
              .size(34.dp)
              .clip(RoundedCornerShape(8.dp))
              .background(SlateElevated)
              .testTag("edit_rule_${rule.id}")
          ) {
            Icon(
              imageVector = Icons.Default.Edit,
              contentDescription = "Edit Rule",
              tint = TextSecondaryDark,
              modifier = Modifier.size(16.dp)
            )
          }

          // Delete Button
          IconButton(
            onClick = onDelete,
            modifier = Modifier
              .size(34.dp)
              .clip(RoundedCornerShape(8.dp))
              .background(SlateElevated)
              .testTag("delete_rule_${rule.id}")
          ) {
            Icon(
              imageVector = Icons.Default.Delete,
              contentDescription = "Delete Rule",
              tint = TitanCrimson,
              modifier = Modifier.size(16.dp)
            )
          }
        }
      }
    }
  }
}

@Composable
fun AutomationRuleConfigDialog(
  initialRule: AutomationRule?,
  onDismiss: () -> Unit,
  onSave: (AutomationRule) -> Unit
) {
  var name by remember { mutableStateOf(initialRule?.name ?: "") }
  var triggerCategory by remember { mutableStateOf(initialRule?.triggerCategory ?: "EMAIL_RECEIVED") }
  var targetCompany by remember { mutableStateOf(initialRule?.targetCompany ?: "") }
  var triggerCondition by remember { mutableStateOf(initialRule?.triggerCondition ?: "") }
  var triggerFilterKeywords by remember { mutableStateOf(initialRule?.triggerFilterKeywords ?: "") }
  var responseType by remember { mutableStateOf(initialRule?.responseType ?: "AUTO_DRAFT_REPLY") }
  var automatedResponse by remember { mutableStateOf(initialRule?.automatedResponse ?: "") }
  var responseTemplate by remember { mutableStateOf(initialRule?.responseTemplate ?: "") }
  var targetAgent by remember { mutableStateOf(initialRule?.targetAgent ?: "Inbound Recruiter Specialist") }
  var requireHumanApproval by remember { mutableStateOf(initialRule?.requireHumanApproval ?: false) }

  val categories = listOf(
    "EMAIL_RECEIVED" to "Email Received",
    "INTERVIEW_INVITE" to "Interview Invite",
    "APPLICATION_STATUS" to "Application Status",
    "RECRUITER_OUTREACH" to "Recruiter Outreach",
    "CUSTOM_TRIGGER" to "Custom Event"
  )

  val responseTypes = listOf(
    "AUTO_DRAFT_REPLY" to "Auto-Draft Email Reply",
    "AI_INTERVIEW_PREP" to "AI Interview Prep Brief",
    "CALENDAR_FOLLOW_UP" to "Calendar Follow-Up Cadence",
    "PIPELINE_UPDATE" to "Pipeline CRM Update & CTC Modeler",
    "CUSTOM_AGENT_ACTION" to "Custom Agent Action"
  )

  val companySuggestions = listOf("Zepto", "Google", "Flipkart", "Swiggy", "Stripe", "Any Company")

  fun applyConditionTemplate(category: String, company: String) {
    val comp = if (company.isNotBlank()) company else "company X"
    when (category) {
      "EMAIL_RECEIVED" -> {
        triggerCondition = "If email from $comp received"
        if (automatedResponse.isBlank()) {
          automatedResponse = "Auto-draft high-conviction executive reply confirming interest & availability"
        }
      }
      "INTERVIEW_INVITE" -> {
        triggerCondition = "If interview invite received from $comp"
        if (automatedResponse.isBlank()) {
          automatedResponse = "Trigger Gemini 360° company intelligence & compile interview war-room dossier"
        }
      }
      "APPLICATION_STATUS" -> {
        triggerCondition = "If application status updates to 'OFFER'"
        if (automatedResponse.isBlank()) {
          automatedResponse = "Calculate monthly in-hand net salary, EPF deduction, and generate counter-offer negotiation script"
        }
      }
      "RECRUITER_OUTREACH" -> {
        triggerCondition = "If recruiter outreach received from $comp"
        if (automatedResponse.isBlank()) {
          automatedResponse = "Send tailored proof-of-work link highlighting verified operational projects"
        }
      }
    }
  }

  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = SlateCard,
    title = {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.Tune,
          contentDescription = null,
          tint = TitanCyan,
          modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = if (initialRule == null) "Configure Automation Rule" else "Edit Automation Rule",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
          color = TextPrimaryDark
        )
      }
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState())
      ) {
        // Rule Name
        OutlinedTextField(
          value = name,
          onValueChange = { name = it },
          label = { Text("Rule Name", color = TextSecondaryDark) },
          placeholder = { Text("e.g. Zepto Recruiter Auto-Responder", color = TextMutedDark) },
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("rule_name_input"),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = TitanCyan,
            unfocusedBorderColor = SlateBorder,
            focusedTextColor = TextPrimaryDark,
            unfocusedTextColor = TextPrimaryDark
          )
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Trigger Category Selection
        Text(
          text = "TRIGGER CATEGORY",
          style = MaterialTheme.typography.labelSmall,
          color = TitanGold,
          fontWeight = FontWeight.Bold,
          fontSize = 10.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          categories.forEach { (catId, catLabel) ->
            val isSelected = triggerCategory == catId
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(if (isSelected) TitanCyan.copy(alpha = 0.2f) else SlateElevated)
                .border(
                  1.dp,
                  if (isSelected) TitanCyan else SlateBorder,
                  RoundedCornerShape(8.dp)
                )
                .clickable {
                  triggerCategory = catId
                  applyConditionTemplate(catId, targetCompany)
                }
                .padding(horizontal = 8.dp, vertical = 5.dp)
            ) {
              Text(
                text = catLabel,
                style = MaterialTheme.typography.labelSmall,
                color = if (isSelected) TitanCyan else TextSecondaryDark,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                fontSize = 11.sp
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Target Company Field + Suggestion Chips
        OutlinedTextField(
          value = targetCompany,
          onValueChange = {
            targetCompany = it
            applyConditionTemplate(triggerCategory, it)
          },
          label = { Text("Target Company (or 'Any Company')", color = TextSecondaryDark) },
          placeholder = { Text("e.g. Zepto, Google, Stripe", color = TextMutedDark) },
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("rule_target_company_input"),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = TitanCyan,
            unfocusedBorderColor = SlateBorder,
            focusedTextColor = TextPrimaryDark,
            unfocusedTextColor = TextPrimaryDark
          )
        )

        Spacer(modifier = Modifier.height(4.dp))
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          companySuggestions.forEach { suggestion ->
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(SlateElevated)
                .border(1.dp, SlateBorder, RoundedCornerShape(6.dp))
                .clickable {
                  targetCompany = suggestion
                  applyConditionTemplate(triggerCategory, suggestion)
                }
                .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
              Text(
                text = suggestion,
                style = MaterialTheme.typography.labelSmall,
                color = TitanCyan,
                fontSize = 10.sp
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Trigger Condition
        OutlinedTextField(
          value = triggerCondition,
          onValueChange = { triggerCondition = it },
          label = { Text("Trigger Condition (e.g. 'If email from company X received')", color = TextSecondaryDark) },
          placeholder = { Text("If email from company Zepto received", color = TextMutedDark) },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("rule_condition_input"),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = TitanGold,
            unfocusedBorderColor = SlateBorder,
            focusedTextColor = TextPrimaryDark,
            unfocusedTextColor = TextPrimaryDark
          )
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Filter Keywords (Optional)
        OutlinedTextField(
          value = triggerFilterKeywords,
          onValueChange = { triggerFilterKeywords = it },
          label = { Text("Filter Keywords (Comma-separated)", color = TextSecondaryDark) },
          placeholder = { Text("e.g. interview, availability, slot, offer", color = TextMutedDark) },
          singleLine = true,
          modifier = Modifier.fillMaxWidth(),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = TitanCyan,
            unfocusedBorderColor = SlateBorder,
            focusedTextColor = TextPrimaryDark,
            unfocusedTextColor = TextPrimaryDark
          )
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Response Type Selector
        Text(
          text = "AUTOMATED RESPONSE TYPE",
          style = MaterialTheme.typography.labelSmall,
          color = TitanCyan,
          fontWeight = FontWeight.Bold,
          fontSize = 10.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          responseTypes.forEach { (typeId, typeLabel) ->
            val isSelected = responseType == typeId
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(if (isSelected) TitanCyan.copy(alpha = 0.2f) else SlateElevated)
                .border(
                  1.dp,
                  if (isSelected) TitanCyan else SlateBorder,
                  RoundedCornerShape(8.dp)
                )
                .clickable { responseType = typeId }
                .padding(horizontal = 8.dp, vertical = 5.dp)
            ) {
              Text(
                text = typeLabel,
                style = MaterialTheme.typography.labelSmall,
                color = if (isSelected) TitanCyan else TextSecondaryDark,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                fontSize = 11.sp
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Automated Response Action Description
        OutlinedTextField(
          value = automatedResponse,
          onValueChange = { automatedResponse = it },
          label = { Text("Automated Response Action", color = TextSecondaryDark) },
          placeholder = { Text("e.g. Auto-draft executive reply confirming interest & weekday availability", color = TextMutedDark) },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("rule_response_input"),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = TitanCyan,
            unfocusedBorderColor = SlateBorder,
            focusedTextColor = TextPrimaryDark,
            unfocusedTextColor = TextPrimaryDark
          )
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Optional Response Template / Prompt
        OutlinedTextField(
          value = responseTemplate,
          onValueChange = { responseTemplate = it },
          label = { Text("Response Template / Prompt (Optional)", color = TextSecondaryDark) },
          placeholder = { Text("Dear {{company}} Talent Team,\n\nThank you for reaching out...", color = TextMutedDark) },
          minLines = 3,
          maxLines = 5,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("rule_template_input"),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = TitanCyan,
            unfocusedBorderColor = SlateBorder,
            focusedTextColor = TextPrimaryDark,
            unfocusedTextColor = TextPrimaryDark
          )
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Assigned Agent
        OutlinedTextField(
          value = targetAgent,
          onValueChange = { targetAgent = it },
          label = { Text("Assigned Autonomous Agent", color = TextSecondaryDark) },
          singleLine = true,
          modifier = Modifier.fillMaxWidth(),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = TitanCyan,
            unfocusedBorderColor = SlateBorder,
            focusedTextColor = TextPrimaryDark,
            unfocusedTextColor = TextPrimaryDark
          )
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Human Approval Gate Switch
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(SlateElevated)
            .padding(10.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "Require Human Approval",
              style = MaterialTheme.typography.bodySmall,
              fontWeight = FontWeight.Bold,
              color = TextPrimaryDark
            )
            Text(
              text = "Hold response in Pending Approvals queue before dispatching",
              style = MaterialTheme.typography.labelSmall,
              color = TextSecondaryDark,
              fontSize = 10.sp
            )
          }

          Switch(
            checked = requireHumanApproval,
            onCheckedChange = { requireHumanApproval = it },
            colors = SwitchDefaults.colors(
              checkedThumbColor = TitanGold,
              checkedTrackColor = TitanGold.copy(alpha = 0.3f)
            ),
            modifier = Modifier.testTag("rule_approval_switch")
          )
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (name.isNotBlank() && triggerCondition.isNotBlank() && automatedResponse.isNotBlank()) {
            val rule = AutomationRule(
              id = initialRule?.id ?: java.util.UUID.randomUUID().toString(),
              name = name.trim(),
              triggerCategory = triggerCategory,
              targetCompany = targetCompany.trim(),
              triggerCondition = triggerCondition.trim(),
              triggerFilterKeywords = triggerFilterKeywords.trim(),
              responseType = responseType,
              automatedResponse = automatedResponse.trim(),
              responseTemplate = responseTemplate.trim(),
              targetAgent = targetAgent.trim(),
              requireHumanApproval = requireHumanApproval,
              isEnabled = initialRule?.isEnabled ?: true,
              executionCount = initialRule?.executionCount ?: 0,
              lastTriggeredTime = initialRule?.lastTriggeredTime ?: "Never",
              lastExecutionResult = initialRule?.lastExecutionResult ?: ""
            )
            onSave(rule)
          }
        },
        enabled = name.isNotBlank() && triggerCondition.isNotBlank() && automatedResponse.isNotBlank(),
        colors = ButtonDefaults.buttonColors(containerColor = TitanCyan),
        modifier = Modifier.testTag("save_rule_button")
      ) {
        Text("Save Rule", color = ObsidianDark, fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      OutlinedButton(
        onClick = onDismiss,
        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondaryDark),
        modifier = Modifier.testTag("cancel_rule_button")
      ) {
        Text("Cancel")
      }
    }
  )
}
