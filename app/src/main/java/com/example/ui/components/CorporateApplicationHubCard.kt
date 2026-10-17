package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CorporateFare
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.example.data.model.CorporateApplicationTarget
import com.example.ui.theme.ObsidianDark
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateCard
import com.example.ui.theme.SlateDarker
import com.example.ui.theme.SlateElevated
import com.example.ui.theme.TextMutedDark
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import com.example.ui.theme.TitanCrimson
import com.example.ui.theme.TitanCyan
import com.example.ui.theme.TitanEmerald
import com.example.ui.theme.TitanGold
import com.example.ui.theme.TitanIndigo
import com.example.ui.viewmodel.TitanViewModel
import java.util.UUID

/**
 * 1-Click Apply Corporate Hub Component.
 * Displays high-priority corporate targets persisted in Room SQLite,
 * provides 1-click application dispatch, and streams engagement telemetry to Firebase Analytics.
 */
@Composable
fun CorporateApplicationHubCard(
  viewModel: TitanViewModel,
  modifier: Modifier = Modifier,
  initialExpanded: Boolean = true
) {
  val corporateTargets by viewModel.corporateApplicationTargets.collectAsState()
  val starredTargets by viewModel.starredCorporateApplicationTargets.collectAsState()

  var isExpanded by remember { mutableStateOf(initialExpanded) }
  var selectedTierFilter by remember { mutableStateOf("ALL") } // ALL, TIER_1_APEX, TIER_2_STRATEGIC
  var showAddTargetDialog by remember { mutableStateOf(false) }

  val filteredTargets = remember(corporateTargets, selectedTierFilter) {
    if (selectedTierFilter == "ALL") {
      corporateTargets
    } else {
      corporateTargets.filter { it.priorityTier == selectedTierFilter }
    }
  }

  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("corporate_application_hub_card")
      .animateContentSize(),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = SlateCard),
    border = BorderStroke(1.2.dp, TitanCyan.copy(alpha = 0.5f))
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp)
    ) {
      // Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Box(
            modifier = Modifier
              .size(36.dp)
              .clip(RoundedCornerShape(8.dp))
              .background(TitanCyan.copy(alpha = 0.15f))
              .border(1.dp, TitanCyan.copy(alpha = 0.4f), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.CorporateFare,
              contentDescription = "Corporate Hub",
              tint = TitanCyan,
              modifier = Modifier.size(20.dp)
            )
          }

          Column {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Text(
                text = "1-CLICK APPLY CORPORATE HUB",
                style = MaterialTheme.typography.labelSmall,
                color = TitanCyan,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
              )
              Text(
                text = "(${corporateTargets.size})",
                style = MaterialTheme.typography.labelSmall,
                color = TextMutedDark,
                fontWeight = FontWeight.Bold
              )
            }

            Text(
              text = "Room SQLite Targets & Automated ATS Dispatch",
              style = MaterialTheme.typography.titleMedium,
              color = TextPrimaryDark,
              fontWeight = FontWeight.Bold
            )
          }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          IconButton(
            onClick = {
              viewModel.analyticsService.trackCorporateHubViewed(
                targetsCount = corporateTargets.size,
                readyMatchesCount = 0,
                starredCount = starredTargets.size
              )
              isExpanded = !isExpanded
            },
            modifier = Modifier.testTag("toggle_corporate_hub_expand")
          ) {
            Icon(
              imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
              contentDescription = if (isExpanded) "Collapse" else "Expand",
              tint = TextSecondaryDark
            )
          }
        }
      }

      // Room Offline Sync Indicator
      Spacer(modifier = Modifier.height(6.dp))
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(6.dp))
          .background(ObsidianDark)
          .border(0.8.dp, TitanEmerald.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
          .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          Box(
            modifier = Modifier
              .size(6.dp)
              .clip(CircleShape)
              .background(TitanEmerald)
          )
          Text(
            text = "ROOM SQLITE OFFLINE READY: ${corporateTargets.size} Targets Cached",
            style = MaterialTheme.typography.labelSmall,
            color = TitanEmerald,
            fontWeight = FontWeight.Bold,
            fontSize = 9.sp
          )
        }
        Text(
          text = "Zero Internet Required",
          style = MaterialTheme.typography.labelSmall,
          color = TextSecondaryDark,
          fontSize = 8.5.sp
        )
      }

      // Tier Filter Chips & Add Button
      Spacer(modifier = Modifier.height(10.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          modifier = Modifier.horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          listOf("ALL", "TIER_1_APEX", "TIER_2_STRATEGIC").forEach { tier ->
            val isSelected = selectedTierFilter == tier
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(if (isSelected) TitanCyan else SlateElevated)
                .border(1.dp, if (isSelected) TitanCyan else SlateBorder, RoundedCornerShape(6.dp))
                .clickable { selectedTierFilter = tier }
                .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
              Text(
                text = when (tier) {
                  "ALL" -> "All Targets (${corporateTargets.size})"
                  "TIER_1_APEX" -> "Tier-1 Apex"
                  else -> "Tier-2 Strategic"
                },
                style = MaterialTheme.typography.labelSmall,
                color = if (isSelected) ObsidianDark else TextSecondaryDark,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp
              )
            }
          }
        }

        IconButton(
          onClick = { showAddTargetDialog = true },
          modifier = Modifier
            .size(32.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(TitanCyan.copy(alpha = 0.15f))
            .testTag("add_corporate_target_button")
        ) {
          Icon(Icons.Default.Add, contentDescription = "Add Corporate Target", tint = TitanCyan, modifier = Modifier.size(18.dp))
        }
      }

      // Target List
      AnimatedVisibility(visible = isExpanded) {
        Column(modifier = Modifier.padding(top = 10.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
          if (filteredTargets.isEmpty()) {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(SlateElevated)
                .padding(16.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = "No corporate targets found in this tier. Tap + to add one.",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondaryDark
              )
            }
          } else {
            filteredTargets.forEach { target ->
              CorporateTargetItemRow(
                target = target,
                onOneClickDispatch = {
                  viewModel.updateCorporateTargetStatus(target.id, "APPLIED")
                  viewModel.analyticsService.trackOneClickApplyDispatched(
                    companyName = target.companyName,
                    jobTitle = target.targetRoleTitle,
                    matchScore = target.alignmentScore,
                    ctcLakhs = target.targetCtcLakhs
                  )
                },
                onToggleStarred = {
                  viewModel.toggleCorporateTargetStarred(target.id, !target.isStarred)
                },
                onOpenDirective = { code ->
                  viewModel.triggerKeypadDirective(code)
                },
                onLaunchInterviewCommand = {
                  viewModel.selectInterviewCommandTarget(target.companyName, target.targetRoleTitle, autoGenerate = true)
                }
              )
            }
          }
        }
      }
    }
  }

  // Dialog to Add a Corporate Target
  if (showAddTargetDialog) {
    AddCorporateTargetDialog(
      onDismiss = { showAddTargetDialog = false },
      onSave = { newTarget ->
        viewModel.saveCorporateApplicationTarget(newTarget)
        showAddTargetDialog = false
      }
    )
  }
}

