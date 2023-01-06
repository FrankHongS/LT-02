package com.hon.librarytest02.customview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.hon.librarytest02.R

/**
 * Created by shuaihua_a on 2023/1/6 15:08.
 * E-mail: hongshuaihua
 */
class StaggeredGridFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_staggered_grid, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView = view.findViewById(R.id.rv_artists)

        recyclerView.run {
            layoutManager = StaggeredGridLayoutManager(2, RecyclerView.VERTICAL)
            adapter = ArtistsAdapter()
        }
    }

    private class ArtistsAdapter : Adapter<ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return object : ViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_fantasy_artists, parent, false)
            ) {}
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val cover = holder.itemView.findViewById<ImageView>(R.id.iv_album_cover)
            if (position % 3 == 0) {
                cover.layoutParams = (cover.layoutParams as ConstraintLayout.LayoutParams).apply {
                    dimensionRatio = "1:1.3"
                }
                cover.setImageResource(R.mipmap.test03)
            }
        }

        override fun getItemCount(): Int {
            return 8
        }
    }

}