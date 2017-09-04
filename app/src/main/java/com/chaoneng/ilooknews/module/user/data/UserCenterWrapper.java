package com.chaoneng.ilooknews.module.user.data;

import java.io.Serializable;
import java.util.List;

/**
 * Created by magical on 17/9/4.
 * Description : 个人中心
 */

public class UserCenterWrapper implements Serializable {

  /**
   * pagesize : 10
   * nickname :
   * userIcon :
   * userid : 402881e55dc28260015dc2827ba10000
   * introduce :
   * fansNum : 1
   * visitorsNum : 0
   * city :
   * notReadCount : 4
   * followlist : [{"target_id":"402881e55dc2873c015dc28741d40000","isFollow":"1","gender":0,"isVip":0,"lastTitleVideo":"","introduce":""}]
   * address :
   * page : 1
   * province :
   * gender : 0
   * lastLoginTime : 2017-08-08 23:41:57
   * followedNum : 1
   */

  public int pagesize;
  public String nickname;
  public String userIcon;
  public String userid;
  public String introduce;
  public int fansNum;
  public int visitorsNum;
  public String city;
  public int notReadCount;
  public String address;
  public int page;
  public String province;
  public int gender;
  public String lastLoginTime;
  public int followedNum;
  public List<FollowlistBean> followlist;

  public static class FollowlistBean {
    /**
     * target_id : 402881e55dc2873c015dc28741d40000
     * isFollow : 1
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
  }
}
