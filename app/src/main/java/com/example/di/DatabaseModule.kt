package com.example.di

import android.content.Context
import com.example.data.AppDatabase
import com.example.data.TitanDatabase
import com.example.data.dao.AgentDao
import com.example.data.dao.ApplicationDao
import com.example.data.dao.ApprovalDao
import com.example.data.dao.AutomationDao
import com.example.data.dao.AutomationTaskDao
import com.example.data.dao.AutomationTaskLogDao
import com.example.data.dao.CareerNoteDao
import com.example.data.dao.CareerStrategyNoteDao
import com.example.data.dao.CompanyBookmarkDao
import com.example.data.dao.CompanyDao
import com.example.data.dao.GoalDecisionDao
import com.example.data.dao.IntegrationDao
import com.example.data.dao.JobDao
import com.example.data.dao.KnowledgeDao
import com.example.data.dao.MarketRadarDao
import com.example.data.dao.MissionDao
import com.example.data.dao.NetworkDao
import com.example.data.dao.PersonalMemoryDao
import com.example.data.dao.ProfileDao
import com.example.data.dao.ResumeVariationDao
import com.example.data.dao.SkillProjectDao
import com.example.data.dao.StarStoryDao
import com.example.data.dao.SystemHealthDao
import com.example.data.dao.TargetCompanyDao
import com.example.data.dao.VerifiedFactDao
import com.example.data.repository.ResumeVariationRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

/**
 * Hilt module responsible for providing the singleton AppDatabase (Room) instance,
 * background database CoroutineScope, and granular DAOs to the application layer.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

  @Provides
  @Singleton
  fun provideDatabaseCoroutineScope(): CoroutineScope {
    return CoroutineScope(SupervisorJob() + Dispatchers.IO)
  }

  @Provides
  @Singleton
  fun provideAppDatabase(
    @ApplicationContext context: Context,
    scope: CoroutineScope
  ): AppDatabase {
    return TitanDatabase.getDatabase(context, scope)
  }

  @Provides
  fun provideCareerNoteDao(database: AppDatabase): CareerNoteDao =
    database.careerNoteDao()

  @Provides
  fun provideCompanyBookmarkDao(database: AppDatabase): CompanyBookmarkDao =
    database.companyBookmarkDao()

  @Provides
  fun provideAutomationTaskDao(database: AppDatabase): AutomationTaskDao =
    database.automationTaskDao()

  @Provides
  fun provideCareerStrategyNoteDao(database: AppDatabase): CareerStrategyNoteDao =
    database.careerStrategyNoteDao()

  @Provides
  fun provideAutomationTaskLogDao(database: AppDatabase): AutomationTaskLogDao =
    database.automationTaskLogDao()

  @Provides
  fun provideProfileDao(database: AppDatabase): ProfileDao =
    database.profileDao()

  @Provides
  fun provideVerifiedFactDao(database: AppDatabase): VerifiedFactDao =
    database.verifiedFactDao()

  @Provides
  fun provideCompanyDao(database: AppDatabase): CompanyDao =
    database.companyDao()

  @Provides
  fun provideTargetCompanyDao(database: AppDatabase): TargetCompanyDao =
    database.targetCompanyDao()

  @Provides
  fun provideJobDao(database: AppDatabase): JobDao =
    database.jobDao()

  @Provides
  fun provideApplicationDao(database: AppDatabase): ApplicationDao =
    database.applicationDao()

  @Provides
  fun provideNetworkDao(database: AppDatabase): NetworkDao =
    database.networkDao()

  @Provides
  fun provideStarStoryDao(database: AppDatabase): StarStoryDao =
    database.starStoryDao()

  @Provides
  fun provideSkillProjectDao(database: AppDatabase): SkillProjectDao =
    database.skillProjectDao()

  @Provides
  fun provideGoalDecisionDao(database: AppDatabase): GoalDecisionDao =
    database.goalDecisionDao()

  @Provides
  fun provideAgentDao(database: AppDatabase): AgentDao =
    database.agentDao()

  @Provides
  fun provideMarketRadarDao(database: AppDatabase): MarketRadarDao =
    database.marketRadarDao()

  @Provides
  fun provideMissionDao(database: AppDatabase): MissionDao =
    database.missionDao()

  @Provides
  fun provideAutomationDao(database: AppDatabase): AutomationDao =
    database.automationDao()

  @Provides
  fun provideApprovalDao(database: AppDatabase): ApprovalDao =
    database.approvalDao()

  @Provides
  fun providePersonalMemoryDao(database: AppDatabase): PersonalMemoryDao =
    database.personalMemoryDao()

  @Provides
  fun provideKnowledgeDao(database: AppDatabase): KnowledgeDao =
    database.knowledgeDao()

  @Provides
  fun provideIntegrationDao(database: AppDatabase): IntegrationDao =
    database.integrationDao()

  @Provides
  fun provideSystemHealthDao(database: AppDatabase): SystemHealthDao =
    database.systemHealthDao()

  @Provides
  fun provideResumeVariationDao(database: AppDatabase): ResumeVariationDao =
    database.resumeVariationDao()

  @Provides
  @Singleton
  fun provideResumeVariationRepository(resumeVariationDao: ResumeVariationDao): ResumeVariationRepository =
    ResumeVariationRepository(resumeVariationDao)
}
