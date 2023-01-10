package com.hon.librarytest02.customview

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

/**
 * [androidx.viewpager2.widget.ViewPager2]有bug，当RecyclerView为StaggeredGridLayoutManager时，
 * ViewPager切换Fragment，RecyclerView上方item会自动对齐，改用[androidx.viewpager.widget.ViewPager]
 *
 * Created by shuaihua_a on 2023/1/10 09:57.
 * E-mail: hongshuaihua
 */
class StaggeredFragmentPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(
    fm,
    BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
) {
    override fun getCount(): Int {
        return 2
    }

    override fun getItem(position: Int): Fragment {
        return StaggeredGridFragment()
    }

}