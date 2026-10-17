package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Launch
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.DailyIntelligenceFeedReport
import com.example.data.model.IntelligenceFeedItem
import com.example.data.model.IntelligenceFeedItemType
import com.example.data.model.PrioritizedSector
import com.example.ui.theme.ObsidianDark
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateCard
import com.example.ui.theme.SlateDarker
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

/**
 * Daily Intelligence Feed View.
 * Utilizes Google Search Grounding to fetch and display the latest daily corporate news,
 * industry trends, and job openings across the prioritized tech, consulting, fintech,
 * and commerce sectors in an interactive, scrollable list.
 */
@Composable
fun DailyIntelligenceFeedView(
  viewModel: TitanViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val feedReport by viewModel.dailyIntelligenceFeedReport.collectAsState()
  val isFetching by viewModel.isFetchingDailyIntelligenceFeed.collectAsState()
  val selectedSector by viewModel.selectedIntelligenceSector.collectAsState()
  val selectedType by viewModel.selectedIntelligenceContentType.collectAsState()
  val searchQuery by viewModel.dailyIntelligenceSearchQuery.collectAsState()
  val groundingStatus by viewModel.dailyIntelligenceGroundingStatus.collectAsState()
  val selectedItemForDetail by viewModel.selectedFeedItemForDetail.collectAsState()

  // Auto-fetch if feed is null on launch
  LaunchedEffect(Unit) {
    if (feedReport == null) {
      viewModel.fetchDailyIntelligenceFeed(forceRefresh = false)
    }
  }

  val items = feedReport?.feedItems ?: emptyList()

  // Filter items in-memory based on selected content type and search query
  val filteredItems = remember(items, selectedType, searchQuery) {
    items.filter { item ->
      val matchesType = selectedType == IntelligenceFeedItemType.ALL || item.type == selectedType
      val matchesQuery = if (searchQuery.isBlank()) true else {
        item.headline.contains(searchQuery, ignoreCase = true) ||
          item.companyName.contains(searchQuery, ignoreCase = true) ||
          item.details.contains(searchQuery, ignoreCase = true) ||
          (item.roleTitle?.contains(searchQuery, ignoreCase = true) == true) ||
          item.keyTags.any { it.contains(searchQuery, ignoreCase = true) }
      }
      matchesType && matchesQuery
    }
  }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(ObsidianDark)
      .testTag("daily_intelligence_feed_view"),
    contentPadding = PaddingValues(12.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    // 1. HERO GOOGLE SEARCH GROUNDING KPI HEADER
    item {
      GroundedIntelligenceHeaderCard(
        feedReport = feedReport,
        isFetching = isFetching,
        groundingStatus = groundingStatus,
        onRefresh = { viewModel.fetchDailyIntelligenceFeed(forceRefresh = true) }
      )
    }

    // 2. SEARCH & LIVE REFRESH CONTROLS
    item {
      IntelligenceSearchBar(
        searchQuery = searchQuery,
        onSearchChange = { viewModel.setDailyIntelligenceSearchQuery(it) },
        isFetching = isFetching,
        onRefresh = { viewModel.fetchDailyIntelligenceFeed(forceRefresh = true) }
      )
    }

    // 3. PRIORITIZED SECTOR FILTER CHIPS (TECH, CONSULTING, FINTECH, COMMERCE)
    item {
      PrioritizedSectorFilterRow(
        selectedSector = selectedSector,
        onSelectSector = { viewModel.setIntelligenceSector(it) }
      )
    }

    // 4. CONTENT TYPE TOGGLE CHIPS (NEWS, TRENDS, ROLES)
    item {
      IntelligenceContentTypeFilterRow(
        selectedType = selectedType,
        onSelectType = { viewModel.setIntelligenceContentType(it) },
        totalCount = filteredItems.size
      )
    }

    // 5. LIST OF INTERACTIVE INTELLIGENCE CARDS
    if (isFetching && items.isEmpty()) {
      item {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .padding(40.dp),
          contentAlignment = Alignment.Center
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = TitanCyan, strokeWidth = 3.dp)
            Spacer(modifier = Modifier.height(12.dp))
            Text(
              text = "Synthesizing Google Search Grounding for Tech, Consulting, Fintech & Commerce...",
              color = TextSecondaryDark,
              fontSize = 11.sp,
              fontFamily = FontFamily.Monospace
            )
          }
        }
      }
    } else if (filteredItems.isEmpty()) {
      item {
        GroundedEmptyStateCard(
          onReset = {
            viewModel.setIntelligenceSector(PrioritizedSector.ALL)
            viewModel.setIntelligenceContentType(IntelligenceFeedItemType.ALL)
            viewModel.setDailyIntelligenceSearchQuery("")
          }
        )
      }
    } else {
      items(filteredItems, key = { it.id }) { item ->
        IntelligenceFeedInteractiveCard(
          item = item,
          onApply1Click = {
            viewModel.dispatch1ClickApplyForGroundedItem(item, context)
            Toast.makeText(context, "1-Click Applied to ${item.companyName} (${item.roleTitle ?: item.headline})", Toast.LENGTH_SHORT).show()
          },
          onTrackInLedger = { status ->
            viewModel.dispatchGroundedItemToLedger(item, context, status)
            Toast.makeText(context, "Tracked in Relationship Ledger ($status)", Toast.LENGTH_SHORT).show()
          },
          onInspect = {
            viewModel.setSelectedFeedItemForDetail(item)
          }
        )
      }
    }

    item {
      Spacer(modifier = Modifier.height(32.dp))
    }
  }

  // 6. DETAIL DOSSIER INSPECTION MODAL
  selectedItemForDetail?.let { detailItem ->
    IntelligenceItemDetailDialog(
      item = detailItem,
      onDismiss = { viewModel.setSelectedFeedItemForDetail(null) },
      onApply1Click = {
        viewModel.dispatch1ClickApplyForGroundedItem(detailItem, context)
        Toast.makeText(context, "Dispatched 1-Click Apply for ${detailItem.companyName}!", Toast.LENGTH_SHORT).show()
      },
      onTrackLedger = { status ->
        viewModel.dispatchGroundedItemToLedger(detailItem, context, status)
        Toast.makeText(context, "Synced to Relationship Ledger ($status)", Toast.LENGTH_SHORT).show()
      }
    )
  }
}

