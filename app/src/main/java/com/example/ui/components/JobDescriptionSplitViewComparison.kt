package com.example.ui.components

import android.widget.Toast
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
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.ViewColumn
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CandidateMatchStatus
import com.example.data.model.ComparisonDimensionBreakdown
import com.example.data.model.JobActionPlan
import com.example.data.model.JobSplitComparisonReport
import com.example.data.model.QuickComparisonPresetPair
import com.example.data.model.ResumeJdMatchResult
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

/**
 * High-craft Split-View Mode UI for comparing match scores of two different
 * Job Descriptions side-by-side with head-to-head score rings, comparative dimension bars,
 * side-by-side skill and requirement audits, and executive strategic trade-off verdicts.
 */
@Composable
fun JobDescriptionSplitViewComparison(
  viewModel: TitanViewModel,
  modifier: Modifier = Modifier,
  onCloseSplitView: () -> Unit = {}
) {
  val context = LocalContext.current
  val clipboardManager = LocalClipboardManager.current

  val isComparing by viewModel.isComparingSplitViewJds.collectAsState()
  val comparisonReport by viewModel.splitComparisonReport.collectAsState()

  val jdTextA by viewModel.splitViewJdTextA.collectAsState()
  val companyA by viewModel.splitViewCompanyA.collectAsState()
  val roleA by viewModel.splitViewRoleA.collectAsState()
  val planA by viewModel.splitViewPlanA.collectAsState()
  val matchA by viewModel.splitViewMatchA.collectAsState()

  val jdTextB by viewModel.splitViewJdTextB.collectAsState()
  val companyB by viewModel.splitViewCompanyB.collectAsState()
  val roleB by viewModel.splitViewRoleB.collectAsState()
  val planB by viewModel.splitViewPlanB.collectAsState()
  val matchB by viewModel.splitViewMatchB.collectAsState()

  val presets = viewModel.availablePresetComparisonPairs

  var showRawJdEditorA by remember { mutableStateOf(false) }
  var showRawJdEditorB by remember { mutableStateOf(false) }
  var viewModeTab by remember { mutableStateOf(0) } // 0: Side-by-Side Dual Columns, 1: Job A Focus, 2: Job B Focus, 3: Head-to-Head Matrix

  Column(
    modifier = modifier
      .fillMaxWidth()
      .testTag("job_description_split_view_comparison_container")
  ) {
    // 1. SPLIT-VIEW HERO BANNER & CONTROL BAR
    SplitViewTopControlBar(
      onCloseSplitView = onCloseSplitView,
      onSwapJobs = {
        viewModel.swapSplitViewJds()
        Toast.makeText(context, "Swapped Job A and Job B", Toast.LENGTH_SHORT).show()
      },
      onRecompare = {
        viewModel.runSplitViewComparison {
          Toast.makeText(context, "Split-view match comparison updated!", Toast.LENGTH_SHORT).show()
        }
      },
      isComparing = isComparing
    )

    Spacer(modifier = Modifier.height(10.dp))

    // 2. QUICK PRESET COMPARISON CHIPS
    PresetPairsRow(
      presets = presets,
      currentCompanyA = companyA,
      currentCompanyB = companyB,
      onSelectPreset = { presetId ->
        viewModel.loadSplitViewPresetPair(presetId)
      }
    )

    Spacer(modifier = Modifier.height(14.dp))

    // 3. HEAD-TO-HEAD MATCH SCORE HERO CARD
    comparisonReport?.let { report ->
      HeadToHeadScoreHeroCard(
        report = report,
        isComparing = isComparing,
        onCopyReport = {
          val summary = buildString {
            appendLine("=== TITAN AI: JOB DESCRIPTION SPLIT-VIEW MATCH COMPARISON ===")
            appendLine("Job A: ${report.jobA.targetCompany} - ${report.jobA.targetRole} | Score: ${report.scoreA}%")
            appendLine("Job B: ${report.jobB.targetCompany} - ${report.jobB.targetRole} | Score: ${report.scoreB}%")
            appendLine("Match Delta: ${report.winningMarginSummary}")
            appendLine("Leading Target: ${report.recommendedPrimaryTarget}")
            appendLine()
            appendLine("DIMENSION BREAKDOWN:")
            report.dimensions.forEach { dim ->
              appendLine("• ${dim.dimensionTitle}: Job A (${dim.scoreA}%) vs Job B (${dim.scoreB}%) -> ${dim.insightText}")
            }
            appendLine()
            appendLine("STRATEGIC VERDICT:")
            appendLine(report.strategicTakeaway)
          }
          clipboardManager.setText(AnnotatedString(summary))
          Toast.makeText(context, "Comparison report copied to clipboard!", Toast.LENGTH_SHORT).show()
        }
      )

      Spacer(modifier = Modifier.height(14.dp))

      // 4. DIMENSION-BY-DIMENSION COMPARATIVE BARS
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("split_view_dimensions_card"),
        colors = CardDefaults.cardColors(containerColor = SlateCard),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.ViewColumn,
                contentDescription = null,
                tint = TitanCyan,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "MATCH SCORE DIMENSION COMPARISON",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = TitanCyan,
                letterSpacing = 0.5.sp
              )
            }

            Text(
              text = "${report.jobA.targetCompany} vs ${report.jobB.targetCompany}",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = TextSecondaryDark
            )
          }

          Spacer(modifier = Modifier.height(12.dp))

          report.dimensions.forEachIndexed { index, dim ->
            HeadToHeadDimensionBar(
              dimension = dim,
              companyA = report.jobA.targetCompany,
              companyB = report.jobB.targetCompany
            )
            if (index < report.dimensions.size - 1) {
              Spacer(modifier = Modifier.height(12.dp))
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // 5. VIEW MODE SWITCHER TABS
      TabRow(
        selectedTabIndex = viewModeTab,
        containerColor = SlateElevated,
        contentColor = TitanCyan,
        indicator = { tabPositions ->
          TabRowDefaults.SecondaryIndicator(
            modifier = Modifier.tabIndicatorOffset(tabPositions[viewModeTab]),
            color = TitanCyan
          )
        },
        modifier = Modifier
          .clip(RoundedCornerShape(10.dp))
          .border(1.dp, SlateBorder, RoundedCornerShape(10.dp))
      ) {
        Tab(
          selected = viewModeTab == 0,
          onClick = { viewModeTab = 0 },
          text = { Text("◫ Side-by-Side Dual Columns", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
          modifier = Modifier.testTag("tab_split_view_dual_columns")
        )
        Tab(
          selected = viewModeTab == 1,
          onClick = { viewModeTab = 1 },
          text = { Text("⚡ ${report.jobA.targetCompany}", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
          modifier = Modifier.testTag("tab_split_view_job_a")
        )
        Tab(
          selected = viewModeTab == 2,
          onClick = { viewModeTab = 2 },
          text = { Text("🏢 ${report.jobB.targetCompany}", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
          modifier = Modifier.testTag("tab_split_view_job_b")
        )
      }

      Spacer(modifier = Modifier.height(12.dp))

      // 6. SIDE-BY-SIDE PANELS OR TABBED VIEW
      when (viewModeTab) {
        0 -> {
          // Dual Columns Side-by-Side Layout
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .height(IntrinsicSize.Min),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            // Left Column: Job A
            Box(modifier = Modifier.weight(1f)) {
              JobComparisonColumnCard(
                label = "JOB A (PRIMARY)",
                job = report.jobA,
                match = report.matchA,
                isWinner = report.scoreA >= report.scoreB,
                primaryColor = TitanCyan,
                showRawEditor = showRawJdEditorA,
                rawJdText = jdTextA,
                onToggleRawEditor = { showRawJdEditorA = !showRawJdEditorA },
                onRawTextChanged = { newText ->
                  viewModel.setSplitViewJdTextA(newText)
                },
                onAdoptAsPrimary = {
                  viewModel.applySplitViewJobAsPrimary(isJobA = true)
                  Toast.makeText(context, "${report.jobA.targetCompany} set as primary active target!", Toast.LENGTH_SHORT).show()
                },
                testTagPrefix = "job_a"
              )
            }

            // Right Column: Job B
            Box(modifier = Modifier.weight(1f)) {
              JobComparisonColumnCard(
                label = "JOB B (COMPARISON)",
                job = report.jobB,
                match = report.matchB,
                isWinner = report.scoreB > report.scoreA,
                primaryColor = TitanGold,
                showRawEditor = showRawJdEditorB,
                rawJdText = jdTextB,
                onToggleRawEditor = { showRawJdEditorB = !showRawJdEditorB },
                onRawTextChanged = { newText ->
                  viewModel.setSplitViewJdTextB(newText)
                },
                onAdoptAsPrimary = {
                  viewModel.applySplitViewJobAsPrimary(isJobA = false)
                  Toast.makeText(context, "${report.jobB.targetCompany} set as primary active target!", Toast.LENGTH_SHORT).show()
                },
                testTagPrefix = "job_b"
              )
            }
          }
        }
        1 -> {
          // Full Focus on Job A
          JobComparisonColumnCard(
            label = "JOB A • DETAILED AUDIT",
            job = report.jobA,
            match = report.matchA,
            isWinner = report.scoreA >= report.scoreB,
            primaryColor = TitanCyan,
            showRawEditor = showRawJdEditorA,
            rawJdText = jdTextA,
            onToggleRawEditor = { showRawJdEditorA = !showRawJdEditorA },
            onRawTextChanged = { newText -> viewModel.setSplitViewJdTextA(newText) },
            onAdoptAsPrimary = {
              viewModel.applySplitViewJobAsPrimary(isJobA = true)
              Toast.makeText(context, "${report.jobA.targetCompany} set as primary active target!", Toast.LENGTH_SHORT).show()
            },
            testTagPrefix = "job_a_focused"
          )
        }
        2 -> {
          // Full Focus on Job B
          JobComparisonColumnCard(
            label = "JOB B • DETAILED AUDIT",
            job = report.jobB,
            match = report.matchB,
            isWinner = report.scoreB > report.scoreA,
            primaryColor = TitanGold,
            showRawEditor = showRawJdEditorB,
            rawJdText = jdTextB,
            onToggleRawEditor = { showRawJdEditorB = !showRawJdEditorB },
            onRawTextChanged = { newText -> viewModel.setSplitViewJdTextB(newText) },
            onAdoptAsPrimary = {
              viewModel.applySplitViewJobAsPrimary(isJobA = false)
              Toast.makeText(context, "${report.jobB.targetCompany} set as primary active target!", Toast.LENGTH_SHORT).show()
            },
            testTagPrefix = "job_b_focused"
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // 7. STRATEGIC EXECUTIVE VERDICT CARD
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("split_view_strategic_verdict_card"),
        colors = CardDefaults.cardColors(containerColor = SlateElevated),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, TitanIndigo.copy(alpha = 0.5f))
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
          ) {
            Box(
              modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(TitanGold.copy(alpha = 0.15f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Lightbulb,
                contentDescription = null,
                tint = TitanGold,
                modifier = Modifier.size(16.dp)
              )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text(
                text = "EXECUTIVE COMPARISON VERDICT",
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                color = TitanGold,
                letterSpacing = 0.5.sp
              )
              Text(
                text = "Primary High-Conviction Target: ${report.recommendedPrimaryTarget}",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimaryDark
              )
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          Text(
            text = report.strategicTakeaway,
            fontSize = 12.sp,
            color = TextSecondaryDark,
            lineHeight = 17.sp
          )
        }
      }
    } ?: run {
      // Loading state while generating initial comparison
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("split_view_loading_card"),
        colors = CardDefaults.cardColors(containerColor = SlateCard),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center
        ) {
          CircularProgressIndicator(
            modifier = Modifier.size(32.dp),
            color = TitanCyan,
            strokeWidth = 3.dp
          )
          Spacer(modifier = Modifier.height(12.dp))
          Text(
            text = "Computing Dual Job Match Comparison...",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimaryDark
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "Benchmarking candidate profile across both job descriptions",
            fontSize = 11.sp,
            color = TextMutedDark
          )
        }
      }
    }
  }
}

/**
 * Top control bar with swap, re-run, and exit split-view actions.
 */
@Composable
private fun SplitViewTopControlBar(
  onCloseSplitView: () -> Unit,
  onSwapJobs: () -> Unit,
  onRecompare: () -> Unit,
  isComparing: Boolean
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.weight(1f)
    ) {
      Box(
        modifier = Modifier
          .size(34.dp)
          .clip(CircleShape)
          .background(TitanIndigo.copy(alpha = 0.2f))
          .border(1.dp, TitanIndigo.copy(alpha = 0.5f), CircleShape),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.AutoMirrored.Filled.CompareArrows,
          contentDescription = "Split-View Compare",
          tint = TitanCyan,
          modifier = Modifier.size(18.dp)
        )
      }

      Spacer(modifier = Modifier.width(8.dp))

      Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = "SPLIT-VIEW MODE",
            fontSize = 10.sp,
            fontWeight = FontWeight.Black,
            color = TitanCyan,
            letterSpacing = 1.sp
          )
          Spacer(modifier = Modifier.width(6.dp))
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(4.dp))
              .background(TitanIndigo.copy(alpha = 0.25f))
              .padding(horizontal = 5.dp, vertical = 1.dp)
          ) {
            Text(
              text = "SIDE-BY-SIDE",
              fontSize = 8.sp,
              fontWeight = FontWeight.ExtraBold,
              color = TitanCyan
            )
          }
        }
        Text(
          text = "Dual Job Match Comparison",
          fontSize = 14.sp,
          fontWeight = FontWeight.Bold,
          color = TextPrimaryDark
        )
      }
    }

    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      // Swap A ⇄ B Button
      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(8.dp))
          .background(SlateElevated)
          .border(1.dp, SlateBorder, RoundedCornerShape(8.dp))
          .clickable { onSwapJobs() }
          .padding(horizontal = 8.dp, vertical = 6.dp)
          .testTag("swap_split_view_jobs_button")
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.SwapHoriz,
            contentDescription = "Swap A and B",
            tint = TitanCyan,
            modifier = Modifier.size(14.dp)
          )
          Spacer(modifier = Modifier.width(3.dp))
          Text("Swap", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TitanCyan)
        }
      }

      // Re-compare Trigger Button
      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(8.dp))
          .background(TitanCyan.copy(alpha = 0.15f))
          .border(1.dp, TitanCyan.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
          .clickable(enabled = !isComparing) { onRecompare() }
          .padding(horizontal = 8.dp, vertical = 6.dp)
          .testTag("recompare_split_view_button")
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          if (isComparing) {
            CircularProgressIndicator(modifier = Modifier.size(12.dp), strokeWidth = 1.5.dp, color = TitanCyan)
          } else {
            Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = TitanCyan, modifier = Modifier.size(13.dp))
          }
          Spacer(modifier = Modifier.width(3.dp))
          Text("Compare", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TitanCyan)
        }
      }

      // Exit / Switch to Single View
      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(8.dp))
          .background(SlateElevated)
          .border(1.dp, SlateBorder, RoundedCornerShape(8.dp))
          .clickable { onCloseSplitView() }
          .padding(horizontal = 8.dp, vertical = 6.dp)
          .testTag("exit_split_view_button")
      ) {
        Text("Single View", fontSize = 10.sp, color = TextMutedDark, fontWeight = FontWeight.Medium)
      }
    }
  }
}

