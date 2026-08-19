package com.example.data.ads

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class StudioAdManager(private val context: Context) {

    // Test Ad Unit IDs provided by Google AdMob for Android
    companion object {
        const val BANNER_AD_UNIT_ID = "ca-app-pub-3940256099942544/6300978111"
        const val INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712"
        const val REWARDED_AD_UNIT_ID = "ca-app-pub-3940256099942544/5224354917"
    }

    private var interstitialAd: InterstitialAd? = null
    private var rewardedAd: RewardedAd? = null

    private val _isRewardUnlocked = MutableStateFlow(false)
    val isRewardUnlocked: StateFlow<Boolean> = _isRewardUnlocked.asStateFlow()

    private val _credits = MutableStateFlow(50)
    val credits: StateFlow<Int> = _credits.asStateFlow()

    init {
        MobileAds.initialize(context) {}
        loadInterstitialAd()
        loadRewardedAd()
    }

    fun loadInterstitialAd() {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            context,
            INTERSTITIAL_AD_UNIT_ID,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    interstitialAd = null
                }
            }
        )
    }

    fun showInterstitial(activity: Activity?, onDismissed: () -> Unit = {}) {
        if (interstitialAd != null && activity != null) {
            interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    interstitialAd = null
                    loadInterstitialAd()
                    onDismissed()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    interstitialAd = null
                    loadInterstitialAd()
                    onDismissed()
                }
            }
            interstitialAd?.show(activity)
        } else {
            onDismissed()
        }
    }

    fun loadRewardedAd() {
        val adRequest = AdRequest.Builder().build()
        RewardedAd.load(
            context,
            REWARDED_AD_UNIT_ID,
            adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    rewardedAd = ad
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    rewardedAd = null
                }
            }
        )
    }

    fun showRewardedAd(activity: Activity?, onRewardEarned: (Int) -> Unit) {
        if (rewardedAd != null && activity != null) {
            rewardedAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    rewardedAd = null
                    loadRewardedAd()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    rewardedAd = null
                    loadRewardedAd()
                }
            }
            rewardedAd?.show(activity) { rewardItem ->
                val amount = rewardItem.amount.coerceAtLeast(25)
                _credits.value += amount
                _isRewardUnlocked.value = true
                onRewardEarned(amount)
            }
        } else {
            // Fallback reward for testing in case ad is loading
            _credits.value += 25
            _isRewardUnlocked.value = true
            onRewardEarned(25)
            loadRewardedAd()
        }
    }
}
