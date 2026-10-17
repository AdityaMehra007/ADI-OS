package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
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
import com.example.ui.theme.TitanEmerald
import com.example.ui.theme.TitanGold
import com.example.ui.theme.TitanIndigo
import kotlin.math.roundToInt

/**
 * Recharts Visualizer Mode for Relationship Ledger Conversion Analytics
 */
enum class RechartsConversionMode(val label: String, val subtitle: String) {
  FUNNEL_FLOW("Funnel Flow", "Applied to Interview & Offer Conversion Pipeline"),
  BENCHMARK_BARS("Benchmark Bars", "Comparative Cartesian Conversion Rates vs Industry"),
  STAGE_BREAKDOWN("Stage Matrix", "Status Distribution & Compensation Bands")
}

/**
 * Stage conversion data point for the Recharts dashboard
 */
data class ConversionStageData(
  val stageKey: String,
  val stageName: String,
  val count: Int,
  val percentageOfTotal: Float,
  val dropOffCount: Int,
  val color: Color,
  val companies: List<String>,
  val avgCompEstimate: String
)

/**
 * Benchmark comparison model
 */
data class ConversionRateMetric(
  val metricKey: String,
  val metricTitle: String,
  val userRatePct: Float,
  val benchmarkRatePct: Float,
  val deltaPct: Float,
  val status: String,
  val barColor: Color,
  val advice: String
)

/**
 * Recharts Application Conversion Dashboard for Relationship Ledger.
 * Computes and renders:
 *  - Applied to Interview Conversion Rate
 *  - Interview to Offer Conversion Rate
 *  - Net Pipeline Conversion Rate
 * with Recharts-inspired Cartesian grid, animated canvas bars, funnel connectors,
 * scrub tooltip, and strategy advice derived directly from persisted company statuses.
 */
