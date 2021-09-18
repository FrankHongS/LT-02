package com.hon.librarytest02.floatingbtn

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.view.isVisible
import com.hon.librarytest02.util.Util

/**
 * Created by shuaihua on 2021/8/20 10:44 上午
 * Email: shuaihua@staff.sina.com.cn
 */
class FloatingButtonContainer @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var leftChild: View? = null
    private var rightChild: View? = null

    private var enableAnimation = true
    private var rightChildStartPosition = 0
    private var rightChildEndPosition = 0

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        when (childCount) {
            0 -> {
                throw RuntimeException("Child count must be more than 0")
            }
            1 -> {
                enableAnimation = false
            }
            2 -> {
                if (leftChild == null) {
                    leftChild = getChildAt(0)
                }
                if (rightChild == null) {
                    rightChild = getChildAt(1)
                    rightChildStartPosition =
                        (rightChild!!.layoutParams as MarginLayoutParams).leftMargin
                    Log.d(
                        "frankhon",
                        "onLayout: width=${leftChild!!.width} rightChildRightMargin=${rightChildStartPosition}"
                    )
                }
            }
            else -> {
                throw RuntimeException("Child count must not be more than 2")
            }
        }
    }

    fun hide(endPosition: Int) {
        if (!enableAnimation) {
            return
        }
        this.rightChildEndPosition = endPosition
        //左边view动画
        leftChild?.let { left ->
            generateLeftAnimator(left, 1f, 0f)
                .apply {
                    doOnEnd {
                        left.isVisible = false
                        //右边view动画
                        rightChild?.let { right ->
                            generateRightAnimator(right, rightChildStartPosition, endPosition)
                                .start()
                        }
                    }
                }.start()
        }

    }

    fun show() {
        if (!enableAnimation) {
            return
        }
        //右边view动画
        rightChild?.let { right ->
            generateRightAnimator(right, rightChildEndPosition, rightChildStartPosition)
                .apply {
                    doOnEnd {
                        // 左边view动画
                        leftChild?.let { left ->
                            generateLeftAnimator(left, 0f, 1f)
                                .apply {
                                    doOnStart {
                                        left.isVisible = true
                                    }
                                }
                                .start()
                        }
                    }
                }
                .start()
        }
    }

    private fun generateLeftAnimator(view: View, vararg values: Float): ObjectAnimator =
        ObjectAnimator.ofFloat(view, View.ALPHA, values[0], values[1])
            .apply {
                duration = 400
            }

    private fun generateRightAnimator(view: View, vararg values: Int): ValueAnimator =
        ValueAnimator.ofInt(values[0], values[1]).apply {
            duration = 400
            addUpdateListener {
                view.layoutParams =
                    (view.layoutParams as MarginLayoutParams).also { params ->
                        params.marginStart = it.animatedValue as Int
                    }
            }
        }
}