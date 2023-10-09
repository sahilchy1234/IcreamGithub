package com.roadpass.icecreamroll.activity

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdListener
import com.applovin.mediation.MaxError
import com.applovin.mediation.ads.MaxAdView
import com.applovin.sdk.AppLovinSdk
import com.roadpass.icecreamroll.R

class RightSwipeScreen : AppCompatActivity(), MaxAdListener {

    private lateinit var adView: MaxAdView
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_right_swipe_screen)


        AppLovinSdk.getInstance(this@RightSwipeScreen).setMediationProvider("max")
        AppLovinSdk.initializeSdk(this@RightSwipeScreen, AppLovinSdk.SdkInitializationListener { configuration ->
            // AppLovin SDK is initialized, start loading ads
        })


        adView = findViewById(R.id.adView)
        adView.loadAd()

    }

    override fun onAdLoaded(ad: MaxAd?) {
        TODO("Not yet implemented")
    }

    override fun onAdDisplayed(ad: MaxAd?) {
        TODO("Not yet implemented")
    }

    override fun onAdHidden(ad: MaxAd?) {
        TODO("Not yet implemented")
    }

    override fun onAdClicked(ad: MaxAd?) {
        TODO("Not yet implemented")
    }

    override fun onAdLoadFailed(adUnitId: String?, error: MaxError?) {
        TODO("Not yet implemented")
    }

    override fun onAdDisplayFailed(ad: MaxAd?, error: MaxError?) {
        TODO("Not yet implemented")
    }
}