@Composable
fun RelationshipLedgerRechartsDashboard(
  ledgerItems: List<CompanyApplicationLedgerItem>,
  modifier: Modifier = Modifier,
  onFilterByStatus: ((String) -> Unit)? = null
) {
  var selectedMode by remember { mutableStateOf(RechartsConversionMode.FUNNEL_FLOW) }
  var selectedStageIndex by remember { mutableStateOf<Int?>(null) }
  var isScrubbing by remember { mutableStateOf(false) }

  // Animated entrance transition
  val animProgress = remember { Animatable(0f) }
  LaunchedEffect(selectedMode, ledgerItems.size) {
    animProgress.snapTo(0f)
    animProgress.animateTo(
      targetValue = 1f,
      animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing)
    )
  }

  // ---------------------------------------------------------------------------
  // Metric Calculations based on Persisted Company Statuses
  // ---------------------------------------------------------------------------
  val totalTracked = ledgerItems.size
  val targetedList = ledgerItems.filter { it.status.equals("Targeted", ignoreCase = true) }
  val appliedList = ledgerItems.filter { it.status.equals("Applied", ignoreCase = true) }
  val interviewingList = ledgerItems.filter { it.status.equals("Interviewing", ignoreCase = true) }
  val offerList = ledgerItems.filter { it.status.equals("Offer", ignoreCase = true) }
  val archivedList = ledgerItems.filter { it.status.equals("Archived", ignoreCase = true) }

  val targetedCount = targetedList.size
  val appliedCount = appliedList.size
  val interviewingCount = interviewingList.size
  val offerCount = offerList.size

  // Total applications that have been formally submitted
  // (companies in Interviewing and Offer previously passed the Applied stage)
  val totalSubmittedApplications = appliedCount + interviewingCount + offerCount

  // Total companies that successfully converted to interview loops or beyond
  val totalInterviewsReached = interviewingCount + offerCount

  // Conversion Rates:
  // 1. Applied to Interview:
  //    Rate at which dispatched applications convert into interview opportunities
  val appliedToInterviewRate = if (totalSubmittedApplications > 0) {
    (totalInterviewsReached.toFloat() / totalSubmittedApplications.toFloat()) * 100f
  } else 0f

  // 2. Interview to Offer:
  //    Rate at which active interviews convert into confirmed job offers
  val interviewToOfferRate = if (totalInterviewsReached > 0) {
    (offerCount.toFloat() / totalInterviewsReached.toFloat()) * 100f
  } else 0f

  // 3. Overall Pipeline Conversion Rate (Submitted -> Offer):
  val overallConversionRate = if (totalSubmittedApplications > 0) {
    (offerCount.toFloat() / totalSubmittedApplications.toFloat()) * 100f
  } else 0f

  // Conversion drop-offs
  val appliedDropOff = (totalSubmittedApplications - totalInterviewsReached).coerceAtLeast(0)
  val interviewDropOff = (totalInterviewsReached - offerCount).coerceAtLeast(0)

  // Funnel Stage Models
  val funnelStages = remember(ledgerItems) {
    listOf(
      ConversionStageData(
        stageKey = "Applied",
        stageName = "Applications Submitted",
        count = totalSubmittedApplications,
        percentageOfTotal = 100f,
        dropOffCount = appliedDropOff,
        color = TitanCyan,
        companies = (appliedList + interviewingList + offerList).map { it.companyName },
        avgCompEstimate = "₹28.4L CTC Avg"
      ),
      ConversionStageData(
        stageKey = "Interviewing",
        stageName = "Interviews Secured",
        count = totalInterviewsReached,
        percentageOfTotal = if (totalSubmittedApplications > 0) appliedToInterviewRate else 0f,
        dropOffCount = interviewDropOff,
        color = TitanGold,
        companies = (interviewingList + offerList).map { it.companyName },
        avgCompEstimate = "₹29.8L CTC Avg"
      ),
      ConversionStageData(
        stageKey = "Offer",
        stageName = "Offers Extended",
        count = offerCount,
        percentageOfTotal = if (totalSubmittedApplications > 0) overallConversionRate else 0f,
        dropOffCount = 0,
        color = TitanEmerald,
        companies = offerList.map { it.companyName },
        avgCompEstimate = "₹30.5L CTC Avg"
      )
    )
  }

  // Benchmark Metrics (User vs Tier-1 Industry Benchmarks)
  val benchmarkMetrics = remember(appliedToInterviewRate, interviewToOfferRate, overallConversionRate) {
    val appliedBenchmark = 22.0f // Industry median: 22% of cold/warm applications reach interview
    val interviewBenchmark = 28.0f // Industry median: 28% of interview loops convert to offer
    val overallBenchmark = 6.2f // Industry median: ~6% overall yield

    listOf(
      ConversionRateMetric(
        metricKey = "APPLIED_TO_INTERVIEW",
        metricTitle = "Applied → Interview",
        userRatePct = appliedToInterviewRate,
        benchmarkRatePct = appliedBenchmark,
        deltaPct = appliedToInterviewRate - appliedBenchmark,
        status = if (appliedToInterviewRate >= appliedBenchmark) "EXCELLING" else "NEEDS_OPTIMIZATION",
        barColor = TitanCyan,
        advice = if (appliedToInterviewRate >= appliedBenchmark) {
          "ATS resume tailoring & 1-Click targeting are outperforming the industry standard by +${(appliedToInterviewRate - appliedBenchmark).roundToInt()}%."
        } else {
          "Focus on tailoring cover letters and alumni referrals to boost interview callback rate."
        }
      ),
      ConversionRateMetric(
        metricKey = "INTERVIEW_TO_OFFER",
        metricTitle = "Interview → Offer",
        userRatePct = interviewToOfferRate,
        benchmarkRatePct = interviewBenchmark,
        deltaPct = interviewToOfferRate - interviewBenchmark,
        status = if (interviewToOfferRate >= interviewBenchmark) "EXCELLING" else "NEEDS_OPTIMIZATION",
        barColor = TitanGold,
        advice = if (interviewToOfferRate >= interviewBenchmark) {
          "High interview loop conversion (+${(interviewToOfferRate - interviewBenchmark).roundToInt()}%). Dark Store unit economic case studies are landing effectively."
        } else {
          "Leverage the Mock Interview War Room to practice partner round unit-economics questions."
        }
      ),
      ConversionRateMetric(
        metricKey = "NET_YIELD",
        metricTitle = "Net Pipeline Yield",
        userRatePct = overallConversionRate,
        benchmarkRatePct = overallBenchmark,
        deltaPct = overallConversionRate - overallBenchmark,
        status = if (overallConversionRate >= overallBenchmark) "EXCELLING" else "NEEDS_OPTIMIZATION",
        barColor = TitanEmerald,
        advice = "Overall funnel efficiency from initial dispatch to formal offer letter."
      )
    )
  }

  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("recharts_conversion_dashboard"),
    colors = CardDefaults.cardColors(containerColor = SlateCard),
    border = BorderStroke(1.dp, SlateBorder),
    shape = RoundedCornerShape(12.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp)
    ) {
      // -----------------------------------------------------------------------
      // 1. RECHARTS HEADER & BADGES
      // -----------------------------------------------------------------------
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
              .size(34.dp)
              .clip(RoundedCornerShape(8.dp))
              .background(TitanCyan.copy(alpha = 0.15f))
              .border(1.dp, TitanCyan.copy(alpha = 0.4f), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ShowChart,
              contentDescription = "Recharts",
              tint = TitanCyan,
              modifier = Modifier.size(18.dp)
            )
          }

          Column {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Text(
                text = "RECHARTS CONVERSION ENGINE",
                style = MaterialTheme.typography.labelSmall,
                color = TitanCyan,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp,
                fontSize = 9.sp
              )
              Box(
                modifier = Modifier
                  .size(6.dp)
                  .clip(CircleShape)
                  .background(TitanEmerald)
              )
            }
            Text(
              text = "Application Conversion Rates",
              style = MaterialTheme.typography.titleMedium,
              color = TextPrimaryDark,
              fontWeight = FontWeight.Bold,
              fontSize = 15.sp
            )
          }
        }

        // Live LocalStorage Telemetry Pill
        Surface(
          shape = RoundedCornerShape(6.dp),
          color = SlateElevated,
          border = BorderStroke(0.8.dp, SlateBorder)
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Storage,
              contentDescription = null,
              tint = TitanEmerald,
              modifier = Modifier.size(11.dp)
            )
            Text(
              text = "${ledgerItems.size} PERSISTED",
              style = MaterialTheme.typography.labelSmall,
              color = TitanEmerald,
              fontWeight = FontWeight.Bold,
              fontSize = 9.sp
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // -----------------------------------------------------------------------
      // 2. PRIMARY KPI SUMMARY TILES (Applied->Interview & Interview->Offer)
      // -----------------------------------------------------------------------
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        // Tile 1: Applied to Interview Conversion Rate
        ConversionKpiTile(
          title = "APPLIED → INTERVIEW",
          rateText = "${"%.1f".format(appliedToInterviewRate)}%",
          benchmarkDelta = appliedToInterviewRate - 22f,
          countText = "$totalInterviewsReached / $totalSubmittedApplications",
          accentColor = TitanCyan,
          testTag = "applied_to_interview_kpi",
          modifier = Modifier.weight(1f)
        )

        // Tile 2: Interview to Offer Conversion Rate
        ConversionKpiTile(
          title = "INTERVIEW → OFFER",
          rateText = "${"%.1f".format(interviewToOfferRate)}%",
          benchmarkDelta = interviewToOfferRate - 28f,
          countText = "$offerCount / $totalInterviewsReached",
          accentColor = TitanGold,
          testTag = "interview_to_offer_kpi",
          modifier = Modifier.weight(1f)
        )

        // Tile 3: Net Pipeline Yield
        ConversionKpiTile(
          title = "NET OFFER YIELD",
          rateText = "${"%.1f".format(overallConversionRate)}%",
          benchmarkDelta = overallConversionRate - 6.2f,
          countText = "$offerCount offers in hand",
          accentColor = TitanEmerald,
          testTag = "net_pipeline_yield_kpi",
          modifier = Modifier.weight(1f)
        )
      }

      Spacer(modifier = Modifier.height(12.dp))

      // -----------------------------------------------------------------------
      // 3. RECHARTS MODE SELECTOR TABS
      // -----------------------------------------------------------------------
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(8.dp))
          .background(SlateDarker)
          .border(1.dp, SlateBorder, RoundedCornerShape(8.dp))
          .padding(3.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        RechartsConversionMode.values().forEach { mode ->
          val isSelected = selectedMode == mode
          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(6.dp))
              .background(if (isSelected) TitanCyan else Color.Transparent)
              .clickable { selectedMode = mode }
              .padding(vertical = 6.dp)
              .testTag("recharts_mode_${mode.name.lowercase()}"),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = mode.label,
              style = MaterialTheme.typography.labelSmall,
              color = if (isSelected) ObsidianDark else TextSecondaryDark,
              fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
              fontSize = 11.sp
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // -----------------------------------------------------------------------
      // 4. MAIN RECHARTS VISUALIZER CONTENT
      // -----------------------------------------------------------------------
      when (selectedMode) {
        RechartsConversionMode.FUNNEL_FLOW -> {
          RechartsFunnelFlowCanvas(
            stages = funnelStages,
            appliedToInterviewRate = appliedToInterviewRate,
            interviewToOfferRate = interviewToOfferRate,
            animProgress = animProgress.value,
            selectedStageIndex = selectedStageIndex,
            onSelectStage = { selectedStageIndex = it },
            modifier = Modifier
              .fillMaxWidth()
              .height(210.dp)
              .testTag("recharts_funnel_canvas")
          )
        }

        RechartsConversionMode.BENCHMARK_BARS -> {
          RechartsBenchmarkBarsCanvas(
            metrics = benchmarkMetrics,
            animProgress = animProgress.value,
            selectedStageIndex = selectedStageIndex,
            onSelectMetric = { selectedStageIndex = it },
            modifier = Modifier
              .fillMaxWidth()
              .height(210.dp)
              .testTag("recharts_conversion_barchart")
          )
        }

        RechartsConversionMode.STAGE_BREAKDOWN -> {
          RechartsStageMatrixView(
            ledgerItems = ledgerItems,
            onFilterByStatus = onFilterByStatus
          )
        }
      }

      // -----------------------------------------------------------------------
      // 5. RECHARTS SCRUB TOOLTIP & STAGE DETAILS CARD
      // -----------------------------------------------------------------------
      Spacer(modifier = Modifier.height(10.dp))
      RechartsScrubDetailsCard(
        selectedMode = selectedMode,
        stages = funnelStages,
        metrics = benchmarkMetrics,
        selectedIndex = selectedStageIndex,
        onClearSelection = { selectedStageIndex = null }
      )

      // -----------------------------------------------------------------------
      // 6. ADVICE & CONVERSION STRATEGY INSIGHT
      // -----------------------------------------------------------------------
      Spacer(modifier = Modifier.height(8.dp))
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(8.dp))
          .background(SlateElevated.copy(alpha = 0.6f))
          .border(0.8.dp, SlateBorder, RoundedCornerShape(8.dp))
          .padding(8.dp),
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
            text = "RECHARTS STRATEGY ADVISORY",
            style = MaterialTheme.typography.labelSmall,
            color = TitanGold,
            fontWeight = FontWeight.Bold,
            fontSize = 9.sp
          )
          Text(
            text = when {
              appliedToInterviewRate >= 50f && interviewToOfferRate >= 35f ->
                "Top-tier conversion efficiency: Both Applied→Interview (${appliedToInterviewRate.roundToInt()}%) and Interview→Offer (${interviewToOfferRate.roundToInt()}%) outpace industry standards. Leverage competing offers to negotiate variable incentives."
              appliedToInterviewRate < 30f ->
                "Applied→Interview conversion (${appliedToInterviewRate.roundToInt()}%) has drop-off room. Use 25-Keypad Directives [RES_01] and [OUT_03] to optimize keyword density and warm alumni referrals."
              interviewToOfferRate < 25f ->
                "Interviews are locking in, but Interview→Offer yield (${interviewToOfferRate.roundToInt()}%) is below peak. Run Mock Interview War Rooms focused on dark store unit economics and cross-functional leadership."
              else ->
                "Funnel is pacing healthily. Persisting status changes in the Relationship Ledger dynamically refreshes this conversion engine."
            },
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondaryDark,
            fontSize = 10.5.sp,
            lineHeight = 14.sp
          )
        }
      }
    }
  }
}

