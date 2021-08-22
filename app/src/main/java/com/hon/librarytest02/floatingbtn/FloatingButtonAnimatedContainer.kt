package com.hon.librarytest02.floatingbtn

import android.animation.Animator
import android.animation.AnimatorSet
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

/**
 * Created by Frank Hon on 2021/8/22 4:23 下午.
 * E-mail: frank_hon@foxmail.com
 */
internal class FloatingButtonAnimatedContainer @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var leftView: View? = null
    private var rightView: View? = null
    private var rightViewLeftMargin: Int = 0
    private var rightViewEndPosition: Int = 0
    private var animatorSet: AnimatorSet? = null

    private val animators = arrayListOf<Animator>()

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        when (childCount) {
            2 -> {
                if (leftView == null) {
                    leftView = getChildAt(0)
                }
                if (rightView == null) {
                    rightView = getChildAt(1).apply {
                        rightViewLeftMargin = (layoutParams as MarginLayoutParams).marginStart
                    }
                }
            }
        }
    }

    fun hide(endPosition: Int = 0) {
        if (animatorSet != null) {
            animatorSet!!.cancel()
        }
        rightViewEndPosition = endPosition
        val leftAnimator = leftView?.let { left ->
            ObjectAnimator.ofFloat(left, View.ALPHA, 1f, 0f)
                .apply {
                    duration = 400
                    doOnEnd {
                        left.isVisible = false
                    }
                }
        }
        val rightAnimator = rightView?.let { right ->
            ValueAnimator.ofInt(rightViewLeftMargin, endPosition).apply {
                duration = 400
                addUpdateListener {
                    right.layoutParams =
                        (right.layoutParams as MarginLayoutParams).also { params ->
                            params.marginStart = it.animatedValue as Int
                        }
                }
            }
        }
        AnimatorSet().apply {
            playSequentially(
                leftAnimator,
                rightAnimator
            )
            doOnStart {
                animatorSet = this
            }
            doOnEnd {
                animatorSet = null
            }
        }.start()
    }

    fun show() {
        if (animatorSet != null) {
            animatorSet!!.cancel()
        }
        val leftAnimator = leftView?.let { left ->
            ObjectAnimator.ofFloat(left, View.ALPHA, 0f, 1f)
                .apply {
                    duration = 400
                    doOnStart {
                        left.isVisible = true
                    }
                }
        }
        val rightAnimator = rightView?.let { right ->
            ValueAnimator.ofInt(rightViewEndPosition, rightViewLeftMargin).apply {
                duration = 400
                addUpdateListener {
                    right.layoutParams =
                        (right.layoutParams as MarginLayoutParams).also { params ->
                            params.marginStart = it.animatedValue as Int
                        }
                }
            }
        }
        AnimatorSet().apply {
            playSequentially(
                rightAnimator,
                leftAnimator
            )
            doOnStart {
                animatorSet = this
            }
            doOnEnd {
                animatorSet = null
            }
        }.start()
    }
}