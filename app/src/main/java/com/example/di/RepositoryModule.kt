package com.example.di

import android.content.Context
import com.example.ai.GeminiServiceWrapper
import com.example.ai.IGeminiServiceWrapper
import com.example.data.AppDatabase
import com.example.repository.TitanRepository
import com.example.service.FirestoreCareerMomentumService
import com.example.service.FirestoreCareerStrategyService
import com.example.service.FirestoreCompanyIntelligenceService
import com.example.service.FirestoreJobApplicationService
import com.example.service.FirestoreMarketIntelligenceService
import com.example.service.SkillsRadarEngine
import com.example.service.WeeklyCareerHealthReportService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module responsible for providing repositories and services that mediate
 * communication between the database layer and UI/presentation layer.
 */
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

  @Provides
  @Singleton
  fun provideGeminiServiceWrapper(): IGeminiServiceWrapper {
    return GeminiServiceWrapper()
  }

  @Provides
  @Singleton
  fun provideTitanRepository(
    database: AppDatabase,
    geminiServiceWrapper: IGeminiServiceWrapper
  ): TitanRepository {
    return TitanRepository(database, geminiServiceWrapper)
  }

  @Provides
  @Singleton
  fun provideWeeklyCareerHealthReportService(): WeeklyCareerHealthReportService {
    return WeeklyCareerHealthReportService
  }

  @Provides
  @Singleton
  fun provideSkillsRadarEngine(): SkillsRadarEngine {
    return SkillsRadarEngine
  }

  @Provides
  @Singleton
  fun provideFirestoreCareerStrategyService(
    @ApplicationContext context: Context
  ): FirestoreCareerStrategyService {
    return FirestoreCareerStrategyService(context)
  }

  @Provides
  @Singleton
  fun provideFirestoreJobApplicationService(
    @ApplicationContext context: Context
  ): FirestoreJobApplicationService {
    return FirestoreJobApplicationService(context)
  }

  @Provides
  @Singleton
  fun provideFirestoreMarketIntelligenceService(
    @ApplicationContext context: Context
  ): FirestoreMarketIntelligenceService {
    return FirestoreMarketIntelligenceService(context)
  }

  @Provides
  @Singleton
  fun provideFirestoreCompanyIntelligenceService(
    @ApplicationContext context: Context
  ): FirestoreCompanyIntelligenceService {
    return FirestoreCompanyIntelligenceService(context)
  }

  @Provides
  @Singleton
  fun provideFirestoreCareerMomentumService(
    @ApplicationContext context: Context
  ): FirestoreCareerMomentumService {
    return FirestoreCareerMomentumService(context)
  }
}
