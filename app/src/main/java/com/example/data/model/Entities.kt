package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlin.math.roundToInt

@Entity(tableName = "user_profile")
data class UserProfile(
  @PrimaryKey val id: String = "ADI_PRIMARY_ID",
  val name: String = "Aditya Mehra",
  val location: String = "Bengaluru, India",
  val careerStage: String = "Early-Career / Graduate",
  val educationDegree: String = "BBA (Bachelor of Business Administration)",
  val educationSpecialization: String = "International Business",
  val university: String = "Dayananda Sagar University (DSU), Bengaluru",
  val targetRoles: String = "Business Analyst, Associate Business Analyst, Strategy & Operations, AI Operations, BizDev Associate, Product Operations",
  val interests: String = "AI, Technology, International Business, Strategy, Operations, Automation, Fintech, Markets, Entrepreneurship",
  val careerScore: Int = 88,
  val careerCapitalScore: Int = 85,
  val aiLeverageScore: Int = 92,
  val brutalStrategyMode: Boolean = false,
  val automationMode: String = "APPROVAL", // AUTO, APPROVAL, MANUAL
  val profileCompleteness: Int = 96,
  val totalApplicationsTracked: Int = 24,
  val totalInterviewsSecured: Int = 4,
  val totalActiveOffers: Int = 1
)

@Entity(tableName = "verified_facts")
data class VerifiedFact(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val category: String, // EDUCATION, EXPERIENCE, PROJECT, SKILL, METRIC, CERTIFICATE
  val claim: String,
  val evidenceDetails: String,
  val isVerified: Boolean = true,
  val tags: String
)

@Entity(tableName = "companies")
data class Company(
  @PrimaryKey val id: String,
  val name: String,
  val logoEmoji: String = "🏢",
  val website: String,
  val careersUrl: String,
  val industry: String,
  val subIndustry: String = "Technology",
  val hqLocation: String = "Bengaluru, India",
  val bengaluruPresence: String = "Active", // e.g. "Major R&D & Tech Hub (Outer Ring Road / Bellandur)"
  val indiaPresence: String = "Active",
  val employeeScale: String = "1,000+",
  val tier: String, // S+, S, A, B, C
  val strategicPriority: String, // DREAM, HIGH_PRIORITY, WATCH, NETWORKING, APPLIED, INTERVIEWING, OFFER
  val hiringVelocity: String = "STABLE", // ACCELERATING, STABLE, DECLINING
  val aiAdoptionLevel: String = "HIGH", // VERY HIGH, HIGH, MODERATE
  val compensationTier: String = "Tier-1", // ₹12L - ₹24L+, Tier-1 Global
  val isOpenForEarlyCareer: Boolean = true,
  val isWatched: Boolean = false,
  val watchNotes: String = "",
  val scoreExplanation: String = "",
  val activeOpeningsCount: Int = 0,
  val latestNewsSummary: String = "",
  val newsSummaryTimestamp: String = "",
  val isCustomAdded: Boolean = false
)

@Entity(tableName = "jobs")
data class Job(
  @PrimaryKey val id: String,
  val companyId: String,
  val companyName: String,
  val title: String,
  val roleFamily: String, // BUSINESS_ANALYST, STRATEGY_OPS, AI_OPERATIONS, BIZDEV, CONSULTING
  val department: String,
  val location: String,
  val city: String,
  val country: String,
  val remoteStatus: String, // ONSITE, HYBRID, REMOTE
  val employmentType: String, // FULL_TIME, GRADUATE_PROGRAM, INTERN_TO_HIRE
  val salaryRange: String,
  val currency: String = "INR",
  val experienceRequirement: String, // "0-1 Years", "Entry Level / Graduate"
  val adiFitScore: Int, // 0-100
  val opportunityScore: Int, // 0-100
  val whyItFits: String,
  val whyItDoesntFit: String,
  val missingRequirements: String,
  val recommendedAction: String,
  val applicationUrl: String,
  val officialSource: String,
  val sourceQuality: String = "OFFICIAL_CAREERS_PORTAL",
  val postedDate: String,
  val isFresherFriendly: Boolean = true,
  val strategicPriority: String, // APPLY_NOW, HIGH_PRIORITY, REVIEW, MANUAL_ACTION, WATCH, DO_NOT_APPLY
  val status: String = "ACTIVE"
)

