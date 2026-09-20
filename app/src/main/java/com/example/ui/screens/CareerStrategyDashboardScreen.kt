package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.CareerMilestoneStatus
import com.example.data.model.CareerStrategyRoadmap
import com.example.data.model.RoadmapPhase
import com.example.data.model.SkillMilestone
import com.example.service.FirestoreSyncState
import com.example.ui.components.CareerGoalCompletionChartCard
import com.example.ui.components.CareerGoalsJobAnalysisView
import com.example.ui.components.CareerMomentumDashboard
import com.example.ui.components.CareerRoadmapFeatureView
import com.example.ui.components.CareerSkillTimelineRechartsVisualizer
import com.example.ui.components.CareerStrategyNotesVaultCard
import com.example.ui.components.FirestoreCareerProgressionVisualizer
import com.example.ui.components.MarketCompensationHeatmapComponent
import com.example.ui.theme.ObsidianDark
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateCard
import com.example.ui.theme.SlateElevated
import com.example.ui.theme.TextMutedDark
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import com.example.ui.theme.TitanAmber
import com.example.ui.theme.TitanCyan
import com.example.ui.theme.TitanEmerald
import com.example.ui.theme.TitanGold
import com.example.ui.theme.TitanIndigo
import com.example.ui.viewmodel.TitanViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CareerStrategyDashboardScreen(
  viewModel: TitanViewModel,
  modifier: Modifier = Modifier
) {
  val roadmap by viewModel.careerRoadmap.collectAsState()
  val isGenerating by viewModel.isGeneratingCareerRoadmap.collectAsState()
  val isSavingToFirestore by viewModel.isSavingStrategyToFirestore.collectAsState()
  val firestoreSyncState by viewModel.firestoreStrategySyncState.collectAsState()
  val lastSyncedTimestamp by viewModel.firestoreStrategyLastSyncedTimestamp.collectAsState()
  val firestoreStatusMessage by viewModel.firestoreStrategyStatusMessage.collectAsState()

  val currentRole by viewModel.currentRoleInput.collectAsState()
  val targetGoals by viewModel.targetCareerGoalsInput.collectAsState()
  val targetHorizonYears by viewModel.targetHorizonYears.collectAsState()
  val strategyNotes by viewModel.careerStrategyNotes.collectAsState()
  val activeDashboardTab by viewModel.careerRoadmapActiveViewTab.collectAsState()
  val milestonesWithTasks by viewModel.milestonesWithTasks.collectAsState()
  val userProfile by viewModel.userProfile.collectAsState()

  var isAddMilestoneDialogOpen by remember { mutableStateOf(false) }
  var addMilestoneTargetPhase by remember { mutableIntStateOf(1) }
  var selectedPhaseFilter by remember { mutableIntStateOf(0) } // 0: All Phases, 1, 2, 3
  var isPillarsExpanded by remember { mutableStateOf(false) }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(ObsidianDark)
      .padding(horizontal = 16.dp)
      .testTag("career_strategy_dashboard_screen"),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    item { Spacer(modifier = Modifier.height(6.dp)) }

    // 1. Executive Terminal Header & Firebase Firestore Synchronization Status
    item {
      ExecutiveHeaderWithFirestoreStatus(
        syncState = firestoreSyncState,
        statusMessage = firestoreStatusMessage,
        lastSyncedTimestamp = lastSyncedTimestamp,
        isSaving = isSavingToFirestore,
        onSaveToFirestore = { viewModel.saveCareerStrategyToFirestore(silent = false) },
        onRestoreFromFirestore = { viewModel.loadCareerStrategyFromFirestore(silent = false) }
      )
    }

    // 1B. Resume Health Score & Industry Standards Scanner Banner
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("resume_health_banner_card"),
        colors = CardDefaults.cardColors(containerColor = SlateCard),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, TitanEmerald.copy(alpha = 0.4f))
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
          ) {
            Box(
              modifier = Modifier
                .size(38.dp)
                .background(TitanEmerald.copy(alpha = 0.15f), RoundedCornerShape(10.dp)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.HealthAndSafety,
                contentDescription = null,
                tint = TitanEmerald,
                modifier = Modifier.size(20.dp)
              )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = "RESUME HEALTH UTILITY",
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  color = TitanEmerald,
                  letterSpacing = 1.sp
                )
              }
              Text(
                text = "Resume Health Score & Quality Audit",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimaryDark
              )
              Text(
                text = "Scan uploaded resume text against 2026 industry standards with actionable fixes",
                fontSize = 11.sp,
                color = TextMutedDark,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
            }
          }

          Spacer(modifier = Modifier.width(10.dp))

          Button(
            onClick = { viewModel.openResumeHealthScanner() },
            colors = ButtonDefaults.buttonColors(containerColor = TitanEmerald),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.testTag("open_resume_health_scanner_button")
          ) {
            Text(
              text = "Audit Health",
              color = ObsidianDark,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }
    }

    // Top Level View Switcher Tab: AI Career Roadmap (Gaps & Phases) vs. Strategy Vault & Milestones
    item {
      ScrollableTabRow(
        selectedTabIndex = activeDashboardTab,
        containerColor = SlateCard,
        contentColor = TitanCyan,
        edgePadding = 8.dp,
        indicator = { tabPositions ->
          if (activeDashboardTab < tabPositions.size) {
            TabRowDefaults.SecondaryIndicator(
              modifier = Modifier.tabIndicatorOffset(tabPositions[activeDashboardTab]),
              color = if (activeDashboardTab == 3) TitanGold else TitanCyan,
              height = 3.dp
            )
          }
        },
        divider = { Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(SlateBorder)) },
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(12.dp))
          .border(1.dp, SlateBorder, RoundedCornerShape(12.dp))
          .testTag("career_roadmap_main_tabs")
      ) {
        Tab(
          selected = activeDashboardTab == 0,
          onClick = { viewModel.setCareerRoadmapActiveViewTab(0) },
          text = {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
              Text(
                "Gemini Career Roadmap & Gaps",
                fontWeight = if (activeDashboardTab == 0) FontWeight.Bold else FontWeight.Normal,
                fontSize = 13.sp
              )
            }
          },
          selectedContentColor = TitanCyan,
          unselectedContentColor = TextMutedDark
        )
        Tab(
          selected = activeDashboardTab == 1,
          onClick = { viewModel.setCareerRoadmapActiveViewTab(1) },
          text = {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Icon(Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(16.dp))
              Text(
                "Strategy Vault & Milestones",
                fontWeight = if (activeDashboardTab == 1) FontWeight.Bold else FontWeight.Normal,
                fontSize = 13.sp
              )
            }
          },
          selectedContentColor = TitanCyan,
          unselectedContentColor = TextMutedDark
        )
        Tab(
          selected = activeDashboardTab == 2,
          onClick = { viewModel.setCareerRoadmapActiveViewTab(2) },
          text = {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Icon(Icons.AutoMirrored.Filled.ShowChart, contentDescription = null, modifier = Modifier.size(16.dp))
              Text(
                "D3 & Goals Visualizer",
                fontWeight = if (activeDashboardTab == 2) FontWeight.Bold else FontWeight.Normal,
                fontSize = 13.sp
              )
            }
          },
          selectedContentColor = TitanCyan,
          unselectedContentColor = TextMutedDark
        )
        Tab(
          selected = activeDashboardTab == 3,
          onClick = { viewModel.setCareerRoadmapActiveViewTab(3) },
          text = {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Icon(Icons.Default.MonetizationOn, contentDescription = null, modifier = Modifier.size(16.dp))
              Text(
                "Compensation Heatmap",
                fontWeight = if (activeDashboardTab == 3) FontWeight.Bold else FontWeight.Normal,
                fontSize = 13.sp
              )
            }
          },
          selectedContentColor = TitanGold,
          unselectedContentColor = TextMutedDark
        )
        Tab(
          selected = activeDashboardTab == 4,
          onClick = { viewModel.setCareerRoadmapActiveViewTab(4) },
          text = {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Icon(Icons.AutoMirrored.Filled.TrendingUp, contentDescription = null, modifier = Modifier.size(16.dp))
              Text(
                "Career Momentum",
                fontWeight = if (activeDashboardTab == 4) FontWeight.Bold else FontWeight.Normal,
                fontSize = 13.sp
              )
            }
          },
          selectedContentColor = TitanCyan,
          unselectedContentColor = TextMutedDark
        )
      }
    }

    if (activeDashboardTab == 4) {
      // Recharts Career Momentum Dashboard
      item {
        CareerMomentumDashboard(
          viewModel = viewModel,
          modifier = Modifier.fillMaxWidth(),
          onAddMilestoneClick = { isAddMilestoneDialogOpen = true }
        )
      }
    } else if (activeDashboardTab == 0) {
      // Feature View: Gemini API Saved Companies & Applications Pipeline Analysis
      item {
        CareerRoadmapFeatureView(viewModel = viewModel)
      }
    } else if (activeDashboardTab == 3) {
      // 2026 Market Compensation & Equity Heatmap Component
      item {
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
            .testTag("open_salary_negotiation_banner"),
          colors = CardDefaults.cardColors(containerColor = SlateCard),
          shape = RoundedCornerShape(12.dp),
          border = BorderStroke(1.dp, TitanEmerald.copy(alpha = 0.4f))
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
                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = TitanEmerald, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "ACTIONABLE SALARY NEGOTIATION SCRIPT OS",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = TitanEmerald
                )
              }
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = "Turn market percentile benchmarks into tailored counter-offer emails and live recruiter phone scripts.",
                fontSize = 11.sp,
                color = TextSecondaryDark
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Button(
              onClick = { viewModel.openSalaryNegotiationDialog() },
              colors = ButtonDefaults.buttonColors(containerColor = TitanEmerald),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.testTag("open_salary_negotiation_dialog_button")
            ) {
              Text("Open Script Lab", fontSize = 11.sp, color = ObsidianDark, fontWeight = FontWeight.Bold)
            }
          }
        }
      }
      item {
        MarketCompensationHeatmapComponent(
          modifier = Modifier.fillMaxWidth()
        )
      }
    } else if (activeDashboardTab == 2) {
      // Recharts Skill Acquisition & Growth Timeline
      item {
        CareerSkillTimelineRechartsVisualizer(
          milestonesWithTasks = milestonesWithTasks,
          roadmap = roadmap,
          userProfile = userProfile,
          onAddMilestoneClick = { isAddMilestoneDialogOpen = true },
          onToggleSkillMilestone = { milestoneId ->
            viewModel.toggleSkillMilestoneStatus(milestoneId)
          },
          modifier = Modifier.fillMaxWidth()
        )
      }

      // D3 & Recharts Career Progression and Strategic Goals Visualizer (Firestore Powered)
      item {
        FirestoreCareerProgressionVisualizer(
          viewModel = viewModel,
          modifier = Modifier.fillMaxWidth()
        )
      }
    } else {
      // 2. Current Role & Target Career Goals Input Terminal Card
      item {
        CurrentRoleAndGoalsInputCard(
          currentRole = currentRole,
          targetGoals = targetGoals,
          targetHorizonYears = targetHorizonYears,
          isGenerating = isGenerating,
          onCurrentRoleChange = { viewModel.updateCurrentRole(it) },
          onTargetGoalsChange = { viewModel.updateTargetCareerGoals(it) },
          onHorizonYearsChange = { viewModel.targetHorizonYears.value = it },
          onRecalibrate = {
            viewModel.generateCareerRoadmap(
              targetRole = targetGoals,
              horizonYears = targetHorizonYears
            )
          },
          onSaveToFirestore = { viewModel.saveCareerStrategyToFirestore(silent = false) }
        )
      }

      // 2B. Local User Career Strategy Notes Vault (Room Encrypted Local Persistence - Offline)
      item {
        CareerStrategyNotesVaultCard(
          notes = strategyNotes,
          onSaveNote = { title, content, cat, comp, role, tags ->
            viewModel.saveCareerStrategyNote(
              title = title,
              content = content,
              category = cat,
              targetCompany = comp,
              targetRole = role,
              tags = tags
            )
          },
          onUpdateNote = { note ->
            viewModel.updateCareerStrategyNote(note)
          },
          onDeleteNote = { noteId ->
            viewModel.deleteCareerStrategyNote(noteId)
          },
          onTogglePin = { noteId, isPinned ->
            viewModel.toggleCareerStrategyNotePin(noteId, isPinned)
          }
        )
      }

      // 3. Recharts Data Visualization: Skill Acquisition & Milestone Growth Timeline
      item {
        CareerSkillTimelineRechartsVisualizer(
          milestonesWithTasks = milestonesWithTasks,
          roadmap = roadmap,
          userProfile = userProfile,
          onAddMilestoneClick = { isAddMilestoneDialogOpen = true },
          onToggleSkillMilestone = { milestoneId ->
            viewModel.toggleSkillMilestoneStatus(milestoneId)
          },
          modifier = Modifier.fillMaxWidth()
        )
      }

      // 3B. Strategy Telemetry Scorecard
      item {
        roadmap?.let { rm ->
          StrategyTelemetryScorecard(
            roadmap = rm,
            syncState = firestoreSyncState
          )
        }
      }

      // 3B. Career Goal Completion Progress Bar & Interactive Roadmap Milestones Chart
      item {
        roadmap?.let { rm ->
          CareerGoalCompletionChartCard(
            roadmap = rm,
            currentRole = currentRole,
            targetGoals = targetGoals,
            syncState = firestoreSyncState,
            lastSyncedTimestamp = lastSyncedTimestamp,
            isSavingToFirestore = isSavingToFirestore,
            onQuickSync = { viewModel.saveCareerStrategyToFirestore(silent = false) },
            onToggleMilestoneStatus = { milestoneId ->
              viewModel.toggleSkillMilestoneStatus(milestoneId)
            },
            onUpdateMilestoneProgress = { milestoneId, progress ->
              viewModel.updateSkillMilestoneProgress(milestoneId, progress)
            }
          )
        }
      }

      // 4. Multi-Horizon Phase Selector & Milestone Timeline
      item {
        roadmap?.let { rm ->
          PhaseFilterTabs(
            phases = rm.phases,
            selectedPhase = selectedPhaseFilter,
            onSelectPhase = { selectedPhaseFilter = it },
            onAddMilestoneClick = { phaseNum ->
              addMilestoneTargetPhase = phaseNum
              isAddMilestoneDialogOpen = true
            }
          )
        }
      }

      // 5. Phased Roadmap Milestones
      roadmap?.let { rm ->
        val visiblePhases = if (selectedPhaseFilter == 0) {
          rm.phases
        } else {
          rm.phases.filter { it.phaseNumber == selectedPhaseFilter }
        }

        items(visiblePhases, key = { it.phaseId }) { phase ->
          PhaseMilestoneSection(
            phase = phase,
            onToggleMilestoneStatus = { milestoneId -> viewModel.toggleSkillMilestoneStatus(milestoneId) },
            onUpdateProgress = { milestoneId, progress -> viewModel.updateSkillMilestoneProgress(milestoneId, progress) },
            onAddMilestone = {
              addMilestoneTargetPhase = phase.phaseNumber
              isAddMilestoneDialogOpen = true
            }
          )
        }
      }

      // 6. Strategic Pillars & Tactical Playbook
      item {
        roadmap?.let { rm ->
          StrategicPillarsAndPlaybookCard(
            roadmap = rm,
            isExpanded = isPillarsExpanded,
            onToggleExpand = { isPillarsExpanded = !isPillarsExpanded }
          )
        }
      }
    }

    item { Spacer(modifier = Modifier.height(32.dp)) }
  }

  // Add Milestone Dialog
  if (isAddMilestoneDialogOpen) {
    AddMilestoneDialog(
      initialPhase = addMilestoneTargetPhase,
      onDismiss = { isAddMilestoneDialogOpen = false },
      onConfirm = { phaseNumber, title, category, proficiency, weeks, artifact, outcomes ->
        viewModel.addCustomSkillMilestone(
          phaseNumber = phaseNumber,
          title = title,
          category = category,
          targetProficiency = proficiency,
          estimatedWeeks = weeks,
          artifact = artifact,
          learningOutcomes = outcomes
        )
        isAddMilestoneDialogOpen = false
      }
    )
  }
}

