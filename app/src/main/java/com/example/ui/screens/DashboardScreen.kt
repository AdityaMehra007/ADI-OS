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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.automirrored.filled.FactCheck
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Search
import com.example.ui.components.AutomationKpiSummaryChart
import com.example.ui.components.CareerMomentumWidget
import com.example.ui.components.CompanyIntelligenceWidget
import com.example.ui.components.CareerProgressionRechartsDashboard
import com.example.ui.components.CareerVelocityDashboard
import com.example.ui.components.SkillsRadarDashboard
import com.example.ui.components.WeeklyCareerHealthReportCard
import com.example.ui.components.DailyCareerBriefingComponent
import com.example.ui.components.MorningExecutiveSummaryComponent
import com.example.ui.components.DailyBriefingActionSummary
import com.example.ui.components.ExecutiveRadarChart
import com.example.ui.components.NetworkGrowthChart
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
fun DashboardScreen(
  viewModel: TitanViewModel
) {
  val userProfile by viewModel.userProfile.collectAsState()
  val dailyBriefing by viewModel.dailyBriefing.collectAsState()
  val marketRadar by viewModel.marketRadar.collectAsState()
  val brutalMode = userProfile?.brutalStrategyMode ?: false

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(ObsidianDark)
      .padding(horizontal = 16.dp, vertical = 8.dp)
      .testTag("dashboard_screen"),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // 1. Executive Hero & Identity Graph Banner
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("executive_hero_card"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SlateCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (brutalMode) TitanCrimson.copy(alpha = 0.5f) else SlateBorder)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "OPERATING SYSTEM FOR ADI",
                style = MaterialTheme.typography.labelSmall,
                color = if (brutalMode) TitanCrimson else TitanCyan,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp
              )
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = "Adi • Global Career Command",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimaryDark
              )
            }

            // Career Readiness Badge
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(SlateElevated)
                .border(1.dp, SlateBorder, RoundedCornerShape(8.dp))
                .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
              Column(horizontalAlignment = Alignment.End) {
                Text(
                  text = "CAREER CAPITAL",
                  style = MaterialTheme.typography.labelSmall,
                  fontSize = 8.sp,
                  color = TextMutedDark
                )
                Text(
                  text = "${userProfile?.careerCapitalScore ?: 85}/100",
                  style = MaterialTheme.typography.labelMedium,
                  color = TitanEmerald,
                  fontWeight = FontWeight.Bold
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Profile metadata tags
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(SlateElevated)
                .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
              Icon(Icons.Default.LocationOn, contentDescription = null, tint = TitanCyan, modifier = Modifier.size(12.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text(userProfile?.location ?: "Bengaluru, India", style = MaterialTheme.typography.bodySmall, color = TextPrimaryDark)
            }

            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(SlateElevated)
                .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
              Icon(Icons.Default.School, contentDescription = null, tint = TitanGold, modifier = Modifier.size(12.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("BBA International Business", style = MaterialTheme.typography.bodySmall, color = TextPrimaryDark)
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          Text(
            text = "Targeting: Business Analyst • Strategy & Operations • AI Operations • BizDev Associate",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondaryDark,
            fontSize = 11.sp
          )
        }
      }
    }

    // 1a. Morning Executive Summary (Gemini API Powered Briefing)
    item {
      MorningExecutiveSummaryComponent(
        viewModel = viewModel,
        modifier = Modifier.fillMaxWidth()
      )
    }

    // 1b. Executive Competency Radar
    item {
      ExecutiveRadarChart()
    }

    // 1c-0. Career Progression Recharts Dashboard (Skill Acquisition Rates & Networking Growth Over Time)
    item {
      CareerProgressionRechartsDashboard(
        onNavigateToSkills = { viewModel.navigateTo(TitanScreen.SKILLS_PROJECTS) },
        onNavigateToNetwork = { viewModel.navigateTo(TitanScreen.NETWORK) }
      )
    }

    // 1c-0a. Weekly Career Health Report (Synthesizes Skills Radar & Career Velocity with PDF Export)
    item {
      WeeklyCareerHealthReportCard(
        viewModel = viewModel,
        modifier = Modifier.fillMaxWidth()
      )
    }

    // 1c-0b. Career Velocity Dashboard (D3-Powered: Progress vs Target Role Requirements Over Time)
    item {
      CareerVelocityDashboard(
        viewModel = viewModel,
        modifier = Modifier.fillMaxWidth()
      )
    }

    // 1c-0c. Skills Radar (Resume vs Target Job Description Requirements Audit)
    item {
      SkillsRadarDashboard(
        viewModel = viewModel,
        modifier = Modifier.fillMaxWidth()
      )
    }

    // 1c-0d. Resume vs Job Requirements Comparator (Gemini Audit)
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .clickable {
            viewModel.setJobsScreenTab(4)
            viewModel.navigateTo(TitanScreen.JOBS)
          }
          .testTag("dashboard_resume_comparator_card"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SlateCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, TitanCyan.copy(alpha = 0.4f))
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(TitanCyan.copy(alpha = 0.15f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.AutoMirrored.Filled.FactCheck,
                contentDescription = null,
                tint = TitanCyan,
                modifier = Modifier.size(20.dp)
              )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = "RESUME VS. JOB COMPARATOR",
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  color = TitanCyan,
                  letterSpacing = 0.8.sp
                )
                Spacer(modifier = Modifier.width(6.dp))
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(TitanEmerald.copy(alpha = 0.15f))
                    .padding(horizontal = 5.dp, vertical = 2.dp)
                ) {
                  Text("GEMINI 3.5", fontSize = 8.sp, fontWeight = FontWeight.Black, color = TitanEmerald)
                }
              }
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = "Target Company Requirement Benchmark",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimaryDark
              )
              Text(
                text = "Compare current resume text vs Zepto, Razorpay & Swiggy criteria to uncover skill gaps & bullet transforms.",
                fontSize = 11.sp,
                color = TextSecondaryDark,
                lineHeight = 15.sp
              )
            }
          }
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = "Open Comparator",
            tint = TitanCyan,
            modifier = Modifier.size(18.dp)
          )
        }
      }
    }

    // 1c. Professional Network Growth Trends (Recharts-style Visualizer)
    item {
      NetworkGrowthChart()
    }

    // 1c-2. Automation Agents KPI Performance (Recharts-style Visualizer: Applications Sent vs Response Rate)
    item {
      AutomationKpiSummaryChart()
    }

    // 1d. Career Strategy & Milestone Acquisition Roadmap Banner
    item {
      val careerRoadmap by viewModel.careerRoadmap.collectAsState()
      val isGeneratingRoadmap by viewModel.isGeneratingCareerRoadmap.collectAsState()

      Card(
        modifier = Modifier
          .fillMaxWidth()
          .clickable { viewModel.openCareerStrategyTab() }
          .testTag("dashboard_career_strategy_banner"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SlateCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, TitanCyan.copy(alpha = 0.45f))
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp)
        ) {
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
                  .background(TitanCyan.copy(alpha = 0.15f))
                  .border(1.dp, TitanCyan, CircleShape),
                contentAlignment = Alignment.Center
              ) {
                Icon(Icons.Default.Timeline, contentDescription = null, tint = TitanCyan, modifier = Modifier.size(16.dp))
              }
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text("CAREER STRATEGY & ROADMAP", color = TitanCyan, fontWeight = FontWeight.Bold, fontSize = 9.5.sp)
                  Spacer(modifier = Modifier.width(4.dp))
                  Box(
                    modifier = Modifier
                      .clip(RoundedCornerShape(4.dp))
                      .background(TitanGold.copy(alpha = 0.15f))
                      .padding(horizontal = 4.dp, vertical = 1.dp)
                  ) {
                    Text("GEMINI", color = TitanGold, fontSize = 7.5.sp, fontWeight = FontWeight.Bold)
                  }
                }
                Text(
                  text = careerRoadmap?.targetRole ?: "Senior Strategy & Ops Lead / Chief of Staff",
                  style = MaterialTheme.typography.titleSmall,
                  color = TextPrimaryDark,
                  fontWeight = FontWeight.Bold,
                  fontSize = 13.sp
                )
              }
            }

            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(TitanCyan.copy(alpha = 0.15f))
                .padding(horizontal = 6.dp, vertical = 3.dp)
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = "${careerRoadmap?.overallProgressPercentage ?: 48}% Acquired",
                  color = TitanCyan,
                  fontSize = 9.sp,
                  fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(2.dp))
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = TitanCyan, modifier = Modifier.size(10.dp))
              }
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          // Progress indicator
          LinearProgressIndicator(
            progress = { ((careerRoadmap?.overallProgressPercentage ?: 48) / 100f) },
            modifier = Modifier
              .fillMaxWidth()
              .height(5.dp)
              .clip(RoundedCornerShape(2.5.dp)),
            color = TitanEmerald,
            trackColor = SlateElevated
          )

          Spacer(modifier = Modifier.height(8.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "Phase 1: ${careerRoadmap?.phases?.getOrNull(0)?.title ?: "Foundation & Execution"}",
              style = MaterialTheme.typography.bodySmall,
              color = TextSecondaryDark,
              fontSize = 9.5.sp
            )
            Text(
              text = "${careerRoadmap?.acquiredMilestonesCount ?: 3}/${careerRoadmap?.totalMilestonesCount ?: 8} Milestones Tracked",
              style = MaterialTheme.typography.labelSmall,
              color = TitanGold,
              fontWeight = FontWeight.Bold,
              fontSize = 9.sp
            )
          }
        }
      }
    }

    // 1d-2. Executive Command Center Live Banner
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .clickable { viewModel.navigateTo(com.example.ui.viewmodel.TitanScreen.COMMAND_CENTER) }
          .testTag("dashboard_command_center_banner"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SlateCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, TitanGold.copy(alpha = 0.5f))
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(TitanGold.copy(alpha = 0.15f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Bolt,
                contentDescription = null,
                tint = TitanGold,
                modifier = Modifier.size(20.dp)
              )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = "EXECUTIVE COMMAND CENTER",
                  style = MaterialTheme.typography.labelSmall,
                  color = TitanGold,
                  fontWeight = FontWeight.Bold,
                  letterSpacing = 0.8.sp
                )
                Spacer(modifier = Modifier.width(6.dp))
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(TitanCyan.copy(alpha = 0.2f))
                    .padding(horizontal = 4.dp, vertical = 1.dp)
                ) {
                  Text(
                    text = "GEMINI 3.5",
                    style = MaterialTheme.typography.labelSmall,
                    color = TitanCyan,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                  )
                }
              }
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = "Live Career Intelligence & Quick-Access Control",
                style = MaterialTheme.typography.bodySmall,
                color = TextPrimaryDark,
                fontWeight = FontWeight.SemiBold
              )
            }
          }
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            tint = TitanGold,
            modifier = Modifier.size(18.dp)
          )
        }
      }
    }

    // 1d-3. Career Momentum Dashboard Widget (Daily Habits & Firestore Progress Bar)
    item {
      CareerMomentumWidget(
        viewModel = viewModel,
        modifier = Modifier.fillMaxWidth()
      )
    }

    // 1d-4. Company Intelligence Widget (Google Search Grounding News Synthesis)
    item {
      CompanyIntelligenceWidget(
        viewModel = viewModel,
        modifier = Modifier.fillMaxWidth(),
        onNavigateToFullIntel = { targetCompanyName ->
          viewModel.selectSavedCompanyForIntel(targetCompanyName, fetchNews = false)
          viewModel.navigateTo(com.example.ui.viewmodel.TitanScreen.COMPANIES)
        }
      )
    }

    // 1e. Quick Strategy Launchpad
    item {
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(10.dp))
              .background(SlateCard)
              .border(1.dp, TitanGold.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
              .clickable { viewModel.setCapabilityMatrixOpen(true) }
              .padding(10.dp)
              .testTag("dashboard_quick_capability_matrix"),
            contentAlignment = Alignment.Center
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Layers, contentDescription = null, tint = TitanGold, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Column {
                Text("CAPABILITIES", style = MaterialTheme.typography.labelSmall, color = TitanGold, fontWeight = FontWeight.Bold, fontSize = 9.sp)
                Text("1,000+ Engines", style = MaterialTheme.typography.bodySmall, color = TextPrimaryDark, fontSize = 10.sp)
              }
            }
          }

          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(10.dp))
              .background(SlateCard)
              .border(1.dp, TitanEmerald.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
              .clickable { viewModel.setScenarioSimulatorOpen(true) }
              .padding(10.dp)
              .testTag("dashboard_quick_scenario_simulator"),
            contentAlignment = Alignment.Center
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.RocketLaunch, contentDescription = null, tint = TitanEmerald, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Column {
                Text("SCENARIOS", style = MaterialTheme.typography.labelSmall, color = TitanEmerald, fontWeight = FontWeight.Bold, fontSize = 9.sp)
                Text("5-Yr Trajectory", style = MaterialTheme.typography.bodySmall, color = TextPrimaryDark, fontSize = 10.sp)
              }
            }
          }
        }

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(10.dp))
              .background(SlateCard)
              .border(1.dp, TitanEmerald.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
              .clickable { viewModel.setSalaryCalculatorOpen(true) }
              .padding(10.dp)
              .testTag("dashboard_quick_salary_modeler"),
            contentAlignment = Alignment.Center
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = TitanEmerald, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Column {
                Text("CTC MODELER", style = MaterialTheme.typography.labelSmall, color = TitanEmerald, fontWeight = FontWeight.Bold, fontSize = 9.sp)
                Text("₹ In-Hand & Tax", style = MaterialTheme.typography.bodySmall, color = TextPrimaryDark, fontSize = 10.sp)
              }
            }
          }

          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(10.dp))
              .background(SlateCard)
              .border(1.dp, TitanGold.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
              .clickable { viewModel.setMultiOfferComparatorOpen(true) }
              .padding(10.dp)
              .testTag("dashboard_quick_offer_comparator"),
            contentAlignment = Alignment.Center
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.AutoMirrored.Filled.CompareArrows, contentDescription = null, tint = TitanGold, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Column {
                Text("OFFER ENGINE", style = MaterialTheme.typography.labelSmall, color = TitanGold, fontWeight = FontWeight.Bold, fontSize = 9.sp)
                Text("Weighted Score", style = MaterialTheme.typography.bodySmall, color = TextPrimaryDark, fontSize = 10.sp)
              }
            }
          }

          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(10.dp))
              .background(SlateCard)
              .border(1.dp, TitanCyan.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
              .clickable { viewModel.setCommandPaletteOpen(true) }
              .padding(10.dp)
              .testTag("dashboard_quick_command_palette"),
            contentAlignment = Alignment.Center
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Search, contentDescription = null, tint = TitanCyan, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Column {
                Text("COMMAND HUD", style = MaterialTheme.typography.labelSmall, color = TitanCyan, fontWeight = FontWeight.Bold, fontSize = 9.sp)
                Text("Super-Search", style = MaterialTheme.typography.bodySmall, color = TextPrimaryDark, fontSize = 10.sp)
              }
            }
          }
        }

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(10.dp))
              .background(SlateCard)
              .border(1.dp, TitanCyan.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
              .clickable { viewModel.openResumeOptimizer() }
              .padding(10.dp)
              .testTag("dashboard_quick_resume_optimizer"),
            contentAlignment = Alignment.Center
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Description, contentDescription = null, tint = TitanCyan, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Column {
                Text("RESUME LAB", style = MaterialTheme.typography.labelSmall, color = TitanCyan, fontWeight = FontWeight.Bold, fontSize = 9.sp)
                Text("Doc Parser & AI", style = MaterialTheme.typography.bodySmall, color = TextPrimaryDark, fontSize = 10.sp)
              }
            }
          }

          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(10.dp))
              .background(SlateCard)
              .border(1.dp, TitanGold.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
              .clickable { viewModel.navigateTo(com.example.ui.viewmodel.TitanScreen.COMMAND_CENTER) }
              .padding(10.dp)
              .testTag("dashboard_quick_daily_insights"),
            contentAlignment = Alignment.Center
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = TitanGold, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Column {
                Text("DAILY TIPS", style = MaterialTheme.typography.labelSmall, color = TitanGold, fontWeight = FontWeight.Bold, fontSize = 9.sp)
                Text("AI Carousel", style = MaterialTheme.typography.bodySmall, color = TextPrimaryDark, fontSize = 10.sp)
              }
            }
          }
        }

        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(SlateCard)
            .border(1.dp, TitanIndigo.copy(alpha = 0.7f), RoundedCornerShape(10.dp))
            .clickable { viewModel.openJobActionPlan() }
            .padding(10.dp)
            .testTag("dashboard_quick_jd_action_plan"),
          contentAlignment = Alignment.Center
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(28.dp)
                  .background(TitanIndigo.copy(alpha = 0.3f), CircleShape),
                contentAlignment = Alignment.Center
              ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = TitanCyan, modifier = Modifier.size(16.dp))
              }
              Spacer(modifier = Modifier.width(8.dp))
              Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text("GEMINI JD ACTION PLAN", style = MaterialTheme.typography.labelSmall, color = TitanCyan, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                  Spacer(modifier = Modifier.width(6.dp))
                  Text("NEW", style = MaterialTheme.typography.labelSmall, color = TitanGold, fontWeight = FontWeight.ExtraBold, fontSize = 8.sp)
                }
                Text("Dissect any Job Description into Skills & STAR Talking Points", style = MaterialTheme.typography.bodySmall, color = TextPrimaryDark, fontSize = 11.sp)
              }
            }
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = TitanCyan, modifier = Modifier.size(16.dp))
          }
        }
      }
    }

    // 2. Daily Career Briefing Dashboard Component (Google Search Grounded)
    item {
      DailyCareerBriefingComponent(
        viewModel = viewModel,
        modifier = Modifier.fillMaxWidth()
      )
    }

    // 3. Daily Intelligence Briefing & High-Priority Directive
    item {
      dailyBriefing?.let { brief ->
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = SlateElevated),
          border = androidx.compose.foundation.BorderStroke(1.dp, TitanGold.copy(alpha = 0.4f))
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.NotificationsActive,
                  contentDescription = null,
                  tint = TitanGold,
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "DAILY EXECUTIVE BRIEFING",
                  style = MaterialTheme.typography.labelMedium,
                  color = TitanGold,
                  fontWeight = FontWeight.Bold
                )
              }
              Text(
                text = brief.dateString,
                style = MaterialTheme.typography.labelSmall,
                color = TextMutedDark
              )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
              text = brief.headline,
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = TextPrimaryDark
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
              text = brief.keyActions,
              style = MaterialTheme.typography.bodyMedium,
              color = TextSecondaryDark,
              lineHeight = 19.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Box(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(SlateCard)
                .padding(10.dp)
            ) {
              Row(verticalAlignment = Alignment.Top) {
                Icon(
                  Icons.Default.Bolt,
                  contentDescription = null,
                  tint = if (brutalMode) TitanCrimson else TitanCyan,
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                  Text(
                    text = if (brutalMode) "BRUTAL DIRECTIVE" else "STRATEGIC DIRECTIVE",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (brutalMode) TitanCrimson else TitanCyan,
                    fontWeight = FontWeight.Bold
                  )
                  Text(
                    text = brief.strategicDirective,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextPrimaryDark
                  )
                }
              }
            }
          }
        }
      }
    }

    // 3. Career Funnel Metrics (24 Tracked -> 4 Interviews -> 1 Offer)
    item {
      Column {
        Text(
          text = "CAREER PIPELINE FUNNEL",
          style = MaterialTheme.typography.labelSmall,
          color = TextMutedDark,
          fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(6.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          FunnelMetricCard(
            label = "Tracked",
            value = "24",
            subtext = "11 S/S+ Tiers",
            color = TitanCyan,
            modifier = Modifier.weight(1f)
          )
          FunnelMetricCard(
            label = "Applied",
            value = "4",
            subtext = "Official route",
            color = TitanIndigo,
            modifier = Modifier.weight(1f)
          )
          FunnelMetricCard(
            label = "Interviews",
            value = "2",
            subtext = "MSFT / Bain",
            color = TitanGold,
            modifier = Modifier.weight(1f)
          )
          FunnelMetricCard(
            label = "Offer",
            value = "1",
            subtext = "Razorpay ₹18.5L",
            color = TitanEmerald,
            modifier = Modifier.weight(1f)
          )
        }
      }
    }

    // 4. Today's Top Actions (Smart Priority Queue)
    item {
      Column {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "TODAY'S TOP ACTIONS",
            style = MaterialTheme.typography.labelSmall,
            color = TextMutedDark,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = "Ranked by ROI & Upside",
            style = MaterialTheme.typography.labelSmall,
            color = TitanCyan
          )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Action 1
        PriorityActionCard(
          priority = "P0 CRITICAL",
          title = "Microsoft Final Round Prep Drill",
          description = "Review Azure AI commercial business model & rehearse Family Business Supply Chain STAR story.",
          buttonLabel = "Launch Simulator",
          tagColor = TitanCrimson,
          onClick = { viewModel.navigateTo(TitanScreen.INTERVIEWS) }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Action 2
        PriorityActionCard(
          priority = "P1 HIGH",
          title = "Razorpay Offer Decision Matrix",
          description = "Evaluate ₹18.5L package, Koramangala commute, and early-career leadership against Microsoft timeline.",
          buttonLabel = "Review Decision",
          tagColor = TitanGold,
          onClick = { viewModel.navigateTo(TitanScreen.APPLICATIONS) }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Action 3
        PriorityActionCard(
          priority = "P2 STRATEGIC",
          title = "Alumni Referral Outreach for Google",
          description = "Dispatch tailored intro message to Siddharth Menon for Associate Account Strategist opening.",
          buttonLabel = "Compose Outreach",
          tagColor = TitanCyan,
          onClick = { viewModel.navigateTo(TitanScreen.NETWORK) }
        )
      }
    }

    // 5. Market Radar & Bengaluru Pulse
    item {
      Column {
        Text(
          text = "MARKET PULSE & HIRING RADAR",
          style = MaterialTheme.typography.labelSmall,
          color = TextMutedDark,
          fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        marketRadar.forEach { radar ->
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .padding(bottom = 6.dp),
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = SlateCard),
            border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = radar.metricName,
                  style = MaterialTheme.typography.titleMedium,
                  fontWeight = FontWeight.Bold,
                  color = TextPrimaryDark
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                  text = radar.insightSummary,
                  style = MaterialTheme.typography.bodySmall,
                  color = TextSecondaryDark,
                  fontSize = 11.sp
                )
              }

              Spacer(modifier = Modifier.width(10.dp))

              Column(horizontalAlignment = Alignment.End) {
                Text(
                  text = radar.value,
                  style = MaterialTheme.typography.labelMedium,
                  fontWeight = FontWeight.Bold,
                  color = TitanCyan
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                    contentDescription = null,
                    tint = TitanEmerald,
                    modifier = Modifier.size(12.dp)
                  )
                  Spacer(modifier = Modifier.width(2.dp))
                  Text(
                    text = radar.trend,
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 9.sp,
                    color = TitanEmerald
                  )
                }
              }
            }
          }
        }
      }
    }

    // 6. Quick Launch Command Modules
    item {
      Column {
        Text(
          text = "QUICK COMMAND MODULES",
          style = MaterialTheme.typography.labelSmall,
          color = TextMutedDark,
          fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          QuickModuleCard(
            title = "Job Matching",
            subtitle = "Active Universe",
            icon = Icons.Default.Work,
            accentColor = TitanCyan,
            modifier = Modifier.weight(1f),
            onClick = { viewModel.navigateTo(TitanScreen.JOBS) }
          )

          QuickModuleCard(
            title = "Interview OS",
            subtitle = "AI Scorecard",
            icon = Icons.Default.Psychology,
            accentColor = TitanGold,
            modifier = Modifier.weight(1f),
            onClick = { viewModel.navigateTo(TitanScreen.INTERVIEWS) }
          )

          QuickModuleCard(
            title = "ADI Copilot",
            subtitle = "Ask Anything",
            icon = Icons.Default.AutoAwesome,
            accentColor = TitanIndigo,
            modifier = Modifier.weight(1f),
            onClick = { viewModel.navigateTo(TitanScreen.COPILOT) }
          )
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(16.dp))
    }
  }
}

