package com.chaoneng.ilooknews.net.client;

import com.chaoneng.ilooknews.ILookApplication;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import java.io.File;
import java.util.concurrent.TimeUnit;
import okhttp3.Cache;
import okhttp3.OkHttpClient;

/**
 * Created by magical on 17/8/17.
 * Description : 构造 OkHttpClient
 */

public enum OKHttpFactory {

  INSTANCE;

  private final OkHttpClient okHttpClient;

  private static final int TIMEOUT_READ = 15;
  private static final int TIMEOUT_WRITE = 10;
  private static final int TIMEOUT_CONNECTION = 10;

  OKHttpFactory() {

    //缓存目录
    Cache cache =
        new Cache(new File(ILookApplication.getAppContext().getExternalCacheDir(), "http_cache"),
            100 * 1024 * 1024);

    okHttpClient = new OkHttpClient.Builder().cache(cache)
        .readTimeout(TIMEOUT_READ, TimeUnit.SECONDS)
        .writeTimeout(TIMEOUT_WRITE, TimeUnit.SECONDS)
        .connectTimeout(TIMEOUT_CONNECTION, TimeUnit.SECONDS)
        //.addInterceptor(new CacheInterceptor())
        //.addInterceptor(new LoggingInterceptor())
        .addNetworkInterceptor(new StethoInterceptor())
        //.cookieJar(new CookieManager())
        //.authenticator(new AuthenticatorManager())
        //失败重连
        .retryOnConnectionFailure(true)
        .build();

    //添加UA
    //.addInterceptor(new UserAgentInterceptor(HttpHelper.getUserAgent()))
  }

  public OkHttpClient getOkHttpClient() {
    return okHttpClient;
  }
}
