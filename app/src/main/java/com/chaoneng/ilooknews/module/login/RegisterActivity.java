package com.chaoneng.ilooknews.module.login;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.OnClick;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import com.chaoneng.ilooknews.R;
import com.chaoneng.ilooknews.api.LoginService;
import com.chaoneng.ilooknews.base.BaseActivity;
import com.chaoneng.ilooknews.data.UserWrapper;
import com.chaoneng.ilooknews.library.mob.MobHelper;
import com.chaoneng.ilooknews.net.callback.SimpleCallback;
import com.chaoneng.ilooknews.net.client.NetRequest;
import com.chaoneng.ilooknews.net.data.HttpResult;
import retrofit2.Call;

import static com.magicalxu.library.blankj.ToastUtils.showShort;

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
    private EventHandler eventHandler;

    @Override
    public int getLayoutId() {
        return R.layout.activity_register;
    }

    @Override
    public void handleChildPage(Bundle savedInstanceState) {

        loginService = NetRequest.getInstance().create(LoginService.class);

        eventHandler = new EventHandler() {
            @Override
            public void afterEvent(int event, int result, Object data) {
                super.afterEvent(event, result, data);
                if (data instanceof Throwable) {
                    Throwable throwable = (Throwable) data;
                    String msg = throwable.getMessage();
                    onUiThread(msg);
                } else {
                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                        //提交验证码成功
                        onUiThread("验证成功，接下来走注册流程");
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

    public void onUiThread(final String msg) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showShort(msg);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MobHelper.unRegister(eventHandler);
    }

    @OnClick({ R.id.id_register, R.id.id_get_code, R.id.id_get_support })
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
            case R.id.id_get_support:
                getMobSupport();
                break;
        }
    }

    private void getMobSupport() {

        MobHelper.getInstance().getSupportCountry();
    }

    /**
     * 发送验证码
     */
    private void sendVerifyCode(String mobile) {

        //Call<HttpResult<JSONObject>> call = loginService.sendMobileCode(mobile);
        //call.enqueue(new SimpleCallback<JSONObject>() {
        //    @Override
        //    public void onSuccess(JSONObject data) {
        //        ToastUtils.showShort("验证码发送成功 ：" + data);
        //    }
        //
        //    @Override
        //    public void onFail(String code, String errorMsg) {
        //        ToastUtils.showShort(errorMsg);
        //    }
        //});

        MobHelper.getInstance().getVerifyCode(mobile);
    }

    /**
     * 测试手机号注册
     */
    private void testMobileRegister(String mobile, String code) {

        if (TextUtils.isEmpty(mobile) || TextUtils.isEmpty(code)) {
            showShort("请输入手机号和验证码");
            return;
        }

        MobHelper.getInstance().submitVerifyCode(mobile, code);

        //Call<HttpResult<UserWrapper>> call = loginService.registerByPhone(mobile, code);
        //call.enqueue(new SimpleCallback<UserWrapper>() {
        //    @Override
        //    public void onSuccess(UserWrapper data) {
        //        ToastUtils.showShort(data.socialUser.id);
        //    }
        //
        //    @Override
        //    public void onFail(String code, String errorMsg) {
        //        ToastUtils.showShort(errorMsg);
        //    }
        //});
    }

    /**
     * 测试用户名密码注册
     */
    private void testNameRegister(String userName, String pwd) {

        if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(pwd)) {
            showShort("请输入用户名和密码");
            return;
        }

        Call<HttpResult<UserWrapper>> call = loginService.registerByName(userName, pwd);
        call.enqueue(new SimpleCallback<UserWrapper>() {
            @Override
            public void onSuccess(UserWrapper data) {
                showShort(data.socialUser.id);
            }

            @Override
            public void onFail(String code, String errorMsg) {
                showShort(errorMsg);
            }
        });
    }
}
