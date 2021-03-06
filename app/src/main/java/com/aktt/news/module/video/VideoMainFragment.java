package com.aktt.news.module.video;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import butterknife.BindView;
import butterknife.OnClick;
import com.aktt.news.AppConstant;
import com.aktt.news.R;
import com.aktt.news.base.BaseFragment;
import com.aktt.news.data.BaseUser;
import com.aktt.news.data.Channel;
import com.aktt.news.instance.AccountManager;
import com.aktt.news.instance.TabManager;
import com.aktt.news.module.video.fragment.VideoListFragment;
import com.aktt.news.util.IntentHelper;
import com.aktt.news.util.LocalBroadcastUtil;
import com.aktt.news.util.NotifyListener;
import com.aktt.news.util.StatusUtil;
import com.aktt.news.util.StringHelper;
import com.aktt.news.widget.adapter.BaseFragmentStateAdapter;
import com.aktt.news.widget.adapter.OnPageChangeListener;
import com.aktt.news.widget.image.HeadImageView;
import com.flyco.tablayout.SlidingTabLayout;
import com.magicalxu.library.blankj.SPUtils;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.scwang.smartrefresh.layout.util.DensityUtil;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by magical on 17/8/15.
 * Description : 主页 - 视频
 */

public class VideoMainFragment extends BaseFragment {

    @BindView(R.id.sliding_tabs) SlidingTabLayout mTabView;
    @BindView(R.id.btn_search) ImageView mSearchView;
    @BindView(R.id.view_pager) ViewPager mViewPager;
    @BindView(R.id.iv_circle_left) HeadImageView mHead;
    @BindView(R.id.id_video_main) ViewGroup mTitleRoot;

    private BaseFragmentStateAdapter mPagerAdapter;
    private List<Fragment> videoFragmentList = new ArrayList<>();

    private IntentFilter filter;

    @Override
    protected void doInit() {
        super.doInit();

        StatusUtil.toGreyMode(getActivity());

        //重置头部高度
        int height = QMUIStatusBarHelper.getStatusbarHeight(getActivity());
        ViewGroup.LayoutParams layoutParams = mTitleRoot.getLayoutParams();
        layoutParams.height = DensityUtil.dp2px(44) + height;
        mTitleRoot.setLayoutParams(layoutParams);

        checkTitle();

        initFilter();
        if (null != receiver && null != filter) {
            LocalBroadcastUtil.register(receiver, filter);
        }

        TabManager tabManager = TabManager.getInstance();
        if (tabManager.hasVideoInit()) {
            setUp();
        } else {
            tabManager.getVideoChannel(getActivity(), new NotifyListener() {
                @Override
                public void onSuccess() {
                    setUp();
                }

                @Override
                public void onFail() {
                }
            });
        }
    }

    private void setUserIcon() {

        String avatar;

        //先从 sp 里 拿头像
        avatar = SPUtils.getInstance().getString(AppConstant.USER_ICON);

        BaseUser user = AccountManager.getInstance().getUser();
        if (null != user) {
            //登录过就拿一下 最新的
            avatar = user.icon;
        }

        //都没有也没关系 显示默认图
        updateAvatar(StringHelper.getString(avatar));
    }

    private void initFilter() {
        filter = new IntentFilter();
        filter.addAction(LocalBroadcastUtil.LOGIN);
        filter.addAction(LocalBroadcastUtil.LOGOUT);
        filter.addAction(LocalBroadcastUtil.UPDATE_USER_INFO);
    }

    private void setUp() {

        List<Channel> tabList = TabManager.getInstance().getVideoList(getActivity());

        for (Channel subTab : tabList) {
            videoFragmentList.add(VideoListFragment.newInstance(subTab.code));
        }

        mPagerAdapter = new BaseFragmentStateAdapter(getChildFragmentManager(), videoFragmentList,
                TabManager.getInstance().getTabNameList(getActivity(), true));

        mViewPager.setAdapter(mPagerAdapter);
        mTabView.setViewPager(mViewPager);
        mViewPager.setCurrentItem(0);

        mViewPager.addOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset,
                    int positionOffsetPixels) {
                GSYVideoPlayer.releaseAllVideos();
            }
        });
    }

    private void checkTitle() {

        mHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onIntentUser();
            }
        });

        setUserIcon();
    }

    private void onIntentUser() {
        if (AccountManager.getInstance().hasLogin()) {
            String userId = AccountManager.getInstance().getUserId();
            if (!TextUtils.isEmpty(userId)) {
                IntentHelper.openUserCenterPage(getActivity(), userId);
            }
        } else {
            IntentHelper.openLoginPage(getActivity());
        }
    }

    @Override
    protected void beginLoadData() {

    }

    @Override
    protected int getLayoutName() {
        return R.layout.layout_main_video_fg;
    }

    @OnClick(R.id.btn_search)
    public void onClickSearch() {
        IntentHelper.openSearchPage(getActivity());
    }

    public boolean onBackPressed() {
        if (StandardGSYVideoPlayer.backFromWindowFull(getActivity())) {
            return true;
        }
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != receiver) {
            LocalBroadcastUtil.unRegister(receiver);
        }
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (TextUtils.equals(intent.getAction(), LocalBroadcastUtil.UPDATE_USER_INFO)) {

                int intExtra = intent.getIntExtra(LocalBroadcastUtil.UPDATE_USER_INFO, -1);
                if (intExtra == LocalBroadcastUtil.UPDATE_AVATAR) {

                    String stringExtra = intent.getStringExtra(LocalBroadcastUtil.UPDATE_CONTENT);
                    if (!TextUtils.isEmpty(stringExtra)) {
                        updateAvatar(stringExtra);
                    }
                }
            } else if (TextUtils.equals(intent.getAction(), LocalBroadcastUtil.LOGOUT)) {
                updateAvatar("");
            } else if (TextUtils.equals(intent.getAction(), LocalBroadcastUtil.LOGIN)) {
                BaseUser user = AccountManager.getInstance().getUser();
                if (null != user) updateAvatar(user.icon);
            }
        }
    };

    private void updateAvatar(String newAvatar) {
        mHead.setHeadImage(newAvatar);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            StatusUtil.toGreyMode(getActivity());
        }
    }
}
