package com.chaoneng.ilooknews.module.home.fragment;

import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ProgressBar;
import butterknife.BindView;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.chaoneng.ilooknews.AppConstant;
import com.chaoneng.ilooknews.R;
import com.chaoneng.ilooknews.base.BaseFragment;
import com.chaoneng.ilooknews.library.glide.ImageLoader;
import uk.co.senab.photoview.PhotoView;

/**
 * des:图文新闻详情
 * Created by xsf
 * on 2016.09.9:57
 */
public class PhotoDetailFragment extends BaseFragment {

    @BindView(R.id.photo_view) PhotoView photoView;
    @BindView(R.id.progress_bar) ProgressBar progressBar;
    private String mImgSrc;

    @Override
    protected void beginLoadData() {
    }

    @Override
    protected void lazyLoad() {
        super.lazyLoad();

        ImageLoader.loadImage(mImgSrc, photoView, new RequestListener<Bitmap>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model,
                    Target<Bitmap> target, boolean isFirstResource) {
                progressBar.setVisibility(View.GONE);
                return false;
            }

            @Override
            public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target,
                    DataSource dataSource, boolean isFirstResource) {
                progressBar.setVisibility(View.GONE);
                return false;
            }
        });
    }

    @Override
    protected void doInit() {

        if (getArguments() != null) {
            mImgSrc = getArguments().getString(AppConstant.PHOTO_DETAIL_IMGSRC);
        }
    }

    @Override
    protected int getLayoutName() {
        return R.layout.fragment_news_photo_detail;
    }
}
