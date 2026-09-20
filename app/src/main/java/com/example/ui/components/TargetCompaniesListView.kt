package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.launch
import com.example.data.model.TargetCompany
import com.example.data.model.TargetCompanyMarketIntel
import com.example.data.model.TargetCompanyWithMarketIntelligence
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

/**
 * UI View to list, filter, and track Target Companies and their relevant Room-persisted Market Intelligence.
 */
@Composable
fun TargetCompaniesListView(
  targetCompanies: List<TargetCompanyWithMarketIntelligence>,
  searchQuery: String,
  onSearchQueryChanged: (String) -> Unit,
  priorityFilter: String,
  onPriorityFilterChanged: (String) -> Unit,
  statusFilter: String,
  onStatusFilterChanged: (String) -> Unit,
  sentimentFilter: String,
  onSentimentFilterChanged: (String) -> Unit,
  onSaveTargetCompany: (TargetCompany, TargetCompanyMarketIntel?) -> Unit,
  onUpdateStatus: (String, String) -> Unit,
  onToggleWatch: (String, Boolean) -> Unit,
  onDeleteTargetCompany: (String) -> Unit,
  onSaveMarketIntel: (TargetCompanyMarketIntel) -> Unit,
  modifier: Modifier = Modifier
) {
  var showAddCompanyDialog by remember { mutableStateOf(false) }
  var intelDialogTargetCompany by remember { mutableStateOf<TargetCompany?>(null) }
  var companyToDelete by remember { mutableStateOf<TargetCompany?>(null) }
  var showWatchedOnly by remember { mutableStateOf(false) }

  val coroutineScope = rememberCoroutineScope()
  val snackbarHostState = remember { SnackbarHostState() }

  // Apply filters
  val filteredList = targetCompanies.filter { item ->
    val company = item.company
    val latestIntel = item.latestIntel

    val matchesQuery = searchQuery.isBlank() ||
        company.name.contains(searchQuery, ignoreCase = true) ||
        company.industry.contains(searchQuery, ignoreCase = true) ||
        company.subIndustry.contains(searchQuery, ignoreCase = true) ||
        company.tickerOrDomain.contains(searchQuery, ignoreCase = true) ||
        company.targetRoleTitle.contains(searchQuery, ignoreCase = true) ||
        company.bengaluruHub.contains(searchQuery, ignoreCase = true) ||
        (latestIntel?.headlineSummary?.contains(searchQuery, ignoreCase = true) == true)

    val matchesPriority = priorityFilter == "ALL" || company.priorityTier == priorityFilter
    val matchesStatus = statusFilter == "ALL" || company.trackingStatus == statusFilter
    val matchesSentiment = sentimentFilter == "ALL" || (latestIntel?.marketSentiment == sentimentFilter)
    val matchesWatched = !showWatchedOnly || company.isWatched

    matchesQuery && matchesPriority && matchesStatus && matchesSentiment && matchesWatched
  }

  // Summary Metrics
  val totalTracked = targetCompanies.size
  val activeTargetsCount = targetCompanies.count { 
    it.company.trackingStatus == "ACTIVE_TARGET" || it.company.trackingStatus == "INTERVIEWING" || it.company.trackingStatus == "OFFER_STAGE" 
  }
  val bullishSignalsCount = targetCompanies.count { it.latestIntel?.marketSentiment == "BULLISH" }
  val avgCulturalFit = if (targetCompanies.isNotEmpty()) {
    targetCompanies.map { it.company.culturalFitScore }.average().toInt()
  } else 0

  Box(modifier = modifier.fillMaxSize()) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .testTag("target_companies_list_view")
    ) {
      // 1. Prominent Top Search Bar & Quick Sector Filter
      TargetCompaniesSearchBar(
        searchQuery = searchQuery,
        onSearchQueryChanged = onSearchQueryChanged,
        onAddCompanyClick = { showAddCompanyDialog = true },
        matchCount = filteredList.size,
        totalCount = totalTracked
      )

      Spacer(modifier = Modifier.height(10.dp))

      // 2. Executive Intelligence Summary Dashboard Strip
      ExecutiveMetricsStrip(
        totalTracked = totalTracked,
        activeTargetsCount = activeTargetsCount,
        bullishSignalsCount = bullishSignalsCount,
        avgCulturalFit = avgCulturalFit
      )

      Spacer(modifier = Modifier.height(10.dp))

      // 3. Multi-Dimensional Filter Chips (Priority, Status, Sentiment, Watched)
      FilterChipsSection(
        priorityFilter = priorityFilter,
        onPriorityFilterChanged = onPriorityFilterChanged,
        statusFilter = statusFilter,
        onStatusFilterChanged = onStatusFilterChanged,
        sentimentFilter = sentimentFilter,
        onSentimentFilterChanged = onSentimentFilterChanged,
        showWatchedOnly = showWatchedOnly,
        onToggleWatchedOnly = { showWatchedOnly = !showWatchedOnly }
      )

      Spacer(modifier = Modifier.height(10.dp))

      // 4. Target Companies List with Market Intelligence Cards
      if (filteredList.isEmpty()) {
        EmptyTargetCompaniesView(
          isFiltered = searchQuery.isNotBlank() || priorityFilter != "ALL" || statusFilter != "ALL" || sentimentFilter != "ALL" || showWatchedOnly,
          onResetFilters = {
            onSearchQueryChanged("")
            onPriorityFilterChanged("ALL")
            onStatusFilterChanged("ALL")
            onSentimentFilterChanged("ALL")
            showWatchedOnly = false
          },
          onAddCompany = { showAddCompanyDialog = true }
        )
      } else {
        LazyColumn(
          verticalArrangement = Arrangement.spacedBy(12.dp),
          contentPadding = PaddingValues(bottom = 80.dp),
          modifier = Modifier
            .fillMaxSize()
            .testTag("target_companies_lazy_list")
        ) {
          item {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "SHOWING ${filteredList.size} OF $totalTracked TARGET ENTITIES",
                style = MaterialTheme.typography.labelSmall,
                color = TitanCyan,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
              )
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                Text(
                  text = "← SWIPE TO REMOVE",
                  style = MaterialTheme.typography.labelSmall,
                  color = TitanCrimson.copy(alpha = 0.85f),
                  fontWeight = FontWeight.Bold,
                  fontSize = 9.sp,
                  modifier = Modifier.testTag("swipe_hint_badge")
                )
                Text(
                  text = "• ROOM DB",
                  style = MaterialTheme.typography.labelSmall,
                  color = TitanEmerald,
                  fontWeight = FontWeight.Bold,
                  fontSize = 9.sp
                )
              }
            }
          }

          items(
            items = filteredList,
            key = { it.company.id }
          ) { item ->
            SwipeableTargetCompanyItem(
              item = item,
              onDelete = {
                val company = item.company
                val intel = item.latestIntel
                onDeleteTargetCompany(company.id)
                coroutineScope.launch {
                  snackbarHostState.currentSnackbarData?.dismiss()
                  val result = snackbarHostState.showSnackbar(
                    message = "Removed ${company.name} from Target Database",
                    actionLabel = "UNDO",
                    duration = SnackbarDuration.Short
                  )
                  if (result == SnackbarResult.ActionPerformed) {
                    onSaveTargetCompany(company, intel)
                  }
                }
              },
              onUpdateStatus = { newStatus -> onUpdateStatus(item.company.id, newStatus) },
              onToggleWatch = { isWatched -> onToggleWatch(item.company.id, isWatched) },
              onAddIntel = { intelDialogTargetCompany = item.company },
              onRequestDeleteDialog = { companyToDelete = item.company }
            )
          }
        }
      }
    }

    // Floating Snackbar with Cyber-Executive Styling and Undo Action
    SnackbarHost(
      hostState = snackbarHostState,
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .padding(16.dp)
        .testTag("target_companies_snackbar_host")
    ) { data: SnackbarData ->
      Snackbar(
        snackbarData = data,
        containerColor = SlateElevated,
        contentColor = TextPrimaryDark,
        actionColor = TitanCyan,
        actionContentColor = TitanCyan,
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.border(1.dp, SlateBorder, RoundedCornerShape(8.dp))
      )
    }
  }

  // Dialog to Add Target Company + Initial Market Intelligence
  if (showAddCompanyDialog) {
    AddTargetCompanyDialog(
      onDismiss = { showAddCompanyDialog = false },
      onSave = { company, intel ->
        onSaveTargetCompany(company, intel)
        showAddCompanyDialog = false
        coroutineScope.launch {
          snackbarHostState.showSnackbar(
            message = "Saved ${company.name} (${company.industry}) to Room Database",
            duration = SnackbarDuration.Short
          )
        }
      }
    )
  }

  // Dialog to Update/Add Market Intelligence for Existing Company
  intelDialogTargetCompany?.let { targetCompany ->
    AddMarketIntelligenceDialog(
      company = targetCompany,
      onDismiss = { intelDialogTargetCompany = null },
      onSave = { intel ->
        onSaveMarketIntel(intel)
        intelDialogTargetCompany = null
      }
    )
  }

  // Delete Confirmation Dialog
  companyToDelete?.let { company ->
    AlertDialog(
      onDismissRequest = { companyToDelete = null },
      containerColor = SlateElevated,
      title = {
        Text("Remove Target Company?", color = TextPrimaryDark, fontWeight = FontWeight.Bold)
      },
      text = {
        Text(
          "Are you sure you want to remove ${company.name} and all its stored market intelligence records from the Room database?",
          color = TextSecondaryDark,
          fontSize = 13.sp
        )
      },
      confirmButton = {
        Button(
          onClick = {
            onDeleteTargetCompany(company.id)
            companyToDelete = null
          },
          colors = ButtonDefaults.buttonColors(containerColor = TitanCrimson),
          modifier = Modifier.testTag("confirm_delete_target_company_btn")
        ) {
          Text("Remove", color = TextPrimaryDark, fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(
          onClick = { companyToDelete = null }
        ) {
          Text("Cancel", color = TextMutedDark)
        }
      }
    )
  }
}

/**
 * Swipe-to-dismiss wrapper allowing fast swipe deletion of target company entries
 * with real-time Room DB cascading deletion and undo toast support.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeableTargetCompanyItem(
  item: TargetCompanyWithMarketIntelligence,
  onDelete: () -> Unit,
  onUpdateStatus: (String) -> Unit,
  onToggleWatch: (Boolean) -> Unit,
  onAddIntel: () -> Unit,
  onRequestDeleteDialog: () -> Unit,
  modifier: Modifier = Modifier
) {
  val currentItem by rememberUpdatedState(item)
  val dismissState = rememberSwipeToDismissBoxState(
    confirmValueChange = { dismissValue ->
      if (dismissValue == SwipeToDismissBoxValue.EndToStart) {
        onDelete()
        true
      } else {
        false
      }
    }
  )

  SwipeToDismissBox(
    state = dismissState,
    modifier = modifier.testTag("swipe_to_dismiss_${item.company.id}"),
    enableDismissFromStartToEnd = false,
    enableDismissFromEndToStart = true,
    backgroundContent = {
      SwipeDeleteBackground(dismissState = dismissState, companyId = item.company.id)
    }
  ) {
    TargetCompanyCard(
      item = currentItem,
      onUpdateStatus = onUpdateStatus,
      onToggleWatch = onToggleWatch,
      onAddIntel = onAddIntel,
      onDelete = onRequestDeleteDialog
    )
  }
}

/**
 * Animated background displayed when swiping a target company card left to delete.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeDeleteBackground(
  dismissState: SwipeToDismissBoxState,
  companyId: String,
  modifier: Modifier = Modifier
) {
  val isSwipingToDelete = dismissState.targetValue == SwipeToDismissBoxValue.EndToStart
  val backgroundColor = if (isSwipingToDelete) TitanCrimson else TitanCrimson.copy(alpha = 0.35f)
  val scale by animateFloatAsState(
    targetValue = if (isSwipingToDelete) 1.15f else 0.9f,
    label = "swipe_icon_scale"
  )

  Box(
    modifier = modifier
      .fillMaxSize()
      .clip(RoundedCornerShape(12.dp))
      .background(backgroundColor)
      .border(1.dp, TitanCrimson.copy(alpha = 0.7f), RoundedCornerShape(12.dp))
      .padding(horizontal = 20.dp)
      .testTag("swipe_delete_background_$companyId"),
    contentAlignment = Alignment.CenterEnd
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      modifier = Modifier.graphicsLayer {
        scaleX = scale
        scaleY = scale
      }
    ) {
      Text(
        text = "DELETE FROM ROOM",
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Black,
        color = Color.White,
        fontSize = 11.sp,
        letterSpacing = 0.5.sp
      )
      Icon(
        imageVector = Icons.Default.Delete,
        contentDescription = "Delete from Room DB",
        tint = Color.White,
        modifier = Modifier.size(24.dp)
      )
    }
  }
}

/**
 * Top Search Bar allowing rapid filtering of Target Companies by company name or sector,
 * complete with instant match counters, quick-select sector pills, and clear action.
 */
@Composable
private fun TargetCompaniesSearchBar(
  searchQuery: String,
  onSearchQueryChanged: (String) -> Unit,
  onAddCompanyClick: () -> Unit,
  matchCount: Int,
  totalCount: Int,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier
      .fillMaxWidth()
      .testTag("target_companies_top_search_section")
  ) {
    // Primary Search Row
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      OutlinedTextField(
        value = searchQuery,
        onValueChange = onSearchQueryChanged,
        placeholder = {
          Text(
            text = "Search by company name or sector...",
            fontSize = 12.sp,
            color = TextMutedDark,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        },
        leadingIcon = {
          Icon(
            Icons.Default.Search,
            contentDescription = "Search",
            tint = if (searchQuery.isNotEmpty()) TitanCyan else TextMutedDark,
            modifier = Modifier.size(18.dp)
          )
        },
        trailingIcon = {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(end = 4.dp)
          ) {
            if (searchQuery.isNotEmpty()) {
              Surface(
                color = TitanCyan.copy(alpha = 0.15f),
                shape = RoundedCornerShape(4.dp),
                border = BorderStroke(0.5.dp, TitanCyan.copy(alpha = 0.4f))
              ) {
                Text(
                  text = "$matchCount of $totalCount",
                  style = MaterialTheme.typography.labelSmall,
                  fontSize = 9.sp,
                  fontWeight = FontWeight.Bold,
                  color = TitanCyan,
                  modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                )
              }
              IconButton(
                onClick = { onSearchQueryChanged("") },
                modifier = Modifier
                  .size(24.dp)
                  .testTag("clear_search_query_btn")
              ) {
                Icon(
                  Icons.Default.Clear,
                  contentDescription = "Clear Search",
                  tint = TextMutedDark,
                  modifier = Modifier.size(16.dp)
                )
              }
            }
          }
        },
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
          focusedContainerColor = SlateCard,
          unfocusedContainerColor = SlateCard,
          focusedBorderColor = TitanCyan,
          unfocusedBorderColor = SlateBorder,
          focusedTextColor = TextPrimaryDark,
          unfocusedTextColor = TextPrimaryDark
        ),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
          .weight(1f)
          .height(50.dp)
          .testTag("target_companies_search_input")
      )

      Button(
        onClick = onAddCompanyClick,
        colors = ButtonDefaults.buttonColors(containerColor = TitanCyan),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp),
        modifier = Modifier
          .height(50.dp)
          .testTag("add_target_company_main_btn")
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            Icons.Default.Add,
            contentDescription = null,
            tint = ObsidianDark,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "Add Target",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = ObsidianDark
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(6.dp))

    // Quick Sector Filter Pills (AI & Tech, Fintech, Cloud, Commerce, Semiconductor)
    val quickSectors = listOf(
      "All Sectors" to "",
      "AI & Tech" to "AI",
      "Fintech" to "Fintech",
      "Cloud" to "Cloud",
      "Commerce" to "Commerce",
      "Semiconductor" to "Semiconductor"
    )

    Row(
      modifier = Modifier
        .fillMaxWidth()
        .horizontalScroll(rememberScrollState()),
      horizontalArrangement = Arrangement.spacedBy(6.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "SECTOR:",
        style = MaterialTheme.typography.labelSmall,
        fontSize = 9.sp,
        fontWeight = FontWeight.Bold,
        color = TextMutedDark,
        modifier = Modifier.padding(end = 2.dp)
      )

      quickSectors.forEach { (label, keyword) ->
        val isSelected = if (keyword.isEmpty()) {
          searchQuery.isEmpty()
        } else {
          searchQuery.equals(keyword, ignoreCase = true)
        }

        FilterChip(
          selected = isSelected,
          onClick = {
            if (isSelected) {
              onSearchQueryChanged("")
            } else {
              onSearchQueryChanged(keyword)
            }
          },
          label = {
            Text(
              text = label,
              fontSize = 10.sp,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
          },
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = TitanCyan.copy(alpha = 0.2f),
            selectedLabelColor = TitanCyan,
            containerColor = SlateCard.copy(alpha = 0.7f),
            labelColor = TextSecondaryDark
          ),
          border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = isSelected,
            borderColor = SlateBorder.copy(alpha = 0.6f),
            selectedBorderColor = TitanCyan
          ),
          modifier = Modifier.testTag("sector_chip_${label.replace(" ", "_").lowercase()}")
        )
      }
    }
  }
}

