package com.chartmann1590.verselight

import android.app.Application
import com.google.firebase.FirebaseApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class VerseLightApplication : Application() {
    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        container = AppContainer(this)
        appScope.launch { container.safety.prepareGeminiNano() }
        ReminderScheduler.createChannel(this)
    }
}
