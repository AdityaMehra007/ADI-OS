package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
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
import com.example.ui.viewmodel.TitanViewModel
import com.example.util.KeypadDirective
import com.example.util.KeypadDirectiveRegistry

/**
 * Floating HUD banner that appears immediately when the user begins typing a 25-Keypad shortcut.
 * E.g., displays "[ 0_ ] Type second digit (01-25) or Enter" with real-time feedback.
 */
@Composable
fun KeypadShortcutHudOverlay(
  viewModel: TitanViewModel,
  modifier: Modifier = Modifier
) {
  val buffer by viewModel.keypadShortcutBuffer.collectAsState()

  AnimatedVisibility(
    visible = buffer.isNotEmpty(),
    enter = fadeIn() + slideInVertically { it / 2 },
    exit = fadeOut() + slideOutVertically { it / 2 },
    modifier = modifier
  ) {
    Surface(
      shape = RoundedCornerShape(24.dp),
      color = SlateElevated,
      border = androidx.compose.foundation.BorderStroke(1.5.dp, TitanGold),
      shadowElevation = 12.dp,
      modifier = Modifier
        .padding(16.dp)
        .testTag("keypad_shortcut_hud")
    ) {
      Row(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Box(
          modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(TitanGold.copy(alpha = 0.2f)),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Keyboard,
            contentDescription = "Keypad Active",
            tint = TitanGold,
            modifier = Modifier.size(18.dp)
          )
        }

        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = "KEYPAD DIRECTIVE: ",
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.Bold,
              color = TitanGold,
              fontSize = 10.sp
            )
            Text(
              text = "[$buffer _]",
              style = MaterialTheme.typography.labelMedium,
              fontWeight = FontWeight.Black,
              fontFamily = FontFamily.Monospace,
              color = TextPrimaryDark,
              fontSize = 13.sp
            )
          }
          Text(
            text = "Type 01..25, press Enter to trigger, or Esc to cancel",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondaryDark,
            fontSize = 10.sp
          )
        }

        Spacer(modifier = Modifier.width(4.dp))

        IconButton(
          onClick = { viewModel.setKeypadShortcutBuffer("") },
          modifier = Modifier.size(24.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Dismiss Keypad Buffer",
            tint = TextMutedDark,
            modifier = Modifier.size(14.dp)
          )
        }
      }
    }
  }
}

/**
 * Global Cybernetic 25-Keypad Directives Shortcut Cheat Sheet Dialog.
 * Triggered via keyboard shortcut ('K' / '?') or top bar action.
 */
