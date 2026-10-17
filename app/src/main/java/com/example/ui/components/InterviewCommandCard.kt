package com.example.ui.components

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Work
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.InterviewCommandDossier
import com.example.data.model.InterviewCommandQuestion
import com.example.data.model.StructuredTalkingPoint
import com.example.ui.theme.ObsidianDark
import androidx.compose.ui.text.style.TextOverflow
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

/**
 * 'Interview Command' Module embedded into the Application Hub.
 * Generates role-specific mock interview questions and structured talking points
 * dynamically based on the job title and company selected in the Application Hub.
 */
@Composable
fun InterviewCommandCard(
  viewModel: TitanViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val clipboardManager = LocalClipboardManager.current

  val selectedCompany by viewModel.selectedInterviewCommandCompany.collectAsState()
  val selectedRole by viewModel.selectedInterviewCommandRole.collectAsState()
  val dossier by viewModel.interviewCommandDossier.collectAsState()
  val isGenerating by viewModel.isGeneratingInterviewCommand.collectAsState()
  val isExpanded by viewModel.isInterviewCommandExpanded.collectAsState()
  val activeTab by viewModel.selectedInterviewCommandTab.collectAsState()
  val pillarFilter by viewModel.selectedInterviewCommandPillarFilter.collectAsState()
  val practicedIds by viewModel.interviewCommandPracticedQuestionIds.collectAsState()
  val practiceNotes by viewModel.interviewCommandPracticeNotes.collectAsState()

  // Targets from Application Hub (Room SQLite targets & Firestore applications)
  val corporateTargets by viewModel.corporateApplicationTargets.collectAsState()
  val applications by viewModel.applications.collectAsState()

  var showCustomTargetFields by remember { mutableStateOf(false) }
  var customCompanyInput by remember { mutableStateOf(selectedCompany) }
  var customRoleInput by remember { mutableStateOf(selectedRole) }
  var questionCategoryFilter by remember { mutableStateOf("ALL") }

  // Auto-generate on launch if empty
  LaunchedEffect(Unit) {
    if (dossier == null && !isGenerating) {
      viewModel.generateInterviewCommand(forceRefresh = false)
    }
  }

  // Combined list of distinct target pairs available in the Application Hub
  val hubTargetPairs = remember(corporateTargets, applications) {
    val pairs = mutableListOf<Pair<String, String>>()
    corporateTargets.forEach { target ->
      if (pairs.none { it.first.equals(target.companyName, ignoreCase = true) }) {
        pairs.add(target.companyName to target.targetRoleTitle)
      }
    }
    applications.forEach { app ->
      if (pairs.none { it.first.equals(app.companyName, ignoreCase = true) }) {
        pairs.add(app.companyName to app.roleTitle)
      }
    }
    if (pairs.none { it.first.equals("Zepto", ignoreCase = true) }) {
      pairs.add("Zepto" to "Lead - Strategy & Dark Store Operations")
    }
    if (pairs.none { it.first.equals("Swiggy", ignoreCase = true) }) {
      pairs.add("Swiggy" to "Strategy & Operations Lead")
    }
    if (pairs.none { it.first.equals("Razorpay", ignoreCase = true) }) {
      pairs.add("Razorpay" to "FinTech Business Analyst")
    }
    if (pairs.none { it.first.equals("Google", ignoreCase = true) }) {
      pairs.add("Google" to "Associate Product Manager")
    }
    pairs
  }

  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("interview_command_card")
      .animateContentSize(),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = SlateCard),
    border = BorderStroke(1.2.dp, TitanGold.copy(alpha = 0.55f))
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp)
    ) {
      // -----------------------------------------------------------------------
      // Top Header Row
      // -----------------------------------------------------------------------
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(10.dp),
          modifier = Modifier.weight(1f)
        ) {
          Box(
            modifier = Modifier
              .size(38.dp)
              .clip(RoundedCornerShape(8.dp))
              .background(TitanGold.copy(alpha = 0.15f))
              .border(1.dp, TitanGold.copy(alpha = 0.45f), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Psychology,
              contentDescription = "Interview Command",
              tint = TitanGold,
              modifier = Modifier.size(22.dp)
            )
          }

          Column {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Text(
                text = "INTERVIEW COMMAND MODULE",
                style = MaterialTheme.typography.labelSmall,
                color = TitanGold,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
              )
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(4.dp))
                  .background(TitanCyan.copy(alpha = 0.15f))
                  .padding(horizontal = 5.dp, vertical = 1.dp)
              ) {
                Text(
                  text = "APPLICATION HUB SYNCED",
                  style = MaterialTheme.typography.labelSmall,
                  color = TitanCyan,
                  fontWeight = FontWeight.Bold,
                  fontSize = 8.5.sp
                )
              }
            }

            Text(
              text = "$selectedCompany • $selectedRole",
              style = MaterialTheme.typography.titleMedium,
              color = TextPrimaryDark,
              fontWeight = FontWeight.Bold,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
          }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          IconButton(
            onClick = { viewModel.toggleInterviewCommandExpanded() },
            modifier = Modifier.testTag("toggle_interview_command_expand")
          ) {
            Icon(
              imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
              contentDescription = if (isExpanded) "Collapse" else "Expand",
              tint = TextSecondaryDark
            )
          }
        }
      }

      // -----------------------------------------------------------------------
      // Application Hub Quick Target Selector Chips
      // -----------------------------------------------------------------------
      Spacer(modifier = Modifier.height(10.dp))
      Text(
        text = "SELECT TARGET FROM APPLICATION HUB:",
        style = MaterialTheme.typography.labelSmall,
        color = TextSecondaryDark,
        fontWeight = FontWeight.Bold,
        fontSize = 9.5.sp
      )
      Spacer(modifier = Modifier.height(6.dp))

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        hubTargetPairs.forEach { (co, role) ->
          val isSelected = selectedCompany.equals(co, ignoreCase = true)
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(if (isSelected) TitanGold else SlateElevated)
              .border(1.dp, if (isSelected) TitanGold else SlateBorder, RoundedCornerShape(6.dp))
              .clickable {
                customCompanyInput = co
                customRoleInput = role
                viewModel.selectInterviewCommandTarget(co, role, autoGenerate = true)
              }
              .padding(horizontal = 9.dp, vertical = 5.dp)
              .testTag("hub_target_chip_${co.lowercase().replace(" ", "_")}")
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              if (isSelected) {
                Icon(
                  Icons.Default.Check,
                  contentDescription = null,
                  tint = ObsidianDark,
                  modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
              }
              Text(
                text = co,
                style = MaterialTheme.typography.labelSmall,
                color = if (isSelected) ObsidianDark else TextPrimaryDark,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
              )
            }
          }
        }

        // Custom Input Toggle
        IconButton(
          onClick = { showCustomTargetFields = !showCustomTargetFields },
          modifier = Modifier
            .size(28.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(SlateElevated)
            .border(1.dp, SlateBorder, RoundedCornerShape(6.dp))
            .testTag("toggle_custom_target_fields")
        ) {
          Icon(
            imageVector = if (showCustomTargetFields) Icons.Default.Close else Icons.Default.Edit,
            contentDescription = "Edit Custom Target",
            tint = TitanGold,
            modifier = Modifier.size(14.dp)
          )
        }
      }

      // -----------------------------------------------------------------------
      // Custom Target Input Fields (Expandable)
      // -----------------------------------------------------------------------
      AnimatedVisibility(visible = showCustomTargetFields) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(SlateDarker)
            .padding(10.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Text(
            text = "CUSTOM COMPANY & ROLE PROMPT",
            style = MaterialTheme.typography.labelSmall,
            color = TitanGold,
            fontWeight = FontWeight.Bold
          )

          OutlinedTextField(
            value = customCompanyInput,
            onValueChange = { customCompanyInput = it },
            label = { Text("Company Name (e.g. Zepto, Bain, Stripe)") },
            leadingIcon = {
              Icon(Icons.Default.Business, contentDescription = null, tint = TitanGold, modifier = Modifier.size(16.dp))
            },
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("custom_company_input"),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = TitanGold,
              unfocusedBorderColor = SlateBorder,
              focusedTextColor = TextPrimaryDark,
              unfocusedTextColor = TextPrimaryDark
            )
          )

          OutlinedTextField(
            value = customRoleInput,
            onValueChange = { customRoleInput = it },
            label = { Text("Role Title (e.g. Dark Store Ops Lead, Product Manager)") },
            leadingIcon = {
              Icon(Icons.Default.Work, contentDescription = null, tint = TitanGold, modifier = Modifier.size(16.dp))
            },
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("custom_role_input"),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = TitanGold,
              unfocusedBorderColor = SlateBorder,
              focusedTextColor = TextPrimaryDark,
              unfocusedTextColor = TextPrimaryDark
            )
          )

          Button(
            onClick = {
              viewModel.selectInterviewCommandTarget(customCompanyInput, customRoleInput, autoGenerate = true)
              showCustomTargetFields = false
            },
            enabled = customCompanyInput.isNotBlank() && customRoleInput.isNotBlank(),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("generate_custom_target_button"),
            colors = ButtonDefaults.buttonColors(containerColor = TitanGold, contentColor = ObsidianDark),
            shape = RoundedCornerShape(8.dp)
          ) {
            Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Generate Role-Specific Command Dossier", fontWeight = FontWeight.Bold, fontSize = 12.sp)
          }
        }
      }

      // -----------------------------------------------------------------------
      // Main Body Content
      // -----------------------------------------------------------------------
      AnimatedVisibility(visible = isExpanded) {
        Column(modifier = Modifier.padding(top = 12.dp)) {
          // Status bar / Quick refresh & stats
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              val questionsCount = dossier?.mockQuestions?.size ?: 0
              val practicedCount = dossier?.mockQuestions?.count { practicedIds.contains(it.id) } ?: 0

              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(4.dp))
                  .background(TitanEmerald.copy(alpha = 0.15f))
                  .padding(horizontal = 6.dp, vertical = 2.dp)
              ) {
                Text(
                  text = "$practicedCount/$questionsCount Questions Practiced",
                  style = MaterialTheme.typography.labelSmall,
                  color = TitanEmerald,
                  fontWeight = FontWeight.Bold,
                  fontSize = 10.sp
                )
              }

              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(4.dp))
                  .background(TitanIndigo.copy(alpha = 0.15f))
                  .padding(horizontal = 6.dp, vertical = 2.dp)
              ) {
                Text(
                  text = "${dossier?.strategicTalkingPoints?.size ?: 0} Talking Points",
                  style = MaterialTheme.typography.labelSmall,
                  color = TitanIndigo,
                  fontWeight = FontWeight.Bold,
                  fontSize = 10.sp
                )
              }
            }

            IconButton(
              onClick = {
                viewModel.generateInterviewCommand(forceRefresh = true)
              },
              enabled = !isGenerating,
              modifier = Modifier
                .size(28.dp)
                .testTag("regenerate_interview_command_button")
            ) {
              if (isGenerating) {
                CircularProgressIndicator(modifier = Modifier.size(16.dp), color = TitanGold, strokeWidth = 2.dp)
              } else {
                Icon(Icons.Default.Refresh, contentDescription = "Regenerate", tint = TitanGold, modifier = Modifier.size(16.dp))
              }
            }
          }

          // Loading Indicator
          if (isGenerating && dossier == null) {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
              contentAlignment = Alignment.Center
            ) {
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator(color = TitanGold, modifier = Modifier.size(32.dp))
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                  text = "Generating role-specific mock questions & talking points with Gemini...",
                  style = MaterialTheme.typography.bodySmall,
                  color = TextSecondaryDark,
                  fontSize = 11.sp
                )
              }
            }
          } else {
            dossier?.let { currentDossier ->
              // Executive Briefing Banner
              Spacer(modifier = Modifier.height(8.dp))
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(8.dp))
                  .background(SlateElevated)
                  .border(1.dp, SlateBorder, RoundedCornerShape(8.dp))
                  .padding(10.dp)
              ) {
                Column {
                  Text(
                    text = "🎯 INTERVIEW EVALUATION CONTEXT",
                    style = MaterialTheme.typography.labelSmall,
                    color = TitanGold,
                    fontWeight = FontWeight.Black,
                    fontSize = 9.sp
                  )
                  Spacer(modifier = Modifier.height(3.dp))
                  Text(
                    text = currentDossier.executiveContext,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextPrimaryDark,
                    fontSize = 11.5.sp,
                    lineHeight = 16.sp
                  )
                  if (currentDossier.candidateMappingRationale.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                      text = "Candidate Mapping: ${currentDossier.candidateMappingRationale}",
                      style = MaterialTheme.typography.bodySmall,
                      color = TitanCyan,
                      fontSize = 10.5.sp
                    )
                  }
                }
              }

              // -----------------------------------------------------------------
              // Tab Navigation: 0 = Structured Talking Points, 1 = Mock Questions, 2 = Closing Traps
              // -----------------------------------------------------------------
              Spacer(modifier = Modifier.height(10.dp))
              TabRow(
                selectedTabIndex = activeTab,
                containerColor = SlateDarker,
                contentColor = TitanGold,
                indicator = { tabPositions ->
                  TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[activeTab]),
                    color = TitanGold,
                    height = 2.dp
                  )
                },
                modifier = Modifier
                  .clip(RoundedCornerShape(8.dp))
                  .testTag("interview_command_tab_row")
              ) {
                Tab(
                  selected = activeTab == 0,
                  onClick = { viewModel.setInterviewCommandTab(0) },
                  text = {
                    Text(
                      text = "Talking Points (${currentDossier.strategicTalkingPoints.size})",
                      fontSize = 11.sp,
                      fontWeight = if (activeTab == 0) FontWeight.Bold else FontWeight.Medium,
                      color = if (activeTab == 0) TitanGold else TextSecondaryDark
                    )
                  },
                  modifier = Modifier.testTag("interview_command_tab_talking_points")
                )
                Tab(
                  selected = activeTab == 1,
                  onClick = { viewModel.setInterviewCommandTab(1) },
                  text = {
                    Text(
                      text = "Mock Questions (${currentDossier.mockQuestions.size})",
                      fontSize = 11.sp,
                      fontWeight = if (activeTab == 1) FontWeight.Bold else FontWeight.Medium,
                      color = if (activeTab == 1) TitanGold else TextSecondaryDark
                    )
                  },
                  modifier = Modifier.testTag("interview_command_tab_mock_questions")
                )
                Tab(
                  selected = activeTab == 2,
                  onClick = { viewModel.setInterviewCommandTab(2) },
                  text = {
                    Text(
                      text = "Interviewer Questions (${currentDossier.highImpactQuestionsForInterviewer.size})",
                      fontSize = 11.sp,
                      fontWeight = if (activeTab == 2) FontWeight.Bold else FontWeight.Medium,
                      color = if (activeTab == 2) TitanGold else TextSecondaryDark
                    )
                  },
                  modifier = Modifier.testTag("interview_command_tab_closing_questions")
                )
              }

              Spacer(modifier = Modifier.height(10.dp))

              // =================================================================
              // TAB 0: STRUCTURED TALKING POINTS
              // =================================================================
              if (activeTab == 0) {
                // Pillar Filter Chips
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                  horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                  val pillars = listOf("ALL", "EXECUTIVE_HOOK", "METRICS_PROOF", "STRATEGIC_ALIGNMENT", "STAR_FRAMEWORK", "PITFALLS_AVOID")
                  pillars.forEach { pillar ->
                    val isSelected = pillarFilter == pillar
                    val label = when (pillar) {
                      "ALL" -> "All Pillars"
                      "EXECUTIVE_HOOK" -> "60s Hook"
                      "METRICS_PROOF" -> "Metrics Proof"
                      "STRATEGIC_ALIGNMENT" -> "Strategic Fit"
                      "STAR_FRAMEWORK" -> "STAR Framework"
                      "PITFALLS_AVOID" -> "Pitfalls & Traps"
                      else -> pillar
                    }
                    Box(
                      modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isSelected) TitanGold else SlateElevated)
                        .border(1.dp, if (isSelected) TitanGold else SlateBorder, RoundedCornerShape(6.dp))
                        .clickable { viewModel.setInterviewCommandPillarFilter(pillar) }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .testTag("pillar_filter_$pillar")
                    ) {
                      Text(
                        text = label,
                        color = if (isSelected) ObsidianDark else TextSecondaryDark,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                      )
                    }
                  }
                }

                Spacer(modifier = Modifier.height(8.dp))

                val filteredTalkingPoints = remember(currentDossier.strategicTalkingPoints, pillarFilter) {
                  if (pillarFilter == "ALL") currentDossier.strategicTalkingPoints
                  else currentDossier.strategicTalkingPoints.filter { it.pillar.equals(pillarFilter, ignoreCase = true) }
                }

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                  filteredTalkingPoints.forEach { point ->
                    StructuredTalkingPointCard(
                      point = point,
                      onCopy = {
                        val text = "${point.title}\n${point.bulletPoints.joinToString("\n") { "• $it" }}\nImpact: ${point.impactMetric}"
                        clipboardManager.setText(AnnotatedString(text))
                        Toast.makeText(context, "Talking point copied!", Toast.LENGTH_SHORT).show()
                      }
                    )
                  }
                }
              }

              // =================================================================
              // TAB 1: ROLE-SPECIFIC MOCK QUESTIONS
              // =================================================================
              if (activeTab == 1) {
                // Category Filter
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                  horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                  val categories = listOf("ALL", "BEHAVIORAL_STAR", "ANALYTICAL_CASE", "STRATEGIC_FIT", "ROLE_DOMAIN", "BAR_RAISER")
                  categories.forEach { cat ->
                    val isSelected = questionCategoryFilter == cat
                    val label = when (cat) {
                      "ALL" -> "All (${currentDossier.mockQuestions.size})"
                      "BEHAVIORAL_STAR" -> "Behavioral"
                      "ANALYTICAL_CASE" -> "Analytics Case"
                      "STRATEGIC_FIT" -> "Strategy Fit"
                      "ROLE_DOMAIN" -> "Role Domain"
                      "BAR_RAISER" -> "Bar Raiser"
                      else -> cat
                    }
                    Box(
                      modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isSelected) TitanCyan else SlateElevated)
                        .border(1.dp, if (isSelected) TitanCyan else SlateBorder, RoundedCornerShape(6.dp))
                        .clickable { questionCategoryFilter = cat }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .testTag("question_category_$cat")
                    ) {
                      Text(
                        text = label,
                        color = if (isSelected) ObsidianDark else TextSecondaryDark,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                      )
                    }
                  }
                }

                Spacer(modifier = Modifier.height(8.dp))

                val filteredQuestions = remember(currentDossier.mockQuestions, questionCategoryFilter) {
                  if (questionCategoryFilter == "ALL") currentDossier.mockQuestions
                  else currentDossier.mockQuestions.filter { it.category.equals(questionCategoryFilter, ignoreCase = true) }
                }

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                  filteredQuestions.forEachIndexed { idx, q ->
                    InterviewCommandQuestionCard(
                      index = idx + 1,
                      question = q,
                      isPracticed = practicedIds.contains(q.id),
                      practiceNote = practiceNotes[q.id] ?: "",
                      onTogglePracticed = { viewModel.toggleInterviewCommandQuestionPracticed(q.id) },
                      onSaveNote = { note -> viewModel.updateInterviewCommandPracticeNote(q.id, note) },
                      onPracticeInSimulator = {
                        viewModel.launchSimulatorForCompany(currentDossier.companyName, currentDossier.jobTitle)
                      }
                    )
                  }
                }
              }

              // =================================================================
              // TAB 2: QUESTIONS FOR INTERVIEWER & CLOSING PLAYBOOK
              // =================================================================
              if (activeTab == 2) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                  Box(
                    modifier = Modifier
                      .fillMaxWidth()
                      .clip(RoundedCornerShape(8.dp))
                      .background(SlateElevated)
                      .border(1.dp, TitanGold.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                      .padding(12.dp)
                  ) {
                    Column {
                      Text(
                        text = "HIGH-IMPACT QUESTIONS TO ASK THE INTERVIEWER",
                        style = MaterialTheme.typography.labelSmall,
                        color = TitanGold,
                        fontWeight = FontWeight.Black,
                        fontSize = 10.sp
                      )
                      Text(
                        text = "Demonstrate senior commercial acumen by asking these high-conviction questions at the end of the round:",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondaryDark,
                        fontSize = 10.5.sp
                      )

                      Spacer(modifier = Modifier.height(8.dp))

                      currentDossier.highImpactQuestionsForInterviewer.forEachIndexed { i, q ->
                        Row(
                          modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                          verticalAlignment = Alignment.Top
                        ) {
                          Text(
                            text = "${i + 1}. ",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = TitanGold,
                            fontSize = 12.sp
                          )
                          Text(
                            text = q,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextPrimaryDark,
                            fontSize = 12.sp,
                            lineHeight = 17.sp
                          )
                        }
                      }
                    }
                  }

                  // Copy Entire Cheat Sheet Button
                  Button(
                    onClick = {
                      val sb = StringBuilder()
                      sb.appendLine("=== INTERVIEW COMMAND DOSSIER: ${currentDossier.companyName} (${currentDossier.jobTitle}) ===")
                      sb.appendLine("Context: ${currentDossier.executiveContext}\n")
                      sb.appendLine("--- STRUCTURED TALKING POINTS ---")
                      currentDossier.strategicTalkingPoints.forEach { pt ->
                        sb.appendLine("[${pt.pillar}] ${pt.title} (${pt.impactMetric})")
                        pt.bulletPoints.forEach { sb.appendLine("  • $it") }
                      }
                      sb.appendLine("\n--- MOCK INTERVIEW QUESTIONS ---")
                      currentDossier.mockQuestions.forEachIndexed { idx, q ->
                        sb.appendLine("${idx + 1}. [${q.category}] ${q.questionText}")
                        sb.appendLine("   Why Asked: ${q.whyCompanyAsksThis}")
                        sb.appendLine("   Talking Points: ${q.structuredTalkingPoints.joinToString("; ")}")
                      }
                      sb.appendLine("\n--- QUESTIONS FOR INTERVIEWER ---")
                      currentDossier.highImpactQuestionsForInterviewer.forEach { sb.appendLine("• $it") }

                      clipboardManager.setText(AnnotatedString(sb.toString()))
                      Toast.makeText(context, "Full Interview Command cheat sheet copied!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                      .fillMaxWidth()
                      .testTag("copy_interview_command_cheat_sheet"),
                    colors = ButtonDefaults.buttonColors(containerColor = SlateElevated, contentColor = TitanGold),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, TitanGold.copy(alpha = 0.5f))
                  ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Copy Entire Interview Command Cheat Sheet", fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}

// -----------------------------------------------------------------------------
// Component: StructuredTalkingPointCard
// -----------------------------------------------------------------------------
@Composable
private fun StructuredTalkingPointCard(
  point: StructuredTalkingPoint,
  onCopy: () -> Unit
) {
  val pillarColor = when (point.pillar) {
    "EXECUTIVE_HOOK" -> TitanGold
    "METRICS_PROOF" -> TitanCyan
    "STRATEGIC_ALIGNMENT" -> TitanIndigo
    "STAR_FRAMEWORK" -> TitanEmerald
    "PITFALLS_AVOID" -> TitanCrimson
    else -> TitanViolet
  }

  Surface(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("talking_point_card_${point.id}"),
    shape = RoundedCornerShape(8.dp),
    color = ObsidianDark,
    border = BorderStroke(1.dp, pillarColor.copy(alpha = 0.4f))
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
              .background(pillarColor.copy(alpha = 0.2f))
              .padding(horizontal = 6.dp, vertical = 2.dp)
          ) {
            Text(
              text = point.pillar.replace("_", " "),
              style = MaterialTheme.typography.labelSmall,
              color = pillarColor,
              fontWeight = FontWeight.Black,
              fontSize = 8.5.sp
            )
          }

          Text(
            text = point.title,
            style = MaterialTheme.typography.titleSmall,
            color = TextPrimaryDark,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        }

        IconButton(
          onClick = onCopy,
          modifier = Modifier.size(24.dp)
        ) {
          Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = TextMutedDark, modifier = Modifier.size(13.dp))
        }
      }

      if (point.impactMetric.isNotBlank()) {
        Spacer(modifier = Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.AutoMirrored.Filled.TrendingUp, contentDescription = null, tint = TitanEmerald, modifier = Modifier.size(12.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = point.impactMetric,
            style = MaterialTheme.typography.labelSmall,
            color = TitanEmerald,
            fontWeight = FontWeight.Bold,
            fontSize = 9.5.sp
          )
        }
      }

      Spacer(modifier = Modifier.height(6.dp))

      // Bullets
      point.bulletPoints.forEach { bullet ->
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
          verticalAlignment = Alignment.Top
        ) {
          Text(
            text = "• ",
            color = pillarColor,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
          )
          Text(
            text = bullet,
            style = MaterialTheme.typography.bodySmall,
            color = TextPrimaryDark,
            fontSize = 11.sp,
            lineHeight = 15.sp
          )
        }
      }

      if (point.strategicRationale.isNotBlank()) {
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = "Rationale: ${point.strategicRationale}",
          style = MaterialTheme.typography.labelSmall,
          color = TextSecondaryDark,
          fontSize = 9.5.sp
        )
      }
    }
  }
}

