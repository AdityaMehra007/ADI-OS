package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
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
import com.example.data.model.CompanyInterviewSimulationDossier
import com.example.data.model.GeneratedInterviewQuestion
import com.example.data.model.InterviewAnswerEvaluation
import com.example.ui.theme.ObsidianDark
import com.example.ui.theme.SlateDarker
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateCard
import com.example.ui.theme.SlateElevated
import com.example.ui.theme.TextMutedDark
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import com.example.ui.theme.TitanCyan
import com.example.ui.theme.TitanCyanGlow
import com.example.ui.theme.TitanEmerald
import com.example.ui.theme.TitanGold
import com.example.ui.theme.TitanIndigo
import com.example.ui.theme.TitanRose
import com.example.ui.theme.TitanViolet
import com.example.ui.viewmodel.TitanViewModel

@Composable
fun InterviewSimulatorView(
  viewModel: TitanViewModel,
  modifier: Modifier = Modifier
) {
  val selectedCompany by viewModel.selectedSimulatorCompanyName.collectAsState()
  val selectedRole by viewModel.selectedSimulatorRoleTitle.collectAsState()
  val selectedRound by viewModel.selectedSimulatorRoundType.collectAsState()
  val dossier by viewModel.companySimulationDossier.collectAsState()
  val isGenerating by viewModel.isGeneratingSimulationQuestions.collectAsState()
  val generationError by viewModel.simulationGenerationError.collectAsState()
  val activeQuestion by viewModel.activeSimulationQuestion.collectAsState()
  val candidateAnswer by viewModel.candidateSimulationAnswer.collectAsState()
  val isEvaluating by viewModel.isEvaluatingSimulationAnswer.collectAsState()
  val answerEvaluation by viewModel.simulationAnswerEvaluation.collectAsState()
  val categoryFilter by viewModel.simulatorCategoryFilter.collectAsState()
  val isAudioRecording by viewModel.isSimulatingAudioRecording.collectAsState()
  val targetCompaniesWithIntel by viewModel.targetCompaniesWithIntel.collectAsState()
  val userProfile by viewModel.userProfile.collectAsState()

  // Auto-generate simulation questions on initial launch if empty
  LaunchedEffect(Unit) {
    if (dossier == null && !isGenerating) {
      viewModel.generateSimulationQuestions(forceRefresh = false)
    }
  }

  val filteredQuestions = remember(dossier, categoryFilter) {
    val allQuestions = dossier?.questions ?: emptyList()
    if (categoryFilter == "ALL") allQuestions
    else allQuestions.filter { it.category.equals(categoryFilter, ignoreCase = true) }
  }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .testTag("interview_simulator_view"),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // 1. EXECUTIVE HEADER BANNER
    item {
      SimulatorHeaderBanner(
        selectedCompany = selectedCompany,
        selectedRole = selectedRole,
        isGenerating = isGenerating,
        onRefresh = { viewModel.generateSimulationQuestions(forceRefresh = true) }
      )
    }

    // 2. TARGET COMPANY SELECTOR CAROUSEL
    item {
      TargetCompanySelectorSection(
        selectedCompany = selectedCompany,
        targetCompanies = targetCompaniesWithIntel.map { it.company.name }.ifEmpty {
          listOf("Microsoft", "Google", "Razorpay", "Zepto", "Swiggy", "Atlassian", "Bain & Company")
        },
        onSelectCompany = { company ->
          viewModel.selectSimulatorCompany(company, autoGenerate = true)
        }
      )
    }

    // 3. INTERVIEW ROUND SELECTOR
    item {
      RoundTypeSelectorSection(
        selectedRound = selectedRound,
        onSelectRound = { round ->
          viewModel.setSimulatorRoundType(round, autoGenerate = true)
        }
      )
    }

    // 4. CANDIDATE CAREER PROFILE GROUNDING CARD
    item {
      ProfileGroundingSummaryCard(
        candidateName = userProfile?.name ?: "Adi",
        degree = "${userProfile?.educationDegree ?: "BBA"} (${userProfile?.educationSpecialization ?: "International Business"})",
        university = userProfile?.university ?: "Dayananda Sagar University (DSU), Bengaluru",
        targetCompany = selectedCompany
      )
    }

    // 5. GENERATE TRIGGER & STATUS
    item {
      Button(
        onClick = { viewModel.generateSimulationQuestions(forceRefresh = true) },
        enabled = !isGenerating,
        modifier = Modifier
          .fillMaxWidth()
          .height(48.dp)
          .testTag("btn_generate_simulation_questions"),
        colors = ButtonDefaults.buttonColors(
          containerColor = TitanCyan,
          disabledContainerColor = TitanCyan.copy(alpha = 0.4f)
        ),
        shape = RoundedCornerShape(10.dp)
      ) {
        if (isGenerating) {
          CircularProgressIndicator(
            modifier = Modifier.size(18.dp),
            strokeWidth = 2.dp,
            color = ObsidianDark
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            "Synthesizing $selectedCompany Interview Dossier via Gemini...",
            color = ObsidianDark,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
          )
        } else {
          Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = ObsidianDark, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            "Generate Potential Interview Questions for $selectedCompany",
            color = ObsidianDark,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
          )
        }
      }

      if (generationError != null) {
        Spacer(modifier = Modifier.height(6.dp))
        Text(
          text = generationError ?: "",
          color = TitanRose,
          fontSize = 11.sp,
          modifier = Modifier.padding(horizontal = 4.dp)
        )
      }
    }

    // 6. QUESTION CATEGORY FILTERS
    if (dossier != null && dossier!!.questions.isNotEmpty()) {
      item {
        CategoryFilterRow(
          selectedFilter = categoryFilter,
          totalCount = dossier!!.questions.size,
          onSelectFilter = { viewModel.setSimulatorCategoryFilter(it) }
        )
      }

      // 7. GENERATED QUESTIONS LIST
      items(filteredQuestions) { question ->
        val isSelected = activeQuestion?.id == question.id
        GeneratedQuestionCard(
          question = question,
          companyName = selectedCompany,
          isSelected = isSelected,
          onSelectForSimulation = {
            viewModel.selectSimulationQuestion(question)
          }
        )
      }
    }

    // 8. ACTIVE SIMULATION & PRACTICE ARENA
    item {
      ActiveSimulationArenaCard(
        activeQuestion = activeQuestion,
        selectedCompany = selectedCompany,
        selectedRole = selectedRole,
        candidateAnswer = candidateAnswer,
        onAnswerChange = { viewModel.updateCandidateSimulationAnswer(it) },
        isEvaluating = isEvaluating,
        evaluation = answerEvaluation,
        isAudioRecording = isAudioRecording,
        onToggleAudioRecording = { viewModel.toggleAudioRecording() },
        onInjectStarEvidence = { viewModel.injectStarEvidenceIntoSimulation(it) },
        onEvaluateAnswer = { viewModel.evaluateActiveSimulationAnswer() }
      )
    }

    item {
      Spacer(modifier = Modifier.height(32.dp))
    }
  }
}

