package com.chaoneng.ilooknews.base;

import android.support.v4.app.DialogFragment;
import com.chaoneng.ilooknews.widget.DialogManager;
import com.magicalxu.library.blankj.ToastUtils;

/**
 * Created by magical on 17/9/20.
 * Description :
 */

public class BaseDialogFragment extends DialogFragment {

    private DialogManager mDialogManager;

    @Override
    public void onDestroy() {
        if (null != mDialogManager) {
            mDialogManager.dismissDialog();
            mDialogManager = null;
        }
        super.onDestroy();
    }

    private void ensureDialogManager() {
        if (null == mDialogManager) {
            mDialogManager = new DialogManager(getActivity());
        }
    }

    public void showLoading(String msg) {
        ensureDialogManager();
        mDialogManager.showDialog(msg);
    }

    public void showLoading() {
        ensureDialogManager();
        mDialogManager.showDialog("正在加载");
    }

    public void hideLoading() {
        ensureDialogManager();
        mDialogManager.dismissDialog();
    }

    /*************** 以下为方便调用 ***************************/
    protected void onSimpleError(String msg) {
        hideLoading();
        ToastUtils.showShort(msg);
    }
}
