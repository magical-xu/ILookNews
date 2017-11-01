package com.aktt.news.module.search;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import butterknife.BindView;
import com.aktt.news.instance.AccountManager;
import com.aktt.news.util.StringHelper;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.aktt.news.AppConstant;
import com.aktt.news.R;
import com.aktt.news.api.SearchService;
import com.aktt.news.base.BaseActivity;
import com.aktt.news.module.home.adapter.NewsListAdapter;
import com.aktt.news.module.home.data.NewsListBean;
import com.aktt.news.module.home.data.NewsListWrapper;
import com.aktt.news.module.video.adapter.VideoListAdapter;
import com.aktt.news.net.callback.SimpleCallback;
import com.aktt.news.net.client.NetRequest;
import com.aktt.news.net.data.HttpResult;
import com.aktt.news.util.IntentHelper;
import com.aktt.news.util.RefreshHelper;
import com.aktt.news.widget.ilook.ILookTitleBar;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import timber.log.Timber;

/**
 * Created by magical on 17/9/12.
 * Description : 搜索详情
 */

public class SearchDetailActivity extends BaseActivity {

    private static final String PARAMS_KEY = "keyword";

    @BindView(R.id.id_recycler) RecyclerView mRecyclerView;
    @BindView(R.id.id_refresh_layout) SmartRefreshLayout mRefreshLayout;

    private NewsListAdapter mAdapter;
    private RefreshHelper mRefreshHelper;
    private List<NewsListBean> mDataList = new ArrayList<>();
    private SearchService service;
    private String mKeyword;
    private boolean mFull;

    private ArrayList<Call> callList;

    public static void getInstance(Context context, String keyword) {
        Intent intent = new Intent(context, SearchDetailActivity.class);
        intent.putExtra(PARAMS_KEY, keyword);
        context.startActivity(intent);
    }

    @Override
    protected boolean addTitleBar() {
        return true;
    }

    @Override
    public ArrayList<Call> addRequestList() {
        callList = new ArrayList<>();
        return callList;
    }

    @Override
    public int getLayoutId() {
        return R.layout.simple_recycler_list;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //如果旋转了就全屏
        if (newConfig.orientation != ActivityInfo.SCREEN_ORIENTATION_USER) {
            mFull = false;
        } else {
            mFull = true;
        }
    }

    @Override
    public void handleChildPage(Bundle savedInstanceState) {

        Intent intent = getIntent();
        mKeyword = intent.getStringExtra(PARAMS_KEY);

        mTitleBar.setTitle(mKeyword).setTitleListener(new ILookTitleBar.TitleCallbackAdapter() {
            @Override
            public void onClickLeft(View view) {
                super.onClickLeft(view);
                finish();
            }
        });

        service = NetRequest.getInstance().create(SearchService.class);

        mAdapter = new NewsListAdapter(mDataList);
        mRefreshHelper = new RefreshHelper(mRefreshLayout, mAdapter, mRecyclerView) {
            @Override
            public void onRequest(int page) {
                load(page);
            }
        };

        mRefreshHelper.beginLoadData();

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            int firstVisibleItem;
            int lastVisibleItem;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager linearLayoutManager =
                        (LinearLayoutManager) recyclerView.getLayoutManager();

                firstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                //大于0说明有播放
                if (GSYVideoManager.instance().getPlayPosition() >= 0) {
                    //当前播放的位置
                    int position = GSYVideoManager.instance().getPlayPosition();
                    //对应的播放列表TAG
                    if (GSYVideoManager.instance().getPlayTag().equals(VideoListAdapter.TAG) && (
                            position < firstVisibleItem
                                    || position > lastVisibleItem)) {

                        //如果滑出去了上面和下面就是否，和今日头条一样
                        //是否全屏
                        if (!mFull) {
                            GSYVideoPlayer.releaseAllVideos();
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        });

        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                NewsListBean bean = mAdapter.getData().get(position);
                int itemType = bean.getItemType();
                String newId = bean.newId;
                if (itemType == NewsListBean.VIDEO) {
                    IntentHelper.openVideoDetailPage(SearchDetailActivity.this, newId, 0,
                            bean.type);
                } else if (itemType == NewsListBean.TEXT) {
                    IntentHelper.openNewsDetailPage(SearchDetailActivity.this, newId, itemType);
                } else if (itemType == NewsListBean.IMAGE) {
                    IntentHelper.openNewsPhotoDetailPage(SearchDetailActivity.this, newId,
                            itemType);
                } else if (itemType == NewsListBean.AD) {
                    //广告类跳转
                    IntentHelper.openWebPage(SearchDetailActivity.this,
                            StringHelper.getString(bean.url),
                            StringHelper.getString(bean.adcontent));
                } else if (itemType == NewsListBean.HTML) {
                    //跳转类
                    IntentHelper.openWebPage(SearchDetailActivity.this,
                            StringHelper.getString(bean.url),
                            StringHelper.getString(bean.adcontent));
                } else {
                    Timber.e("can't resolve jump type.");
                }
            }
        });
    }

    private void load(final int page) {

        String userId = AccountManager.getInstance().getUserId();

        final Call<HttpResult<NewsListWrapper>> call =
                service.doSearch(StringHelper.getString(userId), mKeyword);
        onAddCall(call);
        call.enqueue(new SimpleCallback<NewsListWrapper>() {
            @Override
            public void onSuccess(NewsListWrapper data) {
                onRemoveCall(call);
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
                onRemoveCall(call);
                mRefreshHelper.onFail();
            }
        });
    }
}
