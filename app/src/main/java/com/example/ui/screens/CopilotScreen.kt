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
            text = if (brutalMode) "ADI COPILOT (NO-BS MODE)" else "ADI COPILOT",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = if (brutalMode) TitanCrimson else TitanCyan
          )
          Text(
            text = "Personal Executive Strategist • Grounded in Profile Graph",
            style = MaterialTheme.typography.bodySmall,
            color = TextMutedDark,
            fontSize = 10.sp
          )
        }
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

    Spacer(modifier = Modifier.height(10.dp))

    // Quick Command Chips
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .horizontalScroll(rememberScrollState()),
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      QuickPromptChip("🎯 Prep Microsoft Round") {
        viewModel.sendCopilotMessage("Prepare me for Microsoft Strategy final round interview. What Azure AI & business cases will they ask?")
      }
      QuickPromptChip("💼 Razorpay ₹18.5L CTC") {
        viewModel.sendCopilotMessage("Evaluate my Razorpay ₹18.5L offer. Calculate monthly in-hand net salary, EPF, tax and counter-offer strategy.")
      }
      QuickPromptChip("⚡ Brutal ATS Audit") {
        viewModel.sendCopilotMessage("Brutal audit of my resume and qualifications. What are my real weaknesses against IIT/IIM grads?")
      }
      QuickPromptChip("🚀 Master Plan Titan") {
        viewModel.sendCopilotMessage("Take care of my career. Execute Project Titan master plan for Bengaluru S-Tier Strategy roles.")
      }
      QuickPromptChip("📊 Diagnose Funnel") {
        viewModel.sendCopilotMessage("Diagnose my application funnel: 18 Applied, 5 Screened, 3 Offers. Where is my biggest bottleneck?")
      }
      QuickPromptChip("💡 B2B AI SaaS Idea") {
        viewModel.sendCopilotMessage("Suggest high-margin B2B AI Operations productized service ideas for Bengaluru logistics SMEs.")
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
