package com.hon.librarytest02.workmanager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.hon.librarytest02.R;

import java.util.concurrent.TimeUnit;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.work.Constraints;
import androidx.work.Logger;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import static androidx.work.PeriodicWorkRequest.MIN_PERIODIC_FLEX_MILLIS;
import static androidx.work.PeriodicWorkRequest.MIN_PERIODIC_INTERVAL_MILLIS;

/**
 * Created by Frank_Hon on 12/13/2018.
 * E-mail: v-shhong@microsoft.com
 */
public class WorkManagerActivity extends AppCompatActivity {

    private WorkRequest mWorkRequest;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_manager);

        init();
    }

    private void init() {

//        Constraints myConstraints=new Constraints.Builder()
//                .setRequiresCharging(true)
//                .setRequiresDeviceIdle(true)
//                .build();

//        mWorkRequest=new OneTimeWorkRequest.Builder(CompressWorker.class)
//                .setConstraints(myConstraints)
//                .build();


        mWorkRequest = new PeriodicWorkRequest.Builder(PhotoCheckWorker.class, MIN_PERIODIC_INTERVAL_MILLIS, TimeUnit.MILLISECONDS,
                MIN_PERIODIC_FLEX_MILLIS,TimeUnit.MILLISECONDS)
                .build();

        WorkManager.getInstance().getWorkInfoByIdLiveData(mWorkRequest.getId())
                .observe(this, workInfo -> {
                            if (workInfo != null) {
                                Log.d("PhotoCheckWorker", "workInfo: " + workInfo.getState().ordinal());
                                if (workInfo.getState().isFinished()) {
                                    Log.d("PhotoCheckWorker", "success :)");
                                    Toast.makeText(this, "success", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                );
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_start:
                WorkManager.getInstance().enqueue(mWorkRequest);
                break;
            case R.id.btn_stop:
                WorkManager.getInstance().cancelWorkById(mWorkRequest.getId());
                break;
            default:
                break;
        }
    }
}
