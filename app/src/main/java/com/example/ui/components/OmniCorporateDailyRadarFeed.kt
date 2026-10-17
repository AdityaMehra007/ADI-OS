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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.automirrored.filled.Launch
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Storage
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
import androidx.compose.material3.Surface
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.CorporateDailyUpdateItem
import com.example.data.model.CorporateDepartment
import com.example.data.model.CorporateSector
import com.example.data.model.RoleFreshness
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
 * Omni-Corporate Daily Radar & Intelligence Feed Component.
 * Real-time daily job hunter and corporate pulse tracking startups, MNCs, consulting firms,
 * and fintech leaders across all corporate roles and departments.
 */
@Composable
fun OmniCorporateDailyRadarFeed(
  viewModel: TitanViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val radarItems by viewModel.dailyRadarItems.collectAsState()
  val selectedSector by viewModel.selectedRadarSector.collectAsState()
  val selectedDepartment by viewModel.selectedRadarDepartment.collectAsState()
  val selectedFreshness by viewModel.selectedRadarFreshness.collectAsState()
  val searchQuery by viewModel.radarSearchQuery.collectAsState()
  val isRefreshing by viewModel.isRefreshingRadar.collectAsState()
  val statusMessage by viewModel.radarStatusMessage.collectAsState()
  val selectedItemForModal by viewModel.selectedRadarItemForModal.collectAsState()

  // Filtered in-memory list
  val filteredItems = remember(radarItems, selectedSector, selectedDepartment, selectedFreshness, searchQuery) {
    radarItems.filter { item ->
      val matchesSector = selectedSector == CorporateSector.ALL || item.sector == selectedSector
      val matchesDept = selectedDepartment == CorporateDepartment.ALL || item.department == selectedDepartment
      val matchesFreshness = when (selectedFreshness) {
        RoleFreshness.ALL -> true
        RoleFreshness.TODAY -> item.isNewToday
        RoleFreshness.PAST_48_HOURS -> item.isNewToday || item.postedDate.contains("Yesterday")
        RoleFreshness.THIS_WEEK -> true
      }
      val matchesQuery = if (searchQuery.isBlank()) true else {
        item.companyName.contains(searchQuery, ignoreCase = true) ||
          item.roleTitle.contains(searchQuery, ignoreCase = true) ||
          item.location.contains(searchQuery, ignoreCase = true) ||
          item.headlineUpdate.contains(searchQuery, ignoreCase = true) ||
          item.keyRequirements.any { it.contains(searchQuery, ignoreCase = true) }
      }
      matchesSector && matchesDept && matchesFreshness && matchesQuery
    }
  }

  // Aggregate KPI metrics
  val freshTodayCount = remember(radarItems) { radarItems.count { it.isNewToday } }
  val immediateUrgencyCount = remember(radarItems) { radarItems.count { it.hiringUrgency == "IMMEDIATE" } }
  val distinctCompaniesCount = remember(radarItems) { radarItems.map { it.companyName }.distinct().size }
  val avgCtcLakhs = remember(radarItems) {
    if (radarItems.isNotEmpty()) radarItems.map { it.ctcLakhsEstimate }.average().toInt() else 42
  }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(ObsidianDark)
      .testTag("omni_corporate_daily_radar_feed"),
    contentPadding = PaddingValues(12.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    // 1. HERO RADAR INTELLIGENCE BRIEFING BANNER
    item {
      RadarHeroKpiBanner(
        freshTodayCount = freshTodayCount,
        avgCtcLakhs = avgCtcLakhs,
        distinctCompaniesCount = distinctCompaniesCount,
        immediateUrgencyCount = immediateUrgencyCount,
        isRefreshing = isRefreshing,
        statusMessage = statusMessage,
        onRefresh = { viewModel.refreshDailyCorporateRadar() }
      )
    }

    // 2. SEARCH & ACTION CONTROLS BAR
    item {
      RadarSearchAndActionBar(
        searchQuery = searchQuery,
        onSearchChange = { viewModel.setRadarSearchQuery(it) },
        isRefreshing = isRefreshing,
        onScanOpenings = { viewModel.refreshDailyCorporateRadar() }
      )
    }

    // 3. SECTOR FILTER PILLS (HORIZONTAL SCROLL)
    item {
      RadarSectorFilterRow(
        selectedSector = selectedSector,
        onSelectSector = { viewModel.setRadarSector(it) }
      )
    }

    // 4. DEPARTMENT FILTER PILLS (HORIZONTAL SCROLL)
    item {
      RadarDepartmentFilterRow(
        selectedDepartment = selectedDepartment,
        onSelectDepartment = { viewModel.setRadarDepartment(it) }
      )
    }

    // 5. FRESHNESS FILTER ROW & RESULTS HEADER
    item {
      RadarFreshnessAndCountHeader(
        selectedFreshness = selectedFreshness,
        onSelectFreshness = { viewModel.setRadarFreshness(it) },
        totalFilteredCount = filteredItems.size,
        onResetFilters = {
          viewModel.setRadarSector(CorporateSector.ALL)
          viewModel.setRadarDepartment(CorporateDepartment.ALL)
          viewModel.setRadarFreshness(RoleFreshness.ALL)
          viewModel.setRadarSearchQuery("")
        }
      )
    }

    // 6. CORPORATE DAILY ROLE CARDS
    if (filteredItems.isEmpty()) {
      item {
        RadarEmptyStateCard(
          onReset = {
            viewModel.setRadarSector(CorporateSector.ALL)
            viewModel.setRadarDepartment(CorporateDepartment.ALL)
            viewModel.setRadarFreshness(RoleFreshness.ALL)
            viewModel.setRadarSearchQuery("")
          }
        )
      }
    } else {
      items(filteredItems, key = { it.id }) { item ->
        CorporateDailyUpdateCard(
          item = item,
          onApply1Click = {
            viewModel.dispatch1ClickApplyForRadarRole(item, context)
            Toast.makeText(context, "1-Click Applied to ${item.companyName} (${item.roleTitle})", Toast.LENGTH_SHORT).show()
          },
          onTrackInLedger = { status ->
            viewModel.dispatchDailyRoleToRelationshipLedger(item, context, status)
            Toast.makeText(context, "Saved to Relationship Ledger as $status", Toast.LENGTH_SHORT).show()
          },
          onOpenDetail = { viewModel.setSelectedRadarItemForModal(item) }
        )
      }
    }

    item {
      Spacer(modifier = Modifier.height(24.dp))
    }
  }

  // 7. DETAIL INSPECTION MODAL DIALOG
  selectedItemForModal?.let { modalItem ->
    CorporateRoleDetailDossierDialog(
      item = modalItem,
      onDismiss = { viewModel.setSelectedRadarItemForModal(null) },
      onApply1Click = {
        viewModel.dispatch1ClickApplyForRadarRole(modalItem, context)
        Toast.makeText(context, "1-Click Dispatched for ${modalItem.companyName}!", Toast.LENGTH_SHORT).show()
      },
      onTrackLedger = { status ->
        viewModel.dispatchDailyRoleToRelationshipLedger(modalItem, context, status)
        Toast.makeText(context, "Tracked in Relationship Ledger ($status)", Toast.LENGTH_SHORT).show()
      }
    )
  }
}

