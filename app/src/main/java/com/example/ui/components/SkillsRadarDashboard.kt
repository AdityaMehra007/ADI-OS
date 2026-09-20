package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GapPriority
import com.example.data.model.MissingItemInsight
import com.example.data.model.MissingItemType
import com.example.data.model.SkillsRadarDimension
import com.example.data.model.SkillsRadarReport
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
import com.example.ui.viewmodel.TitanViewModel
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Skills Radar Dashboard
 *
 * Compares the user's stored resume content against the requirements of saved target job descriptions,
 * projecting competencies across 6 strategic pillars, identifying missing keywords and certifications,
 * and providing instant one-tap resume bullet injection.
 */
@Composable
fun SkillsRadarDashboard(
  viewModel: TitanViewModel,
  modifier: Modifier = Modifier
) {
  val report by viewModel.skillsRadarReport.collectAsState()
  val availableJobs = viewModel.availableTargetJobProfiles
  val availableResumes = viewModel.availableMockDocuments
  val selectedJobId by viewModel.selectedSkillsRadarJobId.collectAsState()
  val selectedResumeId by viewModel.selectedSkillsRadarResumeId.collectAsState()

  var activeDimensionIndex by remember { mutableStateOf<Int?>(null) }
  var filterTab by remember { mutableStateOf(0) } // 0: All Gaps, 1: Keywords, 2: Certifications, 3: Strengths
  var isJdSelectorExpanded by remember { mutableStateOf(false) }

  val context = LocalContext.current

  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("skills_radar_dashboard_card"),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = SlateCard),
    border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
    ) {
      // 1. Header with Badge & Actions
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
                  colors = listOf(TitanCyan.copy(alpha = 0.25f), TitanEmerald.copy(alpha = 0.2f))
                )
              ),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Radar,
              contentDescription = "Skills Radar",
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
                text = "SKILLS RADAR",
                style = MaterialTheme.typography.titleSmall,
                color = TextPrimaryDark,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp
              )
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(4.dp))
                  .background(TitanEmerald.copy(alpha = 0.15f))
                  .padding(horizontal = 5.dp, vertical = 2.dp)
              ) {
                Text(
                  text = "RESUME VS JD AUDIT",
                  fontSize = 8.5.sp,
                  fontWeight = FontWeight.ExtraBold,
                  color = TitanEmerald
                )
              }
            }
            Text(
              text = "Stored Resume vs Target Job Description Requirements",
              fontSize = 11.sp,
              color = TextSecondaryDark
            )
          }
        }

        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          IconButton(
            onClick = {
              report?.let { rep ->
                val dossier = buildSkillsRadarDossier(rep)
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                clipboard.setPrimaryClip(ClipData.newPlainText("Skills Radar Audit", dossier))
                Toast.makeText(context, "Skills Radar Dossier copied to clipboard", Toast.LENGTH_SHORT).show()
              }
            },
            modifier = Modifier.size(32.dp)
          ) {
            Icon(
              imageVector = Icons.Default.ContentCopy,
              contentDescription = "Copy Audit",
              tint = TextSecondaryDark,
              modifier = Modifier.size(16.dp)
            )
          }

          IconButton(
            onClick = {
              viewModel.refreshWeeklyCareerHealthReport()
              viewModel.showWeeklyHealthReportDialog.value = true
            },
            modifier = Modifier.size(32.dp)
          ) {
            Icon(
              imageVector = Icons.Default.PictureAsPdf,
              contentDescription = "Executive Weekly Health Report",
              tint = TitanEmerald,
              modifier = Modifier.size(16.dp)
            )
          }

          IconButton(
            onClick = { viewModel.refreshSkillsRadarReport() },
            modifier = Modifier.size(32.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Refresh,
              contentDescription = "Refresh",
              tint = TitanCyan,
              modifier = Modifier.size(16.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // 2. Select Saved Target Job Description
      Text(
        text = "TARGET SAVED JOB DESCRIPTION",
        fontSize = 9.5.sp,
        fontWeight = FontWeight.Bold,
        color = TextSecondaryDark,
        letterSpacing = 0.6.sp
      )
      Spacer(modifier = Modifier.height(6.dp))

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        availableJobs.forEach { job ->
          val isSelected = job.id == selectedJobId
          val borderColor by animateColorAsState(
            targetValue = if (isSelected) TitanCyan else SlateBorder,
            animationSpec = tween(200),
            label = "jobPillBorder"
          )
          val bgColor by animateColorAsState(
            targetValue = if (isSelected) SlateElevated else SlateDarker,
            animationSpec = tween(200),
            label = "jobPillBg"
          )

          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .background(bgColor)
              .border(1.dp, borderColor, RoundedCornerShape(8.dp))
              .clickable { viewModel.selectSkillsRadarJob(job.id) }
              .padding(horizontal = 10.dp, vertical = 7.dp)
          ) {
            Column {
              Text(
                text = job.companyName,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) TitanCyan else TextPrimaryDark
              )
              Text(
                text = job.roleTitle,
                fontSize = 9.5.sp,
                color = TextSecondaryDark,
                maxLines = 1
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // 3. Stored Resume Selector Strip
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(8.dp))
          .background(SlateDarker)
          .border(1.dp, SlateBorder, RoundedCornerShape(8.dp))
          .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Description,
            contentDescription = null,
            tint = TitanCyan,
            modifier = Modifier.size(16.dp)
          )
          Column {
            Text(
              text = "STORED RESUME AUDITED",
              fontSize = 8.5.sp,
              fontWeight = FontWeight.Bold,
              color = TextSecondaryDark
            )
            Text(
              text = report?.selectedResumeTitle ?: "Adi Shenoy - Operations Lead v3",
              fontSize = 11.5.sp,
              fontWeight = FontWeight.SemiBold,
              color = TextPrimaryDark
            )
          }
        }

        // Quick Switch Resume Pill
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(SlateElevated)
            .border(1.dp, TitanCyan.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
            .clickable {
              val currentIdx = availableResumes.indexOfFirst { it.id == selectedResumeId }
              val nextIdx = (currentIdx + 1) % availableResumes.size
              viewModel.selectSkillsRadarResume(availableResumes[nextIdx].id)
            }
            .padding(horizontal = 7.dp, vertical = 4.dp)
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            Icon(Icons.Default.Refresh, contentDescription = null, tint = TitanCyan, modifier = Modifier.size(10.dp))
            Text(text = "Switch Resume", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TitanCyan)
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // 4. Executive KPI Metric Highlights Strip
      report?.let { rep ->
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          // KPI 1: Overall Skills Match
          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(10.dp))
              .background(SlateElevated)
              .border(1.dp, SlateBorder, RoundedCornerShape(10.dp))
              .padding(10.dp)
          ) {
            Column {
              Text(text = "OVERALL FIT", fontSize = 8.5.sp, fontWeight = FontWeight.Bold, color = TextSecondaryDark)
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = "${rep.overallMatchScore}%",
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = if (rep.overallMatchScore >= 85) TitanEmerald else TitanCyan
              )
              Text(
                text = "ATS Passing Level",
                fontSize = 9.sp,
                color = TitanEmerald
              )
            }
          }

          // KPI 2: Keyword Coverage
          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(10.dp))
              .background(SlateElevated)
              .border(1.dp, SlateBorder, RoundedCornerShape(10.dp))
              .padding(10.dp)
          ) {
            Column {
              Text(text = "KEYWORDS MATCHED", fontSize = 8.5.sp, fontWeight = FontWeight.Bold, color = TextSecondaryDark)
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = "${rep.matchedKeywordsCount}/${rep.totalRequiredKeywordsCount}",
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TitanGold
              )
              Text(
                text = "${rep.missingKeywords.size} Gaps Identified",
                fontSize = 9.sp,
                color = if (rep.missingKeywords.isNotEmpty()) TitanCrimson else TitanEmerald
              )
            }
          }

          // KPI 3: Certification Coverage
          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(10.dp))
              .background(SlateElevated)
              .border(1.dp, SlateBorder, RoundedCornerShape(10.dp))
              .padding(10.dp)
          ) {
            Column {
              Text(text = "CERTIFICATIONS", fontSize = 8.5.sp, fontWeight = FontWeight.Bold, color = TextSecondaryDark)
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = "${rep.matchedCertificationsCount}/${rep.totalRequiredCertificationsCount}",
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextPrimaryDark
              )
              Text(
                text = "${rep.missingCertifications.size} Recommended",
                fontSize = 9.sp,
                color = TitanGold
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 5. THE SKILLS RADAR CANVAS (Interactive Spider / Polar Chart)
        InteractiveSkillsRadarCanvas(
          dimensions = rep.dimensions,
          overallFitScore = rep.overallMatchScore,
          selectedIndex = activeDimensionIndex,
          onSelectDimension = { idx ->
            activeDimensionIndex = if (activeDimensionIndex == idx) null else idx
          }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Legend
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.Center,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Box(modifier = Modifier.size(10.dp, 3.dp).background(TitanCyan))
            Text("Stored Resume Score", fontSize = 9.5.sp, color = TextSecondaryDark)
          }
          Spacer(modifier = Modifier.width(16.dp))
          Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Box(modifier = Modifier.size(10.dp, 2.dp).background(TitanGold))
            Text("Target JD Requirement Baseline", fontSize = 9.5.sp, color = TextSecondaryDark)
          }
        }

        // Active Dimension Deep Dive Card (When Tapped)
        AnimatedVisibility(visible = activeDimensionIndex != null) {
          activeDimensionIndex?.let { idx ->
            val dim = rep.dimensions.getOrNull(idx)
            if (dim != null) {
              DimensionInspectorCard(
                dimension = dim,
                onClose = { activeDimensionIndex = null },
                onInjectKeyword = { kw ->
                  viewModel.injectMissingKeyword(kw)
                  Toast.makeText(context, "Injected '$kw' into resume working draft! Score updated.", Toast.LENGTH_SHORT).show()
                }
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 6. Filter Tabs for Missing Keywords & Certifications
        ScrollableTabRow(
          selectedTabIndex = filterTab,
          containerColor = SlateDarker,
          edgePadding = 0.dp,
          indicator = { tabPositions ->
            if (filterTab in tabPositions.indices) {
              TabRowDefaults.SecondaryIndicator(
                Modifier.tabIndicatorOffset(tabPositions[filterTab]),
                color = TitanCyan
              )
            }
          }
        ) {
          Tab(
            selected = filterTab == 0,
            onClick = { filterTab = 0 },
            text = {
              Text(
                "All Deficits (${rep.missingKeywords.size + rep.missingCertifications.size})",
                fontSize = 10.5.sp,
                fontWeight = FontWeight.Bold,
                color = if (filterTab == 0) TitanCyan else TextSecondaryDark
              )
            }
          )
          Tab(
            selected = filterTab == 1,
            onClick = { filterTab = 1 },
            text = {
              Text(
                "Missing Keywords (${rep.missingKeywords.size})",
                fontSize = 10.5.sp,
                fontWeight = FontWeight.Bold,
                color = if (filterTab == 1) TitanCyan else TextSecondaryDark
              )
            }
          )
          Tab(
            selected = filterTab == 2,
            onClick = { filterTab = 2 },
            text = {
              Text(
                "Missing Certifications (${rep.missingCertifications.size})",
                fontSize = 10.5.sp,
                fontWeight = FontWeight.Bold,
                color = if (filterTab == 2) TitanGold else TextSecondaryDark
              )
            }
          )
          Tab(
            selected = filterTab == 3,
            onClick = { filterTab = 3 },
            text = {
              Text(
                "Verified Strengths (${rep.verifiedStrengths.size})",
                fontSize = 10.5.sp,
                fontWeight = FontWeight.Bold,
                color = if (filterTab == 3) TitanEmerald else TextSecondaryDark
              )
            }
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 7. Missing Items / Strengths List Content
        when (filterTab) {
          0 -> {
            // All Deficits
            val allDeficits = rep.missingKeywords + rep.missingCertifications
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
              allDeficits.forEach { item ->
                MissingItemCard(
                  item = item,
                  onInjectToResume = {
                    viewModel.injectMissingKeyword(item.title)
                    Toast.makeText(context, "Injected '${item.title}' into resume working draft!", Toast.LENGTH_SHORT).show()
                  },
                  onCopyBullet = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboard.setPrimaryClip(ClipData.newPlainText("Resume Bullet", item.readyToInjectBullet))
                    Toast.makeText(context, "Pre-formatted bullet copied to clipboard!", Toast.LENGTH_SHORT).show()
                  }
                )
              }
            }
          }
          1 -> {
            // Missing Keywords Only
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
              rep.missingKeywords.forEach { item ->
                MissingItemCard(
                  item = item,
                  onInjectToResume = {
                    viewModel.injectMissingKeyword(item.title)
                    Toast.makeText(context, "Injected '${item.title}' into resume working draft!", Toast.LENGTH_SHORT).show()
                  },
                  onCopyBullet = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboard.setPrimaryClip(ClipData.newPlainText("Resume Bullet", item.readyToInjectBullet))
                    Toast.makeText(context, "Pre-formatted bullet copied to clipboard!", Toast.LENGTH_SHORT).show()
                  }
                )
              }
            }
          }
          2 -> {
            // Missing Certifications Only
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
              rep.missingCertifications.forEach { item ->
                MissingItemCard(
                  item = item,
                  onInjectToResume = {
                    viewModel.injectMissingCertification(item.title)
                    Toast.makeText(context, "Added '${item.title}' to targeted credentials!", Toast.LENGTH_SHORT).show()
                  },
                  onCopyBullet = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboard.setPrimaryClip(ClipData.newPlainText("Resume Bullet", item.readyToInjectBullet))
                    Toast.makeText(context, "Certification bullet copied to clipboard!", Toast.LENGTH_SHORT).show()
                  }
                )
              }
            }
          }
          3 -> {
            // Verified Strengths
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
              rep.verifiedStrengths.forEach { strength ->
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(SlateDarker)
                    .border(1.dp, TitanEmerald.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                    .padding(10.dp),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                  Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = TitanEmerald,
                    modifier = Modifier.size(16.dp)
                  )
                  Text(
                    text = strength,
                    fontSize = 11.5.sp,
                    color = TextPrimaryDark
                  )
                }
              }
            }
          }
        }
      } ?: run {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
          contentAlignment = Alignment.Center
        ) {
          CircularProgressIndicator(color = TitanCyan, strokeWidth = 2.dp)
        }
      }
    }
  }
}

