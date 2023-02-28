package com.hon.librarytest02

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.*
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.frankhon.launchmodetest.LaunchModeActivity
import com.hon.librarytest02.audiomessage.AudioMessageActivity
import com.hon.librarytest02.camera.camerax.CameraShootingPage
import com.hon.librarytest02.chart.ChartActivity
import com.hon.librarytest02.coroutine.AsyncActivity
import com.hon.librarytest02.customview.CustomViewActivity
import com.hon.librarytest02.dialog.DialogActivity
import com.hon.librarytest02.downloader.DownloadManagerActivity
import com.hon.librarytest02.fixedtopbar.FixedTopBarActivity
import com.hon.librarytest02.floatingbtn.FloatingBtnActivity
import com.hon.librarytest02.glide.GlideActivity
import com.hon.librarytest02.gridview.GridViewActivity
import com.hon.librarytest02.jobschedule.JobSchedulerActivity
import com.hon.librarytest02.lifecycle.LifecycleActivity
import com.hon.librarytest02.media.MediaActivity
import com.hon.librarytest02.ndk.NDKTestActivity
import com.hon.librarytest02.preference.PreferenceActivity
import com.hon.librarytest02.saveInstance.SaveInstanceActivity
import com.hon.librarytest02.searchview.SearchViewActivity
import com.hon.librarytest02.service.ServiceActivity
import com.hon.librarytest02.spider.SpiderActivity
import com.hon.librarytest02.text.TextActivity
import com.hon.librarytest02.timelineview.TimelineViewActivity
import com.hon.librarytest02.transition.TransitionActivity
import com.hon.librarytest02.util.screenHeight
import com.hon.librarytest02.viewmoretext.ViewMoreActivity
import com.hon.librarytest02.watchstock.WatchStockActivity
import com.hon.librarytest02.webview.WebActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private var titles = arrayOf(
        "Custom View", "Preference",
        "WorkManager", "Service", "Job Scheduler", "ButterKnife",
        "Floating Button", "Media",
        "WebView", "Timeline View", "Audio Message", "Text", "ChartView",
        "Spider", "Stock", "Lifecycle", "Save Instance", "Transition",
        "Launch Mode", "StaggerGridView", "View More Text", "SearchView", "Downloader",
        "Camera", "Glide", "NDK", "Coroutine", "Fixed Top Bar", "Dialog"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rv_main.let {
            it.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
            it.layoutManager = LinearLayoutManager(this)
            it.layoutAnimation = getLayoutAnimationController()
            it.adapter = MainAdapter(titles) { _, title ->
                when (title) {
                    "Media" -> navigate<MediaActivity>()
                    "Custom View" -> navigate<CustomViewActivity>()
                    "Floating Button" -> navigate<FloatingBtnActivity>()
                    "Preference" -> navigate<PreferenceActivity>()
                    "Service" -> navigate<ServiceActivity>()
                    "Job Scheduler" -> navigate<JobSchedulerActivity>()
                    "ButterKnife" -> navigate<Test02Activity>()
                    "WebView" -> navigate<WebActivity>()
                    "Timeline View" -> navigate<TimelineViewActivity>()
                    "Audio Message" -> navigate<AudioMessageActivity>()
                    "Text" -> navigate<TextActivity>()
                    "ChartView" -> navigate<ChartActivity>()
                    "Spider" -> navigate<SpiderActivity>()
                    "Stock" -> navigate<WatchStockActivity>()
                    "Lifecycle" -> navigate<LifecycleActivity>()
                    "Save Instance" -> navigate<SaveInstanceActivity>()
                    "Transition" -> navigate<TransitionActivity>()
                    "Launch Mode" -> navigate<LaunchModeActivity>()
                    "StaggerGridView" -> navigate<GridViewActivity>()
                    "View More Text" -> navigate<ViewMoreActivity>()
                    "SearchView" -> navigate<SearchViewActivity>()
                    "Downloader" -> navigate<DownloadManagerActivity>()
                    "Camera" -> navigate<CameraShootingPage>()
                    "Glide" -> navigate<GlideActivity>()
                    "NDK" -> navigate<NDKTestActivity>()
                    "Coroutine" -> navigate<AsyncActivity>()
                    "Fixed Top Bar" -> navigate<FixedTopBarActivity>()
                    "Dialog" -> navigate<DialogActivity>()
                }
            }
        }
    }

    private inline fun <reified T : Activity> navigate() {
        startActivity(Intent(this@MainActivity, T::class.java))
    }

    private fun getLayoutAnimationController(): LayoutAnimationController {
        val set = AnimationSet(true)

        // Fade in animation
        val fadeIn = AlphaAnimation(0.0f, 1.0f).apply {
            duration = 400
            fillAfter = true
        }
        set.addAnimation(fadeIn)

        // Slide up animation from bottom of screen
        val slideUp = TranslateAnimation(
            0f, 0f,
            screenHeight.toFloat(), 0f
        )
            .apply {
                interpolator = DecelerateInterpolator(2f)
                duration = 600
            }
        set.addAnimation(slideUp)

        // Set up the animation controller , (second parameter is the delay)
        return LayoutAnimationController(set, 0.2f)
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
