package com.hon.librarytest02.snackbar;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hon.librarytest02.R;
import com.hon.librarytest02.util.Util;

/**
 * Created by Frank Hon on 2020/12/17 5:31 PM.
 * E-mail: frank_hon@foxmail.com
 */
public class CustomSnackBarView extends FrameLayout {

    private TextView contentText;
    private Button actionButton;

    public CustomSnackBarView(@NonNull Context context) {
        this(context, null);
    }

    public CustomSnackBarView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomSnackBarView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View view = LayoutInflater.from(context).inflate(R.layout.layout_snackbar, this, true);
        setBackground(getResources().getDrawable(R.drawable.snackbar_background));

        contentText = view.findViewById(R.id.tv_content);
        actionButton = view.findViewById(R.id.btn_action);

        contentText.setText("Hello world !");
        actionButton.setText("Action");

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(Util.dp2px(60), MeasureSpec.EXACTLY));
    }

    public void animateContentIn(int duration) {
        setAlpha(0f);
        animate().alpha(1f).setDuration(500).start();

        postDelayed(() -> animateContentOut(500), duration);

//        contentText.setAlpha(0f);
//        contentText.animate().alpha(1f).setDuration(duration).setStartDelay(0).start();

//        if (actionButton.getVisibility() == VISIBLE) {
//            actionButton.setAlpha(0f);
//            actionButton.animate().alpha(1f).setDuration(duration).setStartDelay(0).start();
//        }
    }

    public void animateContentOut(int duration) {
        setAlpha(1f);
        animate().alpha(0f).setDuration(duration).setStartDelay(0).start();

//        contentText.setAlpha(1f);
//        contentText.animate().alpha(0f).setDuration(duration).setStartDelay(0).start();
//
//        if (actionButton.getVisibility() == VISIBLE) {
//            actionButton.setAlpha(1f);
//            actionButton.animate().alpha(0f).setDuration(duration).setStartDelay(0).start();
//        }
    }

}
