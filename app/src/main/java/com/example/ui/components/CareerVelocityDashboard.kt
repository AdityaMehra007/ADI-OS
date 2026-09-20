package com.example.ui.components

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.webkit.WebSettings
import android.webkit.WebView
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
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.model.RequirementFulfillmentStatus
import com.example.data.model.RoleRequirementItem
import com.example.data.model.TargetCompanyRole
import com.example.data.model.VelocityTimelinePoint
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
import com.example.ui.theme.TitanViolet
import com.example.ui.viewmodel.TitanViewModel

enum class VelocityChartEngine(val label: String, val badge: String) {
  D3_WEB_SVG("D3.js Interactive", "D3 v7"),
  NATIVE_CANVAS("Native Compose Canvas", "60 FPS")
}

/**
 * Career Velocity Dashboard
 *
 * Uses D3 (Data-Driven Documents) inside an optimized Android WebView container
 * to visualize user progress against target company job role requirements over time,
 * with a seamless dual-engine switch to Native Compose Canvas.
 */
@Composable
fun CareerVelocityDashboard(
  viewModel: TitanViewModel,
  modifier: Modifier = Modifier
) {
  val velocityReport by viewModel.careerVelocityReport.collectAsState()
  val activeRole = velocityReport?.roles?.find { it.id == velocityReport?.activeRoleId }
    ?: velocityReport?.roles?.firstOrNull()

  var selectedEngine by remember { mutableStateOf(VelocityChartEngine.D3_WEB_SVG) }
  var isRequirementsExpanded by remember { mutableStateOf(true) }
  val context = LocalContext.current

  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("career_velocity_dashboard_card"),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = SlateCard),
    border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
    ) {
      // 1. Header Bar: Title, Engine Switcher & Quick Copy
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
                  colors = listOf(TitanCyan.copy(alpha = 0.2f), TitanGold.copy(alpha = 0.25f))
                )
              ),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Speed,
              contentDescription = "Career Velocity",
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
                text = "CAREER VELOCITY DASHBOARD",
                style = MaterialTheme.typography.titleSmall,
                color = TextPrimaryDark,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp
              )
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(4.dp))
                  .background(TitanGold.copy(alpha = 0.18f))
                  .padding(horizontal = 5.dp, vertical = 2.dp)
              ) {
                Text(
                  text = "D3.js POWERED",
                  fontSize = 8.5.sp,
                  fontWeight = FontWeight.ExtraBold,
                  color = TitanGold
                )
              }
            }
            Text(
              text = "Progress Velocity vs Role Requirements Over Time",
              fontSize = 11.sp,
              color = TextSecondaryDark
            )
          }
        }

        // Engine Toggle & Actions
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          // Engine Switcher Pill
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(SlateElevated)
              .border(1.dp, TitanCyan.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
              .clickable {
                selectedEngine = if (selectedEngine == VelocityChartEngine.D3_WEB_SVG) {
                  VelocityChartEngine.NATIVE_CANVAS
                } else {
                  VelocityChartEngine.D3_WEB_SVG
                }
              }
              .padding(horizontal = 7.dp, vertical = 4.dp)
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Layers,
                contentDescription = null,
                tint = TitanCyan,
                modifier = Modifier.size(11.dp)
              )
              Text(
                text = selectedEngine.badge,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = TitanCyan
              )
            }
          }

          IconButton(
            onClick = {
              val shareText = buildVelocityShareText(activeRole)
              val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
              clipboard.setPrimaryClip(ClipData.newPlainText("Career Velocity Dossier", shareText))
              Toast.makeText(context, "Career Velocity briefing copied to clipboard", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.size(32.dp)
          ) {
            Icon(
              imageVector = Icons.Default.ContentCopy,
              contentDescription = "Copy Velocity Briefing",
              tint = TextSecondaryDark,
              modifier = Modifier.size(16.dp)
            )
          }

          IconButton(
            onClick = { viewModel.refreshCareerVelocityData() },
            modifier = Modifier.size(32.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Refresh,
              contentDescription = "Refresh Data",
              tint = TitanCyan,
              modifier = Modifier.size(16.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // 2. Target Company & Role Selector Strip
      velocityReport?.roles?.let { roles ->
        Text(
          text = "SELECT TARGET ENTERPRISE & ROLE",
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
          roles.forEach { role ->
            val isSelected = role.id == activeRole?.id
            val borderColor by animateColorAsState(
              targetValue = if (isSelected) TitanCyan else SlateBorder,
              animationSpec = tween(200),
              label = "rolePillBorder"
            )
            val bgColor by animateColorAsState(
              targetValue = if (isSelected) SlateElevated else SlateDarker,
              animationSpec = tween(200),
              label = "rolePillBg"
            )

            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(bgColor)
                .border(1.dp, borderColor, RoundedCornerShape(8.dp))
                .clickable { viewModel.selectCareerVelocityRole(role.id) }
                .padding(horizontal = 10.dp, vertical = 7.dp)
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                Text(text = role.logoEmoji, fontSize = 15.sp)
                Column {
                  Text(
                    text = role.companyName,
                    fontSize = 11.5.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) TextPrimaryDark else TextSecondaryDark
                  )
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                  ) {
                    Text(
                      text = "${role.overallReadinessScore}% Ready",
                      fontSize = 9.5.sp,
                      fontWeight = FontWeight.SemiBold,
                      color = if (role.overallReadinessScore >= 85) TitanEmerald else TitanCyan
                    )
                    Text(
                      text = "• +${role.monthlyVelocityRate}%/mo",
                      fontSize = 9.5.sp,
                      color = TitanGold
                    )
                  }
                }
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // 3. Executive KPI Metric Strip for Active Role
      activeRole?.let { role ->
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          // KPI 1: Readiness Score
          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(10.dp))
              .background(SlateElevated)
              .border(1.dp, SlateBorder, RoundedCornerShape(10.dp))
              .padding(10.dp)
          ) {
            Column {
              Text(text = "ROLE READINESS", fontSize = 8.5.sp, fontWeight = FontWeight.Bold, color = TextSecondaryDark)
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = "${role.overallReadinessScore}%",
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TitanCyan
              )
              Text(
                text = "${role.fulfilledRequirementsCount}/${role.totalRequirementsCount} Requirements",
                fontSize = 9.sp,
                color = TitanEmerald
              )
            }
          }

          // KPI 2: Velocity Rate
          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(10.dp))
              .background(SlateElevated)
              .border(1.dp, SlateBorder, RoundedCornerShape(10.dp))
              .padding(10.dp)
          ) {
            Column {
              Text(text = "MONTHLY VELOCITY", fontSize = 8.5.sp, fontWeight = FontWeight.Bold, color = TextSecondaryDark)
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = "+${role.monthlyVelocityRate}%",
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TitanGold
              )
              Text(
                text = role.velocityPacingStatus,
                fontSize = 9.sp,
                fontWeight = FontWeight.SemiBold,
                color = TitanEmerald
              )
            }
          }

          // KPI 3: Target ETA
          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(10.dp))
              .background(SlateElevated)
              .border(1.dp, SlateBorder, RoundedCornerShape(10.dp))
              .padding(10.dp)
          ) {
            Column {
              Text(text = "TIME TO 100%", fontSize = 8.5.sp, fontWeight = FontWeight.Bold, color = TextSecondaryDark)
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = "${role.daysToFullReadiness}d",
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextPrimaryDark
              )
              Text(
                text = "Target Readiness",
                fontSize = 9.sp,
                color = TextSecondaryDark
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 4. THE VISUALIZATION ENGINE: D3 WEB SVG OR NATIVE COMPOSE CANVAS
        when (selectedEngine) {
          VelocityChartEngine.D3_WEB_SVG -> {
            D3WebViewContainer(role = role)
          }
          VelocityChartEngine.NATIVE_CANVAS -> {
            NativeComposeVelocityChart(role = role)
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 5. Next Velocity Catalyst Action Tile
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(
              Brush.horizontalGradient(
                colors = listOf(TitanIndigo.copy(alpha = 0.2f), SlateElevated)
              )
            )
            .border(1.dp, TitanIndigo.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
            .padding(12.dp)
        ) {
          Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Icon(
              imageVector = Icons.Default.RocketLaunch,
              contentDescription = null,
              tint = TitanIndigo,
              modifier = Modifier.size(18.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "NEXT VELOCITY CATALYST",
                fontSize = 9.5.sp,
                fontWeight = FontWeight.Bold,
                color = TitanIndigo,
                letterSpacing = 0.5.sp
              )
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = role.executivePitch,
                fontSize = 11.5.sp,
                color = TextPrimaryDark,
                lineHeight = 16.sp
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 6. Role Requirements Breakdown Accordion
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clickable { isRequirementsExpanded = !isRequirementsExpanded },
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Text(
              text = "JOB ROLE REQUIREMENTS BREAKDOWN (${role.requirements.size})",
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              color = TextSecondaryDark,
              letterSpacing = 0.6.sp
            )
          }
          Icon(
            imageVector = if (isRequirementsExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
            contentDescription = "Toggle Requirements",
            tint = TextSecondaryDark,
            modifier = Modifier.size(18.dp)
          )
        }

        AnimatedVisibility(visible = isRequirementsExpanded) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(top = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            role.requirements.forEach { req ->
              RequirementDetailCard(
                requirement = req,
                onBoostRequirement = {
                  viewModel.boostRequirementProgress(role.id, req.id)
                  Toast.makeText(context, "Simulated +5% acceleration on ${req.name}", Toast.LENGTH_SHORT).show()
                }
              )
            }
          }
        }
      } ?: run {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(160.dp),
          contentAlignment = Alignment.Center
        ) {
          CircularProgressIndicator(color = TitanCyan, strokeWidth = 2.dp)
        }
      }
    }
  }
}

