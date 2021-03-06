package com.aktt.news.module.share;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.aktt.news.AppConstant;
import com.aktt.news.R;
import com.aktt.news.api.UserService;
import com.aktt.news.data.ShareData;
import com.aktt.news.instance.AccountManager;
import com.aktt.news.library.glide.GlideApp;
import com.aktt.news.library.glide.ImageLoader;
import com.aktt.news.library.shareloginlib.ShareLoginHelper;
import com.aktt.news.net.callback.SimpleCallback;
import com.aktt.news.net.callback.SimpleRawJsonCallback;
import com.aktt.news.net.client.NetRequest;
import com.aktt.news.net.data.HttpResult;
import com.aktt.news.util.IntentHelper;
import com.aktt.news.util.SimpleNotifyListener;
import com.aktt.news.util.StringHelper;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.liulishuo.share.type.SsoShareType;
import com.magicalxu.library.blankj.ClipboardUtils;
import com.magicalxu.library.blankj.ToastUtils;
import okhttp3.ResponseBody;
import org.json.JSONObject;
import retrofit2.Call;

/**
 * Created by magical on 17/9/21.
 * Description : 底部分享弹窗
 */

public class ShareBoardActivity extends Activity {

    @BindView(R.id.tv_share_wx_friend) TextView tvShareWxFriend;
    @BindView(R.id.tv_share_wx_circle) TextView tvShareWxCircle;
    @BindView(R.id.tv_share_qq) TextView tvShareQq;
    @BindView(R.id.tv_share_qzone) TextView tvShareQzone;
    @BindView(R.id.tv_share_sina) TextView tvShareSina;
    @BindView(R.id.tv_cancel) TextView tvCancel;
    @BindView(R.id.id_ad_image) ImageView mAdImage;
    @BindView(R.id.id_ad_container) ViewGroup mAdRoot;

    private UserService service;
    private String SHARE_NID;
    private int NEWS_TYPE;
    private String PUBLISHER;

    public static void getInstance(Context context, String newsId, int newsType, String publisher) {

        Intent intent = new Intent(context, ShareBoardActivity.class);
        intent.putExtra(AppConstant.PARAMS_NEWS_ID, newsId);
        intent.putExtra(AppConstant.PARAMS_NEWS_TYPE, newsType);
        intent.putExtra(AppConstant.PARAMS_USER_ID, publisher);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.BOTTOM;
        getWindow().setAttributes(lp);

        setContentView(R.layout.activity_share_board);
        ButterKnife.bind(this);

        handleChildPage();
    }

    public void handleChildPage() {

        Intent intent = getIntent();
        SHARE_NID = intent.getStringExtra(AppConstant.PARAMS_NEWS_ID);
        NEWS_TYPE = intent.getIntExtra(AppConstant.PARAMS_NEWS_TYPE, AppConstant.INVALIDATE);
        PUBLISHER = intent.getStringExtra(AppConstant.PARAMS_USER_ID);

        service = NetRequest.getInstance().create(UserService.class);

        loadAdImage();
    }

    /**
     * 加载广告图
     */
    private void loadAdImage() {

        Call<ResponseBody> call = service.getShareAdImage();
        call.enqueue(new SimpleRawJsonCallback() {
            @Override
            public void onSuccess(JSONObject data) {

                String imageUrl = data.optString("picUrl");
                if (TextUtils.isEmpty(imageUrl)) {
                    mAdRoot.setVisibility(View.GONE);
                } else {
                    ImageLoader.loadImage(imageUrl, mAdImage);
                }
            }

            @Override
            public void onFail(String code, String errorMsg) {
                ToastUtils.showShort(errorMsg);
            }
        });
    }

    @OnClick({
            R.id.tv_share_wx_friend, R.id.tv_share_wx_circle, R.id.tv_share_qq, R.id.tv_share_qzone,
            R.id.tv_share_sina, R.id.tv_cancel, R.id.tv_copy_url, R.id.tv_report
    })
    public void onViewClicked(View view) {

        if (AccountManager.getInstance().checkLogin(this)) {
            return;
        }

        switch (view.getId()) {
            case R.id.tv_share_wx_friend:
                onGetShareContent(SsoShareType.WEIXIN_FRIEND);
                break;
            case R.id.tv_share_wx_circle:
                onGetShareContent(SsoShareType.WEIXIN_FRIEND_ZONE);
                break;
            case R.id.tv_share_qq:
                onGetShareContent(SsoShareType.QQ_FRIEND);
                break;
            case R.id.tv_share_qzone:
                onGetShareContent(SsoShareType.QQ_ZONE);
                break;
            case R.id.tv_share_sina:
                onGetShareContent(SsoShareType.WEIBO_TIME_LINE);
                break;
            case R.id.tv_cancel:
                finish();
                break;
            case R.id.tv_copy_url:
                onCopyNewsUrl();
                break;
            case R.id.tv_report:
                onReportUrl();
                break;
        }
    }

