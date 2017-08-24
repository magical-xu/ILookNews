package com.chaoneng.ilooknews.module.video;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.widget.ImageView;
import butterknife.BindView;
import butterknife.OnClick;
import com.chaoneng.ilooknews.AppConstant;
import com.chaoneng.ilooknews.R;
import com.chaoneng.ilooknews.base.BaseTitleFragment;
import com.chaoneng.ilooknews.data.Channel;
import com.chaoneng.ilooknews.instance.TabManager;
import com.chaoneng.ilooknews.module.video.fragment.VideoListFragment;
import com.chaoneng.ilooknews.util.IntentHelper;
import com.chaoneng.ilooknews.widget.adapter.BaseFragmentAdapter;
import com.chaoneng.ilooknews.widget.adapter.OnPageChangeListener;
import com.flyco.tablayout.SlidingTabLayout;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by magical on 17/8/15.
 * Description : 主页 - 视频
 */

public class VideoMainFragment extends BaseTitleFragment {

  @BindView(R.id.sliding_tabs) SlidingTabLayout mTabView;
  @BindView(R.id.btn_search) ImageView mSearchView;
  @BindView(R.id.view_pager) ViewPager mViewPager;

  private BaseFragmentAdapter mPagerAdapter;
  private List<Fragment> videoFragmentList = new ArrayList<>();

  @Override
  public void init() {
    checkTitle();
    List<Channel> tabList = TabManager.getInstance().getTabList();

    for (Channel subTab : tabList) {
      videoFragmentList.add(VideoListFragment.newInstance(subTab.code));
    }

    mPagerAdapter = new BaseFragmentAdapter(getFragmentManager(), videoFragmentList,
        TabManager.getInstance().getTabNameList());

    mViewPager.setAdapter(mPagerAdapter);
    //mViewPager.setOffscreenPageLimit(3);
    mTabView.setViewPager(mViewPager);

    mViewPager.addOnPageChangeListener(new OnPageChangeListener() {
      @Override
      public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        // TODO: 17/8/16 暂停播放视频
        JCVideoPlayerStandard.releaseAllVideos();
      }
    });
  }

  private void checkTitle() {
    mTitleBar.setTitleImage(R.drawable.img_video_title)
        .setLeftCircle(AppConstant.TEST_AVATAR)
        .hideDivider();
  }

  @Override
  public int getSubLayout() {
    return R.layout.layout_main_video_fg;
  }

  @Override
  protected void beginLoadData() {

  }

  @Override
  protected boolean isNeedShowLoadingView() {
    return false;
  }

  @OnClick(R.id.btn_search)
  public void onClickSearch() {
    IntentHelper.openSearchPage(getActivity());
  }
}
