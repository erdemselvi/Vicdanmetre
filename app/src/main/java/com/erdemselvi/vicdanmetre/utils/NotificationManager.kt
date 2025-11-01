package com.erdemselvi.vicdanmetre.utils

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.erdemselvi.vicdanmetre.R
import com.erdemselvi.vicdanmetre.MainActivity

class VicdanimNotificationManager(private val context: Context) {

    companion object {
        const val CHANNEL_ID = "vicdanim_daily"
        const val CHANNEL_NAME = "Günlük Hatırlatıcılar"
        const val NOTIFICATION_ID = 1001

        private const val STREAK_CHANNEL_ID = "vicdanim_streak"
        private const val QUEST_CHANNEL_ID = "vicdanim_quest"
        private const val BADGE_CHANNEL_ID = "vicdanim_badge"
    }

    init {
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channels = listOf(
                NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "Günlük vicdan senaryoları için hatırlatıcılar"
                },
                NotificationChannel(
                    STREAK_CHANNEL_ID,
                    "Streak Uyarıları",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Günlük giriş serisini kaybetme uyarıları"
                },
                NotificationChannel(
                    QUEST_CHANNEL_ID,
                    "Görev Bildirimleri",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "Günlük görev tamamlama bildirimleri"
                },
                NotificationChannel(
                    BADGE_CHANNEL_ID,
                    "Rozet Kazanımları",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Yeni rozet kazandığınızda bildirim"
                }
            )

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            channels.forEach { notificationManager.createNotificationChannel(it) }
        }
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun sendDailyReminder() {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Vicdanınla yüzleşme zamanı 🤍")
            .setContentText("Bugün için yeni bir senaryo seni bekliyor!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("Günlük streakini kaybetme! Bugün hangi kararları alacaksın?")
            )
            .build()

        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun sendStreakWarning(currentStreak: Int) {
        val notification = NotificationCompat.Builder(context, STREAK_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("⚠️ Streak Tehlikede!")
            .setContentText("$currentStreak günlük serini kaybetmek üzeresin!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(1002, notification)
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun sendQuestComplete(questTitle: String, reward: Int) {
        val notification = NotificationCompat.Builder(context, QUEST_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("✅ Görev Tamamlandı!")
            .setContentText("$questTitle - $reward Kristal kazandın!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(1003, notification)
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun sendBadgeUnlocked(badgeName: String) {
        val notification = NotificationCompat.Builder(context, BADGE_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("🏆 Yeni Rozet!")
            .setContentText("'$badgeName' rozetini kazandın!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("Tebrikler! Vicdanının gelişiminde önemli bir aşamaya ulaştın.")
            )
            .build()

        NotificationManagerCompat.from(context).notify(1004, notification)
    }
}