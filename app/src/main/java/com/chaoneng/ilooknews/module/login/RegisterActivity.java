package com.chaoneng.ilooknews.module.login;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.OnClick;
import com.chaoneng.ilooknews.AppConstant;
import com.chaoneng.ilooknews.R;
import com.chaoneng.ilooknews.api.LoginService;
import com.chaoneng.ilooknews.base.BaseActivity;
import com.chaoneng.ilooknews.data.UserWrapper;
import com.chaoneng.ilooknews.library.glide.ImageLoader;
import com.chaoneng.ilooknews.library.gsyvideoplayer.listener.SampleListener;
import com.chaoneng.ilooknews.net.callback.SimpleCallback;
import com.chaoneng.ilooknews.net.client.NetRequest;
import com.chaoneng.ilooknews.net.data.HttpResult;
import com.magicalxu.library.blankj.ToastUtils;
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder;
import com.shuyu.gsyvideoplayer.listener.LockClickListener;
import com.shuyu.gsyvideoplayer.utils.Debuger;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer;
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
  @BindView(R.id.id_video_player) StandardGSYVideoPlayer mVideoPlayer;

  private LoginService loginService;

  @Override
  public int getLayoutId() {
    return R.layout.activity_register;
  }

  @Override
  public void handleChildPage(Bundle savedInstanceState) {

    loginService = NetRequest.getInstance().create(LoginService.class);
    configVideo();
  }

  OrientationUtils orientationUtils;
  private boolean isPlay;
  private boolean isPause;

  private void configVideo() {

    mVideoPlayer.getTitleTextView().setVisibility(View.GONE);
    mVideoPlayer.getTitleTextView().setText("测试标题");
    mVideoPlayer.getBackButton().setVisibility(View.GONE);

    //增加封面
    ImageView imageView = new ImageView(this);
    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
    ImageLoader.loadImage(AppConstant.TEST_THUMB_URL, imageView);

    //外部辅助的旋转，帮助全屏
    orientationUtils = new OrientationUtils(this, mVideoPlayer);
    //初始化不打开外部的旋转
    orientationUtils.setEnable(false);

    GSYVideoOptionBuilder gsyVideoOption = new GSYVideoOptionBuilder();
    gsyVideoOption.setThumbImageView(imageView)
        .setIsTouchWiget(true)
        .setRotateViewAuto(false)
        .setLockLand(false)
        .setShowFullAnimation(false)
        .setNeedLockFull(true)
        .setSeekRatio(1)
        .setUrl(AppConstant.TEST_VIDEO_URL)
        .setCacheWithPlay(false)
        .setVideoTitle("测试视频")
        .setStandardVideoAllCallBack(new SampleListener() {
          @Override
          public void onPrepared(String url, Object... objects) {
            Debuger.printfError("***** onPrepared **** " + objects[0]);
            Debuger.printfError("***** onPrepared **** " + objects[1]);
            super.onPrepared(url, objects);
            //开始播放了才能旋转和全屏
            orientationUtils.setEnable(true);
            isPlay = true;
          }

          @Override
          public void onEnterFullscreen(String url, Object... objects) {
            super.onEnterFullscreen(url, objects);
            Debuger.printfError("***** onEnterFullscreen **** " + objects[0]);//title
            Debuger.printfError("***** onEnterFullscreen **** " + objects[1]);//当前全屏player
          }

          @Override
          public void onAutoComplete(String url, Object... objects) {
            super.onAutoComplete(url, objects);
          }

          @Override
          public void onClickStartError(String url, Object... objects) {
            super.onClickStartError(url, objects);
          }

          @Override
          public void onQuitFullscreen(String url, Object... objects) {
            super.onQuitFullscreen(url, objects);
            Debuger.printfError("***** onQuitFullscreen **** " + objects[0]);//title
            Debuger.printfError("***** onQuitFullscreen **** " + objects[1]);//当前非全屏player
            if (orientationUtils != null) {
              orientationUtils.backToProtVideo();
            }
          }
        })
        .setLockClickListener(new LockClickListener() {
          @Override
          public void onClick(View view, boolean lock) {
            if (orientationUtils != null) {
              //配合下方的onConfigurationChanged
              orientationUtils.setEnable(!lock);
            }
          }
        })
        .build(mVideoPlayer);

    mVideoPlayer.getFullscreenButton().setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        //直接横屏
        orientationUtils.resolveByClick();

        //第一个true是否需要隐藏actionbar，第二个true是否需要隐藏statusbar
        mVideoPlayer.startWindowFullscreen(RegisterActivity.this, true, true);
      }
    });
  }

  @Override
  public void onBackPressed() {
    if (orientationUtils != null) {
      orientationUtils.backToProtVideo();
    }

    if (StandardGSYVideoPlayer.backFromWindowFull(this)) {
      return;
    }
    super.onBackPressed();
  }

  @Override
  protected void onPause() {
    super.onPause();
    isPause = true;
  }

  @Override
  protected void onResume() {
    super.onResume();
    isPause = false;
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    GSYVideoPlayer.releaseAllVideos();
    //GSYPreViewManager.instance().releaseMediaPlayer();
    if (orientationUtils != null) orientationUtils.releaseListener();
  }

  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    //如果旋转了就全屏
    if (isPlay && !isPause) {
      if (newConfig.orientation == ActivityInfo.SCREEN_ORIENTATION_USER) {
        if (!mVideoPlayer.isIfCurrentIsFullscreen()) {
          mVideoPlayer.startWindowFullscreen(RegisterActivity.this, true, true);
        }
      } else {
        //新版本isIfCurrentIsFullscreen的标志位内部提前设置了，所以不会和手动点击冲突
        if (mVideoPlayer.isIfCurrentIsFullscreen()) {
          StandardGSYVideoPlayer.backFromWindowFull(this);
        }
        if (orientationUtils != null) {
          orientationUtils.setEnable(true);
        }
      }
    }
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
