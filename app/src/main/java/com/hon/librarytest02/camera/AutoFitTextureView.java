package com.hon.librarytest02.camera;

import android.content.Context;
import android.util.AttributeSet;
import android.view.TextureView;

/**
 * Created by Frank Hon on 2020/7/21 12:08 PM.
 * E-mail: frank_hon@foxmail.com
 */
public class AutoFitTextureView extends TextureView {

    private float aspectRatio = 0f;

    public AutoFitTextureView(Context context) {
        this(context, null);
    }

    public AutoFitTextureView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AutoFitTextureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setAspectRatio(int width,int height){
        if (width<=0||height<=0){
            return;
        }
        aspectRatio=width*1f/height;

    }
}
