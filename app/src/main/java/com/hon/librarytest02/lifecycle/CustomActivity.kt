package com.hon.librarytest02.lifecycle

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry

/**
 * Created by Frank_Hon on 5/28/2019.
 * E-mail: v-shhong@microsoft.com
 */
@SuppressLint("Registered")
class CustomActivity :Activity(),LifecycleOwner{

    private lateinit var lifecycleRegistry: LifecycleRegistry

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleRegistry= LifecycleRegistry(this)
        lifecycleRegistry.currentState=Lifecycle.State.CREATED
    }

    override fun onStart() {
        super.onStart()
        lifecycleRegistry.currentState=Lifecycle.State.STARTED
    }

    override fun getLifecycle(): Lifecycle {
        return lifecycleRegistry
    }
}