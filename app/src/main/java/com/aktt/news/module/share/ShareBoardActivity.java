package com.aktt.news.module.share;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.View;
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
import com.aktt.news.library.glide.ImageLoader;
import com.aktt.news.net.callback.SimpleCallback;
import com.aktt.news.net.callback.SimpleRawJsonCallback;
import com.aktt.news.net.client.NetRequest;
import com.aktt.news.net.data.HttpResult;
import com.aktt.news.util.StringHelper;
import com.liulishuo.share.SsoShareManager;
import com.liulishuo.share.content.ShareContentWebPage;
import com.liulishuo.share.type.SsoShareType;
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

    private UserService service;
    private String SHARE_NID;
    private int NEWS_TYPE;

    public static void getInstance(Context context, String newsId, int newsType) {

        Intent intent = new Intent(context, ShareBoardActivity.class);
        intent.putExtra(AppConstant.PARAMS_NEWS_ID, newsId);
        intent.putExtra(AppConstant.PARAMS_NEWS_TYPE, newsType);
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
                ImageLoader.loadImage(imageUrl, mAdImage);
            }

            @Override
            public void onFail(String code, String errorMsg) {
                ToastUtils.showShort(errorMsg);
            }
        });
    }

    @OnClick({
            R.id.tv_share_wx_friend, R.id.tv_share_wx_circle, R.id.tv_share_qq, R.id.tv_share_qzone,
            R.id.tv_share_sina, R.id.tv_cancel
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
        }
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

                onShare(type, data);
            }

            @Override
            public void onFail(String code, String errorMsg) {
                ToastUtils.showShort(errorMsg);
            }
        });
    }

    public void onShare(String type, ShareData data) {

        data.title = "爱看头条";
        data.summary = "我们上线了";

        SsoShareManager.share(this, type,
                new ShareContentWebPage(data.title, data.summary, data.newsUrl, null, null),
                new SsoShareManager.ShareStateListener() {

                    @Override
                    public void onSuccess() {
                        super.onSuccess();
                        ToastUtils.showShort("分享成功");
                    }

                    @Override
                    public void onCancel() {
                        super.onCancel();
                        ToastUtils.showShort("分享取消");
                    }

                    @Override
                    public void onError(String msg) {
                        super.onError(msg);
                        ToastUtils.showShort("分享失败");
                    }
                });
    }
}
