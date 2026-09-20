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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
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

enum class OutreachType(val title: String, val subtitle: String) {
  PEER_REFERRAL("Peer Referral Request", "High-response casual ask to engineering alumni / peers"),
  HIRING_MANAGER_DIRECT("Hiring Manager Pitch", "Executive direct value-add pitch with proof of work"),
  WARM_COFFEE_CHAT("15-Min Insight Chat", "Low-pressure exploratory question on team architecture"),
  POST_INTERVIEW_FOLLOWUP("Post-Interview Value Add", "Thoughtful thank you with supplementary insights")
}

data class SynthesizedOutreachMessage(
  val subjectLine: String,
  val messageBody: String,
  val callToAction: String,
  val cadenceSteps: List<String>
)

object OutreachMessageSynthesizer {
  fun generate(
    type: OutreachType,
    contactName: String,
    company: String,
    role: String,
    anchor: String
  ): SynthesizedOutreachMessage {
    val name = contactName.ifBlank { "there" }
    val comp = company.ifBlank { "your team" }
    val targetRole = role.ifBlank { "Senior Engineer" }
    val sharedAnchor = anchor.ifBlank { "your team's recent scaling milestones" }

    return when (type) {
      OutreachType.PEER_REFERRAL -> SynthesizedOutreachMessage(
        subjectLine = "Quick question on $comp's $targetRole team ($sharedAnchor)",
        messageBody = """
Hi $name,

Hope you're having a great week! I've been following $comp's recent trajectory in engineering—especially regarding $sharedAnchor.

I'm currently exploring the $targetRole position at $comp. Given my background building high-scale distributed systems and optimizing latency, I’d love to learn more about how your team approaches architecture challenges.

If you have 10 mins this week for a quick virtual coffee, I’d be grateful for your perspective—and if you feel it's a solid mutual fit, a referral would mean the world.

Best,
Adi
        """.trimIndent(),
        callToAction = "Low-friction ask: 10-minute informal perspective chat",
        cadenceSteps = listOf(
          "Day 0: Send LinkedIn personalized connect note or InMail.",
          "Day 4: Gentle bump on thread with one relevant project link.",
          "Day 8: Final courteous check-in before pivoting to next contact."
        )
      )
      OutreachType.HIRING_MANAGER_DIRECT -> SynthesizedOutreachMessage(
        subjectLine = "Ideas regarding $comp's $sharedAnchor • $targetRole candidate",
        messageBody = """
Hi $name,

I noticed you're leading the group at $comp. Your recent focus on $sharedAnchor caught my attention as I recently resolved a very similar throughput bottleneck at scale, reducing p99 latency by 42%.

I saw the opening for $targetRole and wanted to reach out directly rather than getting lost in the ATS queue. I’ve put together a brief architecture breakdown of how I’d tackle $comp's scaling priorities.

Would you be open to a 15-minute conversation to explore whether my background could accelerate your team's roadmap this quarter?

Best regards,
Adi
        """.trimIndent(),
        callToAction = "Direct executive value proposition with relevant proof of work",
        cadenceSteps = listOf(
          "Day 0: Send direct email / InMail during Tuesday or Thursday morning (9-10 AM).",
          "Day 5: Forward original note with a 2-bullet technical insight.",
          "Day 10: Soft close leaving door open for future quarters."
        )
      )
      OutreachType.WARM_COFFEE_CHAT -> SynthesizedOutreachMessage(
        subjectLine = "Curious about your journey at $comp ($sharedAnchor)",
        messageBody = """
Hi $name,

Came across your profile while researching $comp's engineering culture and was really impressed by your journey, especially with $sharedAnchor.

As someone deepening my focus in $targetRole, I’m keen to hear what separates top performers on your team from the rest.

Do you have 10-15 minutes for a quick virtual chat sometime next week? No pressure at all—either way, congrats on the recent milestones!

Best,
Adi
        """.trimIndent(),
        callToAction = "Pure curiosity-driven relationship building (zero ask for referral initially)",
        cadenceSteps = listOf(
          "Day 0: Send short invite focused 100% on their expertise.",
          "Day 6: Engage with one of their recent LinkedIn posts or articles.",
          "Day 12: Light follow-up with a shared industry piece."
        )
      )
      OutreachType.POST_INTERVIEW_FOLLOWUP -> SynthesizedOutreachMessage(
        subjectLine = "Thank you - $comp $targetRole conversation ($name & Adi)",
        messageBody = """
Hi $name,

Thank you so much for your time and the engaging discussion earlier today regarding the $targetRole role. I particularly enjoyed our deep dive into $sharedAnchor and how $comp plans to address it.

Reflecting on our conversation about edge case reliability, I thought of an additional caching strategy that could shave off another 15% of lookup overhead without compromising consistency.

I'm even more energized about the prospect of contributing to the team. Looking forward to next steps!

Warm regards,
Adi
        """.trimIndent(),
        callToAction = "Solidifies interview performance by providing free supplementary strategic value",
        cadenceSteps = listOf(
          "Day 0 (within 6 hours of interview): Send this tailored thank-you note.",
          "Day 5: Touch base with recruiting coordinator for timeline updates if no response."
        )
      )
    }
  }
}