/**
 * Metric stats banner at the top of the Target Companies view.
 */
@Composable
private fun ExecutiveMetricsStrip(
  totalTracked: Int,
  activeTargetsCount: Int,
  bullishSignalsCount: Int,
  avgCulturalFit: Int
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(10.dp))
      .background(SlateCard)
      .border(1.dp, SlateBorder, RoundedCornerShape(10.dp))
      .padding(horizontal = 12.dp, vertical = 10.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    MetricTile(
      label = "TARGETS",
      value = "$totalTracked",
      valueColor = TitanCyan
    )
    Box(modifier = Modifier.width(1.dp).height(24.dp).background(SlateBorder))
    MetricTile(
      label = "ACTIVE PIPELINE",
      value = "$activeTargetsCount",
      valueColor = TitanGold
    )
    Box(modifier = Modifier.width(1.dp).height(24.dp).background(SlateBorder))
    MetricTile(
      label = "BULLISH SIGNALS",
      value = "$bullishSignalsCount",
      valueColor = TitanEmerald
    )
    Box(modifier = Modifier.width(1.dp).height(24.dp).background(SlateBorder))
    MetricTile(
      label = "AVG FIT",
      value = "$avgCulturalFit%",
      valueColor = TitanIndigo
    )
  }
}

@Composable
private fun MetricTile(
  label: String,
  value: String,
  valueColor: Color
) {
  Column(horizontalAlignment = Alignment.CenterHorizontally) {
    Text(
      text = value,
      fontSize = 16.sp,
      fontWeight = FontWeight.Black,
      color = valueColor
    )
    Text(
      text = label,
      fontSize = 9.sp,
      fontWeight = FontWeight.Bold,
      color = TextMutedDark
    )
  }
}

