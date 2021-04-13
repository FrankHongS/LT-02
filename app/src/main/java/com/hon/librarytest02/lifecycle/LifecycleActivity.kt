package com.hon.librarytest02.lifecycle

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.hon.librarytest02.R

/**
 * Created by Frank_Hon on 5/28/2019.
 * E-mail: v-shhong@microsoft.com
 */
class LifecycleActivity : AppCompatActivity() {

    private lateinit var myLocationListener: MyLocationListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lifecycle)

        myLocationListener = MyLocationListener(this, lifecycle) { position ->
            run {
                Log.d("hon", position)
            }
        }

        Handler(Looper.getMainLooper())
                .postDelayed(
                        { myLocationListener.enable() }, 3000
                )
    }

}