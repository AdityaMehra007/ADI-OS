package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.automirrored.filled.FactCheck
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ComparisonActionStep
import com.example.data.model.ComparisonBulletEnhancement
import com.example.data.model.MatchedRequirementItem
import com.example.data.model.RequirementGapItem
import com.example.data.model.ResumeJobComparisonFeedback
import com.example.data.model.ResumeVariation
import com.example.data.model.TargetCompanyPresetJob
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
import com.example.ui.viewmodel.TitanViewModel

@Composable
fun ResumeJobComparisonView(
  viewModel: TitanViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val resumeText by viewModel.resumeComparisonResumeText.collectAsState()
  val selectedCompany by viewModel.resumeComparisonSelectedCompany.collectAsState()
  val selectedRole by viewModel.resumeComparisonSelectedRole.collectAsState()
  val jobRequirements by viewModel.resumeComparisonJobRequirements.collectAsState()
  val feedback by viewModel.resumeComparisonFeedback.collectAsState()
  val isComparing by viewModel.isComparingResumeWithGemini.collectAsState()
  val errorMessage by viewModel.resumeComparisonError.collectAsState()
  val presets = viewModel.availableComparisonPresets

  val variations by viewModel.resumeVariations.collectAsState()
  val activeVariationId by viewModel.activeResumeVariationId.collectAsState()
  val showSaveVersionDialog by viewModel.showSaveVersionDialog.collectAsState()
  val showCreateVariationDialog by viewModel.showCreateVariationDialog.collectAsState()

  var versionNotesInput by remember { mutableStateOf("") }
  var newTitleInput by remember { mutableStateOf("") }
  var newCompanyInput by remember { mutableStateOf("") }
  var newRoleInput by remember { mutableStateOf("") }
  var newTagsInput by remember { mutableStateOf("") }

  val activeVariation = variations.find { it.id == activeVariationId }
    ?: variations.find { it.isPrimary }
    ?: variations.firstOrNull()

  var isResumeInputExpanded by remember { mutableStateOf(true) }
  var isJdInputExpanded by remember { mutableStateOf(true) }
  var activeFeedbackTab by remember { mutableIntStateOf(0) }

  val wordCount = remember(resumeText) {
    if (resumeText.isBlank()) 0 else resumeText.trim().split("\\s+".toRegex()).size
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(ObsidianDark)
      .verticalScroll(rememberScrollState())
      .padding(16.dp)
      .testTag("resume_job_comparator_view")
  ) {
    // Header Banner
    Card(
      modifier = Modifier.fillMaxWidth(),
      colors = CardDefaults.cardColors(containerColor = SlateCard),
      shape = RoundedCornerShape(16.dp),
      border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
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
                .size(32.dp)
                .clip(CircleShape)
                .background(TitanCyan.copy(alpha = 0.15f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.AutoMirrored.Filled.FactCheck,
                contentDescription = null,
                tint = TitanCyan,
                modifier = Modifier.size(18.dp)
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "GEMINI 3.5 FLASH • CAREER AUDITOR",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = TitanCyan,
                letterSpacing = 1.sp
              )
              Text(
                text = "Resume vs. Job Requirements",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimaryDark
              )
            }
          }

          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(TitanEmerald.copy(alpha = 0.15f))
              .border(1.dp, TitanEmerald.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
              .padding(horizontal = 8.dp, vertical = 4.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.Bolt,
                contentDescription = null,
                tint = TitanEmerald,
                modifier = Modifier.size(11.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "LIVE BENCHMARK",
                color = TitanEmerald,
                fontSize = 9.sp,
                fontWeight = FontWeight.Black
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(
          text = "Benchmark your current resume text against target company requirements. Uncover critical skill gaps, calculate ATS match probability, and adopt tailored bullet rewrites.",
          fontSize = 12.sp,
          color = TextSecondaryDark,
          lineHeight = 17.sp
        )
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // SECTION 1: Resume Text Input Area
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .testTag("resume_input_card"),
      colors = CardDefaults.cardColors(containerColor = SlateCard),
      shape = RoundedCornerShape(14.dp),
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
              imageVector = Icons.Default.Description,
              contentDescription = null,
              tint = TitanCyan,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "1. CANDIDATE RESUME TEXT",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = TextPrimaryDark,
              letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.width(8.dp))
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(SlateElevated)
                .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
              Text(
                text = "$wordCount words • ${resumeText.length} chars",
                fontSize = 10.sp,
                color = TextMutedDark,
                fontWeight = FontWeight.Medium
              )
            }
          }

          TextButton(
            onClick = { isResumeInputExpanded = !isResumeInputExpanded }
          ) {
            Text(
              text = if (isResumeInputExpanded) "Collapse" else "Expand / Edit",
              fontSize = 11.sp,
              color = TitanCyan
            )
          }
        }

        AnimatedVisibility(visible = isResumeInputExpanded) {
          Column {
            Spacer(modifier = Modifier.height(8.dp))

            // ROOM DATABASE RESUME VARIATIONS & VERSIONING PANEL
            Card(
              modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
                .testTag("resume_variations_panel"),
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
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                      imageVector = Icons.Default.Layers,
                      contentDescription = null,
                      tint = TitanCyan,
                      modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                      text = "SAVED ROOM VARIATIONS (${variations.size})",
                      fontSize = 11.sp,
                      fontWeight = FontWeight.Bold,
                      color = TextPrimaryDark,
                      letterSpacing = 0.5.sp
                    )
                  }

                  // New Variation Button
                  Box(
                    modifier = Modifier
                      .clip(RoundedCornerShape(6.dp))
                      .background(TitanCyan.copy(alpha = 0.15f))
                      .border(1.dp, TitanCyan.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                      .clickable {
                        newTitleInput = ""
                        newCompanyInput = selectedCompany
                        newRoleInput = selectedRole
                        newTagsInput = "Operations, Tailored"
                        viewModel.showCreateVariationDialog.value = true
                      }
                      .padding(horizontal = 8.dp, vertical = 4.dp)
                      .testTag("btn_new_resume_variation")
                  ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Icon(Icons.Default.Add, contentDescription = null, tint = TitanCyan, modifier = Modifier.size(12.dp))
                      Spacer(modifier = Modifier.width(4.dp))
                      Text("New Variation", fontSize = 10.sp, color = TitanCyan, fontWeight = FontWeight.Bold)
                    }
                  }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Horizontal Carousel of Variations
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                  horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                  variations.forEach { variation ->
                    val isSelected = variation.id == activeVariationId || (activeVariationId == null && variation.isPrimary)
                    Card(
                      modifier = Modifier
                        .width(220.dp)
                        .clickable { viewModel.selectResumeVariation(variation) }
                        .testTag("variation_card_${variation.id}"),
                      colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) SlateCard else ObsidianDark
                      ),
                      shape = RoundedCornerShape(10.dp),
                      border = androidx.compose.foundation.BorderStroke(
                        width = if (isSelected) 1.5.dp else 1.dp,
                        color = if (isSelected) TitanCyan else SlateBorder
                      )
                    ) {
                      Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                          modifier = Modifier.fillMaxWidth(),
                          horizontalArrangement = Arrangement.SpaceBetween,
                          verticalAlignment = Alignment.CenterVertically
                        ) {
                          // Version Pill
                          Box(
                            modifier = Modifier
                              .clip(RoundedCornerShape(4.dp))
                              .background(TitanCyan.copy(alpha = 0.2f))
                              .padding(horizontal = 5.dp, vertical = 2.dp)
                          ) {
                            Text(
                              text = variation.versionLabel,
                              fontSize = 9.sp,
                              fontWeight = FontWeight.Bold,
                              color = TitanCyan
                            )
                          }

                          if (variation.isPrimary) {
                            Box(
                              modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(TitanGold.copy(alpha = 0.2f))
                                .padding(horizontal = 5.dp, vertical = 2.dp)
                            ) {
                              Text(
                                text = "★ PRIMARY",
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = TitanGold
                              )
                            }
                          }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                          text = variation.title,
                          fontSize = 11.sp,
                          fontWeight = FontWeight.Bold,
                          color = if (isSelected) TextPrimaryDark else TextSecondaryDark,
                          maxLines = 1,
                          overflow = TextOverflow.Ellipsis
                        )

                        Text(
                          text = "🏢 ${variation.targetCompany} • ${variation.targetRole}",
                          fontSize = 9.sp,
                          color = TextMutedDark,
                          maxLines = 1,
                          overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                          text = variation.versionNotes,
                          fontSize = 9.sp,
                          color = TextMutedDark,
                          maxLines = 2,
                          overflow = TextOverflow.Ellipsis,
                          lineHeight = 12.sp
                        )

                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                          modifier = Modifier.fillMaxWidth(),
                          horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                          Text(
                            text = "${variation.wordCount} words",
                            fontSize = 8.sp,
                            color = TextMutedDark
                          )
                          Text(
                            text = "Fit: ${variation.overallMatchScore}%",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (variation.overallMatchScore >= 90) TitanEmerald else TitanGold
                          )
                        }
                      }
                    }
                  }
                }

                if (activeVariation != null) {
                  Spacer(modifier = Modifier.height(10.dp))
                  // Action buttons for active variation
                  Row(
                    modifier = Modifier
                      .fillMaxWidth()
                      .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    // Save as New Version (Branching)
                    Box(
                      modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(TitanEmerald.copy(alpha = 0.15f))
                        .border(1.dp, TitanEmerald.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                        .clickable {
                          versionNotesInput = "Infused tailored metrics and criteria rewrites"
                          viewModel.showSaveVersionDialog.value = true
                        }
                        .padding(horizontal = 9.dp, vertical = 5.dp)
                        .testTag("btn_save_as_new_version")
                    ) {
                      Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.History, contentDescription = null, tint = TitanEmerald, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                          text = "Save as New Version (v${activeVariation.versionNumber + 1}.0)",
                          fontSize = 10.sp,
                          color = TitanEmerald,
                          fontWeight = FontWeight.Bold
                        )
                      }
                    }

                    // Save Edits to Current
                    Box(
                      modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(SlateCard)
                        .border(1.dp, SlateBorder, RoundedCornerShape(6.dp))
                        .clickable {
                          viewModel.updateCurrentVariationContent()
                          Toast.makeText(context, "Saved changes to ${activeVariation.title}", Toast.LENGTH_SHORT).show()
                        }
                        .padding(horizontal = 9.dp, vertical = 5.dp)
                        .testTag("btn_save_current_edits")
                    ) {
                      Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Save, contentDescription = null, tint = TitanCyan, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Save Edits to Current", fontSize = 10.sp, color = TextPrimaryDark)
                      }
                    }

                    // Make Primary
                    if (!activeVariation.isPrimary) {
                      Box(
                        modifier = Modifier
                          .clip(RoundedCornerShape(6.dp))
                          .background(TitanGold.copy(alpha = 0.12f))
                          .border(1.dp, TitanGold.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                          .clickable {
                            viewModel.setResumeVariationAsPrimary(activeVariation.id)
                            Toast.makeText(context, "Set as Primary Master Resume", Toast.LENGTH_SHORT).show()
                          }
                          .padding(horizontal = 9.dp, vertical = 5.dp)
                          .testTag("btn_set_primary_variation")
                      ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                          Icon(Icons.Default.Star, contentDescription = null, tint = TitanGold, modifier = Modifier.size(12.dp))
                          Spacer(modifier = Modifier.width(4.dp))
                          Text("Make Primary", fontSize = 10.sp, color = TitanGold, fontWeight = FontWeight.Bold)
                        }
                      }
                    }

                    // Delete Variation
                    if (variations.size > 1) {
                      Box(
                        modifier = Modifier
                          .clip(RoundedCornerShape(6.dp))
                          .background(TitanCrimson.copy(alpha = 0.12f))
                          .border(1.dp, TitanCrimson.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                          .clickable {
                            viewModel.deleteResumeVariation(activeVariation.id)
                            Toast.makeText(context, "Deleted variation", Toast.LENGTH_SHORT).show()
                          }
                          .padding(horizontal = 9.dp, vertical = 5.dp)
                          .testTag("btn_delete_variation")
                      ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                          Icon(Icons.Default.Delete, contentDescription = null, tint = TitanCrimson, modifier = Modifier.size(12.dp))
                          Spacer(modifier = Modifier.width(4.dp))
                          Text("Delete", fontSize = 10.sp, color = TitanCrimson)
                        }
                      }
                    }
                  }
                }
              }
            }

            // Quick Fill Action Chips
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(8.dp))
                  .background(TitanCyan.copy(alpha = 0.12f))
                  .border(1.dp, TitanCyan.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                  .clickable { viewModel.loadDefaultResumeForComparison() }
                  .padding(horizontal = 10.dp, vertical = 6.dp)
                  .testTag("load_default_resume_chip")
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(Icons.Default.Refresh, contentDescription = null, tint = TitanCyan, modifier = Modifier.size(13.dp))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text("Load Candidate Profile (Adi)", fontSize = 11.sp, color = TitanCyan, fontWeight = FontWeight.Bold)
                }
              }

              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(8.dp))
                  .background(SlateElevated)
                  .border(1.dp, SlateBorder, RoundedCornerShape(8.dp))
                  .clickable { viewModel.clearResumeTextForComparison() }
                  .padding(horizontal = 10.dp, vertical = 6.dp)
                  .testTag("clear_resume_chip")
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(Icons.Default.Edit, contentDescription = null, tint = TextMutedDark, modifier = Modifier.size(13.dp))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text("Clear & Paste Custom", fontSize = 11.sp, color = TextSecondaryDark)
                }
              }
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
              value = resumeText,
              onValueChange = { viewModel.resumeComparisonResumeText.value = it },
              modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 140.dp, max = 260.dp)
                .testTag("resume_content_textfield"),
              placeholder = {
                Text(
                  "Paste your full resume text here (Summary, Experience, Skills, Education, Proof-of-Work)...",
                  fontSize = 12.sp,
                  color = TextMutedDark
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
              shape = RoundedCornerShape(10.dp)
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // SECTION 2: Target Company & Job Requirements Area
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .testTag("company_requirements_card"),
      colors = CardDefaults.cardColors(containerColor = SlateCard),
      shape = RoundedCornerShape(14.dp),
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
              imageVector = Icons.Default.Business,
              contentDescription = null,
              tint = TitanGold,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "2. TARGET COMPANY & REQUIREMENTS",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = TextPrimaryDark,
              letterSpacing = 0.5.sp
            )
          }

          TextButton(
            onClick = { isJdInputExpanded = !isJdInputExpanded }
          ) {
            Text(
              text = if (isJdInputExpanded) "Collapse" else "Expand / Edit",
              fontSize = 11.sp,
              color = TitanGold
            )
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Target Company Preset Chips
        Text(
          text = "Select Company Benchmark:",
          fontSize = 11.sp,
          color = TextMutedDark,
          fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(6.dp))

        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          presets.forEach { preset ->
            val isSelected = selectedCompany.equals(preset.company, ignoreCase = true)
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(if (isSelected) TitanGold.copy(alpha = 0.2f) else SlateElevated)
                .border(
                  width = 1.dp,
                  color = if (isSelected) TitanGold else SlateBorder,
                  shape = RoundedCornerShape(8.dp)
                )
                .clickable { viewModel.selectPresetCompanyForComparison(preset) }
                .padding(horizontal = 10.dp, vertical = 6.dp)
                .testTag("preset_company_${preset.company.lowercase().replace(" ", "_")}")
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = preset.logoEmoji, fontSize = 12.sp)
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                  text = preset.company,
                  fontSize = 11.sp,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                  color = if (isSelected) TitanGold else TextPrimaryDark
                )
              }
            }
          }
        }

        AnimatedVisibility(visible = isJdInputExpanded) {
          Column {
            Spacer(modifier = Modifier.height(12.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              OutlinedTextField(
                value = selectedCompany,
                onValueChange = { viewModel.resumeComparisonSelectedCompany.value = it },
                label = { Text("Company Name", fontSize = 11.sp) },
                modifier = Modifier
                  .weight(1f)
                  .testTag("input_target_company"),
                colors = OutlinedTextFieldDefaults.colors(
                  focusedBorderColor = TitanGold,
                  unfocusedBorderColor = SlateBorder,
                  focusedTextColor = TextPrimaryDark,
                  unfocusedTextColor = TextPrimaryDark
                ),
                singleLine = true,
                shape = RoundedCornerShape(8.dp)
              )

              OutlinedTextField(
                value = selectedRole,
                onValueChange = { viewModel.resumeComparisonSelectedRole.value = it },
                label = { Text("Target Role Title", fontSize = 11.sp) },
                modifier = Modifier
                  .weight(1.3f)
                  .testTag("input_target_role"),
                colors = OutlinedTextFieldDefaults.colors(
                  focusedBorderColor = TitanGold,
                  unfocusedBorderColor = SlateBorder,
                  focusedTextColor = TextPrimaryDark,
                  unfocusedTextColor = TextPrimaryDark
                ),
                singleLine = true,
                shape = RoundedCornerShape(8.dp)
              )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
              text = "Job Requirements / Criteria to Benchmark:",
              fontSize = 11.sp,
              color = TextMutedDark,
              fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(4.dp))

            OutlinedTextField(
              value = jobRequirements,
              onValueChange = { viewModel.resumeComparisonJobRequirements.value = it },
              modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 120.dp, max = 220.dp)
                .testTag("input_job_requirements"),
              placeholder = {
                Text(
                  "Paste exact job requirements, desired qualifications, SLA expectations, and technical stack...",
                  fontSize = 12.sp,
                  color = TextMutedDark
                )
              },
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = TitanGold,
                unfocusedBorderColor = SlateBorder,
                focusedTextColor = TextPrimaryDark,
                unfocusedTextColor = TextPrimaryDark,
                focusedContainerColor = ObsidianDark,
                unfocusedContainerColor = ObsidianDark
              ),
              shape = RoundedCornerShape(10.dp)
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // SECTION 3: Primary Action Trigger
    Button(
      onClick = { viewModel.runResumeJobRequirementsComparison() },
      enabled = !isComparing && resumeText.isNotBlank(),
      modifier = Modifier
        .fillMaxWidth()
        .height(52.dp)
        .testTag("run_resume_comparison_button"),
      colors = ButtonDefaults.buttonColors(
        containerColor = TitanCyan,
        disabledContainerColor = TitanCyan.copy(alpha = 0.4f)
      ),
      shape = RoundedCornerShape(12.dp)
    ) {
      if (isComparing) {
        CircularProgressIndicator(
          modifier = Modifier.size(20.dp),
          color = ObsidianDark,
          strokeWidth = 2.dp
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
          text = "Gemini 3.5 Flash Benchmarking Resume vs $selectedCompany...",
          color = ObsidianDark,
          fontWeight = FontWeight.Bold,
          fontSize = 13.sp
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
          text = "Run Gemini Comparison vs $selectedCompany",
          color = ObsidianDark,
          fontWeight = FontWeight.Bold,
          fontSize = 14.sp
        )
      }
    }

    if (errorMessage != null) {
      Spacer(modifier = Modifier.height(8.dp))
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(8.dp))
          .background(TitanGold.copy(alpha = 0.15f))
          .border(1.dp, TitanGold.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
          .padding(10.dp)
          .testTag("resume_comparison_status_banner")
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Info, contentDescription = null, tint = TitanGold, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Column {
            Text(
              text = if (errorMessage!!.contains("offline", ignoreCase = true) || errorMessage!!.contains("deterministic", ignoreCase = true)) {
                "⚡ Offline Intelligence Active"
              } else {
                "Comparison Notice"
              },
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = TitanGold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = errorMessage ?: "", fontSize = 11.sp, color = TextPrimaryDark)
          }
        }
      }
    }

    // SECTION 3.5: Animated Loading Overlay & Shimmer Effect during comparison
    AnimatedVisibility(
      visible = isComparing,
      enter = fadeIn() + androidx.compose.animation.expandVertically(),
      exit = fadeOut() + androidx.compose.animation.shrinkVertically()
    ) {
      Column(modifier = Modifier.fillMaxWidth()) {
        Spacer(modifier = Modifier.height(16.dp))
        AiAnalysisLoadingShimmerOverlay(
          targetRole = selectedRole.ifBlank { "Senior Operations Lead" },
          targetCompany = selectedCompany.ifBlank { "Target Enterprise" }
        )
      }
    }

    // SECTION 4: Feedback Results Display
    if (!isComparing) {
      feedback?.let { result ->
        Spacer(modifier = Modifier.height(18.dp))

      // Match Score Cards Row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        ComparisonMetricCard(
          title = "Overall Match",
          score = result.overallFitScore,
          badge = if (result.overallFitScore >= 85) "Tier 1 Fit" else "Solid Contender",
          color = if (result.overallFitScore >= 85) TitanEmerald else TitanCyan,
          modifier = Modifier.weight(1f)
        )

        ComparisonMetricCard(
          title = "ATS Pass Rate",
          score = result.atsCompatibilityScore,
          badge = if (result.atsCompatibilityScore >= 90) "High Clearance" else "Keyword Alert",
          color = if (result.atsCompatibilityScore >= 85) TitanEmerald else TitanGold,
          modifier = Modifier.weight(1f)
        )

        ComparisonMetricCard(
          title = "Bar-Raiser Alignment",
          score = result.barRaiserFitScore,
          badge = "Culture Fit",
          color = TitanIndigo,
          modifier = Modifier.weight(1f)
        )
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Executive Verdict Card
      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SlateCard),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, TitanCyan.copy(alpha = 0.4f))
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Bolt, contentDescription = null, tint = TitanCyan, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "EXECUTIVE VERDICT FOR ${result.targetCompany.uppercase()}",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = TitanCyan,
              letterSpacing = 0.8.sp
            )
          }
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = result.readinessVerdict,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimaryDark
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = result.executiveSummary,
            fontSize = 12.sp,
            color = TextSecondaryDark,
            lineHeight = 17.sp
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Feedback Navigation TabRow
      TabRow(
        selectedTabIndex = activeFeedbackTab,
        containerColor = SlateCard,
        contentColor = TitanCyan,
        indicator = { tabPositions ->
          TabRowDefaults.SecondaryIndicator(
            Modifier.tabIndicatorOffset(tabPositions[activeFeedbackTab]),
            color = TitanCyan,
            height = 2.dp
          )
        },
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(8.dp))
          .border(1.dp, SlateBorder, RoundedCornerShape(8.dp))
      ) {
        Tab(
          selected = activeFeedbackTab == 0,
          onClick = { activeFeedbackTab = 0 },
          text = {
            Text(
              text = "Matched (${result.matchedRequirements.size})",
              fontSize = 11.sp,
              fontWeight = if (activeFeedbackTab == 0) FontWeight.Bold else FontWeight.Normal,
              color = if (activeFeedbackTab == 0) TitanEmerald else TextMutedDark
            )
          }
        )
        Tab(
          selected = activeFeedbackTab == 1,
          onClick = { activeFeedbackTab = 1 },
          text = {
            Text(
              text = "Gaps (${result.requirementGaps.size})",
              fontSize = 11.sp,
              fontWeight = if (activeFeedbackTab == 1) FontWeight.Bold else FontWeight.Normal,
              color = if (activeFeedbackTab == 1) TitanGold else TextMutedDark
            )
          }
        )
        Tab(
          selected = activeFeedbackTab == 2,
          onClick = { activeFeedbackTab = 2 },
          text = {
            Text(
              text = "Bullet Rewrites (${result.bulletEnhancements.size})",
              fontSize = 11.sp,
              fontWeight = if (activeFeedbackTab == 2) FontWeight.Bold else FontWeight.Normal,
              color = if (activeFeedbackTab == 2) TitanCyan else TextMutedDark
            )
          }
        )
        Tab(
          selected = activeFeedbackTab == 3,
          onClick = { activeFeedbackTab = 3 },
          text = {
            Text(
              text = "Bar-Raiser Intel",
              fontSize = 11.sp,
              fontWeight = if (activeFeedbackTab == 3) FontWeight.Bold else FontWeight.Normal,
              color = if (activeFeedbackTab == 3) TitanIndigo else TextMutedDark
            )
          }
        )
        Tab(
          selected = activeFeedbackTab == 4,
          onClick = { activeFeedbackTab = 4 },
          text = {
            Text(
              text = "Action Plan (${result.actionPlan.size})",
              fontSize = 11.sp,
              fontWeight = if (activeFeedbackTab == 4) FontWeight.Bold else FontWeight.Normal,
              color = if (activeFeedbackTab == 4) TitanCyan else TextMutedDark
            )
          }
        )
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Tab Content View
      when (activeFeedbackTab) {
        0 -> {
          // Matched Requirements View
          Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            result.matchedRequirements.forEach { match ->
              MatchedRequirementCard(match = match)
            }
          }
        }
        1 -> {
          // Requirement Gaps View
          Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            result.requirementGaps.forEach { gap ->
              RequirementGapCard(gap = gap)
            }
          }
        }
        2 -> {
          // Side-by-Side Bullet Rewrites View
          Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
              text = "1-Tap Apply: Replace weak/generic resume bullets directly in your resume text input above.",
              fontSize = 11.sp,
              color = TextMutedDark,
              fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
            )
            result.bulletEnhancements.forEach { bullet ->
              BulletEnhancementCard(
                enhancement = bullet,
                onApplyToggle = { viewModel.applyComparisonBulletRewrite(bullet.id) }
              )
            }
          }
        }
        3 -> {
          // Bar-Raiser Culture Intel View
          BarRaiserIntelCard(intel = result.companyCultureIntel)
        }
        4 -> {
          // Action Plan Checklist View
          Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            result.actionPlan.forEach { step ->
              ActionStepCard(step = step)
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Utility Buttons: Copy Feedback & Copy Updated Resume
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        OutlinedButton(
          onClick = {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Resume Comparison Feedback", generateFeedbackCopyText(result))
            clipboard.setPrimaryClip(clip)
            Toast.makeText(context, "Full comparison report copied to clipboard!", Toast.LENGTH_SHORT).show()
          },
          modifier = Modifier.weight(1f),
          colors = ButtonDefaults.outlinedButtonColors(contentColor = TitanCyan),
          border = androidx.compose.foundation.BorderStroke(1.dp, TitanCyan.copy(alpha = 0.5f)),
          shape = RoundedCornerShape(10.dp)
        ) {
          Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Copy Report", fontSize = 12.sp)
        }

        Button(
          onClick = {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Updated Tailored Resume", resumeText)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(context, "Updated resume text copied to clipboard!", Toast.LENGTH_SHORT).show()
          },
          modifier = Modifier.weight(1f),
          colors = ButtonDefaults.buttonColors(containerColor = TitanEmerald),
          shape = RoundedCornerShape(10.dp)
        ) {
          Icon(Icons.Default.Done, contentDescription = null, tint = ObsidianDark, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Copy Tailored Resume", color = ObsidianDark, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }
      }
    }
  }

    Spacer(modifier = Modifier.height(30.dp))
  }

  // Dialog: Save as New Version (Branching & Version Increment)
  if (showSaveVersionDialog) {
    AlertDialog(
      onDismissRequest = { viewModel.showSaveVersionDialog.value = false },
      containerColor = SlateCard,
      title = {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.History, contentDescription = null, tint = TitanEmerald, modifier = Modifier.size(20.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Save Version v${(activeVariation?.versionNumber ?: 1) + 1}.0",
            color = TextPrimaryDark,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
          )
        }
      },
      text = {
        Column {
          Text(
            text = "Branching from: ${activeVariation?.title ?: "Master Resume"} (${activeVariation?.versionLabel ?: "v1.0"})",
            fontSize = 12.sp,
            color = TextSecondaryDark
          )
          Spacer(modifier = Modifier.height(10.dp))
          Text(
            text = "Enter version notes explaining recent bullet improvements, SLA metrics, or targeted keywords:",
            fontSize = 11.sp,
            color = TextMutedDark
          )
          Spacer(modifier = Modifier.height(8.dp))
          OutlinedTextField(
            value = versionNotesInput,
            onValueChange = { versionNotesInput = it },
            placeholder = { Text("e.g. Infused sub-10m dark store picking metrics & SLA data", fontSize = 11.sp) },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 3,
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = TitanEmerald,
              unfocusedBorderColor = SlateBorder,
              focusedTextColor = TextPrimaryDark,
              unfocusedTextColor = TextPrimaryDark
            )
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            viewModel.saveCurrentResumeAsNewVersion(versionNotesInput)
            Toast.makeText(context, "Saved new resume version!", Toast.LENGTH_SHORT).show()
          },
          colors = ButtonDefaults.buttonColors(containerColor = TitanEmerald)
        ) {
          Text("Save Version", color = ObsidianDark, fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { viewModel.showSaveVersionDialog.value = false }) {
          Text("Cancel", color = TextSecondaryDark)
        }
      }
    )
  }

  // Dialog: Create New Tailored Variation
  if (showCreateVariationDialog) {
    AlertDialog(
      onDismissRequest = { viewModel.showCreateVariationDialog.value = false },
      containerColor = SlateCard,
      title = {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Add, contentDescription = null, tint = TitanCyan, modifier = Modifier.size(20.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "New Tailored Resume Variation",
            color = TextPrimaryDark,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
          )
        }
      },
      text = {
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
          Text(
            text = "Create a separate variation branch tailored for a specific company or target domain.",
            fontSize = 11.sp,
            color = TextSecondaryDark
          )
          Spacer(modifier = Modifier.height(10.dp))

          Text("Variation Title", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
          Spacer(modifier = Modifier.height(4.dp))
          OutlinedTextField(
            value = newTitleInput,
            onValueChange = { newTitleInput = it },
            placeholder = { Text("e.g. Zepto Quick Commerce Lead", fontSize = 11.sp) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = TitanCyan,
              unfocusedBorderColor = SlateBorder,
              focusedTextColor = TextPrimaryDark,
              unfocusedTextColor = TextPrimaryDark
            )
          )

          Spacer(modifier = Modifier.height(8.dp))
          Text("Target Company", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
          Spacer(modifier = Modifier.height(4.dp))
          OutlinedTextField(
            value = newCompanyInput,
            onValueChange = { newCompanyInput = it },
            placeholder = { Text("e.g. Zepto, Razorpay, Blinkit", fontSize = 11.sp) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = TitanCyan,
              unfocusedBorderColor = SlateBorder,
              focusedTextColor = TextPrimaryDark,
              unfocusedTextColor = TextPrimaryDark
            )
          )

          Spacer(modifier = Modifier.height(8.dp))
          Text("Target Role", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
          Spacer(modifier = Modifier.height(4.dp))
          OutlinedTextField(
            value = newRoleInput,
            onValueChange = { newRoleInput = it },
            placeholder = { Text("e.g. Operations & Strategy Lead", fontSize = 11.sp) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = TitanCyan,
              unfocusedBorderColor = SlateBorder,
              focusedTextColor = TextPrimaryDark,
              unfocusedTextColor = TextPrimaryDark
            )
          )

          Spacer(modifier = Modifier.height(8.dp))
          Text("Domain Tags", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
          Spacer(modifier = Modifier.height(4.dp))
          OutlinedTextField(
            value = newTagsInput,
            onValueChange = { newTagsInput = it },
            placeholder = { Text("e.g. Quick Commerce, SLA, Logistics", fontSize = 11.sp) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = TitanCyan,
              unfocusedBorderColor = SlateBorder,
              focusedTextColor = TextPrimaryDark,
              unfocusedTextColor = TextPrimaryDark
            )
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            viewModel.createNewResumeVariation(
              title = newTitleInput.ifBlank { "${newCompanyInput.ifBlank { "Custom" }} Tailored Resume" },
              targetCompany = newCompanyInput.ifBlank { "General" },
              targetRole = newRoleInput.ifBlank { "Operations Lead" },
              resumeText = resumeText,
              versionNotes = "Initial tailored variation created",
              tags = newTagsInput.ifBlank { "Tailored" },
              setAsPrimary = false
            )
            Toast.makeText(context, "Created new resume variation!", Toast.LENGTH_SHORT).show()
          },
          colors = ButtonDefaults.buttonColors(containerColor = TitanCyan)
        ) {
          Text("Create Variation", color = ObsidianDark, fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { viewModel.showCreateVariationDialog.value = false }) {
          Text("Cancel", color = TextSecondaryDark)
        }
      }
    )
  }
}

@Composable
private fun ComparisonMetricCard(
  title: String,
  score: Int,
  badge: String,
  color: Color,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier,
    colors = CardDefaults.cardColors(containerColor = SlateCard),
    shape = RoundedCornerShape(12.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
  ) {
    Column(
      modifier = Modifier.padding(12.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(
        text = title,
        fontSize = 11.sp,
        fontWeight = FontWeight.Medium,
        color = TextSecondaryDark,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = "$score%",
        fontSize = 22.sp,
        fontWeight = FontWeight.Black,
        color = color
      )
      Spacer(modifier = Modifier.height(4.dp))
      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(4.dp))
          .background(color.copy(alpha = 0.15f))
          .padding(horizontal = 6.dp, vertical = 2.dp)
      ) {
        Text(
          text = badge,
          fontSize = 9.sp,
          fontWeight = FontWeight.Bold,
          color = color
        )
      }
    }
  }
}

@Composable
private fun MatchedRequirementCard(match: MatchedRequirementItem) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    colors = CardDefaults.cardColors(containerColor = SlateCard),
    shape = RoundedCornerShape(12.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, TitanEmerald.copy(alpha = 0.3f))
  ) {
    Column(modifier = Modifier.padding(12.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.CheckCircle, contentDescription = null, tint = TitanEmerald, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = match.requirementTitle,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimaryDark
          )
        }
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(TitanEmerald.copy(alpha = 0.15f))
            .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
          Text(
            text = match.matchStrength,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = TitanEmerald
          )
        }
      }
      Spacer(modifier = Modifier.height(6.dp))
      Text(
        text = "Resume Evidence: ${match.resumeEvidence}",
        fontSize = 11.sp,
        color = TextSecondaryDark,
        lineHeight = 16.sp
      )
    }
  }
}