/**
 * Hosts the interactive D3.js SVG chart inside Android WebView
 */
@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun D3WebViewContainer(
  role: TargetCompanyRole,
  modifier: Modifier = Modifier
) {
  val htmlContent = remember(role) {
    D3CareerVelocityHtmlBuilder.buildHtml(role)
  }

  Box(
    modifier = modifier
      .fillMaxWidth()
      .height(340.dp)
      .clip(RoundedCornerShape(12.dp))
      .border(1.dp, SlateBorder, RoundedCornerShape(12.dp))
      .background(ObsidianDark)
  ) {
    AndroidView(
      modifier = Modifier.fillMaxWidth().height(340.dp),
      factory = { ctx ->
        WebView(ctx).apply {
          settings.javaScriptEnabled = true
          settings.domStorageEnabled = true
          settings.cacheMode = WebSettings.LOAD_DEFAULT
          setBackgroundColor(android.graphics.Color.parseColor("#090D16"))
          loadDataWithBaseURL("https://local.titan", htmlContent, "text/html", "UTF-8", null)
        }
      },
      update = { webView ->
        webView.loadDataWithBaseURL("https://local.titan", htmlContent, "text/html", "UTF-8", null)
      }
    )
  }
}

/**
 * Native Jetpack Compose Canvas implementation of the Career Velocity chart
 */
