package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.AgentAuditLog
import com.example.data.model.AgentItem
import com.example.data.model.Application
import com.example.data.model.AutomationTask
import com.example.data.model.AutomationRule
import com.example.data.model.AutomationTaskLog
import com.example.data.model.CareerMilestone
import com.example.data.model.CareerNote
import com.example.data.model.CareerStrategyNote
import com.example.data.model.Company
import com.example.data.model.CompanyBookmark
import com.example.data.model.DailyBriefing
import com.example.data.model.DecisionItem
import com.example.data.model.GoalItem
import com.example.data.model.Job
import com.example.data.model.MarketRadarItem
import com.example.data.model.MilestoneTask
import com.example.data.model.ProjectItem
import com.example.data.model.RecruiterContact
import com.example.data.model.SkillItem
import com.example.data.model.StarStory
import com.example.data.model.UserProfile
import com.example.data.model.VerifiedFact
import kotlinx.coroutines.flow.Flow

@Dao
interface ProfileDao {
  @Query("SELECT * FROM user_profile WHERE id = :id LIMIT 1")
  fun getUserProfileFlow(id: String = "ADI_PRIMARY_ID"): Flow<UserProfile?>

  @Query("SELECT * FROM user_profile WHERE id = :id LIMIT 1")
  suspend fun getUserProfile(id: String = "ADI_PRIMARY_ID"): UserProfile?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertOrUpdateProfile(profile: UserProfile)

  @Query("UPDATE user_profile SET brutalStrategyMode = :enabled WHERE id = :id")
  suspend fun toggleBrutalMode(enabled: Boolean, id: String = "ADI_PRIMARY_ID")

  @Query("UPDATE user_profile SET automationMode = :mode WHERE id = :id")
  suspend fun setAutomationMode(mode: String, id: String = "ADI_PRIMARY_ID")
}

@Dao
interface VerifiedFactDao {
  @Query("SELECT * FROM verified_facts ORDER BY id DESC")
  fun getAllFacts(): Flow<List<VerifiedFact>>

  @Query("SELECT * FROM verified_facts ORDER BY id DESC")
  suspend fun getAllFactsSnapshot(): List<VerifiedFact>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertFact(fact: VerifiedFact)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(facts: List<VerifiedFact>)

  @Query("DELETE FROM verified_facts WHERE id = :id")
  suspend fun deleteFact(id: Long)
}

@Dao
interface CompanyDao {
  @Query("SELECT * FROM companies ORDER BY tier ASC, name ASC")
  fun getAllCompanies(): Flow<List<Company>>

  @Query("SELECT * FROM companies WHERE isWatched = 1 ORDER BY tier ASC, name ASC")
  fun getWatchedCompanies(): Flow<List<Company>>

  @Query("SELECT * FROM companies WHERE id = :id LIMIT 1")
  suspend fun getCompanyById(id: String): Company?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertCompany(company: Company)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(companies: List<Company>)

  @Query("UPDATE companies SET isWatched = :isWatched WHERE id = :id")
  suspend fun toggleWatch(id: String, isWatched: Boolean)

  @Query("UPDATE companies SET strategicPriority = :priority WHERE id = :id")
  suspend fun updatePriority(id: String, priority: String)

  @Query("SELECT * FROM companies ORDER BY tier ASC, name ASC")
  suspend fun getAllCompaniesSnapshot(): List<Company>

  @Query("DELETE FROM companies WHERE id = :id")
  suspend fun deleteCompanyById(id: String)

  @Query("UPDATE companies SET latestNewsSummary = :newsSummary, newsSummaryTimestamp = :timestamp WHERE id = :id OR name = :name")
  suspend fun updateCompanyNewsSummary(id: String, name: String, newsSummary: String, timestamp: String)
}

@Dao
interface JobDao {
  @Query("SELECT * FROM jobs ORDER BY adiFitScore DESC, opportunityScore DESC")
  fun getAllJobs(): Flow<List<Job>>

  @Query("SELECT * FROM jobs WHERE isFresherFriendly = 1 ORDER BY adiFitScore DESC")
  fun getFresherJobs(): Flow<List<Job>>