// ---------------------------------------------------------------------------
// 1. Executive Header with Live Firebase Firestore Cloud Sync
// ---------------------------------------------------------------------------
@Composable
private fun ExecutiveHeaderWithFirestoreStatus(
  syncState: FirestoreSyncState,
  statusMessage: String,
  lastSyncedTimestamp: Long?,
  isSaving: Boolean,
  onSaveToFirestore: () -> Unit,
  onRestoreFromFirestore: () -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("career_strategy_header_card"),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = SlateCard),
    border = BorderStroke(1.dp, SlateBorder)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
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
              .size(40.dp)
              .clip(RoundedCornerShape(10.dp))
              .background(TitanCyan.copy(alpha = 0.15f))
              .border(1.dp, TitanCyan.copy(alpha = 0.5f), RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.TrendingUp,
              contentDescription = "Career Strategy",
              tint = TitanCyan,
              modifier = Modifier.size(24.dp)
            )
          }

          Column {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              Text(
                text = "CAREER STRATEGY OS",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black,
                color = TextPrimaryDark,
                letterSpacing = 1.sp
              )
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(4.dp))
                  .background(TitanIndigo.copy(alpha = 0.25f))
                  .padding(horizontal = 6.dp, vertical = 2.dp)
              ) {
                Text("STRATEGIC ENGINE", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TitanIndigo)
              }
            }
            Text(
              text = "Role Transition & Multi-Horizon Milestone Matrix",
              style = MaterialTheme.typography.bodySmall,
              color = TextSecondaryDark
            )
          }
        }

        // Action Buttons: Save to Cloud & Restore
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          IconButton(
            onClick = onRestoreFromFirestore,
            modifier = Modifier
              .size(36.dp)
              .clip(RoundedCornerShape(8.dp))
              .background(SlateElevated)
              .border(1.dp, SlateBorder, RoundedCornerShape(8.dp))
              .testTag("btn_restore_firestore_roadmap")
          ) {
            Icon(
              imageVector = Icons.Default.CloudDownload,
              contentDescription = "Restore from Firestore",
              tint = TitanCyan,
              modifier = Modifier.size(18.dp)
            )
          }

          Button(
            onClick = onSaveToFirestore,
            enabled = !isSaving,
            colors = ButtonDefaults.buttonColors(containerColor = TitanCyan),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
              .height(36.dp)
              .testTag("btn_save_firestore_roadmap")
          ) {
            if (isSaving) {
              CircularProgressIndicator(modifier = Modifier.size(14.dp), color = ObsidianDark, strokeWidth = 2.dp)
              Spacer(modifier = Modifier.width(6.dp))
              Text("Syncing...", color = ObsidianDark, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            } else {
              Icon(Icons.Default.CloudUpload, contentDescription = null, tint = ObsidianDark, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Save to Cloud", color = ObsidianDark, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
          }
        }
      }

      HorizontalDivider(color = SlateBorder.copy(alpha = 0.6f))

      // Cloud Synchronization Banner
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(8.dp))
          .background(SlateElevated)
          .border(
            1.dp,
            when (syncState) {
              FirestoreSyncState.SYNCED -> TitanEmerald.copy(alpha = 0.4f)
              FirestoreSyncState.SYNCING -> TitanAmber.copy(alpha = 0.4f)
              FirestoreSyncState.FAILED -> TitanAmber.copy(alpha = 0.4f)
              FirestoreSyncState.IDLE -> TitanCyan.copy(alpha = 0.4f)
            },
            RoundedCornerShape(8.dp)
          )
          .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Box(
            modifier = Modifier
              .size(8.dp)
              .clip(CircleShape)
              .background(
                when (syncState) {
                  FirestoreSyncState.SYNCED -> TitanEmerald
                  FirestoreSyncState.SYNCING -> TitanAmber
                  FirestoreSyncState.FAILED -> TitanAmber
                  FirestoreSyncState.IDLE -> TitanCyan
                }
              )
          )

          Column {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              Text(
                text = "Firebase Firestore Vault",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimaryDark
              )
              Text(
                text = "• users/{userId}/career_strategy",
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                color = TextMutedDark
              )
            }
            Text(
              text = statusMessage,
              fontSize = 10.sp,
              color = TextSecondaryDark
            )
          }
        }

        lastSyncedTimestamp?.let { ts ->
          val formattedTime = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault()).format(Date(ts))
          Text(
            text = "Saved $formattedTime",
            fontSize = 10.sp,
            color = TextMutedDark
          )
        }
      }
    }
  }
}

