package com.chaoneng.ilooknews.module.video.adapter;

import android.support.annotation.LayoutRes;
import android.text.TextUtils;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chaoneng.ilooknews.AppConstant;
import com.chaoneng.ilooknews.ILookApplication;
import com.chaoneng.ilooknews.R;
import com.chaoneng.ilooknews.library.glide.GlideApp;
import com.chaoneng.ilooknews.module.video.data.VideoListBean;
import com.chaoneng.ilooknews.widget.image.HeadImageView;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;

/**
 * Created by magical on 17/8/19.
 * Description : 视频列表适配器
 */

public class VideoListAdapter extends BaseQuickAdapter<VideoListBean, BaseViewHolder> {

  public VideoListAdapter(@LayoutRes int layoutResId) {
    super(layoutResId);
  }

  @Override
  protected void convert(BaseViewHolder helper, VideoListBean item) {

    JCVideoPlayerStandard videoPlayer = helper.getView(R.id.video_player);
    videoPlayer.setUp(ILookApplication.getLocalString(R.string.text_video),
        JCVideoPlayerStandard.SCREEN_LAYOUT_LIST,
        TextUtils.isEmpty("Description() ：架子鼓") ? "title " + "" : "嫂子真会玩");
    GlideApp.with(mContext)
        .load("http://p.qpic.cn/videoyun/0/2449_43b6f696980311e59ed467f22794e792_1/640")
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .centerCrop()
        .dontAnimate()
        .into(videoPlayer.thumbImageView);

    ((HeadImageView) helper.getView(R.id.iv_avatar)).setHeadImage(AppConstant.TEST_AVATAR);

    helper.setText(R.id.tv_author, "magical");

    helper.setText(R.id.tv_play_time, "1111次播放").setText(R.id.tv_comments, "123");

    helper.addOnClickListener(R.id.id_focus_plus).addOnClickListener(R.id.tv_comments);
  }
}
