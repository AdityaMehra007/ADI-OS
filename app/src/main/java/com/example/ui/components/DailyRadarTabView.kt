package com.example.ui.components

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.example.ui.viewmodel.TitanViewModel

/**
 * Daily Radar Tab View.
 * Unifies the Google Search Grounded intelligence feed (covering tech, consulting, fintech,
 * and commerce corporate news, industry trends, and job openings) with the Omni-Corporate
 * role radar and relationship ledger dispatch pipeline.
 */
@Composable
fun DailyRadarTabView(
  viewModel: TitanViewModel,
  modifier: Modifier = Modifier
) {
  var selectedSubMode by remember { mutableStateOf(0) } // 0: Search Grounding Feed, 1: Corporate Roles Radar
  val selectedModalItem by viewModel.selectedRadarItemForModal.collectAsState()
  val feedReport by viewModel.dailyIntelligenceFeedReport.collectAsState()
  val radarItems by viewModel.dailyRadarItems.collectAsState()

  // Auto-switch to Roles Radar if a specific radar item was chosen from Command Center
  LaunchedEffect(selectedModalItem) {
    if (selectedModalItem != null) {
      selectedSubMode = 1
    }
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(ObsidianDark)
      .testTag("daily_radar_view")
  ) {
    // Mode Switcher Header
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 12.dp, vertical = 6.dp)
        .clip(RoundedCornerShape(10.dp))
        .background(SlateCard)
        .border(1.dp, SlateBorder, RoundedCornerShape(10.dp))
        .padding(4.dp)
        .testTag("radar_submode_switcher")
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        // Mode 0: Google Search Grounding Intel Stream
        val isFeedSelected = selectedSubMode == 0
        Row(
          modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isFeedSelected) TitanCyan.copy(alpha = 0.15f) else Color.Transparent)
            .border(
              width = if (isFeedSelected) 1.dp else 0.dp,
              color = if (isFeedSelected) TitanCyan else Color.Transparent,
              shape = RoundedCornerShape(8.dp)
            )
            .clickable { selectedSubMode = 0 }
            .padding(vertical = 8.dp, horizontal = 6.dp)
            .testTag("submode_grounded_intel"),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.Center
        ) {
          Icon(
            imageVector = Icons.Default.Public,
            contentDescription = null,
            tint = if (isFeedSelected) TitanCyan else TextMutedDark,
            modifier = Modifier.size(13.dp)
          )
          Spacer(modifier = Modifier.width(5.dp))
          Text(
            text = "Grounded Intel Feed",
            color = if (isFeedSelected) TitanCyan else TextMutedDark,
            fontSize = 11.sp,
            fontWeight = if (isFeedSelected) FontWeight.Bold else FontWeight.Medium
          )
          val totalFeedCount = feedReport?.feedItems?.size ?: 0
          if (totalFeedCount > 0) {
            Spacer(modifier = Modifier.width(4.dp))
            Box(
              modifier = Modifier
                .clip(CircleShape)
                .background(if (isFeedSelected) TitanCyan.copy(alpha = 0.25f) else SlateDarker)
                .padding(horizontal = 5.dp, vertical = 1.dp)
            ) {
              Text(
                text = "$totalFeedCount",
                color = if (isFeedSelected) TitanCyan else TextSecondaryDark,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
              )
            }
          }
        }

        // Mode 1: Corporate Roles Radar
        val isRadarSelected = selectedSubMode == 1
        Row(
          modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isRadarSelected) TitanGold.copy(alpha = 0.15f) else Color.Transparent)
            .border(
              width = if (isRadarSelected) 1.dp else 0.dp,
              color = if (isRadarSelected) TitanGold else Color.Transparent,
              shape = RoundedCornerShape(8.dp)
            )
            .clickable { selectedSubMode = 1 }
            .padding(vertical = 8.dp, horizontal = 6.dp)
            .testTag("submode_roles_radar"),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.Center
        ) {
          Icon(
            imageVector = Icons.Default.Radar,
            contentDescription = null,
            tint = if (isRadarSelected) TitanGold else TextMutedDark,
            modifier = Modifier.size(13.dp)
          )
          Spacer(modifier = Modifier.width(5.dp))
          Text(
            text = "Corporate Role Radar",
            color = if (isRadarSelected) TitanGold else TextMutedDark,
            fontSize = 11.sp,
            fontWeight = if (isRadarSelected) FontWeight.Bold else FontWeight.Medium
          )
          val freshCount = radarItems.count { it.isNewToday }
          if (freshCount > 0) {
            Spacer(modifier = Modifier.width(4.dp))
            Box(
              modifier = Modifier
                .clip(CircleShape)
                .background(if (isRadarSelected) TitanGold.copy(alpha = 0.25f) else SlateDarker)
                .padding(horizontal = 5.dp, vertical = 1.dp)
            ) {
              Text(
                text = "$freshCount",
                color = if (isRadarSelected) TitanGold else TitanEmerald,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
              )
            }
          }
        }
      }
    }

    // Active Subview
    Box(modifier = Modifier.weight(1f)) {
      if (selectedSubMode == 0) {
        DailyIntelligenceFeedView(
          viewModel = viewModel,
          modifier = Modifier.fillMaxSize()
        )
      } else {
        OmniCorporateDailyRadarFeed(
          viewModel = viewModel,
          modifier = Modifier.fillMaxSize()
        )
      }
    }
  }
}
