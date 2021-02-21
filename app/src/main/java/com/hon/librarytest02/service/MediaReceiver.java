package com.hon.librarytest02.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.hon.mylogger.MyLogger;

/**
 * Created by shuaihua on 2021/1/26 4:39 PM
 * Email: shuaihua@staff.sina.com.cn
 */
public class MediaReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        MyLogger.d("action: " + action);
        switch (action) {
            case "Pause":
                break;
            case "Next":
                break;
            case "Previous":
                break;
            default:
                break;
        }
    }
}
