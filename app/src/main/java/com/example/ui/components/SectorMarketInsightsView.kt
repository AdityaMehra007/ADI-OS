package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SectorMacroSummary
import com.example.data.model.SectorMarketInsightsReport
import com.example.data.model.SectorNewsArticle
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Market Insights Composable:
 * Summarizes recent news, macro shifts, and hiring signals for the sectors listed
 * in the user's target companies using the Gemini API, presented in a scrollable list.
 */
@Composable
fun SectorMarketInsightsView(
  viewModel: TitanViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val insightsReport by viewModel.sectorMarketInsights.collectAsState()
  val isSummarizing by viewModel.isSummarizingSectorNews.collectAsState()
  val selectedSector by viewModel.selectedSectorFilter.collectAsState()
  val searchQuery by viewModel.sectorNewsSearchQuery.collectAsState()
  val statusMessage by viewModel.sectorInsightsStatusMessage.collectAsState()

  val targetCompaniesWithIntel by viewModel.targetCompaniesWithIntel.collectAsState()
  val companiesRoster by viewModel.companies.collectAsState()

  // Trigger initial summary if not loaded
  LaunchedEffect(Unit) {
    if (insightsReport == null) {
      viewModel.summarizeSectorMarketNewsForTargetCompanies(forceRefresh = false)
    }
  }

  // Extract distinct sectors from target companies
  val targetCompanySectors = remember(targetCompaniesWithIntel, companiesRoster) {
    val fromTargets = targetCompaniesWithIntel.mapNotNull { it.company.industry.takeIf { s -> s.isNotBlank() } }
      .plus(targetCompaniesWithIntel.mapNotNull { it.company.subIndustry.takeIf { s -> s.isNotBlank() } })
    val fromRoster = companiesRoster.mapNotNull { it.industry.takeIf { s -> s.isNotBlank() } }
    (fromTargets + fromRoster).distinct().ifEmpty {
      listOf("Enterprise SaaS & AI", "Fintech & Payments", "Quick Commerce & Logistics", "Cloud & DeepTech Infrastructure")
    }
  }

  val totalTargetCompanies = remember(targetCompaniesWithIntel, companiesRoster) {
    (targetCompaniesWithIntel.map { it.company.name } + companiesRoster.map { it.name })
      .filter { it.isNotBlank() }.distinct().size
  }

  // Filtered articles
  val articles = insightsReport?.newsArticles ?: emptyList()
  val filteredArticles = remember(articles, selectedSector, searchQuery) {
    articles.filter { article ->
      val matchesSector = selectedSector == "ALL" ||
          article.sector.equals(selectedSector, ignoreCase = true) ||
          article.sector.contains(selectedSector, ignoreCase = true) ||
          selectedSector.contains(article.sector, ignoreCase = true)

      val matchesQuery = searchQuery.isBlank() ||
          article.headline.contains(searchQuery, ignoreCase = true) ||
          article.summary.contains(searchQuery, ignoreCase = true) ||
          article.keyTakeaway.contains(searchQuery, ignoreCase = true) ||
          article.sector.contains(searchQuery, ignoreCase = true) ||
          article.relevantTargetCompanies.any { it.contains(searchQuery, ignoreCase = true) } ||
          article.tags.any { it.contains(searchQuery, ignoreCase = true) }

      matchesSector && matchesQuery
    }
  }

  // Find active macro summary
  val activeMacroSummary = remember(insightsReport, selectedSector) {
    val summaries = insightsReport?.sectorMacroSummaries ?: emptyList()
    if (selectedSector == "ALL") {
      summaries.firstOrNull()
    } else {
      summaries.find {
        it.sector.equals(selectedSector, ignoreCase = true) ||
        it.sector.contains(selectedSector, ignoreCase = true) ||
        selectedSector.contains(it.sector, ignoreCase = true)
      } ?: summaries.firstOrNull()
    }
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(ObsidianDark)
      .testTag("sector_market_insights_view")
  ) {
    // Scrollable container
    LazyColumn(
      verticalArrangement = Arrangement.spacedBy(14.dp),
      contentPadding = PaddingValues(bottom = 90.dp),
      modifier = Modifier
        .fillMaxSize()
        .testTag("market_insights_scrollable_list")
    ) {
      // 1. Executive Intelligence Header Card
      item {
        MarketInsightsExecutiveHeader(
          report = insightsReport,
          isSummarizing = isSummarizing,
          totalSectors = targetCompanySectors.size,
          totalTargetCompanies = totalTargetCompanies,
          statusMessage = statusMessage,
          onRefreshClick = {
            viewModel.summarizeSectorMarketNewsForTargetCompanies(forceRefresh = true)
          }
        )
      }

      // 2. Search Field & Sector Filter Chips
      item {
        SearchAndSectorFilterSection(
          searchQuery = searchQuery,
          onSearchQueryChanged = { viewModel.setSectorNewsSearchQuery(it) },
          sectors = targetCompanySectors,
          selectedSector = selectedSector,
          onSectorSelected = { viewModel.setSelectedSectorFilter(it) },
          totalArticlesCount = articles.size
        )
      }

      // 3. Sector Macro Dossier Card
      if (activeMacroSummary != null) {
        item {
          SectorMacroDossierCard(
            summary = activeMacroSummary,
            selectedSectorName = selectedSector
          )
        }
      }

      // 4. Section Subheader with article count
      item {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Newspaper,
              contentDescription = null,
              tint = TitanCyan,
              modifier = Modifier.size(16.dp)
            )
            Text(
              text = if (selectedSector == "ALL") "All Sector News Updates" else "$selectedSector News",
              style = MaterialTheme.typography.titleSmall,
              fontWeight = FontWeight.Bold,
              color = TextPrimaryDark
            )
          }

          Text(
            text = "${filteredArticles.size} updates",
            style = MaterialTheme.typography.labelSmall,
            color = TitanCyan,
            fontWeight = FontWeight.SemiBold
          )
        }
      }

      // 5. Scrollable List of News Articles
      if (filteredArticles.isEmpty()) {
        item {
          EmptySectorNewsView(
            searchQuery = searchQuery,
            selectedSector = selectedSector,
            isSummarizing = isSummarizing,
            onResetFilters = {
              viewModel.setSectorNewsSearchQuery("")
              viewModel.setSelectedSectorFilter("ALL")
            },
            onRefresh = {
              viewModel.summarizeSectorMarketNewsForTargetCompanies(forceRefresh = true)
            }
          )
        }
      } else {
        items(
          items = filteredArticles,
          key = { it.id.ifBlank { "${it.headline}_${it.timeAgo}" } }
        ) { article ->
          SectorNewsCard(
            article = article,
            onTargetCompanyClick = { companyName ->
              viewModel.selectCompanyForIntel(companyName, autoFetch = true)
            },
            onCopyClick = { text ->
              val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
              clipboard.setPrimaryClip(ClipData.newPlainText("Sector News", text))
              Toast.makeText(context, "News summary copied to clipboard", Toast.LENGTH_SHORT).show()
            },
            onShareClick = { text ->
              val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, article.headline)
                putExtra(Intent.EXTRA_TEXT, text)
              }
              context.startActivity(Intent.createChooser(shareIntent, "Share Sector News Update"))
            }
          )
        }
      }
    }
  }
}

