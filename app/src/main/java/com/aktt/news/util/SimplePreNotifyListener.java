package com.aktt.news.util;

/**
 * Created by magical on 2017/10/12.
 * Description :
 */

public interface SimplePreNotifyListener {

    void onPreToDo();

    void onSuccess(String msg);

    void onFailed(String msg);
}
