package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AgentAuditLog
import com.example.data.model.AgentItem
import com.example.data.model.ApprovalItem
import com.example.data.model.AutomationItem
import com.example.data.model.IntegrationItem
import com.example.data.model.MissionItem
import com.example.data.model.SystemHealthMetric
import com.example.ui.components.AgentInspectDialog
import com.example.ui.components.AutomationAnalyticsCharts
import com.example.ui.components.AutomationTaskLogsCard
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
import com.example.ui.viewmodel.TitanViewModel

@Composable
fun AgentsScreen(
  viewModel: TitanViewModel
) {
  val agents by viewModel.agents.collectAsState()
  val auditLogs by viewModel.auditLogs.collectAsState()
  val missions by viewModel.missions.collectAsState()
  val automations by viewModel.automations.collectAsState()
  val approvals by viewModel.approvals.collectAsState()
  val integrations by viewModel.integrations.collectAsState()
  val healthMetrics by viewModel.healthMetrics.collectAsState()
  val automationTaskLogs by viewModel.automationTaskLogs.collectAsState()
  val applications by viewModel.applications.collectAsState()
  val automationRules by viewModel.automationRules.collectAsState()
  val inspectedAgent by viewModel.selectedAgentForInspection.collectAsState()

  var selectedTab by remember { mutableStateOf(0) } // 0 = Fleet, 1 = Approvals, 2 = Missions/Automations, 3 = Audit/Health

  if (inspectedAgent != null) {
    AgentInspectDialog(
      agent = inspectedAgent!!,
      onDismissRequest = { viewModel.inspectAgent(null) },
      onStart = { viewModel.runAgent(inspectedAgent!!.id) },
      onPause = { viewModel.pauseAgent(inspectedAgent!!.id) },
      onStop = { viewModel.stopAgent(inspectedAgent!!.id) },
      onRetry = { viewModel.runAgent(inspectedAgent!!.id) }
    )
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(ObsidianDark)
      .padding(horizontal = 16.dp, vertical = 8.dp)
      .testTag("agents_screen")
  ) {
    // Header
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Text(
          text = "TITAN CONTROL ROOM",
          style = MaterialTheme.typography.labelSmall,
          color = TitanCyan,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = "Autonomous Agent Fleet & Safety Engine",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
          color = TextPrimaryDark
        )
      }

      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(8.dp))
          .background(SlateElevated)
          .border(1.dp, TitanEmerald.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
          .padding(horizontal = 8.dp, vertical = 4.dp)
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(6.dp)
              .clip(CircleShape)
              .background(TitanEmerald)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text("HEALTHY", style = MaterialTheme.typography.labelSmall, color = TitanEmerald, fontWeight = FontWeight.Bold, fontSize = 9.sp)
        }
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Scrollable Tabs
    ScrollableTabRow(
      selectedTabIndex = selectedTab,
      containerColor = SlateCard,
      edgePadding = 0.dp,
      indicator = { tabPositions ->
        TabRowDefaults.SecondaryIndicator(
          Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
          color = TitanCyan
        )
      }
    ) {
      Tab(
        selected = selectedTab == 0,
        onClick = { selectedTab = 0 },
        text = {
          Text(
            "Fleet (${agents.size})",
            color = if (selectedTab == 0) TitanCyan else TextSecondaryDark,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp
          )
        }
      )
      Tab(
        selected = selectedTab == 1,
        onClick = { selectedTab = 1 },
        text = {
          val pendingCount = approvals.count { it.status == "PENDING" }
          Text(
            "Approvals ($pendingCount)",
            color = if (selectedTab == 1) TitanGold else TextSecondaryDark,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp
          )
        }
      )
      Tab(
        selected = selectedTab == 2,
        onClick = { selectedTab = 2 },
        text = {
          Text(
            "Missions & Auto",
            color = if (selectedTab == 2) TitanCyan else TextSecondaryDark,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp
          )
        }
      )
      Tab(
        selected = selectedTab == 3,
        onClick = { selectedTab = 3 },
        text = {
          Text(
            "Audit & Health",
            color = if (selectedTab == 3) TitanEmerald else TextSecondaryDark,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp
          )
        }
      )
    }

    Spacer(modifier = Modifier.height(12.dp))

    when (selectedTab) {
      0 -> {
        // Tab 0: Agent Fleet
        LazyColumn(
          modifier = Modifier.fillMaxSize(),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          items(agents, key = { it.id }) { agent ->
            AgentControlCard(
              agent = agent,
              onRunNow = { viewModel.runAgent(agent.id) },
              onInspect = { viewModel.inspectAgent(agent) }
            )
          }
          item { Spacer(modifier = Modifier.height(16.dp)) }
        }
      }
      1 -> {
        // Tab 1: Approval Center Queue
        LazyColumn(
          modifier = Modifier.fillMaxSize(),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          item {
            Card(
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(10.dp),
              colors = CardDefaults.cardColors(containerColor = SlateElevated),
              border = androidx.compose.foundation.BorderStroke(1.dp, TitanGold.copy(alpha = 0.5f))
            ) {
              Column(modifier = Modifier.padding(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(Icons.Default.Security, contentDescription = null, tint = TitanGold, modifier = Modifier.size(16.dp))
                  Spacer(modifier = Modifier.width(6.dp))
                  Text("MANDATORY SAFETY BOUNDARY: APPROVAL QUEUE", style = MaterialTheme.typography.labelSmall, color = TitanGold, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = "No high-risk action (outbound recruiter messages, official portal submissions, or profile sync) executes without explicit authorization.",
                  style = MaterialTheme.typography.bodySmall,
                  color = TextSecondaryDark,
                  fontSize = 10.sp
                )
              }
            }
          }

          items(approvals, key = { it.id }) { approval ->
            ApprovalCard(
              approval = approval,
              onApprove = { viewModel.approveAction(approval.id) },
              onReject = { viewModel.rejectAction(approval.id) },
              onAuthorizeFuture = { viewModel.authorizeSimilarFutureActions(approval.id) }
            )
          }

          item { Spacer(modifier = Modifier.height(16.dp)) }
        }
      }
      2 -> {
        // Tab 2: Missions & Automations
        LazyColumn(
          modifier = Modifier.fillMaxSize(),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          item {
            Text(
              "OUTCOME-BASED MISSIONS (${missions.size})",
              style = MaterialTheme.typography.labelSmall,
              color = TitanCyan,
              fontWeight = FontWeight.Bold
            )
          }

          items(missions, key = { it.id }) { mission ->
            MissionCard(
              mission = mission,
              onUpdateProgress = { newProg -> viewModel.updateMissionProgress(mission.id, newProg, mission.status) }
            )
          }

          item {
            Spacer(modifier = Modifier.height(8.dp))

            // Navigation Card to Automation Rules Screen
            Card(
              colors = CardDefaults.cardColors(containerColor = SlateElevated),
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, TitanCyan.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                .clickable { viewModel.navigateTo(com.example.ui.viewmodel.TitanScreen.AUTOMATION_RULES) }
                .testTag("open_automation_rules_banner")
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                  Box(
                    modifier = Modifier
                      .size(36.dp)
                      .clip(RoundedCornerShape(8.dp))
                      .background(TitanCyan.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                  ) {
                    Icon(
                      imageVector = Icons.Default.Tune,
                      contentDescription = null,
                      tint = TitanCyan,
                      modifier = Modifier.size(20.dp)
                    )
                  }
                  Spacer(modifier = Modifier.width(10.dp))
                  Column {
                    Text(
                      text = "Configure Automation Rules",
                      style = MaterialTheme.typography.titleSmall,
                      fontWeight = FontWeight.Bold,
                      color = TextPrimaryDark
                    )
                    Text(
                      text = "Define triggers (e.g. 'If email from company X received') & automated responses",
                      style = MaterialTheme.typography.labelSmall,
                      color = TextSecondaryDark,
                      fontSize = 10.sp
                    )
                  }
                }
                Icon(
                  imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                  contentDescription = "Go to Rules",
                  tint = TitanCyan,
                  modifier = Modifier.size(18.dp)
                )
              }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Recharts / D3 Pipeline Status Distribution & Activity History Visualizer
            AutomationAnalyticsCharts(
              applications = applications,
              automationRules = automationRules,
              taskLogs = automationTaskLogs
            )

            Spacer(modifier = Modifier.height(12.dp))
            Text(
              "AUTONOMOUS TRIGGER & AUTOMATION ENGINE (${automations.size})",
              style = MaterialTheme.typography.labelSmall,
              color = TitanEmerald,
              fontWeight = FontWeight.Bold
            )
          }

          items(automations, key = { it.id }) { auto ->
            AutomationCard(
              automation = auto,
              onToggle = { enabled -> viewModel.toggleAutomation(auto.id, enabled) }
            )
          }

          item { Spacer(modifier = Modifier.height(16.dp)) }
        }
      }
      3 -> {
        // Tab 3: System Health & Audit Trail & Integration Hub
        LazyColumn(
          modifier = Modifier.fillMaxSize(),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          // System Health Cards Grid
          item {
            Text("SYSTEM HEALTH TELEMETRY", style = MaterialTheme.typography.labelSmall, color = TitanCyan, fontWeight = FontWeight.Bold)
          }

          item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
              healthMetrics.chunked(2).forEach { rowMetrics ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                  rowMetrics.forEach { metric ->
                    Card(
                      modifier = Modifier.weight(1f),
                      shape = RoundedCornerShape(8.dp),
                      colors = CardDefaults.cardColors(containerColor = SlateElevated),
                      border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
                    ) {
                      Column(modifier = Modifier.padding(10.dp)) {
                        Text(metric.metricName, style = MaterialTheme.typography.labelSmall, color = TextMutedDark, fontSize = 9.sp)
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(verticalAlignment = Alignment.Bottom) {
                          Text(metric.value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TitanEmerald)
                          Spacer(modifier = Modifier.width(4.dp))
                          Text(metric.unit, style = MaterialTheme.typography.labelSmall, color = TextSecondaryDark, fontSize = 10.sp)
                        }
                      }
                    }
                  }
                  if (rowMetrics.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                  }
                }
              }
            }
          }

          // Unified Integration Hub
          item {
            Spacer(modifier = Modifier.height(6.dp))
            Text("UNIFIED INTEGRATION HUB & SECURITY ADAPTERS", style = MaterialTheme.typography.labelSmall, color = TitanGold, fontWeight = FontWeight.Bold)
          }

          items(integrations, key = { it.id }) { integration ->
            IntegrationCard(integration = integration)
          }

          // Automation Task Logs (Room Offline Storage Vault)
          item {
            Spacer(modifier = Modifier.height(6.dp))
            AutomationTaskLogsCard(
              logs = automationTaskLogs,
              onDeleteLog = { viewModel.deleteAutomationTaskLog(it) }
            )
          }

          // Audit Logs ("What did ADI OS do?")
          item {
            Spacer(modifier = Modifier.height(6.dp))
            Text("AUDIT LOG: WHAT DID ADI OS DO?", style = MaterialTheme.typography.labelSmall, color = TitanCyan, fontWeight = FontWeight.Bold)
          }

          items(auditLogs, key = { it.id }) { log ->
            AuditLogCard(log = log)
          }

          item { Spacer(modifier = Modifier.height(16.dp)) }
        }
      }
    }
  }
}

