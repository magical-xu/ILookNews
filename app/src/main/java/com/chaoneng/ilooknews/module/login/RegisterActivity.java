package com.chaoneng.ilooknews.module.login;

import android.content.Intent;
import android.os.Bundle;
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
import com.bilibili.boxing.model.entity.BaseMedia;
import com.chaoneng.ilooknews.R;
import com.chaoneng.ilooknews.api.LoginService;
import com.chaoneng.ilooknews.api.UserService;
import com.chaoneng.ilooknews.base.BaseActivity;
import com.chaoneng.ilooknews.data.UserWrapper;
import com.chaoneng.ilooknews.instance.AccountManager;
import com.chaoneng.ilooknews.library.boxing.BoxingHelper;
import com.chaoneng.ilooknews.library.mob.MobHelper;
import com.chaoneng.ilooknews.library.qiniu.QiNiuHelper;
import com.chaoneng.ilooknews.net.callback.SimpleCallback;
import com.chaoneng.ilooknews.net.client.NetRequest;
import com.chaoneng.ilooknews.net.data.HttpResult;
import com.chaoneng.ilooknews.util.PathHelper;
import com.chaoneng.ilooknews.util.SimpleNotifyListener;
import com.chaoneng.ilooknews.widget.edit.ClearEditText;
import com.chaoneng.ilooknews.widget.edit.PasswordEditText;
import com.chaoneng.ilooknews.widget.image.HeadImageView;
import com.magicalxu.library.blankj.ToastUtils;
import java.io.File;
import java.util.List;
import retrofit2.Call;
import timber.log.Timber;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

import static com.magicalxu.library.blankj.ToastUtils.showShort;

/**
 * Created by magical on 2017/8/29.
 * Description : 测试注册
 */

public class RegisterActivity extends BaseActivity {

    @BindView(R.id.iv_finish) ImageView ivFinish;
    @BindView(R.id.id_login_type) TextView idLoginType;
    @BindView(R.id.id_page1_username) ClearEditText idPage1Username;
    @BindView(R.id.id_page1_pwd) PasswordEditText idPage1Pwd;
    @BindView(R.id.id_page2_username) ClearEditText idPage2Username;
    @BindView(R.id.tv_send_msg) TextView tvSendMsg;
    @BindView(R.id.id_page2_pwd) PasswordEditText idPage2Pwd;
    @BindView(R.id.id_page2_verify_code) PasswordEditText idPage2Code;
    @BindView(R.id.id_toggle) ViewFlipper idToggle;
    @BindView(R.id.tv_login) TextView tvLogin;
    @BindView(R.id.tv_change_login_type) TextView tvChangeLoginType;
    @BindView(R.id.tv_back_to_login) TextView tvBackToLogin;
    @BindView(R.id.id_avatar) HeadImageView mHeadIv;

    private LoginService loginService;
    private UserService userService;
    private EventHandler eventHandler;
    private long lastTimeMills;
    private boolean secondPageShow;
    private String lastMobile;      //防止在验证 验证码的时候 用户重新操作了 输入框 导致验证失效
    private String lastPwd;         //防止在验证 验证码的时候 用户重新操作了 输入框 导致验证失效
    private String tempAvatarUrl;

    @Override
    public int getLayoutId() {
        return R.layout.activity_register;
    }

