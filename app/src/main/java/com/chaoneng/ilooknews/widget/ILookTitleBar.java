package com.chaoneng.ilooknews.widget;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.chaoneng.ilooknews.R;

/**
 * Created by magical.zhang on 2017/3/18.
 * Description : 通用头部
 */
public class ILookTitleBar extends RelativeLayout implements View.OnClickListener {

  protected ImageView mLeftImage;
  protected TextView mTitleText;
  protected TextView mRightText;
  protected ImageView mRightImage;
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

    //mLeftImage = (ImageView) findViewById(R.id.iv_title_left);
    //mTitleText = (TextView) findViewById(R.id.tv_title);
    //mRightText = (TextView) findViewById(R.id.tv_title_right);
    //mRightImage = (ImageView) findViewById(R.id.iv_title_right);
  }

  private void setTitleEvent() {
    mRightText.setOnClickListener(this);
    mRightImage.setOnClickListener(this);
    mLeftImage.setOnClickListener(this);
  }

  public void setTitle(String title) {
    if (TextUtils.isEmpty(title)) {
      mTitleText.setVisibility(View.GONE);
    } else {
      mTitleText.setText(title);
    }
  }

  public void setLeftImage(int resId) {
    mLeftImage.setImageResource(resId);
  }

  public void setRightText(String text) {
    if (TextUtils.isEmpty(text)) {
      mRightText.setVisibility(View.GONE);
    } else {
      mRightText.setVisibility(View.VISIBLE);
      mRightText.setText(text);
    }
  }

  public void setRightImage(int resId) {
    mRightImage.setVisibility(View.VISIBLE);
    mRightImage.setImageResource(resId);
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
