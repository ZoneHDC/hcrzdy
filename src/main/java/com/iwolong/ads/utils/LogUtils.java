package com.iwolong.ads.utils;

import android.util.Log;

public class LogUtils {
    private static final String TAG = "iwolong";
    private static boolean sLoggable = false;

    public static void i(String msg) {
        if (sLoggable) {
            Log.i(TAG, msg);
        }
    }

    public static void w(String msg) {
        if (sLoggable) {
            Log.w(TAG, msg);
        }
    }

    public static void d(String msg) {
        if (sLoggable) {
            Log.d(TAG, msg);
        }
    }

    public static void e(String msg) {
        if (sLoggable) {
            Log.e(TAG, msg);
        }
    }

    public static void v(String msg) {
        if (sLoggable) {
            Log.v(TAG, msg);
        }
    }


    public static void i(String msg, Throwable e) {
        if (sLoggable) {
            Log.i(TAG, msg, e);
        }
    }

    public static void w(String msg, Throwable e) {
        if (sLoggable) {
            Log.w(TAG, msg, e);
        }
    }

    public static void d(String msg, Throwable e) {
        if (sLoggable) {
            Log.d(TAG, msg, e);
        }
    }

    public static void e(String msg, Throwable e) {
        if (sLoggable) {
            Log.e(TAG, msg, e);
        }
    }

    public static void v(String msg, Throwable e) {
        if (sLoggable) {
            Log.v(TAG, msg, e);
        }
    }
}
