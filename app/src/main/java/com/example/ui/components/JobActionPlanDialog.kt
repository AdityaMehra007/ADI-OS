package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
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
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.ActionPlanSkill
import com.example.data.model.CandidateMatchStatus
import com.example.data.model.InterviewTalkingPoint
import com.example.data.model.InterviewTrapQuestion
import com.example.data.model.JobActionPlan
import com.example.data.model.SkillImportance
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

@Composable
fun JobActionPlanDialog(
  viewModel: TitanViewModel,
  onDismiss: () -> Unit
) {
  val context = LocalContext.current
  val currentPlan by viewModel.currentActionPlan.collectAsState()
  val isGenerating by viewModel.isGeneratingActionPlan.collectAsState()
  val errorMsg by viewModel.actionPlanError.collectAsState()
  val targetRole by viewModel.actionPlanTargetRole.collectAsState()
  val targetCompany by viewModel.actionPlanTargetCompany.collectAsState()
  val jdText by viewModel.actionPlanJobDescription.collectAsState()
  val sampleJds = viewModel.availableSampleJobDescriptions

  var selectedTab by remember { mutableStateOf(0) }
  val tabs = listOf(
    "Roadmap & Tasks",
    "Experience Rewrites (${currentPlan?.experienceBulletRewrites?.size ?: 0})",
    "Required Skills (${currentPlan?.requiredSkills?.size ?: 0})",
    "Interview Talking Points (${currentPlan?.interviewTalkingPoints?.size ?: 0})",
    "Probing & Trap Questions",
    "JD Source & Config"
  )

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(ObsidianDark.copy(alpha = 0.94f))
        .padding(8.dp),
      contentAlignment = Alignment.Center
    ) {
      Card(
        modifier = Modifier
          .fillMaxWidth(0.98f)
          .fillMaxHeight(0.96f)
          .testTag("job_action_plan_dialog"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = ObsidianDark),
        border = androidx.compose.foundation.BorderStroke(1.dp, TitanCyan.copy(alpha = 0.4f))
      ) {
        Column(modifier = Modifier.fillMaxSize()) {
          // 1. Top Header Bar
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .background(SlateCard)
              .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(36.dp)
                  .background(TitanCyan.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  Icons.Default.AutoAwesome,
                  contentDescription = "Gemini AI",
                  tint = TitanCyan,
                  modifier = Modifier.size(20.dp)
                )
              }
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(
                    text = "JD Action Plan Engine",
                    color = TextPrimaryDark,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                  )
                  Spacer(modifier = Modifier.width(8.dp))
                  Box(
                    modifier = Modifier
                      .background(TitanIndigo.copy(alpha = 0.25f), RoundedCornerShape(4.dp))
                      .padding(horizontal = 6.dp, vertical = 2.dp)
                  ) {
                    Text(
                      text = "Gemini 3.5 Flash",
                      color = TitanCyan,
                      fontSize = 10.sp,
                      fontWeight = FontWeight.SemiBold
                    )
                  }
                }
                Text(
                  text = "Analyzing JD • Strategic Skills Readiness • STAR Talking Points",
                  color = TextSecondaryDark,
                  fontSize = 11.sp
                )
              }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
              currentPlan?.let { plan ->
                IconButton(
                  onClick = {
                    copyActionPlanSummary(context, plan)
                  },
                  modifier = Modifier.testTag("copy_action_plan_button")
                ) {
                  Icon(
                    Icons.Default.ContentCopy,
                    contentDescription = "Copy Briefing",
                    tint = TitanCyan,
                    modifier = Modifier.size(20.dp)
                  )
                }

                IconButton(
                  onClick = {
                    viewModel.saveCurrentActionPlan()
                  },
                  modifier = Modifier.testTag("save_action_plan_button")
                ) {
                  Icon(
                    Icons.Default.Bookmark,
                    contentDescription = "Save Action Plan",
                    tint = if (plan.isSaved) TitanGold else TextSecondaryDark,
                    modifier = Modifier.size(20.dp)
                  )
                }
              }

              IconButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("close_action_plan_dialog")
              ) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondaryDark)
              }
            }
          }

          // 2. Role, Company & Sample Preset Strip
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .background(SlateElevated)
              .padding(horizontal = 14.dp, vertical = 8.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = "$targetRole @ $targetCompany",
                  color = TextPrimaryDark,
                  fontWeight = FontWeight.Bold,
                  fontSize = 14.sp
                )
                Text(
                  text = currentPlan?.seniorityLevel ?: "Target Strategic Role",
                  color = TitanCyan,
                  fontSize = 11.sp
                )
              }

              Button(
                onClick = { viewModel.generateActionPlan() },
                enabled = !isGenerating,
                colors = ButtonDefaults.buttonColors(containerColor = TitanCyan),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                  .height(36.dp)
                  .testTag("regenerate_action_plan_button")
              ) {
                if (isGenerating) {
                  CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    color = ObsidianDark,
                    strokeWidth = 2.dp
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text("Analyzing...", color = ObsidianDark, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                } else {
                  Icon(Icons.Default.Refresh, contentDescription = null, tint = ObsidianDark, modifier = Modifier.size(16.dp))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text("Re-Analyze JD", color = ObsidianDark, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
              }
            }

            // Quick Samples Chips
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp)
                .horizontalScroll(rememberScrollState()),
              horizontalArrangement = Arrangement.spacedBy(6.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "Preset JDs:",
                color = TextMutedDark,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium
              )
              sampleJds.forEach { sample ->
                val isSelected = targetCompany.equals(sample.company, ignoreCase = true)
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (isSelected) TitanIndigo else SlateCard)
                    .border(1.dp, if (isSelected) TitanCyan else SlateBorder, RoundedCornerShape(6.dp))
                    .clickable { viewModel.selectSampleJobDescription(sample) }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .testTag("preset_${sample.id}")
                ) {
                  Text(
                    text = "${sample.company} • ${sample.role.take(18)}...",
                    color = if (isSelected) TextPrimaryDark else TextSecondaryDark,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                  )
                }
              }
            }
          }

          // Error banner if any
          errorMsg?.let { err ->
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .background(TitanCrimson.copy(alpha = 0.2f))
                .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
              Text(
                text = "Notice: $err (Showing local high-fidelity model plan)",
                color = TitanCrimson,
                fontSize = 11.sp
              )
            }
          }

          // 3. Tab Navigation Strip
          ScrollableTabRow(
            selectedTabIndex = selectedTab,
            containerColor = SlateCard,
            contentColor = TitanCyan,
            edgePadding = 8.dp,
            indicator = { tabPositions ->
              if (selectedTab < tabPositions.size) {
                TabRowDefaults.SecondaryIndicator(
                  modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                  color = TitanCyan,
                  height = 3.dp
                )
              }
            }
          ) {
            tabs.forEachIndexed { index, title ->
              Tab(
                selected = selectedTab == index,
                onClick = { selectedTab = index },
                modifier = Modifier.testTag("action_plan_tab_$index"),
                text = {
                  Text(
                    text = title,
                    color = if (selectedTab == index) TitanCyan else TextSecondaryDark,
                    fontSize = 11.sp,
                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                  )
                }
              )
            }
          }

          // 4. Tab Content Body
          Box(
            modifier = Modifier
              .fillMaxSize()
              .padding(10.dp)
          ) {
            if (isGenerating && currentPlan == null) {
              Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
              ) {
                CircularProgressIndicator(color = TitanCyan, strokeWidth = 3.dp)
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                  text = "Gemini 3.5 Flash is dissecting target job description...",
                  color = TextPrimaryDark,
                  fontWeight = FontWeight.Medium,
                  fontSize = 13.sp
                )
                Text(
                  text = "Extracting technical requirements, gap readiness, and formulating STAR talking points",
                  color = TextSecondaryDark,
                  fontSize = 11.sp
                )
              }
            } else if (currentPlan != null) {
              val plan = currentPlan!!
              when (selectedTab) {
                0 -> RoadmapAndOverviewTab(plan = plan, viewModel = viewModel)
                1 -> ExperienceRewritesTab(plan = plan, viewModel = viewModel)
                2 -> RequiredSkillsTab(skills = plan.requiredSkills)
                3 -> InterviewTalkingPointsTab(talkingPoints = plan.interviewTalkingPoints)
                4 -> ProbingAndTrapQuestionsTab(questions = plan.questionsToAsk, traps = plan.trapQuestions)
                5 -> JdSourceAndConfigTab(
                  targetRole = targetRole,
                  targetCompany = targetCompany,
                  jdText = jdText,
                  viewModel = viewModel
                )
              }
            }
          }
        }
      }
    }
  }
}

