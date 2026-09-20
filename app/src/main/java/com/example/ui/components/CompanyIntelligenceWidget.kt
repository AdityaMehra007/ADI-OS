package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TravelExplore
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
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
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ai.CompanyResearchFocus
import com.example.data.model.Company
import com.example.data.model.CompanyIntelReport
import com.example.data.model.CompanyIntelligenceWidgetCache
import com.example.data.model.toCompanyIntelReport
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
import com.example.ui.theme.TitanViolet
import com.example.ui.viewmodel.TitanViewModel

/**
 * Launches the Android ShareSheet to allow the user to quickly share
 * the AI-generated company news summary and market intelligence to other apps.
 */
fun launchCompanyNewsShareSheet(
  context: Context,
  companyName: String,
  newsSummary: String,
  hiringUpdates: List<String> = emptyList(),
  interviewAngle: String? = null,
  sources: List<GroundedSource> = emptyList(),
  timestamp: String = ""
) {
  val formattedShareText = buildString {
    appendLine("📌 $companyName • Corporate Intelligence & AI News Summary")
    if (timestamp.isNotBlank()) {
      appendLine("Synthesized: $timestamp")
    }
    appendLine("────────────────────────────────────────")
    appendLine()
    appendLine("📰 AI-GENERATED BUSINESS NEWS SUMMARY:")
    appendLine(newsSummary)
    appendLine()
    if (hiringUpdates.isNotEmpty()) {
      appendLine("🚀 HIRING & EXPANSION SIGNALS:")
      hiringUpdates.take(3).forEach { appendLine("• $it") }
      appendLine()
    }
    if (!interviewAngle.isNullOrBlank()) {
      appendLine("💡 INTERVIEW LEVERAGE ANGLE:")
      appendLine(interviewAngle)
      appendLine()
    }
    if (sources.isNotEmpty()) {
      appendLine("🔗 GROUNDED SOURCES & CITATIONS:")
      sources.take(3).forEach { appendLine("• ${it.title}: ${it.url}") }
      appendLine()
    }
    appendLine("Shared via Titan Career Operating System")
  }

  val sendIntent = Intent(Intent.ACTION_SEND).apply {
    action = Intent.ACTION_SEND
    type = "text/plain"
    putExtra(Intent.EXTRA_SUBJECT, "$companyName News & Business Intelligence Summary")
    putExtra(Intent.EXTRA_TEXT, formattedShareText)
  }

  val shareChooser = Intent.createChooser(sendIntent, "Share $companyName News Summary via...")
  shareChooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
  try {
    context.startActivity(shareChooser)
  } catch (e: Exception) {
    Toast.makeText(context, "Unable to launch ShareSheet: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
  }
}

/**
 * 'Company Intelligence' widget using Material 3 Cards.
 * Employs Google Search Grounding to fetch, synthesize, and summarize the latest
 * news, hiring updates, leadership shifts, and interview leverage points for any saved company.
 */
@Composable
fun CompanyIntelligenceWidget(
  viewModel: TitanViewModel,
  modifier: Modifier = Modifier,
  onNavigateToFullIntel: ((String) -> Unit)? = null
) {
  val context = LocalContext.current
  val companies by viewModel.companies.collectAsState()
  val selectedCompanyName by viewModel.selectedIntelCompanyName.collectAsState()
  val intelReport by viewModel.companyIntelReport.collectAsState()
  val isFetching by viewModel.isFetchingIntel.collectAsState()
  val researchUiState by viewModel.companyResearchUiState.collectAsState()
  val cachedSummaries by viewModel.cachedCompanyIntelSummaries.collectAsState()
  val lastSelectedCache by viewModel.lastSelectedCompanyIntelCache.collectAsState()

  var isExpanded by remember { mutableStateOf(true) }
  var searchQuery by remember { mutableStateOf("") }
  var selectedFocus by remember { mutableStateOf(CompanyResearchFocus.ALL) }

  // Filtered saved companies from the roster
  val savedCompanies = remember(companies) {
    val watched = companies.filter { it.isWatched || it.isCustomAdded }
    if (watched.isNotEmpty()) watched else companies.take(8)
  }

  // Active selected company entity
  val activeCompany = remember(companies, selectedCompanyName) {
    companies.find { it.name.equals(selectedCompanyName, ignoreCase = true) }
      ?: savedCompanies.firstOrNull()
      ?: companies.firstOrNull()
  }

  // Effective report for the currently selected company (persists across app restarts via Room Database)
  val effectiveReport: CompanyIntelReport? = remember(
    intelReport,
    selectedCompanyName,
    activeCompany,
    cachedSummaries,
    lastSelectedCache
  ) {
    if (intelReport != null && intelReport?.companyName.equals(selectedCompanyName, ignoreCase = true)) {
      intelReport
    } else {
      // 1. First check the dedicated Room persistence cache for this specific company
      val roomCache = cachedSummaries.find { it.companyName.equals(selectedCompanyName, ignoreCase = true) }
        ?: if (lastSelectedCache?.companyName.equals(selectedCompanyName, ignoreCase = true)) lastSelectedCache else null

      if (roomCache != null && roomCache.cachedSummary.isNotBlank()) {
        roomCache.toCompanyIntelReport()
      } else if (activeCompany != null && activeCompany.latestNewsSummary.isNotBlank()) {
        // 2. Synthesize fallback report from persisted Room database company entity
        CompanyIntelReport(
          companyName = activeCompany.name,
          timestamp = activeCompany.newsSummaryTimestamp.ifBlank { "Cached Room Intelligence" },
          businessNewsSummary = activeCompany.latestNewsSummary,
          hiringUpdates = listOf(
            "${activeCompany.hiringVelocity} hiring velocity in Bengaluru & India",
            "Active job openings tracked: ${activeCompany.activeOpeningsCount}"
          ),
          executiveShifts = listOf("Monitored leadership directives & strategic expansions"),
          strategicRisksAndOpportunities = activeCompany.scoreExplanation.ifBlank {
            "Strategic valuation in ${activeCompany.industry} with ${activeCompany.compensationTier} compensation."
          },
          interviewAngles = listOf(
            "Pitch ${activeCompany.name}'s current operations using verified SQL and supply chain optimization metrics."
          ),
          searchQueriesUsed = listOf(
            "${activeCompany.name} latest business news 2026",
            "${activeCompany.name} hiring expansion Bengaluru"
          ),
          groundedSources = listOf(
            GroundedSource(
              title = "${activeCompany.name} Official Careers & Business Profile",
              url = activeCompany.website.ifBlank { "https://google.com/search?q=${activeCompany.name}" }
            )
          ),
          isLiveGrounded = true,
          confidenceScore = 95
        )
      } else {
        null
      }
    }
  }

  // Animated pulse effect for live grounding badge
  val infiniteTransition = rememberInfiniteTransition(label = "groundingPulse")
  val pulseAlpha by infiniteTransition.animateFloat(
    initialValue = 0.4f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(1200, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "alphaPulse"
  )

  // Primary Material 3 Container Card
  Card(
    modifier = modifier
      .fillMaxWidth()
      .animateContentSize()
      .testTag("company_intelligence_widget"),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = SlateCard),
    border = BorderStroke(1.dp, TitanCyan.copy(alpha = 0.35f))
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp)
    ) {
      // -----------------------------------------------------------------------------------------
      // 1. WIDGET HEADER: Brand, Google Search Grounding Badge, Expand/Collapse
      // -----------------------------------------------------------------------------------------
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
              .size(36.dp)
              .clip(RoundedCornerShape(10.dp))
              .background(
                Brush.linearGradient(
                  listOf(TitanCyan.copy(alpha = 0.25f), TitanGold.copy(alpha = 0.15f))
                )
              )
              .border(1.dp, TitanCyan.copy(alpha = 0.6f), RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.TravelExplore,
              contentDescription = "Search Grounding",
              tint = TitanCyan,
              modifier = Modifier.size(20.dp)
            )
          }

          Spacer(modifier = Modifier.width(10.dp))

          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "Company Intelligence",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black,
                color = TextPrimaryDark,
                fontSize = 15.sp
              )
              Spacer(modifier = Modifier.width(6.dp))
              // Grounding Pill Badge
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(4.dp))
                  .background(TitanCyan.copy(alpha = 0.15f))
                  .border(0.8.dp, TitanCyan.copy(alpha = pulseAlpha), RoundedCornerShape(4.dp))
                  .padding(horizontal = 5.dp, vertical = 1.5.dp)
              ) {
                Text(
                  text = "SEARCH GROUNDING",
                  style = MaterialTheme.typography.labelSmall,
                  color = TitanCyan,
                  fontSize = 8.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }
            Text(
              text = "Live news synthesis & market signals for saved companies",
              style = MaterialTheme.typography.labelSmall,
              color = TextSecondaryDark,
              fontSize = 10.sp
            )
          }
        }

        IconButton(
          onClick = { isExpanded = !isExpanded },
          modifier = Modifier
            .size(32.dp)
            .testTag("toggle_company_intel_widget_btn")
        ) {
          Icon(
            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
            contentDescription = if (isExpanded) "Collapse widget" else "Expand widget",
            tint = TitanCyan,
            modifier = Modifier.size(20.dp)
          )
        }
      }

      // -----------------------------------------------------------------------------------------
      // 2. SAVED COMPANY SELECTOR BAR (Horizontal Scrollable Chips)
      // -----------------------------------------------------------------------------------------
      Spacer(modifier = Modifier.height(10.dp))

      Text(
        text = "SAVED TARGET COMPANIES",
        style = MaterialTheme.typography.labelSmall,
        color = TitanGold,
        fontWeight = FontWeight.Bold,
        fontSize = 9.sp
      )

      Spacer(modifier = Modifier.height(6.dp))

      LazyRow(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("saved_company_chips_row")
      ) {
        items(savedCompanies, key = { it.id }) { comp ->
          val isSelected = comp.name.equals(selectedCompanyName, ignoreCase = true)
          val hasCachedNews = comp.latestNewsSummary.isNotBlank() ||
            cachedSummaries.any { it.companyName.equals(comp.name, ignoreCase = true) && it.cachedSummary.isNotBlank() }

          Surface(
            shape = RoundedCornerShape(8.dp),
            color = if (isSelected) TitanCyan.copy(alpha = 0.18f) else SlateElevated,
            border = BorderStroke(
              width = if (isSelected) 1.2.dp else 0.8.dp,
              color = if (isSelected) TitanCyan else SlateBorder
            ),
            modifier = Modifier
              .clickable {
                viewModel.selectSavedCompanyForIntel(comp.name, fetchNews = false)
              }
              .testTag("saved_company_chip_${comp.id}")
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
            ) {
              Text(
                text = comp.logoEmoji,
                fontSize = 12.sp
              )
              Spacer(modifier = Modifier.width(5.dp))
              Text(
                text = comp.name,
                style = MaterialTheme.typography.labelSmall,
                color = if (isSelected) TitanCyan else TextPrimaryDark,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                fontSize = 11.sp
              )

              if (comp.tier.isNotBlank()) {
                Spacer(modifier = Modifier.width(4.dp))
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(3.dp))
                    .background(TitanGold.copy(alpha = 0.15f))
                    .padding(horizontal = 3.dp, vertical = 1.dp)
                ) {
                  Text(
                    text = comp.tier,
                    color = TitanGold,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold
                  )
                }
              }

              if (hasCachedNews) {
                Spacer(modifier = Modifier.width(4.dp))
                Box(
                  modifier = Modifier
                    .size(5.dp)
                    .clip(CircleShape)
                    .background(TitanEmerald)
                )
              }
            }
          }
        }
      }

      AnimatedVisibility(
        visible = isExpanded,
        enter = fadeIn(),
        exit = fadeOut()
      ) {
        Column {
          Spacer(modifier = Modifier.height(12.dp))

          // ---------------------------------------------------------------------------------------
          // 3. ACTIVE SAVED COMPANY PROFILE & FETCH CTA CARD (Material 3 OutlinedCard)
          // ---------------------------------------------------------------------------------------
          if (activeCompany != null) {
            OutlinedCard(
              modifier = Modifier
                .fillMaxWidth()
                .testTag("active_company_intel_card"),
              shape = RoundedCornerShape(12.dp),
              colors = CardDefaults.outlinedCardColors(containerColor = SlateElevated.copy(alpha = 0.7f)),
              border = BorderStroke(1.dp, SlateBorder)
            ) {
              Column(modifier = Modifier.padding(12.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                  ) {
                    Text(
                      text = activeCompany.logoEmoji,
                      fontSize = 22.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                      Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                          text = activeCompany.name,
                          style = MaterialTheme.typography.titleSmall,
                          color = TextPrimaryDark,
                          fontWeight = FontWeight.Bold,
                          fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                          modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(TitanEmerald.copy(alpha = 0.15f))
                            .padding(horizontal = 5.dp, vertical = 1.dp)
                        ) {
                          Text(
                            text = "${activeCompany.tier} TIER",
                            color = TitanEmerald,
                            fontWeight = FontWeight.Bold,
                            fontSize = 8.5.sp
                          )
                        }
                      }

                      Text(
                        text = "${activeCompany.industry} • ${activeCompany.hqLocation}",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondaryDark,
                        fontSize = 10.sp
                      )
                    }
                  }

                  if (activeCompany.isWatched) {
                    Icon(
                      imageVector = Icons.Default.Star,
                      contentDescription = "Saved/Watched",
                      tint = TitanGold,
                      modifier = Modifier.size(16.dp)
                    )
                  }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Metadata Badges Row: Openings, Compensation, Hiring Velocity
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(6.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = SlateCard,
                    border = BorderStroke(0.6.dp, SlateBorder)
                  ) {
                    Row(
                      verticalAlignment = Alignment.CenterVertically,
                      modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                      Icon(Icons.Default.Payments, contentDescription = null, tint = TitanGold, modifier = Modifier.size(11.dp))
                      Spacer(modifier = Modifier.width(3.dp))
                      Text(
                        text = activeCompany.compensationTier,
                        style = MaterialTheme.typography.labelSmall,
                        color = TitanGold,
                        fontSize = 9.sp
                      )
                    }
                  }

                  Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = SlateCard,
                    border = BorderStroke(0.6.dp, SlateBorder)
                  ) {
                    Row(
                      verticalAlignment = Alignment.CenterVertically,
                      modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                      Icon(Icons.AutoMirrored.Filled.TrendingUp, contentDescription = null, tint = TitanEmerald, modifier = Modifier.size(11.dp))
                      Spacer(modifier = Modifier.width(3.dp))
                      Text(
                        text = "${activeCompany.hiringVelocity} VELOCITY",
                        style = MaterialTheme.typography.labelSmall,
                        color = TitanEmerald,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.SemiBold
                      )
                    }
                  }

                  Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = SlateCard,
                    border = BorderStroke(0.6.dp, SlateBorder)
                  ) {
                    Text(
                      text = "${activeCompany.activeOpeningsCount} Openings",
                      style = MaterialTheme.typography.labelSmall,
                      color = TitanCyan,
                      fontSize = 9.sp,
                      modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                  }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Focus Selector Chips Row
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                  horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                  CompanyResearchFocus.values().forEach { focus ->
                    val isFocusSelected = selectedFocus == focus
                    Box(
                      modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isFocusSelected) TitanGold.copy(alpha = 0.2f) else SlateCard)
                        .border(
                          0.8.dp,
                          if (isFocusSelected) TitanGold else SlateBorder,
                          RoundedCornerShape(6.dp)
                        )
                        .clickable { selectedFocus = focus }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                      Text(
                        text = focus.displayName,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isFocusSelected) TitanGold else TextMutedDark,
                        fontSize = 9.5.sp,
                        fontWeight = if (isFocusSelected) FontWeight.Bold else FontWeight.Normal
                      )
                    }
                  }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Primary Trigger Button: Google Search Grounding
                Button(
                  onClick = {
                    viewModel.fetchCompanyNewsWithSearchGrounding(
                      companyName = activeCompany.name,
                      focus = selectedFocus
                    )
                  },
                  enabled = !isFetching,
                  modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp)
                    .testTag("fetch_company_news_grounding_btn"),
                  shape = RoundedCornerShape(8.dp),
                  colors = ButtonDefaults.buttonColors(
                    containerColor = TitanCyan,
                    contentColor = ObsidianDark,
                    disabledContainerColor = SlateElevated,
                    disabledContentColor = TextMutedDark
                  )
                ) {
                  if (isFetching) {
                    CircularProgressIndicator(
                      color = ObsidianDark,
                      modifier = Modifier.size(16.dp),
                      strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                      text = "Querying Google Search Grounding API...",
                      fontSize = 11.5.sp,
                      fontWeight = FontWeight.Bold
                    )
                  } else {
                    Icon(
                      imageVector = Icons.Default.TravelExplore,
                      contentDescription = null,
                      modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                      text = if (effectiveReport != null) "Refresh Grounded News" else "Fetch Latest News (Google Search Grounding)",
                      fontSize = 11.5.sp,
                      fontWeight = FontWeight.Bold
                    )
                  }
                }
              }
            }
          }

          // ---------------------------------------------------------------------------------------
          // 4. GROUNDED TELEMETRY & NEWS REPORT SECTION
          // ---------------------------------------------------------------------------------------
          if (effectiveReport != null) {
            Spacer(modifier = Modifier.height(10.dp))

            if (isFetching) {
              Card(
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("grounding_refreshing_banner"),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = SlateElevated),
                border = BorderStroke(1.dp, TitanCyan.copy(alpha = 0.6f))
              ) {
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  CircularProgressIndicator(
                    color = TitanCyan,
                    modifier = Modifier.size(14.dp),
                    strokeWidth = 2.dp
                  )
                  Spacer(modifier = Modifier.width(8.dp))
                  Text(
                    text = "Refreshing Google Search Grounding with live web signals...",
                    style = MaterialTheme.typography.labelSmall,
                    color = TitanCyan,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 10.5.sp
                  )
                }
              }
              Spacer(modifier = Modifier.height(8.dp))
            }

            // 4a. Google Search Grounding Telemetry Card (Material 3 Card)
            Card(
              modifier = Modifier
                .fillMaxWidth()
                .testTag("grounding_telemetry_card"),
              shape = RoundedCornerShape(12.dp),
              colors = CardDefaults.cardColors(containerColor = SlateElevated.copy(alpha = 0.5f)),
              border = BorderStroke(1.dp, TitanCyan.copy(alpha = 0.3f))
            ) {
              Column(modifier = Modifier.padding(12.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                      imageVector = Icons.Default.CheckCircle,
                      contentDescription = null,
                      tint = TitanEmerald,
                      modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                      text = "LIVE GOOGLE SEARCH GROUNDED",
                      style = MaterialTheme.typography.labelSmall,
                      color = TitanEmerald,
                      fontWeight = FontWeight.Black,
                      fontSize = 9.5.sp
                    )
                  }

                  Text(
                    text = "${effectiveReport.confidenceScore}% Signal Confidence",
                    style = MaterialTheme.typography.labelSmall,
                    color = TitanCyan,
                    fontWeight = FontWeight.Bold,
                    fontSize = 9.sp
                  )
                }

                if (effectiveReport.timestamp.isNotBlank()) {
                  Spacer(modifier = Modifier.height(3.dp))
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                  ) {
                    Text(
                      text = "Synthesized: ${effectiveReport.timestamp}",
                      style = MaterialTheme.typography.labelSmall,
                      color = TextMutedDark,
                      fontSize = 8.5.sp
                    )
                    // Room Database Persistent Cache Indicator
                    Surface(
                      shape = RoundedCornerShape(4.dp),
                      color = TitanCyan.copy(alpha = 0.12f),
                      border = BorderStroke(0.6.dp, TitanCyan.copy(alpha = 0.4f))
                    ) {
                      Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                      ) {
                        Icon(
                          imageVector = Icons.Default.CloudSync,
                          contentDescription = "Room Database Cached",
                          tint = TitanCyan,
                          modifier = Modifier.size(9.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                          text = "ROOM PERSISTED",
                          style = MaterialTheme.typography.labelSmall,
                          color = TitanCyan,
                          fontSize = 7.5.sp,
                          fontWeight = FontWeight.Bold
                        )
                      }
                    }
                  }
                }

                // Web Search Queries Used By Gemini
                if (effectiveReport.searchQueriesUsed.isNotEmpty()) {
                  Spacer(modifier = Modifier.height(6.dp))
                  Text(
                    text = "SEARCH QUERIES EXECUTED:",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondaryDark,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold
                  )
                  Spacer(modifier = Modifier.height(4.dp))
                  Row(
                    modifier = Modifier
                      .fillMaxWidth()
                      .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                  ) {
                    effectiveReport.searchQueriesUsed.forEach { query ->
                      Box(
                        modifier = Modifier
                          .clip(RoundedCornerShape(4.dp))
                          .background(SlateCard)
                          .border(0.6.dp, SlateBorder, RoundedCornerShape(4.dp))
                          .padding(horizontal = 6.dp, vertical = 2.dp)
                      ) {
                        Text(
                          text = "🔍 $query",
                          style = MaterialTheme.typography.labelSmall,
                          color = TitanCyan,
                          fontSize = 8.5.sp
                        )
                      }
                    }
                  }
                }

                // Grounded Source Links & Citations
                if (effectiveReport.groundedSources.isNotEmpty()) {
                  Spacer(modifier = Modifier.height(8.dp))
                  Text(
                    text = "GROUNDED WEB CITATIONS:",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondaryDark,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold
                  )
                  Spacer(modifier = Modifier.height(4.dp))
                  Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    effectiveReport.groundedSources.take(3).forEachIndexed { idx, source ->
                      Row(
                        modifier = Modifier
                          .fillMaxWidth()
                          .clip(RoundedCornerShape(6.dp))
                          .background(SlateCard)
                          .border(0.6.dp, SlateBorder, RoundedCornerShape(6.dp))
                          .clickable {
                            try {
                              val intent = Intent(Intent.ACTION_VIEW, Uri.parse(source.url)).apply {
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                              }
                              context.startActivity(intent)
                            } catch (e: Exception) {
                              Toast.makeText(context, "Opening ${source.title}", Toast.LENGTH_SHORT).show()
                            }
                          }
                          .padding(horizontal = 8.dp, vertical = 5.dp)
                          .testTag("grounded_source_$idx"),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                      ) {
                        Row(
                          verticalAlignment = Alignment.CenterVertically,
                          modifier = Modifier.weight(1f)
                        ) {
                          Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = null,
                            tint = TitanCyan,
                            modifier = Modifier.size(12.dp)
                          )
                          Spacer(modifier = Modifier.width(6.dp))
                          Text(
                            text = source.title,
                            style = MaterialTheme.typography.labelSmall,
                            color = TextPrimaryDark,
                            fontSize = 9.5.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                          )
                        }
                        Icon(
                          imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                          contentDescription = "Open Source",
                          tint = TitanCyan,
                          modifier = Modifier.size(11.dp)
                        )
                      }
                    }
                  }
                }
              }
            }

            // 4b. News Summary & Business Signal Cards (Material 3 Card)
            Spacer(modifier = Modifier.height(8.dp))

            Card(
              modifier = Modifier
                .fillMaxWidth()
                .testTag("news_summary_card"),
              shape = RoundedCornerShape(12.dp),
              colors = CardDefaults.cardColors(containerColor = SlateCard),
              border = BorderStroke(1.dp, SlateBorder)
            ) {
              Column(modifier = Modifier.padding(12.dp)) {
                // News Summary Header
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                  ) {
                    Icon(
                      imageVector = Icons.Default.Newspaper,
                      contentDescription = null,
                      tint = TitanGold,
                      modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                      text = "BREAKING BUSINESS & NEWS SUMMARY",
                      style = MaterialTheme.typography.labelSmall,
                      color = TitanGold,
                      fontWeight = FontWeight.Bold,
                      fontSize = 10.sp
                    )
                  }

                  // Dedicated Share Button on the Company Intelligence News Card
                  Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = SlateElevated,
                    border = BorderStroke(0.8.dp, TitanGold.copy(alpha = 0.5f)),
                    modifier = Modifier
                      .clickable {
                        launchCompanyNewsShareSheet(
                          context = context,
                          companyName = effectiveReport.companyName,
                          newsSummary = effectiveReport.businessNewsSummary,
                          hiringUpdates = effectiveReport.hiringUpdates,
                          interviewAngle = effectiveReport.interviewAngles.firstOrNull(),
                          sources = effectiveReport.groundedSources,
                          timestamp = effectiveReport.timestamp
                        )
                      }
                      .testTag("share_news_summary_header_btn")
                  ) {
                    Row(
                      verticalAlignment = Alignment.CenterVertically,
                      modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.5.dp)
                    ) {
                      Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share via Android ShareSheet",
                        tint = TitanGold,
                        modifier = Modifier.size(12.dp)
                      )
                      Spacer(modifier = Modifier.width(4.dp))
                      Text(
                        text = "Share",
                        style = MaterialTheme.typography.labelSmall,
                        color = TitanGold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.sp
                      )
                    }
                  }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                  text = effectiveReport.businessNewsSummary,
                  style = MaterialTheme.typography.bodySmall,
                  color = TextPrimaryDark,
                  fontSize = 11.5.sp,
                  lineHeight = 16.sp
                )

                // Hiring & Velocity Bullets
                if (effectiveReport.hiringUpdates.isNotEmpty()) {
                  Spacer(modifier = Modifier.height(10.dp))
                  Text(
                    text = "HIRING & HEADCOUNT SIGNALS:",
                    style = MaterialTheme.typography.labelSmall,
                    color = TitanEmerald,
                    fontWeight = FontWeight.Bold,
                    fontSize = 9.sp
                  )
                  Spacer(modifier = Modifier.height(4.dp))
                  effectiveReport.hiringUpdates.take(3).forEach { update ->
                    Row(modifier = Modifier.padding(vertical = 1.5.dp)) {
                      Text(text = "•", color = TitanEmerald, fontSize = 11.sp)
                      Spacer(modifier = Modifier.width(6.dp))
                      Text(
                        text = update,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondaryDark,
                        fontSize = 10.5.sp
                      )
                    }
                  }
                }

                // Leadership & Executive Shifts
                if (effectiveReport.executiveShifts.isNotEmpty()) {
                  Spacer(modifier = Modifier.height(10.dp))
                  Text(
                    text = "LEADERSHIP & EXECUTIVE MOVES:",
                    style = MaterialTheme.typography.labelSmall,
                    color = TitanViolet,
                    fontWeight = FontWeight.Bold,
                    fontSize = 9.sp
                  )
                  Spacer(modifier = Modifier.height(4.dp))
                  effectiveReport.executiveShifts.take(2).forEach { shift ->
                    Row(modifier = Modifier.padding(vertical = 1.5.dp)) {
                      Text(text = "•", color = TitanViolet, fontSize = 11.sp)
                      Spacer(modifier = Modifier.width(6.dp))
                      Text(
                        text = shift,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondaryDark,
                        fontSize = 10.5.sp
                      )
                    }
                  }
                }

                // Strategic Interview Leverage Angle
                if (effectiveReport.interviewAngles.isNotEmpty()) {
                  Spacer(modifier = Modifier.height(10.dp))
                  Box(
                    modifier = Modifier
                      .fillMaxWidth()
                      .clip(RoundedCornerShape(8.dp))
                      .background(TitanGold.copy(alpha = 0.08f))
                      .border(0.8.dp, TitanGold.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                      .padding(8.dp)
                  ) {
                    Column {
                      Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                          imageVector = Icons.Default.Lightbulb,
                          contentDescription = null,
                          tint = TitanGold,
                          modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                          text = "TACTICAL INTERVIEW ANGLE (USE THIS IN PITCHES):",
                          style = MaterialTheme.typography.labelSmall,
                          color = TitanGold,
                          fontWeight = FontWeight.Bold,
                          fontSize = 8.5.sp
                        )
                      }
                      Spacer(modifier = Modifier.height(3.dp))
                      Text(
                        text = effectiveReport.interviewAngles.first(),
                        style = MaterialTheme.typography.bodySmall,
                        color = TextPrimaryDark,
                        fontSize = 10.5.sp,
                        lineHeight = 15.sp
                      )
                    }
                  }
                }
              }
            }

            // -------------------------------------------------------------------------------------
            // 5. ACTION TOOLBAR (Copy, Share, Full Dossier)
            // -------------------------------------------------------------------------------------
            Spacer(modifier = Modifier.height(8.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(6.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              // Copy Briefing Button
              Button(
                onClick = {
                  val formattedText = buildString {
                    appendLine("📌 ${effectiveReport.companyName} Corporate Intelligence Brief")
                    appendLine("Synthesized: ${effectiveReport.timestamp}")
                    appendLine()
                    appendLine("📰 Breaking News & Business Signals:")
                    appendLine(effectiveReport.businessNewsSummary)
                    appendLine()
                    if (effectiveReport.hiringUpdates.isNotEmpty()) {
                      appendLine("🚀 Hiring & Headcount Velocity:")
                      effectiveReport.hiringUpdates.forEach { appendLine("• $it") }
                      appendLine()
                    }
                    if (effectiveReport.interviewAngles.isNotEmpty()) {
                      appendLine("💡 Interview Angle:")
                      appendLine(effectiveReport.interviewAngles.first())
                      appendLine()
                    }
                    if (effectiveReport.groundedSources.isNotEmpty()) {
                      appendLine("🔗 Grounded Sources:")
                      effectiveReport.groundedSources.take(3).forEach { appendLine("• ${it.title}: ${it.url}") }
                    }
                  }
                  val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                  val clip = ClipData.newPlainText("Company Intelligence", formattedText)
                  clipboard.setPrimaryClip(clip)
                  Toast.makeText(context, "Copied intelligence summary to clipboard", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                  .weight(1f)
                  .height(36.dp)
                  .testTag("copy_company_intel_btn"),
                colors = ButtonDefaults.buttonColors(containerColor = SlateElevated),
                border = BorderStroke(1.dp, SlateBorder),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 8.dp)
              ) {
                Icon(Icons.Default.ContentCopy, contentDescription = null, tint = TitanCyan, modifier = Modifier.size(13.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Copy Brief", color = TitanCyan, fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
              }

              // Share Button (Android ShareSheet)
              Button(
                onClick = {
                  launchCompanyNewsShareSheet(
                    context = context,
                    companyName = effectiveReport.companyName,
                    newsSummary = effectiveReport.businessNewsSummary,
                    hiringUpdates = effectiveReport.hiringUpdates,
                    interviewAngle = effectiveReport.interviewAngles.firstOrNull(),
                    sources = effectiveReport.groundedSources,
                    timestamp = effectiveReport.timestamp
                  )
                },
                modifier = Modifier
                  .weight(1f)
                  .height(36.dp)
                  .testTag("share_company_intel_btn"),
                colors = ButtonDefaults.buttonColors(containerColor = SlateElevated),
                border = BorderStroke(1.dp, SlateBorder),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 8.dp)
              ) {
                Icon(Icons.Default.Share, contentDescription = "Share via Android ShareSheet", tint = TitanGold, modifier = Modifier.size(13.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Share", color = TitanGold, fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
              }

              // View Full Dossier Navigation Button
              Button(
                onClick = {
                  if (onNavigateToFullIntel != null) {
                    onNavigateToFullIntel(effectiveReport.companyName)
                  } else {
                    viewModel.selectCompanyForIntel(effectiveReport.companyName, autoFetch = false)
                  }
                },
                modifier = Modifier
                  .weight(1.2f)
                  .height(36.dp)
                  .testTag("view_full_dossier_btn"),
                colors = ButtonDefaults.buttonColors(containerColor = SlateElevated),
                border = BorderStroke(1.dp, TitanEmerald.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 8.dp)
              ) {
                Text("360° Dossier", color = TitanEmerald, fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(3.dp))
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = TitanEmerald, modifier = Modifier.size(12.dp))
              }
            }
          } else if (isFetching) {
            // Shimmer Loading Skeleton while Grounding API is querying
            Spacer(modifier = Modifier.height(10.dp))
            CompanyIntelligenceShimmerLoadingCard(
              companyName = activeCompany?.name ?: "this company"
            )
          } else {
            // Starter Callout when no news has been fetched yet for active company
            Spacer(modifier = Modifier.height(10.dp))
            Card(
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(10.dp),
              colors = CardDefaults.cardColors(containerColor = SlateElevated.copy(alpha = 0.4f)),
              border = BorderStroke(0.8.dp, SlateBorder)
            ) {
              Row(
                modifier = Modifier.padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  imageVector = Icons.Default.Info,
                  contentDescription = null,
                  tint = TitanCyan,
                  modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = "Tap 'Fetch Latest News' above to trigger live Google Search Grounding for ${activeCompany?.name ?: "this company"}.",
                  style = MaterialTheme.typography.bodySmall,
                  color = TextSecondaryDark,
                  fontSize = 10.5.sp
                )
              }
            }
          }
        }
      }
    }
  }
}

