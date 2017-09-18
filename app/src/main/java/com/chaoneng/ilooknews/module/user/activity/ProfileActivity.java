package com.chaoneng.ilooknews.module.user.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import butterknife.BindView;
import butterknife.OnClick;
import com.bilibili.boxing.Boxing;
import com.bilibili.boxing.model.config.BoxingConfig;
import com.bilibili.boxing.model.entity.BaseMedia;
import com.bilibili.boxing_impl.ui.BoxingActivity;
import com.chaoneng.ilooknews.R;
import com.chaoneng.ilooknews.api.UserService;
import com.chaoneng.ilooknews.base.BaseActivity;
import com.chaoneng.ilooknews.data.BaseUser;
import com.chaoneng.ilooknews.instance.AccountManager;
import com.chaoneng.ilooknews.module.user.widget.SettingItemView;
import com.chaoneng.ilooknews.net.callback.SimpleCallback;
import com.chaoneng.ilooknews.net.client.NetRequest;
import com.chaoneng.ilooknews.net.data.HttpResult;
import com.chaoneng.ilooknews.widget.ilook.ILookTitleBar;
import com.magicalxu.library.blankj.EmptyUtils;
import com.magicalxu.library.blankj.KeyboardUtils;
import com.magicalxu.library.blankj.ToastUtils;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONObject;
import retrofit2.Call;

/**
 * Created by magical on 17/8/20.
 * Description : 编辑资料界面
 */

public class ProfileActivity extends BaseActivity {

    @BindView(R.id.id_modify_avatar) SettingItemView modifyAvatar;
    @BindView(R.id.id_modify_nick) SettingItemView modifyNick;
    @BindView(R.id.id_modify_sign) SettingItemView modifySign;

    private UserService service;

    @Override
    public int getLayoutId() {
        return R.layout.activity_profile;
    }

    @Override
    protected boolean addTitleBar() {
        return true;
    }

    @Override
    public void handleChildPage(Bundle savedInstanceState) {

        service = NetRequest.getInstance().create(UserService.class);

        mTitleBar.setTitle("编辑资料");
        mTitleBar.setTitleListener(new ILookTitleBar.TitleCallbackAdapter() {
            @Override
            public void onClickLeft(View view) {
                super.onClickLeft(view);
                finish();
            }
        });

        AccountManager accountManager = AccountManager.getInstance();
        BaseUser user = accountManager.getUser();
        if (null != user) {
            modifyAvatar.setHead(user.avatar);
            modifyNick.setRightText(EmptyUtils.isEmpty(user.username) ? "" : user.username);
            modifySign.setRightText(EmptyUtils.isEmpty(user.introduce) ? "" : user.introduce);
        }

        // init ui
        modifyAvatar.setTitle("修改头像");
        modifyNick.setTitle("修改昵称");
        modifySign.setTitle("签名");
    }

    @OnClick({ R.id.id_modify_avatar, R.id.id_modify_nick, R.id.id_modify_sign })
    public void onClickView(View view) {

        switch (view.getId()) {
            case R.id.id_modify_avatar:
                BoxingConfig config = new BoxingConfig(
                        BoxingConfig.Mode.SINGLE_IMG); // Mode：Mode.SINGLE_IMG, Mode.MULTI_IMG, Mode.VIDEO
                Boxing.of(config).withIntent(this, BoxingActivity.class).start(this, 1024);

                break;
            case R.id.id_modify_nick:
                showNickDialog();
                break;
            case R.id.id_modify_sign:
                showSignDialog();
                break;
        }
    }

