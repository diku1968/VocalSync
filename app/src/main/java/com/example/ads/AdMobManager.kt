package com.example.ads

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

object AdMobManager {
    private const val TAG = "AdMobManager"

    // User's AdMob Ad Unit IDs from AdMob Console
    const val BANNER_AD_UNIT_ID = "ca-app-pub-8497938951597030/1580148237"
    const val INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-8497938951597030/6023624424"

    // Standard Google test ad unit IDs as reliable fallback during development / emulator testing
    const val TEST_BANNER_AD_UNIT_ID = "ca-app-pub-3940256099942544/6300978111"
    const val TEST_INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712"

    private var interstitialAd: InterstitialAd? = null
    private var isAdLoading = false

    fun initialize(context: Context) {
        try {
            MobileAds.initialize(context) { initializationStatus ->
                Log.d(TAG, "AdMob initialized: $initializationStatus")
            }
            loadInterstitial(context)
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing MobileAds", e)
        }
    }

    fun loadInterstitial(context: Context) {
        if (isAdLoading || interstitialAd != null) return
        isAdLoading = true

        val adRequest = AdRequest.Builder().build()
        // Try user's unit ID first, fallback handled smoothly
        InterstitialAd.load(
            context,
            INTERSTITIAL_AD_UNIT_ID,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                    isAdLoading = false
                    Log.d(TAG, "Interstitial ad loaded successfully")
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    interstitialAd = null
                    isAdLoading = false
                    Log.w(TAG, "Interstitial ad failed with live ID: ${loadAdError.message}. Loading test ad.")
                    // Fallback to test interstitial to ensure preview functionality works everywhere
                    InterstitialAd.load(
                        context,
                        TEST_INTERSTITIAL_AD_UNIT_ID,
                        adRequest,
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
        )
    }

    fun showInterstitial(activity: Activity, onAdDismissed: () -> Unit) {
        val ad = interstitialAd
        if (ad != null) {
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    interstitialAd = null
                    loadInterstitial(activity)
                    onAdDismissed()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    interstitialAd = null
                    loadInterstitial(activity)
                    onAdDismissed()
                }
            }
            ad.show(activity)
        } else {
            // If ad is not ready, continue without blocking the user
            loadInterstitial(activity)
            onAdDismissed()
        }
    }
}
