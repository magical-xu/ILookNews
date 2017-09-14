package com.chaoneng.ilooknews.module.user.fragment;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import butterknife.BindView;
import com.chaoneng.ilooknews.R;
import com.chaoneng.ilooknews.base.BaseFragment;
import com.chaoneng.ilooknews.data.MockServer;
import com.chaoneng.ilooknews.module.user.adapter.StateAdapter;
import com.chaoneng.ilooknews.util.IntentHelper;
import com.chaoneng.ilooknews.util.RefreshHelper;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

/**
 * Created by magical on 17/8/24.
 * Description : 动态列表界面
 */

public class StateListFragment extends BaseFragment {

    @BindView(R.id.id_recycler) RecyclerView mRecyclerView;
    @BindView(R.id.id_refresh_layout) SmartRefreshLayout mRefreshLayout;

    private RefreshHelper mRefreshHelper;
    private StateAdapter mAdapter;
    private MockServer mockServer;
    private String pageUid;

    public static StateListFragment getInstance(String uid) {

        StateListFragment fragment = new StateListFragment();
        Bundle bundle = new Bundle();
        bundle.putString(IntentHelper.PARAMS_ONE, uid);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void beginLoadData() {
        mRefreshHelper.beginLoadData();
    }

    @Override
    protected void doInit() {

        mAdapter = new StateAdapter(R.layout.item_user_center_state);
        mRefreshHelper = new RefreshHelper(mRefreshLayout, mAdapter, mRecyclerView, true) {
            @Override
            public void onRequest(int page) {
                mockServer.mockGankCall(page, MockServer.Type.USER_STATE);
            }
        };
        mockServer = MockServer.getInstance();
        mockServer.init(mRefreshHelper);
    }

    @Override
    protected int getLayoutName() {
        return R.layout.simple_recycler_list;
    }
}