// ---------------------------------------------------------------------------
// 2. Current Role & Target Goals Input Terminal
// ---------------------------------------------------------------------------
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CurrentRoleAndGoalsInputCard(
  currentRole: String,
  targetGoals: String,
  targetHorizonYears: Int,
  isGenerating: Boolean,
  onCurrentRoleChange: (String) -> Unit,
  onTargetGoalsChange: (String) -> Unit,
  onHorizonYearsChange: (Int) -> Unit,
  onRecalibrate: () -> Unit,
  onSaveToFirestore: () -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("card_career_role_goals_input"),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = SlateCard),
    border = BorderStroke(1.dp, SlateBorder)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          Icon(Icons.Default.Psychology, contentDescription = null, tint = TitanCyan, modifier = Modifier.size(18.dp))
          Text(
            text = "STRATEGIC PROFILE & TARGET GOALS",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = TitanCyan,
            letterSpacing = 0.5.sp
          )
        }
        Text(
          text = "Firestore Backed",
          fontSize = 10.sp,
          color = TitanEmerald,
          fontWeight = FontWeight.SemiBold
        )
      }

      // Current Role Input Field
      Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
          text = "Current Role (Baseline)",
          fontSize = 11.sp,
          fontWeight = FontWeight.SemiBold,
          color = TextSecondaryDark
        )
        OutlinedTextField(
          value = currentRole,
          onValueChange = onCurrentRoleChange,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("input_current_role"),
          placeholder = { Text("e.g. Associate Strategy & Operations Analyst", color = TextMutedDark, fontSize = 12.sp) },
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = TitanCyan,
            unfocusedBorderColor = SlateBorder,
            focusedContainerColor = SlateElevated,
            unfocusedContainerColor = SlateElevated,
            focusedTextColor = TextPrimaryDark,
            unfocusedTextColor = TextPrimaryDark
          ),
          shape = RoundedCornerShape(10.dp),
          singleLine = true,
          textStyle = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
        )

        // Quick Suggestions for Current Role
        FlowRow(
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          listOf(
            "Strategy & Ops Analyst",
            "BizOps Associate",
            "Product Operations Lead",
            "Management Consultant"
          ).forEach { roleSuggestion ->
            RoleChip(
              text = roleSuggestion,
              isSelected = currentRole == roleSuggestion,
              onClick = { onCurrentRoleChange(roleSuggestion) }
            )
          }
        }
      }

      // Target Career Goals Input Field
      Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
          text = "Target Career Goals & Dream Role",
          fontSize = 11.sp,
          fontWeight = FontWeight.SemiBold,
          color = TextSecondaryDark
        )
        OutlinedTextField(
          value = targetGoals,
          onValueChange = onTargetGoalsChange,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("input_target_career_goals"),
          placeholder = { Text("e.g. Senior Strategy & Ops Lead / Chief of Staff at Tier-1 Tech (Google/Microsoft)", color = TextMutedDark, fontSize = 12.sp) },
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = TitanCyan,
            unfocusedBorderColor = SlateBorder,
            focusedContainerColor = SlateElevated,
            unfocusedContainerColor = SlateElevated,
            focusedTextColor = TextPrimaryDark,
            unfocusedTextColor = TextPrimaryDark
          ),
          shape = RoundedCornerShape(10.dp),
          maxLines = 2,
          textStyle = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
        )

        // Quick Suggestions for Target Goals
        FlowRow(
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          listOf(
            "Chief of Staff (₹35L+)",
            "Senior BizOps Lead",
            "Director of Global Ops",
            "VP of Operations"
          ).forEach { goalSuggestion ->
            RoleChip(
              text = goalSuggestion,
              isSelected = targetGoals.startsWith(goalSuggestion.take(12)),
              onClick = { onTargetGoalsChange(goalSuggestion) }
            )
          }
        }
      }

      // Target Horizon Slider
      Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text("Target Horizon Timeline", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextSecondaryDark)
          Text("$targetHorizonYears ${if (targetHorizonYears == 1) "Year" else "Years"}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TitanCyan)
        }

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          listOf(1, 2, 3, 5).forEach { years ->
            val isSelected = targetHorizonYears == years
            Box(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(8.dp))
                .background(if (isSelected) TitanCyan else SlateElevated)
                .border(1.dp, if (isSelected) TitanCyan else SlateBorder, RoundedCornerShape(8.dp))
                .clickable { onHorizonYearsChange(years) }
                .padding(vertical = 6.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = "$years ${if (years == 1) "Yr" else "Yrs"}",
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) ObsidianDark else TextPrimaryDark
              )
            }
          }
        }
      }

      // Recalibrate Strategy & Save Buttons
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Button(
          onClick = onRecalibrate,
          enabled = !isGenerating,
          colors = ButtonDefaults.buttonColors(containerColor = SlateElevated),
          border = BorderStroke(1.dp, TitanCyan.copy(alpha = 0.6f)),
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier
            .weight(1f)
            .height(44.dp)
            .testTag("btn_recalibrate_strategy")
        ) {
          if (isGenerating) {
            CircularProgressIndicator(modifier = Modifier.size(16.dp), color = TitanCyan, strokeWidth = 2.dp)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Synthesizing...", fontSize = 12.sp, color = TitanCyan, fontWeight = FontWeight.Bold)
          } else {
            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = TitanCyan, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Recalibrate Strategy", fontSize = 12.sp, color = TextPrimaryDark, fontWeight = FontWeight.Bold)
          }
        }

        Button(
          onClick = onSaveToFirestore,
          colors = ButtonDefaults.buttonColors(containerColor = TitanCyan),
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier
            .height(44.dp)
            .testTag("btn_quick_save_firestore")
        ) {
          Icon(Icons.Default.CloudUpload, contentDescription = null, tint = ObsidianDark, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Save Roadmaps", fontSize = 12.sp, color = ObsidianDark, fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}

@Composable
private fun RoleChip(
  text: String,
  isSelected: Boolean,
  onClick: () -> Unit
) {
  Box(
    modifier = Modifier
      .clip(RoundedCornerShape(6.dp))
      .background(if (isSelected) TitanCyan.copy(alpha = 0.2f) else SlateElevated)
      .border(1.dp, if (isSelected) TitanCyan else SlateBorder.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
      .clickable { onClick() }
      .padding(horizontal = 8.dp, vertical = 4.dp)
  ) {
    Text(
      text = text,
      fontSize = 10.sp,
      fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
      color = if (isSelected) TitanCyan else TextMutedDark
    )
  }
}

// ---------------------------------------------------------------------------
// 3. Strategy Telemetry Scorecard
// ---------------------------------------------------------------------------
@Composable
private fun StrategyTelemetryScorecard(
  roadmap: CareerStrategyRoadmap,
  syncState: FirestoreSyncState
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    // Metric 1: Overall Completion Rate
    ScorecardTile(
      modifier = Modifier.weight(1f),
      label = "ROADMAP PROGRESS",
      value = "${roadmap.overallProgressPercentage}%",
      subtext = "${roadmap.acquiredMilestonesCount}/${roadmap.totalMilestonesCount} Acquired",
      accentColor = TitanEmerald,
      icon = Icons.Default.CheckCircle
    )

    // Metric 2: Target Compensation
    ScorecardTile(
      modifier = Modifier.weight(1f),
      label = "TARGET CTC",
      value = roadmap.targetCompensation.take(10),
      subtext = roadmap.targetHorizon,
      accentColor = TitanGold,
      icon = Icons.Default.Payments
    )

    // Metric 3: Firestore Cloud Status
    ScorecardTile(
      modifier = Modifier.weight(1f),
      label = "FIRESTORE VAULT",
      value = if (syncState == FirestoreSyncState.SYNCED) "Synced" else "Active",
      subtext = "${roadmap.totalMilestonesCount} Milestones",
      accentColor = TitanCyan,
      icon = Icons.Default.CloudDone
    )
  }
}

@Composable
private fun ScorecardTile(
  modifier: Modifier = Modifier,
  label: String,
  value: String,
  subtext: String,
  accentColor: Color,
  icon: ImageVector
) {
  Card(
    modifier = modifier,
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = SlateCard),
    border = BorderStroke(1.dp, SlateBorder)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp),
      verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(text = label, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextMutedDark, letterSpacing = 0.5.sp)
        Icon(imageVector = icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(14.dp))
      }
      Text(
        text = value,
        fontSize = 16.sp,
        fontWeight = FontWeight.Black,
        color = TextPrimaryDark
      )
      Text(
        text = subtext,
        fontSize = 10.sp,
        color = accentColor,
        fontWeight = FontWeight.Medium
      )
    }
  }
}

