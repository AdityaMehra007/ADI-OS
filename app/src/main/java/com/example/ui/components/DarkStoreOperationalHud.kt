package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.example.ui.theme.TitanCyanGlow
import com.example.ui.theme.TitanEmerald
import com.example.ui.theme.TitanGold
import com.example.ui.theme.TitanIndigo
import com.example.ui.theme.TitanRose
import com.example.ui.theme.TitanViolet
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * Data Model for Dark Store Hub in Bengaluru Urban Distribution Network.
 */
data class DarkStoreHub(
  val id: String,
  val name: String,
  val clusterCode: String,
  val pickSec: Int,
  val packSec: Int,
  val stagingSec: Int,
  val cm2Margin: Float,
  val slaCompliancePct: Float,
  val activePickers: Int,
  val activeRiders: Int,
  val currentQueue: Int,
  val isMonsoonSurgeActive: Boolean = false
) {
  val totalCycleSec: Int get() = pickSec + packSec + stagingSec
}

/**
 * Historical/Live Cycle Time Telemetry Data Point.
 */
data class OperationalCycleTimePoint(
  val timeLabel: String,
  val hourIndex: Int,
  val pickSec: Float,
  val packSec: Float,
  val stagingSec: Float,
  val targetSlaSec: Float = 240f,
  val baselineSec: Float = 380f,
  val activeDrops: Int,
  val cm2PerDrop: Float
) {
  val totalCycleSec: Float get() = pickSec + packSec + stagingSec
}

/**
 * Unit Economics Margin Waterfall Item.
 */
data class MarginWaterfallStep(
  val label: String,
  val code: String,
  val amount: Float,
  val isDeduction: Boolean,
  val isResult: Boolean,
  val breakdownDescription: String
)

enum class HudVisualizerTab(val label: String, val badge: String) {
  CYCLE_TIMES("Cycle Times", "-42% ⚡"),
  CM2_WATERFALL("CM2 Waterfall", "+₹28.40"),
  HUB_MATRIX("14 Hubs Grid", "100% SLA"),
  ALADDIN_STRESS("Risk Simulation", "Factor Beta")
}

enum class AladdinStressMode(val label: String, val multiplierDesc: String) {
  BASELINE("Nominal Operations", "Standard Bengaluru Transit Matrix"),
  FUEL_SURGE_25("Fuel Volatility +25%", "Stress test rider payout elasticity"),
  MONSOON_DELUGE("Monsoon Peak Surge", "Rain surcharge + batch routing algorithm"),
  PEAK_RUSH_10X("Evening Super-Peak", "14-hub max concurrent queue load")
}

/**
 * Sovereign Dark Store Execution HUD:
 * High-precision Bloomberg/Palantir telemetry HUD visualizing:
 * 1. Order-to-Dispatch cycle times (Pick, Pack, Staging vs SLA 240s vs Baseline 380s).
 * 2. Positive CM2 margin waterfall (+₹28.40 per drop).
 * 3. Bengaluru 14 Urban Distribution Hubs real-time operational grid.
 * 4. BlackRock Aladdin Macro Risk Engine stress-testing factor beta.
 */
