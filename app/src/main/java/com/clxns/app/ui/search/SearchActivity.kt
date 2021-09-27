package com.clxns.app.ui.search

import android.os.Bundle
import android.widget.SearchView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.clxns.app.data.api.helper.NetworkResult
import com.clxns.app.data.model.cases.CasesData
import com.clxns.app.data.preference.SessionManager
import com.clxns.app.databinding.ActivitySearchBinding
import com.clxns.app.ui.main.cases.CasesAdapter
import com.clxns.app.ui.main.cases.CasesViewModel
import com.clxns.app.utils.Constants
import com.clxns.app.utils.hideKeyboard
import com.clxns.app.utils.snackBar
import com.clxns.app.utils.toast
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private lateinit var searchView: SearchView

    private val casesViewModel: CasesViewModel by viewModels()
    private var casesDataList: MutableList<CasesData> = mutableListOf()

    private lateinit var casesAdapter: CasesAdapter

    @Inject
    lateinit var sessionManager: SessionManager

    private lateinit var token: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initViews()

        setListeners()

        token = sessionManager.getString(Constants.TOKEN).toString()

        subscribeObserver()
    }

    private fun subscribeObserver() {
        casesViewModel.responseCaseList.observe(this) {
            when (it) {
                is NetworkResult.Success -> {
                    if (it.data?.error == false && it.data.casesDataList.isNotEmpty()) {
                        casesDataList.addAll(it.data.casesDataList)
                        casesAdapter.notifyItemRangeChanged(0, casesDataList.size)
                    } else {
                        binding.root.snackBar("Nothing found")
                    }
                }
                is NetworkResult.Error -> {
                    toast(it.message!!)
                }
                is NetworkResult.Loading -> {
                    Timber.i("Loading...")
                }
            }
        }
    }

    private fun initViews() {
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        searchView = binding.searchView

        casesAdapter = CasesAdapter(this, casesDataList) {
            toast(it.name)
        }

        binding.recyclerToolbarSearch.layoutManager = LinearLayoutManager(this)
    }

    private fun setListeners() {
        binding.searchBackBtn.setOnClickListener { onBackPressed() }

        binding.recyclerToolbarSearch.adapter = casesAdapter


        binding.recyclerToolbarSearch.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (searchView.hasFocus() && dy > 0) {
                    searchView.clearFocus()
                    this@SearchActivity.hideKeyboard(binding.root)
                }
            }
        })
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                this@SearchActivity.hideKeyboard(binding.root)
                if (searchView.hasFocus()) {
                    searchView.clearFocus()
                }
                toast("Searching..")
                casesDataList.clear()
                if (query != null) {
                    casesViewModel.getCasesList(token, query, "", "", "", "")
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    // Search
                    casesDataList.clear()
                    casesAdapter.notifyItemRangeChanged(0, casesDataList.size)
                }
                return false
            }

        })
    }

    override fun onStart() {
        super.onStart()
        binding.searchView.requestFocus()
    }
}