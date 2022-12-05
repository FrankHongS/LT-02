package com.hon.librarytest02.customview.view.paging

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.frankhon.customview.paging.PagingAdapter
import com.hon.librarytest02.R
import com.hon.mylogger.MyLogger

/**
 * Created by Frank Hon on 2022/11/19 5:48 下午.
 * E-mail: frank_hon@foxmail.com
 */
class MyPagingAdapter : PagingAdapter<String>(10) {

    override fun onCreateNormalViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return NormalViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_paging_normal, parent, false)
        )
    }

    override fun onBindNormalViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        val item = dataList[position]
        MyLogger.d("onBindNormalViewHolder: $item $holder")
        if (holder is NormalViewHolder) {
            holder.bindView(item, position)
        }
    }

    private class NormalViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val content = view.findViewById<TextView>(R.id.tv_content)

        fun bindView(item: String, position: Int) {
            content.text = item
        }

    }
}