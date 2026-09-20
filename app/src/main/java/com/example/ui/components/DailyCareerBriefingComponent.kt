package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.ViewAgenda
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DailyCareerBriefingReport
import com.example.data.model.GroundedCompanyBriefing
import com.example.data.model.GroundedSource
import com.example.data.model.HiringShiftDirection
import com.example.data.model.StockTrendDirection
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
 * Daily Career Briefing Dashboard Component
 *
 * Utilizes Google Search Grounding to synthesize:
 * 1. Latest Breaking News
 * 2. Stock & Valuation Trends
 * 3. Bengaluru & India Hiring Shifts
 * for the user's top 5 target companies.
 */
@Composable
fun DailyCareerBriefingComponent(
  viewModel: TitanViewModel,
  modifier: Modifier = Modifier
) {
  val briefingReport by viewModel.dailyCareerBriefing.collectAsState()
  val isRefreshing by viewModel.isRefreshingCareerBriefing.collectAsState()
  val selectedCompanyId by viewModel.selectedBriefingCompanyId.collectAsState()
  val context = LocalContext.current

  // Toggle between focused single-company view and comparative 5-company grid
  var isMatrixView by remember { mutableStateOf(false) }

  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("daily_career_briefing_card"),
    colors = CardDefaults.cardColors(containerColor = SlateCard),
    shape = RoundedCornerShape(16.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
    ) {
      // 1. Header Bar with Grounding Badge & Refresh
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
              .background(
                Brush.linearGradient(
                  colors = listOf(TitanCyan.copy(alpha = 0.2f), TitanIndigo.copy(alpha = 0.25f))
                )
              ),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Newspaper,
              contentDescription = "Daily Career Briefing",
              tint = TitanCyan,
              modifier = Modifier.size(20.dp)
            )
          }

          Column {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Text(
                text = "DAILY CAREER BRIEFING",
                style = MaterialTheme.typography.titleSmall,
                color = TextPrimaryDark,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
              )

              // Grounding Badge
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(4.dp))
                  .background(
                    if (briefingReport?.isLiveGoogleSearchGrounded == true)
                      TitanEmerald.copy(alpha = 0.18f)
                    else
                      TitanCyan.copy(alpha = 0.15f)
                  )
                  .padding(horizontal = 6.dp, vertical = 2.dp)
              ) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                  Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = if (briefingReport?.isLiveGoogleSearchGrounded == true) TitanEmerald else TitanCyan,
                    modifier = Modifier.size(10.dp)
                  )
                  Text(
                    text = if (briefingReport?.isLiveGoogleSearchGrounded == true) "GOOGLE SEARCH GROUNDED" else "RADAR VERIFIED",
                    fontSize = 8.5.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (briefingReport?.isLiveGoogleSearchGrounded == true) TitanEmerald else TitanCyan
                  )
                }
              }
            }

            Text(
              text = briefingReport?.timestampFormatted ?: "Bengaluru Target Enterprise Radar",
              fontSize = 11.sp,
              color = TextSecondaryDark
            )
          }
        }

        // Actions: Toggle View & Refresh
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          IconButton(
            onClick = { isMatrixView = !isMatrixView },
            modifier = Modifier.size(34.dp)
          ) {
            Icon(
              imageVector = if (isMatrixView) Icons.Default.ViewAgenda else Icons.Default.GridView,
              contentDescription = "Toggle View",
              tint = if (isMatrixView) TitanCyan else TextSecondaryDark,
              modifier = Modifier.size(18.dp)
            )
          }

          IconButton(
            onClick = {
              val shareText = buildShareableBriefing(briefingReport)
              val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
              clipboard.setPrimaryClip(ClipData.newPlainText("Daily Career Briefing", shareText))
              Toast.makeText(context, "Executive briefing copied to clipboard", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.size(34.dp)
          ) {
            Icon(
              imageVector = Icons.Default.ContentCopy,
              contentDescription = "Copy Briefing",
              tint = TextSecondaryDark,
              modifier = Modifier.size(18.dp)
            )
          }

          IconButton(
            onClick = { viewModel.refreshDailyCareerBriefing(forceLiveSearch = true) },
            enabled = !isRefreshing,
            modifier = Modifier.size(34.dp)
          ) {
            if (isRefreshing) {
              CircularProgressIndicator(
                modifier = Modifier.size(16.dp),
                strokeWidth = 2.dp,
                color = TitanCyan
              )
            } else {
              Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Refresh Grounded Briefing",
                tint = TitanCyan,
                modifier = Modifier.size(18.dp)
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // 2. Macro Market Pulse Banner
      briefingReport?.let { report ->
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(SlateElevated)
            .border(1.dp, TitanCyan.copy(alpha = 0.2f), RoundedCornerShape(10.dp))
            .padding(12.dp)
        ) {
          Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ShowChart,
              contentDescription = null,
              tint = TitanCyan,
              modifier = Modifier
                .size(18.dp)
                .padding(top = 2.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                Text(
                  text = "MARKET PULSE & MACRO SENTIMENT",
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  color = TitanCyan,
                  letterSpacing = 0.5.sp
                )
                Text(
                  text = "• ${report.overallMarketSentiment}",
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  color = TitanEmerald
                )
              }
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = report.macroMarketSummary,
                fontSize = 12.sp,
                color = TextPrimaryDark,
                lineHeight = 17.sp
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        val companies = report.topFiveCompanies
        if (companies.isNotEmpty()) {
          val activeCompany = companies.find { it.companyId == selectedCompanyId } ?: companies.first()

          // 3. Top 5 Target Company Selector Strip
          Text(
            text = "TOP 5 TARGET ENTERPRISES • LIVE RADAR",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = TextSecondaryDark,
            letterSpacing = 0.8.sp
          )
          Spacer(modifier = Modifier.height(8.dp))

          Row(
            modifier = Modifier
              .fillMaxWidth()
              .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            companies.forEach { company ->
              val isSelected = company.companyId == activeCompany.companyId
              val borderColor by animateColorAsState(
                targetValue = if (isSelected) TitanCyan else SlateBorder,
                animationSpec = tween(200),
                label = "companyPillBorder"
              )
              val bgColor by animateColorAsState(
                targetValue = if (isSelected) SlateElevated else SlateDarkerBg,
                animationSpec = tween(200),
                label = "companyPillBg"
              )

              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(8.dp))
                  .background(bgColor)
                  .border(1.dp, borderColor, RoundedCornerShape(8.dp))
                  .clickable { viewModel.selectBriefingCompany(company.companyId) }
                  .padding(horizontal = 10.dp, vertical = 7.dp)
              ) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                  Text(text = company.logoEmoji, fontSize = 14.sp)
                  Column {
                    Text(
                      text = company.companyName,
                      fontSize = 12.sp,
                      fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                      color = if (isSelected) TextPrimaryDark else TextSecondaryDark
                    )
                    Row(
                      verticalAlignment = Alignment.CenterVertically,
                      horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                      Text(
                        text = company.stockTrendDelta,
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (company.stockTrendDirection.isPositive) TitanEmerald else TitanCrimson
                      )
                      Icon(
                        imageVector = if (company.stockTrendDirection.isPositive) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                        contentDescription = null,
                        tint = if (company.stockTrendDirection.isPositive) TitanEmerald else TitanCrimson,
                        modifier = Modifier.size(10.dp)
                      )
                    }
                  }
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // 4. View Mode Switch: Focused Dossier OR 5-Company Matrix
          if (!isMatrixView) {
            FocusedCompanyBriefingCard(
              company = activeCompany,
              onOpenUrl = { url ->
                try {
                  val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                  context.startActivity(intent)
                } catch (e: Exception) {
                  Toast.makeText(context, "Could not open citation link", Toast.LENGTH_SHORT).show()
                }
              }
            )
          } else {
            ComparativeCompanyMatrix(
              companies = companies,
              onSelectCompany = { id ->
                viewModel.selectBriefingCompany(id)
                isMatrixView = false
              }
            )
          }
        }
      } ?: run {
        // Loading State
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
          contentAlignment = Alignment.Center
        ) {
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            CircularProgressIndicator(color = TitanCyan, strokeWidth = 2.dp)
            Text(
              text = "Grounding top 5 companies via Google Search...",
              fontSize = 12.sp,
              color = TextSecondaryDark
            )
          }
        }
      }
    }
  }
}