@Composable
fun ExperienceRewritesTab(
  plan: JobActionPlan,
  viewModel: TitanViewModel
) {
  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(12.dp)
      .testTag("action_plan_experience_rewrites_tab"),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    item {
      ExperienceBulletRewritesSection(
        rewrites = plan.experienceBulletRewrites,
        targetRole = plan.targetRole,
        targetCompany = plan.targetCompany,
        onToggleApply = { bulletId ->
          viewModel.toggleExperienceBulletRewrite(bulletId)
        }
      )
    }
  }
}

@Composable
fun RoadmapAndOverviewTab(
  plan: JobActionPlan,
  viewModel: TitanViewModel
) {
  val haptic = LocalHapticFeedback.current
  LazyColumn(
    modifier = Modifier.fillMaxSize(),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    // Executive Match & Benchmark Card
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
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
              Text(
                text = "Target Role Fit & Benchmark",
                color = TextSecondaryDark,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
              )
              Text(
                text = plan.targetRole,
                color = TextPrimaryDark,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
              )
              Text(
                text = plan.targetCompany,
                color = TitanCyan,
                fontWeight = FontWeight.Medium,
                fontSize = 13.sp
              )
            }

            JobMatchScoreGauge(
              score = plan.matchFitScore,
              gaugeSize = 64.dp,
              strokeWidth = 6.dp,
              style = GaugeStyle.CIRCULAR_RING,
              showLabel = true,
              showTierBadge = false
            )
          }

          Spacer(modifier = Modifier.height(10.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Box(
              modifier = Modifier
                .weight(1f)
                .background(SlateElevated, RoundedCornerShape(8.dp))
                .padding(8.dp)
            ) {
              Column {
                Text("Benchmark Compensation", color = TextMutedDark, fontSize = 10.sp)
                Text(plan.compensationBenchmark, color = TitanGold, fontWeight = FontWeight.Bold, fontSize = 12.sp)
              }
            }

            Box(
              modifier = Modifier
                .weight(1f)
                .background(SlateElevated, RoundedCornerShape(8.dp))
                .padding(8.dp)
            ) {
              Column {
                Text("Strategic Level", color = TextMutedDark, fontSize = 10.sp)
                Text(plan.seniorityLevel, color = TitanCyan, fontWeight = FontWeight.Bold, fontSize = 12.sp)
              }
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          Text(
            text = "Role Mission & Core Mandate:",
            color = TextSecondaryDark,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold
          )
          Spacer(modifier = Modifier.height(3.dp))
          Text(
            text = plan.roleMissionSummary,
            color = TextPrimaryDark,
            fontSize = 12.sp,
            lineHeight = 16.sp
          )
        }
      }
    }

    // Strategic Priorities
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SlateCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Lightbulb, contentDescription = null, tint = TitanGold, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Key Strategic Priorities & Positioning",
              color = TextPrimaryDark,
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp
            )
          }
          Spacer(modifier = Modifier.height(8.dp))
          plan.strategicPriorities.forEach { priority ->
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 3.dp),
              verticalAlignment = Alignment.Top
            ) {
              Text("•", color = TitanCyan, fontWeight = FontWeight.Bold, modifier = Modifier.padding(end = 6.dp))
              Text(priority, color = TextSecondaryDark, fontSize = 12.sp, lineHeight = 16.sp)
            }
          }
        }
      }
    }

    // 3-Phase Execution Roadmap with Interactive Checkboxes
    item {
      Text(
        text = "3-Phase Action Roadmap & Execution Checklist",
        color = TextPrimaryDark,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        modifier = Modifier.padding(vertical = 4.dp)
      )
    }

    itemsIndexed(plan.roadmapPhases) { phaseIndex, phase ->
      val completedCount = phase.tasks.count { it.isCompleted }
      val totalCount = phase.tasks.size
      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SlateCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = phase.phaseName,
                color = TitanCyan,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
              )
              Text(
                text = "Timeline: ${phase.timeline}",
                color = TextSecondaryDark,
                fontSize = 11.sp
              )
            }

            Box(
              modifier = Modifier
                .background(if (completedCount == totalCount && totalCount > 0) TitanEmerald.copy(alpha = 0.2f) else SlateElevated, RoundedCornerShape(6.dp))
                .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
              Text(
                text = "$completedCount / $totalCount Done",
                color = if (completedCount == totalCount && totalCount > 0) TitanEmerald else TextSecondaryDark,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          phase.tasks.forEach { task ->
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(if (task.isCompleted) SlateElevated.copy(alpha = 0.5f) else SlateElevated)
                .clickable {
                  haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                  viewModel.toggleActionPlanTask(phaseIndex, task.id)
                }
                .padding(8.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Checkbox(
                checked = task.isCompleted,
                onCheckedChange = {
                  haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                  viewModel.toggleActionPlanTask(phaseIndex, task.id)
                },
                colors = CheckboxDefaults.colors(
                  checkedColor = TitanEmerald,
                  uncheckedColor = TextMutedDark,
                  checkmarkColor = ObsidianDark
                ),
                modifier = Modifier.size(24.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = task.title,
                  color = if (task.isCompleted) TextMutedDark else TextPrimaryDark,
                  fontWeight = FontWeight.SemiBold,
                  fontSize = 12.sp
                )
                Text(
                  text = task.detail,
                  color = if (task.isCompleted) TextMutedDark else TextSecondaryDark,
                  fontSize = 11.sp,
                  lineHeight = 15.sp
                )
              }
            }
          }
        }
      }
    }
  }
}

