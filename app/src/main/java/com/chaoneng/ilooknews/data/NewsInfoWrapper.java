package com.chaoneng.ilooknews.data;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;

/**
 * Created by magical on 2017/9/3.
 * Description : 新闻详情
 */

public class NewsInfoWrapper implements Serializable {

    public boolean haveNext;
    public String newsId;
    public String userid;
    public int newstype;

    @SerializedName(value = "newInfo", alternate = { "newsinfo", "newinfo" })
    //@SerializedName("newsinfo")
    public NewsInfo newInfo;
    public List<CommentBean> commentlist;
}
