package com.example.ui.components

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import androidx.compose.material.icons.automirrored.filled.FactCheck
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.TravelExplore
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Company
import com.example.data.model.Job
import com.example.data.model.RecruiterContact
import com.example.data.model.StarStory
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

@Composable
fun CommandPaletteDialog(
  viewModel: TitanViewModel,
  onDismissRequest: () -> Unit
) {
  var searchQuery by remember { mutableStateOf("") }

  val companies: List<Company> by viewModel.companies.collectAsState()
  val jobs: List<Job> by viewModel.jobs.collectAsState()
  val stories: List<StarStory> by viewModel.stories.collectAsState()
  val contacts: List<RecruiterContact> by viewModel.contacts.collectAsState()

  val isSlashCommand = searchQuery.startsWith("/")
  val cleanQuery = if (isSlashCommand) searchQuery.substring(1).trim() else searchQuery.trim()

  val filteredCompanies = companies.filter {
    cleanQuery.isBlank() || it.name.contains(cleanQuery, ignoreCase = true) || it.industry.contains(cleanQuery, ignoreCase = true)
  }.take(3)

  val filteredJobs = jobs.filter {
    cleanQuery.isBlank() || it.title.contains(cleanQuery, ignoreCase = true) || it.companyName.contains(cleanQuery, ignoreCase = true)
  }.take(3)

  val filteredStories = stories.filter {
    cleanQuery.isBlank() || it.title.contains(cleanQuery, ignoreCase = true) || it.relevantSkills.contains(cleanQuery, ignoreCase = true)
  }.take(3)

  val filteredContacts = contacts.filter {
    cleanQuery.isBlank() || it.name.contains(cleanQuery, ignoreCase = true) || it.companyName.contains(cleanQuery, ignoreCase = true) || it.title.contains(cleanQuery, ignoreCase = true)
  }.take(3)

  AlertDialog(
    onDismissRequest = onDismissRequest,
    containerColor = ObsidianDark,
    shape = RoundedCornerShape(16.dp),
    modifier = Modifier
      .fillMaxWidth()
      .testTag("command_palette_dialog"),
    title = {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(28.dp)
              .clip(CircleShape)
              .background(TitanCyan.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
          ) {
            Icon(Icons.Default.Bolt, contentDescription = null, tint = TitanCyan, modifier = Modifier.size(16.dp))
          }
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "UNIVERSAL COMMAND & SUPER-SEARCH",
            style = MaterialTheme.typography.labelSmall,
            color = TitanCyan,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
          )
        }
        IconButton(onClick = onDismissRequest) {
          Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMutedDark)
        }
      }
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .height(440.dp)
      ) {
        OutlinedTextField(
          value = searchQuery,
          onValueChange = { searchQuery = it },
          placeholder = { Text("Type /jobs, /agents, /skills or search any entity...", fontSize = 12.sp, color = TextMutedDark) },
          leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TitanCyan) },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("command_palette_search_input"),
          colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = SlateCard,
            unfocusedContainerColor = SlateCard,
            focusedBorderColor = TitanCyan,
            unfocusedBorderColor = SlateBorder,
            focusedTextColor = TextPrimaryDark,
            unfocusedTextColor = TextPrimaryDark
          ),
          shape = RoundedCornerShape(10.dp),
          singleLine = true
        )

        Spacer(modifier = Modifier.height(10.dp))

        LazyColumn(
          modifier = Modifier.fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          // Slash Commands Group
          item {
            Text(
              text = "SLASH COMMANDS (DIRECT DISPATCH)",
              style = MaterialTheme.typography.labelSmall,
              color = TitanCyan,
              fontSize = 9.sp,
              fontWeight = FontWeight.Bold
            )
          }

          item {
            PaletteActionItem(
              icon = Icons.Default.SmartToy,
              tint = TitanIndigo,
              title = "/agents • Autonomous Fleet Control Room",
              subtitle = "Monitor 10 swarm agents, approval queue & telemetry",
              onClick = {
                viewModel.navigateTo(TitanScreen.AGENTS)
                onDismissRequest()
              }
            )
          }

          item {
            PaletteActionItem(
              icon = Icons.Default.Tune,
              tint = TitanCyan,
              title = "/automations • Configure Automation Rules",
              subtitle = "Define trigger conditions & automated autonomous responses",
              onClick = {
                viewModel.navigateTo(TitanScreen.AUTOMATION_RULES)
                onDismissRequest()
              }
            )
          }

          item {
            PaletteActionItem(
              icon = Icons.Default.BarChart,
              tint = TitanCyan,
              title = "/charts • Automation Analytics & Recharts",
              subtitle = "Application status distribution donut, activity spline & funnel",
              onClick = {
                viewModel.navigateTo(TitanScreen.AUTOMATION_RULES)
                onDismissRequest()
              }
            )
          }

          item {
            PaletteActionItem(
              icon = Icons.Default.Work,
              tint = TitanGold,
              title = "/jobs • Job Intelligence Radar",
              subtitle = "Explore verified Tier S/A openings with fit matching",
              onClick = {
                viewModel.navigateTo(TitanScreen.JOBS)
                onDismissRequest()
              }
            )
          }

          item {
            PaletteActionItem(
              icon = Icons.Default.Business,
              tint = TitanCyan,
              title = "/company • Company Intelligence Universe",
              subtitle = "Inspect tier classification, hiring velocity, and compensation",
              onClick = {
                viewModel.setCompanyIntelTab(0)
                viewModel.navigateTo(TitanScreen.COMPANIES)
                onDismissRequest()
              }
            )
          }

          item {
            PaletteActionItem(
              icon = Icons.Default.TravelExplore,
              tint = TitanGold,
              title = "/intel • Live Search Grounding Radar",
              subtitle = "Real-time Google Search grounding on business news, hiring & exec shifts",
              onClick = {
                viewModel.setCompanyIntelTab(1)
                viewModel.navigateTo(TitanScreen.COMPANIES)
                onDismissRequest()
              }
            )
          }

          item {
            PaletteActionItem(
              icon = Icons.AutoMirrored.Filled.Assignment,
              tint = TitanEmerald,
              title = "/applications • Application Factory & CRM",
              subtitle = "Track submission stages, follow-ups & recruiter emails",
              onClick = {
                viewModel.navigateTo(TitanScreen.APPLICATIONS)
                onDismissRequest()
              }
            )
          }

          item {
            PaletteActionItem(
              icon = Icons.Default.Psychology,
              tint = TitanIndigo,
              title = "/interviews • Interview OS & STAR Bank",
              subtitle = "Run live voice simulation & query evidence stories",
              onClick = {
                viewModel.navigateTo(TitanScreen.INTERVIEWS)
                onDismissRequest()
              }
            )
          }

          item {
            PaletteActionItem(
              icon = Icons.AutoMirrored.Filled.TrendingUp,
              tint = TitanCyan,
              title = "/strategy • Career Strategy & Roadmap OS",
              subtitle = "Input current role & target goals, recalibrate roadmap & sync to Firestore",
              onClick = {
                viewModel.navigateTo(TitanScreen.CAREER_STRATEGY)
                onDismissRequest()
              }
            )
          }

          item {
            PaletteActionItem(
              icon = Icons.Default.Description,
              tint = TitanCyan,
              title = "/notes • Career Strategy Notes Vault",
              subtitle = "Strategic memos, interview battle-plans & salary negotiation scripts",
              onClick = {
                viewModel.navigateTo(TitanScreen.CAREER_NOTES)
                onDismissRequest()
              }
            )
          }

          item {
            PaletteActionItem(
              icon = Icons.Default.Bookmark,
              tint = TitanGold,
              title = "/bookmarks • Company Bookmarks & Intel",
              subtitle = "Target companies, tier classification, alerts & private notes",
              onClick = {
                viewModel.navigateTo(TitanScreen.COMPANY_BOOKMARKS)
                onDismissRequest()
              }
            )
          }

          item {
            PaletteActionItem(
              icon = Icons.Default.History,
              tint = TitanEmerald,
              title = "/task-logs • Automation Task Logs",
              subtitle = "Live crawler telemetry, background audits & operational logs",
              onClick = {
                viewModel.navigateTo(TitanScreen.TASK_LOGS)
                onDismissRequest()
              }
            )
          }

          item {
            PaletteActionItem(
              icon = Icons.Default.RocketLaunch,
              tint = TitanGold,
              title = "/skills • Career Capital & Venture OS",
              subtitle = "AI multipliers, proof-of-work repos & B2B SaaS pipeline",
              onClick = {
                viewModel.navigateTo(TitanScreen.SKILLS_PROJECTS)
                onDismissRequest()
              }
            )
          }

          item {
            PaletteActionItem(
              icon = Icons.Default.People,
              tint = TitanEmerald,
              title = "/network • Recruiter Relationship Graph",
              subtitle = "Outreach CRM, warmth score & referral discovery",
              onClick = {
                viewModel.navigateTo(TitanScreen.NETWORK)
                onDismissRequest()
              }
            )
          }

          item {
            PaletteActionItem(
              icon = Icons.Default.Email,
              tint = TitanEmerald,
              title = "/outreach • Referral & Pitch Synthesizer",
              subtitle = "Synthesize high-conversion peer referrals, hiring manager pitches & cadences",
              onClick = {
                viewModel.openReferralOutreachDialog()
                onDismissRequest()
              }
            )
          }

          item {
            PaletteActionItem(
              icon = Icons.Default.MonetizationOn,
              tint = TitanEmerald,
              title = "/negotiate • Salary Benchmarking & Negotiation Script Lab",
              subtitle = "Benchmark market percentiles (25th/50th/75th/90th) & synthesize AI negotiation scripts",
              onClick = {
                viewModel.openSalaryNegotiationDialog()
                onDismissRequest()
              }
            )
          }

          item {
            PaletteActionItem(
              icon = Icons.Default.PictureAsPdf,
              tint = TitanEmerald,
              title = "/health-report • Weekly Career Health Report (PDF Export)",
              subtitle = "Synthesizes Skills Radar & Career Velocity into a 2-page executive PDF briefing",
              onClick = {
                viewModel.refreshWeeklyCareerHealthReport()
                viewModel.showWeeklyHealthReportDialog.value = true
                onDismissRequest()
              }
            )
          }

          item {
            PaletteActionItem(
              icon = Icons.Default.Radar,
              tint = TitanEmerald,
              title = "/skills-radar • Skills Radar (Resume vs Target JDs)",
              subtitle = "Compare stored resume against target JD requirements, missing keywords & certifications",
              onClick = {
                viewModel.skillsTabActiveIndex.value = 4
                viewModel.navigateTo(TitanScreen.SKILLS_PROJECTS)
                onDismissRequest()
              }
            )
          }

          item {
            PaletteActionItem(
              icon = Icons.AutoMirrored.Filled.FactCheck,
              tint = TitanCyan,
              title = "/resume-match • Resume vs. Job Requirements Comparator",
              subtitle = "Benchmark resume text against target company criteria, gaps & bullet rewrites",
              onClick = {
                viewModel.setJobsScreenTab(4)
                viewModel.navigateTo(TitanScreen.JOBS)
                onDismissRequest()
              }
            )
          }

          item {
            PaletteActionItem(
              icon = Icons.Default.Hub,
              tint = TitanCyan,
              title = "/skill-mapping • Core Competency & Industry Mapping",
              subtitle = "Tag competencies against target sectors & analyze 2026 Gemini market skill gaps",
              onClick = {
                viewModel.skillsTabActiveIndex.value = 5
                viewModel.navigateTo(TitanScreen.SKILLS_PROJECTS)
                onDismissRequest()
              }
            )
          }

          item {
            PaletteActionItem(
              icon = Icons.Default.Speed,
              tint = TitanCyan,
              title = "/velocity • D3 Career Velocity Radar",
              subtitle = "D3.js visualization of target role requirements vs progress over time",
              onClick = {
                viewModel.navigateTo(TitanScreen.DASHBOARD)
                onDismissRequest()
              }
            )
          }

          item {
            PaletteActionItem(
              icon = Icons.Default.Flag,
              tint = TitanCyan,
              title = "/goals • Long-Term Milestones & Velocity",
              subtitle = "Track multi-year milestones, execution pacing & Recharts progress",
              onClick = {
                viewModel.navigateTo(TitanScreen.GOALS)
                onDismissRequest()
              }
            )
          }

          item {
            PaletteActionItem(
              icon = Icons.Default.Layers,
              tint = TitanGold,
              title = "/capabilities • 1,000+ Master Capability Matrix",
              subtitle = "Inspect and execute all 40 system capability families",
              onClick = {
                viewModel.setCapabilityMatrixOpen(true)
                onDismissRequest()
              }
            )
          }

          item {
            PaletteActionItem(
              icon = Icons.Default.RocketLaunch,
              tint = TitanEmerald,
              title = "/scenarios • 5-Year Career Simulator & Wealth Radar",
              subtitle = "Simulate compensation trajectories & business venture paths",
              onClick = {
                viewModel.setScenarioSimulatorOpen(true)
                onDismissRequest()
              }
            )
          }

          item {
            PaletteActionItem(
              icon = Icons.AutoMirrored.Filled.CompareArrows,
              tint = TitanGold,
              title = "/offers • Multi-Offer Comparative Decision Matrix",
              subtitle = "Weighted evaluation across base, variable, and 4-yr ESOPs",
              onClick = {
                viewModel.setMultiOfferComparatorOpen(true)
                onDismissRequest()
              }
            )
          }

          item {
            PaletteActionItem(
              icon = Icons.Default.HealthAndSafety,
              tint = TitanEmerald,
              title = "/resume-health • Resume Health Score & Standards Scanner",
              subtitle = "Scan uploaded resume text against current industry standards with actionable improvement fixes",
              onClick = {
                viewModel.openResumeHealthScanner()
                onDismissRequest()
              }
            )
          }

          item {
            PaletteActionItem(
              icon = Icons.Default.Description,
              tint = TitanCyan,
              title = "/resume • AI Document Parser & Resume Optimizer",
              subtitle = "Parse documents with local telemetry & run Gemini 3.5 Flash constructive critiques",
              onClick = {
                viewModel.openResumeOptimizer()
                onDismissRequest()
              }
            )
          }

          item {
            PaletteActionItem(
              icon = Icons.Default.AutoAwesome,
              tint = TitanIndigo,
              title = "/actionplan • Gemini AI Job Description Action Plan",
              subtitle = "Extract required skills, readiness gaps, and STAR interview talking points from any JD",
              onClick = {
                viewModel.openJobActionPlan()
                onDismissRequest()
              }
            )
          }

          item {
            PaletteActionItem(
              icon = Icons.Default.Description,
              tint = TitanGold,
              title = "/coverletter • Gemini Personalized Cover Letter Generator",
              subtitle = "Draft personalized cover letters with quantified metrics based on saved JD & resume profile",
              onClick = {
                viewModel.openCoverLetterGenerator()
                onDismissRequest()
              }
            )
          }

          item {
            PaletteActionItem(
              icon = Icons.Default.AutoAwesome,
              tint = TitanCyan,
              title = "/copilot • Personal Executive Strategist",
              subtitle = "Ask AI career copilot with full context graph",
              onClick = {
                viewModel.navigateTo(TitanScreen.COPILOT)
                onDismissRequest()
              }
            )
          }

          // System Tools
          item {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = "EXECUTIVE UTILITIES",
              style = MaterialTheme.typography.labelSmall,
              color = TextMutedDark,
              fontSize = 9.sp,
              fontWeight = FontWeight.Bold
            )
          }

          item {
            PaletteActionItem(
              icon = Icons.Default.AutoAwesome,
              tint = TitanCyan,
              title = "AI Job Description Analyzer",
              subtitle = "Paste raw JD to extract skills, evaluate candidate fit & generate talking points",
              onClick = {
                viewModel.navigateTo(TitanScreen.JOBS)
                viewModel.setJobsScreenTab(0)
                viewModel.showJobDescriptionAnalyzer.value = true
                onDismissRequest()
              }
            )
          }

          item {
            PaletteActionItem(
              icon = Icons.AutoMirrored.Filled.FactCheck,
              tint = TitanEmerald,
              title = "AI Resume & Job Matcher Lab",
              subtitle = "Paste job requirements for Match Score, ATS analysis & bullet improvements",
              onClick = {
                viewModel.openResumeComparison()
                onDismissRequest()
              }
            )
          }

          item {
            PaletteActionItem(
              icon = Icons.Default.MonetizationOn,
              tint = TitanEmerald,
              title = "Bengaluru CTC & Salary Modeler",
              subtitle = "Calculate take-home cash flow & counter-offer",
              onClick = {
                viewModel.setSalaryCalculatorOpen(true)
                onDismissRequest()
              }
            )
          }

          item {
            PaletteActionItem(
              icon = Icons.Default.Warning,
              tint = TitanCrimson,
              title = "Toggle Brutal Strategy Mode",
              subtitle = "Unfiltered critique & zero-BS executive standard",
              onClick = {
                viewModel.toggleBrutalMode()
                onDismissRequest()
              }
            )
          }

          // Target Companies
          if (filteredCompanies.isNotEmpty()) {
            item {
              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = "TARGET COMPANIES (TIER S / A)",
                style = MaterialTheme.typography.labelSmall,
                color = TextMutedDark,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
              )
            }

            items(filteredCompanies) { company ->
              PaletteActionItem(
                icon = Icons.Default.Business,
                tint = TitanCyan,
                title = company.name,
                subtitle = "${company.industry} • Tier ${company.tier} • ${company.activeOpeningsCount} openings",
                onClick = {
                  viewModel.navigateTo(TitanScreen.COMPANIES)
                  onDismissRequest()
                }
              )
            }
          }

          // High Fit Jobs
          if (filteredJobs.isNotEmpty()) {
            item {
              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = "HIGH-FIT JOB RADAR",
                style = MaterialTheme.typography.labelSmall,
                color = TextMutedDark,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
              )
            }

            items(filteredJobs) { job ->
              PaletteActionItem(
                icon = Icons.Default.Work,
                tint = TitanGold,
                title = job.title,
                subtitle = "${job.companyName} • ${job.adiFitScore}% Fit • ${job.salaryRange}",
                onClick = {
                  viewModel.navigateTo(TitanScreen.JOBS)
                  onDismissRequest()
                }
              )
            }
          }

          // Recruiter / Network contacts
          if (filteredContacts.isNotEmpty()) {
            item {
              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = "RECRUITER & NETWORK CONTACTS",
                style = MaterialTheme.typography.labelSmall,
                color = TextMutedDark,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
              )
            }

            items(filteredContacts) { contact ->
              PaletteActionItem(
                icon = Icons.Default.People,
                tint = TitanEmerald,
                title = contact.name,
                subtitle = "${contact.title} @ ${contact.companyName} • ${contact.relationshipState}",
                onClick = {
                  viewModel.navigateTo(TitanScreen.NETWORK)
                  onDismissRequest()
                }
              )
            }
          }

          // STAR Evidence
          if (filteredStories.isNotEmpty()) {
            item {
              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = "VERIFIED STAR STORIES",
                style = MaterialTheme.typography.labelSmall,
                color = TextMutedDark,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
              )
            }

            items(filteredStories) { story ->
              PaletteActionItem(
                icon = Icons.Default.Science,
                tint = TitanEmerald,
                title = story.title,
                subtitle = "Result: ${story.result.take(65)}...",
                onClick = {
                  viewModel.navigateTo(TitanScreen.INTERVIEWS)
                  onDismissRequest()
                }
              )
            }
          }
        }
      }
    },
    confirmButton = {}
  )
}

@Composable
fun PaletteActionItem(
  icon: ImageVector,
  tint: Color,
  title: String,
  subtitle: String,
  onClick: () -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onClick() },
    shape = RoundedCornerShape(8.dp),
    colors = CardDefaults.cardColors(containerColor = SlateElevated),
    border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(10.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(32.dp)
          .clip(RoundedCornerShape(6.dp))
          .background(tint.copy(alpha = 0.15f)),
        contentAlignment = Alignment.Center
      ) {
        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(16.dp))
      }

      Spacer(modifier = Modifier.width(10.dp))

      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = title,
          style = MaterialTheme.typography.bodyMedium,
          fontWeight = FontWeight.SemiBold,
          color = TextPrimaryDark,
          fontSize = 12.sp
        )
        Text(
          text = subtitle,
          style = MaterialTheme.typography.bodySmall,
          color = TextSecondaryDark,
          fontSize = 10.sp
        )
      }
    }
  }
}
