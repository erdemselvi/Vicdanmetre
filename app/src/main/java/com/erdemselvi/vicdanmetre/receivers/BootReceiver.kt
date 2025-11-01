package com.erdemselvi.vicdanmetre.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.erdemselvi.vicdanmetre.workers.DailyReminderWorker
import com.erdemselvi.vicdanmetre.workers.StreakCheckerWorker

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // Cihaz yeniden başladığında worker'ları yeniden planla
            DailyReminderWorker.schedule(context)
            StreakCheckerWorker.schedule(context)
        }
    }
}