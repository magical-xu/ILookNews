package com.chaoneng.ilooknews.instance;

import android.content.Context;
import android.support.annotation.Nullable;
import com.chaoneng.ilooknews.ILookApplication;
import com.chaoneng.ilooknews.api.HomeService;
import com.chaoneng.ilooknews.data.Channel;
import com.chaoneng.ilooknews.data.TabBean;
import com.chaoneng.ilooknews.net.callback.SimpleCallback;
import com.chaoneng.ilooknews.net.client.NetRequest;
import com.chaoneng.ilooknews.net.data.HttpResult;
import com.chaoneng.ilooknews.util.NotifyListener;
import com.chaoneng.ilooknews.util.UpdateUtil;
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

    private List<Channel> mTabList;             // temp list
    private List<Channel> otherChannelList;     // temp list
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

    public void setHasVideoInit(boolean hasInit) {
        this.hasVideoInit = hasInit;
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

        if (null != mTabList) {
            //之前查询过 直接返回临时列表 避免多次去查数据库
            return mTabList;
        }

        //查库
        List<Channel> channels =
                DBManager.getInstance(context).queryChannelList(DBManager.myChannelDb);
        if (EmptyUtils.isEmpty(channels)) {
            Timber.e(" the channel list is empty in my channel db.");
            return mTabList;    //return an empty list
        } else {
            //只要重新查了 就重新赋一次值
            mTabList = channels;
            return mTabList;
        }
    }

    /**
     * 拿到 推荐标签列表
     */
    public List<Channel> getOtherList(Context context) {

        if (null != otherChannelList) {
            return otherChannelList;
        }

        List<Channel> channels =
                DBManager.getInstance(context).queryChannelList(DBManager.otherChannelDb);
        if (EmptyUtils.isEmpty(channels)) {
            Timber.e(" the channel list is empty in other channel db.");
            return otherChannelList;
        } else {
            otherChannelList = channels;
            return otherChannelList;
        }
    }

    /**
     * 拿到 视频标签列表
     */
    public List<Channel> getVideoList(Context context) {

        if (null != mVideoChannelList && !EmptyUtils.isEmpty(mVideoChannelList)) {
            return mVideoChannelList;
        }

        List<Channel> channels = DBManager.getInstance(context).queryChannelList(DBManager.videoDb);
        if (EmptyUtils.isEmpty(channels)) {
            Timber.e(" the channel list is empty in video channel db.");
            return mVideoChannelList;
        } else {
            mVideoChannelList = channels;
            return mVideoChannelList;
        }
    }

    /**
     * 移动到我的频道
     */
    @Nullable
    public Channel moveToMyChannel(final int starPos, final int endPos) {

        int size = otherChannelList.size();
        if (starPos < 0 || starPos >= size) {
            Timber.e(" invalidate position change.");
            return null;
        }

        //仅操作临时列表 最后统一更改顺序和db
        Channel channel = otherChannelList.remove(starPos);
        channel.setItemType(Channel.TYPE_MY_CHANNEL);
        mTabList.add(endPos, channel);

        return channel;
    }

    /**
     * 移动到推荐频道
     */
    @Nullable
    public Channel moveToOtherChannel(final int starPos, final int endPos) {

        int size = mTabList.size();
        if (starPos < 0 || starPos >= size) {
            Timber.e(" invalidate position change.");
            return null;
        }

        //仅操作临时列表 最后统一更改顺序和db
        Channel remove = mTabList.remove(starPos);
        remove.setItemType(Channel.TYPE_OTHER_CHANNEL);
        otherChannelList.add(endPos, remove);

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

        //1.先查数据库有没有记录 因为可以全部删除 所以判断两个表
        final DBManager dbManager = DBManager.getInstance(context);
        if (dbManager.hasData(DBManager.myChannelDb) || dbManager.hasData(
                DBManager.otherChannelDb)) {
            //为临时列表赋值
            mTabList = dbManager.queryChannelList(DBManager.myChannelDb);
            otherChannelList = dbManager.queryChannelList(DBManager.otherChannelDb);
            setHasInit(true);
            if (null != listener) {
                listener.onSuccess();
            }
            return;
        }

        HomeService service = NetRequest.getInstance().create(HomeService.class);
        Call<HttpResult<TabBean>> call = service.getChannel(HomeService.NEWS);
        call.enqueue(new SimpleCallback<TabBean>() {
            @Override
            public void onSuccess(TabBean data) {

                if (null != data) {
                    hasNewsInit = true;

                    mTabList = data.myChannels;
                    otherChannelList = data.myChannelsNot;
                    adaptMultiType(mTabList, true);
                    adaptMultiType(otherChannelList, false);
                    dbManager.insertChannelList(DBManager.myChannelDb, mTabList);
                    dbManager.insertChannelList(DBManager.otherChannelDb, otherChannelList);

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
    public void getVideoChannel(Context context, @Nullable final NotifyListener listener) {

        boolean ignoreDb = UpdateUtil.needUpdateChannel(true);

        final DBManager dbManager = DBManager.getInstance(context);
        if (!ignoreDb) {

            //1.先查数据库有没有视频记录
            if (dbManager.hasData(DBManager.videoDb)) {
                setHasVideoInit(true);
                return;
            }
        }

        HomeService service = NetRequest.getInstance().create(HomeService.class);
        Call<HttpResult<TabBean>> call = service.getChannel(HomeService.VIDEO);
        call.enqueue(new SimpleCallback<TabBean>() {
            @Override
            public void onSuccess(TabBean data) {

                if (null != data) {
                    mVideoChannelList = data.myChannels;
                    //dbManager.insertChannelList(DBManager.videoDb, mVideoChannelList);
                    //删除之前 db里的重新插入
                    updateVideoDb();
                    hasVideoInit = true;
                    UpdateUtil.setChannelUpdateTime(true);

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

    public void updateNewsDb() {

        DBManager.getInstance(ILookApplication.getAppContext())
                .deleteAllAndInsertNew(DBManager.myChannelDb, mTabList);
        DBManager.getInstance(ILookApplication.getAppContext())
                .deleteAllAndInsertNew(DBManager.otherChannelDb, otherChannelList);
    }

    public void updateVideoDb() {

        DBManager.getInstance(ILookApplication.getAppContext())
                .deleteAllAndInsertNew(DBManager.videoDb, mVideoChannelList);
    }
}
