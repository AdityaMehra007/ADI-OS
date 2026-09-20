package com.example.ui.components

import android.content.Intent
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
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
import java.text.NumberFormat
import java.util.Locale

@Composable
fun SalaryCalculatorDialog(
  onDismissRequest: () -> Unit,
  initialCtcLakhs: Float = 18.0f,
  companyName: String = "Target Strategy Firm",
  roleTitle: String = "Associate Strategy / Business Analyst"
) {
  val context = LocalContext.current
  val clipboardManager = LocalClipboardManager.current

  var annualCtcLakhs by remember { mutableFloatStateOf(initialCtcLakhs) }
  var basePercent by remember { mutableFloatStateOf(0.70f) }
  var variablePercent by remember { mutableFloatStateOf(0.15f) }
  var bonusPercent by remember { mutableFloatStateOf(0.10f) }
  var copyFeedback by remember { mutableStateOf(false) }

  // Calculations
  val totalAnnual = annualCtcLakhs * 100_000.0
  val basePayAnnual = totalAnnual * basePercent
  val variableAnnual = totalAnnual * variablePercent
  val bonusAnnual = totalAnnual * bonusPercent

  // Deductions
  val pfAnnual = (basePayAnnual * 0.12).coerceAtMost(21_600.0 * 12) // Provident fund
  val karnatakaPtAnnual = 2_400.0 // Karnataka Professional Tax

  // Tax calculation under India New Tax Regime (FY 2024-25/25-26) with standard deduction ₹75,000
  val standardDeduction = 75_000.0
  val taxableIncome = (basePayAnnual - standardDeduction).coerceAtLeast(0.0)

  val annualTax = when {
    taxableIncome <= 700_000.0 -> 0.0 // 87A rebate covers up to ₹7L
    else -> {
      var tax = 0.0
      val slab1 = (taxableIncome - 300_000.0).coerceIn(0.0, 400_000.0) * 0.05
      val slab2 = (taxableIncome - 700_000.0).coerceIn(0.0, 300_000.0) * 0.10
      val slab3 = (taxableIncome - 1_000_000.0).coerceIn(0.0, 200_000.0) * 0.15
      val slab4 = (taxableIncome - 1_200_000.0).coerceIn(0.0, 300_000.0) * 0.20
      val slab5 = (taxableIncome - 1_500_000.0).coerceAtLeast(0.0) * 0.30
      tax = (slab1 + slab2 + slab3 + slab4 + slab5) * 1.04 // 4% Cess
      tax
    }
  }

  val monthlyGrossBase = basePayAnnual / 12.0
  val monthlyPf = pfAnnual / 12.0
  val monthlyPt = karnatakaPtAnnual / 12.0
  val monthlyTax = annualTax / 12.0
  val monthlyInHand = (monthlyGrossBase - monthlyPf - monthlyPt - monthlyTax).coerceAtLeast(0.0)

  val currencyFormat = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("en-IN"))
  currencyFormat.maximumFractionDigits = 0

  val counterOfferPitch = """
Subject: Offer Discussion - $roleTitle - Adi

Dear Hiring Team at $companyName,

Thank you for extending the offer for the $roleTitle role. I am enthusiastic about the opportunity to drive strategic outcomes and analytics for $companyName in Bengaluru.

Based on my operational track record (42% cycle reduction in trading ops, SQL/Python automation, BBA International Business merit) and current market benchmarks for top-tier strategy cohorts, I would like to propose the following alignment:

1. Target Base Compensation: ${String.format("%.1f", annualCtcLakhs * 1.15)} LPA (representing ₹${currencyFormat.format(monthlyInHand * 1.15)} / month net cash flow).
2. Joining Bonus / Retention Tranche: ₹${currencyFormat.format(bonusAnnual * 1.25)} upfront to offset immediate relocation and sign-on transition.

I am ready to sign and confirm joining within 48 hours upon this alignment.

Best regards,
Adi
  """.trimIndent()

  AlertDialog(
    onDismissRequest = onDismissRequest,
    containerColor = ObsidianDark,
    shape = RoundedCornerShape(16.dp),
    modifier = Modifier.testTag("salary_calculator_dialog"),
    title = {
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
              .background(TitanEmerald.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
          ) {
            Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = TitanEmerald, modifier = Modifier.size(18.dp))
          }
          Spacer(modifier = Modifier.width(8.dp))
          Column {
            Text(
              text = "BENGALURU CTC & IN-HAND MODELER",
              style = MaterialTheme.typography.labelSmall,
              color = TitanEmerald,
              fontWeight = FontWeight.Bold,
              fontSize = 9.sp
            )
            Text(
              text = "$companyName • $roleTitle",
              style = MaterialTheme.typography.titleMedium,
              color = TextPrimaryDark,
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp
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
        // Net Monthly In-Hand Cash Highlight
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SlateElevated)
            .border(1.dp, TitanEmerald.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
            .padding(12.dp)
        ) {
          Column {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "ESTIMATED MONTHLY IN-HAND (NET)",
                style = MaterialTheme.typography.labelSmall,
                color = TitanEmerald,
                fontWeight = FontWeight.Bold,
                fontSize = 9.sp
              )
              Text(
                text = "New Tax Regime FY 24-25",
                style = MaterialTheme.typography.labelSmall,
                color = TextMutedDark,
                fontSize = 8.sp
              )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
              text = "₹${currencyFormat.format(monthlyInHand).replace("₹", "")} / mo",
              style = MaterialTheme.typography.headlineMedium,
              color = TitanEmerald,
              fontWeight = FontWeight.Black
            )

            Text(
              text = "Gross Base: ₹${currencyFormat.format(monthlyGrossBase).replace("₹", "")}/mo | PF: -₹${currencyFormat.format(monthlyPf).replace("₹", "")} | PT: -₹200 | TDS: -₹${currencyFormat.format(monthlyTax).replace("₹", "")}",
              style = MaterialTheme.typography.bodySmall,
              color = TextSecondaryDark,
              fontSize = 9.sp
            )
          }
        }

        // CTC Slider Control
        Column {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "ANNUAL TOTAL CTC:",
              style = MaterialTheme.typography.labelSmall,
              color = TitanGold,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = "₹${String.format("%.1f", annualCtcLakhs)} LPA",
              style = MaterialTheme.typography.titleMedium,
              color = TitanGold,
              fontWeight = FontWeight.Black
            )
          }

          Slider(
            value = annualCtcLakhs,
            onValueChange = { annualCtcLakhs = it },
            valueRange = 8.0f..35.0f,
            steps = 26,
            colors = SliderDefaults.colors(
              thumbColor = TitanGold,
              activeTrackColor = TitanGold,
              inactiveTrackColor = SlateBorder
            )
          )
        }

        // Breakdown Matrix
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(SlateCard)
            .border(1.dp, SlateBorder, RoundedCornerShape(10.dp))
            .padding(10.dp),
          verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Text(
            text = "COMPENSATION BREAKDOWN MATRIX",
            style = MaterialTheme.typography.labelSmall,
            color = TitanCyan,
            fontWeight = FontWeight.Bold,
            fontSize = 9.sp
          )

          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Fixed Base Pay (70%):", color = TextSecondaryDark, fontSize = 11.sp)
            Text("₹${currencyFormat.format(basePayAnnual).replace("₹", "")} / yr", color = TextPrimaryDark, fontWeight = FontWeight.Bold, fontSize = 11.sp)
          }

          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Performance Variable (15%):", color = TextSecondaryDark, fontSize = 11.sp)
            Text("₹${currencyFormat.format(variableAnnual).replace("₹", "")} / yr", color = TitanGold, fontWeight = FontWeight.Bold, fontSize = 11.sp)
          }

          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Joining / Sign-on Bonus (10%):", color = TextSecondaryDark, fontSize = 11.sp)
            Text("₹${currencyFormat.format(bonusAnnual).replace("₹", "")} (1st Year)", color = TitanCyan, fontWeight = FontWeight.Bold, fontSize = 11.sp)
          }

          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Estimated Annual Income Tax:", color = TextSecondaryDark, fontSize = 11.sp)
            Text("₹${currencyFormat.format(annualTax).replace("₹", "")}", color = if (annualTax > 0) TitanGold else TitanEmerald, fontWeight = FontWeight.Bold, fontSize = 11.sp)
          }
        }

        // Negotiation Levers Callout
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(SlateElevated)
            .padding(8.dp)
        ) {
          Column {
            Text(
              text = "EXEC STRATEGY LEVER:",
              style = MaterialTheme.typography.labelSmall,
              color = TitanCyan,
              fontWeight = FontWeight.Bold,
              fontSize = 8.sp
            )
            Text(
              text = "If HR resists increasing Fixed Base beyond ₹${String.format("%.1f", annualCtcLakhs * 0.75)}L, counter-negotiate a guaranteed ₹${String.format("%.1f", annualCtcLakhs * 0.15)}L upfront Joining Bonus or 6-month performance appraisal milestone.",
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
      Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Button(
          onClick = {
            clipboardManager.setText(AnnotatedString(counterOfferPitch))
            val sendIntent = Intent().apply {
              action = Intent.ACTION_SEND
              putExtra(Intent.EXTRA_TEXT, counterOfferPitch)
              type = "text/plain"
            }
            try {
              context.startActivity(Intent.createChooser(sendIntent, "Share Counter-Offer Script"))
            } catch (_: Exception) {}
          },
          colors = ButtonDefaults.buttonColors(containerColor = TitanCyan, contentColor = ObsidianDark),
          shape = RoundedCornerShape(8.dp)
        ) {
          Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(14.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Share Pitch", fontWeight = FontWeight.Bold, fontSize = 11.sp)
        }

        Button(
          onClick = {
            clipboardManager.setText(AnnotatedString(counterOfferPitch))
            copyFeedback = true
          },
          colors = ButtonDefaults.buttonColors(containerColor = TitanEmerald, contentColor = ObsidianDark),
          shape = RoundedCornerShape(8.dp)
        ) {
          Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text(if (copyFeedback) "Copied!" else "Copy Script", fontWeight = FontWeight.Bold, fontSize = 11.sp)
        }
      }
    }
  )
}
