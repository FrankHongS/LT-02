package com.hon.librarytest02.snackbar

import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.hon.librarytest02.R
import com.hon.mylogger.MyLogger
import org.json.JSONObject

/**
 * Created by Frank Hon on 2020/12/21 12:04 PM.
 * E-mail: frank_hon@foxmail.com
 */
object SnackBarUtils {

    interface Callback {
        fun onActionClick()
        fun onDismiss()
    }

    fun show(view: View, data: JSONObject?, callback: Callback?) {
        var message: String? = data?.optString("message")
        val period: String? = data?.optString("period")
        val action: String? = data?.optString("action")
        val position: String? = data?.optString("position")
        if (message.isNullOrBlank()) {
            message = data?.optString("msg", data.toString())
        }

        var snackBarLength = Snackbar.LENGTH_SHORT
        if ("long" == period) {
            snackBarLength = Snackbar.LENGTH_LONG
        } else if ("infinite" == period) {
            snackBarLength = Snackbar.LENGTH_INDEFINITE
        }
        val snackBar = Snackbar.make(view, message.toString(), snackBarLength)
        val layout = snackBar.view as Snackbar.SnackbarLayout
        val layoutParams = layout.layoutParams as FrameLayout.LayoutParams
        layoutParams.gravity = when (position) {
            "top" -> Gravity.TOP or Gravity.CENTER_HORIZONTAL
            "center" -> Gravity.CENTER_VERTICAL or Gravity.CENTER_HORIZONTAL
            else -> Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
        }
        layout.layoutParams = layoutParams

        val content = layout.findViewById<TextView>(R.id.snackbar_text)
        content.visibility = View.INVISIBLE

        val snackView = LayoutInflater.from(view.context).inflate(R.layout.layout_snackbar, null)
        // default text style
        val snackBarText = snackView.findViewById<TextView>(R.id.tv_content)
        snackBarText.text = message
        snackBarText.setTypeface(null, Typeface.BOLD)

        val snackBarAction = snackView.findViewById<Button>(R.id.btn_action)
        snackBarAction.text = action

        try {
            data?.optString("bgColor")?.takeIf { it.isNotBlank() }
                    ?.let { snackView.setBackgroundColor(Color.parseColor(it)) }
            data?.optString("textColor")?.takeIf { it.isNotBlank() }
                    ?.let { snackBarText.setTextColor(Color.parseColor(it)) }
            data?.optString("actionColor")?.takeIf { it.isNotBlank() }
                    ?.let { snackBarAction.setTextColor(Color.parseColor(it)) }
        } catch (ignored: Exception) {
        }

        var actionClicked = false
        snackBarAction.setOnClickListener {
            actionClicked = true
            callback?.onActionClick()
        }

        snackBar.addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
            override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                super.onDismissed(transientBottomBar, event)
                if (!actionClicked) {
                    callback?.onDismiss()
                }
            }
        })

        layout.addView(snackView, 0)
        snackBar.show()
    }
}