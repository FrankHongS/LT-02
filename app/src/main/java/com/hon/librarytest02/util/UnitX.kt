@file: JvmName("UnitX")

package com.hon.librarytest02.util

import android.content.res.Resources
import android.util.TypedValue
import android.widget.Toast
import com.hon.librarytest02.LibraryTest

private val metrics = Resources.getSystem().displayMetrics

val <T : Number> T.dp
    @JvmName("dpToPx")
    get() = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, toFloat(), metrics)

val <T : Number> T.sp
    @JvmName("spToPx")
    get() = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, toFloat(), metrics)

val <T : Number> T.pt
    @JvmName("ptToPx")
    get() = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PT, toFloat(), metrics)

fun showToast(content: String) {
    Toast.makeText(
        LibraryTest.sContext,
        content,
        Toast.LENGTH_SHORT
    )
        .show()
}