  @Query("SELECT * FROM jobs WHERE id = :id LIMIT 1")
  suspend fun getJobById(id: String): Job?

  @Query("SELECT * FROM jobs ORDER BY adiFitScore DESC, opportunityScore DESC")
  suspend fun getAllJobsSnapshot(): List<Job>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertJob(job: Job)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(jobs: List<Job>)

  @Query("UPDATE jobs SET status = :status WHERE id = :id")
  suspend fun updateJobStatus(id: String, status: String)
}

@Dao
interface ApplicationDao {
  @Query("SELECT * FROM applications ORDER BY dateDiscovered DESC")
  fun getAllApplications(): Flow<List<Application>>

  @Query("SELECT * FROM applications WHERE status = :status")
  fun getApplicationsByStatus(status: String): Flow<List<Application>>

  @Query("SELECT * FROM applications WHERE id = :id LIMIT 1")
  suspend fun getApplicationById(id: String): Application?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertApplication(application: Application)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(applications: List<Application>)

  @Query("UPDATE applications SET status = :status WHERE id = :id")
  suspend fun updateApplicationStatus(id: String, status: String)

  @Query("UPDATE applications SET rejectionAnalysis = :analysis WHERE id = :id")
  suspend fun updateRejectionAnalysis(id: String, analysis: String)

  @Query("DELETE FROM applications WHERE id = :id")
  suspend fun deleteApplication(id: String)
}

@Dao
interface NetworkDao {
  @Query("SELECT * FROM recruiter_contacts ORDER BY name ASC")
  fun getAllContacts(): Flow<List<RecruiterContact>>

  @Query("SELECT * FROM recruiter_contacts WHERE companyId = :companyId")
  fun getContactsForCompany(companyId: String): Flow<List<RecruiterContact>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertContact(contact: RecruiterContact)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(contacts: List<RecruiterContact>)

  @Query("UPDATE recruiter_contacts SET relationshipState = :state, lastInteractionDate = :lastDate WHERE id = :id")
  suspend fun updateRelationship(id: String, state: String, lastDate: String)
}

@Dao
interface StarStoryDao {
  @Query("SELECT * FROM star_stories ORDER BY id ASC")
  fun getAllStories(): Flow<List<StarStory>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertStory(story: StarStory)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(stories: List<StarStory>)
}

@Dao
interface SkillProjectDao {
  @Query("SELECT * FROM skills ORDER BY isGap DESC, isTargetDemand DESC")
  fun getAllSkills(): Flow<List<SkillItem>>

  @Query("SELECT * FROM skills ORDER BY isGap DESC, isTargetDemand DESC")
  suspend fun getAllSkillsSnapshot(): List<SkillItem>

  @Query("SELECT * FROM projects ORDER BY status ASC, title ASC")
  fun getAllProjects(): Flow<List<ProjectItem>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertSkill(skill: SkillItem)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAllSkills(skills: List<SkillItem>)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertProject(project: ProjectItem)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAllProjects(projects: List<ProjectItem>)
}

@Dao
interface GoalDecisionDao {
  @Query("SELECT * FROM goals ORDER BY horizon ASC")
  fun getAllGoals(): Flow<List<GoalItem>>

  @Query("SELECT * FROM decisions ORDER BY dateCreated DESC")
  fun getAllDecisions(): Flow<List<DecisionItem>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertGoal(goal: GoalItem)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAllGoals(goals: List<GoalItem>)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertDecision(decision: DecisionItem)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAllDecisions(decisions: List<DecisionItem>)

  @Query("UPDATE goals SET progressPercent = :progress WHERE id = :id")
  suspend fun updateGoalProgress(id: String, progress: Int)

  @Query("SELECT * FROM goals ORDER BY horizon ASC")
  suspend fun getAllGoalsSnapshot(): List<GoalItem>

  // Career Milestones
  @Query("SELECT * FROM career_milestones ORDER BY horizonYears ASC, dateCreated ASC")
  fun getAllMilestones(): Flow<List<CareerMilestone>>

