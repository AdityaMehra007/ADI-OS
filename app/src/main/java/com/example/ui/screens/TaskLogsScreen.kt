package com.example.ui.screens

import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.AutomationTask
import com.example.data.model.AutomationTaskLog
import com.example.ui.theme.ObsidianDark
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateCard
import com.example.ui.theme.SlateElevated
import com.example.ui.theme.TextMutedDark
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TitanCrimson
import com.example.ui.theme.TitanCyan
import com.example.ui.theme.TitanEmerald
import com.example.ui.theme.TitanGold
import com.example.ui.theme.TitanViolet
import com.example.ui.viewmodel.LogExportFormat
import com.example.ui.viewmodel.TaskLogsViewModel

private val STATUS_FILTERS = listOf("ALL", "SUCCESS", "FAILED", "RUNNING", "WARNING")

@Composable
fun TaskLogsScreen(
  viewModel: TaskLogsViewModel,
  modifier: Modifier = Modifier
) {
  val logs by viewModel.filteredLogs.collectAsState()
  val allLogs by viewModel.allTaskLogs.collectAsState()
  val tasks by viewModel.automationTasks.collectAsState()
  val statusFilter by viewModel.statusFilter.collectAsState()
  val searchQuery by viewModel.searchQuery.collectAsState()
  val selectedLogIds by viewModel.selectedLogIds.collectAsState()
  val isSelectionMode by viewModel.isSelectionMode.collectAsState()

  val context = LocalContext.current
  var isClearConfirmOpen by remember { mutableStateOf(false) }
  var isRunTaskDialogOpen by remember { mutableStateOf(false) }
  var logToDelete by remember { mutableStateOf<AutomationTaskLog?>(null) }
  var isBatchDeleteConfirmOpen by remember { mutableStateOf(false) }
  var isBatchExportDialogOpen by remember { mutableStateOf(false) }

  val totalRuns = allLogs.size
  val successRuns = allLogs.count { it.status.equals("SUCCESS", ignoreCase = true) }
  val successRate = if (totalRuns > 0) (successRuns * 100 / totalRuns) else 100
  val totalItemsDiscovered = allLogs.sumOf { it.itemsDiscoveredCount }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(ObsidianDark)
      .testTag("task_logs_screen")
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp, vertical = 12.dp)
        .widthIn(max = 800.dp)
        .align(Alignment.TopCenter)
    ) {
      // Header
      TaskLogsHeader(
        totalLogs = totalRuns,
        isSelectionMode = isSelectionMode,
        selectedCount = selectedLogIds.size,
        onToggleSelectionMode = { viewModel.toggleSelectionMode() },
        onClearAll = { isClearConfirmOpen = true },
        onTriggerTask = { isRunTaskDialogOpen = true }
      )

      Spacer(modifier = Modifier.height(10.dp))

      // KPI Metrics Bar
      TaskLogsMetricsBar(
        totalRuns = totalRuns,
        successRate = successRate,
        itemsDiscovered = totalItemsDiscovered,
        activeTasks = tasks.count { it.isEnabled }
      )

      Spacer(modifier = Modifier.height(12.dp))

      // Search Bar
      OutlinedTextField(
        value = searchQuery,
        onValueChange = { viewModel.setSearchQuery(it) },
        placeholder = { Text("Search task logs, triggers, summary...", color = TextMutedDark) },
        leadingIcon = {
          Icon(Icons.Default.Search, contentDescription = "Search", tint = TitanCyan)
        },
        trailingIcon = {
          if (searchQuery.isNotEmpty()) {
            IconButton(
              onClick = { viewModel.setSearchQuery("") },
              modifier = Modifier.testTag("clear_task_logs_search_btn")
            ) {
              Icon(Icons.Default.Clear, contentDescription = "Clear", tint = TextMutedDark)
            }
          }
        },
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
          focusedTextColor = TextPrimaryDark,
          unfocusedTextColor = TextPrimaryDark,
          focusedBorderColor = TitanCyan,
          unfocusedBorderColor = SlateBorder,
          focusedContainerColor = SlateCard,
          unfocusedContainerColor = SlateCard
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("task_logs_search_input")
      )

      Spacer(modifier = Modifier.height(10.dp))

      // Status Filter Tabs
      StatusFilterRow(
        filters = STATUS_FILTERS,
        selectedFilter = statusFilter,
        onFilterSelect = { viewModel.setStatusFilter(it) }
      )

      Spacer(modifier = Modifier.height(8.dp))

      // Multi-selection batch action bar
      AnimatedVisibility(
        visible = isSelectionMode || selectedLogIds.isNotEmpty(),
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut()
      ) {
        val visibleIds = logs.map { it.id }
        val isAllVisibleSelected = visibleIds.isNotEmpty() && visibleIds.all { selectedLogIds.contains(it) }
        TaskLogsBatchActionBar(
          selectedCount = selectedLogIds.size,
          allSelected = isAllVisibleSelected,
          onToggleSelectAll = {
            if (isAllVisibleSelected) {
              viewModel.deselectAllLogs()
            } else {
              viewModel.selectAllLogs(visibleIds)
            }
          },
          onBatchExport = { isBatchExportDialogOpen = true },
          onBatchDelete = { isBatchDeleteConfirmOpen = true },
          onExitSelectionMode = { viewModel.setSelectionMode(false) }
        )
      }

      Spacer(modifier = Modifier.height(6.dp))

      // Logs List or Empty State
      if (logs.isEmpty()) {
        EmptyTaskLogsState(
          isFiltered = searchQuery.isNotBlank() || statusFilter != "ALL",
          onResetFilter = {
            viewModel.setSearchQuery("")
            viewModel.setStatusFilter("ALL")
          },
          onTriggerFirstRun = { isRunTaskDialogOpen = true }
        )
      } else {
        LazyColumn(
          modifier = Modifier
            .fillMaxSize()
            .testTag("task_logs_list"),
          verticalArrangement = Arrangement.spacedBy(10.dp),
          contentPadding = PaddingValues(bottom = 80.dp)
        ) {
          items(logs, key = { it.id }) { log ->
            AutomationTaskLogCard(
              log = log,
              isSelected = selectedLogIds.contains(log.id),
              isSelectionMode = isSelectionMode || selectedLogIds.isNotEmpty(),
              onToggleSelect = { viewModel.toggleLogSelection(log.id) },
              onLongClick = {
                if (!isSelectionMode) {
                  viewModel.setSelectionMode(true)
                }
                viewModel.toggleLogSelection(log.id)
              },
              onDelete = { logToDelete = log }
            )
          }
        }
      }
    }

    // FAB to run a task (hide when in selection mode for clear batch focus)
    if (!isSelectionMode && selectedLogIds.isEmpty()) {
      FloatingActionButton(
        onClick = { isRunTaskDialogOpen = true },
        containerColor = TitanCyan,
        contentColor = ObsidianDark,
        shape = CircleShape,
        modifier = Modifier
          .align(Alignment.BottomEnd)
          .padding(20.dp)
          .testTag("trigger_task_fab")
      ) {
        Icon(Icons.Default.PlayArrow, contentDescription = "Run Automation Task")
      }
    }

    // Trigger Task Dialog
    if (isRunTaskDialogOpen) {
      RunAutomationTaskDialog(
        tasks = tasks,
        onDismiss = { isRunTaskDialogOpen = false },
        onRunTask = { task ->
          viewModel.runTaskNow(task)
          isRunTaskDialogOpen = false
        }
      )
    }

    // Delete Single Log Dialog
    logToDelete?.let { log ->
      AlertDialog(
        onDismissRequest = { logToDelete = null },
        title = { Text("Delete Task Log?", color = TextPrimaryDark) },
        text = {
          Text("Delete log entry #${log.id} for ${log.taskName}?", color = TextMutedDark)
        },
        confirmButton = {
          Button(
            onClick = {
              viewModel.deleteLog(log.id)
              logToDelete = null
            },
            colors = ButtonDefaults.buttonColors(containerColor = TitanCrimson),
            modifier = Modifier.testTag("confirm_delete_log_btn")
          ) {
            Text("Delete", color = Color.White)
          }
        },
        dismissButton = {
          TextButton(onClick = { logToDelete = null }) {
            Text("Cancel", color = TextMutedDark)
          }
        },
        containerColor = SlateCard,
        shape = RoundedCornerShape(14.dp)
      )
    }

    // Batch Delete Confirmation Dialog
    if (isBatchDeleteConfirmOpen) {
      val count = selectedLogIds.size
      AlertDialog(
        onDismissRequest = { isBatchDeleteConfirmOpen = false },
        title = { Text("Delete $count Selected Logs?", color = TextPrimaryDark) },
        text = {
          Text(
            "Permanently delete $count selected automation task log entries from Room database? This cannot be undone.",
            color = TextMutedDark
          )
        },
        confirmButton = {
          Button(
            onClick = {
              viewModel.deleteSelectedLogs()
              isBatchDeleteConfirmOpen = false
              Toast.makeText(context, "Deleted $count task logs", Toast.LENGTH_SHORT).show()
            },
            colors = ButtonDefaults.buttonColors(containerColor = TitanCrimson),
            modifier = Modifier.testTag("confirm_batch_delete_btn")
          ) {
            Text("Delete ($count)", color = Color.White)
          }
        },
        dismissButton = {
          TextButton(
            onClick = { isBatchDeleteConfirmOpen = false },
            modifier = Modifier.testTag("cancel_batch_delete_btn")
          ) {
            Text("Cancel", color = TextMutedDark)
          }
        },
        containerColor = SlateCard,
        shape = RoundedCornerShape(14.dp)
      )
    }

    // Batch Export Dialog
    if (isBatchExportDialogOpen) {
      val selectedLogsList = remember(selectedLogIds, allLogs) {
        allLogs.filter { selectedLogIds.contains(it.id) }
      }
      BatchExportLogsDialog(
        selectedLogs = selectedLogsList,
        onDismiss = { isBatchExportDialogOpen = false },
        viewModel = viewModel
      )
    }

    // Clear All Logs Dialog
    if (isClearConfirmOpen) {
      AlertDialog(
        onDismissRequest = { isClearConfirmOpen = false },
        title = { Text("Clear All Audit Logs?", color = TextPrimaryDark) },
        text = {
          Text("This will purge all background task telemetry and audit logs from Room database.", color = TextMutedDark)
        },
        confirmButton = {
          Button(
            onClick = {
              viewModel.clearAllLogs()
              isClearConfirmOpen = false
            },
            colors = ButtonDefaults.buttonColors(containerColor = TitanCrimson),
            modifier = Modifier.testTag("confirm_clear_all_logs_btn")
          ) {
            Text("Clear All", color = Color.White)
          }
        },
        dismissButton = {
          TextButton(onClick = { isClearConfirmOpen = false }) {
            Text("Cancel", color = TextMutedDark)
          }
        },
        containerColor = SlateCard,
        shape = RoundedCornerShape(14.dp)
      )
    }
  }
}

