package com.aktt.news.module.user.activity;

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
import com.aktt.news.AppConstant;
import com.aktt.news.R;
import com.aktt.news.api.HomeService;
import com.aktt.news.base.BaseActivity;
import com.aktt.news.instance.AccountManager;
import com.aktt.news.module.home.adapter.NewsListAdapter;
import com.aktt.news.module.home.data.NewsListBean;
import com.aktt.news.module.home.data.NewsListWrapper;
import com.aktt.news.module.video.adapter.VideoListAdapter;
import com.aktt.news.net.callback.SimpleCallback;
import com.aktt.news.net.client.NetRequest;
import com.aktt.news.net.data.HttpResult;
import com.aktt.news.util.IntentHelper;
import com.aktt.news.util.RefreshHelper;
import com.aktt.news.util.SimplePreNotifyListener;
import com.aktt.news.util.StringHelper;
import com.aktt.news.util.UserOptionHelper;
import com.aktt.news.widget.ilook.ILookTitleBar;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import timber.log.Timber;

/**
 * Created by magical on 2017/10/12.
 * Description : 收藏列表
 */

public class CollectionActivity extends BaseActivity {

    @BindView(R.id.id_recycler) RecyclerView mRecyclerView;
    @BindView(R.id.id_refresh_layout) SmartRefreshLayout mRefreshLayout;

    private NewsListAdapter mAdapter;
    private RefreshHelper mRefreshHelper;
    private List<NewsListBean> mDataList = new ArrayList<>();

    private boolean mFull;
    private View mEmptyView;
    private HomeService service;

    private ArrayList<Call> callList;

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

        mTitleBar.setTitle("我的收藏");
        mTitleBar.setTitleListener(new ILookTitleBar.TitleCallbackAdapter() {
            @Override
            public void onClickLeft(View view) {
                super.onClickLeft(view);
                finish();
            }
        });

        service = NetRequest.getInstance().create(HomeService.class);

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
                    IntentHelper.openVideoDetailPage(CollectionActivity.this, newId, 0, bean.type);
                } else if (itemType == NewsListBean.TEXT) {
                    IntentHelper.openNewsDetailPage(CollectionActivity.this, newId, itemType);
                } else if (itemType == NewsListBean.IMAGE) {
                    IntentHelper.openNewsPhotoDetailPage(CollectionActivity.this, newId, itemType);
                } else if (itemType == NewsListBean.AD) {
                    //广告类跳转
                    IntentHelper.openNewsPhotoDetailPage(CollectionActivity.this, newId, itemType);
                } else if (itemType == NewsListBean.HTML) {
                    //跳转类
                    IntentHelper.openWebPage(CollectionActivity.this,
                            StringHelper.getString(bean.url));
                } else {
                    Timber.e("can't resolve jump type.");
                }
            }
        });

        mAdapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                onCancelCollect(position);
                return false;
            }
        });

        mEmptyView = LayoutInflater.from(this)
                .inflate(R.layout.base_empty_view, (ViewGroup) mRecyclerView.getParent(), false);

        mRefreshHelper.beginLoadData();
    }

    /**
     * 取消收藏
     */
    private void onCancelCollect(final int position) {

        QMUIDialog mClearDialog = new QMUIDialog.MessageDialogBuilder(this).setTitle("提示")
                .setMessage("确定取消收藏吗")
                .addAction("取消", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                    }
                })
                .addAction("确定", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                        cancelCollection(position);
                    }
                })
                .create();
        mClearDialog.show();
    }

    private void cancelCollection(final int position) {
        List<NewsListBean> data = mAdapter.getData();
        if (data.size() > position) {

            NewsListBean bean = data.get(position);
            UserOptionHelper.onCancelStar(service, bean.newId, bean.type,
                    new SimplePreNotifyListener() {
                        @Override
                        public void onPreToDo() {
                            showLoading();
                        }

                        @Override
                        public void onSuccess(String msg) {
                            hideLoading();
                            mAdapter.remove(position);
                        }

                        @Override
                        public void onFailed(String msg) {
                            onSimpleError(msg);
                        }
                    });
        }
    }

    private void load(final int page) {

        String userId = AccountManager.getInstance().getUserId();
        if (TextUtils.isEmpty(userId)) {
            mRefreshHelper.onFail();
            return;
        }

        showLoading();
        final Call<HttpResult<NewsListWrapper>> call =
                service.getCollectionList(userId, page, AppConstant.DEFAULT_PAGE_SIZE);
        onAddCall(call);
        call.enqueue(new SimpleCallback<NewsListWrapper>() {
            @Override
            public void onSuccess(NewsListWrapper data) {

                hideLoading();
                onRemoveCall(call);

                if (page == 1 && (null == data || null == data.list || data.list.size() == 0)) {
                    mRefreshHelper.finishRefresh();
                    mAdapter.getData().clear();
                    mAdapter.notifyDataSetChanged();
                    mAdapter.setEmptyView(mEmptyView);
                    return;
                }

                //noinspection unchecked
                mRefreshHelper.setData(data.list, data.havePage);
            }

            @Override
            public void onFail(String code, String errorMsg) {
                onRemoveCall(call);
                mRefreshHelper.onFail();
                hideLoading();
            }
        });
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
    public ArrayList<Call> addRequestList() {
        callList = new ArrayList<>();
        return callList;
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
