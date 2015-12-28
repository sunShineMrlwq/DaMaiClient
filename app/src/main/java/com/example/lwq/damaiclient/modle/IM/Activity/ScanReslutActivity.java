package com.example.lwq.damaiclient.modle.IM.Activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.lwq.damaiclient.R;

public class ScanReslutActivity extends Activity {
    String url;
    WebView webview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanreslut);
        url=getIntent().getStringExtra("result");
        Log.i("URL", url);
        webview= (WebView) findViewById(R.id.webview);
        doWebView("http://www.sina.com.cn/");
    }


    //webview的加载
    public void doWebView(String url ){
        try {
            webview.getSettings().setJavaScriptEnabled(true);
            //			webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
            webview.getSettings().setDefaultTextEncodingName("utf-8");  //防止乱码
            webview.getSettings().setTextSize(WebSettings.TextSize.NORMAL);;//设置默认的字体大小
            //解决空白1
            webview.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
            webview.setVerticalScrollBarEnabled(false);
            webview.setVerticalScrollbarOverlay(false);
            webview.setHorizontalScrollBarEnabled(false);
            webview.setHorizontalScrollbarOverlay(false);

            webview.loadUrl(url);
            webview.setWebViewClient(new WebViewClient(){
                //网页加载开始时调用，显示加载提示旋转进度条
                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    super.onPageStarted(view, url, favicon);
                }
                //网页加载完成时调用，隐藏加载提示旋转进度条
                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);

                }
                //网页加载失败时调用，隐藏加载提示旋转进度条
                @Override
                public void onReceivedError(WebView view, int errorCode,
                                            String description, String failingUrl) {
                    super.onReceivedError(view, errorCode, description, failingUrl);

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
