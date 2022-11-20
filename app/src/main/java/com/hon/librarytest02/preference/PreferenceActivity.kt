package com.hon.librarytest02.preference

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hon.librarytest02.R

/**
 * Created by Frank Hon on 2022/9/20 10:33 上午.
 * E-mail: frank_hon@foxmail.com
 */
class PreferenceActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preference)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.container, DemoFragment())
                .commit()
        }
    }

}