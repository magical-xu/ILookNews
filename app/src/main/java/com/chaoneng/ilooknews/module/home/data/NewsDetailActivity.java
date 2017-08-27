package com.chaoneng.ilooknews.module.home.data;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import butterknife.BindView;
import com.chaoneng.ilooknews.R;
import com.chaoneng.ilooknews.base.BaseActivity;
import com.chaoneng.ilooknews.data.MockServer;
import com.chaoneng.ilooknews.module.video.adapter.VideoCommentAdapter;
import com.chaoneng.ilooknews.util.RefreshHelper;
import com.chaoneng.ilooknews.widget.ILookTitleBar;
import com.magicalxu.library.blankj.ToastUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

/**
 * Created by magical on 17/8/27.
 * Description : 新闻详情页
 */

public class NewsDetailActivity extends BaseActivity {

  @BindView(R.id.id_recycler) RecyclerView mRecyclerView;
  @BindView(R.id.id_refresh_layout) SmartRefreshLayout mRefreshLayout;

  private WebView mWebView;
  private TextView mTitleTv;

  private VideoCommentAdapter mAdapter;
  private RefreshHelper mRefreshHelper;
  private MockServer mockServer;

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

    checkTitle();
    mAdapter = new VideoCommentAdapter(R.layout.item_video_comment);
    mRefreshHelper = new RefreshHelper(mRefreshLayout, mAdapter, mRecyclerView) {
      @Override
      public void onRequest(int page) {
        mockServer.mockGankCall(page, MockServer.Type.VIDEO_COMMENT);
      }
    };
    mockServer = MockServer.getInstance();
    mockServer.init(mRefreshHelper);
    bindHeader();
    mRefreshHelper.beginLoadData();
  }

  private void bindHeader() {

    View header = getLayoutInflater().inflate(R.layout.header_news_detail, null);

    mWebView = header.findViewById(R.id.id_news_detail_web);
    mTitleTv = header.findViewById(R.id.id_news_detail_title);

    configWebView();
    mTitleTv.setText(getString(R.string.test_text_long));

    mWebView.loadUrl("https://www.baidu.com");

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
