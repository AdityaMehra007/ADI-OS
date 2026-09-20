package com.example.ui.components

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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BookmarkAdded
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.example.data.model.SmartJobMatch
import com.example.ui.theme.ObsidianDark
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateCard
import com.example.ui.theme.SlateElevated
import com.example.ui.theme.TextMutedDark
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import com.example.ui.theme.TitanCyan
import com.example.ui.theme.TitanEmerald
import com.example.ui.theme.TitanGold
import com.example.ui.theme.TitanIndigo
import com.example.ui.viewmodel.TitanScreen
import com.example.ui.viewmodel.TitanViewModel

@Composable
fun SmartJobSearchView(
  viewModel: TitanViewModel,
  onNavigateToPipeline: () -> Unit = { viewModel.navigateTo(TitanScreen.APPLICATIONS) },
  modifier: Modifier = Modifier
) {
  val targetCompanies by viewModel.companies.collectAsState()
  val userProfile by viewModel.userProfile.collectAsState()
  val matches by viewModel.smartJobMatches.collectAsState()
  val isSearching by viewModel.isSmartJobSearching.collectAsState()
  val crawlerStatus by viewModel.smartJobSearchCrawlerStatus.collectAsState()
  val scanSummary by viewModel.smartJobSearchSummary.collectAsState()
  val savedFeedback by viewModel.smartJobSearchSavedFeedback.collectAsState()
  val searchQuery by viewModel.smartJobSearchQuery.collectAsState()
  val selectedCompanyFilter by viewModel.smartJobSearchSelectedCompany.collectAsState()
  val minFitFilter by viewModel.smartJobSearchMinFitFilter.collectAsState()

  val filteredMatches = matches.filter { match ->
    val matchesCompany = selectedCompanyFilter == "ALL" ||
      match.companyId.equals(selectedCompanyFilter, ignoreCase = true) ||
      match.companyName.contains(selectedCompanyFilter, ignoreCase = true)

    val matchesRole = searchQuery.isBlank() ||
      match.roleTitle.contains(searchQuery, ignoreCase = true) ||
      match.roleFamily.contains(searchQuery, ignoreCase = true) ||
      match.keyMatchingSkills.any { it.contains(searchQuery, ignoreCase = true) } ||
      match.companyName.contains(searchQuery, ignoreCase = true)

    val matchesScore = match.fitScore >= minFitFilter

    matchesCompany && matchesRole && matchesScore
  }

  val highFitCount = matches.count { it.fitScore >= 85 }
  val savedCount = matches.count { it.isSavedToApplicationDb }

  LazyColumn(
    modifier = modifier
      .fillMaxWidth()
      .testTag("smart_job_search_view"),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // 1. HERO HEADER BANNER
    item {
      Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SlateCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, TitanCyan.copy(alpha = 0.35f)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(36.dp)
                  .clip(CircleShape)
                  .background(TitanCyan.copy(alpha = 0.15f))
                  .border(1.dp, TitanCyan.copy(alpha = 0.4f), CircleShape),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.AutoAwesome,
                  contentDescription = null,
                  tint = TitanCyan,
                  modifier = Modifier.size(20.dp)
                )
              }
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text(
                  text = "SMART JOB SEARCH",
                  style = MaterialTheme.typography.titleMedium,
                  fontWeight = FontWeight.Bold,
                  color = TextPrimaryDark
                )
                Text(
                  text = "Gemini Autonomous Target Company Crawler",
                  style = MaterialTheme.typography.bodySmall,
                  color = TitanCyan,
                  fontSize = 12.sp
                )
              }
            }

            // Badges
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(6.dp))
                  .background(TitanIndigo.copy(alpha = 0.2f))
                  .border(1.dp, TitanIndigo.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                  .padding(horizontal = 6.dp, vertical = 2.dp)
              ) {
                Text(
                  text = "GEMINI 2.5",
                  style = MaterialTheme.typography.labelSmall,
                  color = TitanIndigo,
                  fontSize = 9.sp,
                  fontWeight = FontWeight.Bold
                )
              }
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(6.dp))
                  .background(TitanEmerald.copy(alpha = 0.15f))
                  .border(1.dp, TitanEmerald.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                  .padding(horizontal = 6.dp, vertical = 2.dp)
              ) {
                Text(
                  text = "ROOM DB PERSISTENCE",
                  style = MaterialTheme.typography.labelSmall,
                  color = TitanEmerald,
                  fontSize = 9.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          Text(
            text = "Crawls job listing summaries across ${targetCompanies.size} target companies (Zepto, Swiggy, Google, Microsoft, etc.), evaluates alignment against candidate verified profile facts, and stores high-fit matches directly into your Job Application database.",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondaryDark,
            lineHeight = 18.sp
          )
        }
      }
    }

    // 2. USER FEEDBACK / SUCCESS SNACKBAR
    if (savedFeedback != null) {
      item {
        Card(
          shape = RoundedCornerShape(8.dp),
          colors = CardDefaults.cardColors(containerColor = TitanEmerald.copy(alpha = 0.15f)),
          border = androidx.compose.foundation.BorderStroke(1.dp, TitanEmerald.copy(alpha = 0.6f)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
              Icon(
                imageVector = Icons.Default.CloudDone,
                contentDescription = null,
                tint = TitanEmerald,
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = savedFeedback ?: "",
                style = MaterialTheme.typography.bodySmall,
                color = TextPrimaryDark,
                fontWeight = FontWeight.Medium
              )
            }
            IconButton(
              onClick = { viewModel.clearSmartJobSearchFeedback() },
              modifier = Modifier.size(24.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Dismiss",
                tint = TextMutedDark,
                modifier = Modifier.size(14.dp)
              )
            }
          }
        }
      }
    }

    // 3. TARGET COMPANIES CHIP BAR
    item {
      Column {
        Text(
          text = "FILTER TARGET COMPANIES (${targetCompanies.size})",
          style = MaterialTheme.typography.labelSmall,
          color = TitanCyan,
          fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(6.dp))

        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          // "ALL" chip
          val isAllSelected = selectedCompanyFilter == "ALL"
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .background(if (isAllSelected) TitanCyan.copy(alpha = 0.2f) else SlateCard)
              .border(
                1.dp,
                if (isAllSelected) TitanCyan else SlateBorder,
                RoundedCornerShape(8.dp)
              )
              .clickable { viewModel.smartJobSearchSelectedCompany.value = "ALL" }
              .padding(horizontal = 12.dp, vertical = 6.dp)
              .testTag("filter_company_all")
          ) {
            Text(
              text = "All Companies (${targetCompanies.size})",
              style = MaterialTheme.typography.labelMedium,
              color = if (isAllSelected) TitanCyan else TextSecondaryDark,
              fontWeight = if (isAllSelected) FontWeight.Bold else FontWeight.Normal
            )
          }

          // Individual company chips
          targetCompanies.forEach { company ->
            val isSelected = selectedCompanyFilter == company.id || selectedCompanyFilter == company.name
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(if (isSelected) TitanCyan.copy(alpha = 0.2f) else SlateCard)
                .border(
                  1.dp,
                  if (isSelected) TitanCyan else SlateBorder,
                  RoundedCornerShape(8.dp)
                )
                .clickable { viewModel.smartJobSearchSelectedCompany.value = company.id }
                .padding(horizontal = 12.dp, vertical = 6.dp)
                .testTag("filter_company_${company.id}")
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = company.logoEmoji,
                  fontSize = 12.sp
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = company.name,
                  style = MaterialTheme.typography.labelMedium,
                  color = if (isSelected) TitanCyan else TextSecondaryDark,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
              }
            }
          }
        }
      }
    }

    // 4. SEARCH QUERY & TRIGGER BAR
    item {
      Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = SlateCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.smartJobSearchQuery.value = it },
            placeholder = { Text("Filter by role keyword (e.g. Operations, Strategy, SQL)...", color = TextMutedDark, fontSize = 13.sp) },
            leadingIcon = {
              Icon(Icons.Default.Search, contentDescription = null, tint = TitanCyan, modifier = Modifier.size(18.dp))
            },
            trailingIcon = {
              if (searchQuery.isNotBlank()) {
                IconButton(onClick = { viewModel.smartJobSearchQuery.value = "" }) {
                  Icon(Icons.Default.Close, contentDescription = "Clear", tint = TextMutedDark, modifier = Modifier.size(16.dp))
                }
              }
            },
            modifier = Modifier
              .fillMaxWidth()
              .testTag("smart_job_search_input"),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = TitanCyan,
              unfocusedBorderColor = SlateBorder,
              focusedTextColor = TextPrimaryDark,
              unfocusedTextColor = TextPrimaryDark,
              focusedContainerColor = ObsidianDark,
              unfocusedContainerColor = ObsidianDark
            ),
            shape = RoundedCornerShape(8.dp),
            singleLine = true
          )

          Spacer(modifier = Modifier.height(10.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            // Min Fit Filter Chips
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "Min Fit:",
                style = MaterialTheme.typography.labelSmall,
                color = TextMutedDark
              )
              listOf(70 to "70%+", 80 to "80%+", 90 to "90%+").forEach { (threshold, label) ->
                val active = minFitFilter == threshold
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (active) TitanEmerald.copy(alpha = 0.2f) else ObsidianDark)
                    .border(1.dp, if (active) TitanEmerald else SlateBorder, RoundedCornerShape(6.dp))
                    .clickable { viewModel.smartJobSearchMinFitFilter.value = threshold }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                  Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (active) TitanEmerald else TextSecondaryDark,
                    fontWeight = if (active) FontWeight.Bold else FontWeight.Normal,
                    fontSize = 11.sp
                  )
                }
              }
            }

            // Trigger Button
            Button(
              onClick = {
                viewModel.runSmartJobSearch()
              },
              enabled = !isSearching,
              colors = ButtonDefaults.buttonColors(
                containerColor = TitanCyan,
                contentColor = ObsidianDark
              ),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.testTag("crawl_target_companies_button")
            ) {
              if (isSearching) {
                CircularProgressIndicator(
                  modifier = Modifier.size(14.dp),
                  color = ObsidianDark,
                  strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Crawling...", fontWeight = FontWeight.Bold, fontSize = 12.sp)
              } else {
                Icon(
                  imageVector = Icons.Default.AutoAwesome,
                  contentDescription = null,
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Crawl with Gemini", fontWeight = FontWeight.Bold, fontSize = 12.sp)
              }
            }
          }
        }
      }
    }

    // 5. CRAWLER PROGRESS HUD (When searching)
    if (isSearching) {
      item {
        Card(
          shape = RoundedCornerShape(10.dp),
          colors = CardDefaults.cardColors(containerColor = SlateElevated),
          border = androidx.compose.foundation.BorderStroke(1.dp, TitanCyan.copy(alpha = 0.5f)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            CircularProgressIndicator(
              modifier = Modifier.size(24.dp),
              color = TitanCyan,
              strokeWidth = 2.5.dp
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(
                text = "GEMINI CRAWLER IN PROGRESS",
                style = MaterialTheme.typography.labelSmall,
                color = TitanCyan,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = crawlerStatus.ifBlank { "Scanning open requisition summaries across target companies..." },
                style = MaterialTheme.typography.bodySmall,
                color = TextPrimaryDark,
                fontSize = 12.sp
              )
            }
          }
        }
      }
    }

    // 6. METRICS & BATCH ACTIONS ROW
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "MATCHED ROLES (${filteredMatches.size})",
            style = MaterialTheme.typography.labelSmall,
            color = TitanEmerald,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = "$highFitCount Top Alignment (>= 85%) • $savedCount Saved in DB",
            style = MaterialTheme.typography.bodySmall,
            color = TextMutedDark,
            fontSize = 11.sp
          )
        }

        if (filteredMatches.any { !it.isSavedToApplicationDb && it.fitScore >= 85 }) {
          Button(
            onClick = { viewModel.saveAllTopMatchesToApplications(85) },
            colors = ButtonDefaults.buttonColors(
              containerColor = TitanEmerald.copy(alpha = 0.18f),
              contentColor = TitanEmerald
            ),
            border = androidx.compose.foundation.BorderStroke(1.dp, TitanEmerald.copy(alpha = 0.5f)),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.testTag("batch_save_top_matches_button")
          ) {
            Icon(Icons.Default.BookmarkAdded, contentDescription = null, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Batch Save Top Matches", fontSize = 11.sp, fontWeight = FontWeight.Bold)
          }
        }
      }
    }

    // 7. SCAN SUMMARY BANNER
    if (scanSummary.isNotBlank()) {
      item {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(SlateCard)
            .border(1.dp, SlateBorder, RoundedCornerShape(8.dp))
            .padding(12.dp)
        ) {
          Row(verticalAlignment = Alignment.Top) {
            Icon(
              imageVector = Icons.Default.Radar,
              contentDescription = null,
              tint = TitanCyan,
              modifier = Modifier
                .size(16.dp)
                .padding(top = 2.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = scanSummary,
              style = MaterialTheme.typography.bodySmall,
              color = TextSecondaryDark,
              fontSize = 12.sp,
              lineHeight = 17.sp
            )
          }
        }
      }
    }

    // 8. MATCH CARDS
    if (filteredMatches.isEmpty() && !isSearching) {
      item {
        Card(
          shape = RoundedCornerShape(10.dp),
          colors = CardDefaults.cardColors(containerColor = SlateCard),
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Icon(
              imageVector = Icons.Default.Work,
              contentDescription = null,
              tint = TextMutedDark,
              modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
              text = "No job matches found matching current filters",
              style = MaterialTheme.typography.titleSmall,
              color = TextPrimaryDark,
              fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = "Try clearing the role filter or click 'Crawl with Gemini' to scan all target companies.",
              style = MaterialTheme.typography.bodySmall,
              color = TextMutedDark,
              fontSize = 12.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(
              onClick = {
                viewModel.smartJobSearchQuery.value = ""
                viewModel.smartJobSearchSelectedCompany.value = "ALL"
                viewModel.smartJobSearchMinFitFilter.value = 70
                viewModel.runSmartJobSearch()
              },
              colors = ButtonDefaults.buttonColors(containerColor = TitanCyan, contentColor = ObsidianDark),
              shape = RoundedCornerShape(8.dp)
            ) {
              Text("Reset & Crawl All Companies", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
          }
        }
      }
    } else {
      items(filteredMatches, key = { it.id }) { match ->
        SmartJobMatchCard(
          match = match,
          onSaveToDatabase = {
            viewModel.saveSmartJobMatchToApplicationDatabase(match, "SHORTLISTED")
          },
          onNavigateToPipeline = onNavigateToPipeline,
          onPrepareApplication = {
            // Find or construct job and prepare application
            val job = viewModel.jobs.value.find { it.id == match.jobId } ?: com.example.data.model.Job(
              id = match.jobId,
              companyId = match.companyId,
              companyName = match.companyName,
              title = match.roleTitle,
              roleFamily = match.roleFamily,
              department = "Operations & Strategy",
              location = match.location,
              city = "Bengaluru",
              country = "India",
              remoteStatus = "HYBRID",
              employmentType = "FULL_TIME",
              salaryRange = match.salaryRange,
              currency = "INR",
              experienceRequirement = match.experienceRequirement,
              adiFitScore = match.fitScore,
              opportunityScore = match.fitScore,
              whyItFits = match.whyItFits,
              whyItDoesntFit = "",
              missingRequirements = "",
              recommendedAction = "Apply",
              applicationUrl = match.applicationUrl,
              officialSource = "${match.companyName} Careers",
              postedDate = "Recently",
              isFresherFriendly = true,
              strategicPriority = "APPLY_NOW"
            )
            viewModel.prepareApplication(job)
          }
        )
      }
    }

    item {
      Spacer(modifier = Modifier.height(40.dp))
    }
  }
}

