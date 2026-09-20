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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AgentItem
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

@Composable
fun AgentInspectDialog(
  agent: AgentItem,
  onDismissRequest: () -> Unit,
  onStart: () -> Unit,
  onPause: () -> Unit,
  onStop: () -> Unit,
  onRetry: () -> Unit
) {
  val scrollState = rememberScrollState()

  AlertDialog(
    onDismissRequest = onDismissRequest,
    containerColor = SlateCard,
    titleContentColor = TextPrimaryDark,
    shape = RoundedCornerShape(16.dp),
    modifier = Modifier.testTag("agent_inspect_dialog"),
    title = {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(36.dp)
              .clip(RoundedCornerShape(8.dp))
              .background(SlateElevated)
              .border(1.dp, TitanCyan.copy(alpha = 0.5f), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
          ) {
            Icon(Icons.Default.Memory, contentDescription = null, tint = TitanCyan, modifier = Modifier.size(20.dp))
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(agent.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
            Text("Agent #${agent.agentNumber} • Category: ${agent.roleCategory}", style = MaterialTheme.typography.labelSmall, color = TitanCyan, fontSize = 11.sp)
          }
        }

        IconButton(onClick = onDismissRequest) {
          Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMutedDark)
        }
      }
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(scrollState)
          .padding(vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        // Status & Confidence Banner
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(10.dp),
          colors = CardDefaults.cardColors(containerColor = SlateElevated),
          border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text("LIFECYCLE STATUS", style = MaterialTheme.typography.labelSmall, color = TextMutedDark, fontSize = 9.sp)
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(if (agent.status == "ACTIVE" || agent.status == "EXECUTING") TitanEmerald else TitanGold)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(agent.status, style = MaterialTheme.typography.bodyMedium, color = TextPrimaryDark, fontWeight = FontWeight.Bold)
              }
            }

            Column(horizontalAlignment = Alignment.End) {
              Text("CONFIDENCE SCORE", style = MaterialTheme.typography.labelSmall, color = TextMutedDark, fontSize = 9.sp)
              Text("${agent.confidenceScore}%", style = MaterialTheme.typography.bodyMedium, color = TitanEmerald, fontWeight = FontWeight.Bold)
            }
          }
        }

        // Current Task & Execution
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(10.dp),
          colors = CardDefaults.cardColors(containerColor = SlateElevated),
          border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
        ) {
          Column(modifier = Modifier.padding(12.dp)) {
            Text("CURRENT ACTIVE MISSION & TASK", style = MaterialTheme.typography.labelSmall, color = TitanCyan, fontWeight = FontWeight.Bold, fontSize = 9.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(agent.currentTask, style = MaterialTheme.typography.bodySmall, color = TextPrimaryDark, fontSize = 12.sp, lineHeight = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Last Activity: ${agent.lastRunTimestamp}", style = MaterialTheme.typography.labelSmall, color = TextMutedDark, fontSize = 10.sp)
          }
        }

        // Permission & Tool Scopes
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(10.dp),
          colors = CardDefaults.cardColors(containerColor = SlateElevated),
          border = androidx.compose.foundation.BorderStroke(1.dp, TitanEmerald.copy(alpha = 0.4f))
        ) {
          Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Security, contentDescription = null, tint = TitanEmerald, modifier = Modifier.size(14.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("AUTHORIZED PERMISSION & TOOL SCOPES", style = MaterialTheme.typography.labelSmall, color = TitanEmerald, fontWeight = FontWeight.Bold, fontSize = 9.sp)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(agent.permissionScope, style = MaterialTheme.typography.bodySmall, color = TextSecondaryDark, fontSize = 11.sp, fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace)
          }
        }

        // Operational Metrics (Success vs Failure)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          Card(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = SlateElevated)
          ) {
            Column(modifier = Modifier.padding(10.dp)) {
              Text("SUCCESS PASSES", style = MaterialTheme.typography.labelSmall, color = TitanEmerald, fontSize = 9.sp, fontWeight = FontWeight.Bold)
              Text("${agent.successCount}", style = MaterialTheme.typography.titleMedium, color = TextPrimaryDark, fontWeight = FontWeight.Bold)
            }
          }

          Card(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = SlateElevated)
          ) {
            Column(modifier = Modifier.padding(10.dp)) {
              Text("FAULT RETRIES", style = MaterialTheme.typography.labelSmall, color = TitanCrimson, fontSize = 9.sp, fontWeight = FontWeight.Bold)
              Text("${agent.failureCount}", style = MaterialTheme.typography.titleMedium, color = TextPrimaryDark, fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    },
    confirmButton = {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        Button(
          onClick = {
            onStart()
            onDismissRequest()
          },
          colors = ButtonDefaults.buttonColors(containerColor = TitanCyan, contentColor = ObsidianDark),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier.weight(1f)
        ) {
          Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(14.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Run Now", fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }

        OutlinedButton(
          onClick = {
            onPause()
            onDismissRequest()
          },
          shape = RoundedCornerShape(8.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, TitanGold),
          colors = ButtonDefaults.outlinedButtonColors(contentColor = TitanGold),
          modifier = Modifier.weight(1f)
        ) {
          Icon(Icons.Default.Pause, contentDescription = null, modifier = Modifier.size(14.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Pause", fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }

        OutlinedButton(
          onClick = {
            onRetry()
            onDismissRequest()
          },
          shape = RoundedCornerShape(8.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, TitanEmerald),
          colors = ButtonDefaults.outlinedButtonColors(contentColor = TitanEmerald),
          modifier = Modifier.weight(1f)
        ) {
          Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(14.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Retry", fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
      }
    }
  )
}
