package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import com.example.data.model.CareerStrategyNote
import com.example.ui.components.SpeechDictationButton
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CareerStrategyNotesVaultCard(
  notes: List<CareerStrategyNote>,
  onSaveNote: (title: String, content: String, category: String, targetCompany: String, targetRole: String, tags: String) -> Unit,
  onUpdateNote: (CareerStrategyNote) -> Unit,
  onDeleteNote: (String) -> Unit,
  onTogglePin: (String, Boolean) -> Unit,
  modifier: Modifier = Modifier
) {
  var isAddDialogOpen by remember { mutableStateOf(false) }
  var selectedCategoryFilter by remember { mutableStateOf("ALL") }
  var searchQuery by remember { mutableStateOf("") }

  val categories = listOf("ALL", "TARGET_ROLES", "INTERVIEW_PREP", "NEGOTIATION", "STRATEGY", "NETWORKING")

  val filteredNotes = notes.filter { note ->
    val matchesCategory = if (selectedCategoryFilter == "ALL") true else note.category.equals(selectedCategoryFilter, ignoreCase = true)
    val matchesSearch = if (searchQuery.isBlank()) true else {
      note.title.contains(searchQuery, ignoreCase = true) ||
      note.content.contains(searchQuery, ignoreCase = true) ||
      note.tags.contains(searchQuery, ignoreCase = true) ||
      note.targetCompany.contains(searchQuery, ignoreCase = true)
    }
    matchesCategory && matchesSearch
  }

  if (isAddDialogOpen) {
    AddStrategyNoteDialog(
      onDismiss = { isAddDialogOpen = false },
      onConfirm = { title, content, cat, comp, role, tags ->
        onSaveNote(title, content, cat, comp, role, tags)
        isAddDialogOpen = false
      }
    )
  }

  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("career_strategy_notes_vault_card"),
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = SlateCard),
    border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      // Header with offline indicator
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              Icons.Default.Shield,
              contentDescription = null,
              tint = TitanEmerald,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              "LOCAL STRATEGY NOTES VAULT",
              style = MaterialTheme.typography.labelSmall,
              color = TitanEmerald,
              fontWeight = FontWeight.Bold,
              fontSize = 11.sp
            )
          }
          Text(
            "Room SQLite Encrypted · 100% Offline Accessible",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondaryDark,
            fontSize = 10.sp
          )
        }

        Button(
          onClick = { isAddDialogOpen = true },
          colors = ButtonDefaults.buttonColors(containerColor = TitanEmerald, contentColor = ObsidianDark),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier.testTag("add_strategy_note_button")
        ) {
          Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Add Note", fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
      }

      // Search field
      OutlinedTextField(
        value = searchQuery,
        onValueChange = { searchQuery = it },
        placeholder = { Text("Search offline notes, tactics, battle-plans...", color = TextMutedDark, fontSize = 12.sp) },
        modifier = Modifier
          .fillMaxWidth()
          .testTag("search_strategy_notes_input"),
        shape = RoundedCornerShape(8.dp),
        colors = OutlinedTextFieldDefaults.colors(
          focusedContainerColor = SlateElevated,
          unfocusedContainerColor = SlateElevated,
          focusedBorderColor = TitanEmerald,
          unfocusedBorderColor = SlateBorder,
          focusedTextColor = TextPrimaryDark,
          unfocusedTextColor = TextPrimaryDark
        ),
        singleLine = true
      )

      // Category filter chips
      ScrollableTabRow(
        selectedTabIndex = categories.indexOf(selectedCategoryFilter).coerceAtLeast(0),
        containerColor = Color.Transparent,
        contentColor = TitanCyan,
        edgePadding = 0.dp,
        indicator = { tabPositions ->
          val index = categories.indexOf(selectedCategoryFilter).coerceAtLeast(0)
          if (index < tabPositions.size) {
            TabRowDefaults.SecondaryIndicator(
              Modifier.tabIndicatorOffset(tabPositions[index]),
              color = TitanEmerald,
              height = 2.dp
            )
          }
        },
        divider = {}
      ) {
        categories.forEach { cat ->
          val isSelected = selectedCategoryFilter == cat
          Tab(
            selected = isSelected,
            onClick = { selectedCategoryFilter = cat },
            text = {
              Text(
                cat.replace("_", " "),
                color = if (isSelected) TitanEmerald else TextSecondaryDark,
                fontSize = 10.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
              )
            }
          )
        }
      }

      // Notes List
      if (filteredNotes.isEmpty()) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(SlateElevated)
            .padding(20.dp),
          contentAlignment = Alignment.Center
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("No strategy notes found in local vault", style = MaterialTheme.typography.bodyMedium, color = TextSecondaryDark)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Tap '+ Add Note' above to record interview tactics or salary targets offline.", style = MaterialTheme.typography.bodySmall, color = TextMutedDark, fontSize = 10.sp)
          }
        }
      } else {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          filteredNotes.forEach { note ->
            StrategyNoteItem(
              note = note,
              onTogglePin = { onTogglePin(note.id, note.isPinned) },
              onDelete = { onDeleteNote(note.id) }
            )
          }
        }
      }
    }
  }
}

