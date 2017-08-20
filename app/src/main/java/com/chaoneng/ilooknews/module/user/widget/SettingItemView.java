package com.chaoneng.ilooknews.module.user.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;
import com.chaoneng.ilooknews.R;

/**
 * Created by magical on 17/8/20.
 * Description :
 */

public class SettingItemView extends RelativeLayout {

  private TextView mLeftTitle;

  private TextView mRightText;
  private ImageView mRightArrow;
  private ToggleButton mRightToggle;
  //private View mDivider;

  public SettingItemView(Context context) {
    this(context, null);
  }

  public SettingItemView(Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public SettingItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  private void init() {

    View root = LayoutInflater.from(getContext()).inflate(R.layout.widget_setting_item, this, true);

    mLeftTitle = root.findViewById(R.id.id_item_title);
    mRightArrow = root.findViewById(R.id.id_item_arrow);
    mRightText = root.findViewById(R.id.id_item_right_text);
    mRightToggle = root.findViewById(R.id.id_item_toggle);
    //mDivider = root.findViewById(R.id.id_bottom_divider);
  }

  public SettingItemView setTitle(String title) {
    mLeftTitle.setText(title);
    return this;
  }

  public SettingItemView setRightText(String rightText) {
    mRightText.setVisibility(View.VISIBLE);
    mRightText.setText(rightText);
    return this;
  }

  public SettingItemView hideRightArrow() {
    mRightArrow.setVisibility(View.INVISIBLE);
    return this;
  }

  //public SettingItemView hideDivider() {
  //  mDivider.setVisibility(View.GONE);
  //  return this;
  //}

  public SettingItemView showToggle() {
    mRightToggle.setVisibility(View.VISIBLE);
    mRightText.setVisibility(View.GONE);
    mRightArrow.setVisibility(View.GONE);
    return this;
  }

  public ToggleButton getToggle() {
    return mRightToggle;
  }
}
