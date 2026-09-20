package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import com.example.util.AtsResumeDocumentExporter
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
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
import com.example.ui.theme.TitanViolet
import com.example.ui.viewmodel.TitanViewModel
import com.example.util.BulletHealthTransformation
import com.example.util.HealthCategory
import com.example.util.IndustryBenchmark
import com.example.util.ResumeHealthReport
import com.example.util.ResumeHealthScannerUtility
import com.example.util.ResumeHealthSubScore
import com.example.util.ResumeHealthSuggestion
import com.example.util.SuggestionPriority

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ResumeHealthScannerDialog(
  viewModel: TitanViewModel,
  onDismiss: () -> Unit
) {
  val isOpen by viewModel.isResumeHealthScannerOpen.collectAsState()
  if (!isOpen) return

  val activeReport by viewModel.resumeHealthReport.collectAsState()
  val isScanning by viewModel.isScanningResumeHealth.collectAsState()
  val profile by viewModel.userProfile.collectAsState()

  var inputText by remember {
    mutableStateOf(
      ResumeHealthScannerUtility.PRESET_RESUMES.first().sampleText
    )
  }
  var selectedBenchmark by remember { mutableStateOf(IndustryBenchmark.TECH_DISTRIBUTED_SYSTEMS) }
  var expandedSuggestionId by remember { mutableStateOf<String?>(null) }
  var copyToastVisible by remember { mutableStateOf(false) }

  val clipboardManager = LocalClipboardManager.current
  val context = LocalContext.current

  // Auto-scan on initial launch if report is null
  LaunchedEffect(Unit) {
    if (activeReport == null) {
      viewModel.scanResumeHealth(inputText, selectedBenchmark)
    }
  }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = Modifier
        .fillMaxWidth(0.96f)
        .fillMaxHeight(0.94f)
        .clip(RoundedCornerShape(20.dp))
        .border(1.dp, SlateBorder, RoundedCornerShape(20.dp))
        .testTag("resume_health_scanner_dialog"),
      color = ObsidianDark
    ) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(20.dp)
      ) {
        // 1. Header Bar
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(24.dp)
                  .background(TitanCyan.copy(alpha = 0.15f), RoundedCornerShape(6.dp)),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.HealthAndSafety,
                  contentDescription = null,
                  tint = TitanCyan,
                  modifier = Modifier.size(16.dp)
                )
              }
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "STANDARDS AUDIT & RECRUITER READINESS",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TitanCyan,
                letterSpacing = 1.1.sp
              )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "Resume Health Score & Audit",
              fontSize = 22.sp,
              fontWeight = FontWeight.Bold,
              color = TextPrimaryDark
            )
            Text(
              text = "Scans uploaded resume text against 2026 industry benchmarks with actionable fixes",
              fontSize = 12.sp,
              color = TextMutedDark
            )
          }

          IconButton(
            onClick = onDismiss,
            modifier = Modifier
              .size(36.dp)
              .clip(CircleShape)
              .background(SlateElevated)
              .testTag("close_resume_health_dialog_button")
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Close",
              tint = TextSecondaryDark,
              modifier = Modifier.size(18.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 2. Scrollable Body
        Column(
          modifier = Modifier
            .weight(1f)
            .verticalScroll(rememberScrollState())
        ) {
          // Input Section: Text Upload / Paste & Presets
          Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = SlateCard),
            border = BorderStroke(1.dp, SlateBorder),
            shape = RoundedCornerShape(14.dp)
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "UPLOAD OR PASTE RESUME TEXT",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = TitanCyan,
                  letterSpacing = 0.8.sp
                )

                // Quick Load Profile Data button
                if (profile != null) {
                  Row(
                    modifier = Modifier
                      .clip(RoundedCornerShape(6.dp))
                      .background(TitanIndigo.copy(alpha = 0.2f))
                      .clickable {
                        val p = profile
                        val profileText = buildString {
                          appendLine(p?.name?.uppercase() ?: "CANDIDATE")
                          appendLine("${p?.location ?: "Bengaluru, India"} • candidate@career.io")
                          appendLine("")
                          appendLine("PROFESSIONAL SUMMARY")
                          appendLine("${p?.careerStage ?: "Senior Professional"} specializing in ${p?.targetRoles ?: "Technology & Strategy"}.")
                          appendLine("")
                          appendLine("CORE TARGET DOMAINS & SKILLS")
                          appendLine(p?.interests ?: "Distributed Systems, Architecture, Strategy, Operations")
                          appendLine("")
                          appendLine("EDUCATION")
                          appendLine("${p?.educationDegree ?: "B.Tech in Computer Science"} • ${p?.university ?: "University"}")
                        }
                        inputText = profileText
                      }
                      .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Icon(
                      imageVector = Icons.Default.Description,
                      contentDescription = null,
                      tint = TitanCyan,
                      modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                      text = "Load My Profile",
                      fontSize = 11.sp,
                      color = TitanCyan,
                      fontWeight = FontWeight.SemiBold
                    )
                  }
                }
              }

              Spacer(modifier = Modifier.height(8.dp))

              // Preset Samples
              Text(
                text = "Load Sample Test Case:",
                fontSize = 10.sp,
                color = TextMutedDark
              )
              Spacer(modifier = Modifier.height(4.dp))
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                ResumeHealthScannerUtility.PRESET_RESUMES.forEach { preset ->
                  val isSelected = inputText == preset.sampleText
                  Box(
                    modifier = Modifier
                      .clip(RoundedCornerShape(8.dp))
                      .background(if (isSelected) TitanCyan.copy(alpha = 0.2f) else SlateElevated)
                      .border(
                        1.dp,
                        if (isSelected) TitanCyan else SlateBorder,
                        RoundedCornerShape(8.dp)
                      )
                      .clickable {
                        inputText = preset.sampleText
                        selectedBenchmark = preset.targetBenchmark
                        viewModel.scanResumeHealth(preset.sampleText, preset.targetBenchmark)
                      }
                      .padding(horizontal = 10.dp, vertical = 6.dp)
                  ) {
                    Text(
                      text = preset.title,
                      fontSize = 11.sp,
                      color = if (isSelected) TitanCyan else TextSecondaryDark,
                      fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                  }
                }
              }

              Spacer(modifier = Modifier.height(10.dp))

              // Industry Benchmark Selector
              Text(
                text = "Current Industry Benchmark:",
                fontSize = 10.sp,
                color = TextMutedDark
              )
              Spacer(modifier = Modifier.height(4.dp))
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                IndustryBenchmark.values().forEach { benchmark ->
                  val isSelected = selectedBenchmark == benchmark
                  Box(
                    modifier = Modifier
                      .clip(RoundedCornerShape(8.dp))
                      .background(if (isSelected) TitanEmerald.copy(alpha = 0.2f) else SlateElevated)
                      .border(
                        1.dp,
                        if (isSelected) TitanEmerald else SlateBorder,
                        RoundedCornerShape(8.dp)
                      )
                      .clickable {
                        selectedBenchmark = benchmark
                      }
                      .padding(horizontal = 10.dp, vertical = 6.dp)
                  ) {
                    Text(
                      text = benchmark.displayName,
                      fontSize = 11.sp,
                      color = if (isSelected) TitanEmerald else TextSecondaryDark,
                      fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                  }
                }
              }

              Spacer(modifier = Modifier.height(10.dp))

              // Outlined Text Field
              OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                modifier = Modifier
                  .fillMaxWidth()
                  .height(140.dp)
                  .testTag("resume_input_text_field"),
                placeholder = {
                  Text(
                    text = "Paste resume plaintext or markdown here...",
                    color = TextMutedDark,
                    fontSize = 12.sp
                  )
                },
                colors = OutlinedTextFieldDefaults.colors(
                  focusedBorderColor = TitanCyan,
                  unfocusedBorderColor = SlateBorder,
                  focusedTextColor = TextPrimaryDark,
                  unfocusedTextColor = TextSecondaryDark,
                  cursorColor = TitanCyan
                ),
                shape = RoundedCornerShape(8.dp),
                textStyle = MaterialTheme.typography.bodySmall.copy(
                  fontSize = 11.sp,
                  fontFamily = FontFamily.Monospace,
                  lineHeight = 15.sp
                )
              )

              Spacer(modifier = Modifier.height(10.dp))

              // Action Trigger Row
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                val words = inputText.trim().split("\\s+".toRegex()).filter { it.isNotBlank() }.size
                Text(
                  text = "$words words • ~${(words / 500f).coerceAtLeast(0.5f)} pages",
                  fontSize = 11.sp,
                  color = TextMutedDark,
                  fontFamily = FontFamily.Monospace
                )

                Button(
                  onClick = {
                    viewModel.scanResumeHealth(inputText, selectedBenchmark)
                  },
                  colors = ButtonDefaults.buttonColors(containerColor = TitanCyan),
                  shape = RoundedCornerShape(8.dp),
                  enabled = !isScanning && inputText.isNotBlank(),
                  modifier = Modifier.testTag("run_health_scan_button")
                ) {
                  if (isScanning) {
                    CircularProgressIndicator(
                      modifier = Modifier.size(16.dp),
                      color = ObsidianDark,
                      strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Analyzing Standards...", color = ObsidianDark, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                  } else {
                    Icon(
                      imageVector = Icons.Default.Speed,
                      contentDescription = null,
                      tint = ObsidianDark,
                      modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                      text = "Scan Resume Health",
                      color = ObsidianDark,
                      fontSize = 12.sp,
                      fontWeight = FontWeight.Bold
                    )
                  }
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(16.dp))

          // 3. Scan Results Section
          activeReport?.let { report ->
            ResumeHealthReportCard(
              report = report,
              expandedSuggestionId = expandedSuggestionId,
              onToggleSuggestion = { id ->
                expandedSuggestionId = if (expandedSuggestionId == id) null else id
              },
              onCopyText = { text ->
                clipboardManager.setText(AnnotatedString(text))
                copyToastVisible = true
              }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 4. 100% ATS-Compliant Document Exporter
            Card(
              modifier = Modifier
                .fillMaxWidth()
                .testTag("ats_resume_exporter_card"),
              colors = CardDefaults.cardColors(containerColor = SlateCard),
              shape = RoundedCornerShape(16.dp),
              border = BorderStroke(1.dp, SlateBorder)
            ) {
              Column(modifier = Modifier.padding(16.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                      modifier = Modifier
                        .size(24.dp)
                        .background(TitanEmerald.copy(alpha = 0.15f), RoundedCornerShape(6.dp)),
                      contentAlignment = Alignment.Center
                    ) {
                      Icon(Icons.Default.Description, contentDescription = null, tint = TitanEmerald, modifier = Modifier.size(15.dp))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                      Text(
                        text = "100% ATS-COMPLIANT CLEAN DOCUMENT EXPORT",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TitanEmerald,
                        letterSpacing = 1.sp
                      )
                      Text(
                        text = "Standard single-column formatted resume with optimized metrics",
                        fontSize = 11.sp,
                        color = TextMutedDark
                      )
                    }
                  }
                }

                Spacer(modifier = Modifier.height(12.dp))

                val formattedAtsResume = remember(profile, report) {
                  val transformedBullets = report.bulletTransformations.map { it.improvedBullet }
                  AtsResumeDocumentExporter.formatAtsDocument(
                    profile = profile,
                    targetRole = profile?.targetRoles ?: "Staff Software Architect",
                    customBullets = transformedBullets
                  )
                }

                Box(
                  modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(ObsidianDark)
                    .border(1.dp, SlateBorder, RoundedCornerShape(8.dp))
                    .verticalScroll(rememberScrollState())
                    .padding(10.dp)
                ) {
                  Text(
                    text = formattedAtsResume,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    color = TextPrimaryDark,
                    lineHeight = 14.sp
                  )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                  Button(
                    onClick = {
                      clipboardManager.setText(AnnotatedString(formattedAtsResume))
                      copyToastVisible = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TitanEmerald),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                  ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null, tint = ObsidianDark, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Copy ATS Resume", color = ObsidianDark, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                  }

                  Button(
                    onClick = {
                      AtsResumeDocumentExporter.shareAtsDocument(context, formattedAtsResume)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TitanCyan),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                  ) {
                    Icon(Icons.Default.Share, contentDescription = null, tint = ObsidianDark, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Share / Export", color = ObsidianDark, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                  }
                }
              }
            }
          }
        }

        // Copy Toast Notification
        AnimatedVisibility(
          visible = copyToastVisible,
          enter = fadeIn() + expandVertically(),
          exit = fadeOut() + shrinkVertically()
        ) {
          LaunchedEffect(copyToastVisible) {
            kotlinx.coroutines.delay(2000)
            copyToastVisible = false
          }
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .padding(top = 8.dp)
              .clip(RoundedCornerShape(8.dp))
              .background(TitanEmerald)
              .padding(vertical = 8.dp, horizontal = 14.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "Copied to clipboard!",
              color = ObsidianDark,
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }
    }
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ResumeHealthReportCard(
  report: ResumeHealthReport,
  expandedSuggestionId: String?,
  onToggleSuggestion: (String) -> Unit,
  onCopyText: (String) -> Unit
) {
  Column(modifier = Modifier.fillMaxWidth()) {
    // A. Main Health Score Banner
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .testTag("resume_health_score_banner"),
      colors = CardDefaults.cardColors(containerColor = SlateCard),
      border = BorderStroke(1.dp, SlateBorder),
      shape = RoundedCornerShape(16.dp)
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Radial Circular Score Gauge
          Box(
            modifier = Modifier.size(110.dp),
            contentAlignment = Alignment.Center
          ) {
            val animatedScore by animateFloatAsState(
              targetValue = report.overallHealthScore / 100f,
              animationSpec = tween(durationMillis = 900, easing = FastOutSlowInEasing),
              label = "radial_score"
            )

            Canvas(modifier = Modifier.fillMaxSize()) {
              val strokeWidth = 9.dp.toPx()
              val radius = (size.minDimension - strokeWidth) / 2
              val center = Offset(size.width / 2, size.height / 2)

              // Track
              drawCircle(
                color = SlateBorder.copy(alpha = 0.6f),
                radius = radius,
                center = center,
                style = Stroke(strokeWidth)
              )

              // Gauge Progress
              drawArc(
                brush = Brush.sweepGradient(
                  listOf(TitanCyan, TitanEmerald, TitanGold, TitanCyan)
                ),
                startAngle = -90f,
                sweepAngle = 360f * animatedScore,
                useCenter = false,
                topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
                size = Size(size.width - strokeWidth, size.height - strokeWidth),
                style = Stroke(strokeWidth, cap = StrokeCap.Round)
              )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                text = "${report.overallHealthScore}",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = when {
                  report.overallHealthScore >= 85 -> TitanEmerald
                  report.overallHealthScore >= 70 -> TitanCyan
                  else -> TitanGold
                }
              )
              Text(
                text = "GRADE ${report.letterGrade}",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondaryDark
              )
            }
          }

          Spacer(modifier = Modifier.width(16.dp))

          // Score Diagnosis & Verdict
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "OVERALL RESUME HEALTH",
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              color = TextMutedDark,
              letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = report.verdict,
              fontSize = 15.sp,
              fontWeight = FontWeight.Bold,
              color = when {
                report.overallHealthScore >= 85 -> TitanEmerald
                report.overallHealthScore >= 70 -> TitanCyan
                else -> TitanGold
              }
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "Evaluated against current ${report.targetBenchmark.displayName} hiring bars & Google XYZ impact criteria.",
              fontSize = 11.sp,
              color = TextMutedDark,
              lineHeight = 15.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Quick Stats Strip
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Surface(
                color = SlateElevated,
                shape = RoundedCornerShape(6.dp),
                border = BorderStroke(1.dp, SlateBorder)
              ) {
                Text(
                  text = "${report.quantifiedRatioPercent}% Quantified",
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  color = TitanEmerald,
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                )
              }

              Surface(
                color = SlateElevated,
                shape = RoundedCornerShape(6.dp),
                border = BorderStroke(1.dp, SlateBorder)
              ) {
                Text(
                  text = "${report.powerVerbsFound.size} Power Verbs",
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  color = TitanCyan,
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                )
              }

              if (report.clichesFound.isNotEmpty()) {
                Surface(
                  color = TitanGold.copy(alpha = 0.15f),
                  shape = RoundedCornerShape(6.dp),
                  border = BorderStroke(1.dp, TitanGold.copy(alpha = 0.3f))
                ) {
                  Text(
                    text = "${report.clichesFound.size} Buzzwords",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = TitanGold,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                  )
                }
              }
            }
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // B. Sub-Score Breakdown (5 Core Pillars)
    Text(
      text = "CORE PILLAR DIAGNOSTICS",
      fontSize = 11.sp,
      fontWeight = FontWeight.Bold,
      color = TitanCyan,
      letterSpacing = 1.sp
    )
    Spacer(modifier = Modifier.height(8.dp))

    report.subScores.forEach { sub ->
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = ObsidianDark),
        border = BorderStroke(1.dp, SlateBorder),
        shape = RoundedCornerShape(10.dp)
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = sub.category.title,
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = TextPrimaryDark
            )
            Text(
              text = "${sub.score}/100 • ${sub.status}",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = when (sub.status) {
                "EXCELLENT" -> TitanEmerald
                "HEALTHY" -> TitanCyan
                "NEEDS_ATTENTION" -> TitanGold
                else -> Color(0xFFF87171)
              }
            )
          }
          Spacer(modifier = Modifier.height(6.dp))

          // Progress Bar
          LinearProgressIndicator(
            progress = { (sub.score / 100f).coerceIn(0f, 1f) },
            modifier = Modifier
              .fillMaxWidth()
              .height(5.dp)
              .clip(RoundedCornerShape(3.dp)),
            color = when (sub.status) {
              "EXCELLENT" -> TitanEmerald
              "HEALTHY" -> TitanCyan
              "NEEDS_ATTENTION" -> TitanGold
              else -> Color(0xFFF87171)
            },
            trackColor = SlateBorder
          )

          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = sub.summary,
            fontSize = 11.sp,
            color = TextMutedDark
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // C. Industry Benchmark Keywords Audit
    Card(
      modifier = Modifier.fillMaxWidth(),
      colors = CardDefaults.cardColors(containerColor = SlateCard),
      border = BorderStroke(1.dp, SlateBorder),
      shape = RoundedCornerShape(12.dp)
    ) {
      Column(modifier = Modifier.padding(14.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "INDUSTRY KEYWORD RELEVANCE",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = TitanEmerald,
            letterSpacing = 0.8.sp
          )
          Text(
            text = "${report.detectedIndustryKeywords.size}/${report.targetBenchmark.targetKeywords.size} Matched",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = TextSecondaryDark
          )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Matched Keywords
        if (report.detectedIndustryKeywords.isNotEmpty()) {
          Text(text = "Detected in Resume:", fontSize = 10.sp, color = TextMutedDark)
          Spacer(modifier = Modifier.height(4.dp))
          FlowRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            report.detectedIndustryKeywords.forEach { kw ->
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(6.dp))
                  .background(TitanEmerald.copy(alpha = 0.15f))
                  .border(1.dp, TitanEmerald.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                  .padding(horizontal = 6.dp, vertical = 3.dp)
              ) {
                Text(text = kw, color = TitanEmerald, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
              }
            }
          }
        }

        // Missing Keywords Gaps
        if (report.missingIndustryKeywords.isNotEmpty()) {
          Spacer(modifier = Modifier.height(8.dp))
          Text(text = "Missing High-Impact Industry Keywords:", fontSize = 10.sp, color = TextMutedDark)
          Spacer(modifier = Modifier.height(4.dp))
          FlowRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            report.missingIndustryKeywords.take(8).forEach { kw ->
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(6.dp))
                  .background(TitanGold.copy(alpha = 0.12f))
                  .border(1.dp, TitanGold.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                  .padding(horizontal = 6.dp, vertical = 3.dp)
              ) {
                Text(text = "+ $kw", color = TitanGold, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
              }
            }
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // D. Actionable Improvement Suggestions
    Text(
      text = "ACTIONABLE IMPROVEMENT SUGGESTIONS",
      fontSize = 11.sp,
      fontWeight = FontWeight.Bold,
      color = TitanCyan,
      letterSpacing = 1.sp
    )
    Spacer(modifier = Modifier.height(4.dp))
    Text(
      text = "Prioritized recommendations ranked by health score potential boost",
      fontSize = 11.sp,
      color = TextMutedDark
    )
    Spacer(modifier = Modifier.height(8.dp))

    report.suggestions.forEach { suggestion ->
      val isExpanded = expandedSuggestionId == suggestion.id

      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 4.dp)
          .clickable { onToggleSuggestion(suggestion.id) },
        colors = CardDefaults.cardColors(containerColor = ObsidianDark),
        border = BorderStroke(
          1.dp,
          when (suggestion.priority) {
            SuggestionPriority.CRITICAL -> Color(0xFFF87171).copy(alpha = 0.6f)
            SuggestionPriority.HIGH -> TitanGold.copy(alpha = 0.5f)
            else -> SlateBorder
          }
        ),
        shape = RoundedCornerShape(12.dp)
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.weight(1f)
            ) {
              Surface(
                color = when (suggestion.priority) {
                  SuggestionPriority.CRITICAL -> Color(0xFFF87171).copy(alpha = 0.2f)
                  SuggestionPriority.HIGH -> TitanGold.copy(alpha = 0.2f)
                  SuggestionPriority.MEDIUM -> TitanCyan.copy(alpha = 0.2f)
                  SuggestionPriority.QUICK_WIN -> TitanEmerald.copy(alpha = 0.2f)
                },
                shape = RoundedCornerShape(6.dp)
              ) {
                Text(
                  text = suggestion.priority.label,
                  color = when (suggestion.priority) {
                    SuggestionPriority.CRITICAL -> Color(0xFFF87171)
                    SuggestionPriority.HIGH -> TitanGold
                    SuggestionPriority.MEDIUM -> TitanCyan
                    SuggestionPriority.QUICK_WIN -> TitanEmerald
                  },
                  fontSize = 9.sp,
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }

              Spacer(modifier = Modifier.width(8.dp))

              Text(
                text = "+${suggestion.scorePotentialBonus} pts",
                color = TitanEmerald,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
              )
            }

            Icon(
              imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
              contentDescription = null,
              tint = TextSecondaryDark,
              modifier = Modifier.size(18.dp)
            )
          }

          Spacer(modifier = Modifier.height(6.dp))

          Text(
            text = suggestion.title,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimaryDark
          )

          Spacer(modifier = Modifier.height(4.dp))

          Text(
            text = suggestion.diagnosis,
            fontSize = 11.sp,
            color = TextMutedDark,
            lineHeight = 15.sp
          )

          // Expandable Actionable Fix with Before/After
          AnimatedVisibility(
            visible = isExpanded,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
          ) {
            Column(modifier = Modifier.padding(top = 10.dp)) {
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(8.dp))
                  .background(SlateElevated)
                  .border(1.dp, SlateBorder, RoundedCornerShape(8.dp))
                  .padding(10.dp)
              ) {
                Column {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                      imageVector = Icons.Default.Lightbulb,
                      contentDescription = null,
                      tint = TitanGold,
                      modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                      text = "ACTIONABLE FIX",
                      fontSize = 10.sp,
                      fontWeight = FontWeight.Bold,
                      color = TitanGold
                    )
                  }
                  Spacer(modifier = Modifier.height(4.dp))
                  Text(
                    text = suggestion.actionableFix,
                    fontSize = 11.sp,
                    color = TextPrimaryDark,
                    lineHeight = 15.sp
                  )
                }
              }

              // Before / After Exemplar
              if (suggestion.exampleBefore != null && suggestion.exampleAfter != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Column(
                  modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(ObsidianDark)
                    .border(1.dp, SlateBorder, RoundedCornerShape(8.dp))
                    .padding(10.dp)
                ) {
                  Text(text = "❌ BEFORE (Weak / Passive):", fontSize = 10.sp, color = Color(0xFFF87171), fontWeight = FontWeight.Bold)
                  Text(text = suggestion.exampleBefore, fontSize = 11.sp, color = TextSecondaryDark, fontFamily = FontFamily.Monospace)
                  Spacer(modifier = Modifier.height(6.dp))
                  Text(text = "✅ AFTER (Quantified XYZ Impact):", fontSize = 10.sp, color = TitanEmerald, fontWeight = FontWeight.Bold)
                  Text(text = suggestion.exampleAfter, fontSize = 11.sp, color = TextPrimaryDark, fontFamily = FontFamily.Monospace)
                }
              }
            }
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // E. Bullet Transformation Laboratory
    if (report.bulletTransformations.isNotEmpty()) {
      Text(
        text = "GOOGLE XYZ BULLET TRANSFORMATIONS",
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = TitanEmerald,
        letterSpacing = 1.sp
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = "Tailored metric-driven rewrites using your scanned text",
        fontSize = 11.sp,
        color = TextMutedDark
      )
      Spacer(modifier = Modifier.height(8.dp))

      report.bulletTransformations.forEach { pair ->
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
          colors = CardDefaults.cardColors(containerColor = SlateCard),
          border = BorderStroke(1.dp, SlateBorder),
          shape = RoundedCornerShape(12.dp)
        ) {
          Column(modifier = Modifier.padding(12.dp)) {
            Text(
              text = "ORIGINAL BULLET:",
              fontSize = 9.sp,
              fontWeight = FontWeight.Bold,
              color = TextMutedDark
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = pair.originalBullet,
              fontSize = 11.sp,
              color = TextSecondaryDark,
              fontFamily = FontFamily.Monospace
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "REWRITTEN POWER BULLET (XYZ FORMULA):",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = TitanEmerald
              )
              IconButton(
                onClick = { onCopyText(pair.improvedBullet) },
                modifier = Modifier.size(24.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.ContentCopy,
                  contentDescription = "Copy rewritten bullet",
                  tint = TitanCyan,
                  modifier = Modifier.size(14.dp)
                )
              }
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = pair.improvedBullet,
              fontSize = 11.sp,
              fontWeight = FontWeight.SemiBold,
              color = TextPrimaryDark,
              fontFamily = FontFamily.Monospace
            )

            Spacer(modifier = Modifier.height(6.dp))

            Surface(
              color = TitanEmerald.copy(alpha = 0.12f),
              shape = RoundedCornerShape(4.dp)
            ) {
              Text(
                text = "Injected: ${pair.metricInjected}",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = TitanEmerald,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = pair.reasoning,
              fontSize = 10.sp,
              color = TextMutedDark
            )
          }
        }
      }
    }
  }
}
