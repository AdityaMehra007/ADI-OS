package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
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
import com.example.ui.viewmodel.TitanScreen
import com.example.ui.viewmodel.TitanViewModel

@Composable
fun OmegaTitanSuperAppScreen(
  viewModel: TitanViewModel
) {
  var selectedTab by remember { mutableIntStateOf(0) }
  val tabs = listOf(
    "🏛️ War Room",
    "⚡ 25-Keypad",
    "🏬 Dark Store Lab",
    "🎯 Target Recon",
    "🛡️ Red-Team",
    "💻 SQL Studio"
  )

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(ObsidianDark)
      .testTag("omega_titan_super_app_screen")
  ) {
    // Sovereign Top Banner
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 8.dp),
      colors = CardDefaults.cardColors(containerColor = SlateCard),
      shape = RoundedCornerShape(12.dp),
      border = androidx.compose.foundation.BorderStroke(1.dp, TitanGold.copy(alpha = 0.5f))
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
                .background(TitanGold.copy(alpha = 0.2f))
                .border(1.dp, TitanGold, CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Text("👑", fontSize = 18.sp)
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "OMEGA-TITAN : SOVEREIGN KERNEL",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black,
                color = TitanGold,
                letterSpacing = 0.5.sp
              )
              Text(
                text = "Autonomous Executive Super-App • Principal: Aditya Mehra",
                style = MaterialTheme.typography.bodySmall,
                color = TextMutedDark,
                fontSize = 10.sp
              )
            }
          }

          Badge(containerColor = TitanEmerald.copy(alpha = 0.2f)) {
            Text("v2026.MAX", color = TitanEmerald, fontWeight = FontWeight.Bold, fontSize = 9.sp)
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Verified Truth-Anchor Badges
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          TruthBadge("DSU BBA IB '26 (41/41 First-Attempt)")
          TruthBadge("AERO India 2025 (300+ Builds • 0% Downtime)")
          TruthBadge("14 Supply Chain Hubs (42% Cycle Cut)")
          TruthBadge("Tier-1 SLAs (28% Slippage Cut)")
          TruthBadge("Instawork AI Data (>99% Accuracy)")
          TruthBadge("₹18L+ Pipeline (<48h Proposal Turnaround)")
        }
      }
    }

    // Command Centers Scrollable Tabs
    ScrollableTabRow(
      selectedTabIndex = selectedTab,
      containerColor = ObsidianDark,
      contentColor = TitanCyan,
      edgePadding = 16.dp,
      divider = { HorizontalDivider(color = SlateBorder) }
    ) {
      tabs.forEachIndexed { index, title ->
        Tab(
          selected = selectedTab == index,
          onClick = { selectedTab = index },
          text = {
            Text(
              text = title,
              color = if (selectedTab == index) TitanGold else TextSecondaryDark,
              fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
              fontSize = 12.sp
            )
          }
        )
      }
    }

    // Active Tab Content
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
      when (selectedTab) {
        0 -> WarRoomTab(viewModel)
        1 -> Keypad25Tab(viewModel)
        2 -> DarkStoreLabTab()
        3 -> TargetReconTab(viewModel)
        4 -> RedTeamVaultTab(viewModel)
        5 -> SqlStudioTab()
      }
    }
  }
}