// ---------------------------------------------------------------------------
// 4. Phase Filter Tabs & Add Milestone Action
// ---------------------------------------------------------------------------
@Composable
private fun PhaseFilterTabs(
  phases: List<RoadmapPhase>,
  selectedPhase: Int,
  onSelectPhase: (Int) -> Unit,
  onAddMilestoneClick: (Int) -> Unit
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Row(
      modifier = Modifier
        .weight(1f)
        .horizontalScroll(rememberScrollState()),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      PhaseFilterChip(
        label = "All Phases (${phases.sumOf { it.skillMilestones.size }})",
        isSelected = selectedPhase == 0,
        onClick = { onSelectPhase(0) }
      )

      phases.forEach { phase ->
        PhaseFilterChip(
          label = "Phase ${phase.phaseNumber} (${phase.skillMilestones.size})",
          isSelected = selectedPhase == phase.phaseNumber,
          onClick = { onSelectPhase(phase.phaseNumber) }
        )
      }
    }

    Button(
      onClick = { onAddMilestoneClick(if (selectedPhase > 0) selectedPhase else 1) },
      colors = ButtonDefaults.buttonColors(containerColor = SlateElevated),
      border = BorderStroke(1.dp, TitanCyan.copy(alpha = 0.5f)),
      shape = RoundedCornerShape(8.dp),
      modifier = Modifier
        .height(34.dp)
        .padding(start = 8.dp)
        .testTag("btn_add_milestone_global")
    ) {
      Icon(Icons.Default.Add, contentDescription = null, tint = TitanCyan, modifier = Modifier.size(14.dp))
      Spacer(modifier = Modifier.width(4.dp))
      Text("Add Milestone", fontSize = 11.sp, color = TextPrimaryDark, fontWeight = FontWeight.SemiBold)
    }
  }
}

