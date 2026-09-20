package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ExperienceBulletRewrite
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

/**
 * Section that displays specific, actionable bullet points suggested by the AI
 * to rewrite experience descriptions for better alignment with the targeted role.
 */
@Composable
fun ExperienceBulletRewritesSection(
  rewrites: List<ExperienceBulletRewrite>,
  targetRole: String,
  targetCompany: String,
  onToggleApply: ((String) -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  if (rewrites.isEmpty()) return

  val context = LocalContext.current
  var selectedCategoryFilter by remember { mutableStateOf<String?>(null) }
  val categories = remember(rewrites) {
    listOf("All") + rewrites.map { it.targetExperienceArea }.distinct()
  }

  val filteredRewrites = remember(rewrites, selectedCategoryFilter) {
    if (selectedCategoryFilter == null || selectedCategoryFilter == "All") {
      rewrites
    } else {
      rewrites.filter { it.targetExperienceArea == selectedCategoryFilter }
    }
  }

  val appliedCount = rewrites.count { it.isApplied }

  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("experience_bullet_rewrites_section"),
    colors = CardDefaults.cardColors(containerColor = SlateElevated),
    shape = RoundedCornerShape(14.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, TitanCyan.copy(alpha = 0.35f))
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
    ) {
      // 1. SECTION HEADER
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
              .background(TitanCyan.copy(alpha = 0.15f))
              .border(1.dp, TitanCyan.copy(alpha = 0.4f), CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.AutoAwesome,
              contentDescription = "AI Bullet Rewrites",
              tint = TitanCyan,
              modifier = Modifier.size(16.dp)
            )
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              text = "EXPERIENCE DESCRIPTION REWRITES",
              fontSize = 11.sp,
              fontWeight = FontWeight.Black,
              color = TitanCyan,
              letterSpacing = 0.8.sp
            )
            Text(
              text = "AI-Calibrated Power Bullets for $targetRole",
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold,
              color = TextPrimaryDark
            )
          }
        }

        // Count / Progress Badge
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (appliedCount > 0) TitanEmerald.copy(alpha = 0.15f) else SlateCard)
            .border(
              1.dp,
              if (appliedCount > 0) TitanEmerald.copy(alpha = 0.4f) else SlateBorder,
              RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
          Text(
            text = if (appliedCount > 0) "$appliedCount/${rewrites.size} ADOPTED" else "${rewrites.size} SUGGESTIONS",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = if (appliedCount > 0) TitanEmerald else TitanCyan
          )
        }
      }

      Spacer(modifier = Modifier.height(6.dp))

      Text(
        text = "Specific, actionable before/after bullet transformations suggested by AI to rewrite your experience statements, infusing hard metrics and JD mandates to pass screening filters for $targetCompany.",
        fontSize = 11.sp,
        color = TextSecondaryDark,
        lineHeight = 16.sp
      )

      // Category filter pills if > 1 category
      if (categories.size > 2) {
        Spacer(modifier = Modifier.height(12.dp))
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          categories.forEach { category ->
            val isSelected = (selectedCategoryFilter == null && category == "All") || selectedCategoryFilter == category
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(if (isSelected) TitanCyan.copy(alpha = 0.2f) else SlateCard)
                .border(
                  1.dp,
                  if (isSelected) TitanCyan else SlateBorder,
                  RoundedCornerShape(6.dp)
                )
                .clickable {
                  selectedCategoryFilter = if (category == "All") null else category
                }
                .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
              Text(
                text = category,
                fontSize = 10.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) TitanCyan else TextMutedDark
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // 2. LIST OF ACTIONABLE BULLET REWRITES
      Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        filteredRewrites.forEachIndexed { index, rewrite ->
          ExperienceBulletRewriteCard(
            rewrite = rewrite,
            index = index + 1,
            targetRole = targetRole,
            onToggleApply = {
              onToggleApply?.invoke(rewrite.id)
            }
          )
        }
      }
    }
  }
}

/**
 * Individual Card displaying the before/after experience rewrite with
 * metric infusions, injected ATS keywords, strategic rationale, and copy/adopt actions.
 */
