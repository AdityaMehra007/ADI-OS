package com.example.ui.components

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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.data.model.AutomationTaskLog
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
fun AutomationTaskLogsCard(
  logs: List<AutomationTaskLog>,
  onDeleteLog: (Long) -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedStatusFilter by remember { mutableStateOf("ALL") }
  val statuses = listOf("ALL", "SUCCESS", "FAILED", "RUNNING")

  val filteredLogs = if (selectedStatusFilter == "ALL") {
    logs
  } else {
    logs.filter { it.status.equals(selectedStatusFilter, ignoreCase = true) }
  }

  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("automation_task_logs_card"),
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = SlateCard),
    border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Memory, contentDescription = null, tint = TitanCyan, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              "AUTOMATION TASK AUDIT LOGS",
              style = MaterialTheme.typography.labelSmall,
              color = TitanCyan,
              fontWeight = FontWeight.Bold,
              fontSize = 11.sp
            )
          }
          Text(
            "Persisted in Room DB · ${logs.size} offline task logs captured",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondaryDark,
            fontSize = 10.sp
          )
        }

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(SlateElevated)
            .border(1.dp, TitanEmerald.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
          Text(
            "Offline Accessible",
            style = MaterialTheme.typography.labelSmall,
            color = TitanEmerald,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }

      // Filter tabs
      ScrollableTabRow(
        selectedTabIndex = statuses.indexOf(selectedStatusFilter).coerceAtLeast(0),
        containerColor = Color.Transparent,
        contentColor = TitanCyan,
        edgePadding = 0.dp,
        indicator = { tabPositions ->
          val index = statuses.indexOf(selectedStatusFilter).coerceAtLeast(0)
          if (index < tabPositions.size) {
            TabRowDefaults.SecondaryIndicator(
              Modifier.tabIndicatorOffset(tabPositions[index]),
              color = TitanCyan,
              height = 2.dp
            )
          }
        },
        divider = {}
      ) {
        statuses.forEach { st ->
          val isSelected = selectedStatusFilter == st
          Tab(
            selected = isSelected,
            onClick = { selectedStatusFilter = st },
            text = {
              Text(
                st,
                color = if (isSelected) TitanCyan else TextSecondaryDark,
                fontSize = 10.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
              )
            }
          )
        }
      }

      if (filteredLogs.isEmpty()) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(SlateElevated)
            .padding(16.dp),
          contentAlignment = Alignment.Center
        ) {
          Text("No automation logs recorded yet.", style = MaterialTheme.typography.bodySmall, color = TextSecondaryDark)
        }
      } else {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          filteredLogs.forEach { log ->
            AutomationTaskLogItem(
              log = log,
              onDelete = { onDeleteLog(log.id) }
            )
          }
        }
      }
    }
  }
}

@Composable
fun AutomationTaskLogItem(
  log: AutomationTaskLog,
  onDelete: () -> Unit
) {
  var isExpanded by remember { mutableStateOf(false) }

  val statusColor = when (log.status.uppercase()) {
    "SUCCESS" -> TitanEmerald
    "FAILED", "ERROR" -> TitanCrimson
    "WARNING" -> TitanGold
    else -> TitanCyan
  }

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("automation_log_item_${log.id}"),
    shape = RoundedCornerShape(8.dp),
    colors = CardDefaults.cardColors(containerColor = SlateElevated),
    border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(10.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
          Box(
            modifier = Modifier
              .size(8.dp)
              .clip(CircleShape)
              .background(statusColor)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = log.taskName,
            style = MaterialTheme.typography.titleSmall,
            color = TextPrimaryDark,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp
          )
          Spacer(modifier = Modifier.width(6.dp))
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(4.dp))
              .background(SlateCard)
              .padding(horizontal = 4.dp, vertical = 2.dp)
          ) {
            Text(
              log.triggerSource,
              style = MaterialTheme.typography.labelSmall,
              color = TextMutedDark,
              fontSize = 8.sp
            )
          }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          if (log.executionDurationMs > 0) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Speed, contentDescription = null, tint = TextMutedDark, modifier = Modifier.size(10.dp))
              Spacer(modifier = Modifier.width(2.dp))
              Text(
                "${log.executionDurationMs}ms",
                style = MaterialTheme.typography.labelSmall,
                color = TextMutedDark,
                fontSize = 9.sp
              )
              Spacer(modifier = Modifier.width(8.dp))
            }
          }

          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(4.dp))
              .background(statusColor.copy(alpha = 0.15f))
              .border(1.dp, statusColor.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
              .padding(horizontal = 6.dp, vertical = 2.dp)
          ) {
            Text(
              log.status,
              style = MaterialTheme.typography.labelSmall,
              color = statusColor,
              fontWeight = FontWeight.Bold,
              fontSize = 8.sp
            )
          }

          IconButton(
            onClick = onDelete,
            modifier = Modifier.size(20.dp).padding(start = 4.dp)
          ) {
            Icon(Icons.Default.Delete, contentDescription = "Delete Log", tint = TextMutedDark, modifier = Modifier.size(12.dp))
          }
        }
      }

      Spacer(modifier = Modifier.height(4.dp))

      Text(
        text = log.summary,
        style = MaterialTheme.typography.bodySmall,
        color = TextSecondaryDark,
        fontSize = 10.sp
      )

      if (log.itemsDiscoveredCount > 0 || log.highPriorityMatchesCount > 0) {
        Spacer(modifier = Modifier.height(4.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          if (log.itemsDiscoveredCount > 0) {
            Text(
              "• Discovered: ${log.itemsDiscoveredCount} roles",
              style = MaterialTheme.typography.labelSmall,
              color = TitanCyan,
              fontSize = 9.sp
            )
          }
          if (log.highPriorityMatchesCount > 0) {
            Text(
              "• High-Fit Matches: ${log.highPriorityMatchesCount}",
              style = MaterialTheme.typography.labelSmall,
              color = TitanEmerald,
              fontSize = 9.sp
            )
          }
        }
      }

      if (log.details.isNotBlank()) {
        Spacer(modifier = Modifier.height(4.dp))
        Row(
          modifier = Modifier
            .clickable { isExpanded = !isExpanded }
            .padding(vertical = 2.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            if (isExpanded) "Hide Details" else "View Technical Details",
            style = MaterialTheme.typography.labelSmall,
            color = TitanCyan,
            fontSize = 9.sp
          )
          Icon(
            if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
            contentDescription = null,
            tint = TitanCyan,
            modifier = Modifier.size(10.dp)
          )
        }

        AnimatedVisibility(visible = isExpanded) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(4.dp))
              .background(SlateCard)
              .padding(8.dp)
          ) {
            Text(
              text = log.details,
              style = MaterialTheme.typography.bodySmall,
              color = TextMutedDark,
              fontFamily = FontFamily.Monospace,
              fontSize = 9.sp
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = log.formattedTime,
        style = MaterialTheme.typography.labelSmall,
        color = TextMutedDark,
        fontSize = 8.sp
      )
    }
  }
}