/**
 * Filter chips row supporting Priority, Status, Sentiment, and Watched.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FilterChipsSection(
  priorityFilter: String,
  onPriorityFilterChanged: (String) -> Unit,
  statusFilter: String,
  onStatusFilterChanged: (String) -> Unit,
  sentimentFilter: String,
  onSentimentFilterChanged: (String) -> Unit,
  showWatchedOnly: Boolean,
  onToggleWatchedOnly: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .horizontalScroll(rememberScrollState()),
    horizontalArrangement = Arrangement.spacedBy(6.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    // Watched Only Toggle
    FilterChip(
      selected = showWatchedOnly,
      onClick = onToggleWatchedOnly,
      label = {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = if (showWatchedOnly) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
            contentDescription = null,
            modifier = Modifier.size(12.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text("Watched", fontSize = 11.sp)
        }
      },
      colors = FilterChipDefaults.filterChipColors(
        selectedContainerColor = TitanGold.copy(alpha = 0.2f),
        selectedLabelColor = TitanGold,
        containerColor = SlateCard,
        labelColor = TextSecondaryDark
      ),
      border = FilterChipDefaults.filterChipBorder(
        enabled = true,
        selected = showWatchedOnly,
        borderColor = SlateBorder,
        selectedBorderColor = TitanGold
      ),
      modifier = Modifier.testTag("filter_chip_watched")
    )

    // Priority Tier Filters
    val priorityOptions = listOf(
      "ALL" to "All Tiers",
      "TIER_1_DREAM" to "Tier 1 Dream",
      "TIER_2_STRATEGIC" to "Tier 2 Strategic",
      "TIER_3_WATCHLIST" to "Tier 3 Watchlist"
    )
    priorityOptions.forEach { (key, label) ->
      val isSelected = priorityFilter == key
      FilterChip(
        selected = isSelected,
        onClick = { onPriorityFilterChanged(key) },
        label = { Text(label, fontSize = 11.sp) },
        colors = FilterChipDefaults.filterChipColors(
          selectedContainerColor = TitanCyan.copy(alpha = 0.2f),
          selectedLabelColor = TitanCyan,
          containerColor = SlateCard,
          labelColor = TextSecondaryDark
        ),
        border = FilterChipDefaults.filterChipBorder(
          enabled = true,
          selected = isSelected,
          borderColor = SlateBorder,
          selectedBorderColor = TitanCyan
        ),
        modifier = Modifier.testTag("filter_chip_priority_$key")
      )
    }

    // Sentiment Filters
    val sentimentOptions = listOf(
      "ALL" to "All Sentiments",
      "BULLISH" to "Bullish",
      "NEUTRAL" to "Neutral",
      "CAUTIOUS" to "Cautious"
    )
    sentimentOptions.forEach { (key, label) ->
      val isSelected = sentimentFilter == key
      FilterChip(
        selected = isSelected,
        onClick = { onSentimentFilterChanged(key) },
        label = { Text(label, fontSize = 11.sp) },
        colors = FilterChipDefaults.filterChipColors(
          selectedContainerColor = TitanEmerald.copy(alpha = 0.2f),
          selectedLabelColor = TitanEmerald,
          containerColor = SlateCard,
          labelColor = TextSecondaryDark
        ),
        border = FilterChipDefaults.filterChipBorder(
          enabled = true,
          selected = isSelected,
          borderColor = SlateBorder,
          selectedBorderColor = TitanEmerald
        ),
        modifier = Modifier.testTag("filter_chip_sentiment_$key")
      )
    }
  }
}

/**
 * Rich Card displaying a Target Company and its room-persisted Market Intelligence.
 */
