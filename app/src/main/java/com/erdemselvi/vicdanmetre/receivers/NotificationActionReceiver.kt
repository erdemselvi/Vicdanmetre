package com.erdemselvi.vicdanmetre.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

/**
 * Bildirim aksiyonları için receiver
 */
class NotificationActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            "ACCEPT_FRIEND_REQUEST" -> {
                val friendId = intent.getStringExtra("friend_id")
                // Arkadaşlık isteğini kabul et
                Toast.makeText(context, "Arkadaşlık isteği kabul edildi", Toast.LENGTH_SHORT).show()
            }
            "DECLINE_FRIEND_REQUEST" -> {
                val friendId = intent.getStringExtra("friend_id")
                // Arkadaşlık isteğini reddet
                Toast.makeText(context, "Arkadaşlık isteği reddedildi", Toast.LENGTH_SHORT).show()
            }
        }
    }
}