package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ExecutiveActionDirective
import com.example.data.model.ExecutiveMarketPosture
import com.example.data.model.MorningAutomationTaskItem
import com.example.data.model.MorningExecutiveSummaryReport
import com.example.data.model.SavedCompanyNewsHighlight
import com.example.ui.theme.ObsidianDark
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateCard
import com.example.ui.theme.SlateDarker
import com.example.ui.theme.SlateElevated
import com.example.ui.theme.TextMutedDark
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import com.example.ui.theme.TitanAmber
import com.example.ui.theme.TitanCyan
import com.example.ui.theme.TitanCyanGlow
import com.example.ui.theme.TitanCyanMuted
import com.example.ui.theme.TitanEmerald
import com.example.ui.theme.TitanGold
import com.example.ui.theme.TitanGoldLight
import com.example.ui.theme.TitanIndigo
import com.example.ui.theme.TitanViolet
import com.example.ui.viewmodel.TitanViewModel

@Composable
fun MorningExecutiveSummaryComponent(
  viewModel: TitanViewModel,
  modifier: Modifier = Modifier
) {
  val summaryReport by viewModel.morningExecutiveSummary.collectAsState()
  val isGenerating by viewModel.isGeneratingMorningSummary.collectAsState()
  val isAudioPlaying by viewModel.isExecutiveAudioPlaying.collectAsState()
  val context = LocalContext.current

  var selectedCompanyId by remember { mutableStateOf<String?>(null) }
  var isAudioScriptExpanded by remember { mutableStateOf(false) }

  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("morning_executive_summary_card"),
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(containerColor = SlateDarker),
    border = androidx.compose.foundation.BorderStroke(1.dp, Brush.horizontalGradient(listOf(TitanCyan.copy(alpha = 0.6f), TitanGold.copy(alpha = 0.4f))))
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(20.dp)
    ) {
      // 1. Top Header & Meta Badges
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
              .background(Brush.linearGradient(listOf(TitanCyan, TitanGold))),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.AutoAwesome,
              contentDescription = "Executive AI",
              tint = ObsidianDark,
              modifier = Modifier.size(20.dp)
            )
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "EXECUTIVE SUMMARY",
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = TitanCyan,
                letterSpacing = 1.2.sp
              )
              Spacer(modifier = Modifier.width(6.dp))
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(4.dp))
                  .background(TitanCyanGlow)
                  .padding(horizontal = 5.dp, vertical = 2.dp)
              ) {
                Text(
                  text = if (summaryReport?.isLiveGeminiGenerated == true) "GEMINI 3.5 FLASH • GROUNDED" else "CALIBRATED INTELLIGENCE",
                  fontSize = 8.sp,
                  fontWeight = FontWeight.Bold,
                  color = TitanCyan
                )
              }
            }
            Text(
              text = summaryReport?.dateFormatted ?: "Daily Intelligence Briefing",
              fontSize = 12.sp,
              color = TextMutedDark
            )
          }
        }

        // Action Buttons: Refresh & Audio
        Row(verticalAlignment = Alignment.CenterVertically) {
          IconButton(
            onClick = { viewModel.generateMorningExecutiveSummary(forceRefresh = true) },
            modifier = Modifier
              .size(36.dp)
              .testTag("refresh_morning_summary_button")
          ) {
            if (isGenerating) {
              CircularProgressIndicator(modifier = Modifier.size(18.dp), color = TitanCyan, strokeWidth = 2.dp)
            } else {
              Icon(Icons.Default.Refresh, contentDescription = "Refresh Briefing", tint = TextSecondaryDark, modifier = Modifier.size(18.dp))
            }
          }

          Spacer(modifier = Modifier.width(4.dp))

          // Voice Audio Play/Stop Button with pulse animation
          val infiniteTransition = rememberInfiniteTransition(label = "audio_pulse")
          val pulseScale by infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = if (isAudioPlaying) 1.15f else 1f,
            animationSpec = infiniteRepeatable(
              animation = tween(600),
              repeatMode = RepeatMode.Reverse
            ),
            label = "pulse_scale"
          )

          Box(
            modifier = Modifier
              .scale(pulseScale)
              .clip(RoundedCornerShape(12.dp))
              .background(if (isAudioPlaying) TitanEmerald.copy(alpha = 0.2f) else SlateCard)
              .border(1.dp, if (isAudioPlaying) TitanEmerald else SlateBorder, RoundedCornerShape(12.dp))
              .clickable { viewModel.toggleExecutiveAudioPlayback(context) }
              .padding(horizontal = 10.dp, vertical = 6.dp)
              .testTag("play_audio_briefing_button"),
            contentAlignment = Alignment.Center
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = if (isAudioPlaying) Icons.Default.Stop else Icons.AutoMirrored.Filled.VolumeUp,
                contentDescription = if (isAudioPlaying) "Stop Audio" else "Play Audio Briefing",
                tint = if (isAudioPlaying) TitanEmerald else TitanCyan,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(5.dp))
              Text(
                text = if (isAudioPlaying) "PLAYING" else "LISTEN (60s)",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = if (isAudioPlaying) TitanEmerald else TitanCyan
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // 2. Greeting & Market Posture Banner
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = summaryReport?.greeting ?: "Good morning, Aditya.",
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold,
            color = TextPrimaryDark
          )
          Text(
            text = "Bengaluru Tech Corridor • Pre-Market Synthesis",
            fontSize = 12.sp,
            color = TextSecondaryDark
          )
        }

        val posture = summaryReport?.marketPosture ?: ExecutiveMarketPosture.AGGRESSIVE_EXECUTION
        val (postureColor, postureBg) = when (posture) {
          ExecutiveMarketPosture.AGGRESSIVE_EXECUTION -> TitanEmerald to TitanEmerald.copy(alpha = 0.15f)
          ExecutiveMarketPosture.TACTICAL_INTERVIEW_PREP -> TitanCyan to TitanCyan.copy(alpha = 0.15f)
          ExecutiveMarketPosture.HIGH_CONVICTION_OUTREACH -> TitanGold to TitanGold.copy(alpha = 0.15f)
          ExecutiveMarketPosture.PIPELINE_EXPANSION -> TitanIndigo to TitanIndigo.copy(alpha = 0.15f)
        }

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(postureBg)
            .border(1.dp, postureColor.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.TrendingUp,
              contentDescription = null,
              tint = postureColor,
              modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = posture.label,
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              fontFamily = FontFamily.Monospace,
              color = postureColor
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // 3. Macro Executive Overview Text Card
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(12.dp))
          .background(SlateCard)
          .border(1.dp, SlateBorder, RoundedCornerShape(12.dp))
          .padding(14.dp)
      ) {
        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Psychology, contentDescription = null, tint = TitanCyan, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "MACRO EXECUTIVE DIRECTIVE",
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              fontFamily = FontFamily.Monospace,
              color = TitanCyan
            )
          }
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = summaryReport?.macroExecutiveOverview
              ?: "Hiring velocity across Bengaluru's high-growth tech corridors is accelerating this morning. Zepto and Swiggy are aggressively scaling logistics operations, presenting prime openings for Strategy and Analytics specialists.",
            fontSize = 13.sp,
            color = TextPrimaryDark,
            lineHeight = 18.sp
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // 4. Top 3 Executive Action Directives
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "TODAY'S TOP 3 DIRECTIVES",
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold,
          fontFamily = FontFamily.Monospace,
          color = TitanGold,
          letterSpacing = 1.sp
        )
        val completedCount = summaryReport?.topExecutiveDirectives?.count { it.isCompleted } ?: 0
        Text(
          text = "$completedCount/3 Completed",
          fontSize = 11.sp,
          fontWeight = FontWeight.SemiBold,
          color = if (completedCount == 3) TitanEmerald else TextSecondaryDark
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      val directives = summaryReport?.topExecutiveDirectives ?: emptyList()
      directives.forEach { directive ->
        ExecutiveDirectiveItemRow(
          directive = directive,
          onToggle = { viewModel.toggleExecutiveDirective(directive.id) }
        )
        Spacer(modifier = Modifier.height(6.dp))
      }

      Spacer(modifier = Modifier.height(14.dp))

      // 5. Saved Companies Intelligence & News Radar
      Text(
        text = "SAVED COMPANIES INTELLIGENCE RADAR",
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = FontFamily.Monospace,
        color = TitanCyan,
        letterSpacing = 1.sp
      )
      Spacer(modifier = Modifier.height(8.dp))

      val highlights = summaryReport?.companyNewsHighlights ?: emptyList()
      if (highlights.isNotEmpty()) {
        val scrollState = rememberScrollState()
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState)
        ) {
          highlights.forEach { highlight ->
            val isSelected = (selectedCompanyId == null && highlight == highlights.first()) || selectedCompanyId == highlight.companyId
            CompanyNewsChip(
              highlight = highlight,
              isSelected = isSelected,
              onClick = { selectedCompanyId = highlight.companyId }
            )
            Spacer(modifier = Modifier.width(8.dp))
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Selected Company Intel Detail Card
        val activeHighlight = highlights.find { it.companyId == selectedCompanyId } ?: highlights.firstOrNull()
        if (activeHighlight != null) {
          CompanyIntelDetailCard(highlight = activeHighlight)
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // 6. Autonomous Morning Automation Agenda
      val agenda = summaryReport?.automationAgenda
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.SmartToy, contentDescription = null, tint = TitanEmerald, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "AUTONOMOUS AGENT PIPELINE",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              fontFamily = FontFamily.Monospace,
              color = TitanEmerald,
              letterSpacing = 1.sp
            )
          }
          Text(
            text = agenda?.executionReadinessSummary ?: "Pending pipelines ready to execute",
            fontSize = 11.sp,
            color = TextSecondaryDark
          )
        }

        val hasPending = agenda?.pendingTasks?.any { it.status != "COMPLETED" } == true
        if (hasPending) {
          Button(
            onClick = { viewModel.executeAllPendingAutomationTasks() },
            colors = ButtonDefaults.buttonColors(containerColor = TitanEmerald),
            shape = RoundedCornerShape(8.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
            modifier = Modifier.testTag("execute_all_tasks_button")
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.PlayArrow, contentDescription = null, tint = ObsidianDark, modifier = Modifier.size(14.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "EXECUTE ALL",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = ObsidianDark,
                fontFamily = FontFamily.Monospace
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      val tasks = agenda?.pendingTasks ?: emptyList()
      tasks.forEach { taskItem ->
        MorningAutomationTaskCard(
          task = taskItem,
          onExecute = { viewModel.executePendingAutomationTask(taskItem.taskId) }
        )
        Spacer(modifier = Modifier.height(6.dp))
      }

      Spacer(modifier = Modifier.height(14.dp))

      // 7. Audio Script & Tactical Quote Collapsible Drawer
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(12.dp))
          .background(SlateElevated)
          .border(1.dp, SlateBorder, RoundedCornerShape(12.dp))
          .clickable { isAudioScriptExpanded = !isAudioScriptExpanded }
          .padding(12.dp)
      ) {
        Column {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = null, tint = TitanCyan, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "VIEW 60s SPOKEN AUDIO SCRIPT",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = TextPrimaryDark
              )
            }
            Icon(
              imageVector = if (isAudioScriptExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
              contentDescription = null,
              tint = TextSecondaryDark,
              modifier = Modifier.size(18.dp)
            )
          }

          AnimatedVisibility(visible = isAudioScriptExpanded) {
            Column(modifier = Modifier.padding(top = 10.dp)) {
              Text(
                text = summaryReport?.audioBriefingScript ?: "Voice script not loaded.",
                fontSize = 12.sp,
                color = TextSecondaryDark,
                lineHeight = 17.sp,
                fontStyle = FontStyle.Italic
              )
              Spacer(modifier = Modifier.height(8.dp))
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
              ) {
                OutlinedButton(
                  onClick = {
                    val script = summaryReport?.audioBriefingScript ?: return@OutlinedButton
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboard.setPrimaryClip(ClipData.newPlainText("Executive Briefing Script", script))
                    Toast.makeText(context, "Briefing script copied to clipboard", Toast.LENGTH_SHORT).show()
                  },
                  shape = RoundedCornerShape(8.dp),
                  contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                ) {
                  Icon(Icons.Default.ContentCopy, contentDescription = null, tint = TitanCyan, modifier = Modifier.size(12.dp))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text("Copy Script", fontSize = 10.sp, color = TitanCyan)
                }
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Tactical Quote Footer
      val quote = summaryReport?.tacticalClosingQuote
      if (!quote.isNullOrBlank()) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 6.dp, vertical = 4.dp),
          verticalAlignment = Alignment.Top
        ) {
          Icon(Icons.Default.FormatQuote, contentDescription = null, tint = TitanGold.copy(alpha = 0.7f), modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = quote,
            fontSize = 11.sp,
            fontStyle = FontStyle.Italic,
            color = TextSecondaryDark,
            lineHeight = 15.sp
          )
        }
      }
    }
  }
}