/**
 * Interactive Radar / Spider Canvas
 */
@Composable
private fun InteractiveSkillsRadarCanvas(
  dimensions: List<SkillsRadarDimension>,
  overallFitScore: Int,
  selectedIndex: Int?,
  onSelectDimension: (Int) -> Unit,
  modifier: Modifier = Modifier
) {
  val animProgress = remember { Animatable(0f) }

  LaunchedEffect(dimensions) {
    animProgress.snapTo(0f)
    animProgress.animateTo(1f, animationSpec = tween(700, easing = FastOutSlowInEasing))
  }

  Box(
    modifier = modifier
      .fillMaxWidth()
      .height(260.dp)
      .clip(RoundedCornerShape(12.dp))
      .background(SlateDarker)
      .border(1.dp, SlateBorder, RoundedCornerShape(12.dp)),
    contentAlignment = Alignment.Center
  ) {
    Canvas(
      modifier = Modifier
        .fillMaxSize()
        .pointerInput(dimensions) {
          detectTapGestures { offset ->
            val center = Offset(size.width / 2f, size.height / 2f)
            val n = dimensions.size
            if (n == 0) return@detectTapGestures
            val minDim = minOf(size.width, size.height).toFloat()
            val radius = (minDim / 2f) * 0.72f

            // Find closest axis vertex
            var closestIdx: Int? = null
            var minDist = Float.MAX_VALUE
            for (i in 0 until n) {
              val angle = (2 * PI / n * i - PI / 2).toFloat()
              val vertex = Offset(center.x + radius * cos(angle), center.y + radius * sin(angle))
              val dist = (offset - vertex).getDistance()
              if (dist < 45.dp.toPx() && dist < minDist) {
                minDist = dist
                closestIdx = i
              }
            }
            if (closestIdx != null) {
              onSelectDimension(closestIdx)
            }
          }
        }
    ) {
      val canvasW = size.width
      val canvasH = size.height
      val center = Offset(canvasW / 2f, canvasH / 2f)
      val radius = (size.minDimension / 2f) * 0.72f
      val n = dimensions.size
      if (n < 3) return@Canvas

      val levels = 5 // 20%, 40%, 60%, 80%, 100%

      // 1. Draw Concentric Polygonal Web
      for (lvl in 1..levels) {
        val lvlRatio = lvl.toFloat() / levels
        val lvlPath = Path()
        for (i in 0 until n) {
          val angle = (2 * PI / n * i - PI / 2).toFloat()
          val x = center.x + radius * lvlRatio * cos(angle)
          val y = center.y + radius * lvlRatio * sin(angle)
          if (i == 0) lvlPath.moveTo(x, y) else lvlPath.lineTo(x, y)
        }
        lvlPath.close()

        drawPath(
          path = lvlPath,
          color = SlateBorder.copy(alpha = 0.5f),
          style = Stroke(width = 1.dp.toPx())
        )
      }

      // 2. Draw Spokes from Center
      for (i in 0 until n) {
        val angle = (2 * PI / n * i - PI / 2).toFloat()
        val x = center.x + radius * cos(angle)
        val y = center.y + radius * sin(angle)
        drawLine(
          color = SlateBorder.copy(alpha = 0.7f),
          start = center,
          end = Offset(x, y),
          strokeWidth = 1.dp.toPx()
        )
      }

      // 3. Draw Target Job Requirement Polygon (Gold Dashed)
      val targetPath = Path()
      dimensions.forEachIndexed { i, dim ->
        val angle = (2 * PI / n * i - PI / 2).toFloat()
        val r = radius * dim.targetJdScore.coerceIn(0f, 1f) * animProgress.value
        val x = center.x + r * cos(angle)
        val y = center.y + r * sin(angle)
        if (i == 0) targetPath.moveTo(x, y) else targetPath.lineTo(x, y)
      }
      targetPath.close()

      drawPath(
        path = targetPath,
        color = TitanGold.copy(alpha = 0.8f),
        style = Stroke(
          width = 1.8.dp.toPx(),
          pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f))
        )
      )

      // 4. Draw Stored Resume Coverage Polygon (Cyan Fill & Stroke)
      val resumePath = Path()
      dimensions.forEachIndexed { i, dim ->
        val angle = (2 * PI / n * i - PI / 2).toFloat()
        val r = radius * dim.resumeScore.coerceIn(0f, 1f) * animProgress.value
        val x = center.x + r * cos(angle)
        val y = center.y + r * sin(angle)
        if (i == 0) resumePath.moveTo(x, y) else resumePath.lineTo(x, y)
      }
      resumePath.close()

      // Area fill
      drawPath(
        path = resumePath,
        brush = Brush.radialGradient(
          colors = listOf(TitanCyan.copy(alpha = 0.45f), TitanCyan.copy(alpha = 0.10f)),
          center = center,
          radius = radius
        )
      )

      // Outline stroke
      drawPath(
        path = resumePath,
        color = TitanCyan,
        style = Stroke(width = 2.4.dp.toPx(), cap = StrokeCap.Round)
      )

      // 5. Draw Vertex Anchor Points & Interactive Highlight
      dimensions.forEachIndexed { i, dim ->
        val angle = (2 * PI / n * i - PI / 2).toFloat()
        val r = radius * dim.resumeScore.coerceIn(0f, 1f) * animProgress.value
        val x = center.x + r * cos(angle)
        val y = center.y + r * sin(angle)

        val isSelected = (i == selectedIndex)

        if (isSelected) {
          drawCircle(
            color = TitanCyan.copy(alpha = 0.35f),
            radius = 12.dp.toPx(),
            center = Offset(x, y)
          )
        }

        drawCircle(
          color = Color(0xFF090D16),
          radius = 5.dp.toPx(),
          center = Offset(x, y)
        )
        drawCircle(
          color = if (isSelected) TitanEmerald else TitanCyan,
          radius = 3.5.dp.toPx(),
          center = Offset(x, y)
        )
      }
    }

    // Center Match Badge
    Box(
      modifier = Modifier
        .clip(CircleShape)
        .background(SlateElevated.copy(alpha = 0.95f))
        .border(1.dp, TitanCyan.copy(alpha = 0.6f), CircleShape)
        .padding(horizontal = 10.dp, vertical = 6.dp),
      contentAlignment = Alignment.Center
    ) {
      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
          text = "$overallFitScore%",
          fontSize = 13.sp,
          fontWeight = FontWeight.ExtraBold,
          color = TitanCyan
        )
        Text(
          text = "MATCH",
          fontSize = 7.5.sp,
          fontWeight = FontWeight.Bold,
          color = TextSecondaryDark
        )
      }
    }
  }
}

