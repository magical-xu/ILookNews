package com.aktt.news.library.glide;

import android.graphics.Bitmap;
import android.widget.ImageView;
import com.bumptech.glide.request.RequestListener;
import com.aktt.news.R;

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

    public static void loadImage(String url, ImageView view, RequestListener<Bitmap> listener) {

        GlideApp.with(view.getContext())
                .asBitmap()
                .placeholder(R.drawable.ic_image_loading)
                .error(R.drawable.ic_empty_picture)
                .fallback(R.drawable.ic_empty_picture)
                .load(url)
                .listener(listener)
                .into(view);
    }
}
