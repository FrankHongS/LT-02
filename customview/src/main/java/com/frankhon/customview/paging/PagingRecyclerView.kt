package com.frankhon.customview.paging

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.RecyclerView
import com.frankhon.customview.R

/**
 * Created by Frank Hon on 2022/11/20 5:27 下午.
 * E-mail: frank_hon@foxmail.com
 */
class PagingRecyclerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {

    private var placeholderItemHeight = -1
    private var invisibleItemCount = 2

    init {
        val typedArray = getContext().obtainStyledAttributes(
            attrs, R.styleable.PagingRecyclerView
        )
        placeholderItemHeight = typedArray.getDimensionPixelSize(
            R.styleable.PagingRecyclerView_placeholderItemHeight,
            -1
        )
        invisibleItemCount =
            typedArray.getInteger(R.styleable.PagingRecyclerView_invisibleItemCount, 2)
        typedArray.recycle()
    }

    override fun setAdapter(adapter: Adapter<*>?) {
        (adapter as? PagingAdapter<*>)?.let {
            it.setPlaceholderItemHeight(placeholderItemHeight)
            it.setInvisibleItemCountWhenPaging(invisibleItemCount)
            super.setAdapter(it)
        } ?: kotlin.run {
            throw RuntimeException("PagingRecyclerView's adapter should be PagingAdapter")
        }
    }

}