package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.CareerNoteSummaryResult
import com.example.ui.theme.ObsidianDark
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateCard
import com.example.ui.theme.SlateElevated
import com.example.ui.theme.TextMutedDark
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TitanCyan
import com.example.ui.theme.TitanEmerald
import com.example.ui.theme.TitanGold
import com.example.ui.theme.TitanViolet

@Composable
fun CareerNoteSummaryDialog(
  summary: CareerNoteSummaryResult,
  onDismiss: () -> Unit,
  onSaveAsNewNote: ((title: String, content: String, company: String, role: String) -> Unit)? = null
) {
  val clipboardManager = LocalClipboardManager.current
  var isCopied by remember { mutableStateOf(false) }
  var isSaved by remember { mutableStateOf(false) }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      shape = RoundedCornerShape(16.dp),
      color = ObsidianDark,
      border = androidx.compose.foundation.BorderStroke(1.dp, TitanGold.copy(alpha = 0.6f)),
      modifier = Modifier
        .fillMaxWidth(0.95f)
        .widthIn(max = 680.dp)
        .padding(vertical = 24.dp)
        .testTag("career_note_summary_dialog")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp)
          .verticalScroll(rememberScrollState())
      ) {
        // Header Row
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(TitanGold.copy(alpha = 0.15f))
                .border(1.dp, TitanGold.copy(alpha = 0.4f), CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = TitanGold,
                modifier = Modifier.size(20.dp)
              )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(
                text = "Executive Career Memo Summary",
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimaryDark,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "Synthesized via Quick Action AI Automation",
                style = MaterialTheme.typography.bodySmall,
                color = TitanGold,
                fontSize = 11.sp
              )
            }
          }

          IconButton(
            onClick = onDismiss,
            modifier = Modifier.testTag("close_summary_dialog_btn")
          ) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMutedDark)
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Target Note & Confidence Badge Row
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(SlateElevated)
              .border(1.dp, SlateBorder, RoundedCornerShape(6.dp))
              .padding(horizontal = 10.dp, vertical = 4.dp)
          ) {
            Text(
              text = "Target: ${summary.noteTitle}",
              color = TextPrimaryDark,
              fontSize = 11.sp,
              fontWeight = FontWeight.Medium
            )
          }

          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(TitanEmerald.copy(alpha = 0.15f))
              .border(1.dp, TitanEmerald.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
              .padding(horizontal = 8.dp, vertical = 4.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                Icons.Default.CheckCircle,
                contentDescription = null,
                tint = TitanEmerald,
                modifier = Modifier.size(12.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "${summary.confidenceScore}% Grounded Confidence",
                color = TitanEmerald,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Executive Strategic Brief Card
        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = SlateCard),
          border = androidx.compose.foundation.BorderStroke(1.dp, TitanCyan.copy(alpha = 0.4f)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Lightbulb, contentDescription = null, tint = TitanCyan, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "EXECUTIVE STRATEGIC BRIEF",
                style = MaterialTheme.typography.labelSmall,
                color = TitanCyan,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
              )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = summary.executiveBrief,
              style = MaterialTheme.typography.bodyMedium,
              color = TextPrimaryDark,
              lineHeight = 20.sp
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Key Takeaways Section
        Text(
          text = "KEY TACTICAL TAKEAWAYS",
          style = MaterialTheme.typography.labelMedium,
          color = TitanGold,
          fontWeight = FontWeight.Bold,
          fontSize = 11.sp
        )
        Spacer(modifier = Modifier.height(6.dp))
        summary.keyTakeaways.forEach { takeaway ->
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 3.dp),
            verticalAlignment = Alignment.Top
          ) {
            Icon(
              Icons.Default.CheckCircle,
              contentDescription = null,
              tint = TitanGold,
              modifier = Modifier
                .padding(top = 2.dp)
                .size(14.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = takeaway,
              style = MaterialTheme.typography.bodySmall,
              color = TextPrimaryDark.copy(alpha = 0.9f),
              fontSize = 12.sp,
              lineHeight = 18.sp
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Action Items Section
        Text(
          text = "STRATEGIC NEXT STEPS",
          style = MaterialTheme.typography.labelMedium,
          color = TitanEmerald,
          fontWeight = FontWeight.Bold,
          fontSize = 11.sp
        )
        Spacer(modifier = Modifier.height(6.dp))
        summary.strategicActionItems.forEachIndexed { index, action ->
          Card(
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = SlateElevated),
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 3.dp)
          ) {
            Row(
              modifier = Modifier.padding(10.dp),
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
                  text = "${index + 1}",
                  color = TitanEmerald,
                  fontWeight = FontWeight.Bold,
                  fontSize = 10.sp
                )
              }
              Spacer(modifier = Modifier.width(10.dp))
              Text(
                text = action,
                style = MaterialTheme.typography.bodySmall,
                color = TextPrimaryDark,
                fontSize = 12.sp
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Interview Talking Points
        Text(
          text = "INTERVIEW TALKING POINTS & VALUE VECTORS",
          style = MaterialTheme.typography.labelMedium,
          color = TitanViolet,
          fontWeight = FontWeight.Bold,
          fontSize = 11.sp
        )
        Spacer(modifier = Modifier.height(6.dp))
        summary.interviewTalkingPoints.forEach { point ->
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 3.dp),
            verticalAlignment = Alignment.Top
          ) {
            Icon(
              Icons.Default.FormatQuote,
              contentDescription = null,
              tint = TitanViolet,
              modifier = Modifier
                .padding(top = 2.dp)
                .size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = point,
              style = MaterialTheme.typography.bodySmall,
              color = TextPrimaryDark.copy(alpha = 0.85f),
              fontSize = 12.sp,
              fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
            )
          }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Actions Row
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          OutlinedButton(
            onClick = {
              val formattedText = buildString {
                appendLine("=== EXECUTIVE SUMMARY: ${summary.noteTitle} ===")
                appendLine("Target: ${summary.targetCompany} | ${summary.targetRole}")
                appendLine()
                appendLine("EXECUTIVE BRIEF:")
                appendLine(summary.executiveBrief)
                appendLine()
                appendLine("KEY TAKEAWAYS:")
                summary.keyTakeaways.forEach { appendLine("• $it") }
                appendLine()
                appendLine("ACTION ITEMS:")
                summary.strategicActionItems.forEachIndexed { i, act -> appendLine("${i + 1}. $act") }
                appendLine()
                appendLine("TALKING POINTS:")
                summary.interviewTalkingPoints.forEach { appendLine("• $it") }
              }
              clipboardManager.setText(AnnotatedString(formattedText))
              isCopied = true
            },
            colors = ButtonDefaults.outlinedButtonColors(contentColor = TitanCyan),
            border = androidx.compose.foundation.BorderStroke(1.dp, TitanCyan),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
              .weight(1f)
              .height(44.dp)
              .testTag("copy_summary_btn")
          ) {
            Icon(
              if (isCopied) Icons.Default.CheckCircle else Icons.Default.ContentCopy,
              contentDescription = null,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(if (isCopied) "Copied!" else "Copy Summary", fontSize = 12.sp, fontWeight = FontWeight.Bold)
          }

          if (onSaveAsNewNote != null) {
            Button(
              onClick = {
                val memoContent = buildString {
                  appendLine("=== EXECUTIVE AI SUMMARY ===")
                  appendLine(summary.executiveBrief)
                  appendLine()
                  appendLine("TAKEAWAYS:")
                  summary.keyTakeaways.forEach { appendLine("• $it") }
                  appendLine()
                  appendLine("NEXT STEPS:")
                  summary.strategicActionItems.forEachIndexed { i, a -> appendLine("${i + 1}. $a") }
                  appendLine()
                  appendLine("TALKING POINTS:")
                  summary.interviewTalkingPoints.forEach { appendLine("• $it") }
                }
                onSaveAsNewNote(
                  "Summary: ${summary.noteTitle}",
                  memoContent,
                  summary.targetCompany,
                  summary.targetRole
                )
                isSaved = true
              },
              colors = ButtonDefaults.buttonColors(containerColor = if (isSaved) TitanEmerald else TitanGold),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier
                .weight(1f)
                .height(44.dp)
                .testTag("save_summary_memo_btn")
            ) {
              Icon(
                if (isSaved) Icons.Default.TaskAlt else Icons.Default.PushPin,
                contentDescription = null,
                tint = ObsidianDark,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = if (isSaved) "Saved Memo!" else "Save as Memo",
                color = ObsidianDark,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }
      }
    }
  }
}
