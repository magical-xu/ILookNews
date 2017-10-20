package com.aktt.news.util;

import android.os.Handler;

public abstract class TimeCountdown {

    final int mMaxValue;
    final int mUnitValue;

    private int mCurrentValue;

    final Handler mHandler = new Handler();
    final Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if (mCurrentValue - mUnitValue <= 0) { // 倒计时到1秒，而不是0秒
                countdownListener(mCurrentValue, true);
            } else {
                mCurrentValue -= mUnitValue;
                countdownListener(mCurrentValue, false);
                mHandler.postDelayed(this, mUnitValue);
            }
        }
    };

    public abstract void countdownListener(int currentValue, boolean isEnd);

    public TimeCountdown(final int maxValue, final int unitValue) {
        mMaxValue = maxValue;
        mUnitValue = unitValue;
        mCurrentValue = mMaxValue;

        countdownListener(mCurrentValue, false);
        mHandler.postDelayed(mRunnable, unitValue);
    }

    public void removeCallback() { // activity pause 时要调用，否则内存泄漏
        mHandler.removeCallbacks(mRunnable);
    }
}
