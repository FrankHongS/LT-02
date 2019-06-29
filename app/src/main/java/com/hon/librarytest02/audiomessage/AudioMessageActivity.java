package com.hon.librarytest02.audiomessage;

import android.Manifest;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hon.librarytest02.BaseActivity;
import com.hon.librarytest02.R;
import com.tbruyelle.rxpermissions2.RxPermissions;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by Frank_Hon on 3/7/2019.
 * E-mail: v-shhong@microsoft.com
 */
public class AudioMessageActivity extends BaseActivity {

    private AudioMessageButton mAudioMessageButton;

    private ImageView mAudioAnimImage;
    private TextView mHintText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_audio_message);

        final RxPermissions rxPermissions=new RxPermissions(this);

        rxPermissions.request(Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO)
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Boolean aBoolean) {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

        initView();
    }

    @Override
    protected void initView() {

        mAudioAnimImage=findViewById(R.id.iv_anim_audio);

        final AnimationDrawable animationDrawable= (AnimationDrawable) mAudioAnimImage.getDrawable();

        mAudioMessageButton=findViewById(R.id.amb_audio_message);
        mAudioMessageButton.setOnRecordingListener(new AudioMessageButton.OnRecordingListener() {
            @Override
            public void onRecord() {
                mHintText.setVisibility(View.VISIBLE);
                mAudioAnimImage.setVisibility(View.VISIBLE);
                animationDrawable.start();
            }

            @Override
            public void onStop() {
                animationDrawable.stop();
                mHintText.setVisibility(View.GONE);
                mAudioAnimImage.setVisibility(View.GONE);
            }

            @Override
            public void onError(Exception e) {

            }

            @Override
            public void onFinish(int duration, String audioFilePath) {

            }
        });

        mHintText=findViewById(R.id.tv_hint);
    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.btn_play_audio:
                mAudioMessageButton.playAudio(mp -> {

                });
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAudioMessageButton.releaseResource();
    }
}
