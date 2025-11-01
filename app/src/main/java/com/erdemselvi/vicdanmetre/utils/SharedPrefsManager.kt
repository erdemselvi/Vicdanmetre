package com.erdemselvi.vicdanmetre.utils

import android.content.Context
import android.content.SharedPreferences

class SharedPrefsManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("vicdanim_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_USER_ID = "user_id"
        private const val KEY_FIRST_LAUNCH = "first_launch"
        private const val KEY_LAST_DAILY_QUEST = "last_daily_quest"
        private const val KEY_NOTIFICATION_ENABLED = "notification_enabled"
    }

    var userId: String?
        get() = prefs.getString(KEY_USER_ID, null)
        set(value) = prefs.edit().putString(KEY_USER_ID, value).apply()

    var isFirstLaunch: Boolean
        get() = prefs.getBoolean(KEY_FIRST_LAUNCH, true)
        set(value) = prefs.edit().putBoolean(KEY_FIRST_LAUNCH, value).apply()

    var lastDailyQuestDate: Long
        get() = prefs.getLong(KEY_LAST_DAILY_QUEST, 0)
        set(value) = prefs.edit().putLong(KEY_LAST_DAILY_QUEST, value).apply()

    var notificationEnabled: Boolean
        get() = prefs.getBoolean(KEY_NOTIFICATION_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_NOTIFICATION_ENABLED, value).apply()
}
