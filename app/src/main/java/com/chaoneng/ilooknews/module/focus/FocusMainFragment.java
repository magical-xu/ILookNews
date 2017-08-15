package com.chaoneng.ilooknews.module.focus;

import android.widget.ImageView;
import butterknife.BindView;
import com.chaoneng.ilooknews.AppConstant;
import com.chaoneng.ilooknews.R;
import com.chaoneng.ilooknews.base.BaseFragment;
import com.chaoneng.ilooknews.library.glide.ImageLoader;

/**
 * Created by magical on 17/8/15.
 * Description : 主页 - 关注
 */

public class FocusMainFragment extends BaseFragment {

  @BindView(R.id.id_test) ImageView imageView;

  @Override
  protected void beginLoadData() {

    ImageLoader.loadImage(AppConstant.TEST_AVATAR,imageView);
  }

  @Override
  protected void doInit() {

  }

  @Override
  protected boolean isNeedShowLoadingView() {
    return false;
  }

  @Override
  protected int getLayoutName() {
    return R.layout.layout_main_focus_fg;
  }
}
