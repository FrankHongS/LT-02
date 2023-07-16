package com.hon.librarytest02.customview.drag;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hon.librarytest02.util.Util;

/**
 * Created by Frank Hon on 2023/7/15 10:09 下午.
 * E-mail: frank_hon@foxmail.com
 */
public class DragLayer extends FrameLayout implements DragParent<String> {

    private ImageView snapshot;
    private final Rect borderBox = new Rect();

    private OnCrossDragListener<String> onCrossDragListener;

    public DragLayer(@NonNull Context context) {
        this(context, null);
    }

    public DragLayer(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragLayer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setMotionEventSplittingEnabled(false);
    }

    @Override
    public void onDragStart(View child) {
        Bitmap bitmap = Util.viewToBitmap(child);
        snapshot = new ImageView(getContext());
        snapshot.setImageBitmap(bitmap);
        LayoutParams layoutParams = new LayoutParams(
                bitmap.getWidth(), bitmap.getHeight()
        );
        addView(snapshot, layoutParams);
        snapshot.setTranslationX(child.getLeft());
        snapshot.setTranslationY(child.getTop());
    }

    @Override
    public void onDrag(float dx, float dy) {
        if (snapshot != null) {
            snapshot.setTranslationX(snapshot.getTranslationX() + dx);
            snapshot.setTranslationY(snapshot.getTranslationY() + dy);
            if (onCrossDragListener != null) {
                borderBox.set(
                        (int) snapshot.getTranslationX(),
                        (int) snapshot.getTranslationY(),
                        (int) snapshot.getTranslationX() + snapshot.getWidth(),
                        (int) snapshot.getTranslationY() + snapshot.getHeight()
                );
                onCrossDragListener.onCrossDrag(borderBox);
            }
        }
    }

    @Override
    public boolean onDragEnd(DragInfo<String> dragSourceInfo) {
        if (snapshot != null) {
            removeView(snapshot);
            if (onCrossDragListener != null) {
                return onCrossDragListener.onCrossDragEnd(dragSourceInfo);
            }
        }
        return false;
    }

    public void setOnCrossDragListener(OnCrossDragListener<String> onCrossDragListener) {
        this.onCrossDragListener = onCrossDragListener;
    }

    public interface OnCrossDragListener<T> {
        void onCrossDrag(Rect borderBox);

        boolean onCrossDragEnd(DragInfo<T> dragSourceInfo);
    }
}
