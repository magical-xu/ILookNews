package com.chaoneng.ilooknews.util;

import com.chaoneng.ilooknews.api.HomeService;
import com.chaoneng.ilooknews.net.callback.SimpleCallback;
import com.chaoneng.ilooknews.net.data.HttpResult;
import com.magicalxu.library.blankj.ToastUtils;
import org.json.JSONObject;
import retrofit2.Call;

/**
 * Created by magical on 17/9/8.
 * Description :
 */

public class OptionHelper {

  /**
   * 点赞
   *
   * @param fromId 点赞的用户ID
   * @param toId 被点赞的用户ID
   * @param type 新闻的类型 ==11时点赞的为评论
   * @param tempId 新闻ID 或评论ID
   * @param subType =1为点赞=2为不喜欢
   */
  public static void onPraise(HomeService service, String fromId, String toId, int type,
          String tempId, int subType, final NotifyListener listener) {

    Call<HttpResult<JSONObject>> call = service.optLike(fromId, toId, type, tempId, subType);
    call.enqueue(new SimpleCallback<JSONObject>() {
      @Override
      public void onSuccess(JSONObject data) {
        if (listener != null) {
          listener.onSuccess();
        }
      }

      @Override
      public void onFail(String code, String errorMsg) {
        ToastUtils.showShort(errorMsg);
        if (listener != null) {
          listener.onFail();
        }
      }
    });
  }
}
