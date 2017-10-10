package com.chaoneng.ilooknews.module.user.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import butterknife.BindView;
import com.chaoneng.ilooknews.R;
import com.chaoneng.ilooknews.api.UserService;
import com.chaoneng.ilooknews.base.BaseActivity;
import com.chaoneng.ilooknews.instance.AccountManager;
import com.chaoneng.ilooknews.net.callback.SimpleCallback;
import com.chaoneng.ilooknews.net.client.NetRequest;
import com.chaoneng.ilooknews.net.data.HttpResult;
import com.chaoneng.ilooknews.widget.ilook.ILookTitleBar;
import com.magicalxu.library.blankj.KeyboardUtils;
import com.magicalxu.library.blankj.ToastUtils;
import retrofit2.Call;

/**
 * Created by magical on 17/8/19.
 * Description : 主页 - 我 - 用户反馈
 */

public class FeedBackActivity extends BaseActivity {

    @BindView(R.id.et_feedback) EditText mFeedbackEt;
    @BindView(R.id.et_contacts) EditText mContactEt;

    private UserService service;

    @Override
    public int getLayoutId() {
        return R.layout.activity_user_feedback;
    }

    @Override
    protected boolean addTitleBar() {
        return true;
    }

    @Override
    public void handleChildPage(Bundle savedInstanceState) {

        service = NetRequest.getInstance().create(UserService.class);
        checkTitle();
    }

    private void checkTitle() {

        mTitleBar.setTitle(getString(R.string.title_feedback));
        mTitleBar.setRightText(getString(R.string.save));
        mTitleBar.setTitleListener(new ILookTitleBar.TitleCallbackAdapter() {
            @Override
            public void onClickLeft(View view) {
                super.onClickLeft(view);
                finish();
            }

            @Override
            public void onClickRightText(View view) {
                super.onClickRightText(view);
                onSubmit();
            }
        });
    }

    /**
     * 提交反馈
     */
    private void onSubmit() {

        String feed = mFeedbackEt.getText().toString().trim();
        String contact = mContactEt.getText().toString().trim();
        if (TextUtils.isEmpty(feed) || TextUtils.isEmpty(contact)) {
            ToastUtils.showShort(getString(R.string.tips_input_feed_and_contact));
            return;
        }

        KeyboardUtils.hideSoftInput(this);

        String userId = AccountManager.getInstance().getUserId();
        if (TextUtils.isEmpty(userId)) {
            ToastUtils.showShort("请先登录！");
            return;
        }

        showLoading();
        Call<HttpResult<String>> call = service.addFeedback(userId, feed);
        call.enqueue(new SimpleCallback<String>() {
            @Override
            public void onSuccess(String data) {
                hideLoading();
                ToastUtils.showShort("反馈成功！");
                onBackPressed();
            }

            @Override
            public void onFail(String code, String errorMsg) {
                onSimpleError(errorMsg);
            }
        });
    }
}
