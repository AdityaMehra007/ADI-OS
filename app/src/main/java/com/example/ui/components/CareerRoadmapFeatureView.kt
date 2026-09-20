package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CareerGrowthMilestone
import com.example.data.model.ComprehensiveCareerRoadmapReport
import com.example.data.model.MilestoneGrowthPhase
import com.example.data.model.SkillGapItem
import com.example.data.model.SkillGapSeverity
import com.example.data.model.SkillGapStatus
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
import com.example.ui.theme.TitanCyanMuted
import com.example.ui.theme.TitanEmerald
import com.example.ui.theme.TitanGold
import com.example.ui.theme.TitanIndigo
import com.example.ui.theme.TitanRose
import com.example.ui.theme.TitanViolet
import com.example.ui.viewmodel.TitanViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CareerRoadmapFeatureView(
  viewModel: TitanViewModel,
  modifier: Modifier = Modifier
) {
  val roadmapReport by viewModel.pipelineCareerRoadmap.collectAsState()
  val isAnalyzing by viewModel.isAnalyzingPipelineRoadmap.collectAsState()
  val companies by viewModel.companies.collectAsState()
  val applications by viewModel.applications.collectAsState()

  var selectedTab by remember { mutableIntStateOf(0) } // 0: Skill Gaps, 1: Milestone Growth Phases, 2: Strategic Playbook
  var severityFilter by remember { mutableStateOf<SkillGapSeverity?>(null) } // null = All
  var showExportDialog by remember { mutableStateOf(false) }

  Column(
    modifier = modifier
      .fillMaxWidth()
      .testTag("career_roadmap_feature_view"),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // 1. Executive Intelligence Header Card
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .border(1.dp, Brush.horizontalGradient(listOf(TitanCyan, TitanIndigo)), RoundedCornerShape(16.dp)),
      colors = CardDefaults.cardColors(containerColor = SlateCard),
      shape = RoundedCornerShape(16.dp)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(18.dp),
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
                .clip(CircleShape)
                .background(TitanCyan.copy(alpha = 0.15f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = "Gemini AI",
                tint = TitanCyan,
                modifier = Modifier.size(22.dp)
              )
            }
            Column {
              Text(
                text = "Career Growth Roadmap",
                style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
                color = TextPrimaryDark,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "Gemini API Pipeline & Skill-Gap Intelligence",
                style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                color = TitanCyan
              )
            }
          }

          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            OutlinedButton(
              onClick = { showExportDialog = true },
              colors = ButtonDefaults.outlinedButtonColors(contentColor = TitanCyan),
              border = androidx.compose.foundation.BorderStroke(1.dp, TitanCyan.copy(alpha = 0.5f)),
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier.testTag("export_roadmap_report_button")
            ) {
              Icon(
                imageVector = Icons.Default.Share,
                contentDescription = "Export Report",
                modifier = Modifier.size(15.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text("Export Report", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }

            FilledTonalButton(
              onClick = { viewModel.generatePipelineCareerRoadmap() },
              enabled = !isAnalyzing,
              colors = ButtonDefaults.filledTonalButtonColors(
                containerColor = TitanCyan.copy(alpha = 0.2f),
                contentColor = TitanCyan
              ),
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier.testTag("recalibrate_roadmap_button")
            ) {
              if (isAnalyzing) {
                CircularProgressIndicator(
                  modifier = Modifier.size(16.dp),
                  color = TitanCyan,
                  strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Analyzing...", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
              } else {
                Icon(
                  imageVector = Icons.Default.Refresh,
                  contentDescription = "Recalibrate",
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Recalibrate", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
              }
            }
          }
        }

        // Live Grounding & Scope Pill Badges
        FlowRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Surface(
            color = TitanEmerald.copy(alpha = 0.15f),
            shape = RoundedCornerShape(8.dp)
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              Box(
                modifier = Modifier
                  .size(6.dp)
                  .clip(CircleShape)
                  .background(TitanEmerald)
              )
              Text(
                text = if (roadmapReport?.isLiveGeminiGrounded == true) "Live Gemini 3.5 Flash Grounded" else "Strategic Intelligence Baseline",
                fontSize = 11.sp,
                color = TitanEmerald,
                fontWeight = FontWeight.Medium
              )
            }
          }

          Surface(
            color = SlateElevated,
            shape = RoundedCornerShape(8.dp)
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              Icon(Icons.Default.Business, contentDescription = null, tint = TitanGold, modifier = Modifier.size(12.dp))
              Text(
                text = "${roadmapReport?.metrics?.savedCompaniesCount ?: companies.size} Saved Companies",
                fontSize = 11.sp,
                color = TextSecondaryDark
              )
            }
          }

          Surface(
            color = SlateElevated,
            shape = RoundedCornerShape(8.dp)
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              Icon(Icons.AutoMirrored.Filled.TrendingUp, contentDescription = null, tint = TitanCyan, modifier = Modifier.size(12.dp))
              Text(
                text = "${roadmapReport?.metrics?.activeApplicationsCount ?: applications.size} Active Applications",
                fontSize = 11.sp,
                color = TextSecondaryDark
              )
            }
          }

          roadmapReport?.generatedAt?.let { ts ->
            Surface(
              color = SlateElevated,
              shape = RoundedCornerShape(8.dp)
            ) {
              Text(
                text = "Updated $ts",
                fontSize = 11.sp,
                color = TextMutedDark,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              )
            }
          }
        }

        // Executive Progress Bar
        roadmapReport?.let { report ->
          Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(
                text = "Overall Roadmap Velocity",
                fontSize = 12.sp,
                color = TextSecondaryDark,
                fontWeight = FontWeight.Medium
              )
              Text(
                text = "${report.overallProgressPercentage}% • ${report.completedMilestonesCount}/${report.totalMilestonesCount} Milestones • ${report.closedSkillGapsCount}/${report.skillGaps.size} Gaps Closed",
                fontSize = 12.sp,
                color = TitanCyan,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
              )
            }
            LinearProgressIndicator(
              progress = { report.overallProgressPercentage / 100f },
              modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
              color = TitanCyan,
              trackColor = SlateBorder
            )
          }
        }
      }
    }

    // 2. Navigation Tabs
    TabRow(
      selectedTabIndex = selectedTab,
      containerColor = SlateCard,
      contentColor = TitanCyan,
      indicator = { tabPositions ->
        TabRowDefaults.SecondaryIndicator(
          modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
          color = TitanCyan,
          height = 3.dp
        )
      },
      divider = { Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(SlateBorder)) }
    ) {
      Tab(
        selected = selectedTab == 0,
        onClick = { selectedTab = 0 },
        text = {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Icon(Icons.Default.Psychology, contentDescription = null, modifier = Modifier.size(16.dp))
            Text(
              "Skill-Gaps (${roadmapReport?.skillGaps?.size ?: 0})",
              fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
              fontSize = 13.sp
            )
          }
        },
        selectedContentColor = TitanCyan,
        unselectedContentColor = TextMutedDark
      )
      Tab(
        selected = selectedTab == 1,
        onClick = { selectedTab = 1 },
        text = {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Icon(Icons.Default.Flag, contentDescription = null, modifier = Modifier.size(16.dp))
            Text(
              "Growth Paths (3)",
              fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
              fontSize = 13.sp
            )
          }
        },
        selectedContentColor = TitanCyan,
        unselectedContentColor = TextMutedDark
      )
      Tab(
        selected = selectedTab == 2,
        onClick = { selectedTab = 2 },
        text = {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Icon(Icons.Default.Radar, contentDescription = null, modifier = Modifier.size(16.dp))
            Text(
              "Visual Roadmap",
              fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal,
              fontSize = 13.sp
            )
          }
        },
        selectedContentColor = TitanCyan,
        unselectedContentColor = TextMutedDark
      )
      Tab(
        selected = selectedTab == 3,
        onClick = { selectedTab = 3 },
        text = {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Icon(Icons.Default.Lightbulb, contentDescription = null, modifier = Modifier.size(16.dp))
            Text(
              "Playbook",
              fontWeight = if (selectedTab == 3) FontWeight.Bold else FontWeight.Normal,
              fontSize = 13.sp
            )
          }
        },
        selectedContentColor = TitanCyan,
        unselectedContentColor = TextMutedDark
      )
    }

    // 3. Tab Content
    when (selectedTab) {
      0 -> {
        // Tab 0: Skill-Gaps Matrix
        roadmapReport?.let { report ->
          SkillGapsMatrixSection(
            report = report,
            severityFilter = severityFilter,
            onFilterChange = { severityFilter = it },
            onToggleStatus = { gapId -> viewModel.toggleSkillGapStatus(gapId) },
            onUpdateProgress = { gapId, progress -> viewModel.updateSkillGapProgress(gapId, progress) }
          )
        } ?: EmptyRoadmapState(isAnalyzing = isAnalyzing, onGenerate = { viewModel.generatePipelineCareerRoadmap() })
      }
      1 -> {
        // Tab 1: Milestone Growth Paths
        roadmapReport?.let { report ->
          MilestoneGrowthPathsSection(
            phases = report.growthPhases,
            onToggleMilestone = { phaseId, milestoneId ->
              viewModel.toggleRoadmapGrowthMilestone(phaseId, milestoneId)
            },
            onUpdateMilestoneProgress = { phaseId, milestoneId, progress ->
              viewModel.updateRoadmapGrowthMilestoneProgress(phaseId, milestoneId, progress)
            }
          )
        } ?: EmptyRoadmapState(isAnalyzing = isAnalyzing, onGenerate = { viewModel.generatePipelineCareerRoadmap() })
      }
      2 -> {
        // Tab 2: Visual Career Roadmap (D3 / Recharts Industry Requirement Mapping)
        roadmapReport?.let { report ->
          CareerRoadmapSkillsVisualization(
            report = report,
            onToggleSkillGapStatus = { gapId -> viewModel.toggleSkillGapStatus(gapId) },
            onUpdateSkillProgress = { gapId, progress -> viewModel.updateSkillGapProgress(gapId, progress) },
            onExportReport = { showExportDialog = true }
          )
        } ?: EmptyRoadmapState(isAnalyzing = isAnalyzing, onGenerate = { viewModel.generatePipelineCareerRoadmap() })
      }
      3 -> {
        // Tab 3: Strategic Playbook & Market Diagnosis
        roadmapReport?.let { report ->
          StrategicPlaybookSection(report = report)
        } ?: EmptyRoadmapState(isAnalyzing = isAnalyzing, onGenerate = { viewModel.generatePipelineCareerRoadmap() })
      }
    }

    if (showExportDialog) {
      CareerRoadmapReportExportDialog(
        roadmapReport = roadmapReport,
        applications = applications,
        candidateName = "Adi",
        onDismiss = { showExportDialog = false }
      )
    }
  }
}