@Composable
private fun RequirementGapCard(gap: RequirementGapItem) {
  val isCritical = gap.severity.contains("CRITICAL")
  val badgeColor = if (isCritical) TitanCrimson else TitanGold

  Card(
    modifier = Modifier.fillMaxWidth(),
    colors = CardDefaults.cardColors(containerColor = SlateCard),
    shape = RoundedCornerShape(12.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, badgeColor.copy(alpha = 0.4f))
  ) {
    Column(modifier = Modifier.padding(12.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Warning, contentDescription = null, tint = badgeColor, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = gap.missingSkillOrCriteria,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimaryDark
          )
        }
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(badgeColor.copy(alpha = 0.15f))
            .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
          Text(
            text = if (isCritical) "CRITICAL GAP" else "MODERATE GAP",
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = badgeColor
          )
        }
      }
      Spacer(modifier = Modifier.height(6.dp))
      Text(
        text = "Why Demanded: ${gap.whyCompanyDemandsThis}",
        fontSize = 11.sp,
        color = TextSecondaryDark,
        lineHeight = 16.sp
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = "Actionable Fix: ${gap.actionableRemedy}",
        fontSize = 11.sp,
        color = TitanGold,
        fontWeight = FontWeight.Medium,
        lineHeight = 16.sp
      )
      if (gap.suggestedProjectOrProof.isNotBlank()) {
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = "Suggested Proof: ${gap.suggestedProjectOrProof}",
          fontSize = 10.sp,
          color = TextMutedDark,
          lineHeight = 14.sp
        )
      }
    }
  }
}

