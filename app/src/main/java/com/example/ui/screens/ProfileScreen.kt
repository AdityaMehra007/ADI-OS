package com.example.ui.screens

import android.content.Intent
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import com.example.ui.components.CloudSyncProfileCard
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.KnowledgeEdge
import com.example.data.model.KnowledgeNode
import com.example.data.model.PersonalMemoryItem
import com.example.data.model.StarStory
import com.example.data.model.VerifiedFact
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
import com.example.ui.viewmodel.TitanViewModel

@Composable
fun ProfileScreen(
  viewModel: TitanViewModel
) {
  val context = LocalContext.current
  val profile by viewModel.userProfile.collectAsState()
  val facts by viewModel.verifiedFacts.collectAsState()
  val starStories by viewModel.stories.collectAsState()
  val memories by viewModel.personalMemories.collectAsState()
  val knowledgeNodes by viewModel.knowledgeNodes.collectAsState()
  val knowledgeEdges by viewModel.knowledgeEdges.collectAsState()

  var selectedTab by remember { mutableStateOf(0) } // 0 = Fact Store & STAR, 1 = Layered Memory, 2 = Knowledge Graph

  var showAddFactDialog by remember { mutableStateOf(false) }
  var showAddStoryDialog by remember { mutableStateOf(false) }
  var showAddMemoryDialog by remember { mutableStateOf(false) }
  var showExportDialog by remember { mutableStateOf(false) }

  var factCategory by remember { mutableStateOf("EXPERIENCE") }
  var factClaim by remember { mutableStateOf("") }
  var factEvidence by remember { mutableStateOf("") }
  var factTags by remember { mutableStateOf("") }

  var storyTitle by remember { mutableStateOf("") }
  var storyType by remember { mutableStateOf("LEADERSHIP") }
  var storySituation by remember { mutableStateOf("") }
  var storyTask by remember { mutableStateOf("") }
  var storyAction by remember { mutableStateOf("") }
  var storyResult by remember { mutableStateOf("") }

  var memLayer by remember { mutableStateOf("LONG_TERM_PREFERENCES") }
  var memTitle by remember { mutableStateOf("") }
  var memContent by remember { mutableStateOf("") }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(ObsidianDark)
      .padding(horizontal = 16.dp, vertical = 8.dp)
      .testTag("profile_screen")
  ) {
    // Header
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Text(
          text = "PROFILE GRAPH & VAULT",
          style = MaterialTheme.typography.labelSmall,
          color = TitanCyan,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = "Zero-Hallucination Verified Store",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
          color = TextPrimaryDark
        )
      }

      Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        Button(
          onClick = {
            when (selectedTab) {
              0 -> showAddFactDialog = true
              1 -> showAddMemoryDialog = true
              2 -> showExportDialog = true
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = TitanCyan, contentColor = ObsidianDark),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier.testTag("primary_profile_action_button")
        ) {
          Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = when (selectedTab) {
              0 -> "Add Fact"
              1 -> "Add Memory"
              else -> "Export Graph"
            },
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Scrollable Tabs
    ScrollableTabRow(
      selectedTabIndex = selectedTab,
      containerColor = SlateCard,
      edgePadding = 0.dp,
      indicator = { tabPositions ->
        TabRowDefaults.SecondaryIndicator(
          Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
          color = TitanCyan
        )
      }
    ) {
      Tab(
        selected = selectedTab == 0,
        onClick = { selectedTab = 0 },
        text = {
          Text(
            "Fact Store & STAR (${facts.size})",
            color = if (selectedTab == 0) TitanCyan else TextSecondaryDark,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp
          )
        }
      )
      Tab(
        selected = selectedTab == 1,
        onClick = { selectedTab = 1 },
        text = {
          Text(
            "Layered Memory (${memories.size})",
            color = if (selectedTab == 1) TitanGold else TextSecondaryDark,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp
          )
        }
      )
      Tab(
        selected = selectedTab == 2,
        onClick = { selectedTab = 2 },
        text = {
          Text(
            "Knowledge Graph (${knowledgeNodes.size})",
            color = if (selectedTab == 2) TitanEmerald else TextSecondaryDark,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp
          )
        }
      )
    }

    Spacer(modifier = Modifier.height(12.dp))

    when (selectedTab) {
      0 -> {
        // Tab 0: Profile Summary, Facts, STAR Stories
        LazyColumn(
          modifier = Modifier.fillMaxSize(),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          // Cloud Sync & Firebase Account Card
          item {
            CloudSyncProfileCard(viewModel)
          }

          // AI Document Parser & Resume Optimizer Lab
          item {
            Card(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, TitanCyan.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                .clickable { viewModel.openResumeOptimizer() }
                .testTag("profile_resume_optimizer_banner"),
              colors = CardDefaults.cardColors(containerColor = SlateCard)
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(
                  modifier = Modifier.weight(1f),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Box(
                    modifier = Modifier
                      .size(38.dp)
                      .clip(CircleShape)
                      .background(TitanCyan.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                  ) {
                    Icon(
                      imageVector = Icons.Default.Description,
                      contentDescription = null,
                      tint = TitanCyan,
                      modifier = Modifier.size(20.dp)
                    )
                  }
                  Spacer(modifier = Modifier.width(10.dp))
                  Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Text(
                        text = "RESUME OPTIMIZATION LAB",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = TitanCyan,
                        letterSpacing = 1.sp
                      )
                      Spacer(modifier = Modifier.width(6.dp))
                      Box(
                        modifier = Modifier
                          .clip(RoundedCornerShape(4.dp))
                          .background(TitanEmerald.copy(alpha = 0.2f))
                          .padding(horizontal = 4.dp, vertical = 1.dp)
                      ) {
                        Text(
                          text = "GEMINI 3.5 FLASH",
                          fontSize = 8.sp,
                          fontWeight = FontWeight.Bold,
                          color = TitanEmerald
                        )
                      }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                      text = "Document Parser & AI Feedback Engine",
                      fontSize = 13.sp,
                      fontWeight = FontWeight.Bold,
                      color = TextPrimaryDark
                    )
                    Text(
                      text = "Ingest local documents (TXT, MD, PDF) and generate constructive ATS bullet upgrades.",
                      fontSize = 11.sp,
                      color = TextSecondaryDark
                    )
                  }
                }

                Button(
                  onClick = { viewModel.openResumeOptimizer() },
                  colors = ButtonDefaults.buttonColors(containerColor = TitanCyan),
                  shape = RoundedCornerShape(8.dp),
                  modifier = Modifier.padding(start = 6.dp)
                ) {
                  Text(
                    text = "Launch Lab",
                    color = ObsidianDark,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                  )
                }
              }
            }
          }

          // Profile Card
          item {
            Card(
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(12.dp),
              colors = CardDefaults.cardColors(containerColor = SlateCard),
              border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
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
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(TitanCyan),
                      contentAlignment = Alignment.Center
                    ) {
                      Text("A", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = ObsidianDark)
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                      Text(profile?.name ?: "Adi", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                      Text("${profile?.educationDegree} • ${profile?.educationSpecialization}", style = MaterialTheme.typography.bodySmall, color = TitanCyan, fontSize = 11.sp)
                    }
                  }

                  Box(
                    modifier = Modifier
                      .clip(RoundedCornerShape(8.dp))
                      .background(SlateElevated)
                      .padding(horizontal = 8.dp, vertical = 4.dp)
                  ) {
                    Text("Completeness: ${profile?.profileCompleteness ?: 96}%", style = MaterialTheme.typography.labelSmall, color = TitanEmerald, fontWeight = FontWeight.Bold)
                  }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                  text = "University: ${profile?.university ?: "Bengaluru University"}",
                  style = MaterialTheme.typography.bodySmall,
                  color = TextSecondaryDark,
                  fontSize = 11.sp
                )
                Text(
                  text = "Location: ${profile?.location ?: "Bengaluru, India"}",
                  style = MaterialTheme.typography.bodySmall,
                  color = TextSecondaryDark,
                  fontSize = 11.sp
                )
                Text(
                  text = "Target Roles: ${profile?.targetRoles}",
                  style = MaterialTheme.typography.bodySmall,
                  color = TextSecondaryDark,
                  fontSize = 11.sp
                )
              }
            }
          }

          // Verified Facts
          item {
            Text(
              text = "VERIFIED FACT STORE (${facts.size} ENTRIES):",
              style = MaterialTheme.typography.labelSmall,
              color = TitanCyan,
              fontWeight = FontWeight.Bold
            )
          }

          items(facts, key = { it.id }) { fact ->
            FactCard(fact = fact)
          }

          // Behavioral STAR Stories
          item {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = "BEHAVIORAL STAR REPOSITORY (${starStories.size} STORIES):",
              style = MaterialTheme.typography.labelSmall,
              color = TitanGold,
              fontWeight = FontWeight.Bold
            )
          }

          items(starStories, key = { it.id }) { story ->
            ProfileStarStoryCard(story = story)
          }

          item { Spacer(modifier = Modifier.height(16.dp)) }
        }
      }
      1 -> {
        // Tab 1: Layered Personal Memory
        LazyColumn(
          modifier = Modifier.fillMaxSize(),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          item {
            Card(
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(10.dp),
              colors = CardDefaults.cardColors(containerColor = SlateElevated),
              border = androidx.compose.foundation.BorderStroke(1.dp, TitanGold.copy(alpha = 0.5f))
            ) {
              Column(modifier = Modifier.padding(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(Icons.Default.Memory, contentDescription = null, tint = TitanGold, modifier = Modifier.size(16.dp))
                  Spacer(modifier = Modifier.width(6.dp))
                  Text("LAYERED PERSONAL MEMORY ENGINE", style = MaterialTheme.typography.labelSmall, color = TitanGold, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = "Preserves user preferences, verified facts, active task contexts, and career decisions with strict user control (View, Edit, Delete).",
                  style = MaterialTheme.typography.bodySmall,
                  color = TextSecondaryDark,
                  fontSize = 10.sp
                )
              }
            }
          }

          items(memories, key = { it.id }) { mem ->
            MemoryCard(
              memory = mem,
              onDelete = { viewModel.deleteMemory(mem.id) }
            )
          }

          item { Spacer(modifier = Modifier.height(16.dp)) }
        }
      }
      2 -> {
        // Tab 2: Knowledge Graph Browser
        LazyColumn(
          modifier = Modifier.fillMaxSize(),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          item {
            Card(
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(10.dp),
              colors = CardDefaults.cardColors(containerColor = SlateElevated),
              border = androidx.compose.foundation.BorderStroke(1.dp, TitanEmerald.copy(alpha = 0.5f))
            ) {
              Column(modifier = Modifier.padding(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(Icons.Default.Hub, contentDescription = null, tint = TitanEmerald, modifier = Modifier.size(16.dp))
                  Spacer(modifier = Modifier.width(6.dp))
                  Text("CROSS-DOMAIN KNOWLEDGE GRAPH", style = MaterialTheme.typography.labelSmall, color = TitanEmerald, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = "Represents semantic multi-hop relationships across Identity, Education, Operations, Skills, Target Companies, and Active Missions.",
                  style = MaterialTheme.typography.bodySmall,
                  color = TextSecondaryDark,
                  fontSize = 10.sp
                )
              }
            }
          }

          item {
            Text("SEMANTIC ENTITY NODES (${knowledgeNodes.size})", style = MaterialTheme.typography.labelSmall, color = TitanCyan, fontWeight = FontWeight.Bold)
          }

          items(knowledgeNodes, key = { it.id }) { node ->
            KnowledgeNodeCard(node = node)
          }

          item {
            Spacer(modifier = Modifier.height(6.dp))
            Text("GRAPH RELATIONSHIP EDGES (${knowledgeEdges.size})", style = MaterialTheme.typography.labelSmall, color = TitanGold, fontWeight = FontWeight.Bold)
          }

          items(knowledgeEdges, key = { it.id }) { edge ->
            KnowledgeEdgeCard(edge = edge)
          }

          item { Spacer(modifier = Modifier.height(16.dp)) }
        }
      }
    }
  }

  // Add Fact Dialog
  if (showAddFactDialog) {
    AlertDialog(
      onDismissRequest = { showAddFactDialog = false },
      containerColor = SlateCard,
      title = { Text("Add Verified Fact", color = TextPrimaryDark, fontWeight = FontWeight.Bold) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(
            value = factCategory,
            onValueChange = { factCategory = it },
            label = { Text("Category (EXPERIENCE, EDUCATION, PROJECT, SKILL)") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimaryDark, unfocusedTextColor = TextSecondaryDark)
          )
          OutlinedTextField(
            value = factClaim,
            onValueChange = { factClaim = it },
            label = { Text("Verified Claim / Achievement") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimaryDark, unfocusedTextColor = TextSecondaryDark)
          )
          OutlinedTextField(
            value = factEvidence,
            onValueChange = { factEvidence = it },
            label = { Text("Evidence / Metrics / Transcripts") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimaryDark, unfocusedTextColor = TextSecondaryDark)
          )
          OutlinedTextField(
            value = factTags,
            onValueChange = { factTags = it },
            label = { Text("Search Tags (comma-separated)") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimaryDark, unfocusedTextColor = TextSecondaryDark)
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (factClaim.isNotBlank()) {
              viewModel.addFact(factCategory, factClaim, factEvidence, factTags)
              factClaim = ""
              factEvidence = ""
              factTags = ""
              showAddFactDialog = false
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = TitanCyan, contentColor = ObsidianDark)
        ) {
          Text("Save Verified Fact", fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { showAddFactDialog = false }) { Text("Cancel", color = TextMutedDark) }
      }
    )
  }

  // Add Memory Dialog
  if (showAddMemoryDialog) {
    AlertDialog(
      onDismissRequest = { showAddMemoryDialog = false },
      containerColor = SlateCard,
      title = { Text("Add Personal Memory", color = TextPrimaryDark, fontWeight = FontWeight.Bold) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(
            value = memLayer,
            onValueChange = { memLayer = it },
            label = { Text("Memory Layer (LONG_TERM_PREFERENCES, DECISION, etc.)") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimaryDark, unfocusedTextColor = TextSecondaryDark)
          )
          OutlinedTextField(
            value = memTitle,
            onValueChange = { memTitle = it },
            label = { Text("Title / Key") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimaryDark, unfocusedTextColor = TextSecondaryDark)
          )
          OutlinedTextField(
            value = memContent,
            onValueChange = { memContent = it },
            label = { Text("Context & Memory Content") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimaryDark, unfocusedTextColor = TextSecondaryDark)
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (memTitle.isNotBlank()) {
              viewModel.addMemory(memLayer, memTitle, memContent, "User / Control Room")
              memTitle = ""
              memContent = ""
              showAddMemoryDialog = false
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = TitanGold, contentColor = ObsidianDark)
        ) {
          Text("Store Memory", fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { showAddMemoryDialog = false }) { Text("Cancel", color = TextMutedDark) }
      }
    )
  }
}

@Composable
fun FactCard(fact: VerifiedFact) {
  Card(
    modifier = Modifier.fillMaxWidth(),
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
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Verified, contentDescription = null, tint = TitanEmerald, modifier = Modifier.size(14.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text(fact.category, style = MaterialTheme.typography.labelSmall, color = TitanEmerald, fontWeight = FontWeight.Bold, fontSize = 9.sp)
        }
        Text("VERIFIED #00${fact.id}", style = MaterialTheme.typography.labelSmall, color = TextMutedDark, fontSize = 8.sp)
      }

      Spacer(modifier = Modifier.height(4.dp))
      Text(fact.claim, style = MaterialTheme.typography.bodyMedium, color = TextPrimaryDark, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
      Spacer(modifier = Modifier.height(4.dp))
      Text("Evidence: ${fact.evidenceDetails}", style = MaterialTheme.typography.bodySmall, color = TextSecondaryDark, fontSize = 10.sp)
    }
  }
}

