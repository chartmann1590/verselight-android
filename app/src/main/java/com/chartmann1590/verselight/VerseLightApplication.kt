package com.chartmann1590.verselight

import android.app.Application
import com.google.firebase.FirebaseApp

class VerseLightApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        container = AppContainer(this)
        ReminderScheduler.createChannel(this)
    }
}

