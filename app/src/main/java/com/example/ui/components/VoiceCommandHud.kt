package com.example.ui.components

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.data.model.VoiceCommandInterpretation
import com.example.data.model.VoiceIntentType
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
import com.example.ui.theme.TitanIndigo
import com.example.ui.viewmodel.TitanScreen
import com.example.ui.viewmodel.TitanViewModel
import java.util.Locale

@Composable
fun VoiceCommandCenterCard(
  viewModel: TitanViewModel,
  onNavigateToJobsRadar: () -> Unit,
  onNavigateToCompanyIntel: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  val isListening by viewModel.isVoiceListening.collectAsState()
  val isProcessing by viewModel.isProcessingVoiceCommand.collectAsState()
  val liveTranscript by viewModel.voiceLiveTranscription.collectAsState()
  val lastResult by viewModel.lastVoiceResult.collectAsState()
  val isHudOpen by viewModel.isVoiceHudOpen.collectAsState()
  val context = LocalContext.current

  var hasAudioPermission by remember {
    mutableStateOf(
      ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
    )
  }

  val permissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestPermission()
  ) { isGranted ->
    hasAudioPermission = isGranted
    if (isGranted) {
      viewModel.setVoiceHudOpen(true)
    }
  }

  // Card container in the Command Center
  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("voice_command_center_card"),
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = SlateCard),
    border = androidx.compose.foundation.BorderStroke(1.dp, if (isListening) TitanCyan else SlateBorder)
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      // Header with Live Status & Mic Button
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(38.dp)
              .clip(CircleShape)
              .background(if (isListening) TitanCrimson else TitanCyan.copy(alpha = 0.2f))
              .border(1.dp, if (isListening) TitanCrimson else TitanCyan, CircleShape)
              .clickable {
                if (!hasAudioPermission) {
                  permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                } else {
                  viewModel.setVoiceHudOpen(true)
                }
              }
              .testTag("voice_mic_trigger_btn"),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = if (isListening) Icons.Default.Stop else Icons.Default.Mic,
              contentDescription = "Voice Command",
              tint = if (isListening) ObsidianDark else TitanCyan,
              modifier = Modifier.size(20.dp)
            )
          }

          Spacer(modifier = Modifier.width(10.dp))

          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "VOICE COMMAND CENTER",
                style = MaterialTheme.typography.labelSmall,
                color = TitanCyan,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                letterSpacing = 1.sp
              )
              Spacer(modifier = Modifier.width(6.dp))
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(3.dp))
                  .background(TitanEmerald.copy(alpha = 0.15f))
                  .padding(horizontal = 5.dp, vertical = 1.dp)
              ) {
                Text("AI NATURAL LANGUAGE", color = TitanEmerald, fontSize = 8.sp, fontWeight = FontWeight.Bold)
              }
            }
            Text(
              text = if (isListening) "Listening to prompt..." else "Speak to search jobs, summarize news & dispatch",
              style = MaterialTheme.typography.bodySmall,
              color = if (isListening) TitanGold else TextSecondaryDark,
              fontSize = 11.sp
            )
          }
        }

        Button(
          onClick = {
            if (!hasAudioPermission) {
              permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            } else {
              viewModel.setVoiceHudOpen(true)
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = SlateElevated),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier.height(34.dp)
        ) {
          Icon(Icons.Default.Mic, contentDescription = null, tint = TitanCyan, modifier = Modifier.size(14.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Launch Mic", color = TitanCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Quick Natural Language Spoken Prompt Suggestions
      Text(
        text = "POPULAR VOICE PROMPTS:",
        style = MaterialTheme.typography.labelSmall,
        color = TextMutedDark,
        fontSize = 8.sp,
        fontWeight = FontWeight.Bold
      )

      Spacer(modifier = Modifier.height(6.dp))

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        VoicePromptChip(
          icon = Icons.Default.Search,
          text = "Find Strategy Analyst jobs in Bengaluru",
          onClick = { viewModel.executeVoiceCommand("Find Strategy Analyst jobs in Bengaluru") }
        )
        VoicePromptChip(
          icon = Icons.Default.Business,
          text = "Summarize latest news for Zepto",
          onClick = { viewModel.executeVoiceCommand("Summarize latest news for Zepto") }
        )
        VoicePromptChip(
          icon = Icons.Default.Work,
          text = "Search 0-2 yrs fresher friendly Operations roles",
          onClick = { viewModel.executeVoiceCommand("Search 0-2 yrs fresher friendly Operations roles") }
        )
        VoicePromptChip(
          icon = Icons.Default.AutoAwesome,
          text = "Summarize Google leadership and strategic news",
          onClick = { viewModel.executeVoiceCommand("Summarize Google leadership and strategic news") }
        )
        VoicePromptChip(
          icon = Icons.Default.Bolt,
          text = "Run batch automation pipeline",
          onClick = { viewModel.executeVoiceCommand("Run batch automation pipeline") }
        )
      }

      // Recent Voice Execution Result Card (if any)
      AnimatedVisibility(
        visible = lastResult != null || isProcessing,
        enter = fadeIn(),
        exit = fadeOut()
      ) {
        Column {
          Spacer(modifier = Modifier.height(10.dp))

          if (isProcessing) {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(SlateElevated)
                .padding(12.dp),
              contentAlignment = Alignment.Center
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                CircularProgressIndicator(modifier = Modifier.size(16.dp), color = TitanCyan, strokeWidth = 2.dp)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Analyzing voice command with Gemini AI...", style = MaterialTheme.typography.bodySmall, color = TitanCyan, fontSize = 11.sp)
              }
            }
          } else if (lastResult != null) {
            VoiceResultCard(
              result = lastResult!!,
              onClose = { viewModel.clearLastVoiceResult() },
              onExploreJobs = onNavigateToJobsRadar,
              onExploreCompany = onNavigateToCompanyIntel
            )
          }
        }
      }
    }
  }

  // Full Voice Command Interactive Modal / HUD
  if (isHudOpen) {
    VoiceCommandModalDialog(
      viewModel = viewModel,
      onDismiss = { viewModel.setVoiceHudOpen(false) },
      onExploreJobs = {
        viewModel.setVoiceHudOpen(false)
        onNavigateToJobsRadar()
      },
      onExploreCompany = { companyName ->
        viewModel.setVoiceHudOpen(false)
        onNavigateToCompanyIntel(companyName)
      }
    )
  }
}