@Composable
fun DarkStoreOperationalHud(
  modifier: Modifier = Modifier,
  initialExpanded: Boolean = true
) {
  val haptic = LocalHapticFeedback.current
  val coroutineScope = rememberCoroutineScope()
  var isExpanded by remember { mutableStateOf(initialExpanded) }
  var selectedTab by remember { mutableStateOf(HudVisualizerTab.CYCLE_TIMES) }
  var selectedStressMode by remember { mutableStateOf(AladdinStressMode.BASELINE) }
  var selectedHubId by remember { mutableStateOf<String?>("HSR_03") }
  var simulationTick by remember { mutableIntStateOf(0) }
  var isSimulatingPulse by remember { mutableStateOf(false) }

  // Infinite pulsing animation for live telemetry indicator
  val infiniteTransition = rememberInfiniteTransition(label = "hud_telemetry_pulse")
  val pulseAlpha by infiniteTransition.animateFloat(
    initialValue = 0.35f,
    targetValue = 1.0f,
    animationSpec = infiniteRepeatable(
      animation = tween(900, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "pulse_alpha"
  )

  // 14 Bengaluru Distribution Hubs (Ground Truth Dataset)
  val bengaluruHubs = remember(simulationTick, selectedStressMode) {
    val deltaSec = when (selectedStressMode) {
      AladdinStressMode.BASELINE -> 0
      AladdinStressMode.FUEL_SURGE_25 -> 8
      AladdinStressMode.MONSOON_DELUGE -> 18
      AladdinStressMode.PEAK_RUSH_10X -> 24
    }
    val marginDelta = when (selectedStressMode) {
      AladdinStressMode.BASELINE -> 0f
      AladdinStressMode.FUEL_SURGE_25 -> -3.80f
      AladdinStressMode.MONSOON_DELUGE -> 4.20f // Surge pricing covers cost
      AladdinStressMode.PEAK_RUSH_10X -> 6.50f
    }

    listOf(
      DarkStoreHub("HSR_03", "HSR Layout Sector 1", "HUB-03", 62 + deltaSec / 3, 38, 98 + deltaSec / 2, 34.50f + marginDelta, 100.0f, 14, 28, 42),
      DarkStoreHub("KOR_02", "Koramangala 4th Block", "HUB-02", 65 + deltaSec / 3, 39, 104 + deltaSec / 2, 29.80f + marginDelta, 99.8f, 16, 32, 58),
      DarkStoreHub("IND_01", "Indiranagar 100ft Rd", "HUB-01", 66 + deltaSec / 3, 40, 106 + deltaSec / 2, 31.20f + marginDelta, 99.4f, 15, 30, 51),
      DarkStoreHub("MGR_12", "MG Road Central", "HUB-12", 64 + deltaSec / 3, 38, 103 + deltaSec / 2, 32.10f + marginDelta, 99.9f, 12, 24, 38),
      DarkStoreHub("MAL_08", "Malleshwaram 8th Cross", "HUB-08", 67 + deltaSec / 3, 41, 106 + deltaSec / 2, 29.20f + marginDelta, 99.6f, 14, 26, 44),
      DarkStoreHub("JAY_06", "Jayanagar 4th Block", "HUB-06", 68 + deltaSec / 3, 42, 109 + deltaSec / 2, 30.40f + marginDelta, 99.5f, 15, 29, 49),
      DarkStoreHub("JPN_10", "JP Nagar 2nd Phase", "HUB-10", 68 + deltaSec / 3, 41, 108 + deltaSec / 2, 28.50f + marginDelta, 99.1f, 13, 25, 47),
      DarkStoreHub("BEL_05", "Bellandur Tech Zone", "HUB-05", 70 + deltaSec / 3, 42, 109 + deltaSec / 2, 28.90f + marginDelta, 99.2f, 18, 36, 64),
      DarkStoreHub("SAR_14", "Sarjapur Road Signal", "HUB-14", 71 + deltaSec / 3, 42, 111 + deltaSec / 2, 28.20f + marginDelta, 99.0f, 16, 31, 55),
      DarkStoreHub("EVI_07", "Electronic City Phase 1", "HUB-07", 72 + deltaSec / 3, 43, 111 + deltaSec / 2, 26.80f + marginDelta, 98.7f, 17, 34, 61),
      DarkStoreHub("HEB_11", "Hebbal Outer Ring Rd", "HUB-11", 72 + deltaSec / 3, 43, 114 + deltaSec / 2, 27.40f + marginDelta, 98.6f, 14, 28, 48),
      DarkStoreHub("BAN_13", "Bannerghatta IIM Hub", "HUB-13", 73 + deltaSec / 3, 44, 114 + deltaSec / 2, 26.40f + marginDelta, 98.5f, 15, 29, 53),
      DarkStoreHub("WHI_04", "Whitefield ITPL Main", "HUB-04", 74 + deltaSec / 3, 45, 116 + deltaSec / 2, 27.10f + marginDelta, 98.9f, 20, 38, 72),
      DarkStoreHub("MAR_09", "Marathahalli Bridge", "HUB-09", 75 + deltaSec / 3, 46, 117 + deltaSec / 2, 25.90f + marginDelta, 98.2f, 16, 32, 59)
    )
  }

  // Selected Hub Details
  val activeHub = bengaluruHubs.find { it.id == selectedHubId } ?: bengaluruHubs.first()

  // Chronological 8-Slot Telemetry Window (Pick, Pack, Staging vs Target SLA 240s & Historical Baseline 380s)
  val telemetryHistory = remember(activeHub, simulationTick, selectedStressMode) {
    val basePick = activeHub.pickSec.toFloat()
    val basePack = activeHub.packSec.toFloat()
    val baseStaging = activeHub.stagingSec.toFloat()

    listOf(
      OperationalCycleTimePoint("08:00", 0, basePick - 3f, basePack - 2f, baseStaging - 5f, 240f, 380f, 310, activeHub.cm2Margin + 1.2f),
      OperationalCycleTimePoint("10:00", 1, basePick - 1f, basePack - 1f, baseStaging - 2f, 240f, 380f, 480, activeHub.cm2Margin + 0.8f),
      OperationalCycleTimePoint("12:00", 2, basePick + 2f, basePack, baseStaging + 1f, 240f, 380f, 620, activeHub.cm2Margin),
      OperationalCycleTimePoint("14:00", 3, basePick - 2f, basePack - 1f, baseStaging - 3f, 240f, 380f, 410, activeHub.cm2Margin + 1.5f),
      OperationalCycleTimePoint("16:00", 4, basePick + 1f, basePack + 1f, baseStaging + 2f, 240f, 380f, 540, activeHub.cm2Margin + 0.3f),
      OperationalCycleTimePoint("18:00", 5, basePick + 5f, basePack + 2f, baseStaging + 6f, 240f, 380f, 890, activeHub.cm2Margin + 2.1f),
      OperationalCycleTimePoint("20:00", 6, basePick + 7f, basePack + 3f, baseStaging + 8f, 240f, 380f, 1020, activeHub.cm2Margin + 2.8f),
      OperationalCycleTimePoint("22:00", 7, basePick + 1f, basePack, baseStaging - 1f, 240f, 380f, 590, activeHub.cm2Margin + 0.9f)
    )
  }

  // CM2 Unit Economics Waterfall Breakdown (Adi's Bangalore Dark Store Optimization Model)
  val waterfallSteps = remember(activeHub, selectedStressMode) {
    val aov = 420.00f
    val cogs = 268.00f
    val riderBase = when (selectedStressMode) {
      AladdinStressMode.BASELINE -> 48.20f
      AladdinStressMode.FUEL_SURGE_25 -> 58.50f
      AladdinStressMode.MONSOON_DELUGE -> 54.00f
      AladdinStressMode.PEAK_RUSH_10X -> 51.50f
    }
    val opexLease = 38.50f
    val coldPackaging = 14.10f
    val techGateway = 22.80f
    val grossProfit = aov - cogs
    val netCm2 = aov - cogs - riderBase - opexLease - coldPackaging - techGateway

    listOf(
      MarginWaterfallStep("AOV Gross Revenue", "AOV", aov, isDeduction = false, isResult = false, "Average basket checkout value per drop"),
      MarginWaterfallStep("COGS & Procurement", "COGS", -cogs, isDeduction = true, isResult = false, "Direct inventory cost (FMCG, dairy, perishables)"),
      MarginWaterfallStep("Gross Margin (Level 1)", "GM1", grossProfit, isDeduction = false, isResult = true, "Product gross profit before fulfillment fees"),
      MarginWaterfallStep("Last-Mile Rider Payout", "RIDER", -riderBase, isDeduction = true, isResult = false, "Velocity-zoned delivery partner drop fee"),
      MarginWaterfallStep("Hub Lease & Utility OPEX", "LEASE", -opexLease, isDeduction = true, isResult = false, "Dark store rent, electricity, cold-room refrigeration"),
      MarginWaterfallStep("Insulated Packaging / Cold", "PACK", -coldPackaging, isDeduction = true, isResult = false, "Biodegradable bags, ice packs & dry ice barrier"),
      MarginWaterfallStep("Tech Platform & Payment", "TECH", -techGateway, isDeduction = true, isResult = false, "ADI-OS dispatch algorithm, maps API, payment gateway"),
      MarginWaterfallStep("Net Contribution Margin (CM2)", "CM2", netCm2, isDeduction = false, isResult = true, "Positive unit economic cash surplus per order drop")
    )
  }

  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("dark_store_operational_hud_card"),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = SlateCard),
    border = androidx.compose.foundation.BorderStroke(1.5.dp, TitanCyan.copy(alpha = 0.65f))
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
    ) {
      // 1. TOP EXECUTIVE BANNER & REAL-TIME SYSTEM PULSE
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(9.dp)
                .clip(CircleShape)
                .background(TitanEmerald.copy(alpha = pulseAlpha))
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "MODE 3: DARK STORE EXECUTION HUD",
              fontSize = 11.sp,
              fontWeight = FontWeight.Black,
              fontFamily = FontFamily.Monospace,
              color = TitanCyan,
              letterSpacing = 1.2.sp
            )
            Spacer(modifier = Modifier.width(8.dp))
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(TitanEmerald.copy(alpha = 0.15f))
                .border(1.dp, TitanEmerald.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
                .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
              Text(
                text = "14 HUBS ONLINE",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = TitanEmerald
              )
            }
          }

          Spacer(modifier = Modifier.height(3.dp))
          Text(
            text = "Bengaluru Urban Fulfillment Network",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = TextPrimaryDark
          )
          Text(
            text = "Engineered by Adi • Pick <90s | Pack <45s | Staging <100s | Total <240s",
            fontSize = 11.sp,
            color = TextSecondaryDark
          )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          // Simulation Trigger Button
          IconButton(
            onClick = {
              haptic.performHapticFeedback(HapticFeedbackType.LongPress)
              isSimulatingPulse = true
              simulationTick++
              coroutineScope.launch {
                kotlinx.coroutines.delay(600)
                isSimulatingPulse = false
              }
            },
            modifier = Modifier
              .size(36.dp)
              .clip(CircleShape)
              .background(SlateElevated)
              .testTag("simulate_pulse_button")
          ) {
            Icon(
              imageVector = Icons.Default.Refresh,
              contentDescription = "Simulate Telemetry Pulse",
              tint = if (isSimulatingPulse) TitanCyan else TextMutedDark,
              modifier = Modifier.size(18.dp)
            )
          }

          Spacer(modifier = Modifier.width(6.dp))

          IconButton(
            onClick = {
              haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
              isExpanded = !isExpanded
            },
            modifier = Modifier
              .size(36.dp)
              .clip(CircleShape)
              .background(SlateElevated)
              .testTag("hud_toggle_expand_button")
          ) {
            Icon(
              imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
              contentDescription = if (isExpanded) "Collapse HUD" else "Expand HUD",
              tint = TitanCyan,
              modifier = Modifier.size(20.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // 2. MACRO KPIS METRICS STRIP (ALWAYS VISIBLE MONOSPACE NUMBERS)
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(10.dp))
          .background(ObsidianDark)
          .border(1.dp, SlateBorder, RoundedCornerShape(10.dp))
          .padding(vertical = 10.dp, horizontal = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Metric 1: Avg Order-to-Dispatch
        Column(modifier = Modifier.weight(1f)) {
          Text("AVG DISPATCH", fontSize = 8.5.sp, fontFamily = FontFamily.Monospace, color = TextMutedDark, fontWeight = FontWeight.Bold)
          Row(verticalAlignment = Alignment.Bottom) {
            Text(
              text = "${activeHub.totalCycleSec}s",
              fontSize = 17.sp,
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Black,
              color = if (activeHub.totalCycleSec <= 240) TitanEmerald else TitanCrimson
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "-42%",
              fontSize = 10.sp,
              fontFamily = FontFamily.Monospace,
              color = TitanEmerald,
              fontWeight = FontWeight.Bold
            )
          }
          Text("from 380s baseline", fontSize = 8.sp, color = TextMutedDark)
        }

        Box(modifier = Modifier.width(1.dp).height(32.dp).background(SlateBorder))

        // Metric 2: Net CM2 Margin
        Column(modifier = Modifier.weight(1.1f).padding(horizontal = 6.dp)) {
          Text("NET CM2 MARGIN", fontSize = 8.5.sp, fontFamily = FontFamily.Monospace, color = TextMutedDark, fontWeight = FontWeight.Bold)
          Row(verticalAlignment = Alignment.Bottom) {
            Text(
              text = "+₹${String.format("%.2f", activeHub.cm2Margin)}",
              fontSize = 17.sp,
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Black,
              color = TitanCyan
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text("/drop", fontSize = 9.sp, color = TextMutedDark)
          }
          Text("+₹28.40 benchmark", fontSize = 8.sp, color = TitanCyan)
        }

        Box(modifier = Modifier.width(1.dp).height(32.dp).background(SlateBorder))

        // Metric 3: Pick Velocity
        Column(modifier = Modifier.weight(0.9f).padding(horizontal = 6.dp)) {
          Text("PICK VELOCITY", fontSize = 8.5.sp, fontFamily = FontFamily.Monospace, color = TextMutedDark, fontWeight = FontWeight.Bold)
          Row(verticalAlignment = Alignment.Bottom) {
            Text(
              text = "${activeHub.pickSec}s",
              fontSize = 17.sp,
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Black,
              color = TitanEmerald
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text("<90s", fontSize = 9.sp, color = TextMutedDark)
          }
          Text("SLA compliant", fontSize = 8.sp, color = TitanEmerald)
        }

        Box(modifier = Modifier.width(1.dp).height(32.dp).background(SlateBorder))

        // Metric 4: Selected Hub SLA
        Column(modifier = Modifier.weight(0.9f), horizontalAlignment = Alignment.End) {
          Text("SLA ON-TIME", fontSize = 8.5.sp, fontFamily = FontFamily.Monospace, color = TextMutedDark, fontWeight = FontWeight.Bold)
          Text(
            text = "${String.format("%.1f", activeHub.slaCompliancePct)}%",
            fontSize = 17.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Black,
            color = TitanGold
          )
          Text(activeHub.clusterCode, fontSize = 8.sp, fontFamily = FontFamily.Monospace, color = TextMutedDark)
        }
      }

      // EXPANDED SECTION: VISUALIZATION CHARTS & INTERACTIVE MODES
      AnimatedVisibility(
        visible = isExpanded,
        enter = fadeIn(),
        exit = fadeOut()
      ) {
        Column(modifier = Modifier.fillMaxWidth().padding(top = 14.dp)) {
          // 3. TAB SELECTOR PILLS
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            HudVisualizerTab.values().forEach { tab ->
              val isSelected = selectedTab == tab
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(8.dp))
                  .background(if (isSelected) TitanCyan.copy(alpha = 0.2f) else SlateElevated)
                  .border(
                    width = 1.dp,
                    color = if (isSelected) TitanCyan else SlateBorder,
                    shape = RoundedCornerShape(8.dp)
                  )
                  .clickable {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    selectedTab = tab
                  }
                  .padding(horizontal = 12.dp, vertical = 7.dp)
                  .testTag("hud_tab_${tab.name.lowercase()}")
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(
                    text = tab.label,
                    fontSize = 11.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) TitanCyan else TextSecondaryDark
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Box(
                    modifier = Modifier
                      .clip(RoundedCornerShape(4.dp))
                      .background(if (isSelected) TitanCyan.copy(alpha = 0.25f) else ObsidianDark)
                      .padding(horizontal = 5.dp, vertical = 1.dp)
                  ) {
                    Text(
                      text = tab.badge,
                      fontSize = 9.sp,
                      fontFamily = FontFamily.Monospace,
                      fontWeight = FontWeight.Bold,
                      color = if (isSelected) TextPrimaryDark else TextMutedDark
                    )
                  }
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // 4. MAIN DISPLAY ROUTING ACCORDING TO SELECTED TAB
          when (selectedTab) {
            HudVisualizerTab.CYCLE_TIMES -> {
              CycleTimesVisualizerSection(
                telemetryPoints = telemetryHistory,
                activeHub = activeHub,
                onSelectHub = { hubId -> selectedHubId = hubId },
                bengaluruHubs = bengaluruHubs
              )
            }
            HudVisualizerTab.CM2_WATERFALL -> {
              Cm2MarginWaterfallSection(
                waterfallSteps = waterfallSteps,
                activeHub = activeHub,
                selectedStressMode = selectedStressMode
              )
            }
            HudVisualizerTab.HUB_MATRIX -> {
              Bengaluru14HubMatrixSection(
                hubs = bengaluruHubs,
                selectedHubId = selectedHubId,
                onSelectHub = { hubId ->
                  haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                  selectedHubId = hubId
                }
              )
            }
            HudVisualizerTab.ALADDIN_STRESS -> {
              AladdinMacroStressSection(
                currentMode = selectedStressMode,
                onSelectMode = { mode ->
                  haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                  selectedStressMode = mode
                },
                activeHub = activeHub
              )
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // 5. EXECUTIVE AUDIT TRAIL FOOTER & TRUTH-ANCHOR CITATION
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(8.dp))
              .background(SlateElevated.copy(alpha = 0.6f))
              .border(1.dp, SlateBorder.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
              .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
              Icon(
                imageVector = Icons.Default.Bolt,
                contentDescription = null,
                tint = TitanGold,
                modifier = Modifier.size(14.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "Adi Supply Chain Standard: 380s → 223s (-42%) | +₹28.40 CM2 Margin | 14 Dark Stores",
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                color = TextSecondaryDark,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
            }
          }
        }
      }
    }
  }
}

// ============================================================================
// COMPONENT 1: ORDER-TO-DISPATCH CYCLE TIMES CANVAS VISUALIZER
// ============================================================================
@Composable
private fun CycleTimesVisualizerSection(
  telemetryPoints: List<OperationalCycleTimePoint>,
  activeHub: DarkStoreHub,
  selectedHubId: String? = null,
  onSelectHub: (String) -> Unit,
  bengaluruHubs: List<DarkStoreHub>
) {
  val haptic = LocalHapticFeedback.current
  var selectedIndex by remember { mutableIntStateOf(telemetryPoints.size - 1) }
  val activePoint = telemetryPoints.getOrNull(selectedIndex) ?: telemetryPoints.last()

  Column(modifier = Modifier.fillMaxWidth()) {
    // Header for chart with quick stats and legend
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Text(
          text = "ORDER-TO-DISPATCH VELOCITY TRACKER",
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          fontFamily = FontFamily.Monospace,
          color = TitanCyan
        )
        Text(
          text = "${activeHub.name} (${activeHub.clusterCode})",
          fontSize = 12.sp,
          fontWeight = FontWeight.Medium,
          color = TextPrimaryDark
        )
      }

      // Legend Pills
      Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        LegendPill("Pick", TitanEmerald)
        LegendPill("Pack", TitanCyan)
        LegendPill("Staging", TitanIndigo)
        LegendPill("SLA 240s", TitanGold)
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Interactive Canvas Chart (Stacked Stages with SLA Lines)
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(175.dp)
        .clip(RoundedCornerShape(12.dp))
        .background(ObsidianDark)
        .border(1.dp, SlateBorder, RoundedCornerShape(12.dp))
        .pointerInput(telemetryPoints) {
          detectTapGestures { offset ->
            val slotWidth = size.width / telemetryPoints.size
            val index = (offset.x / slotWidth).toInt().coerceIn(0, telemetryPoints.size - 1)
            selectedIndex = index
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
          }
        }
        .pointerInput(telemetryPoints) {
          detectDragGestures { change, _ ->
            val slotWidth = size.width / telemetryPoints.size
            val index = (change.position.x / slotWidth).toInt().coerceIn(0, telemetryPoints.size - 1)
            selectedIndex = index
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
          }
        }
        .testTag("cycle_times_interactive_canvas")
    ) {
      Canvas(modifier = Modifier.fillMaxWidth().height(175.dp)) {
        val width = size.width
        val height = size.height
        val paddingLeft = 36.dp.toPx()
        val paddingRight = 16.dp.toPx()
        val paddingTop = 20.dp.toPx()
        val paddingBottom = 26.dp.toPx()

        val usableWidth = width - paddingLeft - paddingRight
        val usableHeight = height - paddingTop - paddingBottom
        val maxScaleSec = 400f // Scaling ceiling to comfortably display 380s baseline and 223s performance

        // 1. Gridlines & Y-Axis Labels
        val gridLevels = listOf(100f, 200f, 240f, 300f, 380f)
        gridLevels.forEach { sec ->
          val y = paddingTop + usableHeight * (1f - (sec / maxScaleSec))
          drawLine(
            color = if (sec == 240f) TitanGold.copy(alpha = 0.5f) else if (sec == 380f) TitanCrimson.copy(alpha = 0.4f) else SlateBorder.copy(alpha = 0.3f),
            start = Offset(paddingLeft, y),
            end = Offset(width - paddingRight, y),
            strokeWidth = if (sec == 240f || sec == 380f) 1.5.dp.toPx() else 0.8.dp.toPx(),
            pathEffect = if (sec == 240f || sec == 380f) PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f) else null
          )
        }

        // 2. Bar plotting for each telemetry time slot
        val count = telemetryPoints.size
        val barSlot = usableWidth / count
        val barWidth = barSlot * 0.52f

        telemetryPoints.forEachIndexed { i, pt ->
          val centerX = paddingLeft + i * barSlot + barSlot / 2f
          val left = centerX - barWidth / 2f

          val pickH = (pt.pickSec / maxScaleSec) * usableHeight
          val packH = (pt.packSec / maxScaleSec) * usableHeight
          val stagingH = (pt.stagingSec / maxScaleSec) * usableHeight

          var currentY = paddingTop + usableHeight

          // Draw Pick Bar (Bottom)
          val pickTop = currentY - pickH
          drawRoundRect(
            color = TitanEmerald,
            topLeft = Offset(left, pickTop),
            size = Size(barWidth, pickH),
            cornerRadius = CornerRadius(0f, 0f)
          )
          currentY = pickTop

          // Draw Pack Bar (Middle)
          val packTop = currentY - packH
          drawRoundRect(
            color = TitanCyan,
            topLeft = Offset(left, packTop),
            size = Size(barWidth, packH),
            cornerRadius = CornerRadius(0f, 0f)
          )
          currentY = packTop

          // Draw Staging Bar (Top)
          val stagingTop = currentY - stagingH
          drawRoundRect(
            color = TitanIndigo,
            topLeft = Offset(left, stagingTop),
            size = Size(barWidth, stagingH),
            cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
          )

          // Selected Scrubber Highlight
          if (i == selectedIndex) {
            drawRoundRect(
              color = Color.White.copy(alpha = 0.18f),
              topLeft = Offset(centerX - barSlot / 2f + 2f, paddingTop),
              size = Size(barSlot - 4f, usableHeight),
              cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
            )

            // Cursor Indicator Dot on Top of Stack
            drawCircle(
              color = TitanCyan,
              radius = 4.5.dp.toPx(),
              center = Offset(centerX, stagingTop)
            )
            drawCircle(
              color = ObsidianDark,
              radius = 2.dp.toPx(),
              center = Offset(centerX, stagingTop)
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Interactive Inspection Capsule for Selected Point
    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(10.dp),
      colors = CardDefaults.cardColors(containerColor = SlateElevated),
      border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = "INSPECTION WINDOW: ${activePoint.timeLabel}",
              fontSize = 10.sp,
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Bold,
              color = TitanCyan
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "(${activePoint.activeDrops} orders processed)",
              fontSize = 9.sp,
              color = TextMutedDark
            )
          }
          Spacer(modifier = Modifier.height(3.dp))
          Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            StageTelemetryChip("Pick", "${activePoint.pickSec.toInt()}s", TitanEmerald)
            StageTelemetryChip("Pack", "${activePoint.packSec.toInt()}s", TitanCyan)
            StageTelemetryChip("Staging", "${activePoint.stagingSec.toInt()}s", TitanIndigo)
          }
        }

        Column(horizontalAlignment = Alignment.End) {
          Text(
            text = "${activePoint.totalCycleSec.toInt()}s TOTAL",
            fontSize = 16.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Black,
            color = if (activePoint.totalCycleSec <= 240f) TitanEmerald else TitanCrimson
          )
          Text(
            text = if (activePoint.totalCycleSec <= 240f) "PASSED (<240s SLA)" else "SLA BREACH RISK",
            fontSize = 9.sp,
            fontFamily = FontFamily.Monospace,
            color = if (activePoint.totalCycleSec <= 240f) TitanEmerald else TitanCrimson,
            fontWeight = FontWeight.Bold
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Quick Selector to switch active Dark Store Hub
    Text(
      text = "FILTER TELEMETRY BY DARK STORE HUB:",
      fontSize = 9.5.sp,
      fontFamily = FontFamily.Monospace,
      color = TextMutedDark,
      fontWeight = FontWeight.Bold
    )
    Spacer(modifier = Modifier.height(6.dp))
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .horizontalScroll(rememberScrollState()),
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      bengaluruHubs.take(6).forEach { hub ->
        val isSelected = hub.id == activeHub.id
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(if (isSelected) TitanCyan.copy(alpha = 0.2f) else SlateElevated)
            .border(1.dp, if (isSelected) TitanCyan else SlateBorder, RoundedCornerShape(6.dp))
            .clickable { onSelectHub(hub.id) }
            .padding(horizontal = 8.dp, vertical = 5.dp)
        ) {
          Text(
            text = hub.clusterCode,
            fontSize = 10.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            color = if (isSelected) TitanCyan else TextSecondaryDark
          )
        }
      }
    }
  }
}

// ============================================================================
// COMPONENT 2: CM2 UNIT ECONOMICS MARGIN WATERFALL VISUALIZER
// ============================================================================
@Composable
private fun Cm2MarginWaterfallSection(
  waterfallSteps: List<MarginWaterfallStep>,
  activeHub: DarkStoreHub,
  selectedStressMode: AladdinStressMode
) {
  val haptic = LocalHapticFeedback.current
  var selectedStep by remember { mutableStateOf<MarginWaterfallStep?>(waterfallSteps.last()) }

  Column(modifier = Modifier.fillMaxWidth()) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Text(
          text = "CM2 UNIT ECONOMICS MARGIN WATERFALL",
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          fontFamily = FontFamily.Monospace,
          color = TitanCyan
        )
        Text(
          text = "Net Cash Surplus per Drop // AOV = ₹420.00",
          fontSize = 11.sp,
          color = TextSecondaryDark
        )
      }

      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(6.dp))
          .background(TitanEmerald.copy(alpha = 0.15f))
          .border(1.dp, TitanEmerald.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
          .padding(horizontal = 8.dp, vertical = 4.dp)
      ) {
        Text(
          text = "+₹28.40 NET CM2",
          fontSize = 11.sp,
          fontFamily = FontFamily.Monospace,
          fontWeight = FontWeight.Black,
          color = TitanEmerald
        )
      }
    }

    Spacer(modifier = Modifier.height(12.dp))

    // Interactive Canvas Waterfall Graphic
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(180.dp)
        .clip(RoundedCornerShape(12.dp))
        .background(ObsidianDark)
        .border(1.dp, SlateBorder, RoundedCornerShape(12.dp))
        .pointerInput(waterfallSteps) {
          detectTapGestures { offset ->
            val slotWidth = size.width / waterfallSteps.size
            val index = (offset.x / slotWidth).toInt().coerceIn(0, waterfallSteps.size - 1)
            selectedStep = waterfallSteps[index]
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
          }
        }
        .testTag("cm2_waterfall_canvas")
    ) {
      Canvas(modifier = Modifier.fillMaxWidth().height(180.dp)) {
        val width = size.width
        val height = size.height
        val paddingLeft = 16.dp.toPx()
        val paddingRight = 16.dp.toPx()
        val paddingTop = 22.dp.toPx()
        val paddingBottom = 24.dp.toPx()

        val usableWidth = width - paddingLeft - paddingRight
        val usableHeight = height - paddingTop - paddingBottom
        val maxVal = 460f // Scaling factor against AOV

        val stepCount = waterfallSteps.size
        val colSlot = usableWidth / stepCount
        val colWidth = colSlot * 0.65f

        // Baseline (Zero) Line
        val zeroY = paddingTop + usableHeight * (1f - (0f / maxVal))
        drawLine(
          color = SlateBorder,
          start = Offset(paddingLeft, zeroY),
          end = Offset(width - paddingRight, zeroY),
          strokeWidth = 1.dp.toPx()
        )

        var runningTotal = 0f
        var prevConnectingY = paddingTop + usableHeight * (1f - (0f / maxVal))

        waterfallSteps.forEachIndexed { i, step ->
          val centerX = paddingLeft + i * colSlot + colSlot / 2f
          val left = centerX - colWidth / 2f

          val startVal = if (step.isResult) 0f else runningTotal
          val endVal = if (step.isResult) step.amount else runningTotal + step.amount

          val topVal = max(startVal, endVal)
          val bottomVal = min(startVal, endVal)

          val topY = paddingTop + usableHeight * (1f - (topVal / maxVal))
          val bottomY = paddingTop + usableHeight * (1f - (bottomVal / maxVal))
          val barH = max(bottomY - topY, 3.dp.toPx())

          val barColor = when {
            step.isResult && step.amount > 0 -> TitanCyan
            step.isDeduction -> TitanCrimson
            else -> TitanEmerald
          }

          // Dotted connector from previous step
          if (i > 0 && !step.isResult) {
            drawLine(
              color = SlateBorder.copy(alpha = 0.6f),
              start = Offset(centerX - colSlot / 2f, prevConnectingY),
              end = Offset(left, prevConnectingY),
              strokeWidth = 1.dp.toPx(),
              pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 4f), 0f)
            )
          }

          // Waterfall Bar
          drawRoundRect(
            color = barColor,
            topLeft = Offset(left, topY),
            size = Size(colWidth, barH),
            cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx())
          )

          // Selected Border Highlight
          if (step.code == selectedStep?.code) {
            drawRoundRect(
              color = Color.White.copy(alpha = 0.35f),
              topLeft = Offset(left - 2.dp.toPx(), topY - 2.dp.toPx()),
              size = Size(colWidth + 4.dp.toPx(), barH + 4.dp.toPx()),
              style = Stroke(width = 1.5.dp.toPx()),
              cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
            )
          }

          if (!step.isResult) {
            runningTotal += step.amount
            prevConnectingY = paddingTop + usableHeight * (1f - (runningTotal / maxVal))
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Monospace Tabular Step Detail Inspector
    selectedStep?.let { step ->
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = SlateElevated),
        border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
      ) {
        Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(4.dp))
                  .background(if (step.isDeduction) TitanCrimson.copy(alpha = 0.2f) else TitanEmerald.copy(alpha = 0.2f))
                  .padding(horizontal = 6.dp, vertical = 2.dp)
              ) {
                Text(
                  text = step.code,
                  fontSize = 10.sp,
                  fontFamily = FontFamily.Monospace,
                  fontWeight = FontWeight.Bold,
                  color = if (step.isDeduction) TitanCrimson else TitanEmerald
                )
              }
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = step.label,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimaryDark
              )
            }

            Text(
              text = "${if (step.amount > 0) "+" else ""}₹${String.format("%.2f", step.amount)}",
              fontSize = 14.sp,
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Black,
              color = if (step.isDeduction) TitanCrimson else TitanEmerald
            )
          }

          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = step.breakdownDescription,
            fontSize = 11.sp,
            color = TextSecondaryDark,
            lineHeight = 15.sp
          )
        }
      }
    }
  }
}

