package com.example

import android.app.Application
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.TitanDatabase
import com.example.data.model.AutomationRule
import com.example.data.model.AutomationTask
import com.example.data.model.CareerNote
import com.example.data.model.CompanyBookmark
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class OfflineEntitiesRoomTest {

  private lateinit var database: TitanDatabase

  @Before
  fun setUp() {
    val application = ApplicationProvider.getApplicationContext<Application>()
    database = Room.inMemoryDatabaseBuilder(
      application,
      TitanDatabase::class.java
    ).allowMainThreadQueries().build()
  }

  @After
  fun tearDown() {
    database.close()
  }

  @Test
  fun testCareerNoteEntityAndDao() = runBlocking {
    val dao = database.careerNoteDao()

    val note = CareerNote(
      id = "note_test_01",
      title = "Bengaluru BA Interview Preparation",
      category = "INTERVIEW_PREP",
      content = "Review supply chain economics and SQL query optimization for inventory turnaround.",
      targetCompany = "Zepto",
      targetRole = "Associate Business Analyst",
      tags = "SQL, Bengaluru, STAR",
      isPinned = false
    )

    dao.insertNote(note)

    val retrieved = dao.getNoteById("note_test_01")
    assertNotNull(retrieved)
    assertEquals("Bengaluru BA Interview Preparation", retrieved?.title)
    assertEquals("Zepto", retrieved?.targetCompany)
    assertEquals(false, retrieved?.isPinned)

    // Test toggle pin
    dao.togglePin("note_test_01", true)
    val pinnedNote = dao.getNoteById("note_test_01")
    assertEquals(true, pinnedNote?.isPinned)

    // Test update
    val updatedNote = note.copy(content = "Updated content with advanced Python data modeling.")
    dao.updateNote(updatedNote)
    val reFetched = dao.getNoteById("note_test_01")
    assertEquals("Updated content with advanced Python data modeling.", reFetched?.content)

    // Test deletion
    dao.deleteNoteById("note_test_01")
    assertNull(dao.getNoteById("note_test_01"))
  }

  @Test
  fun testCompanyBookmarkEntityAndDao() = runBlocking {
    val dao = database.companyBookmarkDao()

    val bookmark = CompanyBookmark(
      companyId = "comp_zepto",
      companyName = "Zepto",
      logoEmoji = "⚡",
      industry = "Quick Commerce",
      tier = "S+",
      priority = "DREAM",
      personalNotes = "Top target in Bellandur. High velocity hiring for Analytics.",
      targetRole = "Business Analyst",
      alertOnNewJobs = true
    )

    dao.insertBookmark(bookmark)

    val retrieved = dao.getBookmark("comp_zepto")
    assertNotNull(retrieved)
    assertEquals("Zepto", retrieved?.companyName)
    assertEquals("S+", retrieved?.tier)
    assertTrue(dao.isBookmarkedSnapshot("comp_zepto"))

    // Test note update
    dao.updateBookmarkNotes("comp_zepto", "Spoke to recruiter on LinkedIn. Follow-up scheduled.")
    val updated = dao.getBookmark("comp_zepto")
    assertEquals("Spoke to recruiter on LinkedIn. Follow-up scheduled.", updated?.personalNotes)

    // Test delete
    dao.deleteBookmark("comp_zepto")
    assertFalse(dao.isBookmarkedSnapshot("comp_zepto"))
    assertNull(dao.getBookmark("comp_zepto"))
  }

  @Test
  fun testAutomationTaskEntityAndDao() = runBlocking {
    val dao = database.automationTaskDao()

    val task = AutomationTask(
      id = "task_job_radar",
      title = "Daily Bengaluru Tech Job Scanner",
      taskType = "JOB_RADAR",
      description = "Scans career portals for Strategy Ops and BA positions.",
      frequency = "DAILY",
      triggerCondition = "SCHEDULED_PERIODIC",
      isEnabled = true,
      priorityLevel = "CRITICAL"
    )

    dao.insertTask(task)

    val retrieved = dao.getTaskById("task_job_radar")
    assertNotNull(retrieved)
    assertEquals("Daily Bengaluru Tech Job Scanner", retrieved?.title)
    assertEquals("JOB_RADAR", retrieved?.taskType)
    assertEquals("CRITICAL", retrieved?.priorityLevel)
    assertTrue(retrieved?.isEnabled == true)

    // Test record execution
    val execTime = System.currentTimeMillis()
    dao.recordExecution("task_job_radar", "SUCCESS", "Discovered 5 fresh openings", execTime)
    val executedTask = dao.getTaskById("task_job_radar")
    assertEquals("SUCCESS", executedTask?.status)
    assertEquals("Discovered 5 fresh openings", executedTask?.lastResultSummary)
    assertEquals(1, executedTask?.executionCount)

    // Test toggle enable/disable
    dao.setTaskEnabled("task_job_radar", false)
    val disabledTask = dao.getTaskById("task_job_radar")
    assertFalse(disabledTask?.isEnabled == true)

    // Test delete
    dao.deleteTaskById("task_job_radar")
    assertNull(dao.getTaskById("task_job_radar"))
  }

  @Test
  fun testAutomationRuleEntityAndDao() = runBlocking {
    val dao = database.automationRuleDao()

    val rule = AutomationRule(
      id = "rule_zepto_inbound",
      name = "Zepto Recruiter Inbound Auto-Responder",
      triggerCategory = "EMAIL_RECEIVED",
      targetCompany = "Zepto",
      triggerCondition = "If email from company Zepto received",
      triggerFilterKeywords = "recruiter, interview, slot, screening",
      responseType = "AUTO_DRAFT_REPLY",
      automatedResponse = "Auto-draft high-conviction executive reply confirming interest & weekday availability",
      responseTemplate = "Dear Zepto Talent Acquisition,\n\nThank you for reaching out...",
      targetAgent = "Inbound Recruiter Specialist",
      requireHumanApproval = false,
      isEnabled = true,
      executionCount = 0,
      lastTriggeredTime = "Never",
      lastExecutionResult = ""
    )

    dao.insertRule(rule)

    val retrieved = dao.getRuleById("rule_zepto_inbound")
    assertNotNull(retrieved)
    assertEquals("Zepto Recruiter Inbound Auto-Responder", retrieved?.name)
    assertEquals("EMAIL_RECEIVED", retrieved?.triggerCategory)
    assertEquals("Zepto", retrieved?.targetCompany)
    assertEquals("If email from company Zepto received", retrieved?.triggerCondition)
    assertEquals("AUTO_DRAFT_REPLY", retrieved?.responseType)
    assertTrue(retrieved?.isEnabled == true)
    assertEquals(0, retrieved?.executionCount)

    // Test record execution
    dao.recordExecution("rule_zepto_inbound", "Just now", "Trigger evaluated: Matched condition")
    val executed = dao.getRuleById("rule_zepto_inbound")
    assertEquals(1, executed?.executionCount)
    assertEquals("Just now", executed?.lastTriggeredTime)
    assertEquals("Trigger evaluated: Matched condition", executed?.lastExecutionResult)

    // Test toggle enable/disable
    dao.setRuleEnabled("rule_zepto_inbound", false)
    val disabled = dao.getRuleById("rule_zepto_inbound")
    assertFalse(disabled?.isEnabled == true)

    // Test delete
    dao.deleteRuleById("rule_zepto_inbound")
    assertNull(dao.getRuleById("rule_zepto_inbound"))
  }
}