@Composable
fun VoicePromptChip(
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  text: String,
  onClick: () -> Unit
) {
  Box(
    modifier = Modifier
      .clip(RoundedCornerShape(6.dp))
      .background(SlateElevated)
      .border(1.dp, SlateBorder, RoundedCornerShape(6.dp))
      .clickable { onClick() }
      .padding(horizontal = 8.dp, vertical = 6.dp)
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Icon(icon, contentDescription = null, tint = TitanCyan, modifier = Modifier.size(12.dp))
      Spacer(modifier = Modifier.width(4.dp))
      Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        color = TextPrimaryDark,
        fontSize = 10.sp,
        fontWeight = FontWeight.Medium
      )
    }
  }
}

@Composable
fun VoiceResultCard(
  result: VoiceCommandInterpretation,
  onClose: () -> Unit,
  onExploreJobs: () -> Unit,
  onExploreCompany: (String) -> Unit
) {
  val (badgeColor, badgeTitle, badgeIcon) = when (result.intentType) {
    VoiceIntentType.JOB_SEARCH -> Triple(TitanCyan, "RADAR JOB FILTER EXECUTED", Icons.Default.Search)
    VoiceIntentType.SUMMARIZE_COMPANY_NEWS -> Triple(TitanGold, "GROUNDED COMPANY NEWS SUMMARY", Icons.Default.Business)
    VoiceIntentType.AUTOMATION_DISPATCH -> Triple(TitanEmerald, "AUTOMATION DISPATCH TRIGGERED", Icons.Default.Bolt)
    else -> Triple(TitanIndigo, "EXECUTIVE COMMAND PROCESSED", Icons.Default.AutoAwesome)
  }

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("voice_result_card"),
    shape = RoundedCornerShape(10.dp),
    colors = CardDefaults.cardColors(containerColor = SlateElevated),
    border = androidx.compose.foundation.BorderStroke(1.dp, badgeColor.copy(alpha = 0.5f))
  ) {
    Column(modifier = Modifier.padding(12.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(badgeIcon, contentDescription = null, tint = badgeColor, modifier = Modifier.size(14.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = badgeTitle,
            style = MaterialTheme.typography.labelSmall,
            color = badgeColor,
            fontWeight = FontWeight.Bold,
            fontSize = 9.sp
          )
        }

        IconButton(onClick = onClose, modifier = Modifier.size(20.dp)) {
          Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMutedDark, modifier = Modifier.size(14.dp))
        }
      }

      Spacer(modifier = Modifier.height(4.dp))

      Text(
        text = result.summaryHeadline,
        style = MaterialTheme.typography.titleSmall,
        color = TextPrimaryDark,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp
      )

      Spacer(modifier = Modifier.height(4.dp))

      Text(
        text = result.summaryResult,
        style = MaterialTheme.typography.bodySmall,
        color = TextSecondaryDark,
        fontSize = 11.sp
      )

      Spacer(modifier = Modifier.height(8.dp))

      // Action Button corresponding to Intent
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
      ) {
        when (result.intentType) {
          VoiceIntentType.JOB_SEARCH -> {
            Button(
              onClick = onExploreJobs,
              colors = ButtonDefaults.buttonColors(containerColor = TitanCyan, contentColor = ObsidianDark),
              shape = RoundedCornerShape(6.dp),
              modifier = Modifier.height(30.dp)
            ) {
              Icon(Icons.Default.Work, contentDescription = null, modifier = Modifier.size(12.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("View Radar Matches (${result.matchedJobsCount})", fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
          }
          VoiceIntentType.SUMMARIZE_COMPANY_NEWS -> {
            Button(
              onClick = { onExploreCompany(result.targetCompany) },
              colors = ButtonDefaults.buttonColors(containerColor = TitanGold, contentColor = ObsidianDark),
              shape = RoundedCornerShape(6.dp),
              modifier = Modifier.height(30.dp)
            ) {
              Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null, modifier = Modifier.size(12.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Open ${result.targetCompany} Intel Dossier", fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
          }
          else -> {}
        }
      }
    }
  }
}

@Composable
fun VoiceCommandModalDialog(
  viewModel: TitanViewModel,
  onDismiss: () -> Unit,
  onExploreJobs: () -> Unit,
  onExploreCompany: (String) -> Unit
) {
  val context = LocalContext.current
  val isListening by viewModel.isVoiceListening.collectAsState()
  val isProcessing by viewModel.isProcessingVoiceCommand.collectAsState()
  val liveTranscript by viewModel.voiceLiveTranscription.collectAsState()
  val lastResult by viewModel.lastVoiceResult.collectAsState()
  val audioAmplitude by viewModel.voiceAudioAmplitude.collectAsState()

  var manualInputText by remember { mutableStateOf("") }
  var speechRecognizer by remember { mutableStateOf<SpeechRecognizer?>(null) }
  var speechStatusMessage by remember { mutableStateOf("Tap the microphone below to start speaking") }

  // Check audio permission
  val hasPermission = ContextCompat.checkSelfPermission(
    context,
    Manifest.permission.RECORD_AUDIO
  ) == PackageManager.PERMISSION_GRANTED

  // Speech Recognizer setup
  DisposableEffect(Unit) {
    if (SpeechRecognizer.isRecognitionAvailable(context)) {
      val recognizer = SpeechRecognizer.createSpeechRecognizer(context)
      recognizer.setRecognitionListener(object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) {
          speechStatusMessage = "Listening... Speak your command clearly"
          viewModel.setVoiceListening(true)
        }

        override fun onBeginningOfSpeech() {
          speechStatusMessage = "Capturing voice stream..."
        }

        override fun onRmsChanged(rmsdB: Float) {
          val normalized = ((rmsdB + 2f) / 12f).coerceIn(0.1f, 1.0f)
          viewModel.updateVoiceAudioAmplitude(normalized)
        }

        override fun onBufferReceived(buffer: ByteArray?) {}

        override fun onEndOfSpeech() {
          speechStatusMessage = "Processing speech with Gemini AI..."
          viewModel.setVoiceListening(false)
        }

        override fun onError(error: Int) {
          viewModel.setVoiceListening(false)
          speechStatusMessage = when (error) {
            SpeechRecognizer.ERROR_NO_MATCH -> "No speech detected. Try speaking again or pick a quick prompt."
            SpeechRecognizer.ERROR_AUDIO -> "Audio recording error. Check microphone."
            SpeechRecognizer.ERROR_NETWORK -> "Network issue. Using AI semantic processor."
            else -> "Ready for command. Tap mic or select a prompt."
          }
        }

        override fun onResults(results: Bundle?) {
          val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
          if (!matches.isNullOrEmpty()) {
            val spoken = matches[0]
            viewModel.updateVoiceTranscription(spoken)
            viewModel.executeVoiceCommand(spoken)
          }
        }

        override fun onPartialResults(partialResults: Bundle?) {
          val partial = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
          if (!partial.isNullOrEmpty()) {
            viewModel.updateVoiceTranscription(partial[0])
          }
        }

        override fun onEvent(eventType: Int, params: Bundle?) {}
      })
      speechRecognizer = recognizer
    }

    onDispose {
      speechRecognizer?.destroy()
      speechRecognizer = null
      viewModel.setVoiceListening(false)
    }
  }

  fun startListening() {
    if (SpeechRecognizer.isRecognitionAvailable(context) && hasPermission) {
      val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
      }
      try {
        speechRecognizer?.startListening(intent)
      } catch (e: Exception) {
        viewModel.setVoiceListening(true)
        speechStatusMessage = "Listening mode active..."
      }
    } else {
      // Direct simulation if recognizer unavailable
      viewModel.setVoiceListening(true)
      speechStatusMessage = "Listening mode active. Select a prompt or speak."
    }
  }

  fun stopListening() {
    try {
      speechRecognizer?.stopListening()
    } catch (e: Exception) {
      // Ignored
    }
    viewModel.setVoiceListening(false)
  }

  // Animation for pulsing listening ring
  val infiniteTransition = rememberInfiniteTransition(label = "pulse")
  val pulseScale by infiniteTransition.animateFloat(
    initialValue = 1.0f,
    targetValue = if (isListening) 1.25f else 1.0f,
    animationSpec = infiniteRepeatable(tween(600, easing = FastOutSlowInEasing), RepeatMode.Reverse),
    label = "pulse_scale"
  )

  AlertDialog(
    onDismissRequest = {
      stopListening()
      onDismiss()
    },
    containerColor = ObsidianDark,
    shape = RoundedCornerShape(16.dp),
    modifier = Modifier
      .fillMaxWidth()
      .testTag("voice_command_modal"),
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
              .background(TitanCyan.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
          ) {
            Icon(Icons.Default.Mic, contentDescription = null, tint = TitanCyan, modifier = Modifier.size(18.dp))
          }
          Spacer(modifier = Modifier.width(8.dp))
          Column {
            Text(
              text = "VOICE COMMAND CENTER",
              style = MaterialTheme.typography.titleMedium,
              color = TextPrimaryDark,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = "Natural Language Job Sourcing & News Intel",
              style = MaterialTheme.typography.bodySmall,
              color = TitanCyan,
              fontSize = 10.sp
            )
          }
        }

        IconButton(onClick = {
          stopListening()
          onDismiss()
        }) {
          Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMutedDark)
        }
      }
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Spacer(modifier = Modifier.height(10.dp))

        // Center Pulsing Mic Button & Audio Waveform
        Box(
          modifier = Modifier
            .size(100.dp),
          contentAlignment = Alignment.Center
        ) {
          // Outer Glow Ring
          if (isListening) {
            Box(
              modifier = Modifier
                .size(90.dp)
                .scale(pulseScale)
                .clip(CircleShape)
                .background(TitanCrimson.copy(alpha = 0.25f))
            )
          }

          // Main Mic Toggle
          Box(
            modifier = Modifier
              .size(68.dp)
              .clip(CircleShape)
              .background(
                brush = Brush.radialGradient(
                  colors = if (isListening) listOf(TitanCrimson, Color(0xFF881122)) else listOf(TitanCyan, TitanIndigo)
                )
              )
              .border(2.dp, if (isListening) TitanCrimson else TitanCyan, CircleShape)
              .clickable {
                if (isListening) {
                  stopListening()
                } else {
                  startListening()
                }
              }
              .testTag("modal_mic_action_button"),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = if (isListening) Icons.Default.Stop else Icons.Default.Mic,
              contentDescription = "Microphone Toggle",
              tint = ObsidianDark,
              modifier = Modifier.size(32.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Status Text
        Text(
          text = if (isListening) "LISTENING TO VOICE..." else speechStatusMessage,
          style = MaterialTheme.typography.labelSmall,
          color = if (isListening) TitanGold else TextSecondaryDark,
          fontWeight = FontWeight.Bold,
          fontSize = 11.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Live Audio Equalizer Bars
        Row(
          horizontalArrangement = Arrangement.spacedBy(4.dp),
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.height(24.dp)
        ) {
          val bars = listOf(0.3f, 0.6f, 0.9f, 0.5f, 0.8f, 1.0f, 0.7f, 0.4f, 0.9f, 0.5f, 0.7f, 0.3f)
          bars.forEach { baseHeight ->
            val dynamicHeight = if (isListening) (24.dp * baseHeight * audioAmplitude.coerceAtLeast(0.4f)).coerceAtLeast(4.dp) else 4.dp
            Box(
              modifier = Modifier
                .width(4.dp)
                .height(dynamicHeight)
                .clip(RoundedCornerShape(2.dp))
                .background(if (isListening) TitanCyan else SlateBorder)
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Live Transcription Display Box
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(SlateCard)
            .border(1.dp, SlateBorder, RoundedCornerShape(10.dp))
            .padding(12.dp)
        ) {
          Column {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(
                text = "TRANSCRIBED COMMAND:",
                style = MaterialTheme.typography.labelSmall,
                color = TitanCyan,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
              )
              if (liveTranscript.isNotBlank()) {
                Text("Detected", color = TitanEmerald, fontSize = 9.sp, fontWeight = FontWeight.Bold)
              }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
              text = if (liveTranscript.isBlank()) "\"Speak any job query or company news request...\"" else "\"$liveTranscript\"",
              style = MaterialTheme.typography.bodyMedium,
              color = if (liveTranscript.isBlank()) TextMutedDark else TextPrimaryDark,
              fontWeight = if (liveTranscript.isBlank()) FontWeight.Normal else FontWeight.SemiBold,
              fontSize = 12.sp
            )
          }
        }

        // Result Preview Card (if present)
        if (lastResult != null && !isProcessing) {
          Spacer(modifier = Modifier.height(12.dp))
          VoiceResultCard(
            result = lastResult!!,
            onClose = { viewModel.clearLastVoiceResult() },
            onExploreJobs = onExploreJobs,
            onExploreCompany = onExploreCompany
          )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Suggested Voice Prompts
        Text(
          text = "OR TAP AN EXECUTIVE VOICE PROMPT:",
          style = MaterialTheme.typography.labelSmall,
          color = TextMutedDark,
          fontSize = 8.sp,
          fontWeight = FontWeight.Bold,
          modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(6.dp))

        Column(
          verticalArrangement = Arrangement.spacedBy(6.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          VoicePromptOptionItem(
            category = "🎯 JOB SEARCH",
            prompt = "Find Strategy Analyst jobs in Bengaluru > ₹15 LPA",
            onClick = {
              viewModel.executeVoiceCommand("Find Strategy Analyst jobs in Bengaluru > 15 LPA")
            }
          )
          VoicePromptOptionItem(
            category = "📰 COMPANY NEWS",
            prompt = "Summarize latest news & hiring updates for Zepto",
            onClick = {
              viewModel.executeVoiceCommand("Summarize latest news & hiring updates for Zepto")
            }
          )
          VoicePromptOptionItem(
            category = "🎓 0-2 YRS FRESHER",
            prompt = "Search fresher-friendly Business Analyst openings",
            onClick = {
              viewModel.executeVoiceCommand("Search fresher-friendly Business Analyst openings")
            }
          )
          VoicePromptOptionItem(
            category = "📰 LEADERSHIP INTEL",
            prompt = "Summarize Google strategic business developments",
            onClick = {
              viewModel.executeVoiceCommand("Summarize Google strategic business developments")
            }
          )
          VoicePromptOptionItem(
            category = "⚡ AUTO DISPATCH",
            prompt = "Run batch automation and match top strategy roles",
            onClick = {
              viewModel.executeVoiceCommand("Run batch automation and match top strategy roles")
            }
          )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Manual Text Fallback Field
        OutlinedTextField(
          value = manualInputText,
          onValueChange = { manualInputText = it },
          placeholder = { Text("Or type command...", color = TextMutedDark, fontSize = 11.sp) },
          trailingIcon = {
            IconButton(
              onClick = {
                if (manualInputText.isNotBlank()) {
                  viewModel.executeVoiceCommand(manualInputText)
                  manualInputText = ""
                }
              }
            ) {
              Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = TitanCyan, modifier = Modifier.size(16.dp))
            }
          },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("voice_manual_input_field"),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = TitanCyan,
            unfocusedBorderColor = SlateBorder,
            focusedContainerColor = SlateCard,
            unfocusedContainerColor = SlateCard,
            focusedTextColor = TextPrimaryDark,
            unfocusedTextColor = TextPrimaryDark
          ),
          shape = RoundedCornerShape(8.dp),
          singleLine = true
        )
      }
    },
    confirmButton = {
      Button(
        onClick = {
          stopListening()
          onDismiss()
        },
        colors = ButtonDefaults.buttonColors(containerColor = SlateElevated),
        shape = RoundedCornerShape(8.dp)
      ) {
        Text("Done", color = TextPrimaryDark)
      }
    }
  )
}

@Composable
fun VoicePromptOptionItem(
  category: String,
  prompt: String,
  onClick: () -> Unit
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(8.dp))
      .background(SlateElevated)
      .border(1.dp, SlateBorder, RoundedCornerShape(8.dp))
      .clickable { onClick() }
      .padding(10.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = category,
          style = MaterialTheme.typography.labelSmall,
          color = TitanCyan,
          fontWeight = FontWeight.Bold,
          fontSize = 8.sp
        )
        Text(
          text = "\"$prompt\"",
          style = MaterialTheme.typography.bodySmall,
          color = TextPrimaryDark,
          fontSize = 11.sp,
          fontWeight = FontWeight.Medium
        )
      }

      Icon(
        imageVector = Icons.Default.Mic,
        contentDescription = "Speak",
        tint = TitanGold,
        modifier = Modifier.size(16.dp)
      )
    }
  }
}
