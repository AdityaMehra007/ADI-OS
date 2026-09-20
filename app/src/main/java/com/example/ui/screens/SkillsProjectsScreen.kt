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
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
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
import com.example.data.model.ProjectItem
import com.example.data.model.SkillItem
import com.example.ui.components.CareerProgressionRechartsDashboard
import com.example.ui.components.CareerStrategyView
import com.example.ui.components.SkillsRadarDashboard
import com.example.ui.components.SkillMappingInterfaceView
import com.example.ui.components.WeeklyCareerHealthReportCard
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
fun SkillsProjectsScreen(
  viewModel: TitanViewModel
) {
  val skills by viewModel.skills.collectAsState()
  val projects by viewModel.projects.collectAsState()
  val selectedTab by viewModel.skillsTabActiveIndex.collectAsState()

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(ObsidianDark)
      .padding(horizontal = 16.dp, vertical = 8.dp)
      .testTag("skills_projects_screen")
  ) {
    // Header
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Text(
          text = "CAREER CAPITAL & BUSINESS OS",
          style = MaterialTheme.typography.labelSmall,
          color = TitanCyan,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = "Proof-of-Work & Strategy Lab",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
          color = TextPrimaryDark
        )
      }

      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(8.dp))
          .background(SlateElevated)
          .border(1.dp, TitanEmerald.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
          .padding(horizontal = 8.dp, vertical = 4.dp)
      ) {
        Text("AI Multiplier: 94%", style = MaterialTheme.typography.labelSmall, color = TitanEmerald, fontWeight = FontWeight.Bold, fontSize = 9.sp)
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Scrollable Tabs
    ScrollableTabRow(
      selectedTabIndex = selectedTab,
      containerColor = SlateCard,
      edgePadding = 0.dp,
      indicator = { tabPositions ->
        if (selectedTab in tabPositions.indices) {
          TabRowDefaults.SecondaryIndicator(
            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
            color = TitanCyan
          )
        }
      }
    ) {
      Tab(
        selected = selectedTab == 0,
        onClick = { viewModel.skillsTabActiveIndex.value = 0 },
        text = {
          Text(
            "Skills & AI Multipliers (${skills.size})",
            color = if (selectedTab == 0) TitanCyan else TextSecondaryDark,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp
          )
        }
      )
      Tab(
        selected = selectedTab == 1,
        onClick = { viewModel.skillsTabActiveIndex.value = 1 },
        text = {
          Text(
            "Proof-of-Work Repos (${projects.size})",
            color = if (selectedTab == 1) TitanCyan else TextSecondaryDark,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp
          )
        }
      )
      Tab(
        selected = selectedTab == 2,
        onClick = { viewModel.skillsTabActiveIndex.value = 2 },
        text = {
          Text(
            "Business & Venture OS",
            color = if (selectedTab == 2) TitanGold else TextSecondaryDark,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp
          )
        }
      )
      Tab(
        selected = selectedTab == 3,
        onClick = { viewModel.skillsTabActiveIndex.value = 3 },
        text = {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Timeline, contentDescription = null, tint = if (selectedTab == 3) TitanCyan else TextSecondaryDark, modifier = Modifier.size(12.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              "Career Strategy",
              color = if (selectedTab == 3) TitanCyan else TextSecondaryDark,
              fontWeight = FontWeight.Bold,
              fontSize = 11.sp
            )
          }
        }
      )
      Tab(
        selected = selectedTab == 4,
        onClick = { viewModel.skillsTabActiveIndex.value = 4 },
        text = {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Radar, contentDescription = null, tint = if (selectedTab == 4) TitanCyan else TextSecondaryDark, modifier = Modifier.size(12.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              "Skills Radar",
              color = if (selectedTab == 4) TitanCyan else TextSecondaryDark,
              fontWeight = FontWeight.Bold,
              fontSize = 11.sp
            )
          }
        }
      )
      Tab(
        selected = selectedTab == 5,
        onClick = { viewModel.skillsTabActiveIndex.value = 5 },
        text = {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Hub, contentDescription = null, tint = if (selectedTab == 5) TitanCyan else TextSecondaryDark, modifier = Modifier.size(12.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              "Skill Mapping & Gaps",
              color = if (selectedTab == 5) TitanCyan else TextSecondaryDark,
              fontWeight = FontWeight.Bold,
              fontSize = 11.sp
            )
          }
        }
      )
    }

    Spacer(modifier = Modifier.height(12.dp))

    when (selectedTab) {
      0 -> {
        // Tab 0: Skills & AI Multipliers
        LazyColumn(
          modifier = Modifier.fillMaxSize(),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          // Industry Skill Mapping & Gemini Gap Audit Quick Navigation Banner
          item {
            Card(
              modifier = Modifier
                .fillMaxWidth()
                .clickable { viewModel.skillsTabActiveIndex.value = 5 },
              shape = RoundedCornerShape(10.dp),
              colors = CardDefaults.cardColors(containerColor = SlateCard),
              border = androidx.compose.foundation.BorderStroke(1.dp, TitanCyan.copy(alpha = 0.5f))
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
                  Icon(Icons.Default.Hub, contentDescription = null, tint = TitanCyan, modifier = Modifier.size(20.dp))
                  Spacer(modifier = Modifier.width(8.dp))
                  Column {
                    Text("INDUSTRY SKILL MAPPING & GEMINI GAPS", style = MaterialTheme.typography.labelSmall, color = TitanCyan, fontWeight = FontWeight.Bold, fontSize = 9.sp)
                    Text("Tag competencies against target sectors & analyze 2026 hiring trends", style = MaterialTheme.typography.bodySmall, color = TextPrimaryDark, fontSize = 10.sp)
                  }
                }
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = TitanCyan, modifier = Modifier.size(14.dp))
              }
            }
          }
          // Skill Gap Matrix Header
          item {
            Card(
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(10.dp),
              colors = CardDefaults.cardColors(containerColor = SlateElevated),
              border = androidx.compose.foundation.BorderStroke(1.dp, TitanCyan.copy(alpha = 0.4f))
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column {
                  Text("TIER-1 ATS READINESS INDEX", style = MaterialTheme.typography.labelSmall, color = TitanCyan, fontWeight = FontWeight.Bold, fontSize = 9.sp)
                  Text("96.8% Market Alignment", style = MaterialTheme.typography.titleSmall, color = TextPrimaryDark, fontWeight = FontWeight.Bold)
                }
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(TitanEmerald.copy(alpha = 0.2f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                  Text("0 CRITICAL GAPS", style = MaterialTheme.typography.labelSmall, color = TitanEmerald, fontWeight = FontWeight.Bold, fontSize = 9.sp)
                }
              }
            }
          }

          // Career Progression Metrics (Recharts-style: Skill Acquisition Rates & Network Growth Over Time)
          item {
            CareerProgressionRechartsDashboard(
              onNavigateToNetwork = { viewModel.navigateTo(TitanScreen.NETWORK) }
            )
          }

          // Skills Radar: Stored Resume vs Target JDs
          item {
            SkillsRadarDashboard(
              viewModel = viewModel,
              modifier = Modifier.fillMaxWidth()
            )
          }

          items(skills, key = { it.id }) { skill ->
            SkillCard(skill = skill)
          }
          item { Spacer(modifier = Modifier.height(16.dp)) }
        }
      }
      1 -> {
        // Tab 1: Proof of Work Projects & Repositories
        LazyColumn(
          modifier = Modifier.fillMaxSize(),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          item {
            Card(
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(10.dp),
              colors = CardDefaults.cardColors(containerColor = SlateElevated),
              border = androidx.compose.foundation.BorderStroke(1.dp, TitanGold.copy(alpha = 0.4f))
            ) {
              Column(modifier = Modifier.padding(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(Icons.Default.Bolt, contentDescription = null, tint = TitanGold, modifier = Modifier.size(16.dp))
                  Spacer(modifier = Modifier.width(6.dp))
                  Text("EVIDENCE-BACKED PROOF-OF-WORK VAULT", style = MaterialTheme.typography.labelSmall, color = TitanGold, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  "Every project is backed by verified business outcomes (e.g. 42% cycle time reduction), live SQL schemas, and interview-ready case teardowns.",
                  style = MaterialTheme.typography.bodySmall,
                  color = TextSecondaryDark,
                  fontSize = 10.sp
                )
              }
            }
          }

          items(projects, key = { it.id }) { proj ->
            ProjectCard(project = proj)
          }
          item { Spacer(modifier = Modifier.height(16.dp)) }
        }
      }
      2 -> {
        // Tab 2: Business & Venture OS (from 60-page PDF)
        LazyColumn(
          modifier = Modifier.fillMaxSize(),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          // B2B AI Operations Pipeline Card
          item {
            Card(
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(12.dp),
              colors = CardDefaults.cardColors(containerColor = SlateCard),
              border = androidx.compose.foundation.BorderStroke(1.dp, TitanGold.copy(alpha = 0.6f))
            ) {
              Column(modifier = Modifier.padding(14.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.RocketLaunch, contentDescription = null, tint = TitanGold, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("B2B AI Operations Consultancy", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                  }
                  Box(
                    modifier = Modifier
                      .clip(RoundedCornerShape(6.dp))
                      .background(TitanGold.copy(alpha = 0.2f))
                      .padding(horizontal = 6.dp, vertical = 2.dp)
                  ) {
                    Text("₹18L PIPELINE", style = MaterialTheme.typography.labelSmall, color = TitanGold, fontWeight = FontWeight.Bold, fontSize = 9.sp)
                  }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text("Enterprise AI analytics, supply chain database modernization, and executive dashboard automation for Indian mid-market enterprises.", style = MaterialTheme.typography.bodySmall, color = TextSecondaryDark, fontSize = 11.sp)

                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                  Box(
                    modifier = Modifier
                      .weight(1f)
                      .background(SlateElevated, RoundedCornerShape(8.dp))
                      .padding(8.dp)
                  ) {
                    Column {
                      Text("ACTIVE PILOTS", style = MaterialTheme.typography.labelSmall, color = TextMutedDark, fontSize = 8.sp)
                      Text("3 Enterprises", style = MaterialTheme.typography.titleSmall, color = TitanCyan, fontWeight = FontWeight.Bold)
                    }
                  }
                  Box(
                    modifier = Modifier
                      .weight(1f)
                      .background(SlateElevated, RoundedCornerShape(8.dp))
                      .padding(8.dp)
                  ) {
                    Column {
                      Text("GROSS MARGIN", style = MaterialTheme.typography.labelSmall, color = TextMutedDark, fontSize = 8.sp)
                      Text("85%+", style = MaterialTheme.typography.titleSmall, color = TitanEmerald, fontWeight = FontWeight.Bold)
                    }
                  }
                  Box(
                    modifier = Modifier
                      .weight(1f)
                      .background(SlateElevated, RoundedCornerShape(8.dp))
                      .padding(8.dp)
                  ) {
                    Column {
                      Text("DELIVERY CYCLE", style = MaterialTheme.typography.labelSmall, color = TextMutedDark, fontSize = 8.sp)
                      Text("14 Days", style = MaterialTheme.typography.titleSmall, color = TitanGold, fontWeight = FontWeight.Bold)
                    }
                  }
                }
              }
            }
          }

          // TAM / SAM Market Sizing Teardown
          item {
            Card(
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(10.dp),
              colors = CardDefaults.cardColors(containerColor = SlateCard),
              border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
            ) {
              Column(modifier = Modifier.padding(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(Icons.Default.PieChart, contentDescription = null, tint = TitanCyan, modifier = Modifier.size(16.dp))
                  Spacer(modifier = Modifier.width(6.dp))
                  Text("TAM / SAM COMMERCIAL SIZING MODEL (INDIA)", style = MaterialTheme.typography.labelSmall, color = TitanCyan, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text("• TAM (Total Addressable Market): ₹4,800 Cr (Indian logistics & manufacturing SME digitisation)", style = MaterialTheme.typography.bodySmall, color = TextSecondaryDark, fontSize = 10.sp)
                Text("• SAM (Serviceable Addressable Market): ₹750 Cr (Karnataka & Maharashtra mid-market distributors)", style = MaterialTheme.typography.bodySmall, color = TextSecondaryDark, fontSize = 10.sp)
                Text("• SOM (Serviceable Obtainable Market): ₹25 Cr (Target 0.5% market share in 36 months)", style = MaterialTheme.typography.bodySmall, color = TitanEmerald, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
              }
            }
          }

          // Outbound GTM Strategy & Pricing Card
          item {
            Card(
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(10.dp),
              colors = CardDefaults.cardColors(containerColor = SlateCard),
              border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
            ) {
              Column(modifier = Modifier.padding(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(Icons.AutoMirrored.Filled.TrendingUp, contentDescription = null, tint = TitanEmerald, modifier = Modifier.size(16.dp))
                  Spacer(modifier = Modifier.width(6.dp))
                  Text("GTM CADENCE & PRICING PACKAGES", style = MaterialTheme.typography.labelSmall, color = TitanEmerald, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text("1. Starter Automation Sprint: ₹1.5L (One-time 10-day SQL & dashboard deployment)", style = MaterialTheme.typography.bodySmall, color = TextPrimaryDark, fontSize = 10.sp)
                Text("2. Enterprise Retainer: ₹60,000/mo (Ongoing AI data ops & anomaly alerting)", style = MaterialTheme.typography.bodySmall, color = TextPrimaryDark, fontSize = 10.sp)
                Text("3. Outbound Strategy: 50 personalized Loom/dashboard video audits/month to warehouse owners", style = MaterialTheme.typography.bodySmall, color = TextSecondaryDark, fontSize = 10.sp)
              }
            }
          }

          item { Spacer(modifier = Modifier.height(16.dp)) }
        }
      }
      3 -> {
        // Tab 3: Executive Career Strategy & Milestone Acquisition Roadmap
        CareerStrategyView(
          viewModel = viewModel,
          modifier = Modifier.fillMaxSize()
        )
      }
      4 -> {
        // Tab 4: Skills Radar (Stored Resume vs Saved Target JDs) & Weekly Health Synthesis
        LazyColumn(
          modifier = Modifier.fillMaxSize(),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          item {
            WeeklyCareerHealthReportCard(
              viewModel = viewModel,
              modifier = Modifier.fillMaxWidth()
            )
          }
          item {
            SkillsRadarDashboard(
              viewModel = viewModel,
              modifier = Modifier.fillMaxWidth()
            )
          }
          item { Spacer(modifier = Modifier.height(20.dp)) }
        }
      }
      5 -> {
        // Tab 5: Core Competency & Industry Mapping with Gemini 2026 Skill Gap Audit
        LazyColumn(
          modifier = Modifier.fillMaxSize(),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          item {
            SkillMappingInterfaceView(
              viewModel = viewModel,
              modifier = Modifier.fillMaxWidth()
            )
          }
        }
      }
    }
  }
}

@Composable
fun SkillCard(skill: SkillItem) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("skill_card_${skill.id}"),
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
        Text(
          text = skill.name,
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
          color = TextPrimaryDark
        )

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(if (skill.isGap) TitanGold.copy(alpha = 0.15f) else TitanEmerald.copy(alpha = 0.15f))
            .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
          Text(
            text = if (skill.isGap) "IN-PROGRESS / GAP" else skill.proficiency,
            style = MaterialTheme.typography.labelSmall,
            color = if (skill.isGap) TitanGold else TitanEmerald,
            fontWeight = FontWeight.Bold,
            fontSize = 9.sp
          )
        }
      }

      Spacer(modifier = Modifier.height(6.dp))

      // AI Multiplier Box
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(6.dp))
          .background(SlateElevated)
          .padding(8.dp)
      ) {
        Row(verticalAlignment = Alignment.Top) {
          Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = TitanCyan, modifier = Modifier.size(14.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Column {
            Text("AI MULTIPLIER LEVERAGE:", style = MaterialTheme.typography.labelSmall, color = TitanCyan, fontSize = 8.sp, fontWeight = FontWeight.Bold)
            Text(skill.aiMultiplierDesc, style = MaterialTheme.typography.bodySmall, color = TextPrimaryDark, fontSize = 10.sp)
          }
        }
      }
    }
  }
}

