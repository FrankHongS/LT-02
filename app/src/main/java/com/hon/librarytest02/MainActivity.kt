package com.hon.librarytest02

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.frankhon.launchmodetest.LaunchModeActivity
import com.hon.librarytest02.audiomessage.AudioMessageActivity
import com.hon.librarytest02.camera.camerax.CameraShootingPage
import com.hon.librarytest02.chart.ChartActivity
import com.hon.librarytest02.downloader.DownloadActivity
import com.hon.librarytest02.glide.GlideActivity
import com.hon.librarytest02.gridview.GridViewActivity
import com.hon.librarytest02.jobschedule.JobSchedulerActivity
import com.hon.librarytest02.lifecycle.LifecycleActivity
import com.hon.librarytest02.ndk.NDKTestActivity
import com.hon.librarytest02.saveInstance.SaveInstanceActivity
import com.hon.librarytest02.searchview.SearchViewActivity
import com.hon.librarytest02.service.ServiceActivity
import com.hon.librarytest02.spider.SpiderActivity
import com.hon.librarytest02.text.TextActivity
import com.hon.librarytest02.timelineview.TimelineViewActivity
import com.hon.librarytest02.transition.TransitionActivity
import com.hon.librarytest02.watchstock.WatchStockActivity
import com.hon.librarytest02.webview.WebActivity
import com.hon.librarytest02.workmanager.WorkManagerActivity

class MainActivity : AppCompatActivity() {

    private var recyclerView: RecyclerView? = null

    private var titles = arrayOf("WorkManager", "Service", "Job Scheduler", "ButterKnife",
            "WebView", "Timeline View", "Audio Message", "Text", "ChartView",
            "Spider", "Stock", "Lifecycle", "Save Instance", "Transition",
            "Launch Mode", "StaggerGridView", "SearchView", "Download",
            "Camera", "Glide", "NDK")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.rv_main)
        recyclerView?.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        recyclerView?.layoutManager = LinearLayoutManager(this)
        recyclerView?.adapter = MainAdapter(this, titles, object : MainAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                when (position) {
                    0 -> navigate(WorkManagerActivity::class.java)
                    1 -> navigate(ServiceActivity::class.java)
                    2 -> navigate(JobSchedulerActivity::class.java)
                    3 -> navigate(Test02Activity::class.java)
                    4 -> navigate(WebActivity::class.java)
                    5 -> navigate(TimelineViewActivity::class.java)
                    6 -> navigate(AudioMessageActivity::class.java)
                    7 -> navigate(TextActivity::class.java)
                    8 -> navigate(ChartActivity::class.java)
                    9 -> navigate(SpiderActivity::class.java)
                    10 -> navigate(WatchStockActivity::class.java)
                    11 -> navigate(LifecycleActivity::class.java)
                    12 -> navigate(SaveInstanceActivity::class.java)
                    13 -> navigate(TransitionActivity::class.java)
                    14 -> navigate(LaunchModeActivity::class.java)
                    15 -> navigate(GridViewActivity::class.java)
                    16 -> navigate(SearchViewActivity::class.java)
                    17 -> navigate(DownloadActivity::class.java)
                    18 -> navigate(CameraShootingPage::class.java)
                    19 -> navigate(GlideActivity::class.java)
                    20 -> navigate(NDKTestActivity::class.java)
                }
            }
        })
    }

    fun navigate(target: Class<out Activity>) {
        startActivity(Intent(this@MainActivity, target))
    }

    fun onClick(view: View) {

        val intent = Intent(this, WorkManagerActivity::class.java)

        startActivity(intent)
    }

    class MainAdapter(val context: Context, val titleList: Array<String>, val onItemClickListener: OnItemClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return MainViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_main_item, parent, false))
        }

        override fun getItemCount(): Int {
            return titleList.size
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val mainViewHolder: MainViewHolder = holder as MainViewHolder
            mainViewHolder.bindView(titleList[position], onItemClickListener, position)
        }

        interface OnItemClickListener {
            fun onItemClick(position: Int)
        }

        class MainViewHolder(item: View) : RecyclerView.ViewHolder(item) {

            fun bindView(titleStr: String, onItemClickListener: OnItemClickListener, position: Int) {
                val title: TextView = itemView.findViewById(R.id.tv_title)
                title.text = titleStr
                itemView.setOnClickListener {
                    onItemClickListener.onItemClick(position)
                }
            }
        }
    }
}