@Composable
private fun CorporateTargetItemRow(
  target: CorporateApplicationTarget,
  onOneClickDispatch: () -> Unit,
  onToggleStarred: () -> Unit,
  onOpenDirective: (String) -> Unit,
  onLaunchInterviewCommand: () -> Unit = {}
) {
  Surface(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("corporate_target_item_${target.id}"),
    shape = RoundedCornerShape(10.dp),
    color = ObsidianDark,
    border = BorderStroke(1.dp, if (target.isStarred) TitanGold.copy(alpha = 0.5f) else SlateBorder)
  ) {
    Column(modifier = Modifier.padding(12.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          IconButton(
            onClick = onToggleStarred,
            modifier = Modifier.size(24.dp)
          ) {
            Icon(
              imageVector = if (target.isStarred) Icons.Default.Star else Icons.Default.StarBorder,
              contentDescription = if (target.isStarred) "Starred" else "Unstarred",
              tint = if (target.isStarred) TitanGold else TextMutedDark,
              modifier = Modifier.size(18.dp)
            )
          }

          Column {
            Text(
              text = target.companyName,
              style = MaterialTheme.typography.titleMedium,
              color = TextPrimaryDark,
              fontWeight = FontWeight.Bold,
              fontSize = 14.sp
            )
            Text(
              text = target.targetRoleTitle,
              style = MaterialTheme.typography.bodySmall,
              color = TextSecondaryDark,
              fontSize = 11.sp
            )
          }
        }

        // Compensation Pill & Status
        Column(horizontalAlignment = Alignment.End) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(4.dp))
              .background(TitanEmerald.copy(alpha = 0.15f))
              .padding(horizontal = 6.dp, vertical = 2.dp)
          ) {
            Text(
              text = "₹${target.targetCtcLakhs.toInt()}L CTC",
              style = MaterialTheme.typography.labelSmall,
              color = TitanEmerald,
              fontWeight = FontWeight.Black,
              fontSize = 9.sp
            )
          }

          Spacer(modifier = Modifier.height(3.dp))

          val statusBadgeColor = when (target.applicationStatus) {
            "OFFER", "OFFER_EXTENDED" -> TitanEmerald
            "INTERVIEWING", "ROUND_1_CLEARED", "WAR_ROOM_READY" -> TitanGold
            "APPLIED" -> TitanCyan
            else -> TitanIndigo
          }
          val statusBadgeText = when (target.applicationStatus) {
            "OFFER", "OFFER_EXTENDED" -> "🏆 Offer"
            "INTERVIEWING" -> "🎙️ Interviewing"
            "WAR_ROOM_READY" -> "⚡ War Room"
            "ROUND_1_CLEARED" -> "🎙️ Round 1 Cleared"
            "APPLIED" -> "✓ Applied"
            "TARGETED" -> "🎯 Targeted"
            else -> target.applicationStatus
          }

          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(4.dp))
              .background(statusBadgeColor.copy(alpha = 0.16f))
              .border(0.8.dp, statusBadgeColor.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
              .padding(horizontal = 6.dp, vertical = 2.dp)
              .testTag("status_summary_badge_${target.id}")
          ) {
            Text(
              text = statusBadgeText,
              style = MaterialTheme.typography.labelSmall,
              color = statusBadgeColor,
              fontWeight = FontWeight.Bold,
              fontSize = 8.5.sp
            )
          }
        }
      }

      // Executive Hook / Strategic Angle
      if (target.executiveHook.isNotBlank()) {
        Spacer(modifier = Modifier.height(6.dp))
        Text(
          text = "🎯 ${target.executiveHook}",
          style = MaterialTheme.typography.bodySmall,
          color = TextMutedDark,
          fontSize = 10.sp,
          maxLines = 2
        )
      }

      // Actions Row
      Spacer(modifier = Modifier.height(8.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          // Link to #07 HUD or #18 Dark store directive
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(4.dp))
              .background(SlateDarker)
              .border(0.8.dp, TitanGold.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
              .clickable { onOpenDirective("07") }
              .padding(horizontal = 6.dp, vertical = 3.dp)
          ) {
            Text(
              text = "⚡ Keypad #07",
              style = MaterialTheme.typography.labelSmall,
              color = TitanGold,
              fontSize = 9.sp
            )
          }

          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(4.dp))
              .background(SlateDarker)
              .border(0.8.dp, TitanCyan.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
              .clickable { onOpenDirective("18") }
              .padding(horizontal = 6.dp, vertical = 3.dp)
          ) {
            Text(
              text = "⚡ Keypad #18",
              style = MaterialTheme.typography.labelSmall,
              color = TitanCyan,
              fontSize = 9.sp
            )
          }

          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(4.dp))
              .background(TitanGold.copy(alpha = 0.12f))
              .border(0.8.dp, TitanGold.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
              .clickable { onLaunchInterviewCommand() }
              .padding(horizontal = 6.dp, vertical = 3.dp)
              .testTag("interview_command_target_${target.id}")
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Psychology, contentDescription = null, tint = TitanGold, modifier = Modifier.size(11.dp))
              Spacer(modifier = Modifier.width(3.dp))
              Text(
                text = "Interview Command",
                style = MaterialTheme.typography.labelSmall,
                color = TitanGold,
                fontWeight = FontWeight.Bold,
                fontSize = 9.sp
              )
            }
          }
        }

        if (target.applicationStatus != "APPLIED") {
          Button(
            onClick = onOneClickDispatch,
            shape = RoundedCornerShape(6.dp),
            colors = ButtonDefaults.buttonColors(containerColor = TitanCyan),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
            modifier = Modifier.testTag("one_click_dispatch_${target.id}")
          ) {
            Icon(Icons.Default.Bolt, contentDescription = null, tint = ObsidianDark, modifier = Modifier.size(12.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "1-Click Dispatch",
              color = ObsidianDark,
              fontWeight = FontWeight.Black,
              fontSize = 10.sp
            )
          }
        } else {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = TitanEmerald, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "Dispatched",
              color = TitanEmerald,
              fontWeight = FontWeight.Bold,
              fontSize = 10.sp
            )
          }
        }
      }
    }
  }
}

