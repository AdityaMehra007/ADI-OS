package com.example.ui.screens

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
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Application
import com.example.data.model.DailyCareerTip
import com.example.data.model.ExecutiveCareerInsights
import com.example.data.model.ExecutiveInsightPillar
import com.example.data.model.ExecutiveMarketOpportunity
import com.example.ui.components.CareerMomentumWidget
import com.example.ui.components.CompanyIntelligenceWidget
import com.example.ui.components.CareerRoadmapSkillsVisualization
import kotlinx.coroutines.launch
import com.example.ui.theme.ObsidianDark
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateCard
import com.example.ui.theme.SlateDarker
import com.example.ui.theme.SlateElevated
import com.example.ui.theme.TextMutedDark
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import com.example.ui.theme.TitanCrimson
import com.example.ui.theme.TitanCyan
import com.example.ui.theme.TitanCyanGlow
import com.example.ui.theme.TitanEmerald
import com.example.ui.theme.TitanGold
import com.example.ui.theme.TitanGoldLight
import com.example.ui.theme.TitanIndigo
import com.example.ui.theme.TitanRose
import com.example.ui.theme.TitanViolet
import com.example.ui.viewmodel.TitanViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ExecutiveCommandCenterScreen(viewModel: TitanViewModel) {
  val uiState by viewModel.executiveCommandCenterUiState.collectAsState()
  val userProfile by viewModel.userProfile.collectAsState()
  val insights = uiState.insights

  val targetRoles = listOf(
    "Lead Strategy & Operations / Chief of Staff",
    "AI Operations & Automation Lead",
    "Product Operations & Fulfillment Lead",
    "Founder's Associate / VP BizOps"
  )

  val completedActions = remember { mutableStateListOf<Int>() }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(ObsidianDark)
      .padding(horizontal = 16.dp, vertical = 12.dp)
      .testTag("executive_command_center_screen"),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // 1. HERO HEADER: Executive Title, Status & Gemini Grounding Pill
    item {
      ExecutiveHeaderSection(
        uiState = uiState,
        insights = insights,
        onRefresh = { viewModel.fetchExecutiveInsights(forceRefresh = true) }
      )
    }

    // 2. TARGET ROLE SELECTOR CHIPS
    item {
      RoleSelectorSection(
        selectedRole = uiState.targetRoleFilter,
        roleOptions = targetRoles,
        onRoleSelected = { role -> viewModel.setExecutiveTargetRole(role) }
      )
    }

    // 2b. SWIPEABLE CAROUSEL: DAILY CAREER INSIGHTS (GEMINI API)
    item {
      DailyCareerInsightsCarouselSection(
        dailyTips = insights?.dailyTips,
        viewModel = viewModel,
        onRefreshTips = { viewModel.fetchExecutiveInsights(forceRefresh = true) }
      )
    }

    // 2c. CAREER MOMENTUM DASHBOARD WIDGET (TRACKS DAILY HABITS & FIRESTORE PROGRESS BAR)
    item {
      CareerMomentumWidget(
        viewModel = viewModel,
        modifier = Modifier.fillMaxWidth()
      )
    }

    // 2d. COMPANY INTELLIGENCE WIDGET (MATERIAL3 CARDS + GOOGLE SEARCH GROUNDING)
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

    // 3. QUICK-ACCESS ACTION BUTTONS (EXECUTIVE LAUNCHPAD)
    item {
      QuickAccessLaunchpadSection(viewModel = viewModel)
    }

    // 4. LOADING STATE OR CONTENT
    if (uiState.isLoading) {
      item {
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .testTag("loading_insights_card"),
          colors = CardDefaults.cardColors(containerColor = SlateCard),
          border = androidx.compose.foundation.BorderStroke(1.dp, TitanCyan.copy(alpha = 0.5f))
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
          ) {
            CircularProgressIndicator(
              modifier = Modifier.size(24.dp),
              color = TitanCyan,
              strokeWidth = 2.5.dp
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
              Text(
                text = "Synthesizing Live Career Intelligence via Gemini API...",
                style = MaterialTheme.typography.titleSmall,
                color = TextPrimaryDark,
                fontWeight = FontWeight.SemiBold
              )
              Text(
                text = "Querying modern preview models (gemini-3.5-flash) with verified profile & telemetry...",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondaryDark
              )
            }
          }
        }
      }
    }

    // 5. ERROR BANNER (IF ANY)
    if (uiState.error != null && !uiState.isLoading) {
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          colors = CardDefaults.cardColors(containerColor = TitanCrimson.copy(alpha = 0.15f)),
          border = androidx.compose.foundation.BorderStroke(1.dp, TitanCrimson.copy(alpha = 0.5f))
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(
              modifier = Modifier.weight(1f),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(Icons.Default.Warning, contentDescription = null, tint = TitanCrimson)
              Spacer(modifier = Modifier.width(10.dp))
              Text(
                text = uiState.error ?: "Unable to fetch live insights",
                color = TextPrimaryDark,
                style = MaterialTheme.typography.bodySmall
              )
            }
            OutlinedButton(
              onClick = { viewModel.fetchExecutiveInsights(forceRefresh = true) },
              border = androidx.compose.foundation.BorderStroke(1.dp, TitanCrimson)
            ) {
              Text("Retry", color = TitanCrimson, fontSize = 12.sp)
            }
          }
        }
      }
    }

    if (insights != null) {
      // 6. EXECUTIVE VERDICT & LEVERAGE HERO CARD
      item {
        ExecutiveVerdictCard(insights = insights)
      }

      // 7. TAB NAVIGATION: Executive Briefing | Intelligence Feeds | Career Goals | Job Automation | Strategic Pillars | Market Opportunities | Tactical Playbook
      item {
        ExecutiveTabsNavigation(
          activeTab = uiState.activeTab,
          onTabSelected = { tab -> viewModel.setExecutiveCenterTab(tab) }
        )
      }

      // 8. TAB CONTENT
      when (uiState.activeTab) {
        0 -> {
          // Tab 0: Executive Briefing
          item {
            ExecutiveBriefingTab(insights = insights)
          }
        }
        1 -> {
          // Tab 1: Intelligence Feeds (Real-time Live Grounded & Market News)
          item {
            ExecutiveIntelligenceFeedsTab(viewModel = viewModel)
          }
        }
        2 -> {
          // Tab 2: Career Goals (Executive Milestones, OKRs & Progress)
          item {
            ExecutiveCareerGoalsTab(viewModel = viewModel)
          }
        }
        3 -> {
          // Tab 3: Job Automation Tasks (Active Agent Pipelines & Smart Discovery)
          item {
            ExecutiveJobAutomationTasksTab(viewModel = viewModel)
          }
        }
        4 -> {
          // Tab 4: Strategic Pillars
          item {
            StrategicPillarsTab(pillars = insights.strategicPillars)
          }
        }
        5 -> {
          // Tab 5: Market Opportunities
          item {
            MarketOpportunitiesTab(
              opportunities = insights.topMarketOpportunities,
              viewModel = viewModel
            )
          }
        }
        6 -> {
          // Tab 6: Tactical Playbook & Interview Vectors
          item {
            TacticalPlaybookTab(
              insights = insights,
              completedActions = completedActions,
              onToggleAction = { index ->
                if (completedActions.contains(index)) {
                  completedActions.remove(index)
                } else {
                  completedActions.add(index)
                }
              },
              viewModel = viewModel
            )
          }
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}

// ==============================================================================================
// 1. HEADER SECTION
// ==============================================================================================
@Composable
private fun ExecutiveHeaderSection(
  uiState: com.example.data.model.ExecutiveCommandCenterUiState,
  insights: ExecutiveCareerInsights?,
  onRefresh: () -> Unit
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Column {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier = Modifier
            .size(10.dp)
            .clip(CircleShape)
            .background(TitanEmerald)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "EXECUTIVE COMMAND CENTER",
          style = MaterialTheme.typography.labelSmall,
          color = TitanCyan,
          fontWeight = FontWeight.Bold,
          letterSpacing = 1.2.sp
        )
      }
      Text(
        text = "Career Intelligence OS",
        style = MaterialTheme.typography.titleLarge,
        color = TextPrimaryDark,
        fontWeight = FontWeight.Bold
      )
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
      // Gemini Model indicator badge
      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(20.dp))
          .background(TitanGold.copy(alpha = 0.15f))
          .border(1.dp, TitanGold.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
          .padding(horizontal = 10.dp, vertical = 5.dp)
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.AutoAwesome,
            contentDescription = null,
            tint = TitanGold,
            modifier = Modifier.size(12.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = insights?.modelUsed ?: "gemini-3.5-flash",
            style = MaterialTheme.typography.labelSmall,
            color = TitanGold,
            fontWeight = FontWeight.SemiBold,
            fontSize = 11.sp
          )
        }
      }

      Spacer(modifier = Modifier.width(8.dp))

      // Refresh Button
      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(8.dp))
          .background(SlateElevated)
          .border(1.dp, SlateBorder, RoundedCornerShape(8.dp))
          .clickable(enabled = !uiState.isLoading, onClick = onRefresh)
          .padding(8.dp)
          .testTag("btn_refresh_insights"),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.Refresh,
          contentDescription = "Refresh Gemini Insights",
          tint = if (uiState.isLoading) TextMutedDark else TitanCyan,
          modifier = Modifier.size(18.dp)
        )
      }
    }
  }
}

// ==============================================================================================
// 2. ROLE SELECTOR SECTION
// ==============================================================================================
@Composable
private fun RoleSelectorSection(
  selectedRole: String,
  roleOptions: List<String>,
  onRoleSelected: (String) -> Unit
) {
  val scrollState = rememberScrollState()
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .horizontalScroll(scrollState),
    horizontalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    roleOptions.forEach { role ->
      val isSelected = selectedRole == role
      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(20.dp))
          .background(if (isSelected) TitanCyan.copy(alpha = 0.2f) else SlateCard)
          .border(
            1.dp,
            if (isSelected) TitanCyan else SlateBorder,
            RoundedCornerShape(20.dp)
          )
          .clickable { onRoleSelected(role) }
          .padding(horizontal = 14.dp, vertical = 7.dp)
      ) {
        Text(
          text = role,
          style = MaterialTheme.typography.labelSmall,
          color = if (isSelected) TitanCyan else TextSecondaryDark,
          fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
          fontSize = 12.sp
        )
      }
    }
  }
}

