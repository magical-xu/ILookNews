package com.chaoneng.ilooknews.instance;

import android.content.Context;
import com.chaoneng.ilooknews.ILookApplication;
import com.chaoneng.ilooknews.R;
import com.chaoneng.ilooknews.data.Channel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by magical on 17/8/16.
 * Description : 用于管理 存储 增删标签
 */

public class TabManager {

  private List<Channel> mTabList;
  private List<Channel> otherChannelList;

  private TabManager() {
    mTabList = new ArrayList<>();
    otherChannelList = new ArrayList<>();
    initMyDefault();

    initOther();
  }

  private void initOther() {
    otherChannelList.add(new Channel(Channel.TYPE_OTHER_CHANNEL, "西瓜", "西瓜"));
    otherChannelList.add(new Channel(Channel.TYPE_OTHER_CHANNEL, "橘子", "橘子"));
    otherChannelList.add(new Channel(Channel.TYPE_OTHER_CHANNEL, "西红柿", "西红柿"));
    otherChannelList.add(new Channel(Channel.TYPE_OTHER_CHANNEL, "土豆泥", "土豆泥"));
    otherChannelList.add(new Channel(Channel.TYPE_OTHER_CHANNEL, "薯片", "薯片"));
    otherChannelList.add(new Channel(Channel.TYPE_OTHER_CHANNEL, "牛肉", "牛肉"));
  }

  private void initMyDefault() {
    Context appContext = ILookApplication.getAppContext();
    String recommend = appContext.getString(R.string.sub_tab_recommend);
    String joke = appContext.getString(R.string.sub_tab_joke);
    String music = appContext.getString(R.string.sub_tab_music);
    String social = appContext.getString(R.string.sub_tab_social);

    // add the default tab
    mTabList.add(new Channel(Channel.TYPE_MY_CHANNEL, recommend, recommend, true));
    mTabList.add(new Channel(Channel.TYPE_MY_CHANNEL, joke, joke, true));
    mTabList.add(new Channel(Channel.TYPE_MY_CHANNEL, music, music, true));
    mTabList.add(new Channel(Channel.TYPE_MY_CHANNEL, social, social, true));
  }

  public static TabManager getInstance() {
    return SingletonHolder.mInstance;
  }

  private static class SingletonHolder {

    private static final TabManager mInstance = new TabManager();
  }

  /**
   * 拿到有几个默认标签
   */
  public int getDefaultSize() {
    int size = mTabList.size();
    int count = 0;
    for (int i = 0; i < size; i++) {
      Channel channel = mTabList.get(i);
      if (channel.access) {
        count++;
      }
    }
    return count;
  }

  /**
   * 对标签进行互换位置
   */
  public void swap(int from, int to) {
    Collections.swap(mTabList, from, to);
  }

  /**
   * 添加标签
   */
  public void add(Channel entity) {
    mTabList.add(entity);
  }

  /**
   * 刪除标签
   */
  public void remove(Channel entity) {
    mTabList.remove(entity);
  }

  /**
   * 删除指定位置
   */
  public void remove(int position) {
    mTabList.remove(position);
  }

  /**
   * 拿到 tab 列表
   */
  public List<Channel> getTabList() {
    return mTabList;
  }

  public List<Channel> getOtherList() {
    return otherChannelList;
  }

  /**
   * 移动到我的频道
   */
  public Channel moveToMyChannel(int starPos, int endPos) {

    Channel channel = otherChannelList.remove(starPos);
    mTabList.add(endPos, channel);
    return channel;
  }

  /**
   * 移动到推荐频道
   */
  public Channel moveToOtherChannel(int starPos, int endPos) {

    Channel remove = mTabList.remove(starPos);
    otherChannelList.add(endPos, remove);
    return remove;
  }

  /**
   * 拿到 tab 名字列表
   */
  public List<String> getTabNameList() {
    List<String> nameList = new ArrayList<>();
    for (int i = 0; i < mTabList.size(); i++) {
      nameList.add(mTabList.get(i).title);
    }
    return nameList;
  }
}
