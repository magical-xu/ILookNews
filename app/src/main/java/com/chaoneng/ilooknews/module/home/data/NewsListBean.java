package com.chaoneng.ilooknews.module.home.data;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.google.gson.annotations.SerializedName;

/**
 * Created by magical on 17/8/20.
 * Description :
 * 1视频类 2 图文类 3 图片类 4 广告类 5 跳转类
 */

public class NewsListBean implements MultiItemEntity {

  public static final int VIDEO = 1;
  public static final int TEXT = 2;
  public static final int SINGLE_IMG = 3;
  public static final int TWO_IMG = 4;
  public static final int THREE_IMG = 5;

  @SerializedName("newstype")
  public int type;

  @Override
  public int getItemType() {
    return type;
  }

  public void setItemType(int type) {
    this.type = type;
  }

  public String newId;
  public String userid;
  public String nickname;
  public String userIcon;
  public String title;
  public String coverpic;
  public int commentCount;
  public int pic_count;
  public String createTime;
  public int weight;
  public int adtype;
  public String appName;
}