/**
 * Top Executive Dossier Card presenting the Gemini API synthesis status
 */
@Composable
private fun MarketInsightsExecutiveHeader(
  report: SectorMarketInsightsReport?,
  isSummarizing: Boolean,
  totalSectors: Int,
  totalTargetCompanies: Int,
  statusMessage: String,
  onRefreshClick: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = SlateCard),
    border = BorderStroke(1.dp, SlateBorder),
    modifier = Modifier
      .fillMaxWidth()
      .testTag("market_insights_executive_header")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Surface(
              shape = CircleShape,
              color = TitanCyan.copy(alpha = 0.15f),
              modifier = Modifier.size(26.dp)
            ) {
              Box(contentAlignment = Alignment.Center) {
                Icon(
                  imageVector = Icons.Default.AutoAwesome,
                  contentDescription = null,
                  tint = TitanCyan,
                  modifier = Modifier.size(15.dp)
                )
              }
            }
            Text(
              text = "Market Insights",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = TextPrimaryDark,
              fontSize = 17.sp
            )
          }

          Spacer(modifier = Modifier.height(4.dp))

          Text(
            text = "AI news summarization for sectors in your target pipeline",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondaryDark,
            fontSize = 12.sp
          )
        }

        // Refresh Action Button
        Button(
          onClick = onRefreshClick,
          enabled = !isSummarizing,
          shape = RoundedCornerShape(8.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = TitanCyan,
            contentColor = ObsidianDark
          ),
          contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
          modifier = Modifier
            .height(34.dp)
            .testTag("btn_refresh_sector_insights")
        ) {
          if (isSummarizing) {
            CircularProgressIndicator(
              modifier = Modifier.size(14.dp),
              color = ObsidianDark,
              strokeWidth = 2.dp
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Analyzing...",
              fontWeight = FontWeight.Bold,
              fontSize = 11.sp
            )
          } else {
            Icon(
              imageVector = Icons.Default.Refresh,
              contentDescription = null,
              modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "Summarize",
              fontWeight = FontWeight.Bold,
              fontSize = 11.sp
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Status & Telemetry Strip
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(8.dp))
          .background(SlateElevated)
          .border(1.dp, SlateBorder, RoundedCornerShape(8.dp))
          .padding(horizontal = 10.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Surface(
            shape = CircleShape,
            color = if (isSummarizing) TitanGold else TitanEmerald,
            modifier = Modifier.size(7.dp)
          ) {}
          Text(
            text = if (isSummarizing) "Synthesizing with Gemini..." else "Gemini 3.5 Flash · Grounded",
            style = MaterialTheme.typography.labelSmall,
            color = if (isSummarizing) TitanGold else TitanEmerald,
            fontWeight = FontWeight.Medium,
            fontSize = 11.sp
          )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          Text(
            text = "$totalSectors Sectors",
            style = MaterialTheme.typography.labelSmall,
            color = TitanCyan,
            fontWeight = FontWeight.SemiBold,
            fontSize = 11.sp
          )
          Text(
            text = "•",
            style = MaterialTheme.typography.labelSmall,
            color = TextMutedDark
          )
          Text(
            text = "$totalTargetCompanies Target Companies",
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondaryDark,
            fontWeight = FontWeight.Medium,
            fontSize = 11.sp
          )
        }
      }

      // Executive overview summary paragraph if available
      val execSummary = report?.executiveSummary
      if (!execSummary.isNullOrBlank()) {
        Spacer(modifier = Modifier.height(10.dp))
        Text(
          text = execSummary,
          style = MaterialTheme.typography.bodySmall,
          color = TextPrimaryDark,
          lineHeight = 17.sp,
          fontSize = 12.sp,
          modifier = Modifier.padding(horizontal = 2.dp)
        )
      }
    }
  }
}

