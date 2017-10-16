package com.chaoneng.ilooknews.module.home;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.OnClick;
import com.chaoneng.ilooknews.AppConstant;
import com.chaoneng.ilooknews.R;
import com.chaoneng.ilooknews.api.HomeService;
import com.chaoneng.ilooknews.base.BaseFragment;
import com.chaoneng.ilooknews.data.BaseUser;
import com.chaoneng.ilooknews.data.Channel;
import com.chaoneng.ilooknews.instance.AccountManager;
import com.chaoneng.ilooknews.instance.TabManager;
import com.chaoneng.ilooknews.module.home.callback.OnChannelListener;
import com.chaoneng.ilooknews.module.home.fragment.ChannelDialogFragment;
import com.chaoneng.ilooknews.module.home.fragment.NewsListFragment;
import com.chaoneng.ilooknews.net.callback.SimpleCallback;
import com.chaoneng.ilooknews.net.client.NetRequest;
import com.chaoneng.ilooknews.net.data.HttpResult;
import com.chaoneng.ilooknews.util.IntentHelper;
import com.chaoneng.ilooknews.util.LocalBroadcastUtil;
import com.chaoneng.ilooknews.util.NotifyListener;
import com.chaoneng.ilooknews.util.StringHelper;
import com.chaoneng.ilooknews.widget.adapter.BaseFragmentStateAdapter;
import com.chaoneng.ilooknews.widget.adapter.OnPageChangeListener;
import com.chaoneng.ilooknews.widget.image.HeadImageView;
import com.flyco.tablayout.SlidingTabLayout;
import com.magicalxu.library.blankj.SPUtils;
import com.magicalxu.library.blankj.ToastUtils;
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;

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
    @BindView(R.id.id_search_key) TextView mSearchKey;

    private BaseFragmentStateAdapter mPagerAdapter;
    private List<Fragment> newsFragmentList = new ArrayList<>();
    private HomeService homeService;
    private IntentFilter filter;

    @Override
    protected void beginLoadData() {

        loadSearchKey();
        setUserIcon();
    }

    private void initFilter() {
        filter = new IntentFilter();
        filter.addAction(LocalBroadcastUtil.LOGIN);
        filter.addAction(LocalBroadcastUtil.LOGOUT);
        filter.addAction(LocalBroadcastUtil.UPDATE_USER_INFO);
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
        mHeadView.setHeadImage(StringHelper.getString(avatar));
    }

    /**
     * 加载搜索提示
     */
    private void loadSearchKey() {

        Call<HttpResult<String>> call = homeService.getSearchKey();
        call.enqueue(new SimpleCallback<String>() {
            @Override
            public void onSuccess(String data) {
                if (!TextUtils.isEmpty(data)) {
                    mSearchKey.setText(data);
                }
            }

            @Override
            public void onFail(String code, String errorMsg) {
                ToastUtils.showShort(errorMsg);
            }
        });
    }

    @Override
    protected void doInit() {

        homeService = NetRequest.getInstance().create(HomeService.class);

        initFilter();
        if (null != receiver && null != filter) {
            LocalBroadcastUtil.register(receiver, filter);
        }

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
        if (AccountManager.getInstance().hasLogin()) {
            String userId = AccountManager.getInstance().getUserId();
            if (!TextUtils.isEmpty(userId)) {
                IntentHelper.openUserCenterPage(getActivity(), userId);
            }
        } else {
            IntentHelper.openLoginPage(getActivity());
        }
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
                loadFirstPage();
            }
        });
    }

    private void loadFirstPage() {
        if (mPagerAdapter.getCount() > 0) {

            mViewPager.setCurrentItem(0);
            mTabView.setCurrentTab(0);
            mPagerAdapter.getItem(0).setUserVisibleHint(true);
        }
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

    private void updateAvatar(String newAvatar) {
        mHeadView.setHeadImage(newAvatar);
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
}
