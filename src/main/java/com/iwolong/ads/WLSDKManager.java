package com.iwolong.ads;

import android.app.Activity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.support.annotation.MainThread;
import com.bytedance.sdk.openadsdk.TTSplashAd;


import com.iwolong.ads.utils.WLLogUtils;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.FilterWord;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdDislike;
import com.bytedance.sdk.openadsdk.TTAdManager;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAppDownloadListener;
import com.bytedance.sdk.openadsdk.TTFullScreenVideoAd;
import com.bytedance.sdk.openadsdk.TTNativeExpressAd;
import com.bytedance.sdk.openadsdk.TTRewardVideoAd;
import com.iwolong.ads.config.TTAdManagerHolder;
import com.iwolong.ads.dialog.DislikeDialog;
import com.iwolong.ads.network.WLData;
import com.iwolong.ads.utils.WLInitialization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;

import static com.iwolong.ads.unity.PolyProxy.callbackUnity;

public class WLSDKManager {
    private static WLSDKManager sManager = new com.iwolong.ads.WLSDKManager();
    private  static  final String TAG= "VIDEO";
    private WLSDKManager() {}
    public static WLSDKManager instance() {
        return sManager;
    }

    //TTbaner
    private TTAdNative mTTAdNativeBanner;
    private boolean mHasShowDownloadActive = false;

    //TT插屏

    private TTNativeExpressAd mTTAd;
    private TTNativeExpressAd mTTIntersitialAd;
    private List<TTNativeExpressAd> adsItem;
    private Queue<TTNativeExpressAd> mTTIntersitialAdQueue = new LinkedBlockingDeque<>();
    private Map<String,  Queue<TTNativeExpressAd>> mTTIntersitialAdQueueMap = new HashMap<>();
    private  View IntertactionView;

    //激励视频
    private TTAdNative mTTAdNativeReward;
    private TTRewardVideoAd mttRewardVideoAd;
    private boolean mHasShowDownloadActiveRward = false;

    private Map<String, Queue<TTRewardVideoAd>> mttRewardVideoAdQueue=new HashMap<>();

    private FrameLayout mSplashContainer;
    private static final int AD_TIME_OUT = 30000;


    //全屏视频
    private TTAdNative mTTAdNativeFullReward;
    private TTFullScreenVideoAd mttFullVideoAd;
    private  Map<String, Queue<TTFullScreenVideoAd>> mttFullVideoAdQueue =new HashMap<>();

    
    public void showBanner(final Activity activity, final ViewGroup container, final String position) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTTAdNativeBanner = TTAdManagerHolder.get().createAdNative(activity);

                WLData wldata = WLInitialization.instance().getWLData();

