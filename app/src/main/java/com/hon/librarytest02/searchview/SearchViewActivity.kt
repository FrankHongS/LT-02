package com.hon.librarytest02.searchview

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.frankhon.simplesearchview.generator.DefaultSearchSuggestionGenerator
import com.hon.librarytest02.R
import kotlinx.android.synthetic.main.activity_search_view.*

/**
 * Created by Frank Hon on 2020-06-07 11:09.
 * E-mail: frank_hon@foxmail.com
 */
class SearchViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_view)

        sv_search.setSuggestionGenerator(
                DefaultSearchSuggestionGenerator(this)
        )
        sv_search.setOnSearchListener {
            Toast.makeText(this,"query: $it",Toast.LENGTH_SHORT).show()
        }
        sv_search.setOnBackClickListener {
            Toast.makeText(this,"on back click",Toast.LENGTH_SHORT).show()
        }
    }

}