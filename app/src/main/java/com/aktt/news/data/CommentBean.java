package com.aktt.news.data;

import java.io.Serializable;

/**
 * Created by magical on 17/8/20.
 * Description : 统一 评论实体
 */

public class CommentBean implements Serializable {

    /**
     * cid : 8a10fb5e5e258bec015e27ebd46e0001
     * userid : 402881ea5bbdf23f015bbdf2418b0000
     * nickname : zhj3
     * icon : http://avatar.csdn.net/0/E/8/1_qq_36173712.jpg
     * icon_small : http://avatar.csdn.net/0/E/8/1_qq_36173712.jpg
     * newId : 402881ea5bc512b1015bc512b5110000
     * commentCount : 0
     * newstype : 2
     * createDate : 2017-08-28 16:18:40
     * careCount : 0
     * parentId : 0
     * text : åååï¼ææ¥è¯è®ºå¦
     * isFollow : 0
     */

    public String cid;
    public String userid;
    public String nickname;
    public String icon;
    public String icon_small;
    public String newId;
    public int commentCount;
    public int newstype;
    public String createDate;
    public int careCount;
    public String parentId;
    public String text;
    public String isFollow;
}