@Composable
fun AgentControlCard(
  agent: AgentItem,
  onRunNow: () -> Unit,
  onInspect: () -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("agent_card_${agent.id}"),
    shape = RoundedCornerShape(10.dp),
    colors = CardDefaults.cardColors(containerColor = SlateCard),
    border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
  ) {
    Column(modifier = Modifier.padding(12.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(32.dp)
              .clip(RoundedCornerShape(6.dp))
              .background(SlateElevated),
            contentAlignment = Alignment.Center
          ) {
            Icon(Icons.Default.Memory, contentDescription = null, tint = TitanCyan, modifier = Modifier.size(18.dp))
          }
          Spacer(modifier = Modifier.width(8.dp))
          Column {
            Text(agent.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
            Text("Agent #${agent.agentNumber} • ${agent.roleCategory}", style = MaterialTheme.typography.labelSmall, color = TitanCyan, fontSize = 10.sp)
          }
        }

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(if (agent.status == "ACTIVE" || agent.status == "EXECUTING") TitanEmerald.copy(alpha = 0.15f) else SlateElevated)
            .border(1.dp, if (agent.status == "ACTIVE" || agent.status == "EXECUTING") TitanEmerald else SlateBorder, RoundedCornerShape(6.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
          Text(
            text = agent.status,
            style = MaterialTheme.typography.labelSmall,
            color = if (agent.status == "ACTIVE" || agent.status == "EXECUTING") TitanEmerald else TextMutedDark,
            fontWeight = FontWeight.Bold,
            fontSize = 9.sp
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = agent.currentTask,
        style = MaterialTheme.typography.bodySmall,
        color = TextSecondaryDark,
        fontSize = 11.sp,
        maxLines = 2
      )

      Spacer(modifier = Modifier.height(10.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text("Confidence: ${agent.confidenceScore}%", style = MaterialTheme.typography.labelSmall, color = TitanGold, fontSize = 10.sp)

        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          OutlinedButton(
            onClick = onInspect,
            shape = RoundedCornerShape(6.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimaryDark),
            modifier = Modifier.testTag("inspect_agent_${agent.id}")
          ) {
            Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.size(12.dp), tint = TitanCyan)
            Spacer(modifier = Modifier.width(4.dp))
            Text("Inspect", fontSize = 10.sp)
          }

          Button(
            onClick = onRunNow,
            shape = RoundedCornerShape(6.dp),
            colors = ButtonDefaults.buttonColors(containerColor = TitanCyan, contentColor = ObsidianDark),
            modifier = Modifier.testTag("run_agent_${agent.id}")
          ) {
            Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(12.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Run Now", fontSize = 10.sp, fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}

@Composable
fun ApprovalCard(
  approval: ApprovalItem,
  onApprove: () -> Unit,
  onReject: () -> Unit,
  onAuthorizeFuture: () -> Unit
) {
  val isPending = approval.status == "PENDING"
  val borderCol = when (approval.riskLevel) {
    "HIGH", "SENSITIVE" -> TitanCrimson
    "MEDIUM" -> TitanGold
    else -> TitanEmerald
  }

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("approval_card_${approval.id}"),
    shape = RoundedCornerShape(10.dp),
    colors = CardDefaults.cardColors(containerColor = SlateCard),
    border = androidx.compose.foundation.BorderStroke(1.dp, if (isPending) borderCol else SlateBorder)
  ) {
    Column(modifier = Modifier.padding(12.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(approval.actionTitle, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
          Text("Requested by ${approval.requestedByAgent} • ${approval.timestamp}", style = MaterialTheme.typography.labelSmall, color = TextMutedDark, fontSize = 10.sp)
        }

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(SlateElevated)
            .border(1.dp, borderCol.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
          Text(
            text = "${approval.riskLevel} RISK",
            style = MaterialTheme.typography.labelSmall,
            color = borderCol,
            fontWeight = FontWeight.Bold,
            fontSize = 9.sp
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Structured Breakdown Required by Specification
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .background(SlateElevated, RoundedCornerShape(6.dp))
          .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        Text("• WHY: ${approval.whyReason}", style = MaterialTheme.typography.bodySmall, color = TextSecondaryDark, fontSize = 10.sp)
        Text("• TOOL USED: ${approval.toolUsed}", style = MaterialTheme.typography.bodySmall, color = TitanCyan, fontSize = 10.sp)
        Text("• DATA INVOLVED: ${approval.dataInvolved}", style = MaterialTheme.typography.bodySmall, color = TextSecondaryDark, fontSize = 10.sp)
        Text("• EXPECTED RESULT: ${approval.expectedResult}", style = MaterialTheme.typography.bodySmall, color = TitanEmerald, fontSize = 10.sp)
      }

      Spacer(modifier = Modifier.height(10.dp))

      if (isPending) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Button(
            onClick = onApprove,
            colors = ButtonDefaults.buttonColors(containerColor = TitanEmerald, contentColor = ObsidianDark),
            shape = RoundedCornerShape(6.dp),
            modifier = Modifier.weight(1f).testTag("approve_btn_${approval.id}")
          ) {
            Icon(Icons.Default.Done, contentDescription = null, modifier = Modifier.size(12.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Approve", fontSize = 10.sp, fontWeight = FontWeight.Bold)
          }

          OutlinedButton(
            onClick = onReject,
            border = androidx.compose.foundation.BorderStroke(1.dp, TitanCrimson),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = TitanCrimson),
            shape = RoundedCornerShape(6.dp),
            modifier = Modifier.weight(1f).testTag("reject_btn_${approval.id}")
          ) {
            Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(12.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Reject", fontSize = 10.sp, fontWeight = FontWeight.Bold)
          }

          OutlinedButton(
            onClick = onAuthorizeFuture,
            border = androidx.compose.foundation.BorderStroke(1.dp, TitanGold),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = TitanGold),
            shape = RoundedCornerShape(6.dp),
            modifier = Modifier.weight(1.2f).testTag("auth_future_btn_${approval.id}")
          ) {
            Text("Auth Future", fontSize = 9.sp, fontWeight = FontWeight.Bold)
          }
        }
      } else {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(SlateElevated)
            .padding(vertical = 4.dp),
          contentAlignment = Alignment.Center
        ) {
          Text("Status: ${approval.status}", style = MaterialTheme.typography.labelSmall, color = TitanEmerald, fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}

@Composable
fun MissionCard(
  mission: MissionItem,
  onUpdateProgress: (Int) -> Unit
) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(10.dp),
    colors = CardDefaults.cardColors(containerColor = SlateCard),
    border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
  ) {
    Column(modifier = Modifier.padding(12.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(mission.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
          Text("Target: ${mission.targetMetric} • Deadline: ${mission.deadline}", style = MaterialTheme.typography.labelSmall, color = TitanGold, fontSize = 10.sp)
        }

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(SlateElevated)
            .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
          Text("${mission.progressPercent}% DONE", style = MaterialTheme.typography.labelSmall, color = TitanCyan, fontWeight = FontWeight.Bold, fontSize = 9.sp)
        }
      }

      Spacer(modifier = Modifier.height(6.dp))
      Text(mission.objective, style = MaterialTheme.typography.bodySmall, color = TextSecondaryDark, fontSize = 11.sp)
      Spacer(modifier = Modifier.height(6.dp))

      // Milestones & Key Results
      Text(mission.milestones, style = MaterialTheme.typography.bodySmall, color = TextMutedDark, fontSize = 10.sp, lineHeight = 14.sp)

      Spacer(modifier = Modifier.height(8.dp))

      // Progress bar
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(6.dp)
          .clip(RoundedCornerShape(3.dp))
          .background(SlateElevated)
      ) {
        Box(
          modifier = Modifier
            .fillMaxWidth(mission.progressPercent / 100f)
            .height(6.dp)
            .background(TitanCyan)
        )
      }
    }
  }
}

@Composable
fun AutomationCard(
  automation: AutomationItem,
  onToggle: (Boolean) -> Unit
) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(10.dp),
    colors = CardDefaults.cardColors(containerColor = SlateCard),
    border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(automation.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
          Spacer(modifier = Modifier.width(6.dp))
          Text("[${automation.permissionLevel}]", style = MaterialTheme.typography.labelSmall, color = TitanCyan, fontSize = 9.sp)
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text("Trigger: ${automation.triggerCondition}", style = MaterialTheme.typography.bodySmall, color = TitanGold, fontSize = 10.sp)
        Spacer(modifier = Modifier.height(2.dp))
        Text(automation.actionDescription, style = MaterialTheme.typography.bodySmall, color = TextSecondaryDark, fontSize = 10.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text("Executions: ${automation.executionCount} • Retries: ${automation.retryCount}", style = MaterialTheme.typography.labelSmall, color = TextMutedDark, fontSize = 9.sp)
      }

      Switch(
        checked = automation.isEnabled,
        onCheckedChange = onToggle,
        colors = SwitchDefaults.colors(
          checkedThumbColor = ObsidianDark,
          checkedTrackColor = TitanEmerald,
          uncheckedThumbColor = TextMutedDark,
          uncheckedTrackColor = SlateElevated
        )
      )
    }
  }
}

@Composable
fun IntegrationCard(
  integration: IntegrationItem
) {
  val isConn = integration.status == "CONNECTED"
  val statusColor = when (integration.status) {
    "CONNECTED" -> TitanEmerald
    "REQUIRES_AUTHORIZATION", "REQUIRES_API_KEY" -> TitanGold
    "DEMO_MODE" -> TitanCyan
    else -> TitanCrimson
  }

  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(8.dp),
    colors = CardDefaults.cardColors(containerColor = SlateCard),
    border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(10.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
        Text(integration.iconEmoji, fontSize = 20.sp)
        Spacer(modifier = Modifier.width(10.dp))
        Column {
          Text(integration.serviceName, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = TextPrimaryDark, fontSize = 12.sp)
          Text(integration.description, style = MaterialTheme.typography.bodySmall, color = TextSecondaryDark, fontSize = 10.sp, maxLines = 1)
        }
      }

      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(6.dp))
          .background(SlateElevated)
          .border(1.dp, statusColor.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
          .padding(horizontal = 6.dp, vertical = 2.dp)
      ) {
        Text(
          text = integration.status,
          style = MaterialTheme.typography.labelSmall,
          color = statusColor,
          fontWeight = FontWeight.Bold,
          fontSize = 8.sp
        )
      }
    }
  }
}

@Composable
fun AuditLogCard(
  log: AgentAuditLog
) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(8.dp),
    colors = CardDefaults.cardColors(containerColor = SlateElevated),
    border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder.copy(alpha = 0.5f))
  ) {
    Column(modifier = Modifier.padding(10.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(6.dp)
              .clip(CircleShape)
              .background(TitanEmerald)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(log.agentName, style = MaterialTheme.typography.labelSmall, color = TitanCyan, fontWeight = FontWeight.Bold)
        }
        Text(log.timestamp, style = MaterialTheme.typography.labelSmall, color = TextMutedDark, fontSize = 9.sp)
      }

      Spacer(modifier = Modifier.height(4.dp))
      Text("Action: ${log.actionType}", style = MaterialTheme.typography.bodySmall, color = TextPrimaryDark, fontWeight = FontWeight.SemiBold, fontSize = 11.sp)
      Text("Input: ${log.inputSummary}", style = MaterialTheme.typography.bodySmall, color = TextSecondaryDark, fontSize = 10.sp)
      Text("Result: ${log.outputSummary}", style = MaterialTheme.typography.bodySmall, color = TitanEmerald, fontSize = 10.sp)
    }
  }
}
