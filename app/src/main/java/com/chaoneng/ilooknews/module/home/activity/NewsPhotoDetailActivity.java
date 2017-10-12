package com.chaoneng.ilooknews.module.home.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.OnClick;
import com.chaoneng.ilooknews.AppConstant;
import com.chaoneng.ilooknews.R;
import com.chaoneng.ilooknews.api.HomeService;
import com.chaoneng.ilooknews.base.BaseActivity;
import com.chaoneng.ilooknews.data.ImageInfo;
import com.chaoneng.ilooknews.data.NewsInfo;
import com.chaoneng.ilooknews.data.NewsInfoWrapper;
import com.chaoneng.ilooknews.instance.AccountManager;
import com.chaoneng.ilooknews.module.home.fragment.PhotoDetailFragment;
import com.chaoneng.ilooknews.net.callback.SimpleCallback;
import com.chaoneng.ilooknews.net.client.NetRequest;
import com.chaoneng.ilooknews.net.data.HttpResult;
import com.chaoneng.ilooknews.util.BottomHelper;
import com.chaoneng.ilooknews.util.CompatUtil;
import com.chaoneng.ilooknews.util.IntentHelper;
import com.chaoneng.ilooknews.util.SimpleNotifyListener;
import com.chaoneng.ilooknews.util.StringHelper;
import com.chaoneng.ilooknews.util.UserOptionHelper;
import com.chaoneng.ilooknews.widget.adapter.BaseFragmentAdapter;
import com.chaoneng.ilooknews.widget.adapter.OnPageChangeListener;
import com.chaoneng.ilooknews.widget.ilook.ILookTitleBar;
import com.chaoneng.ilooknews.widget.viewpager.ViewPagerFixed;
import com.jaeger.library.StatusBarUtil;
import com.magicalxu.library.blankj.KeyboardUtils;
import com.magicalxu.library.blankj.ToastUtils;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;
import retrofit2.Call;
import timber.log.Timber;

import static com.chaoneng.ilooknews.AppConstant.PARAMS_NEWS_ID;
import static com.chaoneng.ilooknews.AppConstant.PARAMS_NEWS_TYPE;

/**
 * Created by magical on 17/8/25.
 * Description : 图文详情界面
 */

public class NewsPhotoDetailActivity extends BaseActivity {

    private static final String TAG = "NewsPhotoDetailActivity";

    @BindView(R.id.view_pager) ViewPagerFixed mViewPager;
    @BindView(R.id.photo_detail_title_tv) TextView mContentTv;
    @BindView(R.id.id_fake_bottom) ViewGroup mFakeBottom;
    @BindView(R.id.id_real_bottom) ViewGroup mRealBottom;

    @BindView(R.id.id_bottom_edit) TextView mFakeEdit;
    @BindView(R.id.id_bottom_comment) ViewGroup mCommentView;
    @BindView(R.id.id_bottom_star) ImageView mStarView;
    @BindView(R.id.id_bottom_share) ImageView mShareView;
    @BindView(R.id.id_bottom_comment_count) TextView mCommentCountView;

    @BindView(R.id.id_real_bottom_edit) EditText mInputView;
    @BindView(R.id.id_send) TextView mSendView;

    private List<Fragment> mPhotoDetailFragmentList = new ArrayList<>();
    private List<String> mDesList = new ArrayList<>();
    private HomeService service;

    private String PAGE_NEWS_ID;
    private int PAGE_NEWS_TYPE;

    public static void getInstance(Context context, @NonNull String newsId, int newsType) {
        Intent intent = new Intent(context, NewsPhotoDetailActivity.class);
        intent.putExtra(PARAMS_NEWS_ID, newsId);
        intent.putExtra(PARAMS_NEWS_TYPE, newsType);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_news_photo_detail;
    }

    @Override
    protected boolean addTitleBar() {
        return true;
    }

    @Override
    public void handleChildPage(Bundle savedInstanceState) {

        checkIntent();
        StatusBarUtil.setColor(this, CompatUtil.getColor(this, R.color.black));
        loadData();
        checkTitle();
    }

    private void checkIntent() {

        Intent intent = getIntent();
        PAGE_NEWS_ID = intent.getStringExtra(PARAMS_NEWS_ID);
        PAGE_NEWS_TYPE = intent.getIntExtra(PARAMS_NEWS_TYPE, 0);
    }

