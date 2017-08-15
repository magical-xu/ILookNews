package com.chaoneng.ilooknews.library.glide;

import android.content.Context;
import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.AppGlideModule;
import java.io.InputStream;

/**
 * Created by magical on 17/8/15.
 * Description : Glide 的全局配置类
 * 编译后会自动生成 GlideApp 类用于图片加载 类似之前的 Glide
 */

@GlideModule public class ILookGlideModule extends AppGlideModule {

  @Override
  public boolean isManifestParsingEnabled() {
    //禁用 manifest 解析 主要针对 v3 升级到 v4 的用户
    return false;
  }

  @Override
  public void applyOptions(Context context, GlideBuilder builder) {
    super.applyOptions(context, builder);
    //do nothing 使用 glide 默认配置即可 如需自定义可在此处更改
  }

  @Override
  public void registerComponents(Context context, Glide glide, Registry registry) {
    super.registerComponents(context, glide, registry);
    //使用 okHttp 代替默认的 HttpUrlConnection
    registry.replace(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory());
  }
}