/**
 * Detailed Inspector card shown when an axis or dimension is tapped
 */
@Composable
private fun DimensionInspectorCard(
  dimension: SkillsRadarDimension,
  onClose: () -> Unit,
  onInjectKeyword: (String) -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .padding(top = 8.dp),
    shape = RoundedCornerShape(10.dp),
    colors = CardDefaults.cardColors(containerColor = SlateElevated),
    border = androidx.compose.foundation.BorderStroke(1.dp, TitanCyan.copy(alpha = 0.5f))
  ) {
    Column(modifier = Modifier.padding(12.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          Box(
            modifier = Modifier
              .size(8.dp)
              .clip(CircleShape)
              .background(if (dimension.isDeficit) TitanCrimson else TitanEmerald)
          )
          Text(
            text = dimension.name.uppercase(),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimaryDark
          )
        }
        IconButton(onClick = onClose, modifier = Modifier.size(24.dp)) {
          Icon(Icons.Default.ExpandLess, contentDescription = "Close", tint = TextSecondaryDark, modifier = Modifier.size(16.dp))
        }
      }

      Spacer(modifier = Modifier.height(6.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Text(
          text = "Resume Coverage: ${(dimension.resumeScore * 100).toInt()}%",
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          color = TitanCyan
        )
        Text(
          text = "Target Requirement: ${(dimension.targetJdScore * 100).toInt()}%",
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          color = TitanGold
        )
      }

      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = "Tactical Advice: ${dimension.tacticalAdvice}",
        fontSize = 10.5.sp,
        color = TextSecondaryDark
      )

      if (dimension.missingKeywords.isNotEmpty()) {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
          text = "Missing Keywords in this Dimension:",
          fontSize = 9.5.sp,
          fontWeight = FontWeight.Bold,
          color = TitanCrimson
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          dimension.missingKeywords.forEach { kw ->
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(TitanCrimson.copy(alpha = 0.15f))
                .border(1.dp, TitanCrimson.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
                .clickable { onInjectKeyword(kw) }
                .padding(horizontal = 6.dp, vertical = 3.dp)
            ) {
              Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = kw, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                Icon(Icons.Default.Add, contentDescription = "Add", tint = TitanCrimson, modifier = Modifier.size(10.dp))
              }
            }
          }
        }
      }
    }
  }
}

