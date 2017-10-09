package com.chaoneng.ilooknews.module.home.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
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
import com.chaoneng.ilooknews.data.NewsInfo;
import com.chaoneng.ilooknews.data.NewsInfoWrapper;
import com.chaoneng.ilooknews.module.home.fragment.CommentDialogFragment;
import com.chaoneng.ilooknews.module.video.adapter.CommentAdapter;
import com.chaoneng.ilooknews.net.callback.SimpleCallback;
import com.chaoneng.ilooknews.net.client.NetRequest;
import com.chaoneng.ilooknews.net.data.HttpResult;
import com.chaoneng.ilooknews.util.HtmlUtil;
import com.chaoneng.ilooknews.util.IntentHelper;
import com.chaoneng.ilooknews.util.RefreshHelper;
import com.chaoneng.ilooknews.widget.ilook.ILookTitleBar;
import com.chaoneng.ilooknews.widget.image.HeadImageView;
import com.magicalxu.library.blankj.KeyboardUtils;
import com.magicalxu.library.blankj.ToastUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import java.util.List;
import org.json.JSONObject;
import retrofit2.Call;

import static com.chaoneng.ilooknews.AppConstant.PARAMS_NEWS_ID;
import static com.chaoneng.ilooknews.AppConstant.PARAMS_NEWS_TYPE;

/**
 * Created by magical on 17/8/27.
 * Description : 新闻详情页
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
    private TextView mKey2View;
    private TextView mKey3View;
    private TextView mUpView;
    private TextView mDownView;
    private HeadImageView mHeaderIv;
    private TextView mHeaderName;
    private TextView mHeaderIntro;
    private TextView mHeaderFocus;

    private CommentAdapter mAdapter;
    private RefreshHelper<CommentBean> mRefreshHelper;
    private String PAGE_NEWS_ID;
    private int PAGE_NEWS_TYPE;
    private HomeService homeService;

    private boolean hasNewsPraise;

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
        mAdapter = new CommentAdapter(false, R.layout.item_video_comment);
        mRefreshHelper = new RefreshHelper<CommentBean>(mRefreshLayout, mAdapter, mRecyclerView) {
            @Override
            public void onRequest(int page) {
                loadData(page);
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
        mRefreshHelper.beginLoadData();
    }

    private void onPraise(final int position) {

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
            hasPraise = TextUtils.equals(AppConstant.HAS_PRAISE, commentBean.isFollow);
        }

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
        mKey2View = header.findViewById(R.id.id_keyword2);
        mKey3View = header.findViewById(R.id.id_keyword3);
        mUpView = header.findViewById(R.id.id_up);
        mDownView = header.findViewById(R.id.id_down);
        mHeaderIv = header.findViewById(R.id.iv_avatar);
        mHeaderName = header.findViewById(R.id.tv_name);
        mHeaderIntro = header.findViewById(R.id.tv_intro);
        mHeaderFocus = header.findViewById(R.id.tv_focus);

        configWebView();
        mAdapter.addHeaderView(header);
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
                        ToastUtils.showShort("分享");
                        IntentHelper.openShareBottomPage(NewsDetailActivity.this, PAGE_NEWS_ID,
                                PAGE_NEWS_TYPE);
                    }
                });
    }

    private void loadData(final int page) {

        Call<HttpResult<NewsInfoWrapper>> call =
                homeService.getNewsDetail(AppConstant.TEST_NEWS_USER_ID, PAGE_NEWS_ID, 2);
        call.enqueue(new SimpleCallback<NewsInfoWrapper>() {
            @Override
            public void onSuccess(NewsInfoWrapper data) {

                if (null == data) {
                    return;
                }

                if (page == 1) {
                    bindHeader(data.newInfo);
                }

                bindItem(data.commentlist,data.haveNext);
            }

            @Override
            public void onFail(String code, String errorMsg) {

            }
        });
    }

    private void bindItem(List<CommentBean> list,boolean haveNext) {

        mRefreshHelper.setData(list,haveNext);
    }

    /**
     * 绑定新闻头部数据
     */
    private void bindHeader(@Nullable NewsInfo newInfo) {

        if (null == newInfo) {
            return;
        }

        hasNewsPraise = TextUtils.equals(AppConstant.HAS_PRAISE, newInfo.isFollow);

        mTitleTv.setText(newInfo.title);
        mHeaderIv.setHeadImage(newInfo.userIcon);
        mHeaderName.setText(newInfo.nickname);
        mHeaderIntro.setText(AppConstant.TEST_SIGN);
        mUpView.setText(String.valueOf(newInfo.like_count));
        mDownView.setText(String.valueOf(newInfo.dislike_count));
        mKey1View.setText("关键字1");
        mKey2View.setText("关键字2");
        mKey3View.setText("关键字3");

        mWebView.loadDataWithBaseURL("about:blank", HtmlUtil.getHtmlData(newInfo.content),
                "text/html", "utf-8", null);
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

        //加载数据时先停止加载图片 防止卡顿
        settings.setBlockNetworkImage(true);

        mWebView.setWebViewClient(new MyWebViewClient());
        mWebView.setWebChromeClient(new WebChromeClient());
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

    /**
     * 评论成功
     */
    private void onCommentSuccess() {
        mInputView.setText("");
        loadComment(1);
    }

    private void loadComment(int page) {

        Call<HttpResult<NewsInfoWrapper>> call =
                homeService.getNewsComment(AppConstant.TEST_USER_ID, PAGE_NEWS_ID, PAGE_NEWS_TYPE,
                        AppConstant.NONE_VALUE, page, AppConstant.DEFAULT_PAGE_SIZE);
        call.enqueue(new SimpleCallback<NewsInfoWrapper>() {
            @Override
            public void onSuccess(NewsInfoWrapper data) {
                List<CommentBean> commentList = data.commentlist;
                if (null != commentList) {
                    bindItem(commentList,data.haveNext);
                }
            }

            @Override
            public void onFail(String code, String errorMsg) {
                ToastUtils.showShort(errorMsg);
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
