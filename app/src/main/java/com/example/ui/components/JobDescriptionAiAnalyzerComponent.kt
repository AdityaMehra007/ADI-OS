package com.example.ui.components

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import androidx.compose.material.icons.automirrored.filled.FactCheck
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentPaste
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import com.example.data.model.ResumeJdMatchResult
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CandidateMatchStatus
import com.example.data.model.JobActionPlan
import com.example.data.model.SampleJobDescription
import com.example.data.model.SkillImportance
import com.example.service.LocalJobActionPlanGenerator
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
import com.example.ui.viewmodel.TitanScreen
import com.example.ui.viewmodel.TitanViewModel

/**
 * High-craft, production-ready UI component featuring:
 * 1. Text field for pasting raw Job Descriptions with word/character telemetry.
 * 2. Instant trigger button to initiate Gemini AI analysis.
 * 3. Quick-action clipboard paste, sample JD presets, and clear actions.
 * 4. Extracted role & company fine-tuning.
 * 5. Interactive inline AI Analysis Results breakdown (Fit score, key skills, ATS keywords, talking points).
 */
@Composable
fun JobDescriptionAiAnalyzerComponent(
  viewModel: TitanViewModel,
  modifier: Modifier = Modifier,
  onNavigateToResumeMatcher: ((targetCompany: String, role: String, reqs: String) -> Unit)? = null,
  onOpenActionPlanDialog: (() -> Unit)? = null
) {
  val context = LocalContext.current
  val clipboardManager = LocalClipboardManager.current

  val isAnalyzing by viewModel.isAnalyzingPastedJobDescription.collectAsState()
  val analysisResult by viewModel.pastedJobDescriptionAnalysisResult.collectAsState()
  val sampleJds = viewModel.availableSampleJobDescriptions

  val uploadedFileName by viewModel.uploadedResumeFileName.collectAsState()
  val parsedResume by viewModel.parsedResumeForJdAnalysis.collectAsState()
  val isParsingResume by viewModel.isParsingResumeFile.collectAsState()
  val resumeJdMatch by viewModel.resumeJdMatchResult.collectAsState()
  val resumeParsingError by viewModel.resumeParsingError.collectAsState()
  val availablePresets = viewModel.availableMockDocuments

  val filePickerLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.GetContent()
  ) { uri: Uri? ->
    if (uri != null) {
      viewModel.parseResumeFromFile(context, uri)
      Toast.makeText(context, "Parsing resume PDF/text file...", Toast.LENGTH_SHORT).show()
    }
  }

  var jobDescriptionText by remember {
    mutableStateOf(viewModel.pastedJobDescriptionText.value.ifBlank {
      sampleJds.firstOrNull()?.description ?: ""
    })
  }

  var targetRole by remember {
    mutableStateOf(
      viewModel.actionPlanTargetRole.value.ifBlank {
        viewModel.extractRoleFromJd(jobDescriptionText) ?: "Lead - Strategy & Dark Store Operations"
      }
    )
  }

  var targetCompany by remember {
    mutableStateOf(
      viewModel.actionPlanTargetCompany.value.ifBlank {
        viewModel.extractCompanyFromJd(jobDescriptionText) ?: "Zepto"
      }
    )
  }

  var showMetadataTuning by remember { mutableStateOf(false) }
  var isExpanded by remember { mutableStateOf(true) }

  val isSplitViewMode by viewModel.isSplitViewJdComparisonMode.collectAsState()

  val wordCount = remember(jobDescriptionText) {
    if (jobDescriptionText.isBlank()) 0 else jobDescriptionText.trim().split(Regex("\\s+")).size
  }
  val charCount = remember(jobDescriptionText) { jobDescriptionText.length }

  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("job_description_ai_analyzer_card"),
    colors = CardDefaults.cardColors(containerColor = SlateCard),
    shape = RoundedCornerShape(16.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
    ) {
      // 1. Header Banner & Status Badge
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.weight(1f)
        ) {
          Box(
            modifier = Modifier
              .size(38.dp)
              .clip(CircleShape)
              .background(TitanCyan.copy(alpha = 0.15f))
              .border(1.dp, TitanCyan.copy(alpha = 0.4f), CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.AutoAwesome,
              contentDescription = "AI Analyzer Icon",
              tint = TitanCyan,
              modifier = Modifier.size(20.dp)
            )
          }

          Spacer(modifier = Modifier.width(10.dp))

          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "GEMINI 3.5 FLASH • AI JOB ANALYZER",
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                color = TitanCyan,
                letterSpacing = 1.sp
              )
              Spacer(modifier = Modifier.width(6.dp))
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(4.dp))
                  .background(TitanEmerald.copy(alpha = 0.15f))
                  .padding(horizontal = 5.dp, vertical = 1.5.dp)
              ) {
                Text(
                  text = "LIVE",
                  color = TitanEmerald,
                  fontSize = 8.sp,
                  fontWeight = FontWeight.ExtraBold
                )
              }
            }

            Text(
              text = if (isSplitViewMode) "Job Description Match Split-View" else "Job Description AI Analysis",
              fontSize = 17.sp,
              fontWeight = FontWeight.Bold,
              color = TextPrimaryDark
            )
          }
        }

        // Collapse / Expand Toggle
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(SlateElevated)
            .border(1.dp, SlateBorder, RoundedCornerShape(8.dp))
            .clickable { isExpanded = !isExpanded }
            .padding(horizontal = 8.dp, vertical = 6.dp)
            .testTag("toggle_jd_analyzer_expanded")
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
              contentDescription = if (isExpanded) "Collapse" else "Expand",
              tint = TitanCyan,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(2.dp))
            Text(
              text = if (isExpanded) "Collapse" else "Expand",
              fontSize = 11.sp,
              color = TextSecondaryDark,
              fontWeight = FontWeight.Medium
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(6.dp))

      Text(
        text = "Analyze target role specifications, benchmark compensation, or compare match scores of two different job descriptions side-by-side.",
        fontSize = 12.sp,
        color = TextSecondaryDark,
        lineHeight = 17.sp
      )

      Spacer(modifier = Modifier.height(10.dp))

      // Segmented Mode Selector: Single Analysis vs Split-View Comparison
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(10.dp))
          .background(SlateElevated)
          .border(1.dp, SlateBorder, RoundedCornerShape(10.dp))
          .padding(3.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        Box(
          modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(8.dp))
            .background(if (!isSplitViewMode) TitanCyan.copy(alpha = 0.2f) else Color.Transparent)
            .clickable { viewModel.toggleSplitViewJdComparisonMode(false) }
            .padding(vertical = 7.dp)
            .testTag("activate_single_jd_analysis_tab"),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "Single JD Analysis",
            fontSize = 11.sp,
            fontWeight = if (!isSplitViewMode) FontWeight.Bold else FontWeight.Medium,
            color = if (!isSplitViewMode) TitanCyan else TextMutedDark
          )
        }

        Box(
          modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSplitViewMode) TitanCyan.copy(alpha = 0.2f) else Color.Transparent)
            .clickable { viewModel.toggleSplitViewJdComparisonMode(true) }
            .padding(vertical = 7.dp)
            .testTag("activate_split_view_comparison_tab"),
          contentAlignment = Alignment.Center
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.CompareArrows,
              contentDescription = null,
              tint = if (isSplitViewMode) TitanCyan else TextMutedDark,
              modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "Split-View (Compare 2 JDs)",
              fontSize = 11.sp,
              fontWeight = if (isSplitViewMode) FontWeight.Bold else FontWeight.Medium,
              color = if (isSplitViewMode) TitanCyan else TextMutedDark
            )
          }
        }
      }

      AnimatedVisibility(visible = isExpanded) {
        Column(modifier = Modifier.fillMaxWidth()) {
          Spacer(modifier = Modifier.height(14.dp))

          if (isSplitViewMode) {
            JobDescriptionSplitViewComparison(
              viewModel = viewModel,
              onCloseSplitView = { viewModel.toggleSplitViewJdComparisonMode(false) }
            )
          } else {
            // CANDIDATE RESUME SOURCE & PARSER BAR
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .testTag("active_resume_parser_card"),
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
                  modifier = Modifier.weight(1f)
                ) {
                  Box(
                    modifier = Modifier
                      .size(32.dp)
                      .clip(CircleShape)
                      .background(TitanCyan.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                  ) {
                    Icon(
                      imageVector = Icons.Default.PictureAsPdf,
                      contentDescription = "Resume Document",
                      tint = TitanCyan,
                      modifier = Modifier.size(17.dp)
                    )
                  }
                  Spacer(modifier = Modifier.width(10.dp))
                  Column {
                    Text(
                      text = "RESUME SOURCE FOR JD MATCH",
                      fontSize = 9.sp,
                      fontWeight = FontWeight.Bold,
                      color = TitanCyan,
                      letterSpacing = 0.5.sp
                    )
                    Text(
                      text = uploadedFileName ?: "Aditya_Shenoy_Resume.pdf",
                      fontSize = 13.sp,
                      fontWeight = FontWeight.Bold,
                      color = TextPrimaryDark,
                      maxLines = 1
                    )
                  }
                }

                // Upload PDF/TXT Button
                Button(
                  onClick = { filePickerLauncher.launch("*/*") },
                  modifier = Modifier
                    .height(34.dp)
                    .testTag("upload_resume_file_btn"),
                  colors = ButtonDefaults.buttonColors(containerColor = TitanCyan.copy(alpha = 0.2f)),
                  border = androidx.compose.foundation.BorderStroke(1.dp, TitanCyan.copy(alpha = 0.5f)),
                  shape = RoundedCornerShape(8.dp),
                  contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 0.dp)
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                      imageVector = Icons.Default.UploadFile,
                      contentDescription = null,
                      tint = TitanCyan,
                      modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                      text = "Upload PDF/TXT",
                      fontSize = 11.sp,
                      fontWeight = FontWeight.Bold,
                      color = TitanCyan
                    )
                  }
                }
              }

              Spacer(modifier = Modifier.height(8.dp))

              // Metadata & extracted skill count pill
              parsedResume?.let { doc ->
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                  ) {
                    Box(
                      modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(TitanEmerald.copy(alpha = 0.12f))
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                      Text(
                        text = "✓ ${doc.skills.size} skills parsed",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = TitanEmerald
                      )
                    }
                    Box(
                      modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(TitanGold.copy(alpha = 0.12f))
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                      Text(
                        text = "⚡ ${(doc.quantifiableMetricRatio * 100).toInt()}% quantified",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = TitanGold
                      )
                    }
                    Box(
                      modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(SlateCard)
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                      Text(
                        text = "${doc.fileType} • ${doc.wordCount} words",
                        fontSize = 10.sp,
                        color = TextSecondaryDark
                      )
                    }
                  }
                }
              }

              // Loading state while parsing
              if (isParsingResume) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                  CircularProgressIndicator(
                    modifier = Modifier.size(12.dp),
                    strokeWidth = 2.dp,
                    color = TitanCyan
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = "Parsing resume PDF/text document structure...",
                    fontSize = 11.sp,
                    color = TitanCyan
                  )
                }
              }

              // Parsing error alert
              resumeParsingError?.let { err ->
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                  text = "Parsing warning: $err",
                  fontSize = 10.sp,
                  color = TitanCrimson
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // 2. Preset Samples & Quick Clipboard Paste Actions
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "QUICK BENCHMARKS & SAMPLES",
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              color = TextMutedDark,
              letterSpacing = 0.5.sp
            )

            // Live Word & Char Counter
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(SlateElevated)
                .padding(horizontal = 7.dp, vertical = 3.dp)
            ) {
              Text(
                text = "$wordCount words • $charCount chars",
                fontSize = 10.sp,
                color = if (wordCount > 50) TitanEmerald else TextMutedDark,
                fontWeight = FontWeight.SemiBold
              )
            }
          }

          Spacer(modifier = Modifier.height(6.dp))

          // Preset Chips Carousel
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            // Paste from Clipboard Button
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(TitanCyan.copy(alpha = 0.12f))
                .border(1.dp, TitanCyan.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                .clickable {
                  val clipText = clipboardManager.getText()?.text
                  if (!clipText.isNullOrBlank()) {
                    jobDescriptionText = clipText
                    viewModel.pastedJobDescriptionText.value = clipText
                    val detectedComp = viewModel.extractCompanyFromJd(clipText)
                    val detectedR = viewModel.extractRoleFromJd(clipText)
                    if (detectedComp != null) targetCompany = detectedComp
                    if (detectedR != null) targetRole = detectedR
                    Toast.makeText(context, "Pasted ${clipText.length} characters from clipboard", Toast.LENGTH_SHORT).show()
                  } else {
                    Toast.makeText(context, "Clipboard is empty. Copy a job description first.", Toast.LENGTH_SHORT).show()
                  }
                }
                .padding(horizontal = 10.dp, vertical = 6.dp)
                .testTag("paste_from_clipboard_button")
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.ContentPaste,
                  contentDescription = null,
                  tint = TitanCyan,
                  modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                  text = "Paste from Clipboard",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = TitanCyan
                )
              }
            }

            // Quick Samples
            sampleJds.forEach { sample ->
              val isSelected = jobDescriptionText.contains(sample.company, ignoreCase = true)
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(8.dp))
                  .background(if (isSelected) TitanGold.copy(alpha = 0.2f) else SlateElevated)
                  .border(
                    width = 1.dp,
                    color = if (isSelected) TitanGold else SlateBorder,
                    shape = RoundedCornerShape(8.dp)
                  )
                  .clickable {
                    jobDescriptionText = sample.description
                    targetCompany = sample.company
                    targetRole = sample.role
                    viewModel.pastedJobDescriptionText.value = sample.description
                    viewModel.actionPlanTargetCompany.value = sample.company
                    viewModel.actionPlanTargetRole.value = sample.role
                    Toast.makeText(context, "Loaded ${sample.company} - ${sample.role}", Toast.LENGTH_SHORT).show()
                  }
                  .padding(horizontal = 9.dp, vertical = 6.dp)
                  .testTag("sample_jd_${sample.company.lowercase()}")
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(
                    text = when (sample.company.lowercase()) {
                      "zepto" -> "⚡ Zepto"
                      "razorpay" -> "💳 Razorpay"
                      "blinkit" -> "🛒 Blinkit"
                      "swiggy" -> "🛵 Swiggy"
                      else -> "🏢 ${sample.company}"
                    },
                    fontSize = 11.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) TitanGold else TextPrimaryDark
                  )
                }
              }
            }

            // Clear Button
            if (jobDescriptionText.isNotBlank()) {
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(8.dp))
                  .background(SlateElevated)
                  .border(1.dp, SlateBorder, RoundedCornerShape(8.dp))
                  .clickable {
                    jobDescriptionText = ""
                    viewModel.pastedJobDescriptionText.value = ""
                  }
                  .padding(horizontal = 8.dp, vertical = 6.dp)
                  .testTag("clear_jd_button")
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = "Clear",
                    tint = TextMutedDark,
                    modifier = Modifier.size(12.dp)
                  )
                  Spacer(modifier = Modifier.width(3.dp))
                  Text("Clear", fontSize = 10.sp, color = TextMutedDark)
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // 3. THE TEXT FIELD FOR PASTING JOB DESCRIPTIONS
          OutlinedTextField(
            value = jobDescriptionText,
            onValueChange = { newText ->
              jobDescriptionText = newText
              viewModel.pastedJobDescriptionText.value = newText
              // Auto-detect role & company if user hasn't overridden
              val comp = viewModel.extractCompanyFromJd(newText)
              if (comp != null && targetCompany == "Zepto") targetCompany = comp
              val r = viewModel.extractRoleFromJd(newText)
              if (r != null && targetRole == "Lead - Strategy & Dark Store Operations") targetRole = r
            },
            modifier = Modifier
              .fillMaxWidth()
              .heightIn(min = 150.dp, max = 280.dp)
              .testTag("job_description_paste_textfield"),
            placeholder = {
              Text(
                text = "Paste complete job description here...\n\nInclude:\n• Role overview & operational mandates\n• Key responsibilities & SLA expectations\n• Required qualifications, analytics toolkit & experience\n• Company mission & hiring team notes",
                fontSize = 12.sp,
                color = TextMutedDark,
                lineHeight = 17.sp
              )
            },
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = TitanCyan,
              unfocusedBorderColor = SlateBorder,
              focusedTextColor = TextPrimaryDark,
              unfocusedTextColor = TextPrimaryDark,
              focusedContainerColor = ObsidianDark,
              unfocusedContainerColor = ObsidianDark
            ),
            shape = RoundedCornerShape(12.dp)
          )

          Spacer(modifier = Modifier.height(10.dp))

          // 4. Role & Company Context Fine-Tuning Bar
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.clickable { showMetadataTuning = !showMetadataTuning }
            ) {
              Icon(
                imageVector = Icons.Default.Business,
                contentDescription = null,
                tint = TitanGold,
                modifier = Modifier.size(13.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "Targeting Context: $targetRole at $targetCompany",
                fontSize = 11.sp,
                color = TitanGold,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
            }

            Text(
              text = if (showMetadataTuning) "Hide Fine-Tuning" else "Edit Details",
              fontSize = 10.sp,
              color = TitanCyan,
              fontWeight = FontWeight.Bold,
              modifier = Modifier
                .clickable { showMetadataTuning = !showMetadataTuning }
                .padding(4.dp)
            )
          }

          AnimatedVisibility(visible = showMetadataTuning) {
            Column(modifier = Modifier.padding(top = 8.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                OutlinedTextField(
                  value = targetCompany,
                  onValueChange = { targetCompany = it },
                  label = { Text("Target Company", fontSize = 10.sp) },
                  singleLine = true,
                  modifier = Modifier
                    .weight(0.9f)
                    .testTag("input_target_company_override"),
                  colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TitanGold,
                    unfocusedBorderColor = SlateBorder,
                    focusedTextColor = TextPrimaryDark,
                    unfocusedTextColor = TextPrimaryDark
                  ),
                  shape = RoundedCornerShape(8.dp)
                )

                OutlinedTextField(
                  value = targetRole,
                  onValueChange = { targetRole = it },
                  label = { Text("Target Role Title", fontSize = 10.sp) },
                  singleLine = true,
                  modifier = Modifier
                    .weight(1.3f)
                    .testTag("input_target_role_override"),
                  colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TitanGold,
                    unfocusedBorderColor = SlateBorder,
                    focusedTextColor = TextPrimaryDark,
                    unfocusedTextColor = TextPrimaryDark
                  ),
                  shape = RoundedCornerShape(8.dp)
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // 5. TRIGGER BUTTON TO INITIATE THE AI ANALYSIS PROCESS
          Button(
            onClick = {
              viewModel.analyzePastedJobDescription(
                jobDescription = jobDescriptionText.trim(),
                targetRole = targetRole.trim(),
                targetCompany = targetCompany.trim(),
                onComplete = {
                  Toast.makeText(context, "AI Analysis completed for $targetRole at $targetCompany!", Toast.LENGTH_SHORT).show()
                }
              )
            },
            enabled = !isAnalyzing && jobDescriptionText.isNotBlank(),
            modifier = Modifier
              .fillMaxWidth()
              .height(52.dp)
              .testTag("initiate_ai_analysis_button"),
            colors = ButtonDefaults.buttonColors(
              containerColor = TitanCyan,
              contentColor = ObsidianDark,
              disabledContainerColor = TitanCyan.copy(alpha = 0.35f),
              disabledContentColor = ObsidianDark.copy(alpha = 0.6f)
            ),
            shape = RoundedCornerShape(12.dp)
          ) {
            if (isAnalyzing) {
              CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = ObsidianDark,
                strokeWidth = 2.dp
              )
              Spacer(modifier = Modifier.width(10.dp))
              Text(
                text = "Analyzing Job Description with Gemini AI...",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = ObsidianDark
              )
            } else {
              Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = ObsidianDark,
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "Initiate AI Analysis",
                fontWeight = FontWeight.Black,
                fontSize = 14.sp,
                color = ObsidianDark
              )
            }
          }

          // 6. ANIMATED LOADING OVERLAY & SHIMMER EFFECT DURING AI ANALYSIS
          AnimatedVisibility(
            visible = isAnalyzing,
            enter = fadeIn() + androidx.compose.animation.expandVertically(),
            exit = fadeOut() + androidx.compose.animation.shrinkVertically()
          ) {
            Column(modifier = Modifier.fillMaxWidth()) {
              Spacer(modifier = Modifier.height(16.dp))
              AiAnalysisLoadingShimmerOverlay(
                targetRole = targetRole.ifBlank { "Strategic Operations Lead" },
                targetCompany = targetCompany.ifBlank { "Target Enterprise" }
              )
            }
          }

          // 7. INLINE AI ANALYSIS RESULTS DISPLAY
          if (!isAnalyzing) {
            analysisResult?.let { plan ->
              Spacer(modifier = Modifier.height(18.dp))
              HorizontalDivider(color = SlateBorder)
              Spacer(modifier = Modifier.height(14.dp))

              AnalysisResultSection(
                plan = plan,
                resumeMatchResult = resumeJdMatch,
                onNavigateToResumeMatcher = {
                  onNavigateToResumeMatcher?.invoke(plan.targetCompany, plan.targetRole, plan.jobDescription)
                },
                onOpenActionPlanDialog = onOpenActionPlanDialog,
                onToggleExperienceRewrite = { bulletId ->
                  viewModel.toggleExperienceBulletRewrite(bulletId)
                },
                onOpenSplitViewComparison = {
                  viewModel.setSplitViewJdTextA(jobDescriptionText, plan.targetCompany, plan.targetRole)
                  viewModel.toggleSplitViewJdComparisonMode(true)
                }
              )
            }
          }
        }
      }
    }
  }
}
}