  @Query("SELECT * FROM career_milestones ORDER BY horizonYears ASC, dateCreated ASC")
  suspend fun getAllMilestonesSnapshot(): List<CareerMilestone>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertMilestone(milestone: CareerMilestone)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAllMilestones(milestones: List<CareerMilestone>)

  @Delete
  suspend fun deleteMilestone(milestone: CareerMilestone)

  @Query("DELETE FROM career_milestones WHERE id = :id")
  suspend fun deleteMilestoneById(id: String)

  // Milestone Tasks
  @Query("SELECT * FROM milestone_tasks ORDER BY isCompleted ASC, weightPoints DESC")
  fun getAllMilestoneTasks(): Flow<List<MilestoneTask>>

  @Query("SELECT * FROM milestone_tasks WHERE milestoneId = :milestoneId ORDER BY isCompleted ASC, weightPoints DESC")
  fun getTasksForMilestone(milestoneId: String): Flow<List<MilestoneTask>>

  @Query("SELECT * FROM milestone_tasks WHERE milestoneId = :milestoneId")
  suspend fun getTasksForMilestoneSnapshot(milestoneId: String): List<MilestoneTask>

  @Query("SELECT * FROM milestone_tasks")
  suspend fun getAllMilestoneTasksSnapshot(): List<MilestoneTask>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertTask(task: MilestoneTask)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAllTasks(tasks: List<MilestoneTask>)

  @Update
  suspend fun updateTask(task: MilestoneTask)

  @Delete
  suspend fun deleteTask(task: MilestoneTask)

  @Query("DELETE FROM milestone_tasks WHERE id = :taskId")
  suspend fun deleteTaskById(taskId: String)

  @Query("DELETE FROM milestone_tasks WHERE milestoneId = :milestoneId")
  suspend fun deleteTasksByMilestoneId(milestoneId: String)
}

@Dao
interface AgentDao {
  @Query("SELECT * FROM agents ORDER BY agentNumber ASC")
  fun getAllAgents(): Flow<List<AgentItem>>

  @Query("SELECT * FROM agents ORDER BY agentNumber ASC")
  suspend fun getAllAgentsSnapshot(): List<AgentItem>

  @Query("SELECT * FROM agent_audit_logs ORDER BY id DESC LIMIT 50")
  fun getRecentLogs(): Flow<List<AgentAuditLog>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAgent(agent: AgentItem)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAllAgents(agents: List<AgentItem>)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertLog(log: AgentAuditLog)

  @Query("UPDATE agents SET status = :status, lastRunTimestamp = :timestamp, currentTask = :task WHERE id = :id")
  suspend fun updateAgentStatus(id: String, status: String, timestamp: String, task: String)
}

@Dao
interface MarketRadarDao {
  @Query("SELECT * FROM market_radar ORDER BY region ASC")
  fun getMarketRadar(): Flow<List<MarketRadarItem>>

  @Query("SELECT * FROM daily_briefings ORDER BY id DESC LIMIT 1")
  fun getLatestBriefing(): Flow<DailyBriefing?>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAllRadar(items: List<MarketRadarItem>)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertBriefing(briefing: DailyBriefing)
}

@Dao
interface MissionDao {
  @Query("SELECT * FROM missions ORDER BY progressPercent DESC")
  fun getAllMissions(): Flow<List<com.example.data.model.MissionItem>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(missions: List<com.example.data.model.MissionItem>)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertMission(mission: com.example.data.model.MissionItem)

  @Query("UPDATE missions SET progressPercent = :progress, status = :status WHERE id = :id")
  suspend fun updateMissionProgress(id: String, progress: Int, status: String)
}

@Dao
interface AutomationDao {
  @Query("SELECT * FROM automations ORDER BY isEnabled DESC, name ASC")
  fun getAllAutomations(): Flow<List<com.example.data.model.AutomationItem>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(automations: List<com.example.data.model.AutomationItem>)

  @Query("UPDATE automations SET isEnabled = :enabled WHERE id = :id")
  suspend fun toggleAutomation(id: String, enabled: Boolean)

  @Query("UPDATE automations SET executionCount = executionCount + 1 WHERE id = :id")
  suspend fun incrementExecution(id: String)
}

