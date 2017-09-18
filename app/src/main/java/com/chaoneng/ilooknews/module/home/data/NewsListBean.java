package com.chaoneng.ilooknews.module.home.data;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Created by magical on 17/8/20.
 * Description :
 * 1视频类 2 图文类 3 图片类 4 广告类 5 跳转类
 * picStyle : 1:无图 2:三张图 3:一张图 4：视频类的图
 */

public class NewsListBean implements MultiItemEntity {

    public static final int VIDEO = 1;
    public static final int TEXT = 2;
    public static final int IMAGE = 3;
    public static final int AD = 4;
    public static final int HTML = 5;

    @SerializedName("newstype") public int type;

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
    public List<String> coverpic;
    public int commentCount;
    public int pic_count;
    public String createTime;
    public int weight;
    public int adtype;
    public String appName;

    public String picStyle;
}
