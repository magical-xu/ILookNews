package com.chaoneng.ilooknews.module.home.data;

import java.util.List;

/**
 * Created by magical on 17/8/28.
 * Description :
 */

public class NewsListWrapper {

  public int page;
  public int pageSize;
  public String userid;

  public List<NewsListBean> list;
  public boolean havePage;
  public String cId;
}