/**
 * KPI Summary Tile for individual conversion stage
 */
@Composable
private fun ConversionKpiTile(
  title: String,
  rateText: String,
  benchmarkDelta: Float,
  countText: String,
  accentColor: Color,
  testTag: String,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(8.dp))
      .background(SlateDarker)
      .border(1.dp, SlateBorder, RoundedCornerShape(8.dp))
      .padding(8.dp)
      .testTag(testTag)
  ) {
    Column {
      Text(
        text = title,
        style = MaterialTheme.typography.labelSmall,
        color = TextMutedDark,
        fontSize = 8.5.sp,
        fontWeight = FontWeight.Bold,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = rateText,
        style = MaterialTheme.typography.titleMedium,
        color = accentColor,
        fontWeight = FontWeight.Black,
        fontSize = 17.sp
      )
      Spacer(modifier = Modifier.height(2.dp))
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(3.dp)
      ) {
        val isPositive = benchmarkDelta >= 0
        Icon(
          imageVector = if (isPositive) Icons.AutoMirrored.Filled.TrendingUp else Icons.Default.Info,
          contentDescription = null,
          tint = if (isPositive) TitanEmerald else TitanCrimson,
          modifier = Modifier.size(10.dp)
        )
        Text(
          text = if (isPositive) "+${"%.1f".format(benchmarkDelta)}% BM" else "${"%.1f".format(benchmarkDelta)}% BM",
          style = MaterialTheme.typography.labelSmall,
          color = if (isPositive) TitanEmerald else TitanCrimson,
          fontWeight = FontWeight.Bold,
          fontSize = 8.5.sp
        )
      }
      Text(
        text = countText,
        style = MaterialTheme.typography.labelSmall,
        color = TextSecondaryDark,
        fontSize = 8.sp,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )
    }
  }
}

