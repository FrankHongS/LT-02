package com.hon.librarytest02;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

import com.hon.mylogger.MyCrashHandler;
import com.hon.mylogger.MyLogger;

/**
 * Created by Frank_Hon on 3/7/2019.
 * E-mail: v-shhong@microsoft.com
 */
public class LibraryTest extends Application {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    @SuppressLint("StaticFieldLeak")
    public static Context sContext;

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = getApplicationContext();

        MyLogger.initLogFilePath(getFilesDir().getPath());
        MyCrashHandler.init("1.0.1");
    }

}