/**
 * Top KPI Banner displaying daily radar discovery status and summary statistics.
 */
@Composable
private fun RadarHeroKpiBanner(
  freshTodayCount: Int,
  avgCtcLakhs: Int,
  distinctCompaniesCount: Int,
  immediateUrgencyCount: Int,
  isRefreshing: Boolean,
  statusMessage: String?,
  onRefresh: () -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("radar_hero_kpi_banner"),
    colors = CardDefaults.cardColors(containerColor = SlateCard),
    border = BorderStroke(1.dp, TitanCyan.copy(alpha = 0.4f)),
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
            Icon(
              imageVector = Icons.Default.Radar,
              contentDescription = "Radar",
              tint = TitanCyan,
              modifier = Modifier.size(16.dp)
            )
          }
          Spacer(modifier = Modifier.width(8.dp))
          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "OMNI-CORPORATE DAILY RADAR",
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
              text = "Daily Corporate Intelligence across Startups & Global MNCs",
              color = TextMutedDark,
              fontSize = 11.sp
            )
          }
        }

        IconButton(
          onClick = onRefresh,
          enabled = !isRefreshing,
          modifier = Modifier.size(36.dp)
        ) {
          if (isRefreshing) {
            CircularProgressIndicator(
              modifier = Modifier.size(18.dp),
              color = TitanCyan,
              strokeWidth = 2.dp
            )
          } else {
            Icon(
              imageVector = Icons.Default.Refresh,
              contentDescription = "Refresh Daily Radar",
              tint = TitanCyan,
              modifier = Modifier.size(20.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // 4-COLUMN KPI METRIC CARDS
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        KpiStatMiniCard(
          title = "Fresh Today",
          value = "$freshTodayCount Roles",
          accentColor = TitanEmerald,
          subtitle = "🔥 Just discovered",
          modifier = Modifier.weight(1f)
        )
        KpiStatMiniCard(
          title = "Avg CTC",
          value = "₹${avgCtcLakhs}L CTC",
          accentColor = TitanGold,
          subtitle = "Compensation bench",
          modifier = Modifier.weight(1f)
        )
        KpiStatMiniCard(
          title = "Entities",
          value = "$distinctCompaniesCount Orgs",
          accentColor = TitanCyan,
          subtitle = "MNCs & Startups",
          modifier = Modifier.weight(1f)
        )
        KpiStatMiniCard(
          title = "Urgent Waves",
          value = "$immediateUrgencyCount Openings",
          accentColor = TitanCrimson,
          subtitle = "⚡ Immediate hire",
          modifier = Modifier.weight(1f)
        )
      }

      statusMessage?.let { msg ->
        Spacer(modifier = Modifier.height(10.dp))
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(SlateDarker)
            .padding(horizontal = 10.dp, vertical = 6.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.AutoAwesome,
            contentDescription = null,
            tint = TitanGold,
            modifier = Modifier.size(13.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = msg,
            color = TextSecondaryDark,
            fontSize = 10.5.sp,
            fontFamily = FontFamily.Monospace,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        }
      }
    }
  }
}

@Composable
private fun KpiStatMiniCard(
  title: String,
  value: String,
  accentColor: Color,
  subtitle: String,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(8.dp))
      .background(SlateDarker)
      .border(1.dp, SlateBorder, RoundedCornerShape(8.dp))
      .padding(8.dp)
  ) {
    Column {
      Text(
        text = title,
        color = TextMutedDark,
        fontSize = 9.5.sp,
        fontWeight = FontWeight.Medium
      )
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = value,
        color = accentColor,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = FontFamily.Monospace
      )
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = subtitle,
        color = TextMutedDark,
        fontSize = 8.5.sp,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )
    }
  }
}

