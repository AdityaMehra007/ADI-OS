package com.example.ui.screens

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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import com.example.ui.components.SpeechDictationButton
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
import androidx.compose.ui.window.Dialog
import com.example.data.model.CareerNote
import com.example.ui.theme.ObsidianDark
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateCard
import com.example.ui.theme.SlateElevated
import com.example.ui.theme.TextMutedDark
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TitanCrimson
import com.example.ui.theme.TitanCyan
import com.example.ui.theme.TitanEmerald
import com.example.ui.theme.TitanGold
import com.example.ui.theme.TitanViolet
import com.example.ui.viewmodel.CareerNotesViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val CATEGORIES = listOf(
  "ALL",
  "STRATEGY",
  "INTERVIEW_PREP",
  "NEGOTIATION",
  "NETWORKING",
  "TARGET_ROLES",
  "GENERAL"
)

@Composable
fun CareerNotesScreen(
  viewModel: CareerNotesViewModel,
  modifier: Modifier = Modifier
) {
  val notes by viewModel.filteredNotes.collectAsState()
  val allNotes by viewModel.allNotes.collectAsState()
  val searchQuery by viewModel.searchQuery.collectAsState()
  val selectedCategory by viewModel.selectedCategory.collectAsState()

  var isAddDialogOpen by remember { mutableStateOf(false) }
  var noteToEdit by remember { mutableStateOf<CareerNote?>(null) }
  var noteToDelete by remember { mutableStateOf<CareerNote?>(null) }

  val pinnedCount = allNotes.count { it.isPinned }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(ObsidianDark)
      .testTag("career_notes_screen")
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp, vertical = 12.dp)
        .widthIn(max = 800.dp)
        .align(Alignment.TopCenter)
    ) {
      // Header
      CareerNotesHeader(
        totalNotes = allNotes.size,
        pinnedCount = pinnedCount,
        onAddClick = { isAddDialogOpen = true }
      )

      Spacer(modifier = Modifier.height(12.dp))

      // Search Bar
      OutlinedTextField(
        value = searchQuery,
        onValueChange = { viewModel.setSearchQuery(it) },
        placeholder = { Text("Search strategic memos, tags, battle-plans...", color = TextMutedDark) },
        leadingIcon = {
          Icon(Icons.Default.Search, contentDescription = "Search", tint = TitanCyan)
        },
        trailingIcon = {
          if (searchQuery.isNotEmpty()) {
            IconButton(
              onClick = { viewModel.setSearchQuery("") },
              modifier = Modifier.testTag("clear_notes_search_btn")
            ) {
              Icon(Icons.Default.Clear, contentDescription = "Clear", tint = TextMutedDark)
            }
          }
        },
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
          focusedTextColor = TextPrimaryDark,
          unfocusedTextColor = TextPrimaryDark,
          focusedBorderColor = TitanCyan,
          unfocusedBorderColor = SlateBorder,
          focusedContainerColor = SlateCard,
          unfocusedContainerColor = SlateCard
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("career_notes_search_input")
      )

      Spacer(modifier = Modifier.height(10.dp))

      // Categories Row
      CategoryFilterRow(
        categories = CATEGORIES,
        selectedCategory = selectedCategory,
        onCategorySelect = { viewModel.setCategory(it) }
      )

      Spacer(modifier = Modifier.height(12.dp))

      // Notes List or Empty State
      if (notes.isEmpty()) {
        EmptyNotesState(
          isFiltered = searchQuery.isNotBlank() || selectedCategory != "ALL",
          onResetFilter = {
            viewModel.setSearchQuery("")
            viewModel.setCategory("ALL")
          },
          onCreateNote = { isAddDialogOpen = true }
        )
      } else {
        LazyColumn(
          modifier = Modifier
            .fillMaxSize()
            .testTag("career_notes_list"),
          verticalArrangement = Arrangement.spacedBy(10.dp),
          contentPadding = PaddingValues(bottom = 80.dp)
        ) {
          items(notes, key = { it.id }) { note ->
            CareerNoteCard(
              note = note,
              onTogglePin = { viewModel.togglePin(note.id, note.isPinned) },
              onEdit = { noteToEdit = note },
              onDelete = { noteToDelete = note }
            )
          }
        }
      }
    }

    // Floating Action Button
    FloatingActionButton(
      onClick = { isAddDialogOpen = true },
      containerColor = TitanCyan,
      contentColor = ObsidianDark,
      shape = CircleShape,
      modifier = Modifier
        .align(Alignment.BottomEnd)
        .padding(20.dp)
        .testTag("add_career_note_fab")
    ) {
      Icon(Icons.Default.Add, contentDescription = "Add Career Note")
    }

    // Add Note Dialog
    if (isAddDialogOpen) {
      CareerNoteEditDialog(
        note = null,
        onDismiss = { isAddDialogOpen = false },
        onSave = { title, category, content, company, role, tags, isPinned ->
          viewModel.saveNote(title, category, content, company, role, tags, isPinned)
          isAddDialogOpen = false
        }
      )
    }

    // Edit Note Dialog
    noteToEdit?.let { note ->
      CareerNoteEditDialog(
        note = note,
        onDismiss = { noteToEdit = null },
        onSave = { title, category, content, company, role, tags, isPinned ->
          viewModel.updateNote(
            note.copy(
              title = title,
              category = category,
              content = content,
              targetCompany = company,
              targetRole = role,
              tags = tags,
              isPinned = isPinned
            )
          )
          noteToEdit = null
        }
      )
    }

    // Delete Confirmation Dialog
    noteToDelete?.let { note ->
      AlertDialog(
        onDismissRequest = { noteToDelete = null },
        title = { Text("Delete Strategic Note?", color = TextPrimaryDark) },
        text = {
          Text(
            "Are you sure you want to delete \"${note.title}\"? This action cannot be undone.",
            color = TextMutedDark
          )
        },
        confirmButton = {
          Button(
            onClick = {
              viewModel.deleteNote(note.id)
              noteToDelete = null
            },
            colors = ButtonDefaults.buttonColors(containerColor = TitanCrimson),
            modifier = Modifier.testTag("confirm_delete_note_btn")
          ) {
            Text("Delete", color = Color.White)
          }
        },
        dismissButton = {
          TextButton(onClick = { noteToDelete = null }) {
            Text("Cancel", color = TextMutedDark)
          }
        },
        containerColor = SlateCard,
        shape = RoundedCornerShape(14.dp)
      )
    }
  }
}

