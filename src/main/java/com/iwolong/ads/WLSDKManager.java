package com.iwolong.ads;


import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;
import android.support.annotation.MainThread;
import com.bytedance.sdk.openadsdk.TTSplashAd;



import com.changwan.hcrzdy.sogou.SplashActivity;
import com.iwolong.ads.unity.PolyProxy;
import com.iwolong.ads.utils.WLLogUtils;

import com.bytedance.sdk.openadsdk.FilterWord;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdDislike;
import com.bytedance.sdk.openadsdk.TTAdManager;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAppDownloadListener;
import com.bytedance.sdk.openadsdk.TTFullScreenVideoAd;
import com.bytedance.sdk.openadsdk.TTNativeExpressAd;
import com.bytedance.sdk.openadsdk.TTRewardVideoAd;
import com.iwolong.ads.network.WLData;
import com.iwolong.ads.utils.WLInitialization;
import com.meizu.ads.AdSDK;
import com.meizu.ads.AdSlot;
import com.meizu.ads.banner.BannerAd;
import com.meizu.ads.banner.BannerAdListener;
import com.meizu.ads.interstitial.InterstitialAd;
import com.meizu.ads.interstitial.InterstitialAdListener;
import com.meizu.ads.rewardvideo.RewardVideoAd;
import com.meizu.ads.rewardvideo.RewardVideoAdListener;
import com.meizu.ads.splash.SplashAd;
import com.meizu.ads.splash.SplashAdListener;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;

import static com.iwolong.ads.unity.PolyProxy.callbackUnity;
import static com.iwolong.ads.unity.PolyProxy.callbackUnity2;

public class WLSDKManager {
    private static WLSDKManager sManager = new com.iwolong.ads.WLSDKManager();
    private  static  final String TAG= "WLSDKManager";
    private BannerAd mBannerAd;
    private FrameLayout mSplashContainer;
    private static final int AD_TIME_OUT = 30000;

