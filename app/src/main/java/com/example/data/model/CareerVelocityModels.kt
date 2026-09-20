package com.example.data.model

/**
 * Career Velocity Data Models
 *
 * Tracks the user's velocity of progress against specific target company job role requirements over time.
 * Powers the D3-driven Career Velocity Dashboard.
 */

enum class RequirementFulfillmentStatus(val label: String, val badgeColorHex: Long) {
  FULFILLED("Fulfilled & Verified", 0xFF10B981), // Emerald
  IN_PROGRESS("In Progress", 0xFF00E5FF),        // Cyan
  CRITICAL_GAP("Priority Gap", 0xFFEF4444),      // Crimson
  DEVELOPING("Developing", 0xFFF59E0B)           // Amber
}

data class RoleRequirementItem(
  val id: String,
  val name: String,
  val category: String, // "Data & Analytics", "Strategic Ops", "Product & GTM", "Executive Leadership", "Domain Systems"
  val weightPercent: Int,
  val requiredProficiency: Int, // 0-100 target
  val currentProgress: Int,     // 0-100 actual
  val status: RequirementFulfillmentStatus,
  val gapDetails: String,
  val verificationEvidence: String
) {
  val isFulfilled: Boolean get() = currentProgress >= requiredProficiency
}

data class VelocityTimelinePoint(
  val periodLabel: String,      // e.g. "Apr 2026", "May 2026", "Jun 2026", "Jul 2026", "Aug 2026", "Sep (Now)", "Nov (Target)"
  val monthIndex: Int,          // 0, 1, 2, 3, 4, 5, 6
  val userProgressPercent: Float,        // e.g. 35f, 48f, 58f, 67f, 78f, 88f, 100f
  val targetRequirementBaseline: Float,  // benchmark curve (e.g. 40f, 50f, 60f, 70f, 80f, 90f, 100f)
  val velocityDelta: Float,              // rate of acceleration: +% / mo
  val isProjection: Boolean = false,
  val milestoneAchieved: String? = null
)

data class TargetCompanyRole(
  val id: String,
  val companyId: String,
  val companyName: String,
  val logoEmoji: String,
  val roleTitle: String,
  val department: String,
  val location: String = "Bengaluru, India (Hybrid)",
  val experienceTier: String, // "Associate / 0-2 yrs", "Mid-Level / 2-4 yrs", "Lead / 3-5 yrs"
  val targetCompensation: String, // e.g. "₹18L - ₹26L + Equity"
  val overallReadinessScore: Int, // 0-100
  val monthlyVelocityRate: Float, // e.g. +14.5% / month
  val velocityPacingStatus: String, // "Accelerating", "On Track", "Surging"
  val daysToFullReadiness: Int, // e.g. 45 days
  val primaryFocusArea: String,
  val requirements: List<RoleRequirementItem>,
  val timeline: List<VelocityTimelinePoint>,
  val executivePitch: String
) {
  val fulfilledRequirementsCount: Int get() = requirements.count { it.isFulfilled }
  val totalRequirementsCount: Int get() = requirements.size
  val gapRequirementsCount: Int get() = requirements.count { it.status == RequirementFulfillmentStatus.CRITICAL_GAP }
}

data class CareerVelocityReport(
  val overallVelocityIndex: Int = 86,
  val averageMonthlyGain: Float = 13.8f,
  val pacingVerdict: String = "ACCELERATING • 1.4x Market Benchmark",
  val roles: List<TargetCompanyRole>,
  val activeRoleId: String,
  val lastUpdated: String = "Live • Grounded Radar",
  val nextLeverageAction: String = "Publish SQL & Autonomous Operations Case Study to close Google GTM verification gap."
)
