package com.chaoneng.ilooknews;

import android.app.Application;
import android.content.Context;
import com.chaoneng.ilooknews.api.Constant;
import com.chaoneng.ilooknews.net.client.NetRequest;
import com.facebook.stetho.Stetho;
import com.magicalxu.library.Utils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshFooterCreater;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreater;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import timber.log.Timber;

/**
 * Created by magical on 17/8/14.
 * Description : ILookNewsApplication
 */

public class ILookApplication extends Application {

  private static Context INSTANCE;

  //static 代码段可以防止内存泄露
  static {
    //设置全局的Header构建器
    SmartRefreshLayout.setDefaultRefreshHeaderCreater(new DefaultRefreshHeaderCreater() {
      @Override
      public RefreshHeader createRefreshHeader(Context context, RefreshLayout layout) {
        layout.setPrimaryColorsId(R.color.colorPrimary, android.R.color.white);//全局设置主题颜色
        return new ClassicsHeader(context).setSpinnerStyle(
            SpinnerStyle.Translate);//指定为经典Header，默认是 贝塞尔雷达Header
      }
    });
    //设置全局的Footer构建器
    SmartRefreshLayout.setDefaultRefreshFooterCreater(new DefaultRefreshFooterCreater() {
      @Override
      public RefreshFooter createRefreshFooter(Context context, RefreshLayout layout) {
        //指定为经典Footer，默认是 BallPulseFooter
        ClassicsFooter footer = new ClassicsFooter(context).setSpinnerStyle(SpinnerStyle.Translate);
        footer.setBackgroundResource(android.R.color.white);
        footer.setSpinnerStyle(SpinnerStyle.Scale);
        return footer;
      }
    });
  }

  @Override
  public void onCreate() {
    super.onCreate();

    INSTANCE = getApplicationContext();

    Utils.init(this);
    initLib();
  }

  private void initLib() {

    // init timber
    Timber.plant(new Timber.DebugTree());

    // request debug
    Stetho.initializeWithDefaults(this);

    // init retrofit client
    NetRequest.getInstance().init(this, Constant.BASE_URL);
  }

  public static Context getAppContext() {
    return INSTANCE;
  }
}
