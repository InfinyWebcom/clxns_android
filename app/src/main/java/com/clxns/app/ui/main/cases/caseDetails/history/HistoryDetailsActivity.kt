package com.clxns.app.ui.main.cases.caseDetails.history

import android.os.Bundle
import android.widget.RelativeLayout
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.clxns.app.R
import com.clxns.app.data.api.helper.NetworkResult
import com.clxns.app.data.model.HistoryData
import com.clxns.app.data.preference.SessionManager
import com.clxns.app.databinding.ActivityHistoryDetailsBinding
import com.clxns.app.utils.*
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HistoryDetailsActivity : AppCompatActivity() {

    lateinit var binding : ActivityHistoryDetailsBinding
    private lateinit var historyDetailsAdapter : HistoryDetailsAdapter
    val viewModel : HistoryViewModel by viewModels()

    @Inject
    lateinit var sessionManager : SessionManager

    private lateinit var noDataLayout : RelativeLayout
    private lateinit var historyRV : RecyclerView
    private lateinit var loanAccountNo : String
    private var historyDataList : ArrayList<HistoryData> = arrayListOf()
    private var totalAmount : Int = 0
    private var collectAmount : Int = 0

    private var dueAmount : String = ""

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)

        initView()
        subscribeObserver()
        setListener()
        getHistoryDetails()
    }

    private fun setListener() {
        binding.imgBack.setOnClickListener { finish() }
        binding.historyNoData.retryBtn.setOnClickListener {
            getHistoryDetails()
        }
        binding.dueAmountCard.setOnClickListener {
            it.showDialog(totalAmount, collectAmount)
        }
    }

    private fun getHistoryDetails() {
        viewModel.getCaseHistory(
            sessionManager.getString(Constants.TOKEN)!!,
            loanAccountNo
        )
    }

    private fun initView() {

        binding = ActivityHistoryDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        historyRV = binding.rvDetailsTrack
        loanAccountNo = intent.getStringExtra("loan_account_number").toString()
        totalAmount = intent.getIntExtra("total_due_amount", 0)
        collectAmount = intent.getIntExtra("collected_amount", 0)
        dueAmount = (totalAmount - collectAmount).convertToCurrency()
        binding.dueAmountTv.append(dueAmount)

        if (intent.getStringExtra("name") != null) {
            binding.historyToolbarTitle.text = intent.getStringExtra("name")
        } else {
            binding.historyToolbarTitle.text = getString(R.string.history)
        }
        noDataLayout = binding.historyNoData.root

        //Setting up adapter
        historyDetailsAdapter = HistoryDetailsAdapter(this, historyDataList)
        historyRV.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = historyDetailsAdapter
        }
    }

    private fun subscribeObserver() {
        viewModel.response.observe(this) {
            binding.historyNoData.noDataTxt.text = getString(R.string.something_went_wrong)
            when (it) {
                is NetworkResult.Success -> {
                    binding.progressBar.hide()
                    noDataLayout.hide()
                    historyRV.show()
                    if (!it.data?.error!!) {
                        if (!it.data.historyData.isNullOrEmpty()) {
                            clearAndNotify()
                            val list = it.data.historyData
                            historyDataList.addAll(list)
                            historyDetailsAdapter.notifyItemRangeChanged(0, list.size)
                        } else {
                            binding.historyNoData.retryBtn.hide()
                            binding.historyNoData.noDataTxt.text = getString(R.string.no_data)
                            noDataLayout.show()
                        }
                    } else {
                        binding.root.snackBar(it.data.title)
                        noDataLayout.show()
                        historyRV.hide()
                    }
                    // bind data to the view
                }
                is NetworkResult.Error -> {
                    historyRV.hide()
                    binding.progressBar.hide()
                    noDataLayout.show()
                    binding.root.snackBar(it.message!!)
                    // show error message
                }
                is NetworkResult.Loading -> {
                    binding.progressBar.show()
                    noDataLayout.hide()
                    // show a progress bar
                }
            }
        }
    }

    private fun clearAndNotify() {
        historyDataList.clear()
        historyDetailsAdapter.notifyItemRangeChanged(0, historyDataList.size)
    }

}