// -------------------------------------------------------------------------------------------------
// TAB 1: 🏛️ WAR ROOM (13-MODE MULTI-AGENT C-SUITE SIMULATION)
// -------------------------------------------------------------------------------------------------
@Composable
private fun WarRoomTab(viewModel: TitanViewModel) {
  var selectedTopic by remember { mutableStateOf("Quick Commerce CM2 Margin Defense") }

  LazyColumn(
    modifier = Modifier.fillMaxSize(),
    verticalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    item {
      Text(
        text = "🏛️ SYNTHETIC C-SUITE COUNCIL WAR ROOM",
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        color = TitanCyan
      )
      Text(
        text = "Dispatches parallel autonomous executive agents across Modes A through M.",
        style = MaterialTheme.typography.bodySmall,
        color = TextMutedDark,
        fontSize = 11.sp
      )
    }

    item {
      // Council Agents Grid
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        AgentBadge("CEO", "Capital Allocation", TitanGold)
        AgentBadge("COO (Adi)", "Ground Dispatch & SLAs", TitanCyan)
        AgentBadge("CFO", "Unit Economics & CM2", TitanEmerald)
        AgentBadge("CTO", "Room DB & WorkManager", Color(0xFF818CF8))
        AgentBadge("Red-Team", "Adversarial Stress Test", TitanCrimson)
      }
    }

    item {
      // Debate Cards
      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SlateCard),
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Text("CURRENT WAR-ROOM DIRECTIVE", color = TitanGold, fontSize = 10.sp, fontWeight = FontWeight.Bold)
          Spacer(modifier = Modifier.height(4.dp))
          Text(selectedTopic, color = TextPrimaryDark, fontWeight = FontWeight.Bold, fontSize = 14.sp)
          Spacer(modifier = Modifier.height(8.dp))

          AgentSpeechBubble(
            agent = "COO (Aditya Mehra)",
            quote = "Field telemetry across 14 distribution hubs shows that 60% of picker delays stem from inventory drift, not picker speed. By re-clustering top 20% SKUs within 3m of the packing stations, we compressed cycle times from 28m to 16.2m. We must enforce this on the ground.",
            color = TitanCyan
          )

          Spacer(modifier = Modifier.height(8.dp))

          AgentSpeechBubble(
            agent = "CFO Agent",
            quote = "Unit margins are strictly defended. Net CM2 is +₹28.40/order (AOV ₹480 - COGS ₹384 - Delivery ₹48 - Pick/Pack ₹12 - Lease ₹18 + Ad Rev ₹15). If last-mile fuel climbs 5%, our 3.8% ad take-rate shields EBITDA.",
            color = TitanEmerald
          )

          Spacer(modifier = Modifier.height(8.dp))

          AgentSpeechBubble(
            agent = "Red-Team Inquisitor",
            quote = "Stress test passed. Operational numbers, vendor penalty clauses, and defense airbase zero-downtime protocols withstand scrutiny. Zero vibe coding detected.",
            color = TitanCrimson
          )

          Spacer(modifier = Modifier.height(12.dp))

          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(8.dp))
              .background(TitanGold)
              .clickable {
                viewModel.navigateTo(TitanScreen.COPILOT)
                viewModel.sendCopilotMessage("WAR-ROOM Run 13-Mode simulation on: $selectedTopic")
              }
              .padding(vertical = 10.dp),
            contentAlignment = Alignment.Center
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, tint = ObsidianDark, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Convene Full C-Suite in Copilot", color = ObsidianDark, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
          }
        }
      }
    }
  }
}

// -------------------------------------------------------------------------------------------------
// TAB 2: ⚡ 25-KEYPAD DIRECT EXECUTION MATRIX
// -------------------------------------------------------------------------------------------------
@Composable
private fun Keypad25Tab(viewModel: TitanViewModel) {
  val modules = listOf(
    Pair("1", "JD Decompiler: Reverse-engineer hiring manager anxieties"),
    Pair("2", "ATS Resume Synthesizer: 98%+ match score in [EXP-001..006]"),
    Pair("3", "Executive Pitch & Recruiter Screen Hook"),
    Pair("4", "CXO Radar: Funding, executive shifts & Bangalore intel"),
    Pair("5", "5-Stage Outreach: InMail, Email & WhatsApp hooks"),
    Pair("6", "Zero-Friction Internal Referral Request"),
    Pair("7", "Real-Time Interview HUD: Pick 90s, Pack 45s, Buffer 120s"),
    Pair("8", "STAR-V Behavioral Matrix: AERO India verified proofs"),
    Pair("9", "Hostile Bar-Raiser Counter-Attack: 6.33 CGPA & Fresher Reframe"),
    Pair("10", "Live Whiteboard Case Solver & 5-Whys Root Cause"),
    Pair("11", "Live SQL CTE & Window Function Telemetry Pipeline"),
    Pair("12", "5 High-Leverage Strategic Questions for CXO Interviewer"),
    Pair("13", "Bengaluru Tech CTC Benchmarker (25th to 90th percentile)"),
    Pair("14", "ESOP Valuation & Indian Dry-Tax Model"),
    Pair("15", "Hardball Counter-Offer Ghostwriter"),
    Pair("16", "Multi-Offer Leverage & Bidding War Accelerator"),
    Pair("17", "30-60-90 Day Boardroom Action Plan"),
    Pair("18", "Dark Store Daily Health Audit & 06:30 AM Checklist"),
    Pair("19", "Tier-1 Vendor 07:00 AM Punch-List & SLA Penalty Engine"),
    Pair("20", "Amazon-Style 1-Page Weekly Business Review (WBR) Memo"),
    Pair("21", "Sev-1 Critical Operations Triage SOP"),
    Pair("22", "EXIM Customs Desk: Incoterms 2020 & BCD/SWS/IGST Audit"),
    Pair("23", "Clean Android Software Foundry: Kotlin, Compose & WorkManager"),
    Pair("24", "Automated Brag Sheet & Performance Impact Ledger"),
    Pair("25", "Fast-Track 6-Month Appraisal & Promotion Business Case")
  )

  LazyColumn(
    modifier = Modifier.fillMaxSize(),
    verticalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    item {
      Text("⚡ 25-KEY DIRECT EXECUTION KEYPAD", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = TitanGold)
      Text("Tap any module to immediately dispatch the sovereign protocol.", style = MaterialTheme.typography.bodySmall, color = TextMutedDark, fontSize = 11.sp)
    }

    items(modules) { (key, desc) ->
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .clickable {
            viewModel.navigateTo(TitanScreen.COPILOT)
            viewModel.sendCopilotMessage("$key $desc")
          },
        colors = CardDefaults.cardColors(containerColor = SlateCard),
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
      ) {
        Row(
          modifier = Modifier.padding(10.dp).fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Box(
              modifier = Modifier
                .size(28.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(TitanGold.copy(alpha = 0.2f))
                .border(1.dp, TitanGold, RoundedCornerShape(6.dp)),
              contentAlignment = Alignment.Center
            ) {
              Text(key, color = TitanGold, fontWeight = FontWeight.Black, fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(desc, color = TextPrimaryDark, fontSize = 11.sp, lineHeight = 15.sp)
          }

          Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, tint = TitanCyan, modifier = Modifier.size(16.dp))
        }
      }
    }
  }
}

