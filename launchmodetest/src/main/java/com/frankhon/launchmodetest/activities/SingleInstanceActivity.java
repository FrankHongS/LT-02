package com.frankhon.launchmodetest.activities;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.frankhon.launchmodetest.R;
import com.frankhon.launchmodetest.R2;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Frank_Hon on 9/30/2019.
 * E-mail: v-shhong@microsoft.com
 */
public class SingleInstanceActivity extends BaseLaunchModeActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        hint.setText("Single Instance");
    }
}
