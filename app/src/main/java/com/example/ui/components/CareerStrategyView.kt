package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Pending
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ai.CareerStrategyAdviceResponse
import com.example.ai.CareerStrategyMode
import com.example.data.model.CareerMilestoneStatus
import com.example.data.model.CareerStrategyRoadmap
import com.example.data.model.RoadmapPhase
import com.example.data.model.SkillMilestone
import com.example.data.model.StrategicPillar
import com.example.service.FirestoreSyncState
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
import com.example.ui.viewmodel.TitanViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CareerStrategyView(
  viewModel: TitanViewModel,
  modifier: Modifier = Modifier
) {
  val roadmap by viewModel.careerRoadmap.collectAsState()
  val isGenerating by viewModel.isGeneratingCareerRoadmap.collectAsState()
  val isGeneratorOpen by viewModel.isRoadmapGeneratorModalOpen.collectAsState()
  val selectedMilestone by viewModel.selectedMilestone.collectAsState()

  var isAddMilestoneDialogOpen by remember { mutableStateOf(false) }
  var addMilestoneTargetPhase by remember { mutableIntStateOf(1) }

  // Track expanded phases
  var expandedPhases by remember { mutableStateOf(setOf(1, 2, 3)) }

  if (isGeneratorOpen) {
    RoadmapGeneratorDialog(
      currentRole = viewModel.targetRoleInput.collectAsState().value,
      currentHorizon = viewModel.targetHorizonYears.collectAsState().value,
      isGenerating = isGenerating,
      onDismiss = { viewModel.setRoadmapGeneratorModalOpen(false) },
      onGenerate = { role, horizon, directives ->
        viewModel.generateCareerRoadmap(role, horizon, directives)
        viewModel.setRoadmapGeneratorModalOpen(false)
      }
    )
  }

  if (isAddMilestoneDialogOpen) {
    AddCustomMilestoneDialog(
      phaseNumber = addMilestoneTargetPhase,
      onDismiss = { isAddMilestoneDialogOpen = false },
      onAdd = { title, category, proficiency, weeks, artifact ->
        viewModel.addCustomSkillMilestone(
          phaseNumber = addMilestoneTargetPhase,
          title = title,
          category = category,
          targetProficiency = proficiency,
          estimatedWeeks = weeks,
          artifact = artifact
        )
        isAddMilestoneDialogOpen = false
      }
    )
  }

  selectedMilestone?.let { milestone ->
    MilestoneDetailDialog(
      milestone = milestone,
      onDismiss = { viewModel.selectMilestone(null) },
      onToggleStatus = {
        viewModel.toggleSkillMilestoneStatus(milestone.id)
        viewModel.selectMilestone(null)
      },
      onUpdateProgress = { newProgress ->
        viewModel.updateSkillMilestoneProgress(milestone.id, newProgress)
      },
      onSetStatus = { newStatus ->
        viewModel.setMilestoneStatus(milestone.id, newStatus)
      }
    )
  }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .testTag("career_strategy_view"),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // 1. Executive Strategy Banner & Hero Actions
    item {
      roadmap?.let { data ->
        CareerStrategyHeroHeader(
          roadmap = data,
          isGenerating = isGenerating,
          onOpenGenerator = { viewModel.setRoadmapGeneratorModalOpen(true) },
          onRefresh = { viewModel.generateCareerRoadmap() }
        )
      } ?: run {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(SlateElevated),
          contentAlignment = Alignment.Center
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = TitanCyan, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.height(12.dp))
            Text("Synthesizing Executive Career Strategy...", color = TextSecondaryDark, fontSize = 12.sp)
          }
        }
      }
    }

    roadmap?.let { data ->
      // 1B. Firebase Firestore Synchronization Status
      item {
        val firestoreSyncState by viewModel.firestoreStrategySyncState.collectAsState()
        val isSaving by viewModel.isSavingStrategyToFirestore.collectAsState()
        val statusMessage by viewModel.firestoreStrategyStatusMessage.collectAsState()

        Card(
          modifier = Modifier.fillMaxWidth().testTag("career_strategy_firestore_sync_bar"),
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = SlateCard),
          border = BorderStroke(
            1.dp,
            when (firestoreSyncState) {
              FirestoreSyncState.SYNCED -> TitanEmerald.copy(alpha = 0.5f)
              FirestoreSyncState.SYNCING -> TitanAmber.copy(alpha = 0.5f)
              FirestoreSyncState.FAILED -> TitanAmber.copy(alpha = 0.5f)
              FirestoreSyncState.IDLE -> SlateBorder
            }
          )
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp),
              modifier = Modifier.weight(1f)
            ) {
              Box(
                modifier = Modifier
                  .size(8.dp)
                  .clip(CircleShape)
                  .background(
                    when (firestoreSyncState) {
                      FirestoreSyncState.SYNCED -> TitanEmerald
                      FirestoreSyncState.SYNCING -> TitanAmber
                      FirestoreSyncState.FAILED -> TitanAmber
                      FirestoreSyncState.IDLE -> TitanCyan
                    }
                  )
              )
              Column {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                  Text("Firebase Firestore Vault", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                  Text("• users/{userId}/career_strategy", fontSize = 9.sp, color = TextMutedDark)
                }
                Text(statusMessage, fontSize = 10.sp, color = TextSecondaryDark)
              }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              IconButton(
                onClick = { viewModel.loadCareerStrategyFromFirestore(silent = false) },
                modifier = Modifier.size(32.dp)
              ) {
                Icon(Icons.Default.CloudDownload, contentDescription = "Restore from Cloud", tint = TitanCyan, modifier = Modifier.size(16.dp))
              }

              Button(
                onClick = { viewModel.saveCareerStrategyToFirestore(silent = false) },
                enabled = !isSaving,
                colors = ButtonDefaults.buttonColors(containerColor = TitanCyan),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.height(32.dp)
              ) {
                if (isSaving) {
                  CircularProgressIndicator(modifier = Modifier.size(12.dp), color = ObsidianDark, strokeWidth = 2.dp)
                  Spacer(modifier = Modifier.width(4.dp))
                  Text("Syncing...", fontSize = 10.sp, color = ObsidianDark, fontWeight = FontWeight.Bold)
                } else {
                  Icon(Icons.Default.CloudUpload, contentDescription = null, tint = ObsidianDark, modifier = Modifier.size(14.dp))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text("Save to Cloud", fontSize = 10.sp, color = ObsidianDark, fontWeight = FontWeight.Bold)
                }
              }
            }
          }
        }
      }

      // 2. Career Goal Completion Progress Bar & Interactive Roadmap Milestones Chart
      item {
        val firestoreSyncState by viewModel.firestoreStrategySyncState.collectAsState()
        val isSaving by viewModel.isSavingStrategyToFirestore.collectAsState()
        val lastSyncedTimestamp by viewModel.firestoreStrategyLastSyncedTimestamp.collectAsState()
        val currentRole by viewModel.currentRoleInput.collectAsState()
        val targetGoals by viewModel.targetCareerGoalsInput.collectAsState()

        CareerGoalCompletionChartCard(
          roadmap = data,
          currentRole = currentRole,
          targetGoals = targetGoals,
          syncState = firestoreSyncState,
          lastSyncedTimestamp = lastSyncedTimestamp,
          isSavingToFirestore = isSaving,
          onQuickSync = { viewModel.saveCareerStrategyToFirestore(silent = false) },
          onToggleMilestoneStatus = { milestoneId -> viewModel.toggleSkillMilestoneStatus(milestoneId) },
          onUpdateMilestoneProgress = { milestoneId, progress -> viewModel.updateSkillMilestoneProgress(milestoneId, progress) }
        )
      }

      // 2B. Gemini API Career Strategy & Advisory Engine
      item {
        GeminiCareerStrategyAdvisorCard(viewModel = viewModel)
      }

      // 3. Strategic Pillars Breakdown
      item {
        StrategicPillarsCard(pillars = data.strategicPillars)
      }

      // 4. Sequential Phases & Skill Acquisition Milestones
      items(data.phases, key = { it.phaseId }) { phase ->
        val isExpanded = expandedPhases.contains(phase.phaseNumber)
        PhaseMilestoneCard(
          phase = phase,
          isExpanded = isExpanded,
          onToggleExpand = {
            expandedPhases = if (isExpanded) {
              expandedPhases - phase.phaseNumber
            } else {
              expandedPhases + phase.phaseNumber
            }
          },
          onMilestoneClick = { milestone ->
            viewModel.selectMilestone(milestone)
          },
          onToggleMilestoneStatus = { milestoneId ->
            viewModel.toggleSkillMilestoneStatus(milestoneId)
          },
          onAddMilestoneClick = {
            addMilestoneTargetPhase = phase.phaseNumber
            isAddMilestoneDialogOpen = true
          }
        )
      }

      // 5. Tactical Playbook & Rules of Engagement
      item {
        TacticalPlaybookCard(playbook = data.tacticalPlaybook)
      }

      // Bottom Spacer
      item {
        Spacer(modifier = Modifier.height(24.dp))
      }
    }
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CareerStrategyHeroHeader(
  roadmap: CareerStrategyRoadmap,
  isGenerating: Boolean,
  onOpenGenerator: () -> Unit,
  onRefresh: () -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("career_strategy_hero_header"),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = SlateCard),
    border = BorderStroke(1.dp, TitanCyan.copy(alpha = 0.5f))
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(34.dp)
              .clip(CircleShape)
              .background(TitanCyan.copy(alpha = 0.15f))
              .border(1.dp, TitanCyan, CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Timeline,
              contentDescription = null,
              tint = TitanCyan,
              modifier = Modifier.size(18.dp)
            )
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "EXECUTIVE CAREER STRATEGY",
                style = MaterialTheme.typography.labelSmall,
                color = TitanCyan,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                fontSize = 10.sp
              )
              Spacer(modifier = Modifier.width(6.dp))
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(4.dp))
                  .background(TitanGold.copy(alpha = 0.15f))
                  .padding(horizontal = 5.dp, vertical = 2.dp)
              ) {
                Text(
                  text = "GEMINI 3.5 POWERED",
                  color = TitanGold,
                  fontSize = 8.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }
            Text(
              text = roadmap.targetRole,
              style = MaterialTheme.typography.titleMedium,
              color = TextPrimaryDark,
              fontWeight = FontWeight.Bold,
              fontSize = 15.sp
            )
          }
        }

        // Action Buttons
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          IconButton(
            onClick = onRefresh,
            modifier = Modifier
              .size(32.dp)
              .clip(CircleShape)
              .background(SlateElevated)
              .testTag("career_refresh_btn")
          ) {
            if (isGenerating) {
              CircularProgressIndicator(modifier = Modifier.size(14.dp), color = TitanCyan, strokeWidth = 2.dp)
            } else {
              Icon(Icons.Default.Refresh, contentDescription = "Regenerate", tint = TextSecondaryDark, modifier = Modifier.size(16.dp))
            }
          }

          Button(
            onClick = onOpenGenerator,
            colors = ButtonDefaults.buttonColors(containerColor = TitanCyan),
            shape = RoundedCornerShape(8.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
            modifier = Modifier.testTag("career_customize_roadmap_btn")
          ) {
            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = ObsidianDark, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Customize", color = ObsidianDark, fontWeight = FontWeight.Bold, fontSize = 11.sp)
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Metadata Chips Row
      FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        StrategyBadge(icon = Icons.Default.HourglassTop, label = roadmap.targetHorizon, color = TitanCyan)
        StrategyBadge(icon = Icons.Default.MonetizationOn, label = roadmap.targetCompensation, color = TitanEmerald)
        StrategyBadge(icon = Icons.Default.Verified, label = "${roadmap.confidenceScore}% Strategic Fit", color = TitanGold)
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Gemini Executive Synthesis Box
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(8.dp))
          .background(SlateElevated)
          .border(1.dp, SlateBorder, RoundedCornerShape(8.dp))
          .padding(10.dp)
      ) {
        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Lightbulb, contentDescription = null, tint = TitanGold, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("STRATEGIC THESIS & MARKET LEVERAGE", color = TitanGold, fontWeight = FontWeight.Bold, fontSize = 9.sp)
          }
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = roadmap.executiveSummary,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondaryDark,
            fontSize = 11.sp,
            lineHeight = 16.sp
          )
        }
      }
    }
  }
}

