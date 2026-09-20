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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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

data class ScenarioPathway(
  val id: String,
  val title: String,
  val roleTrack: String,
  val location: String,
  val year1CtcLpa: Float,
  val year3CtcLpa: Float,
  val year5CtcLpa: Float,
  val careerMoatScore: Int,
  val riskLevel: String,
  val upsideSummary: String
)

@Composable
fun CareerScenarioSimulatorDialog(
  onDismissRequest: () -> Unit
) {
  val pathways = remember {
    listOf(
      ScenarioPathway(
        id = "TIER1_STRATEGY",
        title = "Tier-1 Strategy & BizOps Track",
        roleTrack = "Analyst → Senior BizOps → Chief of Staff",
        location = "Bengaluru, India",
        year1CtcLpa = 18.0f,
        year3CtcLpa = 36.0f,
        year5CtcLpa = 65.0f,
        careerMoatScore = 88,
        riskLevel = "LOW-MODERATE",
        upsideSummary = "High brand equity, Tier-1 network access, predictable 35%+ CAGR compensation, strong exit optionality to VC/Founding."
      ),
      ScenarioPathway(
        id = "B2B_AI_VENTURE",
        title = "B2B AI Operations Agency & Venture",
        roleTrack = "B2B AI Consultant → Venture Lead → Multi-Client Founder",
        location = "Bengaluru + Remote Global",
        year1CtcLpa = 24.0f,
        year3CtcLpa = 72.0f,
        year5CtcLpa = 150.0f,
        careerMoatScore = 94,
        riskLevel = "MODERATE-HIGH",
        upsideSummary = "Uncapped equity & cashflow, 85%+ gross margins, direct ownership of client data workflows, compounding business capital."
      ),
      ScenarioPathway(
        id = "GLOBAL_FINTECH",
        title = "Global FinTech / Cross-Border Tech",
        roleTrack = "Product Ops → International Strategy Manager",
        location = "Singapore / Dubai / London",
        year1CtcLpa = 42.0f,
        year3CtcLpa = 85.0f,
        year5CtcLpa = 140.0f,
        careerMoatScore = 86,
        riskLevel = "MODERATE",
        upsideSummary = "Strong currency arbitrage (SGD/AED/USD), international trade & regulatory moat, fast cross-border mobility."
      )
    )
  }

  var selectedPathwayId by remember { mutableStateOf("TIER1_STRATEGY") }
  var learningVelocityFactor by remember { mutableFloatStateOf(1.2f) }

  val activePathway = pathways.find { it.id == selectedPathwayId } ?: pathways.first()
  val adjYear1 = activePathway.year1CtcLpa * learningVelocityFactor
  val adjYear3 = activePathway.year3CtcLpa * (learningVelocityFactor * 1.05f)
  val adjYear5 = activePathway.year5CtcLpa * (learningVelocityFactor * 1.15f)

  AlertDialog(
    onDismissRequest = onDismissRequest,
    containerColor = ObsidianDark,
    shape = RoundedCornerShape(16.dp),
    modifier = Modifier
      .fillMaxWidth()
      .testTag("career_scenario_simulator_dialog"),
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
              .background(TitanEmerald.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
          ) {
            Icon(Icons.Default.RocketLaunch, contentDescription = null, tint = TitanEmerald, modifier = Modifier.size(16.dp))
          }
          Spacer(modifier = Modifier.width(8.dp))
          Column {
            Text(
              text = "CAREER SCENARIO SIMULATOR & WEALTH RADAR",
              style = MaterialTheme.typography.labelSmall,
              color = TitanEmerald,
              fontWeight = FontWeight.Bold,
              fontSize = 9.sp
            )
            Text(
              text = "5-Year Trajectory & Moat Simulation",
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
          .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Text(
          text = "SELECT SCENARIO PATHWAY:",
          style = MaterialTheme.typography.labelSmall,
          color = TitanCyan,
          fontWeight = FontWeight.Bold,
          fontSize = 9.sp
        )

        // Pathway selection cards
        pathways.forEach { pathway ->
          val isSelected = pathway.id == selectedPathwayId
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .clickable { selectedPathwayId = pathway.id },
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = if (isSelected) SlateElevated else SlateCard),
            border = androidx.compose.foundation.BorderStroke(
              1.dp,
              if (isSelected) TitanEmerald else SlateBorder
            )
          ) {
            Column(modifier = Modifier.padding(10.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = pathway.title,
                  style = MaterialTheme.typography.bodyMedium,
                  fontWeight = FontWeight.Bold,
                  color = if (isSelected) TitanEmerald else TextPrimaryDark,
                  fontSize = 12.sp
                )
                Text(
                  text = pathway.riskLevel,
                  style = MaterialTheme.typography.labelSmall,
                  color = TitanGold,
                  fontSize = 8.sp,
                  fontWeight = FontWeight.Bold
                )
              }
              Text(
                text = "${pathway.roleTrack} • ${pathway.location}",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondaryDark,
                fontSize = 10.sp
              )
            }
          }
        }

        // Learning & AI Leverage Multiplier
        Column {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "AI MULTIPLIER & EXECUTION VELOCITY:",
              style = MaterialTheme.typography.labelSmall,
              color = TitanGold,
              fontSize = 9.sp,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = "${String.format("%.2f", learningVelocityFactor)}x Compound",
              style = MaterialTheme.typography.labelMedium,
              color = TitanGold,
              fontWeight = FontWeight.Bold
            )
          }

          Slider(
            value = learningVelocityFactor,
            onValueChange = { learningVelocityFactor = it },
            valueRange = 1.0f..2.0f,
            steps = 9,
            colors = SliderDefaults.colors(
              thumbColor = TitanGold,
              activeTrackColor = TitanGold,
              inactiveTrackColor = SlateBorder
            )
          )
        }

        // 5-Year Compensation Curve Forecast
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(SlateElevated)
            .border(1.dp, TitanEmerald.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
            .padding(10.dp),
          verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Text(
            text = "PROJECTED COMPENSATION TRAJECTORY (ANNUAL CTC)",
            style = MaterialTheme.typography.labelSmall,
            color = TitanEmerald,
            fontWeight = FontWeight.Bold,
            fontSize = 9.sp
          )

          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Year 1 (Immediate Alignment):", color = TextSecondaryDark, fontSize = 11.sp)
            Text("₹${String.format("%.1f", adjYear1)} LPA", color = TextPrimaryDark, fontWeight = FontWeight.Bold, fontSize = 11.sp)
          }

          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Year 3 (Lead / Senior Scope):", color = TextSecondaryDark, fontSize = 11.sp)
            Text("₹${String.format("%.1f", adjYear3)} LPA", color = TitanGold, fontWeight = FontWeight.Bold, fontSize = 11.sp)
          }

          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Year 5 (Principal / Executive / Founder):", color = TextSecondaryDark, fontSize = 11.sp)
            Text("₹${String.format("%.1f", adjYear5)} LPA", color = TitanEmerald, fontWeight = FontWeight.Black, fontSize = 11.sp)
          }

          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Career Moat & Defensibility Score:", color = TextSecondaryDark, fontSize = 11.sp)
            Text("${activePathway.careerMoatScore}/100", color = TitanCyan, fontWeight = FontWeight.Bold, fontSize = 11.sp)
          }
        }

        // Qualitative Strategic Teardown
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(SlateCard)
            .padding(8.dp)
        ) {
          Column {
            Text(
              text = "STRATEGIC EVALUATION & UPSIDE ANALYSIS:",
              style = MaterialTheme.typography.labelSmall,
              color = TitanCyan,
              fontWeight = FontWeight.Bold,
              fontSize = 8.sp
            )
            Text(
              text = activePathway.upsideSummary,
              style = MaterialTheme.typography.bodySmall,
              color = TextSecondaryDark,
              fontSize = 10.sp,
              lineHeight = 14.sp
            )
          }
        }
      }
    },
    confirmButton = {
      Button(
        onClick = onDismissRequest,
        colors = ButtonDefaults.buttonColors(containerColor = TitanEmerald, contentColor = ObsidianDark),
        shape = RoundedCornerShape(8.dp)
      ) {
        Text("Done", fontWeight = FontWeight.Bold, fontSize = 11.sp)
      }
    }
  )
}