                //wldata为空时，可能是网络或我们服务器存在问题，所以当wldata为空时，需要显示广告
                if (wldata != null && !wldata.isDisplayAd(position)) {

                    return;
                }
                loadBannerAd(activity,container,WLInitialization.instance().getBannerAdId());

            }
        });
    }

    public void loadBannerAd(final Activity activity,final ViewGroup container,String position) {


        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(position) //广告位id
                .setSupportDeepLink(true)
                .setAdCount(1) //请求广告数量为1到3条
                .setExpressViewAcceptedSize(280,40) //期望模板广告view的size,单位dp
                .setImageAcceptedSize(640,320 )//这个参数设置即可，不影响模板广告的size
                .build();
        //step5:请求广告，对请求回调的广告作渲染处理
        mTTAdNativeBanner.loadBannerExpressAd(adSlot, new TTAdNative.NativeExpressAdListener() {
            @Override
            public void onError(int code, String message) {

            }

            @Override
            public void onNativeExpressAdLoad(List<TTNativeExpressAd> ads) {
                if (ads == null || ads.size() == 0){
                    return;
                }
                mTTAd = ads.get(0);
//                mTTAd.setSlideIntervalTime(30*1000);
               // mTTAd.setSlideIntervalTime(10*1000);
                bindAdListener(mTTAd,container,activity);


                mTTAd.render();
            }




        });
    }

    private boolean mHasShowDownloadActive1 = false;
    List<View> list=new ArrayList<View>();

    private void clearList(ViewGroup container){
        if(list!=null){
            for(int i=0;i<list.size();i++){
                container.removeView(list.get(i));
            }
            list.clear();

        }

    }

    private void bindAdListener(TTNativeExpressAd ad,final ViewGroup container,final Activity activity) {
        ad.setExpressInteractionListener(new TTNativeExpressAd.ExpressAdInteractionListener() {
            @Override
            public void onAdClicked(View view, int type) {

              //  container.removeViewAt(1);
               // container.removeView(view);
                clearList(container);
            }

            @Override
            public void onAdShow(View view, int type) {

            }

            @Override
            public void onRenderFail(View view, String msg, int code) {

            }

            @Override
            public void onRenderSuccess(View view, float width, float height) {

                clearList(container);
                list.add(view);

                //返回view的宽高 单位 dp
                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(Math.round(activity.getResources().getDisplayMetrics().density * 280), Math.round(activity.getResources().getDisplayMetrics().density * 40)
                );
                layoutParams.gravity = Gravity.BOTTOM| Gravity.CENTER_HORIZONTAL;
////                       layoutParams.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
////                        layoutParams.gravity = Gravity.BOTTOM;
//               layoutParams.leftMargin=250;
//               layoutParams.rightMargin=250;

               // container.addView(view,1,layoutParams);


                container.addView(view,layoutParams);

            }
        });
        //dislike设置
        bindDislike(ad, false,activity,container);
        if (ad.getInteractionType() != TTAdConstant.INTERACTION_TYPE_DOWNLOAD){
            return;
        }
        ad.setDownloadListener(new TTAppDownloadListener() {
            @Override
            public void onIdle() {

            }

            @Override
            public void onDownloadActive(long totalBytes, long currBytes, String fileName, String appName) {
                if (!mHasShowDownloadActive1) {
                    mHasShowDownloadActive1= true;

                }
            }

            @Override
            public void onDownloadPaused(long totalBytes, long currBytes, String fileName, String appName) {

            }

            @Override
            public void onDownloadFailed(long totalBytes, long currBytes, String fileName, String appName) {

            }

            @Override
            public void onInstalled(String fileName, String appName) {

            }

            @Override
            public void onDownloadFinished(long totalBytes, String fileName, String appName) {

            }
        });
    }
    // 按钮事件绑定
    private void bindDislike(TTNativeExpressAd ad, boolean customStyle,final Activity activity,final ViewGroup container) {

        if (customStyle) {
            //使用自定义样式
            List<FilterWord> words = ad.getFilterWords();
            if (words == null || words.isEmpty()) {
                return;
            }

            DislikeDialog dislikeDialog = new DislikeDialog(activity, words);
            dislikeDialog.setOnDislikeItemClick(new DislikeDialog.OnDislikeItemClick() {
                @Override
                public void onItemClick(FilterWord filterWord) {
                    //屏蔽广告
                    Log.i(TAG,"关闭1");
                    //用户选择不喜欢原因后，移除广告展示
                  //  container.removeViewAt(1);
                   // container.removeView(ad.getExpressAdView());
                    clearList(container);
                }
            });
            ad.setDislikeDialog(dislikeDialog);
            return;
        }
        //使用默认模板中默认dislike弹出样式
        ad.setDislikeCallback(activity, new TTAdDislike.DislikeInteractionCallback() {
            @Override
            public void onSelected(int position, String value) {

                //用户选择不喜欢原因后，移除广告展示
                Log.i(TAG,"关闭2");
             //   container.removeViewAt(1);
               // container.removeView(ad.getExpressAdView());
                clearList(container);
            }

            @Override
            public void onCancel() {
                Log.i(TAG,"关闭3");
            }
        });
    }


    // 插屏广告
    public void showInterstitialAd(final Activity activity,final ViewGroup container,final String position) {

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final WLData wldata = WLInitialization.instance().getWLData();

                if (wldata == null || !wldata.isDisplayAd(position)) {
                    return;
                }
                String positionId= WLInitialization.instance().getInterstitialAdId(position);
                ShowIntertactionAd(activity,container,positionId);
                loadInteractionAd(positionId,container,activity);

            }
        });
    }
    // 初始化插屏广告
    public void loadInteractionAd(final String position,final ViewGroup containers,final Activity activity) {
        Log.i(TAG,"调用加载插屏");
        TTAdNative mTTAdNativeInter = TTAdManagerHolder.get().createAdNative(activity);
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(position) //广告位id
                .setSupportDeepLink(true)
                .setAdCount(1) //请求广告数量为1到3条
                .setExpressViewAcceptedSize(450,300) //期望模板广告view的size,单位dp
                .setImageAcceptedSize(450,300 )//这个参数设置即可，不影响模板广告的size
                .build();
        //step5:请求广告，对请求回调的广告作渲染处理
        mTTAdNativeInter.loadInteractionExpressAd(adSlot, new TTAdNative.NativeExpressAdListener() {

            @Override
            public void onError(int code, String message) {
                Log.i(TAG, "onError: "+code+" "+message);
            }

            @Override
            public void onNativeExpressAdLoad(List<TTNativeExpressAd> ads) {

                if (ads == null || ads.size() == 0){

                    return;
                }
                Log.i(TAG,"插屏加载成功");
                adsItem=ads;
//                Random rand = new Random();
//                int i = rand.nextInt(2);
                TTNativeExpressAd ttad =adsItem.get(0);

                bindAdListener(ttad,activity,containers,position);
                ttad.render();
            }
        });
    }

    private  void ShowIntertactionAd(final Activity activity,final ViewGroup container,final String position){

        Queue<TTNativeExpressAd> queue = mTTIntersitialAdQueueMap.get(position);
        if (queue != null && queue.size() > 0) {

            TTNativeExpressAd ttad = queue.poll();
            if(ttad != null) {
                ttad.showInteractionExpressAd(activity);
            }
        }
    }

    
    //按钮事件的监听
    private void bindAdListener(final TTNativeExpressAd ad,final Activity activity,final ViewGroup container,final String position) {
        ad.setExpressInteractionListener(new TTNativeExpressAd.AdInteractionListener() {
            @Override
            public void onAdDismiss() {
//                loadInteractionAd(Constants.TT_SDK_AD_INTERSTITIAL_ID,container,activity);
            }

            @Override
            public void onAdClicked(View view, int type) {

            }

            @Override
            public void onAdShow(View view, int type) {

            }

            @Override
            public void onRenderFail(View view, String msg, int code) {


            }

            @Override
            public void onRenderSuccess(View view, float width, float height) {
                //返回view的宽高 单位 dp

                Queue queue = mTTIntersitialAdQueueMap.get(position);
                if (queue == null) {
                    queue = new LinkedBlockingDeque();
                    mTTIntersitialAdQueueMap.put(position, queue);
                }
                queue.offer(ad);

            }
        });


    }


    public void showRewardAd(final Activity activity, final String position) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
             //   Log.i(TAG,"播放激励视频");
                WLData wldata = WLInitialization.instance().getWLData();
                //wldata为空时，可能是网络或我们服务器存在问题，所以当wldata为空时，需要显示广告
                if (wldata != null && !wldata.isDisplayAd(position)) {
                   // Log.i(TAG,"激励视频播放为false");
                    return;
                }

                String codeId = WLInitialization.instance().getRewardAdId(position);
                Queue<TTRewardVideoAd> queue = mttRewardVideoAdQueue.get(codeId);

                if (queue != null && queue.size() > 0) {
                    TTRewardVideoAd ttad = queue.poll();;
                    ttad.showRewardVideoAd(activity,TTAdConstant.RitScenes.CUSTOMIZE_SCENES,"scenes_test");
                } else {
                    loadRewardAd(codeId, activity, false);
                }

            }
        });
    }

    // 初始化 激励视频
    public void loadRewardAd(final String position,final Activity activity, boolean add2Queue) {
        TTAdManager ttAdManager = TTAdManagerHolder.get();
        TTAdManagerHolder.get().requestPermissionIfNecessary(activity);
        Log.i(TAG,"激励视频开始缓存");
        mTTAdNativeReward = ttAdManager.createAdNative(activity);
        //step4:创建广告请求参数AdSlot,具体参数含义参考文档
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(position)
                .setSupportDeepLink(true)
                .setImageAcceptedSize(1080, 1920)
                .setRewardName("金币") //奖励的名称
                .setRewardAmount(3)  //奖励的数量
                .setUserID("user123")//用户id,必传参数
                .setMediaExtra("media_extra") //附加参数，可选
                .setOrientation(TTAdConstant.HORIZONTAL) //必填参数，期望视频的播放方向：TTAdConstant.HORIZONTAL 或 TTAdConstant.VERTICAL
                .build();
        //step5:请求广告
        mTTAdNativeReward.loadRewardVideoAd(adSlot, new TTAdNative.RewardVideoAdListener() {
            @Override
            public void onError(int code, String message) {
//             TToast.show(activity, "ssss"+" code:"+code);
                Log.i(TAG, "onError: "+code+" "+message);
            }

            //视频广告加载后，视频资源缓存到本地的回调，在此回调后，播放本地视频，流畅不阻塞。
            @Override
            public void onRewardVideoCached() {
                Log.i(TAG,"激励视频缓存成功");

            }

            //视频广告的素材加载完毕，比如视频url等，在此回调后，可以播放在线视频，网络不好可能出现加载缓冲，影响体验。
            @Override
            public void onRewardVideoAdLoad(TTRewardVideoAd ad) {

              //  mttRewardVideoAd = ad;
                ad.setRewardAdInteractionListener(new TTRewardVideoAd.RewardAdInteractionListener() {

                    @Override
                    public void onAdShow() {
                        loadRewardAd(position,activity);
                        loadSplasAdIfNeed("7b26b600e24b80ae4a1773d091199f2f");
                    }

                    @Override
                    public void onAdVideoBarClick() {

                    }

                    @Override
                    public void onAdClose() {

                    }

                    //视频播放完成回调
                    @Override
                    public void onVideoComplete() {
                        callbackUnity("onReward", "", "");
                    }

                    @Override
                    public void onVideoError() {

                    }

                    //视频播放完成后，奖励验证回调，rewardVerify：是否有效，rewardAmount：奖励梳理，rewardName：奖励名称
                    @Override
                    public void onRewardVerify(boolean rewardVerify, int rewardAmount, String rewardName) {

                    }

                    @Override
                    public void onSkippedVideo() {

                    }
                });

                if (add2Queue) {
                    Queue<TTRewardVideoAd> queue = mttRewardVideoAdQueue.get(position);
                    if(queue == null){
                        queue = new LinkedBlockingDeque<>();
                        mttRewardVideoAdQueue.put(position,queue);
                    }

                    queue.offer(ad);
                } else {
                    ad.showRewardVideoAd(activity,TTAdConstant.RitScenes.CUSTOMIZE_SCENES,"scenes_test");
                }





            }
        });
    }
    public void loadRewardAd(final String position,final Activity activity) {
        loadRewardAd(position, activity, true);
    }

    public void fullRewardAd(final Activity activity,final String position){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                WLData wldata = WLInitialization.instance().getWLData();
                if (wldata != null && !wldata.isDisplayAd(position)) {

                    return;
                }
                String codeId = WLInitialization.instance().getFullscreenAdId(position);
                Queue<TTFullScreenVideoAd> queue = mttFullVideoAdQueue.get(codeId);

                if (queue != null && queue.size() > 0) {
                    TTFullScreenVideoAd ttad = queue.poll();;
                    ttad.showFullScreenVideoAd(activity,TTAdConstant.RitScenes.CUSTOMIZE_SCENES,"scenes_test");
                } else {
                    loadFullRewardAd(codeId, activity, false);
                }




            }
        });
    }

    public void loadFullRewardAd(final String position,final Activity activity,boolean add2Queue) {
        //step4:创建广告请求参数AdSlot,具体参数含义参考文档
        TTAdManager ttAdManager = TTAdManagerHolder.get();
        mTTAdNativeFullReward = ttAdManager.createAdNative(activity);
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(position)
                .setSupportDeepLink(true)
                .setImageAcceptedSize(1080, 1920)
                .setOrientation(TTAdConstant.HORIZONTAL)//必填参数，期望视频的播放方向：TTAdConstant.HORIZONTAL 或 TTAdConstant.VERTICAL
                .build();
        //step5:请求广告
        mTTAdNativeFullReward.loadFullScreenVideoAd(adSlot, new TTAdNative.FullScreenVideoAdListener() {
            @Override
            public void onError(int code, String message) {
                Log.i(TAG, "onError: "+code+" "+message);
            }

            @Override
            public void onFullScreenVideoAdLoad(TTFullScreenVideoAd ad) {

                mttFullVideoAd = ad;
                mttFullVideoAd.setFullScreenVideoAdInteractionListener(new TTFullScreenVideoAd.FullScreenVideoAdInteractionListener() {

                    @Override
                    public void onAdShow() {
                        loadFullRewardAd(position,activity);
                        loadSplasAdIfNeed("7b26b600e24b80ae4a1773d091199f2f");
                    }

                    @Override
                    public void onAdVideoBarClick() {

                    }

                    @Override
                    public void onAdClose() {


                    }

                    @Override
                    public void onVideoComplete() {

                    }

                    @Override
                    public void onSkippedVideo() {

                    }
                });
                if (add2Queue) {
                    Queue<TTFullScreenVideoAd> queue = mttFullVideoAdQueue.get(position);
                    if(queue == null){
                        queue = new LinkedBlockingDeque<>();
                        mttFullVideoAdQueue.put(position,queue);
                    }
                    queue.offer(ad);
                } else {
                    ad.showFullScreenVideoAd(activity,TTAdConstant.RitScenes.CUSTOMIZE_SCENES,"scenes_test");
                }

            }

            @Override
            public void onFullScreenVideoCached() {

            }
        });
    }

    public void loadFullRewardAd(final String position,final Activity activity) {
        loadFullRewardAd(position, activity, true);
    }

    /**
     * 加载开屏广告
     */
    public void loadSplashAd(FrameLayout container, OnWLSplashAdLoadedListener loadedListener) {
        if (mSplashContainer == null) {
            mSplashContainer = container;
        }

        TTAdNative ttAdNative = TTAdManagerHolder.get().createAdNative(container.getContext());
        //step3:创建开屏广告请求参数AdSlot,具体参数含义参考文档
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(WLInitialization.instance().getSplashIni().get(0).getSdkPosition())
                .setSupportDeepLink(true)
                .setImageAcceptedSize(1080, 1920)
                .build();
        //step4:请求广告，调用开屏广告异步请求接口，对请求回调的广告作渲染处理
        ttAdNative.loadSplashAd(adSlot, new TTAdNative.SplashAdListener() {
            @Override
            @MainThread
            public void onError(int code, String message) {
                WLLogUtils.d(message);
            }

            @Override
            @MainThread
            public void onTimeout() {
                WLLogUtils.d("load splash timeout");
            }

            @Override
            @MainThread
            public void onSplashAdLoad(TTSplashAd ad) {
                if (ad == null) {
                    return;
                }

                if (loadedListener != null) {
                    loadedListener.onSplashAdLoad(ad);
                }

                //设置SplashView的交互监听器
                ad.setSplashInteractionListener(new TTSplashAd.AdInteractionListener() {
                    @Override
                    public void onAdClicked(View view, int type) {


                    }

                    @Override
                    public void onAdShow(View view, int type) {

                    }

                    @Override
                    public void onAdSkip() {
                        mSplashContainer.removeAllViews();
                        //把SplashView 添加到ViewGroup中,注意开屏广告view：width >=70%屏幕宽；height >=50%屏幕宽
                    }

                    @Override
                    public void onAdTimeOver() {
                        mSplashContainer.removeAllViews();
                        //把SplashView 添加到ViewGroup中,注意开屏广告view：width >=70%屏幕宽；height >=50%屏幕宽
                    }
                });
            }
        }, AD_TIME_OUT);
    }

    public void loadSplasAdIfNeed(String position1) {

        WLData wldata = WLInitialization.instance().getWLData();
        String position = WLInitialization.instance().getSplashIni().get(0).getSdkPosition();
        //wldata为空时，可能是网络或我们服务器存在问题，所以当wldata为空时，这里需要仍然需要加下控制，不显示广告
        if (wldata == null || !wldata.isDisplayAd(position1)) {
            return;
        }
        if (mSplashContainer != null) {
            loadSplashAd(mSplashContainer, new OnWLSplashAdLoadedListener() {
                @Override
                public void onSplashAdLoad(TTSplashAd ad) {
                    //获取SplashView
                    View view = ad.getSplashView();
                    if (view != null) {
                        mSplashContainer.removeAllViews();
                        //把SplashView 添加到ViewGroup中,注意开屏广告view：width >=70%屏幕宽；height >=50%屏幕宽
                        mSplashContainer.addView(view);
                        //设置不开启开屏广告倒计时功能以及不显示跳过按钮,如果这么设置，您需要自定义倒计时逻辑
                        //ad.setNotAllowSdkCountdown();
                    }
                }
            });
        }
    }

    public interface OnWLSplashAdLoadedListener {
        void onSplashAdLoad(TTSplashAd ad);
    }

    public void exitGame(Activity activity) {

    }



}