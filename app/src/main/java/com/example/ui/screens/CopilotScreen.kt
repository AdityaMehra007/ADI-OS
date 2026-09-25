package com.example.ui.screens

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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.example.ui.viewmodel.ChatMessage
import com.example.ui.viewmodel.TitanViewModel

@Composable
fun CopilotScreen(
  viewModel: TitanViewModel
) {
  val messages by viewModel.chatMessages.collectAsState()
  val isThinking by viewModel.isCopilotThinking.collectAsState()
  val userProfile by viewModel.userProfile.collectAsState()
  val brutalMode = userProfile?.brutalStrategyMode ?: false

  var inputMessage by remember { mutableStateOf("") }
  var showKeypad by remember { mutableStateOf(false) }
  val listState = rememberLazyListState()

  LaunchedEffect(messages.size) {
    if (messages.isNotEmpty()) {
      listState.animateScrollToItem(messages.size - 1)
    }
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(ObsidianDark)
      .padding(horizontal = 16.dp, vertical = 8.dp)
      .testTag("copilot_screen")
  ) {
    // Header
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
            .background(if (brutalMode) TitanCrimson.copy(alpha = 0.2f) else TitanCyan.copy(alpha = 0.2f)),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = if (brutalMode) Icons.Default.Warning else Icons.Default.AutoAwesome,
            contentDescription = null,
            tint = if (brutalMode) TitanCrimson else TitanCyan,
            modifier = Modifier.size(18.dp)
          )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column {
          Text(
            text = if (brutalMode) "OMEGA-TITAN (NO-BS SOVEREIGN)" else "OMEGA-TITAN SOVEREIGN",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = if (brutalMode) TitanCrimson else TitanCyan
          )
          Text(
            text = "25 Sovereign Modules • Truth-Anchor Ledger Active",
            style = MaterialTheme.typography.bodySmall,
            color = TextMutedDark,
            fontSize = 10.sp
          )
        }
      }

      Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (showKeypad) TitanGold.copy(alpha = 0.25f) else SlateElevated)
            .border(1.dp, if (showKeypad) TitanGold else SlateBorder, RoundedCornerShape(8.dp))
            .clickable { showKeypad = !showKeypad }
            .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
          Text(
            text = if (showKeypad) "⌨️ KEYPAD [ON]" else "⌨️ 25-KEYPAD",
            style = MaterialTheme.typography.labelSmall,
            color = if (showKeypad) TitanGold else TextSecondaryDark,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold
          )
        }

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (brutalMode) TitanCrimson.copy(alpha = 0.2f) else SlateElevated)
            .border(1.dp, if (brutalMode) TitanCrimson else SlateBorder, RoundedCornerShape(8.dp))
            .clickable { viewModel.toggleBrutalMode() }
            .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
          Text(
            text = if (brutalMode) "⚡ NO-BS ON" else "⚡ NO-BS OFF",
            style = MaterialTheme.typography.labelSmall,
            color = if (brutalMode) TitanCrimson else TextSecondaryDark,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Expandable 25-Keypad Grid
    if (showKeypad) {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(bottom = 8.dp),
        colors = CardDefaults.cardColors(containerColor = SlateCard),
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, TitanGold.copy(alpha = 0.4f))
      ) {
        Column(modifier = Modifier.padding(10.dp)) {
          Text(
            text = "⚡ OMEGA-TITAN 25-MODULE DIRECT EXECUTION KEYPAD",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = TitanGold,
            fontSize = 10.sp
          )
          Spacer(modifier = Modifier.height(6.dp))

          // Keypad Rows
          val keypadRows = listOf(
            listOf("1 JD Decompiler", "2 ATS Resume", "3 Pitch", "4 CXO Radar", "5 Outreach"),
            listOf("6 Referral", "7 Live HUD", "8 STAR-V", "9 CGPA Defense", "10 Case Solver"),
            listOf("11 SQL CTE", "12 Ask CXO", "13 CTC Benchmark", "14 ESOP Model", "15 Counter-Offer"),
            listOf("16 Bidding War", "17 30-60-90 Plan", "18 Dark Store", "19 Punchlist", "20 Amazon WBR"),
            listOf("21 Sev-1 Triage", "22 EXIM Trade", "23 Kotlin Code", "24 Brag Sheet", "25 Promotion"),
            listOf("WAR-ROOM", "1 Zepto", "2 Blinkit", "9 Hostile Question", "18 Morning Check")
          )

          keypadRows.forEach { row ->
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp)
                .horizontalScroll(rememberScrollState()),
              horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              row.forEach { keyLabel ->
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(SlateElevated)
                    .border(1.dp, SlateBorder, RoundedCornerShape(6.dp))
                    .clickable {
                      viewModel.sendCopilotMessage(keyLabel)
                    }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                  Text(
                    text = keyLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextPrimaryDark,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.SemiBold
                  )
                }
              }
            }
          }
        }
      }
    }

    // Quick Command Chips
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .horizontalScroll(rememberScrollState()),
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      QuickPromptChip("1 🎯 JD Decompiler") {
        viewModel.sendCopilotMessage("1 Decompile Operations Lead JD and map hiring manager anxieties against my verified experience")
      }
      QuickPromptChip("3 🎙️ Executive Pitch") {
        viewModel.sendCopilotMessage("3 Generate my 30-second executive pitch and recruiter screen hook")
      }
      QuickPromptChip("7 📊 Live HUD") {
        viewModel.sendCopilotMessage("7 What are my exact dark store cycle time and CM2 numbers?")
      }
      QuickPromptChip("9 🛡️ CGPA Defense") {
        viewModel.sendCopilotMessage("9 Why should we hire a fresher with a 6.33 CGPA from DSU over a 9.5 IIT grad?")
      }
      QuickPromptChip("11 💻 Live SQL CTE") {
        viewModel.sendCopilotMessage("11 Write an automated morning SQL CTE query for dark store cycle time triage")
      }
      QuickPromptChip("13 💰 Bengaluru CTC") {
        viewModel.sendCopilotMessage("13 What are the 25th-90th percentile salary benchmarks for Operations Leads in Bengaluru?")
      }
      QuickPromptChip("15 🤝 Counter-Offer") {
        viewModel.sendCopilotMessage("15 Current offer is ₹13L, draft a hardball counter-offer for ₹16.5L")
      }
      QuickPromptChip("17 📅 30-60-90 Plan") {
        viewModel.sendCopilotMessage("17 Generate my 30-60-90 day boardroom operational action plan")
      }
      QuickPromptChip("18 🏬 Dark Store Health") {
        viewModel.sendCopilotMessage("18 Run morning dark store health audit and SKU velocity check")
      }
      QuickPromptChip("20 📑 Amazon WBR Memo") {
        viewModel.sendCopilotMessage("20 Generate an Amazon-style 1-page Weekly Business Review memo for VP Operations")
      }
      QuickPromptChip("⚔️ WAR-ROOM") {
        viewModel.sendCopilotMessage("WAR-ROOM Simulate synthetic C-Suite meeting on defending dark store CM2 margins")
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Messages List
    LazyColumn(
      state = listState,
      modifier = Modifier
        .weight(1f)
        .fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      items(messages, key = { it.id }) { msg ->
        MessageBubble(msg = msg)
      }

      if (isThinking) {
        item {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .padding(8.dp),
            contentAlignment = Alignment.CenterStart
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              CircularProgressIndicator(color = if (brutalMode) TitanCrimson else TitanCyan, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(8.dp))
              Text("Analyzing profile graph, market indices, and verified evidence...", style = MaterialTheme.typography.bodySmall, color = TextMutedDark, fontSize = 11.sp)
            }
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Chat Input Bar
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically
    ) {
      OutlinedTextField(
        value = inputMessage,
        onValueChange = { inputMessage = it },
        modifier = Modifier
          .weight(1f)
          .testTag("copilot_input_field"),
        placeholder = {
          Text("Ask Adi Copilot anything...", color = TextMutedDark, fontSize = 12.sp)
        },
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = if (brutalMode) TitanCrimson else TitanCyan,
          unfocusedBorderColor = SlateBorder,
          focusedContainerColor = SlateCard,
          unfocusedContainerColor = SlateCard,
          focusedTextColor = TextPrimaryDark,
          unfocusedTextColor = TextPrimaryDark
        ),
        shape = RoundedCornerShape(10.dp),
        maxLines = 3
      )

      Spacer(modifier = Modifier.width(8.dp))

      Box(
        modifier = Modifier
          .size(48.dp)
          .clip(RoundedCornerShape(10.dp))
          .background(if (brutalMode) TitanCrimson else TitanCyan)
          .clickable {
            if (inputMessage.isNotBlank()) {
              val txt = inputMessage
              inputMessage = ""
              viewModel.sendCopilotMessage(txt)
            }
          }
          .testTag("send_copilot_button"),
        contentAlignment = Alignment.Center
      ) {
        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = ObsidianDark, modifier = Modifier.size(20.dp))
      }
    }

    Spacer(modifier = Modifier.height(8.dp))
  }
}

