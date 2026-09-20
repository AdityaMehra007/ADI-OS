package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CoreCompetency
import com.example.data.model.IndustrySkillGap
import com.example.data.model.SkillGapAnalysisReport
import com.example.data.model.TargetIndustryCatalog
import com.example.ui.theme.ObsidianDark
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateCard
import com.example.ui.theme.SlateDarker
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
fun SkillMappingInterfaceView(
  viewModel: TitanViewModel,
  modifier: Modifier = Modifier
) {
  val competencies by viewModel.coreCompetencies.collectAsState()
  val selectedIndustryFilter by viewModel.selectedIndustryFilter.collectAsState()
  val selectedIndustriesForAnalysis by viewModel.selectedTargetIndustriesForAnalysis.collectAsState()
  val gapReport by viewModel.skillGapAnalysisReport.collectAsState()
  val isAnalyzingGaps by viewModel.isAnalyzingSkillGaps.collectAsState()
  val statusMessage by viewModel.skillGapStatusMessage.collectAsState()
  val showAddDialog by viewModel.showAddCompetencyDialog.collectAsState()
  val competencyUnderEdit by viewModel.competencyUnderEdit.collectAsState()

  // Filtered competencies based on selected industry chip
  val filteredCompetencies = remember(competencies, selectedIndustryFilter) {
    if (selectedIndustryFilter == "ALL") {
      competencies
    } else {
      competencies.filter { comp ->
        comp.targetIndustries.any { it.equals(selectedIndustryFilter, ignoreCase = true) }
      }
    }
  }

  val coveredIndustriesCount = remember(competencies) {
    competencies.flatMap { it.targetIndustries }.distinct().size
  }

  val criticalGapsCount = remember(gapReport) {
    gapReport?.gaps?.count { it.gapSeverity.contains("CRITICAL", ignoreCase = true) } ?: 0
  }

  Column(
    modifier = modifier
      .fillMaxWidth()
      .padding(bottom = 24.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // 1. Executive Metric Summary Header
    SkillMappingExecutiveMetricsCard(
      totalCompetencies = competencies.size,
      coveredIndustries = coveredIndustriesCount,
      marketAlignmentScore = gapReport?.marketAlignmentScore ?: 88,
      criticalGapsCount = criticalGapsCount
    )

    // 2. Gemini 2026 Market Gap Analyzer Card
    GeminiMarketGapAnalyzerSection(
      isAnalyzing = isAnalyzingGaps,
      statusMessage = statusMessage,
      selectedIndustries = selectedIndustriesForAnalysis,
      report = gapReport,
      onToggleIndustryForAnalysis = { viewModel.toggleIndustryForGapAnalysis(it) },
      onRunAnalysis = { viewModel.runGeminiSkillGapAnalysis(forceRefresh = true) },
      onAdoptGap = { viewModel.adoptSuggestedSkillGap(it) }
    )

    // 3. Target Industry Filter Chips Bar
    TargetIndustryFilterBar(
      selectedFilter = selectedIndustryFilter,
      onSelectFilter = { viewModel.setSelectedIndustryFilter(it) },
      competencies = competencies
    )

    // 4. Competencies List Header & Add Button
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Text(
          text = if (selectedIndustryFilter == "ALL") "ALL CORE COMPETENCIES (${filteredCompetencies.size})"
                 else "${selectedIndustryFilter.uppercase()} COMPETENCIES (${filteredCompetencies.size})",
          style = MaterialTheme.typography.titleSmall,
          color = TextPrimaryDark,
          fontWeight = FontWeight.Bold,
          fontSize = 12.sp
        )
        Text(
          text = "Tagged with verified evidence and target industry relevance",
          style = MaterialTheme.typography.bodySmall,
          color = TextSecondaryDark,
          fontSize = 10.sp
        )
      }

      Button(
        onClick = {
          viewModel.competencyUnderEdit.value = null
          viewModel.showAddCompetencyDialog.value = true
        },
        colors = ButtonDefaults.buttonColors(containerColor = TitanCyan),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
          .height(34.dp)
          .testTag("add_competency_button")
      ) {
        Icon(Icons.Default.Add, contentDescription = null, tint = ObsidianDark, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text("Add Competency", color = ObsidianDark, fontWeight = FontWeight.Bold, fontSize = 11.sp)
      }
    }

    // 5. Competency Cards List
    if (filteredCompetencies.isEmpty()) {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = SlateCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Icon(Icons.Default.Tag, contentDescription = null, tint = TextMutedDark, modifier = Modifier.size(32.dp))
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = "No competencies tagged for $selectedIndustryFilter yet",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondaryDark
          )
          Spacer(modifier = Modifier.height(8.dp))
          OutlinedButton(
            onClick = {
              viewModel.competencyUnderEdit.value = null
              viewModel.showAddCompetencyDialog.value = true
            },
            border = androidx.compose.foundation.BorderStroke(1.dp, TitanCyan.copy(alpha = 0.5f))
          ) {
            Text("Tag a Competency to this Industry", color = TitanCyan, fontSize = 11.sp)
          }
        }
      }
    } else {
      filteredCompetencies.forEach { comp ->
        CoreCompetencyCard(
          competency = comp,
          onEdit = {
            viewModel.competencyUnderEdit.value = comp
            viewModel.showAddCompetencyDialog.value = true
          },
          onDelete = { viewModel.deleteCoreCompetency(comp.id) },
          onToggleIndustry = { industry -> viewModel.toggleIndustryTagForCompetency(comp.id, industry) }
        )
      }
    }
  }

  // Dialog for Adding / Editing Competency
  if (showAddDialog) {
    AddEditCompetencyDialog(
      initialCompetency = competencyUnderEdit,
      onDismiss = { viewModel.showAddCompetencyDialog.value = false },
      onSave = { name, category, proficiency, industries, evidence, years ->
        if (competencyUnderEdit != null) {
          viewModel.updateCoreCompetency(
            competencyUnderEdit!!.copy(
              name = name,
              category = category,
              proficiency = proficiency,
              targetIndustries = industries,
              evidenceOrProofOfWork = evidence,
              yearsOfExperience = years,
              lastAssessedTimestamp = System.currentTimeMillis()
            )
          )
        } else {
          viewModel.addCoreCompetency(
            name = name,
            category = category,
            proficiency = proficiency,
            targetIndustries = industries,
            evidence = evidence,
            years = years
          )
        }
        viewModel.showAddCompetencyDialog.value = false
      }
    )
  }
}

