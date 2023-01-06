package com.frankhon.customview.round

import android.content.Context
import android.graphics.Outline
import android.util.AttributeSet
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.FrameLayout
import com.frankhon.customview.R
import com.frankhon.customview.util.dp

/**
 * 圆角FrameLayout
 *
 * Created by Frank Hon on 2022/11/12 10:59 下午.
 * E-mail: frank_hon@foxmail.com
 */
class RoundFrameLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val radius: Float

    init {
        val typedArray = getContext().obtainStyledAttributes(
            attrs, R.styleable.RoundFrameLayout
        )
        radius = typedArray.getDimension(R.styleable.RoundFrameLayout_radius_f, 20.dp)
        typedArray.recycle()
        outlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(view: View, outline: Outline) {
                outline.setRoundRect(0, 0, view.width, view.height, radius)
            }
        }
        clipToOutline = true
    }

    /**
     * 可以获取View宽高
     */
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        invalidateOutline()
    }

}