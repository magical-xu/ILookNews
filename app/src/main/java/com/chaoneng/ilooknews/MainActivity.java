package com.chaoneng.ilooknews;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.widget.FrameLayout;
import butterknife.BindView;
import com.chaoneng.ilooknews.base.BaseActivity;
import com.chaoneng.ilooknews.data.TabEntity;
import com.chaoneng.ilooknews.instance.AccountManager;
import com.chaoneng.ilooknews.instance.TabManager;
import com.chaoneng.ilooknews.module.focus.FocusMainFragment;
import com.chaoneng.ilooknews.module.home.HomeMainFragment;
import com.chaoneng.ilooknews.module.user.UserMainFragment;
import com.chaoneng.ilooknews.module.video.VideoMainFragment;
import com.chaoneng.ilooknews.util.FragmentHelper;
import com.chaoneng.ilooknews.util.IntentHelper;
import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer;
import java.util.ArrayList;

public class MainActivity extends BaseActivity {

    private static final int TAB_HOME = 0;
    private static final int TAB_VIDEO = 1;
    private static final int TAB_FOCUS = 2;
    private static final int TAB_USER = 3;

    @BindView(R.id.id_main_container) FrameLayout mContainer;
    @BindView(R.id.id_main_tab_layout) CommonTabLayout mTabLayout;

    private String[] mTitles = {
            ILookApplication.getAppContext().getString(R.string.main_tab_home),
            ILookApplication.getAppContext().getString(R.string.main_tab_video),
            ILookApplication.getAppContext().getString(R.string.main_tab_focus),
            ILookApplication.getAppContext().getString(R.string.main_tab_user)
    };

    private int[] mIconUnSelectIds = {
            R.drawable.ic_home_normal, R.drawable.ic_video_normal, R.drawable.ic_care_normal,
            R.drawable.ic_profile_normal
    };

    private int[] mIconSelectIds = {
            R.drawable.ic_home_selected, R.drawable.ic_video_selected, R.drawable.ic_care_selected,
            R.drawable.ic_profile_selected
    };

    private ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();

    private HomeMainFragment homeMainFragment;
    private VideoMainFragment videoMainFragment;
    private FocusMainFragment careMainFragment;
    private UserMainFragment userMainFragment;
    private Fragment mCurPage;
    private int mLastTabPos;

    public static void startAction(Activity activity) {
        Intent intent = new Intent(activity, MainActivity.class);
        activity.startActivity(intent);
        activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void handleChildPage(Bundle savedInstanceState) {

        initTab();
        initFragment();
    }

    /**
     * 初始化tab
     */
    private void initTab() {
        for (int i = 0; i < mTitles.length; i++) {
            mTabEntities.add(new TabEntity(mTitles[i], mIconSelectIds[i], mIconUnSelectIds[i]));
        }
        mTabLayout.setTabData(mTabEntities);
        //点击监听
        mTabLayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                if (position < TAB_FOCUS || AccountManager.getInstance().hasLogin()) {
                    mLastTabPos = position;
                    loadPage(position);
                } else {
                    mTabLayout.setCurrentTab(mLastTabPos);
                    IntentHelper.openLoginPage(MainActivity.this);
                }
            }

            @Override
            public void onTabReselect(int position) {
            }
        });
    }

    /**
     * 初始化碎片
     */
    private void initFragment() {

        loadPage(TAB_HOME);
        mTabLayout.setCurrentTab(TAB_HOME);
    }

    private void loadPage(int index) {

        Fragment page = null;
        switch (index) {
            case TAB_HOME:
                if (null == homeMainFragment) {
                    homeMainFragment = new HomeMainFragment();
                }
                page = homeMainFragment;
                break;

            case TAB_VIDEO:
                if (null == videoMainFragment) {
                    videoMainFragment = new VideoMainFragment();
                }
                page = videoMainFragment;
                break;
            case TAB_FOCUS:
                if (null == careMainFragment) {
                    careMainFragment = new FocusMainFragment();
                }
                page = careMainFragment;
                break;
            case TAB_USER:
                if (null == userMainFragment) {
                    userMainFragment = new UserMainFragment();
                }
                page = userMainFragment;
                break;
            default:
                break;
        }

        if (null != page) {

            GSYVideoPlayer.releaseAllVideos();
            FragmentHelper.switchFragment(this, mCurPage, page, R.id.id_main_container);
            mCurPage = page;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && null != mCurPage) {
            if (mCurPage instanceof VideoMainFragment) {
                if (((VideoMainFragment) mCurPage).onBackPressed()) {
                    return true;
                }
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TabManager.getInstance().setHasInit(false);
    }
}
