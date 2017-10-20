package com.aktt.news.net.callback;

import java.io.IOException;
import okhttp3.ResponseBody;
import org.json.JSONException;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by magical on 17/9/22.
 * Description : 返回原始信息的 JSONObject
 */

public abstract class SimpleRawJsonCallback implements Callback<ResponseBody> {

    private String SIMPLE_ERROR_CODE = "-1";
    private String successCode = "8888";
    private String err_network = "9001";
    private String err_none_body = "9002";

    @Override
    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

        if (!response.isSuccessful()) {
            onFail(SIMPLE_ERROR_CODE, err_network);
            return;
        }

        ResponseBody raw = response.body();
        if (null == raw) {
            onFail(SIMPLE_ERROR_CODE, err_none_body);
            return;
        }

        try {
            String string = raw.string();
            JSONObject jsonObject = new JSONObject(string);
            onSuccess(jsonObject);
        } catch (IOException e) {
            e.printStackTrace();
            onFail(SIMPLE_ERROR_CODE, e.getMessage());
        } catch (JSONException e) {
            e.printStackTrace();
            onFail(SIMPLE_ERROR_CODE, e.getMessage());
        }
    }

    @Override
    public void onFailure(Call<ResponseBody> call, Throwable t) {
        onFail(SIMPLE_ERROR_CODE, t.getMessage());
    }

    public abstract void onSuccess(JSONObject data);

    public abstract void onFail(String code, String errorMsg);
}
