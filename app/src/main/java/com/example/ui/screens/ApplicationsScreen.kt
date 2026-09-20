package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.automirrored.filled.FactCheck
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Application
import com.example.service.FirestoreSyncState
import com.example.ui.components.CareerRoadmapReportExportDialog
import com.example.ui.components.MultiPlatformSyncCard
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
import com.example.ui.viewmodel.TitanViewModel

val APPLICATION_STATUS_OPTIONS = listOf(
  "APPLIED",
  "ASSESSMENT",
  "INTERVIEW",
  "OFFER",
  "REJECTED"
)

@Composable
fun ApplicationsScreen(
  viewModel: TitanViewModel
) {
  val applications by viewModel.applications.collectAsState()
  val roadmapReport by viewModel.pipelineCareerRoadmap.collectAsState()
  val syncState by viewModel.firestoreAppSyncState.collectAsState()
  val statusMessage by viewModel.firestoreAppStatusMessage.collectAsState()
  val isSyncing by viewModel.isSyncingApplicationsWithFirestore.collectAsState()

  // Quick Application Input State
  var companyInput by remember { mutableStateOf("") }
  var roleInput by remember { mutableStateOf("") }
  var selectedStatus by remember { mutableStateOf("APPLIED") }
  var notesInput by remember { mutableStateOf("") }
  var showNotesField by remember { mutableStateOf(false) }
  var submissionBannerText by remember { mutableStateOf<String?>(null) }

  // Filter & Search State
  var searchQuery by remember { mutableStateOf("") }
  var statusFilter by remember { mutableStateOf("ALL") }

  // Dialogs
  var showNegotiationLab by remember { mutableStateOf(false) }
  var showExportDialog by remember { mutableStateOf(false) }
  var appToDelete by remember { mutableStateOf<Application?>(null) }

  val filteredApplications = remember(applications, searchQuery, statusFilter) {
    applications.filter { app ->
      val matchesQuery = searchQuery.isBlank() ||
        app.companyName.contains(searchQuery, ignoreCase = true) ||
        app.roleTitle.contains(searchQuery, ignoreCase = true)
      val matchesStatus = statusFilter == "ALL" || app.status.equals(statusFilter, ignoreCase = true)
      matchesQuery && matchesStatus
    }
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(ObsidianDark)
      .padding(horizontal = 16.dp, vertical = 8.dp)
      .testTag("applications_screen")
  ) {
    // -------------------------------------------------------------------------
    // Top Bar: Header & Firestore Status Indicator
    // -------------------------------------------------------------------------
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = "FIRESTORE PIPELINE TRACKER",
          style = MaterialTheme.typography.labelSmall,
          color = TitanCyan,
          fontWeight = FontWeight.Bold,
          letterSpacing = 1.sp
        )
        Text(
          text = "Job Applications",
          style = MaterialTheme.typography.titleLarge,
          fontWeight = FontWeight.Bold,
          color = TextPrimaryDark
        )
      }

      Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Sync with Firestore Button
        Button(
          onClick = { viewModel.syncApplicationsWithFirestore() },
          colors = ButtonDefaults.buttonColors(
            containerColor = SlateElevated,
            contentColor = if (syncState == FirestoreSyncState.SYNCED) TitanCyan else TitanGold
          ),
          shape = RoundedCornerShape(8.dp),
          border = BorderStroke(1.dp, SlateBorder),
          modifier = Modifier.testTag("sync_firestore_button")
        ) {
          if (isSyncing || syncState == FirestoreSyncState.SYNCING) {
            CircularProgressIndicator(
              modifier = Modifier.size(12.dp),
              strokeWidth = 2.dp,
              color = TitanCyan
            )
          } else {
            Icon(Icons.Default.CloudDone, contentDescription = null, modifier = Modifier.size(14.dp))
          }
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = if (isSyncing) "Syncing..." else "Firestore Sync",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
          )
        }

        Button(
          onClick = { showNegotiationLab = true },
          colors = ButtonDefaults.buttonColors(containerColor = SlateElevated, contentColor = TitanGold),
          shape = RoundedCornerShape(8.dp),
          border = BorderStroke(1.dp, TitanGold.copy(alpha = 0.5f)),
          modifier = Modifier.testTag("offer_lab_button")
        ) {
          Icon(Icons.Default.MonetizationOn, contentDescription = null, modifier = Modifier.size(14.dp), tint = TitanGold)
          Spacer(modifier = Modifier.width(4.dp))
          Text("Offer Lab", fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }

        Button(
          onClick = { viewModel.openResumeComparison() },
          colors = ButtonDefaults.buttonColors(containerColor = SlateElevated, contentColor = TitanEmerald),
          shape = RoundedCornerShape(8.dp),
          border = BorderStroke(1.dp, TitanEmerald.copy(alpha = 0.5f)),
          modifier = Modifier.testTag("match_resume_applications_button")
        ) {
          Icon(Icons.AutoMirrored.Filled.FactCheck, contentDescription = "Resume Matcher", modifier = Modifier.size(14.dp), tint = TitanEmerald)
          Spacer(modifier = Modifier.width(4.dp))
          Text("Match Resume", fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }

        Button(
          onClick = { showExportDialog = true },
          colors = ButtonDefaults.buttonColors(containerColor = SlateElevated, contentColor = TitanCyan),
          shape = RoundedCornerShape(8.dp),
          border = BorderStroke(1.dp, TitanCyan.copy(alpha = 0.5f)),
          modifier = Modifier.testTag("export_applications_report_button")
        ) {
          Icon(Icons.Default.Share, contentDescription = "Export Report", modifier = Modifier.size(14.dp), tint = TitanCyan)
          Spacer(modifier = Modifier.width(4.dp))
          Text("Export", fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
      }
    }

    Spacer(modifier = Modifier.height(6.dp))

    // Firestore Sync Status Pill
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(6.dp))
        .background(SlateElevated)
        .padding(horizontal = 10.dp, vertical = 5.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(8.dp)
          .clip(CircleShape)
          .background(
            when (syncState) {
              FirestoreSyncState.SYNCED -> TitanEmerald
              FirestoreSyncState.SYNCING -> TitanCyan
              FirestoreSyncState.FAILED -> TitanCrimson
              else -> TitanGold
            }
          )
      )
      Spacer(modifier = Modifier.width(8.dp))
      Text(
        text = statusMessage,
        style = MaterialTheme.typography.labelSmall,
        color = TextSecondaryDark,
        fontSize = 11.sp,
        maxLines = 1,
        modifier = Modifier.weight(1f)
      )
      Text(
        text = "${applications.size} Logged",
        style = MaterialTheme.typography.labelSmall,
        color = TitanCyan,
        fontWeight = FontWeight.Bold,
        fontSize = 11.sp
      )
    }

    Spacer(modifier = Modifier.height(10.dp))

    // -------------------------------------------------------------------------
    // Main Scrollable Content
    // -------------------------------------------------------------------------
    LazyColumn(
      modifier = Modifier.fillMaxSize(),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      // 0. MULTI-PLATFORM SAVED JOBS & ATS TRACKING DAEMON CARD
      item {
        MultiPlatformSyncCard(
          viewModel = viewModel,
          initialExpanded = true
        )
      }

      // 1. SIMPLE LOGGING CARD
      item {
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .testTag("log_application_card"),
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = SlateCard),
          border = BorderStroke(1.dp, TitanCyan.copy(alpha = 0.4f))
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
                    .size(28.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(TitanCyan.copy(alpha = 0.15f)),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    Icons.Default.Cloud,
                    contentDescription = null,
                    tint = TitanCyan,
                    modifier = Modifier.size(16.dp)
                  )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                  Text(
                    text = "Log Company Applied To",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimaryDark
                  )
                  Text(
                    text = "Persists directly to Firebase Firestore",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondaryDark,
                    fontSize = 10.sp
                  )
                }
              }

              // Status indicator tag
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(4.dp))
                  .background(TitanIndigo.copy(alpha = 0.2f))
                  .padding(horizontal = 6.dp, vertical = 2.dp)
              ) {
                Text(
                  text = "FIRESTORE LIVE",
                  style = MaterialTheme.typography.labelSmall,
                  color = TitanIndigo,
                  fontWeight = FontWeight.Bold,
                  fontSize = 9.sp
                )
              }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Company Name Field
            OutlinedTextField(
              value = companyInput,
              onValueChange = {
                companyInput = it
                submissionBannerText = null
              },
              label = { Text("Company Name (e.g. Google, Bain, Swiggy, Stripe)") },
              leadingIcon = {
                Icon(Icons.Default.Business, contentDescription = null, tint = TitanCyan, modifier = Modifier.size(16.dp))
              },
              singleLine = true,
              modifier = Modifier
                .fillMaxWidth()
                .testTag("company_name_input"),
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = TitanCyan,
                unfocusedBorderColor = SlateBorder,
                focusedTextColor = TextPrimaryDark,
                unfocusedTextColor = TextPrimaryDark,
                focusedLabelColor = TitanCyan,
                unfocusedLabelColor = TextSecondaryDark
              )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Role Title Field
            OutlinedTextField(
              value = roleInput,
              onValueChange = {
                roleInput = it
                submissionBannerText = null
              },
              label = { Text("Role Title (e.g. Associate Product Manager, Strategy Lead)") },
              leadingIcon = {
                Icon(Icons.Default.Work, contentDescription = null, tint = TitanCyan, modifier = Modifier.size(16.dp))
              },
              singleLine = true,
              modifier = Modifier
                .fillMaxWidth()
                .testTag("role_title_input"),
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = TitanCyan,
                unfocusedBorderColor = SlateBorder,
                focusedTextColor = TextPrimaryDark,
                unfocusedTextColor = TextPrimaryDark,
                focusedLabelColor = TitanCyan,
                unfocusedLabelColor = TextSecondaryDark
              )
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Status Selector Pills
            Text(
              text = "Application Status:",
              style = MaterialTheme.typography.labelSmall,
              color = TextSecondaryDark,
              fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))

            Row(
              modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              APPLICATION_STATUS_OPTIONS.forEach { statusOption ->
                val isSelected = selectedStatus == statusOption
                val chipColor = when (statusOption) {
                  "OFFER" -> TitanEmerald
                  "INTERVIEW" -> TitanGold
                  "APPLIED" -> TitanIndigo
                  "ASSESSMENT" -> TitanCyan
                  "REJECTED" -> TitanCrimson
                  else -> TextMutedDark
                }

                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isSelected) chipColor.copy(alpha = 0.25f) else SlateElevated)
                    .border(
                      width = if (isSelected) 1.5.dp else 1.dp,
                      color = if (isSelected) chipColor else SlateBorder,
                      shape = RoundedCornerShape(8.dp)
                    )
                    .clickable {
                      selectedStatus = statusOption
                    }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
                    .testTag("status_chip_$statusOption"),
                  contentAlignment = Alignment.Center
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isSelected) {
                      Icon(
                        Icons.Default.Check,
                        contentDescription = null,
                        tint = chipColor,
                        modifier = Modifier.size(12.dp)
                      )
                      Spacer(modifier = Modifier.width(4.dp))
                    }
                    Text(
                      text = statusOption,
                      color = if (isSelected) chipColor else TextSecondaryDark,
                      fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                      fontSize = 11.sp
                    )
                  }
                }
              }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Optional Notes Accordion / Toggle
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clickable { showNotesField = !showNotesField }
                .padding(vertical = 4.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = if (showNotesField) "- Hide Notes / Follow-up" else "+ Add Notes or Follow-up Date",
                style = MaterialTheme.typography.labelSmall,
                color = TitanCyan,
                fontWeight = FontWeight.Medium
              )
            }

            if (showNotesField) {
              Spacer(modifier = Modifier.height(6.dp))
              OutlinedTextField(
                value = notesInput,
                onValueChange = { notesInput = it },
                label = { Text("Follow-up / Referral Notes") },
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("notes_input"),
                colors = OutlinedTextFieldDefaults.colors(
                  focusedBorderColor = TitanCyan,
                  unfocusedBorderColor = SlateBorder,
                  focusedTextColor = TextPrimaryDark,
                  unfocusedTextColor = TextPrimaryDark
                )
              )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Submission Success Banner
            AnimatedVisibility(visible = submissionBannerText != null) {
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(8.dp))
                  .background(TitanEmerald.copy(alpha = 0.15f))
                  .border(1.dp, TitanEmerald.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                  .padding(10.dp)
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = TitanEmerald,
                    modifier = Modifier.size(16.dp)
                  )
                  Spacer(modifier = Modifier.width(8.dp))
                  Text(
                    text = submissionBannerText ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    color = TitanEmerald,
                    fontWeight = FontWeight.SemiBold
                  )
                }
              }
              Spacer(modifier = Modifier.height(8.dp))
            }

            // Submit Button: Log Application to Firestore
            Button(
              onClick = {
                if (companyInput.isNotBlank()) {
                  val company = companyInput.trim()
                  val role = if (roleInput.isNotBlank()) roleInput.trim() else "Candidate Role"
                  viewModel.logJobApplicationToFirestore(
                    companyName = company,
                    roleTitle = role,
                    status = selectedStatus,
                    notes = notesInput
                  )
                  submissionBannerText = "Successfully logged $company ($selectedStatus) to Firestore!"
                  companyInput = ""
                  roleInput = ""
                  notesInput = ""
                }
              },
              enabled = companyInput.isNotBlank(),
              modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .testTag("log_application_submit_button"),
              colors = ButtonDefaults.buttonColors(
                containerColor = TitanCyan,
                contentColor = ObsidianDark,
                disabledContainerColor = SlateElevated,
                disabledContentColor = TextMutedDark
              ),
              shape = RoundedCornerShape(8.dp)
            ) {
              Icon(
                Icons.Default.CloudDone,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "Log Application to Firestore",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
              )
            }
          }
        }
      }

      // 2. STATUS SUMMARY PILLS
      item {
        val totalCount = applications.size
        val appliedCount = applications.count { it.status == "APPLIED" }
        val interviewCount = applications.count { it.status == "INTERVIEW" }
        val offerCount = applications.count { it.status == "OFFER" }
        val assessmentCount = applications.count { it.status == "ASSESSMENT" }
        val rejectedCount = applications.count { it.status == "REJECTED" }

        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          StatusMetricCard("Total", totalCount.toString(), TitanCyan, statusFilter == "ALL") {
            statusFilter = "ALL"
          }
          StatusMetricCard("Applied", appliedCount.toString(), TitanIndigo, statusFilter == "APPLIED") {
            statusFilter = if (statusFilter == "APPLIED") "ALL" else "APPLIED"
          }
          StatusMetricCard("Interview", interviewCount.toString(), TitanGold, statusFilter == "INTERVIEW") {
            statusFilter = if (statusFilter == "INTERVIEW") "ALL" else "INTERVIEW"
          }
          StatusMetricCard("Offer", offerCount.toString(), TitanEmerald, statusFilter == "OFFER") {
            statusFilter = if (statusFilter == "OFFER") "ALL" else "OFFER"
          }
          StatusMetricCard("Assessment", assessmentCount.toString(), TitanCyan, statusFilter == "ASSESSMENT") {
            statusFilter = if (statusFilter == "ASSESSMENT") "ALL" else "ASSESSMENT"
          }
          StatusMetricCard("Rejected", rejectedCount.toString(), TitanCrimson, statusFilter == "REJECTED") {
            statusFilter = if (statusFilter == "REJECTED") "ALL" else "REJECTED"
          }
        }
      }

      // 3. SEARCH BAR & ACTIVE FILTER HEADER
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically
        ) {
          OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search by company or role...", fontSize = 12.sp) },
            leadingIcon = {
              Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondaryDark, modifier = Modifier.size(16.dp))
            },
            trailingIcon = {
              if (searchQuery.isNotBlank()) {
                IconButton(onClick = { searchQuery = "" }) {
                  Icon(Icons.Default.Close, contentDescription = "Clear", tint = TextSecondaryDark, modifier = Modifier.size(14.dp))
                }
              }
            },
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("application_search_bar"),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = TitanCyan,
              unfocusedBorderColor = SlateBorder,
              focusedTextColor = TextPrimaryDark,
              unfocusedTextColor = TextPrimaryDark,
              focusedPlaceholderColor = TextMutedDark,
              unfocusedPlaceholderColor = TextMutedDark
            )
          )
        }
      }

      // Filter indicator text
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = if (statusFilter == "ALL") "All Applications (${filteredApplications.size})" else "$statusFilter (${filteredApplications.size})",
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondaryDark,
            fontWeight = FontWeight.Bold
          )
          if (statusFilter != "ALL" || searchQuery.isNotBlank()) {
            Text(
              text = "Clear filters",
              style = MaterialTheme.typography.labelSmall,
              color = TitanCyan,
              modifier = Modifier
                .clickable {
                  statusFilter = "ALL"
                  searchQuery = ""
                }
                .padding(4.dp)
            )
          }
        }
      }

      // 4. APPLICATIONS LIST
      if (filteredApplications.isEmpty()) {
        item {
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 20.dp),
            colors = CardDefaults.cardColors(containerColor = SlateCard),
            border = BorderStroke(1.dp, SlateBorder)
          ) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Icon(
                Icons.AutoMirrored.Filled.Assignment,
                contentDescription = null,
                tint = TextMutedDark,
                modifier = Modifier.size(36.dp)
              )
              Spacer(modifier = Modifier.height(10.dp))
              Text(
                text = if (searchQuery.isNotBlank() || statusFilter != "ALL") "No matching applications found" else "No applications logged yet",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimaryDark
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = if (searchQuery.isNotBlank() || statusFilter != "ALL") "Try clearing your search query or status filter." else "Enter a company name and status in the form above to log your first application to Firestore.",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondaryDark,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
              )
            }
          }
        }
      } else {
        items(filteredApplications, key = { it.id }) { app ->
          TrackedApplicationCard(
            app = app,
            onStatusChange = { newStatus ->
              viewModel.updateApplicationStatusWithFirestore(app.id, newStatus)
            },
            onDelete = {
              appToDelete = app
            },
            onOpenSalaryModeler = {
              viewModel.setSalaryCalculatorOpen(true)
            },
            onNegotiateOffer = {
              viewModel.openSalaryNegotiationDialog(role = app.roleTitle, company = app.companyName)
            },
            onMatchResume = {
              viewModel.openResumeComparison(
                targetCompany = app.companyName,
                role = app.roleTitle
              )
            }
          )
        }
      }

      item {
        Spacer(modifier = Modifier.height(24.dp))
      }
    }
  }

  // Delete Confirmation Dialog
  appToDelete?.let { app ->
    AlertDialog(
      onDismissRequest = { appToDelete = null },
      containerColor = SlateElevated,
      title = {
        Text(
          text = "Remove Application?",
          style = MaterialTheme.typography.titleMedium,
          color = TextPrimaryDark,
          fontWeight = FontWeight.Bold
        )
      },
      text = {
        Text(
          text = "Are you sure you want to remove the application for ${app.companyName} (${app.roleTitle}) from your pipeline and Firestore?",
          style = MaterialTheme.typography.bodyMedium,
          color = TextSecondaryDark
        )
      },
      confirmButton = {
        Button(
          onClick = {
            viewModel.deleteApplicationWithFirestore(app.id)
            appToDelete = null
          },
          colors = ButtonDefaults.buttonColors(containerColor = TitanCrimson, contentColor = Color.White),
          shape = RoundedCornerShape(8.dp)
        ) {
          Text("Delete", fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { appToDelete = null }) {
          Text("Cancel", color = TextSecondaryDark)
        }
      }
    )
  }

  // Offer Negotiation & Compensation Playbook Dialog
  if (showNegotiationLab) {
    AlertDialog(
      onDismissRequest = { showNegotiationLab = false },
      containerColor = SlateElevated,
      title = {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = TitanGold, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Bengaluru Tier S/A Offer Strategy Lab", style = MaterialTheme.typography.titleMedium, color = TextPrimaryDark)
          }
          IconButton(onClick = { showNegotiationLab = false }) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondaryDark)
          }
        }
      },
      text = {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(8.dp))
              .background(SlateCard)
              .padding(10.dp)
          ) {
            Column {
              Text(
                text = "EXECUTIVE NEGOTIATION FORMULA",
                style = MaterialTheme.typography.labelSmall,
                color = TitanGold,
                fontWeight = FontWeight.Bold
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "Target Range: ₹18.0L - ₹24.5L Fixed CTC + ESOPs\nAnchor high with documented Tier-1 multi-agent automation portfolio proof.",
                style = MaterialTheme.typography.bodySmall,
                color = TextPrimaryDark,
                fontSize = 12.sp
              )
            }
          }
        }
      },
      confirmButton = {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          Button(
            onClick = {
              showNegotiationLab = false
              viewModel.setSalaryCalculatorOpen(true)
            },
            colors = ButtonDefaults.buttonColors(containerColor = TitanEmerald, contentColor = ObsidianDark),
            shape = RoundedCornerShape(8.dp)
          ) {
            Icon(Icons.Default.MonetizationOn, contentDescription = null, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Launch CTC Modeler", fontWeight = FontWeight.Bold, fontSize = 11.sp)
          }

          Button(
            onClick = { showNegotiationLab = false },
            colors = ButtonDefaults.buttonColors(containerColor = SlateElevated, contentColor = TextPrimaryDark),
            shape = RoundedCornerShape(8.dp)
          ) {
            Text("Close", fontWeight = FontWeight.Bold, fontSize = 11.sp)
          }
        }
      }
    )
  }

  // Career Roadmap & Job Pipeline Status Export Dialog
  if (showExportDialog) {
    CareerRoadmapReportExportDialog(
      roadmapReport = roadmapReport,
      applications = applications,
      candidateName = "Adi",
      onDismiss = { showExportDialog = false }
    )
  }
}

