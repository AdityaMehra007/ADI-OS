package com.example.ui.components

import android.annotation.SuppressLint
import android.content.Context
import android.webkit.JavascriptInterface
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.model.CareerMilestoneStatus
import com.example.data.model.CareerStrategyRoadmap
import com.example.data.model.RoadmapPhase
import com.example.data.model.SkillMilestone
import com.example.service.FirestoreSyncState
import com.example.ui.theme.ObsidianDark
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateCard
import com.example.ui.theme.SlateDarker
import com.example.ui.theme.SlateElevated
import com.example.ui.theme.TextMutedDark
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import com.example.ui.theme.TitanCyan
import com.example.ui.theme.TitanCyanGlow
import com.example.ui.theme.TitanEmerald
import com.example.ui.theme.TitanGold
import com.example.ui.theme.TitanIndigo
import com.example.ui.viewmodel.TitanViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

enum class ChartEngine(val label: String, val description: String) {
  D3_WEB_ENGINE("D3.js Web Engine", "Interactive SVG area trajectory curve rendered with D3.js"),
  RECHARTS_COMPOSE("Recharts-Style Canvas", "Native Jetpack Compose hardware-accelerated Bezier chart")
}

/**
 * High-performance Composable that visualizes a user's career progression and strategic goals
 * using data stored in and synchronized with Firebase Firestore.
 * Supports both an interactive D3.js SVG Web Engine and a Recharts-style native Compose Canvas.
 */
@Composable
fun FirestoreCareerProgressionVisualizer(
  viewModel: TitanViewModel,
  modifier: Modifier = Modifier
) {
  val roadmap by viewModel.careerRoadmap.collectAsState()
  val currentRole by viewModel.currentRoleInput.collectAsState()
  val targetGoals by viewModel.targetCareerGoalsInput.collectAsState()
  val isSavingToFirestore by viewModel.isSavingStrategyToFirestore.collectAsState()
  val firestoreSyncState by viewModel.firestoreStrategySyncState.collectAsState()
  val lastSyncedTimestamp by viewModel.firestoreStrategyLastSyncedTimestamp.collectAsState()
  val firestoreStatusMessage by viewModel.firestoreStrategyStatusMessage.collectAsState()

  var selectedEngine by remember { mutableStateOf(ChartEngine.D3_WEB_ENGINE) }
  var selectedPhaseIndex by remember { mutableIntStateOf(0) }

  Column(
    modifier = modifier
      .fillMaxWidth()
      .testTag("career_progression_visualizer"),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // 1. Firebase Firestore Synchronization & Telemetry Status Card
    FirestoreTelemetryCard(
      syncState = firestoreSyncState,
      statusMessage = firestoreStatusMessage,
      lastSyncedTimestamp = lastSyncedTimestamp,
      isSaving = isSavingToFirestore,
      onSyncToFirestore = { viewModel.saveCareerStrategyToFirestore(silent = false) },
      onLoadFromFirestore = { viewModel.loadCareerStrategyFromFirestore(silent = false) }
    )

    // 2. Executive Career Progression Strip (Current Baseline -> Strategic Goal)
    ExecutiveProgressionSummaryCard(
      currentRole = currentRole,
      targetGoals = targetGoals,
      roadmap = roadmap
    )

    // 3. Engine Switcher (D3.js SVG vs. Recharts Compose Canvas)
    EngineModeSelectorRow(
      selectedEngine = selectedEngine,
      onSelectEngine = { selectedEngine = it }
    )

    // 4. Primary Chart Visualization (D3 or Recharts)
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(16.dp))
        .border(1.dp, SlateBorder, RoundedCornerShape(16.dp))
        .testTag("career_progression_chart_card"),
      colors = CardDefaults.cardColors(containerColor = SlateCard)
    ) {
      Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        // Chart Header
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
                .size(32.dp)
                .background(
                  if (selectedEngine == ChartEngine.D3_WEB_ENGINE) TitanCyan.copy(alpha = 0.15f)
                  else TitanGold.copy(alpha = 0.15f),
                  CircleShape
                ),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = if (selectedEngine == ChartEngine.D3_WEB_ENGINE) Icons.Default.Code else Icons.AutoMirrored.Filled.ShowChart,
                contentDescription = null,
                tint = if (selectedEngine == ChartEngine.D3_WEB_ENGINE) TitanCyan else TitanGold,
                modifier = Modifier.size(18.dp)
              )
            }
            Column {
              Text(
                text = if (selectedEngine == ChartEngine.D3_WEB_ENGINE) "D3.js Career Trajectory" else "Recharts Progression Curve",
                color = TextPrimaryDark,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
              )
              Text(
                text = "Data source: Firestore /users/{uid}/career_strategy/roadmap",
                color = TextMutedDark,
                fontSize = 10.sp
              )
            }
          }

          // Goal Marker Badge
          Surface(
            color = TitanGold.copy(alpha = 0.12f),
            shape = RoundedCornerShape(8.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, TitanGold.copy(alpha = 0.35f))
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              Icon(Icons.Default.Flag, contentDescription = null, tint = TitanGold, modifier = Modifier.size(12.dp))
              Text(
                text = roadmap?.targetCompensation ?: "Target Goal",
                color = TitanGold,
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.sp
              )
            }
          }
        }

        // Active Chart View
        if (selectedEngine == ChartEngine.D3_WEB_ENGINE) {
          D3CareerProgressionWebView(
            roadmap = roadmap,
            currentRole = currentRole,
            targetGoal = targetGoals,
            onPhaseSelected = { selectedPhaseIndex = it },
            modifier = Modifier
              .fillMaxWidth()
              .height(310.dp)
              .clip(RoundedCornerShape(12.dp))
              .border(1.dp, SlateBorder.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
              .testTag("d3_webview_container")
          )
        } else {
          RechartsComposeCareerChart(
            roadmap = roadmap,
            currentRole = currentRole,
            targetGoal = targetGoals,
            onPhaseSelected = { selectedPhaseIndex = it },
            modifier = Modifier
              .fillMaxWidth()
              .height(310.dp)
              .clip(RoundedCornerShape(12.dp))
              .background(SlateDarker)
              .border(1.dp, SlateBorder.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
              .testTag("recharts_compose_canvas_container")
          )
        }

        // Chart Legend & Axis Summary
        ChartLegendStrip()
      }
    }

    // 5. Strategic Goals & Pillars Scorecard (Loaded from Firestore)
    roadmap?.strategicPillars?.let { pillars ->
      if (pillars.isNotEmpty()) {
        StrategicPillarsScorecard(
          pillars = pillars,
          modifier = Modifier.fillMaxWidth()
        )
      }
    }

    // 6. Interactive Milestone Action Checklist (Updates Live & Syncs to Firestore)
    roadmap?.phases?.let { phases ->
      if (phases.isNotEmpty()) {
        StrategicMilestonesPhaseList(
          phases = phases,
          selectedPhaseIndex = selectedPhaseIndex,
          onPhaseSelected = { selectedPhaseIndex = it },
          onToggleMilestone = { milestoneId ->
            viewModel.toggleSkillMilestoneStatus(milestoneId)
          },
          onSyncToFirestore = {
            viewModel.saveCareerStrategyToFirestore(silent = false)
          },
          modifier = Modifier.fillMaxWidth()
        )
      }
    }
  }
}

