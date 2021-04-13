package com.hon.librarytest02.glide

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hon.librarytest02.R

/**
 * Created by Frank_Hon on 8/11/2020.
 * E-mail: v-shhong@microsoft.com
 */
class GlideAdapter(private val imageList: List<String>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        Log.d("frankhon", "onCreateViewHolder: viewType = $viewType")
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_glide, parent, false)
        return when (viewType) {
            0 -> ImageViewHolder(view)
            1 -> OddImageViewHolder(view)
            else -> ImageViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        Log.d("frankhon", "onBindViewHolder: position = $position")
        if (holder is ImageViewHolder) {
            holder.bindView(position, imageList[position])
        } else if (holder is OddImageViewHolder) {
            holder.bindView(position, imageList[position])
        }
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (position % 2 == 0) {
            0
        } else {
            0
        }
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        Log.d("frankhon", "onViewRecycled: ")
    }

    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val imageView = itemView.findViewById<ImageView>(R.id.iv_glide)
        private val indexText = itemView.findViewById<TextView>(R.id.tv_index)

        fun bindView(position: Int, imageUrl: String) {

            indexText.text = "${position / 2 + 1}."

            Glide.with(imageView.context)
                    .load(imageUrl)
                    .into(imageView)

            imageView.setOnClickListener {
                itemView.context.apply {
                    startActivity(Intent(this, GlideActivity::class.java))
                }
            }
        }
    }

    class OddImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val imageView = itemView.findViewById<ImageView>(R.id.iv_glide)
        private val indexText = itemView.findViewById<TextView>(R.id.tv_index)

        fun bindView(position: Int, imageUrl: String) {

            indexText.text = "${position / 2 + 1}."
            indexText.setTextColor(itemView.resources.getColor(R.color.colorAccent))

            Glide.with(imageView.context)
                    .load(imageUrl)
                    .into(imageView)

            imageView.setOnClickListener {
                itemView.context.apply {
                    startActivity(Intent(this, GlideActivity::class.java))
                }
            }
        }
    }
}