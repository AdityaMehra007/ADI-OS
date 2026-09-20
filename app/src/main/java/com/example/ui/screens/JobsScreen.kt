package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.automirrored.filled.FactCheck
import com.example.ui.components.JobDescriptionAiAnalyzerComponent
import com.example.ui.components.ResumeJobComparisonView
import com.example.ui.components.MultiPlatformSyncCard
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Work
import com.example.service.JobDiscoveryLogItem
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AutomatedJobMatch
import com.example.data.model.AutomationCriteria
import com.example.data.model.AutomationQueueStatus
import com.example.data.model.Job
import com.example.ui.components.MarketIntelligenceView
import com.example.ui.components.SmartJobSearchView
import com.example.ui.components.VoiceCommandCenterCard
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
fun JobsScreen(
  viewModel: TitanViewModel
) {
  val jobs by viewModel.jobs.collectAsState()
  val activeTab by viewModel.jobsScreenActiveTab.collectAsState()
  val searchQuery by viewModel.searchQuery.collectAsState()
  val selectedTierFilter by viewModel.selectedTierFilter.collectAsState()
  val onlyFresherFriendly by viewModel.onlyFresherFriendly.collectAsState()

  val selectedJob by viewModel.selectedJob.collectAsState()
  val tailoredResumePreview by viewModel.tailoredResumePreview.collectAsState()
  val isGeneratingResume by viewModel.isGeneratingResume.collectAsState()

  var showAddJobDialog by remember { mutableStateOf(false) }
  var newJobTitle by remember { mutableStateOf("") }
  var newJobCompany by remember { mutableStateOf("") }
  var newJobLocation by remember { mutableStateOf("Bengaluru, India") }
  var newJobSalary by remember { mutableStateOf("₹14L - ₹20L") }
  var newJobRoleFamily by remember { mutableStateOf("STRATEGY_OPS") }
  var newJobUrl by remember { mutableStateOf("https://careers.google.com") }
  var newJobIsFresher by remember { mutableStateOf(true) }

  val filteredJobs = jobs.filter { job ->
    val matchesSearch = searchQuery.isBlank() ||
      job.title.contains(searchQuery, ignoreCase = true) ||
      job.companyName.contains(searchQuery, ignoreCase = true) ||
      job.roleFamily.contains(searchQuery, ignoreCase = true) ||
      job.location.contains(searchQuery, ignoreCase = true)

    val matchesFresher = !onlyFresherFriendly || job.isFresherFriendly

    matchesSearch && matchesFresher
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(ObsidianDark)
      .padding(horizontal = 16.dp, vertical = 8.dp)
      .testTag("jobs_screen")
  ) {
    // Header & Mode Indicator
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Text(
          text = "OPPORTUNITY INTELLIGENCE",
          style = MaterialTheme.typography.labelSmall,
          color = TitanCyan,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = when (activeTab) {
            0 -> "Live Opportunity Radar"
            1 -> "Smart Job Search (Gemini Crawler)"
            2 -> "Job Automation Command Center"
            3 -> "Market Intelligence"
            else -> "Resume vs. Job Requirements Comparator"
          },
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
          color = TextPrimaryDark
        )
      }

      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(6.dp))
          .background(TitanEmerald.copy(alpha = 0.12f))
          .border(1.dp, TitanEmerald.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
          .padding(horizontal = 8.dp, vertical = 4.dp)
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.Bolt,
            contentDescription = null,
            tint = TitanEmerald,
            modifier = Modifier.size(12.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "AUTOPILOT READY",
            style = MaterialTheme.typography.labelSmall,
            color = TitanEmerald,
            fontSize = 9.sp,
            fontWeight = FontWeight.Black
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Navigation TabRow
    TabRow(
      selectedTabIndex = activeTab,
      containerColor = SlateCard,
      contentColor = TitanCyan,
      indicator = { tabPositions ->
        TabRowDefaults.SecondaryIndicator(
          Modifier.tabIndicatorOffset(tabPositions[activeTab]),
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
        selected = activeTab == 0,
        onClick = { viewModel.setJobsScreenTab(0) },
        text = {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Radar, contentDescription = null, modifier = Modifier.size(13.dp), tint = if (activeTab == 0) TitanCyan else TextMutedDark)
            Spacer(modifier = Modifier.width(3.dp))
            Text(
              text = "Radar (${jobs.size})",
              style = MaterialTheme.typography.labelSmall,
              fontWeight = if (activeTab == 0) FontWeight.Bold else FontWeight.Normal,
              color = if (activeTab == 0) TitanCyan else TextMutedDark,
              fontSize = 11.sp
            )
          }
        }
      )

      Tab(
        selected = activeTab == 1,
        onClick = { viewModel.setJobsScreenTab(1) },
        text = {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              Icons.Default.AutoAwesome,
              contentDescription = null,
              modifier = Modifier.size(13.dp),
              tint = if (activeTab == 1) TitanCyan else TextMutedDark
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text(
              text = "Smart Search",
              style = MaterialTheme.typography.labelSmall,
              fontWeight = if (activeTab == 1) FontWeight.Bold else FontWeight.Normal,
              color = if (activeTab == 1) TitanCyan else TextMutedDark,
              fontSize = 11.sp
            )
          }
        }
      )

      Tab(
        selected = activeTab == 2,
        onClick = { viewModel.setJobsScreenTab(2) },
        text = {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              Icons.Default.Bolt,
              contentDescription = null,
              modifier = Modifier.size(13.dp),
              tint = if (activeTab == 2) TitanGold else TextMutedDark
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text(
              text = "Automation",
              style = MaterialTheme.typography.labelSmall,
              fontWeight = if (activeTab == 2) FontWeight.Bold else FontWeight.Normal,
              color = if (activeTab == 2) TitanGold else TextMutedDark,
              fontSize = 11.sp
            )
          }
        }
      )

      Tab(
        selected = activeTab == 3,
        onClick = { viewModel.setJobsScreenTab(3) },
        text = {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              Icons.Default.Insights,
              contentDescription = null,
              modifier = Modifier.size(13.dp),
              tint = if (activeTab == 3) TitanEmerald else TextMutedDark
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text(
              text = "Market Intel",
              style = MaterialTheme.typography.labelSmall,
              fontWeight = if (activeTab == 3) FontWeight.Bold else FontWeight.Normal,
              color = if (activeTab == 3) TitanEmerald else TextMutedDark,
              fontSize = 11.sp
            )
          }
        }
      )

      Tab(
        selected = activeTab == 4,
        onClick = { viewModel.setJobsScreenTab(4) },
        text = {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              Icons.AutoMirrored.Filled.FactCheck,
              contentDescription = null,
              modifier = Modifier.size(13.dp),
              tint = if (activeTab == 4) TitanCyan else TextMutedDark
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text(
              text = "Resume Matcher",
              style = MaterialTheme.typography.labelSmall,
              fontWeight = if (activeTab == 4) FontWeight.Bold else FontWeight.Normal,
              color = if (activeTab == 4) TitanCyan else TextMutedDark,
              fontSize = 11.sp
            )
          }
        }
      )
    }

    Spacer(modifier = Modifier.height(12.dp))

    when (activeTab) {
      0 -> {
        // TAB 0: LIVE OPPORTUNITY RADAR
        OpportunityRadarView(
          jobs = filteredJobs,
          totalCount = jobs.size,
          searchQuery = searchQuery,
          onlyFresherFriendly = onlyFresherFriendly,
          selectedTierFilter = selectedTierFilter,
          viewModel = viewModel,
          onSearchQueryChange = { viewModel.searchQuery.value = it },
          onToggleFresher = { viewModel.onlyFresherFriendly.value = !onlyFresherFriendly },
          onOpenAddDialog = { showAddJobDialog = true },
          onOpenVoiceHud = { viewModel.setVoiceHudOpen(true) },
          onPrepareApply = { job -> viewModel.prepareApplication(job) },
          onActionPlan = { job -> viewModel.openJobActionPlan(job = job) },
          onCoverLetter = { job -> viewModel.openCoverLetterGenerator(job = job) },
          onMatchResume = { job ->
            val jobReqs = buildString {
              appendLine("Role: ${job.title} at ${job.companyName} (${job.location})")
              appendLine("Department: ${job.department} | Experience: ${job.experienceRequirement}")
              if (job.whyItFits.isNotBlank()) {
                appendLine("Key Qualifications & Fit Criteria:")
                appendLine(job.whyItFits)
              }
              if (job.missingRequirements.isNotBlank()) {
                appendLine("Core Demanded Skills & Prerequisites:")
                appendLine(job.missingRequirements)
              }
            }
            viewModel.openResumeComparison(
              targetCompany = job.companyName,
              role = job.title,
              reqs = jobReqs.ifBlank { job.whyItFits }
            )
          },
          onOpenActionPlanDialog = { viewModel.openJobActionPlan() },
          onOpenCoverLetterDialog = { viewModel.openCoverLetterGenerator() },
          onOpenMatchResumeDialog = { viewModel.openResumeComparison() },
          onNegotiate = { job -> viewModel.openSalaryNegotiationDialog(targetJob = job) },
          onSwitchToAutomation = { viewModel.setJobsScreenTab(2) },
          onSwitchToSmartSearch = { viewModel.setJobsScreenTab(1) }
        )
      }
      1 -> {
        // TAB 1: SMART JOB SEARCH (GEMINI TARGET COMPANY CRAWLER & PERSISTENCE)
        SmartJobSearchView(
          viewModel = viewModel,
          onNavigateToPipeline = { viewModel.navigateTo(TitanScreen.APPLICATIONS) }
        )
      }
      2 -> {
        // TAB 2: JOB AUTOMATION DASHBOARD & COMMAND CENTER
        JobAutomationDashboardView(
          viewModel = viewModel,
          onNavigateToJobsRadar = { viewModel.setJobsScreenTab(0) },
          onNavigateToCompanyIntel = { companyName ->
            viewModel.selectCompanyForIntel(companyName)
            viewModel.navigateTo(TitanScreen.COMPANIES)
          }
        )
      }
      3 -> {
        // TAB 3: MARKET INTELLIGENCE (SALARY & INDUSTRY TRENDS)
        MarketIntelligenceView(viewModel = viewModel)
      }
      else -> {
        // TAB 4: RESUME VS JOB REQUIREMENTS COMPARATOR
        ResumeJobComparisonView(viewModel = viewModel)
      }
    }
  }

  // Live Tailored Resume & 1-Tap Application Modal
  if (selectedJob != null) {
    AlertDialog(
      onDismissRequest = { viewModel.selectedJob.value = null },
      containerColor = SlateElevated,
      title = {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = TitanCyan, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("AI Tailored Application Engine", style = MaterialTheme.typography.titleMedium, color = TextPrimaryDark)
          }
          IconButton(onClick = { viewModel.selectedJob.value = null }) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondaryDark)
          }
        }
      },
      text = {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
        ) {
          Text(
            text = "Target: ${selectedJob?.title} at ${selectedJob?.companyName}",
            style = MaterialTheme.typography.labelSmall,
            color = TitanGold,
            fontWeight = FontWeight.Bold
          )

          Spacer(modifier = Modifier.height(8.dp))

          if (isGeneratingResume) {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
              contentAlignment = Alignment.Center
            ) {
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator(color = TitanCyan, modifier = Modifier.size(32.dp))
                Spacer(modifier = Modifier.height(12.dp))
                Text("Synthesizing ATS Resume from Verified Candidate Fact Store...", style = MaterialTheme.typography.bodySmall, color = TextSecondaryDark)
              }
            }
          } else {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(SlateCard)
                .border(1.dp, SlateBorder, RoundedCornerShape(8.dp))
                .padding(10.dp)
            ) {
              LazyColumn {
                item {
                  Text(
                    text = tailoredResumePreview ?: "Generating tailored profile...",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextPrimaryDark,
                    fontSize = 11.sp,
                    lineHeight = 16.sp
                  )
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          Text(
            text = "✓ Fact-Checked: 100% verified against verified facts. 0% fabrication.",
            style = MaterialTheme.typography.labelSmall,
            color = TitanEmerald,
            fontSize = 10.sp
          )
        }
      },
      confirmButton = {
        Button(
          onClick = { selectedJob?.let { viewModel.submitApplication(it) } },
          colors = ButtonDefaults.buttonColors(containerColor = TitanCyan, contentColor = ObsidianDark),
          shape = RoundedCornerShape(8.dp),
          enabled = !isGeneratingResume,
          modifier = Modifier.testTag("confirm_apply_button")
        ) {
          Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Submit & Log to CRM", fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { viewModel.selectedJob.value = null }) {
          Text("Cancel", color = TextSecondaryDark)
        }
      }
    )
  }

  // Custom Opportunity Creation Dialog
  if (showAddJobDialog) {
    AlertDialog(
      onDismissRequest = { showAddJobDialog = false },
      containerColor = SlateElevated,
      title = {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Work, contentDescription = null, tint = TitanCyan, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Add Custom Opportunity", style = MaterialTheme.typography.titleMedium, color = TextPrimaryDark)
          }
          IconButton(onClick = { showAddJobDialog = false }) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondaryDark)
          }
        }
      },
      text = {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          OutlinedTextField(
            value = newJobCompany,
            onValueChange = { newJobCompany = it },
            label = { Text("Company Name (e.g. Google, McKinsey)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = TitanCyan,
              unfocusedBorderColor = SlateBorder,
              focusedTextColor = TextPrimaryDark,
              unfocusedTextColor = TextPrimaryDark
            )
          )

          OutlinedTextField(
            value = newJobTitle,
            onValueChange = { newJobTitle = it },
            label = { Text("Role Title (e.g. Associate Strategy Analyst)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = TitanCyan,
              unfocusedBorderColor = SlateBorder,
              focusedTextColor = TextPrimaryDark,
              unfocusedTextColor = TextPrimaryDark
            )
          )

          OutlinedTextField(
            value = newJobSalary,
            onValueChange = { newJobSalary = it },
            label = { Text("Salary Band (e.g. ₹16L - ₹22L)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = TitanCyan,
              unfocusedBorderColor = SlateBorder,
              focusedTextColor = TextPrimaryDark,
              unfocusedTextColor = TextPrimaryDark
            )
          )

          OutlinedTextField(
            value = newJobLocation,
            onValueChange = { newJobLocation = it },
            label = { Text("Location (e.g. Outer Ring Road, Bengaluru)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = TitanCyan,
              unfocusedBorderColor = SlateBorder,
              focusedTextColor = TextPrimaryDark,
              unfocusedTextColor = TextPrimaryDark
            )
          )

          OutlinedTextField(
            value = newJobUrl,
            onValueChange = { newJobUrl = it },
            label = { Text("Careers Portal / Application URL") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
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
            if (newJobTitle.isNotBlank() && newJobCompany.isNotBlank()) {
              val newJob = Job(
                id = "custom_job_${System.currentTimeMillis()}",
                companyId = "comp_${newJobCompany.lowercase().replace(" ", "_")}",
                companyName = newJobCompany.trim(),
                title = newJobTitle.trim(),
                roleFamily = newJobRoleFamily,
                department = "Strategy & Business Operations",
                location = newJobLocation.trim(),
                city = "Bengaluru",
                country = "India",
                remoteStatus = "HYBRID",
                employmentType = "FULL_TIME",
                salaryRange = newJobSalary.trim(),
                currency = "INR",
                experienceRequirement = "0-2 Years",
                adiFitScore = 95,
                opportunityScore = 92,
                whyItFits = "Matched against candidate verified credentials & strategic problem-solving track record.",
                whyItDoesntFit = "Requires domain specific ramp-up during initial onboarding.",
                missingRequirements = "None (Fully qualified for early career tier)",
                recommendedAction = "Apply immediately & trigger warm-path outreach.",
                applicationUrl = newJobUrl.trim(),
                officialSource = "Official Direct Sourcing",
                sourceQuality = "OFFICIAL_CAREERS_PORTAL",
                postedDate = "2026-09-01",
                isFresherFriendly = newJobIsFresher,
                strategicPriority = "APPLY_NOW",
                status = "ACTIVE"
              )
              viewModel.addNewJob(newJob)
              showAddJobDialog = false
              newJobTitle = ""
              newJobCompany = ""
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = TitanCyan, contentColor = ObsidianDark),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier.testTag("save_custom_job_button")
        ) {
          Text("Add to Radar", fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { showAddJobDialog = false }) {
          Text("Cancel", color = TextSecondaryDark)
        }
      }
    )
  }
}

@Composable
fun OpportunityRadarView(
  jobs: List<Job>,
  totalCount: Int,
  searchQuery: String,
  onlyFresherFriendly: Boolean,
  selectedTierFilter: String,
  viewModel: TitanViewModel? = null,
  onSearchQueryChange: (String) -> Unit,
  onToggleFresher: () -> Unit,
  onOpenAddDialog: () -> Unit,
  onOpenVoiceHud: () -> Unit,
  onPrepareApply: (Job) -> Unit,
  onActionPlan: (Job) -> Unit,
  onCoverLetter: (Job) -> Unit = {},
  onMatchResume: (Job) -> Unit = {},
  onNegotiate: (Job) -> Unit = {},
  onOpenActionPlanDialog: () -> Unit,
  onOpenCoverLetterDialog: () -> Unit = {},
  onOpenMatchResumeDialog: () -> Unit = {},
  onSwitchToAutomation: () -> Unit,
  onSwitchToSmartSearch: () -> Unit = {}
) {
  val showJdAnalyzer = viewModel?.showJobDescriptionAnalyzer?.collectAsState()?.value ?: false

  Column(modifier = Modifier.fillMaxSize()) {
    // Autonomous Background Radar Service Status Pill
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(8.dp))
        .background(SlateElevated)
        .border(0.5.dp, TitanCyan.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
        .clickable { onSwitchToAutomation() }
        .padding(horizontal = 10.dp, vertical = 6.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier = Modifier
            .size(7.dp)
            .clip(CircleShape)
            .background(TitanEmerald)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "Background Auto-Radar Active ($totalCount opportunities in local DB)",
          style = MaterialTheme.typography.labelSmall,
          color = TextSecondaryDark,
          fontSize = 10.sp
        )
      }

      Text(
        text = "Command Center →",
        style = MaterialTheme.typography.labelSmall,
        color = TitanCyan,
        fontWeight = FontWeight.Bold,
        fontSize = 10.sp
      )
    }

    if (viewModel != null) {
      Spacer(modifier = Modifier.height(6.dp))
      MultiPlatformSyncCard(
        viewModel = viewModel,
        initialExpanded = false
      )
    }

    Spacer(modifier = Modifier.height(6.dp))

    // Gemini Smart Job Crawler Launch Banner
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(8.dp))
        .background(TitanCyan.copy(alpha = 0.08f))
        .border(1.dp, TitanCyan.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
        .clickable { onSwitchToSmartSearch() }
        .padding(horizontal = 10.dp, vertical = 7.dp)
        .testTag("launch_smart_search_banner"),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
        Icon(
          imageVector = Icons.Default.AutoAwesome,
          contentDescription = null,
          tint = TitanCyan,
          modifier = Modifier.size(15.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "Gemini Smart Job Crawler: Auto-crawl & match target companies",
          style = MaterialTheme.typography.labelSmall,
          color = TitanCyan,
          fontWeight = FontWeight.SemiBold,
          fontSize = 11.sp
        )
      }

      Text(
        text = "Launch →",
        style = MaterialTheme.typography.labelSmall,
        color = TitanCyan,
        fontWeight = FontWeight.Bold,
        fontSize = 10.sp
      )
    }

    Spacer(modifier = Modifier.height(8.dp))

    // Search Bar with Voice Microphone Action
    OutlinedTextField(
      value = searchQuery,
      onValueChange = { onSearchQueryChange(it) },
      modifier = Modifier
        .fillMaxWidth()
        .testTag("job_search_input"),
      placeholder = {
        Text("Natural Language Job Search (e.g. 'Business Analyst Bengaluru', 'S+ Tier')...", color = TextMutedDark, fontSize = 12.sp)
      },
      leadingIcon = {
        Icon(Icons.Default.Search, contentDescription = "Search", tint = TitanCyan, modifier = Modifier.size(18.dp))
      },
      trailingIcon = {
        Row(verticalAlignment = Alignment.CenterVertically) {
          if (searchQuery.isNotEmpty()) {
            IconButton(onClick = { onSearchQueryChange("") }) {
              Icon(Icons.Default.Close, contentDescription = "Clear", tint = TextSecondaryDark, modifier = Modifier.size(16.dp))
            }
          }
          IconButton(
            onClick = onOpenVoiceHud,
            modifier = Modifier.testTag("search_voice_mic_btn")
          ) {
            Icon(Icons.Default.Mic, contentDescription = "Voice Search", tint = TitanCyan, modifier = Modifier.size(18.dp))
          }
        }
      },
      singleLine = true,
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

    // Filter Chips & Add Action
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .horizontalScroll(rememberScrollState()),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Button(
        onClick = { onOpenAddDialog() },
        colors = ButtonDefaults.buttonColors(containerColor = TitanCyan, contentColor = ObsidianDark),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.testTag("add_job_button")
      ) {
        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text("Add Opp", fontSize = 11.sp, fontWeight = FontWeight.Bold)
      }

      Button(
        onClick = { onOpenActionPlanDialog() },
        colors = ButtonDefaults.buttonColors(containerColor = TitanIndigo, contentColor = TextPrimaryDark),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.testTag("ai_jd_action_plan_button")
      ) {
        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = TitanCyan, modifier = Modifier.size(14.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text("JD Action Plan", fontSize = 11.sp, fontWeight = FontWeight.Bold)
      }

      Button(
        onClick = { onOpenCoverLetterDialog() },
        colors = ButtonDefaults.buttonColors(containerColor = SlateElevated, contentColor = TitanGold),
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, TitanGold.copy(alpha = 0.6f)),
        modifier = Modifier.testTag("cover_letter_generator_button")
      ) {
        Icon(Icons.Default.Description, contentDescription = null, tint = TitanGold, modifier = Modifier.size(14.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text("Cover Letter", fontSize = 11.sp, fontWeight = FontWeight.Bold)
      }

      Button(
        onClick = { onOpenMatchResumeDialog() },
        colors = ButtonDefaults.buttonColors(containerColor = SlateElevated, contentColor = TitanEmerald),
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, TitanEmerald.copy(alpha = 0.6f)),
        modifier = Modifier.testTag("resume_match_score_button")
      ) {
        Icon(Icons.AutoMirrored.Filled.FactCheck, contentDescription = null, tint = TitanEmerald, modifier = Modifier.size(14.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text("Match Resume", fontSize = 11.sp, fontWeight = FontWeight.Bold)
      }

      if (viewModel != null) {
        Button(
          onClick = { viewModel.showJobDescriptionAnalyzer.value = !showJdAnalyzer },
          colors = ButtonDefaults.buttonColors(
            containerColor = if (showJdAnalyzer) TitanCyan else SlateElevated,
            contentColor = if (showJdAnalyzer) ObsidianDark else TitanCyan
          ),
          shape = RoundedCornerShape(8.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, TitanCyan.copy(alpha = 0.6f)),
          modifier = Modifier.testTag("toggle_paste_jd_analyzer_btn")
        ) {
          Icon(
            Icons.Default.AutoAwesome,
            contentDescription = null,
            tint = if (showJdAnalyzer) ObsidianDark else TitanCyan,
            modifier = Modifier.size(14.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = if (showJdAnalyzer) "Hide JD Analyzer" else "Paste & Analyze JD",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
          )
        }

        Button(
          onClick = {
            viewModel.toggleSplitViewJdComparisonMode(true)
          },
          colors = ButtonDefaults.buttonColors(
            containerColor = SlateElevated,
            contentColor = TitanCyan
          ),
          shape = RoundedCornerShape(8.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, TitanCyan.copy(alpha = 0.6f)),
          modifier = Modifier.testTag("open_split_view_comparison_btn")
        ) {
          Icon(
            Icons.AutoMirrored.Filled.CompareArrows,
            contentDescription = null,
            tint = TitanCyan,
            modifier = Modifier.size(14.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "Split-View JDs",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }

      FilterChip(
        label = "All ($totalCount)",
        isSelected = !onlyFresherFriendly,
        onClick = {
          if (onlyFresherFriendly) onToggleFresher()
        }
      )

      FilterChip(
        label = "🎓 0-2 Yrs Fresher",
        isSelected = onlyFresherFriendly,
        onClick = { onToggleFresher() }
      )

      FilterChip(
        label = "⚡ Auto-Batch Apply",
        isSelected = false,
        onClick = { onSwitchToAutomation() }
      )
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Quick Prompt Banner when analyzer is closed
    if (viewModel != null && !showJdAnalyzer) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(8.dp))
          .background(TitanIndigo.copy(alpha = 0.12f))
          .border(0.5.dp, TitanCyan.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
          .clickable { viewModel.showJobDescriptionAnalyzer.value = true }
          .padding(horizontal = 10.dp, vertical = 7.dp)
          .testTag("expand_jd_analyzer_prompt_banner"),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
          Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = TitanCyan, modifier = Modifier.size(14.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Have an external JD? Paste & run instant Gemini AI Analysis",
            fontSize = 11.sp,
            color = TitanCyan,
            fontWeight = FontWeight.SemiBold
          )
        }
        Text("Analyze JD →", fontSize = 10.sp, color = TitanCyan, fontWeight = FontWeight.Bold)
      }
      Spacer(modifier = Modifier.height(6.dp))
    }

    // Jobs List
    LazyColumn(
      modifier = Modifier.fillMaxSize(),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      // Pasted Job Description AI Analyzer Component
      if (viewModel != null && showJdAnalyzer) {
        item(key = "pasted_jd_ai_analyzer_component") {
          JobDescriptionAiAnalyzerComponent(
            viewModel = viewModel,
            onNavigateToResumeMatcher = { company, role, reqs ->
              viewModel.openResumeComparison(company, role, reqs)
            },
            onOpenActionPlanDialog = {
              viewModel.openJobActionPlan()
            },
            modifier = Modifier.padding(bottom = 6.dp)
          )
        }
      }

      items(jobs, key = { it.id }) { job ->
        JobItemCard(
          job = job,
          onPrepareApply = { onPrepareApply(job) },
          onActionPlan = { onActionPlan(job) },
          onCoverLetter = { onCoverLetter(job) },
          onMatchResume = { onMatchResume(job) },
          onNegotiate = { onNegotiate(job) }
        )
      }

      item {
        Spacer(modifier = Modifier.height(16.dp))
      }
    }
  }
}

@Composable
fun JobAutomationDashboardView(
  viewModel: TitanViewModel,
  onNavigateToJobsRadar: () -> Unit = {},
  onNavigateToCompanyIntel: (String) -> Unit = {}
) {
  val criteria by viewModel.automationCriteria.collectAsState()
  val queue by viewModel.automationQueue.collectAsState()
  val isRunning by viewModel.isAutomationRunning.collectAsState()
  val lastRun by viewModel.lastAutomationRunTimestamp.collectAsState()
  val appsCount by viewModel.automatedApplicationsCount.collectAsState()
  val successRate by viewModel.automationSuccessRate.collectAsState()

  // Background Job Discovery Automation States
  val isServiceRunning by viewModel.isJobDiscoveryServiceRunning.collectAsState()
  val lastPullTimestamp by viewModel.jobDiscoveryLastPullTimestamp.collectAsState()
  val lastPulledCount by viewModel.jobDiscoveryLastPulledCount.collectAsState()
  val totalStoredFromAutomation by viewModel.jobDiscoveryTotalCount.collectAsState()
  val isPeriodicEnabled by viewModel.jobDiscoveryIsSchedulerEnabled.collectAsState()
  val periodicIntervalMinutes by viewModel.jobDiscoveryIntervalMinutes.collectAsState()
  val discoveryLogs by viewModel.jobDiscoveryLogs.collectAsState()
  val activeGoalsSummary by viewModel.jobDiscoveryActiveGoalsSummary.collectAsState()
  val targetCompaniesCount by viewModel.jobDiscoveryTargetCompaniesCount.collectAsState()

  var showCriteriaDialog by remember { mutableStateOf(false) }

  val readyMatches = queue.filter { it.automationStatus == AutomationQueueStatus.READY_FOR_APPROVAL }
  val submittedMatches = queue.filter { it.automationStatus == AutomationQueueStatus.SUBMITTED }

  LazyColumn(
    modifier = Modifier.fillMaxSize(),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // 0. Voice Command Center & Audio Prompt Interface
    item {
      VoiceCommandCenterCard(
        viewModel = viewModel,
        onNavigateToJobsRadar = onNavigateToJobsRadar,
        onNavigateToCompanyIntel = onNavigateToCompanyIntel
      )
    }

    // 1. Executive Telemetry & Stats Banner
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
            Column {
              Text(
                text = "JOB AUTOMATION COMMAND HUB",
                style = MaterialTheme.typography.labelSmall,
                color = TitanCyan,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp
              )
              Text(
                text = "Multi-Vector Sourcing & Auto-Apply Pipeline",
                style = MaterialTheme.typography.titleSmall,
                color = TextPrimaryDark,
                fontWeight = FontWeight.Bold
              )
            }

            IconButton(
              onClick = { showCriteriaDialog = true },
              modifier = Modifier
                .size(34.dp)
                .background(SlateElevated, CircleShape)
            ) {
              Icon(Icons.Default.Tune, contentDescription = "Criteria", tint = TitanCyan, modifier = Modifier.size(16.dp))
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // 3-Metric Metric Ribbon
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            AutomationMetricTile(
              label = "AUTO APPS LOGGED",
              value = "$appsCount Submitted",
              accent = TitanCyan,
              modifier = Modifier.weight(1f)
            )
            AutomationMetricTile(
              label = "ATS PASS RATE",
              value = "$successRate% Quality",
              accent = TitanEmerald,
              modifier = Modifier.weight(1f)
            )
            AutomationMetricTile(
              label = "READY APPROVALS",
              value = "${readyMatches.size} Queued",
              accent = TitanGold,
              modifier = Modifier.weight(1f)
            )
          }
        }
      }
    }

    // 1.5 Autonomous Background Job Pull Service
    item {
      AutonomousBackgroundJobServiceCard(
        isServiceRunning = isServiceRunning,
        isPeriodicEnabled = isPeriodicEnabled,
        periodicIntervalMinutes = periodicIntervalMinutes,
        lastPullTimestamp = lastPullTimestamp,
        lastPulledCount = lastPulledCount,
        totalStoredFromAutomation = totalStoredFromAutomation,
        targetCompaniesCount = targetCompaniesCount,
        activeGoalsSummary = activeGoalsSummary,
        discoveryLogs = discoveryLogs,
        onTriggerPull = { viewModel.triggerBackgroundJobPull() },
        onTogglePeriodic = { viewModel.setJobDiscoverySchedulerEnabled(it) },
        onSetInterval = { viewModel.setJobDiscoveryInterval(it) }
      )
    }

    // 2. Control Trigger Bar
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SlateCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, TitanCyan.copy(alpha = 0.4f))
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(8.dp)
                  .clip(CircleShape)
                  .background(if (isRunning) TitanGold else TitanEmerald)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = if (isRunning) "SCANNING LIVE PIPELINES..." else "SWARM AGENTS STANDBY",
                style = MaterialTheme.typography.labelSmall,
                color = if (isRunning) TitanGold else TitanEmerald,
                fontWeight = FontWeight.Bold,
                fontSize = 9.sp
              )
            }
            Text(
              text = "Last synced: $lastRun",
              style = MaterialTheme.typography.bodySmall,
              color = TextMutedDark,
              fontSize = 11.sp
            )
          }

          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
              onClick = { showCriteriaDialog = true },
              colors = ButtonDefaults.buttonColors(containerColor = SlateElevated),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.height(40.dp)
            ) {
              Icon(Icons.Default.Settings, contentDescription = null, tint = TextSecondaryDark, modifier = Modifier.size(14.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Criteria", color = TextPrimaryDark, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }

            Button(
              onClick = { viewModel.triggerAutomationBatchRun() },
              colors = ButtonDefaults.buttonColors(containerColor = TitanCyan),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.height(40.dp),
              enabled = !isRunning
            ) {
              if (isRunning) {
                CircularProgressIndicator(color = ObsidianDark, modifier = Modifier.size(14.dp), strokeWidth = 2.dp)
              } else {
                Icon(Icons.Default.PlayArrow, contentDescription = null, tint = ObsidianDark, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Trigger Run", color = ObsidianDark, fontSize = 11.sp, fontWeight = FontWeight.Bold)
              }
            }
          }
        }
      }
    }

    // 3. Active Search Criteria Summary Card
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SlateElevated),
        border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "ACTIVE AUTOMATION CRITERIA",
              style = MaterialTheme.typography.labelSmall,
              color = TitanCyan,
              fontWeight = FontWeight.Bold,
              fontSize = 10.sp
            )

            Text(
              text = "Min Fit: ${criteria.minFitScore}% | ≥ ₹${criteria.minSalaryLakhs} LPA",
              style = MaterialTheme.typography.labelSmall,
              color = TitanGold,
              fontWeight = FontWeight.Bold,
              fontSize = 9.sp
            )
          }

          Spacer(modifier = Modifier.height(8.dp))

          // Target Roles Chips
          LazyRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            items(criteria.targetRoles) { role ->
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(4.dp))
                  .background(SlateCard)
                  .border(1.dp, SlateBorder, RoundedCornerShape(4.dp))
                  .padding(horizontal = 8.dp, vertical = 4.dp)
              ) {
                Text(
                  text = "🎯 $role",
                  style = MaterialTheme.typography.labelSmall,
                  color = TextPrimaryDark,
                  fontSize = 10.sp
                )
              }
            }
            items(criteria.targetLocations) { loc ->
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(4.dp))
                  .background(TitanCyan.copy(alpha = 0.1f))
                  .padding(horizontal = 8.dp, vertical = 4.dp)
              ) {
                Text(
                  text = "📍 $loc",
                  style = MaterialTheme.typography.labelSmall,
                  color = TitanCyan,
                  fontSize = 10.sp
                )
              }
            }
          }
        }
      }
    }

    // 4. Section: READY FOR 1-TAP APPROVAL & DISPATCH
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "AUTOMATED MATCHES • READY FOR DISPATCH (${readyMatches.size})",
          style = MaterialTheme.typography.labelSmall,
          color = TitanGold,
          fontWeight = FontWeight.Bold
        )

        if (readyMatches.isNotEmpty()) {
          TextButton(
            onClick = {
              readyMatches.forEach { match ->
                viewModel.approveAndSubmitAutomatedApplication(match)
              }
            }
          ) {
            Icon(Icons.Default.Bolt, contentDescription = null, tint = TitanGold, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Approve All (${readyMatches.size})", color = TitanGold, fontWeight = FontWeight.Bold, fontSize = 11.sp)
          }
        }
      }
    }

    if (readyMatches.isEmpty()) {
      item {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(SlateElevated)
            .padding(20.dp),
          contentAlignment = Alignment.Center
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = TitanEmerald, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(6.dp))
            Text("All matching applications reviewed & dispatched!", style = MaterialTheme.typography.bodySmall, color = TextSecondaryDark)
          }
        }
      }
    } else {
      items(readyMatches, key = { it.job.id }) { match ->
        AutomatedJobMatchCard(
          match = match,
          onApprove = { viewModel.approveAndSubmitAutomatedApplication(match) },
          onSkip = { viewModel.skipAutomatedJob(match) }
        )
      }
    }

    // 5. Section: RECENTLY SUBMITTED VIA AUTOMATION
    if (submittedMatches.isNotEmpty()) {
      item {
        Spacer(modifier = Modifier.height(10.dp))
        Text(
          text = "SUBMITTED VIA COMMAND CENTER (${submittedMatches.size})",
          style = MaterialTheme.typography.labelSmall,
          color = TitanEmerald,
          fontWeight = FontWeight.Bold
        )
      }

      items(submittedMatches, key = { "sub_${it.job.id}" }) { match ->
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(8.dp),
          colors = CardDefaults.cardColors(containerColor = SlateCard),
          border = androidx.compose.foundation.BorderStroke(1.dp, TitanEmerald.copy(alpha = 0.3f))
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(text = match.job.title, style = MaterialTheme.typography.titleSmall, color = TextPrimaryDark, fontWeight = FontWeight.Bold)
              Text(text = "${match.job.companyName} • ${match.job.location}", style = MaterialTheme.typography.bodySmall, color = TextMutedDark, fontSize = 11.sp)
            }

            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(TitanEmerald.copy(alpha = 0.15f))
                .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
              Text(
                text = "✓ SUBMITTED",
                style = MaterialTheme.typography.labelSmall,
                color = TitanEmerald,
                fontWeight = FontWeight.Bold,
                fontSize = 9.sp
              )
            }
          }
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(24.dp))
    }
  }

  // Criteria Configuration Modal
  if (showCriteriaDialog) {
    AutomationCriteriaConfigDialog(
      currentCriteria = criteria,
      onDismiss = { showCriteriaDialog = false },
      onSave = { updated ->
        viewModel.updateAutomationCriteria(updated)
        showCriteriaDialog = false
      }
    )
  }
}

