package com.chaoneng.ilooknews.module.home.adapter;

import android.support.annotation.LayoutRes;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chaoneng.ilooknews.R;
import com.chaoneng.ilooknews.data.BaseUser;
import com.chaoneng.ilooknews.widget.image.HeadImageView;

/**
 * Created by magical on 17/8/17.
 * Description : 我的消息 Item
 */

public class NotifyAdapter extends BaseQuickAdapter<BaseUser, BaseViewHolder> {

  public NotifyAdapter(@LayoutRes int layoutResId) {
    super(layoutResId);
  }

  @Override
  protected void convert(BaseViewHolder helper, BaseUser item) {

    ((HeadImageView) helper.getView(R.id.iv_avatar)).setHeadImage(item.avatar);
    helper.setText(R.id.tv_notify_type, item.username);
    helper.setText(R.id.tv_notify_msg, item.introduce);
    helper.setText(R.id.tv_notify_time, "刚刚");
  }
}
