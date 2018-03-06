package com.aktt.news.module.video.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.OnClick;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.aktt.news.AppConstant;
import com.aktt.news.R;
import com.aktt.news.api.HomeService;
import com.aktt.news.api.UserService;
import com.aktt.news.base.BaseActivity;
import com.aktt.news.data.CommentBean;
import com.aktt.news.data.NewsInfo;
import com.aktt.news.data.NewsInfoWrapper;
import com.aktt.news.instance.AccountManager;
import com.aktt.news.instance.VideoManager;
import com.aktt.news.library.glide.ImageLoader;
import com.aktt.news.library.gsyvideoplayer.VideoHelper;
import com.aktt.news.module.home.fragment.CommentDialogFragment;
import com.aktt.news.module.video.adapter.CommentAdapter;
import com.aktt.news.net.callback.SimpleCallback;
import com.aktt.news.net.client.NetRequest;
import com.aktt.news.net.data.HttpResult;
import com.aktt.news.util.AnimHelper;
import com.aktt.news.util.BottomHelper;
import com.aktt.news.util.IntentHelper;
import com.aktt.news.util.RefreshHelper;
import com.aktt.news.util.SimpleNotifyListener;
import com.aktt.news.util.SimplePreNotifyListener;
import com.aktt.news.util.StringHelper;
import com.aktt.news.util.UserOptionHelper;
import com.aktt.news.widget.image.HeadImageView;
import com.githang.statusbar.StatusBarCompat;
import com.magicalxu.library.blankj.KeyboardUtils;
import com.magicalxu.library.blankj.ToastUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;
import retrofit2.Call;

/**
 * Created by magical on 17/8/20.
 * Description : 视频详情界面
 * 点赞 ok
 * 关注 ok
 * 评论 ok
 */

public class VideoDetailActivity extends BaseActivity {

    private static final String TAG = "VideoDetailActivity";

    @BindView(R.id.video_player) StandardGSYVideoPlayer mVideoPlayer;
    @BindView(R.id.id_title_back) ImageView mTitleBack;
    @BindView(R.id.id_title_share) ImageView mTitleShare;
    @BindView(R.id.id_recycler) RecyclerView mRecyclerView;
    @BindView(R.id.id_refresh_layout) SmartRefreshLayout mRefreshLayout;

    @BindView(R.id.id_bottom_edit) TextView mFakeInputView;
    @BindView(R.id.id_real_bottom_edit) EditText mRealInputView;
    @BindView(R.id.id_real_bottom) ViewGroup mRealBottomView;
    @BindView(R.id.id_fake_bottom) ViewGroup mFakeBottomView;
    @BindView(R.id.id_bottom_comment_count) TextView mCommentCountView;

    @BindView(R.id.id_send) TextView mSendView;
    @BindView(R.id.id_bottom_star) ImageView mStarView;

    TextView mHeaderTitle;
    TextView mHeaderNum;
    TextView mHeaderUp;
    TextView mHeaderDown;
    HeadImageView mHeaderIv;
    TextView mHeaderName;
    TextView mHeaderFocus;

    private View mEmptyView;

    private CommentAdapter mAdapter;
    private RefreshHelper mRefreshHelper;
    private HomeService service;
    private UserService userService;

    //private boolean isPlay;
    private boolean isPause;
    private boolean hasNewsPraise;
    private boolean hasHeaderAdd;
    private boolean hasCollected;
    private boolean hasFollowed;

    private OrientationUtils orientationUtils;
    private String PAGE_VIDEO_URL;

    private String PAGE_VID;        //新闻 vid
    private long PAGE_PROGRESS;     //当前播放进度
    private int PAGE_NEWS_TYPE;
    private String NEWS_PUBLISHER;  //新闻发布者ID

    private ArrayList<Call> callList;

