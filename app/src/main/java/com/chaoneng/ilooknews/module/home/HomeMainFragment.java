package com.chaoneng.ilooknews.module.home;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import butterknife.BindView;
import butterknife.OnClick;
import com.chaoneng.ilooknews.AppConstant;
import com.chaoneng.ilooknews.R;
import com.chaoneng.ilooknews.base.BaseFragment;
import com.chaoneng.ilooknews.data.Channel;
import com.chaoneng.ilooknews.instance.TabManager;
import com.chaoneng.ilooknews.module.home.fragment.NewsListFragment;
import com.chaoneng.ilooknews.widget.adapter.BaseFragmentStateAdapter;
import com.chaoneng.ilooknews.widget.adapter.OnPageChangeListener;
import com.chaoneng.ilooknews.widget.image.HeadImageView;
import com.flyco.tablayout.SlidingTabLayout;
import com.magicalxu.library.blankj.ToastUtils;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by magical on 17/8/15.
 * Description : 主页 - 首页
 */

public class HomeMainFragment extends BaseFragment {

  @BindView(R.id.iv_avatar) HeadImageView mHeadView;
  @BindView(R.id.ll_search) LinearLayout mSearchView;
  @BindView(R.id.iv_notify) ImageView mNotifyView;
  @BindView(R.id.sliding_tabs) SlidingTabLayout mTabView;
  @BindView(R.id.iv_edit_channel) ImageView mMorePlusView;
  @BindView(R.id.id_view_pager) ViewPager mViewPager;

  private BaseFragmentStateAdapter mPagerAdapter;
  private List<Fragment> newsFragmentList = new ArrayList<>();

  @Override
  protected void beginLoadData() {

    mHeadView.setHeadImage(AppConstant.TEST_AVATAR);
  }

  @Override
  protected void doInit() {

    List<Channel> tabList = TabManager.getInstance().getTabList();

    for (Channel subTab : tabList) {
      newsFragmentList.add(NewsListFragment.newInstance(subTab.code));
    }

    mPagerAdapter = new BaseFragmentStateAdapter(getFragmentManager(), newsFragmentList,
        TabManager.getInstance().getTabNameList());

    mViewPager.setAdapter(mPagerAdapter);
    mTabView.setViewPager(mViewPager);

    mViewPager.addOnPageChangeListener(new OnPageChangeListener() {
      @Override
      public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        // TODO: 17/8/16 暂停播放视频
      }
    });
  }

  @Override
  protected boolean isNeedShowLoadingView() {
    return false;
  }

  @Override
  protected int getLayoutName() {
    return R.layout.layout_main_home_fg;
  }

  @OnClick(R.id.ll_search)
  public void toSearchPage() {
    ToastUtils.showShort("搜索");
  }

  @OnClick(R.id.iv_avatar)
  public void toUserCenterPage() {
    ToastUtils.showShort("个人中心");
  }

  @OnClick(R.id.iv_notify)
  public void toNotifyPage() {
    ToastUtils.showShort("通知");
  }

  @OnClick(R.id.iv_edit_channel)
  public void toMoreTabPage() {
    ToastUtils.showShort("添加标签");
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
  }
}