// ==============================================================================================
// 3. QUICK-ACCESS LAUNCHPAD SECTION (ACTION BUTTONS)
// ==============================================================================================
@Composable
private fun QuickAccessLaunchpadSection(viewModel: TitanViewModel) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("quick_access_launchpad"),
    colors = CardDefaults.cardColors(containerColor = SlateCard),
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
            imageVector = Icons.Default.Bolt,
            contentDescription = null,
            tint = TitanCyan,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "EXECUTIVE QUICK-ACCESS ACTIONS",
            style = MaterialTheme.typography.labelSmall,
            color = TextPrimaryDark,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.8.sp
          )
        }
        Text(
          text = "Direct Multi-Agent Triggers",
          style = MaterialTheme.typography.labelSmall,
          color = TextMutedDark,
          fontSize = 11.sp
        )
      }

      Spacer(modifier = Modifier.height(12.dp))

      // 4x2 Grid of Quick-Access Action Buttons
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        QuickActionButton(
          modifier = Modifier.weight(1f),
          title = "Scenario Sim",
          subtitle = "3-Year CTC Trajectory",
          icon = Icons.Default.RocketLaunch,
          accentColor = TitanEmerald,
          testTag = "action_run_simulator",
          onClick = { viewModel.quickActionRunSimulator() }
        )
        QuickActionButton(
          modifier = Modifier.weight(1f),
          title = "Resume Optimizer",
          subtitle = "Gemini Doc Parser",
          icon = Icons.Default.Description,
          accentColor = TitanCyan,
          testTag = "action_resume_optimizer",
          onClick = { viewModel.openResumeOptimizer() }
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        QuickActionButton(
          modifier = Modifier.weight(1f),
          title = "Interview OS",
          subtitle = "Dark Store Sim Drill",
          icon = Icons.Default.Psychology,
          accentColor = TitanGold,
          testTag = "action_mock_interview",
          onClick = { viewModel.quickActionLaunchInterview() }
        )
        QuickActionButton(
          modifier = Modifier.weight(1f),
          title = "Company Intel",
          subtitle = "360° Grounded Dossier",
          icon = Icons.Default.Business,
          accentColor = TitanViolet,
          testTag = "action_company_intel",
          onClick = { viewModel.quickActionResearchCompany("Zepto") }
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        QuickActionButton(
          modifier = Modifier.weight(1f),
          title = "Matrix Intel",
          subtitle = "Full Capability Map",
          icon = Icons.Default.Layers,
          accentColor = TitanIndigo,
          testTag = "action_capability_matrix",
          onClick = { viewModel.quickActionOpenCapabilityMatrix() }
        )
        QuickActionButton(
          modifier = Modifier.weight(1f),
          title = "Offer Compare",
          subtitle = "Multi-Offer Staging",
          icon = Icons.AutoMirrored.Filled.CompareArrows,
          accentColor = TitanRose,
          testTag = "action_multi_offer",
          onClick = { viewModel.quickActionOpenMultiOfferComparator() }
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        QuickActionButton(
          modifier = Modifier.weight(1f),
          title = "Cloud Vault",
          subtitle = "Firebase Cross-Sync",
          icon = Icons.Default.CloudUpload,
          accentColor = TitanCyan,
          testTag = "action_cloud_sync",
          onClick = { viewModel.quickActionCloudSync() }
        )
        QuickActionButton(
          modifier = Modifier.weight(1f),
          title = "Ask Copilot",
          subtitle = "Deep Executive Session",
          icon = Icons.Default.AutoAwesome,
          accentColor = TitanGold,
          testTag = "action_copilot",
          onClick = { viewModel.navigateTo(com.example.ui.viewmodel.TitanScreen.COPILOT) }
        )
      }
    }
  }
}

@Composable
private fun QuickActionButton(
  modifier: Modifier = Modifier,
  title: String,
  subtitle: String,
  icon: ImageVector,
  accentColor: Color,
  testTag: String,
  onClick: () -> Unit
) {
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(10.dp))
      .background(SlateElevated)
      .border(1.dp, SlateBorder, RoundedCornerShape(10.dp))
      .clickable(onClick = onClick)
      .padding(horizontal = 10.dp, vertical = 8.dp)
      .testTag(testTag)
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.fillMaxWidth()
    ) {
      Box(
        modifier = Modifier
          .size(30.dp)
          .clip(RoundedCornerShape(8.dp))
          .background(accentColor.copy(alpha = 0.15f)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = accentColor,
          modifier = Modifier.size(16.dp)
        )
      }
      Spacer(modifier = Modifier.width(8.dp))
      Column {
        Text(
          text = title,
          style = MaterialTheme.typography.labelSmall,
          color = TextPrimaryDark,
          fontWeight = FontWeight.Bold,
          fontSize = 12.sp,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
        Text(
          text = subtitle,
          style = MaterialTheme.typography.bodySmall,
          color = TextSecondaryDark,
          fontSize = 10.sp,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
      }
    }
  }
}

// ==============================================================================================
// 6. EXECUTIVE VERDICT HERO CARD
// ==============================================================================================
@Composable
private fun ExecutiveVerdictCard(insights: ExecutiveCareerInsights) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("executive_verdict_card"),
    colors = CardDefaults.cardColors(containerColor = SlateCard),
    border = androidx.compose.foundation.BorderStroke(1.dp, TitanCyan.copy(alpha = 0.4f))
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      // Top row: Headline & Readiness Badge
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = "GEMINI STRATEGIC APPRAISAL",
            style = MaterialTheme.typography.labelSmall,
            color = TitanGold,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = insights.headline,
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimaryDark,
            fontWeight = FontWeight.Bold
          )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Readiness Score badge
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(TitanEmerald.copy(alpha = 0.15f))
            .border(1.dp, TitanEmerald.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp),
          contentAlignment = Alignment.Center
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
              text = "${insights.overallReadinessScore}/100",
              style = MaterialTheme.typography.titleSmall,
              color = TitanEmerald,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = "Readiness",
              style = MaterialTheme.typography.labelSmall,
              color = TitanEmerald.copy(alpha = 0.8f),
              fontSize = 9.sp
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Verdict body text
      Text(
        text = insights.executiveVerdict,
        style = MaterialTheme.typography.bodySmall,
        color = TextSecondaryDark,
        lineHeight = 19.sp
      )

      Spacer(modifier = Modifier.height(12.dp))
      HorizontalDivider(color = SlateBorder.copy(alpha = 0.6f))
      Spacer(modifier = Modifier.height(12.dp))

      // Market Positioning & Target Compensation Strip
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = "POSITIONING ARCHETYPE",
            style = MaterialTheme.typography.labelSmall,
            color = TextMutedDark,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold
          )
          Text(
            text = insights.marketPositioningStatement,
            style = MaterialTheme.typography.bodySmall,
            color = TitanCyan,
            fontWeight = FontWeight.Medium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
          )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(horizontalAlignment = Alignment.End) {
          Text(
            text = "BENCHMARK CTC BAND",
            style = MaterialTheme.typography.labelSmall,
            color = TextMutedDark,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold
          )
          Text(
            text = insights.targetCompensationRange,
            style = MaterialTheme.typography.titleSmall,
            color = TitanGold,
            fontWeight = FontWeight.Bold
          )
        }
      }
    }
  }
}

// ==============================================================================================
// 7. TAB NAVIGATION (MATERIAL 3 SCROLLABLE TAB ROW / PILLS)
// ==============================================================================================
private data class CommandTabItem(
  val title: String,
  val icon: ImageVector,
  val badgeText: String? = null
)

@Composable
private fun ExecutiveTabsNavigation(
  activeTab: Int,
  onTabSelected: (Int) -> Unit
) {
  val tabs = listOf(
    CommandTabItem("Executive Briefing", Icons.Default.Security),
    CommandTabItem("Intelligence Feeds", Icons.Default.Newspaper, "LIVE"),
    CommandTabItem("Career Goals", Icons.Default.Flag),
    CommandTabItem("Job Automation", Icons.Default.Tune, "AI"),
    CommandTabItem("Strategic Pillars", Icons.Default.Layers),
    CommandTabItem("Market Opportunities", Icons.AutoMirrored.Filled.TrendingUp),
    CommandTabItem("Tactical Playbook", Icons.AutoMirrored.Filled.Assignment)
  )

  val scrollState = rememberScrollState()

  Row(
    modifier = Modifier
      .fillMaxWidth()
      .horizontalScroll(scrollState),
    horizontalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    tabs.forEachIndexed { index, tabItem ->
      val isSelected = activeTab == index
      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(10.dp))
          .background(
            if (isSelected) {
              Brush.horizontalGradient(
                colors = listOf(SlateElevated, SlateElevated.copy(alpha = 0.9f))
              )
            } else {
              Brush.horizontalGradient(
                colors = listOf(SlateCard, SlateCard)
              )
            }
          )
          .border(
            1.dp,
            if (isSelected) TitanCyan else SlateBorder.copy(alpha = 0.7f),
            RoundedCornerShape(10.dp)
          )
          .clickable { onTabSelected(index) }
          .padding(horizontal = 14.dp, vertical = 9.dp)
          .testTag("tab_${index}")
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Icon(
            imageVector = tabItem.icon,
            contentDescription = null,
            tint = if (isSelected) TitanCyan else TextMutedDark,
            modifier = Modifier.size(15.dp)
          )
          Text(
            text = tabItem.title,
            style = MaterialTheme.typography.labelSmall,
            color = if (isSelected) TextPrimaryDark else TextSecondaryDark,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
          )
          if (tabItem.badgeText != null) {
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(
                  if (tabItem.badgeText == "LIVE") TitanEmerald.copy(alpha = 0.2f)
                  else TitanCyan.copy(alpha = 0.2f)
                )
                .padding(horizontal = 4.dp, vertical = 1.dp)
            ) {
              Text(
                text = tabItem.badgeText,
                style = MaterialTheme.typography.labelSmall,
                color = if (tabItem.badgeText == "LIVE") TitanEmerald else TitanCyan,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 8.sp
              )
            }
          }
        }
      }
    }
  }
}

