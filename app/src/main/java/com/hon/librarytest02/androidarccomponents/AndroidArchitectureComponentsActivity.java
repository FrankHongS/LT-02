package com.hon.librarytest02.androidarccomponents;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.hon.librarytest02.R;
import com.hon.librarytest02.androidarccomponents.viewmodel.VMActivity;

/**
 * Created by Frank Hon on 2020/12/7 1:38 PM.
 * E-mail: frank_hon@foxmail.com
 */
public class AndroidArchitectureComponentsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aac);

        findViewById(R.id.btn_viewmodel)
                .setOnClickListener(v -> startActivity(new Intent(this, VMActivity.class)));
        findViewById(R.id.btn_lifecycle)
                .setOnClickListener(v -> startActivity(new Intent(this, LifecycleActivity.class)));
    }
}