@Composable
fun ExecutiveDirectiveItemRow(
  directive: ExecutiveActionDirective,
  onToggle: () -> Unit
) {
  val isDone = directive.isCompleted
  val bgColor by animateColorAsState(if (isDone) SlateCard.copy(alpha = 0.6f) else SlateCard, label = "dir_bg")
  val borderColor = if (isDone) TitanEmerald.copy(alpha = 0.4f) else SlateBorder

  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(10.dp))
      .background(bgColor)
      .border(1.dp, borderColor, RoundedCornerShape(10.dp))
      .clickable { onToggle() }
      .padding(10.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Icon(
      imageVector = if (isDone) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
      contentDescription = if (isDone) "Mark Incomplete" else "Mark Complete",
      tint = if (isDone) TitanEmerald else TextMutedDark,
      modifier = Modifier.size(20.dp)
    )
    Spacer(modifier = Modifier.width(10.dp))
    Column(modifier = Modifier.weight(1f)) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
          text = "#${directive.priorityNumber}",
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          fontFamily = FontFamily.Monospace,
          color = if (isDone) TextMutedDark else TitanGold
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = directive.title,
          fontSize = 12.sp,
          fontWeight = FontWeight.SemiBold,
          color = if (isDone) TextMutedDark else TextPrimaryDark,
          textDecoration = if (isDone) TextDecoration.LineThrough else TextDecoration.None
        )
      }
      if (!isDone) {
        Text(
          text = directive.rationale,
          fontSize = 10.sp,
          color = TextSecondaryDark,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
      }
    }
  }
}

