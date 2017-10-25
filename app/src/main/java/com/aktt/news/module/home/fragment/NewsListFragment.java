package com.aktt.news.module.home.fragment;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.BindView;
import com.aktt.news.util.StringHelper;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.aktt.news.AppConstant;
import com.aktt.news.R;
import com.aktt.news.api.HomeService;
import com.aktt.news.base.BaseFragment;
import com.aktt.news.module.home.adapter.NewsListAdapter;
import com.aktt.news.module.home.data.NewsListBean;
import com.aktt.news.module.home.data.NewsListWrapper;
import com.aktt.news.module.video.adapter.VideoListAdapter;
import com.aktt.news.net.callback.SimpleCallback;
import com.aktt.news.net.client.NetRequest;
import com.aktt.news.net.data.HttpResult;
import com.aktt.news.util.IntentHelper;
import com.aktt.news.util.RefreshHelper;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import timber.log.Timber;

import static com.aktt.news.AppConstant.PAGE_TYPE;

/**
 * Created by magical on 2017/8/14.
 * 首页新闻列表
 */

public class NewsListFragment extends BaseFragment {

    @BindView(R.id.id_recycler) RecyclerView mRecyclerView;
    @BindView(R.id.id_refresh_layout) SmartRefreshLayout mRefreshLayout;

    private NewsListAdapter mAdapter;
    private RefreshHelper mRefreshHelper;
    private List<NewsListBean> mDataList = new ArrayList<>();

    private String mCid;    //频道标签类型
    private boolean mFull;
    private View mEmptyView;

    public static NewsListFragment newInstance(String type) {
        NewsListFragment fragment = new NewsListFragment();
        Bundle bundle = new Bundle();
        bundle.putString(PAGE_TYPE, type);
        fragment.setArguments(bundle);
        return fragment;
    }

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
        mRefreshHelper = new RefreshHelper(mRefreshLayout, mAdapter, mRecyclerView) {
            @Override
            public void onRequest(int page) {
                load(page);
            }
        };

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
                    IntentHelper.openVideoDetailPage(getActivity(), newId, 0, bean.type);
                } else if (itemType == NewsListBean.TEXT) {
                    IntentHelper.openNewsDetailPage(mContext, newId, itemType);
                } else if (itemType == NewsListBean.IMAGE) {
                    IntentHelper.openNewsPhotoDetailPage(mContext, newId, itemType);
                } else if (itemType == NewsListBean.AD) {
                    //广告类跳转
                    IntentHelper.openNewsPhotoDetailPage(mContext, newId, itemType);
                } else if (itemType == NewsListBean.HTML) {
                    //跳转类
                    IntentHelper.openWebPage(getActivity(), StringHelper.getString(bean.url));
                } else {
                    Timber.e("can't resolve jump type.");
                }
            }
        });

        mEmptyView = LayoutInflater.from(getActivity())
                .inflate(R.layout.base_empty_view, (ViewGroup) mRecyclerView.getParent(), false);
    }

    private void load(final int page) {

        if (TextUtils.isEmpty(mCid)) {
            Timber.e(" cannot get page cid.");
            return;
        }

        showLoading();
        HomeService service = NetRequest.getInstance().create(HomeService.class);
        Call<HttpResult<NewsListWrapper>> call =
                service.getNewsList(mCid, page, AppConstant.DEFAULT_PAGE_SIZE);
        call.enqueue(new SimpleCallback<NewsListWrapper>() {
            @Override
            public void onSuccess(NewsListWrapper data) {

                hideLoading();

                if (page == 1 && (null == data || null == data.list || data.list.size() == 0)) {
                    mRefreshHelper.finishRefresh();
                    mAdapter.setEmptyView(mEmptyView);
                    return;
                }

                //noinspection unchecked
                mRefreshHelper.setData(data.list, data.havePage);
            }

            @Override
            public void onFail(String code, String errorMsg) {
                mRefreshHelper.onFail();
                hideLoading();
            }
        });
    }

    @Override
    protected int getLayoutName() {
        return R.layout.simple_recycler_list;
    }

    @Override
    public void onPause() {
        super.onPause();
        GSYVideoManager.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        GSYVideoManager.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        GSYVideoPlayer.releaseAllVideos();
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
}