@Composable
private fun AddCorporateTargetDialog(
  onDismiss: () -> Unit,
  onSave: (CorporateApplicationTarget) -> Unit
) {
  var companyName by remember { mutableStateOf("") }
  var roleTitle by remember { mutableStateOf("") }
  var ctcText by remember { mutableStateOf("28.0") }
  var executiveHook by remember { mutableStateOf("") }

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(12.dp),
      color = SlateCard,
      border = BorderStroke(1.dp, TitanCyan)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Text(
          text = "Add Corporate Application Target",
          style = MaterialTheme.typography.titleMedium,
          color = TextPrimaryDark,
          fontWeight = FontWeight.Bold
        )

        OutlinedTextField(
          value = companyName,
          onValueChange = { companyName = it },
          label = { Text("Company Name (e.g. Zepto, Swiggy)") },
          modifier = Modifier.fillMaxWidth(),
          singleLine = true
        )

        OutlinedTextField(
          value = roleTitle,
          onValueChange = { roleTitle = it },
          label = { Text("Target Role Title") },
          modifier = Modifier.fillMaxWidth(),
          singleLine = true
        )

        OutlinedTextField(
          value = ctcText,
          onValueChange = { ctcText = it },
          label = { Text("Target CTC (Lakhs INR)") },
          modifier = Modifier.fillMaxWidth(),
          singleLine = true
        )

        OutlinedTextField(
          value = executiveHook,
          onValueChange = { executiveHook = it },
          label = { Text("Executive Pitch / Strategy Hook") },
          modifier = Modifier.fillMaxWidth(),
          maxLines = 3
        )

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.End,
          verticalAlignment = Alignment.CenterVertically
        ) {
          TextButton(onClick = onDismiss) {
            Text("Cancel", color = TextSecondaryDark)
          }
          Spacer(modifier = Modifier.width(8.dp))
          Button(
            onClick = {
              if (companyName.isNotBlank() && roleTitle.isNotBlank()) {
                val target = CorporateApplicationTarget(
                  id = "target_${UUID.randomUUID().toString().take(8)}",
                  companyName = companyName.trim(),
                  targetRoleTitle = roleTitle.trim(),
                  priorityTier = "TIER_1_APEX",
                  targetCtcLakhs = ctcText.toDoubleOrNull() ?: 28.0,
                  applicationStatus = "WAR_ROOM_READY",
                  executiveHook = executiveHook.trim(),
                  alignmentScore = 95,
                  isStarred = true
                )
                onSave(target)
              }
            },
            colors = ButtonDefaults.buttonColors(containerColor = TitanCyan)
          ) {
            Text("Save Target", color = ObsidianDark, fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}
