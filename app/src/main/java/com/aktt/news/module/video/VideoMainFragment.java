package com.aktt.news.module.video;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.widget.ImageView;
import butterknife.BindView;
import butterknife.OnClick;
import com.aktt.news.AppConstant;
import com.aktt.news.R;
import com.aktt.news.base.BaseTitleFragment;
import com.aktt.news.data.Channel;
import com.aktt.news.instance.TabManager;
import com.aktt.news.module.video.fragment.VideoListFragment;
import com.aktt.news.util.IntentHelper;
import com.aktt.news.util.NotifyListener;
import com.aktt.news.widget.adapter.BaseFragmentStateAdapter;
import com.aktt.news.widget.adapter.OnPageChangeListener;
import com.flyco.tablayout.SlidingTabLayout;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by magical on 17/8/15.
 * Description : 主页 - 视频
 */

public class VideoMainFragment extends BaseTitleFragment {

    @BindView(R.id.sliding_tabs) SlidingTabLayout mTabView;
    @BindView(R.id.btn_search) ImageView mSearchView;
    @BindView(R.id.view_pager) ViewPager mViewPager;

    private BaseFragmentStateAdapter mPagerAdapter;
    private List<Fragment> videoFragmentList = new ArrayList<>();

    @Override
    public void init() {
        checkTitle();

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
        mTitleBar.setTitleImage(R.drawable.img_video_title)
                .setLeftCircle(AppConstant.TEST_AVATAR)
                .hideDivider();
    }

    @Override
    public int getSubLayout() {
        return R.layout.layout_main_video_fg;
    }

    @Override
    protected void beginLoadData() {

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
}