/**
 * Search input and horizontal Sector filter chips
 */
@Composable
private fun SearchAndSectorFilterSection(
  searchQuery: String,
  onSearchQueryChanged: (String) -> Unit,
  sectors: List<String>,
  selectedSector: String,
  onSectorSelected: (String) -> Unit,
  totalArticlesCount: Int
) {
  Column(
    modifier = Modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    // Search Bar
    OutlinedTextField(
      value = searchQuery,
      onValueChange = onSearchQueryChanged,
      modifier = Modifier
        .fillMaxWidth()
        .testTag("input_sector_news_search"),
      placeholder = {
        Text(
          "Filter news by keyword, company, or topic...",
          color = TextMutedDark,
          fontSize = 12.sp
        )
      },
      leadingIcon = {
        Icon(
          Icons.Default.Search,
          contentDescription = null,
          tint = TextSecondaryDark,
          modifier = Modifier.size(16.dp)
        )
      },
      trailingIcon = {
        if (searchQuery.isNotEmpty()) {
          IconButton(
            onClick = { onSearchQueryChanged("") },
            modifier = Modifier.size(24.dp)
          ) {
            Icon(
              Icons.Default.Clear,
              contentDescription = "Clear search",
              tint = TextSecondaryDark,
              modifier = Modifier.size(14.dp)
            )
          }
        }
      },
      singleLine = true,
      colors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = TitanCyan,
        unfocusedBorderColor = SlateBorder,
        focusedContainerColor = SlateCard,
        unfocusedContainerColor = SlateCard,
        focusedTextColor = TextPrimaryDark,
        unfocusedTextColor = TextPrimaryDark
      ),
      shape = RoundedCornerShape(8.dp)
    )

    // Sector Chips Carousel
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .horizontalScroll(rememberScrollState()),
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      // "All Sectors" Chip
      FilterChip(
        selected = selectedSector == "ALL",
        onClick = { onSectorSelected("ALL") },
        label = {
          Text(
            text = "All Sectors ($totalArticlesCount)",
            fontSize = 11.sp,
            fontWeight = if (selectedSector == "ALL") FontWeight.Bold else FontWeight.Normal
          )
        },
        colors = FilterChipDefaults.filterChipColors(
          containerColor = SlateCard,
          labelColor = TextSecondaryDark,
          selectedContainerColor = TitanCyan.copy(alpha = 0.2f),
          selectedLabelColor = TitanCyan
        ),
        border = BorderStroke(
          1.dp,
          if (selectedSector == "ALL") TitanCyan else SlateBorder
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.testTag("chip_sector_all")
      )

      // Individual Sector Chips
      sectors.forEach { sector ->
        val isSelected = selectedSector == sector
        FilterChip(
          selected = isSelected,
          onClick = { onSectorSelected(sector) },
          label = {
            Text(
              text = sector,
              fontSize = 11.sp,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
          },
          colors = FilterChipDefaults.filterChipColors(
            containerColor = SlateCard,
            labelColor = TextSecondaryDark,
            selectedContainerColor = TitanCyan.copy(alpha = 0.2f),
            selectedLabelColor = TitanCyan
          ),
          border = BorderStroke(
            1.dp,
            if (isSelected) TitanCyan else SlateBorder
          ),
          shape = RoundedCornerShape(16.dp),
          modifier = Modifier.testTag("chip_sector_$sector")
        )
      }
    }
  }
}