@Composable
private fun ProfileStarStoryCard(story: StarStory) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(10.dp),
    colors = CardDefaults.cardColors(containerColor = SlateCard),
    border = androidx.compose.foundation.BorderStroke(1.dp, TitanGold.copy(alpha = 0.4f))
  ) {
    Column(modifier = Modifier.padding(12.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(story.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(SlateElevated)
            .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
          Text(story.storyType, style = MaterialTheme.typography.labelSmall, color = TitanGold, fontWeight = FontWeight.Bold, fontSize = 8.sp)
        }
      }

      Spacer(modifier = Modifier.height(6.dp))
      Text("• SITUATION: ${story.situation}", style = MaterialTheme.typography.bodySmall, color = TextSecondaryDark, fontSize = 10.sp)
      Text("• TASK: ${story.task}", style = MaterialTheme.typography.bodySmall, color = TextSecondaryDark, fontSize = 10.sp)
      Text("• ACTION: ${story.action}", style = MaterialTheme.typography.bodySmall, color = TitanCyan, fontSize = 10.sp)
      Text("• RESULT: ${story.result}", style = MaterialTheme.typography.bodySmall, color = TitanEmerald, fontSize = 10.sp)
    }
  }
}

@Composable
fun MemoryCard(
  memory: PersonalMemoryItem,
  onDelete: () -> Unit
) {
  Card(
    modifier = Modifier.fillMaxWidth(),
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
          Text(memory.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
          Text("[${memory.memoryLayer}] • ${memory.timestamp}", style = MaterialTheme.typography.labelSmall, color = TitanGold, fontSize = 9.sp)
        }

        IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
          Icon(Icons.Default.Delete, contentDescription = "Delete", tint = TextMutedDark, modifier = Modifier.size(16.dp))
        }
      }

      Spacer(modifier = Modifier.height(4.dp))
      Text(memory.content, style = MaterialTheme.typography.bodySmall, color = TextSecondaryDark, fontSize = 11.sp, lineHeight = 15.sp)
      Spacer(modifier = Modifier.height(4.dp))
      Text("Source: ${memory.sourceAgent} • Confidence: ${memory.confidence}%", style = MaterialTheme.typography.labelSmall, color = TextMutedDark, fontSize = 9.sp)
    }
  }
}

