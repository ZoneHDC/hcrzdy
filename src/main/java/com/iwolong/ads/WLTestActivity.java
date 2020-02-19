package com.iwolong.ads;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.iwolong.ads.utils.WLInitialization;
import com.iwolong.ads.utils.WLTools;

public class WLTestActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showDlg();
    }

    private void showDlg() {
        //TODO 显示广告参数
        StringBuilder sb = new StringBuilder();
        String packageName = WLTools.getPakcageName(this);
        String udid = WLTools.getDeviceId(this);
        String versionName = WLTools.getAppVersion(this);
        String channelName = WLTools.getTdChannel(this);
        sb.append("appkey:").append(WLInitialization.instance().getAdAppId()).append("\n");
        sb.append("channel:").append(channelName).append("\n");
        sb.append("tdid:").append(WLTools.getTdid(this)).append("\n");
        sb.append("tdchannel:").append(channelName).append("\n");
        sb.append("SPLASH_AD_ID:").append(WLInitialization.instance().getSplashIni().get(0).getSdkPosition()).append("\n");
        sb.append("Banner_ID:").append(WLInitialization.instance().getBannerAdId()).append("\n");
        sb.append("INTERSTITIAL_ID:").append(WLInitialization.instance().getInterstitialIni().get(0).getSdkPosition()).append("\n");
        sb.append("REWARFVIDEO_AD:").append(WLInitialization.instance().getRewardIni().get(0).getSdkPosition()).append("\n");
        sb.append("FULLVIDEO_AD:").append(WLInitialization.instance().getFullscreenIni().get(0).getSdkPosition()).append("\n");
        sb.append("app_version").append(versionName).append("\n");
        sb.append("package_name").append(packageName).append("\n");
        AlertDialog dlg = new AlertDialog.Builder(this).setMessage(sb).create();
        dlg.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss (DialogInterface dialog) {
                finish();
            }
        });
        dlg.show();
    }
}
