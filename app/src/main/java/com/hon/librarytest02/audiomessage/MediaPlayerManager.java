package com.hon.librarytest02.audiomessage;

import android.media.MediaPlayer;

import java.io.IOException;

/**
 * Created by Frank_Hon on 3/11/2019.
 * E-mail: v-shhong@microsoft.com
 */
public class MediaPlayerManager {

    private static MediaPlayerManager sInstance;

    private MediaPlayer mMediaPlayer;

    private MediaPlayerManager(){
        mMediaPlayer=new MediaPlayer();
        mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                mp.reset();
                return false;
            }
        });
    }

    public static MediaPlayerManager getInstance(){
        if(sInstance==null){
            synchronized (MediaPlayerManager.class){
                if(sInstance==null){
                    sInstance=new MediaPlayerManager();
                }
            }
        }

        return sInstance;
    }

    public int getDuration(String audioFilePath) throws IOException {

        int duration=0;

        mMediaPlayer.setDataSource(audioFilePath);
        mMediaPlayer.prepare();

        duration=mMediaPlayer.getDuration()/1000;

        mMediaPlayer.reset();

        return duration;
    }

    public void play(String audioFilePath, MediaPlayer.OnCompletionListener onCompletionListener) throws IOException {

            mMediaPlayer.reset();

            mMediaPlayer.setAudioStreamType(android.media.AudioManager.STREAM_MUSIC);
            mMediaPlayer.setOnCompletionListener(onCompletionListener);
            mMediaPlayer.setDataSource(audioFilePath);
            mMediaPlayer.prepare();
            mMediaPlayer.start();
    }

    public void release(){
        mMediaPlayer.release();
    }
}
