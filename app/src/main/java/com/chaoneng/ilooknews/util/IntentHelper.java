package com.chaoneng.ilooknews.util;

import android.content.Context;
import android.content.Intent;
import com.chaoneng.ilooknews.module.home.activity.NotifyActivity;
import com.chaoneng.ilooknews.module.login.LoginActivity;
import com.chaoneng.ilooknews.module.user.activity.BrokeActivity;
import com.chaoneng.ilooknews.module.user.activity.FeedBackActivity;
import com.chaoneng.ilooknews.module.user.activity.ProfileActivity;
import com.chaoneng.ilooknews.module.user.activity.SettingActivity;
import com.chaoneng.ilooknews.module.video.activity.VideoDetailActivity;

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

  /**
   * 跳转 用户反馈界面
   */
  public static void openFeedbackPage(Context context) {
    context.startActivity(new Intent(context, FeedBackActivity.class));
  }

  /**
   * 跳转视频详情界面
   */
  public static void openVideoDetailPage(Context context, String vid) {
    VideoDetailActivity.newInstance(context, vid);
  }

  /**
   * 跳转系统设置界面
   */
  public static void openSettingPage(Context context) {
    context.startActivity(new Intent(context, SettingActivity.class));
  }

  /**
   * 跳转登录界面
   */
  public static void openLoginPage(Context context) {
    context.startActivity(new Intent(context, LoginActivity.class));
  }

  /**
   * 跳转编辑资料界面
   */
  public static void openProfilePage(Context context) {
    context.startActivity(new Intent(context, ProfileActivity.class));
  }

  /**
   * 跳转爆料界面
   */
  public static void openBrokePage(Context context) {
    context.startActivity(new Intent(context, BrokeActivity.class));
  }
}
