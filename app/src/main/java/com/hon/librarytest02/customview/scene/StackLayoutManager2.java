package com.hon.librarytest02.customview.scene;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;

import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by CJJ on 2017/5/17.
 * my thought is simple：we assume the first item in the initial state is the base position ，
 * we only need to calculate the appropriate position{@link #getLeft(int index)}for the given item
 * index with the given offset{@link #mTotalOffset}.After solve this thinking confusion ,this
 * layoutManager is easy to implement
 *
 * @author CJJ
 */

class StackLayoutManager2 extends RecyclerView.LayoutManager {

    private static final String TAG = "StackLayoutManager";

    private static final int MAX_VISIBLE_ITEM_COUNT = 5;

    //the space unit for the stacked item
    private int mSpace;
    /**
     * the offset unit,deciding current position(the sum of  {@link #itemWidth} and {@link #mSpace})
     */
    private int mUnit;
    //item width
    private int itemWidth;
    //the counting variable ,record the total offset including parallex
    private int mTotalOffset;
    private int selectedPosition;
    private ObjectAnimator animator;
    private int animateValue;
    private final int duration = 300;
    private RecyclerView.Recycler recycler;
    private int lastAnimateValue;
    //the max stacked item count;
    private int maxStackCount = 4;
    //initial stacked item
    private float secondaryScale;
    private int mMinVelocityX;
    private final VelocityTracker mVelocityTracker = VelocityTracker.obtain();
    private int pointerId;
    private RecyclerView mRV;
    private Method sSetScrollState;

    private OnSelectListener onSelectListener;

    public StackLayoutManager2(Config config) {
        this.maxStackCount = config.maxStackCount;
        this.mSpace = config.space;
        this.secondaryScale = config.secondaryScale;
    }

    public void setOnSelectListener(OnSelectListener onSelectListener) {
        this.onSelectListener = onSelectListener;
    }

    @Override
    public boolean isAutoMeasureEnabled() {
        return true;
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        // 根据itemWidth的值来判断是否初始化
        if (getItemCount() <= 0 || itemWidth != 0)
            return;
        this.recycler = recycler;
        detachAndScrapAttachedViews(recycler);
        //got the mUnit basing on the first child,of course we assume that  all the item has the same size
        View anchorView = recycler.getViewForPosition(0);
        measureChildWithMargins(anchorView, 0, 0);
        itemWidth = anchorView.getMeasuredWidth();
        mUnit = itemWidth + mSpace;
        mMinVelocityX = ViewConfiguration.get(anchorView.getContext()).getScaledMinimumFlingVelocity();
        fill(recycler);

    }

    @Override
    public void onLayoutCompleted(RecyclerView.State state) {
        super.onLayoutCompleted(state);
    }

    @Override
    public void onAdapterChanged(RecyclerView.Adapter oldAdapter, RecyclerView.Adapter newAdapter) {
        mTotalOffset = 0;
    }

    private void fill(RecyclerView.Recycler recycler) {

        int end = Math.min(MAX_VISIBLE_ITEM_COUNT, getItemCount() - 1);

        int lastVisiblePosition = Math.min(MAX_VISIBLE_ITEM_COUNT - 1, getItemCount() - 1);
        selectedPosition = lastVisiblePosition / 2;

        if (onSelectListener != null) {
            onSelectListener.onSelect(getSelectedPosition());
        }

        //layout view
        for (int i = 0; i <= end / 2; i++) {
            layoutChildView(i, 0, selectedPosition, recycler);
            layoutChildView(end - i, 0, selectedPosition, recycler);
        }
    }

    private int fillFromLeft(int dx, RecyclerView.Recycler recycler) {
        if (mTotalOffset + dx < -MAX_VISIBLE_ITEM_COUNT / 2 * mUnit ||
                mTotalOffset + dx > (getItemCount() - 1 - MAX_VISIBLE_ITEM_COUNT / 2) * mUnit) {
            return 0;
        }

        detachAndScrapAttachedViews(recycler);
        recycleViews(dx, recycler);

        mTotalOffset += dx;

        int firstVisiblePosition;
        if (mTotalOffset >= 0) {
            firstVisiblePosition = mTotalOffset / mUnit;
        } else {
            firstVisiblePosition = mTotalOffset / mUnit - 1;
        }
        int lastVisiblePosition = firstVisiblePosition + MAX_VISIBLE_ITEM_COUNT - 1;
        selectedPosition = (firstVisiblePosition + lastVisiblePosition) / 2;

        int start = Math.max(firstVisiblePosition - 1, 0);
        int end = Math.min(lastVisiblePosition + 1, getItemCount() - 1);

        if (onSelectListener != null) {
            onSelectListener.onSelect(getSelectedPosition());
        }

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
        float alpha = alpha(position);

        addView(view);
        measureChildWithMargins(view, 0, 0);
        int left = getLeft(position, firstVisiblePosition);
        int top = 0;
        int right = left + view.getMeasuredWidth();
        int bottom = top + view.getMeasuredHeight();
        layoutDecoratedWithMargins(view, left, top, right, bottom);
        view.setAlpha(alpha);
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

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mVelocityTracker.addMovement(event);
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (animator != null && animator.isRunning())
                    animator.cancel();
                pointerId = event.getPointerId(0);

            }
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (v.isPressed()) v.performClick();
                mVelocityTracker.computeCurrentVelocity(1000, 14000);
                float xVelocity = mVelocityTracker.getXVelocity(pointerId);
                int offsetPerUnit = Math.abs(mTotalOffset % mUnit);
                int scrollX;
                if (Math.abs(xVelocity) < mMinVelocityX && offsetPerUnit != 0) {
                    if (mTotalOffset >= 0) {
                        if (offsetPerUnit >= mUnit / 2) {
                            scrollX = mUnit - offsetPerUnit;
                        } else {
                            scrollX = -offsetPerUnit;
                        }
                    } else {
                        if (offsetPerUnit >= mUnit / 2) {
                            scrollX = offsetPerUnit - mUnit;
                        } else {
                            scrollX = offsetPerUnit;
                        }
                    }
                    int dur = (int) (Math.abs((scrollX + 0f) / mUnit) * duration);
                    Log.i(TAG, "onTouch: ======BREW===");
                    brewAndStartAnimator(dur, scrollX);
                }
            }
            return false;
        }

    };

    private RecyclerView.OnFlingListener mOnFlingListener = new RecyclerView.OnFlingListener() {
        @Override
        public boolean onFling(int velocityX, int velocityY) {
            int offsetPerUnit;
            if (mTotalOffset >= 0) {
                offsetPerUnit = mTotalOffset % mUnit;
            } else {
                offsetPerUnit = mTotalOffset % mUnit + mUnit;
            }
            Log.d("frankhon", "onFling: velocityX = " + velocityX + ", offsetPerUnit = " + offsetPerUnit);
            int scrollX;
            if (velocityX > 0) {
                // 向左加速
                scrollX = mUnit - offsetPerUnit;
            } else {
                // 向右加速
                scrollX = -offsetPerUnit;
            }
            int dur = computeSettleDuration(Math.abs(scrollX), Math.abs(velocityX));
            brewAndStartAnimator(dur, scrollX);
            setScrollStateIdle();
            return true;
        }
    };

    private int absMax(int a, int b) {
        if (Math.abs(a) > Math.abs(b))
            return a;
        else return b;
    }

    @Override
    public void onAttachedToWindow(RecyclerView view) {
        super.onAttachedToWindow(view);
        mRV = view;
        //check when raise finger and settle to the appropriate item
        view.setOnTouchListener(mTouchListener);

        view.setOnFlingListener(mOnFlingListener);
    }

    private int computeSettleDuration(int distance, float xvel) {
        float sWeight = 0.5f * distance / mUnit;
        float velWeight = xvel > 0 ? 0.5f * mMinVelocityX / xvel : 0;

        return (int) ((sWeight + velWeight) * duration);
    }

    private void brewAndStartAnimator(int dur, int finalX) {
        animator = ObjectAnimator.ofInt(StackLayoutManager2.this, "animateValue", 0, finalX);
        animator.setDuration(dur);
        animator.start();
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                lastAnimateValue = 0;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                lastAnimateValue = 0;
            }
        });
    }

    /******************************precise math method*******************************/
    private float alpha(int position) {
        float alpha;
        int currPos = mTotalOffset / mUnit;
        float n = (mTotalOffset + .0f) / mUnit;
        if (position > currPos)
            alpha = 1.0f;
        else {
            //temporary linear map,barely ok
            alpha = 1 - (n - position) / maxStackCount;
        }
        //for precise checking,oh may be kind of dummy
        return alpha <= 0.001f ? 0 : alpha;
    }

    private float scale(int position, int centerPosition) {
        float offsetRatio = Math.abs((mTotalOffset % mUnit) * 1f / mUnit);
        if (mTotalOffset < 0) {
            offsetRatio = 1 - offsetRatio;
        }
        float scale;
        if (position == centerPosition) {
            scale = 1 - (1 - secondaryScale) * offsetRatio;
        } else if (position == centerPosition + 1) {
            scale = secondaryScale + (1 - secondaryScale) * offsetRatio;
        } else {
            scale = secondaryScale;
        }

        return scale;
    }

    /**
     * @param position the index of the item in the adapter
     * @return the accurate left position for the given item
     */
    private int getLeft(int position, int firstVisiblePosition) {

        int tail = mTotalOffset % mUnit;
        int left;

        if (mTotalOffset >= 0) {
            if (position <= firstVisiblePosition) {
                left = 0;
            } else if (position >= firstVisiblePosition + MAX_VISIBLE_ITEM_COUNT) {
                left = mUnit * 4;
            } else {
                left = mUnit * (position - firstVisiblePosition) - tail;
            }
        } else {
            if (position <= firstVisiblePosition) {
                left = 0;
            } else if (position >= firstVisiblePosition + MAX_VISIBLE_ITEM_COUNT) {
                left = mUnit * 4;
            } else {
                left = mUnit * (position - firstVisiblePosition - 1) - tail;
            }
        }
        return left;
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

    /**
     * should recycle view with the given dy or say check if the
     * view is out of the bound after the dy is applied
     *
     * @param view ..
     * @param dy   ..
     * @return ..
     */
    private boolean canRecycleHorizontally(View view, int dy) {
        return view != null && (view.getLeft() - dy < 0 || view.getRight() - dy > getWidth());
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
        return new RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT);
    }

    /**
     * we need to set scrollstate to {@link RecyclerView#SCROLL_STATE_IDLE} idle
     * stop RV from intercepting the touch event which block the item click
     */
    private void setScrollStateIdle() {
        try {
            if (sSetScrollState == null)
                sSetScrollState = RecyclerView.class.getDeclaredMethod("setScrollState", int.class);
            sSetScrollState.setAccessible(true);
            sSetScrollState.invoke(mRV, RecyclerView.SCROLL_STATE_IDLE);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void scrollToPosition(int position) {
        if (position > getItemCount() - 1 || position < 0) {
            Log.i(TAG, "position is " + position + " but itemCount is " + getItemCount());
            return;
        }
        int distance = (position - getSelectedPosition()) * mUnit;
        int dur = computeSettleDuration(Math.abs(distance), 0);
        brewAndStartAnimator(dur, distance);
    }

    public int getSelectedPosition() {
        if (selectedPosition <= 0) {
            return selectedPosition + 1;
        }
        return selectedPosition;
    }

    public interface OnSelectListener {
        void onSelect(int position);
    }

    @SuppressWarnings("unused")
    public interface CallBack {

        float scale(int totalOffset, int position);

        float alpha(int totalOffset, int position);

        float left(int totalOffset, int position);
    }
}