@Composable
private fun NativeComposeVelocityChart(
  role: TargetCompanyRole,
  modifier: Modifier = Modifier
) {
  var selectedIndex by remember { mutableStateOf<Int?>(null) }
  val animProgress = remember { Animatable(0f) }

  LaunchedEffect(role.id) {
    animProgress.snapTo(0f)
    animProgress.animateTo(1f, animationSpec = tween(750, easing = FastOutSlowInEasing))
  }

  val timeline = role.timeline
  val activePoint = selectedIndex?.let { if (it in timeline.indices) timeline[it] else null }
    ?: timeline.lastOrNull()

  Card(
    modifier = modifier.fillMaxWidth(),
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = SlateDarker),
    border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
  ) {
    Column(modifier = Modifier.padding(12.dp)) {
      // Top Scrub HUD
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = activePoint?.periodLabel ?: "Scrub to inspect",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimaryDark
          )
          Text(
            text = activePoint?.milestoneAchieved ?: ("Velocity Delta: +" + (activePoint?.velocityDelta ?: 0f) + "%/mo"),
            fontSize = 9.5.sp,
            color = TitanCyan
          )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          Column(horizontalAlignment = Alignment.End) {
            Text(
              text = "User Progress",
              fontSize = 8.5.sp,
              color = TextSecondaryDark
            )
            Text(
              text = "${activePoint?.userProgressPercent?.toInt() ?: 0}%",
              fontSize = 13.sp,
              fontWeight = FontWeight.ExtraBold,
              color = TitanCyan
            )
          }
          Column(horizontalAlignment = Alignment.End) {
            Text(
              text = "Role Target",
              fontSize = 8.5.sp,
              color = TextSecondaryDark
            )
            Text(
              text = "100%",
              fontSize = 13.sp,
              fontWeight = FontWeight.ExtraBold,
              color = TitanEmerald
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Canvas Area
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(200.dp)
      ) {
        Canvas(
          modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .pointerInput(timeline) {
              detectTapGestures { offset ->
                val step = size.width / (timeline.size - 1).coerceAtLeast(1)
                val idx = (offset.x / step).toInt().coerceIn(0, timeline.size - 1)
                selectedIndex = idx
              }
            }
            .pointerInput(timeline) {
              detectDragGestures { change, _ ->
                change.consume()
                val step = size.width / (timeline.size - 1).coerceAtLeast(1)
                val idx = (change.position.x / step).toInt().coerceIn(0, timeline.size - 1)
                selectedIndex = idx
              }
            }
        ) {
          val canvasW = size.width
          val canvasH = size.height
          val paddingBottom = 24.dp.toPx()
          val paddingTop = 20.dp.toPx()
          val usableH = canvasH - paddingBottom - paddingTop
          val pointCount = timeline.size

          if (pointCount < 2) return@Canvas
          val stepX = canvasW / (pointCount - 1)

          // 100% Target Baseline Line
          val y100 = paddingTop + usableH * 0f // 100% is top of usable space
          drawLine(
            color = TitanEmerald.copy(alpha = 0.7f),
            start = Offset(0f, y100),
            end = Offset(canvasW, y100),
            strokeWidth = 1.5.dp.toPx(),
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f))
          )

          // Target Baseline Curve (Indigo dashed)
          val targetPath = Path()
          timeline.forEachIndexed { i, pt ->
            val x = i * stepX
            val norm = (pt.targetRequirementBaseline / 100f).coerceIn(0f, 1f)
            val y = paddingTop + usableH * (1f - norm * animProgress.value)
            if (i == 0) targetPath.moveTo(x, y) else targetPath.lineTo(x, y)
          }
          drawPath(
            path = targetPath,
            color = TitanIndigo.copy(alpha = 0.5f),
            style = Stroke(
              width = 1.5.dp.toPx(),
              pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f))
            )
          )

          // User Progress Area Fill & Stroke (Cyan glowing)
          val userAreaPath = Path()
          val userStrokePath = Path()
          timeline.forEachIndexed { i, pt ->
            val x = i * stepX
            val norm = (pt.userProgressPercent / 100f).coerceIn(0f, 1f)
            val y = paddingTop + usableH * (1f - norm * animProgress.value)

            if (i == 0) {
              userAreaPath.moveTo(x, paddingTop + usableH)
              userAreaPath.lineTo(x, y)
              userStrokePath.moveTo(x, y)
            } else {
              val prevX = (i - 1) * stepX
              val prevNorm = (timeline[i - 1].userProgressPercent / 100f).coerceIn(0f, 1f)
              val prevY = paddingTop + usableH * (1f - prevNorm * animProgress.value)
              val cX = (prevX + x) / 2f

              userAreaPath.cubicTo(cX, prevY, cX, y, x, y)
              userStrokePath.cubicTo(cX, prevY, cX, y, x, y)
            }
          }
          userAreaPath.lineTo((pointCount - 1) * stepX, paddingTop + usableH)
          userAreaPath.close()

          // Draw Area Gradient
          drawPath(
            path = userAreaPath,
            brush = Brush.verticalGradient(
              colors = listOf(TitanCyan.copy(alpha = 0.35f), TitanCyan.copy(alpha = 0.0f)),
              startY = paddingTop,
              endY = paddingTop + usableH
            )
          )

          // Draw Stroke
          drawPath(
            path = userStrokePath,
            color = TitanCyan,
            style = Stroke(
              width = 2.5.dp.toPx(),
              cap = StrokeCap.Round
            )
          )

          // Data Points & Milestones
          timeline.forEachIndexed { i, pt ->
            val x = i * stepX
            val norm = (pt.userProgressPercent / 100f).coerceIn(0f, 1f)
            val y = paddingTop + usableH * (1f - norm * animProgress.value)

            val isSelected = (i == selectedIndex)
            val radius = if (isSelected) 6.dp.toPx() else (if (pt.isProjection) 3.5.dp.toPx() else 4.5.dp.toPx())
            val ptColor = if (pt.isProjection) TitanIndigo else TitanCyan

            drawCircle(
              color = Color(0xFF0F172A),
              radius = radius + 2.dp.toPx(),
              center = Offset(x, y)
            )
            drawCircle(
              color = ptColor,
              radius = radius,
              center = Offset(x, y)
            )
          }
        }
      }
    }
  }
}

