package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CareerMomentumData
import com.example.data.model.DailyHabitItem
import com.example.data.model.HabitCategory
import com.example.service.FirestoreSyncState
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateCard
import com.example.ui.theme.SlateDarker
import com.example.ui.theme.SlateElevated
import com.example.ui.theme.TextMutedDark
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import com.example.ui.theme.TitanCyan
import com.example.ui.theme.TitanCyanGlow
import com.example.ui.theme.TitanEmerald
import com.example.ui.theme.TitanGold
import com.example.ui.theme.TitanIndigo
import com.example.ui.theme.TitanRose
import com.example.ui.theme.TitanViolet
import com.example.ui.viewmodel.TitanViewModel

@Composable
fun CareerMomentumWidget(
  viewModel: TitanViewModel,
  modifier: Modifier = Modifier
) {
  val momentumData by viewModel.careerMomentum.collectAsState()
  val syncState by viewModel.firestoreMomentumSyncState.collectAsState()
  val syncMessage by viewModel.firestoreMomentumStatusMessage.collectAsState()
  val lastSyncedTime by viewModel.firestoreMomentumLastSynced.collectAsState()

  var showAddHabitDialog by remember { mutableStateOf(false) }

  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("career_momentum_widget"),
    colors = CardDefaults.cardColors(containerColor = SlateCard),
    border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder),
    shape = RoundedCornerShape(16.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(18.dp)
    ) {
      // 1. HEADER ROW: Title, Streak Pill, and Firestore Cloud Indicator
      HeaderRow(
        streakDays = momentumData.streakDays,
        syncState = syncState,
        syncMessage = syncMessage,
        onForceSync = { viewModel.syncMomentumToFirestore() }
      )

      Spacer(modifier = Modifier.height(14.dp))

      // 2. HERO PROGRESS BAR SECTION (PROGRESS BAR IN FIRESTORE VISUALIZER)
      HeroProgressBarSection(
        momentumData = momentumData,
        syncState = syncState
      )

      Spacer(modifier = Modifier.height(16.dp))
      HorizontalDivider(color = SlateBorder.copy(alpha = 0.6f), thickness = 1.dp)
      Spacer(modifier = Modifier.height(14.dp))

      // 3. HABIT ITEMS LIST (E.G. 'NETWORKING OUTREACH', 'SKILL BUILDING')
      HabitsListSection(
        habits = momentumData.habits,
        onToggleCompletion = { habitId -> viewModel.toggleHabitCompletion(habitId) },
        onIncrementCount = { habitId -> viewModel.incrementHabitCount(habitId) },
        onDecrementCount = { habitId -> viewModel.decrementHabitCount(habitId) },
        onDeleteHabit = { habitId -> viewModel.deleteHabit(habitId) }
      )

      Spacer(modifier = Modifier.height(14.dp))

      // 4. ACTION CONTROLS: Add Custom Habit & Quick Preset Levers
      ActionControlsRow(
        onOpenAddDialog = { showAddHabitDialog = true },
        onResetDay = { viewModel.resetMomentumForDay() },
        onSyncNow = { viewModel.syncMomentumToFirestore() },
        isSyncing = syncState == FirestoreSyncState.SYNCING
      )
    }
  }

  // DIALOG FOR ADDING CUSTOM HABIT
  if (showAddHabitDialog) {
    AddCustomHabitDialog(
      onDismiss = { showAddHabitDialog = false },
      onAddHabit = { title, desc, category, target ->
        viewModel.addCustomHabit(title, desc, category, target)
        showAddHabitDialog = false
      }
    )
  }
}

