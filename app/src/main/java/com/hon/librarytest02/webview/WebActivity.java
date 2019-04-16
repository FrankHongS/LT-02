package com.hon.librarytest02.webview;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hon.librarytest02.R;
import com.hon.mylogger.MyLogger;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by Frank_Hon on 2/25/2019.
 * E-mail: v-shhong@microsoft.com
 */
@SuppressWarnings("all")
public class WebActivity extends AppCompatActivity implements View.OnClickListener {

    private WebView mWebView;
    private EditText mEditText;
    private Button mLoadButton;
    private ProgressBar mLoadingBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        initView();
    }

    private void initView(){
        mEditText=findViewById(R.id.et_website);
        mLoadButton=findViewById(R.id.btn_load);
        mLoadingBar=findViewById(R.id.pb_loading);
        mWebView=findViewById(R.id.wv_main);

        mLoadButton.setOnClickListener(this);

        setWebView();

        setWebClient();

        setWebChromeClient();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_load:
                loadWebView();
                break;
            default:
                break;
        }
    }

    private void loadWebView(){
        //方式1. 加载一个网页：
        if(!TextUtils.isEmpty(mEditText.getText())){
            String urlAfterChecked=getUrlAfterChecked(mEditText.getText().toString());
            mEditText.setText(urlAfterChecked, TextView.BufferType.NORMAL);
            mWebView.loadUrl(urlAfterChecked);
        }else{
            Toast.makeText(this, "website can't be empty :)", Toast.LENGTH_SHORT).show();
        }

        //方式2：加载apk包中的html页面
//        mWebView.loadUrl("file:///android_asset/test.html");

        //方式3：加载手机本地的html页面
//        mWebView.loadUrl("content://com.android.htmlfileprovider/sdcard/test.html");

        // 方式4： 加载 HTML 页面的一小段内容
//        mWebView.loadData(String data, String mimeType, String encoding)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setWebView(){
        WebSettings settings=mWebView.getSettings();

        // enable js
        settings.setJavaScriptEnabled(true);
    }

    private void setWebClient(){
        mWebView.setWebViewClient(new WebViewClient(){

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(request.getUrl().toString());
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                mLoadingBar.setVisibility(View.VISIBLE);
                MyLogger.d(WebActivity.class,"onPageStarted");
            }

            @Override
            public void onLoadResource(WebView view, String url) {
                super.onLoadResource(view, url);
                MyLogger.d(WebActivity.class,"onLoadResource");
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                mLoadingBar.setVisibility(View.INVISIBLE);
                MyLogger.d(WebActivity.class,"onPageFinished");
            }
        });
    }

    private void setWebChromeClient(){
        mWebView.setWebChromeClient(new WebChromeClient(){

            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                MyLogger.d(WebActivity.class,"console: "+consoleMessage.message());
                return super.onConsoleMessage(consoleMessage);
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                return super.onJsAlert(view, url, message, result);
            }

            @Override
            public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
                return super.onJsConfirm(view, url, message, result);
            }

            @Override
            public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
                return super.onJsPrompt(view, url, message, defaultValue, result);
            }
        });
    }

    private String getUrlAfterChecked(String originUrl){
        if(!originUrl.startsWith("http://")&&!originUrl.startsWith("https://")){
            if(!originUrl.startsWith("www.")){
                originUrl="www."+originUrl;
            }
            originUrl="http://"+originUrl;
        }

        return originUrl;
    }
}
