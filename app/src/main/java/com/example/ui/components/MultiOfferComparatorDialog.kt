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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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

data class OfferModel(
  val company: String,
  val role: String,
  val baseLpa: Float,
  val variableLpa: Float,
  val esopsLpa: Float,
  val joiningBonusLpa: Float,
  val brandScore: Int, // 1-10
  val learningSlope: Int, // 1-10
  val managerQuality: Int, // 1-10
  val location: String,
  val isRecommended: Boolean
) {
  val totalFirstYear = baseLpa + variableLpa + joiningBonusLpa + (esopsLpa * 0.25f)
  val decisionScore = (baseLpa * 1.5f) + (brandScore * 4f) + (learningSlope * 4.5f) + (managerQuality * 3f)
}

@Composable
fun MultiOfferComparatorDialog(
  onDismissRequest: () -> Unit
) {
  val sampleOffers = remember {
    listOf(
      OfferModel(
        company = "Zepto",
        role = "Strategy & Ops Analyst",
        baseLpa = 16.5f,
        variableLpa = 3.5f,
        esopsLpa = 4.0f,
        joiningBonusLpa = 2.0f,
        brandScore = 9,
        learningSlope = 10,
        managerQuality = 9,
        location = "Bengaluru (HSR Layout)",
        isRecommended = true
      ),
      OfferModel(
        company = "Razorpay",
        role = "Business Analyst - Strategy",
        baseLpa = 18.0f,
        variableLpa = 2.0f,
        esopsLpa = 5.0f,
        joiningBonusLpa = 1.5f,
        brandScore = 10,
        learningSlope = 9,
        managerQuality = 8,
        location = "Bengaluru (Koramangala)",
        isRecommended = false
      ),
      OfferModel(
        company = "McKinsey & Company",
        role = "Knowledge Analyst - Tech Practice",
        baseLpa = 15.0f,
        variableLpa = 3.0f,
        esopsLpa = 0.0f,
        joiningBonusLpa = 3.0f,
        brandScore = 10,
        learningSlope = 9,
        managerQuality = 9,
        location = "Bengaluru / Hybrid",
        isRecommended = false
      )
    )
  }

  var selectedOfferIndex by remember { mutableStateOf(0) }
  val activeOffer = sampleOffers[selectedOfferIndex]

  AlertDialog(
    onDismissRequest = onDismissRequest,
    containerColor = ObsidianDark,
    shape = RoundedCornerShape(16.dp),
    modifier = Modifier
      .fillMaxWidth()
      .testTag("multi_offer_comparator_dialog"),
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
            Icon(Icons.AutoMirrored.Filled.CompareArrows, contentDescription = null, tint = TitanGold, modifier = Modifier.size(16.dp))
          }
          Spacer(modifier = Modifier.width(8.dp))
          Column {
            Text(
              text = "MULTI-OFFER COMPARATIVE MATRIX (SECTION 82)",
              style = MaterialTheme.typography.labelSmall,
              color = TitanGold,
              fontWeight = FontWeight.Bold,
              fontSize = 9.sp
            )
            Text(
              text = "Weighted Decision Engine & ESOPs",
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
        // Horizontal offer selector
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          sampleOffers.forEachIndexed { index, offer ->
            val isSelected = selectedOfferIndex == index
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(if (isSelected) TitanGold.copy(alpha = 0.2f) else SlateCard)
                .border(1.dp, if (isSelected) TitanGold else SlateBorder, RoundedCornerShape(8.dp))
                .clickable { selectedOfferIndex = index }
                .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
              Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(
                    text = offer.company,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isSelected) TitanGold else TextPrimaryDark,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                  )
                  if (offer.isRecommended) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.Default.Star, contentDescription = "Top Pick", tint = TitanEmerald, modifier = Modifier.size(12.dp))
                  }
                }
                Text(
                  text = "₹${String.format("%.1f", offer.totalFirstYear)}L Total",
                  style = MaterialTheme.typography.bodySmall,
                  color = TextMutedDark,
                  fontSize = 9.sp
                )
              }
            }
          }
        }

        // Active Offer Breakdown Card
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(10.dp),
          colors = CardDefaults.cardColors(containerColor = SlateElevated),
          border = androidx.compose.foundation.BorderStroke(1.dp, TitanCyan.copy(alpha = 0.4f))
        ) {
          Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "${activeOffer.company} • ${activeOffer.role}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimaryDark,
                fontSize = 12.sp
              )
              Text(
                text = "${activeOffer.location}",
                style = MaterialTheme.typography.labelSmall,
                color = TitanCyan,
                fontSize = 9.sp
              )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
              Text("Fixed Base Salary:", color = TextSecondaryDark, fontSize = 11.sp)
              Text("₹${String.format("%.1f", activeOffer.baseLpa)} LPA", color = TextPrimaryDark, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
              Text("Performance Variable:", color = TextSecondaryDark, fontSize = 11.sp)
              Text("₹${String.format("%.1f", activeOffer.variableLpa)} LPA", color = TitanGold, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
              Text("Upfront Joining Bonus:", color = TextSecondaryDark, fontSize = 11.sp)
              Text("₹${String.format("%.1f", activeOffer.joiningBonusLpa)} LPA", color = TitanCyan, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
              Text("ESOPs / Stock Value (4-Yr Vesting):", color = TextSecondaryDark, fontSize = 11.sp)
              Text("₹${String.format("%.1f", activeOffer.esopsLpa)} Lakhs (₹${String.format("%.1f", activeOffer.esopsLpa * 0.25f)}L/yr)", color = TitanIndigo, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
              Text("First-Year Realizable Cash & Equity:", color = TextPrimaryDark, fontWeight = FontWeight.Bold, fontSize = 11.sp)
              Text("₹${String.format("%.1f", activeOffer.totalFirstYear)} LPA", color = TitanEmerald, fontWeight = FontWeight.Black, fontSize = 12.sp)
            }
          }
        }

        // Decision Score Card
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(SlateCard)
            .border(1.dp, SlateBorder, RoundedCornerShape(10.dp))
            .padding(10.dp),
          verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          Text(
            text = "DECISION WEIGHTED SCORES",
            style = MaterialTheme.typography.labelSmall,
            color = TitanGold,
            fontWeight = FontWeight.Bold,
            fontSize = 9.sp
          )

          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Brand Tier & Exit Equity:", color = TextSecondaryDark, fontSize = 10.sp)
            Text("${activeOffer.brandScore}/10", color = TextPrimaryDark, fontWeight = FontWeight.Bold, fontSize = 10.sp)
          }

          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Learning Velocity & Slope:", color = TextSecondaryDark, fontSize = 10.sp)
            Text("${activeOffer.learningSlope}/10", color = TitanEmerald, fontWeight = FontWeight.Bold, fontSize = 10.sp)
          }

          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Manager & Mentorship Quality:", color = TextSecondaryDark, fontSize = 10.sp)
            Text("${activeOffer.managerQuality}/10", color = TitanCyan, fontWeight = FontWeight.Bold, fontSize = 10.sp)
          }

          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Overall Weighted Decision Score:", color = TitanGold, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            Text("${String.format("%.1f", activeOffer.decisionScore)} pts", color = TitanGold, fontWeight = FontWeight.Black, fontSize = 11.sp)
          }
        }
      }
    },
    confirmButton = {
      Button(
        onClick = onDismissRequest,
        colors = ButtonDefaults.buttonColors(containerColor = TitanGold, contentColor = ObsidianDark),
        shape = RoundedCornerShape(8.dp)
      ) {
        Text("Done", fontWeight = FontWeight.Bold, fontSize = 11.sp)
      }
    }
  )
}
