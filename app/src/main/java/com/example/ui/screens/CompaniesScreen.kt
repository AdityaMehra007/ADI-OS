package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkAdded
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.TravelExplore
import androidx.compose.material.icons.filled.Newspaper
import com.example.ui.components.CompanyIntelFirestoreSyncBadge
import com.example.ui.components.CompanyIntelFirestoreSyncIndicator
import com.example.ui.components.CompanyIntelligenceWidget
import com.example.ui.components.launchCompanyNewsShareSheet
import com.example.ui.components.TargetCompaniesListView
import com.example.ui.components.SectorMarketInsightsView
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ai.CompanyResearchFocus
import com.example.data.model.Company
import com.example.data.model.CompanyBookmark
import com.example.data.model.CompanyIntelReport
import com.example.data.model.GroundedSource
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

@Composable
fun CompaniesScreen(
  viewModel: TitanViewModel
) {
  val companies by viewModel.companies.collectAsState()
  val companyBookmarks by viewModel.companyBookmarks.collectAsState()
  val targetCompanies by viewModel.targetCompaniesWithIntel.collectAsState()
  val targetCompanySearchQuery by viewModel.targetCompanySearchQuery.collectAsState()
  val targetCompanyPriorityFilter by viewModel.targetCompanyPriorityFilter.collectAsState()
  val targetCompanyStatusFilter by viewModel.targetCompanyStatusFilter.collectAsState()
  val targetCompanySentimentFilter by viewModel.targetCompanySentimentFilter.collectAsState()
  val activeTab by viewModel.companyIntelTab.collectAsState()
  val selectedIntelCompany by viewModel.selectedIntelCompanyName.collectAsState()
  val intelReport by viewModel.companyIntelReport.collectAsState()
  val isFetchingIntel by viewModel.isFetchingIntel.collectAsState()
  val isFetchingIntelFromFirestore by viewModel.isFetchingCompanyIntelFromFirestore.collectAsState()
  val firestoreIntelSyncState by viewModel.firestoreCompanyIntelSyncState.collectAsState()
  val firestoreIntelProgress by viewModel.companyIntelFirestoreProgress.collectAsState()
  val firestoreIntelStatus by viewModel.companyIntelFirestoreStatus.collectAsState()

  var showWatchedOnly by remember { mutableStateOf(false) }
  var showSaveCompanyDialog by remember { mutableStateOf(false) }

  // Automatically fetch live search intel on first visit to tab 2 if empty
  LaunchedEffect(activeTab, selectedIntelCompany) {
    if (activeTab == 2 && intelReport == null && !isFetchingIntel && !isFetchingIntelFromFirestore) {
      viewModel.fetchCompanyIntel(selectedIntelCompany)
    }
  }

  val filteredCompanies = if (showWatchedOnly) {
    companies.filter { it.isWatched }
  } else {
    companies
  }

  if (showSaveCompanyDialog) {
    SaveCompanyDialog(
      onDismiss = { showSaveCompanyDialog = false },
      onSave = { name, industry, website, careersUrl, tier, priority, notes, fetchNews ->
        viewModel.saveCompany(
          name = name,
          industry = industry,
          website = website,
          careersUrl = careersUrl,
          tier = tier,
          strategicPriority = priority,
          watchNotes = notes,
          fetchNewsImmediately = fetchNews
        )
        showSaveCompanyDialog = false
      }
    )
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(ObsidianDark)
      .padding(horizontal = 16.dp, vertical = 8.dp)
      .testTag("companies_screen")
  ) {
    // Header & Grounding Engine Status Badge + Firestore Sync Badge
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = "COMPANY INTELLIGENCE & RADAR",
          style = MaterialTheme.typography.labelSmall,
          color = TitanCyan,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = if (activeTab == 0) "${companies.size} Tier S+/S/A Tracked" else "Live Search Grounding Intel",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
          color = TextPrimaryDark
        )
      }

      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        Button(
          onClick = { showSaveCompanyDialog = true },
          colors = ButtonDefaults.buttonColors(containerColor = TitanCyan),
          shape = RoundedCornerShape(6.dp),
          contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp),
          modifier = Modifier.testTag("header_save_company_btn")
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Add, contentDescription = null, tint = ObsidianDark, modifier = Modifier.size(13.dp))
            Spacer(modifier = Modifier.width(3.dp))
            Text(
              text = "Save Target",
              style = MaterialTheme.typography.labelSmall,
              color = ObsidianDark,
              fontWeight = FontWeight.Bold,
              fontSize = 10.sp
            )
          }
        }

        CompanyIntelFirestoreSyncBadge(
          isSyncing = isFetchingIntelFromFirestore,
          targetCompany = selectedIntelCompany,
          onClick = { viewModel.fetchCompanyIntelFromFirestore(selectedIntelCompany) }
        )

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(TitanCyan.copy(alpha = 0.12f))
            .border(1.dp, TitanCyan.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.TravelExplore,
              contentDescription = null,
              tint = TitanCyan,
              modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "GROUNDING",
              style = MaterialTheme.typography.labelSmall,
              color = TitanCyan,
              fontSize = 9.sp,
              fontWeight = FontWeight.Black
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Top Navigation Tabs
    TabRow(
      selectedTabIndex = activeTab.coerceIn(0, 3),
      containerColor = SlateCard,
      contentColor = TitanCyan,
      indicator = { tabPositions ->
        val safeIndex = activeTab.coerceIn(0, tabPositions.lastIndex)
        TabRowDefaults.SecondaryIndicator(
          Modifier.tabIndicatorOffset(tabPositions[safeIndex]),
          color = TitanCyan,
          height = 2.dp
        )
      },
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(8.dp))
        .border(1.dp, SlateBorder, RoundedCornerShape(8.dp))
    ) {
      Tab(
        selected = activeTab == 0,
        onClick = { viewModel.setCompanyIntelTab(0) },
        text = {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(13.dp), tint = if (activeTab == 0) TitanCyan else TextMutedDark)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "Target Tracker",
              style = MaterialTheme.typography.labelMedium,
              fontWeight = if (activeTab == 0) FontWeight.Bold else FontWeight.Normal,
              color = if (activeTab == 0) TitanCyan else TextMutedDark,
              fontSize = 11.sp
            )
          }
        }
      )

      Tab(
        selected = activeTab == 1,
        onClick = { viewModel.setCompanyIntelTab(1) },
        text = {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Business, contentDescription = null, modifier = Modifier.size(13.dp), tint = if (activeTab == 1) TitanCyan else TextMutedDark)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "Roster",
              style = MaterialTheme.typography.labelMedium,
              fontWeight = if (activeTab == 1) FontWeight.Bold else FontWeight.Normal,
              color = if (activeTab == 1) TitanCyan else TextMutedDark,
              fontSize = 11.sp
            )
          }
        }
      )

      Tab(
        selected = activeTab == 2,
        onClick = { viewModel.setCompanyIntelTab(2) },
        text = {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(13.dp), tint = if (activeTab == 2) TitanGold else TextMutedDark)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "Live Search",
              style = MaterialTheme.typography.labelMedium,
              fontWeight = if (activeTab == 2) FontWeight.Bold else FontWeight.Normal,
              color = if (activeTab == 2) TitanGold else TextMutedDark,
              fontSize = 11.sp
            )
          }
        }
      )

      Tab(
        selected = activeTab == 3,
        onClick = { viewModel.setCompanyIntelTab(3) },
        text = {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Newspaper, contentDescription = null, modifier = Modifier.size(13.dp), tint = if (activeTab == 3) TitanCyan else TextMutedDark)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "Market Insights",
              style = MaterialTheme.typography.labelMedium,
              fontWeight = if (activeTab == 3) FontWeight.Bold else FontWeight.Normal,
              color = if (activeTab == 3) TitanCyan else TextMutedDark,
              fontSize = 11.sp
            )
          }
        }
      )
    }

    Spacer(modifier = Modifier.height(12.dp))

    when (activeTab) {
      0 -> {
        // TAB 0: TARGET COMPANIES & ROOM-PERSISTED MARKET INTELLIGENCE TRACKER
        TargetCompaniesListView(
          targetCompanies = targetCompanies,
          searchQuery = targetCompanySearchQuery,
          onSearchQueryChanged = { viewModel.setTargetCompanySearchQuery(it) },
          priorityFilter = targetCompanyPriorityFilter,
          onPriorityFilterChanged = { viewModel.setTargetCompanyPriorityFilter(it) },
          statusFilter = targetCompanyStatusFilter,
          onStatusFilterChanged = { viewModel.setTargetCompanyStatusFilter(it) },
          sentimentFilter = targetCompanySentimentFilter,
          onSentimentFilterChanged = { viewModel.setTargetCompanySentimentFilter(it) },
          onSaveTargetCompany = { comp, intel -> viewModel.saveTargetCompany(comp, intel) },
          onUpdateStatus = { id, st -> viewModel.updateTargetCompanyStatus(id, st) },
          onToggleWatch = { id, watched -> viewModel.toggleTargetCompanyWatch(id, watched) },
          onDeleteTargetCompany = { id -> viewModel.deleteTargetCompany(id) },
          onSaveMarketIntel = { intel -> viewModel.saveTargetCompanyMarketIntel(intel) }
        )
      }
      1 -> {
        // TAB 1: COMPANY DIRECTORY & TIER MATRIX
        CompanyDirectoryView(
          companies = filteredCompanies,
          bookmarks = companyBookmarks,
          totalTracked = companies.size,
          showWatchedOnly = showWatchedOnly,
          onToggleFilterWatched = { showWatchedOnly = !showWatchedOnly },
          onToggleWatch = { companyId, watched ->
            viewModel.toggleCompanyWatch(companyId, watched)
            companies.find { it.id == companyId }?.let { comp ->
              viewModel.toggleCompanyBookmark(comp)
            }
          },
          onUpdateBookmarkNotes = { companyId, notes ->
            viewModel.updateCompanyBookmarkNotes(companyId, notes)
          },
          onOpenLiveIntel = { companyName ->
            viewModel.selectCompanyForIntel(companyName, autoFetch = true)
          },
          onSaveCompanyClick = { showSaveCompanyDialog = true },
          onFetchNews = { companyName ->
            viewModel.fetchAndSaveCompanyNewsSummary(companyName)
          },
          onDeleteCompany = { companyId ->
            viewModel.deleteCompany(companyId)
          }
        )
      }
      2 -> {
        // TAB 2: LIVE SEARCH GROUNDING INTEL
        Column(
          modifier = Modifier.fillMaxSize(),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          CompanyIntelligenceWidget(
            viewModel = viewModel,
            modifier = Modifier.fillMaxWidth()
          )
          Box(modifier = Modifier.weight(1f)) {
            LiveSearchGroundingIntelView(
              viewModel = viewModel,
              companies = companies,
              selectedCompany = selectedIntelCompany,
              intelReport = intelReport,
              isFetching = isFetchingIntel
            )
          }
        }
      }
      else -> {
        // TAB 3: MARKET INSIGHTS (GEMINI API SECTOR NEWS SUMMARIZER)
        SectorMarketInsightsView(
          viewModel = viewModel
        )
      }
    }
  }
}

