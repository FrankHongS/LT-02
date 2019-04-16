package com.hon.librarytest02.util;

import android.content.res.Resources;

import com.hon.librarytest02.LibraryTest;

/**
 * Created by Frank_Hon on 3/7/2019.
 * E-mail: v-shhong@microsoft.com
 */
public class Util {

    public static String getResourceString(int resId){
        return LibraryTest.sContext.getResources().getString(resId);
    }

    public static int dp2px(float value) {
        final float scale = Resources.getSystem().getDisplayMetrics().densityDpi;
        return (int) (value * (scale / 160) + 0.5f);
    }
}
