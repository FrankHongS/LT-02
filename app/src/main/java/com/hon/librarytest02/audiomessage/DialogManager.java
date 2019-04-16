package com.hon.librarytest02.audiomessage;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.hon.librarytest02.R;

import androidx.appcompat.app.AlertDialog;

/**
 * Created by Frank_Hon on 3/7/2019.
 * E-mail: v-shhong@microsoft.com
 */
public class DialogManager {
    private AlertDialog.Builder builder;
    private ImageView mIcon;
    //    private ImageView mVoice;
    private TextView mLable;

    private Context context;

    private AlertDialog dialog;//用于取消AlertDialog.Builder

    private AnimationDrawable mAnimationDrawable;

    /**
     * 构造方法 传入上下文
     */
    public DialogManager(Context context) {
        this.context = context;
    }

    // 显示录音的对话框
    public void showRecordingDialog() {
        builder = new AlertDialog.Builder(context, R.style.Theme_AudioRecord_Dialog);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.xichat_audio_record_dialog, null);

        mIcon = (ImageView) view.findViewById(R.id.xichat_record_dialog_icon);
        mLable = (TextView) view.findViewById(R.id.xichat_record_dialog_text);
//        GlideUtil.loadSquarePicture(R.drawable.gif_record, mIcon);
        builder.setView(view);
        dialog=builder.create();
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = 600;
        lp.height = 600;
        dialogWindow.setAttributes(lp);
        dialog.show();
//        builder.show();
        dialog.setCanceledOnTouchOutside(false);//设置点击外部不消失

        mIcon.setImageResource(R.drawable.anim_chat_record_voice);
        mAnimationDrawable= (AnimationDrawable) mIcon.getDrawable();
    }

    public void recording() {
        if (dialog != null && dialog.isShowing()) { //显示状态
            mIcon.setVisibility(View.VISIBLE);
            mLable.setVisibility(View.VISIBLE);
//            GlideUtil.loadSquarePicture(R.drawable.gif_record, mIcon);
//            mIcon.setImageResource(R.mipmap.setting_feedback_chat_voice_record_0);
            mAnimationDrawable.start();
            mLable.setText("手指上滑，取消发送");
        }
    }

    // 显示想取消的对话框
    public void wantToCancel() {
        if (dialog != null && dialog.isShowing()) { //显示状态
            mIcon.setVisibility(View.VISIBLE);
            mLable.setVisibility(View.VISIBLE);
//            mIcon.setImageResource(R.mipmap.ic_release_to_cancel);
            mAnimationDrawable.stop();
            mLable.setText("松开手指，取消发送");
        }
    }

    public void updateTime(int time) {
        if (dialog != null && dialog.isShowing()) { //显示状态
            mIcon.setVisibility(View.VISIBLE);
            mLable.setVisibility(View.VISIBLE);
            mLable.setText(time + "s");
        }
    }

    // 显示时间过短的对话框
    public void tooShort() {
        if (dialog != null && dialog.isShowing()) { //显示状态
            mIcon.setVisibility(View.VISIBLE);
            mLable.setVisibility(View.VISIBLE);
            mLable.setText("录音时间过短");
        }
    }

    // 显示取消的对话框
    public void dimissDialog() {
        if (dialog != null && dialog.isShowing()) { //显示状态
            dialog.dismiss();
            dialog = null;
        }
    }

    // 显示更新音量级别的对话框
    public void updateVoiceLevel(int level) {
        if (dialog != null && dialog.isShowing()) { //显示状态
            mIcon.setVisibility(View.VISIBLE);
            mLable.setVisibility(View.VISIBLE);
//          在这里没有做操作
//可以再这里根据音量大小，设置图片，实现效果；
        }
    }
}

