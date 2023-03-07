package com.hon.librarytest02.customview

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.frankhon.customview.view.RangeSeekBar
import com.hon.librarytest02.R
import com.hon.librarytest02.util.msToMMSS
import kotlinx.android.synthetic.main.fragment_lyrics.*
import kotlinx.coroutines.*

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
        140L to "院子落叶", 150L to "跟我的思念厚厚一叠",
        155L to "分别总是在九月", 160L to "回忆是思念的愁", 165L to "深秋嫩绿的翠柳",
        170L to "亲吻着我额头", 175L to "在那座阴雨的小城里", 180L to "我从未忘记你",
        185L to "成都 带不走的", 190L to "只有你", 195L to "和我在成都的街头走一走"
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

        rsb.length = 200
        rsb.setOnTrackingTouchListener(object : RangeSeekBar.OnTrackingTouchListener {
            override fun onProgressChanged(thumb: RangeSeekBar.Thumb, start: Int, end: Int) {
                Log.d("frankhon", "onProgressChanged: $thumb ${start} ${end.toLong()}")
                when (thumb) {
                    RangeSeekBar.Thumb.LEFT -> tv_start.text = msToMMSS(start * 1000L)
                    RangeSeekBar.Thumb.RIGHT -> tv_end.text = msToMMSS(end * 1000L)
                    else -> {}
                }
            }

            override fun onStopTrackingTouch(thumb: RangeSeekBar.Thumb, start: Int, end: Int) {
                when (thumb) {
                    RangeSeekBar.Thumb.LEFT -> lv_lyrics.updateCurLyricRangeStart(start.toLong())
                    RangeSeekBar.Thumb.RIGHT -> lv_lyrics.updateCurLyricRangeEnd(end.toLong())
                    else -> {}
                }
            }
        })
//        lifecycleScope.launchWhenCreated {
//            withContext(Dispatchers.IO) {
//                var count = 0
//                while (isActive) {
//                    delay(300)
//                    count++
//                    lv_lyrics?.post { lv_lyrics.updateCurLyricTime(count.toLong()) }
//                }
//            }
//        }

    }

}