    private void loadData() {

        service = NetRequest.getInstance().create(HomeService.class);
        showLoading();
        Call<HttpResult<NewsInfoWrapper>> call = service.getNewsDetail(PAGE_NEWS_ID, 3);
        call.enqueue(new SimpleCallback<NewsInfoWrapper>() {
            @Override
            public void onSuccess(NewsInfoWrapper data) {

                hideLoading();
                if (null == data) {
                    Timber.e("data is null");
                    return;
                }

                NewsInfo newInfo = data.newInfo;
                if (null == newInfo) {
                    Timber.e("news info is null.");
                    return;
                }

                if (newInfo.commentCount == 0) {
                    mCommentCountView.setVisibility(View.GONE);
                } else {
                    mCommentCountView.setVisibility(View.VISIBLE);
                    mCommentCountView.setText(String.valueOf(newInfo.commentCount));
                }

                config(newInfo.pictures);
            }

            @Override
            public void onFail(String code, String errorMsg) {
                onSimpleError(errorMsg);
            }
        });
    }

    private void checkTitle() {

        mTitleBar.setTitleBg(android.R.color.black)
                .setLeftImage(R.drawable.ic_back)
                .setRightImage(R.drawable.ic_more_white)
                .hideDivider()
                .setTitleListener(new ILookTitleBar.TitleCallbackAdapter() {
                    @Override
                    public void onClickLeft(View view) {
                        super.onClickLeft(view);
                        KeyboardUtils.hideSoftInput(NewsPhotoDetailActivity.this);
                        finish();
                    }

                    @Override
                    public void onClickRightImage(View view) {
                        super.onClickRightImage(view);
                        IntentHelper.openShareBottomPage(NewsPhotoDetailActivity.this, PAGE_NEWS_ID,
                                PAGE_NEWS_TYPE);
                    }
                });
    }

    private void initViewPager() {
        BaseFragmentAdapter photoPagerAdapter =
                new BaseFragmentAdapter(getSupportFragmentManager(), mPhotoDetailFragmentList);
        mViewPager.setAdapter(photoPagerAdapter);
        mViewPager.setOffscreenPageLimit(5);
        mViewPager.addOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                updateDescription(position);
            }
        });
        updateDescription(0);
    }

    private void config(List<ImageInfo> list) {
        mPhotoDetailFragmentList.clear();
        mDesList.clear();
        for (ImageInfo imageInfo : list) {
            PhotoDetailFragment fragment = new PhotoDetailFragment();
            Bundle bundle = new Bundle();
            bundle.putString(AppConstant.PHOTO_DETAIL_IMGSRC, imageInfo.picUrl);
            fragment.setArguments(bundle);
            mPhotoDetailFragmentList.add(fragment);

            mDesList.add(imageInfo.description);
        }

        initViewPager();
    }

    public void updateDescription(int position) {
        String title = mDesList.get(position);
        mContentTv.setText(
                getString(R.string.photo_detail_title, position + 1, mDesList.size(), title));
    }

    @OnClick(R.id.id_bottom_edit)
    public void onClickInput(View view) {

        BottomHelper.showKeyboard(mRealBottom, mFakeBottom, mInputView);
    }

    @OnClick(R.id.id_bottom_comment)
    public void openCommentPage(View view) {

        //CommentDialogFragment.newInstance().show(getSupportFragmentManager(), TAG);
        // 跳 一级评论页
        IntentHelper.openNewsCommentPage(this, PAGE_NEWS_ID, PAGE_NEWS_TYPE);
    }

    @OnClick(R.id.id_bottom_star)
    public void onClickStar(View view) {

        showLoading();
        UserOptionHelper.onClickStar(service, PAGE_NEWS_ID, PAGE_NEWS_TYPE,
                new SimpleNotifyListener() {
                    @Override
                    public void onSuccess(String msg) {
                        hideLoading();
                    }

                    @Override
                    public void onFailed(String msg) {
                        onSimpleError(msg);
                    }
                });
    }

    @OnClick(R.id.id_send)
    public void onSendComment(View view) {

        String comment = mInputView.getText().toString().trim();
        if (TextUtils.isEmpty(comment)) {
            ToastUtils.showShort(R.string.comment_can_not_be_null);
            return;
        }

        String userId = AccountManager.getInstance().getUserId();

        KeyboardUtils.hideSoftInput(this);
        showLoading();
        Call<HttpResult<JSONObject>> call =
                service.postNewsComment(StringHelper.getString(userId), PAGE_NEWS_ID,
                        PAGE_NEWS_TYPE, AppConstant.NONE_VALUE, comment);
        call.enqueue(new SimpleCallback<JSONObject>() {

            @Override
            public void onSuccess(JSONObject data) {
                hideLoading();
                onCommentSuccess();
            }

            @Override
            public void onFail(String code, String errorMsg) {
                onSimpleError(errorMsg);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (BottomHelper.isKeyboardShow(mRealBottom)) {
            BottomHelper.recoverNormalBottom(mRealBottom, mFakeBottom, mInputView);
            return;
        }
        super.onBackPressed();
    }

    /**
     * 评论成功
     */
    private void onCommentSuccess() {
        mInputView.setText("");
        ToastUtils.showShort("评论成功");
        onBackPressed();
    }
}
