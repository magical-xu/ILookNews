package com.chaoneng.ilooknews.module.user.fragment;

import android.support.v7.widget.RecyclerView;
import butterknife.BindView;
import com.chaoneng.ilooknews.R;
import com.chaoneng.ilooknews.base.BaseFragment;
import com.chaoneng.ilooknews.data.MockServer;
import com.chaoneng.ilooknews.module.user.adapter.BrokeNewsListAdapter;
import com.chaoneng.ilooknews.util.RefreshHelper;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

/**
 * Created by magical on 2017/8/22.
 * Description : 爆料界面
 */

public class BrokeNewsListFragment extends BaseFragment {

  @BindView(R.id.id_recycler) RecyclerView mRecyclerView;
  @BindView(R.id.id_refresh_layout) SmartRefreshLayout mRefreshLayout;

  private RefreshHelper mRefreshHelper;
  private BrokeNewsListAdapter mAdapter;

  @Override
  protected void beginLoadData() {

    mRefreshHelper.beginLoadData();
  }

  @Override
  protected void doInit() {

    mAdapter = new BrokeNewsListAdapter(R.layout.item_user_center_broke_news);
    mRefreshHelper = new RefreshHelper(mRefreshLayout, mAdapter, mRecyclerView, true) {
      @Override
      public void onRequest(int page) {
        MockServer.getInstance().mockGankCall(page, MockServer.Type.USER_BROKE);
      }
    };
    MockServer.getInstance().init(mRefreshHelper);
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