// ============================================================================
// COMPONENT 3: BENGALURU 14-HUB DISTRIBUTION MATRIX SECTION
// ============================================================================
@Composable
private fun Bengaluru14HubMatrixSection(
  hubs: List<DarkStoreHub>,
  selectedHubId: String?,
  onSelectHub: (String) -> Unit
) {
  Column(modifier = Modifier.fillMaxWidth()) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "14 URBAN DISTRIBUTION HUBS // BENGALURU",
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = FontFamily.Monospace,
        color = TitanCyan
      )
      Text(
        text = "TAP HUB TO INSPECT",
        fontSize = 9.sp,
        fontFamily = FontFamily.Monospace,
        color = TextMutedDark
      )
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Grid of all 14 Dark Store Hubs with live cycle times & CM2 margins
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
      hubs.chunked(2).forEach { rowHubs ->
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          rowHubs.forEach { hub ->
            val isSelected = hub.id == selectedHubId
            Card(
              modifier = Modifier
                .weight(1f)
                .clickable { onSelectHub(hub.id) }
                .testTag("hub_card_${hub.id.lowercase()}"),
              shape = RoundedCornerShape(10.dp),
              colors = CardDefaults.cardColors(containerColor = if (isSelected) SlateElevated else ObsidianDark),
              border = androidx.compose.foundation.BorderStroke(
                width = if (isSelected) 1.5.dp else 1.dp,
                color = if (isSelected) TitanCyan else SlateBorder
              )
            ) {
              Column(modifier = Modifier.fillMaxWidth().padding(10.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(
                    text = hub.clusterCode,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Black,
                    color = if (isSelected) TitanCyan else TextMutedDark
                  )
                  Text(
                    text = "${hub.totalCycleSec}s",
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = if (hub.totalCycleSec <= 240) TitanEmerald else TitanCrimson
                  )
                }

                Spacer(modifier = Modifier.height(2.dp))
                Text(
                  text = hub.name,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = TextPrimaryDark,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(
                    text = "+₹${String.format("%.1f", hub.cm2Margin)} CM2",
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    color = TitanEmerald,
                    fontWeight = FontWeight.Bold
                  )
                  Text(
                    text = "${hub.currentQueue} queued",
                    fontSize = 9.sp,
                    color = TextMutedDark
                  )
                }
              }
            }
          }
          if (rowHubs.size == 1) {
            Spacer(modifier = Modifier.weight(1f))
          }
        }
      }
    }
  }
}

