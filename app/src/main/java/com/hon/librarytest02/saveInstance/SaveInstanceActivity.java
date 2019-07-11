package com.hon.librarytest02.saveInstance;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.hon.librarytest02.R;
import com.jakewharton.rxbinding3.view.RxView;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.OnErrorNotImplementedException;
import io.reactivex.functions.Function;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_instance);
        ButterKnife.bind(this);

        mDisposables = new CompositeDisposable();

//        Disposable disposable=method01();
        Disposable disposable = method02();

        mDisposables.add(disposable);
    }

    private Disposable method01() {
        //TODO bug 当横竖屏切换时，请求会被dispose
        return RxView.clicks(submitView)
                .doOnNext(ignored -> {
                    submitView.setEnabled(false);
                    progressView.setVisibility(View.VISIBLE);
                })
                //TODO 网络请求不应该直接依赖UI
                .flatMap(ignored -> MaskService.getInstance().setName(nameView.getText().toString()))
                .observeOn(AndroidSchedulers.mainThread())
                //TODO bug 在stream中抛出错误时，progressBar不会消失，只有成功时才会消失
                .doOnNext(ignored -> progressView.setVisibility(View.GONE))
                .subscribe(
                        s -> finish(),
                        //TODO bug 当发生错误，再次点击button不会重新请求
                        //error is the terminal event.
                        t -> {
                            submitView.setEnabled(true);
                            Toast.makeText(this, "Fail to set name: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                );

    }

    private Disposable method02() {

        ObservableTransformer<SubmitEvent, SubmitUIModel> submit =
                events -> events
                        .flatMap(event -> MaskService.getInstance()
                                .setName(event.name)
                                .map(response -> SubmitUIModel.success())
                                .onErrorReturn(t -> SubmitUIModel.failure(t.getMessage()))
                                .observeOn(AndroidSchedulers.mainThread())
                                .startWith(SubmitUIModel.inProgress()
                                )
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

    private void method03(){
//        ObservableTransformer<SubmitEvent, SubmitUIModel> submitUI=
//                events->events.publish(new Function<Observable<SubmitEvent>, ObservableSource<SubmitUIModel>>() {
//                    @Override
//                    public ObservableSource<SubmitUIModel> apply(Observable<SubmitEvent> submitEventObservable) throws Exception {
//                        return Observable.merge(
//                                submitEventObservable.ofType(),
//                                submitEventObservable.ofType()
//                        );
//                    }
//                });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDisposables.dispose();
    }
}
