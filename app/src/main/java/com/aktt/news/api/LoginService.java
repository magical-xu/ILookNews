package com.aktt.news.api;

import com.aktt.news.data.UserWrapper;
import com.aktt.news.net.data.HttpResult;
import java.util.Map;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * Created by magical on 17/8/29.
 * Description : 登录相关接口
 */

public interface LoginService {

    /**
     * 用户名密码注册
     */
    @POST("registerByName")
    Call<HttpResult<UserWrapper>> registerByName(@Query("username") String userName,
            @Query("userpwd") String pwd, @Query("icon") String avatar);

    /**
     * 手机号注册
     */
    @POST("registerByPhone")
    Call<HttpResult<UserWrapper>> registerByPhone(@Query("mobile") String mobile,
            @Query("veriCode") String verifyCode, @Query("icon") String avatar);

    /**
     * 手机号登录
     */
    @POST("loginByPhone")
    Call<HttpResult<UserWrapper>> loginByPhone(@Query("mobile") String mobile);

    /**
     * 用户名密码登录
     */
    @POST("loginByUsername")
    Call<HttpResult<UserWrapper>> loginByName(@Query("username") String userName,
            @Query("userpwd") String pwd);

    /**
     * 发送验证码
     */
    @POST("sendMobileMsg")
    Call<HttpResult<JSONObject>> sendMobileCode(@Query("mobile") String mobile);

    @POST("loginThrid")
    Call<HttpResult<UserWrapper>> onThirdLogin(@Query("type") String type,
            @Query("thrid") String openId, @Query("thridtoken") String token,
            @Query("thridexpired") String expired, @QueryMap Map<String, String> options);
}