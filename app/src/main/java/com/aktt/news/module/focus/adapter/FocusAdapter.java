package com.aktt.news.module.focus.adapter;

import android.support.annotation.LayoutRes;
import com.aktt.news.R;
import com.aktt.news.module.focus.data.FocusBean;
import com.aktt.news.util.StringHelper;
import com.aktt.news.util.TimeUtil;
import com.aktt.news.widget.image.HeadImageView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

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
                .setText(R.id.tv_time, TimeUtil.getFormatString(item.thisTime))
                .setText(R.id.tv_title_one, StringHelper.getString(item.lastOneNewstitle))
                .setText(R.id.tv_title_two, StringHelper.getString(item.lastTwoNewsTitle));

        helper.addOnClickListener(R.id.iv_avatar);
    }


}
