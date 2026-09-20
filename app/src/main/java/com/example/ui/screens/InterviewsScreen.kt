package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Star
import com.example.ui.viewmodel.TitanScreen
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.StarStory
import com.example.ui.components.AudioWaveformVisualizer
import com.example.ui.components.InterviewSimulatorView
import com.example.ui.components.PreInterviewCalmView
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
import com.example.ui.viewmodel.TitanViewModel

@Composable
fun InterviewsScreen(
  viewModel: TitanViewModel
) {
  val stories by viewModel.stories.collectAsState()
  val currentQuestion by viewModel.currentInterviewQuestion.collectAsState()
  val currentAnswer by viewModel.currentInterviewAnswer.collectAsState()
  val evaluationResult by viewModel.interviewEvaluationResult.collectAsState()
  val isEvaluating by viewModel.isEvaluatingInterview.collectAsState()
  val applications by viewModel.applications.collectAsState()
  val selectedAppId by viewModel.selectedInterviewApplicationId.collectAsState()
  val isSavingToFirestore by viewModel.isSavingInterviewToFirestore.collectAsState()
  val firestoreSaveStatus by viewModel.interviewFirestoreSaveStatus.collectAsState()

  val activeApplication = remember(applications, selectedAppId) {
    applications.firstOrNull { it.id == selectedAppId } ?: applications.firstOrNull()
  }

  val selectedTab by viewModel.interviewsScreenActiveTab.collectAsState()

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(ObsidianDark)
      .padding(horizontal = 16.dp, vertical = 8.dp)
      .testTag("interviews_screen")
  ) {
    // Header
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Text(
          text = "INTERVIEW OS & INTELLIGENCE",
          style = MaterialTheme.typography.labelSmall,
          color = TitanGold,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = "Microsoft Final Round Prep",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
          color = TextPrimaryDark
        )
      }

      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(8.dp))
          .background(SlateElevated)
          .border(1.dp, SlateBorder, RoundedCornerShape(8.dp))
          .padding(horizontal = 10.dp, vertical = 6.dp)
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Bolt, contentDescription = null, tint = TitanCyan, modifier = Modifier.size(14.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Score: 96%", style = MaterialTheme.typography.labelSmall, color = TitanCyan, fontWeight = FontWeight.Bold)
        }
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Tabs
    TabRow(
      selectedTabIndex = selectedTab.coerceIn(0, 3),
      containerColor = SlateCard,
      indicator = { tabPositions ->
        TabRowDefaults.SecondaryIndicator(
          Modifier.tabIndicatorOffset(tabPositions[selectedTab.coerceIn(0, 3)]),
          color = if (selectedTab == 3) TitanGold else TitanCyan
        )
      }
    ) {
      Tab(
        selected = selectedTab == 0,
        onClick = { viewModel.setInterviewsScreenTab(0) },
        text = {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              Icons.Default.AutoAwesome,
              contentDescription = null,
              modifier = Modifier.size(13.dp),
              tint = if (selectedTab == 0) TitanCyan else TextSecondaryDark
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              "Simulator",
              color = if (selectedTab == 0) TitanCyan else TextSecondaryDark,
              fontWeight = FontWeight.Bold,
              fontSize = 11.sp
            )
          }
        },
        modifier = Modifier.testTag("tab_interview_simulator")
      )
      Tab(
        selected = selectedTab == 1,
        onClick = { viewModel.setInterviewsScreenTab(1) },
        text = {
          Text(
            "AI Drills",
            color = if (selectedTab == 1) TitanCyan else TextSecondaryDark,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp
          )
        },
        modifier = Modifier.testTag("tab_ai_drills")
      )
      Tab(
        selected = selectedTab == 2,
        onClick = { viewModel.setInterviewsScreenTab(2) },
        text = {
          Text(
            "STAR Bank (${stories.size})",
            color = if (selectedTab == 2) TitanCyan else TextSecondaryDark,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp
          )
        },
        modifier = Modifier.testTag("tab_star_bank")
      )
      Tab(
        selected = selectedTab == 3,
        onClick = { viewModel.setInterviewsScreenTab(3) },
        text = {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              Icons.Default.Psychology,
              contentDescription = null,
              modifier = Modifier.size(13.dp),
              tint = if (selectedTab == 3) TitanGold else TextSecondaryDark
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              "Calm & Pep",
              color = if (selectedTab == 3) TitanGold else TextSecondaryDark,
              fontWeight = FontWeight.Bold,
              fontSize = 11.sp
            )
          }
        },
        modifier = Modifier.testTag("tab_pre_interview_calm")
      )
    }

    Spacer(modifier = Modifier.height(12.dp))

    if (selectedTab == 0) {
      // Gemini-Powered Target Company & Profile Grounded Interview Simulator
      InterviewSimulatorView(
        viewModel = viewModel,
        modifier = Modifier.fillMaxSize()
      )
    } else if (selectedTab == 1) {
      val isSimulatingAudio by viewModel.isSimulatingAudioRecording.collectAsState()

      // AI Mock Interview Simulator
      LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        // Pre-Interview Calm Quick Launch Card
        item {
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .clickable { viewModel.setInterviewsScreenTab(3) }
              .testTag("launch_pre_interview_calm_banner"),
            colors = CardDefaults.cardColors(containerColor = SlateCard),
            shape = RoundedCornerShape(10.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, TitanCyan.copy(alpha = 0.4f))
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Box(
                  modifier = Modifier
                    .size(32.dp)
                    .clip(androidx.compose.foundation.shape.CircleShape)
                    .background(TitanCyan.copy(alpha = 0.15f)),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(Icons.Default.Psychology, contentDescription = null, tint = TitanCyan, modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                  Text("PRE-INTERVIEW CALM MODE", style = MaterialTheme.typography.labelSmall, color = TitanCyan, fontWeight = FontWeight.Bold)
                  Text("Interactive Box Breathing · 2-Min Gemini Pep Talk", style = MaterialTheme.typography.bodySmall, color = TextPrimaryDark, fontSize = 11.sp)
                }
              }
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(6.dp))
                  .background(TitanCyan)
                  .padding(horizontal = 8.dp, vertical = 4.dp)
              ) {
                Text("LAUNCH →", color = ObsidianDark, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold)
              }
            }
          }
        }

        // Voice Audio Rehearsal Visualizer
        item {
          AudioWaveformVisualizer(
            isRecording = isSimulatingAudio,
            onToggleRecording = { viewModel.toggleAudioRecording() }
          )
        }

        // Preset Drills
        item {
          Column {
            Text(
              text = "SELECT EXECUTIVE INTERVIEW DRILL:",
              style = MaterialTheme.typography.labelSmall,
              color = TextMutedDark,
              fontWeight = FontWeight.Bold,
              fontSize = 9.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Box(
                modifier = Modifier
                  .weight(1f)
                  .clip(RoundedCornerShape(6.dp))
                  .background(SlateElevated)
                  .border(1.dp, SlateBorder, RoundedCornerShape(6.dp))
                  .clickable {
                    viewModel.setInterviewQuestion("Tell me about a time you solved a complex operational or supply chain bottleneck using data.")
                  }
                  .padding(vertical = 6.dp, horizontal = 4.dp),
                contentAlignment = Alignment.Center
              ) {
                Text("Microsoft Ops", color = TitanCyan, fontWeight = FontWeight.Bold, fontSize = 10.sp)
              }

              Box(
                modifier = Modifier
                  .weight(1f)
                  .clip(RoundedCornerShape(6.dp))
                  .background(SlateElevated)
                  .border(1.dp, SlateBorder, RoundedCornerShape(6.dp))
                  .clickable {
                    viewModel.setInterviewQuestion("How would you evaluate market entry strategy and TAM sizing for an Indian SaaS expanding to SEA?")
                  }
                  .padding(vertical = 6.dp, horizontal = 4.dp),
                contentAlignment = Alignment.Center
              ) {
                Text("Bain Strategy", color = TitanGold, fontWeight = FontWeight.Bold, fontSize = 10.sp)
              }

              Box(
                modifier = Modifier
                  .weight(1f)
                  .clip(RoundedCornerShape(6.dp))
                  .background(SlateElevated)
                  .border(1.dp, SlateBorder, RoundedCornerShape(6.dp))
                  .clickable {
                    viewModel.setInterviewQuestion("Describe how you diagnosed and improved a 28% drop-off in a merchant onboarding funnel.")
                  }
                  .padding(vertical = 6.dp, horizontal = 4.dp),
                contentAlignment = Alignment.Center
              ) {
                Text("Razorpay KYC", color = TitanEmerald, fontWeight = FontWeight.Bold, fontSize = 10.sp)
              }
            }
          }
        }

        // Target Application Selector from Firestore
        if (applications.isNotEmpty()) {
          item {
            Column {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(Icons.Default.Business, contentDescription = null, tint = TitanCyan, modifier = Modifier.size(14.dp))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(
                    text = "TARGET APPLICATION (FIRESTORE):",
                    style = MaterialTheme.typography.labelSmall,
                    color = TitanCyan,
                    fontWeight = FontWeight.Bold,
                    fontSize = 9.sp
                  )
                }
                Text(
                  text = "Score: ${activeApplication?.interviewScore ?: 0}/100",
                  style = MaterialTheme.typography.labelSmall,
                  color = TitanEmerald,
                  fontWeight = FontWeight.Bold,
                  fontSize = 9.sp
                )
              }

              Spacer(modifier = Modifier.height(4.dp))

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                applications.take(4).forEach { app ->
                  val isSelected = app.id == (activeApplication?.id ?: "")
                  Box(
                    modifier = Modifier
                      .weight(1f)
                      .clip(RoundedCornerShape(6.dp))
                      .background(if (isSelected) TitanCyan.copy(alpha = 0.2f) else SlateCard)
                      .border(
                        1.dp,
                        if (isSelected) TitanCyan else SlateBorder,
                        RoundedCornerShape(6.dp)
                      )
                      .clickable {
                        viewModel.selectedInterviewApplicationId.value = app.id
                      }
                      .padding(vertical = 6.dp, horizontal = 4.dp),
                    contentAlignment = Alignment.Center
                  ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                      Text(
                        text = app.companyName,
                        color = if (isSelected) TitanCyan else TextPrimaryDark,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        maxLines = 1
                      )
                      Text(
                        text = app.status,
                        color = if (isSelected) TitanCyan.copy(alpha = 0.8f) else TextMutedDark,
                        fontSize = 8.sp,
                        maxLines = 1
                      )
                    }
                  }
                }
              }
            }
          }
        }

        // Question Card
        item {
          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = SlateCard),
            border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "ACTIVE SIMULATION PROMPT",
                  style = MaterialTheme.typography.labelSmall,
                  color = TitanCyan,
                  fontWeight = FontWeight.Bold,
                  fontSize = 9.sp
                )
                Text(
                  text = "${activeApplication?.companyName ?: "Tier-1 Tech"} · ${activeApplication?.roleTitle?.take(16) ?: "Strategy"}",
                  style = MaterialTheme.typography.labelSmall,
                  color = TitanGold,
                  fontSize = 9.sp,
                  fontWeight = FontWeight.Bold
                )
              }

              Spacer(modifier = Modifier.height(6.dp))

              Text(
                text = currentQuestion,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimaryDark
              )

              Spacer(modifier = Modifier.height(8.dp))

              // Question Presets
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                Box(
                  modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(4.dp))
                    .background(SlateElevated)
                    .clickable {
                      viewModel.currentInterviewQuestion.value = "Tell me about a time you solved a complex operational or supply chain bottleneck using data."
                    }
                    .padding(4.dp),
                  contentAlignment = Alignment.Center
                ) {
                  Text("Ops Bottleneck", fontSize = 8.sp, color = TextSecondaryDark, maxLines = 1)
                }

                Box(
                  modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(4.dp))
                    .background(SlateElevated)
                    .clickable {
                      viewModel.currentInterviewQuestion.value = "How do you balance high-velocity user expansion against dark store unit economics?"
                    }
                    .padding(4.dp),
                  contentAlignment = Alignment.Center
                ) {
                  Text("Unit Economics", fontSize = 8.sp, color = TextSecondaryDark, maxLines = 1)
                }

                Box(
                  modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(4.dp))
                    .background(SlateElevated)
                    .clickable {
                      viewModel.currentInterviewQuestion.value = "Describe a situation where cross-functional stakeholders had conflicting OKRs. How did you align them?"
                    }
                    .padding(4.dp),
                  contentAlignment = Alignment.Center
                ) {
                  Text("Stakeholder OKRs", fontSize = 8.sp, color = TextSecondaryDark, maxLines = 1)
                }
              }
            }
          }
        }

        // 1-Tap STAR Story Injections
        item {
          Column {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "INJECT GROUND-TRUTH STAR EVIDENCE:",
                style = MaterialTheme.typography.labelSmall,
                color = TextMutedDark,
                fontWeight = FontWeight.Bold,
                fontSize = 9.sp
              )
              Text("Tap to prefill", style = MaterialTheme.typography.labelSmall, color = TitanCyan, fontSize = 9.sp)
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Box(
                modifier = Modifier
                  .weight(1f)
                  .clip(RoundedCornerShape(6.dp))
                  .background(SlateCard)
                  .border(1.dp, TitanEmerald.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                  .clickable { viewModel.fillCandidateStarAnswer("FAMILY_BIZ") }
                  .padding(vertical = 6.dp, horizontal = 4.dp),
                contentAlignment = Alignment.Center
              ) {
                Text("Family Biz (42%)", color = TitanEmerald, fontWeight = FontWeight.Bold, fontSize = 9.sp)
              }

              Box(
                modifier = Modifier
                  .weight(1f)
                  .clip(RoundedCornerShape(6.dp))
                  .background(SlateCard)
                  .border(1.dp, TitanGold.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                  .clickable { viewModel.fillCandidateStarAnswer("RAZORPAY_CASE") }
                  .padding(vertical = 6.dp, horizontal = 4.dp),
                contentAlignment = Alignment.Center
              ) {
                Text("Razorpay Hackathon", color = TitanGold, fontWeight = FontWeight.Bold, fontSize = 9.sp)
              }

              Box(
                modifier = Modifier
                  .weight(1f)
                  .clip(RoundedCornerShape(6.dp))
                  .background(SlateCard)
                  .border(1.dp, TitanCyan.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                  .clickable { viewModel.fillCandidateStarAnswer("CAPSTONE") }
                  .padding(vertical = 6.dp, horizontal = 4.dp),
                contentAlignment = Alignment.Center
              ) {
                Text("BBA Capstone 98%", color = TitanCyan, fontWeight = FontWeight.Bold, fontSize = 9.sp)
              }
            }
          }
        }

        // Answer Input Field
        item {
          Column {
            Text(
              text = "YOUR STRUCTURED RESPONSE (STAR FRAMEWORK):",
              style = MaterialTheme.typography.labelSmall,
              color = TextMutedDark,
              fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))

            OutlinedTextField(
              value = currentAnswer,
              onValueChange = { viewModel.currentInterviewAnswer.value = it },
              modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .testTag("interview_answer_input"),
              placeholder = {
                Text(
                  "Situation: Wholesale supply chain bottlenecks...\nTask: Automate inventory forecasting...\nAction: Built SQL pipeline & AI demand model...\nResult: 42% faster cycle time, zero stockouts.",
                  color = TextMutedDark,
                  fontSize = 11.sp
                )
              },
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = TitanCyan,
                unfocusedBorderColor = SlateBorder,
                focusedContainerColor = SlateCard,
                unfocusedContainerColor = SlateCard,
                focusedTextColor = TextPrimaryDark,
                unfocusedTextColor = TextPrimaryDark
              ),
              shape = RoundedCornerShape(10.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Button(
              onClick = {
                val role = activeApplication?.roleTitle ?: "Associate Strategy Analyst"
                val comp = activeApplication?.companyName ?: "Microsoft India"
                viewModel.evaluateInterviewAnswer(role, comp)
              },
              colors = ButtonDefaults.buttonColors(containerColor = TitanGold, contentColor = ObsidianDark),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier
                .fillMaxWidth()
                .testTag("evaluate_interview_button"),
              enabled = !isEvaluating && currentAnswer.isNotBlank()
            ) {
              if (isEvaluating) {
                CircularProgressIndicator(color = ObsidianDark, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Analyzing STAR Structure & Executive Delivery...", fontWeight = FontWeight.Bold)
              } else {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Evaluate Response with AI Scorecard", fontWeight = FontWeight.Bold)
              }
            }
          }
        }

        // Scorecard / Evaluation Result with Firestore Integration
        if (evaluationResult != null) {
          item {
            Card(
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(12.dp),
              colors = CardDefaults.cardColors(containerColor = SlateElevated),
              border = androidx.compose.foundation.BorderStroke(1.dp, TitanEmerald.copy(alpha = 0.5f))
            ) {
              Column(modifier = Modifier.padding(14.dp)) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = TitanEmerald, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                      text = "AI EXECUTIVE SCORECARD",
                      style = MaterialTheme.typography.labelMedium,
                      color = TitanEmerald,
                      fontWeight = FontWeight.Bold
                    )
                  }

                  Box(
                    modifier = Modifier
                      .clip(RoundedCornerShape(4.dp))
                      .background(TitanEmerald.copy(alpha = 0.2f))
                      .padding(horizontal = 6.dp, vertical = 2.dp)
                  ) {
                    Text(
                      text = "REAL-TIME FEEDBACK",
                      color = TitanEmerald,
                      fontWeight = FontWeight.Bold,
                      fontSize = 8.sp
                    )
                  }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                  text = evaluationResult ?: "",
                  style = MaterialTheme.typography.bodySmall,
                  color = TextPrimaryDark,
                  lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Action Buttons: Save to Firestore & Ask Career Coach
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                  Button(
                    onClick = {
                      viewModel.saveInterviewSimulationToFirestore(activeApplication?.id)
                    },
                    enabled = !isSavingToFirestore,
                    colors = ButtonDefaults.buttonColors(
                      containerColor = TitanEmerald,
                      contentColor = ObsidianDark
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                      .weight(1f)
                      .testTag("save_interview_firestore_button")
                  ) {
                    if (isSavingToFirestore) {
                      CircularProgressIndicator(color = ObsidianDark, modifier = Modifier.size(14.dp), strokeWidth = 2.dp)
                      Spacer(modifier = Modifier.width(4.dp))
                      Text("Saving...", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    } else {
                      Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(14.dp))
                      Spacer(modifier = Modifier.width(4.dp))
                      Text("Save to Firestore DB", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                  }

                  Button(
                    onClick = {
                      activeApplication?.let {
                        viewModel.askCareerCoachAboutApplication(it, "My interview answer score is ${it.interviewScore}. How can I make my delivery more compelling for executive interviewers?")
                      } ?: run {
                        viewModel.navigateTo(TitanScreen.COPILOT)
                        viewModel.sendCopilotMessage("As my career coach, evaluate my response to: '$currentQuestion'")
                      }
                    },
                    colors = ButtonDefaults.buttonColors(
                      containerColor = TitanCyan,
                      contentColor = ObsidianDark
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                      .weight(1f)
                      .testTag("ask_career_coach_interview_button")
                  ) {
                    Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Ask Coach Advice", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                  }
                }

                if (firestoreSaveStatus != null) {
                  Spacer(modifier = Modifier.height(8.dp))
                  Text(
                    text = firestoreSaveStatus ?: "",
                    style = MaterialTheme.typography.labelSmall,
                    color = TitanEmerald,
                    fontSize = 10.sp
                  )
                }
              }
            }
          }
        }

        item {
          Spacer(modifier = Modifier.height(16.dp))
        }
      }
    } else if (selectedTab == 2) {
      // STAR Story Bank
      LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        items(stories, key = { it.id }) { story ->
          StarStoryCard(story = story)
        }

        item {
          Spacer(modifier = Modifier.height(16.dp))
        }
      }
    } else {
      // Pre-Interview Calm & Grounding View (Breathing Guide + 2-Min Gemini Motivational Pep Talk)
      PreInterviewCalmView(
        viewModel = viewModel,
        modifier = Modifier.fillMaxSize()
      )
    }
  }
}