@Entity(tableName = "applications")
data class Application(
  @PrimaryKey val id: String,
  val jobId: String,
  val companyName: String,
  val roleTitle: String,
  val status: String, // DISCOVERED, SHORTLISTED, PREPARING, APPROVAL_REQUIRED, APPLIED, VIEWED, RECRUITER_CONTACT, ASSESSMENT, INTERVIEW, FINAL_ROUND, OFFER, REJECTED, WITHDRAWN, CLOSED
  val dateDiscovered: String,
  val dateApplied: String,
  val resumeVersion: String,
  val coverLetterSnippet: String,
  val customAnswersJson: String,
  val followUpDate: String,
  val followUpNotes: String,
  val outcomeNotes: String,
  val rejectionAnalysis: String = "",
  val interviewScore: Int = 0
)

@Entity(tableName = "recruiter_contacts")
data class RecruiterContact(
  @PrimaryKey val id: String,
  val companyId: String,
  val companyName: String,
  val name: String,
  val title: String,
  val department: String,
  val linkedinUrl: String,
  val email: String,
  val relationshipState: String, // NEW, AWARE, CONNECTED, CONVERSATION, REFERRAL, MENTOR, STRONG_RELATIONSHIP
  val lastInteractionDate: String,
  val notes: String,
  val nextFollowUpDate: String
)

@Entity(tableName = "star_stories")
data class StarStory(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val title: String,
  val storyType: String, // LEADERSHIP, PROBLEM_SOLVING, TEAMWORK, FAILURE, ACHIEVEMENT, CUSTOMER, BUSINESS, CONFLICT, LEARNING
  val situation: String,
  val task: String,
  val action: String,
  val result: String,
  val verifiedFactsUsed: String,
  val relevantSkills: String,
  val matchedQuestions: String
)

@Entity(tableName = "skills")
data class SkillItem(
  @PrimaryKey val id: String,
  val name: String,
  val category: String, // BUSINESS_ANALYTICS, AI_AUTOMATION, STRATEGY, OPERATIONS, INTERNATIONAL_BUSINESS, SALES, FINANCE
  val proficiency: String, // BEGINNER, INTERMEDIATE, ADVANCED, EXPERT
  val isTargetDemand: Boolean,
  val isGap: Boolean,
  val aiMultiplierDesc: String, // e.g. "Business Analysis + AI: Automates market sizing & SQL queries"
  val recommendedProject: String
)

@Entity(tableName = "projects")
data class ProjectItem(
  @PrimaryKey val id: String,
  val title: String,
  val projectType: String, // ADI_PROJECT_LAB, ADI_VENTURE_LAB
  val problem: String,
  val solution: String,
  val technology: String,
  val businessImpact: String,
  val skillsDemonstrated: String,
  val status: String, // IN_PROGRESS, COMPLETED, PLANNED
  val demoUrl: String,
  val targetRoleRelevance: String
)

@Entity(tableName = "goals")
data class GoalItem(
  @PrimaryKey val id: String,
  val title: String,
  val category: String, // CAREER, INCOME, SKILLS, PROJECTS, NETWORKING, APPLICATIONS, VENTURE
  val horizon: String, // TODAY, WEEKLY_7D, MONTHLY_30D, QUARTERLY_90D, ANNUAL_1YR, HORIZON_3YR
  val targetMetric: String,
  val progressPercent: Int,
  val status: String, // ACTIVE, COMPLETED, BLOCKED
  val nextAction: String
)

