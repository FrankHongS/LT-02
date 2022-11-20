package com.hon.librarytest02.customview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.hon.librarytest02.R

/**
 * Created by Frank Hon on 2022/7/18 7:30 下午.
 * E-mail: frank_hon@foxmail.com
 */
class CustomViewMainFragment : Fragment() {

    private lateinit var btn1: Button
    private lateinit var btn2: Button
    private lateinit var btn3: Button

    private val parentActivity by lazy {
        activity as? CustomViewActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_custom_view_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initView(view)
    }

    private fun initView(view: View) {
        btn1 = view.findViewById(R.id.btn_sliding_up)
        btn2 = view.findViewById(R.id.btn_audio_control)
        btn3 = view.findViewById(R.id.btn_paging)

        btn1.setOnClickListener {
            parentActivity?.navigateTo(TAG_SLIDING_UP)
        }
        btn2.setOnClickListener {
            parentActivity?.navigateTo(TAG_AUDIO_CONTROL)
        }

        btn3.setOnClickListener {
            parentActivity?.navigateTo(TAG_PAGING)
        }
    }

}