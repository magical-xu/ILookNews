package com.chaoneng.ilooknews.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;

/**
 * Created by magical on 17/8/17.
 * Description :
 */

public class CompatUtil {

  public static Drawable getDrawable(Context context, int drawableId) {
    return ResourcesCompat.getDrawable(context.getResources(), drawableId, null);
  }

  public static int getDimension(Context context, int dimenId) {
    return context.getResources().getDimensionPixelSize(dimenId);
  }
}
