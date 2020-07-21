package com.hon.librarytest02.downloader

import com.hon.mylogger.MyLogger
import java.io.BufferedInputStream
import java.io.File
import java.io.InputStream
import java.io.RandomAccessFile
import java.net.HttpURLConnection
import java.net.URL

/**
 * Created by Frank Hon on 2020/7/12 7:51 PM.
 * E-mail: frank_hon@foxmail.com
 */
class UnitDownloader(private val from: Long,
                     private val to: Long,
                     private val target: File,
                     private val uri: String,
                     private val callback: SliceDownloader.Callback) : Runnable {
    init {
        MyLogger.d("from: $from ,to: $to")
    }

    override fun run() {
        var connection: HttpURLConnection? = null
        var inputStream: InputStream? = null
        var randomAccessFile: RandomAccessFile? = null
        try {
            val url = URL(uri)
            connection = url.openConnection() as HttpURLConnection
            //set Request Range
            connection.setRequestProperty("Range", "bytes=$from-$to")
            connection.connect()
            inputStream = BufferedInputStream(connection.inputStream)
            randomAccessFile = RandomAccessFile(target, "rw")
            randomAccessFile.seek(from)
            val buffer = ByteArray(1024 * 1024)
            var count: Int
            while (true) {
                count = inputStream.read(buffer)
                if (count == -1) {
                    break
                }
                randomAccessFile.write(buffer, 0, count)
            }
            callback.onSuccess()
        } catch (e: Exception) {
            callback.onError(e)
            e.printStackTrace()
        } finally {
            connection?.disconnect()
            inputStream?.close()
            randomAccessFile?.close()
        }
    }
}