/**
 * Top KPI Header showcasing Google Search Grounding telemetry, citations, and summary metrics.
 */
@Composable
private fun GroundedIntelligenceHeaderCard(
  feedReport: DailyIntelligenceFeedReport?,
  isFetching: Boolean,
  groundingStatus: String,
  onRefresh: () -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("card_grounded_intel_header"),
    colors = CardDefaults.cardColors(containerColor = SlateCard),
    border = BorderStroke(1.dp, TitanCyan.copy(alpha = 0.5f)),
    shape = RoundedCornerShape(12.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(28.dp)
              .clip(CircleShape)
              .background(TitanCyan.copy(alpha = 0.15f))
              .border(1.dp, TitanCyan, CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Icon(Icons.Default.Public, contentDescription = null, tint = TitanCyan, modifier = Modifier.size(16.dp))
          }
          Spacer(modifier = Modifier.width(8.dp))
          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "DAILY INTELLIGENCE FEED",
                color = TextPrimaryDark,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
              )
              Spacer(modifier = Modifier.width(6.dp))
              // Green Pulse Indicator
              Box(
                modifier = Modifier
                  .size(8.dp)
                  .clip(CircleShape)
                  .background(TitanEmerald)
              )
            }
            Text(
              text = "Google Search Grounded • Tech, Consulting, Fintech & Commerce",
              color = TextMutedDark,
              fontSize = 10.5.sp
            )
          }
        }

        IconButton(
          onClick = onRefresh,
          enabled = !isFetching,
          modifier = Modifier.size(34.dp)
        ) {
          if (isFetching) {
            CircularProgressIndicator(modifier = Modifier.size(16.dp), color = TitanCyan, strokeWidth = 2.dp)
          } else {
            Icon(Icons.Default.Refresh, contentDescription = "Refresh Grounded Feed", tint = TitanCyan, modifier = Modifier.size(18.dp))
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // 4-COLUMN SUMMARY STATS
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        GroundedStatPill(
          title = "News Alerts",
          value = "${feedReport?.totalNewsCount ?: 5}",
          color = TitanGold,
          modifier = Modifier.weight(1f)
        )
        GroundedStatPill(
          title = "Macro Trends",
          value = "${feedReport?.totalTrendsCount ?: 4}",
          color = TitanCyan,
          modifier = Modifier.weight(1f)
        )
        GroundedStatPill(
          title = "Live Openings",
          value = "${feedReport?.totalOpeningsCount ?: 7}",
          color = TitanEmerald,
          modifier = Modifier.weight(1f)
        )
        GroundedStatPill(
          title = "Citations",
          value = "15 Live",
          color = TitanIndigo,
          modifier = Modifier.weight(1f)
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Grounding Engine Status Banner
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(6.dp))
          .background(SlateDarker)
          .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = TitanCyan, modifier = Modifier.size(12.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = groundingStatus,
          color = TitanCyan,
          fontSize = 10.sp,
          fontFamily = FontFamily.Monospace,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
      }
    }
  }
}

