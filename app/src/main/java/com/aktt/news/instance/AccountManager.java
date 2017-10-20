package com.aktt.news.instance;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import com.aktt.news.AppConstant;
import com.aktt.news.data.BaseUser;
import com.aktt.news.util.IntentHelper;
import com.aktt.news.util.LocalBroadcastUtil;
import com.magicalxu.library.blankj.SPUtils;

/**
 * Created by magical on 17/8/30.
 * Description : 账户管理类
 */

public class AccountManager {

    private BaseUser user;

    private AccountManager() {
    }

    private static class SingletonHolder {

        private static final AccountManager instance = new AccountManager();
    }

    public static AccountManager getInstance() {
        return SingletonHolder.instance;
    }

    /**
     * 保存用户ID到 sp 中
     * 不安全
     *
     * @param user 登录后的
     */
    public void saveUser(@NonNull BaseUser user) {
        SPUtils spUtils = SPUtils.getInstance();
        spUtils.put(AppConstant.UID, user.id);
        spUtils.put(AppConstant.USER_ICON, user.icon);
        this.user = user;
        LocalBroadcastUtil.sendUserLogin();
        Log.d("magical", " save user data ");
    }

    public void saveUser(BaseUser user, boolean saveId) {
        if (saveId) {
            saveUser(user);
        } else {
            this.user = user;
        }
    }

    @Nullable
    public String getUserId() {
        return SPUtils.getInstance().getString(AppConstant.UID);
    }

    @Nullable
    public BaseUser getUser() {
        return this.user;
    }

    /**
     * 暂时根据是否有存 uid 判断登录状态
     */
    public boolean hasLogin() {
        String uid = SPUtils.getInstance().getString(AppConstant.UID);
        return !TextUtils.isEmpty(uid);
    }

    public boolean checkLogin(Context context) {
        if (hasLogin()) {
            return false;
        } else {
            IntentHelper.openLoginPage(context);
            return true;
        }
    }

    /**
     * 登出
     */
    public void logout() {
        this.user = null;
        SPUtils spUtils = SPUtils.getInstance();
        spUtils.put(AppConstant.UID, "");
        spUtils.put(AppConstant.USER_ICON, "");
        LocalBroadcastUtil.sendUserLogout();
    }

    public void updateLocalNick(String nick) {
        BaseUser user = getUser();
        if (null != user) {
            user.username = nick;
        }
    }

    public void updateLocalSign(String sign) {
        BaseUser user = getUser();
        if (null != user) {
            user.introduce = sign;
        }
    }

    public void updateLocalAvatar(String avatarUrl) {
        BaseUser user = getUser();
        if (null != user) {
            user.icon = avatarUrl;
        }
    }
}