@Composable
fun StrategyBadge(
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  label: String,
  color: Color
) {
  Box(
    modifier = Modifier
      .clip(RoundedCornerShape(6.dp))
      .background(SlateElevated)
      .border(1.dp, color.copy(alpha = 0.35f), RoundedCornerShape(6.dp))
      .padding(horizontal = 8.dp, vertical = 4.dp)
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(12.dp))
      Spacer(modifier = Modifier.width(4.dp))
      Text(text = label, color = TextPrimaryDark, fontSize = 10.sp, fontWeight = FontWeight.Medium)
    }
  }
}

@Composable
fun OverallProgressCard(roadmap: CareerStrategyRoadmap) {
  val overallPct = roadmap.overallProgressPercentage
  val acquiredCount = roadmap.acquiredMilestonesCount
  val totalCount = roadmap.totalMilestonesCount

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("career_overall_progress_card"),
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = SlateCard),
    border = BorderStroke(1.dp, SlateBorder)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "CAREER CAPITAL ACQUISITION",
            style = MaterialTheme.typography.labelSmall,
            color = TitanEmerald,
            fontWeight = FontWeight.Bold,
            fontSize = 9.5.sp
          )
          Text(
            text = "$overallPct% Roadmap Maturity",
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimaryDark,
            fontWeight = FontWeight.Bold
          )
        }

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(SlateElevated)
            .border(1.dp, TitanEmerald.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 5.dp)
        ) {
          Text(
            text = "$acquiredCount of $totalCount Milestones Acquired",
            color = TitanEmerald,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Linear Progress Bar
      LinearProgressIndicator(
        progress = { overallPct / 100f },
        modifier = Modifier
          .fillMaxWidth()
          .height(8.dp)
          .clip(RoundedCornerShape(4.dp)),
        color = TitanEmerald,
        trackColor = SlateElevated
      )

      Spacer(modifier = Modifier.height(10.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        PhaseProgressBar(label = "Phase 1 (M 1-6)", progress = roadmap.phases.getOrNull(0)?.completionPercentage ?: 0, color = TitanCyan, modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.width(8.dp))
        PhaseProgressBar(label = "Phase 2 (M 6-18)", progress = roadmap.phases.getOrNull(1)?.completionPercentage ?: 0, color = TitanGold, modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.width(8.dp))
        PhaseProgressBar(label = "Phase 3 (M 18-36)", progress = roadmap.phases.getOrNull(2)?.completionPercentage ?: 0, color = TitanIndigo, modifier = Modifier.weight(1f))
      }
    }
  }
}

