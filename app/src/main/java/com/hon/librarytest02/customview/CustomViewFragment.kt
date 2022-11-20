package com.hon.librarytest02.customview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
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
        val btn = view.findViewById<AudioControlButton>(R.id.btn_audio_control_2)
    }
}