// -------------------------------------------------------------------------------------------------
// TAB 3: 🏬 DARK STORE & SUPPLY CHAIN SIMULATOR
// -------------------------------------------------------------------------------------------------
@Composable
private fun DarkStoreLabTab() {
  var pickSec by remember { mutableDoubleStateOf(86.0) }
  var packSec by remember { mutableDoubleStateOf(42.0) }
  var bufferSec by remember { mutableDoubleStateOf(95.0) }
  val totalDockSec = pickSec + packSec + bufferSec
  val totalMinutes = totalDockSec / 60.0
  val isOptimal = totalDockSec <= 255.0

  LazyColumn(
    modifier = Modifier.fillMaxSize(),
    verticalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    item {
      Text("🏬 DARK STORE & DOCK PHYSICS SIMULATOR", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = TitanCyan)
      Text("Real-time mathematical decomposition of micro-fulfillment throughput.", style = MaterialTheme.typography.bodySmall, color = TextMutedDark, fontSize = 11.sp)
    }

    item {
      // Total Dock Handover Card
      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SlateCard),
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isOptimal) TitanEmerald else TitanCrimson)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("TOTAL DOCK CYCLE TIME (T_cycle)", color = TextSecondaryDark, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Badge(containerColor = if (isOptimal) TitanEmerald else TitanCrimson) {
              Text(if (isOptimal) "SLA OPTIMAL" else "SLA BREACH", color = ObsidianDark, fontWeight = FontWeight.Bold)
            }
          }
          Spacer(modifier = Modifier.height(4.dp))
          Text("${String.format("%.1f", totalDockSec)}s (${String.format("%.2f", totalMinutes)} min)", color = TextPrimaryDark, fontSize = 24.sp, fontWeight = FontWeight.Black)
          Text("Target: ≤255s (4.25 min) inside dock • Leaves 6m for safe 1.5km delivery", color = TextMutedDark, fontSize = 10.sp)
        }
      }
    }

    item {
      // Sliders Card
      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SlateCard),
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          PhysicsSlider("1. Picker Travel & Bagging (T_pick)", pickSec, 90.0, 30.0..180.0) { pickSec = it }
          PhysicsSlider("2. Packer Verification & Sealing (T_pack)", packSec, 45.0, 20.0..90.0) { packSec = it }
          PhysicsSlider("3. Staging Buffer to Rider Handover (T_stage)", bufferSec, 120.0, 30.0..240.0) { bufferSec = it }
        }
      }
    }

    item {
      // CM2 Net Margin Unit Economics
      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SlateCard),
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Text("CONTRIBUTION MARGIN 2 (CM2) WATERFALL", color = TitanGold, fontSize = 10.sp, fontWeight = FontWeight.Bold)
          Spacer(modifier = Modifier.height(6.dp))
          EconomicsRow("Average Order Value (AOV)", "₹480.00", TextPrimaryDark)
          EconomicsRow("Cost of Goods Sold (COGS, 20% Gross)", "-₹384.00", TextMutedDark)
          EconomicsRow("Last-Mile Rider Payout", "-₹48.00", TextMutedDark)
          EconomicsRow("Store Picking & Packing Cost", "-₹12.00", TextMutedDark)
          EconomicsRow("Dark Store Lease & Utilities Allocation", "-₹18.00", TextMutedDark)
          EconomicsRow("Payment Gateway Fee (PG 1%)", "-₹5.00", TextMutedDark)
          EconomicsRow("Brand Monetization & Ads (3.1%)", "+₹15.00", TitanEmerald)
          HorizontalDivider(color = SlateBorder, modifier = Modifier.padding(vertical = 4.dp))
          EconomicsRow("Net CM2 Margin Per Order", "+₹28.00 (+5.8%)", TitanEmerald, isBold = true)
        }
      }
    }
  }
}

