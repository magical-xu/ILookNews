package com.chaoneng.ilooknews.module.user.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.OnClick;
import com.chaoneng.ilooknews.AppConstant;
import com.chaoneng.ilooknews.R;
import com.chaoneng.ilooknews.base.BaseActivity;
import com.chaoneng.ilooknews.module.user.fragment.BrokeNewsListFragment;
import com.chaoneng.ilooknews.module.user.fragment.StateListFragment;
import com.chaoneng.ilooknews.widget.adapter.BaseFragmentAdapter;
import com.chaoneng.ilooknews.widget.image.HeadImageView;
import com.flyco.tablayout.SlidingTabLayout;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by magical on 2017/8/23.
 * 他人中心界面
 */
public class UserCenterActivity extends BaseActivity {

  @BindView(R.id.iv_avatar) HeadImageView ivAvatar;
  @BindView(R.id.tv_name) TextView tvName;
  @BindView(R.id.tv_focus) TextView tvFocus;
  @BindView(R.id.tv_signature) TextView tvSignature;
  @BindView(R.id.sliding_tabs) SlidingTabLayout slidingTabLayout;
  @BindView(R.id.view_pager) ViewPager viewPager;
  @BindView(R.id.toolbar) Toolbar toolbar;

  private BaseFragmentAdapter baseFragmentAdapter;

  @Override
  public int getLayoutId() {
    return R.layout.activity_user_center;
  }

  @Override
  public void handleChildPage(Bundle savedInstanceState) {

    initView();
  }

  public void initView() {
    initUserInfo();

    ArrayList<Fragment> fragments = new ArrayList<>();
    fragments.add(new StateListFragment());
    fragments.add(new BrokeNewsListFragment());

    List<String> titles = new ArrayList<>(2);
    titles.add("动态");
    titles.add("爆料");

    String[] titleArray = new String[] { "动态", "爆料" };
    baseFragmentAdapter = new BaseFragmentAdapter(getSupportFragmentManager(), fragments, titles);
    viewPager.setAdapter(baseFragmentAdapter);
    slidingTabLayout.setViewPager(viewPager);
  }

  private void initUserInfo() {

    setSupportActionBar(toolbar);
    toolbar.setNavigationIcon(R.drawable.ic_back);

    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        onBackPressed();
      }
    });

    ivAvatar.setHeadImage(AppConstant.TEST_AVATAR);
    tvName.setText(getResources().getString(R.string.test_text_short));
    tvSignature.setText(AppConstant.TEST_SIGN);
  }

  @OnClick({ R.id.iv_avatar, R.id.tv_focus })
  public void onViewClicked(View view) {
    switch (view.getId()) {
      case R.id.iv_avatar:
        break;
      case R.id.tv_focus:
        break;
    }
  }
}