@Composable
fun PhaseProgressBar(
  label: String,
  progress: Int,
  color: Color,
  modifier: Modifier = Modifier
) {
  Column(modifier = modifier) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Text(text = label, style = MaterialTheme.typography.labelSmall, color = TextMutedDark, fontSize = 8.sp)
      Text(text = "$progress%", style = MaterialTheme.typography.labelSmall, color = color, fontWeight = FontWeight.Bold, fontSize = 8.sp)
    }
    Spacer(modifier = Modifier.height(3.dp))
    LinearProgressIndicator(
      progress = { progress / 100f },
      modifier = Modifier
        .fillMaxWidth()
        .height(4.dp)
        .clip(RoundedCornerShape(2.dp)),
      color = color,
      trackColor = SlateElevated
    )
  }
}

@Composable
fun StrategicPillarsCard(pillars: List<StrategicPillar>) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("career_strategic_pillars_card"),
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = SlateCard),
    border = BorderStroke(1.dp, SlateBorder)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.AutoMirrored.Filled.TrendingUp, contentDescription = null, tint = TitanGold, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "STRATEGIC LEVERAGE PILLARS",
            style = MaterialTheme.typography.labelSmall,
            color = TitanGold,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp
          )
        }
        Text(
          text = "Weight Distribution",
          style = MaterialTheme.typography.bodySmall,
          color = TextMutedDark,
          fontSize = 9.sp
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        pillars.forEach { pillar ->
          PillarItemRow(pillar = pillar)
        }
      }
    }
  }
}

