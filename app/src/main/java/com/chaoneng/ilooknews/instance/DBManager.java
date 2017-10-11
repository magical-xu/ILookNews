package com.chaoneng.ilooknews.instance;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import com.chaoneng.ilooknews.data.Channel;
import com.chaoneng.ilooknews.data.ChannelDao;
import com.chaoneng.ilooknews.data.DaoMaster;
import com.chaoneng.ilooknews.data.DaoSession;
import java.util.List;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.query.QueryBuilder;

/**
 * Created by magical on 17/9/11.
 * Description :
 */

public class DBManager {

    public final static String myChannelDb = "my_channel_db";
    public final static String otherChannelDb = "other_channel_db";
    public final static String videoDb = "video_ch_db";

    private static DBManager mInstance;
    private DaoMaster.DevOpenHelper myHelper;
    private DaoMaster.DevOpenHelper otherHelper;
    private DaoMaster.DevOpenHelper videoHelper;
    private DaoMaster.DevOpenHelper historyHelper;
    private Context context;

    public DBManager(Context context) {
        this.context = context;
        myHelper = new DaoMaster.DevOpenHelper(context, myChannelDb, null);
        otherHelper = new DaoMaster.DevOpenHelper(context, otherChannelDb, null);
        videoHelper = new DaoMaster.DevOpenHelper(context, videoDb, null);
    }

    /**
     * 获取单例引用
     */
    public static DBManager getInstance(Context context) {
        if (mInstance == null) {
            synchronized (DBManager.class) {
                if (mInstance == null) {
                    mInstance = new DBManager(context);
                }
            }
        }
        return mInstance;
    }

    /**
     * 获取可读数据库
     */
    private SQLiteDatabase getReadableDatabase(String type) {

        SQLiteDatabase db;
        if (TextUtils.equals(type, myChannelDb)) {
            if (myHelper == null) {
                myHelper = new DaoMaster.DevOpenHelper(context, myChannelDb, null);
            }
            db = myHelper.getReadableDatabase();
        } else if (TextUtils.equals(type, otherChannelDb)) {
            if (otherHelper == null) {
                otherHelper = new DaoMaster.DevOpenHelper(context, otherChannelDb, null);
            }
            db = otherHelper.getReadableDatabase();
        } else {
            if (videoHelper == null) {
                videoHelper = new DaoMaster.DevOpenHelper(context, videoDb, null);
            }
            db = videoHelper.getReadableDatabase();
        }

        return db;
    }

    /**
     * 获取可写数据库
     */
    private SQLiteDatabase getWritableDatabase(String type) {
        SQLiteDatabase db;
        if (TextUtils.equals(type, myChannelDb)) {
            if (myHelper == null) {
                myHelper = new DaoMaster.DevOpenHelper(context, myChannelDb, null);
            }
            db = myHelper.getWritableDatabase();
        } else if (TextUtils.equals(type, otherChannelDb)) {
            if (otherHelper == null) {
                otherHelper = new DaoMaster.DevOpenHelper(context, otherChannelDb, null);
            }
            db = otherHelper.getWritableDatabase();
        } else {
            if (videoHelper == null) {
                videoHelper = new DaoMaster.DevOpenHelper(context, videoDb, null);
            }
            db = videoHelper.getWritableDatabase();
        }

        return db;
    }

    /**
     * 插入一条记录
     */
    public void insert(String type, Channel channel) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase(type));
        DaoSession daoSession = daoMaster.newSession();
        ChannelDao dao = daoSession.getChannelDao();
        dao.insert(channel);
    }

    /**
     * 插入集合
     */
    public void insertChannelList(String type, List<Channel> channels) {
        if (channels == null || channels.isEmpty()) {
            return;
        }
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase(type));
        DaoSession daoSession = daoMaster.newSession();
        ChannelDao dao = daoSession.getChannelDao();
        dao.insertInTx(channels);
    }

    /**
     * 删除一条记录
     */
    public void delete(String type, Channel channel) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase(type));
        DaoSession daoSession = daoMaster.newSession();
        ChannelDao dao = daoSession.getChannelDao();
        dao.delete(channel);
    }

    /**
     * 删除所有记录 并 插入新的集合
     */
    public void deleteAllAndInsertNew(String type, @NotNull List<Channel> list) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase(type));
        DaoSession daoSession = daoMaster.newSession();
        ChannelDao dao = daoSession.getChannelDao();
        dao.deleteAll();
        dao.insertInTx(list);
    }

    public void update(String type, Channel channel) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase(type));
        DaoSession daoSession = daoMaster.newSession();
        ChannelDao dao = daoSession.getChannelDao();
        dao.update(channel);
    }

    /**
     * 判断是否有数据记录
     */
    public boolean hasData(String type) {
        DaoMaster daoMaster = new DaoMaster(getReadableDatabase(type));
        DaoSession daoSession = daoMaster.newSession();
        ChannelDao userDao = daoSession.getChannelDao();
        QueryBuilder<Channel> qb = userDao.queryBuilder();
        List<Channel> list = qb.list();
        return list != null && !list.isEmpty();
    }

    /**
     * 查数据并获取
     */
    public List<Channel> queryChannelList(String type) {
        DaoMaster daoMaster = new DaoMaster(getReadableDatabase(type));
        DaoSession daoSession = daoMaster.newSession();
        ChannelDao userDao = daoSession.getChannelDao();
        return userDao.loadAll();
        //QueryBuilder<Channel> qb = userDao.queryBuilder().orderAsc(ChannelDao.Properties.Sort);
        //return qb.list();
    }
}
