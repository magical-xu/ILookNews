package com.aktt.news.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import butterknife.ButterKnife;
import cn.bingoogolapple.swipebacklayout.BGASwipeBackHelper;
import com.aktt.news.AppConstant;
import com.aktt.news.R;
import com.aktt.news.net.client.NetRequest;
import com.aktt.news.widget.DialogManager;
import com.aktt.news.widget.ilook.ILookTitleBar;
import com.githang.statusbar.StatusBarCompat;
import com.magicalxu.library.blankj.SPUtils;
import com.magicalxu.library.blankj.ToastUtils;
import java.util.ArrayList;
import retrofit2.Call;

/**
 * Created by magical on 17/8/14.
 * Description :
 */

public abstract class BaseActivity extends AppCompatActivity implements BGASwipeBackHelper.Delegate{

    protected String TAG = getClass().getSimpleName();

    protected ViewGroup mDecorView;
    protected FrameLayout mFrameContent;
    protected ILookTitleBar mTitleBar;

    public static final String PAGE_ANIMATION_FROM_BOTTOM = "page_animation_from_bottom";
    protected boolean pageAnimationFromBottom = false;  //底部弹出动画标记

    private DialogManager mDialogManager;
    protected BGASwipeBackHelper mSwipeBackHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        initSwipeBackFinish();
        super.onCreate(savedInstanceState);

        beforeContentView();
        checkAnimation();
        checkTitleBar();

