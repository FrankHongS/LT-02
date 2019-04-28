package com.hon.librarytest02.chart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hon.librarytest02.R
import kotlinx.android.synthetic.main.fragment_chart.*

/**
 * Created by Frank Hon on 2019/4/21 9:58 AM.
 * E-mail: frank_hon@foxmail.com
 */
class Fragment3:Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view=inflater.inflate(R.layout.fragment_chart,container,false)

        initView(view)

        return view
    }

    private fun initView(view: View){
        val rv=view.findViewById<RecyclerView>(R.id.testRV)
        rv.adapter=ChartAdapter()
        rv.layoutManager= LinearLayoutManager(context)
    }

}