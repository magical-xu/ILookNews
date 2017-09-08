package com.chaoneng.ilooknews.util;

import android.support.annotation.NonNull;
import android.view.View;
import com.magicalxu.library.blankj.KeyboardUtils;

/**
 * Created by magical on 17/9/8.
 * Description :
 */

public class BottomHelper {

  public static boolean isKeyboardShow(@NonNull View realView) {
    return realView.getVisibility() == View.VISIBLE;
  }

  public static void recoverNormalBottom(View realView, View fakeView, View inputView) {

    fakeView.setVisibility(View.VISIBLE);
    realView.setVisibility(View.GONE);
    inputView.clearFocus();
    KeyboardUtils.hideSoftInput(inputView);
  }

  public static void showKeyboard(View realView, View fakeView, View inputView) {
    fakeView.setVisibility(View.GONE);
    realView.setVisibility(View.VISIBLE);
    KeyboardUtils.showSoftInput(inputView);
  }
}
