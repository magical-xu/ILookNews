package com.chaoneng.ilooknews.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.google.android.flexbox.FlexboxLayout;

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

  public static int getColor(Context context, int color) {
    return ResourcesCompat.getColor(context.getResources(), color, null);
  }

  public static int getTotal(Context context, int... dimenIds) {
    int totalPx = 0;
    for (int id : dimenIds)
      totalPx += get(context, id);
    return totalPx;
  }

  public static int get(Context context, int dimenId) {
    return context.getResources().getDimensionPixelSize(dimenId);
  }

  public static void resize(View view, View v, int width, int height) {
    if (view instanceof FlexboxLayout) {
      FlexboxLayout.LayoutParams params = new FlexboxLayout.LayoutParams(width, height);
      v.setLayoutParams(params);
    } else if (view instanceof LinearLayout) {
      LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, height);
      v.setLayoutParams(params);
    } else {
      RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, height);
      v.setLayoutParams(params);
    }
  }
}
