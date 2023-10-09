package com.roadpass.icecreamroll;

import android.app.Application;
import android.content.Intent;

import com.roadpass.icecreamroll.activity.HomeActivity;
import com.roadpass.icecreamroll.activity.RightSwipeScreen;
import com.roadpass.icecreamroll.ads.AppOpenManager;
import com.google.android.gms.ads.MobileAds;

public class AppObject extends Application {
    private static AppObject _instance;
    private static AppOpenManager appOpenManager;

    public static AppObject get() {
        return _instance;
    }

    private int clickCount = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        _instance = this;
        MobileAds.initialize(
                this,
                initializationStatus -> {

                });
        appOpenManager = new AppOpenManager(AppObject.this);

        setAppWideClickListener();


    }
    public void showAd(){
        appOpenManager.showAdIfAvailable(HomeActivity._launcher);
    }

    private void setAppWideClickListener() {
        // Set a global click listener that will track clicks anywhere in the app
        // For example, you can use this for tracking clicks on the root view of each activity
        // Replace HomeActivity with the actual activity class you want to track clicks in
        HomeActivity.setOnGlobalClickListener(view -> {
            // Handle the click event

            clickCount++;
            if (clickCount % 4 == 0) {
                // Show the interstitial ad here
                Intent intent= new Intent(AppObject.this, RightSwipeScreen.class);
            }
        });
    }
}