// ==============================================================================================
// 8. TAB 0: EXECUTIVE BRIEFING TAB (MOATS & BLINDSPOTS)
// ==============================================================================================
@Composable
private fun ExecutiveBriefingTab(insights: ExecutiveCareerInsights) {
  Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
    // Competitive Moats
    Card(
      modifier = Modifier.fillMaxWidth(),
      colors = CardDefaults.cardColors(containerColor = SlateCard),
      border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
    ) {
      Column(modifier = Modifier.padding(14.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.Security,
            contentDescription = null,
            tint = TitanEmerald,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "VERIFIED COMPETITIVE MOATS",
            style = MaterialTheme.typography.labelSmall,
            color = TitanEmerald,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.8.sp
          )
        }
        Spacer(modifier = Modifier.height(10.dp))
        insights.competitiveMoats.forEach { moat ->
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 4.dp),
            verticalAlignment = Alignment.Top
          ) {
            Icon(
              imageVector = Icons.Default.CheckCircle,
              contentDescription = null,
              tint = TitanEmerald,
              modifier = Modifier
                .size(14.dp)
                .padding(top = 2.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = moat,
              style = MaterialTheme.typography.bodySmall,
              color = TextPrimaryDark,
              lineHeight = 18.sp
            )
          }
        }
      }
    }

    // Critical Risks & Blindspots
    Card(
      modifier = Modifier.fillMaxWidth(),
      colors = CardDefaults.cardColors(containerColor = SlateCard),
      border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
    ) {
      Column(modifier = Modifier.padding(14.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = null,
            tint = TitanGold,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "STRATEGIC RISKS & BLINDSPOTS (MITIGATION REQUIRED)",
            style = MaterialTheme.typography.labelSmall,
            color = TitanGold,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.8.sp
          )
        }
        Spacer(modifier = Modifier.height(10.dp))
        insights.criticalBlindspots.forEach { blindspot ->
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 4.dp),
            verticalAlignment = Alignment.Top
          ) {
            Box(
              modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(TitanGold)
                .padding(top = 6.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
              text = blindspot,
              style = MaterialTheme.typography.bodySmall,
              color = TextSecondaryDark,
              lineHeight = 18.sp
            )
          }
        }
      }
    }
  }
}

// ==============================================================================================
// 9. TAB 1: STRATEGIC PILLARS TAB
// ==============================================================================================
@Composable
private fun StrategicPillarsTab(pillars: List<ExecutiveInsightPillar>) {
  Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
    pillars.forEach { pillar ->
      Card(
        modifier = Modifier.fillMaxWidth(),
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
              text = pillar.title,
              style = MaterialTheme.typography.titleSmall,
              color = TextPrimaryDark,
              fontWeight = FontWeight.Bold
            )
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(TitanCyan.copy(alpha = 0.15f))
                .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
              Text(
                text = pillar.benchmarkTier,
                style = MaterialTheme.typography.labelSmall,
                color = TitanCyan,
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.sp
              )
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          // Score bar
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
          ) {
            LinearProgressIndicator(
              progress = { pillar.score / 100f },
              modifier = Modifier
                .weight(1f)
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
              color = if (pillar.score >= 90) TitanEmerald else if (pillar.score >= 80) TitanCyan else TitanGold,
              trackColor = SlateElevated,
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
              text = "${pillar.score}%",
              style = MaterialTheme.typography.labelSmall,
              color = TextPrimaryDark,
              fontWeight = FontWeight.Bold
            )
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Strength
          Text(
            text = "Core Advantage:",
            style = MaterialTheme.typography.labelSmall,
            color = TextMutedDark,
            fontSize = 10.sp
          )
          Text(
            text = pillar.keyStrength,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondaryDark,
            fontSize = 12.sp
          )

          Spacer(modifier = Modifier.height(6.dp))

          // Strategic Imperative
          Text(
            text = "Strategic Imperative:",
            style = MaterialTheme.typography.labelSmall,
            color = TextMutedDark,
            fontSize = 10.sp
          )
          Text(
            text = pillar.strategicImperative,
            style = MaterialTheme.typography.bodySmall,
            color = TitanGoldLight,
            fontSize = 12.sp
          )
        }
      }
    }
  }
}

// ==============================================================================================
// 10. TAB 2: MARKET OPPORTUNITIES TAB
// ==============================================================================================
@Composable
private fun MarketOpportunitiesTab(
  opportunities: List<ExecutiveMarketOpportunity>,
  viewModel: TitanViewModel
) {
  Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
    opportunities.forEach { opp ->
      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SlateCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          // Company, Velocity & Fit Score
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = opp.companyName,
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimaryDark,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = opp.targetRole,
                style = MaterialTheme.typography.bodySmall,
                color = TitanCyan,
                fontWeight = FontWeight.Medium
              )
            }

            // Fit Score Pill
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(TitanEmerald.copy(alpha = 0.15f))
                .border(1.dp, TitanEmerald.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
              Text(
                text = "${opp.fitScore}% FIT",
                style = MaterialTheme.typography.labelSmall,
                color = TitanEmerald,
                fontWeight = FontWeight.Bold
              )
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          // Compensation Range
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.MonetizationOn,
              contentDescription = null,
              tint = TitanGold,
              modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = opp.compensationRange,
              style = MaterialTheme.typography.labelSmall,
              color = TitanGold,
              fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.width(12.dp))
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(SlateElevated)
                .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
              Text(
                text = "Hiring: ${opp.hiringVelocity}",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondaryDark,
                fontSize = 10.sp
              )
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          // Leverage Thesis
          Text(
            text = opp.leverageThesis,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondaryDark,
            lineHeight = 17.sp
          )

          Spacer(modifier = Modifier.height(12.dp))

          // Action buttons: Tailor Resume & Company Intel
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Button(
              onClick = {
                viewModel.quickActionTailorResume(opp.targetRole, opp.companyName)
              },
              modifier = Modifier.weight(1f),
              colors = ButtonDefaults.buttonColors(containerColor = TitanCyan),
              shape = RoundedCornerShape(8.dp)
            ) {
              Icon(Icons.Default.Description, contentDescription = null, tint = ObsidianDark, modifier = Modifier.size(14.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Tailor ATS Resume", color = ObsidianDark, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }

            OutlinedButton(
              onClick = {
                viewModel.quickActionResearchCompany(opp.companyName)
              },
              modifier = Modifier.weight(1f),
              border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder),
              shape = RoundedCornerShape(8.dp)
            ) {
              Icon(Icons.Default.Business, contentDescription = null, tint = TitanCyan, modifier = Modifier.size(14.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("360° Intel", color = TextPrimaryDark, fontSize = 11.sp)
            }
          }
        }
      }
    }
  }
}

// ==============================================================================================
// 11. TAB 3: TACTICAL PLAYBOOK TAB
// ==============================================================================================
@Composable
private fun TacticalPlaybookTab(
  insights: ExecutiveCareerInsights,
  completedActions: List<Int>,
  onToggleAction: (Int) -> Unit,
  viewModel: TitanViewModel
) {
  Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
    // 30-Day Priorities with checkable status
    Card(
      modifier = Modifier.fillMaxWidth(),
      colors = CardDefaults.cardColors(containerColor = SlateCard),
      border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
    ) {
      Column(modifier = Modifier.padding(14.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.RocketLaunch,
            contentDescription = null,
            tint = TitanCyan,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "IMMEDIATE 30-DAY TACTICAL PRIORITIES",
            style = MaterialTheme.typography.labelSmall,
            color = TitanCyan,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.8.sp
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        insights.immediate30DayPriorities.forEachIndexed { index, action ->
          val isDone = completedActions.contains(index)
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(8.dp))
              .clickable { onToggleAction(index) }
              .padding(vertical = 6.dp, horizontal = 4.dp),
            verticalAlignment = Alignment.Top
          ) {
            Box(
              modifier = Modifier
                .size(18.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(if (isDone) TitanEmerald else SlateElevated)
                .border(1.dp, if (isDone) TitanEmerald else SlateBorder, RoundedCornerShape(4.dp)),
              contentAlignment = Alignment.Center
            ) {
              if (isDone) {
                Icon(
                  imageVector = Icons.Default.CheckCircle,
                  contentDescription = null,
                  tint = ObsidianDark,
                  modifier = Modifier.size(12.dp)
                )
              }
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
              text = action,
              style = MaterialTheme.typography.bodySmall,
              color = if (isDone) TextMutedDark else TextPrimaryDark,
              lineHeight = 18.sp
            )
          }
        }
      }
    }

    // Interview Preparation Vectors
    Card(
      modifier = Modifier.fillMaxWidth(),
      colors = CardDefaults.cardColors(containerColor = SlateCard),
      border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
    ) {
      Column(modifier = Modifier.padding(14.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.Psychology,
            contentDescription = null,
            tint = TitanGold,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "HIGH-STAKES INTERVIEW PREP VECTORS",
            style = MaterialTheme.typography.labelSmall,
            color = TitanGold,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.8.sp
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        insights.interviewPreparationVectors.forEach { vector ->
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 4.dp)
              .clip(RoundedCornerShape(8.dp))
              .background(SlateElevated)
              .padding(10.dp)
          ) {
            Text(
              text = vector,
              style = MaterialTheme.typography.bodySmall,
              color = TextSecondaryDark,
              lineHeight = 18.sp
            )
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
          onClick = { viewModel.quickActionLaunchInterview() },
          modifier = Modifier.fillMaxWidth(),
          colors = ButtonDefaults.buttonColors(containerColor = TitanGold),
          shape = RoundedCornerShape(8.dp)
        ) {
          Icon(Icons.Default.Psychology, contentDescription = null, tint = ObsidianDark, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Launch Simulated Interview Drill",
            color = ObsidianDark,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
          )
        }
      }
    }
  }
}

/**
 * Swipeable Carousel of Daily Career Insights & personalized tactics powered by Gemini API
 */