/**
 * Search input field and manual "Scan Daily Openings" trigger button.
 */
@Composable
private fun RadarSearchAndActionBar(
  searchQuery: String,
  onSearchChange: (String) -> Unit,
  isRefreshing: Boolean,
  onScanOpenings: () -> Unit
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
        .testTag("radar_search_input"),
      placeholder = {
        Text("Search Zepto, Google, Staff AI, Ops...", color = TextMutedDark, fontSize = 12.sp)
      },
      leadingIcon = {
        Icon(Icons.Default.Search, contentDescription = "Search", tint = TitanCyan, modifier = Modifier.size(16.dp))
      },
      trailingIcon = {
        if (searchQuery.isNotEmpty()) {
          IconButton(onClick = { onSearchChange("") }) {
            Icon(Icons.Default.Clear, contentDescription = "Clear", tint = TextMutedDark, modifier = Modifier.size(16.dp))
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
      onClick = onScanOpenings,
      enabled = !isRefreshing,
      colors = ButtonDefaults.buttonColors(containerColor = TitanCyan, contentColor = ObsidianDark),
      shape = RoundedCornerShape(8.dp),
      modifier = Modifier
        .height(52.dp)
        .testTag("btn_scan_daily_radar")
    ) {
      if (isRefreshing) {
        CircularProgressIndicator(modifier = Modifier.size(14.dp), color = ObsidianDark, strokeWidth = 2.dp)
        Spacer(modifier = Modifier.width(6.dp))
        Text("Scanning...", fontSize = 11.sp, fontWeight = FontWeight.Bold)
      } else {
        Icon(Icons.Default.Bolt, contentDescription = null, modifier = Modifier.size(15.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text("Scan Radar", fontSize = 11.sp, fontWeight = FontWeight.Bold)
      }
    }
  }
}

/**
 * Sector filter chips across Tech MNCs, Startups, Unicorns, Consulting, Fintech, Commerce.
 */
@Composable
private fun RadarSectorFilterRow(
  selectedSector: CorporateSector,
  onSelectSector: (CorporateSector) -> Unit
) {
  Column(modifier = Modifier.fillMaxWidth()) {
    Text(
      text = "TARGET SECTORS & ORGANIZATIONAL TIERS",
      color = TextMutedDark,
      fontSize = 10.sp,
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
      CorporateSector.values().forEach { sector ->
        val isSelected = selectedSector == sector
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
          shape = RoundedCornerShape(16.dp),
          modifier = Modifier.testTag("chip_sector_${sector.name}")
        )
      }
    }
  }
}

/**
 * Department filter chips across Strategy, Engineering, Product, Finance, Talent.
 */
@Composable
private fun RadarDepartmentFilterRow(
  selectedDepartment: CorporateDepartment,
  onSelectDepartment: (CorporateDepartment) -> Unit
) {
  Column(modifier = Modifier.fillMaxWidth()) {
    Text(
      text = "FUNCTIONAL DEPARTMENTS & ROLES",
      color = TextMutedDark,
      fontSize = 10.sp,
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
      CorporateDepartment.values().forEach { dept ->
        val isSelected = selectedDepartment == dept
        FilterChip(
          selected = isSelected,
          onClick = { onSelectDepartment(dept) },
          label = {
            Text(
              text = dept.label,
              fontSize = 11.sp,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
          },
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = TitanGold.copy(alpha = 0.2f),
            selectedLabelColor = TitanGold,
            containerColor = SlateCard,
            labelColor = TextSecondaryDark
          ),
          border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = isSelected,
            borderColor = SlateBorder,
            selectedBorderColor = TitanGold
          ),
          shape = RoundedCornerShape(16.dp),
          modifier = Modifier.testTag("chip_dept_${dept.name}")
        )
      }
    }
  }
}

/**
 * Freshness filters row and active count banner.
 */
@Composable
private fun RadarFreshnessAndCountHeader(
  selectedFreshness: RoleFreshness,
  onSelectFreshness: (RoleFreshness) -> Unit,
  totalFilteredCount: Int,
  onResetFilters: () -> Unit
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
      RoleFreshness.values().forEach { freshness ->
        val isSelected = selectedFreshness == freshness
        FilterChip(
          selected = isSelected,
          onClick = { onSelectFreshness(freshness) },
          label = {
            Text(
              text = freshness.label,
              fontSize = 10.5.sp,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
          },
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = TitanEmerald.copy(alpha = 0.2f),
            selectedLabelColor = TitanEmerald,
            containerColor = SlateCard,
            labelColor = TextMutedDark
          ),
          border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = isSelected,
            borderColor = SlateBorder,
            selectedBorderColor = TitanEmerald
          ),
          shape = RoundedCornerShape(16.dp)
        )
      }
    }

    Text(
      text = "$totalFilteredCount Active Roles",
      color = TitanCyan,
      fontSize = 11.sp,
      fontWeight = FontWeight.Bold,
      fontFamily = FontFamily.Monospace
    )
  }
}

