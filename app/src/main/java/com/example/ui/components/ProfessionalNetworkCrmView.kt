package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.CorporateFare
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.AlertDialog
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
import com.example.data.model.Company
import com.example.data.model.ContactInteraction
import com.example.data.model.FollowUpReminder
import com.example.data.model.RecruiterContact
import com.example.service.CrmContactBundle
import com.example.service.FirestoreSyncState
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

@Composable
fun ProfessionalNetworkCrmView(
  viewModel: TitanViewModel,
  modifier: Modifier = Modifier
) {
  val crmBundles by viewModel.crmBundles.collectAsState()
  val allReminders by viewModel.allFollowUpReminders.collectAsState()
  val syncState by viewModel.crmSyncState.collectAsState()
  val syncMessage by viewModel.crmStatusMessage.collectAsState()
  val isSyncing by viewModel.isCrmSyncing.collectAsState()
  val companies by viewModel.companies.collectAsState()

  var selectedContactForInteraction by remember { mutableStateOf<RecruiterContact?>(null) }
  var selectedContactForReminder by remember { mutableStateOf<RecruiterContact?>(null) }
  var showAddContactDialog by remember { mutableStateOf(false) }
  var searchQuery by remember { mutableStateOf("") }
  var filterTab by remember { mutableStateOf(0) } // 0: All Contacts, 1: Reminders Queue

  // Dialog fields for Log Interaction
  var interactionType by remember { mutableStateOf("LinkedIn InMail") }
  var interactionNotes by remember { mutableStateOf("") }
  var interactionNextSteps by remember { mutableStateOf("") }

  // Dialog fields for Add Reminder
  var reminderSubject by remember { mutableStateOf("") }
  var reminderDueDate by remember { mutableStateOf("Tomorrow, 10:00 AM") }
  var reminderPriority by remember { mutableStateOf("HIGH") }

  // Dialog fields for Add Contact
  var newName by remember { mutableStateOf("") }
  var newTitle by remember { mutableStateOf("") }
  var newCompany by remember { mutableStateOf("") }
  var newEmail by remember { mutableStateOf("") }
  var newLinkedin by remember { mutableStateOf("") }

  Column(
    modifier = modifier
      .fillMaxWidth()
      .testTag("professional_network_crm_view"),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // Top Firestore Cloud Sync Status Card
    Card(
      colors = CardDefaults.cardColors(containerColor = SlateCard),
      shape = RoundedCornerShape(12.dp),
      border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.weight(1f)
        ) {
          Box(
            modifier = Modifier
              .size(36.dp)
              .clip(CircleShape)
              .background(
                when (syncState) {
                  FirestoreSyncState.SYNCED -> TitanEmerald.copy(alpha = 0.15f)
                  FirestoreSyncState.SYNCING -> TitanCyan.copy(alpha = 0.15f)
                  else -> TitanGold.copy(alpha = 0.15f)
                }
              ),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = when (syncState) {
                FirestoreSyncState.SYNCED -> Icons.Default.CloudDone
                FirestoreSyncState.SYNCING -> Icons.Default.CloudSync
                else -> Icons.Default.CloudUpload
              },
              contentDescription = null,
              tint = when (syncState) {
                FirestoreSyncState.SYNCED -> TitanEmerald
                FirestoreSyncState.SYNCING -> TitanCyan
                else -> TitanGold
              },
              modifier = Modifier.size(20.dp)
            )
          }

          Spacer(modifier = Modifier.width(10.dp))

          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "FIRESTORE CRM CLOUD SYNC",
                style = MaterialTheme.typography.labelSmall,
                color = TitanCyan,
                fontWeight = FontWeight.Bold
              )
              Spacer(modifier = Modifier.width(6.dp))
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(4.dp))
                  .background(TitanEmerald.copy(alpha = 0.2f))
                  .padding(horizontal = 6.dp, vertical = 2.dp)
              ) {
                Text(
                  text = "LIVE",
                  fontSize = 9.sp,
                  fontWeight = FontWeight.Bold,
                  color = TitanEmerald
                )
              }
            }
            Text(
              text = syncMessage,
              style = MaterialTheme.typography.bodySmall,
              color = TextSecondaryDark,
              maxLines = 1
            )
          }
        }

        Button(
          onClick = { viewModel.loadCrmFromFirestore() },
          enabled = !isSyncing,
          colors = ButtonDefaults.buttonColors(containerColor = SlateElevated, contentColor = TitanCyan),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier.testTag("crm_sync_button")
        ) {
          if (isSyncing) {
            CircularProgressIndicator(modifier = Modifier.size(12.dp), strokeWidth = 2.dp, color = TitanCyan)
          } else {
            Icon(Icons.Default.CloudSync, contentDescription = null, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Sync", fontSize = 11.sp, fontWeight = FontWeight.Bold)
          }
        }
      }
    }

    // Key Metric Summary Grid
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      CrmMetricCard(
        title = "CONTACTS",
        count = "${crmBundles.size}",
        subtitle = "In Firestore CRM",
        icon = Icons.Default.People,
        tint = TitanCyan,
        modifier = Modifier.weight(1f)
      )
      CrmMetricCard(
        title = "INTERACTIONS",
        count = "${crmBundles.sumOf { it.interactions.size }}",
        subtitle = "Past Logs",
        icon = Icons.Default.History,
        tint = TitanIndigo,
        modifier = Modifier.weight(1f)
      )
      CrmMetricCard(
        title = "REMINDERS",
        count = "${allReminders.count { !it.isCompleted }}",
        subtitle = "Pending Follow-ups",
        icon = Icons.Default.NotificationsActive,
        tint = TitanGold,
        modifier = Modifier.weight(1f)
      )
    }

    // Navigation Sub-tabs & Action Button
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(
        modifier = Modifier
          .clip(RoundedCornerShape(8.dp))
          .background(SlateCard)
          .padding(2.dp)
      ) {
        listOf("Contacts & History", "Follow-Up Queue (${allReminders.count { !it.isCompleted }})").forEachIndexed { index, title ->
          val isSelected = filterTab == index
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(if (isSelected) SlateElevated else Color.Transparent)
              .clickable { filterTab = index }
              .padding(horizontal = 10.dp, vertical = 6.dp)
          ) {
            Text(
              text = title,
              fontSize = 11.sp,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
              color = if (isSelected) TitanCyan else TextMutedDark
            )
          }
        }
      }

      Button(
        onClick = { showAddContactDialog = true },
        colors = ButtonDefaults.buttonColors(containerColor = TitanCyan, contentColor = ObsidianDark),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.testTag("add_crm_contact_btn")
      ) {
        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text("New Contact", fontSize = 11.sp, fontWeight = FontWeight.Bold)
      }
    }

    // Search Box
    OutlinedTextField(
      value = searchQuery,
      onValueChange = { searchQuery = it },
      modifier = Modifier
        .fillMaxWidth()
        .testTag("crm_search_field"),
      placeholder = { Text("Filter contacts by name or company...", fontSize = 12.sp, color = TextMutedDark) },
      singleLine = true,
      colors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = TitanCyan,
        unfocusedBorderColor = SlateBorder,
        focusedContainerColor = SlateCard,
        unfocusedContainerColor = SlateCard,
        focusedTextColor = TextPrimaryDark,
        unfocusedTextColor = TextPrimaryDark
      ),
      shape = RoundedCornerShape(8.dp)
    )

    // View Content based on active tab
    if (filterTab == 0) {
      // Contacts & Interactions List
      val filteredBundles = crmBundles.filter {
        it.contact.name.contains(searchQuery, ignoreCase = true) ||
          it.contact.companyName.contains(searchQuery, ignoreCase = true)
      }

      if (filteredBundles.isEmpty()) {
        Card(
          colors = CardDefaults.cardColors(containerColor = SlateCard),
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Icon(Icons.Default.People, contentDescription = null, tint = TextMutedDark, modifier = Modifier.size(36.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text("No contacts found", color = TextSecondaryDark, fontWeight = FontWeight.Medium)
            Text("Add a strategic contact to begin tracking interactions and reminders in Firestore.", color = TextMutedDark, fontSize = 12.sp)
          }
        }
      } else {
        filteredBundles.forEach { bundle ->
          CrmContactItemCard(
            bundle = bundle,
            onLogInteraction = {
              selectedContactForInteraction = bundle.contact
              interactionNotes = ""
              interactionNextSteps = ""
            },
            onAddReminder = {
              selectedContactForReminder = bundle.contact
              reminderSubject = "Follow up with ${bundle.contact.name}"
            },
            onSaveToFirestore = {
              viewModel.saveContactToNetworkCrm(bundle.contact)
            },
            onComposeOutreach = {
              val comp = companies.find { it.id == bundle.contact.companyId } ?: Company(
                id = bundle.contact.companyId,
                name = bundle.contact.companyName,
                logoEmoji = "🏢",
                website = "",
                careersUrl = "",
                industry = "Tech",
                subIndustry = "Strategy",
                hqLocation = "",
                bengaluruPresence = "",
                indiaPresence = "",
                employeeScale = "",
                tier = "S",
                strategicPriority = "",
                hiringVelocity = "",
                aiAdoptionLevel = "",
                compensationTier = "",
                isOpenForEarlyCareer = true,
                isWatched = true,
                watchNotes = "",
                scoreExplanation = "",
                activeOpeningsCount = 0
              )
              viewModel.generateOutreachForContact(bundle.contact, comp)
            }
          )
        }
      }
    } else {
      // Follow-Up Reminders Queue
      val activeReminders = allReminders.sortedBy { it.isCompleted }
      if (activeReminders.isEmpty()) {
        Card(
          colors = CardDefaults.cardColors(containerColor = SlateCard),
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Icon(Icons.Default.Alarm, contentDescription = null, tint = TextMutedDark, modifier = Modifier.size(36.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text("No follow-up reminders pending", color = TextSecondaryDark, fontWeight = FontWeight.Medium)
            Text("Schedule reminders on recruiter contacts to stay on top of job search outreach.", color = TextMutedDark, fontSize = 12.sp)
          }
        }
      } else {
        activeReminders.forEach { reminder ->
          ReminderQueueItem(
            reminder = reminder,
            onToggleCompleted = { viewModel.toggleFollowUpReminder(reminder.id) }
          )
        }
      }
    }
  }

  // DIALOG: Log Interaction
  if (selectedContactForInteraction != null) {
    val contact = selectedContactForInteraction!!
    AlertDialog(
      onDismissRequest = { selectedContactForInteraction = null },
      containerColor = SlateCard,
      title = {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.History, contentDescription = null, tint = TitanCyan)
          Spacer(modifier = Modifier.width(8.dp))
          Text("Log Interaction: ${contact.name}", color = TextPrimaryDark, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
      },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Text("Select Interaction Type:", fontSize = 12.sp, color = TextSecondaryDark)
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            listOf("InMail", "Coffee Chat", "Referral", "Screening").forEach { type ->
              val isSelected = interactionType.contains(type)
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(6.dp))
                  .background(if (isSelected) TitanCyan else SlateElevated)
                  .clickable {
                    interactionType = when (type) {
                      "InMail" -> "LinkedIn InMail"
                      "Coffee Chat" -> "Coffee Chat"
                      "Referral" -> "Referral Request"
                      else -> "Screening Call"
                    }
                  }
                  .padding(horizontal = 8.dp, vertical = 6.dp)
              ) {
                Text(
                  text = type,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (isSelected) ObsidianDark else TextPrimaryDark
                )
              }
            }
          }

          OutlinedTextField(
            value = interactionNotes,
            onValueChange = { interactionNotes = it },
            label = { Text("Summary Notes & Conversation Highlights") },
            placeholder = { Text("e.g., Discussed 10-minute dark store unit economics, positive feedback on SQL portfolio.") },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 4,
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = TitanCyan,
              unfocusedBorderColor = SlateBorder,
              focusedTextColor = TextPrimaryDark,
              unfocusedTextColor = TextPrimaryDark
            )
          )

          OutlinedTextField(
            value = interactionNextSteps,
            onValueChange = { interactionNextSteps = it },
            label = { Text("Next Action / Follow-up Step") },
            placeholder = { Text("e.g., Send updated resume and follow up on Tuesday.") },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 2,
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = TitanCyan,
              unfocusedBorderColor = SlateBorder,
              focusedTextColor = TextPrimaryDark,
              unfocusedTextColor = TextPrimaryDark
            )
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (interactionNotes.isNotBlank()) {
              viewModel.logContactInteraction(
                contactId = contact.id,
                interactionType = interactionType,
                summaryNotes = interactionNotes,
                nextSteps = interactionNextSteps.ifBlank { "Follow up in 3 days" }
              )
              selectedContactForInteraction = null
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = TitanCyan, contentColor = ObsidianDark),
          shape = RoundedCornerShape(8.dp)
        ) {
          Text("Save & Sync to Firestore", fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { selectedContactForInteraction = null }) {
          Text("Cancel", color = TextSecondaryDark)
        }
      }
    )
  }

  // DIALOG: Add Follow-up Reminder
  if (selectedContactForReminder != null) {
    val contact = selectedContactForReminder!!
    AlertDialog(
      onDismissRequest = { selectedContactForReminder = null },
      containerColor = SlateCard,
      title = {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = TitanGold)
          Spacer(modifier = Modifier.width(8.dp))
          Text("Set Follow-up Reminder", color = TextPrimaryDark, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
      },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Text("Contact: ${contact.name} (${contact.companyName})", fontSize = 12.sp, color = TitanCyan, fontWeight = FontWeight.Bold)

          OutlinedTextField(
            value = reminderSubject,
            onValueChange = { reminderSubject = it },
            label = { Text("Reminder Subject") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = TitanCyan,
              unfocusedBorderColor = SlateBorder,
              focusedTextColor = TextPrimaryDark,
              unfocusedTextColor = TextPrimaryDark
            )
          )

          OutlinedTextField(
            value = reminderDueDate,
            onValueChange = { reminderDueDate = it },
            label = { Text("Due Date & Time") },
            placeholder = { Text("e.g., Tomorrow, 10:00 AM or In 3 days") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = TitanCyan,
              unfocusedBorderColor = SlateBorder,
              focusedTextColor = TextPrimaryDark,
              unfocusedTextColor = TextPrimaryDark
            )
          )

          Text("Priority Level:", fontSize = 12.sp, color = TextSecondaryDark)
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            listOf("HIGH", "MEDIUM", "LOW").forEach { p ->
              val isSelected = reminderPriority == p
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(6.dp))
                  .background(if (isSelected) TitanGold else SlateElevated)
                  .clickable { reminderPriority = p }
                  .padding(horizontal = 12.dp, vertical = 6.dp)
              ) {
                Text(
                  text = p,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (isSelected) ObsidianDark else TextPrimaryDark
                )
              }
            }
          }
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (reminderSubject.isNotBlank()) {
              viewModel.addCrmFollowUpReminder(
                contactId = contact.id,
                reminderSubject = reminderSubject,
                dueDate = reminderDueDate,
                priorityLevel = reminderPriority
              )
              selectedContactForReminder = null
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = TitanGold, contentColor = ObsidianDark),
          shape = RoundedCornerShape(8.dp)
        ) {
          Text("Add Reminder", fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { selectedContactForReminder = null }) {
          Text("Cancel", color = TextSecondaryDark)
        }
      }
    )
  }

  // DIALOG: Add New Contact
  if (showAddContactDialog) {
    AlertDialog(
      onDismissRequest = { showAddContactDialog = false },
      containerColor = SlateCard,
      title = {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Person, contentDescription = null, tint = TitanCyan)
          Spacer(modifier = Modifier.width(8.dp))
          Text("Add Recruiter / Contact", color = TextPrimaryDark, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
      },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(
            value = newName,
            onValueChange = { newName = it },
            label = { Text("Full Name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = TitanCyan,
              unfocusedBorderColor = SlateBorder,
              focusedTextColor = TextPrimaryDark,
              unfocusedTextColor = TextPrimaryDark
            )
          )
          OutlinedTextField(
            value = newTitle,
            onValueChange = { newTitle = it },
            label = { Text("Title / Role") },
            placeholder = { Text("e.g., Senior Strategy Recruiter") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = TitanCyan,
              unfocusedBorderColor = SlateBorder,
              focusedTextColor = TextPrimaryDark,
              unfocusedTextColor = TextPrimaryDark
            )
          )
          OutlinedTextField(
            value = newCompany,
            onValueChange = { newCompany = it },
            label = { Text("Company Name") },
            placeholder = { Text("e.g., Zepto / Swiggy / Google") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = TitanCyan,
              unfocusedBorderColor = SlateBorder,
              focusedTextColor = TextPrimaryDark,
              unfocusedTextColor = TextPrimaryDark
            )
          )
          OutlinedTextField(
            value = newEmail,
            onValueChange = { newEmail = it },
            label = { Text("Email (Optional)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = TitanCyan,
              unfocusedBorderColor = SlateBorder,
              focusedTextColor = TextPrimaryDark,
              unfocusedTextColor = TextPrimaryDark
            )
          )
          OutlinedTextField(
            value = newLinkedin,
            onValueChange = { newLinkedin = it },
            label = { Text("LinkedIn URL (Optional)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = TitanCyan,
              unfocusedBorderColor = SlateBorder,
              focusedTextColor = TextPrimaryDark,
              unfocusedTextColor = TextPrimaryDark
            )
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (newName.isNotBlank() && newCompany.isNotBlank()) {
              val newContact = RecruiterContact(
                id = "contact_${System.currentTimeMillis()}",
                name = newName,
                title = newTitle.ifBlank { "Talent Acquisition / Hiring Manager" },
                companyId = "comp_${newCompany.lowercase().replace(" ", "_")}",
                companyName = newCompany,
                department = "Talent Acquisition / Strategy",
                email = newEmail,
                linkedinUrl = newLinkedin,
                relationshipState = "CONNECTED",
                lastInteractionDate = "Just now",
                notes = "Saved via Professional Network CRM.",
                nextFollowUpDate = "In 7 days"
              )
              viewModel.addNewContact(newContact)
              viewModel.saveContactToNetworkCrm(newContact)
              showAddContactDialog = false
              newName = ""
              newTitle = ""
              newCompany = ""
              newEmail = ""
              newLinkedin = ""
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = TitanCyan, contentColor = ObsidianDark),
          shape = RoundedCornerShape(8.dp)
        ) {
          Text("Save to Firestore CRM", fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { showAddContactDialog = false }) {
          Text("Cancel", color = TextSecondaryDark)
        }
      }
    )
  }
}

@Composable
private fun CrmMetricCard(
  title: String,
  count: String,
  subtitle: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  tint: Color,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier,
    colors = CardDefaults.cardColors(containerColor = SlateCard),
    shape = RoundedCornerShape(10.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
  ) {
    Column(
      modifier = Modifier.padding(10.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(title, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = tint)
        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(14.dp))
      }
      Spacer(modifier = Modifier.height(4.dp))
      Text(count, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = TextPrimaryDark)
      Text(subtitle, fontSize = 9.sp, color = TextMutedDark, maxLines = 1)
    }
  }
}

@Composable
private fun CrmContactItemCard(
  bundle: CrmContactBundle,
  onLogInteraction: () -> Unit,
  onAddReminder: () -> Unit,
  onSaveToFirestore: () -> Unit,
  onComposeOutreach: () -> Unit
) {
  var isExpanded by remember { mutableStateOf(false) }
  val contact = bundle.contact

  Card(
    colors = CardDefaults.cardColors(containerColor = SlateCard),
    shape = RoundedCornerShape(12.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      // Header: Avatar, Name, Company, Status
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(40.dp)
              .clip(CircleShape)
              .background(TitanIndigo.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = contact.name.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString(""),
              color = TitanCyan,
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp
            )
          }

          Spacer(modifier = Modifier.width(10.dp))

          Column {
            Text(contact.name, fontWeight = FontWeight.Bold, color = TextPrimaryDark, fontSize = 14.sp)
            Text("${contact.title} • ${contact.companyName}", color = TextSecondaryDark, fontSize = 11.sp)
          }
        }

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(TitanCyan.copy(alpha = 0.15f))
            .padding(horizontal = 8.dp, vertical = 3.dp)
        ) {
          Text(contact.relationshipState, color = TitanCyan, fontSize = 9.sp, fontWeight = FontWeight.Bold)
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Notes & Metadata
      if (contact.notes.isNotBlank()) {
        Text(
          text = contact.notes,
          fontSize = 11.sp,
          color = TextSecondaryDark,
          lineHeight = 15.sp,
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(SlateElevated)
            .padding(8.dp)
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Interaction Counts & Expand History Button
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          modifier = Modifier.clickable { isExpanded = !isExpanded },
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
            contentDescription = null,
            tint = TitanCyan,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "Interaction History (${bundle.interactions.size})",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = TitanCyan
          )
        }

        if (bundle.reminders.any { !it.isCompleted }) {
          val next = bundle.reminders.firstOrNull { !it.isCompleted }
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Alarm, contentDescription = null, tint = TitanGold, modifier = Modifier.size(12.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(next?.dueDateFormatted ?: "Pending", fontSize = 10.sp, color = TitanGold, fontWeight = FontWeight.Bold)
          }
        }
      }

      // Expandable Interactions Timeline
      AnimatedVisibility(visible = isExpanded) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          if (bundle.interactions.isEmpty()) {
            Text("No past interactions logged yet.", fontSize = 11.sp, color = TextMutedDark)
          } else {
            bundle.interactions.forEach { item ->
              Card(
                colors = CardDefaults.cardColors(containerColor = SlateElevated),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
              ) {
                Column(modifier = Modifier.padding(10.dp)) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Box(
                      modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(TitanIndigo.copy(alpha = 0.3f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                      Text(item.interactionType, color = TitanCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                    Text(item.dateFormatted, color = TextMutedDark, fontSize = 10.sp)
                  }
                  Spacer(modifier = Modifier.height(4.dp))
                  Text(item.summaryNotes, color = TextPrimaryDark, fontSize = 11.sp)
                  if (item.nextSteps.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Next: ${item.nextSteps}", color = TitanGold, fontSize = 10.sp, fontWeight = FontWeight.Medium)
                  }
                }
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Action Buttons: Log Interaction, Add Reminder, AI Outreach, Save to Firestore
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        OutlinedButton(
          onClick = onLogInteraction,
          modifier = Modifier.weight(1f),
          shape = RoundedCornerShape(6.dp),
          colors = ButtonDefaults.outlinedButtonColors(contentColor = TitanCyan),
          border = androidx.compose.foundation.BorderStroke(1.dp, TitanCyan.copy(alpha = 0.6f)),
          contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 4.dp, horizontal = 6.dp)
        ) {
          Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(12.dp))
          Spacer(modifier = Modifier.width(3.dp))
          Text("Log", fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }

        OutlinedButton(
          onClick = onAddReminder,
          modifier = Modifier.weight(1f),
          shape = RoundedCornerShape(6.dp),
          colors = ButtonDefaults.outlinedButtonColors(contentColor = TitanGold),
          border = androidx.compose.foundation.BorderStroke(1.dp, TitanGold.copy(alpha = 0.6f)),
          contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 4.dp, horizontal = 6.dp)
        ) {
          Icon(Icons.Default.Alarm, contentDescription = null, modifier = Modifier.size(12.dp))
          Spacer(modifier = Modifier.width(3.dp))
          Text("Remind", fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }

        Button(
          onClick = onComposeOutreach,
          modifier = Modifier.weight(1f),
          shape = RoundedCornerShape(6.dp),
          colors = ButtonDefaults.buttonColors(containerColor = SlateElevated, contentColor = TitanCyan),
          contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 4.dp, horizontal = 6.dp)
        ) {
          Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(12.dp))
          Spacer(modifier = Modifier.width(3.dp))
          Text("AI Pitch", fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }

        Button(
          onClick = onSaveToFirestore,
          modifier = Modifier.weight(1f),
          shape = RoundedCornerShape(6.dp),
          colors = ButtonDefaults.buttonColors(containerColor = TitanCyan, contentColor = ObsidianDark),
          contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 4.dp, horizontal = 6.dp)
        ) {
          Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(12.dp))
          Spacer(modifier = Modifier.width(3.dp))
          Text("Save", fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}

@Composable
private fun ReminderQueueItem(
  reminder: FollowUpReminder,
  onToggleCompleted: () -> Unit
) {
  Card(
    colors = CardDefaults.cardColors(
      containerColor = if (reminder.isCompleted) SlateElevated.copy(alpha = 0.5f) else SlateCard
    ),
    shape = RoundedCornerShape(10.dp),
    border = androidx.compose.foundation.BorderStroke(
      1.dp,
      if (reminder.isCompleted) SlateBorder else TitanGold.copy(alpha = 0.5f)
    ),
    modifier = Modifier.fillMaxWidth()
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      IconButton(
        onClick = onToggleCompleted,
        modifier = Modifier.size(24.dp)
      ) {
        Icon(
          imageVector = if (reminder.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
          contentDescription = null,
          tint = if (reminder.isCompleted) TitanEmerald else TitanGold
        )
      }

      Spacer(modifier = Modifier.width(10.dp))

      Column(modifier = Modifier.weight(1f)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = reminder.reminderSubject,
            fontWeight = FontWeight.Bold,
            color = if (reminder.isCompleted) TextMutedDark else TextPrimaryDark,
            fontSize = 13.sp
          )
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(4.dp))
              .background(
                when (reminder.priorityLevel) {
                  "HIGH" -> TitanGold.copy(alpha = 0.2f)
                  "MEDIUM" -> TitanCyan.copy(alpha = 0.2f)
                  else -> TextMutedDark.copy(alpha = 0.2f)
                }
              )
              .padding(horizontal = 6.dp, vertical = 2.dp)
          ) {
            Text(
              reminder.priorityLevel,
              fontSize = 9.sp,
              fontWeight = FontWeight.Bold,
              color = when (reminder.priorityLevel) {
                "HIGH" -> TitanGold
                "MEDIUM" -> TitanCyan
                else -> TextMutedDark
              }
            )
          }
        }

        Spacer(modifier = Modifier.height(3.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = "${reminder.contactName} (${reminder.companyName})",
            fontSize = 11.sp,
            color = TextSecondaryDark
          )
          Text(" • ", color = TextMutedDark)
          Text(
            text = reminder.dueDateFormatted,
            fontSize = 10.sp,
            color = if (reminder.isCompleted) TextMutedDark else TitanGold,
            fontWeight = FontWeight.Medium
          )
        }
      }
    }
  }
}
