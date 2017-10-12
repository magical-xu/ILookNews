package com.chaoneng.ilooknews.util;

import android.text.TextUtils;
import android.widget.EditText;
import com.chaoneng.ilooknews.AppConstant;
import com.chaoneng.ilooknews.R;
import com.chaoneng.ilooknews.api.HomeService;
import com.chaoneng.ilooknews.instance.AccountManager;
import com.chaoneng.ilooknews.net.callback.SimpleCallback;
import com.chaoneng.ilooknews.net.data.HttpResult;
import com.magicalxu.library.blankj.ToastUtils;
import org.json.JSONObject;
import retrofit2.Call;

/**
 * Created by magical on 2017/10/12.
 * Description : 统一用户操作的调用 复用代码减少错误
 */

public class UserOptionHelper {

    /**
     * 操作收藏
     */
    public static void onClickStar(HomeService service, String newsId, int newsType,
            final SimpleNotifyListener listener) {

        String userId = AccountManager.getInstance().getUserId();
        if (TextUtils.isEmpty(userId)) {
            ToastUtils.showShort("请先登录");
            return;
        }

        Call<HttpResult<JSONObject>> call =
                service.addCollection(StringHelper.getString(userId), newsId, newsType);
        call.enqueue(new SimpleCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject data) {
                ToastUtils.showShort("收藏成功");
                if (null != listener) {
                    listener.onSuccess("");
                }
            }

            @Override
            public void onFail(String code, String errorMsg) {
                if (null != listener) {
                    listener.onFailed(errorMsg);
                }
            }
        });
    }

    /**
     * 评论新闻
     */
    public static void onSendComment(EditText inputView, HomeService service, String newsId,
            int newsType, final SimplePreNotifyListener listener) {

        String comment = inputView.getText().toString().trim();
        if (TextUtils.isEmpty(comment)) {
            ToastUtils.showShort(R.string.comment_can_not_be_null);
            return;
        }

        String userId = AccountManager.getInstance().getUserId();

        if (null != listener) {
            listener.onPreToDo();
        }
        Call<HttpResult<JSONObject>> call =
                service.postNewsComment(StringHelper.getString(userId), newsId, newsType,
                        AppConstant.NONE_VALUE, comment);
        call.enqueue(new SimpleCallback<JSONObject>() {

            @Override
            public void onSuccess(JSONObject data) {
                if (null != listener) {
                    listener.onSuccess("");
                }
            }

            @Override
            public void onFail(String code, String errorMsg) {
                if (null != listener) {
                    listener.onFailed(errorMsg);
                }
            }
        });
    }
}