/**
 * Individual High-Density Corporate Role & Daily Update Card.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CorporateDailyUpdateCard(
  item: CorporateDailyUpdateItem,
  onApply1Click: () -> Unit,
  onTrackInLedger: (String) -> Unit,
  onOpenDetail: () -> Unit
) {
  val sectorColor = when (item.sector) {
    CorporateSector.TECH_MNC -> TitanGold
    CorporateSector.STARTUP -> TitanCyan
    CorporateSector.UNICORN -> TitanEmerald
    CorporateSector.CONSULTING -> TitanIndigo
    CorporateSector.FINTECH -> Color(0xFF00E5FF)
    CorporateSector.COMMERCE -> Color(0xFFFF9100)
    else -> TitanCyan
  }

  val urgencyColor = when (item.hiringUrgency) {
    "IMMEDIATE" -> TitanCrimson
    "HIGH_PRIORITY" -> TitanGold
    else -> TitanCyan
  }

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .animateContentSize()
      .testTag("card_daily_radar_${item.id}"),
    colors = CardDefaults.cardColors(containerColor = SlateCard),
    border = BorderStroke(1.dp, if (item.isNewToday) TitanCyan.copy(alpha = 0.5f) else SlateBorder),
    shape = RoundedCornerShape(10.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp)
    ) {
      // 1. TOP HEADER: COMPANY, SECTOR BADGE, FRESHNESS & URGENCY
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = item.companyName,
            color = TextPrimaryDark,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
          )
          Spacer(modifier = Modifier.width(6.dp))
          // Sector Badge
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(4.dp))
              .background(sectorColor.copy(alpha = 0.15f))
              .border(0.8.dp, sectorColor, RoundedCornerShape(4.dp))
              .padding(horizontal = 6.dp, vertical = 2.dp)
          ) {
            Text(
              text = item.sector.badgeTitle,
              color = sectorColor,
              fontSize = 9.sp,
              fontWeight = FontWeight.Bold,
              fontFamily = FontFamily.Monospace
            )
          }
        }

        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
          if (item.isNewToday) {
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(TitanEmerald.copy(alpha = 0.15f))
                .border(0.8.dp, TitanEmerald, RoundedCornerShape(4.dp))
                .padding(horizontal = 5.dp, vertical = 2.dp)
            ) {
              Text(
                text = "TODAY 🔥",
                color = TitanEmerald,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }

          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(4.dp))
              .background(urgencyColor.copy(alpha = 0.15f))
              .border(0.8.dp, urgencyColor, RoundedCornerShape(4.dp))
              .padding(horizontal = 5.dp, vertical = 2.dp)
          ) {
            Text(
              text = if (item.hiringUrgency == "IMMEDIATE") "IMMEDIATE ⚡" else "PRIORITY",
              color = urgencyColor,
              fontSize = 9.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(6.dp))

      // 2. ROLE TITLE & COMPENSATION
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = item.roleTitle,
          color = TitanCyan,
          fontSize = 14.sp,
          fontWeight = FontWeight.SemiBold,
          modifier = Modifier.weight(1f)
        )
        Text(
          text = item.compensationRange,
          color = TitanGold,
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold,
          fontFamily = FontFamily.Monospace
        )
      }

      Spacer(modifier = Modifier.height(6.dp))

      // 3. LOCATION & DEPARTMENT CHIPS
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.LocationOn, contentDescription = null, tint = TextMutedDark, modifier = Modifier.size(12.dp))
          Spacer(modifier = Modifier.width(3.dp))
          Text(text = item.location, color = TextMutedDark, fontSize = 10.5.sp)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Work, contentDescription = null, tint = TextMutedDark, modifier = Modifier.size(12.dp))
          Spacer(modifier = Modifier.width(3.dp))
          Text(text = item.department.code, color = TextMutedDark, fontSize = 10.5.sp, fontFamily = FontFamily.Monospace)
        }
        Text(text = "• ${item.experienceLevel}", color = TextMutedDark, fontSize = 10.5.sp)
      }

      Spacer(modifier = Modifier.height(8.dp))

      // 4. CORPORATE HEADLINE / BREAKING SIGNAL
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(6.dp))
          .background(SlateDarker)
          .border(0.8.dp, SlateBorder, RoundedCornerShape(6.dp))
          .padding(8.dp)
      ) {
        Row(verticalAlignment = Alignment.Top) {
          Icon(
            imageVector = Icons.Default.Newspaper,
            contentDescription = null,
            tint = TitanGold,
            modifier = Modifier
              .size(14.dp)
              .padding(top = 2.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = item.headlineUpdate,
            color = TextSecondaryDark,
            fontSize = 11.sp,
            lineHeight = 15.sp
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // 5. KEY REQUIREMENTS TAGS
      if (item.keyRequirements.isNotEmpty()) {
        FlowRow(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(4.dp),
          verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          item.keyRequirements.take(3).forEach { req ->
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(SlateElevated)
                .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
              Text(
                text = req,
                color = TextSecondaryDark,
                fontSize = 9.5.sp
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))
      HorizontalDivider(color = SlateBorder.copy(alpha = 0.5f))
      Spacer(modifier = Modifier.height(8.dp))

      // 6. ACTION HUB (1-CLICK APPLY, TRACK IN LEDGER, DOSSIER)
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Track in Relationship Ledger status / button
        if (item.isSavedToLedger) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(TitanEmerald.copy(alpha = 0.15f))
              .padding(horizontal = 8.dp, vertical = 6.dp)
          ) {
            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = TitanEmerald, modifier = Modifier.size(13.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "Ledger: ${item.ledgerStatus ?: "Tracked"}",
              color = TitanEmerald,
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold
            )
          }
        } else {
          OutlinedButton(
            onClick = { onTrackInLedger("Applied") },
            border = BorderStroke(1.dp, TitanIndigo),
            shape = RoundedCornerShape(6.dp),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
            modifier = Modifier.testTag("btn_track_ledger_${item.id}")
          ) {
            Icon(Icons.Default.Handshake, contentDescription = null, tint = TitanIndigo, modifier = Modifier.size(13.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Track Ledger", color = TitanIndigo, fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
          }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          TextButton(
            onClick = onOpenDetail,
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
            modifier = Modifier.testTag("btn_intel_${item.id}")
          ) {
            Icon(Icons.Default.Info, contentDescription = null, tint = TitanCyan, modifier = Modifier.size(13.dp))
            Spacer(modifier = Modifier.width(3.dp))
            Text("Intel", color = TitanCyan, fontSize = 10.5.sp)
          }

          Button(
            onClick = onApply1Click,
            colors = ButtonDefaults.buttonColors(containerColor = TitanGold, contentColor = ObsidianDark),
            shape = RoundedCornerShape(6.dp),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
            modifier = Modifier.testTag("btn_apply_${item.id}")
          ) {
            Icon(Icons.Default.Bolt, contentDescription = null, modifier = Modifier.size(13.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("1-Click Apply", fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}

/**
 * Empty state view when search or filter combinations yield zero results.
 */
