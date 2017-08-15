package com.chaoneng.ilooknews;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.widget.FrameLayout;
import butterknife.BindView;
import com.chaoneng.ilooknews.base.BaseActivity;
import com.chaoneng.ilooknews.data.TabEntity;
import com.chaoneng.ilooknews.module.focus.FocusMainFragment;
import com.chaoneng.ilooknews.module.home.HomeMainFragment;
import com.chaoneng.ilooknews.module.user.UserMainFragment;
import com.chaoneng.ilooknews.module.video.VideoMainFragment;
import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import java.util.ArrayList;

public class MainActivity extends BaseActivity {

  @BindView(R.id.id_main_container) FrameLayout mContainer;
  @BindView(R.id.id_main_tab_layout) CommonTabLayout mTabLayout;

  private String[] mTitles = { "首页", "视频", "关注", "我" };
  private int[] mIconUnSelectIds = {
      R.drawable.ic_home_normal, R.drawable.ic_video_normal, R.drawable.ic_care_normal,
      R.drawable.ic_profile_normal
  };
  private int[] mIconSelectIds = {
      R.drawable.ic_home_selected, R.drawable.ic_video_selected, R.drawable.ic_care_selected,
      R.drawable.ic_profile_selected
  };
  private ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();

  private HomeMainFragment newsMainFragment;
  private VideoMainFragment videoMainFragment;
  private FocusMainFragment careMainFragment;
  private UserMainFragment userMainFragment;

  @Override
  public int getLayoutId() {
    return R.layout.activity_main;
  }

  @Override
  public void handleChildPage(Bundle savedInstanceState) {

    initTab();
    initFragment(savedInstanceState);
  }

  /**
   * 初始化tab
   */
  private void initTab() {
    for (int i = 0; i < mTitles.length; i++) {
      mTabEntities.add(new TabEntity(mTitles[i], mIconSelectIds[i], mIconUnSelectIds[i]));
    }
    mTabLayout.setTabData(mTabEntities);
    //点击监听
    mTabLayout.setOnTabSelectListener(new OnTabSelectListener() {
      @Override
      public void onTabSelect(int position) {
        SwitchTo(position);
      }
      @Override
      public void onTabReselect(int position) {
      }
    });
  }
  /**
   * 初始化碎片
   */
  private void initFragment(Bundle savedInstanceState) {
    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
    int currentTabPosition = 0;
    if (savedInstanceState != null) {
      newsMainFragment = (HomeMainFragment) getSupportFragmentManager().findFragmentByTag("newsMainFragment");
      videoMainFragment = (VideoMainFragment) getSupportFragmentManager().findFragmentByTag("videoMainFragment");
      careMainFragment = (FocusMainFragment) getSupportFragmentManager().findFragmentByTag("careMainFragment");
      userMainFragment = (UserMainFragment) getSupportFragmentManager().findFragmentByTag("userMainFragment");
      currentTabPosition = savedInstanceState.getInt(AppConstant.HOME_CURRENT_TAB_POSITION);
    } else {
      newsMainFragment = new HomeMainFragment();
      videoMainFragment = new VideoMainFragment();
      careMainFragment = new FocusMainFragment();
      userMainFragment = new UserMainFragment();

      transaction.add(R.id.id_main_container, newsMainFragment, "newsMainFragment");
      transaction.add(R.id.id_main_container, videoMainFragment, "videoMainFragment");
      transaction.add(R.id.id_main_container, careMainFragment, "careMainFragment");
      transaction.add(R.id.id_main_container, userMainFragment, "userMainFragment");
    }
    transaction.commit();
    SwitchTo(currentTabPosition);
    mTabLayout.setCurrentTab(currentTabPosition);
  }

  /**
   * 切换
   */
  private void SwitchTo(int position) {
    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
    //JCVideoPlayerStandard.releaseAllVideos(); //补丁
    switch (position) {
      //首页
      case 0:
        transaction.show(newsMainFragment);
        transaction.hide(videoMainFragment);
        transaction.hide(careMainFragment);
        transaction.hide(userMainFragment);
        transaction.commitAllowingStateLoss();
        break;
      //视频
      case 1:
        transaction.hide(newsMainFragment);
        transaction.show(videoMainFragment);
        transaction.hide(careMainFragment);
        transaction.hide(userMainFragment);
        transaction.commitAllowingStateLoss();
        break;
      //关注
      case 2:
        transaction.hide(newsMainFragment);
        transaction.hide(videoMainFragment);
        transaction.show(careMainFragment);
        transaction.hide(userMainFragment);
        transaction.commitAllowingStateLoss();
        break;
      case 3:
        transaction.hide(newsMainFragment);
        transaction.hide(videoMainFragment);
        transaction.hide(careMainFragment);
        transaction.show(userMainFragment);
        transaction.commitAllowingStateLoss();
        break;
      default:
        break;
    }
  }
}
