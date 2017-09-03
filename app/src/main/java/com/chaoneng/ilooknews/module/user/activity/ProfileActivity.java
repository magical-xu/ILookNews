package com.chaoneng.ilooknews.module.user.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import butterknife.BindView;
import butterknife.OnClick;
import com.bilibili.boxing.Boxing;
import com.bilibili.boxing.model.config.BoxingConfig;
import com.bilibili.boxing.model.entity.BaseMedia;
import com.bilibili.boxing_impl.ui.BoxingActivity;
import com.chaoneng.ilooknews.AppConstant;
import com.chaoneng.ilooknews.R;
import com.chaoneng.ilooknews.base.BaseActivity;
import com.chaoneng.ilooknews.module.user.widget.SettingItemView;
import com.chaoneng.ilooknews.widget.ilook.ILookTitleBar;
import com.magicalxu.library.blankj.ToastUtils;
import java.util.List;

/**
 * Created by magical on 17/8/20.
 * Description : 编辑资料界面
 */

public class ProfileActivity extends BaseActivity {

  @BindView(R.id.id_modify_avatar) SettingItemView modifyAvatar;
  @BindView(R.id.id_modify_nick) SettingItemView modifyNick;
  @BindView(R.id.id_modify_sign) SettingItemView modifySign;

  @Override
  public int getLayoutId() {
    return R.layout.activity_profile;
  }

  @Override
  protected boolean addTitleBar() {
    return true;
  }

  @Override
  public void handleChildPage(Bundle savedInstanceState) {

    mTitleBar.setTitle("编辑资料");
    mTitleBar.setTitleListener(new ILookTitleBar.TitleCallbackAdapter() {
      @Override
      public void onClickLeft(View view) {
        super.onClickLeft(view);
        finish();
      }
    });

    modifyAvatar.setTitle("修改头像").setHead(AppConstant.TEST_AVATAR);

    modifyNick.setTitle("修改昵称").setRightText("magical");

    modifySign.setTitle("签名").setRightText(" just do it !");
  }

  @OnClick({ R.id.id_modify_avatar, R.id.id_modify_nick, R.id.id_modify_sign })
  public void onClickView(View view) {

    switch (view.getId()) {
      case R.id.id_modify_avatar:
        BoxingConfig config = new BoxingConfig(
            BoxingConfig.Mode.SINGLE_IMG); // Mode：Mode.SINGLE_IMG, Mode.MULTI_IMG, Mode.VIDEO
        Boxing.of(config).withIntent(this, BoxingActivity.class).start(this, 1024);

        break;
      case R.id.id_modify_nick:
        ToastUtils.showShort("修改昵称");
        break;
      case R.id.id_modify_sign:
        ToastUtils.showShort("修改签名");
        break;
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    List<BaseMedia> medias = Boxing.getResult(data);
    //注意判断null
    if (null != medias) {
      ToastUtils.showShort(medias.size() + "");
    }
  }
}
