package com.chaoneng.ilooknews.widget;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.chaoneng.ilooknews.R;
import com.chaoneng.ilooknews.widget.image.HeadImageView;

/**
 * Created by magical.zhang on 2017/3/18.
 * Description : 通用头部
 */
public class ILookTitleBar extends RelativeLayout implements View.OnClickListener {

  protected HeadImageView mLeftCircle;
  protected ImageView mLeftImage;
  protected TextView mLeftText;

  protected TextView mTitleText;
  protected ImageView mTitleImage;

  protected TextView mRightText;
  protected ImageView mRightImage;

  protected View mDivider;
  protected ViewGroup mTitleBg;

  private TitleCallback listener;

  public ILookTitleBar(Context context) {
    this(context, null);
  }

  public ILookTitleBar(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public ILookTitleBar(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    if (isInEditMode()) {
      return;
    }
    inflateLayout(context);
    setTitleEvent();
  }

  private void inflateLayout(Context context) {
    LayoutInflater inflater = LayoutInflater.from(context);
    inflater.inflate(R.layout.layout_title_bar_center, this);

    mLeftCircle = findViewById(R.id.iv_circle_left);
    mLeftImage = findViewById(R.id.iv_title_left);
    mLeftText = findViewById(R.id.tv_title_left);

    mTitleText = findViewById(R.id.tv_title);
    mTitleImage = findViewById(R.id.iv_title);

    mRightText = findViewById(R.id.tv_title_right);
    mRightImage = findViewById(R.id.iv_title_right);

    mDivider = findViewById(R.id.id_divider_bottom);
    mTitleBg = findViewById(R.id.id_title_bg);
  }

  private void setTitleEvent() {
    mRightText.setOnClickListener(this);
    mRightImage.setOnClickListener(this);
    mLeftImage.setOnClickListener(this);
    mLeftCircle.setOnClickListener(this);
  }

  public ILookTitleBar setTitle(String title) {
    if (TextUtils.isEmpty(title)) {
      mTitleText.setVisibility(View.GONE);
    } else {
      mTitleText.setText(title);
    }
    return this;
  }

  public ILookTitleBar setTitleImage(int resId) {
    mTitleText.setVisibility(View.GONE);
    mTitleImage.setVisibility(View.VISIBLE);
    mTitleImage.setImageResource(resId);
    return this;
  }

  public ILookTitleBar setLeftImage(int resId) {
    mLeftImage.setVisibility(View.VISIBLE);
    mLeftImage.setImageResource(resId);
    return this;
  }

  public ILookTitleBar hideLeftImage() {
    mLeftImage.setVisibility(View.GONE);
    return this;
  }

  public ILookTitleBar hideDivider() {
    mDivider.setVisibility(View.GONE);
    return this;
  }

  public ILookTitleBar setLeftCircle(String url) {
    mLeftCircle.setVisibility(View.VISIBLE);
    mLeftImage.setVisibility(View.GONE);
    mLeftCircle.setHeadImage(url);
    return this;
  }

  public ILookTitleBar setRightText(String text) {
    if (TextUtils.isEmpty(text)) {
      mRightText.setVisibility(View.GONE);
    } else {
      mRightText.setVisibility(View.VISIBLE);
      mRightText.setText(text);
    }
    return this;
  }

  public ILookTitleBar setRightImage(int resId) {
    mRightImage.setVisibility(View.VISIBLE);
    mRightImage.setImageResource(resId);
    return this;
  }

  public ILookTitleBar setTitleBg(int color) {
    mTitleBg.setBackgroundResource(color);
    return this;
  }

  public void attach(Activity activity) {
    if (mLeftImage != null) {
      mLeftImage.setTag(activity);
    }
  }

  @Override
  public void onClick(View v) {
    if (v == mRightText && null != listener) {
      listener.onClickRightText(mRightText);
    } else if (v == mRightImage && null != listener) {
      listener.onClickRightImage(mRightImage);
    } else if (v == mLeftImage) {
      //默认实现 返回按键
      if (null != listener) {
        listener.onClickLeft(mLeftImage);
      }
      performBack();
    }
  }

  private void performBack() {

    if (null != listener && !listener.needBack()) {
      return;
    }

    //Drawable backDrawable =
    //        ImageUtil.getDrawableById(mLeftImage.getContext(), R.drawable.ic_return);
    //if (null != backDrawable) {
    //    if (mLeftImage.getDrawable().getCurrent().getConstantState()
    //            == backDrawable.getCurrent().getConstantState()) {
    //        Activity attach = (Activity) mLeftImage.getTag();
    //        attach.onBackPressed();
    //    }
    //}
  }

  private interface TitleCallback {

    void onClickLeft(View view);

    void onClickRightText(View view);

    void onClickRightImage(View view);

    boolean needBack();
  }

  public static class TitleCallbackAdapter implements TitleCallback {

    @Override
    public void onClickLeft(View view) {
    }

    @Override
    public void onClickRightText(View view) {
    }

    @Override
    public void onClickRightImage(View view) {
    }

    @Override
    public boolean needBack() {
      return true;
    }
  }

  public void setTitleListener(TitleCallback listener) {
    this.listener = listener;
  }

  public TextView getTitleRightText() {
    return mRightText;
  }
}
