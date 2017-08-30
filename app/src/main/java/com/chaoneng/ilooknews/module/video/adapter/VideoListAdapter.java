package com.chaoneng.ilooknews.module.video.adapter;

import android.support.annotation.LayoutRes;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chaoneng.ilooknews.AppConstant;
import com.chaoneng.ilooknews.R;
import com.chaoneng.ilooknews.library.glide.ImageLoader;
import com.chaoneng.ilooknews.library.gsyvideoplayer.listener.SampleListener;
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
    //JCVideoPlayerStandard videoPlayer = helper.getView(R.id.video_player);
    //videoPlayer.setUp(ILookApplication.getLocalString(R.string.text_video),
    //    JCVideoPlayerStandard.SCREEN_LAYOUT_LIST,
    //    TextUtils.isEmpty("Description() ：架子鼓") ? "title " + "" : "嫂子真会玩");
    //GlideApp.with(mContext)
    //    .load("http://p.qpic.cn/videoyun/0/2449_43b6f696980311e59ed467f22794e792_1/640")
    //    .diskCacheStrategy(DiskCacheStrategy.ALL)
    //    .centerCrop()
    //    .dontAnimate()
    //    .into(videoPlayer.thumbImageView);

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

    gsyVideoPlayer.setIsTouchWiget(false);

    gsyVideoPlayer.setThumbImageView(imageView);

    //默认缓存路径
    gsyVideoPlayer.setUp(AppConstant.TEST_VIDEO_URL, true, null, "这是title");

    //增加title
    gsyVideoPlayer.getTitleTextView().setVisibility(View.GONE);

    //设置返回键
    gsyVideoPlayer.getBackButton().setVisibility(View.GONE);

    //设置全屏按键功能
    gsyVideoPlayer.getFullscreenButton().setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        resolveFullBtn(gsyVideoPlayer);
      }
    });
    gsyVideoPlayer.setRotateViewAuto(true);
    gsyVideoPlayer.setLockLand(true);
    gsyVideoPlayer.setPlayTag(TAG);
    gsyVideoPlayer.setShowFullAnimation(true);
    //循环
    //gsyVideoPlayer.setLooping(true);
    gsyVideoPlayer.setNeedLockFull(true);

    //gsyVideoPlayer.setSpeed(2);

    gsyVideoPlayer.setPlayPosition(helper.getAdapterPosition());

    gsyVideoPlayer.setStandardVideoAllCallBack(new SampleListener() {
      @Override
      public void onPrepared(String url, Object... objects) {
        super.onPrepared(url, objects);
        //if (!gsyVideoPlayer.isIfCurrentIsFullscreen()) {
        //  //静音
        //  GSYVideoManager.instance().setNeedMute(true);
        //}
      }

      @Override
      public void onQuitFullscreen(String url, Object... objects) {
        super.onQuitFullscreen(url, objects);
        //全屏不静音
        //GSYVideoManager.instance().setNeedMute(true);
      }

      @Override
      public void onEnterFullscreen(String url, Object... objects) {
        super.onEnterFullscreen(url, objects);
        //GSYVideoManager.instance().setNeedMute(false);
      }
    });
  }

  /**
   * 全屏幕按键处理
   */
  private void resolveFullBtn(final StandardGSYVideoPlayer standardGSYVideoPlayer) {
    standardGSYVideoPlayer.startWindowFullscreen(mContext, true, true);
  }
}