/**
 * Canvas rendering the Recharts Funnel Flow with stage bars and conversion connector arrows
 */
@Composable
private fun RechartsFunnelFlowCanvas(
  stages: List<ConversionStageData>,
  appliedToInterviewRate: Float,
  interviewToOfferRate: Float,
  animProgress: Float,
  selectedStageIndex: Int?,
  onSelectStage: (Int) -> Unit,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(8.dp))
      .background(ObsidianDark)
      .border(1.dp, SlateBorder, RoundedCornerShape(8.dp))
      .pointerInput(stages) {
        detectTapGestures { offset ->
          val stepHeight = size.height / stages.size.toFloat()
          val clickedIndex = (offset.y / stepHeight).toInt().coerceIn(0, stages.lastIndex)
          onSelectStage(clickedIndex)
        }
      }
  ) {
    Canvas(modifier = Modifier.fillMaxSize()) {
      val w = size.width
      val h = size.height
      val padX = 16.dp.toPx()
      val padY = 14.dp.toPx()
      val availW = w - (padX * 2)
      val availH = h - (padY * 2)

      // 1. Cartesian Grid lines (dashed Recharts style)
      val gridLines = 4
      val dashEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
      for (i in 0..gridLines) {
        val y = padY + (availH / gridLines) * i
        drawLine(
          color = SlateBorder.copy(alpha = 0.5f),
          start = Offset(padX, y),
          end = Offset(w - padX, y),
          strokeWidth = 1f,
          pathEffect = dashEffect
        )
      }

      val maxCount = stages.firstOrNull()?.count?.coerceAtLeast(1) ?: 1
      val stageCount = stages.size
      val barHeight = 36.dp.toPx()
      val spacing = (availH - (barHeight * stageCount)) / (stageCount - 1).coerceAtLeast(1)

      stages.forEachIndexed { index, stage ->
        val y = padY + index * (barHeight + spacing)
        val normalizedWidth = (stage.count.toFloat() / maxCount.toFloat()).coerceIn(0.12f, 1.0f)
        val targetWidth = availW * normalizedWidth
        val currentWidth = targetWidth * animProgress

        val isSelected = selectedStageIndex == index
        val barColor = stage.color

        // Funnel Step Bar
        drawRoundRect(
          brush = Brush.horizontalGradient(
            colors = listOf(
              barColor.copy(alpha = if (isSelected) 0.95f else 0.75f),
              barColor.copy(alpha = if (isSelected) 0.65f else 0.40f)
            ),
            startX = padX,
            endX = padX + currentWidth
          ),
          topLeft = Offset(padX, y),
          size = Size(currentWidth, barHeight),
          cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
        )

        // Highlight border if selected
        if (isSelected) {
          drawRoundRect(
            color = barColor,
            topLeft = Offset(padX, y),
            size = Size(currentWidth, barHeight),
            cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx()),
            style = Stroke(width = 2.dp.toPx())
          )
        }

        // Funnel Step Marker on left
        drawCircle(
          color = barColor,
          radius = 4.dp.toPx(),
          center = Offset(padX + 12.dp.toPx(), y + barHeight / 2)
        )

        // Draw Stage Connector Arrow / Drop-off indicator between steps
        if (index < stages.lastIndex) {
          val nextY = y + barHeight + spacing
          val midY = y + barHeight + (spacing / 2)
          val connectorX = padX + (availW * 0.78f)

          // Conversion rate pill connection
          val convRate = if (index == 0) appliedToInterviewRate else interviewToOfferRate
          val pillColor = if (index == 0) TitanCyan else TitanGold

          // Connecting line
          drawLine(
            color = pillColor.copy(alpha = 0.5f),
            start = Offset(connectorX, y + barHeight),
            end = Offset(connectorX, nextY),
            strokeWidth = 2f,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 4f), 0f)
          )
        }
      }
    }

    // Compose overlay for labels and interactive tags over canvas
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp, vertical = 14.dp),
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      stages.forEachIndexed { index, stage ->
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .height(36.dp)
            .clickable { onSelectStage(index) }
            .padding(horizontal = 24.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Text(
              text = stage.stageName,
              style = MaterialTheme.typography.labelSmall,
              color = TextPrimaryDark,
              fontWeight = FontWeight.Bold,
              fontSize = 11.5.sp
            )
            Surface(
              shape = RoundedCornerShape(4.dp),
              color = stage.color.copy(alpha = 0.2f)
            ) {
              Text(
                text = "${stage.count} COs",
                style = MaterialTheme.typography.labelSmall,
                color = stage.color,
                fontWeight = FontWeight.Black,
                fontSize = 9.sp,
                modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
              )
            }
          }

          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Text(
              text = "${"%.1f".format(stage.percentageOfTotal)}% of Funnel",
              style = MaterialTheme.typography.labelSmall,
              color = stage.color,
              fontWeight = FontWeight.Bold,
              fontSize = 10.5.sp
            )
            Text(
              text = stage.avgCompEstimate,
              style = MaterialTheme.typography.labelSmall,
              color = TextMutedDark,
              fontSize = 9.sp
            )
          }
        }
      }
    }
  }
}

