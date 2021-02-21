package com.hon.librarytest02.snackbar

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.hon.librarytest02.R
import com.hon.mylogger.MyLogger
import kotlinx.android.synthetic.main.activity_snackbar.*
import org.json.JSONObject

/**
 * Created by Frank Hon on 2020/12/16 8:33 PM.
 * E-mail: frank_hon@foxmail.com
 */
class SnackBarActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_snackbar)
        btn_normal_snackbar.setOnClickListener {
            showNormalSnackBar()
        }
        btn_custom_snackbar.setOnClickListener {
            showCustomSnackBar()
        }
//        snackbar.animateContentIn(1500)
    }

    private fun showNormalSnackBar() {
        Snackbar.make(btn_normal_snackbar, "Normal", Snackbar.LENGTH_SHORT)
                .setAction("Action") {

                }
                .show()
    }

    private fun showCustomSnackBar() {
//        val snackBar = Snackbar.make(btn_normal_snackbar, "Custom", Snackbar.LENGTH_SHORT)
//                .setAction("Action") {
//
//                }
//        val snackBarView = snackBar.view
//        val layoutParams = snackBarView.layoutParams as FrameLayout.LayoutParams
//        layoutParams.gravity = Gravity.BOTTOM
//        Log.d("frankhon", "showCustomSnackBar: ${layoutParams.gravity}")
//
//        snackBarView.layoutParams = layoutParams
//        snackBar.show()

//        val snackBar = Snackbar.make(btn_custom_snackbar, "", Snackbar.LENGTH_SHORT)
//        val layout = snackBar.view as Snackbar.SnackbarLayout
//        val content = layout.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
//        content.visibility = View.INVISIBLE
//
//        val snackView = LayoutInflater.from(this).inflate(R.layout.layout_snackbar, null)
//        snackView.findViewById<TextView>(R.id.tv_content).text = "Hello world !"
//        snackView.findViewById<Button>(R.id.btn_action).text = "Action"
//        layout.addView(snackView, 0)
//
//        val layoutParams = layout.layoutParams as FrameLayout.LayoutParams
//        layoutParams.marginStart = Util.dp2px(64)
//        layoutParams.marginEnd = Util.dp2px(64)
//        layout.layoutParams = layoutParams
//
//        snackBar.show()
        SnackBarUtils.show(btn_custom_snackbar,
                JSONObject().put("message", "Hello world !")
                        .put("action", "Action")
                        .put("position", "center")
                        .put("textColor", "black")
                        .put("bgColor","gray")
                ,
                object : SnackBarUtils.Callback {
                    override fun onActionClick() {
                        MyLogger.d("onActionClick")
                    }

                    override fun onDismiss() {
                        MyLogger.d("onDismiss")
                    }
                })
    }

}