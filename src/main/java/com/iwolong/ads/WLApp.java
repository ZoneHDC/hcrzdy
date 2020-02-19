package com.iwolong.ads;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.iwolong.ads.config.TTAdManagerHolder;
import com.iwolong.ads.unity.PolyProxy;
import com.iwolong.ads.utils.WLInitialization;

import java.lang.ref.WeakReference;

public class WLApp extends Application {
    private WeakReference<Activity> mCurrentActivity;
    public static String PROCESS_NAME_XXXX = "process_name_xxxx";

    @Override
    protected void attachBaseContext(Context base) {

        super.attachBaseContext(base);

    }

    @Override
    public void onCreate() {
        super.onCreate();

        try {
            WLInitialization.instance().parse(this);
        } catch (Exception e) {

        }

        //爱奇艺SDK初始化

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