// ---------------------------------------------------------------------------
// Skill-Gaps Matrix Section
// ---------------------------------------------------------------------------
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SkillGapsMatrixSection(
  report: ComprehensiveCareerRoadmapReport,
  severityFilter: SkillGapSeverity?,
  onFilterChange: (SkillGapSeverity?) -> Unit,
  onToggleStatus: (String) -> Unit,
  onUpdateProgress: (String, Int) -> Unit
) {
  Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
    // Filter Chips
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "Filter:",
        fontSize = 12.sp,
        color = TextMutedDark,
        fontWeight = FontWeight.Medium
      )

      FilterChipItem(
        label = "All (${report.skillGaps.size})",
        isSelected = severityFilter == null,
        onClick = { onFilterChange(null) },
        selectedColor = TitanCyan
      )

      FilterChipItem(
        label = "Critical",
        isSelected = severityFilter == SkillGapSeverity.CRITICAL,
        onClick = { onFilterChange(SkillGapSeverity.CRITICAL) },
        selectedColor = TitanCrimson
      )

      FilterChipItem(
        label = "High Priority",
        isSelected = severityFilter == SkillGapSeverity.HIGH_PRIORITY,
        onClick = { onFilterChange(SkillGapSeverity.HIGH_PRIORITY) },
        selectedColor = TitanAmber
      )

      FilterChipItem(
        label = "Moat",
        isSelected = severityFilter == SkillGapSeverity.COMPETITIVE_MOAT,
        onClick = { onFilterChange(SkillGapSeverity.COMPETITIVE_MOAT) },
        selectedColor = TitanEmerald
      )
    }

    val displayedGaps = if (severityFilter == null) {
      report.skillGaps
    } else {
      report.skillGaps.filter { it.severity == severityFilter }
    }

    displayedGaps.forEach { gap ->
      SkillGapCard(
        gap = gap,
        onToggleStatus = { onToggleStatus(gap.id) },
        onUpdateProgress = { onUpdateProgress(gap.id, it) }
      )
    }
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SkillGapCard(
  gap: SkillGapItem,
  onToggleStatus: () -> Unit,
  onUpdateProgress: (Int) -> Unit
) {
  var isExpanded by remember { mutableStateOf(false) }

  val severityColor = when (gap.severity) {
    SkillGapSeverity.CRITICAL -> TitanCrimson
    SkillGapSeverity.HIGH_PRIORITY -> TitanAmber
    SkillGapSeverity.COMPETITIVE_MOAT -> TitanEmerald
  }

  val statusColor = when (gap.status) {
    SkillGapStatus.CLOSED -> TitanEmerald
    SkillGapStatus.IN_PROGRESS -> TitanCyan
    SkillGapStatus.IDENTIFIED -> TextMutedDark
  }

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .border(1.dp, if (gap.status == SkillGapStatus.CLOSED) TitanEmerald.copy(alpha = 0.5f) else SlateBorder, RoundedCornerShape(12.dp))
      .animateContentSize(tween(250))
      .testTag("skill_gap_card_${gap.id}"),
    colors = CardDefaults.cardColors(containerColor = SlateCard),
    shape = RoundedCornerShape(12.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      // Top Row: Category & Severity + Status Pill
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Surface(
            color = severityColor.copy(alpha = 0.15f),
            shape = RoundedCornerShape(6.dp)
          ) {
            Text(
              text = gap.severity.displayName,
              fontSize = 11.sp,
              color = severityColor,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
            )
          }

          Text(
            text = gap.category,
            fontSize = 11.sp,
            color = TextMutedDark,
            fontWeight = FontWeight.Medium
          )
        }

        // Interactive Status Pill
        Surface(
          color = statusColor.copy(alpha = 0.15f),
          shape = RoundedCornerShape(16.dp),
          modifier = Modifier.clickable { onToggleStatus() }
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            Icon(
              imageVector = if (gap.status == SkillGapStatus.CLOSED) Icons.Default.CheckCircle else Icons.Outlined.RadioButtonUnchecked,
              contentDescription = null,
              tint = statusColor,
              modifier = Modifier.size(13.dp)
            )
            Text(
              text = gap.status.label,
              fontSize = 11.sp,
              color = statusColor,
              fontWeight = FontWeight.SemiBold
            )
          }
        }
      }

      // Title
      Text(
        text = gap.skillName,
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold,
        color = TextPrimaryDark
      )

      // Demanded by Saved Companies & Affected Applications Chips
      FlowRow(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        Text(
          text = "Demanded By:",
          fontSize = 11.sp,
          color = TextMutedDark,
          modifier = Modifier.align(Alignment.CenterVertically)
        )
        gap.demandedByCompanies.forEach { comp ->
          Surface(
            color = TitanIndigo.copy(alpha = 0.15f),
            shape = RoundedCornerShape(6.dp)
          ) {
            Text(
              text = comp,
              fontSize = 11.sp,
              color = TitanIndigo,
              fontWeight = FontWeight.Medium,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
        }

        Surface(
          color = SlateElevated,
          shape = RoundedCornerShape(6.dp)
        ) {
          Text(
            text = "${gap.affectedApplicationsCount} active apps",
            fontSize = 11.sp,
            color = TextSecondaryDark,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
          )
        }
      }

      // Benchmark: Current vs Required
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(SlateElevated, RoundedCornerShape(8.dp))
          .padding(horizontal = 10.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text("Candidate Level", fontSize = 10.sp, color = TextMutedDark)
          Text(gap.candidateLevel, fontSize = 12.sp, color = TextSecondaryDark, fontWeight = FontWeight.SemiBold)
        }
        Icon(Icons.AutoMirrored.Filled.TrendingUp, contentDescription = null, tint = TitanCyan, modifier = Modifier.size(16.dp))
        Column(horizontalAlignment = Alignment.End) {
          Text("Company Standard Bar", fontSize = 10.sp, color = TextMutedDark)
          Text(gap.requiredLevel, fontSize = 12.sp, color = TitanGold, fontWeight = FontWeight.SemiBold)
        }
      }

      // Progress Slider
      Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text("Gap Closure Progress", fontSize = 11.sp, color = TextMutedDark)
          Text("${gap.progress}%", fontSize = 11.sp, color = TitanCyan, fontWeight = FontWeight.Bold)
        }
        Slider(
          value = gap.progress.toFloat(),
          onValueChange = { onUpdateProgress(it.toInt()) },
          valueRange = 0f..100f,
          colors = SliderDefaults.colors(
            thumbColor = TitanCyan,
            activeTrackColor = TitanCyan,
            inactiveTrackColor = SlateBorder
          ),
          modifier = Modifier.height(24.dp)
        )
      }

      // Expand / Collapse Details Button
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clickable { isExpanded = !isExpanded }
          .padding(top = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = if (isExpanded) "Hide Proof-of-Work Projects" else "View Proof-of-Work Closing Projects (${gap.recommendedProjects.size})",
          fontSize = 12.sp,
          color = TitanCyan,
          fontWeight = FontWeight.Medium
        )
        Icon(
          imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
          contentDescription = null,
          tint = TitanCyan,
          modifier = Modifier.size(18.dp)
        )
      }

      AnimatedVisibility(visible = isExpanded) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Text(
            text = "Why Crucial for Target Companies:",
            fontSize = 11.sp,
            color = TextMutedDark,
            fontWeight = FontWeight.SemiBold
          )
          Text(
            text = gap.relevanceRationale,
            fontSize = 12.sp,
            color = TextSecondaryDark,
            lineHeight = 17.sp
          )

          Spacer(modifier = Modifier.height(2.dp))

          Text(
            text = "Recommended Proof-of-Work Deliverables:",
            fontSize = 11.sp,
            color = TitanEmerald,
            fontWeight = FontWeight.SemiBold
          )
          gap.recommendedProjects.forEachIndexed { idx, proj ->
            Row(
              horizontalArrangement = Arrangement.spacedBy(8.dp),
              modifier = Modifier.fillMaxWidth()
            ) {
              Text(
                text = "${idx + 1}.",
                fontSize = 12.sp,
                color = TitanEmerald,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = proj,
                fontSize = 12.sp,
                color = TextPrimaryDark,
                lineHeight = 16.sp
              )
            }
          }
        }
      }
    }
  }
}

