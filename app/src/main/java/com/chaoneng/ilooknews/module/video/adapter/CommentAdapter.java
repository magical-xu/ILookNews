package com.chaoneng.ilooknews.module.video.adapter;

import android.support.annotation.LayoutRes;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chaoneng.ilooknews.R;
import com.chaoneng.ilooknews.data.CommentBean;
import com.chaoneng.ilooknews.widget.image.HeadImageView;

/**
 * Created by magical on 17/8/20.
 * Description : 通用评论 Item
 */

public class CommentAdapter extends BaseQuickAdapter<CommentBean, BaseViewHolder> {

  public CommentAdapter(@LayoutRes int layoutResId) {
    super(layoutResId);
  }

  @Override
  protected void convert(BaseViewHolder helper, CommentBean item) {

    if (null == item) {
      return;
    }

    HeadImageView headImageView = helper.getView(R.id.iv_avatar);
    headImageView.setHeadImage(item.icon);

    helper.setText(R.id.tv_name, item.nickname)
        .setText(R.id.tv_up, String.valueOf(item.careCount))
        .setText(R.id.tv_comment, item.text)
        .setText(R.id.id_comment_count, String.format(mContext.getString(R.string.place_reply),
            String.valueOf(item.commentCount)))
        .setText(R.id.id_timestamp, item.createDate)
        .addOnClickListener(R.id.id_comment_count)
        .addOnClickListener(R.id.tv_up);
  }
}
