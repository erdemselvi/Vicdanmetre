package com.erdemselvi.vicdanmetre.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.erdemselvi.vicdanmetre.R
import com.erdemselvi.vicdanmetre.MainActivity
import com.erdemselvi.vicdanmetre.receivers.NotificationActionReceiver

/**
 * Firebase Cloud Messaging Service
 * Push bildirimleri için Firebase entegrasyonu
 *
 * NOT: Firebase kullanmıyorsanız bu sınıfı AndroidManifest.xml'den kaldırın
 */
class VicdanimMessagingService : FirebaseMessagingService() {

    companion object {
        private const val CHANNEL_ID = "vicdanim_push"
        private const val CHANNEL_NAME = "Push Notifications"
        private const val NOTIFICATION_ID = 2001
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    /**
     * Yeni FCM token oluşturulduğunda çağrılır
     * Token'ı sunucunuza göndermeniz gerekir
     */
    override fun onNewToken(token: String) {
        super.onNewToken(token)

        // Token'ı backend'e gönder
        sendTokenToServer(token)

        // Local storage'a kaydet
        saveTokenToPreferences(token)
    }

    /**
     * Push bildirimi alındığında çağrılır
     */
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        // Bildirim içeriğini al
        val title = message.notification?.title ?: "Vicdanım"
        val body = message.notification?.body ?: ""
        val data = message.data

        // Bildirim tipine göre işlem yap
        when (data["type"]) {
            "new_scenario" -> handleNewScenarioNotification(title, body, data)
            "badge_earned" -> handleBadgeNotification(title, body, data)
            "friend_request" -> handleFriendRequestNotification(title, body, data)
            "leaderboard_update" -> handleLeaderboardNotification(title, body, data)
            else -> showDefaultNotification(title, body)
        }
    }

    private fun handleNewScenarioNotification(title: String, body: String, data: Map<String, String>) {
        val scenarioId = data["scenario_id"]

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("navigate_to", "scenario")
            putExtra("scenario_id", scenarioId)
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .build()

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun handleBadgeNotification(title: String, body: String, data: Map<String, String>) {
        val badgeId = data["badge_id"]
        val badgeIcon = data["badge_icon"] ?: "🏆"

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("navigate_to", "badges")
            putExtra("badge_id", badgeId)
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("$badgeIcon $title")
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(body)
                    .setBigContentTitle("$badgeIcon $title")
            )
            .build()

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID + 1, notification)
    }

    private fun handleFriendRequestNotification(title: String, body: String, data: Map<String, String>) {
        val friendId = data["friend_id"]
        val friendName = data["friend_name"]

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("navigate_to", "friends")
            putExtra("friend_id", friendId)
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Accept/Decline action buttons
        val acceptIntent = Intent(this, NotificationActionReceiver::class.java).apply {
            action = "ACCEPT_FRIEND_REQUEST"
            putExtra("friend_id", friendId)
        }
        val acceptPendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            acceptIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .addAction(R.drawable.ic_launcher_foreground, "Kabul Et", acceptPendingIntent)
            .build()

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID + 2, notification)
    }

    private fun handleLeaderboardNotification(title: String, body: String, data: Map<String, String>) {
        val rank = data["rank"]

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("navigate_to", "leaderboard")
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID + 3, notification)
    }

    private fun showDefaultNotification(title: String, body: String) {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Push bildirimleri için kanal"
                enableVibration(true)
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun sendTokenToServer(token: String) {
        // TODO: Token'ı backend API'nize gönderin
        // Bu sayede kullanıcılara hedefli bildirim gönderebilirsiniz

        // Örnek:
        // RetrofitClient.api.updateFCMToken(userId, token)
    }

    private fun saveTokenToPreferences(token: String) {
        val prefs = getSharedPreferences("vicdanim_prefs", MODE_PRIVATE)
        prefs.edit().putString("fcm_token", token).apply()
    }
}