/**
 * Focused Deep-Dive Dossier for the selected company
 */
@Composable
private fun FocusedCompanyBriefingCard(
  company: GroundedCompanyBriefing,
  onOpenUrl: (String) -> Unit
) {
  Column(
    modifier = Modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    // 1. Stock & Valuation Tile
    Card(
      modifier = Modifier.fillMaxWidth(),
      colors = CardDefaults.cardColors(containerColor = SlateElevated),
      shape = RoundedCornerShape(12.dp),
      border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
    ) {
      Column(modifier = Modifier.padding(12.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Text(text = company.logoEmoji, fontSize = 20.sp)
            Column {
              Text(
                text = company.companyName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimaryDark
              )
              Text(
                text = "${company.industry} • ${company.tickerSymbol}",
                fontSize = 11.sp,
                color = TextSecondaryDark
              )
            }
          }

          // Price & Movement Tag
          Column(horizontalAlignment = Alignment.End) {
            Text(
              text = company.stockPrice,
              fontSize = 16.sp,
              fontWeight = FontWeight.ExtraBold,
              color = TextPrimaryDark
            )
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(
                  if (company.stockTrendDirection.isPositive) TitanEmerald.copy(alpha = 0.18f) else TitanCrimson.copy(alpha = 0.18f)
                )
                .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
              ) {
                Icon(
                  imageVector = if (company.stockTrendDirection.isPositive) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                  contentDescription = null,
                  tint = if (company.stockTrendDirection.isPositive) TitanEmerald else TitanCrimson,
                  modifier = Modifier.size(11.dp)
                )
                Text(
                  text = "${company.stockTrendDelta} (${company.stockTrendDirection.label})",
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (company.stockTrendDirection.isPositive) TitanEmerald else TitanCrimson
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(color = SlateBorder.copy(alpha = 0.6f))
        Spacer(modifier = Modifier.height(8.dp))

        Row(
          verticalAlignment = Alignment.Top,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.TrendingUp,
            contentDescription = null,
            tint = TitanGold,
            modifier = Modifier
              .size(14.dp)
              .padding(top = 2.dp)
          )
          Text(
            text = company.stockTrendRationale,
            fontSize = 11.5.sp,
            color = TextSecondaryDark,
            lineHeight = 16.sp
          )
        }
      }
    }

    // 2. Latest Breaking News
    Card(
      modifier = Modifier.fillMaxWidth(),
      colors = CardDefaults.cardColors(containerColor = SlateDarkerBg),
      shape = RoundedCornerShape(12.dp),
      border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
    ) {
      Column(modifier = Modifier.padding(12.dp)) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Newspaper,
            contentDescription = null,
            tint = TitanCyan,
            modifier = Modifier.size(14.dp)
          )
          Text(
            text = "VERIFIED BREAKING NEWS",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = TitanCyan,
            letterSpacing = 0.5.sp
          )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
          text = company.newsHeadline,
          fontSize = 13.sp,
          fontWeight = FontWeight.Bold,
          color = TextPrimaryDark,
          lineHeight = 18.sp
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
          text = company.newsSummary,
          fontSize = 11.5.sp,
          color = TextSecondaryDark,
          lineHeight = 16.sp
        )
      }
    }

    // 3. Hiring Shifts & Talent Demand
    Card(
      modifier = Modifier.fillMaxWidth(),
      colors = CardDefaults.cardColors(containerColor = SlateDarkerBg),
      shape = RoundedCornerShape(12.dp),
      border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
    ) {
      Column(modifier = Modifier.padding(12.dp)) {
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
              imageVector = Icons.Default.Work,
              contentDescription = null,
              tint = Color(company.hiringShiftDirection.badgeColorHex),
              modifier = Modifier.size(14.dp)
            )
            Text(
              text = "HIRING SHIFTS & TALENT VELOCITY",
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              color = Color(company.hiringShiftDirection.badgeColorHex),
              letterSpacing = 0.5.sp
            )
          }

          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(4.dp))
              .background(Color(company.hiringShiftDirection.badgeColorHex).copy(alpha = 0.15f))
              .padding(horizontal = 6.dp, vertical = 2.dp)
          ) {
            Text(
              text = company.hiringShiftLabel,
              fontSize = 9.sp,
              fontWeight = FontWeight.ExtraBold,
              color = Color(company.hiringShiftDirection.badgeColorHex)
            )
          }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
          text = company.hiringShiftsSummary,
          fontSize = 11.5.sp,
          color = TextPrimaryDark,
          lineHeight = 16.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
          text = "HIGH-DEMAND ROLES:",
          fontSize = 9.5.sp,
          fontWeight = FontWeight.Bold,
          color = TextSecondaryDark,
          letterSpacing = 0.5.sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          company.keyInDemandRoles.forEach { role ->
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(SlateElevated)
                .border(1.dp, SlateBorder, RoundedCornerShape(4.dp))
                .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
              Text(
                text = role,
                fontSize = 10.5.sp,
                fontWeight = FontWeight.Medium,
                color = TitanCyan
              )
            }
          }
        }
      }
    }

    // 4. Strategic Candidate Angle (Adi's Interview & Outreach Pitch)
    Card(
      modifier = Modifier.fillMaxWidth(),
      colors = CardDefaults.cardColors(containerColor = SlateElevated),
      shape = RoundedCornerShape(12.dp),
      border = androidx.compose.foundation.BorderStroke(1.dp, TitanIndigo.copy(alpha = 0.4f))
    ) {
      Row(
        modifier = Modifier.padding(12.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Box(
          modifier = Modifier
            .size(30.dp)
            .clip(CircleShape)
            .background(TitanIndigo.copy(alpha = 0.2f)),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Psychology,
            contentDescription = null,
            tint = TitanIndigo,
            modifier = Modifier.size(18.dp)
          )
        }

        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = "STRATEGIC INTERVIEW & OUTREACH LEVERAGE",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = TitanIndigo,
            letterSpacing = 0.5.sp
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = company.strategicInterviewAngle,
            fontSize = 11.5.sp,
            color = TextPrimaryDark,
            lineHeight = 16.5.sp
          )
        }
      }
    }

    // 5. Grounded Citations & Sources
    if (company.groundedSources.isNotEmpty()) {
      Column(modifier = Modifier.fillMaxWidth()) {
        Text(
          text = "GROUNDED CITATIONS & WEB SOURCES (${company.groundedSources.size})",
          fontSize = 9.5.sp,
          fontWeight = FontWeight.Bold,
          color = TextSecondaryDark,
          letterSpacing = 0.5.sp
        )
        Spacer(modifier = Modifier.height(6.dp))

        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          company.groundedSources.forEach { source ->
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(SlateDarkerBg)
                .border(1.dp, SlateBorder, RoundedCornerShape(6.dp))
                .clickable { onOpenUrl(source.url) }
                .padding(horizontal = 8.dp, vertical = 5.dp)
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.Search,
                  contentDescription = null,
                  tint = TitanCyan,
                  modifier = Modifier.size(11.dp)
                )
                Text(
                  text = source.title,
                  fontSize = 10.sp,
                  color = TextPrimaryDark,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
                )
                Icon(
                  imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                  contentDescription = "Open Link",
                  tint = TextSecondaryDark,
                  modifier = Modifier.size(10.dp)
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
 * Comparative 5-Company Matrix View (Scannable Grid)
 */
@Composable
private fun ComparativeCompanyMatrix(
  companies: List<GroundedCompanyBriefing>,
  onSelectCompany: (String) -> Unit
) {
  Column(
    modifier = Modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    Text(
      text = "5-TARGET COMPARATIVE MATRIX",
      fontSize = 10.sp,
      fontWeight = FontWeight.Bold,
      color = TitanCyan,
      letterSpacing = 0.5.sp
    )

    companies.forEach { company ->
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .clickable { onSelectCompany(company.companyId) },
        colors = CardDefaults.cardColors(containerColor = SlateDarkerBg),
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Text(text = company.logoEmoji, fontSize = 18.sp)
            Column {
              Text(
                text = company.companyName,
                fontSize = 12.5.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimaryDark
              )
              Text(
                text = company.newsHeadline,
                fontSize = 10.5.sp,
                color = TextSecondaryDark,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
            }
          }

          Column(horizontalAlignment = Alignment.End) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
              Text(
                text = company.stockTrendDelta,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (company.stockTrendDirection.isPositive) TitanEmerald else TitanCrimson
              )
              Icon(
                imageVector = if (company.stockTrendDirection.isPositive) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                contentDescription = null,
                tint = if (company.stockTrendDirection.isPositive) TitanEmerald else TitanCrimson,
                modifier = Modifier.size(11.dp)
              )
            }
            Text(
              text = company.hiringShiftLabel,
              fontSize = 8.5.sp,
              fontWeight = FontWeight.SemiBold,
              color = Color(company.hiringShiftDirection.badgeColorHex)
            )
          }
        }
      }
    }
  }
}

