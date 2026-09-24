package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.AgentDao
import com.example.data.dao.ApplicationDao
import com.example.data.dao.ApprovalDao
import com.example.data.dao.AutomationDao
import com.example.data.dao.AutomationRuleDao
import com.example.data.dao.AutomationTaskDao
import com.example.data.dao.AutomationTaskLogDao
import com.example.data.dao.CareerNoteDao
import com.example.data.dao.CareerStrategyNoteDao
import com.example.data.dao.CompanyBookmarkDao
import com.example.data.dao.CompanyDao
import com.example.data.dao.CompanyIntelligenceWidgetDao
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
import com.example.data.model.AgentAuditLog
import com.example.data.model.AgentItem
import com.example.data.model.Application
import com.example.data.model.ApprovalItem
import com.example.data.model.AutomationItem
import com.example.data.model.AutomationRule
import com.example.data.model.AutomationTask
import com.example.data.model.AutomationTaskLog
import com.example.data.model.CareerMilestone
import com.example.data.model.CareerNote
import com.example.data.model.CareerStrategyNote
import com.example.data.model.Company
import com.example.data.model.CompanyBookmark
import com.example.data.model.CompanyIntelligenceWidgetCache
import com.example.data.model.DailyBriefing
import com.example.data.model.DecisionItem
import com.example.data.model.GoalItem
import com.example.data.model.IntegrationItem
import com.example.data.model.Job
import com.example.data.model.KnowledgeEdge
import com.example.data.model.KnowledgeNode
import com.example.data.model.MarketRadarItem
import com.example.data.model.MilestoneTask
import com.example.data.model.MissionItem
import com.example.data.model.PersonalMemoryItem
import com.example.data.model.ProjectItem
import com.example.data.model.RecruiterContact
import com.example.data.model.ResumeVariation
import com.example.data.model.SkillItem
import com.example.data.model.StarStory
import com.example.data.model.SystemHealthMetric
import com.example.data.model.TargetCompany
import com.example.data.model.TargetCompanyMarketIntel
import com.example.data.model.UserProfile
import com.example.data.model.VerifiedFact
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
  entities = [
    UserProfile::class,
    VerifiedFact::class,
    Company::class,
    Job::class,
    Application::class,
    RecruiterContact::class,
    StarStory::class,
    SkillItem::class,
    ProjectItem::class,
    GoalItem::class,
    CareerMilestone::class,
    MilestoneTask::class,
    DecisionItem::class,
    AgentItem::class,
    AgentAuditLog::class,
    MarketRadarItem::class,
    DailyBriefing::class,
    MissionItem::class,
    AutomationItem::class,
    ApprovalItem::class,
    PersonalMemoryItem::class,
    KnowledgeNode::class,
    KnowledgeEdge::class,
    IntegrationItem::class,
    SystemHealthMetric::class,
    CareerStrategyNote::class,
    CompanyBookmark::class,
    AutomationTaskLog::class,
    CareerNote::class,
    AutomationTask::class,
    AutomationRule::class,
    TargetCompany::class,
    TargetCompanyMarketIntel::class,
    ResumeVariation::class,
    CompanyIntelligenceWidgetCache::class
  ],
  version = 10,
  exportSchema = true
)
abstract class TitanDatabase : RoomDatabase() {
  abstract fun profileDao(): ProfileDao
  abstract fun verifiedFactDao(): VerifiedFactDao
  abstract fun companyDao(): CompanyDao
  abstract fun targetCompanyDao(): TargetCompanyDao
  abstract fun companyIntelligenceWidgetDao(): CompanyIntelligenceWidgetDao
  abstract fun jobDao(): JobDao
  abstract fun applicationDao(): ApplicationDao
  abstract fun networkDao(): NetworkDao
  abstract fun starStoryDao(): StarStoryDao
  abstract fun skillProjectDao(): SkillProjectDao
  abstract fun goalDecisionDao(): GoalDecisionDao
  abstract fun agentDao(): AgentDao
  abstract fun marketRadarDao(): MarketRadarDao
  abstract fun missionDao(): MissionDao
  abstract fun automationDao(): AutomationDao
  abstract fun automationRuleDao(): AutomationRuleDao
  abstract fun approvalDao(): ApprovalDao
  abstract fun personalMemoryDao(): PersonalMemoryDao
  abstract fun knowledgeDao(): KnowledgeDao
  abstract fun integrationDao(): IntegrationDao
  abstract fun systemHealthDao(): SystemHealthDao
  abstract fun careerStrategyNoteDao(): CareerStrategyNoteDao
  abstract fun companyBookmarkDao(): CompanyBookmarkDao
  abstract fun automationTaskLogDao(): AutomationTaskLogDao
  abstract fun careerNoteDao(): CareerNoteDao
  abstract fun automationTaskDao(): AutomationTaskDao
  abstract fun resumeVariationDao(): ResumeVariationDao



  companion object {
    @Volatile
    private var INSTANCE: TitanDatabase? = null

    fun getDatabase(context: Context, scope: CoroutineScope): TitanDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          TitanDatabase::class.java,
          "adi_os_titan.db"
        )
        .addCallback(DatabaseCallback(scope))
        .fallbackToDestructiveMigration(dropAllTables = true)
        .build()
        INSTANCE = instance
        instance
      }
    }
  }

  private class DatabaseCallback(
    private val scope: CoroutineScope
  ) : RoomDatabase.Callback() {
    override fun onCreate(db: SupportSQLiteDatabase) {
      super.onCreate(db)
      INSTANCE?.let { database ->
        scope.launch(Dispatchers.IO) {
          SeedDataProvider.populateInitialData(database)
        }
      }
    }
  }
}

/**
 * Typealias representing the singleton database instance as AppDatabase.
 */
typealias AppDatabase = TitanDatabase