// -------------------------------------------------------------------------------------
// 1. Firebase Firestore Telemetry & Cloud Connection Bar
// -------------------------------------------------------------------------------------

@Composable
fun FirestoreTelemetryCard(
  syncState: FirestoreSyncState,
  statusMessage: String,
  lastSyncedTimestamp: Long?,
  isSaving: Boolean,
  onSyncToFirestore: () -> Unit,
  onLoadFromFirestore: () -> Unit,
  modifier: Modifier = Modifier
) {
  val syncStatusColor = when (syncState) {
    FirestoreSyncState.SYNCED -> TitanEmerald
    FirestoreSyncState.SYNCING -> TitanCyan
    FirestoreSyncState.FAILED -> Color(0xFFEF4444)
    FirestoreSyncState.IDLE -> TitanGold
  }

  val syncStatusIcon = when (syncState) {
    FirestoreSyncState.SYNCED -> Icons.Default.CloudDone
    FirestoreSyncState.SYNCING -> Icons.Default.CloudSync
    FirestoreSyncState.FAILED -> Icons.Default.CloudOff
    FirestoreSyncState.IDLE -> Icons.Default.CloudQueue
  }

  val formattedTime = remember(lastSyncedTimestamp) {
    if (lastSyncedTimestamp != null && lastSyncedTimestamp > 0) {
      val sdf = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault())
      sdf.format(Date(lastSyncedTimestamp))
    } else {
      "Not synchronized yet"
    }
  }

  Card(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(14.dp))
      .border(1.dp, SlateBorder, RoundedCornerShape(14.dp))
      .testTag("firestore_telemetry_card"),
    colors = CardDefaults.cardColors(containerColor = SlateCard)
  ) {
    Column(
      modifier = Modifier.padding(14.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
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
              .size(28.dp)
              .background(syncStatusColor.copy(alpha = 0.15f), CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = syncStatusIcon,
              contentDescription = null,
              tint = syncStatusColor,
              modifier = Modifier.size(16.dp)
            )
          }

          Column {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Text(
                text = "Firestore Cloud Sync",
                color = TextPrimaryDark,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
              )
              Surface(
                color = syncStatusColor.copy(alpha = 0.15f),
                shape = RoundedCornerShape(4.dp)
              ) {
                Text(
                  text = syncState.name,
                  color = syncStatusColor,
                  fontWeight = FontWeight.Bold,
                  fontSize = 9.sp,
                  modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                )
              }
            }
            Text(
              text = if (isSaving) "Writing career telemetry to Firestore..." else "Last Synced: $formattedTime",
              color = TextMutedDark,
              fontSize = 11.sp
            )
          }
        }

        // Action Buttons: Sync & Fetch
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          OutlinedButton(
            onClick = onLoadFromFirestore,
            enabled = !isSaving,
            modifier = Modifier
              .height(34.dp)
              .testTag("btn_refresh_career_progression_firestore"),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = TitanCyan),
            border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 0.dp),
            shape = RoundedCornerShape(8.dp)
          ) {
            Icon(Icons.Default.CloudDownload, contentDescription = "Fetch Firestore", modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Fetch", fontSize = 11.sp)
          }

          OutlinedButton(
            onClick = onSyncToFirestore,
            enabled = !isSaving,
            modifier = Modifier
              .height(34.dp)
              .testTag("btn_sync_career_progression_firestore"),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = TitanEmerald),
            border = androidx.compose.foundation.BorderStroke(1.dp, TitanEmerald.copy(alpha = 0.5f)),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 0.dp),
            shape = RoundedCornerShape(8.dp)
          ) {
            if (isSaving) {
              CircularProgressIndicator(modifier = Modifier.size(12.dp), color = TitanEmerald, strokeWidth = 2.dp)
            } else {
              Icon(Icons.Default.CloudSync, contentDescription = "Sync Firestore", modifier = Modifier.size(14.dp))
            }
            Spacer(modifier = Modifier.width(4.dp))
            Text("Push", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
          }
        }
      }

      if (statusMessage.isNotEmpty()) {
        Text(
          text = statusMessage,
          color = TextSecondaryDark,
          fontSize = 11.sp,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
      }
    }
  }
}

