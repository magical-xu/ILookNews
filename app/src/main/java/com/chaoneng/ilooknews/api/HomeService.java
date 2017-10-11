package com.chaoneng.ilooknews.api;

import com.chaoneng.ilooknews.data.NewsInfoWrapper;
import com.chaoneng.ilooknews.data.TabBean;
import com.chaoneng.ilooknews.module.home.data.NewsListWrapper;
import com.chaoneng.ilooknews.net.data.HttpResult;
import java.util.Map;
import okhttp3.ResponseBody;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

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
    Call<HttpResult<TabBean>> getChannel(@Query("type") int type);

    @POST("addMyChannel")
    Call<HttpResult<String>> addMyChannel(@Query("userid") String userId, @Query("cid") String cid);

    @POST("deleteMyChannel")
    Call<HttpResult<String>> deleteMyChannel(@Query("userid") String userId,
            @Query("cid") String cid);

    /**
     * 获取新闻列表
     */
    @GET("getNewsByChannel")
    Call<HttpResult<NewsListWrapper>> getNewsList(@Query("cId") String cid, @Query("page") int page,
            @Query("pageSize") int size);

    /**
     * 获取新闻详情
     */
    @GET("getNewsInfoByid")
    Call<HttpResult<NewsInfoWrapper>> getNewsDetail(@Query("newsId") String newsId,
            @Query("newstype") int type);

    /**
     * 发表评论
     *
     * @param parentId 默认为0， =0时评论的是新闻 其他值评论的是父类评论
     */
    @POST("addNewsComment")
    Call<HttpResult<JSONObject>> postNewsComment(@Query("userid") String userId,
            @Query("newsId") String newsId, @Query("newstype") int type,
            @Query("parentId") String parentId, @Query("text") String comment);

    /**
     * 获取评论
     *
     * @param parentId ==0时为一级评论=其他为父级的id
     */
    @GET("getNewsComment")
    Call<HttpResult<NewsInfoWrapper>> getNewsComment(@Query("newsId") String newsId,
            @Query("newstype") int type, @Query("parentId") String parentId,
            @Query("page") int page, @Query("pagesize") int size);

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

    /**
     * 我要爆料
     *
     * @param context 文字说明
     * @param options pitcUrl json格式图片 | small_pitcUrl json格式压缩图 | picTotal 图片数量
     */
    @POST("addBaoLiao")
    Call<HttpResult<String>> addBaoLiao(@Query("userid") String userId,
            @Query("context") String context, @QueryMap Map<String, String> options);

    /**
     * 我的收藏列表
     */
    @GET("getCollectionList")
    Call<HttpResult<String>> getCollectionList(@Query("userid") String userId,
            @Query("page") int page, @Query("pageSize") int pageSize);

    /**
     * 点赞或不喜欢
     *
     * @param fromId 点赞的用户ID
     * @param toId 被点赞的用户ID
     * @param type 新闻的类型 ==11时点赞的为评论
     * @param tempId 新闻ID 或评论ID
     * @param subType =1为点赞=2为不喜欢
     */
    @POST("addLike")
    Call<HttpResult<JSONObject>> optLike(@Query("fromUserid") String fromId,
            @Query("toUserid") String toId, @Query("type") int type, @Query("tempid") String tempId,
            @Query("subtype") int subType);

    /**
     * 获取上传凭证
     */
    @GET("getQiNiuToken")
    Call<ResponseBody> getUploadToken(@Query("certificate") String certificate);

    /**
     * 获取搜索框提示
     */
    @GET("getSearchInputKeyWord")
    Call<HttpResult<String>> getSearchKey();
}
