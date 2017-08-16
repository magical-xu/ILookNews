package com.chaoneng.ilooknews.data;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import java.io.Serializable;

/**
 * Created by magical on 17/8/16.
 * Description :
 */

public class Channel implements MultiItemEntity, Serializable {

  public static final int TYPE_MY = 1;              //我的頻道
  public static final int TYPE_OTHER = 2;           //推荐频道

  public static final int TYPE_MY_CHANNEL = 3;      //我的频道列表
  public static final int TYPE_OTHER_CHANNEL = 4;   //推荐频道列表

  public String title;

  public String code;     //用于 网络请求代码

  public int type;        //界面区分类型

  public boolean access;  //是否为默认 tab项

  public Channel(int type, String title, String code) {
    this.type = type;
    this.title = title;
    this.code = code;
  }

  public Channel(int type, String title, String code, boolean access) {
    this(type, title, code);
    this.access = access;
  }

  @Override
  public int getItemType() {
    return type;
  }

  public void setItemType(int type) {
    this.type = type;
  }
}
