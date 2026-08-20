package com.chartmann1590.verselight.ads

import android.app.Activity
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.FormError
import com.google.android.ump.UserMessagingPlatform

object ConsentManager {
    var canRequestAds = false
        private set

    fun requestConsent(activity: Activity, onResult: (Boolean) -> Unit) {
        val params = ConsentRequestParameters.Builder().build()
        val info = UserMessagingPlatform.getConsentInformation(activity)
        info.requestConsentInfoUpdate(
            activity,
            params,
            {
                UserMessagingPlatform.loadAndShowConsentFormIfRequired(activity) { _: FormError? ->
                    canRequestAds = info.canRequestAds()
                    onResult(canRequestAds)
                }
            },
            { _: FormError ->
                canRequestAds = info.canRequestAds()
                onResult(canRequestAds)
            }
        )
    }

    fun showPrivacyOptionsForm(activity: Activity, onDismiss: (() -> Unit)? = null) {
        UserMessagingPlatform.showPrivacyOptionsForm(activity) { _: FormError? -> onDismiss?.invoke() }
    }
}
