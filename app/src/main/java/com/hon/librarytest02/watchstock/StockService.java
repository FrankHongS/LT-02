package com.hon.librarytest02.watchstock;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import com.hon.librarytest02.network.ApiServiceImpl;
import com.hon.librarytest02.util.Constants;
import com.hon.librarytest02.util.Util;
import com.hon.librarytest02.vo.Stock;
import com.hon.mylogger.MyLogger;

import java.util.concurrent.TimeUnit;

import androidx.annotation.Nullable;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Frank_Hon on 4/28/2019.
 * E-mail: v-shhong@microsoft.com
 */
public class StockService extends Service {

    private Disposable mStockDisposable;

    private ApiServiceImpl mApiService;

    @Override
    public void onCreate() {
        MyLogger.d(getClass(), "onCreate");
        super.onCreate();

        mApiService=ApiServiceImpl.getInstance();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        MyLogger.d(getClass(), "onStartCommand");

        Toast.makeText(this, "service is watching stock :)", Toast.LENGTH_SHORT).show();

        String stockId=intent.getStringExtra(WatchStockActivity.KEY_STOCK_ID);
        int increase=intent.getIntExtra(WatchStockActivity.KEY_INCREASE,2);
        int decrease=intent.getIntExtra(WatchStockActivity.KEY_DECREASE,2);

        mStockDisposable = Observable
                .interval(1, TimeUnit.MINUTES)
                .flatMap(aLong -> mApiService.getStockInfo(stockId))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                       stock -> {
                           if("200".equals(stock.resultCode)){
                               updateStockInfo(stock,increase,decrease);
                           }
                       }
                );

        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        MyLogger.d(getClass(), "onDestroy");
        Toast.makeText(this, "service is stopping", Toast.LENGTH_SHORT).show();
        super.onDestroy();
        mStockDisposable.dispose();
    }

    private void updateStockInfo(Stock stock, int increase, int decrease){
        Stock.StockResult result=stock.result.get(0);
        float increasePercent=Math.round(Float.valueOf(result.data.increasePer)*100)/100f;

        String title=result.data.name;

        String percent;
        if(increasePercent>=0){
            percent="涨 "+increasePercent+"%";
        }else {
            percent="跌 "+(-increasePercent)+"%";
        }

        String nowPrice=result.data.nowPrice;

        String text=String.format(("%s   现价 %s"),percent,nowPrice);
        if((increasePercent<0&&-increasePercent<decrease)||(increasePercent>0&&increasePercent<increase)) {
            sendNotification(Constants.StockNotification.STOCK_CHANNEL_ID,
                    title,
                    text
            );
        }else {
            sendNotification(Constants.StockNotification.STOCK_WATCH_OUT_CHANNEL_ID,
                    title,
                    text
            );
        }
    }

    private void sendNotification(String channelId,String title, String text) {
        Notification notification = Util.createNotification(WatchStockActivity.class,
                channelId,
                title,
                text);
        startForeground(1, notification);

    }
}