@Composable
private fun PhaseFilterChip(
  label: String,
  isSelected: Boolean,
  onClick: () -> Unit
) {
  Box(
    modifier = Modifier
      .clip(RoundedCornerShape(8.dp))
      .background(if (isSelected) TitanCyan.copy(alpha = 0.2f) else SlateCard)
      .border(1.dp, if (isSelected) TitanCyan else SlateBorder, RoundedCornerShape(8.dp))
      .clickable { onClick() }
      .padding(horizontal = 12.dp, vertical = 8.dp)
  ) {
    Text(
      text = label,
      fontSize = 11.sp,
      fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
      color = if (isSelected) TitanCyan else TextSecondaryDark
    )
  }
}

// ---------------------------------------------------------------------------
// 5. Phase Milestone Section
// ---------------------------------------------------------------------------
@Composable
private fun PhaseMilestoneSection(
  phase: RoadmapPhase,
  onToggleMilestoneStatus: (String) -> Unit,
  onUpdateProgress: (String, Int) -> Unit,
  onAddMilestone: () -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("phase_card_${phase.phaseNumber}"),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = SlateCard),
    border = BorderStroke(1.dp, SlateBorder)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      // Phase Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(TitanCyan.copy(alpha = 0.2f))
                .border(1.dp, TitanCyan, RoundedCornerShape(6.dp))
                .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
              Text("PHASE ${phase.phaseNumber}", fontSize = 10.sp, fontWeight = FontWeight.Black, color = TitanCyan)
            }
            Text(
              text = phase.timeframe,
              fontSize = 11.sp,
              color = TextMutedDark,
              fontFamily = FontFamily.Monospace
            )
          }

          Spacer(modifier = Modifier.height(4.dp))

          Text(
            text = phase.title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = TextPrimaryDark
          )

          Text(
            text = "Target Role Tier: ${phase.targetRoleTier} • ${phase.targetCompLakhs}",
            fontSize = 11.sp,
            color = TitanGold,
            fontWeight = FontWeight.SemiBold
          )
        }

        // Phase Progress indicator
        Column(horizontalAlignment = Alignment.End) {
          Text(
            text = "${phase.completionPercentage}%",
            fontSize = 16.sp,
            fontWeight = FontWeight.Black,
            color = if (phase.completionPercentage >= 100) TitanEmerald else TitanCyan
          )
          Text(
            text = "${phase.skillMilestones.count { it.status == CareerMilestoneStatus.ACQUIRED }}/${phase.skillMilestones.size} Done",
            fontSize = 10.sp,
            color = TextMutedDark
          )
        }
      }

      LinearProgressIndicator(
        progress = { (phase.completionPercentage / 100f).coerceIn(0f, 1f) },
        modifier = Modifier
          .fillMaxWidth()
          .height(6.dp)
          .clip(RoundedCornerShape(3.dp)),
        color = if (phase.completionPercentage >= 100) TitanEmerald else TitanCyan,
        trackColor = SlateElevated
      )

      // Focus Theme & Deliverables chips
      if (phase.focusTheme.isNotBlank()) {
        Text(
          text = "Focus: ${phase.focusTheme}",
          fontSize = 11.sp,
          color = TextSecondaryDark,
          style = MaterialTheme.typography.bodySmall
        )
      }

      HorizontalDivider(color = SlateBorder.copy(alpha = 0.6f))

      // Milestones List
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        phase.skillMilestones.forEach { milestone ->
          MilestoneCard(
            milestone = milestone,
            onToggleStatus = { onToggleMilestoneStatus(milestone.id) },
            onUpdateProgress = { progress -> onUpdateProgress(milestone.id, progress) }
          )
        }
      }

      // Add Milestone Button for this Phase
      OutlinedButton(
        onClick = onAddMilestone,
        modifier = Modifier
          .fillMaxWidth()
          .height(38.dp)
          .testTag("btn_add_milestone_phase_${phase.phaseNumber}"),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, SlateBorder)
      ) {
        Icon(Icons.Default.Add, contentDescription = null, tint = TitanCyan, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "Add Milestone to Phase ${phase.phaseNumber}",
          fontSize = 11.sp,
          color = TextSecondaryDark,
          fontWeight = FontWeight.SemiBold
        )
      }
    }
  }
}

