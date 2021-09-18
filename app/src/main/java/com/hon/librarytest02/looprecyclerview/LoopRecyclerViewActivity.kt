package com.hon.librarytest02.looprecyclerview

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.hon.librarytest02.R
import kotlinx.android.synthetic.main.activity_loop_recyclerview.*

/**
 * Created by Frank Hon on 2021/2/21 5:44 PM.
 * E-mail: frank_hon@foxmail.com
 */
class LoopRecyclerViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loop_recyclerview)
        val data = arrayListOf(
                "", "", "", "", "", "", "",
                "", "", "", "", "", "",
                "", "", "", "", "", ""
        )
        itemList.adapter = AudioBookAdapter(data)
        itemList.layoutManager = LinearLayoutManager(this)
    }

}