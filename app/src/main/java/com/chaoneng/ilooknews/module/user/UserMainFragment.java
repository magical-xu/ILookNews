package com.chaoneng.ilooknews.module.user;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.OnClick;
import com.chaoneng.ilooknews.AppConstant;
import com.chaoneng.ilooknews.R;
import com.chaoneng.ilooknews.api.UserService;
import com.chaoneng.ilooknews.base.BaseFragment;
import com.chaoneng.ilooknews.data.BaseUser;
import com.chaoneng.ilooknews.instance.AccountManager;
import com.chaoneng.ilooknews.module.user.data.UserCenterWrapper;
import com.chaoneng.ilooknews.module.user.widget.RelationView;
import com.chaoneng.ilooknews.net.callback.SimpleCallback;
import com.chaoneng.ilooknews.net.client.NetRequest;
import com.chaoneng.ilooknews.net.data.HttpResult;
import com.chaoneng.ilooknews.util.IntentHelper;
import com.chaoneng.ilooknews.widget.image.HeadImageView;
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

    private UserService service;

    @Override
    protected void beginLoadData() {

        String uid = AccountManager.getInstance().getUserId();

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
        user.username = data.nickname;
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
        mTvTopic.setCount(String.valueOf(data.followedNum));
        mTvFollow.setCount(String.valueOf(data.followedNum));
        mTvFans.setCount(String.valueOf(data.fansNum));
        mTvVisit.setCount(String.valueOf(data.visitorsNum));

        // user data
        mHeadIv.setHeadImage(data.userIcon);
        mTvNick.setText(data.nickname);
        mTvSign.setText(data.introduce);
    }

    @Override
    protected void doInit() {

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
            R.id.credit_visitors
    })
    public void onItemClick(View view) {

        switch (view.getId()) {
            case R.id.id_item_msg:
                IntentHelper.openNotifyPage(getActivity());
                break;
            case R.id.id_item_collect:
                ToastUtils.showShort("我的收藏");
                break;
            case R.id.id_item_broke:
                IntentHelper.openBrokePage(getActivity());
                break;
            case R.id.id_item_feedback:
                IntentHelper.openFeedbackPage(getActivity());
                break;
            case R.id.id_item_setting:
                IntentHelper.openSettingPage(getActivity());
                break;
            case R.id.credit_topic:
                ToastUtils.showShort("动态");
                break;
            case R.id.credit_following:
                ToastUtils.showShort("关注");
                break;
            case R.id.credit_fans:
                ToastUtils.showShort("粉丝");
                break;
            case R.id.credit_visitors:
                ToastUtils.showShort("访客");
                break;
        }
    }
}
