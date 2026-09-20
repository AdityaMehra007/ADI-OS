package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
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
import com.example.ui.theme.TitanCyan
import com.example.ui.theme.TitanEmerald
import com.example.ui.theme.TitanGold
import com.example.ui.theme.TitanIndigo
import com.example.ui.theme.TitanViolet

enum class MarketTier(val label: String, val description: String) {
  BIG_TECH_FAANG("Tier-1 Big Tech / FAANG", "Google, Microsoft, Meta, Uber, Stripe"),
  UNICORN_HIGH_GROWTH("High-Growth Unicorn", "Zepto, Razorpay, Swiggy, CRED, Meesho"),
  ENTERPRISE_SAAS("Global Enterprise SaaS", "Salesforce, Adobe, Snowflake, Atlassian"),
  EARLY_STAGE("Series A / B High-Equity", "Pre-IPO Startups with High Equity Upside")
}

enum class CareerSeniority(val label: String, val levelCode: String) {
  EARLY_CAREER("Early-Career / Associate", "L3 / IC3"),
  MID_LEVEL("Mid-Level Specialist", "L4 / IC4"),
  SENIOR("Senior Strategist / Architect", "L5 / IC5"),
  STAFF_PRINCIPAL("Staff / Principal Lead", "L6 / IC6")
}

data class CompensationBenchmark(
  val p25TotalCompLakhs: Float,
  val p50TotalCompLakhs: Float,
  val p75TotalCompLakhs: Float,
  val p90TotalCompLakhs: Float,
  val baseSalaryLakhs: Float,
  val annualBonusPercent: Int,
  val fourYearEquityLakhs: Float,
  val vestingSchedule: String,
  val negotiationLeverageAdvice: String
)

