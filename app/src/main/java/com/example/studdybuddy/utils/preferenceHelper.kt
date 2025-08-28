package com.example.studdybuddy.utils


import android.content.Context
import android.content.SharedPreferences

object PreferencesHelper {
    private const val PREF_NAME = "studdybuddy_prefs"
    private const val KEY_ONBOARDING = "onboarding_completed"

    fun setOnboardingCompleted(context: Context, completed: Boolean) {
        val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_ONBOARDING, completed).apply()
    }

    fun isOnboardingCompleted(context: Context): Boolean {
        val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_ONBOARDING, false)
    }
}
