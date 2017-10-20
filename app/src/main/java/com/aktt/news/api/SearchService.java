package com.aktt.news.api;

import com.aktt.news.module.home.data.NewsListWrapper;
import com.aktt.news.module.search.data.SearchHistory;
import com.aktt.news.module.search.data.SearchRecommend;
import com.aktt.news.net.data.HttpResult;
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
    Call<HttpResult<NewsListWrapper>> doSearch(@Query("userid") String userId,
            @Query("keyText") String key);

    /**
     * 获取搜索历史
     */
    @GET("beforeSearch")
    Call<HttpResult<SearchHistory>> getSearchHistory(@Query("userid") String userId);

    /**
     * 获取搜索推荐
     */
    @GET("getSearchKeyWord")
    Call<HttpResult<SearchRecommend>> getSearchRecommend();
}
