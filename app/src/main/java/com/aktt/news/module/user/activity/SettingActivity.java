package com.aktt.news.module.user.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.OnClick;
import com.aktt.news.AppConstant;
import com.aktt.news.R;
import com.aktt.news.base.BaseActivity;
import com.aktt.news.instance.AccountManager;
import com.aktt.news.module.user.widget.SettingItemView;
import com.aktt.news.util.DataCleanUtil;
import com.aktt.news.util.IntentHelper;
import com.aktt.news.widget.ilook.ILookTitleBar;
import com.magicalxu.library.blankj.SPUtils;
import com.magicalxu.library.blankj.ToastUtils;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;

/**
 * Created by magical on 2017/8/19.
 * 设置界面
 */

public class SettingActivity extends BaseActivity {

    @BindView(R.id.id_edit) SettingItemView idEdit;
    @BindView(R.id.id_toggle_text) SettingItemView idToggleText;
    @BindView(R.id.id_clear_cache) SettingItemView idClearCache;
    @BindView(R.id.id_wifi) SettingItemView idWifi;
    @BindView(R.id.id_agreement) SettingItemView idAgreement;
    @BindView(R.id.id_version) SettingItemView idVersion;
    @BindView(R.id.id_about) SettingItemView idAbout;
    @BindView(R.id.tv_logout) TextView tvLogout;

    private QMUIDialog mClearDialog;
    private Handler handler;

    private String[] sizeArray = new String[] { "小", "中", "大" };

    @Override
    public int getLayoutId() {
        return R.layout.activity_user_setting;
    }

    @Override
    protected boolean addTitleBar() {
        return true;
    }

    @Override
    public void handleChildPage(Bundle savedInstanceState) {

        init();
    }

    private void init() {

        mTitleBar.setTitle("设置");
        mTitleBar.setTitleListener(new ILookTitleBar.TitleCallbackAdapter() {
            @Override
            public void onClickLeft(View view) {
                super.onClickLeft(view);
                finish();
            }
        });

        idEdit.setTitle("个人资料编辑");

        idToggleText.setTitle("字体大小").hideRightArrow();
        setSystemTextSize(SPUtils.getInstance().getInt(AppConstant.NEWS_TEXT_SIZE, 1));

        idClearCache.setTitle("清除缓存").hideRightArrow();

        idWifi.setTitle("非 WIFI 网络播放提醒").hideRightArrow().showToggle();
        idWifi.getToggle().setChecked(SPUtils.getInstance().getBoolean(AppConstant.WIFI_4G_TIP));
        idWifi.getToggle().setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                ToastUtils.showShort(b ? "已开启WIFI播放提醒" : "已关闭WIFI播放提醒");
                SPUtils.getInstance().put(AppConstant.WIFI_4G_TIP, b);
            }
        });

        idAgreement.setTitle("系统协议");

        idVersion.setTitle("当前版本").setRightText("v1.0.0").hideRightArrow();

        idAbout.setTitle("关于我们");

        handler = new Handler(getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                checkCacheSize();
                hideLoading();
            }
        };

        checkCacheSize();
    }

    @OnClick({
            R.id.id_edit, R.id.id_toggle_text, R.id.id_clear_cache, R.id.id_agreement,
            R.id.id_about, R.id.tv_logout
    })
    public void onClickView(View view) {

        switch (view.getId()) {
            case R.id.id_edit:
                if (AccountManager.getInstance().checkLogin(this)) {
                    return;
                }
                IntentHelper.openProfilePage(this);
                break;
            case R.id.id_toggle_text:
                showTextSizeDialog();
                break;
            case R.id.id_clear_cache:
                showClearDialog();
                break;
            case R.id.id_agreement:
                ToastUtils.showShort("用户协议");
                break;
            case R.id.id_about:
                ToastUtils.showShort("关于");
                break;
            case R.id.tv_logout:
                AccountManager.getInstance().logout();
                IntentHelper.openLoginPage(this);
                break;
        }
    }

    private void showTextSizeDialog() {

        new QMUIDialog.CheckableDialogBuilder(this).setTitle("字体大小")
                .addItems(sizeArray, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        onChangeTextSize(i);
                    }
                })
                .setCheckedIndex(SPUtils.getInstance().getInt(AppConstant.NEWS_TEXT_SIZE, 1))
                .addAction("取消", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    /**
     * 设置新闻系统字体大小
     */
    private void onChangeTextSize(int index) {

        setSystemTextSize(index);
        SPUtils.getInstance().put(AppConstant.NEWS_TEXT_SIZE, index);
    }

    private void setSystemTextSize(int index) {
        String size;
        if (index == 0) {
            size = "小";
        } else if (index == 1) {
            size = "中";
        } else {
            size = "大";
        }
        idToggleText.setRightText(size);
    }

    private void checkCacheSize() {
        try {
            String totalCacheSize = DataCleanUtil.getTotalCacheSize(this);
            if (!TextUtils.isEmpty(totalCacheSize)) {
                idClearCache.setRightText(totalCacheSize);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showClearDialog() {

        if (null == mClearDialog) {
            mClearDialog = new QMUIDialog.MessageDialogBuilder(this).setTitle("提示")
                    .setMessage("确定清除所有缓存吗？离线内容及图片均会被清除。")
                    .addAction("取消", new QMUIDialogAction.ActionListener() {
                        @Override
                        public void onClick(QMUIDialog dialog, int index) {
                            dialog.dismiss();
                        }
                    })
                    .addAction("确定", new QMUIDialogAction.ActionListener() {
                        @Override
                        public void onClick(QMUIDialog dialog, int index) {
                            dialog.dismiss();
                            clearCache();
                        }
                    })
                    .create();
        }
        mClearDialog.show();
    }

    private void clearCache() {
        showLoading("正在清理");
        DataCleanUtil.clearAllCache(this);
        handler.sendEmptyMessageDelayed(0, 1500);
    }
}
