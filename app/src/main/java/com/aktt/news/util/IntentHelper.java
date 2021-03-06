package com.aktt.news.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.aktt.news.AppConstant;
import com.aktt.news.SimpleWebActivity;
import com.aktt.news.module.focus.AddFollowListActivity;
import com.aktt.news.module.home.activity.CommentActivity;
import com.aktt.news.module.home.activity.NewsDetailActivity;
import com.aktt.news.module.home.activity.NewsPhotoDetailActivity;
import com.aktt.news.module.home.activity.NotifyActivity;
import com.aktt.news.module.login.LoginActivity;
import com.aktt.news.module.login.RegisterActivity;
import com.aktt.news.module.search.SearchActivity;
import com.aktt.news.module.search.SearchDetailActivity;
import com.aktt.news.module.share.ShareBoardActivity;
import com.aktt.news.module.user.activity.BrokeActivity;
import com.aktt.news.module.user.activity.BrokeListActivity;
import com.aktt.news.module.user.activity.CollectionActivity;
import com.aktt.news.module.user.activity.FeedBackActivity;
import com.aktt.news.module.user.activity.ImageBrowseActivity;
import com.aktt.news.module.user.activity.ProfileActivity;
import com.aktt.news.module.user.activity.ReportActivity;
import com.aktt.news.module.user.activity.SettingActivity;
import com.aktt.news.module.user.activity.UserCenterActivity;
import com.aktt.news.module.video.activity.VideoDetailActivity;
import java.util.ArrayList;

/**
 * Created by magical on 17/8/17.
 * Description : 跳转帮助类
 */

public class IntentHelper {

    public static final String PARAMS_ONE = "params_one";
    public static final String PARAMS_TWO = "params_two";

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
    public static void openVideoDetailPage(Context context, String vid, long seek, int newsType) {
        VideoDetailActivity.newInstance(context, vid, seek, newsType);
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
        UserCenterActivity.getInstance(context, uid);
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
    public static void openRegisterPage(Activity context) {
        context.startActivityForResult(new Intent(context, RegisterActivity.class),
                AppConstant.REQUEST_CODE);
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
    public static void openWebPage(Context context, @NonNull String url, @Nullable String content) {
        SimpleWebActivity.getInstance(context, url, content);
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
    public static void openShareBottomPage(Context context, String newsId, int newsType,
            String publisher) {
        ShareBoardActivity.getInstance(context, newsId, newsType, publisher);
    }

    /**
     * 跳转 收藏界面
     */
    public static void openCollectionPage(Context context) {
        context.startActivity(new Intent(context, CollectionActivity.class));
    }

    /**
     * 跳转 图片浏览 界面
     */
    public static void openImageBrowsePage(Context context, int curPos, ArrayList<String> images) {
        ImageBrowseActivity.getInstance(context, curPos, images);
    }

    /**
     * 跳转 爆料列表 界面
     */
    public static void openBrokeListPage(Context context) {
        context.startActivity(new Intent(context, BrokeListActivity.class));
    }

    /**
     * 跳转 举报界面
     */
    public static void openReportPage(Context context, String toId, String newsId, int newsType) {
        ReportActivity.getInstance(context, toId, newsId, newsType);
    }
}