@Composable
fun SmartJobMatchCard(
  match: SmartJobMatch,
  onSaveToDatabase: () -> Unit,
  onNavigateToPipeline: () -> Unit,
  onPrepareApplication: () -> Unit,
  modifier: Modifier = Modifier
) {
  var isExpanded by remember { mutableStateOf(false) }

  val scoreColor = when {
    match.fitScore >= 90 -> TitanEmerald
    match.fitScore >= 80 -> TitanCyan
    else -> TitanGold
  }

  Card(
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = SlateCard),
    border = androidx.compose.foundation.BorderStroke(
      1.dp,
      if (match.isSavedToApplicationDb) TitanEmerald.copy(alpha = 0.5f) else SlateBorder
    ),
    modifier = modifier
      .fillMaxWidth()
      .testTag("smart_job_match_card_${match.id}")
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      // 1. TOP HEADER: FIT SCORE BADGE & VERDICT & SAVED BADGE
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          // Score Pill
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(scoreColor.copy(alpha = 0.15f))
              .border(1.dp, scoreColor.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
              .padding(horizontal = 8.dp, vertical = 4.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Star, contentDescription = null, tint = scoreColor, modifier = Modifier.size(12.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "${match.fitScore}% MATCH",
                style = MaterialTheme.typography.labelSmall,
                color = scoreColor,
                fontWeight = FontWeight.Black,
                fontSize = 11.sp
              )
            }
          }

          // Verdict Tag
          Text(
            text = match.matchVerdict,
            style = MaterialTheme.typography.labelSmall,
            color = if (match.fitScore >= 85) TitanEmerald else TextSecondaryDark,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp
          )
        }

        // DB Status Indicator
        if (match.isSavedToApplicationDb) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(TitanEmerald.copy(alpha = 0.12f))
              .border(1.dp, TitanEmerald.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
              .clickable { onNavigateToPipeline() }
              .padding(horizontal = 8.dp, vertical = 4.dp)
              .testTag("saved_in_app_db_badge_${match.id}")
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                Icons.Default.CheckCircle,
                contentDescription = null,
                tint = TitanEmerald,
                modifier = Modifier.size(12.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "IN APPLICATION DB",
                style = MaterialTheme.typography.labelSmall,
                color = TitanEmerald,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // 2. ROLE TITLE & COMPANY
      Text(
        text = match.roleTitle,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = TextPrimaryDark,
        fontSize = 16.sp
      )

      Spacer(modifier = Modifier.height(2.dp))

      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          Icons.Default.Business,
          contentDescription = null,
          tint = TitanCyan,
          modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
          text = match.companyName,
          style = MaterialTheme.typography.bodyMedium,
          fontWeight = FontWeight.SemiBold,
          color = TitanCyan
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      // 3. KEY ATTRIBUTES CHIPS (Location, Salary, Experience)
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        // Location
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(ObsidianDark)
            .border(1.dp, SlateBorder, RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.LocationOn, contentDescription = null, tint = TextMutedDark, modifier = Modifier.size(12.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = match.location, style = MaterialTheme.typography.labelSmall, color = TextSecondaryDark, fontSize = 11.sp)
          }
        }

        // Salary
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(ObsidianDark)
            .border(1.dp, SlateBorder, RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Payments, contentDescription = null, tint = TitanGold, modifier = Modifier.size(12.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = match.salaryRange, style = MaterialTheme.typography.labelSmall, color = TitanGold, fontSize = 11.sp, fontWeight = FontWeight.Medium)
          }
        }

        // Experience
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(ObsidianDark)
            .border(1.dp, SlateBorder, RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
          Text(text = match.experienceRequirement, style = MaterialTheme.typography.labelSmall, color = TextMutedDark, fontSize = 11.sp)
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // 4. JOB LISTING SUMMARY
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(8.dp))
          .background(ObsidianDark)
          .padding(10.dp)
      ) {
        Column {
          Text(
            text = "CRAWLED JOB MANDATE:",
            style = MaterialTheme.typography.labelSmall,
            color = TextMutedDark,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = match.jobSummary,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondaryDark,
            fontSize = 12.sp,
            lineHeight = 17.sp
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // 5. WHY IT FITS (GEMINI ALIGNMENT RATIONALE)
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(8.dp))
          .background(TitanEmerald.copy(alpha = 0.08f))
          .border(1.dp, TitanEmerald.copy(alpha = 0.25f), RoundedCornerShape(8.dp))
          .padding(10.dp)
      ) {
        Row(verticalAlignment = Alignment.Top) {
          Icon(
            imageVector = Icons.Default.AutoAwesome,
            contentDescription = null,
            tint = TitanEmerald,
            modifier = Modifier
              .size(16.dp)
              .padding(top = 2.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Column {
            Text(
              text = "WHY THIS ROLE ALIGNS WITH YOUR PROFILE:",
              style = MaterialTheme.typography.labelSmall,
              color = TitanEmerald,
              fontWeight = FontWeight.Bold,
              fontSize = 10.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = match.whyItFits,
              style = MaterialTheme.typography.bodySmall,
              color = TextPrimaryDark,
              fontSize = 12.sp,
              lineHeight = 17.sp
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // 6. KEY MATCHING SKILLS
      if (match.keyMatchingSkills.isNotEmpty()) {
        Column {
          Text(
            text = "KEY MATCHING SKILLS & ACHIEVEMENTS",
            style = MaterialTheme.typography.labelSmall,
            color = TextMutedDark,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold
          )
          Spacer(modifier = Modifier.height(4.dp))
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            match.keyMatchingSkills.forEach { skill ->
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(4.dp))
                  .background(TitanCyan.copy(alpha = 0.12f))
                  .border(1.dp, TitanCyan.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                  .padding(horizontal = 6.dp, vertical = 2.dp)
              ) {
                Text(
                  text = skill,
                  style = MaterialTheme.typography.labelSmall,
                  color = TitanCyan,
                  fontSize = 10.sp
                )
              }
            }
          }
        }
      }

      // 7. EXPANDABLE SECTION (Tailored Pitch & Advantage)
      AnimatedVisibility(visible = isExpanded) {
        Column(modifier = Modifier.padding(top = 10.dp)) {
          if (match.strategicAdvantage.isNotBlank()) {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(6.dp))
                .background(TitanGold.copy(alpha = 0.08f))
                .border(1.dp, TitanGold.copy(alpha = 0.25f), RoundedCornerShape(6.dp))
                .padding(8.dp)
            ) {
              Column {
                Text(
                  text = "STRATEGIC UNFAIR ADVANTAGE:",
                  style = MaterialTheme.typography.labelSmall,
                  color = TitanGold,
                  fontWeight = FontWeight.Bold,
                  fontSize = 9.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                  text = match.strategicAdvantage,
                  style = MaterialTheme.typography.bodySmall,
                  color = TextPrimaryDark,
                  fontSize = 11.sp
                )
              }
            }
            Spacer(modifier = Modifier.height(8.dp))
          }

          if (match.tailoredCoverSnippet.isNotBlank()) {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(6.dp))
                .background(ObsidianDark)
                .border(1.dp, SlateBorder, RoundedCornerShape(6.dp))
                .padding(8.dp)
            ) {
              Column {
                Text(
                  text = "RECOMMENDED TAILORED COVER HOOK:",
                  style = MaterialTheme.typography.labelSmall,
                  color = TitanCyan,
                  fontWeight = FontWeight.Bold,
                  fontSize = 9.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                  text = "\"${match.tailoredCoverSnippet}\"",
                  style = MaterialTheme.typography.bodySmall,
                  color = TextSecondaryDark,
                  fontSize = 11.sp,
                  fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // 8. ACTIONS FOOTER ROW
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Toggle details button
        TextButton(
          onClick = { isExpanded = !isExpanded },
          modifier = Modifier.testTag("toggle_details_button_${match.id}")
        ) {
          Text(
            text = if (isExpanded) "Hide Details" else "View Tailored Hook",
            color = TitanCyan,
            fontSize = 11.sp
          )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          if (!match.isSavedToApplicationDb) {
            // DIRECT SAVE TO APPLICATION DATABASE BUTTON
            Button(
              onClick = onSaveToDatabase,
              colors = ButtonDefaults.buttonColors(
                containerColor = TitanCyan,
                contentColor = ObsidianDark
              ),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.testTag("save_to_app_db_button_${match.id}")
            ) {
              Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Save to DB", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
          } else {
            // Already Saved Action -> View in Pipeline CRM
            OutlinedButton(
              onClick = onNavigateToPipeline,
              colors = ButtonDefaults.outlinedButtonColors(contentColor = TitanEmerald),
              border = androidx.compose.foundation.BorderStroke(1.dp, TitanEmerald),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.testTag("view_in_pipeline_button_${match.id}")
            ) {
              Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("In Pipeline CRM", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
          }

          // PREPARE / APPLY
          OutlinedButton(
            onClick = onPrepareApplication,
            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimaryDark),
            border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.testTag("prepare_app_button_${match.id}")
          ) {
            Icon(Icons.Default.Description, contentDescription = null, modifier = Modifier.size(14.dp), tint = TitanCyan)
            Spacer(modifier = Modifier.width(4.dp))
            Text("Prepare", fontSize = 11.sp)
          }
        }
      }
    }
  }
}
