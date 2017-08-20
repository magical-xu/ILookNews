package com.chaoneng.ilooknews.library.boxing;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ImageView;
import com.bilibili.boxing.loader.IBoxingCallback;
import com.bilibili.boxing.loader.IBoxingMediaLoader;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.chaoneng.ilooknews.R;
import com.chaoneng.ilooknews.library.glide.GlideApp;
import com.chaoneng.ilooknews.library.glide.GlideRequest;

/**
 * use https://github.com/bumptech/glide as media loader.
 * can <b>not</b> be used in Production Environment.
 *
 * @author ChenSL
 */
public class BoxingGlideLoader implements IBoxingMediaLoader {

  @Override
  public void displayThumbnail(@NonNull ImageView img, @NonNull String absPath, int width,
      int height) {
    String path = "file://" + absPath;
    try {
      // https://github.com/bumptech/glide/issues/1531
      GlideApp.with(img.getContext())
          .load(path)
          .placeholder(R.drawable.ic_boxing_default_image)
          .centerCrop()
          .override(width, height)
          .into(img);
    } catch (IllegalArgumentException ignore) {
    }
  }

  @Override
  public void displayRaw(@NonNull final ImageView img, @NonNull String absPath, int width,
      int height, final IBoxingCallback callback) {
    String path = "file://" + absPath;
    GlideRequest<Bitmap> request = GlideApp.with(img.getContext()).asBitmap().load(path);
    if (width > 0 && height > 0) {
      request.override(width, height);
    }

    request.listener(new RequestListener<Bitmap>() {
      @Override
      public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target,
          boolean isFirstResource) {
        if (callback != null) {
          callback.onFail(e);
          return true;
        }
        return false;
      }

      @Override
      public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target,
          DataSource dataSource, boolean isFirstResource) {
        if (resource != null && callback != null) {
          img.setImageBitmap(resource);
          callback.onSuccess();
          return true;
        }
        return false;
      }
    }).into(img);
  }
}