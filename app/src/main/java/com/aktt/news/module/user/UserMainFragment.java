package com.aktt.news.module.user;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.OnClick;
import com.aktt.news.AppConstant;
import com.aktt.news.R;
import com.aktt.news.api.UserService;
import com.aktt.news.base.BaseFragment;
import com.aktt.news.data.BaseUser;
import com.aktt.news.instance.AccountManager;
import com.aktt.news.module.user.data.UserCenterWrapper;
import com.aktt.news.module.user.widget.RelationView;
import com.aktt.news.net.callback.SimpleCallback;
import com.aktt.news.net.client.NetRequest;
import com.aktt.news.net.data.HttpResult;
import com.aktt.news.util.IntentHelper;
import com.aktt.news.util.LocalBroadcastUtil;
import com.aktt.news.widget.image.HeadImageView;
import com.magicalxu.library.blankj.ToastUtils;
import retrofit2.Call;

/**
 * Created by magical on 17/8/15.
 * Description : 主页 - 个人
 */

public class UserMainFragment extends BaseFragment {

    @BindView(R.id.iv_avatar) HeadImageView mHeadIv;
    @BindView(R.id.id_nickname) TextView mTvNick;
    @BindView(R.id.id_sign) TextView mTvSign;
    @BindView(R.id.credit_topic) RelationView mTvTopic;
    @BindView(R.id.credit_following) RelationView mTvFollow;
    @BindView(R.id.credit_fans) RelationView mTvFans;
    @BindView(R.id.credit_visitors) RelationView mTvVisit;
    @BindView(R.id.id_item_msg) RelativeLayout mItemMsg;
    @BindView(R.id.id_item_collect) RelativeLayout mItemCollect;
    @BindView(R.id.id_item_broke) RelativeLayout mItemBroke;
    @BindView(R.id.id_item_feedback) RelativeLayout mItemFeed;
    @BindView(R.id.id_item_setting) RelativeLayout mItemSetting;
    @BindView(R.id.id_item_broke_list) RelativeLayout mItemBrokeList;

    private UserService service;
    private IntentFilter filter;

    @Override
    protected void beginLoadData() {

        String uid = AccountManager.getInstance().getUserId();
        if (TextUtils.isEmpty(uid)) {
            // 未登录状态
            setLogoutState();
        } else {
            loadUserInfo(uid);
        }
    }

    private void initFilter() {
        filter = new IntentFilter();
        filter.addAction(LocalBroadcastUtil.UPDATE_USER_INFO);
        filter.addAction(LocalBroadcastUtil.LOGOUT);
        filter.addAction(LocalBroadcastUtil.LOGIN);
    }

    private void loadUserInfo(String uid) {
        service = NetRequest.getInstance().create(UserService.class);
        Call<HttpResult<UserCenterWrapper>> call =
                service.getMyCenter(uid, 1, AppConstant.DEFAULT_PAGE_SIZE);

        call.enqueue(new SimpleCallback<UserCenterWrapper>() {
            @Override
            public void onSuccess(UserCenterWrapper data) {

                saveUserData(data);
                bindUserData(data);
            }

            @Override
            public void onFail(String code, String errorMsg) {
                ToastUtils.showShort(errorMsg);
            }
        });
    }

    /**
     * 保存一份用户信息
     */
    private void saveUserData(UserCenterWrapper data) {

        BaseUser user = new BaseUser();
        user.icon = data.userIcon;
        user.nickname = data.nickname;
        user.address = data.address;
        user.city = data.city;
        user.fansNum = data.fansNum;
        user.followedNum = data.followedNum;
        user.gender = data.gender;
        user.introduce = data.introduce;
        user.visitorsNum = data.visitorsNum;
        user.province = data.province;

        AccountManager.getInstance().saveUser(user, false);
    }

    private void bindUserData(UserCenterWrapper data) {

        if (null == data) {
            return;
        }

        // relation data
        mTvTopic.setCount(String.valueOf(data.dongtai));
        mTvFollow.setCount(String.valueOf(data.followedNum));
        mTvFans.setCount(String.valueOf(data.fansNum));
        mTvVisit.setCount(String.valueOf(data.visitorsNum));

        // user data
        mHeadIv.setHeadImage(data.userIcon);
        mTvNick.setText(data.nickname);
        mTvSign.setText(String.format(getString(R.string.sign_pre), data.introduce));
    }

    @Override
    protected void doInit() {

        //StatusBarCompat.setStatusBarColor(getActivity(),
        //        ContextCompat.getColor(getActivity(), R.color.white));
        initFilter();

        if (null != filter && null != receiver) {
            //Log.d("magical", " doInit : register");
            LocalBroadcastUtil.register(receiver, filter);
        }

        mTvTopic.setBottom("动态").hideRedPoint();
        mTvFollow.setBottom("关注").hideRedPoint();
        mTvFans.setBottom("粉丝").hideRedPoint();
        mTvVisit.setBottom("访客").hideRedPoint();
    }