    public static void newInstance(Context context, String vid, long seek, int newsType) {
        Intent intent = new Intent(context, VideoDetailActivity.class);
        intent.putExtra(AppConstant.PAGE_TYPE, vid);
        intent.putExtra(AppConstant.PAGE_PROGRESS, seek);
        intent.putExtra(AppConstant.PARAMS_NEWS_TYPE, newsType);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_video_detail;
    }

    @Override
    public void handleChildPage(Bundle savedInstanceState) {

        StatusBarCompat.setStatusBarColor(this, ContextCompat.getColor(this, R.color.black));
        mSwipeBackHelper.setSwipeBackEnable(true);
        checkIntent();
        checkTitle();
        mAdapter = new CommentAdapter(false, R.layout.item_video_comment);
        mAdapter.setHeaderAndEmpty(true);
        mRefreshHelper = new RefreshHelper(mRefreshLayout, mAdapter, mRecyclerView) {
            @Override
            public void onRequest(int page) {
                if (page == 1) {
                    loadData(page);
                }
                loadComment(page);
            }
        };

        mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {

                CommentBean commentBean = mAdapter.getData().get(position);
                switch (view.getId()) {
                    case R.id.id_comment_count:

                        String commentId = commentBean.cid;
                        CommentDialogFragment fragment =
                                CommentDialogFragment.newInstance(PAGE_VID, PAGE_NEWS_TYPE,
                                        commentId);
                        fragment.show(getSupportFragmentManager(), TAG);
                        break;
                    case R.id.tv_up:
                        onPraise(position);
                        break;
                    case R.id.iv_avatar:
                        if (TextUtils.isEmpty(commentBean.nickname)) {
                            return;
                        }
                        IntentHelper.openUserCenterPage(VideoDetailActivity.this,
                                commentBean.userid);
                        break;
                }
            }
        });

        mEmptyView = LayoutInflater.from(this)
                .inflate(R.layout.base_comment_empty_view, (ViewGroup) mRecyclerView.getParent(),
                        false);
        mStarView.setImageResource(R.drawable.selector_star_white);

        service = NetRequest.getInstance().create(HomeService.class);
        userService = NetRequest.getInstance().create(UserService.class);
        mRefreshHelper.beginLoadData();
    }

    private void onPraise(final int position) {

        int type;
        final boolean hasPraise;
        String cid;
        if (position == AppConstant.INVALIDATE) {
            //操作 新闻

            type = PAGE_NEWS_TYPE;
            hasPraise = hasNewsPraise;
            cid = PAGE_VID;
        } else {
            //操作 评论列表

            type = 11;
            CommentBean commentBean = mAdapter.getData().get(position);
            cid = commentBean.cid;
            //hasPraise = TextUtils.equals(AppConstant.HAS_PRAISE, commentBean.isFollow);
            hasPraise = false;

            AnimHelper.showAnim(mAdapter.getViewByPosition(mRecyclerView,
                    position + mAdapter.getHeaderLayoutCount(), R.id.tv_up));
        }

        int subType = hasPraise ? 2 : 1;

        String userId = AccountManager.getInstance().getUserId();
        showLoading();
        Call<HttpResult<JSONObject>> call =
                service.optLike(StringHelper.getString(userId), null, type, cid, subType);
        call.enqueue(new SimpleCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject data) {

                hideLoading();
                if (position == AppConstant.INVALIDATE) {

                    onOptLikeSuccess(hasPraise);
                } else {

                    List<CommentBean> listData = mAdapter.getData();
                    if (listData.size() > position) {
                        CommentBean commentBean = listData.get(position);
                        if (hasPraise) {
                        } else {
                            //点赞成功
                            ToastUtils.showShort("点赞成功");
                            commentBean.isFollow = AppConstant.HAS_PRAISE;
                            ++commentBean.careCount;
                            mAdapter.notifyItemChanged(position + mAdapter.getHeaderLayoutCount());
                        }
                    }
                }
            }

            @Override
            public void onFail(String code, String errorMsg) {
                onSimpleError(errorMsg);
            }
        });
    }

    private void onOptLikeSuccess(boolean hasPraise) {

        if (hasPraise) {
            //不喜欢+1
            String disLikeCount = mHeaderDown.getText().toString();
            if (TextUtils.isDigitsOnly(disLikeCount)) {
                int disCount = Integer.parseInt(disLikeCount);
                mHeaderDown.setText(String.valueOf(disCount + 1));
            }
        } else {
            //喜欢+1
            String likeCount = mHeaderUp.getText().toString();
            if (TextUtils.isDigitsOnly(likeCount)) {
                int lCount = Integer.parseInt(likeCount);
                mHeaderUp.setText(String.valueOf(lCount + 1));
            }
        }
    }

    private void checkIntent() {

        Intent intent = getIntent();
        if (null != intent) {
            PAGE_VID = intent.getStringExtra(AppConstant.PAGE_TYPE);
            PAGE_PROGRESS = intent.getLongExtra(AppConstant.PAGE_PROGRESS, 0);
            PAGE_NEWS_TYPE = intent.getIntExtra(AppConstant.PARAMS_NEWS_TYPE, 0);
        }
    }

    private void bindHeader(NewsInfo info) {

        if (null == info) {
            return;
        }

        if (!hasHeaderAdd) {
            View view = getLayoutInflater().inflate(R.layout.header_video_coments, null);
            mHeaderTitle = view.findViewById(R.id.tv_title);
            mHeaderNum = view.findViewById(R.id.tv_play_num);
            mHeaderUp = view.findViewById(R.id.tv_up);
            mHeaderUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    hasNewsPraise = false;
                    onPraise(AppConstant.INVALIDATE);
                }
            });
            mHeaderDown = view.findViewById(R.id.tv_down);
            mHeaderDown.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    hasNewsPraise = true;
                    onPraise(AppConstant.INVALIDATE);
                }
            });
            mHeaderIv = view.findViewById(R.id.id_header_iv);
            mHeaderName = view.findViewById(R.id.id_header_name);
            mHeaderFocus = view.findViewById(R.id.id_header_focus);
            mAdapter.addHeaderView(view);

            NEWS_PUBLISHER = info.userid;
            hasCollected = TextUtils.equals(AppConstant.HAS_PRAISE, info.isCollection);
            mStarView.setSelected(hasCollected);
            hasFollowed = TextUtils.equals(AppConstant.HAS_PRAISE, info.isFollow);
            changeFollowState(hasFollowed);
            mHeaderFocus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onFollowUser(view);
                }
            });

            PAGE_VIDEO_URL = info.video_url;
            orientationUtils = new OrientationUtils(this, mVideoPlayer);
            orientationUtils.setEnable(false);
            //增加封面
            List<String> coverList = info.coverpic;
            String coverPicUrl;
            if (coverList != null && coverList.size() > 0) {
                coverPicUrl = coverList.get(0);
                if (TextUtils.isEmpty(coverPicUrl)) {
                    coverPicUrl = info.video_url + AppConstant.VIDEO_SUFFIX;
                }
            } else {
                coverPicUrl = info.video_url + AppConstant.VIDEO_SUFFIX;
            }
            ImageView imageView = new ImageView(this);
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            ImageLoader.loadImage(coverPicUrl, imageView);
            if (imageView.getParent() != null) {
                ViewGroup viewGroup = (ViewGroup) imageView.getParent();
                viewGroup.removeView(imageView);
            }
            mVideoPlayer.setThumbImageView(imageView);
            //mVideoPlayer.setPlayPosition(helper.getAdapterPosition());
            VideoHelper.initDetailPage(this, info.video_url, mVideoPlayer, orientationUtils,
                    PAGE_PROGRESS);

            addVideoPlayCount();
        }

        hasHeaderAdd = true;

        mHeaderIv.setHeadImage(StringHelper.getString(info.userIcon));
        mHeaderDown.setText(String.valueOf(info.dislike_count));
        mHeaderUp.setText(String.valueOf(info.like_count));
        mHeaderFocus.setText(hasFollowed ? "已关注" : "关注");
        mHeaderName.setText(StringHelper.getString(info.nickname));
        mHeaderNum.setText(String.format(getString(R.string.video_play_times),
                String.valueOf(info.play_count)));
        mHeaderTitle.setText(StringHelper.getString(info.title));

        if (info.commentCount == 0) {
            mCommentCountView.setVisibility(View.GONE);
        } else {
            mCommentCountView.setVisibility(View.VISIBLE);
            mCommentCountView.setText(String.valueOf(info.commentCount));
        }
    }

    private void addVideoPlayCount() {

        if (TextUtils.isEmpty(PAGE_VID)) {
            return;
        }

        Call<HttpResult<JSONObject>> call = userService.addVideoPlayCount(PAGE_VID);
        call.enqueue(new SimpleCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject data) {
            }

            @Override
            public void onFail(String code, String errorMsg) {
            }
        });
    }

    private void checkTitle() {

        mTitleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        mTitleShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentHelper.openShareBottomPage(VideoDetailActivity.this, PAGE_VID, PAGE_NEWS_TYPE,
                        NEWS_PUBLISHER);
            }
        });
    }

    private void loadData(final int page) {

        String userId = AccountManager.getInstance().getUserId();
        showLoading();
        Call<HttpResult<NewsInfoWrapper>> call = service.getNewsDetail(userId, PAGE_VID, 1);
        call.enqueue(new SimpleCallback<NewsInfoWrapper>() {
            @Override
            public void onSuccess(NewsInfoWrapper data) {

                hideLoading();
                if (page == 1) {
                    bindHeader(data.newInfo);
                }
            }

            @Override
            public void onFail(String code, String errorMsg) {
                onSimpleError(errorMsg);
            }
        });
    }

    private void loadComment(final int page) {

        String userId = AccountManager.getInstance().getUserId();

        showLoading();
        mRefreshHelper.setCurPage(page);
        final Call<HttpResult<NewsInfoWrapper>> call =
                service.getNewsComment(StringHelper.getString(userId), PAGE_VID, PAGE_NEWS_TYPE,
                        AppConstant.NONE_VALUE, page, AppConstant.DEFAULT_PAGE_SIZE);
        onAddCall(call);
        call.enqueue(new SimpleCallback<NewsInfoWrapper>() {
            @Override
            public void onSuccess(NewsInfoWrapper data) {
                hideLoading();
                onRemoveCall(call);

                if (page == 1 && (null == data
                        || null == data.commentlist
                        || data.commentlist.size() == 0)) {
                    mRefreshHelper.finishRefresh();
                    mAdapter.setEmptyView(mEmptyView);
                    return;
                }

                List<CommentBean> commentList = data.commentlist;
                if (null != commentList) {
                    //noinspection unchecked
                    mRefreshHelper.setData(data.commentlist, data.haveNext);
                }
            }

            @Override
            public void onFail(String code, String errorMsg) {
                onRemoveCall(call);
                onSimpleError(errorMsg);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();

        mVideoPlayer.getCurrentPlayer().onVideoPause();
        isPause = true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        mVideoPlayer.getCurrentPlayer().onVideoResume();
        isPause = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //保存进度
        long progress = GSYVideoManager.instance().getMediaPlayer().getCurrentPosition();
        Log.d("magical", " detail : " + progress);
        //if (-1 != progress) {
        if (!TextUtils.isEmpty(PAGE_VIDEO_URL)) {
            VideoManager.getInstance().putProgress(PAGE_VIDEO_URL, progress);
        }
        //}

        mVideoPlayer.getCurrentPlayer().release();
        if (orientationUtils != null) orientationUtils.releaseListener();
    }

    @Override
    public ArrayList<Call> addRequestList() {
        callList = new ArrayList<>();
        return callList;
    }

    @Override
    public void onBackPressed() {
        if (orientationUtils != null) {
            orientationUtils.backToProtVideo();
        }

        if (BottomHelper.isKeyboardShow(mRealBottomView)) {
            BottomHelper.recoverNormalBottom(mRealBottomView, mFakeBottomView, mRealInputView);
            return;
        }

        if (StandardGSYVideoPlayer.backFromWindowFull(this)) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //如果旋转了就全屏
        if (!isPause) {
            mVideoPlayer.onConfigurationChanged(this, newConfig, null);
        }
    }

    @OnClick(R.id.id_bottom_edit)
    public void onUserInput() {

        BottomHelper.showKeyboard(mRealBottomView, mFakeBottomView, mRealInputView);
    }

    @OnClick(R.id.id_send)
    public void onSendComment(View view) {

        UserOptionHelper.onSendComment(mRealInputView, service, PAGE_VID, PAGE_NEWS_TYPE,
                new SimplePreNotifyListener() {
                    @Override
                    public void onPreToDo() {
                        KeyboardUtils.hideSoftInput(VideoDetailActivity.this);
                        showLoading();
                    }

                    @Override
                    public void onSuccess(String msg) {
                        onCommentSuccess();
                    }

                    @Override
                    public void onFailed(String msg) {
                        onSimpleError(msg);
                    }
                });
    }

    /**
     * 评论成功
     */
    private void onCommentSuccess() {
        hideLoading();
        mRealInputView.setText("");
        ToastUtils.showShort("评论成功");
        onBackPressed();
        loadComment(1);
    }

    @OnClick(R.id.id_bottom_star)
    public void onClickStar(View view) {

        if (AccountManager.getInstance().checkLogin(this)) {
            return;
        }

        if (isLoading()) {
            return;
        }

        showLoading();
        if (hasCollected) {
            UserOptionHelper.onCancelStar(service, PAGE_VID, PAGE_NEWS_TYPE,
                    new SimplePreNotifyListener() {
                        @Override
                        public void onPreToDo() {
                        }

                        @Override
                        public void onSuccess(String msg) {
                            hideLoading();
                            changeStarState(false);
                        }

                        @Override
                        public void onFailed(String msg) {
                            onSimpleError(msg);
                        }
                    });
        } else {
            UserOptionHelper.onClickStar(service, PAGE_VID, PAGE_NEWS_TYPE,
                    new SimplePreNotifyListener() {

                        @Override
                        public void onPreToDo() {
                        }

                        @Override
                        public void onSuccess(String msg) {
                            hideLoading();
                            changeStarState(true);
                        }

                        @Override
                        public void onFailed(String msg) {
                            onSimpleError(msg);
                        }
                    });
        }
    }

    /**
     * 关注操作
     */
    public void onFollowUser(View view) {

        if (AccountManager.getInstance().checkLogin(this)) {
            return;
        }

        if (TextUtils.isEmpty(NEWS_PUBLISHER)) {
            return;
        }

        if (isLoading()) {
            return;
        }

        showLoading();
        if (hasFollowed) {
            // cancel follow
            UserOptionHelper.onCancelFollow(userService, NEWS_PUBLISHER,
                    new SimpleNotifyListener() {
                        @Override
                        public void onSuccess(String msg) {
                            hideLoading();
                            changeFollowState(false);
                        }

                        @Override
                        public void onFailed(String msg) {
                            onSimpleError(msg);
                        }
                    });
        } else {
            // add follow
            UserOptionHelper.onFollow(userService, NEWS_PUBLISHER, new SimpleNotifyListener() {
                @Override
                public void onSuccess(String msg) {
                    hideLoading();
                    changeFollowState(true);
                }

                @Override
                public void onFailed(String msg) {
                    onSimpleError(msg);
                }
            });
        }
    }

    /**
     * 更改 Star 状态
     */
    private void changeStarState(boolean collected) {
        hasCollected = collected;
        mStarView.setSelected(hasCollected);
    }

    /**
     * 更改 Follow 状态
     */
    private void changeFollowState(boolean hasFollowed) {
        this.hasFollowed = hasFollowed;
        mHeaderFocus.setText(this.hasFollowed ? "已关注" : "关注");
    }
}
