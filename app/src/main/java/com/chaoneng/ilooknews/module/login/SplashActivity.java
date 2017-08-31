package com.chaoneng.ilooknews.module.login;

import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.BindView;
import com.chaoneng.ilooknews.BuildConfig;
import com.chaoneng.ilooknews.MainActivity;
import com.chaoneng.ilooknews.R;
import com.chaoneng.ilooknews.base.BaseActivity;
import com.chaoneng.ilooknews.instance.TabManager;
import com.chaoneng.ilooknews.util.TimeCountdown;

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
        TabManager tabManager = TabManager.getInstance();
        tabManager.getNewsChannel(null);
        tabManager.getVideoChannel(null);
    }

    private void alphaAnimation() {
        long durationMs = 200;
        if (BuildConfig.DEBUG) durationMs = 0; // speed up under debug

        Animation anim = new AlphaAnimation(0.1f, 1.0f);
        anim.setDuration(durationMs);
        rootView.startAnimation(anim);
    }

    private void countDown() {
        int durationMs = 3 * 1000;
        if (BuildConfig.DEBUG) durationMs = 0; // speed up under debug

        mTimeCountdown = new TimeCountdown(durationMs, 1000) {
            @Override
            public void countdownListener(int currentValue, boolean isEnd) {
                if (isEnd) {
                    MainActivity.startAction(SplashActivity.this);
                    finish();
                } else {
                    mTimer.setText(String.format("跳过 %s", currentValue / 1000));
                }
            }
        };
    }
}