// -------------------------------------------------------------------------------------
// 2. Executive Progression Summary Card
// -------------------------------------------------------------------------------------

@Composable
fun ExecutiveProgressionSummaryCard(
  currentRole: String,
  targetGoals: String,
  roadmap: CareerStrategyRoadmap?,
  modifier: Modifier = Modifier
) {
  val progress = roadmap?.overallProgressPercentage ?: 0
  val totalMilestones = roadmap?.totalMilestonesCount ?: 0
  val acquiredMilestones = roadmap?.acquiredMilestonesCount ?: 0

  Card(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(14.dp))
      .border(1.dp, SlateBorder, RoundedCornerShape(14.dp))
      .testTag("executive_progression_summary_card"),
    colors = CardDefaults.cardColors(containerColor = SlateCard)
  ) {
    Column(
      modifier = Modifier.padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = "CAREER PROGRESSION TRAJECTORY",
            color = TitanCyan,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = currentRole,
            color = TextSecondaryDark,
            fontSize = 12.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            Icon(Icons.AutoMirrored.Filled.TrendingUp, contentDescription = null, tint = TitanEmerald, modifier = Modifier.size(14.dp))
            Text(
              text = "Goal: $targetGoals",
              color = TextPrimaryDark,
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
          }
        }

        Column(horizontalAlignment = Alignment.End) {
          Text(
            text = "$progress%",
            color = TitanEmerald,
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold
          )
          Text(
            text = "$acquiredMilestones/$totalMilestones Acquired",
            color = TextMutedDark,
            fontSize = 10.sp
          )
        }
      }

      LinearProgressIndicator(
        progress = { (progress / 100f).coerceIn(0f, 1f) },
        modifier = Modifier
          .fillMaxWidth()
          .height(6.dp)
          .clip(RoundedCornerShape(3.dp)),
        color = TitanEmerald,
        trackColor = SlateDarker
      )

      // Trajectory Key Statistics
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        StatisticMiniItem(label = "Horizon", value = roadmap?.targetHorizon ?: "3 Years")
        StatisticMiniItem(label = "Target CTC", value = roadmap?.targetCompensation ?: "₹35L - ₹50L")
        StatisticMiniItem(label = "Confidence", value = "${roadmap?.confidenceScore ?: 94}%")
        StatisticMiniItem(label = "Phases", value = "${roadmap?.phases?.size ?: 3} Stages")
      }
    }
  }
}

@Composable
fun StatisticMiniItem(label: String, value: String) {
  Column(horizontalAlignment = Alignment.Start) {
    Text(text = label, color = TextMutedDark, fontSize = 9.sp, fontWeight = FontWeight.SemiBold)
    Text(text = value, color = TextPrimaryDark, fontSize = 11.sp, fontWeight = FontWeight.Bold)
  }
}

// -------------------------------------------------------------------------------------
// 3. Engine Mode Selector (D3 vs Recharts)
// -------------------------------------------------------------------------------------

@Composable
fun EngineModeSelectorRow(
  selectedEngine: ChartEngine,
  onSelectEngine: (ChartEngine) -> Unit,
  modifier: Modifier = Modifier
) {
  Row(
    modifier = modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    FilterChip(
      selected = selectedEngine == ChartEngine.D3_WEB_ENGINE,
      onClick = { onSelectEngine(ChartEngine.D3_WEB_ENGINE) },
      label = {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Icon(Icons.Default.Code, contentDescription = null, modifier = Modifier.size(14.dp))
          Text("D3.js SVG Web Engine", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        }
      },
      colors = FilterChipDefaults.filterChipColors(
        selectedContainerColor = TitanCyan.copy(alpha = 0.18f),
        selectedLabelColor = TitanCyan,
        containerColor = SlateCard,
        labelColor = TextMutedDark
      ),
      border = FilterChipDefaults.filterChipBorder(
        enabled = true,
        selected = selectedEngine == ChartEngine.D3_WEB_ENGINE,
        borderColor = SlateBorder,
        selectedBorderColor = TitanCyan
      ),
      modifier = Modifier
        .weight(1f)
        .testTag("engine_toggle_d3")
    )

    FilterChip(
      selected = selectedEngine == ChartEngine.RECHARTS_COMPOSE,
      onClick = { onSelectEngine(ChartEngine.RECHARTS_COMPOSE) },
      label = {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Icon(Icons.AutoMirrored.Filled.ShowChart, contentDescription = null, modifier = Modifier.size(14.dp))
          Text("Recharts Canvas", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        }
      },
      colors = FilterChipDefaults.filterChipColors(
        selectedContainerColor = TitanGold.copy(alpha = 0.18f),
        selectedLabelColor = TitanGold,
        containerColor = SlateCard,
        labelColor = TextMutedDark
      ),
      border = FilterChipDefaults.filterChipBorder(
        enabled = true,
        selected = selectedEngine == ChartEngine.RECHARTS_COMPOSE,
        borderColor = SlateBorder,
        selectedBorderColor = TitanGold
      ),
      modifier = Modifier
        .weight(1f)
        .testTag("engine_toggle_recharts")
    )
  }
}