@Composable
fun RequiredSkillsTab(skills: List<ActionPlanSkill>) {
  var selectedCategoryFilter by remember { mutableStateOf("ALL") }
  val categories = listOf("ALL", "Technical / Analytics", "Strategic Operations", "Leadership & Presence")

  Column(modifier = Modifier.fillMaxSize()) {
    // Filter Strip
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .horizontalScroll(rememberScrollState())
        .padding(bottom = 8.dp),
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      categories.forEach { cat ->
        val isSelected = selectedCategoryFilter == cat
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(if (isSelected) TitanCyan else SlateElevated)
            .clickable { selectedCategoryFilter = cat }
            .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
          Text(
            text = cat,
            color = if (isSelected) ObsidianDark else TextSecondaryDark,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
          )
        }
      }
    }

    val filteredSkills = if (selectedCategoryFilter == "ALL") {
      skills
    } else {
      skills.filter { it.category.contains(selectedCategoryFilter, ignoreCase = true) }
    }

    LazyColumn(
      modifier = Modifier.fillMaxSize(),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      items(filteredSkills) { skill ->
        Card(
          modifier = Modifier.fillMaxWidth(),
          colors = CardDefaults.cardColors(containerColor = SlateCard),
          border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
        ) {
          Column(modifier = Modifier.padding(12.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = skill.name,
                color = TextPrimaryDark,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                modifier = Modifier.weight(1f)
              )

              Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                // Importance Badge
                val (impColor, impText) = when (skill.importance) {
                  SkillImportance.CRITICAL -> Pair(TitanCrimson, "CRITICAL")
                  SkillImportance.HIGH -> Pair(TitanGold, "HIGH")
                  SkillImportance.FOUNDATIONAL -> Pair(TitanCyan, "CORE")
                }
                Box(
                  modifier = Modifier
                    .background(impColor.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                  Text(text = impText, color = impColor, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }

                // Match Status Badge
                val (matchColor, matchText) = when (skill.candidateMatchStatus) {
                  CandidateMatchStatus.STRONG_MATCH -> Pair(TitanEmerald, "MATCH")
                  CandidateMatchStatus.PARTIAL_GAP -> Pair(TitanGold, "PARTIAL GAP")
                  CandidateMatchStatus.CRITICAL_GAP -> Pair(TitanCrimson, "GAP")
                }
                Box(
                  modifier = Modifier
                    .background(matchColor.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                  Text(text = matchText, color = matchColor, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
              }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
              text = "Category: ${skill.category}",
              color = TextMutedDark,
              fontSize = 10.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
              text = "Why JD Demands It:",
              color = TextSecondaryDark,
              fontSize = 11.sp,
              fontWeight = FontWeight.SemiBold
            )
            Text(
              text = skill.whyDemanded,
              color = TextSecondaryDark,
              fontSize = 11.sp,
              lineHeight = 15.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Box(
              modifier = Modifier
                .fillMaxWidth()
                .background(SlateElevated, RoundedCornerShape(6.dp))
                .padding(8.dp)
            ) {
              Row(verticalAlignment = Alignment.Top) {
                Icon(
                  Icons.Default.CheckCircle,
                  contentDescription = null,
                  tint = TitanCyan,
                  modifier = Modifier
                    .size(14.dp)
                    .padding(top = 2.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                  Text(
                    text = "Recommended Tactical Action:",
                    color = TitanCyan,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                  )
                  Text(
                    text = skill.upskillingAction,
                    color = TextPrimaryDark,
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                  )
                }
              }
            }
          }
        }
      }
    }
  }
}

@Composable
fun InterviewTalkingPointsTab(talkingPoints: List<InterviewTalkingPoint>) {
  LazyColumn(
    modifier = Modifier.fillMaxSize(),
    verticalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    items(talkingPoints) { point ->
      var isExpanded by remember { mutableStateOf(false) }

      Card(
        modifier = Modifier
          .fillMaxWidth()
          .clickable { isExpanded = !isExpanded },
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
              Text(
                text = point.theme,
                color = TitanCyan,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
              )
              Text(
                text = "JD Alignment: ${point.jdMandate}",
                color = TextSecondaryDark,
                fontSize = 11.sp
              )
            }

            IconButton(onClick = { isExpanded = !isExpanded }, modifier = Modifier.size(28.dp)) {
              Icon(
                imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = null,
                tint = TitanCyan
              )
            }
          }

          Spacer(modifier = Modifier.height(6.dp))

          // Suggested Executive Delivery
          Text(
            text = "Suggested Candidate Delivery:",
            color = TextSecondaryDark,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold
          )
          Text(
            text = point.executiveNarrative,
            color = TextPrimaryDark,
            fontSize = 12.sp,
            lineHeight = 16.sp
          )

          Spacer(modifier = Modifier.height(6.dp))

          // Verified Proof Point badge
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .background(TitanIndigo.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
              .padding(horizontal = 8.dp, vertical = 4.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Check, contentDescription = null, tint = TitanCyan, modifier = Modifier.size(14.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "Proof Metric: ${point.proofMetricOrFact}",
                color = TitanCyan,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium
              )
            }
          }

          // Expandable STAR Method Breakdown
          AnimatedVisibility(visible = isExpanded) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
                .background(SlateElevated, RoundedCornerShape(8.dp))
                .padding(10.dp),
              verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Text(
                text = "STAR Method Delivery Framework:",
                color = TitanGold,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
              )

              StarLine(letter = "S", label = "Situation", text = point.starBreakdown.situation, color = TitanCyan)
              StarLine(letter = "T", label = "Task", text = point.starBreakdown.task, color = TitanGold)
              StarLine(letter = "A", label = "Action", text = point.starBreakdown.action, color = TitanEmerald)
              StarLine(letter = "R", label = "Result", text = point.starBreakdown.result, color = TitanIndigo)
            }
          }
        }
      }
    }
  }
}

