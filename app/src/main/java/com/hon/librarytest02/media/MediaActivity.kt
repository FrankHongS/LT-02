package com.hon.librarytest02.media

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore.Audio
import android.provider.MediaStore.MediaColumns
import android.provider.Settings
import android.webkit.MimeTypeMap
import androidx.activity.result.contract.ActivityResultContract
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.hon.librarytest02.R
import com.hon.librarytest02.util.clipAudio
import com.hon.librarytest02.util.showToast
import kotlinx.android.synthetic.main.activity_media.*
import kotlinx.coroutines.launch
import java.io.*


/**
 * Created by shuaihua_a on 2023/2/9 19:35.
 * E-mail: hongshuaihua
 */
class MediaActivity : AppCompatActivity() {

    private var resultFile: File? = null

    @RequiresApi(Build.VERSION_CODES.M)
    private val launcher =
        registerForActivityResult(object : ActivityResultContract<String, String>() {
            override fun createIntent(context: Context, input: String): Intent {
                return Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS).apply {
                    data = Uri.parse("package:$packageName")
                }
            }

            override fun parseResult(resultCode: Int, intent: Intent?): String {
                return ""
            }
        }) {
            if (Settings.System.canWrite(this)) {
                setAudioAsRingtone()
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media)
        btn_clip.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                lifecycleScope.launch {
                    tv_clip_log.text = "Start clipping..."
                    val result = clipAudio(
                        fileName = "songs/晴天_周杰伦.mp3",
                        dirType = Environment.DIRECTORY_RINGTONES,
                        resultFileName = "fantasy_晴天.mp3",
                        start = 0,
                        end = 60_000
                    )
                    resultFile = result.getOrNull()
                    btn_ringtone.isEnabled = result.isSuccess && result.getOrNull() != null
                    tv_clip_log.text = result.let {
                        if (it.isSuccess) {
                            "Success\nabsolutePath = ${it.getOrNull()?.absolutePath}" +
                                    "\npath = ${it.getOrNull()?.path}"
                        } else {
                            it.exceptionOrNull()?.message
                        }
                    }
                }
            }
        }
        btn_ringtone.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.System.canWrite(this)) {
                    setAudioAsRingtone()
                } else {
                    launcher.launch("")
                }
            } else {
                setAudioAsRingtone()
            }
        }
    }

    private fun setAudioAsRingtone() {
        resultFile?.let { audioFile ->
            val displayName = audioFile.name
            if (isRingtoneExist(displayName)) {
                return
            }
            val mimeType = MimeTypeMap.getSingleton()
                .getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(audioFile.absolutePath))
            val values = ContentValues().apply {
                put(Audio.Media.DISPLAY_NAME, audioFile.name)
                put(Audio.Media.MIME_TYPE, mimeType)
                put(Audio.Media.IS_RINGTONE, true)
                put(Audio.Media.IS_NOTIFICATION, false)
                put(Audio.Media.IS_ALARM, false)
                put(Audio.Media.IS_MUSIC, false)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                setRingtoneApi29(values, audioFile)
            } else {
                setRingtone(values, audioFile)
            }
        }
    }

    @SuppressLint("Range")
    private fun isRingtoneExist(displayName: String): Boolean {
        val mediaCursor: Cursor? = contentResolver
            .query(
                Audio.Media.EXTERNAL_CONTENT_URI,
                arrayOf(
                    Audio.Media.DISPLAY_NAME,
                    Audio.Media._ID
                ),
                Audio.Media.IS_RINGTONE + "!= 0",
                null,
                Audio.Media.DISPLAY_NAME + " ASC"
            )

        mediaCursor?.let {
            while (it.moveToNext()) {
                if (displayName == it.getString(it.getColumnIndex(Audio.Media.DISPLAY_NAME))) {
                    setActualDefaultRingtoneUri(
                        this,
                        Uri.parse(
                            Audio.Media.EXTERNAL_CONTENT_URI.toString() + "/"
                                    + it.getString(it.getColumnIndex(Audio.Media._ID))
                        )
                    )
                    it.close()
                    return true
                }
            }
        }
        return false
    }

    @TargetApi(Build.VERSION_CODES.Q)
    private fun setRingtoneApi29(values: ContentValues, audioFile: File) {
        try {
            values.put(MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_RINGTONES)
            val newUri = contentResolver.insert(Audio.Media.EXTERNAL_CONTENT_URI, values)
            newUri?.let {
                contentResolver.openOutputStream(it)?.use { os ->
                    val size = audioFile.length().toInt()
                    val bytes = ByteArray(size)
                    val buf = BufferedInputStream(FileInputStream(audioFile))
                    buf.read(bytes, 0, bytes.size)
                    buf.close()
                    os.write(bytes)
                    os.close()
                    os.flush()
                }
                setActualDefaultRingtoneUri(this, it)
            }
        } catch (e: Exception) {
            // do nothing
        }
    }

    @Suppress("DEPRECATION")
    private fun setRingtone(values: ContentValues, audioFile: File) {
        values.put(MediaColumns.DATA, audioFile.absolutePath)
        val uri = Audio.Media.getContentUriForPath(audioFile.absolutePath)
        uri?.let {
            contentResolver.delete(
                it,
                MediaColumns.DATA + "=\"" + audioFile.absolutePath + "\"",
                null
            )
            val newUri = contentResolver.insert(it, values)
            RingtoneManager.setActualDefaultRingtoneUri(this, RingtoneManager.TYPE_RINGTONE, newUri)
        }
    }

    private fun setActualDefaultRingtoneUri(context: Context, uri: Uri) {
        RingtoneManager.setActualDefaultRingtoneUri(context, RingtoneManager.TYPE_RINGTONE, uri)
        showToast("Set ringtone successfully")
    }

}