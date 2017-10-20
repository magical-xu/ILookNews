package com.aktt.news.data;

import java.io.Serializable;

/**
 * Created by magical on 17/8/17.
 * Description : 基础用户信息
 */

public class BaseUser implements Serializable {

    /**
     * id : 402881e75e0008c3015e0008e1b60000
     * username : zhj4
     * pwd :
     * gender : 0
     * place_lon : 0
     * place_lat : 0
     * province :
     * city :
     * address :
     * createTime : 2017-08-20 22:25:35
     * status : 1
     * isVip : 0
     * lastLoginTime : 2017-08-20 22:25:35
     * thisTime : 2017-08-20 22:26:29
     * dongTai : 0
     * lastTitleVideo :
     * followedNum : 0
     * fansNum : 0
     * visitorsNum : 0
     * introduce :
     * registerType : 1
     * mobile_code : 0
     * qq_code : 0
     * microblog_code : 0
     * wechat_code : 0
     */

    public String id;
    public String username;
    public String pwd;
    public int gender;
    public int place_lon;
    public int place_lat;
    public String province;
    public String city;
    public String address;
    public String createTime;
    public int status;
    public int isVip;
    public String lastLoginTime;
    public String thisTime;
    public int dongTai;
    public String lastTitleVideo;
    public int followedNum;
    public int fansNum;
    public int visitorsNum;
    public String introduce;
    public int registerType;
    public int mobile_code;
    public int qq_code;
    public int microblog_code;
    public int wechat_code;

    //add by magical to test
    public String icon;
}
