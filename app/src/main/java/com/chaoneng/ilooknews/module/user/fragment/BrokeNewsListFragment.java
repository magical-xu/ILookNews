package com.chaoneng.ilooknews.module.user.fragment;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import butterknife.BindView;
import com.chaoneng.ilooknews.AppConstant;
import com.chaoneng.ilooknews.R;
import com.chaoneng.ilooknews.api.UserService;
import com.chaoneng.ilooknews.base.BaseFragment;
import com.chaoneng.ilooknews.data.MockServer;
import com.chaoneng.ilooknews.instance.AccountManager;
import com.chaoneng.ilooknews.module.user.adapter.BrokeNewsListAdapter;
import com.chaoneng.ilooknews.module.user.data.BrokeListWrapper;
import com.chaoneng.ilooknews.net.callback.SimpleCallback;
import com.chaoneng.ilooknews.net.client.NetRequest;
import com.chaoneng.ilooknews.net.data.HttpResult;
import com.chaoneng.ilooknews.util.IntentHelper;
import com.chaoneng.ilooknews.util.RefreshHelper;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import retrofit2.Call;

/**
 * Created by magical on 2017/8/22.
 * Description : 爆料界面
 */

public class BrokeNewsListFragment extends BaseFragment {

    @BindView(R.id.id_recycler) RecyclerView mRecyclerView;
    @BindView(R.id.id_refresh_layout) SmartRefreshLayout mRefreshLayout;

    private RefreshHelper mRefreshHelper;
    private BrokeNewsListAdapter mAdapter;
    private MockServer mockServer;
    private UserService userService;
    private String pageUid;

    public static BrokeNewsListFragment getInstance(String uid) {

        BrokeNewsListFragment fragment = new BrokeNewsListFragment();
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
        if (null != arguments) {
            pageUid = arguments.getString(IntentHelper.PARAMS_ONE);
        }

        userService = NetRequest.getInstance().create(UserService.class);

        mAdapter = new BrokeNewsListAdapter(R.layout.item_user_center_broke_news);
        mRefreshHelper = new RefreshHelper(mRefreshLayout, mAdapter, mRecyclerView, true) {
            @Override
            public void onRequest(int page) {
                mockServer.mockGankCall(page, MockServer.Type.USER_BROKE);
            }
        };
        mockServer = MockServer.getInstance();
        mockServer.init(mRefreshHelper);

        loadData(1);
    }

    @Override
    protected int getLayoutName() {
        return R.layout.simple_recycler_list;
    }

    public void loadData(int page) {

        String userId = AccountManager.getInstance().getUserId();
        if (TextUtils.isEmpty(userId) || TextUtils.isEmpty(pageUid)) {
            return;
        }
        // TODO: 2017/9/14 替换测试的uid
        Call<HttpResult<BrokeListWrapper>> call =
                userService.getBaoLiaoList(AppConstant.TEST_USER_ID, page,
                        AppConstant.DEFAULT_PAGE_SIZE);
        call.enqueue(new SimpleCallback<BrokeListWrapper>() {
            @Override
            public void onSuccess(BrokeListWrapper data) {

            }

            @Override
            public void onFail(String code, String errorMsg) {

            }
        });
    }
}
