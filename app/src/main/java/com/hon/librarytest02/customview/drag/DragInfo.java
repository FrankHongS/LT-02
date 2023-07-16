package com.hon.librarytest02.customview.drag;

/**
 * Created by Frank Hon on 2023/7/17 12:33 上午.
 * E-mail: frank_hon@foxmail.com
 */
public class DragInfo<T> {

    public T data;
    public int position;

    public DragInfo(T data, int position) {
        this.data = data;
        this.position = position;
    }
}
