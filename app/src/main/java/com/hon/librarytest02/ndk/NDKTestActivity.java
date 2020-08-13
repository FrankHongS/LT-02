package com.hon.librarytest02.ndk;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.hon.librarytest02.R;

/**
 * Created by Frank Hon on 2020/8/14 12:52 AM.
 * E-mail: frank_hon@foxmail.com
 */
public class NDKTestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_ndk);
        TextView text = findViewById(R.id.tv_ndk);
        text.setText(stringFromJNI());
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}
