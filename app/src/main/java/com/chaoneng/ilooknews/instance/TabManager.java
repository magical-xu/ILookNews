package com.chaoneng.ilooknews.instance;

import android.content.Context;
import android.support.annotation.Nullable;
import com.chaoneng.ilooknews.AppConstant;
import com.chaoneng.ilooknews.api.HomeService;
import com.chaoneng.ilooknews.data.Channel;
import com.chaoneng.ilooknews.data.TabBean;
import com.chaoneng.ilooknews.net.callback.SimpleCallback;
import com.chaoneng.ilooknews.net.client.NetRequest;
import com.chaoneng.ilooknews.net.data.HttpResult;
import com.chaoneng.ilooknews.util.NotifyListener;
import com.magicalxu.library.blankj.EmptyUtils;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import timber.log.Timber;

/**
 * Created by magical on 17/8/16.
 * Description : 用于管理 存储 增删标签
 */

public class TabManager {

    private List<Channel> mTabList;
    private List<Channel> otherChannelList;
    private List<Channel> mVideoChannelList;    // 视频的tab是单独拉的
    private boolean hasNewsInit;                // 新闻tab 是否初始化完毕
    private boolean hasVideoInit;               // 视频tab 是否初始化完毕

    private TabManager() {
        mTabList = new ArrayList<>();
        otherChannelList = new ArrayList<>();
        mVideoChannelList = new ArrayList<>();
    }

    public static TabManager getInstance() {
        return SingletonHolder.mInstance;
    }

    private static class SingletonHolder {

        private static final TabManager mInstance = new TabManager();
    }

    public boolean hasNewsInit() {
        return hasNewsInit;
    }

    public void setHasInit(boolean hasInit) {
        this.hasNewsInit = hasInit;
    }

    public boolean hasVideoInit() {
        return hasVideoInit;
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
    public List<Channel> getTabList(Context context) {
        List<Channel> channels =
                DBManager.getInstance(context).queryChannelList(DBManager.myChannelDb);
        if (EmptyUtils.isEmpty(channels)) {
            return mTabList;
        } else {
            return channels;
        }
    }

    /**
     * 拿到 推荐标签列表
     */
    public List<Channel> getOtherList(Context context) {
        List<Channel> channels =
                DBManager.getInstance(context).queryChannelList(DBManager.otherChannelDb);
        if (EmptyUtils.isEmpty(channels)) {
            return otherChannelList;
        } else {
            return channels;
        }
    }

    /**
     * 拿到 视频标签列表
     */
    public List<Channel> getVideoList() {
        return mVideoChannelList;
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
    public List<String> getTabNameList(Context context, boolean isVideo) {

        List<Channel> targetList;
        if (isVideo) {
            targetList = mVideoChannelList;
        } else {
            targetList = getTabList(context);
        }

        List<String> nameList = new ArrayList<>();
        for (int i = 0; i < targetList.size(); i++) {
            nameList.add(targetList.get(i).title);
        }
        return nameList;
    }

    /**
     * 网络拉取 新闻类型 频道
     */
    public void getNewsChannel(final Context context, @Nullable final NotifyListener listener) {

        //1.先查数据库有没有记录 判断一个表就好了
        if (DBManager.getInstance(context).hasData(DBManager.myChannelDb)) {
            //do nothing 用到直接获取就好了
            setHasInit(true);
            return;
        }

        HomeService service = NetRequest.getInstance().create(HomeService.class);
        Call<HttpResult<TabBean>> call =
                service.getChannel(AppConstant.TEST_USER_ID, HomeService.NEWS);
        call.enqueue(new SimpleCallback<TabBean>() {
            @Override
            public void onSuccess(TabBean data) {

                if (null != data) {
                    hasNewsInit = true;

                    mTabList = data.myChannels;
                    otherChannelList = data.myChannelsNot;
                    adaptMultiType(mTabList, true);
                    adaptMultiType(otherChannelList, false);
                    DBManager.getInstance(context)
                            .insertChannelList(DBManager.myChannelDb, mTabList);
                    DBManager.getInstance(context)
                            .insertChannelList(DBManager.otherChannelDb, otherChannelList);

                    if (null != listener) {
                        listener.onSuccess();
                    }
                } else {
                    Timber.e(" can't get tab data.");
                }
            }

            @Override
            public void onFail(String code, String errorMsg) {

                hasNewsInit = false;
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

    /**
     * 网络拉取 视频类型 频道
     */
    public void getVideoChannel(@Nullable final NotifyListener listener) {

        HomeService service = NetRequest.getInstance().create(HomeService.class);
        Call<HttpResult<TabBean>> call =
                service.getChannel(AppConstant.TEST_USER_ID, HomeService.VIDEO);
        call.enqueue(new SimpleCallback<TabBean>() {
            @Override
            public void onSuccess(TabBean data) {

                if (null != data) {
                    mVideoChannelList = data.myChannels;
                    hasVideoInit = true;

                    if (null != listener) {
                        listener.onSuccess();
                    }
                } else {
                    Timber.e(" can't get tab data.");
                }
            }

            @Override
            public void onFail(String code, String errorMsg) {

                hasVideoInit = false;
                if (null != listener) {
                    listener.onFail();
                }
            }
        });
    }
}
