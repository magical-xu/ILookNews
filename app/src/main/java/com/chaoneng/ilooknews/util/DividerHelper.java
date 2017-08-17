package com.chaoneng.ilooknews.util;

import android.content.Context;
import com.chaoneng.ilooknews.R;
import com.chaoneng.ilooknews.widget.divider.DrawableItemDecoration;
import com.magicalxu.library.blankj.SizeUtils;

/**
 * Created by magical on 17/8/17.
 * Description : RecyclerView 分割线帮助类
 */

public class DividerHelper {

  public static DrawableItemDecoration newRvDividerRect(Context context) {
    return new DrawableItemDecoration(context, R.color.main_divider_color,
        CompatUtil.getDimension(context, R.dimen.divide_block_width), 0);
  }

  public static DrawableItemDecoration newRvDividerLine(Context context) {
    return new DrawableItemDecoration(context, R.color.main_divider_color,
        CompatUtil.getDimension(context, R.dimen.divide_line_width), 0);
  }

  public static DrawableItemDecoration newRvDividerLine(Context context, int marginDp) {
    return new DrawableItemDecoration(context, R.color.main_divider_color,
        CompatUtil.getDimension(context, R.dimen.divide_line_width), SizeUtils.dp2px(marginDp));
  }

  public static DrawableItemDecoration drawVerticalLine(Context context, int marginDp, boolean top,
      boolean bottom) {
    return new DrawableItemDecoration(context, true, R.color.main_divider_color,
        CompatUtil.getDimension(context, R.dimen.divide_line_width), marginDp, top, bottom);
  }
}