@Composable
private fun SimulatorHeaderBanner(
  selectedCompany: String,
  selectedRole: String,
  isGenerating: Boolean,
  onRefresh: () -> Unit
) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = SlateCard),
    border = BorderStroke(1.dp, SlateBorder)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(24.dp)
              .clip(CircleShape)
              .background(TitanCyanGlow),
            contentAlignment = Alignment.Center
          ) {
            Icon(Icons.Default.Psychology, contentDescription = null, tint = TitanCyan, modifier = Modifier.size(14.dp))
          }
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "AI INTERVIEW SIMULATOR",
            style = MaterialTheme.typography.labelSmall,
            color = TitanCyan,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp,
            letterSpacing = 1.sp
          )
          Spacer(modifier = Modifier.width(6.dp))
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(4.dp))
              .background(TitanEmerald.copy(alpha = 0.2f))
              .padding(horizontal = 5.dp, vertical = 2.dp)
          ) {
            Text("GEMINI 3.5 FLASH", color = TitanEmerald, fontSize = 8.sp, fontWeight = FontWeight.Bold)
          }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = "$selectedCompany · $selectedRole",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
          color = TextPrimaryDark
        )
        Text(
          text = "Conditioned on company hiring bars & verified candidate portfolio",
          style = MaterialTheme.typography.bodySmall,
          color = TextSecondaryDark,
          fontSize = 11.sp
        )
      }

      IconButton(
        onClick = onRefresh,
        enabled = !isGenerating,
        modifier = Modifier
          .clip(RoundedCornerShape(8.dp))
          .background(SlateElevated)
          .border(1.dp, SlateBorder, RoundedCornerShape(8.dp))
      ) {
        Icon(
          Icons.Default.Refresh,
          contentDescription = "Regenerate",
          tint = if (isGenerating) TextMutedDark else TitanCyan,
          modifier = Modifier.size(18.dp)
        )
      }
    }
  }
}

