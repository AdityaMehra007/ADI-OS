package com.example

import com.example.ai.GeminiCareerService
import com.example.data.model.UserProfile
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class OmegaTitanSovereignTest {

  private lateinit var geminiService: GeminiCareerService
  private lateinit var profile: UserProfile

  @Before
  fun setup() {
    geminiService = GeminiCareerService()
    profile = UserProfile(
      id = "adi_primary",
      name = "Aditya Mehra",
      educationDegree = "BBA in International Business",
      educationSpecialization = "International Business",
      university = "Dayananda Sagar University (DSU), Bengaluru",
      location = "Bengaluru, Karnataka, India",
      interests = "Quick Commerce, Operations, Supply Chain, Android, AI",
      automationMode = "APPROVAL",
      brutalStrategyMode = false,
      profileCompleteness = 100
    )
  }

  @Test
  fun testSovereignTruthAnchorInPrompt() = runBlocking {
    val prompt = "3 Pitch"
    val response = geminiService.askCopilot(prompt, profile, brutalMode = false)

    assertNotNull(response)
    assertTrue("Response should contain Aditya Mehra or Adi", response.contains("Aditya") || response.contains("Adi"))
    assertTrue("Response should reference AERO India", response.contains("AERO India"))
    assertTrue("Response should reference 300+ builds", response.contains("300+"))
    assertTrue("Response should reference 0.00% downtime", response.contains("0.00% downtime") || response.contains("zero downtime"))
    assertTrue("Response should reference 14 hubs or 42%", response.contains("14") && response.contains("42%"))
    assertFalse("Must not contain hallucinated names", response.contains("Shenoy", ignoreCase = true))
  }

  @Test
  fun testHostileBarRaiserCounterAttackKeypad9() = runBlocking {
    val prompt = "9 Why hire a fresher with a 6.33 CGPA?"
    val response = geminiService.askCopilot(prompt, profile, brutalMode = true)

    assertNotNull(response)
    assertTrue("Should address CGPA", response.contains("6.33") || response.contains("CGPA"))
    assertTrue("Should highlight 41/41 first-attempt clearance", response.contains("41 out of 41") || response.contains("41/41"))
    assertTrue("Should reference Puma India or Tata Communications", response.contains("Puma India") || response.contains("Tata Communications"))
    assertTrue("Should mention Yelahanka", response.contains("Yelahanka"))
  }

  @Test
  fun testDarkStoreTelemetryKeypad7And18() = runBlocking {
    val hudResponse = geminiService.askCopilot("7 Live HUD", profile, brutalMode = false)
    val darkStoreResponse = geminiService.askCopilot("18 Dark Store Morning Health", profile, brutalMode = false)

    assertTrue("HUD must quote pick time <90s", hudResponse.contains("90s"))
    assertTrue("HUD must quote pack time <45s", hudResponse.contains("45s"))
    assertTrue("HUD must quote CM2 formula or target", hudResponse.contains("CM2"))

    assertTrue("Dark store check must include morning checklist", darkStoreResponse.contains("06:30 AM") || darkStoreResponse.contains("Perishable"))
    assertTrue("Dark store check must include 255s cycle target", darkStoreResponse.contains("255") || darkStoreResponse.contains("4.25m"))
  }

  @Test
  fun testSalaryCounterOfferKeypad15() = runBlocking {
    val response = geminiService.askCopilot("15 Counter-Offer", profile, brutalMode = false)

    assertTrue("Should contain formal counter script", response.contains("Subject:") || response.contains("Thank you for extending"))
    assertTrue("Should reference 42% supply chain metric", response.contains("42%"))
    assertTrue("Should reference AERO India metric", response.contains("AERO India") || response.contains("300+"))
  }

  @Test
  fun testLiveSqlDiagnosticKeypad11() = runBlocking {
    val response = geminiService.askCopilot("11 SQL CTE", profile, brutalMode = false)

    assertTrue("Should contain SQL code block", response.contains("SELECT") && response.contains("WITH"))
    assertTrue("Should contain window function or DENSE_RANK", response.contains("DENSE_RANK") || response.contains("ORDER BY"))
  }

  @Test
  fun testWarRoomMultiAgentSimulation() = runBlocking {
    val response = geminiService.askCopilot("WAR-ROOM", profile, brutalMode = true)

    assertTrue("Should simulate CEO", response.contains("CEO Agent"))
    assertTrue("Should simulate COO", response.contains("COO Agent"))
    assertTrue("Should simulate CFO", response.contains("CFO Agent"))
    assertTrue("Should simulate CTO", response.contains("CTO Agent"))
  }

  @Test
  fun testSovereignNotificationChannelConstant() {
    org.junit.Assert.assertEquals(
      "channel_sovereign_operations",
      com.example.notification.TitanNotificationManager.CHANNEL_SOVEREIGN_OPERATIONS
    )
  }

  @Test
  fun testDarkStoreDockPhysicsMath() {
    val pickSec = 86.0
    val packSec = 42.0
    val bufferSec = 95.0
    val totalSec = pickSec + packSec + bufferSec
    org.junit.Assert.assertEquals(223.0, totalSec, 0.01)
    assertTrue("Total dock cycle time must be under 255s optimal SLA threshold", totalSec <= 255.0)

    val aov = 480.0
    val cogs = 384.0
    val delivery = 48.0
    val pickPack = 12.0
    val lease = 18.0
    val pg = 5.0
    val ads = 15.0
    val cm2 = aov - cogs - delivery - pickPack - lease - pg + ads
    org.junit.Assert.assertEquals(28.0, cm2, 0.01)
    assertTrue("Contribution Margin 2 must remain positive", cm2 > 0)
  }

  @Test
  fun testTrillionDollarMasterPromptAndAladdinRisk() = runBlocking {
    val response = geminiService.askCopilot("TRILLION", profile, brutalMode = true)

    assertNotNull(response)
    assertTrue("Should include $1T valuation", response.contains("1,000,000,000,000") || response.contains("TRILLION"))
    assertTrue("Should include BlackRock Aladdin", response.contains("Aladdin") || response.contains("BlackRock"))
    assertTrue("Should reference 12-Agent swarm", response.contains("12-AGENT") || response.contains("12-Agent"))
    assertTrue("Should reference Aditya Mehra", response.contains("Aditya Mehra") || response.contains("Adi"))
    assertTrue("Should include dynamic micro-cluster batching", response.contains("Micro-Cluster") || response.contains("Batching"))
    assertTrue("Should reference audited AERO India credential", response.contains("AERO India"))
  }

  @Test
  fun testAladdinMacroShockMath() {
    val aov = 480.0
    val cogs = 384.0
    val baseDelivery = 48.0
    val surgedDelivery = 60.0 // +25% fuel spike
    val batchedDelivery = 39.0 // Aladdin 2-drop route
    val pickPack = 12.0
    val lease = 18.0
    val pg = 5.0
    val ads = 15.0

    val unmitigatedCm2 = aov - cogs - surgedDelivery - pickPack - lease - pg + ads
    val stabilizedCm2 = aov - cogs - batchedDelivery - pickPack - lease - pg + ads

    org.junit.Assert.assertEquals(16.0, unmitigatedCm2, 0.01)
    org.junit.Assert.assertEquals(28.5, stabilizedCm2, 0.01)
    assertTrue("Aladdin micro-cluster dynamic batching must defend CM2 margin above unmitigated shock", stabilizedCm2 > unmitigatedCm2)
  }
}
