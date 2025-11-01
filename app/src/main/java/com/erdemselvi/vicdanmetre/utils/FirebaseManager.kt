package com.erdemselvi.vicdanmetre.utils

import android.content.Context
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await

/**
 * Firebase işlemleri için yardımcı sınıf
 */
class FirebaseManager(private val context: Context) {

    /**
     * FCM token'ını al
     */
    suspend fun getFCMToken(): String? {
        return try {
            FirebaseMessaging.getInstance().token.await()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Belirli bir topic'e abone ol
     * Örnek: "all_users", "premium_users", "turkish_users"
     */
    suspend fun subscribeToTopic(topic: String): Boolean {
        return try {
            FirebaseMessaging.getInstance().subscribeToTopic(topic).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Topic'ten çık
     */
    suspend fun unsubscribeFromTopic(topic: String): Boolean {
        return try {
            FirebaseMessaging.getInstance().unsubscribeFromTopic(topic).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}