package com.chaoneng.ilooknews.module.user.adapter;

import android.support.annotation.LayoutRes;
import android.widget.ImageView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chaoneng.ilooknews.AppConstant;
import com.chaoneng.ilooknews.R;
import com.chaoneng.ilooknews.data.NewsInfo;
import com.chaoneng.ilooknews.library.glide.ImageLoader;
import com.chaoneng.ilooknews.util.StringHelper;
import com.chaoneng.ilooknews.widget.image.HeadImageView;

/**
 * Created by magical on 17/8/24.
 * Description : 动态列表Item
 */

public class StateAdapter extends BaseQuickAdapter<NewsInfo, BaseViewHolder> {

  public StateAdapter(@LayoutRes int layoutResId) {
    super(layoutResId);
  }

  @Override
  protected void convert(BaseViewHolder helper, NewsInfo item) {

    ((HeadImageView) helper.getView(R.id.iv_avatar)).setHeadImage(StringHelper.getString(item.userIcon));
    helper.setText(R.id.tv_name, StringHelper.getString(item.nickname));
    helper.setText(R.id.tv_time, StringHelper.getString(item.createTime));
    helper.setText(R.id.tv_title, StringHelper.getString(item.title));
    ImageLoader.loadImage(AppConstant.TEST_AVATAR, (ImageView) helper.getView(R.id.iv_refer));
    helper.setText(R.id.tv_refer, StringHelper.getString(item.content));

    helper.setText(R.id.tv_up, String.valueOf(item.like_count));
    helper.setText(R.id.tv_comment, String.valueOf(item.commentCount));
  }
}
