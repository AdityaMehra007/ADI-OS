package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.BulletOptimizationPair
import com.example.data.model.MockResumeDocument
import com.example.data.model.ParsedResumeDocument
import com.example.data.model.ResumeOptimizationFeedback
import com.example.data.model.SectionOptimizationCritique
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
import com.example.ui.viewmodel.TitanViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ResumeOptimizationLabDialog(
  viewModel: TitanViewModel,
  onDismiss: () -> Unit
) {
  val isOpen by viewModel.isResumeOptimizerOpen.collectAsState()
  if (!isOpen) return

  val availableDocs = viewModel.availableMockDocuments
  val selectedDoc by viewModel.selectedMockDocument.collectAsState()
  val parsedDoc by viewModel.parsedResumeDocument.collectAsState()
  val feedback by viewModel.resumeOptimizationFeedback.collectAsState()
  val isParsing by viewModel.isParsingDocument.collectAsState()
  val isOptimizing by viewModel.isOptimizingResumeWithGemini.collectAsState()
  val targetRole by viewModel.optimizerTargetRole.collectAsState()
  val targetCompany by viewModel.optimizerTargetCompany.collectAsState()

  var isCustomInputMode by remember { mutableStateOf(false) }
  var customDocName by remember { mutableStateOf("My_Custom_Resume_v1.txt") }
  var customDocText by remember { mutableStateOf("") }
  var isStructureExpanded by remember { mutableStateOf(false) }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = Modifier
        .fillMaxWidth(0.95f)
        .fillMaxHeight(0.92f)
        .clip(RoundedCornerShape(20.dp))
        .border(1.dp, SlateBorder, RoundedCornerShape(20.dp))
        .testTag("resume_optimization_dialog"),
      color = ObsidianDark
    ) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(20.dp)
      ) {
        // Dialog Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(10.dp)
                  .clip(CircleShape)
                  .background(TitanCyan)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "GEMINI 3.5 FLASH • DOCUMENT PARSER & OPTIMIZER",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TitanCyan,
                letterSpacing = 1.1.sp
              )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "Resume Optimization Lab",
              fontSize = 22.sp,
              fontWeight = FontWeight.Bold,
              color = TextPrimaryDark
            )
          }

          IconButton(
            onClick = onDismiss,
            modifier = Modifier
              .size(36.dp)
              .clip(CircleShape)
              .background(SlateElevated)
              .testTag("close_resume_optimizer_button")
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Close Resume Optimizer",
              tint = TextSecondaryDark,
              modifier = Modifier.size(18.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Scrollable Content
        Column(
          modifier = Modifier
            .weight(1f)
            .verticalScroll(rememberScrollState())
        ) {
          // Section 1: Ingest Local Document (Mock Source Switcher)
          Text(
            text = "1. INGEST LOCAL DOCUMENT DATA",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = TextSecondaryDark,
            letterSpacing = 0.8.sp
          )
          Spacer(modifier = Modifier.height(8.dp))

          // Mock Document Selector Chips
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            availableDocs.forEach { doc ->
              val isSelected = !isCustomInputMode && selectedDoc.id == doc.id
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(10.dp))
                  .background(if (isSelected) TitanCyan.copy(alpha = 0.15f) else SlateCard)
                  .border(
                    width = 1.dp,
                    color = if (isSelected) TitanCyan else SlateBorder,
                    shape = RoundedCornerShape(10.dp)
                  )
                  .clickable {
                    isCustomInputMode = false
                    viewModel.selectAndParseMockDocument(doc)
                  }
                  .padding(horizontal = 12.dp, vertical = 8.dp)
                  .testTag("mock_doc_chip_${doc.id}")
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    imageVector = Icons.Default.Description,
                    contentDescription = null,
                    tint = if (isSelected) TitanCyan else TextMutedDark,
                    modifier = Modifier.size(16.dp)
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Column {
                    Text(
                      text = doc.fileName,
                      fontSize = 12.sp,
                      fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                      color = if (isSelected) TitanCyan else TextPrimaryDark
                    )
                    Text(
                      text = doc.fileType,
                      fontSize = 10.sp,
                      color = TextMutedDark
                    )
                  }
                }
              }
            }

            // Custom Ingestion Mode Chip
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(if (isCustomInputMode) TitanGold.copy(alpha = 0.15f) else SlateCard)
                .border(
                  width = 1.dp,
                  color = if (isCustomInputMode) TitanGold else SlateBorder,
                  shape = RoundedCornerShape(10.dp)
                )
                .clickable { isCustomInputMode = true }
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .testTag("custom_doc_chip")
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.UploadFile,
                  contentDescription = null,
                  tint = if (isCustomInputMode) TitanGold else TextMutedDark,
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "Custom Text Ingestion",
                  fontSize = 12.sp,
                  fontWeight = if (isCustomInputMode) FontWeight.Bold else FontWeight.Medium,
                  color = if (isCustomInputMode) TitanGold else TextPrimaryDark
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Custom Input Area (Visible if custom input mode selected)
          AnimatedVisibility(visible = isCustomInputMode) {
            Card(
              modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
              colors = CardDefaults.cardColors(containerColor = SlateCard),
              shape = RoundedCornerShape(12.dp),
              border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
            ) {
              Column(modifier = Modifier.padding(14.dp)) {
                Text(
                  text = "Custom Resume Ingestion",
                  fontSize = 13.sp,
                  fontWeight = FontWeight.Bold,
                  color = TextPrimaryDark
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                  value = customDocName,
                  onValueChange = { customDocName = it },
                  label = { Text("File Name", fontSize = 11.sp) },
                  modifier = Modifier.fillMaxWidth(),
                  colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TitanCyan,
                    unfocusedBorderColor = SlateBorder,
                    focusedTextColor = TextPrimaryDark,
                    unfocusedTextColor = TextPrimaryDark
                  ),
                  singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                  value = customDocText,
                  onValueChange = { customDocText = it },
                  label = { Text("Paste Resume Content (Plaintext / Markdown)", fontSize = 11.sp) },
                  modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                  colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TitanCyan,
                    unfocusedBorderColor = SlateBorder,
                    focusedTextColor = TextPrimaryDark,
                    unfocusedTextColor = TextPrimaryDark
                  )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                  onClick = {
                    if (customDocText.isNotBlank()) {
                      viewModel.parseCustomDocument(customDocName, customDocText)
                    }
                  },
                  colors = ButtonDefaults.buttonColors(containerColor = TitanGold),
                  shape = RoundedCornerShape(8.dp),
                  modifier = Modifier.align(Alignment.End)
                ) {
                  Text(
                    text = "Parse Custom Document",
                    color = ObsidianDark,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                  )
                }
              }
            }
          }

          // Document Metadata & Ingestion Overview Card
          if (isParsing) {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .height(90.dp)
                .background(SlateCard, RoundedCornerShape(12.dp)),
              contentAlignment = Alignment.Center
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                CircularProgressIndicator(
                  modifier = Modifier.size(20.dp),
                  color = TitanCyan,
                  strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                  text = "Parsing document structure & telemetry...",
                  fontSize = 12.sp,
                  color = TextSecondaryDark
                )
              }
            }
          } else {
            parsedDoc?.let { doc ->
              ParsedDocumentHeaderCard(
                doc = doc,
                scenarioDesc = if (!isCustomInputMode) selectedDoc.description else "Custom ingested resume data for constructive evaluation.",
                isExpanded = isStructureExpanded,
                onToggleExpand = { isStructureExpanded = !isStructureExpanded }
              )
            }
          }

          Spacer(modifier = Modifier.height(16.dp))

          // Section 2: Target Calibration
          Text(
            text = "2. TARGET CALIBRATION & BENCHMARK",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = TextSecondaryDark,
            letterSpacing = 0.8.sp
          )
          Spacer(modifier = Modifier.height(8.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            OutlinedTextField(
              value = targetRole,
              onValueChange = { viewModel.optimizerTargetRole.value = it },
              label = { Text("Target Role", fontSize = 11.sp) },
              modifier = Modifier.weight(1f),
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = TitanCyan,
                unfocusedBorderColor = SlateBorder,
                focusedTextColor = TextPrimaryDark,
                unfocusedTextColor = TextPrimaryDark
              ),
              singleLine = true
            )

            OutlinedTextField(
              value = targetCompany,
              onValueChange = { viewModel.optimizerTargetCompany.value = it },
              label = { Text("Target Company", fontSize = 11.sp) },
              modifier = Modifier.weight(1f),
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = TitanCyan,
                unfocusedBorderColor = SlateBorder,
                focusedTextColor = TextPrimaryDark,
                unfocusedTextColor = TextPrimaryDark
              ),
              singleLine = true
            )
          }

          Spacer(modifier = Modifier.height(8.dp))

          // Quick Preset Role Chips
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            listOf(
              "Zepto (Ops Lead)" to Pair("Lead Strategy & Operations", "Zepto"),
              "McKinsey (Strategy)" to Pair("Strategy Associate", "McKinsey & Company"),
              "Razorpay (BizOps)" to Pair("Business Operations Specialist", "Razorpay"),
              "Swiggy (Instamart GM)" to Pair("Dark Store General Manager", "Swiggy Instamart")
            ).forEach { (label, pair) ->
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(6.dp))
                  .background(SlateElevated)
                  .clickable {
                    viewModel.optimizerTargetRole.value = pair.first
                    viewModel.optimizerTargetCompany.value = pair.second
                  }
                  .padding(horizontal = 8.dp, vertical = 4.dp)
              ) {
                Text(text = label, fontSize = 10.sp, color = TextSecondaryDark)
              }
            }
          }

          Spacer(modifier = Modifier.height(16.dp))

          // Section 3: Local Document Health Telemetry (Pre-Gemini)
          parsedDoc?.let { doc ->
            Text(
              text = "3. LOCAL DOCUMENT HEALTH METRICS (PARSER TELEMETRY)",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = TextSecondaryDark,
              letterSpacing = 0.8.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              MetricTelemetryTile(
                title = "Quantified Impact",
                value = "${(doc.quantifiableMetricRatio * 100).toInt()}%",
                subtitle = if (doc.quantifiableMetricRatio >= 0.6f) "High Leverage" else "Needs Numbers",
                color = if (doc.quantifiableMetricRatio >= 0.6f) TitanEmerald else TitanGold,
                modifier = Modifier.weight(1f)
              )

              MetricTelemetryTile(
                title = "Action Verbs",
                value = "${doc.actionVerbStrengthScore}/100",
                subtitle = if (doc.actionVerbStrengthScore >= 70) "Strong Active" else "Passive Heavy",
                color = if (doc.actionVerbStrengthScore >= 70) TitanCyan else TitanGold,
                modifier = Modifier.weight(1f)
              )

              MetricTelemetryTile(
                title = "ATS Readability",
                value = "${doc.atsFormattingScore}/100",
                subtitle = doc.wordDensityRating.substringBefore("(").trim(),
                color = if (doc.atsFormattingScore >= 80) TitanEmerald else TitanCyan,
                modifier = Modifier.weight(1f)
              )
            }

            if (doc.weakVerbOccurrences.isNotEmpty()) {
              Spacer(modifier = Modifier.height(8.dp))
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(8.dp))
                  .background(Color(0xFF422006))
                  .border(1.dp, Color(0xFFCA8A04), RoundedCornerShape(8.dp))
                  .padding(10.dp)
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = TitanGold,
                    modifier = Modifier.size(16.dp)
                  )
                  Spacer(modifier = Modifier.width(8.dp))
                  Text(
                    text = "Weak/Passive verbs detected: ${doc.weakVerbOccurrences.distinct().joinToString(", ")}. Gemini will transform these into executive power verbs.",
                    fontSize = 11.sp,
                    color = TextPrimaryDark
                  )
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(18.dp))

          // Section 4: Run Gemini Optimization Action
          Button(
            onClick = { viewModel.runGeminiResumeOptimization() },
            enabled = !isOptimizing && parsedDoc != null,
            modifier = Modifier
              .fillMaxWidth()
              .height(50.dp)
              .testTag("run_gemini_resume_optimizer_button"),
            colors = ButtonDefaults.buttonColors(
              containerColor = TitanCyan,
              disabledContainerColor = TitanCyan.copy(alpha = 0.4f)
            ),
            shape = RoundedCornerShape(12.dp)
          ) {
            if (isOptimizing) {
              CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = ObsidianDark,
                strokeWidth = 2.dp
              )
              Spacer(modifier = Modifier.width(10.dp))
              Text(
                text = "Optimizing via Gemini 3.5 Flash...",
                color = ObsidianDark,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
              )
            } else {
              Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = ObsidianDark,
                modifier = Modifier.size(20.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = if (feedback == null) "Run Gemini Resume Optimization Engine" else "Re-Analyze with Gemini Engine",
                color = ObsidianDark,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
              )
            }
          }

          Spacer(modifier = Modifier.height(20.dp))

          // Section 5: Constructive Feedback Results
          feedback?.let { result ->
            OptimizationResultsSection(
              feedback = result,
              onToggleBullet = { viewModel.applyBulletTransformation(it) }
            )
          }
        }
      }
    }
  }
}