    @Override
    protected int getLayoutName() {
        return R.layout.layout_main_user_fg;
    }

    @OnClick({
            R.id.id_item_msg, R.id.id_item_collect, R.id.id_item_broke, R.id.id_item_feedback,
            R.id.id_item_setting, R.id.credit_topic, R.id.credit_following, R.id.credit_fans,
            R.id.credit_visitors, R.id.id_item_broke_list
    })
    public void onItemClick(View view) {

        switch (view.getId()) {
            case R.id.id_item_msg:
                if (AccountManager.getInstance().checkLogin(getActivity())) {
                    return;
                }
                IntentHelper.openNotifyPage(getActivity());
                break;
            case R.id.id_item_collect:
                if (AccountManager.getInstance().checkLogin(getActivity())) {
                    return;
                }
                IntentHelper.openCollectionPage(getActivity());
                break;
            case R.id.id_item_broke:
                if (AccountManager.getInstance().checkLogin(getActivity())) {
                    return;
                }
                IntentHelper.openBrokePage(getActivity());
                break;
            case R.id.id_item_feedback:
                IntentHelper.openFeedbackPage(getActivity());
                break;
            case R.id.id_item_setting:
                IntentHelper.openSettingPage(getActivity());
                break;
            case R.id.id_item_broke_list:
                //if (AccountManager.getInstance().checkLogin(getActivity())) {
                //    return;
                //}
                IntentHelper.openBrokeListPage(getActivity());
                break;
            case R.id.credit_topic:
                break;
            case R.id.credit_following:
                break;
            case R.id.credit_fans:
                break;
            case R.id.credit_visitors:
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != receiver) {
            //Log.d("magical", " onDestroy : unregister ");
            LocalBroadcastUtil.unRegister(receiver);
        }
    }

    private void updateUserInfo(String change, int type) {

        if (type == LocalBroadcastUtil.UPDATE_AVATAR) {
            mHeadIv.setHeadImage(change);
        } else if (type == LocalBroadcastUtil.UPDATE_NICK) {
            mTvNick.setText(change);
        } else if (type == LocalBroadcastUtil.UPDATE_SIGN) {
            mTvSign.setText(String.format(getString(R.string.sign_pre), change));
        }
    }

    private void setLogoutState() {

        mHeadIv.setHeadImage("");
        mTvNick.setText("未登录");
        mTvSign.setText("点击头像可进行登录");

        mTvTopic.setCount(AppConstant.NONE_VALUE);
        mTvFollow.setCount(AppConstant.NONE_VALUE);
        mTvFans.setCount(AppConstant.NONE_VALUE);
        mTvVisit.setCount(AppConstant.NONE_VALUE);
    }

    private void setLoginState() {

        BaseUser data = AccountManager.getInstance().getUser();
        if (null != data) {

            UserCenterWrapper user = new UserCenterWrapper();
            user.userIcon = data.icon;
            user.nickname = data.nickname;
            user.address = data.address;
            user.city = data.city;
            user.fansNum = data.fansNum;
            user.followedNum = data.followedNum;
            user.gender = data.gender;
            user.introduce = data.introduce;
            user.visitorsNum = data.visitorsNum;
            user.province = data.province;
            user.dongtai = data.dongTai;
            bindUserData(user);
        }
    }

    @OnClick(R.id.iv_avatar)
    public void onClickAvatar(View view) {

        if (AccountManager.getInstance().checkLogin(getActivity())) {
            return;
        }
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Log.d("magical", " onReceive : " + intent.getAction());

            if (TextUtils.equals(intent.getAction(), LocalBroadcastUtil.UPDATE_USER_INFO)) {

                int intExtra = intent.getIntExtra(LocalBroadcastUtil.UPDATE_USER_INFO, -1);
                String stringExtra = intent.getStringExtra(LocalBroadcastUtil.UPDATE_CONTENT);
                if (TextUtils.isEmpty(stringExtra)) {
                    return;
                }
                updateUserInfo(stringExtra, intExtra);
            } else if (TextUtils.equals(intent.getAction(), LocalBroadcastUtil.LOGOUT)) {
                setLogoutState();
            } else if (TextUtils.equals(intent.getAction(), LocalBroadcastUtil.LOGIN)) {
                setLoginState();
            }
        }
    };

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            //StatusBarCompat.setStatusBarColor(getActivity(),
            //        ContextCompat.getColor(getActivity(), R.color.white));
        }
    }
}