/**
 * Detailed requirement progress card with weight, target vs current, and boost action
 */
@Composable
private fun RequirementDetailCard(
  requirement: RoleRequirementItem,
  onBoostRequirement: () -> Unit
) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(10.dp),
    colors = CardDefaults.cardColors(containerColor = SlateDarker),
    border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(10.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = requirement.name,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimaryDark
          )
          Text(
            text = "${requirement.category} • Weight: ${requirement.weightPercent}%",
            fontSize = 9.5.sp,
            color = TextSecondaryDark
          )
        }

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(Color(requirement.status.badgeColorHex).copy(alpha = 0.15f))
            .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
          Text(
            text = "${requirement.currentProgress}% / ${requirement.requiredProficiency}% Target",
            fontSize = 9.5.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(requirement.status.badgeColorHex)
          )
        }
      }

      Spacer(modifier = Modifier.height(6.dp))

      // Custom Dual-Indicator Progress Bar
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(6.dp)
          .clip(RoundedCornerShape(3.dp))
          .background(SlateBorder)
      ) {
        // Actual Progress Fill
        Box(
          modifier = Modifier
            .fillMaxWidth(fraction = (requirement.currentProgress / 100f).coerceIn(0f, 1f))
            .height(6.dp)
            .background(
              Brush.horizontalGradient(
                colors = listOf(TitanCyan, TitanIndigo)
              )
            )
        )

        // Target Threshold Tick
        Box(
          modifier = Modifier
            .fillMaxWidth(fraction = (requirement.requiredProficiency / 100f).coerceIn(0f, 1f))
        ) {
          Box(
            modifier = Modifier
              .align(Alignment.CenterEnd)
              .width(2.dp)
              .height(6.dp)
              .background(TitanEmerald)
          )
        }
      }

      Spacer(modifier = Modifier.height(6.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Evidence: ${requirement.verificationEvidence}",
          fontSize = 9.5.sp,
          color = TextSecondaryDark,
          modifier = Modifier.weight(1f)
        )

        // Quick sprint simulation action
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(TitanCyan.copy(alpha = 0.12f))
            .clickable { onBoostRequirement() }
            .padding(horizontal = 6.dp, vertical = 3.dp)
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(3.dp)
          ) {
            Icon(Icons.Default.Bolt, contentDescription = null, tint = TitanCyan, modifier = Modifier.size(10.dp))
            Text(text = "+5% Sprint", fontSize = 8.5.sp, fontWeight = FontWeight.Bold, color = TitanCyan)
          }
        }
      }
    }
  }
}

