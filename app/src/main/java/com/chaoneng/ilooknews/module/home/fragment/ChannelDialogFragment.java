package com.chaoneng.ilooknews.module.home.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chaoneng.ilooknews.R;
import com.chaoneng.ilooknews.data.Channel;
import com.chaoneng.ilooknews.module.home.adapter.ChannelAdapter;
import com.chaoneng.ilooknews.module.home.callback.ItemDragHelperCallBack;
import com.chaoneng.ilooknews.module.home.callback.OnChannelDragListener;
import com.chaoneng.ilooknews.module.home.callback.OnChannelListener;
import java.util.ArrayList;
import java.util.List;

import static com.chaoneng.ilooknews.data.Channel.TYPE_MY_CHANNEL;

/**
 * Created by magical on 2017/8/18 .
 * 频道编辑的 fragment
 */

public class ChannelDialogFragment extends DialogFragment implements OnChannelDragListener {

  @BindView(R.id.recyclerView) RecyclerView mRecyclerView;

  private ChannelAdapter mAdapter;
  private ItemTouchHelper mHelper;
  private List<Channel> mDatas = new ArrayList<>();

  private OnChannelListener mOnChannelListener;

  public void setOnChannelListener(OnChannelListener onChannelListener) {
    mOnChannelListener = onChannelListener;
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
    return inflater.inflate(R.layout.fragment_home_channel_edit, null);
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    ButterKnife.bind(this, view);
    processLogic();
  }

  public static ChannelDialogFragment newInstance(List<Channel> selectedDatas,
      List<Channel> unselectedDatas) {
    ChannelDialogFragment dialogFragment = new ChannelDialogFragment();
    Bundle bundle = new Bundle();
    //bundle.putSerializable(ConstanceValue.DATA_SELECTED, (Serializable) selectedDatas);
    //bundle.putSerializable(ConstanceValue.DATA_UNSELECTED, (Serializable) unselectedDatas);
    dialogFragment.setArguments(bundle);
    return dialogFragment;
  }

  private void setDataType(List<Channel> datas, int type) {
    for (int i = 0; i < datas.size(); i++) {
      datas.get(i).setItemType(type);
    }
  }

  private void processLogic() {
    mDatas.add(new Channel(Channel.TYPE_MY, "我的频道", ""));
    Bundle bundle = getArguments();
    //List<Channel> selectedDatas =
    //    (List<Channel>) bundle.getSerializable(ConstanceValue.DATA_SELECTED);
    //List<Channel> unselectedDatas =
    //    (List<Channel>) bundle.getSerializable(ConstanceValue.DATA_UNSELECTED);
    //setDataType(selectedDatas, TYPE_MY_CHANNEL);
    //setDataType(unselectedDatas, Channel.TYPE_OTHER_CHANNEL);

    //mDatas.addAll(selectedDatas);
    //mDatas.add(new Channel(Channel.TYPE_OTHER, "频道推荐", ""));
    //mDatas.addAll(unselectedDatas);

    mAdapter = new ChannelAdapter(mDatas);
    GridLayoutManager manager = new GridLayoutManager(getActivity(), 4);
    mRecyclerView.setLayoutManager(manager);
    mRecyclerView.setAdapter(mAdapter);
    manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
      @Override
      public int getSpanSize(int position) {
        int itemViewType = mAdapter.getItemViewType(position);
        return itemViewType == TYPE_MY_CHANNEL || itemViewType == Channel.TYPE_OTHER_CHANNEL ? 1
            : 4;
      }
    });
    ItemDragHelperCallBack callBack = new ItemDragHelperCallBack(this);
    mHelper = new ItemTouchHelper(callBack);
    mAdapter.setOnChannelDragListener(this);
    //attach to RecyclerView
    mHelper.attachToRecyclerView(mRecyclerView);
  }

  @OnClick(R.id.icon_collapse)
  public void onClick(View v) {
    dismiss();
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

  @Override
  public void onStarDrag(BaseViewHolder baseViewHolder) {
    //开始拖动
    mHelper.startDrag(baseViewHolder);
  }

  @Override
  public void onItemMove(int starPos, int endPos) {
    //        if (starPos < 0||endPos<0) return;
    //我的频道之间移动
    if (mOnChannelListener != null) {
      mOnChannelListener.onItemMove(starPos - 1, endPos - 1);//去除标题所占的一个index
    }
    onMove(starPos, endPos);
  }

  private void onMove(int starPos, int endPos) {
    Channel startChannel = mDatas.get(starPos);
    //先删除之前的位置
    mDatas.remove(starPos);
    //添加到现在的位置
    mDatas.add(endPos, startChannel);
    mAdapter.notifyItemMoved(starPos, endPos);
  }

  @Override
  public void onMoveToMyChannel(int starPos, int endPos) {
    //移动到我的频道
    onMove(starPos, endPos);

    if (mOnChannelListener != null) {
      mOnChannelListener.onMoveToMyChannel(starPos - 1 - mAdapter.getMyChannelSize(), endPos - 1);
    }
  }

  @Override
  public void onMoveToOtherChannel(int starPos, int endPos) {
    //移动到推荐频道
    onMove(starPos, endPos);
    if (mOnChannelListener != null) {
      mOnChannelListener.onMoveToOtherChannel(starPos - 1,
          endPos - 2 - mAdapter.getMyChannelSize());
    }
  }
}