// ==============================================================================================
// 1. HEADER ROW: TITLE, STREAK, AND FIRESTORE SYNC STATUS
// ==============================================================================================
@Composable
private fun HeaderRow(
  streakDays: Int,
  syncState: FirestoreSyncState,
  syncMessage: String,
  onForceSync: () -> Unit
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Box(
        modifier = Modifier
          .size(36.dp)
          .clip(RoundedCornerShape(10.dp))
          .background(TitanCyanGlow)
          .border(1.dp, TitanCyan.copy(alpha = 0.4f), RoundedCornerShape(10.dp)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.Bolt,
          contentDescription = null,
          tint = TitanCyan,
          modifier = Modifier.size(20.dp)
        )
      }
      Spacer(modifier = Modifier.width(10.dp))
      Column {
        Text(
          text = "CAREER MOMENTUM",
          style = MaterialTheme.typography.titleMedium,
          color = TextPrimaryDark,
          fontWeight = FontWeight.Bold,
          letterSpacing = 0.5.sp
        )
        Text(
          text = "Daily Habit Execution & Velocity Engine",
          style = MaterialTheme.typography.labelSmall,
          color = TextSecondaryDark,
          fontSize = 11.sp
        )
      }
    }

    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      // Streak Badge
      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(16.dp))
          .background(TitanGold.copy(alpha = 0.15f))
          .border(1.dp, TitanGold.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
          .padding(horizontal = 9.dp, vertical = 4.dp)
          .testTag("streak_badge")
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(text = "🔥", fontSize = 11.sp)
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "$streakDays-Day Streak",
            style = MaterialTheme.typography.labelSmall,
            color = TitanGold,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp
          )
        }
      }

      // Firestore Cloud Sync Pill
      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(16.dp))
          .background(
            if (syncState == FirestoreSyncState.SYNCED) TitanEmerald.copy(alpha = 0.15f)
            else SlateElevated
          )
          .border(
            1.dp,
            if (syncState == FirestoreSyncState.SYNCED) TitanEmerald.copy(alpha = 0.4f) else SlateBorder,
            RoundedCornerShape(16.dp)
          )
          .clickable(onClick = onForceSync)
          .padding(horizontal = 8.dp, vertical = 4.dp)
          .testTag("btn_sync_firestore_momentum")
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          if (syncState == FirestoreSyncState.SYNCING) {
            CircularProgressIndicator(
              modifier = Modifier.size(10.dp),
              color = TitanCyan,
              strokeWidth = 1.5.dp
            )
          } else {
            Icon(
              imageVector = if (syncState == FirestoreSyncState.SYNCED) Icons.Default.CloudDone else Icons.Default.CloudUpload,
              contentDescription = "Firestore Sync",
              tint = if (syncState == FirestoreSyncState.SYNCED) TitanEmerald else TextMutedDark,
              modifier = Modifier.size(12.dp)
            )
          }
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = if (syncState == FirestoreSyncState.SYNCED) "Firestore Live" else "Sync",
            style = MaterialTheme.typography.labelSmall,
            color = if (syncState == FirestoreSyncState.SYNCED) TitanEmerald else TextSecondaryDark,
            fontWeight = FontWeight.Medium,
            fontSize = 11.sp
          )
        }
      }
    }
  }
}

