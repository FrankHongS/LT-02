package com.frankhon.customview.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.frankhon.customview.util.dp
import kotlin.math.max
import kotlin.math.min

/**
 * Created by shuaihua_a on 2023/3/3 14:58.
 * E-mail: hongshuaihua
 */
class RangeSeekBar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    interface OnTrackingTouchListener {
        fun onStartTrackingTouch(thumb: Thumb, start: Int, end: Int) {}

        fun onProgressChanged(thumb: Thumb, start: Int, end: Int) {}

        fun onStopTrackingTouch(thumb: Thumb, start: Int, end: Int) {}
    }

    enum class Thumb {
        NONE,
        LEFT,
        RIGHT
    }

    private val selectedPaint = Paint().apply {
        strokeWidth = 3.dp
        color = Color.parseColor("#009688")
        isAntiAlias = true
        style = Paint.Style.FILL
    }

    private val unselectedPaint = Paint().apply {
        strokeWidth = 3.dp
        color = Color.parseColor("#D0D0D0")
        isAntiAlias = true
        style = Paint.Style.FILL
    }

    private val thumbPaint = Paint().apply {
        strokeWidth = 3.dp
        color = Color.parseColor("#795548")
        isAntiAlias = true
        style = Paint.Style.FILL
    }

    private val defaultHeight = 56.dp.toInt()

    private val defaultWidth = 220.dp.toInt()
    private val horizontalPadding = 18.dp
    private val verticalPadding = 4.dp
    private val seekBarHeight = 7.dp

    private var startRatio = 0f
    private var endRatio = 0f
    private var startValue = 0
    private var endValue = 0

    val start
        get() = startValue
    val end
        get() = endValue
    var length = 100

    private val thumbWidth = 10.dp
    private val thumbRadius = 5.dp

    private val touchGap = 20.dp

    private val leftThumbRect = RectF()
    private val rightThumbRect = RectF()
    private val thumbPath = Path()

    private var lastThumb = Thumb.NONE
    private var lastTouchDownX = 0f
    private var lastStartRatio = startRatio
    private var lastEndRatio = endRatio

    // 除去边距后的数值条长度，由于需要获取width，注意调用时机
    private val realWidth
        get() = width - horizontalPadding * 2 - paddingRight - paddingLeft

    private var listener: OnTrackingTouchListener? = null

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
        // draw background
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
        // draw selected background
        canvas.drawRect(
            paddingLeft + horizontalPadding + startRatio * realWidth,
            (height - seekBarHeight) / 2,
            paddingLeft + horizontalPadding + endRatio * realWidth,
            (height + seekBarHeight) / 2,
            selectedPaint
        )

        // left thumb
        drawLeftThumb(canvas)
        // right thumb
        drawRightThumb(canvas)
    }

    private fun drawLeftThumb(canvas: Canvas) {
        refreshLeftThumbRect()
        thumbPath.run {
            reset()
            addArc(
                leftThumbRect.left,
                leftThumbRect.top,
                leftThumbRect.left + 2 * thumbRadius,
                leftThumbRect.top + thumbRadius * 2,
                180f,
                90f
            )
            lineTo(leftThumbRect.right, leftThumbRect.top)
            lineTo(leftThumbRect.right, leftThumbRect.bottom)
            lineTo(leftThumbRect.left + thumbRadius, leftThumbRect.bottom)
            arcTo(
                leftThumbRect.left,
                leftThumbRect.bottom - thumbRadius * 2,
                leftThumbRect.left + 2 * thumbRadius,
                leftThumbRect.bottom,
                90f,
                90f,
                false
            )
            close()
        }
        canvas.drawPath(thumbPath, thumbPaint)
    }

    private fun drawRightThumb(canvas: Canvas) {
        refreshRightThumbRect()
        thumbPath.run {
            reset()
            moveTo(rightThumbRect.left, rightThumbRect.top)
            lineTo(rightThumbRect.left + thumbWidth, rightThumbRect.top)
            arcTo(
                rightThumbRect.left + thumbWidth - thumbRadius,
                rightThumbRect.top,
                rightThumbRect.left + thumbWidth + thumbRadius,
                rightThumbRect.top + thumbRadius * 2,
                270f,
                90f,
                false
            )
            lineTo(rightThumbRect.right, rightThumbRect.bottom - thumbRadius)
            arcTo(
                rightThumbRect.right - 2 * thumbRadius,
                rightThumbRect.bottom - thumbRadius * 2,
                rightThumbRect.right,
                rightThumbRect.bottom,
                0f,
                90f,
                false
            )
            lineTo(rightThumbRect.left, rightThumbRect.bottom)
            close()
        }
        canvas.drawPath(thumbPath, thumbPaint)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                lastThumb = findProperThumb(event.x, event.y)
                lastTouchDownX = event.x
                if (lastThumb != Thumb.NONE) {
                    listener?.onStartTrackingTouch(
                        lastThumb,
                        (startRatio * length).toInt(),
                        (endRatio * length).toInt()
                    )
                }
            }
            MotionEvent.ACTION_MOVE -> {
                when (lastThumb) {
                    Thumb.LEFT -> {
                        startRatio =
                            max(
                                min(
                                    lastStartRatio + distanceToDuration(event.x - lastTouchDownX),
                                    endRatio
                                ),
                                0f
                            )
                        invalidate()
                    }
                    Thumb.RIGHT -> {
                        endRatio = min(
                            max(
                                startRatio,
                                lastEndRatio + distanceToDuration(event.x - lastTouchDownX)
                            ),
                            1f
                        )
                        invalidate()
                    }
                    else -> {}
                }
                if (lastThumb != Thumb.NONE) {
                    listener?.onProgressChanged(
                        lastThumb,
                        (startRatio * length).toInt(),
                        (endRatio * length).toInt()
                    )
                }
            }
            MotionEvent.ACTION_UP -> {
                if (lastThumb != Thumb.NONE) {
                    listener?.onStopTrackingTouch(
                        lastThumb,
                        (startRatio * length).toInt(),
                        (endRatio * length).toInt()
                    )
                }
                lastThumb = Thumb.NONE
                lastTouchDownX = 0f
                lastStartRatio = startRatio
                lastEndRatio = endRatio
            }
        }
        return true
    }

    private fun distanceToDuration(distance: Float): Float {
        return distance / realWidth
    }

    private fun refreshLeftThumbRect() {
        leftThumbRect.run {
            left =
                paddingLeft + horizontalPadding + startRatio * realWidth - thumbRadius - thumbWidth
            top = paddingTop + verticalPadding
            right = paddingLeft + horizontalPadding + startRatio * realWidth
            bottom = height - paddingBottom - verticalPadding
        }
    }

    private fun refreshRightThumbRect() {
        rightThumbRect.run {
            left =
                paddingLeft + horizontalPadding + endRatio * realWidth
            top = paddingTop + verticalPadding
            right =
                paddingLeft + horizontalPadding + endRatio * realWidth + thumbRadius + thumbWidth
            bottom = height - paddingBottom - verticalPadding
        }
    }

    private fun findProperThumb(x: Float, y: Float): Thumb {
        if (x in (leftThumbRect.left - touchGap)..leftThumbRect.right
            && y in leftThumbRect.top..leftThumbRect.bottom
        ) {
            return Thumb.LEFT
        }

        if (x in rightThumbRect.left..(rightThumbRect.right + touchGap)
            && y in rightThumbRect.top..rightThumbRect.bottom
        ) {
            return Thumb.RIGHT
        }

        if (x in leftThumbRect.right..rightThumbRect.left) {
            val deltaLeft = x - leftThumbRect.right
            val deltaRight = rightThumbRect.left - x
            if (deltaLeft <= deltaRight && deltaLeft <= 16.dp) {
                return Thumb.LEFT
            } else if (deltaLeft > deltaRight && deltaRight <= 16.dp) {
                return Thumb.RIGHT
            }
        }

        return Thumb.NONE
    }

    fun init(start: Int, end: Int) {
        if (start > end || start < 0 || end > length) {
            return
        }
        startValue = start
        endValue = end
        startRatio = start * 1f / length
        endRatio = end * 1f / length
        lastStartRatio = startRatio
        lastEndRatio = endRatio
        invalidate()
    }

    fun setOnTrackingTouchListener(listener: OnTrackingTouchListener) {
        this.listener = listener
    }
}