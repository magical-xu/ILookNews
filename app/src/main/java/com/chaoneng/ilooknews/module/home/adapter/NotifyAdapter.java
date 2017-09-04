package com.chaoneng.ilooknews.module.home.adapter;

import android.support.annotation.LayoutRes;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chaoneng.ilooknews.AppConstant;
import com.chaoneng.ilooknews.R;
import com.chaoneng.ilooknews.module.user.data.NotifyBean;
import com.chaoneng.ilooknews.widget.image.HeadImageView;

/**
 * Created by magical on 17/8/17.
 * Description : 我的消息 Item
 */

public class NotifyAdapter extends BaseQuickAdapter<NotifyBean, BaseViewHolder> {

  public NotifyAdapter(@LayoutRes int layoutResId) {
    super(layoutResId);
  }

  @Override
  protected void convert(BaseViewHolder helper, NotifyBean item) {

    ((HeadImageView) helper.getView(R.id.iv_avatar)).setHeadImage(AppConstant.TEST_AVATAR);
    helper.setText(R.id.tv_notify_type, item.message_type);
    helper.setText(R.id.tv_notify_msg, item.content);
    helper.setText(R.id.tv_notify_time, item.created_time);
  }
}