// ---------------------------------------------------------------------------
// Milestone Growth Paths Section
// ---------------------------------------------------------------------------
@Composable
private fun MilestoneGrowthPathsSection(
  phases: List<MilestoneGrowthPhase>,
  onToggleMilestone: (String, String) -> Unit,
  onUpdateMilestoneProgress: (String, String, Int) -> Unit
) {
  Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
    phases.forEach { phase ->
      MilestoneGrowthPhaseCard(
        phase = phase,
        onToggleMilestone = { mId -> onToggleMilestone(phase.phaseId, mId) },
        onUpdateMilestoneProgress = { mId, prog -> onUpdateMilestoneProgress(phase.phaseId, mId, prog) }
      )
    }
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun MilestoneGrowthPhaseCard(
  phase: MilestoneGrowthPhase,
  onToggleMilestone: (String) -> Unit,
  onUpdateMilestoneProgress: (String, Int) -> Unit
) {
  val isCompleted = phase.isFullyCompleted
  val borderColor = if (isCompleted) TitanEmerald else when (phase.phaseNumber) {
    1 -> TitanCyan
    2 -> TitanIndigo
    else -> TitanViolet
  }

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .border(1.dp, borderColor.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
      .testTag("growth_phase_${phase.phaseNumber}"),
    colors = CardDefaults.cardColors(containerColor = SlateCard),
    shape = RoundedCornerShape(14.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      // Phase Header Banner
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Surface(
            color = borderColor.copy(alpha = 0.2f),
            shape = RoundedCornerShape(8.dp)
          ) {
            Text(
              text = "PHASE ${phase.phaseNumber}",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = borderColor,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
          }

          Text(
            text = phase.timeframe,
            fontSize = 12.sp,
            color = TextMutedDark,
            fontWeight = FontWeight.Medium
          )
        }

        Surface(
          color = TitanGold.copy(alpha = 0.15f),
          shape = RoundedCornerShape(8.dp)
        ) {
          Text(
            text = phase.projectedCompensation,
            fontSize = 11.sp,
            color = TitanGold,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
          )
        }
      }

      // Title & Target Role
      Column {
        Text(
          text = phase.title,
          fontSize = 16.sp,
          fontWeight = FontWeight.Bold,
          color = TextPrimaryDark
        )
        Text(
          text = "Target Role: ${phase.targetRoleTier}",
          fontSize = 12.sp,
          color = TitanCyan,
          fontWeight = FontWeight.SemiBold
        )
      }

      // Strategic Focus
      Text(
        text = phase.strategicFocus,
        fontSize = 12.sp,
        color = TextSecondaryDark,
        lineHeight = 17.sp
      )

      // Unlocks Target Companies Chips
      FlowRow(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(4.dp),
          modifier = Modifier.align(Alignment.CenterVertically)
        ) {
          Icon(Icons.Default.LockOpen, contentDescription = null, tint = TitanEmerald, modifier = Modifier.size(13.dp))
          Text("Unlocks:", fontSize = 11.sp, color = TextMutedDark)
        }
        phase.targetCompaniesUnlocked.forEach { comp ->
          Surface(
            color = TitanEmerald.copy(alpha = 0.15f),
            shape = RoundedCornerShape(6.dp)
          ) {
            Text(
              text = comp,
              fontSize = 11.sp,
              color = TitanEmerald,
              fontWeight = FontWeight.Medium,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
        }
      }

      // Phase Progress Indicator
      Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text("Phase Completion", fontSize = 11.sp, color = TextMutedDark)
          Text("${phase.completionPercentage}%", fontSize = 11.sp, color = borderColor, fontWeight = FontWeight.Bold)
        }
        LinearProgressIndicator(
          progress = { phase.completionPercentage / 100f },
          modifier = Modifier
            .fillMaxWidth()
            .height(6.dp)
            .clip(RoundedCornerShape(3.dp)),
          color = borderColor,
          trackColor = SlateBorder
        )
      }

      Spacer(modifier = Modifier.height(4.dp))

      // Milestone Items
      Text(
        text = "Key Milestones & Deliverables:",
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = TextPrimaryDark
      )

      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        phase.milestones.forEach { milestone ->
          GrowthMilestoneItemRow(
            milestone = milestone,
            onToggle = { onToggleMilestone(milestone.id) },
            onUpdateProgress = { onUpdateMilestoneProgress(milestone.id, it) }
          )
        }
      }
    }
  }
}