object MarketCompensationRepository {
  fun getBenchmark(tier: MarketTier, seniority: CareerSeniority): CompensationBenchmark {
    return when (tier) {
      MarketTier.BIG_TECH_FAANG -> when (seniority) {
        CareerSeniority.EARLY_CAREER -> CompensationBenchmark(
          p25TotalCompLakhs = 22f,
          p50TotalCompLakhs = 28f,
          p75TotalCompLakhs = 34f,
          p90TotalCompLakhs = 40f,
          baseSalaryLakhs = 18f,
          annualBonusPercent = 15,
          fourYearEquityLakhs = 32f,
          vestingSchedule = "25% / 25% / 25% / 25% (Standard 1-year cliff)",
          negotiationLeverageAdvice = "Highlight counteroffers from rival Tier-1s; Big Tech recruiters have flexible sign-on bonus pools to bridge gaps."
        )
        CareerSeniority.MID_LEVEL -> CompensationBenchmark(
          p25TotalCompLakhs = 38f,
          p50TotalCompLakhs = 48f,
          p75TotalCompLakhs = 58f,
          p90TotalCompLakhs = 68f,
          baseSalaryLakhs = 32f,
          annualBonusPercent = 15,
          fourYearEquityLakhs = 65f,
          vestingSchedule = "25% / 25% / 25% / 25% or Google Frontloaded (33/33/22/12)",
          negotiationLeverageAdvice = "Leverage system architecture experience and cross-functional project scale; RSU refreshers are tied to performance tiers."
        )
        CareerSeniority.SENIOR -> CompensationBenchmark(
          p25TotalCompLakhs = 65f,
          p50TotalCompLakhs = 82f,
          p75TotalCompLakhs = 105f,
          p90TotalCompLakhs = 130f,
          baseSalaryLakhs = 52f,
          annualBonusPercent = 20,
          fourYearEquityLakhs = 120f,
          vestingSchedule = "25% annually with annual equity refresher grants",
          negotiationLeverageAdvice = "Senior L5+ packages are heavily equity-driven. Push for upper-quartile initial RSU allocation over small base increments."
        )
        CareerSeniority.STAFF_PRINCIPAL -> CompensationBenchmark(
          p25TotalCompLakhs = 110f,
          p50TotalCompLakhs = 145f,
          p75TotalCompLakhs = 190f,
          p90TotalCompLakhs = 250f,
          baseSalaryLakhs = 75f,
          annualBonusPercent = 25,
          fourYearEquityLakhs = 280f,
          vestingSchedule = "Frontloaded / Quarterly RSU distributions with discretionary VP multipliers",
          negotiationLeverageAdvice = "Principal packages require Compensation Committee sign-off. Emphasize multi-org strategic impact and patents/open-source authority."
        )
      }
      MarketTier.UNICORN_HIGH_GROWTH -> when (seniority) {
        CareerSeniority.EARLY_CAREER -> CompensationBenchmark(
          p25TotalCompLakhs = 18f,
          p50TotalCompLakhs = 24f,
          p75TotalCompLakhs = 30f,
          p90TotalCompLakhs = 36f,
          baseSalaryLakhs = 16f,
          annualBonusPercent = 10,
          fourYearEquityLakhs = 20f,
          vestingSchedule = "10% / 20% / 30% / 40% or 25% annual vesting",
          negotiationLeverageAdvice = "Unicorns value immediate execution velocity. Highlight hands-on end-to-end delivery of 0-to-1 features."
        )
        CareerSeniority.MID_LEVEL -> CompensationBenchmark(
          p25TotalCompLakhs = 32f,
          p50TotalCompLakhs = 42f,
          p75TotalCompLakhs = 52f,
          p90TotalCompLakhs = 62f,
          baseSalaryLakhs = 28f,
          annualBonusPercent = 12,
          fourYearEquityLakhs = 48f,
          vestingSchedule = "25% annual vesting with ESOP liquidity buyback history",
          negotiationLeverageAdvice = "Ask for ESOP strike price and latest 409A valuation; request ESOP acceleration upon acquisition or IPO."
        )
        CareerSeniority.SENIOR -> CompensationBenchmark(
          p25TotalCompLakhs = 55f,
          p50TotalCompLakhs = 70f,
          p75TotalCompLakhs = 90f,
          p90TotalCompLakhs = 115f,
          baseSalaryLakhs = 46f,
          annualBonusPercent = 15,
          fourYearEquityLakhs = 90f,
          vestingSchedule = "Monthly vesting post-1yr cliff with guaranteed secondary buyback windows",
          negotiationLeverageAdvice = "Negotiate joining bonus to offset unvested equity from current employer; request dual-track liquidity clauses."
        )
        CareerSeniority.STAFF_PRINCIPAL -> CompensationBenchmark(
          p25TotalCompLakhs = 90f,
          p50TotalCompLakhs = 120f,
          p75TotalCompLakhs = 160f,
          p90TotalCompLakhs = 210f,
          baseSalaryLakhs = 65f,
          annualBonusPercent = 20,
          fourYearEquityLakhs = 200f,
          vestingSchedule = "Founder-pool stock grants with board observer / advisory privileges",
          negotiationLeverageAdvice = "Benchmark directly against C-level or VP packages. Focus on equity upside multiple (3x-5x target)."
        )
      }
      MarketTier.ENTERPRISE_SAAS -> when (seniority) {
        CareerSeniority.EARLY_CAREER -> CompensationBenchmark(
          p25TotalCompLakhs = 16f,
          p50TotalCompLakhs = 20f,
          p75TotalCompLakhs = 26f,
          p90TotalCompLakhs = 32f,
          baseSalaryLakhs = 15f,
          annualBonusPercent = 10,
          fourYearEquityLakhs = 18f,
          vestingSchedule = "25% annual vesting + ESPP (15% discount)",
          negotiationLeverageAdvice = "Enterprise SaaS offers high stability and excellent work-life balance; optimize for ESPP enrollment and sign-on."
        )
        CareerSeniority.MID_LEVEL -> CompensationBenchmark(
          p25TotalCompLakhs = 28f,
          p50TotalCompLakhs = 36f,
          p75TotalCompLakhs = 46f,
          p90TotalCompLakhs = 55f,
          baseSalaryLakhs = 26f,
          annualBonusPercent = 12,
          fourYearEquityLakhs = 38f,
          vestingSchedule = "Quarterly vesting post-1yr cliff with stable public RSUs",
          negotiationLeverageAdvice = "Emphasize security, compliance, and enterprise customer stability metrics."
        )
        CareerSeniority.SENIOR -> CompensationBenchmark(
          p25TotalCompLakhs = 48f,
          p50TotalCompLakhs = 62f,
          p75TotalCompLakhs = 78f,
          p90TotalCompLakhs = 95f,
          baseSalaryLakhs = 42f,
          annualBonusPercent = 15,
          fourYearEquityLakhs = 75f,
          vestingSchedule = "Quarterly public stock grants with predictable cash bonus",
          negotiationLeverageAdvice = "Enterprise SaaS HR follows tight compensation bands; ask for promotion acceleration clause in year 1."
        )
        CareerSeniority.STAFF_PRINCIPAL -> CompensationBenchmark(
          p25TotalCompLakhs = 80f,
          p50TotalCompLakhs = 105f,
          p75TotalCompLakhs = 140f,
          p90TotalCompLakhs = 180f,
          baseSalaryLakhs = 58f,
          annualBonusPercent = 20,
          fourYearEquityLakhs = 160f,
          vestingSchedule = "Quarterly vesting with long-term retention bonus tranches",
          negotiationLeverageAdvice = "Leverage competitive offers from FAANG; enterprise compensation teams match Big Tech RSUs for critical staff."
        )
      }
      MarketTier.EARLY_STAGE -> when (seniority) {
        CareerSeniority.EARLY_CAREER -> CompensationBenchmark(
          p25TotalCompLakhs = 12f,
          p50TotalCompLakhs = 16f,
          p75TotalCompLakhs = 22f,
          p90TotalCompLakhs = 28f,
          baseSalaryLakhs = 14f,
          annualBonusPercent = 0,
          fourYearEquityLakhs = 30f,
          vestingSchedule = "4-year vesting with 1-year cliff (0.1% - 0.25% ownership)",
          negotiationLeverageAdvice = "Base will be lower than Big Tech; maximize equity percentage and exercise window (ask for 10-year PTE)."
        )
        CareerSeniority.MID_LEVEL -> CompensationBenchmark(
          p25TotalCompLakhs = 22f,
          p50TotalCompLakhs = 30f,
          p75TotalCompLakhs = 40f,
          p90TotalCompLakhs = 50f,
          baseSalaryLakhs = 24f,
          annualBonusPercent = 5,
          fourYearEquityLakhs = 60f,
          vestingSchedule = "4-year vesting (0.3% - 0.6% company equity)",
          negotiationLeverageAdvice = "Negotiate early exercise with 83(b) election to minimize capital gains tax on future upside."
        )
        CareerSeniority.SENIOR -> CompensationBenchmark(
          p25TotalCompLakhs = 40f,
          p50TotalCompLakhs = 52f,
          p75TotalCompLakhs = 68f,
          p90TotalCompLakhs = 85f,
          baseSalaryLakhs = 38f,
          annualBonusPercent = 10,
          fourYearEquityLakhs = 110f,
          vestingSchedule = "1.0% - 2.0% equity pool with single/double trigger acceleration",
          negotiationLeverageAdvice = "Demand double-trigger acceleration upon change of control; ensure non-dilution protection during immediate Series A."
        )
        CareerSeniority.STAFF_PRINCIPAL -> CompensationBenchmark(
          p25TotalCompLakhs = 65f,
          p50TotalCompLakhs = 85f,
          p75TotalCompLakhs = 115f,
          p90TotalCompLakhs = 150f,
          baseSalaryLakhs = 50f,
          annualBonusPercent = 10,
          fourYearEquityLakhs = 220f,
          vestingSchedule = "2.0% - 4.5% founding engineer / principal equity stake",
          negotiationLeverageAdvice = "Structure package as co-founding or founding architect with direct cap table representation and board advisory seats."
        )
      }
    }
  }
}

