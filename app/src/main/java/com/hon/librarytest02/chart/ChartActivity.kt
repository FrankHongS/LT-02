package com.hon.librarytest02.chart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hon.librarytest02.R
import com.hon.simplechartview.ChartEntity
import com.hon.simplechartview.ChartView
import kotlinx.android.synthetic.main.activity_chart.*
import kotlinx.android.synthetic.main.fragment_chart.*

/**
 * Created by Frank Hon on 2019/4/14 9:12 PM.
 * E-mail: frank_hon@foxmail.com
 */
class ChartActivity:AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chart)

        initView()
    }

    private fun initView(){
        val fragment1= Fragment1()
        val fragment2=Fragment2()
        val fragment3=Fragment3()

        val fragments= listOf(fragment1,fragment2,fragment3)

        chartViewPager.adapter=ChartViewPagerAdapter(supportFragmentManager,fragments)
    }

    class ChartViewPagerAdapter(fm: FragmentManager,val fragments:List<Fragment>) : FragmentPagerAdapter(fm) {
        override fun getItem(position: Int): Fragment {
            return fragments[position]
        }

        override fun getCount(): Int {
            return fragments.size
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            return super.instantiateItem(container, position)
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            super.destroyItem(container, position, `object`)
        }

    }
}