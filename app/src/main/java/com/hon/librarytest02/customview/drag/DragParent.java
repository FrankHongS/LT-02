package com.hon.librarytest02.customview.drag;

import android.view.View;

/**
 * Created by Frank Hon on 2023/7/15 11:56 下午.
 * E-mail: frank_hon@foxmail.com
 */
interface DragParent<T> {

    void onDragStart(View child);

    void onDrag(float dx, float dy);

    boolean onDragEnd(DragInfo<T> dragSourceInfo);

}
