package com.roadpass.icecreamroll;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import com.roadpass.icecreamroll.activity.HomeActivity;

public class ApplicationClass extends Application {

    private int clickCount = 0;
    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize any ad-related components here (e.g., interstitial ad)

        // Set up a click listener to track clicks anywhere in your app
        setAppWideClickListener();
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
            }
        });
    }
}
