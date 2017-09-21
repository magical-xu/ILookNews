package com.chaoneng.ilooknews.util;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import com.chaoneng.ilooknews.SimpleWebActivity;
import com.chaoneng.ilooknews.module.focus.AddFollowListActivity;
import com.chaoneng.ilooknews.module.home.activity.CommentActivity;
import com.chaoneng.ilooknews.module.home.activity.NewsDetailActivity;
import com.chaoneng.ilooknews.module.home.activity.NewsPhotoDetailActivity;
import com.chaoneng.ilooknews.module.home.activity.NotifyActivity;
import com.chaoneng.ilooknews.module.login.LoginActivity;
import com.chaoneng.ilooknews.module.login.RegisterActivity;
import com.chaoneng.ilooknews.module.search.SearchActivity;
import com.chaoneng.ilooknews.module.search.SearchDetailActivity;
import com.chaoneng.ilooknews.module.share.ShareBoardActivity;
import com.chaoneng.ilooknews.module.user.activity.BrokeActivity;
import com.chaoneng.ilooknews.module.user.activity.FeedBackActivity;
import com.chaoneng.ilooknews.module.user.activity.ProfileActivity;
import com.chaoneng.ilooknews.module.user.activity.SettingActivity;
import com.chaoneng.ilooknews.module.user.activity.UserCenterActivity;
import com.chaoneng.ilooknews.module.video.activity.VideoDetailActivity;

/**
 * Created by magical on 17/8/17.
 * Description : 跳转帮助类
 */

public class IntentHelper {

    public static final String PARAMS_ONE = "params_one";

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
     *
     * @param seek 播放进度
     */
    public static void openVideoDetailPage(Context context, String vid, long seek) {
        VideoDetailActivity.newInstance(context, vid, seek);
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

    /**
     * 跳转搜索界面
     */
    public static void openSearchPage(Context context) {
        context.startActivity(new Intent(context, SearchActivity.class));
    }

    /**
     * 跳转搜索详情
     */
    public static void openSearchDetailPage(Context context, String keyword) {
        SearchDetailActivity.getInstance(context, keyword);
    }

    /**
     * 跳转用户中心界面
     */
    public static void openUserCenterPage(Context context, @NonNull String uid) {
        Intent intent = new Intent(context, UserCenterActivity.class);
        intent.putExtra(PARAMS_ONE, uid);
        context.startActivity(intent);
    }

    /**
     * 跳转新闻图片详情界面
     */
    public static void openNewsPhotoDetailPage(Context context, String newsId, int type) {
        NewsPhotoDetailActivity.getInstance(context, newsId, type);
    }

    /**
     * 跳转新闻详情界面 （图文）
     */
    public static void openNewsDetailPage(Context context, String newsId, int type) {
        NewsDetailActivity.getInstance(context, newsId, type);
    }

    /**
     * 跳转注册界面
     */
    public static void openRegisterPage(Context context) {
        context.startActivity(new Intent(context, RegisterActivity.class));
    }

    /**
     * 跳转加关注界面
     */
    public static void openAddFocusPage(Context context) {
        context.startActivity(new Intent(context, AddFollowListActivity.class));
    }

    /**
     * 跳转 web 页面
     */
    public static void openWebPage(Context context, @NonNull String url) {
        SimpleWebActivity.getInstance(context, url);
    }

    /**
     * 跳转新闻评论 一级界面
     */
    public static void openNewsCommentPage(Context context, String newsId, int type) {
        CommentActivity.getInstance(context, newsId, type);
    }

    /**
     * 底部弹出分享框
     */
    public static void openShareBottomPage(Context context, String newsId, int newsType) {
        ShareBoardActivity.getInstance(context, newsId, newsType);
    }
}
