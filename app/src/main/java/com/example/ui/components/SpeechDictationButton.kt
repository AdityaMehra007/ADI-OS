package com.example.ui.components

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
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
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
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
import java.util.Locale

/**
 * Modern Microphone Dictation Composable that connects directly with Android's SpeechRecognizer API
 * to dictate text into Career Strategy note input fields.
 */
@Composable
fun SpeechDictationButton(
  currentText: String,
  onTextAppended: (String) -> Unit,
  modifier: Modifier = Modifier,
  targetFieldLabel: String = "Content",
  testTag: String = "dictate_note_mic_btn"
) {
  val context = LocalContext.current
  var isListening by remember { mutableStateOf(false) }
  var speechRecognizer by remember { mutableStateOf<SpeechRecognizer?>(null) }
  var partialResultText by remember { mutableStateOf("") }
  var statusMessage by remember { mutableStateOf("") }
  var audioAmplitude by remember { mutableFloatStateOf(0.1f) }

  // Check microphone audio permission
  var hasPermission by remember {
    mutableStateOf(
      ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.RECORD_AUDIO
      ) == PackageManager.PERMISSION_GRANTED
    )
  }

  val permissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestPermission()
  ) { isGranted ->
    hasPermission = isGranted
    if (isGranted) {
      statusMessage = "Microphone permission granted. Tap mic to dictate."
    } else {
      statusMessage = "Microphone access denied. Enable in Settings."
      Toast.makeText(context, "Microphone permission needed to dictate notes", Toast.LENGTH_SHORT).show()
    }
  }

  // Animation for recording pulse
  val infiniteTransition = rememberInfiniteTransition(label = "micPulse")
  val pulseScale by infiniteTransition.animateFloat(
    initialValue = 1.0f,
    targetValue = if (isListening) 1.25f else 1.0f,
    animationSpec = infiniteRepeatable(
      animation = tween(600, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "micPulseScale"
  )

  // SpeechRecognizer Lifecycle
  DisposableEffect(context) {
    if (SpeechRecognizer.isRecognitionAvailable(context)) {
      val recognizer = SpeechRecognizer.createSpeechRecognizer(context)
      recognizer.setRecognitionListener(object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) {
          isListening = true
          statusMessage = "Listening... Speak your $targetFieldLabel"
        }

        override fun onBeginningOfSpeech() {
          statusMessage = "Recording voice input..."
        }

        override fun onRmsChanged(rmsdB: Float) {
          audioAmplitude = ((rmsdB + 2f) / 12f).coerceIn(0.1f, 1.0f)
        }

        override fun onBufferReceived(buffer: ByteArray?) {}

        override fun onEndOfSpeech() {
          statusMessage = "Processing speech transcription..."
        }

        override fun onError(error: Int) {
          isListening = false
          audioAmplitude = 0.1f
          statusMessage = when (error) {
            SpeechRecognizer.ERROR_NO_MATCH -> "No speech detected. Tap mic or try preset."
            SpeechRecognizer.ERROR_AUDIO -> "Audio recording error. Check mic."
            SpeechRecognizer.ERROR_NETWORK -> "Network issue. Try again."
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "Speech timeout. Tap mic to retry."
            else -> "Ready. Tap mic to dictate."
          }
        }

        override fun onResults(results: Bundle?) {
          isListening = false
          audioAmplitude = 0.1f
          val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
          if (!matches.isNullOrEmpty()) {
            val spokenText = matches[0].trim()
            if (spokenText.isNotBlank()) {
              appendSpokenText(currentText, spokenText, onTextAppended)
              statusMessage = "Dictated: \"$spokenText\""
            }
          }
          partialResultText = ""
        }

        override fun onPartialResults(partialResults: Bundle?) {
          val partial = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
          if (!partial.isNullOrEmpty()) {
            partialResultText = partial[0]
          }
        }

        override fun onEvent(eventType: Int, params: Bundle?) {}
      })
      speechRecognizer = recognizer
    }

    onDispose {
      try {
        speechRecognizer?.stopListening()
        speechRecognizer?.destroy()
      } catch (_: Exception) {}
      speechRecognizer = null
    }
  }

  fun startListening() {
    if (!hasPermission) {
      permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
      return
    }

    if (SpeechRecognizer.isRecognitionAvailable(context)) {
      val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        putExtra(RecognizerIntent.EXTRA_PROMPT, "Dictate note $targetFieldLabel")
      }
      try {
        isListening = true
        partialResultText = ""
        statusMessage = "Listening... Speak your $targetFieldLabel"
        speechRecognizer?.startListening(intent)
      } catch (e: Exception) {
        isListening = false
        statusMessage = "Error starting microphone: ${e.localizedMessage}"
      }
    } else {
      isListening = true
      statusMessage = "Speech service unavailable. Select a quick voice preset below."
    }
  }

  fun stopListening() {
    try {
      speechRecognizer?.stopListening()
    } catch (_: Exception) {}
    isListening = false
    audioAmplitude = 0.1f
    if (partialResultText.isNotBlank()) {
      appendSpokenText(currentText, partialResultText, onTextAppended)
      statusMessage = "Dictated: \"$partialResultText\""
      partialResultText = ""
    }
  }

  Column(modifier = modifier) {
    // 1. Mic Trigger Button
    IconButton(
      onClick = {
        if (isListening) {
          stopListening()
        } else {
          startListening()
        }
      },
      modifier = Modifier
        .testTag(testTag)
        .size(36.dp)
        .clip(CircleShape)
        .background(
          if (isListening) TitanCrimson.copy(alpha = 0.25f)
          else TitanCyan.copy(alpha = 0.15f)
        )
        .border(
          width = 1.dp,
          color = if (isListening) TitanCrimson else TitanCyan.copy(alpha = 0.5f),
          shape = CircleShape
        )
    ) {
      Box(
        modifier = Modifier.scale(if (isListening) pulseScale else 1.0f),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = if (isListening) Icons.Default.Stop else Icons.Default.Mic,
          contentDescription = if (isListening) "Stop Dictating" else "Dictate with Microphone",
          tint = if (isListening) TitanCrimson else TitanCyan,
          modifier = Modifier.size(18.dp)
        )
      }
    }

    // 2. Active Dictation HUD (shown when listening or status active)
    AnimatedVisibility(
      visible = isListening || statusMessage.isNotBlank(),
      enter = fadeIn() + scaleIn(),
      exit = fadeOut() + scaleOut()
    ) {
      Surface(
        shape = RoundedCornerShape(8.dp),
        color = SlateElevated,
        border = androidx.compose.foundation.BorderStroke(
          1.dp,
          if (isListening) TitanCyan.copy(alpha = 0.6f) else SlateBorder
        ),
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 6.dp)
          .testTag("dictation_live_hud")
      ) {
        Column(modifier = Modifier.padding(10.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(8.dp)
                  .clip(CircleShape)
                  .background(if (isListening) TitanCrimson else TitanEmerald)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = if (isListening) "LIVE DICTATION ($targetFieldLabel)" else "SPEECH STATUS",
                style = MaterialTheme.typography.labelSmall,
                color = if (isListening) TitanCyan else TitanEmerald,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
              )
            }

            if (isListening) {
              Row(
                modifier = Modifier
                  .clip(RoundedCornerShape(4.dp))
                  .background(TitanCrimson.copy(alpha = 0.2f))
                  .clickable { stopListening() }
                  .padding(horizontal = 6.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(Icons.Default.Stop, contentDescription = null, tint = TitanCrimson, modifier = Modifier.size(12.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("DONE", color = TitanCrimson, fontSize = 9.sp, fontWeight = FontWeight.Bold)
              }
            }
          }

          Spacer(modifier = Modifier.height(4.dp))

          // Live spoken words or status
          val displayText = when {
            partialResultText.isNotBlank() -> "\"$partialResultText...\""
            statusMessage.isNotBlank() -> statusMessage
            else -> "Speak clearly into your microphone..."
          }

          Text(
            text = displayText,
            style = MaterialTheme.typography.bodySmall,
            color = if (partialResultText.isNotBlank()) TextPrimaryDark else TextSecondaryDark,
            fontSize = 11.sp,
            fontWeight = if (partialResultText.isNotBlank()) FontWeight.Medium else FontWeight.Normal
          )

          // Quick Dictation Voice Preset Chips
          if (isListening || partialResultText.isBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = "Quick Voice Presets (1-tap insert):",
              style = MaterialTheme.typography.labelSmall,
              color = TextMutedDark,
              fontSize = 9.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              val presets = if (targetFieldLabel.contains("Title", ignoreCase = true)) {
                listOf(
                  "Zepto Dark-Store Unit Economics Angle",
                  "Swiggy Compensation & Equity Multiplier",
                  "SQL Automation & 42% Cycle Compression",
                  "STAR Behavioral Prep for Cross-Functional Ops"
                )
              } else {
                listOf(
                  "Highlight 42% cycle time reduction achieved through automated SQL and Python pipeline dispatch.",
                  "Anchor base compensation at ₹24L with front-loaded quarterly equity vesting tranches.",
                  "Focus interview questions on dark-store unit economics and wastage mitigation models.",
                  "Structure behavioral answers around quantified EBITDA margin optimization."
                )
              }

              presets.forEachIndexed { index, preset ->
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(ObsidianDark)
                    .border(1.dp, TitanCyan.copy(alpha = 0.35f), RoundedCornerShape(6.dp))
                    .clickable {
                      appendSpokenText(currentText, preset, onTextAppended)
                      statusMessage = "Dictated: \"$preset\""
                      if (isListening) stopListening()
                    }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .testTag("dictate_preset_chip_$index")
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                      Icons.Default.RecordVoiceOver,
                      contentDescription = null,
                      tint = TitanCyan,
                      modifier = Modifier.size(11.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                      text = preset.take(38) + if (preset.length > 38) "..." else "",
                      color = TitanCyan,
                      fontSize = 9.sp,
                      fontWeight = FontWeight.Medium
                    )
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}

/**
 * Intelligent text appender that handles leading spacing, capitalization,
 * and punctuation when user speaks multiple phrases.
 */
private fun appendSpokenText(
  existingText: String,
  newText: String,
  onTextUpdated: (String) -> Unit
) {
  val cleanNew = newText.trim().replaceFirstChar {
    if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
  }

  if (existingText.isBlank()) {
    onTextUpdated(cleanNew)
  } else {
    val needsSpace = !existingText.endsWith(" ") && !existingText.endsWith("\n")
    val separator = if (existingText.endsWith(".") || existingText.endsWith("?") || existingText.endsWith("!")) {
      " "
    } else if (needsSpace) {
      ". "
    } else {
      ""
    }
    onTextUpdated(existingText + separator + cleanNew)
  }
}