private val SlateDarkerBg = Color(0xFF0D1524)

/**
 * Builds an executive plain text copy of the briefing for easy morning clipboard sharing
 */
private fun buildShareableBriefing(report: DailyCareerBriefingReport?): String {
  if (report == null) return "Daily Career Briefing unavailable."
  val sb = StringBuilder()
  sb.appendLine("═══════════════════════════════════════════")
  sb.appendLine("⚡ TITAN OS • DAILY CAREER BRIEFING")
  sb.appendLine("📅 ${report.dateFormatted} • ${report.timestampFormatted}")
  sb.appendLine("🌐 Grounding: ${report.groundingStatusBadge}")
  sb.appendLine("═══════════════════════════════════════════")
  sb.appendLine()
  sb.appendLine("📊 MACRO MARKET PULSE: ${report.overallMarketSentiment}")
  sb.appendLine(report.macroMarketSummary)
  sb.appendLine()
  sb.appendLine("───────────────────────────────────────────")
  sb.appendLine("🏢 TOP 5 TARGET ENTERPRISES INTELLIGENCE:")
  sb.appendLine("───────────────────────────────────────────")

  report.topFiveCompanies.forEachIndexed { index, comp ->
    sb.appendLine("${index + 1}. ${comp.companyName} (${comp.tickerSymbol})")
    sb.appendLine("   • Stock Trend: ${comp.stockPrice} (${comp.stockTrendDelta}) - ${comp.stockTrendRationale}")
    sb.appendLine("   • Latest News: ${comp.newsHeadline}")
    sb.appendLine("   • Hiring Shift: [${comp.hiringShiftLabel}] ${comp.hiringShiftsSummary}")
    sb.appendLine("   • Key Roles: ${comp.keyInDemandRoles.joinToString(", ")}")
    sb.appendLine("   • Candidate Leverage Pitch: ${comp.strategicInterviewAngle}")
    sb.appendLine()
  }

  sb.appendLine("═══════════════════════════════════════════")
  sb.appendLine("Grounding Engine: Google Gemini 3.5 Flash with Real-Time Google Search Tool")
  return sb.toString()
}
