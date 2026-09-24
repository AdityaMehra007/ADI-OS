package com.example

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.ai.GeminiServiceWrapper
import com.example.data.TitanDatabase
import com.example.repository.TitanRepository
import com.example.ui.viewmodel.TitanViewModel
import com.example.ui.viewmodel.TitanViewModelFactory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class TitanViewModelFactoryTest {

  @Test
  fun `factory creates TitanViewModel with injected repository and database`() {
    val application = ApplicationProvider.getApplicationContext<Application>()
    val inMemoryDb = Room.inMemoryDatabaseBuilder(
      application,
      TitanDatabase::class.java
    ).allowMainThreadQueries().build()

    val repository = TitanRepository(inMemoryDb, GeminiServiceWrapper())
    val factory = TitanViewModelFactory(
      application = application,
      repository = repository,
      database = inMemoryDb
    )

    val viewModel = factory.create(TitanViewModel::class.java)

    assertNotNull(viewModel)
    assertEquals(repository, viewModel.repository)
    assertEquals(inMemoryDb, viewModel.database)

    val strategyVm = factory.create(com.example.ui.viewmodel.CareerStrategyViewModel::class.java)
    assertNotNull(strategyVm)

    val companyVm = factory.create(com.example.ui.viewmodel.CompanyIntelViewModel::class.java)
    assertNotNull(companyVm)

    val jobVm = factory.create(com.example.ui.viewmodel.JobAutomationViewModel::class.java)
    assertNotNull(jobVm)

    val resumeVm = factory.create(com.example.ui.viewmodel.ResumeLabViewModel::class.java)
    assertNotNull(resumeVm)

    val execVm = factory.create(com.example.ui.viewmodel.ExecutiveCommandViewModel::class.java)
    assertNotNull(execVm)

    inMemoryDb.close()
  }

  @Test(expected = IllegalArgumentException::class)
  fun `factory throws exception for unsupported ViewModel class`() {
    val application = ApplicationProvider.getApplicationContext<Application>()
    val inMemoryDb = Room.inMemoryDatabaseBuilder(
      application,
      TitanDatabase::class.java
    ).allowMainThreadQueries().build()

    val repository = TitanRepository(inMemoryDb, GeminiServiceWrapper())
    val factory = TitanViewModelFactory(
      application = application,
      repository = repository,
      database = inMemoryDb
    )

    class UnsupportedViewModel : ViewModel()

    try {
      factory.create(UnsupportedViewModel::class.java)
    } finally {
      inMemoryDb.close()
    }
  }

  @Test
  fun `createFactory helper returns a valid factory instance`() {
    val application = ApplicationProvider.getApplicationContext<Application>()
    val factory = TitanViewModelFactory.createFactory(application)
    assertNotNull(factory)

    val viewModel = factory.create(TitanViewModel::class.java)
    assertNotNull(viewModel)
    assertNotNull(viewModel.repository)
    assertNotNull(viewModel.database)
  }
}
