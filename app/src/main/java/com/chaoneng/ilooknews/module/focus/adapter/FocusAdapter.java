package com.chaoneng.ilooknews.module.focus.adapter;

import android.support.annotation.LayoutRes;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chaoneng.ilooknews.R;
import com.chaoneng.ilooknews.module.focus.data.FocusBean;
import com.chaoneng.ilooknews.util.StringHelper;
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

        ((HeadImageView) helper.getView(R.id.iv_avatar)).setHeadImage(
                StringHelper.getString(item.icon));

        helper.setText(R.id.tv_name, StringHelper.getString(item.nickname))
                .setText(R.id.tv_intro, item.introduce)
                .setText(R.id.tv_time, item.lastTitleVideo)
                .setText(R.id.tv_title_one, "第一条 content 后台没有对应字段 不知道填啥")
                .setText(R.id.tv_title_two, "第二条 content 后台没有对应字段 不知道填啥");

        helper.addOnClickListener(R.id.iv_avatar);
    }
}
