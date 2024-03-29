package com.hon.librarytest02.customview

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hon.librarytest02.R
import com.hon.librarytest02.customview.drag.DragFragment
import com.hon.librarytest02.customview.scene.SceneFragment

/**
 * Created by Frank Hon on 2022/7/18 7:24 下午.
 * E-mail: frank_hon@foxmail.com
 */
const val TAG_SLIDING_UP = "TAG_SLIDING_UP"
const val TAG_AUDIO_CONTROL = "TAG_AUDIO_CONTROL"
const val TAG_PAGING = "TAG_PAGING"
const val TAG_LYRICS = "TAG_LYRICS"
const val TAG_STAGGERED_GRID = "TAG_STAGGERED_GRID"
const val TAG_DRAG_LAYOUT = "TAG_DRAG_LAYOUT"
const val TAG_PILE_LAYOUT = "TAG_PILE_LAYOUT"

class CustomViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_view)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, CustomViewMainFragment())
                .commit()
        }
    }

    fun navigateTo(tag: String) {
        val fragment = when (tag) {
            TAG_SLIDING_UP -> SlidingUpPanelFragment()
            TAG_AUDIO_CONTROL -> CustomViewFragment()
            TAG_PAGING -> PagingFragment()
            TAG_LYRICS -> LyricsFragment()
            TAG_STAGGERED_GRID -> StaggeredGridContainerFragment()
            TAG_DRAG_LAYOUT -> DragFragment()
            TAG_PILE_LAYOUT -> SceneFragment()
            else -> null
        }
        fragment?.let {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, it)
                .addToBackStack(null)
                .commit()
        }
    }
}