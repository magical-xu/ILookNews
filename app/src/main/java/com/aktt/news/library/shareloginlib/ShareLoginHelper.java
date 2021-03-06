package com.aktt.news.library.shareloginlib;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import com.aktt.news.R;
import com.aktt.news.data.ShareData;
import com.aktt.news.util.SimpleNotifyListener;
import com.liulishuo.share.ShareLoginSDK;
import com.liulishuo.share.SlConfig;
import com.liulishuo.share.SsoLoginManager;
import com.liulishuo.share.SsoShareManager;
import com.liulishuo.share.SsoUserInfoManager;
import com.liulishuo.share.content.ShareContentWebPage;

/**
 * Created by magical on 17/9/21.
 * Description :
 */

public class ShareLoginHelper {

    public static final String QQ_APP_ID = "101414852";
    public static final String QQ_SCOPE = "get_simple_userinfo";

    public static final String WX_APP_ID = "wx97a9b0c8570b0df9";
    public static final String WX_APP_SECRET = "9ac335291b56015b1dea76044c5a5774";

    public static final String WB_APP_ID = "3216227037";
    public static final String WB_REDIRECT_URL = "http://www.super1288.xyz/";
    public static final String WB_SCOPE = "email,direct_messages_read,direct_messages_write,"
            + "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
            + "follow_app_official_microblog,"
            + "invitation_write";

    public static void init(Application application) {

        SlConfig config = new SlConfig.Builder().debug(false)
                .appName(application.getString(R.string.app_name))
                .picTempFile(null) // 指定缓存缩略图的目录名字，如无特殊要求可以是null
                .qq(QQ_APP_ID, QQ_SCOPE)
                .weiXin(WX_APP_ID, WX_APP_SECRET)
                .weiBo(WB_APP_ID, WB_REDIRECT_URL, WB_SCOPE)
                .build();

        ShareLoginSDK.init(application, config);
    }

    public static void login(Activity context, String type, final ThirdLoginListener listener) {

        // 登录
        SsoLoginManager.login(context, type, new SsoLoginManager.LoginListener() {
            @Override
            public void onSuccess(String accessToken, String uId, long expiresIn,
                    @Nullable String wholeData) {
                super.onSuccess(accessToken, uId, expiresIn, wholeData); // must call super
                if (null != listener) {
                    listener.onSuccess(accessToken, uId, expiresIn, wholeData);
                }
            }

            @Override
            public void onCancel() {
                super.onCancel(); // must call super
                if (null != listener) {
                    listener.onCancel();
                }
            }

            @Override
            public void onError(String errorMsg) {
                super.onError(errorMsg); // must call super
                if (null != listener) {
                    listener.onError(errorMsg);
                }
            }
        });
    }

    public static void share(Activity context, String type, ShareData shareData,
            @Nullable Bitmap thumb, @Nullable Bitmap large, final SimpleNotifyListener listener) {

        // 分享
        SsoShareManager.share(context, type,
                new ShareContentWebPage(shareData.title, shareData.description, shareData.url,
                        thumb, large), new SsoShareManager.ShareStateListener() {
                    @Override
                    public void onSuccess() {
                        super.onSuccess(); // must call super
                        if (null != listener) {
                            listener.onSuccess("分享成功");
                        }
                    }

                    @Override
                    public void onCancel() {
                        super.onCancel(); // must call super
                        if (null != listener) {
                            listener.onFailed("分享取消");
                        }
                    }

                    @Override
                    public void onError(String msg) {
                        super.onError(msg); // must call super
                        if (null != listener) {
                            listener.onFailed("分享失败");
                        }
                    }
                });
    }

    public static void getUserInfo(Context context, String type, String token, String openId,
            SsoUserInfoManager.UserInfoListener listener) {
        SsoUserInfoManager.getUserInfo(context, type, token, openId, listener);
    }

    public interface ThirdLoginListener {

        void onSuccess(String accessToken, String uId, long expiresIn, @Nullable String wholeData);

        void onCancel();

        void onError(String msg);
    }
}
