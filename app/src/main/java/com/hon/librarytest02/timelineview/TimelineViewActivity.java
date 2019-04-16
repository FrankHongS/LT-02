package com.hon.librarytest02.timelineview;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.hon.librarytest02.R;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by Frank_Hon on 3/5/2019.
 * E-mail: v-shhong@microsoft.com
 */
@SuppressWarnings("all")
public class TimelineViewActivity extends AppCompatActivity {

    private TimelineView mTimelineView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_timeline_view);

        initView();
    }

    private void initView(){
        mTimelineView=findViewById(R.id.tlv_test);
        mTimelineView.setOnNodeClickListener(
                position -> {
                    Toast.makeText(TimelineViewActivity.this, position+"", Toast.LENGTH_SHORT).show();
                }
        );
    }
}