// -------------------------------------------------------------------------------------------------
// TAB 4: 🎯 TARGET GIANTS RECON
// -------------------------------------------------------------------------------------------------
@Composable
private fun TargetReconTab(viewModel: TitanViewModel) {
  val companies = listOf(
    CompanyReconData(
      name = "Zepto",
      industry = "Quick Commerce",
      model = "10-minute delivery, 500+ dark stores, CM2 expansion target +₹15/order",
      adityaFit = "14-hub SQL telemetry (42% cycle cut) + AERO India zero-downtime staging buffer to eliminate picker walking dead-zones."
    ),
    CompanyReconData(
      name = "Blinkit (Zomato)",
      industry = "Quick Commerce / Hyperlocal",
      model = "High SKU density, 3-5% ad revenue take-rate, batched rider routing",
      adityaFit = "Tier-1 vendor SLA governance (EXP-002) preventing morning dock chokepoints and stockout variance."
    ),
    CompanyReconData(
      name = "Razorpay",
      industry = "Fintech & Payments",
      model = "Full-stack gateway, 95%+ success rate, sub-180ms latency, merchant neo-banking",
      adityaFit = "Commercial pipeline acceleration (₹18L+, <48h proposals) and Instawork AI dataset QA (>99% accuracy)."
    ),
    CompanyReconData(
      name = "CRED",
      industry = "High-Trust Member Commerce",
      model = "High-credit-score member network, high-AOV drops, vehicle & offline commerce",
      adityaFit = "High-stakes brand activations (Puma India, Tata Communications) and clean mobile architecture."
    ),
    CompanyReconData(
      name = "Swiggy Instamart",
      industry = "Hyperlocal Grocery",
      model = "12,000+ SKU depth, mother-hub to micro-hub replenishment, defect-free dispatch",
      adityaFit = "Predictive inventory reorder threshold modeling via SQL CTEs cutting stockouts by 28%."
    )
  )

  LazyColumn(
    modifier = Modifier.fillMaxSize(),
    verticalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    items(companies) { comp ->
      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SlateCard),
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(comp.name, color = TitanGold, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Text(comp.industry, color = TextMutedDark, fontSize = 10.sp)
          }
          Spacer(modifier = Modifier.height(4.dp))
          Text("Operating Model: ${comp.model}", color = TextSecondaryDark, fontSize = 11.sp)
          Spacer(modifier = Modifier.height(4.dp))
          Text("Aditya's Unfair Advantage: ${comp.adityaFit}", color = TitanCyan, fontSize = 11.sp, fontWeight = FontWeight.Medium)
          Spacer(modifier = Modifier.height(8.dp))
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(SlateElevated)
              .border(1.dp, SlateBorder, RoundedCornerShape(6.dp))
              .clickable {
                viewModel.navigateTo(TitanScreen.COPILOT)
                viewModel.sendCopilotMessage("1 ${comp.name} Decompile JD and generate tailored pitch")
              }
              .padding(horizontal = 8.dp, vertical = 4.dp)
          ) {
            Text("Generate Bespoke Pitch for ${comp.name} →", color = TextPrimaryDark, fontSize = 10.sp)
          }
        }
      }
    }
  }
}