@Composable
fun ProjectCard(project: ProjectItem) {
  val isVenture = project.projectType == "ADI_VENTURE_LAB"

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("project_card_${project.id}"),
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = SlateCard),
    border = androidx.compose.foundation.BorderStroke(1.dp, if (isVenture) TitanGold.copy(alpha = 0.5f) else SlateBorder)
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
          Icon(
            imageVector = if (isVenture) Icons.Default.RocketLaunch else Icons.Default.Code,
            contentDescription = null,
            tint = if (isVenture) TitanGold else TitanCyan,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = project.title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = TextPrimaryDark
          )
        }

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(if (isVenture) TitanGold.copy(alpha = 0.15f) else TitanCyan.copy(alpha = 0.15f))
            .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
          Text(
            text = if (isVenture) "VENTURE LAB" else "PROJECT LAB",
            style = MaterialTheme.typography.labelSmall,
            color = if (isVenture) TitanGold else TitanCyan,
            fontWeight = FontWeight.Bold,
            fontSize = 9.sp
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = "PROBLEM & SOLUTION:",
        style = MaterialTheme.typography.labelSmall,
        color = TextMutedDark,
        fontSize = 9.sp,
        fontWeight = FontWeight.Bold
      )
      Text(
        text = "${project.problem} ${project.solution}",
        style = MaterialTheme.typography.bodySmall,
        color = TextSecondaryDark,
        fontSize = 11.sp
      )

      Spacer(modifier = Modifier.height(6.dp))

      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(6.dp))
          .background(SlateElevated)
          .padding(8.dp)
      ) {
        Column {
          Text("BUSINESS IMPACT & TARGET ROI:", style = MaterialTheme.typography.labelSmall, color = TitanEmerald, fontSize = 8.sp, fontWeight = FontWeight.Bold)
          Text(project.businessImpact, style = MaterialTheme.typography.bodySmall, color = TitanEmerald, fontSize = 10.sp)
        }
      }
    }
  }
}