@Composable
private fun CareerNotesHeader(
  totalNotes: Int,
  pinnedCount: Int,
  onAddClick: () -> Unit
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Column {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.AutoMirrored.Filled.Notes,
          contentDescription = null,
          tint = TitanCyan,
          modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "Career Strategy Notes",
          style = MaterialTheme.typography.titleLarge,
          color = TextPrimaryDark,
          fontWeight = FontWeight.Bold
        )
      }
      Text(
        text = "$totalNotes notes stored offline in Room ($pinnedCount pinned)",
        style = MaterialTheme.typography.bodySmall,
        color = TextMutedDark
      )
    }

    OutlinedButton(
      onClick = onAddClick,
      border = androidx.compose.foundation.BorderStroke(1.dp, TitanCyan.copy(alpha = 0.8f)),
      colors = ButtonDefaults.outlinedButtonColors(contentColor = TitanCyan),
      shape = RoundedCornerShape(8.dp),
      contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
      modifier = Modifier.testTag("header_add_note_btn")
    ) {
      Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
      Spacer(modifier = Modifier.width(4.dp))
      Text("New Note", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
  }
}

@Composable
private fun CategoryFilterRow(
  categories: List<String>,
  selectedCategory: String,
  onCategorySelect: (String) -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .horizontalScroll(rememberScrollState()),
    horizontalArrangement = Arrangement.spacedBy(6.dp)
  ) {
    categories.forEach { cat ->
      val isSelected = selectedCategory.equals(cat, ignoreCase = true)
      val pillColor = getCategoryColor(cat)
      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(8.dp))
          .background(if (isSelected) pillColor.copy(alpha = 0.2f) else SlateCard)
          .border(
            1.dp,
            if (isSelected) pillColor else SlateBorder,
            RoundedCornerShape(8.dp)
          )
          .clickable { onCategorySelect(cat) }
          .padding(horizontal = 10.dp, vertical = 6.dp)
          .testTag("category_chip_${cat.lowercase()}"),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = cat.replace("_", " "),
          style = MaterialTheme.typography.labelSmall,
          color = if (isSelected) TextPrimaryDark else TextMutedDark,
          fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
          fontSize = 11.sp
        )
      }
    }
  }
}