/**
 * Card representing a specific missing keyword or certification
 */
@Composable
private fun MissingItemCard(
  item: MissingItemInsight,
  onInjectToResume: () -> Unit,
  onCopyBullet: () -> Unit
) {
  var isExpanded by remember { mutableStateOf(false) }

  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(10.dp),
    colors = CardDefaults.cardColors(containerColor = SlateDarker),
    border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
  ) {
    Column(modifier = Modifier.padding(10.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          modifier = Modifier.weight(1f)
        ) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(4.dp))
              .background(
                if (item.type == MissingItemType.KEYWORD) TitanCyan.copy(alpha = 0.15f) else TitanGold.copy(alpha = 0.2f)
              )
              .padding(horizontal = 5.dp, vertical = 2.dp)
          ) {
            Text(
              text = if (item.type == MissingItemType.KEYWORD) "KEYWORD" else "CERTIFICATION",
              fontSize = 8.5.sp,
              fontWeight = FontWeight.ExtraBold,
              color = if (item.type == MissingItemType.KEYWORD) TitanCyan else TitanGold
            )
          }

          Text(
            text = item.title,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimaryDark
          )
        }

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(Color(item.priority.colorHex).copy(alpha = 0.15f))
            .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
          Text(
            text = item.priority.label,
            fontSize = 8.5.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(item.priority.colorHex)
          )
        }
      }

      Spacer(modifier = Modifier.height(4.dp))

      Text(
        text = item.presenceInTargetJd,
        fontSize = 10.sp,
        color = TextSecondaryDark
      )

      Spacer(modifier = Modifier.height(6.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = if (item.isAddedToDraft) "✓ Injected in Draft" else "Click to inspect & inject",
          fontSize = 9.sp,
          color = if (item.isAddedToDraft) TitanEmerald else TextMutedDark,
          modifier = Modifier.clickable { isExpanded = !isExpanded }
        )

        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          // One-tap Inject Action
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(4.dp))
              .background(TitanCyan.copy(alpha = 0.12f))
              .clickable { onInjectToResume() }
              .padding(horizontal = 6.dp, vertical = 3.dp)
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
              Icon(
                imageVector = if (item.isAddedToDraft) Icons.Default.Check else Icons.Default.Bolt,
                contentDescription = null,
                tint = TitanCyan,
                modifier = Modifier.size(10.dp)
              )
              Text(
                text = if (item.isAddedToDraft) "Injected" else "Inject Keyword",
                fontSize = 8.5.sp,
                fontWeight = FontWeight.Bold,
                color = TitanCyan
              )
            }
          }

          // Expand / Collapse details
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(4.dp))
              .background(SlateElevated)
              .clickable { isExpanded = !isExpanded }
              .padding(horizontal = 6.dp, vertical = 3.dp)
          ) {
            Icon(
              imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
              contentDescription = "Details",
              tint = TextSecondaryDark,
              modifier = Modifier.size(12.dp)
            )
          }
        }
      }

      AnimatedVisibility(visible = isExpanded) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(SlateElevated)
            .padding(8.dp)
        ) {
          Text(
            text = "PRE-WRITTEN RESUME BULLET PROPOSAL:",
            fontSize = 8.5.sp,
            fontWeight = FontWeight.Bold,
            color = TitanEmerald
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = item.readyToInjectBullet,
            fontSize = 11.sp,
            color = TextPrimaryDark,
            lineHeight = 15.sp
          )
          Spacer(modifier = Modifier.height(6.dp))

          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(4.dp))
              .background(TitanEmerald.copy(alpha = 0.15f))
              .clickable { onCopyBullet() }
              .padding(horizontal = 6.dp, vertical = 3.dp)
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              Icon(Icons.Default.ContentCopy, contentDescription = null, tint = TitanEmerald, modifier = Modifier.size(10.dp))
              Text(text = "Copy Bullet Phrasing", fontSize = 8.5.sp, fontWeight = FontWeight.Bold, color = TitanEmerald)
            }
          }
        }
      }
    }
  }
}