private fun buildVelocityShareText(role: TargetCompanyRole?): String {
  if (role == null) return "Career Velocity Dossier unavailable."
  val sb = StringBuilder()
  sb.appendLine("═══════════════════════════════════════════")
  sb.appendLine("⚡ CAREER VELOCITY RADAR • D3.js DOSSIER")
  sb.appendLine("🏢 Target: ${role.companyName} • ${role.roleTitle}")
  sb.appendLine("📍 Location: ${role.location} | Tier: ${role.experienceTier}")
  sb.appendLine("💰 Target Comp: ${role.targetCompensation}")
  sb.appendLine("═══════════════════════════════════════════")
  sb.appendLine("📊 Overall Readiness: ${role.overallReadinessScore}%")
  sb.appendLine("🚀 Monthly Velocity: +${role.monthlyVelocityRate}% / mo (${role.velocityPacingStatus})")
  sb.appendLine("⏳ Estimated Readiness ETA: ${role.daysToFullReadiness} Days")
  sb.appendLine()
  sb.appendLine("───────────────────────────────────────────")
  sb.appendLine("📋 REQUIREMENTS VELOCITY MATRIX:")
  sb.appendLine("───────────────────────────────────────────")
  role.requirements.forEachIndexed { i, req ->
    sb.appendLine("${i + 1}. ${req.name} [${req.category}]")
    sb.appendLine("   • Status: ${req.status.label} (${req.currentProgress}% vs ${req.requiredProficiency}% Target)")
    sb.appendLine("   • Evidence: ${req.verificationEvidence}")
    sb.appendLine("   • Diagnostic: ${req.gapDetails}")
  }
  sb.appendLine()
  sb.appendLine("💡 Executive Pitch: ${role.executivePitch}")
  sb.appendLine("═══════════════════════════════════════════")
  return sb.toString()
}
