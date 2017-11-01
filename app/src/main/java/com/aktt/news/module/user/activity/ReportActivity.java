package com.aktt.news.module.user.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import butterknife.BindView;
import com.aktt.news.AppConstant;
import com.aktt.news.R;
import com.aktt.news.api.UserService;
import com.aktt.news.base.BaseActivity;
import com.aktt.news.instance.AccountManager;
import com.aktt.news.net.callback.SimpleJsonCallback;
import com.aktt.news.net.client.NetRequest;
import com.aktt.news.net.data.HttpResult;
import com.aktt.news.widget.ilook.ILookTitleBar;
import com.magicalxu.library.blankj.ToastUtils;
import java.util.ArrayList;
import org.json.JSONObject;
import retrofit2.Call;

/**
 * Created by magical on 17/11/1.
 * Description : 举报界面
 */

public class ReportActivity extends BaseActivity {

    @BindView(R.id.id_report_edit) EditText mEditText;

    private String PAGE_USER_ID;
    private String PAGE_NEWS_ID;
    private int PAGE_NEWS_TYPE;

    private UserService service;

    public static void getInstance(Context context, String toId, String newsId, int newsType) {
        Intent intent = new Intent(context, ReportActivity.class);
        intent.putExtra(AppConstant.PARAMS_USER_ID, toId);
        intent.putExtra(AppConstant.PARAMS_NEWS_ID, newsId);
        intent.putExtra(AppConstant.PARAMS_NEWS_TYPE, newsType);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_report;
    }

    @Override
    protected boolean addTitleBar() {
        return true;
    }

    @Override
    public void handleChildPage(Bundle savedInstanceState) {

        service = NetRequest.getInstance().create(UserService.class);

        mTitleBar.setTitle("举报")
                .setRightText("提交")
                .setTitleListener(new ILookTitleBar.TitleCallbackAdapter() {
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

        Intent intent = getIntent();
        PAGE_USER_ID = intent.getStringExtra(AppConstant.PARAMS_USER_ID);
        PAGE_NEWS_ID = intent.getStringExtra(AppConstant.PARAMS_NEWS_ID);
        PAGE_NEWS_TYPE = intent.getIntExtra(AppConstant.PARAMS_NEWS_TYPE, 0);
    }

    private void onSubmit() {

        String reason = mEditText.getText().toString();
        if (TextUtils.isEmpty(reason)) {
            ToastUtils.showShort("请填写原因");
            return;
        }

        String userId = AccountManager.getInstance().getUserId();
        Call<HttpResult<JSONObject>> call =
                service.report(userId, PAGE_USER_ID, PAGE_NEWS_ID, PAGE_NEWS_TYPE, reason);
        showLoading();
        call.enqueue(new SimpleJsonCallback<HttpResult<JSONObject>>() {
            @Override
            public void onSuccess(HttpResult<JSONObject> data) {
                hideLoading();
                ToastUtils.showShort("举报成功");
                finish();
            }

            @Override
            public void onFailed(int code, String message) {
                onSimpleError(message);
            }
        });
    }

    @Override
    public ArrayList<Call> addRequestList() {
        return null;
    }
}
