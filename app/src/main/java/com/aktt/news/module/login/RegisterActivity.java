package com.aktt.news.module.login;

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
import com.aktt.news.R;
import com.aktt.news.api.LoginService;
import com.aktt.news.api.UserService;
import com.aktt.news.base.BaseActivity;
import com.aktt.news.data.UserWrapper;
import com.aktt.news.instance.AccountManager;
import com.aktt.news.library.boxing.BoxingHelper;
import com.aktt.news.library.mob.MobHelper;
import com.aktt.news.library.qiniu.QiNiuHelper;
import com.aktt.news.net.callback.SimpleCallback;
import com.aktt.news.net.client.NetRequest;
import com.aktt.news.net.data.HttpResult;
import com.aktt.news.util.PathHelper;
import com.aktt.news.util.SimpleNotifyListener;
import com.aktt.news.widget.edit.ClearEditText;
import com.aktt.news.widget.edit.PasswordEditText;
import com.aktt.news.widget.image.HeadImageView;
import com.magicalxu.library.blankj.ToastUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import timber.log.Timber;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

import static com.magicalxu.library.blankj.ToastUtils.showShort;

/**
 * Created by magical on 2017/8/29.
 * Description : 注册界面
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
                hideLoadingOnUiThread();
                if (data instanceof Throwable) {
                    Throwable throwable = (Throwable) data;
                    String msg = throwable.getMessage();
                    onUiThread(msg);
                } else {
                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                        //提交验证码成功
                        //onUiThread("验证成功，接下来走注册流程");
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

    @Override
    public ArrayList<Call> addRequestList() {
        return null;
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
        lastMobile = mobile;    //重要，记录真正验证的手机号
        showLoading();
        MobHelper.getInstance().getVerifyCode(mobile);
    }

    /**
     * 通过Mob Sdk 验证验证码
     */
    private void checkVerifyCode() {

        String mobile = idPage2Username.getText().toString();
        String pwd = idPage2Pwd.getText().toString();
        String code = idPage2Code.getText().toString();

        if (TextUtils.isEmpty(mobile) || TextUtils.isEmpty(code) || TextUtils.isEmpty(pwd)) {
            showShort(R.string.plz_input_mobile_and_code);
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
        MobHelper.getInstance().submitVerifyCode(lastMobile, code);
    }

    /**
     * 手机号注册
     */
    private void registerByMobile() {

        String pwd = idPage2Pwd.getText().toString().trim();    //拿最新的 密码可以不用管 只用最后一次的

        showLoadingOnUiThread();
        Call<HttpResult<UserWrapper>> call =
                loginService.registerByPhone(lastMobile, pwd, tempAvatarUrl);
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
