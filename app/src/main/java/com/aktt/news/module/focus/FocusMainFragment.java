package com.aktt.news.module.focus;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import butterknife.BindView;
import com.chad.library.adapter.base.BaseQuickAdapter;
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
import com.aktt.news.util.RefreshHelper;
import com.aktt.news.widget.ilook.ILookTitleBar;
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
        config();
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
        Call<HttpResult<FocusWrapper>> call =
                service.getNotFollowList(loginUserId, page, AppConstant.DEFAULT_PAGE_SIZE);
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
}
