package com.hon.librarytest02.viewmoretext

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hon.librarytest02.R
import kotlinx.android.synthetic.main.activity_viewmore.*

/**
 * Created by Frank Hon on 2021/11/7 6:17 下午.
 * E-mail: frank_hon@foxmail.com
 */
class ViewMoreActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_viewmore)

        viewMore
            .setAnimationDuration(500)
            .setEllipsizedText("展开" to "")
            .setVisibleLines(3)
            .setEllipsizedTextColor(Color.parseColor("blue"))

        viewMore.text =
            "在 2019 年 Google I/O 大会上，我们宣布今后将优先采用 Kotlin 进行 Android 开发，" +
                    "并且也坚守了这一承诺。Kotlin 是一种富有表现力且简洁的编程语言，不仅可以减少" +
                    "常见代码错误，还可以轻松集成到现有应用中。如果您想构建 Android 应用，" +
                    "建议您从 Kotlin 开始着手，充分利用一流的 Kotlin 功能"

        viewMore.setOnClickListener {
            if (!viewMore.isExpand()) {
                viewMore.toggle()
            }
        }
    }

}