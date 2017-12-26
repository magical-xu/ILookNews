package com.aktt.news;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.aktt.news.util.HtmlUtil;
import com.aktt.news.widget.Html5WebView;
import com.githang.statusbar.StatusBarCompat;

/**
 * Created by magical on 17/9/7.
 * Description : 简单粗暴
 */

public class SimpleWebActivity extends AppCompatActivity {

    private static final String TAG = "SimpleWebActivity";
    private static final String PARAMS = "params";
    private static final String PARAMS_CONTENT = "params_content";
    private String mUrl;
    private String mAdContent;

    private LinearLayout mLayout;
    private WebView mWebView;
    private TextView mWebTitle;
    private ImageView mWebBack;
    private ProgressBar mWebProgress;

    private long mOldTime;

    public static void getInstance(Context context, @NonNull String url, @Nullable String content) {

        Intent intent = new Intent(context, SimpleWebActivity.class);
        intent.putExtra(PARAMS, url);
        intent.putExtra(PARAMS_CONTENT, content);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        setContentView(R.layout.activity_simple_web);

        StatusBarCompat.setStatusBarColor(this, ContextCompat.getColor(this, R.color.white));

        mLayout = (LinearLayout) findViewById(R.id.web_container);
        mWebProgress = (ProgressBar) findViewById(R.id.id_web_progress);
        mWebBack = (ImageView) findViewById(R.id.id_web_back);
        mWebTitle = (TextView) findViewById(R.id.id_web_title);

        mWebBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        Intent intent = getIntent();
        mUrl = intent.getStringExtra(PARAMS);
        mAdContent = intent.getStringExtra(PARAMS_CONTENT);

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
        mWebView = new Html5WebView(getApplicationContext());

        mWebView.setWebChromeClient(webChromeClient);
        mWebView.setWebViewClient(webViewClient);
        mWebView.setLayoutParams(params);
        mLayout.addView(mWebView);

        if (TextUtils.isEmpty(mUrl)) {

            if (!TextUtils.isEmpty(mAdContent)) {
                mWebView.loadDataWithBaseURL("about:blank", HtmlUtil.getHtmlData(mAdContent),
                        "text/html", "utf-8", null);
            }
        } else {
            mWebView.loadUrl(mUrl);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (null != mWebView && System.currentTimeMillis() - mOldTime < 1500) {
                mWebView.clearHistory();

                if (TextUtils.isEmpty(mUrl)) {
                    mWebView.loadDataWithBaseURL("about:blank", HtmlUtil.getHtmlData(mAdContent),
                            "text/html", "utf-8", null);
                } else {
                    mWebView.loadUrl(mUrl);
                }
            } else if (null != mWebView && mWebView.canGoBack()) {
                mWebView.goBack();
            } else {
                SimpleWebActivity.this.finish();
            }
            mOldTime = System.currentTimeMillis();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        if (mWebView != null) {
            mWebView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            mWebView.clearHistory();

            ((ViewGroup) mWebView.getParent()).removeView(mWebView);
            mWebView.destroy();
            mWebView = null;
        }
        super.onDestroy();
    }

    WebViewClient webViewClient = new WebViewClient() {
        /**
         * 多页面在同一个WebView中打开，就是不新建activity或者调用系统浏览器打开
         */
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    };

    WebChromeClient webChromeClient = new WebChromeClient() {

        //=========HTML5定位==========================================================
        //需要先加入权限
        //<uses-permission android:name="android.permission.INTERNET"/>
        //<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
        //<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>
        @Override
        public void onReceivedIcon(WebView view, Bitmap icon) {
            super.onReceivedIcon(view, icon);
        }

        @Override
        public void onGeolocationPermissionsHidePrompt() {
            super.onGeolocationPermissionsHidePrompt();
        }

        @Override
        public void onGeolocationPermissionsShowPrompt(final String origin,
                final GeolocationPermissions.Callback callback) {
            callback.invoke(origin, true, false);//注意个函数，第二个参数就是是否同意定位权限，第三个是是否希望内核记住
            super.onGeolocationPermissionsShowPrompt(origin, callback);
        }
        //=========HTML5定位==========================================================

        //=========多窗口的问题==========================================================
        @Override
        public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture,
                Message resultMsg) {
            WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
            transport.setWebView(view);
            resultMsg.sendToTarget();
            return true;
        }
        //=========多窗口的问题==========================================================

        @Override
        public void onReceivedTitle(WebView view, String title) {
            //super.onReceivedTitle(view, title);
            //为解决6.0系统上，这个api会调用两次，而且第一次是显示url的系统bug
            if (null != title && !view.getUrl().contains(title)) {
                mWebTitle.setText(title);
            }
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            //super.onProgressChanged(view, newProgress);
            if (newProgress == mWebProgress.getMax()) {
                mWebProgress.setVisibility(View.GONE);
            } else {
                mWebProgress.setProgress(newProgress);
            }
        }
    };
}