// -------------------------------------------------------------------------------------
// 4A. D3.js Interactive Web Engine (Embedded SVG via WebView)
// -------------------------------------------------------------------------------------

class D3Bridge(private val onPhaseSelected: (Int) -> Unit) {
  @JavascriptInterface
  fun onSelectPhase(phaseIndex: Int) {
    onPhaseSelected(phaseIndex)
  }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun D3CareerProgressionWebView(
  roadmap: CareerStrategyRoadmap?,
  currentRole: String,
  targetGoal: String,
  onPhaseSelected: (Int) -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val htmlContent = remember(roadmap, currentRole, targetGoal) {
    buildD3CareerProgressionHtml(roadmap, currentRole, targetGoal)
  }

  AndroidView(
    factory = { ctx ->
      WebView(ctx).apply {
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        settings.useWideViewPort = true
        settings.loadWithOverviewMode = true
        setBackgroundColor(0x00000000)
        webViewClient = WebViewClient()
        addJavascriptInterface(D3Bridge(onPhaseSelected), "AndroidBridge")
        loadDataWithBaseURL("https://titan.local", htmlContent, "text/html", "UTF-8", null)
      }
    },
    update = { webView ->
      webView.loadDataWithBaseURL("https://titan.local", htmlContent, "text/html", "UTF-8", null)
    },
    modifier = modifier
  )
}

/**
 * Builds high-performance interactive HTML/SVG markup powered by D3.js v7
 * with responsive viewBox, glowing trajectory curves, animated milestone markers,
 * and tap-to-inspect tooltips.
 */
fun buildD3CareerProgressionHtml(
  roadmap: CareerStrategyRoadmap?,
  currentRole: String,
  targetGoal: String
): String {
  val phases = roadmap?.phases ?: emptyList()
  val phasesJson = if (phases.isEmpty()) {
    """[
      {"number":0,"title":"Baseline: $currentRole","timeframe":"Current","compLakhs":18,"progress":100,"acquired":3,"total":3,"milestones":["Foundational Execution","Analytics & SQL"]},
      {"number":1,"title":"Phase 1: Foundation","timeframe":"Months 1-6","compLakhs":25,"progress":75,"acquired":3,"total":4,"milestones":["Cross-functional Ops","Executive Narrative"]},
      {"number":2,"title":"Phase 2: Scale","timeframe":"Months 7-18","compLakhs":38,"progress":40,"acquired":2,"total":5,"milestones":["P&L Leadership","Strategic Dealflow"]},
      {"number":3,"title":"Phase 3: Executive Goal","timeframe":"Months 19-36","compLakhs":52,"progress":15,"acquired":1,"total":4,"milestones":["Board Advisory","Global Strategy Lead"]}
    ]"""
  } else {
    val phaseList = mutableListOf<String>()
    // Add baseline
    phaseList.add(
      """{"number":0,"title":"Baseline: ${escapeJson(currentRole)}","timeframe":"Current","compLakhs":18,"progress":100,"acquired":3,"total":3,"milestones":["Current Core Competencies"]}"""
    )
    phases.forEachIndexed { idx, p ->
      val compValue = extractCompensationNumber(p.targetCompLakhs, defaultVal = 22 + (idx * 12))
      val acquiredCount = p.skillMilestones.count { it.status == CareerMilestoneStatus.ACQUIRED }
      val totalCount = p.skillMilestones.size.coerceAtLeast(1)
      val msList = p.skillMilestones.take(3).joinToString(",") { "\"${escapeJson(it.title)}\"" }
      phaseList.add(
        """{"number":${idx + 1},"title":"Phase ${p.phaseNumber}: ${escapeJson(p.title)}","timeframe":"${escapeJson(p.timeframe)}","compLakhs":$compValue,"progress":${p.completionPercentage},"acquired":$acquiredCount,"total":$totalCount,"milestones":[$msList]}"""
      )
    }
    phaseList.joinToString(prefix = "[", postfix = "]")
  }

  val targetCompDisplay = roadmap?.targetCompensation ?: "₹35L - ₹50L CTC"

  return """
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
  <title>D3 Career Progression</title>
  <script src="https://d3js.org/d3.v7.min.js"></script>
  <style>
    * { box-sizing: border-box; margin: 0; padding: 0; user-select: none; -webkit-tap-highlight-color: transparent; }
    body {
      background: #0B111E;
      color: #F8FAFC;
      font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif;
      padding: 8px;
      overflow: hidden;
    }
    .chart-container {
      width: 100%;
      height: 100%;
      position: relative;
    }
    svg {
      width: 100%;
      height: 220px;
      display: block;
    }
    .grid line {
      stroke: #1E2D47;
      stroke-dasharray: 2,3;
      stroke-opacity: 0.6;
    }
    .axis text {
      fill: #64748B;
      font-size: 9px;
      font-weight: 600;
    }
    .axis line, .axis path {
      stroke: #1E2D47;
    }
    .goal-line {
      stroke: #F59E0B;
      stroke-width: 1.5;
      stroke-dasharray: 4,4;
    }
    .trajectory-area {
      fill: url(#cyan-emerald-gradient);
      opacity: 0.35;
    }
    .trajectory-line {
      fill: none;
      stroke: #00E5FF;
      stroke-width: 3;
      filter: drop-shadow(0 0 6px rgba(0,229,255,0.7));
    }
    .node-outer {
      fill: #0B111E;
      stroke-width: 2.5;
      cursor: pointer;
      transition: r 0.2s ease;
    }
    .node-inner {
      cursor: pointer;
    }
    .info-hud {
      background: #141C2E;
      border: 1px solid #26354D;
      border-radius: 8px;
      padding: 6px 10px;
      margin-top: 6px;
      display: flex;
      justify-content: space-between;
      align-items: center;
    }
    .hud-title {
      font-size: 11px;
      font-weight: 700;
      color: #00E5FF;
    }
    .hud-subtitle {
      font-size: 9px;
      color: #94A3B8;
    }
    .hud-comp {
      font-size: 11px;
      font-weight: 800;
      color: #10B981;
    }
  </style>
</head>
<body>
  <div class="chart-container">
    <svg id="d3-chart" viewBox="0 0 380 210" preserveAspectRatio="none">
      <defs>
        <linearGradient id="cyan-emerald-gradient" x1="0%" y1="0%" x2="100%" y2="100%">
          <stop offset="0%" stop-color="#00E5FF" stop-opacity="0.6"/>
          <stop offset="60%" stop-color="#10B981" stop-opacity="0.3"/>
          <stop offset="100%" stop-color="#10B981" stop-opacity="0.0"/>
        </linearGradient>
      </defs>
    </svg>
    <div class="info-hud" id="hud">
      <div>
        <div class="hud-title" id="hud-title">Tap any milestone to inspect progression</div>
        <div class="hud-subtitle" id="hud-sub">Target Goal: ${escapeJson(targetGoal)}</div>
      </div>
      <div class="hud-comp" id="hud-comp">${escapeJson(targetCompDisplay)}</div>
    </div>
  </div>

  <script>
    const data = $phasesJson;
    const svg = d3.select("#d3-chart");
    const width = 380;
    const height = 205;
    const margin = { top: 20, right: 25, bottom: 30, left: 35 };

    const innerWidth = width - margin.left - margin.right;
    const innerHeight = height - margin.top - margin.bottom;

    const g = svg.append("g")
      .attr("transform", "translate(" + margin.left + "," + margin.top + ")");

    // X Scale: Categories / Stages
    const xScale = d3.scaleLinear()
      .domain([0, data.length - 1])
      .range([0, innerWidth]);

    // Y Scale: Compensation in Lakhs INR
    const maxComp = d3.max(data, d => d.compLakhs) * 1.15;
    const yScale = d3.scaleLinear()
      .domain([10, maxComp || 60])
      .range([innerHeight, 0]);

    // Grid lines
    g.append("g")
      .attr("class", "grid")
      .call(d3.axisLeft(yScale).ticks(4).tickSize(-innerWidth).tickFormat(""));

    // Strategic Goal Benchmark Line
    const goalComp = data[data.length - 1].compLakhs;
    g.append("line")
      .attr("class", "goal-line")
      .attr("x1", 0)
      .attr("y1", yScale(goalComp))
      .attr("x2", innerWidth)
      .attr("y2", yScale(goalComp));

    g.append("text")
      .attr("x", innerWidth - 5)
      .attr("y", yScale(goalComp) - 5)
      .attr("text-anchor", "end")
      .attr("fill", "#F59E0B")
      .attr("font-size", "8px")
      .attr("font-weight", "700")
      .text("GOAL TARGET");

    // Area generator
    const area = d3.area()
      .x((d, i) => xScale(i))
      .y0(innerHeight)
      .y1(d => yScale(d.compLakhs))
      .curve(d3.curveMonotoneX);

    g.append("path")
      .datum(data)
      .attr("class", "trajectory-area")
      .attr("d", area);

    // Line generator
    const line = d3.line()
      .x((d, i) => xScale(i))
      .y(d => yScale(d.compLakhs))
      .curve(d3.curveMonotoneX);

    g.append("path")
      .datum(data)
      .attr("class", "trajectory-line")
      .attr("d", line);

    // X-Axis
    const xAxis = d3.axisBottom(xScale)
      .ticks(data.length)
      .tickFormat((d, i) => {
        const item = data[i];
        return item ? (item.number === 0 ? "Current" : "P" + item.number) : "";
      });

    g.append("g")
      .attr("class", "axis")
      .attr("transform", "translate(0," + innerHeight + ")")
      .call(xAxis);

    // Y-Axis (Lakhs)
    const yAxis = d3.axisLeft(yScale)
      .ticks(4)
      .tickFormat(d => "₹" + d + "L");

    g.append("g")
      .attr("class", "axis")
      .call(yAxis);

    // Nodes
    const nodes = g.selectAll(".node-group")
      .data(data)
      .enter()
      .append("g")
      .attr("class", "node-group")
      .attr("transform", (d, i) => "translate(" + xScale(i) + "," + yScale(d.compLakhs) + ")");

    nodes.append("circle")
      .attr("class", "node-outer")
      .attr("r", 7)
      .attr("stroke", (d) => d.progress >= 90 ? "#10B981" : (d.progress > 0 ? "#00E5FF" : "#64748B"));

    nodes.append("circle")
      .attr("class", "node-inner")
      .attr("r", 3.5)
      .attr("fill", (d) => d.progress >= 90 ? "#10B981" : (d.progress > 0 ? "#00E5FF" : "#64748B"));

    // Click handler for interactive inspection
    nodes.on("click", function(event, d) {
      d3.selectAll(".node-outer").attr("r", 7);
      d3.select(this).select(".node-outer").attr("r", 10);

      document.getElementById("hud-title").innerText = d.title + " (" + d.timeframe + ")";
      document.getElementById("hud-sub").innerText = d.acquired + "/" + d.total + " Milestones Acquired (" + d.progress + "% Complete)";
      document.getElementById("hud-comp").innerText = "₹" + d.compLakhs + "L CTC";

      try {
        if (window.AndroidBridge) {
          window.AndroidBridge.onSelectPhase(d.number);
        }
      } catch (e) {}
    });
  </script>
</body>
</html>
  """.trimIndent()
}

// -------------------------------------------------------------------------------------
// 4B. Recharts-Style Native Compose Canvas Chart
// -------------------------------------------------------------------------------------

@Composable
fun RechartsComposeCareerChart(
  roadmap: CareerStrategyRoadmap?,
  currentRole: String,
  targetGoal: String,
  onPhaseSelected: (Int) -> Unit,
  modifier: Modifier = Modifier
) {
  val phases = roadmap?.phases ?: emptyList()

  // Points list: Baseline (0) + Roadmap Phases
  val points = remember(phases, currentRole) {
    val list = mutableListOf<RechartsPoint>()
    list.add(
      RechartsPoint(
        phaseNumber = 0,
        label = "Current",
        title = currentRole,
        compLakhs = 18f,
        progress = 100,
        acquiredMilestones = 3,
        totalMilestones = 3
      )
    )
    phases.forEachIndexed { idx, p ->
      val compVal = extractCompensationNumber(p.targetCompLakhs, defaultVal = 22 + (idx * 12)).toFloat()
      list.add(
        RechartsPoint(
          phaseNumber = idx + 1,
          label = "Phase ${p.phaseNumber}",
          title = p.title,
          compLakhs = compVal,
          progress = p.completionPercentage,
          acquiredMilestones = p.skillMilestones.count { it.status == CareerMilestoneStatus.ACQUIRED },
          totalMilestones = p.skillMilestones.size.coerceAtLeast(1)
        )
      )
    }
    list
  }

  var activeScrubIndex by remember { mutableIntStateOf(points.size - 1) }

  Column(
    modifier = modifier.padding(12.dp),
    verticalArrangement = Arrangement.SpaceBetween
  ) {
    // Interactive HUD Banner at top of chart
    val activePoint = points.getOrNull(activeScrubIndex) ?: points.last()
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .background(SlateCard, RoundedCornerShape(8.dp))
        .border(1.dp, SlateBorder, RoundedCornerShape(8.dp))
        .padding(horizontal = 10.dp, vertical = 6.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Text(
          text = "${activePoint.label}: ${activePoint.title}",
          color = TitanCyan,
          fontWeight = FontWeight.Bold,
          fontSize = 11.sp,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
        Text(
          text = "${activePoint.acquiredMilestones}/${activePoint.totalMilestones} Milestones • ${activePoint.progress}% Complete",
          color = TextMutedDark,
          fontSize = 10.sp
        )
      }
      Surface(
        color = TitanEmerald.copy(alpha = 0.15f),
        shape = RoundedCornerShape(6.dp)
      ) {
        Text(
          text = "₹${activePoint.compLakhs.roundToInt()}L CTC",
          color = TitanEmerald,
          fontWeight = FontWeight.ExtraBold,
          fontSize = 11.sp,
          modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
      }
    }

    Spacer(modifier = Modifier.height(8.dp))

    // Hardware-accelerated Canvas with Bezier trajectory & interactive scrub gesture
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .weight(1f)
        .pointerInput(points) {
          detectTapGestures { offset ->
            val stepWidth = size.width / (points.size - 1).coerceAtLeast(1)
            val idx = (offset.x / stepWidth).roundToInt().coerceIn(0, points.size - 1)
            activeScrubIndex = idx
            onPhaseSelected(points[idx].phaseNumber)
          }
        }
        .pointerInput(points) {
          detectDragGestures { change, _ ->
            val stepWidth = size.width / (points.size - 1).coerceAtLeast(1)
            val idx = (change.position.x / stepWidth).roundToInt().coerceIn(0, points.size - 1)
            activeScrubIndex = idx
            onPhaseSelected(points[idx].phaseNumber)
          }
        }
    ) {
      Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val padLeft = 40.dp.toPx()
        val padRight = 20.dp.toPx()
        val padTop = 20.dp.toPx()
        val padBottom = 30.dp.toPx()

        val plotW = w - padLeft - padRight
        val plotH = h - padTop - padBottom

        val minComp = 10f
        val maxComp = (points.maxOfOrNull { it.compLakhs } ?: 50f) * 1.2f

        // Draw horizontal grid lines
        val gridLines = 4
        for (i in 0..gridLines) {
          val y = padTop + (plotH / gridLines) * i
          drawLine(
            color = SlateBorder.copy(alpha = 0.5f),
            start = Offset(padLeft, y),
            end = Offset(w - padRight, y),
            strokeWidth = 1.dp.toPx(),
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
          )
        }

        // Draw Target Goal reference threshold line
        val goalComp = points.last().compLakhs
        val goalY = padTop + plotH * (1f - (goalComp - minComp) / (maxComp - minComp))
        drawLine(
          color = TitanGold.copy(alpha = 0.8f),
          start = Offset(padLeft, goalY),
          end = Offset(w - padRight, goalY),
          strokeWidth = 1.5.dp.toPx(),
          pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 6f), 0f)
        )

        // Calculate node screen coordinates
        val nodeCoords = points.mapIndexed { idx, pt ->
          val normX = idx.toFloat() / (points.size - 1).coerceAtLeast(1)
          val normY = (pt.compLakhs - minComp) / (maxComp - minComp)
          Offset(
            x = padLeft + normX * plotW,
            y = padTop + plotH * (1f - normY.coerceIn(0f, 1f))
          )
        }

        // Generate smooth cubic Bezier area path and stroke path
        val strokePath = Path()
        val areaPath = Path()

        if (nodeCoords.isNotEmpty()) {
          strokePath.moveTo(nodeCoords[0].x, nodeCoords[0].y)
          areaPath.moveTo(nodeCoords[0].x, padTop + plotH)
          areaPath.lineTo(nodeCoords[0].x, nodeCoords[0].y)

          for (i in 0 until nodeCoords.size - 1) {
            val p0 = nodeCoords[i]
            val p1 = nodeCoords[i + 1]
            val cx1 = p0.x + (p1.x - p0.x) / 2f
            val cy1 = p0.y
            val cx2 = p0.x + (p1.x - p0.x) / 2f
            val cy2 = p1.y

            strokePath.cubicTo(cx1, cy1, cx2, cy2, p1.x, p1.y)
            areaPath.cubicTo(cx1, cy1, cx2, cy2, p1.x, p1.y)
          }

          areaPath.lineTo(nodeCoords.last().x, padTop + plotH)
          areaPath.close()

          // Draw Area Gradient
          drawPath(
            path = areaPath,
            brush = Brush.verticalGradient(
              colors = listOf(
                TitanCyan.copy(alpha = 0.35f),
                TitanEmerald.copy(alpha = 0.15f),
                Color.Transparent
              ),
              startY = padTop,
              endY = padTop + plotH
            )
          )

          // Draw Trajectory Stroke Line
          drawPath(
            path = strokePath,
            color = TitanCyan,
            style = Stroke(
              width = 3.dp.toPx(),
              cap = StrokeCap.Round
            )
          )

          // Draw Milestone Nodes
          nodeCoords.forEachIndexed { idx, coord ->
            val pt = points[idx]
            val isSelected = idx == activeScrubIndex
            val nodeColor = if (pt.progress >= 90) TitanEmerald else if (pt.progress > 0) TitanCyan else TitanGold

            // Selected glow halo
            if (isSelected) {
              drawCircle(
                color = nodeColor.copy(alpha = 0.3f),
                radius = 12.dp.toPx(),
                center = coord
              )
            }

            // Outer ring
            drawCircle(
              color = ObsidianDark,
              radius = if (isSelected) 8.dp.toPx() else 6.dp.toPx(),
              center = coord
            )
            drawCircle(
              color = nodeColor,
              radius = if (isSelected) 8.dp.toPx() else 6.dp.toPx(),
              center = coord,
              style = Stroke(width = 2.dp.toPx())
            )

            // Inner core
            drawCircle(
              color = nodeColor,
              radius = if (isSelected) 4.dp.toPx() else 2.5.dp.toPx(),
              center = coord
            )
          }
        }
      }
    }
  }
}

