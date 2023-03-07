package com.hon.librarytest02.util

import android.content.res.Resources
import android.widget.Toast
import com.hon.librarytest02.LibraryTest
import com.hon.mylogger.MyLogger
import java.text.SimpleDateFormat
import java.util.*

fun showToast(content: String) {
    Toast.makeText(
        LibraryTest.sContext,
        content,
        Toast.LENGTH_SHORT
    ).show()
}

val metrics = Resources.getSystem().displayMetrics!!

val screenWidth = metrics.widthPixels
val screenHeight = metrics.heightPixels

fun msToMMSS(millis: Long): String {
    try {
        val sdf = SimpleDateFormat("mm:ss", Locale.CHINA)
        return sdf.format(millis)
    } catch (e: IllegalArgumentException) {
        MyLogger.e(e)
    }
    return ""
}