@Composable
fun StarStoryCard(story: StarStory) {
  var isExpanded by remember { mutableStateOf(false) }

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .clickable { isExpanded = !isExpanded }
      .testTag("star_story_card_${story.id}"),
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = SlateCard),
    border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.weight(1f)
        ) {
          Icon(Icons.Default.Star, contentDescription = null, tint = TitanGold, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = story.title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = TextPrimaryDark
          )
        }

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(TitanCyan.copy(alpha = 0.15f))
            .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
          Text(
            text = story.storyType,
            style = MaterialTheme.typography.labelSmall,
            color = TitanCyan,
            fontWeight = FontWeight.Bold,
            fontSize = 9.sp
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = "SITUATION & TASK:",
        style = MaterialTheme.typography.labelSmall,
        color = TextMutedDark,
        fontSize = 9.sp,
        fontWeight = FontWeight.Bold
      )
      Text(
        text = "${story.situation} ${story.task}",
        style = MaterialTheme.typography.bodySmall,
        color = TextSecondaryDark,
        fontSize = 11.sp,
        maxLines = if (isExpanded) Int.MAX_VALUE else 2
      )

      if (isExpanded) {
        Spacer(modifier = Modifier.height(8.dp))

        Text(
          text = "ACTION TAKEN:",
          style = MaterialTheme.typography.labelSmall,
          color = TitanCyan,
          fontSize = 9.sp,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = story.action,
          style = MaterialTheme.typography.bodySmall,
          color = TextPrimaryDark,
          fontSize = 11.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
          text = "QUANTIFIED RESULT & ROI:",
          style = MaterialTheme.typography.labelSmall,
          color = TitanEmerald,
          fontSize = 9.sp,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = story.result,
          style = MaterialTheme.typography.bodySmall,
          color = TitanEmerald,
          fontSize = 11.sp,
          fontWeight = FontWeight.Medium
        )
      }
    }
  }
}