@Composable
fun CompanyDirectoryView(
  companies: List<Company>,
  bookmarks: List<CompanyBookmark>,
  totalTracked: Int,
  showWatchedOnly: Boolean,
  onToggleFilterWatched: () -> Unit,
  onToggleWatch: (String, Boolean) -> Unit,
  onUpdateBookmarkNotes: (String, String) -> Unit,
  onOpenLiveIntel: (String) -> Unit,
  onSaveCompanyClick: () -> Unit,
  onFetchNews: (String) -> Unit,
  onDeleteCompany: (String) -> Unit
) {
  var searchQuery by remember { mutableStateOf("") }
  var selectedTier by remember { mutableStateOf("ALL") }

  val tiers = listOf("ALL", "S+", "S", "A", "CUSTOM")

  val displayedCompanies = remember(companies, searchQuery, selectedTier) {
    companies.filter { comp ->
      val matchesSearch = searchQuery.isBlank() ||
        comp.name.contains(searchQuery, ignoreCase = true) ||
        comp.industry.contains(searchQuery, ignoreCase = true)
      val matchesTier = when (selectedTier) {
        "ALL" -> true
        "CUSTOM" -> comp.isCustomAdded
        else -> comp.tier.equals(selectedTier, ignoreCase = true)
      }
      matchesSearch && matchesTier
    }
  }

  Column(modifier = Modifier.fillMaxSize()) {
    // Search Bar
    OutlinedTextField(
      value = searchQuery,
      onValueChange = { searchQuery = it },
      placeholder = { Text("Search tracked companies or industries...", color = TextMutedDark, fontSize = 12.sp) },
      leadingIcon = {
        Icon(Icons.Default.Search, contentDescription = null, tint = TitanCyan, modifier = Modifier.size(16.dp))
      },
      trailingIcon = {
        if (searchQuery.isNotEmpty()) {
          IconButton(onClick = { searchQuery = "" }) {
            Icon(Icons.Default.Close, contentDescription = "Clear", tint = TextMutedDark, modifier = Modifier.size(14.dp))
          }
        }
      },
      singleLine = true,
      colors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = TitanCyan,
        unfocusedBorderColor = SlateBorder,
        focusedTextColor = TextPrimaryDark,
        unfocusedTextColor = TextPrimaryDark,
        focusedContainerColor = SlateCard,
        unfocusedContainerColor = SlateCard
      ),
      shape = RoundedCornerShape(8.dp),
      modifier = Modifier
        .fillMaxWidth()
        .height(48.dp)
        .testTag("company_search_input")
    )

    Spacer(modifier = Modifier.height(8.dp))

    // Tier Filter Chips & Action Row
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      LazyRow(
        modifier = Modifier.weight(1f),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        items(tiers) { tier ->
          val isSelected = selectedTier == tier
          val chipColor = when (tier) {
            "S+" -> TitanCrimson
            "S" -> TitanGold
            "A" -> TitanCyan
            "CUSTOM" -> TitanEmerald
            else -> TitanCyan
          }
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(if (isSelected) chipColor.copy(alpha = 0.2f) else SlateCard)
              .border(1.dp, if (isSelected) chipColor else SlateBorder, RoundedCornerShape(6.dp))
              .clickable { selectedTier = tier }
              .padding(horizontal = 8.dp, vertical = 4.dp)
          ) {
            Text(
              text = tier,
              style = MaterialTheme.typography.labelSmall,
              color = if (isSelected) chipColor else TextSecondaryDark,
              fontSize = 10.sp,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
          }
        }
      }

      Spacer(modifier = Modifier.width(6.dp))

      Row(
        modifier = Modifier
          .clip(RoundedCornerShape(6.dp))
          .background(SlateCard)
          .border(1.dp, SlateBorder, RoundedCornerShape(8.dp))
          .clickable { onToggleFilterWatched() }
          .padding(horizontal = 8.dp, vertical = 4.dp)
      ) {
        Icon(
          imageVector = if (showWatchedOnly) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
          contentDescription = null,
          tint = if (showWatchedOnly) TitanGold else TextSecondaryDark,
          modifier = Modifier.size(13.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
          text = if (showWatchedOnly) "Watched" else "Watchlist",
          style = MaterialTheme.typography.labelSmall,
          color = if (showWatchedOnly) TitanGold else TextSecondaryDark,
          fontWeight = FontWeight.Bold,
          fontSize = 10.sp
        )
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "Showing ${displayedCompanies.size} of $totalTracked tracked companies",
        style = MaterialTheme.typography.bodySmall,
        color = TextMutedDark,
        fontSize = 11.sp
      )

      Button(
        onClick = onSaveCompanyClick,
        colors = ButtonDefaults.buttonColors(containerColor = SlateElevated),
        border = androidx.compose.foundation.BorderStroke(1.dp, TitanCyan.copy(alpha = 0.6f)),
        shape = RoundedCornerShape(6.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp),
        modifier = Modifier.height(28.dp).testTag("save_company_directory_btn")
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Add, contentDescription = null, tint = TitanCyan, modifier = Modifier.size(12.dp))
          Spacer(modifier = Modifier.width(3.dp))
          Text("+ Add Target", color = TitanCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
      }
    }

    Spacer(modifier = Modifier.height(8.dp))

    LazyColumn(
      modifier = Modifier.fillMaxSize(),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      items(displayedCompanies, key = { it.id }) { company ->
        val bookmark = bookmarks.find { it.companyId == company.id }
        CompanyCard(
          company = company,
          bookmark = bookmark,
          onToggleWatch = { onToggleWatch(company.id, !company.isWatched) },
          onUpdateBookmarkNotes = { notes -> onUpdateBookmarkNotes(company.id, notes) },
          onOpenLiveIntel = { onOpenLiveIntel(company.name) },
          onFetchNews = { onFetchNews(company.name) },
          onDelete = { onDeleteCompany(company.id) }
        )
      }

      item {
        Spacer(modifier = Modifier.height(20.dp))
      }
    }
  }
}

