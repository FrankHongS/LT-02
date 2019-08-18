package com.hon.librarytest02.saveInstance;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.hon.librarytest02.R;
import com.hon.mylogger.MyLogger;
import com.jakewharton.rxbinding3.view.RxView;

import org.jetbrains.annotations.NotNull;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.OnErrorNotImplementedException;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Frank_Hon on 7/10/2019.
 * E-mail: v-shhong@microsoft.com
 */
public class SaveInstanceActivity extends AppCompatActivity {

    @BindView(R.id.et_test)
    EditText nameView;
    @BindView(R.id.btn_submit)
    Button submitView;
    @BindView(R.id.pb_progress)
    ProgressBar progressView;

    private CompositeDisposable mDisposables;
    private MockService mockService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_instance);
        ButterKnife.bind(this);

        mockService = MockService.getInstance();

//        methodNormal();
        mDisposables = new CompositeDisposable();

//        Disposable disposable=method01();
//        Disposable disposable = method02();
        Disposable disposable = methodWithRxJava();

        mDisposables.add(disposable);
    }

//    private void methodNormal() {
//
//        submitView.setOnClickListener(v -> {
//            progressView.setVisibility(View.VISIBLE);
//            submitView.setEnabled(false);
//
//            String name = nameView.getText().toString();
//            mockService.checkName(name)
//                    .enqueue(new Callback<String>() {
//                        @Override
//                        public void onResponse(@NotNull Call<String> call, @NotNull Response<String> response) {
//                            mockService.setName(response.body())
//                                    .enqueue(new Callback<String>() {
//                                        @Override
//                                        public void onResponse(@NotNull Call<String> call, @NotNull Response<String> response) {
//                                            progressView.setVisibility(View.GONE);
//                                            submitView.setEnabled(true);
//                                            finish();
//                                        }
//
//                                        @Override
//                                        public void onFailure(@NotNull Call<String> call, @NotNull Throwable t) {
//                                            progressView.setVisibility(View.GONE);
//                                            submitView.setEnabled(true);
//                                            Toast.makeText(SaveInstanceActivity.this, "Fail to set Name: " + t.getMessage(), Toast.LENGTH_SHORT).show();
//                                        }
//                                    });
//
//                        }
//
//                        @Override
//                        public void onFailure(@NotNull Call<String> call, @NotNull Throwable t) {
//                            progressView.setVisibility(View.GONE);
//                            submitView.setEnabled(true);
//                            Toast.makeText(SaveInstanceActivity.this, "Fail to set Name: " + t.getMessage(), Toast.LENGTH_SHORT).show();
//                        }
//                    });
//
//        });
//    }

    private Disposable methodWithRxJava() {

        ObservableTransformer<SubmitEvent, SubmitUIModel> submit =
                events -> events
                        .flatMap(event -> mockService
                                .checkName(event.name)
                                .flatMap(name -> mockService.setName(name))
                                .map(response -> SubmitUIModel.success())
                                .onErrorReturn(t -> SubmitUIModel.failure(t.getMessage()))
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .startWith(SubmitUIModel.inProgress())
                        );

        return RxView.clicks(submitView)
                .map(ignored -> new SubmitEvent(nameView.getText().toString()))
                .compose(submit)
                .subscribe(
                        model -> {
                            submitView.setEnabled(!model.inProgress);
                            progressView.setVisibility(model.inProgress ? View.VISIBLE : View.GONE);
                            if (!model.inProgress) {
                                if (model.success) {
                                    finish();
                                } else {
                                    Toast.makeText(this, "Fail to set name: " + model.errorMessage,
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        }, t -> {
                            throw new OnErrorNotImplementedException(t);
                        }
                );
    }
//
//    private Disposable method01() {
//        //TODO bug 当横竖屏切换时，请求会被dispose
//        return RxView.clicks(submitView)
//                .doOnNext(ignored -> {
//                    submitView.setEnabled(false);
//                    progressView.setVisibility(View.VISIBLE);
//                })
//                //TODO 网络请求不应该直接依赖UI
//                .flatMap(ignored -> MockService.getInstance().setName(nameView.getText().toString()))
//                .observeOn(AndroidSchedulers.mainThread())
//                //TODO bug 在stream中抛出错误时，progressBar不会消失，只有成功时才会消失
//                .doOnNext(ignored -> progressView.setVisibility(View.GONE))
//                .subscribe(
//                        s -> finish(),
//                        //TODO bug 当发生错误，再次点击button不会重新请求
//                        //error is the terminal event.
//                        t -> {
//                            submitView.setEnabled(true);
//                            Toast.makeText(this, "Fail to set name: " + t.getMessage(), Toast.LENGTH_SHORT).show();
//                        }
//                );
//
//    }
//
//    private Disposable method02() {
//
//        ObservableTransformer<SubmitEvent, SubmitUIModel> submit =
//                events -> events
//                        .flatMap(event -> MockService.getInstance()
//                                .setName(event.name)
//                                .map(response -> SubmitUIModel.success())
//                                .onErrorReturn(t -> SubmitUIModel.failure(t.getMessage()))
//                                .observeOn(AndroidSchedulers.mainThread())
//                                .startWith(SubmitUIModel.inProgress()
//                                )
//                        );
//
//        return RxView.clicks(submitView)
//                .map(ignored -> new SubmitEvent(nameView.getText().toString()))
//                .compose(submit)
//                .subscribe(
//                        model -> {
//                            submitView.setEnabled(!model.inProgress);
//                            progressView.setVisibility(model.inProgress ? View.VISIBLE : View.GONE);
//                            if (!model.inProgress) {
//                                if (model.success) {
//                                    finish();
//                                } else {
//                                    Toast.makeText(this, "Fail to set name: " + model.errorMessage,
//                                            Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                        }, t -> {
//                            throw new OnErrorNotImplementedException(t);
//                        }
//                );
//    }
//
//    private Disposable method03() {
//
//        Observable<SubmitEvent> submitEvents = RxView.clicks(submitView)
//                .map(ignored -> new SubmitEvent(nameView.getText().toString()));
//
//        Observable<SubmitAction> submitActions = submitEvents.map(
//                event -> new SubmitAction(event.name)
//        );
//
//        SubmitUIModel initialState = SubmitUIModel.idle();
////
//        ObservableTransformer<SubmitAction, SubmitResult> submit =
//                actions -> actions.flatMap(action -> MockService.getInstance().setName(action.name)
//                        .map(response -> SubmitResult.SUCCESS)
//                        .onErrorReturn(t -> SubmitResult.failure(t.getMessage()))
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .startWith(SubmitResult.IN_FLIGHT));
//
//        Observable<SubmitResult> results = submitActions.compose(submit);
//
//        Observable<SubmitUIModel> uiModels = results.scan(initialState, (state, result) -> {
//            if (result == SubmitResult.IN_FLIGHT) {
//                return SubmitUIModel.inProgress();
//            }
//
//            if (result == SubmitResult.SUCCESS) {
//                return SubmitUIModel.success();
//            }
//
//            if (result == SubmitResult.FAILURE) {
//                return SubmitUIModel.failure(result.getErrorMessage());
//            }
//
//            throw new IllegalArgumentException("Unknown result: " + result);
//        });
////
////        ObservableTransformer<CheckNameAction, CheckNameResult> checkName =
////                actions -> actions.delay(200, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
////                                .flatMap(action -> MockService.getInstance().checkName(action.name)
////                                .map(response -> CheckNameResult.SUCCESS)
////                                .onErrorReturn(t -> CheckNameResult.failure(t.getMessage()))
////                                .observeOn(AndroidSchedulers.mainThread())
////                                .startWith(CheckNameResult.IN_FLIGHT));
//
////        Observable<Result> results=Observable.merge(
////
////        );
////        Observable<SubmitUIModel> uiModels=
////        ObservableTransformer<SubmitEvent, SubmitUIModel> submitUI=
////                events->events.publish(new Function<Observable<SubmitEvent>, ObservableSource<SubmitUIModel>>() {
////                    @Override
////                    public ObservableSource<SubmitUIModel> apply(Observable<SubmitEvent> submitEventObservable) throws Exception {
////                        return Observable.merge(
////                                submitEventObservable.ofType(SubmitEvent.class),
////                                submitEventObservable.ofType(CheckNameEvent.class)
////                        );
////                    }
////                });
//
//        return uiModels.subscribe(
//                model -> {
//                    submitView.setEnabled(!model.inProgress);
//                    progressView.setVisibility(model.inProgress ? View.VISIBLE : View.GONE);
//                    if (!model.inProgress) {
//                        if (model.success) {
//                            finish();
//                        } else {
//                            if (!TextUtils.isEmpty(model.errorMessage)) {
//                                Toast.makeText(this, "Fail to set name: " + model.errorMessage,
//                                        Toast.LENGTH_SHORT).show();
//                            } else {
//                                MyLogger.d("initial state");
//                            }
//                        }
//                    }
//                }, t -> {
//                    throw new OnErrorNotImplementedException(t);
//                }
//        );
//    }

    private void method04() {
//        Subject<SubmitUIEvent>
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDisposables != null)
            mDisposables.dispose();
    }
}
