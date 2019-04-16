package com.hon.librarytest02.text;

import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.widget.TextView;

import com.hon.librarytest02.R;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by Frank_Hon on 3/15/2019.
 * E-mail: v-shhong@microsoft.com
 */
public class TextActivity extends AppCompatActivity {

    private TextView textView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text);

        initView();
    }

    private void initView() {
//        textView=findViewById(R.id.tv_text);

//        CharSequence text=textView.getText();
//        SpannableStringBuilder ssb = new SpannableStringBuilder(text);
//        ssb.setSpan(new BackgroundColorSpan(0x2f303F9F), 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        textView.setText(ssb);
    }
}
