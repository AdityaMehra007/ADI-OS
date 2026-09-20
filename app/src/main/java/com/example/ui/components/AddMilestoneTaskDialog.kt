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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
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

@Composable
fun AddMilestoneTaskDialog(
  milestoneTitle: String,
  milestoneId: String,
  onDismiss: () -> Unit,
  onConfirm: (
    milestoneId: String,
    title: String,
    description: String,
    category: String,
    weight: Int,
    dueDate: String
  ) -> Unit
) {
  var title by remember { mutableStateOf("") }
  var description by remember { mutableStateOf("") }
  var category by remember { mutableStateOf("EXECUTION") }
  var weight by remember { mutableStateOf(2) }
  var dueDate by remember { mutableStateOf("") }

  val categories = listOf(
    "EXECUTION" to "Execution",
    "SKILLS" to "Skills",
    "PORTFOLIO" to "Portfolio",
    "NETWORKING" to "Networking",
    "INTERVIEW" to "Interview"
  )

  Dialog(onDismissRequest = onDismiss) {
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .testTag("add_milestone_task_dialog"),
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = SlateCard),
      border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp)
      ) {
        // Header
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
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = TitanCyan,
                modifier = Modifier.size(18.dp)
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "Add Action Item",
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimaryDark,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = milestoneTitle.take(30) + if (milestoneTitle.length > 30) "..." else "",
                style = MaterialTheme.typography.labelSmall,
                color = TitanCyan,
                fontSize = 11.sp
              )
            }
          }

          IconButton(
            onClick = onDismiss,
            modifier = Modifier.testTag("close_task_dialog")
          ) {
            Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextMutedDark)
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Task Title
        Text(text = "Task Title", style = MaterialTheme.typography.labelMedium, color = TextPrimaryDark, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
          value = title,
          onValueChange = { title = it },
          placeholder = { Text("e.g. Master SQL Cohort Retention Analysis", color = TextMutedDark, fontSize = 12.sp) },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("task_title_input"),
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

        // Category Pills
        Text(text = "Category", style = MaterialTheme.typography.labelMedium, color = TextPrimaryDark, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(6.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          categories.forEach { (catKey, label) ->
            val isSelected = category == catKey
            Box(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(6.dp))
                .background(if (isSelected) TitanCyan.copy(alpha = 0.25f) else SlateElevated)
                .border(1.dp, if (isSelected) TitanCyan else SlateBorder, RoundedCornerShape(6.dp))
                .clickable { category = catKey }
                .padding(vertical = 6.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = label.take(6),
                style = MaterialTheme.typography.labelSmall,
                color = if (isSelected) TitanCyan else TextSecondaryDark,
                fontSize = 10.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Weight points & Due Date
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(text = "Weight Points (1-5)", style = MaterialTheme.typography.labelMedium, color = TextPrimaryDark, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
              listOf(1, 2, 3, 4, 5).forEach { w ->
                val isSelected = weight == w
                Box(
                  modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (isSelected) TitanGold.copy(alpha = 0.25f) else SlateElevated)
                    .border(1.dp, if (isSelected) TitanGold else SlateBorder, RoundedCornerShape(6.dp))
                    .clickable { weight = w },
                  contentAlignment = Alignment.Center
                ) {
                  Text(
                    text = "$w",
                    style = MaterialTheme.typography.labelMedium,
                    color = if (isSelected) TitanGold else TextSecondaryDark,
                    fontWeight = FontWeight.Bold
                  )
                }
              }
            }
          }

          Column(modifier = Modifier.weight(1f)) {
            Text(text = "Target Date", style = MaterialTheme.typography.labelMedium, color = TextPrimaryDark, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
              value = dueDate,
              onValueChange = { dueDate = it },
              placeholder = { Text("YYYY-MM-DD", color = TextMutedDark, fontSize = 11.sp) },
              modifier = Modifier
                .fillMaxWidth()
                .testTag("task_due_date_input"),
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
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Description
        Text(text = "Tactical Notes (Optional)", style = MaterialTheme.typography.labelMedium, color = TextPrimaryDark, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
          value = description,
          onValueChange = { description = it },
          placeholder = { Text("Specific steps or deliverables for this task...", color = TextMutedDark, fontSize = 12.sp) },
          modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .testTag("task_desc_input"),
          shape = RoundedCornerShape(8.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = TitanCyan,
            unfocusedBorderColor = SlateBorder,
            focusedContainerColor = ObsidianDark,
            unfocusedContainerColor = ObsidianDark,
            focusedTextColor = TextPrimaryDark,
            unfocusedTextColor = TextPrimaryDark
          ),
          maxLines = 2
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Buttons
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.End,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Button(
            onClick = onDismiss,
            colors = ButtonDefaults.buttonColors(containerColor = SlateElevated),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.testTag("cancel_task_button")
          ) {
            Text(text = "Cancel", color = TextSecondaryDark)
          }

          Spacer(modifier = Modifier.width(10.dp))

          Button(
            onClick = {
              if (title.isNotBlank()) {
                onConfirm(
                  milestoneId,
                  title.trim(),
                  description.trim(),
                  category,
                  weight,
                  dueDate.trim()
                )
              }
            },
            enabled = title.isNotBlank(),
            colors = ButtonDefaults.buttonColors(containerColor = TitanCyan),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.testTag("save_task_button")
          ) {
            Text(text = "Add Task", color = ObsidianDark, fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}
