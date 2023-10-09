package com.roadpass.icecreamroll.browser

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import com.roadpass.icecreamroll.R

class AppIconActivity : AppCompatActivity() {
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_browser)

        val webView = findViewById<View>(R.id.web) as WebView
        webView.loadUrl("https://poki.com/en/ice-cream")
        webView.settings.javaScriptEnabled = true
        webView.webViewClient = WebViewClient()
    }
}