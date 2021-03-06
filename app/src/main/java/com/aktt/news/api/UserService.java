package com.aktt.news.api;

import com.aktt.news.data.NewsInfoListWrapper;
import com.aktt.news.data.ShareData;
import com.aktt.news.module.focus.data.FocusWrapper;
import com.aktt.news.module.user.data.BrokeListWrapper;
import com.aktt.news.module.user.data.NotifyDetail;
import com.aktt.news.module.user.data.NotifyWrapper;
import com.aktt.news.module.user.data.UserCenterWrapper;
import com.aktt.news.module.user.data.UserInfoWrapper;
import com.aktt.news.net.data.HttpResult;
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
 * Description : 用户相关接口
 */

public interface UserService {

    String PARAMS_ICON = "icon";                    // 如果更改的为头像则需要传此参数
    String PARAMS_ICON_SMALL = "icon_small";        // 如果更改的为头像则需要传此参数
    String PARAMS_NICK = "nickname";                // 如果更改的是昵称，则必传
    String PARAMS_GENDER = "gender";                // 0男 1 女
    String PARAMS_BIRTH = "birth";                  // 生日 格式为'2013-10-10'
    String PARAMS_ADDRESS = "address";              // 地址
    String PARAMS_INTRODUCE = "introduce";          // 个性签名

    /**
     * 加关注
     */
    @POST("addFollow")
    Call<HttpResult<JSONObject>> follow(@Query("userid") String userId, @Query("fid") String fid);

    /**
     * 取消关注
     */
    @POST("deleteFollow")
    Call<HttpResult<JSONObject>> cancelFollow(@Query("userid") String userId,
            @Query("fid") String fid);

    /**
     * 获取关系列表
     *
     * @param userId 要查看的用户id
     * @param myId 登录用户id
     * @param type 0关注的人 type=1粉丝列表 2其他访客
     */
    @GET("getfollowsBytype")
    Call<HttpResult<FocusWrapper>> getRelationList(@Query("userid") String userId,
            @Query("myid") String myId, @Query("type") int type, @Query("page") int page,
            @Query("pageSize") int size);

    /**
     * 获取分享信息
     */
    @GET("getShare")
    Call<HttpResult<ShareData>> getShare(@Query("userid") String userId, @Query("nid") String nid,
            @Query("shareType") int share, @Query("newsType") int news);

    /**
     * 获取分享广告图
     */
    @GET("getShareBeforeAds")
    Call<ResponseBody> getShareAdImage();

    /**
     * 添加用户反馈信息
     */
    @POST("addfeedbackRecord")
    Call<HttpResult<String>> addFeedback(@Query("userid") String userId,
            @Query("content") String content);

    /**
     * 更改用户信息
     *
     * @param userId 登录用户ID
     * @param type 1更改头像，=2昵称，=3性别 =4生日 =5地址 6签名
     */
    @POST("changeUserInfo")
    Call<HttpResult<JSONObject>> modifyUserInfo(@Query("userid") String userId,
            @Query("type") int type, @QueryMap Map<String, String> options);

    /**
     * 我未关注的用户列表
     */
    @GET("getNotFollow")
    Call<HttpResult<FocusWrapper>> getNotFollowList(@Query("myid") String userId,
            @Query("page") int page, @Query("pageSize") int pageSize);

    /**
     * 系统消息列表
     */
    @GET("getMyMessageList")
    Call<HttpResult<NotifyWrapper>> getMyMessageList(@Query("userid") String userId,
            @Query("page") int page, @Query("pageSize") int pageSize);

    /**
     * 获取系统消息详情
     */
    @GET("getMyMessageByid")
    Call<HttpResult<NotifyDetail>> getMsgDetail(@Query("userid") String userId,
            @Query("mid") String mid);

    /**
     * 根据 ID 删除系统消息
     */
    @POST("deleteMyMessageByid")
    Call<HttpResult<JSONObject>> deleteMyMessageByid(@Query("userid") String userId,
            @Query("mid") String mid);

    /**
     * 获取用户信息
     *
     * @param type 1 动态 2 我的爆料
     */
    @GET("getUserInfo")
    Call<HttpResult<UserInfoWrapper>> getUserInfo(@Query("userid") String userId,
            @Query("targetId") String targetId, @Query("type") int type, @Query("page") int page,
            @Query("pageSize") int pageSize);

    /**
     * 获取动态列表
     */
    @GET("getDongTaiList")
    Call<HttpResult<NewsInfoListWrapper>> getDongTaiList(@Query("userid") String userId,
            @Query("targetId") String targetId, @Query("page") int page,
            @Query("pageSize") int pageSize);

    /**
     * 获取爆料列表
     */
    @GET("getBaoLiaoList")
    Call<HttpResult<BrokeListWrapper>> getBaoLiaoList(@Query("userid") String userId,
            @Query("page") int page, @Query("pageSize") int pageSize);

    /**
     * 获取所有用户的爆料列表
     */
    @GET("getBaoLiaoListAll")
    Call<HttpResult<BrokeListWrapper>> getBaoLiaoListAll(@Query("userid") String userId,
            @Query("page") int page, @Query("pageSize") int pageSize);

    /**
     * 我的个人中心
     */
    @GET("getMyCenter")
    Call<HttpResult<UserCenterWrapper>> getMyCenter(@Query("userid") String userId,
            @Query("page") int page, @Query("pageSize") int pageSize);

    /**
     * 添加视频播放次数
     */
    @POST("addPlayCount")
    Call<HttpResult<JSONObject>> addVideoPlayCount(@Query("newsId") String newsId);

    /**
     * 举报
     */
    @POST("addJuBaoRecord")
    Call<HttpResult<JSONObject>> report(@Query("fromUserid") String uid,
            @Query("toUserid") String toUid, @Query("tempid") String newsId,
            @Query("type") int type, @Query("reason") String reason);
}
