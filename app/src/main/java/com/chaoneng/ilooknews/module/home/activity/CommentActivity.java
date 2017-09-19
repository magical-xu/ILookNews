package com.chaoneng.ilooknews.module.home.activity;

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
import com.chaoneng.ilooknews.module.home.fragment.CommentDialogFragment;
import com.chaoneng.ilooknews.module.video.adapter.CommentAdapter;
import com.chaoneng.ilooknews.net.callback.SimpleCallback;
import com.chaoneng.ilooknews.net.client.NetRequest;
import com.chaoneng.ilooknews.net.data.HttpResult;
import com.chaoneng.ilooknews.util.RefreshHelper;
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

        mAdapter = new CommentAdapter(R.layout.item_video_comment);
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
                        CommentDialogFragment fragment = CommentDialogFragment.newInstance();
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
                homeService.getNewsComment(AppConstant.TEST_USER_ID, PAGE_NEWS_ID, PAGE_NEWS_TYPE,
                        AppConstant.COMMENT_LEVEL_ONE, page, AppConstant.DEFAULT_PAGE_SIZE);
        call.enqueue(new SimpleCallback<NewsInfoWrapper>() {
            @Override
            public void onSuccess(NewsInfoWrapper data) {

                hideLoading();
                if (page == 1 && (null == data
                        || null == data.commentlist
                        || data.commentlist.size() == 0)) {
                    mAdapter.setEmptyView(mEmptyView);
                    return;
                }

                if (page == 1) {

                    mRefreshHelper.finishRefresh();
                    mAdapter.setNewData(data.commentlist);
                } else {

                    if (!data.haveNext) {
                        mRefreshHelper.setNoMoreData();
                        return;
                    }

                    if (data.commentlist.size() < AppConstant.DEFAULT_PAGE_SIZE) {
                        mRefreshHelper.setNoMoreData();
                        return;
                    }

                    mRefreshHelper.finishLoadmore();
                    mAdapter.addData(data.commentlist);
                }
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

        KeyboardUtils.hideSoftInput(this);
        Call<HttpResult<JSONObject>> call =
                homeService.postNewsComment(AppConstant.TEST_USER_ID, PAGE_NEWS_ID, PAGE_NEWS_TYPE,
                        AppConstant.NONE_VALUE, comment);
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
        mInputView.setText("");
        loadComment(1);
    }

    private void onPraise(final int position) {

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
        Call<HttpResult<JSONObject>> call =
                homeService.optLike(AppConstant.TEST_USER_ID, null, type, cid, subType);
        call.enqueue(new SimpleCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject data) {

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
                    }
                }
            }

            @Override
            public void onFail(String code, String errorMsg) {
                ToastUtils.showShort(errorMsg);
            }
        });
    }
}