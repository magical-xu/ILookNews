package com.chaoneng.ilooknews.module.home.data;

import com.chaoneng.ilooknews.module.video.data.VideoComment;
import java.io.Serializable;
import java.util.List;

/**
 * Created by magical on 2017/9/3.
 * Description : 新闻详情
 */

public class NewsDetailBean implements Serializable {

    /**
     * newInfo : {"newId":"402881ea5bc512b1015bc512b5110000","userid":"402881ea5bba10c9015bba10cb730000","nickname":"zhj","userIcon":"http://avatar.csdn.net/0/E/8/1_qq_36173712.jpg","title":"图文类新闻0","content":"<p>\r\n
     * <span style=\"font-size: 18px;\">你好吗？哈哈哈，<strong>第一最好不想见 ，如此便和不相恋<\/strong>。<\/span>\r\n<p>\r\n
     * <span style=\"font-size: 18px;\">你好吗？哈哈哈，<strong>第一最好不想见 ，如此便和不相恋<\/strong>。<\/span>\r\n<p><span
     * style=\"font-size: 18px;\">你好吗？哈哈哈，<strong>第一最好不想见 ，如此便和不相恋<\/strong>。<\/span><\/p>","commentCount":0,"createTime":"May
     * 2, 2017 1:33:09 AM","isFollow":"1","isCollection":"0"}
     * commentlist : []
     */

    public NewInfoBean newInfo;
    public List<VideoComment> commentlist;

    public static class NewInfoBean {
        /**
         * newId : 402881ea5bc512b1015bc512b5110000
         * userid : 402881ea5bba10c9015bba10cb730000
         * nickname : zhj
         * userIcon : http://avatar.csdn.net/0/E/8/1_qq_36173712.jpg
         * title : 图文类新闻0
         * content : <p>
         * <span style="font-size: 18px;">你好吗？哈哈哈，<strong>第一最好不想见 ，如此便和不相恋</strong>。</span>
         * <p>
         * <span style="font-size: 18px;">你好吗？哈哈哈，<strong>第一最好不想见 ，如此便和不相恋</strong>。</span>
         * <p><span style="font-size: 18px;">你好吗？哈哈哈，<strong>第一最好不想见 ，如此便和不相恋</strong>。</span></p>
         * commentCount : 0
         * createTime : May 2, 2017 1:33:09 AM
         * isFollow : 1
         * isCollection : 0
         */

        public String newId;
        public String userid;
        public String nickname;
        public String userIcon;
        public String title;
        public String content;
        public int commentCount;
        public String createTime;
        public String isFollow;
        public String isCollection;
    }
}
