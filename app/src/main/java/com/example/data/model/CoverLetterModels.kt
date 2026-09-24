package com.example.data.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

/**
 * Tone and strategic positioning options for Gemini cover letter generation.
 */
enum class CoverLetterTone(
  val displayName: String,
  val subtitle: String,
  val badgeEmoji: String
) {
  METRIC_DRIVEN(
    displayName = "Impact & Metric-Heavy",
    subtitle = "Quantified wins, percentages, cost reductions, throughput",
    badgeEmoji = "📈"
  ),
  EXECUTIVE(
    displayName = "Executive & Strategic",
    subtitle = "Visionary, high business acumen, cross-functional leadership",
    badgeEmoji = "👔"
  ),
  MODERN_TECH(
    displayName = "Modern Tech & Startup",
    subtitle = "High velocity, bias for action, agile culture, technical leverage",
    badgeEmoji = "⚡"
  ),
  BESPOKE_CONSULTING(
    displayName = "Structured Consulting",
    subtitle = "MECE frameworks, root-cause resolution, strategic roadmaps",
    badgeEmoji = "🎯"
  ),
  CONCISE(
    displayName = "Concise & Direct",
    subtitle = "Sub-250 words, maximum punch, zero filler words",
    badgeEmoji = "✂️"
  )
}

/**
 * Fully structured cover letter generated via the Gemini API
 * grounded on the candidate's uploaded profile and target job description.
 */
data class CoverLetterGenerated(
  val id: String = UUID.randomUUID().toString(),
  val targetRole: String,
  val targetCompany: String,
  val recipientName: String = "Hiring Team",
  val recipientTitle: String = "Head of Talent & Operations",
  val companyAddress: String = "Bengaluru, Karnataka, India",
  val dateFormatted: String = SimpleDateFormat("MMMM d, yyyy", Locale.US).format(Date()),
  val tone: CoverLetterTone = CoverLetterTone.METRIC_DRIVEN,
  val subjectLine: String,
  val salutation: String,
  val openingHook: String,
  val bodyParagraphImpact: String,
  val bodyParagraphSkills: String,
  val bodyParagraphCultureMoat: String,
  val callToAction: String,
  val formalSignOff: String = "Sincerely,",
  val candidateName: String = "Aditya \"Adi\" Mehra",
  val candidateTitle: String = "Operations & Strategy Lead",
  val candidateContact: String = "Bengaluru, India | +91-7003456624 | adityamehra799@gmail.com",
  val fullFormattedLetter: String,
  val keyStrategicHooks: List<String> = emptyList(),
  val injectedQuantifiedMetrics: List<String> = emptyList(),
  val matchedAtsKeywords: List<String> = emptyList(),
  val estimatedReadTimeSeconds: Int = 85,
  val atsMatchScore: Int = 95,
  val isLiveGeminiApi: Boolean = false,
  val modelVersion: String = "gemini-3.5-flash",
  val timestamp: Long = System.currentTimeMillis()
)
