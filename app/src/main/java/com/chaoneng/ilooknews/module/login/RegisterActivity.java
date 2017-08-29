package com.chaoneng.ilooknews.module.login;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.OnClick;
import com.chaoneng.ilooknews.R;
import com.chaoneng.ilooknews.api.LoginService;
import com.chaoneng.ilooknews.base.BaseActivity;
import com.chaoneng.ilooknews.data.UserWrapper;
import com.chaoneng.ilooknews.net.callback.SimpleCallback;
import com.chaoneng.ilooknews.net.client.NetRequest;
import com.chaoneng.ilooknews.net.data.HttpResult;
import com.magicalxu.library.blankj.ToastUtils;
import org.json.JSONObject;
import retrofit2.Call;

/**
 * Created by magical on 2017/8/29.
 * Description : 测试注册
 */

public class RegisterActivity extends BaseActivity {

    @BindView(R.id.id_mobile) EditText idMobile;
    @BindView(R.id.id_code) EditText idCode;
    @BindView(R.id.id_username) EditText idUsername;
    @BindView(R.id.id_pwd) EditText idPwd;
    @BindView(R.id.id_register) TextView mRegisterBtn;
    @BindView(R.id.id_get_code) TextView mGetCodeBtn;

    private LoginService loginService;

    @Override
    public int getLayoutId() {
        return R.layout.activity_register;
    }

    @Override
    public void handleChildPage(Bundle savedInstanceState) {

        loginService = NetRequest.getInstance().create(LoginService.class);
    }

    @OnClick({ R.id.id_register, R.id.id_get_code })
    public void onViewClick(View view) {

        String mobile = idMobile.getText().toString().trim();

        switch (view.getId()) {
            case R.id.id_register:

                String code = idCode.getText().toString().trim();
                String userName = idUsername.getText().toString().trim();
                String pwd = idPwd.getText().toString().trim();

                if (TextUtils.isEmpty(mobile)) {
                    testNameRegister(userName, pwd);
                } else {
                    testMobileRegister(mobile, code);
                }
                break;
            case R.id.id_get_code:
                sendVerifyCode(mobile);
                break;
        }
    }

    /**
     * 发送验证码
     */
    private void sendVerifyCode(String mobile) {

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

    /**
     * 测试手机号注册
     */
    private void testMobileRegister(String mobile, String code) {

        if (TextUtils.isEmpty(mobile) || TextUtils.isEmpty(code)) {
            ToastUtils.showShort("请输入手机号和验证码");
            return;
        }

        Call<HttpResult<UserWrapper>> call = loginService.registerByPhone(mobile, code);
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

    /**
     * 测试用户名密码注册
     */
    private void testNameRegister(String userName, String pwd) {

        if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(pwd)) {
            ToastUtils.showShort("请输入用户名和密码");
            return;
        }

        Call<HttpResult<UserWrapper>> call = loginService.registerByName(userName, pwd);
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
}