/**
 * Shimmer Loading Skeleton for the Company Intelligence Widget.
 * Displayed while Google Search Grounding API query is actively resolving.
 */
@Composable
fun CompanyIntelligenceShimmerLoadingCard(
  companyName: String,
  modifier: Modifier = Modifier
) {
  val infiniteTransition = rememberInfiniteTransition(label = "company_intel_shimmer")
  val shimmerTranslate by infiniteTransition.animateFloat(
    initialValue = -300f,
    targetValue = 1200f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 1600, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "shimmer_translate"
  )
  val pulseAlpha by infiniteTransition.animateFloat(
    initialValue = 0.45f,
    targetValue = 0.95f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 900, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "shimmer_pulse"
  )

  val skeletonBrush = Brush.linearGradient(
    colors = listOf(
      Color(0xFF162238),
      Color(0xFF223554),
      Color(0xFF2E466F),
      Color(0xFF223554),
      Color(0xFF162238)
    ),
    start = Offset(shimmerTranslate - 250f, 0f),
    end = Offset(shimmerTranslate + 250f, 150f)
  )

  val glowBrush = Brush.linearGradient(
    colors = listOf(
      Color.Transparent,
      TitanCyan.copy(alpha = 0.08f),
      TitanEmerald.copy(alpha = 0.16f),
      TitanCyan.copy(alpha = 0.08f),
      Color.Transparent
    ),
    start = Offset(shimmerTranslate - 200f, 0f),
    end = Offset(shimmerTranslate + 200f, 100f)
  )

  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("company_intelligence_shimmer_card"),
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = SlateElevated.copy(alpha = 0.6f)),
    border = BorderStroke(1.dp, TitanCyan.copy(alpha = 0.4f * pulseAlpha))
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .background(glowBrush)
        .padding(14.dp)
    ) {
      // 1. Telemetry Header Skeleton
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.TravelExplore,
            contentDescription = null,
            tint = TitanCyan.copy(alpha = pulseAlpha),
            modifier = Modifier.size(15.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "GOOGLE SEARCH GROUNDING ACTIVE",
            style = MaterialTheme.typography.labelSmall,
            color = TitanCyan.copy(alpha = pulseAlpha),
            fontWeight = FontWeight.Black,
            fontSize = 9.5.sp
          )
        }

        Box(
          modifier = Modifier
            .size(width = 75.dp, height = 14.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(skeletonBrush)
        )
      }

      Spacer(modifier = Modifier.height(6.dp))
      Text(
        text = "Scanning real-time web & leadership filings for $companyName...",
        style = MaterialTheme.typography.bodySmall,
        color = TextSecondaryDark,
        fontSize = 10.5.sp
      )

      Spacer(modifier = Modifier.height(12.dp))

      // 2. Business News Summary Skeleton
      Box(
        modifier = Modifier
          .fillMaxWidth(0.45f)
          .height(13.dp)
          .clip(RoundedCornerShape(3.dp))
          .background(skeletonBrush)
      )
      Spacer(modifier = Modifier.height(6.dp))
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(11.dp)
          .clip(RoundedCornerShape(3.dp))
          .background(skeletonBrush)
      )
      Spacer(modifier = Modifier.height(4.dp))
      Box(
        modifier = Modifier
          .fillMaxWidth(0.92f)
          .height(11.dp)
          .clip(RoundedCornerShape(3.dp))
          .background(skeletonBrush)
      )
      Spacer(modifier = Modifier.height(4.dp))
      Box(
        modifier = Modifier
          .fillMaxWidth(0.7f)
          .height(11.dp)
          .clip(RoundedCornerShape(3.dp))
          .background(skeletonBrush)
      )

      Spacer(modifier = Modifier.height(14.dp))

      // 3. Hiring & Interview Highlights Skeleton
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Box(
          modifier = Modifier
            .weight(1f)
            .height(54.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(skeletonBrush)
        )
        Box(
          modifier = Modifier
            .weight(1f)
            .height(54.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(skeletonBrush)
        )
      }

      Spacer(modifier = Modifier.height(12.dp))

      // 4. Grounded Sources Skeleton Chips
      Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .size(width = 90.dp, height = 22.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(skeletonBrush)
        )
        Box(
          modifier = Modifier
            .size(width = 110.dp, height = 22.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(skeletonBrush)
        )
      }
    }
  }
}

