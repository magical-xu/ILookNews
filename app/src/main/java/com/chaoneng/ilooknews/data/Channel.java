package com.chaoneng.ilooknews.data;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.google.gson.annotations.SerializedName;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by magical on 17/8/16.
 * Description : 频道
 */
@Entity public class Channel implements MultiItemEntity {

  public static final int TYPE_MY = 1;              //我的頻道
  public static final int TYPE_OTHER = 2;           //推荐频道

  public static final int TYPE_MY_CHANNEL = 3;      //我的频道列表
  public static final int TYPE_OTHER_CHANNEL = 4;   //推荐频道列表

    @Id(autoincrement = true) public Long kId;

  @SerializedName("id") public String code;     //用于 网络请求代码   cid

  @SerializedName("menuName") public String title;

  public String parentId;
  public String parentName;
  public int style;
  public String createTime;
  public int sort;
  public String ishot;
  public int isshow;

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

    @Generated(hash = 870412446)
    public Channel(Long kId, String code, String title, String parentId, String parentName,
            int style, String createTime, int sort, String ishot, int isshow, int type,
            boolean access) {
        this.kId = kId;
        this.code = code;
        this.title = title;
        this.parentId = parentId;
        this.parentName = parentName;
        this.style = style;
        this.createTime = createTime;
        this.sort = sort;
        this.ishot = ishot;
        this.isshow = isshow;
        this.type = type;
        this.access = access;
    }

    @Generated(hash = 459652974)
    public Channel() {
    }

  @Override
  public int getItemType() {
    return type;
  }

  public void setItemType(int type) {
    this.type = type;
  }

    public Long getKId() {
        return this.kId;
    }

    public void setKId(Long kId) {
        this.kId = kId;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getParentId() {
        return this.parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getParentName() {
        return this.parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public int getStyle() {
        return this.style;
    }

    public void setStyle(int style) {
        this.style = style;
    }

    public String getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public int getSort() {
        return this.sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public String getIshot() {
        return this.ishot;
    }

    public void setIshot(String ishot) {
        this.ishot = ishot;
    }

    public int getIsshow() {
        return this.isshow;
    }

    public void setIsshow(int isshow) {
        this.isshow = isshow;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean getAccess() {
        return this.access;
    }

    public void setAccess(boolean access) {
        this.access = access;
  }
}
