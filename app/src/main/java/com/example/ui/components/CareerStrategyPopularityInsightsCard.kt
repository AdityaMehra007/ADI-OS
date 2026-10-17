package com.example.ui.components

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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CorporateFare
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.analytics.CareerStrategyInsightItem
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
import com.example.ui.theme.TitanRose
import com.example.ui.theme.TitanViolet
import com.example.ui.viewmodel.TitanViewModel

/**
 * Material 3 Card displaying real-time Firebase Analytics Telemetry
 * and popularity rankings across all career strategies based on user interactions
 * with the 25-Keypad Grid and 1-Click Apply Corporate Hub.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CareerStrategyPopularityInsightsCard(
  viewModel: TitanViewModel,
  modifier: Modifier = Modifier,
  initialExpanded: Boolean = true
) {
  val report by viewModel.careerStrategyPopularityReport.collectAsState()
  var isExpanded by remember { mutableStateOf(initialExpanded) }
  var selectedStrategyId by remember { mutableStateOf<String?>(null) }
  var showTelemetryBreakdown by remember { mutableStateOf(false) }

  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("career_strategy_popularity_insights_card")
      .animateContentSize(),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = SlateCard),
    border = BorderStroke(1.2.dp, TitanGold.copy(alpha = 0.5f))
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp)
    ) {
      // -------------------------------------------------------------
      // TOP HEADER: Firebase Analytics Telemetry Badge & Expansion
      // -------------------------------------------------------------
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Box(
            modifier = Modifier
              .size(36.dp)
              .clip(RoundedCornerShape(8.dp))
              .background(TitanGold.copy(alpha = 0.15f))
              .border(1.dp, TitanGold.copy(alpha = 0.4f), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.BarChart,
              contentDescription = "Analytics Telemetry",
              tint = TitanGold,
              modifier = Modifier.size(20.dp)
            )
          }

          Column {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Text(
                text = "FIREBASE ANALYTICS TELEMETRY",
                style = MaterialTheme.typography.labelSmall,
                color = TitanGold,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
              )

              Box(
                modifier = Modifier
                  .size(6.dp)
                  .clip(CircleShape)
                  .background(TitanEmerald)
              )
            }

            Text(
              text = "Popular Career Strategies & Hub Insights",
              style = MaterialTheme.typography.titleMedium,
              color = TextPrimaryDark,
              fontWeight = FontWeight.Bold
            )
          }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          IconButton(
            onClick = {
              viewModel.analyticsService.trackCareerPopularityExplored()
              isExpanded = !isExpanded
            },
            modifier = Modifier.testTag("toggle_strategy_popularity_expand")
          ) {
            Icon(
              imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
              contentDescription = if (isExpanded) "Collapse" else "Expand",
              tint = TextSecondaryDark
            )
          }
        }
      }

      // -------------------------------------------------------------
      // SUMMARY KPI METRICS BAR
      // -------------------------------------------------------------
      Spacer(modifier = Modifier.height(10.dp))

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(10.dp))
          .background(ObsidianDark)
          .border(1.dp, SlateBorder, RoundedCornerShape(10.dp))
          .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // KPI 1: Top Strategy
        Column {
          Text(
            text = "🏆 #1 MOST POPULAR",
            style = MaterialTheme.typography.labelSmall,
            color = TitanGold,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = report.topStrategy?.strategyName?.take(22) ?: "Operations Scaler",
            style = MaterialTheme.typography.bodySmall,
            color = TextPrimaryDark,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        }

        // KPI 2: Keypad Executions
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text(
            text = "25-KEYPAD EXECS",
            style = MaterialTheme.typography.labelSmall,
            color = TitanCyan,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold
          )
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Bolt, contentDescription = null, tint = TitanCyan, modifier = Modifier.size(12.dp))
            Spacer(modifier = Modifier.width(2.dp))
            Text(
              text = "${report.totalKeypadExecutions}",
              style = MaterialTheme.typography.bodySmall,
              color = TitanCyan,
              fontWeight = FontWeight.Bold
            )
          }
        }

        // KPI 3: 1-Click Dispatches
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text(
            text = "1-CLICK DISPATCH",
            style = MaterialTheme.typography.labelSmall,
            color = TitanEmerald,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold
          )
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, tint = TitanEmerald, modifier = Modifier.size(12.dp))
            Spacer(modifier = Modifier.width(2.dp))
            Text(
              text = "${report.totalOneClickDispatches}",
              style = MaterialTheme.typography.bodySmall,
              color = TitanEmerald,
              fontWeight = FontWeight.Bold
            )
          }
        }

        // KPI 4: Transmission Status
        Column(horizontalAlignment = Alignment.End) {
          Text(
            text = "STATUS",
            style = MaterialTheme.typography.labelSmall,
            color = TextMutedDark,
            fontSize = 9.sp
          )
          Text(
            text = "ONLINE",
            style = MaterialTheme.typography.labelSmall,
            color = TitanEmerald,
            fontWeight = FontWeight.Black,
            fontSize = 10.sp
          )
        }
      }

      // -------------------------------------------------------------
      // EXPANDED POPULAR STRATEGIES LIST & INTERACTIVE RANKINGS
      // -------------------------------------------------------------
      AnimatedVisibility(visible = isExpanded) {
        Column(modifier = Modifier.padding(top = 12.dp)) {
          Text(
            text = "CAREER STRATEGY POPULARITY RANKINGS (DERIVED FROM KEYPAD & HUB USAGE)",
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondaryDark,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp
          )

          Spacer(modifier = Modifier.height(8.dp))

          val maxScore = (report.strategies.maxOfOrNull { it.popularityScore } ?: 300).coerceAtLeast(1)

          report.strategies.forEach { strategy ->
            val isSelected = selectedStrategyId == strategy.strategyId
            val progressFraction = (strategy.popularityScore.toFloat() / maxScore.toFloat()).coerceIn(0.1f, 1.0f)

            Surface(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .clickable {
                  selectedStrategyId = if (isSelected) null else strategy.strategyId
                  viewModel.analyticsService.trackCareerStrategySelected(
                    strategyId = strategy.strategyId,
                    strategyName = strategy.strategyName,
                    source = "popularity_insights_card"
                  )
                }
                .testTag("popularity_item_${strategy.strategyId}"),
              shape = RoundedCornerShape(10.dp),
              color = if (isSelected) SlateElevated else ObsidianDark.copy(alpha = 0.8f),
              border = BorderStroke(
                1.dp,
                if (strategy.popularityRank == 1) TitanGold.copy(alpha = 0.6f) else SlateBorder
              )
            ) {
              Column(modifier = Modifier.padding(10.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                  ) {
                    // Rank badge
                    Box(
                      modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(
                          when (strategy.popularityRank) {
                            1 -> TitanGold
                            2 -> TitanCyan
                            3 -> TitanEmerald
                            else -> SlateElevated
                          }
                        ),
                      contentAlignment = Alignment.Center
                    ) {
                      Text(
                        text = "#${strategy.popularityRank}",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (strategy.popularityRank <= 3) ObsidianDark else TextPrimaryDark,
                        fontWeight = FontWeight.Black,
                        fontSize = 10.sp
                      )
                    }

                    Column {
                      Text(
                        text = strategy.strategyName,
                        style = MaterialTheme.typography.titleSmall,
                        color = TextPrimaryDark,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                      )
                      Text(
                        text = strategy.categoryLabel,
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMutedDark,
                        fontSize = 9.sp
                      )
                    }
                  }

                  // Popularity Score & Engagement Pill
                  Column(horizontalAlignment = Alignment.End) {
                    Box(
                      modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                          if (strategy.popularityRank == 1) TitanGold.copy(alpha = 0.15f)
                          else TitanCyan.copy(alpha = 0.1f)
                        )
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                      Text(
                        text = strategy.engagementLevel,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (strategy.popularityRank == 1) TitanGold else TitanCyan,
                        fontWeight = FontWeight.Bold,
                        fontSize = 8.sp
                      )
                    }

                    Text(
                      text = "${strategy.popularityScore} PTS",
                      style = MaterialTheme.typography.labelSmall,
                      color = TextSecondaryDark,
                      fontFamily = FontFamily.Monospace,
                      fontSize = 9.sp
                    )
                  }
                }

                // Relative popularity bar
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                  progress = { progressFraction },
                  modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                  color = when (strategy.popularityRank) {
                    1 -> TitanGold
                    2 -> TitanCyan
                    3 -> TitanEmerald
                    else -> SlateBorder
                  },
                  trackColor = SlateDarker
                )

                // Expanded Strategy Details
                if (isSelected) {
                  Spacer(modifier = Modifier.height(8.dp))
                  Text(
                    text = strategy.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondaryDark,
                    fontSize = 11.sp
                  )

                  Spacer(modifier = Modifier.height(6.dp))
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                  ) {
                    Text(
                      text = "Target Compensation: ${strategy.targetCtcRange}",
                      style = MaterialTheme.typography.labelSmall,
                      color = TitanEmerald,
                      fontWeight = FontWeight.Bold,
                      fontSize = 10.sp
                    )
                    Text(
                      text = "Market Demand: ${strategy.marketDemandPercentile}%",
                      style = MaterialTheme.typography.labelSmall,
                      color = TitanCyan,
                      fontWeight = FontWeight.Bold,
                      fontSize = 10.sp
                    )
                  }

                  // Recommended 25-Keypad Directives
                  Spacer(modifier = Modifier.height(6.dp))
                  Text(
                    text = "Recommended 25-Keypad Directives:",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMutedDark,
                    fontSize = 9.sp
                  )
                  FlowRow(
                    modifier = Modifier.fillMaxWidth().padding(top = 2.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                  ) {
                    strategy.recommendedDirectives.forEach { code ->
                      Box(
                        modifier = Modifier
                          .clip(RoundedCornerShape(4.dp))
                          .background(SlateDarker)
                          .border(0.8.dp, TitanGold.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                          .clickable {
                            viewModel.triggerKeypadDirective(code)
                          }
                          .padding(horizontal = 6.dp, vertical = 2.dp)
                      ) {
                        Text(
                          text = "⚡ Keypad #$code",
                          style = MaterialTheme.typography.labelSmall,
                          color = TitanGold,
                          fontSize = 9.sp,
                          fontWeight = FontWeight.Bold
                        )
                      }
                    }
                  }

                  // Target Corporate Companies
                  Spacer(modifier = Modifier.height(6.dp))
                  Text(
                    text = "Aligned 1-Click Corporate Targets:",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMutedDark,
                    fontSize = 9.sp
                  )
                  FlowRow(
                    modifier = Modifier.fillMaxWidth().padding(top = 2.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                  ) {
                    strategy.primaryTargetCompanies.forEach { company ->
                      Box(
                        modifier = Modifier
                          .clip(RoundedCornerShape(4.dp))
                          .background(SlateDarker)
                          .border(0.8.dp, TitanEmerald.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
                          .padding(horizontal = 6.dp, vertical = 2.dp)
                      ) {
                        Text(
                          text = "🏢 $company",
                          style = MaterialTheme.typography.labelSmall,
                          color = TitanEmerald,
                          fontSize = 9.sp
                        )
                      }
                    }
                  }
                }
              }
            }
          }

          // -------------------------------------------------------------
          // BOTTOM ACTIONS: Open 25-Keypad Grid & Telemetry Details
          // -------------------------------------------------------------
          Spacer(modifier = Modifier.height(10.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            OutlinedButton(
              onClick = {
                viewModel.setKeypadCheatSheetOpen(true)
                viewModel.analyticsService.trackKeypadGridViewed(
                  categoryFilter = "STRATEGY",
                  source = "popularity_insights_card_button"
                )
              },
              shape = RoundedCornerShape(8.dp),
              border = BorderStroke(1.dp, TitanGold.copy(alpha = 0.6f)),
              colors = ButtonDefaults.outlinedButtonColors(contentColor = TitanGold),
              contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
            ) {
              Icon(Icons.Default.Keyboard, contentDescription = null, modifier = Modifier.size(14.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Open 25-Keypad Grid", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }

            TextButton(
              onClick = { showTelemetryBreakdown = !showTelemetryBreakdown }
            ) {
              Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = TitanCyan, modifier = Modifier.size(12.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = if (showTelemetryBreakdown) "Hide Telemetry" else "Firebase Telemetry Logs",
                color = TitanCyan,
                fontSize = 11.sp
              )
            }
          }

          // Telemetry Log Breakdown
          AnimatedVisibility(visible = showTelemetryBreakdown) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(ObsidianDark)
                .border(1.dp, SlateBorder, RoundedCornerShape(8.dp))
                .padding(10.dp)
            ) {
              Text(
                text = "FIREBASE ANALYTICS TELEMETRY DISPATCH LOGS",
                style = MaterialTheme.typography.labelSmall,
                color = TitanCyan,
                fontWeight = FontWeight.Bold,
                fontSize = 9.sp
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "• Event 'keypad_directive_triggered': Transmitted on each 01..25 shortcut execution\n" +
                  "• Event 'one_click_apply_dispatched': Transmitted on 1-click corporate applications\n" +
                  "• Event 'corporate_target_saved': Transmitted on Room SQLite corporate target creation\n" +
                  "• User Property 'clearance_level': 'LEVEL-5 EXECUTIVE CLEARANCE'\n" +
                  "• Backend Region: asia-southeast1 • Status: TRANSMITTING",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondaryDark,
                fontFamily = FontFamily.Monospace,
                fontSize = 10.sp
              )
            }
          }
        }
      }
    }
  }
}
