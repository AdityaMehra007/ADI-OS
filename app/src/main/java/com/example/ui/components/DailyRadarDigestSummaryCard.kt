package com.example.ui.components

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CorporateSector
import com.example.ui.theme.ObsidianDark
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateCard
import com.example.ui.theme.SlateDarker
import com.example.ui.theme.TextMutedDark
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import com.example.ui.theme.TitanCyan
import com.example.ui.theme.TitanEmerald
import com.example.ui.theme.TitanGold
import com.example.ui.viewmodel.TitanScreen
import com.example.ui.viewmodel.TitanViewModel

/**
 * Executive Command Center Daily Radar Digest Card.
 * Gives executive visibility into daily startup and MNC openings,
 * with 1-click apply dispatch and navigation to the full Daily Radar feed.
 */
@Composable
fun DailyRadarDigestSummaryCard(
  viewModel: TitanViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val radarItems by viewModel.dailyRadarItems.collectAsState()

  val freshRolesCount = remember(radarItems) { radarItems.count { it.isNewToday } }
  val topHighlights = remember(radarItems) { radarItems.filter { it.isNewToday }.take(3) }

  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("card_daily_radar_digest_summary"),
    colors = CardDefaults.cardColors(containerColor = SlateCard),
    border = BorderStroke(1.dp, TitanCyan.copy(alpha = 0.5f)),
    shape = RoundedCornerShape(12.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp)
    ) {
      // Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(24.dp)
              .clip(CircleShape)
              .background(TitanCyan.copy(alpha = 0.15f))
              .border(1.dp, TitanCyan, CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Icon(Icons.Default.Radar, contentDescription = null, tint = TitanCyan, modifier = Modifier.size(14.dp))
          }
          Spacer(modifier = Modifier.width(8.dp))
          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "OMNI-CORPORATE DAILY RADAR",
                color = TextPrimaryDark,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
              )
              Spacer(modifier = Modifier.width(5.dp))
              Box(
                modifier = Modifier
                  .size(6.dp)
                  .clip(CircleShape)
                  .background(TitanEmerald)
              )
            }
            Text(
              text = "$freshRolesCount fresh roles discovered today across Startups & MNCs",
              color = TitanEmerald,
              fontSize = 10.5.sp
            )
          }
        }

        OutlinedButton(
          onClick = {
            viewModel.setCompanyIntelTab(1)
            viewModel.navigateTo(TitanScreen.COMPANIES)
          },
          border = BorderStroke(1.dp, TitanCyan),
          shape = RoundedCornerShape(6.dp),
          modifier = Modifier.testTag("btn_explore_full_radar")
        ) {
          Text("Full Radar", color = TitanCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
          Spacer(modifier = Modifier.width(3.dp))
          Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = TitanCyan, modifier = Modifier.size(11.dp))
        }
      }

      Spacer(modifier = Modifier.height(10.dp))
      HorizontalDivider(color = SlateBorder)
      Spacer(modifier = Modifier.height(10.dp))

      // Top Highlights List
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        topHighlights.forEach { item ->
          val sectorColor = when (item.sector) {
            CorporateSector.TECH_MNC -> TitanGold
            CorporateSector.STARTUP -> TitanCyan
            CorporateSector.UNICORN -> TitanEmerald
            else -> TitanCyan
          }

          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(8.dp))
              .background(SlateDarker)
              .border(0.8.dp, SlateBorder, RoundedCornerShape(8.dp))
              .clickable {
                viewModel.setCompanyIntelTab(1)
                viewModel.setSelectedRadarItemForModal(item)
                viewModel.navigateTo(TitanScreen.COMPANIES)
              }
              .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = item.companyName,
                  color = TextPrimaryDark,
                  fontSize = 12.5.sp,
                  fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(6.dp))
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(3.dp))
                    .background(sectorColor.copy(alpha = 0.15f))
                    .padding(horizontal = 4.dp, vertical = 1.dp)
                ) {
                  Text(
                    text = item.sector.badgeTitle,
                    color = sectorColor,
                    fontSize = 8.5.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                  )
                }
              }
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = item.roleTitle,
                color = TitanCyan,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = "${item.location} • ${item.compensationRange}",
                color = TextMutedDark,
                fontSize = 9.5.sp
              )
            }

            Spacer(modifier = Modifier.width(8.dp))

            if (item.isSavedToLedger) {
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(4.dp))
                  .background(TitanEmerald.copy(alpha = 0.15f))
                  .padding(horizontal = 6.dp, vertical = 4.dp)
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(Icons.Default.CheckCircle, contentDescription = null, tint = TitanEmerald, modifier = Modifier.size(11.dp))
                  Spacer(modifier = Modifier.width(3.dp))
                  Text("Applied", color = TitanEmerald, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
              }
            } else {
              Button(
                onClick = {
                  viewModel.dispatch1ClickApplyForRadarRole(item, context)
                  Toast.makeText(context, "1-Click Applied to ${item.companyName}!", Toast.LENGTH_SHORT).show()
                },
                colors = ButtonDefaults.buttonColors(containerColor = TitanGold, contentColor = ObsidianDark),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.size(height = 30.dp, width = 76.dp)
              ) {
                Icon(Icons.Default.Bolt, contentDescription = null, modifier = Modifier.size(11.dp))
                Spacer(modifier = Modifier.width(2.dp))
                Text("Apply", fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
              }
            }
          }
        }
      }
    }
  }
}