// ==============================================================================================
// 2. HERO PROGRESS BAR SECTION (VISUALIZES PROGRESS SAVED IN FIRESTORE)
// ==============================================================================================
@Composable
private fun HeroProgressBarSection(
  momentumData: CareerMomentumData,
  syncState: FirestoreSyncState
) {
  val animatedProgress by animateFloatAsState(
    targetValue = momentumData.progressFraction,
    animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
    label = "MomentumProgressAnim"
  )

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(12.dp))
      .background(SlateElevated.copy(alpha = 0.6f))
      .border(1.dp, SlateBorder.copy(alpha = 0.7f), RoundedCornerShape(12.dp))
      .padding(14.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Row(verticalAlignment = Alignment.Bottom) {
          Text(
            text = "${momentumData.progressPercentage}%",
            style = MaterialTheme.typography.headlineMedium,
            color = if (momentumData.progressPercentage >= 100) TitanEmerald else TitanCyan,
            fontWeight = FontWeight.ExtraBold,
            fontFamily = FontFamily.Monospace,
            fontSize = 28.sp
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "MOMENTUM",
            style = MaterialTheme.typography.labelMedium,
            color = TextSecondaryDark,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 4.dp),
            fontSize = 12.sp
          )
        }
        Text(
          text = "${momentumData.completedHabitsCount} of ${momentumData.totalHabitsCount} daily habits conquered",
          style = MaterialTheme.typography.bodySmall,
          color = TextSecondaryDark,
          fontSize = 12.sp
        )
      }

      // Tier Badge
      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(8.dp))
          .background(
            when (momentumData.momentumTier) {
              "SUPERCHARGED" -> TitanEmerald.copy(alpha = 0.2f)
              "HIGH MOMENTUM" -> TitanCyan.copy(alpha = 0.2f)
              "SOLID VELOCITY" -> TitanIndigo.copy(alpha = 0.2f)
              else -> SlateDarker
            }
          )
          .border(
            1.dp,
            when (momentumData.momentumTier) {
              "SUPERCHARGED" -> TitanEmerald
              "HIGH MOMENTUM" -> TitanCyan
              "SOLID VELOCITY" -> TitanIndigo
              else -> SlateBorder
            },
            RoundedCornerShape(8.dp)
          )
          .padding(horizontal = 10.dp, vertical = 5.dp)
      ) {
        Text(
          text = momentumData.momentumTier,
          style = MaterialTheme.typography.labelSmall,
          color = when (momentumData.momentumTier) {
            "SUPERCHARGED" -> TitanEmerald
            "HIGH MOMENTUM" -> TitanCyan
            "SOLID VELOCITY" -> TitanIndigo
            else -> TextSecondaryDark
          },
          fontWeight = FontWeight.Bold,
          fontSize = 11.sp
        )
      }
    }

    Spacer(modifier = Modifier.height(12.dp))

    // DYNAMIC GRADIENT PROGRESS BAR
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(14.dp)
        .clip(RoundedCornerShape(7.dp))
        .background(SlateDarker)
        .border(1.dp, SlateBorder.copy(alpha = 0.5f), RoundedCornerShape(7.dp))
        .testTag("momentum_progress_bar")
    ) {
      // Animated Progress Fill with Gradient
      Box(
        modifier = Modifier
          .fillMaxHeight()
          .fillMaxWidth(animatedProgress)
          .clip(RoundedCornerShape(7.dp))
          .background(
            Brush.horizontalGradient(
              colors = listOf(
                TitanCyan,
                if (momentumData.progressPercentage >= 100) TitanEmerald else TitanCyanMuted(),
                if (momentumData.progressPercentage >= 100) TitanGold else TitanEmerald
              )
            )
          )
      )

      // Milestone Markers (25%, 50%, 75%)
      Row(
        modifier = Modifier
          .fillMaxSize()
          .padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        listOf(0.25f, 0.5f, 0.75f).forEach { mark ->
          Box(
            modifier = Modifier
              .size(4.dp)
              .clip(CircleShape)
              .background(if (animatedProgress >= mark) Color.White.copy(alpha = 0.9f) else SlateBorder)
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(8.dp))

    // Firestore Sync Subtext Banner
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.AutoAwesome,
          contentDescription = null,
          tint = TitanGold,
          modifier = Modifier.size(11.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
          text = momentumData.momentumSummary,
          style = MaterialTheme.typography.labelSmall,
          color = TextPrimaryDark.copy(alpha = 0.85f),
          fontSize = 11.sp,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
      }

      Text(
        text = "users/career_momentum/today",
        style = MaterialTheme.typography.labelSmall,
        color = TextMutedDark,
        fontSize = 10.sp,
        fontFamily = FontFamily.Monospace
      )
    }
  }
}

// ==============================================================================================
// 3. HABITS LIST SECTION
// ==============================================================================================
@Composable
private fun HabitsListSection(
  habits: List<DailyHabitItem>,
  onToggleCompletion: (String) -> Unit,
  onIncrementCount: (String) -> Unit,
  onDecrementCount: (String) -> Unit,
  onDeleteHabit: (String) -> Unit
) {
  Column(
    modifier = Modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    habits.forEach { habit ->
      HabitRowItem(
        habit = habit,
        onToggleCompletion = { onToggleCompletion(habit.id) },
        onIncrement = { onIncrementCount(habit.id) },
        onDecrement = { onDecrementCount(habit.id) },
        onDelete = { onDeleteHabit(habit.id) }
      )
    }
  }
}

