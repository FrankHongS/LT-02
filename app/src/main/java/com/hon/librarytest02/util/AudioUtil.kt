package com.hon.librarytest02.util

import android.media.MediaExtractor
import android.media.MediaFormat
import android.os.Build
import android.os.Environment
import androidx.annotation.RequiresApi
import com.hon.librarytest02.LibraryTest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer

/**
 * Created by shuaihua_a on 2023/3/3 12:08.
 * E-mail: hongshuaihua
 */

private const val BUFFER_SIZE = 200 * 1024

/**
 * @param start start time, unit ms
 * @param end end time, unit ms
 */
@RequiresApi(Build.VERSION_CODES.N)
suspend fun clipAudio(
    fileName: String,
    dirType: String,
    resultFileName: String,
    start: Long,
    end: Long
): Result<File> {
    return withContext(Dispatchers.IO) {
        var bos: BufferedOutputStream? = null
        val extractor = MediaExtractor()
        val afd = appContext.assets.openFd(fileName)
        val realStart = start * 1000
        val realEnd = end * 1000
        try {
            extractor.setDataSource(afd)
            var trackIndex = -1
            for (i in 0 until extractor.trackCount) {
                val format = extractor.getTrackFormat(i)
                val mime = format.getString(MediaFormat.KEY_MIME)
                if (mime?.startsWith("audio") == true) {
                    trackIndex = i
                    break
                }
            }
            if (trackIndex < 0) {
                return@withContext Result.failure(RuntimeException("File not invalid"))
            }
            extractor.selectTrack(trackIndex)
            extractor.seekTo(realStart, MediaExtractor.SEEK_TO_PREVIOUS_SYNC)
            val resultFile = File(appContext.getExternalFilesDir(dirType), resultFileName)
            bos = BufferedOutputStream(FileOutputStream(resultFile), BUFFER_SIZE)
            val bufferArray = ByteArray(BUFFER_SIZE)
            val buffer = ByteBuffer.allocate(BUFFER_SIZE)
            while (true) {
                val sampleSize = extractor.readSampleData(buffer, 0)
                val timeStamp = extractor.sampleTime
                if (timeStamp - realEnd >= 1_000_000 || sampleSize <= 0) {
                    break
                }
                buffer.get(bufferArray, 0, sampleSize)
                bos.write(bufferArray, 0, sampleSize)
                extractor.advance()
            }
            return@withContext Result.success(resultFile)
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext Result.failure(e)
        } finally {
            bos?.close()
            extractor.release()
            afd.close()
        }
    }
}