package com.hon.librarytest02.watchstock;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.hon.librarytest02.network.ApiServiceImpl;
import com.hon.librarytest02.util.Constants;
import com.hon.librarytest02.util.Util;
import com.hon.librarytest02.vo.Stock;
import com.hon.mylogger.MyLogger;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
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
        MyLogger.d("onCreate");
        super.onCreate();

        mApiService = ApiServiceImpl.getInstance();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        MyLogger.d("onStartCommand");

        Toast.makeText(this, "service is watching stock :)", Toast.LENGTH_SHORT).show();

        sendNotification(Constants.StockNotification.STOCK_CHANNEL_ID,
                "Hello",
                "service is watching stock :)");

        String stockId = intent.getStringExtra(WatchStockActivity.KEY_STOCK_ID);
        float cost = intent.getFloatExtra(WatchStockActivity.KEY_COST, 0f);
        int increase = intent.getIntExtra(WatchStockActivity.KEY_INCREASE, 2);
        int decrease = intent.getIntExtra(WatchStockActivity.KEY_DECREASE, 2);
        int profit = intent.getIntExtra(WatchStockActivity.KEY_PROFIT, 20);
        int loss = intent.getIntExtra(WatchStockActivity.KEY_LOSS, 10);

        mStockDisposable = Observable
                .interval(2, TimeUnit.MINUTES)
                .flatMap(aLong -> mApiService.getStockInfo(stockId))
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(throwable -> {/*do nothing*/})
                .subscribe(
                        stock -> {
                            if ("200".equals(stock.resultCode)) {
                                updateStockInfo(stock, cost, increase, decrease, profit, loss);
                            }
                        }
                );

        return START_REDELIVER_INTENT;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        MyLogger.d("onDestroy");
        Toast.makeText(this, "service is stopping", Toast.LENGTH_SHORT).show();
        super.onDestroy();
        mStockDisposable.dispose();
    }

    private void updateStockInfo(Stock stock, float cost, int increase, int decrease, int profit, int loss) {
        Stock.StockResult result = stock.result.get(0);
        float increasePercent = Math.round(Float.valueOf(result.data.increasePer) * 100) / 100f;

        String title = result.data.name;

        String percent;
        if (increasePercent >= 0) {
            percent = "涨 " + increasePercent + "%";
        } else {
            percent = "跌 " + (-increasePercent) + "%";
        }

        String nowPrice = result.data.nowPrice;

        String text = String.format(("%s   现价 %s"), percent, nowPrice);

        if ((increasePercent < 0 && -increasePercent >= decrease) || (increasePercent > 0 && increasePercent >= increase)) {
            sendNotification(Constants.StockNotification.STOCK_WATCH_OUT_CHANNEL_ID,
                    title,
                    text
            );
        } else if (checkPrice(nowPrice, cost, profit, loss)) {
            sendNotification(Constants.StockNotification.STOCK_WATCH_OUT_CHANNEL_ID,
                    title,
                    text
            );
        } else {
            sendNotification(Constants.StockNotification.STOCK_CHANNEL_ID,
                    title,
                    String.format(("%s   现价 %s"), "***", "**")
            );
        }
    }

    private boolean checkPrice(String nowPrice, float cost, int profit, int loss) {
        float curPrice = Float.valueOf(nowPrice);
        if (curPrice > cost) {
            float increaseRate = (curPrice - cost) / cost;
            return increaseRate >= profit * 1f / 100;
        } else {
            float decreaseRate = (cost - curPrice) / cost;
            return decreaseRate > loss * 1f / 100;
        }

    }

    private void sendNotification(String channelId, String title, String text) {
        Notification notification = Util.createNotification(WatchStockActivity.class,
                channelId,
                title,
                text);
        startForeground(1, notification);

    }
}