@Composable
private fun BulletEnhancementCard(
  enhancement: ComparisonBulletEnhancement,
  onApplyToggle: () -> Unit
) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    colors = CardDefaults.cardColors(containerColor = SlateCard),
    shape = RoundedCornerShape(12.dp),
    border = androidx.compose.foundation.BorderStroke(
      1.dp,
      if (enhancement.isApplied) TitanEmerald else SlateBorder
    )
  ) {
    Column(modifier = Modifier.padding(12.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(TitanCyan.copy(alpha = 0.12f))
            .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
          Text(
            text = "TARGETS: ${enhancement.targetedRequirement}",
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = TitanCyan
          )
        }

        Button(
          onClick = onApplyToggle,
          colors = ButtonDefaults.buttonColors(
            containerColor = if (enhancement.isApplied) TitanEmerald else TitanCyan
          ),
          shape = RoundedCornerShape(6.dp),
          modifier = Modifier.height(28.dp),
          contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 0.dp)
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = if (enhancement.isApplied) Icons.Default.Check else Icons.Default.AutoAwesome,
              contentDescription = null,
              tint = ObsidianDark,
              modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = if (enhancement.isApplied) "Applied" else "Apply to Resume",
              fontSize = 10.sp,
              color = ObsidianDark,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Original Bullet
      Text(
        text = "ORIGINAL CANDIDATE BULLET:",
        fontSize = 9.sp,
        fontWeight = FontWeight.Bold,
        color = TextMutedDark
      )
      Text(
        text = enhancement.originalBullet,
        fontSize = 11.sp,
        color = TextSecondaryDark,
        lineHeight = 16.sp
      )

      Spacer(modifier = Modifier.height(8.dp))

      // Enhanced Bullet
      Text(
        text = "TAILORED POWER BULLET (INFUSED WITH TARGET REQS):",
        fontSize = 9.sp,
        fontWeight = FontWeight.Bold,
        color = TitanEmerald
      )
      Text(
        text = enhancement.enhancedBullet,
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
        color = TextPrimaryDark,
        lineHeight = 17.sp
      )

      Spacer(modifier = Modifier.height(6.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Text(
          text = "Metric Added: ${enhancement.metricInfused}",
          fontSize = 10.sp,
          color = TitanGold,
          fontWeight = FontWeight.Medium
        )
        if (enhancement.keywordsAdded.isNotEmpty()) {
          Text(
            text = "Keywords: ${enhancement.keywordsAdded.take(3).joinToString(", ")}",
            fontSize = 10.sp,
            color = TitanCyan
          )
        }
      }
    }
  }
}

