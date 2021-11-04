package com.hon.librarytest02.gridview

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.get
import com.hon.librarytest02.R
import com.hon.librarytest02.util.dp
import kotlin.math.max

/**
 * Created by shuaihua on 2021/11/4 3:21 下午
 * Email: shuaihua@staff.sina.com.cn
 */
class GridTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr) {

    private var marginHorizontal = 10.dp.toInt()
    private var marginVertical = 10.dp.toInt()
    private var layoutId = 0
    private var spanCount = 2

    init {
        val a = getContext().obtainStyledAttributes(attrs, R.styleable.GridTextView)
        layoutId = a.getResourceId(R.styleable.GridTextView_itemLayout, 0)
        marginHorizontal =
            a.getDimensionPixelSize(R.styleable.GridTextView_marginHorizontal, marginHorizontal)
        marginVertical =
            a.getDimensionPixelSize(R.styleable.GridTextView_marginVertical, marginVertical)
        spanCount = a.getInt(R.styleable.GridTextView_spanCount, spanCount)
        a.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        measureChildren(widthMeasureSpec, heightMeasureSpec)
        var maxWidth = 0
        var maxHeight = 0
        var tempWidth = 0
        for (i in 0 until childCount) {
            val child = get(i)
//            measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0)
            if (i % spanCount == 0) {
                maxWidth = max(maxWidth, tempWidth)
                tempWidth = 0
                maxHeight += child.measuredHeight + marginVertical
            }
//            if (widthMode == MeasureSpec.EXACTLY) {
//                if (i % spanCount == spanCount - 1) {
//                    if (widthSize - tempWidth < child.measuredWidth) {
//                        val childWidthMeasureSpec =
//                            MeasureSpec.makeMeasureSpec(widthSize - tempWidth, MeasureSpec.EXACTLY)
//                        val childHeightMeasureSpec =
//                            MeasureSpec.makeMeasureSpec(child.measuredHeight, MeasureSpec.EXACTLY)
//                        child.measure(childWidthMeasureSpec, childHeightMeasureSpec)
//                    }
//                }
//            }
            tempWidth += child.measuredWidth + marginHorizontal
        }
        maxWidth = max(maxWidth, tempWidth)
        setMeasuredDimension(
            maxWidth + marginHorizontal,
            maxHeight + marginVertical
        )
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        var tempPaddingLeft = paddingLeft
        var tempPaddingTop = paddingTop
        for (i in 0 until childCount) {
            val view = get(i)
            view.layout(
                tempPaddingLeft + marginHorizontal,
                tempPaddingTop + marginVertical,
                tempPaddingLeft + view.measuredWidth + marginHorizontal,
                tempPaddingTop + view.measuredHeight + marginVertical
            )
            if (i % spanCount != spanCount - 1) {
                tempPaddingLeft += view.measuredWidth + marginHorizontal
            } else {
                tempPaddingLeft = paddingLeft
                tempPaddingTop += view.measuredHeight + marginVertical
            }
        }
    }

    fun setData(strList: List<String>) {
        strList.forEach {
            val textView = LayoutInflater.from(context).inflate(layoutId, null)
                    as? TextView
            textView?.run {
                addView(this.apply {
                    text = it
                })
            }
        }
        requestLayout()
    }
}
