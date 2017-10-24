package com.aktt.news.net.client;

import android.content.Context;
import android.support.annotation.Nullable;
import com.aktt.news.net.config.CookieManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by magical on 17/8/18.
 * Description :
 */

public class NetRequest {

    public static final String TAG = "NetRequest";

    private static NetRequest INSTANCE = new NetRequest();

    public static NetRequest getInstance() {
        return INSTANCE;
    }

    private HashMap<String, ArrayList<Call>> mRequestMap = new HashMap<>();

    public Map<String, ArrayList<Call>> getRequestMap() {
        return mRequestMap;
    }

    public Context mContext;

    private Retrofit mRetrofit;

    private OkHttpClient mOkHttpClient;

    private NetRequest() {
    }

    /**
     * 初始化Retrofit
     */
    public NetRequest init(Context context, String baseURL) {

        this.mContext = context;
        synchronized (NetRequest.this) {
            mOkHttpClient = OKHttpFactory.INSTANCE.getOkHttpClient();
            mRetrofit = new Retrofit.Builder().baseUrl(baseURL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(mOkHttpClient)
                    .build();
        }
        return this;
    }

    public <T> T create(Class<T> tClass) {
        return mRetrofit.create(tClass);
    }

    public void clearCookie() {
        ((CookieManager) mOkHttpClient.cookieJar()).clearCookie();
    }

    public void cancelByTag(String TAG) {

        ArrayList<Call> list = mRequestMap.get(TAG);
        if (null != list && list.size() > 0) {
            cancelPageCall(list);
            mRequestMap.remove(list);
        }
    }

    public void cancelPageCall(ArrayList<Call> list) {
        for (int i = 0; i < list.size(); i++) {

            Call call = list.get(i);
            if (!call.isCanceled()) {
                call.cancel();
            }
        }
    }

    public void addRequest(String TAG, Call call) {

        ArrayList<Call> list = mRequestMap.get(TAG);
        if (null != list) {
            list.add(call);
        }
    }

    public void removeRequest(String TAG, Call call) {

        ArrayList<Call> list = mRequestMap.get(TAG);
        if (null != list) {
            list.remove(call);
        }
    }

    public void addRequestList(String TAG, @Nullable ArrayList<Call> list) {

        if (null != list) {
            mRequestMap.put(TAG, list);
        }
    }
}
