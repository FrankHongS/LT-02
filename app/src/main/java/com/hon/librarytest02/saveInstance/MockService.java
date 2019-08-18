package com.hon.librarytest02.saveInstance;


import android.os.Handler;
import android.os.Looper;

import com.hon.mylogger.MyLogger;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.mock.Calls;

/**
 * Created by Frank_Hon on 7/11/2019.
 * E-mail: v-shhong@microsoft.com
 */
public class MockService {

    private static volatile MockService instance;

    private MockService() {
    }

    public static MockService getInstance() {
        if (instance == null) {
            synchronized (MockService.class) {
                if (instance == null) {
                    instance = new MockService();
                }
            }
        }

        return instance;
    }

    public Observable<String> setName(String name) {
        MyLogger.d(name);
        return Observable.just(name)
                .flatMap(s -> {
                    Thread.sleep(3000);
                    int r = new Random().nextInt(10);

                    if (r % 2 == 0) {
                        return Observable.error(new RuntimeException("even number is bad ..."));
                    } else {
                        return Observable.just(s);
                    }
                });
    }

    public Observable<String> checkName(String name) {
        MyLogger.d(name);
        return Observable.just(name)
                .flatMap(s -> {
                    Thread.sleep(1000);
                    int r = new Random().nextInt(10);

                    if (r % 2 == 0) {
                        return Observable.error(new RuntimeException("name is invalid ..."));
                    } else {
                        return Observable.just(s);
                    }
                });
    }

//    public Call<String> setName(String name) {
//        return Calls.defer(() -> {
//            MyLogger.d(name);
//
////            Thread.sleep(3000);
//            int r = new Random().nextInt(10);
//
//            if (r % 2 == 0) {
//                return Calls.response(name);
//            } else {
//                return Calls.failure(new IOException("even number is bad ..."));
//            }
//        });
//    }
//
//    public Call<String> checkName(String name) {
//
//        return Calls.defer(() -> {
//            MyLogger.d(name);
//
////            Thread.sleep(1000);
//            int r = new Random().nextInt(10);
//
//            if (r % 2 == 0) {
//                return Calls.response(name);
//            } else {
//                return Calls.failure(new IOException("name is invalid ..."));
//            }
//        });
//    }

}
