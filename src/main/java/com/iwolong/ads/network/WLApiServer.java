package com.iwolong.ads.network;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface WLApiServer {
    @POST(WLConstants.WL_GET_CONFIG)
    @FormUrlEncoded
    @Headers("Content-Type:application/x-www-form-urlencoded")
    Call<WLConfigInfo> getConfig(@FieldMap Map<String, String> params);
}
