package com.hon.librarytest02.audiomessage;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.text.TextUtils;

import java.io.File;
import java.io.IOException;

/**
 * Created by Frank_Hon on 3/7/2019.
 * E-mail: v-shhong@microsoft.com
 */
@SuppressWarnings("all")
public class AudioMsgManager {

    private MediaRecorder mMediaRecorder;

    private MediaPlayerManager mMediaPlayerManager;

    private String mAudioFilePath="";

    public AudioMsgManager() {
        mMediaRecorder=new MediaRecorder();
    }

    public void prepareRecord(String path) throws IOException {
        File dir = new File(path);
        if (!dir.exists()) {
            if(!dir.mkdirs()){
                throw new RuntimeException("fail to create folder");
            }
        }
        String fileName = System.currentTimeMillis()+".amr";
        File file = new File(dir, fileName);

        mAudioFilePath=file.getAbsolutePath();

        mMediaRecorder.setOutputFile(mAudioFilePath);
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mMediaRecorder.prepare();
        mMediaRecorder.start();

    }

    public int getVoiceLevel() {
        return mMediaRecorder.getMaxAmplitude() / 32768 + 1;
    }

    /**
     *
     * @return audio file duration( unit second)
     * @throws IOException
     */
    public int getDuration() throws IOException {

        if(mMediaPlayerManager==null){
            mMediaPlayerManager=MediaPlayerManager.getInstance();
        }

        return mMediaPlayerManager.getDuration(mAudioFilePath);
    }

    public String getCurrentAudioFilePath(){
        return mAudioFilePath;
    }

    public void play(MediaPlayer.OnCompletionListener onCompletionListener) throws IOException {

        if(mMediaPlayerManager==null){
            mMediaPlayerManager=MediaPlayerManager.getInstance();
        }

        mMediaPlayerManager.play(mAudioFilePath, onCompletionListener);
    }

    public void reset() {
        if (mMediaRecorder != null) {
            mMediaRecorder.reset();
        }
    }

    public void cancel() {
        reset();
        if (!TextUtils.isEmpty(mAudioFilePath)) {
            File file = new File(mAudioFilePath);
            file.delete();
        }
    }

    /**
     *  release resources when view destroyed
     */
    public void release(){
        if(mMediaRecorder!=null){
            mMediaRecorder.release();
        }

        if(mMediaPlayerManager!=null){
            mMediaPlayerManager.release();
        }
    }
}
