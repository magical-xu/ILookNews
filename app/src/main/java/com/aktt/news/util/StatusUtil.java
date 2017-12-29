package com.aktt.news.util;

import android.app.Activity;
import com.aktt.news.R;
import com.gyf.barlibrary.ImmersionBar;

/**
 * Created by magical on 17/12/28.
 * Description :
 */

public class StatusUtil {

    public static void toDarkMode(Activity context) {

        ImmersionBar.with(context).transparentStatusBar().statusBarDarkFont(false).init();
    }

    public static void toGreyMode(Activity context) {

        ImmersionBar.with(context).statusBarColor(R.color.tv_talk_content).init();
    }
}
