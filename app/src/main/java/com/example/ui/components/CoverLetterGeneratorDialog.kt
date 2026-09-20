package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FolderSpecial
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.CoverLetterGenerated
import com.example.data.model.CoverLetterTone
import com.example.data.model.Job
import com.example.data.model.SampleJobDescription
import com.example.ui.theme.ObsidianDark
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateCard
import com.example.ui.theme.SlateDarker
import com.example.ui.theme.SlateElevated
import com.example.ui.theme.TextMutedDark
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import com.example.ui.theme.TitanCyan
import com.example.ui.theme.TitanCyanGlow
import com.example.ui.theme.TitanCyanMuted
import com.example.ui.theme.TitanEmerald
import com.example.ui.theme.TitanGold
import com.example.ui.viewmodel.TitanViewModel

@Composable
fun CoverLetterGeneratorDialog(
  viewModel: TitanViewModel,
  onDismiss: () -> Unit
) {
  val context = LocalContext.current
  val jobs by viewModel.jobs.collectAsState()
  val userProfile by viewModel.userProfile.collectAsState()
  val verifiedFacts by viewModel.verifiedFacts.collectAsState()
  val parsedResume by viewModel.parsedResumeDocument.collectAsState()
  val availableMockDocs = viewModel.availableMockDocuments
  val selectedDoc by viewModel.selectedMockDocument.collectAsState()

  val currentLetter by viewModel.currentCoverLetter.collectAsState()
  val isGenerating by viewModel.isGeneratingCoverLetter.collectAsState()
  val targetRole by viewModel.coverLetterTargetRole.collectAsState()
  val targetCompany by viewModel.coverLetterTargetCompany.collectAsState()
  val jobDescription by viewModel.coverLetterJobDescription.collectAsState()
  val selectedJobId by viewModel.coverLetterSelectedJobId.collectAsState()
  val selectedTone by viewModel.coverLetterTone.collectAsState()
  val customDirectives by viewModel.coverLetterCustomDirectives.collectAsState()
  val savedLetters by viewModel.savedCoverLetters.collectAsState()
  val statusMessage by viewModel.coverLetterNotificationMessage.collectAsState()

  var selectedTab by remember { mutableIntStateOf(if (currentLetter != null) 1 else 0) }
  var isEditMode by remember { mutableStateOf(false) }
  var editableLetterText by remember(currentLetter) { mutableStateOf(currentLetter?.fullFormattedLetter ?: "") }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(
      usePlatformDefaultWidth = false,
      dismissOnBackPress = true,
      dismissOnClickOutside = false
    )
  ) {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(ObsidianDark)
        .testTag("cover_letter_generator_dialog")
    ) {
      Column(modifier = Modifier.fillMaxSize()) {
        // TOP APP BAR
        CoverLetterTopBar(
          isLiveApi = currentLetter?.isLiveGeminiApi ?: false,
          onClose = onDismiss
        )

        // TAB ROW
        CoverLetterTabRow(
          selectedTab = selectedTab,
          savedCount = savedLetters.size,
          onTabSelected = { selectedTab = it }
        )

        // STATUS BANNER
        if (!statusMessage.isNullOrBlank()) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .background(SlateElevated)
              .padding(horizontal = 16.dp, vertical = 6.dp)
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Icon(
                Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = TitanCyan,
                modifier = Modifier.size(14.dp)
              )
              Text(
                text = statusMessage ?: "",
                style = MaterialTheme.typography.bodySmall,
                color = TitanCyan,
                fontSize = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
            }
          }
        }

        // TAB CONTENT
        Box(
          modifier = Modifier
            .weight(1f)
            .fillMaxWidth()
        ) {
          when (selectedTab) {
            0 -> {
              CoverLetterBuilderTab(
                viewModel = viewModel,
                jobs = jobs,
                selectedJobId = selectedJobId,
                targetRole = targetRole,
                targetCompany = targetCompany,
                jobDescription = jobDescription,
                selectedTone = selectedTone,
                customDirectives = customDirectives,
                isGenerating = isGenerating,
                selectedDocName = selectedDoc.fileName,
                verifiedFactsCount = verifiedFacts.size,
                onSelectSavedJob = { job -> viewModel.selectSavedJobForCoverLetter(job) },
                onSelectSampleJd = { sample -> viewModel.selectSampleJobDescriptionForCoverLetter(sample) },
                onTargetRoleChange = { viewModel.setCoverLetterTargetRole(it) },
                onTargetCompanyChange = { viewModel.setCoverLetterTargetCompany(it) },
                onJobDescriptionChange = { viewModel.setCoverLetterJobDescription(it) },
                onToneSelected = { viewModel.setCoverLetterTone(it) },
                onDirectivesChange = { viewModel.setCoverLetterCustomDirectives(it) },
                onGenerateClick = {
                  viewModel.generateCoverLetter()
                  selectedTab = 1
                }
              )
            }
            1 -> {
              CoverLetterPreviewTab(
                letter = currentLetter,
                isGenerating = isGenerating,
                isEditMode = isEditMode,
                editableText = editableLetterText,
                onEditableTextChange = { editableLetterText = it },
                onToggleEditMode = {
                  if (isEditMode) {
                    viewModel.updateCurrentCoverLetterText(editableLetterText)
                  }
                  isEditMode = !isEditMode
                },
                onSave = { viewModel.saveCurrentCoverLetter() },
                onRegenerate = { viewModel.generateCoverLetter() },
                onToneChange = { newTone ->
                  viewModel.setCoverLetterTone(newTone)
                  viewModel.generateCoverLetter()
                },
                onCopy = {
                  val textToCopy = currentLetter?.fullFormattedLetter ?: ""
                  if (textToCopy.isNotBlank()) {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("Cover Letter", textToCopy)
                    clipboard.setPrimaryClip(clip)
                    Toast.makeText(context, "Cover letter copied to clipboard!", Toast.LENGTH_SHORT).show()
                  }
                },
                onShare = {
                  val textToShare = currentLetter?.fullFormattedLetter ?: ""
                  if (textToShare.isNotBlank()) {
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                      type = "text/plain"
                      putExtra(Intent.EXTRA_SUBJECT, currentLetter?.subjectLine ?: "Personalized Cover Letter")
                      putExtra(Intent.EXTRA_TEXT, textToShare)
                    }
                    context.startActivity(Intent.createChooser(shareIntent, "Share Cover Letter via"))
                  }
                },
                onBackToBuilder = { selectedTab = 0 }
              )
            }
            2 -> {
              CoverLetterSavedVaultTab(
                savedLetters = savedLetters,
                onSelectLetter = { letter ->
                  viewModel.setCurrentCoverLetter(letter)
                  editableLetterText = letter.fullFormattedLetter
                  selectedTab = 1
                },
                onDeleteLetter = { id -> viewModel.deleteSavedCoverLetter(id) },
                onCopyLetter = { text ->
                  val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                  val clip = ClipData.newPlainText("Saved Cover Letter", text)
                  clipboard.setPrimaryClip(clip)
                  Toast.makeText(context, "Copied to clipboard!", Toast.LENGTH_SHORT).show()
                },
                onCreateNew = { selectedTab = 0 }
              )
            }
          }
        }
      }
    }
  }
}

