package com.chaoneng.ilooknews.module.home.fragment;

import android.view.View;
import android.widget.ProgressBar;
import butterknife.BindView;
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
  protected void doInit() {

    if (getArguments() != null) {
      mImgSrc = getArguments().getString(AppConstant.PHOTO_DETAIL_IMGSRC);
    }
    initPhotoView();
  }

  @Override
  protected boolean isNeedShowLoadingView() {
    return false;
  }

  @Override
  protected int getLayoutName() {
    return R.layout.fragment_news_photo_detail;
  }

  private void initPhotoView() {
    ImageLoader.loadImage(mImgSrc, photoView);
    progressBar.setVisibility(View.GONE);
  }
}