@Composable
fun CompanyCard(
  company: Company,
  bookmark: CompanyBookmark? = null,
  onToggleWatch: () -> Unit,
  onUpdateBookmarkNotes: (String) -> Unit = {},
  onOpenLiveIntel: () -> Unit,
  onFetchNews: () -> Unit = {},
  onDelete: () -> Unit = {}
) {
  var isExpanded by remember { mutableStateOf(false) }
  var isEditingNotes by remember { mutableStateOf(false) }
  var customNoteText by remember(bookmark?.personalNotes) { mutableStateOf(bookmark?.personalNotes ?: "") }

  val tierColor = when (company.tier) {
    "S+" -> TitanCrimson
    "S" -> TitanGold
    "A" -> TitanCyan
    else -> TextMutedDark
  }

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .clickable { isExpanded = !isExpanded }
      .testTag("company_card_${company.id}"),
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = SlateCard),
    border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      // Header: Logo emoji + Name + Tier Badge + Watch
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
              .size(38.dp)
              .clip(RoundedCornerShape(8.dp))
              .background(SlateElevated),
            contentAlignment = Alignment.Center
          ) {
            Text(text = company.logoEmoji, fontSize = 20.sp)
          }

          Spacer(modifier = Modifier.width(10.dp))

          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = company.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimaryDark
              )
              Spacer(modifier = Modifier.width(6.dp))
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(4.dp))
                  .background(tierColor.copy(alpha = 0.15f))
                  .padding(horizontal = 6.dp, vertical = 2.dp)
              ) {
                Text(
                  text = "TIER ${company.tier}",
                  style = MaterialTheme.typography.labelSmall,
                  color = tierColor,
                  fontWeight = FontWeight.Black,
                  fontSize = 9.sp
                )
              }
              if (company.isCustomAdded) {
                Spacer(modifier = Modifier.width(4.dp))
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(TitanEmerald.copy(alpha = 0.2f))
                    .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                  Text(
                    text = "SAVED",
                    style = MaterialTheme.typography.labelSmall,
                    color = TitanEmerald,
                    fontWeight = FontWeight.Bold,
                    fontSize = 8.sp
                  )
                }
              }
            }
            Text(
              text = company.industry,
              style = MaterialTheme.typography.bodySmall,
              color = TextMutedDark,
              fontSize = 11.sp
            )
          }
        }

        IconButton(onClick = { onToggleWatch() }, modifier = Modifier.testTag("watch_btn_${company.id}")) {
          Icon(
            imageVector = if (company.isWatched) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
            contentDescription = "Watch",
            tint = if (company.isWatched) TitanGold else TextMutedDark,
            modifier = Modifier.size(20.dp)
          )
        }
      }

      if (bookmark != null) {
        Spacer(modifier = Modifier.height(6.dp))
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(TitanEmerald.copy(alpha = 0.08f))
            .border(1.dp, TitanEmerald.copy(alpha = 0.25f), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Bookmark, contentDescription = null, tint = TitanEmerald, modifier = Modifier.size(11.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              "Room Offline Vault Bookmarked",
              style = MaterialTheme.typography.labelSmall,
              color = TitanEmerald,
              fontWeight = FontWeight.Bold,
              fontSize = 9.sp
            )
          }
          if (bookmark.personalNotes.isNotBlank()) {
            Text(
              "Has Private Note 📝",
              style = MaterialTheme.typography.labelSmall,
              color = TitanGold,
              fontSize = 9.sp
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Bengaluru Presence & Hiring Velocity
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        // Bengaluru Presence
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(6.dp))
            .background(SlateElevated)
            .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
          Icon(Icons.Default.LocationOn, contentDescription = null, tint = TitanCyan, modifier = Modifier.size(12.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = company.bengaluruPresence,
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondaryDark,
            fontSize = 10.sp,
            maxLines = 1
          )
        }

        // Velocity
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(SlateElevated)
            .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
          Icon(Icons.AutoMirrored.Filled.TrendingUp, contentDescription = null, tint = TitanEmerald, modifier = Modifier.size(12.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = company.hiringVelocity,
            style = MaterialTheme.typography.labelSmall,
            color = TitanEmerald,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Compensation, Openings & Live Intel Trigger
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Payments, contentDescription = null, tint = TitanGold, modifier = Modifier.size(12.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = company.compensationTier,
            style = MaterialTheme.typography.labelSmall,
            color = TitanGold,
            fontSize = 10.sp
          )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = "${company.activeOpeningsCount} Openings",
            style = MaterialTheme.typography.labelSmall,
            color = TextMutedDark,
            fontSize = 10.sp
          )
        }
      }

      // GEMINI RELEVANT NEWS SUMMARY SECTION
      if (company.latestNewsSummary.isNotBlank()) {
        Spacer(modifier = Modifier.height(8.dp))
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(TitanGold.copy(alpha = 0.08f))
            .border(1.dp, TitanGold.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
            .padding(10.dp)
        ) {
          Column {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.AutoMirrored.Filled.Article, contentDescription = null, tint = TitanGold, modifier = Modifier.size(13.dp))
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                  text = "LATEST NEWS SUMMARY (GEMINI AI)",
                  style = MaterialTheme.typography.labelSmall,
                  color = TitanGold,
                  fontWeight = FontWeight.Bold,
                  fontSize = 9.sp
                )
              }
              if (company.newsSummaryTimestamp.isNotBlank()) {
                Text(
                  text = company.newsSummaryTimestamp,
                  style = MaterialTheme.typography.labelSmall,
                  color = TextMutedDark,
                  fontSize = 8.sp
                )
              }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = company.latestNewsSummary,
              style = MaterialTheme.typography.bodySmall,
              color = TextPrimaryDark,
              fontSize = 11.sp,
              maxLines = if (isExpanded) Int.MAX_VALUE else 3
            )
          }
        }
      }

      // Quick Action Buttons Row
      Spacer(modifier = Modifier.height(8.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Fetch News (Gemini) button
        Button(
          onClick = onFetchNews,
          colors = ButtonDefaults.buttonColors(containerColor = SlateElevated),
          border = androidx.compose.foundation.BorderStroke(1.dp, TitanGold.copy(alpha = 0.6f)),
          shape = RoundedCornerShape(6.dp),
          contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp),
          modifier = Modifier.weight(1f).height(32.dp).testTag("card_fetch_news_${company.id}")
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = TitanGold, modifier = Modifier.size(12.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = if (company.latestNewsSummary.isBlank()) "Fetch News (AI)" else "Refresh News",
              color = TitanGold,
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }

        // Live 360° Intel button
        Button(
          onClick = onOpenLiveIntel,
          colors = ButtonDefaults.buttonColors(containerColor = SlateElevated),
          border = androidx.compose.foundation.BorderStroke(1.dp, TitanCyan.copy(alpha = 0.6f)),
          shape = RoundedCornerShape(6.dp),
          contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp),
          modifier = Modifier.weight(1f).height(32.dp).testTag("card_live_intel_${company.id}")
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.TravelExplore, contentDescription = null, tint = TitanCyan, modifier = Modifier.size(12.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("360° Intel", color = TitanCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
          }
        }

        // Delete button if custom added
        if (company.isCustomAdded) {
          IconButton(
            onClick = onDelete,
            modifier = Modifier.size(32.dp).background(SlateElevated, RoundedCornerShape(6.dp)).testTag("card_delete_${company.id}")
          ) {
            Icon(Icons.Default.Delete, contentDescription = "Delete company", tint = TitanCrimson, modifier = Modifier.size(14.dp))
          }
        }
      }

      if (isExpanded) {
        Spacer(modifier = Modifier.height(10.dp))

        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(SlateElevated)
            .padding(10.dp)
        ) {
          Column {
            Text(
              text = "STRATEGIC EVALUATION & UPSIDE:",
              style = MaterialTheme.typography.labelSmall,
              color = TitanCyan,
              fontWeight = FontWeight.Bold,
              fontSize = 9.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = company.scoreExplanation,
              style = MaterialTheme.typography.bodySmall,
              color = TextPrimaryDark,
              fontSize = 11.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
              text = "WATCH NOTES:",
              style = MaterialTheme.typography.labelSmall,
              color = TitanGold,
              fontWeight = FontWeight.Bold,
              fontSize = 9.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = company.watchNotes,
              style = MaterialTheme.typography.bodySmall,
              color = TextSecondaryDark,
              fontSize = 11.sp
            )

            Spacer(modifier = Modifier.height(8.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "PERSONAL CANDIDATE STRATEGY (ROOM OFFLINE):",
                style = MaterialTheme.typography.labelSmall,
                color = TitanEmerald,
                fontWeight = FontWeight.Bold,
                fontSize = 9.sp
              )
              if (!isEditingNotes) {
                Text(
                  text = if (customNoteText.isNotBlank()) "Edit Note" else "+ Add Private Note",
                  style = MaterialTheme.typography.labelSmall,
                  color = TitanCyan,
                  fontWeight = FontWeight.Bold,
                  fontSize = 9.sp,
                  modifier = Modifier.clickable { isEditingNotes = true }
                )
              }
            }
            if (isEditingNotes) {
              Spacer(modifier = Modifier.height(4.dp))
              OutlinedTextField(
                value = customNoteText,
                onValueChange = { customNoteText = it },
                placeholder = { Text("e.g. Spoke with VP of Ops, focus on BLR micro-fulfillment hub unit economics...", fontSize = 10.sp, color = TextMutedDark) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(6.dp),
                textStyle = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                colors = OutlinedTextFieldDefaults.colors(
                  focusedContainerColor = SlateCard,
                  unfocusedContainerColor = SlateCard,
                  focusedTextColor = TextPrimaryDark,
                  unfocusedTextColor = TextPrimaryDark
                )
              )
              Spacer(modifier = Modifier.height(4.dp))
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
              ) {
                Button(
                  onClick = {
                    onUpdateBookmarkNotes(customNoteText)
                    isEditingNotes = false
                  },
                  colors = ButtonDefaults.buttonColors(containerColor = TitanEmerald, contentColor = ObsidianDark),
                  shape = RoundedCornerShape(6.dp)
                ) {
                  Text("Save Note to Room", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
              }
            } else if (bookmark?.personalNotes.isNullOrBlank()) {
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = "Tap '+ Add Private Note' to attach private interview tactics or target salary notes offline.",
                style = MaterialTheme.typography.bodySmall,
                color = TextMutedDark,
                fontSize = 10.sp
              )
            } else {
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = bookmark?.personalNotes ?: "",
                style = MaterialTheme.typography.bodySmall,
                color = TextPrimaryDark,
                fontSize = 10.sp
              )
            }
          }
        }
      }
    }
  }
}

