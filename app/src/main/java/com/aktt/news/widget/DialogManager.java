package com.aktt.news.widget;

import android.app.Activity;
import android.content.Context;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

/**
 * Created by magical on 17/9/18.
 * Description :
 */

public class DialogManager {

    private Activity activity;
    private QMUITipDialog mDialog;
    private boolean isInitSuccess;

    public DialogManager(Context context) {

        if (context instanceof Activity) {
            activity = (Activity) context;
            isInitSuccess = true;
        } else {
            isInitSuccess = false;
        }
    }

    public void showDialog(String msg) {

        if (!isInitSuccess || null == activity || activity.isFinishing()) {
            return;
        }

        if (null == mDialog) {
            mDialog = new QMUITipDialog.Builder(activity).setIconType(
                    QMUITipDialog.Builder.ICON_TYPE_LOADING).setTipWord(msg).create();
            mDialog.setCanceledOnTouchOutside(true);
            mDialog.setCancelable(true);
        }

        if (!mDialog.isShowing()) {
            mDialog.show();
        }
    }

    public void dismissDialog() {
        if (null != mDialog && mDialog.isShowing()) {
            mDialog.dismiss();
            mDialog = null;
        }
    }

    public boolean isShowing() {
        if (null != mDialog) {
            return mDialog.isShowing();
        }
        return false;
    }
}
