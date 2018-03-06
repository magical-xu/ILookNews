package com.aktt.news.util;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import java.sql.Timestamp;

/**
 * Created by magical on 2018/3/6.
 * Description :
 */

public class TimeUtil {

    public static long string2Timestamp(@NonNull String formatDate) {

        Timestamp ts;
        try {
            ts = Timestamp.valueOf(formatDate);
            return ts.getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static String getFormatString(String raw) {

        if (TextUtils.isEmpty(raw)) {
            return "";
        }

        long rawTimeStamp = string2Timestamp(raw);
        long nowTimeStamp = System.currentTimeMillis();
        Log.d("magical", "raw : " + rawTimeStamp);
        Log.d("magical", "now : " + nowTimeStamp);

        long diff = nowTimeStamp - rawTimeStamp;
        long secondDiff = diff / 1000;
        if (secondDiff < 60) {
            return "刚刚";
        }

        long minute = secondDiff / 60;
        if (minute < 60) {
            return String.format("%s分钟前", String.valueOf(minute));
        }

        long hour = minute / 60;
        if (hour < 24) {
            return String.format("%s小时前", String.valueOf(hour));
        }

        long day = hour / 24;
        if (day < 30) {
            return String.format("%s天前", String.valueOf(day));
        }

        long month = day / 30;
        if (month < 12) {
            return String.format("%s月前", String.valueOf(month));
        }

        long year = month / 12;
        return String.format("%s年前", String.valueOf(year));
    }
}
