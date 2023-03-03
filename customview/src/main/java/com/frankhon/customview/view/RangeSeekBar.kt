package com.frankhon.customview.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.frankhon.customview.util.dp

/**
 * Created by shuaihua_a on 2023/3/3 14:58.
 * E-mail: hongshuaihua
 */
class RangeSeekBar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val selectedPaint = Paint().apply {
        strokeWidth = 3.dp
        color = Color.GREEN
        isAntiAlias = true
        style = Paint.Style.FILL
    }

    private val unselectedPaint = Paint().apply {
        strokeWidth = 3.dp
        color = Color.GRAY
        isAntiAlias = true
        style = Paint.Style.FILL
    }

    private val thumbPaint = Paint().apply {
        strokeWidth = 3.dp
        color = Color.CYAN
        isAntiAlias = true
        style = Paint.Style.FILL
    }

    private val path = Path()

    private val defaultHeight = 56.dp.toInt()
    private val defaultWidth = 220.dp.toInt()
    private val horizontalPadding = 18.dp
    private val verticalPadding = 4.dp
    private val seekBarHeight = 7.dp

    private val total = 300f
    private var start = 0f
    private var end = 180f

    private val thumbWidth = 10.dp
    private val thumbRadius = 5.dp

    private val thumbMatrix = Matrix()

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        var widthSize = MeasureSpec.getSize(widthMeasureSpec)
        if (widthMode != MeasureSpec.EXACTLY) {
            widthSize = defaultWidth
        }
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        var heightSize = MeasureSpec.getSize(heightMeasureSpec)
        if (heightMode != MeasureSpec.EXACTLY) {
            heightSize = defaultHeight
        }
        setMeasuredDimension(widthSize, heightSize)
    }

    override fun onDraw(canvas: Canvas) {
        val radius = (height - verticalPadding * 2) / 2
        canvas.drawRoundRect(
            paddingLeft + horizontalPadding,
            (height - seekBarHeight) / 2,
            width - horizontalPadding - paddingRight,
            (height + seekBarHeight) / 2,
            radius,
            radius,
            unselectedPaint
        )
        val realWidth = width - horizontalPadding * 2 - paddingRight - paddingLeft
        canvas.drawRect(
            paddingLeft + horizontalPadding + start / total * realWidth,
            (height - seekBarHeight) / 2,
            paddingLeft + horizontalPadding + end / total * realWidth,
            (height + seekBarHeight) / 2,
            selectedPaint
        )

        path.addArc(
            paddingLeft + horizontalPadding + start / total * realWidth - thumbRadius - thumbWidth,
            paddingTop + verticalPadding,
            paddingLeft + horizontalPadding + start / total * realWidth + thumbRadius - thumbWidth,
            paddingTop + verticalPadding + thumbRadius * 2,
            180f,
            90f
        )
        path.lineTo(
            paddingLeft + horizontalPadding + start / total * realWidth,
            paddingTop + verticalPadding
        )
        path.lineTo(
            paddingLeft + horizontalPadding + start / total * realWidth,
            height - paddingBottom - verticalPadding
        )
        path.lineTo(
            paddingLeft + horizontalPadding + start / total * realWidth - thumbWidth,
            height - paddingBottom - verticalPadding
        )
        path.arcTo(
            paddingLeft + horizontalPadding + start / total * realWidth - thumbRadius - thumbWidth,
            height - paddingBottom - verticalPadding - thumbRadius * 2,
            paddingLeft + horizontalPadding + start / total * realWidth + thumbRadius - thumbWidth,
            height - paddingBottom - verticalPadding,
            90f,
            90f,
            false
        )
        path.close()
        canvas.drawPath(path, thumbPaint)

    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return super.onTouchEvent(event)
    }

}