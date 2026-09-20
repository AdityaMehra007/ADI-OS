package com.example.ui.components

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.PhoneInTalk
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.SalaryNegotiationBundle
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
import com.example.ui.theme.TitanViolet
import com.example.ui.viewmodel.TitanViewModel
import com.example.util.SalaryBenchmarkNegotiationUtility
import kotlin.math.roundToInt

@Composable
fun SalaryBenchmarkNegotiationDialog(
  viewModel: TitanViewModel,
  onDismiss: () -> Unit
) {
  val activeBundle by viewModel.activeSalaryNegotiationBundle.collectAsState()
  val isGenerating by viewModel.isGeneratingSalaryNegotiation.collectAsState()
  val profile by viewModel.userProfile.collectAsState()

  val clipboardManager = LocalClipboardManager.current
  val context = LocalContext.current

  var copyToastVisible by remember { mutableStateOf(false) }
  var toastMessage by remember { mutableStateOf("Copied to clipboard!") }

  // Input states
  var companyInput by remember { mutableStateOf(activeBundle?.targetCompany ?: "Zepto") }
  var roleInput by remember { mutableStateOf(activeBundle?.targetRole ?: "Lead - Strategy & Dark Store Operations") }
  var jdInput by remember {
    mutableStateOf(
      SalaryBenchmarkNegotiationUtility.PRESET_BENCHMARK_JOBS.first().description
    )
  }
  var currentCtcInput by remember { mutableDoubleStateOf(18.0) }
  var targetCtcInput by remember { mutableDoubleStateOf(42.0) }
  var isConfigExpanded by remember { mutableStateOf(false) }
  var selectedScenarioTab by remember { mutableIntStateOf(0) } // 0: Initial Counter, 1: Budget Ceiling, 2: Competing Offer, 3: Phone Call

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = Modifier
        .fillMaxWidth(0.96f)
        .fillMaxHeight(0.94f)
        .clip(RoundedCornerShape(20.dp))
        .border(1.dp, SlateBorder, RoundedCornerShape(20.dp))
        .testTag("salary_benchmark_negotiation_dialog"),
      color = ObsidianDark
    ) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(16.dp)
      ) {
        // Dialog Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(TitanEmerald.copy(alpha = 0.15f))
                .border(1.dp, TitanEmerald.copy(alpha = 0.3f), CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.MonetizationOn,
                contentDescription = null,
                tint = TitanEmerald,
                modifier = Modifier.size(20.dp)
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = "DATA-DRIVEN SALARY BENCHMARK & NEGOTIATION SCRIPT",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = TitanEmerald,
                  letterSpacing = 1.sp
                )
                if (activeBundle?.isAiGenerated == true) {
                  Spacer(modifier = Modifier.width(6.dp))
                  Box(
                    modifier = Modifier
                      .clip(RoundedCornerShape(4.dp))
                      .background(TitanCyan.copy(alpha = 0.2f))
                      .padding(horizontal = 5.dp, vertical = 1.dp)
                  ) {
                    Text(
                      text = "Gemini 3.5 AI",
                      fontSize = 9.sp,
                      color = TitanCyan,
                      fontWeight = FontWeight.Bold
                    )
                  }
                }
              }
              Text(
                text = "${activeBundle?.targetCompany ?: companyInput} • ${activeBundle?.targetRole ?: roleInput}",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimaryDark
              )
            }
          }

          IconButton(
            onClick = onDismiss,
            modifier = Modifier.size(32.dp).testTag("close_negotiation_dialog_button")
          ) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondaryDark)
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Main Scrollable Area
        Column(
          modifier = Modifier
            .weight(1f)
            .verticalScroll(rememberScrollState())
        ) {
          // 1. Config & Target JD Section (Collapsible)
          Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = SlateCard),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, SlateBorder)
          ) {
            Column(modifier = Modifier.padding(12.dp)) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .clickable { isConfigExpanded = !isConfigExpanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(Icons.Default.Description, contentDescription = null, tint = TitanCyan, modifier = Modifier.size(14.dp))
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = "TARGET ROLE, COMPANY & JOB DESCRIPTION",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TitanCyan
                  )
                }
                Icon(
                  imageVector = if (isConfigExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                  contentDescription = null,
                  tint = TextMutedDark,
                  modifier = Modifier.size(18.dp)
                )
              }

              // Preset JD chips
              Spacer(modifier = Modifier.height(8.dp))
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                SalaryBenchmarkNegotiationUtility.PRESET_BENCHMARK_JOBS.forEach { preset ->
                  val isSelected = companyInput.equals(preset.company, ignoreCase = true)
                  Box(
                    modifier = Modifier
                      .clip(RoundedCornerShape(6.dp))
                      .background(if (isSelected) TitanEmerald.copy(alpha = 0.2f) else SlateElevated)
                      .border(
                        1.dp,
                        if (isSelected) TitanEmerald else SlateBorder,
                        RoundedCornerShape(6.dp)
                      )
                      .clickable {
                        companyInput = preset.company
                        roleInput = preset.role
                        jdInput = preset.description
                      }
                      .padding(horizontal = 8.dp, vertical = 4.dp)
                  ) {
                    Text(
                      text = "${preset.company} (${preset.role.take(16)}...)",
                      fontSize = 10.sp,
                      color = if (isSelected) TitanEmerald else TextSecondaryDark,
                      fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                  }
                }
              }

              AnimatedVisibility(visible = isConfigExpanded) {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                  ) {
                    OutlinedTextField(
                      value = companyInput,
                      onValueChange = { companyInput = it },
                      label = { Text("Company", fontSize = 10.sp) },
                      modifier = Modifier.weight(1f),
                      colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TitanCyan,
                        unfocusedBorderColor = SlateBorder,
                        focusedTextColor = TextPrimaryDark,
                        unfocusedTextColor = TextSecondaryDark
                      ),
                      shape = RoundedCornerShape(8.dp),
                      textStyle = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp)
                    )

                    OutlinedTextField(
                      value = roleInput,
                      onValueChange = { roleInput = it },
                      label = { Text("Target Role", fontSize = 10.sp) },
                      modifier = Modifier.weight(1f),
                      colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TitanCyan,
                        unfocusedBorderColor = SlateBorder,
                        focusedTextColor = TextPrimaryDark,
                        unfocusedTextColor = TextSecondaryDark
                      ),
                      shape = RoundedCornerShape(8.dp),
                      textStyle = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp)
                    )
                  }

                  Spacer(modifier = Modifier.height(8.dp))

                  OutlinedTextField(
                    value = jdInput,
                    onValueChange = { jdInput = it },
                    label = { Text("Job Description (Paste or Edit)", fontSize = 10.sp) },
                    modifier = Modifier
                      .fillMaxWidth()
                      .height(110.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                      focusedBorderColor = TitanCyan,
                      unfocusedBorderColor = SlateBorder,
                      focusedTextColor = TextPrimaryDark,
                      unfocusedTextColor = TextSecondaryDark
                    ),
                    shape = RoundedCornerShape(8.dp),
                    textStyle = MaterialTheme.typography.bodySmall.copy(
                      fontSize = 10.sp,
                      fontFamily = FontFamily.Monospace,
                      lineHeight = 14.sp
                    )
                  )
                }
              }

              Spacer(modifier = Modifier.height(10.dp))

              // Trigger Benchmark Button
              Button(
                onClick = {
                  viewModel.generateSalaryNegotiationPlan(
                    role = roleInput,
                    company = companyInput,
                    jobDescription = jdInput,
                    currentCtcLakhs = currentCtcInput,
                    requestedTargetCtcLakhs = targetCtcInput
                  )
                },
                colors = ButtonDefaults.buttonColors(containerColor = TitanEmerald),
                shape = RoundedCornerShape(8.dp),
                enabled = !isGenerating,
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("run_salary_negotiation_benchmark_button")
              ) {
                if (isGenerating) {
                  CircularProgressIndicator(modifier = Modifier.size(16.dp), color = ObsidianDark, strokeWidth = 2.dp)
                  Spacer(modifier = Modifier.width(8.dp))
                  Text("Benchmarking Market Data & Drafting Scripts...", color = ObsidianDark, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                } else {
                  Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = ObsidianDark, modifier = Modifier.size(16.dp))
                  Spacer(modifier = Modifier.width(6.dp))
                  Text("Benchmark Market & Synthesize Negotiation Scripts", color = ObsidianDark, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // 2. Active Benchmark Intelligence Card
          activeBundle?.let { bundle ->
            val analysis = bundle.benchmarkAnalysis

            Card(
              modifier = Modifier.fillMaxWidth(),
              colors = CardDefaults.cardColors(containerColor = SlateCard),
              shape = RoundedCornerShape(14.dp),
              border = BorderStroke(1.dp, SlateBorder)
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
                        .clip(RoundedCornerShape(6.dp))
                        .background(TitanGold.copy(alpha = 0.2f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                      Text(
                        text = analysis.detectedTier.label,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = TitanGold
                      )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                      modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(TitanCyan.copy(alpha = 0.2f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                      Text(
                        text = analysis.detectedSeniority.levelCode,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = TitanCyan
                      )
                    }
                  }

                  // Leverage Badge
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Security, contentDescription = null, tint = TitanEmerald, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                      text = "${analysis.candidateLeverageScore}% Leverage",
                      fontSize = 11.sp,
                      fontWeight = FontWeight.Bold,
                      color = TitanEmerald
                    )
                  }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Percentile Benchmark Grid
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                  PercentileMiniCard(
                    title = "25th %ile",
                    totalComp = "₹${analysis.p25TotalLakhs.roundToInt()}L",
                    base = "Base: ₹${analysis.p25BaseLakhs.roundToInt()}L",
                    color = TextSecondaryDark,
                    modifier = Modifier.weight(1f)
                  )
                  PercentileMiniCard(
                    title = "Median (50th)",
                    totalComp = "₹${analysis.p50TotalLakhs.roundToInt()}L",
                    base = "Base: ₹${analysis.p50BaseLakhs.roundToInt()}L",
                    color = TitanCyan,
                    modifier = Modifier.weight(1f)
                  )
                  PercentileMiniCard(
                    title = "🎯 Target (75th)",
                    totalComp = "₹${analysis.p75TotalLakhs.roundToInt()}L",
                    base = "Base: ₹${analysis.p75BaseLakhs.roundToInt()}L",
                    color = TitanEmerald,
                    isRecommended = true,
                    modifier = Modifier.weight(1f)
                  )
                  PercentileMiniCard(
                    title = "90th Stretch",
                    totalComp = "₹${analysis.p90TotalLakhs.roundToInt()}L",
                    base = "Base: ₹${analysis.p90BaseLakhs.roundToInt()}L",
                    color = TitanGold,
                    modifier = Modifier.weight(1f)
                  )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Equity and Sign-on details
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(ObsidianDark)
                    .border(1.dp, SlateBorder, RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Payments, contentDescription = null, tint = TitanGold, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                      text = "4-Yr Equity: ₹${analysis.fourYearEquityLakhs.roundToInt()}L • Bonus: ${analysis.medianBonusPercent}%",
                      fontSize = 11.sp,
                      color = TextPrimaryDark,
                      fontWeight = FontWeight.Medium
                    )
                  }
                  Text(
                    text = "Sign-on: ${analysis.signOnBonusRange}",
                    fontSize = 10.sp,
                    color = TitanEmerald,
                    fontWeight = FontWeight.Bold
                  )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Key Market Data Points
                Text(
                  text = "MARKET ANCHOR INTELLIGENCE",
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  color = TitanCyan,
                  letterSpacing = 0.6.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                analysis.keyMarketDataPoints.take(2).forEach { pt ->
                  Row(modifier = Modifier.padding(vertical = 1.dp)) {
                    Text("• ", color = TitanCyan, fontSize = 11.sp)
                    Text(pt, fontSize = 11.sp, color = TextSecondaryDark, lineHeight = 15.sp)
                  }
                }
              }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 3. Scenario-Based Negotiation Scripts Card
            Card(
              modifier = Modifier
                .fillMaxWidth()
                .testTag("negotiation_scripts_container_card"),
              colors = CardDefaults.cardColors(containerColor = SlateCard),
              shape = RoundedCornerShape(14.dp),
              border = BorderStroke(1.dp, SlateBorder)
            ) {
              Column(modifier = Modifier.padding(14.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Psychology, contentDescription = null, tint = TitanCyan, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                      text = "DATA-DRIVEN SCRIPT STUDIO",
                      fontSize = 12.sp,
                      fontWeight = FontWeight.Bold,
                      color = TitanCyan,
                      letterSpacing = 0.8.sp
                    )
                  }

                  Text(
                    text = "Anchor: ₹${bundle.recommendedTargetAnchorLakhs.roundToInt()}L Total",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TitanEmerald
                  )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Script Tabs
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                  horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                  listOf(
                    "🎯 75th %ile Counter",
                    "🛡️ Budget Pushback",
                    "⚡ Competing Offer",
                    "📞 Live Phone Script"
                  ).forEachIndexed { index, label ->
                    val isSelected = selectedScenarioTab == index
                    Box(
                      modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) TitanEmerald else SlateElevated)
                        .clickable { selectedScenarioTab = index }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                      Text(
                        text = label,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) ObsidianDark else TextSecondaryDark
                      )
                    }
                  }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Active Scenario Body Display
                if (selectedScenarioTab in 0..2) {
                  val scenario = bundle.scenarios.getOrNull(selectedScenarioTab) ?: bundle.scenarios.first()

                  // Subject line box
                  Row(
                    modifier = Modifier
                      .fillMaxWidth()
                      .clip(RoundedCornerShape(6.dp))
                      .background(ObsidianDark)
                      .border(1.dp, SlateBorder, RoundedCornerShape(6.dp))
                      .padding(horizontal = 10.dp, vertical = 7.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Text(
                      text = "Subject: ${scenario.subjectLine}",
                      fontSize = 11.sp,
                      fontWeight = FontWeight.SemiBold,
                      color = TitanCyan,
                      modifier = Modifier.weight(1f)
                    )
                    IconButton(
                      onClick = {
                        clipboardManager.setText(AnnotatedString(scenario.subjectLine))
                        toastMessage = "Subject line copied!"
                        copyToastVisible = true
                      },
                      modifier = Modifier.size(24.dp)
                    ) {
                      Icon(Icons.Default.ContentCopy, contentDescription = null, tint = TextMutedDark, modifier = Modifier.size(13.dp))
                    }
                  }

                  Spacer(modifier = Modifier.height(8.dp))

                  // Script Body Box
                  Box(
                    modifier = Modifier
                      .fillMaxWidth()
                      .height(210.dp)
                      .clip(RoundedCornerShape(8.dp))
                      .background(ObsidianDark)
                      .border(1.dp, SlateBorder, RoundedCornerShape(8.dp))
                      .verticalScroll(rememberScrollState())
                      .padding(10.dp)
                  ) {
                    Text(
                      text = scenario.scriptBody,
                      fontSize = 11.sp,
                      fontFamily = FontFamily.Monospace,
                      color = TextPrimaryDark,
                      lineHeight = 16.sp
                    )
                  }

                  Spacer(modifier = Modifier.height(8.dp))

                  // Tactical Psychology Notes
                  Box(
                    modifier = Modifier
                      .fillMaxWidth()
                      .clip(RoundedCornerShape(6.dp))
                      .background(TitanIndigo.copy(alpha = 0.15f))
                      .border(1.dp, TitanIndigo.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                      .padding(8.dp)
                  ) {
                    Row {
                      Icon(Icons.Default.Psychology, contentDescription = null, tint = TitanIndigo, modifier = Modifier.size(14.dp))
                      Spacer(modifier = Modifier.width(6.dp))
                      Text(
                        text = "Tactical Rationale: ${scenario.tacticalPsychologyNotes}",
                        fontSize = 10.sp,
                        color = TextSecondaryDark,
                        lineHeight = 13.sp
                      )
                    }
                  }

                  Spacer(modifier = Modifier.height(10.dp))

                  // Action Row: Copy & Share
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                  ) {
                    Button(
                      onClick = {
                        clipboardManager.setText(AnnotatedString("${scenario.subjectLine}\n\n${scenario.scriptBody}"))
                        toastMessage = "Negotiation script copied to clipboard!"
                        copyToastVisible = true
                      },
                      colors = ButtonDefaults.buttonColors(containerColor = TitanEmerald),
                      shape = RoundedCornerShape(8.dp),
                      modifier = Modifier.weight(1f).testTag("copy_script_button")
                    ) {
                      Icon(Icons.Default.ContentCopy, contentDescription = null, tint = ObsidianDark, modifier = Modifier.size(14.dp))
                      Spacer(modifier = Modifier.width(6.dp))
                      Text("Copy Script", color = ObsidianDark, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                      onClick = {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                          type = "text/plain"
                          putExtra(Intent.EXTRA_SUBJECT, scenario.subjectLine)
                          putExtra(Intent.EXTRA_TEXT, scenario.scriptBody)
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share Negotiation Script"))
                      },
                      colors = ButtonDefaults.buttonColors(containerColor = TitanCyan),
                      shape = RoundedCornerShape(8.dp),
                      modifier = Modifier.weight(1f)
                    ) {
                      Icon(Icons.Default.Share, contentDescription = null, tint = ObsidianDark, modifier = Modifier.size(14.dp))
                      Spacer(modifier = Modifier.width(6.dp))
                      Text("Share / Email Draft", color = ObsidianDark, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                  }
                } else {
                  // Tab 3: Live Phone Call Guide
                  val phone = bundle.phoneCallGuide

                  Column(modifier = Modifier.fillMaxWidth()) {
                    PhoneTalkingPointItem(
                      stepNumber = "1",
                      title = "Opening Hook & Gratitude",
                      script = phone.openingHook,
                      tint = TitanCyan
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    PhoneTalkingPointItem(
                      stepNumber = "2",
                      title = "The Market Anchor Ask",
                      script = phone.theTargetAnchorAsk,
                      tint = TitanEmerald
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    PhoneTalkingPointItem(
                      stepNumber = "3",
                      title = "If Recruiter Says: 'That's above our salary band'",
                      script = phone.objectionHandlingBudgetCeiling,
                      tint = TitanGold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    PhoneTalkingPointItem(
                      stepNumber = "4",
                      title = "If Recruiter Says: 'Internal equity parity constraints'",
                      script = phone.objectionHandlingInternalEquity,
                      tint = TitanViolet
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    PhoneTalkingPointItem(
                      stepNumber = "5",
                      title = "Closing Commitment",
                      script = phone.closingCommitment,
                      tint = TitanEmerald
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                      onClick = {
                        val fullCheatsheet = buildString {
                          appendLine("=== RECRUITER PHONE CALL NEGOTIATION SCRIPT ===")
                          appendLine("1. OPENING: ${phone.openingHook}")
                          appendLine("2. THE ANCHOR: ${phone.theTargetAnchorAsk}")
                          appendLine("3. OBJECTION (BUDGET): ${phone.objectionHandlingBudgetCeiling}")
                          appendLine("4. OBJECTION (EQUITY): ${phone.objectionHandlingInternalEquity}")
                          appendLine("5. CLOSING: ${phone.closingCommitment}")
                        }
                        clipboardManager.setText(AnnotatedString(fullCheatsheet))
                        toastMessage = "Phone call cheat-sheet copied!"
                        copyToastVisible = true
                      },
                      colors = ButtonDefaults.buttonColors(containerColor = TitanEmerald),
                      shape = RoundedCornerShape(8.dp),
                      modifier = Modifier.fillMaxWidth()
                    ) {
                      Icon(Icons.Default.ContentCopy, contentDescription = null, tint = ObsidianDark, modifier = Modifier.size(14.dp))
                      Spacer(modifier = Modifier.width(6.dp))
                      Text("Copy Phone Call Cheat-Sheet", color = ObsidianDark, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                  }
                }
              }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 4. Strategic Rules Card
            Card(
              modifier = Modifier.fillMaxWidth(),
              colors = CardDefaults.cardColors(containerColor = SlateCard),
              shape = RoundedCornerShape(12.dp),
              border = BorderStroke(1.dp, SlateBorder)
            ) {
              Column(modifier = Modifier.padding(12.dp)) {
                Text(
                  text = "TACTICAL NEGOTIATION RULES",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = TitanGold
                )
                Spacer(modifier = Modifier.height(6.dp))
                bundle.strategicNegotiationRules.forEach { rule ->
                  Row(modifier = Modifier.padding(vertical = 2.dp)) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = TitanEmerald, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(rule, fontSize = 11.sp, color = TextSecondaryDark, lineHeight = 14.sp)
                  }
                }
              }
            }
          }
        }

        // Copy Toast Banner
        AnimatedVisibility(
          visible = copyToastVisible,
          enter = fadeIn() + expandVertically(),
          exit = fadeOut() + shrinkVertically()
        ) {
          LaunchedEffect(copyToastVisible) {
            kotlinx.coroutines.delay(2000)
            copyToastVisible = false
          }
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .padding(top = 8.dp)
              .clip(RoundedCornerShape(8.dp))
              .background(TitanEmerald)
              .padding(vertical = 8.dp, horizontal = 14.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = toastMessage,
              color = ObsidianDark,
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }
    }
  }
}

@Composable
private fun PercentileMiniCard(
  title: String,
  totalComp: String,
  base: String,
  color: androidx.compose.ui.graphics.Color,
  isRecommended: Boolean = false,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(8.dp))
      .background(if (isRecommended) color.copy(alpha = 0.15f) else SlateElevated)
      .border(
        1.dp,
        if (isRecommended) color else SlateBorder,
        RoundedCornerShape(8.dp)
      )
      .padding(horizontal = 6.dp, vertical = 8.dp)
  ) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
      Text(
        text = title,
        fontSize = 9.sp,
        fontWeight = FontWeight.Bold,
        color = if (isRecommended) color else TextMutedDark
      )
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = totalComp,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        color = color
      )
      Spacer(modifier = Modifier.height(1.dp))
      Text(
        text = base,
        fontSize = 9.sp,
        color = TextSecondaryDark
      )
    }
  }
}

@Composable
private fun PhoneTalkingPointItem(
  stepNumber: String,
  title: String,
  script: String,
  tint: androidx.compose.ui.graphics.Color
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(8.dp))
      .background(ObsidianDark)
      .border(1.dp, SlateBorder, RoundedCornerShape(8.dp))
      .padding(10.dp)
  ) {
    Column {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier = Modifier
            .size(18.dp)
            .clip(CircleShape)
            .background(tint.copy(alpha = 0.2f)),
          contentAlignment = Alignment.Center
        ) {
          Text(text = stepNumber, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = tint)
        }
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = title, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = tint)
      }
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = "\"$script\"",
        fontSize = 11.sp,
        fontFamily = FontFamily.Monospace,
        color = TextPrimaryDark,
        lineHeight = 15.sp
      )
    }
  }
}
