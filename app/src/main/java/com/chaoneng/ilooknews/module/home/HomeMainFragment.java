package com.chaoneng.ilooknews.module.home;

import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import butterknife.BindView;
import butterknife.OnClick;
import com.chaoneng.ilooknews.AppConstant;
import com.chaoneng.ilooknews.R;
import com.chaoneng.ilooknews.base.BaseFragment;
import com.chaoneng.ilooknews.data.Channel;
import com.chaoneng.ilooknews.instance.TabManager;
import com.chaoneng.ilooknews.module.home.callback.OnChannelListener;
import com.chaoneng.ilooknews.module.home.fragment.ChannelDialogFragment;
import com.chaoneng.ilooknews.module.home.fragment.NewsListFragment;
import com.chaoneng.ilooknews.util.IntentHelper;
import com.chaoneng.ilooknews.util.NotifyListener;
import com.chaoneng.ilooknews.widget.adapter.BaseFragmentStateAdapter;
import com.chaoneng.ilooknews.widget.adapter.OnPageChangeListener;
import com.chaoneng.ilooknews.widget.image.HeadImageView;
import com.flyco.tablayout.SlidingTabLayout;
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by magical on 17/8/15.
 * Description : 主页 - 首页
 */

public class HomeMainFragment extends BaseFragment implements OnChannelListener {

    @BindView(R.id.iv_avatar) HeadImageView mHeadView;
    @BindView(R.id.ll_search) LinearLayout mSearchView;
    @BindView(R.id.iv_notify) ImageView mNotifyView;
    @BindView(R.id.sliding_tabs) SlidingTabLayout mTabView;
    @BindView(R.id.iv_edit_channel) ImageView mMorePlusView;
    @BindView(R.id.id_view_pager) ViewPager mViewPager;

    private BaseFragmentStateAdapter mPagerAdapter;
    private List<Fragment> newsFragmentList = new ArrayList<>();

    @Override
    protected void beginLoadData() {

        mHeadView.setHeadImage(AppConstant.TEST_AVATAR);
    }

    @Override
    protected void doInit() {

        if (TabManager.getInstance().hasNewsInit()) {
            setUp();
        } else {
            TabManager.getInstance().getNewsChannel(getActivity(), new NotifyListener() {
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
        List<Channel> tabList = TabManager.getInstance().getTabList(getActivity());

        for (Channel subTab : tabList) {
            newsFragmentList.add(NewsListFragment.newInstance(subTab.code));
        }

        mPagerAdapter = new BaseFragmentStateAdapter(getChildFragmentManager(), newsFragmentList,
                TabManager.getInstance().getTabNameList(getActivity(), false));

        mViewPager.setAdapter(mPagerAdapter);
        mTabView.setViewPager(mViewPager);

        mViewPager.addOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset,
                    int positionOffsetPixels) {
                GSYVideoPlayer.releaseAllVideos();
            }
        });
    }

    @Override
    protected int getLayoutName() {
        return R.layout.layout_main_home_fg;
    }

    @OnClick(R.id.ll_search)
    public void toSearchPage() {
        IntentHelper.openSearchPage(getActivity());
    }

    @OnClick(R.id.iv_avatar)
    public void toUserCenterPage() {
        IntentHelper.openUserCenterPage(getActivity());
    }

    @OnClick(R.id.iv_notify)
    public void toNotifyPage() {
        IntentHelper.openNotifyPage(getActivity());
    }

    @OnClick(R.id.iv_edit_channel)
    public void toMoreTabPage() {
        ChannelDialogFragment dialogFragment = ChannelDialogFragment.newInstance();
        dialogFragment.setOnChannelListener(this);
        dialogFragment.show(getChildFragmentManager(), "CHANNEL");
        dialogFragment.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {

                //update database
                TabManager.getInstance().updateDb();

                mPagerAdapter.updateTitle(
                        TabManager.getInstance().getTabNameList(getActivity(), false));
                mPagerAdapter.notifyDataSetChanged();
                mTabView.notifyDataSetChanged();
                //mVp.setOffscreenPageLimit(mSelectedDatas.size());
                //tab.setCurrentItem(tab.getSelectedTabPosition());
            }
        });
    }

    @Override
    public void onItemMove(int starPos, int endPos) {
        listMove(TabManager.getInstance().getTabList(getActivity()), starPos, endPos);
        listMove(newsFragmentList, starPos, endPos);
    }

    @Override
    public void onMoveToMyChannel(int starPos, int endPos) {

        Channel channel = TabManager.getInstance().moveToMyChannel(starPos, endPos);
        if (null != channel) {
            newsFragmentList.add(NewsListFragment.newInstance(channel.code));
        }
    }

    @Override
    public void onMoveToOtherChannel(int starPos, int endPos) {
        TabManager.getInstance().moveToOtherChannel(starPos, endPos);
        newsFragmentList.remove(starPos);
    }

    private void listMove(List datas, int starPos, int endPos) {
        Object o = datas.get(starPos);
        //先删除之前的位置
        datas.remove(starPos);
        //添加到现在的位置
        datas.add(endPos, o);
    }
}
