package com.aktt.news.module.home.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.OnClick;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.aktt.news.AppConstant;
import com.aktt.news.R;
import com.aktt.news.api.HomeService;
import com.aktt.news.api.UserService;
import com.aktt.news.base.BaseActivity;
import com.aktt.news.data.CommentBean;
import com.aktt.news.data.NewsInfo;
import com.aktt.news.data.NewsInfoWrapper;
import com.aktt.news.instance.AccountManager;
import com.aktt.news.module.home.fragment.CommentDialogFragment;
import com.aktt.news.module.video.adapter.CommentAdapter;
import com.aktt.news.net.callback.SimpleCallback;
import com.aktt.news.net.client.NetRequest;
import com.aktt.news.net.data.HttpResult;
import com.aktt.news.util.AnimHelper;
import com.aktt.news.util.HtmlUtil;
import com.aktt.news.util.IntentHelper;
import com.aktt.news.util.RefreshHelper;
import com.aktt.news.util.SimpleNotifyListener;
import com.aktt.news.util.SimplePreNotifyListener;
import com.aktt.news.util.StringHelper;
import com.aktt.news.util.UserOptionHelper;
import com.aktt.news.widget.ilook.ILookTitleBar;
import com.aktt.news.widget.image.HeadImageView;
import com.magicalxu.library.blankj.KeyboardUtils;
import com.magicalxu.library.blankj.SPUtils;
import com.magicalxu.library.blankj.ToastUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import java.util.List;
import org.json.JSONObject;
import retrofit2.Call;

import static com.aktt.news.AppConstant.PARAMS_NEWS_ID;
import static com.aktt.news.AppConstant.PARAMS_NEWS_TYPE;

/**
 * Created by magical on 17/8/27.
 * Description : 新闻详情页
 * 关注 ok
 * 赞  ok
 * 评论 ok
 */

public class NewsDetailActivity extends BaseActivity {

    private static final String TAG = "NewsDetailActivity";

    @BindView(R.id.id_recycler) RecyclerView mRecyclerView;
    @BindView(R.id.id_refresh_layout) SmartRefreshLayout mRefreshLayout;
    @BindView(R.id.id_edit) EditText mInputView;
    @BindView(R.id.id_send) TextView mSendBtn;

    private WebView mWebView;
    private TextView mTitleTv;
    private TextView mKey1View;
    //private TextView mKey2View;
    //private TextView mKey3View;
    private TextView mUpView;
    private TextView mDownView;
    private HeadImageView mHeaderIv;
    private TextView mHeaderName;
    private TextView mHeaderIntro;
    private TextView mHeaderFocus;

    private View mEmptyView;

    private CommentAdapter mAdapter;
    private RefreshHelper<CommentBean> mRefreshHelper;
    private String PAGE_NEWS_ID;
    private int PAGE_NEWS_TYPE;
    private HomeService homeService;
    private UserService userService;

    private String NEWS_PUBLISHER;

    private boolean hasNewsPraise;
    private boolean hasFollowed;

