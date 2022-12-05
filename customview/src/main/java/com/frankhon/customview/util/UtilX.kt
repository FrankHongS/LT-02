package com.frankhon.customview.util

import android.content.Context
import android.view.View

/**
 * Created by Frank Hon on 2022/12/2 11:20 上午.
 * E-mail: frank_hon@foxmail.com
 */

internal fun Context.dp2px(dp: Int): Float {
    val density = resources.displayMetrics.density
    return dp * density + 0.5f
}

internal fun View.dp2px(dp: Int): Float {
    val density = resources.displayMetrics.density
    return dp * density + 0.5f
}