@Composable
private fun TaskLogsHeader(
  totalLogs: Int,
  isSelectionMode: Boolean,
  selectedCount: Int,
  onToggleSelectionMode: () -> Unit,
  onClearAll: () -> Unit,
  onTriggerTask: () -> Unit
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Column {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.History,
          contentDescription = null,
          tint = TitanCyan,
          modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "Automation Task Logs",
          style = MaterialTheme.typography.titleLarge,
          color = TextPrimaryDark,
          fontWeight = FontWeight.Bold
        )
      }
      Text(
        text = "Audit trail & telemetry for background agents and crawlers",
        style = MaterialTheme.typography.bodySmall,
        color = TextMutedDark
      )
    }

    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
      if (totalLogs > 0) {
        OutlinedButton(
          onClick = onToggleSelectionMode,
          border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSelectionMode) TitanCyan else SlateBorder
          ),
          colors = ButtonDefaults.outlinedButtonColors(
            contentColor = if (isSelectionMode) TitanCyan else TextMutedDark,
            containerColor = if (isSelectionMode) TitanCyan.copy(alpha = 0.12f) else Color.Transparent
          ),
          shape = RoundedCornerShape(8.dp),
          contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
          modifier = Modifier.testTag("toggle_selection_mode_btn")
        ) {
          Icon(
            imageVector = if (isSelectionMode) Icons.Default.Check else Icons.Default.SelectAll,
            contentDescription = null,
            modifier = Modifier.size(15.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = if (isSelectionMode) "Done" else "Select",
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold
          )
        }

        IconButton(
          onClick = onClearAll,
          modifier = Modifier
            .size(36.dp)
            .testTag("clear_all_task_logs_btn")
        ) {
          Icon(
            Icons.Default.DeleteSweep,
            contentDescription = "Clear All Logs",
            tint = TitanCrimson.copy(alpha = 0.8f),
            modifier = Modifier.size(20.dp)
          )
        }
      }

      OutlinedButton(
        onClick = onTriggerTask,
        border = androidx.compose.foundation.BorderStroke(1.dp, TitanCyan.copy(alpha = 0.8f)),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = TitanCyan),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
        modifier = Modifier.testTag("trigger_task_header_btn")
      ) {
        Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text("Run Task", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
      }
    }
  }
}

