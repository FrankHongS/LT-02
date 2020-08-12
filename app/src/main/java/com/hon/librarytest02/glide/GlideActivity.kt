package com.hon.librarytest02.glide

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hon.librarytest02.R
import kotlinx.android.synthetic.main.activity_glide.*

/**
 * Created by Frank_Hon on 8/11/2020.
 * E-mail: v-shhong@microsoft.com
 */
class GlideActivity : AppCompatActivity() {

    private val imageList = arrayListOf(
            "https://w.wallhaven.cc/full/5w/wallhaven-5we787.jpg",
            "https://w.wallhaven.cc/full/xl/wallhaven-xlq1rv.jpg",
            "https://w.wallhaven.cc/full/2e/wallhaven-2em38y.jpg",
            "https://w.wallhaven.cc/full/dg/wallhaven-dgrgql.jpg",
            "https://w.wallhaven.cc/full/39/wallhaven-3911w9.jpg",
            "https://w.wallhaven.cc/full/dg/wallhaven-dg7y23.jpg",
            "https://w.wallhaven.cc/full/zm/wallhaven-zmm7mw.png",
            "https://w.wallhaven.cc/full/md/wallhaven-mdzdok.jpg",
            "https://w.wallhaven.cc/full/x1/wallhaven-x1wroo.jpg",
            "https://w.wallhaven.cc/full/5w/wallhaven-5w82r1.jpg"
//            ,
//
//            "https://w.wallhaven.cc/full/5w/wallhaven-5we787.jpg",
//            "https://w.wallhaven.cc/full/xl/wallhaven-xlq1rv.jpg",
//            "https://w.wallhaven.cc/full/2e/wallhaven-2em38y.jpg",
//            "https://w.wallhaven.cc/full/dg/wallhaven-dgrgql.jpg",
//            "https://w.wallhaven.cc/full/39/wallhaven-3911w9.jpg",
//            "https://w.wallhaven.cc/full/dg/wallhaven-dg7y23.jpg",
//            "https://w.wallhaven.cc/full/zm/wallhaven-zmm7mw.png",
//            "https://w.wallhaven.cc/full/md/wallhaven-mdzdok.jpg",
//            "https://w.wallhaven.cc/full/x1/wallhaven-x1wroo.jpg",
//            "https://w.wallhaven.cc/full/5w/wallhaven-5w82r1.jpg"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_glide)

        initView()

    }

    private fun initView() {
        val adapter = GlideAdapter(imageList)
        rv_glide.adapter = adapter
        rv_glide.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
    }
}