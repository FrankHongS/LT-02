package com.frankhon.customview.paging

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.frankhon.customview.R

/**
 * Created by Frank Hon on 2022/11/19 12:07 下午.
 * E-mail: frank_hon@foxmail.com
 */
abstract class PagingAdapter<T> : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_NORMAL = 0
        private const val TYPE_LOADING = 1
        private const val TYPE_ERROR = 2
        private const val TYPE_NO_MORE = 3
    }

    protected val dataList = mutableListOf<T>()
    private var pagingState = PagingState.STATE_NORMAL
    private lateinit var recyclerView: RecyclerView
    private var onLoadListener: (PagingAdapter<T>.() -> Unit)? = null
    private val onPagingScrollListener by lazy {
        object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                pagingWhenScrollStateChanged(recyclerView, newState)
            }
        }
    }

    private var placeholderItemHeight = -1

    // invisible item count when paging
    private var invisibleItemCount = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_LOADING -> LoadingViewHolder(
                parent.inflate(R.layout.cv_item_paging_loading_default)
                    .updateHeight(placeholderItemHeight)
            )
            TYPE_ERROR -> ErrorViewHolder(
                parent.inflate(R.layout.cv_item_paging_error_default)
                    .updateHeight(placeholderItemHeight)
            )
            TYPE_NO_MORE -> NoMoreViewHolder(
                parent.inflate(R.layout.cv_item_paging_no_more_default)
                    .updateHeight(placeholderItemHeight)
            )
            else -> onCreateNormalViewHolder(parent, viewType)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            TYPE_ERROR -> {
                if (holder is ErrorViewHolder) {
                    holder.itemView.setOnClickListener {
                        loadData()
                    }
                }
            }
            TYPE_LOADING, TYPE_NO_MORE -> {}
            else -> {
                onBindNormalViewHolder(holder, position)
            }
        }
    }

    override fun getItemCount(): Int {
        val extraCount = when (pagingState) {
            PagingState.STATE_LOADING,
            PagingState.STATE_ERROR,
            PagingState.STATE_NO_MORE -> 1
            else -> 0
        }
        return dataList.size + extraCount
    }

    override fun getItemViewType(position: Int): Int {
        if (position == itemCount - 1) {
            return when (pagingState) {
                PagingState.STATE_LOADING -> TYPE_LOADING
                PagingState.STATE_ERROR -> TYPE_ERROR
                PagingState.STATE_NO_MORE -> TYPE_NO_MORE
                else -> getNormalItemViewType(position)
            }
        }
        return getNormalItemViewType(position)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        recyclerView.addOnScrollListener(onPagingScrollListener)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        recyclerView.removeOnScrollListener(onPagingScrollListener)
    }

    abstract fun onCreateNormalViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder

    abstract fun onBindNormalViewHolder(holder: RecyclerView.ViewHolder, position: Int)

    open fun getNormalItemViewType(position: Int): Int = TYPE_NORMAL

    @SuppressLint("NotifyDataSetChanged")
    fun addData(newData: List<T>?) {
        newData?.takeIf { it.isNotEmpty() }?.let {
            pagingState = PagingState.STATE_NORMAL
            dataList.addAll(it)
            notifyDataSetChanged()
        } ?: kotlin.run {
            pagingState = PagingState.STATE_NO_MORE
            notifyItemChanged(itemCount)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(newData: List<T>) {
        if (dataList == newData) {
            return
        }
        pagingState = PagingState.STATE_NORMAL
        dataList.run {
            clear()
            addAll(newData)
        }
        notifyDataSetChanged()
    }

    fun markErrorState() {
        pagingState = PagingState.STATE_ERROR
        notifyItemChanged(itemCount)
    }

    fun markNoMoreState() {
        pagingState = PagingState.STATE_NO_MORE
        notifyItemChanged(itemCount)
    }

    fun setOnLoadListener(listener: PagingAdapter<T>.() -> Unit) {
        this.onLoadListener = listener
    }

    fun getDataSize() = dataList.size

    /**
     * @param height, unit pixels
     */
    internal fun setPlaceholderItemHeight(height: Int) {
        this.placeholderItemHeight = height
    }

    internal fun setInvisibleItemCountWhenPaging(invisibleItemCount: Int) {
        this.invisibleItemCount = invisibleItemCount
    }

    private fun pagingWhenScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        val layoutManager = recyclerView.layoutManager
        val totalCount = layoutManager?.itemCount ?: 0
        val lastVisibleItemPosition = getLastVisibleItem(layoutManager)
        if (newState != RecyclerView.SCROLL_STATE_IDLE &&
            pagingState == PagingState.STATE_NORMAL &&
            lastVisibleItemPosition >= totalCount - invisibleItemCount
        ) {
            loadData()
        }
    }

    private fun loadData() {
        pagingState = PagingState.STATE_LOADING
        notifyItemChanged(itemCount)
        onLoadListener?.invoke(this)
    }

    private fun getLastVisibleItem(layoutManager: RecyclerView.LayoutManager?): Int {
        return when (layoutManager) {
            is LinearLayoutManager -> layoutManager.findLastVisibleItemPosition()
            is GridLayoutManager -> layoutManager.findLastVisibleItemPosition()
            is StaggeredGridLayoutManager -> {
                val positionArray = IntArray(layoutManager.spanCount)
                layoutManager.findLastVisibleItemPositions(positionArray)
                positionArray.maxOrNull() ?: 0
            }
            else -> 0
        }
    }

    private fun ViewGroup.inflate(@LayoutRes layoutId: Int): View {
        return LayoutInflater.from(context).inflate(layoutId, this, false)
    }

    private fun View.updateHeight(newHeight: Int): View = apply {
        if (newHeight > 0) {
            layoutParams = layoutParams.apply { height = placeholderItemHeight }
        }
    }

    enum class PagingState {
        STATE_NORMAL,
        STATE_LOADING,
        STATE_ERROR,
        STATE_NO_MORE
    }

    private class LoadingViewHolder(view: View) : RecyclerView.ViewHolder(view)
    private class ErrorViewHolder(view: View) : RecyclerView.ViewHolder(view)
    private class NoMoreViewHolder(view: View) : RecyclerView.ViewHolder(view)
}