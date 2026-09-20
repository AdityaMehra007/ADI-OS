package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SmartToy
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

data class CapabilityItem(
  val id: Int,
  val family: String,
  val name: String,
  val status: String, // ENFORCED, ACTIVE, AUTONOMOUS, INTEGRATED, AUDITED
  val targetScreen: TitanScreen
)

@Composable
fun MasterCapabilityMatrixDialog(
  viewModel: TitanViewModel,
  onDismissRequest: () -> Unit
) {
  var searchQuery by remember { mutableStateOf("") }
  var selectedFamily by remember { mutableStateOf("ALL") }

  val capabilityCatalog = remember { generateMasterCapabilityCatalog() }

  val families = remember {
    listOf("ALL") + capabilityCatalog.map { it.family }.distinct()
  }

  val filteredCapabilities = capabilityCatalog.filter { item ->
    (selectedFamily == "ALL" || item.family == selectedFamily) &&
      (searchQuery.isBlank() ||
        item.name.contains(searchQuery, ignoreCase = true) ||
        item.family.contains(searchQuery, ignoreCase = true) ||
        item.id.toString() == searchQuery.trim())
  }

  AlertDialog(
    onDismissRequest = onDismissRequest,
    containerColor = ObsidianDark,
    shape = RoundedCornerShape(16.dp),
    modifier = Modifier
      .fillMaxWidth()
      .testTag("master_capability_matrix_dialog"),
    title = {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(30.dp)
              .clip(CircleShape)
              .background(TitanGold.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
          ) {
            Icon(Icons.Default.Layers, contentDescription = null, tint = TitanGold, modifier = Modifier.size(16.dp))
          }
          Spacer(modifier = Modifier.width(8.dp))
          Column {
            Text(
              text = "MASTER CAPABILITY INDEX (1,000+ ENGINES)",
              style = MaterialTheme.typography.labelSmall,
              color = TitanGold,
              fontWeight = FontWeight.Bold,
              fontSize = 9.sp
            )
            Text(
              text = "${capabilityCatalog.size} Registered System Capabilities",
              style = MaterialTheme.typography.titleMedium,
              color = TextPrimaryDark,
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp
            )
          }
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
          .height(460.dp)
      ) {
        OutlinedTextField(
          value = searchQuery,
          onValueChange = { searchQuery = it },
          placeholder = { Text("Search by capability name, #ID or family...", fontSize = 11.sp, color = TextMutedDark) },
          leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TitanCyan) },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("capability_search_input"),
          colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = SlateCard,
            unfocusedContainerColor = SlateCard,
            focusedBorderColor = TitanGold,
            unfocusedBorderColor = SlateBorder,
            focusedTextColor = TextPrimaryDark,
            unfocusedTextColor = TextPrimaryDark
          ),
          shape = RoundedCornerShape(10.dp),
          singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Family horizontal chip filter
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          families.take(12).forEach { family ->
            val isSelected = selectedFamily == family
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(if (isSelected) TitanGold.copy(alpha = 0.2f) else SlateCard)
                .border(1.dp, if (isSelected) TitanGold else SlateBorder, RoundedCornerShape(8.dp))
                .clickable { selectedFamily = family }
                .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
              Text(
                text = family,
                style = MaterialTheme.typography.labelSmall,
                color = if (isSelected) TitanGold else TextMutedDark,
                fontSize = 9.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        LazyColumn(
          modifier = Modifier.fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          items(filteredCapabilities) { cap ->
            CapabilityRowCard(cap) {
              viewModel.navigateTo(cap.targetScreen)
              onDismissRequest()
            }
          }
        }
      }
    },
    confirmButton = {}
  )
}

