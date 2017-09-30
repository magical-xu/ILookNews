package com.chaoneng.ilooknews.api;

import com.chaoneng.ilooknews.data.UserWrapper;
import com.chaoneng.ilooknews.net.data.HttpResult;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

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
    Call<HttpResult<UserWrapper>> loginByPhone(@Query("mobile") String mobile,
            @Query("veriCode") String verifyCode);

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
}