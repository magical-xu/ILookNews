package com.chaoneng.ilooknews.util;

import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.view.View;
import android.view.animation.BounceInterpolator;

/**
 * Created by magical on 2017/10/16.
 * Description :
 */

public class AnimHelper {

    public static void showAnim(View view) {

        if (view == null) {
            return;
        }
        float scaleLarge = 1.15f;
        float scaleHuge = 1.3f;
        int duration = 1200;

        //先变小后变大
        PropertyValuesHolder scaleXValuesHolder =
                PropertyValuesHolder.ofKeyframe(View.SCALE_X, Keyframe.ofFloat(0f, 1.0f),
                        Keyframe.ofFloat(0.25f, scaleLarge), Keyframe.ofFloat(0.5f, scaleHuge),
                        Keyframe.ofFloat(0.75f, scaleLarge), Keyframe.ofFloat(1.0f, 1.0f));
        PropertyValuesHolder scaleYValuesHolder =
                PropertyValuesHolder.ofKeyframe(View.SCALE_Y, Keyframe.ofFloat(0f, 1.0f),
                        Keyframe.ofFloat(0.25f, scaleLarge), Keyframe.ofFloat(0.5f, scaleHuge),
                        Keyframe.ofFloat(0.75f, scaleLarge), Keyframe.ofFloat(1.0f, 1.0f));

        ObjectAnimator objectAnimator =
                ObjectAnimator.ofPropertyValuesHolder(view, scaleXValuesHolder, scaleYValuesHolder);
        objectAnimator.setDuration(duration);
        objectAnimator.setInterpolator(new BounceInterpolator());
        objectAnimator.start();
    }
}