@Composable
fun AutomatedJobMatchCard(
  match: AutomatedJobMatch,
  onApprove: () -> Unit,
  onSkip: () -> Unit
) {
  val haptic = LocalHapticFeedback.current
  var isExpanded by remember { mutableStateOf(false) }

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .clickable { isExpanded = !isExpanded },
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = SlateCard),
    border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      // Top Row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = match.job.companyName,
            style = MaterialTheme.typography.labelSmall,
            color = TitanCyan,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = match.job.title,
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimaryDark,
            fontWeight = FontWeight.Bold
          )
        }

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(SlateElevated)
            .border(1.dp, TitanGold.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
              text = "${match.matchScore}%",
              style = MaterialTheme.typography.titleSmall,
              color = TitanGold,
              fontWeight = FontWeight.Black
            )
            Text(
              text = "MATCH",
              style = MaterialTheme.typography.labelSmall,
              color = TextMutedDark,
              fontSize = 7.sp
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Tags Row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        TagPill(match.job.location, Icons.Default.LocationOn, TitanCyan)
        TagPill(match.job.salaryRange, Icons.Default.Payments, TitanEmerald)
        TagPill("ATS Tailored Resume Ready", Icons.Default.Description, TitanGold)
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Match Logic Teardown
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(6.dp))
          .background(SlateElevated)
          .padding(8.dp)
      ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
          Text(
            text = "EXECUTIVE STRATEGY PITCH:",
            style = MaterialTheme.typography.labelSmall,
            color = TitanCyan,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = match.recommendedPitch,
            style = MaterialTheme.typography.bodySmall,
            color = TextPrimaryDark,
            fontSize = 11.sp
          )
        }
      }

      if (isExpanded) {
        Spacer(modifier = Modifier.height(8.dp))

        Text(
          text = "KEY MATCH FACTORS:",
          style = MaterialTheme.typography.labelSmall,
          color = TitanEmerald,
          fontSize = 9.sp,
          fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        match.keyMatchFactors.forEach { factor ->
          Row(verticalAlignment = Alignment.Top) {
            Icon(Icons.Default.Check, contentDescription = null, tint = TitanEmerald, modifier = Modifier.size(12.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = factor, style = MaterialTheme.typography.bodySmall, color = TextSecondaryDark, fontSize = 10.sp)
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // 1-Tap Trigger Application Actions
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        TextButton(
          onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            onSkip()
          }
        ) {
          Icon(Icons.Default.SkipNext, contentDescription = null, tint = TextMutedDark, modifier = Modifier.size(14.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Skip", color = TextMutedDark, fontSize = 11.sp)
        }

        Button(
          onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            onApprove()
          },
          colors = ButtonDefaults.buttonColors(containerColor = TitanCyan),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier.height(38.dp)
        ) {
          Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, tint = ObsidianDark, modifier = Modifier.size(14.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Dispatch Application", color = ObsidianDark, fontWeight = FontWeight.Bold, fontSize = 11.sp)
        }
      }
    }
  }
}

@Composable
fun AutomationCriteriaConfigDialog(
  currentCriteria: AutomationCriteria,
  onDismiss: () -> Unit,
  onSave: (AutomationCriteria) -> Unit
) {
  var minFit by remember { mutableStateOf(currentCriteria.minFitScore.toFloat()) }
  var minSalary by remember { mutableStateOf(currentCriteria.minSalaryLakhs.toFloat()) }
  var fresherOnly by remember { mutableStateOf(currentCriteria.onlyFresherFriendly) }
  var autoResume by remember { mutableStateOf(currentCriteria.autoGenerateTailoredResume) }
  var autoCoverLetter by remember { mutableStateOf(currentCriteria.autoDraftCoverLetter) }

  AlertDialog(
    onDismissRequest = { onDismiss() },
    containerColor = SlateElevated,
    title = {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.Tune, contentDescription = null, tint = TitanCyan, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text("Configure Job Automation Rules", style = MaterialTheme.typography.titleMedium, color = TextPrimaryDark)
      }
    },
    text = {
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        // Minimum Fit Score Slider
        Column {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text("Minimum Fit Threshold", style = MaterialTheme.typography.bodySmall, color = TextPrimaryDark)
            Text("${minFit.toInt()}%", style = MaterialTheme.typography.labelSmall, color = TitanCyan, fontWeight = FontWeight.Bold)
          }
          Slider(
            value = minFit,
            onValueChange = { minFit = it },
            valueRange = 70f..98f,
            steps = 28,
            colors = SliderDefaults.colors(thumbColor = TitanCyan, activeTrackColor = TitanCyan)
          )
        }

        // Minimum Salary Slider
        Column {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text("Minimum Salary Floor", style = MaterialTheme.typography.bodySmall, color = TextPrimaryDark)
            Text("₹${minSalary.toInt()} LPA", style = MaterialTheme.typography.labelSmall, color = TitanEmerald, fontWeight = FontWeight.Bold)
          }
          Slider(
            value = minSalary,
            onValueChange = { minSalary = it },
            valueRange = 8f..30f,
            steps = 22,
            colors = SliderDefaults.colors(thumbColor = TitanEmerald, activeTrackColor = TitanEmerald)
          )
        }

        // Toggle 1: Fresher Friendly
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text("0-2 Years / Fresher Roles Only", style = MaterialTheme.typography.bodySmall, color = TextPrimaryDark)
            Text("Strictly filter for early career bandwidth", style = MaterialTheme.typography.labelSmall, color = TextMutedDark, fontSize = 10.sp)
          }
          Switch(
            checked = fresherOnly,
            onCheckedChange = { fresherOnly = it },
            colors = SwitchDefaults.colors(checkedThumbColor = TitanCyan, checkedTrackColor = TitanCyan.copy(alpha = 0.5f))
          )
        }

        // Toggle 2: Auto Generate Tailored Resume
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text("Auto-Tailor ATS Resumes", style = MaterialTheme.typography.bodySmall, color = TextPrimaryDark)
            Text("Synthesize ATS copy against verified fact bank", style = MaterialTheme.typography.labelSmall, color = TextMutedDark, fontSize = 10.sp)
          }
          Switch(
            checked = autoResume,
            onCheckedChange = { autoResume = it },
            colors = SwitchDefaults.colors(checkedThumbColor = TitanGold, checkedTrackColor = TitanGold.copy(alpha = 0.5f))
          )
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          onSave(
            currentCriteria.copy(
              minFitScore = minFit.toInt(),
              minSalaryLakhs = minSalary.toInt(),
              onlyFresherFriendly = fresherOnly,
              autoGenerateTailoredResume = autoResume,
              autoDraftCoverLetter = autoCoverLetter
            )
          )
        },
        colors = ButtonDefaults.buttonColors(containerColor = TitanCyan, contentColor = ObsidianDark),
        shape = RoundedCornerShape(8.dp)
      ) {
        Text("Save & Recalculate", fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      TextButton(onClick = { onDismiss() }) {
        Text("Cancel", color = TextSecondaryDark)
      }
    }
  )
}

