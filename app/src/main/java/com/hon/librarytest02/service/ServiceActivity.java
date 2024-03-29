package com.hon.librarytest02.service;

import android.Manifest;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.hon.librarytest02.R;
import com.hon.librarytest02.util.Constants;
import com.hon.librarytest02.util.Util;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

/**
 * Created by Frank_Hon on 2/18/2019.
 * E-mail: v-shhong@microsoft.com
 */
public class ServiceActivity extends AppCompatActivity {

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Service01.MyBinder binder = (Service01.MyBinder) service;
            binder.startWork();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Util.createNotificationChannel("player", "player",
                    NotificationManager.IMPORTANCE_LOW);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 0:
                if (grantResults[0] == PERMISSION_GRANTED) {
                    startService(new Intent(this, Service01.class));
                    bindService(new Intent(this, Service01.class),
                            mConnection, BIND_AUTO_CREATE);
                }
                break;
            default:
                break;
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_start_service:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    requestPermissions(new String[]{Manifest.permission.FOREGROUND_SERVICE}, 0);
                } else {
                    startService(new Intent(this, Service01.class));
                }
                break;
            case R.id.btn_stop_service:
                stopService(new Intent(this, Service01.class));
                break;
            case R.id.btn_bind_service:
                bindService(new Intent(this, Service01.class),
                        mConnection, BIND_AUTO_CREATE);
                break;
            case R.id.btn_unbind_service:
                unbindService(mConnection);
                break;
            case R.id.btn_start_intent_service:
                startService(new Intent(this, Service02.class));
                break;
            case R.id.btn_stop_intent_service:
                stopService(new Intent(this, Service02.class));
                break;
            case R.id.btn_start_media:
                startService(new Intent(this, MediaPlaybackService.class));
                break;
            default:
                break;
        }
    }
}