    private void showSignDialog() {

        final QMUIDialog.EditTextDialogBuilder builder = new QMUIDialog.EditTextDialogBuilder(this);
        final QMUIDialog rawDialog = builder.setTitle("修改签名")
                .setPlaceholder("在此输入您的签名")
                .setInputType(InputType.TYPE_CLASS_TEXT)
                .addAction("取消", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        KeyboardUtils.hideSoftInput(dialog.getCurrentFocus());
                        dialog.dismiss();
                    }
                })
                .addAction("确定", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        String text = builder.getEditText().getText().toString();
                        if (!TextUtils.isEmpty(text)) {
                            modifySignature(text);
                            KeyboardUtils.hideSoftInput(dialog.getCurrentFocus());
                            dialog.dismiss();
                        } else {
                            ToastUtils.showShort("请填入签名");
                        }
                    }
                })
                .show();
        rawDialog.setCanceledOnTouchOutside(false);
    }

    private void showNickDialog() {

        final QMUIDialog.EditTextDialogBuilder builder = new QMUIDialog.EditTextDialogBuilder(this);
        final QMUIDialog rawDialog = builder.setTitle("修改昵称")
                .setPlaceholder("在此输入您的昵称")
                .setInputType(InputType.TYPE_CLASS_TEXT)
                .addAction("取消", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        KeyboardUtils.hideSoftInput(dialog.getCurrentFocus());
                        dialog.dismiss();
                    }
                })
                .addAction("确定", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        String text = builder.getEditText().getText().toString();
                        if (!TextUtils.isEmpty(text)) {
                            modifyNickName(text);
                            KeyboardUtils.hideSoftInput(dialog.getCurrentFocus());
                            dialog.dismiss();
                        } else {
                            ToastUtils.showShort("请填入昵称");
                        }
                    }
                })
                .show();

        rawDialog.setCanceledOnTouchOutside(false);
    }

    /**
     * 修改昵称
     */
    private void modifyNickName(final String text) {

        AccountManager accountManager = AccountManager.getInstance();
        if (accountManager.hasLogin()) {
            String uid = accountManager.getUserId();
            Map<String, String> params = new HashMap<>();
            params.put(UserService.PARAMS_NICK, text);
            Call<HttpResult<JSONObject>> call = service.modifyUserInfo(uid, 2, params);
            call.enqueue(new SimpleCallback<JSONObject>() {
                @Override
                public void onSuccess(JSONObject data) {
                    ToastUtils.showShort("更改昵称成功");
                    modifyNick.setRightText(text);
                }

                @Override
                public void onFail(String code, String errorMsg) {
                    ToastUtils.showShort(errorMsg);
                }
            });
        }
    }

    /**
     * 修改签名
     */
    private void modifySignature(final String text) {

        AccountManager accountManager = AccountManager.getInstance();
        if (accountManager.hasLogin()) {
            String uid = accountManager.getUserId();
            Map<String, String> params = new HashMap<>();
            params.put(UserService.PARAMS_NICK, text);
            Call<HttpResult<JSONObject>> call = service.modifyUserInfo(uid, 2, params);
            call.enqueue(new SimpleCallback<JSONObject>() {
                @Override
                public void onSuccess(JSONObject data) {
                    ToastUtils.showShort("更改签名成功");
                    modifySign.setRightText(text);
                }

                @Override
                public void onFail(String code, String errorMsg) {
                    ToastUtils.showShort(errorMsg);
                }
            });
        }
    }

    /**
     * 修改头像
     */
    private void modifyAvatar(String avatarUrl) {

        AccountManager accountManager = AccountManager.getInstance();
        if (accountManager.hasLogin()) {
            String uid = accountManager.getUserId();
            Map<String, String> params = new HashMap<>();
            params.put(UserService.PARAMS_ICON, avatarUrl);
            Call<HttpResult<JSONObject>> call = service.modifyUserInfo(uid, 1, params);
            call.enqueue(new SimpleCallback<JSONObject>() {
                @Override
                public void onSuccess(JSONObject data) {
                    ToastUtils.showShort("更改头像成功");
                }

                @Override
                public void onFail(String code, String errorMsg) {
                    ToastUtils.showShort(errorMsg);
                }
            });
        }
    }

    /**
     * 上传图片
     */
    private void uploadImage(@NonNull BaseMedia baseMedia) {
        modifyAvatar.setHead(baseMedia.getPath());
        // TODO: 17/9/13 上传头像图片 拿到地址
        //modifyAvatar(baseMedia.getPath());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        List<BaseMedia> medias = Boxing.getResult(data);
        if (null != medias) {
            ToastUtils.showShort(medias.size() + "");
            BaseMedia baseMedia = medias.get(0);
            if (null != baseMedia) {
                uploadImage(baseMedia);
            }
        }
    }
}
