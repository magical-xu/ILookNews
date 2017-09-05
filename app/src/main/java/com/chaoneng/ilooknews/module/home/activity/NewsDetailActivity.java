package com.chaoneng.ilooknews.module.home.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import butterknife.BindView;
import com.chaoneng.ilooknews.AppConstant;
import com.chaoneng.ilooknews.R;
import com.chaoneng.ilooknews.api.HomeService;
import com.chaoneng.ilooknews.base.BaseActivity;
import com.chaoneng.ilooknews.data.MockServer;
import com.chaoneng.ilooknews.data.NewsInfo;
import com.chaoneng.ilooknews.data.NewsInfoWrapper;
import com.chaoneng.ilooknews.module.video.adapter.CommentAdapter;
import com.chaoneng.ilooknews.data.CommentBean;
import com.chaoneng.ilooknews.net.callback.SimpleCallback;
import com.chaoneng.ilooknews.net.client.NetRequest;
import com.chaoneng.ilooknews.net.data.HttpResult;
import com.chaoneng.ilooknews.util.HtmlUtil;
import com.chaoneng.ilooknews.util.RefreshHelper;
import com.chaoneng.ilooknews.widget.ilook.ILookTitleBar;
import com.magicalxu.library.blankj.ToastUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import java.util.List;
import retrofit2.Call;

/**
 * Created by magical on 17/8/27.
 * Description : 新闻详情页
 */

public class NewsDetailActivity extends BaseActivity {

    private static final String PARAMS_NEWS = "params_news";

    @BindView(R.id.id_recycler) RecyclerView mRecyclerView;
    @BindView(R.id.id_refresh_layout) SmartRefreshLayout mRefreshLayout;

    private WebView mWebView;
    private TextView mTitleTv;

  private CommentAdapter mAdapter;
    private RefreshHelper<CommentBean> mRefreshHelper;
    private MockServer mockServer;
    private String PAGE_NEWS_ID;

    public static void getInstance(Context context, @NonNull String newsId) {
        Intent intent = new Intent(context, NewsDetailActivity.class);
        intent.putExtra(PARAMS_NEWS, newsId);
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
      mAdapter = new CommentAdapter(R.layout.item_video_comment);
        mRefreshHelper = new RefreshHelper<CommentBean>(mRefreshLayout, mAdapter, mRecyclerView) {
            @Override
            public void onRequest(int page) {
                //mockServer.mockGankCall(page, MockServer.Type.VIDEO_COMMENT);
                loadData(page);
            }
        };
        //mockServer = MockServer.getInstance();
        //mockServer.init(mRefreshHelper);
        initHeader();
        mRefreshHelper.beginLoadData();
    }

    private void checkIntent() {

        Intent intent = getIntent();
        PAGE_NEWS_ID = intent.getStringExtra(PARAMS_NEWS);
    }

    private void initHeader() {

        View header = getLayoutInflater().inflate(R.layout.header_news_detail, null);

        mWebView = header.findViewById(R.id.id_news_detail_web);
        mTitleTv = header.findViewById(R.id.id_news_detail_title);

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
                    }
                });
    }

    private void loadData(final int page) {

        HomeService homeService = NetRequest.getInstance().create(HomeService.class);
        Call<HttpResult<NewsInfoWrapper>> call =
                homeService.getNewsDetail(AppConstant.TEST_NEWS_USER_ID, AppConstant.TEST_NEWS_ID,
                        2);
        call.enqueue(new SimpleCallback<NewsInfoWrapper>() {
            @Override
            public void onSuccess(NewsInfoWrapper data) {

                if (null == data) {
                    return;
                }

                if (page == 1) {
                    bindHeader(data.newInfo);
                }

                bindItem(data.commentlist);
            }

            @Override
            public void onFail(String code, String errorMsg) {

            }
        });
    }

    private void bindItem(List<CommentBean> list) {

        mRefreshHelper.setData(list);
    }

    /**
     * 绑定新闻头部数据
     */
    private void bindHeader(@Nullable NewsInfo newInfo) {

        if (null == newInfo) {
            return;
        }

        mTitleTv.setText(newInfo.title);
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

    private class MyWebViewClient extends WebViewClient {

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            mWebView.getSettings().setBlockNetworkImage(false);
        }
    }
}
