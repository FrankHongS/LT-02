package com.hon.librarytest02.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.hon.mylogger.MyLogger;

import androidx.annotation.Nullable;

/**
 * Created by Frank Hon on 2019/2/17 9:02 PM.
 * E-mail: frank_hon@foxmail.com
 */
public class Service01 extends Service {

    private IBinder mBinder=new MyBinder();

    /**
     *  onCreate invoked when startService first executing.
     *  if startService executes again, onCreate not invoked again
     */
    @Override
    public void onCreate() {
        super.onCreate();
        MyLogger.d(getClass(),"onCreate");
    }

    /**
     *  the method runs in the main thread
     * @param intent
     * @param flags
     * @param startId
     * @return
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        MyLogger.d(getClass(),"onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        MyLogger.d(getClass(),"onBind");
        return mBinder;
    }

    @Override
    public void onDestroy() {
        MyLogger.d(getClass(),"onDestroy");
        super.onDestroy();
    }

    public static class MyBinder extends Binder{

        public void startDownload(){
            MyLogger.d(getClass(),"startDownload");
        }
    }
}
