package com.chaoneng.ilooknews.module.user.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chaoneng.ilooknews.R;
import com.chaoneng.ilooknews.library.glide.ImageLoader;
import com.chaoneng.ilooknews.module.user.data.BrokeListBean;
import com.chaoneng.ilooknews.util.CompatUtil;
import com.chaoneng.ilooknews.util.IntentHelper;
import com.chaoneng.ilooknews.util.StringHelper;
import com.chaoneng.ilooknews.widget.image.HeadImageView;
import com.google.android.flexbox.FlexboxLayout;
import com.magicalxu.library.blankj.ScreenUtils;
import com.magicalxu.library.blankj.SizeUtils;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by magical on 17/8/24.
 * Description : 爆料列表Item
 */

public class BrokeNewsListAdapter extends BaseQuickAdapter<BrokeListBean, BaseViewHolder> {

    public BrokeNewsListAdapter(@LayoutRes int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, BrokeListBean item) {

        ((HeadImageView) helper.getView(R.id.iv_avatar)).setHeadImage(
                StringHelper.getString(item.userIcon));

        helper.setText(R.id.tv_name, StringHelper.getString(item.nickname));
        helper.setText(R.id.tv_time, StringHelper.getString(item.createtime));
        helper.setText(R.id.tv_title, StringHelper.getString(item.context));

        FlexboxLayout flexboxLayout = helper.getView(R.id.fl_body);
        flexboxLayout.removeAllViews();
        String picUrl = item.picUrl;
        if (TextUtils.isEmpty(picUrl)) {
            flexboxLayout.setVisibility(View.GONE);
            return;
        }

        try {
            JSONArray images = new JSONArray(picUrl);
            int size = images.length();
            final ArrayList<String> list = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {

                String path = (String) images.opt(i);
                list.add(path);
                ImageView img = new ImageView(mContext);
                img.setScaleType(ImageView.ScaleType.CENTER_CROP);
                int wh = calcImgWidth(mContext);
                CompatUtil.resize(flexboxLayout, img, wh, wh);
                img.setPadding(0, 0, SizeUtils.dp2px(8), SizeUtils.dp2px(8));
                ImageLoader.loadImage(path, img);
                final int finalI = i;
                img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onShowImageDetail(finalI, list);
                    }
                });
                flexboxLayout.addView(img);
            }

            flexboxLayout.setVisibility(size == 0 ? View.GONE : View.VISIBLE);
        } catch (JSONException e) {
            flexboxLayout.setVisibility(View.GONE);
            e.printStackTrace();
        }
    }

    private void onShowImageDetail(int position, ArrayList<String> list) {

        IntentHelper.openImageBrowsePage(mContext, position, list);
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
