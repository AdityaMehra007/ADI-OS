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
import com.example.data.model.JobPlatform
import com.example.data.model.PlatformApplicationStatus
import com.example.data.model.PlatformHealthStatus
import com.example.data.model.PlatformSyncDaemonConfig
import com.example.data.model.PlatformSyncResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Android Background Service that runs periodic and on-demand synchronization of
 * saved job listings and tracks recruitment application status progression across external platforms.
 */
class PlatformJobSyncBackgroundService : Service() {

  companion object {
    private const val TAG = "PlatformJobSyncService"
    const val CHANNEL_ID = "titan_platform_sync_channel"
    const val NOTIFICATION_ID = 4001

    const val ACTION_TRIGGER_SYNC = "com.example.service.ACTION_TRIGGER_PLATFORM_SYNC"
    const val ACTION_START_PERIODIC = "com.example.service.ACTION_START_PLATFORM_PERIODIC"
    const val ACTION_STOP_PERIODIC = "com.example.service.ACTION_STOP_PLATFORM_PERIODIC"
    const val EXTRA_TRIGGER_SOURCE = "extra_trigger_source"

    fun triggerSync(context: Context, source: String = "MANUAL_INTENT") {
      val intent = Intent(context, PlatformJobSyncBackgroundService::class.java).apply {
        action = ACTION_TRIGGER_SYNC
        putExtra(EXTRA_TRIGGER_SOURCE, source)
      }
      try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
          context.startForegroundService(intent)
        } else {
          context.startService(intent)
        }
      } catch (e: Exception) {
        Log.w(TAG, "Failed to startForegroundService: ${e.message}, executing engine directly in background")
        CoroutineScope(Dispatchers.IO).launch {
          JobPlatformSyncEngine.getInstance(context).executeSyncCycle(source)
        }
      }
    }

    fun startPeriodicSync(context: Context, intervalMinutes: Int = 30) {
      val engine = JobPlatformSyncEngine.getInstance(context)
      engine.updateDaemonConfig(
        engine.daemonConfig.value.copy(
          isPeriodicEnabled = true,
          intervalMinutes = intervalMinutes
        )
      )

      val intent = Intent(context, PlatformJobSyncBackgroundService::class.java).apply {
        action = ACTION_START_PERIODIC
      }
      try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
          context.startForegroundService(intent)
        } else {
          context.startService(intent)
        }
      } catch (e: Exception) {
        scheduleNextAlarm(context, intervalMinutes)
      }
    }

    fun stopPeriodicSync(context: Context) {
      val engine = JobPlatformSyncEngine.getInstance(context)
      engine.updateDaemonConfig(
        engine.daemonConfig.value.copy(
          isPeriodicEnabled = false
        )
      )

      val intent = Intent(context, PlatformJobSyncBackgroundService::class.java).apply {
        action = ACTION_STOP_PERIODIC
      }
      context.startService(intent)
      cancelAlarm(context)
    }

    fun scheduleNextAlarm(context: Context, intervalMinutes: Int) {
      val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
      val intent = Intent(context, PlatformJobSyncBackgroundService::class.java).apply {
        action = ACTION_TRIGGER_SYNC
        putExtra(EXTRA_TRIGGER_SOURCE, "SCHEDULED_PERIODIC_ALARM")
      }

      val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        PendingIntent.getForegroundService(
          context,
          4002,
          intent,
          PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
      } else {
        PendingIntent.getService(
          context,
          4002,
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
        Log.i(TAG, "Successfully scheduled next platform sync alarm in $intervalMinutes minutes.")
      } catch (e: Exception) {
        Log.w(TAG, "Alarm scheduling fallback: ${e.message}")
        alarmManager.set(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
      }
    }

    fun cancelAlarm(context: Context) {
      val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
      val intent = Intent(context, PlatformJobSyncBackgroundService::class.java).apply {
        action = ACTION_TRIGGER_SYNC
      }
      val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        PendingIntent.getForegroundService(
          context,
          4002,
          intent,
          PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
      } else {
        PendingIntent.getService(
          context,
          4002,
          intent,
          PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
      }
      alarmManager.cancel(pendingIntent)
    }

    // Engine StateFlow delegators for convenient UI access
    fun getEngine(context: Context): JobPlatformSyncEngine = JobPlatformSyncEngine.getInstance(context)
  }

  private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
  private lateinit var syncEngine: JobPlatformSyncEngine

  override fun onCreate() {
    super.onCreate()
    Log.d(TAG, "PlatformJobSyncBackgroundService created")
    syncEngine = JobPlatformSyncEngine.getInstance(applicationContext)
    createNotificationChannel()
  }

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    val action = intent?.action ?: ACTION_TRIGGER_SYNC
    val source = intent?.getStringExtra(EXTRA_TRIGGER_SOURCE) ?: "BACKGROUND_ALARM"

    Log.d(TAG, "onStartCommand received action: $action, source: $source")

    // Display foreground notification immediately to satisfy Android 8+ foreground requirements
    startForeground(
      NOTIFICATION_ID,
      buildForegroundNotification("Adi OS Sync Engine: Synchronizing saved job listings & candidate tracking across platforms...")
    )

    serviceScope.launch {
      when (action) {
        ACTION_TRIGGER_SYNC -> {
          val result = syncEngine.executeSyncCycle(source)

          val notifText = if (result.isSuccess) {
            "Synced ${result.savedJobsPolledCount} saved jobs (${result.newSavedJobsAdded} new). Monitored ${result.applicationStatusesCheckedCount} applications (${result.statusTransitionsCount} progressed)."
          } else {
            result.summaryMessage
          }
          updateNotification("Platform Sync: $notifText")

          // If periodic mode is enabled, schedule next alarm
          val config = syncEngine.daemonConfig.value
          if (config.isPeriodicEnabled) {
            scheduleNextAlarm(applicationContext, config.intervalMinutes)
          }

          delay(3000)
          stopForeground(STOP_FOREGROUND_DETACH)
          if (!config.isPeriodicEnabled) {
            stopSelf()
          }
        }

        ACTION_START_PERIODIC -> {
          val interval = syncEngine.daemonConfig.value.intervalMinutes
          scheduleNextAlarm(applicationContext, interval)
          updateNotification("Periodic Platform Sync Active (Every ${interval}m). Background tracking live.")
          delay(2500)
          stopForeground(STOP_FOREGROUND_DETACH)
        }

        ACTION_STOP_PERIODIC -> {
          cancelAlarm(applicationContext)
          updateNotification("Periodic Platform Sync Deactivated.")
          delay(1500)
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
    Log.d(TAG, "PlatformJobSyncBackgroundService destroyed")
  }

  private fun createNotificationChannel() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val channel = NotificationChannel(
        CHANNEL_ID,
        "Adi OS Multi-Platform Job & ATS Sync",
        NotificationManager.IMPORTANCE_LOW
      ).apply {
        description = "Background notifications for periodic synchronization of saved job listings and candidate ATS status progression."
        setShowBadge(false)
      }
      val manager = getSystemService(NotificationManager::class.java)
      manager?.createNotificationChannel(channel)
    }
  }

  private fun buildForegroundNotification(content: String): Notification {
    val openAppIntent = Intent(this, MainActivity::class.java).apply {
      flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
    }
    val pendingIntent = PendingIntent.getActivity(
      this,
      4003,
      openAppIntent,
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    return NotificationCompat.Builder(this, CHANNEL_ID)
      .setSmallIcon(R.mipmap.ic_launcher)
      .setContentTitle("Adi OS Platform Sync Daemon")
      .setContentText(content)
      .setStyle(NotificationCompat.BigTextStyle().bigText(content))
      .setPriority(NotificationCompat.PRIORITY_LOW)
      .setContentIntent(pendingIntent)
      .setOngoing(true)
      .build()
  }

  private fun updateNotification(content: String) {
    try {
      val notification = buildForegroundNotification(content)
      val manager = getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
      manager?.notify(NOTIFICATION_ID, notification)
    } catch (e: Exception) {
      Log.e(TAG, "Failed to update notification: ${e.message}")
    }
  }
}