@Composable
fun AutonomousBackgroundJobServiceCard(
  isServiceRunning: Boolean,
  isPeriodicEnabled: Boolean,
  periodicIntervalMinutes: Int,
  lastPullTimestamp: String,
  lastPulledCount: Int,
  totalStoredFromAutomation: Int,
  targetCompaniesCount: Int,
  activeGoalsSummary: String,
  discoveryLogs: List<JobDiscoveryLogItem>,
  onTriggerPull: () -> Unit,
  onTogglePeriodic: (Boolean) -> Unit,
  onSetInterval: (Int) -> Unit
) {
  var isLogsExpanded by remember { mutableStateOf(false) }

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("autonomous_job_service_card"),
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = SlateCard),
    border = androidx.compose.foundation.BorderStroke(1.dp, TitanCyan.copy(alpha = 0.5f))
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      // Header: Title & Status Indicator
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Radar, contentDescription = null, tint = TitanCyan, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "BACKGROUND JOB AUTOMATION SERVICE",
              style = MaterialTheme.typography.labelSmall,
              color = TitanCyan,
              fontWeight = FontWeight.Bold,
              fontSize = 10.sp
            )
          }
          Text(
            text = "Target Companies Auto-Pull & Room Sync",
            style = MaterialTheme.typography.titleSmall,
            color = TextPrimaryDark,
            fontWeight = FontWeight.Bold
          )
        }

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(if (isPeriodicEnabled) TitanEmerald.copy(alpha = 0.15f) else SlateElevated)
            .border(1.dp, if (isPeriodicEnabled) TitanEmerald.copy(alpha = 0.4f) else SlateBorder, RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(if (isServiceRunning) TitanGold else if (isPeriodicEnabled) TitanEmerald else TextMutedDark)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
              text = if (isServiceRunning) "SCANNING..." else if (isPeriodicEnabled) "DAEMON ACTIVE" else "STANDBY",
              style = MaterialTheme.typography.labelSmall,
              color = if (isServiceRunning) TitanGold else if (isPeriodicEnabled) TitanEmerald else TextMutedDark,
              fontSize = 9.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Career Goals Integration Callout
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(8.dp))
          .background(SlateElevated)
          .border(1.dp, TitanGold.copy(alpha = 0.25f), RoundedCornerShape(8.dp))
          .padding(10.dp)
      ) {
        Row(verticalAlignment = Alignment.Top) {
          Icon(Icons.Default.Flag, contentDescription = null, tint = TitanGold, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Column {
            Text(
              text = "CRITERIA TIED TO USER CAREER GOALS",
              style = MaterialTheme.typography.labelSmall,
              color = TitanGold,
              fontWeight = FontWeight.Bold,
              fontSize = 9.sp
            )
            Text(
              text = "BBA International Business • Quantitative Strategy & Ops • Target: ₹16L+ in Bengaluru",
              style = MaterialTheme.typography.bodySmall,
              color = TextPrimaryDark,
              fontWeight = FontWeight.Medium,
              fontSize = 11.sp
            )
            Text(
              text = "Pulls active openings from watched & Tier S/A companies matching 30d/90d goals and stores verified records directly in Room database.",
              style = MaterialTheme.typography.labelSmall,
              color = TextMutedDark,
              fontSize = 10.sp
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // 3-Tile Telemetry Grid
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        AutomationMetricTile(
          label = "TARGET COS",
          value = "$targetCompaniesCount Tracked",
          accent = TitanCyan,
          modifier = Modifier.weight(1f)
        )
        AutomationMetricTile(
          label = "STORED IN ROOM",
          value = "$totalStoredFromAutomation Saved",
          accent = TitanEmerald,
          modifier = Modifier.weight(1f)
        )
        AutomationMetricTile(
          label = "LAST SYNC",
          value = if (lastPullTimestamp == "Never") "Standby" else lastPullTimestamp.takeLast(8),
          accent = TitanGold,
          modifier = Modifier.weight(1f)
        )
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Controls: Trigger Pull Button & Scheduler Settings
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Button(
          onClick = onTriggerPull,
          enabled = !isServiceRunning,
          colors = ButtonDefaults.buttonColors(containerColor = TitanCyan, contentColor = ObsidianDark),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier.testTag("pull_jobs_now_button")
        ) {
          if (isServiceRunning) {
            CircularProgressIndicator(color = ObsidianDark, modifier = Modifier.size(14.dp), strokeWidth = 2.dp)
            Spacer(modifier = Modifier.width(6.dp))
            Text("Pulling from Companies...", fontSize = 11.sp, fontWeight = FontWeight.Bold)
          } else {
            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Pull Jobs Now", fontSize = 11.sp, fontWeight = FontWeight.Bold)
          }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = "Periodic Auto-Pull",
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondaryDark,
            fontSize = 11.sp
          )
          Spacer(modifier = Modifier.width(6.dp))
          Switch(
            checked = isPeriodicEnabled,
            onCheckedChange = onTogglePeriodic,
            colors = SwitchDefaults.colors(
              checkedThumbColor = TitanEmerald,
              checkedTrackColor = TitanEmerald.copy(alpha = 0.5f),
              uncheckedThumbColor = TextMutedDark,
              uncheckedTrackColor = SlateElevated
            )
          )
        }
      }

      // Cadence Selector Chips (if periodic is enabled)
      if (isPeriodicEnabled) {
        Spacer(modifier = Modifier.height(8.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Cadence:",
            style = MaterialTheme.typography.labelSmall,
            color = TextMutedDark,
            fontSize = 10.sp
          )
          listOf(15 to "15m", 30 to "30m", 60 to "1h", 360 to "6h").forEach { (interval, label) ->
            val isSelected = periodicIntervalMinutes == interval
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(if (isSelected) TitanCyan.copy(alpha = 0.2f) else SlateElevated)
                .border(1.dp, if (isSelected) TitanCyan else SlateBorder, RoundedCornerShape(6.dp))
                .clickable { onSetInterval(interval) }
                .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
              Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = if (isSelected) TitanCyan else TextSecondaryDark,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                fontSize = 10.sp
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Expandable Activity & Sync Feed Toggle
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(6.dp))
          .background(SlateElevated)
          .clickable { isLogsExpanded = !isLogsExpanded }
          .padding(horizontal = 10.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Description, contentDescription = null, tint = TitanCyan, modifier = Modifier.size(13.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Service Activity & Storage Logs (${discoveryLogs.size})",
            style = MaterialTheme.typography.labelSmall,
            color = TextPrimaryDark,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
          )
        }
        Text(
          text = if (isLogsExpanded) "Hide ▲" else "Expand ▼",
          style = MaterialTheme.typography.labelSmall,
          color = TitanCyan,
          fontSize = 10.sp
        )
      }

      if (isLogsExpanded) {
        Spacer(modifier = Modifier.height(8.dp))
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
          discoveryLogs.take(5).forEach { log ->
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(6.dp))
                .background(ObsidianDark)
                .border(0.5.dp, SlateBorder, RoundedCornerShape(6.dp))
                .padding(8.dp)
            ) {
              Column {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Text(
                    text = "${log.triggerSource} • ${log.timestamp}",
                    style = MaterialTheme.typography.labelSmall,
                    color = TitanGold,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                  )
                  Text(
                    text = "+${log.newJobsStoredCount} Stored in Room",
                    style = MaterialTheme.typography.labelSmall,
                    color = TitanEmerald,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                  )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                  text = log.summaryMessage,
                  style = MaterialTheme.typography.bodySmall,
                  color = TextSecondaryDark,
                  fontSize = 10.sp
                )
                if (log.topJobTitles.isNotEmpty()) {
                  Spacer(modifier = Modifier.height(2.dp))
                  Text(
                    text = "Discovered: ${log.topJobTitles.joinToString(", ")}",
                    style = MaterialTheme.typography.labelSmall,
                    color = TitanCyan,
                    fontSize = 9.sp
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

@Composable
fun AutomationMetricTile(
  label: String,
  value: String,
  accent: Color,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(8.dp))
      .background(SlateElevated)
      .border(1.dp, SlateBorder, RoundedCornerShape(8.dp))
      .padding(8.dp)
  ) {
    Column {
      Text(text = label, style = MaterialTheme.typography.labelSmall, color = TextMutedDark, fontSize = 8.sp, fontWeight = FontWeight.Bold)
      Spacer(modifier = Modifier.height(2.dp))
      Text(text = value, style = MaterialTheme.typography.titleSmall, color = accent, fontWeight = FontWeight.Black, fontSize = 11.sp)
    }
  }
}

@Composable
fun JobItemCard(
  job: Job,
  onPrepareApply: () -> Unit,
  onActionPlan: () -> Unit,
  onCoverLetter: () -> Unit = {},
  onMatchResume: () -> Unit = {},
  onNegotiate: () -> Unit = {}
) {
  var isExpanded by remember { mutableStateOf(false) }

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .clickable { isExpanded = !isExpanded }
      .testTag("job_card_${job.id}"),
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = SlateCard),
    border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      // Top Row: Company & Scores
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = job.companyName,
            style = MaterialTheme.typography.labelMedium,
            color = TitanCyan,
            fontWeight = FontWeight.Bold
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = job.title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = TextPrimaryDark
          )
        }

        // Score Badges
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          // Fit Score
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(SlateElevated)
              .border(1.dp, TitanEmerald.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
              .padding(horizontal = 6.dp, vertical = 3.dp)
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                text = "${job.adiFitScore}%",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = TitanEmerald
              )
              Text("FIT", style = MaterialTheme.typography.labelSmall, fontSize = 7.sp, color = TextMutedDark)
            }
          }

          // Opportunity Score
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(SlateElevated)
              .border(1.dp, TitanGold.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
              .padding(horizontal = 6.dp, vertical = 3.dp)
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                text = "${job.opportunityScore}",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = TitanGold
              )
              Text("OPP", style = MaterialTheme.typography.labelSmall, fontSize = 7.sp, color = TextMutedDark)
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Tags: Location, Salary, Remote, Experience
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        TagPill(job.location, Icons.Default.LocationOn, TitanCyan)
        TagPill(job.salaryRange, Icons.Default.Payments, TitanEmerald)
        if (job.id.contains("job_auto_") || job.officialSource.contains("Portal")) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(4.dp))
              .background(TitanCyan.copy(alpha = 0.15f))
              .border(0.5.dp, TitanCyan.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
              .padding(horizontal = 6.dp, vertical = 2.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Bolt, contentDescription = null, tint = TitanCyan, modifier = Modifier.size(10.dp))
              Spacer(modifier = Modifier.width(3.dp))
              Text("RADAR SYNC", color = TitanCyan, fontSize = 8.sp, fontWeight = FontWeight.Bold)
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Why It Fits Highlight
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(6.dp))
          .background(SlateElevated)
          .padding(8.dp)
      ) {
        Column {
          Text(
            text = "WHY IT FITS ADI:",
            style = MaterialTheme.typography.labelSmall,
            color = TitanCyan,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = job.whyItFits,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondaryDark,
            fontSize = 11.sp,
            maxLines = if (isExpanded) Int.MAX_VALUE else 2
          )
        }
      }

      if (isExpanded) {
        Spacer(modifier = Modifier.height(8.dp))

        if (job.missingRequirements.isNotEmpty()) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(6.dp))
              .background(SlateElevated)
              .padding(8.dp)
          ) {
            Column {
              Text(
                text = "MISSING REQUIREMENTS / FOCUS:",
                style = MaterialTheme.typography.labelSmall,
                color = TitanGold,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
              )
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = job.missingRequirements,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondaryDark,
                fontSize = 11.sp
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Action Row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Status indicator
        StatusBadge(job.status)

        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          // Action Plan Button
          Button(
            onClick = { onActionPlan() },
            colors = ButtonDefaults.buttonColors(containerColor = SlateElevated, contentColor = TitanCyan),
            shape = RoundedCornerShape(8.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, TitanCyan.copy(alpha = 0.6f)),
            modifier = Modifier.testTag("action_plan_btn_${job.id}")
          ) {
            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = TitanCyan, modifier = Modifier.size(13.dp))
            Spacer(modifier = Modifier.width(3.dp))
            Text("Action Plan", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, fontSize = 10.5.sp)
          }

          // Resume Match Score Button
          Button(
            onClick = { onMatchResume() },
            colors = ButtonDefaults.buttonColors(containerColor = SlateElevated, contentColor = TitanEmerald),
            shape = RoundedCornerShape(8.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, TitanEmerald.copy(alpha = 0.6f)),
            modifier = Modifier.testTag("match_resume_btn_${job.id}")
          ) {
            Icon(Icons.AutoMirrored.Filled.FactCheck, contentDescription = null, tint = TitanEmerald, modifier = Modifier.size(13.dp))
            Spacer(modifier = Modifier.width(3.dp))
            Text("Match", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, fontSize = 10.5.sp)
          }

          // Salary Negotiation & Benchmark Button
          Button(
            onClick = { onNegotiate() },
            colors = ButtonDefaults.buttonColors(containerColor = SlateElevated, contentColor = TitanEmerald),
            shape = RoundedCornerShape(8.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, TitanEmerald.copy(alpha = 0.6f)),
            modifier = Modifier.testTag("negotiate_job_btn_${job.id}")
          ) {
            Icon(Icons.Default.Payments, contentDescription = null, tint = TitanEmerald, modifier = Modifier.size(13.dp))
            Spacer(modifier = Modifier.width(3.dp))
            Text("Negotiate", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, fontSize = 10.5.sp)
          }

          // Cover Letter Button
          Button(
            onClick = { onCoverLetter() },
            colors = ButtonDefaults.buttonColors(containerColor = SlateElevated, contentColor = TitanGold),
            shape = RoundedCornerShape(8.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, TitanGold.copy(alpha = 0.6f)),
            modifier = Modifier.testTag("cover_letter_btn_${job.id}")
          ) {
            Icon(Icons.Default.Description, contentDescription = null, tint = TitanGold, modifier = Modifier.size(13.dp))
            Spacer(modifier = Modifier.width(3.dp))
            Text("Cover Letter", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, fontSize = 10.5.sp)
          }

          // Prepare & Apply Button
          Button(
            onClick = { onPrepareApply() },
            colors = ButtonDefaults.buttonColors(containerColor = TitanCyan, contentColor = ObsidianDark),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.testTag("prepare_apply_btn_${job.id}")
          ) {
            Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(13.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Prepare", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, fontSize = 10.5.sp)
          }
        }
      }
    }
  }
}