@Composable
fun CompanyNewsChip(
  highlight: SavedCompanyNewsHighlight,
  isSelected: Boolean,
  onClick: () -> Unit
) {
  val bg = if (isSelected) TitanCyan.copy(alpha = 0.15f) else SlateCard
  val border = if (isSelected) TitanCyan else SlateBorder

  Row(
    modifier = Modifier
      .clip(RoundedCornerShape(8.dp))
      .background(bg)
      .border(1.dp, border, RoundedCornerShape(8.dp))
      .clickable { onClick() }
      .padding(horizontal = 10.dp, vertical = 6.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(text = highlight.logoEmoji, fontSize = 14.sp)
    Spacer(modifier = Modifier.width(6.dp))
    Text(
      text = highlight.companyName,
      fontSize = 12.sp,
      fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
      color = if (isSelected) TitanCyan else TextPrimaryDark
    )
    Spacer(modifier = Modifier.width(4.dp))
    Box(
      modifier = Modifier
        .clip(RoundedCornerShape(4.dp))
        .background(SlateDarker)
        .padding(horizontal = 4.dp, vertical = 1.dp)
    ) {
      Text(
        text = highlight.tier,
        fontSize = 9.sp,
        fontWeight = FontWeight.Bold,
        color = TitanGold
      )
    }
  }
}

@Composable
fun CompanyIntelDetailCard(
  highlight: SavedCompanyNewsHighlight
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(12.dp))
      .background(SlateCard)
      .border(1.dp, TitanCyan.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
      .padding(12.dp)
  ) {
    Column {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Newspaper, contentDescription = null, tint = TitanCyan, modifier = Modifier.size(14.dp))
          Spacer(modifier = Modifier.width(5.dp))
          Text(
            text = "${highlight.companyName.uppercase()} • ${highlight.relevanceTag}",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            color = TitanCyan
          )
        }
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(TitanEmerald.copy(alpha = 0.15f))
            .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
          Text(
            text = "${highlight.sentimentScore}% HIRING VELOCITY",
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = TitanEmerald,
            fontFamily = FontFamily.Monospace
          )
        }
      }

      Spacer(modifier = Modifier.height(6.dp))

      Text(
        text = highlight.headline,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        color = TextPrimaryDark
      )

      Spacer(modifier = Modifier.height(4.dp))

      Text(
        text = highlight.newsSummary,
        fontSize = 12.sp,
        color = TextSecondaryDark,
        lineHeight = 16.sp
      )

      Spacer(modifier = Modifier.height(8.dp))

      // Strategic Interview Angle
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(8.dp))
          .background(SlateElevated)
          .border(1.dp, TitanGold.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
          .padding(8.dp)
      ) {
        Row(verticalAlignment = Alignment.Top) {
          Text(text = "🎯", fontSize = 12.sp)
          Spacer(modifier = Modifier.width(6.dp))
          Column {
            Text(
              text = "STRATEGIC INTERVIEW & APPLICATION ANGLE",
              fontSize = 9.sp,
              fontWeight = FontWeight.Bold,
              fontFamily = FontFamily.Monospace,
              color = TitanGold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = highlight.strategicImplication,
              fontSize = 11.sp,
              color = TextPrimaryDark,
              lineHeight = 15.sp
            )
          }
        }
      }
    }
  }
}

