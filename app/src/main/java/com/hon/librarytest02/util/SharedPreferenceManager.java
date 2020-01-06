package com.hon.librarytest02.util;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.hon.librarytest02.LibraryTest;

/**
 * Created by Frank_Hon on 1/6/2020.
 * E-mail: v-shhong@microsoft.com
 */
public class SharedPreferenceManager {

    private static volatile SharedPreferenceManager INSTANCE;

    private SharedPreferences mSharedPreferences;

    private SharedPreferenceManager() {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(LibraryTest.sContext);
    }

    public static SharedPreferenceManager getInstance() {
        if (INSTANCE == null) {
            synchronized (SharedPreferenceManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new SharedPreferenceManager();
                }
            }
        }

        return INSTANCE;
    }

    public void putInt(String key, int value) {
        mSharedPreferences.edit().putInt(key, value).apply();
    }

    public int getInt(String key) {
        return mSharedPreferences.getInt(key, 0);
    }

    public void putString(String key, String value) {
        mSharedPreferences.edit().putString(key, value).apply();
    }

    public String getString(String key) {
        return mSharedPreferences.getString(key, "");
    }

}
