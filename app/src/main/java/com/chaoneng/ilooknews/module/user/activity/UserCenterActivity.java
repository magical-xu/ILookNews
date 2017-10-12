package com.chaoneng.ilooknews.module.user.activity;

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
import com.chaoneng.ilooknews.AppConstant;
import com.chaoneng.ilooknews.R;
import com.chaoneng.ilooknews.api.UserService;
import com.chaoneng.ilooknews.base.BaseActivity;
import com.chaoneng.ilooknews.module.user.data.UserInfoWrapper;
import com.chaoneng.ilooknews.module.user.fragment.BrokeNewsListFragment;
import com.chaoneng.ilooknews.module.user.fragment.StateListFragment;
import com.chaoneng.ilooknews.net.callback.SimpleCallback;
import com.chaoneng.ilooknews.net.client.NetRequest;
import com.chaoneng.ilooknews.net.data.HttpResult;
import com.chaoneng.ilooknews.util.IntentHelper;
import com.chaoneng.ilooknews.util.StringHelper;
import com.chaoneng.ilooknews.widget.adapter.BaseFragmentAdapter;
import com.chaoneng.ilooknews.widget.image.HeadImageView;
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

    public void initView() {
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

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

        // TODO: 2017/10/9 差个关注
    }

    private void loadData() {

        if (TextUtils.isEmpty(pageUid)) {
            return;
        }

        showLoading();
        Call<HttpResult<UserInfoWrapper>> call =
                service.getUserInfo(pageUid, pageUid, 1, 1, AppConstant.DEFAULT_PAGE_SIZE);
        call.enqueue(new SimpleCallback<UserInfoWrapper>() {
            @Override
            public void onSuccess(UserInfoWrapper data) {
                initUserInfo(data);
            }

            @Override
            public void onFail(String code, String errorMsg) {
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
                break;
        }
    }
}