@Composable
fun DailyCareerInsightsCarouselSection(
  dailyTips: List<DailyCareerTip>?,
  viewModel: TitanViewModel,
  onRefreshTips: () -> Unit
) {
  val fallbackTips = remember {
    listOf(
      DailyCareerTip(
        id = "tip_1",
        category = "TACTICAL NEGOTIATION",
        headline = "Anchor on Outcome Value, Not Past Compensation",
        tipText = "In 2026 Bengaluru leadership searches, counter past-salary inquiries by stating: 'My focus is matching Zepto's ₹32L-₹42L benchmark for leaders who engineer 40%+ operational cycle compression.' Never quote past numbers first.",
        actionableTag = "Compare in Multi-Offer Lab",
        iconType = "MONETIZATION"
      ),
      DailyCareerTip(
        id = "tip_2",
        category = "OPERATIONAL MOAT",
        headline = "Translate 42% SLA Reduction to Bottom-Line P&L",
        tipText = "Frame your 42% operational cycle compression not just as workflow speed, but as dark-store throughput velocity and runway preservation. In quick commerce, cycle reduction translates to 18% higher order volume per hub.",
        actionableTag = "Export Grounded Case Study",
        iconType = "BOLT"
      ),
      DailyCareerTip(
        id = "tip_3",
        category = "AI AGENT ARCHITECTURE",
        headline = "Present Agents as an Enterprise Force Multiplier",
        tipText = "Position your Gemini & multi-agent orchestration skills as 'Autonomous Operations Architecture'—you don't just use AI to write emails; you build autonomous agents that monitor SLAs and triage supply chain delays 24/7.",
        actionableTag = "Launch Copilot Strategy Session",
        iconType = "ROCKET"
      ),
      DailyCareerTip(
        id = "tip_4",
        category = "FOUNDER ALIGNMENT",
        headline = "The Chief of Staff Unfair Advantage",
        tipText = "Series B/C founders look for operators who possess both business judgment and technical execution. Your combination of BBA business acumen plus Python/SQL eliminates the communication tax between the CEO and engineering.",
        actionableTag = "Tailor Resume for Chief of Staff",
        iconType = "STAR"
      ),
      DailyCareerTip(
        id = "tip_5",
        category = "EXECUTIVE PRESENCE",
        headline = "Run Every Interview Like a Board Presentation",
        tipText = "When asked behavioral questions, use the Executive Telemetry Framework: 1) Context & Business Stakes, 2) Data Ingestion & Diagnostic, 3) Algorithmic Solution, 4) Measurable Business Impact (e.g. 42% cycle time reduction).",
        actionableTag = "Drill in Interview OS",
        iconType = "PSYCHOLOGY"
      )
    )
  }

  val activeTips = if (!dailyTips.isNullOrEmpty()) dailyTips else fallbackTips
  val pagerState = rememberPagerState(initialPage = 0, pageCount = { activeTips.size })
  val coroutineScope = rememberCoroutineScope()

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("daily_career_insights_carousel_section")
  ) {
    // Header Row with Title, Gemini AI Pill, Page Counter, and Nav Arrows
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 8.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier = Modifier
            .size(30.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(TitanGold.copy(alpha = 0.15f)),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Lightbulb,
            contentDescription = null,
            tint = TitanGold,
            modifier = Modifier.size(16.dp)
          )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = "DAILY CAREER INSIGHTS",
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
                text = "GEMINI 3.5 AI",
                style = MaterialTheme.typography.labelSmall,
                color = TitanCyan,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }
          Text(
            text = "Swipeable personalized daily tips & unfair advantages",
            style = MaterialTheme.typography.bodySmall,
            color = TextMutedDark,
            fontSize = 11.sp
          )
        }
      }

      // Page tracker & manual prev/next navigation controls
      Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
          text = "${pagerState.currentPage + 1}/${activeTips.size}",
          style = MaterialTheme.typography.labelSmall,
          color = TextSecondaryDark,
          fontWeight = FontWeight.SemiBold,
          fontSize = 11.sp
        )
        Spacer(modifier = Modifier.width(6.dp))
        Box(
          modifier = Modifier
            .size(28.dp)
            .clip(CircleShape)
            .background(if (pagerState.currentPage > 0) SlateElevated else SlateDarker)
            .clickable(enabled = pagerState.currentPage > 0) {
              coroutineScope.launch {
                pagerState.animateScrollToPage(pagerState.currentPage - 1)
              }
            }
            .testTag("carousel_prev_button"),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.ChevronLeft,
            contentDescription = "Previous Insight",
            tint = if (pagerState.currentPage > 0) TextPrimaryDark else TextMutedDark,
            modifier = Modifier.size(16.dp)
          )
        }
        Spacer(modifier = Modifier.width(4.dp))
        Box(
          modifier = Modifier
            .size(28.dp)
            .clip(CircleShape)
            .background(if (pagerState.currentPage < activeTips.size - 1) SlateElevated else SlateDarker)
            .clickable(enabled = pagerState.currentPage < activeTips.size - 1) {
              coroutineScope.launch {
                pagerState.animateScrollToPage(pagerState.currentPage + 1)
              }
            }
            .testTag("carousel_next_button"),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = "Next Insight",
            tint = if (pagerState.currentPage < activeTips.size - 1) TextPrimaryDark else TextMutedDark,
            modifier = Modifier.size(16.dp)
          )
        }
      }
    }

    // HorizontalPager Swipeable Carousel
    HorizontalPager(
      state = pagerState,
      modifier = Modifier
        .fillMaxWidth()
        .testTag("daily_career_insights_horizontal_pager"),
      pageSpacing = 12.dp
    ) { page ->
      val tip = activeTips[page]
      DailyCareerTipCard(tip = tip, viewModel = viewModel)
    }

    // Centered Dot Indicators with interactive tap
    Spacer(modifier = Modifier.height(10.dp))
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.Center,
      verticalAlignment = Alignment.CenterVertically
    ) {
      activeTips.indices.forEach { index ->
        val isSelected = pagerState.currentPage == index
        val categoryColor = getCategoryColor(activeTips[index].category)
        Box(
          modifier = Modifier
            .padding(horizontal = 3.dp)
            .height(5.dp)
            .width(if (isSelected) 22.dp else 6.dp)
            .clip(RoundedCornerShape(3.dp))
            .background(if (isSelected) categoryColor else SlateElevated)
            .clickable {
              coroutineScope.launch {
                pagerState.animateScrollToPage(index)
              }
            }
        )
      }
    }
  }
}

/**
 * Individual Daily Career Insight Tip Card in the swipeable carousel
 */
@Composable
private fun DailyCareerTipCard(
  tip: DailyCareerTip,
  viewModel: TitanViewModel
) {
  val categoryColor = getCategoryColor(tip.category)
  val categoryIcon = getCategoryIcon(tip.iconType, tip.category)

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("daily_career_tip_card_${tip.id}"),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = SlateCard),
    border = androidx.compose.foundation.BorderStroke(1.dp, categoryColor.copy(alpha = 0.45f))
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp)
    ) {
      // Top Tag & Model Pill
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(categoryColor.copy(alpha = 0.12f))
            .border(0.8.dp, categoryColor.copy(alpha = 0.35f), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
        ) {
          Icon(
            imageVector = categoryIcon,
            contentDescription = null,
            tint = categoryColor,
            modifier = Modifier.size(13.dp)
          )
          Spacer(modifier = Modifier.width(5.dp))
          Text(
            text = tip.category,
            style = MaterialTheme.typography.labelSmall,
            color = categoryColor,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp,
            letterSpacing = 0.4.sp
          )
        }

        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(SlateElevated)
            .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
          Icon(
            imageVector = Icons.Default.AutoAwesome,
            contentDescription = null,
            tint = TitanCyan,
            modifier = Modifier.size(11.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "Personalized Tip",
            style = MaterialTheme.typography.labelSmall,
            color = TextMutedDark,
            fontSize = 9.5.sp
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Headline
      Text(
        text = tip.headline,
        style = MaterialTheme.typography.titleSmall,
        color = TextPrimaryDark,
        fontWeight = FontWeight.Bold,
        fontSize = 14.5.sp,
        lineHeight = 19.sp
      )

      Spacer(modifier = Modifier.height(6.dp))

      // Body Text
      Text(
        text = tip.tipText,
        style = MaterialTheme.typography.bodySmall,
        color = TextSecondaryDark,
        fontSize = 12.sp,
        lineHeight = 17.5.sp
      )

      Spacer(modifier = Modifier.height(12.dp))

      // Action Footer
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .weight(1f, fill = false)
            .clip(RoundedCornerShape(6.dp))
            .background(SlateElevated)
            .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
          Text(
            text = "Actionable: ${tip.actionableTag}",
            style = MaterialTheme.typography.labelSmall,
            color = TitanCyan,
            fontSize = 10.5.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Button(
          onClick = {
            executeTipAction(actionTag = tip.actionableTag, viewModel = viewModel)
          },
          modifier = Modifier
            .height(32.dp)
            .testTag("execute_tip_action_btn_${tip.id}"),
          colors = ButtonDefaults.buttonColors(
            containerColor = categoryColor.copy(alpha = 0.2f),
            contentColor = categoryColor
          ),
          border = androidx.compose.foundation.BorderStroke(1.dp, categoryColor.copy(alpha = 0.6f)),
          shape = RoundedCornerShape(8.dp),
          contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 0.dp)
        ) {
          Text(
            text = "Execute",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp
          )
          Spacer(modifier = Modifier.width(4.dp))
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            modifier = Modifier.size(12.dp)
          )
        }
      }
    }
  }
}

private fun getCategoryColor(category: String): Color {
  return when {
    category.contains("NEGOTIATION", ignoreCase = true) || category.contains("COMP", ignoreCase = true) -> TitanGold
    category.contains("MOAT", ignoreCase = true) || category.contains("OPERATIONAL", ignoreCase = true) -> TitanEmerald
    category.contains("AI", ignoreCase = true) || category.contains("AGENT", ignoreCase = true) -> TitanCyan
    category.contains("FOUNDER", ignoreCase = true) || category.contains("CHIEF", ignoreCase = true) -> TitanViolet
    category.contains("PRESENCE", ignoreCase = true) || category.contains("INTERVIEW", ignoreCase = true) -> TitanRose
    else -> TitanCyan
  }
}

