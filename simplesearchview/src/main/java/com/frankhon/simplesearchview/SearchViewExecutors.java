package com.frankhon.simplesearchview;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Frank Hon on 2020-06-07 17:10.
 * E-mail: frank_hon@foxmail.com
 */
class SearchViewExecutors {
    private ExecutorService diskIO;
    private MainThreadExecutor mainThread;

    Executor getDiskIO() {
        if (diskIO == null) {
            diskIO = Executors.newSingleThreadExecutor();
        }
        return diskIO;
    }

    Executor getMainThread() {
        if (mainThread == null) {
            mainThread = new MainThreadExecutor();
        }
        return mainThread;
    }

    void release() {
        diskIO.shutdown();
        mainThread.release();
    }

    static final class MainThreadExecutor implements Executor {

        private final Handler mainThreadHandler;

        private MainThreadExecutor() {
            this.mainThreadHandler = new Handler(Looper.getMainLooper());
        }

        @Override
        public void execute(Runnable command) {
            mainThreadHandler.post(command);
        }

        public void release() {
            mainThreadHandler.removeCallbacksAndMessages(null);
        }
    }
}
