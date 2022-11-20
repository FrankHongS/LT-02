package com.hon.librarytest02.customview.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.children
import androidx.customview.widget.ViewDragHelper
import com.hon.librarytest02.util.dp
import kotlin.math.abs

/**
 * Created by Frank Hon on 2022/7/18 6:47 下午.
 * E-mail: frank_hon@foxmail.com
 */
class SlidingUpPanelLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : ViewGroup(context, attrs, defStyle) {

    companion object {
        // unit, dp
        private const val PANEL_HEIGHT = 68
    }

    private var bottomView: View? = null
    private val touchPoint = Array(2) { 0f }

    private var isShadowAdded = false
    private val shadowView by lazy {
        View(context).apply {
            layoutParams = LayoutParams(width, height - PANEL_HEIGHT.dp.toInt())
            setBackgroundColor(0x000000)
        }
    }

    private val viewDragHelperCallback by lazy {
        object : ViewDragHelper.Callback() {
            override fun tryCaptureView(child: View, pointerId: Int): Boolean {
                return bottomView == child
            }

            override fun clampViewPositionVertical(child: View, top: Int, dy: Int): Int {
                return if (top <= height - child.height) {
                    height - child.height
                } else if (top >= height - PANEL_HEIGHT.dp.toInt()) {
                    height - PANEL_HEIGHT.dp.toInt()
                } else {
                    top
                }
            }

            override fun onViewPositionChanged(
                changedView: View,
                left: Int,
                top: Int,
                dx: Int,
                dy: Int
            ) {

            }

            override fun onViewReleased(releasedChild: View, xvel: Float, yvel: Float) {
                val offsetPercent = (height - releasedChild.top - PANEL_HEIGHT.dp.toInt()) * 1f /
                        (releasedChild.height - PANEL_HEIGHT.dp.toInt())
                val finalTop = if (offsetPercent > 0.25f && yvel <= 0) {
                    height - releasedChild.height
                } else {
                    height - PANEL_HEIGHT.dp.toInt()
                }
                viewDragHelper.settleCapturedViewAt(left, finalTop)
                invalidate()
            }

            override fun getViewVerticalDragRange(child: View): Int {
                return 0
            }
        }
    }

    private val viewDragHelper: ViewDragHelper by lazy {
        ViewDragHelper.create(this, 0.5f, viewDragHelperCallback)
    }

    private fun showShadow() {
        if (childCount > 1 && !isShadowAdded) {
            isShadowAdded = true
            addView(shadowView, 1)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        Log.d(
            "frankhon", "onMeasure: " +
                    "width=${MeasureSpec.getSize(widthMeasureSpec)}," +
                    "widthMode=${
                        when (MeasureSpec.getMode(widthMeasureSpec)) {
                            MeasureSpec.AT_MOST -> "AT_MOST"
                            MeasureSpec.UNSPECIFIED -> "UNSPECIFIED"
                            MeasureSpec.EXACTLY -> "EXACTLY"
                            else -> ""
                        }
                    }," +
                    "height=${MeasureSpec.getSize(heightMeasureSpec)}," +
                    "heightMode=${
                        when (MeasureSpec.getMode(heightMeasureSpec)) {
                            MeasureSpec.AT_MOST -> "AT_MOST"
                            MeasureSpec.UNSPECIFIED -> "UNSPECIFIED"
                            MeasureSpec.EXACTLY -> "EXACTLY"
                            else -> ""
                        }
                    }"
        )
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        if (childCount > 0 && heightMode != MeasureSpec.UNSPECIFIED) {
            val height = MeasureSpec.getSize(heightMeasureSpec)
            val topView = getChildAt(0)
            measureChild(
                topView,
                widthMeasureSpec,
                MeasureSpec.makeMeasureSpec(height - PANEL_HEIGHT.dp.toInt(), heightMode)
            )
            if (childCount > 1) {
                val bottomView = getChildAt(1)
                val layoutParams = bottomView.layoutParams
                val bottomHeightMeasureSpec = when (layoutParams.height) {
                    LayoutParams.WRAP_CONTENT -> MeasureSpec.makeMeasureSpec(
                        0,
                        MeasureSpec.UNSPECIFIED
                    )
                    LayoutParams.MATCH_PARENT -> MeasureSpec.makeMeasureSpec(
                        PANEL_HEIGHT.dp.toInt(),
                        MeasureSpec.EXACTLY
                    )
                    else -> MeasureSpec.makeMeasureSpec(layoutParams.height, MeasureSpec.EXACTLY)
                }
                bottomView.measure(widthMeasureSpec, bottomHeightMeasureSpec)
                this.bottomView = bottomView
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var tempPaddingTop = paddingTop
        children.forEach {
            it.layout(
                paddingLeft,
                tempPaddingTop,
                paddingLeft + it.measuredWidth,
                tempPaddingTop + it.measuredHeight
            )
            tempPaddingTop += it.measuredHeight
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return viewDragHelper.shouldInterceptTouchEvent(ev)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                touchPoint[0] = event.x
                touchPoint[1] = event.y
            }
            MotionEvent.ACTION_MOVE -> {
                val deltaX = abs(event.x - touchPoint[0])
                val deltaY = abs(event.y - touchPoint[1])
                touchPoint[0] = event.x
                touchPoint[1] = event.y
                if (deltaX >= deltaY) {
                    return false
                }
            }
        }
        viewDragHelper.processTouchEvent(event)
        return true
    }

    override fun computeScroll() {
        if (viewDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this)
        }
    }
}