/**
 * Cartesian comparative Bar Chart comparing User Conversion Rates vs Industry Benchmarks
 */
@Composable
private fun RechartsBenchmarkBarsCanvas(
  metrics: List<ConversionRateMetric>,
  animProgress: Float,
  selectedStageIndex: Int?,
  onSelectMetric: (Int) -> Unit,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(8.dp))
      .background(ObsidianDark)
      .border(1.dp, SlateBorder, RoundedCornerShape(8.dp))
      .pointerInput(metrics) {
        detectTapGestures { offset ->
          val colW = size.width / metrics.size.toFloat()
          val clickedIndex = (offset.x / colW).toInt().coerceIn(0, metrics.lastIndex)
          onSelectMetric(clickedIndex)
        }
      }
  ) {
    Canvas(modifier = Modifier.fillMaxSize()) {
      val w = size.width
      val h = size.height
      val padL = 36.dp.toPx()
      val padR = 16.dp.toPx()
      val padT = 16.dp.toPx()
      val padB = 30.dp.toPx()

      val chartW = w - padL - padR
      val chartH = h - padT - padB

      // 1. Cartesian Grid Horizontal Lines & Y-Axis Labels
      val maxPct = 100f
      val gridSteps = 4 // 0%, 25%, 50%, 75%, 100%
      val dashEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)

      for (i in 0..gridSteps) {
        val pct = (maxPct / gridSteps) * i
        val y = padT + chartH - (pct / maxPct * chartH)

        drawLine(
          color = SlateBorder.copy(alpha = 0.6f),
          start = Offset(padL, y),
          end = Offset(w - padR, y),
          strokeWidth = 1f,
          pathEffect = dashEffect
        )
      }

      // 2. Render Bars (Dual Bar per Metric: User Rate vs Benchmark)
      val metricCount = metrics.size
      val colWidth = chartW / metricCount.toFloat()
      val barWidth = 18.dp.toPx()

      metrics.forEachIndexed { index, m ->
        val centerX = padL + index * colWidth + (colWidth / 2)

        // Bar 1: User Actual Rate
        val userH = (m.userRatePct / maxPct * chartH).coerceIn(0f, chartH) * animProgress
        val userTopY = padT + chartH - userH
        val userLeft = centerX - barWidth - 2.dp.toPx()

        drawRoundRect(
          brush = Brush.verticalGradient(
            colors = listOf(m.barColor, m.barColor.copy(alpha = 0.5f)),
            startY = userTopY,
            endY = padT + chartH
          ),
          topLeft = Offset(userLeft, userTopY),
          size = Size(barWidth, userH),
          cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
        )

        // Bar 2: Benchmark Rate (Muted Slate with dashed outline)
        val bmH = (m.benchmarkRatePct / maxPct * chartH).coerceIn(0f, chartH) * animProgress
        val bmTopY = padT + chartH - bmH
        val bmLeft = centerX + 2.dp.toPx()

        drawRoundRect(
          color = SlateElevated,
          topLeft = Offset(bmLeft, bmTopY),
          size = Size(barWidth, bmH),
          cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
        )

        drawRoundRect(
          color = TextMutedDark,
          topLeft = Offset(bmLeft, bmTopY),
          size = Size(barWidth, bmH),
          cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx()),
          style = Stroke(width = 1f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(3f, 3f), 0f))
        )

        // Selection highlight ring
        if (selectedStageIndex == index) {
          drawRoundRect(
            color = TitanCyan,
            topLeft = Offset(userLeft - 4.dp.toPx(), userTopY - 4.dp.toPx()),
            size = Size(barWidth * 2 + 12.dp.toPx(), userH + 8.dp.toPx()),
            cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx()),
            style = Stroke(width = 1.5.dp.toPx())
          )
        }
      }
    }

    // X-Axis Labels Row overlay
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(start = 36.dp, end = 16.dp, top = 16.dp, bottom = 6.dp),
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      // Y-axis tick markers indicator
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Text("100%", color = TextMutedDark, fontSize = 8.sp, fontFamily = FontFamily.Monospace)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Box(modifier = Modifier.size(7.dp).background(TitanCyan, RoundedCornerShape(2.dp)))
            Text("Actual Conversion", color = TextSecondaryDark, fontSize = 8.5.sp)
          }
          Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Box(modifier = Modifier.size(7.dp).border(1.dp, TextMutedDark, RoundedCornerShape(2.dp)))
            Text("Industry Benchmark", color = TextMutedDark, fontSize = 8.5.sp)
          }
        }
      }

      // Bottom Category Labels
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
      ) {
        metrics.forEachIndexed { idx, m ->
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.clickable { onSelectMetric(idx) }
          ) {
            Text(
              text = m.metricTitle,
              style = MaterialTheme.typography.labelSmall,
              color = if (selectedStageIndex == idx) TitanCyan else TextPrimaryDark,
              fontWeight = FontWeight.Bold,
              fontSize = 10.sp
            )
            Text(
              text = "${"%.1f".format(m.userRatePct)}% vs ${"%.0f".format(m.benchmarkRatePct)}%",
              style = MaterialTheme.typography.labelSmall,
              color = m.barColor,
              fontWeight = FontWeight.Black,
              fontSize = 9.sp
            )
          }
        }
      }
    }
  }
}

