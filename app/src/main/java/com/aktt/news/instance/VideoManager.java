package com.aktt.news.instance;

import java.util.HashMap;

/**
 * Created by magical on 17/9/19.
 * Description : 记录视频播放进度
 */

public class VideoManager {

    /**
     * record the video 's progress
     */
    private HashMap<String, Long> map;

    public static int ERROR_PLAY_POS = -22; //当前无播放的位置返回

    private static class SingletonHolder {
        public static VideoManager instance = new VideoManager();
    }

    private VideoManager() {
        map = new HashMap<>();
    }

    public static VideoManager getInstance() {

        return SingletonHolder.instance;
    }

    /**
     * 存进度
     *
     * @param url 视频url
     * @param progress 视频播放进度
     */
    public void putProgress(String url, long progress) {
        map.put(url, progress);
    }

    /**
     * 获取视频播放进度
     *
     * @param url 视频url
     * @return 找不到key 默认为0
     */
    public long getProgress(String url) {
        return map.containsKey(url) ? map.get(url) : 0;
    }
}
