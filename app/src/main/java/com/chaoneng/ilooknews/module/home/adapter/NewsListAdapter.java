package com.chaoneng.ilooknews.module.home.adapter;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chaoneng.ilooknews.AppConstant;
import com.chaoneng.ilooknews.ILookApplication;
import com.chaoneng.ilooknews.R;
import com.chaoneng.ilooknews.library.glide.ImageLoader;
import com.chaoneng.ilooknews.library.gsyvideoplayer.VideoHelper;
import com.chaoneng.ilooknews.module.home.data.NewsListBean;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;
import java.util.List;

/**
 * Created by magical on 17/8/20.
 * Description : 新闻列表
 */

public class NewsListAdapter extends BaseMultiItemQuickAdapter<NewsListBean, BaseViewHolder> {

    public static String ONE_IMAGE = "3";
    public static String THREE_IMAGE = "2";
    public static String NONE_IMAGE = "1";

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public NewsListAdapter(List<NewsListBean> data) {
        super(data);
        addItemType(NewsListBean.TEXT, R.layout.item_news_text);
        addItemType(NewsListBean.IMAGE, R.layout.item_news_text);
        addItemType(NewsListBean.AD, R.layout.item_news_text);
        addItemType(NewsListBean.HTML, R.layout.item_news_text);
        //addItemType(NewsListBean.IMAGE, R.layout.item_news_single_img);
        //addItemType(NewsListBean.TWO_IMG, R.layout.item_news_two_img);
        //addItemType(NewsListBean.THREE_IMG, R.layout.item_news_three_img);
        addItemType(NewsListBean.VIDEO, R.layout.item_news_video);
    }

    @Override
    protected void convert(BaseViewHolder helper, NewsListBean item) {

        switch (helper.getItemViewType()) {
            case NewsListBean.TEXT:
            case NewsListBean.IMAGE:
            case NewsListBean.AD:
            case NewsListBean.HTML:
                bindText(helper, item);
                break;
            case NewsListBean.VIDEO:
                bindVideo(helper, item);
                break;
        }
    }

    private void bindVideo(BaseViewHolder helper, NewsListBean item) {

        helper.setText(R.id.tv_news_title, item.title);
        helper.setText(R.id.id_bottom_publisher, item.nickname);
        helper.setText(R.id.id_bottom_comment_count,
                String.format(mContext.getString(R.string.place_comment),
                        String.valueOf(item.commentCount)));
        helper.setText(R.id.id_bottom_time, item.createTime);

        StandardGSYVideoPlayer mVideoPlayer = helper.getView(R.id.videoplayer);
        VideoHelper.initPlayer(mContext, mVideoPlayer, item.title, null);
    }

    private void bindThreeImg(BaseViewHolder helper, NewsListBean item) {

        helper.getView(R.id.ll_three_container).setVisibility(View.VISIBLE);
        helper.getView(R.id.iv_news_right).setVisibility(View.GONE);

        View view1 = helper.getView(R.id.iv_news_center_image1);
        View view2 = helper.getView(R.id.iv_news_center_image2);
        View view3 = helper.getView(R.id.iv_news_center_image3);

        List<String> list = item.coverpic;
        if (null != list && list.size() >= 1) {
            String imageUrl = list.get(0);
            ImageLoader.loadImage(imageUrl, ((ImageView) view1));

            if (list.size() >= 2) {
                String imageUrl2 = list.get(1);
                ImageLoader.loadImage(imageUrl2, ((ImageView) view2));
            }

            if (list.size() >= 3) {
                String imageUrl3 = list.get(2);
                ImageLoader.loadImage(imageUrl3, ((ImageView) view3));
            }
        }
    }

    private void bindTwoImg(BaseViewHolder helper, NewsListBean item) {

        helper.setText(R.id.tv_news_title,
                ILookApplication.getLocalString(R.string.test_text_long));
        ImageLoader.loadImage(AppConstant.TEST_AVATAR,
                ((ImageView) helper.getView(R.id.iv_news_right)));
        ImageLoader.loadImage(AppConstant.TEST_AVATAR,
                ((ImageView) helper.getView(R.id.iv_news_left)));
    }

    private void bindSingleImg(BaseViewHolder helper, NewsListBean item) {

        helper.getView(R.id.ll_three_container).setVisibility(View.GONE);
        View view = helper.getView(R.id.iv_news_right);
        view.setVisibility(View.VISIBLE);

        List<String> list = item.coverpic;
        if (null != list && list.size() > 1) {
            String imageUrl = list.get(0);
            ImageLoader.loadImage(imageUrl, ((ImageView) view));
        }
    }

    private void bindText(BaseViewHolder helper, NewsListBean item) {

        helper.setText(R.id.tv_news_title, item.title);
        helper.setText(R.id.id_bottom_publisher, item.nickname);
        helper.setText(R.id.id_bottom_comment_count,
                String.format(mContext.getString(R.string.place_comment),
                        String.valueOf(item.commentCount)));
        helper.setText(R.id.id_bottom_time, item.createTime);

        String picStyle = item.picStyle;
        if (TextUtils.equals(picStyle, ONE_IMAGE)) {
            bindSingleImg(helper, item);
        } else if (TextUtils.equals(picStyle, THREE_IMAGE)) {
            bindThreeImg(helper, item);
        } else {

            double random = Math.random();
            if (random > 0.5) {
                bindSingleImg(helper, item);
            } else {
                bindThreeImg(helper, item);
            }
        }
    }
}