@Composable
fun ReferralOutreachGeneratorDialog(
  onDismissRequest: () -> Unit
) {
  var selectedType by remember { mutableStateOf(OutreachType.PEER_REFERRAL) }
  var contactName by remember { mutableStateOf("Vikram Malhotra") }
  var company by remember { mutableStateOf("Zepto") }
  var role by remember { mutableStateOf("Staff Android Engineer") }
  var anchor by remember { mutableStateOf("Quick-Commerce dark store logistics & Compose architecture") }

  val clipboardManager = LocalClipboardManager.current
  var copyToastVisible by remember { mutableStateOf(false) }

  val currentMessage = remember(selectedType, contactName, company, role, anchor) {
    OutreachMessageSynthesizer.generate(selectedType, contactName, company, role, anchor)
  }

  Dialog(
    onDismissRequest = onDismissRequest,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = Modifier
        .fillMaxWidth(0.95f)
        .fillMaxHeight(0.92f)
        .clip(RoundedCornerShape(20.dp))
        .border(1.dp, SlateBorder, RoundedCornerShape(20.dp))
        .testTag("referral_outreach_generator_dialog"),
      color = ObsidianDark
    ) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(20.dp)
      ) {
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
                  .background(TitanEmerald.copy(alpha = 0.15f), RoundedCornerShape(6.dp)),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.Email,
                  contentDescription = null,
                  tint = TitanEmerald,
                  modifier = Modifier.size(16.dp)
                )
              }
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "EXECUTIVE NETWORKING & REFERRAL OS",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TitanEmerald,
                letterSpacing = 1.1.sp
              )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = "Personalized Referral & Outreach Synthesizer",
              fontSize = 20.sp,
              fontWeight = FontWeight.Bold,
              color = TextPrimaryDark
            )
            Text(
              text = "High-conversion messages & cadence sequences tailored for target company insiders",
              fontSize = 11.sp,
              color = TextMutedDark
            )
          }

          IconButton(
            onClick = onDismissRequest,
            modifier = Modifier
              .size(36.dp)
              .clip(CircleShape)
              .background(SlateElevated)
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Close",
              tint = TextSecondaryDark,
              modifier = Modifier.size(18.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Scrollable Body
        Column(
          modifier = Modifier
            .weight(1f)
            .verticalScroll(rememberScrollState())
        ) {
          // Outreach Type Pills
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            OutreachType.values().forEach { type ->
              val isSelected = selectedType == type
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(8.dp))
                  .background(if (isSelected) TitanEmerald.copy(alpha = 0.2f) else SlateElevated)
                  .border(1.dp, if (isSelected) TitanEmerald else SlateBorder, RoundedCornerShape(8.dp))
                  .clickable { selectedType = type }
                  .padding(horizontal = 10.dp, vertical = 6.dp)
              ) {
                Text(
                  text = type.title,
                  fontSize = 11.sp,
                  color = if (isSelected) TitanEmerald else TextSecondaryDark,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Target Inputs Card
          Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = SlateCard),
            border = BorderStroke(1.dp, SlateBorder),
            shape = RoundedCornerShape(12.dp)
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                OutlinedTextField(
                  value = contactName,
                  onValueChange = { contactName = it },
                  label = { Text("Contact Name", fontSize = 11.sp) },
                  modifier = Modifier.weight(1f),
                  colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TitanEmerald,
                    unfocusedBorderColor = SlateBorder,
                    focusedTextColor = TextPrimaryDark,
                    unfocusedTextColor = TextSecondaryDark
                  ),
                  shape = RoundedCornerShape(8.dp)
                )

                OutlinedTextField(
                  value = company,
                  onValueChange = { company = it },
                  label = { Text("Target Company", fontSize = 11.sp) },
                  modifier = Modifier.weight(1f),
                  colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TitanEmerald,
                    unfocusedBorderColor = SlateBorder,
                    focusedTextColor = TextPrimaryDark,
                    unfocusedTextColor = TextSecondaryDark
                  ),
                  shape = RoundedCornerShape(8.dp)
                )
              }

              Spacer(modifier = Modifier.height(8.dp))

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                OutlinedTextField(
                  value = role,
                  onValueChange = { role = it },
                  label = { Text("Target Role", fontSize = 11.sp) },
                  modifier = Modifier.weight(1f),
                  colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TitanEmerald,
                    unfocusedBorderColor = SlateBorder,
                    focusedTextColor = TextPrimaryDark,
                    unfocusedTextColor = TextSecondaryDark
                  ),
                  shape = RoundedCornerShape(8.dp)
                )

                OutlinedTextField(
                  value = anchor,
                  onValueChange = { anchor = it },
                  label = { Text("Shared Anchor / Angle", fontSize = 11.sp) },
                  modifier = Modifier.weight(1f),
                  colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TitanEmerald,
                    unfocusedBorderColor = SlateBorder,
                    focusedTextColor = TextPrimaryDark,
                    unfocusedTextColor = TextSecondaryDark
                  ),
                  shape = RoundedCornerShape(8.dp)
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Generated Message Card
          Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = SlateCard),
            border = BorderStroke(1.dp, SlateBorder),
            shape = RoundedCornerShape(14.dp)
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "HIGH-CONVERSION OUTREACH MESSAGE",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = TitanEmerald,
                  letterSpacing = 1.sp
                )

                Button(
                  onClick = {
                    val fullText = "Subject: ${currentMessage.subjectLine}\n\n${currentMessage.messageBody}"
                    clipboardManager.setText(AnnotatedString(fullText))
                    copyToastVisible = true
                  },
                  colors = ButtonDefaults.buttonColors(containerColor = TitanEmerald),
                  shape = RoundedCornerShape(8.dp)
                ) {
                  Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = null,
                    tint = ObsidianDark,
                    modifier = Modifier.size(14.dp)
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = "Copy Message",
                    color = ObsidianDark,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                  )
                }
              }

              Spacer(modifier = Modifier.height(10.dp))

              // Subject Line
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(8.dp))
                  .background(SlateElevated)
                  .border(1.dp, SlateBorder, RoundedCornerShape(8.dp))
                  .padding(10.dp)
              ) {
                Column {
                  Text(text = "SUBJECT LINE:", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TitanCyan)
                  Spacer(modifier = Modifier.height(2.dp))
                  Text(
                    text = currentMessage.subjectLine,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimaryDark,
                    fontFamily = FontFamily.Monospace
                  )
                }
              }

              Spacer(modifier = Modifier.height(10.dp))

              // Body
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(8.dp))
                  .background(ObsidianDark)
                  .border(1.dp, SlateBorder, RoundedCornerShape(8.dp))
                  .padding(12.dp)
              ) {
                Text(
                  text = currentMessage.messageBody,
                  fontSize = 12.sp,
                  color = TextPrimaryDark,
                  lineHeight = 18.sp,
                  fontFamily = FontFamily.Monospace
                )
              }

              Spacer(modifier = Modifier.height(10.dp))

              // Call To Action Highlight
              Surface(
                color = TitanEmerald.copy(alpha = 0.12f),
                shape = RoundedCornerShape(6.dp)
              ) {
                Text(
                  text = "CTA Strategy: ${currentMessage.callToAction}",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = TitanEmerald,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Follow-up Cadence Sequence
          Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = SlateCard),
            border = BorderStroke(1.dp, SlateBorder),
            shape = RoundedCornerShape(12.dp)
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.DateRange,
                  contentDescription = null,
                  tint = TitanGold,
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "RECOMMENDED FOLLOW-UP CADENCE SEQUENCE",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = TitanGold,
                  letterSpacing = 0.8.sp
                )
              }
              Spacer(modifier = Modifier.height(8.dp))

              currentMessage.cadenceSteps.forEachIndexed { i, step ->
                Row(
                  modifier = Modifier.padding(vertical = 3.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Box(
                    modifier = Modifier
                      .size(20.dp)
                      .background(SlateElevated, CircleShape)
                      .border(1.dp, TitanGold.copy(alpha = 0.4f), CircleShape),
                    contentAlignment = Alignment.Center
                  ) {
                    Text(text = "${i + 1}", fontSize = 10.sp, color = TitanGold, fontWeight = FontWeight.Bold)
                  }
                  Spacer(modifier = Modifier.width(8.dp))
                  Text(text = step, fontSize = 11.sp, color = TextSecondaryDark)
                }
              }
            }
          }
        }

        // Copy feedback
        AnimatedVisibility(visible = copyToastVisible) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .padding(top = 8.dp)
              .clip(RoundedCornerShape(8.dp))
              .background(TitanEmerald)
              .padding(vertical = 8.dp, horizontal = 12.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "Message copied to clipboard!",
              color = ObsidianDark,
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }
    }
  }
}
