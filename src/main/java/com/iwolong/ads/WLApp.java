package com.iwolong.ads;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import android.support.multidex.MultiDexApplication;
import android.util.Log;


import com.iwolong.ads.unity.PolyProxy;
import com.iwolong.ads.utils.WLInitialization;
import com.meizu.ads.AdSDK;

import java.lang.ref.WeakReference;

public class WLApp extends MultiDexApplication {
    private WeakReference<Activity> mCurrentActivity;
    public static String PROCESS_NAME_XXXX = "process_name_xxxx";
    private static final String TAG = "MyApplication";


    @Override
    public void onCreate() {
        super.onCreate();
        //请求响应权限
        try {
            WLInitialization.instance().parse(this);
        } catch (Exception e) {

        }
        //穿山甲sdk初始化
//        TTAdManagerHolder.init(this);
//        TTAdManagerHolder.get().requestPermissionIfNecessary(this);
        //魅族sdk初始化
        AdSDK.init(this,Constants.APP_KEY);
//        StringBuilder buildSB = new StringBuilder();
//        buildSB.append(Build.BRAND).append("/");    // xiaomi
//        buildSB.append(Build.PRODUCT).append("/");  // kenzo
//        buildSB.append(Build.MODEL).append("/");    // redmi note 33
//        buildSB.append(Build.CPU_ABI).append("/");  // arm64-v8a
//        Log.e("AdSDK", "BuildInfo:\n" + buildSB.toString());

        //检查权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
                @Override
                public void onActivityCreated (Activity activity, Bundle bundle) {
                    //do nothing
                }

                @Override
                public void onActivityStarted (Activity activity) {
                    //do nothing
                }

                @Override
                public void onActivityResumed (Activity activity) {
                    mCurrentActivity = new WeakReference<>(activity);
                    String activityName = activity.getClass().getSimpleName();
                    if ("MainActivity".equals(activityName)) {
                        PolyProxy.setsActivity(activity);
                    }
                }

                @Override
                public void onActivityPaused (Activity activity) {
                    //do nothing
                }

                @Override
                public void onActivityStopped (Activity activity) {
                    //do nothing
                }

                @Override
                public void onActivitySaveInstanceState (Activity activity, Bundle bundle) {
                    //do nothing
                }

                @Override
                public void onActivityDestroyed (Activity activity) {
                    //do nothing
                }
            });
        }
    }
}