@Composable
fun StrategyNoteItem(
  note: CareerStrategyNote,
  onTogglePin: () -> Unit,
  onDelete: () -> Unit
) {
  var isExpanded by remember { mutableStateOf(false) }

  val categoryColor = when (note.category.uppercase()) {
    "NEGOTIATION" -> TitanGold
    "INTERVIEW_PREP" -> TitanEmerald
    "TARGET_ROLES" -> TitanCyan
    else -> TitanCyan
  }

  val formattedDate = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(note.updatedAt))

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("strategy_note_item_${note.id}"),
    shape = RoundedCornerShape(8.dp),
    colors = CardDefaults.cardColors(containerColor = SlateElevated),
    border = androidx.compose.foundation.BorderStroke(
      1.dp,
      if (note.isPinned) TitanGold.copy(alpha = 0.6f) else SlateBorder
    )
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
          if (note.isPinned) {
            Icon(
              Icons.Filled.PushPin,
              contentDescription = "Pinned",
              tint = TitanGold,
              modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
          }

          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(4.dp))
              .background(categoryColor.copy(alpha = 0.15f))
              .border(1.dp, categoryColor.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
              .padding(horizontal = 6.dp, vertical = 2.dp)
          ) {
            Text(
              note.category.replace("_", " "),
              style = MaterialTheme.typography.labelSmall,
              color = categoryColor,
              fontSize = 9.sp,
              fontWeight = FontWeight.Bold
            )
          }

          Spacer(modifier = Modifier.width(6.dp))

          Text(
            formattedDate,
            style = MaterialTheme.typography.labelSmall,
            color = TextMutedDark,
            fontSize = 9.sp
          )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          IconButton(
            onClick = onTogglePin,
            modifier = Modifier.size(24.dp)
          ) {
            Icon(
              if (note.isPinned) Icons.Filled.PushPin else Icons.Outlined.PushPin,
              contentDescription = "Pin Note",
              tint = if (note.isPinned) TitanGold else TextSecondaryDark,
              modifier = Modifier.size(14.dp)
            )
          }

          IconButton(
            onClick = onDelete,
            modifier = Modifier.size(24.dp)
          ) {
            Icon(
              Icons.Default.Delete,
              contentDescription = "Delete Note",
              tint = TextMutedDark,
              modifier = Modifier.size(14.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(6.dp))

      Text(
        text = note.title,
        style = MaterialTheme.typography.titleSmall,
        color = TextPrimaryDark,
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp
      )

      if (note.targetCompany.isNotBlank() || note.targetRole.isNotBlank()) {
        Spacer(modifier = Modifier.height(4.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          if (note.targetCompany.isNotBlank()) {
            Text(
              "🏢 ${note.targetCompany}",
              style = MaterialTheme.typography.labelSmall,
              color = TextSecondaryDark,
              fontSize = 10.sp
            )
          }
          if (note.targetRole.isNotBlank()) {
            Text(
              "🎯 ${note.targetRole}",
              style = MaterialTheme.typography.labelSmall,
              color = TitanCyan,
              fontSize = 10.sp
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(6.dp))

      Text(
        text = note.content,
        style = MaterialTheme.typography.bodySmall,
        color = TextSecondaryDark,
        fontSize = 11.sp,
        maxLines = if (isExpanded) Int.MAX_VALUE else 3
      )

      if (note.content.length > 120) {
        Spacer(modifier = Modifier.height(4.dp))
        Row(
          modifier = Modifier
            .clickable { isExpanded = !isExpanded }
            .padding(vertical = 2.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            if (isExpanded) "Show Less" else "Read Full Strategy Note",
            style = MaterialTheme.typography.labelSmall,
            color = TitanEmerald,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp
          )
          Icon(
            if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
            contentDescription = null,
            tint = TitanEmerald,
            modifier = Modifier.size(12.dp)
          )
        }
      }

      if (note.tags.isNotBlank()) {
        Spacer(modifier = Modifier.height(6.dp))
        Row(
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          note.tags.split(",").map { it.trim() }.filter { it.isNotBlank() }.forEach { tag ->
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(SlateCard)
                .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
              Text("#$tag", style = MaterialTheme.typography.labelSmall, color = TextMutedDark, fontSize = 9.sp)
            }
          }
        }
      }
    }
  }
}

@Composable
fun AddStrategyNoteDialog(
  onDismiss: () -> Unit,
  onConfirm: (title: String, content: String, category: String, targetCompany: String, targetRole: String, tags: String) -> Unit
) {
  var title by remember { mutableStateOf("") }
  var content by remember { mutableStateOf("") }
  var category by remember { mutableStateOf("TARGET_ROLES") }
  var targetCompany by remember { mutableStateOf("") }
  var targetRole by remember { mutableStateOf("") }
  var tags by remember { mutableStateOf("") }

  val categories = listOf("TARGET_ROLES", "INTERVIEW_PREP", "NEGOTIATION", "STRATEGY", "NETWORKING")

  Dialog(onDismissRequest = onDismiss) {
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      shape = RoundedCornerShape(12.dp),
      colors = CardDefaults.cardColors(containerColor = SlateCard),
      border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Shield, contentDescription = null, tint = TitanEmerald, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            "NEW OFFLINE STRATEGY NOTE",
            style = MaterialTheme.typography.titleSmall,
            color = TextPrimaryDark,
            fontWeight = FontWeight.Bold
          )
        }

        OutlinedTextField(
          value = title,
          onValueChange = { title = it },
          label = { Text("Note Title (e.g. Zepto Unit Economics Angle)") },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("strategy_note_title_input"),
          shape = RoundedCornerShape(8.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = TextPrimaryDark,
            unfocusedTextColor = TextPrimaryDark
          ),
          singleLine = true,
          trailingIcon = {
            SpeechDictationButton(
              currentText = title,
              onTextAppended = { title = it },
              targetFieldLabel = "Title",
              testTag = "dictate_strategy_title_mic_btn"
            )
          }
        )

        // Category selection
        Text("Category", style = MaterialTheme.typography.labelSmall, color = TextSecondaryDark)
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          categories.take(3).forEach { cat ->
            val isSel = category == cat
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(if (isSel) TitanEmerald.copy(alpha = 0.2f) else SlateElevated)
                .border(1.dp, if (isSel) TitanEmerald else SlateBorder, RoundedCornerShape(6.dp))
                .clickable { category = cat }
                .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
              Text(
                cat.replace("_", " "),
                style = MaterialTheme.typography.labelSmall,
                color = if (isSel) TitanEmerald else TextSecondaryDark,
                fontSize = 9.sp
              )
            }
          }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          categories.drop(3).forEach { cat ->
            val isSel = category == cat
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(if (isSel) TitanEmerald.copy(alpha = 0.2f) else SlateElevated)
                .border(1.dp, if (isSel) TitanEmerald else SlateBorder, RoundedCornerShape(6.dp))
                .clickable { category = cat }
                .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
              Text(
                cat.replace("_", " "),
                style = MaterialTheme.typography.labelSmall,
                color = if (isSel) TitanEmerald else TextSecondaryDark,
                fontSize = 9.sp
              )
            }
          }
        }

        OutlinedTextField(
          value = targetCompany,
          onValueChange = { targetCompany = it },
          label = { Text("Target Company (optional)") },
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(8.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = TextPrimaryDark,
            unfocusedTextColor = TextPrimaryDark
          ),
          singleLine = true
        )

        OutlinedTextField(
          value = content,
          onValueChange = { content = it },
          label = { Text("Content / Strategic Memo / Talking Points") },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("strategy_note_content_input"),
          shape = RoundedCornerShape(8.dp),
          minLines = 4,
          colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = TextPrimaryDark,
            unfocusedTextColor = TextPrimaryDark
          ),
          trailingIcon = {
            SpeechDictationButton(
              currentText = content,
              onTextAppended = { content = it },
              targetFieldLabel = "Content Memo",
              testTag = "dictate_strategy_content_mic_btn"
            )
          }
        )

        OutlinedTextField(
          value = tags,
          onValueChange = { tags = it },
          label = { Text("Tags (comma-separated, e.g. ₹16L+, BLR, STAR)") },
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(8.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = TextPrimaryDark,
            unfocusedTextColor = TextPrimaryDark
          ),
          singleLine = true
        )

        Spacer(modifier = Modifier.height(6.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.End,
          verticalAlignment = Alignment.CenterVertically
        ) {
          OutlinedButton(
            onClick = onDismiss,
            shape = RoundedCornerShape(8.dp)
          ) {
            Text("Cancel", color = TextSecondaryDark)
          }

          Spacer(modifier = Modifier.width(8.dp))

          Button(
            onClick = {
              if (title.isNotBlank() && content.isNotBlank()) {
                onConfirm(title, content, category, targetCompany, targetRole, tags)
              }
            },
            enabled = title.isNotBlank() && content.isNotBlank(),
            colors = ButtonDefaults.buttonColors(containerColor = TitanEmerald, contentColor = ObsidianDark),
            shape = RoundedCornerShape(8.dp)
          ) {
            Text("Save to Vault", fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}
