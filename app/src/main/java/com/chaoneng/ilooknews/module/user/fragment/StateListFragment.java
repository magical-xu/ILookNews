package com.chaoneng.ilooknews.module.user.fragment;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.BindView;
import com.chaoneng.ilooknews.AppConstant;
import com.chaoneng.ilooknews.R;
import com.chaoneng.ilooknews.api.UserService;
import com.chaoneng.ilooknews.base.BaseFragment;
import com.chaoneng.ilooknews.data.MockServer;
import com.chaoneng.ilooknews.data.NewsInfoListWrapper;
import com.chaoneng.ilooknews.instance.AccountManager;
import com.chaoneng.ilooknews.module.user.adapter.StateAdapter;
import com.chaoneng.ilooknews.net.callback.SimpleCallback;
import com.chaoneng.ilooknews.net.client.NetRequest;
import com.chaoneng.ilooknews.net.data.HttpResult;
import com.chaoneng.ilooknews.util.IntentHelper;
import com.chaoneng.ilooknews.util.RefreshHelper;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import retrofit2.Call;

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
    private UserService userService;
    private String pageUid;

    private View mEmptyView;

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

        Bundle arguments = getArguments();
        pageUid = arguments.getString(IntentHelper.PARAMS_ONE);

        mAdapter = new StateAdapter(R.layout.item_user_center_state);
        mRefreshHelper = new RefreshHelper(mRefreshLayout, mAdapter, mRecyclerView, true) {
            @Override
            public void onRequest(int page) {
                //mockServer.mockGankCall(page, MockServer.Type.USER_STATE);
                loadData(page);
            }
        };

        userService = NetRequest.getInstance().create(UserService.class);
        //mockServer = MockServer.getInstance();
        //mockServer.init(mRefreshHelper);

        mEmptyView = LayoutInflater.from(getActivity())
                .inflate(R.layout.base_empty_view, (ViewGroup) mRecyclerView.getParent(), false);
    }

    @Override
    protected int getLayoutName() {
        return R.layout.simple_recycler_list;
    }

    private void loadData(final int page) {

        if (TextUtils.isEmpty(pageUid)) {
            return;
        }

        String loginUserId = AccountManager.getInstance().getUserId();
        if (TextUtils.isEmpty(loginUserId)) {
            loginUserId = "";
        }

        showLoading();
        Call<HttpResult<NewsInfoListWrapper>> call =
                userService.getDongTaiList(loginUserId, pageUid, page,
                        AppConstant.DEFAULT_PAGE_SIZE);
        call.enqueue(new SimpleCallback<NewsInfoListWrapper>() {
            @Override
            public void onSuccess(NewsInfoListWrapper data) {

                hideLoading();
                if (page == 1 && (null == data || null == data.list || data.list.size() == 0)) {
                    mAdapter.setEmptyView(mEmptyView);
                    return;
                }

                if (page == 1) {

                    mRefreshHelper.finishRefresh();
                    mAdapter.setNewData(data.list);
                } else {

                    if (!data.haveNext) {
                        mRefreshHelper.setNoMoreData();
                        return;
                    }

                    if (data.list.size() < AppConstant.DEFAULT_PAGE_SIZE) {
                        mRefreshHelper.setNoMoreData();
                        return;
                    }

                    mRefreshHelper.finishLoadmore();
                    mAdapter.addData(data.list);
                }
            }

            @Override
            public void onFail(String code, String errorMsg) {

                mRefreshHelper.onFail();
                onSimpleError(errorMsg);
            }
        });
    }
}