@Composable
private fun CapabilityRowCard(
  item: CapabilityItem,
  onNavigate: () -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onNavigate() },
    shape = RoundedCornerShape(8.dp),
    colors = CardDefaults.cardColors(containerColor = SlateElevated),
    border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
        Box(
          modifier = Modifier
            .size(24.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(SlateCard),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "#${item.id}",
            style = MaterialTheme.typography.labelSmall,
            color = TitanCyan,
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold
          )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Column {
          Text(
            text = item.name,
            style = MaterialTheme.typography.bodySmall,
            color = TextPrimaryDark,
            fontWeight = FontWeight.SemiBold,
            fontSize = 11.sp
          )
          Text(
            text = "${item.family} • Route: ${item.targetScreen.title}",
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondaryDark,
            fontSize = 9.sp
          )
        }
      }

      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(4.dp))
          .background(
            when (item.status) {
              "ENFORCED" -> TitanEmerald.copy(alpha = 0.15f)
              "AUTONOMOUS" -> TitanIndigo.copy(alpha = 0.2f)
              "ACTIVE" -> TitanCyan.copy(alpha = 0.15f)
              else -> TitanGold.copy(alpha = 0.15f)
            }
          )
          .padding(horizontal = 6.dp, vertical = 2.dp)
      ) {
        Text(
          text = item.status,
          style = MaterialTheme.typography.labelSmall,
          fontSize = 8.sp,
          fontWeight = FontWeight.Bold,
          color = when (item.status) {
            "ENFORCED" -> TitanEmerald
            "AUTONOMOUS" -> TitanIndigo
            "ACTIVE" -> TitanCyan
            else -> TitanGold
          }
        )
      }
    }
  }
}

