package com.clxns.app.ui.search

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.RelativeLayout
import android.widget.SearchView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.clxns.app.R
import com.clxns.app.data.api.helper.NetworkResult
import com.clxns.app.data.model.cases.CasesData
import com.clxns.app.data.preference.SessionManager
import com.clxns.app.databinding.ActivitySearchBinding
import com.clxns.app.ui.main.cases.CasesAdapter
import com.clxns.app.ui.main.cases.CasesViewModel
import com.clxns.app.ui.main.cases.casedetails.DetailsActivity
import com.clxns.app.utils.*
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class SearchActivity : AppCompatActivity(), CasesAdapter.OnCaseItemClickListener {

    private lateinit var binding: ActivitySearchBinding
    private lateinit var searchView: SearchView

    private val casesViewModel: CasesViewModel by viewModels()
    private var casesDataList: MutableList<CasesData> = mutableListOf()

    private lateinit var casesAdapter: CasesAdapter

    @Inject
    lateinit var sessionManager: SessionManager

    private lateinit var token: String

    private var searchTxt: String = ""

    private lateinit var searchRV: RecyclerView
    private lateinit var noDataLayout: RelativeLayout

    private lateinit var planStatusIntent: Intent


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initViews()

        setListeners()

        subscribeObserver()
    }

    private fun subscribeObserver() {
        casesViewModel.responseCaseList.observe(this) {
            when (it) {
                is NetworkResult.Success -> {
                    searchRV.show()
                    noDataLayout.hide()
                    if (it.data?.error == false && it.data.casesDataList.isNotEmpty()) {
                        clearAndNotifyAdapter()
                        val dataList = it.data.casesDataList
                        casesDataList.addAll(dataList)
                        casesAdapter.notifyItemRangeChanged(0, dataList.size)
                    } else {
                        binding.searchNoData.noDataTv.text = getString(R.string.no_data)
                        binding.searchNoData.retryBtn.hide()
                        noDataLayout.show()
                        val size = casesDataList.size
                        casesDataList.clear()
                        casesAdapter.notifyItemRangeRemoved(0, size)
                    }
                }
                is NetworkResult.Error -> {
                    noDataLayout.show()
                    searchRV.hide()
                    binding.searchNoData.noDataTv.text = getString(R.string.something_went_wrong)
                    binding.searchNoData.retryBtn.show()
                    binding.root.snackBar(it.message!!)
                }
                is NetworkResult.Loading -> {
                    binding.root.snackBar("Searching...")
                }
            }
        }

        casesViewModel.responseAddToPlan.observe(this) {
            when (it) {
                is NetworkResult.Success -> {
                    binding.root.snackBar(it.data?.title!!)
                    setPlanStatus()
                    casesViewModel.getCasesList(
                        token,
                        searchTxt,
                        "",
                        "",
                        "",
                        "",
                        "",
                        ""
                    )
                }

                is NetworkResult.Error -> binding.root.snackBar(it.message!!)

                is NetworkResult.Loading -> binding.root.snackBar("Adding to my plan...")
            }
        }

        casesViewModel.responseRemovePlan.observe(this) {
            when (it) {
                is NetworkResult.Success -> {
                    binding.root.snackBar(it.data?.title!!)
                    if (!it.data.error) {
                        setPlanStatus()
                        casesViewModel.getCasesList(
                            token,
                            searchTxt,
                            "",
                            "",
                            "",
                            "",
                            "",
                            ""
                        )
                    }
                }
                is NetworkResult.Error -> {
                    binding.root.snackBar(it.message!!)
                    // show error message
                }
                is NetworkResult.Loading -> {
                    binding.root.snackBar("Removing plan...")
                    // show a progress bar
                }
            }
        }
    }

    private fun initViews() {
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        token = sessionManager.getString(Constants.TOKEN).toString()

        searchView = binding.searchView
        searchRV = binding.searchRecyclerView
        noDataLayout = binding.searchNoData.root

        casesAdapter = CasesAdapter(this, casesDataList, this)
        searchRV.layoutManager = LinearLayoutManager(this)
        searchRV.adapter = casesAdapter
    }

    private fun setListeners() {
        binding.searchBackBtn.setOnClickListener { onBackPressed() }

        searchRV.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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
                if (query != null) {
                    casesViewModel.getCasesList(
                        token, query, "", "",
                        "", "",
                        "",
                        ""
                    )
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    // Search
                    val size = casesDataList.size
                    casesDataList.clear()
                    casesAdapter.notifyItemRangeRemoved(0, size)
                } else {
                    searchTxt = newText
                }
                return false
            }

        })
    }

    private fun clearAndNotifyAdapter() {
        val size = casesDataList.size
        casesDataList.clear()
        casesAdapter.notifyItemRangeChanged(0, size)
    }


    private fun showPlanDialog(casesData: CasesData) {
        val cal = Calendar.getInstance()
        val dateSetListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                casesViewModel.addToPlan(
                    token,
                    casesData.loanAccountNo.toString(),
                    "${year}-${monthOfYear + 1}-${dayOfMonth}"
                )
            }
        val datePicker = DatePickerDialog(
            this,
            dateSetListener,
            // set DatePickerDialog to point to today's date when it loads up
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.datePicker.minDate = cal.timeInMillis
        datePicker.show()

    }

    private fun showConfirmUnPlanDialog(casesData: CasesData) {
        val logoutDialog = AlertDialog.Builder(this)
        logoutDialog.setTitle("UnPlan -> ${casesData.name}")
        logoutDialog.setMessage("Are you sure you want to un-plan this case?")

        logoutDialog.setPositiveButton("Yes") { dialog, _ ->
            casesViewModel.removePlan(token, casesData.loanAccountNo.toString())
            dialog.dismiss()
        }.setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }

        val d = logoutDialog.create()
        d.show()
        d.getButton(AlertDialog.BUTTON_POSITIVE).isAllCaps = false
        d.getButton(AlertDialog.BUTTON_NEGATIVE).isAllCaps = false
    }

    private fun setPlanStatus() {
        planStatusIntent = Intent()
        planStatusIntent.putExtra("hasChangedPlanStatus", true)
        setResult(Activity.RESULT_OK, planStatusIntent)
    }


    override fun onStart() {
        super.onStart()
        binding.searchView.requestFocus()
    }

    override fun onPlanClick(isPlanned: Boolean, casesData: CasesData) {
        if (isPlanned) {
            showConfirmUnPlanDialog(casesData)
        } else {
            showPlanDialog(casesData)
        }
    }

    override fun openDetailActivity(
        loadId: String,
        name: String,
        dispositions: String,
        isPlanned: Boolean
    ) {
        val intent = Intent(this, DetailsActivity::class.java)
        intent.putExtra("loan_account_number", loadId)
        intent.putExtra("status", dispositions)
        intent.putExtra("name", name)
        intent.putExtra("isPlanned", isPlanned)
        intent.putExtra("isCaseDetail", true)
        startDetailActivityForResult.launch(intent)
    }

    private val startDetailActivityForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val data: Intent? = it.data
                val status = data?.getBooleanExtra("hasChangedPlanStatus", false)
                if (status == true) {
                    casesViewModel.getCasesList(
                        token, searchTxt, "",
                        "", "", "",
                        "",
                        ""
                    )
                }
            }
        }

}