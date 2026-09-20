package com.example.ui.screens

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Work
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.CompanyBookmark
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
import com.example.ui.viewmodel.CompanyBookmarksViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val TIERS = listOf("ALL", "S+", "S", "A", "B", "C")
private val PRIORITIES = listOf("ALL", "DREAM", "HIGH", "MEDIUM", "WATCH")

@Composable
fun CompanyBookmarksScreen(
  viewModel: CompanyBookmarksViewModel,
  modifier: Modifier = Modifier
) {
  val bookmarks by viewModel.filteredBookmarks.collectAsState()
  val allBookmarks by viewModel.allBookmarks.collectAsState()
  val searchQuery by viewModel.searchQuery.collectAsState()
  val selectedTier by viewModel.selectedTier.collectAsState()
  val selectedPriority by viewModel.selectedPriority.collectAsState()

  var isAddDialogOpen by remember { mutableStateOf(false) }
  var bookmarkToEditNotes by remember { mutableStateOf<CompanyBookmark?>(null) }
  var bookmarkToDelete by remember { mutableStateOf<CompanyBookmark?>(null) }

  val topTierCount = allBookmarks.count { it.tier == "S+" || it.tier == "S" }
  val alertsActiveCount = allBookmarks.count { it.alertOnNewJobs }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(ObsidianDark)
      .testTag("company_bookmarks_screen")
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp, vertical = 12.dp)
        .widthIn(max = 800.dp)
        .align(Alignment.TopCenter)
    ) {
      // Header
      CompanyBookmarksHeader(
        totalCount = allBookmarks.size,
        topTierCount = topTierCount,
        alertsActiveCount = alertsActiveCount,
        onAddClick = { isAddDialogOpen = true }
      )

      Spacer(modifier = Modifier.height(12.dp))

      // Search Bar
      OutlinedTextField(
        value = searchQuery,
        onValueChange = { viewModel.setSearchQuery(it) },
        placeholder = { Text("Search bookmarked companies, notes, roles...", color = TextMutedDark) },
        leadingIcon = {
          Icon(Icons.Default.Search, contentDescription = "Search", tint = TitanCyan)
        },
        trailingIcon = {
          if (searchQuery.isNotEmpty()) {
            IconButton(
              onClick = { viewModel.setSearchQuery("") },
              modifier = Modifier.testTag("clear_bookmarks_search_btn")
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
          .testTag("company_bookmarks_search_input")
      )

      Spacer(modifier = Modifier.height(10.dp))

      // Tier & Priority Filters
      Text("Tier Filter", color = TextMutedDark, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
      Spacer(modifier = Modifier.height(4.dp))
      TierFilterRow(
        tiers = TIERS,
        selectedTier = selectedTier,
        onTierSelect = { viewModel.setSelectedTier(it) }
      )

      Spacer(modifier = Modifier.height(8.dp))

      Text("Priority Filter", color = TextMutedDark, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
      Spacer(modifier = Modifier.height(4.dp))
      PriorityFilterRow(
        priorities = PRIORITIES,
        selectedPriority = selectedPriority,
        onPrioritySelect = { viewModel.setSelectedPriority(it) }
      )

      Spacer(modifier = Modifier.height(12.dp))

      // List of Bookmarks or Empty State
      if (bookmarks.isEmpty()) {
        EmptyBookmarksState(
          isFiltered = searchQuery.isNotBlank() || selectedTier != "ALL" || selectedPriority != "ALL",
          onResetFilter = {
            viewModel.setSearchQuery("")
            viewModel.setSelectedTier("ALL")
            viewModel.setSelectedPriority("ALL")
          },
          onAddBookmark = { isAddDialogOpen = true }
        )
      } else {
        LazyColumn(
          modifier = Modifier
            .fillMaxSize()
            .testTag("company_bookmarks_list"),
          verticalArrangement = Arrangement.spacedBy(10.dp),
          contentPadding = PaddingValues(bottom = 80.dp)
        ) {
          items(bookmarks, key = { it.companyId }) { bookmark ->
            CompanyBookmarkCard(
              bookmark = bookmark,
              onToggleAlerts = { viewModel.toggleJobAlerts(bookmark) },
              onEditNotes = { bookmarkToEditNotes = bookmark },
              onDelete = { bookmarkToDelete = bookmark }
            )
          }
        }
      }
    }

    // FAB
    FloatingActionButton(
      onClick = { isAddDialogOpen = true },
      containerColor = TitanCyan,
      contentColor = ObsidianDark,
      shape = CircleShape,
      modifier = Modifier
        .align(Alignment.BottomEnd)
        .padding(20.dp)
        .testTag("add_company_bookmark_fab")
    ) {
      Icon(Icons.Default.Add, contentDescription = "Bookmark Company")
    }

    // Add Bookmark Dialog
    if (isAddDialogOpen) {
      AddCompanyBookmarkDialog(
        onDismiss = { isAddDialogOpen = false },
        onSave = { id, name, emoji, ind, tier, prio, notes, role, alerts ->
          viewModel.addBookmark(id, name, emoji, ind, tier, prio, notes, role, alerts)
          isAddDialogOpen = false
        }
      )
    }

    // Edit Notes Dialog
    bookmarkToEditNotes?.let { bookmark ->
      BookmarkNotesDialog(
        bookmark = bookmark,
        onDismiss = { bookmarkToEditNotes = null },
        onSaveNotes = { newNotes ->
          viewModel.updateNotes(bookmark.companyId, newNotes)
          bookmarkToEditNotes = null
        }
      )
    }

    // Delete Confirmation Dialog
    bookmarkToDelete?.let { bookmark ->
      AlertDialog(
        onDismissRequest = { bookmarkToDelete = null },
        title = { Text("Remove Bookmark?", color = TextPrimaryDark) },
        text = {
          Text(
            "Remove \"${bookmark.companyName}\" from your offline company bookmarks?",
            color = TextMutedDark
          )
        },
        confirmButton = {
          Button(
            onClick = {
              viewModel.removeBookmark(bookmark.companyId)
              bookmarkToDelete = null
            },
            colors = ButtonDefaults.buttonColors(containerColor = TitanCrimson),
            modifier = Modifier.testTag("confirm_remove_bookmark_btn")
          ) {
            Text("Remove", color = Color.White)
          }
        },
        dismissButton = {
          TextButton(onClick = { bookmarkToDelete = null }) {
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
private fun CompanyBookmarksHeader(
  totalCount: Int,
  topTierCount: Int,
  alertsActiveCount: Int,
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
          imageVector = Icons.Default.Bookmark,
          contentDescription = null,
          tint = TitanCyan,
          modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "Company Bookmarks",
          style = MaterialTheme.typography.titleLarge,
          color = TextPrimaryDark,
          fontWeight = FontWeight.Bold
        )
      }
      Text(
        text = "$totalCount bookmarked ($topTierCount Tier S+/S • $alertsActiveCount alerts active)",
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
      modifier = Modifier.testTag("header_add_bookmark_btn")
    ) {
      Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
      Spacer(modifier = Modifier.width(4.dp))
      Text("Bookmark", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
  }
}

@Composable
private fun TierFilterRow(
  tiers: List<String>,
  selectedTier: String,
  onTierSelect: (String) -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .horizontalScroll(rememberScrollState()),
    horizontalArrangement = Arrangement.spacedBy(6.dp)
  ) {
    tiers.forEach { tier ->
      val isSelected = selectedTier.equals(tier, ignoreCase = true)
      val tierColor = getTierColor(tier)
      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(8.dp))
          .background(if (isSelected) tierColor.copy(alpha = 0.2f) else SlateCard)
          .border(
            1.dp,
            if (isSelected) tierColor else SlateBorder,
            RoundedCornerShape(8.dp)
          )
          .clickable { onTierSelect(tier) }
          .padding(horizontal = 10.dp, vertical = 5.dp)
          .testTag("tier_chip_${tier.lowercase()}"),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = if (tier == "ALL") "All Tiers" else "Tier $tier",
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
private fun PriorityFilterRow(
  priorities: List<String>,
  selectedPriority: String,
  onPrioritySelect: (String) -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .horizontalScroll(rememberScrollState()),
    horizontalArrangement = Arrangement.spacedBy(6.dp)
  ) {
    priorities.forEach { prio ->
      val isSelected = selectedPriority.equals(prio, ignoreCase = true)
      val prioColor = getPriorityColor(prio)
      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(8.dp))
          .background(if (isSelected) prioColor.copy(alpha = 0.2f) else SlateCard)
          .border(
            1.dp,
            if (isSelected) prioColor else SlateBorder,
            RoundedCornerShape(8.dp)
          )
          .clickable { onPrioritySelect(prio) }
          .padding(horizontal = 10.dp, vertical = 5.dp)
          .testTag("priority_chip_${prio.lowercase()}"),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = prio,
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
private fun CompanyBookmarkCard(
  bookmark: CompanyBookmark,
  onToggleAlerts: () -> Unit,
  onEditNotes: () -> Unit,
  onDelete: () -> Unit
) {
  val tierColor = getTierColor(bookmark.tier)
  val prioColor = getPriorityColor(bookmark.priority)
  val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy", Locale.US) }
  val dateStr = remember(bookmark.bookmarkedAt) { dateFormat.format(Date(bookmark.bookmarkedAt)) }

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("company_bookmark_card_${bookmark.companyId}"),
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = SlateCard),
    border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      // Top row: Emoji + Company Name + Tier Badge + Priority Tag + Actions
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Box(
            modifier = Modifier
              .size(36.dp)
              .clip(RoundedCornerShape(8.dp))
              .background(SlateElevated),
            contentAlignment = Alignment.Center
          ) {
            Text(text = bookmark.logoEmoji, fontSize = 20.sp)
          }

          Column {
            Text(
              text = bookmark.companyName,
              style = MaterialTheme.typography.titleMedium,
              color = TextPrimaryDark,
              fontWeight = FontWeight.Bold
            )
            if (bookmark.industry.isNotBlank()) {
              Text(
                text = bookmark.industry,
                style = MaterialTheme.typography.bodySmall,
                color = TextMutedDark,
                fontSize = 11.sp
              )
            }
          }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          // Tier Pill
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(tierColor.copy(alpha = 0.15f))
              .border(1.dp, tierColor.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
              .padding(horizontal = 7.dp, vertical = 3.dp)
          ) {
            Text(
              text = "Tier ${bookmark.tier}",
              color = tierColor,
              fontWeight = FontWeight.Bold,
              fontSize = 10.sp
            )
          }

          Spacer(modifier = Modifier.width(6.dp))

          // Priority Pill
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(prioColor.copy(alpha = 0.15f))
              .border(1.dp, prioColor.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
              .padding(horizontal = 7.dp, vertical = 3.dp)
          ) {
            Text(
              text = bookmark.priority,
              color = prioColor,
              fontWeight = FontWeight.Bold,
              fontSize = 10.sp
            )
          }

          IconButton(
            onClick = onDelete,
            modifier = Modifier
              .size(28.dp)
              .testTag("remove_bookmark_${bookmark.companyId}")
          ) {
            Icon(
              Icons.Default.Delete,
              contentDescription = "Remove Bookmark",
              tint = TitanCrimson.copy(alpha = 0.8f),
              modifier = Modifier.size(16.dp)
            )
          }
        }
      }

      // Target Role if specified
      if (bookmark.targetRole.isNotBlank()) {
        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Work, contentDescription = null, tint = TitanCyan, modifier = Modifier.size(13.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Targeting: ${bookmark.targetRole}", color = TextPrimaryDark, fontSize = 12.sp, fontWeight = FontWeight.Medium)
        }
      }

      // Personal Notes Box
      Spacer(modifier = Modifier.height(8.dp))
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(8.dp))
          .background(SlateElevated)
          .padding(10.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.Top
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "Personal Intel & Notes:",
              color = TextMutedDark,
              fontSize = 10.sp,
              fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = if (bookmark.personalNotes.isNotBlank()) bookmark.personalNotes else "No personal notes added yet. Tap edit to record intel.",
              color = if (bookmark.personalNotes.isNotBlank()) TextPrimaryDark else TextMutedDark,
              fontSize = 12.sp
            )
          }
          IconButton(
            onClick = onEditNotes,
            modifier = Modifier
              .size(24.dp)
              .testTag("edit_bookmark_notes_${bookmark.companyId}")
          ) {
            Icon(Icons.Default.Edit, contentDescription = "Edit Notes", tint = TitanCyan, modifier = Modifier.size(14.dp))
          }
        }
      }

      // Footer: Alerts switch + Date
      Spacer(modifier = Modifier.height(10.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.clickable { onToggleAlerts() }
        ) {
          Icon(
            imageVector = if (bookmark.alertOnNewJobs) Icons.Default.Notifications else Icons.Default.NotificationsNone,
            contentDescription = null,
            tint = if (bookmark.alertOnNewJobs) TitanEmerald else TextMutedDark,
            modifier = Modifier.size(15.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = if (bookmark.alertOnNewJobs) "Radar Alerts Active" else "Alerts Muted",
            color = if (bookmark.alertOnNewJobs) TitanEmerald else TextMutedDark,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
          )
        }

        Text(
          text = "Saved $dateStr",
          color = TextMutedDark,
          fontSize = 10.sp
        )
      }
    }
  }
}

@Composable
private fun EmptyBookmarksState(
  isFiltered: Boolean,
  onResetFilter: () -> Unit,
  onAddBookmark: () -> Unit
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .padding(top = 40.dp)
      .testTag("empty_bookmarks_state"),
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
          imageVector = Icons.Default.BookmarkBorder,
          contentDescription = null,
          tint = TitanCyan.copy(alpha = 0.6f),
          modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
          text = if (isFiltered) "No Bookmarks Match Filter" else "No Bookmarked Companies",
          style = MaterialTheme.typography.titleMedium,
          color = TextPrimaryDark,
          fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
          text = if (isFiltered)
            "Try clearing your search query or tier/priority filters."
          else
            "Bookmark target companies with custom tiers (S+, S, A), personal recruiter intel, and radar alerts.",
          style = MaterialTheme.typography.bodySmall,
          color = TextMutedDark,
          textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        if (isFiltered) {
          OutlinedButton(
            onClick = onResetFilter,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.testTag("reset_bookmarks_filter_btn")
          ) {
            Text("Clear Filter", color = TitanCyan)
          }
        } else {
          Button(
            onClick = onAddBookmark,
            colors = ButtonDefaults.buttonColors(containerColor = TitanCyan),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.testTag("empty_bookmarks_add_btn")
          ) {
            Icon(Icons.Default.Add, contentDescription = null, tint = ObsidianDark, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Bookmark First Company", color = ObsidianDark, fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}

@Composable
private fun AddCompanyBookmarkDialog(
  onDismiss: () -> Unit,
  onSave: (
    id: String,
    name: String,
    emoji: String,
    industry: String,
    tier: String,
    priority: String,
    notes: String,
    role: String,
    alerts: Boolean
  ) -> Unit
) {
  var name by remember { mutableStateOf("") }
  var emoji by remember { mutableStateOf("🏢") }
  var industry by remember { mutableStateOf("") }
  var tier by remember { mutableStateOf("S") }
  var priority by remember { mutableStateOf("HIGH") }
  var notes by remember { mutableStateOf("") }
  var role by remember { mutableStateOf("") }
  var alerts by remember { mutableStateOf(true) }

  var nameError by remember { mutableStateOf(false) }

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(16.dp),
      color = SlateCard,
      border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder),
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
        .testTag("add_company_bookmark_dialog")
    ) {
      Column(
        modifier = Modifier.padding(20.dp)
      ) {
        Text(
          text = "Bookmark Company",
          style = MaterialTheme.typography.titleLarge,
          color = TextPrimaryDark,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = "Track target companies offline with custom intel",
          style = MaterialTheme.typography.bodySmall,
          color = TextMutedDark
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Name & Emoji Row
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          OutlinedTextField(
            value = emoji,
            onValueChange = { emoji = it.take(2) },
            label = { Text("Icon", color = TextMutedDark, fontSize = 11.sp) },
            singleLine = true,
            modifier = Modifier
              .width(64.dp)
              .testTag("bookmark_emoji_input"),
            colors = OutlinedTextFieldDefaults.colors(
              focusedTextColor = TextPrimaryDark,
              unfocusedTextColor = TextPrimaryDark,
              focusedBorderColor = TitanCyan,
              unfocusedBorderColor = SlateBorder
            )
          )
          OutlinedTextField(
            value = name,
            onValueChange = {
              name = it
              if (it.isNotBlank()) nameError = false
            },
            label = { Text("Company Name *", color = TextMutedDark) },
            isError = nameError,
            singleLine = true,
            modifier = Modifier
              .weight(1f)
              .testTag("bookmark_name_input"),
            colors = OutlinedTextFieldDefaults.colors(
              focusedTextColor = TextPrimaryDark,
              unfocusedTextColor = TextPrimaryDark,
              focusedBorderColor = TitanCyan,
              unfocusedBorderColor = SlateBorder
            )
          )
        }
        if (nameError) {
          Text("Company name is required", color = TitanCrimson, fontSize = 10.sp)
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Industry & Target Role
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          OutlinedTextField(
            value = industry,
            onValueChange = { industry = it },
            label = { Text("Industry", color = TextMutedDark, fontSize = 11.sp) },
            singleLine = true,
            modifier = Modifier
              .weight(1f)
              .testTag("bookmark_industry_input"),
            colors = OutlinedTextFieldDefaults.colors(
              focusedTextColor = TextPrimaryDark,
              unfocusedTextColor = TextPrimaryDark,
              focusedBorderColor = TitanCyan,
              unfocusedBorderColor = SlateBorder
            )
          )
          OutlinedTextField(
            value = role,
            onValueChange = { role = it },
            label = { Text("Target Role", color = TextMutedDark, fontSize = 11.sp) },
            singleLine = true,
            modifier = Modifier
              .weight(1f)
              .testTag("bookmark_role_input"),
            colors = OutlinedTextFieldDefaults.colors(
              focusedTextColor = TextPrimaryDark,
              unfocusedTextColor = TextPrimaryDark,
              focusedBorderColor = TitanCyan,
              unfocusedBorderColor = SlateBorder
            )
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Tier Selector
        Text("Strategic Tier", color = TextMutedDark, fontSize = 11.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          listOf("S+", "S", "A", "B", "C").forEach { t ->
            val isSelected = tier == t
            val color = getTierColor(t)
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(if (isSelected) color.copy(alpha = 0.25f) else SlateElevated)
                .border(1.dp, if (isSelected) color else SlateBorder, RoundedCornerShape(6.dp))
                .clickable { tier = t }
                .padding(horizontal = 8.dp, vertical = 4.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = t,
                color = if (isSelected) TextPrimaryDark else TextMutedDark,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Priority Selector
        Text("Priority", color = TextMutedDark, fontSize = 11.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          listOf("DREAM", "HIGH", "MEDIUM", "WATCH").forEach { p ->
            val isSelected = priority == p
            val color = getPriorityColor(p)
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(if (isSelected) color.copy(alpha = 0.25f) else SlateElevated)
                .border(1.dp, if (isSelected) color else SlateBorder, RoundedCornerShape(6.dp))
                .clickable { priority = p }
                .padding(horizontal = 8.dp, vertical = 4.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = p,
                color = if (isSelected) TextPrimaryDark else TextMutedDark,
                fontSize = 10.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Personal Notes
        OutlinedTextField(
          value = notes,
          onValueChange = { notes = it },
          label = { Text("Personal Notes & Intel", color = TextMutedDark) },
          minLines = 2,
          maxLines = 4,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("bookmark_notes_input"),
          colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = TextPrimaryDark,
            unfocusedTextColor = TextPrimaryDark,
            focusedBorderColor = TitanCyan,
            unfocusedBorderColor = SlateBorder
          )
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Alerts Toggle
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text("Alert on new jobs", color = TextPrimaryDark, fontSize = 13.sp)
          Switch(
            checked = alerts,
            onCheckedChange = { alerts = it },
            colors = SwitchDefaults.colors(
              checkedThumbColor = TitanEmerald,
              checkedTrackColor = TitanEmerald.copy(alpha = 0.4f),
              uncheckedThumbColor = TextMutedDark,
              uncheckedTrackColor = SlateElevated
            ),
            modifier = Modifier.testTag("bookmark_alerts_switch")
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
            modifier = Modifier.testTag("cancel_bookmark_btn")
          ) {
            Text("Cancel", color = TextMutedDark)
          }
          Spacer(modifier = Modifier.width(8.dp))
          Button(
            onClick = {
              if (name.isBlank()) {
                nameError = true
              } else {
                val companyId = name.lowercase().replace("\\s+".toRegex(), "_")
                onSave(companyId, name, emoji, industry, tier, priority, notes, role, alerts)
              }
            },
            colors = ButtonDefaults.buttonColors(containerColor = TitanCyan),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.testTag("save_bookmark_btn")
          ) {
            Text("Bookmark", color = ObsidianDark, fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}

@Composable
private fun BookmarkNotesDialog(
  bookmark: CompanyBookmark,
  onDismiss: () -> Unit,
  onSaveNotes: (String) -> Unit
) {
  var notes by remember { mutableStateOf(bookmark.personalNotes) }

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(16.dp),
      color = SlateCard,
      border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder),
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
        .testTag("edit_bookmark_notes_dialog")
    ) {
      Column(modifier = Modifier.padding(20.dp)) {
        Text(
          text = "Intel for ${bookmark.companyName}",
          style = MaterialTheme.typography.titleLarge,
          color = TextPrimaryDark,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = "Update your offline notes and recruitment strategy",
          style = MaterialTheme.typography.bodySmall,
          color = TextMutedDark
        )

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
          value = notes,
          onValueChange = { notes = it },
          label = { Text("Personal Notes & Observations", color = TextMutedDark) },
          minLines = 4,
          maxLines = 8,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("bookmark_notes_edit_input"),
          colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = TextPrimaryDark,
            unfocusedTextColor = TextPrimaryDark,
            focusedBorderColor = TitanCyan,
            unfocusedBorderColor = SlateBorder
          )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.End,
          verticalAlignment = Alignment.CenterVertically
        ) {
          TextButton(onClick = onDismiss) {
            Text("Cancel", color = TextMutedDark)
          }
          Spacer(modifier = Modifier.width(8.dp))
          Button(
            onClick = { onSaveNotes(notes) },
            colors = ButtonDefaults.buttonColors(containerColor = TitanCyan),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.testTag("save_notes_btn")
          ) {
            Text("Save Notes", color = ObsidianDark, fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}

private fun getTierColor(tier: String): Color {
  return when (tier.uppercase()) {
    "S+" -> TitanGold
    "S" -> TitanCyan
    "A" -> TitanEmerald
    "B" -> Color(0xFF60A5FA)
    "C" -> Color(0xFF94A3B8)
    else -> Color(0xFF94A3B8)
  }
}

private fun getPriorityColor(priority: String): Color {
  return when (priority.uppercase()) {
    "DREAM" -> TitanGold
    "HIGH" -> TitanEmerald
    "MEDIUM" -> TitanCyan
    "WATCH" -> TitanViolet
    else -> Color(0xFF94A3B8)
  }
}
