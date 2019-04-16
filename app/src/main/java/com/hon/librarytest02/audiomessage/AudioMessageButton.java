package com.hon.librarytest02.audiomessage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Toast;

import com.hon.librarytest02.R;
import com.hon.librarytest02.util.Util;
import com.hon.mylogger.MyLogger;

import java.io.IOException;

import androidx.appcompat.widget.AppCompatButton;

import static android.media.AudioManager.AUDIOFOCUS_GAIN_TRANSIENT;
import static android.media.AudioManager.STREAM_MUSIC;

/**
 * Created by Frank_Hon on 3/7/2019.
 * E-mail: v-shhong@microsoft.com
 */
@SuppressWarnings("all")
public class AudioMessageButton extends AppCompatButton implements AudioManager.OnAudioFocusChangeListener {

    private static final int STATE_NORMAL = 1;
    private static final int STATE_RECORDING = 2;
    private static final int STATE_WANT_TO_CANCEL = 3;
    private static final int STATE_NOT_WANT_TO_CANCEL = 4;
    private static final int STATE_CANCELED = 5;
    private static final int STATE_FINISHED=6;

    private int mState = STATE_NORMAL;

    private static final int DISTANCE_Y_CANCEL = 125;

    private AudioManager mAudioManager;
    private AudioMsgManager mAudioMsgManager;

    private long mStartTime = 0L;
    private long mEndTime = 0L;

    private OnRecordingListener mOnRecordingListener;

    public AudioMessageButton(Context context) {
        this(context, null);
    }

    public AudioMessageButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AudioMessageButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        mAudioMsgManager = new AudioMsgManager();

        setOnLongClickListener(v -> {

            try {
                switchState(STATE_RECORDING);

                mAudioManager.requestAudioFocus(
                        this,
                        STREAM_MUSIC,
                        AUDIOFOCUS_GAIN_TRANSIENT
                );
                mAudioMsgManager.prepareRecord(context.getFilesDir() + "/audio");

                mStartTime = System.currentTimeMillis();

            } catch (Exception e) {
                Toast.makeText(context, "fail to prepare resource", Toast.LENGTH_SHORT).show();
            }

            return true;
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                break;
            case MotionEvent.ACTION_MOVE:
                if (Math.abs(event.getY()) >= DISTANCE_Y_CANCEL) {
                    if (mState == STATE_RECORDING) {
                        switchState(STATE_WANT_TO_CANCEL);
                    }
                } else {
                    if (mState == STATE_WANT_TO_CANCEL) {
                        switchState(STATE_NOT_WANT_TO_CANCEL);
                    }
                }

                break;
            case MotionEvent.ACTION_UP:
                if(mState==STATE_WANT_TO_CANCEL){
                    switchState(STATE_CANCELED);
                }else {
                    checkIfTimeTooShort();
                }
                switchState(STATE_NORMAL);
                mAudioManager.abandonAudioFocus(this);
                break;
            default:
                break;
        }

        return super.onTouchEvent(event);
    }

    @Override
    public void onAudioFocusChange(int focusChange) {

    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        releaseResource();
    }

    private void switchState(int state) {
        mState = state;
        switch (state) {
            case STATE_NORMAL:
                setText(Util.getResourceString(R.string.chat_hold_to_talk));
                mAudioMsgManager.reset();
                if (mOnRecordingListener != null) {
                    mOnRecordingListener.onStop();
                }
                break;
            case STATE_WANT_TO_CANCEL:
                setText(Util.getResourceString(R.string.chat_release_to_cancel));
                break;
            case STATE_RECORDING:
                setText(Util.getResourceString(R.string.chat_release_to_send));
                if (mOnRecordingListener != null) {
                    mOnRecordingListener.onRecord();
                }
                break;
            case STATE_NOT_WANT_TO_CANCEL:
                setText(Util.getResourceString(R.string.chat_release_to_send));
                break;
            case STATE_CANCELED:
                mAudioMsgManager.cancel();
                break;
            case STATE_FINISHED:
                if (mOnRecordingListener != null) {
                    try {
                        mOnRecordingListener.onFinish(mAudioMsgManager.getDuration(),mAudioMsgManager.getCurrentAudioFilePath());
                    } catch (IOException e) {
                        e.printStackTrace();
                        mOnRecordingListener.onError(e);
                    }
                }
                break;
            default:
                break;
        }
    }

    private void checkIfTimeTooShort() {
        if (mStartTime != 0L) {
            mEndTime = System.currentTimeMillis();
            long deltaTime = (mEndTime - mStartTime) / 1000;
            if (deltaTime >= 1) {
                switchState(STATE_FINISHED);
            } else {
                switchState(STATE_CANCELED);
                Toast.makeText(getContext(), "time too short", Toast.LENGTH_SHORT).show();
            }

            resetTime();
        }

    }


    private void resetTime() {
        mStartTime = 0L;
        mEndTime = 0L;
    }

    public int getAudioLength() {
        try {
            return mAudioMsgManager.getDuration();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public void playAudio(MediaPlayer.OnCompletionListener listener) {
        try {
            mAudioMsgManager.play(listener);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void releaseResource() {
        mAudioMsgManager.release();
    }

    public void setOnRecordingListener(OnRecordingListener listener) {
        this.mOnRecordingListener = listener;
    }

    public interface OnRecordingListener {

        void onRecord();

        void onStop();

        void onFinish(int duration,String audioFilePath);

        void onError(Exception e);
    }

}
