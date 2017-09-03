package com.chaoneng.ilooknews.widget.ilook;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.chaoneng.ilooknews.R;
import com.chaoneng.ilooknews.widget.image.HeadImageView;

/**
 * Created by magical on 17/9/3.
 * Description :
 */

public class UserItemRowView extends LinearLayout {

  @BindView(R.id.iv_avatar) HeadImageView headIv;
  @BindView(R.id.tv_name) TextView tvName;
  @BindView(R.id.tv_intro) TextView tvIntro;
  @BindView(R.id.tv_focus) TextView tvFocus;

  public UserItemRowView(Context context) {
    this(context, null);
  }

  public UserItemRowView(Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public UserItemRowView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    initView();
  }

  private void initView() {

    LayoutInflater.from(getContext()).inflate(R.layout.item_user_row, this, true);
    setOrientation(LinearLayout.HORIZONTAL);
    ButterKnife.bind(this);
  }

  public UserItemRowView setHead(String url) {
    headIv.setHeadImage(url);
    return this;
  }

  public UserItemRowView setName(String name) {
    tvName.setText(name);
    return this;
  }

  public UserItemRowView setIntro(String intro) {
    tvIntro.setText(intro);
    return this;
  }

  public UserItemRowView setFocusListener(OnClickListener listener) {
    tvFocus.setOnClickListener(listener);
    return this;
  }
}
