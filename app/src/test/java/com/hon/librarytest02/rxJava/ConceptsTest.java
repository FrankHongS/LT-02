package com.hon.librarytest02.rxJava;

import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Frank_Hon on 6/21/2019.
 * E-mail: v-shhong@microsoft.com
 */
public class ConceptsTest {

    // Flowable with backpressure
    // Observable without backpressure
    @Test
    public void assemblyTime() {
        Flowable<Integer> flow = Flowable.range(1, 5)
                .map(v -> v * v)
                .filter(v -> v % 3 == 0);

        Disposable disposable = flow.subscribe(System.out::println);
        disposable.dispose();

        Observable<Integer> observable = Observable.range(1, 5)
                .map(v -> v * v)
                .filter(v -> v % 3 == 0);

        Disposable disposable1 = observable.subscribe(System.out::println);
        disposable1.dispose();
    }

    @Test
    public void schedulers() throws InterruptedException {

//        Disposable disposable = Observable.create(emitter -> {
//            System.out.println(Thread.currentThread().getName()+" "+Thread.currentThread().getId());
//            while (!emitter.isDisposed()) {
//                long time = System.currentTimeMillis();
//                emitter.onNext(time);
//                if (time % 2 != 0) {
//                    emitter.onError(new IllegalStateException("Odd millisecond!"));
//                    break;
//                }
//            }
//        })
//                .subscribeOn(Schedulers.io())
//                .observeOn(Schedulers.io())
//                .subscribe(o -> {
//                    System.out.println(o);
//                    System.out.println("onNext: "+Thread.currentThread().getName()+" "+Thread.currentThread().getId());
//                }, throwable -> {
//                    throwable.printStackTrace();
//                    System.out.println("onError: "+Thread.currentThread().getName()+" "+Thread.currentThread().getId());
//                });


        Disposable disposable = Flowable.just(1, 2, 3, 4, 5)
                .subscribeOn(Schedulers.single())
                .observeOn(Schedulers.io())
                .map(i -> {
                    if (i == 4) {
                        System.out.println("map: "+Thread.currentThread().getName() + " " + Thread.currentThread().getId());
                        throw new IllegalStateException("2 is bad...");
                    }
                    return i * i;
                })
                .subscribe(
                        i -> {
                            System.out.println(i);
                            System.out.println("onNext: "+Thread.currentThread().getName() + " " + Thread.currentThread().getId());
                        },
                        throwable -> {
                            throwable.printStackTrace();
                            System.out.println("onError: "+Thread.currentThread().getName() + " " + Thread.currentThread().getId());
                        },
                        ()->{
                            System.out.println("onComplete: "+Thread.currentThread().getName() + " " + Thread.currentThread().getId());
                        }
                );

        Thread.sleep(3000);
        disposable.dispose();
    }

    @Test
    public void concurrent(){
        Flowable.range(1, 10)
                .parallel()
                .runOn(Schedulers.computation())
                .map(v -> v * v)
                .sequential()
                .blockingSubscribe(System.out::println);
    }

    @Test
    public void deferDependent(){
        AtomicInteger count = new AtomicInteger();

        Observable.range(1, 10)
                .doOnNext(ignored -> count.incrementAndGet())
                .ignoreElements()
                .andThen(Single.just(count.get()))
                .subscribe(System.out::println);
    }
}