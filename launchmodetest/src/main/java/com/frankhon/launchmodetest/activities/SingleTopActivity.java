package com.frankhon.launchmodetest.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.frankhon.launchmodetest.R;
import com.frankhon.launchmodetest.R2;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Frank_Hon on 9/30/2019.
 * E-mail: v-shhong@microsoft.com
 */
public class SingleTopActivity extends AppCompatActivity {

    private static final String TAG = SingleTopActivity.class.getSimpleName();

    @BindView(R2.id.tv_launch_mode)
    TextView hint;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_launch_mode_single_top);
        ButterKnife.bind(this);

        hint.setText("Single Top");
        Log.d(TAG, "onCreate: "+this+" task id: "+getTaskId());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d(TAG, "onNewIntent: "+this+" task id: "+getTaskId());
    }

    @OnClick(R2.id.btn_next)
    public void next() {
        Intent intent = new Intent(this, SingleTopActivity.class);
        startActivity(intent);
    }

    @OnClick(R2.id.btn_new_task)
    public void newTask(){
        Intent intent = new Intent(this, SingleTopActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
