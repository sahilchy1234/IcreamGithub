package com.roadpass.icecreamroll.iceCreamGameLaunch

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.roadpass.icecreamroll.R
import com.unity3d.player.UnityPlayerActivity

class IceCreamGameEntryActivity : AppCompatActivity() {
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_browser)

        val intent = Intent(this@IceCreamGameEntryActivity, UnityPlayerActivity::class.java)
        startActivity(intent)
    }
}