@Composable
private fun ParsedDocumentHeaderCard(
  doc: ParsedResumeDocument,
  scenarioDesc: String,
  isExpanded: Boolean,
  onToggleExpand: () -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(12.dp))
      .border(1.dp, SlateBorder, RoundedCornerShape(12.dp)),
    colors = CardDefaults.cardColors(containerColor = SlateCard)
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(TitanCyan.copy(alpha = 0.2f))
              .padding(horizontal = 6.dp, vertical = 2.dp)
          ) {
            Text(
              text = doc.fileType,
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              color = TitanCyan
            )
          }
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = doc.fileName,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimaryDark
          )
        }

        Text(
          text = "${doc.wordCount} words • ${(doc.fileSizeBytes / 1024)} KB",
          fontSize = 11.sp,
          color = TextMutedDark
        )
      }

      Spacer(modifier = Modifier.height(6.dp))
      Text(
        text = scenarioDesc,
        fontSize = 12.sp,
        color = TextSecondaryDark,
        lineHeight = 16.sp
      )

      Spacer(modifier = Modifier.height(8.dp))

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clickable { onToggleExpand() },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = if (isExpanded) "Hide Parsed Structure & Raw Text" else "Inspect Parsed Document Structure & Sections",
          fontSize = 11.sp,
          fontWeight = FontWeight.SemiBold,
          color = TitanCyan
        )
        Icon(
          imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
          contentDescription = null,
          tint = TitanCyan,
          modifier = Modifier.size(16.dp)
        )
      }

      AnimatedVisibility(visible = isExpanded) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp)
            .background(SlateElevated, RoundedCornerShape(8.dp))
            .padding(10.dp)
        ) {
          Text(
            text = "Candidate: ${doc.contactInfo.candidateName} (${doc.contactInfo.location})",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimaryDark
          )
          Text(
            text = "Email: ${doc.contactInfo.email} • Phone: ${doc.contactInfo.phone}",
            fontSize = 10.sp,
            color = TextMutedDark
          )
          Spacer(modifier = Modifier.height(6.dp))

          Text(
            text = "Detected Sections: Experience (${doc.experiences.size} items), Skills (${doc.skills.size} items), Education, Projects",
            fontSize = 10.sp,
            color = TitanEmerald
          )

          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "Raw Content Preview (First 400 chars):",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = TextSecondaryDark
          )
          Text(
            text = doc.rawText.take(400) + "...",
            fontSize = 10.sp,
            fontFamily = FontFamily.Monospace,
            color = TextMutedDark,
            lineHeight = 14.sp
          )
        }
      }
    }
  }
}

