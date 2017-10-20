package com.aktt.news.util;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import com.aktt.news.ILookApplication;

/**
 * Created by magical on 17/10/16.
 * Description :
 */

public class LocalBroadcastUtil {

    public static final String LOGIN = "login";
    public static final String LOGOUT = "logout";
    public static final String UPDATE_CONTENT = "update_content";
    public static final String UPDATE_USER_INFO = "update_user_info";
    public static final int UPDATE_AVATAR = 1001;
    public static final int UPDATE_NICK = 1002;
    public static final int UPDATE_SIGN = 1003;

    /**
     * 在 onResume()中注册
     */
    public static void register(BroadcastReceiver receiver, IntentFilter filter) {

        LocalBroadcastManager.getInstance(ILookApplication.getAppContext())
                .registerReceiver(receiver, filter);
    }

    /**
     * 在 onPause()中销毁
     */
    public static void unRegister(BroadcastReceiver receiver) {

        LocalBroadcastManager.getInstance(ILookApplication.getAppContext())
                .unregisterReceiver(receiver);
    }

    /**
     * 发送本地广播 更新用户信息
     *
     * @param type 要更新的类型
     */
    public static void sendUpdateUserInfo(int type, String content) {

        Intent intent = new Intent(UPDATE_USER_INFO);
        intent.putExtra(UPDATE_USER_INFO, type);
        intent.putExtra(UPDATE_CONTENT, content);
        LocalBroadcastManager.getInstance(ILookApplication.getAppContext()).sendBroadcast(intent);
    }

    /**
     * 用户登出
     */
    public static void sendUserLogout() {

        Intent intent = new Intent(LOGOUT);
        LocalBroadcastManager.getInstance(ILookApplication.getAppContext()).sendBroadcast(intent);
    }

    /**
     * 用户登录
     */
    public static void sendUserLogin() {

        Intent intent = new Intent(LOGIN);
        LocalBroadcastManager.getInstance(ILookApplication.getAppContext()).sendBroadcast(intent);
    }
}
