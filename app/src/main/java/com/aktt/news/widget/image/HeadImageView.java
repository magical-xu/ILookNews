package com.aktt.news.widget.image;

import android.content.Context;
import android.util.AttributeSet;
import com.aktt.news.R;
import com.aktt.news.library.glide.GlideApp;
import com.magicalxu.library.MR;

/**
 * 封装一层 通用头像View
 * 可统一对变化进行处理
 * Created by allen.yu on 2017/3/1.
 */

public class HeadImageView extends CircularImage {

  public HeadImageView(Context context) {
    this(context, null);
  }

  public HeadImageView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public HeadImageView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    setImageResource(MR.getIdByDrawableName(context, "wpk_default_head_bg"));
  }

  /**
   * 直接调用mXCRoundRectImageView的setHeadImageUrl方法
   */
  public void setHeadImage(String url) {
    GlideApp.with(this)
        .load(url)
        .error(R.drawable.default_head)
        .fallback(R.drawable.default_head)
        .placeholder(R.drawable.default_head)
        .into(this);
  }
}