private fun buildSkillsRadarDossier(report: SkillsRadarReport): String {
  val sb = StringBuilder()
  sb.appendLine("═══════════════════════════════════════════════════════════")
  sb.appendLine("🎯 TITAN SKILLS RADAR • RESUME VS TARGET JD AUDIT")
  sb.appendLine("📄 Stored Resume: ${report.selectedResumeTitle}")
  sb.appendLine("🏢 Target Job Description: ${report.targetCompanyName} • ${report.selectedJobTitle}")
  sb.appendLine("═══════════════════════════════════════════════════════════")
  sb.appendLine("📊 Overall Skills Match: ${report.overallMatchScore}%")
  sb.appendLine("🔑 ATS Keyword Coverage: ${report.matchedKeywordsCount}/${report.totalRequiredKeywordsCount} (${report.keywordMatchPercent}%)")
  sb.appendLine("🎓 Certification Alignment: ${report.matchedCertificationsCount}/${report.totalRequiredCertificationsCount} (${report.certificationMatchPercent}%)")
  sb.appendLine()
  sb.appendLine("───────────────────────────────────────────────────────────")
  sb.appendLine("📐 6-DIMENSIONAL RADAR PROFILE:")
  sb.appendLine("───────────────────────────────────────────────────────────")
  report.dimensions.forEach { dim ->
    val resPct = (dim.resumeScore * 100).toInt()
    val jdPct = (dim.targetJdScore * 100).toInt()
    val status = if (dim.isDeficit) "⚠️ GAP (-${jdPct - resPct}%)" else "✅ MET (+${resPct - jdPct}%)"
    sb.appendLine("• ${dim.name}: $resPct% vs $jdPct% Target [$status]")
    sb.appendLine("  Guidance: ${dim.tacticalAdvice}")
  }
  sb.appendLine()
  sb.appendLine("───────────────────────────────────────────────────────────")
  sb.appendLine("🚨 CRITICAL MISSING KEYWORDS (${report.missingKeywords.size}):")
  sb.appendLine("───────────────────────────────────────────────────────────")
  report.missingKeywords.forEachIndexed { i, kw ->
    sb.appendLine("${i + 1}. ${kw.title} [${kw.priority.label}]")
    sb.appendLine("   • Requirement: ${kw.presenceInTargetJd}")
    sb.appendLine("   • Action: ${kw.suggestedAction}")
    sb.appendLine("   • Proposed Bullet: \"${kw.readyToInjectBullet}\"")
  }
  sb.appendLine()
  sb.appendLine("───────────────────────────────────────────────────────────")
  sb.appendLine("🎓 RECOMMENDED / MISSING CERTIFICATIONS (${report.missingCertifications.size}):")
  sb.appendLine("───────────────────────────────────────────────────────────")
  report.missingCertifications.forEachIndexed { i, cert ->
    sb.appendLine("${i + 1}. ${cert.title} [${cert.priority.label}]")
    sb.appendLine("   • Target Rationale: ${cert.presenceInTargetJd}")
    sb.appendLine("   • Action: ${cert.suggestedAction}")
  }
  sb.appendLine()
  sb.appendLine("💡 EXECUTIVE SUMMARY: ${report.executiveSummary}")
  sb.appendLine("═══════════════════════════════════════════════════════════")
  return sb.toString()
}
