package com.aktt.news.module.focus;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import butterknife.BindView;
import com.aktt.news.AppConstant;
import com.aktt.news.R;
import com.aktt.news.api.UserService;
import com.aktt.news.base.BaseTitleFragment;
import com.aktt.news.instance.AccountManager;
import com.aktt.news.module.focus.adapter.FocusAdapter;
import com.aktt.news.module.focus.data.FocusBean;
import com.aktt.news.module.focus.data.FocusWrapper;
import com.aktt.news.net.callback.SimpleCallback;
import com.aktt.news.net.client.NetRequest;
import com.aktt.news.net.data.HttpResult;
import com.aktt.news.util.IntentHelper;
import com.aktt.news.util.LocalBroadcastUtil;
import com.aktt.news.util.RefreshHelper;
import com.aktt.news.widget.ilook.ILookTitleBar;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import java.util.List;
import retrofit2.Call;

/**
 * Created by magical on 17/8/15.
 * Description : 主页 - 关注
 */

public class FocusMainFragment extends BaseTitleFragment {

    @BindView(R.id.id_recycler) RecyclerView mRecyclerView;
    @BindView(R.id.id_refresh_layout) SmartRefreshLayout mRefreshLayout;

    private FocusAdapter mAdapter;
    private RefreshHelper<FocusBean> mRefreshHelper;
    private UserService service;
    private boolean needRefresh = false;        //防止用户退出登录 换账号登录后还显示上个账户加载的数据
    private IntentFilter filter;

    @Override
    protected void beginLoadData() {
        mRefreshHelper.beginLoadData();
    }

    @Override
    public void init() {

        mTitleBar.setTitleImage(R.drawable.img_focus_title)
                .setRightImage(R.drawable.ic_care_plus_blue)
                .hideLeftImage()
                .setTitleListener(new ILookTitleBar.TitleCallbackAdapter() {
                    @Override
                    public void onClickRightImage(View view) {
                        super.onClickRightImage(view);
                        IntentHelper.openAddFocusPage(getActivity());
                    }
                });

        service = NetRequest.getInstance().create(UserService.class);

        initFilter();
        if (null != receiver && null != filter) {
            LocalBroadcastUtil.register(receiver, filter);
        }

        config();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (needRefresh && getUserVisibleHint()) {
            loadData(1);
            needRefresh = false;
        }
    }

    private void initFilter() {
        filter = new IntentFilter();
        filter.addAction(LocalBroadcastUtil.LOGIN);
        filter.addAction(LocalBroadcastUtil.LOGOUT);
    }

    @Override
    public int getSubLayout() {
        return R.layout.simple_recycler_list;
    }

    private void config() {

        mAdapter = new FocusAdapter(R.layout.item_main_focus);
        mRecyclerView.setAdapter(mAdapter);
        mRefreshHelper = new RefreshHelper<FocusBean>(mRefreshLayout, mAdapter, mRecyclerView) {
            @Override
            public void onRequest(int page) {
                loadData(page);
            }
        };

        mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                onChildClick(view, position);
            }
        });
    }

    private void onChildClick(View view, int position) {

        switch (view.getId()) {
            case R.id.iv_avatar:

                List<FocusBean> data = mAdapter.getData();
                if (position < data.size()) {

                    FocusBean item = data.get(position);
                    String target_id = item.target_id;
                    if (!TextUtils.isEmpty(target_id)) {
                        IntentHelper.openUserCenterPage(getActivity(), target_id);
                    }
                }
                break;
            default:
                break;
        }
    }

    private void loadData(int page) {

        String loginUserId = AccountManager.getInstance().getUserId();
        if (TextUtils.isEmpty(loginUserId)) {
            return;
        }

        showLoading();
        //Call<HttpResult<FocusWrapper>> call =
        //        service.getNotFollowList(loginUserId, page, AppConstant.DEFAULT_PAGE_SIZE);
        //call.enqueue(new SimpleCallback<FocusWrapper>() {
        //    @Override
        //    public void onSuccess(FocusWrapper data) {
        //        hideLoading();
        //        mRefreshHelper.setData(data.list, data.haveNext);
        //    }
        //
        //    @Override
        //    public void onFail(String code, String errorMsg) {
        //        mRefreshHelper.onFail();
        //        onSimpleError(errorMsg);
        //    }
        //});

        Call<HttpResult<FocusWrapper>> call =
                service.getRelationList(loginUserId, loginUserId, 0, page,
                        AppConstant.DEFAULT_PAGE_SIZE);
        call.enqueue(new SimpleCallback<FocusWrapper>() {
            @Override
            public void onSuccess(FocusWrapper data) {
                hideLoading();
                mRefreshHelper.setData(data.list, data.haveNext);
            }

            @Override
            public void onFail(String code, String errorMsg) {
                mRefreshHelper.onFail();
                onSimpleError(errorMsg);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != receiver) {
            LocalBroadcastUtil.unRegister(receiver);
        }
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (TextUtils.equals(intent.getAction(), LocalBroadcastUtil.LOGOUT)) {
                needRefresh = true;
            } else if (TextUtils.equals(intent.getAction(), LocalBroadcastUtil.LOGIN)) {
                needRefresh = true;
            }
        }
    };
}
