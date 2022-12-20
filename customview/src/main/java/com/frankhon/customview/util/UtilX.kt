package com.frankhon.customview.util

import android.content.Context
import android.content.res.Resources
import android.view.View

/**
 * Created by Frank Hon on 2022/12/2 11:20 上午.
 * E-mail: frank_hon@foxmail.com
 */

internal val displayMetrics = Resources.getSystem().displayMetrics

internal fun dp2px(dp: Int): Float {
    val density = displayMetrics.density
    return dp * density + 0.5f
}

val Int.dp
    get() = dp2px(this)