// ============================================================================
// COMPONENT 4: ALADDIN MACRO STRESS SIMULATOR
// ============================================================================
@Composable
private fun AladdinMacroStressSection(
  currentMode: AladdinStressMode,
  onSelectMode: (AladdinStressMode) -> Unit,
  activeHub: DarkStoreHub
) {
  Column(modifier = Modifier.fillMaxWidth()) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Text(
          text = "BLACKROCK ALADDIN MACRO RISK ENGINE",
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          fontFamily = FontFamily.Monospace,
          color = TitanCyan
        )
        Text(
          text = "Factor Beta Volatility Stress Test // R_p - R_f = β(R_m - R_f) + α",
          fontSize = 10.sp,
          fontFamily = FontFamily.Monospace,
          color = TextSecondaryDark
        )
      }
      Icon(
        imageVector = Icons.Default.Tune,
        contentDescription = null,
        tint = TitanCyan,
        modifier = Modifier.size(18.dp)
      )
    }

    Spacer(modifier = Modifier.height(12.dp))

    // Stress Modes Cards
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
      AladdinStressMode.values().forEach { mode ->
        val isSelected = currentMode == mode
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelectMode(mode) }
            .testTag("aladdin_mode_${mode.name.lowercase()}"),
          shape = RoundedCornerShape(10.dp),
          colors = CardDefaults.cardColors(containerColor = if (isSelected) SlateElevated else ObsidianDark),
          border = androidx.compose.foundation.BorderStroke(
            width = if (isSelected) 1.5.dp else 1.dp,
            color = if (isSelected) TitanGold else SlateBorder
          )
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = mode.label,
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (isSelected) TitanGold else TextPrimaryDark
                )
                if (isSelected) {
                  Spacer(modifier = Modifier.width(6.dp))
                  Box(
                    modifier = Modifier
                      .clip(RoundedCornerShape(4.dp))
                      .background(TitanGold.copy(alpha = 0.2f))
                      .padding(horizontal = 6.dp, vertical = 2.dp)
                  ) {
                    Text(
                      text = "ACTIVE STRESS",
                      fontSize = 8.sp,
                      fontFamily = FontFamily.Monospace,
                      fontWeight = FontWeight.Bold,
                      color = TitanGold
                    )
                  }
                }
              }
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = mode.multiplierDesc,
                fontSize = 11.sp,
                color = TextSecondaryDark
              )
            }

            Column(horizontalAlignment = Alignment.End) {
              Text(
                text = "${activeHub.totalCycleSec}s Cycle",
                fontSize = 13.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = if (activeHub.totalCycleSec <= 240) TitanEmerald else TitanCrimson
              )
              Text(
                text = "+₹${String.format("%.1f", activeHub.cm2Margin)} CM2",
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                color = TitanCyan
              )
            }
          }
        }
      }
    }
  }
}

// ============================================================================
// HELPER UI CHIPS & LEGENDS
// ============================================================================
@Composable
private fun LegendPill(label: String, color: Color) {
  Row(verticalAlignment = Alignment.CenterVertically) {
    Box(
      modifier = Modifier
        .size(7.dp)
        .clip(CircleShape)
        .background(color)
    )
    Spacer(modifier = Modifier.width(4.dp))
    Text(
      text = label,
      fontSize = 9.sp,
      fontFamily = FontFamily.Monospace,
      color = TextMutedDark
    )
  }
}

@Composable
private fun StageTelemetryChip(stage: String, duration: String, color: Color) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier
      .clip(RoundedCornerShape(4.dp))
      .background(color.copy(alpha = 0.15f))
      .padding(horizontal = 6.dp, vertical = 2.dp)
  ) {
    Text(
      text = "$stage: ",
      fontSize = 9.sp,
      color = TextMutedDark,
      fontFamily = FontFamily.Monospace
    )
    Text(
      text = duration,
      fontSize = 10.sp,
      fontWeight = FontWeight.Bold,
      color = color,
      fontFamily = FontFamily.Monospace
    )
  }
}
