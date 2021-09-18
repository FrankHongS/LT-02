package com.hon.librarytest02.floatingbtn

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.graphics.Shader
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.view.isVisible
import androidx.core.view.marginRight
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.hon.librarytest02.R
import com.hon.librarytest02.util.Util
import kotlinx.android.synthetic.main.activity_floating_btn.*

/**
 * Created by shuaihua on 2021/8/18 3:26 下午
 * Email: shuaihua@staff.sina.com.cn
 */
class FloatingBtnActivity : AppCompatActivity() {

    private var isExpand = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_floating_btn)

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
        iv_avatar.setOnClickListener {
            isExpand = !isExpand
            if (isExpand) {
                fl_container.show()
            } else {
                fl_container.hide(Util.dp2px(5f))
            }
        }
    }
}