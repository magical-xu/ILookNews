package com.chaoneng.ilooknews.module.focus;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import butterknife.BindView;
import com.chaoneng.ilooknews.AppConstant;
import com.chaoneng.ilooknews.R;
import com.chaoneng.ilooknews.api.UserService;
import com.chaoneng.ilooknews.base.BaseActivity;
import com.chaoneng.ilooknews.module.focus.adapter.FocusAddAdapter;
import com.chaoneng.ilooknews.module.focus.data.FocusBean;
import com.chaoneng.ilooknews.module.focus.data.FocusWrapper;
import com.chaoneng.ilooknews.net.callback.SimpleCallback;
import com.chaoneng.ilooknews.net.client.NetRequest;
import com.chaoneng.ilooknews.net.data.HttpResult;
import com.chaoneng.ilooknews.util.RefreshHelper;
import com.chaoneng.ilooknews.widget.ilook.ILookTitleBar;
import com.magicalxu.library.blankj.ToastUtils;
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

        Call<HttpResult<FocusWrapper>> call =
                service.getNotFollowList(AppConstant.TEST_USER_ID, page,
                        AppConstant.DEFAULT_PAGE_SIZE);
        call.enqueue(new SimpleCallback<FocusWrapper>() {
            @Override
            public void onSuccess(FocusWrapper data) {

                mRefreshHelper.setData(data.list);
            }

            @Override
            public void onFail(String code, String errorMsg) {
                ToastUtils.showShort(errorMsg);
            }
        });
    }
}