@Composable
private fun CoverLetterTopBar(
  isLiveApi: Boolean,
  onClose: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .background(SlateDarker)
      .padding(horizontal = 16.dp, vertical = 12.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      Box(
        modifier = Modifier
          .size(36.dp)
          .clip(RoundedCornerShape(8.dp))
          .background(TitanCyan.copy(alpha = 0.15f)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          Icons.Default.AutoAwesome,
          contentDescription = null,
          tint = TitanCyan,
          modifier = Modifier.size(20.dp)
        )
      }

      Column {
        Text(
          text = "Cover Letter Generator",
          style = MaterialTheme.typography.titleMedium,
          color = TextPrimaryDark,
          fontWeight = FontWeight.Bold,
          fontSize = 17.sp
        )
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Box(
            modifier = Modifier
              .size(6.dp)
              .clip(CircleShape)
              .background(if (isLiveApi) TitanEmerald else TitanCyan)
          )
          Text(
            text = if (isLiveApi) "GEMINI 3.5 FLASH • LIVE API" else "GEMINI 3.5 POWERED • HIGH-FIDELITY",
            style = MaterialTheme.typography.labelSmall,
            color = if (isLiveApi) TitanEmerald else TitanCyan,
            fontSize = 9.5.sp,
            fontWeight = FontWeight.SemiBold
          )
        }
      }
    }

    IconButton(
      onClick = onClose,
      modifier = Modifier
        .size(36.dp)
        .testTag("btn_close_cover_letter_dialog")
    ) {
      Icon(
        Icons.Default.Close,
        contentDescription = "Close",
        tint = TextSecondaryDark,
        modifier = Modifier.size(20.dp)
      )
    }
  }
}

