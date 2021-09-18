package com.hon.librarytest02.floatingbtn

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.hon.librarytest02.R
import com.hon.librarytest02.util.Util
import kotlinx.android.synthetic.main.activity_floating_button.*
import kotlinx.android.synthetic.main.layout_floating_button.*

/**
 * Created by shuaihua on 2021/8/18 3:26 下午
 * Email: shuaihua@staff.sina.com.cn
 */
class FloatingBtnActivity : AppCompatActivity() {

    private var isExpand = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_floating_button)

        // 圆形Image
        // 方式1
//        Shader
        // 方式2
//        static {
//            CIRCLE_CROP_BITMAP_PAINT = new Paint(CIRCLE_CROP_PAINT_FLAGS);
//            CIRCLE_CROP_BITMAP_PAINT.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
//        }

        Glide.with(this)
            .load("https://w.wallhaven.cc/full/5w/wallhaven-5we787.jpg")
//            .apply(RequestOptions().optionalTransform(CircleCrop()))
            .into(iv_avatar)
        iv_avatar.startUpdateProgress(25)
        btn_control.setOnClickListener {
            isExpand = !isExpand
            if (isExpand) {
                floating_button_container.show()
            } else {
                floating_button_container.hide(Util.dp2px(5f))
            }
        }
        ViewCompat.setElevation(floating_button_container, Util.dp2px(8f).toFloat())
    }
}