@Entity(tableName = "career_milestones")
data class CareerMilestone(
  @PrimaryKey val id: String,
  val title: String,
  val horizon: String, // 6M, 1Y, 2Y, 3Y, 5Y
  val horizonYears: Float, // 0.5f, 1.0f, 2.0f, 3.0f, 5.0f
  val targetQuarter: String, // e.g. "2026-Q4", "2027-Q2", "2028-Q2", "2031-Q2"
  val category: String, // ROLE_TRANSITION, COMPENSATION, LEADERSHIP, TECHNICAL_MASTERY, VENTURE_EQUITY
  val targetMetric: String, // e.g. "₹35-45 LPA Base + ESOPs"
  val strategicRationale: String,
  val status: String = "IN_PROGRESS", // NOT_STARTED, IN_PROGRESS, ON_TRACK, AT_RISK, ACHIEVED
  val priority: String = "HIGH", // CRITICAL, HIGH, STRATEGIC
  val dateCreated: Long = System.currentTimeMillis()
)

@Entity(
  tableName = "milestone_tasks",
  indices = [Index(value = ["milestoneId"])]
)
data class MilestoneTask(
  @PrimaryKey val id: String,
  val milestoneId: String,
  val title: String,
  val description: String = "",
  val category: String = "EXECUTION", // SKILLS, PORTFOLIO, NETWORKING, INTERVIEW, EXECUTION
  val weightPoints: Int = 1, // 1 to 5
  val isCompleted: Boolean = false,
  val completedAtTimestamp: Long? = null,
  val dueDate: String = "",
  val source: String = "USER" // USER, AI_RECOMMENDED
)

data class MilestoneWithTasks(
  val milestone: CareerMilestone,
  val tasks: List<MilestoneTask>
) {
  val totalTasksCount: Int get() = tasks.size
  val completedTasksCount: Int get() = tasks.count { it.isCompleted }
  val totalWeight: Int get() = tasks.sumOf { it.weightPoints }.coerceAtLeast(1)
  val completedWeight: Int get() = tasks.filter { it.isCompleted }.sumOf { it.weightPoints }
  val progressPercent: Int
    get() = if (tasks.isEmpty()) 0 else ((completedWeight.toFloat() / totalWeight) * 100).roundToInt().coerceIn(0, 100)
}

@Entity(tableName = "decisions")
data class DecisionItem(
  @PrimaryKey val id: String,
  val title: String,
  val question: String,
  val chosenOption: String,
  val rationale: String,
  val evidenceSummary: String,
  val riskVsUpside: String,
  val dateCreated: String,
  val status: String // ACTIVE, VALIDATED, RETROSPECTIVE_REVIEW
)

@Entity(tableName = "agents")
data class AgentItem(
  @PrimaryKey val id: String,
  val agentNumber: Int,
  val name: String,
  val roleCategory: String,
  val permissionScope: String,
  val status: String, // ACTIVE, IDLE, EXECUTING, PAUSED
  val lastRunTimestamp: String,
  val currentTask: String,
  val successCount: Int,
  val failureCount: Int,
  val confidenceScore: Int
)

@Entity(tableName = "agent_audit_logs")
data class AgentAuditLog(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val timestamp: String,
  val agentName: String,
  val actionType: String,
  val inputSummary: String,
  val outputSummary: String,
  val status: String,
  val confidence: Int,
  val source: String
)

@Entity(tableName = "market_radar")
data class MarketRadarItem(
  @PrimaryKey val id: String,
  val metricName: String,
  val region: String, // BENGALURU, INDIA, GLOBAL
  val value: String,
  val trend: String, // ACCELERATING, STABLE, DECLINING
  val insightSummary: String
)

@Entity(tableName = "daily_briefings")
data class DailyBriefing(
  @PrimaryKey val id: String,
  val dateString: String,
  val headline: String,
  val keyActions: String,
  val marketNews: String,
  val strategicDirective: String,
  val interviewFocus: String
)

@Entity(tableName = "missions")
data class MissionItem(
  @PrimaryKey val id: String,
  val title: String,
  val objective: String,
  val keyResults: String,
  val milestones: String,
  val tasks: String,
  val assignedAgentId: String,
  val dependencies: String,
  val deadline: String,
  val riskLevel: String, // LOW, MODERATE, HIGH, CRITICAL
  val targetMetric: String,
  val progressPercent: Int,
  val status: String // ACTIVE, IN_PROGRESS, COMPLETED, BLOCKED
)