@Composable
private fun TargetCompanySelectorSection(
  selectedCompany: String,
  targetCompanies: List<String>,
  onSelectCompany: (String) -> Unit
) {
  Column {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.Business, contentDescription = null, tint = TitanCyan, modifier = Modifier.size(12.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(
          text = "SELECT TARGET COMPANY:",
          style = MaterialTheme.typography.labelSmall,
          color = TextMutedDark,
          fontWeight = FontWeight.Bold,
          fontSize = 9.sp
        )
      }
      Text(
        text = "${targetCompanies.size} Pipeline Companies",
        style = MaterialTheme.typography.labelSmall,
        color = TitanCyan,
        fontSize = 9.sp
      )
    }

    Spacer(modifier = Modifier.height(6.dp))

    Row(
      modifier = Modifier
        .fillMaxWidth()
        .horizontalScroll(rememberScrollState()),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      targetCompanies.distinct().forEach { company ->
        val isSelected = company.equals(selectedCompany, ignoreCase = true)
        val emoji = when {
          company.contains("Microsoft", ignoreCase = true) -> "🪟"
          company.contains("Google", ignoreCase = true) -> "🔍"
          company.contains("Razorpay", ignoreCase = true) -> "💳"
          company.contains("Zepto", ignoreCase = true) -> "⚡"
          company.contains("Swiggy", ignoreCase = true) -> "🛵"
          company.contains("Atlassian", ignoreCase = true) -> "🔷"
          company.contains("Bain", ignoreCase = true) -> "📈"
          else -> "🏢"
        }

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) TitanCyan.copy(alpha = 0.15f) else SlateCard)
            .border(
              1.dp,
              if (isSelected) TitanCyan else SlateBorder,
              RoundedCornerShape(8.dp)
            )
            .clickable { onSelectCompany(company) }
            .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(emoji, fontSize = 12.sp)
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = company,
              color = if (isSelected) TitanCyan else TextPrimaryDark,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
              fontSize = 11.sp
            )
          }
        }
      }
    }
  }
}

@Composable
private fun RoundTypeSelectorSection(
  selectedRound: String,
  onSelectRound: (String) -> Unit
) {
  val rounds = listOf(
    "ALL_ROUNDS" to "🎯 All Rounds",
    "RECRUITER_SCREEN" to "👔 Recruiter Screen",
    "HIRING_MANAGER" to "💼 Hiring Manager",
    "ANALYTICS_CASE" to "📊 Case & Analytics",
    "VP_EXECUTIVE" to "🚀 VP Bar Raiser"
  )

  Column {
    Text(
      text = "INTERVIEW ROUND FOCUS:",
      style = MaterialTheme.typography.labelSmall,
      color = TextMutedDark,
      fontWeight = FontWeight.Bold,
      fontSize = 9.sp
    )

    Spacer(modifier = Modifier.height(6.dp))

    Row(
      modifier = Modifier
        .fillMaxWidth()
        .horizontalScroll(rememberScrollState()),
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      rounds.forEach { (key, label) ->
        val isSelected = selectedRound == key
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(if (isSelected) TitanGold.copy(alpha = 0.2f) else SlateElevated)
            .border(
              1.dp,
              if (isSelected) TitanGold else SlateBorder,
              RoundedCornerShape(6.dp)
            )
            .clickable { onSelectRound(key) }
            .padding(horizontal = 8.dp, vertical = 5.dp)
        ) {
          Text(
            text = label,
            color = if (isSelected) TitanGold else TextSecondaryDark,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            fontSize = 10.sp
          )
        }
      }
    }
  }
}

