package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CareerMilestone
import com.example.data.model.MilestoneTask
import com.example.data.model.MilestoneWithTasks
import com.example.ui.components.AddMilestoneDialog
import com.example.ui.components.AddMilestoneTaskDialog
import com.example.ui.components.AiMilestoneAuditDialog
import com.example.ui.components.RechartsMilestoneProgressChart
import com.example.ui.theme.ObsidianDark
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateCard
import com.example.ui.theme.SlateElevated
import com.example.ui.theme.TextMutedDark
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import com.example.ui.theme.TitanCyan
import com.example.ui.theme.TitanEmerald
import com.example.ui.theme.TitanGold
import com.example.ui.theme.TitanIndigo
import com.example.ui.theme.TitanCrimson
import com.example.ui.viewmodel.TitanViewModel

@Composable
fun GoalSettingScreen(
  viewModel: TitanViewModel,
  modifier: Modifier = Modifier
) {
  val milestonesWithTasks by viewModel.milestonesWithTasks.collectAsState()
  val aiAuditState by viewModel.aiMilestoneAdviceState.collectAsState()

  var selectedHorizonFilter by remember { mutableStateOf("ALL") }
  var selectedCategoryFilter by remember { mutableStateOf("ALL") }

  var showAddMilestoneDialog by remember { mutableStateOf(false) }
  var activeMilestoneForTask by remember { mutableStateOf<CareerMilestone?>(null) }
  var showAiAuditDialog by remember { mutableStateOf(false) }

  // Overall statistics
  val totalMilestones = milestonesWithTasks.size
  val totalTasks = milestonesWithTasks.sumOf { it.totalTasksCount }
  val completedTasks = milestonesWithTasks.sumOf { it.completedTasksCount }
  val overallProgressPercent = if (totalTasks > 0) ((completedTasks.toFloat() / totalTasks) * 100).toInt() else 0

  val filteredMilestones = remember(milestonesWithTasks, selectedHorizonFilter, selectedCategoryFilter) {
    milestonesWithTasks.filter { mWithT ->
      val matchesHorizon = when (selectedHorizonFilter) {
        "ALL" -> true
        else -> mWithT.milestone.horizon == selectedHorizonFilter
      }
      val matchesCategory = when (selectedCategoryFilter) {
        "ALL" -> true
        else -> mWithT.milestone.category == selectedCategoryFilter
      }
      matchesHorizon && matchesCategory
    }
  }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(ObsidianDark)
      .testTag("goal_setting_screen"),
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // 1. Header & Quick Actions
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(28.dp)
                .background(TitanCyan.copy(alpha = 0.2f), RoundedCornerShape(8.dp)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Flag,
                contentDescription = null,
                tint = TitanCyan,
                modifier = Modifier.size(16.dp)
              )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "LONG-TERM GOAL SETTING",
              style = MaterialTheme.typography.labelSmall,
              color = TitanCyan,
              fontWeight = FontWeight.Bold,
              letterSpacing = 1.sp
            )
          }
          Text(
            text = "Career Milestones & Velocity",
            style = MaterialTheme.typography.headlineSmall,
            color = TextPrimaryDark,
            fontWeight = FontWeight.ExtraBold
          )
          Text(
            text = "Define multi-year horizons • Track task execution pacing via Recharts",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondaryDark,
            fontSize = 12.sp
          )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          // AI Pacing Audit Button
          Button(
            onClick = {
              viewModel.runAiMilestonePacingAudit()
              showAiAuditDialog = true
            },
            colors = ButtonDefaults.buttonColors(containerColor = SlateElevated),
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp),
            modifier = Modifier.testTag("open_ai_pacing_audit_button")
          ) {
            Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = TitanGold, modifier = Modifier.size(15.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = "AI Audit", color = TitanGold, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
          }

          // Add Milestone Button
          Button(
            onClick = { showAddMilestoneDialog = true },
            colors = ButtonDefaults.buttonColors(containerColor = TitanCyan),
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
            modifier = Modifier.testTag("open_add_milestone_button")
          ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = ObsidianDark, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = "New Milestone", color = ObsidianDark, fontSize = 12.sp, fontWeight = FontWeight.Bold)
          }
        }
      }
    }

    // 2. Executive KPI Velocity Strip
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        // KPI 1: Active Horizons
        Card(
          modifier = Modifier.weight(1f),
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = SlateCard),
          border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
        ) {
          Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(imageVector = Icons.Default.Layers, contentDescription = null, tint = TitanCyan, modifier = Modifier.size(14.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text(text = "MILESTONES", style = MaterialTheme.typography.labelSmall, color = TextMutedDark, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "$totalMilestones Horizons", style = MaterialTheme.typography.titleMedium, color = TextPrimaryDark, fontWeight = FontWeight.Bold)
            Text(text = "6M to 5Y roadmap", style = MaterialTheme.typography.labelSmall, color = TitanCyan, fontSize = 10.sp)
          }
        }

        // KPI 2: Overall Progress
        Card(
          modifier = Modifier.weight(1f),
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = SlateCard),
          border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
        ) {
          Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(imageVector = Icons.Default.Speed, contentDescription = null, tint = TitanEmerald, modifier = Modifier.size(14.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text(text = "TASK VELOCITY", style = MaterialTheme.typography.labelSmall, color = TextMutedDark, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "$overallProgressPercent% Complete", style = MaterialTheme.typography.titleMedium, color = TitanEmerald, fontWeight = FontWeight.Bold)
            Text(text = "$completedTasks of $totalTasks tasks done", style = MaterialTheme.typography.labelSmall, color = TextSecondaryDark, fontSize = 10.sp)
          }
        }

        // KPI 3: Peak Long-Term Target
        Card(
          modifier = Modifier.weight(1.2f),
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = SlateCard),
          border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
        ) {
          Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(imageVector = Icons.Default.Stars, contentDescription = null, tint = TitanGold, modifier = Modifier.size(14.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text(text = "5-YEAR TARGET", style = MaterialTheme.typography.labelSmall, color = TitanGold, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "Co-Founder / GM",
              style = MaterialTheme.typography.titleMedium,
              color = TextPrimaryDark,
              fontWeight = FontWeight.Bold,
              maxLines = 1
            )
            Text(text = "₹2.5Cr+ Net Worth Equity", style = MaterialTheme.typography.labelSmall, color = TextSecondaryDark, fontSize = 10.sp, maxLines = 1)
          }
        }
      }
    }

    // 3. Recharts Milestone Progress Chart
    item {
      RechartsMilestoneProgressChart(
        milestonesWithTasks = milestonesWithTasks,
        onMilestoneSelected = { selectedId ->
          val target = milestonesWithTasks.find { it.milestone.id == selectedId }
          if (target != null) {
            selectedHorizonFilter = target.milestone.horizon
          }
        }
      )
    }

    // 4. Horizon & Category Filter Strip
    item {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        // Horizon Filter Pills
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "HORIZON TIMELINE",
            style = MaterialTheme.typography.labelSmall,
            color = TextMutedDark,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp
          )
          if (selectedHorizonFilter != "ALL" || selectedCategoryFilter != "ALL") {
            Text(
              text = "Reset Filters",
              style = MaterialTheme.typography.labelSmall,
              color = TitanCyan,
              modifier = Modifier
                .clickable {
                  selectedHorizonFilter = "ALL"
                  selectedCategoryFilter = "ALL"
                }
                .testTag("reset_filters_text"),
              fontSize = 10.sp
            )
          }
        }

        LazyRow(
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          val horizons = listOf("ALL", "6M", "1Y", "2Y", "3Y", "5Y")
          items(horizons) { h ->
            val isSelected = selectedHorizonFilter == h
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(if (isSelected) TitanCyan.copy(alpha = 0.25f) else SlateCard)
                .border(1.dp, if (isSelected) TitanCyan else SlateBorder, RoundedCornerShape(8.dp))
                .clickable { selectedHorizonFilter = h }
                .padding(horizontal = 12.dp, vertical = 6.dp)
                .testTag("filter_horizon_$h"),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = if (h == "ALL") "All Horizons" else "$h Horizon",
                style = MaterialTheme.typography.labelSmall,
                color = if (isSelected) TitanCyan else TextSecondaryDark,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
              )
            }
          }
        }
      }
    }

    // 5. Milestones List Header
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "ACTIVE CAREER MILESTONES (${filteredMilestones.size})",
          style = MaterialTheme.typography.labelSmall,
          color = TextMutedDark,
          fontWeight = FontWeight.Bold,
          fontSize = 10.sp
        )

        Text(
          text = "Click checkbox to mark task completion",
          style = MaterialTheme.typography.labelSmall,
          color = TitanCyan,
          fontSize = 10.sp
        )
      }
    }

    // 6. Milestone Cards with Associated Tasks
    items(filteredMilestones, key = { it.milestone.id }) { milestoneWithTasks ->
      MilestoneCard(
        milestoneWithTasks = milestoneWithTasks,
        onToggleTask = { task -> viewModel.toggleMilestoneTask(task) },
        onAddTask = { activeMilestoneForTask = milestoneWithTasks.milestone },
        onAiSuggestTasks = { viewModel.aiSuggestTasksForMilestone(milestoneWithTasks.milestone) },
        onDeleteTask = { taskId -> viewModel.deleteMilestoneTask(taskId) },
        onDeleteMilestone = { viewModel.deleteMilestone(milestoneWithTasks.milestone.id) }
      )
    }

    // Empty state if filters yielded 0
    if (filteredMilestones.isEmpty()) {
      item {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
          contentAlignment = Alignment.Center
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(imageVector = Icons.Default.Flag, contentDescription = null, tint = TextMutedDark, modifier = Modifier.size(36.dp))
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = "No milestones match the current filter", style = MaterialTheme.typography.bodyMedium, color = TextSecondaryDark)
            Spacer(modifier = Modifier.height(6.dp))
            Button(
              onClick = {
                selectedHorizonFilter = "ALL"
                selectedCategoryFilter = "ALL"
              },
              colors = ButtonDefaults.buttonColors(containerColor = SlateElevated),
              shape = RoundedCornerShape(8.dp)
            ) {
              Text(text = "Show All Milestones", color = TitanCyan)
            }
          }
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(40.dp))
    }
  }

  // Dialogs
  if (showAddMilestoneDialog) {
    AddMilestoneDialog(
      onDismiss = { showAddMilestoneDialog = false },
      onConfirm = { title, horizon, horizonYears, quarter, cat, metric, rationale, priority ->
        viewModel.addMilestone(title, horizon, horizonYears, quarter, cat, metric, rationale, priority)
        showAddMilestoneDialog = false
      }
    )
  }

  activeMilestoneForTask?.let { milestone ->
    AddMilestoneTaskDialog(
      milestoneTitle = milestone.title,
      milestoneId = milestone.id,
      onDismiss = { activeMilestoneForTask = null },
      onConfirm = { mId, taskTitle, desc, cat, weight, dueDate ->
        viewModel.addMilestoneTask(mId, taskTitle, desc, cat, weight, dueDate)
        activeMilestoneForTask = null
      }
    )
  }

  if (showAiAuditDialog) {
    AiMilestoneAuditDialog(
      state = aiAuditState,
      onDismiss = {
        showAiAuditDialog = false
        viewModel.dismissAiMilestoneAudit()
      }
    )
  }
}

