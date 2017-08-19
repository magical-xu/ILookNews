package com.chaoneng.ilooknews.module.focus.adapter;

import android.support.annotation.LayoutRes;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chaoneng.ilooknews.R;
import com.chaoneng.ilooknews.module.focus.data.FocusBean;
import com.chaoneng.ilooknews.widget.image.HeadImageView;

/**
 * Created by magical on 17/8/19.
 * Description : 主页 - 关注 - 适配器
 */

public class FocusAdapter extends BaseQuickAdapter<FocusBean, BaseViewHolder> {

  public FocusAdapter(@LayoutRes int layoutResId) {
    super(layoutResId);
  }

  @Override
  protected void convert(BaseViewHolder helper, FocusBean item) {

    ((HeadImageView) helper.getView(R.id.iv_avatar)).setHeadImage(item.avatar);
    helper.setText(R.id.tv_name, item.name)
        .setText(R.id.tv_intro, item.intro)
        .setText(R.id.tv_time, item.time)
        .setText(R.id.tv_title_one, item.content)
        .setText(R.id.tv_title_two, item.content);
  }
}
