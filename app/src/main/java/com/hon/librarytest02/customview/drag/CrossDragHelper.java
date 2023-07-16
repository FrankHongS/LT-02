package com.hon.librarytest02.customview.drag;

import android.graphics.Rect;
import android.util.Log;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Frank Hon on 2023/7/16 11:47 下午.
 * E-mail: frank_hon@foxmail.com
 */
public class CrossDragHelper {

    private static final String TAG = "CrossDragHelper";

    private final DragLayer dragLayer;
    private RecyclerView targetView;
    private DragAdapter<String> dragAdapter;

    private String lastTargetValue = "";
    private int lastTargetPosition = -1;

    public CrossDragHelper(DragLayer dragLayer) {
        this.dragLayer = dragLayer;
    }

    public void attachToDragTarget(RecyclerView targetView) {
        this.targetView = targetView;
        this.dragAdapter = (DragAdapter<String>) targetView.getAdapter();
        dragLayer.setOnCrossDragListener(new DragLayer.OnCrossDragListener<String>() {
            @Override
            public void onCrossDrag(Rect borderBox) {
                if (outOfBound(borderBox)) {
                    dragAdapter.remove(lastTargetPosition);
                    lastTargetPosition = -1;
                    lastTargetValue = "";
                    return;
                }
                RecyclerView.LayoutManager layoutManager = targetView.getLayoutManager();
                if (layoutManager == null) {
                    return;
                }
                int childCount = layoutManager.getChildCount();
                for (int i = 0; i < childCount; i++) {
                    View child = layoutManager.getChildAt(i);
                    if (child == null || child.getTag() == null) {
                        continue;
                    }
                    RecyclerView.ViewHolder viewHolder = targetView.getChildViewHolder(child);
                    int adapterPosition = viewHolder.getAdapterPosition();
                    DragInfo<String> targetInfo = (DragInfo<String>) child.getTag();
                    if (hitTest(child, borderBox)) {
                        Log.d(TAG, "onCrossDrag: value = " + targetInfo.data
                                + ", position = " + targetInfo.position
                                + ", lastTargetValue = " + lastTargetValue
                                + ", adapterPosition = " + adapterPosition
                                + ", lastTargetPosition = " + lastTargetPosition);
                        if (!lastTargetValue.equals(targetInfo.data) ||
                                lastTargetPosition != adapterPosition) {
                            dragAdapter.remove(lastTargetPosition);
                            lastTargetValue = targetInfo.data;
                            lastTargetPosition = adapterPosition;
                            insert(adapterPosition, null);
                        }
                        break;
                    }
                }
            }

            @Override
            public boolean onCrossDragEnd(DragInfo<String> dragSourceInfo) {
                lastTargetValue = "";
                if (lastTargetPosition != -1) {
                    dragAdapter.change(lastTargetPosition, dragSourceInfo.data);
                    lastTargetPosition = -1;
                    return true;
                }
                return false;
            }
        });
    }

    private boolean hitTest(View child, Rect borderBox) {
        int viewCenterX = (child.getLeft() + child.getRight()) / 2 + targetView.getLeft();
        int viewCenterY = (child.getTop() + child.getBottom()) / 2 + targetView.getTop();

        int boxCenterX = (borderBox.left + borderBox.right) / 2;
        int boxCenterY = (borderBox.top + borderBox.bottom) / 2;

        return Math.abs(viewCenterX - boxCenterX) <= 100 &&
                Math.abs(viewCenterY - boxCenterY) <= 100;
    }

    /**
     * 拖拽View是否在目标View之外
     * @return true, 拖拽View在目标View之外
     */
    private boolean outOfBound(Rect borderBox) {
        return borderBox.top >= targetView.getBottom();
    }

    private void insert(int position, String value) {
        dragAdapter.insert(position, value);
        if (position == 0) {
            targetView.smoothScrollToPosition(0);
        }
    }

}
