package com.hon.librarytest02.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.IBinder;

import com.hon.librarytest02.R;
import com.hon.mylogger.MyLogger;

import java.util.concurrent.TimeUnit;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Frank Hon on 2019/2/17 9:02 PM.
 * E-mail: frank_hon@foxmail.com
 */
public class Service01 extends Service {

    private IBinder mBinder;

    private int mCount=0;
    private CompositeSubscription mCompositeSubscription;

    /**
     *  onCreate invoked when startService first executing.
     *  if startService executes again, onCreate not invoked again
     */
    @Override
    public void onCreate() {
        super.onCreate();
        MyLogger.d(getClass(),"onCreate");

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
        mCompositeSubscription = new CompositeSubscription();
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

        Subscription subscription=Observable.interval(3, TimeUnit.MINUTES)
                .subscribe(new Subscriber<Long>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Long aLong) {
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
                    }
                });

        mCompositeSubscription.add(subscription);

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

        mCompositeSubscription.clear();
    }

    public static class MyBinder extends Binder{

        public void startWork(){
            MyLogger.d(getClass(),"startWork");
        }
    }
}
