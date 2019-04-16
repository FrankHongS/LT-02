package com.hon.librarytest02;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

/**
 * Created by Frank_Hon on 3/7/2019.
 * E-mail: v-shhong@microsoft.com
 */
public class LibraryTest extends Application {

    @SuppressLint("StaticFieldLeak")
    public static Context sContext;

    @Override
    public void onCreate() {
        super.onCreate();

        sContext=getApplicationContext();
    }

}
