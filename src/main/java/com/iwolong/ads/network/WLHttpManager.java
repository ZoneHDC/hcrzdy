package com.iwolong.ads.network;

import java.util.Map;



import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WLHttpManager {
    private WLApiServer mApiServer;
    private static WLHttpManager sInstance = new WLHttpManager();

    public static  WLHttpManager instance() {
        if (sInstance.mApiServer == null) {
            Retrofit retrofit = new Retrofit.Builder()  //创建Retrofit实例
                    .baseUrl(WLConstants.WL_HTTP_BASE_URL)    //url头部
                    .addConverterFactory(GsonConverterFactory.create()) //返回的数据经过转换工厂转换成我们想要的数据，最常用的就是Gson
                    .build();   //构建实例
            sInstance.mApiServer = retrofit.create(WLApiServer.class);   //使用retrofit的create()方法实现请求接口类
        }

        return  sInstance;
    }


    public void requestConfig(Map<String, String> params, Callback callback) {
        Call<WLConfigInfo> call = mApiServer.getConfig(params);
        call.enqueue(callback);
    }
}
