package com.chaoneng.ilooknews.module.home.activity;

import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.OnClick;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chaoneng.ilooknews.AppConstant;
import com.chaoneng.ilooknews.R;
import com.chaoneng.ilooknews.api.HomeService;
import com.chaoneng.ilooknews.base.BaseActivity;
import com.chaoneng.ilooknews.data.CommentBean;
import com.chaoneng.ilooknews.data.NewsInfoWrapper;
import com.chaoneng.ilooknews.instance.AccountManager;
import com.chaoneng.ilooknews.module.home.fragment.CommentDialogFragment;
import com.chaoneng.ilooknews.module.video.adapter.CommentAdapter;
import com.chaoneng.ilooknews.net.callback.SimpleCallback;
import com.chaoneng.ilooknews.net.client.NetRequest;
import com.chaoneng.ilooknews.net.data.HttpResult;
import com.chaoneng.ilooknews.util.RefreshHelper;
import com.chaoneng.ilooknews.util.StringHelper;
import com.chaoneng.ilooknews.widget.ilook.ILookTitleBar;
import com.magicalxu.library.blankj.KeyboardUtils;
import com.magicalxu.library.blankj.ToastUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import org.json.JSONObject;
import retrofit2.Call;

import static com.chaoneng.ilooknews.AppConstant.PARAMS_NEWS_ID;
import static com.chaoneng.ilooknews.AppConstant.PARAMS_NEWS_TYPE;

/**
 * Created by magical on 17/9/19.
 * Description : 一级评论界面
 */

public class CommentActivity extends BaseActivity {

    private static final String TAG = "CommentActivity";

    @BindView(R.id.id_recycler) RecyclerView mRecyclerView;
    @BindView(R.id.id_refresh_layout) SmartRefreshLayout mRefreshLayout;
    @BindView(R.id.id_edit) EditText mInputView;
    @BindView(R.id.id_send) TextView mSendBtn;

    private CommentAdapter mAdapter;
    private RefreshHelper<CommentBean> mRefreshHelper;
    private HomeService homeService;

    private String PAGE_NEWS_ID;
    private int PAGE_NEWS_TYPE;
    private View mEmptyView;

    public static void getInstance(Context context, @NonNull String newsId, int newsType) {
        Intent intent = new Intent(context, CommentActivity.class);
        intent.putExtra(PARAMS_NEWS_ID, newsId);
        intent.putExtra(PARAMS_NEWS_TYPE, newsType);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_news_detail;
    }

    @Override
    protected boolean addTitleBar() {
        return true;
    }

