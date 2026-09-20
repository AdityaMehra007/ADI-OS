package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.CorporateFare
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.IndustryTrend
import com.example.data.model.MarketSalaryBenchmark
import com.example.service.FirestoreSyncState
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
import com.example.ui.viewmodel.TitanViewModel

@Composable
fun MarketIntelligenceView(
  viewModel: TitanViewModel
) {
  val currentReport by viewModel.currentMarketReport.collectAsState()
  val isAnalyzing by viewModel.isAnalyzingMarket.collectAsState()
  val isSaving by viewModel.isSavingMarketToFirestore.collectAsState()
  val firestoreStatus by viewModel.firestoreMarketStatus.collectAsState()
  val savedReports by viewModel.savedMarketReports.collectAsState()
  val jobTitleQuery by viewModel.marketJobTitleQuery.collectAsState()
  val locationQuery by viewModel.marketLocationQuery.collectAsState()

  var selectedRoleFilter by remember { mutableStateOf("Strategy & Ops") }
  val quickRoles = listOf(
    "Strategy & Operations Lead",
    "Chief of Staff",
    "Fintech BizOps Analyst",
    "Product Operations Manager",
    "AI Operations Associate"
  )

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .testTag("market_intelligence_view"),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // Top Hero Search & Analysis Controls
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SlateCard),
        border = BorderStroke(1.dp, SlateBorder)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.Insights,
                contentDescription = null,
                tint = TitanCyan,
                modifier = Modifier.size(20.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "MARKET INTELLIGENCE & SALARY DATA",
                style = MaterialTheme.typography.labelSmall,
                color = TitanCyan,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
              )
            }

            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(TitanGold.copy(alpha = 0.15f))
                .border(1.dp, TitanGold.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
                .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
              Text(
                text = "GEMINI SEARCH GROUNDED",
                style = MaterialTheme.typography.labelSmall,
                color = TitanGold,
                fontWeight = FontWeight.Black,
                fontSize = 8.sp
              )
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Quick Role Chips
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            quickRoles.take(3).forEach { role ->
              val isSelected = jobTitleQuery.contains(role.take(8), ignoreCase = true)
              Box(
                modifier = Modifier
                  .weight(1f)
                  .clip(RoundedCornerShape(6.dp))
                  .background(if (isSelected) TitanCyan.copy(alpha = 0.2f) else SlateElevated)
                  .border(
                    1.dp,
                    if (isSelected) TitanCyan else SlateBorder,
                    RoundedCornerShape(6.dp)
                  )
                  .clickable {
                    viewModel.marketJobTitleQuery.value = role
                    viewModel.searchAndAnalyzeMarketIntelligence(role)
                  }
                  .padding(vertical = 6.dp, horizontal = 4.dp),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = role.substringBefore(" ").let { if (it.length > 9) it.take(9) else it } + "...",
                  color = if (isSelected) TitanCyan else TextSecondaryDark,
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  maxLines = 1
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Input Row
          OutlinedTextField(
            value = jobTitleQuery,
            onValueChange = { viewModel.marketJobTitleQuery.value = it },
            label = { Text("Target Job Title", fontSize = 11.sp) },
            leadingIcon = {
              Icon(Icons.Default.Search, contentDescription = null, tint = TitanCyan, modifier = Modifier.size(16.dp))
            },
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("market_search_job_title_input"),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = TitanCyan,
              unfocusedBorderColor = SlateBorder,
              focusedContainerColor = SlateElevated,
              unfocusedContainerColor = SlateElevated,
              focusedTextColor = TextPrimaryDark,
              unfocusedTextColor = TextPrimaryDark
            ),
            shape = RoundedCornerShape(8.dp)
          )

          Spacer(modifier = Modifier.height(8.dp))

          OutlinedTextField(
            value = locationQuery,
            onValueChange = { viewModel.marketLocationQuery.value = it },
            label = { Text("Geography / Tech Cluster", fontSize = 11.sp) },
            leadingIcon = {
              Icon(Icons.Default.LocationOn, contentDescription = null, tint = TitanGold, modifier = Modifier.size(16.dp))
            },
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("market_search_location_input"),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = TitanGold,
              unfocusedBorderColor = SlateBorder,
              focusedContainerColor = SlateElevated,
              unfocusedContainerColor = SlateElevated,
              focusedTextColor = TextPrimaryDark,
              unfocusedTextColor = TextPrimaryDark
            ),
            shape = RoundedCornerShape(8.dp)
          )

          Spacer(modifier = Modifier.height(12.dp))

          // Action Buttons: Search with Gemini & Save to Firestore
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Button(
              onClick = {
                viewModel.searchAndAnalyzeMarketIntelligence(jobTitleQuery, locationQuery)
              },
              enabled = !isAnalyzing && jobTitleQuery.isNotBlank(),
              colors = ButtonDefaults.buttonColors(
                containerColor = TitanCyan,
                contentColor = ObsidianDark
              ),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier
                .weight(1f)
                .testTag("market_analyze_button")
            ) {
              if (isAnalyzing) {
                CircularProgressIndicator(
                  color = ObsidianDark,
                  modifier = Modifier.size(16.dp),
                  strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Analyzing...", fontWeight = FontWeight.Bold, fontSize = 12.sp)
              } else {
                Icon(
                  Icons.Default.AutoAwesome,
                  contentDescription = null,
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Analyze Trends", fontWeight = FontWeight.Bold, fontSize = 12.sp)
              }
            }

            if (currentReport != null) {
              Button(
                onClick = { viewModel.saveCurrentMarketReportToFirestore() },
                enabled = !isSaving,
                colors = ButtonDefaults.buttonColors(
                  containerColor = if (currentReport?.isSavedToFirestore == true) TitanEmerald else TitanGold,
                  contentColor = ObsidianDark
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                  .weight(1f)
                  .testTag("market_save_firestore_button")
              ) {
                if (isSaving) {
                  CircularProgressIndicator(
                    color = ObsidianDark,
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp
                  )
                  Spacer(modifier = Modifier.width(4.dp))
                  Text("Saving...", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                } else if (currentReport?.isSavedToFirestore == true) {
                  Icon(Icons.Default.CloudDone, contentDescription = null, modifier = Modifier.size(16.dp))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text("Saved to Firestore", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                } else {
                  Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(16.dp))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text("Save to Firestore", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
              }
            }
          }

          if (firestoreStatus.isNotBlank()) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = firestoreStatus,
              style = MaterialTheme.typography.labelSmall,
              color = TitanEmerald,
              fontSize = 10.sp
            )
          }
        }
      }
    }

    // Current Report Details
    currentReport?.let { report ->
      // Executive Summary
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = SlateCard),
          border = BorderStroke(1.dp, SlateBorder)
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "EXECUTIVE MARKET DIAGNOSIS",
                style = MaterialTheme.typography.labelSmall,
                color = TitanGold,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp
              )
              Text(
                text = report.timestampFormatted,
                style = MaterialTheme.typography.labelSmall,
                color = TextMutedDark,
                fontSize = 9.sp
              )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
              text = "${report.jobTitle} (${report.location})",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = TextPrimaryDark
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
              text = report.executiveSummary,
              style = MaterialTheme.typography.bodySmall,
              color = TextSecondaryDark,
              lineHeight = 18.sp
            )
          }
        }
      }

      // Salary Benchmarks Percentiles
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = SlateCard),
          border = BorderStroke(1.dp, SlateBorder)
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  Icons.Default.MonetizationOn,
                  contentDescription = null,
                  tint = TitanEmerald,
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "COMPENSATION BENCHMARKS (LPA)",
                  style = MaterialTheme.typography.labelSmall,
                  color = TitanEmerald,
                  fontWeight = FontWeight.Bold
                )
              }
              Text("₹ INR Annualized", style = MaterialTheme.typography.labelSmall, color = TextMutedDark, fontSize = 9.sp)
            }

            Spacer(modifier = Modifier.height(10.dp))

            report.salaryBenchmarks.forEach { bench ->
              SalaryBenchmarkRow(bench = bench)
              Spacer(modifier = Modifier.height(8.dp))
            }
          }
        }
      }

      // Industry Trends & Hiring Signals
      item {
        Column {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.AutoMirrored.Filled.TrendingUp, contentDescription = null, tint = TitanCyan, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "INDUSTRY HIRING TRENDS & SHIFTS",
                style = MaterialTheme.typography.labelSmall,
                color = TitanCyan,
                fontWeight = FontWeight.Bold
              )
            }
            Text("${report.industryTrends.size} Signals", style = MaterialTheme.typography.labelSmall, color = TextMutedDark, fontSize = 9.sp)
          }

          Spacer(modifier = Modifier.height(8.dp))

          report.industryTrends.forEach { trend ->
            IndustryTrendCard(trend = trend)
            Spacer(modifier = Modifier.height(8.dp))
          }
        }
      }

      // Top In-Demand Skills
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = SlateCard),
          border = BorderStroke(1.dp, SlateBorder)
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Text(
              text = "PREMIUM COMPENSATION SKILL DRIVERS:",
              style = MaterialTheme.typography.labelSmall,
              color = TitanGold,
              fontWeight = FontWeight.Bold,
              fontSize = 10.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
              report.topInDemandSkills.forEach { skill ->
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(SlateElevated)
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Box(
                    modifier = Modifier
                      .size(6.dp)
                      .clip(RoundedCornerShape(3.dp))
                      .background(TitanCyan)
                  )
                  Spacer(modifier = Modifier.width(8.dp))
                  Text(
                    text = skill,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextPrimaryDark,
                    fontWeight = FontWeight.Medium
                  )
                }
              }
            }
          }
        }
      }

      // Negotiation Playbook
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = SlateCard),
          border = BorderStroke(1.dp, TitanIndigo.copy(alpha = 0.5f))
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Text(
              text = "TACTICAL NEGOTIATION PLAYBOOK:",
              style = MaterialTheme.typography.labelSmall,
              color = TitanIndigo,
              fontWeight = FontWeight.Bold,
              fontSize = 10.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            report.negotiationTips.forEachIndexed { idx, tip ->
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(vertical = 3.dp),
                verticalAlignment = Alignment.Top
              ) {
                Text(
                  text = "${idx + 1}.",
                  color = TitanGold,
                  fontWeight = FontWeight.Bold,
                  fontSize = 11.sp,
                  modifier = Modifier.width(20.dp)
                )
                Text(
                  text = tip,
                  style = MaterialTheme.typography.bodySmall,
                  color = TextSecondaryDark,
                  lineHeight = 17.sp
                )
              }
            }
          }
        }
      }
    }

    // Saved Market Intelligence Vault History
    if (savedReports.isNotEmpty()) {
      item {
        Column {
          Text(
            text = "SAVED MARKET INTELLIGENCE IN FIRESTORE (${savedReports.size}):",
            style = MaterialTheme.typography.labelSmall,
            color = TextMutedDark,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp
          )
          Spacer(modifier = Modifier.height(6.dp))

          savedReports.forEach { saved ->
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(SlateCard)
                .border(1.dp, SlateBorder, RoundedCornerShape(8.dp))
                .clickable {
                  viewModel.currentMarketReport.value = saved
                  viewModel.marketJobTitleQuery.value = saved.jobTitle
                  viewModel.marketLocationQuery.value = saved.location
                }
                .padding(10.dp)
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column {
                  Text(saved.jobTitle, fontWeight = FontWeight.Bold, color = TextPrimaryDark, fontSize = 12.sp)
                  Text(saved.location, color = TextMutedDark, fontSize = 10.sp)
                }
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(TitanEmerald.copy(alpha = 0.15f))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                  Text("Firestore", color = TitanEmerald, fontWeight = FontWeight.Bold, fontSize = 9.sp)
                }
              }
            }
            Spacer(modifier = Modifier.height(6.dp))
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
fun SalaryBenchmarkRow(bench: MarketSalaryBenchmark) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(8.dp))
      .background(SlateElevated)
      .border(1.dp, SlateBorder, RoundedCornerShape(8.dp))
      .padding(10.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = bench.experienceLevel,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Bold,
        color = TextPrimaryDark
      )
      Text(
        text = "Median: ${bench.currencySymbol}${bench.medianLakhs} ${bench.unit}",
        style = MaterialTheme.typography.labelSmall,
        color = TitanEmerald,
        fontWeight = FontWeight.Bold
      )
    }

    Spacer(modifier = Modifier.height(6.dp))

    // Visual percentile bar
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      PercentileBadge("25th", "${bench.currencySymbol}${bench.percentile25Lakhs}L", TextMutedDark)
      PercentileBadge("Median", "${bench.currencySymbol}${bench.medianLakhs}L", TitanEmerald)
      PercentileBadge("75th", "${bench.currencySymbol}${bench.percentile75Lakhs}L", TitanCyan)
      PercentileBadge("90th", "${bench.currencySymbol}${bench.percentile90Lakhs}L", TitanGold)
    }
  }
}