@Dao
interface ApprovalDao {
  @Query("SELECT * FROM approvals ORDER BY timestamp DESC")
  fun getAllApprovals(): Flow<List<com.example.data.model.ApprovalItem>>

  @Query("SELECT * FROM approvals WHERE status = 'PENDING' ORDER BY timestamp DESC")
  fun getPendingApprovals(): Flow<List<com.example.data.model.ApprovalItem>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(approvals: List<com.example.data.model.ApprovalItem>)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertApproval(approval: com.example.data.model.ApprovalItem)

  @Query("UPDATE approvals SET status = :status WHERE id = :id")
  suspend fun updateApprovalStatus(id: String, status: String)
}

@Dao
interface PersonalMemoryDao {
  @Query("SELECT * FROM personal_memories ORDER BY timestamp DESC")
  fun getAllMemories(): Flow<List<com.example.data.model.PersonalMemoryItem>>

  @Query("SELECT * FROM personal_memories WHERE memoryLayer = :layer ORDER BY timestamp DESC")
  fun getMemoriesByLayer(layer: String): Flow<List<com.example.data.model.PersonalMemoryItem>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(memories: List<com.example.data.model.PersonalMemoryItem>)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertMemory(memory: com.example.data.model.PersonalMemoryItem)

  @Query("DELETE FROM personal_memories WHERE id = :id")
  suspend fun deleteMemory(id: String)
}

@Dao
interface KnowledgeDao {
  @Query("SELECT * FROM knowledge_nodes ORDER BY significanceScore DESC")
  fun getAllNodes(): Flow<List<com.example.data.model.KnowledgeNode>>

  @Query("SELECT * FROM knowledge_edges")
  fun getAllEdges(): Flow<List<com.example.data.model.KnowledgeEdge>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAllNodes(nodes: List<com.example.data.model.KnowledgeNode>)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAllEdges(edges: List<com.example.data.model.KnowledgeEdge>)
}

@Dao
interface IntegrationDao {
  @Query("SELECT * FROM integrations ORDER BY status ASC, serviceName ASC")
  fun getAllIntegrations(): Flow<List<com.example.data.model.IntegrationItem>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(integrations: List<com.example.data.model.IntegrationItem>)

  @Query("UPDATE integrations SET status = :status, lastSyncTimestamp = :lastSync WHERE id = :id")
  suspend fun updateIntegrationStatus(id: String, status: String, lastSync: String)
}

@Dao
interface SystemHealthDao {
  @Query("SELECT * FROM system_health_metrics ORDER BY category ASC")
  fun getAllMetrics(): Flow<List<com.example.data.model.SystemHealthMetric>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(metrics: List<com.example.data.model.SystemHealthMetric>)

  @Query("UPDATE system_health_metrics SET value = :value, status = :status WHERE id = :id")
  suspend fun updateMetric(id: String, value: String, status: String)
}

@Dao
interface CareerStrategyNoteDao {
  @Query("SELECT * FROM career_strategy_notes ORDER BY isPinned DESC, updatedAt DESC")
  fun getAllNotes(): Flow<List<CareerStrategyNote>>

  @Query("SELECT * FROM career_strategy_notes ORDER BY isPinned DESC, updatedAt DESC")
  suspend fun getAllNotesSnapshot(): List<CareerStrategyNote>

  @Query("SELECT * FROM career_strategy_notes WHERE category = :category ORDER BY isPinned DESC, updatedAt DESC")
  fun getNotesByCategory(category: String): Flow<List<CareerStrategyNote>>

  @Query("SELECT * FROM career_strategy_notes WHERE id = :id LIMIT 1")
  suspend fun getNoteById(id: String): CareerStrategyNote?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertNote(note: CareerStrategyNote)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(notes: List<CareerStrategyNote>)

  @Update
  suspend fun updateNote(note: CareerStrategyNote)

  @Query("DELETE FROM career_strategy_notes WHERE id = :id")
  suspend fun deleteNoteById(id: String)

  @Query("UPDATE career_strategy_notes SET isPinned = :isPinned, updatedAt = :updatedAt WHERE id = :id")
  suspend fun togglePin(id: String, isPinned: Boolean, updatedAt: Long = System.currentTimeMillis())

