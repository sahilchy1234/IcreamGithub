package com.roadpass.icecreamroll.activity.onboardactivity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.appsflyer.AppsFlyerLib
import com.roadpass.avid.util.getInstallationId
import com.roadpass.icecreamroll.R
import com.roadpass.icecreamroll.activity.HomeActivity


class OnBoardActivity : AppCompatActivity() {
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AppsFlyerLib.getInstance().init("cKCUQ38QmEpeatEyGr3CeV", null, this)
        AppsFlyerLib.getInstance().start(this)

        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)

        setContentView(R.layout.activity_onboard)

        getInstallationId(this)

        if (getSharedPreferences(
                "app",
                MODE_PRIVATE
            ).getBoolean("first", false)
        ) {
            skipStart()
            return
        }
        getSharedPreferences("app", MODE_PRIVATE).edit().putBoolean(
            "first",true
        ).apply()

        val viewPager = findViewById<ViewPager2>(R.id.onboard_viewpager)
//        val tabLayout = findViewById<TabLayout>(R.id.tab_layout)
        val adapter = OnBoardFragmentAdapter(this)
        viewPager.adapter = adapter
//        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
//
//        }.attach()

    }

    override fun onDestroy() {
        super.onDestroy()
        setState()
    }

    private fun skipStart() {
        setState()
        finish()
    }

    private fun setState() {
        getSharedPreferences("app", MODE_PRIVATE).edit().putBoolean(
            resources.getString(R.string.pref_key__show_intro),true
        ).apply()
        val intent = Intent(this, HomeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        startActivity(intent)
    }

}