/**
 * Row of quick preset pairs for 1-tap side-by-side comparison loading.
 */
@Composable
private fun PresetPairsRow(
  presets: List<QuickComparisonPresetPair>,
  currentCompanyA: String,
  currentCompanyB: String,
  onSelectPreset: (String) -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .horizontalScroll(rememberScrollState()),
    horizontalArrangement = Arrangement.spacedBy(8.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(
      text = "PRESETS:",
      fontSize = 9.sp,
      fontWeight = FontWeight.Black,
      color = TextMutedDark,
      letterSpacing = 0.5.sp
    )

    presets.forEach { pair ->
      val isSelected = (currentCompanyA.contains(pair.companyA, ignoreCase = true) && currentCompanyB.contains(pair.companyB, ignoreCase = true)) ||
        (currentCompanyA.contains(pair.companyB, ignoreCase = true) && currentCompanyB.contains(pair.companyA, ignoreCase = true))

      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(8.dp))
          .background(if (isSelected) TitanCyan.copy(alpha = 0.2f) else SlateElevated)
          .border(
            width = 1.dp,
            color = if (isSelected) TitanCyan else SlateBorder,
            shape = RoundedCornerShape(8.dp)
          )
          .clickable { onSelectPreset(pair.id) }
          .padding(horizontal = 9.dp, vertical = 5.dp)
          .testTag("preset_pair_${pair.id}")
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = pair.label,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) TitanCyan else TextPrimaryDark
          )
          Spacer(modifier = Modifier.width(5.dp))
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(4.dp))
              .background(SlateCard)
              .padding(horizontal = 4.dp, vertical = 1.dp)
          ) {
            Text(
              text = pair.tag,
              fontSize = 8.sp,
              color = TextMutedDark
            )
          }
        }
      }
    }
  }
}

