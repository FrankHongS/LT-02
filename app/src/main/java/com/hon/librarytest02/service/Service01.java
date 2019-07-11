package com.hon.librarytest02.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.hon.librarytest02.R;
import com.hon.mylogger.MyLogger;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * Created by Frank Hon on 2019/2/17 9:02 PM.
 * E-mail: frank_hon@foxmail.com
 */
public class Service01 extends Service {

    private IBinder mBinder;

    private int mCount=0;
    private CompositeDisposable mCompositeDisposable;

    /**
     *  onCreate invoked when startService first executing.
     *  if startService executes again, onCreate not invoked again
     */
    @Override
    public void onCreate() {
        super.onCreate();
        MyLogger.d("onCreate");

        Intent notificationIntent = new Intent(this, ServiceActivity.class);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Notification notification =
                new NotificationCompat.Builder(this, "test")
                        .setContentTitle("title")
                        .setContentText("notification message")
                        // important!, not showing if not set
                        .setSmallIcon(R.mipmap.ic_launcher_round)
                        .setContentIntent(pendingIntent)
                        .setTicker("ticker")
                        .build();

        startForeground(1, notification);

        mBinder=new MyBinder();
        mCompositeDisposable = new CompositeDisposable();
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
        MyLogger.d("onStartCommand");

        Disposable disposable= Observable.interval(3, TimeUnit.MINUTES)
                .subscribe(ignored->{
                    Intent notificationIntent = new Intent(Service01.this, ServiceActivity.class);
                    PendingIntent pendingIntent =
                            PendingIntent.getActivity(Service01.this, 0, notificationIntent, 0);

                    Notification notification =
                            new NotificationCompat.Builder(Service01.this, "test")
                                    .setContentTitle("title")
                                    .setContentText("notification message"+(++mCount))
                                    // important!, not showing if not set
                                    .setSmallIcon(R.mipmap.ic_launcher_round)
                                    .setContentIntent(pendingIntent)
                                    .setTicker("ticker")
                                    .build();

                    startForeground(1, notification);
                });

        mCompositeDisposable.add(disposable);

        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        MyLogger.d("onBind");
        return mBinder;
    }

    @Override
    public void onDestroy() {
        MyLogger.d("onDestroy");
        super.onDestroy();

        mCompositeDisposable.dispose();
    }

    public static class MyBinder extends Binder{

        public void startWork(){
            MyLogger.d("startWork");
        }
    }
}
