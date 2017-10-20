package com.aktt.news.module.focus;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import butterknife.BindView;
import com.aktt.news.AppConstant;
import com.aktt.news.R;
import com.aktt.news.api.UserService;
import com.aktt.news.base.BaseActivity;
import com.aktt.news.instance.AccountManager;
import com.aktt.news.module.focus.adapter.FocusAddAdapter;
import com.aktt.news.module.focus.data.FocusBean;
import com.aktt.news.module.focus.data.FocusWrapper;
import com.aktt.news.net.callback.SimpleCallback;
import com.aktt.news.net.client.NetRequest;
import com.aktt.news.net.data.HttpResult;
import com.aktt.news.util.RefreshHelper;
import com.aktt.news.widget.ilook.ILookTitleBar;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import retrofit2.Call;

/**
 * Created by magical on 2017/9/3.
 * Description : 加关注列表
 */

public class AddFollowListActivity extends BaseActivity {

    @BindView(R.id.id_recycler) RecyclerView mRecyclerView;
    @BindView(R.id.id_refresh_layout) SmartRefreshLayout mRefreshLayout;

    private FocusAddAdapter mAdapter;
    private RefreshHelper<FocusBean> mRefreshHelper;
    private UserService service;

    @Override
    public int getLayoutId() {
        return R.layout.simple_recycler_list;
    }

    @Override
    protected boolean addTitleBar() {
        return true;
    }

    @Override
    public void handleChildPage(Bundle savedInstanceState) {

        mTitleBar.setTitle("添加关注").setTitleListener(new ILookTitleBar.TitleCallbackAdapter() {
            @Override
            public void onClickLeft(View view) {
                super.onClickLeft(view);
                finish();
            }
        });

        service = NetRequest.getInstance().create(UserService.class);
        config();
    }

    private void config() {

        mAdapter = new FocusAddAdapter(R.layout.item_add_focus);
        mRecyclerView.setAdapter(mAdapter);
        mRefreshHelper = new RefreshHelper<FocusBean>(mRefreshLayout, mAdapter, mRecyclerView) {
            @Override
            public void onRequest(int page) {
                loadData(page);
            }
        };

        mRefreshHelper.beginLoadData();
    }

    private void loadData(int page) {

        String userId = AccountManager.getInstance().getUserId();
        if (TextUtils.isEmpty(userId)) {
            return;
        }

        showLoading();
        Call<HttpResult<FocusWrapper>> call =
                service.getNotFollowList(userId, page, AppConstant.DEFAULT_PAGE_SIZE);
        call.enqueue(new SimpleCallback<FocusWrapper>() {
            @Override
            public void onSuccess(FocusWrapper data) {

                hideLoading();
                mRefreshHelper.setData(data.list,data.haveNext);
            }

            @Override
            public void onFail(String code, String errorMsg) {
                mRefreshHelper.onFail();
                onSimpleError(errorMsg);
            }
        });
    }
}