    @Override
    public void handleChildPage(Bundle savedInstanceState) {

        checkIntent();
        checkTitle();
        homeService = NetRequest.getInstance().create(HomeService.class);

        mAdapter = new CommentAdapter(false, R.layout.item_video_comment);
        mRefreshHelper = new RefreshHelper<CommentBean>(mRefreshLayout, mAdapter, mRecyclerView) {
            @Override
            public void onRequest(int page) {
                loadComment(page);
            }
        };

        mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()) {
                    case R.id.id_comment_count:
                        CommentBean commentBean = mAdapter.getData().get(position);

                        //int commentCount = commentBean.commentCount;
                        //if (0 == commentCount) {
                        //    return;
                        //}

                        String commentId = commentBean.cid;
                        CommentDialogFragment fragment =
                                CommentDialogFragment.newInstance(PAGE_NEWS_ID, PAGE_NEWS_TYPE,
                                        commentId);
                        fragment.show(getSupportFragmentManager(), TAG);
                        break;
                    case R.id.tv_up:
                        onPraise(position);
                        break;
                }
            }
        });

        mEmptyView = LayoutInflater.from(this)
                .inflate(R.layout.base_empty_view, (ViewGroup) mRecyclerView.getParent(), false);

        // begin to load data
        mRefreshHelper.beginLoadData();
    }

    private void checkTitle() {

        mTitleBar.setTitle("评论详情").setTitleListener(new ILookTitleBar.TitleCallbackAdapter() {
            @Override
            public void onClickLeft(View view) {
                super.onClickLeft(view);
                finish();
            }
        });
    }

    private void checkIntent() {

        Intent intent = getIntent();
        PAGE_NEWS_ID = intent.getStringExtra(PARAMS_NEWS_ID);
        PAGE_NEWS_TYPE = intent.getIntExtra(PARAMS_NEWS_TYPE, 0);
    }

    private void loadComment(final int page) {

        showLoading();
        Call<HttpResult<NewsInfoWrapper>> call =
                homeService.getNewsComment(PAGE_NEWS_ID, PAGE_NEWS_TYPE,
                        AppConstant.COMMENT_LEVEL_ONE, page, AppConstant.DEFAULT_PAGE_SIZE);
        call.enqueue(new SimpleCallback<NewsInfoWrapper>() {
            @Override
            public void onSuccess(NewsInfoWrapper data) {

                hideLoading();
                if (page == 1 && (null == data
                        || null == data.commentlist
                        || data.commentlist.size() == 0)) {
                    mRefreshHelper.finishRefresh();
                    mAdapter.setEmptyView(mEmptyView);
                    return;
                }

                mRefreshHelper.setData(data.commentlist, data.haveNext);
            }

            @Override
            public void onFail(String code, String errorMsg) {

                mRefreshHelper.onFail();
                onSimpleError(errorMsg);
            }
        });
    }

    @OnClick(R.id.id_send)
    public void sendComment() {

        String comment = mInputView.getText().toString().trim();
        if (TextUtils.isEmpty(comment)) {
            ToastUtils.showShort(R.string.comment_can_not_be_null);
            return;
        }

        String userId = AccountManager.getInstance().getUserId();

        KeyboardUtils.hideSoftInput(this);
        showLoading();
        Call<HttpResult<JSONObject>> call =
                homeService.postNewsComment(StringHelper.getString(userId), PAGE_NEWS_ID,
                        PAGE_NEWS_TYPE, AppConstant.NONE_VALUE, comment);
        call.enqueue(new SimpleCallback<JSONObject>() {

            @Override
            public void onSuccess(JSONObject data) {
                onCommentSuccess();
            }

            @Override
            public void onFail(String code, String errorMsg) {
                ToastUtils.showShort(errorMsg);
            }
        });
    }

    private void onCommentSuccess() {
        hideLoading();
        mInputView.setText("");
        loadComment(1);
    }

    private void onPraise(final int position) {

        showAnim(position);

        int type;
        final boolean hasPraise;
        String cid;
        //if (position == AppConstant.INVALIDATE) {
        //    //操作 新闻
        //
        //    type = PAGE_NEWS_TYPE;
        //    hasPraise = hasNewsPraise;
        //    cid = PAGE_NEWS_ID;
        //} else {
        //操作 评论列表

        type = 11;
        CommentBean commentBean = mAdapter.getData().get(position);
        cid = commentBean.cid;
        hasPraise = TextUtils.equals(AppConstant.HAS_PRAISE, commentBean.isFollow);
        //}

        int subType = hasPraise ? 2 : 1;

        String userId = AccountManager.getInstance().getUserId();
        showLoading();
        Call<HttpResult<JSONObject>> call =
                homeService.optLike(StringHelper.getString(userId), null, type, cid, subType);
        call.enqueue(new SimpleCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject data) {

                hideLoading();
                if (position == AppConstant.INVALIDATE) {

                } else {
                    if (hasPraise) {
                        //取消点赞成功
                        ToastUtils.showShort("取消点赞成功");
                        //commentBean.isFollow = AppConstant.UN_PRAISE;
                    } else {
                        //点赞成功
                        ToastUtils.showShort("点赞成功");
                        //commentBean.isFollow = AppConstant.HAS_PRAISE;
                        CommentBean commentBean1 = mAdapter.getData().get(position);
                        commentBean1.careCount++;
                        mAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFail(String code, String errorMsg) {
                onSimpleError(errorMsg);
            }
        });
    }

    private void showAnim(int position) {

        View view = mAdapter.getViewByPosition(mRecyclerView, position, R.id.tv_up);
        if (view == null) {
            return;
        }
        //TODO 验证参数的有效性
        float scaleSmall = 0.8f;
        float scaleLarge = 1.2f;
        float shakeDegrees = 10;
        int duration = 1000;

        //先变小后变大
        PropertyValuesHolder scaleXValuesHolder =
                PropertyValuesHolder.ofKeyframe(View.SCALE_X, Keyframe.ofFloat(0f, 1.0f),
                        Keyframe.ofFloat(0.25f, scaleSmall), Keyframe.ofFloat(0.5f, scaleLarge),
                        Keyframe.ofFloat(0.75f, scaleLarge), Keyframe.ofFloat(1.0f, 1.0f));
        PropertyValuesHolder scaleYValuesHolder =
                PropertyValuesHolder.ofKeyframe(View.SCALE_Y, Keyframe.ofFloat(0f, 1.0f),
                        Keyframe.ofFloat(0.25f, scaleSmall), Keyframe.ofFloat(0.5f, scaleLarge),
                        Keyframe.ofFloat(0.75f, scaleLarge), Keyframe.ofFloat(1.0f, 1.0f));

        //先往左再往右
        PropertyValuesHolder rotateValuesHolder =
                PropertyValuesHolder.ofKeyframe(View.ROTATION, Keyframe.ofFloat(0f, 0f),
                        Keyframe.ofFloat(0.1f, -shakeDegrees), Keyframe.ofFloat(0.2f, shakeDegrees),
                        Keyframe.ofFloat(0.3f, -shakeDegrees), Keyframe.ofFloat(0.4f, shakeDegrees),
                        Keyframe.ofFloat(0.5f, -shakeDegrees), Keyframe.ofFloat(0.6f, shakeDegrees),
                        Keyframe.ofFloat(0.7f, -shakeDegrees), Keyframe.ofFloat(0.8f, shakeDegrees),
                        Keyframe.ofFloat(0.9f, -shakeDegrees), Keyframe.ofFloat(1.0f, 0f));

        ObjectAnimator objectAnimator =
                ObjectAnimator.ofPropertyValuesHolder(view, scaleXValuesHolder, scaleYValuesHolder,
                        rotateValuesHolder);
        objectAnimator.setDuration(duration);
        objectAnimator.start();
    }
}