// ---------------------------------------------------------------------------
// Milestone Card with Firestore Sync Indicator & Progress Controls
// ---------------------------------------------------------------------------
@Composable
private fun MilestoneCard(
  milestone: SkillMilestone,
  onToggleStatus: () -> Unit,
  onUpdateProgress: (Int) -> Unit
) {
  var isExpanded by remember { mutableStateOf(false) }

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("milestone_card_${milestone.id}"),
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(
      containerColor = if (milestone.status == CareerMilestoneStatus.ACQUIRED) SlateElevated.copy(alpha = 0.5f) else SlateElevated
    ),
    border = BorderStroke(
      1.dp,
      when (milestone.status) {
        CareerMilestoneStatus.ACQUIRED -> TitanEmerald.copy(alpha = 0.4f)
        CareerMilestoneStatus.IN_PROGRESS -> TitanCyan.copy(alpha = 0.4f)
        CareerMilestoneStatus.NOT_STARTED -> SlateBorder
      }
    )
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      // Top Row: Category tag, Firestore sync badge, Status Toggle
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
              .background(TitanIndigo.copy(alpha = 0.2f))
              .padding(horizontal = 6.dp, vertical = 2.dp)
          ) {
            Text(milestone.category, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TitanIndigo)
          }

          // Firestore Saved Badge
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp)
          ) {
            Icon(Icons.Default.CloudDone, contentDescription = "Synced to Firestore", tint = TitanCyan, modifier = Modifier.size(12.dp))
            Text("Firestore", fontSize = 9.sp, color = TitanCyan)
          }
        }

        // Status Pill Button
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(
              when (milestone.status) {
                CareerMilestoneStatus.ACQUIRED -> TitanEmerald.copy(alpha = 0.2f)
                CareerMilestoneStatus.IN_PROGRESS -> TitanCyan.copy(alpha = 0.2f)
                CareerMilestoneStatus.NOT_STARTED -> SlateCard
              }
            )
            .border(
              1.dp,
              when (milestone.status) {
                CareerMilestoneStatus.ACQUIRED -> TitanEmerald
                CareerMilestoneStatus.IN_PROGRESS -> TitanCyan
                CareerMilestoneStatus.NOT_STARTED -> SlateBorder
              },
              RoundedCornerShape(12.dp)
            )
            .clickable { onToggleStatus() }
            .padding(horizontal = 8.dp, vertical = 3.dp)
            .testTag("status_pill_${milestone.id}")
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            Icon(
              imageVector = when (milestone.status) {
                CareerMilestoneStatus.ACQUIRED -> Icons.Default.CheckCircle
                CareerMilestoneStatus.IN_PROGRESS -> Icons.Default.HourglassTop
                CareerMilestoneStatus.NOT_STARTED -> Icons.Default.RadioButtonUnchecked
              },
              contentDescription = null,
              tint = when (milestone.status) {
                CareerMilestoneStatus.ACQUIRED -> TitanEmerald
                CareerMilestoneStatus.IN_PROGRESS -> TitanCyan
                CareerMilestoneStatus.NOT_STARTED -> TextMutedDark
              },
              modifier = Modifier.size(12.dp)
            )
            Text(
              text = milestone.status.label,
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              color = when (milestone.status) {
                CareerMilestoneStatus.ACQUIRED -> TitanEmerald
                CareerMilestoneStatus.IN_PROGRESS -> TitanCyan
                CareerMilestoneStatus.NOT_STARTED -> TextSecondaryDark
              }
            )
          }
        }
      }

      // Milestone Title & Target Proficiency
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = milestone.title,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = TextPrimaryDark
          )

          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(top = 2.dp)
          ) {
            Text(
              text = "Target: ${milestone.targetProficiency}",
              fontSize = 10.sp,
              color = TitanGold,
              fontWeight = FontWeight.Medium
            )
            Text(
              text = "• ${milestone.estimatedWeeks} Weeks",
              fontSize = 10.sp,
              color = TextMutedDark
            )
          }
        }

        IconButton(
          onClick = { isExpanded = !isExpanded },
          modifier = Modifier.size(28.dp)
        ) {
          Icon(
            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
            contentDescription = "Expand details",
            tint = TextMutedDark,
            modifier = Modifier.size(18.dp)
          )
        }
      }

      // Progress Controls (Slider + Jump Buttons)
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        LinearProgressIndicator(
          progress = { (milestone.progress / 100f).coerceIn(0f, 1f) },
          modifier = Modifier
            .weight(1f)
            .height(6.dp)
            .clip(RoundedCornerShape(3.dp)),
          color = when (milestone.status) {
            CareerMilestoneStatus.ACQUIRED -> TitanEmerald
            CareerMilestoneStatus.IN_PROGRESS -> TitanCyan
            CareerMilestoneStatus.NOT_STARTED -> TextMutedDark
          },
          trackColor = SlateCard
        )

        Text(
          text = "${milestone.progress}%",
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          color = TextPrimaryDark,
          modifier = Modifier.width(36.dp),
          textAlign = TextAlign.End
        )

        // Quick Jump Buttons
        QuickProgressButton(text = "+25%", onClick = { onUpdateProgress((milestone.progress + 25).coerceAtMost(100)) })
        QuickProgressButton(text = "100%", onClick = { onUpdateProgress(100) })
      }

      // Expandable Details (Learning outcomes & Proof of Work Artifact)
      AnimatedVisibility(visible = isExpanded) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 6.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          HorizontalDivider(color = SlateBorder.copy(alpha = 0.5f))

          if (milestone.keyLearningOutcomes.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
              Text(
                text = "Key Learning Outcomes & Mastery Criteria:",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondaryDark
              )
              milestone.keyLearningOutcomes.forEach { outcome ->
                Row(
                  verticalAlignment = Alignment.Top,
                  horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                  Text("•", fontSize = 10.sp, color = TitanCyan)
                  Text(outcome, fontSize = 10.sp, color = TextPrimaryDark)
                }
              }
            }
          }

          if (milestone.proofOfWorkArtifact.isNotBlank()) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
              Text("Proof of Work Deliverable:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextSecondaryDark)
              Text(
                text = milestone.proofOfWorkArtifact,
                fontSize = 10.sp,
                color = TitanEmerald,
                fontFamily = FontFamily.Monospace
              )
            }
          }

          if (milestone.verificationMethod.isNotBlank()) {
            Text(
              text = "Verification: ${milestone.verificationMethod}",
              fontSize = 9.sp,
              color = TextMutedDark
            )
          }
        }
      }
    }
  }
}

