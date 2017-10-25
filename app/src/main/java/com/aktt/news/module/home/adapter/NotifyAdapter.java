package com.aktt.news.module.home.adapter;

import android.support.annotation.LayoutRes;
import android.text.TextUtils;
import android.view.View;
import com.aktt.news.R;
import com.aktt.news.api.UserService;
import com.aktt.news.instance.AccountManager;
import com.aktt.news.module.user.data.NotifyBean;
import com.aktt.news.net.callback.SimpleCallback;
import com.aktt.news.net.client.NetRequest;
import com.aktt.news.net.data.HttpResult;
import com.aktt.news.util.StringHelper;
import com.aktt.news.widget.image.HeadImageView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.magicalxu.library.blankj.ToastUtils;
import com.mcxtzhang.swipemenulib.SwipeMenuLayout;
import org.json.JSONObject;
import retrofit2.Call;

/**
 * Created by magical on 17/8/17.
 * Description : 我的消息 Item
 */

public class NotifyAdapter extends BaseQuickAdapter<NotifyBean, BaseViewHolder> {

    UserService service;

    public NotifyAdapter(@LayoutRes int layoutResId) {
        super(layoutResId);
        service = NetRequest.getInstance().create(UserService.class);
    }

    @Override
    protected void convert(BaseViewHolder helper, NotifyBean item) {

        final int pos = helper.getLayoutPosition();
        final SwipeMenuLayout root = helper.getView(R.id.id_item_root);

        // TODO: 2017/10/10 系统消息 缺个头像
        ((HeadImageView) helper.getView(R.id.iv_avatar)).setHeadImage("");
        helper.setText(R.id.tv_notify_type, StringHelper.getString(item.title));
        helper.setText(R.id.tv_notify_msg, StringHelper.getString(item.content));
        helper.setText(R.id.tv_notify_time, StringHelper.getString(item.created_time));

        View contentView = helper.getView(R.id.id_item_content);
        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMsgDetail(pos);
            }
        });

        View delView = helper.getView(R.id.id_item_del);
        delView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                delItem(pos, root);
            }
        });
    }

    private void delItem(final int position, final SwipeMenuLayout rootView) {

        NotifyBean notifyBean = getData().get(position);
        String mid = notifyBean.mid;

        if (TextUtils.isEmpty(mid)) {
            return;
        }

        String userId = AccountManager.getInstance().getUserId();
        if (TextUtils.isEmpty(userId)) {
            return;
        }

        Call<HttpResult<JSONObject>> call = service.deleteMyMessageByid(userId, mid);
        call.enqueue(new SimpleCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject data) {
                rootView.quickClose();
                getData().remove(position);
                NotifyAdapter.this.notifyItemRemoved(position);
                NotifyAdapter.this.notifyItemRangeChanged(position, getItemCount());
            }

            @Override
            public void onFail(String code, String errorMsg) {
                ToastUtils.showShort(errorMsg);
            }
        });
    }

    private void showMsgDetail(int position) {

        NotifyBean notifyBean = getData().get(position);
        String mid = notifyBean.mid;
        // TODO: 17/9/4 跳转系统消息详情 界面暂时没有
        //ToastUtils.showShort(mid);
    }
}
