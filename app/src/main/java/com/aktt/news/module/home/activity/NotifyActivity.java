package com.aktt.news.module.home.activity;

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
import com.aktt.news.module.home.adapter.NotifyAdapter;
import com.aktt.news.module.user.data.NotifyBean;
import com.aktt.news.module.user.data.NotifyWrapper;
import com.aktt.news.net.callback.SimpleCallback;
import com.aktt.news.net.client.NetRequest;
import com.aktt.news.net.data.HttpResult;
import com.aktt.news.util.RefreshHelper;
import com.aktt.news.widget.ilook.ILookTitleBar;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import retrofit2.Call;

/**
 * Created by magical on 17/8/17.
 * Description : 我的消息界面
 */

public class NotifyActivity extends BaseActivity {

    @BindView(R.id.id_recycler) RecyclerView mRecyclerView;
    @BindView(R.id.id_refresh_layout) SmartRefreshLayout mRefreshLayout;

    private NotifyAdapter mAdapter;
    private RefreshHelper<NotifyBean> mRefreshHelper;
    private UserService service;

    @Override
    public int getLayoutId() {
        return R.layout.activity_notify;
    }

    @Override
    protected boolean addTitleBar() {
        return true;
    }

    @Override
    public void handleChildPage(Bundle savedInstanceState) {

        service = NetRequest.getInstance().create(UserService.class);
        configTitle();
        init();
    }

    private void configTitle() {

        mTitleBar.setTitle("我的消息");
        mTitleBar.setTitleListener(new ILookTitleBar.TitleCallbackAdapter() {
            @Override
            public void onClickLeft(View view) {
                super.onClickLeft(view);
                finish();
            }
        });
    }

    private void init() {

        mAdapter = new NotifyAdapter(R.layout.item_home_notify);
        mRefreshHelper = new RefreshHelper<NotifyBean>(mRefreshLayout, mAdapter, mRecyclerView) {
            @Override
            public void onRequest(int page) {
                loadData(page);
            }
        };
        //mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
        //    @Override
        //    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        //        showMsgDetail(position);
        //    }
        //});
        mRefreshHelper.beginLoadData();
    }

    private void loadData(int page) {

        String userId = AccountManager.getInstance().getUserId();
        if (TextUtils.isEmpty(userId)) {
            mRefreshHelper.onFail();
            return;
        }

        showLoading();
        Call<HttpResult<NotifyWrapper>> call =
                service.getMyMessageList(userId, page, AppConstant.DEFAULT_PAGE_SIZE);
        call.enqueue(new SimpleCallback<NotifyWrapper>() {
            @Override
            public void onSuccess(NotifyWrapper data) {
                hideLoading();
                mRefreshHelper.setData(data.systemMessageList, data.haveNext);
            }

            @Override
            public void onFail(String code, String errorMsg) {
                mRefreshHelper.onFail();
                onSimpleError(errorMsg);
            }
        });
    }
}
