package com.hon.librarytest02.chart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hon.librarytest02.R
import com.hon.simplechartview.ChartEntity
import com.hon.simplechartview.ChartView01
import kotlinx.android.synthetic.main.activity_chart_01.*
import kotlinx.android.synthetic.main.item_chart_01.*

/**
 * Created by Frank Hon on 2019/4/14 9:12 PM.
 * E-mail: frank_hon@foxmail.com
 */
class ChartActivity01:AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chart_01)

        initView()
//        testChartView01.setChartData(dataList)
    }

    private fun initView(){
        testRV.adapter=ChartAdapter()
        testRV.layoutManager=LinearLayoutManager(this)
    }

    class ChartAdapter:RecyclerView.Adapter<RecyclerView.ViewHolder>(){

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val view=LayoutInflater.from(parent.context).inflate(R.layout.item_chart_01,parent,false)
            return ChartViewHolder(view)
        }

        override fun getItemCount(): Int {
            return 3
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            (holder as ChartViewHolder).bindView()
        }

    }

    class ChartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val dataList= listOf(
                ChartEntity(
                        listOf(10f,20f,30f,40f,50f,60f,70f,70f,70f,70f,70f,70f,80f),
                        "#ff0000",
                        "#0000ff"
                ),
                ChartEntity(
                        listOf(15f,25f,35f,45f,55f,60f),
                        "#ffff00",
                        "#00ffff"
                )
        )

        private val labelList= listOf(
                "第1天","第2天","第3天","第4天","第5天","第6天","第7天","第8天"
        )

        private val chartView:ChartView01=itemView.findViewById(R.id.testChartView01)

        fun bindView(){
            chartView.setChartData(dataList,labelList)
        }

    }

}