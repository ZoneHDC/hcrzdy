package com.iwolong.ads.unity;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;


import com.iwolong.ads.WLSDKManager;
import com.iwolong.ads.utils.LogUtils;
import com.unity3d.player.UnityPlayer;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class PolyProxy {
    public static WeakReference<Activity> sActivity;
    public static final int REQUEST_PERMISSIONS_CODE = 100;
//
   private final static String sGameObjectName="GameSDKLoad";
   private final static String sGameFunctionName="AndroidCallUnity";
    private final static String sGameFunctionName2="CloseBannerCall";
//     private final static String sGameObjectName="ADManager";
//   private final static String sGameFunctionName="AndroidCallUnity";
    private static Method sUnitySendMessageMethod;
    private static String sParams;
    private static UnityPlayer sUnityPlayer ;
    public static View container1;
    public static  View container2;
    private static PolyProxy sManager = new PolyProxy();



    private static List<String> mNeedRequestPMSList = new ArrayList<>();

    public static PolyProxy instance() {
        return sManager;
    }

    public static Activity getActivity() {
        return sActivity == null ? null : sActivity.get();
    }


    public static void initSDK(String gameObject, String function, String appid) {
//        sGameObjectName = gameObject;
//        sGameFunctionName = function;
        sParams = appid;
    }


    public static void setsActivity(Activity activity) {
        sActivity = new WeakReference<>(activity);
    }

    public static Object getUnityPlayer() {
        Activity activity = getActivity();
        if (activity != null) {
            try {
                Class<?> clazz = activity.getClass().getSuperclass();
                Field unityPlayerField = clazz.getDeclaredField("mUnityPlayer");
                unityPlayerField.setAccessible(true);
                Object unityPlayerObj = unityPlayerField.get(activity);
                return unityPlayerObj;
            } catch (Exception e) {
                LogUtils.e("", e);
            }
        }

        return null;
    }

    private static Method getUnitySendMessageMethod() throws Exception {
        Class<?> clazz = Class.forName("com.unity3d.player.UnityPlayer");
        return clazz.getDeclaredMethod("UnitySendMessage", new Class<?>[] {String.class, String.class, String.class});
    }

    public static void showBannerAd(String positionId) {
        ViewGroup container = (ViewGroup)getUnityPlayer();
        WLSDKManager.instance().showBanner(sActivity.get(), (ViewGroup) container1, positionId);
    }

    public static void showInterstitialAd(String positionId) {
        WLSDKManager.instance().showInterstitialAd(sActivity.get(),(ViewGroup) container1, positionId);
    }

    public static void showRewardAd(String positionId) {
        WLSDKManager.instance().showRewardAd(sActivity.get(), positionId);
    }

    public  static  void fullRewardAd(String positonId){
       WLSDKManager.instance().fullRewardAd(sActivity.get(),positonId);
    }

    public  static  void SpalshAd(String postionId){
        WLSDKManager.instance().loadSplasAdIfNeed(postionId);
    }


    public static void destroy() {

    }

    public static void callbackUnity(final String function, final String message, final String position) {
        callback.unitySendMessage(function, message, position);
    }
    public static void callbackUnity2(final String function, final String message, final String position) {
        callback.unitySendMessage2(function, message, position);
    }
    private static PolyProxyCallback callback = new PolyProxyCallback() {
        @Override
        public void unitySendMessage(String function, String message, String positonId) {
            LogUtils.e("func:" + function + " msg:" + message + " id:" + positonId) ;
            try {
                JSONObject jobj = new JSONObject();
                jobj.put("function", function);
                jobj.put("message", message);
                jobj.put("position", positonId);
                Class<?> clazz = Class.forName("com.unity3d.player.UnityPlayer");
                Method md = clazz.getDeclaredMethod("UnitySendMessage", new Class<?>[] {String.class, String.class, String.class});
                md.invoke(clazz, sGameObjectName, sGameFunctionName, jobj.toString());
            } catch (Exception e) {
                LogUtils.e("", e);
            }
        }
        @Override
        public void unitySendMessage2(String function, String message, String positonId) {

            try {
                JSONObject jobj = new JSONObject();
                jobj.put("function", function);
                jobj.put("message", message);
                jobj.put("position", positonId);
                Class<?> clazz = Class.forName("com.unity3d.player.UnityPlayer");
                Method md = clazz.getDeclaredMethod("UnitySendMessage", new Class<?>[] {String.class, String.class, String.class});
                md.invoke(clazz, sGameObjectName, sGameFunctionName2, jobj.toString());
            } catch (Exception e) {

            }
        }

    };

    public static void initGameCentre( ) {

    }

    public static void login() {

    }

    public static void exitGame() {
           WLSDKManager.instance().exitGame(sActivity.get());
    }

}