/**
 * Macro intelligence overview card for the currently selected sector
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SectorMacroDossierCard(
  summary: SectorMacroSummary,
  selectedSectorName: String
) {
  Card(
    shape = RoundedCornerShape(10.dp),
    colors = CardDefaults.cardColors(containerColor = SlateElevated),
    border = BorderStroke(1.dp, TitanIndigo.copy(alpha = 0.4f)),
    modifier = Modifier
      .fillMaxWidth()
      .testTag("card_sector_macro_dossier")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.TrendingUp,
            contentDescription = null,
            tint = TitanIndigo,
            modifier = Modifier.size(15.dp)
          )
          Text(
            text = "Macro Sector Dossier: ${summary.sector}",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = TextPrimaryDark,
            fontSize = 12.sp
          )
        }

        // Velocity Pill
        val velocityColor = when (summary.hiringVelocity.uppercase()) {
          "SURGING" -> TitanEmerald
          "STEADY" -> TitanCyan
          else -> TitanGold
        }
        Surface(
          shape = RoundedCornerShape(4.dp),
          color = velocityColor.copy(alpha = 0.15f),
          border = BorderStroke(1.dp, velocityColor.copy(alpha = 0.4f))
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Speed,
              contentDescription = null,
              tint = velocityColor,
              modifier = Modifier.size(11.dp)
            )
            Text(
              text = "Hiring: ${summary.hiringVelocity}",
              color = velocityColor,
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }

      // Macro Trend Body
      Text(
        text = summary.macroTrend,
        style = MaterialTheme.typography.bodySmall,
        color = TextSecondaryDark,
        fontSize = 12.sp,
        lineHeight = 16.sp
      )

      // Top Skills in Demand
      if (summary.topSkillsInDemand.isNotEmpty()) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Text(
            text = "High-Demand Skills:",
            style = MaterialTheme.typography.labelSmall,
            color = TitanCyan,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp
          )
          FlowRow(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            summary.topSkillsInDemand.forEach { skill ->
              Surface(
                shape = RoundedCornerShape(4.dp),
                color = SlateCard,
                border = BorderStroke(1.dp, SlateBorder)
              ) {
                Text(
                  text = skill,
                  color = TextPrimaryDark,
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Medium,
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
            }
          }
        }
      }

      // Pipeline companies in this sector
      if (summary.keyCompaniesTracked.isNotEmpty()) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Text(
            text = "Your Target Pipeline:",
            style = MaterialTheme.typography.labelSmall,
            color = TitanGold,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp
          )
          FlowRow(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            summary.keyCompaniesTracked.forEach { comp ->
              Surface(
                shape = RoundedCornerShape(4.dp),
                color = TitanGold.copy(alpha = 0.12f),
                border = BorderStroke(1.dp, TitanGold.copy(alpha = 0.3f))
              ) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                  horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                  Text(text = "🏢", fontSize = 9.sp)
                  Text(
                    text = comp,
                    color = TitanGold,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold
                  )
                }
              }
            }
          }
        }
      }
    }
  }
}

/**
 * Individual News Article Card with rich strategic and hiring breakdowns
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SectorNewsCard(
  article: SectorNewsArticle,
  onTargetCompanyClick: (String) -> Unit,
  onCopyClick: (String) -> Unit,
  onShareClick: (String) -> Unit
) {
  var isExpanded by remember { mutableStateOf(false) }

  Card(
    shape = RoundedCornerShape(10.dp),
    colors = CardDefaults.cardColors(containerColor = SlateCard),
    border = BorderStroke(1.dp, SlateBorder),
    modifier = Modifier
      .fillMaxWidth()
      .animateContentSize()
      .testTag("card_sector_news_${article.id}")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      // 1. Sector Tag & Sentiment / Impact Badges
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Sector Pill
        Surface(
          shape = RoundedCornerShape(4.dp),
          color = TitanCyan.copy(alpha = 0.15f),
          border = BorderStroke(1.dp, TitanCyan.copy(alpha = 0.3f))
        ) {
          Text(
            text = article.sector,
            color = TitanCyan,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
          )
        }

        // Sentiment & Impact
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          val sentimentColor = when (article.sentiment.uppercase()) {
            "BULLISH" -> TitanEmerald
            "CAUTIOUS", "BEARISH" -> TitanCrimson
            else -> TitanGold
          }
          Surface(
            shape = RoundedCornerShape(4.dp),
            color = sentimentColor.copy(alpha = 0.12f),
            border = BorderStroke(1.dp, sentimentColor.copy(alpha = 0.3f))
          ) {
            Text(
              text = article.sentiment,
              color = sentimentColor,
              fontSize = 9.sp,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
            )
          }

          if (article.impactLevel.isNotBlank()) {
            Surface(
              shape = RoundedCornerShape(4.dp),
              color = TitanIndigo.copy(alpha = 0.15f),
              border = BorderStroke(1.dp, TitanIndigo.copy(alpha = 0.3f))
            ) {
              Text(
                text = "${article.impactLevel} IMPACT",
                color = TitanIndigo,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
              )
            }
          }
        }
      }

      // 2. Headline
      Text(
        text = article.headline,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = TextPrimaryDark,
        fontSize = 15.sp,
        lineHeight = 20.sp
      )

      // 3. Source & Time metadata
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        Icon(
          imageVector = Icons.Default.Public,
          contentDescription = null,
          tint = TextMutedDark,
          modifier = Modifier.size(12.dp)
        )
        Text(
          text = article.source,
          style = MaterialTheme.typography.labelSmall,
          color = TextSecondaryDark,
          fontSize = 11.sp
        )
        Text(
          text = "•",
          style = MaterialTheme.typography.labelSmall,
          color = TextMutedDark
        )
        Text(
          text = article.timeAgo,
          style = MaterialTheme.typography.labelSmall,
          color = TextMutedDark,
          fontSize = 11.sp
        )
      }

      // 4. Gemini Summary Paragraph
      Text(
        text = article.summary,
        style = MaterialTheme.typography.bodySmall,
        color = TextSecondaryDark,
        fontSize = 12.sp,
        lineHeight = 18.sp
      )

      // 5. Strategic Takeaway Callout Card
      if (article.keyTakeaway.isNotBlank()) {
        Surface(
          shape = RoundedCornerShape(6.dp),
          color = SlateElevated,
          border = BorderStroke(1.dp, TitanGold.copy(alpha = 0.3f)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Lightbulb,
              contentDescription = null,
              tint = TitanGold,
              modifier = Modifier
                .size(15.dp)
                .padding(top = 1.dp)
            )
            Column {
              Text(
                text = "Strategic Career Takeaway",
                style = MaterialTheme.typography.labelSmall,
                color = TitanGold,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
              )
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = article.keyTakeaway,
                style = MaterialTheme.typography.bodySmall,
                color = TextPrimaryDark,
                fontSize = 11.sp,
                lineHeight = 16.sp
              )
            }
          }
        }
      }

      // 6. Hiring & Role Implication (expandable or direct)
      if (article.hiringImplication.isNotBlank()) {
        Row(
          verticalAlignment = Alignment.Top,
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(TitanCyan.copy(alpha = 0.06f))
            .padding(8.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Work,
            contentDescription = null,
            tint = TitanCyan,
            modifier = Modifier.size(14.dp)
          )
          Column {
            Text(
              text = "Hiring & Headcount Implication:",
              style = MaterialTheme.typography.labelSmall,
              color = TitanCyan,
              fontWeight = FontWeight.Bold,
              fontSize = 11.sp
            )
            Text(
              text = article.hiringImplication,
              style = MaterialTheme.typography.bodySmall,
              color = TextSecondaryDark,
              fontSize = 11.sp,
              lineHeight = 15.sp
            )
          }
        }
      }

      // 7. Relevant Target Companies in User's Pipeline
      if (article.relevantTargetCompanies.isNotEmpty()) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Text(
            text = "Target Companies Mentioned:",
            style = MaterialTheme.typography.labelSmall,
            color = TextMutedDark,
            fontSize = 10.sp
          )
          FlowRow(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            article.relevantTargetCompanies.forEach { compName ->
              Surface(
                shape = RoundedCornerShape(4.dp),
                color = TitanCyan.copy(alpha = 0.12f),
                border = BorderStroke(1.dp, TitanCyan.copy(alpha = 0.3f)),
                modifier = Modifier.clickable { onTargetCompanyClick(compName) }
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                  Text(text = "🏢", fontSize = 9.sp)
                  Text(
                    text = compName,
                    color = TitanCyan,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold
                  )
                }
              }
            }
          }
        }
      }

      // 8. Tags & Action Affordances (Copy, Share)
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Hashtag chips
        FlowRow(
          horizontalArrangement = Arrangement.spacedBy(4.dp),
          verticalArrangement = Arrangement.spacedBy(2.dp),
          modifier = Modifier.weight(1f)
        ) {
          article.tags.forEach { tag ->
            Text(
              text = "#$tag",
              style = MaterialTheme.typography.labelSmall,
              color = TextMutedDark,
              fontSize = 10.sp
            )
          }
        }

        // Action Icons
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
          IconButton(
            onClick = {
              val shareText = "${article.headline}\n\n${article.summary}\n\nStrategic Takeaway: ${article.keyTakeaway}\nSource: ${article.source} (${article.timeAgo})"
              onCopyClick(shareText)
            },
            modifier = Modifier.size(28.dp)
          ) {
            Icon(
              imageVector = Icons.Default.ContentCopy,
              contentDescription = "Copy Summary",
              tint = TextSecondaryDark,
              modifier = Modifier.size(14.dp)
            )
          }

          IconButton(
            onClick = {
              val shareText = "${article.headline}\n\n${article.summary}\n\nKey Takeaway: ${article.keyTakeaway}\nSource: ${article.source}"
              onShareClick(shareText)
            },
            modifier = Modifier.size(28.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Share,
              contentDescription = "Share Update",
              tint = TextSecondaryDark,
              modifier = Modifier.size(14.dp)
            )
          }
        }
      }
    }
  }
}

/**
 * Clean Empty State when no news matches active filters
 */