@Composable
fun MarketCompensationHeatmapComponent(
  modifier: Modifier = Modifier
) {
  var selectedTier by remember { mutableStateOf(MarketTier.BIG_TECH_FAANG) }
  var selectedSeniority by remember { mutableStateOf(CareerSeniority.SENIOR) }

  val benchmark = remember(selectedTier, selectedSeniority) {
    MarketCompensationRepository.getBenchmark(selectedTier, selectedSeniority)
  }

  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("market_compensation_heatmap_card"),
    colors = CardDefaults.cardColors(containerColor = SlateCard),
    shape = RoundedCornerShape(16.dp),
    border = BorderStroke(1.dp, SlateBorder)
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      // Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(24.dp)
                .background(TitanGold.copy(alpha = 0.15f), RoundedCornerShape(6.dp)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.MonetizationOn,
                contentDescription = null,
                tint = TitanGold,
                modifier = Modifier.size(16.dp)
              )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "2026 MARKET COMPENSATION BENCHMARK & HEATMAP",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = TitanGold,
              letterSpacing = 1.1.sp
            )
          }
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = "Total Rewards & Equity Percentiles",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimaryDark
          )
          Text(
            text = "Verified compensation distributions across Tier-1 Tech, Unicorns & SaaS",
            fontSize = 12.sp,
            color = TextMutedDark
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Tier Selector Pills
      Text(text = "Market Tier:", fontSize = 10.sp, color = TextMutedDark, fontWeight = FontWeight.Bold)
      Spacer(modifier = Modifier.height(4.dp))
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        MarketTier.values().forEach { tier ->
          val isSelected = selectedTier == tier
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .background(if (isSelected) TitanGold.copy(alpha = 0.2f) else SlateElevated)
              .border(1.dp, if (isSelected) TitanGold else SlateBorder, RoundedCornerShape(8.dp))
              .clickable { selectedTier = tier }
              .padding(horizontal = 10.dp, vertical = 6.dp)
          ) {
            Text(
              text = tier.label,
              fontSize = 11.sp,
              color = if (isSelected) TitanGold else TextSecondaryDark,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Seniority Selector Pills
      Text(text = "Career Seniority Level:", fontSize = 10.sp, color = TextMutedDark, fontWeight = FontWeight.Bold)
      Spacer(modifier = Modifier.height(4.dp))
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        CareerSeniority.values().forEach { seniority ->
          val isSelected = selectedSeniority == seniority
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .background(if (isSelected) TitanCyan.copy(alpha = 0.2f) else SlateElevated)
              .border(1.dp, if (isSelected) TitanCyan else SlateBorder, RoundedCornerShape(8.dp))
              .clickable { selectedSeniority = seniority }
              .padding(horizontal = 10.dp, vertical = 6.dp)
          ) {
            Text(
              text = "${seniority.levelCode} • ${seniority.label}",
              fontSize = 11.sp,
              color = if (isSelected) TitanCyan else TextSecondaryDark,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Percentile Cards Strip (25th, Median/50th, 75th, 90th)
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        // 25th Percentile
        CompPercentileCard(
          percentile = "25th (Entry)",
          amountLakhs = benchmark.p25TotalCompLakhs,
          labelColor = TextMutedDark,
          modifier = Modifier.weight(1f)
        )
        // 50th Percentile (Median)
        CompPercentileCard(
          percentile = "50th (Median)",
          amountLakhs = benchmark.p50TotalCompLakhs,
          labelColor = TitanCyan,
          isHighlight = true,
          modifier = Modifier.weight(1f)
        )
        // 75th Percentile
        CompPercentileCard(
          percentile = "75th (Top 25%)",
          amountLakhs = benchmark.p75TotalCompLakhs,
          labelColor = TitanEmerald,
          modifier = Modifier.weight(1f)
        )
        // 90th Percentile
        CompPercentileCard(
          percentile = "90th (Top 10%)",
          amountLakhs = benchmark.p90TotalCompLakhs,
          labelColor = TitanGold,
          modifier = Modifier.weight(1f)
        )
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Distribution Bar Visualization
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(10.dp))
          .background(ObsidianDark)
          .border(1.dp, SlateBorder, RoundedCornerShape(10.dp))
          .padding(12.dp)
      ) {
        Text(
          text = "ANNUALIZED TOTAL REWARDS BREAKDOWN (MEDIAN ₹${benchmark.p50TotalCompLakhs}L)",
          fontSize = 10.sp,
          fontWeight = FontWeight.Bold,
          color = TitanCyan,
          letterSpacing = 0.8.sp
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Component Bars: Base + Bonus + Annualized Equity
        val annualEquityLakhs = benchmark.fourYearEquityLakhs / 4f
        val annualBonusLakhs = benchmark.baseSalaryLakhs * (benchmark.annualBonusPercent / 100f)
        val totalMedian = benchmark.baseSalaryLakhs + annualBonusLakhs + annualEquityLakhs

        Row(
          modifier = Modifier
            .fillMaxWidth()
            .height(18.dp)
            .clip(RoundedCornerShape(6.dp))
        ) {
          // Base
          val baseWeight = (benchmark.baseSalaryLakhs / totalMedian).coerceIn(0.1f, 0.9f)
          Box(
            modifier = Modifier
              .weight(baseWeight)
              .height(18.dp)
              .background(TitanCyan)
          )
          // Bonus
          val bonusWeight = (annualBonusLakhs / totalMedian).coerceIn(0.05f, 0.5f)
          Box(
            modifier = Modifier
              .weight(bonusWeight)
              .height(18.dp)
              .background(TitanGold)
          )
          // Equity
          val equityWeight = (annualEquityLakhs / totalMedian).coerceIn(0.1f, 0.9f)
          Box(
            modifier = Modifier
              .weight(equityWeight)
              .height(18.dp)
              .background(TitanEmerald)
          )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Legend Row
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(8.dp).background(TitanCyan, RoundedCornerShape(2.dp)))
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = "Base: ₹${benchmark.baseSalaryLakhs}L", fontSize = 10.sp, color = TextSecondaryDark)
          }
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(8.dp).background(TitanGold, RoundedCornerShape(2.dp)))
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = "Bonus: ${benchmark.annualBonusPercent}% (₹${String.format("%.1f", annualBonusLakhs)}L)", fontSize = 10.sp, color = TextSecondaryDark)
          }
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(8.dp).background(TitanEmerald, RoundedCornerShape(2.dp)))
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = "Equity: ₹${String.format("%.1f", annualEquityLakhs)}L/yr", fontSize = 10.sp, color = TextSecondaryDark)
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Vesting Schedule & Strategic Negotiation Insight
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        // Vesting Info
        Box(
          modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(10.dp))
            .background(SlateElevated)
            .border(1.dp, SlateBorder, RoundedCornerShape(10.dp))
            .padding(10.dp)
        ) {
          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(imageVector = Icons.Default.Security, contentDescription = null, tint = TitanCyan, modifier = Modifier.size(13.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text(text = "EQUITY VESTING SCHEDULE", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TitanCyan)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = benchmark.vestingSchedule, fontSize = 11.sp, color = TextPrimaryDark, lineHeight = 14.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "4-Yr Grant: ₹${benchmark.fourYearEquityLakhs}L", fontSize = 10.sp, color = TitanEmerald, fontWeight = FontWeight.Bold)
          }
        }

        // Negotiation Leverage Advice
        Box(
          modifier = Modifier
            .weight(1.3f)
            .clip(RoundedCornerShape(10.dp))
            .background(SlateElevated)
            .border(1.dp, SlateBorder, RoundedCornerShape(10.dp))
            .padding(10.dp)
        ) {
          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(imageVector = Icons.AutoMirrored.Filled.TrendingUp, contentDescription = null, tint = TitanGold, modifier = Modifier.size(13.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text(text = "NEGOTIATION LEVERAGE ADVICE", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TitanGold)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = benchmark.negotiationLeverageAdvice, fontSize = 11.sp, color = TextPrimaryDark, lineHeight = 14.sp)
          }
        }
      }
    }
  }
}

@Composable
private fun CompPercentileCard(
  percentile: String,
  amountLakhs: Float,
  labelColor: Color,
  modifier: Modifier = Modifier,
  isHighlight: Boolean = false
) {
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(10.dp))
      .background(if (isHighlight) TitanCyan.copy(alpha = 0.12f) else SlateElevated)
      .border(
        1.dp,
        if (isHighlight) TitanCyan.copy(alpha = 0.5f) else SlateBorder,
        RoundedCornerShape(10.dp)
      )
      .padding(8.dp)
  ) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
      Text(
        text = percentile,
        fontSize = 9.sp,
        fontWeight = FontWeight.Bold,
        color = labelColor
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = "₹${amountLakhs.toInt()}L",
        fontSize = 14.sp,
        fontWeight = FontWeight.ExtraBold,
        color = TextPrimaryDark,
        fontFamily = FontFamily.Monospace
      )
      Text(
        text = "($${(amountLakhs * 1.2f).toInt()}k)",
        fontSize = 9.sp,
        color = TextMutedDark,
        fontFamily = FontFamily.Monospace
      )
    }
  }
}
