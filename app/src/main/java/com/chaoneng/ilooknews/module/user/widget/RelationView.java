package com.chaoneng.ilooknews.module.user.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.chaoneng.ilooknews.R;

/**
 * Created by magical.zhang on 2017/2/27.
 * Description : 关系控件
 */
public class RelationView extends RelativeLayout {

  View mRootView;

  @BindView(R.id.id_top) TextView mCountTv;

  @BindView(R.id.id_bottom) TextView mSuffix;

  @BindView(R.id.id_red) TextView mRedPoint;

  public RelationView(Context context) {
    this(context, null);
  }

  public RelationView(Context context, AttributeSet attrs) {
    super(context, attrs);
    initView();
  }

  private void initView() {
    mRootView =
        LayoutInflater.from(getContext()).inflate(R.layout.widget_relation_item, this, true);
    ButterKnife.bind(this, mRootView);
  }

  public RelationView setCount(String count) {
    mCountTv.setText(count);
    return this;
  }

  public RelationView setBottom(String bottom) {
    mSuffix.setText(bottom);
    return this;
  }

  public RelationView setRedPoint(String count) {
    mRedPoint.setText(count);
    mRedPoint.setVisibility(View.VISIBLE);
    return this;
  }

  public RelationView hideRedPoint() {
    mRedPoint.setVisibility(View.GONE);
    return this;
  }
}