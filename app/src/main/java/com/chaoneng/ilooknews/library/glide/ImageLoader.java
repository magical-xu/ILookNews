package com.chaoneng.ilooknews.library.glide;

import android.widget.ImageView;
import com.chaoneng.ilooknews.R;

/**
 * Created by magical on 17/8/15.
 * Description : 封装统一 Glide 加载图片
 */

public class ImageLoader {

  public static void loadImage(String url, ImageView view) {
    GlideApp.with(view.getContext())
        .load(url)
        .placeholder(R.drawable.ic_image_loading)
        .error(R.drawable.ic_empty_picture)
        .fallback(R.drawable.ic_empty_picture)
        .into(view);
  }
}
