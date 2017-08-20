package com.chaoneng.ilooknews.module.video.adapter;

import android.support.annotation.LayoutRes;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chaoneng.ilooknews.AppConstant;
import com.chaoneng.ilooknews.R;
import com.chaoneng.ilooknews.module.video.data.VideoComment;
import com.chaoneng.ilooknews.widget.image.HeadImageView;

/**
 * Created by magical on 17/8/20.
 * Description :
 */

public class VideoCommentAdapter extends BaseQuickAdapter<VideoComment, BaseViewHolder> {

  public VideoCommentAdapter(@LayoutRes int layoutResId) {
    super(layoutResId);
  }

  @Override
  protected void convert(BaseViewHolder helper, VideoComment item) {

    HeadImageView headImageView = helper.getView(R.id.iv_avatar);
    headImageView.setHeadImage(AppConstant.TEST_AVATAR);

    helper.setText(R.id.tv_name, "magical")
        .setText(R.id.tv_up, "123")
        .setText(R.id.tv_comment, "不論何時，做你認為最重要的事，不论何时，做你认为最重要的事");
  }
}