@Composable
fun SkillMappingExecutiveMetricsCard(
  totalCompetencies: Int,
  coveredIndustries: Int,
  marketAlignmentScore: Int,
  criticalGapsCount: Int
) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = SlateCard),
    border = androidx.compose.foundation.BorderStroke(1.dp, TitanCyan.copy(alpha = 0.35f))
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(28.dp)
              .clip(CircleShape)
              .background(TitanCyan.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
          ) {
            Icon(Icons.Default.Hub, contentDescription = null, tint = TitanCyan, modifier = Modifier.size(16.dp))
          }
          Spacer(modifier = Modifier.width(8.dp))
          Column {
            Text(
              "SKILL-TO-INDUSTRY MATRIX",
              style = MaterialTheme.typography.labelSmall,
              color = TitanCyan,
              fontWeight = FontWeight.Bold,
              fontSize = 9.sp
            )
            Text(
              "Core Competency Mapping & 2026 Gap Intel",
              style = MaterialTheme.typography.titleMedium,
              color = TextPrimaryDark,
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp
            )
          }
        }

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(TitanEmerald.copy(alpha = 0.15f))
            .border(1.dp, TitanEmerald.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Verified, contentDescription = null, tint = TitanEmerald, modifier = Modifier.size(11.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("$marketAlignmentScore% Match", color = TitanEmerald, fontWeight = FontWeight.Bold, fontSize = 10.sp)
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))
      HorizontalDivider(color = SlateBorder.copy(alpha = 0.6f))
      Spacer(modifier = Modifier.height(10.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        MetricPill(title = "MAPPED SKILLS", value = "$totalCompetencies", highlightColor = TitanCyan)
        MetricPill(title = "INDUSTRIES", value = "$coveredIndustries", highlightColor = TitanGold)
        MetricPill(title = "ALIGNMENT", value = "$marketAlignmentScore%", highlightColor = TitanEmerald)
        MetricPill(
          title = "CRITICAL GAPS",
          value = "$criticalGapsCount",
          highlightColor = if (criticalGapsCount > 0) TitanCrimson else TitanEmerald
        )
      }
    }
  }
}

