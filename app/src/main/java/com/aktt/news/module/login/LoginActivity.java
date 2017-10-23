package com.aktt.news.module.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;
import butterknife.BindView;
import butterknife.OnClick;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import com.aktt.news.AppConstant;
import com.aktt.news.R;
import com.aktt.news.api.LoginService;
import com.aktt.news.base.BaseActivity;
import com.aktt.news.data.UserWrapper;
import com.aktt.news.instance.AccountManager;
import com.aktt.news.library.mob.MobHelper;
import com.aktt.news.library.shareloginlib.ShareLoginHelper;
import com.aktt.news.net.callback.SimpleCallback;
import com.aktt.news.net.client.NetRequest;
import com.aktt.news.net.data.HttpResult;
import com.aktt.news.util.IntentHelper;
import com.aktt.news.widget.edit.ClearEditText;
import com.aktt.news.widget.edit.PasswordEditText;
import com.liulishuo.share.type.SsoLoginType;
import com.magicalxu.library.blankj.KeyboardUtils;
import com.magicalxu.library.blankj.ToastUtils;
import retrofit2.Call;

import static com.magicalxu.library.blankj.ToastUtils.showShort;

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
    @BindView(R.id.tv_to_register) TextView mRegisterTv;

    boolean showPage;
    private LoginService loginService;
    private EventHandler eventHandler;
    private long lastTimeMills;

    private String lastMobile;      //防止在验证 验证码的时候 用户重新操作了 输入框 导致验证失效
    //private String lastPwd;         //防止在验证 验证码的时候 用户重新操作了 输入框 导致验证失效

    @Override
    public int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    public void handleChildPage(Bundle savedInstanceState) {

        loginService = NetRequest.getInstance().create(LoginService.class);

        eventHandler = new EventHandler() {
            @Override
            public void afterEvent(int event, int result, Object data) {
                super.afterEvent(event, result, data);
                hideLoadingOnUiThread();
                if (data instanceof Throwable) {
                    Throwable throwable = (Throwable) data;
                    String msg = throwable.getMessage();
                    onUiThread(msg);
                } else {
                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                        //提交验证码成功
                        //onUiThread("验证成功，接下来走登录流程");
                        onRealMobileLogin();
                    } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                        //获取验证码成功
                        if (result == SMSSDK.RESULT_COMPLETE) {
                            boolean smart = (Boolean) data;
                            if (smart) {
                                //通过智能验证
                            } else {
                                //依然走短信验证
                                onUiThread("已成功发送验证码");
                            }
                        }
                    } else if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {
                        //返回支持发送验证码的国家列表
                        //ToastUtils.showShort(data.toString());
                        Log.d("magical", data.toString());
                    }
                }
            }
        };

        MobHelper.register(eventHandler);
    }

    @OnClick({
            R.id.iv_finish, R.id.tv_change_login_type, R.id.tv_login, R.id.id_we_chat, R.id.id_qq,
            R.id.id_wei_bo, R.id.tv_send_msg, R.id.tv_to_register
    })
    public void onClickView(View view) {

        switch (view.getId()) {
            case R.id.iv_finish:
                finish();
                break;
            case R.id.tv_to_register:
                IntentHelper.openRegisterPage(this);
                break;
            case R.id.tv_change_login_type:
                changeLoginType();
                break;
            case R.id.tv_login:
                onLogin();
                break;
            case R.id.id_we_chat:
                showShort("微信登录");
                onThirdLogin(SsoLoginType.WEIXIN);
                break;
            case R.id.id_qq:
                showShort("QQ登录");
                onThirdLogin(SsoLoginType.QQ);
                break;
            case R.id.id_wei_bo:
                showShort("微博登录");
                onThirdLogin(SsoLoginType.WEIBO);
                break;
            case R.id.tv_send_msg:
                sendVerifyCode();
                break;
        }
    }

    private void onThirdLogin(final String type) {

        int serverType;
        if (TextUtils.equals(SsoLoginType.QQ, type)) {
            serverType = 4;
        } else if (TextUtils.equals(SsoLoginType.WEIXIN, type)) {
            serverType = 3;
        } else if (TextUtils.equals(SsoLoginType.WEIBO, type)) {
            serverType = 5;
        } else {
            serverType = AppConstant.INVALIDATE;
        }

        final int copyInt = serverType;
        ShareLoginHelper.login(this, type, new ShareLoginHelper.ThirdLoginListener() {
            @Override
            public void onSuccess(String accessToken, String uId, long expiresIn,
                    @Nullable String wholeData) {
                onRequestInnerServer(copyInt, accessToken, String.valueOf(expiresIn), wholeData);
            }

            @Override
            public void onCancel() {
                showShort("登录取消");
            }

            @Override
            public void onError(String msg) {
                showShort(msg);
            }
        });
    }

    private void onRequestInnerServer(int type, String token, String expired, String wholeData) {

        loginService.onThirdLogin(String.valueOf(type), token, expired, null);
    }

    public void onUiThread(final String msg) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showShort(msg);
            }
        });
    }

    /**
     * 发送验证码
     */
    private void sendVerifyCode() {

        String mobile = idPage2Username.getText().toString().trim();
        if (TextUtils.isEmpty(mobile)) {
            showShort("请输入手机号！");
            return;
        }

        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis - lastTimeMills < 60 * 1000) {
            ToastUtils.showShort(R.string.cannot_send_verify_code_in_60_seconds);
            return;
        }

        lastTimeMills = currentTimeMillis;

        lastMobile = mobile;    //重要，记录真正验证的手机号
        showLoading();
        MobHelper.getInstance().getVerifyCode(lastMobile);
    }

    private void onLogin() {

        KeyboardUtils.hideSoftInput(this);
        if (showPage) {
            onMobileLogin();
        } else {
            onNameLogin();
        }
    }

    /**
     * 手机号登录
     */
    private void onMobileLogin() {

        String mobile = idPage2Username.getText().toString().trim();
        String code = idPage2Pwd.getText().toString().trim();

        if (TextUtils.isEmpty(mobile) || TextUtils.isEmpty(code)) {
            showShort("请输入手机号和验证码！");
            return;
        }

        if (TextUtils.isEmpty(lastMobile)) {
            showShort("请先获取正确的验证码！");
            return;
        }

        if (!TextUtils.equals(mobile, lastMobile)) {
            showShort("验证的手机号和当前输入手机号不一致！");
            return;
        }

        showLoading();
        checkVerifyCode();
    }

    private void onRealMobileLogin() {
        showLoadingOnUiThread();
        Call<HttpResult<UserWrapper>> call = loginService.loginByPhone(lastMobile);
        call.enqueue(new SimpleCallback<UserWrapper>() {
            @Override
            public void onSuccess(UserWrapper data) {
                onLoginSuccess(data);
            }

            @Override
            public void onFail(String code, String errorMsg) {
                onSimpleError(errorMsg);
            }
        });
    }

    /**
     * 通过Mob Sdk 验证验证码
     */
    private void checkVerifyCode() {

        String mobile = idPage2Username.getText().toString();
        //lastPwd = idPage2Pwd.getText().toString();
        String code = idPage2Pwd.getText().toString();

        if (TextUtils.isEmpty(mobile) || TextUtils.isEmpty(code)) {
            showShort(R.string.plz_input_mobile_or_code);
            return;
        }

        MobHelper.getInstance().submitVerifyCode(lastMobile, code);
    }

    /**
     * 账号密码登录
     */
    private void onNameLogin() {

        String username = idPage1Username.getText().toString().trim();
        String pwd = idPage1Pwd.getText().toString().trim();

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(pwd)) {
            showShort(R.string.plz_input_user_and_pwd);
            return;
        }

        showLoading();
        Call<HttpResult<UserWrapper>> call = loginService.loginByName(username, pwd);
        call.enqueue(new SimpleCallback<UserWrapper>() {
            @Override
            public void onSuccess(UserWrapper data) {
                onLoginSuccess(data);
            }

            @Override
            public void onFail(String code, String errorMsg) {
                onSimpleError(errorMsg);
            }
        });
    }

    /**
     * 登录成功
     *
     * @param data 用戶包裝信息
     */
    private void onLoginSuccess(UserWrapper data) {

        hideLoading();
        AccountManager.getInstance().saveUser(data.socialUser);
        finish();
    }

    /**
     * 更换登录类型
     */
    private void changeLoginType() {

        showPage = !showPage;

        idToggle.setDisplayedChild(showPage ? 1 : 0);
        mLoginType.setText(showPage ? "免密码登录" : "账号密码登录");
        tvChangeLoginType.setText(showPage ? "账号密码登录" : "免密码登录");
        idPage2Pwd.setHint(showPage ? "验证码" : "密码");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppConstant.REQUEST_CODE && resultCode == RESULT_OK) {
            // register success
            Log.d("magical", " onActivityResult : back from register page");
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MobHelper.unRegister(eventHandler);
    }
}
