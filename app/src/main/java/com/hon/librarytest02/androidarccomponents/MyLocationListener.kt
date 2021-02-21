package com.hon.librarytest02.androidarccomponents

import android.content.Context
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent

/**
 * Created by Frank_Hon on 5/28/2019.
 * E-mail: v-shhong@microsoft.com
 */
class MyLocationListener(
        private val context: Context,
        private val lifecycle: Lifecycle,
        private val callback: (String) -> Unit
) : LifecycleObserver {

    init {
        lifecycle.addObserver(this)
    }

    private var enable = false

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun start() {
        Log.d("hon", "start")
        if (enable) {
            // connect
        }
    }

    fun enable() {
        Log.d("hon", "enable")
        enable = true
        if (lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
            // connect if not connected
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun stop() {
        Log.d("hon", "stop")
        //disconnect if connected
    }

}