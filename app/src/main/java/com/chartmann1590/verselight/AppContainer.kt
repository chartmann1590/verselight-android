package com.chartmann1590.verselight

import android.content.Context
import com.chartmann1590.verselight.data.AuthRepository
import com.chartmann1590.verselight.data.CommunityRepository
import com.chartmann1590.verselight.data.DailyVerseRepository
import com.chartmann1590.verselight.data.PreferenceRepository
import com.chartmann1590.verselight.moderation.OnDeviceSafetyClassifier

class AppContainer(context: Context) {
    val verses = DailyVerseRepository()
    val auth = AuthRepository(context)
    val community = CommunityRepository()
    val preferences = PreferenceRepository(context)
    val safety = OnDeviceSafetyClassifier(context)
}

