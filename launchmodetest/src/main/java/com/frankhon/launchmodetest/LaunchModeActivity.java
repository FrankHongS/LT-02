package com.frankhon.launchmodetest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.frankhon.launchmodetest.activities.SingleInstanceActivity;
import com.frankhon.launchmodetest.activities.SingleTaskActivity;
import com.frankhon.launchmodetest.activities.SingleTopActivity;
import com.frankhon.launchmodetest.activities.StandardActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Frank_Hon on 9/30/2019.
 * E-mail: v-shhong@microsoft.com
 */
public class LaunchModeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_launch_mode);
        ButterKnife.bind(this);
    }

    @OnClick(R2.id.btn_standard)
    void standard() {
        navigateTo(StandardActivity.class);
    }

    @OnClick(R2.id.btn_single_top)
    void singleTop() {
        navigateTo(SingleTopActivity.class);
    }

    @OnClick(R2.id.btn_single_task)
    void singleTask() {
        navigateTo(SingleTaskActivity.class);
    }

    @OnClick(R2.id.btn_single_instance)
    void singleInstance() {
        navigateTo(SingleInstanceActivity.class);
    }

    private void navigateTo(Class<? extends Activity> target){
        startActivity(new Intent(this, target));
    }
}
