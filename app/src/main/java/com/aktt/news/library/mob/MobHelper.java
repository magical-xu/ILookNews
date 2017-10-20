package com.aktt.news.library.mob;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

/**
 * Created by magical on 17/9/26.
 * Description : mob sdk 帮助类
 */

public class MobHelper {

    private MobHelper() {
    }

    private static class SingletonHolder {
        private static MobHelper instance = new MobHelper();
    }

    public static MobHelper getInstance() {
        return SingletonHolder.instance;
    }

    public static void register(EventHandler eventHandler) {
        SMSSDK.registerEventHandler(eventHandler);
    }

    public static void unRegister(EventHandler eventHandler) {
        SMSSDK.registerEventHandler(eventHandler);
    }

    /**
     * 获取短信验证码
     */
    public void getVerifyCode(String mobile) {

        SMSSDK.getVerificationCode("86", mobile);
    }

    public void submitVerifyCode(String mobile, String verifyCode) {

        SMSSDK.submitVerificationCode("86", mobile, verifyCode);
    }
}
