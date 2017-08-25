package com.chaoneng.ilooknews.module.focus;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import butterknife.BindView;
import com.chaoneng.ilooknews.R;
import com.chaoneng.ilooknews.base.BaseTitleFragment;
import com.chaoneng.ilooknews.data.MockServer;
import com.chaoneng.ilooknews.module.focus.adapter.FocusAdapter;
import com.chaoneng.ilooknews.util.RefreshHelper;
import com.chaoneng.ilooknews.widget.ILookTitleBar;
import com.magicalxu.library.blankj.ToastUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

/**
 * Created by magical on 17/8/15.
 * Description : 主页 - 关注
 */

public class FocusMainFragment extends BaseTitleFragment {

  @BindView(R.id.id_recycler) RecyclerView mRecyclerView;
  @BindView(R.id.id_refresh_layout) SmartRefreshLayout mRefreshLayout;

  private FocusAdapter mAdapter;
  private RefreshHelper mRefreshHelper;
  private MockServer mockServer;

  @Override
  protected void beginLoadData() {
    mRefreshHelper.beginLoadData();
  }

  @Override
  protected boolean isNeedShowLoadingView() {
    return false;
  }

  @Override
  public void init() {

    mTitleBar.setTitleImage(R.drawable.img_focus_title)
        .setRightImage(R.drawable.ic_care_plus_blue)
        .hideLeftImage()
        .setTitleListener(new ILookTitleBar.TitleCallbackAdapter() {
          @Override
          public void onClickRightImage(View view) {
            super.onClickRightImage(view);
            ToastUtils.showShort("加關注");
          }
        });

    config();
  }

  @Override
  public int getSubLayout() {
    return R.layout.simple_recycler_list;
  }

  private void config() {

    mAdapter = new FocusAdapter(R.layout.item_main_focus);
    mRecyclerView.setAdapter(mAdapter);
    mRefreshHelper = new RefreshHelper(mRefreshLayout, mAdapter, mRecyclerView) {
      @Override
      public void onRequest(int page) {
        mockServer.mockGankCall(page, MockServer.Type.FOCUS);
      }
    };
    mockServer = MockServer.getInstance();
    mockServer.init(mRefreshHelper);
  }
}