@Composable
private fun EmptySectorNewsView(
  searchQuery: String,
  selectedSector: String,
  isSummarizing: Boolean,
  onResetFilters: () -> Unit,
  onRefresh: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = SlateCard),
    border = BorderStroke(1.dp, SlateBorder),
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 20.dp)
      .testTag("empty_sector_news_view")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(24.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      Surface(
        shape = CircleShape,
        color = SlateElevated,
        modifier = Modifier.size(52.dp)
      ) {
        Box(contentAlignment = Alignment.Center) {
          Icon(
            imageVector = Icons.Default.Newspaper,
            contentDescription = null,
            tint = TitanCyan,
            modifier = Modifier.size(26.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      Text(
        text = if (searchQuery.isNotBlank()) "No updates match \"$searchQuery\"" else "No news updates found for $selectedSector",
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        color = TextPrimaryDark
      )

      Spacer(modifier = Modifier.height(6.dp))

      Text(
        text = if (searchQuery.isNotBlank()) {
          "Try clearing your search keyword or switching to \"All Sectors\"."
        } else {
          "Run the Gemini news synthesizer to pull the latest strategic moves and hiring news for your target company sectors."
        },
        style = MaterialTheme.typography.bodySmall,
        color = TextSecondaryDark,
        fontSize = 12.sp,
        textAlign = androidx.compose.ui.text.style.TextAlign.Center
      )

      Spacer(modifier = Modifier.height(16.dp))

      Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        if (searchQuery.isNotBlank() || selectedSector != "ALL") {
          OutlinedButton(
            onClick = onResetFilters,
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = TitanCyan),
            border = BorderStroke(1.dp, TitanCyan)
          ) {
            Text("Reset Filters", fontSize = 12.sp)
          }
        }

        Button(
          onClick = onRefresh,
          enabled = !isSummarizing,
          shape = RoundedCornerShape(8.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = TitanCyan,
            contentColor = ObsidianDark
          )
        ) {
          Icon(
            imageVector = Icons.Default.AutoAwesome,
            contentDescription = null,
            modifier = Modifier.size(14.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text("Summarize with Gemini", fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}