@Composable
fun KnowledgeNodeCard(node: KnowledgeNode) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(8.dp),
    colors = CardDefaults.cardColors(containerColor = SlateCard),
    border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(10.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(node.label, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = TextPrimaryDark, fontSize = 12.sp)
          Spacer(modifier = Modifier.width(6.dp))
          Text("[${node.nodeType}]", style = MaterialTheme.typography.labelSmall, color = TitanCyan, fontSize = 9.sp)
        }
        Text(node.details, style = MaterialTheme.typography.bodySmall, color = TextSecondaryDark, fontSize = 10.sp)
      }

      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(6.dp))
          .background(SlateElevated)
          .padding(horizontal = 6.dp, vertical = 2.dp)
      ) {
        Text("${node.significanceScore} PT", style = MaterialTheme.typography.labelSmall, color = TitanEmerald, fontWeight = FontWeight.Bold, fontSize = 8.sp)
      }
    }
  }
}

@Composable
fun KnowledgeEdgeCard(edge: KnowledgeEdge) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(6.dp),
    colors = CardDefaults.cardColors(containerColor = SlateElevated),
    border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder.copy(alpha = 0.5f))
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text("${edge.sourceId} ──[${edge.relationshipType}]──► ${edge.targetId}", style = MaterialTheme.typography.bodySmall, color = TitanCyan, fontSize = 10.sp, fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace)
      Text("wt: ${edge.weight}", style = MaterialTheme.typography.labelSmall, color = TextMutedDark, fontSize = 8.sp)
    }
  }
}
