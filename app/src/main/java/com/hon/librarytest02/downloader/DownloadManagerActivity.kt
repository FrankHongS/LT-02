package com.hon.librarytest02.downloader

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.hon.librarytest02.R

/**
 * Created by Frank Hon on 2022/10/8 11:07 下午.
 * E-mail: frank_hon@foxmail.com
 */
class DownloadManagerActivity : AppCompatActivity() {

    private val SONG_URL =
//        "https://freetyst.nf.migu.cn/public%2Fproduct9th%2Fproduct46%2F2022%2F09%2F0517%2F2022%E5%B9%B409%E6%9C%8804%E6%97%A523%E7%82%B938%E5%88%86%E7%B4%A7%E6%80%A5%E5%86%85%E5%AE%B9%E5%87%86%E5%85%A5%E5%98%89%E7%BE%8E%E4%B9%B0%E6%96%AD4248%E9%A6%96816445%2F%E5%85%A8%E6%9B%B2%E8%AF%95%E5%90%AC%2FMp3_64_22_16%2F69002607532172909.mp3?Key=5baf8b99b7f6c6db&Tim=1665230731998&channelid=01&msisdn=c22f17c2e8c94305aa7033e9c72635be"
        "https://dldir1.qq.com/qqfile/QQIntl/QQi_wireless/Android/qqi_5.0.10.6046_android_office.apk"
    private var downloadId = 0L

    private val receiver = object : BroadcastReceiver() {
        @SuppressLint("Range")
        override fun onReceive(context: Context, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if (downloadId == id) {
                Toast.makeText(context, "下载完成", Toast.LENGTH_SHORT).show()

                val cursor = dm.query(DownloadManager.Query().setFilterById(downloadId))
                if (cursor.moveToFirst()) {
                    val storedUri =
                        cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI))
                    Log.d("frankhon", "storedUri: $storedUri")
                }
            }
        }
    }
    private val dm by lazy {
        getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    }

    @SuppressLint("Range")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_download_manager)
        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        findViewById<Button>(R.id.button).setOnClickListener {
            val request = DownloadManager.Request(
                Uri.parse(SONG_URL)
            )
                .setTitle("hello")
                .setDescription("Downloading...")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)
                .setDestinationInExternalFilesDir(
                    this,
                    Environment.DIRECTORY_MUSIC,
                    "my_test_songs.mp3"
                )
            downloadId = dm.enqueue(request)

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }

}