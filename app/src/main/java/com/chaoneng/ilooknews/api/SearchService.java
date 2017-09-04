package com.chaoneng.ilooknews.api;

import com.chaoneng.ilooknews.net.data.HttpResult;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by magical on 17/8/28.
 * Description : 搜索相关api
 */

public interface SearchService {

  /**
   * 搜索
   */
  @GET("doSearch")
  Call<HttpResult<String>> doSearch(@Query("userid") String userId, @Query("keyText") String key);

  /**
   * 获取搜索历史
   */
  @GET("beforeSearch")
  Call<HttpResult<String>> getSearchHistory(@Query("userid") String userId);
}
