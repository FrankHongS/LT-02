package com.hon.librarytest02.customview

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.frankhon.customview.view.RangeSeekBar
import com.hon.librarytest02.R
import com.hon.librarytest02.customview.view.AudioControlButton

/**
 * Created by Frank Hon on 2022/8/11 1:01 下午.
 * E-mail: frank_hon@foxmail.com
 */
class CustomViewFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_audio_control, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val rangeSeekBar = view.findViewById<RangeSeekBar>(R.id.rsb)
        rangeSeekBar.init(10, 50)
        rangeSeekBar.setOnTrackingTouchListener(object : RangeSeekBar.OnTrackingTouchListener {
            override fun onStartTrackingTouch(thumb: RangeSeekBar.Thumb, start: Int, end: Int) {
                Log.d("frankhon", "onStartTrackingTouch: $thumb $start $end")
            }

            override fun onProgressChanged(thumb: RangeSeekBar.Thumb, start: Int, end: Int) {
                Log.d("frankhon", "onProgressChanged: $thumb $start $end")
            }

            override fun onStopTrackingTouch(thumb: RangeSeekBar.Thumb, start: Int, end: Int) {
                Log.d("frankhon", "onStopTrackingTouch: $thumb $start $end")
            }
        })
    }
}