private fun getCategoryIcon(iconType: String, category: String): ImageVector {
  return when {
    iconType.equals("MONETIZATION", ignoreCase = true) || category.contains("NEGOTIATION", ignoreCase = true) -> Icons.Default.MonetizationOn
    iconType.equals("BOLT", ignoreCase = true) || category.contains("OPERATIONAL", ignoreCase = true) -> Icons.Default.Bolt
    iconType.equals("ROCKET", ignoreCase = true) || category.contains("AI", ignoreCase = true) -> Icons.Default.RocketLaunch
    iconType.equals("STAR", ignoreCase = true) || category.contains("FOUNDER", ignoreCase = true) -> Icons.Default.Star
    iconType.equals("PSYCHOLOGY", ignoreCase = true) || category.contains("PRESENCE", ignoreCase = true) -> Icons.Default.Psychology
    else -> Icons.Default.AutoAwesome
  }
}

private fun executeTipAction(actionTag: String, viewModel: TitanViewModel) {
  when {
    actionTag.contains("Multi-Offer", ignoreCase = true) || actionTag.contains("Offer", ignoreCase = true) -> {
      viewModel.quickActionOpenMultiOfferComparator()
    }
    actionTag.contains("Interview", ignoreCase = true) -> {
      viewModel.quickActionLaunchInterview()
    }
    actionTag.contains("Resume", ignoreCase = true) || actionTag.contains("Optimize", ignoreCase = true) || actionTag.contains("Chief of Staff", ignoreCase = true) -> {
      viewModel.openResumeOptimizer(targetRole = "Chief of Staff", targetCompany = "Zepto")
    }
    actionTag.contains("Copilot", ignoreCase = true) -> {
      viewModel.navigateTo(com.example.ui.viewmodel.TitanScreen.COPILOT)
    }
    actionTag.contains("Case Study", ignoreCase = true) || actionTag.contains("Grounded", ignoreCase = true) -> {
      viewModel.quickActionOpenCapabilityMatrix()
    }
    else -> {
      viewModel.quickActionRunSimulator()
    }
  }
}

