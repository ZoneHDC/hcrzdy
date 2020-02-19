package com.changwan.hcrzdy.sogou;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;

import com.bytedance.sdk.openadsdk.TTSplashAd;
import com.iwolong.ads.WLSDKManager;
import com.iwolong.ads.config.TTAdManagerHolder;
import com.iwolong.ads.network.WLConfigInfo;
import com.iwolong.ads.network.WLData;
import com.iwolong.ads.network.WLHttpManager;
import com.iwolong.ads.unity.PolyProxy;
import com.iwolong.ads.utils.WLInitialization;
import com.iwolong.ads.utils.WLTools;
import com.iwolong.ads.utils.WeakHandler;
import com.unity3d.player.UnityPlayerActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends UnityPlayerActivity implements WLSDKManager.OnWLSplashAdLoadedListener{


    private static final String TAG = "platform";
    private WeakHandler mHandler;
    private static List<String> mNeedRequestPMSList = new ArrayList<>(); //权限列表
    private FrameLayout mSplashContainer;
    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PolyProxy.instance().container1 = mUnityPlayer;
        mHandler = new WeakHandler(new WeakHandler.IHandler() {
            @Override
            public void handleMsg(Message msg) {

            }
        });
        View view = getLayoutInflater().inflate(R.layout.activity_splash, null);
        mSplashContainer = view.findViewById(R.id.splash_container);
        mUnityPlayer.addView(view);
        requestPermission();



        //请求响应权限


        //爱奇艺闪屏


       // requestConfig();
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        Log.i(TAG, "onBackPressed: ssssssss");


    }

    public void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            Window window = getWindow();
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
        //
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            /**
             * 如果你的targetSDKVersion >= 23，就要主动申请好权限。如果您的App没有适配到Android6.0（即targetSDKVersion < 23），那么只需要在这里直接回调unity，可以调用广告sdk。
             *
             */
            checkAndRequestPermissions();
        } else {
            // 如果是Android6.0以下的机器，默认在安装时获得了所有权限，可以直接调用SDK。
            initSDK();
            loadSplashAd();

        }
    }

    private void checkAndRequestPermissions() {
        /**
         * READ_PHONE_STATE、WRITE_EXTERNAL_STORAGE 两个权限是必须权限，没有这两个权限SDK无法正常获得广告。
         */
        if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)) {
            mNeedRequestPMSList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            mNeedRequestPMSList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        /**
         * WRITE_CALENDAR、ACCESS_FINE_LOCATION 是两个可选权限；没有不影响SDK获取广告；但是如果应用申请到该权限，会显著提升应用的广告收益。
         */
        if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR)) {
            mNeedRequestPMSList.add(Manifest.permission.WRITE_CALENDAR);
        }

        if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            mNeedRequestPMSList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        if (0 == mNeedRequestPMSList.size()) {
            /**
             * 权限都已经有了，那么直接回调unity，可以直接调用广告sdk。
             */
            initSDK();
            loadSplashAd();
            requestConfig();
        } else {

            //有权限需要申请，主动申请。
            String[] temp = new String[mNeedRequestPMSList.size()];
            mNeedRequestPMSList.toArray(temp);
            ActivityCompat.requestPermissions(this, temp, PolyProxy.REQUEST_PERMISSIONS_CODE);
        }
    }

    private void requestConfig() {
        String appid = WLTools.getAppId(this);
        String channel = WLTools.getChannel(this);
        String packageName = WLTools.getPakcageName(this);
        String udid = WLTools.getDeviceId(this);
        String sn = WLTools.getSerialNo();
        String mac = WLTools.getMac(this);
        String versionName = WLTools.getAppVersion(this);
        int versionCode = WLTools.getAppVersionCode(this);
        String deviceModel = WLTools.getDeviceModel();
        int av = WLTools.getApiLevel();
        String os = WLTools.getOsVersion();
        String screen = WLTools.getScreen(this);
        String network = WLTools.getNetworkState(this);

        Map<String, String> params = new HashMap<>();
        params.put("app_id", appid);
        params.put("package_name", packageName);
        params.put("chn", channel);
        params.put("udid", udid);
        params.put("sn", sn);
        params.put("mac", mac);
        params.put("app_version", versionName);
        params.put("vc", String.valueOf(versionCode));
        params.put("model", deviceModel);
        params.put("nt", network);
        params.put("screen", screen);
        params.put("os_version", os);
        params.put("av", String.valueOf(av));
        WLHttpManager.instance().requestConfig(params, new Callback<WLConfigInfo>() {

            @Override
            public void onResponse(Call<WLConfigInfo> call, Response<WLConfigInfo> response) {
                WLConfigInfo config = response.body();
                if (config.getCode() == 0 ) {
                    WLData data = config.getWlData();

                    if (data != null) {
                        WLInitialization.instance().setWLData(data);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
//                                loadSplashAd();
                            }
                        });
                    }

                }
            }

            @Override
            public void onFailure(Call<WLConfigInfo> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }


    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            /**
             *处理SDK申请权限的结果。
             */
            case PolyProxy.REQUEST_PERMISSIONS_CODE:
                if (hasNecessaryPMSGranted()) {
                    /**
                     * 应用已经获得SDK运行必须的READ_PHONE_STATE、WRITE_EXTERNAL_STORAGE两个权限，直接请求广告。
                     */
                    initSDK();
                    loadSplashAd();
                    requestConfig();
                } else {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.setData(Uri.parse("package:" + getPackageName()));
                    startActivity(intent);
                    finish();
                }

                break;
            default:
                break;
        }
    }

    private boolean hasNecessaryPMSGranted() {
        if (PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)) {
            if (PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                return true;
            }
        }
        return false;
    }

    private void initSDK() {
        //穿山甲sdk初始化
        TTAdManagerHolder.init(this);
        TTAdManagerHolder.get().requestPermissionIfNecessary(this);
        WLSDKManager.instance().loadInteractionAd(WLInitialization.instance().getInterstitialIni().get(0).getSdkPosition(), mUnityPlayer, MainActivity.this);
//        WLSDKManager.instance().loadBannerAd(MainActivity.this, mUnityPlayer,Constants.TT_SDK_AD_BANNNER_ID );
        WLSDKManager.instance().loadFullRewardAd(WLInitialization.instance().getFullscreenIni().get(0).getSdkPosition(), MainActivity.this);
        WLSDKManager.instance().loadRewardAd(WLInitialization.instance().getRewardIni().get(0).getSdkPosition(), MainActivity.this);
        //请求响应权限
    }
    private void loadSplashAd() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                WLSDKManager.instance().loadSplashAd(mSplashContainer, MainActivity.this);
            }
        }, 5 * 1000);



    }


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
}
