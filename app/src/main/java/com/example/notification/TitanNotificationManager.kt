package com.example.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity
import com.example.R
import com.example.data.model.Company
import com.example.data.model.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TitanNotificationManager private constructor(private val appContext: Context) {

  companion object {
    const val CHANNEL_HIGH_MATCH_JOBS = "channel_high_match_jobs"
    const val CHANNEL_COMPANY_INTEL = "channel_company_intel"
    const val CHANNEL_APPLICATION_ALERTS = "channel_application_alerts"
    const val CHANNEL_SOVEREIGN_OPERATIONS = "channel_sovereign_operations"

    const val EXTRA_TARGET_SCREEN = "extra_target_screen"
    const val EXTRA_ITEM_ID = "extra_item_id"
    const val EXTRA_ITEM_NAME = "extra_item_name"

    @Volatile
    private var INSTANCE: TitanNotificationManager? = null

    fun getInstance(context: Context): TitanNotificationManager {
      return INSTANCE ?: synchronized(this) {
        val instance = TitanNotificationManager(context.applicationContext)
        INSTANCE = instance
        instance
      }
    }
  }

  private val notificationManager = appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

  // In-app Alert History State
  private val _alerts = MutableStateFlow<List<NotificationAlertItem>>(emptyList())
  val alerts: StateFlow<List<NotificationAlertItem>> = _alerts.asStateFlow()

  // Unread Alert Count State
  private val _unreadCount = MutableStateFlow(0)
  val unreadCount: StateFlow<Int> = _unreadCount.asStateFlow()

  // Preferences State
  private val _preferences = MutableStateFlow(NotificationPreferences())
  val preferences: StateFlow<NotificationPreferences> = _preferences.asStateFlow()

  // Track already-notified items to avoid repeat spam
  private val notifiedJobIds = mutableSetOf<String>()
  private val notifiedCompanyNewsKeys = mutableSetOf<String>()

  init {
    createNotificationChannels()
    seedInitialAlerts()
  }

  private fun seedInitialAlerts() {
    val initialList = listOf(
      NotificationAlertItem(
        id = "notif_seed_job_1",
        type = NotificationType.HIGH_MATCH_JOB,
        title = "🔥 95% Match: Business Strategy Analyst",
        message = "Swiggy (Corporate Strategy & Planning) • ₹14L - ₹20L • Bengaluru",
        detailedSummary = "High alignment with BBA International Business, quantitative SQL models, and unit economics teardown. Direct early-career track in S-Tier startup.",
        timestamp = "10m ago",
        targetScreen = "JOBS",
        targetItemId = "job_swiggy_strat",
        targetItemName = "Swiggy",
        matchScore = 95,
        priority = NotificationPriority.HIGH,
        isRead = false
      ),
      NotificationAlertItem(
        id = "notif_seed_intel_1",
        type = NotificationType.COMPANY_INTEL,
        title = "🚨 Breaking Intel: Zepto (Impact 94/100)",
        message = "Zepto expands dark store footprint by 40% in Bengaluru & flags strategy associate hiring surge.",
        detailedSummary = "Zepto recently closed \$340M series financing, aggressive expansion into quick-commerce micro-fulfillment. Key interview angle: Rapid inventory turnaround and cross-border vendor SLA optimization.",
        timestamp = "35m ago",
        targetScreen = "COMPANIES",
        targetItemId = "comp_zepto",
        targetItemName = "Zepto",
        impactScore = 94,
        priority = NotificationPriority.CRITICAL,
        isRead = false
      )
    )
    _alerts.value = initialList
    updateUnreadCount()
  }

  private fun createNotificationChannels() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      // 1. High Match Jobs Channel
      val jobChannel = NotificationChannel(
        CHANNEL_HIGH_MATCH_JOBS,
        "High-Match Career Opportunities",
        NotificationManager.IMPORTANCE_HIGH
      ).apply {
        description = "Instant alerts when top-fit jobs (≥85% Match) or target roles are discovered in Bengaluru & India"
        enableLights(true)
        lightColor = 0xFF00E5FF.toInt() // TitanCyan
        enableVibration(true)
        setShowBadge(true)
      }

      // 2. Followed Company Breaking Intel Channel
      val intelChannel = NotificationChannel(
        CHANNEL_COMPANY_INTEL,
        "Followed Company Breaking Intel & Radar",
        NotificationManager.IMPORTANCE_HIGH
      ).apply {
        description = "Live market radar updates, funding rounds, strategic shifts, and hiring signals for followed companies"
        enableLights(true)
        lightColor = 0xFFFFB300.toInt() // TitanGold
        enableVibration(true)
        setShowBadge(true)
      }

      // 3. Application Pipeline Channel
      val appChannel = NotificationChannel(
        CHANNEL_APPLICATION_ALERTS,
        "Application Pipeline & Agent Status",
        NotificationManager.IMPORTANCE_DEFAULT
      ).apply {
        description = "Status updates for submitted applications, recruiter interactions, and autonomous pipeline runs"
        enableLights(true)
        lightColor = 0xFF00E676.toInt() // TitanEmerald
        enableVibration(false)
        setShowBadge(true)
      }

      // 4. Sovereign Operations & Dark Store Channel
      val sovereignChannel = NotificationChannel(
        CHANNEL_SOVEREIGN_OPERATIONS,
        "Sovereign Operations & Dark Store SLAs",
        NotificationManager.IMPORTANCE_HIGH
      ).apply {
        description = "High-priority alerts for 07:00 AM dark store health checks, SLA breach warnings, and C-Suite War Room briefings"
        enableLights(true)
        lightColor = 0xFFFFD700.toInt() // Gold
        enableVibration(true)
        setShowBadge(true)
      }

      notificationManager.createNotificationChannels(listOf(jobChannel, intelChannel, appChannel, sovereignChannel))
      Log.d("TitanNotificationManager", "Notification channels registered successfully")
    }
  }

  fun updatePreferences(newPrefs: NotificationPreferences) {
    _preferences.value = newPrefs
  }

  fun hasNotificationPermission(): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      ContextCompat.checkSelfPermission(
        appContext,
        Manifest.permission.POST_NOTIFICATIONS
      ) == PackageManager.PERMISSION_GRANTED
    } else {
      true
    }
  }

  /**
   * Dispatches a high-priority system notification and records it in the in-app notification center
   * when a high-match job is found.
   */
  fun notifyHighMatchJob(
    job: Job,
    overrideThresholdCheck: Boolean = false
  ) {
    val prefs = _preferences.value
    if (!prefs.notificationsEnabled) return
    if (!overrideThresholdCheck && job.adiFitScore < prefs.minFitScoreThreshold) return

    // Prevent duplicate notification for identical job
    if (!overrideThresholdCheck && notifiedJobIds.contains(job.id)) return
    notifiedJobIds.add(job.id)

    val notifId = (job.id.hashCode() and 0x7FFFFFFF)
    val timeFormatted = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())

    val alertItem = NotificationAlertItem(
      id = "notif_job_${job.id}_${System.currentTimeMillis()}",
      type = NotificationType.HIGH_MATCH_JOB,
      title = "🔥 ${job.adiFitScore}% Match: ${job.title}",
      message = "${job.companyName} • ${job.salaryRange} • ${job.location}",
      detailedSummary = "${job.whyItFits.ifBlank { "Matches your core strategy, BBA International Business, and data analytics capabilities." }}\n\nRecommended Action: ${job.recommendedAction}",
      timestamp = "Just now",
      targetScreen = "JOBS",
      targetItemId = job.id,
      targetItemName = job.companyName,
      matchScore = job.adiFitScore,
      priority = if (job.adiFitScore >= 90) NotificationPriority.CRITICAL else NotificationPriority.HIGH,
      isRead = false
    )

    // Add to in-app alerts
    addAlert(alertItem)

    // Dispatch System Tray Notification if permission is present
    if (hasNotificationPermission()) {
      try {
        // Intent to launch MainActivity and route to Jobs screen
        val tapIntent = Intent(appContext, MainActivity::class.java).apply {
          flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
          putExtra(EXTRA_TARGET_SCREEN, "JOBS")
          putExtra(EXTRA_ITEM_ID, job.id)
          putExtra(EXTRA_ITEM_NAME, job.companyName)
        }
        val tapPendingIntent = PendingIntent.getActivity(
          appContext,
          notifId,
          tapIntent,
          PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Action Intent: View in Applications Pipeline
        val applyIntent = Intent(appContext, MainActivity::class.java).apply {
          flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
          putExtra(EXTRA_TARGET_SCREEN, "APPLICATIONS")
          putExtra(EXTRA_ITEM_ID, job.id)
          putExtra(EXTRA_ITEM_NAME, job.companyName)
        }
        val applyPendingIntent = PendingIntent.getActivity(
          appContext,
          notifId + 10000,
          applyIntent,
          PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val bigTextStyle = NotificationCompat.BigTextStyle()
          .setBigContentTitle("🔥 High-Fit Opportunity: ${job.title} (${job.adiFitScore}% Match)")
          .bigText(
            "🏢 ${job.companyName} • 📍 ${job.location}\n" +
            "💰 Compensation: ${job.salaryRange}\n" +
            "⚡ Match Insights: ${job.whyItFits.ifBlank { "Direct match with your verified international business and analytics background." }}\n" +
            "🎯 Next Step: ${job.recommendedAction}"
          )
          .setSummaryText("Career Opportunity Alert • $timeFormatted")

        val builder = NotificationCompat.Builder(appContext, CHANNEL_HIGH_MATCH_JOBS)
          .setSmallIcon(R.drawable.ic_notification_job)
          .setContentTitle("🔥 High Match: ${job.title} (${job.adiFitScore}%)")
          .setContentText("${job.companyName} • ${job.salaryRange} • ${job.location}")
          .setStyle(bigTextStyle)
          .setPriority(NotificationCompat.PRIORITY_HIGH)
          .setCategory(NotificationCompat.CATEGORY_RECOMMENDATION)
          .setColor(0xFF00E5FF.toInt()) // TitanCyan
          .setAutoCancel(true)
          .setContentIntent(tapPendingIntent)
          .addAction(R.drawable.ic_notification_job, "Open in Jobs", tapPendingIntent)
          .addAction(R.drawable.ic_notification_job, "Prepare Pitch", applyPendingIntent)

        if (prefs.soundAndVibration) {
          builder.setVibrate(longArrayOf(0, 250, 150, 250))
        }

        NotificationManagerCompat.from(appContext).notify(notifId, builder.build())
        Log.d("TitanNotificationManager", "High match job notification dispatched for ${job.title} at ${job.companyName}")
      } catch (e: Exception) {
        Log.e("TitanNotificationManager", "Failed to post job notification: ${e.message}")
      }
    }
  }

  /**
   * Dispatches a high-priority system digest notification when multiple new roles matching criteria
   * are discovered in a background scan by Gemini.
   */
  fun notifyRolesDiscoveredBatch(
    newJobs: List<Job>,
    criteriaSummary: String
  ) {
    val prefs = _preferences.value
    if (!prefs.notificationsEnabled || newJobs.isEmpty()) return

    val notifId = 888801
    val timeFormatted = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())
    val topRole = newJobs.maxByOrNull { it.adiFitScore } ?: newJobs.first()

    val alertItem = NotificationAlertItem(
      id = "notif_batch_${System.currentTimeMillis()}",
      type = NotificationType.HIGH_MATCH_JOB,
      title = "🎯 ${newJobs.size} New Roles Found Matching Criteria",
      message = "Top match: ${topRole.title} at ${topRole.companyName} (${topRole.adiFitScore}% fit)",
      detailedSummary = "Gemini background radar discovered ${newJobs.size} new matching roles across ${newJobs.map { it.companyName }.distinct().joinToString(", ")} based on your criteria ($criteriaSummary).",
      timestamp = "Just now",
      targetScreen = "JOBS",
      targetItemId = topRole.id,
      targetItemName = topRole.companyName,
      matchScore = topRole.adiFitScore,
      priority = NotificationPriority.HIGH,
      isRead = false
    )
    addAlert(alertItem)

    if (hasNotificationPermission()) {
      try {
        val tapIntent = Intent(appContext, MainActivity::class.java).apply {
          flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
          putExtra(EXTRA_TARGET_SCREEN, "JOBS")
        }
        val tapPendingIntent = PendingIntent.getActivity(
          appContext,
          notifId,
          tapIntent,
          PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val inboxStyle = NotificationCompat.InboxStyle()
          .setBigContentTitle("🎯 ${newJobs.size} New Roles Discovered via Gemini")
          .setSummaryText("Gemini Background Radar • $timeFormatted")

        for (job in newJobs.take(5)) {
          inboxStyle.addLine("${job.adiFitScore}% • ${job.title} at ${job.companyName} (${job.salaryRange})")
        }

        val builder = NotificationCompat.Builder(appContext, CHANNEL_HIGH_MATCH_JOBS)
          .setSmallIcon(R.drawable.ic_notification_job)
          .setContentTitle("🎯 ${newJobs.size} New Roles Discovered via Gemini")
          .setContentText("Top: ${topRole.title} at ${topRole.companyName} (${topRole.adiFitScore}%)")
          .setStyle(inboxStyle)
          .setPriority(NotificationCompat.PRIORITY_HIGH)
          .setCategory(NotificationCompat.CATEGORY_RECOMMENDATION)
          .setColor(0xFF00E5FF.toInt())
          .setAutoCancel(true)
          .setContentIntent(tapPendingIntent)
          .addAction(R.drawable.ic_notification_job, "Review Roles", tapPendingIntent)

        if (prefs.soundAndVibration) {
          builder.setVibrate(longArrayOf(0, 250, 150, 250))
        }

        NotificationManagerCompat.from(appContext).notify(notifId, builder.build())
        Log.d("TitanNotificationManager", "Batch roles discovered notification dispatched for ${newJobs.size} jobs")
      } catch (e: Exception) {
        Log.e("TitanNotificationManager", "Failed to post batch notification: ${e.message}")
      }
    }
  }

  /**
   * Dispatches a high-priority system notification and records it when critical breaking news
   * or strategic intel is identified for a followed company.
   */
  fun notifyCriticalCompanyNews(
    company: Company,
    headline: String,
    summary: String,
    impactScore: Int = 92,
    strategicAngle: String = "",
    overrideFollowCheck: Boolean = false
  ) {
    val prefs = _preferences.value
    if (!prefs.notificationsEnabled || !prefs.notifyOnCriticalNews) return
    if (!overrideFollowCheck && prefs.notifyFollowedCompaniesOnly && !company.isWatched) return

    val dedupeKey = "${company.id}_${headline.hashCode()}"
    if (!overrideFollowCheck && notifiedCompanyNewsKeys.contains(dedupeKey)) return
    notifiedCompanyNewsKeys.add(dedupeKey)

    val notifId = (dedupeKey.hashCode() and 0x7FFFFFFF)
    val timeFormatted = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())

    val alertItem = NotificationAlertItem(
      id = "notif_intel_${company.id}_${System.currentTimeMillis()}",
      type = NotificationType.COMPANY_INTEL,
      title = "🚨 Breaking Intel: ${company.name} (Impact $impactScore/100)",
      message = headline,
      detailedSummary = "$summary\n\nStrategic Interview Angle: ${strategicAngle.ifBlank { "Leverage this growth initiative during upcoming executive outreach and interview rounds." }}",
      timestamp = "Just now",
      targetScreen = "COMPANIES",
      targetItemId = company.id,
      targetItemName = company.name,
      impactScore = impactScore,
      priority = NotificationPriority.CRITICAL,
      isRead = false
    )

    // Add to in-app alerts
    addAlert(alertItem)

    // Dispatch System Tray Notification
    if (hasNotificationPermission()) {
      try {
        val tapIntent = Intent(appContext, MainActivity::class.java).apply {
          flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
          putExtra(EXTRA_TARGET_SCREEN, "COMPANIES")
          putExtra(EXTRA_ITEM_ID, company.id)
          putExtra(EXTRA_ITEM_NAME, company.name)
        }
        val tapPendingIntent = PendingIntent.getActivity(
          appContext,
          notifId,
          tapIntent,
          PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val bigTextStyle = NotificationCompat.BigTextStyle()
          .setBigContentTitle("🚨 Followed Company Intel: ${company.name}")
          .bigText(
            "📰 $headline\n\n" +
            "💡 Impact Level: $impactScore/100 (Tier ${company.tier})\n" +
            "📊 Strategic Context: $summary\n" +
            (if (strategicAngle.isNotBlank()) "🎯 Strategy Angle: $strategicAngle" else "")
          )
          .setSummaryText("Watched Company Radar • $timeFormatted")

        val builder = NotificationCompat.Builder(appContext, CHANNEL_COMPANY_INTEL)
          .setSmallIcon(R.drawable.ic_notification_intel)
          .setContentTitle("🚨 Breaking Intel: ${company.name}")
          .setContentText(headline)
          .setStyle(bigTextStyle)
          .setPriority(NotificationCompat.PRIORITY_HIGH)
          .setCategory(NotificationCompat.CATEGORY_EVENT)
          .setColor(0xFFFFB300.toInt()) // TitanGold
          .setAutoCancel(true)
          .setContentIntent(tapPendingIntent)
          .addAction(R.drawable.ic_notification_intel, "Open Dossier", tapPendingIntent)

        if (prefs.soundAndVibration) {
          builder.setVibrate(longArrayOf(0, 300, 100, 300))
        }

        NotificationManagerCompat.from(appContext).notify(notifId, builder.build())
        Log.d("TitanNotificationManager", "Critical company news notification dispatched for ${company.name}")
      } catch (e: Exception) {
        Log.e("TitanNotificationManager", "Failed to post company intel notification: ${e.message}")
      }
    }
  }

  /**
   * Dispatches application status alerts.
   */
  fun notifyApplicationStatus(
    companyName: String,
    roleTitle: String,
    newStatus: String,
    notes: String = ""
  ) {
    val prefs = _preferences.value
    if (!prefs.notificationsEnabled || !prefs.notifyOnApplicationStatus) return

    val notifId = ((companyName + roleTitle + newStatus).hashCode() and 0x7FFFFFFF)

    val alertItem = NotificationAlertItem(
      id = "notif_app_${System.currentTimeMillis()}",
      type = NotificationType.APPLICATION_STATUS,
      title = "📬 Application Update: $companyName",
      message = "$roleTitle • Status moved to $newStatus",
      detailedSummary = notes.ifBlank { "Your application for $roleTitle at $companyName has progressed to $newStatus." },
      timestamp = "Just now",
      targetScreen = "APPLICATIONS",
      targetItemName = companyName,
      priority = NotificationPriority.NORMAL,
      isRead = false
    )

    addAlert(alertItem)

    if (hasNotificationPermission()) {
      try {
        val tapIntent = Intent(appContext, MainActivity::class.java).apply {
          flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
          putExtra(EXTRA_TARGET_SCREEN, "APPLICATIONS")
          putExtra(EXTRA_ITEM_NAME, companyName)
        }
        val tapPendingIntent = PendingIntent.getActivity(
          appContext,
          notifId,
          tapIntent,
          PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(appContext, CHANNEL_APPLICATION_ALERTS)
          .setSmallIcon(R.drawable.ic_notification_job)
          .setContentTitle("📬 Application: $companyName ($newStatus)")
          .setContentText("$roleTitle • Status changed to $newStatus")
          .setPriority(NotificationCompat.PRIORITY_DEFAULT)
          .setColor(0xFF00E676.toInt()) // TitanEmerald
          .setAutoCancel(true)
          .setContentIntent(tapPendingIntent)

        NotificationManagerCompat.from(appContext).notify(notifId, builder.build())
      } catch (e: Exception) {
        Log.e("TitanNotificationManager", "Failed to post application alert: ${e.message}")
      }
    }
  }

  fun notifySovereignAlert(
    title: String,
    message: String,
    detailedSummary: String = "",
    targetScreen: String = "SOVEREIGN_SUPER_APP"
  ) {
    val notifId = System.currentTimeMillis().toInt()
    val alertItem = NotificationAlertItem(
      id = "sovereign_$notifId",
      type = NotificationType.SYSTEM_ALERT,
      title = title,
      message = message,
      detailedSummary = detailedSummary.ifBlank { message },
      timestamp = "Just now",
      targetScreen = targetScreen,
      targetItemId = "sovereign",
      targetItemName = "OMEGA-TITAN Sovereign",
      priority = NotificationPriority.CRITICAL,
      isRead = false
    )
    addAlert(alertItem)

    if (hasNotificationPermission()) {
      try {
        val tapIntent = Intent(appContext, MainActivity::class.java).apply {
          flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
          putExtra(EXTRA_TARGET_SCREEN, targetScreen)
        }
        val tapPendingIntent = PendingIntent.getActivity(
          appContext,
          notifId,
          tapIntent,
          PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val warRoomIntent = Intent(appContext, MainActivity::class.java).apply {
          flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
          putExtra(EXTRA_TARGET_SCREEN, "SOVEREIGN_SUPER_APP")
        }
        val warRoomPending = PendingIntent.getActivity(
          appContext,
          notifId + 1,
          warRoomIntent,
          PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(appContext, CHANNEL_SOVEREIGN_OPERATIONS)
          .setSmallIcon(R.drawable.ic_launcher_foreground)
          .setContentTitle(title)
          .setContentText(message)
          .setStyle(NotificationCompat.BigTextStyle().bigText(if (detailedSummary.isNotBlank()) "$message\n\n$detailedSummary" else message))
          .setPriority(NotificationCompat.PRIORITY_HIGH)
          .setColor(0xFFFFD700.toInt()) // TitanGold
          .setAutoCancel(true)
          .setContentIntent(tapPendingIntent)
          .addAction(R.drawable.ic_launcher_foreground, "WAR ROOM", warRoomPending)

        NotificationManagerCompat.from(appContext).notify(notifId, builder.build())
        Log.d("TitanNotificationManager", "Sovereign operational alert dispatched: $title")
      } catch (e: Exception) {
        Log.e("TitanNotificationManager", "Failed to post sovereign alert: ${e.message}")
      }
    }
  }

  fun notifySystemAlert(
    title: String,
    message: String,
    detailedSummary: String = "",
    targetScreen: String = "PROFILE"
  ) {
    val alertItem = NotificationAlertItem(
      type = NotificationType.SYSTEM_ALERT,
      title = title,
      message = message,
      detailedSummary = detailedSummary.ifBlank { message },
      timestamp = "Just now",
      targetScreen = targetScreen,
      priority = NotificationPriority.NORMAL,
      isRead = false
    )
    addAlert(alertItem)
  }

  fun addAlertItem(item: NotificationAlertItem) {
    addAlert(item)
  }

  private fun addAlert(item: NotificationAlertItem) {
    val updated = listOf(item) + _alerts.value.take(49) // Keep last 50 alerts
    _alerts.value = updated
    updateUnreadCount()
  }

  fun markAlertAsRead(alertId: String) {
    _alerts.value = _alerts.value.map {
      if (it.id == alertId) it.copy(isRead = true) else it
    }
    updateUnreadCount()
  }

  fun markAllAlertsAsRead() {
    _alerts.value = _alerts.value.map { it.copy(isRead = true) }
    updateUnreadCount()
  }

  fun clearAlert(alertId: String) {
    _alerts.value = _alerts.value.filter { it.id != alertId }
    updateUnreadCount()
  }

  fun clearAllAlerts() {
    _alerts.value = emptyList()
    updateUnreadCount()
    notificationManager.cancelAll()
  }

  private fun updateUnreadCount() {
    _unreadCount.value = _alerts.value.count { !it.isRead }
  }
}