@Composable
private fun CareerNoteCard(
  note: CareerNote,
  onTogglePin: () -> Unit,
  onEdit: () -> Unit,
  onDelete: () -> Unit
) {
  var isExpanded by remember { mutableStateOf(false) }
  val categoryColor = getCategoryColor(note.category)
  val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy", Locale.US) }
  val dateStr = remember(note.updatedAt) { dateFormat.format(Date(note.updatedAt)) }

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("career_note_card_${note.id}"),
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = SlateCard),
    border = androidx.compose.foundation.BorderStroke(
      1.dp,
      if (note.isPinned) TitanGold.copy(alpha = 0.6f) else SlateBorder
    )
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      // Top row: Category Badge, Pin Icon, Target company/role, Actions
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(categoryColor.copy(alpha = 0.15f))
              .border(1.dp, categoryColor.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
              .padding(horizontal = 8.dp, vertical = 3.dp)
          ) {
            Text(
              text = note.category.replace("_", " "),
              style = MaterialTheme.typography.labelSmall,
              color = categoryColor,
              fontWeight = FontWeight.Bold,
              fontSize = 10.sp
            )
          }

          if (note.isPinned) {
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(TitanGold.copy(alpha = 0.15f))
                .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  Icons.Default.PushPin,
                  contentDescription = "Pinned",
                  tint = TitanGold,
                  modifier = Modifier.size(11.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text("PINNED", color = TitanGold, fontSize = 9.sp, fontWeight = FontWeight.Bold)
              }
            }
          }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          IconButton(
            onClick = onTogglePin,
            modifier = Modifier
              .size(28.dp)
              .testTag("pin_note_${note.id}")
          ) {
            Icon(
              imageVector = if (note.isPinned) Icons.Default.PushPin else Icons.Outlined.PushPin,
              contentDescription = "Toggle Pin",
              tint = if (note.isPinned) TitanGold else TextMutedDark,
              modifier = Modifier.size(16.dp)
            )
          }
          IconButton(
            onClick = onEdit,
            modifier = Modifier
              .size(28.dp)
              .testTag("edit_note_${note.id}")
          ) {
            Icon(
              Icons.Default.Edit,
              contentDescription = "Edit Note",
              tint = TitanCyan,
              modifier = Modifier.size(16.dp)
            )
          }
          IconButton(
            onClick = onDelete,
            modifier = Modifier
              .size(28.dp)
              .testTag("delete_note_${note.id}")
          ) {
            Icon(
              Icons.Default.Delete,
              contentDescription = "Delete Note",
              tint = TitanCrimson,
              modifier = Modifier.size(16.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(6.dp))

      // Note Title
      Text(
        text = note.title,
        style = MaterialTheme.typography.titleMedium,
        color = TextPrimaryDark,
        fontWeight = FontWeight.Bold
      )

      // Target Company / Role tags
      if (note.targetCompany.isNotBlank() || note.targetRole.isNotBlank()) {
        Spacer(modifier = Modifier.height(4.dp))
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          if (note.targetCompany.isNotBlank()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Business, contentDescription = null, tint = TextMutedDark, modifier = Modifier.size(12.dp))
              Spacer(modifier = Modifier.width(3.dp))
              Text(note.targetCompany, color = TextMutedDark, fontSize = 11.sp)
            }
          }
          if (note.targetRole.isNotBlank()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Work, contentDescription = null, tint = TextMutedDark, modifier = Modifier.size(12.dp))
              Spacer(modifier = Modifier.width(3.dp))
              Text(note.targetRole, color = TextMutedDark, fontSize = 11.sp)
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Note Content (with expand/collapse if long)
      val isLong = note.content.length > 160
      Text(
        text = note.content,
        style = MaterialTheme.typography.bodyMedium,
        color = TextPrimaryDark.copy(alpha = 0.9f),
        maxLines = if (isExpanded || !isLong) Int.MAX_VALUE else 3,
        overflow = TextOverflow.Ellipsis
      )

      if (isLong) {
        Spacer(modifier = Modifier.height(4.dp))
        Row(
          modifier = Modifier
            .clickable { isExpanded = !isExpanded }
            .padding(vertical = 2.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = if (isExpanded) "Show less" else "Read full note",
            color = TitanCyan,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold
          )
          Icon(
            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
            contentDescription = null,
            tint = TitanCyan,
            modifier = Modifier.size(14.dp)
          )
        }
      }

      // Tags & Date Footer
      Spacer(modifier = Modifier.height(8.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        if (note.tags.isNotBlank()) {
          Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            note.tags.split(",", ";", " ").filter { it.isNotBlank() }.forEach { tag ->
              val cleanTag = if (tag.startsWith("#")) tag else "#$tag"
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(4.dp))
                  .background(SlateElevated)
                  .padding(horizontal = 6.dp, vertical = 2.dp)
              ) {
                Text(cleanTag, color = TextMutedDark, fontSize = 10.sp)
              }
            }
          }
        } else {
          Spacer(modifier = Modifier.width(4.dp))
        }

        Text(
          text = "Updated $dateStr",
          color = TextMutedDark,
          fontSize = 10.sp
        )
      }
    }
  }
}