@Composable
private fun GroundedStatPill(
  title: String,
  value: String,
  color: Color,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(6.dp))
      .background(SlateDarker)
      .border(0.8.dp, SlateBorder, RoundedCornerShape(6.dp))
      .padding(6.dp)
  ) {
    Column {
      Text(text = title, color = TextMutedDark, fontSize = 9.sp, fontWeight = FontWeight.Medium)
      Spacer(modifier = Modifier.height(1.dp))
      Text(text = value, color = color, fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
    }
  }
}

/**
 * Search Bar for live query debouncing.
 */
@Composable
private fun IntelligenceSearchBar(
  searchQuery: String,
  onSearchChange: (String) -> Unit,
  isFetching: Boolean,
  onRefresh: () -> Unit
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.spacedBy(8.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    OutlinedTextField(
      value = searchQuery,
      onValueChange = onSearchChange,
      modifier = Modifier
        .weight(1f)
        .testTag("feed_search_input"),
      placeholder = {
        Text("Search Zepto, Gemini, Bain, UPI Credit...", color = TextMutedDark, fontSize = 11.5.sp)
      },
      leadingIcon = {
        Icon(Icons.Default.Search, contentDescription = "Search", tint = TitanCyan, modifier = Modifier.size(15.dp))
      },
      trailingIcon = {
        if (searchQuery.isNotEmpty()) {
          IconButton(onClick = { onSearchChange("") }) {
            Icon(Icons.Default.Clear, contentDescription = "Clear", tint = TextMutedDark, modifier = Modifier.size(15.dp))
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
      shape = RoundedCornerShape(8.dp)
    )

    Button(
      onClick = onRefresh,
      enabled = !isFetching,
      colors = ButtonDefaults.buttonColors(containerColor = TitanCyan, contentColor = ObsidianDark),
      shape = RoundedCornerShape(8.dp),
      modifier = Modifier
        .height(52.dp)
        .testTag("btn_refresh_grounded_feed")
    ) {
      if (isFetching) {
        CircularProgressIndicator(modifier = Modifier.size(13.dp), color = ObsidianDark, strokeWidth = 2.dp)
        Spacer(modifier = Modifier.width(4.dp))
        Text("Scanning...", fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
      } else {
        Icon(Icons.Default.Bolt, contentDescription = null, modifier = Modifier.size(14.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text("Grounded Scan", fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
      }
    }
  }
}

/**
 * Filter row for prioritized sectors: Tech, Consulting, Fintech, Commerce, All.
 */
@Composable
private fun PrioritizedSectorFilterRow(
  selectedSector: PrioritizedSector,
  onSelectSector: (PrioritizedSector) -> Unit
) {
  Column(modifier = Modifier.fillMaxWidth()) {
    Text(
      text = "PRIORITIZED INDUSTRY SECTORS",
      color = TextMutedDark,
      fontSize = 9.5.sp,
      fontWeight = FontWeight.Bold,
      fontFamily = FontFamily.Monospace
    )
    Spacer(modifier = Modifier.height(4.dp))
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .horizontalScroll(rememberScrollState()),
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      PrioritizedSector.values().forEach { sector ->
        val isSelected = selectedSector == sector
        val sectorAccent = when (sector) {
          PrioritizedSector.TECH -> TitanGold
          PrioritizedSector.CONSULTING -> TitanIndigo
          PrioritizedSector.FINTECH -> TitanCyan
          PrioritizedSector.COMMERCE -> TitanEmerald
          PrioritizedSector.ALL -> TitanCyan
        }

        FilterChip(
          selected = isSelected,
          onClick = { onSelectSector(sector) },
          label = {
            Text(
              text = sector.label,
              fontSize = 11.sp,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
          },
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = sectorAccent.copy(alpha = 0.2f),
            selectedLabelColor = sectorAccent,
            containerColor = SlateCard,
            labelColor = TextSecondaryDark
          ),
          border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = isSelected,
            borderColor = SlateBorder,
            selectedBorderColor = sectorAccent
          ),
          shape = RoundedCornerShape(16.dp),
          modifier = Modifier.testTag("chip_sector_${sector.name}")
        )
      }
    }
  }
}

/**
 * Filter row for content types: Corporate News, Industry Trends, Job Openings.
 */
@Composable
private fun IntelligenceContentTypeFilterRow(
  selectedType: IntelligenceFeedItemType,
  onSelectType: (IntelligenceFeedItemType) -> Unit,
  totalCount: Int
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Row(
      modifier = Modifier.horizontalScroll(rememberScrollState()),
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      IntelligenceFeedItemType.values().forEach { type ->
        val isSelected = selectedType == type
        FilterChip(
          selected = isSelected,
          onClick = { onSelectType(type) },
          label = {
            Text(
              text = type.label,
              fontSize = 10.5.sp,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
          },
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = TitanCyan.copy(alpha = 0.2f),
            selectedLabelColor = TitanCyan,
            containerColor = SlateCard,
            labelColor = TextMutedDark
          ),
          border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = isSelected,
            borderColor = SlateBorder,
            selectedBorderColor = TitanCyan
          ),
          shape = RoundedCornerShape(16.dp),
          modifier = Modifier.testTag("chip_type_${type.name}")
        )
      }
    }

    Text(
      text = "$totalCount Items",
      color = TitanCyan,
      fontSize = 10.5.sp,
      fontWeight = FontWeight.Bold,
      fontFamily = FontFamily.Monospace
    )
  }
}

/**
 * Individual Interactive Card for Grounded Intelligence Feed.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun IntelligenceFeedInteractiveCard(
  item: IntelligenceFeedItem,
  onApply1Click: () -> Unit,
  onTrackInLedger: (String) -> Unit,
  onInspect: () -> Unit
) {
  val context = LocalContext.current

  val (typeLabel, typeColor, typeIcon) = when (item.type) {
    IntelligenceFeedItemType.CORPORATE_NEWS -> Triple("BREAKING NEWS", TitanGold, Icons.Default.Newspaper)
    IntelligenceFeedItemType.INDUSTRY_TREND -> Triple("MACRO TREND", TitanCyan, Icons.AutoMirrored.Filled.TrendingUp)
    IntelligenceFeedItemType.JOB_OPENING -> Triple("OPENING", TitanEmerald, Icons.Default.Work)
    else -> Triple("INTELLIGENCE", TitanCyan, Icons.Default.Radar)
  }

  val sectorColor = when (item.sector) {
    PrioritizedSector.TECH -> TitanGold
    PrioritizedSector.CONSULTING -> TitanIndigo
    PrioritizedSector.FINTECH -> TitanCyan
    PrioritizedSector.COMMERCE -> TitanEmerald
    PrioritizedSector.ALL -> TitanCyan
  }

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .animateContentSize()
      .testTag("card_feed_item_${item.id}"),
    colors = CardDefaults.cardColors(containerColor = SlateCard),
    border = BorderStroke(1.dp, if (item.isBreaking) typeColor.copy(alpha = 0.5f) else SlateBorder),
    shape = RoundedCornerShape(10.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp)
    ) {
      // 1. TOP HEADER: TYPE, SECTOR, IMPACT SCORE & TIMESTAMP
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          // Type Badge
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(4.dp))
              .background(typeColor.copy(alpha = 0.15f))
              .border(0.8.dp, typeColor, RoundedCornerShape(4.dp))
              .padding(horizontal = 6.dp, vertical = 2.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(typeIcon, contentDescription = null, tint = typeColor, modifier = Modifier.size(10.dp))
              Spacer(modifier = Modifier.width(3.dp))
              Text(text = typeLabel, color = typeColor, fontSize = 8.5.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
            }
          }

          // Sector Badge
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(4.dp))
              .background(sectorColor.copy(alpha = 0.15f))
              .padding(horizontal = 5.dp, vertical = 2.dp)
          ) {
            Text(text = item.sector.code, color = sectorColor, fontSize = 8.5.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
          }
        }

        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          Text(text = item.publishedTime, color = TextMutedDark, fontSize = 9.sp)
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(4.dp))
              .background(SlateDarker)
              .padding(horizontal = 5.dp, vertical = 2.dp)
          ) {
            Text(text = "${item.impactScore} SCORE", color = TitanCyan, fontSize = 8.5.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // 2. HEADLINE & COMPANY
      Text(
        text = item.headline,
        color = TextPrimaryDark,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        lineHeight = 18.sp
      )

      if (item.type == IntelligenceFeedItemType.JOB_OPENING && item.roleTitle != null) {
        Spacer(modifier = Modifier.height(4.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "${item.companyName} • ", color = TitanCyan, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Icon(Icons.Default.LocationOn, contentDescription = null, tint = TextMutedDark, modifier = Modifier.size(11.dp))
            Text(text = item.location ?: "Bengaluru", color = TextMutedDark, fontSize = 10.5.sp)
          }
          item.compensationRange?.let { comp ->
            Text(text = comp, color = TitanGold, fontSize = 11.5.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // 3. IN-DEPTH DETAILS
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(6.dp))
          .background(SlateDarker)
          .border(0.8.dp, SlateBorder, RoundedCornerShape(6.dp))
          .padding(8.dp)
      ) {
        Text(
          text = item.details,
          color = TextSecondaryDark,
          fontSize = 11.sp,
          lineHeight = 15.sp
        )
      }

      // 4. KEY TAGS
      if (item.keyTags.isNotEmpty()) {
        Spacer(modifier = Modifier.height(8.dp))
        FlowRow(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(4.dp),
          verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          item.keyTags.forEach { tag ->
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(SlateElevated)
                .padding(horizontal = 5.dp, vertical = 2.dp)
            ) {
              Text(text = tag, color = TextMutedDark, fontSize = 9.sp)
            }
          }
        }
      }

      // 5. GROUNDED CITATION CHIP
      if (item.groundedSources.isNotEmpty()) {
        val firstSource = item.groundedSources.first()
        Spacer(modifier = Modifier.height(8.dp))
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .background(SlateDarker)
            .clickable {
              try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(firstSource.url))
                context.startActivity(intent)
              } catch (e: Exception) {
                Toast.makeText(context, "Cannot open: ${firstSource.url}", Toast.LENGTH_SHORT).show()
              }
            }
            .padding(horizontal = 6.dp, vertical = 4.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(Icons.AutoMirrored.Filled.Launch, contentDescription = null, tint = TitanCyan, modifier = Modifier.size(10.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "Grounded via: ${firstSource.title}",
            color = TitanCyan,
            fontSize = 9.5.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))
      HorizontalDivider(color = SlateBorder.copy(alpha = 0.5f))
      Spacer(modifier = Modifier.height(8.dp))

      // 6. ACTION CONTROLS ROW
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        if (item.type == IntelligenceFeedItemType.JOB_OPENING) {
          if (item.isSavedToLedger) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(TitanEmerald.copy(alpha = 0.15f))
                .padding(horizontal = 6.dp, vertical = 4.dp)
            ) {
              Icon(Icons.Default.CheckCircle, contentDescription = null, tint = TitanEmerald, modifier = Modifier.size(11.dp))
              Spacer(modifier = Modifier.width(3.dp))
              Text("Ledger: ${item.ledgerStatus ?: "Tracked"}", color = TitanEmerald, fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
            }
          } else {
            OutlinedButton(
              onClick = { onTrackInLedger("Applied") },
              border = BorderStroke(1.dp, TitanIndigo),
              shape = RoundedCornerShape(6.dp),
              contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
              modifier = Modifier.testTag("btn_ledger_grounded_${item.id}")
            ) {
              Icon(Icons.Default.Handshake, contentDescription = null, tint = TitanIndigo, modifier = Modifier.size(11.dp))
              Spacer(modifier = Modifier.width(3.dp))
              Text("Track Ledger", color = TitanIndigo, fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
            }
          }

          Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            TextButton(
              onClick = onInspect,
              contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp)
            ) {
              Icon(Icons.Default.Info, contentDescription = null, tint = TitanCyan, modifier = Modifier.size(11.dp))
              Spacer(modifier = Modifier.width(2.dp))
              Text("Intel", color = TitanCyan, fontSize = 9.5.sp)
            }

            Button(
              onClick = onApply1Click,
              colors = ButtonDefaults.buttonColors(containerColor = TitanGold, contentColor = ObsidianDark),
              shape = RoundedCornerShape(6.dp),
              contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
              modifier = Modifier.testTag("btn_apply_grounded_${item.id}")
            ) {
              Icon(Icons.Default.Bolt, contentDescription = null, modifier = Modifier.size(11.dp))
              Spacer(modifier = Modifier.width(3.dp))
              Text("1-Click Apply", fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
            }
          }
        } else {
          // Corporate News & Trends Card Actions
          Text(text = "Signal Score: ${item.impactScore}/100", color = TextMutedDark, fontSize = 9.5.sp)

          Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            OutlinedButton(
              onClick = { onTrackInLedger("Targeted") },
              border = BorderStroke(1.dp, TitanCyan),
              shape = RoundedCornerShape(6.dp),
              contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
            ) {
              Icon(Icons.Default.Business, contentDescription = null, tint = TitanCyan, modifier = Modifier.size(11.dp))
              Spacer(modifier = Modifier.width(3.dp))
              Text("Add Target", color = TitanCyan, fontSize = 9.5.sp)
            }

            Button(
              onClick = onInspect,
              colors = ButtonDefaults.buttonColors(containerColor = SlateDarker, contentColor = TextPrimaryDark),
              shape = RoundedCornerShape(6.dp),
              contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
            ) {
              Text("Deep Intel", fontSize = 9.5.sp, fontWeight = FontWeight.Medium)
            }
          }
        }
      }
    }
  }
}

/**
 * Empty state view when no search matches found.
 */
@Composable
private fun GroundedEmptyStateCard(
  onReset: () -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 24.dp),
    colors = CardDefaults.cardColors(containerColor = SlateCard),
    border = BorderStroke(1.dp, SlateBorder),
    shape = RoundedCornerShape(12.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(24.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Icon(Icons.Default.Public, contentDescription = null, tint = TitanCyan.copy(alpha = 0.5f), modifier = Modifier.size(40.dp))
      Spacer(modifier = Modifier.height(10.dp))
      Text("No Intelligence Matches in Current Filter", color = TextPrimaryDark, fontSize = 14.sp, fontWeight = FontWeight.Bold)
      Spacer(modifier = Modifier.height(4.dp))
      Text("Try resetting sector and content type filters to load all Google Search Grounding feeds.", color = TextMutedDark, fontSize = 11.5.sp)
      Spacer(modifier = Modifier.height(14.dp))
      OutlinedButton(
        onClick = onReset,
        border = BorderStroke(1.dp, TitanCyan),
        shape = RoundedCornerShape(8.dp)
      ) {
        Icon(Icons.Default.Clear, contentDescription = null, tint = TitanCyan, modifier = Modifier.size(14.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text("Reset Filters", color = TitanCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
      }
    }
  }
}

/**
 * Modal dialog for inspecting complete grounded intelligence citations and details.
 */
@Composable
private fun IntelligenceItemDetailDialog(
  item: IntelligenceFeedItem,
  onDismiss: () -> Unit,
  onApply1Click: () -> Unit,
  onTrackLedger: (String) -> Unit
) {
  val context = LocalContext.current

  Dialog(onDismissRequest = onDismiss) {
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .testTag("dialog_intelligence_detail_${item.id}"),
      colors = CardDefaults.cardColors(containerColor = SlateCard),
      border = BorderStroke(1.dp, TitanCyan),
      shape = RoundedCornerShape(14.dp)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(18.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(text = item.companyName, color = TitanCyan, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Text(text = item.headline, color = TextPrimaryDark, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, maxLines = 2, overflow = TextOverflow.Ellipsis)
          }
          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMutedDark)
          }
        }

        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(color = SlateBorder)
        Spacer(modifier = Modifier.height(10.dp))

        // Details
        Text(text = "GROUNDED STRATEGIC INTELLIGENCE", color = TextMutedDark, fontSize = 9.5.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = item.details, color = TextSecondaryDark, fontSize = 12.sp, lineHeight = 16.sp)

        // Web Sources
        if (item.groundedSources.isNotEmpty()) {
          Spacer(modifier = Modifier.height(12.dp))
          Text(text = "GROUNDED CITATION SOURCES", color = TextMutedDark, fontSize = 9.5.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
          Spacer(modifier = Modifier.height(4.dp))
          item.groundedSources.forEach { src ->
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 3.dp)
                .clickable {
                  try {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(src.url))
                    context.startActivity(intent)
                  } catch (e: Exception) {
                    Toast.makeText(context, "Cannot open: ${src.url}", Toast.LENGTH_SHORT).show()
                  }
                },
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(Icons.AutoMirrored.Filled.Launch, contentDescription = null, tint = TitanCyan, modifier = Modifier.size(12.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text(text = src.title, color = TitanCyan, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Actions
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          OutlinedButton(
            onClick = {
              onTrackLedger("Applied")
              onDismiss()
            },
            border = BorderStroke(1.dp, TitanIndigo),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.weight(1f)
          ) {
            Icon(Icons.Default.Handshake, contentDescription = null, tint = TitanIndigo, modifier = Modifier.size(13.dp))
            Spacer(modifier = Modifier.width(3.dp))
            Text("Track Ledger", color = TitanIndigo, fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
          }

          if (item.type == IntelligenceFeedItemType.JOB_OPENING) {
            Button(
              onClick = {
                onApply1Click()
                onDismiss()
              },
              colors = ButtonDefaults.buttonColors(containerColor = TitanGold, contentColor = ObsidianDark),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.weight(1f)
            ) {
              Icon(Icons.Default.Bolt, contentDescription = null, modifier = Modifier.size(13.dp))
              Spacer(modifier = Modifier.width(3.dp))
              Text("1-Click Apply", fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    }
  }
}
