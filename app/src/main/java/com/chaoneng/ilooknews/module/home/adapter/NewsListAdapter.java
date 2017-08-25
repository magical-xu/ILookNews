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
 * Description :
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
    addItemType(NewsListBean.SINGLE_IMG, R.layout.item_news_single_img);
    addItemType(NewsListBean.TWO_IMG, R.layout.item_news_two_img);
    addItemType(NewsListBean.THREE_IMG, R.layout.item_news_three_img);
    addItemType(NewsListBean.VIDEO, R.layout.item_news_video);
  }

  @Override
  protected void convert(BaseViewHolder helper, NewsListBean item) {

    switch (helper.getItemViewType()) {
      case NewsListBean.TEXT:
        bindText(helper, item);
        break;
      case NewsListBean.SINGLE_IMG:
        bindSingleImg(helper, item);
        break;
      case NewsListBean.TWO_IMG:
        bindTwoImg(helper, item);
        break;
      case NewsListBean.THREE_IMG:
        bindThreeImg(helper, item);
        break;
      case NewsListBean.VIDEO:
        bindVideo(helper, item);
        break;
    }
  }

  private void bindVideo(BaseViewHolder helper, NewsListBean item) {

    helper.setText(R.id.tv_news_title, ILookApplication.getLocalString(R.string.test_text_long));

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

    helper.setText(R.id.tv_news_title, ILookApplication.getLocalString(R.string.test_text_long));
    ImageLoader.loadImage(AppConstant.TEST_AVATAR,
        ((ImageView) helper.getView(R.id.iv_news_right)));
    ImageLoader.loadImage(AppConstant.TEST_AVATAR, ((ImageView) helper.getView(R.id.iv_news_left)));
    ImageLoader.loadImage(AppConstant.TEST_AVATAR,
        ((ImageView) helper.getView(R.id.iv_news_middle)));
  }

  private void bindTwoImg(BaseViewHolder helper, NewsListBean item) {

    helper.setText(R.id.tv_news_title, ILookApplication.getLocalString(R.string.test_text_long));
    ImageLoader.loadImage(AppConstant.TEST_AVATAR,
        ((ImageView) helper.getView(R.id.iv_news_right)));
    ImageLoader.loadImage(AppConstant.TEST_AVATAR, ((ImageView) helper.getView(R.id.iv_news_left)));
  }

  private void bindSingleImg(BaseViewHolder helper, NewsListBean item) {

    helper.setText(R.id.tv_news_title, ILookApplication.getLocalString(R.string.test_text_long));
    ImageLoader.loadImage(AppConstant.TEST_AVATAR,
        ((ImageView) helper.getView(R.id.iv_news_right)));
  }

  private void bindText(BaseViewHolder helper, NewsListBean item) {

    helper.setText(R.id.tv_news_title, ILookApplication.getLocalString(R.string.test_text_long));
  }
}