/**
 * Head-to-head score comparison hero card featuring twin radial progress rings,
 * leader crown tag, and differential delta badge.
 */
@Composable
private fun HeadToHeadScoreHeroCard(
  report: JobSplitComparisonReport,
  isComparing: Boolean,
  onCopyReport: () -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("split_view_hero_score_card"),
    colors = CardDefaults.cardColors(containerColor = SlateCard),
    shape = RoundedCornerShape(16.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.EmojiEvents,
            contentDescription = null,
            tint = TitanGold,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "HEAD-TO-HEAD MATCH SCORE",
            fontSize = 11.sp,
            fontWeight = FontWeight.Black,
            color = TitanGold,
            letterSpacing = 0.5.sp
          )
        }

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(SlateElevated)
            .border(1.dp, SlateBorder, RoundedCornerShape(6.dp))
            .clickable { onCopyReport() }
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .testTag("copy_comparison_summary_button")
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = TitanCyan, modifier = Modifier.size(12.dp))
            Spacer(modifier = Modifier.width(3.dp))
            Text("Copy Report", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TitanCyan)
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Radial Score Gauges Layout
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Left Side: Job A Gauge
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier
            .weight(1f)
            .testTag("score_gauge_job_a")
        ) {
          Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(76.dp)
          ) {
            CircularProgressIndicator(
              progress = { report.scoreA / 100f },
              modifier = Modifier.fillMaxSize(),
              color = TitanCyan,
              trackColor = TitanCyan.copy(alpha = 0.15f),
              strokeWidth = 6.dp,
              strokeCap = StrokeCap.Round
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                text = "${report.scoreA}%",
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = TitanCyan
              )
              Text(
                text = "MATCH",
                fontSize = 7.sp,
                fontWeight = FontWeight.Bold,
                color = TextMutedDark,
                letterSpacing = 0.5.sp
              )
            }
          }

          Spacer(modifier = Modifier.height(6.dp))

          Text(
            text = report.jobA.targetCompany,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimaryDark,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
          Text(
            text = report.jobA.targetRole,
            fontSize = 10.sp,
            color = TextMutedDark,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
          )

          Spacer(modifier = Modifier.height(4.dp))

          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(4.dp))
              .background(TitanCyan.copy(alpha = 0.15f))
              .padding(horizontal = 6.dp, vertical = 2.dp)
          ) {
            Text(
              text = report.tierA.label,
              fontSize = 8.sp,
              fontWeight = FontWeight.Bold,
              color = TitanCyan
            )
          }
        }

        // Center: VS Badge & Delta Margin
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier.padding(horizontal = 8.dp)
        ) {
          Box(
            modifier = Modifier
              .size(32.dp)
              .clip(CircleShape)
              .background(SlateElevated)
              .border(1.dp, SlateBorder, CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "VS",
              fontSize = 11.sp,
              fontWeight = FontWeight.Black,
              color = TextMutedDark
            )
          }

          Spacer(modifier = Modifier.height(6.dp))

          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .background(
                if (report.scoreDelta != 0) TitanEmerald.copy(alpha = 0.15f) else SlateElevated
              )
              .border(
                1.dp,
                if (report.scoreDelta != 0) TitanEmerald.copy(alpha = 0.4f) else SlateBorder,
                RoundedCornerShape(8.dp)
              )
              .padding(horizontal = 8.dp, vertical = 3.dp)
          ) {
            Text(
              text = if (report.scoreDelta > 0) "+${report.scoreDelta}% A Lead"
              else if (report.scoreDelta < 0) "+${-report.scoreDelta}% B Lead"
              else "Tied Fit",
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              color = if (report.scoreDelta != 0) TitanEmerald else TextMutedDark
            )
          }
        }

        // Right Side: Job B Gauge
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier
            .weight(1f)
            .testTag("score_gauge_job_b")
        ) {
          Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(76.dp)
          ) {
            CircularProgressIndicator(
              progress = { report.scoreB / 100f },
              modifier = Modifier.fillMaxSize(),
              color = TitanGold,
              trackColor = TitanGold.copy(alpha = 0.15f),
              strokeWidth = 6.dp,
              strokeCap = StrokeCap.Round
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                text = "${report.scoreB}%",
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = TitanGold
              )
              Text(
                text = "MATCH",
                fontSize = 7.sp,
                fontWeight = FontWeight.Bold,
                color = TextMutedDark,
                letterSpacing = 0.5.sp
              )
            }
          }

          Spacer(modifier = Modifier.height(6.dp))

          Text(
            text = report.jobB.targetCompany,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimaryDark,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
          Text(
            text = report.jobB.targetRole,
            fontSize = 10.sp,
            color = TextMutedDark,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
          )

          Spacer(modifier = Modifier.height(4.dp))

          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(4.dp))
              .background(TitanGold.copy(alpha = 0.15f))
              .padding(horizontal = 6.dp, vertical = 2.dp)
          ) {
            Text(
              text = report.tierB.label,
              fontSize = 8.sp,
              fontWeight = FontWeight.Bold,
              color = TitanGold
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Bottom Winning Margin Pill
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(8.dp))
          .background(SlateElevated)
          .padding(horizontal = 10.dp, vertical = 7.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.Center,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.AutoAwesome,
            contentDescription = null,
            tint = TitanCyan,
            modifier = Modifier.size(13.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = report.winningMarginSummary,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = TitanCyan
          )
        }
      }
    }
  }
}