@Composable
private fun TargetCompanyCard(
  item: TargetCompanyWithMarketIntelligence,
  onUpdateStatus: (String) -> Unit,
  onToggleWatch: (Boolean) -> Unit,
  onAddIntel: () -> Unit,
  onDelete: () -> Unit
) {
  val company = item.company
  val latestIntel = item.latestIntel
  var isExpanded by remember { mutableStateOf(false) }
  var showStatusMenu by remember { mutableStateOf(false) }

  Card(
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = SlateCard),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    modifier = Modifier
      .fillMaxWidth()
      .border(
        width = 1.dp,
        color = if (company.priorityTier == "TIER_1_DREAM") TitanGold.copy(alpha = 0.35f) else SlateBorder,
        shape = RoundedCornerShape(12.dp)
      )
      .animateContentSize()
      .testTag("target_company_card_${company.id}")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp)
    ) {
      // Top Row: Emoji Icon, Company Name, Priority Tier Badge, Watch Button
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.weight(1f)
        ) {
          // Company Logo Emoji Badge
          Box(
            modifier = Modifier
              .size(42.dp)
              .clip(CircleShape)
              .background(SlateElevated)
              .border(1.dp, SlateBorder, CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Text(text = company.logoEmoji, fontSize = 22.sp)
          }

          Spacer(modifier = Modifier.width(10.dp))

          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = company.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimaryDark,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
              if (company.tickerOrDomain.isNotBlank()) {
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "• ${company.tickerOrDomain}",
                  style = MaterialTheme.typography.labelSmall,
                  color = TextMutedDark
                )
              }
            }

            Text(
              text = "${company.industry} • ${company.subIndustry}",
              style = MaterialTheme.typography.bodySmall,
              color = TextSecondaryDark,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
          }
        }

        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          // Priority Tier Pill
          PriorityTierBadge(tier = company.priorityTier)

          // Bookmark / Watch Toggle
          IconButton(
            onClick = { onToggleWatch(!company.isWatched) },
            modifier = Modifier.size(36.dp).testTag("toggle_watch_${company.id}")
          ) {
            Icon(
              imageVector = if (company.isWatched) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
              contentDescription = if (company.isWatched) "Watched" else "Watch",
              tint = if (company.isWatched) TitanGold else TextMutedDark,
              modifier = Modifier.size(20.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Location & Target Role Row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.weight(1f)
        ) {
          Icon(Icons.Default.LocationOn, contentDescription = null, tint = TitanCyan, modifier = Modifier.size(13.dp))
          Spacer(modifier = Modifier.width(3.dp))
          Text(
            text = company.bengaluruHub,
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondaryDark,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Work, contentDescription = null, tint = TitanGold, modifier = Modifier.size(13.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = company.targetRoleTitle,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimaryDark,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Status Pill & Compensation & Hiring Velocity Strip
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Status with interactive dropdown
        Box {
          Row(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(getStatusContainerColor(company.trackingStatus))
              .clickable { showStatusMenu = true }
              .padding(horizontal = 8.dp, vertical = 4.dp)
              .testTag("status_chip_${company.id}"),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(getStatusTextColor(company.trackingStatus))
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
              text = formatTrackingStatus(company.trackingStatus),
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.Bold,
              color = getStatusTextColor(company.trackingStatus),
              fontSize = 10.sp
            )
            Spacer(modifier = Modifier.width(2.dp))
            Icon(
              Icons.Default.KeyboardArrowDown,
              contentDescription = "Change Status",
              tint = getStatusTextColor(company.trackingStatus),
              modifier = Modifier.size(12.dp)
            )
          }

          DropdownMenu(
            expanded = showStatusMenu,
            onDismissRequest = { showStatusMenu = false },
            modifier = Modifier.background(SlateElevated).border(1.dp, SlateBorder, RoundedCornerShape(8.dp))
          ) {
            val statuses = listOf(
              "EXPLORING", "ACTIVE_TARGET", "NETWORKING", "APPLICATION_STAGED", "INTERVIEWING", "OFFER_STAGE"
            )
            statuses.forEach { st ->
              DropdownMenuItem(
                text = { Text(formatTrackingStatus(st), color = TextPrimaryDark, fontSize = 12.sp) },
                onClick = {
                  onUpdateStatus(st)
                  showStatusMenu = false
                },
                modifier = Modifier.testTag("menu_status_${st}_${company.id}")
              )
            }
          }
        }

        // Compensation Range
        Text(
          text = company.targetCompensationRange,
          style = MaterialTheme.typography.labelSmall,
          color = TitanEmerald,
          fontWeight = FontWeight.Bold,
          fontSize = 11.sp
        )

        // Hiring Velocity & Cultural Fit Score
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          HiringVelocityBadge(velocity = company.hiringVelocity)
          CulturalFitBadge(score = company.culturalFitScore)
        }
      }

      // Notes (Target Reason & Strategic Rationale)
      if (company.notes.isNotBlank()) {
        Spacer(modifier = Modifier.height(8.dp))
        Surface(
          shape = RoundedCornerShape(6.dp),
          color = SlateElevated,
          border = BorderStroke(0.5.dp, SlateBorder)
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Icon(
              Icons.Default.Bookmark,
              contentDescription = "Target Reason",
              tint = TitanGold,
              modifier = Modifier.size(13.dp).padding(top = 1.dp)
            )
            Text(
              text = "Target Reason: ${company.notes}",
              style = MaterialTheme.typography.bodySmall,
              color = TextSecondaryDark,
              fontSize = 11.sp,
              maxLines = if (isExpanded) Int.MAX_VALUE else 2,
              overflow = TextOverflow.Ellipsis
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // 5. Market Intelligence Section Preview / Header
      Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = SlateElevated),
        modifier = Modifier
          .fillMaxWidth()
          .border(1.dp, SlateBorder.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = TitanCyan, modifier = Modifier.size(14.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "MARKET INTELLIGENCE",
                style = MaterialTheme.typography.labelSmall,
                color = TitanCyan,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp
              )
              if (latestIntel != null) {
                Spacer(modifier = Modifier.width(8.dp))
                SentimentBadge(sentiment = latestIntel.marketSentiment, score = latestIntel.sentimentScore)
              }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
              if (latestIntel != null) {
                Text(
                  text = "Confidence ${latestIntel.confidenceRating}%",
                  style = MaterialTheme.typography.labelSmall,
                  color = TextMutedDark,
                  fontSize = 9.sp
                )
              }
              IconButton(
                onClick = { isExpanded = !isExpanded },
                modifier = Modifier.size(24.dp).testTag("toggle_expand_intel_${company.id}")
              ) {
                Icon(
                  imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                  contentDescription = if (isExpanded) "Collapse" else "Expand",
                  tint = TitanCyan,
                  modifier = Modifier.size(16.dp)
                )
              }
            }
          }

          if (latestIntel != null) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = latestIntel.headlineSummary,
              style = MaterialTheme.typography.bodySmall,
              color = TextPrimaryDark,
              fontWeight = FontWeight.Medium,
              fontSize = 12.sp,
              maxLines = if (isExpanded) Int.MAX_VALUE else 2,
              overflow = TextOverflow.Ellipsis
            )

            // Expanded Granular Intelligence
            AnimatedVisibility(
              visible = isExpanded,
              enter = fadeIn() + expandVertically(),
              exit = fadeOut() + shrinkVertically()
            ) {
              Column(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(top = 10.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                if (latestIntel.strategicExpansionSignals.isNotBlank()) {
                  IntelDetailRow(
                    title = "Expansion Signals",
                    content = latestIntel.strategicExpansionSignals,
                    accentColor = TitanCyan
                  )
                }

                if (latestIntel.hiringTrends.isNotBlank()) {
                  IntelDetailRow(
                    title = "Hiring Signals",
                    content = latestIntel.hiringTrends,
                    accentColor = TitanEmerald
                  )
                }

                if (latestIntel.executiveLeadershipShifts.isNotBlank()) {
                  IntelDetailRow(
                    title = "Executive Moves",
                    content = latestIntel.executiveLeadershipShifts,
                    accentColor = TitanGold
                  )
                }

                if (latestIntel.interviewTalkingPoints.isNotBlank()) {
                  IntelDetailRow(
                    title = "Interview Angles",
                    content = latestIntel.interviewTalkingPoints,
                    accentColor = TitanIndigo
                  )
                }

                if (latestIntel.financialHealthAndGrowth.isNotBlank()) {
                  IntelDetailRow(
                    title = "Financial Health",
                    content = latestIntel.financialHealthAndGrowth,
                    accentColor = TitanCyan
                  )
                }

                if (latestIntel.risksAndHeadwinds.isNotBlank()) {
                  IntelDetailRow(
                    title = "Headwinds / Risks",
                    content = latestIntel.risksAndHeadwinds,
                    accentColor = TitanCrimson
                  )
                }

                // Grounding source & date stamp
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(
                    text = "Source: ${latestIntel.sourceGrounding}",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMutedDark,
                    fontSize = 9.sp
                  )
                  Text(
                    text = formatTimestamp(latestIntel.lastRefreshedTimestamp),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMutedDark,
                    fontSize = 9.sp
                  )
                }
              }
            }
          } else {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "No stored market intelligence report yet for this entity.",
              style = MaterialTheme.typography.bodySmall,
              color = TextMutedDark,
              fontSize = 11.sp
            )
          }
        }
      }

      // Bottom Card Actions: Add Intel, Delete
      Spacer(modifier = Modifier.height(8.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        OutlinedButton(
          onClick = onAddIntel,
          shape = RoundedCornerShape(6.dp),
          colors = ButtonDefaults.outlinedButtonColors(contentColor = TitanCyan),
          border = androidx.compose.foundation.BorderStroke(1.dp, TitanCyan.copy(alpha = 0.5f)),
          contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
          modifier = Modifier.height(32.dp).testTag("add_intel_btn_${company.id}")
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(12.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = if (latestIntel == null) "Log Intelligence" else "Update Intel",
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }

        IconButton(
          onClick = onDelete,
          modifier = Modifier.size(32.dp).testTag("delete_target_company_${company.id}")
        ) {
          Icon(
            Icons.Default.Delete,
            contentDescription = "Remove Target Company",
            tint = TextMutedDark,
            modifier = Modifier.size(16.dp)
          )
        }
      }
    }
  }
}

