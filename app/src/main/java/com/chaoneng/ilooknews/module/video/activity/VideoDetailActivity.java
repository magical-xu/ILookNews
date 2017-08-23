package com.chaoneng.ilooknews.module.video.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chaoneng.ilooknews.AppConstant;
import com.chaoneng.ilooknews.ILookApplication;
import com.chaoneng.ilooknews.R;
import com.chaoneng.ilooknews.base.BaseActivity;
import com.chaoneng.ilooknews.data.MockServer;
import com.chaoneng.ilooknews.library.glide.GlideApp;
import com.chaoneng.ilooknews.module.video.adapter.VideoCommentAdapter;
import com.chaoneng.ilooknews.util.RefreshHelper;
import com.chaoneng.ilooknews.widget.image.HeadImageView;
import com.magicalxu.library.blankj.ToastUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;

/**
 * Created by magical on 17/8/20.
 * Description : 视频详情界面
 */

public class VideoDetailActivity extends BaseActivity {

  @BindView(R.id.video_player) JCVideoPlayerStandard mVideoPlayer;
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

  private VideoCommentAdapter mAdapter;
  private RefreshHelper mRefreshHelper;

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
    mVideoPlayer.setUp(ILookApplication.getLocalString(R.string.text_video),
        JCVideoPlayerStandard.SCREEN_LAYOUT_NORMAL,
        TextUtils.isEmpty("Description() ：架子鼓") ? "title " + "" : "");
    GlideApp.with(this)
        .load("http://p.qpic.cn/videoyun/0/2449_43b6f696980311e59ed467f22794e792_1/640")
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .centerCrop()
        .dontAnimate()
        .into(mVideoPlayer.thumbImageView);
    JCVideoPlayer.FULLSCREEN_ORIENTATION = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
    JCVideoPlayer.NORMAL_ORIENTATION = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;

    mAdapter = new VideoCommentAdapter(R.layout.item_video_comment);
    mRefreshHelper = new RefreshHelper(mRefreshLayout, mAdapter, mRecyclerView) {
      @Override
      public void onRequest(int page) {
        MockServer.getInstance().mockGankCall(page, MockServer.Type.VIDEO_COMMENT);
      }
    };
    MockServer.getInstance().init(mRefreshHelper);
    bindHeader();

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
        JCVideoPlayerStandard.releaseAllVideos();
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
    JCVideoPlayerStandard.releaseAllVideos();
  }

  @Override
  public void onBackPressed() {
    if (JCVideoPlayerStandard.backPress()) {
      return;
    }
    super.onBackPressed();
  }
}
