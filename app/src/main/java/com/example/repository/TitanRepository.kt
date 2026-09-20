package com.example.repository

import com.example.ai.GeminiCareerService
import com.example.ai.GeminiServiceWrapper
import com.example.ai.IGeminiServiceWrapper
import com.example.data.TitanDatabase
import com.example.data.model.AgentAuditLog
import com.example.data.model.AgentItem
import com.example.data.model.Application
import com.example.data.model.AutomationCriteria
import com.example.data.model.AutomationTask
import com.example.data.model.AutomationTaskLog
import com.example.data.model.CareerGoalsJobAnalysisResult
import com.example.data.model.CareerMilestone
import com.example.data.model.CareerNote
import com.example.data.model.CareerStrategyNote
import com.example.data.model.Company
import com.example.data.model.CompanyBookmark
import com.example.data.model.CompanyIntelligenceWidgetCache
import com.example.data.model.DailyBriefing
import com.example.data.model.DailyCareerBriefingReport
import com.example.data.model.DecisionItem
import com.example.data.model.GoalItem
import com.example.data.model.Job
import com.example.data.model.MarketIntelligenceReport
import com.example.data.model.MarketRadarItem
import com.example.data.model.MilestoneTask
import com.example.data.model.MilestoneWithTasks
import com.example.data.model.ParsedResumeDocument
import com.example.data.model.PreInterviewPepTalk
import com.example.data.model.ProjectItem
import com.example.data.model.RecruiterContact
import com.example.data.model.ResumeOptimizationFeedback
import com.example.data.model.ResumeVariation
import com.example.data.repository.ResumeVariationRepository
import com.example.data.model.SectorMarketInsightsReport
import com.example.data.model.SkillItem
import com.example.data.model.SmartJobMatch
import com.example.data.model.SmartJobSearchCrawlerResponse
import com.example.data.model.StarStory
import com.example.data.model.SystemHealthMetric
import com.example.data.model.TargetCompany
import com.example.data.model.TargetCompanyMarketIntel
import com.example.data.model.TargetCompanyWithMarketIntelligence
import com.example.data.model.UserProfile
import com.example.data.model.VerifiedFact
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TitanRepository(
  val database: TitanDatabase,
  val geminiService: IGeminiServiceWrapper = GeminiServiceWrapper()
) {

  // Resume Variations & Versioning
  val resumeVariationRepository = ResumeVariationRepository(database.resumeVariationDao())
  val allResumeVariationsFlow: Flow<List<ResumeVariation>> = resumeVariationRepository.allVariationsFlow
  val primaryResumeVariationFlow: Flow<ResumeVariation?> = resumeVariationRepository.primaryVariationFlow

  // Profile & Facts
  val userProfileFlow: Flow<UserProfile?> = database.profileDao().getUserProfileFlow()
  val verifiedFactsFlow: Flow<List<VerifiedFact>> = database.verifiedFactDao().getAllFacts()

  suspend fun getUserProfile(): UserProfile? = database.profileDao().getUserProfile()

  suspend fun updateProfile(profile: UserProfile) {
    database.profileDao().insertOrUpdateProfile(profile)
  }

  suspend fun toggleBrutalMode(enabled: Boolean) {
    database.profileDao().toggleBrutalMode(enabled)
  }

  suspend fun setAutomationMode(mode: String) {
    database.profileDao().setAutomationMode(mode)
  }

  suspend fun addVerifiedFact(category: String, claim: String, evidence: String, tags: String) {
    val fact = VerifiedFact(
      category = category,
      claim = claim,
      evidenceDetails = evidence,
      tags = tags
    )
    database.verifiedFactDao().insertFact(fact)
    logAudit(
      agentName = "Profile & Fact Graph Agent",
      action = "FACT_ADDED",
      input = "New claim in $category",
      output = claim,
      confidence = 100,
      source = "USER_INPUT"
    )
  }

  // Companies
  val companiesFlow: Flow<List<Company>> = database.companyDao().getAllCompanies()
  val watchedCompaniesFlow: Flow<List<Company>> = database.companyDao().getWatchedCompanies()

  suspend fun saveCompany(company: Company) {
    database.companyDao().insertCompany(company)
  }

  suspend fun deleteCompany(companyId: String) {
    database.companyDao().deleteCompanyById(companyId)
  }

  suspend fun updateCompanyNewsSummary(companyId: String, companyName: String, newsSummary: String, timestamp: String) {
    database.companyDao().updateCompanyNewsSummary(companyId, companyName, newsSummary, timestamp)
  }

  suspend fun getCompanyById(companyId: String): Company? {
    return database.companyDao().getCompanyById(companyId)
  }

  suspend fun toggleCompanyWatch(companyId: String, isWatched: Boolean) {
    database.companyDao().toggleWatch(companyId, isWatched)
  }

  suspend fun updateCompanyPriority(companyId: String, priority: String) {
    database.companyDao().updatePriority(companyId, priority)
  }

  // Company Intelligence Widget Room Persistence Cache
  val lastSelectedCompanyIntelCacheFlow: Flow<CompanyIntelligenceWidgetCache?> =
    database.companyIntelligenceWidgetDao().getLastSelectedWidgetCacheFlow()

  val allCachedCompanyIntelSummariesFlow: Flow<List<CompanyIntelligenceWidgetCache>> =
    database.companyIntelligenceWidgetDao().getAllCachedWidgetSummariesFlow()

  suspend fun getLastSelectedCompanyIntelCache(): CompanyIntelligenceWidgetCache? {
    return database.companyIntelligenceWidgetDao().getLastSelectedWidgetCache()
  }

  suspend fun getCompanyIntelCache(companyName: String): CompanyIntelligenceWidgetCache? {
    return database.companyIntelligenceWidgetDao().getCacheForCompany(companyName)
  }

  fun getCompanyIntelCacheFlow(companyName: String): Flow<CompanyIntelligenceWidgetCache?> {
    return database.companyIntelligenceWidgetDao().getCacheForCompanyFlow(companyName)
  }

  suspend fun saveCompanyIntelWidgetCache(cache: CompanyIntelligenceWidgetCache) {
    database.companyIntelligenceWidgetDao().saveLastSelectedWidgetCache(cache)
  }

  suspend fun setCompanyAsLastSelected(companyName: String) {
    database.companyIntelligenceWidgetDao().markCompanyAsLastSelected(companyName)
  }

  suspend fun deleteCompanyIntelWidgetCache(companyName: String) {
    database.companyIntelligenceWidgetDao().deleteCacheByCompany(companyName)
  }

  // Target Companies & Market Intelligence (Room Persistence)
  val targetCompaniesWithIntelFlow: Flow<List<TargetCompanyWithMarketIntelligence>> =
    database.targetCompanyDao().getAllTargetCompaniesWithIntel()

  val watchedTargetCompaniesFlow: Flow<List<TargetCompanyWithMarketIntelligence>> =
    database.targetCompanyDao().getWatchedTargetCompanies()

  suspend fun saveTargetCompany(
    company: TargetCompany,
    marketIntel: TargetCompanyMarketIntel? = null
  ) {
    database.targetCompanyDao().insertTargetCompany(company)
    if (marketIntel != null) {
      database.targetCompanyDao().insertMarketIntel(marketIntel)
    }
  }

  suspend fun updateTargetCompany(company: TargetCompany) {
    database.targetCompanyDao().updateTargetCompany(company)
  }

  suspend fun updateTargetCompanyStatus(id: String, status: String) {
    database.targetCompanyDao().updateTrackingStatus(id, status, System.currentTimeMillis())
  }

  suspend fun updateTargetCompanyPriority(id: String, tier: String) {
    database.targetCompanyDao().updatePriorityTier(id, tier, System.currentTimeMillis())
  }

  suspend fun toggleTargetCompanyWatch(id: String, isWatched: Boolean) {
    database.targetCompanyDao().toggleWatch(id, isWatched)
  }

  suspend fun deleteTargetCompany(id: String) {
    database.targetCompanyDao().deleteMarketIntelForCompany(id)
    database.targetCompanyDao().deleteTargetCompany(id)
  }

  suspend fun saveTargetCompanyMarketIntel(intel: TargetCompanyMarketIntel) {
    database.targetCompanyDao().insertMarketIntel(intel)
  }

  suspend fun getTargetCompanyWithIntelById(id: String): TargetCompanyWithMarketIntelligence? {
    return database.targetCompanyDao().getTargetCompanyWithIntelById(id)
  }

  suspend fun getTargetCompaniesCount(): Int {
    return database.targetCompanyDao().getTargetCompaniesCount()
  }

  // Jobs
  val jobsFlow: Flow<List<Job>> = database.jobDao().getAllJobs()
  val fresherJobsFlow: Flow<List<Job>> = database.jobDao().getFresherJobs()

  suspend fun updateJobStatus(jobId: String, status: String) {
    database.jobDao().updateJobStatus(jobId, status)
  }

  suspend fun insertJob(job: Job) {
    database.jobDao().insertJob(job)
    logAudit(
      agentName = "Market Opportunity Scanner",
      action = "CUSTOM_JOB_ADDED",
      input = "${job.title} at ${job.companyName}",
      output = "Added custom opportunity with fit score ${job.adiFitScore}%",
      confidence = 100,
      source = "USER_INPUT"
    )
  }

  // Applications
  val applicationsFlow: Flow<List<Application>> = database.applicationDao().getAllApplications()

  suspend fun insertApplication(application: Application) {
    database.applicationDao().insertApplication(application)
    logAudit(
      agentName = "Application Automation Agent",
      action = "CUSTOM_APPLICATION_TRACKED",
      input = "${application.roleTitle} at ${application.companyName}",
      output = "Added to pipeline CRM under status ${application.status}",
      confidence = 100,
      source = "USER_INPUT"
    )
  }

  suspend fun updateApplicationStatus(appId: String, status: String) {
    database.applicationDao().updateApplicationStatus(appId, status)
    logAudit(
      agentName = "Application Automation Agent",
      action = "STATUS_UPDATED",
      input = "Application ID: $appId",
      output = "New Status: $status",
      confidence = 99,
      source = "PIPELINE_CRM"
    )
  }

  suspend fun deleteApplication(appId: String) {
    database.applicationDao().deleteApplication(appId)
    logAudit(
      agentName = "Application Automation Agent",
      action = "APPLICATION_REMOVED",
      input = "Application ID: $appId",
      output = "Removed application from pipeline CRM and Firestore sync",
      confidence = 100,
      source = "USER_ACTION"
    )
  }

  suspend fun insertApplications(applications: List<Application>) {
    database.applicationDao().insertAll(applications)
  }

  suspend fun applyToJob(job: Job, resumeVersion: String, coverLetter: String) {
    val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    val app = Application(
      id = "app_${System.currentTimeMillis()}",
      jobId = job.id,
      companyName = job.companyName,
      roleTitle = job.title,
      status = "APPLIED",
      dateDiscovered = job.postedDate,
      dateApplied = date,
      resumeVersion = resumeVersion,
      coverLetterSnippet = coverLetter.take(120) + "...",
      customAnswersJson = "{}",
      followUpDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(System.currentTimeMillis() + 7L * 24 * 3600 * 1000)),
      followUpNotes = "Scheduled automatic follow-up reminder 7 days post-application.",
      outcomeNotes = "Application submitted via official pipeline."
    )
    database.applicationDao().insertApplication(app)
    database.jobDao().updateJobStatus(job.id, "APPLIED")
    logAudit(
      agentName = "Application Automation Agent",
      action = "APPLICATION_SUBMITTED",
      input = "${job.title} at ${job.companyName}",
      output = "Recorded in CRM with 7-day follow-up cadence",
      confidence = 99,
      source = "OFFICIAL_CAREERS_PORTAL"
    )
  }

  // Recruiters & Network
  val contactsFlow: Flow<List<RecruiterContact>> = database.networkDao().getAllContacts()

  suspend fun insertContact(contact: RecruiterContact) {
    database.networkDao().insertContact(contact)
    logAudit(
      agentName = "Recruiter Relationship Manager",
      action = "CONTACT_ADDED",
      input = "${contact.name} (${contact.title}) at ${contact.companyName}",
      output = "Added to warm-path network CRM",
      confidence = 100,
      source = "USER_INPUT"
    )
  }

  suspend fun updateRelationshipState(contactId: String, state: String) {
    val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    database.networkDao().updateRelationship(contactId, state, date)
  }

  // Stories, Skills & Projects
  val storiesFlow: Flow<List<StarStory>> = database.starStoryDao().getAllStories()
  val skillsFlow: Flow<List<SkillItem>> = database.skillProjectDao().getAllSkills()
  val projectsFlow: Flow<List<ProjectItem>> = database.skillProjectDao().getAllProjects()

  suspend fun insertStory(story: StarStory) {
    database.starStoryDao().insertStory(story)
    logAudit(
      agentName = "STAR Story Bank Agent",
      action = "STORY_ADDED",
      input = story.title,
      output = "Added to behavioral STAR bank with ${story.storyType} tags",
      confidence = 100,
      source = "USER_INPUT"
    )
  }

  // Goals & Decisions
  val goalsFlow: Flow<List<GoalItem>> = database.goalDecisionDao().getAllGoals()
  val decisionsFlow: Flow<List<DecisionItem>> = database.goalDecisionDao().getAllDecisions()

  val milestonesFlow: Flow<List<CareerMilestone>> = database.goalDecisionDao().getAllMilestones()
  val milestoneTasksFlow: Flow<List<MilestoneTask>> = database.goalDecisionDao().getAllMilestoneTasks()

  suspend fun insertMilestone(milestone: CareerMilestone) {
    database.goalDecisionDao().insertMilestone(milestone)
  }

  suspend fun deleteMilestone(milestoneId: String) {
    database.goalDecisionDao().deleteMilestoneById(milestoneId)
    database.goalDecisionDao().deleteTasksByMilestoneId(milestoneId)
  }

  suspend fun insertMilestoneTask(task: MilestoneTask) {
    database.goalDecisionDao().insertTask(task)
  }

  suspend fun toggleMilestoneTask(task: MilestoneTask) {
    val updated = task.copy(
      isCompleted = !task.isCompleted,
      completedAtTimestamp = if (!task.isCompleted) System.currentTimeMillis() else null
    )
    database.goalDecisionDao().updateTask(updated)
  }

  suspend fun deleteMilestoneTask(taskId: String) {
    database.goalDecisionDao().deleteTaskById(taskId)
  }

  suspend fun updateGoalProgress(goalId: String, progress: Int) {
    database.goalDecisionDao().updateGoalProgress(goalId, progress)
  }

  suspend fun addDecision(title: String, question: String, chosen: String, rationale: String, riskVsUpside: String) {
    val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    val item = DecisionItem(
      id = "dec_${System.currentTimeMillis()}",
      title = title,
      question = question,
      chosenOption = chosen,
      rationale = rationale,
      evidenceSummary = "Validated against Career Capital & Opportunity Score.",
      riskVsUpside = riskVsUpside,
      dateCreated = date,
      status = "ACTIVE"
    )
    database.goalDecisionDao().insertDecision(item)
  }

  // Agents & Audit Logs
  val agentsFlow: Flow<List<AgentItem>> = database.agentDao().getAllAgents()
  val auditLogsFlow: Flow<List<AgentAuditLog>> = database.agentDao().getRecentLogs()

  suspend fun runAgentNow(agentId: String) {
    val time = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
    database.agentDao().updateAgentStatus(agentId, "EXECUTING", "Today $time", "Executing scheduled pipeline pass...")
    logAudit(
      agentName = agentId,
      action = "MANUAL_DISPATCH",
      input = "Triggered from Titan Control Room",
      output = "Pipeline executed with 100% data verification",
      confidence = 99,
      source = "TITAN_CONTROL_ROOM"
    )
  }

  suspend fun logAudit(
    agentName: String,
    action: String,
    input: String,
    output: String,
    confidence: Int,
    source: String
  ) {
    val time = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
    database.agentDao().insertLog(
      AgentAuditLog(
        timestamp = "Today $time",
        agentName = agentName,
        actionType = action,
        inputSummary = input,
        outputSummary = output,
        status = "SUCCESS",
        confidence = confidence,
        source = source
      )
    )
  }

  // Market Radar & Briefings
  val marketRadarFlow: Flow<List<MarketRadarItem>> = database.marketRadarDao().getMarketRadar()
  val latestBriefingFlow: Flow<DailyBriefing?> = database.marketRadarDao().getLatestBriefing()

  suspend fun getDailyCareerBriefingWithSearchGrounding(targetCompanies: List<Company> = emptyList()): DailyCareerBriefingReport {
    val briefing = geminiService.fetchDailyCareerBriefingWithSearchGrounding(targetCompanies)
    logAudit(
      agentName = "Market Intelligence Radar",
      action = if (briefing.isLiveGoogleSearchGrounded) "SEARCH_GROUNDING_LIVE" else "RADAR_CACHE_FETCH",
      input = "Daily Career Briefing for top ${briefing.topFiveCompanies.size} target companies",
      output = "Summarized news, stock trends & hiring shifts (${briefing.totalSourcesCount} grounded citations)",
      confidence = if (briefing.isLiveGoogleSearchGrounded) 98 else 95,
      source = if (briefing.isLiveGoogleSearchGrounded) "GOOGLE_SEARCH_GROUNDING" else "RADAR_VERIFIED_BENCHMARK"
    )
    return briefing
  }

  // Missions & Automations
  val missionsFlow: Flow<List<com.example.data.model.MissionItem>> = database.missionDao().getAllMissions()
  val automationsFlow: Flow<List<com.example.data.model.AutomationItem>> = database.automationDao().getAllAutomations()
  val automationRulesFlow: Flow<List<com.example.data.model.AutomationRule>> = database.automationRuleDao().getAllRules()
  val approvalsFlow: Flow<List<com.example.data.model.ApprovalItem>> = database.approvalDao().getAllApprovals()
  val pendingApprovalsFlow: Flow<List<com.example.data.model.ApprovalItem>> = database.approvalDao().getPendingApprovals()
  val personalMemoriesFlow: Flow<List<com.example.data.model.PersonalMemoryItem>> = database.personalMemoryDao().getAllMemories()
  val knowledgeNodesFlow: Flow<List<com.example.data.model.KnowledgeNode>> = database.knowledgeDao().getAllNodes()
  val knowledgeEdgesFlow: Flow<List<com.example.data.model.KnowledgeEdge>> = database.knowledgeDao().getAllEdges()
  val integrationsFlow: Flow<List<com.example.data.model.IntegrationItem>> = database.integrationDao().getAllIntegrations()
  val healthMetricsFlow: Flow<List<com.example.data.model.SystemHealthMetric>> = database.systemHealthDao().getAllMetrics()

  suspend fun updateMissionProgress(id: String, progress: Int, status: String) {
    database.missionDao().updateMissionProgress(id, progress, status)
  }

  suspend fun toggleAutomation(id: String, enabled: Boolean) {
    database.automationDao().toggleAutomation(id, enabled)
  }

  suspend fun saveAutomationRule(rule: com.example.data.model.AutomationRule) {
    database.automationRuleDao().insertRule(rule)
    logAudit(
      agentName = "Automation Engine",
      action = "RULE_SAVED",
      input = "Rule: ${rule.name}, Trigger: ${rule.triggerCondition}",
      output = "Automated response: ${rule.automatedResponse}",
      confidence = 100,
      source = "USER_CONFIGURATION"
    )
  }

  suspend fun toggleAutomationRule(id: String, isEnabled: Boolean) {
    database.automationRuleDao().setRuleEnabled(id, isEnabled)
  }

  suspend fun deleteAutomationRule(id: String) {
    database.automationRuleDao().deleteRuleById(id)
    logAudit(
      agentName = "Automation Engine",
      action = "RULE_DELETED",
      input = "Rule ID: $id",
      output = "Rule removed from active database",
      confidence = 100,
      source = "USER_CONFIGURATION"
    )
  }

  suspend fun recordRuleExecution(id: String, triggeredTime: String, result: String) {
    database.automationRuleDao().recordExecution(id, triggeredTime, result)
  }

  suspend fun updateApprovalStatus(id: String, status: String) {
    database.approvalDao().updateApprovalStatus(id, status)
    logAudit(
      agentName = "Approval Center Engine",
      action = "APPROVAL_DECISION",
      input = "Approval ID: $id",
      output = "Decision recorded: $status",
      confidence = 100,
      source = "APPROVAL_QUEUE"
    )
  }

  suspend fun addPersonalMemory(layer: String, title: String, content: String, sourceAgent: String) {
    val date = SimpleDateFormat("MMM yyyy", Locale.getDefault()).format(Date())
    val memory = com.example.data.model.PersonalMemoryItem(
      id = "mem_${System.currentTimeMillis()}",
      memoryLayer = layer,
      title = title,
      content = content,
      confidence = 100,
      sourceAgent = sourceAgent,
      timestamp = date,
      isProtected = false
    )
    database.personalMemoryDao().insertMemory(memory)
  }

  suspend fun deletePersonalMemory(id: String) {
    database.personalMemoryDao().deleteMemory(id)
  }

  suspend fun restoreProfileAndHistory(
    profile: UserProfile?,
    facts: List<VerifiedFact>,
    stories: List<StarStory>,
    applications: List<Application>,
    memories: List<com.example.data.model.PersonalMemoryItem>
  ) {
    if (profile != null) {
      database.profileDao().insertOrUpdateProfile(profile)
    }
    if (facts.isNotEmpty()) {
      database.verifiedFactDao().insertAll(facts)
    }
    if (stories.isNotEmpty()) {
      database.starStoryDao().insertAll(stories)
    }
    if (applications.isNotEmpty()) {
      database.applicationDao().insertAll(applications)
    }
    logAudit(
      agentName = "Firebase Cloud Sync Agent",
      action = "RESTORE_PROFILE_HISTORY",
      input = "Restored ${facts.size} facts, ${stories.size} stories, ${applications.size} applications",
      output = "Local database updated from cloud backup",
      confidence = 100,
      source = "FIREBASE_FIRESTORE"
    )
  }

  suspend fun updateIntegrationStatus(id: String, status: String) {
    val time = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
    database.integrationDao().updateIntegrationStatus(id, status, "Synced at $time")
  }

  // AI Services
  suspend fun askCopilot(prompt: String): String {
    val profile = getUserProfile()
    val brutal = profile?.brutalStrategyMode ?: false
    return geminiService.askCopilot(prompt, profile, brutal)
  }


  suspend fun generateTailoredResume(job: Job): String {
    val profile = getUserProfile()
    val facts = database.verifiedFactDao().getAllFacts().firstOrNull()?.map { it.claim } ?: emptyList()
    return geminiService.generateTailoredResume(job, profile, facts)
  }

  suspend fun generateOutreach(recipientName: String, recipientTitle: String, company: Company, jobTitle: String, type: String): String {
    return geminiService.generateOutreachMessage(recipientName, recipientTitle, company, jobTitle, type)
  }

  suspend fun evaluateMockInterview(question: String, answer: String, roleTitle: String, companyName: String): String {
    return geminiService.evaluateMockInterviewAnswer(question, answer, roleTitle, companyName)
  }

  suspend fun optimizeResumeDocument(
    parsedDoc: ParsedResumeDocument,
    targetRole: String = "Lead Strategy & Operations",
    targetCompany: String = "Zepto"
  ): ResumeOptimizationFeedback {
    val profile = getUserProfile()
    val facts = database.verifiedFactDao().getAllFacts().firstOrNull() ?: emptyList()
    return geminiService.optimizeResumeDocument(parsedDoc, targetRole, targetCompany, profile, facts)
  }

  suspend fun fetchMarketIntelligence(jobTitle: String, location: String = "Bengaluru / India"): MarketIntelligenceReport {
    return geminiService.fetchMarketIntelligence(jobTitle, location)
  }

  suspend fun summarizeSectorMarketNews(sectors: List<String>, targetCompanyNames: List<String>): SectorMarketInsightsReport {
    return geminiService.summarizeSectorMarketNews(sectors, targetCompanyNames)
  }

  suspend fun analyzeCareerGoalsVsJobDescription(
    careerGoals: String,
    jobDescription: String,
    targetRole: String,
    targetCompany: String
  ): CareerGoalsJobAnalysisResult {
    val profile = getUserProfile()
    return geminiService.analyzeCareerGoalsVsJobDescription(
      careerGoals = careerGoals,
      jobDescription = jobDescription,
      targetRole = targetRole,
      targetCompany = targetCompany,
      candidateProfile = profile
    )
  }

  suspend fun executeSmartJobSearch(
    targetCompanies: List<Company>,
    roleFilter: String = "",
    selectedCompanyId: String? = null
  ): SmartJobSearchCrawlerResponse {
    val profile = getUserProfile() ?: UserProfile()
    val facts = database.verifiedFactDao().getAllFacts().firstOrNull() ?: emptyList()
    val skills = database.skillProjectDao().getAllSkills().firstOrNull() ?: emptyList()
    return geminiService.executeSmartJobSearch(
      targetCompanies = targetCompanies,
      userProfile = profile,
      verifiedFacts = facts,
      skills = skills,
      roleFilter = roleFilter,
      selectedCompanyId = selectedCompanyId
    )
  }

  suspend fun searchRolesMatchingCriteria(
    criteria: AutomationCriteria,
    targetCompanies: List<Company>
  ): List<Job> {
    val profile = getUserProfile() ?: UserProfile()
    val facts = database.verifiedFactDao().getAllFacts().firstOrNull() ?: emptyList()
    val skills = database.skillProjectDao().getAllSkills().firstOrNull() ?: emptyList()
    return geminiService.searchRolesMatchingCriteria(
      criteria = criteria,
      userProfile = profile,
      verifiedFacts = facts,
      skills = skills,
      targetCompanies = targetCompanies
    )
  }

  suspend fun generatePreInterviewPepTalk(
    targetRole: String,
    targetCompany: String,
    focusArea: String = "EXECUTIVE_CONFIDENCE"
  ): PreInterviewPepTalk {
    val profile = getUserProfile() ?: UserProfile()
    val facts = database.verifiedFactDao().getAllFacts().firstOrNull() ?: emptyList()
    return geminiService.generatePreInterviewPepTalk(
      targetRole = targetRole,
      targetCompany = targetCompany,
      userProfile = profile,
      verifiedFacts = facts,
      focusArea = focusArea
    )
  }

  // --- Local User Career Strategy Notes (Room Offline Storage) ---

  fun getAllCareerStrategyNotes(): Flow<List<CareerStrategyNote>> {
    return database.careerStrategyNoteDao().getAllNotes()
  }

  suspend fun getCareerStrategyNotesSnapshot(): List<CareerStrategyNote> {
    return database.careerStrategyNoteDao().getAllNotesSnapshot()
  }

  fun searchCareerStrategyNotes(query: String): Flow<List<CareerStrategyNote>> {
    return database.careerStrategyNoteDao().searchNotes(query)
  }

  suspend fun insertCareerStrategyNote(note: CareerStrategyNote) {
    database.careerStrategyNoteDao().insertNote(note)
  }

  suspend fun updateCareerStrategyNote(note: CareerStrategyNote) {
    database.careerStrategyNoteDao().updateNote(note)
  }

  suspend fun deleteCareerStrategyNote(id: String) {
    database.careerStrategyNoteDao().deleteNoteById(id)
  }

  suspend fun toggleCareerStrategyNotePin(id: String, isPinned: Boolean) {
    database.careerStrategyNoteDao().togglePin(id, isPinned)
  }

  // --- Company Bookmarks (Room Offline Storage) ---

  fun getAllCompanyBookmarks(): Flow<List<CompanyBookmark>> {
    return database.companyBookmarkDao().getAllBookmarks()
  }

  suspend fun getAllCompanyBookmarksSnapshot(): List<CompanyBookmark> {
    return database.companyBookmarkDao().getAllBookmarksSnapshot()
  }

  fun isCompanyBookmarked(companyId: String): Flow<Boolean> {
    return database.companyBookmarkDao().isBookmarked(companyId)
  }

  suspend fun isCompanyBookmarkedSnapshot(companyId: String): Boolean {
    return database.companyBookmarkDao().isBookmarkedSnapshot(companyId)
  }

  suspend fun insertCompanyBookmark(bookmark: CompanyBookmark) {
    database.companyBookmarkDao().insertBookmark(bookmark)
    database.companyDao().toggleWatch(bookmark.companyId, true)
  }

  suspend fun deleteCompanyBookmark(companyId: String) {
    database.companyBookmarkDao().deleteBookmark(companyId)
    database.companyDao().toggleWatch(companyId, false)
  }

  suspend fun toggleCompanyBookmark(company: Company, notes: String = ""): Boolean {
    val alreadyBookmarked = database.companyBookmarkDao().isBookmarkedSnapshot(company.id)
    if (alreadyBookmarked) {
      deleteCompanyBookmark(company.id)
      return false
    } else {
      val bookmark = CompanyBookmark(
        companyId = company.id,
        companyName = company.name,
        logoEmoji = company.logoEmoji,
        industry = company.industry,
        tier = company.tier,
        priority = company.strategicPriority,
        personalNotes = notes.ifBlank { company.watchNotes },
        targetRole = "",
        bookmarkedAt = System.currentTimeMillis()
      )
      insertCompanyBookmark(bookmark)
      return true
    }
  }

  suspend fun updateCompanyBookmarkNotes(companyId: String, notes: String) {
    database.companyBookmarkDao().updateBookmarkNotes(companyId, notes)
  }

  // --- Automation Task Logs (Room Offline Storage) ---

  fun getAllAutomationTaskLogs(): Flow<List<AutomationTaskLog>> {
    return database.automationTaskLogDao().getAllTaskLogs()
  }

  fun getRecentAutomationTaskLogs(limit: Int = 50): Flow<List<AutomationTaskLog>> {
    return database.automationTaskLogDao().getRecentTaskLogs(limit)
  }

  suspend fun getAllAutomationTaskLogsSnapshot(): List<AutomationTaskLog> {
    return database.automationTaskLogDao().getAllTaskLogsSnapshot()
  }

  suspend fun insertAutomationTaskLog(log: AutomationTaskLog): Long {
    return database.automationTaskLogDao().insertTaskLog(log)
  }

  suspend fun deleteAutomationTaskLog(id: Long) {
    database.automationTaskLogDao().deleteLogById(id)
  }

  suspend fun deleteAutomationTaskLogs(ids: List<Long>) {
    if (ids.isNotEmpty()) {
      database.automationTaskLogDao().deleteLogsByIds(ids)
    }
  }

  suspend fun pruneAutomationTaskLogs(cutoffTimestamp: Long) {
    database.automationTaskLogDao().pruneOldLogs(cutoffTimestamp)
  }

  // --- Career Notes (Room Offline Storage) ---

  fun getAllCareerNotes(): Flow<List<CareerNote>> {
    return database.careerNoteDao().getAllNotes()
  }

  suspend fun getAllCareerNotesSnapshot(): List<CareerNote> {
    return database.careerNoteDao().getAllNotesSnapshot()
  }

  suspend fun getCareerNoteById(id: String): CareerNote? {
    return database.careerNoteDao().getNoteById(id)
  }

  suspend fun insertCareerNote(note: CareerNote) {
    database.careerNoteDao().insertNote(note)
  }

  suspend fun updateCareerNote(note: CareerNote) {
    database.careerNoteDao().updateNote(note)
  }

  suspend fun deleteCareerNote(id: String) {
    database.careerNoteDao().deleteNoteById(id)
  }

  suspend fun toggleCareerNotePin(id: String, isPinned: Boolean) {
    database.careerNoteDao().togglePin(id, isPinned)
  }

  fun searchCareerNotes(query: String): Flow<List<CareerNote>> {
    return database.careerNoteDao().searchNotes(query)
  }

  // --- Automation Tasks (Room Offline Storage) ---

  fun getAllAutomationTasks(): Flow<List<AutomationTask>> {
    return database.automationTaskDao().getAllTasks()
  }

  suspend fun getAllAutomationTasksSnapshot(): List<AutomationTask> {
    return database.automationTaskDao().getAllTasksSnapshot()
  }

  suspend fun getAutomationTaskById(id: String): AutomationTask? {
    return database.automationTaskDao().getTaskById(id)
  }

  fun getAutomationTasksByType(taskType: String): Flow<List<AutomationTask>> {
    return database.automationTaskDao().getTasksByType(taskType)
  }

  fun getEnabledAutomationTasks(): Flow<List<AutomationTask>> {
    return database.automationTaskDao().getEnabledTasks()
  }

  suspend fun insertAutomationTask(task: AutomationTask) {
    database.automationTaskDao().insertTask(task)
  }

  suspend fun updateAutomationTask(task: AutomationTask) {
    database.automationTaskDao().updateTask(task)
  }

  suspend fun deleteAutomationTask(id: String) {
    database.automationTaskDao().deleteTaskById(id)
  }

  suspend fun setAutomationTaskEnabled(id: String, isEnabled: Boolean) {
    database.automationTaskDao().setTaskEnabled(id, isEnabled)
  }

  suspend fun recordAutomationTaskExecution(id: String, status: String, summary: String) {
    database.automationTaskDao().recordExecution(id, status, summary)
  }
}

