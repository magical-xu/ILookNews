package com.chaoneng.ilooknews.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Created by magical on 17/8/17.
 * Description : for test
 */

public interface GankService {

  @GET()
  Call<GankModel> getData(@Url() String url);
}