// ==============================================================================================
// 9. COMMAND CENTER TAB 1: EXECUTIVE INTELLIGENCE FEEDS (LIVE GROUNDED & MARKET SIGNALS)
// ==============================================================================================
@Composable
private fun ExecutiveIntelligenceFeedsTab(viewModel: TitanViewModel) {
  val briefingReport by viewModel.dailyCareerBriefing.collectAsState()
  val isRefreshing by viewModel.isRefreshingCareerBriefing.collectAsState()
  val companies by viewModel.companies.collectAsState()
  val selectedCompanyId by viewModel.selectedBriefingCompanyId.collectAsState()

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("executive_intelligence_feeds_tab"),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // 1. Live Intelligence Stream Header Card
    Card(
      modifier = Modifier.fillMaxWidth(),
      colors = CardDefaults.cardColors(containerColor = SlateCard),
      border = androidx.compose.foundation.BorderStroke(1.dp, TitanCyan.copy(alpha = 0.4f)),
      shape = RoundedCornerShape(14.dp)
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
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
                imageVector = Icons.Default.Newspaper,
                contentDescription = null,
                tint = TitanCyan,
                modifier = Modifier.size(20.dp)
              )
            }
            Column {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = "REAL-TIME INTELLIGENCE STREAM",
                  style = MaterialTheme.typography.labelSmall,
                  color = TitanCyan,
                  fontWeight = FontWeight.Bold,
                  letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.width(6.dp))
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(TitanEmerald.copy(alpha = 0.2f))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                  Text(
                    text = "GEMINI GROUNDED",
                    style = MaterialTheme.typography.labelSmall,
                    color = TitanEmerald,
                    fontWeight = FontWeight.Bold,
                    fontSize = 8.sp
                  )
                }
              }
              Text(
                text = "Market News, Stock Movements & Hiring Telemetry",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondaryDark,
                fontSize = 11.sp
              )
            }
          }

          IconButton(
            onClick = { viewModel.refreshDailyCareerBriefing(forceLiveSearch = true) },
            enabled = !isRefreshing,
            modifier = Modifier
              .size(36.dp)
              .background(SlateElevated, CircleShape)
              .testTag("refresh_intelligence_feed_btn")
          ) {
            if (isRefreshing) {
              CircularProgressIndicator(
                modifier = Modifier.size(16.dp),
                color = TitanCyan,
                strokeWidth = 2.dp
              )
            } else {
              Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Refresh Intelligence Feed",
                tint = TitanCyan,
                modifier = Modifier.size(18.dp)
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Macro Market Mood Strip
        briefingReport?.let { report ->
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(8.dp))
              .background(SlateElevated)
              .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp),
              modifier = Modifier.weight(1f)
            ) {
              Icon(
                imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                contentDescription = null,
                tint = TitanGold,
                modifier = Modifier.size(18.dp)
              )
              Column {
                Text(
                  text = "MARKET PULSE: ${report.dateFormatted} • ${report.overallMarketSentiment}",
                  style = MaterialTheme.typography.labelSmall,
                  color = TitanGold,
                  fontWeight = FontWeight.Bold,
                  fontSize = 10.sp
                )
                Text(
                  text = report.macroMarketSummary,
                  style = MaterialTheme.typography.bodySmall,
                  color = TextPrimaryDark,
                  fontWeight = FontWeight.Medium,
                  fontSize = 11.sp,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
                )
              }
            }

            OutlinedButton(
              onClick = { viewModel.navigateTo(com.example.ui.viewmodel.TitanScreen.COMPANIES) },
              modifier = Modifier.height(30.dp),
              contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 0.dp),
              border = androidx.compose.foundation.BorderStroke(1.dp, TitanCyan.copy(alpha = 0.5f))
            ) {
              Text(
                text = "Full Dossier",
                style = MaterialTheme.typography.labelSmall,
                color = TitanCyan,
                fontSize = 10.sp
              )
            }
          }
        }
      }
    }

    // 2. Tracked Companies Stock & News Cards
    val briefingCompanies = briefingReport?.topFiveCompanies ?: emptyList()
    if (briefingCompanies.isNotEmpty()) {
      Text(
        text = "TARGET COMPANIES LIVE RADAR (${briefingCompanies.size})",
        style = MaterialTheme.typography.labelSmall,
        color = TextMutedDark,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp
      )

      briefingCompanies.forEach { companyBrief ->
        val isSelected = companyBrief.companyId == selectedCompanyId
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .testTag("intel_company_card_${companyBrief.companyId}")
            .clickable { viewModel.selectBriefingCompany(companyBrief.companyId) },
          colors = CardDefaults.cardColors(
            containerColor = if (isSelected) SlateElevated else SlateCard
          ),
          border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSelected) TitanCyan else SlateBorder.copy(alpha = 0.6f)
          ),
          shape = RoundedCornerShape(12.dp)
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: Company name, ticker, and stock price / delta
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  modifier = Modifier
                    .size(28.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(TitanIndigo.copy(alpha = 0.2f)),
                  contentAlignment = Alignment.Center
                ) {
                  Text(
                    text = companyBrief.companyName.take(1).uppercase(),
                    color = TitanCyan,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                  )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                  Text(
                    text = companyBrief.companyName,
                    style = MaterialTheme.typography.titleSmall,
                    color = TextPrimaryDark,
                    fontWeight = FontWeight.Bold
                  )
                  Text(
                    text = companyBrief.industry,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMutedDark,
                    fontSize = 10.sp
                  )
                }
              }

              // Stock / Valuation Capsule
              if (companyBrief.tickerSymbol.isNotBlank() || companyBrief.stockPrice.isNotBlank()) {
                val isPositiveDelta = companyBrief.stockTrendDirection.isPositive
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(
                      if (isPositiveDelta) TitanEmerald.copy(alpha = 0.15f)
                      else TitanCrimson.copy(alpha = 0.15f)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    val stockText = buildString {
                      if (companyBrief.tickerSymbol.isNotBlank()) {
                        append("${companyBrief.tickerSymbol} ")
                      }
                      if (companyBrief.stockPrice.isNotBlank()) {
                        append(companyBrief.stockPrice)
                      }
                      if (companyBrief.stockTrendDelta.isNotBlank()) {
                        append(" (${companyBrief.stockTrendDelta})")
                      }
                    }
                    Text(
                      text = stockText.trim(),
                      style = MaterialTheme.typography.labelSmall,
                      color = if (isPositiveDelta) TitanEmerald else TitanCrimson,
                      fontWeight = FontWeight.Bold,
                      fontSize = 11.sp
                    )
                  }
                }
              }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Breaking News Summary
            if (companyBrief.newsHeadline.isNotBlank() || companyBrief.newsSummary.isNotBlank()) {
              Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                if (companyBrief.newsHeadline.isNotBlank()) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                  ) {
                    Text(
                      text = "• ",
                      color = TitanCyan,
                      fontWeight = FontWeight.Bold,
                      fontSize = 12.sp
                    )
                    Text(
                      text = companyBrief.newsHeadline,
                      style = MaterialTheme.typography.bodySmall,
                      color = TextPrimaryDark,
                      fontWeight = FontWeight.SemiBold,
                      fontSize = 11.sp,
                      maxLines = 1,
                      overflow = TextOverflow.Ellipsis
                    )
                  }
                }
                if (companyBrief.newsSummary.isNotBlank()) {
                  Text(
                    text = companyBrief.newsSummary,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondaryDark,
                    fontSize = 11.sp,
                    lineHeight = 16.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(start = 12.dp)
                  )
                }
              }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Footer: Bengaluru Hiring Signal & Action
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              if (companyBrief.hiringShiftLabel.isNotBlank()) {
                Text(
                  text = "Hiring: ${companyBrief.hiringShiftLabel}",
                  style = MaterialTheme.typography.labelSmall,
                  color = TitanGold,
                  fontWeight = FontWeight.SemiBold,
                  fontSize = 10.sp
                )
              } else {
                Spacer(modifier = Modifier.width(1.dp))
              }

              Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                  onClick = {
                    viewModel.quickActionResearchCompany(companyBrief.companyName)
                  },
                  modifier = Modifier.height(28.dp),
                  contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                  border = androidx.compose.foundation.BorderStroke(1.dp, TitanCyan.copy(alpha = 0.4f))
                ) {
                  Text(
                    text = "Dossier",
                    style = MaterialTheme.typography.labelSmall,
                    color = TitanCyan,
                    fontSize = 10.sp
                  )
                }
                Button(
                  onClick = {
                    viewModel.openResumeOptimizer(targetRole = "Strategy & Ops", targetCompany = companyBrief.companyName)
                  },
                  modifier = Modifier.height(28.dp),
                  colors = ButtonDefaults.buttonColors(containerColor = TitanCyan),
                  contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                ) {
                  Text(
                    text = "Tailor Resume",
                    style = MaterialTheme.typography.labelSmall,
                    color = ObsidianDark,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                  )
                }
              }
            }
          }
        }
      }
    } else {
      // Empty / Fallback State with Action to trigger briefing
      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SlateCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder),
        shape = RoundedCornerShape(12.dp)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Bolt,
            contentDescription = null,
            tint = TitanCyan,
            modifier = Modifier.size(32.dp)
          )
          Text(
            text = "Tracked Intelligence Feed Ready",
            style = MaterialTheme.typography.titleSmall,
            color = TextPrimaryDark,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = "Tap below to fetch real-time grounded market news and stock trends across ${companies.size} tracked companies.",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondaryDark,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            fontSize = 12.sp
          )
          Spacer(modifier = Modifier.height(6.dp))
          Button(
            onClick = { viewModel.refreshDailyCareerBriefing(forceLiveSearch = true) },
            colors = ButtonDefaults.buttonColors(containerColor = TitanCyan),
            modifier = Modifier.testTag("trigger_initial_intel_btn")
          ) {
            Icon(
              imageVector = Icons.Default.Refresh,
              contentDescription = null,
              tint = ObsidianDark,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text("Fetch Real-Time Intelligence", color = ObsidianDark, fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}

// ==============================================================================================
// 10. COMMAND CENTER TAB 2: CAREER GOALS (EXECUTIVE MILESTONES & TARGET COMP STRATEGY)
// ==============================================================================================
@Composable
private fun ExecutiveCareerGoalsTab(viewModel: TitanViewModel) {
  val haptic = LocalHapticFeedback.current
  val milestonesWithTasks by viewModel.milestonesWithTasks.collectAsState()
  val goals by viewModel.goals.collectAsState()

  // Milestones statistics
  val totalMilestones = milestonesWithTasks.size
  val totalTasks = milestonesWithTasks.sumOf { it.totalTasksCount }
  val completedTasks = milestonesWithTasks.sumOf { it.completedTasksCount }
  val milestoneProgressPercent = if (totalTasks > 0) ((completedTasks.toFloat() / totalTasks) * 100).toInt() else 0

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("executive_career_goals_tab"),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // 1. Goals Summary & Target Metric Hero
    Card(
      modifier = Modifier.fillMaxWidth(),
      colors = CardDefaults.cardColors(containerColor = SlateCard),
      border = androidx.compose.foundation.BorderStroke(1.dp, TitanGold.copy(alpha = 0.4f)),
      shape = RoundedCornerShape(14.dp)
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
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
                .background(TitanGold.copy(alpha = 0.15f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Flag,
                contentDescription = null,
                tint = TitanGold,
                modifier = Modifier.size(20.dp)
              )
            }
            Column {
              Text(
                text = "EXECUTIVE CAREER GOALS & MILESTONES",
                style = MaterialTheme.typography.labelSmall,
                color = TitanGold,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp
              )
              Text(
                text = "Target Compensation: ₹65L - ₹85L CTC • Chief of Staff",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondaryDark,
                fontSize = 11.sp
              )
            }
          }

          OutlinedButton(
            onClick = { viewModel.navigateTo(com.example.ui.viewmodel.TitanScreen.GOALS) },
            modifier = Modifier.height(32.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 0.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, TitanGold.copy(alpha = 0.5f))
          ) {
            Text("Manage OKRs", color = TitanGold, fontSize = 11.sp)
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Progress bar against quarterly targets
        val progressRatio = (milestoneProgressPercent / 100f).coerceIn(0f, 1f)
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Active Milestones: $completedTasks of $totalTasks Tasks Done",
            style = MaterialTheme.typography.labelSmall,
            color = TextPrimaryDark,
            fontWeight = FontWeight.SemiBold
          )
          Text(
            text = "$milestoneProgressPercent%",
            style = MaterialTheme.typography.labelSmall,
            color = TitanEmerald,
            fontWeight = FontWeight.Bold
          )
        }
        Spacer(modifier = Modifier.height(6.dp))
        LinearProgressIndicator(
          progress = { progressRatio },
          modifier = Modifier
            .fillMaxWidth()
            .height(6.dp)
            .clip(RoundedCornerShape(3.dp)),
          color = TitanEmerald,
          trackColor = SlateDarker
        )
      }
    }

    // 2. Individual Career Milestones & Horizon Tasks List
    if (milestonesWithTasks.isNotEmpty()) {
      Text(
        text = "STRATEGIC CAREER HORIZONS (${milestonesWithTasks.size})",
        style = MaterialTheme.typography.labelSmall,
        color = TextMutedDark,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp
      )

      milestonesWithTasks.forEach { milestoneWithTasks ->
        val milestone = milestoneWithTasks.milestone
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .testTag("milestone_card_${milestone.id}"),
          colors = CardDefaults.cardColors(containerColor = SlateCard),
          border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder.copy(alpha = 0.7f)),
          shape = RoundedCornerShape(12.dp)
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
              ) {
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(TitanIndigo.copy(alpha = 0.2f))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                  Text(
                    text = milestone.horizon,
                    style = MaterialTheme.typography.labelSmall,
                    color = TitanIndigo,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                  )
                }
                Text(
                  text = milestone.title,
                  style = MaterialTheme.typography.titleSmall,
                  color = TextPrimaryDark,
                  fontWeight = FontWeight.SemiBold,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
                )
              }

              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(4.dp))
                  .background(TitanCyan.copy(alpha = 0.15f))
                  .padding(horizontal = 6.dp, vertical = 2.dp)
              ) {
                Text(
                  text = "${milestoneWithTasks.progressPercent}%",
                  style = MaterialTheme.typography.labelSmall,
                  color = TitanCyan,
                  fontWeight = FontWeight.Bold,
                  fontSize = 10.sp
                )
              }
            }

            if (milestone.targetMetric.isNotBlank()) {
              Text(
                text = "Target: ${milestone.targetMetric}",
                style = MaterialTheme.typography.bodySmall,
                color = TitanGold,
                fontSize = 11.sp
              )
            }

            // Tasks under this milestone
            milestoneWithTasks.tasks.take(3).forEach { task ->
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(6.dp))
                  .background(SlateElevated.copy(alpha = 0.5f))
                  .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(6.dp),
                  modifier = Modifier.weight(1f)
                ) {
                  IconButton(
                    onClick = {
                      haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                      viewModel.toggleMilestoneTask(task)
                    },
                    modifier = Modifier.size(20.dp)
                  ) {
                    Icon(
                      imageVector = if (task.isCompleted) Icons.Default.CheckCircle else Icons.Default.Flag,
                      contentDescription = null,
                      tint = if (task.isCompleted) TitanEmerald else TextMutedDark,
                      modifier = Modifier.size(16.dp)
                    )
                  }
                  Text(
                    text = task.title,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (task.isCompleted) TextMutedDark else TextPrimaryDark,
                    fontSize = 11.sp,
                    textDecoration = if (task.isCompleted) androidx.compose.ui.text.style.TextDecoration.LineThrough else null,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                  )
                }

                if (task.dueDate.isNotBlank()) {
                  Text(
                    text = task.dueDate,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMutedDark,
                    fontSize = 9.sp
                  )
                }
              }
            }
          }
        }
      }
    } else if (goals.isNotEmpty()) {
      // Fallback to Goals list
      Text(
        text = "ACTIVE GOALS & OBJECTIVES (${goals.size})",
        style = MaterialTheme.typography.labelSmall,
        color = TextMutedDark,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp
      )

      goals.forEach { goal ->
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .testTag("goal_card_${goal.id}"),
          colors = CardDefaults.cardColors(containerColor = SlateCard),
          border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder.copy(alpha = 0.7f)),
          shape = RoundedCornerShape(12.dp)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = goal.title,
                style = MaterialTheme.typography.bodyMedium,
                color = TextPrimaryDark,
                fontWeight = FontWeight.SemiBold
              )
              Text(
                text = "${goal.category} • ${goal.targetMetric}",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondaryDark,
                fontSize = 10.sp
              )
            }

            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(TitanCyan.copy(alpha = 0.15f))
                .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
              Text(
                text = "${goal.progressPercent}%",
                style = MaterialTheme.typography.labelSmall,
                color = TitanCyan,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp
              )
            }
          }
        }
      }
    }

    // 3. Quick-Nav to Strategy Matrix
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .clickable { viewModel.navigateTo(com.example.ui.viewmodel.TitanScreen.CAREER_STRATEGY) },
      colors = CardDefaults.cardColors(containerColor = SlateElevated),
      border = androidx.compose.foundation.BorderStroke(1.dp, TitanIndigo.copy(alpha = 0.4f)),
      shape = RoundedCornerShape(12.dp)
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Icon(Icons.AutoMirrored.Filled.TrendingUp, contentDescription = null, tint = TitanIndigo, modifier = Modifier.size(20.dp))
          Column {
            Text("Open 3-Year Career Strategy Engine", style = MaterialTheme.typography.labelSmall, color = TextPrimaryDark, fontWeight = FontWeight.Bold)
            Text("Simulate career leaps, pivot scenarios, and salary bands", style = MaterialTheme.typography.bodySmall, color = TextSecondaryDark, fontSize = 10.sp)
          }
        }
        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = TitanIndigo, modifier = Modifier.size(16.dp))
      }
    }
  }
}

