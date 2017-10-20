package com.aktt.news.util;

import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import timber.log.Timber;

/**
 * Created by magical on 17/8/31.
 * Description :
 */

public class FragmentHelper {

  public static void switchFragment(FragmentActivity activity, Fragment from, Fragment to,
      @IdRes int contentId) {

    if (null == activity) {
      Timber.e("activity is null.");
      return;
    }

    FragmentManager manager = activity.getSupportFragmentManager();
    if (null == manager) {
      Timber.e("FragmentManager is null.");
      return;
    }

    FragmentTransaction transaction = manager.beginTransaction();
    if (null != from) {
      transaction.hide(from);
    }

    if (!to.isAdded()) {
      // no added
      transaction.add(contentId, to, to.getClass().getSimpleName());
    } else {
      transaction.show(to);
    }
    transaction.commit();
  }
}
