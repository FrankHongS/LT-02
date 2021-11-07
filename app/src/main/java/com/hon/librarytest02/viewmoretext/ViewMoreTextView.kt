package com.hon.librarytest02.viewmoretext

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

/**
 * From https://github.com/mike5v/viewmore-textview
 */
class ViewMoreTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {

    companion object {
        const val ANIMATION_PROPERTY_MAX_HEIGHT = "maxHeight"
        const val DEFAULT_ELLIPSIZED_TEXT = "..."
    }

    private var visibleLines: Int = 3
    private var isExpanded = false
    private var animationDuration = 300
    private var ellipsizeTexts: Pair<String, String>? = null
    private var initialValue: String? = null
    private var isUnderlined = false
    private var ellipsizeTextColor = Color.parseColor("blue")

    private val visibleText by lazy { visibleText() }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (initialValue.isNullOrBlank()) {
            initialValue = text.toString()
            setMaxLines()
            setEllipsizedText()
        }
    }

    fun toggle() {
        if (visibleText.isAllTextVisible()) {
            return
        }
        isExpanded = !isExpanded
        if (isExpanded) {
            setEllipsizedText()
        }
        val startHeight = measuredHeight
        setMaxLines()
        measure(
            MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        )
        val endHeight = measuredHeight

        animationSet(startHeight, endHeight).apply {
            duration = animationDuration.toLong()
            start()

            addListener(object : Animator.AnimatorListener {
                override fun onAnimationEnd(animation: Animator?) {
                    if (!isExpanded) {
                        setEllipsizedText()
                    }
                }

                override fun onAnimationRepeat(animation: Animator?) {}
                override fun onAnimationCancel(animation: Animator?) {}
                override fun onAnimationStart(animation: Animator?) {}
            })
        }
    }

    fun setVisibleLines(visibleLines: Int): ViewMoreTextView {
        this.visibleLines = visibleLines
        return this
    }

    fun setAnimationDuration(animationDuration: Int): ViewMoreTextView {
        this.animationDuration = animationDuration
        return this
    }

    fun setIsUnderlined(isUnderlined: Boolean): ViewMoreTextView {
        this.isUnderlined = isUnderlined
        return this
    }

    fun setEllipsizedText(ellipsizeTexts: Pair<String, String>): ViewMoreTextView {
        this.ellipsizeTexts = ellipsizeTexts
        return this
    }

    fun setEllipsizedTextColor(ellipsizeTextColor: Int): ViewMoreTextView {
        this.ellipsizeTextColor = ellipsizeTextColor
        return this
    }

    fun isExpand() = isExpanded

    private fun setEllipsizedText() {
        if (initialValue.isNullOrBlank()) {
            return
        }

        text = if (isExpanded || visibleText.isAllTextVisible()) {
            SpannableStringBuilder(initialValue)
                .append(ellipsizeTexts?.second.orEmpty().span())
        } else {
            SpannableStringBuilder(
                visibleText.substring(
                    0,
                    visibleText.length - (ellipsizeTexts?.first.orEmpty().length + DEFAULT_ELLIPSIZED_TEXT.length)
                )
            ).append(DEFAULT_ELLIPSIZED_TEXT)
                .append(ellipsizeTexts?.first.orEmpty().span())

        }
    }

    private fun visibleText(): String {
        var end = 0

        for (i in 0 until visibleLines) {
            if (layout.getLineEnd(i) != 0)
                end = layout.getLineEnd(i)
        }

        return initialValue?.substring(0, end)!!
    }

    private fun setMaxLines() {
        maxLines = if (isExpanded) {
            Integer.MAX_VALUE
        } else {
            visibleLines
        }
    }

    private fun animationSet(startHeight: Int, endHeight: Int): AnimatorSet {
        return AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofInt(
                    this@ViewMoreTextView,
                    ANIMATION_PROPERTY_MAX_HEIGHT,
                    startHeight,
                    endHeight
                )
            )
        }
    }

    private fun String.isAllTextVisible(): Boolean = this == text

    private fun String.span(): SpannableString =
        SpannableString(this).apply {
            setSpan(
                ForegroundColorSpan(ellipsizeTextColor),
                0,
                length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            if (isUnderlined) {
                setSpan(UnderlineSpan(), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }

}