@Composable
private fun TaskLogsBatchActionBar(
  selectedCount: Int,
  allSelected: Boolean,
  onToggleSelectAll: () -> Unit,
  onBatchExport: () -> Unit,
  onBatchDelete: () -> Unit,
  onExitSelectionMode: () -> Unit
) {
  Surface(
    shape = RoundedCornerShape(12.dp),
    color = SlateCard,
    border = androidx.compose.foundation.BorderStroke(1.dp, TitanCyan.copy(alpha = 0.5f)),
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp)
      .testTag("task_logs_batch_action_bar")
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 12.dp, vertical = 8.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        // Selection count pill
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(TitanCyan.copy(alpha = 0.15f))
            .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
          Text(
            text = "$selectedCount Selected",
            color = TitanCyan,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.testTag("batch_selection_count_label")
          )
        }

        // Select All / Deselect All
        TextButton(
          onClick = onToggleSelectAll,
          contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
          modifier = Modifier.testTag("select_all_logs_btn")
        ) {
          Icon(
            imageVector = if (allSelected) Icons.Default.Close else Icons.Default.SelectAll,
            contentDescription = null,
            tint = TitanCyan,
            modifier = Modifier.size(14.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = if (allSelected) "Deselect All" else "Select All",
            color = TitanCyan,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold
          )
        }
      }

      // Actions: Export, Delete, Exit
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        // Batch Export Button
        Button(
          onClick = onBatchExport,
          enabled = selectedCount > 0,
          colors = ButtonDefaults.buttonColors(
            containerColor = TitanCyan,
            contentColor = ObsidianDark,
            disabledContainerColor = SlateElevated,
            disabledContentColor = TextMutedDark
          ),
          shape = RoundedCornerShape(8.dp),
          contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
          modifier = Modifier.testTag("batch_export_logs_btn")
        ) {
          Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(13.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Export ($selectedCount)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }

        // Batch Delete Button
        Button(
          onClick = onBatchDelete,
          enabled = selectedCount > 0,
          colors = ButtonDefaults.buttonColors(
            containerColor = TitanCrimson,
            contentColor = Color.White,
            disabledContainerColor = SlateElevated,
            disabledContentColor = TextMutedDark
          ),
          shape = RoundedCornerShape(8.dp),
          contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
          modifier = Modifier.testTag("batch_delete_logs_btn")
        ) {
          Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(13.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Delete ($selectedCount)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }

        // Close Selection
        IconButton(
          onClick = onExitSelectionMode,
          modifier = Modifier
            .size(28.dp)
            .testTag("exit_selection_mode_btn")
        ) {
          Icon(
            Icons.Default.Close,
            contentDescription = "Exit Selection",
            tint = TextMutedDark,
            modifier = Modifier.size(16.dp)
          )
        }
      }
    }
  }
}

