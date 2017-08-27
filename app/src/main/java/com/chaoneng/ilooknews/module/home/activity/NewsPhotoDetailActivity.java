package com.chaoneng.ilooknews.module.home.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import com.chaoneng.ilooknews.AppConstant;
import com.chaoneng.ilooknews.R;
import com.chaoneng.ilooknews.base.BaseActivity;
import com.chaoneng.ilooknews.module.home.data.NewsPhotoDetail;
import com.chaoneng.ilooknews.module.home.fragment.PhotoDetailFragment;
import com.chaoneng.ilooknews.widget.ILookTitleBar;
import com.chaoneng.ilooknews.widget.adapter.BaseFragmentAdapter;
import com.chaoneng.ilooknews.widget.adapter.OnPageChangeListener;
import com.chaoneng.ilooknews.widget.viewpager.ViewPagerFixed;
import com.magicalxu.library.blankj.ToastUtils;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by magical on 17/8/25.
 * Description : 图文详情界面
 */

public class NewsPhotoDetailActivity extends BaseActivity {

  @BindView(R.id.view_pager) ViewPagerFixed mViewPager;
  @BindView(R.id.photo_detail_title_tv) TextView mContentTv;
  @BindView(R.id.id_edit) EditText mEditText;
  @BindView(R.id.id_star) ImageView mStarIv;
  @BindView(R.id.id_comment) ImageView mCommentIv;

  private List<Fragment> mPhotoDetailFragmentList = new ArrayList<>();
  private NewsPhotoDetail mNewsPhotoDetail;

  @Override
  public int getLayoutId() {
    return R.layout.activity_news_photo_detail;
  }

  @Override
  protected boolean addTitleBar() {
    return true;
  }

  @Override
  public void handleChildPage(Bundle savedInstanceState) {

    mNewsPhotoDetail = new NewsPhotoDetail();
    mNewsPhotoDetail.setTitle(getString(R.string.test_text_long));
    List<NewsPhotoDetail.Picture> list = new ArrayList<>();
    for (int i = 0; i < 9; i++) {
      NewsPhotoDetail.Picture picture = new NewsPhotoDetail.Picture();
      picture.setImgSrc(AppConstant.TEST_AVATAR);
      picture.setTitle(AppConstant.TEST_SIGN);
      list.add(picture);
    }
    mNewsPhotoDetail.setPictures(list);
    checkTitle();
    createFragment(mNewsPhotoDetail);
    initViewPager();
    setPhotoDetailTitle(0);
  }

  private void checkTitle() {

    mTitleBar.setTitleBg(android.R.color.black)
        .setLeftImage(R.drawable.ic_back)
        .setRightImage(R.drawable.ic_more_white)
        .hideDivider()
        .setTitleListener(new ILookTitleBar.TitleCallbackAdapter() {
          @Override
          public void onClickLeft(View view) {
            super.onClickLeft(view);
            finish();
          }

          @Override
          public void onClickRightImage(View view) {
            super.onClickRightImage(view);
            ToastUtils.showShort("分享");
          }
        });
  }

  private void initViewPager() {
    BaseFragmentAdapter photoPagerAdapter =
        new BaseFragmentAdapter(getSupportFragmentManager(), mPhotoDetailFragmentList);
    mViewPager.setAdapter(photoPagerAdapter);
    mViewPager.addOnPageChangeListener(new OnPageChangeListener() {
      @Override
      public void onPageSelected(int position) {
        super.onPageSelected(position);
      }
    });
  }

  private void createFragment(NewsPhotoDetail newsPhotoDetail) {
    mPhotoDetailFragmentList.clear();
    for (NewsPhotoDetail.Picture picture : newsPhotoDetail.getPictures()) {
      PhotoDetailFragment fragment = new PhotoDetailFragment();
      Bundle bundle = new Bundle();
      bundle.putString(AppConstant.PHOTO_DETAIL_IMGSRC, picture.getImgSrc());
      fragment.setArguments(bundle);
      mPhotoDetailFragmentList.add(fragment);
    }
  }

  public void setPhotoDetailTitle(int position) {
    String title = getTitle(position);
    mContentTv.setText(
        getString(R.string.photo_detail_title, position + 1, mPhotoDetailFragmentList.size(),
            title));
  }

  private String getTitle(int position) {
    String title = mNewsPhotoDetail.getPictures().get(position).getTitle();
    if (title == null) {
      title = mNewsPhotoDetail.getTitle();
    }
    return title;
  }
}