@Composable
private fun IntelDetailRow(
  title: String,
  content: String,
  accentColor: Color
) {
  Column {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Box(modifier = Modifier.size(4.dp).clip(CircleShape).background(accentColor))
      Spacer(modifier = Modifier.width(6.dp))
      Text(
        text = title,
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Bold,
        color = accentColor,
        fontSize = 10.sp
      )
    }
    Spacer(modifier = Modifier.height(2.dp))
    Text(
      text = content,
      style = MaterialTheme.typography.bodySmall,
      color = TextSecondaryDark,
      fontSize = 11.sp,
      lineHeight = 15.sp
    )
  }
}

@Composable
private fun PriorityTierBadge(tier: String) {
  val (label, bg, text, icon) = when (tier) {
    "TIER_1_DREAM" -> Quadruple("TIER 1 DREAM", TitanGold.copy(alpha = 0.15f), TitanGold, true)
    "TIER_2_STRATEGIC" -> Quadruple("STRATEGIC", TitanEmerald.copy(alpha = 0.15f), TitanEmerald, false)
    else -> Quadruple("WATCHLIST", TitanCyan.copy(alpha = 0.15f), TitanCyan, false)
  }

  Box(
    modifier = Modifier
      .clip(RoundedCornerShape(6.dp))
      .background(bg)
      .border(1.dp, text.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
      .padding(horizontal = 6.dp, vertical = 3.dp)
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      if (icon) {
        Icon(Icons.Default.Star, contentDescription = null, tint = text, modifier = Modifier.size(10.dp))
        Spacer(modifier = Modifier.width(3.dp))
      }
      Text(
        text = label,
        style = MaterialTheme.typography.labelSmall,
        color = text,
        fontWeight = FontWeight.Black,
        fontSize = 9.sp
      )
    }
  }
}

@Composable
private fun SentimentBadge(sentiment: String, score: Int) {
  val (bg, text) = when (sentiment) {
    "BULLISH" -> TitanEmerald.copy(alpha = 0.2f) to TitanEmerald
    "CAUTIOUS" -> TitanCrimson.copy(alpha = 0.2f) to TitanCrimson
    else -> TitanGold.copy(alpha = 0.2f) to TitanGold
  }

  Box(
    modifier = Modifier
      .clip(RoundedCornerShape(4.dp))
      .background(bg)
      .padding(horizontal = 5.dp, vertical = 2.dp)
  ) {
    Text(
      text = "$sentiment ($score%)",
      style = MaterialTheme.typography.labelSmall,
      color = text,
      fontWeight = FontWeight.Bold,
      fontSize = 9.sp
    )
  }
}

@Composable
private fun HiringVelocityBadge(velocity: String) {
  val (color, icon) = when (velocity) {
    "SURGING" -> TitanEmerald to Icons.AutoMirrored.Filled.TrendingUp
    "STEADY" -> TitanCyan to Icons.Default.Timeline
    else -> TitanGold to Icons.Default.Timeline
  }

  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier
      .clip(RoundedCornerShape(4.dp))
      .background(color.copy(alpha = 0.12f))
      .padding(horizontal = 5.dp, vertical = 2.dp)
  ) {
    Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(11.dp))
    Spacer(modifier = Modifier.width(3.dp))
    Text(text = velocity, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = color)
  }
}

@Composable
private fun CulturalFitBadge(score: Int) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier
      .clip(RoundedCornerShape(4.dp))
      .background(TitanIndigo.copy(alpha = 0.12f))
      .padding(horizontal = 5.dp, vertical = 2.dp)
  ) {
    Icon(Icons.Default.Psychology, contentDescription = null, tint = TitanIndigo, modifier = Modifier.size(11.dp))
    Spacer(modifier = Modifier.width(3.dp))
    Text(text = "$score% Fit", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TitanIndigo)
  }
}

