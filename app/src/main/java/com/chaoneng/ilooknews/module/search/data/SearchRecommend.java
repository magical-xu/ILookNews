package com.chaoneng.ilooknews.module.search.data;

import java.util.List;

/**
 * Created by magical on 17/9/18.
 * Description : 搜索推荐
 */

public class SearchRecommend {

    /**
     * pagesize : 10
     * page : 1
     * list : [{"keyText":"范冰冰","weight":0},{"keyText":"极客学院","weight":0},{"keyText":"人月神话","weight":0},{"keyText":"基础与案例开发","weight":0},{"keyText":"朗读者","weight":0},{"keyText":"美丽的神话","weight":0},{"keyText":"哈哈哈","weight":0},{"keyText":"啦啦","weight":0},{"keyText":"生活","weight":0},{"keyText":"美好升高","weight":0}]
     * havePage : false
     */

    public int pagesize;
    public int page;
    public boolean havePage;
    public List<ListBean> list;

    public static class ListBean {
        /**
         * keyText : 范冰冰
         * weight : 0
         */

        public String keyText;
        public int weight;
    }
}
