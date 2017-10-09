package com.chaoneng.ilooknews.module.focus.adapter;

import android.support.annotation.LayoutRes;
import android.text.TextUtils;
import android.view.View;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chaoneng.ilooknews.AppConstant;
import com.chaoneng.ilooknews.R;
import com.chaoneng.ilooknews.api.UserService;
import com.chaoneng.ilooknews.module.focus.data.FocusBean;
import com.chaoneng.ilooknews.net.callback.SimpleCallback;
import com.chaoneng.ilooknews.net.client.NetRequest;
import com.chaoneng.ilooknews.net.data.HttpResult;
import com.chaoneng.ilooknews.util.StringHelper;
import com.chaoneng.ilooknews.widget.ilook.UserItemRowView;
import com.magicalxu.library.blankj.ToastUtils;
import retrofit2.Call;

/**
 * Created by magical on 2017/9/4.
 * Description : 推荐的关注列表
 */

public class FocusAddAdapter extends BaseQuickAdapter<FocusBean, BaseViewHolder> {

    private UserService service;

    public FocusAddAdapter(@LayoutRes int layoutResId) {
        super(layoutResId);
        service = NetRequest.getInstance().create(UserService.class);
    }

    @Override
    protected void convert(BaseViewHolder helper, final FocusBean item) {

        if (null == item) {
            return;
        }

        UserItemRowView view = helper.getView(R.id.id_user_row);
        view.setHead(StringHelper.getString(item.icon))
                .setName(StringHelper.getString(item.nickname))
                .setIntro(StringHelper.getString(item.introduce))
                .setFocusListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        checkFocus(item);
                    }
                });
    }

    private void checkFocus(FocusBean item) {

        if (TextUtils.equals(item.isFollow, AppConstant.USER_UN_FOLLOW)) {
            addFocus(item.target_id);
        } else {
            cancelFocus(item.target_id);
        }
    }

    private void cancelFocus(String targetId) {

        Call<HttpResult<String>> call = service.cancelFollow(AppConstant.TEST_USER_ID, targetId);
        call.enqueue(new SimpleCallback<String>() {
            @Override
            public void onSuccess(String data) {
                ToastUtils.showShort("取消关注成功！");
                // TODO: 2017/9/4 改变UI
            }

            @Override
            public void onFail(String code, String errorMsg) {
                ToastUtils.showShort(errorMsg);
            }
        });
    }

    private void addFocus(String targetId) {

        Call<HttpResult<String>> call = service.follow(AppConstant.TEST_USER_ID, targetId);
        call.enqueue(new SimpleCallback<String>() {
            @Override
            public void onSuccess(String data) {
                ToastUtils.showShort("加关注成功！");
                // TODO: 2017/9/4 改变UI
            }

            @Override
            public void onFail(String code, String errorMsg) {
                ToastUtils.showShort(errorMsg);
            }
        });
    }
}
