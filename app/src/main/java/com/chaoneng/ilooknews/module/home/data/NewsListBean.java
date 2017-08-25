package com.chaoneng.ilooknews.module.home.data;

import com.chad.library.adapter.base.entity.MultiItemEntity;

/**
 * Created by magical on 17/8/20.
 * Description :
 */

public class NewsListBean implements MultiItemEntity {

  public static final int TEXT = 1;
  public static final int SINGLE_IMG = 2;
  public static final int TWO_IMG = 3;
  public static final int THREE_IMG = 4;
  public static final int VIDEO = 5;

  public int type;

  @Override
  public int getItemType() {
    return type;
  }

  public void setItemType(int type) {
    this.type = type;
  }
}
