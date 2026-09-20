package com.example.ui.components

import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BreathingPattern
import com.example.data.model.PreInterviewPepTalk
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
import com.example.ui.viewmodel.TitanViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import java.util.Locale

@Composable
fun PreInterviewCalmView(
  viewModel: TitanViewModel,
  modifier: Modifier = Modifier
) {
  val subTab by viewModel.preInterviewCalmSubTab.collectAsState()
  val selectedPattern by viewModel.selectedBreathingPattern.collectAsState()
  val isBreathingActive by viewModel.isBreathingActive.collectAsState()
  val completedCycles by viewModel.breathingCompletedCycles.collectAsState()
  val pepTalk by viewModel.preInterviewPepTalk.collectAsState()
  val isGeneratingPepTalk by viewModel.isGeneratingPepTalk.collectAsState()
  val targetCompany by viewModel.pepTalkTargetCompany.collectAsState()
  val targetRole by viewModel.pepTalkTargetRole.collectAsState()

  // Ensure pep talk is generated on entry if missing
  LaunchedEffect(pepTalk) {
    if (pepTalk == null && !isGeneratingPepTalk) {
      viewModel.generatePreInterviewPepTalk()
    }
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(ObsidianDark)
      .testTag("pre_interview_calm_view")
  ) {
    // Header Banner
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 12.dp),
      colors = CardDefaults.cardColors(containerColor = SlateCard),
      shape = RoundedCornerShape(12.dp),
      border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(TitanCyan.copy(alpha = 0.2f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.Psychology, contentDescription = null, tint = TitanCyan, modifier = Modifier.size(16.dp))
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              "PRE-INTERVIEW CALM & GROUNDING",
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.ExtraBold,
              color = TitanCyan,
              letterSpacing = 1.sp
            )
          }
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "Reset your nervous system & prime unshakable executive confidence.",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondaryDark,
            fontSize = 12.sp
          )
        }

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(SlateElevated)
            .border(1.dp, TitanEmerald.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(TitanEmerald)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text("VAGAL READY", color = TitanEmerald, fontSize = 10.sp, fontWeight = FontWeight.Bold)
          }
        }
      }
    }

    // Sub Tabs: 0 = Breathing Guide, 1 = 2-Min Motivational Pep Talk
    TabRow(
      selectedTabIndex = subTab,
      containerColor = SlateCard,
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(10.dp)),
      indicator = { tabPositions ->
        TabRowDefaults.SecondaryIndicator(
          Modifier.tabIndicatorOffset(tabPositions[subTab]),
          color = if (subTab == 0) TitanCyan else TitanGold
        )
      }
    ) {
      Tab(
        selected = subTab == 0,
        onClick = { viewModel.setPreInterviewCalmSubTab(0) },
        text = {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              Icons.Default.Psychology,
              contentDescription = null,
              modifier = Modifier.size(14.dp),
              tint = if (subTab == 0) TitanCyan else TextSecondaryDark
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              "Interactive Breathing",
              color = if (subTab == 0) TitanCyan else TextSecondaryDark,
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp
            )
          }
        },
        modifier = Modifier.testTag("tab_interactive_breathing")
      )

      Tab(
        selected = subTab == 1,
        onClick = { viewModel.setPreInterviewCalmSubTab(1) },
        text = {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              Icons.Default.AutoAwesome,
              contentDescription = null,
              modifier = Modifier.size(14.dp),
              tint = if (subTab == 1) TitanGold else TextSecondaryDark
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              "2-Min Gemini Pep Talk",
              color = if (subTab == 1) TitanGold else TextSecondaryDark,
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp
            )
          }
        },
        modifier = Modifier.testTag("tab_gemini_pep_talk")
      )
    }

    Spacer(modifier = Modifier.height(12.dp))

    // Content Body
    if (subTab == 0) {
      InteractiveBreathingGuide(
        viewModel = viewModel,
        selectedPattern = selectedPattern,
        isBreathingActive = isBreathingActive,
        completedCycles = completedCycles
      )
    } else {
      MotivationalPepTalkView(
        viewModel = viewModel,
        pepTalk = pepTalk,
        isGenerating = isGeneratingPepTalk,
        targetCompany = targetCompany,
        targetRole = targetRole
      )
    }
  }
}