    @Override
    public void handleChildPage(Bundle savedInstanceState) {

        loginService = NetRequest.getInstance().create(LoginService.class);
        userService = NetRequest.getInstance().create(UserService.class);

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
                        registerByMobile();
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

        mHeadIv.setHeadImage("");
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

    @OnClick({
            R.id.tv_login, R.id.tv_send_msg, R.id.iv_finish, R.id.tv_back_to_login,
            R.id.tv_change_login_type, R.id.id_avatar
    })
    public void onViewClick(View view) {

        switch (view.getId()) {
            case R.id.tv_login:

                if (secondPageShow) {
                    // mobile register page
                    checkVerifyCode();
                } else {
                    // username pwd register page
                    registerByName();
                }
                break;
            case R.id.tv_send_msg:
                sendVerifyCode();
                break;
            case R.id.iv_finish:
            case R.id.tv_back_to_login:
                finish();
                break;
            case R.id.tv_change_login_type:
                changeRegisterType();
                break;
            case R.id.id_avatar:
                onShowImageAlbum();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        List<BaseMedia> baseMedias =
                BoxingHelper.onActivityResult(this, requestCode, resultCode, data);
        if (null != baseMedias && baseMedias.size() > 0) {

            BaseMedia baseMedia = baseMedias.get(0);
            if (null != baseMedia) {
                Timber.d("select image success , begin to compress it ");
                String localPath = baseMedia.getPath();
                compressImage(localPath);
            }
        }
    }

    /**
     * 开启压缩图片 为上传做准备
     *
     * @param localPath 本地图片地址
     */
    private void compressImage(String localPath) {

        if (TextUtils.isEmpty(localPath)) {
            return;
        }

        showLoading();
        Luban.with(this)
                .load(localPath)
                .ignoreBy(100)
                .setTargetDir(PathHelper.getCachePath())
                .setCompressListener(new OnCompressListener() {
                    @Override
                    public void onStart() {
                        Timber.d("start to compress image");
                    }

                    @Override
                    public void onSuccess(File file) {
                        if (null == file || !file.exists()) {
                            return;
                        }
                        getUploadToken(file);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e("failed to compress image");
                        hideLoading();
                    }
                })
                .launch();
    }

    /**
     * 获取上传token
     */
    private void getUploadToken(final File file) {

        QiNiuHelper.getInstance().getUpToken(new SimpleNotifyListener() {
            @Override
            public void onSuccess(String msg) {
                uploadImage(file, msg);
            }

            @Override
            public void onFailed(String msg) {
                onSimpleError(msg);
            }
        });
    }

    /**
     * 传图到七牛
     */
    private void uploadImage(File file, String token) {

        QiNiuHelper.getInstance().upload(file.getAbsolutePath(), token, new SimpleNotifyListener() {
            @Override
            public void onSuccess(String msg) {
                hideLoading();
                tempAvatarUrl = msg;
                mHeadIv.setHeadImage(tempAvatarUrl);
            }

            @Override
            public void onFailed(final String msg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        onSimpleError(msg);
                    }
                });
            }
        });
    }

    /**
     * 展示 Boxing 相册选图
     */
    private void onShowImageAlbum() {

        BoxingHelper.startSingle(this);
    }

    /**
     * 发送验证码
     */
    private void sendVerifyCode() {

        if (null == idPage2Username) {
            return;
        }

        String mobile = idPage2Username.getText().toString();
        if (TextUtils.isEmpty(mobile)) {
            ToastUtils.showShort(R.string.plz_input_mobile_first);
            return;
        }

        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis - lastTimeMills < 60 * 1000) {
            ToastUtils.showShort(R.string.cannot_send_verify_code_in_60_seconds);
            return;
        }

        lastTimeMills = currentTimeMillis;
        MobHelper.getInstance().getVerifyCode(mobile);
    }

    /**
     * 通过Mob Sdk 验证验证码
     */
    private void checkVerifyCode() {

        lastMobile = idPage2Username.getText().toString();
        lastPwd = idPage2Pwd.getText().toString();
        String code = idPage2Code.getText().toString();

        if (TextUtils.isEmpty(lastMobile) || TextUtils.isEmpty(code) || TextUtils.isEmpty(
                lastPwd)) {
            showShort(R.string.plz_input_mobile_and_code);
            return;
        }

        MobHelper.getInstance().submitVerifyCode(lastMobile, code);
    }

    /**
     * 手机号注册
     */
    private void registerByMobile() {

        // 再次验证一下
        if (TextUtils.isEmpty(lastMobile) || TextUtils.isEmpty(lastPwd)) {
            return;
        }

        showLoading();
        Call<HttpResult<UserWrapper>> call =
                loginService.registerByPhone(lastMobile, lastPwd, tempAvatarUrl);
        call.enqueue(new SimpleCallback<UserWrapper>() {
            @Override
            public void onSuccess(UserWrapper data) {
                onRegisterSuccess(data);
            }

            @Override
            public void onFail(String code, String errorMsg) {
                onSimpleError(errorMsg);
            }
        });
    }

    /**
     * 用户名密码注册
     */
    private void registerByName() {

        String userName = idPage1Username.getText().toString().trim();
        String pwd = idPage1Pwd.getText().toString().trim();

        if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(pwd)) {
            showShort(R.string.plz_input_username_and_pwd);
            return;
        }

        showLoading();
        Call<HttpResult<UserWrapper>> call =
                loginService.registerByName(userName, pwd, tempAvatarUrl);
        call.enqueue(new SimpleCallback<UserWrapper>() {
            @Override
            public void onSuccess(UserWrapper data) {
                onRegisterSuccess(data);
            }

            @Override
            public void onFail(String code, String errorMsg) {
                onSimpleError(errorMsg);
            }
        });
    }

    private void onRegisterSuccess(UserWrapper data) {
        hideLoading();
        if (null != data) {
            AccountManager.getInstance().saveUser(data.socialUser);
            setResult(RESULT_OK);
            finish();
        }
    }

    /**
     * 更改注册类型
     */
    private void changeRegisterType() {

        secondPageShow = !secondPageShow;
        idPage2Code.setVisibility(secondPageShow ? View.VISIBLE : View.GONE);

        idToggle.setDisplayedChild(secondPageShow ? 1 : 0);
        tvChangeLoginType.setText(secondPageShow ? "账号密码注册" : "手机号注册");
    }
}