    public static void getInstance(Context context, @NonNull String newsId, int newsType) {
        Intent intent = new Intent(context, NewsDetailActivity.class);
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
        userService = NetRequest.getInstance().create(UserService.class);
        mAdapter = new CommentAdapter(false, R.layout.item_video_comment);
        mAdapter.setHeaderAndEmpty(true);
        mRefreshHelper = new RefreshHelper<CommentBean>(mRefreshLayout, mAdapter, mRecyclerView) {
            @Override
            public void onRequest(int page) {
                if (page == 1) {
                    loadData(1);//加载新闻
                }
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

                        String cid = commentBean.cid;
                        CommentDialogFragment fragment =
                                CommentDialogFragment.newInstance(PAGE_NEWS_ID, PAGE_NEWS_TYPE,
                                        cid);
                        fragment.show(getSupportFragmentManager(), TAG);
                        break;
                    case R.id.tv_up:
                        onPraise(position);
                        break;
                }
            }
        });
        initHeader();
        mEmptyView = LayoutInflater.from(this)
                .inflate(R.layout.base_comment_empty_view, (ViewGroup) mRecyclerView.getParent(),
                        false);
        mRefreshHelper.beginLoadData();
    }

    private void onPraise(final int position) {

        if (AccountManager.getInstance().checkLogin(this)) {
            return;
        }

        int type;
        final boolean hasPraise;
        String cid;
        if (position == AppConstant.INVALIDATE) {
            //操作 新闻

            type = PAGE_NEWS_TYPE;
            hasPraise = hasNewsPraise;
            cid = PAGE_NEWS_ID;
        } else {
            //操作 评论列表

            type = 11;
            CommentBean commentBean = mAdapter.getData().get(position);
            cid = commentBean.cid;
            //hasPraise = TextUtils.equals(AppConstant.HAS_PRAISE, commentBean.isFollow);
            //评论不许取消点赞
            hasPraise = false;

            AnimHelper.showAnim(mAdapter.getViewByPosition(mRecyclerView,
                    position + mAdapter.getHeaderLayoutCount(), R.id.tv_up));
        }

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

                    onOptLikeSuccess(hasPraise);
                } else {

                    List<CommentBean> listData = mAdapter.getData();
                    if (listData.size() > position) {
                        CommentBean commentBean = listData.get(position);
                        if (hasPraise) {
                            ////取消点赞成功
                            //ToastUtils.showShort("取消点赞成功");
                            //commentBean.isFollow = AppConstant.UN_PRAISE;
                            //--commentBean.careCount;
                            //mAdapter.notifyItemChanged(position + mAdapter.getHeaderLayoutCount());
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

    private void onOptLikeSuccess(boolean hasPraise) {

        if (hasPraise) {
            //不喜欢+1
            String disLikeCount = mDownView.getText().toString();
            if (TextUtils.isDigitsOnly(disLikeCount)) {
                int disCount = Integer.parseInt(disLikeCount);
                mDownView.setText(String.valueOf(disCount + 1));
            }
        } else {
            //喜欢+1
            String likeCount = mUpView.getText().toString();
            if (TextUtils.isDigitsOnly(likeCount)) {
                int lCount = Integer.parseInt(likeCount);
                mUpView.setText(String.valueOf(lCount + 1));
            }
        }
    }

    private void checkIntent() {

        Intent intent = getIntent();
        PAGE_NEWS_ID = intent.getStringExtra(PARAMS_NEWS_ID);
        PAGE_NEWS_TYPE = intent.getIntExtra(PARAMS_NEWS_TYPE, 0);
    }

    private void initHeader() {

        View header = getLayoutInflater().inflate(R.layout.header_news_detail, null);

        mWebView = header.findViewById(R.id.id_news_detail_web);
        mTitleTv = header.findViewById(R.id.id_news_detail_title);
        mKey1View = header.findViewById(R.id.id_keyword1);
        //mKey2View = header.findViewById(R.id.id_keyword2);
        //mKey3View = header.findViewById(R.id.id_keyword3);
        mUpView = header.findViewById(R.id.id_up);
        mDownView = header.findViewById(R.id.id_down);
        mHeaderIv = header.findViewById(R.id.iv_avatar);
        mHeaderName = header.findViewById(R.id.tv_name);
        mHeaderIntro = header.findViewById(R.id.tv_intro);
        mHeaderFocus = header.findViewById(R.id.tv_focus);

        mUpView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // like
                hasNewsPraise = false;
                onPraise(AppConstant.INVALIDATE);
            }
        });
        mDownView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // dislike
                hasNewsPraise = true;
                onPraise(AppConstant.INVALIDATE);
            }
        });
        mHeaderFocus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onOptFollow();
            }
        });

        configWebView();
        mAdapter.addHeaderView(header);
    }

    /**
     * 操作关注关系
     */
    private void onOptFollow() {

        if (AccountManager.getInstance().checkLogin(this)) {
            return;
        }

        if (isLoading()) {
            return;
        }

        showLoading();
        if (hasFollowed) {
            UserOptionHelper.onCancelFollow(userService, NEWS_PUBLISHER,
                    new SimpleNotifyListener() {
                        @Override
                        public void onSuccess(String msg) {
                            hideLoading();
                            changeFollowState(false);
                        }

                        @Override
                        public void onFailed(String msg) {
                            onSimpleError(msg);
                        }
                    });
        } else {
            UserOptionHelper.onFollow(userService, NEWS_PUBLISHER, new SimpleNotifyListener() {
                @Override
                public void onSuccess(String msg) {
                    hideLoading();
                    changeFollowState(true);
                }

                @Override
                public void onFailed(String msg) {
                    onSimpleError(msg);
                }
            });
        }
    }

    private void checkTitle() {

        mTitleBar.setTitle("详情")
                .setRightImage(R.drawable.ic_more_black)
                .setTitleListener(new ILookTitleBar.TitleCallbackAdapter() {
                    @Override
                    public void onClickLeft(View view) {
                        super.onClickLeft(view);
                        finish();
                    }

                    @Override
                    public void onClickRightImage(View view) {
                        super.onClickRightImage(view);
                        IntentHelper.openShareBottomPage(NewsDetailActivity.this, PAGE_NEWS_ID,
                                PAGE_NEWS_TYPE);
                    }
                });
    }

    private void loadData(final int page) {

        String userId = AccountManager.getInstance().getUserId();

        showLoading();
        Call<HttpResult<NewsInfoWrapper>> call =
                homeService.getNewsDetail(StringHelper.getString(userId), PAGE_NEWS_ID, 2);
        call.enqueue(new SimpleCallback<NewsInfoWrapper>() {
            @Override
            public void onSuccess(NewsInfoWrapper data) {

                hideLoading();
                if (null == data) {
                    return;
                }

                if (page == 1) {
                    bindHeader(data.newInfo);
                }
            }

            @Override
            public void onFail(String code, String errorMsg) {
                onSimpleError(errorMsg);
            }
        });
    }

    private void bindItem(List<CommentBean> list, boolean haveNext) {
        mRefreshHelper.setData(list, haveNext);
    }

    /**
     * 绑定新闻头部数据
     */
    private void bindHeader(@Nullable NewsInfo newInfo) {

        if (null == newInfo) {
            return;
        }

        NEWS_PUBLISHER = newInfo.userid;
        hasNewsPraise = TextUtils.equals(AppConstant.HAS_PRAISE, newInfo.isFollow);
        hasFollowed = TextUtils.equals(AppConstant.USER_FOLLOW, newInfo.isFollow);
        changeFollowState(hasFollowed);

        mTitleTv.setText(StringHelper.getString(newInfo.title));
        mHeaderIv.setHeadImage(StringHelper.getString(newInfo.userIcon));
        mHeaderName.setText(StringHelper.getString(newInfo.nickname));
        mHeaderIntro.setText(StringHelper.getString(newInfo.createTime));
        mUpView.setText(String.valueOf(newInfo.like_count));
        mDownView.setText(String.valueOf(newInfo.dislike_count));

        String keyword = String.format(getResources().getString(R.string.key_word_pre),
                StringHelper.getString(newInfo.text_key));
        mKey1View.setText(keyword);

        mWebView.loadDataWithBaseURL("about:blank", HtmlUtil.getHtmlData(newInfo.content),
                "text/html", "utf-8", null);
    }

    private void changeFollowState(boolean hasFollowed) {

        this.hasFollowed = hasFollowed;
        mHeaderFocus.setText(this.hasFollowed ? "已关注" : "关注");
    }

    /**
     * 配置 WebView
     */
    private void configWebView() {

        // 配置 WebView
        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);

        int size = SPUtils.getInstance().getInt(AppConstant.NEWS_TEXT_SIZE, 1);
        if (size == 0) {
            settings.setTextSize(WebSettings.TextSize.SMALLER);
        } else if (size == 1) {
            settings.setTextSize(WebSettings.TextSize.NORMAL);
        } else {
            settings.setTextSize(WebSettings.TextSize.LARGER);
        }

        //加载数据时先停止加载图片 防止卡顿
        settings.setBlockNetworkImage(true);

        mWebView.setWebViewClient(new MyWebViewClient());
        mWebView.setWebChromeClient(new WebChromeClient());
    }

    @OnClick(R.id.id_send)
    public void sendComment() {

        UserOptionHelper.onSendComment(mInputView, homeService, PAGE_NEWS_ID, PAGE_NEWS_TYPE,
                new SimplePreNotifyListener() {
                    @Override
                    public void onPreToDo() {
                        KeyboardUtils.hideSoftInput(NewsDetailActivity.this);
                        showLoading();
                    }

                    @Override
                    public void onSuccess(String msg) {
                        hideLoading();
                        onCommentSuccess();
                    }

                    @Override
                    public void onFailed(String msg) {
                        onSimpleError(msg);
                    }
                });
    }

    /**
     * 评论成功
     */
    private void onCommentSuccess() {
        mInputView.setText("");
        loadComment(1);
    }

    private void loadComment(final int page) {

        showLoading();
        mRefreshHelper.setCurPage(page);
        Call<HttpResult<NewsInfoWrapper>> call =
                homeService.getNewsComment(PAGE_NEWS_ID, PAGE_NEWS_TYPE, AppConstant.NONE_VALUE,
                        page, AppConstant.DEFAULT_PAGE_SIZE);
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

                List<CommentBean> commentList = data.commentlist;
                if (null != commentList) {
                    bindItem(commentList, data.haveNext);
                }
            }

            @Override
            public void onFail(String code, String errorMsg) {
                onSimpleError(errorMsg);
            }
        });
    }

    private class MyWebViewClient extends WebViewClient {

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            mWebView.getSettings().setBlockNetworkImage(false);
        }
    }
}