@Composable
private fun ProfileGroundingSummaryCard(
  candidateName: String,
  degree: String,
  university: String,
  targetCompany: String
) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(10.dp),
    colors = CardDefaults.cardColors(containerColor = SlateCard),
    border = BorderStroke(1.dp, TitanIndigo.copy(alpha = 0.4f))
  ) {
    Column(modifier = Modifier.padding(12.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.School, contentDescription = null, tint = TitanIndigo, modifier = Modifier.size(13.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "CAREER PROFILE GROUNDING",
            style = MaterialTheme.typography.labelSmall,
            color = TitanIndigo,
            fontWeight = FontWeight.Bold,
            fontSize = 9.sp
          )
        }
        Text(
          text = "Candidate: $candidateName",
          style = MaterialTheme.typography.labelSmall,
          color = TextSecondaryDark,
          fontSize = 9.sp
        )
      }

      Spacer(modifier = Modifier.height(6.dp))

      Text(
        text = "Gemini uses your verified portfolio & $targetCompany's technical rubric to generate these custom vectors:",
        style = MaterialTheme.typography.bodySmall,
        color = TextPrimaryDark,
        fontSize = 11.sp
      )

      Spacer(modifier = Modifier.height(8.dp))

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        GroundingChip(icon = "🎓", label = degree)
        GroundingChip(icon = "⚡", label = "42% Cycle Reduction (Family Biz)")
        GroundingChip(icon = "💰", label = "₹24L Working Capital Saved")
        GroundingChip(icon = "💳", label = "Razorpay Finalist (₹18.5L Offer)")
        GroundingChip(icon = "🛠️", label = "SQL & Agentic AI Workflows")
      }
    }
  }
}

@Composable
private fun GroundingChip(icon: String, label: String) {
  Box(
    modifier = Modifier
      .clip(RoundedCornerShape(6.dp))
      .background(SlateElevated)
      .border(1.dp, SlateBorder, RoundedCornerShape(6.dp))
      .padding(horizontal = 7.dp, vertical = 4.dp)
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Text(icon, fontSize = 10.sp)
      Spacer(modifier = Modifier.width(4.dp))
      Text(label, color = TextPrimaryDark, fontSize = 9.sp, fontWeight = FontWeight.Medium)
    }
  }
}

@Composable
private fun CategoryFilterRow(
  selectedFilter: String,
  totalCount: Int,
  onSelectFilter: (String) -> Unit
) {
  val filters = listOf(
    "ALL" to "All ($totalCount)",
    "BEHAVIORAL" to "Behavioral",
    "ANALYTICAL_CASE" to "Case & Analytics",
    "STRATEGY_FIT" to "Strategy & Fit",
    "ROLE_SPECIFIC" to "Role Specific",
    "CURVEBALL_PRESSURE" to "Curveball"
  )

  Row(
    modifier = Modifier
      .fillMaxWidth()
      .horizontalScroll(rememberScrollState()),
    horizontalArrangement = Arrangement.spacedBy(6.dp)
  ) {
    filters.forEach { (key, label) ->
      val isSelected = selectedFilter == key
      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(6.dp))
          .background(if (isSelected) TitanCyan else SlateElevated)
          .border(
            1.dp,
            if (isSelected) TitanCyan else SlateBorder,
            RoundedCornerShape(6.dp)
          )
          .clickable { onSelectFilter(key) }
          .padding(horizontal = 8.dp, vertical = 4.dp)
      ) {
        Text(
          text = label,
          color = if (isSelected) ObsidianDark else TextSecondaryDark,
          fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
          fontSize = 10.sp
        )
      }
    }
  }
}

