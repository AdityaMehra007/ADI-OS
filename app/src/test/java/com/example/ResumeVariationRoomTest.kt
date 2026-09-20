package com.example

import android.app.Application
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.TitanDatabase
import com.example.data.model.ResumeVariation
import com.example.data.repository.ResumeVariationRepository
import kotlinx.coroutines.flow.first
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
class ResumeVariationRoomTest {

  private lateinit var database: TitanDatabase
  private lateinit var repository: ResumeVariationRepository

  @Before
  fun setUp() {
    val application = ApplicationProvider.getApplicationContext<Application>()
    database = Room.inMemoryDatabaseBuilder(
      application,
      TitanDatabase::class.java
    ).allowMainThreadQueries().build()

    repository = ResumeVariationRepository(database.resumeVariationDao())
  }

  @After
  fun tearDown() {
    database.close()
  }

  @Test
  fun testResumeVariationInsertAndFetch() = runBlocking {
    val dao = database.resumeVariationDao()

    val variation = ResumeVariation(
      id = "var_zepto_v1",
      title = "Zepto Strategy & Ops Resume",
      targetCompany = "Zepto",
      targetRole = "Lead - Dark Store Operations",
      resumeText = "Candidate Adi with 5 years experience scaling dark store operations and reducing picking latency.",
      versionNumber = 1,
      versionLabel = "v1.0",
      versionNotes = "Initial baseline version",
      isPrimary = true,
      wordCount = 14,
      overallMatchScore = 92
    )

    dao.insertVariation(variation)

    val fetched = dao.getVariationById("var_zepto_v1")
    assertNotNull(fetched)
    assertEquals("Zepto Strategy & Ops Resume", fetched?.title)
    assertEquals("Zepto", fetched?.targetCompany)
    assertEquals(1, fetched?.versionNumber)
    assertEquals("v1.0", fetched?.versionLabel)
    assertTrue(fetched?.isPrimary == true)
    assertEquals(92, fetched?.overallMatchScore)

    val zeptoVariations = dao.getVariationsForCompany("Zepto").first()
    assertEquals(1, zeptoVariations.size)
    assertEquals("var_zepto_v1", zeptoVariations.first().id)
  }

  @Test
  fun testPrimaryVariationSwitchingTransaction() = runBlocking {
    val dao = database.resumeVariationDao()

    val var1 = ResumeVariation(
      id = "var_primary_01",
      title = "General Operations Resume",
      targetCompany = "General",
      targetRole = "Operations Director",
      resumeText = "Operations resume text",
      versionNumber = 1,
      versionLabel = "v1.0",
      isPrimary = true
    )

    val var2 = ResumeVariation(
      id = "var_razorpay_01",
      title = "Razorpay FinTech Resume",
      targetCompany = "Razorpay",
      targetRole = "Strategy Lead",
      resumeText = "FinTech payments resume text",
      versionNumber = 1,
      versionLabel = "v1.0",
      isPrimary = false
    )

    dao.insertVariation(var1)
    dao.insertVariation(var2)

    var primary = dao.getPrimaryVariation()
    assertEquals("var_primary_01", primary?.id)

    // Switch primary to var2
    dao.setPrimaryVariation("var_razorpay_01")

    primary = dao.getPrimaryVariation()
    assertNotNull(primary)
    assertEquals("var_razorpay_01", primary?.id)

    val updatedVar1 = dao.getVariationById("var_primary_01")
    assertFalse(updatedVar1?.isPrimary ?: true)
  }

  @Test
  fun testRepositoryVersionBranchingAndLineage() = runBlocking {
    val initial = repository.createVariation(
      title = "Zepto Operations Lead",
      targetCompany = "Zepto",
      targetRole = "Lead - Strategy & Dark Store Operations",
      resumeText = "Initial resume content with picking and inventory management.",
      versionNotes = "Baseline draft",
      tags = "Zepto, Ops, SLA",
      isPrimary = true
    )

    assertEquals(1, initial.versionNumber)
    assertEquals("v1.0", initial.versionLabel)
    assertNull(initial.parentVariationId)

    // Branch a new version v2.0
    val updatedText = "Initial resume content with picking and inventory management. Reduced sub-10m dispatch SLA from 9.4m to 6.2m."
    val v2 = repository.createNewVersion(
      parentVariation = initial,
      updatedResumeText = updatedText,
      versionNotes = "Infused sub-10m dispatch SLA metric"
    )

    assertEquals(2, v2.versionNumber)
    assertEquals("v2.0", v2.versionLabel)
    assertEquals(initial.id, v2.parentVariationId)
    assertEquals("Zepto Operations Lead (v2.0)", v2.title)
    assertEquals("Zepto", v2.targetCompany)
    assertEquals("Infused sub-10m dispatch SLA metric", v2.versionNotes)

    // Check version lineage history
    val lineageHistory = repository.getVersionHistoryForLineage(initial.id)
    assertEquals(2, lineageHistory.size)
    // Ordered descending by versionNumber
    assertEquals(2, lineageHistory[0].versionNumber)
    assertEquals(1, lineageHistory[1].versionNumber)
  }

  @Test
  fun testUpdateContentAndScoreCalculations() = runBlocking {
    val variation = repository.createVariation(
      title = "Swiggy Instamart Lead",
      targetCompany = "Swiggy",
      targetRole = "Operations Lead",
      resumeText = "One two three four five.",
      versionNotes = "Initial",
      tags = "Swiggy"
    )

    assertEquals(5, variation.wordCount)

    // Update text
    repository.updateContent(variation.id, "One two three four five six seven eight nine ten.")
    val updated = database.resumeVariationDao().getVariationById(variation.id)
    assertEquals(10, updated?.wordCount)

    // Update scores
    repository.updateScores(variation.id, overallMatchScore = 95, atsCompatibilityScore = 97)
    val scored = database.resumeVariationDao().getVariationById(variation.id)
    assertEquals(95, scored?.overallMatchScore)
    assertEquals(97, scored?.atsCompatibilityScore)
  }

  @Test
  fun testDeleteVariation() = runBlocking {
    val variation = repository.createVariation(
      title = "Temporary Variation",
      targetCompany = "TestCo",
      targetRole = "Test Role",
      resumeText = "Test content",
      versionNotes = "Test notes"
    )

    assertNotNull(database.resumeVariationDao().getVariationById(variation.id))

    repository.deleteVariation(variation.id)
    assertNull(database.resumeVariationDao().getVariationById(variation.id))
  }
}