// -------------------------------------------------------------------------------------------------
// TAB 5: 🛡️ RED-TEAM DEFENSE VAULT
// -------------------------------------------------------------------------------------------------
@Composable
private fun RedTeamVaultTab(viewModel: TitanViewModel) {
  val probes = listOf(
    Pair(
      "Hostile Probe 1: 6.33 CGPA & Fresher Reframe",
      "A 9.5 CGPA proves you follow instructions inside a sanitized classroom. My ~6.33 CGPA was achieved while clearing 41/41 subjects on first attempt with zero backlogs, all while managing 300+ vendor builds at AERO India under defense security with 0.00% downtime and optimizing 14 supply chain hubs (42% cycle cut). If your company operates on theory, hire the 9.5. If your company operates where vendors slip and uptime is non-negotiable, hire the operator who has already delivered under fire."
    ),
    Pair(
      "Hostile Probe 2: Dark Store Picker Delays in Week 1",
      "I don't guess; I look at order telemetry. At 14 distribution hubs, I proved 60% of picker delays weren't slow pickers—they were physical inventory drift where high-velocity SKUs were placed in dead zones. In Week 1, I run SQL frequency clustering and relocate top 20% SKUs within 3 meters of the staging buffer. That cuts picker walking distance by 34% and cycle times from 28 to 16.2 minutes."
    ),
    Pair(
      "Hostile Probe 3: Vendor Budget Blowouts & Schedule Slippages",
      "Three non-negotiables: First, lock standardized rate cards upfront to eliminate unbudgeted quotes. Second, enforce milestone-linked escrows with automated penalty clauses for slippage. Third, daily 07:00 AM physical punch-list audits. At AERO India, this discipline eliminated unbudgeted overruns and cut slippages by 28%."
    ),
    Pair(
      "Hostile Probe 4: Delivery Partner Safety vs 10-Minute SLA",
      "That is a false dilemma. Delivery partners don't crash because of the last mile; they crash when dark stores waste 7 minutes on disorganized picking and packing, leaving the rider only 3 minutes to navigate traffic. By engineering the dock cycle to under 255s (pick <90s, pack <45s, buffer <120s), the rider has a calm 6 to 7 minutes for a 1.5 km delivery."
    )
  )

  LazyColumn(
    modifier = Modifier.fillMaxSize(),
    verticalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    items(probes) { (title, counterAttack) ->
      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SlateCard),
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, TitanCrimson.copy(alpha = 0.4f))
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Text(title, color = TitanCrimson, fontWeight = FontWeight.Bold, fontSize = 12.sp)
          Spacer(modifier = Modifier.height(6.dp))
          Text(counterAttack, color = TextPrimaryDark, fontSize = 11.sp, lineHeight = 16.sp)
          Spacer(modifier = Modifier.height(8.dp))
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(SlateElevated)
              .clickable {
                viewModel.navigateTo(TitanScreen.COPILOT)
                viewModel.sendCopilotMessage("4 $title")
              }
              .padding(horizontal = 8.dp, vertical = 4.dp)
          ) {
            Text("Simulate Live Defense in Copilot →", color = TitanCyan, fontSize = 10.sp)
          }
        }
      }
    }
  }
}