@Composable
fun ExperienceBulletRewriteCard(
  rewrite: ExperienceBulletRewrite,
  index: Int,
  targetRole: String,
  onToggleApply: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  var isRationaleExpanded by remember { mutableStateOf(false) }

  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("bullet_rewrite_card_${rewrite.id}")
      .animateContentSize(animationSpec = tween(250)),
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(
      containerColor = if (rewrite.isApplied) ObsidianDark else SlateCard
    ),
    border = androidx.compose.foundation.BorderStroke(
      1.dp,
      if (rewrite.isApplied) TitanEmerald.copy(alpha = 0.5f) else SlateBorder
    )
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      // Header: Domain Area & Targeted Mandate
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.weight(1f)
        ) {
          Box(
            modifier = Modifier
              .size(22.dp)
              .clip(CircleShape)
              .background(TitanCyan.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "$index",
              fontSize = 11.sp,
              fontWeight = FontWeight.Black,
              color = TitanCyan
            )
          }
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = rewrite.targetExperienceArea.uppercase(),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = TitanCyan,
            letterSpacing = 0.5.sp
          )
        }

        if (rewrite.isApplied) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(TitanEmerald.copy(alpha = 0.2f))
              .padding(horizontal = 6.dp, vertical = 2.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = TitanEmerald,
                modifier = Modifier.size(12.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "ADOPTED",
                fontSize = 9.sp,
                fontWeight = FontWeight.Black,
                color = TitanEmerald
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(6.dp))

      // Targeted JD Requirement Pill
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(6.dp))
          .background(ObsidianDark.copy(alpha = 0.6f))
          .border(1.dp, SlateBorder.copy(alpha = 0.7f), RoundedCornerShape(6.dp))
        .padding(horizontal = 8.dp, vertical = 5.dp)
      ) {
        Text(
          text = "🎯 JD MANDATE: ${rewrite.targetedJdRequirement}",
          fontSize = 10.sp,
          color = TitanGold,
          fontWeight = FontWeight.SemiBold
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      // BEFORE / ORIGINAL BULLET
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(8.dp))
          .background(TitanCrimson.copy(alpha = 0.05f))
          .border(1.dp, TitanCrimson.copy(alpha = 0.25f), RoundedCornerShape(8.dp))
          .padding(10.dp)
      ) {
        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(TitanCrimson)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "ORIGINAL / PASSIVE PHRASING",
              fontSize = 9.sp,
              fontWeight = FontWeight.Bold,
              color = TitanCrimson,
              letterSpacing = 0.5.sp
            )
          }
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "\"${rewrite.originalBullet}\"",
            fontSize = 11.sp,
            color = TextMutedDark,
            fontStyle = FontStyle.Italic,
            lineHeight = 16.sp
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // AFTER / AI SUGGESTED POWER REWRITE
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(8.dp))
          .background(TitanEmerald.copy(alpha = 0.08f))
          .border(1.dp, TitanEmerald.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
          .padding(10.dp)
      ) {
        Column {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(6.dp)
                  .clip(CircleShape)
                  .background(TitanEmerald)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "AI SUGGESTED REWRITE (QUANTIFIED)",
                fontSize = 9.sp,
                fontWeight = FontWeight.Black,
                color = TitanEmerald,
                letterSpacing = 0.5.sp
              )
            }
            Icon(
              imageVector = Icons.Default.AutoAwesome,
              contentDescription = null,
              tint = TitanEmerald,
              modifier = Modifier.size(13.dp)
            )
          }
          Spacer(modifier = Modifier.height(5.dp))
          Text(
            text = rewrite.suggestedRewriteBullet,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimaryDark,
            lineHeight = 17.sp
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // METRIC INFUSED BADGE
      if (rewrite.metricInfused.isNotBlank()) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(SlateElevated)
            .border(1.dp, SlateBorder, RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 5.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.TrendingUp,
            contentDescription = null,
            tint = TitanEmerald,
            modifier = Modifier.size(14.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Metric Proof: ",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = TextMutedDark
          )
          Text(
            text = rewrite.metricInfused,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = TitanEmerald
          )
        }
        Spacer(modifier = Modifier.height(8.dp))
      }

      // INJECTED KEYWORDS
      if (rewrite.keywordsInjected.isNotEmpty()) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "KEYWORDS:",
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = TextMutedDark,
            letterSpacing = 0.5.sp
          )
          rewrite.keywordsInjected.forEach { kw ->
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(TitanCyan.copy(alpha = 0.1f))
                .border(1.dp, TitanCyan.copy(alpha = 0.25f), RoundedCornerShape(4.dp))
                .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
              Text(
                text = "#$kw",
                fontSize = 9.sp,
                fontWeight = FontWeight.SemiBold,
                color = TitanCyan
              )
            }
          }
        }
        Spacer(modifier = Modifier.height(8.dp))
      }

      // STRATEGIC RATIONALE TOGGLE
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(6.dp))
          .clickable { isRationaleExpanded = !isRationaleExpanded }
          .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.Psychology,
            contentDescription = null,
            tint = TitanCyan,
            modifier = Modifier.size(13.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Why this rewrite works for $targetRole",
            fontSize = 10.sp,
            color = TitanCyan,
            fontWeight = FontWeight.SemiBold
          )
        }
        Icon(
          imageVector = if (isRationaleExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
          contentDescription = null,
          tint = TextMutedDark,
          modifier = Modifier.size(16.dp)
        )
      }

      AnimatedVisibility(
        visible = isRationaleExpanded,
        enter = fadeIn(),
        exit = fadeOut()
      ) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(ObsidianDark)
            .padding(8.dp)
        ) {
          Text(
            text = rewrite.strategicRationale,
            fontSize = 11.sp,
            color = TextSecondaryDark,
            lineHeight = 15.sp
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))
      HorizontalDivider(color = SlateBorder.copy(alpha = 0.6f))
      Spacer(modifier = Modifier.height(8.dp))

      // 3. ACTION BUTTONS: COPY REWRITE & ADOPT TOGGLE
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        // Copy Rewrite Button
        OutlinedButton(
          onClick = {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("AI Experience Bullet Rewrite", rewrite.suggestedRewriteBullet)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(context, "Copied AI rewrite to clipboard!", Toast.LENGTH_SHORT).show()
          },
          modifier = Modifier
            .weight(1f)
            .height(36.dp)
            .testTag("copy_rewrite_btn_${rewrite.id}"),
          shape = RoundedCornerShape(8.dp),
          colors = ButtonDefaults.outlinedButtonColors(
            contentColor = TitanCyan
          ),
          border = androidx.compose.foundation.BorderStroke(1.dp, TitanCyan.copy(alpha = 0.5f))
        ) {
          Icon(
            imageVector = Icons.Default.ContentCopy,
            contentDescription = "Copy Rewrite",
            tint = TitanCyan,
            modifier = Modifier.size(13.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Copy Bullet",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = TitanCyan
          )
        }

        // Adopt Rewrite Toggle Button
        Button(
          onClick = onToggleApply,
          modifier = Modifier
            .weight(1f)
            .height(36.dp)
            .testTag("toggle_adopt_rewrite_btn_${rewrite.id}"),
          shape = RoundedCornerShape(8.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = if (rewrite.isApplied) TitanEmerald else SlateElevated
          ),
          border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (rewrite.isApplied) TitanEmerald else SlateBorder
          )
        ) {
          Icon(
            imageVector = if (rewrite.isApplied) Icons.Default.CheckCircle else Icons.Default.Edit,
            contentDescription = if (rewrite.isApplied) "Adopted" else "Adopt Rewrite",
            tint = if (rewrite.isApplied) ObsidianDark else TextPrimaryDark,
            modifier = Modifier.size(13.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = if (rewrite.isApplied) "Adopted" else "Adopt Rewrite",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = if (rewrite.isApplied) ObsidianDark else TextPrimaryDark
          )
        }
      }
    }
  }
}
