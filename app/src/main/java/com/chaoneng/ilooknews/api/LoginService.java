package com.chaoneng.ilooknews.api;

import com.chaoneng.ilooknews.data.BaseUser;
import com.chaoneng.ilooknews.net.data.HttpResult;
import retrofit2.Call;

/**
 * Created by magical on 17/8/29.
 * Description : 登录相关接口
 */

public interface LoginService {

  Call<HttpResult<BaseUser>> registerByName();
}