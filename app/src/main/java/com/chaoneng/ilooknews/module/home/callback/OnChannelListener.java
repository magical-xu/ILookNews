package com.chaoneng.ilooknews.module.home.callback;

/**
 * Created by magical on 2017/8/15.
 */

public interface OnChannelListener {

  void onItemMove(int starPos, int endPos);

  void onMoveToMyChannel(int starPos, int endPos);

  void onMoveToOtherChannel(int starPos, int endPos);
}
