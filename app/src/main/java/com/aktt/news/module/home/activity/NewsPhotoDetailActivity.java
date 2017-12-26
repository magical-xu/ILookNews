package com.aktt.news.module.home.activity;

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
import com.aktt.news.AppConstant;
import com.aktt.news.R;
import com.aktt.news.api.HomeService;
import com.aktt.news.api.UserService;
import com.aktt.news.base.BaseActivity;
import com.aktt.news.data.ImageInfo;
import com.aktt.news.data.NewsInfo;
import com.aktt.news.data.NewsInfoWrapper;
import com.aktt.news.instance.AccountManager;
import com.aktt.news.module.home.fragment.PhotoDetailFragment;
import com.aktt.news.net.callback.SimpleCallback;
import com.aktt.news.net.client.NetRequest;
import com.aktt.news.net.data.HttpResult;
import com.aktt.news.util.BottomHelper;
import com.aktt.news.util.CompatUtil;
import com.aktt.news.util.IntentHelper;
import com.aktt.news.util.SimpleNotifyListener;
import com.aktt.news.util.SimplePreNotifyListener;
import com.aktt.news.util.StringHelper;
import com.aktt.news.util.UserOptionHelper;
import com.aktt.news.widget.adapter.BaseFragmentAdapter;
import com.aktt.news.widget.adapter.OnPageChangeListener;
import com.aktt.news.widget.ilook.ILookTitleBar;
import com.aktt.news.widget.viewpager.ViewPagerFixed;
import com.githang.statusbar.StatusBarCompat;
import com.magicalxu.library.blankj.KeyboardUtils;
import com.magicalxu.library.blankj.ToastUtils;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;
import retrofit2.Call;
import timber.log.Timber;

import static com.aktt.news.AppConstant.PARAMS_NEWS_ID;
import static com.aktt.news.AppConstant.PARAMS_NEWS_TYPE;

/**
 * Created by magical on 17/8/25.
 * Description : 图文详情界面
 */

public class NewsPhotoDetailActivity extends BaseActivity {

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
    private UserService userService;

    private String PAGE_NEWS_ID;
    private int PAGE_NEWS_TYPE;
    private String PAGE_UID;

    private boolean hasCollected;
    private boolean hasFollowed;

    private ArrayList<Call> callList;

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

        StatusBarCompat.setStatusBarColor(this, CompatUtil.getColor(this, R.color.black));
        checkIntent();
        checkTitle();

        service = NetRequest.getInstance().create(HomeService.class);
        userService = NetRequest.getInstance().create(UserService.class);
        loadData();
    }

    private void checkIntent() {

        Intent intent = getIntent();
        PAGE_NEWS_ID = intent.getStringExtra(PARAMS_NEWS_ID);
        PAGE_NEWS_TYPE = intent.getIntExtra(PARAMS_NEWS_TYPE, 0);
    }

    private void loadData() {

        String userId = AccountManager.getInstance().getUserId();
        showLoading();
        final Call<HttpResult<NewsInfoWrapper>> call =
                service.getNewsDetail(StringHelper.getString(userId), PAGE_NEWS_ID, 3);
        onAddCall(call);
        call.enqueue(new SimpleCallback<NewsInfoWrapper>() {
            @Override
            public void onSuccess(NewsInfoWrapper data) {

                hideLoading();
                onRemoveCall(call);
                if (null == data) {
                    Timber.e("data is null");
                    return;
                }

                NewsInfo newInfo = data.newInfo;
                if (null == newInfo) {
                    Timber.e("news info is null.");
                    return;
                } else {
                    hasCollected = TextUtils.equals(AppConstant.HAS_PRAISE, newInfo.isCollection);
                    hasFollowed = TextUtils.equals(AppConstant.USER_FOLLOW, newInfo.isFollow);
                    setFollowState();
                    PAGE_UID = newInfo.userid;
                    mTitleBar.setHeadImage(StringHelper.getString(newInfo.userIcon));
                    changeStarState(hasCollected);
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
                onRemoveCall(call);
                onSimpleError(errorMsg);
            }
        });
    }

    private void checkTitle() {

        mTitleBar.setTitleBg(android.R.color.black)
                .setLeftImage(R.drawable.ic_back)
                .setRightImage(R.drawable.ic_more_white)
                .hideDivider()
                .openImageMode()
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
                                PAGE_NEWS_TYPE, PAGE_UID);
                    }

                    @Override
                    public void onClickAvatar(View view) {
                        super.onClickAvatar(view);
                        onJumpToUserPage();
                    }

                    @Override
                    public void onClickFocus(View view) {
                        super.onClickFocus(view);
                        onOptFocus();
                    }
                });
    }

    private void onJumpToUserPage() {

        if (TextUtils.isEmpty(PAGE_UID)) {
            return;
        }

        IntentHelper.openUserCenterPage(this, PAGE_UID);
    }

    private void onOptFocus() {

        if (hasFollowed) {
            // cancel follow
            UserOptionHelper.onCancelFollow(userService, PAGE_UID, new SimpleNotifyListener() {
                @Override
                public void onSuccess(String msg) {

                    hasFollowed = false;
                    setFollowState();
                }

                @Override
                public void onFailed(String msg) {
                    onSimpleError(msg);
                }
            });
        } else {
            // follow
            UserOptionHelper.onFollow(userService, PAGE_UID, new SimpleNotifyListener() {
                @Override
                public void onSuccess(String msg) {

                    hasFollowed = true;
                    setFollowState();
                }

                @Override
                public void onFailed(String msg) {
                    onSimpleError(msg);
                }
            });
        }
    }

    private void setFollowState() {
        mTitleBar.setFocusText(hasFollowed ? "已关注" : "关注");
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

        if (AccountManager.getInstance().checkLogin(this)) {
            return;
        }

        showLoading();
        if (hasCollected) {
            UserOptionHelper.onCancelStar(service, PAGE_NEWS_ID, PAGE_NEWS_TYPE,
                    new SimplePreNotifyListener() {
                        @Override
                        public void onPreToDo() {
                        }

                        @Override
                        public void onSuccess(String msg) {
                            hideLoading();
                            changeStarState(false);
                        }

                        @Override
                        public void onFailed(String msg) {
                            onSimpleError(msg);
                        }
                    });
        } else {
            UserOptionHelper.onClickStar(service, PAGE_NEWS_ID, PAGE_NEWS_TYPE,
                    new SimplePreNotifyListener() {

                        @Override
                        public void onPreToDo() {
                        }

                        @Override
                        public void onSuccess(String msg) {
                            hideLoading();
                            changeStarState(true);
                        }

                        @Override
                        public void onFailed(String msg) {
                            onSimpleError(msg);
                        }
                    });
        }
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

    @Override
    public ArrayList<Call> addRequestList() {
        callList = new ArrayList<>();
        return callList;
    }

    /**
     * 评论成功
     */
    private void onCommentSuccess() {
        mInputView.setText("");
        ToastUtils.showShort("评论成功");
        onBackPressed();
    }

    /**
     * 更新 收藏状态
     */
    private void changeStarState(boolean collected) {
        hasCollected = collected;
        mStarView.setSelected(hasCollected);
    }
}
