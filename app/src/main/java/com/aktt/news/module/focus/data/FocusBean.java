package com.aktt.news.module.focus.data;

import java.io.Serializable;

/**
 * Created by magical on 17/8/19.
 * Description : 未关注人列表
 */

public class FocusBean implements Serializable {

    /**
     * target_id : 402881e55dc289e6015dc289eb300000
     * isFollow : 0
     * gender : 0
     * isVip : 0
     * lastTitleVideo :
     * introduce :
     */

    public String target_id;
    public String isFollow;
    public int gender;
    public int isVip;
    public String lastTitleVideo;
    public String introduce;

    public String icon;
    public String nickname;

    public String lastOneNewstitle;
    public String lastTwoNewsTitle;
}
