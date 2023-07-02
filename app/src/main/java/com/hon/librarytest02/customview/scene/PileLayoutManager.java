package com.hon.librarytest02.customview.scene;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;

import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 类似纸牌的堆叠布局
 * 1，要求所有元素尺寸一致
 * 2，目前只支持水平布局
 * 3，中间元素放大，为选中状态
 */
public class PileLayoutManager extends RecyclerView.LayoutManager {

    private static final String TAG = "PileLayoutManager";

    private static final int MAX_VISIBLE_ITEM_COUNT = 5;
    private static final int DURATION = 300;

    /**
     * 元素间隔
     */
    private final int space;
    /**
     * 元素宽度
     */
    private int itemWidth;
    /**
     * itemWidth + space
     */
    private int unitWidth;
    /**
     * 累计偏移量
     */
    private int totalOffset;
    /**
     * 选中放大元素
     */
    private int selectedPosition;
    /**
     * 非选中元素缩放比例
     */
    private final float secondaryScale;

    private ObjectAnimator animator;
    private int animateValue;
    private RecyclerView.Recycler recycler;
    private int lastAnimateValue;
    private int minVelocityX;
    private final VelocityTracker velocityTracker = VelocityTracker.obtain();
    private int pointerId;
    private RecyclerView recyclerView;
    private Method sSetScrollState;