@Composable
fun MorningAutomationTaskCard(
  task: MorningAutomationTaskItem,
  onExecute: () -> Unit
) {
  val isCompleted = task.status == "COMPLETED"

  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(10.dp))
      .background(SlateCard)
      .border(1.dp, if (isCompleted) TitanEmerald.copy(alpha = 0.3f) else SlateBorder, RoundedCornerShape(10.dp))
      .padding(10.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Column(modifier = Modifier.weight(1f)) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(if (task.priority == "CRITICAL") TitanAmber.copy(alpha = 0.2f) else TitanCyan.copy(alpha = 0.15f))
            .padding(horizontal = 5.dp, vertical = 2.dp)
        ) {
          Text(
            text = task.priority,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            color = if (task.priority == "CRITICAL") TitanAmber else TitanCyan
          )
        }
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = task.title,
          fontSize = 12.sp,
          fontWeight = FontWeight.SemiBold,
          color = if (isCompleted) TextMutedDark else TextPrimaryDark
        )
      }

      Spacer(modifier = Modifier.height(3.dp))

      Text(
        text = task.recommendedTriggerReason,
        fontSize = 10.sp,
        color = TextSecondaryDark,
        lineHeight = 14.sp
      )
    }

    Spacer(modifier = Modifier.width(8.dp))

    if (isCompleted) {
      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(6.dp))
          .background(TitanEmerald.copy(alpha = 0.15f))
          .padding(horizontal = 8.dp, vertical = 4.dp)
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Check, contentDescription = null, tint = TitanEmerald, modifier = Modifier.size(12.dp))
          Spacer(modifier = Modifier.width(3.dp))
          Text(text = "SYNCED", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TitanEmerald, fontFamily = FontFamily.Monospace)
        }
      }
    } else {
      OutlinedButton(
        onClick = onExecute,
        shape = RoundedCornerShape(6.dp),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = TitanCyan),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp)
      ) {
        Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(12.dp))
        Spacer(modifier = Modifier.width(3.dp))
        Text(text = "RUN", fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
      }
    }
  }
}