@Composable
private fun CoverLetterTabRow(
  selectedTab: Int,
  savedCount: Int,
  onTabSelected: (Int) -> Unit
) {
  TabRow(
    selectedTabIndex = selectedTab,
    containerColor = SlateDarker,
    contentColor = TitanCyan,
    indicator = { tabPositions ->
      TabRowDefaults.SecondaryIndicator(
        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
        color = TitanCyan,
        height = 3.dp
      )
    },
    divider = {
      HorizontalDivider(color = SlateBorder, thickness = 1.dp)
    }
  ) {
    Tab(
      selected = selectedTab == 0,
      onClick = { onTabSelected(0) },
      modifier = Modifier
        .testTag("tab_cover_letter_builder")
        .height(48.dp),
      text = {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
          Text(
            "Builder & JD",
            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
            fontSize = 13.sp
          )
        }
      },
      selectedContentColor = TitanCyan,
      unselectedContentColor = TextSecondaryDark
    )

    Tab(
      selected = selectedTab == 1,
      onClick = { onTabSelected(1) },
      modifier = Modifier
        .testTag("tab_cover_letter_preview")
        .height(48.dp),
      text = {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Icon(Icons.Default.Description, contentDescription = null, modifier = Modifier.size(16.dp))
          Text(
            "Letter Preview",
            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
            fontSize = 13.sp
          )
        }
      },
      selectedContentColor = TitanCyan,
      unselectedContentColor = TextSecondaryDark
    )

    Tab(
      selected = selectedTab == 2,
      onClick = { onTabSelected(2) },
      modifier = Modifier
        .testTag("tab_cover_letter_vault")
        .height(48.dp),
      text = {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Icon(Icons.Default.FolderSpecial, contentDescription = null, modifier = Modifier.size(16.dp))
          Text(
            "Saved Vault ($savedCount)",
            fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal,
            fontSize = 13.sp
          )
        }
      },
      selectedContentColor = TitanCyan,
      unselectedContentColor = TextSecondaryDark
    )
  }
}

// -----------------------------------------------------------------------------------------
// TAB 0: BUILDER & JD MATCH
// -----------------------------------------------------------------------------------------