    /**
     * 复制链接
     */
    private void onCopyNewsUrl() {

        String userId = AccountManager.getInstance().getUserId();
        Call<HttpResult<ShareData>> call =
                service.getShare(StringHelper.getString(userId), SHARE_NID, 2, NEWS_TYPE);
        call.enqueue(new SimpleCallback<ShareData>() {
            @Override
            public void onSuccess(ShareData data) {

                copyToClipBoard(data);
            }

            @Override
            public void onFail(String code, String errorMsg) {
                ToastUtils.showShort(errorMsg);
            }
        });
    }

    private void copyToClipBoard(ShareData data) {

        ClipboardUtils.copyText(StringHelper.getString(data.url));
        ToastUtils.showShort("已经复制到剪贴板");
    }

    /**
     * 举报新闻
     */
    private void onReportUrl() {

        if (AccountManager.getInstance().checkLogin(this)) {
            return;
        }

        IntentHelper.openReportPage(this, PUBLISHER, SHARE_NID, NEWS_TYPE);
    }

    /**
     * 1 微博 2 qq 3 微信
     *
     * @param type SsoShareType
     */
    private void onGetShareContent(final String type) {

        int shareType;
        switch (type) {
            case SsoShareType.QQ_FRIEND:
            case SsoShareType.QQ_ZONE:
                shareType = 2;
                break;
            case SsoShareType.WEIXIN_FRIEND:
            case SsoShareType.WEIXIN_FRIEND_ZONE:
                shareType = 3;
                break;
            case SsoShareType.WEIBO_TIME_LINE:
                shareType = 1;
                break;
            default:
                shareType = AppConstant.INVALIDATE;
                break;
        }

        String userId = AccountManager.getInstance().getUserId();
        Call<HttpResult<ShareData>> call =
                service.getShare(StringHelper.getString(userId), SHARE_NID, shareType, NEWS_TYPE);
        call.enqueue(new SimpleCallback<ShareData>() {
            @Override
            public void onSuccess(ShareData data) {

                downLoadImage(type, data);
            }

            @Override
            public void onFail(String code, String errorMsg) {
                ToastUtils.showShort(errorMsg);
            }
        });
    }

    private void downLoadImage(final String type, final ShareData data) {

        //final String url = data.gtt;

        GlideApp.with(this).asBitmap().dontAnimate().load(adaptUrl(data))
                .into(new SimpleTarget<Bitmap>() {

                    @Override
                    public void onResourceReady(Bitmap resource,
                            Transition<? super Bitmap> transition) {

                        //if (!TextUtils.isEmpty(url)) {
                        //    downLoadNewsImage(type, data, resource);
                        //} else {
                        onShare(type, data, resource, null);
                        //}
                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        super.onLoadFailed(errorDrawable);
                        ToastUtils.showShort("获取分享缩略图失败 --> 错误的图片链接");
                    }
                });
    }

    private String adaptUrl(ShareData data) {

        String rawUrl = data.img;
        if (rawUrl.endsWith("\"")) {
            int length = rawUrl.length();
            rawUrl = rawUrl.substring(0, length - 1);
        }

        return rawUrl;
    }

    private void downLoadNewsImage(final String type, final ShareData data, final Bitmap thumb) {

        String url = data.gtt;
        GlideApp.with(this).asBitmap().dontAnimate().load(url).into(new SimpleTarget<Bitmap>() {

            @Override
            public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {

                onShare(type, data, thumb, resource);
            }
        });
    }

    public void onShare(String type, final ShareData data, Bitmap thumb, Bitmap large) {

        if (TextUtils.isEmpty(data.title)) {
            data.title = "趣看看";
        }

        if (TextUtils.isEmpty(data.description)) {
            data.description = "趣看看";
        }

        ShareLoginHelper.share(this, type, data, thumb, large, new SimpleNotifyListener() {
            @Override
            public void onSuccess(String msg) {
                ToastUtils.showShort(msg);
            }

            @Override
            public void onFailed(String msg) {
                ToastUtils.showShort(msg);
            }
        });
    }
}
