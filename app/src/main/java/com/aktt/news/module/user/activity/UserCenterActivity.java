package com.aktt.news.module.user.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.OnClick;
import com.aktt.news.AppConstant;
import com.aktt.news.R;
import com.aktt.news.api.UserService;
import com.aktt.news.base.BaseActivity;
import com.aktt.news.instance.AccountManager;
import com.aktt.news.module.user.data.UserInfoWrapper;
import com.aktt.news.module.user.fragment.BrokeNewsListFragment;
import com.aktt.news.module.user.fragment.StateListFragment;
import com.aktt.news.net.callback.SimpleCallback;
import com.aktt.news.net.client.NetRequest;
import com.aktt.news.net.data.HttpResult;
import com.aktt.news.util.IntentHelper;
import com.aktt.news.util.SimpleNotifyListener;
import com.aktt.news.util.StringHelper;
import com.aktt.news.util.UserOptionHelper;
import com.aktt.news.widget.adapter.BaseFragmentAdapter;
import com.aktt.news.widget.image.HeadImageView;
import com.flyco.tablayout.SlidingTabLayout;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;

/**
 * Created by magical on 2017/8/23.
 * 他人中心界面
 */
public class UserCenterActivity extends BaseActivity {

    @BindView(R.id.iv_avatar) HeadImageView ivAvatar;
    @BindView(R.id.tv_name) TextView tvName;
    @BindView(R.id.tv_focus) TextView tvFocus;
    @BindView(R.id.id_focus) TextView focusView;
    @BindView(R.id.id_fans) TextView fansView;
    @BindView(R.id.tv_signature) TextView tvSignature;
    @BindView(R.id.sliding_tabs) SlidingTabLayout slidingTabLayout;
    @BindView(R.id.view_pager) ViewPager viewPager;
    @BindView(R.id.toolbar) Toolbar toolbar;

    private BaseFragmentAdapter baseFragmentAdapter;
    private UserService service;
    private String pageUid;
    private boolean hasFollowed;

    private ArrayList<Call> callList;

    public static void getInstance(Context context, String userId) {
        Intent intent = new Intent(context, UserCenterActivity.class);
        intent.putExtra(IntentHelper.PARAMS_ONE, userId);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_user_center;
    }

    @Override
    public void handleChildPage(Bundle savedInstanceState) {

        Intent intent = getIntent();
        pageUid = intent.getStringExtra(IntentHelper.PARAMS_ONE);
        service = NetRequest.getInstance().create(UserService.class);

        initView();
        loadData();
    }

    @Override
    public ArrayList<Call> addRequestList() {
        callList = new ArrayList<>();
        return callList;
    }

    public void initView() {
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        String userId = AccountManager.getInstance().getUserId();
        if (TextUtils.equals(userId, pageUid)) {
            tvFocus.setVisibility(View.GONE);
        } else {
            tvFocus.setVisibility(View.VISIBLE);
        }

        ArrayList<Fragment> fragments = new ArrayList<>();
        fragments.add(StateListFragment.getInstance(pageUid));
        fragments.add(BrokeNewsListFragment.getInstance(pageUid));

        List<String> titles = new ArrayList<>(2);
        titles.add("动态");
        titles.add("爆料");

        baseFragmentAdapter =
                new BaseFragmentAdapter(getSupportFragmentManager(), fragments, titles);
        viewPager.setAdapter(baseFragmentAdapter);
        slidingTabLayout.setViewPager(viewPager);
    }

    private void initUserInfo(UserInfoWrapper data) {

        hideLoading();

        ivAvatar.setHeadImage(StringHelper.getString(data.userIcon));
        tvName.setText(StringHelper.getString(data.nickname));
        tvSignature.setText(StringHelper.getString(data.introduce));

        focusView.setText(String.valueOf(data.followedNum));
        fansView.setText(String.valueOf(data.fansNum));

        hasFollowed = AppConstant.USER_FOLLOW_INT == data.isfollow;
        checkFollowState(hasFollowed);
    }

    private void checkFollowState(boolean isFollow) {

        tvFocus.setText(isFollow ? "已关注" : "关注");
    }

    private void loadData() {

        if (TextUtils.isEmpty(pageUid)) {
            return;
        }

        String userId = AccountManager.getInstance().getUserId();

        showLoading();
        final Call<HttpResult<UserInfoWrapper>> call =
                service.getUserInfo(StringHelper.getString(userId), pageUid, 1, 1,
                        AppConstant.DEFAULT_PAGE_SIZE);
        onAddCall(call);
        call.enqueue(new SimpleCallback<UserInfoWrapper>() {
            @Override
            public void onSuccess(UserInfoWrapper data) {
                onRemoveCall(call);
                initUserInfo(data);
            }

            @Override
            public void onFail(String code, String errorMsg) {
                onRemoveCall(call);
                onSimpleError(errorMsg);
            }
        });
    }

    @OnClick({ R.id.iv_avatar, R.id.tv_focus })
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_avatar:
                break;
            case R.id.tv_focus:
                onClickFocus();
                break;
        }
    }

    private void onClickFocus() {

        if (AccountManager.getInstance().checkLogin(this)) {
            return;
        }

        if (isLoading()) {
            return;
        }

        if (hasFollowed) {
            //操作取消关注
            UserOptionHelper.onCancelFollow(service, pageUid, new SimpleNotifyListener() {
                @Override
                public void onSuccess(String msg) {
                    hideLoading();
                    hasFollowed = false;
                    checkFollowState(hasFollowed);
                }

                @Override
                public void onFailed(String msg) {
                    onSimpleError(msg);
                }
            });
        } else {
            //操作关注
            UserOptionHelper.onFollow(service, pageUid, new SimpleNotifyListener() {
                @Override
                public void onSuccess(String msg) {
                    hideLoading();
                    hasFollowed = true;
                    checkFollowState(hasFollowed);
                }

                @Override
                public void onFailed(String msg) {
                    onSimpleError(msg);
                }
            });
        }
    }
}