@Composable
fun MilestoneCard(
  milestoneWithTasks: MilestoneWithTasks,
  onToggleTask: (MilestoneTask) -> Unit,
  onAddTask: () -> Unit,
  onAiSuggestTasks: () -> Unit,
  onDeleteTask: (String) -> Unit,
  onDeleteMilestone: () -> Unit,
  modifier: Modifier = Modifier
) {
  val milestone = milestoneWithTasks.milestone
  val tasks = milestoneWithTasks.tasks
  val progress = milestoneWithTasks.progressPercent
  val animatedProgress by animateFloatAsState(
    targetValue = progress / 100f,
    animationSpec = tween(600),
    label = "progress_bar_anim"
  )

  var isTasksExpanded by remember { mutableStateOf(true) }

  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("milestone_card_${milestone.id}"),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = SlateCard),
    border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
    ) {
      // 1. Top Horizon & Category & Priority Badges
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Horizon badge
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(TitanCyan.copy(alpha = 0.2f))
              .padding(horizontal = 8.dp, vertical = 3.dp)
          ) {
            Text(
              text = "${milestone.horizon} HORIZON",
              style = MaterialTheme.typography.labelSmall,
              color = TitanCyan,
              fontWeight = FontWeight.Bold,
              fontSize = 10.sp
            )
          }

          // Target Quarter
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(SlateElevated)
              .padding(horizontal = 8.dp, vertical = 3.dp)
          ) {
            Text(
              text = milestone.targetQuarter,
              style = MaterialTheme.typography.labelSmall,
              color = TextSecondaryDark,
              fontSize = 10.sp
            )
          }

          // Category
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(SlateElevated)
              .padding(horizontal = 8.dp, vertical = 3.dp)
          ) {
            Text(
              text = milestone.category.replace("_", " "),
              style = MaterialTheme.typography.labelSmall,
              color = TitanGold,
              fontSize = 10.sp,
              fontWeight = FontWeight.Medium
            )
          }
        }

        // Priority Pill
        val priorityColor = when (milestone.priority) {
          "CRITICAL" -> TitanCrimson
          "HIGH" -> TitanGold
          else -> TitanIndigo
        }
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(priorityColor.copy(alpha = 0.15f))
            .padding(horizontal = 8.dp, vertical = 3.dp)
        ) {
          Text(
            text = milestone.priority,
            style = MaterialTheme.typography.labelSmall,
            color = priorityColor,
            fontWeight = FontWeight.Bold,
            fontSize = 9.sp
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // 2. Milestone Title & Strategic Rationale
      Text(
        text = milestone.title,
        style = MaterialTheme.typography.titleMedium,
        color = TextPrimaryDark,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp
      )

      Spacer(modifier = Modifier.height(4.dp))

      Text(
        text = milestone.strategicRationale,
        style = MaterialTheme.typography.bodySmall,
        color = TextSecondaryDark,
        fontSize = 12.sp,
        lineHeight = 16.sp
      )

      Spacer(modifier = Modifier.height(8.dp))

      // 3. Target Metric Badge
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(8.dp))
          .background(ObsidianDark)
          .border(1.dp, SlateBorder.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
          .padding(horizontal = 10.dp, vertical = 7.dp)
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(imageVector = Icons.AutoMirrored.Filled.TrendingUp, contentDescription = null, tint = TitanGold, modifier = Modifier.size(14.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "TARGET METRIC: ${milestone.targetMetric}",
            style = MaterialTheme.typography.labelSmall,
            color = TitanGold,
            fontWeight = FontWeight.SemiBold,
            fontSize = 11.sp
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // 4. Progress Bar & Task Completion Metric
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = "$progress% PROGRESS",
            style = MaterialTheme.typography.labelSmall,
            color = if (progress >= 60) TitanEmerald else TitanCyan,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 11.sp
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "(${milestoneWithTasks.completedTasksCount}/${milestoneWithTasks.totalTasksCount} tasks • ${milestoneWithTasks.completedWeight}/${milestoneWithTasks.totalWeight} pts)",
            style = MaterialTheme.typography.labelSmall,
            color = TextMutedDark,
            fontSize = 10.sp
          )
        }

        Text(
          text = if (isTasksExpanded) "Collapse Tasks" else "View Tasks (${tasks.size})",
          style = MaterialTheme.typography.labelSmall,
          color = TitanCyan,
          modifier = Modifier.clickable { isTasksExpanded = !isTasksExpanded },
          fontSize = 10.sp
        )
      }

      Spacer(modifier = Modifier.height(6.dp))

      // Glowing Animated Progress Bar
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(8.dp)
          .clip(RoundedCornerShape(4.dp))
          .background(SlateElevated)
      ) {
        Box(
          modifier = Modifier
            .fillMaxWidth(fraction = animatedProgress)
            .height(8.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(
              Brush.horizontalGradient(
                colors = listOf(TitanCyan, TitanEmerald)
              )
            )
        )
      }

      // 5. Tasks List Section
      AnimatedVisibility(visible = isTasksExpanded) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          tasks.forEach { task ->
            TaskItemRow(
              task = task,
              onToggle = { onToggleTask(task) },
              onDelete = { onDeleteTask(task.id) }
            )
          }

          if (tasks.isEmpty()) {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(ObsidianDark)
                .padding(12.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = "No action tasks defined yet. Add tasks or use AI suggest below.",
                style = MaterialTheme.typography.bodySmall,
                color = TextMutedDark,
                fontSize = 11.sp
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // 6. Action Buttons Bar
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          // + Add Task
          OutlinedButton(
            onClick = onAddTask,
            colors = ButtonDefaults.outlinedButtonColors(contentColor = TitanCyan),
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
            modifier = Modifier.testTag("add_task_button_${milestone.id}")
          ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(13.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = "Add Task", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
          }

          // AI Suggest Tasks
          OutlinedButton(
            onClick = onAiSuggestTasks,
            colors = ButtonDefaults.outlinedButtonColors(contentColor = TitanGold),
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
            modifier = Modifier.testTag("ai_suggest_tasks_button_${milestone.id}")
          ) {
            Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(13.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = "AI Suggest", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
          }
        }

        // Delete Milestone
        IconButton(
          onClick = onDeleteMilestone,
          modifier = Modifier
            .size(32.dp)
            .testTag("delete_milestone_${milestone.id}")
        ) {
          Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete Milestone", tint = TextMutedDark, modifier = Modifier.size(16.dp))
        }
      }
    }
  }
}

