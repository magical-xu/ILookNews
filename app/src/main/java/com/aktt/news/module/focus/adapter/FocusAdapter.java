package com.aktt.news.module.focus.adapter;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import com.aktt.news.R;
import com.aktt.news.module.focus.data.FocusBean;
import com.aktt.news.util.StringHelper;
import com.aktt.news.widget.image.HeadImageView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import java.sql.Timestamp;

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
                .setText(R.id.tv_time, getFormatString(item.thisTime))
                .setText(R.id.tv_title_one, StringHelper.getString(item.lastOneNewstitle))
                .setText(R.id.tv_title_two, StringHelper.getString(item.lastTwoNewsTitle));

        helper.addOnClickListener(R.id.iv_avatar);
    }

    private long string2Timestamp(@NonNull String formatDate) {

        Timestamp ts;
        try {
            ts = Timestamp.valueOf(formatDate);
            return ts.getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private String getFormatString(String raw) {

        if (TextUtils.isEmpty(raw)) {
            return "";
        }

        long rawTimeStamp = string2Timestamp(raw);
        long nowTimeStamp = System.currentTimeMillis();
        Log.d("magical", "raw : " + rawTimeStamp);
        Log.d("magical", "now : " + nowTimeStamp);

        long diff = nowTimeStamp - rawTimeStamp;
        long secondDiff = diff / 1000;
        if (secondDiff < 60) {
            return "刚刚";
        }

        long minute = secondDiff / 60;
        if (minute < 60) {
            return String.format("%s分钟前", String.valueOf(minute));
        }

        long hour = minute / 60;
        if (hour < 24) {
            return String.format("%s小时前", String.valueOf(hour));
        }

        long day = hour / 24;
        if (day < 30) {
            return String.format("%s天前", String.valueOf(day));
        }

        long month = day / 30;
        if (month < 12) {
            return String.format("%s月前", String.valueOf(month));
        }

        long year = month / 12;
        return String.format("%s年前", String.valueOf(year));
    }
}
