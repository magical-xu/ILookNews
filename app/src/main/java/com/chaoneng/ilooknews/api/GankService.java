package com.chaoneng.ilooknews.api;

import com.chaoneng.ilooknews.data.BaseUser;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by magical on 17/8/17.
 * Description : for test
 */

public interface GankService {

  @GET("api/data/Android/{limit}/{page}")
  Call<BaseUser> getData(@Path("limit") String limit, @Path("page") String page);
}
