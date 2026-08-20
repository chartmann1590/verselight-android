package com.chartmann1590.verselight.ads

import android.content.Context
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object AdMobManager {
    private const val COOLDOWN_MS = 120_000L

    private var interstitialAd: InterstitialAd? = null
    private var lastShowMs = 0L
    private var isLoading = false

    private val _isReady = MutableStateFlow(false)
    val isReady: StateFlow<Boolean> = _isReady

    fun initialize(context: Context, onInitialized: (() -> Unit)? = null) {
        MobileAds.initialize(context) {
            _isReady.value = true
            onInitialized?.invoke()
        }
    }

    fun loadInterstitial(context: Context, adUnitId: String) {
        if (isLoading || interstitialAd != null) return
        if (!ConsentManager.canRequestAds) return
        isLoading = true
        val request = AdRequest.Builder().build()
        InterstitialAd.load(context, adUnitId, request, object : InterstitialAdLoadCallback() {
            override fun onAdLoaded(ad: InterstitialAd) {
                interstitialAd = ad
                isLoading = false
            }
            override fun onAdFailedToLoad(error: LoadAdError) {
                interstitialAd = null
                isLoading = false
            }
        })
    }

    fun canShowInterstitial(): Boolean {
        val now = System.currentTimeMillis()
        return interstitialAd != null && now - lastShowMs >= COOLDOWN_MS
    }

    fun showInterstitial(activity: android.app.Activity, adUnitId: String, onDismissed: (() -> Unit)? = null): Boolean {
        val ad = interstitialAd ?: return false
        if (System.currentTimeMillis() - lastShowMs < COOLDOWN_MS) return false
        ad.fullScreenContentCallback = object : com.google.android.gms.ads.FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                interstitialAd = null
                lastShowMs = System.currentTimeMillis()
                loadInterstitial(activity, adUnitId)
                onDismissed?.invoke()
            }
            override fun onAdFailedToShowFullScreenContent(e: com.google.android.gms.ads.AdError) {
                interstitialAd = null
                loadInterstitial(activity, adUnitId)
                onDismissed?.invoke()
            }
        }
        ad.show(activity)
        return true
    }

    fun preloadIfNeeded(context: Context, adUnitId: String) {
        if (interstitialAd == null && !isLoading && ConsentManager.canRequestAds) {
            loadInterstitial(context, adUnitId)
        }
    }
}
