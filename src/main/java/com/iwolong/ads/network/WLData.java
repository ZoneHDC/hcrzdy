package com.iwolong.ads.network;

import com.google.gson.annotations.SerializedName;
import com.iwolong.ads.utils.WLTools;

import java.io.Serializable;
import java.util.List;

public class WLData implements Serializable {
    @SerializedName("package_name")
    private String packageName;
    @SerializedName("is_online")
    private int isOnline;
    @SerializedName("positions")
    private List<WLAdPosition> adPositionList;

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public int getIsOnline() {
        return isOnline;
    }

    public void setIsOnline(int isOnline) {
        this.isOnline = isOnline;
    }

    public List<WLAdPosition> getAdPositionList() {
        return adPositionList;
    }

    public void setAdPositionList(List<WLAdPosition> adPositionList) {
        this.adPositionList = adPositionList;
    }
    
    public WLAdPosition getAdPostionById(String positionId) {
        WLAdPosition p = null;
        
        if (this.adPositionList != null && this.adPositionList.size() > 0) {
            for (WLAdPosition adPosition:
                 this.adPositionList) {
                if (positionId.equals(adPosition.getPositionId())) {
                    p = adPosition;
                    break;
                }
            }    
        }

        return p;
    }


    public boolean isDisplayAd(String positionId) {
        WLAdPosition adPosition = getAdPostionById(positionId);
        boolean isDisplay = false;

        if (adPosition != null && WLTools.randomBooleanByProbability((int)(adPosition.getProbability() * 100))) {
            isDisplay = true;
        }

        return isDisplay;
    }
}