@Composable
private fun QuickProgressButton(
  text: String,
  onClick: () -> Unit
) {
  Box(
    modifier = Modifier
      .clip(RoundedCornerShape(4.dp))
      .background(SlateCard)
      .border(1.dp, SlateBorder, RoundedCornerShape(4.dp))
      .clickable { onClick() }
      .padding(horizontal = 6.dp, vertical = 2.dp)
  ) {
    Text(text, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TitanCyan)
  }
}

// ---------------------------------------------------------------------------
// 6. Strategic Pillars & Tactical Playbook Card
// ---------------------------------------------------------------------------
@Composable
private fun StrategicPillarsAndPlaybookCard(
  roadmap: CareerStrategyRoadmap,
  isExpanded: Boolean,
  onToggleExpand: () -> Unit
) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = SlateCard),
    border = BorderStroke(1.dp, SlateBorder)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          Icon(Icons.Default.Layers, contentDescription = null, tint = TitanGold, modifier = Modifier.size(20.dp))
          Text(
            text = "STRATEGIC PILLARS & TACTICAL DIRECTIVES",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = TitanGold,
            letterSpacing = 0.5.sp
          )
        }

        IconButton(onClick = onToggleExpand, modifier = Modifier.size(28.dp)) {
          Icon(
            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
            contentDescription = "Toggle Pillars",
            tint = TextMutedDark
          )
        }
      }

      if (roadmap.executiveSummary.isNotBlank()) {
        Text(
          text = roadmap.executiveSummary,
          style = MaterialTheme.typography.bodySmall,
          color = TextSecondaryDark,
          lineHeight = 16.sp
        )
      }

      AnimatedVisibility(visible = isExpanded) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
          HorizontalDivider(color = SlateBorder.copy(alpha = 0.5f))

          // Strategic Pillars breakdown
          roadmap.strategicPillars.forEach { pillar ->
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text(pillar.name, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                Text("${pillar.weightPercentage}% Weight • ${pillar.currentScore}/100", fontSize = 10.sp, color = TitanGold)
              }
              LinearProgressIndicator(
                progress = { (pillar.currentScore / 100f).coerceIn(0f, 1f) },
                modifier = Modifier
                  .fillMaxWidth()
                  .height(4.dp)
                  .clip(RoundedCornerShape(2.dp)),
                color = TitanGold,
                trackColor = SlateElevated
              )
              Text(pillar.description, fontSize = 10.sp, color = TextMutedDark)
            }
          }

          // Tactical Playbook
          if (roadmap.tacticalPlaybook.isNotEmpty()) {
            HorizontalDivider(color = SlateBorder.copy(alpha = 0.5f))
            Text("Tactical Execution Directives:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TitanCyan)
            roadmap.tacticalPlaybook.forEach { rule ->
              Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("›", color = TitanCyan, fontWeight = FontWeight.Bold)
                Text(rule, fontSize = 10.sp, color = TextPrimaryDark)
              }
            }
          }
        }
      }
    }
  }
}

