package com.chaoneng.ilooknews.module.home.adapter;

import android.text.TextUtils;
import android.widget.ImageView;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chaoneng.ilooknews.AppConstant;
import com.chaoneng.ilooknews.ILookApplication;
import com.chaoneng.ilooknews.R;
import com.chaoneng.ilooknews.library.glide.GlideApp;
import com.chaoneng.ilooknews.library.glide.ImageLoader;
import com.chaoneng.ilooknews.module.home.data.NewsListBean;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;
import java.util.List;

/**
 * Created by magical on 17/8/20.
 * Description : 新闻列表
 */

public class NewsListAdapter extends BaseMultiItemQuickAdapter<NewsListBean, BaseViewHolder> {

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

        JCVideoPlayerStandard mVideoPlayer = helper.getView(R.id.videoplayer);
        mVideoPlayer.setUp(ILookApplication.getLocalString(R.string.text_video),
                JCVideoPlayerStandard.SCREEN_LAYOUT_NORMAL,
                TextUtils.isEmpty("Description() ：架子鼓") ? "title " + "" : "嫂子啊嫂子");
        GlideApp.with(mContext)
                .load("http://p.qpic.cn/videoyun/0/2449_43b6f696980311e59ed467f22794e792_1/640")
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .dontAnimate()
                .into(mVideoPlayer.thumbImageView);
        //JCVideoPlayer.FULLSCREEN_ORIENTATION = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
        //JCVideoPlayer.NORMAL_ORIENTATION = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
    }

    private void bindThreeImg(BaseViewHolder helper, NewsListBean item) {

        helper.setText(R.id.tv_news_title,
                ILookApplication.getLocalString(R.string.test_text_long));
        ImageLoader.loadImage(AppConstant.TEST_AVATAR,
                ((ImageView) helper.getView(R.id.iv_news_center_image1)));
        ImageLoader.loadImage(AppConstant.TEST_AVATAR,
                ((ImageView) helper.getView(R.id.iv_news_center_image2)));
        ImageLoader.loadImage(AppConstant.TEST_AVATAR,
                ((ImageView) helper.getView(R.id.iv_news_center_image3)));
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

        helper.setText(R.id.tv_news_title,
                ILookApplication.getLocalString(R.string.test_text_long));
        ImageLoader.loadImage(AppConstant.TEST_AVATAR,
                ((ImageView) helper.getView(R.id.iv_news_right)));
    }

    private void bindText(BaseViewHolder helper, NewsListBean item) {

        helper.setText(R.id.tv_news_title, item.title);
        helper.setText(R.id.id_bottom_publisher, item.nickname);
        helper.setText(R.id.id_bottom_comment_count,
                String.format(mContext.getString(R.string.place_comment),
                        String.valueOf(item.commentCount)));
        helper.setText(R.id.id_bottom_time, item.createTime);
    }
}
