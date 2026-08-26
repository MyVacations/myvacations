package es.myvacations.myvacations.data.repository

import android.app.Activity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import es.myvacations.myvacations.domain.repository.AdsController

actual class AdsRepositoryImpl actual constructor() : AdsController {

    private var activity: Activity? = null
    private var interstitialAd: InterstitialAd? = null

    fun setActivity(activity: Activity) {
        this.activity = activity
    }

    actual override fun showInterstitial() {
        val currentActivity = activity ?: return
        val ad = interstitialAd ?: return

        ad.show(currentActivity)

        interstitialAd = null
        loadInterstitial(currentActivity)
    }

    fun loadInterstitial(currentActivity: Activity) {
        val request = AdRequest.Builder().build()
        //TEST - ca-app-pub-3940256099942544/1033173712
        InterstitialAd.load(
            currentActivity,
            "ca-app-pub-5817611533084445/3170321299",
            request,
            object : InterstitialAdLoadCallback() {

                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    interstitialAd = null
                }
            }
        )
    }
}