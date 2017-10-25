package com.aktt.news.module.home.adapter;

import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.aktt.news.AppConstant;
import com.aktt.news.ILookApplication;
import com.aktt.news.R;
import com.aktt.news.library.glide.ImageLoader;
import com.aktt.news.library.gsyvideoplayer.VideoHelper;
import com.aktt.news.module.home.data.NewsListBean;
import com.aktt.news.util.StringHelper;
import com.magicalxu.library.blankj.SPUtils;
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
        checkTextSize(((TextView) helper.getView(R.id.tv_news_title)));
        helper.setText(R.id.id_bottom_publisher, item.nickname);
        helper.setText(R.id.id_bottom_comment_count,
                String.format(mContext.getString(R.string.place_comment),
                        String.valueOf(item.commentCount)));
        helper.setText(R.id.id_bottom_time, item.createTime);

        StandardGSYVideoPlayer mVideoPlayer = helper.getView(R.id.videoplayer);
        String url = item.videoUrl;

        //增加封面
        List<String> coverList = item.coverpic;
        String coverPicUrl;
        if (coverList != null && coverList.size() > 0) {
            coverPicUrl = coverList.get(0);
            if (TextUtils.isEmpty(coverPicUrl)) {
                coverPicUrl = item.videoUrl + AppConstant.VIDEO_SUFFIX;
            }
        } else {
            coverPicUrl = item.videoUrl + AppConstant.VIDEO_SUFFIX;
        }

        ImageView imageView = new ImageView(mContext);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        ImageLoader.loadImage(coverPicUrl, imageView);
        if (imageView.getParent() != null) {
            ViewGroup viewGroup = (ViewGroup) imageView.getParent();
            viewGroup.removeView(imageView);
        }
        mVideoPlayer.setThumbImageView(imageView);
        mVideoPlayer.setPlayPosition(helper.getAdapterPosition());

        if (!TextUtils.isEmpty(url)) {
            VideoHelper.initPlayer(mContext, mVideoPlayer, url, StringHelper.getString(item.title),
                    null);
        }
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
        ImageLoader.loadImage("url", ((ImageView) helper.getView(R.id.iv_news_right)));
        ImageLoader.loadImage("url", ((ImageView) helper.getView(R.id.iv_news_left)));
    }

    private void bindSingleImg(BaseViewHolder helper, NewsListBean item) {

        helper.getView(R.id.ll_three_container).setVisibility(View.GONE);
        View view = helper.getView(R.id.iv_news_right);
        view.setVisibility(View.VISIBLE);

        List<String> list = item.coverpic;
        if (null != list && list.size() >= 1) {
            String imageUrl = list.get(0);
            ImageLoader.loadImage(imageUrl, ((ImageView) view));
        }
    }

    private void bindText(BaseViewHolder helper, NewsListBean item) {

        TextView tvTitle = helper.getView(R.id.tv_news_title);
        tvTitle.setText(StringHelper.getString(item.title));
        checkTextSize(tvTitle);

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
        } else if (TextUtils.equals(picStyle, NONE_IMAGE)) {
            helper.getView(R.id.ll_three_container).setVisibility(View.GONE);
            helper.getView(R.id.iv_news_right).setVisibility(View.GONE);
        }
    }

    private void checkTextSize(TextView textView) {

        int size = SPUtils.getInstance().getInt(AppConstant.NEWS_TEXT_SIZE, 1);
        // 14sp 17sp 20sp
        if (size == 0) {
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        } else if (size == 1) {
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
        } else {
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        }
    }
}
