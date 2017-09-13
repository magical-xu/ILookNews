package com.chaoneng.ilooknews.module.search.data;

import java.util.List;

/**
 * Created by magical on 17/9/12.
 * Description :
 */

public class SearchHistory {

    /**
     * pagesize : 10
     * page : 1
     * list : [{"userId":"402881ea5bba10c9015bba10cb730000","keyText":"图片","createtime":"2017-09-12
     * 10:32:30"},{"userId":"402881ea5bba10c9015bba10cb730000","keyText":"图片","createtime":"2017-09-10
     * 23:14:39"},{"userId":"402881ea5bba10c9015bba10cb730000","keyText":"图片","createtime":"2017-08-17
     * 00:11:40"},{"userId":"402881ea5bba10c9015bba10cb730000","keyText":"图片","createtime":"2017-08-17
     * 00:07:52"},{"userId":"402881ea5bba10c9015bba10cb730000","keyText":"图片","createtime":"2017-08-17
     * 00:04:08"},{"userId":"402881ea5bba10c9015bba10cb730000","keyText":"图片","createtime":"2017-08-16
     * 23:52:22"},{"userId":"402881ea5bba10c9015bba10cb730000","keyText":"图片","createtime":"2017-08-16
     * 23:46:43"},{"userId":"402881ea5bba10c9015bba10cb730000","keyText":"图片","createtime":"2017-08-16
     * 23:45:47"},{"userId":"402881ea5bba10c9015bba10cb730000","keyText":"图片","createtime":"2017-08-16
     * 23:44:49"},{"userId":"402881ea5bba10c9015bba10cb730000","keyText":"图片","createtime":"2017-08-16
     * 23:43:29"},{"userId":"402881ea5bba10c9015bba10cb730000","keyText":"图片","createtime":"2017-08-16
     * 23:40:57"}]
     * userid : 402881ea5bba10c9015bba10cb730000
     */

    public int pagesize;
    public int page;
    public String userid;
    public List<ListBean> list;

    public static class ListBean {
        /**
         * userId : 402881ea5bba10c9015bba10cb730000
         * keyText : 图片
         * createtime : 2017-09-12 10:32:30
         */

        public String userId;
        public String keyText;
        public String createtime;
    }
}