// -------------------------------------------------------------------------------------------------
// TAB 6: 💻 LIVE SQL & TELEMETRY STUDIO
// -------------------------------------------------------------------------------------------------
@Composable
private fun SqlStudioTab() {
  val clipboard = LocalClipboardManager.current
  var copiedQuery by remember { mutableStateOf<String?>(null) }

  val sqlQuery = """
-- Automated Morning Dark Store Telemetry & SLA Breach Triage
WITH OrderCycleMetrics AS (
    SELECT 
        order_id,
        hub_id,
        EXTRACT(EPOCH FROM (packing_completed_at - picker_assigned_at)) AS pick_sec,
        EXTRACT(EPOCH FROM (rider_handover_at - packing_completed_at)) AS staging_sec,
        EXTRACT(EPOCH FROM (rider_handover_at - order_placed_at)) AS total_dock_sec
    FROM hub_order_telemetry
    WHERE order_placed_at >= NOW() - INTERVAL '24 HOURS'
)
SELECT 
    hub_id,
    COUNT(order_id) AS total_orders,
    ROUND(AVG(pick_sec), 1) AS avg_pick_seconds,
    ROUND(AVG(staging_sec), 1) AS avg_staging_seconds,
    ROUND(COUNT(CASE WHEN total_dock_sec > 255 THEN 1 END) * 100.0 / COUNT(order_id), 2) AS breach_rate_pct,
    DENSE_RANK() OVER (ORDER BY COUNT(CASE WHEN total_dock_sec > 255 THEN 1 END) * 100.0 / COUNT(order_id) DESC) as severity_rank
FROM OrderCycleMetrics
GROUP BY hub_id
HAVING COUNT(order_id) >= 50;
  """.trimIndent()

  LazyColumn(
    modifier = Modifier.fillMaxSize(),
    verticalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    item {
      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Column {
          Text("💻 PRODUCTION SQL CTE TELEMETRY", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = TitanGold)
          Text("Optimized for PostgreSQL, Redshift & BigQuery", style = MaterialTheme.typography.bodySmall, color = TextMutedDark, fontSize = 10.sp)
        }
        IconButton(onClick = {
          clipboard.setText(AnnotatedString(sqlQuery))
          copiedQuery = "SQL Copied!"
        }) {
          Icon(Icons.Default.ContentCopy, contentDescription = "Copy SQL", tint = TitanCyan, modifier = Modifier.size(18.dp))
        }
      }
    }

    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SlateCard),
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          if (copiedQuery != null) {
            Text(copiedQuery!!, color = TitanEmerald, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
          }
          Text(
            text = sqlQuery,
            color = Color(0xFFA5B4FC),
            fontSize = 10.sp,
            fontFamily = FontFamily.Monospace,
            lineHeight = 14.sp
          )
        }
      }
    }
  }
}

// -------------------------------------------------------------------------------------------------
// HELPER UI COMPONENTS
// -------------------------------------------------------------------------------------------------
@Composable
private fun TruthBadge(text: String) {
  Box(
    modifier = Modifier
      .clip(RoundedCornerShape(6.dp))
      .background(SlateElevated)
      .border(1.dp, SlateBorder, RoundedCornerShape(6.dp))
      .padding(horizontal = 8.dp, vertical = 4.dp)
  ) {
    Text(text, color = TextSecondaryDark, fontSize = 9.sp, fontWeight = FontWeight.SemiBold)
  }
}

@Composable
private fun AgentBadge(role: String, domain: String, color: Color) {
  Box(
    modifier = Modifier
      .clip(RoundedCornerShape(6.dp))
      .background(color.copy(alpha = 0.15f))
      .border(1.dp, color.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
      .padding(horizontal = 6.dp, vertical = 4.dp)
  ) {
    Column {
      Text(role, color = color, fontWeight = FontWeight.Bold, fontSize = 10.sp)
      Text(domain, color = TextMutedDark, fontSize = 8.sp)
    }
  }
}

@Composable
private fun AgentSpeechBubble(agent: String, quote: String, color: Color) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(8.dp))
      .background(SlateElevated)
      .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
      .padding(10.dp)
  ) {
    Text(agent, color = color, fontWeight = FontWeight.Bold, fontSize = 10.sp)
    Spacer(modifier = Modifier.height(3.dp))
    Text(quote, color = TextPrimaryDark, fontSize = 11.sp, lineHeight = 15.sp)
  }
}

@Composable
private fun PhysicsSlider(label: String, value: Double, target: Double, range: ClosedFloatingPointRange<Double>, onValueChange: (Double) -> Unit) {
  Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
      Text(label, color = TextPrimaryDark, fontSize = 10.sp)
      Text("${value.toInt()}s (Target: <${target.toInt()}s)", color = if (value <= target) TitanEmerald else TitanCrimson, fontSize = 10.sp, fontWeight = FontWeight.Bold)
    }
    Slider(
      value = value.toFloat(),
      onValueChange = { onValueChange(it.toDouble()) },
      valueRange = range.start.toFloat()..range.endInclusive.toFloat(),
      colors = SliderDefaults.colors(
        thumbColor = TitanGold,
        activeTrackColor = TitanGold,
        inactiveTrackColor = SlateBorder
      )
    )
  }
}

@Composable
private fun EconomicsRow(label: String, value: String, valueColor: Color, isBold: Boolean = false) {
  Row(
    modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Text(label, color = TextSecondaryDark, fontSize = 10.sp, fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal)
    Text(value, color = valueColor, fontSize = 10.sp, fontWeight = if (isBold) FontWeight.Bold else FontWeight.SemiBold)
  }
}

private data class CompanyReconData(
  val name: String,
  val industry: String,
  val model: String,
  val adityaFit: String
)
