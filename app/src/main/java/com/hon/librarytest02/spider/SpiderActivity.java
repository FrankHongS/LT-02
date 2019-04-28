package com.hon.librarytest02.spider;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import com.hon.librarytest02.R;
import com.hon.mylogger.MyLogger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by Frank_Hon on 4/26/2019.
 * E-mail: v-shhong@microsoft.com
 */
public class SpiderActivity extends AppCompatActivity {

    private WebView webView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_spider);

        webView=findViewById(R.id.webview);
    }

    private void test(){
//        new Thread(){
//            @Override
//            public void run() {
//                try {
//                    Document doc= Jsoup.connect("http://www.baidu.com/")
////                            .userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 "+
////                                    "(KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36")
////                            .timeout(5000)
//                            .get();
//
//                    String price=doc.html();
////                    String price=doc.getElementById("hexm_curPrice").text();
//                    MyLogger.d(getClass(),price);
//                    runOnUiThread(()->{
//                        textView.setText(price);
//                    });
//
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }.start();
        webView.loadUrl("http://stockpage.10jqka.com.cn/realHead_v2.html#hs_000063");

    }

    public void onClick(View view){
        test();
    }
}