/**
 * Stage Matrix View showing status breakdown and company counts
 */
@Composable
private fun RechartsStageMatrixView(
  ledgerItems: List<CompanyApplicationLedgerItem>,
  onFilterByStatus: ((String) -> Unit)? = null
) {
  val statusBuckets = listOf("Applied", "Interviewing", "Offer", "Targeted")

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(8.dp))
      .background(ObsidianDark)
      .border(1.dp, SlateBorder, RoundedCornerShape(8.dp))
      .padding(10.dp),
    verticalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    statusBuckets.forEach { st ->
      val matching = ledgerItems.filter { it.status.equals(st, ignoreCase = true) }
      val color = when (st) {
        "Applied" -> TitanCyan
        "Interviewing" -> TitanGold
        "Offer" -> TitanEmerald
        else -> TitanIndigo
      }

      val (accentBg, accentBorder) = when (st) {
        "Applied" -> Pair(Color(0xFF0D2235), TitanCyan.copy(alpha = 0.5f))
        "Interviewing" -> Pair(Color(0xFF2B230B), TitanGold.copy(alpha = 0.5f))
        "Offer" -> Pair(Color(0xFF0C2719), TitanEmerald.copy(alpha = 0.5f))
        else -> Pair(Color(0xFF1B1530), TitanIndigo.copy(alpha = 0.5f))
      }

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(6.dp))
          .background(accentBg)
          .border(0.8.dp, accentBorder, RoundedCornerShape(6.dp))
          .clickable { onFilterByStatus?.invoke(st) }
          .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Box(
            modifier = Modifier
              .size(10.dp)
              .clip(CircleShape)
              .background(color)
          )
          Column {
            Text(
              text = st.uppercase(),
              style = MaterialTheme.typography.labelSmall,
              color = color,
              fontWeight = FontWeight.Bold,
              fontSize = 10.sp
            )
            Text(
              text = matching.joinToString(", ") { it.companyName }.ifBlank { "None recorded" },
              style = MaterialTheme.typography.bodySmall,
              color = TextSecondaryDark,
              fontSize = 9.5.sp,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
          }
        }

        Surface(
          shape = RoundedCornerShape(4.dp),
          color = color.copy(alpha = 0.2f)
        ) {
          Text(
            text = "${matching.size} Companies",
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.Black,
            fontSize = 9.sp,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
          )
        }
      }
    }
  }
}