@Composable
private fun BatchExportLogsDialog(
  selectedLogs: List<AutomationTaskLog>,
  onDismiss: () -> Unit,
  viewModel: TaskLogsViewModel
) {
  val context = LocalContext.current
  val clipboardManager = LocalClipboardManager.current
  var selectedFormat by remember { mutableStateOf(LogExportFormat.JSON) }

  val exportedContent = remember(selectedLogs, selectedFormat) {
    viewModel.exportLogs(selectedLogs, selectedFormat)
  }

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(16.dp),
      color = SlateCard,
      border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder),
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 4.dp)
        .testTag("batch_export_dialog")
    ) {
      Column(modifier = Modifier.padding(18.dp)) {
        // Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.FileDownload, contentDescription = null, tint = TitanCyan, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text(
                text = "Batch Export Logs",
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimaryDark,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "${selectedLogs.size} task log records selected",
                style = MaterialTheme.typography.bodySmall,
                color = TextMutedDark,
                fontSize = 11.sp
              )
            }
          }
          IconButton(
            onClick = onDismiss,
            modifier = Modifier
              .size(28.dp)
              .testTag("close_export_dialog_btn")
          ) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMutedDark)
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Format selection chips
        Text(
          text = "Select Export Format:",
          style = MaterialTheme.typography.labelSmall,
          color = TextMutedDark,
          fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(6.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          LogExportFormat.values().forEach { format ->
            val isSelected = selectedFormat == format
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
                .clickable { selectedFormat = format }
                .padding(vertical = 8.dp)
                .testTag("export_format_${format.extension}"),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = format.label.substringBefore(" ("),
                color = if (isSelected) TitanCyan else TextMutedDark,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                fontSize = 11.sp
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Preview box
        Text(
          text = "Data Preview (${selectedFormat.label}):",
          style = MaterialTheme.typography.labelSmall,
          color = TextMutedDark,
          fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(6.dp))
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = ObsidianDark,
          border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder),
          modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
        ) {
          Box(
            modifier = Modifier
              .fillMaxSize()
              .padding(10.dp)
              .verticalScroll(rememberScrollState())
          ) {
            Text(
              text = exportedContent,
              style = MaterialTheme.typography.bodySmall,
              color = TextPrimaryDark,
              fontFamily = FontFamily.Monospace,
              fontSize = 10.sp,
              modifier = Modifier.testTag("export_preview_text")
            )
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Action Buttons: Copy to Clipboard & Share via Android Intent
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          OutlinedButton(
            onClick = {
              clipboardManager.setText(AnnotatedString(exportedContent))
              Toast.makeText(
                context,
                "Copied ${selectedLogs.size} logs (${selectedFormat.name}) to clipboard",
                Toast.LENGTH_SHORT
              ).show()
            },
            modifier = Modifier
              .weight(1f)
              .testTag("copy_export_clipboard_btn"),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = TitanCyan),
            border = androidx.compose.foundation.BorderStroke(1.dp, TitanCyan.copy(alpha = 0.8f))
          ) {
            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(15.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Copy Text", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
          }

          Button(
            onClick = {
              val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = selectedFormat.mimeType
                putExtra(Intent.EXTRA_TEXT, exportedContent)
                putExtra(Intent.EXTRA_TITLE, "Titan Automation Logs (${selectedLogs.size} items)")
                putExtra(Intent.EXTRA_SUBJECT, "Titan Task Logs Export - ${selectedFormat.name}")
              }
              val chooser = Intent.createChooser(shareIntent, "Share Task Logs Export")
              context.startActivity(chooser)
            },
            modifier = Modifier
              .weight(1f)
              .testTag("share_export_btn"),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = TitanCyan, contentColor = ObsidianDark)
          ) {
            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(15.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Share File", fontSize = 12.sp, fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}

@Composable
private fun TaskLogsMetricsBar(
  totalRuns: Int,
  successRate: Int,
  itemsDiscovered: Int,
  activeTasks: Int
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    MetricPill(
      title = "TOTAL RUNS",
      value = "$totalRuns",
      valueColor = TitanCyan,
      modifier = Modifier.weight(1f)
    )
    MetricPill(
      title = "SUCCESS RATE",
      value = "$successRate%",
      valueColor = if (successRate >= 90) TitanEmerald else TitanGold,
      modifier = Modifier.weight(1f)
    )
    MetricPill(
      title = "DISCOVERED",
      value = "$itemsDiscovered",
      valueColor = TitanViolet,
      modifier = Modifier.weight(1f)
    )
    MetricPill(
      title = "ACTIVE TASKS",
      value = "$activeTasks",
      valueColor = TitanGold,
      modifier = Modifier.weight(1f)
    )
  }
}

@Composable
private fun MetricPill(
  title: String,
  value: String,
  valueColor: Color,
  modifier: Modifier = Modifier
) {
  Card(
    shape = RoundedCornerShape(8.dp),
    colors = CardDefaults.cardColors(containerColor = SlateCard),
    border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder),
    modifier = modifier
  ) {
    Column(
      modifier = Modifier.padding(vertical = 8.dp, horizontal = 10.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(text = title, color = TextMutedDark, fontSize = 9.sp, fontWeight = FontWeight.SemiBold)
      Spacer(modifier = Modifier.height(2.dp))
      Text(text = value, color = valueColor, fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }
  }
}

@Composable
private fun StatusFilterRow(
  filters: List<String>,
  selectedFilter: String,
  onFilterSelect: (String) -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .horizontalScroll(rememberScrollState()),
    horizontalArrangement = Arrangement.spacedBy(6.dp)
  ) {
    filters.forEach { filter ->
      val isSelected = selectedFilter.equals(filter, ignoreCase = true)
      val color = getStatusColor(filter)
      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(8.dp))
          .background(if (isSelected) color.copy(alpha = 0.2f) else SlateCard)
          .border(
            1.dp,
            if (isSelected) color else SlateBorder,
            RoundedCornerShape(8.dp)
          )
          .clickable { onFilterSelect(filter) }
          .padding(horizontal = 12.dp, vertical = 6.dp)
          .testTag("status_chip_${filter.lowercase()}"),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = filter,
          style = MaterialTheme.typography.labelSmall,
          color = if (isSelected) TextPrimaryDark else TextMutedDark,
          fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
          fontSize = 11.sp
        )
      }
    }
  }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun AutomationTaskLogCard(
  log: AutomationTaskLog,
  isSelected: Boolean,
  isSelectionMode: Boolean,
  onToggleSelect: () -> Unit,
  onLongClick: () -> Unit,
  onDelete: () -> Unit
) {
  var isExpanded by remember { mutableStateOf(false) }
  val statusColor = getStatusColor(log.status)

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("automation_task_log_card_${log.id}")
      .clip(RoundedCornerShape(12.dp))
      .combinedClickable(
        onClick = {
          if (isSelectionMode) {
            onToggleSelect()
          } else {
            isExpanded = !isExpanded
          }
        },
        onLongClick = onLongClick
      ),
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(
      containerColor = if (isSelected) SlateElevated else SlateCard
    ),
    border = androidx.compose.foundation.BorderStroke(
      width = if (isSelected) 1.5.dp else 1.dp,
      color = if (isSelected) TitanCyan else SlateBorder
    )
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      // Top row: Checkbox (if in selection mode) + Status pill + Task Name + Duration + Delete
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          modifier = Modifier.weight(1f, fill = false)
        ) {
          if (isSelectionMode) {
            Checkbox(
              checked = isSelected,
              onCheckedChange = { onToggleSelect() },
              colors = CheckboxDefaults.colors(
                checkedColor = TitanCyan,
                uncheckedColor = TextMutedDark,
                checkmarkColor = ObsidianDark
              ),
              modifier = Modifier
                .size(24.dp)
                .testTag("log_checkbox_${log.id}")
            )
          }

          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(statusColor.copy(alpha = 0.15f))
              .border(1.dp, statusColor.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
              .padding(horizontal = 8.dp, vertical = 3.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              val icon = when (log.status.uppercase()) {
                "SUCCESS" -> Icons.Default.CheckCircle
                "FAILED" -> Icons.Default.Error
                "RUNNING" -> Icons.Default.Sync
                else -> Icons.Default.Warning
              }
              Icon(icon, contentDescription = null, tint = statusColor, modifier = Modifier.size(11.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text(log.status, color = statusColor, fontWeight = FontWeight.Bold, fontSize = 10.sp)
            }
          }

          Column {
            Text(
              text = log.taskName,
              style = MaterialTheme.typography.titleSmall,
              color = TextPrimaryDark,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = "Trigger: ${log.triggerSource} • ${log.formattedTime}",
              style = MaterialTheme.typography.bodySmall,
              color = TextMutedDark,
              fontSize = 10.sp
            )
          }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          if (log.executionDurationMs > 0) {
            Text(
              text = "${log.executionDurationMs}ms",
              color = TitanCyan,
              fontSize = 11.sp,
              fontWeight = FontWeight.Medium
            )
          }
          IconButton(
            onClick = onDelete,
            modifier = Modifier
              .size(28.dp)
              .testTag("delete_task_log_${log.id}")
          ) {
            Icon(
              Icons.Default.Delete,
              contentDescription = "Delete Log",
              tint = TextMutedDark,
              modifier = Modifier.size(15.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Summary
      Text(
        text = log.summary,
        style = MaterialTheme.typography.bodyMedium,
        color = TextPrimaryDark
      )

      // Metrics Pills Row
      Spacer(modifier = Modifier.height(8.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        if (log.itemsDiscoveredCount > 0) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(SlateElevated)
              .padding(horizontal = 8.dp, vertical = 3.dp)
          ) {
            Text(
              text = "Discovered: ${log.itemsDiscoveredCount}",
              color = TitanCyan,
              fontSize = 11.sp,
              fontWeight = FontWeight.Medium
            )
          }
        }
        if (log.highPriorityMatchesCount > 0) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(SlateElevated)
              .padding(horizontal = 8.dp, vertical = 3.dp)
          ) {
            Text(
              text = "High Priority: ${log.highPriorityMatchesCount}",
              color = TitanGold,
              fontSize = 11.sp,
              fontWeight = FontWeight.Medium
            )
          }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Expand / collapse details
        if (log.details.isNotBlank()) {
          Row(
            modifier = Modifier
              .clickable { isExpanded = !isExpanded }
              .padding(vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = if (isExpanded) "Hide details" else "Details",
              color = TitanCyan,
              fontSize = 11.sp
            )
            Icon(
              imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
              contentDescription = null,
              tint = TitanCyan,
              modifier = Modifier.size(14.dp)
            )
          }
        }
      }

      // Expanded Details
      if (isExpanded && log.details.isNotBlank()) {
        Spacer(modifier = Modifier.height(8.dp))
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(SlateElevated)
            .padding(8.dp)
        ) {
          Text(
            text = log.details,
            color = TextMutedDark,
            fontSize = 11.sp,
            lineHeight = 16.sp
          )
        }
      }
    }
  }
}

