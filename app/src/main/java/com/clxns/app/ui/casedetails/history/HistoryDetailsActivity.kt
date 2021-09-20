package com.clxns.app.ui.casedetails.history

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.clxns.app.R
import com.clxns.app.data.api.helper.NetworkResult
import com.clxns.app.data.preference.SessionManager
import com.clxns.app.databinding.ActivityHistoryDetailsBinding
import com.clxns.app.utils.Constants
import com.clxns.app.utils.hide
import com.clxns.app.utils.show
import com.clxns.app.utils.toast
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HistoryDetailsActivity : AppCompatActivity() {

    lateinit var binding: ActivityHistoryDetailsBinding
    lateinit var historyDetailsAdapter: HistoryDetailsAdapter
    val viewModel: HistoryViewModel by viewModels()

    @Inject
    lateinit var sessionManager: SessionManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)
        binding = ActivityHistoryDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setObserver()
        setListeners()
        setAdapter()

    }

    private fun setAdapter() {

        historyDetailsAdapter = HistoryDetailsAdapter(this)
        val ll = LinearLayoutManager(this)
        ll.isAutoMeasureEnabled = false
        binding.rvDetailsTrack.apply {
            layoutManager = ll
            adapter = historyDetailsAdapter
        }

    }

    private fun setListeners() {

        binding.imgBack.setOnClickListener { finish() }

    }

    private fun setObserver() {
        viewModel.getCaseHistory(
            sessionManager.getString(Constants.TOKEN)!!,
            intent.getStringExtra("loan_account_number")!!
        )
        viewModel.response.observe(this) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    binding.progressBar.hide()
                    if (!response.data?.error!!) {
//                        setData(response.data.data!!)

                    } else {
                        toast(response.data.title!!)
                        finish()
                    }
                    // bind data to the view
                }
                is NetworkResult.Error -> {
                    binding.progressBar.hide()
                    toast(response.message!!)
                    finish()
                    // show error message
                }
                is NetworkResult.Loading -> {
                    binding.progressBar.show()
                    // show a progress bar
                }
            }
        }
    }


}