  @Query("SELECT * FROM career_strategy_notes WHERE title LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%' OR tags LIKE '%' || :query || '%' ORDER BY isPinned DESC, updatedAt DESC")
  fun searchNotes(query: String): Flow<List<CareerStrategyNote>>
}

@Dao
interface CompanyBookmarkDao {
  @Query("SELECT * FROM company_bookmarks ORDER BY bookmarkedAt DESC")
  fun getAllBookmarks(): Flow<List<CompanyBookmark>>

  @Query("SELECT * FROM company_bookmarks ORDER BY bookmarkedAt DESC")
  suspend fun getAllBookmarksSnapshot(): List<CompanyBookmark>

  @Query("SELECT * FROM company_bookmarks WHERE companyId = :companyId LIMIT 1")
  suspend fun getBookmark(companyId: String): CompanyBookmark?

  @Query("SELECT * FROM company_bookmarks WHERE companyId = :companyId LIMIT 1")
  fun observeBookmark(companyId: String): Flow<CompanyBookmark?>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertBookmark(bookmark: CompanyBookmark)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(bookmarks: List<CompanyBookmark>)

  @Query("DELETE FROM company_bookmarks WHERE companyId = :companyId")
  suspend fun deleteBookmark(companyId: String)

  @Query("SELECT EXISTS(SELECT 1 FROM company_bookmarks WHERE companyId = :companyId)")
  fun isBookmarked(companyId: String): Flow<Boolean>

  @Query("SELECT EXISTS(SELECT 1 FROM company_bookmarks WHERE companyId = :companyId)")
  suspend fun isBookmarkedSnapshot(companyId: String): Boolean

  @Query("UPDATE company_bookmarks SET personalNotes = :notes WHERE companyId = :companyId")
  suspend fun updateBookmarkNotes(companyId: String, notes: String)
}

@Dao
interface AutomationTaskLogDao {
  @Query("SELECT * FROM automation_task_logs ORDER BY timestamp DESC")
  fun getAllTaskLogs(): Flow<List<AutomationTaskLog>>

  @Query("SELECT * FROM automation_task_logs ORDER BY timestamp DESC LIMIT :limit")
  fun getRecentTaskLogs(limit: Int = 50): Flow<List<AutomationTaskLog>>

  @Query("SELECT * FROM automation_task_logs ORDER BY timestamp DESC")
  suspend fun getAllTaskLogsSnapshot(): List<AutomationTaskLog>

  @Query("SELECT * FROM automation_task_logs WHERE taskId = :taskId ORDER BY timestamp DESC")
  fun getLogsForTask(taskId: String): Flow<List<AutomationTaskLog>>

  @Query("SELECT * FROM automation_task_logs WHERE status = :status ORDER BY timestamp DESC")
  fun getLogsByStatus(status: String): Flow<List<AutomationTaskLog>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertTaskLog(log: AutomationTaskLog): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(logs: List<AutomationTaskLog>)

  @Query("DELETE FROM automation_task_logs WHERE id = :id")
  suspend fun deleteLogById(id: Long)

  @Query("DELETE FROM automation_task_logs WHERE id IN (:ids)")
  suspend fun deleteLogsByIds(ids: List<Long>)

  @Query("DELETE FROM automation_task_logs WHERE timestamp < :cutoffTimestamp")
  suspend fun pruneOldLogs(cutoffTimestamp: Long)
}

@Dao
interface CareerNoteDao {
  @Query("SELECT * FROM career_notes ORDER BY isPinned DESC, updatedAt DESC")
  fun getAllNotes(): Flow<List<CareerNote>>

  @Query("SELECT * FROM career_notes ORDER BY isPinned DESC, updatedAt DESC")
  suspend fun getAllNotesSnapshot(): List<CareerNote>

  @Query("SELECT * FROM career_notes WHERE category = :category ORDER BY isPinned DESC, updatedAt DESC")
  fun getNotesByCategory(category: String): Flow<List<CareerNote>>

