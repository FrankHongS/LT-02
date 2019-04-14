package com.hon.librarytest02.chart

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hon.librarytest02.R
import kotlinx.android.synthetic.main.activity_chart_01.*

/**
 * Created by Frank Hon on 2019/4/14 9:12 PM.
 * E-mail: frank_hon@foxmail.com
 */
class ChartActivity01:AppCompatActivity() {

    private val dataList= listOf(
            listOf(10f,20f,30f,40f,50f,60f),
            listOf(15f,25f,35f,45f,55f,60f)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chart_01)

        testChartView01.setChartData(dataList)
    }

}