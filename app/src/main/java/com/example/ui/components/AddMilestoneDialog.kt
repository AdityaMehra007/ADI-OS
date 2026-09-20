package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.ObsidianDark
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateCard
import com.example.ui.theme.SlateElevated
import com.example.ui.theme.TextMutedDark
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import com.example.ui.theme.TitanCyan
import com.example.ui.theme.TitanGold

data class MilestonePreset(
  val title: String,
  val horizon: String,
  val horizonYears: Float,
  val targetQuarter: String,
  val category: String,
  val targetMetric: String,
  val rationale: String,
  val priority: String
)

@Composable
fun AddMilestoneDialog(
  onDismiss: () -> Unit,
  onConfirm: (
    title: String,
    horizon: String,
    horizonYears: Float,
    targetQuarter: String,
    category: String,
    targetMetric: String,
    strategicRationale: String,
    priority: String
  ) -> Unit
) {
  var title by remember { mutableStateOf("") }
  var horizon by remember { mutableStateOf("1Y") }
  var horizonYears by remember { mutableStateOf(1.0f) }
  var targetQuarter by remember { mutableStateOf("2027-Q2") }
  var category by remember { mutableStateOf("ROLE_TRANSITION") }
  var targetMetric by remember { mutableStateOf("") }
  var strategicRationale by remember { mutableStateOf("") }
  var priority by remember { mutableStateOf("HIGH") }

  val presets = remember {
    listOf(
      MilestonePreset(
        title = "Director of Business Operations / Chief of Staff",
        horizon = "2Y",
        horizonYears = 2.0f,
        targetQuarter = "2028-Q2",
        category = "ROLE_TRANSITION",
        targetMetric = "₹35-45 LPA + Substantial Equity",
        rationale = "Lead strategic corporate initiatives reporting to founders/C-suite.",
        priority = "CRITICAL"
      ),
      MilestonePreset(
        title = "P&L General Manager / VP of Operations",
        horizon = "3Y",
        horizonYears = 3.0f,
        targetQuarter = "2029-Q2",
        category = "LEADERSHIP",
        targetMetric = "₹60L+ Cash & Equity • ₹100Cr+ Revenue Responsibility",
        rationale = "Direct P&L and multi-city team leadership.",
        priority = "HIGH"
      ),
      MilestonePreset(
        title = "AI Enterprise Co-Founder / Angel Operator",
        horizon = "5Y",
        horizonYears = 5.0f,
        targetQuarter = "2031-Q2",
        category = "VENTURE_EQUITY",
        targetMetric = "₹2.5Cr+ Equity Net Worth • Tier-1 Backed Startup",
        rationale = "Generational wealth creation through venture equity and angel investments.",
        priority = "STRATEGIC"
      )
    )
  }

  val horizons = listOf(
    Triple("6M", 0.5f, "6-Month Sprint"),
    Triple("1Y", 1.0f, "1-Year Horizon"),
    Triple("2Y", 2.0f, "2-Year Growth"),
    Triple("3Y", 3.0f, "3-Year Expansion"),
    Triple("5Y", 5.0f, "5-Year Vision")
  )

  val categories = listOf(
    "ROLE_TRANSITION" to "Role Transition",
    "LEADERSHIP" to "Leadership",
    "COMPENSATION" to "Compensation",
    "TECHNICAL_MASTERY" to "Technical Mastery",
    "VENTURE_EQUITY" to "Venture Equity"
  )

  val priorities = listOf("CRITICAL", "HIGH", "STRATEGIC")

  Dialog(onDismissRequest = onDismiss) {
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .testTag("add_milestone_dialog"),
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = SlateCard),
      border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp)
          .verticalScroll(rememberScrollState())
      ) {
        // Dialog Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(32.dp)
                .background(TitanCyan.copy(alpha = 0.2f), RoundedCornerShape(8.dp)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Flag,
                contentDescription = null,
                tint = TitanCyan,
                modifier = Modifier.size(18.dp)
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "Define Career Milestone",
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimaryDark,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "Establish long-term velocity & pacing targets",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondaryDark,
                fontSize = 11.sp
              )
            }
          }

          IconButton(
            onClick = onDismiss,
            modifier = Modifier.testTag("close_add_milestone_dialog")
          ) {
            Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextMutedDark)
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Executive Presets
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(imageVector = Icons.Default.Lightbulb, contentDescription = null, tint = TitanGold, modifier = Modifier.size(14.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "QUICK EXECUTIVE PRESETS",
            style = MaterialTheme.typography.labelSmall,
            color = TitanGold,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp
          )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          presets.forEach { preset ->
            Box(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(8.dp))
                .background(SlateElevated)
                .border(1.dp, SlateBorder, RoundedCornerShape(8.dp))
                .clickable {
                  title = preset.title
                  horizon = preset.horizon
                  horizonYears = preset.horizonYears
                  targetQuarter = preset.targetQuarter
                  category = preset.category
                  targetMetric = preset.targetMetric
                  strategicRationale = preset.rationale
                  priority = preset.priority
                }
                .padding(horizontal = 8.dp, vertical = 6.dp)
                .testTag("preset_${preset.horizon.lowercase()}"),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = preset.horizon,
                style = MaterialTheme.typography.labelSmall,
                color = TitanCyan,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Title Field
        Text(text = "Milestone Title", style = MaterialTheme.typography.labelMedium, color = TextPrimaryDark, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
          value = title,
          onValueChange = { title = it },
          placeholder = { Text("e.g. Lead Operations Pod / Director of BizOps", color = TextMutedDark, fontSize = 13.sp) },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("milestone_title_input"),
          shape = RoundedCornerShape(8.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = TitanCyan,
            unfocusedBorderColor = SlateBorder,
            focusedContainerColor = ObsidianDark,
            unfocusedContainerColor = ObsidianDark,
            focusedTextColor = TextPrimaryDark,
            unfocusedTextColor = TextPrimaryDark
          ),
          singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Horizon Picker
        Text(text = "Time Horizon", style = MaterialTheme.typography.labelMedium, color = TextPrimaryDark, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(6.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          horizons.forEach { (h, y, _) ->
            val isSelected = horizon == h
            Box(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(8.dp))
                .background(if (isSelected) TitanCyan.copy(alpha = 0.25f) else SlateElevated)
                .border(1.dp, if (isSelected) TitanCyan else SlateBorder, RoundedCornerShape(8.dp))
                .clickable {
                  horizon = h
                  horizonYears = y
                }
                .padding(vertical = 8.dp)
                .testTag("horizon_pill_$h"),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = h,
                style = MaterialTheme.typography.labelSmall,
                color = if (isSelected) TitanCyan else TextSecondaryDark,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Target Quarter & Metric
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(text = "Target Quarter", style = MaterialTheme.typography.labelMedium, color = TextPrimaryDark, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
              value = targetQuarter,
              onValueChange = { targetQuarter = it },
              placeholder = { Text("e.g. 2027-Q2", color = TextMutedDark, fontSize = 12.sp) },
              modifier = Modifier
                .fillMaxWidth()
                .testTag("milestone_target_quarter_input"),
              shape = RoundedCornerShape(8.dp),
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = TitanCyan,
                unfocusedBorderColor = SlateBorder,
                focusedContainerColor = ObsidianDark,
                unfocusedContainerColor = ObsidianDark,
                focusedTextColor = TextPrimaryDark,
                unfocusedTextColor = TextPrimaryDark
              ),
              singleLine = true
            )
          }

          Column(modifier = Modifier.weight(1f)) {
            Text(text = "Priority Level", style = MaterialTheme.typography.labelMedium, color = TextPrimaryDark, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(4.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              priorities.forEach { p ->
                val isSelected = priority == p
                Box(
                  modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isSelected) TitanGold.copy(alpha = 0.2f) else SlateElevated)
                    .border(1.dp, if (isSelected) TitanGold else SlateBorder, RoundedCornerShape(8.dp))
                    .clickable { priority = p }
                    .padding(2.dp),
                  contentAlignment = Alignment.Center
                ) {
                  Text(
                    text = p.take(4),
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isSelected) TitanGold else TextSecondaryDark,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                  )
                }
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Target Metric Field
        Text(text = "Target Success Metric / Deliverable", style = MaterialTheme.typography.labelMedium, color = TextPrimaryDark, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
          value = targetMetric,
          onValueChange = { targetMetric = it },
          placeholder = { Text("e.g. ₹28 LPA CTC + Direct ownership of ₹15Cr line", color = TextMutedDark, fontSize = 12.sp) },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("milestone_metric_input"),
          shape = RoundedCornerShape(8.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = TitanCyan,
            unfocusedBorderColor = SlateBorder,
            focusedContainerColor = ObsidianDark,
            unfocusedContainerColor = ObsidianDark,
            focusedTextColor = TextPrimaryDark,
            unfocusedTextColor = TextPrimaryDark
          ),
          singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Strategic Rationale Field
        Text(text = "Strategic Rationale & Career Capital", style = MaterialTheme.typography.labelMedium, color = TextPrimaryDark, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
          value = strategicRationale,
          onValueChange = { strategicRationale = it },
          placeholder = { Text("Why does this milestone multiply your long-term career optionality?", color = TextMutedDark, fontSize = 12.sp) },
          modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .testTag("milestone_rationale_input"),
          shape = RoundedCornerShape(8.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = TitanCyan,
            unfocusedBorderColor = SlateBorder,
            focusedContainerColor = ObsidianDark,
            unfocusedContainerColor = ObsidianDark,
            focusedTextColor = TextPrimaryDark,
            unfocusedTextColor = TextPrimaryDark
          ),
          maxLines = 3
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Action Buttons
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.End,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Button(
            onClick = onDismiss,
            colors = ButtonDefaults.buttonColors(containerColor = SlateElevated),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.testTag("cancel_milestone_button")
          ) {
            Text(text = "Cancel", color = TextSecondaryDark)
          }

          Spacer(modifier = Modifier.width(12.dp))

          Button(
            onClick = {
              if (title.isNotBlank()) {
                onConfirm(
                  title.trim(),
                  horizon,
                  horizonYears,
                  targetQuarter.trim(),
                  category,
                  if (targetMetric.isNotBlank()) targetMetric.trim() else "Target Established",
                  if (strategicRationale.isNotBlank()) strategicRationale.trim() else "Career leverage acceleration.",
                  priority
                )
              }
            },
            enabled = title.isNotBlank(),
            colors = ButtonDefaults.buttonColors(containerColor = TitanCyan),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.testTag("save_milestone_button")
          ) {
            Text(text = "Save Milestone", color = ObsidianDark, fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}
