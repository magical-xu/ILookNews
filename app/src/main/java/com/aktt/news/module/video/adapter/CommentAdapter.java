package com.aktt.news.module.video.adapter;

import android.support.annotation.LayoutRes;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.aktt.news.R;
import com.aktt.news.data.CommentBean;
import com.aktt.news.widget.image.HeadImageView;

/**
 * Created by magical on 17/8/20.
 * Description : 通用评论 Item
 */

public class CommentAdapter extends BaseQuickAdapter<CommentBean, BaseViewHolder> {

    private boolean isSubPage;  //是否为次级评论

    public CommentAdapter(boolean subPage, @LayoutRes int layoutResId) {
        super(layoutResId);
        isSubPage = subPage;
    }

    @Override
    protected void convert(BaseViewHolder helper, CommentBean item) {

        if (null == item) {
            return;
        }

        HeadImageView headImageView = helper.getView(R.id.iv_avatar);
        headImageView.setHeadImage(item.icon);

        helper.setText(R.id.tv_name, getOrDefault(item.nickname))
                .setText(R.id.tv_up, String.valueOf(item.careCount))
                .setText(R.id.tv_comment, item.text)
                .setText(R.id.id_timestamp, item.createDate)
                .addOnClickListener(R.id.id_comment_count)
                .addOnClickListener(R.id.tv_up)
                .addOnClickListener(R.id.iv_avatar);

        TextView tvReply = helper.getView(R.id.id_comment_count);
        if (isSubPage) {
            tvReply.setVisibility(View.GONE);
        } else {
            tvReply.setVisibility(View.VISIBLE);
            int commentCount = item.commentCount;
            String reply;
            //if (0 == commentCount) {
            //    reply = "";
            //} else {
            reply = String.valueOf(commentCount);
            //}
            String format = String.format(mContext.getString(R.string.place_reply), reply);
            tvReply.setText(format);
        }
    }

    public String getOrDefault(String raw) {
        return TextUtils.isEmpty(raw) ? "游客" : raw;
    }
}

