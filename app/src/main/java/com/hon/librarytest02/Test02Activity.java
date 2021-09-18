package com.hon.librarytest02;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.OnClick;

import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.hon.apt_annotation.BindView;
import com.hon.apt_annotation.MyButterKnife;
import com.hon.mylogger.MyLogger;

/**
 * Created by Frank_Hon on 1/10/2019.
 * E-mail: v-shhong@microsoft.com
 */
public class Test02Activity extends AppCompatActivity{

    @BindView(R.id.test)
    TextView textView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_test_constraint);
        MyButterKnife.bind(this);
        textView.setText("hahaha...");
    }

//    @OnClick({R.id.test})
    public void onClick(View view){
//        Toast.makeText(this,"qqqqq",Toast.LENGTH_SHORT).show();
//        MyLogger.i(getClass(),"oop");
//        MyLogger.d(getClass(),"hello world !");
    }
}