@Composable
fun LiveSearchGroundingIntelView(
  viewModel: TitanViewModel,
  companies: List<Company>,
  selectedCompany: String,
  intelReport: CompanyIntelReport?,
  isFetching: Boolean
) {
  var customCompanyInput by remember { mutableStateOf("") }
  val context = LocalContext.current
  val companyResearchState by viewModel.companyResearchUiState.collectAsState()
  var selectedFocus by remember { mutableStateOf(CompanyResearchFocus.ALL) }

  val isFetchingFirestore by viewModel.isFetchingCompanyIntelFromFirestore.collectAsState()
  val firestoreSyncState by viewModel.firestoreCompanyIntelSyncState.collectAsState()
  val firestoreSyncProgress by viewModel.companyIntelFirestoreProgress.collectAsState()
  val firestoreSyncStatus by viewModel.companyIntelFirestoreStatus.collectAsState()

  val quickTargetPicks = remember(companies) {
    val names = companies.map { it.name }.toMutableList()
    if (!names.contains("Zepto")) names.add(0, "Zepto")
    if (!names.contains("Razorpay")) names.add(1, "Razorpay")
    if (!names.contains("Swiggy")) names.add("Swiggy")
    if (!names.contains("Meesho")) names.add("Meesho")
    if (!names.contains("CRED")) names.add("CRED")
    if (!names.contains("Flipkart")) names.add("Flipkart")
    if (!names.contains("Peak XV")) names.add("Peak XV")
    if (!names.contains("McKinsey")) names.add("McKinsey")
    if (!names.contains("Bain")) names.add("Bain")
    if (!names.contains("Google")) names.add("Google")
    if (!names.contains("Microsoft")) names.add("Microsoft")
    names
  }

  LazyColumn(
    modifier = Modifier.fillMaxSize(),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // 1. Selector Carousel & Search Bar
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SlateCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Text(
            text = "SELECT OR SEARCH TARGET COMPANY",
            style = MaterialTheme.typography.labelSmall,
            color = TitanCyan,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp
          )
          Spacer(modifier = Modifier.height(8.dp))

          // Quick Selection Chips Carousel
          LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            items(quickTargetPicks) { name ->
              val isSelected = selectedCompany.equals(name, ignoreCase = true)
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(8.dp))
                  .background(if (isSelected) TitanCyan.copy(alpha = 0.2f) else SlateElevated)
                  .border(
                    1.dp,
                    if (isSelected) TitanCyan else SlateBorder,
                    RoundedCornerShape(8.dp)
                  )
                  .clickable {
                    customCompanyInput = ""
                    viewModel.performCompanyResearch(name, selectedFocus)
                  }
                  .padding(horizontal = 12.dp, vertical = 6.dp)
              ) {
                Text(
                  text = name,
                  style = MaterialTheme.typography.labelSmall,
                  color = if (isSelected) TitanCyan else TextSecondaryDark,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Focus Area Selector Chips
          Text(
            text = "RESEARCH FOCUS (GEMINI SERVICE WRAPPER)",
            style = MaterialTheme.typography.labelSmall,
            color = TitanGold,
            fontWeight = FontWeight.Bold,
            fontSize = 9.sp
          )
          Spacer(modifier = Modifier.height(6.dp))

          LazyRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            items(CompanyResearchFocus.entries.toTypedArray()) { focus ->
              val isChosen = selectedFocus == focus
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(6.dp))
                  .background(if (isChosen) TitanGold.copy(alpha = 0.2f) else SlateElevated)
                  .border(
                    1.dp,
                    if (isChosen) TitanGold else SlateBorder,
                    RoundedCornerShape(6.dp)
                  )
                  .clickable {
                    selectedFocus = focus
                    val target = customCompanyInput.trim().ifEmpty { selectedCompany }
                    viewModel.performCompanyResearch(target, focus)
                  }
                  .padding(horizontal = 8.dp, vertical = 4.dp)
              ) {
                Text(
                  text = focus.displayName,
                  style = MaterialTheme.typography.labelSmall,
                  color = if (isChosen) TitanGold else TextSecondaryDark,
                  fontSize = 10.sp,
                  fontWeight = if (isChosen) FontWeight.Bold else FontWeight.Normal
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Custom Search Input Row
          OutlinedTextField(
            value = customCompanyInput,
            onValueChange = { customCompanyInput = it },
            placeholder = { Text("Enter any company (e.g. Zepto, Razorpay, Google)...", color = TextMutedDark, fontSize = 12.sp) },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = TitanCyan,
              unfocusedBorderColor = SlateBorder,
              focusedTextColor = TextPrimaryDark,
              unfocusedTextColor = TextPrimaryDark,
              focusedContainerColor = SlateElevated,
              unfocusedContainerColor = SlateElevated
            ),
            modifier = Modifier
              .fillMaxWidth()
              .height(50.dp),
            shape = RoundedCornerShape(8.dp)
          )

          Spacer(modifier = Modifier.height(10.dp))

          // Dual Action Row: Firestore Sync & AI Live Grounding
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Button(
              onClick = {
                val target = customCompanyInput.trim().ifEmpty { selectedCompany }
                viewModel.fetchCompanyIntelFromFirestore(target)
              },
              colors = ButtonDefaults.buttonColors(containerColor = SlateElevated),
              border = androidx.compose.foundation.BorderStroke(1.dp, TitanCyan.copy(alpha = 0.5f)),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier
                .weight(1f)
                .height(44.dp)
                .testTag("fetch_company_intel_firestore_btn"),
              enabled = !isFetching && !isFetchingFirestore
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.CloudSync,
                  contentDescription = null,
                  tint = TitanCyan,
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "Fetch Firestore",
                  color = TitanCyan,
                  fontWeight = FontWeight.Bold,
                  fontSize = 11.sp
                )
              }
            }

            Button(
              onClick = {
                val target = customCompanyInput.trim().ifEmpty { selectedCompany }
                viewModel.performCompanyResearch(target, selectedFocus)
              },
              colors = ButtonDefaults.buttonColors(containerColor = TitanCyan),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier
                .weight(1f)
                .height(44.dp)
                .testTag("live_ground_ai_btn"),
              enabled = !isFetching && !isFetchingFirestore
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.Search,
                  contentDescription = null,
                  tint = ObsidianDark,
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "Live Ground (AI)",
                  color = ObsidianDark,
                  fontWeight = FontWeight.Bold,
                  fontSize = 11.sp
                )
              }
            }
          }
        }
      }
    }

    // 2. Animated Firestore Sync & Loading Telemetry Indicator
    val isAnyFetching = isFetching || isFetchingFirestore
    if (isAnyFetching) {
      item {
        CompanyIntelFirestoreSyncIndicator(
          isSyncing = true,
          syncState = firestoreSyncState,
          targetCompany = customCompanyInput.trim().ifEmpty { selectedCompany },
          syncProgress = if (isFetchingFirestore) firestoreSyncProgress else 0.72f,
          statusMessage = if (isFetchingFirestore) firestoreSyncStatus else "Live Grounding $selectedCompany with Gemini AI...",
          onRetry = { viewModel.fetchCompanyIntelFromFirestore(selectedCompany) },
          modifier = Modifier.fillMaxWidth()
        )
      }
    }

    // 3. Grounded Intel Report Dossier
    if (intelReport != null && !isAnyFetching) {
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = SlateCard),
          border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            // Dossier Header
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(
                    text = intelReport.companyName,
                    style = MaterialTheme.typography.headlineSmall,
                    color = TextPrimaryDark,
                    fontWeight = FontWeight.Black
                  )
                  Spacer(modifier = Modifier.width(8.dp))
                  Box(
                    modifier = Modifier
                      .clip(RoundedCornerShape(4.dp))
                      .background(TitanEmerald.copy(alpha = 0.15f))
                      .padding(horizontal = 6.dp, vertical = 2.dp)
                  ) {
                    Text(
                      text = "${intelReport.confidenceScore}% GROUNDED",
                      style = MaterialTheme.typography.labelSmall,
                      color = TitanEmerald,
                      fontWeight = FontWeight.Bold,
                      fontSize = 9.sp
                    )
                  }
                }
                Text(
                  text = intelReport.timestamp,
                  style = MaterialTheme.typography.labelSmall,
                  color = TitanCyan,
                  fontSize = 10.sp
                )
              }

              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                IconButton(
                  onClick = {
                    launchCompanyNewsShareSheet(
                      context = context,
                      companyName = intelReport.companyName,
                      newsSummary = intelReport.businessNewsSummary,
                      hiringUpdates = intelReport.hiringUpdates,
                      interviewAngle = intelReport.interviewAngles.firstOrNull(),
                      sources = intelReport.groundedSources,
                      timestamp = intelReport.timestamp
                    )
                  },
                  modifier = Modifier
                    .size(36.dp)
                    .background(SlateElevated, CircleShape)
                    .testTag("share_company_dossier_btn")
                ) {
                  Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Share via Android ShareSheet",
                    tint = TitanGold,
                    modifier = Modifier.size(18.dp)
                  )
                }

                IconButton(
                  onClick = { viewModel.fetchCompanyIntelFromFirestore(intelReport.companyName) },
                  modifier = Modifier
                    .size(36.dp)
                    .background(SlateElevated, CircleShape)
                    .testTag("firestore_dossier_sync_btn")
                ) {
                  Icon(
                    imageVector = Icons.Default.CloudSync,
                    contentDescription = "Sync from Firestore",
                    tint = TitanCyan,
                    modifier = Modifier.size(18.dp)
                  )
                }

                IconButton(
                  onClick = { viewModel.fetchCompanyIntel(intelReport.companyName) },
                  modifier = Modifier
                    .size(36.dp)
                    .background(SlateElevated, CircleShape)
                    .testTag("refresh_intel_btn")
                ) {
                  Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refresh",
                    tint = TitanCyan,
                    modifier = Modifier.size(18.dp)
                  )
                }
              }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // SECTION 1: LIVE BUSINESS & FINANCIAL NEWS
            IntelSectionCard(
              title = "LIVE BUSINESS & FINANCIAL SIGNALS",
              icon = Icons.Default.Bolt,
              iconTint = TitanGold
            ) {
              Column {
                Text(
                  text = intelReport.businessNewsSummary,
                  style = MaterialTheme.typography.bodySmall,
                  color = TextPrimaryDark,
                  lineHeight = 18.sp,
                  fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.End
                ) {
                  Box(
                    modifier = Modifier
                      .clip(RoundedCornerShape(6.dp))
                      .background(SlateElevated)
                      .border(0.8.dp, TitanGold.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                      .clickable {
                        launchCompanyNewsShareSheet(
                          context = context,
                          companyName = intelReport.companyName,
                          newsSummary = intelReport.businessNewsSummary,
                          hiringUpdates = intelReport.hiringUpdates,
                          interviewAngle = intelReport.interviewAngles.firstOrNull(),
                          sources = intelReport.groundedSources,
                          timestamp = intelReport.timestamp
                        )
                      }
                      .padding(horizontal = 8.dp, vertical = 4.dp)
                      .testTag("share_business_news_signals_btn"),
                    contentAlignment = Alignment.Center
                  ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share News via ShareSheet",
                        tint = TitanGold,
                        modifier = Modifier.size(12.dp)
                      )
                      Spacer(modifier = Modifier.width(4.dp))
                      Text(
                        text = "Share News Summary",
                        style = MaterialTheme.typography.labelSmall,
                        color = TitanGold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.5.sp
                      )
                    }
                  }
                }
              }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // SECTION 2: HIRING UPDATES & TALENT RADAR
            IntelSectionCard(
              title = "HIRING UPDATES & HEADCOUNT RADAR",
              icon = Icons.Default.People,
              iconTint = TitanEmerald
            ) {
              Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                intelReport.hiringUpdates.forEach { item ->
                  Row(verticalAlignment = Alignment.Top) {
                    Icon(
                      imageVector = Icons.Default.CheckCircle,
                      contentDescription = null,
                      tint = TitanEmerald,
                      modifier = Modifier
                        .size(14.dp)
                        .padding(top = 2.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                      text = item,
                      style = MaterialTheme.typography.bodySmall,
                      color = TextPrimaryDark,
                      fontSize = 11.sp
                    )
                  }
                }
              }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // SECTION 3: EXECUTIVE & LEADERSHIP SHIFTS
            IntelSectionCard(
              title = "EXECUTIVE & LEADERSHIP SHIFTS",
              icon = Icons.Default.Business,
              iconTint = TitanCyan
            ) {
              Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                intelReport.executiveShifts.forEach { shift ->
                  Row(verticalAlignment = Alignment.Top) {
                    Text("•", color = TitanCyan, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                      text = shift,
                      style = MaterialTheme.typography.bodySmall,
                      color = TextSecondaryDark,
                      fontSize = 11.sp
                    )
                  }
                }
              }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // SECTION 4: CANDIDATE INTERVIEW & OUTREACH ANGLES
            IntelSectionCard(
              title = "ACTIONABLE INTERVIEW & OUTREACH ANGLES",
              icon = Icons.Default.Lightbulb,
              iconTint = TitanGold
            ) {
              Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                intelReport.interviewAngles.forEachIndexed { index, angle ->
                  Box(
                    modifier = Modifier
                      .fillMaxWidth()
                      .clip(RoundedCornerShape(6.dp))
                      .background(TitanGold.copy(alpha = 0.08f))
                      .border(1.dp, TitanGold.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                      .padding(8.dp)
                  ) {
                    Row(verticalAlignment = Alignment.Top) {
                      Text(
                        text = "Pitch Angle ${index + 1}: ",
                        style = MaterialTheme.typography.labelSmall,
                        color = TitanGold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                      )
                      Text(
                        text = angle,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextPrimaryDark,
                        fontSize = 11.sp
                      )
                    }
                  }
                }
              }
            }

            // SECTION 4B: COMPETITIVE MOATS & OUTREACH HOOK (FROM GEMINI SERVICE WRAPPER)
            companyResearchState.result?.let { res ->
              if (res.competitiveMoats.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                IntelSectionCard(
                  title = "COMPETITIVE MOATS & DEFICITS",
                  icon = Icons.Default.CheckCircle,
                  iconTint = TitanEmerald
                ) {
                  Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    res.competitiveMoats.forEach { moat ->
                      Row(verticalAlignment = Alignment.Top) {
                        Text("🛡️ ", fontSize = 11.sp)
                        Text(
                          text = moat,
                          style = MaterialTheme.typography.bodySmall,
                          color = TextSecondaryDark,
                          fontSize = 11.sp
                        )
                      }
                    }
                  }
                }
              }

              if (res.recommendedOutreachPitch.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                IntelSectionCard(
                  title = "RECOMMENDED OUTREACH PITCH HOOK",
                  icon = Icons.Default.AutoAwesome,
                  iconTint = TitanCyan
                ) {
                  Box(
                    modifier = Modifier
                      .fillMaxWidth()
                      .clip(RoundedCornerShape(6.dp))
                      .background(TitanCyan.copy(alpha = 0.08f))
                      .border(1.dp, TitanCyan.copy(alpha = 0.25f), RoundedCornerShape(6.dp))
                      .padding(10.dp)
                  ) {
                    Text(
                      text = res.recommendedOutreachPitch,
                      style = MaterialTheme.typography.bodySmall,
                      color = TextPrimaryDark,
                      fontSize = 11.sp
                    )
                  }
                }
              }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // SECTION 5: GOOGLE SEARCH GROUNDING CITATIONS
            Text(
              text = "SEARCH GROUNDING CITATIONS & QUERIES",
              style = MaterialTheme.typography.labelSmall,
              color = TitanCyan,
              fontWeight = FontWeight.Bold,
              fontSize = 9.sp
            )
            Spacer(modifier = Modifier.height(6.dp))

            // Search queries tags
            LazyRow(
              horizontalArrangement = Arrangement.spacedBy(6.dp),
              modifier = Modifier.fillMaxWidth()
            ) {
              items(intelReport.searchQueriesUsed) { query ->
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(SlateElevated)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                  Text(
                    text = "🔍 $query",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondaryDark,
                    fontSize = 9.sp
                  )
                }
              }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Source links
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
              intelReport.groundedSources.forEach { source ->
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(SlateElevated)
                    .clickable {
                      try {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(source.url))
                        context.startActivity(intent)
                      } catch (e: Exception) {
                        // ignore if no browser available
                      }
                    }
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Icon(
                      imageVector = Icons.Default.Language,
                      contentDescription = null,
                      tint = TitanCyan,
                      modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                      text = source.title,
                      style = MaterialTheme.typography.bodySmall,
                      color = TextPrimaryDark,
                      fontSize = 11.sp,
                      maxLines = 1
                    )
                  }
                  Icon(
                    imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                    contentDescription = "Open",
                    tint = TextMutedDark,
                    modifier = Modifier.size(12.dp)
                  )
                }
              }
            }
          }
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}