@Composable
fun StarLine(letter: String, label: String, text: String, color: Color) {
  Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
    Box(
      modifier = Modifier
        .size(20.dp)
        .background(color.copy(alpha = 0.25f), CircleShape),
      contentAlignment = Alignment.Center
    ) {
      Text(letter, color = color, fontSize = 10.sp, fontWeight = FontWeight.Bold)
    }
    Spacer(modifier = Modifier.width(8.dp))
    Column {
      Text("$label:", color = color, fontSize = 10.sp, fontWeight = FontWeight.Bold)
      Text(text, color = TextPrimaryDark, fontSize = 11.sp, lineHeight = 15.sp)
    }
  }
}

@Composable
fun ProbingAndTrapQuestionsTab(
  questions: List<com.example.data.model.InterviewQuestionToAsk>,
  traps: List<InterviewTrapQuestion>
) {
  LazyColumn(
    modifier = Modifier.fillMaxSize(),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    item {
      Text(
        text = "High-Impact Questions to Ask Your Interviewer",
        color = TextPrimaryDark,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp
      )
      Text(
        text = "Signals strategic executive thinking, ownership mindset, and operational depth",
        color = TextSecondaryDark,
        fontSize = 11.sp
      )
    }

    items(questions) { q ->
      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SlateCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Box(
            modifier = Modifier
              .background(TitanCyan.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
              .padding(horizontal = 6.dp, vertical = 2.dp)
          ) {
            Text(
              text = "Ask: ${q.targetInterviewer}",
              color = TitanCyan,
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold
            )
          }

          Spacer(modifier = Modifier.height(6.dp))

          Text(
            text = "\"${q.question}\"",
            color = TextPrimaryDark,
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp,
            lineHeight = 16.sp
          )

          Spacer(modifier = Modifier.height(4.dp))

          Text(
            text = "Strategic Signaling: ${q.strategicIntent}",
            color = TextSecondaryDark,
            fontSize = 11.sp
          )
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(6.dp))
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.Warning, contentDescription = null, tint = TitanCrimson, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "Trap Questions & Defensible Counter-Pivots",
          color = TitanCrimson,
          fontWeight = FontWeight.Bold,
          fontSize = 14.sp
        )
      }
      Text(
        text = "Prepared answers to dangerous questions that derail early-career candidates",
        color = TextSecondaryDark,
        fontSize = 11.sp
      )
    }

    items(traps) { trap ->
      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SlateCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, TitanCrimson.copy(alpha = 0.3f))
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Text(
            text = "Question: \"${trap.question}\"",
            color = TextPrimaryDark,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
          )

          Spacer(modifier = Modifier.height(4.dp))

          Text(
            text = "Why It's a Trap: ${trap.trapReason}",
            color = TitanCrimson,
            fontSize = 11.sp
          )

          Spacer(modifier = Modifier.height(6.dp))

          Box(
            modifier = Modifier
              .fillMaxWidth()
              .background(SlateElevated, RoundedCornerShape(6.dp))
              .padding(8.dp)
          ) {
            Column {
              Text(
                text = "Recommended Defensible Pivot:",
                color = TitanEmerald,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = trap.recommendedPivot,
                color = TextPrimaryDark,
                fontSize = 11.sp,
                lineHeight = 15.sp
              )
            }
          }
        }
      }
    }
  }
}