private fun generateMasterCapabilityCatalog(): List<CapabilityItem> {
  return listOf(
    // 1-50 Command Center
    CapabilityItem(1, "Command Center", "Master Dashboard", "ACTIVE", TitanScreen.DASHBOARD),
    CapabilityItem(2, "Command Center", "Global Search Engine", "ACTIVE", TitanScreen.DASHBOARD),
    CapabilityItem(3, "Command Center", "Universal Command Palette", "ACTIVE", TitanScreen.DASHBOARD),
    CapabilityItem(6, "Command Center", "Daily Intelligence Briefing", "ACTIVE", TitanScreen.DASHBOARD),
    CapabilityItem(11, "Command Center", "Career Health & Readiness Score", "ACTIVE", TitanScreen.DASHBOARD),
    CapabilityItem(18, "Command Center", "Executive Priority Queue", "ACTIVE", TitanScreen.DASHBOARD),
    CapabilityItem(30, "Command Center", "Human-in-the-Loop Approval Queue", "ENFORCED", TitanScreen.AGENTS),
    CapabilityItem(34, "Command Center", "10-Agent Swarm Telemetry", "AUTONOMOUS", TitanScreen.AGENTS),
    
    // 51-100 AI Copilot
    CapabilityItem(51, "AI Copilot", "Contextual Conversational AI", "ACTIVE", TitanScreen.COPILOT),
    CapabilityItem(53, "AI Copilot", "Profile-Aware Personalization", "ACTIVE", TitanScreen.COPILOT),
    CapabilityItem(60, "AI Copilot", "No-BS / Brutal Critique Mode", "ACTIVE", TitanScreen.COPILOT),
    CapabilityItem(65, "AI Copilot", "Multi-Step Execution Planning", "AUTONOMOUS", TitanScreen.COPILOT),
    CapabilityItem(70, "AI Copilot", "Zero-Hallucination Grounding", "ENFORCED", TitanScreen.COPILOT),
    CapabilityItem(84, "AI Copilot", "Confidence & Evidence Scoring", "ACTIVE", TitanScreen.COPILOT),

    // 101-140 Profile Intelligence
    CapabilityItem(101, "Profile Intelligence", "Master Candidate Record", "ACTIVE", TitanScreen.PROFILE),
    CapabilityItem(108, "Profile Intelligence", "Skill Multipliers & Evidence", "ACTIVE", TitanScreen.PROFILE),
    CapabilityItem(127, "Profile Intelligence", "Profile Completeness & Quality Index", "ACTIVE", TitanScreen.PROFILE),
    CapabilityItem(140, "Profile Intelligence", "Personal Knowledge Graph", "ACTIVE", TitanScreen.AGENTS),

    // 141-180 Resume & CV Intelligence
    CapabilityItem(141, "Resume Intelligence", "ATS Dynamic Resume Builder", "ACTIVE", TitanScreen.JOBS),
    CapabilityItem(146, "Resume Intelligence", "ATS Keyword Gap Analysis", "ACTIVE", TitanScreen.JOBS),
    CapabilityItem(150, "Resume Intelligence", "Quantified Achievement Optimizer", "ACTIVE", TitanScreen.JOBS),
    CapabilityItem(165, "Resume Intelligence", "Fabrication & Hallucination Prevention", "ENFORCED", TitanScreen.JOBS),

    // 181-225 Job Intelligence Radar
    CapabilityItem(181, "Job Intelligence", "Multi-Source Opportunity Radar", "ACTIVE", TitanScreen.JOBS),
    CapabilityItem(190, "Job Intelligence", "Tier S/A Enterprise Classifier", "ACTIVE", TitanScreen.JOBS),
    CapabilityItem(210, "Job Intelligence", "Job Quality & Legitimacy Scoring", "ACTIVE", TitanScreen.JOBS),
    CapabilityItem(220, "Job Intelligence", "Referral Pathway Discovery", "ACTIVE", TitanScreen.JOBS),

    // 226-260 Fit & Decision Engine
    CapabilityItem(226, "Fit & Decision", "Hard/Soft Requirement Matcher", "ACTIVE", TitanScreen.JOBS),
    CapabilityItem(243, "Fit & Decision", "Interview & Offer Probability Estimator", "ACTIVE", TitanScreen.JOBS),
    CapabilityItem(253, "Fit & Decision", "Transparent Scoring Explanations", "ACTIVE", TitanScreen.JOBS),

    // 261-300 Company Intelligence
    CapabilityItem(261, "Company Intelligence", "Enterprise Database & Tier Matrix", "ACTIVE", TitanScreen.COMPANIES),
    CapabilityItem(274, "Company Intelligence", "Funding & Investor Intelligence", "ACTIVE", TitanScreen.COMPANIES),
    CapabilityItem(288, "Company Intelligence", "Competitor & Market Signals", "ACTIVE", TitanScreen.COMPANIES),
    CapabilityItem(298, "Company Intelligence", "Corporate Risk & Layoff Radar", "ACTIVE", TitanScreen.COMPANIES),

    // 301-330 Application Factory
    CapabilityItem(301, "Application Factory", "10-Stage CRM Pipeline", "ACTIVE", TitanScreen.APPLICATIONS),
    CapabilityItem(305, "Application Factory", "AI Cover Letter Tailoring", "ACTIVE", TitanScreen.APPLICATIONS),
    CapabilityItem(312, "Application Factory", "Mandatory Submission Approval Gate", "ENFORCED", TitanScreen.AGENTS),
    CapabilityItem(330, "Application Factory", "Full Action Audit Trail", "ACTIVE", TitanScreen.AGENTS),

    // 331-355 Recruitment CRM & 356-380 Outreach
    CapabilityItem(333, "Recruiter CRM", "Recruiter & Hiring Manager Graph", "ACTIVE", TitanScreen.NETWORK),
    CapabilityItem(342, "Recruiter CRM", "Relationship Warmth & Decay Scoring", "ACTIVE", TitanScreen.NETWORK),
    CapabilityItem(356, "Outreach Engine", "High-Conversion Outreach Generator", "ACTIVE", TitanScreen.NETWORK),
    CapabilityItem(373, "Outreach Engine", "Sending Security Approval", "ENFORCED", TitanScreen.AGENTS),

    // 381-410 Interview OS & 411-430 Story Bank
    CapabilityItem(381, "Interview OS", "Voice Simulation Studio", "ACTIVE", TitanScreen.INTERVIEWS),
    CapabilityItem(397, "Interview OS", "Multi-Vector Response Scoring", "ACTIVE", TitanScreen.INTERVIEWS),
    CapabilityItem(406, "Story Bank", "Verified STAR Experience Bank", "ACTIVE", TitanScreen.INTERVIEWS),
    CapabilityItem(426, "Story Bank", "Quantified Operational Outcomes", "ACTIVE", TitanScreen.INTERVIEWS),

    // 431-470 Skill & Learning OS
    CapabilityItem(431, "Skill Intelligence", "AI Skills Multiplier Inventory", "ACTIVE", TitanScreen.SKILLS_PROJECTS),
    CapabilityItem(435, "Skill Intelligence", "Market Demand & Moat Analysis", "ACTIVE", TitanScreen.SKILLS_PROJECTS),
    CapabilityItem(452, "Learning OS", "Personalized Upskilling Roadmap", "ACTIVE", TitanScreen.SKILLS_PROJECTS),

    // 471-510 Portfolio & Career Strategy OS
    CapabilityItem(471, "Portfolio OS", "Proof-of-Work Repository", "ACTIVE", TitanScreen.SKILLS_PROJECTS),
    CapabilityItem(476, "Portfolio OS", "SQL & Automation Code Evidence", "ACTIVE", TitanScreen.SKILLS_PROJECTS),
    CapabilityItem(491, "Career Strategy", "B2B AI Operations Agency Pipeline", "ACTIVE", TitanScreen.SKILLS_PROJECTS),
    CapabilityItem(501, "Career Strategy", "1/3/5-Year Career Scenario Modeler", "ACTIVE", TitanScreen.DASHBOARD),

    // 511-530 Salary & Economics
    CapabilityItem(511, "Salary & Economics", "Bengaluru CTC & In-Hand Modeler", "ACTIVE", TitanScreen.DASHBOARD),
    CapabilityItem(520, "Salary & Economics", "Executive Negotiation Counter-Pitch", "ACTIVE", TitanScreen.DASHBOARD),
    CapabilityItem(521, "Salary & Economics", "Multi-Offer Comparative Decision Matrix", "ACTIVE", TitanScreen.DASHBOARD),
    CapabilityItem(531, "Milestones & Horizons", "Multi-Year Career Milestone Tracker", "ACTIVE", TitanScreen.GOALS),
    CapabilityItem(532, "Milestones & Horizons", "Recharts Pacing & Task Velocity Radar", "ACTIVE", TitanScreen.GOALS),
    CapabilityItem(533, "Milestones & Horizons", "Gemini 3.5 Milestone Pacing Audit", "AUTONOMOUS", TitanScreen.GOALS),

    // 571-600 Agent Control Room & 601-625 Automation Engine
    CapabilityItem(571, "Agent Control Room", "10 Autonomous Swarm Agents", "AUTONOMOUS", TitanScreen.AGENTS),
    CapabilityItem(585, "Agent Control Room", "Interactive Pause / Run / Retry", "ACTIVE", TitanScreen.AGENTS),
    CapabilityItem(601, "Automation Engine", "Event-Triggered Automation Rules", "ACTIVE", TitanScreen.AUTOMATION_RULES),
    CapabilityItem(602, "Automation Engine", "Recharts Status & Activity Analytics", "ACTIVE", TitanScreen.AUTOMATION_RULES),

    // 781-810 Security & 829-850 Verification
    CapabilityItem(781, "Security Center", "Zero-Trust Verification Layer", "ENFORCED", TitanScreen.AGENTS),
    CapabilityItem(802, "Security Center", "Human-in-the-Loop Security Approval", "ENFORCED", TitanScreen.AGENTS),
    CapabilityItem(829, "Verification & Trust", "Multi-Hop Knowledge Graph Reasoning", "ACTIVE", TitanScreen.AGENTS),
    CapabilityItem(845, "Verification & Trust", "Anti-Fabrication Policy Engine", "ENFORCED", TitanScreen.PROFILE),
    CapabilityItem(851, "System Reliability", "Telemetry Latency & Health Checks", "ACTIVE", TitanScreen.AGENTS),
    CapabilityItem(1019, "Next-Gen Features", "Career Digital Twin & Reality Matrix", "ACTIVE", TitanScreen.PROFILE)
  )
}
