package com.frankhon.launchmodetest.activities;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by Frank_Hon on 9/30/2019.
 * E-mail: v-shhong@microsoft.com
 */
public class SingleTopActivity extends BaseLaunchModeActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        hint.setText("Single Top");
    }
}
