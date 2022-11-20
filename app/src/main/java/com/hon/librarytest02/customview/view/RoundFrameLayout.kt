package com.hon.librarytest02.customview.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Outline
import android.util.AttributeSet
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.FrameLayout
import com.hon.librarytest02.util.dp
import com.hon.mylogger.MyLogger

/**
 * Created by Frank Hon on 2022/11/12 10:59 下午.
 * E-mail: frank_hon@foxmail.com
 */
class RoundFrameLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    init {
        outlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(view: View, outline: Outline) {
                outline.setRoundRect(0, 0, view.width, view.height, 20.dp)
            }
        }
        clipToOutline = true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        MyLogger.d("onMeasure: ")
    }

    /**
     * 可以获取View宽高
     */
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        MyLogger.d("onSizeChanged: ")
        invalidateOutline()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        MyLogger.d("onDraw: ")
    }

}