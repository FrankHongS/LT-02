package com.hon.librarytest02

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.frankhon.launchmodetest.LaunchModeActivity
import com.hon.librarytest02.async.AsyncActivity
import com.hon.librarytest02.audiomessage.AudioMessageActivity
import com.hon.librarytest02.camera.camerax.CameraShootingPage
import com.hon.librarytest02.chart.ChartActivity
import com.hon.librarytest02.dialog.DialogActivity
import com.hon.librarytest02.downloader.DownloadActivity
import com.hon.librarytest02.fixedtopbar.FixedTopBarActivity
import com.hon.librarytest02.floatingbtn.FloatingBtnActivity
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
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var titles = arrayOf(
        "WorkManager", "Service", "Job Scheduler", "ButterKnife",
        "Floating Button",
        "WebView", "Timeline View", "Audio Message", "Text", "ChartView",
        "Spider", "Stock", "Lifecycle", "Save Instance", "Transition",
        "Launch Mode", "StaggerGridView", "SearchView", "Download",
        "Camera", "Glide", "NDK", "Coroutine", "Fixed Top Bar", "Dialog"
    )

    private var isInEditMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rv_main.let {
            it.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
            it.layoutManager = LinearLayoutManager(this)
            it.adapter = MainAdapter(titles) { _, title ->
                when (title) {
                    "Floating Button" -> navigate(FloatingBtnActivity::class.java)
                    "WorkManager" -> navigate(WorkManagerActivity::class.java)
                    "Service" -> navigate(ServiceActivity::class.java)
                    "Job Scheduler" -> navigate(JobSchedulerActivity::class.java)
                    "ButterKnife" -> navigate(Test02Activity::class.java)
                    "WebView" -> navigate(WebActivity::class.java)
                    "Timeline View" -> navigate(TimelineViewActivity::class.java)
                    "Audio Message" -> navigate(AudioMessageActivity::class.java)
                    "Text" -> navigate(TextActivity::class.java)
                    "ChartView" -> navigate(ChartActivity::class.java)
                    "Spider" -> navigate(SpiderActivity::class.java)
                    "Stock" -> navigate(WatchStockActivity::class.java)
                    "Lifecycle" -> navigate(LifecycleActivity::class.java)
                    "Save Instance" -> navigate(SaveInstanceActivity::class.java)
                    "Transition" -> navigate(TransitionActivity::class.java)
                    "Launch Mode" -> navigate(LaunchModeActivity::class.java)
                    "StaggerGridView" -> navigate(GridViewActivity::class.java)
                    "SearchView" -> navigate(SearchViewActivity::class.java)
                    "Download" -> navigate(DownloadActivity::class.java)
                    "Camera" -> navigate(CameraShootingPage::class.java)
                    "Glide" -> navigate(GlideActivity::class.java)
                    "NDK" -> navigate(NDKTestActivity::class.java)
                    "Coroutine" -> navigate(AsyncActivity::class.java)
                    "Fixed Top Bar" -> navigate(FixedTopBarActivity::class.java)
                    "Dialog" -> navigate(DialogActivity::class.java)
                }
            }
        }

        btn_test_kotlin.setOnClickListener {
            isInEditMode = !isInEditMode
            btn_test_kotlin.text = if (isInEditMode) "完成" else "编辑"
        }
    }

    private fun navigate(target: Class<out Activity>) {
        startActivity(Intent(this@MainActivity, target))
    }

    class MainAdapter(
        private val titleList: Array<String>,
        private val onItemClickListener: (Int, String) -> Unit
    ) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return MainViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_simple_text, parent, false)
            )
        }

        override fun getItemCount(): Int {
            return titleList.size
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val mainViewHolder: MainViewHolder = holder as MainViewHolder
            mainViewHolder.bindView(titleList[position], onItemClickListener, position)
        }
    }

    class MainViewHolder(item: View) : RecyclerView.ViewHolder(item) {

        fun bindView(titleStr: String, onItemClickListener: (Int, String) -> Unit, position: Int) {
            val title: TextView = itemView.findViewById(R.id.tv_title)
            title.text = titleStr
            itemView.setOnClickListener {
                onItemClickListener(position, titleStr)
            }
        }
    }
}