@Composable
private fun EmptyTargetCompaniesView(
  isFiltered: Boolean,
  onResetFilters: () -> Unit,
  onAddCompany: () -> Unit
) {
  Box(
    modifier = Modifier
      .fillMaxSize()
      .padding(24.dp),
    contentAlignment = Alignment.Center
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      Box(
        modifier = Modifier
          .size(64.dp)
          .clip(CircleShape)
          .background(SlateCard)
          .border(1.dp, SlateBorder, CircleShape),
        contentAlignment = Alignment.Center
      ) {
        Icon(Icons.Default.Business, contentDescription = null, tint = TitanCyan, modifier = Modifier.size(32.dp))
      }

      Spacer(modifier = Modifier.height(14.dp))

      Text(
        text = if (isFiltered) "No Target Companies Match Filters" else "No Target Companies Stored",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = TextPrimaryDark
      )

      Spacer(modifier = Modifier.height(6.dp))

      Text(
        text = if (isFiltered)
          "Try clearing search queries or expanding your priority tier filters."
        else
          "Add your first Target Company to start tracking intelligence, hiring velocity, and interview angles in Room.",
        style = MaterialTheme.typography.bodySmall,
        color = TextSecondaryDark,
        fontSize = 12.sp,
        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        modifier = Modifier.padding(horizontal = 16.dp)
      )

      Spacer(modifier = Modifier.height(16.dp))

      if (isFiltered) {
        OutlinedButton(
          onClick = onResetFilters,
          colors = ButtonDefaults.outlinedButtonColors(contentColor = TitanCyan),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier.testTag("reset_filters_btn")
        ) {
          Text("Reset Filters")
        }
      } else {
        Button(
          onClick = onAddCompany,
          colors = ButtonDefaults.buttonColors(containerColor = TitanCyan),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier.testTag("empty_state_add_company_btn")
        ) {
          Icon(Icons.Default.Add, contentDescription = null, tint = ObsidianDark, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Add Target Company", color = ObsidianDark, fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}

/**
 * Modal Dialog for adding a Target Company with company name, industry, and a note
 * about why it is a target, persisting directly to the local Room database.
 */
@Composable
private fun AddTargetCompanyDialog(
  onDismiss: () -> Unit,
  onSave: (TargetCompany, TargetCompanyMarketIntel?) -> Unit
) {
  var name by remember { mutableStateOf("") }
  var industry by remember { mutableStateOf("Enterprise SaaS & AI") }
  var targetReasonNote by remember { mutableStateOf("") }
  var logoEmoji by remember { mutableStateOf("🏢") }
  var tickerOrDomain by remember { mutableStateOf("") }

  // Optional Advanced Strategy Details
  var showAdvancedDetails by remember { mutableStateOf(false) }
  var priorityTier by remember { mutableStateOf("TIER_1_DREAM") }
  var trackingStatus by remember { mutableStateOf("ACTIVE_TARGET") }
  var targetRoleTitle by remember { mutableStateOf("Strategy & Operations Analyst") }
  var bengaluruHub by remember { mutableStateOf("Outer Ring Road / Bellandur") }
  var targetCompensationRange by remember { mutableStateOf("₹20,00,000 - ₹28,00,000 CTC") }
  var culturalFitScore by remember { mutableStateOf("92") }
  var hiringVelocity by remember { mutableStateOf("SURGING") }

  // Optional Initial Market Intelligence
  var includeMarketIntel by remember { mutableStateOf(false) }
  var headlineSummary by remember { mutableStateOf("") }
  var marketSentiment by remember { mutableStateOf("BULLISH") }
  var strategicExpansionSignals by remember { mutableStateOf("") }
  var interviewTalkingPoints by remember { mutableStateOf("") }

  var hasAttemptedSubmit by remember { mutableStateOf(false) }
  val isNameValid = name.trim().isNotBlank()

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(14.dp),
      color = SlateCard,
      border = BorderStroke(1.dp, SlateBorder),
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 16.dp)
        .testTag("add_target_company_dialog")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(18.dp)
          .verticalScroll(rememberScrollState())
      ) {
        // Dialog Header
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
                .size(32.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(TitanCyan.copy(alpha = 0.15f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                Icons.Default.Business,
                contentDescription = null,
                tint = TitanCyan,
                modifier = Modifier.size(18.dp)
              )
            }
            Column {
              Text(
                text = "Add Target Company",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimaryDark
              )
              Text(
                text = "Save to Room database",
                style = MaterialTheme.typography.labelSmall,
                color = TextMutedDark,
                fontSize = 10.sp
              )
            }
          }
          IconButton(
            onClick = onDismiss,
            modifier = Modifier.size(24.dp).testTag("dialog_close_btn")
          ) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMutedDark)
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 1. Company Name Input (Required)
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          OutlinedTextField(
            value = logoEmoji,
            onValueChange = { logoEmoji = it.take(2) },
            label = { Text("Emoji", fontSize = 11.sp) },
            singleLine = true,
            modifier = Modifier.width(64.dp),
            colors = dialogTextFieldColors()
          )
          OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Company Name *", fontSize = 11.sp) },
            placeholder = { Text("e.g. Google India, Razorpay, Zepto", fontSize = 12.sp) },
            isError = hasAttemptedSubmit && !isNameValid,
            supportingText = if (hasAttemptedSubmit && !isNameValid) {
              { Text("Company name is required", color = TitanCrimson, fontSize = 10.sp) }
            } else null,
            singleLine = true,
            modifier = Modifier
              .weight(1f)
              .testTag("dialog_input_company_name"),
            colors = dialogTextFieldColors()
          )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 2. Industry / Sector Input (Required)
        OutlinedTextField(
          value = industry,
          onValueChange = { industry = it },
          label = { Text("Industry / Sector *", fontSize = 11.sp) },
          placeholder = { Text("e.g. Fintech & Payments, Enterprise SaaS & AI", fontSize = 12.sp) },
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("dialog_input_industry"),
          colors = dialogTextFieldColors()
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Quick Industry Preset Chips
        val industryPresets = listOf(
          "Enterprise SaaS & AI",
          "Fintech & Payments",
          "Cloud & DeepTech",
          "Consumer & E-Commerce",
          "Semiconductors",
          "Consulting & GCC"
        )
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "PRESETS:",
            style = MaterialTheme.typography.labelSmall,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = TextMutedDark
          )
          industryPresets.forEach { preset ->
            val isPresetSelected = industry.equals(preset, ignoreCase = true)
            FilterChip(
              selected = isPresetSelected,
              onClick = { industry = preset },
              label = { Text(preset, fontSize = 10.sp) },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = TitanCyan.copy(alpha = 0.2f),
                selectedLabelColor = TitanCyan,
                containerColor = SlateElevated,
                labelColor = TextSecondaryDark
              ),
              border = FilterChipDefaults.filterChipBorder(
                enabled = true,
                selected = isPresetSelected,
                borderColor = SlateBorder,
                selectedBorderColor = TitanCyan
              )
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 3. Note About Why It Is a Target (Required/Highlighted)
        OutlinedTextField(
          value = targetReasonNote,
          onValueChange = { targetReasonNote = it },
          label = { Text("Why is this a target? (Strategic Rationale & Notes) *", fontSize = 11.sp) },
          placeholder = {
            Text(
              "Explain why this company is a strategic target (e.g. aggressive expansion in Bengaluru, 40+ strategic analyst openings, high compensation upside, alumni referrals)...",
              fontSize = 11.sp
            )
          },
          minLines = 3,
          maxLines = 5,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("dialog_input_target_reason_notes"),
          colors = dialogTextFieldColors()
        )

        Spacer(modifier = Modifier.height(6.dp))
        Text(
          text = "Saved in offline Room database for fast search, filtering, and interview strategy.",
          style = MaterialTheme.typography.labelSmall,
          fontSize = 10.sp,
          color = TextMutedDark
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Optional Advanced Strategy Settings Accordion
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(SlateElevated)
            .clickable { showAdvancedDetails = !showAdvancedDetails }
            .padding(horizontal = 12.dp, vertical = 10.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              if (showAdvancedDetails) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
              contentDescription = null,
              tint = TitanCyan,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = if (showAdvancedDetails) "Hide Additional Strategy Details" else "Additional Strategy Details (Optional)",
              style = MaterialTheme.typography.labelMedium,
              fontWeight = FontWeight.SemiBold,
              color = TextPrimaryDark
            )
          }
          Text(
            text = if (showAdvancedDetails) "COLLAPSE" else "EXPAND",
            style = MaterialTheme.typography.labelSmall,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = TitanCyan
          )
        }

        if (showAdvancedDetails) {
          Spacer(modifier = Modifier.height(10.dp))

          // Target Role & Domain
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            OutlinedTextField(
              value = targetRoleTitle,
              onValueChange = { targetRoleTitle = it },
              label = { Text("Target Role Title", fontSize = 11.sp) },
              singleLine = true,
              modifier = Modifier.weight(1f),
              colors = dialogTextFieldColors()
            )
            OutlinedTextField(
              value = tickerOrDomain,
              onValueChange = { tickerOrDomain = it },
              label = { Text("Domain / URL", fontSize = 11.sp) },
              placeholder = { Text("company.com", fontSize = 11.sp) },
              singleLine = true,
              modifier = Modifier.weight(1f),
              colors = dialogTextFieldColors()
            )
          }

          Spacer(modifier = Modifier.height(8.dp))

          // Hub & Compensation
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            OutlinedTextField(
              value = bengaluruHub,
              onValueChange = { bengaluruHub = it },
              label = { Text("Location / Tech Hub", fontSize = 11.sp) },
              singleLine = true,
              modifier = Modifier.weight(1f),
              colors = dialogTextFieldColors()
            )
            OutlinedTextField(
              value = targetCompensationRange,
              onValueChange = { targetCompensationRange = it },
              label = { Text("Compensation Target", fontSize = 11.sp) },
              singleLine = true,
              modifier = Modifier.weight(1f),
              colors = dialogTextFieldColors()
            )
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Priority Tier Pickers
          Text("Priority Tier", style = MaterialTheme.typography.labelSmall, color = TitanCyan, fontWeight = FontWeight.Bold)
          Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            listOf("TIER_1_DREAM" to "Tier 1 Dream", "TIER_2_STRATEGIC" to "Strategic", "TIER_3_WATCHLIST" to "Watchlist").forEach { (tierKey, label) ->
              FilterChip(
                selected = priorityTier == tierKey,
                onClick = { priorityTier = tierKey },
                label = { Text(label, fontSize = 10.sp) },
                colors = FilterChipDefaults.filterChipColors(
                  selectedContainerColor = TitanGold.copy(alpha = 0.2f),
                  selectedLabelColor = TitanGold
                )
              )
            }
          }

          Spacer(modifier = Modifier.height(6.dp))

          // Tracking Status
          Text("Pipeline Status", style = MaterialTheme.typography.labelSmall, color = TitanCyan, fontWeight = FontWeight.Bold)
          Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            listOf("ACTIVE_TARGET", "INTERVIEWING", "APPLICATION_STAGED", "NETWORKING", "EXPLORING").forEach { st ->
              FilterChip(
                selected = trackingStatus == st,
                onClick = { trackingStatus = st },
                label = { Text(formatTrackingStatus(st), fontSize = 10.sp) }
              )
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Optional Initial Market Intelligence Checkbox Section
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(8.dp))
              .background(SlateElevated)
              .clickable { includeMarketIntel = !includeMarketIntel }
              .padding(10.dp)
          ) {
            Icon(
              Icons.Default.AutoAwesome,
              contentDescription = null,
              tint = if (includeMarketIntel) TitanCyan else TextMutedDark,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Attach Initial Market Intelligence Signal",
              style = MaterialTheme.typography.labelMedium,
              fontWeight = FontWeight.Bold,
              color = if (includeMarketIntel) TextPrimaryDark else TextMutedDark
            )
          }

          if (includeMarketIntel) {
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
              value = headlineSummary,
              onValueChange = { headlineSummary = it },
              label = { Text("Market Intel Headline", fontSize = 11.sp) },
              placeholder = { Text("e.g. Scaling Bengaluru AI CoE with 400+ new hires", fontSize = 12.sp) },
              modifier = Modifier.fillMaxWidth(),
              colors = dialogTextFieldColors()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Market Sentiment Row
            Text("Market Sentiment", style = MaterialTheme.typography.labelSmall, color = TitanEmerald, fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              listOf("BULLISH", "NEUTRAL", "CAUTIOUS").forEach { snt ->
                FilterChip(
                  selected = marketSentiment == snt,
                  onClick = { marketSentiment = snt },
                  label = { Text(snt, fontSize = 10.sp) }
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Actions: Cancel & Save
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          OutlinedButton(
            onClick = onDismiss,
            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondaryDark),
            border = BorderStroke(1.dp, SlateBorder),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
              .weight(1f)
              .height(46.dp)
              .testTag("dialog_cancel_btn")
          ) {
            Text("Cancel", fontWeight = FontWeight.SemiBold)
          }

          Button(
            onClick = {
              hasAttemptedSubmit = true
              if (isNameValid) {
                val companyId = "tc_${UUID.randomUUID().toString().take(8)}"
                val finalNotes = targetReasonNote.trim()
                val targetCompany = TargetCompany(
                  id = companyId,
                  name = name.trim(),
                  tickerOrDomain = tickerOrDomain.trim(),
                  logoEmoji = if (logoEmoji.isNotBlank()) logoEmoji else "🏢",
                  industry = industry.trim().ifBlank { "Technology" },
                  bengaluruHub = bengaluruHub.trim().ifBlank { "Bengaluru, India" },
                  priorityTier = priorityTier,
                  trackingStatus = trackingStatus,
                  targetRoleTitle = targetRoleTitle.trim().ifBlank { "Associate Business Analyst" },
                  targetCompensationRange = targetCompensationRange.trim(),
                  culturalFitScore = culturalFitScore.toIntOrNull() ?: 90,
                  hiringVelocity = hiringVelocity,
                  notes = finalNotes,
                  isWatched = true
                )

                val marketIntel = if (includeMarketIntel && headlineSummary.isNotBlank()) {
                  TargetCompanyMarketIntel(
                    id = "intel_${UUID.randomUUID().toString().take(8)}",
                    targetCompanyId = companyId,
                    companyName = name.trim(),
                    headlineSummary = headlineSummary.trim(),
                    marketSentiment = marketSentiment,
                    strategicExpansionSignals = strategicExpansionSignals.trim(),
                    interviewTalkingPoints = interviewTalkingPoints.trim(),
                    sourceGrounding = "User Logged & Field Intelligence",
                    lastRefreshedTimestamp = System.currentTimeMillis()
                  )
                } else null

                onSave(targetCompany, marketIntel)
              }
            },
            enabled = isNameValid,
            colors = ButtonDefaults.buttonColors(containerColor = TitanCyan),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
              .weight(1.5f)
              .height(46.dp)
              .testTag("dialog_save_target_company_btn")
              .testTag("save_target_company_submit_btn")
          ) {
            Icon(Icons.Default.Add, contentDescription = null, tint = ObsidianDark, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Save to Room DB", color = ObsidianDark, fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}

/**
 * Modal Dialog to update or record new Market Intelligence for a target company.
 */
@Composable
private fun AddMarketIntelligenceDialog(
  company: TargetCompany,
  onDismiss: () -> Unit,
  onSave: (TargetCompanyMarketIntel) -> Unit
) {
  var headlineSummary by remember { mutableStateOf("") }
  var marketSentiment by remember { mutableStateOf("BULLISH") }
  var strategicExpansionSignals by remember { mutableStateOf("") }
  var executiveLeadershipShifts by remember { mutableStateOf("") }
  var hiringTrends by remember { mutableStateOf("") }
  var interviewTalkingPoints by remember { mutableStateOf("") }
  var sourceGrounding by remember { mutableStateOf("Verified Industry Intel & Grounding") }

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(14.dp),
      color = SlateCard,
      border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder),
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 16.dp)
        .testTag("add_market_intel_dialog")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(18.dp)
          .verticalScroll(rememberScrollState())
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "Log Market Intelligence",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = TextPrimaryDark
            )
            Text(
              text = company.name,
              style = MaterialTheme.typography.labelSmall,
              color = TitanCyan,
              fontWeight = FontWeight.SemiBold
            )
          }
          IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMutedDark)
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
          value = headlineSummary,
          onValueChange = { headlineSummary = it },
          label = { Text("Market Movement / Headline*", fontSize = 11.sp) },
          placeholder = { Text("e.g. Won $500M enterprise migration deal in APAC", fontSize = 12.sp) },
          modifier = Modifier.fillMaxWidth().testTag("intel_dialog_headline_input"),
          colors = dialogTextFieldColors()
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text("Market Sentiment", style = MaterialTheme.typography.labelSmall, color = TitanEmerald, fontWeight = FontWeight.Bold)
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          listOf("BULLISH", "NEUTRAL", "CAUTIOUS").forEach { snt ->
            FilterChip(
              selected = marketSentiment == snt,
              onClick = { marketSentiment = snt },
              label = { Text(snt, fontSize = 10.sp) }
            )
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
          value = strategicExpansionSignals,
          onValueChange = { strategicExpansionSignals = it },
          label = { Text("Strategic Expansion Signals", fontSize = 11.sp) },
          placeholder = { Text("Capex investments, hub expansion...", fontSize = 12.sp) },
          modifier = Modifier.fillMaxWidth().height(65.dp),
          colors = dialogTextFieldColors()
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
          value = executiveLeadershipShifts,
          onValueChange = { executiveLeadershipShifts = it },
          label = { Text("Executive Shifts & Appointments", fontSize = 11.sp) },
          placeholder = { Text("New VP or Director hires...", fontSize = 12.sp) },
          modifier = Modifier.fillMaxWidth().height(65.dp),
          colors = dialogTextFieldColors()
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
          value = hiringTrends,
          onValueChange = { hiringTrends = it },
          label = { Text("Hiring Trends & Skill In-Demand", fontSize = 11.sp) },
          placeholder = { Text("Heavy intake for Prompt Ops & SQL analytics...", fontSize = 12.sp) },
          modifier = Modifier.fillMaxWidth().height(65.dp),
          colors = dialogTextFieldColors()
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
          value = interviewTalkingPoints,
          onValueChange = { interviewTalkingPoints = it },
          label = { Text("Interview Talking Points", fontSize = 11.sp) },
          placeholder = { Text("Framing your BBA skills against company objectives...", fontSize = 12.sp) },
          modifier = Modifier.fillMaxWidth().height(65.dp),
          colors = dialogTextFieldColors()
        )

        Spacer(modifier = Modifier.height(18.dp))

        Button(
          onClick = {
            if (headlineSummary.isNotBlank()) {
              val intel = TargetCompanyMarketIntel(
                id = "intel_${UUID.randomUUID().toString().take(8)}",
                targetCompanyId = company.id,
                companyName = company.name,
                headlineSummary = headlineSummary.trim(),
                marketSentiment = marketSentiment,
                strategicExpansionSignals = strategicExpansionSignals.trim(),
                executiveLeadershipShifts = executiveLeadershipShifts.trim(),
                hiringTrends = hiringTrends.trim(),
                interviewTalkingPoints = interviewTalkingPoints.trim(),
                sourceGrounding = sourceGrounding.trim(),
                lastRefreshedTimestamp = System.currentTimeMillis()
              )
              onSave(intel)
            }
          },
          enabled = headlineSummary.isNotBlank(),
          colors = ButtonDefaults.buttonColors(containerColor = TitanCyan),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier.fillMaxWidth().height(46.dp).testTag("save_market_intel_submit_btn")
        ) {
          Text("Save Intelligence Record", color = ObsidianDark, fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}

// Helpers & Formatters
private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

private fun formatTrackingStatus(status: String): String {
  return when (status) {
    "ACTIVE_TARGET" -> "Active Target"
    "INTERVIEWING" -> "Interviewing"
    "APPLICATION_STAGED" -> "Staged App"
    "NETWORKING" -> "Networking"
    "EXPLORING" -> "Exploring"
    "OFFER_STAGE" -> "Offer Received"
    else -> status
  }
}

private fun getStatusContainerColor(status: String): Color {
  return when (status) {
    "ACTIVE_TARGET" -> TitanCyan.copy(alpha = 0.15f)
    "INTERVIEWING" -> TitanGold.copy(alpha = 0.15f)
    "OFFER_STAGE" -> TitanEmerald.copy(alpha = 0.2f)
    "APPLICATION_STAGED" -> TitanIndigo.copy(alpha = 0.15f)
    else -> SlateElevated
  }
}

private fun getStatusTextColor(status: String): Color {
  return when (status) {
    "ACTIVE_TARGET" -> TitanCyan
    "INTERVIEWING" -> TitanGold
    "OFFER_STAGE" -> TitanEmerald
    "APPLICATION_STAGED" -> TitanIndigo
    else -> TextSecondaryDark
  }
}

private fun formatTimestamp(timestamp: Long): String {
  return SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(timestamp))
}

@Composable
private fun dialogTextFieldColors() = OutlinedTextFieldDefaults.colors(
  focusedContainerColor = SlateElevated,
  unfocusedContainerColor = SlateElevated,
  focusedBorderColor = TitanCyan,
  unfocusedBorderColor = SlateBorder,
  focusedTextColor = TextPrimaryDark,
  unfocusedTextColor = TextPrimaryDark,
  focusedLabelColor = TitanCyan,
  unfocusedLabelColor = TextMutedDark
)
