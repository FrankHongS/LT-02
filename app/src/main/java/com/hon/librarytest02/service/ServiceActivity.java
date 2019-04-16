package com.hon.librarytest02.service;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;

import com.hon.librarytest02.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

/**
 * Created by Frank_Hon on 2/18/2019.
 * E-mail: v-shhong@microsoft.com
 */
public class ServiceActivity extends AppCompatActivity {

    private ServiceConnection mConnection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Service01.MyBinder binder= (Service01.MyBinder) service;
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
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 0:
                if(grantResults[0]==PERMISSION_GRANTED ){
                    startService(new Intent(this,Service01.class));
                }
                break;
            default:
                break;
        }
    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.btn_start_service:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    requestPermissions(new String[]{Manifest.permission.FOREGROUND_SERVICE},0);
                }else{
                    startService(new Intent(this,Service01.class));
                }
                break;
            case R.id.btn_stop_service:
                stopService(new Intent(this,Service01.class));
                break;
            case R.id.btn_bind_service:
                bindService(new Intent(this,Service01.class),
                        mConnection,BIND_AUTO_CREATE);
                break;
            case R.id.btn_unbind_service:
                unbindService(mConnection);
                break;
            default:
                break;
        }
    }
}
