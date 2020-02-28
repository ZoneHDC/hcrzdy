package com.changwan.hcrzdy.sogou;

import android.Manifest;
import android.content.Context;
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
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.iwolong.ads.WLSDKManager;
import com.iwolong.ads.network.WLConfigInfo;
import com.iwolong.ads.network.WLData;
import com.iwolong.ads.network.WLHttpManager;
import com.iwolong.ads.unity.PolyProxy;
import com.iwolong.ads.utils.WLInitialization;
import com.iwolong.ads.utils.WLTools;
import com.iwolong.ads.utils.WeakHandler;
import com.meizu.ads.splash.SplashAd;
import com.meizu.ads.splash.SplashAdListener;
import com.unity3d.player.UnityPlayerActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends UnityPlayerActivity implements SplashAdListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private ViewGroup adContainer;
    public ViewGroup adContainer2;
    private static final int REQUEST_PERMISSIONS_CODE = 100;
    private static final String SKIP_TEXT = "跳过 | %d";
    private List<String> mNeedRequestPMSList = new ArrayList<>();
    private WeakHandler mHandler;
    private Context getContext() {
        return this;
    }
    private ViewGroup mAdContainer;
    private Button mSkipView;
    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new WeakHandler(new WeakHandler.IHandler() {
            @Override
            public void handleMsg(Message msg) {

            }
        });
