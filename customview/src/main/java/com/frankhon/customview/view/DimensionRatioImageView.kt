package com.frankhon.customview.view

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.frankhon.customview.R

/**
 * 限定宽高比的ImageView
 *
 * Created by shuaihua_a on 2023/1/6 11:29.
 * E-mail: hongshuaihua
 */
class DimensionRatioImageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    private var dimensionRatio = 1f

    init {
        val typedArray = getContext().obtainStyledAttributes(
            attrs, R.styleable.DimensionRatioImageView
        )
        val dimensionRatioStr =
            typedArray.getString(R.styleable.DimensionRatioImageView_dimensionRatio)
        dimensionRatioStr?.split(":")?.let {
            if (it.size >= 2) {
                dimensionRatio = it[0].toFloat() / it[1].toFloat()
            }
        }
        typedArray.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var widthSize = MeasureSpec.getSize(widthMeasureSpec)
        var heightSize = MeasureSpec.getSize(heightMeasureSpec)
        if (widthSize == 0) {
            widthSize = (heightSize * dimensionRatio).toInt()
        }
        if (heightSize == 0) {
            heightSize = (widthSize / dimensionRatio).toInt()
        }
        setMeasuredDimension(widthSize, heightSize)
    }

}