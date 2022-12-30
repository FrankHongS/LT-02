package com.hon.librarytest02.util

import android.content.res.Resources
import android.widget.Toast
import com.hon.librarytest02.LibraryTest

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