@Composable
fun TagPill(text: String, icon: androidx.compose.ui.graphics.vector.ImageVector, tint: Color) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier
      .clip(RoundedCornerShape(4.dp))
      .background(SlateElevated)
      .padding(horizontal = 6.dp, vertical = 2.dp)
  ) {
    Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(11.dp))
    Spacer(modifier = Modifier.width(4.dp))
    Text(text, style = MaterialTheme.typography.labelSmall, color = TextPrimaryDark, fontSize = 9.sp, maxLines = 1)
  }
}

@Composable
fun StatusBadge(status: String) {
  val (color, text) = when (status) {
    "OFFER" -> TitanEmerald to "OFFER SECURED"
    "INTERVIEWING" -> TitanGold to "INTERVIEW SCHEDULED"
    "APPLIED" -> TitanIndigo to "APPLIED"
    "SHORTLISTED" -> TitanCyan to "SHORTLISTED"
    else -> TextMutedDark to "ACTIVE OPPORTUNITY"
  }

  Box(
    modifier = Modifier
      .clip(RoundedCornerShape(4.dp))
      .background(color.copy(alpha = 0.15f))
      .padding(horizontal = 6.dp, vertical = 3.dp)
  ) {
    Text(
      text = text,
      style = MaterialTheme.typography.labelSmall,
      color = color,
      fontWeight = FontWeight.Bold,
      fontSize = 9.sp
    )
  }
}

@Composable
fun FilterChip(
  label: String,
  isSelected: Boolean,
  onClick: () -> Unit
) {
  Box(
    modifier = Modifier
      .clip(RoundedCornerShape(8.dp))
      .background(if (isSelected) SlateElevated else SlateCard)
      .border(1.dp, if (isSelected) TitanCyan else SlateBorder, RoundedCornerShape(8.dp))
      .clickable { onClick() }
      .padding(horizontal = 10.dp, vertical = 6.dp)
  ) {
    Text(
      text = label,
      style = MaterialTheme.typography.labelSmall,
      color = if (isSelected) TitanCyan else TextSecondaryDark,
      fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
    )
  }
}