data class RechartsPoint(
  val phaseNumber: Int,
  val label: String,
  val title: String,
  val compLakhs: Float,
  val progress: Int,
  val acquiredMilestones: Int,
  val totalMilestones: Int
)

// -------------------------------------------------------------------------------------
// Chart Legend Strip
// -------------------------------------------------------------------------------------

@Composable
fun ChartLegendStrip() {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    LegendDot(color = TitanCyan, text = "Trajectory Curve")
    LegendDot(color = TitanEmerald, text = "Acquired Status")
    LegendDot(color = TitanGold, text = "Strategic Goal Line")
  }
}

@Composable
fun LegendDot(color: Color, text: String) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(5.dp)
  ) {
    Box(
      modifier = Modifier
        .size(8.dp)
        .background(color, CircleShape)
    )
    Text(text = text, color = TextMutedDark, fontSize = 10.sp, fontWeight = FontWeight.Medium)
  }
}

// -------------------------------------------------------------------------------------
// 5. Strategic Pillars & Goals Scorecard
// -------------------------------------------------------------------------------------

@Composable
fun StrategicPillarsScorecard(
  pillars: List<com.example.data.model.StrategicPillar>,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(14.dp))
      .border(1.dp, SlateBorder, RoundedCornerShape(14.dp)),
    colors = CardDefaults.cardColors(containerColor = SlateCard)
  ) {
    Column(
      modifier = Modifier.padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp)
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
          Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = TitanCyan, modifier = Modifier.size(16.dp))
          Text(
            text = "Strategic Pillars & Core Competencies",
            color = TextPrimaryDark,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
          )
        }
        Text(
          text = "${pillars.size} Pillars",
          color = TextMutedDark,
          fontSize = 11.sp
        )
      }

      pillars.forEach { pillar ->
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(
              text = pillar.name,
              color = TextPrimaryDark,
              fontSize = 12.sp,
              fontWeight = FontWeight.SemiBold
            )
            Text(
              text = "${pillar.currentScore}% (${pillar.weightPercentage}% Weight)",
              color = TitanCyan,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold
            )
          }
          LinearProgressIndicator(
            progress = { (pillar.currentScore / 100f).coerceIn(0f, 1f) },
            modifier = Modifier
              .fillMaxWidth()
              .height(5.dp)
              .clip(RoundedCornerShape(2.5.dp)),
            color = if (pillar.currentScore >= 70) TitanEmerald else TitanCyan,
            trackColor = SlateDarker
          )
          if (pillar.description.isNotEmpty()) {
            Text(
              text = pillar.description,
              color = TextMutedDark,
              fontSize = 10.sp,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
          }
        }
      }
    }
  }
}

