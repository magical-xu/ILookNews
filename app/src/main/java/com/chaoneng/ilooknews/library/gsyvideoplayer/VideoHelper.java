package com.chaoneng.ilooknews.library.gsyvideoplayer;

import android.content.Context;
import android.view.View;
import com.bumptech.glide.request.RequestOptions;
import com.chaoneng.ilooknews.AppConstant;
import com.chaoneng.ilooknews.R;
import com.chaoneng.ilooknews.library.glide.GlideApp;
import com.chaoneng.ilooknews.library.gsyvideoplayer.listener.SampleListener;
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
            final StandardGSYVideoPlayer gsyVideoPlayer, String title,
            final OrientationUtils orientationUtils) {

        //wifi播放提醒
        gsyVideoPlayer.setNeedShowWifiTip(true);

        //滑动界面改变 进度、声音
        gsyVideoPlayer.setIsTouchWiget(false);

        //默认缓存路径 初始化播放器
        gsyVideoPlayer.setUp(AppConstant.TEST_VIDEO_URL, true, null, title);

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
        gsyVideoPlayer.setNeedLockFull(true);

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
}