@Composable
fun MessageBubble(msg: ChatMessage) {
  val isUser = msg.sender == "ADI"

  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
  ) {
    Card(
      modifier = Modifier.fillMaxWidth(0.92f),
      shape = RoundedCornerShape(12.dp),
      colors = CardDefaults.cardColors(
        containerColor = if (isUser) SlateElevated else SlateCard
      ),
      border = androidx.compose.foundation.BorderStroke(
        1.dp,
        if (msg.isBrutal) TitanCrimson.copy(alpha = 0.5f) else if (isUser) TitanCyan.copy(alpha = 0.4f) else SlateBorder
      )
    ) {
      Column(modifier = Modifier.padding(12.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = if (isUser) "ADI" else if (msg.isBrutal) "COPILOT (BRUTAL STRATEGY)" else "ADI COPILOT",
            style = MaterialTheme.typography.labelSmall,
            color = if (msg.isBrutal) TitanCrimson else if (isUser) TitanGold else TitanCyan,
            fontWeight = FontWeight.Bold,
            fontSize = 9.sp
          )
          Text(
            text = msg.timestamp,
            style = MaterialTheme.typography.labelSmall,
            color = TextMutedDark,
            fontSize = 8.sp
          )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
          text = msg.text,
          style = MaterialTheme.typography.bodySmall,
          color = TextPrimaryDark,
          lineHeight = 17.sp,
          fontSize = 11.sp
        )
      }
    }
  }
}

@Composable
fun QuickPromptChip(text: String, onClick: () -> Unit) {
  Box(
    modifier = Modifier
      .clip(RoundedCornerShape(6.dp))
      .background(SlateElevated)
      .border(1.dp, SlateBorder, RoundedCornerShape(6.dp))
      .clickable { onClick() }
      .padding(horizontal = 8.dp, vertical = 4.dp)
  ) {
    Text(text, style = MaterialTheme.typography.labelSmall, color = TextSecondaryDark, fontSize = 10.sp)
  }
}
