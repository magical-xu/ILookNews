package com.chaoneng.ilooknews.library.gsyvideoplayer;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import com.bumptech.glide.request.RequestOptions;
import com.chaoneng.ilooknews.AppConstant;
import com.chaoneng.ilooknews.R;
import com.chaoneng.ilooknews.instance.VideoManager;
import com.chaoneng.ilooknews.library.glide.GlideApp;
import com.chaoneng.ilooknews.library.gsyvideoplayer.listener.SampleListener;
import com.magicalxu.library.blankj.KeyboardUtils;
import com.magicalxu.library.blankj.SPUtils;
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder;
import com.shuyu.gsyvideoplayer.listener.LockClickListener;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;

/**
 * Created by magical on 17/9/5.
 * Description : gsy video player 调用帮助类
 */

public class VideoHelper {

    public static final String TAG = "VideoHelper";

    public static void initPlayer(final Context context,
            final StandardGSYVideoPlayer gsyVideoPlayer, String url, String title,
            final OrientationUtils orientationUtils) {

        long progress = VideoManager.getInstance().getProgress(url);
        gsyVideoPlayer.setSeekOnStart(progress);

        //wifi播放提醒
        boolean needTip = SPUtils.getInstance().getBoolean(AppConstant.WIFI_4G_TIP, true);
        gsyVideoPlayer.setNeedShowWifiTip(needTip);

        //滑动界面改变 进度、声音
        gsyVideoPlayer.setIsTouchWiget(false);

        //默认缓存路径 初始化播放器
        gsyVideoPlayer.setUp(url, true, null, title);

        //隐藏title
        gsyVideoPlayer.getTitleTextView().setVisibility(View.GONE);
        //隐藏返回键
        gsyVideoPlayer.getBackButton().setVisibility(View.GONE);

        //设置全屏按键功能
        gsyVideoPlayer.getFullscreenButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //直接横屏
                if (null != orientationUtils) {
                    orientationUtils.resolveByClick();
                }
                gsyVideoPlayer.startWindowFullscreen(context, true, true);
            }
        });

        //自动旋转
        gsyVideoPlayer.setRotateViewAuto(false);
        gsyVideoPlayer.setLockLand(false);
        gsyVideoPlayer.setPlayTag(TAG + title);
        gsyVideoPlayer.setShowFullAnimation(false);
        //需要全屏锁定功能
        gsyVideoPlayer.setNeedLockFull(false);

        gsyVideoPlayer.setStandardVideoAllCallBack(new SampleListener() {
            @Override
            public void onPrepared(String url, Object... objects) {
                super.onPrepared(url, objects);
                //开始播放了才能旋转和全屏
                if (null != orientationUtils) {
                    orientationUtils.setEnable(true);
                }
            }

            @Override
            public void onQuitFullscreen(String url, Object... objects) {
                super.onQuitFullscreen(url, objects);
                if (orientationUtils != null) {
                    orientationUtils.backToProtVideo();
                }
            }

            @Override
            public void onEnterFullscreen(String url, Object... objects) {
                super.onEnterFullscreen(url, objects);
            }

            @Override
            public void onClickStartIcon(String url, Object... objects) {
                long progress = VideoManager.getInstance().getProgress(url);
                gsyVideoPlayer.setSeekOnStart(progress);
                super.onClickStartIcon(url, objects);
            }
        });

        gsyVideoPlayer.setLockClickListener(new LockClickListener() {
            @Override
            public void onClick(View view, boolean lock) {
                if (orientationUtils != null) {
                    //配合下方的onConfigurationChanged
                    orientationUtils.setEnable(!lock);
                }
            }
        });
    }

    public void loadFirstFrame(Context context) {

        GlideApp.with(context)
                .setDefaultRequestOptions(new RequestOptions().frame(1000000)
                        .centerCrop()
                        .error(R.drawable.ic_empty_picture));
    }

    public static void initDetailPage(final Context context, String url,
            final StandardGSYVideoPlayer gsyVideoPlayer, final OrientationUtils orientationUtils,
            long progress) {

        if (null == orientationUtils) {
            return;
        }

        gsyVideoPlayer.getBackButton().setVisibility(View.GONE);
        gsyVideoPlayer.getTitleTextView().setVisibility(View.GONE);

        long progress1 = VideoManager.getInstance().getProgress(url);

        boolean needTip = SPUtils.getInstance().getBoolean(AppConstant.WIFI_4G_TIP, true);
        GSYVideoOptionBuilder gsyVideoOption = new GSYVideoOptionBuilder();
        gsyVideoOption
                //.setThumbImageView(imageView)
                .setIsTouchWiget(true).setNeedShowWifiTip(needTip)
                .setRotateViewAuto(false).setSeekOnStart(progress1)
                .setLockLand(false)
                .setShowFullAnimation(false)
                .setNeedLockFull(false)
                .setSeekRatio(1)
                .setUrl(url)
                .setCacheWithPlay(false)
                .setStandardVideoAllCallBack(new SampleListener() {
                    @Override
                    public void onPrepared(String url, Object... objects) {
                        super.onPrepared(url, objects);
                        //开始播放了才能旋转和全屏
                        //orientationUtils.setEnable(true);
                        //isPlay = true;
                    }

                    @Override
                    public void onEnterFullscreen(String url, Object... objects) {
                        super.onEnterFullscreen(url, objects);
                        KeyboardUtils.hideSoftInput(((Activity) context));
                    }

                    @Override
                    public void onAutoComplete(String url, Object... objects) {
                        super.onAutoComplete(url, objects);
                    }

                    @Override
                    public void onClickStartError(String url, Object... objects) {
                        super.onClickStartError(url, objects);
                    }

                    @Override
                    public void onQuitFullscreen(String url, Object... objects) {
                        super.onQuitFullscreen(url, objects);
                        orientationUtils.backToProtVideo();
                    }
                })
                .setLockClickListener(new LockClickListener() {
                    @Override
                    public void onClick(View view, boolean lock) {
                        //配合下方的onConfigurationChanged
                        //orientationUtils.setEnable(!lock);
                    }
                })
                .build(gsyVideoPlayer);

        gsyVideoPlayer.getFullscreenButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //直接横屏
                orientationUtils.resolveByClick();

                //第一个true是否需要隐藏actionbar，第二个true是否需要隐藏statusbar
                gsyVideoPlayer.startWindowFullscreen(context, true, true);
            }
        });

        if (progress1 != 0) {
            //自动播放
            gsyVideoPlayer.getStartButton().performClick();
        }
    }
}
