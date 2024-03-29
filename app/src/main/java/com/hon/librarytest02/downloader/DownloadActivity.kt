package com.hon.librarytest02.downloader

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hon.librarytest02.R
import kotlinx.android.synthetic.main.activity_download.*
import java.io.File

/**
 * Created by Frank Hon on 2020/7/12 10:38 PM.
 * E-mail: frank_hon@foxmail.com
 */
class DownloadActivity() : AppCompatActivity() {

    private val url = arrayListOf(
            "http://tyst.migu.cn/public%2Fproduct5th%2Fproduct34%2F2019%2F07%2F1822%2F2009%E5%B9%B406%E6%9C%8826%E6%97%A5%E5%8D%9A%E5%B0%94%E6%99%AE%E6%96%AF%2F%E5%85%A8%E6%9B%B2%E8%AF%95%E5%90%AC%2FMp3_64_22_16%2F60054701923.mp3",
            "http://mcontent.migu.cn/newlv2/new/album/20191031/8592/s_cNhRDwG7QFLdfm6e.jpg",
            "https://dldir1.qq.com/qqfile/QQIntl/QQi_wireless/Android/qqi_5.0.10.6046_android_office.apk",
            "https://dlied4.myapp.com/myapp/1104466820/cos.release-40109/2017_com.tencent.tmgp.sgame_h836289_1.54.1.4_40tpe2.apk"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_download)
        val downloadUtils =    DownloadUtils(this)
        btn_download.setOnClickListener {
//            val index = et_download_index.text.toString().let {
//                if (it.isEmpty()) {
//                    0
//                } else {
//                    it.toInt()
//                }
//            }
//            val threadCount = et_thread_count.text.toString().let {
//                if (it.isEmpty()) {
//                    6
//                } else {
//                    it.toInt()
//                }
//            }
//            val cur = System.currentTimeMillis()
//            val file = createFile()
//            val sliceDownloader = SliceDownloader(
//                    file,
//                    url[index],
//                    AppExecutors.getInstance().networkIO,
//                    object : SliceDownloader.Callback {
//                        override fun onSuccess() {
//                            runOnUiThread {
//                                val delta = System.currentTimeMillis() - cur
//                                tv_message.text = "下载成功: $delta"
////                                Glide.with(applicationContext)
////                                        .load(file)
////                                        .into(iv_test)
//                            }
//                        }
//
//                        override fun onError(e: Exception) {
//                            e.printStackTrace()
//                            runOnUiThread {
//                                tv_message.text = "下载失败: ${e.message}"
//                            }
//                        }
//                    },
//                    threadCount = threadCount
//            )
//            AppExecutors.getInstance()
//                    .networkIO
//                    .execute(sliceDownloader)

            downloadUtils.downloadAPK(url[2], "QQ.apk");
        }
    }

    private fun createFile(): File {
        val dir = filesDir.absolutePath + File.separator + "download_test"
        val dirFile = File(dir)
        if (!dirFile.exists()) {
            dirFile.mkdirs()
        }
        val fileName = System.currentTimeMillis().toString()
        return File(dirFile, "ds/$fileName.tmp")
    }
}