// -------------------------------------------------------------------------------------
// 6. Strategic Milestones Phase Checklist with Live Toggle & Firestore Push
// -------------------------------------------------------------------------------------

@Composable
fun StrategicMilestonesPhaseList(
  phases: List<RoadmapPhase>,
  selectedPhaseIndex: Int,
  onPhaseSelected: (Int) -> Unit,
  onToggleMilestone: (String) -> Unit,
  onSyncToFirestore: () -> Unit,
  modifier: Modifier = Modifier
) {
  val activePhase = phases.find { it.phaseNumber == selectedPhaseIndex } ?: phases.firstOrNull()

  Card(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(14.dp))
      .border(1.dp, SlateBorder, RoundedCornerShape(14.dp)),
    colors = CardDefaults.cardColors(containerColor = SlateCard)
  ) {
    Column(
      modifier = Modifier.padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "STRATEGIC MILESTONES BREAKDOWN",
            color = TitanGold,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
          )
          Text(
            text = "Tap any milestone to update verification status",
            color = TextMutedDark,
            fontSize = 11.sp
          )
        }

        OutlinedButton(
          onClick = onSyncToFirestore,
          modifier = Modifier.height(32.dp),
          colors = ButtonDefaults.outlinedButtonColors(contentColor = TitanCyan),
          border = androidx.compose.foundation.BorderStroke(1.dp, TitanCyan.copy(alpha = 0.5f)),
          contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 0.dp),
          shape = RoundedCornerShape(8.dp)
        ) {
          Icon(Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(13.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Push to Cloud", fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
      }

      // Phase Selector Filter Chips
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        phases.forEach { p ->
          val isSelected = (activePhase?.phaseNumber ?: 1) == p.phaseNumber
          FilterChip(
            selected = isSelected,
            onClick = { onPhaseSelected(p.phaseNumber) },
            label = {
              Text("Phase ${p.phaseNumber}", fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
            },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = TitanCyan.copy(alpha = 0.2f),
              selectedLabelColor = TitanCyan,
              containerColor = SlateElevated,
              labelColor = TextSecondaryDark
            ),
            border = FilterChipDefaults.filterChipBorder(
              enabled = true,
              selected = isSelected,
              borderColor = SlateBorder,
              selectedBorderColor = TitanCyan
            )
          )
        }
      }

      // Milestones in selected phase
      activePhase?.let { phase ->
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          phase.skillMilestones.forEach { milestone ->
            MilestoneStatusRow(
              milestone = milestone,
              onToggle = { onToggleMilestone(milestone.id) }
            )
          }
        }
      }
    }
  }
}