@Composable
private fun HabitRowItem(
  habit: DailyHabitItem,
  onToggleCompletion: () -> Unit,
  onIncrement: () -> Unit,
  onDecrement: () -> Unit,
  onDelete: () -> Unit
) {
  val haptic = LocalHapticFeedback.current
  val isCompleted = habit.isFullyCompleted

  val categoryColor = when (habit.category) {
    "NETWORKING" -> TitanCyan
    "SKILLS" -> TitanEmerald
    "APPLICATIONS" -> TitanIndigo
    "RESEARCH" -> TitanGold
    else -> TitanViolet
  }

  val categoryIcon = when (habit.category) {
    "NETWORKING" -> Icons.Default.People
    "SKILLS" -> Icons.Default.Code
    "APPLICATIONS" -> Icons.AutoMirrored.Filled.Send
    "RESEARCH" -> Icons.Default.Lightbulb
    else -> Icons.Default.Bolt
  }

  Surface(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(10.dp))
      .border(
        1.dp,
        if (isCompleted) TitanEmerald.copy(alpha = 0.5f) else SlateBorder,
        RoundedCornerShape(10.dp)
      )
      .testTag("habit_item_${habit.id}"),
    color = if (isCompleted) SlateElevated.copy(alpha = 0.7f) else SlateDarker
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      // Left: Checkbox / Completion Toggle Circle
      Row(
        modifier = Modifier.weight(1f),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .size(26.dp)
            .clip(CircleShape)
            .background(if (isCompleted) TitanEmerald else SlateCard)
            .border(
              1.5.dp,
              if (isCompleted) TitanEmerald else SlateBorder,
              CircleShape
            )
            .clickable(onClick = {
              haptic.performHapticFeedback(HapticFeedbackType.LongPress)
              onToggleCompletion()
            })
            .testTag("habit_check_${habit.id}"),
          contentAlignment = Alignment.Center
        ) {
          if (isCompleted) {
            Icon(
              imageVector = Icons.Default.Check,
              contentDescription = "Completed",
              tint = SlateDarker,
              modifier = Modifier.size(16.dp)
            )
          }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Habit details
        Column(modifier = Modifier.weight(1f)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(categoryColor.copy(alpha = 0.15f))
                .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
              Text(
                text = habit.category,
                style = MaterialTheme.typography.labelSmall,
                color = categoryColor,
                fontWeight = FontWeight.Bold,
                fontSize = 9.sp
              )
            }
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = habit.title,
              style = MaterialTheme.typography.bodyMedium,
              color = if (isCompleted) TextPrimaryDark else TextPrimaryDark.copy(alpha = 0.9f),
              fontWeight = if (isCompleted) FontWeight.Bold else FontWeight.SemiBold,
              fontSize = 13.sp,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
          }
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = habit.description,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondaryDark,
            fontSize = 11.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
          )
        }
      }

      Spacer(modifier = Modifier.width(8.dp))

      // Right: Target Counter Stepper (e.g. 1/2) & Increment/Decrement buttons
      Row(verticalAlignment = Alignment.CenterVertically) {
        // Decrement button
        Box(
          modifier = Modifier
            .size(24.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(SlateCard)
            .border(1.dp, SlateBorder, RoundedCornerShape(6.dp))
            .clickable(enabled = habit.currentCount > 0, onClick = {
              haptic.performHapticFeedback(HapticFeedbackType.LongPress)
              onDecrement()
            }),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Remove,
            contentDescription = "Decrement",
            tint = if (habit.currentCount > 0) TextPrimaryDark else TextMutedDark,
            modifier = Modifier.size(12.dp)
          )
        }

        Spacer(modifier = Modifier.width(6.dp))

        Text(
          text = "${habit.currentCount}/${habit.targetCount}",
          style = MaterialTheme.typography.labelSmall,
          color = if (isCompleted) TitanEmerald else TitanCyan,
          fontWeight = FontWeight.Bold,
          fontFamily = FontFamily.Monospace,
          fontSize = 12.sp
        )

        Spacer(modifier = Modifier.width(6.dp))

        // Increment button
        Box(
          modifier = Modifier
            .size(24.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(TitanCyan.copy(alpha = 0.15f))
            .border(1.dp, TitanCyan.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
            .clickable(onClick = {
              haptic.performHapticFeedback(HapticFeedbackType.LongPress)
              onIncrement()
            })
            .testTag("btn_increment_${habit.id}"),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Increment",
            tint = TitanCyan,
            modifier = Modifier.size(14.dp)
          )
        }

        // Delete button for custom habits or general management
        if (habit.category == "CUSTOM" || habit.id.startsWith("custom_")) {
          Spacer(modifier = Modifier.width(4.dp))
          IconButton(
            onClick = onDelete,
            modifier = Modifier.size(24.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Delete,
              contentDescription = "Delete Habit",
              tint = TitanRose.copy(alpha = 0.7f),
              modifier = Modifier.size(14.dp)
            )
          }
        }
      }
    }
  }
}