@Composable
private fun BarRaiserIntelCard(intel: com.example.data.model.TargetCompanyCultureIntel) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    colors = CardDefaults.cardColors(containerColor = SlateCard),
    shape = RoundedCornerShape(12.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, TitanIndigo.copy(alpha = 0.4f))
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.Psychology, contentDescription = null, tint = TitanIndigo, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "${intel.companyName.uppercase()} HIRING CULTURE & BAR-RAISER INTEL",
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold,
          color = TitanIndigo,
          letterSpacing = 0.5.sp
        )
      }
      Spacer(modifier = Modifier.height(10.dp))

      Text(
        text = "Bar-Raiser Core Focus:",
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = TextPrimaryDark
      )
      Text(
        text = intel.barRaiserFocus,
        fontSize = 12.sp,
        color = TextSecondaryDark,
        lineHeight = 16.sp
      )

      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = "Core Cultural Pillar:",
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = TextPrimaryDark
      )
      Text(
        text = intel.culturePillar,
        fontSize = 12.sp,
        color = TextSecondaryDark,
        lineHeight = 16.sp
      )

      if (intel.commonInterviewTraps.isNotEmpty()) {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
          text = "Common Interview Traps to Avoid:",
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          color = TitanCrimson
        )
        intel.commonInterviewTraps.forEach { trap ->
          Text(
            text = "• $trap",
            fontSize = 11.sp,
            color = TextSecondaryDark,
            lineHeight = 16.sp
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = "Recommended Positioning Angle:",
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = TitanCyan
      )
      Text(
        text = intel.recommendedAngle,
        fontSize = 12.sp,
        color = TextPrimaryDark,
        lineHeight = 16.sp
      )
    }
  }
}