/**
 * 1. Interactive Breathing Guide with real-time animated breathing ring,
 * scientifically proven rhythms (Box 4-4-4-4, 4-7-8, and Resonance 5-1-5-1), and somatic cues.
 */
@Composable
private fun InteractiveBreathingGuide(
  viewModel: TitanViewModel,
  selectedPattern: BreathingPattern,
  isBreathingActive: Boolean,
  completedCycles: Int
) {
  var secondsElapsed by remember { mutableIntStateOf(0) }

  // Heart of the timer loop
  LaunchedEffect(isBreathingActive, selectedPattern) {
    if (isBreathingActive) {
      while (isActive) {
        delay(1000L)
        secondsElapsed += 1
        val cycle = selectedPattern.totalCycleDuration
        if (cycle > 0 && secondsElapsed % cycle == 0) {
          viewModel.incrementBreathingCycle()
        }
      }
    }
  }

  // Calculate current phase and sub-second within the cycle
  val cycleDuration = selectedPattern.totalCycleDuration.coerceAtLeast(1)
  val secondInCycle = secondsElapsed % cycleDuration

  val (phaseTitle, phaseInstruction, phaseRemaining, phaseRatio, activeColor) = remember(
    secondInCycle,
    selectedPattern
  ) {
    when (selectedPattern) {
      BreathingPattern.BOX_BREATHING -> {
        when (secondInCycle) {
          in 0 until 4 -> {
            val rem = 4 - secondInCycle
            val ratio = secondInCycle / 4f
            PhaseState("INHALE DEEP", "Expand diaphragm smoothly with quiet confidence", rem, ratio, TitanCyan)
          }
          in 4 until 8 -> {
            val rem = 8 - secondInCycle
            PhaseState("HOLD WITH POISE", "Retain air comfortably · Oxygenating prefrontal cortex", rem, 1f, TitanGold)
          }
          in 8 until 12 -> {
            val rem = 12 - secondInCycle
            val ratio = 1f - ((secondInCycle - 8) / 4f)
            PhaseState("EXHALE TENSION", "Release all adrenaline and physical doubt", rem, ratio, TitanEmerald)
          }
          else -> {
            val rem = 16 - secondInCycle
            PhaseState("HOLD IN STILLNESS", "Empty lungs · Centered, grounded, and present", rem, 0f, TextSecondaryDark)
          }
        }
      }
      BreathingPattern.RELAXING_478 -> {
        when (secondInCycle) {
          in 0 until 4 -> {
            val rem = 4 - secondInCycle
            val ratio = secondInCycle / 4f
            PhaseState("INHALE QUIETLY", "Inhale through nose for 4 counts", rem, ratio, TitanCyan)
          }
          in 4 until 11 -> {
            val rem = 11 - secondInCycle
            PhaseState("HOLD GENTLY", "Hold breath for 7 counts · Vagus nerve activation", rem, 1f, TitanGold)
          }
          else -> {
            val rem = 19 - secondInCycle
            val ratio = 1f - ((secondInCycle - 11) / 8f)
            PhaseState("SLOW WHOOSH EXHALE", "Exhale completely through mouth with whoosh sound", rem, ratio, TitanEmerald)
          }
        }
      }
      BreathingPattern.RESONANCE -> {
        when (secondInCycle) {
          in 0 until 5 -> {
            val rem = 5 - secondInCycle
            val ratio = secondInCycle / 5f
            PhaseState("BALANCED INHALE", "Smooth 5-second breath aligning heart coherence", rem, ratio, TitanCyan)
          }
          5 -> PhaseState("GENTLE PAUSE", "Transition naturally without force", 1, 1f, TitanGold)
          in 6 until 11 -> {
            val rem = 11 - secondInCycle
            val ratio = 1f - ((secondInCycle - 6) / 5f)
            PhaseState("BALANCED EXHALE", "Smooth 5-second release syncing vocal cords", rem, ratio, TitanEmerald)
          }
          else -> PhaseState("GENTLE PAUSE", "Rest in pure presence", 1, 0f, TextSecondaryDark)
        }
      }
    }
  }

  // Smooth visual scale animation
  val animatedScale by animateFloatAsState(
    targetValue = if (!isBreathingActive) 0.85f else 0.72f + (phaseRatio * 0.45f),
    animationSpec = tween(durationMillis = 950, easing = LinearEasing),
    label = "breathing_scale"
  )

  val animatedRingColor by animateColorAsState(
    targetValue = if (!isBreathingActive) TitanCyan.copy(alpha = 0.5f) else activeColor,
    animationSpec = tween(durationMillis = 600),
    label = "breathing_color"
  )

  Column(
    modifier = Modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState()),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    // Pattern Selector Chips
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 4.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      BreathingPattern.entries.forEach { pattern ->
        val isSelected = pattern == selectedPattern
        Box(
          modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) SlateElevated else SlateCard)
            .border(
              width = 1.dp,
              color = if (isSelected) TitanCyan else SlateBorder,
              shape = RoundedCornerShape(8.dp)
            )
            .clickable {
              viewModel.selectBreathingPattern(pattern)
              secondsElapsed = 0
            }
            .padding(vertical = 8.dp, horizontal = 6.dp)
            .testTag("pattern_chip_${pattern.name}"),
          contentAlignment = Alignment.Center
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
              text = when (pattern) {
                BreathingPattern.BOX_BREATHING -> "Box (4-4-4-4)"
                BreathingPattern.RELAXING_478 -> "4-7-8 Vagus"
                BreathingPattern.RESONANCE -> "Resonance (5-5)"
              },
              color = if (isSelected) TitanCyan else TextSecondaryDark,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Pattern Description Card
    Card(
      modifier = Modifier.fillMaxWidth(),
      colors = CardDefaults.cardColors(containerColor = SlateCard.copy(alpha = 0.7f)),
      shape = RoundedCornerShape(8.dp),
      border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
    ) {
      Row(
        modifier = Modifier.padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(Icons.Default.Lightbulb, contentDescription = null, tint = TitanGold, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = selectedPattern.subtitle,
          fontSize = 11.sp,
          color = TextSecondaryDark
        )
      }
    }

    Spacer(modifier = Modifier.height(24.dp))

    // Animated Breathing Circle Canvas Visualizer
    Box(
      modifier = Modifier
        .size(240.dp)
        .testTag("breathing_circle_container"),
      contentAlignment = Alignment.Center
    ) {
      // Ambient Glow Ring
      Canvas(modifier = Modifier.size(230.dp)) {
        drawCircle(
          color = animatedRingColor.copy(alpha = 0.08f),
          radius = size.minDimension / 2f
        )
        drawCircle(
          brush = Brush.radialGradient(
            colors = listOf(animatedRingColor.copy(alpha = 0.15f), Color.Transparent),
            center = center,
            radius = size.minDimension / 2f
          )
        )
      }

      // Outer Guide Ring
      Canvas(modifier = Modifier.size(210.dp)) {
        drawCircle(
          color = SlateBorder.copy(alpha = 0.6f),
          style = Stroke(width = 2.dp.toPx())
        )
      }

      // Dynamic Expanding / Contracting Breathing Core
      Box(
        modifier = Modifier
          .size(170.dp)
          .scale(animatedScale)
          .clip(CircleShape)
          .background(
            Brush.radialGradient(
              colors = listOf(
                animatedRingColor.copy(alpha = 0.35f),
                animatedRingColor.copy(alpha = 0.12f),
                Color.Transparent
              )
            )
          )
          .border(2.dp, animatedRingColor, CircleShape),
        contentAlignment = Alignment.Center
      ) {
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center,
          modifier = Modifier.padding(12.dp)
        ) {
          if (!isBreathingActive) {
            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = TitanCyan, modifier = Modifier.size(36.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text("TAP START", color = TextPrimaryDark, fontSize = 12.sp, fontWeight = FontWeight.Bold)
          } else {
            Text(
              text = "$phaseRemaining",
              style = MaterialTheme.typography.headlineMedium,
              color = TextPrimaryDark,
              fontWeight = FontWeight.ExtraBold,
              fontSize = 32.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = phaseTitle,
              color = animatedRingColor,
              fontWeight = FontWeight.ExtraBold,
              fontSize = 11.sp,
              textAlign = TextAlign.Center
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Real-time somatic directive text
    Text(
      text = if (isBreathingActive) phaseInstruction else "Tap start to activate rhythm & lower heart rate before dialogue.",
      style = MaterialTheme.typography.bodyMedium,
      color = if (isBreathingActive) TextPrimaryDark else TextSecondaryDark,
      fontSize = 13.sp,
      fontWeight = FontWeight.SemiBold,
      textAlign = TextAlign.Center,
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 24.dp)
        .height(36.dp)
    )

    Spacer(modifier = Modifier.height(14.dp))

    // Stats HUD: Cycles completed & elapsed seconds
    Row(
      modifier = Modifier
        .clip(RoundedCornerShape(8.dp))
        .background(SlateCard)
        .border(1.dp, SlateBorder, RoundedCornerShape(8.dp))
        .padding(horizontal = 16.dp, vertical = 8.dp),
      horizontalArrangement = Arrangement.spacedBy(16.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.Refresh, contentDescription = null, tint = TitanCyan, modifier = Modifier.size(14.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text("Cycles: $completedCycles", color = TitanCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
      }

      Box(modifier = Modifier.size(1.dp, 14.dp).background(SlateBorder))

      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.Bolt, contentDescription = null, tint = TitanGold, modifier = Modifier.size(14.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text("${secondsElapsed}s Active Calm", color = TitanGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
      }
    }

    Spacer(modifier = Modifier.height(20.dp))

    // Controls: Play/Pause, Reset
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.Center,
      verticalAlignment = Alignment.CenterVertically
    ) {
      OutlinedButton(
        onClick = {
          viewModel.resetBreathing()
          secondsElapsed = 0
        },
        border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondaryDark),
        modifier = Modifier.testTag("reset_breathing_button")
      ) {
        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text("Reset", fontSize = 12.sp)
      }

      Spacer(modifier = Modifier.width(16.dp))

      Button(
        onClick = {
          viewModel.toggleBreathing(!isBreathingActive)
        },
        colors = ButtonDefaults.buttonColors(
          containerColor = if (isBreathingActive) TitanCrimson else TitanCyan,
          contentColor = ObsidianDark
        ),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
          .width(160.dp)
          .testTag("toggle_breathing_button")
      ) {
        Icon(
          if (isBreathingActive) Icons.Default.Pause else Icons.Default.PlayArrow,
          contentDescription = null,
          modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = if (isBreathingActive) "Pause Breath" else "Start Breathing",
          fontWeight = FontWeight.ExtraBold,
          fontSize = 13.sp
        )
      }
    }

    Spacer(modifier = Modifier.height(24.dp))
  }
}

private data class PhaseState(
  val title: String,
  val instruction: String,
  val remainingSeconds: Int,
  val ratio: Float,
  val color: Color
)

/**
 * 2. Motivational Pep Talk View
 * Tailored 2-minute spoken motivational briefing generated by Gemini API with text-to-speech reading capability.
 */
@Composable
private fun MotivationalPepTalkView(
  viewModel: TitanViewModel,
  pepTalk: PreInterviewPepTalk?,
  isGenerating: Boolean,
  targetCompany: String,
  targetRole: String
) {
  val context = LocalContext.current
  val clipboardManager = LocalClipboardManager.current

  var isTtsReady by remember { mutableStateOf(false) }
  var isSpeaking by remember { mutableStateOf(false) }
  var copiedOpener by remember { mutableStateOf(false) }

  // Text-To-Speech lifecycle management
  var ttsInstance by remember { mutableStateOf<TextToSpeech?>(null) }

  DisposableEffect(context) {
    val tts = TextToSpeech(context) { status ->
      if (status == TextToSpeech.SUCCESS) {
        ttsInstance?.language = Locale.US
        isTtsReady = true
      }
    }
    tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
      override fun onStart(utteranceId: String?) {
        isSpeaking = true
      }
      override fun onDone(utteranceId: String?) {
        isSpeaking = false
      }
      override fun onError(utteranceId: String?) {
        isSpeaking = false
      }
    })
    ttsInstance = tts

    onDispose {
      try {
        tts.stop()
        tts.shutdown()
      } catch (_: Exception) {}
    }
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState())
  ) {
    // Target Company & Role Selector Row
    Card(
      modifier = Modifier.fillMaxWidth(),
      colors = CardDefaults.cardColors(containerColor = SlateCard),
      shape = RoundedCornerShape(10.dp),
      border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
    ) {
      Column(modifier = Modifier.padding(12.dp)) {
        Text(
          "TARGET INTERVIEW CONTEXT",
          style = MaterialTheme.typography.labelSmall,
          color = TitanGold,
          fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(6.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          OutlinedTextField(
            value = targetCompany,
            onValueChange = { viewModel.setPepTalkTarget(it, targetRole) },
            label = { Text("Target Company", fontSize = 10.sp) },
            modifier = Modifier.weight(1f).testTag("pep_talk_company_input"),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = TitanCyan,
              unfocusedBorderColor = SlateBorder,
              focusedTextColor = TextPrimaryDark,
              unfocusedTextColor = TextPrimaryDark
            ),
            singleLine = true
          )

          OutlinedTextField(
            value = targetRole,
            onValueChange = { viewModel.setPepTalkTarget(targetCompany, it) },
            label = { Text("Target Role", fontSize = 10.sp) },
            modifier = Modifier.weight(1.3f).testTag("pep_talk_role_input"),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = TitanCyan,
              unfocusedBorderColor = SlateBorder,
              focusedTextColor = TextPrimaryDark,
              unfocusedTextColor = TextPrimaryDark
            ),
            singleLine = true
          )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Quick Pick Chips
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          listOf(
            "Google" to "Strategy & Ops Lead",
            "Microsoft" to "Product Operations",
            "Zepto" to "Supply Chain Lead",
            "Stripe" to "BizOps & Analytics"
          ).forEach { (comp, role) ->
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(if (targetCompany.equals(comp, true)) TitanCyan.copy(alpha = 0.2f) else SlateElevated)
                .border(1.dp, if (targetCompany.equals(comp, true)) TitanCyan else SlateBorder, RoundedCornerShape(6.dp))
                .clickable {
                  viewModel.setPepTalkTarget(comp, role)
                  viewModel.generatePreInterviewPepTalk(role, comp)
                }
                .padding(horizontal = 6.dp, vertical = 4.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(comp, color = if (targetCompany.equals(comp, true)) TitanCyan else TextSecondaryDark, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(
          onClick = {
            viewModel.generatePreInterviewPepTalk()
          },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("generate_pep_talk_button"),
          colors = ButtonDefaults.buttonColors(containerColor = TitanCyan, contentColor = ObsidianDark),
          shape = RoundedCornerShape(8.dp),
          enabled = !isGenerating
        ) {
          if (isGenerating) {
            CircularProgressIndicator(modifier = Modifier.size(16.dp), color = ObsidianDark, strokeWidth = 2.dp)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Synthesizing 2-Min Briefing...", fontWeight = FontWeight.Bold, fontSize = 12.sp)
          } else {
            Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Regenerate Gemini Executive Pep Talk", fontWeight = FontWeight.Bold, fontSize = 12.sp)
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    if (pepTalk != null) {
      // Audio Player Hero Card
      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SlateCard),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, TitanGold.copy(alpha = 0.4f))
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
                  .size(32.dp)
                  .clip(CircleShape)
                  .background(TitanGold.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  if (isSpeaking) Icons.Default.PlayArrow else Icons.Default.AutoAwesome,
                  contentDescription = null,
                  tint = TitanGold,
                  modifier = Modifier.size(18.dp)
                )
              }
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text("2-MINUTE AUDIO PEP TALK", style = MaterialTheme.typography.labelSmall, color = TitanGold, fontWeight = FontWeight.Bold)
                Text("Gemini 3.5 Flash Spoken Briefing", style = MaterialTheme.typography.titleSmall, color = TextPrimaryDark, fontWeight = FontWeight.Bold)
              }
            }

            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(SlateElevated)
                .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
              Text("~120 Secs", color = TitanCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Audio Waveform Visualizer
          AudioWaveformVisualizer(
            isRecording = isSpeaking,
            onToggleRecording = {
              if (isSpeaking) {
                ttsInstance?.stop()
                isSpeaking = false
              } else {
                ttsInstance?.speak(pepTalk.speechScript, TextToSpeech.QUEUE_FLUSH, null, "pep_talk_utterance")
                isSpeaking = true
              }
            },
            modifier = Modifier.fillMaxWidth()
          )

          Spacer(modifier = Modifier.height(10.dp))

          // Audio Control Buttons
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Button(
              onClick = {
                if (isSpeaking) {
                  ttsInstance?.stop()
                  isSpeaking = false
                } else {
                  ttsInstance?.speak(pepTalk.speechScript, TextToSpeech.QUEUE_FLUSH, null, "pep_talk_utterance")
                  isSpeaking = true
                }
              },
              colors = ButtonDefaults.buttonColors(
                containerColor = if (isSpeaking) TitanCrimson else TitanGold,
                contentColor = ObsidianDark
              ),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.testTag("play_pep_talk_audio_button")
            ) {
              Icon(if (isSpeaking) Icons.Default.Stop else Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                if (isSpeaking) "Stop Audio Briefing" else "Listen to Audio Pep Talk",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Spoken Script Transcript Card
      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SlateCard),
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text("MOTIVATIONAL SCRIPT TRANSCRIPT", style = MaterialTheme.typography.labelSmall, color = TitanCyan, fontWeight = FontWeight.Bold)
            Text("Spoken Voice", color = TextMutedDark, fontSize = 10.sp)
          }

          Spacer(modifier = Modifier.height(8.dp))

          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(8.dp))
              .background(SlateElevated)
              .padding(12.dp)
          ) {
            Text(
              text = "\"${pepTalk.speechScript}\"",
              color = TextPrimaryDark,
              fontSize = 13.sp,
              lineHeight = 20.sp,
              fontStyle = FontStyle.Italic
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // 3 Mindset Anchors Card
      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SlateCard),
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Text("CORE PSYCHOLOGICAL ANCHORS", style = MaterialTheme.typography.labelSmall, color = TitanGold, fontWeight = FontWeight.Bold)
          Spacer(modifier = Modifier.height(8.dp))

          pepTalk.mindsetAnchors.forEachIndexed { index, anchor ->
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
              verticalAlignment = Alignment.Top
            ) {
              Box(
                modifier = Modifier
                  .size(20.dp)
                  .clip(CircleShape)
                  .background(TitanGold.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
              ) {
                Text("${index + 1}", color = TitanGold, fontSize = 10.sp, fontWeight = FontWeight.Bold)
              }
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = anchor,
                color = TextPrimaryDark,
                fontSize = 12.sp,
                lineHeight = 17.sp,
                fontWeight = FontWeight.Medium
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Power Affirmations Card
      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SlateCard),
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Text("HIGH-IMPACT AFFIRMATIONS", style = MaterialTheme.typography.labelSmall, color = TitanEmerald, fontWeight = FontWeight.Bold)
          Spacer(modifier = Modifier.height(8.dp))

          pepTalk.powerAffirmations.forEach { affirmation ->
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(Icons.Default.CheckCircle, contentDescription = null, tint = TitanEmerald, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = affirmation,
                color = TextPrimaryDark,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Somatic Grounding Checklist
      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SlateCard),
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Bolt, contentDescription = null, tint = TitanCyan, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("SOMATIC POSTURE DIRECTIVE", style = MaterialTheme.typography.labelSmall, color = TitanCyan, fontWeight = FontWeight.Bold)
          }
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = pepTalk.physicalGroundingTip,
            color = TextSecondaryDark,
            fontSize = 12.sp,
            lineHeight = 18.sp
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // 30-Second Power Opener
      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SlateCard),
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, TitanCyan.copy(alpha = 0.3f))
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text("30-SECOND POWER OPENER", style = MaterialTheme.typography.labelSmall, color = TitanCyan, fontWeight = FontWeight.Bold)
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(if (copiedOpener) TitanEmerald.copy(alpha = 0.2f) else SlateElevated)
                .clickable {
                  clipboardManager.setText(AnnotatedString(pepTalk.thirtySecondOpener))
                  copiedOpener = true
                }
                .padding(horizontal = 8.dp, vertical = 4.dp),
              contentAlignment = Alignment.Center
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  if (copiedOpener) Icons.Default.Check else Icons.Default.AutoAwesome,
                  contentDescription = null,
                  tint = if (copiedOpener) TitanEmerald else TitanCyan,
                  modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  if (copiedOpener) "Copied!" else "Copy Opener",
                  color = if (copiedOpener) TitanEmerald else TitanCyan,
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = pepTalk.thirtySecondOpener,
            color = TextPrimaryDark,
            fontSize = 12.sp,
            lineHeight = 18.sp
          )
        }
      }

      Spacer(modifier = Modifier.height(30.dp))
    }
  }
}
