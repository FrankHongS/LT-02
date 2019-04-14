package com.hon.librarytest02.chart

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hon.librarytest02.R
import kotlinx.android.synthetic.main.activity_chart.*

/**
 * Created by Frank Hon on 2019/4/14 1:08 AM.
 * E-mail: frank_hon@foxmail.com
 */
class ChartActivity:AppCompatActivity() {

    // Test Data
    private val mStrDayRoomData = "22,23,23,23,23,23,23,23,24,23,25,26"
    private val mStrDaySettingData = "26,26,26,26,26,26,26,26,26,16,16,17"
    private val mStrDayPowerTimeData = "80,90,91,93,100,100,100,100,50,70,89,60,100,60,70,80,80,90,95,100,100,100,100,100"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chart)

        initView()
    }

    fun initView(){
        testChartView.setOnViewLayoutListener{
            updateView()
        }
    }

    fun updateView(){
        testChartView.setData("$mStrDayRoomData-$mStrDaySettingData-$mStrDayPowerTimeData",0)
    }

}