@Composable
fun StatusMetricCard(
  title: String,
  count: String,
  color: Color,
  isSelected: Boolean,
  onClick: () -> Unit
) {
  Box(
    modifier = Modifier
      .clip(RoundedCornerShape(8.dp))
      .background(if (isSelected) color.copy(alpha = 0.2f) else SlateCard)
      .border(
        width = 1.dp,
        color = if (isSelected) color else SlateBorder,
        shape = RoundedCornerShape(8.dp)
      )
      .clickable { onClick() }
      .padding(horizontal = 12.dp, vertical = 6.dp)
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Text(
        text = title,
        style = MaterialTheme.typography.labelSmall,
        color = if (isSelected) color else TextSecondaryDark,
        fontSize = 11.sp
      )
      Spacer(modifier = Modifier.width(6.dp))
      Text(
        text = count,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Bold,
        color = if (isSelected) color else TextPrimaryDark,
        fontSize = 12.sp
      )
    }
  }
}

@Composable
fun TrackedApplicationCard(
  app: Application,
  onStatusChange: (String) -> Unit,
  onDelete: () -> Unit,
  onOpenSalaryModeler: () -> Unit = {},
  onNegotiateOffer: () -> Unit = {},
  onMatchResume: () -> Unit = {}
) {
  var showStatusMenu by remember { mutableStateOf(false) }

  val statusColor = when (app.status) {
    "OFFER" -> TitanEmerald
    "INTERVIEW" -> TitanGold
    "APPLIED" -> TitanIndigo
    "ASSESSMENT" -> TitanCyan
    "REJECTED" -> TitanCrimson
    else -> TextMutedDark
  }

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("app_card_${app.id}"),
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = SlateCard),
    border = BorderStroke(1.dp, SlateBorder)
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = app.companyName,
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimaryDark,
            fontWeight = FontWeight.Bold
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = app.roleTitle,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = TitanCyan
          )
        }

        // Status Selector Dropdown & Delete
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box {
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(statusColor.copy(alpha = 0.15f))
                .border(1.dp, statusColor.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                .clickable { showStatusMenu = true }
                .padding(horizontal = 8.dp, vertical = 4.dp)
                .testTag("status_picker_${app.id}")
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = app.status,
                  style = MaterialTheme.typography.labelSmall,
                  color = statusColor,
                  fontWeight = FontWeight.Bold,
                  fontSize = 11.sp
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "▼",
                  fontSize = 8.sp,
                  color = statusColor
                )
              }
            }

            DropdownMenu(
              expanded = showStatusMenu,
              onDismissRequest = { showStatusMenu = false },
              modifier = Modifier.background(SlateElevated)
            ) {
              APPLICATION_STATUS_OPTIONS.forEach { status ->
                val itemColor = when (status) {
                  "OFFER" -> TitanEmerald
                  "INTERVIEW" -> TitanGold
                  "APPLIED" -> TitanIndigo
                  "ASSESSMENT" -> TitanCyan
                  "REJECTED" -> TitanCrimson
                  else -> TextMutedDark
                }
                DropdownMenuItem(
                  text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Box(
                        modifier = Modifier
                          .size(8.dp)
                          .clip(CircleShape)
                          .background(itemColor)
                      )
                      Spacer(modifier = Modifier.width(8.dp))
                      Text(
                        status,
                        color = if (app.status == status) itemColor else TextPrimaryDark,
                        fontWeight = if (app.status == status) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 12.sp
                      )
                    }
                  },
                  onClick = {
                    onStatusChange(status)
                    showStatusMenu = false
                  },
                  modifier = Modifier.testTag("select_status_${status}_${app.id}")
                )
              }
            }
          }

          Spacer(modifier = Modifier.width(4.dp))

          IconButton(
            onClick = onDelete,
            modifier = Modifier
              .size(28.dp)
              .testTag("delete_app_${app.id}")
          ) {
            Icon(
              Icons.Default.Delete,
              contentDescription = "Delete application",
              tint = TextMutedDark,
              modifier = Modifier.size(16.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Metadata: Dates & Cloud Sync
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        if (app.dateApplied.isNotBlank()) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(SlateElevated)
              .padding(horizontal = 8.dp, vertical = 4.dp)
          ) {
            Icon(Icons.Default.CalendarToday, contentDescription = null, tint = TitanCyan, modifier = Modifier.size(11.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Applied: ${app.dateApplied}", style = MaterialTheme.typography.labelSmall, color = TextSecondaryDark, fontSize = 10.sp)
          }
        }

        // Firestore Synced Pill
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(SlateElevated)
            .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
          Icon(Icons.Default.CloudDone, contentDescription = null, tint = TitanEmerald, modifier = Modifier.size(11.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Firestore Synced", style = MaterialTheme.typography.labelSmall, color = TitanEmerald, fontSize = 10.sp)
        }
      }

      // Follow-up notes if present
      if (app.followUpNotes.isNotBlank()) {
        Spacer(modifier = Modifier.height(8.dp))
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(SlateElevated)
            .padding(8.dp)
        ) {
          Column {
            Text(
              text = "NOTES & NEXT ACTION:",
              style = MaterialTheme.typography.labelSmall,
              color = TitanCyan,
              fontWeight = FontWeight.Bold,
              fontSize = 9.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = app.followUpNotes,
              style = MaterialTheme.typography.bodySmall,
              color = TextPrimaryDark,
              fontSize = 11.sp
            )
          }
        }
      }

      // Action Buttons
      Spacer(modifier = Modifier.height(10.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Button(
          onClick = onMatchResume,
          modifier = Modifier
            .weight(1f)
            .testTag("match_resume_from_app_card_${app.id}"),
          colors = ButtonDefaults.buttonColors(containerColor = SlateElevated, contentColor = TitanEmerald),
          shape = RoundedCornerShape(8.dp),
          border = BorderStroke(1.dp, TitanEmerald.copy(alpha = 0.5f))
        ) {
          Icon(Icons.AutoMirrored.Filled.FactCheck, contentDescription = null, modifier = Modifier.size(14.dp), tint = TitanEmerald)
          Spacer(modifier = Modifier.width(6.dp))
          Text("Match Resume", fontWeight = FontWeight.Bold, fontSize = 11.sp)
        }
      }

      if (app.status == "OFFER") {
        Spacer(modifier = Modifier.height(8.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Button(
            onClick = onNegotiateOffer,
            modifier = Modifier
              .weight(1.1f)
              .testTag("negotiate_offer_btn_${app.id}"),
            colors = ButtonDefaults.buttonColors(containerColor = TitanEmerald, contentColor = ObsidianDark),
            shape = RoundedCornerShape(8.dp)
          ) {
            Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Negotiation Scripts", fontWeight = FontWeight.Bold, fontSize = 10.5.sp)
          }

          Button(
            onClick = onOpenSalaryModeler,
            modifier = Modifier
              .weight(1f)
              .testTag("open_salary_modeler_from_card_${app.id}"),
            colors = ButtonDefaults.buttonColors(containerColor = SlateElevated, contentColor = TitanEmerald),
            border = BorderStroke(1.dp, TitanEmerald.copy(alpha = 0.5f)),
            shape = RoundedCornerShape(8.dp)
          ) {
            Icon(Icons.Default.MonetizationOn, contentDescription = null, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("In-Hand Tax Modeler", fontWeight = FontWeight.Bold, fontSize = 10.5.sp)
          }
        }
      }
    }
  }
}
