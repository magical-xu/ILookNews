package com.chaoneng.ilooknews.net.client;

import android.content.Context;
import com.chaoneng.ilooknews.net.config.CookieManager;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import okhttp3.OkHttpClient;
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

    private Map<String, Map<Integer, retrofit2.Call>> mRequestMap = new ConcurrentHashMap<>();

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
}