  @Query("SELECT * FROM career_notes WHERE id = :id LIMIT 1")
  suspend fun getNoteById(id: String): CareerNote?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertNote(note: CareerNote)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(notes: List<CareerNote>)

  @Update
  suspend fun updateNote(note: CareerNote)

  @Query("DELETE FROM career_notes WHERE id = :id")
  suspend fun deleteNoteById(id: String)

  @Query("UPDATE career_notes SET isPinned = :isPinned, updatedAt = :updatedAt WHERE id = :id")
  suspend fun togglePin(id: String, isPinned: Boolean, updatedAt: Long = System.currentTimeMillis())

  @Query("SELECT * FROM career_notes WHERE title LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%' OR tags LIKE '%' || :query || '%' ORDER BY isPinned DESC, updatedAt DESC")
  fun searchNotes(query: String): Flow<List<CareerNote>>
}

@Dao
interface AutomationTaskDao {
  @Query("SELECT * FROM automation_tasks ORDER BY priorityLevel ASC, createdAt DESC")
  fun getAllTasks(): Flow<List<AutomationTask>>

  @Query("SELECT * FROM automation_tasks ORDER BY priorityLevel ASC, createdAt DESC")
  suspend fun getAllTasksSnapshot(): List<AutomationTask>

  @Query("SELECT * FROM automation_tasks WHERE id = :id LIMIT 1")
  suspend fun getTaskById(id: String): AutomationTask?

  @Query("SELECT * FROM automation_tasks WHERE taskType = :taskType ORDER BY createdAt DESC")
  fun getTasksByType(taskType: String): Flow<List<AutomationTask>>

  @Query("SELECT * FROM automation_tasks WHERE isEnabled = 1")
  fun getEnabledTasks(): Flow<List<AutomationTask>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertTask(task: AutomationTask)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(tasks: List<AutomationTask>)

  @Update
  suspend fun updateTask(task: AutomationTask)

  @Query("DELETE FROM automation_tasks WHERE id = :id")
  suspend fun deleteTaskById(id: String)

  @Query("UPDATE automation_tasks SET isEnabled = :isEnabled, updatedAt = :updatedAt WHERE id = :id")
  suspend fun setTaskEnabled(id: String, isEnabled: Boolean, updatedAt: Long = System.currentTimeMillis())

  @Query("UPDATE automation_tasks SET status = :status, lastExecutedTimestamp = :executedAt, lastResultSummary = :summary, executionCount = executionCount + 1, updatedAt = :executedAt WHERE id = :id")
  suspend fun recordExecution(id: String, status: String, summary: String, executedAt: Long = System.currentTimeMillis())
}

@Dao
interface AutomationRuleDao {
  @Query("SELECT * FROM automation_rules ORDER BY isEnabled DESC, createdAt DESC")
  fun getAllRules(): Flow<List<AutomationRule>>

  @Query("SELECT * FROM automation_rules ORDER BY isEnabled DESC, createdAt DESC")
  suspend fun getAllRulesSnapshot(): List<AutomationRule>

  @Query("SELECT * FROM automation_rules WHERE id = :id LIMIT 1")
  suspend fun getRuleById(id: String): AutomationRule?

  @Query("SELECT * FROM automation_rules WHERE isEnabled = 1")
  fun getEnabledRules(): Flow<List<AutomationRule>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertRule(rule: AutomationRule)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(rules: List<AutomationRule>)

  @Update
  suspend fun updateRule(rule: AutomationRule)

  @Query("DELETE FROM automation_rules WHERE id = :id")
  suspend fun deleteRuleById(id: String)

  @Query("UPDATE automation_rules SET isEnabled = :isEnabled, updatedAt = :updatedAt WHERE id = :id")
  suspend fun setRuleEnabled(id: String, isEnabled: Boolean, updatedAt: Long = System.currentTimeMillis())

  @Query("UPDATE automation_rules SET executionCount = executionCount + 1, lastTriggeredTime = :triggeredTime, lastExecutionResult = :result, updatedAt = :updatedAt WHERE id = :id")
  suspend fun recordExecution(id: String, triggeredTime: String, result: String, updatedAt: Long = System.currentTimeMillis())
}


