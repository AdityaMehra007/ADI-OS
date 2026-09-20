package com.example

import com.example.data.model.CareerNote
import com.example.data.model.QuickActionType
import com.example.data.model.UserProfile
import com.example.service.CareerNoteSummarizerService
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class QuickActionAutomationTest {

  @Test
  fun testPredefinedQuickActionTypesAreComplete() {
    val types = QuickActionType.values()
    assertEquals(5, types.size)

    val syncIntel = QuickActionType.SYNC_COMPANY_INTEL
    assertEquals("Sync Company Intel", syncIntel.displayName)
    assertEquals("FIRESTORE", syncIntel.badgeText)
    assertEquals("TASK_SYNC_COMPANY_INTEL", syncIntel.taskId)

    val summarizeNote = QuickActionType.SUMMARIZE_CAREER_NOTE
    assertEquals("Summarize Career Note", summarizeNote.displayName)
    assertEquals("GEMINI AI", summarizeNote.badgeText)
    assertEquals("TASK_SUMMARIZE_CAREER_NOTE", summarizeNote.taskId)

    val jobRadar = QuickActionType.RUN_JOB_RADAR
    assertEquals("Run Job Discovery Radar", jobRadar.displayName)
    assertEquals("CRAWLER", jobRadar.badgeText)

    val careerAudit = QuickActionType.AUDIT_CAREER_HEALTH
    assertEquals("Audit Career Health", careerAudit.displayName)
    assertEquals("TELEMETRY", careerAudit.badgeText)

    val triggerRule = QuickActionType.TRIGGER_AUTOMATION_RULE
    assertEquals("Automation Rule Engine", triggerRule.displayName)
    assertEquals("RULES", triggerRule.badgeText)
    assertEquals("TASK_AUTOMATION_RULE_ENGINE", triggerRule.taskId)
  }

  @Test
  fun testCareerNoteSummarizerSynthesizesZeptoMemo() = runBlocking {
    val summarizer = CareerNoteSummarizerService(geminiService = null)
    val zeptoNote = CareerNote(
      id = "note_test_zepto",
      title = "Zepto Strategy Ops Battle-Plan",
      category = "STRATEGY",
      content = "Dark-store fulfillment turnaround strategy. Emphasize SQL batch dispatch algorithm that compressed cycle time by 42%.",
      targetCompany = "Zepto",
      targetRole = "Associate Business Analyst",
      tags = "SQL, Python, Logistics, Strategy",
      isPinned = true
    )

    val profile = UserProfile(
      name = "Adi",
      targetRoles = "Associate Business Analyst, Strategy & Operations"
    )

    val result = summarizer.summarizeNote(zeptoNote, profile)

    assertNotNull(result)
    assertEquals("note_test_zepto", result.noteId)
    assertEquals("Zepto Strategy Ops Battle-Plan", result.noteTitle)
    assertEquals("Zepto", result.targetCompany)
    assertTrue("Executive brief should be meaningful", result.executiveBrief.isNotBlank())
    assertTrue("Should produce key takeaways", result.keyTakeaways.isNotEmpty())
    assertTrue("Should have tactical action items", result.strategicActionItems.isNotEmpty())
    assertTrue("Should have interview talking points", result.interviewTalkingPoints.isNotEmpty())
    assertTrue("Confidence score should be high", result.confidenceScore >= 90)
  }

  @Test
  fun testCareerNoteSummarizerSynthesizesSwiggyNegotiationMemo() = runBlocking {
    val summarizer = CareerNoteSummarizerService(geminiService = null)
    val swiggyNote = CareerNote(
      id = "note_test_swiggy",
      title = "Swiggy Negotiation Script & Equity Multipliers",
      category = "NEGOTIATION",
      content = "Market-anchored compensation strategy establishing non-negotiable floor with performance-accelerated bonuses.",
      targetCompany = "Swiggy",
      targetRole = "Strategy & Operations Lead",
      tags = "Compensation, Equity, ESOP",
      isPinned = false
    )

    val result = summarizer.summarizeNote(swiggyNote)

    assertNotNull(result)
    assertEquals("Swiggy", result.targetCompany)
    assertTrue(result.executiveBrief.contains("compensation", ignoreCase = true) || result.executiveBrief.contains("Swiggy", ignoreCase = true))
    assertTrue(result.keyTakeaways.size >= 3)
    assertTrue(result.strategicActionItems.size >= 3)
  }
}
