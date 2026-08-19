package com.example.data.ads

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

object AdMobManager {

    private const val TAG = "AdMobManager"

    // Google AdMob Standard Test Ad Unit IDs (Safe for testing and production fallback)
    const val BANNER_AD_UNIT_ID = "ca-app-pub-3940256099942544/6300978111"
    const val INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712"
    const val REWARDED_AD_UNIT_ID = "ca-app-pub-3940256099942544/5224354917"

    private var interstitialAd: InterstitialAd? = null
    private var isInterstitialLoading = false

    private var rewardedAd: RewardedAd? = null
    private var isRewardedLoading = false

    var isInitialized = false
        private set

    /**
     * Initializes Google Mobile Ads SDK with COPPA / Families Policy compliance.
     */
    fun initialize(context: Context) {
        if (isInitialized) return

        try {
            // Configure for child-safe / family-friendly ads (Rating G)
            val requestConfiguration = RequestConfiguration.Builder()
                .setTagForChildDirectedTreatment(RequestConfiguration.TAG_FOR_CHILD_DIRECTED_TREATMENT_TRUE)
                .setMaxAdContentRating(RequestConfiguration.MAX_AD_CONTENT_RATING_G)
                .build()

            MobileAds.setRequestConfiguration(requestConfiguration)
            MobileAds.initialize(context) {
                isInitialized = true
                Log.d(TAG, "Google Mobile Ads initialized successfully.")
                loadInterstitialAd(context)
                loadRewardedAd(context)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing MobileAds", e)
        }
    }

    /**
     * Creates a standardized AdRequest.
     */
    fun buildAdRequest(): AdRequest {
        return AdRequest.Builder().build()
    }

    // ==========================================
    // INTERSTITIAL ADS
    // ==========================================

    fun loadInterstitialAd(context: Context) {
        if (interstitialAd != null || isInterstitialLoading) return
        isInterstitialLoading = true

        InterstitialAd.load(
            context,
            INTERSTITIAL_AD_UNIT_ID,
            buildAdRequest(),
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                    isInterstitialLoading = false
                    Log.d(TAG, "Interstitial ad loaded.")
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    interstitialAd = null
                    isInterstitialLoading = false
                    Log.w(TAG, "Interstitial failed to load: ${error.message}")
                }
            }
        )
    }

    fun showInterstitialAd(activity: Activity, onAdDismissed: () -> Unit = {}) {
        val ad = interstitialAd
        if (ad != null) {
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    interstitialAd = null
                    loadInterstitialAd(activity)
                    onAdDismissed()
                }

                override fun onAdFailedToShowFullScreenContent(error: AdError) {
                    interstitialAd = null
                    loadInterstitialAd(activity)
                    onAdDismissed()
                }
            }
            ad.show(activity)
        } else {
            // If ad not loaded yet, proceed immediately without blocking user
            loadInterstitialAd(activity)
            onAdDismissed()
        }
    }

    // ==========================================
    // REWARDED VIDEO ADS
    // ==========================================

    fun loadRewardedAd(context: Context) {
        if (rewardedAd != null || isRewardedLoading) return
        isRewardedLoading = true

        RewardedAd.load(
            context,
            REWARDED_AD_UNIT_ID,
            buildAdRequest(),
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    rewardedAd = ad
                    isRewardedLoading = false
                    Log.d(TAG, "Rewarded ad loaded.")
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    rewardedAd = null
                    isRewardedLoading = false
                    Log.w(TAG, "Rewarded ad failed to load: ${error.message}")
                }
            }
        )
    }

    fun isRewardedAdReady(): Boolean = rewardedAd != null

    fun showRewardedAd(
        activity: Activity,
        onUserEarnedReward: (rewardAmount: Int) -> Unit,
        onAdDismissed: () -> Unit = {}
    ) {
        val ad = rewardedAd
        if (ad != null) {
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    rewardedAd = null
                    loadRewardedAd(activity)
                    onAdDismissed()
                }

                override fun onAdFailedToShowFullScreenContent(error: AdError) {
                    rewardedAd = null
                    loadRewardedAd(activity)
                    onAdDismissed()
                }
            }
            ad.show(activity) { rewardItem ->
                val amount = if (rewardItem.amount > 0) rewardItem.amount else 5
                onUserEarnedReward(amount)
            }
        } else {
            loadRewardedAd(activity)
            onAdDismissed()
        }
    }
}
