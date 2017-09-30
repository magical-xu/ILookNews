package com.chaoneng.ilooknews.library.qiniu;

import android.util.Log;
import com.chaoneng.ilooknews.AppConstant;
import com.chaoneng.ilooknews.api.HomeService;
import com.chaoneng.ilooknews.net.client.NetRequest;
import com.chaoneng.ilooknews.util.SimpleNotifyListener;
import com.magicalxu.library.blankj.ToastUtils;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCancellationSignal;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import okhttp3.ResponseBody;
import org.json.JSONException;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by magical on 17/9/12.
 * Description :
 */

public class QiNiuHelper {

    private UploadManager uploadManager;

    public static QiNiuHelper getInstance() {
        return QiNiuHelperHolder.INSTANCE;
    }

    private static class QiNiuHelperHolder {
        private static QiNiuHelper INSTANCE = new QiNiuHelper();
    }

    private QiNiuHelper() {
        //Configuration config =
        //        new Configuration.Builder().chunkSize(512 * 1024)        // 分片上传时，每片的大小。 默认256K
        //                .putThreshhold(1024 * 1024)   // 启用分片上传阀值。默认512K
        //                .connectTimeout(10)           // 链接超时。默认10秒
        //                .useHttps(true)               // 是否使用https上传域名
        //                .responseTimeout(60)          // 服务器响应超时。默认60秒
        //                .recorder(null)           // recorder分片上传时，已上传片记录器。默认null
        //                //.recorder(recorder, keyGen)   // keyGen 分片上传时，生成标识符，用于片记录器区分是那个文件的上传记录
        //                .zone(FixedZone.zone0)        // 设置区域，指定不同区域的上传域名、备用域名、备用IP。
        //                .build();
        // 重用uploadManager。一般地，只需要创建一个uploadManager对象
        this.uploadManager = new UploadManager();
    }

    /**
     * 上传到七牛
     *
     * @param path 图片本地地址
     * //* @param key 上传文件保存的文件名
     * @param token 七牛上传凭证
     */
    public void upload(String path, String token, final SimpleNotifyListener listener) {

        Map<String, String> params = new HashMap<>();
        uploadManager.put(path, null, token, new UpCompletionHandler() {
            @Override
            public void complete(String key, ResponseInfo info, JSONObject response) {

                if (info.isOK()) {

                    //Log.e("qiniu", TextUtils.isEmpty(key) ? " complete key is null" : key);
                    //Log.e("qiniu", "response : " + (null == response ? "" : response.toString()));

                    //upload success
                    //String testUrl =
                    //        "http://owfsbxql8.bkt.clouddn.com/13666430c7714b4bb7b55a1abda39ea4.png";
                    if (null != response) {
                        String imageKey = response.optString("key");
                        if (null != listener) {
                            listener.onSuccess(AppConstant.REMOTE_HOST + imageKey);
                        }
                    } else {
                        if (null != listener) {
                            listener.onFailed("response is null");
                        }
                    }
                } else {
                    Log.e("qiniu", " upload fail : " + info.error);
                    if (null != listener) {
                        listener.onFailed(info.error);
                    }
                }
            }
        }, new UploadOptions(params, "image", false, new UpProgressHandler() {
            @Override
            public void progress(String key, double percent) {

            }
        }, new UpCancellationSignal() {
            @Override
            public boolean isCancelled() {
                return false;
            }
        }));
    }

    public void getUpToken(final SimpleNotifyListener listener) {

        HomeService service = NetRequest.getInstance().create(HomeService.class);
        Call<ResponseBody> call = service.getUploadToken("ilooknews123456");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (response.isSuccessful()) {

                    String json;
                    try {
                        ResponseBody body = response.body();
                        if (null != body) {
                            json = body.string().trim();
                            JSONObject jsonObject = new JSONObject(json);
                            String token = jsonObject.optString("token");
                            if (null != listener) {
                                listener.onSuccess(token);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ToastUtils.showShort(t.getMessage());
                if (null != listener) {
                    listener.onFailed(t.getMessage());
                }
            }
        });
    }

    private String getPicName() {

        long timeMillis = System.currentTimeMillis();
        return "android_" + timeMillis;
    }
}
