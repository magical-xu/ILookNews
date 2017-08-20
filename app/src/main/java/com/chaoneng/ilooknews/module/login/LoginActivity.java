package com.chaoneng.ilooknews.module.login;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;
import butterknife.BindView;
import butterknife.OnClick;
import com.chaoneng.ilooknews.R;
import com.chaoneng.ilooknews.base.BaseActivity;
import com.chaoneng.ilooknews.widget.ClearEditText;
import com.chaoneng.ilooknews.widget.PasswordEditText;
import com.magicalxu.library.blankj.ToastUtils;

/**
 * Created by magical on 17/8/20.
 * Description : 登录界面
 */

public class LoginActivity extends BaseActivity {

  @BindView(R.id.iv_finish) ImageView ivFinish;
  @BindView(R.id.id_page1_username) ClearEditText idPage1Username;
  @BindView(R.id.id_page1_pwd) PasswordEditText idPage1Pwd;
  @BindView(R.id.id_page2_username) ClearEditText idPage2Username;
  @BindView(R.id.tv_send_msg) TextView tvSendMsg;
  @BindView(R.id.id_page2_pwd) PasswordEditText idPage2Pwd;
  @BindView(R.id.id_toggle) ViewFlipper idToggle;
  @BindView(R.id.tv_login) TextView tvLogin;
  @BindView(R.id.tv_change_login_type) TextView tvChangeLoginType;
  @BindView(R.id.id_we_chat) ImageView idWeChat;
  @BindView(R.id.id_qq) ImageView idQq;
  @BindView(R.id.id_wei_bo) ImageView idWeiBo;
  @BindView(R.id.id_login_type) TextView mLoginType;

  boolean showPage;

  @Override
  public int getLayoutId() {
    return R.layout.activity_login;
  }

  @Override
  public void handleChildPage(Bundle savedInstanceState) {

  }

  @OnClick({
      R.id.iv_finish, R.id.tv_change_login_type, R.id.tv_login, R.id.id_we_chat, R.id.id_qq,
      R.id.id_wei_bo, R.id.tv_send_msg
  })
  public void onClickView(View view) {

    switch (view.getId()) {
      case R.id.iv_finish:
        finish();
        break;
      case R.id.tv_change_login_type:
        changeLoginType();
        break;
      case R.id.tv_login:
        onLogin();
        break;
      case R.id.id_we_chat:
        ToastUtils.showShort("微信登录");
        break;
      case R.id.id_qq:
        ToastUtils.showShort("QQ登录");
        break;
      case R.id.id_wei_bo:
        ToastUtils.showShort("微博登录");
        break;
      case R.id.tv_send_msg:
        sendMsg();
        break;
    }
  }

  private void sendMsg() {

    ToastUtils.showShort("发送短信");
  }

  private void onLogin() {

  }

  private void changeLoginType() {

    showPage = !showPage;

    idToggle.setDisplayedChild(showPage ? 1 : 0);
    mLoginType.setText(showPage ? "免密码登录" : "账号密码登录");
    tvChangeLoginType.setText(showPage ? "账号密码登录" : "免密码登录");
  }
}
