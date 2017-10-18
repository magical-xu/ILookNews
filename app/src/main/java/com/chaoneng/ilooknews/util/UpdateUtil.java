package com.chaoneng.ilooknews.util;

import com.magicalxu.library.blankj.SPUtils;

/**
 * Created by magical on 17/10/18.
 * Description :
 */

public class UpdateUtil {

    public static final String VIDEO_CHANNEL_UPDATE_TIME = "video_channel_update_time";
    public static final String NEWS_CHANNEL_UPDATE_TIME = "news_channel_update_time";

    public static long TIME_GAP_FOR_NEWS = 6L * 60L * 60L * 1000L;      //新闻以6小时为一次更新标准
    public static long TIME_GAP_FOR_VIDEO = 24L * 60L * 60L * 1000L;    //以一天为时间更新间隙
    public static long TIME_GAP_FOR_TEST = 60L * 1000L;       //便于调试 60s

    /**
     * 设置 新闻或视频频道 最近一次 更新时间
     */
    public static void setChannelUpdateTime(boolean isVideo) {

        if (isVideo) {
            SPUtils.getInstance().put(VIDEO_CHANNEL_UPDATE_TIME, System.currentTimeMillis());
        } else {
            SPUtils.getInstance().put(NEWS_CHANNEL_UPDATE_TIME, System.currentTimeMillis());
        }
    }

    /**
     * 获取 视频频道 最近一次 更新时间
     *
     * @return long
     */
    public static long getVideoUpdateTime() {

        return SPUtils.getInstance().getLong(VIDEO_CHANNEL_UPDATE_TIME, 0);
    }

    /**
     * 获取 新闻频道 最近一次 更新时间
     *
     * @return long
     */
    public static long getNewsUpdateTime() {

        return SPUtils.getInstance().getLong(NEWS_CHANNEL_UPDATE_TIME, 0);
    }

    /**
     * 判断是否需要 重新拉取 频道
     */
    public static boolean needUpdateChannel(boolean isVideo) {

        long history;
        if (isVideo) {
            history = getVideoUpdateTime();
            return System.currentTimeMillis() - history > TIME_GAP_FOR_TEST;
        } else {
            history = getNewsUpdateTime();
            return System.currentTimeMillis() - history > TIME_GAP_FOR_NEWS;
        }
    }
}
