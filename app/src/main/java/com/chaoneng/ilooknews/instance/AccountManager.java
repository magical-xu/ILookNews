package com.chaoneng.ilooknews.instance;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import com.chaoneng.ilooknews.AppConstant;
import com.chaoneng.ilooknews.data.BaseUser;
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
    SPUtils.getInstance().put(AppConstant.UID, user.id);
    this.user = user;
  }

  @Nullable
  public String getUserId() {
    return user == null ? null : user.id;
  }

  /**
   * 暂时根据是否有存 uid 判断登录状态
   */
  public boolean hasLogin() {
    String uid = SPUtils.getInstance().getString(AppConstant.UID);
    return !TextUtils.isEmpty(uid);
  }
}
