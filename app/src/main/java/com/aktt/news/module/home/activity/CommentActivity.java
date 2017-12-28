package com.aktt.news.module.home.activity;

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
import com.aktt.news.util.IntentHelper;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.aktt.news.AppConstant;
import com.aktt.news.R;
import com.aktt.news.api.HomeService;
import com.aktt.news.base.BaseActivity;
import com.aktt.news.data.CommentBean;
import com.aktt.news.data.NewsInfoWrapper;
import com.aktt.news.instance.AccountManager;
import com.aktt.news.module.home.fragment.CommentDialogFragment;
import com.aktt.news.module.video.adapter.CommentAdapter;
import com.aktt.news.net.callback.SimpleCallback;
import com.aktt.news.net.client.NetRequest;
import com.aktt.news.net.data.HttpResult;
import com.aktt.news.util.AnimHelper;
import com.aktt.news.util.RefreshHelper;
import com.aktt.news.util.StringHelper;
import com.aktt.news.widget.ilook.ILookTitleBar;
import com.magicalxu.library.blankj.KeyboardUtils;
import com.magicalxu.library.blankj.ToastUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;
import retrofit2.Call;

import static com.aktt.news.AppConstant.PARAMS_NEWS_ID;
import static com.aktt.news.AppConstant.PARAMS_NEWS_TYPE;

/**
 * Created by magical on 17/9/19.
 * Description : 一级评论界面
 * 评论 ok
 * 点赞
 */

public class CommentActivity extends BaseActivity {

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

    private ArrayList<Call> callList;

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
    public ArrayList<Call> addRequestList() {
        callList = new ArrayList<>();
        return callList;
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
                CommentBean commentBean = mAdapter.getData().get(position);
                switch (view.getId()) {
                    case R.id.id_comment_count:

                        String commentId = commentBean.cid;
                        CommentDialogFragment fragment =
                                CommentDialogFragment.newInstance(PAGE_NEWS_ID, PAGE_NEWS_TYPE,
                                        commentId);
                        fragment.show(getSupportFragmentManager(), TAG);
                        break;
                    case R.id.tv_up:
                        onPraise(position);
                        break;
                    case R.id.iv_avatar:

                        if (TextUtils.isEmpty(commentBean.nickname)) {
                            return;
                        }
                        IntentHelper.openUserCenterPage(CommentActivity.this, commentBean.userid);

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
                KeyboardUtils.hideSoftInput(CommentActivity.this);
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

        String userId = AccountManager.getInstance().getUserId();

        showLoading();
        final Call<HttpResult<NewsInfoWrapper>> call =
                homeService.getNewsComment(StringHelper.getString(userId), PAGE_NEWS_ID,
                        PAGE_NEWS_TYPE, AppConstant.COMMENT_LEVEL_ONE, page,
                        AppConstant.DEFAULT_PAGE_SIZE);
        onAddCall(call);
        call.enqueue(new SimpleCallback<NewsInfoWrapper>() {
            @Override
            public void onSuccess(NewsInfoWrapper data) {

                onRemoveCall(call);
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

                onRemoveCall(call);
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

        AnimHelper.showAnim(mAdapter.getViewByPosition(mRecyclerView, position, R.id.tv_up));

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
        //hasPraise = TextUtils.equals(AppConstant.HAS_PRAISE, commentBean.isFollow);
        hasPraise = false;
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
                    List<CommentBean> listData = mAdapter.getData();
                    if (listData.size() > position) {
                        CommentBean commentBean = listData.get(position);
                        if (hasPraise) {
                        } else {
                            //点赞成功
                            ToastUtils.showShort("点赞成功");
                            commentBean.isFollow = AppConstant.HAS_PRAISE;
                            ++commentBean.careCount;
                            mAdapter.notifyItemChanged(position + mAdapter.getHeaderLayoutCount());
                        }
                    }
                }
            }

            @Override
            public void onFail(String code, String errorMsg) {
                onSimpleError(errorMsg);
            }
        });
    }
}
