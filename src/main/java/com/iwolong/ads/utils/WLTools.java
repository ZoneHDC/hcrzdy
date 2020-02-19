package com.iwolong.ads.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;

import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;



import java.lang.reflect.Method;
import java.util.Random;

public class WLTools {
    //没有网络连接
    public static final String NETWORN_NONE = "none";
    //wifi连接
    public static final String NETWORN_WIFI = "wifi";
    public static final String UnCon_WIFI = "unconnect";
    //手机网络数据连接类型
    public static final String NETWORN_2G = "2g";
    public static final String NETWORN_3G = "3g";
    public static final String NETWORN_4G = "4g";
    public static final String NETWORN_MOBILE = "mobile";
    public static final String NETWORN_ETHERNET = "ehternet";

    public static String getPakcageName(Context context) {
        return context.getPackageName();
    }

    public static String getAppId(Context context) {
//        String appid = "";
//
//        try {
//            ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
//            appid = appInfo.metaData.getString("cw_appid", "cw");
//            appid = appid.substring(2);
//        } catch (Exception e) {}

        return WLInitialization.instance().getCWAppId();
    }

    public static String getChannel(Context context) {
//        String channel = "";
//        try {
//            ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
//            channel = appInfo.metaData.getString("cw_channel", "cw");
//            channel = channel.substring(2);
//        } catch (Exception e) {}

        return WLInitialization.instance().getCWChannel();
    }

    public  static  String getTdChannel(Context context){
//        String channel = "";
//
//        try {
//            ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
//            channel = appInfo.metaData.getString("td_channel");
//        }catch (Exception e) {}

        return WLInitialization.instance().getChannel();
    }

    public  static  String getTdid(Context context) {
//        String appid = "";
//        try {
//            ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
//            appid = appInfo.metaData.getString("td_id");
//        } catch (Exception e) {
//
//        }
        return WLInitialization.instance().getTalkingDataAppId();
    }
    public static String getDeviceId(Context context) {
        String did = "000000000000000";
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            did = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
        }
        return did;
    }

    public static String getSerialNo() {
        String serial = "12345678";
        try {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {//8.0+
                serial = Build.SERIAL;
            } else {//8.0-
                Class<?> c = Class.forName("android.os.SystemProperties");
                Method get = c.getMethod("get", String.class);
                serial = (String) get.invoke(c, "ro.serialno");
            }
        } catch (Exception e) {
            e.printStackTrace();
            WLLogUtils.e("读取设备序列号异常：" + e.toString());
        }
        return serial;
    }

    public static String getMac(Context context) {
        try {
            WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

            if (wifiMgr != null) {
                WifiInfo info = wifiMgr.getConnectionInfo();
                if (info != null) {
                    return info.getMacAddress();
                }
            }
        } catch (Exception e) {
            WLLogUtils.e("", e);
        }
        return "00:00:00:00:00:00";
    }

    public static String getDeviceModel() {
        return Build.MODEL;
    }

    public static int getApiLevel() {
        return Build.VERSION.SDK_INT;
    }

    public static String getOsVersion() {
        return Build.VERSION.RELEASE;
    }

    public static String getScreen(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.widthPixels + "_" + dm.heightPixels;
    }

    public static String getNetworkState(Context context) {
        //获取系统的网络服务
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        //如果当前没有网络
        if (null == connManager)
            return NETWORN_NONE;
        //获取当前网络类型，如果为空，返回无网络
        NetworkInfo activeNetInfo = connManager.getActiveNetworkInfo();
        if (activeNetInfo == null || !activeNetInfo.isAvailable()) {
            return NETWORN_NONE;
        }
        // 判断是不是连接的是不是wifi
        NetworkInfo wifiInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (null != wifiInfo) {
            NetworkInfo.State state = wifiInfo.getState();
            if (null != state)
                if (state == NetworkInfo.State.CONNECTED
                        || state == NetworkInfo.State.CONNECTING) {
                    return NETWORN_WIFI;
                } else if (state == NetworkInfo.State.DISCONNECTED) {
                    return UnCon_WIFI;
                }
        }
        // 如果不是wifi，则判断当前连接的是运营商的哪种网络2g、3g、4g等
        NetworkInfo networkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (null != networkInfo) {
            NetworkInfo.State state = networkInfo.getState();
            String strSubTypeName = networkInfo.getSubtypeName();
            if (null != state)
                if (state == NetworkInfo.State.CONNECTED
                        || state == NetworkInfo.State.CONNECTING) {
                    switch (activeNetInfo.getSubtype()) {
                        //如果是2g类型
                        case TelephonyManager.NETWORK_TYPE_GPRS: // 联通2g
                        case TelephonyManager.NETWORK_TYPE_CDMA: // 电信2g
                        case TelephonyManager.NETWORK_TYPE_EDGE: // 移动2g
                        case TelephonyManager.NETWORK_TYPE_1xRTT:
                        case TelephonyManager.NETWORK_TYPE_IDEN:
                            return NETWORN_2G;
                        //如果是3g类型
                        case TelephonyManager.NETWORK_TYPE_EVDO_A: // 电信3g
                        case TelephonyManager.NETWORK_TYPE_UMTS:
                        case TelephonyManager.NETWORK_TYPE_EVDO_0:
                        case TelephonyManager.NETWORK_TYPE_HSDPA:
                        case TelephonyManager.NETWORK_TYPE_HSUPA:
                        case TelephonyManager.NETWORK_TYPE_HSPA:
                        case TelephonyManager.NETWORK_TYPE_EVDO_B:
                        case TelephonyManager.NETWORK_TYPE_EHRPD:
                        case TelephonyManager.NETWORK_TYPE_HSPAP:
                            return NETWORN_3G;
                        //如果是4g类型
                        case TelephonyManager.NETWORK_TYPE_LTE:
                            return NETWORN_4G;
                        default:
                            //中国移动 联通 电信 三种3G制式
                            if (strSubTypeName.equalsIgnoreCase("TD-SCDMA")
                                    || strSubTypeName.equalsIgnoreCase("WCDMA")
                                    || strSubTypeName.equalsIgnoreCase("CDMA2000")) {
                                return NETWORN_3G;
                            } else {
                                return NETWORN_MOBILE;
                            }
                    }
                }
        }
        NetworkInfo EthernetInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);
        if (null != EthernetInfo) {
            NetworkInfo.State state = EthernetInfo.getState();
            if (null != state)
                if (state == NetworkInfo.State.CONNECTED || state == NetworkInfo.State.CONNECTING) {
                    return NETWORN_ETHERNET;
                }
        }
        return NETWORN_NONE;
    }

    public static String getAppVersion(Context context) {
        try {
            PackageInfo pkgInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_CONFIGURATIONS);
            return pkgInfo.versionName;
        }catch (Exception e) {}

        return "1.0";
    }

    public static int getAppVersionCode(Context context) {
        try {
            PackageInfo pkgInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_CONFIGURATIONS);
            return pkgInfo.versionCode;
        }catch (Exception e) {}

        return 1;
    }

    public static boolean randomBooleanByProbability(int probability) {
        Random random = new Random();
        int i = random.nextInt(99);
        if (i > 0 && i < probability) {
            return true;
        } else {
            return false;
        }
    }
}
