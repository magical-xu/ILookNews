package com.aktt.news.module.user.adapter;

import android.support.annotation.LayoutRes;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.aktt.news.R;
import com.aktt.news.data.NewsInfo;
import com.aktt.news.library.glide.ImageLoader;
import com.aktt.news.util.StringHelper;
import com.aktt.news.widget.image.HeadImageView;
import java.util.List;

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

        ((HeadImageView) helper.getView(R.id.iv_avatar)).setHeadImage(
                StringHelper.getString(item.userIcon));
        helper.setText(R.id.tv_name, StringHelper.getString(item.nickname));
        helper.setText(R.id.tv_time, StringHelper.getString(item.createTime));
        helper.setText(R.id.tv_title, StringHelper.getString(item.title));
        helper.setText(R.id.tv_refer, StringHelper.getString(item.content));

        helper.setText(R.id.tv_up, String.valueOf(item.likeCount));
        helper.setText(R.id.tv_comment, String.valueOf(item.commentCount));

        helper.addOnClickListener(R.id.tv_share);

        ImageView refer = helper.getView(R.id.iv_refer);
        List<String> coverPic = item.coverpic;
        if (null != coverPic && coverPic.size() > 0) {
            refer.setVisibility(View.VISIBLE);
            String url = coverPic.get(0);
            if (TextUtils.isEmpty(url)) {
                refer.setVisibility(View.GONE);
                return;
            }

            if (url.startsWith("\"") || url.startsWith("[")) {
                //不规则情况
                String[] split = url.split("\"");
                String rightUrl = "";
                for (int i = 0; i < split.length; i++) {
                    String one = split[i];
                    if (one.length() > 3) {
                        rightUrl = one;
                        break;
                    }
                }
                ImageLoader.loadImage(rightUrl, refer);
            } else {
                ImageLoader.loadImage(url, refer);
            }
        } else {
            refer.setVisibility(View.GONE);
        }
    }
}
