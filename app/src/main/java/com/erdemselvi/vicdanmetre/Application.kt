package com.erdemselvi.vicdanmetre

import android.app.Application
import com.erdemselvi.vicdanmetre.workers.DailyReminderWorker
import com.erdemselvi.vicdanmetre.workers.StreakCheckerWorker

class Application : Application() {

    override fun onCreate() {
        super.onCreate()

        // Schedule background workers
        DailyReminderWorker.schedule(this)
        StreakCheckerWorker.schedule(this)
    }
}