@Composable
fun JdSourceAndConfigTab(
  targetRole: String,
  targetCompany: String,
  jdText: String,
  viewModel: TitanViewModel
) {
  var roleInput by remember { mutableStateOf(targetRole) }
  var companyInput by remember { mutableStateOf(targetCompany) }
  var jdInput by remember { mutableStateOf(jdText) }

  LazyColumn(
    modifier = Modifier.fillMaxSize(),
    verticalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    item {
      Text(
        text = "Customize Target Role & Job Description",
        color = TextPrimaryDark,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp
      )
      Text(
        text = "Paste any company's job description to generate a tailored strategic action plan.",
        color = TextSecondaryDark,
        fontSize = 11.sp
      )
    }

    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        OutlinedTextField(
          value = roleInput,
          onValueChange = {
            roleInput = it
            viewModel.setActionPlanTargetRole(it)
          },
          label = { Text("Target Role Title", fontSize = 11.sp) },
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = TitanCyan,
            unfocusedBorderColor = SlateBorder,
            focusedTextColor = TextPrimaryDark,
            unfocusedTextColor = TextPrimaryDark
          ),
          modifier = Modifier
            .weight(1f)
            .testTag("action_plan_role_input")
        )

        OutlinedTextField(
          value = companyInput,
          onValueChange = {
            companyInput = it
            viewModel.setActionPlanTargetCompany(it)
          },
          label = { Text("Company Name", fontSize = 11.sp) },
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = TitanCyan,
            unfocusedBorderColor = SlateBorder,
            focusedTextColor = TextPrimaryDark,
            unfocusedTextColor = TextPrimaryDark
          ),
          modifier = Modifier
            .weight(1f)
            .testTag("action_plan_company_input")
        )
      }
    }

    item {
      OutlinedTextField(
        value = jdInput,
        onValueChange = {
          jdInput = it
          viewModel.setActionPlanJobDescription(it)
        },
        label = { Text("Full Job Description / Requirements Text", fontSize = 11.sp) },
        minLines = 8,
        maxLines = 16,
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = TitanCyan,
          unfocusedBorderColor = SlateBorder,
          focusedTextColor = TextPrimaryDark,
          unfocusedTextColor = TextPrimaryDark
        ),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("action_plan_jd_input")
      )
    }

    item {
      Button(
        onClick = {
          viewModel.setActionPlanTargetRole(roleInput)
          viewModel.setActionPlanTargetCompany(companyInput)
          viewModel.setActionPlanJobDescription(jdInput)
          viewModel.generateActionPlan()
        },
        colors = ButtonDefaults.buttonColors(containerColor = TitanCyan),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
          .fillMaxWidth()
          .height(44.dp)
          .testTag("run_action_plan_analysis_btn")
      ) {
        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = ObsidianDark)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "Execute Gemini Analysis on This Job Description",
          color = ObsidianDark,
          fontWeight = FontWeight.Bold,
          fontSize = 13.sp
        )
      }
    }
  }
}

