package com.aktt.news.module.user.fragment;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.BindView;
import com.aktt.news.AppConstant;
import com.aktt.news.R;
import com.aktt.news.api.UserService;
import com.aktt.news.base.BaseFragment;
import com.aktt.news.module.user.adapter.BrokeNewsListAdapter;
import com.aktt.news.module.user.data.BrokeListWrapper;
import com.aktt.news.net.callback.SimpleCallback;
import com.aktt.news.net.client.NetRequest;
import com.aktt.news.net.data.HttpResult;
import com.aktt.news.util.IntentHelper;
import com.aktt.news.util.RefreshHelper;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import retrofit2.Call;

/**
 * Created by magical on 2017/8/22.
 * Description : 爆料列表 界面
 */

public class BrokeNewsListFragment extends BaseFragment {

    @BindView(R.id.id_recycler) RecyclerView mRecyclerView;
    @BindView(R.id.id_refresh_layout) SmartRefreshLayout mRefreshLayout;

    private RefreshHelper mRefreshHelper;
    private BrokeNewsListAdapter mAdapter;
    private UserService userService;
    private String pageUid;
    private int pageType;

    private View mEmptyView;

    public static BrokeNewsListFragment getInstance(String uid, int pageType) {

        BrokeNewsListFragment fragment = new BrokeNewsListFragment();
        Bundle bundle = new Bundle();
        bundle.putString(IntentHelper.PARAMS_ONE, uid);
        bundle.putInt(IntentHelper.PARAMS_TWO, pageType);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void beginLoadData() {
    }

    @Override
    protected void lazyLoad() {
        super.lazyLoad();

        mRefreshHelper.beginLoadData();
    }

    @Override
    protected void doInit() {

        Bundle arguments = getArguments();
        if (null != arguments) {
            pageUid = arguments.getString(IntentHelper.PARAMS_ONE);
            pageType = arguments.getInt(IntentHelper.PARAMS_TWO, 0);
        }

        userService = NetRequest.getInstance().create(UserService.class);

        mAdapter = new BrokeNewsListAdapter(R.layout.item_user_center_broke_news);
        mRefreshHelper = new RefreshHelper(mRefreshLayout, mAdapter, mRecyclerView, true) {
            @Override
            public void onRequest(int page) {
                loadData(page);
            }
        };

        mEmptyView = LayoutInflater.from(getActivity())
                .inflate(R.layout.base_empty_view, (ViewGroup) mRecyclerView.getParent(), false);
    }

    @Override
    protected int getLayoutName() {
        return R.layout.simple_recycler_list;
    }

    public void loadData(final int page) {

        if (TextUtils.isEmpty(pageUid)) {
            return;
        }

        showLoading();
        Call<HttpResult<BrokeListWrapper>> call;
        if (pageType == 0) {
            //个人爆料
            call = userService.getBaoLiaoList(pageUid, page, AppConstant.DEFAULT_PAGE_SIZE);
        } else {
            //所有人爆料
            call = userService.getBaoLiaoListAll(pageUid, page, AppConstant.DEFAULT_PAGE_SIZE);
        }

        call.enqueue(new SimpleCallback<BrokeListWrapper>() {
            @Override
            public void onSuccess(BrokeListWrapper data) {

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
