package com.hon.librarytest02.saveInstance;


import com.hon.mylogger.MyLogger;

import java.util.Random;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Frank_Hon on 7/11/2019.
 * E-mail: v-shhong@microsoft.com
 */
public class MaskService {

    private static volatile MaskService instance;

    private MaskService(){}

    public static MaskService getInstance(){
        if (instance==null){
            synchronized (MaskService.class){
                if (instance==null){
                    instance=new MaskService();
                }
            }
        }

        return instance;
    }

    public Observable<String> setName(String name){
        MyLogger.d(name);
        return Observable.just(name)
                .flatMap(s->{
                    Thread.sleep(3000);
                    int r=new Random().nextInt(10);

                    if(r%2==0){
                        return Observable.error(new RuntimeException("even number is bad ..."));
                    }else {
                        return Observable.just(s);
                    }
                })
                .subscribeOn(Schedulers.io());
    }

}