        ButterKnife.bind(this);
        NetRequest.getInstance().addRequestList(TAG, addRequestList());
        StatusBarCompat.setStatusBarColor(this,
                ContextCompat.getColor(this, R.color.tv_talk_content));
        //getSwipeBackLayout().setEnableGesture(false);
        handleChildPage(savedInstanceState);
    }

    /**
     * 初始化滑动返回。在 super.onCreate(savedInstanceState) 之前调用该方法
     */
    private void initSwipeBackFinish() {
        mSwipeBackHelper = new BGASwipeBackHelper(this, this);

        // 「必须在 Application 的 onCreate 方法中执行 BGASwipeBackHelper.init 来初始化滑动返回」
        // 下面几项可以不配置，这里只是为了讲述接口用法。

        // 设置滑动返回是否可用。默认值为 true
        mSwipeBackHelper.setSwipeBackEnable(false);
        // 设置是否仅仅跟踪左侧边缘的滑动返回。默认值为 true
        mSwipeBackHelper.setIsOnlyTrackingLeftEdge(false);
        // 设置是否是微信滑动返回样式。默认值为 true
        mSwipeBackHelper.setIsWeChatStyle(true);
        // 设置阴影资源 id。默认值为 R.drawable.bga_sbl_shadow
        mSwipeBackHelper.setShadowResId(R.drawable.bga_sbl_shadow);
        // 设置是否显示滑动返回的阴影效果。默认值为 true
        mSwipeBackHelper.setIsNeedShowShadow(true);
        // 设置阴影区域的透明度是否根据滑动的距离渐变。默认值为 true
        mSwipeBackHelper.setIsShadowAlphaGradient(true);
        // 设置触发释放后自动滑动返回的阈值，默认值为 0.3f
        mSwipeBackHelper.setSwipeBackThreshold(0.3f);
        // 设置底部导航条是否悬浮在内容上，默认值为 false
        mSwipeBackHelper.setIsNavigationBarOverlap(false);
    }

    /**
     * 是否支持滑动返回。这里在父类中默认返回 true 来支持滑动返回，如果某个界面不想支持滑动返回则重写该方法返回 false 即可
     *
     * @return
     */
    @Override
    public boolean isSupportSwipeBack() {
        return true;
    }

    /**
     * 正在滑动返回
     *
     * @param slideOffset 从 0 到 1
     */
    @Override
    public void onSwipeBackLayoutSlide(float slideOffset) {
    }

    /**
     * 没达到滑动返回的阈值，取消滑动返回动作，回到默认状态
     */
    @Override
    public void onSwipeBackLayoutCancel() {
    }

    /**
     * 滑动返回执行完毕，销毁当前 Activity
     */
    @Override
    public void onSwipeBackLayoutExecuted() {
        mSwipeBackHelper.swipeBackward();
    }

    public abstract int getLayoutId();

    public abstract void handleChildPage(Bundle savedInstanceState);

    /**
     * 钩子函数 是否显示过场动画 默认开启
     */
    protected boolean showPageAnimation() {
        return true;
    }

    /**
     * 钩子函数 是否添加TitleBar 默认不添加
     */
    protected boolean addTitleBar() {
        return false;
    }

    @Override
    public void finish() {
        super.finish();
        checkExitAnimation();
    }

    protected void beforeContentView() {
        // do nothing
        int size = SPUtils.getInstance().getInt(AppConstant.NEWS_TEXT_SIZE, 1);
        if (size == 0) {
            setTheme(R.style.text_style_small);
        } else if (size == 1) {
            setTheme(R.style.text_style_normal);
        } else {
            setTheme(R.style.text_style_big);
        }
    }

    /**
     * 检查 退出动画
     */
    private void checkExitAnimation() {
        if (showPageAnimation()) {
            if (!pageAnimationFromBottom) {
                overridePendingTransition(R.anim.page_enter_scale, R.anim.page_exit_right);
            } else {
                overridePendingTransition(R.anim.page_enter_scale, R.anim.page_exit_bottom);
            }
        }
    }

    /**
     * 检查 过场动画
     */
    private void checkAnimation() {
        if (showPageAnimation()) {
            pageAnimationFromBottom =
                    getIntent().getBooleanExtra(PAGE_ANIMATION_FROM_BOTTOM, false);
            if (!pageAnimationFromBottom) {
                overridePendingTransition(R.anim.page_enter_right, R.anim.page_exit_scale);
            } else {
                overridePendingTransition(R.anim.page_enter_bottom, R.anim.page_exit_scale);
            }
        }
    }

    /**
     * 检查 是否需要创建TitleBar
     */
    private void checkTitleBar() {

        mDecorView = (ViewGroup) getWindow().getDecorView();
        LayoutInflater mInflater = LayoutInflater.from(this);
        if (addTitleBar()) {
            LinearLayout titleBarLayout =
                    (LinearLayout) mInflater.inflate(R.layout.layout_title_base_normal, mDecorView,
                            false);
            mTitleBar = titleBarLayout.findViewById(R.id.id_title_bar);
            mTitleBar.attach(this);
            mFrameContent = titleBarLayout.findViewById(R.id.id_content_frame);
            View contentView = mInflater.inflate(getLayoutId(), mDecorView, false);
            mFrameContent.addView(contentView);
            setContentView(titleBarLayout);
        } else {
            if (getLayoutId() != 0) {
                View view = mInflater.inflate(getLayoutId(), null);
                setContentView(view);
            }
        }
    }

    //@Override
    //public void onBackPressed() {
    //    // 正在滑动返回的时候取消返回按钮事件
    //    if (mSwipeBackHelper.isSliding()) {
    //        return;
    //    }
    //    mSwipeBackHelper.backward();
    //}

    private void ensureDialogManager() {
        if (mDialogManager == null) {
            mDialogManager = new DialogManager(this);
        }
    }

    public void showLoading(String msg) {
        ensureDialogManager();
        mDialogManager.showDialog(msg);
    }

    public void showLoading() {
        ensureDialogManager();
        mDialogManager.showDialog("正在加载");
    }

    public boolean isLoading() {
        ensureDialogManager();
        return mDialogManager.isShowing();
    }

    public void hideLoading() {
        ensureDialogManager();
        mDialogManager.dismissDialog();
    }

    public void showLoadingOnUiThread() {
        ensureDialogManager();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mDialogManager.showDialog("正在加载");
            }
        });
    }

    public void hideLoadingOnUiThread() {
        ensureDialogManager();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mDialogManager.dismissDialog();
            }
        });
    }

    @Override
    protected void onDestroy() {

        if (null != mDialogManager) {
            mDialogManager.dismissDialog();
            mDialogManager = null;
        }

        //Log.d("magical"," onDestroy : " + TAG);
        NetRequest.getInstance().cancelByTag(TAG);

        super.onDestroy();
    }

    public abstract ArrayList<Call> addRequestList();

    /*********************** 以下为方便调用使用 ***********************/
    protected void onSimpleError(String msg) {
        hideLoading();
        ToastUtils.showShort(msg);
    }

    protected void onRemoveCall(Call call) {
        NetRequest.getInstance().removeRequest(TAG, call);
    }

    protected void onAddCall(Call call) {
        NetRequest.getInstance().addRequest(TAG, call);
    }
}
