package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.export.CareerRoadmapReportExporter
import com.example.data.export.ExportReportFormat
import com.example.data.export.ExportReportScope
import com.example.data.model.Application
import com.example.data.model.ComprehensiveCareerRoadmapReport
import com.example.ui.theme.ObsidianDark
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateCard
import com.example.ui.theme.SlateDarker
import com.example.ui.theme.SlateElevated
import com.example.ui.theme.TextMutedDark
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import com.example.ui.theme.TitanCyan
import com.example.ui.theme.TitanEmerald
import com.example.ui.theme.TitanGold

/**
 * Interactive Export Dialog for Career Roadmap & Job Status Lists
 * Supports real-time preview, formatted markdown / plain text, clipboard copying, and system share.
 */
@Composable
fun CareerRoadmapReportExportDialog(
  roadmapReport: ComprehensiveCareerRoadmapReport?,
  applications: List<Application>,
  candidateName: String = "Adi",
  onDismiss: () -> Unit
) {
  val context = LocalContext.current

  var selectedScope by remember { mutableStateOf(ExportReportScope.FULL_DOSSIER) }
  var selectedFormat by remember { mutableStateOf(ExportReportFormat.MARKDOWN) }
  var isCopied by remember { mutableStateOf(false) }

  val generatedReportText = remember(roadmapReport, applications, candidateName, selectedScope, selectedFormat) {
    CareerRoadmapReportExporter.generateReport(
      roadmapReport = roadmapReport,
      applications = applications,
      candidateName = candidateName,
      scope = selectedScope,
      format = selectedFormat
    )
  }

  val wordCount = remember(generatedReportText) {
    generatedReportText.split(Regex("\\s+")).count { it.isNotBlank() }
  }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Card(
      modifier = Modifier
        .fillMaxWidth(0.95f)
        .fillMaxHeight(0.92f)
        .clip(RoundedCornerShape(20.dp))
        .border(1.dp, SlateBorder, RoundedCornerShape(20.dp))
        .testTag("career_roadmap_export_dialog"),
      colors = CardDefaults.cardColors(containerColor = SlateCard)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        // Dialog Header
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
                .size(38.dp)
                .clip(CircleShape)
                .background(TitanCyan.copy(alpha = 0.15f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Description,
                contentDescription = "Report Export",
                tint = TitanCyan,
                modifier = Modifier.size(20.dp)
              )
            }
            Column {
              Text(
                text = "Export Career Report",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimaryDark
              )
              Text(
                text = "Summary Text & Executive Dossier",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondaryDark,
                fontSize = 11.5.sp
              )
            }
          }

          IconButton(
            onClick = onDismiss,
            modifier = Modifier.testTag("close_export_dialog_button")
          ) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMutedDark)
          }
        }

        // Scope Selection Row
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
          Text(
            text = "REPORT CONTENT SCOPE",
            style = MaterialTheme.typography.labelSmall,
            color = TitanCyan,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.8.sp
          )
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            ExportReportScope.values().forEach { scope ->
              val isSelected = selectedScope == scope
              FilterChip(
                selected = isSelected,
                onClick = {
                  selectedScope = scope
                  isCopied = false
                },
                label = {
                  Text(
                    text = when (scope) {
                      ExportReportScope.FULL_DOSSIER -> "Full Dossier (${applications.size} Jobs + Roadmap)"
                      ExportReportScope.ROADMAP_ONLY -> "Roadmap & Skills Only"
                      ExportReportScope.JOB_STATUS_ONLY -> "Job Pipeline Status Only (${applications.size})"
                    },
                    fontSize = 11.5.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                  )
                },
                colors = FilterChipDefaults.filterChipColors(
                  selectedContainerColor = TitanCyan.copy(alpha = 0.2f),
                  selectedLabelColor = TitanCyan,
                  containerColor = SlateElevated,
                  labelColor = TextMutedDark
                ),
                border = FilterChipDefaults.filterChipBorder(
                  enabled = true,
                  selected = isSelected,
                  borderColor = SlateBorder,
                  selectedBorderColor = TitanCyan
                )
              )
            }
          }
        }

        // Format Selection & Stats Bar
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            ExportReportFormat.values().forEach { fmt ->
              val isSelected = selectedFormat == fmt
              FilterChip(
                selected = isSelected,
                onClick = {
                  selectedFormat = fmt
                  isCopied = false
                },
                label = {
                  Text(
                    text = "${fmt.displayName} (${fmt.extension})",
                    fontSize = 11.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                  )
                },
                colors = FilterChipDefaults.filterChipColors(
                  selectedContainerColor = TitanGold.copy(alpha = 0.2f),
                  selectedLabelColor = TitanGold,
                  containerColor = SlateElevated,
                  labelColor = TextMutedDark
                ),
                border = FilterChipDefaults.filterChipBorder(
                  enabled = true,
                  selected = isSelected,
                  borderColor = SlateBorder,
                  selectedBorderColor = TitanGold
                )
              )
            }
          }

          // Metadata Chips (Word count & char count)
          Surface(
            color = SlateDarker,
            shape = RoundedCornerShape(6.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
          ) {
            Text(
              text = "$wordCount words • ${generatedReportText.length} chars",
              fontSize = 10.sp,
              color = TextMutedDark,
              fontFamily = FontFamily.Monospace,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
          }
        }

        // Live Text Preview Area
        Box(
          modifier = Modifier
            .weight(1f)
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SlateDarker)
            .border(1.dp, SlateBorder, RoundedCornerShape(12.dp))
            .padding(12.dp)
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .verticalScroll(rememberScrollState())
              .testTag("report_text_preview_scroll")
          ) {
            Text(
              text = generatedReportText,
              fontFamily = FontFamily.Monospace,
              fontSize = 11.5.sp,
              color = TextPrimaryDark,
              lineHeight = 17.sp
            )
          }
        }

        // Action Buttons Row
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Copy Button
          Button(
            onClick = {
              val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
              val clip = ClipData.newPlainText("Career Strategy & Pipeline Report", generatedReportText)
              clipboard.setPrimaryClip(clip)
              isCopied = true
              Toast.makeText(context, "Report copied to clipboard!", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier
              .weight(1f)
              .height(48.dp)
              .testTag("copy_report_clipboard_button"),
            colors = ButtonDefaults.buttonColors(
              containerColor = if (isCopied) TitanEmerald else TitanCyan,
              contentColor = ObsidianDark
            ),
            shape = RoundedCornerShape(12.dp)
          ) {
            Icon(
              imageVector = if (isCopied) Icons.Default.Check else Icons.Default.ContentCopy,
              contentDescription = null,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = if (isCopied) "Copied!" else "Copy to Clipboard",
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp
            )
          }

          // Share Button
          OutlinedButton(
            onClick = {
              val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(
                  Intent.EXTRA_SUBJECT,
                  "Executive Career Strategy & Job Pipeline Report - $candidateName"
                )
                putExtra(Intent.EXTRA_TEXT, generatedReportText)
              }
              context.startActivity(Intent.createChooser(shareIntent, "Share Career Report via"))
            },
            modifier = Modifier
              .weight(1f)
              .height(48.dp)
              .testTag("share_report_button"),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = TitanCyan),
            border = androidx.compose.foundation.BorderStroke(1.dp, TitanCyan.copy(alpha = 0.6f)),
            shape = RoundedCornerShape(12.dp)
          ) {
            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Share Report", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
          }
        }
      }
    }
  }
}
