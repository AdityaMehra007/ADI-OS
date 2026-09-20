package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
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
import com.example.data.model.RecruiterContact
import com.example.ui.components.LinkedInIntegrationView
import com.example.ui.components.NetworkGrowthChart
import com.example.ui.components.ProfessionalNetworkCrmView
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
fun NetworkScreen(
  viewModel: TitanViewModel
) {
  val contacts by viewModel.contacts.collectAsState()
  val companies by viewModel.companies.collectAsState()
  val outreachDraft by viewModel.outreachDraft.collectAsState()
  val isGeneratingOutreach by viewModel.isGeneratingOutreach.collectAsState()

  var selectedContactForOutreach by remember { mutableStateOf<RecruiterContact?>(null) }
  var showAddContactDialog by remember { mutableStateOf(false) }
  var networkTab by remember { mutableStateOf(0) } // 0: Professional CRM, 1: Network Directory & Growth

  var newContactName by remember { mutableStateOf("") }
  var newContactTitle by remember { mutableStateOf("") }
  var newContactCompany by remember { mutableStateOf("") }
  var newContactEmail by remember { mutableStateOf("") }
  var newContactLinkedin by remember { mutableStateOf("") }
  var newContactNotes by remember { mutableStateOf("Met via talent network / referral.") }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(ObsidianDark)
      .padding(horizontal = 16.dp, vertical = 8.dp)
      .testTag("network_screen")
  ) {
    // Header
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Text(
          text = "RECRUITER & NETWORK CRM",
          style = MaterialTheme.typography.labelSmall,
          color = TitanCyan,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = "${contacts.size} Strategic Contacts",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
          color = TextPrimaryDark
        )
      }

      Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Button(
          onClick = { viewModel.openReferralOutreachDialog() },
          colors = ButtonDefaults.buttonColors(containerColor = TitanEmerald, contentColor = ObsidianDark),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier.testTag("open_outreach_dialog_button")
        ) {
          Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, modifier = Modifier.size(13.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Outreach Lab", fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }

        Button(
          onClick = { showAddContactDialog = true },
          colors = ButtonDefaults.buttonColors(containerColor = TitanCyan, contentColor = ObsidianDark),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier.testTag("add_contact_button")
        ) {
          Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Add", fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Top Level Tabs
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(8.dp))
        .background(SlateCard)
        .padding(3.dp),
      horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
      listOf("CRM & Contacts", "Growth & Directory", "LinkedIn Sync").forEachIndexed { index, title ->
        val isSelected = networkTab == index
        Box(
          modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(6.dp))
            .background(if (isSelected) SlateElevated else Color.Transparent)
            .clickable { networkTab = index }
            .padding(vertical = 8.dp),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = title,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) TitanCyan else TextMutedDark
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    when (networkTab) {
      0 -> {
        LazyColumn(
          modifier = Modifier.fillMaxSize(),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          item {
            LinkedInIntegrationView(viewModel = viewModel)
          }
          item {
            ProfessionalNetworkCrmView(viewModel = viewModel)
          }
          item {
            Spacer(modifier = Modifier.height(16.dp))
          }
        }
      }
      1 -> {
        LazyColumn(
          modifier = Modifier.fillMaxSize(),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          item {
            NetworkGrowthChart()
          }

        items(contacts, key = { it.id }) { contact ->
          ContactCard(
            contact = contact,
            onComposeOutreach = {
              selectedContactForOutreach = contact
              val comp = companies.find { it.id == contact.companyId } ?: Company(
                id = contact.companyId,
                name = contact.companyName,
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
              viewModel.generateOutreachForContact(contact, comp)
            }
          )
        }

        item {
          Spacer(modifier = Modifier.height(16.dp))
        }
      }
    }
    2 -> {
      LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        item {
          LinkedInIntegrationView(viewModel = viewModel)
        }
        item {
          Spacer(modifier = Modifier.height(16.dp))
        }
      }
    }
  }
}

  // AI Outreach Composer Modal
  if (selectedContactForOutreach != null) {
    AlertDialog(
      onDismissRequest = {
        selectedContactForOutreach = null
        viewModel.outreachDraft.value = null
      },
      containerColor = SlateElevated,
      title = {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = TitanCyan, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("AI Recruiter Outreach", style = MaterialTheme.typography.titleMedium, color = TextPrimaryDark)
          }
          IconButton(onClick = {
            selectedContactForOutreach = null
            viewModel.outreachDraft.value = null
          }) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondaryDark)
          }
        }
      },
      text = {
        Column(modifier = Modifier.fillMaxWidth()) {
          Text(
            text = "To: ${selectedContactForOutreach?.name} (${selectedContactForOutreach?.title})",
            style = MaterialTheme.typography.labelSmall,
            color = TitanGold,
            fontWeight = FontWeight.Bold
          )

          Spacer(modifier = Modifier.height(10.dp))

          if (isGeneratingOutreach) {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .height(140.dp),
              contentAlignment = Alignment.Center
            ) {
              CircularProgressIndicator(color = TitanCyan, modifier = Modifier.size(28.dp))
            }
          } else {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(SlateCard)
                .border(1.dp, SlateBorder, RoundedCornerShape(8.dp))
                .padding(10.dp)
            ) {
              Text(
                text = outreachDraft ?: "Generating message...",
                style = MaterialTheme.typography.bodySmall,
                color = TextPrimaryDark,
                lineHeight = 18.sp
              )
            }
          }
        }
      },
      confirmButton = {
        Button(
          onClick = {
            selectedContactForOutreach = null
            viewModel.outreachDraft.value = null
          },
          colors = ButtonDefaults.buttonColors(containerColor = TitanCyan, contentColor = ObsidianDark),
          shape = RoundedCornerShape(8.dp)
        ) {
          Text("Copy & Mark Follow-Up", fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = {
          selectedContactForOutreach = null
          viewModel.outreachDraft.value = null
        }) {
          Text("Cancel", color = TextSecondaryDark)
        }
      }
    )
  }

  // Add Recruiter Contact Dialog
  if (showAddContactDialog) {
    AlertDialog(
      onDismissRequest = { showAddContactDialog = false },
      containerColor = SlateElevated,
      title = {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Person, contentDescription = null, tint = TitanCyan, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Add Recruiter / Sponsor", style = MaterialTheme.typography.titleMedium, color = TextPrimaryDark)
          }
          IconButton(onClick = { showAddContactDialog = false }) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondaryDark)
          }
        }
      },
      text = {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          OutlinedTextField(
            value = newContactName,
            onValueChange = { newContactName = it },
            label = { Text("Contact Full Name (e.g. Priya Sharma)") },
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
            value = newContactTitle,
            onValueChange = { newContactTitle = it },
            label = { Text("Title (e.g. Talent Partner / Strategy Lead)") },
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
            value = newContactCompany,
            onValueChange = { newContactCompany = it },
            label = { Text("Company Name (e.g. Google India, Bain)") },
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
            value = newContactLinkedin,
            onValueChange = { newContactLinkedin = it },
            label = { Text("LinkedIn Profile URL") },
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
            value = newContactNotes,
            onValueChange = { newContactNotes = it },
            label = { Text("Relationship Context & Notes") },
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
            if (newContactName.isNotBlank() && newContactCompany.isNotBlank()) {
              val contact = RecruiterContact(
                id = "contact_${System.currentTimeMillis()}",
                companyId = "comp_${newContactCompany.lowercase().replace(" ", "_")}",
                companyName = newContactCompany.trim(),
                name = newContactName.trim(),
                title = if (newContactTitle.isBlank()) "Talent Acquisition Lead" else newContactTitle.trim(),
                department = "Human Resources & Talent Acquisition",
                linkedinUrl = newContactLinkedin.trim(),
                email = newContactEmail.trim(),
                relationshipState = "AWARE",
                lastInteractionDate = "2026-09-01",
                notes = newContactNotes.trim(),
                nextFollowUpDate = "2026-09-08"
              )
              viewModel.addNewContact(contact)
              showAddContactDialog = false
              newContactName = ""
              newContactCompany = ""
              newContactTitle = ""
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = TitanCyan, contentColor = ObsidianDark),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier.testTag("save_contact_button")
        ) {
          Text("Save Contact", fontWeight = FontWeight.Bold)
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
fun ContactCard(
  contact: RecruiterContact,
  onComposeOutreach: () -> Unit
) {
  val stateColor = when (contact.relationshipState) {
    "STRONG_RELATIONSHIP" -> TitanEmerald
    "REFERRAL" -> TitanGold
    "CONVERSATION" -> TitanCyan
    "AWARE" -> TitanIndigo
    else -> TextMutedDark
  }

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("contact_card_${contact.id}"),
    shape = RoundedCornerShape(10.dp),
    colors = CardDefaults.cardColors(containerColor = SlateCard),
    border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
  ) {
    Column(modifier = Modifier.padding(12.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = contact.name,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = TextPrimaryDark
          )
          Text(
            text = "${contact.title} • ${contact.companyName}",
            style = MaterialTheme.typography.bodySmall,
            color = TitanCyan,
            fontSize = 11.sp
          )
        }

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(stateColor.copy(alpha = 0.15f))
            .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
          Text(
            text = contact.relationshipState,
            style = MaterialTheme.typography.labelSmall,
            color = stateColor,
            fontWeight = FontWeight.Bold,
            fontSize = 8.sp
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = "CRM NOTES: ${contact.notes}",
        style = MaterialTheme.typography.bodySmall,
        color = TextSecondaryDark,
        fontSize = 10.sp
      )

      Spacer(modifier = Modifier.height(10.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Next follow-up: ${contact.nextFollowUpDate}",
          style = MaterialTheme.typography.labelSmall,
          color = TitanGold,
          fontSize = 9.sp
        )

        Button(
          onClick = { onComposeOutreach() },
          colors = ButtonDefaults.buttonColors(containerColor = SlateElevated, contentColor = TitanCyan),
          shape = RoundedCornerShape(6.dp),
          modifier = Modifier.testTag("compose_outreach_btn_${contact.id}")
        ) {
          Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(12.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Draft Outreach", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, fontSize = 10.sp)
        }
      }
    }
  }
}
