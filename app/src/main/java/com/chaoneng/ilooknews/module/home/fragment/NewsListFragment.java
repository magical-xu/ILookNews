package com.chaoneng.ilooknews.module.home.fragment;

import android.os.Bundle;
import com.chaoneng.ilooknews.R;
import com.chaoneng.ilooknews.base.BaseFragment;

/**
 * Created by magical on 2017/8/14.
 * 首页新闻列表
 */

public class NewsListFragment extends BaseFragment {

  private static final String PAGE_TYPE = "PAGE_TYPE";

  public static NewsListFragment newInstance(String type) {
    NewsListFragment fragment = new NewsListFragment();
    Bundle bundle = new Bundle();
    bundle.putString(PAGE_TYPE, type);
    fragment.setArguments(bundle);
    return fragment;
  }

  @Override
  protected void beginLoadData() {

  }

  @Override
  protected void doInit() {

  }

  @Override
  protected boolean isNeedShowLoadingView() {
    return false;
  }

  @Override
  protected int getLayoutName() {
    return R.layout.fragment_home_news_list;
  }
}