    private InterstitialAd mInterstitialAd;
    private InterstitialAd mInterstitialAd2;
    private RewardVideoAd mRewardVideoAd;
    private WLSDKManager() {}
    public static WLSDKManager instance() {
        return sManager;
    }
    public void showBanner(final Activity activity, final ViewGroup container, final String position) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG,"bannerPos="+position);
                WLData wldata = WLInitialization.instance().getWLData();
                if (wldata != null && !wldata.isDisplayAd(position)) {


                    return;
                }
                if(mBannerAd!=null){
                    mBannerAd.showAd();

                }
            }
        });
    }

    public void loadBannerAd(final Activity activity,final ViewGroup container,String codeId) {
        //step1:创建Banner广告对象，在此之前请确保已经初始化
        if (mBannerAd == null) {

            com.meizu.ads.AdSlot adSlot = new AdSlot.Builder()
                    .setBlockId(codeId)
                    .setInterval(30).build();


            mBannerAd = new BannerAd(activity, container, adSlot, new BannerAdListener() {
                @Override
                public void onAdLoaded() {
                    Log.i(TAG,"banner已加载");
                }

                @Override
                public void onNoAd(int errorCode, String msg) {
                    Log.i(TAG,"banner无广告填充:"+errorCode+" "+msg);

                }

                @Override
                public void onAdClicked() {

                }

                @Override
                public void onAdShow() {
                    Log.i(TAG,"banner展示成功");
                }

                @Override
                public void onAdClosed() {
                    callbackUnity2("","","");
                    loadBannerAd(activity,container,WLInitialization.instance().getBannerAdId());
                }

                @Override
                public void onAdError(int errorCode, String msg) {
                    Log.i(TAG,"banner出错:"+errorCode+" "+msg);

                }
            });
        }
        //step2:加载广告
        mBannerAd.loadAd();
    }

    private boolean mHasShowDownloadActive1 = false;

    private void bindAdListener(TTNativeExpressAd ad,final ViewGroup container,final Activity activity) {

    }
    // 按钮事件绑定
    private void bindDislike(TTNativeExpressAd ad, boolean customStyle,final Activity activity,final ViewGroup container) {


    }


    // 插屏广告
    public void showInterstitialAd(final Activity activity,final ViewGroup container,final String position) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final WLData wldata = WLInitialization.instance().getWLData();

                if (wldata != null && !wldata.isDisplayAd(position)) {
                    return;
                }
                if (mInterstitialAd != null) {
                    if (mInterstitialAd.isReady()) {
                        Log.w(TAG, "插页广告1缓存就绪.");
//                        mInterstitialAd.showAd(activity);
//                        mInterstitialAd.showAd(activity);
                        ShowIntertactionAd(activity,container,"");
                        loadInteractionAd(WLInitialization.instance().getInterstitialAdId(position),container,activity);
                    } else {
                        Log.w(TAG, "插页广告1缓存未就绪.");
                        loadInteractionAd(WLInitialization.instance().getInterstitialAdId(position),container,activity);
                    }
                }
            }
        });

    }
    // 初始化插屏广告
    public void loadInteractionAd(final String codeId,final ViewGroup containers,final Activity activity) {
        if (mInterstitialAd == null) {
            mInterstitialAd = new InterstitialAd(activity, codeId,
                    new InterstitialAdListener() {
                        @Override
                        public void onAdLoaded() {
                            Log.v(TAG, "#onAdLoaded.");
                            if(adsItem==null){
                                adsItem=new LinkedBlockingDeque<>();

                            }
                            adsItem.offer(mInterstitialAd);
                        }

                        @Override
                        public void onNoAd(int code, String msg) {
                            Log.v(TAG, "#onNoAd : " + code + " " + msg);

                        }

                        @Override
                        public void onAdShow() {
                            Log.v(TAG, "#onAdShow.");

                        }

                        @Override
                        public void onAdClicked() {
                            Log.v(TAG, "#onAdClicked.");

                        }

                        @Override
                        public void onAdClosed() {
                            Log.v(TAG, "#onAdClosed.");

                        }

                        @Override
                        public void onAdError(int code, String msg) {
                            Log.v(TAG, "#onAdError : " + code + " " + msg);

                        }
                    });
        }
        mInterstitialAd.loadAd();

    }
    private Queue<InterstitialAd> adsItem=new LinkedBlockingDeque<>();
    private  void ShowIntertactionAd(final Activity activity,final ViewGroup container,final String position){
        if(adsItem!=null&&adsItem.size()>0){
            InterstitialAd ad=adsItem.poll();
            if(ad!=null){
                ad.showAd(activity);
               // ad.showAd(activity);

            }

        }
    }

    // 插屏广告
    public void showFullAd(final Activity activity,final ViewGroup container,final String position) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                WLData wldata = WLInitialization.instance().getWLData();
                if (wldata != null && !wldata.isDisplayAd(position)) {
                    return;
                }
                if (mInterstitialAd2 != null) {
                    if (mInterstitialAd2.isReady()) {
                        Log.w(TAG, "插页广告2缓存就绪.");
//                        mInterstitialAd.showAd(activity);
//                        mInterstitialAd.showAd(activity);
                       mInterstitialAd2.showAd(activity);
                        loadFullAd(WLInitialization.instance().getFullscreenAdId(position),container,activity);
                    } else {
                        Log.w(TAG, "插页广告2缓存未就绪.");
                        loadFullAd(WLInitialization.instance().getFullscreenAdId(position),container,activity);
                    }
                }
            }
        });

    }
    // 初始化插屏广告
    public void loadFullAd(final String codeId,final ViewGroup containers,final Activity activity) {
        if (mInterstitialAd2 == null) {
            mInterstitialAd2 = new InterstitialAd(activity, codeId,
                    new InterstitialAdListener() {
                        @Override
                        public void onAdLoaded() {
                            Log.v(TAG, "#onAdLoaded.");


                        }

                        @Override
                        public void onNoAd(int code, String msg) {
                            Log.v(TAG, "#onNoAd : " + code + " " + msg);

                        }

                        @Override
                        public void onAdShow() {
                            Log.v(TAG, "#onAdShow.");

                        }

                        @Override
                        public void onAdClicked() {
                            Log.v(TAG, "#onAdClicked.");

                        }

                        @Override
                        public void onAdClosed() {
                            Log.v(TAG, "#onAdClosed.");
                            WLData wldata2 = WLInitialization.instance().getWLData();
                            if (wldata2 != null && !wldata2.isDisplayAd("7b26b600e24b80ae4a1773d091199f2f")) {
                                return;
                            }
                            loadSplasAdIfNeed(activity,containers);
                        }

                        @Override
                        public void onAdError(int code, String msg) {
                            Log.v(TAG, "#onAdError : " + code + " " + msg);

                        }
                    });
        }
        mInterstitialAd2.loadAd();

    }







    //按钮事件的监听
    private void bindAdListener(final TTNativeExpressAd ad,final Activity activity,final ViewGroup container) {

    }


    public void showRewardAd(final Activity activity, final String position,ViewGroup containers) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                WLData wldata = WLInitialization.instance().getWLData();
                if (wldata != null && !wldata.isDisplayAd(position)) {
                    return;
                }
                if (mRewardVideoAd != null && mRewardVideoAd.isReady()) {
                    mRewardVideoAd.showAd();
                    loadRewardAd(WLInitialization.instance().getRewardAdId(position),activity);

                } else {
                    Log.w(TAG, "激励视频广告尚未缓存就绪!");

                    loadRewardAd(WLInitialization.instance().getRewardAdId(position),activity);
                }
            }
        });
    }

    // 初始化 激励视频
    public void loadRewardAd(final String codeId, final Activity activity) {
        if (mRewardVideoAd == null) {
            mRewardVideoAd = new RewardVideoAd(activity,codeId, new VideoRewardVideoAdListener(activity, codeId));
        }
        mRewardVideoAd.loadAd();


    }





    private static class VideoRewardVideoAdListener implements RewardVideoAdListener {

        private Activity activity;
        private String blockId;

        VideoRewardVideoAdListener(Activity activity, String blockId) {
            this.activity = activity;
            this.blockId = blockId;
        }

        @Override
        public void onAdLoaded() {

            Log.v(TAG, "Video AD loaded successfully : " + blockId);
        }

        @Override
        public void onNoAd(int code, String msg) {

            Log.v(TAG, String.format("Failed to loadAd video AD, blockId=%s, errCode=%d, errMsg=%s ",  blockId, code, msg));
        }

        @Override
        public void onAdShow() {

            Log.v(TAG, "onAdShow, blockId => " + blockId);
        }

        @Override
        public void onAdClicked() {

            Log.v(TAG, "onAdClicked, blockId => " + blockId);
        }

        @Override
        public void onAdClosed(boolean reward) {
            Log.v(TAG, "onAdsDismissed, blockId=>," + blockId + " reward=>" + reward);
            callbackUnity("onReward","","");
            WLData wldata2 = WLInitialization.instance().getWLData();
            if (wldata2 != null && !wldata2.isDisplayAd("7b26b600e24b80ae4a1773d091199f2f")) {
                return;
            }
            loadSplasAdIfNeed(activity, (ViewGroup) PolyProxy.instance().container1);
            if(reward) {
                Log.v(TAG, "可以发放奖励");

            }
        }

        @Override
        public void onAdError(int code, String msg) {

            Log.v(TAG, "Video play error, blockId=> "+blockId+", code=> " + code + ", message=> " + msg);
        }
    }
    /**
     * 加载开屏广告
     */
    private SplashAd mSplashAd;
    public void loadSplashAd(SplashAdListener ad,Activity a) {
//        mSplashAd = new SplashAd(a,
//                mAdContainer, mSkipView, WLInitialization.instance().getAdSDKSplashId(), ad);
    }
    private static final int REQUEST_PERMISSIONS_CODE = 100;
    private static final String SKIP_TEXT = "跳过 | %d";
    private ViewGroup mAdContainer;
    private Button mSkipView;

    public static void loadSplasAdIfNeed(Activity a,ViewGroup v) {
        Intent it = new Intent(a, SplashActivity.class);
        it.addCategory("android.intent.category.LAUNCHER");
        it.setAction("android.intent.action.MAIN");
        a.startActivity(it);
//        a.runOnUiThread(new Runnable() {
//
//            @Override
//            public void run() {
//                Log.e("AdSDK", "开屏1" );
//                View view = a.getLayoutInflater().inflate(R.layout.activity_splash_ad, null);
//                mAdContainer = view.findViewById(R.id.container);
//                mSkipView =view.findViewById(R.id.btn_skip);
//                mSkipView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        mAdContainer.removeAllViews();
//                        mSkipView.setVisibility(View.GONE);
//                    }
//                });
//                v.addView(view);
//                Log.e("AdSDK", "开屏2" );
//                loadSplashAd(new SplashAdListener() {
//                    public void onAdLoaded() {
//                        Log.e("AdSDK", "开屏加载完成" );
//
//                    }
//
//                    @Override
//                    public void onAdShow() {
//                        Log.e("AdSDK", "开屏xianshi" );
//                    }
//
//                    @Override
//                    public void onTick(long millisUntilFinished) {
//                        Log.e("AdSDK", "onTick : " + millisUntilFinished);
//                        if (mSkipView != null) {
//                            mSkipView.setText(String.format(SKIP_TEXT, Math.round(millisUntilFinished / 1000L)));
//                            mSkipView.setVisibility(View.VISIBLE);
//                        }
//                    }
//
//                    @Override
//                    public void onAdClicked() {
//
//                    }
//
//                    @Override
//                    public void onAdClosed(int i) {
//                        Log.e("AdSDK", "onAdClosed" );
////                        mAdContainer.removeAllViews();
////                        mSkipView.setVisibility(View.GONE);
//                    }
//
//                    @Override
//                    public void onAdError(int i, String s) {
//                        Log.e("AdSDK", "开屏加shibai" );
//                        mSkipView.setVisibility(View.GONE);
//                        mAdContainer.removeAllViews();
////                 if (!isFinishing()) {
////
////                 }
//                    }
//                },a);
//            }
//        });


    }

    public interface OnWLSplashAdLoadedListener {

    }



}