package com.chartmann1590.verselight.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("verselight_preferences")

class PreferenceRepository(private val context: Context) {
    private val reminderEnabled = booleanPreferencesKey("reminder_enabled")
    private val reminderHour = intPreferencesKey("reminder_hour")
    val reminder = context.dataStore.data.map { (it[reminderEnabled] ?: false) to (it[reminderHour] ?: 8) }

    suspend fun setReminder(enabled: Boolean, hour: Int) {
        context.dataStore.edit {
            it[reminderEnabled] = enabled
            it[reminderHour] = hour.coerceIn(0, 23)
        }
    }
}