    private OnSelectListener onSelectListener;
    private final View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            velocityTracker.addMovement(event);
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (animator != null && animator.isRunning()) {
                    animator.cancel();
                }
                pointerId = event.getPointerId(0);
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                if (v.isPressed()) {
                    v.performClick();
                }
                velocityTracker.computeCurrentVelocity(1000, 14000);
                float xVelocity = velocityTracker.getXVelocity(pointerId);
                int offsetPerUnit = Math.abs(totalOffset % unitWidth);
                int scrollX;
                if (Math.abs(xVelocity) < minVelocityX && offsetPerUnit != 0) {
                    if (totalOffset >= 0) {
                        if (offsetPerUnit >= unitWidth / 2) {
                            scrollX = unitWidth - offsetPerUnit;
                        } else {
                            scrollX = -offsetPerUnit;
                        }
                    } else {
                        if (offsetPerUnit >= unitWidth / 2) {
                            scrollX = offsetPerUnit - unitWidth;
                        } else {
                            scrollX = offsetPerUnit;
                        }
                    }
                    int duration = (int) (Math.abs(scrollX * 1f / unitWidth) * DURATION);
                    brewAndStartAnimator(duration, scrollX);
                }
            }
            return false;
        }

    };

    private final RecyclerView.OnFlingListener mOnFlingListener = new RecyclerView.OnFlingListener() {
        @Override
        public boolean onFling(int velocityX, int velocityY) {
            int offsetPerUnit;
            if (totalOffset >= 0) {
                offsetPerUnit = totalOffset % unitWidth;
            } else {
                offsetPerUnit = totalOffset % unitWidth + unitWidth;
            }
            int scrollX;
            if (velocityX > 0) {
                // 向左加速
                scrollX = unitWidth - offsetPerUnit;
            } else {
                // 向右加速
                scrollX = -offsetPerUnit;
            }
            int duration = computeSettleDuration(Math.abs(scrollX), Math.abs(velocityX));
            brewAndStartAnimator(duration, scrollX);
            setScrollStateIdle();
            return true;
        }
    };

    public PileLayoutManager(int space, float secondaryScale) {
        this.space = space;
        this.secondaryScale = secondaryScale;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onAttachedToWindow(RecyclerView view) {
        super.onAttachedToWindow(view);
        recyclerView = view;
        //check when raise finger and settle to the appropriate item
        view.setOnTouchListener(mTouchListener);
        view.setOnFlingListener(mOnFlingListener);
    }

    @Override
    public boolean isAutoMeasureEnabled() {
        return true;
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        // 根据itemWidth的值来判断是否初始化
        if (getItemCount() <= 0 || itemWidth != 0) {
            return;
        }
        this.recycler = recycler;
        detachAndScrapAttachedViews(recycler);
        View anchorView = recycler.getViewForPosition(0);
        measureChildWithMargins(anchorView, 0, 0);
        itemWidth = getDecoratedMeasuredWidth(anchorView);
        unitWidth = itemWidth + space;
        minVelocityX = ViewConfiguration.get(anchorView.getContext()).getScaledMinimumFlingVelocity();
        fill(recycler);
    }

    @Override
    public void onAdapterChanged(RecyclerView.Adapter oldAdapter, RecyclerView.Adapter newAdapter) {
        totalOffset = 0;
    }


    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        return fillFromLeft(dx, recycler);
    }

    @Override
    public boolean canScrollHorizontally() {
        return true;
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT,
                RecyclerView.LayoutParams.WRAP_CONTENT);
    }

    private void fill(RecyclerView.Recycler recycler) {
        int itemCount = getItemCount();
        int end;
        int firstVisiblePosition, lastVisiblePosition;
        if (isLessThanHalf()) {
            end = itemCount - 1;
            firstVisiblePosition = 0;
            lastVisiblePosition = itemCount - 1;
            selectedPosition = (firstVisiblePosition + lastVisiblePosition + 1) / 2;
        } else {
            end = Math.min(MAX_VISIBLE_ITEM_COUNT, itemCount - 1);
            firstVisiblePosition = 0;
            lastVisiblePosition = MAX_VISIBLE_ITEM_COUNT - 1;
            selectedPosition = (firstVisiblePosition + lastVisiblePosition) / 2;
        }

        if (onSelectListener != null) {
            onSelectListener.onSelect(getSelectedPosition());
        }

        //layout view
        for (int i = 0; i <= end / 2; i++) {
            layoutChildView(i, firstVisiblePosition, selectedPosition, recycler);
            layoutChildView(end - i, firstVisiblePosition, selectedPosition, recycler);
        }
    }

    private int fillFromLeft(int dx, RecyclerView.Recycler recycler) {
        int itemCount = getItemCount();
        int firstVisiblePosition;
        int start, end;
        if (isLessThanHalf()) {
            if (totalOffset + dx < -itemCount / 2 * unitWidth || totalOffset + dx > 0) {
                return 0;
            }
            totalOffset += dx;
            firstVisiblePosition = 0;
            selectedPosition = itemCount - 1 + totalOffset / unitWidth;
            start = 0;
            end = itemCount - 1;

        } else {
            if (totalOffset + dx < -MAX_VISIBLE_ITEM_COUNT / 2 * unitWidth ||
                    totalOffset + dx > (getItemCount() - 1 - MAX_VISIBLE_ITEM_COUNT / 2) * unitWidth) {
                return 0;
            }

            totalOffset += dx;
            if (totalOffset >= 0) {
                firstVisiblePosition = totalOffset / unitWidth;
            } else {
                firstVisiblePosition = totalOffset / unitWidth - 1;
            }
            int lastVisiblePosition = firstVisiblePosition + MAX_VISIBLE_ITEM_COUNT - 1;
            selectedPosition = (firstVisiblePosition + lastVisiblePosition) / 2;
            start = Math.max(firstVisiblePosition - 1, 0);
            end = Math.min(lastVisiblePosition + 1, getItemCount() - 1);
        }

        detachAndScrapAttachedViews(recycler);
        recycleViews(dx, recycler);

        //layout view
        for (int i = start; i <= (start + end) / 2; i++) {
            layoutChildView(i, firstVisiblePosition, selectedPosition, recycler);
            layoutChildView(start + end - i, firstVisiblePosition, selectedPosition, recycler);
        }

        return dx;
    }

    private void layoutChildView(int position, int firstVisiblePosition,
                                 int selectedPosition, RecyclerView.Recycler recycler) {

        View view = recycler.getViewForPosition(position);
        float scale = scale(position, selectedPosition);

        addView(view);
        measureChildWithMargins(view, 0, 0);
        int left = getLeft(position, firstVisiblePosition);
        int top = 0;
        int right = left + getDecoratedMeasuredWidth(view);
        int bottom = top + getDecoratedMeasuredHeight(view);
        layoutDecoratedWithMargins(view, left, top, right, bottom);
        view.setScaleY(scale);
        view.setScaleX(scale);
    }

    private void recycleViews(int dx, RecyclerView.Recycler recycler) {
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (canRecycleHorizontally(child, dx)) {
                removeAndRecycleView(child, recycler);
            }
        }
    }

    private int computeSettleDuration(int distance, float xvel) {
        float sWeight = 0.5f * distance / unitWidth;
        float velWeight = xvel > 0 ? 0.5f * minVelocityX / xvel : 0;

        return (int) ((sWeight + velWeight) * DURATION);
    }

    private void brewAndStartAnimator(int duration, int finalX) {
        animator = ObjectAnimator.ofInt(PileLayoutManager.this, "animateValue", 0, finalX);
        animator.setDuration(duration);
        animator.start();
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (onSelectListener != null) {
                    onSelectListener.onSelect(getSelectedPosition());
                }
                lastAnimateValue = 0;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                lastAnimateValue = 0;
            }
        });
    }

    @SuppressWarnings("unused")
    public void setAnimateValue(int animateValue) {
        this.animateValue = animateValue;
        int dy = this.animateValue - lastAnimateValue;
        fillFromLeft(dy, recycler);
        lastAnimateValue = animateValue;
    }

    @SuppressWarnings("unused")
    public int getAnimateValue() {
        return animateValue;
    }

    private float scale(int position, int selectedPosition) {
        float scale;
        float offsetRatio = Math.abs((totalOffset % unitWidth) * 1f / unitWidth);
        if (isLessThanHalf()) {
            if (position == selectedPosition) {
                scale = 1 - (1 - secondaryScale) * offsetRatio;
            } else if (position == selectedPosition - 1) {
                scale = secondaryScale + (1 - secondaryScale) * offsetRatio;
            } else {
                scale = secondaryScale;
            }
        } else {
            if (totalOffset < 0) {
                offsetRatio = 1 - offsetRatio;
            }
            if (position == selectedPosition) {
                scale = 1 - (1 - secondaryScale) * offsetRatio;
            } else if (position == selectedPosition + 1) {
                scale = secondaryScale + (1 - secondaryScale) * offsetRatio;
            } else {
                scale = secondaryScale;
            }
        }

        return scale;
    }

    private int getLeft(int position, int firstVisiblePosition) {
        int itemCount = getItemCount();
        int left;
        if (isLessThanHalf()) {
            left = unitWidth * (position + MAX_VISIBLE_ITEM_COUNT / 2 - itemCount + 1) - totalOffset;
        } else {
            int offsetPerUnit = totalOffset % unitWidth;
            if (position <= firstVisiblePosition) {
                left = 0;
            } else if (position >= firstVisiblePosition + MAX_VISIBLE_ITEM_COUNT) {
                left = unitWidth * (MAX_VISIBLE_ITEM_COUNT - 1);
            } else {
                if (totalOffset >= 0) {
                    left = unitWidth * (position - firstVisiblePosition) - offsetPerUnit;
                } else {
                    left = unitWidth * (position - firstVisiblePosition - 1) - offsetPerUnit;
                }
            }
        }

        return left;
    }

    private boolean canRecycleHorizontally(View view, int dx) {
        return view != null && (view.getLeft() - dx < 0 || view.getRight() - dx > getRealWidth());
    }

    /**
     * we need to set scrollstate to {@link RecyclerView#SCROLL_STATE_IDLE} idle
     * stop RV from intercepting the touch event which block the item click
     */
    private void setScrollStateIdle() {
        try {
            if (sSetScrollState == null) {
                sSetScrollState = RecyclerView.class.getDeclaredMethod("setScrollState", int.class);
            }
            sSetScrollState.setAccessible(true);
            sSetScrollState.invoke(recyclerView, RecyclerView.SCROLL_STATE_IDLE);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private boolean isLessThanHalf() {
        int itemCount = getItemCount();
        return itemCount - 1 < MAX_VISIBLE_ITEM_COUNT / 2;
    }

    private int getRealWidth() {
        return MAX_VISIBLE_ITEM_COUNT * unitWidth;
    }

    @Override
    public void scrollToPosition(int position) {
        if (position > getItemCount() - 1 || position < 0) {
            return;
        }
        int distance = (position - getSelectedPosition()) * unitWidth;
        int dur = computeSettleDuration(Math.abs(distance), 0);
        brewAndStartAnimator(dur, distance);
    }

    public int getSelectedPosition() {
        if (isLessThanHalf()) {
            return selectedPosition;
        }
        /*
         * totalOffset / mUnit, 该值通常情况下为第一个元素索引，但是当totalOffset为0时，
         *  以及当totalOffset为unit时，都向右滑动，该值的变化不一致。因此此处做了特殊处理
         */
        if (selectedPosition <= 0) {
            return selectedPosition + 1;
        }
        return selectedPosition;
    }

    public void setOnSelectListener(OnSelectListener onSelectListener) {
        this.onSelectListener = onSelectListener;
    }

    public interface OnSelectListener {
        void onSelect(int position);
    }
}
