package com.hon.librarytest02.androidarccomponents.viewmodel;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.hon.librarytest02.R;

/**
 * Created by Frank Hon on 2020/12/7 1:28 PM.
 * E-mail: frank_hon@foxmail.com
 */
public class VMActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewmodel);

        TextView numText = findViewById(R.id.tv_number);
        Button addButton = findViewById(R.id.btn_add);

        NumberViewModel numberViewModel = new ViewModelProvider(this).get(NumberViewModel.class);
        numberViewModel.numberLiveData.observe(this, s -> {
            // do nothing
        });
        numText.setText(String.valueOf(numberViewModel.number));
        addButton.setOnClickListener(v -> {
            numberViewModel.updateNumber();
            numText.setText(String.valueOf(numberViewModel.number));
        });
    }
}