@Composable
fun PillarItemRow(pillar: StrategicPillar) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(8.dp))
      .background(SlateElevated)
      .padding(10.dp)
  ) {
    Column {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = pillar.name,
          style = MaterialTheme.typography.titleSmall,
          color = TextPrimaryDark,
          fontWeight = FontWeight.Bold,
          fontSize = 11.5.sp
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(4.dp))
              .background(TitanCyan.copy(alpha = 0.15f))
              .padding(horizontal = 5.dp, vertical = 2.dp)
          ) {
            Text(
              text = "${pillar.weightPercentage}% Weight",
              color = TitanCyan,
              fontSize = 8.5.sp,
              fontWeight = FontWeight.Bold
            )
          }
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "${pillar.currentScore}/100 Readiness",
            color = if (pillar.currentScore >= 90) TitanEmerald else TitanGold,
            fontWeight = FontWeight.Bold,
            fontSize = 9.5.sp
          )
        }
      }

      Spacer(modifier = Modifier.height(4.dp))

      Text(
        text = pillar.description,
        style = MaterialTheme.typography.bodySmall,
        color = TextSecondaryDark,
        fontSize = 10.sp
      )

      Spacer(modifier = Modifier.height(6.dp))

      LinearProgressIndicator(
        progress = { pillar.currentScore / 100f },
        modifier = Modifier
          .fillMaxWidth()
          .height(4.dp)
          .clip(RoundedCornerShape(2.dp)),
        color = if (pillar.currentScore >= 90) TitanEmerald else TitanGold,
        trackColor = SlateCard
      )
    }
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PhaseMilestoneCard(
  phase: RoadmapPhase,
  isExpanded: Boolean,
  onToggleExpand: () -> Unit,
  onMilestoneClick: (SkillMilestone) -> Unit,
  onToggleMilestoneStatus: (String) -> Unit,
  onAddMilestoneClick: () -> Unit
) {
  val phaseColor = when (phase.phaseNumber) {
    1 -> TitanCyan
    2 -> TitanGold
    else -> TitanIndigo
  }

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .animateContentSize()
      .testTag("career_phase_${phase.phaseNumber}_card"),
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = SlateCard),
    border = BorderStroke(1.dp, phaseColor.copy(alpha = 0.45f))
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp)
    ) {
      // Phase Header Row
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clickable { onToggleExpand() },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(28.dp)
              .clip(CircleShape)
              .background(phaseColor.copy(alpha = 0.15f))
              .border(1.dp, phaseColor, CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "P${phase.phaseNumber}",
              color = phaseColor,
              fontWeight = FontWeight.Bold,
              fontSize = 11.sp
            )
          }

          Spacer(modifier = Modifier.width(10.dp))

          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "PHASE ${phase.phaseNumber}: ${phase.timeframe.uppercase()}",
                style = MaterialTheme.typography.labelSmall,
                color = phaseColor,
                fontWeight = FontWeight.Bold,
                fontSize = 9.5.sp
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "(${phase.completionPercentage}% Complete)",
                color = if (phase.completionPercentage == 100) TitanEmerald else TextMutedDark,
                fontSize = 8.5.sp,
                fontWeight = FontWeight.Bold
              )
            }
            Text(
              text = phase.title,
              style = MaterialTheme.typography.titleSmall,
              color = TextPrimaryDark,
              fontWeight = FontWeight.Bold,
              fontSize = 13.5.sp
            )
          }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          IconButton(
            onClick = onToggleExpand,
            modifier = Modifier.size(28.dp)
          ) {
            Icon(
              imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
              contentDescription = "Expand/Collapse",
              tint = TextSecondaryDark,
              modifier = Modifier.size(20.dp)
            )
          }
        }
      }

      // Compact Summary Badges
      Spacer(modifier = Modifier.height(8.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(SlateElevated)
            .padding(horizontal = 6.dp, vertical = 3.dp)
        ) {
          Text(
            text = "Role: ${phase.targetRoleTier}",
            color = TextSecondaryDark,
            fontSize = 9.sp
          )
        }
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(SlateElevated)
            .padding(horizontal = 6.dp, vertical = 3.dp)
        ) {
          Text(
            text = "Target: ${phase.targetCompLakhs}",
            color = TitanEmerald,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }

      AnimatedVisibility(
        visible = isExpanded,
        enter = fadeIn(),
        exit = fadeOut()
      ) {
        Column {
          Spacer(modifier = Modifier.height(10.dp))

          // Focus Theme Box
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(6.dp))
              .background(SlateElevated)
              .padding(8.dp)
          ) {
            Text(
              text = phase.focusTheme,
              style = MaterialTheme.typography.bodySmall,
              color = TextSecondaryDark,
              fontSize = 10.sp
            )
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Objectives Sub-Section
          Text(
            text = "STRATEGIC OBJECTIVES",
            style = MaterialTheme.typography.labelSmall,
            color = phaseColor,
            fontWeight = FontWeight.Bold,
            fontSize = 8.5.sp
          )
          Spacer(modifier = Modifier.height(4.dp))
          phase.keyObjectives.forEach { obj ->
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp),
              verticalAlignment = Alignment.Top
            ) {
              Text("• ", color = phaseColor, fontWeight = FontWeight.Bold, fontSize = 11.sp)
              Text(obj, color = TextSecondaryDark, fontSize = 10.5.sp, lineHeight = 15.sp)
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Skill Acquisition Milestones Header
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.School, contentDescription = null, tint = phaseColor, modifier = Modifier.size(14.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "SKILL ACQUISITION MILESTONES (${phase.skillMilestones.size})",
                style = MaterialTheme.typography.labelSmall,
                color = phaseColor,
                fontWeight = FontWeight.Bold,
                fontSize = 9.sp
              )
            }

            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(SlateElevated)
                .border(1.dp, phaseColor.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                .clickable { onAddMilestoneClick() }
                .padding(horizontal = 6.dp, vertical = 3.dp)
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Add, contentDescription = "Add", tint = phaseColor, modifier = Modifier.size(10.dp))
                Spacer(modifier = Modifier.width(2.dp))
                Text("Add Milestone", color = phaseColor, fontSize = 8.5.sp, fontWeight = FontWeight.Bold)
              }
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          // List of Skill Milestones
          Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            phase.skillMilestones.forEach { milestone ->
              SkillMilestoneItemRow(
                milestone = milestone,
                onClick = { onMilestoneClick(milestone) },
                onToggleStatus = { onToggleMilestoneStatus(milestone.id) }
              )
            }
          }
        }
      }
    }
  }
}

