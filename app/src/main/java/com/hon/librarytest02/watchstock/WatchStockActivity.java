package com.hon.librarytest02.watchstock;

import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.hon.librarytest02.MainActivity;
import com.hon.librarytest02.R;
import com.hon.librarytest02.util.Constants;
import com.hon.librarytest02.util.SharedPreferenceManager;
import com.hon.librarytest02.util.Util;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Frank_Hon on 4/28/2019.
 * E-mail: v-shhong@microsoft.com
 */
public class WatchStockActivity extends AppCompatActivity {

    public static final String KEY_STOCK_ID = "stock_id";
    public static final String KEY_INCREASE = "increase";
    public static final String KEY_DECREASE = "decrease";
    public static final String KEY_COST = "cost";
    public static final String KEY_PROFIT = "profit";
    public static final String KEY_LOSS = "loss";

    @BindView(R.id.et_stock_id)
    EditText stockIdText;
    @BindView(R.id.et_cost)
    EditText costText;
    @BindView(R.id.et_increase)
    EditText increaseText;
    @BindView(R.id.et_decrease)
    EditText decreaseText;
    @BindView(R.id.et_profit)
    EditText profitText;
    @BindView(R.id.et_loss)
    EditText lossText;

    private SharedPreferenceManager mSharedPreferenceManager = SharedPreferenceManager.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_watch_stock);

        ButterKnife.bind(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Util.createNotificationChannel(Constants.StockNotification.STOCK_CHANNEL_ID, "normal",
                    NotificationManager.IMPORTANCE_LOW);
            Util.createNotificationChannel(Constants.StockNotification.STOCK_WATCH_OUT_CHANNEL_ID, "important",
                    NotificationManager.IMPORTANCE_HIGH);
        }

        initView();
    }

    private void initView() {
        stockIdText.setText(mSharedPreferenceManager.getString(KEY_STOCK_ID));
        costText.setText(mSharedPreferenceManager.getString(KEY_COST));
        increaseText.setText(mSharedPreferenceManager.getString(KEY_INCREASE));
        decreaseText.setText(mSharedPreferenceManager.getString(KEY_DECREASE));
        profitText.setText(mSharedPreferenceManager.getString(KEY_PROFIT));
        lossText.setText(mSharedPreferenceManager.getString(KEY_LOSS));
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_start:
                String stockId = stockIdText.getText().toString();
                String cost = costText.getText().toString();
                String increase = increaseText.getText().toString();
                String decrease = decreaseText.getText().toString();
                String profit = profitText.getText().toString();
                String loss = lossText.getText().toString();

                if (!TextUtils.isEmpty(stockId)
                        && !TextUtils.isEmpty(increase)
                        && !TextUtils.isEmpty(decrease)
                        && !TextUtils.isEmpty(cost)
                        && !TextUtils.isEmpty(profit)
                        && !TextUtils.isEmpty(loss)) {
                    Intent stockIntent = new Intent(this, StockService.class);
                    stockIntent.putExtra(KEY_STOCK_ID, stockId);
                    stockIntent.putExtra(KEY_COST, Float.valueOf(cost));
                    stockIntent.putExtra(KEY_INCREASE, Integer.valueOf(increase));
                    stockIntent.putExtra(KEY_DECREASE, Integer.valueOf(decrease));
                    stockIntent.putExtra(KEY_PROFIT, Integer.valueOf(profit));
                    stockIntent.putExtra(KEY_LOSS, Integer.valueOf(loss));
                    startService(stockIntent);
                } else {
                    Toast.makeText(this, "stock id is empty", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_stop:
                stopService(new Intent(this, StockService.class));
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, MainActivity.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSharedPreferenceManager.putString(KEY_STOCK_ID, stockIdText.getText().toString());
        mSharedPreferenceManager.putString(KEY_COST, costText.getText().toString());
        mSharedPreferenceManager.putString(KEY_INCREASE, increaseText.getText().toString());
        mSharedPreferenceManager.putString(KEY_DECREASE, decreaseText.getText().toString());
        mSharedPreferenceManager.putString(KEY_PROFIT, profitText.getText().toString());
        mSharedPreferenceManager.putString(KEY_LOSS, lossText.getText().toString());
    }
}
