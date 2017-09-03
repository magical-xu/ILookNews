package com.chaoneng.ilooknews.module.home.fragment;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import butterknife.BindView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chaoneng.ilooknews.AppConstant;
import com.chaoneng.ilooknews.R;
import com.chaoneng.ilooknews.api.Constant;
import com.chaoneng.ilooknews.api.GankModel;
import com.chaoneng.ilooknews.api.GankService;
import com.chaoneng.ilooknews.api.HomeService;
import com.chaoneng.ilooknews.base.BaseFragment;
import com.chaoneng.ilooknews.data.MockServer;
import com.chaoneng.ilooknews.module.home.adapter.NewsListAdapter;
import com.chaoneng.ilooknews.module.home.data.NewsListBean;
import com.chaoneng.ilooknews.module.home.data.NewsListWrapper;
import com.chaoneng.ilooknews.net.callback.SimpleCallback;
import com.chaoneng.ilooknews.net.callback.SimpleJsonCallback;
import com.chaoneng.ilooknews.net.client.NetRequest;
import com.chaoneng.ilooknews.net.data.HttpResult;
import com.chaoneng.ilooknews.util.IntentHelper;
import com.chaoneng.ilooknews.util.RefreshHelper;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import timber.log.Timber;

import static com.chaoneng.ilooknews.AppConstant.PAGE_TYPE;

/**
 * Created by magical on 2017/8/14.
 * 首页新闻列表
 */

public class NewsListFragment extends BaseFragment {

    @BindView(R.id.id_recycler) RecyclerView mRecyclerView;
    @BindView(R.id.id_refresh_layout) SmartRefreshLayout mRefreshLayout;

    private NewsListAdapter mAdapter;
    private RefreshHelper mRefreshHelper;
    private MockServer mockServer;
    private List<NewsListBean> mDataList = new ArrayList<>();

    private String mCid;    //频道标签类型

    public static NewsListFragment newInstance(String type) {
        NewsListFragment fragment = new NewsListFragment();
        Bundle bundle = new Bundle();
        bundle.putString(PAGE_TYPE, type);
        fragment.setArguments(bundle);
        return fragment;
    }

    //@Override
    //protected void beginLoadData() {
    //    mRefreshHelper.beginLoadData();
    //}

    @Override
    protected void lazyLoad() {
        super.lazyLoad();
        mRefreshHelper.beginLoadData();
    }

    @Override
    protected void doInit() {

        Bundle bundle = getArguments();
        if (null != bundle) {
            mCid = bundle.getString(PAGE_TYPE);
        }

        mAdapter = new NewsListAdapter(mDataList);
        mockServer = MockServer.getInstance();
        mRefreshHelper = new RefreshHelper(mRefreshLayout, mAdapter, mRecyclerView) {
            @Override
            public void onRequest(int page) {
                //loadData(page);
                load(page);
            }
        };
        mockServer.init(mRefreshHelper);

        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                NewsListBean bean = mAdapter.getData().get(position);
                int itemType = bean.getItemType();
                String newId = bean.newId;
                if (itemType == NewsListBean.VIDEO) {
                    IntentHelper.openVideoDetailPage(getActivity(), "");
                } else if (itemType == NewsListBean.TEXT) {
                    IntentHelper.openNewsDetailPage(mContext, newId);
                } else if (itemType == NewsListBean.THREE_IMG) {
                    IntentHelper.openNewsPhotoDetailPage(mContext);
                } else {
                    IntentHelper.openNewsDetailPage(mContext, newId);
                }
            }
        });
    }

    private void loadData(final int page) {
        GankService service = NetRequest.getInstance().create(GankService.class);
        Call<GankModel> call = service.getData(Constant.BASE_URL + page);

        call.enqueue(new SimpleJsonCallback<GankModel>() {
            @Override
            public void onSuccess(GankModel data) {
                buildFakeData(page);
            }

            @Override
            public void onFailed(int code, String message) {
                mRefreshHelper.onFail();
            }
        });
    }

    private void load(final int page) {

        if (TextUtils.isEmpty(mCid)) {
            Timber.e(" cannot get page cid.");
            return;
        }

        HomeService service = NetRequest.getInstance().create(HomeService.class);
        Call<HttpResult<NewsListWrapper>> call =
                service.getNewsList(AppConstant.TEST_USER_ID, mCid, page,
                        AppConstant.DEFAULT_PAGE_SIZE);
        call.enqueue(new SimpleCallback<NewsListWrapper>() {
            @Override
            public void onSuccess(NewsListWrapper data) {

                if (page == 1) {

                    mRefreshHelper.finishRefresh();
                    mAdapter.setNewData(data.list);
                } else {

                    if (!data.havePage) {
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
            }
        });
    }

    private void buildFakeData(int page) {

        List<NewsListBean> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            NewsListBean bean = new NewsListBean();
            bean.setItemType((int) (Math.random() * 5) + 1);
            list.add(bean);
        }

        if (page == 1) {

            mRefreshHelper.finishRefresh();
            mAdapter.setNewData(list);
        } else {

            if (page == 4) {
                mRefreshHelper.setNoMoreData();
                mRefreshHelper.setCurPage(3);
                return;
            }

            mRefreshHelper.finishLoadmore();
            mAdapter.addData(list);
        }
    }

    @Override
    protected int getLayoutName() {
        return R.layout.simple_recycler_list;
    }
}
