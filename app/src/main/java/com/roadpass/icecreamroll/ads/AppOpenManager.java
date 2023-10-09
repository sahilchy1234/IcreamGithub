package com.roadpass.icecreamroll.ads;


import android.app.Activity;

import com.roadpass.icecreamroll.AppObject;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.appopen.AppOpenAd;


public class AppOpenManager {
    private static final String AD_UNIT_ID = "ca-app-pub-3940256099942544/3419835294";
    private AppOpenAd appOpenAd = null;

    private AppOpenAd.AppOpenAdLoadCallback loadCallback;

    private final AppObject myApplication;


    public AppOpenManager(AppObject myApplication) {
        this.myApplication = myApplication;
        fetchAd();
    }

    public void fetchAd() {

        if (isAdAvailable()) {
            return;
        }

        loadCallback =
                new AppOpenAd.AppOpenAdLoadCallback() {

                    @Override
                    public void onAdLoaded(AppOpenAd ad) {

                        AppOpenManager.this.appOpenAd = ad;
                    }

                    @Override
                    public void onAdFailedToLoad(LoadAdError error) {

                    }

                };
        AdRequest request = getAdRequest();
        AppOpenAd.load(
                myApplication, AD_UNIT_ID, request,
                AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT, loadCallback);
    }


    private AdRequest getAdRequest() {
        return new AdRequest.Builder().build();
    }


    public boolean isAdAvailable() {
        return appOpenAd != null;
    }

    private static boolean isShowingAd = false;

    public void showAdIfAvailable(Activity activity) {

        if (!isShowingAd && isAdAvailable()) {


            FullScreenContentCallback fullScreenContentCallback =
                    new FullScreenContentCallback() {
                        @Override
                        public void onAdDismissedFullScreenContent() {

                            AppOpenManager.this.appOpenAd = null;
                            isShowingAd = false;

                        }

                        @Override
                        public void onAdFailedToShowFullScreenContent(AdError adError) {
                        }

                        @Override
                        public void onAdShowedFullScreenContent() {
                            isShowingAd = true;
                        }
                    };

            appOpenAd.setFullScreenContentCallback(fullScreenContentCallback);
            appOpenAd.show(activity);

        } else {
            fetchAd();
        }
    }
}
