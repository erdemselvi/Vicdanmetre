package com.erdemselvi.vicdanmetre.workers

import android.Manifest
import android.content.Context
import androidx.annotation.RequiresPermission
import androidx.work.*
import com.erdemselvi.vicdanmetre.utils.VicdanimNotificationManager
import java.util.concurrent.TimeUnit

class DailyReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override suspend fun doWork(): Result {
        return try {
            val notificationManager = VicdanimNotificationManager(applicationContext)
            notificationManager.sendDailyReminder()
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    companion object {
        const val WORK_NAME = "daily_reminder_work"

        fun schedule(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                .build()

            val dailyWorkRequest = PeriodicWorkRequestBuilder<DailyReminderWorker>(
                repeatInterval = 1,
                repeatIntervalTimeUnit = TimeUnit.DAYS,
                flexTimeInterval = 1,
                flexTimeIntervalUnit = TimeUnit.HOURS
            )
                .setConstraints(constraints)
                .setInitialDelay(calculateInitialDelay(), TimeUnit.MILLISECONDS)
                .build()

            WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(
                    WORK_NAME,
                    ExistingPeriodicWorkPolicy.KEEP,
                    dailyWorkRequest
                )
        }

        private fun calculateInitialDelay(): Long {
            val currentTime = System.currentTimeMillis()
            val calendar = java.util.Calendar.getInstance().apply {
                timeInMillis = currentTime
                set(java.util.Calendar.HOUR_OF_DAY, 20) // 20:00'de bildirim
                set(java.util.Calendar.MINUTE, 0)
                set(java.util.Calendar.SECOND, 0)

                if (timeInMillis <= currentTime) {
                    add(java.util.Calendar.DAY_OF_MONTH, 1)
                }
            }

            return calendar.timeInMillis - currentTime
        }
    }
}