@Composable
private fun GrowthMilestoneItemRow(
  milestone: CareerGrowthMilestone,
  onToggle: () -> Unit,
  onUpdateProgress: (Int) -> Unit
) {
  Surface(
    color = SlateElevated,
    shape = RoundedCornerShape(10.dp),
    modifier = Modifier
      .fillMaxWidth()
      .border(0.5.dp, if (milestone.isCompleted) TitanEmerald.copy(alpha = 0.5f) else SlateBorder, RoundedCornerShape(10.dp))
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp),
      verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          modifier = Modifier.weight(1f)
        ) {
          IconButton(
            onClick = onToggle,
            modifier = Modifier.size(24.dp)
          ) {
            Icon(
              imageVector = if (milestone.isCompleted) Icons.Default.CheckCircle else Icons.Outlined.RadioButtonUnchecked,
              contentDescription = "Toggle Complete",
              tint = if (milestone.isCompleted) TitanEmerald else TextMutedDark,
              modifier = Modifier.size(20.dp)
            )
          }

          Text(
            text = milestone.title,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (milestone.isCompleted) TextMutedDark else TextPrimaryDark
          )
        }

        Surface(
          color = TitanCyan.copy(alpha = 0.12f),
          shape = RoundedCornerShape(4.dp)
        ) {
          Text(
            text = milestone.timeEstimate,
            fontSize = 10.sp,
            color = TitanCyan,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
          )
        }
      }

      // Key Deliverable & Verification
      Text(
        text = "Deliverable: ${milestone.keyDeliverable}",
        fontSize = 11.sp,
        color = TextSecondaryDark,
        lineHeight = 15.sp,
        modifier = Modifier.padding(start = 32.dp)
      )

      Text(
        text = "Verification: ${milestone.verificationCriteria}",
        fontSize = 10.sp,
        color = TextMutedDark,
        lineHeight = 14.sp,
        modifier = Modifier.padding(start = 32.dp)
      )

      // Artifact Link
      if (milestone.proofOfWorkArtifact.isNotBlank()) {
        Row(
          modifier = Modifier
            .padding(start = 32.dp, top = 2.dp)
            .background(SlateCard, RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp, vertical = 3.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          Icon(Icons.AutoMirrored.Filled.TrendingUp, contentDescription = null, tint = TitanCyan, modifier = Modifier.size(11.dp))
          Text(
            text = milestone.proofOfWorkArtifact,
            fontSize = 10.sp,
            color = TitanCyan,
            fontFamily = FontFamily.Monospace
          )
        }
      }
    }
  }
}

