package com.chaoneng.ilooknews.module.home.fragment;

import com.chaoneng.ilooknews.R;
import com.chaoneng.ilooknews.base.BaseFragment;

/**
 * Created by magical on 2017/8/14.
 * 首页新闻列表
 */

public class NewsListFragment extends BaseFragment {

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