// ==============================================================================================
// 11. COMMAND CENTER TAB 3: JOB AUTOMATION TASKS (AGENT PIPELINES & AUTONOMOUS DISCOVERY)
// ==============================================================================================
@Composable
private fun ExecutiveJobAutomationTasksTab(viewModel: TitanViewModel) {
  val haptic = LocalHapticFeedback.current
  val automationRules by viewModel.automationRules.collectAsState()
  val taskLogs by viewModel.automationTaskLogs.collectAsState()
  val savedJobs by viewModel.jobs.collectAsState()
  val applications by viewModel.applications.collectAsState()

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("executive_job_automation_tab"),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // 1. Automation Status & Master Controls Header
    Card(
      modifier = Modifier.fillMaxWidth(),
      colors = CardDefaults.cardColors(containerColor = SlateCard),
      border = androidx.compose.foundation.BorderStroke(1.dp, TitanCyan.copy(alpha = 0.4f)),
      shape = RoundedCornerShape(14.dp)
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
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
                imageVector = Icons.Default.Tune,
                contentDescription = null,
                tint = TitanCyan,
                modifier = Modifier.size(20.dp)
              )
            }
            Column {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = "AUTONOMOUS JOB AUTOMATION PIPELINE",
                  style = MaterialTheme.typography.labelSmall,
                  color = TitanCyan,
                  fontWeight = FontWeight.Bold,
                  letterSpacing = 0.8.sp
                )
                Spacer(modifier = Modifier.width(6.dp))
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(TitanEmerald.copy(alpha = 0.2f))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                  Text(
                    text = "ACTIVE",
                    style = MaterialTheme.typography.labelSmall,
                    color = TitanEmerald,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 8.sp
                  )
                }
              }
              Text(
                text = "${automationRules.size} Rules Active • Automatic Scraping & Tailoring",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondaryDark,
                fontSize = 11.sp
              )
            }
          }

          Button(
            onClick = {
              haptic.performHapticFeedback(HapticFeedbackType.LongPress)
              viewModel.runSmartJobSearch(silent = false)
            },
            colors = ButtonDefaults.buttonColors(containerColor = TitanCyan),
            modifier = Modifier
              .height(32.dp)
              .testTag("run_job_automation_now_btn"),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 0.dp)
          ) {
            Icon(Icons.Default.Bolt, contentDescription = null, tint = ObsidianDark, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Run Now", color = ObsidianDark, fontWeight = FontWeight.Bold, fontSize = 11.sp)
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Automation Metrics Strip
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(8.dp))
              .background(SlateElevated)
              .padding(10.dp)
          ) {
            Column {
              Text("DISCOVERED JOBS", style = MaterialTheme.typography.labelSmall, color = TextMutedDark, fontSize = 9.sp)
              Text("${savedJobs.size}", style = MaterialTheme.typography.titleMedium, color = TitanCyan, fontWeight = FontWeight.Bold)
            }
          }
          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(8.dp))
              .background(SlateElevated)
              .padding(10.dp)
          ) {
            Column {
              Text("ACTIVE RULES", style = MaterialTheme.typography.labelSmall, color = TextMutedDark, fontSize = 9.sp)
              Text("${automationRules.count { it.isEnabled }}", style = MaterialTheme.typography.titleMedium, color = TitanEmerald, fontWeight = FontWeight.Bold)
            }
          }
          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(8.dp))
              .background(SlateElevated)
              .padding(10.dp)
          ) {
            Column {
              Text("TRACKED APPS", style = MaterialTheme.typography.labelSmall, color = TextMutedDark, fontSize = 9.sp)
              Text("${applications.size}", style = MaterialTheme.typography.titleMedium, color = TitanGold, fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    }

    // 2. Automated Job Application Status Tracking Module
    ExecutiveAutomatedApplicationsTracker(
      applications = applications,
      onUpdateStatus = { appId, newStatus ->
        viewModel.updateApplicationStatus(appId, newStatus)
      },
      onDeleteApplication = { appId ->
        viewModel.deleteApplicationWithFirestore(appId)
      },
      onNavigateToApplications = {
        viewModel.navigateTo(com.example.ui.viewmodel.TitanScreen.APPLICATIONS)
      }
    )

    // 3. Active Automation Rules Cards
    Text(
      text = "CONFIGURED AUTOMATION RULES & TRIGGERS",
      style = MaterialTheme.typography.labelSmall,
      color = TextMutedDark,
      fontWeight = FontWeight.Bold,
      letterSpacing = 1.sp
    )

    if (automationRules.isNotEmpty()) {
      automationRules.forEach { rule ->
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .testTag("rule_item_${rule.id}"),
          colors = CardDefaults.cardColors(containerColor = SlateCard),
          border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder.copy(alpha = 0.6f)),
          shape = RoundedCornerShape(12.dp)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.weight(1f),
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              Box(
                modifier = Modifier
                  .size(28.dp)
                  .clip(RoundedCornerShape(6.dp))
                  .background(if (rule.isEnabled) TitanEmerald.copy(alpha = 0.15f) else SlateDarker),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.SmartToy,
                  contentDescription = null,
                  tint = if (rule.isEnabled) TitanEmerald else TextMutedDark,
                  modifier = Modifier.size(16.dp)
                )
              }
              Column {
                Text(
                  text = rule.name,
                  style = MaterialTheme.typography.titleSmall,
                  color = TextPrimaryDark,
                  fontWeight = FontWeight.SemiBold
                )
                Text(
                  text = "Trigger: ${rule.triggerCategory} • Action: ${rule.responseType}",
                  style = MaterialTheme.typography.labelSmall,
                  color = TextSecondaryDark,
                  fontSize = 10.sp
                )
              }
            }

            androidx.compose.material3.Switch(
              checked = rule.isEnabled,
              onCheckedChange = { isChecked ->
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                viewModel.toggleAutomationRule(rule.id, isChecked)
              },
              colors = androidx.compose.material3.SwitchDefaults.colors(
                checkedThumbColor = TitanCyan,
                checkedTrackColor = TitanCyan.copy(alpha = 0.3f),
                uncheckedThumbColor = TextMutedDark,
                uncheckedTrackColor = SlateDarker
              )
            )
          }
        }
      }
    }

    // 3. Direct Navigation Buttons for Pipelines & Automation Control Room
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      Button(
        onClick = { viewModel.navigateTo(com.example.ui.viewmodel.TitanScreen.AUTOMATION_RULES) },
        modifier = Modifier.weight(1f),
        colors = ButtonDefaults.buttonColors(containerColor = SlateElevated),
        border = androidx.compose.foundation.BorderStroke(1.dp, TitanCyan.copy(alpha = 0.4f)),
        shape = RoundedCornerShape(10.dp)
      ) {
        Icon(Icons.Default.Tune, contentDescription = null, tint = TitanCyan, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text("Rules Engine", color = TextPrimaryDark, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
      }

      Button(
        onClick = { viewModel.navigateTo(com.example.ui.viewmodel.TitanScreen.AGENTS) },
        modifier = Modifier.weight(1f),
        colors = ButtonDefaults.buttonColors(containerColor = SlateElevated),
        border = androidx.compose.foundation.BorderStroke(1.dp, TitanIndigo.copy(alpha = 0.4f)),
        shape = RoundedCornerShape(10.dp)
      ) {
        Icon(Icons.Default.SmartToy, contentDescription = null, tint = TitanIndigo, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text("Agent Control", color = TextPrimaryDark, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
      }
    }
  }
}

// ==============================================================================================
// 12. TASK MANAGEMENT MODULE: AUTOMATED JOB APPLICATION STATUS TRACKER
// ==============================================================================================
@Composable
private fun ExecutiveAutomatedApplicationsTracker(
  applications: List<Application>,
  onUpdateStatus: (String, String) -> Unit,
  onDeleteApplication: (String) -> Unit,
  onNavigateToApplications: () -> Unit
) {
  val haptic = LocalHapticFeedback.current
  var selectedFilter by remember { mutableStateOf("ALL") }
  
  // State for Status Update Confirmation Dialog
  var pendingStatusUpdate by remember { mutableStateOf<Pair<Application, String>?>(null) }
  // State for Delete Confirmation Dialog
  var pendingDeleteApp by remember { mutableStateOf<Application?>(null) }

  val statusCounts = remember(applications) {
    mapOf(
      "ALL" to applications.size,
      "APPLIED" to applications.count { it.status.equals("APPLIED", ignoreCase = true) },
      "ASSESSMENT" to applications.count { it.status.equals("ASSESSMENT", ignoreCase = true) },
      "INTERVIEW" to applications.count { it.status.equals("INTERVIEW", ignoreCase = true) || it.status.equals("FINAL_ROUND", ignoreCase = true) },
      "OFFER" to applications.count { it.status.equals("OFFER", ignoreCase = true) }
    )
  }

  val filteredApps = remember(applications, selectedFilter) {
    if (selectedFilter == "ALL") {
      applications
    } else if (selectedFilter == "INTERVIEW") {
      applications.filter { it.status.equals("INTERVIEW", ignoreCase = true) || it.status.equals("FINAL_ROUND", ignoreCase = true) }
    } else {
      applications.filter { it.status.equals(selectedFilter, ignoreCase = true) }
    }
  }

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("executive_automated_applications_tracker"),
    colors = CardDefaults.cardColors(containerColor = SlateCard),
    border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder),
    shape = RoundedCornerShape(14.dp)
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      // Header with Title, Status & Navigation link
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Box(
            modifier = Modifier
              .size(28.dp)
              .clip(RoundedCornerShape(6.dp))
              .background(TitanGold.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Work,
              contentDescription = null,
              tint = TitanGold,
              modifier = Modifier.size(16.dp)
            )
          }
          Column {
            Text(
              text = "AUTOMATED APPLICATION TASK TRACKER",
              style = MaterialTheme.typography.labelSmall,
              color = TitanGold,
              fontWeight = FontWeight.Bold,
              letterSpacing = 0.8.sp
            )
            Text(
              text = "${applications.size} Total Tracked • Real-time Status Sync",
              style = MaterialTheme.typography.bodySmall,
              color = TextSecondaryDark,
              fontSize = 10.sp
            )
          }
        }

        OutlinedButton(
          onClick = onNavigateToApplications,
          modifier = Modifier.height(28.dp),
          contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 0.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, TitanCyan.copy(alpha = 0.5f))
        ) {
          Text(
            text = "Full Tracker",
            style = MaterialTheme.typography.labelSmall,
            color = TitanCyan,
            fontSize = 10.sp
          )
          Spacer(modifier = Modifier.width(4.dp))
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            tint = TitanCyan,
            modifier = Modifier.size(12.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Status Filter Pills
      val filterTabs = listOf("ALL", "APPLIED", "ASSESSMENT", "INTERVIEW", "OFFER")
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        filterTabs.forEach { tab ->
          val isSelected = selectedFilter == tab
          val count = statusCounts[tab] ?: 0
          val filterBadgeColor = when (tab) {
            "OFFER" -> TitanEmerald
            "INTERVIEW" -> TitanCyan
            "ASSESSMENT" -> TitanGold
            "APPLIED" -> TitanIndigo
            else -> TextSecondaryDark
          }

          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .background(if (isSelected) SlateElevated else SlateDarker)
              .border(
                1.dp,
                if (isSelected) filterBadgeColor else SlateBorder.copy(alpha = 0.5f),
                RoundedCornerShape(8.dp)
              )
              .clickable { selectedFilter = tab }
              .padding(horizontal = 10.dp, vertical = 5.dp)
              .testTag("app_filter_$tab")
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
              Text(
                text = tab,
                style = MaterialTheme.typography.labelSmall,
                color = if (isSelected) TextPrimaryDark else TextMutedDark,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                fontSize = 10.sp
              )
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(4.dp))
                  .background(if (isSelected) filterBadgeColor.copy(alpha = 0.25f) else SlateBorder.copy(alpha = 0.4f))
                  .padding(horizontal = 4.dp, vertical = 1.dp)
              ) {
                Text(
                  text = "$count",
                  style = MaterialTheme.typography.labelSmall,
                  color = if (isSelected) filterBadgeColor else TextMutedDark,
                  fontWeight = FontWeight.Bold,
                  fontSize = 9.sp
                )
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Application List Items
      if (filteredApps.isEmpty()) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(SlateDarker)
            .padding(vertical = 20.dp),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = if (selectedFilter == "ALL") "No automated job applications tracked yet" else "No applications in '$selectedFilter' stage",
            style = MaterialTheme.typography.bodySmall,
            color = TextMutedDark,
            fontSize = 11.sp
          )
        }
      } else {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          filteredApps.take(6).forEach { app ->
            AutomatedApplicationTaskItem(
              app = app,
              onRequestStatusChange = { newStatus ->
                pendingStatusUpdate = app to newStatus
              },
              onRequestDelete = {
                pendingDeleteApp = app
              }
            )
          }

          if (filteredApps.size > 6) {
            Text(
              text = "+ ${filteredApps.size - 6} more automated applications in Full Tracker",
              style = MaterialTheme.typography.labelSmall,
              color = TitanCyan,
              modifier = Modifier
                .clickable { onNavigateToApplications() }
                .padding(top = 4.dp),
              fontSize = 10.sp
            )
          }
        }
      }
    }
  }

  // --- CONFIRMATION DIALOG: TASK STATUS UPDATE ---
  pendingStatusUpdate?.let { (app, newStatus) ->
    AlertDialog(
      onDismissRequest = { pendingStatusUpdate = null },
      containerColor = SlateElevated,
      title = {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Tune,
            contentDescription = null,
            tint = TitanCyan,
            modifier = Modifier.size(20.dp)
          )
          Text(
            text = "Update Application Stage?",
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimaryDark,
            fontWeight = FontWeight.Bold
          )
        }
      },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
          Text(
            text = "Are you sure you want to update the stage for ${app.companyName} (${app.roleTitle})?",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondaryDark
          )
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Text(
              text = app.status.uppercase(),
              style = MaterialTheme.typography.labelSmall,
              color = TextMutedDark,
              fontWeight = FontWeight.Bold
            )
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowForward,
              contentDescription = null,
              tint = TitanCyan,
              modifier = Modifier.size(12.dp)
            )
            Text(
              text = newStatus.uppercase(),
              style = MaterialTheme.typography.labelSmall,
              color = TitanCyan,
              fontWeight = FontWeight.ExtraBold
            )
          }
        }
      },
      confirmButton = {
        Button(
          onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            onUpdateStatus(app.id, newStatus)
            pendingStatusUpdate = null
          },
          colors = ButtonDefaults.buttonColors(containerColor = TitanCyan),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier.testTag("confirm_status_update_button")
        ) {
          Text("Confirm Update", fontWeight = FontWeight.Bold, color = SlateDarker)
        }
      },
      dismissButton = {
        TextButton(
          onClick = { pendingStatusUpdate = null },
          modifier = Modifier.testTag("cancel_status_update_button")
        ) {
          Text("Cancel", color = TextSecondaryDark)
        }
      }
    )
  }

  // --- CONFIRMATION DIALOG: TASK DELETION ---
  pendingDeleteApp?.let { app ->
    AlertDialog(
      onDismissRequest = { pendingDeleteApp = null },
      containerColor = SlateElevated,
      title = {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = null,
            tint = TitanCrimson,
            modifier = Modifier.size(20.dp)
          )
          Text(
            text = "Remove Tracked Application?",
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimaryDark,
            fontWeight = FontWeight.Bold
          )
        }
      },
      text = {
        Text(
          text = "Are you sure you want to remove the tracked application for ${app.companyName} (${app.roleTitle})? This action cannot be undone.",
          style = MaterialTheme.typography.bodyMedium,
          color = TextSecondaryDark
        )
      },
      confirmButton = {
        Button(
          onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            onDeleteApplication(app.id)
            pendingDeleteApp = null
          },
          colors = ButtonDefaults.buttonColors(containerColor = TitanCrimson),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier.testTag("confirm_delete_app_button")
        ) {
          Text("Remove", fontWeight = FontWeight.Bold, color = androidx.compose.ui.graphics.Color.White)
        }
      },
      dismissButton = {
        TextButton(
          onClick = { pendingDeleteApp = null },
          modifier = Modifier.testTag("cancel_delete_app_button")
        ) {
          Text("Cancel", color = TextSecondaryDark)
        }
      }
    )
  }
}