@Composable
fun TaskItemRow(
  task: MilestoneTask,
  onToggle: () -> Unit,
  onDelete: () -> Unit,
  modifier: Modifier = Modifier
) {
  val checkColor by animateColorAsState(
    targetValue = if (task.isCompleted) TitanEmerald else SlateBorder,
    label = "check_color"
  )

  Box(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(10.dp))
      .background(if (task.isCompleted) SlateElevated.copy(alpha = 0.5f) else SlateElevated)
      .border(
        1.dp,
        if (task.isCompleted) TitanEmerald.copy(alpha = 0.3f) else SlateBorder.copy(alpha = 0.5f),
        RoundedCornerShape(10.dp)
      )
      .clickable { onToggle() }
      .padding(horizontal = 12.dp, vertical = 10.dp)
      .testTag("task_item_${task.id}")
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Interactive Custom Checkbox
      Box(
        modifier = Modifier
          .size(22.dp)
          .clip(RoundedCornerShape(6.dp))
          .background(if (task.isCompleted) TitanEmerald else Color.Transparent)
          .border(1.5.dp, checkColor, RoundedCornerShape(6.dp))
          .testTag("task_checkbox_${task.id}"),
        contentAlignment = Alignment.Center
      ) {
        if (task.isCompleted) {
          Icon(
            imageVector = Icons.Default.Check,
            contentDescription = "Completed",
            tint = ObsidianDark,
            modifier = Modifier.size(14.dp)
          )
        }
      }

      Spacer(modifier = Modifier.width(12.dp))

      // Task Title & Category Badges
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = task.title,
          style = MaterialTheme.typography.bodyMedium,
          color = if (task.isCompleted) TextMutedDark else TextPrimaryDark,
          textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
          fontWeight = if (task.isCompleted) FontWeight.Normal else FontWeight.Medium,
          fontSize = 13.sp
        )

        if (task.description.isNotBlank()) {
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = task.description,
            style = MaterialTheme.typography.bodySmall,
            color = TextMutedDark,
            fontSize = 11.sp,
            maxLines = 1
          )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Row(
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Category pill
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(4.dp))
              .background(TitanCyan.copy(alpha = 0.15f))
              .padding(horizontal = 5.dp, vertical = 2.dp)
          ) {
            Text(
              text = task.category,
              style = MaterialTheme.typography.labelSmall,
              color = TitanCyan,
              fontSize = 9.sp,
              fontWeight = FontWeight.Bold
            )
          }

          // Weight Points pill
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(4.dp))
              .background(TitanGold.copy(alpha = 0.15f))
              .padding(horizontal = 5.dp, vertical = 2.dp)
          ) {
            Text(
              text = "${task.weightPoints} PTS",
              style = MaterialTheme.typography.labelSmall,
              color = TitanGold,
              fontSize = 9.sp,
              fontWeight = FontWeight.Bold
            )
          }

          // Due Date pill
          if (task.dueDate.isNotBlank()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(imageVector = Icons.Default.Schedule, contentDescription = null, tint = TextMutedDark, modifier = Modifier.size(11.dp))
              Spacer(modifier = Modifier.width(3.dp))
              Text(text = task.dueDate, style = MaterialTheme.typography.labelSmall, color = TextMutedDark, fontSize = 9.sp)
            }
          }
        }
      }

      // Delete Task Button
      IconButton(
        onClick = onDelete,
        modifier = Modifier
          .size(24.dp)
          .testTag("delete_task_${task.id}")
      ) {
        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete Task", tint = TextMutedDark, modifier = Modifier.size(14.dp))
      }
    }
  }
}
