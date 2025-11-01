package com.erdemselvi.vicdanmetre.workers

import android.Manifest
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.work.*
import com.erdemselvi.vicdanmetre.database.ConscienceDatabase
import com.erdemselvi.vicdanmetre.utils.VicdanimNotificationManager
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit

class StreakCheckerWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun doWork(): Result {
        return try {
            val database = ConscienceDatabase.getDatabase(applicationContext)
            val userDao = database.userDao()

            // Kullanıcıyı al (gerçek uygulamada userId SharedPreferences'ten gelecek)
            val userId = "current_user_id"
            val user = userDao.getUser(userId) ?: return Result.success()

            val now = LocalDateTime.now()
            val hoursSinceLastLogin = ChronoUnit.HOURS.between(user.lastLoginAt, now)

            // 20 saatten fazla giriş yapılmamışsa uyarı gönder
            if (hoursSinceLastLogin >= 20 && user.currentStreak > 0) {
                val notificationManager = VicdanimNotificationManager(applicationContext)
                notificationManager.sendStreakWarning(user.currentStreak)
            }

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    companion object {
        const val WORK_NAME = "streak_checker_work"

        fun schedule(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                .build()

            val streakCheckRequest = PeriodicWorkRequestBuilder<StreakCheckerWorker>(
                repeatInterval = 6,
                repeatIntervalTimeUnit = TimeUnit.HOURS
            )
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(
                    WORK_NAME,
                    ExistingPeriodicWorkPolicy.KEEP,
                    streakCheckRequest
                )
        }
    }
}
