@file: JvmName("UnitX")

package com.hon.librarytest02.util

import android.util.TypedValue
import com.hon.librarytest02.LibraryTest

val appContext = LibraryTest.sContext

val <T : Number> T.dp
    @JvmName("dpToPx")
    get() = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, toFloat(), metrics)

val <T : Number> T.sp
    @JvmName("spToPx")
    get() = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, toFloat(), metrics)

val <T : Number> T.pt
    @JvmName("ptToPx")
    get() = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PT, toFloat(), metrics)
