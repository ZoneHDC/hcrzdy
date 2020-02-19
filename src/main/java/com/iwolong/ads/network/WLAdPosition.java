package com.iwolong.ads.network;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class WLAdPosition implements Serializable {
    @SerializedName("position_id")
    private String positionId;
    @SerializedName("probability")
    private float probability;
    @SerializedName("ad_interval")
    private int adInterval;
    @SerializedName("day_limits")
    private int dayLimits;

    public String getPositionId() {
        return positionId;
    }

    public void setPositionId(String positionId) {
        this.positionId = positionId;
    }

    public float getProbability() {
        return probability;
    }

    public void setProbability(float probability) {
        this.probability = probability;
    }

    public int getAdInterval() {
        return adInterval;
    }

    public void setAdInterval(int adInterval) {
        this.adInterval = adInterval;
    }

    public int getDayLimits() {
        return dayLimits;
    }

    public void setDayLimits(int dayLimits) {
        this.dayLimits = dayLimits;
    }
}