/**
 * Comparative progress bar for individual match dimensions (Skills, Experience, ATS, Metrics).
 */
@Composable
private fun HeadToHeadDimensionBar(
  dimension: ComparisonDimensionBreakdown,
  companyA: String,
  companyB: String
) {
  Column(modifier = Modifier.fillMaxWidth()) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = dimension.dimensionTitle,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = TextPrimaryDark
      )

      Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
          text = "$companyA: ${dimension.metricLabelA}",
          fontSize = 10.sp,
          fontWeight = FontWeight.Bold,
          color = TitanCyan
        )
        Text(text = " • ", fontSize = 10.sp, color = TextMutedDark)
        Text(
          text = "$companyB: ${dimension.metricLabelB}",
          fontSize = 10.sp,
          fontWeight = FontWeight.Bold,
          color = TitanGold
        )
      }
    }

    Spacer(modifier = Modifier.height(4.dp))

    // Head-to-Head Comparative Bar
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .height(7.dp)
        .clip(RoundedCornerShape(4.dp))
        .background(SlateElevated),
      horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
      LinearProgressIndicator(
        progress = { dimension.scoreA / 100f },
        modifier = Modifier
          .weight(1f)
          .fillMaxHeight(),
        color = TitanCyan,
        trackColor = SlateElevated,
        strokeCap = StrokeCap.Round
      )
      LinearProgressIndicator(
        progress = { dimension.scoreB / 100f },
        modifier = Modifier
          .weight(1f)
          .fillMaxHeight(),
        color = TitanGold,
        trackColor = SlateElevated,
        strokeCap = StrokeCap.Round
      )
    }

    Spacer(modifier = Modifier.height(4.dp))

    Text(
      text = dimension.insightText,
      fontSize = 10.sp,
      color = TextMutedDark,
      lineHeight = 14.sp
    )
  }
}

