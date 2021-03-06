package com.aktt.news.module.video.fragment;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.BindView;
import com.aktt.news.AppConstant;
import com.aktt.news.R;
import com.aktt.news.api.HomeService;
import com.aktt.news.base.BaseFragment;
import com.aktt.news.instance.VideoManager;
import com.aktt.news.module.home.data.NewsListBean;
import com.aktt.news.module.home.data.NewsListWrapper;
import com.aktt.news.module.video.adapter.VideoListAdapter;
import com.aktt.news.net.callback.SimpleCallback;
import com.aktt.news.net.client.NetRequest;
import com.aktt.news.net.data.HttpResult;
import com.aktt.news.util.IntentHelper;
import com.aktt.news.util.RefreshHelper;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer;
import java.util.List;
import retrofit2.Call;
import timber.log.Timber;

import static com.aktt.news.AppConstant.PAGE_TYPE;

/**
 * Created by magical on 17/8/19.
 * Description : 视频列表界面
 */

public class VideoListFragment extends BaseFragment {

    @BindView(R.id.id_recycler) RecyclerView mRecyclerView;
    @BindView(R.id.id_refresh_layout) SmartRefreshLayout mRefreshLayout;

    private VideoListAdapter mAdapter;
    private RefreshHelper mRefreshHelper;

    private boolean mFull = false;
    private String mCid;
    private HomeService service;

    private View mEmptyView;

    public static VideoListFragment newInstance(String type) {
        VideoListFragment fragment = new VideoListFragment();
        Bundle bundle = new Bundle();
        bundle.putString(PAGE_TYPE, type);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void lazyLoad() {
        super.lazyLoad();
        mRefreshHelper.beginLoadData();
        //load(1);
    }

    private void load(final int page) {

        showLoading();
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
                onSimpleError(errorMsg);
            }
        });
    }

    @Override
    protected void doInit() {

        checkBundle();
        service = NetRequest.getInstance().create(HomeService.class);
        mAdapter = new VideoListAdapter(R.layout.item_main_video_list);
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

        mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                int viewId = view.getId();
                if (viewId == R.id.id_focus_plus) {
                    List<NewsListBean> newsListBean = mAdapter.getData();
                    if (newsListBean.size() > position) {
                        NewsListBean item = newsListBean.get(position);
                        if (null != item) {
                            IntentHelper.openShareBottomPage(mContext, item.newId, item.type,
                                    item.userid);
                        }
                    }
                } else if (viewId == R.id.tv_comments) {
                    handleItemClick(position);
                }
            }
        });

        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                handleItemClick(position);
            }
        });

        mEmptyView = LayoutInflater.from(getActivity())
                .inflate(R.layout.base_empty_view, (ViewGroup) mRecyclerView.getParent(), false);
    }

    private void handleItemClick(int position) {

        List<NewsListBean> data = mAdapter.getData();
        if (data.size() < position) {
            return;
        }

        NewsListBean videoListBean = data.get(position);
        if (null == videoListBean || TextUtils.isEmpty(videoListBean.newId)) {
            // can't get the news id , so we don't jump to detail page
            Timber.e("invalidate news id.");
            return;
        }

        String newsId = videoListBean.newId;
        int newsType = videoListBean.type;

        long progress = 0;
        //int curPos = GSYVideoManager.instance().getPlayPosition();
        //if (curPos == position) {
        //
        //    // 当前播放Item 即点击跳转的Item 则记录播放位置
        //    progress = GSYVideoManager.instance().getMediaPlayer().getCurrentPosition();
        //    Log.d("magical", "manager: " + progress);
        //}

        //String dataSource = GSYVideoManager.instance().getMediaPlayer().getDataSource();
        //Log.d("magical", "play data : " + dataSource);
        //long curPos = GSYVideoManager.instance().getMediaPlayer().getCurrentPosition();
        //Log.d("magical", " cur pos : " + curPos);
        //
        int playPos = GSYVideoManager.instance().getPlayPosition();
        Log.d("magical", " play pos : " + playPos);
        if (playPos != VideoManager.ERROR_PLAY_POS && data.size() > playPos) {
            String url = data.get(playPos).videoUrl;
            Log.d("magical", " play url : " + url);

            if (!TextUtils.isEmpty(url)) {
                //保存进度
                long progress2 = GSYVideoManager.instance().getMediaPlayer().getCurrentPosition();
                VideoManager.getInstance().putProgress(url, progress2);
            }
        }

        // 跳转视频详情播放
        IntentHelper.openVideoDetailPage(getActivity(), newsId, progress, newsType);
    }

    private void checkBundle() {

        Bundle bundle = getArguments();
        mCid = bundle.getString(PAGE_TYPE);
    }

    @Override
    protected int getLayoutName() {
        return R.layout.simple_recycler_list;
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("magical", "onPause()");
        GSYVideoManager.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("magical", "onStop");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("magical", "onResume");
        GSYVideoManager.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
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