@Composable
private fun EmptyNotesState(
  isFiltered: Boolean,
  onResetFilter: () -> Unit,
  onCreateNote: () -> Unit
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .padding(top = 40.dp)
      .testTag("empty_notes_state"),
    contentAlignment = Alignment.Center
  ) {
    Card(
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = SlateCard),
      border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder),
      modifier = Modifier.fillMaxWidth(0.9f)
    ) {
      Column(
        modifier = Modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
      ) {
        Icon(
          imageVector = Icons.AutoMirrored.Filled.Notes,
          contentDescription = null,
          tint = TitanCyan.copy(alpha = 0.6f),
          modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
          text = if (isFiltered) "No Notes Match Filter" else "No Career Notes Yet",
          style = MaterialTheme.typography.titleMedium,
          color = TextPrimaryDark,
          fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
          text = if (isFiltered)
            "Try clearing your search query or switching to 'ALL' categories."
          else
            "Store interview battle-cards, strategic compensation scripts, and company memos completely offline.",
          style = MaterialTheme.typography.bodySmall,
          color = TextMutedDark,
          textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        if (isFiltered) {
          OutlinedButton(
            onClick = onResetFilter,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.testTag("reset_notes_filter_btn")
          ) {
            Text("Clear Filter", color = TitanCyan)
          }
        } else {
          Button(
            onClick = onCreateNote,
            colors = ButtonDefaults.buttonColors(containerColor = TitanCyan),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.testTag("empty_notes_add_btn")
          ) {
            Icon(Icons.Default.Add, contentDescription = null, tint = ObsidianDark, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Create First Note", color = ObsidianDark, fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}

@Composable
private fun CareerNoteEditDialog(
  note: CareerNote?,
  onDismiss: () -> Unit,
  onSave: (
    title: String,
    category: String,
    content: String,
    company: String,
    role: String,
    tags: String,
    isPinned: Boolean
  ) -> Unit
) {
  var title by remember { mutableStateOf(note?.title ?: "") }
  var category by remember { mutableStateOf(note?.category ?: "STRATEGY") }
  var content by remember { mutableStateOf(note?.content ?: "") }
  var targetCompany by remember { mutableStateOf(note?.targetCompany ?: "") }
  var targetRole by remember { mutableStateOf(note?.targetRole ?: "") }
  var tags by remember { mutableStateOf(note?.tags ?: "") }
  var isPinned by remember { mutableStateOf(note?.isPinned ?: false) }

  var titleError by remember { mutableStateOf(false) }
  var contentError by remember { mutableStateOf(false) }

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(16.dp),
      color = SlateCard,
      border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder),
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
        .testTag("career_note_editor_dialog")
    ) {
      Column(
        modifier = Modifier
          .padding(20.dp)
          .verticalScrollableDialog()
      ) {
        Text(
          text = if (note == null) "New Career Note" else "Edit Career Note",
          style = MaterialTheme.typography.titleLarge,
          color = TextPrimaryDark,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = "Persisted locally in Room database",
          style = MaterialTheme.typography.bodySmall,
          color = TextMutedDark
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Title Field
        OutlinedTextField(
          value = title,
          onValueChange = {
            title = it
            if (it.isNotBlank()) titleError = false
          },
          label = { Text("Note Title *", color = TextMutedDark) },
          isError = titleError,
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("note_title_input"),
          trailingIcon = {
            SpeechDictationButton(
              currentText = title,
              onTextAppended = {
                title = it
                titleError = false
              },
              targetFieldLabel = "Note Title",
              testTag = "dictate_career_note_title_mic_btn"
            )
          },
          colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = TextPrimaryDark,
            unfocusedTextColor = TextPrimaryDark,
            focusedBorderColor = TitanCyan,
            unfocusedBorderColor = SlateBorder
          )
        )
        if (titleError) {
          Text("Title is required", color = TitanCrimson, fontSize = 10.sp)
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Category Selector
        Text("Category", color = TextMutedDark, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          CATEGORIES.filter { it != "ALL" }.forEach { cat ->
            val isSelected = category == cat
            val pillColor = getCategoryColor(cat)
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(if (isSelected) pillColor.copy(alpha = 0.25f) else SlateElevated)
                .border(1.dp, if (isSelected) pillColor else SlateBorder, RoundedCornerShape(6.dp))
                .clickable { category = cat }
                .padding(horizontal = 8.dp, vertical = 4.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = cat.replace("_", " "),
                color = if (isSelected) TextPrimaryDark else TextMutedDark,
                fontSize = 10.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Content Field
        OutlinedTextField(
          value = content,
          onValueChange = {
            content = it
            if (it.isNotBlank()) contentError = false
          },
          label = { Text("Content / Battle-plan *", color = TextMutedDark) },
          isError = contentError,
          minLines = 4,
          maxLines = 8,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("note_content_input"),
          trailingIcon = {
            SpeechDictationButton(
              currentText = content,
              onTextAppended = {
                content = it
                contentError = false
              },
              targetFieldLabel = "Content Memo",
              testTag = "dictate_career_note_content_mic_btn"
            )
          },
          colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = TextPrimaryDark,
            unfocusedTextColor = TextPrimaryDark,
            focusedBorderColor = TitanCyan,
            unfocusedBorderColor = SlateBorder
          )
        )
        if (contentError) {
          Text("Content is required", color = TitanCrimson, fontSize = 10.sp)
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Company & Role Row
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          OutlinedTextField(
            value = targetCompany,
            onValueChange = { targetCompany = it },
            label = { Text("Company (opt)", color = TextMutedDark, fontSize = 11.sp) },
            singleLine = true,
            modifier = Modifier
              .weight(1f)
              .testTag("note_company_input"),
            colors = OutlinedTextFieldDefaults.colors(
              focusedTextColor = TextPrimaryDark,
              unfocusedTextColor = TextPrimaryDark,
              focusedBorderColor = TitanCyan,
              unfocusedBorderColor = SlateBorder
            )
          )
          OutlinedTextField(
            value = targetRole,
            onValueChange = { targetRole = it },
            label = { Text("Role (opt)", color = TextMutedDark, fontSize = 11.sp) },
            singleLine = true,
            modifier = Modifier
              .weight(1f)
              .testTag("note_role_input"),
            colors = OutlinedTextFieldDefaults.colors(
              focusedTextColor = TextPrimaryDark,
              unfocusedTextColor = TextPrimaryDark,
              focusedBorderColor = TitanCyan,
              unfocusedBorderColor = SlateBorder
            )
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Tags
        OutlinedTextField(
          value = tags,
          onValueChange = { tags = it },
          label = { Text("Tags (comma separated, e.g. #offer, #l6)", color = TextMutedDark, fontSize = 11.sp) },
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("note_tags_input"),
          colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = TextPrimaryDark,
            unfocusedTextColor = TextPrimaryDark,
            focusedBorderColor = TitanCyan,
            unfocusedBorderColor = SlateBorder
          )
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Pin Switch
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.PushPin, contentDescription = null, tint = TitanGold, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Pin to top of vault", color = TextPrimaryDark, fontSize = 13.sp)
          }
          Switch(
            checked = isPinned,
            onCheckedChange = { isPinned = it },
            colors = SwitchDefaults.colors(
              checkedThumbColor = TitanGold,
              checkedTrackColor = TitanGold.copy(alpha = 0.4f),
              uncheckedThumbColor = TextMutedDark,
              uncheckedTrackColor = SlateElevated
            ),
            modifier = Modifier.testTag("note_pin_switch")
          )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Actions
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.End,
          verticalAlignment = Alignment.CenterVertically
        ) {
          TextButton(
            onClick = onDismiss,
            modifier = Modifier.testTag("cancel_note_btn")
          ) {
            Text("Cancel", color = TextMutedDark)
          }
          Spacer(modifier = Modifier.width(8.dp))
          Button(
            onClick = {
              var valid = true
              if (title.isBlank()) {
                titleError = true
                valid = false
              }
              if (content.isBlank()) {
                contentError = true
                valid = false
              }
              if (valid) {
                onSave(title, category, content, targetCompany, targetRole, tags, isPinned)
              }
            },
            colors = ButtonDefaults.buttonColors(containerColor = TitanCyan),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.testTag("save_note_btn")
          ) {
            Text("Save Note", color = ObsidianDark, fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}

private fun Modifier.verticalScrollableDialog(): Modifier {
  return this
}

private fun getCategoryColor(category: String): Color {
  return when (category.uppercase()) {
    "STRATEGY" -> TitanCyan
    "INTERVIEW_PREP" -> TitanViolet
    "NEGOTIATION" -> TitanGold
    "NETWORKING" -> TitanEmerald
    "TARGET_ROLES" -> Color(0xFF60A5FA)
    else -> Color(0xFF94A3B8)
  }
}
