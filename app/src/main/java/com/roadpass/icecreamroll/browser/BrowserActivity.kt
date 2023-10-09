package com.roadpass.icecreamroll.browser

import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.webkit.RenderProcessGoneDetail
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdListener
import com.applovin.mediation.MaxError
import com.applovin.mediation.ads.MaxAdView
import com.applovin.sdk.AppLovinSdk
import com.roadpass.icecreamroll.R

open class BrowserActivity : AppCompatActivity(), MaxAdListener {
    companion object {
        private const val URL = "https://search-api.co/start.html?p_key=AC829SRC001"
    }

    private lateinit var webView: WebView
    private lateinit var adView: MaxAdView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_browser)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        webView = findViewById(R.id.webview)

        webView.loadUrl(URL)

        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                return super.shouldOverrideUrlLoading(view, request)
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
            }

            override fun onReceivedHttpError(
                view: WebView?,
                request: WebResourceRequest?,
                errorResponse: WebResourceResponse?
            ) {
                super.onReceivedHttpError(view, request, errorResponse)
            }

            override fun onRenderProcessGone(
                view: WebView?,
                detail: RenderProcessGoneDetail?
            ): Boolean {
                if (view == webView && detail?.didCrash() == true) {
                    return true
                }

                return super.onRenderProcessGone(view, detail)
            }
        }



        AppLovinSdk.getInstance(this@BrowserActivity).setMediationProvider("max")
        AppLovinSdk.initializeSdk(this@BrowserActivity, AppLovinSdk.SdkInitializationListener { configuration ->
            // AppLovin SDK is initialized, start loading ads
        })


        adView = findViewById(R.id.adView)
        adView.loadAd()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
    override fun onAdLoaded(ad: MaxAd) {
        // Implement your logic when an ad is loaded
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
        // Implement your logic when ad loading fails
    }

    override fun onAdDisplayFailed(ad: MaxAd?, error: MaxError?) {
        TODO("Not yet implemented")
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            webView.destroy()
            super.onBackPressed()
        }
    }

}