package com.example.ui.components

import android.app.Activity
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.auth.CloudSyncStatus
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
import com.example.ui.viewmodel.TitanViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CloudSyncProfileCard(
  viewModel: TitanViewModel
) {
  val context = LocalContext.current
  val activity = context as? Activity

  val authUser by viewModel.authUser.collectAsState()
  val syncSummary by viewModel.cloudSyncSummary.collectAsState()
  val isSyncing by viewModel.isCloudSyncInProgress.collectAsState()

  val infiniteTransition = rememberInfiniteTransition(label = "sync_rotation")
  val rotation by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 360f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "sync_spin"
  )

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("cloud_sync_profile_card"),
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = SlateCard),
    border = BorderStroke(1.dp, if (authUser != null) TitanCyan.copy(alpha = 0.4f) else SlateBorder)
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      if (authUser != null) {
        val user = authUser!!

        // Signed In Header with Cloud Sync Indicator
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            if (!user.photoUrl.isNullOrBlank()) {
              AsyncImage(
                model = user.photoUrl,
                contentDescription = "User Avatar",
                modifier = Modifier
                  .size(36.dp)
                  .clip(CircleShape)
                  .border(1.dp, TitanCyan, CircleShape)
              )
            } else {
              Box(
                modifier = Modifier
                  .size(36.dp)
                  .clip(CircleShape)
                  .background(TitanCyan),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = user.displayName?.take(1)?.uppercase() ?: "A",
                  fontSize = 18.sp,
                  fontWeight = FontWeight.Black,
                  color = ObsidianDark
                )
              }
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = user.displayName ?: "Career Titan",
                  style = MaterialTheme.typography.titleSmall,
                  fontWeight = FontWeight.Bold,
                  color = TextPrimaryDark
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                  imageVector = Icons.Default.CheckCircle,
                  contentDescription = "Verified User",
                  tint = TitanEmerald,
                  modifier = Modifier.size(13.dp)
                )
              }
              Text(
                text = user.email ?: "Anonymous Session",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondaryDark,
                fontSize = 11.sp
              )
            }
          }

          // Cloud Status Pill
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(if (syncSummary.status == CloudSyncStatus.SYNCED) TitanEmerald.copy(alpha = 0.15f) else TitanGold.copy(alpha = 0.15f))
              .border(1.dp, if (syncSummary.status == CloudSyncStatus.SYNCED) TitanEmerald.copy(alpha = 0.4f) else TitanGold.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
              .clickable { viewModel.setAuthDialogOpen(true) }
              .padding(horizontal = 8.dp, vertical = 4.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = if (syncSummary.status == CloudSyncStatus.SYNCED) Icons.Default.CloudDone else Icons.Default.Sync,
                contentDescription = null,
                tint = if (syncSummary.status == CloudSyncStatus.SYNCED) TitanEmerald else TitanGold,
                modifier = if (isSyncing) Modifier.size(12.dp).rotate(rotation) else Modifier.size(12.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = if (isSyncing) "Syncing..." else "Synced",
                style = MaterialTheme.typography.labelSmall,
                color = if (syncSummary.status == CloudSyncStatus.SYNCED) TitanEmerald else TitanGold,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Sync Metrics
        val lastSyncStr = syncSummary.lastSyncedTimestamp?.let {
          SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()).format(Date(it))
        } ?: "Just now"

        Text(
          text = "☁️ Firebase Firestore: Backed up profile & verified facts across devices ($lastSyncStr)",
          style = MaterialTheme.typography.bodySmall,
          color = TitanCyan,
          fontSize = 11.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Action Buttons: Sync Now, Restore, Manage
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Button(
            onClick = { viewModel.syncCareerProfileToCloud() },
            enabled = !isSyncing,
            colors = ButtonDefaults.buttonColors(containerColor = TitanCyan, contentColor = ObsidianDark),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.weight(1f)
          ) {
            Icon(
              imageVector = Icons.Default.CloudUpload,
              contentDescription = null,
              modifier = if (isSyncing) Modifier.size(14.dp).rotate(rotation) else Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text("Sync Now", fontSize = 11.sp, fontWeight = FontWeight.Bold)
          }

          OutlinedButton(
            onClick = { viewModel.restoreCareerProfileFromCloud() },
            enabled = !isSyncing,
            border = BorderStroke(1.dp, SlateBorder),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.weight(1f)
          ) {
            Icon(Icons.Default.CloudDownload, contentDescription = null, tint = TitanGold, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Restore", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TitanGold)
          }

          Box(
            modifier = Modifier
              .size(36.dp)
              .clip(RoundedCornerShape(8.dp))
              .background(SlateElevated)
              .border(1.dp, SlateBorder, RoundedCornerShape(8.dp))
              .clickable { viewModel.setAuthDialogOpen(true) },
            contentAlignment = Alignment.Center
          ) {
            Icon(Icons.Default.ManageAccounts, contentDescription = "Manage Account", tint = TextSecondaryDark, modifier = Modifier.size(18.dp))
          }
        }
      } else {
        // Unauthenticated Banner
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.Top
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "FIREBASE CLOUD VAULT",
              style = MaterialTheme.typography.labelSmall,
              color = TitanCyan,
              fontWeight = FontWeight.Bold,
              letterSpacing = 0.5.sp
            )
            Text(
              text = "Save Career Profile & History Across Devices",
              style = MaterialTheme.typography.titleSmall,
              fontWeight = FontWeight.Bold,
              color = TextPrimaryDark
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "Sign in with Google to securely synchronize your verified facts, STAR stories, and applications across your devices.",
              style = MaterialTheme.typography.bodySmall,
              color = TextSecondaryDark,
              fontSize = 11.sp,
              lineHeight = 15.sp
            )
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          // Google Sign In CTA
          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(8.dp))
              .background(Color.White)
              .clickable {
                if (activity != null) {
                  viewModel.signInWithGoogle(activity)
                } else {
                  viewModel.setAuthDialogOpen(true)
                }
              }
              .padding(vertical = 8.dp, horizontal = 12.dp),
            contentAlignment = Alignment.Center
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text("G", fontSize = 14.sp, fontWeight = FontWeight.Black, color = Color(0xFF4285F4))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Sign in with Google", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1F1F1F))
            }
          }

          // Options / Manage Button
          OutlinedButton(
            onClick = { viewModel.setAuthDialogOpen(true) },
            border = BorderStroke(1.dp, SlateBorder),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.weight(0.7f)
          ) {
            Text("Options", fontSize = 11.sp, color = TitanCyan, fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}