@Composable
private fun CoverLetterBuilderTab(
  viewModel: TitanViewModel,
  jobs: List<Job>,
  selectedJobId: String?,
  targetRole: String,
  targetCompany: String,
  jobDescription: String,
  selectedTone: CoverLetterTone,
  customDirectives: String,
  isGenerating: Boolean,
  selectedDocName: String,
  verifiedFactsCount: Int,
  onSelectSavedJob: (Job) -> Unit,
  onSelectSampleJd: (SampleJobDescription) -> Unit,
  onTargetRoleChange: (String) -> Unit,
  onTargetCompanyChange: (String) -> Unit,
  onJobDescriptionChange: (String) -> Unit,
  onToneSelected: (CoverLetterTone) -> Unit,
  onDirectivesChange: (String) -> Unit,
  onGenerateClick: () -> Unit
) {
  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp),
    contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp)
  ) {
    // 1. SELECT SAVED JOB SOURCE
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SlateCard),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, SlateBorder)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Icon(Icons.Default.Work, contentDescription = null, tint = TitanCyan, modifier = Modifier.size(18.dp))
            Text(
              text = "1. Select Saved Job Description",
              style = MaterialTheme.typography.titleSmall,
              color = TextPrimaryDark,
              fontWeight = FontWeight.Bold
            )
          }

          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "Pick a saved job from your pipeline to ground the Gemini draft directly in the employer's requirements.",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondaryDark,
            fontSize = 11.5.sp
          )

          Spacer(modifier = Modifier.height(12.dp))

          // Saved Jobs Horizontal Carousel
          LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(horizontal = 2.dp)
          ) {
            items(jobs.take(8)) { job ->
              val isSelected = job.id == selectedJobId
              Card(
                modifier = Modifier
                  .width(220.dp)
                  .clickable { onSelectSavedJob(job) }
                  .testTag("job_chip_${job.id}"),
                colors = CardDefaults.cardColors(
                  containerColor = if (isSelected) SlateElevated else SlateDarker
                ),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(
                  1.dp,
                  if (isSelected) TitanCyan else SlateBorder
                )
              ) {
                Column(modifier = Modifier.padding(10.dp)) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Text(
                      text = job.companyName,
                      style = MaterialTheme.typography.labelSmall,
                      color = if (isSelected) TitanCyan else TextSecondaryDark,
                      fontWeight = FontWeight.Bold
                    )
                    Box(
                      modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(TitanEmerald.copy(alpha = 0.2f))
                        .padding(horizontal = 5.dp, vertical = 2.dp)
                    ) {
                      Text(
                        text = "${job.adiFitScore}% Match",
                        color = TitanEmerald,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                      )
                    }
                  }
                  Spacer(modifier = Modifier.height(4.dp))
                  Text(
                    text = job.title,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextPrimaryDark,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                  )
                  Spacer(modifier = Modifier.height(2.dp))
                  Text(
                    text = "${job.location} • ${job.experienceRequirement}",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMutedDark,
                    fontSize = 10.sp,
                    maxLines = 1
                  )
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Sample JDs quick picker
          Text(
            text = "Or choose sample high-tier roles:",
            style = MaterialTheme.typography.labelSmall,
            color = TextMutedDark,
            fontSize = 11.sp
          )
          Spacer(modifier = Modifier.height(6.dp))

          Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            viewModel.availableSampleJobDescriptions.forEach { sample ->
              val isSampleSelected = selectedJobId == sample.id
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(8.dp))
                  .background(if (isSampleSelected) TitanCyan.copy(alpha = 0.2f) else SlateElevated)
                  .border(
                    1.dp,
                    if (isSampleSelected) TitanCyan else SlateBorder,
                    RoundedCornerShape(8.dp)
                  )
                  .clickable { onSelectSampleJd(sample) }
                  .padding(horizontal = 10.dp, vertical = 6.dp)
              ) {
                Text(
                  text = "${sample.company} (${sample.role.take(22)}...)",
                  style = MaterialTheme.typography.bodySmall,
                  color = if (isSampleSelected) TitanCyan else TextPrimaryDark,
                  fontSize = 11.sp,
                  fontWeight = if (isSampleSelected) FontWeight.Bold else FontWeight.Normal
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Manual Fields
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            OutlinedTextField(
              value = targetCompany,
              onValueChange = onTargetCompanyChange,
              modifier = Modifier
                .weight(1f)
                .testTag("input_cover_letter_company"),
              label = { Text("Target Company", color = TextSecondaryDark, fontSize = 11.sp) },
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = TitanCyan,
                unfocusedBorderColor = SlateBorder,
                focusedTextColor = TextPrimaryDark,
                unfocusedTextColor = TextPrimaryDark
              ),
              shape = RoundedCornerShape(8.dp),
              singleLine = true
            )

            OutlinedTextField(
              value = targetRole,
              onValueChange = onTargetRoleChange,
              modifier = Modifier
                .weight(1.3f)
                .testTag("input_cover_letter_role"),
              label = { Text("Target Role Title", color = TextSecondaryDark, fontSize = 11.sp) },
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = TitanCyan,
                unfocusedBorderColor = SlateBorder,
                focusedTextColor = TextPrimaryDark,
                unfocusedTextColor = TextPrimaryDark
              ),
              shape = RoundedCornerShape(8.dp),
              singleLine = true
            )
          }

          Spacer(modifier = Modifier.height(10.dp))

          OutlinedTextField(
            value = jobDescription,
            onValueChange = onJobDescriptionChange,
            modifier = Modifier
              .fillMaxWidth()
              .height(130.dp)
              .testTag("input_cover_letter_jd"),
            label = { Text("Job Description / Requirements", color = TextSecondaryDark, fontSize = 11.sp) },
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = TitanCyan,
              unfocusedBorderColor = SlateBorder,
              focusedTextColor = TextPrimaryDark,
              unfocusedTextColor = TextPrimaryDark
            ),
            shape = RoundedCornerShape(8.dp)
          )
        }
      }
    }

    // 2. GROUNDED PROFESSIONAL PROFILE
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SlateCard),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, SlateBorder)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Icon(Icons.Default.Psychology, contentDescription = null, tint = TitanGold, modifier = Modifier.size(18.dp))
            Text(
              text = "2. Grounded Candidate Profile & Evidence",
              style = MaterialTheme.typography.titleSmall,
              color = TextPrimaryDark,
              fontWeight = FontWeight.Bold
            )
          }

          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "Gemini will weave real metrics from your uploaded resume and verified achievements into the narrative.",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondaryDark,
            fontSize = 11.5.sp
          )

          Spacer(modifier = Modifier.height(12.dp))

          // Profile Grounding Details Card
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(10.dp))
              .background(SlateElevated)
              .padding(12.dp)
          ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                  Icon(Icons.Default.Description, contentDescription = null, tint = TitanCyan, modifier = Modifier.size(16.dp))
                  Text(
                    text = selectedDocName,
                    style = MaterialTheme.typography.bodySmall,
                    color = TitanCyan,
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
                    text = "ACTIVE RESUME",
                    color = TitanEmerald,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                  )
                }
              }

              Text(
                text = "• Verified Metrics: 42% order cycle reduction, ₹18L enterprise pipeline, 1.6x inventory turns, 14 dark stores.",
                style = MaterialTheme.typography.labelSmall,
                color = TextPrimaryDark,
                fontSize = 11.sp
              )
              Text(
                text = "• Technical Stack: Advanced SQL (CTEs, Window Fns), Python ETL Automation, PowerBI, Supply Chain SLAs.",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondaryDark,
                fontSize = 10.5.sp
              )
              Text(
                text = "• Factual Grounding: $verifiedFactsCount verified career facts registered in Room database.",
                style = MaterialTheme.typography.labelSmall,
                color = TitanGold,
                fontSize = 10.5.sp
              )
            }
          }
        }
      }
    }

    // 3. TONE & STRATEGIC POSITIONING
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SlateCard),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, SlateBorder)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Icon(Icons.AutoMirrored.Filled.TrendingUp, contentDescription = null, tint = TitanEmerald, modifier = Modifier.size(18.dp))
            Text(
              text = "3. Strategic Tone & Positioning Style",
              style = MaterialTheme.typography.titleSmall,
              color = TextPrimaryDark,
              fontWeight = FontWeight.Bold
            )
          }

          Spacer(modifier = Modifier.height(12.dp))

          Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            CoverLetterTone.values().forEach { tone ->
              val isSelected = tone == selectedTone
              Card(
                modifier = Modifier
                  .fillMaxWidth()
                  .clickable { onToneSelected(tone) }
                  .testTag("tone_chip_${tone.name}"),
                colors = CardDefaults.cardColors(
                  containerColor = if (isSelected) SlateElevated else SlateDarker
                ),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(
                  1.dp,
                  if (isSelected) TitanCyan else SlateBorder
                )
              ) {
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Text(text = tone.badgeEmoji, fontSize = 20.sp)
                    Column {
                      Text(
                        text = tone.displayName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isSelected) TitanCyan else TextPrimaryDark,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.5.sp
                      )
                      Text(
                        text = tone.subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondaryDark,
                        fontSize = 11.sp
                      )
                    }
                  }

                  if (isSelected) {
                    Box(
                      modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(TitanCyan),
                      contentAlignment = Alignment.Center
                    ) {
                      Icon(
                        Icons.Default.Check,
                        contentDescription = null,
                        tint = ObsidianDark,
                        modifier = Modifier.size(14.dp)
                      )
                    }
                  }
                }
              }
            }
          }
        }
      }
    }

    // 4. CUSTOM DIRECTIVES & EMPHASIS
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SlateCard),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, SlateBorder)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Icon(Icons.Default.Lightbulb, contentDescription = null, tint = TitanGold, modifier = Modifier.size(18.dp))
            Text(
              text = "4. Special Focus or Candidate Directives",
              style = MaterialTheme.typography.titleSmall,
              color = TextPrimaryDark,
              fontWeight = FontWeight.Bold
            )
          }

          Spacer(modifier = Modifier.height(8.dp))

          OutlinedTextField(
            value = customDirectives,
            onValueChange = onDirectivesChange,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("input_cover_letter_directives"),
            placeholder = {
              Text(
                "e.g., Emphasize my dark store throughput experience, vendor SLA contracts, and cross-functional leadership of 65 shift leads.",
                color = TextMutedDark,
                fontSize = 11.5.sp
              )
            },
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = TitanCyan,
              unfocusedBorderColor = SlateBorder,
              focusedTextColor = TextPrimaryDark,
              unfocusedTextColor = TextPrimaryDark
            ),
            shape = RoundedCornerShape(8.dp),
            maxLines = 3
          )
        }
      }
    }

    // 5. GENERATE BUTTON
    item {
      Button(
        onClick = onGenerateClick,
        enabled = !isGenerating,
        modifier = Modifier
          .fillMaxWidth()
          .height(52.dp)
          .testTag("btn_generate_cover_letter"),
        colors = ButtonDefaults.buttonColors(
          containerColor = TitanCyan,
          contentColor = ObsidianDark,
          disabledContainerColor = SlateElevated,
          disabledContentColor = TextMutedDark
        ),
        shape = RoundedCornerShape(12.dp)
      ) {
        if (isGenerating) {
          CircularProgressIndicator(
            modifier = Modifier.size(20.dp),
            color = ObsidianDark,
            strokeWidth = 2.dp
          )
          Spacer(modifier = Modifier.width(10.dp))
          Text(
            text = "Gemini is Drafting Cover Letter...",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
          )
        } else {
          Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Draft Personalized Cover Letter with Gemini",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
          )
        }
      }
    }
  }
}

