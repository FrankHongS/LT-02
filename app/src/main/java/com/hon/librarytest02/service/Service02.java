package com.hon.librarytest02.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Looper;

import androidx.annotation.Nullable;

import com.hon.mylogger.MyLogger;

/**
 * Created by Frank_Hon on 11/27/2019.
 * E-mail: v-shhong@microsoft.com
 */
public class Service02 extends IntentService {
    public Service02() {
        super("hello");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        // 执行完onHandleIntent，自动调用onDestroy
        MyLogger.d("onHandleIntent: %s", Looper.myLooper() == Looper.getMainLooper());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MyLogger.d("onDestroy");
    }
}