@Composable
private fun GeneratedQuestionCard(
  question: GeneratedInterviewQuestion,
  companyName: String,
  isSelected: Boolean,
  onSelectForSimulation: () -> Unit
) {
  var isExpanded by remember { mutableStateOf(false) }
  val clipboard = LocalClipboardManager.current
  val context = LocalContext.current

  val categoryColor = when (question.category.uppercase()) {
    "ANALYTICAL_CASE" -> TitanEmerald
    "STRATEGY_FIT" -> TitanGold
    "CURVEBALL_PRESSURE" -> TitanRose
    "ROLE_SPECIFIC" -> TitanViolet
    else -> TitanCyan
  }

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .border(
        width = if (isSelected) 1.5.dp else 1.dp,
        color = if (isSelected) TitanCyan else SlateBorder,
        shape = RoundedCornerShape(12.dp)
      ),
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = if (isSelected) SlateElevated else SlateCard)
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      // Badges Row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(4.dp))
              .background(categoryColor.copy(alpha = 0.15f))
              .border(1.dp, categoryColor.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
              .padding(horizontal = 6.dp, vertical = 2.dp)
          ) {
            Text(
              text = question.category.replace("_", " "),
              color = categoryColor,
              fontSize = 9.sp,
              fontWeight = FontWeight.Bold
            )
          }

          Spacer(modifier = Modifier.width(6.dp))

          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(4.dp))
              .background(SlateElevated)
              .padding(horizontal = 6.dp, vertical = 2.dp)
          ) {
            Text(
              text = question.difficulty,
              color = TextMutedDark,
              fontSize = 8.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }

        if (question.isPracticed && question.candidateLastScore != null) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(4.dp))
              .background(TitanEmerald.copy(alpha = 0.2f))
              .padding(horizontal = 6.dp, vertical = 2.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.CheckCircle, contentDescription = null, tint = TitanEmerald, modifier = Modifier.size(10.dp))
              Spacer(modifier = Modifier.width(3.dp))
              Text("Practiced: ${question.candidateLastScore}/100", color = TitanEmerald, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Question Text
      Text(
        text = question.questionText,
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = FontWeight.SemiBold,
        color = TextPrimaryDark,
        fontSize = 13.sp,
        lineHeight = 18.sp
      )

      Spacer(modifier = Modifier.height(8.dp))

      // Why Company Asks This (Interviewer's Secret Rubric)
      if (question.whyCompanyAsksThis.isNotBlank()) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(SlateElevated.copy(alpha = 0.6f))
            .padding(8.dp)
        ) {
          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Lightbulb, contentDescription = null, tint = TitanGold, modifier = Modifier.size(12.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "WHY $companyName ASKS THIS:",
                style = MaterialTheme.typography.labelSmall,
                color = TitanGold,
                fontWeight = FontWeight.Bold,
                fontSize = 8.sp
              )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = question.whyCompanyAsksThis,
              style = MaterialTheme.typography.bodySmall,
              color = TextSecondaryDark,
              fontSize = 10.sp,
              lineHeight = 14.sp
            )
          }
        }
      }

      // Profile Grounding Anchor
      if (question.candidateProfileAnchor.isNotBlank()) {
        Spacer(modifier = Modifier.height(6.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Bolt, contentDescription = null, tint = TitanCyan, modifier = Modifier.size(11.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "Tests Your Experience: ",
            style = MaterialTheme.typography.labelSmall,
            color = TitanCyan,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = question.candidateProfileAnchor,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondaryDark,
            fontSize = 9.sp,
            maxLines = 2
          )
        }
      }

      // Expandable Model Answer & Key Points
      AnimatedVisibility(
        visible = isExpanded,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
      ) {
        Column(modifier = Modifier.padding(top = 10.dp)) {
          if (question.keyPointsToHit.isNotEmpty()) {
            Text(
              text = "KEY POINTS TO ARTICULATE:",
              style = MaterialTheme.typography.labelSmall,
              color = TitanCyan,
              fontWeight = FontWeight.Bold,
              fontSize = 8.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            question.keyPointsToHit.forEach { point ->
              Row(modifier = Modifier.padding(vertical = 1.dp)) {
                Text("• ", color = TitanCyan, fontSize = 10.sp)
                Text(point, color = TextPrimaryDark, fontSize = 10.sp, lineHeight = 14.sp)
              }
            }
            Spacer(modifier = Modifier.height(8.dp))
          }

          if (question.sampleStarAnswer.isNotBlank()) {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(6.dp))
                .background(TitanIndigo.copy(alpha = 0.1f))
                .border(1.dp, TitanIndigo.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                .padding(10.dp)
            ) {
              Column {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(
                    text = "SAMPLE HIGH-IMPACT STAR ANSWER:",
                    style = MaterialTheme.typography.labelSmall,
                    color = TitanIndigo,
                    fontWeight = FontWeight.Bold,
                    fontSize = 8.sp
                  )
                  IconButton(
                    onClick = { clipboard.setText(AnnotatedString(question.sampleStarAnswer)) },
                    modifier = Modifier.size(18.dp)
                  ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = TitanIndigo, modifier = Modifier.size(12.dp))
                  }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = question.sampleStarAnswer,
                  style = MaterialTheme.typography.bodySmall,
                  color = TextPrimaryDark,
                  fontSize = 10.sp,
                  lineHeight = 14.sp
                )
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Bottom Actions Row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        OutlinedButton(
          onClick = { isExpanded = !isExpanded },
          modifier = Modifier.height(32.dp),
          shape = RoundedCornerShape(6.dp),
          colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondaryDark),
          border = BorderStroke(1.dp, SlateBorder)
        ) {
          Text(if (isExpanded) "Hide Model Answer" else "Model Answer", fontSize = 10.sp)
          Spacer(modifier = Modifier.width(4.dp))
          Icon(if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore, contentDescription = null, modifier = Modifier.size(12.dp))
        }

        Button(
          onClick = onSelectForSimulation,
          modifier = Modifier.height(32.dp),
          shape = RoundedCornerShape(6.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) TitanCyan else SlateElevated,
            contentColor = if (isSelected) ObsidianDark else TitanCyan
          ),
          border = BorderStroke(1.dp, TitanCyan.copy(alpha = 0.5f))
        ) {
          Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(14.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text(if (isSelected) "Active in Simulator" else "Simulate Question", fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}

@Composable
private fun ActiveSimulationArenaCard(
  activeQuestion: GeneratedInterviewQuestion?,
  selectedCompany: String,
  selectedRole: String,
  candidateAnswer: String,
  onAnswerChange: (String) -> Unit,
  isEvaluating: Boolean,
  evaluation: InterviewAnswerEvaluation?,
  isAudioRecording: Boolean,
  onToggleAudioRecording: () -> Unit,
  onInjectStarEvidence: (String) -> Unit,
  onEvaluateAnswer: () -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .border(1.dp, TitanCyan.copy(alpha = 0.6f), RoundedCornerShape(12.dp)),
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = SlateCard)
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      // Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(20.dp)
              .clip(CircleShape)
              .background(TitanCyanGlow),
            contentAlignment = Alignment.Center
          ) {
            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = TitanCyan, modifier = Modifier.size(12.dp))
          }
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "SIMULATION PRACTICE ARENA",
            style = MaterialTheme.typography.labelSmall,
            color = TitanCyan,
            fontWeight = FontWeight.Bold,
            fontSize = 9.sp,
            letterSpacing = 1.sp
          )
        }

        Text(
          text = "$selectedCompany · $selectedRole",
          style = MaterialTheme.typography.labelSmall,
          color = TitanGold,
          fontSize = 9.sp,
          fontWeight = FontWeight.Bold
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Active Question Display
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
            text = "ACTIVE INTERVIEW PROMPT:",
            style = MaterialTheme.typography.labelSmall,
            color = TextMutedDark,
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = activeQuestion?.questionText ?: "Select a generated question above to rehearse and simulate.",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = TextPrimaryDark,
            fontSize = 12.sp
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Audio Waveform Toggle
      AudioWaveformVisualizer(
        isRecording = isAudioRecording,
        onToggleRecording = onToggleAudioRecording
      )

      Spacer(modifier = Modifier.height(10.dp))

      // 1-Tap STAR Injections
      Column {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "INJECT VERIFIED STAR EVIDENCE:",
            style = MaterialTheme.typography.labelSmall,
            color = TextMutedDark,
            fontWeight = FontWeight.Bold,
            fontSize = 8.sp
          )
          Text("Tap to paste", color = TitanCyan, fontSize = 8.sp)
        }

        Spacer(modifier = Modifier.height(4.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          StarInjectionPill(
            title = "Family Biz (42%)",
            color = TitanEmerald,
            onClick = { onInjectStarEvidence("FAMILY_BIZ") },
            modifier = Modifier.weight(1f)
          )
          StarInjectionPill(
            title = "Razorpay Case",
            color = TitanGold,
            onClick = { onInjectStarEvidence("RAZORPAY_CASE") },
            modifier = Modifier.weight(1f)
          )
          StarInjectionPill(
            title = "SQL Pipeline",
            color = TitanIndigo,
            onClick = { onInjectStarEvidence("SQL_RECONCILIATION") },
            modifier = Modifier.weight(1f)
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Answer Input Box
      OutlinedTextField(
        value = candidateAnswer,
        onValueChange = onAnswerChange,
        modifier = Modifier
          .fillMaxWidth()
          .height(140.dp)
          .testTag("input_simulation_answer"),
        placeholder = {
          Text(
            "Structure your response with STAR:\n• Situation: What was the context?\n• Task: What was your specific responsibility?\n• Action: What did you build/execute?\n• Result: Quantified impact (e.g. 42% cycle reduction)",
            color = TextMutedDark,
            fontSize = 11.sp,
            lineHeight = 15.sp
          )
        },
        colors = OutlinedTextFieldDefaults.colors(
          focusedContainerColor = SlateDarker,
          unfocusedContainerColor = SlateDarker,
          focusedBorderColor = TitanCyan,
          unfocusedBorderColor = SlateBorder,
          focusedTextColor = TextPrimaryDark,
          unfocusedTextColor = TextPrimaryDark
        ),
        shape = RoundedCornerShape(8.dp)
      )

      Spacer(modifier = Modifier.height(10.dp))

      // Evaluate Button
      Button(
        onClick = onEvaluateAnswer,
        enabled = !isEvaluating && candidateAnswer.isNotBlank(),
        modifier = Modifier
          .fillMaxWidth()
          .height(44.dp)
          .testTag("btn_evaluate_simulation_answer"),
        colors = ButtonDefaults.buttonColors(
          containerColor = TitanEmerald,
          disabledContainerColor = TitanEmerald.copy(alpha = 0.3f)
        ),
        shape = RoundedCornerShape(8.dp)
      ) {
        if (isEvaluating) {
          CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = ObsidianDark)
          Spacer(modifier = Modifier.width(6.dp))
          Text("Gemini Evaluating STAR Rubric...", color = ObsidianDark, fontWeight = FontWeight.Bold, fontSize = 11.sp)
        } else {
          Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = ObsidianDark, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Evaluate Answer with Gemini (Bar Raiser Rubric)", color = ObsidianDark, fontWeight = FontWeight.Bold, fontSize = 11.sp)
        }
      }

      // Evaluation Result Card
      if (evaluation != null) {
        Spacer(modifier = Modifier.height(12.dp))
        EvaluationResultDisplayCard(evaluation = evaluation, companyName = selectedCompany)
      }
    }
  }
}