@Composable
fun IntelSectionCard(
  title: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  iconTint: Color,
  content: @Composable () -> Unit
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(8.dp))
      .background(SlateElevated)
      .border(1.dp, SlateBorder, RoundedCornerShape(8.dp))
      .padding(12.dp)
  ) {
    Column {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(14.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = title,
          style = MaterialTheme.typography.labelSmall,
          color = iconTint,
          fontWeight = FontWeight.Bold,
          fontSize = 10.sp
        )
      }
      Spacer(modifier = Modifier.height(8.dp))
      content()
    }
  }
}

@Composable
fun SaveCompanyDialog(
  onDismiss: () -> Unit,
  onSave: (name: String, industry: String, website: String, careersUrl: String, tier: String, priority: String, notes: String, fetchNews: Boolean) -> Unit
) {
  var name by remember { mutableStateOf("") }
  var industry by remember { mutableStateOf("") }
  var website by remember { mutableStateOf("") }
  var careersUrl by remember { mutableStateOf("") }
  var tier by remember { mutableStateOf("S") }
  var priority by remember { mutableStateOf("High") }
  var notes by remember { mutableStateOf("") }
  var fetchNewsImmediately by remember { mutableStateOf(true) }

  val tiers = listOf("S+", "S", "A", "B")
  val priorities = listOf("Urgent", "High", "Medium", "Watch")

  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = SlateCard,
    title = {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.Business, contentDescription = null, tint = TitanCyan, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text("Add Target Company", color = TextPrimaryDark, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
      }
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Text(
          "Save a high-priority recruitment target to your local vault and trigger Gemini AI news research.",
          style = MaterialTheme.typography.bodySmall,
          color = TextSecondaryDark,
          fontSize = 11.sp
        )

        // Name
        OutlinedTextField(
          value = name,
          onValueChange = { name = it },
          label = { Text("Company Name *") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth().testTag("dialog_company_name_input"),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = TitanCyan,
            unfocusedBorderColor = SlateBorder,
            focusedTextColor = TextPrimaryDark,
            unfocusedTextColor = TextPrimaryDark,
            focusedLabelColor = TitanCyan,
            unfocusedLabelColor = TextMutedDark,
            focusedContainerColor = SlateElevated,
            unfocusedContainerColor = SlateElevated
          ),
          shape = RoundedCornerShape(8.dp)
        )

        // Industry
        OutlinedTextField(
          value = industry,
          onValueChange = { industry = it },
          label = { Text("Industry / Domain (e.g. Fintech, AI)") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth().testTag("dialog_company_industry_input"),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = TitanCyan,
            unfocusedBorderColor = SlateBorder,
            focusedTextColor = TextPrimaryDark,
            unfocusedTextColor = TextPrimaryDark,
            focusedLabelColor = TitanCyan,
            unfocusedLabelColor = TextMutedDark,
            focusedContainerColor = SlateElevated,
            unfocusedContainerColor = SlateElevated
          ),
          shape = RoundedCornerShape(8.dp)
        )

        // Tier Selector
        Column {
          Text("Target Tier", style = MaterialTheme.typography.labelSmall, color = TitanGold, fontWeight = FontWeight.Bold, fontSize = 10.sp)
          Spacer(modifier = Modifier.height(4.dp))
          Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            tiers.forEach { t ->
              val isSelected = tier == t
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(6.dp))
                  .background(if (isSelected) TitanGold.copy(alpha = 0.2f) else SlateElevated)
                  .border(1.dp, if (isSelected) TitanGold else SlateBorder, RoundedCornerShape(6.dp))
                  .clickable { tier = t }
                  .padding(horizontal = 10.dp, vertical = 6.dp)
              ) {
                Text(
                  t,
                  color = if (isSelected) TitanGold else TextSecondaryDark,
                  fontSize = 10.sp,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
              }
            }
          }
        }

        // Priority Selector
        Column {
          Text("Strategic Priority", style = MaterialTheme.typography.labelSmall, color = TitanCyan, fontWeight = FontWeight.Bold, fontSize = 10.sp)
          Spacer(modifier = Modifier.height(4.dp))
          Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            priorities.forEach { p ->
              val isSelected = priority == p
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(6.dp))
                  .background(if (isSelected) TitanCyan.copy(alpha = 0.2f) else SlateElevated)
                  .border(1.dp, if (isSelected) TitanCyan else SlateBorder, RoundedCornerShape(6.dp))
                  .clickable { priority = p }
                  .padding(horizontal = 8.dp, vertical = 5.dp)
              ) {
                Text(
                  p,
                  color = if (isSelected) TitanCyan else TextSecondaryDark,
                  fontSize = 10.sp,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
              }
            }
          }
        }

        // Website
        OutlinedTextField(
          value = website,
          onValueChange = { website = it },
          label = { Text("Website (e.g. stripe.com)") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth().testTag("dialog_company_website_input"),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = TitanCyan,
            unfocusedBorderColor = SlateBorder,
            focusedTextColor = TextPrimaryDark,
            unfocusedTextColor = TextPrimaryDark,
            focusedLabelColor = TitanCyan,
            unfocusedLabelColor = TextMutedDark,
            focusedContainerColor = SlateElevated,
            unfocusedContainerColor = SlateElevated
          ),
          shape = RoundedCornerShape(8.dp)
        )

        // Careers URL
        OutlinedTextField(
          value = careersUrl,
          onValueChange = { careersUrl = it },
          label = { Text("Careers URL") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth().testTag("dialog_company_careers_input"),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = TitanCyan,
            unfocusedBorderColor = SlateBorder,
            focusedTextColor = TextPrimaryDark,
            unfocusedTextColor = TextPrimaryDark,
            focusedLabelColor = TitanCyan,
            unfocusedLabelColor = TextMutedDark,
            focusedContainerColor = SlateElevated,
            unfocusedContainerColor = SlateElevated
          ),
          shape = RoundedCornerShape(8.dp)
        )

        // Notes
        OutlinedTextField(
          value = notes,
          onValueChange = { notes = it },
          label = { Text("Candidate Strategy & Angles") },
          placeholder = { Text("Key focus areas, VP contacts, target engineering teams...") },
          modifier = Modifier.fillMaxWidth().height(80.dp).testTag("dialog_company_notes_input"),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = TitanCyan,
            unfocusedBorderColor = SlateBorder,
            focusedTextColor = TextPrimaryDark,
            unfocusedTextColor = TextPrimaryDark,
            focusedLabelColor = TitanCyan,
            unfocusedLabelColor = TextMutedDark,
            focusedContainerColor = SlateElevated,
            unfocusedContainerColor = SlateElevated
          ),
          shape = RoundedCornerShape(8.dp)
        )

        // Fetch News Toggle
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(SlateElevated)
            .padding(10.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = TitanGold, modifier = Modifier.size(13.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Fetch News with Gemini AI", style = MaterialTheme.typography.labelSmall, color = TitanGold, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            }
            Text("Immediately triggers an AI news summary for this company", style = MaterialTheme.typography.bodySmall, color = TextMutedDark, fontSize = 9.sp)
          }
          Switch(
            checked = fetchNewsImmediately,
            onCheckedChange = { fetchNewsImmediately = it },
            colors = SwitchDefaults.colors(
              checkedThumbColor = TitanGold,
              checkedTrackColor = TitanGold.copy(alpha = 0.3f),
              uncheckedThumbColor = TextMutedDark,
              uncheckedTrackColor = SlateCard
            ),
            modifier = Modifier.testTag("dialog_fetch_news_switch")
          )
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (name.isNotBlank()) {
            onSave(name.trim(), industry.trim(), website.trim(), careersUrl.trim(), tier, priority, notes.trim(), fetchNewsImmediately)
          }
        },
        enabled = name.isNotBlank(),
        colors = ButtonDefaults.buttonColors(containerColor = TitanCyan, contentColor = ObsidianDark),
        shape = RoundedCornerShape(6.dp),
        modifier = Modifier.testTag("dialog_save_confirm_btn")
      ) {
        Text("Save Target", fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      Button(
        onClick = onDismiss,
        colors = ButtonDefaults.buttonColors(containerColor = SlateElevated, contentColor = TextSecondaryDark),
        shape = RoundedCornerShape(6.dp),
        modifier = Modifier.testTag("dialog_cancel_btn")
      ) {
        Text("Cancel")
      }
    }
  )
}