@Composable
fun KeypadDirectiveCheatSheetDialog(
  viewModel: TitanViewModel,
  onDismissRequest: () -> Unit
) {
  var searchQuery by remember { mutableStateOf("") }
  var selectedCategory by remember { mutableStateOf("ALL") }

  val allDirectives = remember { KeypadDirectiveRegistry.DIRECTIVES }
  val categories = remember {
    listOf("ALL") + allDirectives.map { it.category }.distinct()
  }

  val filteredDirectives = remember(searchQuery, selectedCategory) {
    allDirectives.filter { item ->
      val matchesCategory = selectedCategory == "ALL" || item.category == selectedCategory
      val matchesSearch = searchQuery.isBlank() ||
        item.code.contains(searchQuery, ignoreCase = true) ||
        item.title.contains(searchQuery, ignoreCase = true) ||
        item.summary.contains(searchQuery, ignoreCase = true) ||
        item.id.toString() == searchQuery.trim()
      matchesCategory && matchesSearch
    }
  }

  Dialog(
    onDismissRequest = onDismissRequest,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = Modifier
        .fillMaxWidth(0.95f)
        .fillMaxHeight(0.92f)
        .testTag("keypad_directive_cheat_sheet_dialog"),
      shape = RoundedCornerShape(16.dp),
      color = ObsidianDark,
      border = androidx.compose.foundation.BorderStroke(1.5.dp, TitanGold.copy(alpha = 0.6f))
    ) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(16.dp)
      ) {
        // Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Box(
              modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(TitanGold.copy(alpha = 0.2f))
                .border(1.dp, TitanGold, RoundedCornerShape(8.dp)),
              contentAlignment = Alignment.Center
            ) {
              Text("⌨️", fontSize = 18.sp)
            }
            Column {
              Text(
                text = "⚡ 25-KEYPAD GLOBAL SHORTCUTS",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black,
                color = TitanGold
              )
              Text(
                text = "Type '01', '02', ..., '25' anywhere to execute instantly",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondaryDark,
                fontSize = 11.sp
              )
            }
          }

          IconButton(
            onClick = onDismissRequest,
            modifier = Modifier.testTag("close_keypad_dialog_button")
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Close",
              tint = TextSecondaryDark
            )
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Search bar
        OutlinedTextField(
          value = searchQuery,
          onValueChange = { searchQuery = it },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("keypad_search_input"),
          placeholder = {
            Text("Search directive by code (01..25) or keyword...", color = TextMutedDark, fontSize = 12.sp)
          },
          leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = "Search", tint = TitanGold)
          },
          singleLine = true,
          shape = RoundedCornerShape(10.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = TitanGold,
            unfocusedBorderColor = SlateBorder,
            focusedContainerColor = SlateCard,
            unfocusedContainerColor = SlateCard,
            focusedTextColor = TextPrimaryDark,
            unfocusedTextColor = TextPrimaryDark
          )
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Category filter chips
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          categories.forEach { cat ->
            val isSelected = selectedCategory == cat
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(if (isSelected) TitanGold else SlateElevated)
                .border(1.dp, if (isSelected) TitanGold else SlateBorder, RoundedCornerShape(6.dp))
                .clickable { selectedCategory = cat }
                .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
              Text(
                text = cat,
                style = MaterialTheme.typography.labelSmall,
                color = if (isSelected) ObsidianDark else TextSecondaryDark,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // List of 25 Directives
        LazyColumn(
          modifier = Modifier
            .weight(1f)
            .fillMaxWidth(),
          contentPadding = PaddingValues(bottom = 12.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          items(filteredDirectives, key = { it.id }) { directive ->
            KeypadDirectiveItemCard(
              directive = directive,
              onTrigger = {
                viewModel.triggerKeypadDirective(directive)
                onDismissRequest()
              }
            )
          }
        }
      }
    }
  }
}

@Composable
private fun KeypadDirectiveItemCard(
  directive: KeypadDirective,
  onTrigger: () -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onTrigger() }
      .testTag("keypad_item_${directive.code}"),
    shape = RoundedCornerShape(10.dp),
    colors = CardDefaults.cardColors(containerColor = SlateCard),
    border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(10.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(
        modifier = Modifier.weight(1f),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        // Keyboard Key Badge
        Box(
          modifier = Modifier
            .size(38.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(SlateElevated)
            .border(1.5.dp, TitanGold, RoundedCornerShape(8.dp)),
          contentAlignment = Alignment.Center
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
              text = directive.code,
              color = TitanGold,
              fontWeight = FontWeight.Black,
              fontFamily = FontFamily.Monospace,
              fontSize = 14.sp
            )
          }
        }

        Column {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Text(
              text = "${directive.iconEmoji} ${directive.title}",
              style = MaterialTheme.typography.bodyMedium,
              fontWeight = FontWeight.Bold,
              color = TextPrimaryDark,
              fontSize = 13.sp
            )
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(
                  when (directive.category) {
                    "STRATEGY" -> TitanGold.copy(alpha = 0.15f)
                    "RESUME" -> TitanCyan.copy(alpha = 0.15f)
                    "INTERVIEW" -> TitanEmerald.copy(alpha = 0.15f)
                    "OFFER" -> TitanIndigo.copy(alpha = 0.15f)
                    else -> SlateElevated
                  }
                )
                .padding(horizontal = 5.dp, vertical = 2.dp)
            ) {
              Text(
                text = directive.category,
                style = MaterialTheme.typography.labelSmall,
                color = when (directive.category) {
                  "STRATEGY" -> TitanGold
                  "RESUME" -> TitanCyan
                  "INTERVIEW" -> TitanEmerald
                  "OFFER" -> TitanIndigo
                  else -> TextMutedDark
                },
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }
          Text(
            text = directive.summary,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondaryDark,
            fontSize = 11.sp,
            lineHeight = 14.sp
          )
        }
      }

      Spacer(modifier = Modifier.width(8.dp))

      // Trigger button
      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(6.dp))
          .background(TitanGold)
          .clickable { onTrigger() }
          .padding(horizontal = 10.dp, vertical = 6.dp)
      ) {
        Text(
          text = "⚡ EXECUTE",
          color = ObsidianDark,
          fontWeight = FontWeight.Black,
          fontSize = 10.sp
        )
      }
    }
  }
}
