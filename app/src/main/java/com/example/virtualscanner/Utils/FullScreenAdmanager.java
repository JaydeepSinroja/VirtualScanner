package com.example.virtualscanner.Utils;

import android.app.Activity;

import com.example.virtualscanner.R;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;


public class FullScreenAdmanager {

    private static final String TAG = "InterstitialAdHelper";
    private Activity activity;
    private InterstitialAd mInterstitialAd;
    private Loader loader;
    private OnCompleteAdListener listener;


    public FullScreenAdmanager(Activity context) {
        this.activity = context;
        loader = new Loader(activity, false);


    }

    public void adMobFullScreen(OnCompleteAdListener listener) {
        this.listener = listener;
        loader.show();
        MobileAds.initialize(activity, "ca-app-pub-6181261630904450~3907448086");

        mInterstitialAd = new InterstitialAd(activity);
        mInterstitialAd.setAdUnitId(activity.getResources().getString(R.string.admob_interstitial_id));
        //mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        AdRequest adRequest = new AdRequest.Builder().build();

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                if (loader != null)
                    loader.dismiss();
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();

                }
            }

            @Override
            public void onAdFailedToLoad(LoadAdError adError) {
                if (loader != null)
                    loader.dismiss();
                listener.onAdFinished();

            }

            @Override
            public void onAdOpened() {
                // Code to be executed when the ad is displayed.
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                if (loader != null)
                    loader.dismiss();
                listener.onAdFinished();


            }
        });

        mInterstitialAd.loadAd(adRequest);
    }

}
