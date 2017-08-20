package com.chaoneng.ilooknews.module.user.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.OnClick;
import com.chaoneng.ilooknews.R;
import com.chaoneng.ilooknews.base.BaseActivity;
import com.chaoneng.ilooknews.module.user.widget.SettingItemView;
import com.chaoneng.ilooknews.util.IntentHelper;
import com.chaoneng.ilooknews.widget.ILookTitleBar;
import com.magicalxu.library.blankj.ToastUtils;

/**
 * Created by magical on 2017/8/19.
 * 设置界面
 */

public class SettingActivity extends BaseActivity {

  @BindView(R.id.id_edit) SettingItemView idEdit;
  @BindView(R.id.id_toggle_text) SettingItemView idToggleText;
  @BindView(R.id.id_clear_cache) SettingItemView idClearCache;
  @BindView(R.id.id_wifi) SettingItemView idWifi;
  @BindView(R.id.id_agreement) SettingItemView idAgreement;
  @BindView(R.id.id_version) SettingItemView idVersion;
  @BindView(R.id.id_about) SettingItemView idAbout;
  @BindView(R.id.tv_logout) TextView tvLogout;

  @Override
  public int getLayoutId() {
    return R.layout.activity_user_setting;
  }

  @Override
  protected boolean addTitleBar() {
    return true;
  }

  @Override
  public void handleChildPage(Bundle savedInstanceState) {

    init();
  }

  private void init() {

    mTitleBar.setTitle("设置");
    mTitleBar.setTitleListener(new ILookTitleBar.TitleCallbackAdapter() {
      @Override
      public void onClickLeft(View view) {
        super.onClickLeft(view);
        finish();
      }
    });

    idEdit.setTitle("个人资料编辑");

    idToggleText.setTitle("字体大小").hideRightArrow().setRightText("中等");

    idClearCache.setTitle("清除缓存").setRightText("32MB").hideRightArrow();

    idWifi.setTitle("非 WIFI 网络播放提醒").hideRightArrow().showToggle();
    idWifi.getToggle().setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (b) {
          ToastUtils.showShort("打开");
        } else {
          ToastUtils.showShort("关闭");
        }
      }
    });

    idAgreement.setTitle("系统协议");

    idVersion.setTitle("当前版本").setRightText("v1.0.0").hideRightArrow();

    idAbout.setTitle("关于我们");
  }

  @OnClick({
      R.id.id_edit, R.id.id_toggle_text, R.id.id_clear_cache, R.id.id_agreement, R.id.id_about,
      R.id.tv_logout
  })
  public void onClickView(View view) {

    switch (view.getId()) {
      case R.id.id_edit:
        IntentHelper.openProfilePage(this);
        break;
      case R.id.id_toggle_text:
        ToastUtils.showShort("個人資料");
        break;
      case R.id.id_clear_cache:
        ToastUtils.showShort("個人資料");
        break;
      case R.id.id_agreement:
        ToastUtils.showShort("個人資料");
        break;
      case R.id.id_about:
        ToastUtils.showShort("個人資料");
        break;
      case R.id.tv_logout:
        IntentHelper.openLoginPage(this);
        break;
    }
  }
}
