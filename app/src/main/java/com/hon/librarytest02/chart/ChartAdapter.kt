package com.hon.librarytest02.chart

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hon.librarytest02.R

/**
 * Created by Frank Hon on 2019/4/21 10:05 AM.
 * E-mail: frank_hon@foxmail.com
 */
class ChartAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.item_chart,parent,false)
        return ChartViewHolder(view)
    }

    override fun getItemCount(): Int {
        return 3
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ChartViewHolder).bindView()
    }

}