package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.ui.theme.TextMutedDark
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TitanCrimson
import com.example.ui.theme.TitanCyan
import com.example.ui.theme.TitanEmerald
import com.example.ui.theme.TitanGold
import kotlinx.coroutines.delay

@Composable
fun AudioWaveformVisualizer(
  isRecording: Boolean,
  onToggleRecording: () -> Unit,
  modifier: Modifier = Modifier
) {
  var recordingSeconds by remember { mutableIntStateOf(0) }

  LaunchedEffect(isRecording) {
    if (isRecording) {
      recordingSeconds = 0
      while (true) {
        delay(1000)
        recordingSeconds++
      }
    }
  }

  val infiniteTransition = rememberInfiniteTransition(label = "audio_bars")
  val animScale1 by infiniteTransition.animateFloat(
    initialValue = 0.2f,
    targetValue = 0.9f,
    animationSpec = infiniteRepeatable(tween(350, easing = FastOutSlowInEasing), RepeatMode.Reverse),
    label = "bar1"
  )
  val animScale2 by infiniteTransition.animateFloat(
    initialValue = 0.4f,
    targetValue = 1.0f,
    animationSpec = infiniteRepeatable(tween(420, easing = FastOutSlowInEasing), RepeatMode.Reverse),
    label = "bar2"
  )
  val animScale3 by infiniteTransition.animateFloat(
    initialValue = 0.15f,
    targetValue = 0.75f,
    animationSpec = infiniteRepeatable(tween(280, easing = FastOutSlowInEasing), RepeatMode.Reverse),
    label = "bar3"
  )
  val animScale4 by infiniteTransition.animateFloat(
    initialValue = 0.3f,
    targetValue = 0.85f,
    animationSpec = infiniteRepeatable(tween(500, easing = FastOutSlowInEasing), RepeatMode.Reverse),
    label = "bar4"
  )

  val barHeights = if (isRecording) {
    listOf(animScale1, animScale2, animScale3, animScale4, animScale2, animScale1, animScale4, animScale3, animScale2, animScale1, animScale3, animScale4)
  } else {
    listOf(0.2f, 0.3f, 0.25f, 0.35f, 0.2f, 0.4f, 0.25f, 0.3f, 0.2f, 0.25f, 0.35f, 0.2f)
  }

  Box(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(10.dp))
      .background(SlateCard)
      .border(1.dp, if (isRecording) TitanCrimson.copy(alpha = 0.6f) else SlateBorder, RoundedCornerShape(10.dp))
      .padding(horizontal = 12.dp, vertical = 10.dp)
      .testTag("audio_waveform_visualizer")
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Mic Toggle & Timer
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(if (isRecording) TitanCrimson else TitanCyan)
            .clickable { onToggleRecording() },
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = if (isRecording) Icons.Default.Stop else Icons.Default.Mic,
            contentDescription = "Voice Rehearsal",
            tint = ObsidianDark,
            modifier = Modifier.size(20.dp)
          )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column {
          Text(
            text = if (isRecording) "RECORDING VOICE REHEARSAL" else "VOICE DRILL ENGINE",
            style = MaterialTheme.typography.labelSmall,
            color = if (isRecording) TitanCrimson else TitanCyan,
            fontWeight = FontWeight.Bold,
            fontSize = 9.sp
          )
          Text(
            text = if (isRecording) String.format("%02d:%02d • Live Cadence Analysis", recordingSeconds / 60, recordingSeconds % 60) else "Tap mic to rehearse aloud",
            style = MaterialTheme.typography.bodySmall,
            color = if (isRecording) TitanGold else TextMutedDark,
            fontSize = 11.sp
          )
        }
      }

      // Waveform frequency bars
      Row(
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.height(28.dp)
      ) {
        barHeights.forEach { fraction ->
          Box(
            modifier = Modifier
              .width(3.dp)
              .height((24.dp * fraction).coerceAtLeast(4.dp))
              .clip(RoundedCornerShape(2.dp))
              .background(if (isRecording) TitanEmerald else TitanCyan.copy(alpha = 0.5f))
          )
        }
      }
    }
  }
}
