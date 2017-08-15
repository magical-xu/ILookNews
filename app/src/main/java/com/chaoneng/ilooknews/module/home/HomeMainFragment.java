package com.chaoneng.ilooknews.module.home;

import android.support.v4.view.ViewPager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import butterknife.BindView;
import butterknife.OnClick;
import com.chaoneng.ilooknews.AppConstant;
import com.chaoneng.ilooknews.R;
import com.chaoneng.ilooknews.base.BaseFragment;
import com.chaoneng.ilooknews.widget.image.HeadImageView;
import com.flyco.tablayout.SlidingTabLayout;
import com.magicalxu.library.blankj.ToastUtils;

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

  @Override
  protected void beginLoadData() {

    mHeadView.setHeadImage(AppConstant.TEST_AVATAR);
  }

  @Override
  protected void doInit() {

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
}
