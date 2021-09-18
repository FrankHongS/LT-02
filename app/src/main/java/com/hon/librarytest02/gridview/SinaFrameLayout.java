package com.hon.librarytest02.gridview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.hon.librarytest02.R;
import com.hon.librarytest02.util.Util;

/**
 * Created by shuaihua on 2021/8/5 3:20 下午
 * Email: shuaihua@staff.sina.com.cn
 */
public class SinaFrameLayout extends FrameLayout {
    public SinaFrameLayout(Context context) {
        this(context, null);
    }

    public SinaFrameLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SinaFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int mode = MeasureSpec.getMode(widthMeasureSpec);
        switch (mode) {
            case MeasureSpec.UNSPECIFIED:
                Log.d("frankhon", "SinaFrameLayout onMeasure: UNSPECIFIED");
                break;
            case MeasureSpec.AT_MOST:
                Log.d("frankhon", "SinaFrameLayout onMeasure: AT_MOST");
                break;
            case MeasureSpec.EXACTLY:
                Log.d("frankhon", "SinaFrameLayout onMeasure: EXACTLY");
                break;
        }
        int size = MeasureSpec.getSize(widthMeasureSpec);
        Log.d("frankhon", "SinaFrameLayout onMeasure: " + Util.px2dp(getMeasuredWidth()) +
                ", size=" + Util.px2dp(size) + ", " + size +
                ", parentWidth=" + Util.px2dp(Util.getDisplayMetrics().widthPixels) + ", "
                + Util.getDisplayMetrics().widthPixels);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        Log.d("frankhon", "onLayout: width=" + Util.px2dp(getWidth()) + ", " +
                "height=" + Util.px2dp(getHeight()));
    }
}
