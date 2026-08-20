package com.chartmann1590.verselight

import android.app.Application
import com.chartmann1590.verselight.ads.AdMobManager
import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.perf.FirebasePerformance
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
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)
        FirebasePerformance.getInstance().isPerformanceCollectionEnabled = true
        container = AppContainer(this)
        appScope.launch { container.safety.prepareGeminiNano() }
        ReminderScheduler.createChannel(this)
        AdMobManager.initialize(this)
    }
}
