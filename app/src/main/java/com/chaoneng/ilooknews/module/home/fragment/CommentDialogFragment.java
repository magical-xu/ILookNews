package com.chaoneng.ilooknews.module.home.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.chaoneng.ilooknews.R;
import com.chaoneng.ilooknews.module.home.callback.OnChannelListener;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

/**
 * Created by magical on 2017/8/18 .
 * 频道编辑的 fragment
 */

public class CommentDialogFragment extends DialogFragment {

  @BindView(R.id.id_recycler) RecyclerView mRecyclerView;
  @BindView(R.id.id_refresh_layout) SmartRefreshLayout mRefreshLayout;
  @BindView(R.id.tv_title) TextView mTvTitle;
  @BindView(R.id.iv_title_left) View mIvBack;

  private OnChannelListener mOnChannelListener;

  public void setOnChannelListener(OnChannelListener onChannelListener) {
    mOnChannelListener = onChannelListener;
  }

  public static CommentDialogFragment newInstance() {
    CommentDialogFragment dialogFragment = new CommentDialogFragment();
    return dialogFragment;
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar);
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    Dialog dialog = getDialog();
    if (dialog != null && dialog.getWindow() != null) {
      //添加动画
      dialog.getWindow().setWindowAnimations(R.style.dialogSlideAnim);
    }
    return inflater.inflate(R.layout.fragment_comment_list, null);
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    ButterKnife.bind(this, view);
    processLogic();
  }

  private void processLogic() {

    mTvTitle.setText("楼层回复");
    mIvBack.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        dismiss();
      }
    });
  }

  private DialogInterface.OnDismissListener mOnDismissListener;

  public void setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
    mOnDismissListener = onDismissListener;
  }

  @Override
  public void onDismiss(DialogInterface dialog) {
    super.onDismiss(dialog);
    if (mOnDismissListener != null) mOnDismissListener.onDismiss(dialog);
  }
}
