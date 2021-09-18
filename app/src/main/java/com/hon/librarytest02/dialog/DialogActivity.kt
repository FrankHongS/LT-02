package com.hon.librarytest02.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hon.librarytest02.R
import kotlinx.android.synthetic.main.activity_dialog.*

/**
 * Created by shuaihua on 2021/6/24 11:16 AM
 * Email: shuaihua@staff.sina.com.cn
 */
class DialogActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dialog)

        btn_show_dialog.setOnClickListener {
            CommonDialog.show(fragmentManager, "Test_Dialog",
                    CommonDialogParams.Builder()
                            .setCreateViewDelegate { root, dialogFragment ->
                                root.findViewById<RecyclerView>(R.id.rv_area_dialog_areas).let {
                                    it.layoutManager = GridLayoutManager(this, 3)
                                    it.adapter = AreaAdapter(listOf("头条"))
                                }
                            }
                            .setLayoutId(R.layout.dialog_following_area)
                            .setThemeId(R.style.Theme_Area_Dialog)
                            .build())
        }
    }

    class AreaAdapter(val areaNames: List<String>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return object : RecyclerView.ViewHolder(
                    LayoutInflater.from(parent.context).inflate(R.layout.item_area_dialog, parent, false)
            ) {}
        }

        override fun getItemCount(): Int {
            return 18
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            holder.itemView.findViewById<TextView>(R.id.tv_area_name).text = areaNames[0]
        }

    }
}