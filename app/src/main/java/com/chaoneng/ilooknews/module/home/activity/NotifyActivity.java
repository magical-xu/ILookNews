package com.chaoneng.ilooknews.module.home.activity;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import butterknife.BindView;
import com.chaoneng.ilooknews.R;
import com.chaoneng.ilooknews.base.BaseActivity;
import com.chaoneng.ilooknews.data.MockServer;
import com.chaoneng.ilooknews.module.home.adapter.NotifyAdapter;
import com.chaoneng.ilooknews.util.RefreshHelper;
import com.chaoneng.ilooknews.widget.ILookTitleBar;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

/**
 * Created by magical on 17/8/17.
 * Description : 我的消息界面
 */

public class NotifyActivity extends BaseActivity {

  @BindView(R.id.id_recycler) RecyclerView mRecyclerView;
  @BindView(R.id.id_refresh_layout) SmartRefreshLayout mRefreshLayout;

  private NotifyAdapter mAdapter;
  private RefreshHelper mRefreshHelper;

  @Override
  public int getLayoutId() {
    return R.layout.activity_notify;
  }

  @Override
  protected boolean addTitleBar() {
    return true;
  }

  @Override
  public void handleChildPage(Bundle savedInstanceState) {

    configTitle();
    init();
  }

  private void configTitle() {

    mTitleBar.setTitle("我的消息");
    mTitleBar.setTitleListener(new ILookTitleBar.TitleCallbackAdapter() {
      @Override
      public void onClickLeft(View view) {
        super.onClickLeft(view);
        finish();
      }
    });
  }

  private void init() {

    mAdapter = new NotifyAdapter(R.layout.item_home_notify);
    mRefreshHelper = new RefreshHelper(mRefreshLayout, mAdapter, mRecyclerView) {
      @Override
      public void onRequest(int page) {
        MockServer.getInstance().mockGankCall(page, MockServer.Type.NOTIFY);
      }
    };
    MockServer.getInstance().init(mRefreshHelper);
    mRefreshHelper.beginLoadData();
  }
}