private fun copyActionPlanSummary(context: Context, plan: JobActionPlan) {
  val summary = buildString {
    appendLine("=== GEMINI CAREER STRATEGY ACTION PLAN ===")
    appendLine("TARGET: ${plan.targetRole} @ ${plan.targetCompany}")
    appendLine("MATCH FIT: ${plan.matchFitScore}% | LEVEL: ${plan.seniorityLevel} | COMP: ${plan.compensationBenchmark}")
    appendLine()
    appendLine("ROLE MISSION:")
    appendLine(plan.roleMissionSummary)
    appendLine()
    appendLine("REQUIRED SKILLS MATRIX:")
    plan.requiredSkills.forEach { skill ->
      appendLine("• ${skill.name} [${skill.category}] - Importance: ${skill.importance}, Match: ${skill.candidateMatchStatus}")
      appendLine("  Action: ${skill.upskillingAction}")
    }
    appendLine()
    appendLine("INTERVIEW TALKING POINTS:")
    plan.interviewTalkingPoints.forEach { pt ->
      appendLine("• Theme: ${pt.theme}")
      appendLine("  Narrative: ${pt.executiveNarrative}")
      appendLine("  Proof: ${pt.proofMetricOrFact}")
    }
    appendLine()
    appendLine("QUESTIONS TO ASK INTERVIEWER:")
    plan.questionsToAsk.forEach { q ->
      appendLine("• [${q.targetInterviewer}] ${q.question} (Signaling: ${q.strategicIntent})")
    }
  }

  val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
  val clip = ClipData.newPlainText("Job Action Plan", summary)
  clipboard.setPrimaryClip(clip)
  Toast.makeText(context, "Action Plan briefing copied to clipboard!", Toast.LENGTH_SHORT).show()
}