@Entity(tableName = "automations")
data class AutomationItem(
  @PrimaryKey val id: String,
  val name: String,
  val triggerType: String, // SCHEDULED_EVENT, STATE_CHANGE, INCOMING_DATA, THRESHOLD, USER_REQUEST
  val triggerCondition: String,
  val actionDescription: String,
  val verificationPolicy: String,
  val ownerAgentId: String,
  val permissionLevel: String, // LEVEL_1_READ, LEVEL_2_ANALYZE, LEVEL_3_PREPARE, LEVEL_4_APPROVAL, LEVEL_5_AUTONOMOUS
  val executionCount: Int,
  val retryCount: Int,
  val failureState: String,
  val isEnabled: Boolean = true
)

@Entity(tableName = "approvals")
data class ApprovalItem(
  @PrimaryKey val id: String,
  val actionTitle: String,
  val whyReason: String,
  val toolUsed: String,
  val targetEntity: String,
  val dataInvolved: String,
  val expectedResult: String,
  val riskLevel: String, // LOW, MEDIUM, HIGH, SENSITIVE
  val requestedByAgent: String,
  val timestamp: String,
  val status: String // PENDING, APPROVED, REJECTED, AUTHORIZED_FUTURE
)

@Entity(tableName = "personal_memories")
data class PersonalMemoryItem(
  @PrimaryKey val id: String,
  val memoryLayer: String, // TEMPORARY_CONTEXT, ACTIVE_TASK, LONG_TERM_PREFERENCES, PROJECT_MEMORY, VERIFIED_FACT, DECISION, OUTCOME
  val title: String,
  val content: String,
  val confidence: Int,
  val sourceAgent: String,
  val timestamp: String,
  val isProtected: Boolean = false
)

@Entity(tableName = "knowledge_nodes")
data class KnowledgeNode(
  @PrimaryKey val id: String,
  val nodeType: String, // USER, GOAL, SKILL, EXPERIENCE, PROJECT, COMPANY, CONTACT, JOB, APPLICATION, INTERVIEW, MISSION
  val label: String,
  val category: String,
  val details: String,
  val significanceScore: Int
)

@Entity(tableName = "knowledge_edges")
data class KnowledgeEdge(
  @PrimaryKey val id: String,
  val sourceId: String,
  val targetId: String,
  val relationshipType: String, // TARGETS, DEMONSTRATES, DRIVES, APPLIED_TO, INTERVIEWS_AT, REQUIRES, CONNECTS_TO
  val weight: Float = 1.0f
)

@Entity(tableName = "integrations")
data class IntegrationItem(
  @PrimaryKey val id: String,
  val serviceName: String,
  val serviceType: String, // GOOGLE_SERVICES, GMAIL, GOOGLE_CALENDAR, GOOGLE_DRIVE, GOOGLE_SHEETS, GITHUB, SLACK, CRM, MCP_TOOL, LINKEDIN
  val status: String, // CONNECTED, NOT_CONNECTED, REQUIRES_AUTHORIZATION, REQUIRES_API_KEY, DEMO_MODE
  val description: String,
  val iconEmoji: String = "🔌",
  val lastSyncTimestamp: String
)

@Entity(tableName = "system_health_metrics")
data class SystemHealthMetric(
  @PrimaryKey val id: String,
  val metricName: String,
  val category: String, // API_LATENCY, DB_PERFORMANCE, AGENT_UPTIME, MEMORY_USAGE, SECURITY_SCORE, MODEL_TOKENS
  val value: String,
  val unit: String,
  val status: String, // HEALTHY, WARNING, CRITICAL
  val description: String
)

data class MarketSalaryBenchmark(
  val experienceLevel: String, // e.g. "0-2 Yrs (Associate)", "2-5 Yrs (Lead)", "5-8 Yrs (Principal)"
  val percentile25Lakhs: Float,
  val medianLakhs: Float,
  val percentile75Lakhs: Float,
  val percentile90Lakhs: Float,
  val currencySymbol: String = "₹",
  val unit: String = "LPA"
)

