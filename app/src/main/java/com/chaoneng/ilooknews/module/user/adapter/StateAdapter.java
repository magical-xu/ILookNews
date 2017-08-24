package com.chaoneng.ilooknews.module.user.adapter;

import android.support.annotation.LayoutRes;
import android.widget.ImageView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chaoneng.ilooknews.AppConstant;
import com.chaoneng.ilooknews.ILookApplication;
import com.chaoneng.ilooknews.R;
import com.chaoneng.ilooknews.library.glide.ImageLoader;
import com.chaoneng.ilooknews.module.user.data.UserStateBean;
import com.chaoneng.ilooknews.widget.image.HeadImageView;

/**
 * Created by magical on 17/8/24.
 * Description :
 */

public class StateAdapter extends BaseQuickAdapter<UserStateBean, BaseViewHolder> {

  public StateAdapter(@LayoutRes int layoutResId) {
    super(layoutResId);
  }

  @Override
  protected void convert(BaseViewHolder helper, UserStateBean item) {

    ((HeadImageView) helper.getView(R.id.iv_avatar)).setHeadImage(AppConstant.TEST_AVATAR);
    helper.setText(R.id.tv_name, "magical");
    helper.setText(R.id.tv_time, "04-25 23:00");
    helper.setText(R.id.tv_title, ILookApplication.getLocalString(R.string.test_text_long));
    ImageLoader.loadImage(AppConstant.TEST_AVATAR, (ImageView) helper.getView(R.id.iv_refer));
    helper.setText(R.id.tv_refer, ILookApplication.getLocalString(R.string.test_text_long));

    helper.setText(R.id.tv_up, "123");
    helper.setText(R.id.tv_comment, "456");
  }
}
