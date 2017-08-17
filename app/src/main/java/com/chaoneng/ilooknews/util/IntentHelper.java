package com.chaoneng.ilooknews.util;

import android.content.Context;
import android.content.Intent;
import com.chaoneng.ilooknews.module.home.activity.NotifyActivity;

/**
 * Created by magical on 17/8/17.
 * Description : 跳转帮助类
 */

public class IntentHelper {

  /**
   * 跳转 我的消息界面
   */
  public static void openNotifyPage(Context context) {
    context.startActivity(new Intent(context, NotifyActivity.class));
  }
}
