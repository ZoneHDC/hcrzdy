package com.iwolong.ads.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.text.TextUtils;

import com.iwolong.ads.network.WLData;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class WLInitialization {
    private String mTalkingDataAppId;
    private String mChannel;
    private String mCwAppId;
    private String mCWChannel;
    private String mAdAppId;
    private List<WLIniBean> mSplashConfig;
    private List<WLIniBean> mInterstitialConfig;
    private WLIniBean mBannerConfig;
    private List<WLIniBean> mRewardConfig;
    private List<WLIniBean> mFullscreenConfig;
    private WLData mAdConfigurationFromServer;

    private static WLInitialization sInitialization = new WLInitialization();
    private String mFilename = "wlini";


    public static WLInitialization instance() {
        return sInitialization;
    }

    public WLInitialization parse(Context context) throws Exception {
        AssetManager am = context.getAssets();
        BufferedReader br = new BufferedReader(new InputStreamReader(am.open(mFilename)));
        String line;
        StringBuilder sb = new StringBuilder();

        while ((line = br.readLine()) != null) {
            sb.append(line);
        }

        JSONObject jobj = new JSONObject(sb.toString());
        mCwAppId = jobj.optString("cw_appid");
        mCWChannel = jobj.optString("cw_channel");
        mTalkingDataAppId = jobj.optString("td_appid");
        mChannel = jobj.optString("channel");
        mAdAppId = jobj.optString("ad_appid");
        mSplashConfig = getAdList(jobj.optJSONArray("splash_ad"));
        mInterstitialConfig = getAdList(jobj.optJSONArray("interstitial_ad"));
        mBannerConfig = getAdMap(jobj.optJSONObject("banner_ad"));
        mRewardConfig = getAdList(jobj.optJSONArray("reward_ad"));
        mFullscreenConfig = getAdList(jobj.optJSONArray("fullscreen_ad"));

        return this;
    }

    private WLIniBean getAdMap(JSONObject jobj) {
        WLIniBean data = new WLIniBean();

        if (jobj != null) {
            String cwPosition = jobj.optString("cw_position");
            String sdkPosition = jobj.optString("sdk_position");
            data.setSdkPosition(sdkPosition);
            data.setCwPosition(cwPosition);
        }

        return data;
    }

    private List<WLIniBean> getAdList(JSONArray ja) throws Exception {
        List<WLIniBean> list = new ArrayList<>();

        if (ja != null) {
            for (int i=0; i < ja.length(); ++i) {
                list.add(getAdMap(ja.getJSONObject(i)));
            }
        }

        return list;
    }

    public List<WLIniBean> getSplashIni() {
        return mSplashConfig;
    }

    public List<WLIniBean> getInterstitialIni() {
        return mInterstitialConfig;
    }

    public WLIniBean getBannerIni() {
        return mBannerConfig;
    }

    public List<WLIniBean> getRewardIni() {
        return mRewardConfig;
    }

    public List<WLIniBean> getFullscreenIni() {
        return mFullscreenConfig;
    }

    public String getCWAppId() {
        return mCwAppId;
    }

    public String getTalkingDataAppId() {
        return mTalkingDataAppId;
    }

    public String getChannel() {
        return mChannel;
    }

    public String getCWChannel() {
        return mCWChannel;
    }

    public String getAdAppId() {
        return mAdAppId;
    }

    public void setWLData(WLData data) {
        mAdConfigurationFromServer = data;
    }

    public WLData getWLData() {
        return mAdConfigurationFromServer;
    }

    public String getAdSDKSplashId(String position) {
        String pos = "";

        if (!TextUtils.isEmpty(position) && mSplashConfig != null && mSplashConfig.size() > 0) {
            for (WLIniBean ini : mSplashConfig) {
                if (position.equals(ini.getCwPosition())) {
                    pos = ini.getSdkPosition();
                    break;
                }
            }
        }
        return pos;
    }

    public String getBannerAdId() {
        String pos = "";

        if (mBannerConfig != null) {
            pos = mBannerConfig.getSdkPosition();
        }

        return pos;
    }

    public String getInterstitialAdId(String position) {
        String pos = "";

        if (!TextUtils.isEmpty(position) && mInterstitialConfig != null && mInterstitialConfig.size() > 0) {
            for (WLIniBean ini : mInterstitialConfig) {
                if (position.equals(ini.getCwPosition())) {
                    pos = ini.getSdkPosition();
                    break;
                }
            }
        }

        return pos;
    }

    public String getRewardAdId(String position) {
        String pos = "";

        if (!TextUtils.isEmpty(position) && mRewardConfig != null && mRewardConfig.size() > 0) {
            for (WLIniBean ini : mRewardConfig) {
                if (position.equals(ini.getCwPosition())) {
                    pos = ini.getSdkPosition();
                    break;
                }
            }
        }

        return pos;
    }


    public String getFullscreenAdId(String position) {
        String pos = "";

        if (!TextUtils.isEmpty(position) && mFullscreenConfig != null && mFullscreenConfig.size() > 0) {
            for (WLIniBean ini : mFullscreenConfig) {
                if (position.equals(ini.getCwPosition())) {
                    pos = ini.getSdkPosition();
                    break;
                }
            }
        }

        return pos;
    }

//    public String getADSDKBannerId() {
//        WLIniBean ini = getBannerIni();
//        if (ini != null) {
//            return ini.getSdkPosition();
//        } else {
//            return null;
//        }
//
//    }
}
