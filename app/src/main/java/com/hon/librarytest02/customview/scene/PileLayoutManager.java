package com.hon.librarytest02.customview.scene;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Frank Hon on 2023/6/29 10:45 下午.
 * E-mail: frank_hon@foxmail.com
 */
class PileLayoutManager extends RecyclerView.LayoutManager {

    private int realWidth;

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.MATCH_PARENT,
                RecyclerView.LayoutParams.WRAP_CONTENT
        );
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {

        detachAndScrapAttachedViews(recycler);

        int itemCount = getItemCount();
        if (itemCount == 0) {
            return;
        }
        int count = Math.min(itemCount, 5);
        int mid = count / 2;
        int offsetX = 0;
        float scale;
        for (int i = 0; i < count; i++) {
            int delta = Math.abs(mid - i);
            if (delta == 0) {
                scale = 1f;
            } else {
                scale = 0.8f;
            }
            View view = recycler.getViewForPosition(i);
            addView(view);
            view.setScaleX(scale);
            view.setScaleY(scale);
            measureChildWithMargins(view, 0, 0);
            int viewWidth = getDecoratedMeasuredWidth(view);
            int viewHeight = getDecoratedMeasuredHeight(view);
            layoutDecoratedWithMargins(view, offsetX, 0, offsetX + viewWidth, viewHeight);
            if (i == mid - 1) {
                offsetX += viewWidth;
            } else {
                offsetX += viewWidth * (1 + scale) / 2;
            }
        }
        realWidth = offsetX;
    }

    @Override
    public boolean canScrollHorizontally() {
        return true;
    }

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        return 0;
    }


}
