package com.aktt.news.net.callback;

import com.aktt.news.net.data.HttpResult;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by magical on 17/8/18.
 * Description :
 */

public abstract class SimpleJsonCallback<T extends HttpResult> implements Callback<T> {

    public int error_code = -1;

    @Override
    public void onResponse(Call<T> call, Response<T> response) {

        if (response.isSuccessful()) {
            T body = response.body();
            onSuccess(body);
        } else {
            onFailed(response.code(), response.message());
        }
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        onFailed(error_code, t.getMessage());
    }

    public abstract void onSuccess(T data);

    public abstract void onFailed(int code, String message);
}
