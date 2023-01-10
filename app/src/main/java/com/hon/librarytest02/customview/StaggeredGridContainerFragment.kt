package com.hon.librarytest02.customview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.hon.librarytest02.R
import com.hon.librarytest02.util.dp

/**
 * Created by shuaihua_a on 2023/1/10 10:03.
 * E-mail: hongshuaihua
 */
class StaggeredGridContainerFragment : Fragment() {

    private lateinit var viewPager: ViewPager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_staggered_grid_container, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initView(view)
    }

    private fun initView(view: View) {
        viewPager = view.findViewById(R.id.vp_staggered_grid)

        viewPager.let {
            it.adapter = StaggeredFragmentPagerAdapter(childFragmentManager)
            it.pageMargin = 30.dp.toInt()
        }
    }

}