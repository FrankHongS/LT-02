package com.hon.librarytest02.customview.drag;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hon.librarytest02.R;

/**
 * Created by Frank Hon on 2023/7/16 12:03 上午.
 * E-mail: frank_hon@foxmail.com
 */
public class DraggableView extends FrameLayout {

    private final GestureDetector gestureDetector;

    private float touchX;
    private float touchY;

    private boolean isDragging = false;

    private DragParent<String> dragParent;

    public DraggableView(@NonNull Context context) {
        this(context, null);
    }

    public DraggableView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DraggableView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View.inflate(context, R.layout.layout_drag_view, this);
        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                starDrag(e);
            }
        });
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        dragParent = findDragParent();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isDragging) {
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_MOVE:
                    float deltaX = event.getX() - touchX;
                    float deltaY = event.getY() - touchY;
                    if (dragParent != null) {
                        dragParent.onDrag(deltaX, deltaY);
                    }
                    touchX = event.getX();
                    touchY = event.getY();
                    break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    isDragging = false;
                    boolean isDrop = false;
                    if (dragParent != null) {
                        isDrop = dragParent.onDragEnd(new DragInfo<>("12", 0));
                    }
                    setVisibility(isDrop ? GONE : VISIBLE);
                    break;
                default:
                    break;
            }
            return true;
        }
        return gestureDetector.onTouchEvent(event);
    }

    private void starDrag(MotionEvent e) {
        isDragging = true;
        touchX = e.getX();
        touchY = e.getY();
        if (dragParent != null) {
            dragParent.onDragStart(this);
        }
        setVisibility(INVISIBLE);
    }

    private DragParent<String> findDragParent() {
        ViewParent parent = getParent();
        while (parent != null) {
            if (parent instanceof DragParent) {
                return (DragParent<String>) parent;
            } else {
                parent = parent.getParent();
            }
        }
        return null;
    }
}
