package com.aktt.news.module.login;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.OnClick;
import com.aktt.news.BuildConfig;
import com.aktt.news.MainActivity;
import com.aktt.news.R;
import com.aktt.news.base.BaseActivity;
import com.aktt.news.instance.TabManager;
import com.aktt.news.util.NotifyListener;
import com.aktt.news.util.TimeCountdown;

/**
 * Created by magical on 17/8/20.
 * Description : 闪屏页
 */

public class SplashActivity extends BaseActivity {

    @BindView(R.id.tv_count_down) TextView mTimer;
    @BindView(R.id.rl_container_ad) RelativeLayout mAdRoot;
    @BindView(R.id.iv_logo) TextView mLogo;
    @BindView(R.id.container) RelativeLayout rootView;

    private TimeCountdown mTimeCountdown;

    @Override
    public int getLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    public void handleChildPage(Bundle savedInstanceState) {

        alphaAnimation();
        countDown();

        // for init tab as early as possible
        final TabManager tabManager = TabManager.getInstance();
        tabManager.getNewsChannel(this, new NotifyListener() {
            @Override
            public void onSuccess() {
                tabManager.setHasInit(true);
                //onTimeEnd();
            }

            @Override
            public void onFail() {
                tabManager.setHasInit(false);
                //onTimeEnd();
            }
        });
        tabManager.getVideoChannel(this, null);
    }

    @Override
    protected void beforeContentView() {
        super.beforeContentView();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private void alphaAnimation() {
        long durationMs = 200;
        if (BuildConfig.DEBUG) durationMs = 0; // speed up under debug

        Animation anim = new AlphaAnimation(0.1f, 1.0f);
        anim.setDuration(durationMs);
        rootView.startAnimation(anim);
    }

    private void countDown() {
        int durationMs = 1500;

        mTimeCountdown = new TimeCountdown(durationMs, 1000) {
            @Override
            public void countdownListener(int currentValue, boolean isEnd) {
                if (isEnd) {
                    onTimeEnd();
                } else {
                    mTimer.setText(String.format("跳过 %s", currentValue / 1000));
                }
            }
        };
    }

    private void onTimeEnd() {
        MainActivity.startAction(SplashActivity.this);
        finish();
    }

    @OnClick(R.id.tv_count_down)
    public void onClickJump(View view) {
        onTimeEnd();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mTimeCountdown) {
            mTimeCountdown.removeCallback();
        }
    }
}
