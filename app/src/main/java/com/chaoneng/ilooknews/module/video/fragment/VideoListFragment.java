package com.chaoneng.ilooknews.module.video.fragment;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import butterknife.BindView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chaoneng.ilooknews.R;
import com.chaoneng.ilooknews.base.BaseFragment;
import com.chaoneng.ilooknews.data.MockServer;
import com.chaoneng.ilooknews.module.video.adapter.VideoListAdapter;
import com.chaoneng.ilooknews.util.IntentHelper;
import com.chaoneng.ilooknews.util.RefreshHelper;
import com.magicalxu.library.blankj.ToastUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerManager;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;

import static com.chaoneng.ilooknews.AppConstant.PAGE_TYPE;

/**
 * Created by magical on 17/8/19.
 * Description : 视频列表界面
 */

public class VideoListFragment extends BaseFragment {

  @BindView(R.id.id_recycler) RecyclerView mRecyclerView;
  @BindView(R.id.id_refresh_layout) SmartRefreshLayout mRefreshLayout;

  private VideoListAdapter mAdapter;
  private RefreshHelper mRefreshHelper;
  private MockServer mockServer;

  public static VideoListFragment newInstance(String type) {
    VideoListFragment fragment = new VideoListFragment();
    Bundle bundle = new Bundle();
    bundle.putString(PAGE_TYPE, type);
    fragment.setArguments(bundle);
    return fragment;
  }

  @Override
  protected void beginLoadData() {
    mRefreshHelper.beginLoadData();
  }

  @Override
  protected void doInit() {

    mAdapter = new VideoListAdapter(R.layout.item_main_video_list);
    mRefreshHelper = new RefreshHelper(mRefreshLayout, mAdapter, mRecyclerView) {
      @Override
      public void onRequest(int page) {
        mockServer.mockGankCall(page, MockServer.Type.VIDEO_LIST);
      }
    };
    mockServer = MockServer.getInstance();
    mockServer.init(mRefreshHelper);

    mRecyclerView.addOnChildAttachStateChangeListener(
        new RecyclerView.OnChildAttachStateChangeListener() {
          @Override
          public void onChildViewAttachedToWindow(View view) {
          }

          @Override
          public void onChildViewDetachedFromWindow(View view) {
            if (JCVideoPlayerManager.getFirstFloor() != null) {
              JCVideoPlayer videoPlayer = JCVideoPlayerManager.getFirstFloor();
              if (((ViewGroup) view).indexOfChild(videoPlayer) != -1
                  && videoPlayer.currentState == JCVideoPlayer.CURRENT_STATE_PLAYING) {
                JCVideoPlayer.releaseAllVideos();
              }
            }
          }
        });

    mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
      @Override
      public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        int viewId = view.getId();
        if (viewId == R.id.id_focus_plus) {
          ToastUtils.showShort(" 加关注 " + position);
        } else if (viewId == R.id.tv_comments) {
          ToastUtils.showShort("跳转视频详情" + position);
          IntentHelper.openVideoDetailPage(getActivity(), "");
        }
      }
    });
  }

  @Override
  protected boolean isNeedShowLoadingView() {
    return false;
  }

  @Override
  protected int getLayoutName() {
    return R.layout.simple_recycler_list;
  }

  @Override
  public void onPause() {
    super.onPause();
    JCVideoPlayerStandard.releaseAllVideos();
  }
}
