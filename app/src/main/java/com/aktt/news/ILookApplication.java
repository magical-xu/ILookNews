package com.aktt.news;

import android.content.Context;
import android.support.multidex.MultiDex;
import cn.bingoogolapple.swipebacklayout.BGASwipeBackHelper;
import com.aktt.news.api.Constant;
import com.aktt.news.library.boxing.BoxingHelper;
import com.aktt.news.library.shareloginlib.ShareLoginHelper;
import com.aktt.news.net.client.NetRequest;
import com.facebook.stetho.Stetho;
import com.magicalxu.library.Utils;
import com.mob.MobApplication;
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

public class ILookApplication extends MobApplication {

    private static Context INSTANCE;

    //static 代码段可以防止内存泄露
    static {
        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreater(new DefaultRefreshHeaderCreater() {
            @Override
            public RefreshHeader createRefreshHeader(Context context, RefreshLayout layout) {
                layout.setPrimaryColorsId(R.color.light_gray, android.R.color.white);//全局设置主题颜色
                return new ClassicsHeader(context).setSpinnerStyle(
                        SpinnerStyle.Translate);//指定为经典Header，默认是 贝塞尔雷达Header
            }
        });
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreater(new DefaultRefreshFooterCreater() {
            @Override
            public RefreshFooter createRefreshFooter(Context context, RefreshLayout layout) {
                //指定为经典Footer，默认是 BallPulseFooter
                ClassicsFooter footer =
                        new ClassicsFooter(context).setSpinnerStyle(SpinnerStyle.Translate);
                footer.setBackgroundResource(android.R.color.white);
                footer.setSpinnerStyle(SpinnerStyle.Scale);
                return footer;
            }
        });
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
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

        // init boxing
        BoxingHelper.init();

        // init retrofit client
        NetRequest.getInstance().init(this, Constant.ILOOK_BASE_URL);

        // init share and login lib
        ShareLoginHelper.init(this);

        /**
         * 必须在 Application 的 onCreate 方法中执行 BGASwipeBackHelper.init 来初始化滑动返回
         * 第一个参数：应用程序上下文
         * 第二个参数：如果发现滑动返回后立即触摸界面时应用崩溃，请把该界面里比较特殊的 View 的 class 添加到该集合中，目前在库中已经添加了 WebView 和 SurfaceView
         */
        BGASwipeBackHelper.init(this, null);
    }

    public static Context getAppContext() {
        return INSTANCE;
    }

    public static String getLocalString(int resId) {
        return getAppContext().getString(resId);
    }
}
