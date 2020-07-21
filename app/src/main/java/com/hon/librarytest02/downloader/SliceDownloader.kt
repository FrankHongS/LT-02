package com.hon.librarytest02.downloader

import com.hon.mylogger.MyLogger
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executor

/**
 * Created by Frank Hon on 2020/7/12 8:47 PM.
 * E-mail: frank_hon@foxmail.com
 */
class SliceDownloader(private val target: File,
                      private val uri: String,
                      private val executor: Executor,
                      private val downloadCallback: Callback,
                      private val threadCount: Int = 6) : Runnable {

    private val downloaderList = arrayListOf<UnitDownloader>()
    private val unitCallback = object : Callback {
        var successCount: Int = 0
        var errorCount: Int = 0
        override fun onSuccess() {
            successCount++
            if (successCount == threadCount) {
                downloadCallback.onSuccess()
            }
        }

        override fun onError(e: Exception) {
            MyLogger.e(e)
            MyLogger.d(target.length().toString())
            errorCount++
            if (errorCount == 1) {
                downloadCallback.onError(e)
            }
        }
    }

    override fun run() {
        var connection: HttpURLConnection? = null
        try {
            val url = URL(uri)
            connection = url.openConnection() as HttpURLConnection
            connection.connectTimeout = 8 * 1000
            connection.readTimeout = 8 * 1000
            connection.connect()
            val contentLength = connection.contentLength
            MyLogger.d("contentLength: $contentLength")
            val unitLength = contentLength / threadCount
            var from = 0L
            for (i in 1..threadCount) {
                var downloader: UnitDownloader
                if (i == threadCount) {
                    downloader = UnitDownloader(from, contentLength.toLong(), target, uri, unitCallback)
                } else {
                    downloader = UnitDownloader(from, from + unitLength, target, uri, unitCallback)
                    from += unitLength + 1
                }
                downloaderList.add(downloader)
            }
            startSliceDownload()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            connection?.disconnect()
        }
    }

    private fun startSliceDownload() {
        for (downloader in downloaderList) {
            executor.execute(downloader)
        }
    }

    interface Callback {
        fun onSuccess()
        fun onError(e: Exception)
    }
}