// ---------------------------------------------------------------------------
// Strategic Playbook & Market Diagnosis Section
// ---------------------------------------------------------------------------
@Composable
private fun StrategicPlaybookSection(report: ComprehensiveCareerRoadmapReport) {
  Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
    // Market Diagnosis Card
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .border(1.dp, SlateBorder, RoundedCornerShape(14.dp)),
      colors = CardDefaults.cardColors(containerColor = SlateCard),
      shape = RoundedCornerShape(14.dp)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Icon(Icons.Default.Psychology, contentDescription = null, tint = TitanCyan, modifier = Modifier.size(20.dp))
          Text(
            text = "Executive Market Diagnosis",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimaryDark
          )
        }

        Text(
          text = report.metrics.executiveMarketDiagnosis,
          fontSize = 13.sp,
          color = TextSecondaryDark,
          lineHeight = 19.sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = TitanGold, modifier = Modifier.size(16.dp))
          Text(
            text = "Strategic Advantage Identified:",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = TitanGold
          )
        }

        Text(
          text = report.metrics.strategicAdvantageIdentified,
          fontSize = 12.sp,
          color = TextSecondaryDark,
          lineHeight = 17.sp
        )
      }
    }

    // High-Leverage Strategic Actions
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .border(1.dp, SlateBorder, RoundedCornerShape(14.dp)),
      colors = CardDefaults.cardColors(containerColor = SlateCard),
      shape = RoundedCornerShape(14.dp)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Icon(Icons.Default.Lightbulb, contentDescription = null, tint = TitanEmerald, modifier = Modifier.size(20.dp))
          Text(
            text = "High-Leverage Pipeline Tactics",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimaryDark
          )
        }

        report.highLeverageTactics.forEachIndexed { idx, tactic ->
          Surface(
            color = SlateElevated,
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier.padding(12.dp),
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              Box(
                modifier = Modifier
                  .size(24.dp)
                  .clip(CircleShape)
                  .background(TitanEmerald.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = "${idx + 1}",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = TitanEmerald
                )
              }
              Text(
                text = tactic,
                fontSize = 12.sp,
                color = TextPrimaryDark,
                lineHeight = 17.sp,
                modifier = Modifier.weight(1f)
              )
            }
          }
        }
      }
    }

    // Executive Verdict Card
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .border(1.dp, Brush.horizontalGradient(listOf(TitanGold, TitanAmber)), RoundedCornerShape(14.dp)),
      colors = CardDefaults.cardColors(containerColor = SlateCard),
      shape = RoundedCornerShape(14.dp)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Icon(Icons.Default.Flag, contentDescription = null, tint = TitanGold, modifier = Modifier.size(18.dp))
          Text(
            text = "Gemini Executive Verdict",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = TitanGold
          )
        }

        Text(
          text = report.geminiExecutiveVerdict,
          fontSize = 13.sp,
          color = TextSecondaryDark,
          lineHeight = 19.sp,
          fontWeight = FontWeight.Normal
        )
      }
    }
  }
}