@Composable
private fun RadarEmptyStateCard(
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
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      Icon(
        imageVector = Icons.Default.Radar,
        contentDescription = null,
        tint = TitanCyan.copy(alpha = 0.5f),
        modifier = Modifier.size(44.dp)
      )
      Spacer(modifier = Modifier.height(10.dp))
      Text(
        text = "No Matching Daily Roles in Current Filter",
        color = TextPrimaryDark,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = "Try clearing the department or sector filters to view all 15+ live corporate opportunities.",
        color = TextMutedDark,
        fontSize = 11.5.sp,
        lineHeight = 16.sp
      )
      Spacer(modifier = Modifier.height(14.dp))
      OutlinedButton(
        onClick = onReset,
        border = BorderStroke(1.dp, TitanCyan),
        shape = RoundedCornerShape(8.dp)
      ) {
        Icon(Icons.Default.Clear, contentDescription = null, tint = TitanCyan, modifier = Modifier.size(14.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text("Reset All Filters", color = TitanCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
      }
    }
  }
}

/**
 * Modal Dialog for in-depth corporate role inspection, requirements check, and direct apply.
 */
@Composable
private fun CorporateRoleDetailDossierDialog(
  item: CorporateDailyUpdateItem,
  onDismiss: () -> Unit,
  onApply1Click: () -> Unit,
  onTrackLedger: (String) -> Unit
) {
  val context = LocalContext.current

  Dialog(onDismissRequest = onDismiss) {
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .testTag("dialog_role_dossier_${item.id}"),
      colors = CardDefaults.cardColors(containerColor = SlateCard),
      border = BorderStroke(1.dp, TitanCyan),
      shape = RoundedCornerShape(14.dp)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(18.dp)
      ) {
        // Modal Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = item.companyName,
              color = TitanCyan,
              fontSize = 18.sp,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = item.roleTitle,
              color = TextPrimaryDark,
              fontSize = 13.sp,
              fontWeight = FontWeight.Medium
            )
          }
          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMutedDark)
          }
        }

        Spacer(modifier = Modifier.height(10.dp))
        HorizontalDivider(color = SlateBorder)
        Spacer(modifier = Modifier.height(10.dp))

        // Specs Grid
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Column {
            Text("Compensation CTC", color = TextMutedDark, fontSize = 10.sp)
            Text(item.compensationRange, color = TitanGold, fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
          }
          Column {
            Text("Experience", color = TextMutedDark, fontSize = 10.sp)
            Text(item.experienceLevel, color = TextPrimaryDark, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
          }
          Column {
            Text("Location", color = TextMutedDark, fontSize = 10.sp)
            Text(item.location, color = TextPrimaryDark, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Hiring Manager Lead
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(SlateDarker)
            .padding(10.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(Icons.Default.Person, contentDescription = null, tint = TitanCyan, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Column {
            Text("Hiring Lead & Contact Sponsor", color = TextMutedDark, fontSize = 9.5.sp)
            Text(item.hiringManagerLead, color = TextPrimaryDark, fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold)
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Key Requirements Checklist
        Text(
          text = "CORE REQUIREMENTS & MATCH FACTORS",
          color = TextMutedDark,
          fontSize = 10.sp,
          fontWeight = FontWeight.Bold,
          fontFamily = FontFamily.Monospace
        )
        Spacer(modifier = Modifier.height(4.dp))
        item.keyRequirements.forEach { req ->
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = TitanEmerald, modifier = Modifier.size(13.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = req, color = TextSecondaryDark, fontSize = 11.sp)
          }
        }

        // Grounded Sources
        if (item.groundedSources.isNotEmpty()) {
          Spacer(modifier = Modifier.height(10.dp))
          Text(
            text = "GROUNDED WEB SOURCES & CITATIONS",
            color = TextMutedDark,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
          )
          Spacer(modifier = Modifier.height(4.dp))
          item.groundedSources.forEach { source ->
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp)
                .clickable {
                  try {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(source.url))
                    context.startActivity(intent)
                  } catch (e: Exception) {
                    Toast.makeText(context, "Cannot open link: ${source.url}", Toast.LENGTH_SHORT).show()
                  }
                },
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(Icons.AutoMirrored.Filled.Launch, contentDescription = null, tint = TitanCyan, modifier = Modifier.size(12.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = source.title,
                color = TitanCyan,
                fontSize = 10.5.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Action Buttons Row
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
            Icon(Icons.Default.Handshake, contentDescription = null, tint = TitanIndigo, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Ledger", color = TitanIndigo, fontSize = 11.sp, fontWeight = FontWeight.Bold)
          }

          Button(
            onClick = {
              onApply1Click()
              onDismiss()
            },
            colors = ButtonDefaults.buttonColors(containerColor = TitanGold, contentColor = ObsidianDark),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.weight(1f)
          ) {
            Icon(Icons.Default.Bolt, contentDescription = null, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("1-Click Apply", fontSize = 11.sp, fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}
