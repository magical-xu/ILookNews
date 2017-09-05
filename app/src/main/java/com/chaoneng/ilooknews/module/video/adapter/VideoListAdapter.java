package com.chaoneng.ilooknews.module.video.adapter;

import android.support.annotation.LayoutRes;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chaoneng.ilooknews.AppConstant;
import com.chaoneng.ilooknews.R;
import com.chaoneng.ilooknews.library.glide.ImageLoader;
import com.chaoneng.ilooknews.library.gsyvideoplayer.VideoHelper;
import com.chaoneng.ilooknews.module.video.data.VideoListBean;
import com.chaoneng.ilooknews.widget.image.HeadImageView;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;

/**
 * Created by magical on 17/8/19.
 * Description : 视频列表适配器
 */

public class VideoListAdapter extends BaseQuickAdapter<VideoListBean, BaseViewHolder> {

  public static final String TAG = "VideoListAdapter";

  private ImageView imageView;

  public VideoListAdapter(@LayoutRes int layoutResId) {
    super(layoutResId);
  }

  @Override
  public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    imageView = new ImageView(parent.getContext());
    return super.onCreateViewHolder(parent, viewType);
  }

  @Override
  protected void convert(BaseViewHolder helper, VideoListBean item) {

    configVideo(helper, item);

    ((HeadImageView) helper.getView(R.id.iv_avatar)).setHeadImage(AppConstant.TEST_AVATAR);

    helper.setText(R.id.tv_author, "magical");

    helper.setText(R.id.tv_play_time, "1111次播放").setText(R.id.tv_comments, "123");

    helper.addOnClickListener(R.id.id_focus_plus).addOnClickListener(R.id.tv_comments);
  }

  private void configVideo(BaseViewHolder helper, VideoListBean item) {

    final StandardGSYVideoPlayer gsyVideoPlayer = helper.getView(R.id.video_player);

    //增加封面
    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
    ImageLoader.loadImage(AppConstant.TEST_THUMB_URL, imageView);
    if (imageView.getParent() != null) {
      ViewGroup viewGroup = (ViewGroup) imageView.getParent();
      viewGroup.removeView(imageView);
    }
    gsyVideoPlayer.setThumbImageView(imageView);
    gsyVideoPlayer.setPlayPosition(helper.getAdapterPosition());

    VideoHelper.initPlayer(mContext, gsyVideoPlayer, "嫂子真会玩", null);
  }
}
