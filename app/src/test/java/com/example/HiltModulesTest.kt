package com.example

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.example.data.AppDatabase
import com.example.data.TitanDatabase
import com.example.di.DatabaseModule
import com.example.di.RepositoryModule
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertSame
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class HiltModulesTest {

  @Test
  fun testDatabaseModuleProvidesSingletonAppDatabaseAndDaos() {
    val application = ApplicationProvider.getApplicationContext<Application>()
    val scope = DatabaseModule.provideDatabaseCoroutineScope()

    val database1: AppDatabase = DatabaseModule.provideAppDatabase(application, scope)
    val database2: AppDatabase = DatabaseModule.provideAppDatabase(application, scope)

    assertNotNull(database1)
    assertNotNull(database2)
    assertSame("Database instances must be identical singleton reference", database1, database2)

    // Verify DAOs provided by DatabaseModule
    val careerNoteDao = DatabaseModule.provideCareerNoteDao(database1)
    val companyBookmarkDao = DatabaseModule.provideCompanyBookmarkDao(database1)
    val automationTaskDao = DatabaseModule.provideAutomationTaskDao(database1)
    val profileDao = DatabaseModule.provideProfileDao(database1)
    val companyDao = DatabaseModule.provideCompanyDao(database1)
    val jobDao = DatabaseModule.provideJobDao(database1)
    val applicationDao = DatabaseModule.provideApplicationDao(database1)

    assertNotNull(careerNoteDao)
    assertNotNull(companyBookmarkDao)
    assertNotNull(automationTaskDao)
    assertNotNull(profileDao)
    assertNotNull(companyDao)
    assertNotNull(jobDao)
    assertNotNull(applicationDao)
  }

  @Test
  fun testRepositoryModuleProvidesTitanRepositoryAndServices() = runBlocking {
    val application = ApplicationProvider.getApplicationContext<Application>()
    val scope = DatabaseModule.provideDatabaseCoroutineScope()
    val database = DatabaseModule.provideAppDatabase(application, scope)

    val geminiWrapper = RepositoryModule.provideGeminiServiceWrapper()
    val repository = RepositoryModule.provideTitanRepository(database, geminiWrapper)

    assertNotNull(repository)
    assertSame(database, repository.database)

    val healthReportService = RepositoryModule.provideWeeklyCareerHealthReportService()
    val radarEngine = RepositoryModule.provideSkillsRadarEngine()
    val strategyService = RepositoryModule.provideFirestoreCareerStrategyService(application)
    val appService = RepositoryModule.provideFirestoreJobApplicationService(application)
    val marketService = RepositoryModule.provideFirestoreMarketIntelligenceService(application)
    val momentumService = RepositoryModule.provideFirestoreCareerMomentumService(application)

    assertNotNull(healthReportService)
    assertNotNull(radarEngine)
    assertNotNull(strategyService)
    assertNotNull(appService)
    assertNotNull(marketService)
    assertNotNull(momentumService)

    // Verify that the repository interacts seamlessly with database
    val note = com.example.data.model.CareerNote(
      id = "hilt_test_note_01",
      title = "Hilt Strategy Memo",
      content = "Verified repository and Room singleton wiring."
    )
    repository.insertCareerNote(note)
    val notes = repository.getAllCareerNotes().firstOrNull()
    assertNotNull(notes)
    val retrieved = notes?.find { it.id == "hilt_test_note_01" }
    assertNotNull(retrieved)
    assertEquals("Hilt Strategy Memo", retrieved?.title)
  }
}
