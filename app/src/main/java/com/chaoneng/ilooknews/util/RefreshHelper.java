package com.chaoneng.ilooknews.util;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import java.util.List;

/**
 * Created by magical on 17/8/19.
 * Description : 下拉刷新、上拉加载、配置RecyclerView的帮助类
 */

public abstract class RefreshHelper<T> {

  private SmartRefreshLayout mRefreshLayout;
  private BaseQuickAdapter<T, BaseViewHolder> mAdapter;
  private RecyclerView mRecyclerView;

  private int curPage;

  /**
   * 刷新好帮手
   *
   * @param mRefreshLayout 刷新布局
   * @param mAdapter 适配器
   * @param mRecyclerView RecyclerView
   */
  public RefreshHelper(SmartRefreshLayout mRefreshLayout, BaseQuickAdapter mAdapter,
      RecyclerView mRecyclerView) {
    this.mRefreshLayout = mRefreshLayout;
    this.mAdapter = mAdapter;
    this.mRecyclerView = mRecyclerView;
    init();
  }

  public abstract void onRequest(int page);

  private void init() {

    configRecyclerView();
    mRecyclerView.setAdapter(mAdapter);
    mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
      @Override
      public void onRefresh(RefreshLayout refreshlayout) {
        curPage = 1;
        onRequest(curPage);
      }
    }).setOnLoadmoreListener(new OnLoadmoreListener() {
      @Override
      public void onLoadmore(RefreshLayout refreshlayout) {
        curPage++;
        onRequest(curPage);
      }
    });
  }

  private void configRecyclerView() {

    LinearLayoutManager manager = new LinearLayoutManager(mRecyclerView.getContext());
    mRecyclerView.setLayoutManager(manager);
    mRecyclerView.setHasFixedSize(true);
    mRecyclerView.addItemDecoration(
        DividerHelper.drawVerticalLine(mRecyclerView.getContext(), 0, false, false));
  }

  public void beginLoadData() {
    curPage = 1;
    onRequest(curPage);
  }

  public void setNoMoreData() {
    mRefreshLayout.finishLoadmore();
    mRefreshLayout.setLoadmoreFinished(true);
  }

  public void finishRefresh() {
    mRefreshLayout.finishRefresh();
    mRefreshLayout.setLoadmoreFinished(false);
  }

  public void finishLoadmore() {
    mRefreshLayout.finishLoadmore();
  }

  public int getCurPage() {
    return curPage;
  }

  public void setCurPage(int page) {
    this.curPage = page;
  }

  public void onFail() {
    if (curPage == 1) {
      finishRefresh();
    } else {
      finishLoadmore();
    }
  }

  public void setData(List<T> data) {
    if (curPage == 1) {
      finishRefresh();
      mAdapter.setNewData(data);
    } else {
      finishLoadmore();
      mAdapter.addData(data);
    }
  }

  public boolean mockNoMoreData() {
    if (curPage == 4) {
      setNoMoreData();
      curPage = 3;
      return true;
    }
    return false;
  }
}
