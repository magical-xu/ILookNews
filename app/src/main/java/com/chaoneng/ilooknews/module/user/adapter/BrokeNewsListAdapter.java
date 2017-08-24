package com.chaoneng.ilooknews.module.user.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.widget.ImageView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chaoneng.ilooknews.AppConstant;
import com.chaoneng.ilooknews.ILookApplication;
import com.chaoneng.ilooknews.R;
import com.chaoneng.ilooknews.library.glide.ImageLoader;
import com.chaoneng.ilooknews.module.user.data.BrokeNewsBean;
import com.chaoneng.ilooknews.util.CompatUtil;
import com.chaoneng.ilooknews.widget.image.HeadImageView;
import com.google.android.flexbox.FlexboxLayout;
import com.magicalxu.library.blankj.ScreenUtils;
import com.magicalxu.library.blankj.SizeUtils;

/**
 * Created by magical on 17/8/24.
 * Description :
 */

public class BrokeNewsListAdapter extends BaseQuickAdapter<BrokeNewsBean, BaseViewHolder> {

  public BrokeNewsListAdapter(@LayoutRes int layoutResId) {
    super(layoutResId);
  }

  @Override
  protected void convert(BaseViewHolder helper, BrokeNewsBean item) {

    ((HeadImageView) helper.getView(R.id.iv_avatar)).setHeadImage(AppConstant.TEST_AVATAR);

    helper.setText(R.id.tv_name, "超能工作室");
    helper.setText(R.id.tv_time, "04-25 23:00");
    helper.setText(R.id.tv_title, ILookApplication.getLocalString(R.string.test_text_long));
    FlexboxLayout flexboxLayout = helper.getView(R.id.fl_body);

    flexboxLayout.removeAllViews();
    for (int i = 0; i < 9; i++) {
      ImageView img = new ImageView(mContext);
      int wh = calcImgWidth(mContext);
      CompatUtil.resize(flexboxLayout, img, wh, wh);
      img.setPadding(0, 0, SizeUtils.dp2px(8), SizeUtils.dp2px(8));
      ImageLoader.loadImage(AppConstant.TEST_AVATAR, img);
      flexboxLayout.addView(img);
    }
  }

  private int calcImgWidth(Context context) {
    final int columnCount = 3;

    int containerWidth =
        ScreenUtils.getScreenWidth() - CompatUtil.getTotal(context, R.dimen.padding_normal,
            R.dimen.padding_normal);
    int imgWidth = SizeUtils.dp2px(80);

    int remain = containerWidth - columnCount * imgWidth;
    imgWidth += (remain / columnCount);
    return imgWidth;
  }
}
