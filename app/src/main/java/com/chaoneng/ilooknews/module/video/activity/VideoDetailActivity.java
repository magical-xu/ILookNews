package com.chaoneng.ilooknews.module.video.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import com.chaoneng.ilooknews.AppConstant;
import com.chaoneng.ilooknews.R;
import com.chaoneng.ilooknews.base.BaseActivity;
import com.chaoneng.ilooknews.data.MockServer;
import com.chaoneng.ilooknews.library.gsyvideoplayer.VideoHelper;
import com.chaoneng.ilooknews.module.video.adapter.CommentAdapter;
import com.chaoneng.ilooknews.util.RefreshHelper;
import com.chaoneng.ilooknews.widget.image.HeadImageView;
import com.magicalxu.library.blankj.ToastUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer;

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

  //private boolean isPlay;
  private boolean isPause;

  private OrientationUtils orientationUtils;

  public static void newInstance(Context context, String vid) {
    Intent intent = new Intent(context, VideoDetailActivity.class);
    intent.putExtra(AppConstant.PAGE_TYPE, vid);
    context.startActivity(intent);
  }

  @Override
  public int getLayoutId() {
    return R.layout.activity_video_detail;
  }

  @Override
  public void handleChildPage(Bundle savedInstanceState) {

    checkTitle();
    mAdapter = new CommentAdapter(R.layout.item_video_comment);
    mRefreshHelper = new RefreshHelper(mRefreshLayout, mAdapter, mRecyclerView) {
      @Override
      public void onRequest(int page) {
        mockServer.mockGankCall(page, MockServer.Type.VIDEO_COMMENT);
      }
    };
    mockServer = MockServer.getInstance();
    mockServer.init(mRefreshHelper);
    bindHeader();

    orientationUtils = new OrientationUtils(this, mVideoPlayer);
    orientationUtils.setEnable(false);
    VideoHelper.initDetailPage(this, AppConstant.TEST_VIDEO_URL, mVideoPlayer, orientationUtils);

    mRefreshHelper.beginLoadData();
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
      }
    });
  }

  @Override
  protected void onPause() {
    super.onPause();

    GSYVideoManager.onPause();
    isPause = true;
  }

  @Override
  protected void onResume() {
    super.onResume();

    GSYVideoManager.onResume();
    isPause = false;
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();

    GSYVideoPlayer.releaseAllVideos();
    if (orientationUtils != null) orientationUtils.releaseListener();
  }

  @Override
  public void onBackPressed() {
    if (orientationUtils != null) {
      orientationUtils.backToProtVideo();
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
}