// -----------------------------------------------------------------------------------------
// TAB 1: GENERATED LETTER PREVIEW & POLISH
// -----------------------------------------------------------------------------------------

@Composable
private fun CoverLetterPreviewTab(
  letter: CoverLetterGenerated?,
  isGenerating: Boolean,
  isEditMode: Boolean,
  editableText: String,
  onEditableTextChange: (String) -> Unit,
  onToggleEditMode: () -> Unit,
  onSave: () -> Unit,
  onRegenerate: () -> Unit,
  onToneChange: (CoverLetterTone) -> Unit,
  onCopy: () -> Unit,
  onShare: () -> Unit,
  onBackToBuilder: () -> Unit
) {
  if (letter == null) {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(24.dp),
      contentAlignment = Alignment.Center
    ) {
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        Icon(
          Icons.Default.Description,
          contentDescription = null,
          tint = TextSecondaryDark,
          modifier = Modifier.size(48.dp)
        )
        Text(
          text = "No Cover Letter Generated Yet",
          style = MaterialTheme.typography.titleMedium,
          color = TextPrimaryDark,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = "Select a saved job and candidate profile in the Builder tab to generate your custom cover letter.",
          style = MaterialTheme.typography.bodySmall,
          color = TextSecondaryDark,
          textAlign = TextAlign.Center
        )
        Button(
          onClick = onBackToBuilder,
          colors = ButtonDefaults.buttonColors(containerColor = TitanCyan, contentColor = ObsidianDark),
          shape = RoundedCornerShape(8.dp)
        ) {
          Text("Go to Builder", fontWeight = FontWeight.Bold)
        }
      }
    }
    return
  }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp),
    contentPadding = PaddingValues(top = 16.dp, bottom = 40.dp)
  ) {
    // 1. TOP ACTION CONTROLS
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          // Copy Button
          Button(
            onClick = onCopy,
            colors = ButtonDefaults.buttonColors(containerColor = TitanCyan, contentColor = ObsidianDark),
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
            modifier = Modifier.testTag("btn_copy_cover_letter")
          ) {
            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Copy Letter", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
          }

          // Save Button
          Button(
            onClick = onSave,
            colors = ButtonDefaults.buttonColors(containerColor = SlateElevated, contentColor = TitanGold),
            border = BorderStroke(1.dp, TitanGold.copy(alpha = 0.6f)),
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
            modifier = Modifier.testTag("btn_save_cover_letter")
          ) {
            Icon(Icons.Default.Bookmark, contentDescription = null, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Save to Vault", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
          }

          // Share Button
          IconButton(
            onClick = onShare,
            modifier = Modifier
              .size(36.dp)
              .clip(RoundedCornerShape(8.dp))
              .background(SlateElevated)
          ) {
            Icon(Icons.Default.Share, contentDescription = "Share", tint = TextPrimaryDark, modifier = Modifier.size(16.dp))
          }
        }

        // Toggle Edit Mode
        IconButton(
          onClick = onToggleEditMode,
          modifier = Modifier
            .size(36.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isEditMode) TitanCyan else SlateElevated)
            .testTag("btn_toggle_edit_mode")
        ) {
          Icon(
            if (isEditMode) Icons.Default.Check else Icons.Default.Edit,
            contentDescription = "Edit Text",
            tint = if (isEditMode) ObsidianDark else TextPrimaryDark,
            modifier = Modifier.size(16.dp)
          )
        }
      }
    }

    // 2. STRATEGIC INTELLIGENCE AUDIT CARD
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SlateCard),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, SlateBorder)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = TitanCyan, modifier = Modifier.size(16.dp))
              Text(
                text = "Gemini Strategic Alignment Audit",
                style = MaterialTheme.typography.labelMedium,
                color = TextPrimaryDark,
                fontWeight = FontWeight.Bold
              )
            }

            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(TitanEmerald.copy(alpha = 0.2f))
                .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
              Text(
                text = "${letter.atsMatchScore}% ATS MATCH",
                color = TitanEmerald,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Injected Metrics Badges
          Text(
            text = "Quantified Proof-of-Work Injected:",
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondaryDark,
            fontSize = 11.sp
          )
          Spacer(modifier = Modifier.height(6.dp))

          Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            letter.injectedQuantifiedMetrics.forEach { metric ->
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(6.dp))
                  .background(TitanCyan.copy(alpha = 0.15f))
                  .border(1.dp, TitanCyan.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                  .padding(horizontal = 8.dp, vertical = 3.dp)
              ) {
                Text(
                  text = metric,
                  color = TitanCyan,
                  fontSize = 10.sp,
                  fontWeight = FontWeight.SemiBold
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Key Strategic Hooks
          Text(
            text = "Strategic Pitch Vectors:",
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondaryDark,
            fontSize = 11.sp
          )
          Spacer(modifier = Modifier.height(4.dp))
          letter.keyStrategicHooks.forEach { hook ->
            Row(
              modifier = Modifier.padding(vertical = 2.dp),
              horizontalArrangement = Arrangement.spacedBy(6.dp),
              verticalAlignment = Alignment.Top
            ) {
              Text("•", color = TitanGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
              Text(hook, color = TextPrimaryDark, fontSize = 11.sp)
            }
          }
        }
      }
    }

    // 3. LETTER VIEW OR EDIT MODE
    item {
      if (isEditMode) {
        Column(modifier = Modifier.fillMaxWidth()) {
          Text(
            text = "EDITING MODE (Changes will be saved to your Cover Letter)",
            style = MaterialTheme.typography.labelSmall,
            color = TitanCyan,
            fontWeight = FontWeight.Bold,
            fontSize = 10.5.sp
          )
          Spacer(modifier = Modifier.height(6.dp))
          OutlinedTextField(
            value = editableText,
            onValueChange = onEditableTextChange,
            modifier = Modifier
              .fillMaxWidth()
              .height(420.dp)
              .testTag("input_edit_cover_letter"),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = TitanCyan,
              unfocusedBorderColor = SlateBorder,
              focusedTextColor = TextPrimaryDark,
              unfocusedTextColor = TextPrimaryDark
            ),
            textStyle = MaterialTheme.typography.bodySmall.copy(
              fontFamily = FontFamily.Monospace,
              lineHeight = 20.sp,
              fontSize = 12.sp
            ),
            shape = RoundedCornerShape(10.dp)
          )
        }
      } else {
        // Executive Formal Business Letter Layout Card
        Card(
          modifier = Modifier.fillMaxWidth(),
          colors = CardDefaults.cardColors(containerColor = SlateDarker),
          shape = RoundedCornerShape(12.dp),
          border = BorderStroke(1.dp, SlateBorder)
        ) {
          Column(modifier = Modifier.padding(20.dp)) {
            // Document Header
            Text(
              text = letter.candidateName,
              style = MaterialTheme.typography.titleMedium,
              color = TextPrimaryDark,
              fontWeight = FontWeight.Bold,
              fontSize = 18.sp
            )
            Text(
              text = letter.candidateContact,
              style = MaterialTheme.typography.bodySmall,
              color = TextSecondaryDark,
              fontSize = 11.sp
            )

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = SlateBorder, thickness = 1.dp)
            Spacer(modifier = Modifier.height(14.dp))

            // Date & Recipient
            Text(
              text = letter.dateFormatted,
              style = MaterialTheme.typography.bodySmall,
              color = TextSecondaryDark,
              fontSize = 11.5.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = letter.recipientName,
              style = MaterialTheme.typography.bodyMedium,
              color = TextPrimaryDark,
              fontWeight = FontWeight.SemiBold
            )
            Text(
              text = letter.targetCompany,
              style = MaterialTheme.typography.bodySmall,
              color = TextSecondaryDark
            )
            Text(
              text = letter.companyAddress,
              style = MaterialTheme.typography.bodySmall,
              color = TextMutedDark,
              fontSize = 11.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Subject
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(6.dp))
                .background(SlateElevated)
                .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
              Text(
                text = "SUBJECT: ${letter.subjectLine}",
                style = MaterialTheme.typography.bodyMedium,
                color = TitanCyan,
                fontWeight = FontWeight.Bold,
                fontSize = 12.5.sp
              )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Salutation
            Text(
              text = letter.salutation,
              style = MaterialTheme.typography.bodyMedium,
              color = TextPrimaryDark,
              fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Opening Hook
            Text(
              text = letter.openingHook,
              style = MaterialTheme.typography.bodyMedium,
              color = TextPrimaryDark,
              lineHeight = 22.sp,
              fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Impact Paragraph
            Text(
              text = letter.bodyParagraphImpact,
              style = MaterialTheme.typography.bodyMedium,
              color = TextPrimaryDark,
              lineHeight = 22.sp,
              fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Skills & Architecture Paragraph
            Text(
              text = letter.bodyParagraphSkills,
              style = MaterialTheme.typography.bodyMedium,
              color = TextPrimaryDark,
              lineHeight = 22.sp,
              fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Culture & Strategic Moat Paragraph
            Text(
              text = letter.bodyParagraphCultureMoat,
              style = MaterialTheme.typography.bodyMedium,
              color = TextPrimaryDark,
              lineHeight = 22.sp,
              fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Call to Action
            Text(
              text = letter.callToAction,
              style = MaterialTheme.typography.bodyMedium,
              color = TextPrimaryDark,
              lineHeight = 22.sp,
              fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Sign-off
            Text(
              text = letter.formalSignOff,
              style = MaterialTheme.typography.bodyMedium,
              color = TextPrimaryDark
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
              text = letter.candidateName,
              style = MaterialTheme.typography.bodyMedium,
              color = TextPrimaryDark,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = letter.candidateTitle,
              style = MaterialTheme.typography.bodySmall,
              color = TextSecondaryDark
            )
          }
        }
      }
    }

    // 4. QUICK REGENERATE WITH ALTERNATE TONE
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SlateCard),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, SlateBorder)
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "Adjust Tone & Regenerate",
              style = MaterialTheme.typography.labelSmall,
              color = TextSecondaryDark,
              fontWeight = FontWeight.Bold
            )

            Button(
              onClick = onRegenerate,
              enabled = !isGenerating,
              colors = ButtonDefaults.buttonColors(containerColor = SlateElevated, contentColor = TitanCyan),
              shape = RoundedCornerShape(6.dp),
              contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
              modifier = Modifier.testTag("btn_regenerate_cover_letter")
            ) {
              Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(12.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Regenerate", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            CoverLetterTone.values().forEach { t ->
              val isCurrent = t == letter.tone
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(6.dp))
                  .background(if (isCurrent) TitanCyan.copy(alpha = 0.2f) else SlateDarker)
                  .border(1.dp, if (isCurrent) TitanCyan else SlateBorder, RoundedCornerShape(6.dp))
                  .clickable { onToneChange(t) }
                  .padding(horizontal = 8.dp, vertical = 4.dp)
              ) {
                Text(
                  text = "${t.badgeEmoji} ${t.displayName}",
                  color = if (isCurrent) TitanCyan else TextPrimaryDark,
                  fontSize = 10.5.sp,
                  fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal
                )
              }
            }
          }
        }
      }
    }
  }
}

