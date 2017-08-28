package com.chaoneng.ilooknews.instance;

import android.content.Context;
import android.support.annotation.Nullable;
import com.chaoneng.ilooknews.AppConstant;
import com.chaoneng.ilooknews.ILookApplication;
import com.chaoneng.ilooknews.R;
import com.chaoneng.ilooknews.api.HomeService;
import com.chaoneng.ilooknews.data.Channel;
import com.chaoneng.ilooknews.data.TabBean;
import com.chaoneng.ilooknews.net.callback.SimpleCallback;
import com.chaoneng.ilooknews.net.client.NetRequest;
import com.chaoneng.ilooknews.net.data.HttpResult;
import com.chaoneng.ilooknews.util.NotifyListener;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;

/**
 * Created by magical on 17/8/16.
 * Description : 用于管理 存储 增删标签
 */

public class TabManager {

  private List<Channel> mTabList;
  private List<Channel> otherChannelList;
  private boolean hasInit;

  private TabManager() {
    mTabList = new ArrayList<>();
    otherChannelList = new ArrayList<>();

    //initMyDefault();
    //initOther();
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

  public boolean hasInit() {
    return hasInit;
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
   * 添加标签
   */
  public void add(Channel entity) {
    mTabList.add(entity);
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
  public Channel moveToMyChannel(final int starPos, final int endPos) {

    //Channel channel = otherChannelList.get(starPos);
    //
    //HomeService service = NetRequest.getInstance().create(HomeService.class);
    //Call<HttpResult<String>> call = service.addMyChannel(AppConstant.TEST_USER_ID, channel.code);
    //call.enqueue(new SimpleCallback<String>() {
    //  @Override
    //  public void onSuccess(String data) {

    Channel channel = otherChannelList.remove(starPos);
    channel.setItemType(Channel.TYPE_MY_CHANNEL);
    mTabList.add(endPos, channel);
    //  }
    //
    //  @Override
    //  public void onFail(String code, String errorMsg) {
    //
    //  }
    //});

    return channel;
  }

  /**
   * 移动到推荐频道
   */
  public Channel moveToOtherChannel(final int starPos, final int endPos) {

    //Channel channel = mTabList.get(starPos);
    //
    //HomeService service = NetRequest.getInstance().create(HomeService.class);
    //Call<HttpResult<String>> call = service.deleteMyChannel(AppConstant.TEST_USER_ID, channel.code);
    //call.enqueue(new Callback<HttpResult<String>>() {
    //  @Override
    //  public void onResponse(Call<HttpResult<String>> call, Response<HttpResult<String>> response) {
    Channel remove = mTabList.remove(starPos);
    remove.setItemType(Channel.TYPE_OTHER_CHANNEL);
    otherChannelList.add(endPos, remove);
    //  }
    //
    //  @Override
    //  public void onFailure(Call<HttpResult<String>> call, Throwable t) {
    //
    //  }
    //});

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

  /**
   * 网络拉取 新闻类型 频道
   */
  public void getNewsChannel(@Nullable final NotifyListener listener) {

    HomeService service = NetRequest.getInstance().create(HomeService.class);
    Call<HttpResult<TabBean>> call = service.getChannel(AppConstant.TEST_USER_ID, HomeService.NEWS);
    call.enqueue(new SimpleCallback<TabBean>() {
      @Override
      public void onSuccess(TabBean data) {

        hasInit = true;
        mTabList = data.myChannels;
        otherChannelList = data.myChannelsNot;
        adaptMultiType(mTabList, true);
        adaptMultiType(otherChannelList, false);
        if (null != listener) {
          listener.onSuccess();
        }
      }

      @Override
      public void onFail(String code, String errorMsg) {

        hasInit = false;
        if (null != listener) {
          listener.onFail();
        }
      }
    });
  }

  private void adaptMultiType(List<Channel> list, boolean mine) {
    for (Channel one : list) {
      one.setItemType(mine ? Channel.TYPE_MY_CHANNEL : Channel.TYPE_OTHER_CHANNEL);
    }
  }
}
