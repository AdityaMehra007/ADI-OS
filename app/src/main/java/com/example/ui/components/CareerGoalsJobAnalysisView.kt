package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CareerGoalsJobAnalysisResult
import com.example.data.model.ProfileImprovementSuggestion
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
import com.example.ui.viewmodel.TitanScreen
import com.example.ui.viewmodel.TitanViewModel

@Composable
fun CareerGoalsJobAnalysisView(
  viewModel: TitanViewModel,
  modifier: Modifier = Modifier
) {
  val careerGoalsInput by viewModel.targetCareerGoalsInput.collectAsState()
  val jobDescriptionInput by viewModel.goalsAnalysisJobDescriptionInput.collectAsState()
  val isAnalyzing by viewModel.isAnalyzingGoalsVsJob.collectAsState()
  val analysisResult by viewModel.careerGoalsVsJobAnalysisResult.collectAsState()

  var targetRole by remember { mutableStateOf("Associate Strategy & Operations Lead") }
  var targetCompany by remember { mutableStateOf("Zepto") }
  var isInputExpanded by remember { mutableStateOf(analysisResult == null) }

  Column(
    modifier = modifier
      .fillMaxWidth()
      .testTag("career_goals_job_analysis_view"),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // Header Banner
    Card(
      colors = CardDefaults.cardColors(containerColor = SlateCard),
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
            Box(
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(TitanCyan.copy(alpha = 0.15f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = TitanCyan,
                modifier = Modifier.size(20.dp)
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "GEMINI GOALS VS JD ANALYZER",
                style = MaterialTheme.typography.labelSmall,
                color = TitanCyan,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "Profile Improvement Engine",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimaryDark
              )
            }
          }

          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(TitanIndigo.copy(alpha = 0.3f))
              .padding(horizontal = 8.dp, vertical = 4.dp)
          ) {
            Text(
              text = "GEMINI 2.5",
              fontSize = 9.sp,
              fontWeight = FontWeight.Bold,
              color = TitanCyan
            )
          }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(
          text = "Benchmark your uploaded career goals against target job specs. Gemini analyzes alignment gaps and generates actionable suggestions to elevate your professional profile.",
          style = MaterialTheme.typography.bodySmall,
          color = TextSecondaryDark,
          lineHeight = 16.sp
        )
      }
    }

    // Input & Configuration Card
    Card(
      colors = CardDefaults.cardColors(containerColor = SlateCard),
      shape = RoundedCornerShape(12.dp),
      border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
    ) {
      Column(modifier = Modifier.padding(14.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "TARGET TARGETING PARAMETERS",
            style = MaterialTheme.typography.labelSmall,
            color = TitanGold,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = if (isInputExpanded) "Collapse" else "Edit Inputs",
            fontSize = 11.sp,
            color = TitanCyan,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable { isInputExpanded = !isInputExpanded }
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Preset Target Company Chips
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          listOf("Zepto", "Swiggy", "Google", "Microsoft").forEach { comp ->
            val isSelected = targetCompany.equals(comp, ignoreCase = true)
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(if (isSelected) TitanCyan else SlateElevated)
                .clickable {
                  targetCompany = comp
                  targetRole = when (comp) {
                    "Zepto" -> "Associate Strategy & Operations Lead"
                    "Swiggy" -> "Quick Commerce Category & Ops Manager"
                    "Google" -> "Strategic Partner Operations Specialist"
                    else -> "Operations & Program Lead"
                  }
                }
                .padding(horizontal = 8.dp, vertical = 5.dp)
            ) {
              Text(
                text = comp,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) ObsidianDark else TextPrimaryDark
              )
            }
          }
        }

        AnimatedVisibility(visible = isInputExpanded) {
          Column(
            modifier = Modifier.padding(top = 10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              OutlinedTextField(
                value = targetRole,
                onValueChange = { targetRole = it },
                label = { Text("Target Role Title", fontSize = 11.sp) },
                singleLine = true,
                modifier = Modifier.weight(1.2f),
                colors = OutlinedTextFieldDefaults.colors(
                  focusedBorderColor = TitanCyan,
                  unfocusedBorderColor = SlateBorder,
                  focusedTextColor = TextPrimaryDark,
                  unfocusedTextColor = TextPrimaryDark
                )
              )
              OutlinedTextField(
                value = targetCompany,
                onValueChange = { targetCompany = it },
                label = { Text("Target Company", fontSize = 11.sp) },
                singleLine = true,
                modifier = Modifier.weight(0.8f),
                colors = OutlinedTextFieldDefaults.colors(
                  focusedBorderColor = TitanCyan,
                  unfocusedBorderColor = SlateBorder,
                  focusedTextColor = TextPrimaryDark,
                  unfocusedTextColor = TextPrimaryDark
                )
              )
            }

            OutlinedTextField(
              value = careerGoalsInput,
              onValueChange = { viewModel.targetCareerGoalsInput.value = it },
              label = { Text("Candidate's Career Goals & Target Milestones", fontSize = 11.sp) },
              modifier = Modifier.fillMaxWidth(),
              maxLines = 3,
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = TitanCyan,
                unfocusedBorderColor = SlateBorder,
                focusedTextColor = TextPrimaryDark,
                unfocusedTextColor = TextPrimaryDark
              )
            )

            OutlinedTextField(
              value = jobDescriptionInput,
              onValueChange = { viewModel.goalsAnalysisJobDescriptionInput.value = it },
              label = { Text("Target Job Description (JD Mandate)", fontSize = 11.sp) },
              modifier = Modifier.fillMaxWidth(),
              maxLines = 4,
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = TitanCyan,
                unfocusedBorderColor = SlateBorder,
                focusedTextColor = TextPrimaryDark,
                unfocusedTextColor = TextPrimaryDark
              )
            )
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Analyze Action Button
        Button(
          onClick = {
            isInputExpanded = false
            viewModel.analyzeCareerGoalsAgainstJobDescription(
              careerGoals = careerGoalsInput,
              jobDescription = jobDescriptionInput,
              targetRole = targetRole,
              targetCompany = targetCompany
            )
          },
          enabled = !isAnalyzing,
          colors = ButtonDefaults.buttonColors(containerColor = TitanCyan, contentColor = ObsidianDark),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("analyze_goals_vs_jd_button")
        ) {
          if (isAnalyzing) {
            CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = ObsidianDark)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Analyzing with Gemini AI...", fontWeight = FontWeight.Bold, fontSize = 12.sp)
          } else {
            Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Analyze Goals vs JD & Improve Profile", fontWeight = FontWeight.Bold, fontSize = 12.sp)
          }
        }
      }
    }

    // Results Section
    if (analysisResult != null) {
      val res = analysisResult!!

      // Alignment Score & Assessment Card
      Card(
        colors = CardDefaults.cardColors(containerColor = SlateCard),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "ROLE ALIGNMENT FIT",
                style = MaterialTheme.typography.labelSmall,
                color = TitanEmerald,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "${res.targetRole} @ ${res.targetCompany}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimaryDark
              )
            }

            Box(
              modifier = Modifier
                .clip(CircleShape)
                .background(TitanEmerald.copy(alpha = 0.15f))
                .border(2.dp, TitanEmerald, CircleShape)
                .size(54.dp),
              contentAlignment = Alignment.Center
            ) {
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                  text = "${res.alignmentScore}%",
                  fontWeight = FontWeight.ExtraBold,
                  fontSize = 15.sp,
                  color = TitanEmerald
                )
                Text("MATCH", fontSize = 7.sp, fontWeight = FontWeight.Bold, color = TextSecondaryDark)
              }
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          Text(
            text = res.fitAssessment,
            fontSize = 12.sp,
            color = TextSecondaryDark,
            lineHeight = 17.sp,
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(8.dp))
              .background(SlateElevated)
              .padding(10.dp)
          )
        }
      }

      // Strengths & Missing Skills Grid
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        // Key Strengths
        Card(
          colors = CardDefaults.cardColors(containerColor = SlateCard),
          shape = RoundedCornerShape(10.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder),
          modifier = Modifier.weight(1f)
        ) {
          Column(modifier = Modifier.padding(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.CheckCircle, contentDescription = null, tint = TitanEmerald, modifier = Modifier.size(14.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Key Strengths", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TitanEmerald)
            }
            Spacer(modifier = Modifier.height(6.dp))
            res.keyStrengths.forEach { s ->
              Row(modifier = Modifier.padding(vertical = 2.dp)) {
                Text("• ", color = TitanEmerald, fontSize = 10.sp)
                Text(s, color = TextPrimaryDark, fontSize = 10.sp, lineHeight = 13.sp)
              }
            }
          }
        }

        // Missing Keywords / Gaps
        Card(
          colors = CardDefaults.cardColors(containerColor = SlateCard),
          shape = RoundedCornerShape(10.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder),
          modifier = Modifier.weight(1f)
        ) {
          Column(modifier = Modifier.padding(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Warning, contentDescription = null, tint = TitanGold, modifier = Modifier.size(14.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("JD Skill Gaps", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TitanGold)
            }
            Spacer(modifier = Modifier.height(6.dp))
            res.missingKeywordsAndSkills.forEach { m ->
              Row(modifier = Modifier.padding(vertical = 2.dp)) {
                Text("• ", color = TitanGold, fontSize = 10.sp)
                Text(m, color = TextPrimaryDark, fontSize = 10.sp, lineHeight = 13.sp)
              }
            }
          }
        }
      }

      // Personalized Profile Improvement Suggestions
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "PERSONALIZED PROFILE SUGGESTIONS (${res.suggestions.size})",
            style = MaterialTheme.typography.labelSmall,
            color = TitanCyan,
            fontWeight = FontWeight.Bold
          )
        }

        res.suggestions.forEach { sug ->
          ProfileSuggestionCard(sug)
        }
      }

      // Tactical Action Steps
      Card(
        colors = CardDefaults.cardColors(containerColor = SlateCard),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Lightbulb, contentDescription = null, tint = TitanGold, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "TACTICAL PROFILE IMPROVEMENT STEPS",
              style = MaterialTheme.typography.labelSmall,
              color = TitanGold,
              fontWeight = FontWeight.Bold
            )
          }

          Spacer(modifier = Modifier.height(8.dp))

          res.tacticalNextSteps.forEachIndexed { idx, step ->
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
              verticalAlignment = Alignment.Top
            ) {
              Box(
                modifier = Modifier
                  .size(18.dp)
                  .clip(CircleShape)
                  .background(TitanGold.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
              ) {
                Text("${idx + 1}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TitanGold)
              }
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = step,
                fontSize = 11.sp,
                color = TextPrimaryDark,
                lineHeight = 16.sp
              )
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          Button(
            onClick = {
              viewModel.navigateTo(TitanScreen.COPILOT)
              viewModel.sendCopilotMessage(
                "Career Coach Request: Review the Gemini Profile Suggestions for my target role '${res.targetRole}' at '${res.targetCompany}'. The identified gaps are: ${res.missingKeywordsAndSkills.joinToString(", ")}. How should I position my dark store inventory turnaround and SQL achievements in my LinkedIn summary?"
              )
            },
            colors = ButtonDefaults.buttonColors(containerColor = SlateElevated, contentColor = TitanCyan),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Icon(Icons.Default.Psychology, contentDescription = null, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Discuss Profile Improvements with Career Coach", fontSize = 11.sp, fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}

@Composable
private fun ProfileSuggestionCard(
  suggestion: ProfileImprovementSuggestion
) {
  var isComparingExpanded by remember { mutableStateOf(false) }

  Card(
    colors = CardDefaults.cardColors(containerColor = SlateCard),
    shape = RoundedCornerShape(12.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(TitanIndigo.copy(alpha = 0.3f))
            .padding(horizontal = 8.dp, vertical = 3.dp)
        ) {
          Text(
            text = suggestion.category,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = TitanCyan
          )
        }

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(
              when (suggestion.priority) {
                "CRITICAL" -> Color(0xFFFF5252).copy(alpha = 0.2f)
                "HIGH" -> TitanGold.copy(alpha = 0.2f)
                else -> TitanCyan.copy(alpha = 0.2f)
              }
            )
            .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
          Text(
            text = suggestion.priority,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = when (suggestion.priority) {
              "CRITICAL" -> Color(0xFFFF5252)
              "HIGH" -> TitanGold
              else -> TitanCyan
            }
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = "Current Gap: ${suggestion.currentGap}",
        fontSize = 11.sp,
        color = TextSecondaryDark,
        lineHeight = 15.sp
      )

      Spacer(modifier = Modifier.height(4.dp))

      Text(
        text = suggestion.actionableSuggestion,
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
        color = TextPrimaryDark,
        lineHeight = 16.sp
      )

      Spacer(modifier = Modifier.height(8.dp))

      // Toggle Before vs After
      Row(
        modifier = Modifier
          .clip(RoundedCornerShape(6.dp))
          .background(SlateElevated)
          .clickable { isComparingExpanded = !isComparingExpanded }
          .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(
          imageVector = Icons.AutoMirrored.Filled.CompareArrows,
          contentDescription = null,
          tint = TitanCyan,
          modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = if (isComparingExpanded) "Hide Before / After Framing" else "View Executive Before vs After",
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          color = TitanCyan
        )
      }

      AnimatedVisibility(visible = isComparingExpanded) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp),
          verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          // Before
          Card(
            colors = CardDefaults.cardColors(containerColor = SlateElevated),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(8.dp)) {
              Text("BEFORE (Standard / Passive)", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextMutedDark)
              Spacer(modifier = Modifier.height(2.dp))
              Text(suggestion.beforeExample, fontSize = 11.sp, color = TextSecondaryDark)
            }
          }

          // After
          Card(
            colors = CardDefaults.cardColors(containerColor = TitanEmerald.copy(alpha = 0.08f)),
            border = androidx.compose.foundation.BorderStroke(1.dp, TitanEmerald.copy(alpha = 0.3f)),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(8.dp)) {
              Text("AFTER (Executive Pitch / High Leverage)", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TitanEmerald)
              Spacer(modifier = Modifier.height(2.dp))
              Text(suggestion.afterExample, fontSize = 11.sp, color = TextPrimaryDark, fontWeight = FontWeight.Medium)
            }
          }
        }
      }
    }
  }
}
