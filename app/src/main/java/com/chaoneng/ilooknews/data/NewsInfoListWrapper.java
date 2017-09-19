package com.chaoneng.ilooknews.data;

import java.util.List;

/**
 * Created by magical on 17/9/19.
 * Description :
 */

public class NewsInfoListWrapper {

    /**
     * nickname : zhj
     * haveNext : true
     * pageSize : 10
     * userIcon : http://avatar.csdn.net/0/E/8/1_qq_36173712.jpg
     * introduce :
     * userid : 402881e55dc28260015dc2827ba10000
     * targetId : 402881ea5bba10c9015bba10cb730000
     * fansNum : 0
     * city :
     * page : 1
     * address :
     * province :
     * gender : 1
     * isfollow : 0
     * followedNum : 2
     */

    public String nickname;
    public boolean haveNext;
    public int pageSize;
    public String userIcon;
    public String introduce;
    public String userid;
    public String targetId;
    public int fansNum;
    public String city;
    public int page;
    public String address;
    public String province;
    public String gender;
    public int isfollow;
    public int followedNum;

    public List<NewsInfo> list;
}
