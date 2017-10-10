package com.chaoneng.ilooknews.module.video.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.OnClick;
import com.chaoneng.ilooknews.AppConstant;
import com.chaoneng.ilooknews.R;
import com.chaoneng.ilooknews.api.HomeService;
import com.chaoneng.ilooknews.base.BaseActivity;
import com.chaoneng.ilooknews.data.MockServer;
import com.chaoneng.ilooknews.data.NewsInfoWrapper;
import com.chaoneng.ilooknews.instance.VideoManager;
import com.chaoneng.ilooknews.library.gsyvideoplayer.VideoHelper;
import com.chaoneng.ilooknews.module.video.adapter.CommentAdapter;
import com.chaoneng.ilooknews.net.callback.SimpleCallback;
import com.chaoneng.ilooknews.net.client.NetRequest;
import com.chaoneng.ilooknews.net.data.HttpResult;
import com.chaoneng.ilooknews.util.BottomHelper;
import com.chaoneng.ilooknews.util.RefreshHelper;
import com.chaoneng.ilooknews.widget.image.HeadImageView;
import com.magicalxu.library.blankj.ToastUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;
import retrofit2.Call;

/**
 * Created by magical on 17/8/20.
 * Description : 视频详情界面
 */

public class VideoDetailActivity extends BaseActivity {

    @BindView(R.id.video_player) StandardGSYVideoPlayer mVideoPlayer;
    @BindView(R.id.id_title_back) ImageView mTitleBack;
    @BindView(R.id.id_title_share) ImageView mTitleShare;
    @BindView(R.id.id_recycler) RecyclerView mRecyclerView;
    @BindView(R.id.id_refresh_layout) SmartRefreshLayout mRefreshLayout;

    @BindView(R.id.id_bottom_edit) TextView mFakeInputView;
    @BindView(R.id.id_real_bottom_edit) EditText mRealInputView;
    @BindView(R.id.id_real_bottom) ViewGroup mRealBottomView;
    @BindView(R.id.id_fake_bottom) ViewGroup mFakeBottomView;

    TextView mHeaderTitle;
    TextView mHeaderNum;
    TextView mHeaderUp;
    TextView mHeaderDown;
    HeadImageView mHeaderIv;
    TextView mHeaderName;
    TextView mHeaderFocus;

    private CommentAdapter mAdapter;
    private RefreshHelper mRefreshHelper;
    private MockServer mockServer;
    private HomeService service;

    //private boolean isPlay;
    private boolean isPause;

    private OrientationUtils orientationUtils;

    private String PAGE_VID;        //新闻 vid
    private long PAGE_PROGRESS;     //当前播放进度

    public static void newInstance(Context context, String vid, long seek) {
        Intent intent = new Intent(context, VideoDetailActivity.class);
        intent.putExtra(AppConstant.PAGE_TYPE, vid);
        intent.putExtra(AppConstant.PAGE_PROGRESS, seek);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_video_detail;
    }

    @Override
    public void handleChildPage(Bundle savedInstanceState) {

        checkIntent();
        checkTitle();
        mAdapter = new CommentAdapter(false, R.layout.item_video_comment);
        mRefreshHelper = new RefreshHelper(mRefreshLayout, mAdapter, mRecyclerView) {
            @Override
            public void onRequest(int page) {
                mockServer.mockGankCall(page, MockServer.Type.VIDEO_COMMENT);
            }
        };

        service = NetRequest.getInstance().create(HomeService.class);
        mockServer = MockServer.getInstance();
        mockServer.init(mRefreshHelper);
        bindHeader();

        orientationUtils = new OrientationUtils(this, mVideoPlayer);
        orientationUtils.setEnable(false);
        VideoHelper.initDetailPage(this, AppConstant.TEST_VIDEO_URL, mVideoPlayer, orientationUtils,
                PAGE_PROGRESS);

        mRefreshHelper.beginLoadData();
    }

    private void checkIntent() {

        Intent intent = getIntent();
        if (null != intent) {
            PAGE_VID = intent.getStringExtra(AppConstant.PAGE_TYPE);
            PAGE_PROGRESS = intent.getLongExtra(AppConstant.PAGE_PROGRESS, 0);
        }
    }

    private void bindHeader() {
        View view = getLayoutInflater().inflate(R.layout.header_video_coments, null);

        mHeaderTitle = view.findViewById(R.id.tv_title);
        mHeaderNum = view.findViewById(R.id.tv_play_num);
        mHeaderUp = view.findViewById(R.id.tv_up);
        mHeaderDown = view.findViewById(R.id.tv_down);
        mHeaderIv = view.findViewById(R.id.id_header_iv);
        mHeaderName = view.findViewById(R.id.id_header_name);
        mHeaderFocus = view.findViewById(R.id.id_header_focus);

        mHeaderIv.setHeadImage(AppConstant.TEST_AVATAR);
        mHeaderDown.setText("123");
        mHeaderUp.setText("35");
        mHeaderFocus.setText("关注");
        mHeaderName.setText("magical");
        mHeaderNum.setText("3333次播放");
        mHeaderTitle.setText("繁华过后你还有我");
        mAdapter.addHeaderView(view);
    }

    private void checkTitle() {

        mTitleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mTitleShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ToastUtils.showShort("分享");
                onShowShare();
            }
        });
    }

    private void onShowShare() {

        //IntentHelper.openShareBottomPage(this,PAGE_VID,);
    }

    private void loadData(int page) {

        showLoading();
        Call<HttpResult<NewsInfoWrapper>> call = service.getNewsDetail(PAGE_VID, 1);
        call.enqueue(new SimpleCallback<NewsInfoWrapper>() {
            @Override
            public void onSuccess(NewsInfoWrapper data) {

            }

            @Override
            public void onFail(String code, String errorMsg) {
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
        VideoManager.getInstance().putProgress(AppConstant.TEST_VIDEO_URL, progress);
        //}

        mVideoPlayer.getCurrentPlayer().release();
        if (orientationUtils != null) orientationUtils.releaseListener();
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
}