@Composable
fun FunnelMetricCard(
  label: String,
  value: String,
  subtext: String,
  color: Color,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier,
    shape = RoundedCornerShape(10.dp),
    colors = CardDefaults.cardColors(containerColor = SlateCard),
    border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
  ) {
    Column(
      modifier = Modifier.padding(10.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(
        text = label.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        color = TextMutedDark,
        fontSize = 9.sp
      )
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = value,
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.Black,
        color = color
      )
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = subtext,
        style = MaterialTheme.typography.labelSmall,
        color = TextSecondaryDark,
        fontSize = 8.sp,
        maxLines = 1
      )
    }
  }
}

@Composable
fun PriorityActionCard(
  priority: String,
  title: String,
  description: String,
  buttonLabel: String,
  tagColor: Color,
  onClick: () -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onClick() },
    shape = RoundedCornerShape(10.dp),
    colors = CardDefaults.cardColors(containerColor = SlateCard),
    border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
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
            .background(tagColor.copy(alpha = 0.15f))
            .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
          Text(
            text = priority,
            style = MaterialTheme.typography.labelSmall,
            color = tagColor,
            fontWeight = FontWeight.Bold,
            fontSize = 9.sp
          )
        }

        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.clickable { onClick() }
        ) {
          Text(
            text = buttonLabel,
            style = MaterialTheme.typography.labelSmall,
            color = TitanCyan,
            fontWeight = FontWeight.Bold
          )
          Spacer(modifier = Modifier.width(4.dp))
          Icon(
            Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            tint = TitanCyan,
            modifier = Modifier.size(12.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(6.dp))

      Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = TextPrimaryDark
      )

      Spacer(modifier = Modifier.height(2.dp))

      Text(
        text = description,
        style = MaterialTheme.typography.bodySmall,
        color = TextSecondaryDark,
        fontSize = 11.sp
      )
    }
  }
}

@Composable
fun QuickModuleCard(
  title: String,
  subtitle: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  accentColor: Color,
  modifier: Modifier = Modifier,
  onClick: () -> Unit
) {
  Card(
    modifier = modifier
      .clickable { onClick() },
    shape = RoundedCornerShape(10.dp),
    colors = CardDefaults.cardColors(containerColor = SlateElevated),
    border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
  ) {
    Column(
      modifier = Modifier.padding(10.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Box(
        modifier = Modifier
          .size(32.dp)
          .clip(CircleShape)
          .background(accentColor.copy(alpha = 0.15f)),
        contentAlignment = Alignment.Center
      ) {
        Icon(icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(16.dp))
      }
      Spacer(modifier = Modifier.height(6.dp))
      Text(
        text = title,
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Bold,
        color = TextPrimaryDark,
        fontSize = 10.sp
      )
      Text(
        text = subtitle,
        style = MaterialTheme.typography.labelSmall,
        color = TextMutedDark,
        fontSize = 8.sp
      )
    }
  }
}