// ---------------------------------------------------------------------------
// 7. Add Custom Milestone Dialog
// ---------------------------------------------------------------------------
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AddMilestoneDialog(
  initialPhase: Int,
  onDismiss: () -> Unit,
  onConfirm: (
    phaseNumber: Int,
    title: String,
    category: String,
    proficiency: String,
    estimatedWeeks: Int,
    artifact: String,
    outcomes: List<String>
  ) -> Unit
) {
  var phaseNumber by remember { mutableIntStateOf(initialPhase) }
  var title by remember { mutableStateOf("") }
  var category by remember { mutableStateOf("AI & Autonomous Ops") }
  var proficiency by remember { mutableStateOf("Production Ready") }
  var estimatedWeeks by remember { mutableIntStateOf(4) }
  var artifact by remember { mutableStateOf("") }
  var outcomeInput by remember { mutableStateOf("") }

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(16.dp),
      color = SlateCard,
      border = BorderStroke(1.dp, SlateBorder),
      modifier = Modifier
        .fillMaxWidth()
        .testTag("dialog_add_milestone")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(Icons.Default.Flag, contentDescription = null, tint = TitanCyan, modifier = Modifier.size(20.dp))
            Text(
              text = "Add Strategy Milestone",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = TextPrimaryDark
            )
          }
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(4.dp))
              .background(TitanEmerald.copy(alpha = 0.2f))
              .padding(horizontal = 6.dp, vertical = 2.dp)
          ) {
            Text("Saves to Firestore", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TitanEmerald)
          }
        }

        HorizontalDivider(color = SlateBorder)

        // Phase Selector
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
          Text("Target Phase", fontSize = 10.sp, color = TextMutedDark, fontWeight = FontWeight.SemiBold)
          Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            listOf(1, 2, 3).forEach { pNum ->
              val isSelected = phaseNumber == pNum
              Box(
                modifier = Modifier
                  .weight(1f)
                  .clip(RoundedCornerShape(6.dp))
                  .background(if (isSelected) TitanCyan else SlateElevated)
                  .border(1.dp, if (isSelected) TitanCyan else SlateBorder, RoundedCornerShape(6.dp))
                  .clickable { phaseNumber = pNum }
                  .padding(vertical = 6.dp),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  "Phase $pNum",
                  fontSize = 11.sp,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                  color = if (isSelected) ObsidianDark else TextPrimaryDark
                )
              }
            }
          }
        }

        // Title Input
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
          Text("Milestone Title", fontSize = 10.sp, color = TextMutedDark, fontWeight = FontWeight.SemiBold)
          OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            modifier = Modifier
              .fillMaxWidth()
              .testTag("input_milestone_title"),
            placeholder = { Text("e.g. Architect Enterprise Multi-Agent Knowledge Graph", fontSize = 11.sp, color = TextMutedDark) },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = TitanCyan,
              unfocusedBorderColor = SlateBorder,
              focusedContainerColor = SlateElevated,
              unfocusedContainerColor = SlateElevated,
              focusedTextColor = TextPrimaryDark,
              unfocusedTextColor = TextPrimaryDark
            ),
            shape = RoundedCornerShape(8.dp)
          )
        }

        // Category Selector
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
          Text("Competency Category", fontSize = 10.sp, color = TextMutedDark, fontWeight = FontWeight.SemiBold)
          FlowRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            listOf(
              "AI & Autonomous Ops",
              "Strategy & Casebooks",
              "Financial Modeling & SQL",
              "Executive Leadership",
              "Network & Dealflow"
            ).forEach { cat ->
              RoleChip(
                text = cat,
                isSelected = category == cat,
                onClick = { category = cat }
              )
            }
          }
        }

        // Target Proficiency & Estimated Weeks
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("Target Proficiency", fontSize = 10.sp, color = TextMutedDark, fontWeight = FontWeight.SemiBold)
            OutlinedTextField(
              value = proficiency,
              onValueChange = { proficiency = it },
              modifier = Modifier.fillMaxWidth(),
              singleLine = true,
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = TitanCyan,
                unfocusedBorderColor = SlateBorder,
                focusedContainerColor = SlateElevated,
                unfocusedContainerColor = SlateElevated,
                focusedTextColor = TextPrimaryDark,
                unfocusedTextColor = TextPrimaryDark
              ),
              shape = RoundedCornerShape(8.dp)
            )
          }

          Column(modifier = Modifier.weight(0.6f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("Weeks", fontSize = 10.sp, color = TextMutedDark, fontWeight = FontWeight.SemiBold)
            OutlinedTextField(
              value = estimatedWeeks.toString(),
              onValueChange = { estimatedWeeks = it.toIntOrNull() ?: estimatedWeeks },
              modifier = Modifier.fillMaxWidth(),
              singleLine = true,
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = TitanCyan,
                unfocusedBorderColor = SlateBorder,
                focusedContainerColor = SlateElevated,
                unfocusedContainerColor = SlateElevated,
                focusedTextColor = TextPrimaryDark,
                unfocusedTextColor = TextPrimaryDark
              ),
              shape = RoundedCornerShape(8.dp)
            )
          }
        }

        // Proof of Work Artifact
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
          Text("Proof-of-Work Artifact / Deliverable", fontSize = 10.sp, color = TextMutedDark, fontWeight = FontWeight.SemiBold)
          OutlinedTextField(
            value = artifact,
            onValueChange = { artifact = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("e.g. Published Case Study or GitHub Repo", fontSize = 11.sp, color = TextMutedDark) },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = TitanCyan,
              unfocusedBorderColor = SlateBorder,
              focusedContainerColor = SlateElevated,
              unfocusedContainerColor = SlateElevated,
              focusedTextColor = TextPrimaryDark,
              unfocusedTextColor = TextPrimaryDark
            ),
            shape = RoundedCornerShape(8.dp)
          )
        }

        // Buttons
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.End,
          verticalAlignment = Alignment.CenterVertically
        ) {
          TextButton(onClick = onDismiss) {
            Text("Cancel", color = TextMutedDark, fontSize = 12.sp)
          }
          Spacer(modifier = Modifier.width(8.dp))
          Button(
            onClick = {
              if (title.isNotBlank()) {
                val outcomes = if (outcomeInput.isNotBlank()) {
                  outcomeInput.split("\n").filter { it.isNotBlank() }
                } else {
                  listOf("Execute verified proof of work deliverable", "Demonstrate production proficiency in peer teardown")
                }
                onConfirm(phaseNumber, title, category, proficiency, estimatedWeeks, artifact, outcomes)
              }
            },
            enabled = title.isNotBlank(),
            colors = ButtonDefaults.buttonColors(containerColor = TitanCyan),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.testTag("btn_confirm_add_milestone")
          ) {
            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = ObsidianDark, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Save Milestone to Cloud", color = ObsidianDark, fontSize = 11.sp, fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}
