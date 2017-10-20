package com.aktt.news.util;

import android.text.TextUtils;

/**
 * Created by magical on 2017/10/9.
 * Description : 保证字符串不为空
 */

public class StringHelper {

    public static String getString(String raw){
        return TextUtils.isEmpty(raw)?"":raw;
    }
}