@Composable
private fun MetricPill(
  title: String,
  value: String,
  highlightColor: Color
) {
  Column(horizontalAlignment = Alignment.CenterHorizontally) {
    Text(title, style = MaterialTheme.typography.labelSmall, color = TextSecondaryDark, fontSize = 8.sp, fontWeight = FontWeight.Bold)
    Spacer(modifier = Modifier.height(2.dp))
    Text(value, style = MaterialTheme.typography.titleMedium, color = highlightColor, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GeminiMarketGapAnalyzerSection(
  isAnalyzing: Boolean,
  statusMessage: String?,
  selectedIndustries: Set<String>,
  report: SkillGapAnalysisReport?,
  onToggleIndustryForAnalysis: (String) -> Unit,
  onRunAnalysis: () -> Unit,
  onAdoptGap: (IndustrySkillGap) -> Unit
) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = SlateElevated),
    border = androidx.compose.foundation.BorderStroke(1.dp, TitanGold.copy(alpha = 0.45f))
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = TitanGold, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            "GEMINI 2026 MARKET GAP AUDIT",
            style = MaterialTheme.typography.labelSmall,
            color = TitanGold,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp
          )
        }

        Text(
          report?.modelUsed ?: "gemini-3.5-flash",
          style = MaterialTheme.typography.labelSmall,
          color = TextSecondaryDark,
          fontSize = 9.sp
        )
      }

      Spacer(modifier = Modifier.height(4.dp))
      Text(
        "Benchmarking your mapped competencies against live hiring shifts across Bengaluru & global tech hubs.",
        style = MaterialTheme.typography.bodySmall,
        color = TextSecondaryDark,
        fontSize = 11.sp
      )

      Spacer(modifier = Modifier.height(10.dp))
      Text(
        "Target Industries for Market Gap Audit:",
        style = MaterialTheme.typography.labelSmall,
        color = TextPrimaryDark,
        fontWeight = FontWeight.SemiBold,
        fontSize = 10.sp
      )
      Spacer(modifier = Modifier.height(6.dp))

      // Industry selector chips
      FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        TargetIndustryCatalog.ALL_INDUSTRIES.take(5).forEach { ind ->
          val isSelected = selectedIndustries.contains(ind)
          val colorInt = TargetIndustryCatalog.INDUSTRY_COLORS[ind] ?: 0xFF00E5FF
          val indColor = Color(colorInt)

          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(if (isSelected) indColor.copy(alpha = 0.2f) else SlateDarker)
              .border(1.dp, if (isSelected) indColor else SlateBorder, RoundedCornerShape(6.dp))
              .clickable { onToggleIndustryForAnalysis(ind) }
              .padding(horizontal = 8.dp, vertical = 4.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              if (isSelected) {
                Icon(Icons.Default.Check, contentDescription = null, tint = indColor, modifier = Modifier.size(10.dp))
                Spacer(modifier = Modifier.width(4.dp))
              }
              Text(
                ind,
                color = if (isSelected) indColor else TextSecondaryDark,
                fontSize = 10.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Run Analysis Button
      Button(
        onClick = onRunAnalysis,
        enabled = !isAnalyzing,
        modifier = Modifier
          .fillMaxWidth()
          .height(40.dp)
          .testTag("run_skill_gap_analysis_button"),
        colors = ButtonDefaults.buttonColors(containerColor = TitanGold),
        shape = RoundedCornerShape(8.dp)
      ) {
        if (isAnalyzing) {
          CircularProgressIndicator(modifier = Modifier.size(16.dp), color = ObsidianDark, strokeWidth = 2.dp)
          Spacer(modifier = Modifier.width(8.dp))
          Text("Analyzing 2026 Trends...", color = ObsidianDark, fontWeight = FontWeight.Bold, fontSize = 11.sp)
        } else {
          Icon(Icons.Default.Psychology, contentDescription = null, tint = ObsidianDark, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Analyze Skill Gaps with Gemini", color = ObsidianDark, fontWeight = FontWeight.Bold, fontSize = 11.sp)
        }
      }

      if (!statusMessage.isNullOrBlank() && isAnalyzing) {
        Spacer(modifier = Modifier.height(6.dp))
        Text(statusMessage, style = MaterialTheme.typography.bodySmall, color = TitanGold, fontSize = 10.sp)
      }

      // Display Gap Report Results if present
      if (report != null && report.gaps.isNotEmpty()) {
        Spacer(modifier = Modifier.height(14.dp))
        HorizontalDivider(color = SlateBorder.copy(alpha = 0.5f))
        Spacer(modifier = Modifier.height(10.dp))

        // Macro summary box
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(SlateDarker)
            .border(1.dp, TitanCyan.copy(alpha = 0.25f), RoundedCornerShape(8.dp))
            .padding(10.dp)
        ) {
          Row(verticalAlignment = Alignment.Top) {
            Icon(Icons.Default.Lightbulb, contentDescription = null, tint = TitanCyan, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Column {
              Text("2026 HIRING MACRO DYNAMICS", style = MaterialTheme.typography.labelSmall, color = TitanCyan, fontWeight = FontWeight.Bold, fontSize = 9.sp)
              Spacer(modifier = Modifier.height(2.dp))
              Text(report.marketMacroSummary, style = MaterialTheme.typography.bodySmall, color = TextPrimaryDark, fontSize = 10.sp, lineHeight = 14.sp)
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))
        Text(
          "HIGH-DEMAND SKILL GAPS IDENTIFIED (${report.gaps.size}):",
          style = MaterialTheme.typography.labelSmall,
          color = TitanGold,
          fontWeight = FontWeight.Bold,
          fontSize = 10.sp
        )
        Spacer(modifier = Modifier.height(6.dp))

        report.gaps.forEach { gap ->
          IndustrySkillGapCard(
            gap = gap,
            onAdopt = { onAdoptGap(gap) }
          )
          Spacer(modifier = Modifier.height(8.dp))
        }
      }
    }
  }
}

@Composable
fun IndustrySkillGapCard(
  gap: IndustrySkillGap,
  onAdopt: () -> Unit
) {
  val severityColor = when (gap.gapSeverity) {
    "CRITICAL_GAP" -> TitanCrimson
    "HIGH_PRIORITY" -> TitanGold
    else -> TitanCyan
  }

  val severityLabel = when (gap.gapSeverity) {
    "CRITICAL_GAP" -> "CRITICAL MARKET GAP"
    "HIGH_PRIORITY" -> "HIGH PRIORITY TREND"
    else -> "EMERGING ADVANTAGE"
  }

  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(8.dp),
    colors = CardDefaults.cardColors(containerColor = SlateDarker),
    border = androidx.compose.foundation.BorderStroke(1.dp, severityColor.copy(alpha = 0.4f))
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
              .clip(RoundedCornerShape(4.dp))
              .background(severityColor.copy(alpha = 0.2f))
              .padding(horizontal = 6.dp, vertical = 2.dp)
          ) {
            Text(severityLabel, color = severityColor, fontWeight = FontWeight.Bold, fontSize = 8.sp)
          }

          Spacer(modifier = Modifier.width(6.dp))
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(4.dp))
              .background(SlateCard)
              .padding(horizontal = 6.dp, vertical = 2.dp)
          ) {
            Text(gap.targetIndustry, color = TextSecondaryDark, fontSize = 8.sp)
          }
        }

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(TitanEmerald.copy(alpha = 0.15f))
            .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
          Text(gap.estimatedSalaryDelta, color = TitanEmerald, fontWeight = FontWeight.Bold, fontSize = 9.sp)
        }
      }

      Spacer(modifier = Modifier.height(6.dp))
      Text(
        text = gap.skillName,
        style = MaterialTheme.typography.titleSmall,
        color = TextPrimaryDark,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp
      )

      Spacer(modifier = Modifier.height(4.dp))
      Row(verticalAlignment = Alignment.Top) {
        Icon(Icons.AutoMirrored.Filled.TrendingUp, contentDescription = null, tint = TitanGold, modifier = Modifier.size(12.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(
          text = gap.currentMarketTrend,
          style = MaterialTheme.typography.bodySmall,
          color = TextSecondaryDark,
          fontSize = 10.sp,
          lineHeight = 14.sp
        )
      }

      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = "Action to close: ${gap.recommendedAction}",
        style = MaterialTheme.typography.bodySmall,
        color = TitanCyan,
        fontSize = 9.sp,
        fontWeight = FontWeight.Medium
      )

      Spacer(modifier = Modifier.height(8.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
      ) {
        if (gap.isAddedToMyCompetencies) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(TitanEmerald.copy(alpha = 0.2f))
              .padding(horizontal = 8.dp, vertical = 4.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.CheckCircle, contentDescription = null, tint = TitanEmerald, modifier = Modifier.size(12.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Added to Competencies", color = TitanEmerald, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
          }
        } else {
          OutlinedButton(
            onClick = onAdopt,
            modifier = Modifier.height(28.dp),
            shape = RoundedCornerShape(6.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, TitanCyan.copy(alpha = 0.6f))
          ) {
            Icon(Icons.Default.Add, contentDescription = null, tint = TitanCyan, modifier = Modifier.size(12.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Add to My Competencies", color = TitanCyan, fontSize = 9.sp, fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}

@Composable
fun TargetIndustryFilterBar(
  selectedFilter: String,
  onSelectFilter: (String) -> Unit,
  competencies: List<CoreCompetency>
) {
  val scrollState = rememberScrollState()

  Row(
    modifier = Modifier
      .fillMaxWidth()
      .horizontalScroll(scrollState),
    horizontalArrangement = Arrangement.spacedBy(6.dp)
  ) {
    // "ALL" chip
    val isAllSelected = selectedFilter == "ALL"
    Box(
      modifier = Modifier
        .clip(RoundedCornerShape(8.dp))
        .background(if (isAllSelected) TitanCyan else SlateCard)
        .clickable { onSelectFilter("ALL") }
        .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
      Text(
        text = "All Industries (${competencies.size})",
        color = if (isAllSelected) ObsidianDark else TextSecondaryDark,
        fontWeight = if (isAllSelected) FontWeight.Bold else FontWeight.Normal,
        fontSize = 11.sp
      )
    }

    TargetIndustryCatalog.ALL_INDUSTRIES.forEach { industry ->
      val count = competencies.count { it.targetIndustries.any { t -> t.equals(industry, ignoreCase = true) } }
      val isSelected = selectedFilter.equals(industry, ignoreCase = true)
      val colorInt = TargetIndustryCatalog.INDUSTRY_COLORS[industry] ?: 0xFF00E5FF
      val indColor = Color(colorInt)

      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(8.dp))
          .background(if (isSelected) indColor.copy(alpha = 0.25f) else SlateCard)
          .border(1.dp, if (isSelected) indColor else SlateBorder, RoundedCornerShape(8.dp))
          .clickable { onSelectFilter(industry) }
          .padding(horizontal = 10.dp, vertical = 6.dp)
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(6.dp)
              .clip(CircleShape)
              .background(indColor)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "$industry ($count)",
            color = if (isSelected) indColor else TextPrimaryDark,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            fontSize = 11.sp
          )
        }
      }
    }
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CoreCompetencyCard(
  competency: CoreCompetency,
  onEdit: () -> Unit,
  onDelete: () -> Unit,
  onToggleIndustry: (String) -> Unit
) {
  var showIndustryPicker by remember { mutableStateOf(false) }

  val proficiencyColor = when (competency.proficiency.uppercase()) {
    "EXPERT" -> TitanEmerald
    "ADVANCED" -> TitanCyan
    "INTERMEDIATE" -> TitanGold
    else -> TextSecondaryDark
  }

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("competency_card_${competency.id}"),
    shape = RoundedCornerShape(10.dp),
    colors = CardDefaults.cardColors(containerColor = SlateCard),
    border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
  ) {
    Column(modifier = Modifier.padding(12.dp)) {
      // Top row: Category, Proficiency & Actions
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(4.dp))
              .background(SlateElevated)
              .padding(horizontal = 6.dp, vertical = 2.dp)
          ) {
            Text(
              competency.category.replace("_", " "),
              color = TitanCyan,
              fontWeight = FontWeight.Bold,
              fontSize = 8.sp
            )
          }

          Spacer(modifier = Modifier.width(6.dp))
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(4.dp))
              .background(proficiencyColor.copy(alpha = 0.15f))
              .padding(horizontal = 6.dp, vertical = 2.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(5.dp)
                  .clip(CircleShape)
                  .background(proficiencyColor)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(competency.proficiency, color = proficiencyColor, fontWeight = FontWeight.Bold, fontSize = 8.sp)
            }
          }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          IconButton(onClick = onEdit, modifier = Modifier.size(24.dp)) {
            Icon(Icons.Default.Edit, contentDescription = "Edit Competency", tint = TextSecondaryDark, modifier = Modifier.size(14.dp))
          }
          IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
            Icon(Icons.Default.Delete, contentDescription = "Delete Competency", tint = TitanCrimson.copy(alpha = 0.7f), modifier = Modifier.size(14.dp))
          }
        }
      }

      Spacer(modifier = Modifier.height(6.dp))

      // Competency Name
      Text(
        text = competency.name,
        style = MaterialTheme.typography.titleMedium,
        color = TextPrimaryDark,
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp
      )

      // Evidence or notes
      if (competency.evidenceOrProofOfWork.isNotBlank()) {
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = "Evidence: ${competency.evidenceOrProofOfWork}",
          style = MaterialTheme.typography.bodySmall,
          color = TextSecondaryDark,
          fontSize = 10.sp,
          lineHeight = 14.sp
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Tagged Industries Section
      Text(
        text = "TAGGED TARGET INDUSTRIES (${competency.targetIndustries.size}):",
        style = MaterialTheme.typography.labelSmall,
        color = TextMutedDark,
        fontSize = 8.sp,
        fontWeight = FontWeight.Bold
      )
      Spacer(modifier = Modifier.height(4.dp))

      FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        competency.targetIndustries.forEach { industry ->
          val colorInt = TargetIndustryCatalog.INDUSTRY_COLORS[industry] ?: 0xFF00E5FF
          val indColor = Color(colorInt)

          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(4.dp))
              .background(indColor.copy(alpha = 0.15f))
              .border(1.dp, indColor.copy(alpha = 0.35f), RoundedCornerShape(4.dp))
              .padding(horizontal = 6.dp, vertical = 2.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(industry, color = indColor, fontSize = 9.sp, fontWeight = FontWeight.Medium)
              Spacer(modifier = Modifier.width(4.dp))
              Icon(
                Icons.Default.Close,
                contentDescription = "Untag $industry",
                tint = indColor.copy(alpha = 0.7f),
                modifier = Modifier
                  .size(10.dp)
                  .clickable { onToggleIndustry(industry) }
              )
            }
          }
        }

        // Quick Tag Plus button
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(SlateElevated)
            .border(1.dp, SlateBorder, RoundedCornerShape(4.dp))
            .clickable { showIndustryPicker = !showIndustryPicker }
            .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Add, contentDescription = "Add Tag", tint = TitanCyan, modifier = Modifier.size(10.dp))
            Spacer(modifier = Modifier.width(2.dp))
            Text("Tag Sector", color = TitanCyan, fontSize = 9.sp, fontWeight = FontWeight.Bold)
          }
        }
      }

      // Quick Industry Dropdown / Picker Drawer
      AnimatedVisibility(visible = showIndustryPicker) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(SlateDarker)
            .padding(8.dp)
        ) {
          Text("Select industry to tag/untag:", style = MaterialTheme.typography.labelSmall, color = TextSecondaryDark, fontSize = 9.sp)
          Spacer(modifier = Modifier.height(6.dp))
          FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            TargetIndustryCatalog.ALL_INDUSTRIES.forEach { ind ->
              val isTagged = competency.targetIndustries.contains(ind)
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(4.dp))
                  .background(if (isTagged) TitanCyan.copy(alpha = 0.2f) else SlateCard)
                  .clickable { onToggleIndustry(ind) }
                  .padding(horizontal = 6.dp, vertical = 3.dp)
              ) {
                Text(
                  text = if (isTagged) "✓ $ind" else "+ $ind",
                  color = if (isTagged) TitanCyan else TextPrimaryDark,
                  fontSize = 9.sp
                )
              }
            }
          }
        }
      }
    }
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddEditCompetencyDialog(
  initialCompetency: CoreCompetency?,
  onDismiss: () -> Unit,
  onSave: (name: String, category: String, proficiency: String, industries: List<String>, evidence: String, years: Double) -> Unit
) {
  var name by remember { mutableStateOf(initialCompetency?.name ?: "") }
  var category by remember { mutableStateOf(initialCompetency?.category ?: "STRATEGY_OPERATIONS") }
  var proficiency by remember { mutableStateOf(initialCompetency?.proficiency ?: "ADVANCED") }
  var evidence by remember { mutableStateOf(initialCompetency?.evidenceOrProofOfWork ?: "") }
  var selectedIndustries by remember {
    mutableStateOf(initialCompetency?.targetIndustries?.toSet() ?: setOf("Enterprise Cloud & SaaS"))
  }

  val categories = listOf(
    "STRATEGY_OPERATIONS" to "Strategy & Ops",
    "ANALYTICS_DATA" to "Analytics & Data",
    "AI_AUTOMATION" to "AI & Automation",
    "COMMERCIAL_FINANCE" to "Finance & Commercial",
    "PRODUCT_TECH" to "Product & Tech"
  )

  val proficiencies = listOf("BEGINNER", "INTERMEDIATE", "ADVANCED", "EXPERT")

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Text(
        text = if (initialCompetency != null) "Edit Core Competency" else "Add Core Competency",
        color = TextPrimaryDark,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp
      )
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        // Presets row for fast entry
        if (initialCompetency == null) {
          Text("Quick Presets:", style = MaterialTheme.typography.labelSmall, color = TitanGold, fontSize = 9.sp)
          FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            listOf(
              "SQL Data Pipelines" to "ANALYTICS_DATA",
              "Agentic LLM Workflows" to "AI_AUTOMATION",
              "Unit Economics & LTV" to "COMMERCIAL_FINANCE",
              "Dark Store Fulfillment" to "STRATEGY_OPERATIONS"
            ).forEach { (presetName, presetCat) ->
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(4.dp))
                  .background(SlateElevated)
                  .clickable {
                    name = presetName
                    category = presetCat
                  }
                  .padding(horizontal = 6.dp, vertical = 2.dp)
              ) {
                Text(presetName, color = TitanCyan, fontSize = 9.sp)
              }
            }
          }
        }

        // Competency Name
        OutlinedTextField(
          value = name,
          onValueChange = { name = it },
          label = { Text("Competency Name", fontSize = 11.sp) },
          placeholder = { Text("e.g. SQL Data Pipelines, Cohort Modeling", fontSize = 11.sp) },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("competency_name_input"),
          singleLine = true,
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = TitanCyan,
            unfocusedBorderColor = SlateBorder,
            focusedTextColor = TextPrimaryDark,
            unfocusedTextColor = TextPrimaryDark
          )
        )

        // Category Selection
        Text("Category:", style = MaterialTheme.typography.labelSmall, color = TextSecondaryDark, fontSize = 10.sp)
        FlowRow(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(4.dp),
          verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          categories.forEach { (catKey, catLabel) ->
            val isSelected = category == catKey
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(if (isSelected) TitanCyan.copy(alpha = 0.2f) else SlateCard)
                .border(1.dp, if (isSelected) TitanCyan else SlateBorder, RoundedCornerShape(4.dp))
                .clickable { category = catKey }
                .padding(horizontal = 6.dp, vertical = 3.dp)
            ) {
              Text(
                catLabel,
                color = if (isSelected) TitanCyan else TextSecondaryDark,
                fontSize = 9.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
              )
            }
          }
        }

        // Proficiency Selection
        Text("Proficiency Level:", style = MaterialTheme.typography.labelSmall, color = TextSecondaryDark, fontSize = 10.sp)
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          proficiencies.forEach { prof ->
            val isSelected = proficiency == prof
            Box(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(4.dp))
                .background(if (isSelected) TitanEmerald.copy(alpha = 0.2f) else SlateCard)
                .border(1.dp, if (isSelected) TitanEmerald else SlateBorder, RoundedCornerShape(4.dp))
                .clickable { proficiency = prof }
                .padding(vertical = 4.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(
                prof.take(3),
                color = if (isSelected) TitanEmerald else TextSecondaryDark,
                fontSize = 9.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
              )
            }
          }
        }

        // Target Industry Multi-Tagging
        Text("Tag Target Industries:", style = MaterialTheme.typography.labelSmall, color = TextSecondaryDark, fontSize = 10.sp)
        FlowRow(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(4.dp),
          verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          TargetIndustryCatalog.ALL_INDUSTRIES.forEach { ind ->
            val isTagged = selectedIndustries.contains(ind)
            val colorInt = TargetIndustryCatalog.INDUSTRY_COLORS[ind] ?: 0xFF00E5FF
            val indColor = Color(colorInt)

            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(if (isTagged) indColor.copy(alpha = 0.2f) else SlateCard)
                .border(1.dp, if (isTagged) indColor else SlateBorder, RoundedCornerShape(4.dp))
                .clickable {
                  selectedIndustries = if (isTagged) {
                    selectedIndustries - ind
                  } else {
                    selectedIndustries + ind
                  }
                }
                .padding(horizontal = 6.dp, vertical = 3.dp)
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                if (isTagged) {
                  Icon(Icons.Default.Check, contentDescription = null, tint = indColor, modifier = Modifier.size(9.dp))
                  Spacer(modifier = Modifier.width(3.dp))
                }
                Text(
                  ind,
                  color = if (isTagged) indColor else TextSecondaryDark,
                  fontSize = 9.sp,
                  fontWeight = if (isTagged) FontWeight.Bold else FontWeight.Normal
                )
              }
            }
          }
        }

        // Evidence / Proof of work
        OutlinedTextField(
          value = evidence,
          onValueChange = { evidence = it },
          label = { Text("Evidence / Verified Metric", fontSize = 11.sp) },
          placeholder = { Text("e.g. 42% cycle reduction in family enterprise", fontSize = 11.sp) },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("competency_evidence_input"),
          maxLines = 2,
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = TitanCyan,
            unfocusedBorderColor = SlateBorder,
            focusedTextColor = TextPrimaryDark,
            unfocusedTextColor = TextPrimaryDark
          )
        )
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (name.isNotBlank()) {
            onSave(name, category, proficiency, selectedIndustries.toList(), evidence, 1.5)
          }
        },
        enabled = name.isNotBlank(),
        colors = ButtonDefaults.buttonColors(containerColor = TitanCyan),
        modifier = Modifier.testTag("save_competency_button")
      ) {
        Text("Save Competency", color = ObsidianDark, fontWeight = FontWeight.Bold, fontSize = 11.sp)
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancel", color = TextSecondaryDark, fontSize = 11.sp)
      }
    },
    containerColor = SlateDarker
  )
}
