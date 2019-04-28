package com.hon.librarytest02.watchstock;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.hon.librarytest02.R;
import com.hon.librarytest02.network.ApiServiceImpl;
import com.hon.librarytest02.util.Constants;
import com.hon.librarytest02.util.Util;
import com.hon.librarytest02.vo.Stock;
import com.hon.mylogger.MyLogger;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Frank_Hon on 4/28/2019.
 * E-mail: v-shhong@microsoft.com
 */
public class WatchStockActivity extends AppCompatActivity {

    public static final String KEY_STOCK_ID = "stock_id";
    public static final String KEY_INCREASE = "increase";
    public static final String KEY_DECREASE = "decrease";

    private EditText mStockIdText;
    private EditText mIncreaseText;
    private EditText mDecreaseText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_watch_stock);

        initView();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Util.createNotificationChannel(Constants.StockNotification.STOCK_CHANNEL_ID,"stock1",
                    NotificationManager.IMPORTANCE_LOW);
            Util.createNotificationChannel(Constants.StockNotification.STOCK_WATCH_OUT_CHANNEL_ID,"stock2",
                    NotificationManager.IMPORTANCE_HIGH);
        }
    }

    private void initView(){
        mStockIdText=findViewById(R.id.et_stock_id);
        mIncreaseText=findViewById(R.id.et_increase);
        mDecreaseText=findViewById(R.id.et_decrease);
    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.btn_start:
                String stockId=mStockIdText.getText().toString();
                String increase=mIncreaseText.getText().toString();
                String decrease=mDecreaseText.getText().toString();

                if(!TextUtils.isEmpty(stockId)
                        &&!TextUtils.isEmpty(increase)
                        &&!TextUtils.isEmpty(decrease)) {
                    Intent startIntent=new Intent(this,StockService.class);
                    startIntent.putExtra(KEY_STOCK_ID,stockId);
                    startIntent.putExtra(KEY_INCREASE,Integer.valueOf(increase));
                    startIntent.putExtra(KEY_DECREASE,Integer.valueOf(decrease));
                    startService(startIntent);
                }else {
                    Toast.makeText(this, "stock id is empty", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_stop:
                stopService(new Intent(this,StockService.class));
                break;
        }
    }
}
