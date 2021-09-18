package com.hon.librarytest02.looprecyclerview

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hon.librarytest02.R
import com.hon.librarytest02.util.Util

/**
 * Created by Frank Hon on 2021/2/21 6:05 PM.
 * E-mail: frank_hon@foxmail.com
 */
class AudioBookAdapter(private val dataList: ArrayList<String>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val NORMAL_TYPE = 0
    private val SPECIAL_TYPE = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            SPECIAL_TYPE ->
                SpecialViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.audio_news_entry, parent, false))
            NORMAL_TYPE ->
                NormalViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_normal, parent, false))
            else ->
                NormalViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_normal, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is NormalViewHolder) {
            holder.bindView("$position, Hello, Joey Tribiani")
        } else if (holder is SpecialViewHolder) {
            holder.bindView()
        }
    }

    override fun getItemCount(): Int {
        return dataList.size + 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 2) {
            SPECIAL_TYPE
        } else {
            NORMAL_TYPE
        }
    }

    private class NormalViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val text: TextView = view.findViewById(R.id.normal_text)

        fun bindView(desc: String) {
            text.text = desc
        }

    }

    @SuppressLint("ClickableViewAccessibility")
    private class SpecialViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val newsList: RecyclerView = view.findViewById(R.id.audio_news_list)
        var isTouched = false

        init {
            newsList.adapter = AudioNewsAdapter(
                    arrayListOf(
                            "1, 世间美好与你环环相扣",
                            "2, 世间美好与你环环相扣",
                            "3, 世间美好与你环环相扣",
                            "4, 世间美好与你环环相扣",
                            "5, 世间美好与你环环相扣",
                            "6, 世间美好与你环环相扣",
                            "7, 世间美好与你环环相扣",
                            "8, 世间美好与你环环相扣"
                    )
            )
            newsList.layoutManager = LinearLayoutManager(itemView.context)
            loopAudioNewsItem(newsList)
            newsList.setOnTouchListener { v, event ->
                Log.d("frankhon", ": ${event.action}")
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> isTouched = true
                    MotionEvent.ACTION_UP -> isTouched = false
                    MotionEvent.ACTION_CANCEL -> isTouched = false
                }
                false
            }

        }

        fun bindView() {

        }

        private fun loopAudioNewsItem(view: RecyclerView) {
            view.postDelayed(
                    {
                        if (!isTouched) {
                            view.smoothScrollBy(0, view.height, LinearInterpolator(), 500)
                        }
                        loopAudioNewsItem(view)
                    }, 5000
            )
        }

        private class AudioNewsAdapter(val audioNewsList: ArrayList<String>) : RecyclerView.Adapter<AudioNewsViewHolder>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AudioNewsViewHolder {
                return AudioNewsViewHolder(
                        LayoutInflater.from(parent.context).inflate(R.layout.audio_news_card_item, parent, false)
                )
            }

            override fun onBindViewHolder(holder: AudioNewsViewHolder, position: Int) {
                holder.bindView(audioNewsList[position % audioNewsList.size])
            }

            override fun getItemCount(): Int {
                return Int.MAX_VALUE
            }

        }

        private class AudioNewsViewHolder(view: View) : RecyclerView.ViewHolder(view) {

            val titleText: TextView = view.findViewById(R.id.item_title)

            fun bindView(title: String) {
                titleText.text = title
            }

        }
    }
}