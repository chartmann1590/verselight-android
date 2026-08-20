package com.chartmann1590.verselight

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.chartmann1590.verselight.ads.AdMobManager
import com.chartmann1590.verselight.ads.ConsentManager
import com.chartmann1590.verselight.ui.VerseLightRoot
import com.chartmann1590.verselight.ui.theme.VerseLightTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        ConsentManager.requestConsent(this) { canRequest ->
            if (canRequest) {
                AdMobManager.initialize(this) {
                    val adUnit = getString(R.string.admob_interstitial_id)
                    AdMobManager.loadInterstitial(this, adUnit)
                }
            }
        }
        setContent { VerseLightTheme { VerseLightRoot() } }
    }
}
