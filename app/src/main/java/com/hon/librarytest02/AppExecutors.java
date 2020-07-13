package com.hon.librarytest02;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by Frank Hon on 2019-08-18 23:45.
 * E-mail: frank_hon@foxmail.com
 */
public final class AppExecutors {

    private static volatile AppExecutors INSTANCE;

    private Executor diskIO;
    private Executor networkIO;

    private AppExecutors() {
        diskIO = Executors.newSingleThreadExecutor();
        networkIO = Executors.newFixedThreadPool(10);
    }

    public static AppExecutors getInstance() {
        if (INSTANCE == null) {
            synchronized (AppExecutors.class) {
                if (INSTANCE == null) {
                    INSTANCE = new AppExecutors();
                }
            }
        }

        return INSTANCE;
    }

    public Executor getDiskIO() {
        return diskIO;
    }

    public Executor getNetworkIO() {
        return networkIO;
    }
}
