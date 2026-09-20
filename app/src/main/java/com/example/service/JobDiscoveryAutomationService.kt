package com.example.service

import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/**
 * Background Android Service that coordinates automated pulling of job postings
 * from target companies based on the user's career goals and persists them to Room.
 */
class JobDiscoveryAutomationService : Service() {

  companion object {
    private const val TAG = "JobDiscoveryService"
    const val CHANNEL_ID = "titan_job_discovery_service"
    const val NOTIFICATION_ID = 2001

    const val ACTION_TRIGGER_PULL = "com.example.service.ACTION_TRIGGER_PULL"
    const val ACTION_START_PERIODIC = "com.example.service.ACTION_START_PERIODIC"
    const val ACTION_STOP_PERIODIC = "com.example.service.ACTION_STOP_PERIODIC"
    const val EXTRA_TRIGGER_SOURCE = "extra_trigger_source"

    fun triggerPull(context: Context, source: String = "MANUAL_INTENT") {
      val intent = Intent(context, JobDiscoveryAutomationService::class.java).apply {
        action = ACTION_TRIGGER_PULL
        putExtra(EXTRA_TRIGGER_SOURCE, source)
      }
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        context.startForegroundService(intent)
      } else {
        context.startService(intent)
      }
    }

    fun startPeriodicRadar(context: Context) {
      val intent = Intent(context, JobDiscoveryAutomationService::class.java).apply {
        action = ACTION_START_PERIODIC
      }
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        context.startForegroundService(intent)
      } else {
        context.startService(intent)
      }
    }

    fun stopPeriodicRadar(context: Context) {
      val intent = Intent(context, JobDiscoveryAutomationService::class.java).apply {
        action = ACTION_STOP_PERIODIC
      }
      context.startService(intent)
      cancelAlarm(context)
    }

    fun scheduleNextAlarm(context: Context, intervalMinutes: Int) {
      val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
      val intent = Intent(context, JobDiscoveryAutomationService::class.java).apply {
        action = ACTION_TRIGGER_PULL
        putExtra(EXTRA_TRIGGER_SOURCE, "ALARM_PERIODIC_RADAR")
      }
      val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        PendingIntent.getForegroundService(
          context,
          2002,
          intent,
          PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
      } else {
        PendingIntent.getService(
          context,
          2002,
          intent,
          PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
      }
      val triggerAtMillis = System.currentTimeMillis() + (intervalMinutes * 60 * 1000L)
      try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
          alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
        } else {
          alarmManager.set(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
        }
        Log.d(TAG, "Scheduled next background radar alarm for $intervalMinutes minutes from now")
      } catch (e: Exception) {
        Log.w(TAG, "Alarm scheduling fallback: ${e.message}")
        alarmManager.set(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
      }
    }

    fun cancelAlarm(context: Context) {
      val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
      val intent = Intent(context, JobDiscoveryAutomationService::class.java).apply {
        action = ACTION_TRIGGER_PULL
      }
      val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        PendingIntent.getForegroundService(
          context,
          2002,
          intent,
          PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
      } else {
        PendingIntent.getService(
          context,
          2002,
          intent,
          PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
      }
      alarmManager.cancel(pendingIntent)
    }
  }

  private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
  private lateinit var automationManager: JobDiscoveryAutomationManager

  override fun onCreate() {
    super.onCreate()
    Log.d(TAG, "JobDiscoveryAutomationService created")
    automationManager = JobDiscoveryAutomationManager.getInstance(applicationContext)
    createNotificationChannel()
  }

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    val action = intent?.action ?: ACTION_TRIGGER_PULL
    val source = intent?.getStringExtra(EXTRA_TRIGGER_SOURCE) ?: "BACKGROUND_AUTOMATION_SERVICE"

    Log.d(TAG, "onStartCommand received action: $action, source: $source")

    // Provide a foreground notification to ensure background survival
    startForeground(NOTIFICATION_ID, buildForegroundNotification("Syncing target company career portals..."))

    serviceScope.launch {
      when (action) {
        ACTION_TRIGGER_PULL -> {
          val result = automationManager.triggerManualPull(source = source)
          updateNotification(
            if (result.success) {
              "Scan complete: Discovered ${result.jobsPulledCount} roles, stored ${result.newJobsStoredCount} new opportunities."
            } else {
              "Background scan completed with notice: ${result.message}"
            }
          )
          // Re-arm next alarm if periodic is enabled
          if (automationManager.isPeriodicEnabled.value) {
            scheduleNextAlarm(applicationContext, automationManager.periodicIntervalMinutes.value)
          }
        }

        ACTION_START_PERIODIC -> {
          automationManager.setPeriodicEnabled(true)
          scheduleNextAlarm(applicationContext, automationManager.periodicIntervalMinutes.value)
          updateNotification("Periodic radar active. Monitoring target companies for high-match roles.")
        }

        ACTION_STOP_PERIODIC -> {
          automationManager.setPeriodicEnabled(false)
          cancelAlarm(applicationContext)
          stopForeground(STOP_FOREGROUND_REMOVE)
          stopSelf()
        }
      }
    }

    return START_STICKY
  }

  override fun onBind(intent: Intent?): IBinder? = null

  override fun onDestroy() {
    super.onDestroy()
    serviceScope.cancel()
    Log.d(TAG, "JobDiscoveryAutomationService destroyed")
  }

  private fun createNotificationChannel() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val channel = NotificationChannel(
        CHANNEL_ID,
        "Job Discovery Background Service",
        NotificationManager.IMPORTANCE_LOW
      ).apply {
        description = "Monitors target company career feeds based on user career goals"
        setShowBadge(false)
      }
      val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
      notificationManager.createNotificationChannel(channel)
    }
  }

  private fun buildForegroundNotification(contentText: String): Notification {
    val pendingIntent = PendingIntent.getActivity(
      this,
      0,
      Intent(this, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
      },
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    return NotificationCompat.Builder(this, CHANNEL_ID)
      .setSmallIcon(R.mipmap.ic_launcher)
      .setContentTitle("Titan Career Intelligence Radar")
      .setContentText(contentText)
      .setPriority(NotificationCompat.PRIORITY_LOW)
      .setOngoing(true)
      .setContentIntent(pendingIntent)
      .build()
  }

  private fun updateNotification(newText: String) {
    val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    manager.notify(NOTIFICATION_ID, buildForegroundNotification(newText))
  }
}