// ==============================================================================================
// 4. ACTION CONTROLS ROW
// ==============================================================================================
@Composable
private fun ActionControlsRow(
  onOpenAddDialog: () -> Unit,
  onResetDay: () -> Unit,
  onSyncNow: () -> Unit,
  isSyncing: Boolean
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    // Add Custom Habit Button
    Button(
      onClick = onOpenAddDialog,
      colors = ButtonDefaults.buttonColors(containerColor = SlateElevated),
      border = androidx.compose.foundation.BorderStroke(1.dp, TitanCyan.copy(alpha = 0.4f)),
      shape = RoundedCornerShape(8.dp),
      modifier = Modifier.testTag("btn_add_habit")
    ) {
      Icon(
        imageVector = Icons.Default.Add,
        contentDescription = null,
        tint = TitanCyan,
        modifier = Modifier.size(16.dp)
      )
      Spacer(modifier = Modifier.width(6.dp))
      Text(
        text = "Add Habit",
        style = MaterialTheme.typography.labelMedium,
        color = TitanCyan,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp
      )
    }

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      // Reset Day Button
      OutlinedButton(
        onClick = onResetDay,
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondaryDark),
        modifier = Modifier.testTag("btn_reset_momentum_day")
      ) {
        Icon(
          imageVector = Icons.Default.Refresh,
          contentDescription = null,
          tint = TextSecondaryDark,
          modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
          text = "Reset",
          style = MaterialTheme.typography.labelSmall,
          fontSize = 11.sp
        )
      }

      // Sync with Firestore Button
      Button(
        onClick = onSyncNow,
        enabled = !isSyncing,
        colors = ButtonDefaults.buttonColors(containerColor = TitanCyan),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.testTag("btn_push_firestore_momentum")
      ) {
        if (isSyncing) {
          CircularProgressIndicator(
            modifier = Modifier.size(14.dp),
            color = SlateDarker,
            strokeWidth = 2.dp
          )
        } else {
          Icon(
            imageVector = Icons.Default.CloudUpload,
            contentDescription = null,
            tint = SlateDarker,
            modifier = Modifier.size(14.dp)
          )
        }
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "Firestore Sync",
          style = MaterialTheme.typography.labelMedium,
          color = SlateDarker,
          fontWeight = FontWeight.Bold,
          fontSize = 12.sp
        )
      }
    }
  }
}