@Composable
fun SkillMilestoneItemRow(
  milestone: SkillMilestone,
  onClick: () -> Unit,
  onToggleStatus: () -> Unit
) {
  val statusColor = when (milestone.status) {
    CareerMilestoneStatus.ACQUIRED -> TitanEmerald
    CareerMilestoneStatus.IN_PROGRESS -> TitanGold
    CareerMilestoneStatus.NOT_STARTED -> TextMutedDark
  }

  val statusIcon = when (milestone.status) {
    CareerMilestoneStatus.ACQUIRED -> Icons.Default.CheckCircle
    CareerMilestoneStatus.IN_PROGRESS -> Icons.Default.HourglassTop
    CareerMilestoneStatus.NOT_STARTED -> Icons.Default.Pending
  }

  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(8.dp))
      .background(SlateElevated)
      .border(1.dp, statusColor.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
      .clickable { onClick() }
      .padding(10.dp)
      .testTag("milestone_item_${milestone.id}")
  ) {
    Column {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          modifier = Modifier.weight(1f),
          verticalAlignment = Alignment.CenterVertically
        ) {
          IconButton(
            onClick = onToggleStatus,
            modifier = Modifier.size(24.dp)
          ) {
            Icon(
              imageVector = statusIcon,
              contentDescription = milestone.status.label,
              tint = statusColor,
              modifier = Modifier.size(18.dp)
            )
          }

          Spacer(modifier = Modifier.width(6.dp))

          Column {
            Text(
              text = milestone.title,
              style = MaterialTheme.typography.titleSmall,
              color = TextPrimaryDark,
              fontWeight = FontWeight.Bold,
              fontSize = 11.5.sp
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = milestone.category,
                color = TitanCyan,
                fontSize = 8.5.sp,
                fontWeight = FontWeight.Medium
              )
              Text(" • ", color = TextMutedDark, fontSize = 8.5.sp)
              Text(
                text = "${milestone.estimatedWeeks} wks",
                color = TextSecondaryDark,
                fontSize = 8.5.sp
              )
              Text(" • ", color = TextMutedDark, fontSize = 8.5.sp)
              Text(
                text = milestone.targetProficiency,
                color = TitanGold,
                fontSize = 8.5.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(statusColor.copy(alpha = 0.15f))
            .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
          Text(
            text = "${milestone.progress}%",
            color = statusColor,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }

      if (milestone.proofOfWorkArtifact.isNotBlank()) {
        Spacer(modifier = Modifier.height(6.dp))
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .background(SlateCard)
            .padding(horizontal = 6.dp, vertical = 4.dp)
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Verified, contentDescription = null, tint = TitanEmerald, modifier = Modifier.size(10.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "Artifact: ${milestone.proofOfWorkArtifact}",
              color = TitanEmerald,
              fontSize = 8.5.sp,
              fontWeight = FontWeight.Medium
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(6.dp))

      LinearProgressIndicator(
        progress = { milestone.progress / 100f },
        modifier = Modifier
          .fillMaxWidth()
          .height(3.dp)
          .clip(RoundedCornerShape(1.5.dp)),
        color = statusColor,
        trackColor = SlateCard
      )
    }
  }
}

@Composable
fun TacticalPlaybookCard(playbook: List<String>) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("career_tactical_playbook_card"),
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = SlateCard),
    border = BorderStroke(1.dp, SlateBorder)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp)
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.Flag, contentDescription = null, tint = TitanCyan, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "TACTICAL PLAYBOOK & RULES OF ENGAGEMENT",
          style = MaterialTheme.typography.labelSmall,
          color = TitanCyan,
          fontWeight = FontWeight.Bold,
          fontSize = 10.sp
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        playbook.forEachIndexed { idx, rule ->
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
          ) {
            Box(
              modifier = Modifier
                .size(18.dp)
                .clip(CircleShape)
                .background(TitanCyan.copy(alpha = 0.15f)),
              contentAlignment = Alignment.Center
            ) {
              Text("${idx + 1}", color = TitanCyan, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = rule,
              style = MaterialTheme.typography.bodySmall,
              color = TextSecondaryDark,
              fontSize = 10.5.sp,
              lineHeight = 15.sp
            )
          }
        }
      }
    }
  }
}

