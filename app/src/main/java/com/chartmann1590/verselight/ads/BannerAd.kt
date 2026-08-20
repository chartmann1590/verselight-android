package com.chartmann1590.verselight.ads

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

@Composable
fun BannerAd(adUnitId: String, modifier: Modifier = Modifier) {
    if (!ConsentManager.canRequestAds) return
    val width = LocalConfiguration.current.screenWidthDp
    val adSize = AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(
        androidx.compose.ui.platform.LocalContext.current, width
    )
    val heightDp = adSize.height.dp
    AndroidView(
        modifier = modifier.fillMaxWidth().height(heightDp),
        factory = { context ->
            AdView(context).apply {
                setAdSize(adSize)
                this.adUnitId = adUnitId
                loadAd(AdRequest.Builder().build())
            }
        },
        update = { view ->
            if (view.adUnitId != adUnitId) {
                view.adUnitId = adUnitId
                view.loadAd(AdRequest.Builder().build())
            }
        }
    )
}
