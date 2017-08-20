package com.chaoneng.ilooknews.module.home.fragment;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import butterknife.BindView;
import com.chaoneng.ilooknews.R;
import com.chaoneng.ilooknews.base.BaseFragment;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import static com.chaoneng.ilooknews.AppConstant.PAGE_TYPE;

/**
 * Created by magical on 2017/8/14.
 * 首页新闻列表
 */

public class NewsListFragment extends BaseFragment {

  @BindView(R.id.id_recycler) RecyclerView mRecyclerView;
  @BindView(R.id.id_refresh_layout) SmartRefreshLayout mRefreshLayout;



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
    return R.layout.simple_recycler_list;
  }
}
