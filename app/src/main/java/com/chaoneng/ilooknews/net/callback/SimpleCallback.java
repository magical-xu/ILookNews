package com.chaoneng.ilooknews.net.callback;

import android.text.TextUtils;
import com.chaoneng.ilooknews.net.data.HttpResult;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by magical on 17/8/28.
 * Description :
 */

public abstract class SimpleCallback<T> implements Callback<HttpResult<T>> {

    private String SIMPLE_ERROR_CODE = "-1";
    private String successCode = "8888";
    private String err_network = "9001";
    private String err_none_body = "9002";

    @Override
    public void onResponse(Call<HttpResult<T>> call, Response<HttpResult<T>> response) {

        if (!response.isSuccessful()) {
            onFail(SIMPLE_ERROR_CODE, err_network);
            return;
        }

        HttpResult<T> raw = response.body();
        if (null == raw) {
            onFail(SIMPLE_ERROR_CODE, err_none_body);
            return;
        }

        if (TextUtils.equals(raw.code, successCode)) {

            T data = raw.data;
            onSuccess(data);
        } else {

            onFail(raw.code, raw.msg);
        }
    }

    @Override
    public void onFailure(Call<HttpResult<T>> call, Throwable t) {
        onFail(SIMPLE_ERROR_CODE, t.getMessage());
    }

    public abstract void onSuccess(T data);

    public abstract void onFail(String code, String errorMsg);
}
