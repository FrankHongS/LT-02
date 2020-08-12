package com.hon.librarytest02.glide

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hon.librarytest02.R

/**
 * Created by Frank_Hon on 8/11/2020.
 * E-mail: v-shhong@microsoft.com
 */
class GlideAdapter(private val imageList: List<String>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_glide, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val imageViewHolder = holder as ImageViewHolder
        imageViewHolder.bindView(imageList[position])
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val imageView = itemView.findViewById<ImageView>(R.id.iv_glide)

        fun bindView(imageUrl: String) {
            Glide.with(imageView.context)
                    .load(imageUrl)
                    .into(imageView)
        }
    }
}