@Composable
fun MilestoneStatusRow(
  milestone: SkillMilestone,
  onToggle: () -> Unit
) {
  val statusColor = when (milestone.status) {
    CareerMilestoneStatus.ACQUIRED -> TitanEmerald
    CareerMilestoneStatus.IN_PROGRESS -> TitanCyan
    CareerMilestoneStatus.NOT_STARTED -> TextMutedDark
  }

  val statusIcon = when (milestone.status) {
    CareerMilestoneStatus.ACQUIRED -> Icons.Default.CheckCircle
    CareerMilestoneStatus.IN_PROGRESS -> Icons.Default.HourglassTop
    CareerMilestoneStatus.NOT_STARTED -> Icons.Default.RadioButtonUnchecked
  }

  Surface(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(10.dp))
      .clickable { onToggle() }
      .border(1.dp, SlateBorder.copy(alpha = 0.5f), RoundedCornerShape(10.dp)),
    color = SlateDarker
  ) {
    Row(
      modifier = Modifier.padding(12.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      Icon(
        imageVector = statusIcon,
        contentDescription = milestone.status.label,
        tint = statusColor,
        modifier = Modifier.size(20.dp)
      )

      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = milestone.title,
          color = TextPrimaryDark,
          fontWeight = FontWeight.SemiBold,
          fontSize = 12.sp
        )
        Row(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(text = milestone.category, color = TextMutedDark, fontSize = 10.sp)
          Text(text = "•", color = TextMutedDark, fontSize = 10.sp)
          Text(text = milestone.targetProficiency, color = TitanCyan, fontSize = 10.sp)
        }
      }

      Surface(
        color = statusColor.copy(alpha = 0.15f),
        shape = RoundedCornerShape(6.dp)
      ) {
        Text(
          text = milestone.status.label,
          color = statusColor,
          fontWeight = FontWeight.Bold,
          fontSize = 9.sp,
          modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
      }
    }
  }
}

// -------------------------------------------------------------------------------------
// Utility JSON & String Helpers
// -------------------------------------------------------------------------------------

fun escapeJson(text: String): String {
  return text.replace("\\", "\\\\")
    .replace("\"", "\\\"")
    .replace("\n", " ")
    .replace("\r", " ")
}

fun extractCompensationNumber(text: String, defaultVal: Int): Int {
  val regex = Regex("""(\d+)""")
  val match = regex.find(text)
  return match?.groupValues?.get(1)?.toIntOrNull() ?: defaultVal
}
