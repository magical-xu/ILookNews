package com.aktt.news.module.user.fragment;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.BindView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.aktt.news.AppConstant;
import com.aktt.news.R;
import com.aktt.news.api.UserService;
import com.aktt.news.base.BaseFragment;
import com.aktt.news.data.NewsInfo;
import com.aktt.news.data.NewsInfoListWrapper;
import com.aktt.news.instance.AccountManager;
import com.aktt.news.module.user.adapter.StateAdapter;
import com.aktt.news.net.callback.SimpleCallback;
import com.aktt.news.net.client.NetRequest;
import com.aktt.news.net.data.HttpResult;
import com.aktt.news.util.IntentHelper;
import com.aktt.news.util.RefreshHelper;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import java.util.List;
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
                loadData(page);
            }
        };
        mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                if (view.getId() == R.id.tv_share) {
                    List<NewsInfo> data = mAdapter.getData();
                    if (data.size() > position) {
                        NewsInfo newsInfo = data.get(position);

                        IntentHelper.openShareBottomPage(getActivity(), newsInfo.newId,
                                newsInfo.newstype, pageUid);
                    }
                }
            }
        });

        userService = NetRequest.getInstance().create(UserService.class);

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
                    mRefreshHelper.finishRefresh();
                    mAdapter.setEmptyView(mEmptyView);
                    return;
                }

                //noinspection unchecked
                mRefreshHelper.setData(data.list, data.haveNext);
            }

            @Override
            public void onFail(String code, String errorMsg) {
                mRefreshHelper.onFail();
                onSimpleError(errorMsg);
            }
        });
    }
}
