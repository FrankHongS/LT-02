package com.hon.librarytest02.customview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.hon.librarytest02.R
import kotlinx.android.synthetic.main.fragment_lyrics.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Created by Frank Hon on 2022/12/2 11:02 上午.
 * E-mail: frank_hon@foxmail.com
 */
class LyricsFragment : Fragment() {

    private val lyrics = listOf(
        0L to "I'm standing there on a balcony in summer air",
        0L to "最美不是下雨天", 0L to "而是与你躲过雨的屋檐", 0L to "我一路向北",
        30L to "离开了有你的季节",
        40L to "窗外的麻雀", 50L to "在电线杆上多嘴", 60L to "你说这一句很有夏天的感觉",
        70L to "手中的铅笔", 80L to "在纸上来来回回在纸上来来回回在纸上来来回回在纸上来来回回在纸上来来回回", 90L to "我用几行字来形容你是我的谁",
        100L to "秋刀鱼的滋味", 110L to "猫跟你都想了解", 120L to "雨下整夜",
        130L to "我的爱溢出就像雨水",
        135L to "you are my girl, you are my girl, you are my girl, you are my girl, you are my girl",
        140L to "院子落叶", 150L to "跟我的思念厚厚一叠"
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_lyrics, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        lv_lyrics.run {
            lifecycleScope.launch {
                reset()
                delay(510)
                setLyrics(lyrics)
            }
            setOnLyricsClickListener {
                isVisible = false
            }
        }
        btn_test.setOnClickListener {
            lv_lyrics.isVisible = true
        }

        lifecycleScope.launchWhenCreated {
            withContext(Dispatchers.IO) {
                var count = 0
                while (true) {
                    delay(300)
                    count++
                    lv_lyrics?.post { lv_lyrics.updateCurLyricTime(count.toLong()) }
                }
            }
        }

    }

}