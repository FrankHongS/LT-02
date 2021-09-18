package com.hon.librarytest02.fixedtopbar

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hon.librarytest02.R
import kotlinx.android.synthetic.main.activity_fixed_top_bar.*

/**
 * Created by shuaihua on 2021/5/8 7:49 PM
 * Email: shuaihua@staff.sina.com.cn
 */
class FixedTopBarActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fixed_top_bar)

        btn_material.setOnClickListener {
            startActivity(Intent(this, MaterialFixedTopBarActivity::class.java))
        }
    }

}