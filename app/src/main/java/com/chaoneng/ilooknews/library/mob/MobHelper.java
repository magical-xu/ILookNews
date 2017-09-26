package com.chaoneng.ilooknews.library.mob;

import android.content.Context;
import android.widget.Toast;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

/**
 * Created by magical on 17/9/26.
 * Description :
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

    public void testHandler(final Context context) {

        EventHandler handler = new EventHandler() {

            @Override
            public void onRegister() {
                super.onRegister();
            }

            @Override
            public void beforeEvent(int i, Object o) {
                super.beforeEvent(i, o);
            }

            @Override
            public void afterEvent(int event, int result, Object data) {
                super.afterEvent(event, result, data);
                if (data instanceof Throwable) {
                    Throwable throwable = (Throwable) data;
                    String msg = throwable.getMessage();
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                } else {
                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                        //提交验证码成功
                    } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                        //获取验证码成功
                        if (result == SMSSDK.RESULT_COMPLETE) {
                            boolean smart = (Boolean) data;
                            if (smart) {
                                //通过智能验证
                            } else {
                                //依然走短信验证
                            }
                        }
                    } else if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {
                        //返回支持发送验证码的国家列表

                    }
                }
            }

            @Override
            public void onUnregister() {
                super.onUnregister();
            }
        };
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

    public void getSupportCountry() {

        SMSSDK.getSupportedCountries();
    }
}
