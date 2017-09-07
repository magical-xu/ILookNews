package com.chaoneng.ilooknews.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import butterknife.ButterKnife;
import com.chaoneng.ilooknews.R;
import com.chaoneng.ilooknews.widget.ilook.ILookTitleBar;

/**
 * Created by magical on 17/8/14.
 * Description :
 */

public abstract class BaseActivity extends AppCompatActivity {

  protected ViewGroup mDecorView;
  private FrameLayout mFrameContent;
  protected ILookTitleBar mTitleBar;

  public static final String PAGE_ANIMATION_FROM_BOTTOM = "page_animation_from_bottom";
  protected boolean pageAnimationFromBottom = false;  //底部弹出动画标记

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    checkAnimation();
    checkTitleBar();

    ButterKnife.bind(this);
    handleChildPage(savedInstanceState);
  }

  public abstract int getLayoutId();

  public abstract void handleChildPage(Bundle savedInstanceState);

  /**
   * 钩子函数 是否显示过场动画 默认开启
   */
  protected boolean showPageAnimation() {
    return true;
  }

  /**
   * 钩子函数 是否添加TitleBar 默认不添加
   */
  protected boolean addTitleBar() {
    return false;
  }

  @Override
  public void finish() {
    super.finish();
    checkExitAnimation();
  }

  /**
   * 检查 退出动画
   */
  private void checkExitAnimation() {
    if (showPageAnimation()) {
      if (!pageAnimationFromBottom) {
        overridePendingTransition(R.anim.page_enter_scale, R.anim.page_exit_right);
      } else {
        overridePendingTransition(R.anim.page_enter_scale, R.anim.page_exit_bottom);
      }
    }
  }

  /**
   * 检查 过场动画
   */
  private void checkAnimation() {
    if (showPageAnimation()) {
      pageAnimationFromBottom = getIntent().getBooleanExtra(PAGE_ANIMATION_FROM_BOTTOM, false);
      if (!pageAnimationFromBottom) {
        overridePendingTransition(R.anim.page_enter_right, R.anim.page_exit_scale);
      } else {
        overridePendingTransition(R.anim.page_enter_bottom, R.anim.page_exit_scale);
      }
    }
  }

  /**
   * 检查 是否需要创建TitleBar
   */
  private void checkTitleBar() {

    mDecorView = (ViewGroup) getWindow().getDecorView();
    LayoutInflater mInflater = LayoutInflater.from(this);
    if (addTitleBar()) {
      LinearLayout titleBarLayout =
          (LinearLayout) mInflater.inflate(R.layout.layout_title_base_normal, mDecorView, false);
      mTitleBar = titleBarLayout.findViewById(R.id.id_title_bar);
      mTitleBar.attach(this);
      mFrameContent = titleBarLayout.findViewById(R.id.id_content_frame);
      View contentView = mInflater.inflate(getLayoutId(), mDecorView, false);
      mFrameContent.addView(contentView);
      setContentView(titleBarLayout);
    } else {
      if (getLayoutId() != 0) {
        View view = mInflater.inflate(getLayoutId(), null);
        setContentView(view);
      }
    }
  }

  @Override
  public void onBackPressed() {
    super.onBackPressed();
  }
}
