package com.aktt.news.module.focus.adapter;

import android.support.annotation.LayoutRes;
import android.text.TextUtils;
import android.view.View;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.aktt.news.AppConstant;
import com.aktt.news.R;
import com.aktt.news.api.UserService;
import com.aktt.news.base.BaseActivity;
import com.aktt.news.instance.AccountManager;
import com.aktt.news.module.focus.data.FocusBean;
import com.aktt.news.net.callback.SimpleCallback;
import com.aktt.news.net.client.NetRequest;
import com.aktt.news.net.data.HttpResult;
import com.aktt.news.util.IntentHelper;
import com.aktt.news.util.StringHelper;
import com.aktt.news.widget.ilook.UserItemRowView;
import com.magicalxu.library.blankj.ToastUtils;
import java.util.List;
import org.json.JSONObject;
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
    protected void convert(final BaseViewHolder helper, final FocusBean item) {

        if (null == item) {
            return;
        }

        UserItemRowView view = helper.getView(R.id.id_user_row);
        view.setHead(StringHelper.getString(item.icon))
                .setName(StringHelper.getString(item.nickname))
                .setIntro(TextUtils.isEmpty(item.introduce) ? "这家伙很懒，什么也没有留下~" : item.introduce)
                .changeFollowState(TextUtils.equals(AppConstant.USER_FOLLOW, item.isFollow))
                .setFocusListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        checkFocus(item, helper.getAdapterPosition());
                    }
                });
    }

    private void checkFocus(FocusBean item, int position) {

        String userId = AccountManager.getInstance().getUserId();
        if (TextUtils.isEmpty(userId)) {
            IntentHelper.openLoginPage(mContext);
            return;
        }

        BaseActivity baseActivity = (BaseActivity) this.mContext;
        if (baseActivity.isLoading()) {
            return;
        }

        baseActivity.showLoading();
        if (TextUtils.equals(item.isFollow, AppConstant.USER_UN_FOLLOW)) {
            addFocus(baseActivity, userId, item.target_id, position);
        } else {
            cancelFocus(baseActivity, userId, item.target_id, position);
        }
    }

    private void cancelFocus(final BaseActivity baseActivity, String userId, String targetId,
            final int position) {

        Call<HttpResult<JSONObject>> call = service.cancelFollow(userId, targetId);
        call.enqueue(new SimpleCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject data) {
                ToastUtils.showShort("取消关注成功！");
                baseActivity.hideLoading();
                changeFollowState(position, false);
            }

            @Override
            public void onFail(String code, String errorMsg) {
                baseActivity.hideLoading();
                ToastUtils.showShort(errorMsg);
            }
        });
    }

    private void addFocus(final BaseActivity activity, String userId, String targetId,
            final int position) {

        Call<HttpResult<JSONObject>> call = service.follow(userId, targetId);
        call.enqueue(new SimpleCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject data) {
                ToastUtils.showShort("加关注成功！");
                activity.hideLoading();
                changeFollowState(position, true);
            }

            @Override
            public void onFail(String code, String errorMsg) {
                ToastUtils.showShort(errorMsg);
                activity.hideLoading();
            }
        });
    }

    private void changeFollowState(int position, boolean hasFollowed) {

        List<FocusBean> data = this.getData();
        if (data.size() > position) {

            FocusBean focusBean = data.get(position);
            focusBean.isFollow = hasFollowed ? AppConstant.USER_FOLLOW : AppConstant.USER_UN_FOLLOW;
            notifyDataSetChanged();
        }
    }
}
