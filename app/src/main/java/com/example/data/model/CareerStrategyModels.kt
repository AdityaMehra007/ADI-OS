package com.example.data.model

enum class CareerMilestoneStatus(val label: String) {
  NOT_STARTED("Not Started"),
  IN_PROGRESS("In Progress"),
  ACQUIRED("Acquired & Verified")
}

data class SkillMilestone(
  val id: String,
  val title: String,
  val category: String, // "AI & Autonomous Ops", "Strategy & Casebooks", "Financial Modeling & SQL", "Executive Leadership", "Network & Dealflow"
  val targetProficiency: String, // "Production Ready", "Advanced Practitioner", "Executive Mastery"
  val status: CareerMilestoneStatus = CareerMilestoneStatus.NOT_STARTED,
  val progress: Int = 0, // 0..100
  val estimatedWeeks: Int = 4,
  val keyLearningOutcomes: List<String> = emptyList(),
  val proofOfWorkArtifact: String = "",
  val verificationMethod: String = ""
)

data class RoadmapPhase(
  val phaseId: String,
  val phaseNumber: Int,
  val title: String,
  val timeframe: String,
  val targetRoleTier: String,
  val targetCompLakhs: String,
  val focusTheme: String,
  val keyObjectives: List<String>,
  val skillMilestones: List<SkillMilestone>,
  val deliverableArtifacts: List<String>
) {
  val completionPercentage: Int
    get() {
      if (skillMilestones.isEmpty()) return 0
      val totalProgress = skillMilestones.sumOf { it.progress }
      return (totalProgress / skillMilestones.size).coerceIn(0, 100)
    }
}

data class StrategicPillar(
  val id: String,
  val name: String,
  val description: String,
  val weightPercentage: Int,
  val currentScore: Int // 0..100
)

data class CareerStrategyRoadmap(
  val id: String,
  val targetRole: String,
  val targetHorizon: String,
  val targetCompensation: String,
  val executiveSummary: String,
  val strategicPillars: List<StrategicPillar>,
  val phases: List<RoadmapPhase>,
  val tacticalPlaybook: List<String>,
  val geminiStrategicAnalysis: String,
  val confidenceScore: Int = 94,
  val lastUpdated: String = "Just now"
) {
  val totalMilestonesCount: Int
    get() = phases.sumOf { it.skillMilestones.size }

  val acquiredMilestonesCount: Int
    get() = phases.sumOf { it.skillMilestones.count { m -> m.status == CareerMilestoneStatus.ACQUIRED } }

  val overallProgressPercentage: Int
    get() {
      val total = totalMilestonesCount
      if (total == 0) return 0
      val sum = phases.sumOf { it.skillMilestones.sumOf { m -> m.progress } }
      return (sum / total).coerceIn(0, 100)
    }
}