// -----------------------------------------------------------------------------
// Component: InterviewCommandQuestionCard
// -----------------------------------------------------------------------------
@Composable
private fun InterviewCommandQuestionCard(
  index: Int,
  question: InterviewCommandQuestion,
  isPracticed: Boolean,
  practiceNote: String,
  onTogglePracticed: () -> Unit,
  onSaveNote: (String) -> Unit,
  onPracticeInSimulator: () -> Unit
) {
  var showDetails by remember { mutableStateOf(false) }
  var showModelAnswer by remember { mutableStateOf(false) }
  var showNotesInput by remember { mutableStateOf(practiceNote.isNotBlank()) }
  var noteText by remember { mutableStateOf(practiceNote) }

  val categoryColor = when (question.category) {
    "BEHAVIORAL_STAR" -> TitanGold
    "ANALYTICAL_CASE" -> TitanCyan
    "STRATEGIC_FIT" -> TitanIndigo
    "ROLE_DOMAIN" -> TitanEmerald
    "BAR_RAISER" -> TitanCrimson
    else -> TitanViolet
  }

  Surface(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("question_card_${question.id}"),
    shape = RoundedCornerShape(10.dp),
    color = ObsidianDark,
    border = BorderStroke(1.dp, if (isPracticed) TitanEmerald.copy(alpha = 0.6f) else SlateBorder)
  ) {
    Column(modifier = Modifier.padding(12.dp)) {
      // Header: Index, Category, Difficulty, Practiced toggle
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Text(
            text = "Q$index",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Black,
            color = categoryColor,
            fontSize = 11.sp
          )

          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(4.dp))
              .background(categoryColor.copy(alpha = 0.15f))
              .padding(horizontal = 6.dp, vertical = 2.dp)
          ) {
            Text(
              text = question.category.replace("_", " "),
              style = MaterialTheme.typography.labelSmall,
              color = categoryColor,
              fontWeight = FontWeight.Bold,
              fontSize = 8.5.sp
            )
          }

          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(4.dp))
              .background(SlateElevated)
              .padding(horizontal = 5.dp, vertical = 2.dp)
          ) {
            Text(
              text = question.difficulty,
              style = MaterialTheme.typography.labelSmall,
              color = TextSecondaryDark,
              fontSize = 8.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }

        // Practiced Status Pill
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(if (isPracticed) TitanEmerald.copy(alpha = 0.2f) else SlateElevated)
            .border(1.dp, if (isPracticed) TitanEmerald else SlateBorder, RoundedCornerShape(6.dp))
            .clickable { onTogglePracticed() }
            .padding(horizontal = 8.dp, vertical = 3.dp)
            .testTag("toggle_practiced_question_${question.id}")
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = if (isPracticed) Icons.Default.CheckCircle else Icons.Default.Check,
              contentDescription = null,
              tint = if (isPracticed) TitanEmerald else TextMutedDark,
              modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = if (isPracticed) "Practiced" else "Mark Practiced",
              style = MaterialTheme.typography.labelSmall,
              color = if (isPracticed) TitanEmerald else TextSecondaryDark,
              fontWeight = FontWeight.Bold,
              fontSize = 9.5.sp
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // The Actual Question Text
      Text(
        text = question.questionText,
        style = MaterialTheme.typography.bodyMedium,
        color = TextPrimaryDark,
        fontWeight = FontWeight.SemiBold,
        fontSize = 13.sp,
        lineHeight = 18.sp
      )

      // Why Asked & Profile Anchor
      if (question.whyCompanyAsksThis.isNotBlank()) {
        Spacer(modifier = Modifier.height(6.dp))
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(SlateElevated)
            .padding(8.dp)
        ) {
          Column {
            Text(
              text = "WHY COMPANY ASKS THIS:",
              style = MaterialTheme.typography.labelSmall,
              color = TitanGold,
              fontWeight = FontWeight.Bold,
              fontSize = 8.5.sp
            )
            Text(
              text = question.whyCompanyAsksThis,
              style = MaterialTheme.typography.bodySmall,
              color = TextSecondaryDark,
              fontSize = 10.5.sp
            )
            if (question.candidateProfileAnchor.isNotBlank()) {
              Spacer(modifier = Modifier.height(3.dp))
              Text(
                text = "Candidate Anchor: ${question.candidateProfileAnchor}",
                style = MaterialTheme.typography.bodySmall,
                color = TitanCyan,
                fontSize = 10.sp
              )
            }
          }
        }
      }

      // Structured Talking Points to Hit
      if (question.structuredTalkingPoints.isNotEmpty()) {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
          text = "TALKING POINTS TO HIT:",
          style = MaterialTheme.typography.labelSmall,
          color = TitanCyan,
          fontWeight = FontWeight.Bold,
          fontSize = 9.sp
        )
        Spacer(modifier = Modifier.height(3.dp))
        question.structuredTalkingPoints.forEach { pt ->
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 1.dp)
          ) {
            Text("• ", color = TitanCyan, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            Text(pt, style = MaterialTheme.typography.bodySmall, color = TextPrimaryDark, fontSize = 11.sp)
          }
        }
      }

      // Expandable STAR breakdown & Model Answer
      Spacer(modifier = Modifier.height(8.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          if (question.sampleStarAnswer.isNotBlank()) {
            OutlinedButton(
              onClick = { showModelAnswer = !showModelAnswer },
              shape = RoundedCornerShape(6.dp),
              contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
              modifier = Modifier.height(28.dp)
            ) {
              Text(
                text = if (showModelAnswer) "Hide STAR Answer" else "Model STAR Answer",
                fontSize = 10.sp,
                color = TitanGold
              )
            }
          }

          OutlinedButton(
            onClick = { showNotesInput = !showNotesInput },
            shape = RoundedCornerShape(6.dp),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
            modifier = Modifier.height(28.dp)
          ) {
            Text(
              text = if (showNotesInput) "Notes" else "+ Add Notes",
              fontSize = 10.sp,
              color = TitanCyan
            )
          }
        }

        IconButton(
          onClick = onPracticeInSimulator,
          modifier = Modifier.size(28.dp)
        ) {
          Icon(Icons.Default.Mic, contentDescription = "Practice in Simulator", tint = TitanEmerald, modifier = Modifier.size(16.dp))
        }
      }

      // Model STAR Answer Container
      AnimatedVisibility(visible = showModelAnswer) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(SlateDarker)
            .border(1.dp, TitanGold.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
            .padding(10.dp)
        ) {
          Text(
            text = "EXECUTIVE MODEL STAR ANSWER",
            style = MaterialTheme.typography.labelSmall,
            color = TitanGold,
            fontWeight = FontWeight.Bold,
            fontSize = 9.sp
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = question.sampleStarAnswer,
            style = MaterialTheme.typography.bodySmall,
            color = TextPrimaryDark,
            fontSize = 11.5.sp,
            lineHeight = 16.sp
          )
        }
      }

      // Notes Input
      AnimatedVisibility(visible = showNotesInput) {
        Column(modifier = Modifier.padding(top = 8.dp)) {
          OutlinedTextField(
            value = noteText,
            onValueChange = {
              noteText = it
              onSaveNote(it)
            },
            placeholder = { Text("Write your practice answer bullets or notes...", fontSize = 11.sp) },
            modifier = Modifier
              .fillMaxWidth()
              .testTag("practice_note_input_${question.id}"),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = TitanCyan,
              unfocusedBorderColor = SlateBorder,
              focusedTextColor = TextPrimaryDark,
              unfocusedTextColor = TextPrimaryDark
            ),
            maxLines = 4
          )
        }
      }
    }
  }
}
