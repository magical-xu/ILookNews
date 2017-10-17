package com.chaoneng.ilooknews.data;

import java.io.Serializable;
import java.util.List;

/**
 * Created by magical on 17/9/4.
 * Description : 新闻详情 头部数据
 */

public class NewsInfo implements Serializable {

    /**
     * newId : 2c7eb0ac5bc87a0c015bc87a12400000
     * userid : 402881ea5bba10c9015bba10cb730000
     * nickname : zhj
     * userIcon : http://avatar.csdn.net/0/E/8/1_qq_36173712.jpg
     * title : 跳转链接0
     * html_url : http://www.toutiao.com/a6414961193177317889/
     * commentCount : 11
     * createTime : 2017-05-02 17:24:54
     * isFollow : 0
     * isCollection : 0
     * like_count : 1
     * dislike_count : 1
     * newstype : 5
     */

    public String newId;
    public String userid;
    public String nickname;
    public String userIcon;
    public String title;

    public int commentCount;
    public String createTime;
    public String isFollow;
    public String isCollection;
    public int like_count;
    public int likeCount;
    public int dislike_count;
    public int newstype;

    public String text_key;

    //视频多出来的
    public String video_url;
    public int play_count;

    //图文多出来的
    public String content;

    //图片类多出来的
    public int picCount;
    public List<ImageInfo> pictures;

    //跳转类 跳转链接
    public String html_url;

    //动态列表多出来的
    public List<String> coverpic;
}
