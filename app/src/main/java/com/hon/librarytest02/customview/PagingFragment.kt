package com.hon.librarytest02.customview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hon.librarytest02.R
import com.hon.librarytest02.customview.view.paging.MyPagingAdapter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Created by Frank Hon on 2022/11/19 1:16 下午.
 * E-mail: frank_hon@foxmail.com
 */
class PagingFragment : Fragment() {

    private var count = 0

    private val pagingAdapter by lazy {
        MyPagingAdapter().apply {
            setOnLoadListener {
                lifecycleScope.launch {
                    delay(3_000)
                    if (count == 3) {
                        addData(null)
                    } else if (count == 1) {
                        markErrorState()
                        count++
                    } else {
                        addData(mutableListOf<String>().apply {
                            for (i in 11..20) {
                                add("Item $i")
                            }
                        })
                        count++
                    }
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_paging, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.rv_paging)
        recyclerView.run {
            adapter = pagingAdapter
            layoutManager = LinearLayoutManager(context)
        }
        pagingAdapter.setData(mutableListOf<String>().apply {
            for (i in 1 until 11) {
                add("Item $i")
            }
        })
    }

}