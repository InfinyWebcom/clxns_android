package com.clxns.app.ui.search

import android.os.Bundle
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.clxns.app.databinding.ActivitySearchBinding
import com.clxns.app.utils.hideKeyboard

class SearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchBinding
    private lateinit var searchView : SearchView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViews()


        setListeners()
    }

    private fun initViews() {
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        searchView = binding.searchView
    }

    private fun setListeners() {
        binding.searchBackBtn.setOnClickListener { onBackPressed() }

        searchView.setOnSearchClickListener {
            this.hideKeyboard(binding.root)
            if (searchView.hasFocus()){
                searchView.clearFocus()
            }
        }

        binding.recyclerToolbarSearch.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (searchView.hasFocus() && dy > 0){
                    searchView.clearFocus()
                    this@SearchActivity.hideKeyboard(binding.root)
                }
            }
        })
    }

    override fun onStart() {
        super.onStart()
        binding.searchView.requestFocus()
    }
}