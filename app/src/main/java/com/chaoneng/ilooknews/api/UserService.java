package com.chaoneng.ilooknews.api;

import com.chaoneng.ilooknews.net.data.HttpResult;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by magical on 17/8/28.
 * Description :
 */

public interface UserService {

  /**
   * 加关注
   */
  @POST("addFollow")
  Call<HttpResult<String>> follow(@Query("userid") String userId, @Query("fid") String fid);

  /**
   * 取消关注
   */
  @POST("deleteFollow")
  Call<HttpResult<String>> cancelFollow(@Query("userid") String userId, @Query("fid") String fid);

  /**
   * 获取关系列表
   *
   * @param userId 要查看的用户id
   * @param myId 登录用户id
   * @param type 0关注的人 type=1粉丝列表 2其他访客
   */
  @GET("getfollowsBytype")
  Call<HttpResult<String>> getRelationList(@Query("userid") String userId,
      @Query("myid") String myId, @Query("type") int type, @Query("page") int page,
      @Query("pageSize") int size);

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
  Call<HttpResult<String>> optLike(@Query("fromUserid") String fromId,
      @Query("toUserid") String toId, @Query("type") int type, @Query("tempid") String tempId,
      @Query("subtype") int subType);

  @GET("getShare")
  Call<HttpResult<String>> getShare(@Query("userid") String userId, @Query("nid") String nid,
      @Query("shareType") int share, @Query("newsType") int news);

  /**
   * 添加用户反馈信息
   */
  @POST("addfeedbackRecord")
  Call<HttpResult<String>> addFeedback(@Query("userid") String userId,
      @Query("content") String content);
}
