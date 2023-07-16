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
        view.findViewById<Button>(R.id.btn_sliding_up).setOnClickListener {
            parentActivity?.navigateTo(TAG_SLIDING_UP)
        }
        view.findViewById<Button>(R.id.btn_audio_control).setOnClickListener {
            parentActivity?.navigateTo(TAG_AUDIO_CONTROL)
        }
        view.findViewById<Button>(R.id.btn_paging).setOnClickListener {
            parentActivity?.navigateTo(TAG_PAGING)
        }
        view.findViewById<Button>(R.id.btn_lyrics).setOnClickListener {
            parentActivity?.navigateTo(TAG_LYRICS)
        }
        view.findViewById<Button>(R.id.btn_staggered_grid).setOnClickListener {
            parentActivity?.navigateTo(TAG_STAGGERED_GRID)
        }
        view.findViewById<Button>(R.id.btn_drag_layout).setOnClickListener {
            parentActivity?.navigateTo(TAG_DRAG_LAYOUT)
        }
        view.findViewById<Button>(R.id.btn_pile_layout).setOnClickListener {
            parentActivity?.navigateTo(TAG_PILE_LAYOUT)
        }
    }

}