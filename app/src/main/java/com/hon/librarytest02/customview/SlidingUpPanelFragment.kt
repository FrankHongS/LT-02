package com.hon.librarytest02.customview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.hon.librarytest02.R

/**
 * Created by Frank Hon on 2022/7/18 7:39 下午.
 * E-mail: frank_hon@foxmail.com
 */
class SlidingUpPanelFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sliding_up, container, false)
    }

}