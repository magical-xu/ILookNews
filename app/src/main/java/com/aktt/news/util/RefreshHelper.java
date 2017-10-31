package com.aktt.news.util;

import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.aktt.news.AppConstant;
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
    private boolean rect;   // ItemDivider use rect

    /**
     * 刷新好帮手
     *
     * @param mRefreshLayout 刷新布局
     * @param mAdapter 适配器
     * @param mRecyclerView RecyclerView
     */
    public RefreshHelper(SmartRefreshLayout mRefreshLayout, BaseQuickAdapter mAdapter,
            RecyclerView mRecyclerView) {
        this(mRefreshLayout, mAdapter, mRecyclerView, false);
    }

    public RefreshHelper(SmartRefreshLayout mRefreshLayout, BaseQuickAdapter mAdapter,
            RecyclerView mRecyclerView, boolean rect) {
        this.mRefreshLayout = mRefreshLayout;
        this.mAdapter = mAdapter;
        this.mRecyclerView = mRecyclerView;
        this.rect = rect;
        init();
    }

    public abstract void onRequest(int page);

    private void init() {

        configRecyclerView();
        mAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_LEFT);
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
                rect ? DividerHelper.newRvDividerRect(mRecyclerView.getContext())
                        : DividerHelper.drawVerticalLine(mRecyclerView.getContext(), 0, false,
                                false));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
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

    public void setData(List<T> data,boolean hasMore) {
        //if (curPage == 1) {
        //    finishRefresh();
        //    mAdapter.setNewData(data);
        //} else {
        //    finishLoadmore();
        //    mAdapter.addData(data);
        //}

        if (curPage == 1) {
            finishRefresh();
            mAdapter.setNewData(data);
        } else {

            finishLoadmore();
            mAdapter.addData(data);

            if (!hasMore) {
                setNoMoreData();
                return;
            }

            if (data.size() < AppConstant.DEFAULT_PAGE_SIZE) {
                setNoMoreData();
                return;
            }
        }
    }
}
