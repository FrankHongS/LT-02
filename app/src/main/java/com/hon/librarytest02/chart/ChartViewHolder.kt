package com.hon.librarytest02.chart

import android.graphics.Color
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.hon.librarytest02.R
import com.hon.simplechartview.ChartEntity
import com.hon.simplechartview.ChartView

/**
 * Created by Frank Hon on 2019/4/21 10:06 AM.
 * E-mail: frank_hon@foxmail.com
 */
class ChartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val dataList= listOf(
            ChartEntity(
                    listOf(10f,20f,30f,40f,50f,60f,70f,70f,70f,70f,70f,70f,80f),
                    Color.parseColor("#ff0000"),
                    Color.parseColor("#0000ff")
            ),
            ChartEntity(
                    listOf(15f,25f,35f,45f,55f,60f),
                    Color.parseColor("#ffff00"),
                    Color.parseColor("#00ffff")
            )
    )

    private val labelList= listOf(
            "第1天","第2天","第3天","第4天","第5天","第6天","第7天","第8天"
    )

    private val chartView: ChartView =itemView.findViewById(R.id.testChartView01)

    fun bindView(){
        chartView.setChartData(dataList,labelList)
    }

}