// -----------------------------------------------------------------------------------------
// TAB 2: SAVED LETTERS VAULT
// -----------------------------------------------------------------------------------------

@Composable
private fun CoverLetterSavedVaultTab(
  savedLetters: List<CoverLetterGenerated>,
  onSelectLetter: (CoverLetterGenerated) -> Unit,
  onDeleteLetter: (String) -> Unit,
  onCopyLetter: (String) -> Unit,
  onCreateNew: () -> Unit
) {
  if (savedLetters.isEmpty()) {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(24.dp),
      contentAlignment = Alignment.Center
    ) {
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        Icon(
          Icons.Default.FolderSpecial,
          contentDescription = null,
          tint = TextSecondaryDark,
          modifier = Modifier.size(48.dp)
        )
        Text(
          text = "No Saved Cover Letters",
          style = MaterialTheme.typography.titleMedium,
          color = TextPrimaryDark,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = "Generate and save tailored cover letters for each company to access them offline anytime.",
          style = MaterialTheme.typography.bodySmall,
          color = TextSecondaryDark,
          textAlign = TextAlign.Center
        )
        Button(
          onClick = onCreateNew,
          colors = ButtonDefaults.buttonColors(containerColor = TitanCyan, contentColor = ObsidianDark),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier.testTag("btn_vault_create_new")
        ) {
          Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Draft New Cover Letter", fontWeight = FontWeight.Bold)
        }
      }
    }
    return
  }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp),
    contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp)
  ) {
    items(savedLetters, key = { it.id }) { item ->
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .clickable { onSelectLetter(item) }
          .testTag("saved_letter_card_${item.id}"),
        colors = CardDefaults.cardColors(containerColor = SlateCard),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, SlateBorder)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                Text(
                  text = item.targetCompany,
                  style = MaterialTheme.typography.titleSmall,
                  color = TitanCyan,
                  fontWeight = FontWeight.Bold,
                  fontSize = 15.sp
                )
                Text(
                  text = "• ${item.tone.displayName}",
                  style = MaterialTheme.typography.labelSmall,
                  color = TextMutedDark,
                  fontSize = 11.sp
                )
              }
              Text(
                text = item.targetRole,
                style = MaterialTheme.typography.bodySmall,
                color = TextPrimaryDark,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
            }

            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(TitanEmerald.copy(alpha = 0.2f))
                .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
              Text(
                text = "${item.atsMatchScore}% ATS",
                color = TitanEmerald,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          Text(
            text = item.openingHook,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondaryDark,
            fontSize = 11.5.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            lineHeight = 16.sp
          )

          Spacer(modifier = Modifier.height(10.dp))
          HorizontalDivider(color = SlateBorder, thickness = 0.8.dp)
          Spacer(modifier = Modifier.height(8.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = item.dateFormatted,
              style = MaterialTheme.typography.labelSmall,
              color = TextMutedDark,
              fontSize = 10.sp
            )

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              IconButton(
                onClick = { onCopyLetter(item.fullFormattedLetter) },
                modifier = Modifier.size(32.dp)
              ) {
                Icon(
                  Icons.Default.ContentCopy,
                  contentDescription = "Copy",
                  tint = TitanCyan,
                  modifier = Modifier.size(16.dp)
                )
              }

              IconButton(
                onClick = { onDeleteLetter(item.id) },
                modifier = Modifier.size(32.dp)
              ) {
                Icon(
                  Icons.Default.Delete,
                  contentDescription = "Delete",
                  tint = Color(0xFFEF4444),
                  modifier = Modifier.size(16.dp)
                )
              }

              Button(
                onClick = { onSelectLetter(item) },
                colors = ButtonDefaults.buttonColors(containerColor = SlateElevated, contentColor = TextPrimaryDark),
                shape = RoundedCornerShape(6.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
              ) {
                Text("Open", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(4.dp))
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(12.dp))
              }
            }
          }
        }
      }
    }
  }
}