data class IndustryTrend(
  val trendTitle: String,
  val category: String, // "Hiring Velocity", "Emerging Tech/AI", "Compensation Surge", "Talent Shortage"
  val growthPercentage: String,
  val impactDescription: String,
  val keyCompaniesHiring: List<String>
)

data class MarketIntelligenceReport(
  val id: String = "market_intel_${System.currentTimeMillis()}",
  val jobTitle: String,
  val location: String = "Bengaluru / India Tech Hubs",
  val timestampFormatted: String,
  val executiveSummary: String,
  val salaryBenchmarks: List<MarketSalaryBenchmark> = emptyList(),
  val industryTrends: List<IndustryTrend> = emptyList(),
  val topInDemandSkills: List<String> = emptyList(),
  val negotiationTips: List<String> = emptyList(),
  val isGroundedWithGemini: Boolean = true,
  val isSavedToFirestore: Boolean = false
)

data class ContactInteraction(
  val id: String = "interaction_${System.currentTimeMillis()}",
  val contactId: String,
  val contactName: String = "",
  val interactionType: String, // "LinkedIn InMail", "Coffee Chat", "Referral Request", "Screening Call", "Follow-up"
  val dateFormatted: String,
  val summaryNotes: String,
  val nextSteps: String = ""
)

data class FollowUpReminder(
  val id: String = "rem_${System.currentTimeMillis()}",
  val contactId: String,
  val contactName: String,
  val companyName: String,
  val dueDateFormatted: String,
  val reminderSubject: String,
  val isCompleted: Boolean = false,
  val priorityLevel: String = "HIGH" // "HIGH", "MEDIUM", "LOW"
)

data class ProfileImprovementSuggestion(
  val category: String, // "Headline & Value Prop", "Quantified Achievements", "Skill Gap Closure", "Executive Narrative"
  val priority: String, // "HIGH", "CRITICAL", "MEDIUM"
  val currentGap: String,
  val actionableSuggestion: String,
  val beforeExample: String,
  val afterExample: String
)

data class CareerGoalsJobAnalysisResult(
  val targetRole: String,
  val targetCompany: String,
  val careerGoalsSummary: String,
  val alignmentScore: Int, // 0..100
  val fitAssessment: String,
  val keyStrengths: List<String>,
  val missingKeywordsAndSkills: List<String>,
  val suggestions: List<ProfileImprovementSuggestion>,
  val tacticalNextSteps: List<String>,
  val isGroundedWithGemini: Boolean = true
)

data class PreInterviewPepTalk(
  val targetRole: String,
  val targetCompany: String,
  val candidateName: String = "Adi",
  val speechScript: String,
  val mindsetAnchors: List<String>,
  val powerAffirmations: List<String>,
  val physicalGroundingTip: String,
  val thirtySecondOpener: String,
  val estimatedDurationSeconds: Int = 120,
  val isGeminiGenerated: Boolean = true
)

enum class BreathingPattern(
  val title: String,
  val subtitle: String,
  val inhaleSec: Int,
  val holdInhaleSec: Int,
  val exhaleSec: Int,
  val holdExhaleSec: Int
) {
  BOX_BREATHING(
    title = "Box Breathing (Navy SEAL)",
    subtitle = "4-4-4-4 equal rhythm to reset adrenaline & anchor executive composure",
    inhaleSec = 4,
    holdInhaleSec = 4,
    exhaleSec = 4,
    holdExhaleSec = 4
  ),
  RELAXING_478(
    title = "4-7-8 Vagus Grounding",
    subtitle = "Extended exhale activates deep parasympathetic serenity",
    inhaleSec = 4,
    holdInhaleSec = 7,
    exhaleSec = 8,
    holdExhaleSec = 0
  ),
  RESONANCE(
    title = "Coherent Resonance",
    subtitle = "Balanced 5s rhythm aligns cardiac coherence & vocal resonance",
    inhaleSec = 5,
    holdInhaleSec = 1,
    exhaleSec = 5,
    holdExhaleSec = 1
  );

  val totalCycleDuration: Int get() = inhaleSec + holdInhaleSec + exhaleSec + holdExhaleSec
}


