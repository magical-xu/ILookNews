package com.chaoneng.ilooknews.module.video.adapter;

import android.support.annotation.LayoutRes;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chaoneng.ilooknews.AppConstant;
import com.chaoneng.ilooknews.R;
import com.chaoneng.ilooknews.library.glide.ImageLoader;
import com.chaoneng.ilooknews.library.gsyvideoplayer.VideoHelper;
import com.chaoneng.ilooknews.module.home.data.NewsListBean;
import com.chaoneng.ilooknews.util.StringHelper;
import com.chaoneng.ilooknews.widget.image.HeadImageView;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;
import java.util.List;

/**
 * Created by magical on 17/8/19.
 * Description : 视频列表适配器
 */

public class VideoListAdapter extends BaseQuickAdapter<NewsListBean, BaseViewHolder> {

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
    protected void convert(BaseViewHolder helper, NewsListBean item) {

        if (null == item) {
            return;
        }

        configVideo(helper, item);

        ((HeadImageView) helper.getView(R.id.iv_avatar)).setHeadImage(
                StringHelper.getString(item.userIcon));

        helper.setText(R.id.tv_author, StringHelper.getString(item.nickname));

        helper.setText(R.id.tv_play_time, "1111次播放")
                .setText(R.id.tv_comments, String.valueOf(item.commentCount));

        helper.addOnClickListener(R.id.id_focus_plus).addOnClickListener(R.id.tv_comments);
    }

    private void configVideo(BaseViewHolder helper, NewsListBean item) {

        final StandardGSYVideoPlayer gsyVideoPlayer = helper.getView(R.id.video_player);

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
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        ImageLoader.loadImage(coverPicUrl, imageView);
        if (imageView.getParent() != null) {
            ViewGroup viewGroup = (ViewGroup) imageView.getParent();
            viewGroup.removeView(imageView);
        }
        gsyVideoPlayer.setThumbImageView(imageView);
        gsyVideoPlayer.setPlayPosition(helper.getAdapterPosition());

        VideoHelper.initPlayer(mContext, gsyVideoPlayer, url, StringHelper.getString(item.title),
                null);
    }
}
