package com.aktt.news.module.focus.data;

import java.io.Serializable;
import java.util.List;

/**
 * Created by magical on 2017/9/3.
 * Description : 第三个 Tab 未关注列表
 */

public class FocusWrapper implements Serializable {

    /**
     * haveNext : false
     * page : 1
     * pageSize : 20
     * myid : 402881ea5bba10c9015bba10cb730000
     */

    public List<FocusBean> list;
    public boolean haveNext;
    public int page;
    public int pageSize;
    public String myid;
}