/**
 * Recharts Scrub Details Tooltip Card
 */
@Composable
private fun RechartsScrubDetailsCard(
  selectedMode: RechartsConversionMode,
  stages: List<ConversionStageData>,
  metrics: List<ConversionRateMetric>,
  selectedIndex: Int?,
  onClearSelection: () -> Unit
) {
  if (selectedIndex == null) {
    // Default hint
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(6.dp))
        .background(SlateDarker)
        .border(0.8.dp, SlateBorder, RoundedCornerShape(6.dp))
        .padding(horizontal = 10.dp, vertical = 6.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "Tap any funnel tier or bar to inspect stage drop-off and company roster",
        style = MaterialTheme.typography.labelSmall,
        color = TextMutedDark,
        fontSize = 9.5.sp
      )
      Icon(
        imageVector = Icons.Default.FilterAlt,
        contentDescription = null,
        tint = TitanCyan,
        modifier = Modifier.size(12.dp)
      )
    }
    return
  }

  // Active Tooltip Card
  Surface(
    shape = RoundedCornerShape(8.dp),
    color = SlateDarker,
    border = BorderStroke(1.dp, TitanCyan.copy(alpha = 0.6f)),
    modifier = Modifier
      .fillMaxWidth()
      .testTag("recharts_conversion_tooltip")
  ) {
    Column(modifier = Modifier.padding(10.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        val stage = stages.getOrNull(selectedIndex)
        val metric = metrics.getOrNull(selectedIndex)

        Text(
          text = if (selectedMode == RechartsConversionMode.FUNNEL_FLOW && stage != null) {
            "STAGE INSPECTION: ${stage.stageName.uppercase()}"
          } else if (metric != null) {
            "METRIC: ${metric.metricTitle.uppercase()}"
          } else "INSPECTION",
          style = MaterialTheme.typography.labelSmall,
          color = TitanCyan,
          fontWeight = FontWeight.Black,
          fontSize = 10.sp
        )

        Text(
          text = "Close [×]",
          style = MaterialTheme.typography.labelSmall,
          color = TextMutedDark,
          fontSize = 9.sp,
          modifier = Modifier.clickable { onClearSelection() }
        )
      }

      Spacer(modifier = Modifier.height(4.dp))

      if (selectedMode == RechartsConversionMode.FUNNEL_FLOW) {
        val stage = stages.getOrNull(selectedIndex)
        if (stage != null) {
          Text(
            text = "${stage.count} companies in stage • ${"%.1f".format(stage.percentageOfTotal)}% conversion yield • ${stage.dropOffCount} stage drop-offs",
            style = MaterialTheme.typography.bodySmall,
            color = TextPrimaryDark,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = "Active Roster: ${stage.companies.joinToString(", ")}",
            style = MaterialTheme.typography.bodySmall,
            color = stage.color,
            fontSize = 10.sp
          )
        }
      } else {
        val metric = metrics.getOrNull(selectedIndex)
        if (metric != null) {
          Text(
            text = "${"%.1f".format(metric.userRatePct)}% Actual vs ${"%.1f".format(metric.benchmarkRatePct)}% Industry Benchmark (${if (metric.deltaPct >= 0) "+${"%.1f".format(metric.deltaPct)}%" else "${"%.1f".format(metric.deltaPct)}%"})",
            style = MaterialTheme.typography.bodySmall,
            color = TextPrimaryDark,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = metric.advice,
            style = MaterialTheme.typography.bodySmall,
            color = metric.barColor,
            fontSize = 10.sp
          )
        }
      }
    }
  }
}
