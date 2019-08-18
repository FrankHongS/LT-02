package com.hon.librarytest02.rxJava;

import org.junit.Test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;
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
                .map(i -> {
                    if (i == 4) {
                        System.out.println("map: " + Thread.currentThread().getName() + " " + Thread.currentThread().getId());
                        throw new IllegalStateException("2 is bad...");
                    }
                    return i * i;
                })
                .observeOn(Schedulers.io())
                .subscribe(
                        i -> {
                            System.out.println(i);
                            System.out.println("onNext: " + Thread.currentThread().getName() + " " + Thread.currentThread().getId());
                        },
                        throwable -> {
                            throwable.printStackTrace();
                            System.out.println("onError: " + Thread.currentThread().getName() + " " + Thread.currentThread().getId());
                        },
                        () -> {
                            System.out.println("onComplete: " + Thread.currentThread().getName() + " " + Thread.currentThread().getId());
                        }
                );

        Thread.sleep(3000);
        disposable.dispose();
    }

    @Test
    public void concurrent() {
        Flowable.range(1, 10)
                .parallel()
                .runOn(Schedulers.computation())
                .map(v -> v * v)
                .sequential()
                .blockingSubscribe(System.out::println);
    }

    @Test
    public void deferDependent() {
        AtomicInteger count = new AtomicInteger();

        Observable.range(1, 10)
                .doOnNext(ignored -> count.incrementAndGet())
                .ignoreElements()
                .andThen(Single.just(count.get()))
                .subscribe(System.out::println);
    }

    @Test
    public void lifecycle() {
        Observable.range(1, 10)
                .map(i -> {
                    Thread.sleep(2000);
                    return i * i;
                })
                .doOnSubscribe(disposable -> System.out.println("onSubscribe"))
                .subscribe(
                        i -> {
                            System.out.println(i);
                            System.out.println(Thread.currentThread().getName() + " " + Thread.currentThread().getId());
                        },
                        Throwable::printStackTrace
                );

        System.out.println("onComplete: " + Thread.currentThread().getName() + " " + Thread.currentThread().getId());
    }

    @Test
    public void compose() {
        Observable.range(1, 10)
                .compose(upstream ->
                        upstream.subscribeOn(Schedulers.io())
                                .observeOn(Schedulers.single())
                ).subscribe();
    }

    @Test
    public void operator() throws InterruptedException {
        int[] numbers = {1, 3, 5, 6, 8};

        Flowable.defer(() -> Flowable.fromArray(1, 3, 5, 6, 8))
                .distinct()
                .take(3)
                .map(i -> {
                    printThreadName("outer");
                    return i;
                })
//                .subscribeOn(Schedulers.io())
                .concatMapEager(i -> {
//                    Thread.sleep((10-i)*100);
                    return Flowable.just(i)
                            .map(n -> {
                                printThreadName("inner");
                                return n * n;
                            })
                            .subscribeOn(Schedulers.io())
                            .observeOn(Schedulers.single())
                            ;
                })
//                .subscribeOn(Schedulers.io())
//                .map(i->{
//                    printThreadName("outer2");
//                    return i;
//                })
//                .observeOn(Schedulers.single())
                .subscribe(
                        i -> {
                            System.out.println(i);
                            printThreadName("onNext");
                        }
                );

        Thread.sleep(3000);
    }

    @SuppressWarnings("all")
    @Test
    public void operator02() {

        Observable.just("1")
                .map(new Function<String, Integer>() {
                    @Override
                    public Integer apply(String s) throws Exception {
                        return Integer.valueOf(s);
                    }
                })
                .subscribe(System.out::println);


        Observable.just(1)
                .flatMap(new Function<Integer, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(Integer i) throws Exception {
                        return Observable.fromArray("Joey", "Chandler", "Ross");
                    }
                })
                .subscribe(System.out::println);

        Observable<Integer> observable1 = Observable.just(1, 2, 3, 4);
        Observable<Integer> observable2 = Observable.just(3, 4, 5);

        observable1.zipWith(observable2, (first, last) -> first + last)
                .subscribe(System.out::println);
    }

    @SuppressWarnings("all")
    @Test
    public void backpressure() throws InterruptedException {
        Observable.interval(1, TimeUnit.MICROSECONDS)
                .flatMap(aLong -> Observable.range(0,10000000))
                .map(num->String.valueOf(num))
                .observeOn(Schedulers.newThread())
                .subscribe(num -> {
                            Thread.sleep(1000);
                            System.out.println(num);
                        },
                        Throwable::printStackTrace);

        Thread.sleep(10000);

        Flowable.create()
    }

    private void printThreadName(String tag) {
        System.out.println(tag + ": " + Thread.currentThread().getName() + " " + Thread.currentThread().getId());
    }
}