@Composable
private fun AnalysisResultSection(
  plan: JobActionPlan,
  resumeMatchResult: ResumeJdMatchResult? = null,
  onNavigateToResumeMatcher: () -> Unit,
  onOpenActionPlanDialog: (() -> Unit)?,
  onToggleExperienceRewrite: ((String) -> Unit)? = null,
  onOpenSplitViewComparison: (() -> Unit)? = null
) {
  val context = LocalContext.current
  val clipboardManager = LocalClipboardManager.current

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("job_description_analysis_result_section"),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    // 1. Visually Stunning Circular Gauge & AI Job Match Score Hero Card
    val effectiveScore = resumeMatchResult?.overallMatchScore ?: plan.matchFitScore
    val matchedSkillsCount = remember(plan.requiredSkills, resumeMatchResult) {
      resumeMatchResult?.matchedCount ?: plan.requiredSkills.count { it.candidateMatchStatus == CandidateMatchStatus.STRONG_MATCH }
    }
    val totalSkills = remember(plan.requiredSkills, resumeMatchResult) {
      resumeMatchResult?.totalJdSkillsCount ?: plan.requiredSkills.size
    }

    JobMatchScoreHeroCard(
      score = effectiveScore,
      targetCompany = plan.targetCompany,
      targetRole = plan.targetRole,
      skillsMatchedCount = matchedSkillsCount,
      totalSkillsCount = totalSkills,
      compensationBenchmark = plan.compensationBenchmark,
      seniorityLevel = plan.seniorityLevel,
      isGeminiGenerated = plan.isGeminiGenerated
    )

    // Split-View Mode Trigger CTA Card
    onOpenSplitViewComparison?.let { openSplitView ->
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .clickable { openSplitView() }
          .testTag("compare_in_split_view_cta_card"),
        colors = CardDefaults.cardColors(containerColor = SlateElevated),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, TitanCyan.copy(alpha = 0.4f))
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
          ) {
            Box(
              modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(TitanCyan.copy(alpha = 0.15f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.AutoMirrored.Filled.CompareArrows,
                contentDescription = null,
                tint = TitanCyan,
                modifier = Modifier.size(18.dp)
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "Compare in Split-View Mode",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimaryDark
              )
              Text(
                text = "Benchmark match score side-by-side against another job description",
                fontSize = 10.sp,
                color = TextMutedDark
              )
            }
          }

          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(TitanCyan.copy(alpha = 0.18f))
              .padding(horizontal = 8.dp, vertical = 4.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text("Split-View", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TitanCyan)
              Spacer(modifier = Modifier.width(2.dp))
              Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = TitanCyan,
                modifier = Modifier.size(11.dp)
              )
            }
          }
        }
      }
    }

    // Dedicated AI Experience Description Rewrites Section
    if (plan.experienceBulletRewrites.isNotEmpty()) {
      ExperienceBulletRewritesSection(
        rewrites = plan.experienceBulletRewrites,
        targetRole = plan.targetRole,
        targetCompany = plan.targetCompany,
        onToggleApply = onToggleExperienceRewrite
      )
    }

    // Dedicated Resume vs JD Cross-Audit Card
    resumeMatchResult?.let { match ->
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("resume_jd_cross_audit_card"),
        colors = CardDefaults.cardColors(containerColor = SlateElevated),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.AutoMirrored.Filled.FactCheck,
                contentDescription = null,
                tint = TitanCyan,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "RESUME COMPARISON AUDIT • ${match.parsedResumeName.take(24)}",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = TitanCyan,
                letterSpacing = 0.5.sp
              )
            }

            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(TitanEmerald.copy(alpha = 0.15f))
                .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
              Text(
                text = "${match.overallMatchScore}% FIT",
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                color = TitanEmerald
              )
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // 3 sub-metrics
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            // Skills Match
            Box(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(8.dp))
                .background(SlateCard)
                .padding(8.dp)
            ) {
              Column {
                Text(text = "SKILLS MATCH", fontSize = 9.sp, color = TextMutedDark, fontWeight = FontWeight.Bold)
                Text(text = "${match.skillsMatchScore}%", fontSize = 16.sp, color = TitanEmerald, fontWeight = FontWeight.Black)
                Text(text = "${match.matchedCount} of ${match.totalJdSkillsCount} skills", fontSize = 9.sp, color = TextSecondaryDark)
              }
            }

            // Experience Alignment
            Box(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(8.dp))
                .background(SlateCard)
                .padding(8.dp)
            ) {
              Column {
                Text(text = "TENURE & SCOPE", fontSize = 9.sp, color = TextMutedDark, fontWeight = FontWeight.Bold)
                Text(text = "${match.experienceFitScore}%", fontSize = 16.sp, color = TitanCyan, fontWeight = FontWeight.Black)
                Text(text = "Seniority fit", fontSize = 9.sp, color = TextSecondaryDark)
              }
            }

            // Metrics Density
            Box(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(8.dp))
                .background(SlateCard)
                .padding(8.dp)
            ) {
              Column {
                Text(text = "IMPACT METRICS", fontSize = 9.sp, color = TextMutedDark, fontWeight = FontWeight.Bold)
                Text(text = "${match.metricsScore}%", fontSize = 16.sp, color = TitanGold, fontWeight = FontWeight.Black)
                Text(text = "Metric proof", fontSize = 9.sp, color = TextSecondaryDark)
              }
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Executive Verdict
          Text(
            text = match.executiveVerdict,
            fontSize = 11.sp,
            color = TextPrimaryDark,
            lineHeight = 16.sp
          )

          if (match.missingSkills.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = "PRIORITY GAPS TO ADDRESS IN INTERVIEW:",
              fontSize = 9.sp,
              fontWeight = FontWeight.Bold,
              color = TitanCrimson,
              letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              match.missingSkills.take(6).forEach { skill ->
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(TitanCrimson.copy(alpha = 0.12f))
                    .border(1.dp, TitanCrimson.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                    .padding(horizontal = 7.dp, vertical = 3.dp)
                ) {
                  Text(
                    text = "• $skill",
                    fontSize = 10.sp,
                    color = TitanCrimson,
                    fontWeight = FontWeight.SemiBold
                  )
                }
              }
            }
          }
        }
      }
    }

    // Mission Summary
    Card(
      modifier = Modifier.fillMaxWidth(),
      colors = CardDefaults.cardColors(containerColor = SlateElevated),
      shape = RoundedCornerShape(10.dp),
      border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
    ) {
      Column(modifier = Modifier.padding(12.dp)) {
        Text(
          text = "CORE MISSION & MANDATE",
          fontSize = 10.sp,
          fontWeight = FontWeight.Bold,
          color = TitanCyan,
          letterSpacing = 0.5.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = plan.roleMissionSummary,
          fontSize = 12.sp,
          color = TextPrimaryDark,
          lineHeight = 17.sp
        )
      }
    }

    // Required Skills Grid (Matched vs Gap)
    Text(
      text = "EXTRACTED SKILLS & CANDIDATE ALIGNMENT (${plan.requiredSkills.size})",
      fontSize = 11.sp,
      fontWeight = FontWeight.Bold,
      color = TextMutedDark,
      letterSpacing = 0.5.sp
    )

    Row(
      modifier = Modifier
        .fillMaxWidth()
        .horizontalScroll(rememberScrollState()),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      plan.requiredSkills.take(8).forEach { skill ->
        val isMatched = skill.candidateMatchStatus == CandidateMatchStatus.STRONG_MATCH
        val badgeColor = when (skill.candidateMatchStatus) {
          CandidateMatchStatus.STRONG_MATCH -> TitanEmerald
          CandidateMatchStatus.PARTIAL_GAP -> TitanGold
          CandidateMatchStatus.CRITICAL_GAP -> TitanCrimson
        }

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(badgeColor.copy(alpha = 0.12f))
            .border(1.dp, badgeColor.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
            .padding(horizontal = 9.dp, vertical = 6.dp)
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = if (isMatched) Icons.Default.Check else Icons.Default.Lightbulb,
              contentDescription = null,
              tint = badgeColor,
              modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
              text = skill.name,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = badgeColor
            )
          }
        }
      }
    }

    // Interview Talking Points Preview
    if (plan.interviewTalkingPoints.isNotEmpty()) {
      val firstPoint = plan.interviewTalkingPoints.first()
      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = ObsidianDark),
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Psychology, contentDescription = null, tint = TitanCyan, modifier = Modifier.size(14.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "TOP INTERVIEW TALKING POINT: ${firstPoint.theme.uppercase()}",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = TitanCyan,
                letterSpacing = 0.5.sp
              )
            }
          }
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "\"${firstPoint.executiveNarrative}\"",
            fontSize = 12.sp,
            color = TextPrimaryDark,
            lineHeight = 17.sp,
            fontWeight = FontWeight.Medium
          )
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "Metric Proof: ${firstPoint.proofMetricOrFact}",
            fontSize = 10.sp,
            color = TitanEmerald,
            fontWeight = FontWeight.Bold
          )
        }
      }
    }

    // Action Buttons
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      // Button: Match with Resume
      OutlinedButton(
        onClick = onNavigateToResumeMatcher,
        modifier = Modifier
          .weight(1f)
          .height(44.dp)
          .testTag("action_match_with_resume_btn"),
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, TitanCyan)
      ) {
        Icon(
          imageVector = Icons.AutoMirrored.Filled.FactCheck,
          contentDescription = null,
          tint = TitanCyan,
          modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "Resume Match",
          fontSize = 11.sp,
          color = TitanCyan,
          fontWeight = FontWeight.Bold
        )
      }

      // Button: View Full Action Plan
      Button(
        onClick = { onOpenActionPlanDialog?.invoke() },
        modifier = Modifier
          .weight(1f)
          .height(44.dp)
          .testTag("action_view_full_plan_btn"),
        colors = ButtonDefaults.buttonColors(containerColor = SlateElevated),
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
      ) {
        Icon(
          imageVector = Icons.AutoMirrored.Filled.ArrowForward,
          contentDescription = null,
          tint = TextPrimaryDark,
          modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "Full Action Plan",
          fontSize = 11.sp,
          color = TextPrimaryDark,
          fontWeight = FontWeight.Bold
        )
      }

      // Copy Summary
      Box(
        modifier = Modifier
          .size(44.dp)
          .clip(RoundedCornerShape(8.dp))
          .background(SlateElevated)
          .border(1.dp, SlateBorder, RoundedCornerShape(8.dp))
          .clickable {
            val summary = buildString {
              appendLine("🎯 AI Job Analysis: ${plan.targetRole} @ ${plan.targetCompany}")
              appendLine("Fit Score: ${plan.matchFitScore}% | Seniority: ${plan.seniorityLevel}")
              appendLine("Compensation: ${plan.compensationBenchmark}")
              appendLine("Mission: ${plan.roleMissionSummary}")
              appendLine("\nRequired Skills:")
              plan.requiredSkills.forEach { appendLine("• ${it.name} (${it.candidateMatchStatus})") }
            }
            clipboardManager.setText(AnnotatedString(summary))
            Toast.makeText(context, "Analysis summary copied to clipboard", Toast.LENGTH_SHORT).show()
          }
          .padding(8.dp)
          .testTag("copy_analysis_summary_btn"),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.ContentCopy,
          contentDescription = "Copy Summary",
          tint = TextSecondaryDark,
          modifier = Modifier.size(16.dp)
        )
      }
    }
  }
}
