package com.chaoneng.ilooknews.library.qiniu;

import com.qiniu.android.common.FixedZone;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.UpCancellationSignal;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;

/**
 * Created by magical on 17/9/12.
 * Description :
 */

public class QiNiuHelper {

    public void upload() {

        Configuration config =
                new Configuration.Builder().chunkSize(512 * 1024)        // 分片上传时，每片的大小。 默认256K
                        .putThreshhold(1024 * 1024)   // 启用分片上传阀值。默认512K
                        .connectTimeout(10)           // 链接超时。默认10秒
                        .useHttps(true)               // 是否使用https上传域名
                        .responseTimeout(60)          // 服务器响应超时。默认60秒
                        .recorder(null)           // recorder分片上传时，已上传片记录器。默认null
                        //.recorder(recorder, keyGen)   // keyGen 分片上传时，生成标识符，用于片记录器区分是那个文件的上传记录
                        .zone(FixedZone.zone0)        // 设置区域，指定不同区域的上传域名、备用域名、备用IP。
                        .build();
        // 重用uploadManager。一般地，只需要创建一个uploadManager对象
        UploadManager uploadManager = new UploadManager(config);

        Map<String, String> params = new HashMap<>();
        uploadManager.put("filePath", "key", "token", new UpCompletionHandler() {
            @Override
            public void complete(String key, ResponseInfo info, JSONObject response) {

            }
        }, new UploadOptions(params, "mimeType", false, new UpProgressHandler() {
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
}