// ==============================================================================================
// 5. ADD CUSTOM HABIT DIALOG
// ==============================================================================================
@Composable
private fun AddCustomHabitDialog(
  onDismiss: () -> Unit,
  onAddHabit: (title: String, description: String, category: String, targetCount: Int) -> Unit
) {
  var title by remember { mutableStateOf("") }
  var description by remember { mutableStateOf("") }
  var selectedCategory by remember { mutableStateOf("NETWORKING") }
  var targetCount by remember { mutableIntStateOf(1) }

  val categories = listOf(
    "NETWORKING" to "Networking Outreach",
    "SKILLS" to "Skill Building",
    "APPLICATIONS" to "Job Pipeline",
    "RESEARCH" to "Market Intel",
    "CUSTOM" to "Custom Ritual"
  )

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.Bolt, contentDescription = null, tint = TitanCyan)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "Track New Career Habit",
          style = MaterialTheme.typography.titleMedium,
          color = TextPrimaryDark,
          fontWeight = FontWeight.Bold
        )
      }
    },
    text = {
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        Text(
          text = "Add a high-leverage daily activity to track and visualize on your Firestore Career Momentum progress bar.",
          style = MaterialTheme.typography.bodySmall,
          color = TextSecondaryDark,
          fontSize = 12.sp
        )

        // Title Input
        OutlinedTextField(
          value = title,
          onValueChange = { title = it },
          label = { Text("Habit Name") },
          placeholder = { Text("e.g. Outreach to 2 Engineering Directors") },
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("input_habit_title"),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = TitanCyan,
            unfocusedBorderColor = SlateBorder,
            focusedTextColor = TextPrimaryDark,
            unfocusedTextColor = TextPrimaryDark
          )
        )

        // Description Input
        OutlinedTextField(
          value = description,
          onValueChange = { description = it },
          label = { Text("Context / Execution Rule") },
          placeholder = { Text("e.g. Pitch strategic case study via LinkedIn") },
          maxLines = 2,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("input_habit_desc"),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = TitanCyan,
            unfocusedBorderColor = SlateBorder,
            focusedTextColor = TextPrimaryDark,
            unfocusedTextColor = TextPrimaryDark
          )
        )

        // Category Selector Chips
        Text(
          text = "Category",
          style = MaterialTheme.typography.labelSmall,
          color = TextSecondaryDark,
          fontWeight = FontWeight.Bold
        )
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          categories.take(3).forEach { (catKey, catName) ->
            val isSelected = selectedCategory == catKey
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(if (isSelected) TitanCyan.copy(alpha = 0.2f) else SlateDarker)
                .border(1.dp, if (isSelected) TitanCyan else SlateBorder, RoundedCornerShape(16.dp))
                .clickable { selectedCategory = catKey }
                .padding(horizontal = 8.dp, vertical = 5.dp)
            ) {
              Text(
                text = catKey,
                style = MaterialTheme.typography.labelSmall,
                color = if (isSelected) TitanCyan else TextSecondaryDark,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                fontSize = 10.sp
              )
            }
          }
        }

        // Target Count Selector
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Daily Target Count:",
            style = MaterialTheme.typography.bodySmall,
            color = TextPrimaryDark
          )
          Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
              onClick = { if (targetCount > 1) targetCount-- },
              enabled = targetCount > 1
            ) {
              Icon(Icons.Default.Remove, contentDescription = "Decrease", tint = TitanCyan)
            }
            Text(
              text = "$targetCount",
              style = MaterialTheme.typography.titleMedium,
              color = TitanCyan,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(horizontal = 8.dp)
            )
            IconButton(
              onClick = { if (targetCount < 20) targetCount++ }
            ) {
              Icon(Icons.Default.Add, contentDescription = "Increase", tint = TitanCyan)
            }
          }
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (title.isNotBlank()) {
            onAddHabit(
              title.trim(),
              description.trim().ifBlank { "Daily momentum lever for career progression" },
              selectedCategory,
              targetCount
            )
          }
        },
        enabled = title.isNotBlank(),
        colors = ButtonDefaults.buttonColors(containerColor = TitanCyan),
        modifier = Modifier.testTag("btn_confirm_add_habit")
      ) {
        Text("Add & Sync", color = SlateDarker, fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancel", color = TextSecondaryDark)
      }
    },
    containerColor = SlateCard
  )
}

// Color helper
private fun TitanCyanMuted(): Color = Color(0xFF0891B2)
