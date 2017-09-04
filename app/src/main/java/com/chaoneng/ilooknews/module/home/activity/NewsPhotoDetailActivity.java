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
import com.chaoneng.ilooknews.api.HomeService;
import com.chaoneng.ilooknews.base.BaseActivity;
import com.chaoneng.ilooknews.data.ImageInfo;
import com.chaoneng.ilooknews.data.NewsInfo;
import com.chaoneng.ilooknews.data.NewsInfoWrapper;
import com.chaoneng.ilooknews.module.home.fragment.PhotoDetailFragment;
import com.chaoneng.ilooknews.net.callback.SimpleCallback;
import com.chaoneng.ilooknews.net.client.NetRequest;
import com.chaoneng.ilooknews.net.data.HttpResult;
import com.chaoneng.ilooknews.widget.adapter.BaseFragmentAdapter;
import com.chaoneng.ilooknews.widget.adapter.OnPageChangeListener;
import com.chaoneng.ilooknews.widget.ilook.ILookTitleBar;
import com.chaoneng.ilooknews.widget.viewpager.ViewPagerFixed;
import com.magicalxu.library.blankj.ToastUtils;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import timber.log.Timber;

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
  private List<String> mDesList = new ArrayList<>();
  private HomeService service;

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

    loadData();
    checkTitle();
  }

  private void loadData() {

    service = NetRequest.getInstance().create(HomeService.class);
    Call<HttpResult<NewsInfoWrapper>> call =
        service.getNewsDetail(AppConstant.TEST_USER_ID, AppConstant.TEST_PHOTO_NEWS_ID, 3);
    call.enqueue(new SimpleCallback<NewsInfoWrapper>() {
      @Override
      public void onSuccess(NewsInfoWrapper data) {

        if (null == data) {
          Timber.e("data is null");
          return;
        }

        NewsInfo newInfo = data.newInfo;
        if (null == newInfo) {
          Timber.e("news info is null.");
          return;
        }

        config(newInfo.pictures);
      }

      @Override
      public void onFail(String code, String errorMsg) {
        ToastUtils.showShort(errorMsg);
      }
    });
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
        updateDescription(position);
      }
    });
    updateDescription(0);
  }

  private void config(List<ImageInfo> list) {
    mPhotoDetailFragmentList.clear();
    mDesList.clear();
    for (ImageInfo imageInfo : list) {
      PhotoDetailFragment fragment = new PhotoDetailFragment();
      Bundle bundle = new Bundle();
      bundle.putString(AppConstant.PHOTO_DETAIL_IMGSRC, imageInfo.picUrl);
      fragment.setArguments(bundle);
      mPhotoDetailFragmentList.add(fragment);

      mDesList.add(imageInfo.description);
    }

    initViewPager();
  }

  public void updateDescription(int position) {
    String title = mDesList.get(position);
    mContentTv.setText(
        getString(R.string.photo_detail_title, position + 1, mDesList.size(), title));
  }
}