@Composable
private fun PercentileBadge(label: String, value: String, color: Color) {
  Column(horizontalAlignment = Alignment.CenterHorizontally) {
    Text(label, fontSize = 9.sp, color = TextMutedDark)
    Text(value, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = color)
  }
}

@Composable
fun IndustryTrendCard(trend: IndustryTrend) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(10.dp),
    colors = CardDefaults.cardColors(containerColor = SlateCard),
    border = BorderStroke(1.dp, SlateBorder)
  ) {
    Column(modifier = Modifier.padding(12.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = trend.trendTitle,
          style = MaterialTheme.typography.bodyMedium,
          fontWeight = FontWeight.Bold,
          color = TextPrimaryDark,
          modifier = Modifier.weight(1f)
        )

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(TitanEmerald.copy(alpha = 0.15f))
            .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
          Text(
            text = trend.growthPercentage,
            color = TitanEmerald,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp
          )
        }
      }

      Spacer(modifier = Modifier.height(4.dp))

      Text(
        text = trend.impactDescription,
        style = MaterialTheme.typography.bodySmall,
        color = TextSecondaryDark,
        fontSize = 11.sp,
        lineHeight = 16.sp
      )

      if (trend.keyCompaniesHiring.isNotEmpty()) {
        Spacer(modifier = Modifier.height(6.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.CorporateFare, contentDescription = null, tint = TextMutedDark, modifier = Modifier.size(12.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "Actively Hiring: " + trend.keyCompaniesHiring.joinToString(", "),
            style = MaterialTheme.typography.labelSmall,
            color = TitanCyan,
            fontSize = 10.sp
          )
        }
      }
    }
  }
}
