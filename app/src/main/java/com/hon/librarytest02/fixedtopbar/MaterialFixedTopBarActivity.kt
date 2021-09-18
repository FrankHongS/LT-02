package com.hon.librarytest02.fixedtopbar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hon.librarytest02.R
import kotlinx.android.synthetic.main.activity_material_fixed_bar.*

/**
 * Created by shuaihua on 2021/5/10 4:03 PM
 * Email: shuaihua@staff.sina.com.cn
 */
class MaterialFixedTopBarActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_material_fixed_bar)

        rv_items.let {
            it.adapter = SimpleAdapter()
            it.layoutManager = LinearLayoutManager(this)
            it.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        }
    }

    class SimpleAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return object : RecyclerView.ViewHolder(
                    LayoutInflater.from(parent.context).inflate(R.layout.item_simple_text, parent, false)
            ) {}
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            holder.itemView.findViewById<TextView>(R.id.tv_title).text = "${position + 1}, hello world !"
        }

        override fun getItemCount(): Int {
            return 3
        }

    }
}