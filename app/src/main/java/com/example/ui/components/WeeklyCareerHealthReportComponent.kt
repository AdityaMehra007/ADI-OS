package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WorkspacePremium
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
import androidx.compose.material3.Surface
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.WeeklyCareerHealthReport
import com.example.data.model.WeeklySprintObjective
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
import com.example.ui.viewmodel.TitanViewModel

/**
 * High-craft executive dashboard component synthesizing telemetry from both
 * the Skills Radar (Resume vs Target JDs) and the Career Velocity Dashboard (Pacing).
 * Provides one-tap PDF generation, sharing, and actionable 7-day sprint objectives.
 */
@Composable
fun WeeklyCareerHealthReportCard(
  viewModel: TitanViewModel,
  modifier: Modifier = Modifier,
  onOpenDetailedDialog: () -> Unit = { viewModel.showWeeklyHealthReportDialog.value = true }
) {
  val context = LocalContext.current
  val reportState by viewModel.weeklyCareerHealthReport.collectAsState()
  val isGeneratingPdf by viewModel.isGeneratingHealthReportPdf.collectAsState()
  var isExpanded by remember { mutableStateOf(false) }

  val report = reportState ?: return

  Card(
    modifier = modifier.testTag("weekly_career_health_report_card"),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = SlateCard),
    border = BorderStroke(1.dp, TitanCyan.copy(alpha = 0.45f))
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      // 1. Executive Top Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
          Box(
            modifier = Modifier
              .size(36.dp)
              .clip(RoundedCornerShape(8.dp))
              .background(
                Brush.linearGradient(
                  colors = listOf(TitanCyan.copy(alpha = 0.25f), TitanEmerald.copy(alpha = 0.2f))
                )
              ),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              Icons.Default.HealthAndSafety,
              contentDescription = "Career Health",
              tint = TitanCyan,
              modifier = Modifier.size(20.dp)
            )
          }

          Spacer(modifier = Modifier.width(10.dp))

          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                "WEEKLY CAREER HEALTH REPORT",
                style = MaterialTheme.typography.labelSmall,
                color = TitanCyan,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                letterSpacing = 0.8.sp
              )
              Spacer(modifier = Modifier.width(6.dp))
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(4.dp))
                  .background(TitanAmber.copy(alpha = 0.2f))
                  .padding(horizontal = 5.dp, vertical = 1.dp)
              ) {
                Text(
                  "PDF EXPORTABLE",
                  style = MaterialTheme.typography.labelSmall,
                  color = TitanAmber,
                  fontWeight = FontWeight.ExtraBold,
                  fontSize = 8.sp
                )
              }
            }
            Text(
              report.weekTitle,
              style = MaterialTheme.typography.titleMedium,
              color = TextPrimaryDark,
              fontWeight = FontWeight.Bold
            )
          }
        }

        // Action Buttons
        Row(verticalAlignment = Alignment.CenterVertically) {
          IconButton(
            onClick = {
              viewModel.refreshWeeklyCareerHealthReport()
              Toast.makeText(context, "Weekly Health Report re-synthesized", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.size(32.dp)
          ) {
            Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = TextSecondaryDark, modifier = Modifier.size(16.dp))
          }

          IconButton(
            onClick = onOpenDetailedDialog,
            modifier = Modifier.size(32.dp)
          ) {
            Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = "Full Briefing", tint = TitanCyan, modifier = Modifier.size(16.dp))
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // 2. Synthesized Composite Score Banner
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(12.dp))
          .background(
            Brush.horizontalGradient(
              colors = listOf(SlateElevated, SlateElevated.copy(alpha = 0.8f))
            )
          )
          .border(BorderStroke(1.dp, SlateBorder), RoundedCornerShape(12.dp))
          .padding(14.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          // Left: Composite Score Circle / Ring
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(54.dp)
                .clip(CircleShape)
                .background(
                  Brush.radialGradient(
                    colors = listOf(
                      TitanEmerald.copy(alpha = 0.25f),
                      ObsidianDark
                    )
                  )
                )
                .border(2.dp, TitanEmerald, CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                  "${report.compositeHealthScore}",
                  color = TitanEmerald,
                  fontWeight = FontWeight.Black,
                  fontSize = 18.sp
                )
                Text(
                  "/100",
                  color = TextMutedDark,
                  fontSize = 8.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  report.healthGrade,
                  color = TitanEmerald,
                  fontWeight = FontWeight.ExtraBold,
                  fontSize = 13.sp
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  "• ${report.pacingStatus}",
                  color = TextSecondaryDark,
                  fontSize = 11.sp,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
                )
              }
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                "Benchmark Target: ${report.targetBenchmarkCompany} — ${report.targetBenchmarkRole}",
                color = TextPrimaryDark,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
              Text(
                "Telemetry: ${report.daysToFullMarketReadiness}d to readiness • ${report.compensationTrajectory}",
                color = TitanAmber,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // 3. Four-Card Synthesis Metric Bar (Skills Radar vs Career Velocity)
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        MetricScorePill(
          icon = Icons.Default.Radar,
          iconTint = TitanCyan,
          label = "SKILLS RADAR",
          value = "${report.skillsRadarMatchScore}%",
          sub = "${report.keywordMatchPct}% Keywords",
          modifier = Modifier.weight(1f)
        )
        MetricScorePill(
          icon = Icons.Default.Speed,
          iconTint = Color(0xFF818CF8), // Indigo
          label = "VELOCITY GAIN",
          value = "+${report.monthlySkillGainRate}%",
          sub = "Monthly rate",
          modifier = Modifier.weight(1f)
        )
        MetricScorePill(
          icon = Icons.Default.CheckCircle,
          iconTint = TitanEmerald,
          label = "REQUIREMENTS",
          value = "${report.verifiedRequirementsCount}/${report.totalRequirementsCount}",
          sub = "Verified Moat",
          modifier = Modifier.weight(1f)
        )
      }

      Spacer(modifier = Modifier.height(14.dp))

      // 4. Executive Summary Narrative (Clean scannable briefing)
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(8.dp))
          .background(SlateElevated)
          .border(BorderStroke(1.dp, SlateBorder.copy(alpha = 0.7f)), RoundedCornerShape(8.dp))
          .padding(12.dp)
      ) {
        Column {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = TitanCyan, modifier = Modifier.size(13.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                "EXECUTIVE SYNTHESIS BRIEFING",
                style = MaterialTheme.typography.labelSmall,
                color = TitanCyan,
                fontWeight = FontWeight.Bold,
                fontSize = 9.sp
              )
            }

            // Copy narrative button
            IconButton(
              onClick = {
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("Executive Career Summary", report.executiveSummaryNarrative)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(context, "Executive Summary copied to clipboard", Toast.LENGTH_SHORT).show()
              },
              modifier = Modifier.size(24.dp)
            ) {
              Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = TextSecondaryDark, modifier = Modifier.size(13.dp))
            }
          }

          Spacer(modifier = Modifier.height(6.dp))

          Text(
            text = report.executiveSummaryNarrative,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondaryDark,
            fontSize = 11.5.sp,
            lineHeight = 16.sp
          )
        }
      }

      // Expandable section for Dimension deltas & 7-day action sprint
      AnimatedVisibility(
        visible = isExpanded,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
      ) {
        Column {
          Spacer(modifier = Modifier.height(14.dp))

          // Skills Radar vs Target JD Breakdown
          Text(
            "RADAR DIMENSION READINESS DELTAS",
            style = MaterialTheme.typography.labelSmall,
            color = TitanCyan,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp
          )
          Spacer(modifier = Modifier.height(6.dp))

          report.dimensionScores.forEach { dim ->
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 3.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                dim.title,
                style = MaterialTheme.typography.bodySmall,
                color = TextPrimaryDark,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1.5f)
              )

              Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.End) {
                Text(
                  "Resume ${dim.resumeScorePct}%",
                  color = TitanCyan,
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  "vs ${dim.targetJdScorePct}%",
                  color = TextMutedDark,
                  fontSize = 10.sp
                )
                Spacer(modifier = Modifier.width(6.dp))
                val deltaText = if (dim.deltaPct >= 0) "+${dim.deltaPct}%" else "${dim.deltaPct}%"
                val deltaColor = if (dim.deltaPct >= 0) TitanEmerald else Color(0xFFEF4444)
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(3.dp))
                    .background(deltaColor.copy(alpha = 0.15f))
                    .padding(horizontal = 4.dp, vertical = 1.dp)
                ) {
                  Text(deltaText, color = deltaColor, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
              }
            }
            HorizontalDivider(color = SlateBorder.copy(alpha = 0.4f), thickness = 0.5.dp)
          }

          Spacer(modifier = Modifier.height(14.dp))

          // 7-Day High-Impact Sprint Objectives
          Text(
            "WEEKLY 7-DAY ACTION SPRINT",
            style = MaterialTheme.typography.labelSmall,
            color = TitanEmerald,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp
          )
          Spacer(modifier = Modifier.height(6.dp))

          report.weeklySprintObjectives.take(3).forEach { sprint ->
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(SlateElevated.copy(alpha = 0.6f))
                .padding(8.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Box(
                modifier = Modifier
                  .size(20.dp)
                  .clip(CircleShape)
                  .background(TitanEmerald.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  "#${sprint.priorityRank}",
                  color = TitanEmerald,
                  fontWeight = FontWeight.Bold,
                  fontSize = 9.sp
                )
              }
              Spacer(modifier = Modifier.width(8.dp))
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  sprint.title,
                  color = TextPrimaryDark,
                  fontWeight = FontWeight.SemiBold,
                  fontSize = 11.sp,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
                )
                Text(
                  sprint.estimatedImpact,
                  color = TitanAmber,
                  fontSize = 9.5.sp
                )
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // 5. Action Bar: PDF Export & Expand Details
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Toggle view button
        Row(
          modifier = Modifier
            .clickable { isExpanded = !isExpanded }
            .padding(vertical = 6.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            if (isExpanded) "Hide Full Breakdown" else "View Radar Deltas & Sprint",
            color = TitanCyan,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp
          )
          Icon(
            if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
            contentDescription = null,
            tint = TitanCyan,
            modifier = Modifier.size(16.dp)
          )
        }

        // Primary Action: Export Official Executive PDF
        Button(
          onClick = {
            viewModel.exportAndShareWeeklyHealthReportPdf(context)
          },
          enabled = !isGeneratingPdf,
          colors = ButtonDefaults.buttonColors(
            containerColor = TitanEmerald,
            contentColor = ObsidianDark
          ),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier.testTag("export_pdf_button")
        ) {
          if (isGeneratingPdf) {
            CircularProgressIndicator(
              modifier = Modifier.size(14.dp),
              color = ObsidianDark,
              strokeWidth = 2.dp
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text("Exporting...", fontSize = 11.sp, fontWeight = FontWeight.Bold)
          } else {
            Icon(Icons.Default.PictureAsPdf, contentDescription = "PDF", modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Export Executive PDF", fontSize = 11.sp, fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}

/**
 * Metric Pill for high-density summary statistics.
 */
@Composable
private fun MetricScorePill(
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  iconTint: Color,
  label: String,
  value: String,
  sub: String,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(8.dp))
      .background(SlateElevated)
      .border(BorderStroke(1.dp, SlateBorder.copy(alpha = 0.5f)), RoundedCornerShape(8.dp))
      .padding(8.dp)
  ) {
    Column {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(12.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(
          label,
          style = MaterialTheme.typography.labelSmall,
          color = TextMutedDark,
          fontSize = 8.sp,
          fontWeight = FontWeight.Bold
        )
      }
      Spacer(modifier = Modifier.height(3.dp))
      Text(
        value,
        color = TextPrimaryDark,
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp
      )
      Text(
        sub,
        color = iconTint,
        fontSize = 9.sp,
        fontWeight = FontWeight.Medium,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )
    }
  }
}

/**
 * Full-screen / modal briefing dialog displaying the comprehensive Weekly Career Health Report
 * with instant PDF export, viewing, and direct sharing capabilities.
 */
@Composable
fun WeeklyCareerHealthReportDialog(
  viewModel: TitanViewModel,
  onDismissRequest: () -> Unit
) {
  val context = LocalContext.current
  val reportState by viewModel.weeklyCareerHealthReport.collectAsState()
  val isGeneratingPdf by viewModel.isGeneratingHealthReportPdf.collectAsState()
  val scrollState = rememberScrollState()

  val report = reportState ?: return

  Dialog(
    onDismissRequest = onDismissRequest,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = Modifier
        .fillMaxSize()
        .padding(12.dp)
        .clip(RoundedCornerShape(16.dp))
        .border(BorderStroke(1.dp, TitanCyan.copy(alpha = 0.5f)), RoundedCornerShape(16.dp)),
      color = ObsidianDark
    ) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(16.dp)
      ) {
        // Dialog Top Bar
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(TitanCyan.copy(alpha = 0.2f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.PictureAsPdf, contentDescription = null, tint = TitanCyan, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                "TITAN CAREER OS • EXECUTIVE REPORT",
                style = MaterialTheme.typography.labelSmall,
                color = TitanCyan,
                fontWeight = FontWeight.Bold,
                fontSize = 9.5.sp
              )
              Text(
                report.weekTitle,
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimaryDark,
                fontWeight = FontWeight.Bold
              )
            }
          }

          IconButton(
            onClick = onDismissRequest,
            modifier = Modifier.size(32.dp)
          ) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondaryDark)
          }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = SlateBorder)

        // Scrollable Report Body
        Column(
          modifier = Modifier
            .weight(1f)
            .verticalScroll(scrollState)
        ) {
          // Metadata Box
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(10.dp))
              .background(SlateElevated)
              .border(BorderStroke(1.dp, SlateBorder), RoundedCornerShape(10.dp))
              .padding(12.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Column(modifier = Modifier.weight(1.2f)) {
                Text("CANDIDATE", color = TextMutedDark, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                Text(report.candidateName, color = TextPrimaryDark, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Text("${report.candidateRole} • ${report.resumeVersionAnalyzed}", color = TextSecondaryDark, fontSize = 10.5.sp)
              }

              Column(modifier = Modifier.weight(1.2f)) {
                Text("TARGET BENCHMARK", color = TextMutedDark, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                Text(report.targetBenchmarkCompany, color = TitanEmerald, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Text(report.targetBenchmarkRole, color = TextSecondaryDark, fontSize = 10.5.sp)
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // 4 Scorecard KPI metrics
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            ScorecardGridCell(
              title = "HEALTH COMPOSITE",
              value = "${report.compositeHealthScore}/100",
              sub = report.healthGrade,
              accentColor = TitanEmerald,
              modifier = Modifier.weight(1f)
            )
            ScorecardGridCell(
              title = "SKILLS RADAR",
              value = "${report.skillsRadarMatchScore}%",
              sub = "${report.keywordMatchPct}% Match",
              accentColor = TitanCyan,
              modifier = Modifier.weight(1f)
            )
            ScorecardGridCell(
              title = "VELOCITY PACING",
              value = "+${report.monthlySkillGainRate}%",
              sub = "1.4x Market",
              accentColor = Color(0xFF818CF8),
              modifier = Modifier.weight(1f)
            )
            ScorecardGridCell(
              title = "DAYS TO ROLE",
              value = "${report.daysToFullMarketReadiness}d",
              sub = "Readiness",
              accentColor = TitanAmber,
              modifier = Modifier.weight(1f)
            )
          }

          Spacer(modifier = Modifier.height(16.dp))

          // Executive Synthesis Narrative
          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = SlateCard),
            border = BorderStroke(1.dp, SlateBorder)
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  "1. EXECUTIVE MARKET SYNTHESIS",
                  style = MaterialTheme.typography.labelSmall,
                  color = TitanCyan,
                  fontWeight = FontWeight.Bold,
                  fontSize = 10.sp
                )

                IconButton(
                  onClick = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboard.setPrimaryClip(ClipData.newPlainText("Executive Narrative", report.executiveSummaryNarrative))
                    Toast.makeText(context, "Narrative copied", Toast.LENGTH_SHORT).show()
                  },
                  modifier = Modifier.size(24.dp)
                ) {
                  Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = TextSecondaryDark, modifier = Modifier.size(13.dp))
                }
              }

              Spacer(modifier = Modifier.height(6.dp))
              Text(
                report.executiveSummaryNarrative,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondaryDark,
                lineHeight = 18.sp,
                fontSize = 12.sp
              )
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Skills Radar vs Target JDs (All 6 Dimensions Table)
          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = SlateCard),
            border = BorderStroke(1.dp, SlateBorder)
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Text(
                "2. SKILLS RADAR DIMENSIONS (STORED RESUME VS BENCHMARK)",
                style = MaterialTheme.typography.labelSmall,
                color = TitanCyan,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp
              )
              Spacer(modifier = Modifier.height(8.dp))

              report.dimensionScores.forEach { dim ->
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(
                    dim.title,
                    color = TextPrimaryDark,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1.5f)
                  )

                  Row(
                    modifier = Modifier.weight(1.5f),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Text("Resume: ${dim.resumeScorePct}%", color = TitanCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Target: ${dim.targetJdScorePct}%", color = TitanAmber, fontSize = 10.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    val deltaStr = if (dim.deltaPct >= 0) "+${dim.deltaPct}%" else "${dim.deltaPct}%"
                    val deltaColor = if (dim.deltaPct >= 0) TitanEmerald else Color(0xFFEF4444)
                    Box(
                      modifier = Modifier
                        .clip(RoundedCornerShape(3.dp))
                        .background(deltaColor.copy(alpha = 0.2f))
                        .padding(horizontal = 4.dp, vertical = 1.dp)
                    ) {
                      Text(deltaStr, color = deltaColor, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                  }
                }
                HorizontalDivider(color = SlateBorder.copy(alpha = 0.3f), thickness = 0.5.dp)
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // ATS Gaps: Missing Keywords & Missing Certifications
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            // Missing Keywords
            Card(
              modifier = Modifier.weight(1f),
              shape = RoundedCornerShape(8.dp),
              colors = CardDefaults.cardColors(containerColor = SlateElevated),
              border = BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.4f))
            ) {
              Column(modifier = Modifier.padding(10.dp)) {
                Text(
                  "MISSING ATS KEYWORDS (${report.criticalMissingKeywords.size})",
                  color = Color(0xFFEF4444),
                  fontWeight = FontWeight.Bold,
                  fontSize = 9.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                report.criticalMissingKeywords.take(4).forEach { kw ->
                  Text("• $kw", color = TextPrimaryDark, fontSize = 10.5.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
              }
            }

            // Missing Certifications
            Card(
              modifier = Modifier.weight(1f),
              shape = RoundedCornerShape(8.dp),
              colors = CardDefaults.cardColors(containerColor = SlateElevated),
              border = BorderStroke(1.dp, TitanAmber.copy(alpha = 0.4f))
            ) {
              Column(modifier = Modifier.padding(10.dp)) {
                Text(
                  "CRITICAL CERTIFICATION GAPS",
                  color = TitanAmber,
                  fontWeight = FontWeight.Bold,
                  fontSize = 9.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                report.missingCertifications.take(2).forEach { cert ->
                  Text("• $cert", color = TextPrimaryDark, fontSize = 10.5.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                report.verifiedMoatStrengths.take(1).forEach { str ->
                  Text("✓ $str", color = TitanEmerald, fontSize = 10.5.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // 7-Day Sprint Plan
          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = SlateCard),
            border = BorderStroke(1.dp, SlateBorder)
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Text(
                "3. PRIORITIZED 7-DAY ACTION SPRINT",
                style = MaterialTheme.typography.labelSmall,
                color = TitanEmerald,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp
              )
              Spacer(modifier = Modifier.height(8.dp))

              report.weeklySprintObjectives.forEach { sprint ->
                SprintActionRow(sprint = sprint)
                Spacer(modifier = Modifier.height(6.dp))
              }
            }
          }

          Spacer(modifier = Modifier.height(16.dp))
        }

        // Bottom Action Bar: Share PDF & View PDF
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          OutlinedButton(
            onClick = {
              viewModel.exportAndViewWeeklyHealthReportPdf(context)
            },
            enabled = !isGeneratingPdf,
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, TitanCyan),
            modifier = Modifier.weight(1f)
          ) {
            Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null, tint = TitanCyan, modifier = Modifier.size(15.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("View PDF", color = TitanCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
          }

          Button(
            onClick = {
              viewModel.exportAndShareWeeklyHealthReportPdf(context)
            },
            enabled = !isGeneratingPdf,
            colors = ButtonDefaults.buttonColors(
              containerColor = TitanEmerald,
              contentColor = ObsidianDark
            ),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.weight(1.3f)
          ) {
            if (isGeneratingPdf) {
              CircularProgressIndicator(modifier = Modifier.size(16.dp), color = ObsidianDark, strokeWidth = 2.dp)
              Spacer(modifier = Modifier.width(6.dp))
              Text("Compiling PDF...", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            } else {
              Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(15.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Export & Share PDF", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    }
  }
}

@Composable
private fun ScorecardGridCell(
  title: String,
  value: String,
  sub: String,
  accentColor: Color,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(8.dp))
      .background(SlateElevated)
      .border(BorderStroke(1.dp, SlateBorder), RoundedCornerShape(8.dp))
      .padding(8.dp)
  ) {
    Column {
      Text(title, color = TextMutedDark, fontSize = 8.sp, fontWeight = FontWeight.Bold)
      Spacer(modifier = Modifier.height(2.dp))
      Text(value, color = accentColor, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
      Text(sub, color = TextSecondaryDark, fontSize = 8.5.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
  }
}

@Composable
private fun SprintActionRow(
  sprint: WeeklySprintObjective
) {
  var isChecked by remember { mutableStateOf(sprint.isCompleted) }

  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(6.dp))
      .background(SlateElevated)
      .clickable { isChecked = !isChecked }
      .padding(10.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Box(
      modifier = Modifier
        .size(22.dp)
        .clip(CircleShape)
        .background(if (isChecked) TitanEmerald else TitanCyan.copy(alpha = 0.2f)),
      contentAlignment = Alignment.Center
    ) {
      if (isChecked) {
        Icon(Icons.Default.Check, contentDescription = null, tint = ObsidianDark, modifier = Modifier.size(14.dp))
      } else {
        Text("#${sprint.priorityRank}", color = TitanCyan, fontSize = 9.sp, fontWeight = FontWeight.Bold)
      }
    }

    Spacer(modifier = Modifier.width(10.dp))

    Column(modifier = Modifier.weight(1f)) {
      Text(
        sprint.title,
        color = if (isChecked) TextMutedDark else TextPrimaryDark,
        fontWeight = FontWeight.SemiBold,
        fontSize = 11.5.sp
      )
      Text(
        "${sprint.estimatedImpact} • ${sprint.rationale}",
        color = if (isChecked) TextMutedDark else TextSecondaryDark,
        fontSize = 10.sp,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis
      )
    }
  }
}