// ---------------------------------------------------------------------------
// Helpers & Subcomponents
// ---------------------------------------------------------------------------
@Composable
private fun FilterChipItem(
  label: String,
  isSelected: Boolean,
  onClick: () -> Unit,
  selectedColor: Color
) {
  Surface(
    color = if (isSelected) selectedColor.copy(alpha = 0.2f) else SlateElevated,
    shape = RoundedCornerShape(16.dp),
    modifier = Modifier
      .clickable { onClick() }
      .border(
        0.5.dp,
        if (isSelected) selectedColor else SlateBorder,
        RoundedCornerShape(16.dp)
      )
  ) {
    Text(
      text = label,
      fontSize = 11.sp,
      fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
      color = if (isSelected) selectedColor else TextMutedDark,
      modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
    )
  }
}

@Composable
private fun EmptyRoadmapState(
  isAnalyzing: Boolean,
  onGenerate: () -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 24.dp),
    colors = CardDefaults.cardColors(containerColor = SlateCard),
    shape = RoundedCornerShape(14.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(24.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      Icon(
        imageVector = Icons.Default.AutoAwesome,
        contentDescription = null,
        tint = TitanCyan,
        modifier = Modifier.size(44.dp)
      )
      Text(
        text = "Analyze Saved Companies & Pipeline",
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        color = TextPrimaryDark
      )
      Text(
        text = "Run Gemini AI to diagnose skill gaps across all your saved target companies and construct a 3-phase milestone growth path.",
        fontSize = 13.sp,
        color = TextSecondaryDark,
        textAlign = androidx.compose.ui.text.style.TextAlign.Center
      )
      FilledTonalButton(
        onClick = onGenerate,
        enabled = !isAnalyzing,
        colors = ButtonDefaults.filledTonalButtonColors(
          containerColor = TitanCyan,
          contentColor = ObsidianDark
        ),
        shape = RoundedCornerShape(10.dp)
      ) {
        if (isAnalyzing) {
          CircularProgressIndicator(modifier = Modifier.size(16.dp), color = ObsidianDark, strokeWidth = 2.dp)
          Spacer(modifier = Modifier.width(8.dp))
          Text("Diagnosing Pipeline with Gemini...")
        } else {
          Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text("Generate Career Roadmap", fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}
