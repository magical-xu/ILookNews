package com.chaoneng.ilooknews.module.login;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;
import butterknife.BindView;
import butterknife.OnClick;
import com.chaoneng.ilooknews.R;
import com.chaoneng.ilooknews.api.LoginService;
import com.chaoneng.ilooknews.base.BaseActivity;
import com.chaoneng.ilooknews.data.UserWrapper;
import com.chaoneng.ilooknews.net.callback.SimpleCallback;
import com.chaoneng.ilooknews.net.client.NetRequest;
import com.chaoneng.ilooknews.net.data.HttpResult;
import com.chaoneng.ilooknews.util.IntentHelper;
import com.chaoneng.ilooknews.widget.ClearEditText;
import com.chaoneng.ilooknews.widget.PasswordEditText;
import com.magicalxu.library.blankj.ToastUtils;
import org.json.JSONObject;
import retrofit2.Call;

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
    private LoginService loginService;

    @Override
    public int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    public void handleChildPage(Bundle savedInstanceState) {

        loginService = NetRequest.getInstance().create(LoginService.class);
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
                //ToastUtils.showShort("微信登录");
                IntentHelper.openRegisterPage(this);
                break;
            case R.id.id_qq:
                ToastUtils.showShort("QQ登录");
                break;
            case R.id.id_wei_bo:
                ToastUtils.showShort("微博登录");
                break;
            case R.id.tv_send_msg:
                sendVerifyCode();
                break;
        }
    }

    /**
     * 发送验证码
     */
    private void sendVerifyCode() {

        String mobile = idPage2Username.getText().toString().trim();
        if (TextUtils.isEmpty(mobile)) {
            ToastUtils.showShort("请输入手机号！");
            return;
        }

        Call<HttpResult<JSONObject>> call = loginService.sendMobileCode(mobile);
        call.enqueue(new SimpleCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject data) {
                ToastUtils.showShort("验证码发送成功 ：" + data);
            }

            @Override
            public void onFail(String code, String errorMsg) {
                ToastUtils.showShort(errorMsg);
            }
        });
    }

    private void onLogin() {

        if (showPage) {
            // mobile login
            onMobileLogin();
        } else {
            // username pwd login
            onNameLogin();
        }
    }

    private void onMobileLogin() {

        String mobile = idPage2Username.getText().toString().trim();
        String code = idPage2Pwd.getText().toString().trim();

        if (TextUtils.isEmpty(mobile) || TextUtils.isEmpty(code)) {
            ToastUtils.showShort("请输入手机号和验证码！");
            return;
        }

        Call<HttpResult<UserWrapper>> call = loginService.loginByPhone(mobile, code);
        call.enqueue(new SimpleCallback<UserWrapper>() {
            @Override
            public void onSuccess(UserWrapper data) {
                ToastUtils.showShort(data.socialUser.id);
            }

            @Override
            public void onFail(String code, String errorMsg) {
                ToastUtils.showShort(errorMsg);
            }
        });
    }

    private void onNameLogin() {

        String username = idPage1Username.getText().toString().trim();
        String pwd = idPage1Pwd.getText().toString().trim();

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(pwd)) {
            ToastUtils.showShort("请输入用户名和密码！");
            return;
        }

        Call<HttpResult<UserWrapper>> call = loginService.loginByName(username, pwd);
        call.enqueue(new SimpleCallback<UserWrapper>() {
            @Override
            public void onSuccess(UserWrapper data) {
                ToastUtils.showShort(data.socialUser.id);
            }

            @Override
            public void onFail(String code, String errorMsg) {
                ToastUtils.showShort(errorMsg);
            }
        });
    }

    private void changeLoginType() {

        showPage = !showPage;

        idToggle.setDisplayedChild(showPage ? 1 : 0);
        mLoginType.setText(showPage ? "免密码登录" : "账号密码登录");
        tvChangeLoginType.setText(showPage ? "账号密码登录" : "免密码登录");
    }
}