@Composable
private fun ActionStepCard(step: ComparisonActionStep) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    colors = CardDefaults.cardColors(containerColor = SlateCard),
    shape = RoundedCornerShape(10.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
  ) {
    Row(
      modifier = Modifier.padding(12.dp),
      verticalAlignment = Alignment.Top
    ) {
      Box(
        modifier = Modifier
          .size(24.dp)
          .clip(CircleShape)
          .background(TitanCyan.copy(alpha = 0.2f)),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = "${step.priorityOrder}",
          fontSize = 11.sp,
          fontWeight = FontWeight.Black,
          color = TitanCyan
        )
      }
      Spacer(modifier = Modifier.width(10.dp))
      Column(modifier = Modifier.weight(1f)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text(
            text = step.title,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimaryDark
          )
          Text(
            text = step.estimatedEffort,
            fontSize = 10.sp,
            color = TitanGold,
            fontWeight = FontWeight.Medium
          )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = step.details,
          fontSize = 11.sp,
          color = TextSecondaryDark,
          lineHeight = 16.sp
        )
      }
    }
  }
}

private fun generateFeedbackCopyText(result: ResumeJobComparisonFeedback): String {
  val sb = StringBuilder()
  sb.appendLine("=== TITAN AI RESUME VS JOB COMPARISON REPORT ===")
  sb.appendLine("Target Company: ${result.targetCompany}")
  sb.appendLine("Target Role: ${result.targetRole}")
  sb.appendLine("Overall Fit: ${result.overallFitScore}% | ATS Pass Rate: ${result.atsCompatibilityScore}% | Bar Raiser Score: ${result.barRaiserFitScore}%")
  sb.appendLine("Verdict: ${result.readinessVerdict}")
  sb.appendLine()
  sb.appendLine("--- EXECUTIVE SUMMARY ---")
  sb.appendLine(result.executiveSummary)
  sb.appendLine()
  sb.appendLine("--- CORE MATCHED REQUIREMENTS ---")
  result.matchedRequirements.forEach {
    sb.appendLine("• [${it.matchStrength}] ${it.requirementTitle}: ${it.resumeEvidence}")
  }
  sb.appendLine()
  sb.appendLine("--- CRITICAL GAPS & MISSING CRITERIA ---")
  result.requirementGaps.forEach {
    sb.appendLine("• [${it.severity}] ${it.missingSkillOrCriteria}: ${it.whyCompanyDemandsThis} -> FIX: ${it.actionableRemedy}")
  }
  sb.appendLine()
  sb.appendLine("--- SIDE-BY-SIDE BULLET ENHANCEMENTS ---")
  result.bulletEnhancements.forEach {
    sb.appendLine("ORIGINAL: ${it.originalBullet}")
    sb.appendLine("ENHANCED: ${it.enhancedBullet}")
    sb.appendLine("METRIC: ${it.metricInfused}")
    sb.appendLine()
  }
  sb.appendLine("--- ACTION PLAN ---")
  result.actionPlan.forEach {
    sb.appendLine("${it.priorityOrder}. ${it.title} (${it.estimatedEffort}): ${it.details}")
  }
  return sb.toString()
}