@Composable
private fun EmptyTaskLogsState(
  isFiltered: Boolean,
  onResetFilter: () -> Unit,
  onTriggerFirstRun: () -> Unit
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .padding(top = 40.dp)
      .testTag("empty_task_logs_state"),
    contentAlignment = Alignment.Center
  ) {
    Card(
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = SlateCard),
      border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder),
      modifier = Modifier.fillMaxWidth(0.9f)
    ) {
      Column(
        modifier = Modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
      ) {
        Icon(
          imageVector = Icons.Default.History,
          contentDescription = null,
          tint = TitanCyan.copy(alpha = 0.6f),
          modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
          text = if (isFiltered) "No Logs Match Filter" else "No Task Logs Yet",
          style = MaterialTheme.typography.titleMedium,
          color = TextPrimaryDark,
          fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
          text = if (isFiltered)
            "Try clearing your search query or switching to 'ALL' status."
          else
            "Background crawler cycles and agent telemetry will record here automatically in Room.",
          style = MaterialTheme.typography.bodySmall,
          color = TextMutedDark,
          textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        if (isFiltered) {
          OutlinedButton(
            onClick = onResetFilter,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.testTag("reset_logs_filter_btn")
          ) {
            Text("Clear Filter", color = TitanCyan)
          }
        } else {
          Button(
            onClick = onTriggerFirstRun,
            colors = ButtonDefaults.buttonColors(containerColor = TitanCyan),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.testTag("run_first_task_btn")
          ) {
            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = ObsidianDark, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Run Automation Task", color = ObsidianDark, fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}

@Composable
private fun RunAutomationTaskDialog(
  tasks: List<AutomationTask>,
  onDismiss: () -> Unit,
  onRunTask: (AutomationTask) -> Unit
) {
  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(16.dp),
      color = SlateCard,
      border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder),
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
        .testTag("run_automation_task_dialog")
    ) {
      Column(modifier = Modifier.padding(20.dp)) {
        Text(
          text = "Trigger Automation Task",
          style = MaterialTheme.typography.titleLarge,
          color = TextPrimaryDark,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = "Execute background crawler on demand and audit output in Room",
          style = MaterialTheme.typography.bodySmall,
          color = TextMutedDark
        )

        Spacer(modifier = Modifier.height(14.dp))

        if (tasks.isEmpty()) {
          Text("No automation tasks registered.", color = TextMutedDark, fontSize = 12.sp)
        } else {
          Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            tasks.forEach { task ->
              Card(
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = SlateElevated),
                border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder),
                modifier = Modifier
                  .fillMaxWidth()
                  .clickable { onRunTask(task) }
                  .testTag("trigger_task_${task.id}")
              ) {
                Row(
                  modifier = Modifier.padding(12.dp),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Column(modifier = Modifier.weight(1f)) {
                    Text(
                      text = task.title,
                      color = TextPrimaryDark,
                      fontWeight = FontWeight.Bold,
                      fontSize = 13.sp
                    )
                    Text(
                      text = "${task.taskType} • ${task.frequency} • Priority ${task.priorityLevel}",
                      color = TextMutedDark,
                      fontSize = 11.sp
                    )
                  }
                  Icon(
                    Icons.Default.PlayArrow,
                    contentDescription = "Run",
                    tint = TitanCyan,
                    modifier = Modifier.size(20.dp)
                  )
                }
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
          TextButton(onClick = onDismiss) {
            Text("Close", color = TextMutedDark)
          }
        }
      }
    }
  }
}

private fun getStatusColor(status: String): Color {
  return when (status.uppercase()) {
    "SUCCESS" -> TitanEmerald
    "FAILED" -> TitanCrimson
    "RUNNING" -> TitanCyan
    "WARNING" -> TitanGold
    else -> TitanCyan
  }
}
