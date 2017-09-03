package com.chaoneng.ilooknews.api;

import com.chaoneng.ilooknews.data.TabBean;
import com.chaoneng.ilooknews.module.home.data.NewsDetailBean;
import com.chaoneng.ilooknews.module.home.data.NewsListWrapper;
import com.chaoneng.ilooknews.net.data.HttpResult;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by magical on 17/8/28.
 * Description : 首页接口
 */

public interface HomeService {

    int NEWS = 1;
    int VIDEO = 2;

    /**
     * 获取我的频道
     *
     * @param type 1 ：新闻 ， 2：视频
     */
    @GET("getMyChannel")
    Call<HttpResult<TabBean>> getChannel(@Query("userid") String userId, @Query("type") int type);

    @POST("addMyChannel")
    Call<HttpResult<String>> addMyChannel(@Query("userid") String userId, @Query("cid") String cid);

    @POST("deleteMyChannel")
    Call<HttpResult<String>> deleteMyChannel(@Query("userid") String userId,
            @Query("cid") String cid);

    /**
     * 获取新闻列表
     */
    @GET("getNewsByChannel")
    Call<HttpResult<NewsListWrapper>> getNewsList(@Query("userid") String userId,
            @Query("cId") String cid, @Query("page") int page, @Query("pageSize") int size);

    /**
     * 获取新闻详情
     */
    @GET("getNewsInfoByid")
    Call<HttpResult<NewsDetailBean>> getNewsDetail(@Query("userid") String userId,
            @Query("newsId") String newsId, @Query("newstype") int type);

    /**
     * 发表评论
     */
    @POST("addNewsComment")
    Call<HttpResult<String>> postNewsComment(@Query("userid") String userId,
            @Query("newsId") String newsId, @Query("newstype") int type,
            @Query("parentId") String parentId);

    /**
     * 获取评论
     */
    @GET("getNewsComment")
    Call<HttpResult<String>> getNewsComment(@Query("userid") String userId,
            @Query("newsId") String newsId, @Query("newstype") int type,
            @Query("parentId") String parentId, @Query("page") int page,
            @Query("pagesize") int size);

    /**
     * 删除评论
     */
    @POST("delNewsComment")
    Call<HttpResult<String>> deleteNewsComment(@Query("userid") String userId,
            @Query("parentId") String parentId, @Query("cid") String cId);

    /**
     * 收藏
     */
    @POST("addCollection")
    Call<HttpResult<String>> addCollection(@Query("userid") String userId, @Query("nid") String nid,
            @Query("newtype") int type);
}