@Composable
fun MilestoneDetailDialog(
  milestone: SkillMilestone,
  onDismiss: () -> Unit,
  onToggleStatus: () -> Unit,
  onUpdateProgress: (Int) -> Unit,
  onSetStatus: (CareerMilestoneStatus) -> Unit
) {
  var currentProgress by remember(milestone) { mutableFloatStateOf(milestone.progress.toFloat()) }

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(16.dp),
      color = SlateCard,
      border = BorderStroke(1.dp, TitanCyan.copy(alpha = 0.5f)),
      modifier = Modifier.fillMaxWidth().testTag("milestone_detail_dialog")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(18.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.School, contentDescription = null, tint = TitanCyan, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Skill Milestone Dossier", style = MaterialTheme.typography.titleMedium, color = TextPrimaryDark, fontWeight = FontWeight.Bold)
          }
          IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondaryDark, modifier = Modifier.size(18.dp))
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
          text = milestone.title,
          style = MaterialTheme.typography.titleMedium,
          color = TextPrimaryDark,
          fontWeight = FontWeight.Bold,
          fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        Row(
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(modifier = Modifier.clip(RoundedCornerShape(4.dp)).background(SlateElevated).padding(horizontal = 6.dp, vertical = 2.dp)) {
            Text(milestone.category, color = TitanCyan, fontSize = 9.sp, fontWeight = FontWeight.Bold)
          }
          Box(modifier = Modifier.clip(RoundedCornerShape(4.dp)).background(SlateElevated).padding(horizontal = 6.dp, vertical = 2.dp)) {
            Text("${milestone.estimatedWeeks} Weeks", color = TextSecondaryDark, fontSize = 9.sp)
          }
          Box(modifier = Modifier.clip(RoundedCornerShape(4.dp)).background(SlateElevated).padding(horizontal = 6.dp, vertical = 2.dp)) {
            Text(milestone.targetProficiency, color = TitanGold, fontSize = 9.sp, fontWeight = FontWeight.Bold)
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Progress Slider
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text("ACQUISITION PROGRESS", color = TitanEmerald, fontWeight = FontWeight.Bold, fontSize = 9.5.sp)
          Text("${currentProgress.toInt()}%", color = TitanEmerald, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }

        Slider(
          value = currentProgress,
          onValueChange = {
            currentProgress = it
            onUpdateProgress(it.toInt())
          },
          valueRange = 0f..100f,
          colors = SliderDefaults.colors(
            thumbColor = TitanEmerald,
            activeTrackColor = TitanEmerald,
            inactiveTrackColor = SlateElevated
          )
        )

        // Status Quick Selector Chips
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          CareerMilestoneStatus.values().forEach { st ->
            val isSel = milestone.status == st
            Box(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(6.dp))
                .background(if (isSel) TitanEmerald else SlateElevated)
                .border(1.dp, if (isSel) TitanEmerald else SlateBorder, RoundedCornerShape(6.dp))
                .clickable { onSetStatus(st) }
                .padding(vertical = 6.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = st.label,
                fontSize = 8.5.sp,
                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                color = if (isSel) ObsidianDark else TextSecondaryDark
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Key Learning Outcomes
        if (milestone.keyLearningOutcomes.isNotEmpty()) {
          Text("KEY LEARNING OUTCOMES", color = TitanGold, fontWeight = FontWeight.Bold, fontSize = 9.sp)
          Spacer(modifier = Modifier.height(4.dp))
          milestone.keyLearningOutcomes.forEach { outcome ->
            Row(modifier = Modifier.padding(vertical = 2.dp)) {
              Text("✓ ", color = TitanEmerald, fontWeight = FontWeight.Bold, fontSize = 11.sp)
              Text(outcome, color = TextSecondaryDark, fontSize = 10.5.sp)
            }
          }
          Spacer(modifier = Modifier.height(10.dp))
        }

        // Proof of Work Artifact
        if (milestone.proofOfWorkArtifact.isNotBlank()) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(8.dp))
              .background(SlateElevated)
              .padding(8.dp)
          ) {
            Column {
              Text("PROOF-OF-WORK DELIVERABLE", color = TitanCyan, fontWeight = FontWeight.Bold, fontSize = 8.5.sp)
              Spacer(modifier = Modifier.height(2.dp))
              Text(milestone.proofOfWorkArtifact, color = TextPrimaryDark, fontSize = 10.5.sp, fontWeight = FontWeight.Medium)
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Button(
          onClick = onDismiss,
          modifier = Modifier.fillMaxWidth(),
          colors = ButtonDefaults.buttonColors(containerColor = TitanCyan),
          shape = RoundedCornerShape(8.dp)
        ) {
          Text("Save & Close", color = ObsidianDark, fontWeight = FontWeight.Bold, fontSize = 11.sp)
        }
      }
    }
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RoadmapGeneratorDialog(
  currentRole: String,
  currentHorizon: Int,
  isGenerating: Boolean,
  onDismiss: () -> Unit,
  onGenerate: (String, Int, List<String>) -> Unit
) {
  var targetRole by remember { mutableStateOf(currentRole) }
  var selectedHorizon by remember { mutableIntStateOf(currentHorizon) }
  var selectedDirectives by remember { mutableStateOf(setOf("Top 1% Compensation", "Autonomous AI Workflows", "Bengaluru Tech & Quick-Commerce")) }

  val availableDirectives = listOf(
    "Top 1% Compensation",
    "Autonomous AI Workflows",
    "Bengaluru Tech & Quick-Commerce",
    "Chief of Staff & Board Ops",
    "M&A and VC Dealflow",
    "Consulting Partner Track",
    "Global Remote Tech (USD)"
  )

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(16.dp),
      color = SlateCard,
      border = BorderStroke(1.dp, TitanCyan.copy(alpha = 0.5f)),
      modifier = Modifier.fillMaxWidth().testTag("roadmap_generator_dialog")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(18.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = TitanGold, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Generate Career Strategy", style = MaterialTheme.typography.titleMedium, color = TextPrimaryDark, fontWeight = FontWeight.Bold)
          }
          IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondaryDark, modifier = Modifier.size(18.dp))
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text("TARGET EXECUTIVE ROLE", color = TitanCyan, fontWeight = FontWeight.Bold, fontSize = 9.sp)
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
          value = targetRole,
          onValueChange = { targetRole = it },
          modifier = Modifier.fillMaxWidth(),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = TitanCyan,
            unfocusedBorderColor = SlateBorder,
            focusedTextColor = TextPrimaryDark,
            unfocusedTextColor = TextPrimaryDark
          ),
          shape = RoundedCornerShape(8.dp),
          singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text("TIME HORIZON", color = TitanCyan, fontWeight = FontWeight.Bold, fontSize = 9.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          listOf(1 to "1 Year", 2 to "2 Years", 3 to "3 Years", 5 to "5 Years").forEach { (yrs, label) ->
            val isSel = selectedHorizon == yrs
            Box(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(6.dp))
                .background(if (isSel) TitanCyan else SlateElevated)
                .border(1.dp, if (isSel) TitanCyan else SlateBorder, RoundedCornerShape(6.dp))
                .clickable { selectedHorizon = yrs }
                .padding(vertical = 6.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = label,
                fontSize = 8.5.sp,
                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                color = if (isSel) ObsidianDark else TextSecondaryDark
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text("STRATEGIC DIRECTIVES & MULTIPLIERS", color = TitanGold, fontWeight = FontWeight.Bold, fontSize = 9.sp)
        Spacer(modifier = Modifier.height(4.dp))
        FlowRow(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          availableDirectives.forEach { directive ->
            val isSel = selectedDirectives.contains(directive)
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(if (isSel) TitanGold.copy(alpha = 0.2f) else SlateElevated)
                .border(1.dp, if (isSel) TitanGold else SlateBorder, RoundedCornerShape(6.dp))
                .clickable {
                  selectedDirectives = if (isSel) selectedDirectives - directive else selectedDirectives + directive
                }
                .padding(horizontal = 7.dp, vertical = 4.dp)
            ) {
              Text(
                text = directive,
                color = if (isSel) TitanGold else TextSecondaryDark,
                fontSize = 8.5.sp,
                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
          onClick = {
            onGenerate(targetRole, selectedHorizon, selectedDirectives.toList())
          },
          enabled = !isGenerating && targetRole.isNotBlank(),
          modifier = Modifier.fillMaxWidth(),
          colors = ButtonDefaults.buttonColors(containerColor = TitanCyan),
          shape = RoundedCornerShape(8.dp)
        ) {
          if (isGenerating) {
            CircularProgressIndicator(modifier = Modifier.size(16.dp), color = ObsidianDark, strokeWidth = 2.dp)
            Spacer(modifier = Modifier.width(6.dp))
            Text("Synthesizing with Gemini...", color = ObsidianDark, fontWeight = FontWeight.Bold, fontSize = 11.sp)
          } else {
            Icon(Icons.Default.RocketLaunch, contentDescription = null, tint = ObsidianDark, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Generate Long-Term Roadmap", color = ObsidianDark, fontWeight = FontWeight.Bold, fontSize = 11.sp)
          }
        }
      }
    }
  }
}

@Composable
fun AddCustomMilestoneDialog(
  phaseNumber: Int,
  onDismiss: () -> Unit,
  onAdd: (title: String, category: String, proficiency: String, weeks: Int, artifact: String) -> Unit
) {
  var title by remember { mutableStateOf("") }
  var category by remember { mutableStateOf("AI & Autonomous Ops") }
  var proficiency by remember { mutableStateOf("Advanced Practitioner") }
  var weeks by remember { mutableIntStateOf(4) }
  var artifact by remember { mutableStateOf("") }

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(16.dp),
      color = SlateCard,
      border = BorderStroke(1.dp, TitanCyan.copy(alpha = 0.5f)),
      modifier = Modifier.fillMaxWidth().testTag("add_custom_milestone_dialog")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(18.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text("Add Milestone to Phase $phaseNumber", style = MaterialTheme.typography.titleMedium, color = TextPrimaryDark, fontWeight = FontWeight.Bold)
          IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondaryDark, modifier = Modifier.size(18.dp))
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text("MILESTONE TITLE", color = TitanCyan, fontWeight = FontWeight.Bold, fontSize = 9.sp)
        Spacer(modifier = Modifier.height(3.dp))
        OutlinedTextField(
          value = title,
          onValueChange = { title = it },
          placeholder = { Text("e.g., Cross-Border Supply Chain Modeling", color = TextMutedDark, fontSize = 11.sp) },
          modifier = Modifier.fillMaxWidth(),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = TitanCyan,
            unfocusedBorderColor = SlateBorder,
            focusedTextColor = TextPrimaryDark,
            unfocusedTextColor = TextPrimaryDark
          ),
          shape = RoundedCornerShape(8.dp),
          singleLine = true
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text("CATEGORY", color = TitanCyan, fontWeight = FontWeight.Bold, fontSize = 9.sp)
        Spacer(modifier = Modifier.height(3.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          listOf("AI & Ops", "Strategy", "Financial/SQL", "Leadership").forEach { cat ->
            val isSel = category.startsWith(cat)
            Box(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(6.dp))
                .background(if (isSel) TitanCyan else SlateElevated)
                .border(1.dp, if (isSel) TitanCyan else SlateBorder, RoundedCornerShape(6.dp))
                .clickable { category = if (cat == "AI & Ops") "AI & Autonomous Ops" else if (cat == "Financial/SQL") "Financial Modeling & SQL" else cat }
                .padding(vertical = 4.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = cat,
                fontSize = 8.sp,
                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                color = if (isSel) ObsidianDark else TextSecondaryDark
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text("PROOF-OF-WORK ARTIFACT", color = TitanGold, fontWeight = FontWeight.Bold, fontSize = 9.sp)
        Spacer(modifier = Modifier.height(3.dp))
        OutlinedTextField(
          value = artifact,
          onValueChange = { artifact = it },
          placeholder = { Text("e.g. Published Python ETL Pipeline / Case Study", color = TextMutedDark, fontSize = 11.sp) },
          modifier = Modifier.fillMaxWidth(),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = TitanGold,
            unfocusedBorderColor = SlateBorder,
            focusedTextColor = TextPrimaryDark,
            unfocusedTextColor = TextPrimaryDark
          ),
          shape = RoundedCornerShape(8.dp),
          singleLine = true
        )

        Spacer(modifier = Modifier.height(14.dp))

        Button(
          onClick = {
            if (title.isNotBlank()) {
              onAdd(title, category, proficiency, weeks, artifact)
            }
          },
          enabled = title.isNotBlank(),
          modifier = Modifier.fillMaxWidth(),
          colors = ButtonDefaults.buttonColors(containerColor = TitanCyan),
          shape = RoundedCornerShape(8.dp)
        ) {
          Icon(Icons.Default.Add, contentDescription = null, tint = ObsidianDark, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Add Skill Milestone", color = ObsidianDark, fontWeight = FontWeight.Bold, fontSize = 11.sp)
        }
      }
    }
  }
}

@Composable
fun GeminiCareerStrategyAdvisorCard(
  viewModel: TitanViewModel,
  modifier: Modifier = Modifier
) {
  val adviceState by viewModel.careerStrategyAdviceUiState.collectAsState()
  var customQueryInput by remember { mutableStateOf("") }
  var selectedMode by remember { mutableStateOf(CareerStrategyMode.EXECUTIVE) }

  val presetQuestions = listOf(
    "How should I leverage the Razorpay ₹18.5L offer vs Microsoft director round?",
    "Actionable roadmap to transition into Chief of Staff in 18 months",
    "Brutal audit of my profile: BBA background vs tech pedigree",
    "How to command ₹24-30 LPA compensation package in Bengaluru"
  )

  Card(
    modifier = modifier.fillMaxWidth(),
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = SlateCard),
    border = BorderStroke(1.dp, if (adviceState.result != null) TitanGold.copy(alpha = 0.5f) else SlateBorder)
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      // Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.AutoAwesome,
            contentDescription = null,
            tint = TitanGold,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Column {
            Text(
              text = "GEMINI CAREER STRATEGY ADVISOR",
              style = MaterialTheme.typography.labelSmall,
              color = TitanGold,
              fontWeight = FontWeight.Black,
              fontSize = 11.sp
            )
            Text(
              text = "Executive Strategy & High-Stakes Career Advice (Service Wrapper)",
              style = MaterialTheme.typography.bodySmall,
              color = TextMutedDark,
              fontSize = 10.sp
            )
          }
        }

        if (adviceState.result != null) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(SlateElevated)
              .clickable { viewModel.clearCareerStrategyAdvice() }
              .padding(horizontal = 8.dp, vertical = 4.dp)
          ) {
            Text(
              text = "Clear / New Query",
              style = MaterialTheme.typography.labelSmall,
              color = TextSecondaryDark,
              fontSize = 9.sp
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Mode Selection Pills
      Text(
        text = "STRATEGY MODE:",
        style = MaterialTheme.typography.labelSmall,
        color = TitanCyan,
        fontWeight = FontWeight.Bold,
        fontSize = 9.sp
      )
      Spacer(modifier = Modifier.height(6.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        CareerStrategyMode.entries.forEach { mode ->
          val isSelected = selectedMode == mode
          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(6.dp))
              .background(if (isSelected) TitanCyan.copy(alpha = 0.2f) else SlateElevated)
              .border(
                1.dp,
                if (isSelected) TitanCyan else SlateBorder,
                RoundedCornerShape(6.dp)
              )
              .clickable { selectedMode = mode }
              .padding(vertical = 6.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = mode.label.take(10),
              style = MaterialTheme.typography.labelSmall,
              color = if (isSelected) TitanCyan else TextSecondaryDark,
              fontSize = 9.sp,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Preset Suggestions
      Text(
        text = "HIGH-STAKES CAREER DILEMMAS (TAP TO RUN):",
        style = MaterialTheme.typography.labelSmall,
        color = TextMutedDark,
        fontSize = 9.sp
      )
      Spacer(modifier = Modifier.height(6.dp))

      Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        presetQuestions.forEach { question ->
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(6.dp))
              .background(SlateElevated)
              .clickable {
                customQueryInput = question
                viewModel.requestCareerStrategyAdvice(
                  questionOrGoal = question,
                  mode = selectedMode
                )
              }
              .padding(horizontal = 8.dp, vertical = 6.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                contentDescription = null,
                tint = TitanGold,
                modifier = Modifier.size(12.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = question,
                style = MaterialTheme.typography.bodySmall,
                color = TextPrimaryDark,
                fontSize = 11.sp
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Custom Query Field
      OutlinedTextField(
        value = customQueryInput,
        onValueChange = { customQueryInput = it },
        placeholder = { Text("Ask Gemini any career strategy question or negotiation dilemma...", color = TextMutedDark, fontSize = 11.sp) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = TitanGold,
          unfocusedBorderColor = SlateBorder,
          focusedContainerColor = SlateElevated,
          unfocusedContainerColor = SlateElevated,
          focusedTextColor = TextPrimaryDark,
          unfocusedTextColor = TextPrimaryDark
        ),
        maxLines = 3
      )

      Spacer(modifier = Modifier.height(10.dp))

      Button(
        onClick = {
          if (customQueryInput.isNotBlank()) {
            viewModel.requestCareerStrategyAdvice(
              questionOrGoal = customQueryInput,
              mode = selectedMode
            )
          }
        },
        enabled = customQueryInput.isNotBlank() && !adviceState.isLoading,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(containerColor = TitanGold),
        shape = RoundedCornerShape(8.dp)
      ) {
        if (adviceState.isLoading) {
          CircularProgressIndicator(color = ObsidianDark, modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
          Spacer(modifier = Modifier.width(6.dp))
          Text("Synthesizing Strategy...", color = ObsidianDark, fontWeight = FontWeight.Bold, fontSize = 11.sp)
        } else {
          Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = ObsidianDark, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Advise Career Strategy", color = ObsidianDark, fontWeight = FontWeight.Bold, fontSize = 11.sp)
        }
      }

      // Render Result Card when available
      adviceState.result?.let { result ->
        Spacer(modifier = Modifier.height(14.dp))

        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(SlateElevated)
            .border(1.dp, TitanGold.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
            .padding(12.dp)
        ) {
          Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            // Mode & Confidence
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(4.dp))
                  .background(TitanGold.copy(alpha = 0.2f))
                  .padding(horizontal = 6.dp, vertical = 2.dp)
              ) {
                Text(
                  text = "MODE: ${result.mode.label.take(18).uppercase()}",
                  style = MaterialTheme.typography.labelSmall,
                  color = TitanGold,
                  fontWeight = FontWeight.Bold,
                  fontSize = 9.sp
                )
              }

              Text(
                text = "${result.confidenceScore}% CONFIDENCE",
                style = MaterialTheme.typography.labelSmall,
                color = TitanEmerald,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp
              )
            }

            // Executive Verdict
            Column {
              Text(
                text = "EXECUTIVE VERDICT:",
                style = MaterialTheme.typography.labelSmall,
                color = TitanCyan,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = result.executiveVerdict,
                style = MaterialTheme.typography.bodyMedium,
                color = TextPrimaryDark,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp
              )
            }

            // Market Positioning Statement
            Column {
              Text(
                text = "MARKET POSITIONING:",
                style = MaterialTheme.typography.labelSmall,
                color = TitanGold,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp
              )
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = result.marketPositioning,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondaryDark,
                fontSize = 11.sp
              )
            }

            // Compensation Bracket
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(6.dp))
                .background(TitanEmerald.copy(alpha = 0.08f))
                .border(1.dp, TitanEmerald.copy(alpha = 0.25f), RoundedCornerShape(6.dp))
                .padding(8.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(
                text = "TARGET COMPENSATION BAND",
                style = MaterialTheme.typography.labelSmall,
                color = TitanEmerald,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp
              )
              Text(
                text = result.recommendedCompensationRange,
                style = MaterialTheme.typography.labelMedium,
                color = TitanEmerald,
                fontWeight = FontWeight.Black,
                fontSize = 12.sp
              )
            }

            // Immediate 30-Day Priorities
            if (result.immediate30DayActions.isNotEmpty()) {
              Column {
                Text(
                  text = "IMMEDIATE 30-DAY ACTIONS:",
                  style = MaterialTheme.typography.labelSmall,
                  color = TitanCyan,
                  fontWeight = FontWeight.Bold,
                  fontSize = 10.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                result.immediate30DayActions.forEach { priority ->
                  Row(
                    modifier = Modifier.padding(vertical = 2.dp),
                    verticalAlignment = Alignment.Top
                  ) {
                    Text("⚡ ", fontSize = 10.sp)
                    Text(
                      text = priority,
                      style = MaterialTheme.typography.bodySmall,
                      color = TextPrimaryDark,
                      fontSize = 11.sp
                    )
                  }
                }
              }
            }

            // Negotiation Levers
            if (result.negotiationTactics.isNotEmpty()) {
              Column {
                Text(
                  text = "NEGOTIATION & DEALMAKING TACTICS:",
                  style = MaterialTheme.typography.labelSmall,
                  color = TitanGold,
                  fontWeight = FontWeight.Bold,
                  fontSize = 10.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                result.negotiationTactics.forEach { tactic ->
                  Row(
                    modifier = Modifier.padding(vertical = 2.dp),
                    verticalAlignment = Alignment.Top
                  ) {
                    Text("💼 ", fontSize = 10.sp)
                    Text(
                      text = tactic,
                      style = MaterialTheme.typography.bodySmall,
                      color = TextSecondaryDark,
                      fontSize = 11.sp
                    )
                  }
                }
              }
            }

            // Risks & Blindspots
            if (result.criticalRisksAndBlindspots.isNotEmpty()) {
              Column {
                Text(
                  text = "CRITICAL BLINDSPOTS TO AVOID:",
                  style = MaterialTheme.typography.labelSmall,
                  color = TitanCrimson,
                  fontWeight = FontWeight.Bold,
                  fontSize = 10.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                result.criticalRisksAndBlindspots.forEach { blindspot ->
                  Row(
                    modifier = Modifier.padding(vertical = 2.dp),
                    verticalAlignment = Alignment.Top
                  ) {
                    Text("⚠️ ", fontSize = 10.sp)
                    Text(
                      text = blindspot,
                      style = MaterialTheme.typography.bodySmall,
                      color = TextSecondaryDark,
                      fontSize = 11.sp
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
}