@Composable
private fun MetricTelemetryTile(
  title: String,
  value: String,
  subtitle: String,
  color: Color,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(10.dp))
      .background(SlateCard)
      .border(1.dp, SlateBorder, RoundedCornerShape(10.dp))
      .padding(10.dp)
  ) {
    Column {
      Text(text = title, fontSize = 10.sp, color = TextMutedDark)
      Spacer(modifier = Modifier.height(2.dp))
      Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = color)
      Spacer(modifier = Modifier.height(2.dp))
      Text(text = subtitle, fontSize = 9.sp, color = TextSecondaryDark, maxLines = 1)
    }
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun OptimizationResultsSection(
  feedback: ResumeOptimizationFeedback,
  onToggleBullet: (String) -> Unit
) {
  Column(modifier = Modifier.fillMaxWidth()) {
    // Executive Scorecard
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(14.dp))
        .border(1.dp, TitanCyan.copy(alpha = 0.4f), RoundedCornerShape(14.dp)),
      colors = CardDefaults.cardColors(containerColor = SlateCard)
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "EXECUTIVE RESUME VERDICT",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = TitanCyan,
              letterSpacing = 1.sp
            )
            Text(
              text = feedback.marketAlignmentTier,
              fontSize = 15.sp,
              fontWeight = FontWeight.Bold,
              color = TextPrimaryDark
            )
          }

          Row(verticalAlignment = Alignment.CenterVertically) {
            Column(horizontalAlignment = Alignment.End) {
              Text(
                text = "${feedback.overallScore}/100",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = if (feedback.overallScore >= 85) TitanEmerald else if (feedback.overallScore >= 70) TitanCyan else TitanGold
              )
              Text(
                text = "ATS Pass: ${feedback.atsPassRatePercent}%",
                fontSize = 11.sp,
                color = TextSecondaryDark
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))
        Text(
          text = feedback.executiveVerdict,
          fontSize = 13.sp,
          color = TextPrimaryDark,
          lineHeight = 18.sp
        )

        Spacer(modifier = Modifier.height(10.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(4.dp))
              .background(if (feedback.isLiveApi) TitanEmerald.copy(alpha = 0.2f) else SlateElevated)
              .padding(horizontal = 6.dp, vertical = 2.dp)
          ) {
            Text(
              text = if (feedback.isLiveApi) "LIVE GEMINI API" else "LOCAL HIGH-FIDELITY ENGINE",
              fontSize = 9.sp,
              fontWeight = FontWeight.Bold,
              color = if (feedback.isLiveApi) TitanEmerald else TextSecondaryDark
            )
          }
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Model: ${feedback.modelUsed} • Benchmarked against ${feedback.targetCompanyEvaluated}",
            fontSize = 10.sp,
            color = TextMutedDark
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Interactive Bullet Transformation Lab
    Text(
      text = "CONSTRUCTIVE BULLET TRANSFORMATIONS (BEFORE → AFTER)",
      fontSize = 12.sp,
      fontWeight = FontWeight.Bold,
      color = TextSecondaryDark,
      letterSpacing = 0.8.sp
    )
    Spacer(modifier = Modifier.height(8.dp))

    feedback.bulletTransformations.forEach { bulletPair ->
      BulletTransformationCard(
        pair = bulletPair,
        onToggleApply = { onToggleBullet(bulletPair.id) }
      )
      Spacer(modifier = Modifier.height(10.dp))
    }

    Spacer(modifier = Modifier.height(12.dp))

    // Section-by-Section Constructive Critiques
    Text(
      text = "SECTION-BY-SECTION STRATEGIC CRITIQUE",
      fontSize = 12.sp,
      fontWeight = FontWeight.Bold,
      color = TextSecondaryDark,
      letterSpacing = 0.8.sp
    )
    Spacer(modifier = Modifier.height(8.dp))

    feedback.sectionCritiques.forEach { critique ->
      SectionCritiqueCard(critique = critique)
      Spacer(modifier = Modifier.height(8.dp))
    }

    Spacer(modifier = Modifier.height(12.dp))

    // ATS Keyword Audit Matrix
    Text(
      text = "ATS KEYWORD & COMPETENCY MATRIX",
      fontSize = 12.sp,
      fontWeight = FontWeight.Bold,
      color = TextSecondaryDark,
      letterSpacing = 0.8.sp
    )
    Spacer(modifier = Modifier.height(8.dp))

    Card(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(12.dp))
        .border(1.dp, SlateBorder, RoundedCornerShape(12.dp)),
      colors = CardDefaults.cardColors(containerColor = SlateCard)
    ) {
      Column(modifier = Modifier.padding(14.dp)) {
        // Matched Keywords
        Text(
          text = "Matched High-Value Keywords (${feedback.keywordAudit.matchedKeywords.size})",
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          color = TitanEmerald
        )
        Spacer(modifier = Modifier.height(6.dp))
        FlowRow(
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          feedback.keywordAudit.matchedKeywords.forEach { kw ->
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(TitanEmerald.copy(alpha = 0.15f))
                .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
              Text(text = kw, fontSize = 11.sp, color = TitanEmerald)
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Missing Keywords to Add
        Text(
          text = "Missing High-Priority ATS Keywords (Recommended Additions)",
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          color = TitanCyan
        )
        Spacer(modifier = Modifier.height(6.dp))
        FlowRow(
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          feedback.keywordAudit.missingHighPriorityKeywords.forEach { kw ->
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(TitanCyan.copy(alpha = 0.15f))
                .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
              Text(text = "+ $kw", fontSize = 11.sp, color = TitanCyan)
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Cliches to purge
        if (feedback.keywordAudit.overusedClichePhrases.isNotEmpty()) {
          Text(
            text = "Overused Cliches to Purge",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFF87171)
          )
          Spacer(modifier = Modifier.height(6.dp))
          FlowRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            feedback.keywordAudit.overusedClichePhrases.forEach { kw ->
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(6.dp))
                  .background(Color(0xFF450A0A))
                  .padding(horizontal = 8.dp, vertical = 4.dp)
              ) {
                Text(text = "✕ $kw", fontSize = 11.sp, color = Color(0xFFFCA5A5))
              }
            }
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Top 3 Immediate Action Priorities
    Text(
      text = "IMMEDIATE OPTIMIZATION PRIORITIES (HIGH CONVERSION)",
      fontSize = 12.sp,
      fontWeight = FontWeight.Bold,
      color = TextSecondaryDark,
      letterSpacing = 0.8.sp
    )
    Spacer(modifier = Modifier.height(8.dp))

    feedback.topImmediatePriorities.forEachIndexed { index, priority ->
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .padding(bottom = 6.dp)
          .clip(RoundedCornerShape(10.dp))
          .background(SlateCard)
          .border(1.dp, SlateBorder, RoundedCornerShape(10.dp))
          .padding(12.dp)
      ) {
        Row(verticalAlignment = Alignment.Top) {
          Box(
            modifier = Modifier
              .size(22.dp)
              .clip(CircleShape)
              .background(TitanCyan.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "${index + 1}",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = TitanCyan
            )
          }
          Spacer(modifier = Modifier.width(10.dp))
          Text(
            text = priority,
            fontSize = 12.sp,
            color = TextPrimaryDark,
            lineHeight = 17.sp
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Formatting Note
    Text(
      text = "Typography & Layout Guidance: ${feedback.formattingAndLengthGuidance}",
      fontSize = 11.sp,
      color = TextMutedDark,
      lineHeight = 15.sp
    )
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun BulletTransformationCard(
  pair: BulletOptimizationPair,
  onToggleApply: () -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(12.dp))
      .border(
        width = 1.dp,
        color = if (pair.isApplied) TitanEmerald else SlateBorder,
        shape = RoundedCornerShape(12.dp)
      ),
    colors = CardDefaults.cardColors(containerColor = SlateCard)
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = pair.roleOrSection,
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          color = TitanCyan
        )

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(if (pair.isApplied) TitanEmerald.copy(alpha = 0.2f) else SlateElevated)
            .clickable { onToggleApply() }
            .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            if (pair.isApplied) {
              Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = TitanEmerald,
                modifier = Modifier.size(14.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(text = "APPLIED TO ACTIVE RESUME", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TitanEmerald)
            } else {
              Text(text = "+ APPLY TO ACTIVE RESUME", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextSecondaryDark)
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Before (Original)
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(8.dp))
          .background(ObsidianDark)
          .padding(10.dp)
      ) {
        Column {
          Text(
            text = "BEFORE (PASSIVE / WEAK METRIC):",
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFF87171)
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = pair.originalBullet,
            fontSize = 12.sp,
            color = TextSecondaryDark,
            lineHeight = 16.sp
          )
        }
      }

      Spacer(modifier = Modifier.height(6.dp))

      // After (Optimized)
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(8.dp))
          .background(TitanEmerald.copy(alpha = 0.08f))
          .border(1.dp, TitanEmerald.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
          .padding(10.dp)
      ) {
        Column {
          Text(
            text = "GEMINI OPTIMIZED (POWER IMPACT BULLET):",
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = TitanEmerald
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = pair.improvedBullet,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = TextPrimaryDark,
            lineHeight = 17.sp
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Metric Added & Keywords
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.Lightbulb,
          contentDescription = null,
          tint = TitanGold,
          modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "Metric Added: ${pair.metricAdded}",
          fontSize = 11.sp,
          color = TitanGold,
          fontWeight = FontWeight.Medium
        )
      }

      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = "Strategic Logic: ${pair.reasoning}",
        fontSize = 11.sp,
        color = TextMutedDark,
        lineHeight = 15.sp
      )
    }
  }
}

@Composable
private fun SectionCritiqueCard(critique: SectionOptimizationCritique) {
  val statusColor = when (critique.status) {
    "OPTIMIZED" -> TitanEmerald
    "NEEDS_ATTENTION" -> TitanGold
    else -> Color(0xFFF87171)
  }

  val statusLabel = when (critique.status) {
    "OPTIMIZED" -> "OPTIMIZED"
    "NEEDS_ATTENTION" -> "NEEDS ATTENTION"
    else -> "CRITICAL GAP"
  }

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(10.dp))
      .border(1.dp, SlateBorder, RoundedCornerShape(10.dp)),
    colors = CardDefaults.cardColors(containerColor = SlateCard)
  ) {
    Column(modifier = Modifier.padding(12.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = critique.sectionName,
          fontSize = 13.sp,
          fontWeight = FontWeight.Bold,
          color = TextPrimaryDark
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(4.dp))
              .background(statusColor.copy(alpha = 0.15f))
              .padding(horizontal = 6.dp, vertical = 2.dp)
          ) {
            Text(
              text = statusLabel,
              fontSize = 9.sp,
              fontWeight = FontWeight.Bold,
              color = statusColor
            )
          }
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "${critique.score}/100",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = statusColor
          )
        }
      }

      Spacer(modifier = Modifier.height(6.dp))
      Text(
        text = critique.critique,
        fontSize = 12.sp,
        color = TextSecondaryDark,
        lineHeight = 16.sp
      )

      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = "Action: ${critique.actionableGuidance}",
        fontSize = 11.sp,
        fontWeight = FontWeight.Medium,
        color = TitanCyan,
        lineHeight = 15.sp
      )
    }
  }
}
