package com.aktt.news.net.config;

import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by magical on 17/8/18.
 * Description : 日志拦截器
 */

public class LoggingInterceptor implements Interceptor {

  @Override
  public Response intercept(Chain chain) throws IOException {

    Request request = chain.request();
    long t1 = System.nanoTime();

    Response response = chain.proceed(request);
    long t2 = System.nanoTime();

    if (request.url() != null) {
    }

    return response;
  }
}
