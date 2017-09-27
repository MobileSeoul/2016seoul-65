package com.sbway.News;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.sbway.Bookmark.BookmarkActivity;
import com.sbway.Memo.MemoListActivity;
import com.sbway.R;
import com.sbway.Weather.WeatherActivity;

public class NewsActivity extends AppCompatActivity {
    WebView myWebView;
    ProgressDialog progressDialog;
    int aa=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);


        ProgressThread progressThread = new ProgressThread();
        progressThread.setDaemon(true);
        progressThread.start();

        Intent i = getIntent();
        String url = i.getStringExtra("link");

        myWebView = (WebView)findViewById(R.id.webview);
        myWebView.loadUrl(url);
        myWebView.setWebViewClient(new WebClient());
        myWebView.getSettings().setJavaScriptEnabled(true);

    }

    class ProgressThread extends Thread {
        public void run() {
            while (aa<2) {
                progressHandler.sendEmptyMessage(aa);
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                }
            }
        }
    }

    Handler progressHandler = new Handler() {
        public void handleMessage(Message msg) {
            if(msg.what==0){
                progressDialog = ProgressDialog.show(NewsActivity.this, "", "로딩중입니다.", true, true);
                aa++;
            }else if(msg.what==1){
                progressDialog.dismiss();
            }
        }
    };

    public class WebClient extends WebViewClient {
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if((keyCode == KeyEvent.KEYCODE_BACK) && myWebView.canGoBack()) {
            myWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