/**
 * Column card representing one side of the split comparison (Job A or Job B).
 */
@Composable
private fun JobComparisonColumnCard(
  label: String,
  job: JobActionPlan,
  match: ResumeJdMatchResult?,
  isWinner: Boolean,
  primaryColor: androidx.compose.ui.graphics.Color,
  showRawEditor: Boolean,
  rawJdText: String,
  onToggleRawEditor: () -> Unit,
  onRawTextChanged: (String) -> Unit,
  onAdoptAsPrimary: () -> Unit,
  testTagPrefix: String
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("${testTagPrefix}_comparison_column_card"),
    colors = CardDefaults.cardColors(containerColor = SlateCard),
    shape = RoundedCornerShape(14.dp),
    border = androidx.compose.foundation.BorderStroke(
      width = if (isWinner) 1.5.dp else 1.dp,
      color = if (isWinner) primaryColor.copy(alpha = 0.8f) else SlateBorder
    )
  ) {
    Column(modifier = Modifier.padding(12.dp)) {
      // Header & Winner Tag
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(primaryColor.copy(alpha = 0.15f))
            .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
          Text(
            text = label,
            fontSize = 8.sp,
            fontWeight = FontWeight.Black,
            color = primaryColor
          )
        }

        if (isWinner) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(4.dp))
              .background(TitanEmerald.copy(alpha = 0.18f))
              .padding(horizontal = 5.dp, vertical = 2.dp)
          ) {
            Text(
              text = "🏆 HIGHER FIT",
              fontSize = 8.sp,
              fontWeight = FontWeight.Black,
              color = TitanEmerald
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(6.dp))

      Text(
        text = job.targetCompany,
        fontSize = 15.sp,
        fontWeight = FontWeight.Black,
        color = TextPrimaryDark,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )
      Text(
        text = job.targetRole,
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold,
        color = primaryColor,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )

      Spacer(modifier = Modifier.height(6.dp))

      // Compensation & Seniority Pills
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(SlateElevated)
            .padding(horizontal = 5.dp, vertical = 2.dp)
        ) {
          Text(
            text = job.compensationBenchmark.take(18),
            fontSize = 9.sp,
            color = TextSecondaryDark
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))
      HorizontalDivider(color = SlateBorder)
      Spacer(modifier = Modifier.height(10.dp))

      // Matched Skills Breakdown
      Text(
        text = "TOP MATCHING SKILLS",
        fontSize = 9.sp,
        fontWeight = FontWeight.Black,
        color = TitanEmerald,
        letterSpacing = 0.5.sp
      )
      Spacer(modifier = Modifier.height(4.dp))

      val matchedSkills = job.requiredSkills
        .filter { it.candidateMatchStatus == CandidateMatchStatus.STRONG_MATCH }
        .take(3)

      if (matchedSkills.isNotEmpty()) {
        matchedSkills.forEach { skill ->
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 1.dp)
          ) {
            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = TitanEmerald, modifier = Modifier.size(11.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = skill.name,
              fontSize = 10.sp,
              color = TextPrimaryDark,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
          }
        }
      } else {
        Text("Foundational alignment across core specs", fontSize = 10.sp, color = TextMutedDark)
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Critical Gaps Breakdown
      Text(
        text = "CRITICAL GAP TO BRIDGE",
        fontSize = 9.sp,
        fontWeight = FontWeight.Black,
        color = TitanCrimson,
        letterSpacing = 0.5.sp
      )
      Spacer(modifier = Modifier.height(4.dp))

      val gaps = job.requiredSkills
        .filter { it.candidateMatchStatus != CandidateMatchStatus.STRONG_MATCH }
        .take(2)

      if (gaps.isNotEmpty()) {
        gaps.forEach { gap ->
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 1.dp)
          ) {
            Icon(Icons.Default.Warning, contentDescription = null, tint = TitanCrimson, modifier = Modifier.size(11.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = gap.name,
              fontSize = 10.sp,
              color = TextSecondaryDark,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
          }
        }
      } else {
        Text("No blocking critical skill gaps identified", fontSize = 10.sp, color = TitanEmerald)
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Primary Interview Talking Point Preview
      job.interviewTalkingPoints.firstOrNull()?.let { point ->
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(SlateElevated)
            .padding(6.dp)
        ) {
          Column {
            Text(
              text = "PITCH THEME: ${point.theme.take(28)}",
              fontSize = 8.sp,
              fontWeight = FontWeight.Bold,
              color = primaryColor
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = point.executiveNarrative.take(75) + "...",
              fontSize = 9.sp,
              color = TextSecondaryDark,
              lineHeight = 13.sp
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Raw JD Editor Toggle
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clickable { onToggleRawEditor() }
          .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = if (showRawEditor) "Hide Raw JD" else "Inspect / Edit JD",
          fontSize = 10.sp,
          color = primaryColor,
          fontWeight = FontWeight.Bold
        )
        Icon(
          imageVector = if (showRawEditor) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
          contentDescription = null,
          tint = primaryColor,
          modifier = Modifier.size(14.dp)
        )
      }

      AnimatedVisibility(visible = showRawEditor) {
        Column {
          Spacer(modifier = Modifier.height(6.dp))
          OutlinedTextField(
            value = rawJdText,
            onValueChange = onRawTextChanged,
            modifier = Modifier
              .fillMaxWidth()
              .heightIn(min = 100.dp, max = 180.dp)
              .testTag("${testTagPrefix}_raw_jd_textfield"),
            placeholder = { Text("Paste JD text here...", fontSize = 10.sp) },
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = primaryColor,
              unfocusedBorderColor = SlateBorder,
              focusedTextColor = TextPrimaryDark,
              unfocusedTextColor = TextPrimaryDark,
              focusedContainerColor = ObsidianDark,
              unfocusedContainerColor = ObsidianDark
            ),
            shape = RoundedCornerShape(8.dp),
            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 10.sp)
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Adopt as Active Primary Search Action Button
      Button(
        onClick = onAdoptAsPrimary,
        modifier = Modifier
          .fillMaxWidth()
          .height(36.dp)
          .testTag("${testTagPrefix}_adopt_as_primary_button"),
        colors = ButtonDefaults.buttonColors(
          containerColor = primaryColor.copy(alpha = 0.2f),
          contentColor = primaryColor
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, primaryColor.copy(alpha = 0.6f)),
        shape = RoundedCornerShape(8.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 6.dp, vertical = 0.dp)
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = "Make Primary Plan",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
          )
          Spacer(modifier = Modifier.width(4.dp))
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            modifier = Modifier.size(11.dp)
          )
        }
      }
    }
  }
}
