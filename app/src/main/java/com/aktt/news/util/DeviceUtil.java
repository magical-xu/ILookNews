package com.aktt.news.util;

import android.annotation.SuppressLint;
import android.provider.Settings;
import com.aktt.news.ILookApplication;

/**
 * Created by magical on 2017/10/12.
 * Description :
 */

public class DeviceUtil {

    /**
     * 获取设备AndroidID
     *
     * @return AndroidID
     */
    @SuppressLint("HardwareIds")
    public static String getAndroidID() {
        return Settings.Secure.getString(ILookApplication.getAppContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }
}
