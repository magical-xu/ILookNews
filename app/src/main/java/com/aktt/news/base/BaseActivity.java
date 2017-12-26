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

public abstract class BaseActivity extends AppCompatActivity {

    protected String TAG = getClass().getSimpleName();

    protected ViewGroup mDecorView;
    protected FrameLayout mFrameContent;
    protected ILookTitleBar mTitleBar;

    public static final String PAGE_ANIMATION_FROM_BOTTOM = "page_animation_from_bottom";
    protected boolean pageAnimationFromBottom = false;  //底部弹出动画标记

    private DialogManager mDialogManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        beforeContentView();
        checkAnimation();
        checkTitleBar();

        ButterKnife.bind(this);
        NetRequest.getInstance().addRequestList(TAG, addRequestList());
        StatusBarCompat.setStatusBarColor(this, ContextCompat.getColor(this, R.color.white));
        handleChildPage(savedInstanceState);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

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
