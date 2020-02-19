package com.iwolong.ads.network;


import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class WLConfigInfo implements Serializable {

    @SerializedName("code")
    private int code;
    @SerializedName("msg")
    private String msg;
    @SerializedName("data")
    private WLData wldata;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public WLData getWlData() {
        return wldata;
    }

    public void setWlData(WLData wldata) {
        this.wldata = wldata;
    }
}