//        View view = getLayoutInflater().inflate(R.layout.activity_splash_ad, null);
//        mAdContainer = view.findViewById(R.id.container);
//        mSkipView =view.findViewById(R.id.btn_skip);
//        mUnityPlayer.addView(view);
//        PolyProxy.instance().skipView=mSkipView;
//        PolyProxy.instance().mAdContainer=mAdContainer;
//        Log.e(TAG, "brand:" + android.os.Build.BRAND);
//        Log.e(TAG, "model:" + android.os.Build.MODEL);
//        Log.e(TAG, "version:" + android.os.Build.VERSION.RELEASE);
//        Log.e(TAG, "build-id:" + android.os.Build.ID);
        // 国内版
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            //  如果你的targetSDKVersion >= 23，就要主动申请好权限。如果您的App没有适配到Android6.0（即targetSDKVersion < 23）
            //  那么只需要在这里直接调用fetchSplashAd方法。

            requestPermission();
        } else {
            /* 如果是Android6.0以下的机器，默认在安装时获得了所有权限，可以直接调用SDK。*/
            init();
            requestConfig();
        }
        //请求响应权限

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
            Log.i(TAG,"checkAndRequestPermissions");
            checkAndRequestPermissions();
        } else {
            // 如果是Android6.0以下的机器，默认在安装时获得了所有权限，可以直接调用SDK。
            init();
            requestConfig();
        }
    }

    private void loadSplashAd() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                fetchSplashAd();
            }
        }, 5 * 1000);

    }
    public void init(){
        Log.i(TAG,"init了");
        PolyProxy.instance().container1 = mUnityPlayer;
        PolyProxy.instance().container2 = adContainer2;
        ViewGroup vg=(ViewGroup) mUnityPlayer.getView();
        adContainer = (ViewGroup) View.inflate(MainActivity.this, R.layout.banner_layout, null);
        // LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(Math.round(getResources().getDisplayMetrics().density * 300), Math.round(getResources().getDisplayMetrics().density * 50));
        lp.gravity = Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL;
        vg.addView(adContainer,lp);
        adContainer2=findViewById(R.id.banner_container);
        WLSDKManager.instance().loadBannerAd(this,adContainer2 , WLInitialization.instance().getBannerAdId());
        WLSDKManager.instance().loadInteractionAd(WLInitialization.instance().getInterstitialIni().get(0).getSdkPosition(),mUnityPlayer,this);
        WLSDKManager.instance().loadRewardAd(WLInitialization.instance().getRewardIni().get(0).getSdkPosition(),this);
        WLSDKManager.instance().loadFullAd(WLInitialization.instance().getFullscreenIni().get(0).getSdkPosition(),mUnityPlayer,this);



    }
    @Override
    public void onAdLoaded() {

    }

    @Override
    public void onAdShow() {

    }

    @Override
    public void onTick(long millisUntilFinished) {
        Log.e("AdSDK", "onTick : " + millisUntilFinished);
        if (mSkipView != null) {
            mSkipView.setText(String.format(SKIP_TEXT, Math.round(millisUntilFinished / 1000L)));
            mSkipView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onAdClicked() {

    }

    @Override
    public void onAdClosed(int i) {
        mAdContainer.removeAllViews();
        mSkipView.setVisibility(View.GONE);
    }

    @Override
    public void onAdError(int i, String s) {
        mSkipView.setVisibility(View.GONE);
        mAdContainer.removeAllViews();
        if (!isFinishing()) {

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

        //
        if (0 == mNeedRequestPMSList.size()) {
            /**
             * 权限都已经有了，那么直接调用SDK请求广告。
             */
            Log.i(TAG,"权限都已经有了，那么直接调用SDK请求广告。");
            init();
            requestConfig();
        } else {
            /**
             * 有权限需要申请，主动申请。
             */

            String[] temp = new String[mNeedRequestPMSList.size()];
            mNeedRequestPMSList.toArray(temp);
            ActivityCompat.requestPermissions(this, temp, REQUEST_PERMISSIONS_CODE);
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
                Log.i(TAG,"onResponse");
                WLConfigInfo config = response.body();
                if (config.getCode() == 0 ) {
                    WLData data = config.getWlData();
                    WLInitialization.instance().setWLData(data);
                    //                    if (data != null && data.getIsOnline() == 0) {
                    //                        //TODO 显示广告参数
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run () {
                            Log.i(TAG,"requestConfig");
//                            TTAdManagerHolder.init(SplashActivity.this);
//                            TTAdManagerHolder.get().requestPermissionIfNecessary(SplashActivity.this);
                            loadSplashAd();
                        }
                    });

                }
            }

            @Override
            public void onFailure(Call<WLConfigInfo> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
    private SplashAd mSplashAd;
    private void fetchSplashAd() {
        // 确保每次退出应用后重新进入应用该代码可以被调用到


//        AdSDK.init(this, Constants.APP_KEY, new AdSDK.InitCallback() {
//            @Override
//            public void onSuccess() {
//                // Toast.makeText(SplashActivity.this, "SDK初始化成功!", Toast.LENGTH_LONG).show();
//                mSplashAd = new SplashAd(MainActivity.this,
//                        mAdContainer, mSkipView, WLInitialization.instance().getAdSDKSplashId(), MainActivity.this);
//            }
//
//            @Override
//            public void onError(int code, String message) {
//                mSkipView.setVisibility(View.GONE);
//                mAdContainer.removeAllViews();
//            }
//        });
        //next();
        Intent it = new Intent(this, SplashActivity.class);
        it.addCategory("android.intent.category.LAUNCHER");
        it.setAction("android.intent.action.MAIN");
        startActivity(it);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        /**
         *处理SDK申请权限的结果。
         */
        if (requestCode == REQUEST_PERMISSIONS_CODE) {
            if (hasNecessaryPMSGranted()) {
                /**
                 * 应用已经获得SDK运行必须的READ_PHONE_STATE、WRITE_EXTERNAL_STORAGE两个权限，直接请求广告。
                 */
                init();
                requestConfig();
            } else {
                /**
                 * 如果用户没有授权，那么应该说明意图，引导用户去设置里面授权。
                 */
                Toast.makeText(this, "应用缺少SDK运行必须的READ_PHONE_STATE、WRITE_EXTERNAL_STORAGE两个权限！请点击\"应用权限\"，打开所需要的权限。", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivity(intent);
                finish();
            }
        }
    }

    private boolean hasNecessaryPMSGranted() {
        if (PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)) {
            return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        return false;
    }

}