@Composable
private fun StarInjectionPill(
  title: String,
  color: Color,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(6.dp))
      .background(SlateElevated)
      .border(1.dp, color.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
      .clickable(onClick = onClick)
      .padding(vertical = 5.dp, horizontal = 4.dp),
    contentAlignment = Alignment.Center
  ) {
    Text(title, color = color, fontSize = 9.sp, fontWeight = FontWeight.Bold, maxLines = 1)
  }
}

@Composable
private fun EvaluationResultDisplayCard(
  evaluation: InterviewAnswerEvaluation,
  companyName: String
) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(10.dp),
    colors = CardDefaults.cardColors(containerColor = SlateDarker),
    border = BorderStroke(1.dp, TitanEmerald.copy(alpha = 0.5f))
  ) {
    Column(modifier = Modifier.padding(12.dp)) {
      // Score Row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(36.dp)
              .clip(CircleShape)
              .background(TitanEmerald.copy(alpha = 0.2f))
              .border(1.5.dp, TitanEmerald, CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Text("${evaluation.score}", color = TitanEmerald, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
          }
          Spacer(modifier = Modifier.width(8.dp))
          Column {
            Text(
              text = if (evaluation.score >= 90) "STRONG HIRE" else if (evaluation.score >= 80) "LEAN HIRE" else "NEEDS POLISH",
              color = if (evaluation.score >= 90) TitanEmerald else if (evaluation.score >= 80) TitanGold else TitanRose,
              fontWeight = FontWeight.Bold,
              fontSize = 11.sp
            )
            Text(
              text = "$companyName Bar Raiser Assessment",
              color = TextSecondaryDark,
              fontSize = 9.sp
            )
          }
        }

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(TitanEmerald.copy(alpha = 0.15f))
            .padding(horizontal = 6.dp, vertical = 3.dp)
        ) {
          Text("SCORE: ${evaluation.score}/100", color = TitanEmerald, fontSize = 9.sp, fontWeight = FontWeight.Bold)
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // STAR Clarity Checklist
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(6.dp))
          .background(SlateElevated)
          .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        StarMetricRow("SITUATION", evaluation.situationClarity)
        StarMetricRow("TASK", evaluation.taskClarity)
        StarMetricRow("ACTION", evaluation.actionImpact)
        StarMetricRow("RESULT", evaluation.resultMetrics)
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Strengths
      if (evaluation.keyStrengths.isNotEmpty()) {
        Text("KEY STRENGTHS:", style = MaterialTheme.typography.labelSmall, color = TitanEmerald, fontWeight = FontWeight.Bold, fontSize = 8.sp)
        Spacer(modifier = Modifier.height(2.dp))
        evaluation.keyStrengths.forEach { s ->
          Row(modifier = Modifier.padding(vertical = 1.dp)) {
            Icon(Icons.Default.Check, contentDescription = null, tint = TitanEmerald, modifier = Modifier.size(10.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(s, color = TextPrimaryDark, fontSize = 9.sp, lineHeight = 13.sp)
          }
        }
      }

      // Areas for Improvement
      if (evaluation.areasForImprovement.isNotEmpty()) {
        Spacer(modifier = Modifier.height(6.dp))
        Text("POLISH RECOMMENDATIONS:", style = MaterialTheme.typography.labelSmall, color = TitanGold, fontWeight = FontWeight.Bold, fontSize = 8.sp)
        Spacer(modifier = Modifier.height(2.dp))
        evaluation.areasForImprovement.forEach { a ->
          Row(modifier = Modifier.padding(vertical = 1.dp)) {
            Icon(Icons.Default.Lightbulb, contentDescription = null, tint = TitanGold, modifier = Modifier.size(10.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(a, color = TextSecondaryDark, fontSize = 9.sp, lineHeight = 13.sp)
          }
        }
      }

      // Polished Executive Rephrase
      if (evaluation.polishedExecutiveRephrase.isNotBlank()) {
        Spacer(modifier = Modifier.height(8.dp))
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(TitanCyan.copy(alpha = 0.1f))
            .border(1.dp, TitanCyan.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
            .padding(8.dp)
        ) {
          Column {
            Text("POLISHED EXECUTIVE REPHRASE:", style = MaterialTheme.typography.labelSmall, color = TitanCyan, fontWeight = FontWeight.Bold, fontSize = 8.sp)
            Spacer(modifier = Modifier.height(3.dp))
            Text(
              text = "\"${evaluation.polishedExecutiveRephrase}\"",
              style = MaterialTheme.typography.bodySmall,
              color = TextPrimaryDark,
              fontSize = 10.sp,
              lineHeight = 14.sp
            )
          }
        }
      }
    }
  }
}

@Composable
private fun StarMetricRow(label: String, detail: String) {
  Row(verticalAlignment = Alignment.Top) {
    Text(
      text = "$label: ",
      color = TitanCyan,
      fontSize = 8.sp,
      fontWeight = FontWeight.Bold,
      modifier = Modifier.width(60.dp)
    )
    Text(
      text = detail,
      color = TextPrimaryDark,
      fontSize = 8.sp,
      lineHeight = 12.sp,
      modifier = Modifier.weight(1f)
    )
  }
}
