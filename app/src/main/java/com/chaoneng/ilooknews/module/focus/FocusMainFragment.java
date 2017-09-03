package com.chaoneng.ilooknews.module.focus;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import butterknife.BindView;
import com.chaoneng.ilooknews.AppConstant;
import com.chaoneng.ilooknews.R;
import com.chaoneng.ilooknews.api.UserService;
import com.chaoneng.ilooknews.base.BaseTitleFragment;
import com.chaoneng.ilooknews.data.MockServer;
import com.chaoneng.ilooknews.module.focus.adapter.FocusAdapter;
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
 * Created by magical on 17/8/15.
 * Description : 主页 - 关注
 */

public class FocusMainFragment extends BaseTitleFragment {

    @BindView(R.id.id_recycler) RecyclerView mRecyclerView;
    @BindView(R.id.id_refresh_layout) SmartRefreshLayout mRefreshLayout;

    private FocusAdapter mAdapter;
    private RefreshHelper<FocusBean> mRefreshHelper;
    private MockServer mockServer;
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
                        ToastUtils.showShort("加關注");
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
                //mockServer.mockGankCall(page, MockServer.Type.FOCUS);
                loadData(page);
            }
        };
        //mockServer = MockServer.getInstance();
        //mockServer.init(mRefreshHelper);
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