@Composable
private fun AutomatedApplicationTaskItem(
  app: Application,
  onRequestStatusChange: (String) -> Unit,
  onRequestDelete: () -> Unit
) {
  val statusColor = when (app.status.uppercase()) {
    "OFFER" -> TitanEmerald
    "INTERVIEW", "FINAL_ROUND" -> TitanCyan
    "ASSESSMENT" -> TitanGold
    "APPLIED" -> TitanIndigo
    "REJECTED" -> TitanCrimson
    else -> TextMutedDark
  }

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("app_task_item_${app.id}"),
    colors = CardDefaults.cardColors(containerColor = SlateElevated),
    border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder.copy(alpha = 0.6f)),
    shape = RoundedCornerShape(10.dp)
  ) {
    Column(modifier = Modifier.padding(12.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = app.companyName,
              style = MaterialTheme.typography.titleSmall,
              color = TextPrimaryDark,
              fontWeight = FontWeight.Bold,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.width(6.dp))
            if (app.dateApplied.isNotBlank()) {
              Text(
                text = "• ${app.dateApplied}",
                style = MaterialTheme.typography.labelSmall,
                color = TextMutedDark,
                fontSize = 10.sp
              )
            }
          }
          Text(
            text = app.roleTitle,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondaryDark,
            fontSize = 11.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        }

        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          // Status Badge Pill
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(statusColor.copy(alpha = 0.18f))
              .border(1.dp, statusColor.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
          ) {
            Text(
              text = app.status.uppercase(),
              style = MaterialTheme.typography.labelSmall,
              color = statusColor,
              fontWeight = FontWeight.ExtraBold,
              fontSize = 9.sp
            )
          }

          // Delete Action Icon
          IconButton(
            onClick = onRequestDelete,
            modifier = Modifier
              .size(24.dp)
              .testTag("delete_app_btn_${app.id}")
          ) {
            Icon(
              imageVector = Icons.Default.Delete,
              contentDescription = "Delete Application",
              tint = TextMutedDark,
              modifier = Modifier.size(14.dp)
            )
          }
        }
      }

      // Quick Stage Progression Action Strip
      Spacer(modifier = Modifier.height(8.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        val nextAction = when (app.status.uppercase()) {
          "DISCOVERED", "SHORTLISTED" -> "Mark Applied" to "APPLIED"
          "APPLIED" -> "Log Assessment" to "ASSESSMENT"
          "ASSESSMENT" -> "Advance to Interview" to "INTERVIEW"
          "INTERVIEW" -> "Mark Offer Received" to "OFFER"
          else -> null
        }

        if (nextAction != null) {
          OutlinedButton(
            onClick = { onRequestStatusChange(nextAction.second) },
            modifier = Modifier.height(24.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 6.dp, vertical = 0.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, TitanCyan.copy(alpha = 0.4f))
          ) {
            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = TitanCyan, modifier = Modifier.size(10.dp))
            Spacer(modifier = Modifier.width(3.dp))
            Text(
              text = nextAction.first,
              style = MaterialTheme.typography.labelSmall,
              color = TitanCyan,
              fontSize = 9.sp
            )
          }
        } else {
          Spacer(modifier = Modifier.width(1.dp))
        }

        if (app.interviewScore > 0) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Star, contentDescription = null, tint = TitanGold, modifier = Modifier.size(11.dp))
            Spacer(modifier = Modifier.width(2.dp))
            Text(
              text = "Score: ${app.interviewScore}/100",
              style = MaterialTheme.typography.labelSmall,
              color = TitanGold,
              fontSize = 9.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }
    }
  }
}

